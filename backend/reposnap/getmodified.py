#!/usr/bin/python3
# vim: tabstop=4 expandtab shiftwidth=4 softtabstop=4
import os
import time
import sys
import traceback
import logging
import email.utils
import shutil
import requests
from requests.adapters import HTTPAdapter
from requests.packages.urllib3.util.retry import Retry
import http.client

logger = logging.getLogger("nupama")

cached_session = None


def get_session():
    global cached_session

    if cached_session is not None:
        return cached_session

    session = requests.Session()
    retry_strategy = Retry(
        total=4,
        backoff_factor=2,
        status_forcelist=[408, 413, 429, 503]
    )
    adapter = HTTPAdapter(max_retries=retry_strategy)
    session.mount("https://", adapter)
    session.mount("http://", adapter)

    cached_session = session

    return session


def close_session():
    global cached_session
    if cached_session is not None:
        cached_session.close()
        cached_session = None


def http_request(method, url, headers):
    connection = None
    scheme, _, server, path = url.split('/', 3)

    session = get_session()

    body = None

    if method == 'GET':
        resp = None
        try:
            resp = session.get(url, headers=headers, timeout=180, stream=True)
            if resp.status_code == 503:
                timeout = 300
                raise Exception("HTTP status 503 - Service Unavailable")
        except Exception as e:
            logger.exception("GET request")
            close_session()
    else:
        raise Exception("unsupported method")

    return resp


def get_modified(url, oldfilepath):
    modified = False

    headers = None
    try:
        stats = os.stat(oldfilepath)
        m_stamp = stats.st_mtime
        timestr = time.strftime('%a, %d %b %Y %H:%M:%S GMT', time.gmtime(m_stamp))

        logger.debug(" If-Modified-Since: " + timestr)

        headers = { "If-Modified-Since": timestr }
    except:
        pass

    resp = http_request('GET', url, headers)

    if resp is None:
        return False

    m_stamp = None

    if resp.status_code >=400:
        if resp.status_code != 404:
            logger.warning("error response text: " + resp.text)
        raise http.client.HTTPException("status {} fetching {}".format(resp.status_code, url))

    if resp.status_code != 304:
        logger.debug("HTTP status: {}".format(resp.status_code))
        if 'Last-Modified' in resp.headers:
            last_modified = resp.headers['Last-Modified']
            m_stamp = email.utils.mktime_tz(email.utils.parsedate_tz(last_modified))
            logger.debug("Last-Modified: " + last_modified)

        # remove old file to break hard links
        try:
            os.remove(oldfilepath)
        except:
            pass

        try:
            with open(oldfilepath,"wb") as f:
                for chunk in resp.iter_content(chunk_size=65536):
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
        logger.debug(" (not modified)")
    return modified


if __name__ == '__main__':
    get_modified(sys.argv[1], sys.argv[2])
