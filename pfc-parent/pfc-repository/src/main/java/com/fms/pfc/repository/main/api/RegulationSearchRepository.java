package com.fms.pfc.repository.main.api;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.fms.pfc.domain.model.main.Regulation;

@Repository
public interface RegulationSearchRepository extends JpaRepository<Regulation, String> {
	 
	//Search query
	@Query(value = "SELECT ROW_NUMBER() OVER(ORDER BY REG.REG_ID) as ROW_ID, " 
			+ "REG.REG_ID, "
			+ "REG.COUNTRY_ID, "
			+ "REG.RAW_MATL_ID, "
			+ "DOC.REG_DOC_ID, "
			+ "DOC.REG_DOC_CAT_GRP_ID, "
			+ "DOC.REG_DOC_CAT_ID, "
			+ "DOC.FILE_NAME, "
			+ "CASE DOC.ARCHIVED_FLAG WHEN 'Y' THEN 'Yes' ELSE 'No' END AS ARCHIVED_FLAG, "
			+ "CNT.COUNTRY_NAME, "
			+ "GRP.REG_DOC_CAT_GRP_NAME, "
			+ "CAT.REG_DOC_CAT_NAME, "
			+ "RMT.RAW_MATL_NAME "
			+ "FROM REGULATION REG "
			+ "INNER JOIN REGULATION_DOC DOC ON DOC.REG_ID = REG.REG_ID "
			+ "LEFT JOIN REF_COUNTRY CNT ON CNT.COUNTRY_ID = REG.COUNTRY_ID "
			+ "LEFT JOIN REG_DOC_CAT_GRP GRP ON GRP.REG_DOC_CAT_GRP_ID = DOC.REG_DOC_CAT_GRP_ID "
			+ "LEFT JOIN REG_DOC_CAT CAT ON CAT.REG_DOC_CAT_ID = DOC.REG_DOC_CAT_ID "
			+ "LEFT JOIN RAW_MATERIAL RMT ON RMT.RAW_MATL_ID = REG.RAW_MATL_ID "
			+ "WHERE (?1 = '' OR REG.COUNTRY_ID = ?1) "
  			+ "AND (?2 = '' OR DOC.REG_DOC_CAT_GRP_ID = ?2) "
  			+ "AND (?3 = '' OR DOC.REG_DOC_CAT_ID = ?3) "
  			+ "AND (?4 = '' OR (DOC.FILE_NAME LIKE CASE WHEN ?6 = '1' THEN CONCAT('%', ?4, '%') "
			+ "								WHEN ?6 = '2' THEN CONCAT('%', ?4) "
			+ "								WHEN ?6 = '3' THEN ?4 "
			+ "								WHEN ?6 = '4' THEN CONCAT(?4, '%') END)) "
			+ "AND (?5 = '' OR DOC.ARCHIVED_FLAG = ?5)"
			+ "AND (?7 = '' OR RMT.RAW_MATL_NAME = ?7) ", nativeQuery = true)
	//Parameters for search query
	List<Regulation> searchRegulation(String country, String group, String category,
			String file, String isActive, String expr, String rmName);

}
