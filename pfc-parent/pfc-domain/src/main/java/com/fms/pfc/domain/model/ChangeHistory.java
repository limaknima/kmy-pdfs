package com.fms.pfc.domain.model;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "change_history_log")
public class ChangeHistory {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "change_history_id")
	private int changeHisId;
	
	@Column(name = "log_datetime")
	private Date logDateTime;
	
	@Column(name = "function_type")
	private int funcType;

	@Column(name = "table_name")
	private String tableName;
	
	@Column(name = "pk_value")
	private String pkValue;
	
	@Column(name = "field_name")
	private String fieldName;
	
	@Column(name = "old_value")
	private String oldValue;
	
	@Column(name = "new_value")
	private String newValue;
	
	@Column(name = "user_id")
	private String userId;
	
	@Column(name = "OLD_VAL_OBJ")
	private byte[] oldContentObj;
	
	@Column(name = "NEW_VAL_OBJ")
	private byte[] newContentObj;
	
	@Column(name = "RECORD_TYPE_ID")
	private int recTypeId;
	
	@Column(name = "RECORD_REF")
	private String recRef;

	public int getChangeHisId() {
		return changeHisId;
	}

	public void setChangeHisId(int changeHisId) {
		this.changeHisId = changeHisId;
	}

	public Date getLogDateTime() {
		return logDateTime;
	}

	public void setLogDateTime(Date logDateTime) {
		this.logDateTime = logDateTime;
	}

	public int getFuncType() {
		return funcType;
	}

	public void setFuncType(int funcType) {
		this.funcType = funcType;
	}

	public String getTableName() {
		return tableName;
	}

	public void setTableName(String tableName) {
		this.tableName = tableName;
	}

	public String getPkValue() {
		return pkValue;
	}

	public void setPkValue(String pkValue) {
		this.pkValue = pkValue;
	}

	public String getFieldName() {
		return fieldName;
	}

	public void setFieldName(String fieldName) {
		this.fieldName = fieldName;
	}

	public String getOldValue() {
		return oldValue;
	}

	public void setOldValue(String oldValue) {
		this.oldValue = oldValue;
	}

	public String getNewValue() {
		return newValue;
	}

	public void setNewValue(String newValue) {
		this.newValue = newValue;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public byte[] getOldContentObj() {
		return oldContentObj;
	}

	public void setOldContentObj(byte[] oldContentObj) {
		this.oldContentObj = oldContentObj;
	}

	public byte[] getNewContentObj() {
		return newContentObj;
	}

	public void setNewContentObj(byte[] newContentObj) {
		this.newContentObj = newContentObj;
	}

	public int getRecTypeId() {
		return recTypeId;
	}

	public void setRecTypeId(int recTypeId) {
		this.recTypeId = recTypeId;
	}

	public String getRecRef() {
		return recRef;
	}

	public void setRecRef(String recRef) {
		this.recRef = recRef;
	}
	
}
