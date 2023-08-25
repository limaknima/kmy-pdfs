package com.fms.pfc.repository.base.api;

import java.util.List;

import javax.transaction.Transactional;

import com.fms.pfc.domain.model.Group;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;

@Repository
public interface GroupRepository extends JpaRepository<Group, String>{
	@Query(value = "SELECT ORG.ORG_NAME, GRP.* FROM GRP GRP, ORGANIZATION ORG "
			+ "WHERE (?1 = '' OR GRP.ORG_ID LIKE %?1%) "
			+ "AND (?2 = '' OR GRP.GROUP_ID LIKE %?2%) "
			+ "AND ORG.ORG_ID = GRP.ORG_ID ", nativeQuery = true)
	List<Group> searchGroup(String orgId, String groupId);

	@Query(value = "SELECT O.ORG_NAME, G.* FROM GRP G, ORGANIZATION O "
			+ "WHERE G.ORG_ID IN ( "
			+ "	SELECT ORG_ID FROM ORGANIZATION "
			+ "	WHERE (?1 = '' OR (ORG_NAME LIKE CASE WHEN ?2 = '1' THEN CONCAT('%', ?1, '%') "
			+ "										  WHEN ?2 = '2' THEN CONCAT('%', ?1) "
			+ "										  WHEN ?2 = '3' THEN ?1 "
			+ "										  WHEN ?2 = '4' THEN CONCAT(?1, '%') END)) "
			+ ") "
			+ "AND (?3 = '' OR (G.GROUP_ID LIKE CASE WHEN ?4 = '1' THEN CONCAT('%', ?3, '%') "
			+ "									WHEN ?4 = '2' THEN CONCAT('%', ?3) "
			+ "									WHEN ?4 = '3' THEN ?3 "
			+ "									WHEN ?4 = '4' THEN CONCAT(?3, '%') END)) "
			+ "AND (?5 = '' OR (G.GROUP_NAME LIKE CASE WHEN ?6 = '1' THEN CONCAT('%', ?5, '%') "
			+ "									WHEN ?6 = '2' THEN CONCAT('%', ?5) "
			+ "									WHEN ?6 = '3' THEN ?5 "
			+ "									WHEN ?6 = '4' THEN CONCAT(?5, '%') END)) "
			+ "AND O.ORG_ID = G.ORG_ID", nativeQuery = true)
	List<Group> searchGroupList(String orgName, String orgNameWildcard, String groupId, String groupIdWildcard, String groupName, String groupNameWildcard);
	
	@Modifying
	@Transactional
	@Query(value = "DELETE FROM GRP "
			+ "WHERE ORG_ID = ?1 "
			+ "AND GROUP_ID = ?2 ", nativeQuery = true)
	void deleteGroupBatch(String orgId, String groupId);
	
	@Modifying
	@Transactional
	@Query(value = "INSERT INTO GRP (ORG_ID,GROUP_ID,GROUP_NAME,DESCRIPTION,CREATOR_ID) "
			+ "VALUES "
			+ "(?1,?2,?3,?4,?5) ", nativeQuery = true)
	void addGroup(String orgId, String groupId, String groupName, String description, String creatorId);	

	@Modifying
	@Transactional
	@Query(value = "UPDATE GRP SET "
			+ "		GROUP_NAME = ?1, "
			+ "		DESCRIPTION= ?2, "
			+ "		MODIFIER_ID = ?3, "
			+ "		MODIFIED_DATETIME = GETDATE() "
			+ "WHERE ORG_ID = ?4 "
			+ "AND GROUP_ID = ?5 ", nativeQuery = true)
	void updateGroup(String groupName, String description, String modifierId, String orgId, String groupId);	

}
