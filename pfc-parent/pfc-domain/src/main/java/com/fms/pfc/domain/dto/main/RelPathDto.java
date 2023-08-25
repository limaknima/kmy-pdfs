package com.fms.pfc.domain.dto.main;

import java.util.Date;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RelPathDto {

	private Integer pkRelPathId;
	private Integer fkCatgId;
	private String filePath;
	private String prodFileFormat;
	private String creatorId;
	private Date createdDatetime;
	private String modifierId;
	private Date modifiedDatetime;
	private String indicator;
	private int rowNo;
}
