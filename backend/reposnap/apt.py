#!/usr/bin/python3
# vim: tabstop=4 expandtab shiftwidth=4 softtabstop=4
import os
import time
import sys
import re
import gzip
import lzma
import http.client

import reposnap.fetch as fetch
import reposnap.getmodified as getmodified
import dpkg_cmp

blacklist='../conf/apt.blacklist'
blacklist_re='../conf/apt.blacklist.re'


def get_unwanted_sections():
    result = []
    with open(blacklist) as f:
        for l in f.readlines():
            ls = l.strip()
            if ls == '' or ls.startswith('#'):
                continue
            result.append(bytes(ls,'utf8'))

    return result


def get_unwanted_regex():
    if os.path.isfile(blacklist_re):
        with open(blacklist_re, 'r') as f:
            re_text = f.read().strip()
        return re.compile(re_text)
    else:
        return None


def process_record(data, results, filter_sections=None, filter_regex=None):
    filename = b'Filename:'
    section = b'Section:'
    arch = b'Architecture:'
    version = b'Version:'
    package = b'Package:'
    description = b'Description:'

    if filename in data:
        # print(data[filename],data[section], data[section] in filter_sections)
        if filter_sections is None or section not in data or data[section] not in filter_sections:
            arc = data[arch].decode('utf8')
            vers = data[version].decode('utf8')
            pack = data[package].decode('utf8')
            fn = data[filename].decode('utf8')
            desc = data[description].decode('utf8')

            if filter_regex is not None and filter_regex.match(pack):
                #print("skipping(re blacklist)", pack)
                return

            if fn.startswith('./'):
                fn = fn[2:]
            results.append((fn, pack, vers, arc, desc))
        else:
            #print("skipping (section blacklist)", data[filename], " ", data[section])
            pass


def filter_packages_apt(path, filter_sections=None, filter_regex=None):
    results = []

    if path.endswith('.xz'):
        open_func = lzma.open
    elif path.endswith('.gz'):
        open_func = gzip.open
    else:
        open_func = open

    if not os.path.isfile(path):
        return results

    with open_func(path, 'rb') as f:
        data = {}
        for l in f.readlines():
            line = l.strip()
            if line == b'':
                process_record(data, results, filter_sections, filter_regex)
                data = {}
            else:
                parts = line.split(b' ',1)
                if len(parts) > 1:
                    data[parts[0]] = parts[1]

    process_record(data, results, filter_sections, filter_regex)
    return results


def fetch_simple_repo(url, localdir, archs=[], progress_updater=None):
    fetch.fetchdir(url, '', localdir, False)

    print("fetching simple repo",url, archs)

    results = filter_packages_apt(os.path.join(localdir, 'Packages'))

    current = 0
    total = len(results)

    for path, package, version, arch, desc in results:
        fetch.fetch(url, path, localdir)
        current += 1
        if (current % 16 == 0):
            progress_updater('{}/{}'.format(current,total))

    return results


def make_pkg_key(package, arch):
    return "{}%{}".format(package, arch)


def get_old_versions_dict(packages_gz, filter_sections, filter_regex):
    print("reading old packages from", packages_gz, end="")
    old_packages = filter_packages_apt(packages_gz, filter_sections, filter_regex)
    print(" ({})".format(len(old_packages)))

    c = dpkg_cmp.VersionComparator()

    result = {}
    for path, package, version, arch, desc in old_packages:
        key = make_pkg_key(package, arch)
        if key in result:
            if c.compare(result[key], version) == 1:
                #print(" skipping version", version)
                continue
        result[key] = version
    
    return result


def fetch_by_release_file(components, url, releasepath, localdir, archs):
    release_file = localdir + "/" + releasepath + "Release"
    byhash = False
    with open(release_file) as f:
        in_md5sum_section = False
        for line in f.readlines():
            if not in_md5sum_section:
                if line == "SHA256:\n" or line == "MD5Sum:\n":
                    in_md5sum_section = True
                elif line == "Acquire-By-Hash: yes\n":
                    byhash = True
            else:
                if line[0] == " ":
                    md5sum, fsize, fname = line.split()
                    print(" ",fname)
                    try:
                        fetch.fetch(url, releasepath + fname, localdir)
                    except http.client.HTTPException:
                        pass
                else:
                    break

    if byhash:
        print("getting by-hash directories:")
        for comp in  components:
            distpath = releasepath + comp
            for arch in archs:
                subpath = distpath + '/binary-' + arch
                byhashdir = subpath + '/by-hash'
                print(" ", byhashdir)
                fetch.fetchdir(url, byhashdir, localdir, True)


def find_packages_file(localdir, subpath):
    p = os.path.join(localdir, subpath, 'Packages.xz')
    if os.path.exists(p):
        return p
    else:
        return os.path.join(localdir, subpath, 'Packages.gz')


def fetch_repo_components(components, url, localdir, dist, archs = None,
    filter_sections = None, filter_regex = None,
    progress_updater = None):
    poolpath = "pool"
    releasepath = "dists/" + dist + "/"
    old_packages = {}
    total_results = []
    c = dpkg_cmp.VersionComparator()

    print("fetching repo",url,"dist", dist, "archs",archs, "components", components)

    for p in [ "ChangeLog", "InRelease",  "Release", "Release.gpg" ]:
        try:
            fetch.fetch(url, releasepath + p, localdir)
        except http.client.HTTPException:
            pass

    if filter_sections is None:
        filter_sections = get_unwanted_sections()

    if filter_regex is None:
        filter_regex = get_unwanted_regex()

    if archs is None:
        archs = [ "all", "amd64" ]

    for comp in components:
        distpath = "dists/" + dist + "/" + comp
        old_packages[comp] = {}
        for arch in archs:
            subpath = distpath + '/binary-' + arch
            packages_gz = find_packages_file(localdir, subpath)
            old_packages[comp].update(get_old_versions_dict(packages_gz, filter_sections, filter_regex))

    fetch_by_release_file(components, url, releasepath, localdir, archs)

    miss_count = 0

    for comp in components:
        for arch in archs:
            distpath = "dists/" + dist + "/" + comp
            subpath = distpath + '/binary-' + arch
            packages_gz = find_packages_file(localdir, subpath)

            print("scanning {} ({})".format(packages_gz, arch), end="")
            results = filter_packages_apt(packages_gz, filter_sections, filter_regex)
            total = len(results)
            current = 0
            print(" ({})".format(total))

            progress_updater('{} {}/{}'.format(comp,0,total))

            for path, package, version, arch, desc in results:
                localfile = os.path.join(localdir, path)
                exists_locally =  os.path.exists(localfile)
                #print("path {} exists: {}".format(localfile, exists_locally))

                key = make_pkg_key(package, arch)
                old_version = None
                # print(" new version:", version)
                if key in old_packages[comp]:
                    old_version = old_packages[comp][key]
                    # print(key, version, old_packages[comp][key])
                else:
                    print("package has no old version", key, comp, arch)
                    miss_count += 1
                    if False and miss_count > 5:
                        for k,v in old_packages[comp].items():
                            print("{}: {}".format(k,v))
                        sys.exit(0)

                if old_version is not None:
                    if c.compare(version, old_packages[comp][key]) <= 0:
                        #print(" skipping older version", version)
                        # if we have an older version but the file
                        # does not exist locally, fetch it
                        if exists_locally:
                            continue

                # print(path, package, version, arch)
                fetch.fetch(url, path, localdir)
                current += 1
                if (current % 16 == 0):
                    progress_updater('{} {}/{}'.format(comp,current,total))

            total_results.extend(results)
            results = None
        print(" ", current,"new packages")
    return total_results


def fetch_repo(url, localdir, dist, components, archs = None,
    filter_sections = None, filter_regex = None,
    progress_updater = None):
    result = []

    if dist is None or dist == '':
        result.extend(fetch_simple_repo(url, localdir, archs, progress_updater))
    else:
        result.extend(fetch_repo_components(components, url, localdir, dist, archs, filter_sections,
            filter_regex,
            progress_updater))

    getmodified.close_session(url)

    return result
