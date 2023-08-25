package com.fms.pfc.domain.model.main;


import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

@Entity
@Table(name = "PRODUCT_RECIPE")
public class ProductRecipeList {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "PR_ID")
	private Integer prId;

	@Column(name = "PR_CODE")
	private String prCode;
	
	@Column(name = "PR_NAME")
	private String prName;
	
	@Column(name = "RECORD_STATUS")
	private Integer recordStatus;
	
	@Column(name = "FINAL_STATUS")
	private String finalStatus;	
	
	@Column(name = "CREATOR_ID")
	private String creatorId;
	
	@Column(name = "ROLE_ID")
	private String roleId;
	
	@Column(name ="INGREDIENT_NAME")
	private String ingredientName;

	@Transient
	private Integer numberPermitted;

	@Transient
	private Integer numberNotPermitted;

	@Transient
	private Integer numberNotSure;
	
	@Transient
	private boolean permissionToDelete;
	

	public String getCreatorId() {
		return creatorId;
	}

	public void setCreatorId(String creatorId) {
		this.creatorId = creatorId;
	}

	public String getIngredientName() {
		return ingredientName;
	}

	public void setIngredientName(String ingredientName) {
		this.ingredientName = ingredientName;
	}

	public Integer getNumberPermitted() {
		return numberPermitted;
	}

	public void setNumberPermitted(Integer numberPermitted) {
		this.numberPermitted = numberPermitted;
	}

	public Integer getNumberNotPermitted() {
		return numberNotPermitted;
	}

	public void setNumberNotPermitted(Integer numberNotPermitted) {
		this.numberNotPermitted = numberNotPermitted;
	}

	public Integer getNumberNotSure() {
		return numberNotSure;
	}

	public void setNumberNotSure(Integer numberNotSure) {
		this.numberNotSure = numberNotSure;
	}

	public boolean isPermissionToDelete() {
		return permissionToDelete;
	}

	public void setPermissionToDelete(boolean permissionToDelete) {
		this.permissionToDelete = permissionToDelete;
	}
	
	public Integer getPrId() {
		return prId;
	}

	public void setPrId(Integer prId) {
		this.prId = prId;
	}

	public String getPrCode() {
		return prCode;
	}

	public void setPrCode(String prCode) {
		this.prCode = prCode;
	}

	public String getPrName() {
		return prName;
	}

	public void setPrName(String prName) {
		this.prName = prName;
	}

	public String getFinalStatus() {
		return finalStatus;
	}

	public void setFinalStatus(String finalStatus) {
		this.finalStatus = finalStatus;
	}

	public Integer getRecordStatus() {
		return recordStatus;
	}

	public void setRecordStatus(Integer recordStatus) {
		this.recordStatus = recordStatus;
	}

	public String getRoleId() {
		return roleId;
	}

	public void setRoleId(String roleId) {
		this.roleId = roleId;
	}	

}
