package com.fms.pfc.domain.model;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Embeddable;

@Embeddable
public class AlertxRecipientPk implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;	
	
	private int alertId;	
	private String userId;

	public AlertxRecipientPk() {
		super();
	}

	public AlertxRecipientPk(int alertId, String userId) {
		super();
		this.alertId = alertId;
		this.userId = userId;
	}
	
	@Column(name = "alert_id")
	public int getAlertId() {
		return alertId;
	}

	public void setAlertId(int alertId) {
		this.alertId = alertId;
	}

	@Column(name = "to_user_id")
	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + alertId;
		result = prime * result + ((userId == null) ? 0 : userId.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		AlertxRecipientPk other = (AlertxRecipientPk) obj;
		if (alertId != other.alertId)
			return false;
		if (userId == null) {
			if (other.userId != null)
				return false;
		} else if (!userId.equals(other.userId))
			return false;
		return true;
	}
	
}
