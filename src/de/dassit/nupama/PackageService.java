package de.dassit.nupama;

import java.sql.SQLException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.stmt.DeleteBuilder;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;

public class PackageService {
	private Dao<Package, Integer> dao;
	private static final Logger LOGGER = Logger.getLogger(PackageService.class.getName());

	public PackageService() {
		try {
			ConnectionSource connectionSource = DbConnection.getConnectionSource();
			dao = DaoManager.createDao(connectionSource, Package.class);
		} catch (SQLException e) {
			LOGGER.log(Level.SEVERE, e.getMessage(), e);
			dao = null;
		}
	}

	public void createTable() throws SQLException {
		ConnectionSource connectionSource = DbConnection.getConnectionSource();
		TableUtils.createTableIfNotExists(connectionSource, Package.class);		
	}
	
	public List<Package> getAll() {
		List<Package> result = null;
		try {
			result = dao.queryForAll();
		} catch (SQLException e) {
			LOGGER.log(Level.SEVERE, e.getMessage(), e);
		}
		return result;
	}
	
	public Package makeNew() {
		return new Package();
	}
	
	public List<Package> getByRepoId(Integer i) {
		List<Package> result = null;

			try {
				result = dao.queryForEq("repo",i);
			} catch (SQLException e) {
				LOGGER.log(Level.SEVERE, e.getMessage(), e);
			}
			
			return result;
	}

	public Package getById(Integer i) {
		List<Package> result = null;

			try {
				result = dao.queryForEq("id",i);
			} catch (SQLException e) {
				LOGGER.log(Level.SEVERE, e.getMessage(), e);
			}

			return result.get(0);
	}
	
	public void deleteByRepository(Repository aRepo) throws SQLException {
		DeleteBuilder<Package,Integer> deleter = dao.deleteBuilder();
		deleter.where().eq("repo", aRepo.getId());
		deleter.delete();
	}
	
	public void save(Package r) throws SQLException {
		dao.createOrUpdate(r);
		LOGGER.log(Level.INFO, "saved " + r.getName());
	}
}
