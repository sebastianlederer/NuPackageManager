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
* tomcat/webapps/nupama/hosts/action-presets.inc.dist
* tomcat/webapps/nupama/WEB-INF/tools.xml.dist
* tomcat/webapps/nupama/WEB-INF/web.xml.dist
* tomcat/webapps/nupama/META-INF/context.xml.dist
* conf/apt.blacklist.dist
* conf/apt.blacklist.re.dist
* conf/actions/reboot.dist
* conf/actions/pubkey.dist
* conf/actions/report.dist
* conf/actions/config.dist
* conf/actions/cleanconfig.dist
* conf/actions/upgrade.dist
* conf/backend.conf.dist


> [!NOTE]
> You need to modify at least these files according to your installation:
> * conf/backend.conf - set the server url, mirroring directory and SSH identity
> * tomcat/nupama-tomcat.properties - set server.location to your install directory
> * tomcat/conf/server.xml.dist - set up authentication, or delegate it to reverse proxy
> * tomcat/conf/tomcat-users.xml.dist - set up users and groups if you use Tomcat builtin authentication
> * tomcat/webapps/nupama/hosts/action-presets.inc.dist
Backend Configuration
----------------------
### SSH identity
Nupama will make SSH connections to the managed hosts to gather information,
configure repositories, do updates and reboots. You can configure the user that
Nupama will use for the SSH connection, and the SSH key, with *ssh_user* and
*ssh_key* in *conf/backend.conf*.
You also need to make sure that the host keys of all managed hosts are stored
in the *.ssh/known_hosts* file of *ssh_user*.

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
action script is fed as standard input to the remote shell via SSH.
In some cases (*config*, *cleanconfig*, *pubkey*), a simple templating
mechanism is used. The variables *repo*, *conf*, *repos*, and *pubkey* can
be referenced with a preceeding dollar sign. If you want to include a literal
dollar sign inside a template, use a double dollar sign.

Action scripts are stored in the *conf/actions* subdirectory.

* *config* is used to create the repository configuration - for APT repositories, this
   writes files under  */etc/apt/sources.list.d*. It is called once per repository. On each call,
   the variables *repo* and *conf* are set accordingly.
* *cleanconfig* removes repositories which are no longer configured for a managed host - it is executed once per host and gets the template variable *repos*
* *pubkey* installs a repository signing key, if there is one. It is called once per repository.
   On each call, the variables *repo* and *pubkey* are set.
* *reboot* reboots the target machine
* *report* creates a report of all installed packages - this script must output
   a line for each package with three space-separated fields: Package name, version
  and architecture
* *upgrade* executes an upgrade command on the target machine - for APT repositories,
   this is the command *apt update && apt upgrade*

#### Variables
* *repo* contains the name of the current repository
* *pubkey* contains the base64-encoded public key of the repository, if there is one
* *conf* contains the repository configuration text for the current repository, e.g. the line for an
APT source configuration
* *repos* contains a string will all repositories configured for a managed host, separated by spaces

### Timeouts
The connection timeout for SSH connections can be set with *ssh_timeout*
in *conf/backend.conf*.

You can also configure a timeout for running action scripts with the
*command_timeout* variable in *conf/backend.conf*.

### Parallel SSH connections
When executing host actions, several SSH connections are made concurrently.
The number of parallel connections can be configured in *conf/backend.conf*
with the *ssh_processes* variable.

Frontend Configuration
-------------------------
The web application is a servlet inside an Tomcat embedded application server. Standard
Tomcat and servlet configuration mechanisms apply.
Important configuration files are:
* Tomcat configuration: ports, authentication in *tomcat/conf/server.xml*
* Database connection (e.g. set a different password) in *tomcat/webapps/nupama/META-INF/context.xml*

### Actions
In the *Hosts* view, you can issue actions for managed hosts, either by selecting a single host,
multi-selection or by selecting a profile.

Standard host actions are:
* *Report* - retrieve information about installed packages and available updates
* *Configure* - configure the package management (e.g. APT or DNF) on the managed host to
  use the repositories from Nupama
* *Update* - execute pending updates - will also execute *configure* and *report*
* *Reboot* - reboot the managed host
* *Custom* - execute a user-specified command

### Custom Actions
For the *Custom* action, you can configure multiple presets which can be
selected from a drop-down menu instead of typing a command.

The custom actions are defined in the file *tomcat/webapps/nupama/hosts/action-presets.inc*.

The file uses the *Velocity* macro language to set an array variable comprised of strings.

Example:
<code>
#set($actionPresets = [
"echo test;false",
"sleep 10"
]
</code>

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
