package com.fms.pfc.domain.dto.main;

import java.util.Date;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class FileTypeSzDto {

	private Integer pkFtypeId;
	private Integer fkHplId;
	private String fkHplName;
	private String fileType;
	private Integer minSize;
	private Integer maxSize;
	private String creatorId;
	private Date createdDatetime;
	private String modifierId;
	private Date modifiedDatetime;
	private String indicator;
	private int rowNo;
	private String hpl;
}
