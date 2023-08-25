package com.fms.pfc.service.api.base;

import java.util.Locale;

import org.quartz.JobExecutionContext;
import org.quartz.Trigger;
import org.quartz.Trigger.CompletedExecutionInstruction;
import org.quartz.TriggerListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Component;

@Component
public class JobTriggersService implements TriggerListener {

	private final Logger logger = LoggerFactory.getLogger(JobTriggersService.class);

	private SchedulerActivityLogService schedulerActService;
	private MessageSource msgSource;

	@Autowired
	public JobTriggersService(SchedulerActivityLogService schedulerActService, MessageSource msgSource) {
		super();
		this.schedulerActService = schedulerActService;
		this.msgSource = msgSource;
	}

	@Override
	public String getName() {
		return "globalTrigger";
	}

	@Override
	public void triggerFired(Trigger trigger, JobExecutionContext context) {
		logger.info("TriggerListner.triggerFired() job=" + trigger.getJobKey().getName());
		schedulerActService.addSchedulerActivityLog(trigger.getJobKey().getName(), trigger.getJobKey().getGroup(),
				msgSource.getMessage("msgJobStarted", null, Locale.getDefault()));
	}

	@Override
	public boolean vetoJobExecution(Trigger trigger, JobExecutionContext context) {
		logger.info("TriggerListner.vetoJobExecution()");
		return false;
	}

	@Override
	public void triggerMisfired(Trigger trigger) {
		logger.info("TriggerListner.triggerMisfired() job=" + trigger.getJobKey().getName());
		schedulerActService.addSchedulerActivityLog(trigger.getJobKey().getName(), trigger.getJobKey().getGroup(),
				msgSource.getMessage("msgJobMisfired", null, Locale.getDefault()));
	}

	@Override
	public void triggerComplete(Trigger trigger, JobExecutionContext context,
			CompletedExecutionInstruction triggerInstructionCode) {
		logger.info("TriggerListner.triggerComplete() job=" + trigger.getJobKey().getName());
		schedulerActService.addSchedulerActivityLog(trigger.getJobKey().getName(), trigger.getJobKey().getGroup(),
				msgSource.getMessage("msgJobCompleted", null, Locale.getDefault()));
	}
}
