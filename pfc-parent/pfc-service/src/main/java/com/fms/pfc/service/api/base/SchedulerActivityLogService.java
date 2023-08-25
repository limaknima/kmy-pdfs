package com.fms.pfc.service.api.base;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fms.pfc.domain.model.SchedulerActivityLog;
import com.fms.pfc.domain.model.SchedulerActivityLogPK;
import com.fms.pfc.exception.CommonException;
import com.fms.pfc.repository.base.api.SchedulerActivityLogRepository;

@Service
public class SchedulerActivityLogService {
	
	private final Logger logger = LoggerFactory.getLogger(SchedulerActivityLogService.class);
	
	private SchedulerActivityLogRepository schedulerActRepo;

	@Autowired
	public SchedulerActivityLogService(SchedulerActivityLogRepository schedulerActRepo) {
		super();
		this.schedulerActRepo = schedulerActRepo;
	}
	
	public List<SchedulerActivityLog> searchSchedulerActivityLog(String triggerName, String triggerGrp) {
		return schedulerActRepo.searchSchedulerActivityLog(triggerName, triggerGrp);
	}
	
	public void addSchedulerActivityLog(String triggerName, String triggerGrp, String activityDesc) {
		SchedulerActivityLogPK pk = new SchedulerActivityLogPK();
		pk.setTriggerGroup(triggerGrp);
		pk.setTriggerName(triggerName);
		pk.setTriggerActivityTimestamp(new Date());
		
		SchedulerActivityLog schedulerActivityLog = new SchedulerActivityLog();
		schedulerActivityLog.setSchedulerActivityLogPK(pk);
		schedulerActivityLog.setTriggerActivityDesc(activityDesc);		
		
		schedulerActRepo.saveAndFlush(schedulerActivityLog);
	}	
	
	public void deleteById(String... param) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
		SchedulerActivityLogPK pk = null;
		
		try {
		
			pk = new SchedulerActivityLogPK(param[0], param[1], sdf.parse(param[2]));
			this.deleteById(pk);
		} catch (ParseException e) {
			e.printStackTrace();
			throw new CommonException();
		}
	}
	
	public void deleteByIdNative(String... param) {
		try {
			schedulerActRepo.deleteByIdNative(param[0], param[1], param[2]);
		} catch (Exception e) {
			e.printStackTrace();
			throw new CommonException();
		}
	}

	public void deleteById(SchedulerActivityLogPK pk) {
		schedulerActRepo.deleteById(pk);
	}
	

}
