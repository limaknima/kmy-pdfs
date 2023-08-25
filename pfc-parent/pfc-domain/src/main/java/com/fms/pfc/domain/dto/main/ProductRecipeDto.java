package com.fms.pfc.domain.dto.main;

import java.util.Date;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProductRecipeDto {

	private Integer prId;
	private String prCode;
	private String prName;
	private String remarks;
	private String remarksUserId;
	private Double totalPerc;
	private Integer recordStatus;
	private String creatorId;
	private Date createdDate;
	private String modifierId;
	private Date modifiedDate;
	private String checkerId;
	private Date submittedDate;
	private String prOtherName1;
	private String prOtherName2;
	private String prOtherName3;
	private String prOtherName4;
	private String prOtherName5;
	private Integer optLock;
	private String onHoldFlag;
	private Integer flavStatusId;
	private String ingredientName;
	private Integer numberPermitted;
	private Integer numberNotPermitted;
	private Integer numberNotSure;
	private Double ingPercentage;
	private Integer prRowNo;
	private String prodRecipeRecStatus;
	private boolean permissionToDelete;
	private String allOtherNames;
	private String flavStatusName;
	private Integer flavStatusRank;

	private Integer newFlavStatusId;
	private String newFlavStatusName;
	private Integer newFlavStatusRank;
}
