--
-- Name: plpgsql; Type: EXTENSION; Schema: -; Owner: 
--

-- CREATE EXTENSION IF NOT EXISTS plpgsql WITH SCHEMA pg_catalog;
CREATE LANGUAGE plpgsql;

--
-- Name: EXTENSION plpgsql; Type: COMMENT; Schema: -; Owner: 
--

-- COMMENT ON EXTENSION plpgsql IS 'PL/pgSQL procedural language';


-- SET search_path = public, pg_catalog;

--
-- Name: update_notification(); Type: FUNCTION; Schema: public; Owner: packagemanager
--

CREATE FUNCTION update_notification() RETURNS trigger
    LANGUAGE plpgsql
    AS $$
   BEGIN
        UPDATE notification SET serial = NEXTVAL('notification_serial_seq') WHERE id = 1;
        RETURN NEW;
    END;
$$;


SET default_tablespace = '';

SET default_with_oids = false;

--
-- Name: notification; Type: TABLE; Schema: public; Owner: packagemanager; Tablespace: 
--

CREATE TABLE notification (
    id integer NOT NULL,
    serial integer NOT NULL,
    ack integer
);


CREATE TABLE upstream_repo (
	id SERIAL PRIMARY KEY,
	name VARCHAR(80) NOT NULL UNIQUE,
	description VARCHAR(130),
	url VARCHAR(250),
	dist VARCHAR(80),
	component VARCHAR(40),
	arch VARCHAR(40),
	type VARCHAR(40) NOT NULL,
	pubkey VARCHAR(8000)
);

CREATE TABLE repository (
	id SERIAL PRIMARY KEY,
	name VARCHAR(80) NOT NULL UNIQUE,
	description VARCHAR(130),
	pred INTEGER REFERENCES repository(id),
	origin INTEGER REFERENCES repository(id),
	upstream INTEGER REFERENCES upstream_repo(id),
	upd_count INTEGER NOT NULL,
	upd_count_origin INTEGER NOT NULL,
	approvalreqd BOOLEAN NOT NULL,
	approved BOOLEAN NOT NULL,
	atomic BOOLEAN NOT NULL,
	owner VARCHAR(80),
	signingmode BOOLEAN NOT NULL,
	schedule VARCHAR(40),
	last_update TIMESTAMP,
	action VARCHAR(8),
	result VARCHAR(120)
);

CREATE TABLE package (
	id SERIAL PRIMARY KEY,
	name VARCHAR(80) NOT NULL,
	description VARCHAR(130),
	version VARCHAR(80) NOT NULL,
	vers_origin VARCHAR(80),
	vers_pred VARCHAR(80),
	arch VARCHAR(40),
	repo INTEGER REFERENCES repository(id),
	flags VARCHAR(16),
	UNIQUE (name, arch, repo)
);

CREATE TABLE profile (
	id SERIAL PRIMARY KEY,
	name VARCHAR(80) NOT NULL UNIQUE,
	description VARCHAR(130),
	owner VARCHAR(80),
	config_opts VARCHAR(130)
);

CREATE TABLE host (
	id SERIAL PRIMARY KEY,
	name VARCHAR(80) NOT NULL UNIQUE,
	description VARCHAR(130),
	owner VARCHAR(80),
	profile INTEGER REFERENCES profile(id),
	ip_addr VARCHAR(40),
	mac_addr VARCHAR(17),
	options VARCHAR(130),
	upd_count INTEGER NOT NULL,
	upd_count_origin INTEGER NOT NULL,
	reboot_required BOOLEAN NOT NULL,
	action VARCHAR(8),
	result VARCHAR(120),
	needsrefresh BOOLEAN NOT NULL
);

CREATE TABLE installed_pkg (
	name VARCHAR(80),
	arch VARCHAR(20),
	vers_local VARCHAR(80),
	vers_repo VARCHAR(80),
	vers_origin VARCHAR(80),
	host INTEGER REFERENCES host(id),
	fromrepo INTEGER,
	flags VARCHAR(16),
	PRIMARY KEY (name, arch, host)
);

CREATE TABLE profile_repo (
	profile INTEGER REFERENCES profile(id),
	repo INTEGER REFERENCES repository(id),
	PRIMARY KEY (profile, repo)
);

CREATE TABLE notification (
	id INTEGER NOT NULL PRIMARY KEY,
	serial SERIAL,
	ack INTEGER NOT NULL
);

CREATE TABLE taskstatus (
	id SERIAL PRIMARY KEY,
	task VARCHAR(40),
	progress VARCHAR(40),
	message VARCHAR(250)
);

CREATE TABLE config (
	key VARCHAR(40) PRIMARY KEY,
	value INTEGER
);

INSERT INTO config VALUES('schema_version', 2);

CREATE TRIGGER notification_trigger1 AFTER INSERT OR UPDATE OR DELETE ON host FOR EACH STATEMENT EXECUTE PROCEDURE update_notification();
CREATE TRIGGER notification_trigger2 AFTER INSERT OR UPDATE OR DELETE ON repository FOR EACH STATEMENT EXECUTE PROCEDURE update_notification();
CREATE TRIGGER notification_trigger3 AFTER INSERT OR UPDATE OR DELETE ON profile_repo FOR EACH STATEMENT EXECUTE PROCEDURE update_notification();

INSERT INTO notification VALUES(1,1,1);
INSERT INTO taskstatus VALUES(1,'','','');
