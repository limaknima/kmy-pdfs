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
@Table(name = "regl_file")
public class ReglFile implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = -3347387516407269638L;
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "REGL_FILE_ID")
	private Integer reglFileId;

	@Column(name = "DOC_CAT_ID")
	private Integer docCatId;

	@Column(name = "DOC_CAT_GRP_ID")
	private Integer docCatGrpId;

	@Column(name = "FILE_NAME")
	private String fileName;

	@Column(name = "FILE_PATH")
	private String filePath;
	
	@Column(name = "COUNTRY_ID")
	private String countryId;

	@Column(name = "DOC_TYPE")
	private String docType;

	@Column(name = "CONTENT_OBJECT", length=1000)
	private byte[] contentObject;

	@Column(name = "ARCHIVED_FLAG")
	private String archivedFlag;

	@Column(name = "VERSION")
	private Integer version;

	@Column(name = "CRC_VALUE")
	private Long crcValue;

	@Column(name = "CREATOR_ID")
	private String creatorId;

	@Column(name = "CREATED_DATETIME")
	private Date createdDatetime;

	@Column(name = "MODIFIER_ID")
	private String modifierId;

	@Column(name = "MODIFIED_DATETIME")
	private Date modifiedDatetime;
	
	@Transient
	private String docCatName;
	@Transient
	private String docCatGrpName;
	@Transient
	private String countryName;
	

}
