package com.fms.pfc.domain.dto.main;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RegDocDto {
	private String regDocId;
	private String regId;
	private String regDocCatId;
	private String regDocCatGrpId;
	private String fileName;
	private String filePath;
	private String docType;
	private byte[] contentObject;
	private String archivedFlag;
	private String version;
	private String crcValue;
	private String creatorId;
	private String createdDatetime;
	private String modifierId;
	private String modifiedDatetime;
	
	private int rowNo;
	private String regDocCatName;
	private String regDocCatGrpName;
	private String indicator;
}
