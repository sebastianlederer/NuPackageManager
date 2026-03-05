package de.dassit.nupama;

import java.sql.SQLException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.stmt.DeleteBuilder;
import com.j256.ormlite.stmt.QueryBuilder;
import com.j256.ormlite.stmt.UpdateBuilder;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;

public class PropertyService {
	private Dao<Property, Object> dao;
	private static final Logger LOGGER = Logger.getLogger(PropertyService.class.getName());

	public PropertyService() {
		try {
			ConnectionSource connectionSource = DbConnection.getConnectionSource();
			dao = DaoManager.createDao(connectionSource, Property.class);
		} catch (SQLException e) {
			LOGGER.log(Level.SEVERE, e.getMessage(), e);
			dao = null;
		}
	}

	public void createTable() throws SQLException {
		ConnectionSource connectionSource = DbConnection.getConnectionSource();
		TableUtils.createTableIfNotExists(connectionSource, Property.class);
	}

	public List<Property> getAll() {
		List<Property> result = null;
		try {
			result = dao.queryForAll();
		} catch (SQLException e) {
			LOGGER.log(Level.SEVERE, e.getMessage(), e);
		}
		return result;
	}

	public Property makeNew() {
		return new Property();
	}

	public List<Property> getByProfileId(Integer i) {
		List<Property> result = null;

		try {
			result = dao.queryForEq("profile", i);
		} catch (SQLException e) {
			LOGGER.log(Level.SEVERE, e.getMessage(), e);
		}

		return result;
	}

	public List<Property> getByProfile(Profile p) {
		return getByProfileId(p.getId());
	}

	public List<Property> getByHostId(Integer i) {
		List<Property> result = null;

		try {
			result = dao.queryForEq("host", i);
		} catch (SQLException e) {
			LOGGER.log(Level.SEVERE, e.getMessage(), e);
		}

		return result;
	}

	public void save(Property r) throws SQLException {
		dao.create(r);
	}

	public Property get(Profile pr, Host h, String key) throws SQLException {
		QueryBuilder<Property, Object> q = dao.queryBuilder();

		if (pr != null) {
			q.where().eq("profile", pr.getId()).and().eq("key", key);
		} else {
			q.where().eq("host", h.getId()).and().eq("key", key);
		}
		List<Property> results = q.query();
		if (results.size() > 0) {
			return results.get(0);
		} else {
			return null;
		}
	}

	public void set(Profile pr, Host h, String key, String value) throws SQLException {
		if (pr != null)
			setOnProfile(pr, key, value);
		else
			setOnHost(h, key, value);
	}

	public boolean existsOnProfile(Profile pr, String key) throws SQLException {
		QueryBuilder<Property, Object> q = dao.queryBuilder();
		q.where().eq("profile", pr.getId()).and().eq("key", key);
		List<Property> results = q.query();

		return results.size() > 0;
	}

	public boolean existsOnHost(Host h, String key) throws SQLException {
		QueryBuilder<Property, Object> q = dao.queryBuilder();
		q.where().eq("host", h.getId()).and().eq("key", key);
		List<Property> results = q.query();

		return results.size() > 0;
	}

	public void updateOnProfile(Profile pr, String key, String value) throws SQLException {
		UpdateBuilder<Property, Object> u = dao.updateBuilder();
		u.updateColumnValue("key", key);
		u.updateColumnValue("value", value);
		u.where().eq("profile", pr.getId()).and().eq("key", key);
		u.update();
		LOGGER.log(Level.INFO, "update " + key + " of profile " + pr.getName() + " to " + value);
	}

	public void updateOnHost(Host h, String key, String value) throws SQLException {
		UpdateBuilder<Property, Object> u = dao.updateBuilder();
		u.updateColumnValue("key", key);
		u.updateColumnValue("value", value);
		u.where().eq("host", h.getId()).and().eq("key", key);
		u.update();
		LOGGER.log(Level.INFO, "update " + key + " of host " + h.getName() + " to " + value);
	}

	public void setOnProfile(Profile pr, String key, String value) throws SQLException {
		if (existsOnProfile(pr, key))
			updateOnProfile(pr, key, value);
		else {
			Property p = makeNew();
			p.setProfile(pr);
			p.setKey(key);
			p.setValue(value);
			LOGGER.log(Level.INFO, "set " + key + " of profile " + pr.getName() + " to " + value);
		}
	}

	public void setOnHost(Host h, String key, String value) throws SQLException {
		if (existsOnHost(h, key))
			updateOnHost(h, key, value);
		else {
			Property p = makeNew();
			p.setHost(h);
			p.setKey(key);
			p.setValue(value);
		}
		LOGGER.log(Level.INFO, "set " + key + " of host " + h.getName() + " to " + value);
	}

	public void deleteFromProfile(Profile pr, String key) throws SQLException {
		DeleteBuilder<Property, Object> d = dao.deleteBuilder();
		d.where().eq("profile", pr.getId());
		d.delete();
		LOGGER.log(Level.INFO, "deleted " + key + " from profile " + pr.getName());
	}

	public void deleteFromHost(Host h, String key) throws SQLException {
		DeleteBuilder<Property, Object> d = dao.deleteBuilder();
		d.where().eq("host", h.getId());
		d.delete();
		LOGGER.log(Level.INFO, "deleted " + key + " from host " + h.getName());
	}
}
