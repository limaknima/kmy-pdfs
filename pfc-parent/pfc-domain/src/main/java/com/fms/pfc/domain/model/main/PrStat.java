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
@Table(name = "PR_STAT")
public class PrStat {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "PR_STAT_ID")
	private Integer prStatId;

	@Column(name = "PR_ID")
	private Integer prId;

	@Transient
	private String productName;

	@Column(name = "FINAL_STATUS")
	private Integer finalStatus;

	@Transient
	private String statusName;

	@Column(name = "COUNTRY_ID")
	private String countryId;

	@Column(name = "CNTRY_NAME")
	private String cntryName;

	@Transient
	private String countryName;

	@Column(name = "REMARKS")
	private String remarks;

	@Column(name = "REMARKS_USER_ID")
	private String remarksUserId;

	@Column(name = "CREATOR_ID")
	private String creatorId;

	@Column(name = "CREATED_DATETIME")
	private Date createdDate;

	@Column(name = "MODIFIER_ID")
	private String modifierId;

	@Column(name = "MODIFIED_DATETIME")
	private Date modifiedDate;

	@Transient
	private String finalStatusDesc;

	@Transient
	private String prStatRecStatus;

	@Transient
	private int prStatRowNo;
	
	@Transient
	private String affectedFlag;

	public Integer getPrStatId() {
		return prStatId;
	}

	public void setPrStatId(Integer prStatId) {
		this.prStatId = prStatId;
	}

	public Integer getPrId() {
		return prId;
	}

	public void setPrId(Integer prId) {
		this.prId = prId;
	}

	public String getProductName() {
		return productName;
	}

	public void setProductName(String productName) {
		this.productName = productName;
	}

	public Integer getFinalStatus() {
		return finalStatus;
	}

	public void setFinalStatus(Integer finalStatus) {
		this.finalStatus = finalStatus;
	}

	public String getStatusName() {
		return statusName;
	}

	public void setStatusName(String statusName) {
		this.statusName = statusName;
	}

	public String getCountryId() {
		return countryId;
	}

	public void setCountryId(String countryId) {
		this.countryId = countryId;
	}

	public String getCntryName() {
		return cntryName;
	}

	public void setCntryName(String cntryName) {
		this.cntryName = cntryName;
	}

	public String getCountryName() {
		return countryName;
	}

	public void setCountryName(String countryName) {
		this.countryName = countryName;
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

	public String getFinalStatusDesc() {
		return finalStatusDesc;
	}

	public void setFinalStatusDesc(String finalStatusDesc) {
		this.finalStatusDesc = finalStatusDesc;
	}

	public String getPrStatRecStatus() {
		return prStatRecStatus;
	}

	public void setPrStatRecStatus(String prStatRecStatus) {
		this.prStatRecStatus = prStatRecStatus;
	}

	public int getPrStatRowNo() {
		return prStatRowNo;
	}

	public void setPrStatRowNo(int prStatRowNo) {
		this.prStatRowNo = prStatRowNo;
	}

	public String getAffectedFlag() {
		return affectedFlag;
	}

	public void setAffectedFlag(String affectedFlag) {
		this.affectedFlag = affectedFlag;
	}

}
