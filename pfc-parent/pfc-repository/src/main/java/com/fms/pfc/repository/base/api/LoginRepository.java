package com.fms.pfc.repository.base.api;

import com.fms.pfc.domain.model.Usr;

import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

import javax.transaction.Transactional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;

@Repository
public interface LoginRepository extends JpaRepository<Usr, String> {

	@Query(value = "SELECT *, CONVERT(VARCHAR, EFFECTIVE_DATE_FROM, 103) AS effective_date_from, "
			+ "CONVERT(VARCHAR, EFFECTIVE_DATE_TO, 103) AS effective_date_to, "
			+ "PASSWORD_SET_DATE + 90 as password_set_date "
			+ "FROM USR WHERE USER_ID = ?1", nativeQuery = true)
	Usr searchUser(String user_id);
	
	@Query(value = "SELECT * FROM USR ORDER BY USER_ID", nativeQuery = true)
	List<Usr> searchAllUser();

	//Input 1 is to get checker role
	//Input 2 is to get HOD role
	@Query(value = "SELECT DISTINCT(A.USER_ID), A.* FROM USR A "
			+ "INNER JOIN USR_ROLE B "
			+ "ON A.USER_ID = B.USER_ID "
			+ "WHERE A.GROUP_ID = 'FR' "
			+ "AND B.ROLE_ID != 'SUSR' "
			+ "AND ((?1 = 1 AND B.ROLE_ID != 'MKR' AND B.ROLE_ID != 'HOD') "
			+ "OR (?1 = 2 AND B.ROLE_ID = 'HOD')) ", nativeQuery = true)
	List<Usr> searchGrpUser(int authLvl);

	@Modifying
	@Transactional
	@Query(value = "UPDATE USR SET USER_NAME = ?2, EMAIL = ?3, EFFECTIVE_DATE_FROM = CONVERT(DATETIME, ?4, 105), "
			+ "EFFECTIVE_DATE_TO = CONVERT(DATETIME, ?5, 105), ALERT_PREFERENCE = ?6, MODIFIER_ID = ?7, "
			+ "MODIFIED_DATETIME = GETDATE() WHERE USER_ID = ?1", nativeQuery = true)
	void updateUser(String user_id, String user_name, String email, String effecDateFr, String effecDateTo, int alert,
			String loggedUser);

	@Modifying
	@Transactional
	@Query(value = "UPDATE USR SET PASSWORD = ?2, "
			+ "PASSWORD_SET_DATE = GETDATE(), "
			+ "MODIFIER_ID = ?1, "
			+ "MODIFIED_DATETIME = GETDATE() "
			+ "WHERE USER_ID = ?1", nativeQuery = true)
	void updateUserPass(String user_id, String newPassword);

	@Modifying
	@Transactional
	@Query(value = "UPDATE USR SET FAILED_ATTEMPT_COUNT = ?1 WHERE USER_ID = ?2", nativeQuery = true)
	void updateFailAttempt(int attempts, String user_id);

	@Modifying
	@Transactional
	@Query(value = "UPDATE USR SET DISABLED_FLAG = 'Y' WHERE USER_ID = ?1", nativeQuery = true)
	void lockUser(String user_id);
}
