package de.dassit.nupama;

import java.io.Serializable;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable(tableName = "profile_repo")
public class ProfileRepo implements Cloneable, Serializable {

	private static final long serialVersionUID = 1L;

	@DatabaseField(columnName = "profile", foreign = true, foreignAutoRefresh = true)
	private Profile profile;
	@DatabaseField(columnName = "repo", foreign = true, foreignAutoRefresh = true)
	private Repository repo;

	public Profile getProfile() {
		return profile;
	}

	public void setProfile(Profile profile) {
		this.profile = profile;
	}

	public Repository getRepo() {
		return repo;
	}

	public void setRepo(Repository repo) {
		this.repo = repo;
	}
}