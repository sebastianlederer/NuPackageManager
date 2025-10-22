package de.dassit.nupama;

import java.io.Serializable;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable(tableName = "role")
public class Role implements Cloneable, Serializable {

	private static final long serialVersionUID = 1L;
	public static final String HOST_FIELD_NAME = "host";

	@DatabaseField(generatedId = true)
	private Integer id;
	@DatabaseField
	private String name;
	@DatabaseField(canBeNull = true)
	private String description;
	@DatabaseField(columnName = "profile", foreign = true, foreignAutoRefresh = true)
	private Profile profile;
	@DatabaseField(columnName = "host", foreign = true, foreignAutoRefresh = true)
	private Host host;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Profile getProfile() {
		return profile;
	}

	public void setProfile(Profile profile) {
		this.profile = profile;
	}

	public Host getHost() {
		return host;
	}

	public void setHost(Host host) {
		this.host = host;
	}
}
