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
@Table(name = "raw_material")
public class RawMaterial {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "raw_matl_id", nullable = true)
	private int rawMatlId;

	@Column(name = "raw_matl_name")
	private String rawMatlName;

	@Column(name = "ts_no")
	private String tsNo;

	@Column(name = "remarks")
	private String remarks;

	@Column(name = "remarks_user_id")
	private String remarksUserId;

	@Column(name = "record_status")
	private int recordStatus;

	@Column(name = "ins_no")
	private String insNo;

	@Column(name = "e_no")
	private String eNo;

	@Column(name = "fema_no")
	private String femaNo;

	@Column(name = "jecfa_no")
	private String jecfaNo;

	@Column(name = "cas_no")
	private String casNo;

	@Column(name = "gmo_status")
	private String gmoStatus;

	@Column(name = "flavor_status")
	private String flavorStatus;

	@Column(name = "pho_flag")
	private String phoFlag;

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
	
	@Column(name = "FLAVOR_STATUS_ID")
	private Integer flavStatusId;
	
	@Version
	@Column(name = "OPT_LOCK")
	private Integer optLock;

	@Transient
	private String tsNoManufacturer;

	@Transient
	private String manufacturer;

	@Transient
	private String supplier;

	@Transient
	private String vipdDate;

	@Transient
	private String vipdAnnex2Date;

	@Transient
	private boolean permissionToDelete;
	
	@Transient
	private String flavStatusName;

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

	public String getTsNo() {
		return tsNo;
	}

	public void setTsNo(String tsNo) {
		this.tsNo = tsNo;
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

	public int getRecordStatus() {
		return recordStatus;
	}

	public void setRecordStatus(int recordStatus) {
		this.recordStatus = recordStatus;
	}

	public String getInsNo() {
		return insNo;
	}

	public void setInsNo(String insNo) {
		this.insNo = insNo;
	}

	public String getENo() {
		return eNo;
	}

	public void setENo(String eNo) {
		this.eNo = eNo;
	}

	public String getFemaNo() {
		return femaNo;
	}

	public void setFemaNo(String femaNo) {
		this.femaNo = femaNo;
	}

	public String getJecfaNo() {
		return jecfaNo;
	}

	public void setJecfaNo(String jecfaNo) {
		this.jecfaNo = jecfaNo;
	}

	public String getCasNo() {
		return casNo;
	}

	public void setCasNo(String casNo) {
		this.casNo = casNo;
	}

	public String getGmoStatus() {
		return gmoStatus;
	}

	public void setGmoStatus(String gmoStatus) {
		this.gmoStatus = gmoStatus;
	}

	public String getFlavorStatus() {
		return flavorStatus;
	}

	public void setFlavorStatus(String flavorStatus) {
		this.flavorStatus = flavorStatus;
	}

	public String getPhoFlag() {
		return phoFlag;
	}

	public void setPhoFlag(String phoFlag) {
		this.phoFlag = phoFlag;
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

	public String getTsNoManufacturer() {
		return tsNoManufacturer;
	}

	public void setTsNoManufacturer(String tsNoManufacturer) {
		this.tsNoManufacturer = tsNoManufacturer;
	}

	public String getManufacturer() {
		return manufacturer;
	}

	public void setManufacturer(String manufacturer) {
		this.manufacturer = manufacturer;
	}

	public String getSupplier() {
		return supplier;
	}

	public void setSupplier(String supplier) {
		this.supplier = supplier;
	}

	public String getVipdDate() {
		return vipdDate;
	}

	public void setVipdDate(String vipdDate) {
		this.vipdDate = vipdDate;
	}

	public String getVipdAnnex2Date() {
		return vipdAnnex2Date;
	}

	public void setVipdAnnex2Date(String vipdAnnex2Date) {
		this.vipdAnnex2Date = vipdAnnex2Date;
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
