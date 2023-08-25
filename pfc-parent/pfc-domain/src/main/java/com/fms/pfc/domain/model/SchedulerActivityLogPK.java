package com.fms.pfc.domain.model;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Embeddable;

@Embeddable
public class SchedulerActivityLogPK implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private String triggerName;
	private String triggerGroup;
	private Date triggerActivityTimestamp;	
	
	public SchedulerActivityLogPK() {
		super();
	}	
	
	public SchedulerActivityLogPK(String triggerName, String triggerGroup, Date triggerActivityTimestamp) {
		super();
		this.triggerName = triggerName;
		this.triggerGroup = triggerGroup;
		this.triggerActivityTimestamp = triggerActivityTimestamp;
	}

	@Column(name="TRIGGER_NAME", nullable=false)
	public String getTriggerName() {
		return triggerName;
	}
	public void setTriggerName(String triggerName) {
		this.triggerName = triggerName;
	}
	
	@Column(name="TRIGGER_GROUP", nullable=false)
	public String getTriggerGroup() {
		return triggerGroup;
	}
	public void setTriggerGroup(String triggerGroup) {
		this.triggerGroup = triggerGroup;
	}
	
	@Column(name="TRIGGER_ACTIVITY_TIMESTAMP")
	public Date getTriggerActivityTimestamp() {
		return triggerActivityTimestamp;
	}
	public void setTriggerActivityTimestamp(Date triggerActivityTimestamp) {
		this.triggerActivityTimestamp = triggerActivityTimestamp;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((triggerActivityTimestamp == null) ? 0 : triggerActivityTimestamp.hashCode());
		result = prime * result + ((triggerGroup == null) ? 0 : triggerGroup.hashCode());
		result = prime * result + ((triggerName == null) ? 0 : triggerName.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		
		SchedulerActivityLogPK other = (SchedulerActivityLogPK) obj;
		if (triggerActivityTimestamp == null) {
			if (other.triggerActivityTimestamp != null)
				return false;
		} else if (!triggerActivityTimestamp.equals(other.triggerActivityTimestamp))
			return false;
		if (triggerGroup == null) {
			if (other.triggerGroup != null)
				return false;
		} else if (!triggerGroup.equals(other.triggerGroup))
			return false;
		if (triggerName == null) {
			if (other.triggerName != null)
				return false;
		} else if (!triggerName.equals(other.triggerName))
			return false;
		return true;
	}	
	
	@Override
	public String toString() {
		return "tn="+this.triggerName+";tg="+this.triggerGroup+";tt="+this.triggerActivityTimestamp;
	}

}
