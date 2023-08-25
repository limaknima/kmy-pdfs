package com.fms.pfc.domain.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "grp_menu_item")
public class GrpMenuItem {
	
	@Column(name = "grp_id")
	private String groupId;
	
	@Column(name = "menu_id")
	private int menuId;
	
	@Column(name = "parent_menu_item_id")
	private int parentMenuId;
	
	@Id
	@Column(name = "menu_item_id")
	private int menuItemId;

	public String getGroupId() {
		return groupId;
	}

	public void setGroupId(String groupId) {
		this.groupId = groupId;
	}

	public int getMenuId() {
		return menuId;
	}

	public void setMenuId(int menuId) {
		this.menuId = menuId;
	}

	public int getParentMenuId() {
		return parentMenuId;
	}

	public void setParentMenuId(int parentMenuId) {
		this.parentMenuId = parentMenuId;
	}

	public int getMenuItemId() {
		return menuItemId;
	}

	public void setMenuItemId(int menuItemId) {
		this.menuItemId = menuItemId;
	}

	
}
