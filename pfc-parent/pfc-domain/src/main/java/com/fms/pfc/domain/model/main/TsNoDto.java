package com.fms.pfc.domain.model.main;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Entity
@Table(name = "tsNo")
public class TsNoDto {
	
	@Id
	private String idx;
	private int recType;
	private String tsNo;
}
