#!/bin/sh
psql="psql -U nupama nupama -h localhost"
schema_version=$($psql -q -t -A -c "SELECT value FROM config WHERE key='schema_version'")
schema_version=$schema_version
echo "database schema version is $schema_version"

if [ "$schema_version" = 1 ]
then
	:
	$psql -q -t -A <<EOF
	ALTER TABLE upstream_repo ADD COLUMN pubkey VARCHAR(8000);
	ALTER TABLE package ADD COLUMN flags VARCHAR(16);
	ALTER TABLE installed_pkg ADD COLUMN fromrepo INTEGER, ADD COLUMN flags VARCHAR(16);
	UPDATE config SET value=2 WHERE key='schema_version';
EOF
	schema_version=2
fi
