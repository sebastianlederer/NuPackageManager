import datetime
import os
import reposnap
import reposnap.apt
import reposnap.rpm
import reposnap.takesnap
import config
import dpkg_cmp

descr_max_length = 130

def get_local_prefix():
    return config.mirror_dir + '/repo'


def get_local_dir(repo ):
    return get_local_prefix() + str(repo.id)


def get_upstream(dbconn, repo):
    upstream = None
    with dbconn.cursor() as cursor:
        cursor.execute("SELECT * FROM upstream_repo WHERE id=%s", (repo.upstream,))
        upstream = cursor.fetchone()
    return upstream


def repocopy(dbconn, repo):
    dst_dir = get_local_dir(repo)
    src_dir = get_local_prefix() + str(repo.pred)
    print("repocopy {} -> {}".format(src_dir,dst_dir))
    reposnap.takesnap.copydir(src_dir, dst_dir)
    copy_repo_packages(dbconn, repo)
    result = ''
    update_repo_status(dbconn, repo.id, result, datetime.datetime.now())


def repodelete(dbconn, repo):
    dst_dir = get_local_dir(repo)
    print("repodelete {}".format(dst_dir))
    reposnap.takesnap.cleardir(dst_dir)
    result = ''
    update_repo_status(dbconn, repo.id, result, datetime.datetime.now())
    with dbconn.cursor() as cursor:
        cursor.execute("DELETE FROM package WHERE repo=%s", (repo.id,))
        cursor.execute("DELETE FROM repository WHERE id=%s", (repo.id,))


def copy_repo_packages(dbconn, repo):
    with dbconn.cursor() as cursor:
        cursor.execute("DELETE FROM package WHERE repo=%s", (repo.id,))

        print("deleted package versions from repo id", repo.id)
        print("copying packages from {} to {}".format(repo.pred, repo.id))

        cursor.execute("""
            INSERT INTO package(name, description, version, vers_origin, vers_pred, arch, repo)
            SELECT name, description, version, vers_origin, vers_pred, arch, %s FROM package
            WHERE repo=%s
        """, (repo.id, repo.pred))

    dbconn.commit()


def crs_update_pkg_versions(cursor, repo):
    print("update_pkg_versions", repo.name, repo.id)

    cursor.execute("SELECT * FROM repository WHERE id=%s", (repo.origin,))
    origin = cursor.fetchone()

    cursor.execute("SELECT * FROM repository WHERE id=%s", (repo.pred,))
    pred = cursor.fetchone()

    if origin is not None:
        cursor.execute("""
            UPDATE package as p1 SET vers_origin=p2.version
                FROM package as p2
                WHERE p1.name=p2.name AND p1.arch=p2.arch
                AND p1.repo=%s AND p2.repo=%s
        """, (repo.id, origin.id))

        cursor.execute("""
            UPDATE repository SET upd_count_origin=(
                SELECT COUNT(*) FROM package
                    WHERE repo=%s AND version <> vers_origin
            ) WHERE id=%s
        """, (repo.id, repo.id))

    if pred is not None:
        cursor.execute("""
            UPDATE package as p1 SET vers_pred=p2.version
                FROM package as p2
                WHERE p1.name=p2.name AND p1.arch=p2.arch
                AND p1.repo=%s AND p2.repo=%s
        """, (repo.id, pred.id))

        cursor.execute("""
            UPDATE repository SET upd_count=(
                SELECT COUNT(*) FROM package
                    WHERE repo=%s AND version <> vers_pred
            ) WHERE id=%s
        """, (repo.id, repo.id))


def update_all_pkg_versions(dbconn):
    with dbconn.cursor() as cursor:
        cursor.execute("SELECT * FROM repository")
        repos = cursor.fetchall()
        for r in repos:
            cursor.execute("LOCK table repository IN ROW EXCLUSIVE MODE")
            crs_update_pkg_versions(cursor, r)
            dbconn.commit()        


def update_pkg_versions_chain(dbconn, origin):
    with dbconn.cursor() as cursor:
        cursor.execute("SELECT * FROM repository WHERE id=%s OR pred=%s OR origin=%s",
            (origin,origin,origin))
        repos = cursor.fetchall()
        for r in repos:
            cursor.execute("LOCK table repository IN ROW EXCLUSIVE MODE")
            crs_update_pkg_versions(cursor, r)
            dbconn.commit()


def update_repo_packages(dbconn, repo, fetched):
    c = dpkg_cmp.VersionComparator()
    packages = {}
    with dbconn.cursor() as cursor:
        cursor.execute("DELETE FROM package WHERE repo=%s", (repo.id,))
        print("deleted package versions from repo id",repo.id, repo.name)

        for path, package, version, arch, description in fetched:
            key = "{}%{}".format(package,arch)
            if key in packages:
                v = packages[key]
                if c.compare(v,version) >= 0:
                    #print(" skip version {} {}".format(version,v))
                    continue
                else:
                    packages[key] = version
            else:
                packages[key] = version

            description = description[:descr_max_length]
            #print("insert into packages",package,version,arch)
            cursor.execute("""
                INSERT INTO package VALUES(DEFAULT, %s, %s, %s, NULL, NULL, %s, %s)
                ON CONFLICT (name, arch, repo) DO UPDATE SET version = EXCLUDED.version
            """, (package, description, version, arch, repo.id))

        print("updated {} package versions from metadata for repo id".format(len(fetched)), repo.id)

        cursor.execute("""UPDATE host SET needsrefresh=TRUE WHERE id IN
                (SELECT id FROM host WHERE profile IN
                 (SELECT profile FROM profile_repo WHERE repo=%s))
                """, (repo.id,))

        dbconn.commit()


def set_taskstatus(dbconn, repo):
    with dbconn.cursor() as cursor:
        cursor.execute("""
            UPDATE taskstatus SET (task,progress,message)
                = (%s,%s,%s)
            """, ('repo','', 'mirroring ' + repo.name))
        dbconn.commit()


def clear_taskstatus(dbconn, repo):
    with dbconn.cursor() as cursor:
        cursor.execute("""
            UPDATE taskstatus SET (task,progress,message)
                = ('','','')
            """)
        dbconn.commit()


def progress_updater(dbconn, p):
    with dbconn.cursor() as cursor:
        cursor.execute("""
            UPDATE taskstatus SET progress=%s WHERE id=1
        """, (p,))

        dbconn.commit()


def mirror(dbconn, repo):
    print("executing mirror action for", repo.name)

    suffix = ".unknown"

    set_taskstatus(dbconn, repo)

    with dbconn.cursor() as cursor:
        cursor.execute("SELECT * FROM upstream_repo WHERE id=%s", (repo.upstream,))
        upstream = cursor.fetchone()
        dbconn.commit()  # close transaction opened by above SELECT

        if upstream is None:
            update_repo_status(dbconn, repo.id, None, repo.last_update)
            return

        print("  upstream is", upstream.name)
        print("  repo type  ", upstream.type)
        print("  arch       ", upstream.arch)

        fetched = []

        if upstream.type == 'apt':
            suffix = ".deb"
            print("  components ", upstream.component)

            localdir = get_local_dir(repo)
            components = upstream.component.split(" ")
            fetched = reposnap.apt.fetch_repo(upstream.url, localdir,
                upstream.dist, components,
                [ "all", upstream.arch ],
                progress_updater = lambda p: progress_updater(dbconn, p) )

        elif upstream.type == 'rpm':
            suffix = ".rpm"
            localdir = get_local_dir(repo)
            fetched = reposnap.rpm.get_rpm_repo(upstream.url, localdir,
                lambda p: progress_updater(dbconn, p))
        else:
            print("  unknown repo type")

        update_repo_packages(dbconn, repo, fetched)

        result = None

        clear_taskstatus(dbconn, repo)
        update_repo_status(dbconn, repo.id, result, datetime.datetime.now())
        cleanup_packages(localdir, fetched, suffix)


def cleanup_packages(localdir, fetched, suffix):
    counter = 0

    files_dict = {}
    for f in fetched:
        if f[0].endswith(suffix):
            files_dict[os.path.basename(f[0])] = True

    for root, dirs, files in os.walk(localdir):
        for f in files:
            if f.endswith(suffix):
                if f not in files_dict:
                    os.remove(os.path.join(root,f))
                    counter += 1
    print("{} old package files removed".format(counter))


def mirror_by_schedule(dbconn, schedule_name):
    print("scheduling mirror action", schedule_name);
    with dbconn.cursor() as cursor:
        cursor.execute("""
            UPDATE repository SET action='M' where schedule=%s
        """, (schedule_name,))
        dbconn.commit()


def update_repo_status(dbconn, id, result, timestamp):
    with dbconn.cursor() as cursor:
        cursor.execute("""
            UPDATE repository SET (approved,action,result,last_update) =
            (false, NULL, %s, %s) WHERE id=%s
            """, (result, timestamp, id))
        dbconn.commit()


def process_repo(dbconn, r):
        if "M" in r.action:
            mirror(dbconn, r)
        if "C" in r.action:
            repocopy(dbconn, r)
        if "D" in r.action:
            repodelete(dbconn, r)


def handler(dbconn):
    print("repository update handler")
    with dbconn.cursor() as cursor:
        cursor.execute("SELECT * FROM repository WHERE action IS NOT NULL and action <> ''")
        for r in cursor:
            print(r.name, r.upstream, r.action)
            process_repo(dbconn, r)
            update_pkg_versions_chain(dbconn, r.id)


def schedule(dbconn, schedule_name):
    mirror_by_schedule(dbconn, schedule_name)


