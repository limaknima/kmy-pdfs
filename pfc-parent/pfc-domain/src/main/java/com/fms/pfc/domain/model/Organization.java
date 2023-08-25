package com.fms.pfc.domain.model;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

@Entity
@Table(name = "ORGANIZATION")
public class Organization {

	@Id
	@Column(name = "ORG_ID")
	private String orgaID;

	@Column(name = "ORG_NAME")
	private String orgaName;

	@Column(name = "ORG_TYPE")
	private String orgaType;

	@Column(name = "EMAIL")
	private String email;

	@Column(name = "STREET")
	private String address;

	@Column(name = "TOWN")
	private String city;

	@Column(name = "POSTCODE")
	private String postcode;

	@Column(name = "STATE_ID")
	private String state;

	@Column(name = "COUNTRY_ID")
	private String country;
	
	@Column(name = "OFFICE_TEL")
	private String tel;

	@Column(name = "FAX_NO")
	private String fax;

	@Column(name = "IMAGE")
	private byte[] logo;

	@Column(name = "REMARKS")
	private String url;

	@Column(name = "EFFECTIVE_DATE_FROM")
	private Date effectStart;
	
	@Transient
	private String effectStartStr;

	@Column(name = "EFFECTIVE_DATE_TO")
	private Date effectEnd;
	
	@Transient
	private String effectEndStr;

	@Column(name = "CREATOR_ID")
	private String creator;

	@Column(name = "CREATED_DATETIME")
	private Date createTime;

	@Column(name = "MODIFIER_ID")
	private String modifier;

	@Column(name = "MODIFIED_DATETIME")
	private Date modifyTime;
	
	public Organization() {
	}

	public String getOrgaID() {
		return orgaID;
	}

	public void setOrgaID(String orgaID) {
		this.orgaID = orgaID;
	}

	public String getOrgaName() {
		return orgaName;
	}

	public void setOrgaName(String orgaName) {
		this.orgaName = orgaName;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getOrgaType() {
		return orgaType;
	}

	public void setOrgaType(String orgaType) {
		this.orgaType = orgaType;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	public String getPostcode() {
		return postcode;
	}

	public void setPostcode(String postcode) {
		this.postcode = postcode;
	}

	public String getState() {
		return state;
	}

	public void setState(String state) {
		this.state = state;
	}

	public String getCountry() {
		return country;
	}

	public void setCountry(String country) {
		this.country = country;
	}

	public String getTel() {
		return tel;
	}

	public void setTel(String tel) {
		this.tel = tel;
	}

	public String getFax() {
		return fax;
	}

	public void setFax(String fax) {
		this.fax = fax;
	}

	public byte[] getLogo() {
		return logo;
	}

	public void setLogo(byte[] logo) {
		this.logo = logo;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public Date getEffectStart() {
		return effectStart;
	}

	public void setEffectStart(Date effectStart) {
		this.effectStart = effectStart;
	}

	public Date getEffectEnd() {
		return effectEnd;
	}

	public void setEffectEnd(Date effectEnd) {
		this.effectEnd = effectEnd;
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

	public String getEffectStartStr() {
		return effectStartStr;
	}

	public void setEffectStartStr(String effectStartStr) {
		this.effectStartStr = effectStartStr;
	}

	public String getEffectEndStr() {
		return effectEndStr;
	}

	public void setEffectEndStr(String effectEndStr) {
		this.effectEndStr = effectEndStr;
	}

}
