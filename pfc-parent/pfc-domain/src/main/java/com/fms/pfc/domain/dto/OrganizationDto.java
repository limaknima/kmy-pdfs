package com.fms.pfc.domain.dto;

import java.util.Date;

import lombok.Data;

@Data
public class OrganizationDto {

	private String orgaID;
	private String orgaName;
	private String orgaType;
	private String email;
	private String address;
	private String city;
	private String postcode;
	private String state;
	private String country;
	private String tel;
	private String fax;
	private byte[] logo;
	private String url;
	private Date effectStart;
	private String effectStartStr;
	private Date effectEnd;
	private String effectEndStr;
	private String creator;
	private Date createTime;
	private String modifier;
	private Date modifyTime;

}
