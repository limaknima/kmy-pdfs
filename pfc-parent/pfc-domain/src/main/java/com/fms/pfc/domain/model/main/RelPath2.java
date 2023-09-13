package com.fms.pfc.domain.model.main;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
@EqualsAndHashCode
@Entity
@Table(name = "REL_PATH2")
public class RelPath2 implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -285378929027384859L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "REL_ID")
	private Integer pkRelPathId;

	@Column(name = "CATG_ID")
	private Integer fkCatgId;
	
	@Column(name = "HMODEL")
	private String hModel;
	
	@Column(name = "YEAR")
	private String year;

	@Column(name = "MTH")
	private String mth;

	@Column(name = "DAY")
	private String day;

	@Column(name = "PROD_LN")
	private String prodLn;

	@Column(name = "SEQ")
	private String seq;

	@Column(name = "FILE_PATH")
	private String filePath;

	@Column(name = "PROD_FILE_FORMAT")
	private String prodFileFormat;
	
	@Column(name = "PROC_TYPE")
	private Integer procType;

	@Column(name = "CREATOR_ID")
	private String creatorId;

	@Column(name = "CREATED_DATETIME")
	private Date createdDatetime;

	@Column(name = "MODIFIER_ID")
	private String modifierId;

	@Column(name = "MODIFIED_DATETIME")
	private Date modifiedDatetime;

}
