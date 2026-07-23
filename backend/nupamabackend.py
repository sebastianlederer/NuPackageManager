#!/usr/bin/python3
import sys
import time
import psycopg2
import psycopg2.extras
import psycopg2.extensions
import json
import repo_plugin,host_plugin
import config
import traceback
import logging

logger = logging.getLogger("nupama")

dbconn = None
dburl = None

handler_plugin_list = [ repo_plugin.handler, host_plugin.handler ]
schedule_plugin_list = [ repo_plugin.schedule, host_plugin.schedule ]


def createhost(dbconn, entry):
    with dbconn.cursor() as cursor:
        cursor.execute("""
            INSERT INTO host
            VALUES(DEFAULT, %(name)s, %(description)s, %(owner)s, %(profile)s,
                %(ip_addr)s, %(mac_addr)s, %(options)s, %(upd_count)s, %(upd_count_origin)s,
                %(reboot_required)s, %(action)s, %(result)s)
            """, entry)
        dbconn.commit()


def create_installed_pkg(dbconn, name, arch, vers_local, vers_repo, vers_origin, host):
    with dbconn.cursor() as cursor:
        cursor.execute("""
            INSERT INTO installed_pkg
            VALUES(%s, %s, %s, %s, %s, %s)
            """, (name, arch, vers_local, vers_repo, vers_origin, host))
        dbconn.commit()


def update_installed_pkg(dbconn, name, arch, vers_local, vers_repo, vers_origin, host):
    with dbconn.cursor() as cursor:
        cursor.execute("""
            UPDATE installed_pkg SET (arch,vers_local, vers_repo, vers_origin, host) =
            (%s, %s, %s, %s, %s) WHERE name=%s AND host=%s
            """, (arch, vers_local, vers_repo, vers_origin, host, name, host))
        dbconn.commit()


def process_schedule(dbconn, schedule_name):
    global schedule_plugin_list
    for s in schedule_plugin_list:
        s(dbconn, schedule_name)


def update_handler(dbconn):
    global handler_plugin_list
    logger.debug("update_handler called")
    for h in handler_plugin_list:
        h(dbconn)
        if dbconn.status != psycopg2.extensions.STATUS_READY:
            dbconn.cancel()
            dbconn.reset()


def database_changed(dbconn, cursor,serial,id,handler):
        handler(dbconn)
        time.sleep(3)


def watch_db(db,id,handler):
        # first, call the handler to check if
        # there are any pending actions
        try:
            handler(db)
        except Exception as ex:
            logger.exception("calling handler")

        cursor=db.cursor()
        run = True
        while run:
            try:
                time.sleep(3)
                cursor.execute("SELECT serial,ack FROM notification WHERE id=%d" % (id))
                result=cursor.fetchone()
                serial,ack=result
                cursor.execute("UPDATE notification SET ack=%d WHERE id=%d" % (serial,id))
                db.commit()
                if ack<serial:
                        logger.debug("serial {} -> {}".format(ack, serial))
                        database_changed(db,cursor,serial,id,handler)
                run = True
            except psycopg2.errors.InFailedSqlTransaction as ex:
                logger.exception("cancelling failed transaction")
                db.cancel()
                db.reset()
                run = False
            except Exception as ex:
                logger.exception("exception during select, resuming main loop")
                run = False
        cursor.close()


def list_roles(dbconn, hostname):
    with dbconn.cursor() as cursor:
        cursor.execute("""
            SELECT DISTINCT role.name FROM role
            WHERE role.host = (SELECT id FROM host WHERE name = %s)
            OR role.profile = (SELECT profile FROM host WHERE name= %s)
            ORDER BY name ASC;
        """, (hostname, hostname))
        for r in cursor.fetchall():
            print(r.name)


def guess_type(s):
    result = s

    ls = s.lower()
    if ls in [ "yes", "no", "true", "false" ]:
        return ls in [ "yes", "true" ]

    try:
        return int(ls)
    except:
        pass

    try:
        return float(ls)
    except:
        pass
    return result


def list_inventory(dbconn):
    with dbconn.cursor() as cursor:
        cursor.execute("""
            SELECT name, options FROM host
            ORDER BY name ASC
        """);
        hosts = []
        host_options = {}
        for hostname, options in cursor.fetchall():
            hosts.append(hostname)
            if options is not None:
                options = options.strip()
            else:
                options = ""
            if options != "":
                host_options[hostname] = options

        cursor.execute("""
            SELECT host.name, role.name as role FROM host, role
            WHERE host.id = role.host
            ORDER BY role.name ASC
        """)

        host_roles = {}
        for r in cursor.fetchall():
            hostname, role = r
            if hostname in host_roles:
                host_roles[hostname].append(role)
            else:
                host_roles[hostname] = [ role ]

        inventory = { "nupama": { "hosts": [], "vars": { "nupama_dynamic_inventory": True }},
            "_meta": { "hostvars": {}}}

        for h in hosts:
            inventory["nupama"]["hosts"].append(h)
            if h in host_options:
                opts = {}
                opts_string = host_options[h]
                parts = opts_string.split(",")
                for p in parts:
                    try:
                        key,value = p.split("=")
                        key = key.strip()
                        value = value.strip()
                        opts[key] = guess_type(value)
                    except:
                        pass
                inventory["_meta"]["hostvars"][h] = opts

        print(json.dumps(inventory))


def main():
    global dbconn
    config.read_config()

    cmd = sys.argv[1]
    reconnect = False
    connect_count = 0
    dbconn = None

    logging.basicConfig(level=logging.INFO,
        format='%(asctime)s %(levelname)-8s %(message)s',
        datefmt='%Y-%m-%d %H:%M:%S')

    if cmd == "watch":
            reconnect = True
            logger.info("backend started")

    while reconnect:
        if dbconn is None or dbconn.closed:
            try:
                dbconn = psycopg2.connect(config.dsn, cursor_factory=psycopg2.extras.NamedTupleCursor)
                connect_count += 1
            except psycopg2.OperationalError as e:
                logger.exception("database connection failed")
                time.sleep(5)
                continue

        if cmd == "watch":
            if connect_count > 1:
                logger.info("database connection reestablished")
            else:
                logger.debug("database connection established")
            watch_db(dbconn, 1, update_handler)
        elif cmd == "schedule":
            reconnect = False
            process_schedule(dbconn, sys.argv[2])
        elif cmd == "listroles":
            reconnect = False
            list_roles(dbconn, sys.argv[2])
        elif cmd == "ansible-inventory":
            reconnect = False
            list_inventory(dbconn)
        else:
            reconnect = False
    try:
        dbconn.close()
    except:
        pass

    if cmd == "watch":
        logger.info("backend shutdown")

if __name__ == "__main__":
    main()
