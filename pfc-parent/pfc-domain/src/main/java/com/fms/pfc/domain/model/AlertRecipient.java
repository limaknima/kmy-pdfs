package com.fms.pfc.domain.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "alert_recipient")
public class AlertRecipient {

	@Column(name = "alert_id")
	private int alertId;
	
	@Id
	@Column(name = "to_user_id")
	private String userId;
	
	@Column(name = "read_recipient")
	private int readRecipient;

	public int getAlertId() {
		return alertId;
	}

	public void setAlertId(int alertId) {
		this.alertId = alertId;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public int getReadRecipient() {
		return readRecipient;
	}

	public void setReadRecipient(int readRecipient) {
		this.readRecipient = readRecipient;
	}

}
