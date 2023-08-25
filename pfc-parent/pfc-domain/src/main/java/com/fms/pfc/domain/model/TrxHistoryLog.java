package com.fms.pfc.domain.model;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@NoArgsConstructor
@ToString
@Entity
@Table(name = "trx_history_log")
public class TrxHistoryLog implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -4040775496554115463L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "log_id")
	private Integer logId;	

	@Column(name = "log_datetime")
	private Date logDateTime;

	@Column(name = "log_desc")
	private String logDesc;

	@Column(name = "user_id")
	private String userId;

	@Column(name = "function_type")
	private Integer funcType;

	@Column(name = "record_ref")
	private String recordRef;

	@Column(name = "record_type_id")
	private Integer recordType;

	@Column(name = "search_string")
	private String searchStr;
	
	public TrxHistoryLog(Date logDateTime, String logDesc, String userId, Integer funcType, String recordRef,
			Integer recordType, String searchStr) {
		super();
		this.logDateTime = logDateTime;
		this.logDesc = logDesc;
		this.userId = userId;
		this.funcType = funcType;
		this.recordRef = recordRef;
		this.recordType = recordType;
		this.searchStr = searchStr;
	}

}
