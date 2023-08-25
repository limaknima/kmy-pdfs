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
@Table(name = "RAW_MATERIAL")
public class RawMaterialList {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "RAW_MATL_ID", nullable = true)
	private int rawMatlId;

	@Column(name = "RAW_MATL_NAME")
	private String rawMatlName;

	@Column(name = "TS_NO")
	private String tsNo;

	@Column(name = "RECORD_STATUS")
	private int recordStatus;

	@Column(name = "GMO_STATUS")
	private String gmoStatus;

	@Column(name = "FLAVOR_STATUS")
	private String flavorStatus;

	@Column(name = "CREATOR_ID")
	private String creatorId;

	@Column(name = "CREATED_DATETIME")
	private Date createdDate;

	@Column(name = "MANUFACTURER_NAME")
	private String manufacturerName;

	@Column(name = "SUPPLIER_NAME")
	private String supplierName;

	@Column(name = "VIPD_DATE")
	private String vipdDate;

	@Column(name = "VIPD_ANNEX2_DATE")
	private String vipdAnnex2Date;

	@Transient
	private String vipdDateFormatted;

	@Transient
	private String vipdAnnex2DateFormatted;

	@Transient
	private boolean permissionToDelete;

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

	public int getRecordStatus() {
		return recordStatus;
	}

	public void setRecordStatus(int recordStatus) {
		this.recordStatus = recordStatus;
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

	public Date getCreatedDate() {
		return createdDate;
	}

	public void setCreatedDate(Date createdDate) {
		this.createdDate = createdDate;
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

	public String getManufacturerName() {
		return manufacturerName;
	}

	public void setManufacturerName(String manufacturerName) {
		this.manufacturerName = manufacturerName;
	}

	public String getSupplierName() {
		return supplierName;
	}

	public void setSupplierName(String supplierName) {
		this.supplierName = supplierName;
	}

	public String getVipdDateFormatted() {
		return vipdDateFormatted;
	}

	public void setVipdDateFormatted(String vipdDateFormatted) {
		this.vipdDateFormatted = vipdDateFormatted;
	}

	public String getVipdAnnex2DateFormatted() {
		return vipdAnnex2DateFormatted;
	}

	public void setVipdAnnex2DateFormatted(String vipdAnnex2DateFormatted) {
		this.vipdAnnex2DateFormatted = vipdAnnex2DateFormatted;
	}

	public String getCreatorId() {
		return creatorId;
	}

	public void setCreatorId(String creatorId) {
		this.creatorId = creatorId;
	}

}
