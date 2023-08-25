package com.fms.pfc.domain.model;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "vendor")
public class Manufacturer {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "vendor_id")
	private int vendorId;

	@Column(name = "vendor_name")
	private String vendorName;

	@Column(name = "vendor_type")
	private String vendorType;

	@Column(name = "email")
	private String email;

	@Column(name = "address")
	private String address;

	@Column(name = "street")
	private String street;

	@Column(name = "town")
	private String town;

	@Column(name = "postcode")
	private String postcode;

	@Column(name = "state_id")
	private String stateId;

	@Column(name = "country_id")
	private String countryId;

	@Column(name = "url")
	private String url;

	@Column(name = "office_tel")
	private String officeTel;

	@Column(name = "fax_no")
	private String faxNo;

	@Column(name = "effective_date_from")
	private String effDateFrom;

	@Column(name = "effective_date_to")
	private String effDateTo;

	@Column(name = "creator_id")
	private String creatorId;

	@Column(name = "created_datetime")
	private Date createdDateTime;

	@Column(name = "modifier_id")
	private String modifierId;

	@Column(name = "modified_datetime")
	private Date modifiedDateTime;

	public int getVendorId() {
		return vendorId;
	}

	public void setVendorId(int vendorId) {
		this.vendorId = vendorId;
	}

	public String getVendorName() {
		return vendorName;
	}

	public void setVendorName(String vendorName) {
		this.vendorName = vendorName;
	}

	public String getVendorType() {
		return vendorType;
	}

	public void setVendorType(String vendorType) {
		this.vendorType = vendorType;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getStreet() {
		return street;
	}

	public void setStreet(String street) {
		this.street = street;
	}

	public String getTown() {
		return town;
	}

	public void setTown(String town) {
		this.town = town;
	}

	public String getPostcode() {
		return postcode;
	}

	public void setPostcode(String postcode) {
		this.postcode = postcode;
	}

	public String getStateId() {
		return stateId;
	}

	public void setStateId(String stateId) {
		this.stateId = stateId;
	}

	public String getCountryId() {
		return countryId;
	}

	public void setCountryId(String countryId) {
		this.countryId = countryId;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getOfficeTel() {
		return officeTel;
	}

	public void setOfficeTel(String officeTel) {
		this.officeTel = officeTel;
	}

	public String getFaxNo() {
		return faxNo;
	}

	public void setFaxNo(String faxNo) {
		this.faxNo = faxNo;
	}

	public String getEffDateFrom() {
		return effDateFrom;
	}

	public void setEffDateFrom(String effDateFrom) {
		this.effDateFrom = effDateFrom;
	}

	public String getEffDateTo() {
		return effDateTo;
	}

	public void setEffDateTo(String effDateTo) {
		this.effDateTo = effDateTo;
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
