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
@Table(name = "PR_INGREDIENT")
public class PrIngredient {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "PR_ING_ID")
	private int prIngId;

	@Column(name = "PR_ID")
	private int prId;

	@Column(name = "PARENT_REF_ID")
	private int parentRefId;

	@Column(name = "REF_ID")
	private int refId;

	@Column(name = "REF_TYPE")
	private int refType;

	@Column(name = "ING_PERC")
	private Double ingPerc;

	@Column(name = "SEL_TS_NO")
	private String selTsNo;

	@Column(name = "SEQ_NO")
	private int seqNo;

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
	
	@Column(name = "ALT_RM")
	private String altRm;

	@Transient
	private String categoryName;

	@Transient
	private String ingredientName;

	@Transient
	private int prIngRowNo;

	@Transient
	private String ingStatus;
	
	@Transient
	private String altRmNames;
	
	@Transient
	private String rmFlavStatusName;

	public int getPrIngId() {
		return prIngId;
	}

	public void setPrIngId(int prIngId) {
		this.prIngId = prIngId;
	}

	public int getPrId() {
		return prId;
	}

	public void setPrId(int prId) {
		this.prId = prId;
	}

	public int getParentRefId() {
		return parentRefId;
	}

	public void setParentRefId(int parentRefId) {
		this.parentRefId = parentRefId;
	}

	public int getRefId() {
		return refId;
	}

	public void setRefId(int refId) {
		this.refId = refId;
	}

	public int getRefType() {
		return refType;
	}

	public void setRefType(int refType) {
		this.refType = refType;
	}

	public Double getIngPerc() {
		return ingPerc;
	}

	public void setIngPerc(Double ingPerc) {
		this.ingPerc = ingPerc;
	}

	public String getSelTsNo() {
		return selTsNo;
	}

	public void setSelTsNo(String selTsNo) {
		this.selTsNo = selTsNo;
	}

	public int getSeqNo() {
		return seqNo;
	}

	public void setSeqNo(int seqNo) {
		this.seqNo = seqNo;
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

	public int getPrIngRowNo() {
		return prIngRowNo;
	}

	public void setPrIngRowNo(int prIngRowNo) {
		this.prIngRowNo = prIngRowNo;
	}

	public String getCategoryName() {
		return categoryName;
	}

	public void setCategoryName(String categoryName) {
		this.categoryName = categoryName;
	}

	public String getIngredientName() {
		return ingredientName;
	}

	public void setIngredientName(String ingredientName) {
		this.ingredientName = ingredientName;
	}

	public String getIngStatus() {
		return ingStatus;
	}

	public void setIngStatus(String ingStatus) {
		this.ingStatus = ingStatus;
	}

	public String getAltRm() {
		return altRm;
	}

	public void setAltRm(String altRm) {
		this.altRm = altRm;
	}

	public String getAltRmNames() {
		return altRmNames;
	}

	public void setAltRmNames(String altRmNames) {
		this.altRmNames = altRmNames;
	}

	public String getRmFlavStatusName() {
		return rmFlavStatusName;
	}

	public void setRmFlavStatusName(String rmFlavStatusName) {
		this.rmFlavStatusName = rmFlavStatusName;
	}

}
