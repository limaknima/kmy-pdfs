package com.fms.pfc.domain.model.main;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "REGULATION")
public class RegulationRawMaterial {

	@Id
	@Column(name = "RAW_MATL_NAME")
	private String rawMatlName;

	public String getRawMatlName() {
		return rawMatlName;
	}

	public void setRawMatlName(String rawMatlName) {
		this.rawMatlName = rawMatlName;
	}

}
