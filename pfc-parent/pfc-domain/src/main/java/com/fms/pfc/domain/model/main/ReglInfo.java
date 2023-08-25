package com.fms.pfc.domain.model.main;

import java.io.Serializable;

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
@Table(name = "regl_info")
public class ReglInfo implements Serializable{	
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 5295040638045208456L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "regl_info_id")
	private Integer reglInfoId;
		
	@Column(name = "regl_file_id")
	private Integer reglFileId;
	
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
	
	@Column(name = "raw_matl_id")
	private Integer rmId;
	
	@Transient
	private String indicator;
	
}
