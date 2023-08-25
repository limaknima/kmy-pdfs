package com.fms.pfc.domain.dto.main;

import java.util.Date;

import lombok.Data;

@Data
public class RegulationCatDto {
	private int regDocCatId;
	private String catName;
	private String catDesc;
	private String creatorId;
	private Date createdDate;
	private String modifierId;
	private Date modifiedDatetime;
	private String countryList;
	private String countryListDesc;
}
