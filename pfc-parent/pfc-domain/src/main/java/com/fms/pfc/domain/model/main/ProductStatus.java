package com.fms.pfc.domain.model.main;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "PR_STAT")
public class ProductStatus {
	
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "PR_STAT_ID")
	private Integer prStatId;
	
	@Column(name = "PR_ID")
	private Integer prId;
	
	@Column(name = "FINAL_STATUS")
	private String finalSta;
	
	@Column(name = "COUNTRY_ID")
	private String conId;
	
	@Column(name = "REMARKS")
	private String remark;

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

	public String getFinalSta() {
		return finalSta;
	}

	public void setFinalSta(String finalSta) {
		this.finalSta = finalSta;
	}

	public String getConId() {
		return conId;
	}

	public void setConId(String conId) {
		this.conId = conId;
	}

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}
	
}
