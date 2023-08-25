package com.fms.pfc.domain.dto.main;

import java.util.Date;
import java.util.Objects;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class FoldCatgConfDto {

	private Integer pkCatgId;
	private Integer fkHplId;
	private String fkHplName;
	private Integer fkHplModelId;
	private String fkHplModelName;
	private String year;
	private String mth;
	private String day;
	private String prodLn;
	private String seq;
	private String prodFileFormat;
	private String creatorId;
	private Date createdDatetime;
	private String modifierId;
	private Date modifiedDatetime;
	private String hpl;
	private String hModel;
	
	@Override
	public String toString() {
		return "hpl=" + (Objects.nonNull(fkHplId) ? fkHplId.toString() : null) + ",model="
				+ (Objects.nonNull(fkHplModelId) ? fkHplModelId.toString() : null) + ";year=" + year + ";mth=" + mth
				+ ";day=" + day + ";prodLn=" + prodLn + ";seq=" + seq + ";prodFileFormat=" + prodFileFormat;
	}
}
