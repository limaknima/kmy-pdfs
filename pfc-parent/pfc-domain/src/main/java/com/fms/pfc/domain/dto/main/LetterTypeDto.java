package com.fms.pfc.domain.dto.main;

import java.util.Date;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
@Data
@AllArgsConstructor
@NoArgsConstructor
public class LetterTypeDto {
	private Integer ltId;
	private String ltName;
	private String ltDesc;
	private Integer recordTypeId;
	private String creatorId;
	private Date createdDate;
	private String modifierId;
	private Date modifiedDatetime;
}
