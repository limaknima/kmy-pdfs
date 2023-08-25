package com.fms.pfc.domain.model.main;

import java.io.Serializable;
import java.util.Objects;

import javax.persistence.Column;
import javax.persistence.Id;

public class RawMatlReportPk implements Serializable {
	
	private int rawMatlIdRow;
	private String tsNoRow;
//	private String matlNameRow;
	private String rmIngIdRow;
//	private String ingredientRow;
//	private String compositionRow;
//	private String vipdDateRow;
//	private String suppDecDateRow;
	private String suppRow;
	private String manuRow;
//	private String insNoRow;
//	private String eNoRow;
//	private String femaNoRow;
//	private String jecfaNoRow;
//	private String casNoRow;
//	private String gmoStatusRow;
//	private String flavorStatusRow;
//	private String phoFlagRow;
//	private String rmManfIdRow;
//	private String rmStatIdRow;
//	private String vipdStatusRow;
//	private String finalStatusRow;
	private String countryRow;
//	private String regulationRow;
//	private String referenceRow;
//	private String remarksRow;
	
	public RawMatlReportPk() {
    }

    public RawMatlReportPk(int rawMatlIdRow, String tsNoRow, String matlNameRow, String rmIngIdRow, 
    		String ingredientRow, String compositionRow, String vipdDateRow, String suppDecDateRow, 
    		String suppRow, String manuRow, String insNoRow, String eNoRow, 
    		String femaNoRow, String jecfaNoRow, String casNoRow, String gmoStatusRow,
    		String flavorStatusRow, String phoFlagRow, String rmManfIdRow, String rmStatIdRow, 
    		String vipdStatusRow, String finalStatusRow, String countryRow, String regulationRow, 
    		String referenceRow, String remarksRow) {
    	this.rawMatlIdRow = rawMatlIdRow;
        this.tsNoRow = tsNoRow;
//        this.matlNameRow = matlNameRow;
        this.rmIngIdRow = rmIngIdRow;
//        this.ingredientRow = ingredientRow;
//        this.compositionRow = compositionRow;
//        this.vipdDateRow = vipdDateRow;
//        this.suppDecDateRow = suppDecDateRow;
        this.suppRow = suppRow;
        this.manuRow = manuRow;
//        this.insNoRow = insNoRow;
//        this.eNoRow = eNoRow;
//        this.femaNoRow = femaNoRow;
//        this.jecfaNoRow = jecfaNoRow;
//        this.casNoRow = casNoRow;
//        this.gmoStatusRow = gmoStatusRow;
//        this.flavorStatusRow = flavorStatusRow;
//        this.phoFlagRow = phoFlagRow;
//        this.rmManfIdRow = rmManfIdRow;
//        this.rmStatIdRow = rmStatIdRow;
//        this.vipdStatusRow = vipdStatusRow;
//        this.finalStatusRow = finalStatusRow;
        this.countryRow = countryRow;
//        this.regulationRow = regulationRow;
//        this.referenceRow = referenceRow;
//        this.remarksRow = remarksRow;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RawMatlReportPk rawMatlReportPk = (RawMatlReportPk) o;
        return (rawMatlIdRow == rawMatlReportPk.rawMatlIdRow) &&  
        		tsNoRow.equals(rawMatlReportPk.tsNoRow) &&  
//        		matlNameRow.equals(rawMatlReportPk.matlNameRow) &&  
        		rmIngIdRow.equals(rawMatlReportPk.rmIngIdRow) &&  
//        		ingredientRow.equals(rawMatlReportPk.ingredientRow) &&  
//        		compositionRow.equals(rawMatlReportPk.compositionRow) &&  
//        		vipdDateRow.equals(rawMatlReportPk.vipdDateRow) &&  
//        		suppDecDateRow.equals(rawMatlReportPk.suppDecDateRow) &&  
        		suppRow.equals(rawMatlReportPk.suppRow) &&  
        		manuRow.equals(rawMatlReportPk.manuRow)   
//        		&& insNoRow.equals(rawMatlReportPk.insNoRow) &&  
//        		eNoRow.equals(rawMatlReportPk.eNoRow) &&  
//        		femaNoRow.equals(rawMatlReportPk.femaNoRow) &&  
//        		jecfaNoRow.equals(rawMatlReportPk.jecfaNoRow) &&  
//        		casNoRow.equals(rawMatlReportPk.casNoRow) &&  
//        		gmoStatusRow.equals(rawMatlReportPk.gmoStatusRow) &&  
//        		flavorStatusRow.equals(rawMatlReportPk.flavorStatusRow) &&  
//        		phoFlagRow.equals(rawMatlReportPk.phoFlagRow) &&  
//        		rmManfIdRow.equals(rawMatlReportPk.rmManfIdRow) &&  
//        		rmStatIdRow.equals(rawMatlReportPk.rmStatIdRow) &&  
//        		vipdStatusRow.equals(rawMatlReportPk.vipdStatusRow) &&  
//        		finalStatusRow.equals(rawMatlReportPk.finalStatusRow) &&  
//        		countryRow.equals(rawMatlReportPk.countryRow) &&  
//        		regulationRow.equals(rawMatlReportPk.regulationRow) &&  
//        		referenceRow.equals(rawMatlReportPk.referenceRow) &&  
//        		remarksRow.equals(rawMatlReportPk.remarksRow)
        		;
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(rawMatlIdRow, tsNoRow, 
//        		matlNameRow, 
        		rmIngIdRow, 
//        		ingredientRow, compositionRow, vipdDateRow, suppDecDateRow, 
        		suppRow, manuRow
//        		, insNoRow, eNoRow, 
//        		femaNoRow, jecfaNoRow, casNoRow, gmoStatusRow,
//        		flavorStatusRow, phoFlagRow, rmManfIdRow, rmStatIdRow, 
//        		vipdStatusRow, finalStatusRow, countryRow, regulationRow, 
//        		referenceRow, remarksRow
        		);
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

//	public String getMatlNameRow() {
//		return matlNameRow;
//	}
//	public void setMatlNameRow(String matlNameRow) {
//		this.matlNameRow = matlNameRow;
//	}

	public String getRmIngIdRow() {
		return rmIngIdRow;
	}
	public void setRmIngIdRow(String rmIngIdRow) {
		this.rmIngIdRow = rmIngIdRow;
	}

//	public String getIngredientRow() {
//		return ingredientRow;
//	}
//	public void setIngredientRow(String ingredientRow) {
//		this.ingredientRow = ingredientRow;
//	}
//
//	public String getCompositionRow() {
//		return compositionRow;
//	}
//	public void setCompositionRow(String compositionRow) {
//		this.compositionRow = compositionRow;
//	}
//
//	public String getVipdDateRow() {
//		return vipdDateRow;
//	}
//	public void setVipdDateRow(String vipdDateRow) {
//		this.vipdDateRow = vipdDateRow;
//	}
//
//	public String getSuppDecDateRow() {
//		return suppDecDateRow;
//	}
//	public void setSuppDecDateRow(String suppDecDateRow) {
//		this.suppDecDateRow = suppDecDateRow;
//	}

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

//	public String getInsNoRow() {
//		return insNoRow;
//	}
//	public void setInsNoRow(String insNoRow) {
//		this.insNoRow = insNoRow;
//	}
//
//	public String geteNoRow() {
//		return eNoRow;
//	}
//	public void seteNoRow(String eNoRow) {
//		this.eNoRow = eNoRow;
//	}
//
//	public String getFemaNoRow() {
//		return femaNoRow;
//	}
//	public void setFemaNoRow(String femaNoRow) {
//		this.femaNoRow = femaNoRow;
//	}
//
//	public String getJecfaNoRow() {
//		return jecfaNoRow;
//	}
//	public void setJecfaNoRow(String jecfaNoRow) {
//		this.jecfaNoRow = jecfaNoRow;
//	}
//
//	public String getCasNoRow() {
//		return casNoRow;
//	}
//	public void setCasNoRow(String casNoRow) {
//		this.casNoRow = casNoRow;
//	}
//
//	public String getGmoStatusRow() {
//		return gmoStatusRow;
//	}
//	public void setGmoStatusRow(String gmoStatusRow) {
//		this.gmoStatusRow = gmoStatusRow;
//	}
//
//	public String getFlavorStatusRow() {
//		return flavorStatusRow;
//	}
//	public void setFlavorStatusRow(String flavorStatusRow) {
//		this.flavorStatusRow = flavorStatusRow;
//	}
//
//	public String getPhoFlagRow() {
//		return phoFlagRow;
//	}
//	public void setPhoFlagRow(String phoFlagRow) {
//		this.phoFlagRow = phoFlagRow;
//	}
//
//	public String getRmManfIdRow() {
//		return rmManfIdRow;
//	}
//	public void setRmManfIdRow(String rmManfIdRow) {
//		this.rmManfIdRow = rmManfIdRow;
//	}
//
//	public String getRmStatIdRow() {
//		return rmStatIdRow;
//	}
//	public void setRmStatIdRow(String rmStatIdRow) {
//		this.rmStatIdRow = rmStatIdRow;
//	}
//
//	public String getVipdStatusRow() {
//		return vipdStatusRow;
//	}
//	public void setVipdStatusRow(String vipdStatusRow) {
//		this.vipdStatusRow = vipdStatusRow;
//	}
//
//	public String getFinalStatusRow() {
//		return finalStatusRow;
//	}
//	public void setFinalStatusRow(String finalStatusRow) {
//		this.finalStatusRow = finalStatusRow;
//	}

	public String getCountryRow() {
		return countryRow;
	}
	public void setCountryRow(String countryRow) {
		this.countryRow = countryRow;
	}
//
//	public String getRegulationRow() {
//		return regulationRow;
//	}
//	public void setRegulationRow(String regulationRow) {
//		this.regulationRow = regulationRow;
//	}
//
//	public String getReferenceRow() {
//		return referenceRow;
//	}
//	public void setReferenceRow(String referenceRow) {
//		this.referenceRow = referenceRow;
//	}
//
//	public String getRemarksRow() {
//		return remarksRow;
//	}
//	public void setRemarksRow(String remarksRow) {
//		this.remarksRow = remarksRow;
//	}

}
