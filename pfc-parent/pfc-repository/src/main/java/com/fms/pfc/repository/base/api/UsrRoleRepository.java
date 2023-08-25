package com.fms.pfc.repository.base.api;

import java.util.List;

import javax.transaction.Transactional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.fms.pfc.domain.model.UsrRole;

@Repository
public interface UsrRoleRepository extends JpaRepository<UsrRole, String> {
	
	@Query(value = " SELECT usr_role.*, role.role_name  "
			+ " from usr_role "
			+ " left join role on usr_role.role_ID = role.role_ID "			
			+ " WHERE USER_ID = ?1 ", nativeQuery = true )
	List<UsrRole> searchUserRole(String userId);
	
	//Add user role
	@Modifying
	@Transactional
	@Query(value = " INSERT INTO USR_ROLE (USER_ID,ROLE_ID) VALUES "
			+ "( ?1, ?2 ) ", nativeQuery = true)
	void addUserRole(String userId, String roleId);
	
	//Delete user role
	@Modifying
	@Transactional
	@Query(value = " DELETE FROM USR_ROLE WHERE USER_ID = ?1 ", nativeQuery = true)
	void deleteUserRole(String userId);
}
