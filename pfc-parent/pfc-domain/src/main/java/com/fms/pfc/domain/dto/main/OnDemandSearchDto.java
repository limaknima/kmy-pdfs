package com.fms.pfc.domain.dto.main;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OnDemandSearchDto {
	private String lot;
	private String hpl;	
	private String fileName;
	private String filePath;
	private Integer fileSize;
	private String alert;
	private String year;
	private String mth;
	private String day;	
	private Integer fileId;	
}
