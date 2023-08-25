package com.fms.pfc.repository.base.api;

import java.util.List;
import java.util.Map;

import org.quartz.Trigger;
import org.springframework.scheduling.quartz.QuartzJobBean;

public interface JobSchedulerRepository {
	
	boolean scheduleCronJob(String jobName, String jobGroup, String description, Class<? extends QuartzJobBean> jobClass, Trigger cronTriggerBean, String userId);
	
	boolean updateCronJob(String jobName, String jobGroup, String description, Class<? extends QuartzJobBean> jobClass, Trigger cronTriggerBean, String userId);
	
	boolean unScheduleJob(String jobName, String jobGroup, String userId);
	
	boolean deleteJob(String jobName, String jobGroup, String userId);
	
	boolean pauseJob(String jobName, String jobGroup, String userId);
	
	boolean resumeJob(String jobName, String jobGroup, String userId);
	
	boolean startJobNow(String jobName, String jobGroup, String userId);
	
	List<Map<String, Object>> getAllJobs();
	
	boolean isJobWithNamePresent(String jobName, String jobGroup);
	
	String getJobState(String jobName, String jobGroup);
	
	boolean stopJob(String jobName, String jobGroup, String userId);
}