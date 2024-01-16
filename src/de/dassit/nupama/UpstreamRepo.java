package de.dassit.nupama;

import java.io.Serializable;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable(tableName = "upstream_repo")
public class UpstreamRepo implements Cloneable, Serializable {

	private static final long serialVersionUID = 1L;

	@DatabaseField(generatedId = true)
	private Integer id;
	@DatabaseField(canBeNull = false)
	private String name;
	@DatabaseField
	private String description;
	@DatabaseField
	private String url;
	@DatabaseField
	private String dist;
	@DatabaseField
	private String component;
	@DatabaseField
	private String arch;
	@DatabaseField(canBeNull = false)
	private String type;
	@DatabaseField
	private String pubkey;
	
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

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getDist() {
		return dist;
	}

	public void setDist(String dist) {
		this.dist = dist;
	}

	public String getComponent() {
		return component;
	}

	public void setComponent(String component) {
		this.component = component;
	}

	public String getArch() {
		return arch;
	}

	public void setArch(String arch) {
		this.arch = arch;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getPubkey() {
		return pubkey;
	}

	public void setPubkey(String pubkey) {
		this.pubkey = pubkey;
	}
}
