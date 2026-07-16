package de.dassit.nupama;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.dao.GenericRawResults;
import com.j256.ormlite.field.DataType;
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

	public List<Profile> getAllWithCount() {
		List<Profile> result = new ArrayList<Profile>();
		try {
			String queryStr = "select p.id, p.name, p.description, p.owner, p.config_opts, " + "count(p.id) "
					+ "from profile as p,host where p.id = host.profile GROUP BY p.id";
			DataType[] columnTypes = { DataType.INTEGER, DataType.STRING, DataType.STRING, DataType.STRING,
                                        DataType.STRING, DataType.INTEGER };
			GenericRawResults<Object[]> rawResults = dao.queryRaw(queryStr, columnTypes);
			for(Object[] row:rawResults) {
				Profile p = new Profile();
				p.setId((Integer)row[0]);
				p.setName((String)row[1]);
				p.setDescription((String)row[2]);
				p.setOwner((String)row[3]);
				p.setConfigOpts((String)row[4]);
				p.setHostCount((Integer)row[5]);
				result.add(p);
			}
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

		if (result == null || result.size() == 0)
			return null;
		else
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
