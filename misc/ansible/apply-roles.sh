#!/bin/sh
target="$1"
if [ -z "$target" ]
then
	echo No hostname given, exiting.
	exit 1
fi

cd $HOME/nupama/ansible
if find .timestamp -mmin -1 -print | grep -q .timestamp
then
	: ok
else
	touch .timestamp
	(cd infra; flock nupama_playbook.yml git pull)
fi

cd infra
ansible-playbook -i $HOME/nupama/ansible/dynamic-inventory.sh nupama_playbook.yml \
	--vault-password-file="$HOME/nupama/ansible/.vaultpass" \
	-e "nupama_target=$target"
