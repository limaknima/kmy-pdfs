package com.fms.pfc.repository.base.api;

import java.util.List;

import javax.transaction.Transactional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.fms.pfc.domain.model.UsrProfile;

@Repository
public interface UserProfileRepository extends JpaRepository<UsrProfile, String> {
	 
	//Search query for userProfileList
	@Query(value = "select usr.*, org.ORG_NAME, grp.GROUP_NAME, "
			+ " convert(varchar, usr.effective_date_from, 103) as effective_date_from, "
			+ " convert(varchar, usr.effective_date_to, 103) as effective_date_to, "
			+ " (SELECT STRING_AGG(role.role_name,',') from usr_role  left join role on usr_role.role_ID = role.role_ID  WHERE USER_ID = usr.user_id) as roles "
			+ " from usr "
			+ " inner join organization org on org.org_ID = usr.org_ID "
			+ " inner join grp on grp.group_ID = usr.group_ID "			
			+ " where (?1 = '' or (org.org_name like case when ?6 = '1' then concat('%', ?1, '%') "
			+ "						 			   when ?6 = '2' then concat('%', ?1) "
			+ "        				  			   when ?6 = '3' then ?1 "
			+ "        				   			   when ?6 = '4' then concat(?1, '%') end)) "
			+ " and (?2 = '' or (usr.user_id like case when ?7 = '1' then concat('%', ?2, '%') "
			+ "					     			   when ?7 = '2' then concat('%', ?2) "
			+ "        				 			   when ?7 = '3' then ?2 "
			+ "        				 		  	   when ?7 = '4' then concat(?2, '%') end))"
			+ " and (?3 = '' or (usr.user_name like case when ?8 = '1' then concat('%', ?3, '%') "
			+ "					     			   when ?8 = '2' then concat('%', ?3) "
			+ "        				 			   when ?8 = '3' then ?3 "
			+ "        				 		  	   when ?8 = '4' then concat(?3, '%') end))"
			+ " and (?4 = '' or (usr.email like case when ?9 = '1' then concat('%', ?4, '%') "
			+ "					     			   when ?9 = '2' then concat('%', ?4) "
			+ "        				 			   when ?9 = '3' then ?4 "
			+ "        				 		  	   when ?9 = '4' then concat(?4, '%') end))"
			+ " and (?5 = '' or usr.group_ID = ?5) ", nativeQuery = true)

	//Parameters for search query
	List<UsrProfile> searchUserProfile(String orgName, String userID, String userName, String email, 
			String userGp, String para1, String para2, String para3, String para4);
	
	  //Search query for userProfileRole
	  @Query(value = "select usr.*, org.ORG_NAME, grp.GROUP_NAME, usr_role.USER_ID, usr_role.ROLE_ID,"
	  + "  convert(varchar, effective_date_from, 103) as effective_date_from " 
	  +	", convert(varchar, effective_date_to, 103) as effective_date_to " 
	  + " from usr " 
	  + " left join organization org on org.org_ID = usr.org_ID " 
	  +	" left join grp on grp.group_ID = usr.group_ID " 
	  + " left join USR_ROLE on usr_role.USER_ID = usr.USER_ID", nativeQuery = true)
	  
	  //Parameters for search query 
	  List<UsrProfile> searchUserRole();
	  
	//Search query for userProfileRole
	@Query(value = "select count(*) from usr " 
	+ "where group_ID = ?1 ", nativeQuery = true)
	  
	//Parameters for search query 
	int getUserProfileCountByGrp(String groupId);
	 
	//Search query for userProfileRole
	@Query(value = "select count(*) from usr " 
	+ "where user_ID = ?1 ", nativeQuery = true)
	  
	//Parameters for search query 
	int getUserProfileCountByUsr(String userId);
	
	//Insert query
    @Modifying	  
    @Transactional
    @Query(value = "INSERT INTO USR 		"
	           + " ([USER_ID] 				"
	           + " ,[USER_NAME] 			"
	           + " ,[ORG_ID] 				"
	           + " ,[GROUP_ID] 				"
	           + " ,[PASSWORD] 				"
	           + " ,[EMAIL]					"
	           + " ,[DISABLED_FLAG] 	  	"
	           + " ,[FAILED_ATTEMPT_COUNT]  "
	           + " ,[ALERT_PREFERENCE]		"
	           + " ,[CREATOR_ID]			"	           
	           + " ,[EFFECTIVE_DATE_FROM] 	"
	           + " ,[EFFECTIVE_DATE_TO] )	"
    		   + " VALUES					"
    		   + " (?1 "
    		   + ", ?2 "
    		   + ", ?3 "
    		   + ", ?4 "
    		   + ", ?5 "
    		   + ", ?6 "
    		   + ", ?7 "
    		   + ", ?8 "
    		   + ", ?9 "   
    		   + ", ?10 "  
    		   + ", CASE WHEN ?11 = '' THEN NULL ELSE CONVERT( DATETIME, ?11, 105) END"
    		   + ", CASE WHEN ?12 = '' THEN NULL ELSE CONVERT( DATETIME, ?12, 105) END); ", nativeQuery = true)

	void addUserProfile(String userId, String userName, String orgId, String grpId, String password,
			  String email, String disabledFlag, int failCount, int alertPre, String creatorId,
			  String effecDate, String expDate);
    
    @Modifying	  
    @Transactional	
    @Query(value ="UPDATE USR "         		   
	           + " SET [USER_NAME] 			= ?2 "
	           + " ,[ORG_ID] 				= ?3 "
	           + " ,[GROUP_ID] 				= ?4 "
	           + " ,[EMAIL]					= ?5 "
	           + " ,[ALERT_PREFERENCE]	    = CASE "
	           + " WHEN ?6 = '' THEN ALERT_PREFERENCE "
	           + " ELSE ?6 END"
	           + " ,[MODIFIER_ID]	  		= ?7 "
	           + " ,[MODIFIED_DATETIME]		= GETDATE() "
	           + " ,[EFFECTIVE_DATE_FROM]	= (CASE WHEN ?8 = '' THEN NULL ELSE CONVERT( DATETIME, ?8, 105) END) "
	           + " ,[EFFECTIVE_DATE_TO] 	= (CASE WHEN ?9 = '' THEN NULL ELSE CONVERT( DATETIME, ?9, 105) END) "
			   + " WHERE " 
			   + " USER_ID = ?1 ", nativeQuery = true)
	  
	  void updateUserProfile(String userID, String userName, String orgId, String grpId, String email,
			  int alertPre, String modifierId, String effecDate, String expDate);	 
	
	  @Modifying	  
	  @Transactional	  
	  @Query(value = "DELETE FROM USR WHERE USER_ID = ?1", 
	  nativeQuery = true) 
	  void deleteUserProfile(String userId);
	 
	  @Modifying
	  @Transactional
	  @Query(value = "update usr set failed_attempt_count = ?1 where user_id = ?2", nativeQuery = true)
	  void updateFailAttempt(int attempts, String user_id);
	  
	  @Modifying
	  @Transactional
	  @Query(value = " UPDATE USR  "
	  		+ " SET PASSWORD 		= ?2 "
	  		+ ", MODIFIER_ID 		= ?3 "
	  		+ ", FAILED_ATTEMPT_COUNT = 0 "
	  		+ ", DIS_DT = NULL "
	  		+ ", REL_DIS_DT = NULL "
	  		+ ", MODIFIED_DATETIME 	= GETDATE() "
	  		+ " WHERE 		USER_ID = ?1", nativeQuery = true)
	  void resetPassword(String user_id, String newPassword, String modifier);

	  @Modifying
	  @Transactional
	  @Query(value = " UPDATE USR "
	  		+ " SET DISABLED_FLAG 	= ?3 "
	  		+ ", MODIFIER_ID 	  	= ?2 "
	  		+ ", MODIFIED_DATETIME 	= GETDATE() "
	  		+ " WHERE 	USER_ID 	= ?1 ", nativeQuery = true)
	  void lockOrUnlockAccount(String user_id, String modifier, String flag);
	  
	  @Modifying
	  @Transactional
	  @Query(value = " UPDATE USR "
	  		+ " SET DISABLED_FLAG 	= ?3 "
	  		+ ", MODIFIER_ID 	  	= ?2 "
	  		+ ", MODIFIED_DATETIME 	= GETDATE() "
	  		+ ", FAILED_ATTEMPT_COUNT = 0 "
	  		+ ", DIS_DT = NULL "
	  		+ ", REL_DIS_DT = NULL "
	  		+ " WHERE 	USER_ID 	= ?1 ", nativeQuery = true)
	  void unlockAccountOnly(String user_id, String modifier, String flag);
	  
	  @Query(value = "SELECT U.*, 'orgname' as org_name,'grpname' as group_name FROM USR U WHERE 1=1 AND U.GROUP_ID=?1 ", nativeQuery = true)
	  List<UsrProfile> findUsersByGroup(String grpId);
}
