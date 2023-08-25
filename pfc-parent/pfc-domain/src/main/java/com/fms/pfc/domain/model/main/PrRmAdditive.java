package com.fms.pfc.domain.model.main;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

@Entity
@Table(name = "PR_RM_ADDITIVE")
public class PrRmAdditive {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "PR_RM_ADDT_ID")
	private Integer prRmAddtID;

	@Column(name = "PR_ID")
	private Integer prId;

	@Column(name = "RAW_MATL_ID")
	private Integer rawMatlId;
	
	@Column(name = "ADDITIVE_DESC")
	private String additiveDesc;

	@Column(name = "CREATOR_ID")
	private String creatorId;

	@Column(name = "CREATED_DATETIME")
	private Date createdDate;

	@Column(name = "MODIFIER_ID")
	private String modifierId;

	@Column(name = "MODIFIED_DATETIME")
	private Date modifiedDate;
	
	@Transient
	private String productName;
	
	@Transient
	private Integer prRmAdditiveRowNo;
	
	@Transient
	private String prRmAdditiveRecStatus;
	
	@Transient
	private boolean isChanged;

	public Integer getPrRmAddtID() {
		return prRmAddtID;
	}

	public void setPrRmAddtID(Integer prRmAddtID) {
		this.prRmAddtID = prRmAddtID;
	}

	public Integer getPrId() {
		return prId;
	}

	public void setPrId(Integer prId) {
		this.prId = prId;
	}

	public Integer getRawMatlId() {
		return rawMatlId;
	}

	public void setRawMatlId(Integer rawMatlId) {
		this.rawMatlId = rawMatlId;
	}

	public String getAdditiveDesc() {
		return additiveDesc;
	}

	public void setAdditiveDesc(String additiveDesc) {
		this.additiveDesc = additiveDesc;
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

	public Date getModifiedDate() {
		return modifiedDate;
	}

	public void setModifiedDate(Date modifiedDate) {
		this.modifiedDate = modifiedDate;
	}

	public String getProductName() {
		return productName;
	}

	public void setProductName(String productName) {
		this.productName = productName;
	}

	public Integer getPrRmAdditiveRowNo() {
		return prRmAdditiveRowNo;
	}

	public void setPrRmAdditiveRowNo(Integer prRmAdditiveRowNo) {
		this.prRmAdditiveRowNo = prRmAdditiveRowNo;
	}

	public String getPrRmAdditiveRecStatus() {
		return prRmAdditiveRecStatus;
	}

	public void setPrRmAdditiveRecStatus(String prRmAdditiveRecStatus) {
		this.prRmAdditiveRecStatus = prRmAdditiveRecStatus;
	}

	public boolean isChanged() {
		return isChanged;
	}

	public void setChanged(boolean isChanged) {
		this.isChanged = isChanged;
	}
}
