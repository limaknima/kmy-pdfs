package com.fms.pfc.repository.base.api;

import java.util.List;

import javax.transaction.Transactional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.fms.pfc.domain.model.UsrActivityLog;

@Repository
public interface UsrActivityRepository extends JpaRepository<UsrActivityLog, String> {
	
	@Query(value = "select a.*, b.org_id, b.user_name from usr_activity_log a " 
			+ "inner join usr b " 
			+ "on a.user_id = b.user_id "
			+ "where (?1 = '' or (b.disabled_flag = case when ?1 = 'active' then 'N' "
			+ "                                          when ?2 = 'inactive' then 'Y' end)) "
			+ "and (?2 = '' or a.user_id = ?2) "
			+ "and (?3 = '' or b.org_id = ?3) "
			+ "and (?4 = '' or convert(date, a.login_datetime) >= convert(datetime, ?4, 105)) "
			+ "and (?5 = '' or convert(date, a.login_datetime) <= convert(datetime, ?5, 105)) "
			+ "order by a.login_datetime desc", nativeQuery = true)
	List<UsrActivityLog> searchActivityLog(String userSession, String userId, String orgId, String dateFr, String dateTo);
	
	@Modifying
	@Transactional
	@Query(value = "insert into usr_activity_log values (?1, getdate(), null, ?2, ?3, ?4)", nativeQuery = true)
	void insertUsrActivityLog(String user_id, String successFlag, String timeoutFlag, String sessionId);
	
	@Modifying
	@Transactional
	@Query(value = "update usr_activity_log set logout_datetime = getdate() where session_id = ?1", nativeQuery = true)
	void updateUsrActivityLog(String sessionId);
}
