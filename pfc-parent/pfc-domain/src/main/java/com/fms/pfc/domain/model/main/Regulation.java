package com.fms.pfc.domain.model.main;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "REGULATION")
public class Regulation {

	@Id
	@Column(name = "ROW_ID")
	private String rowId;

	@Column(name = "REG_ID")
	private String regId;

	@Column(name = "REG_DOC_ID")
	private String regDocId;

	@Column(name = "COUNTRY_ID")
	private String countryId;

	@Column(name = "COUNTRY_NAME")
	private String countryName;

	@Column(name = "REG_DOC_CAT_GRP_ID")
	private String regDocCatGrpId;

	@Column(name = "REG_DOC_CAT_GRP_NAME")
	private String regDocCatGrpName;

	@Column(name = "RAW_MATL_ID")
	private String rawMatlId;

	@Column(name = "RAW_MATL_NAME")
	private String rawMatlName;

	@Column(name = "REG_DOC_CAT_ID")
	private String regDocCatId;

	@Column(name = "REG_DOC_CAT_NAME")
	private String regDocCatName;

	@Column(name = "FILE_NAME")
	private String fileName;

	@Column(name = "ARCHIVED_FLAG")
	private String archivedFlag;
	
	public String getRegId() {
		return regId;
	}

	public void setRegId(String regId) {
		this.regId = regId;
	}

	public String getRegDocId() {
		return regDocId;
	}

	public void setRegDocId(String regDocId) {
		this.regDocId = regDocId;
	}

	public String getCountryId() {
		return countryId;
	}

	public void setCountryId(String countryId) {
		this.countryId = countryId;
	}

	public String getCountryName() {
		return countryName;
	}

	public void setCountryName(String countryName) {
		this.countryName = countryName;
	}

	public String getRegDocCatGrpId() {
		return regDocCatGrpId;
	}

	public void setRegDocCatGrpId(String regDocCatGrpId) {
		this.regDocCatGrpId = regDocCatGrpId;
	}

	public String getRegDocCatGrpName() {
		return regDocCatGrpName;
	}

	public void setRegDocCatGrpName(String regDocCatGrpName) {
		this.regDocCatGrpName = regDocCatGrpName;
	}

	public String getRawMatlId() {
		return rawMatlId;
	}

	public void setRawMatlId(String rawMatlId) {
		this.rawMatlId = rawMatlId;
	}

	public String getRawMatlName() {
		return rawMatlName;
	}

	public void setRawMatlName(String rawMatlName) {
		this.rawMatlName = rawMatlName;
	}

	public String getRegDocCatId() {
		return regDocCatId;
	}

	public void setRegDocCatId(String regDocCatId) {
		this.regDocCatId = regDocCatId;
	}

	public String getRegDocCatName() {
		return regDocCatName;
	}

	public void setRegDocCatName(String regDocCatName) {
		this.regDocCatName = regDocCatName;
	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public String getArchivedFlag() {
		return archivedFlag;
	}

	public void setArchivedFlag(String archivedFlag) {
		this.archivedFlag = archivedFlag;
	}

}
