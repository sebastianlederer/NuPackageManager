#!/usr/bin/python3
import sys
import time
import psycopg2
import psycopg2.extras
import psycopg2.extensions
import repo_plugin,host_plugin
import config
import traceback

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
    print("update_handler called")
    for h in handler_plugin_list:
        h(dbconn)
        if dbconn.status != psycopg2.extensions.STATUS_READY:
            dbconn.cancel()
            dbconn.reset()


def database_changed(dbconn, cursor,serial,id,handler):
        # print " update"
        handler(dbconn)
        time.sleep(3)


def watch_db(db,id,handler):
        # first, call the handler to check if
        # there are any pending actions
        try:
            handler(db)
        except Exception as ex:
            traceback.print_exc()

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
                        print("serial", ack, "->", serial)
                        database_changed(db,cursor,serial,id,handler)
                run = True
            except psycopg2.errors.InFailedSqlTransaction as ex:
                traceback.print_exc()
                print("\ncancelling failed transaction")
                db.cancel()
                db.reset()
                run = False
            except Exception as ex:
                traceback.print_exc()
                print("\nresuming main loop")
                run = False
        cursor.close()


def main():
    global dbconn
    config.read_config()

    cmd = sys.argv[1]
    reconnect = True
    dbconn = None

    while reconnect:
        if dbconn is None or dbconn.closed:
            dbconn = psycopg2.connect(config.dsn, cursor_factory=psycopg2.extras.NamedTupleCursor)
            time.sleep(5)

        if cmd == "watch":
            print("database connection established", dbconn)
            watch_db(dbconn, 1, update_handler)
        elif cmd == "schedule":
            process_schedule(dbconn, sys.argv[2])
            reconnect = False

    dbconn.close()


if __name__ == "__main__":
    main()
