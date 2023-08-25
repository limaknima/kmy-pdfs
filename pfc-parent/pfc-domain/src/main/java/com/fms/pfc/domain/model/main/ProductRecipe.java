package com.fms.pfc.domain.model.main;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.persistence.Version;

@Entity
@Table(name = "product_recipe")
public class ProductRecipe {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "pr_id")
	private Integer prId;

	@Column(name = "pr_code")
	private String prCode;

	@Column(name = "pr_name")
	private String prName;

	@Column(name = "remarks")
	private String remarks;

	@Column(name = "remarks_user_id")
	private String remarksUserId;

	@Column(name = "total_perc")
	private Double totalPerc;

	@Column(name = "record_status")
	private Integer recordStatus;

	@Column(name = "creator_id")
	private String creatorId;

	@Column(name = "created_datetime")
	private Date createdDate;

	@Column(name = "modifier_id")
	private String modifierId;

	@Column(name = "modified_datetime")
	private Date modifiedDate;

	@Column(name = "checker_id")
	private String checkerId;

	@Column(name = "submitted_datetime")
	private Date submittedDate;

	@Column(name = "pr_other_name1")
	private String prOtherName1;

	@Column(name = "pr_other_name2")
	private String prOtherName2;

	@Column(name = "pr_other_name3")
	private String prOtherName3;

	@Column(name = "pr_other_name4")
	private String prOtherName4;

	@Column(name = "pr_other_name5")
	private String prOtherName5;
	
	@Version
	@Column(name = "OPT_LOCK")
	private Integer optLock;
	
	@Column(name = "ONHOLD_FLAG")
	private String onHoldFlag;
	
	@Column(name = "FLAVOR_STATUS_ID")
	private Integer flavStatusId;

	@Transient
	private String ingredientName;

	@Transient
	private Integer numberPermitted;

	@Transient
	private Integer numberNotPermitted;

	@Transient
	private Integer numberNotSure;

	@Transient
	private Double ingPercentage;

	@Transient
	private Integer prRowNo;

	@Transient
	private String prodRecipeRecStatus;

	@Transient
	private boolean permissionToDelete;
	
	@Transient
	private String allOtherNames;
	
	@Transient
	private String flavStatusName;

	public Integer getPrId() {
		return prId;
	}

	public void setPrId(Integer prId) {
		this.prId = prId;
	}

	public String getPrCode() {
		return prCode;
	}

	public void setPrCode(String prCode) {
		this.prCode = prCode;
	}

	public String getPrName() {
		return prName;
	}

	public void setPrName(String prName) {
		this.prName = prName;
	}

	public String getRemarks() {
		return remarks;
	}

	public void setRemarks(String remarks) {
		this.remarks = remarks;
	}

	public String getRemarksUserId() {
		return remarksUserId;
	}

	public void setRemarksUserId(String remarksUserId) {
		this.remarksUserId = remarksUserId;
	}

	public Double getTotalPerc() {
		return totalPerc;
	}

	public void setTotalPerc(Double totalPerc) {
		this.totalPerc = totalPerc;
	}

	public Integer getRecordStatus() {
		return recordStatus;
	}

	public void setRecordStatus(Integer recordStatus) {
		this.recordStatus = recordStatus;
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

	public String getCheckerId() {
		return checkerId;
	}

	public void setCheckerId(String checkerId) {
		this.checkerId = checkerId;
	}

	public Date getSubmittedDate() {
		return submittedDate;
	}

	public void setSubmittedDate(Date submittedDate) {
		this.submittedDate = submittedDate;
	}

	public String getPrOtherName1() {
		return prOtherName1;
	}

	public void setPrOtherName1(String prOtherName1) {
		this.prOtherName1 = prOtherName1;
	}

	public String getPrOtherName2() {
		return prOtherName2;
	}

	public void setPrOtherName2(String prOtherName2) {
		this.prOtherName2 = prOtherName2;
	}

	public String getPrOtherName3() {
		return prOtherName3;
	}

	public void setPrOtherName3(String prOtherName3) {
		this.prOtherName3 = prOtherName3;
	}

	public String getPrOtherName4() {
		return prOtherName4;
	}

	public void setPrOtherName4(String prOtherName4) {
		this.prOtherName4 = prOtherName4;
	}

	public String getPrOtherName5() {
		return prOtherName5;
	}

	public void setPrOtherName5(String prOtherName5) {
		this.prOtherName5 = prOtherName5;
	}

	public String getIngredientName() {
		return ingredientName;
	}

	public void setIngredientName(String ingredientName) {
		this.ingredientName = ingredientName;
	}

	public Integer getNumberPermitted() {
		return numberPermitted;
	}

	public void setNumberPermitted(Integer numberPermitted) {
		this.numberPermitted = numberPermitted;
	}

	public Integer getNumberNotPermitted() {
		return numberNotPermitted;
	}

	public void setNumberNotPermitted(Integer numberNotPermitted) {
		this.numberNotPermitted = numberNotPermitted;
	}

	public Integer getNumberNotSure() {
		return numberNotSure;
	}

	public void setNumberNotSure(Integer numberNotSure) {
		this.numberNotSure = numberNotSure;
	}

	public Double getIngPercentage() {
		return ingPercentage;
	}

	public void setIngPercentage(Double ingPercentage) {
		this.ingPercentage = ingPercentage;
	}

	public Integer getPrRowNo() {
		return prRowNo;
	}

	public void setPrRowNo(Integer prRowNo) {
		this.prRowNo = prRowNo;
	}

	public String getProdRecipeRecStatus() {
		return prodRecipeRecStatus;
	}

	public void setProdRecipeRecStatus(String prodRecipeRecStatus) {
		this.prodRecipeRecStatus = prodRecipeRecStatus;
	}

	public boolean isPermissionToDelete() {
		return permissionToDelete;
	}

	public void setPermissionToDelete(boolean permissionToDelete) {
		this.permissionToDelete = permissionToDelete;
	}

	public Integer getOptLock() {
		return optLock;
	}

	public void setOptLock(Integer optLock) {
		this.optLock = optLock;
	}

	public String getOnHoldFlag() {
		return onHoldFlag;
	}

	public void setOnHoldFlag(String onHoldFlag) {
		this.onHoldFlag = onHoldFlag;
	}

	public String getAllOtherNames() {
		return allOtherNames;
	}

	public void setAllOtherNames(String allOtherNames) {
		this.allOtherNames = allOtherNames;
	}

	public Integer getFlavStatusId() {
		return flavStatusId;
	}

	public void setFlavStatusId(Integer flavStatusId) {
		this.flavStatusId = flavStatusId;
	}

	public String getFlavStatusName() {
		return flavStatusName;
	}

	public void setFlavStatusName(String flavStatusName) {
		this.flavStatusName = flavStatusName;
	}
}
