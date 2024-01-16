package de.dassit.nupama;

import java.sql.SQLException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;

public class UpstreamRepoService {
	private Dao<UpstreamRepo, Integer> dao;
	private static final Logger LOGGER = Logger.getLogger(UpstreamRepoService.class.getName());

	public UpstreamRepoService() {
		try {
			ConnectionSource connectionSource = DbConnection.getConnectionSource();
			dao = DaoManager.createDao(connectionSource, UpstreamRepo.class);
		} catch (SQLException e) {
			LOGGER.log(Level.SEVERE, e.getMessage(), e);
			dao = null;
		}
	}

	public String test() {
		return "test";
	}

	public void createTable() throws SQLException {
		ConnectionSource connectionSource = DbConnection.getConnectionSource();
		TableUtils.createTableIfNotExists(connectionSource, UpstreamRepo.class);		
	}
	
	public List<UpstreamRepo> getAll() {
		List<UpstreamRepo> result = null;
		try {
			result = dao.queryForAll();
		} catch (SQLException e) {
			LOGGER.log(Level.SEVERE, e.getMessage(), e);
		}
		return result;
	}
	
	public UpstreamRepo makeNew() {
		return new UpstreamRepo();
	}
	
	public UpstreamRepo getByName(String n) {
		List<UpstreamRepo> result = null;

			try {
				result = dao.queryForEq("name",n);
			} catch (SQLException e) {
				LOGGER.log(Level.SEVERE, e.getMessage(), e);
			}
			
			return result.get(0);
	}

	public UpstreamRepo getById(Integer i) {
		List<UpstreamRepo> result = null;

			try {
				result = dao.queryForEq("id",i);
				return result.get(0);
			} catch (SQLException e) {
				LOGGER.log(Level.SEVERE, e.getMessage(), e);
				return null;
			}

	}
	
	public void save(UpstreamRepo r) throws SQLException {
		dao.createOrUpdate(r);
		LOGGER.log(Level.INFO, "saved " + r.getName());
	}
	
	public void delete(UpstreamRepo r) throws SQLException {
		dao.delete(r);
		LOGGER.log(Level.INFO, "deleted " + r.getName());
	}
}
