package com.fms.pfc.domain.dto;

import java.util.Date;

import lombok.Data;

@Data
public class CountryDto {

	private String countryId;
	private String countryName;
	private String creator;
	private Date createTime;
	private String modifier;
	private Date modifyTime;
	private String evalFlag;
	private String originFlag;
}
