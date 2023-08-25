package com.fms.pfc.domain.dto;

import java.util.Date;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ChangeHistoryDto {

	private int changeHisId;
	private Date logDateTime;
	private int funcType;
	private String tableName;
	private String pkValue;
	private String fieldName;
	private String oldValue;
	private String newValue;
	private String userId;
	private byte[] oldContentObj;
	private byte[] newContentObj;
	private int recTypeId;
	private String recRef;
}
