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
@Table(name = "FILE_SZ_TYPE")
public class FileTypeSz implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 4110789339758666781L;
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "FTYPE_ID")
	private Integer pkFTypeId;

	@Column(name = "HPL_ID")
	private Integer fkHplId;

	@Column(name = "FILE_TYPE")
	private String fileType;

	@Column(name = "MIN_SIZE")
	private Integer minSize;
	
	@Column(name = "MAX_SIZE")
	private Integer maxSize;

	@Column(name = "CREATOR_ID")
	private String creatorId;

	@Column(name = "CREATED_DATETIME")
	private Date createdDatetime;

	@Column(name = "MODIFIER_ID")
	private String modifierId;

	@Column(name = "MODIFIED_DATETIME")
	private Date modifiedDatetime;
	
	@Column(name = "HPL")
	private String hpl;
}
