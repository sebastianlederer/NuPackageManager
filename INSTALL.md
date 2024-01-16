Requirements
============
* OpenJDK 11 or higher
* Python 3.7 or higher
* Ant 1.10 or higher
* Ivy 2.5 or higher
* Psycopg2 2.9 or higher
* PostgresSQL 12 or higher

Download
========
``` sh
git clone ...
cd <dir>
```

Create database
===============
``` sh
su -l postgres
createuser -P nupama
createdb nupama -O nupama
exit
psql -h localhost -U nupama nupama < webapp/WEB-INF/create_tables.sql
```

Build
======
* modify the file 'build.properties', especially the installation path in the first two lines
```
ant prepare-build install
```

Configure
=======
* modify tomcat/conf/server.xml for authentication

Run
======
``` sh
ant tomcat-start
```
