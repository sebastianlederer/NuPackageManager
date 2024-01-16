import os

import reposnap.getmodified as getmodified
import reposnap.filter_href as filter_href


def createdirs(path):
    remaining_path = path

    head, tail = os.path.split(remaining_path)

    if head not in [ "/", "" ]:
            #print("createdirs ",head)
            createdirs(head)
            if not os.path.isdir(head):
                os.mkdir(head)


def fetch(baseurl, path, localdir, recursive=False):
    localpath =  os.path.join(localdir, path)
    # print("fetch",path, localdir, localpath)
    if path == '' or path.endswith('/'):
        if recursive:
            fetchdir(baseurl, path, localdir, True)
        return

    createdirs(localpath)

    sep = '/'
    if path == '' or baseurl.endswith('/'):
        sep = ''
    newurl = baseurl + sep + path

    print(newurl, ' -> ', localpath)

    return getmodified.get_modified(newurl, localpath)


def fetchdir(baseurl, path, localdir, recursive=False):
    localpath =  os.path.join(localdir, path, '.index')
    print("fetchdir", baseurl, path, localdir, localpath)
    createdirs(localpath)
    sep = '/'
    if baseurl.endswith('/'):
        sep = ''
    getmodified.get_modified(baseurl + sep + path, localpath)

    more_files = filter_href.filter_href(localpath)

    print(more_files)

    for f in more_files:
        sep = '/'
        if path=='' or path.endswith('/'):
            sep = ''
        fetch(baseurl, path + sep + f, localdir, recursive)

    # remove old files
    for entry in os.scandir(os.path.join(localdir, path)):
        if entry.name == '.index':
            continue
        if entry.is_file() and entry.name not in more_files:
            print("cleaning up", entry.path)
            os.remove(entry.path)

    return more_files
