package com.fms.pfc.domain.dto.main;

import java.util.Date;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RawMaterialDto {

	private int rawMatlId;
	private String rawMatlName;
	private String tsNo;
	private String remarks;
	private String remarksUserId;
	private int recordStatus;
	private String insNo;
	private String eNo;
	private String femaNo;
	private String jecfaNo;
	private String casNo;
	private String gmoStatus;
	private String flavorStatus;
	private String phoFlag;
	private String creatorId;
	private Date createdDate;
	private String modifierId;
	private Date modifiedDate;
	private String checkerId;
	private Date submittedDate;
	private Integer flavStatusId;
}
