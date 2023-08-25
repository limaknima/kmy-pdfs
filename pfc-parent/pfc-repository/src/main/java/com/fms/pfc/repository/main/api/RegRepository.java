package com.fms.pfc.repository.main.api;
//Kimberley 
//Differences with Regulation.java are some some difference with the datatypes
//Used for 5.3.2	Upload RM regulation

import java.util.List;

import javax.transaction.Transactional;

import org.springframework.stereotype.Repository;

import com.fms.pfc.domain.model.main.Reg;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;

@Repository
public interface RegRepository extends JpaRepository<Reg, Integer> {

	// Search query
	@Query(value = 
			"SELECT C.COUNTRY_NAME, R.* FROM REGULATION R "
			+ "LEFT JOIN REF_COUNTRY C ON C.COUNTRY_ID = R.COUNTRY_ID "
			+ "WHERE (?1 = '' or R.REG_ID = ?1) "
			+ "AND (?2 = '' or C.COUNTRY_ID = ?2) "
			+ "ORDER BY R.REG_NAME, "
			+ "C.COUNTRY_NAME, R.REF_URL1, "
			+ "		R.REF_URL2, R.REF_URL3, R.REF_URL4, R.REF_URL5, "
			+ "		R.REMARKS ASC ", nativeQuery = true)

	// Parameters for search query
	List<Reg> getRegList(int regId, String countryId);
	
	// Search query
	@Query(value = 
			"SELECT C.COUNTRY_NAME, R.* FROM REGULATION R "
			+ "LEFT JOIN REF_COUNTRY C ON C.COUNTRY_ID = R.COUNTRY_ID "
			+ "WHERE (?1 = 0 or R.RAW_MATL_ID = ?1) "
			+ "AND (?2 = '' or C.COUNTRY_ID = ?2) "
			+ "ORDER BY "
			+ "C.COUNTRY_NAME, R.REF_URL1, "
			+ "		R.REF_URL2, R.REF_URL3, R.REF_URL4, R.REF_URL5, "
			+ "		R.REMARKS ASC ", nativeQuery = true)
		// Parameters for search query
	List<Reg> searchRegList(int rawMatlId, String countryId);
	
	@Modifying
	@Transactional
	@Query(value = "INSERT INTO REGULATION "
			+ "	(RAW_MATL_ID, COUNTRY_ID, REG_NAME, REF_URL1, REF_URL2, REF_URL3, REF_URL4, REF_URL5, REMARKS, CREATOR_ID, CREATED_DATETIME) "
			+ "	VALUES "
			+ "	(?1, ?2, ?3, ?4, ?5, ?6, ?7, ?8, ?9, ?10, GETDATE()) ", nativeQuery = true)

	void addRegulation(int rawMatlId, String countryId, String regName, String refUrl1, 
			String refUrl2, String refUrl3, String refUrl4, String refUrl5, String remarks, String creatorId);

	@Modifying
	@Transactional
	@Query(value = "UPDATE REGULATION "
			+ "SET [REG_NAME] = ?1, "
			+ "	[REF_URL1] = ?2, "
			+ "	[REF_URL2] = ?3, "
			+ "	[REF_URL3] = ?4, "
			+ "	[REF_URL4] = ?5, "
			+ "	[REF_URL5] = ?6, "
			+ "	[REMARKS] = ?7, "
			+ "	[MODIFIER_ID] = ?8, "
			+ " [MODIFIED_DATETIME] =  GETDATE() "
			+ "WHERE [REG_ID] = ?9 ", nativeQuery = true)
	
	void updRegulation(String regName, String refUrl1, String refUrl2, String refUrl3, String refUrl4, String refUrl5, 
			String remarks, String modifiedId, int regId);
}
