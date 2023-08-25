package com.fms.pfc.common;

import java.io.Serializable;

public enum RecordStatusEnum implements Serializable {

	DRAFT(CommonConstants.RECORD_STATUS_DRAFT),
	PEND_AUTH(CommonConstants.RECORD_STATUS_PENDING_AUTH),
	SUBMITTED(CommonConstants.RECORD_STATUS_SUBMITTED),
	REJECTED(CommonConstants.RECORD_STATUS_REJECTED),
	REWORK(CommonConstants.RECORD_STATUS_REWORK),
	REWORK_PEND_AUTH(CommonConstants.RECORD_STATUS_REWORK_PENDING_AUTH),
	PEND_CONFIRM(CommonConstants.RECORD_STATUS_PENDING_CONFIRM),
	CHG_PEND_AUTH(CommonConstants.RECORD_STATUS_CHG_PENDING_AUTH),
	CHG_PEND_CONFIRM(CommonConstants.RECORD_STATUS_CHG_PENDING_CONFIRM),
	CHG_REWORK(CommonConstants.RECORD_STATUS_CHG_REWORK),
	PEND_DEACTIVATE(CommonConstants.RECORD_STATUS_PEND_DEACTIVATE),
	DEACTIVATED(CommonConstants.RECORD_STATUS_DEACTIVATED),
	PEND_ACTIVATE(CommonConstants.RECORD_STATUS_PEND_ACTIVATE)
	;
	
	private int intValue;
	private String strKey = "vrStatus_";
	
	private RecordStatusEnum (int intValue) {
		this.intValue = intValue;
	}
	
	public int intValue() {
		return intValue;
	}
	
	public String strKey() {
		return strKey + intValue;
	}
	
}
