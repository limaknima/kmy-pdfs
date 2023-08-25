package com.fms.pfc.domain.dto;

import lombok.Data;

@Data
public class LabelAndValueDto {

	private String label;
	private int value;
	private String strValue;

	public LabelAndValueDto(String label, int value) {
		super();
		this.label = label;
		this.value = value;
	}

	public LabelAndValueDto(String label, String strValue) {
		super();
		this.label = label;
		this.strValue = strValue;
	}

}
