package de.dassit.nupama;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.stmt.DeleteBuilder;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;

public class ProfileRepoService {
	private Dao<ProfileRepo, Object> dao;
	private static final Logger LOGGER = Logger.getLogger(ProfileRepoService.class.getName());

	public ProfileRepoService() {
		try {
			ConnectionSource connectionSource = DbConnection.getConnectionSource();
			dao = DaoManager.createDao(connectionSource, ProfileRepo.class);
		} catch (SQLException e) {
			LOGGER.log(Level.SEVERE, e.getMessage(), e);
			dao = null;
		}
	}

	public void createTable() throws SQLException {
		ConnectionSource connectionSource = DbConnection.getConnectionSource();
		TableUtils.createTableIfNotExists(connectionSource, ProfileRepo.class);
	}

	public List<ProfileRepo> getAll() {
		List<ProfileRepo> result = null;
		try {
			result = dao.queryForAll();
		} catch (SQLException e) {
			LOGGER.log(Level.SEVERE, e.getMessage(), e);
		}
		return result;
	}

	public ProfileRepo makeNew() {
		return new ProfileRepo();
	}

	public List<ProfileRepo> getByProfileId(Integer i) {
		List<ProfileRepo> result = null;

		try {
			result = dao.queryForEq("profile", i);
		} catch (SQLException e) {
			LOGGER.log(Level.SEVERE, e.getMessage(), e);
		}

		return result;
	}

	public List<ProfileRepo> getByProfile(Profile p) {
		return getByProfileId(p.getId());
	}
	
	public List<Repository> getReposFromProfile(Profile p) {
		List<Repository> repos = new ArrayList<Repository>();
		
		List<ProfileRepo> result = getByProfile(p);
		for(ProfileRepo pr:result) {
			repos.add(pr.getRepo());
		}
		return repos;
	}

	public void addRepoToProfile(Repository r, Profile p) throws SQLException {
		ProfileRepo pr = new ProfileRepo();
		pr.setRepo(r);
		pr.setProfile(p);
		save(pr);
	}

	private DeleteBuilder<ProfileRepo, Object> getDeleteQuery(Repository r, Profile p) throws SQLException {
		DeleteBuilder<ProfileRepo,Object> builder;
		
		builder = dao.deleteBuilder();
		builder.where()
			.eq("repo", r.getId())
			.and()
			.eq("profile", p.getId());
		return builder;
	}

	public void removeRepoFromProfile(Repository r, Profile p) throws SQLException {
		getDeleteQuery(r,p).delete();
	}

	public List<ProfileRepo> getByRepoId(Integer i) {
		List<ProfileRepo> result = null;

		try {
			result = dao.queryForEq("repo", i);
		} catch (SQLException e) {
			LOGGER.log(Level.SEVERE, e.getMessage(), e);
		}

		return result;
	}

	public void save(ProfileRepo r) throws SQLException {
		dao.create(r);
	}

	public void delete(ProfileRepo pr) throws SQLException {
		getDeleteQuery(pr.getRepo(), pr.getProfile()).delete();
		LOGGER.log(Level.INFO, "deleted " + pr.getProfile().getId() + "/" + pr.getRepo().getId());
	}
}
