package de.dassit.nupama;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.misc.TransactionManager;
import com.j256.ormlite.stmt.DeleteBuilder;
import com.j256.ormlite.stmt.QueryBuilder;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;

public class RoleService {
	private Dao<Role, Object> dao;
	private static final Logger LOGGER = Logger.getLogger(RoleService.class.getName());

	public RoleService() {
		try {
			ConnectionSource connectionSource = DbConnection.getConnectionSource();
			dao = DaoManager.createDao(connectionSource, Role.class);
		} catch (SQLException e) {
			LOGGER.log(Level.SEVERE, e.getMessage(), e);
			dao = null;
		}
	}

	public void createTable() throws SQLException {
		ConnectionSource connectionSource = DbConnection.getConnectionSource();
		TableUtils.createTableIfNotExists(connectionSource, Role.class);
	}

	public List<Role> getAll() {
		List<Role> result = null;
		try {
			result = dao.queryForAll();
		} catch (SQLException e) {
			LOGGER.log(Level.SEVERE, e.getMessage(), e);
		}
		return result;
	}

	public Role makeNew() {
		return new Role();
	}

	public List<Role> getByProfileId(Integer i) {
		List<Role> result = null;

		try {
			result = dao.queryForEq("profile", i);
		} catch (SQLException e) {
			LOGGER.log(Level.SEVERE, e.getMessage(), e);
		}

		return result;
	}

	public List<Role> getByProfile(Profile p) {
		return getByProfileId(p.getId());
	}

	public List<Role> getByHostId(Integer i) {
		List<Role> result = null;

		try {
			result = dao.queryForEq("host", i);
		} catch (SQLException e) {
			LOGGER.log(Level.SEVERE, e.getMessage(), e);
		}

		return result;
	}

	public List<Role> getByHost(Host h) {
		return getByHostId(h.getId());
	}

	public void setRolesForHost(Host h, String[] rolenames) throws SQLException {
		TransactionManager.callInTransaction(DbConnection.getConnectionSource(),
				new Callable<Void>() {
			public Void call() throws Exception {
				DeleteBuilder<Role,Object> b = dao.deleteBuilder();
				b.where().eq(Role.HOST_FIELD_NAME, h.getId());
				b.delete();
				for(String n:rolenames) {
					Role r = makeNew();
					r.setHost(h);
					r.setName(n);
					save(r);
				}
				return null;
			}
		});
	}

	public void setRolesForProfile(Profile p, String[] rolenames) throws SQLException {
		TransactionManager.callInTransaction(DbConnection.getConnectionSource(),
				new Callable<Void>() {
			public Void call() throws Exception {
				DeleteBuilder<Role,Object> b = dao.deleteBuilder();
				b.where().eq(Role.PROFILE_FIELD_NAME, p.getId());
				b.delete();
				for(String n:rolenames) {
					Role r = makeNew();
					r.setProfile(p);
					r.setName(n);
					save(r);
				}
				return null;
			}
		});
	}

	public List<String> getCombinedRoleNames(Host h) throws SQLException {
		Profile p = h.getProfile();

		List<String> result = new ArrayList<String>();
		QueryBuilder<Role,Object> q = dao.queryBuilder();
		if(p == null)
			q.where().eq("host", h.getId());
		else
			q.where().eq("host", h.getId()).or().eq("profile", p.getId());
		List<Role> roles = q.orderBy("name", true).query();
		for(Role r:roles)
			result.add(r.getName());
		return result.stream().distinct().collect(Collectors.toList());
	}

	public void save(Role r) throws SQLException {
		dao.create(r);
	}

	public void delete(Role r) throws SQLException {
		dao.delete(r);
		LOGGER.log(Level.INFO, "deleted " + r.getId());
	}
}
