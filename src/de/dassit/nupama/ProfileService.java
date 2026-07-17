package de.dassit.nupama;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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

	public Map<Integer,Integer> getHostCounts() {
		HashMap<Integer, Integer> result = new HashMap<Integer,Integer>();
		try {
			String queryStr = "select profile.id, count(profile.id) "
					+ "from profile,host where profile.id = host.profile GROUP BY profile.id";
			DataType[] columnTypes = { DataType.INTEGER, DataType.INTEGER };
			GenericRawResults<Object[]> rawResults = dao.queryRaw(queryStr, columnTypes);
			for (Object[] row : rawResults) {
				Integer profileId = (Integer) row[0];
				Integer hostCount = (Integer) row[1];
				result.put(profileId, hostCount);
			}
		} catch (SQLException e) {
			LOGGER.log(Level.SEVERE, e.getMessage(), e);
		}
		return result;
	}

	public Map<Integer, List<String>> getRepoMap() {
		HashMap<Integer, List<String>> result = new HashMap<Integer, List<String>>();
		try {
			String queryStr = "select profile.id, repository.name from repository, profile, profile_repo "
					+ "where repository.id = profile_repo.repo and profile.id = profile_repo.profile "
					+ "order by profile.id,repository.name";
			DataType[] columnTypes = { DataType.INTEGER, DataType.STRING };
			GenericRawResults<Object[]> rawResults = dao.queryRaw(queryStr, columnTypes);
			for (Object[] row : rawResults) {
				Integer repoId = (Integer) row[0];
				String repoName = (String) row[1];
				List<String> repoNames = result.get(repoId);
				if (repoNames == null) {
					repoNames = new ArrayList<String>();
				}
				repoNames.add(repoName);
				result.put(repoId, repoNames);
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
