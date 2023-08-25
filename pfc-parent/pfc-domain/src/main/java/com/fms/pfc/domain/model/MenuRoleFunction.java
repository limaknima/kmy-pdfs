package com.fms.pfc.domain.model;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
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
@Table(name = "MENU_ITEM_ROLE_FUNCTION")
public class MenuRoleFunction implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 4628725345907571490L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "MRF_ID")
	private Integer pkMrfId;

	@Column(name = "MENU_ITEM_ID")
	private Integer menuItemId;

	@Column(name = "ROLE_ID")
	private String roleId;

	@Column(name = "RECORD_TYPE_ID")
	private Integer recordTypeId;

	@Column(name = "FUNCTION_TYPE")
	private Integer functionType;

	@Column(name = "CREATOR_ID")
	private String creatorId;

	@Column(name = "CREATED_DATETIME")
	private Date createdDatetime;

	@Column(name = "MODIFIER_ID")
	private String modifierId;

	@Column(name = "MODIFIED_DATETIME")
	private Date modifiedDatetime;

}
