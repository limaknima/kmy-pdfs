package com.fms.pfc.repository.main.api;


import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.fms.pfc.domain.model.main.ProductRecipeList;

@Repository
public interface ProductRecipeListRepository extends JpaRepository<ProductRecipeList, String> {

	@Query(value = "WITH CTE_TableName3 AS "
			+ "( "
			+ "SELECT A.PR_ID, 							 				  "
			+ "	CASE 											  "
			+ "		WHEN A.REF_TYPE = '201' THEN C.RAW_MATL_NAME "
			+ "		WHEN A.REF_TYPE = '301' THEN B.PR_NAME	  "
			+ "	ELSE '' END AS INGREDIENT_NAME FROM PR_INGREDIENT A  "
			+ "		LEFT JOIN PRODUCT_RECIPE B 			  "
			+ "		ON A.REF_ID = B.PR_ID 			  "
			+ "	LEFT JOIN RAW_MATERIAL C 			  "
			+ "		ON A.REF_ID = C.RAW_MATL_ID 	  "
			+ "), "
			+ "CTE_TableName2 AS  "
			+ "( "
			+ "SELECT PR_ID,  "
			+ "FINAL_STATUS  "
			+ "FROM PR_STAT "
			+ "), "
			+ " CTE_TableName AS  "
			+ "( "
			+ "SELECT A.PR_ID, 	  "
			+ "A.PR_NAME, 			  "
			+ "A.PR_CODE, 			  "
			+ "B.INGREDIENT_NAME, 	  "
			+ "E.FINAL_STATUS, 		  "
			+ "A.CREATOR_ID, 		  "
			+ "A.RECORD_STATUS,		  "
			+ "F.ROLE_ID  "
			+ "FROM   							  "
			+ "	( SELECT 	A.PR_ID, 			  "
			+ "			 	A.PR_NAME,  		  "
			+ "			 	A.PR_CODE, 			  "
			+ "			 	A.CREATOR_ID,  		  "
			+ "			 	A.RECORD_STATUS 	  "
			+ "		FROM PRODUCT_RECIPE A 		  "
			+ "	JOIN PR_INGREDIENT B 			  "
			+ "		ON B.PR_ID = A.PR_ID 		  "
			+ "	JOIN RAW_MATERIAL C 				  "
			+ "		ON C.RAW_MATL_ID = B.REF_ID 	  "
			+ "		AND B.REF_TYPE = '201' 		  "
			+ "	WHERE ?11 = 1  "
			+ "	AND (?5 = '' OR (C.RAW_MATL_NAME LIKE CASE WHEN ?6 = '1' THEN CONCAT('%', ?5, '%')    "
			+ "			 			   						WHEN ?6 = '2' THEN CONCAT('%', ?5)  		  "
			+ "			 			   				       	WHEN ?6 = '3' THEN ?5   					  "
			+ "			 			   				       	WHEN ?6 = '4' THEN CONCAT(?5, '%') END)) 	  "
			+ "	UNION 						  "
			+ "	SELECT 	A.PR_ID, 		  "
			+ "			 	A.PR_NAME,   	  "
			+ "			 	A.PR_CODE,		  "
			+ "			 	A.CREATOR_ID,  	  "
			+ "			 	A.RECORD_STATUS  "
			+ "	FROM PRODUCT_RECIPE A 		  "
			+ "	JOIN PR_INGREDIENT B 		  "
			+ "		ON B.PR_ID = A.PR_ID 	  "
			+ "	JOIN PRODUCT_RECIPE C 		  "
			+ "		ON C.PR_ID = B.REF_ID 	  "
			+ "		AND B.REF_TYPE = '301' 	 	 "
			+ "	WHERE ?12 = 1  				  "
			+ "		AND (?7 = '' OR (C.PR_NAME LIKE CASE WHEN ?8 = '1' THEN CONCAT('%', ?7, '%')  "
			+ "			 			   				WHEN ?8 = '2' THEN CONCAT('%', ?7)   		  "
			+ "			 			   				WHEN ?8 = '3' THEN ?7   					  "
			+ "			 			   				WHEN ?8 = '4' THEN CONCAT(?7, '%') END))) A 		 "
			+ "	LEFT JOIN  										  "
			+ "		( "
			+ "SELECT t0.PR_ID, "
			+ "STUFF((  "
			+ "SELECT ',' + CONVERT(NVARCHAR(max), t1.FINAL_STATUS ) "
			+ "FROM CTE_TableName2 t1  "
			+ "WHERE t1.PR_ID = t0.PR_ID  "
			+ "FOR XML PATH('')), 1, LEN(','), '') AS FINAL_STATUS "
			+ "FROM CTE_TableName2 t0 "
			+ "GROUP BY  								  "
			+ "t0.PR_ID) E   								  "
			+ "		ON A.PR_ID = E.PR_ID  			    		  "
			+ "	LEFT JOIN USR_ROLE F 							  "
			+ "		ON A.CREATOR_ID = F.USER_ID 				  "
			+ "		AND F.USER_ID = ?10 						  "
			+ "	JOIN(  "
			+ "SELECT t0.PR_ID,  "
			+ "STUFF((  "
			+ "SELECT ',' + CONVERT(NVARCHAR(max), t1.INGREDIENT_NAME ) "
			+ "FROM CTE_TableName3 t1  "
			+ "WHERE t1.PR_ID = t0.PR_ID  "
			+ "FOR XML PATH('')), 1, LEN(','), '') AS INGREDIENT_NAME "
			+ "FROM CTE_TableName3 t0 "
			+ "GROUP BY  								  "
			+ "t0.PR_ID ) B 				  "
			+ "		ON A.PR_ID = B.PR_ID	 		  "
			+ "	WHERE  "
			+ "		(?1 = '' OR (A.PR_NAME LIKE CASE WHEN ?2 = '1' THEN CONCAT('%', ?1, '%')   	  "
			+ "			 			   				WHEN ?2 = '2' THEN CONCAT('%', ?1)   		  "
			+ "			 			          		WHEN ?2 = '3' THEN ?1   					  "
			+ "			 			          		WHEN ?2 = '4' THEN CONCAT(?1, '%') END))   	  "
			+ "	AND (?3 = '' OR (A.PR_CODE LIKE CASE WHEN ?4 = '1' THEN CONCAT('%', ?3, '%')   	  "
			+ "			 			   			WHEN ?4 = '2' THEN CONCAT('%', ?3)   			  "
			+ "			 			   			WHEN ?4 = '3' THEN ?3   						  "
			+ "			 			   			WHEN ?4 = '4' THEN CONCAT(?3, '%') END))  		  "
			+ "AND (?9 = 0 OR A.RECORD_STATUS = ?9)  	 				 "
			+ ") "
			+ "SELECT  "
			+ "t0.PR_ID, 	  "
			+ "t0.PR_NAME, 			  "
			+ "t0.PR_CODE, 			  "
			+ "t0.INGREDIENT_NAME, 	  "
			+ "t0.FINAL_STATUS, 		  "
			+ "t0.CREATOR_ID, 		  "
			+ "t0.RECORD_STATUS,  "
			+ "STUFF((  "
			+ "SELECT ',' + t1.ROLE_ID  "
			+ "FROM CTE_TableName t1  "
			+ "WHERE t1.PR_ID = t0.PR_ID  "
			+ "FOR XML PATH('')), 1, LEN(','), '') AS ROLE_ID "
			+ "FROM CTE_TableName t0 "
			+ "GROUP BY  								  "
			+ "t0.PR_ID, 								  "
			+ "t0.PR_NAME, 								  "
			+ "t0.PR_CODE, 			   				     "
			+ "t0.CREATOR_ID, 							  "
			+ "t0.RECORD_STATUS, 						  "
			+ "t0.INGREDIENT_NAME, 						  "
			+ "t0.FINAL_STATUS", nativeQuery = true)
	List<ProductRecipeList> searchProductRecipeList(String prName, String exp1, String prCode, String exp2, 
			String rawMatlName, String exp3, String interProdName, String exp4, int recordStatus, String currUser,
			int matFlag, int imPrFlag);

}
