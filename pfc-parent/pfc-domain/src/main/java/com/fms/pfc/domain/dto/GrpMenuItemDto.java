package com.fms.pfc.domain.dto;

import lombok.Data;

@Data
public class GrpMenuItemDto {
	private String groupId;
	private int menuId;
	private int parentMenuId;
	private int menuItemId;
	private String menuItemNameTemp;
}
