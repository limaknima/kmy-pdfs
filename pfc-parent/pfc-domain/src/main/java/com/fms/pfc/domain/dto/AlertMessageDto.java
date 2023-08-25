package com.fms.pfc.domain.dto;

import java.util.Date;

import lombok.Data;

@Data
public class AlertMessageDto {

	private String alertType;
	private String subject;
	private String description;
	private String createdBy;
	private Date createdDate;
	private String modifiedBy;
	private Date lastModifiedDate;
}
