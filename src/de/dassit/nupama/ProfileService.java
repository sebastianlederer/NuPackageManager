package de.dassit.nupama;

import java.sql.SQLException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;

public class ProfileService {
	private Dao<Profile, Integer> dao;
	private static final Logger LOGGER = Logger.getLogger(ProfileService.class.getName());

	public ProfileService() {
		try {
			ConnectionSource connectionSource = DbConnection.getConnectionSource();
			dao = DaoManager.createDao(connectionSource, Profile.class);
		} catch (SQLException e) {
			LOGGER.log(Level.SEVERE, e.getMessage(), e);
			dao = null;
		}
	}

	public void createTable() throws SQLException {
		ConnectionSource connectionSource = DbConnection.getConnectionSource();
		TableUtils.createTableIfNotExists(connectionSource, Profile.class);
	}

	public List<Profile> getAll() {
		List<Profile> result = null;
		try {
			result = dao.queryForAll();
		} catch (SQLException e) {
			LOGGER.log(Level.SEVERE, e.getMessage(), e);
		}
		return result;
	}

	public Profile makeNew() {
		return new Profile();
	}

	public Profile getByName(String n) {
		List<Profile> result = null;

		try {
			result = dao.queryForEq("name", n);
		} catch (SQLException e) {
			LOGGER.log(Level.SEVERE, e.getMessage(), e);
		}

		return result.get(0);
	}

	public Profile getById(Integer i) {
		List<Profile> result = null;

		try {
			result = dao.queryForEq("id", i);
		} catch (SQLException e) {
			LOGGER.log(Level.SEVERE, e.getMessage(), e);
		}

		return result.get(0);
	}

	public boolean nameExists(String n) {
		List<Profile> result = null;

		try {
			result = dao.queryForEq("name", n);
		} catch (SQLException e) {
			LOGGER.log(Level.SEVERE, e.getMessage(), e);
		}

		return result.size() > 0;
	}

	public void save(Profile r) throws SQLException {
		dao.createOrUpdate(r);
		LOGGER.log(Level.INFO, "saved " + r.getName());
	}

	public void delete(Profile p) throws SQLException {
		dao.delete(p);
		LOGGER.log(Level.INFO, "deleted " + p.getName());
	}
}
