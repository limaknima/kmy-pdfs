package com.fms.pfc.domain.model;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "USR")
public class Usr {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private int user_unique_id;

	@Column(name = "user_id", nullable = false)
	private String userId;

	@Column(name = "password", nullable = false)
	private String password;

	@Column(name = "user_name")
	private String userName;

	@Column(name = "org_id")
	private String orgId;

	@Column(name = "group_id")
	private String groupId;

	@Column(name = "email")
	private String email;

	@Column(name = "effective_date_from")
	private String effecDateFr;

	@Column(name = "effective_date_to")
	private String effecDateTo;

	@Column(name = "expiry_date")
	private String expDate;

	@Column(name = "alert_preference")
	private int alertPre;

	@Column(name = "creator_id")
	private String creatorId;

	@Column(name = "created_datetime")
	private Date createdDate;

	@Column(name = "modifier_id")
	private String modifierId;

	@Column(name = "modified_datetime")
	private Date modifiedDate;

	@Column(name = "disabled_flag")
	private String disabledFlag;

	@Column(name = "failed_attempt_count")
	private int failedAttemptCnt;

	@Column(name = "password_set_date")
	private Date passwordSetDate;

	public int getUser_unique_id() {
		return user_unique_id;
	}

	public void setUser_unique_id(int user_unique_id) {
		this.user_unique_id = user_unique_id;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getOrgId() {
		return orgId;
	}

	public void setOrgId(String orgId) {
		this.orgId = orgId;
	}

	public String getGroupId() {
		return groupId;
	}

	public void setGroupId(String groupId) {
		this.groupId = groupId;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getEffecDateFr() {
		return effecDateFr;
	}

	public void setEffecDateFr(String effecDateFr) {
		this.effecDateFr = effecDateFr;
	}

	public String getEffecDateTo() {
		return effecDateTo;
	}

	public void setEffecDateTo(String effecDateTo) {
		this.effecDateTo = effecDateTo;
	}

	public String getExpDate() {
		return expDate;
	}

	public void setExpDate(String expDate) {
		this.expDate = expDate;
	}

	public int getAlertPre() {
		return alertPre;
	}

	public void setAlertPre(int alertPre) {
		this.alertPre = alertPre;
	}

	public String getCreatorId() {
		return creatorId;
	}

	public void setCreatorId(String creatorId) {
		this.creatorId = creatorId;
	}

	public Date getCreatedDate() {
		return createdDate;
	}

	public void setCreatedDate(Date createdDate) {
		this.createdDate = createdDate;
	}

	public String getModifierId() {
		return modifierId;
	}

	public void setModifierId(String modifierId) {
		this.modifierId = modifierId;
	}

	public Date getModifiedDate() {
		return modifiedDate;
	}

	public void setModifiedDate(Date modifiedDate) {
		this.modifiedDate = modifiedDate;
	}

	public String getDisabledFlag() {
		return disabledFlag;
	}

	public void setDisabledFlag(String disabledFlag) {
		this.disabledFlag = disabledFlag;
	}

	public int getFailedAttemptCnt() {
		return failedAttemptCnt;
	}

	public void setFailedAttemptCnt(int failedAttemptCnt) {
		this.failedAttemptCnt = failedAttemptCnt;
	}

	public Date getPasswordSetDate() {
		return passwordSetDate;
	}

	public void setPasswordSetDate(Date passwordSetDate) {
		this.passwordSetDate = passwordSetDate;
	}
}
