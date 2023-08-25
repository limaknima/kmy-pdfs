package com.fms.pfc.domain.model.main;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

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
@Table(name = "regulation")
public class Regl implements Serializable{	
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -9058913382368941532L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "reg_id")
	private int regId;
		
	@Column(name = "raw_matl_id")
	private int rawMatlId;
	
	@Column(name = "country_id")
	private String countryId;
	
	@Column(name = "reg_name")
	private String regName;
	
	@Column(name = "ref_url1")
	private String refUrl1;
	
	@Column(name = "ref_url2")
	private String refUrl2;
	
	@Column(name = "ref_url3")
	private String refUrl3;
	
	@Column(name = "ref_url4")
	private String refUrl4;
	
	@Column(name = "ref_url5")
	private String refUrl5;
	
	@Column(name = "remarks")
	private String remarks;
	
	@Column(name = "creator_id")
	private String creatorId;
	
	@Column(name = "created_datetime")
	private Date createdDateTime;
	
	@Column(name = "modifier_id")
	private String modifierId;
	
	@Column(name = "modified_datetime")
	private Date modifiedDateTime;
	
	@Transient
	private String countryName;
	
	@Transient
	private String rawMatlName;
	@Transient
	private String fileName;	
	@Transient
	private List<ReglDoc> regDocList;
	@Transient
	private String regDocCatId;
	@Transient
	private String regDocCatGrpId;	
	
	//@OneToMany(mappedBy = "regl", cascade = CascadeType.ALL, orphanRemoval = true)
	//@JoinColumn(name = "reg_id", insertable=false, updatable=false, referencedColumnName = "reg_id")
	//private List<ReglDoc> reglDocItems = new ArrayList<>();

}
