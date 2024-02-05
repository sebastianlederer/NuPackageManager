package de.dassit.nupama;

import java.sql.SQLException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;

public class HostService {
	private Dao<Host, Integer> dao;
	private static final Logger LOGGER = Logger.getLogger(HostService.class.getName());

	public HostService() {
		try {
			ConnectionSource connectionSource = DbConnection.getConnectionSource();
			dao = DaoManager.createDao(connectionSource, Host.class);
		} catch (SQLException e) {
			LOGGER.log(Level.SEVERE, e.getMessage(), e);
			dao = null;
		}
	}

	public void createTable() throws SQLException {
		ConnectionSource connectionSource = DbConnection.getConnectionSource();
		TableUtils.createTableIfNotExists(connectionSource, Host.class);		
	}
	
	public List<Host> getAll() {
		List<Host> result = null;
		try {
			result = dao.queryForAll();
		} catch (SQLException e) {
			LOGGER.log(Level.SEVERE, e.getMessage(), e);
		}
		return result;
	}

        public List<Host> getByProfile(Profile p) {
                List<Host> result = null;
                try {
                        result = dao.queryForEq("profile", p.getId());
                } catch (SQLException e) {
                        LOGGER.log(Level.SEVERE, e.getMessage(), e);
                }
                return result;
        }

	public Host makeNew() {
		return new Host();
	}
	
	public Host getByName(String n) {
		List<Host> result = null;

			try {
				result = dao.queryForEq("name",n);
			} catch (SQLException e) {
				LOGGER.log(Level.SEVERE, e.getMessage(), e);
			}
	                if(result == null || result.size() == 0)
                            return null;
                        else
			    return result.get(0);
	}

	public Host getById(Integer i) {
		List<Host> result = null;

			try {
				result = dao.queryForEq("id",i);
			} catch (SQLException e) {
				LOGGER.log(Level.SEVERE, e.getMessage(), e);
			}

			return result.get(0);
	}
	
	public void save(Host h) throws SQLException {
		dao.createOrUpdate(h);
		LOGGER.log(Level.INFO, "saved " + h.getName());
	}
	
	public void delete(Host h) throws SQLException {
		dao.delete(h);
		LOGGER.log(Level.INFO, "deleted " + h.getName());
	}
}
