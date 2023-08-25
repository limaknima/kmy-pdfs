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
@Table(name = "PR_STAT")
public class Compliance {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "PR_STAT_ID")
	private Integer prStatId;

	@Transient
	private Integer count = 0;
	
	@Column(name = "PR_ID")
	private Integer prId;

	@Column(name = "PR_NAME")
	private String productName; 
	
	@Column(name = "FINAL_STATUS")
	private Integer finalStatus;
	
	@Transient
	private String statusName;
	
	@Column(name = "COUNTRY_ID")
	private String countryId;
		
	@Column(name = "COUNTRY_NAME") 
	private String countryName;
	 
	
	public Integer getPrStatId() {
		return prStatId;
	}

	public void setPrStatId(Integer prStatId) {
		this.prStatId = prStatId;
	}

	public Integer getPrId() {
		return prId;
	}

	public void setPrId(Integer prId) {
		this.prId = prId;
	}

	public Integer getCount() {
		return count;
	}

	public void setCount(Integer count) {
		this.count = count;
	}

	public String getProductName() {
		return productName;
	}

	public void setProductName(String productName) {
		this.productName = productName;
	}

	public Integer getFinalStatus() {
		return finalStatus;
	}

	public void setFinalStatus(Integer finalStatus) {
		this.finalStatus = finalStatus;
	}

	public String getStatusName() {
		return statusName;
	}

	public void setStatusName(String statusName) {
		this.statusName = statusName;
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

}
