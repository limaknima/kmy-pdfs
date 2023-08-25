package com.fms.pfc.domain.model;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "PASSWORD_HISTORY")
public class PassHistory {
	
    @Column(name = "user_id", nullable = false)
    private String userId;
	
    @Id
    @Column(name = "password")
    private String password;
    
    @Column(name = "created_datetime")
    private Date dateTime;
    
    public PassHistory() {
    	
    }
    
	public PassHistory(String userId, String password, Date dateTime) {
		this.userId = userId;
		this.password = password;
		this.dateTime = dateTime;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public Date getDateTime() {
		return dateTime;
	}

	public void setDateTime(Date dateTime) {
		this.dateTime = dateTime;
	}
    
	
	
}
