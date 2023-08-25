package com.fms.pfc.domain.model.main;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "prName")
public class PrNameDto {
	
	@Id
	private String idx;
	private int type;
	private int prId;
	private String prCode;
	private String prName;
}
