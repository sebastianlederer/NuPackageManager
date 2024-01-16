package de.dassit.nupama;

import java.io.Serializable;
import java.sql.SQLException;
import java.util.Date;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable(tableName = "repository")
public class Repository implements Cloneable, Serializable {
	private static final long serialVersionUID = 1L;

	@DatabaseField(generatedId = true)
	private Integer id;
	@DatabaseField
	private String name;
	@DatabaseField(canBeNull = true)
	private String description;
	@DatabaseField(columnName = "pred", foreign = true)
	private Repository pred;
	@DatabaseField(columnName = "origin", foreign = true)
	private Repository origin;
	@DatabaseField(columnName = "upstream", foreign = true)
	private UpstreamRepo upstream;
	@DatabaseField(columnName = "upd_count", canBeNull = false)
	private Integer updateCount;
	@DatabaseField(columnName = "upd_count_origin", canBeNull = false)
	private Integer updateCountOrigin;
	@DatabaseField(columnName = "approvalreqd", canBeNull = false)
	private Boolean approvalRequired;
	@DatabaseField(columnName = "approved", canBeNull = false)
	private Boolean approved;
	@DatabaseField(canBeNull = false)
	private Boolean atomic;
	@DatabaseField
	private String owner;
	@DatabaseField(columnName = "signingmode", canBeNull = false)
	private Boolean signingMode;
	@DatabaseField
	private String schedule;
	@DatabaseField(columnName = "last_update")
	private Date lastUpdate;
	@DatabaseField
	private String action;
	@DatabaseField
	private String result;
	// not stored in the database:
	private int indent;

	private Dao<Repository, Integer> dao;
	private Dao<UpstreamRepo, Integer> upstreamRepoDao;

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

	public Repository getPred() throws SQLException {
		if (pred != null)
			dao.refresh(pred);
		return pred;
	}

	public void setPred(Repository pred) {
		this.pred = pred;
	}

	public Repository getOrigin() throws SQLException {
		if (origin != null)
			dao.refresh(origin);
		return origin;
	}

	public void setOrigin(Repository origin) {
		this.origin = origin;
	}

	public UpstreamRepo getUpstream() throws SQLException {
		if (upstream != null)
			upstreamRepoDao.refresh(upstream);
		return upstream;
	}

	public void setUpstream(UpstreamRepo upstream) {
		this.upstream = upstream;
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

	public Boolean getApprovalRequired() {
		return approvalRequired;
	}

	public void setApprovalRequired(Boolean approvalRequired) {
		this.approvalRequired = approvalRequired;
	}

	public Boolean getApproved() {
		return approved;
	}

	public void setApproved(Boolean approved) {
		this.approved = approved;
	}

	public Boolean getAtomic() {
		return atomic;
	}

	public void setAtomic(Boolean atomic) {
		this.atomic = atomic;
	}

	public String getOwner() {
		return owner;
	}

	public void setOwner(String owner) {
		this.owner = owner;
	}

	public Boolean getSigningMode() {
		return signingMode;
	}

	public void setSigningMode(Boolean signingMode) {
		this.signingMode = signingMode;
	}

	public String getSchedule() {
		return schedule;
	}

	public void setSchedule(String schedule) {
		this.schedule = schedule;
	}

	public Date getLastUpdate() {
		return lastUpdate;
	}

	public void setLastUpdate(Date lastUpdate) {
		this.lastUpdate = lastUpdate;
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

	public int getIndent() {
		return indent;
	}

	public void setIndent(int indent) {
		this.indent = indent;
	}

	public void setDao(Dao<Repository, Integer> dao) {
		this.dao = dao;
	}

	public void setUpstreamRepoDao(Dao<UpstreamRepo, Integer> upstreamRepoDao) {
		this.upstreamRepoDao = upstreamRepoDao;
	}
}
