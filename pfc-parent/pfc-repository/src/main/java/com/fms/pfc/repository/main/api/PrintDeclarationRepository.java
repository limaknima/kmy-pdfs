package com.fms.pfc.repository.main.api;

import java.util.List;

import javax.transaction.Transactional;

import com.fms.pfc.domain.model.Country;

import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;

@Repository
public interface PrintDeclarationRepository extends JpaRepository<Country, String> {

	// Search query
	@Query(value = "select * from ref_country "
			+ "where (?1 = '' or (country_id like case when ?3 = '1' then concat('%', ?1, '%' ) "
			+ "						 			   	   when ?3 = '2' then concat('%', ?1) 		"
			+ "        				  			       when ?3 = '3' then ?1 "
			+ "        				   			       when ?3 = '4' then concat(?1, '%') end ) ) "
			+ "and (?2 = '' or (country_name like case when ?4 = '1' then concat('%', ?2, '%') 	"
			+ "					     			       when ?4 = '2' then concat('%', ?2) 		"
			+ "        				 			       when ?4 = '3' then ?2 					"
			+ "        				 		  	       when ?4 = '4' then concat(?2, '%') end ) ) ", nativeQuery = true)

	// Parameters for search query
	List<Country> searchCountry(String conID, String conName, String para1, String para2);

	// Insert query
	@Modifying
	@Transactional
	@Query(value = " INSERT INTO REF_COUNTRY " + "([COUNTRY_ID] 	    " + ",[COUNTRY_NAME]    " + ",[CREATOR_ID]	)"
			+ "VALUES " + " (?1 " + ", ?2 " + ", ?3 ); ", nativeQuery = true)

	void addCountry(String conID, String conName, String creator);

	@Modifying
	@Transactional
	@Query(value = "UPDATE REF_COUNTRY " + "	SET [COUNTRY_ID] 	 = ?1 " + "   ,[COUNTRY_NAME]  	 = ?2 "
			+ "   ,[MODIFIER_ID] 		 = ?3 " + "   ,[MODIFIED_DATETIME] = ?4 " + " WHERE "
			+ " COUNTRY_ID = ?1 ", nativeQuery = true)

	void updateCountry(String conID, String conName, String modifier, String modifyTime);

	@Modifying
	@Transactional
	@Query(value = "DELETE FROM REF_COUNTRY WHERE COUNTRY_ID = ?1", nativeQuery = true)
	void deleteCountry(String conID);
}
