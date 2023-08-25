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
@Table(name = "LETTER_CONF")
public class LetterContConf implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 4927054126187571037L;
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "lt_conf_id", nullable = false)
	private Integer ltConfId;

	@Column(name = "lt_id", nullable = false)
	private Integer ltId;
	
	@Column(name = "record_type_id")
	private Integer recordTypeId;
	
	@Column(name = "sec1_desc")
	private String sec1Desc;
	
	@Column(name = "sec2_desc")
	private String sec2Desc;
	
	@Column(name = "sec3_desc")
	private String sec3Desc;
	
	@Column(name = "sec4_desc")
	private String sec4Desc;
	
	@Column(name = "sec5_desc")
	private String sec5Desc;
	
	@Column(name = "country_id")
	private String countryId;
	
	@Column(name = "creator_id")
	private String creatorId;

	@Column(name = "created_datetime")
	private Date createdDate;

	@Column(name = "modifier_id")
	private String modifierId;

	@Column(name = "modified_datetime")
	private Date modifiedDatetime;
	

	public LetterContConf() {
		super();
		// TODO Auto-generated constructor stub
	}	

	public LetterContConf(Integer ltConfId, Integer ltId, Integer recordTypeId, String sec1Desc, String sec2Desc,
			String sec3Desc, String sec4Desc, String sec5Desc, String countryId, String creatorId, Date createdDate,
			String modifierId, Date modifiedDatetime) {
		super();
		this.ltConfId = ltConfId;
		this.ltId = ltId;
		this.recordTypeId = recordTypeId;
		this.sec1Desc = sec1Desc;
		this.sec2Desc = sec2Desc;
		this.sec3Desc = sec3Desc;
		this.sec4Desc = sec4Desc;
		this.sec5Desc = sec5Desc;
		this.countryId = countryId;
		this.creatorId = creatorId;
		this.createdDate = createdDate;
		this.modifierId = modifierId;
		this.modifiedDatetime = modifiedDatetime;
	}

	public Integer getLtConfId() {
		return ltConfId;
	}

	public void setLtConfId(Integer ltConfId) {
		this.ltConfId = ltConfId;
	}

	public Integer getLtId() {
		return ltId;
	}

	public void setLtId(Integer ltId) {
		this.ltId = ltId;
	}

	public Integer getRecordTypeId() {
		return recordTypeId;
	}

	public void setRecordTypeId(Integer recordTypeId) {
		this.recordTypeId = recordTypeId;
	}

	public String getSec1Desc() {
		return sec1Desc;
	}

	public void setSec1Desc(String sec1Desc) {
		this.sec1Desc = sec1Desc;
	}

	public String getSec2Desc() {
		return sec2Desc;
	}

	public void setSec2Desc(String sec2Desc) {
		this.sec2Desc = sec2Desc;
	}

	public String getSec3Desc() {
		return sec3Desc;
	}

	public void setSec3Desc(String sec3Desc) {
		this.sec3Desc = sec3Desc;
	}

	public String getSec4Desc() {
		return sec4Desc;
	}

	public void setSec4Desc(String sec4Desc) {
		this.sec4Desc = sec4Desc;
	}

	public String getSec5Desc() {
		return sec5Desc;
	}

	public void setSec5Desc(String sec5Desc) {
		this.sec5Desc = sec5Desc;
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

	public Date getModifiedDatetime() {
		return modifiedDatetime;
	}

	public void setModifiedDatetime(Date modifiedDatetime) {
		this.modifiedDatetime = modifiedDatetime;
	}	
	
}
