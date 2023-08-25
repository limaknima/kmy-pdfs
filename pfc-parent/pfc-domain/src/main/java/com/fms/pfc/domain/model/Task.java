package com.fms.pfc.domain.model;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.springframework.format.annotation.DateTimeFormat;

@Entity
@Table(name = "wf_task")
public class Task {

	@Column(name = "record_ref", unique = true)
	private String referenceNum;

	@Column(name = "record_type_id")
	private int recordTypeId;

	@Id
	@Column(name = "start_datetime")
	@DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	private Date dateAssigned;

	@Column(name = "end_datetime")
	@DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	private Date endedDate;

	@Column(name = "due_datetime")
	@DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	private Date dueDate;

	@Column(name = "org_id")
	private String orgId;

	@Column(name = "actor_id")
	private String actorId;

	@Column(name = "pooled_actor_id")
	private String assignedTo;

	@Column(name = "task_desc")
	private String subject;

	@Column(name = "task_action")
	private Integer taskStatus;

	@Column(name = "task_remarks")
	private String taskRemarks;

	@Column(name = "creator_id")
	private String creatorId;

	@Transient
	private String assignedFormattedDate;

	@Transient
	private String takenOnFormattedDate;
	
	@Transient
	private int diffDate;
	
	@Transient
	private boolean overdue = false;

	public String getReferenceNum() {
		return referenceNum;
	}

	public void setReferenceNum(String referenceNum) {
		this.referenceNum = referenceNum;
	}

	public int getRecordTypeId() {
		return recordTypeId;
	}

	public void setRecordTypeId(int recordTypeId) {
		this.recordTypeId = recordTypeId;
	}

	public Date getDateAssigned() {
		return dateAssigned;
	}

	public void setDateAssigned(Date dateAssigned) {
		this.dateAssigned = dateAssigned;
	}

	public Date getEndedDate() {
		return endedDate;
	}

	public void setEndedDate(Date endedDate) {
		this.endedDate = endedDate;
	}

	public Date getDueDate() {
		return dueDate;
	}

	public void setDueDate(Date dueDate) {
		this.dueDate = dueDate;
	}

	public String getOrgId() {
		return orgId;
	}

	public void setOrgId(String orgId) {
		this.orgId = orgId;
	}

	public String getActorId() {
		return actorId;
	}

	public void setActorId(String actorId) {
		this.actorId = actorId;
	}

	public String getAssignedTo() {
		return assignedTo;
	}

	public void setAssignedTo(String assignedTo) {
		this.assignedTo = assignedTo;
	}

	public String getSubject() {
		return subject;
	}

	public void setSubject(String subject) {
		this.subject = subject;
	}

	public Integer getTaskStatus() {
		return taskStatus;
	}

	public void setTaskStatus(Integer taskStatus) {
		this.taskStatus = taskStatus;
	}

	public String getTaskRemarks() {
		return taskRemarks;
	}

	public void setTaskRemarks(String taskRemarks) {
		this.taskRemarks = taskRemarks;
	}

	public String getAssignedFormattedDate() {
		return assignedFormattedDate;
	}

	public void setAssignedFormattedDate(String assignedFormattedDate) {
		this.assignedFormattedDate = assignedFormattedDate;
	}

	public String getTakenOnFormattedDate() {
		return takenOnFormattedDate;
	}

	public void setTakenOnFormattedDate(String takenOnFormattedDate) {
		this.takenOnFormattedDate = takenOnFormattedDate;
	}

	public String getCreatorId() {
		return creatorId;
	}

	public void setCreatorId(String creatorId) {
		this.creatorId = creatorId;
	}

	public int getDiffDate() {
		return diffDate;
	}

	public void setDiffDate(int diffDate) {
		this.diffDate = diffDate;
	}

	public boolean isOverdue() {
		return overdue;
	}

	public void setOverdue(boolean overdue) {
		this.overdue = overdue;
	}

}
