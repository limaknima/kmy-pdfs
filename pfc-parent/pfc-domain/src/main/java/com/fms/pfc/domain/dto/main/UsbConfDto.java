package com.fms.pfc.domain.dto.main;

import java.util.Date;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UsbConfDto {

	private Integer pkUconfId;
	private Integer fkHplId;
	private String fkHplName;
	private String serialNo;
	private String name;
	private String prodLn;
	private String userListStr;
	private String creatorId;
	private Date createdDatetime;
	private String modifierId;
	private Date modifiedDatetime;
	private String hpl;
}
