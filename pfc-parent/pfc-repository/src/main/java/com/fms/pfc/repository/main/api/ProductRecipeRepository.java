package com.fms.pfc.repository.main.api;

import java.util.Date;
import java.util.List;

import javax.transaction.Transactional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.fms.pfc.domain.model.main.ProductRecipe;

@Repository
public interface ProductRecipeRepository extends JpaRepository<ProductRecipe, String> {

	@Query(value = "SELECT * FROM PRODUCT_RECIPE "
			+ "WHERE (?5 = 0 OR PR_ID = ?5) "
			+ "AND (?1 = '' OR (LOWER(PR_NAME) LIKE CASE WHEN ?2 = '1' THEN LOWER(CONCAT('%', ?1, '%')) "
			+ "						 			  WHEN ?2 = '2' THEN LOWER(CONCAT('%', ?1)) "
			+ "        				  			  WHEN ?2 = '3' THEN LOWER(?1) "
			+ "        				   			  WHEN ?2 = '4' THEN LOWER(CONCAT(?1, '%')) END)) "
			+ "AND (?3 = '' OR (LOWER(PR_CODE) LIKE CASE WHEN ?4 = '1' THEN LOWER(CONCAT('%', ?3, '%')) "
			+ "						 			  WHEN ?4 = '2' THEN LOWER(CONCAT('%', ?3)) "
			+ "        				  			  WHEN ?4 = '3' THEN LOWER(?3) "
			+ "        				   			  WHEN ?4 = '4' THEN LOWER(CONCAT(?3, '%')) END)) "
			+ "AND (?6 = 0 OR RECORD_STATUS = ?6)", nativeQuery = true)
	List<ProductRecipe> searchProductRecipe(String prName, String exp1, String prCode, String exp2, int prId,
			int recordStatus);
	
	@Query(value = "SELECT * FROM PRODUCT_RECIPE "
			+ "WHERE (?5 = 0 OR PR_ID = ?5) "
			+ "AND (?1 = '' OR (LOWER(PR_NAME) LIKE CASE WHEN ?2 = '1' THEN LOWER(CONCAT('%', ?1, '%')) "
			+ "						 			  WHEN ?2 = '2' THEN LOWER(CONCAT('%', ?1)) "
			+ "        				  			  WHEN ?2 = '3' THEN LOWER(?1) "
			+ "        				   			  WHEN ?2 = '4' THEN LOWER(CONCAT(?1, '%')) END)) "
			+ "AND (?3 = '' OR (LOWER(PR_CODE) LIKE CASE WHEN ?4 = '1' THEN LOWER(CONCAT('%', ?3, '%')) "
			+ "						 			  WHEN ?4 = '2' THEN LOWER(CONCAT('%', ?3)) "
			+ "        				  			  WHEN ?4 = '3' THEN LOWER(?3) "
			+ "        				   			  WHEN ?4 = '4' THEN LOWER(CONCAT(?3, '%')) END)) "
			+ "AND (?6 = 0 OR RECORD_STATUS = ?6) "
			+ "AND (?7 = '' OR (LOWER(concat(PR_OTHER_NAME1,PR_OTHER_NAME2,PR_OTHER_NAME3,PR_OTHER_NAME4,PR_OTHER_NAME5)) LIKE CASE WHEN ?8 = '1' THEN LOWER(CONCAT('%', ?7, '%')) "
			+ "						 			  WHEN ?8 = '2' THEN LOWER(CONCAT('%', ?7)) "
			+ "        				  			  WHEN ?8 = '3' THEN LOWER(?7) "
			+ "        				   			  WHEN ?8 = '4' THEN LOWER(CONCAT(?7, '%')) END)) "
			, nativeQuery = true)
	List<ProductRecipe> searchProductRecipe2(String prName, String exp1, String prCode, String exp2, int prId,
			int recordStatus, String otherName, String otherNameOpt);

	@Query(value = "SELECT * FROM PRODUCT_RECIPE A "
			+ "INNER JOIN PR_INGREDIENT B "
			+ "ON A.PR_ID = B.REF_ID "
			+ "AND B.REF_TYPE = 301 "
			+ "WHERE (?5 = 0 OR A.PR_ID = ?5) "
			+ "AND ( ?1 = '' OR (LOWER(A.PR_NAME) LIKE CASE WHEN ?2 = '1' THEN LOWER(CONCAT('%', ?1, '%')) "
			+ "			 						WHEN ?2 = '2' THEN LOWER(CONCAT('%', ?1)) "
			+ "			         				WHEN ?2 = '3' THEN LOWER(?1) "
			+ "			         				WHEN ?2 = '4' THEN LOWER(CONCAT(?1, '%')) END)) "
			+ "AND (?3 = '' OR (LOWER(A.PR_CODE) LIKE CASE WHEN ?4 = '1' THEN LOWER(CONCAT('%', ?3, '%')) "
			+ "			 						WHEN ?4 = '2' THEN LOWER(CONCAT('%', ?3)) "
			+ "			         				WHEN ?4 = '3' THEN LOWER(?3) "
			+ "			         				WHEN ?4 = '4' THEN LOWER(CONCAT(?3, '%')) END)) "
			+ "AND (?6 = 0 OR A.RECORD_STATUS = ?6) ", nativeQuery = true)
	List<ProductRecipe> searchIntermediateProduct(String prName, String exp1, String prCode, String exp2, int prId,
			int recordStatus);
	
	@Query(value = "SELECT * FROM PRODUCT_RECIPE "
			+ "WHERE (?1 = '' OR (lower(PR_CODE) LIKE CASE WHEN ?2 = '1' THEN lower(CONCAT('%', ?1, '%')) "
			+ "						 			  		   WHEN ?2 = '2' THEN lower(CONCAT('%', ?1)) "
			+ "      		   				  			   WHEN ?2 = '3' THEN lower(?1) "
			+ "        				   			  		   WHEN ?2 = '4' THEN lower(CONCAT(?1, '%')) END)) "
			, nativeQuery = true)
	List<ProductRecipe> searchProductId(String prCode, String para);
	
	@Query(value = "SELECT * FROM PRODUCT_RECIPE "
			+ "WHERE (?1 = '' OR (lower(PR_NAME) LIKE CASE WHEN ?2 = '1' THEN lower(CONCAT('%', ?1, '%')) "
			+ "						 			  		   WHEN ?2 = '2' THEN lower(CONCAT('%', ?1)) "
			+ "      		   				  			   WHEN ?2 = '3' THEN lower(?1) "
			+ "        				   			  		   WHEN ?2 = '4' THEN lower(CONCAT(?1, '%')) END)) "
			, nativeQuery = true)
	List<ProductRecipe> searchProductIdByName(String prName, String para);
	
	@Query(value = "SELECT * FROM PRODUCT_RECIPE "
			+ "WHERE (?1 = '' OR (lower(PR_ID) LIKE CASE WHEN ?2 = '1' THEN lower(CONCAT('%', ?1, '%')) "
			+ "						 			  		   WHEN ?2 = '2' THEN lower(CONCAT('%', ?1)) "
			+ "      		   				  			   WHEN ?2 = '3' THEN lower(?1) "
			+ "        				   			  		   WHEN ?2 = '4' THEN lower(CONCAT(?1, '%')) END)) "
			, nativeQuery = true)
	List<ProductRecipe> searchProductName(int prId, String para);

	@Query(value = "SELECT A.* FROM PRODUCT_RECIPE A "
			+ "WHERE EXISTS(SELECT B.PR_ID FROM PR_INGREDIENT B "
			+ "				WHERE A.PR_ID = B.PR_ID "
			+ "				AND B.REF_TYPE = 201 "
			+ "				AND B.REF_ID = ?1) "
			+ "AND A.RECORD_STATUS = 3 ", nativeQuery = true)
	List<ProductRecipe> searchProductLinkage(int rawMatlId);
	
	@Query(value = " SELECT A.PR_ID, A.PR_CODE, A.PR_NAME FROM PRODUCT_RECIPE A "
			+ "INNER JOIN PR_INGREDIENT B "
			+ "ON A.PR_ID = B.REF_ID "
			+ "WHERE B.REF_TYPE = 301 "
			+ "GROUP BY A.PR_ID, A.PR_CODE, A.PR_NAME "
			+ "ORDER BY A.PR_NAME ", nativeQuery = true)
	List<ProductRecipe> searchIntermediate();
	
	@Query(value = " SELECT E.* FROM PRODUCT_RECIPE E "
			+ "WHERE E.PR_CODE = (CASE WHEN 	   ?1 = '' THEN E.PR_CODE "
			+ "		   				   WHEN E.PR_CODE = ?1 THEN ?1 END) "
			+ "AND E.PR_NAME   = (CASE WHEN 	   ?2 = '' THEN E.PR_NAME "
			+ "		    			   WHEN E.PR_NAME = ?2 THEN ?2 END) "
			+ "AND ( "
			+ " ?5 = '' OR ("
			+ "	E.PR_OTHER_NAME1 = (CASE WHEN ?5 = '' THEN E.PR_OTHER_NAME1 WHEN E.PR_OTHER_NAME1 = ?5 THEN ?5 END)"
			+ "	OR E.PR_OTHER_NAME2 = (CASE WHEN ?5 = '' THEN E.PR_OTHER_NAME2 WHEN E.PR_OTHER_NAME2 = ?5 THEN ?5 END)"
			+ "	OR E.PR_OTHER_NAME3 = (CASE WHEN ?5 = '' THEN E.PR_OTHER_NAME3 WHEN E.PR_OTHER_NAME3 = ?5 THEN ?5 END)"
			+ "	OR E.PR_OTHER_NAME4 = (CASE WHEN ?5 = '' THEN E.PR_OTHER_NAME4 WHEN E.PR_OTHER_NAME4 = ?5 THEN ?5 END)"
			+ "	OR E.PR_OTHER_NAME5 = (CASE WHEN ?5 = '' THEN E.PR_OTHER_NAME5 WHEN E.PR_OTHER_NAME5 = ?5 THEN ?5 END) )"
			+ ") "
			+ "AND EXISTS (SELECT * FROM PR_INGREDIENT A INNER JOIN RAW_MATERIAL B "
			+ "ON  A.PR_ID 	  = E.PR_ID "
			+ "AND A.REF_ID   = B.RAW_MATL_ID "
			+ "AND A.REF_TYPE = '201' "
			+ "WHERE B.RAW_MATL_NAME = (CASE WHEN 			?3 	  = '' THEN B.RAW_MATL_NAME "
			+ "								 WHEN B.RAW_MATL_NAME = ?3 THEN ?3 END)) "
			+ "AND (?4 = '' OR EXISTS ( SELECT C.*, D.PR_NAME FROM PR_INGREDIENT C INNER JOIN PRODUCT_RECIPE D "
			+ "ON C.PR_ID = E.PR_ID "
			+ "AND C.REF_ID 	= D.PR_ID "			
			+ "AND C.REF_TYPE 	= '301' "
			+ "WHERE D.PR_NAME 	= (CASE WHEN 		?4 = '' THEN D.PR_NAME "
			+ "							WHEN D.PR_NAME = ?4 THEN ?4 END))) "
			, nativeQuery = true)
	List<ProductRecipe> searchProductReport(String prCode , String prName, String rmName, String ipName, String prOtherName);
	
	@Modifying
	@Transactional
	@Query(value = "DELETE FROM PRODUCT_RECIPE WHERE PR_ID = ?1", nativeQuery = true)
	void delProductRecipe(int prId);

	@Modifying
	@Transactional
	@Query(value = "INSERT INTO PRODUCT_RECIPE "
			+ "(PR_CODE, PR_NAME, REMARKS, REMARKS_USER_ID, TOTAL_PERC, RECORD_STATUS, CREATOR_ID, CREATED_DATETIME,"
			+ "PR_OTHER_NAME1, PR_OTHER_NAME2, PR_OTHER_NAME3, PR_OTHER_NAME4, PR_OTHER_NAME5, OPT_LOCK, FLAVOR_STATUS_ID) "
			+ "VALUES "
			+ "(?1, ?2, ?3, ?4, ?5, ?6, ?7, ?13, ?8, ?9, ?10, ?11, ?12, 1, ?14)", nativeQuery = true)
	void addProductRecipe(String prCode, String prName, String remarks, String remarksUserId, double totalPerc, 
			int recordStatus, String creatorId, String otherName1, String otherName2, String otherName3, String otherName4,
			String otherName5, Date date, Integer flavStatus);
	
	@Modifying
	@Transactional
	@Query(value = "UPDATE PRODUCT_RECIPE SET PR_CODE = ?2, PR_NAME = ?3, REMARKS = ?4, REMARKS_USER_ID = ?5, TOTAL_PERC = ?6, "
			+ "RECORD_STATUS = ?7, MODIFIER_ID = ?8, MODIFIED_DATETIME = ?14, PR_OTHER_NAME1 = ?9, PR_OTHER_NAME2 = ?10, "
			+ "PR_OTHER_NAME3 = ?11, PR_OTHER_NAME4 = ?12, PR_OTHER_NAME5 = ?13 "
			+ "WHERE PR_ID = ?1", nativeQuery = true)
	void updProductRecipe(int prId, String prCode, String prName, String remarks, String remarksUserId, double totalPerc,
			int recordStatus, String modifierId, String otherName1, String otherName2, String otherName3, String otherName4,
			String otherName5, Date date);
	
	@Modifying
	@Transactional
	@Query(value = "UPDATE PRODUCT_RECIPE SET RECORD_STATUS = ?2, MODIFIER_ID = ?3, MODIFIED_DATETIME = ?4, "
			+ "CHECKER_ID = (CASE WHEN ?2 = 7 THEN ?3 WHEN ?2 != 7 THEN CHECKER_ID END), "
			+ "SUBMITTED_DATETIME = (CASE WHEN ?2 = 7 THEN ?4 WHEN ?2 != 7 THEN SUBMITTED_DATETIME END),"
			+ "REMARKS = ?5 "
			+ "WHERE PR_ID = ?1", nativeQuery = true)
	void updProductRecipeStatus(int prId, int recordStatus, String modifierId, Date date, String remarks);
	
	@Query(value = "WITH INGREDIENT AS ( "
			+ "	SELECT "
			+ "		I.PR_CODE, "
			+ "		I.PR_NAME, "
			+ "		I.PR_ID, "
			+ "		RW.RAW_MATL_NAME, "
			+ "		PR.PR_NAME AS INTER_MEDIATE_PRODUCT "
			+ "	FROM ( "
			+ "		SELECT "
			+ "			PR.PR_CODE, "
			+ "			PR.PR_NAME, "
			+ "			PRI.PR_ID, "
			+ "			PRI.REF_ID, "
			+ "			PRI.REF_TYPE "
			+ "		FROM PRODUCT_RECIPE PR "
			+ "		LEFT JOIN PR_INGREDIENT PRI ON PRI.PR_ID = PR.PR_ID "
			+ "		LEFT JOIN RAW_MATERIAL RW ON RW.RAW_MATL_ID = PRI.REF_ID "
			+ "	) I "
			+ "	LEFT JOIN RAW_MATERIAL RW ON RW.RAW_MATL_ID = I.REF_ID AND I.REF_TYPE = ?1 "
			+ "	LEFT JOIN PRODUCT_RECIPE PR ON PR.PR_ID = I.REF_ID AND I.REF_TYPE = ?2 "
			+ ") "
			+ "SELECT DISTINCT(INGREDIENT.PR_ID) FROM INGREDIENT "
			+ "WHERE 1=1 " 
			+ "AND ('' = ?3 OR PR_CODE = ?3 ) "
			+ "AND ('' = ?4 OR PR_NAME = ?4 ) " , nativeQuery = true)
	List<Integer> getProductRecipeId(String ref1, String ref2, String prCode, String prName);
	
	@Query(value = "WITH INGREDIENT AS ( "
			+ "	SELECT "
			+ "		I.PR_CODE, "
			+ "		I.PR_NAME, "
			+ "		I.PR_ID, "
			+ "		RW.RAW_MATL_NAME, "
			+ "		PR.PR_NAME AS INTER_MEDIATE_PRODUCT "
			+ "	FROM ( "
			+ "		SELECT "
			+ "			PR.PR_CODE, "
			+ "			PR.PR_NAME, "
			+ "			PRI.PR_ID, "
			+ "			PRI.REF_ID, "
			+ "			PRI.REF_TYPE "
			+ "		FROM PRODUCT_RECIPE PR "
			+ "		LEFT JOIN PR_INGREDIENT PRI ON PRI.PR_ID = PR.PR_ID "
			+ "		LEFT JOIN RAW_MATERIAL RW ON RW.RAW_MATL_ID = PRI.REF_ID "
			+ "	) I "
			+ "	LEFT JOIN RAW_MATERIAL RW ON RW.RAW_MATL_ID = I.REF_ID AND I.REF_TYPE = ?1 "
			+ "	LEFT JOIN PRODUCT_RECIPE PR ON PR.PR_ID = I.REF_ID AND I.REF_TYPE = ?2 "
			+ ") "
			+ "SELECT DISTINCT(INGREDIENT.PR_ID) FROM INGREDIENT "
			+ "WHERE 1=1 " 
			+ "AND ('' = ?3 OR PR_CODE = ?3 ) "
			+ "AND ('' = ?4 OR PR_NAME = ?4 ) "
			+ "AND ('' = ?5 OR RAW_MATL_NAME = ?5 ) "
			+ "AND ('' = ?6 OR INTER_MEDIATE_PRODUCT = ?6 ) "
			+ "GROUP BY INGREDIENT.PR_ID", nativeQuery = true)
	List<Integer> getRecipeWithRawMatOrIntermediate(String ref1, String ref2, String prCode, String prName, String rmName, String ipName);
	
	@Query(value = "WITH INGREDIENT AS ( "
			+ "	SELECT "
			+ "		I.PR_CODE, "
			+ "		I.PR_NAME, "
			+ "		I.PR_ID, "
			+ "		RW.RAW_MATL_NAME, "
			+ "		PR.PR_NAME AS INTER_MEDIATE_PRODUCT "
			+ "	FROM ( "
			+ "		SELECT "
			+ "			PR.PR_CODE, "
			+ "			PR.PR_NAME, "
			+ "			PRI.PR_ID, "
			+ "			PRI.REF_ID, "
			+ "			PRI.REF_TYPE "
			+ "		FROM PRODUCT_RECIPE PR "
			+ "		LEFT JOIN PR_INGREDIENT PRI ON PRI.PR_ID = PR.PR_ID "
			+ "		LEFT JOIN RAW_MATERIAL RW ON RW.RAW_MATL_ID = PRI.REF_ID "
			+ "	) I "
			+ "	LEFT JOIN RAW_MATERIAL RW ON RW.RAW_MATL_ID = I.REF_ID AND I.REF_TYPE = ?1 "
			+ "	LEFT JOIN PRODUCT_RECIPE PR ON PR.PR_ID = I.REF_ID AND I.REF_TYPE = ?2 "
			+ ") "
			+ "SELECT DISTINCT(RM.PR_ID) "
			+ "FROM (Select * from INGREDIENT WHERE INGREDIENT.RAW_MATL_NAME = ?5 ) RM "
			+ "INNER JOIN (Select * from INGREDIENT WHERE INGREDIENT.INTER_MEDIATE_PRODUCT = ?6 ) IMP ON RM.PR_ID = IMP.PR_ID "
			+ "WHERE 1=1 " 
			+ "AND ('' = ?3 OR RM.PR_CODE = ?3 ) "
			+ "AND ('' = ?4 OR RM.PR_NAME = ?4 ) "
			+ "GROUP BY RM.PR_ID" , nativeQuery = true)
	List<Integer> getRecipeWithRawMatANDIntermediate(String ref1, String ref2, String prCode, String prName, String rmName, String ipName);
	
	public default String getPRSQL(List<Integer> pridList) {
		String prSQL = " SELECT PR.*, " 
				+ " IsNULL((SELECT U.USER_NAME "
				+ " FROM USR U WHERE U.USER_ID = PR.CREATOR_ID),'SYSTEM') AS USER_NAME "
				+ " FROM PRODUCT_RECIPE PR "
				+ " WHERE 1=1 "
				+ " AND PR.PR_ID IN (" +pridList + ")" ;		
		return prSQL;
	}
	
	@Modifying
	@Transactional
	@Query(value = "UPDATE PRODUCT_RECIPE SET OPT_LOCK = OPT_LOCK+1, PR_CODE = ?2, PR_NAME = ?3, REMARKS = ?4, REMARKS_USER_ID = ?5, TOTAL_PERC = ?6, "
			+ "RECORD_STATUS = ?7, MODIFIER_ID = ?8, MODIFIED_DATETIME = ?14, PR_OTHER_NAME1 = ?9, PR_OTHER_NAME2 = ?10, "
			+ "PR_OTHER_NAME3 = ?11, PR_OTHER_NAME4 = ?12, PR_OTHER_NAME5 = ?13, FLAVOR_STATUS_ID = ?16 "
			+ "WHERE PR_ID = ?1 AND (0 = ?15 OR OPT_LOCK = ?15)", nativeQuery = true)
	void updProductRecipeWithOptLock(int prId, String prCode, String prName, String remarks, String remarksUserId,
			double totalPerc, int recordStatus, String modifierId, String otherName1, String otherName2,
			String otherName3, String otherName4, String otherName5, Date date, Integer currentLock, Integer flavStatus);

	@Modifying
	@Transactional
	@Query(value = "UPDATE PRODUCT_RECIPE SET OPT_LOCK = OPT_LOCK+1, RECORD_STATUS = ?2, MODIFIER_ID = ?3, MODIFIED_DATETIME = ?4, "
			+ "CHECKER_ID = (CASE WHEN ?2 = 7 THEN ?3 WHEN ?2 != 7 THEN CHECKER_ID END), "
			+ "SUBMITTED_DATETIME = (CASE WHEN ?2 = 7 THEN ?4 WHEN ?2 != 7 THEN SUBMITTED_DATETIME END), "
			+ "REMARKS = ?6 "
			+ "WHERE PR_ID = ?1 AND (0 = ?5 OR OPT_LOCK = ?5)", nativeQuery = true)
	void updProductRecipeStatusWithOptLock(int prId, int recordStatus, String modifierId, Date date, Integer currentLock, String remarks);
	
	@Modifying
	@Transactional
	@Query(value = "UPDATE PRODUCT_RECIPE SET OPT_LOCK = OPT_LOCK+1, RECORD_STATUS = ?2, MODIFIER_ID = ?3, MODIFIED_DATETIME = ?4, "
			+ "CHECKER_ID = (CASE WHEN ?2 = 7 THEN ?3 WHEN ?2 != 7 THEN CHECKER_ID END), "
			+ "SUBMITTED_DATETIME = (CASE WHEN ?2 = 7 THEN ?4 WHEN ?2 != 7 THEN SUBMITTED_DATETIME END) "
			+ "WHERE PR_ID = ?1 AND (0 = ?5 OR OPT_LOCK = ?5)", nativeQuery = true)
	void updProductRecipeStatusWithOptLock(int prId, int recordStatus, String modifierId, Date date, Integer currentLock);
	
	@Modifying
	@Transactional
	@Query(value = "UPDATE PRODUCT_RECIPE SET "
			+ "ONHOLD_FLAG= ?2, REMARKS = ?5, MODIFIER_ID = ?3, MODIFIED_DATETIME = ?4 "
			+ "WHERE PR_ID = ?1 ", nativeQuery = true)
	void updatePrOnHoldFlag (int prId, String flag, String modifierId, Date date, String remarks);
	
	@Modifying
	@Transactional
	@Query(value = "UPDATE PRODUCT_RECIPE SET "
			+ "ONHOLD_FLAG= ?2, MODIFIER_ID = ?3, MODIFIED_DATETIME = ?4 "
			+ "WHERE PR_ID = ?1 ", nativeQuery = true)
	void updatePrOnHoldFlag (int prId, String flag, String modifierId, Date date);
	
	@Modifying
	@Transactional
	@Query(value = "UPDATE PRODUCT_RECIPE SET FLAVOR_STATUS_ID=?2, MODIFIER_ID=?3, MODIFIED_DATETIME=?4 WHERE PR_ID=?1", nativeQuery = true)
	void updProductRecipe(Integer prId, Integer fsId, String modifierId, Date date);
	
}
