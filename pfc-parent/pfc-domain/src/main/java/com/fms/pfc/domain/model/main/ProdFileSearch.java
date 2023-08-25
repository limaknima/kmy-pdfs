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
@Table(name = "PROD_FILE_SEARCH")
public class ProdFileSearch implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 5406427055193808651L;
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "PFILE_ID")
	private Integer pkPfileId;

	@Column(name = "HPL_ID")
	private Integer fkHplId;

	@Column(name = "HMODEL_ID")
	private Integer fkHplModelId;
	
	@Column(name = "G2_LOT_NO")
	private String g2Lot;

	@Column(name = "YEAR")
	private String year;

	@Column(name = "MTH")
	private String mth;

	@Column(name = "FILE_NAME")
	private String fileName;
	
	@Column(name = "FILE_PATH")
	private String filePath;
	
	@Column(name = "FILE_TYPE")
	private String fileType;

	@Column(name = "FILE_SIZE")
	private Integer fileSize;

	@Column(name = "CRC_VALUE")
	private Integer crcValue;

	@Column(name = "CREATOR_ID")
	private String creatorId;

	@Column(name = "CREATED_DATETIME")
	private Date createdDatetime;

	@Column(name = "MODIFIER_ID")
	private String modifierId;

	@Column(name = "MODIFIED_DATETIME")
	private Date modifiedDatetime;
	
	@Column(name = "HPL_NAME")
	private String hplName;
	
	@Column(name = "HPL_MODEL_NAME")
	private String hplModelName;

}
