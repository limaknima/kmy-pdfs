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
@Table(name = "REG_DOC_CAT")
public class RegulationCategory {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "reg_doc_cat_id", nullable = false)
	private int regDocCatId;

	@Column(name = "reg_doc_cat_name", nullable = false)
	private String catName;

	@Column(name = "reg_doc_cat_desc")
	private String catDesc;

	@Column(name = "creator_id")
	private String creatorId;

	@Column(name = "created_datetime")
	private Date createdDate;

	@Column(name = "modifier_id")
	private String modifierId;

	@Column(name = "modified_datetime")
	private Date modifiedDatetime;
	
	@Column(name = "country_list")
	private String countryList;
	
	@Transient
	private String countryListDesc;

	public int getRegDocCatId() {
		return regDocCatId;
	}

	public void setRegDocCatId(int regDocCatId) {
		this.regDocCatId = regDocCatId;
	}

	public String getCatName() {
		return catName;
	}

	public void setCatName(String catName) {
		this.catName = catName;
	}

	public String getCatDesc() {
		return catDesc;
	}

	public void setCatDesc(String catDesc) {
		this.catDesc = catDesc;
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

	public Date getModifiedDatetime() {
		return modifiedDatetime;
	}

	public void setModifiedDatetime(Date modifiedDatetime) {
		this.modifiedDatetime = modifiedDatetime;
	}

	public String getCountryList() {
		return countryList;
	}

	public void setCountryList(String countryList) {
		this.countryList = countryList;
	}

	public String getCountryListDesc() {
		return countryListDesc;
	}

	public void setCountryListDesc(String countryListDesc) {
		this.countryListDesc = countryListDesc;
	}

}
