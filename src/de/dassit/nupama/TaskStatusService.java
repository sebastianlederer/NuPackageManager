package de.dassit.nupama;

import java.sql.SQLException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;

public class TaskStatusService {
	private Dao<TaskStatus, Integer> dao;
	private static final Logger LOGGER = Logger.getLogger(TaskStatusService.class.getName());

	public TaskStatusService() {
		try {
			ConnectionSource connectionSource = DbConnection.getConnectionSource();
			dao = DaoManager.createDao(connectionSource, TaskStatus.class);
		} catch (SQLException e) {
			LOGGER.log(Level.SEVERE, e.getMessage(), e);
			dao = null;
		}
	}

	public void createTable() throws SQLException {
		ConnectionSource connectionSource = DbConnection.getConnectionSource();
		TableUtils.createTableIfNotExists(connectionSource, InstalledPackage.class);		
	}
	
	public List<TaskStatus> getAll() {
		List<TaskStatus> result = null;
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
	
	public TaskStatus getById(Integer i) {
		List<TaskStatus> result = null;

			try {
				result = dao.queryForEq("id",i);
			} catch (SQLException e) {
				LOGGER.log(Level.SEVERE, e.getMessage(), e);
			}

			return result.get(0);
	}
	
	public void save(TaskStatus r) throws SQLException {
		dao.createOrUpdate(r);
		LOGGER.log(Level.INFO, "saved " + r.getId());
	}
	
	public TaskStatus getDefaultStatus() {
		return getById(1);
	}
	
	public void update(String task, String progress, String message) throws SQLException {
		TaskStatus s = getDefaultStatus();
		s.setTask(task);
		s.setProgress(progress);
		s.setMessage(message);
		save(s);
	}
	
	public void updateProgress(String progress) throws SQLException {
		TaskStatus s = getDefaultStatus();
		s.setProgress(progress);
		save(s);
	}
}
