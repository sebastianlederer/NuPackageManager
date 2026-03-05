package de.dassit.nupama;

import java.io.Serializable;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable(tableName = "property")
public class Property implements Cloneable, Serializable {

	private static final long serialVersionUID = 1L;

	@DatabaseField(foreign = true, foreignAutoRefresh = true, canBeNull = true)
	private Profile profile;
	@DatabaseField(foreign = true, foreignAutoRefresh = true, canBeNull = true)
	private Host host;
        @DatabaseField
        private String key;
        @DatabaseField
        private String value;

	public Profile getProfile() {
		return profile;
	}

	public void setProfile(Profile profile) {
		this.profile = profile;
	}

	public Host getHost() {
		return host;
	}

	public void setHost(Host h) {
		this.host = h;
	}
	
	public void setKey(String s) {
		key = s;
	}
	
	public String getKey() {
		return key;
	}
	public void setValue(String s) {
		value = s;
	}
	
	public String getValue() {
		return value;
	}
}
