package com.fms.pfc.domain.model.main;

import java.util.Date;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

@Entity
@Table(name = "rm_manf")
public class RmManf {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "rm_manf_id")
	private int rmManfId;

	@Column(name = "raw_matl_id")
	private int rawMatlId;

	@Column(name = "ts_no")
	private String tsNo;

	@Column(name = "manf_id")
	private int manfId;

	@Column(name = "vipd_date")
	private Date vipdDate;
	
	@Column(name = "vipd_filename")
	private String vipdFileName;

	@Column(name = "vipd_object")
	private byte[] vipdObject;

	@Column(name = "vipd_annex2_date")
	private Date vipdAnnex2Date;
	
	@Column(name = "vipd_annex2_filename")
	private String vipdAnnex2FileName;

	@Column(name = "vipd_annex2_object")
	private byte[] vipdAnnex2Object;

	@Column(name = "creator_id")
	private String creatorId;

	@Column(name = "created_datetime")
	private Date createdDate;

	@Column(name = "modifier_id")
	private String modifierId;

	@Column(name = "modified_datetime")
	private Date modifiedDate;

	@Column(name = "origin_country_id")
	private String originCountryId;

	private String vendorName;

	@Transient
	private List<Integer> supplId;

	@Transient
	private String supplierName;

	@Transient
	private String countryName;

	@Transient
	private String manfRecStatus;

	@Transient
	private int manfRowNo;

	@Transient
	private int supplDeclareStatus;
	
	@Transient
	private String rawMatName;
	
	@Transient
	private int vendorType;

	public int getRmManfId() {
		return rmManfId;
	}

	public void setRmManfId(int rmManfId) {
		this.rmManfId = rmManfId;
	}

	public int getRawMatlId() {
		return rawMatlId;
	}

	public void setRawMatlId(int rawMatlId) {
		this.rawMatlId = rawMatlId;
	}

	public String getTsNo() {
		return tsNo;
	}

	public void setTsNo(String tsNo) {
		this.tsNo = tsNo;
	}

	public int getManfId() {
		return manfId;
	}

	public void setManfId(int manfId) {
		this.manfId = manfId;
	}

	public Date getVipdDate() {
		return vipdDate;
	}

	public void setVipdDate(Date vipdDate) {
		this.vipdDate = vipdDate;
	}

	public byte[] getVipdObject() {
		return vipdObject;
	}

	public void setVipdObject(byte[] vipdObject) {
		this.vipdObject = vipdObject;
	}

	public Date getVipdAnnex2Date() {
		return vipdAnnex2Date;
	}

	public void setVipdAnnex2Date(Date vipdAnnex2Date) {
		this.vipdAnnex2Date = vipdAnnex2Date;
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

	public String getOriginCountryId() {
		return originCountryId;
	}

	public void setOriginCountryId(String originCountryId) {
		this.originCountryId = originCountryId;
	}

	public String getVendorName() {
		return vendorName;
	}

	public void setVendorName(String vendorName) {
		this.vendorName = vendorName;
	}

	public List<Integer> getSupplId() {
		return supplId;
	}

	public void setSupplId(List<Integer> supplId) {
		this.supplId = supplId;
	}

	public String getSupplierName() {
		return supplierName;
	}

	public void setSupplierName(String supplierName) {
		this.supplierName = supplierName;
	}

	public String getCountryName() {
		return countryName;
	}

	public void setCountryName(String countryName) {
		this.countryName = countryName;
	}

	public String getManfRecStatus() {
		return manfRecStatus;
	}

	public void setManfRecStatus(String manfRecStatus) {
		this.manfRecStatus = manfRecStatus;
	}

	public int getManfRowNo() {
		return manfRowNo;
	}

	public void setManfRowNo(int manfRowNo) {
		this.manfRowNo = manfRowNo;
	}

	public int getSupplDeclareStatus() {
		return supplDeclareStatus;
	}

	public void setSupplDeclareStatus(int supplDeclareStatus) {
		this.supplDeclareStatus = supplDeclareStatus;
	}

	public byte[] getVipdAnnex2Object() {
		return vipdAnnex2Object;
	}

	public void setVipdAnnex2Object(byte[] vipdAnnex2Object) {
		this.vipdAnnex2Object = vipdAnnex2Object;
	}

	public String getVipdFileName() {
		return vipdFileName;
	}

	public void setVipdFileName(String vipdFileName) {
		this.vipdFileName = vipdFileName;
	}

	public String getVipdAnnex2FileName() {
		return vipdAnnex2FileName;
	}

	public void setVipdAnnex2FileName(String vipdAnnex2FileName) {
		this.vipdAnnex2FileName = vipdAnnex2FileName;
	}

	public String getRawMatName() {
		return rawMatName;
	}

	public void setRawMatName(String rawMatName) {
		this.rawMatName = rawMatName;
	}

	public int getVendorType() {
		return vendorType;
	}

	public void setVendorType(int vendorType) {
		this.vendorType = vendorType;
	}

}
