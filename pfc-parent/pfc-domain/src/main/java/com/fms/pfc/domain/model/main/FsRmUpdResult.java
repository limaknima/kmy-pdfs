package com.fms.pfc.domain.model.main;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "FS_RM_UPD_RESULT")
public class FsRmUpdResult implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 3793520933913269495L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "UPD_RST_ID", nullable = false)
	private Integer updRstId;

	@Column(name = "UPD_ID")
	private Integer updId;

	@Column(name = "PR_ID")
	private Integer prId;

	@Column(name = "PR_FS_ID")
	private Integer prFsId;

	@Column(name = "NEW_PR_FS_ID")
	private Integer newPrFsId;

	public FsRmUpdResult() {
		super();
		// TODO Auto-generated constructor stub
	}

	public FsRmUpdResult(Integer updRstId, Integer updId, Integer prId, Integer prFsId, Integer newPrFsId) {
		super();
		this.updRstId = updRstId;
		this.updId = updId;
		this.prId = prId;
		this.prFsId = prFsId;
		this.newPrFsId = newPrFsId;
	}

	public Integer getUpdRstId() {
		return updRstId;
	}

	public void setUpdRstId(Integer updRstId) {
		this.updRstId = updRstId;
	}

	public Integer getUpdId() {
		return updId;
	}

	public void setUpdId(Integer updId) {
		this.updId = updId;
	}

	public Integer getPrId() {
		return prId;
	}

	public void setPrId(Integer prId) {
		this.prId = prId;
	}

	public Integer getPrFsId() {
		return prFsId;
	}

	public void setPrFsId(Integer prFsId) {
		this.prFsId = prFsId;
	}

	public Integer getNewPrFsId() {
		return newPrFsId;
	}

	public void setNewPrFsId(Integer newPrFsId) {
		this.newPrFsId = newPrFsId;
	}

}
