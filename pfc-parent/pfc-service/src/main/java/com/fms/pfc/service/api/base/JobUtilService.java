package com.fms.pfc.service.api.base;

import org.quartz.JobDataMap;
import org.quartz.JobDetail;
import org.springframework.context.ApplicationContext;
import org.springframework.scheduling.quartz.JobDetailFactoryBean;
import org.springframework.scheduling.quartz.QuartzJobBean;

public class JobUtilService {
	
	//Create Quartz Job
	protected static JobDetail createJob(Class<? extends QuartzJobBean> jobClass, boolean isDurable, 
			ApplicationContext context, String jobName, String jobGroup, String description){
	    JobDetailFactoryBean factoryBean = new JobDetailFactoryBean();
	    factoryBean.setJobClass(jobClass);
	    factoryBean.setDurability(isDurable);
	    factoryBean.setApplicationContext(context);
	    factoryBean.setName(jobName);
	    factoryBean.setGroup(jobGroup);
	    factoryBean.setDescription(description);
	    factoryBean.afterPropertiesSet();
        
	    return factoryBean.getObject();
	}
}
