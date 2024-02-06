#!/usr/bin/python3
# vim: tabstop=4 expandtab shiftwidth=4 softtabstop=4
import os
import time
import sys
import traceback
import email.utils
import shutil
import requests
import http.client

cached_server = None
cached_session = None

def http_request(method, url, headers):
    global cached_server, cached_session

    connection = None
    scheme, _, server, path = url.split('/', 3)
    #print("http_request",scheme, method, server, path)

    if cached_session == None:
        cached_session = requests.Session()

    session = cached_session

    body = None

    if method == 'GET':
        success = False
        retries = 3
        resp = None
        timeout = 10
        while not success and retries > 0:
            try:
                resp = session.get(url, headers=headers, timeout=180)
                if resp.status_code == 503:
                    timeout = 180
                    raise Exception("HTTP status 503 - Service Unavailable")
                success = True
            except Exception as e:
                traceback.print_exc(0)
                session.close()
                print("  retrying",url)
                time.sleep(timeout)
                timeout = timeout + timeout
                retries -= 1
                session = requests.Session()
                cached_session = session
        if retries == 0:
            raise Exception("Connection error for " + url)
    else:
        raise Exception("unsupported method")

    return resp


def get_modified(url, oldfilepath):
    modified = False

    headers = {}
    try:
        stats = os.stat(oldfilepath)
        m_stamp = stats.st_mtime
        timestr = time.strftime('%a, %d %b %Y %H:%M:%S GMT', time.gmtime(m_stamp))

        print(" If-Modified-Since: ", timestr, end=" " )

        headers = { "If-Modified-Since": timestr }
    except:
        pass

    resp = http_request('GET', url, headers)
    m_stamp = None

    if resp.status_code >=400:
        raise http.client.HTTPException("fetching {}: status {}".format(url, resp.status_code))

    if resp.status_code != 304:
        print("HTTP status:", resp.status_code)
        if 'Last-Modified' in resp.headers:
            last_modified = resp.headers['Last-Modified']
            m_stamp = email.utils.mktime_tz(email.utils.parsedate_tz(last_modified))
            print("Last-Modified: ", last_modified, end="")

        # remove old file to break hard links
        try:
            os.remove(oldfilepath)
        except:
            pass

        try:
            with open(oldfilepath,"wb") as f:
                for chunk in resp.iter_content(chunk_size=8192):
                    f.write(chunk)
        except Exception as ex:
            traceback.print_exc()
            # if we get an error during download,
            # the file is most likely incomplete,
            # so delete it
            os.remove(oldfilepath)

        modified = True

        if m_stamp is not None:
            os.utime(oldfilepath, (time.time(), m_stamp))
    else:
        print(" (not modified)", end="")
    print()
    return modified


if __name__ == '__main__':
    get_modified(sys.argv[1], sys.argv[2])
