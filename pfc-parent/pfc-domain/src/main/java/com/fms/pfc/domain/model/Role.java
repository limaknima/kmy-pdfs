package com.fms.pfc.domain.model;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "role")
public class Role {

	private String roleId;
	private String roleName;
	private String roleDesc;
	private String systemUseFlag;
	private String creatorId;
	private Date createdDateTime;
	private String modifierId;
	private Date modifiedDateTime;
	private String orgId;

	public Role() {

	}

	public Role(String roleId, String roleName, String roleDesc, String systemUseFlag, String creatorId,
			Date createdDateTime, String modifierId, Date modifiedDateTime, String orgId) {
		this.setRoleId(roleId);
		this.setRoleName(roleName);
		this.setRoleDesc(roleDesc);
		this.setSystemUseFlag(systemUseFlag);
		this.setCreatorId(creatorId);
		this.setCreatedDateTime(createdDateTime);
		this.setModifierId(modifierId);
		this.setModifiedDateTime(modifiedDateTime);
		this.setOrgId(orgId);
	}

	@Id
	@Column(name = "role_id")
	public String getRoleId() {
		return roleId;
	}

	public void setRoleId(String roleId) {
		this.roleId = roleId;
	}

	@Column(name = "role_name")
	public String getRoleName() {
		return roleName;
	}

	public void setRoleName(String roleName) {
		this.roleName = roleName;
	}

	@Column(name = "role_desc")
	public String getRoleDesc() {
		return roleDesc;
	}

	public void setRoleDesc(String roleDesc) {
		this.roleDesc = roleDesc;
	}

	@Column(name = "system_use_flag")
	public String getSystemUseFlag() {
		return systemUseFlag;
	}

	public void setSystemUseFlag(String systemUseFlag) {
		this.systemUseFlag = systemUseFlag;
	}

	@Column(name = "creator_id")
	public String getCreatorId() {
		return creatorId;
	}

	public void setCreatorId(String creatorId) {
		this.creatorId = creatorId;
	}

	@Column(name = "created_datetime ")
	public Date getCreatedDateTime() {
		return createdDateTime;
	}

	public void setCreatedDateTime(Date createdDateTime) {
		this.createdDateTime = createdDateTime;
	}

	@Column(name = "modifier_id")
	public String getModifierId() {
		return modifierId;
	}

	public void setModifierId(String modifierId) {
		this.modifierId = modifierId;
	}

	@Column(name = "modified_datetime")
	public Date getModifiedDateTime() {
		return modifiedDateTime;
	}

	public void setModifiedDateTime(Date modifiedDateTime) {
		this.modifiedDateTime = modifiedDateTime;
	}

	@Column(name = "org_id")
	public String getOrgId() {
		return orgId;
	}

	public void setOrgId(String orgId) {
		this.orgId = orgId;
	}

}
