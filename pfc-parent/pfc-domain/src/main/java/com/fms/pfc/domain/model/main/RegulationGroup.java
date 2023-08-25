package com.fms.pfc.domain.model.main;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "REG_DOC_CAT_GRP")
public class RegulationGroup {

	public RegulationGroup() {
		
	}
	
	public RegulationGroup(String grpName, String grpDesc, String creatorId, Date createdDate, String modifierId, Date modifiedDatetime) {
		this.setGrpName(grpName);
		this.setGrpDesc(grpDesc);
		this.setCreatorId(creatorId);
		this.setCreatedDate(createdDate);
		this.setModifierId(modifierId);
		this.setModifiedDatetime(modifiedDatetime);
	}

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "reg_doc_cat_grp_id", nullable = false)
	private int regDocCatGrpId;//regDocCatGrpId

	@Column(name = "reg_doc_cat_grp_name", nullable = false)
	private String grpName;

	@Column(name = "reg_doc_cat_grp_desc")
	private String grpDesc;

	@Column(name = "creator_id")
	private String creatorId;

	@Column(name = "created_datetime")
	private Date createdDate;

	@Column(name = "modifier_id")
	private String modifierId;

	@Column(name = "modified_datetime")
	private Date modifiedDatetime;

	public int getRegDocCatGrpId() {
		return regDocCatGrpId;
	}

	public void setRegDocCatGrpId(int regDocCatGrpId) {
		this.regDocCatGrpId = regDocCatGrpId;
	}

	public String getGrpName() {
		return grpName;
	}

	public void setGrpName(String grpName) {
		this.grpName = grpName;
	}

	public String getGrpDesc() {
		return grpDesc;
	}

	public void setGrpDesc(String grpDesc) {
		this.grpDesc = grpDesc;
	}

	public String getCreatorId() {
		return creatorId;
	}

	public void setCreatorId(String creatorId) {
		this.creatorId = creatorId;
	}

	public Date getCreatedDate() {
		return createdDate;
	}

	public void setCreatedDate(Date createdDate) {
		this.createdDate = createdDate;
	}

	public String getModifierId() {
		return modifierId;
	}

	public void setModifierId(String modifierId) {
		this.modifierId = modifierId;
	}

	public Date getModifiedDatetime() {
		return modifiedDatetime;
	}

	public void setModifiedDatetime(Date modifiedDatetime) {
		this.modifiedDatetime = modifiedDatetime;
	}

}
