package com.fms.pfc.domain.model.main;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

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
@Table(name = "REGULATION_DOC")
public class ReglDoc implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 8561918992441363046L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "REG_DOC_ID")
	private int regDocId;

	@Column(name = "REG_ID")
	private int regId;

	@Column(name = "REG_DOC_CAT_ID")
	private int regDocCatId;

	@Column(name = "REG_DOC_CAT_GRP_ID")
	private int regDocCatGrpId;

	@Column(name = "FILE_NAME")
	private String fileName;

	@Column(name = "FILE_PATH")
	private String filePath;

	@Column(name = "DOC_TYPE")
	private String docType;

	@Column(name = "CONTENT_OBJECT", length=1000)
	private byte[] contentObject;

	@Column(name = "ARCHIVED_FLAG")
	private String archivedFlag;

	@Column(name = "VERSION")
	private int version;

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
	
	//Start Kimberley 20210109
	@Transient
	private int rowNo;
	@Transient
	private String regDocCatName;
	@Transient
	private String regDocCatGrpName;
	@Transient
	private String indicator;
	//End Kimberley 20210109
	@Transient
	private String fileNameDis;
	
	@Transient
	public String getArchiveDesc() {
		return (this.archivedFlag.equals("Y")?"Yes":"No");
	}
	
//	@ManyToOne
//	@JoinColumn(name = "REG_ID", insertable = false, updatable = false)
//	private Regl regl;

}
