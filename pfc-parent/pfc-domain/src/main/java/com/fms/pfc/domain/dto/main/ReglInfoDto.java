package com.fms.pfc.domain.dto.main;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ReglInfoDto {
	private Integer reglInfoId;
	private Integer reglFileId;
	private String regName;
	private String refUrl1;
	private String refUrl2;
	private String refUrl3;
	private String refUrl4;
	private String refUrl5;
	private String remarks;
	private Integer rmId;
	private String rmName;
	private String indicator;
	private int rowNo;
}
