import os

import reposnap.getmodified as getmodified
import reposnap.filter_href as filter_href

def extend_url(url, ext):
    parts = url.split('?')
    if len(parts) > 1:
        baseurl = parts[0]
        query = '?' + parts[1]
    else:
        baseurl = url
        query = ''

    if (not baseurl.endswith('/')) and (ext != '') and (ext != '/'):
        baseurl += '/'

    if baseurl.endswith('/') and ext.startswith('/'):
        baseurl = baseurl[:len(baseurl)-1]

    return baseurl + ext + query


def make_local_path(localdir, relpath):
    parts = relpath.split('?')
    return os.path.join(localdir, parts[0])


def createdirs(path):
    remaining_path = path

    head, tail = os.path.split(remaining_path)

    if head not in [ "/", "" ]:
            #print("createdirs ",head)
            createdirs(head)
            if not os.path.isdir(head):
                os.mkdir(head)


def fetch(baseurl, path, localdir, recursive=False):
    localpath =  make_local_path(localdir, path)
    # print("fetch",path, localdir, localpath)
    if path == '' or path.endswith('/'):
        if recursive:
            fetchdir(baseurl, path, localdir, True)
        return

    createdirs(localpath)

    newurl = extend_url(baseurl, path)

    print(newurl, ' -> ', localpath)

    return getmodified.get_modified(newurl, localpath)


def fetchdir(baseurl, path, localdir, recursive=False):
    localpath =  os.path.join(make_local_path(localdir, path), '.index')
    print("fetchdir", baseurl, path, localdir, localpath)
    createdirs(localpath)
    url = extend_url(baseurl, path)
    getmodified.get_modified(url, localpath)

    more_files = filter_href.filter_href(localpath)

    print(more_files)

    for f in more_files:
        sep = '/'
        if path=='' or path.endswith('/'):
            sep = ''
        fetch(baseurl, path + sep + f, localdir, recursive)

    # remove old files
    for entry in os.scandir(make_local_path(localdir, path)):
        if entry.name == '.index':
            continue
        if entry.is_file() and entry.name not in more_files:
            print("cleaning up", entry.path)
            os.remove(entry.path)

    return more_files
