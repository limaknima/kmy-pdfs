package com.fms.pfc.repository.base.api;

import java.util.List;

import javax.transaction.Transactional;

import com.fms.pfc.domain.model.Country;

import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;

@Repository
public interface CountryRepository extends JpaRepository<Country, String> {

	// Search query
	@Query(value = "SELECT * FROM REF_COUNTRY "
			+ "WHERE 1=1 "
			+ "AND (?1 = '' OR (COUNTRY_ID LIKE CASE WHEN ?3 = '1' THEN CONCAT('%', ?1, '%' )"
			+ " WHEN ?3 = '2' THEN CONCAT('%', ?1)"
			+ " WHEN ?3 = '3' THEN ?1"
			+ " WHEN ?3 = '4' THEN CONCAT(?1, '%') END ) ) "
			+ "AND (?2 = '' OR (COUNTRY_NAME LIKE CASE WHEN ?4 = '1' THEN CONCAT('%', ?2, '%')"
			+ " WHEN ?4 = '2' THEN CONCAT('%', ?2)"
			+ " WHEN ?4 = '3' THEN ?2"
			+ " WHEN ?4 = '4' THEN CONCAT(?2, '%') END ) ) "
			+ "AND (?5 = '' OR (EVAL_FLAG = ?5)) "
			+ "AND (?6 = '' OR (ORIGIN_FLAG = ?6)) "
			+ "ORDER BY COUNTRY_NAME", nativeQuery = true)

	// Parameters for search query
	List<Country> searchCountry(String conID, String conName, String conIDSelOpt, String conNameSelOpt, String evalFlag, String originFlag);
	
	@Query(value = "SELECT * FROM REF_COUNTRY WHERE 1=1 AND COUNTRY_ID IN (?1)", nativeQuery = true)
	List<Country> searchCountry(List<String> ids);

	// Insert query
	@Modifying
	@Transactional
	@Query(value = "INSERT INTO REF_COUNTRY ([COUNTRY_ID],[COUNTRY_NAME],[CREATOR_ID],[EVAL_FLAG],[ORIGIN_FLAG])" 
			+ " VALUES " 
			+ "(?1 , ?2 , ?3, ?4, ?5 ) ", nativeQuery = true)

	void addCountry(String conID, String conName, String creator, String evalFlag, String cooFlag);

	@Modifying
	@Transactional
	@Query(value = "UPDATE REF_COUNTRY " 
			+ "SET "
			+ "[COUNTRY_NAME] = ?2 "
			+ ",[MODIFIER_ID] = ?3 " 
			+ ",[MODIFIED_DATETIME] = GETDATE() " 
			+ ",[EVAL_FLAG] = ?4 "
			+ ",[ORIGIN_FLAG] = ?5 "
			+ " WHERE "
			+ " COUNTRY_ID = ?1 ", nativeQuery = true)

	void updateCountry(String conID, String conName, String modifier, String evalFlag, String cooFlag);

	@Modifying
	@Transactional
	@Query(value = "DELETE FROM REF_COUNTRY WHERE COUNTRY_ID = ?1", nativeQuery = true)
	void deleteCountry(String conID);
	
	// Check Country code usage
	@Query(value = "SELECT CASE WHEN EXISTS ( "
			+ "(SELECT a.COUNTRY_ID FROM REF_COUNTRY a "
			+ "WHERE 1=1 "
			+ "AND a.COUNTRY_ID = ?1 "
			+ "AND (EXISTS( SELECT COUNTRY_ID FROM PR_RM_STAT WHERE COUNTRY_ID = a.COUNTRY_ID) "
			+ "	OR EXISTS( SELECT COUNTRY_ID FROM PR_STAT WHERE COUNTRY_ID = a.COUNTRY_ID) "
			+ "	OR EXISTS( SELECT COUNTRY_ID FROM REGULATION WHERE COUNTRY_ID = a.COUNTRY_ID) "
			+ "	OR EXISTS( SELECT COUNTRY_ID FROM RM_MANF WHERE ORIGIN_COUNTRY_ID = a.COUNTRY_ID) "
			+ "	OR EXISTS( SELECT COUNTRY_ID FROM RM_STAT WHERE COUNTRY_ID = a.COUNTRY_ID) "
			+ "	OR EXISTS( SELECT COUNTRY_ID FROM VENDOR WHERE COUNTRY_ID = a.COUNTRY_ID) "
			+ " OR EXISTS( SELECT COUNTRY_LIST FROM REG_DOC_CAT WHERE COUNTRY_LIST LIKE CONCAT('%', a.COUNTRY_ID, '%')) )) "
			+ ") THEN '1' ELSE '0' END ", nativeQuery = true)
	String validateCountry(String conID);
}
