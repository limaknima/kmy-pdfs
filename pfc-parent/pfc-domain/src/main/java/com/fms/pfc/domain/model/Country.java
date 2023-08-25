package com.fms.pfc.domain.model;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "REF_COUNTRY")
public class Country {
	
	@Id	
	@Column(name = "COUNTRY_ID")
	private String countryId;

	@Column(name = "COUNTRY_NAME")
	private String countryName;

	@Column(name = "CREATOR_ID")
	private String creator;
	
	@Column(name = "CREATED_DATETIME")
	private Date createTime;
	
	@Column(name = "MODIFIER_ID")
	private String modifier;

	@Column(name = "MODIFIED_DATETIME")
	private Date modifyTime;
	
	@Column(name = "EVAL_FLAG")
	private String evalFlag;
	
	@Column(name = "ORIGIN_FLAG")
	private String originFlag;
	
	public Country() {
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

	public String getCreator() {
		return creator;
	}

	public void setCreator(String creator) {
		this.creator = creator;
	}

	public Date getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}

	public String getModifier() {
		return modifier;
	}

	public void setModifier(String modifier) {
		this.modifier = modifier;
	}

	public Date getModifyTime() {
		return modifyTime;
	}

	public void setModifyTime(Date modifyTime) {
		this.modifyTime = modifyTime;
	}

	public String getEvalFlag() {
		return evalFlag;
	}

	public void setEvalFlag(String evalFlag) {
		this.evalFlag = evalFlag;
	}

	public String getOriginFlag() {
		return originFlag;
	}

	public void setOriginFlag(String originFlag) {
		this.originFlag = originFlag;
	}
	
}
