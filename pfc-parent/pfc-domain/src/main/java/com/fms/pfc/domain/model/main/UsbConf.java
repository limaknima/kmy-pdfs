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
@Table(name = "USB_CONF")
public class UsbConf implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 6558135815581878650L;
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "UCONF_ID")
	private Integer pkUconfId;

	@Column(name = "HPL_ID")
	private Integer fkHplId;

	@Column(name = "SERIAL_NO")
	private String serialNo;
	
	@Column(name = "NAME")
	private String name;
	
	@Column(name = "PROD_LN")
	private String prodLn;	

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
