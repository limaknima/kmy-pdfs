package com.fms.pfc.repository.main.api;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.fms.pfc.domain.model.main.LetterType;

public interface LetterTypeRepository extends JpaRepository<LetterType, Integer> {
	// Search query
		@Query(value = "SELECT * FROM LETTER_TYPE WHERE 1=1 " 
				+ "AND (?1 = '' OR (LT_NAME LIKE CASE "
				+ "                                 WHEN ?3 = '1' THEN CONCAT('%', ?1, '%') "
				+ "									WHEN ?3 = '2' THEN CONCAT('%', ?1) "
				+ "									WHEN ?3 = '3' THEN ?1 "
				+ "									WHEN ?3 = '4' THEN CONCAT(?1, '%') END)) "
				+ "AND (?2 = '' OR (LT_DESC LIKE CASE "
				+ "                                 WHEN ?4 = '1' THEN CONCAT('%', ?2, '%') "
				+ "									WHEN ?4 = '2' THEN CONCAT('%', ?2) "
				+ "									WHEN ?4 = '3' THEN ?2 "
				+ "									WHEN ?4 = '4' THEN CONCAT(?2, '%') END)) "
				+ "ORDER BY LT_NAME", nativeQuery = true)
		// Parameters for search query
		List<LetterType> searchByCriteria(String lsName, String lsDesc, String fsNameExp, String fsDescExp);
}
