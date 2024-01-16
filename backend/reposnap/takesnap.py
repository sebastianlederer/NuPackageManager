#!/usr/bin/python3
import os
import shutil
import sys

# remove directory contents without touching
# the directory itself
def cleardir(d):
    if not os.path.exists(d) or len(d) < 10 or not d.startswith('/'):
        return

    shutil.rmtree(d)


def ununsed():
    for f in os.listdir(d):
        p = os.path.join(d,f)
        print('cleardir',p)
        if os.path.isdir(p):
            shutil.rmtree(p)
        else:
            os.remove(p)


def copydir(src, dest):
    cleardir(dest)
    shutil.copytree(src, dest, copy_function=os.link)


def takesnap(basepath, origin, dest):
    s = os.path.join(basepath,origin)
    d = os.path.join(basepath,dest)

    cleardir(d)

    shutil.copytree(s, d, copy_function=os.link)


if __name__ == '__main__':
    basepath = sys.argv[1]
    origin = sys.argv[2]
    dest = sys.argv[3]
    takesnap(basepath, origin, dest)
