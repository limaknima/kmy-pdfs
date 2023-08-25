package com.fms.pfc.domain.model.main;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "rm_manf_suppl")
public class RmManfSuppl {

	@Column(name = "rm_manf_id")
	private int rmManfId;

	@Id
	@Column(name = "suppl_id")
	private int supplId;
	
	private String vendorName;

	public int getRmManfId() {
		return rmManfId;
	}

	public void setRmManfId(int rmManfId) {
		this.rmManfId = rmManfId;
	}

	public int getSupplId() {
		return supplId;
	}

	public void setSupplId(int supplId) {
		this.supplId = supplId;
	}

	public String getVendorName() {
		return vendorName;
	}

	public void setVendorName(String vendorName) {
		this.vendorName = vendorName;
	}

}
