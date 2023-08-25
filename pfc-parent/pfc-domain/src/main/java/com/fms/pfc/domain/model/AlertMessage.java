package com.fms.pfc.domain.model;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.Id;
import javax.persistence.Table;

import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import org.springframework.format.annotation.DateTimeFormat;

@Entity
@EntityListeners(AuditingEntityListener.class)
@Table(name = "ALERT_DEFINITION")
public class AlertMessage {

	@Id
	@Column(name = "alert_def_id", unique = true, nullable = false, length = 20)
	private String alertType;

	@Column(name = "subject", nullable = false, length = 200)
	private String subject;

	@Column(name = "created_datetime", updatable = false)
	@CreatedDate
	@DateTimeFormat(pattern = "yyyy-MM-dd")
	private Date createdDate;

	@CreatedBy
	@Column(name = "creator_id", updatable = false)
	private String createdBy;

	@Column(name = "modified_datetime")
	@LastModifiedDate
	@DateTimeFormat(pattern = "yyyy-MM-dd")
	private Date lastModifiedDate;

	@Column(name = "modifier_id")
	@LastModifiedBy
	private String modifiedBy;

	@Column(name = "alert_desc", nullable = false, length = 300)
	private String description;

	public AlertMessage() {

	}

	public AlertMessage(String alertType, String subject, Date createdDate, String createdBy, Date lastModifiedDate,
			String modifiedBy, String description) {
		this.alertType = alertType;
		this.subject = subject;
		this.createdDate = createdDate;
		this.createdBy = createdBy;
		this.lastModifiedDate = lastModifiedDate;
		this.modifiedBy = modifiedBy;
		this.description = description;
	}

	public String getAlertType() {
		return alertType;
	}

	public void setAlertType(String alertType) {
		this.alertType = alertType;
	}

	public String getSubject() {
		return subject;
	}

	public void setSubject(String subject) {
		this.subject = subject;
	}

	public Date getCreatedDate() {
		return createdDate;
	}

	public String getCreatedBy() {
		return createdBy;
	}

	public Date getLastModifiedDate() {
		return lastModifiedDate;
	}

	public void setLastModifiedDate(Date lastModifiedDate) {
		this.lastModifiedDate = lastModifiedDate;
	}

	public String getModifiedBy() {
		return modifiedBy;
	}

	public void setModifiedBy(String modifiedBy) {
		this.modifiedBy = modifiedBy;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public void setCreatedDate(Date createdDate) {
		this.createdDate = createdDate;
	}

	public void setCreatedBy(String createdBy) {
		this.createdBy = createdBy;
	}
}
