import configparser

dsn=None
ssh_processes=None
ssh_timeout=None
mirror_dir=None
server_url=None
repo_path=None
ssh_user=None
ssh_key=None
ssh_options=''
command_timeout=None
action_hook=None

def getconf(section, key, default):
    if key in section:
        return section[key]
    else:
        return default


def read_config():
    global dsn, mirror_dir, server_url, repo_path
    global ssh_user, ssh_key, ssh_processes, ssh_timeout, ssh_options

    conffile = '../conf/backend.conf'

    config = configparser.ConfigParser()
    config.read(conffile)

    section = config['DEFAULT'] 

    dsn        = section['dsn']
    mirror_dir = section['mirror_dir']
    server_url = section['server_url']
    repo_path  = section['repo_path']
    ssh_user   = section['ssh_user']
    ssh_key    = section['ssh_key']
    ssh_processes = int(getconf(section, 'ssh_processes', 4))
    ssh_timeout = int(getconf(section, 'ssh_timeout', 15))
    if 'ssh_options' in section:
        ssh_options = section['ssh_options']
    command_timeout = int(getconf(section, 'command_timeout', 900))
    if 'action_hook' in section:
        action_hook = section['action_hook']


def get_scriptlet(name):
    with open('../conf/actions/'+name, 'r') as f:
        script = f.read()
    return script
