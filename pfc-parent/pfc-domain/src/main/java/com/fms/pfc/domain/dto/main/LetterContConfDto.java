package com.fms.pfc.domain.dto.main;

import java.util.Date;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class LetterContConfDto {
	private Integer ltConfId;
	private Integer ltId;
	private Integer recordTypeId;
	private String sec1Desc;
	private String sec2Desc;
	private String sec3Desc;
	private String sec4Desc;
	private String sec5Desc;
	private String countryId;
	private String creatorId;
	private Date createdDate;
	private String modifierId;
	private Date modifiedDatetime;
	private String prodNameList;
	private String countryName;
	private String letterTypeName;
	private String prodIdList;
}
