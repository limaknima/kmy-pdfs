package com.fms.pfc.domain.model.main;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.Table;

@Entity
@Table(name = "raw_material")
@IdClass(RawMatlReportPk.class)
public class RawMaterialReport implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 6210480274163905393L;

	@Id
	@Column(name = "raw_matl_id", nullable = true)
	private int rawMatlIdRow;
	
	@Id
	@Column(name = "manf_ts")
	private String tsNoRow;
	
	@Id
	@Column(name = "raw_matl_name")
	private String matlNameRow;

	@Id
	@Column(name = "rm_ing_id")
	private String rmIngIdRow;
	
	@Id
	@Column(name = "ing_name")
	private String ingredientRow;
	
	@Id
	@Column(name = "comp_perc")
	private String compositionRow;
	
	@Id
	@Column(name = "vipd_date")
	private String vipdDateRow;

	@Id
	@Column(name = "vipd_annex2_date")
	private String suppDecDateRow;
	
	@Id
	@Column(name = "supplier")
	private String suppRow;
	
	@Id
	@Column(name = "manufacturer")
	private String manuRow;
	
	@Id
	@Column(name = "rm_ins")
	private String insNoRow;
	
	@Id
	@Column(name = "RM_ENO")
	private String eNoRow;
	
	@Id
	@Column(name = "rm_fema")
	private String femaNoRow;
	
	@Id
	@Column(name = "rm_jecfa")
	private String jecfaNoRow;
	
	@Id
	@Column(name = "rm_cas")
	private String casNoRow;
	
	@Id
	@Column(name = "rm_gmo")
	private String gmoStatusRow;
	
	@Id
	@Column(name = "rm_flav")
	private String flavorStatusRow;
	
	@Id
	@Column(name = "rm_pho")
	private String phoFlagRow;
	
	@Id
	@Column(name = "rm_manf_id")
	private String rmManfIdRow;
	
	@Id
	@Column(name = "rm_stat_id")
	private String rmStatIdRow;
	
	@Id
	@Column(name = "vipd_status")
	private String vipdStatusRow;
	
	@Id
	@Column(name = "final_status")
	private String finalStatusRow;
	
	@Id
	@Column(name = "country")
	private String countryRow;
	
	@Id
	@Column(name = "reg_name")
	private String regulationRow;
	
	@Id
	@Column(name = "ref_url1")
	private String referenceRow;
	
	@Id
	@Column(name = "remarks")
	private String remarksRow;
	
	public RawMaterialReport() {
		
	}
	
	public int getRawMatlIdRow() {
		return rawMatlIdRow;
	}
	public void setRawMatlIdRow(int rawMatlIdRow) {
		this.rawMatlIdRow = rawMatlIdRow;
	}
	
	public String getTsNoRow() {
		return tsNoRow;
	}
	public void setTsNoRow(String tsNoRow) {
		this.tsNoRow = tsNoRow;
	}
	
	public String getMatlNameRow() {
		return matlNameRow;
	}
	public void setMatlNameRow(String matlNameRow) {
		this.matlNameRow = matlNameRow;
	}
	
	public String getRmIngIdRow() {
		return rmIngIdRow;
	}
	public void setRmIngIdRow(String rmIngIdRow) {
		this.rmIngIdRow = rmIngIdRow;
	}
	
	public String getIngredientRow() {
		return ingredientRow;
	}
	public void setIngredientRow(String ingredientRow) {
		this.ingredientRow = ingredientRow;
	}
	
	public String getCompositionRow() {
		return compositionRow;
	}
	public void setCompositionRow(String compositionRow) {
		this.compositionRow = compositionRow;
	}
	
	public String getVipdDateRow() {
		return vipdDateRow;
	}
	public void setVipdDateRow(String vipdDateRow) {
		this.vipdDateRow = vipdDateRow;
	}
	
	public String getSuppDecDateRow() {
		return suppDecDateRow;
	}
	public void setSuppDecDateRow(String suppDecDateRow) {
		this.suppDecDateRow = suppDecDateRow;
	}
	
	public String getSuppRow() {
		return suppRow;
	}
	public void setSuppRow(String suppRow) {
		this.suppRow = suppRow;
	}
	
	public String getManuRow() {
		return manuRow;
	}
	public void setManuRow(String manuRow) {
		this.manuRow = manuRow;
	}
	
	public String getInsNoRow() {
		return insNoRow;
	}
	public void setInsNoRow(String insNoRow) {
		this.insNoRow = insNoRow;
	}
	
	public String geteNoRow() {
		return eNoRow;
	}
	public void seteNoRow(String eNoRow) {
		this.eNoRow = eNoRow;
	}
	
	public String getFemaNoRow() {
		return femaNoRow;
	}
	public void setFemaNoRow(String femaNoRow) {
		this.femaNoRow = femaNoRow;
	}
	
	public String getJecfaNoRow() {
		return jecfaNoRow;
	}
	public void setJecfaNoRow(String jecfaNoRow) {
		this.jecfaNoRow = jecfaNoRow;
	}
	
	public String getCasNoRow() {
		return casNoRow;
	}
	public void setCasNoRow(String casNoRow) {
		this.casNoRow = casNoRow;
	}
	
	public String getGmoStatusRow() {
		return gmoStatusRow;
	}
	public void setGmoStatusRow(String gmoStatusRow) {
		this.gmoStatusRow = gmoStatusRow;
	}
	
	public String getFlavorStatusRow() {
		return flavorStatusRow;
	}
	public void setFlavorStatusRow(String flavorStatusRow) {
		this.flavorStatusRow = flavorStatusRow;
	}
	
	public String getPhoFlagRow() {
		return phoFlagRow;
	}
	public void setPhoFlagRow(String phoFlagRow) {
		this.phoFlagRow = phoFlagRow;
	}
		
	public String getRmManfIdRow() {
		return rmManfIdRow;
	}
	public void setRmManfIdRow(String rmManfIdRow) {
		this.rmManfIdRow = rmManfIdRow;
	}	
	
	public String getrmStatIdRow() {
		return rmStatIdRow;
	}
	public void setRmStatIdRow(String rmStatIdRow) {
		this.rmStatIdRow = rmStatIdRow;
	}
	
	public String getVipdStatusRow() {
		return vipdStatusRow;
	}
	public void setVipdStatusRow(String vipdStatusRow) {
		this.vipdStatusRow = vipdStatusRow;
	}
	
	public String getFinalStatusRow() {
		return finalStatusRow;
	}
	public void setFinalStatusRow(String finalStatusRow) {
		this.finalStatusRow = finalStatusRow;
	}
	
	public String getCountryRow() {
		return countryRow;
	}
	public void setCountryRow(String countryRow) {
		this.countryRow = countryRow;
	}
	
	public String getRegulationRow() {
		return regulationRow;
	}
	public void setRegulationRow(String regulationRow) {
		this.regulationRow = regulationRow;
	}
	
	public String getReferenceRow() {
		return referenceRow;
	}
	public void setReferenceRow(String referenceRow) {
		this.referenceRow = referenceRow;
	}
	
	public String getRemarksRow() {
		return remarksRow;
	}
	public void setRemarksRow(String remarksRow) {
		this.remarksRow = remarksRow;
	}
}
