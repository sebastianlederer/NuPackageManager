package de.dassit.nupama;

import java.io.Serializable;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable(tableName = "taskstatus")
public class TaskStatus implements Cloneable, Serializable {
	
	private static final long serialVersionUID = 1L;

	@DatabaseField(id=true)
	private Integer id;
	@DatabaseField
	private String task;
	@DatabaseField
	private String progress;
	@DatabaseField
	private String message;

	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public String getTask() {
		return task;
	}
	public void setTask(String task) {
		this.task = task;
	}
	public String getProgress() {
		return progress;
	}
	public void setProgress(String progress) {
		this.progress = progress;
	}
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}
}