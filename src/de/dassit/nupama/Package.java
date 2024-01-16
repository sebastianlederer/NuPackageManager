package de.dassit.nupama;

import java.io.Serializable;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable(tableName = "package")
public class Package implements Cloneable, Serializable {
	
	private static final long serialVersionUID = 1L;
	
	@DatabaseField(generatedId = true)
	private Integer id;
	@DatabaseField
	private String name;
	@DatabaseField(canBeNull=true)
	private String description;
	@DatabaseField
	private String version;
	@DatabaseField(columnName = "vers_origin", canBeNull = true)
	private String versionOrigin;
	@DatabaseField(columnName = "vers_pred", canBeNull = true)
	private String versionPred;
	@DatabaseField(canBeNull = true)
	private String arch;
	@DatabaseField(columnName = "repo", foreign = true)
	private Repository repo;
	@DatabaseField
	private String flags;

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
	public String getVersion() {
		return version;
	}
	public void setVersion(String version) {
		this.version = version;
	}
	public String getVersionOrigin() {
		return versionOrigin;
	}
	public void setVersionOrigin(String versionOrigin) {
		this.versionOrigin = versionOrigin;
	}
	public String getVersionPred() {
		return versionPred;
	}
	public void setVersionPred(String versionPred) {
		this.versionPred = versionPred;
	}
	public String getArch() {
		return arch;
	}
	public void setArch(String arch) {
		this.arch = arch;
	}
	public Repository getRepo() {
		return repo;
	}
	public void setRepo(Repository repo) {
		this.repo = repo;
	}
	public String getFlags() {
		return flags;
	}
	public void setFlags(String flags) {
		this.flags = flags;
	}	
}