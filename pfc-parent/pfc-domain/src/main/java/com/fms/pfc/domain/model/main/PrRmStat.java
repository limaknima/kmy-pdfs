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
@Table(name = "PR_RM_STAT")
public class PrRmStat {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "PR_RM_STAT_ID")
	private Integer prRmStatId;

	@Column(name = "PR_ID")
	private Integer prId;

	@Column(name = "RAW_MATL_ID")
	private int rawMatlId;

	@Transient
	private String rawMatlName;

	@Column(name = "TOTAL_PERC")
	private Double totalPerc;

	@Column(name = "SYS_FINAL_STATUS")
	private Integer sysFinalStatus;

	@Transient
	private String sysFinalStatusName;

	@Column(name = "FINAL_STATUS")
	private Integer finalStatus;

	@Transient
	private String finalStatusName;

	@Column(name = "COUNTRY_ID")
	private String countryId;

	@Column(name = "REMARKS")
	private String remarks;

	@Column(name = "REG_ID")
	private Integer regId;

	@Column(name = "CREATOR_ID")
	private String creatorId;

	@Column(name = "CREATED_DATETIME")
	private Date createdDate;

	@Column(name = "MODIFIER_ID")
	private String modifierId;

	@Column(name = "MODIFIED_DATETIME")
	private Date modifiedDate;

	@Column(name = "AFFECTED_FLAG")
	private String affectedFlag;

	@Transient
	private String productName;

	@Transient
	private Boolean TSNo;
	
	@Transient
	private String countryName;
	
	@Transient
	private String prName;
	
	@Transient
	private String prCode;
	
	@Transient
	private Integer rmRefChanged;
	
	public Integer getPrRmStatId() {
		return prRmStatId;
	}

	public void setPrRmStatId(Integer prRmStatId) {
		this.prRmStatId = prRmStatId;
	}

	public Integer getPrId() {
		return prId;
	}

	public void setPrId(Integer prId) {
		this.prId = prId;
	}

	public int getRawMatlId() {
		return rawMatlId;
	}

	public void setRawMatlId(int rawMatlId) {
		this.rawMatlId = rawMatlId;
	}

	public String getRawMatlName() {
		return rawMatlName;
	}

	public void setRawMatlName(String rawMatlName) {
		this.rawMatlName = rawMatlName;
	}

	public Double getTotalPerc() {
		return totalPerc;
	}

	public void setTotalPerc(Double totalPerc) {
		this.totalPerc = totalPerc;
	}

	public Integer getSysFinalStatus() {
		return sysFinalStatus;
	}

	public void setSysFinalStatus(Integer sysFinalStatus) {
		this.sysFinalStatus = sysFinalStatus;
	}

	public Integer getFinalStatus() {
		return finalStatus;
	}

	public void setFinalStatus(Integer finalStatus) {
		this.finalStatus = finalStatus;
	}

	public String getSysFinalStatusName() {
		return sysFinalStatusName;
	}

	public void setSysFinalStatusName(String sysFinalStatusName) {
		this.sysFinalStatusName = sysFinalStatusName;
	}

	public String getFinalStatusName() {
		return finalStatusName;
	}

	public void setFinalStatusName(String finalStatusName) {
		this.finalStatusName = finalStatusName;
	}

	public String getCountryId() {
		return countryId;
	}

	public void setCountryId(String countryId) {
		this.countryId = countryId;
	}

	public String getRemarks() {
		return remarks;
	}

	public void setRemarks(String remarks) {
		this.remarks = remarks;
	}

	public Integer getRegId() {
		return regId;
	}

	public void setRegId(Integer regId) {
		this.regId = regId;
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

	public Boolean getTSNo() {
		return TSNo;
	}

	public void setTSNo(Boolean tSNo) {
		TSNo = tSNo;
	}

	public String getAffectedFlag() {
		return affectedFlag;
	}

	public void setAffectedFlag(String affectedFlag) {
		this.affectedFlag = affectedFlag;
	}
	
	public String getCountryName() {
		return countryName;
	}

	public void setCountryName(String countryName) {
		this.countryName = countryName;
	}

	public String getPrName() {
		return prName;
	}

	public void setPrName(String prName) {
		this.prName = prName;
	}

	public String getPrCode() {
		return prCode;
	}

	public void setPrCode(String prCode) {
		this.prCode = prCode;
	}

	public Integer getRmRefChanged() {
		return rmRefChanged;
	}

	public void setRmRefChanged(Integer rmRefChanged) {
		this.rmRefChanged = rmRefChanged;
	}

	

}
