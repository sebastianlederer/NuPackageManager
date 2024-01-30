import subprocess
import time
import string
import psycopg2
import psycopg2.extras
from multiprocessing import Pool
from collections import namedtuple
import config
import dpkg_cmp

HostTuple = namedtuple("HostTuple", "id name profile action index max")

command_prefix = ''


def set_taskstatus(dbconn, host, cur, max, current_action):
    with dbconn.cursor() as cursor:
        cursor.execute("""
            UPDATE taskstatus SET (task,progress,message)
                = (%s,%s,%s)
            """, ('host','{}/{}'.format(cur, max),
                    '{} {}'.format(host.name, current_action)))
        dbconn.commit()


def clear_taskstatus(dbconn):
    with dbconn.cursor() as cursor:
        cursor.execute("""
            UPDATE taskstatus SET (task,progress,message)
                = ('','','')
            """)
        dbconn.commit()


def update_host_status(dbconn, id, result):
    with dbconn.cursor() as cursor:
        cursor.execute("""
            UPDATE host SET (action,result) =
            (NULL, %s) WHERE id=%s
            """, (result, id))
        dbconn.commit()


def crs_get_latest_packages(cursor, host, fromorigin=False):
    cursor.execute("""
        SELECT * FROM package WHERE repo in (
            SELECT repo FROM profile_repo WHERE profile=%s
        )
    """, (host.profile,))

    latest_packages = {}

    c = dpkg_cmp.VersionComparator()

    for r in cursor:
        if r.name in latest_packages:
            other_version = latest_packages[r.name]
            if fromorigin:
                this_version = r.vers_origin
            else:
                this_version = r.version

            if this_version is not None:
                if c.compare(this_version, other_version) == 1:
                    latest_packages[r.name] = this_version
        else:
            latest_packages[r.name] = r.version
    return latest_packages


def crs_update_pkg_versions(cursor, host):
    print("updating pkg versions for host",host.name)

    latest_packages = crs_get_latest_packages(cursor, host)
    latest_packages_origin = crs_get_latest_packages(cursor, host, fromorigin=True)

    cursor.execute("""
        SELECT * FROM installed_pkg WHERE host=%s
    """, (host.id,))

    update_list = []
    update_list_origin = []

    for p in cursor:
        if p.name in latest_packages and \
            (p.vers_repo is None or
             p.vers_repo != latest_packages[p.name]):
                update_list.append((p.name, p.arch))

        if p.name in latest_packages_origin and \
            (p.vers_origin is None or
             p.vers_origin != latest_packages_origin[p.name]):
                update_list_origin.append((p.name, p.arch))

    for name,arch in update_list:
        cursor.execute("""
            UPDATE installed_pkg SET vers_repo=%s
                WHERE host=%s AND name=%s AND arch=%s
        """,(latest_packages[name], host.id, name, arch))

    for name, arch in update_list_origin:
        cursor.execute("""
            UPDATE installed_pkg SET vers_origin=%s
                WHERE host=%s AND name=%s AND arch=%s
        """,(latest_packages_origin[name], host.id, name, arch))


def update_all_pkg_versions(dbconn):
    with dbconn.cursor() as cursor:
        cursor.execute("SELECT * FROM host WHERE needsrefresh=TRUE")
        hosts = cursor.fetchall()

        for h in hosts:
            crs_update_pkg_versions(cursor, h)

        # number of different installed package versions to predecessor per host
        cursor.execute("""
            UPDATE host SET upd_count=(SELECT COUNT(*) FROM installed_pkg AS i
                WHERE host.id=i.host AND vers_local <> vers_repo)
        """)

        # number of different installed package versions to origin per host
        cursor.execute("""
            UPDATE host SET needsrefresh=FALSE, upd_count_origin=(SELECT COUNT(*) FROM installed_pkg AS i
                WHERE host.id=i.host AND vers_local <> vers_origin)
        """)

        dbconn.commit()


def mark_host_for_refresh(dbconn, host):
    with dbconn.cursor() as cursor:
        cursor.execute("UPDATE host SET needsrefresh=TRUE WHERE id=%s", (host.id,))
        dbconn.commit()


def update_host_pkgs(dbconn, host, pkgs):
    with dbconn.cursor() as cursor:
        cursor.execute("DELETE FROM installed_pkg WHERE host=%s", (host.id,))
        for name, version, arch in pkgs:
            cursor.execute("""
                INSERT INTO installed_pkg
                VALUES (%s, %s, %s, NULL, NULL, %s)
            """, (name, arch, version, host.id))

        dbconn.commit()


def command(cmd):
    print("executing command ",cmd)
    subprocess.check_call(cmd, shell=True)


def command_output(cmd, script):
    print("executing command",cmd)
    child = subprocess.Popen(cmd, shell=True, encoding='utf8',
        stdin=subprocess.PIPE, stdout=subprocess.PIPE, stderr=subprocess.STDOUT)
    output, err = child.communicate(input=script, timeout=600)
    child.wait()
    return (child.returncode, output)


def remote_command(host, script):
    cmd = '/usr/bin/ssh -o ConnectTimeout={} -o PasswordAuthentication=no -i {} -T {}@{}'.format(
                config.ssh_timeout, config.ssh_key, config.ssh_user,  host.name)
    return command_output(cmd, script)


def perform_report(dbconn, host):
    script = """
     dpkg-query --show -f '${db:Status-Abbrev} ${Package} ${Version} ${Architecture}\\n' | awk '/^ii / { print   $2, $3, $4 }'
    """
    script = config.get_scriptlet('report')
    exitcode, output = remote_command(host, script)


    if exitcode != 0:
        return "[{}] ".format(exitcode) + output.splitlines()[-1][:78]

    pkgs = []
    for l in output.splitlines():
        try:
            name, version, arch = l.strip().split(" ")
            pkgs.append((name, version, arch))
        except:
            print("malformed line:",l.strip())

    update_host_pkgs(dbconn, host, pkgs)

    mark_host_for_refresh(dbconn, host)

    return "OK"


def perform_upgrade(dbconn, host):
    script = """
        sudo DEBIAN_FRONTEND=noninteractive apt update -y
        sudo DEBIAN_FRONTEND=noninteractive apt upgrade -y -o pkg::Options::="--force-confold"
    """

    script = config.get_scriptlet('upgrade')
    exitcode, output = remote_command(host, script)

    print("perform_upgrade:")
    print(output)

    if exitcode == 0:
        return "OK"
    else:
        return "- " + output.splitlines()[-1][:78]


def perform_reboot(dbconn, host):
    script = """
        (sudo sleep 3; sudo /sbin/reboot) &
    """

    script = config.get_scriptlet('reboot')
    exitcode, output = remote_command(host, script)

    if exitcode == 0:
        return "OK"
    else:
        return "- " + output.splitlines()[-1][:78]


def perform_config(dbconn, host):
    download_url = config.server_url
    repo_path = config.repo_path

    script = """
        sudo sh -c "cat >/etc/apt/sources.list" <<EOF
# automatically generated, do not edit!
"""
    script = config.get_scriptlet("config")

    cursor = dbconn.cursor()
    cursor.execute("""
    SELECT r.id, r.name, u.dist, u.component, u.arch, u.pubkey FROM repository as r, repository as r_o, upstream_repo as u WHERE r.id IN
        (SELECT repo FROM profile_repo WHERE profile=(SELECT profile FROM host WHERE id=%s))
    AND r.origin = r_o.id AND r_o.upstream = u.id
    """, (host.id,))

    pubkeys = []

    cursor2 = dbconn.cursor()
    for repo in cursor.fetchall():
        if repo.arch and repo.arch != '':
            arch_spec = "arch={}".format(repo.arch)
        else:
            arch_spec = ""

        if repo.pubkey and repo.pubkey != "":
            sign_spec = "signed-by=/etc/apt/keyrings/{}.gpg".format(repo.name)
            pubkeys.append((repo.name, repo.pubkey))
        else:
            sign_spec = ""

        if sign_spec != "" or arch_spec != "":
            repo_opts = "[{} {}]".format(arch_spec, sign_spec)
        else:
            repo_opts = ""

        if repo.dist and repo.dist != "":
            dist_spec = repo.dist
        else:
            dist_spec = "/"

        script += "deb {} {}{}{} {} {} # {}\n".format(repo_opts,
                download_url, repo_path, str(repo.id), dist_spec, repo.component, repo.name)

    script += "EOF\n"

    pubkey_template = string.Template(config.get_scriptlet("pubkey"))

    for name, pubkey in pubkeys:
        script += pubkey_template.substitute({"repo":name, "pubkey":pubkey})

    print(script)

    exitcode, output = remote_command(host, script)

    if exitcode == 0:
        return "OK"
    else:
        return "- " + output.splitlines()[-1][:78]


def call_action_hook(host):
    if config.action_hook is not None:
        cmd = '{} {} {}'.format(config.action_hook, host.name, host.action)


def perform_action(dbconn, host):
    call_action_hook(host)

    if 'C' in host.action:
        result = perform_config(dbconn, host)
    if  'U' in host.action:
        result = perform_upgrade(dbconn, host)
    if 'R' in host.action:
        result = perform_report(dbconn, host)
    if 'B' in host.action:
        result = perform_reboot(dbconn, host)

    return result


def describe_action(dbconn, host):
    result = 'executing action'
    if len(host.action) > 1:
        result += 's'

    if 'U' in host.action:
        result += ' upgrade'
    if 'R' in host.action:
        result += ' report'
    if 'B' in host.action:
        result += ' reboot'
    if 'C' in host.action:
        result += ' config'

    return result


def new_dbconn():
    return psycopg2.connect(config.dsn, cursor_factory=psycopg2.extras.NamedTupleCursor)


def forked_action(host):
    print("forked_action",host.name,host.action)
    # we need a new db connection because
    # this is a forked process
    dbconn = new_dbconn()
    action_result = perform_action(dbconn, host)
    update_host_status(dbconn, host.id, action_result)
    set_taskstatus(dbconn, host, host.index, host.max, describe_action(dbconn, host))


def parallel_action(dbconn, hosts):
    if len(hosts) < 1:
        return

    with Pool(processes=config.ssh_processes) as pool:
        pool.map(forked_action, hosts)

    update_all_pkg_versions(dbconn)


global_count = 0
def counter():
    global global_count
    global_count += 1
    return global_count


def reset_counter():
    global global_count
    global_count = 0


def handler(dbconn):
    print("host update handler")
    reset_counter()
    with dbconn.cursor() as cursor:
        cursor.execute("SELECT * FROM host WHERE action IS NOT NULL AND action <>'' ORDER BY name")
        result = list(cursor.fetchall())
        max = len(result)
        if max > 0:
            hosts = [ HostTuple(h.id,h.name,h.profile,h.action,counter(),max) for h in result ]
            firsthost = hosts[0]
            set_taskstatus(dbconn, firsthost, 0, max, describe_action(dbconn, firsthost))
            parallel_action(dbconn, hosts)

        clear_taskstatus(dbconn)


def schedule(dbconn, schedule_name):
    if schedule_name != "report":
        return

    print("host schedule handler")
    with dbconn.cursor() as cursor:
        cursor.execute("UPDATE host SET action='R' WHERE action = '' or action is NULL")
        dbconn.commit()
