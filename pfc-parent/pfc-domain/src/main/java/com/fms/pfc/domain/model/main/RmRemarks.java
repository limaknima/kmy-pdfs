package com.fms.pfc.domain.model.main;

import java.io.Serializable;
import java.util.Date;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class RmRemarks implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -2767583247773827580L;
	
	private Integer remarksId;
	private Integer rawMatlId;
	private String remarks;
	private String userId;
	private Date createdDatetime;

}
