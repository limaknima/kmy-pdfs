package com.fms.pfc.repository.main.api;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.fms.pfc.domain.model.main.FlavorStatus;

public interface FlavorStatusRepository extends JpaRepository<FlavorStatus, Integer> {
	// Search query
	@Query(value = "SELECT * FROM FLAVOR_STATUS WHERE 1=1 " 
			+ "AND (?1 = '' OR (FS_NAME LIKE CASE "
			+ "                                 WHEN ?3 = '1' THEN CONCAT('%', ?1, '%') "
			+ "									WHEN ?3 = '2' THEN CONCAT('%', ?1) "
			+ "									WHEN ?3 = '3' THEN ?1 "
			+ "									WHEN ?3 = '4' THEN CONCAT(?1, '%') END)) "
			+ "AND (?2 = '' OR (FS_DESC LIKE CASE "
			+ "                                 WHEN ?4 = '1' THEN CONCAT('%', ?2, '%') "
			+ "									WHEN ?4 = '2' THEN CONCAT('%', ?2) "
			+ "									WHEN ?4 = '3' THEN ?2 "
			+ "									WHEN ?4 = '4' THEN CONCAT(?2, '%') END)) "
			+ "AND (?5 = 100 OR FS_RANK = ?5)  "
			+ "ORDER BY FS_NAME", nativeQuery = true)
	// Parameters for search query
	List<FlavorStatus> searchByCriteria(String fsName, String fsDesc, String fsNameExp, String fsDescExp, String fsRank);

}
