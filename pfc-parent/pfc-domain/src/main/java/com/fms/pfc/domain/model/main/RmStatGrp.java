package com.fms.pfc.domain.model.main;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

@Entity
@Table(name = "rm_stat")
public class RmStatGrp {

	@Column(name = "raw_matl_id")
	private int rawMatlId;

	@Column(name = "final_status")
	private int finalStatus;

	@Id
	@Column(name = "country_id")
	private String countryId;
	
	@Column(name = "data_chg_flag")
	private String dataChange;

	@Transient
	private int rmStatId;

	public int getRawMatlId() {
		return rawMatlId;
	}

	public void setRawMatlId(int rawMatlId) {
		this.rawMatlId = rawMatlId;
	}

	public int getFinalStatus() {
		return finalStatus;
	}

	public void setFinalStatus(int finalStatus) {
		this.finalStatus = finalStatus;
	}

	public String getCountryId() {
		return countryId;
	}

	public void setCountryId(String countryId) {
		this.countryId = countryId;
	}

	public int getRmStatId() {
		return rmStatId;
	}

	public void setRmStatId(int rmStatId) {
		this.rmStatId = rmStatId;
	}

	public String getDataChange() {
		return dataChange;
	}

	public void setDataChange(String dataChange) {
		this.dataChange = dataChange;
	}

}
