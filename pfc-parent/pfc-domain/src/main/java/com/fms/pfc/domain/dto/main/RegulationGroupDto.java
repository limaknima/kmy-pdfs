package com.fms.pfc.domain.dto.main;

import java.util.Date;

import lombok.Data;

@Data
public class RegulationGroupDto {
	private int regDocCatGrpId;
	private String grpName;
	private String grpDesc;
	private String creatorId;
	private Date createdDate;
	private String modifierId;
	private Date modifiedDatetime;

}
