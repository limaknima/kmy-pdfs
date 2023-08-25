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
@Table(name = "rm_ingredient")
public class RmIngredient {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "rm_ing_id")
	private int rmIngId;

	@Column(name = "raw_matl_id")
	private int rawMatlId;

	@Column(name = "manf_id")
	private int manfId;

	@Column(name = "ing_name")
	private String ingName;

	@Column(name = "comp_perc")
	private String compPerc;

	@Column(name = "origin_country_id")
	private String orginCountryId;

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

	@Column(name = "other_field1")
	private String otherField1;

	@Column(name = "other_field2")
	private String otherField2;

	@Column(name = "other_field3")
	private String otherField3;

	@Column(name = "other_field4")
	private String otherField4;

	@Column(name = "other_field5")
	private String otherField5;

	@Column(name = "creator_id")
	private String creatorId;

	@Column(name = "created_datetime")
	private Date createdDate;

	@Column(name = "modifier_id")
	private String modifierId;

	@Column(name = "modified_datetime")
	private Date modifiedDate;

	private String vendorName;

	@Transient
	private String ingRecStatus;

	@Transient
	private int ingRowNo;

	public int getRmIngId() {
		return rmIngId;
	}

	public void setRmIngId(int rmIngId) {
		this.rmIngId = rmIngId;
	}

	public int getRawMatlId() {
		return rawMatlId;
	}

	public void setRawMatlId(int rawMatlId) {
		this.rawMatlId = rawMatlId;
	}

	public int getManfId() {
		return manfId;
	}

	public void setManfId(int manfId) {
		this.manfId = manfId;
	}

	public String getIngName() {
		return ingName;
	}

	public void setIngName(String ingName) {
		this.ingName = ingName;
	}

	public String getCompPerc() {
		return compPerc;
	}

	public void setCompPerc(String compPerc) {
		this.compPerc = compPerc;
	}

	public String getOrginCountryId() {
		return orginCountryId;
	}

	public void setOrginCountryId(String orginCountryId) {
		this.orginCountryId = orginCountryId;
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

	public String getOtherField1() {
		return otherField1;
	}

	public void setOtherField1(String otherField1) {
		this.otherField1 = otherField1;
	}

	public String getOtherField2() {
		return otherField2;
	}

	public void setOtherField2(String otherField2) {
		this.otherField2 = otherField2;
	}

	public String getOtherField3() {
		return otherField3;
	}

	public void setOtherField3(String otherField3) {
		this.otherField3 = otherField3;
	}

	public String getOtherField4() {
		return otherField4;
	}

	public void setOtherField4(String otherField4) {
		this.otherField4 = otherField4;
	}

	public String getOtherField5() {
		return otherField5;
	}

	public void setOtherField5(String otherField5) {
		this.otherField5 = otherField5;
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

	public String getVendorName() {
		return vendorName;
	}

	public void setVendorName(String vendorName) {
		this.vendorName = vendorName;
	}

	public String getIngRecStatus() {
		return ingRecStatus;
	}

	public void setIngRecStatus(String ingRecStatus) {
		this.ingRecStatus = ingRecStatus;
	}

	public int getIngRowNo() {
		return ingRowNo;
	}

	public void setIngRowNo(int ingRowNo) {
		this.ingRowNo = ingRowNo;
	}

}
