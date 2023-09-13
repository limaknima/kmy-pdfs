package com.fms.pfc.domain.dto.main;

import java.util.Date;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RelPathDto2 {

	private Integer pkRelPathId;
	private Integer fkCatgId;
	private String hModel;
	private String year;
	private String mth;
	private String day;
	private String prodLn;
	private String seq;
	private String filePath;
	private String prodFileFormat;
	private String creatorId;
	private Date createdDatetime;
	private String modifierId;
	private Date modifiedDatetime;
	private String indicator;
	private int rowNo;
	private Integer procType;
	
	@Override
	public String toString() {
		return "hModel=" + hModel 
		+ ";year=" + year
		+ ";mth=" + mth
		+ ";day=" + day
		+ ";prodLn=" + prodLn
		+ ";seq=" + seq
		+ ";filePath=" + filePath
		+ ";prodFileFormat=" + prodFileFormat
		+ ";procType=" + procType
		;
	}
}
