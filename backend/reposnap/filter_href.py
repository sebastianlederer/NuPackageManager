#!/usr/bin/python3
# vim: tabstop=4 expandtab shiftwidth=4 softtabstop=4
import os
import time
import sys
import re

def filter_href(filename):
    results = []
    with open(filename) as f:
        ignore_prefixes = [ "/", "http:", "https:", "?", "..", "#" ]
        ignore_suffixes = [ ".mirrorlist" ]
        ignore_infix = ".."
        results = []
        r = re.compile('a href="([^"]+)"')
        for l in f.readlines():
            matches = r.findall(l)
            for m in matches:
                ignore = False

                if ignore_infix in m:
                    ignore = True
                else:
                    for i in ignore_prefixes:
                        if m.startswith(i):
                            ignore = True
                            break
                    for i in ignore_suffixes:
                        if m.endswith(i):
                            ignore = True
                            break
                if not ignore and m not in results:
                    if m.startswith("./"):
                        m = m[2:]
                    results.append(m)
    return results


if __name__ == "__main__":
    results = filter_href(sys.argv[1])

    for r in results:
        print(r)
