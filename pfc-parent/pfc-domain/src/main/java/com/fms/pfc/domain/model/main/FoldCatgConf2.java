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
@Table(name = "FOLD_CATG_CONF2")
public class FoldCatgConf2 implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -289178408322367982L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "CATG_ID")
	private Integer pkCatgId;

	@Column(name = "HPL")
	private String hpl;

	@Column(name = "PROD_FILE_FORMAT")
	private String prodFileFormat;

	@Column(name = "CREATOR_ID")
	private String creatorId;

	@Column(name = "CREATED_DATETIME")
	private Date createdDatetime;

	@Column(name = "MODIFIER_ID")
	private String modifierId;

	@Column(name = "MODIFIED_DATETIME")
	private Date modifiedDatetime;
	
}
