package com.fms.pfc.domain.model;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.IdClass;
import java.io.Serializable;

@Entity
@Table(name = "grp")
@IdClass(GroupPk.class)
public class Group implements Serializable {
		
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String orgName;
	private String orgId;
	private String groupId;
	private String groupName;
	private String description;
	private String systemUseFlag;
	private String creatorId;
	private Date createdDateTime;
	private String modifierId;
	private Date modifiedDateTime;
	
	public Group() {
		
	}
	
	public Group(String orgName, String orgId, String groupId, String groupName, String description, String systemUseFlag, String creatorId, Date createdDateTime, String modifierId, Date modifiedDateTime) {
		this.setOrgName(orgName);
		this.setOrgId(orgId);
		this.setGroupId(groupId);
		this.setGroupName(groupName);
		this.setDescription(description);
		this.setSystemUseFlag(systemUseFlag);
		this.setCreatorId(creatorId);
		this.setCreatedDateTime(createdDateTime);
		this.setModifierId(modifierId);
		this.setModifiedDateTime(modifiedDateTime);
	}

	@Column(name = "org_name")
	public String getOrgName() {
		return orgName;
	}
	public void setOrgName(String orgName) {
		this.orgName = orgName;
	}

	@Id
	@Column(name = "org_id")
	public String getOrgId() {
		return orgId;
	}
	public void setOrgId(String orgId) {
		this.orgId = orgId;
	}

	@Id
	@Column(name = "group_id")
	public String getGroupId() {
		return groupId;
	}
	public void setGroupId(String groupId) {
		this.groupId = groupId;
	}

	@Column(name = "group_name")
	public String getGroupName() {
		return groupName;
	}
	public void setGroupName(String groupName) {
		this.groupName = groupName;
	}

	@Column(name = "description")
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
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

}
