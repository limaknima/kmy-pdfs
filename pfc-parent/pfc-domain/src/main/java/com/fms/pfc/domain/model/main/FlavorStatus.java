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
@Table(name = "FLAVOR_STATUS")
public class FlavorStatus implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = -3033614398170811644L;
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "fs_id", nullable = false)
	private int fsId;
	
	@Column(name = "fs_name", unique = true)
	private String fsName;

	@Column(name = "fs_desc")
	private String fsDesc;
	
	@Column(name = "fs_rank")
	private int fsRank;

	@Column(name = "creator_id")
	private String creatorId;

	@Column(name = "created_datetime")
	private Date createdDate;

	@Column(name = "modifier_id")
	private String modifierId;

	@Column(name = "modified_datetime")
	private Date modifiedDatetime;
		

	public FlavorStatus() {
		super();
		// TODO Auto-generated constructor stub
	}

	public FlavorStatus(int fsId, String fsName, String fsDesc, int fsRank, String creatorId, Date createdDate,
			String modifierId, Date modifiedDatetime) {
		super();
		this.fsId = fsId;
		this.fsName = fsName;
		this.fsDesc = fsDesc;
		this.fsRank = fsRank;
		this.creatorId = creatorId;
		this.createdDate = createdDate;
		this.modifierId = modifierId;
		this.modifiedDatetime = modifiedDatetime;
	}

	public int getFsId() {
		return fsId;
	}

	public void setFsId(int fsId) {
		this.fsId = fsId;
	}

	public String getFsName() {
		return fsName;
	}

	public void setFsName(String fsName) {
		this.fsName = fsName;
	}

	public String getFsDesc() {
		return fsDesc;
	}

	public void setFsDesc(String fsDesc) {
		this.fsDesc = fsDesc;
	}

	public int getFsRank() {
		return fsRank;
	}

	public void setFsRank(int fsRank) {
		this.fsRank = fsRank;
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
