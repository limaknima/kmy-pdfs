package com.fms.pfc.domain.model;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "SCHEDULER_ACTIVITY_LOG")
public class SchedulerActivityLog implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private SchedulerActivityLogPK schedulerActivityLogPK;
	private String triggerActivityDesc;
		
	public SchedulerActivityLog() {
		super();
	}
	
	public SchedulerActivityLog(SchedulerActivityLogPK schedulerActivityLogPK, String triggerActivityDesc) {
		super();
		this.schedulerActivityLogPK = schedulerActivityLogPK;
		this.triggerActivityDesc = triggerActivityDesc;
	}
	
	@Id
	public SchedulerActivityLogPK getSchedulerActivityLogPK() {
		return schedulerActivityLogPK;
	}
	public void setSchedulerActivityLogPK(SchedulerActivityLogPK schedulerActivityLogPK) {
		this.schedulerActivityLogPK = schedulerActivityLogPK;
	}
	
	@Column(name="TRIGGER_ACTIVITY_DESC")
	public String getTriggerActivityDesc() {
		return triggerActivityDesc;
	}
	public void setTriggerActivityDesc(String triggerActivityDesc) {
		this.triggerActivityDesc = triggerActivityDesc;
	}
	
	

}
