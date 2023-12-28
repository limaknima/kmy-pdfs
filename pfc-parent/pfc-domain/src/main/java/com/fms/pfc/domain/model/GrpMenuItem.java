package com.fms.pfc.domain.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
@EqualsAndHashCode
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
	
	@Column(name = "roles")
	private String roles;
	
}
