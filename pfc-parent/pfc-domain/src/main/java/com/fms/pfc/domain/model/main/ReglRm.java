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
@Table(name = "regl_rm")
public class ReglRm implements Serializable{	

	/**
	 * 
	 */
	private static final long serialVersionUID = -74406003555676154L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "regl_rm_id")
	private Integer reglRmId;
		
	@Column(name = "regl_file_id")
	private Integer reglFileId;
	
	@Column(name = "raw_matl_id")
	private Integer rawMatlId;
	
	@Transient
	private String rmName;
	
}
