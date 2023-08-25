package com.fms.pfc.domain.model;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

@Entity
@Table(name = "alert")
public class Alert {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "alert_id")
	private int alertId;
	
	@Column(name = "subject")
	private String subject;
	
	@Column(name = "alert_desc")
	private String alertDesc;
	
	@Column(name = "from_user_id")
	private String frUserId;
	
	@Column(name = "created_datetime")
	private Date createdDate;
	
	@Column(name = "record_ref")
	private String recordRef;
	
	@Column(name = "record_type_id")
	private int recordTypeId;
	
	@Column(name = "read_recipient")
	private int readRecipient;
	
	@Column(name = "record_ref_desc")
	private String recordRefDesc;

	public int getAlertId() {
		return alertId;
	}

	public void setAlertId(int alertId) {
		this.alertId = alertId;
	}

	public String getSubject() {
		return subject;
	}

	public void setSubject(String subject) {
		this.subject = subject;
	}

	public String getAlertDesc() {
		return alertDesc;
	}

	public void setAlertDesc(String alertDesc) {
		this.alertDesc = alertDesc;
	}

	public String getFrUserId() {
		return frUserId;
	}

	public void setFrUserId(String frUserId) {
		this.frUserId = frUserId;
	}

	public Date getCreatedDate() {
		return createdDate;
	}

	public void setCreatedDate(Date createdDate) {
		this.createdDate = createdDate;
	}

	public String getRecordRef() {
		return recordRef;
	}

	public void setRecordRef(String recordRef) {
		this.recordRef = recordRef;
	}

	public int getRecordTypeId() {
		return recordTypeId;
	}

	public void setRecordTypeId(int recordTypeId) {
		this.recordTypeId = recordTypeId;
	}

	public int getReadRecipient() {
		return readRecipient;
	}

	public void setReadRecipient(int readRecipient) {
		this.readRecipient = readRecipient;
	}

	public String getRecordRefDesc() {
		return recordRefDesc;
	}

	public void setRecordRefDesc(String recordRefDesc) {
		this.recordRefDesc = recordRefDesc;
	}

}
