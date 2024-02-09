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
* modify conf/backend.conf

Run
======
Install the files *nupama.service* and *nupama-tomcat.service* from the *misc* directory
into the systemd configuration directory of your choice (e.g. */etc/systemd/system*) and
modify the files according to your installation path. Then execute the following commands as root:
```
systemctl daemon-reload
systemctl enable nupama nupama-tomcat
systemctl start nupama
systemctl start nupama-tomcat
```

For scheduled mirroring and reports, put the file *nupama.cron* from the *misc* directoy into
*/etc/cron.d* and modify it according to your installation path and desired execution times.


Configuration
================
When running the `ant install` target, all configuration files are written with an
extra ".dist" suffix, so an existing configuration will never be overwritten.

When configuring for the first time, you need to copy the *.conf.dist* files to *.conf* files.

Here is a list of files you need to prepare in this way:
* tomcat/conf/server.xml.dist
* tomcat/conf/tomcat-users.xml.dist
* tomcat/nupama-tomcat.properties.dist
* tomcat/webapps/nupama/WEB-INF/tools.xml.dist
* tomcat/webapps/nupama/WEB-INF/web.xml.dist
* tomcat/webapps/nupama/META-INF/context.xml.dist
* conf/apt.blacklist.dist
* conf/apt.blacklist.re.dist
* conf/actions/reboot.dist
* conf/actions/pubkey.dist
* conf/actions/report.dist
* conf/actions/config.dist
* conf/actions/upgrade.dist
* conf/backend.conf.dist

You need to modify at least these files according to your installation:
* conf/backend.conf - set the server url, mirroring directory and SSH identity
* tomcat/nupama-tomcat.properties - set server.location to your install directory
* tomcat/conf/server.xml.dist - set up authentication, or delegate it to reverse proxy
* tomcat/conf/tomcat-users.xml.dist - set up users and groups if you use Tomcat builtin authentication

Backend-Configuration
----------------------
### SSH identity
Nupama will make SSH connections to the managed hosts to gather information,
configure repositories, do updates and reboots. You can configure the user that
Nupama will use for the SSH connection, and the SSH key, with *ssh_user* and
*ssh_key* in *conf/backend.conf*.

### Server URL
The server URL is used by the configuration action to set up repositories on
the managed hosts. So it should be an URL that is reachable from all managed
hosts, and this URL should be mapped by the webserver to the mirror directory
for static file access. It is specified by *server_url* in *conf/backend.conf*.

### Mirror Directory
The mirror directory is used to store all mirrored data (metadata and packages).
The corresponding configuration setting is *mirror_dir* in *conf/backend.conf*.
It should be accessible under the URL specified by *server_url*.

### Database connection
The backend needs to connect to the database. The DSN string is specified
by the *dsn* setting in *conf/backend.conf*, the syntax is a Python DB API
connection string.

### Package Blacklists
When mirroring, packages which have the *Section* attribute set can be filtered.
The package blacklists contain, line by line, section names which are blacklisted,
i.e. ignored when mirroring. The package metadata will still contain these packages,
so a managed host could attempt to install one of the blacklisted packages and
will get an error during download because the file is not there.

For example, you could include the line *games* to filter out all packages in
the *games* section.

The blacklists are separate for APT and RPM repositories, with the configuration
files *conf/apt.blacklist* and *conf/rpm.blacklist* respectively.

### Action Scripts
Action scripts are executed on the managed hosts when an action is requested
via the web interface (e.g. "report" or "upgrade"). The content of the
action script is fed as standard input to the remote shell via SSH. In some cases
("config" and "pubkey"), additional data is appended to the action script file
contents, that is, the generated content for repo keys or repo configuration.

Frontend-Configuration
-------------------------
The web application is a servlet inside an Tomcat embedded application server. Standard
Tomcat and servlet configuration mechanisms apply.
Important configuration files are:
* Tomcat configuration: ports, authentication in *tomcat/conf/server.xml*
* Database connection (e.g. set a different password) in *tomcat/webapps/nupama/META-INF/context.xml*

Authentication
---------------
You can use Tomcat authentication by configuring a Authentication Realm in the *server.xml* file.
LDAP authentication is possible via JNDIRealm, or you can just use the built-in UserDatabaseRealm
which uses the *tomcat-users.xml* file to authenticate users.

You can also create a setup where Tomcat sits behind a reverse proxy server (like Apache or NGINX)
and let the reverse proxy do the authentication. You can also let the reverse proxy do HTTPS
encryption.

In this case, you should also make sure that the Tomcat HTTP port is only accessible from
localhost by modifying the *Connector* configuration.

It is also possible to let Tomcat do HTTPS encryption by configuring a
*Connector* with *SSLEnable="true"*. You may then have to deal with preparing a Java
keystore with the certificates, though.

> [!CAUTION]
> The provided configuration allows HTTP connections to port 8080 from anywhere, which
> should never be used except for testing. Please change this on a production server.
