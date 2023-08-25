package com.fms.pfc.domain.dto.main;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class FsRmUpdResultDto {
	private Integer updRstId;
	private Integer updId;
	private Integer prId;
	private Integer prFsId;
	private Integer newPrFsId;
	private String prName;
	private String flavStatusName;
	private String newFlavStatusName;
}
