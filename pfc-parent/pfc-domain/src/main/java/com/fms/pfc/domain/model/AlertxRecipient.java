package com.fms.pfc.domain.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "alert_recipient")
public class AlertxRecipient {

	private AlertxRecipientPk alertxRecipientPk;
	private int readRecipient;

	public AlertxRecipient() {
		super();
	}

	public AlertxRecipient(AlertxRecipientPk alertxRecipientPk, int readRecipient) {
		super();
		this.alertxRecipientPk = alertxRecipientPk;
		this.readRecipient = readRecipient;
	}

	@Column(name = "read_recipient")
	public int getReadRecipient() {
		return readRecipient;
	}

	public void setReadRecipient(int readRecipient) {
		this.readRecipient = readRecipient;
	}

	@Id
	public AlertxRecipientPk getAlertxRecipientPk() {
		return alertxRecipientPk;
	}

	public void setAlertxRecipientPk(AlertxRecipientPk alertxRecipientPk) {
		this.alertxRecipientPk = alertxRecipientPk;
	}

}
