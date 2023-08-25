package com.fms.pfc.repository.main.api;

import java.util.Date;
import java.util.List;

import javax.transaction.Transactional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.fms.pfc.domain.model.main.PrIngredient;

@Repository
public interface PrIngRepository extends JpaRepository<PrIngredient, String> {

	@Query(value = "SELECT A.* FROM PR_INGREDIENT A "
			+ "WHERE 1=1 "
			+ "AND A.PARENT_REF_ID = 0 "
			+ "AND (?1 = 0 OR A.PR_ID = ?1) "
			+ "AND (?2 = 0 OR EXISTS (SELECT B.REF_ID FROM PR_INGREDIENT B"
			+ "			 			  WHERE A.PR_ID = B.PR_ID"
			+ "			 			  AND (B.REF_TYPE = 201 AND B.REF_ID = ?2))) "
			+ "AND (?3 = 0 OR EXISTS (SELECT C.REF_ID FROM PR_INGREDIENT C "
			+ "			 			  WHERE A.PR_ID = C.PR_ID"
			+ "						  AND (C.REF_TYPE = 301 AND C.REF_ID = ?3))) ", nativeQuery = true)
	List<PrIngredient> searchPrIngredient(int prId, int rawMatlRefId, int prRefId);
	
	@Query(value = "SELECT A.* FROM PR_INGREDIENT A "
			+ "WHERE A.PR_ID = ?1 "
			+ "AND A.PARENT_REF_ID = 0 "
			+ "AND (?2 = 0 OR EXISTS (SELECT B.REF_ID FROM PR_INGREDIENT B"
			+ "			 			  WHERE A.PR_ID = B.PR_ID"
			+ "			 			  AND (B.REF_TYPE = 201 AND B.REF_ID in (?4)))) "
			+ "AND (?3 = 0 OR EXISTS (SELECT C.REF_ID FROM PR_INGREDIENT C "
			+ "			 			  WHERE A.PR_ID = C.PR_ID"
			+ "						  AND (C.REF_TYPE = 301 AND C.REF_ID in (?5)))) ", nativeQuery = true)
	List<PrIngredient> searchPrIngredient(int prId, int rawMatlRefId, int prRefId, List<Integer> rmIds, List<Integer> prIds);

	@Modifying
	@Transactional
	@Query(value = "INSERT INTO PR_INGREDIENT (PR_ID, PARENT_REF_ID, REF_ID, REF_TYPE, ING_PERC, SEL_TS_NO, SEQ_NO, "
			+ "ADDITIVE_DESC, CREATOR_ID, CREATED_DATETIME, ALT_RM) "
			+ "VALUES "
			+ "((SELECT PR_ID FROM PRODUCT_RECIPE WHERE PR_CODE = ?1), ?2, ?3, ?4, ?5, ?6, ?7, "
			+ "?8, ?9, ?10, ?11)", nativeQuery = true)
	void addPrIngredient(String prCode, int parentRefId, int refId, int refType, double ingPerc, String selTsNo, 
			int seqNo, String additiveDesc, String creatorId, Date date, String altRm);

	@Modifying
	@Transactional
	@Query(value = "DELETE FROM PR_INGREDIENT WHERE PR_ID = ?1", nativeQuery = true)
	void delPrIngredient(int prId);

	@Modifying
	@Transactional
	@Query(value = "DELETE FROM PR_INGREDIENT WHERE PR_ING_ID = ?1", nativeQuery = true)
	void delDetailPrIngredient(int prIngId);

	@Modifying
	@Transactional
	@Query(value = "UPDATE PR_INGREDIENT SET REF_ID = ?2, REF_TYPE = ?3, ING_PERC = ?4, SEL_TS_NO = ?5, "
			+ " MODIFIER_ID = ?6, MODIFIED_DATETIME = ?7, ALT_RM = ?8 "
			+ "WHERE PR_ING_ID = ?1", nativeQuery = true)
	void updPrIngredient(int prIngId, int refId, int refType, double ingPrec, String selTsNo, String modifierId, Date date, String altRm);
}
