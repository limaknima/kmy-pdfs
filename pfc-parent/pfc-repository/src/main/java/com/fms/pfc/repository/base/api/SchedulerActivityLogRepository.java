package com.fms.pfc.repository.base.api;

import java.util.List;

import javax.transaction.Transactional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.fms.pfc.domain.model.SchedulerActivityLog;
import com.fms.pfc.domain.model.SchedulerActivityLogPK;

@Repository
public interface SchedulerActivityLogRepository extends JpaRepository<SchedulerActivityLog, SchedulerActivityLogPK> {
	
	@Query(value = "SELECT S.* FROM SCHEDULER_ACTIVITY_LOG S "
			+ "WHERE 1=1 "
			+ "AND S.TRIGGER_NAME = ?1 AND S.TRIGGER_GROUP = ?2 "
			+ "ORDER BY S.TRIGGER_ACTIVITY_TIMESTAMP DESC ", nativeQuery = true)
	List<SchedulerActivityLog> searchSchedulerActivityLog(String triggerName, String triggerGrp);
	
	@Modifying
	@Transactional
	@Query(value = "delete from SCHEDULER_ACTIVITY_LOG where TRIGGER_NAME =?1 and TRIGGER_GROUP=?2 and TRIGGER_ACTIVITY_TIMESTAMP=?3 ", nativeQuery = true)
	void deleteByIdNative(String name, String group, String ts);
}
