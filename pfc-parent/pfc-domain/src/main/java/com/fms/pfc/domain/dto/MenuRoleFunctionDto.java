package com.fms.pfc.domain.dto;

import java.util.Date;

import lombok.Data;

@Data
public class MenuRoleFunctionDto {
	private Integer pkMrfId;
	private Integer menuItemId;
	private String roleId;
	private Integer recordTypeId;
	private Integer functionType;
	private String creatorId;
	private Date createdDatetime;
	private String modifierId;
	private Date modifiedDatetime;
	private String menuName;
	private String roleName;
	private String functionName;
}
