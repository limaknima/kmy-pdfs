package com.fms.pfc.domain.dto.main;

import java.util.Date;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class FoldCatgConfDto2 {

	private Integer pkCatgId;
	private String hpl;
	private String prodFileFormat;
	private String creatorId;
	private Date createdDatetime;
	private String modifierId;
	private Date modifiedDatetime;
	
	@Override
	public String toString() {
		return "hpl=" + hpl + ";prodFileFormat=" + prodFileFormat;
	}
}
