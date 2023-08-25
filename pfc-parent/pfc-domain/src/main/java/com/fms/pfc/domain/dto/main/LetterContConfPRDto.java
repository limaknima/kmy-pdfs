package com.fms.pfc.domain.dto.main;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class LetterContConfPRDto {
	private Integer ltConfPrId;
	private Integer ltConfId;
	private Integer prId;
	private String prName;
}
