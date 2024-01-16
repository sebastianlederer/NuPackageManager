package de.dassit.nupama;

import java.io.Serializable;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable(tableName = "installed_pkg")
public class InstalledPackage implements Cloneable, Serializable {
	
	private static final long serialVersionUID = 1L;
	
	@DatabaseField(id=true)
	private String name;
	@DatabaseField(canBeNull = true)
	private String arch;
	@DatabaseField(columnName = "vers_local", canBeNull = true)
	private String localVersion;
	@DatabaseField(columnName = "vers_repo", canBeNull = true)
	private String originVersion;
	@DatabaseField(columnName = "vers_origin", canBeNull = true)
	private String repoVersion;
	@DatabaseField(columnName = "host", foreign = true)
	private Host host;
	@DatabaseField(columnName = "fromrepo", foreign = true, canBeNull = true)
	private Repository fromrepo;
	@DatabaseField
	private String flags;

	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getArch() {
		return arch;
	}
	public void setArch(String arch) {
		this.arch = arch;
	}
	public String getLocalVersion() {
		return localVersion;
	}
	public void setLocalVersion(String localVersion) {
		this.localVersion = localVersion;
	}
	public String getOriginVersion() {
		return originVersion;
	}
	public void setOriginVersion(String originVersion) {
		this.originVersion = originVersion;
	}
	public String getRepoVersion() {
		return repoVersion;
	}
	public void setRepoVersion(String predVersion) {
		this.repoVersion = predVersion;
	}
	public Host getHost() {
		return host;
	}
	public void setHost(Host repo) {
		this.host = repo;
	}
	public Repository getFromrepo() {
		return fromrepo;
	}
	public void setFromrepo(Repository fromrepo) {
		this.fromrepo = fromrepo;
	}
	public String getFlags() {
		return flags;
	}
	public void setFlags(String flags) {
		this.flags = flags;
	}	
}