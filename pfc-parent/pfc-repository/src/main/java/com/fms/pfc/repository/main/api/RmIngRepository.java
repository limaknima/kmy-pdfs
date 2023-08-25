package com.fms.pfc.repository.main.api;

import java.util.Date;
import java.util.List;

import javax.transaction.Transactional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.fms.pfc.domain.model.main.RmIngredient;

@Repository
public interface RmIngRepository extends JpaRepository<RmIngredient, String> {

	@Query(value = "SELECT A.*, V.VENDOR_NAME FROM RM_INGREDIENT A INNER JOIN VENDOR V "
			+ "ON V.VENDOR_ID = A.MANF_ID "
			+ "WHERE A.RAW_MATL_ID = ?1 ", nativeQuery = true)
	List<RmIngredient> searchRmIng(int rawMatlId);

	@Modifying
	@Transactional
	@Query(value = "INSERT INTO RM_INGREDIENT (RAW_MATL_ID, MANF_ID, ING_NAME, COMP_PERC, INS_NO, E_NO, FEMA_NO, "
			+ "JECFA_NO, CAS_NO, CREATOR_ID, CREATED_DATETIME) "
			+ "VALUES "
			+ "((SELECT RAW_MATL_ID FROM RAW_MATERIAL WHERE RAW_MATL_NAME = ?1), ?2, ?3, ?4, ?5, ?6, ?7, "
			+ "?8, ?9, ?10, ?11)", nativeQuery = true)
	void addRmIngredient(String matlName, int manfId, String ingName, String compPrec, String insNo, String eNo, String femaNo,
			String jecfaNo, String casNo, String creatorId, Date date);

	@Modifying
	@Transactional
	@Query(value = "DELETE FROM RM_INGREDIENT WHERE RAW_MATL_ID = ?1", nativeQuery = true)
	void delRmIngredient(int rawMatlId);

	@Modifying
	@Transactional
	@Query(value = "DELETE FROM RM_INGREDIENT WHERE RAW_MATL_ID = ?1 AND RM_ING_ID = ?2", nativeQuery = true)
	void delDetailRmIngredient(int rawMatlId, int rmIngId);

	@Modifying
	@Transactional
	@Query(value = "DELETE FROM RM_INGREDIENT WHERE RAW_MATL_ID = ?1 AND MANF_ID = ?2", nativeQuery = true)
	void delRmIngByManf(int rawMatlId, int manfId);

	@Modifying
	@Transactional
	@Query(value = "UPDATE RM_INGREDIENT SET MANF_ID = ?2, ING_NAME = ?3, COMP_PERC = ?4, INS_NO = ?5, E_NO = ?6, FEMA_NO = ?7, "
			+ "JECFA_NO = ?8, CAS_NO = ?9, MODIFIER_ID = ?10, MODIFIED_DATETIME = ?12 "
			+ "WHERE RAW_MATL_ID = ?1 AND RM_ING_ID = ?11", nativeQuery = true)
	void updRmIngredient(int rawMatlId, int manfId, String ingName, String compPrec, String insNo, String eNo, String femaNo,
			String jecfaNo, String casNo, String creatorId, int rmIngId, Date date);
}
