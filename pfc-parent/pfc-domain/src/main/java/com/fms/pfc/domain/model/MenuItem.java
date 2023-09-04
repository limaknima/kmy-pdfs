package com.fms.pfc.domain.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

@Entity
@Table(name = "menu_item")
public class MenuItem {
	
	@Column(name = "menu_id")
	private int menuId;
	
	@Column(name = "parent_menu_item_id")
	private int parentMenuId;
	
	@Id
	@Column(name = "menu_item_id")
	private int menuItemId;
	
	@Column(name = "menu_item_name")
	private String menuItemName;
	
	@Column(name = "seq_no")
	private int seqNo;
	
	@Column(name = "url")
	private String url;
	
	@Transient
	private String menuItemNameTemp;

	public String getMenuItemNameTemp() {
		return menuItemNameTemp;
	}

	public void setMenuItemNameTemp(String menuItemNameTemp) {
		this.menuItemNameTemp = menuItemNameTemp;
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

	public String getMenuItemName() {
		return menuItemName;
	}

	public void setMenuItemName(String menuItemName) {
		this.menuItemName = menuItemName;
	}

	public int getSeqNo() {
		return seqNo;
	}

	public void setSeqNo(int seqNo) {
		this.seqNo = seqNo;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}
	
}
