package com.fms.pfc.domain.model;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "ref_state")
public class State {
		
	private String stateId;
	private String countryId;
	private String stateName;
	private String nonMalFlag;
	private String creatorId;
	private Date createdDateTime;
	private String modifierId;
	private Date modifiedDateTime;
	
	public State() {
		
	}
	
	public State(String stateId, String countryId, String stateName, String nonMalFlag, String creatorId, Date createdDateTime, String modifierId, Date modifiedDateTime) {
		this.setStateId(stateId);
		this.setCountryId(countryId);
		this.setStateName(stateName);
		this.setNonMalFlag(nonMalFlag);
		this.setCreatorId(creatorId);
		this.setCreatedDateTime(createdDateTime);
		this.setModifierId(modifierId);
		this.setModifiedDateTime(modifiedDateTime);
	}

	@Id
	@Column(name = "state_id")
	public String getStateId() {
		return stateId;
	}
	public void setStateId(String stateId) {
		this.stateId = stateId;
	}

	@Column(name = "country_id")
	public String getCountryId() {
		return countryId;
	}
	public void setCountryId(String countryId) {
		this.countryId = countryId;
	}

	@Column(name = "state_name")
	public String getStateName() {
		return stateName;
	}
	public void setStateName(String stateName) {
		this.stateName = stateName;
	}

	@Column(name = "non_mal_flag")
	public String getNonMalFlag() {
		return nonMalFlag;
	}
	public void setNonMalFlag(String nonMalFlag) {
		this.nonMalFlag = nonMalFlag;
	}
	
	@Column(name = "creator_id")
	public String getCreatorId() {
		return creatorId;
	}
	public void setCreatorId(String creatorId) {
		this.creatorId = creatorId;
	}

	@Column(name = "created_datetime ")
	public Date getCreatedDateTime() {
		return createdDateTime;
	}
	public void setCreatedDateTime(Date createdDateTime) {
		this.createdDateTime = createdDateTime;
	}

	@Column(name = "modifier_id")
	public String getModifierId() {
		return modifierId;
	}
	public void setModifierId(String modifierId) {
		this.modifierId = modifierId;
	}

	@Column(name = "modified_datetime")
	public Date getModifiedDateTime() {
		return modifiedDateTime;
	}
	public void setModifiedDateTime(Date modifiedDateTime) {
		this.modifiedDateTime = modifiedDateTime;
	}

}
