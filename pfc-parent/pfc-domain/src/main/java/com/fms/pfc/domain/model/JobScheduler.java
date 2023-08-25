package com.fms.pfc.domain.model;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "scheduler_activity")
public class JobScheduler {
	
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "job_id")
	private Integer jobId;
	
	@Column(name = "job_name")
	private String jobName;
	
	@Column(name = "job_group")
	private String jobGroup;
	
	@Column(name = "job_description")
	private String description;
	
	@Column(name = "job_status")
	private String jobStatus;
	
	@Column(name = "start_date")
	private Date startDate;
	
	@Column(name = "end_date")
	private Date endDate;
	
	@Column(name = "perform_task")
	private String performTask;
	
	@Column(name = "creator_id")
	private String createdUser;
	
	@Column(name = "created_datetime")
	private Date createdTime;
	
	@Column(name = "modifier_id")
	private String updatedUser;
	
	@Column(name = "modified_datetime")
	private Date updatedTime;
	
	@Column(name = "task_opt")
	private String taskOpt;
	
	@Column(name = "task_opt_det")
	private String taskOptDet;
	
	public JobScheduler() {

	}
	
	public JobScheduler(Integer jobId, String jobName, String jobGroup, String description, String jobStatus,
			Date startDate, Date endDate, String performTask, String createdUser,
			Date createdTime, String updatedUser, Date updatedTime) {
		this.jobId = jobId;
		this.jobName = jobName;
		this.jobGroup = jobGroup;
		this.description = description;
		this.jobStatus = jobStatus;
		this.startDate = startDate;
		this.endDate = endDate;
		this.performTask = performTask;
		this.createdUser = createdUser;
		this.createdTime = createdTime;
		this.updatedUser = updatedUser;
		this.updatedTime = updatedTime;
	}

	public Integer getJobId() {
		return jobId;
	}

	public void setJobId(Integer jobId) {
		this.jobId = jobId;
	}

	public String getJobName() {
		return jobName;
	}

	public void setJobName(String jobName) {
		this.jobName = jobName;
	}

	public String getJobGroup() {
		return jobGroup;
	}

	public void setJobGroup(String jobGroup) {
		this.jobGroup = jobGroup;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getJobStatus() {
		return jobStatus;
	}

	public void setJobStatus(String jobStatus) {
		this.jobStatus = jobStatus;
	}

	public Date getStartDate() {
		return startDate;
	}

	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}

	public Date getEndDate() {
		return endDate;
	}

	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}

	public String getPerformTask() {
		return performTask;
	}

	public void setPerformTask(String performTask) {
		this.performTask = performTask;
	}

	public String getCreatedUser() {
		return createdUser;
	}

	public void setCreatedUser(String createdUser) {
		this.createdUser = createdUser;
	}

	public Date getCreatedTime() {
		return createdTime;
	}

	public void setCreatedTime(Date createdTime) {
		this.createdTime = createdTime;
	}

	public String getUpdatedUser() {
		return updatedUser;
	}

	public void setUpdatedUser(String updatedUser) {
		this.updatedUser = updatedUser;
	}

	public Date getUpdatedTime() {
		return updatedTime;
	}

	public void setUpdatedTime(Date updatedTime) {
		this.updatedTime = updatedTime;
	}

	public String getTaskOpt() {
		return taskOpt;
	}

	public void setTaskOpt(String taskOpt) {
		this.taskOpt = taskOpt;
	}

	public String getTaskOptDet() {
		return taskOptDet;
	}

	public void setTaskOptDet(String taskOptDet) {
		this.taskOptDet = taskOptDet;
	}
}
