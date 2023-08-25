package com.fms.pfc.domain.model;

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
@Table(name = "REF_FILE_TYPE")
public class FileType implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 5605543452789732136L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "FT_ID")
	private String pkFtId;

	@Column(name = "FT_NAME")
	private String ftName;

	@Column(name = "CREATOR_ID")
	private String creatorId;

	@Column(name = "CREATED_DATETIME")
	private Date createdDatetime;

	@Column(name = "MODIFIER_ID")
	private String modifierId;

	@Column(name = "MODIFIED_DATETIME")
	private Date modifiedDatetime;

}
