package com.fms.pfc.domain.model.main;
//Kimberley 
//Differences with Regulation.java are some some difference with the datatypes
//Used for 5.3.2	Upload RM regulation

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
@Table(name = "regulation")
public class Reg{
	
	@Transient
	private String rawMatlName;
	@Transient
	private String fileName;	
	@Transient
	private List<RegulationDoc> regDocList;
	@Transient
	private String regDocCatId;
	@Transient
	private String regDocCatGrpId;	
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "reg_id")
	private int regId;
	
	@Column(name = "country_name")
	private String countryName;
	
	@Column(name = "raw_matl_id")
	private int rawMatlId;
	
	@Column(name = "country_id")
	private String countryId;
	
	@Column(name = "reg_name")
	private String regName;
	
	@Column(name = "ref_url1")
	private String refUrl1;
	
	@Column(name = "ref_url2")
	private String refUrl2;
	
	@Column(name = "ref_url3")
	private String refUrl3;
	
	@Column(name = "ref_url4")
	private String refUrl4;
	
	@Column(name = "ref_url5")
	private String refUrl5;
	
	@Column(name = "remarks")
	private String remarks;
	
	@Column(name = "creator_id")
	private String creatorId;
	
	@Column(name = "created_datetime")
	private Date createdDateTime;
	
	@Column(name = "modifier_id")
	private String modifierId;
	
	@Column(name = "modified_datetime")
	private Date modifiedDateTime;

	public Reg() {
		
	}
	
	public Reg(String rawMatlName, String countryName, String regDocCatGrpName, String regDocCatName, String fileName, 
			int regId, int rawMatlId, String countryId, String regName, String refUrl1, 
			String refUrl2, String refUrl3, String refUrl4, String refUrl5, String remarks, 
			String creatorId, Date createdDateTime, String modifierId, Date modifiedDateTime) {
		this.setRawMatlName(rawMatlName);
		this.setRegId(regId);
		this.setRawMatlId(rawMatlId);
		this.setCountryId(countryId);
		this.setRegName(regName);
		this.setRefUrl1(refUrl1);
		this.setRefUrl2(refUrl2);
		this.setRefUrl3(refUrl3);
		this.setRefUrl4(refUrl4);
		this.setRefUrl5(refUrl5);
		this.setRemarks(remarks);
		this.setCreatorId(creatorId);
		this.setCreatedDateTime(createdDateTime);
		this.setModifierId(modifierId);
		this.setModifiedDateTime(modifiedDateTime);
	}

	
	public String getRawMatlName() {
		return rawMatlName;
	}
	public void setRawMatlName(String rawMatlName) {
		this.rawMatlName = rawMatlName;
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

	public String getRegDocCatId() {
		return regDocCatId;
	}
	public void setRegDocCatId(String regDocCatId) {
		this.regDocCatId = regDocCatId;
	}

	public String getFileName() {
		return fileName;
	}
	public void setFileName(String fileName) {
		this.fileName = fileName;
	}
	
	public List<RegulationDoc> getRegDocList() {
		return regDocList;
	}
	public void setRegDocList(List<RegulationDoc> regDocList) {
		this.regDocList = regDocList;
	}

	public int getRegId() {
		return regId;
	}
	public void setRegId(int regId) {
		this.regId = regId;
	}
	
	public int getRawMatlId() {
		return rawMatlId;
	}
	public void setRawMatlId(int rawMatlId) {
		this.rawMatlId = rawMatlId;
	}
	
	public String getCountryId() {
		return countryId;
	}
	public void setCountryId(String countryId) {
		this.countryId = countryId;
	}

	public String getRegName() {
		return regName;
	}
	public void setRegName(String regName) {
		this.regName = regName;
	}

	public String getRefUrl1() {
		return refUrl1;
	}
	public void setRefUrl1(String refUrl1) {
		this.refUrl1 = refUrl1;
	}

	public String getRefUrl2() {
		return refUrl2;
	}
	public void setRefUrl2(String refUrl2) {
		this.refUrl2 = refUrl2;
	}
	
	public String getRefUrl3() {
		return refUrl3;
	}
	public void setRefUrl3(String refUrl3) {
		this.refUrl3 = refUrl3;
	}
	
	public String getRefUrl4() {
		return refUrl4;
	}
	public void setRefUrl4(String refUrl4) {
		this.refUrl4 = refUrl4;
	}
	
	public String getRefUrl5() {
		return refUrl5;
	}
	public void setRefUrl5(String refUrl5) {
		this.refUrl5 = refUrl5;
	}
	
	public String getRemarks() {
		return remarks;
	}
	public void setRemarks(String remarks) {
		this.remarks = remarks;
	}
	
	public String getCreatorId() {
		return creatorId;
	}
	public void setCreatorId(String creatorId) {
		this.creatorId = creatorId;
	}

	public Date getCreatedDateTime() {
		return createdDateTime;
	}
	public void setCreatedDateTime(Date createdDateTime) {
		this.createdDateTime = createdDateTime;
	}
	
	public String getModifierId() {
		return modifierId;
	}
	public void setModifierId(String modifierId) {
		this.modifierId = modifierId;
	}
	
	public Date getModifiedDateTime() {
		return modifiedDateTime;
	}
	public void setModifiedDateTime(Date modifiedDateTime) {
		this.modifiedDateTime = modifiedDateTime;
	}
}
