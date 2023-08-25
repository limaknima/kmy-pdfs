package com.fms.pfc.domain.dto.main;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ReglRmDto {
	private Integer reglRmId;
	private Integer reglFileId;
	private Integer rawMatlId;
	private String rmName;
}
