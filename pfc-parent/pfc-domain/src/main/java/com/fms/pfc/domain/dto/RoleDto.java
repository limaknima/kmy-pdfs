package com.fms.pfc.domain.dto;

import java.util.Date;

import lombok.Data;

@Data
public class RoleDto {

	private String roleId;
	private String roleName;
	private String roleDesc;
	private String systemUseFlag;
	private String creatorId;
	private Date createdDateTime;
	private String modifierId;
	private Date modifiedDateTime;
	private String orgId;
}
