package com.fms.pfc.domain.dto.main;

import java.util.Date;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PrIngredientDto {
	private int prIngId;
	private int prId;
	private int parentRefId;
	private int refId;
	private int refType;
	private Double ingPerc;
	private String selTsNo;
	private int seqNo;
	private String additiveDesc;
	private String creatorId;
	private Date createdDate;
	private String modifierId;
	private Date modifiedDate;
	private String altRm;
	private String categoryName;
	private String ingredientName;
	private int prIngRowNo;
	private String ingStatus;
	private String altRmNames;
	private String rmFlavStatusName;
	private String newRmFlavStatusName;
}
