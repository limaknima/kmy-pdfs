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
public class PrRemarks implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 2373142692935063974L;
	
	private Integer remarksId;
	private Integer prId;
	private String remarks;
	private String userId;
	private Date createdDatetime;


}
