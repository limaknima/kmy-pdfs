package com.fms.pfc.service.api.base;

import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fms.pfc.domain.model.JobScheduler;
import com.fms.pfc.repository.base.api.SchedulerRepository;

@Service
public class SchedulerService {

	@Autowired
	private SchedulerRepository schedulerRes;

	public List<JobScheduler> searchJobScheduler(String jobGroup, String jobName, String jobStatus, String exp1) {
		return schedulerRes.searchJobScheduler(jobGroup, jobName, jobStatus, exp1);
	}
	
	public String searchIdJobScheduler(String jobName, String jobGroup) {
		return schedulerRes.searchIdJobScheduler(jobName, jobGroup);
	}
	
	public List<String> searchAllJobGroup() {
		return schedulerRes.searchAllJobGroup();
	}
	
	public List<String> searchAllJobStatus() {
		return schedulerRes.searchAllJobStatus();
	}
	
	public JobScheduler searchOnlyOneJob(String jobId) {
		return schedulerRes.searchOnlyOneJob(jobId);
	}
	
	public void addJobScheduler(String jobName, String jobGroup, String jobDescription, Date startDate, Date endDate, String performTask, String createdUser, String taskOpt, String taskOptDet) {
		schedulerRes.addJobScheduler(jobName, jobGroup, jobDescription, startDate, endDate, performTask, createdUser, taskOpt, taskOptDet);
	}
	
	public void addLogJobScheduler(String triggerName, String triggerGroup, Date timeStamp, String triggerDesc) {
		schedulerRes.addLogJobScheduler(triggerName, triggerGroup, timeStamp, triggerDesc);
	}
	
	public void updateJobScheduler(String jobDescription, Date startDate, Date endDate, String performTask, String updatedUser, String jobId, String taskOpt, String taskOptDet) {
		schedulerRes.updateJobScheduler(jobDescription, startDate, endDate, performTask, updatedUser, jobId, taskOpt, taskOptDet);
	}
	
	public void updateJobStatus(String jobStatus, String jobId) {
		schedulerRes.updateJobStatus(jobStatus, jobId);
	}
	
	public void deleteJobScheduler(String jobId) {
		schedulerRes.deleteJobScheduler(jobId);
	}
}
