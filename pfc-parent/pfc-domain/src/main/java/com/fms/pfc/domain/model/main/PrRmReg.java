package com.fms.pfc.domain.model.main;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

@Entity
@Table(name = "PR_RM_REG")
public class PrRmReg {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "PR_RM_REG_ID")
	private int prRmStatId;

	@Column(name = "PR_ID")
	private int prId;

	@Column(name = "RAW_MATL_ID")
	private int rawMatlId;

	@Column(name = "COUNTRY_ID")
	private String countryId;

	@Column(name = "REG_DOC_ID")
	private int regDocId;

	@Transient
	private String prCode;

	public int getPrRmStatId() {
		return prRmStatId;
	}

	public void setPrRmStatId(int prRmStatId) {
		this.prRmStatId = prRmStatId;
	}

	public int getPrId() {
		return prId;
	}

	public void setPrId(int prId) {
		this.prId = prId;
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

	public int getRegDocId() {
		return regDocId;
	}

	public void setRegDocId(int regDocId) {
		this.regDocId = regDocId;
	}

	public String getPrCode() {
		return prCode;
	}

	public void setPrCode(String prCode) {
		this.prCode = prCode;
	}

}
