package com.fms.pfc.domain.model.main;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "PR_INGREDIENT")
public class PrIngredientAll implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Column(name = "PR_ID")
	private Integer prId;
	
	@Column(name = "PR_NAME")
	private String prName;
	
	@Column(name = "RAW_MATL_NAME")
	private String rawMatlName;
	
	@Column(name = "CATEGORY")
	private String category;
	
	@Column(name = "SEL_TS_NO")
	private String selTsNo;
	
	@Column(name = "INGREDIENT_NAME")
	private String ingredientName;
	
	@Column(name = "ALTNAMES")
	private String altNames;
	
	@Column(name = "FLVSTATNAME")
	private String flavStatName;
	
	@Column(name = "FLVSTATRANK")
	private Integer flavStatRank;
	
	@Column(name = "PARENT")
	private Integer parent;
	
	@Column(name = "LEVEL")
	private Integer level;
	
	@Id
	@Column(name = "SEQ")
	private String seq;
	
	@Column(name = "REF_ID")
	private Integer refId;
	
	@Column(name = "REF_TYPE")
	private Integer refType;
	
	@Column(name = "ING_PERC")
	private Double ingPerc;

	public PrIngredientAll() {
		super();
		// TODO Auto-generated constructor stub
	}

	public PrIngredientAll(Integer prId, String prName, String rawMatlName, String category, String selTsNo,
			String ingredientName, String altNames, String flavStatName, Integer flavStatRank, Integer parent,
			Integer level, String seq, Integer refId, Integer refType, Double ingPerc) {
		super();
		this.prId = prId;
		this.prName = prName;
		this.rawMatlName = rawMatlName;
		this.category = category;
		this.selTsNo = selTsNo;
		this.ingredientName = ingredientName;
		this.altNames = altNames;
		this.flavStatName = flavStatName;
		this.flavStatRank = flavStatRank;
		this.parent = parent;
		this.level = level;
		this.seq = seq;
		this.refId = refId;
		this.refType = refType;
		this.ingPerc = ingPerc;
	}

	public Integer getPrId() {
		return prId;
	}

	public void setPrId(Integer prId) {
		this.prId = prId;
	}

	public String getPrName() {
		return prName;
	}

	public void setPrName(String prName) {
		this.prName = prName;
	}

	public String getRawMatlName() {
		return rawMatlName;
	}

	public void setRawMatlName(String rawMatlName) {
		this.rawMatlName = rawMatlName;
	}

	public String getCategory() {
		return category;
	}

	public void setCategory(String category) {
		this.category = category;
	}

	public String getSelTsNo() {
		return selTsNo;
	}

	public void setSelTsNo(String selTsNo) {
		this.selTsNo = selTsNo;
	}

	public String getIngredientName() {
		return ingredientName;
	}

	public void setIngredientName(String ingredientName) {
		this.ingredientName = ingredientName;
	}

	public String getAltNames() {
		return altNames;
	}

	public void setAltNames(String altNames) {
		this.altNames = altNames;
	}

	public String getFlavStatName() {
		return flavStatName;
	}

	public void setFlavStatName(String flavStatName) {
		this.flavStatName = flavStatName;
	}

	public Integer getFlavStatRank() {
		return flavStatRank;
	}

	public void setFlavStatRank(Integer flavStatRank) {
		this.flavStatRank = flavStatRank;
	}

	public Integer getParent() {
		return parent;
	}

	public void setParent(Integer parent) {
		this.parent = parent;
	}

	public Integer getLevel() {
		return level;
	}

	public void setLevel(Integer level) {
		this.level = level;
	}

	public String getSeq() {
		return seq;
	}

	public void setSeq(String seq) {
		this.seq = seq;
	}

	public Integer getRefId() {
		return refId;
	}

	public void setRefId(Integer refId) {
		this.refId = refId;
	}

	public Integer getRefType() {
		return refType;
	}

	public void setRefType(Integer refType) {
		this.refType = refType;
	}

	public Double getIngPerc() {
		return ingPerc;
	}

	public void setIngPerc(Double ingPerc) {
		this.ingPerc = ingPerc;
	}
	
	
	
	
}
