package com.fms.pfc.repository.base.api;

import java.util.List;

import javax.transaction.Transactional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.fms.pfc.domain.model.Task;

@Repository
public interface TaskRepository extends JpaRepository<Task, String> {

	/*@Query(value = "SELECT * FROM WF_TASK "
			+ "WHERE (?1 = '' OR CONVERT(DATE, START_DATETIME) >= CONVERT(DATETIME, ?1, 105)) "
			+ "AND (?2 = '' OR CONVERT(DATE, START_DATETIME) <= CONVERT(DATETIME, ?2, 105)) "
			+ "AND (?3 = '' OR (RECORD_REF LIKE CASE WHEN ?7 = '1' THEN CONCAT('%', ?3, '%') "
			+ "						 			  WHEN ?7 = '2' THEN CONCAT('%', ?3) "
			+ "        				  			  WHEN ?7 = '3' THEN ?3 "
			+ "        				   			  WHEN ?7 = '4' THEN CONCAT(?3, '%') END)) "
			+ "AND (?4 = '' OR (ACTOR_ID LIKE CASE WHEN ?8 = '1' THEN CONCAT('%', ?4, '%') "
			+ "					     			      WHEN ?8 = '2' THEN CONCAT('%', ?4) "
			+ "        				 			      WHEN ?8 = '3' THEN ?4 "
			+ "        				 		  	      WHEN ?8 = '4' THEN CONCAT(?4, '%') END)) "
			+ "AND (?5 = '' OR (TASK_DESC LIKE CASE WHEN ?9 = '1' THEN CONCAT('%', ?5, '%') "
			+ "                                   WHEN ?9 = '2' THEN CONCAT('%', ?5) "
			+ "									  WHEN ?9 = '3' THEN ?5"
			+ "									  WHEN ?9 = '4' THEN CONCAT(?5, '%') END)) "
			+ "AND ((?6 = 'Pending' AND END_DATETIME IS NULL) "
			+ "OR (?6 = 'Completed' AND END_DATETIME IS NOT NULL)) "
			+ "AND (POOLED_ACTOR_ID = ?10 OR POOLED_ACTOR_ID = ?11)",nativeQuery = true)
	List<Task> searchTask(String dateFr, String dateTo, String refNum, String assignedTo, String subject, 
			String taskStatus, String exp1, String exp2, String exp3, String roleId, String currUser);*/
	
	@Query(value = "SELECT * FROM ( SELECT "
			+ "CASE WHEN (RECORD_TYPE_ID = 201 AND ISNUMERIC(RECORD_REF) = 1) THEN (SELECT RAW_MATL_NAME FROM RAW_MATERIAL WHERE RAW_MATL_ID = RECORD_REF) "
			+ "WHEN (RECORD_TYPE_ID = 301 AND ISNUMERIC(RECORD_REF) = 1) THEN (SELECT PR_NAME FROM PRODUCT_RECIPE WHERE PR_ID = RECORD_REF) "
			+ "ELSE RECORD_REF END AS RECORD_REF, "
			+ "RECORD_TYPE_ID, START_DATETIME, END_DATETIME, DUE_DATETIME, "
			+ "ORG_ID, ACTOR_ID, POOLED_ACTOR_ID, TASK_DESC, TASK_ACTION, "
			+ "TASK_REMARKS, CREATOR_ID "
			+ "FROM WF_TASK ) tbl1 "
			+ "WHERE 1=1 "
			+ "AND (?1 = '' OR CONVERT(DATE, START_DATETIME) >= CONVERT(DATETIME, ?1, 105)) "
			+ "AND (?2 = '' OR CONVERT(DATE, START_DATETIME) <= CONVERT(DATETIME, ?2, 105)) "
			+ "AND (?3 = '' OR (RECORD_REF LIKE CASE WHEN ?7 = '1' THEN CONCAT('%', ?3, '%') "
			+ "						 			  WHEN ?7 = '2' THEN CONCAT('%', ?3) "
			+ "        				  			  WHEN ?7 = '3' THEN ?3 "
			+ "        				   			  WHEN ?7 = '4' THEN CONCAT(?3, '%') END)) "
			+ "AND (?4 = '' OR (ACTOR_ID LIKE CASE WHEN ?8 = '1' THEN CONCAT('%', ?4, '%') "
			+ "					     			      WHEN ?8 = '2' THEN CONCAT('%', ?4) "
			+ "        				 			      WHEN ?8 = '3' THEN ?4 "
			+ "        				 		  	      WHEN ?8 = '4' THEN CONCAT(?4, '%') END)) "
			+ "AND (?5 = '' OR (TASK_DESC LIKE CASE WHEN ?9 = '1' THEN CONCAT('%', ?5, '%') "
			+ "                                   WHEN ?9 = '2' THEN CONCAT('%', ?5) "
			+ "									  WHEN ?9 = '3' THEN ?5"
			+ "									  WHEN ?9 = '4' THEN CONCAT(?5, '%') END)) "
			+ "AND ((?6 = 'Pending' AND END_DATETIME IS NULL) "
			+ "OR (?6 = 'Completed' AND END_DATETIME IS NOT NULL)) "
			+ "AND (POOLED_ACTOR_ID = ?10 OR POOLED_ACTOR_ID = ?11)",nativeQuery = true)
	List<Task> searchTask(String dateFr, String dateTo, String refNum, String assignedTo, String subject, 
			String taskStatus, String exp1, String exp2, String exp3, String roleId, String currUser);
	
	@Query(value = "SELECT * FROM WF_TASK WHERE RECORD_REF = ?1 AND RECORD_TYPE_ID = ?2 ORDER BY START_DATETIME DESC", nativeQuery = true)
	List<Task> searchTaskByReference(String recordRef, int recordTypeId);
	
	@Query(value = "SELECT * FROM WF_TASK "
			+ "WHERE RECORD_REF = ?1 "
			+ "AND RECORD_TYPE_ID = ?2 "
			+ "AND START_DATETIME = ?3 ", nativeQuery = true)
	Task searchDetailTask(String recordRef, int recordTypeId, String startDateTime);

	@Modifying
	@Transactional
	@Query(value = "INSERT INTO WF_PROCESS (RECORD_REF, RECORD_TYPE_ID, PROCESS_TYPE, START_DATETIME, SOURCE_ORG_ID, CREATOR_ID) "
			+ "VALUES (?1, ?2, ?3, GETDATE(), ?4, ?5)", nativeQuery = true)
	void addProcess(String recordRef, int recordType, int processType, String orgId, String creatorId);

	@Modifying
	@Transactional
	@Query(value = "INSERT INTO WF_TASK (RECORD_REF, RECORD_TYPE_ID, PROCESS_TYPE, START_DATETIME, ORG_ID, POOLED_ACTOR_ID, "
			+ "POOLED_ACTOR_TYPE, TASK_DESC, CREATOR_ID, TASK_REMARKS) "
			+ "VALUES (?1, ?2, ?3, GETDATE(), ?4, ?5, ?6, ?7, ?8, ?9)", nativeQuery = true)
	void addTask(String recordRef, int recordType, int processType, String orgId, String pooledActorId, int pooledActorType,
			String actorId, String creatorId, String taskRemark);
	
	@Modifying
	@Transactional
	@Query(value = "UPDATE TOP(1) WF_TASK SET END_DATETIME = GETDATE(), ACTOR_ID = ?2, TASK_ACTION = ?3, TASK_REMARKS = CONCAT(TASK_REMARKS,' ', ?4) "
			+ "WHERE RECORD_REF = ?1 AND RECORD_TYPE_ID = ?5 "
			+ "AND START_DATETIME = (SELECT MAX(START_DATETIME) FROM WF_TASK WHERE RECORD_REF = ?1 AND RECORD_TYPE_ID = ?5) ", nativeQuery = true)
	void updEndTask(String recordRef, String actorId, int taskAction, String taskRemarks, int recordTypeId);
	
	@Query(value = "SELECT * FROM WF_TASK WHERE RECORD_REF = ?1 AND RECORD_TYPE_ID = ?2 "
			+ "AND START_DATETIME = (SELECT MAX(START_DATETIME) FROM WF_TASK WHERE RECORD_REF = ?1 AND RECORD_TYPE_ID = ?2) ", nativeQuery = true)
	Task searchLatestTask(String recordRef, int recordTypeId);
	
	@Query(value = "select top 1 * from WF_TASK w "
			+ "where 1=1 "
			+ "and (?1 = '' or w.RECORD_REF = ?1) "
			+ "and (?2 = 0 or w.RECORD_TYPE_ID = ?2) "
			+ "and (?3 = '' or w.POOLED_ACTOR_ID = ?3) "
			+ "and (?4 = 0 or w.TASK_ACTION = ?4) "
			//+ "and w.ACTOR_ID is not null "
			+ "and w.CREATOR_ID in ( "
			+ "	select u.USER_ID from USR u, USR_ROLE r "
			+ "	where 1=1 "
			+ "	and u.USER_ID = r.USER_ID "
			+ "	and (?5 = '' or r.ROLE_ID = ?5) "
			+ ") "
			+ "and (?6 = '' or w.TASK_REMARKS like concat(?6,'%')) "
			+ "order by w.START_DATETIME desc ", nativeQuery = true)
	Task searchLatestTask(String recordRef, int recordTypeId, String pooledActor, int actionType, String fromRole, String taskRemarks);
	
	@Query(value = "select top 1 * from WF_TASK w "
			+ "where 1=1 "
			+ "and (?1 = '' or w.RECORD_REF = ?1) "
			+ "and (?2 = 0 or w.RECORD_TYPE_ID = ?2) "
			+ "and (?3 = '' or w.POOLED_ACTOR_ID = ?3) "
			+ "and (?4 = 0 or w.TASK_ACTION = ?4) "
			//+ "and w.ACTOR_ID is not null "
			+ "and w.CREATOR_ID in ( "
			+ "	select u.USER_ID from USR u, USR_ROLE r "
			+ "	where 1=1 "
			+ "	and u.USER_ID = r.USER_ID "
			+ "	and (?5 = '' or r.ROLE_ID = ?5) "
			+ ") "
			+ "and (?6 = '' or w.TASK_REMARKS like concat(?6,'%')) "
			+ "order by w.START_DATETIME asc ", nativeQuery = true)
	Task searchFirstTask(String recordRef, int recordTypeId, String pooledActor, int actionType, String fromRole, String taskRemarks);
	
	@Query(value = "select w.* from WF_TASK w "
			+ "where 1=1 "
			+ "and w.START_DATETIME is not null and w.END_DATETIME is null and w.ACTOR_ID is null "
			+ "and (?1 = '' OR w.RECORD_REF = ?1) "
			+ "and (?2 = 0 OR w.RECORD_TYPE_ID = ?2) "
			+ "and (?3 = '' OR w.POOLED_ACTOR_ID = ?3) "
			+ "and (?4 = 0 OR w.POOLED_ACTOR_TYPE = ?4) "
			+ "order by  w.RECORD_TYPE_ID, w.START_DATETIME desc", nativeQuery = true)
	List<Task> searchOverdueTask(String recordRef, int recordType, String poolActor, int actorType);
}
