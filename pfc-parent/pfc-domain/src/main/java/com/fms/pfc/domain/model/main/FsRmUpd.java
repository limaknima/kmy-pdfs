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
@Table(name = "FS_RM_UPD")
public class FsRmUpd implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 5263727890512706557L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "UPD_ID", nullable = false)
	private Integer updId;

	@Column(name = "RAW_MATL_ID")
	private Integer rmId;

	@Column(name = "CURR_FS_ID")
	private Integer currFsId;

	@Column(name = "NEW_FS_ID")
	private Integer newFsId;

	@Column(name = "MAKER_ID")
	private String makerId;

	@Column(name = "CHECKER_ID")
	private String checkerId;

	@Column(name = "UPD_STATUS")
	private Integer updStatus;

	@Column(name = "CREATOR_ID")
	private String creatorId;

	@Column(name = "CREATED_DATETIME")
	private Date createdDatetime;

	@Column(name = "MODIFIER_ID")
	private String modifierId;

	@Column(name = "MODIFIED_DATETIME")
	private Date modifiedDatetime;

	public FsRmUpd() {
		super();
		// TODO Auto-generated constructor stub
	}

	public FsRmUpd(Integer updId, Integer rmId, Integer currFsId, Integer newFsId, String makerId, String checkerId,
			Integer updStatus, String creatorId, Date createdDatetime, String modifierId, Date modifiedDatetime) {
		super();
		this.updId = updId;
		this.rmId = rmId;
		this.currFsId = currFsId;
		this.newFsId = newFsId;
		this.makerId = makerId;
		this.checkerId = checkerId;
		this.updStatus = updStatus;
		this.creatorId = creatorId;
		this.createdDatetime = createdDatetime;
		this.modifierId = modifierId;
		this.modifiedDatetime = modifiedDatetime;
	}

	public Integer getUpdId() {
		return updId;
	}

	public void setUpdId(Integer updId) {
		this.updId = updId;
	}

	public Integer getRmId() {
		return rmId;
	}

	public void setRmId(Integer rmId) {
		this.rmId = rmId;
	}

	public Integer getCurrFsId() {
		return currFsId;
	}

	public void setCurrFsId(Integer currFsId) {
		this.currFsId = currFsId;
	}

	public Integer getNewFsId() {
		return newFsId;
	}

	public void setNewFsId(Integer newFsId) {
		this.newFsId = newFsId;
	}

	public String getMakerId() {
		return makerId;
	}

	public void setMakerId(String makerId) {
		this.makerId = makerId;
	}

	public String getCheckerId() {
		return checkerId;
	}

	public void setCheckerId(String checkerId) {
		this.checkerId = checkerId;
	}

	public Integer getUpdStatus() {
		return updStatus;
	}

	public void setUpdStatus(Integer updStatus) {
		this.updStatus = updStatus;
	}

	public String getCreatorId() {
		return creatorId;
	}

	public void setCreatorId(String creatorId) {
		this.creatorId = creatorId;
	}

	public Date getCreatedDatetime() {
		return createdDatetime;
	}

	public void setCreatedDatetime(Date createdDatetime) {
		this.createdDatetime = createdDatetime;
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
