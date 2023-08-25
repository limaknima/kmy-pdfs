package com.fms.pfc.repository.base.api;

import java.util.List;

import javax.transaction.Transactional;
import com.fms.pfc.domain.model.Role;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;

@Repository
public interface RoleRepository extends JpaRepository<Role, String>{
	@Query(value = "SELECT * FROM ROLE "
			+ "WHERE (?1 = '' OR (ROLE_ID LIKE CASE WHEN ?2 = '1' THEN CONCAT('%', ?1, '%') "
			+ "									WHEN ?2 = '2' THEN CONCAT('%', ?1) "
			+ "									WHEN ?2 = '3' THEN ?1 "
			+ "									WHEN ?2 = '4' THEN CONCAT(?1, '%') END)) "
			+ "AND (?3 = '' OR (ROLE_NAME LIKE CASE WHEN ?4 = '1' THEN CONCAT('%', ?3, '%') "
			+ "									WHEN ?4 = '2' THEN CONCAT('%', ?3) "
			+ "									WHEN ?4 = '3' THEN ?3 "
			+ "									WHEN ?4 = '4' THEN CONCAT(?3, '%') END)) ", nativeQuery = true)
	List<Role> searchUserRole(String roleId, String roleIdWildcard, String roleName, String roleNameWildcard);	   

	@Query(value = " SELECT A.* FROM ROLE A"
		+ " WHERE NOT EXISTS (SELECT B.* from USR_ROLE B "
		+ "                  WHERE A.ROLE_ID = B.ROLE_ID "
		+ "                  AND B.USER_ID = ?1 ) " , nativeQuery = true)
	List<Role> searchListBoxUserRole(String userId);	
	
	@Modifying
	@Transactional
	@Query(value = "INSERT INTO ROLE (ROLE_ID,ROLE_NAME,ROLE_DESC,CREATOR_ID) VALUES "
			+ "(?1,?2,?3,?4) ", nativeQuery = true)
	void addUserRole(String roleId, String roleName, String roleDesc, String creatorId);
	
	@Modifying
	@Transactional
	@Query(value = "UPDATE ROLE SET  "
			+ "	ROLE_NAME = ?1, "
			+ "	ROLE_DESC = ?2, "
			+ "	MODIFIER_ID = ?3, "
			+ "	MODIFIED_DATETIME = GETDATE() "
			+ "WHERE ROLE_ID = ?4 ", nativeQuery = true)
	void updateUserRole(String roleName, String roleDesc, String modifierId, String roleId);
	
	@Modifying
	@Transactional
	@Query(value = "DELETE FROM ROLE WHERE ROLE_ID = ?1 ", nativeQuery = true)
	void deleteUserRoleBatch(String roleId);

}
