package com.fms.pfc.app;

import java.io.IOException;
import java.util.Properties;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.PropertiesFactoryBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;
import com.fms.pfc.service.api.base.AutowiringSpringBeanJobFactory;

import com.fms.pfc.service.api.base.JobTriggersService;
import com.fms.pfc.service.api.base.JobListenerService;
 
@Configuration
public class QuartzSchedulerConfig {
 
    @Autowired
    DataSource dataSource;
 
    @Autowired
    private ApplicationContext applicationContext;
     
    @Autowired
    private JobTriggersService triggerListner;

    @Autowired
    private JobListenerService jobsListener;
    
    //Create Scheduler
    @Bean
    public SchedulerFactoryBean schedulerFactoryBean() throws IOException {
 
        SchedulerFactoryBean factory = new SchedulerFactoryBean();
        factory.setOverwriteExistingJobs(true);
        factory.setDataSource(dataSource);
        factory.setQuartzProperties(quartzProperties());
        
        factory.setGlobalTriggerListeners(triggerListner);
        factory.setGlobalJobListeners(jobsListener);
        
        AutowiringSpringBeanJobFactory jobFactory = new AutowiringSpringBeanJobFactory();
        jobFactory.setApplicationContext(applicationContext);
        factory.setJobFactory(jobFactory);
        
        return factory;
    }
 
    //Configure Quartz using properties file
    @Bean
    public Properties quartzProperties() throws IOException {
        PropertiesFactoryBean propertiesFactoryBean = new PropertiesFactoryBean();
        propertiesFactoryBean.setLocation(new ClassPathResource("/quartz.properties"));
        propertiesFactoryBean.afterPropertiesSet();
        return propertiesFactoryBean.getObject();
    }
 
  
}
