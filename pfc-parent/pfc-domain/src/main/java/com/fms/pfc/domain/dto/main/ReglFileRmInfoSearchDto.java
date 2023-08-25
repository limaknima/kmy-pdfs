package com.fms.pfc.domain.dto.main;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ReglFileRmInfoSearchDto {
	private Integer reglFileId;
	private String fileName;
	private Integer docCatId;
	private String docCatName;
	private Integer docCatGrpId;
	private String docCatGrpName;
	private String countryId;
	private String countryName;
	private Integer rawMatlId;
	private String rmName;
	private String refUrls;
	private String remarks;
	private List<Integer> rmIdList;
}
