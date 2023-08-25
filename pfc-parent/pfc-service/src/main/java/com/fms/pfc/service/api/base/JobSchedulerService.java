package com.fms.pfc.service.api.base;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.quartz.JobDetail;
import org.quartz.JobKey;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.Trigger;
import org.quartz.Trigger.TriggerState;
import org.quartz.TriggerKey;
import org.quartz.impl.matchers.GroupMatcher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.MessageSource;
import org.springframework.scheduling.quartz.QuartzJobBean;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;
import org.springframework.stereotype.Service;

import com.fms.pfc.repository.base.api.JobSchedulerRepository;

@Service
public class JobSchedulerService implements JobSchedulerRepository {

	private final Logger logger = LoggerFactory.getLogger(JobSchedulerService.class);
	
	private SchedulerFactoryBean schedulerFactoryBean;
	private SchedulerService schedulerService;
	private ApplicationContext context;	
	private SchedulerActivityLogService schedulerActService;	
	private MessageSource msgSource;
	
	@Autowired
	public JobSchedulerService(SchedulerFactoryBean schedulerFactoryBean, SchedulerService schedulerService,
			ApplicationContext context, SchedulerActivityLogService schedulerActService,MessageSource msgSource) {
		super();
		this.schedulerFactoryBean = schedulerFactoryBean;
		this.schedulerService = schedulerService;
		this.context = context;
		this.schedulerActService = schedulerActService;
		this.msgSource = msgSource;
	}

	@Override
	public boolean scheduleCronJob(String jobName, String jobGroup, String description, Class<? extends QuartzJobBean> jobClass, Trigger cronTriggerBean, String userId) {
		String jobKey = jobName;
		String groupKey = jobGroup;
		String descriptionKey = description;

		JobDetail jobDetail = JobUtilService.createJob(jobClass, false, context, jobKey, groupKey, descriptionKey);

		try {
			Scheduler scheduler = schedulerFactoryBean.getScheduler();
			scheduler.scheduleJob(jobDetail, cronTriggerBean);
			
			doSchedulerActivityLog(jobKey, groupKey, "msgJobCreatedBy", userId);
			
			return true;
		} catch (SchedulerException e) {
			e.printStackTrace();
		}

		return false;
	}

	@Override
	public boolean updateCronJob(String jobName, String jobGroup, String description, Class<? extends QuartzJobBean> jobClass, Trigger cronTriggerBean, String userId) {
		String jobKey = jobName;
		String groupKey = jobGroup;
		String descriptionKey = description;

		try {
			deleteJob(jobKey, groupKey, userId);
			JobDetail jobDetail = JobUtilService.createJob(jobClass, false, context, jobKey, groupKey, descriptionKey);
			Scheduler scheduler = schedulerFactoryBean.getScheduler();
			scheduler.scheduleJob(jobDetail, cronTriggerBean);
			
			doSchedulerActivityLog(jobKey, groupKey, "msgJobModifiedBy", userId);
			
			return true;
		} catch ( Exception e ) {
			e.printStackTrace();
			return false;
		}
	}
	
	@Override
	public boolean unScheduleJob(String jobName, String jobGroup, String userId) {
		String jobKey = jobName;
		String groupKey = jobGroup;

		TriggerKey tkey = new TriggerKey(jobKey, groupKey);
		
		try {
			boolean status = schedulerFactoryBean.getScheduler().unscheduleJob(tkey);
			
			doSchedulerActivityLog(jobKey, groupKey, "msgJobStoppedBy", userId);
			
			return status;
		} catch (SchedulerException e) {
			e.printStackTrace();
			return false;
		}
	}

	@Override
	public boolean deleteJob(String jobName, String jobGroup, String userId) {
		String jobKey = jobName;
		String groupKey = jobGroup;

		JobKey jkey = new JobKey(jobKey, groupKey); 

		try {
			boolean status = schedulerFactoryBean.getScheduler().deleteJob(jkey);
			
			doSchedulerActivityLog(jobKey, groupKey, "msgJobDeletedBy", userId);
			
			return status;
		} catch (SchedulerException e) {
			e.printStackTrace();
			return false;
		}
	}

	@Override
	public boolean pauseJob(String jobName, String jobGroup, String userId) {
		String jobKey = jobName;
		String groupKey = jobGroup;
		JobKey jkey = new JobKey(jobKey, groupKey); 

		try {
			schedulerFactoryBean.getScheduler().pauseJob(jkey);
			
			doSchedulerActivityLog(jobKey, groupKey, "msgJobPausedBy", userId);
			
			return true;
		} catch (SchedulerException e) {
			e.printStackTrace();
			return false;
		}
	}

	@Override
	public boolean resumeJob(String jobName, String jobGroup, String userId) {
		String jobKey = jobName;
		String groupKey = jobGroup;

		JobKey jKey = new JobKey(jobKey, groupKey); 

		try {
			schedulerFactoryBean.getScheduler().resumeJob(jKey);
			
			doSchedulerActivityLog(jobKey, groupKey, "msgJobResumedBy", userId);
			
			return true;
		} catch (SchedulerException e) {
			e.printStackTrace();
			return false;
		}
	}

	@Override
	public boolean startJobNow(String jobName, String jobGroup, String userId) {
		String jobKey = jobName;
		String groupKey = jobGroup;

		JobKey jKey = new JobKey(jobKey, groupKey); 

		try {
			schedulerFactoryBean.getScheduler().triggerJob(jKey);
			
			doSchedulerActivityLog(jobKey, groupKey, "msgJobStartedBy", userId);
			
			return true;
		} catch (SchedulerException e) {
			e.printStackTrace();
			return false;
		}		
	}

	@Override
	public List<Map<String, Object>> getAllJobs() {
		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
		try {
			Scheduler scheduler = schedulerFactoryBean.getScheduler();

			for (String groupName : scheduler.getJobGroupNames()) {
				for (JobKey jobKey : scheduler.getJobKeys(GroupMatcher.jobGroupEquals(groupName))) {
					
					String jobName = jobKey.getName();
					String jobGroup = jobKey.getGroup();
					
					List<Trigger> triggers = (List<Trigger>) scheduler.getTriggersOfJob(jobKey);
					Date scheduleTime = triggers.get(0).getStartTime();
					Date nextFireTime = triggers.get(0).getNextFireTime();
					Date lastFiredTime = triggers.get(0).getPreviousFireTime();
					Date endScheduleTime = triggers.get(0).getEndTime();
					
					Map<String, Object> map = new HashMap<String, Object>();
					map.put("scheduleTime", scheduleTime);
					map.put("lastFiredTime", lastFiredTime);
					map.put("nextFireTime", nextFireTime);
					map.put("endScheduleTime", endScheduleTime);
					
					int nextFireTimeWithLastFiredTime = 0;
					boolean isStandbyMode = false;
					Date currentDate = new Date();
					
					if (!isStandbyMode) {
						if (scheduleTime.compareTo(currentDate) > 0) {
							schedulerService.updateJobStatus("Scheduled", schedulerService.searchIdJobScheduler(jobName, jobGroup));

						} else if (scheduleTime.compareTo(currentDate) < 0) {
							schedulerService.updateJobStatus("Standby", schedulerService.searchIdJobScheduler(jobName, jobGroup));
							isStandbyMode = true;
							nextFireTimeWithLastFiredTime = 1;

						} else if (scheduleTime.compareTo(currentDate) == 0) {
							schedulerService.updateJobStatus("Standby", schedulerService.searchIdJobScheduler(jobName, jobGroup));
							isStandbyMode = true;
							nextFireTimeWithLastFiredTime = 1;
						}
					}
					
					if (isStandbyMode && lastFiredTime != null && nextFireTime != null) {
						schedulerService.updateJobStatus("Running", schedulerService.searchIdJobScheduler(jobName, jobGroup));
					}
					
					if (nextFireTimeWithLastFiredTime == 1 && lastFiredTime != null && nextFireTime != null && nextFireTime.getDate() > lastFiredTime.getDate()) {
						schedulerService.updateJobStatus("Scheduled", schedulerService.searchIdJobScheduler(jobName, jobGroup));
						nextFireTimeWithLastFiredTime = 2;
					}
					
					if (nextFireTimeWithLastFiredTime == 2 && nextFireTime != null && currentDate.compareTo(nextFireTime) == 0) {
						schedulerService.updateJobStatus("Running", schedulerService.searchIdJobScheduler(jobName, jobGroup));
						nextFireTimeWithLastFiredTime = 1;
					}
					
					String jobStatus = getJobState(jobName, jobGroup);
					if (jobStatus != null) {
						schedulerService.updateJobStatus(jobStatus, schedulerService.searchIdJobScheduler(jobName, jobGroup));
					}

					list.add(map);
				}

			}
		} catch (SchedulerException e) {
			e.printStackTrace();
		}
		return list;
	}

	@Override
	public boolean isJobWithNamePresent(String jobName, String jobGroup) {
		try {
			String groupKey = jobGroup;
			JobKey jobKey = new JobKey(jobName, groupKey);
			Scheduler scheduler = schedulerFactoryBean.getScheduler();
			if (scheduler.checkExists(jobKey)){
				return true;
			}
		} catch (SchedulerException e) {
			e.printStackTrace();
		}
		return false;
	}
	
	public String getJobState(String jobName, String jobGroup) {
		try {
			JobKey jobKey = new JobKey(jobName, jobGroup);

			Scheduler scheduler = schedulerFactoryBean.getScheduler();
			JobDetail jobDetail = scheduler.getJobDetail(jobKey);

			List<? extends Trigger> triggers = scheduler.getTriggersOfJob(jobDetail.getKey());
			if(triggers != null && triggers.size() > 0){
				for (Trigger trigger : triggers) {
					TriggerState triggerState = scheduler.getTriggerState(trigger.getKey());

					if (TriggerState.PAUSED.equals(triggerState)) {
						return "Paused";
					}else if (TriggerState.BLOCKED.equals(triggerState)) {
						return "Blocked";
					}else if (TriggerState.ERROR.equals(triggerState)) {
						return "Error";
					}else if (TriggerState.NONE.equals(triggerState)) {
						return "None";
					}else if (TriggerState.COMPLETE.equals(triggerState)) {
						return "Completed";
					}
				}
			}
		} catch (SchedulerException e) {
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public boolean stopJob(String jobName, String jobGroup, String userId) {
		try{	
			String jobKey = jobName;
			String groupKey = jobGroup;

			Scheduler scheduler = schedulerFactoryBean.getScheduler();
			JobKey jkey = new JobKey(jobKey, groupKey);
			
			doSchedulerActivityLog(jobKey, groupKey, "msgJobStoppedBy", userId);

			return scheduler.interrupt(jkey);

		} catch (SchedulerException e) {
			e.printStackTrace();
		}
		return false;
	}
	
	private void doSchedulerActivityLog(String job, String group, String msgCode, String... param) {
		schedulerActService.addSchedulerActivityLog(job, group,
				msgSource.getMessage(msgCode, param, Locale.getDefault()));
	}
}
