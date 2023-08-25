package com.fms.pfc.common;

import java.io.Serializable;

public enum FinalStatusEnum implements Serializable{
	
	NA(CommonConstants.VF_STATUS_NA),
	PERMITTED(CommonConstants.VF_STATUS_PERMITTED),
	NOT_PERMITTED(CommonConstants.VF_STATUS_NOT_PERMITTED),
	NOT_SURE(CommonConstants.VF_STATUS_NOT_SURE),
	YET_COMPELTE(CommonConstants.VF_STATUS_YET_COMPELTE);
	
	private int intValue;
	private String strKey = "vfStatus_";
	
	private FinalStatusEnum (int intValue) {
		this.intValue = intValue;
	}
	
	public int intValue() {
		return intValue;
	}
	
	public String strKey() {
		return strKey + intValue;
	}
}
