#!/bin/sh
psql="psql -U nupama nupama -h localhost"
schema_version=$($psql -q -t -A -c "SELECT value FROM config WHERE key='schema_version'")
schema_version=$schema_version
orig_schema_version=$schema_version
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

if [ "$schema_version" = 2 ]
then
	:
	$psql -q -t -A <<EOF
	ALTER TABLE host ADD COLUMN needsrefresh BOOLEAN;
	UPDATE host SET needsrefresh=false;
	UPDATE config SET value=3 WHERE key='schema_version';
EOF
	schema_version=3
fi

if [ "$schema_version" = 3 ]
then
	:
	$psql -q -t -A <<EOF
	ALTER TABLE host ADD COLUMN action_args VARCHAR(128);
	UPDATE config SET value=4 WHERE key='schema_version';
EOF
	schema_version=4
fi

if [ "$schema_version" = 4 ]
then
	:
	$psql -q -t -A <<EOF
	ALTER TABLE upstream_repo ALTER COLUMN url TYPE VARCHAR(500);
	UPDATE config SET value=5 WHERE key='schema_version';
EOF
	schema_version=5
fi

if [ "$schema_version" = 5 ]
then
	:
	$psql -q -t -A <<EOF
	CREATE TABLE role (id SERIAL PRIMARY KEY,
        name VARCHAR(80) NOT NULL,
        description VARCHAR(130),
        profile INTEGER REFERENCES profile(id),
        host INTEGER REFERENCES host(id)
	);
	UPDATE config SET value=6 WHERE key='schema_version';
EOF
	schema_version=6
fi

if [ "$orig_schema_version" != "$schema_version" ]
then
	echo upgraded schema version to $schema_version.
fi
