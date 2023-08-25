package com.fms.pfc.domain.model.main;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "LETTER_TYPE")
public class LetterType implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = -3478062752370929255L;
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "lt_id", nullable = false)
	private Integer ltId;
	
	@Column(name = "lt_name", unique = true)
	private String ltName;
	
	@Column(name = "lt_desc", unique = true)
	private String ltDesc;
	
	@Column(name = "record_type_id", unique = true)
	private Integer recordTypeId;
	
	@Column(name = "creator_id")
	private String creatorId;

	@Column(name = "created_datetime")
	private Date createdDate;

	@Column(name = "modifier_id")
	private String modifierId;

	@Column(name = "modified_datetime")
	private Date modifiedDatetime;

	public LetterType() {
		super();
		// TODO Auto-generated constructor stub
	}

	public LetterType(Integer ltId, String ltName, String ltDesc, Integer recordTypeId, String creatorId,
			Date createdDate, String modifierId, Date modifiedDatetime) {
		super();
		this.ltId = ltId;
		this.ltName = ltName;
		this.ltDesc = ltDesc;
		this.recordTypeId = recordTypeId;
		this.creatorId = creatorId;
		this.createdDate = createdDate;
		this.modifierId = modifierId;
		this.modifiedDatetime = modifiedDatetime;
	}

	public Integer getLtId() {
		return ltId;
	}

	public void setLtId(Integer ltId) {
		this.ltId = ltId;
	}

	public String getLtName() {
		return ltName;
	}

	public void setLtName(String ltName) {
		this.ltName = ltName;
	}

	public String getLtDesc() {
		return ltDesc;
	}

	public void setLtDesc(String ltDesc) {
		this.ltDesc = ltDesc;
	}

	public Integer getRecordTypeId() {
		return recordTypeId;
	}

	public void setRecordTypeId(Integer recordTypeId) {
		this.recordTypeId = recordTypeId;
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
	
	

}
