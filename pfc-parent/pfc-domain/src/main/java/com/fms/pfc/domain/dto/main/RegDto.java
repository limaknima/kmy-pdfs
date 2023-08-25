package com.fms.pfc.domain.dto.main;

import java.util.Date;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RegDto {
	private String rawMatlName;	
	private String fileName;		
	private List<RegDocDto> regDocList;	
	private String regDocCatId;	
	private String regDocCatGrpId;		
	private int regId;	
	private String countryName;	
	private int rawMatlId;	
	private String countryId;	
	private String regName;	
	private String refUrl1;	
	private String refUrl2;	
	private String refUrl3;	
	private String refUrl4;	
	private String refUrl5;	
	private String remarks;	
	private String creatorId;	
	private Date createdDateTime;		
	private String modifierId;		
	private Date modifiedDateTime;
}
