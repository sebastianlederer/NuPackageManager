package de.dassit.nupama;

import java.io.Serializable;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable(tableName = "host")
public class Host implements Cloneable, Serializable {

	private static final long serialVersionUID = 1L;

	@DatabaseField(generatedId = true)
	private Integer id;
	@DatabaseField(canBeNull = false)
	private String name;
	@DatabaseField
	private String description;
	@DatabaseField
	private String owner;
	@DatabaseField(columnName="profile", foreign = true, foreignAutoRefresh = true)
	private Profile profile;
	@DatabaseField(columnName="ip_addr")
	private String ipAddress;
	@DatabaseField(columnName="mac_addr")
	private String macAddress;
	@DatabaseField
	private String options;
	@DatabaseField(columnName="upd_count", canBeNull = false)
	private Integer updateCount;
	@DatabaseField(columnName="upd_count_origin", canBeNull = false)
	private Integer updateCountOrigin;
	@DatabaseField(columnName="reboot_required", canBeNull = false)
	private Boolean rebootRequired;
	@DatabaseField
	private String action;
	@DatabaseField
	private String result;
        @DatabaseField(columnName="needsrefresh", canBeNull = false)
        private Boolean needsRefresh;
        @DatabaseField(columnName="action_args")
        private String actionArgs;

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
	public Profile getProfile() {
		return profile;
	}
	public void setProfile(Profile profile) {
		this.profile = profile;
	}
	public String getIpAddress() {
		return ipAddress;
	}
	public void setIpAddress(String ipAddress) {
		this.ipAddress = ipAddress;
	}
	public String getMacAddress() {
		return macAddress;
	}
	public void setMacAddress(String macAddress) {
		this.macAddress = macAddress;
	}
	public String getOptions() {
		return options;
	}
	public void setOptions(String options) {
		this.options = options;
	}
	public Integer getUpdateCount() {
		return updateCount;
	}
	public void setUpdateCount(Integer updateCount) {
		this.updateCount = updateCount;
	}
	public Integer getUpdateCountOrigin() {
		return updateCountOrigin;
	}
	public void setUpdateCountOrigin(Integer updateCountOrigin) {
		this.updateCountOrigin = updateCountOrigin;
	}
	public Boolean getRebootRequired() {
		return rebootRequired;
	}
	public void setRebootRequired(Boolean rebootRequired) {
		this.rebootRequired = rebootRequired;
	}
	public String getAction() {
		return action;
	}
	public void setAction(String action) {
		this.action = action;
	}
	public String getResult() {
		return result;
	}
	public void setResult(String result) {
		this.result = result;
	}
	public Boolean getNeedsRefresh() {
		return needsRefresh;
	}
	public void setNeedsRefresh(Boolean needsRefresh) {
		this.needsRefresh = needsRefresh;
	}
	public String getActionArgs() {
		return actionArgs;
	}
	public void setActionArgs(String actionArgs) {
		this.actionArgs = actionArgs;
	}
}
