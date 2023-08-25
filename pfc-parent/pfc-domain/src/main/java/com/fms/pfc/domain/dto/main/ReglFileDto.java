package com.fms.pfc.domain.dto.main;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ReglFileDto {
	private Integer reglFileId;
	private Integer docCatId;
	private Integer docCatGrpId;
	private String fileName;
	private String filePath;
	private String countryId;
	private String docType;
	private byte[] contentObject;
	private String archivedFlag;
	private Integer version;
	private Long crcValue;
	private String creatorId;
	private Date createdDatetime;
	private String modifierId;
	private Date modifiedDatetime;
	private String docCatName;
	private String docCatGrpName;
	private String countryName;
	

	private String[] rmChoice;
	private MultipartFile fileContent;
	private Integer docCatId2;
	
	private List<ReglInfoDto> regInfoList = new ArrayList<ReglInfoDto>();
	private List<ReglRmDto> regRmList = new ArrayList<ReglRmDto>();
}
