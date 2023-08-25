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
@Table(name = "rm_stat")
public class RmStat {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "rm_stat_id")
	private int rmStatId;

	@Column(name = "raw_matl_id")
	private int rawMatlId;

	@Column(name = "manf_id")
	private int manfId;

	@Column(name = "vipd_status")
	private int vipdStatus;

	@Column(name = "final_status")
	private int finalStatus;

	@Column(name = "country_id")
	private String countryId;

	@Column(name = "creator_id")
	private String creatorId;

	@Column(name = "created_datetime")
	private Date createdDate;

	@Column(name = "modifier_id")
	private String modifierId;

	@Column(name = "modified_datetime")
	private Date modifiedDate;

	@Column(name = "data_chg_flag")
	private String dataChange;
	
	@Transient
	private String countryName;

	@Transient
	private String finalStatusDesc;

	@Transient
	private String rmStatRecStatus;

	@Transient
	private int rmStatRowNo;

	@Transient
	private boolean manfIdChanged = false;
	
	@Transient
	private boolean dataChgBool = false;

	public int getRmStatId() {
		return rmStatId;
	}

	public void setRmStatId(int rmStatId) {
		this.rmStatId = rmStatId;
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

	public int getVipdStatus() {
		return vipdStatus;
	}

	public void setVipdStatus(int vipdStatus) {
		this.vipdStatus = vipdStatus;
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

	public String getCountryName() {
		return countryName;
	}

	public void setCountryName(String countryName) {
		this.countryName = countryName;
	}

	public String getFinalStatusDesc() {
		return finalStatusDesc;
	}

	public void setFinalStatusDesc(String finalStatusDesc) {
		this.finalStatusDesc = finalStatusDesc;
	}

	public String getRmStatRecStatus() {
		return rmStatRecStatus;
	}

	public void setRmStatRecStatus(String rmStatRecStatus) {
		this.rmStatRecStatus = rmStatRecStatus;
	}

	public int getRmStatRowNo() {
		return rmStatRowNo;
	}

	public void setRmStatRowNo(int rmStatRowNo) {
		this.rmStatRowNo = rmStatRowNo;
	}

	public String getDataChange() {
		return dataChange;
	}

	public void setDataChange(String dataChange) {
		this.dataChange = dataChange;
	}

	public boolean isManfIdChanged() {
		return manfIdChanged;
	}

	public void setManfIdChanged(boolean manfIdChanged) {
		this.manfIdChanged = manfIdChanged;
	}

	public boolean isDataChgBool() {
		
		if(this.getDataChange().equals("Y"))
			dataChgBool = true;
		
		return dataChgBool;
	}

	public void setDataChgBool(boolean dataChgBool) {
		this.dataChgBool = dataChgBool;
	}

}
