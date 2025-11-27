#!/bin/sh
if [ -z "$1" ]
then
	echo No hostname given, exiting.
	exit 1
fi

cd /home/nupama/nupama/backend
python3.11 /home/nupama/nupama/backend/nupamabackend.py listroles "$1"
