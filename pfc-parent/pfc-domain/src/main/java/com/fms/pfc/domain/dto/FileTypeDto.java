package com.fms.pfc.domain.dto;

import java.util.Date;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class FileTypeDto {

	private String pkFtId;
	private String ftName;
	private String creatorId;
	private Date createdDatetime;
	private String modifierId;
	private Date modifiedDatetime;
}
