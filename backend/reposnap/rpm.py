#!/usr/bin/python3
# vim: tabstop=4 expandtab shiftwidth=4 softtabstop=4
import os
import sys
import gzip
import xml.etree.ElementTree as ET
import reposnap.fetch
import reposnap.getmodified as getmodified
import reposnap.filter_href
import reposnap.rpm

blacklist='../conf/rpm.blacklist'
blacklist_re='../conf/rpm.blacklist.re'

namespaces = {
    "metadata": "http://linux.duke.edu/metadata/common",
    "rpm":      "http://linux.duke.edu/metadata/rpm"
}


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


def get_rpm_primary(url, repomd_path, localdir, progress_updater):
    localpath = os.path.join(localdir, repomd_path)
    count = 0
    new_count = 0
    if localpath.endswith('.gz'):
        open_func = gzip.open
    else:
        open_func = open

    root = None
    with open_func(localpath, 'rb') as f:
        root = ET.parse(f)

    prefix = '{http://linux.duke.edu/metadata/repo}'
    data_el = root.find(prefix+"data[@type='primary']")
    location_el = data_el.find(prefix + 'location')
    primary_href = location_el.get('href')

    primary_path = os.path.join(localdir, primary_href)
    print("primary_path:", primary_path)
    more_files = filter_packages_rpm(primary_path)

    total = len(more_files)
    for f,name,vers,arch,descr in more_files:
        count += 1
        progress_updater("{}/{}".format(count,total))
        localfile = os.path.join(localdir,f)
        # we assume that if we downloaded a package, it
        # will not change in the future so we
        # do not even check if the file is newer on
        # the upstream server if the file exists locally
        if os.path.isfile(localfile):
            continue
        reposnap.fetch.fetch(url, f, localdir)
        new_count += 1

    print(" {} new packages, {} total".format(new_count, count))

    return more_files


def get_rpm_repo(url, localdir, progress_updater):
    repodata_path = 'repodata'
    repomd_path = 'repodata/repomd.xml'

    reposnap.fetch.fetchdir(url, '', localdir, False)

    reposnap.fetch.fetchdir(url, repodata_path, localdir, True)

    result = get_rpm_primary(url, repomd_path, localdir, progress_updater)

    getmodified.close_session(url)

    # TODO: check signature


def filter_packages_rpm(path, filter_sections=None):
    results = []

    if filter_sections is None:
        filter_sections = get_unwanted_sections()

    if path.endswith(".gz"):
        open_func = gzip.open
    else:
        open_func = open

    root = None

    with open_func(path, "rb") as f:
        root = ET.parse(f)

    for e in root.iterfind("metadata:package", namespaces):
        if e.get("type") != "rpm":
            continue

        name = e.find("metadata:name", namespaces).text

        arch = e.find("metadata:arch", namespaces).text
        if arch == 'src':
            continue

        location = e.find("metadata:location", namespaces)
        href = location.get('href')

        description = e.find("metadata:summary", namespaces).text
        if description is None:
            description = ""

        v = e.find("metadata:version", namespaces)
        epoch = v.get("epoch")
        ver = v.get("ver")
        rel = v.get("rel")
        if epoch != "0":
            version = "{}:{}-{}".format(epoch,ver,rel)
        else:
            version = "{}-{}".format(ver,rel)

        format = e.find("metadata:format", namespaces)
        group = format.find("rpm:group", namespaces)
        if group.text in filter_sections:
            continue

        if href is not None:
            results.append((href, name, version, arch, description))

    return results


if __name__ == '__main__':
    filename = sys.argv[1]
    results = filter_packages_rpm(filename, [ 'other000' ])
    for r in results:
        print(r)
