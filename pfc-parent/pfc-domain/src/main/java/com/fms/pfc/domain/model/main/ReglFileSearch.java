package com.fms.pfc.domain.model.main;

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
@Table(name = "regl_file_search")
public class ReglFileSearch {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "REGL_FILE_ID")
	private Integer reglFileId;

	@Column(name = "RMCOUNT")
	private Integer rmcount;

	@Column(name = "RMNAMES")
	private String rmnames;

	@Column(name = "COUNTRY")
	private String country;

	@Column(name = "FGROUP")
	private String fgroup;

	@Column(name = "FCATEGORY")
	private String fcategory;

	@Column(name = "FILE_NAME")
	private String fileName;

	@Column(name = "RMIDS")
	private String rmids;
	
	@Column(name = "RAW_MATL_ID")
	private Integer rawMatlId;
	
	@Column(name = "REG_NAME")
	private String regName;
	
	@Column(name = "REF_URL1")
	private String refUrl1;
	
	@Column(name = "REF_URL2")
	private String refUrl2;
	
	@Column(name = "REF_URL3")
	private String refUrl3;
	
	@Column(name = "REF_URL4")
	private String refUrl4;
	
	@Column(name = "REF_URL5")
	private String refUrl5;
	
	@Column(name = "REMARKS")
	private String remarks;
	
	@Column(name = "RM")
	private String infoRmName;
	
	

}
