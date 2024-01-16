package de.dassit.nupama;

import java.io.Serializable;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable(tableName = "profile")
public class Profile implements Cloneable, Serializable {

	private static final long serialVersionUID = 1L;

	@DatabaseField(generatedId = true)
	private Integer id;
	@DatabaseField
	private String name;
	@DatabaseField(canBeNull = true)
	private String description;
	@DatabaseField(canBeNull = true)
	private String owner;
	@DatabaseField(columnName = "config_opts", canBeNull = true)
	private String configOpts;

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
	public String getOwner() {
		return owner;
	}
	public void setOwner(String owner) {
		this.owner = owner;
	}
	public String getConfigOpts() {
		return configOpts;
	}
	public void setConfigOpts(String configOpts) {
		this.configOpts = configOpts;
	}
}