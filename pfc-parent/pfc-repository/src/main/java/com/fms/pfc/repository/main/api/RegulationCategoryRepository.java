package com.fms.pfc.repository.main.api;

import java.util.List;

import javax.transaction.Transactional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.fms.pfc.domain.model.main.RegulationCategory;

@Repository
public interface RegulationCategoryRepository extends JpaRepository<RegulationCategory, Integer> {
	 
	
	//Start Kimberley 20210109
	//Search query based on id
	@Query(value = "SELECT * FROM REG_DOC_CAT WHERE REG_DOC_CAT_ID = ?1 ", nativeQuery = true)
	
	//Parameters for search query
	RegulationCategory searchCategoryRegulation(String catId);
	//End Kimberley 20210109
		
	//Search query
	@Query(value = "SELECT * FROM REG_DOC_CAT "
			+ "WHERE (?1 = '' OR (REG_DOC_CAT_NAME LIKE CASE "
			+ "                                 WHEN ?3 = '1' THEN CONCAT('%', ?1, '%') "
			+ "									WHEN ?3 = '2' THEN CONCAT('%', ?1) "
			+ "									WHEN ?3 = '3' THEN ?1 "
			+ "									WHEN ?3 = '4' THEN CONCAT(?1, '%') END)) "
			+ "AND (?2 = '' OR (REG_DOC_CAT_DESC LIKE CASE "
			+ "                                 WHEN ?4 = '1' THEN CONCAT('%', ?2, '%') "
			+ "									WHEN ?4 = '2' THEN CONCAT('%', ?2) "
			+ "									WHEN ?4 = '3' THEN ?2 "
			+ "									WHEN ?4 = '4' THEN CONCAT(?2, '%') END)) "
			+ "AND (?5 = '' OR COUNTRY_LIST LIKE CONCAT('%', ?5, '%'))  "
			+ "ORDER BY REG_DOC_CAT_NAME", nativeQuery = true)
	//Parameters for search query
	List<RegulationCategory> searchCategoryRegulation(String catName, String catDesc, String exp1, String exp2, String countryId);

	//FSGS) KarVei 27/2/2021 Add - Check if category is used in REGULATION_DOC
	@Query(value = "SELECT * FROM REG_DOC_CAT A "
			+ "WHERE EXISTS "
			+ "(SELECT REG_DOC_CAT_ID FROM REGULATION_DOC B WHERE A.REG_DOC_CAT_ID = B.REG_DOC_CAT_ID)", nativeQuery = true)	
	List<RegulationCategory> checkCategoryExist();
	
	//Insert query
    @Modifying	  
    @Transactional
    @Query(value = " INSERT INTO REG_DOC_CAT " 
			  + "([REG_DOC_CAT_NAME] " 
			  +	",[REG_DOC_CAT_DESC] " 
			  +	",[CREATOR_ID] " 
			  + ",[CREATED_DATETIME] " 
			  + ",[COUNTRY_LIST] )" 
			  + "VALUES " 
			  + "(?1, ?2, ?3, GETDATE(), ?4 )", nativeQuery = true)
	 
	void addCategoryRegulation(String catName, String catDesc, String creatorId, String countryList);
	 
    @Modifying	  
    @Transactional	
    @Query(value ="UPDATE REG_DOC_CAT " 
			  + "	SET [REG_DOC_CAT_NAME] = ?1 " 
			  + "   	,[REG_DOC_CAT_DESC] = ?2 " 
			  + "   	,[MODIFIER_ID] = ?3 " 
			  + "   	,[MODIFIED_DATETIME] = GETDATE() " 
			  + "       ,[COUNTRY_LIST] = ?4"
			  + " WHERE " 
			  + " REG_DOC_CAT_NAME = ?1 ", nativeQuery = true)
	  
	  void updateCategoryRegulation(String catName, String catDesc, String creatorId, String countryList);	 
	
	  @Modifying	  
	  @Transactional	  
	  @Query(value = "DELETE FROM REG_DOC_CAT WHERE REG_DOC_CAT_NAME = ?1", 
	  nativeQuery = true) 
	  void deleteCategoryRegulation(String catName);
	  
		@Query(value = "SELECT * FROM REG_DOC_CAT WHERE 1=1 AND (?1 = '' OR COUNTRY_LIST LIKE CONCAT('%', ?1, '%')) ORDER BY REG_DOC_CAT_NAME ", nativeQuery = true)
		List<RegulationCategory> searchCategoryRegulationByCountry(String countryId);
	 	
}
