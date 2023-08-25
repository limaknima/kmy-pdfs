package com.fms.pfc.repository.base.api;

import com.fms.pfc.domain.model.JobScheduler;

import java.util.Date;
import java.util.List;

import javax.transaction.Transactional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

public interface SchedulerRepository extends JpaRepository<JobScheduler, String>{
	@Query(value = "select * from scheduler_activity "
			+ "where (?1 = '' or (lower(job_group) = lower(?1))) "
			+ "and (?2 = '' or (lower(job_name) like case when ?4 = '1' then lower(concat('%', ?2, '%')) "
			+ "									when ?4 = '2' then lower(concat('%', ?2)) "
			+ "									when ?4 = '3' then lower(?2) "
			+ "									when ?4 = '4' then lower(concat(?2, '%')) end)) "
			+ "and (?3 = '' or (lower(job_status) = lower(?3))) ", nativeQuery = true)
	List<JobScheduler> searchJobScheduler(String jobGroup, String jobName, String jobStatus, String exp1);
	
	@Query(value = "select job_id from scheduler_activity " 
			+ "where job_name = ?1 and job_group = ?2 ", nativeQuery = true)
	String searchIdJobScheduler(String jobName, String jobGroup);
	
	@Query(value = "select distinct job_group from scheduler_activity", nativeQuery = true)
	List<String> searchAllJobGroup();
	
	@Query(value = "select distinct job_status from scheduler_activity", nativeQuery = true)
	List<String> searchAllJobStatus();
	
	@Query(value = "select * from scheduler_activity "
			+ "where job_id = ?1 ", nativeQuery = true)
	JobScheduler searchOnlyOneJob(String jobId);
	
	@Modifying(clearAutomatically = true)
	@Transactional
	@Query(value = "insert into scheduler_activity (job_name, job_group, job_description, start_date, end_date, perform_task, creator_id, task_opt, task_opt_det) VALUES "
			+ "(?1, ?2, ?3, ?4, ?5, ?6, ?7, ?8, ?9) ", nativeQuery = true)
	void addJobScheduler(String jobName, String jobGroup, String jobDescription, Date startDate, Date endDate, String performTask, String createdUser, String taskOpt, String taskOptDet);
	
	@Modifying
	@Transactional
	@Query(value = "insert into scheduler_activity_log (trigger_name, trigger_group, trigger_activity_timestamp, trigger_activity_desc) VALUES "
			+ "(?1, ?2, ?3, ?4) ", nativeQuery = true)
	void addLogJobScheduler(String triggerName, String triggerGroup, Date timeStamp, String triggerDesc);
	
	@Modifying(clearAutomatically = true)
	@Transactional
	@Query(value = "update scheduler_activity set "
			+ "	job_description = ?1, "
			+ " start_date = ?2, "
			+ " end_date = ?3, "
			+ " perform_task = ?4, "
			+ "	modifier_id = ?5, "
			+ "	modified_datetime = GETDATE(), "
			+ " task_opt = ?7, "
			+ " task_opt_det = ?8 "
			+ " where job_id = ?6  ", nativeQuery = true)
	void updateJobScheduler(String jobDescription, Date startDate, Date endDate, String performTask, String updatedUser, String jobId, String taskOpt, String taskOptDet);
	
	@Modifying(clearAutomatically = true)
	@Transactional
	@Query(value = "update scheduler_activity set "
			+ " job_status = ?1 "
			+ " where job_id = ?2 ", nativeQuery = true)
	void updateJobStatus(String jobStatus, String jobId);
	
	@Modifying(clearAutomatically = true)
	@Transactional
	@Query(value = "delete from scheduler_activity WHERE job_id = ?1 ", nativeQuery = true)
	void deleteJobScheduler(String jobId);
}
