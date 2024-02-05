package de.dassit.nupama;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;

public class RepositoryService {
	private Dao<Repository, Integer> dao;
	private Dao<ProfileRepo, Integer> profileRepoDao;
	private Dao<UpstreamRepo, Integer> upstreamRepoDao;

	private static final Logger LOGGER = Logger.getLogger(RepositoryService.class.getName());

	public RepositoryService() {
		try {
			ConnectionSource connectionSource = DbConnection.getConnectionSource();
			dao = DaoManager.createDao(connectionSource, Repository.class);
			profileRepoDao = DaoManager.createDao(connectionSource, ProfileRepo.class);
			upstreamRepoDao = DaoManager.createDao(connectionSource, UpstreamRepo.class);
		} catch (SQLException e) {
			LOGGER.log(Level.SEVERE, e.getMessage(), e);
			dao = null;
		}
	}

	public void createTable() throws SQLException {
		ConnectionSource connectionSource = DbConnection.getConnectionSource();
		TableUtils.createTableIfNotExists(connectionSource, Repository.class);
	}

	private Repository addDaos(Repository r) {
		r.setDao(dao);
		r.setUpstreamRepoDao(upstreamRepoDao);
		return r;
	}
	
	public List<Repository> getAll() {
		List<Repository> result = null;
		try {
			// result = dao.queryForAll();
			result = dao.queryBuilder().orderBy("name", true).query();
		} catch (SQLException e) {
			LOGGER.log(Level.SEVERE, e.getMessage(), e);
		}
		for(Repository r:result) {
			r.setDao(dao);
			r.setUpstreamRepoDao(upstreamRepoDao);
		}
		return result;
	}

	private void addChildren(List<Repository> all, Repository parent, int indent,
			List<Repository> result) throws SQLException {
		for(Repository r:all) {
			Repository pred = r.getPred();
			if(pred != null && (pred.getId() == parent.getId())) {
				result.add(r);
				r.setIndent(indent);
				addChildren(all, r, indent + 1, result);
			}
		}
	}

	public List<Repository> getTree() throws SQLException {
		List<Repository> result = new ArrayList<Repository>();
		List<Repository> all = getAll();

		for(Repository r:all) {
			if(r.getPred() == null) {
				result.add(r);
				r.setIndent(0);
				// for each top-level repo, find repos which have that repo as
				// predecessor
				addChildren(all, r, 1, result);
			}
		}
		return result;
	}

	public Repository makeNew() {
		Repository aRepo = new Repository();
		aRepo.setDao(dao);
		aRepo.setUpstreamRepoDao(upstreamRepoDao);
		return aRepo;
	}

	public Repository getByName(String n) {
		List<Repository> result = null;

		try {
			result = dao.queryForEq("name", n);
		} catch (SQLException e) {
			LOGGER.log(Level.SEVERE, e.getMessage(), e);
		}

                if(result == null || result.size() == 0)
                    return null;
                else
		    return addDaos(result.get(0));
	}

	public boolean nameExists(String n) {
		List<Repository> result = null;

		try {
			result = dao.queryForEq("name", n);
		} catch (SQLException e) {
			LOGGER.log(Level.SEVERE, e.getMessage(), e);
		}

		return result.size() > 0;
	}

	public Repository getById(Integer i) {
		List<Repository> result = null;
		try {
			result = dao.queryForEq("id", i);
		} catch (SQLException e) {
			LOGGER.log(Level.SEVERE, e.getMessage(), e);
		}
		return addDaos(result.get(0));
	}

	public List<Repository> getAllByProfile(Profile p) {
		List<Repository> repos = new ArrayList<Repository>();
		List<ProfileRepo> result;
		Repository repo;
		try {
			result = profileRepoDao.queryForEq("profile", p.getId());
		} catch (SQLException e) {
			LOGGER.log(Level.SEVERE, e.getMessage(), e);
			return repos;
		}

		for (ProfileRepo prepo : result) {
			repo = prepo.getRepo();
			try {
				dao.refresh(repo);
			} catch (SQLException e) {
				LOGGER.log(Level.SEVERE, e.getMessage(), e);
			}
			repos.add(repo);
		}

		return repos;
	}

	public void save(Repository r) throws SQLException {
		dao.createOrUpdate(r);
		LOGGER.log(Level.INFO, "saved " + r.getName());
	}

	public void delete(Repository r) throws SQLException {
		dao.delete(r);
		LOGGER.log(Level.INFO, "deleted " + r.getName());
	}
}
