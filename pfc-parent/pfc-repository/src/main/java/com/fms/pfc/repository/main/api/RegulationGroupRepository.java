package com.fms.pfc.repository.main.api;

import java.util.List;

import javax.transaction.Transactional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.fms.pfc.domain.model.main.RegulationCategory;
import com.fms.pfc.domain.model.main.RegulationGroup;

@Repository
public interface RegulationGroupRepository extends JpaRepository<RegulationGroup, Integer> {
	
	//Start Kimberley 20210109
	//Search query based on id
	@Query(value = "SELECT * FROM REG_DOC_CAT_GRP WHERE REG_DOC_CAT_GRP_ID = ?1 ", nativeQuery = true)
	
	//Parameters for search query
	RegulationGroup searchGroupRegulation(String grpId);
	//End Kimberley 20210109
	 
	//Search query
	@Query(value = "SELECT * FROM REG_DOC_CAT_GRP "
			+ "WHERE (?1 = '' OR (REG_DOC_CAT_GRP_NAME LIKE CASE "
			+ "                                 WHEN ?3 = '1' THEN CONCAT('%', ?1, '%') "
			+ "									WHEN ?3 = '2' THEN CONCAT('%', ?1) "
			+ "									WHEN ?3 = '3' THEN ?1 "
			+ "									WHEN ?3 = '4' THEN CONCAT(?1, '%') END)) "
			+ "AND (?2 = '' OR (reg_doc_cat_grp_desc LIKE CASE "
			+ "			 						WHEN ?4 = '1' THEN CONCAT('%', ?2, '%') "
			+ "									WHEN ?4 = '2' THEN CONCAT('%', ?2) "
			+ "									WHEN ?4 = '3' THEN ?2 "
			+ "									WHEN ?4 = '4' THEN CONCAT(?2, '%') END)) "
			+ "ORDER BY REG_DOC_CAT_GRP_NAME", nativeQuery = true)
	//Parameters for search query 
	List<RegulationGroup> searchGroupRegulation(String grpName, String grpDesc, String exp1, String exp2);

	//FSGS) KarVei 27/2/2021 Add - Check if group is used in REGULATION_DOC
	@Query(value = "SELECT * FROM REG_DOC_CAT_GRP A "
			+ "WHERE EXISTS "
			+ "(SELECT REG_DOC_CAT_GRP_ID FROM REGULATION_DOC B WHERE A.REG_DOC_CAT_GRP_ID = B.REG_DOC_CAT_GRP_ID)", nativeQuery = true)
	List<RegulationGroup> checkGroupExist();
	
	//Insert query
    @Modifying	  
    @Transactional
    @Query(value = " INSERT INTO REG_DOC_CAT_GRP " 
			  + "([REG_DOC_CAT_GRP_NAME] " 
			  +	",[REG_DOC_CAT_GRP_DESC] " 
			  +	",[CREATOR_ID] " 
			  + ",[CREATED_DATETIME] )" 
			  + "VALUES " + " (?1 " + ", ?2 " + ", ?3 " + ", GETDATE() )", nativeQuery = true)
	 
	void addGroupRegulation(String grpName, String grpDesc, String creatorId);
	 
    @Modifying	  
    @Transactional	
    @Query(value ="UPDATE REG_DOC_CAT_GRP " 
			  + "	SET [REG_DOC_CAT_GRP_NAME] 	= ?1 " 
			  + "   	,[REG_DOC_CAT_GRP_DESC] = ?2 " 
			  + "   	,[MODIFIER_ID]          = ?3 " 
			  + "   	,[MODIFIED_DATETIME]    = GETDATE() " 
			  + " WHERE " 
			  + " REG_DOC_CAT_GRP_NAME = ?1 ", nativeQuery = true)
	  
	  void updateGroupRegulation(String grpName, String grpDesc, String creatorId);	 
	
	  @Modifying	  
	  @Transactional	  
	  @Query(value = "DELETE FROM REG_DOC_CAT_GRP WHERE REG_DOC_CAT_GRP_NAME = ?1", 
	  nativeQuery = true) 
	  void deleteGroupRegulation(String grpName);
	 	
}
