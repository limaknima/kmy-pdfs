package com.fms.pfc.repository.main.api;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.fms.pfc.domain.model.main.Regl;

@Repository
public interface ReglRepository extends JpaRepository<Regl, Integer> {
	@Query(value = 
			"SELECT top 50 C.COUNTRY_NAME, R.* FROM REGULATION R "
			+ "LEFT JOIN REF_COUNTRY C ON C.COUNTRY_ID = R.COUNTRY_ID "
			+ "WHERE 1=1 "
			+ "AND (?1 = 0 or R.RAW_MATL_ID = ?1) "
			+ "AND (?2 = '' or C.COUNTRY_ID = ?2) "
			+ "ORDER BY"
			+ " C.COUNTRY_NAME, R.REF_URL1, "
			+ "R.REF_URL2, R.REF_URL3, R.REF_URL4, R.REF_URL5, "
			+ "R.REMARKS ASC ", nativeQuery = true)
		// Parameters for search query
	List<Regl> searchReglList(int rawMatlId, String countryId);

}
