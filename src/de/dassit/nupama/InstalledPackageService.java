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

public class InstalledPackageService {
	private Dao<InstalledPackage, Integer> dao;
	private static final Logger LOGGER = Logger.getLogger(InstalledPackageService.class.getName());

	public InstalledPackageService() {
		try {
			ConnectionSource connectionSource = DbConnection.getConnectionSource();
			dao = DaoManager.createDao(connectionSource, InstalledPackage.class);
		} catch (SQLException e) {
			LOGGER.log(Level.SEVERE, e.getMessage(), e);
			dao = null;
		}
	}

	public void createTable() throws SQLException {
		ConnectionSource connectionSource = DbConnection.getConnectionSource();
		TableUtils.createTableIfNotExists(connectionSource, InstalledPackage.class);		
	}
	
	public List<InstalledPackage> getAll() {
		List<InstalledPackage> result = null;
		try {
			result = dao.queryForAll();
		} catch (SQLException e) {
			LOGGER.log(Level.SEVERE, e.getMessage(), e);
		}
		return result;
	}
	
	public InstalledPackage makeNew() {
		return new InstalledPackage();
	}
	
	public List<InstalledPackage> getByHostId(Integer i) {
		List<InstalledPackage> result = null;

			try {
				result = dao.queryForEq("host",i);
			} catch (SQLException e) {
				LOGGER.log(Level.SEVERE, e.getMessage(), e);
			}
			
			return result;
	}

	public InstalledPackage getById(Integer i) {
		List<InstalledPackage> result = null;

			try {
				result = dao.queryForEq("id",i);
			} catch (SQLException e) {
				LOGGER.log(Level.SEVERE, e.getMessage(), e);
			}

			return result.get(0);
	}
	
	public void deleteByHost(Host aHost) throws SQLException {
		DeleteBuilder<InstalledPackage,Integer> deleter = dao.deleteBuilder();
		deleter.where().eq("host", aHost.getId());
		deleter.delete();
	}
	
	public void save(InstalledPackage r) throws SQLException {
		dao.createOrUpdate(r);
		LOGGER.log(Level.INFO, "saved " + r.getName());
	}
}
