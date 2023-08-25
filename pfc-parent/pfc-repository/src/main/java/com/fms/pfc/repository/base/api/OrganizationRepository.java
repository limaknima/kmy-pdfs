package com.fms.pfc.repository.base.api;

import java.util.List;

import javax.transaction.Transactional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.fms.pfc.domain.model.Organization;

@Repository
public interface OrganizationRepository extends JpaRepository<Organization, String> {
	 
	//Search query
	@Query(value = "SELECT *, "
			+ "  CONVERT(VARCHAR, EFFECTIVE_DATE_FROM, 103) AS EFFECT_START "
			+ ", CONVERT(VARCHAR, EFFECTIVE_DATE_TO, 103) AS EFFECT_END "
			+ " FROM ORGANIZATION "
			+ "WHERE (?1 = '' OR (ORG_ID LIKE CASE WHEN ?4 = '1' THEN CONCAT('%', ?1, '%') "
			+ "						 			   WHEN ?4 = '2' THEN CONCAT('%', ?1) "
			+ "        				  			   WHEN ?4 = '3' THEN ?1 "
			+ "        				   			   WHEN ?4 = '4' THEN CONCAT(?1, '%') END)) "
			+ "AND (?2 = '' OR (ORG_NAME LIKE CASE WHEN ?5 = '1' THEN CONCAT('%', ?2, '%') "
			+ "					     			   WHEN ?5 = '2' THEN CONCAT('%', ?2) "
			+ "        				 			   WHEN ?5 = '3' THEN ?2 "
			+ "        				 		  	   WHEN ?5 = '4' THEN CONCAT(?2, '%') END))"
			+ "AND ( ?3 = '' OR ORG_TYPE = ?3 ) "
			+ "ORDER BY ORG_NAME", nativeQuery = true)
	
	//Parameters for search query
	List<Organization> searchOrganization(String orgaID, String orgaName, 
			String orgaType, String exp1, String exp2);

	//Insert query
    @Modifying	  
    @Transactional
    @Query(value = " INSERT INTO ORGANIZATION " 
			  + "([ORG_ID] 	    " 
			  +	",[ORG_NAME]    " 
			  + ",[ORG_TYPE]    " 
			  + ",[EMAIL]		" 
			  + ",[STREET]	    " 
			  +	",[TOWN]		" 
			  + ",[POSTCODE]	" 
			  + ",[STATE_ID]	"
			  + ",[COUNTRY_ID]	" 
			  + ",[OFFICE_TEL]	" 
			  + ",[FAX_NO]	    " 
			  + ",[REMARKS]	    " 
			  + ",[EFFECTIVE_DATE_FROM]" 
			  + ",[EFFECTIVE_DATE_TO]  " 
			  + ",[IMAGE]		"
			  + ",[CREATOR_ID]	)"
			  + "VALUES " + " (?1 " + ", ?2 " + ", ?3 " + ", ?4 " + ", ?5 " + ", ?6 " 
			  +	", ?7 " + ", ?8 " + ", 'MY' " + ", ?9 " + ", ?10 " + ", ?11 " 
			  + ", CASE WHEN ?12 = '' THEN NULL ELSE CONVERT( DATETIME, ?12, 105) END" 
			  + ", CASE WHEN ?13 = '' THEN NULL ELSE CONVERT( DATETIME, ?13, 105) END" 
			  +	", CASE WHEN ?14 = '' THEN NULL ELSE ?14 END" + ", ?15); " , nativeQuery = true)
	 
	void addOrganization(String orgaID, String orgaName, String orgaType, String email, String street,
			  String town, String postcode, String state, String tel, String fax,
			  String url, String effectStart, String effectEnd, byte[] logo, String creator);
	 
    @Modifying	  
    @Transactional	
    @Query(value ="UPDATE ORGANIZATION " 
			  + "	SET [ORG_ID] 	= ?1 " 
			  + "   	,[ORG_NAME]  	= ?2 " 
			  + "   	,[ORG_TYPE] 	= ?3 " 
			  + "   	,[EMAIL] 		= ?4 " 
			  + "   	,[STREET] 		= ?5 " 
			  + "   	,[TOWN] 		= ?6 " 
			  + "   	,[POSTCODE] 	= ?7 " 
			  + "  		,[STATE_ID] 	= ?8 " 
			  + "  		,[OFFICE_TEL] 	= ?9 " 
			  + " 		,[FAX_NO] 		= ?10 " 
			  + "		,[REMARKS] 		= ?11 " 
			  + "		,[EFFECTIVE_DATE_FROM] = (CASE WHEN ?12 = '' THEN NULL ELSE CONVERT( DATETIME, ?12, 105) END)" 
			  + "		,[EFFECTIVE_DATE_TO] 	= (CASE WHEN ?13 = '' THEN NULL ELSE CONVERT( DATETIME, ?13, 105) END)" 
			  + "   	,[IMAGE] 		= (CASE WHEN ?14 = '' THEN [IMAGE] ELSE ?14 END)"			
			  + "   	,[MODIFIER_ID] 		 = ?15 " 
			  + "   	,[MODIFIED_DATETIME] = getdate() " 
			  + " WHERE " 
			  + " ORG_ID = ?1 ", nativeQuery = true)
	  
	  void updateOrganization(String orgaID, String orgaName, String orgaType, String email, String street,
			  String town, String postcode, String state, String tel, String fax, String url, String effectStart,
			  String effectEnd, byte[] logo, String modifier);	 
	
	  @Modifying	  
	  @Transactional	  
	  @Query(value = "DELETE FROM ORGANIZATION WHERE ORG_ID = ?1", 
	  nativeQuery = true) 
	  void deleteOrganization(String orgaID);
	  
	//FSGS) Hani 2/3/2021 Add/changed - Add Check OrgCode usage START
	  @Query(value = "SELECT CASE WHEN EXISTS ( "
				+ "(SELECT o.ORG_ID FROM ORGANIZATION o "
				+ "WHERE o.ORG_ID = ?1 "
				+ "AND (EXISTS( SELECT ORG_ID FROM usr WHERE ORG_ID = o.ORG_ID))) "
				+ ") THEN '1' ELSE '0' END ", nativeQuery = true)
		String checkOrgCodeUsage(String orgaID);
	//FSGS) Hani 2/3/2021 Add/changed - Add Check OrgCode usage END
	  
	  
	 	
}
