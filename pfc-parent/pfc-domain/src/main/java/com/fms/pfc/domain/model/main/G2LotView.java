package com.fms.pfc.domain.model.main;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;

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
@Table(name = "CV22PDF")
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id")
public class G2LotView  implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 3814781762929596296L;
	
	@Id
	@Column(name = "LOT")
	private String lot;
	@Column(name = "HPL")
	private String hpl;
	@Column(name = "MODEL")
	private String model;
	@Column(name = "YEAR")
	private String year;
	@Column(name = "MTH")
	private String mth;
	@Column(name = "DDAY")
	private String day;
	@Column(name = "PROD_LN")
	private String prodLn;
	@Column(name = "SEQ")
	private String seq;

}
