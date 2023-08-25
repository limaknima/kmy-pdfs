package com.fms.pfc.domain.dto.main;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class G2LotViewDto {

	private String lot;
	private String hpl;
	private String model;
	private String year;
	private String mth;
	private String day;
	private String prodLn;
	private String seq;
}
