package com.fms.pfc.domain.model;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.EntityListeners;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.LastModifiedBy;

import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import org.springframework.format.annotation.DateTimeFormat;

@Entity
@EntityListeners(AuditingEntityListener.class)
@Table(name = "document_type")
public class DocumentType {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "document_type_id")
	private Integer id;
	
	@Column(name = "document_type_name")
	private String name;
	
	@Column(name = "created_datetime", updatable = false)
	@CreatedDate
	@DateTimeFormat(pattern = "yyyy-MM-dd")
	private Date createdDate;
	
	@CreatedBy
	@Column(name = "creator_id", updatable = false)
	private String createdBy;
	
	@Column(name = "modified_datetime")
	@LastModifiedDate
	@DateTimeFormat(pattern = "yyyy-MM-dd")
	private Date lastModifiedDate;
	
	@Column(name = "modifier_id")
	@LastModifiedBy
	private String modifiedBy;
	
	public DocumentType() {
	}
	
	public DocumentType(Integer id, String name, Date createdDate, String createdBy, Date lastModifiedDate,
			String modifiedBy) {
		this.id = id;
		this.name = name;
		this.createdDate = createdDate;
		this.createdBy = createdBy;
		this.lastModifiedDate = lastModifiedDate;
		this.modifiedBy = modifiedBy;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Date getCreatedDate() {
		return createdDate;
	}

	public void setCreatedDate(Date createdDate) {
		this.createdDate = createdDate;
	}

	public String getCreatedBy() {
		return createdBy;
	}

	public void setCreatedBy(String createdBy) {
		this.createdBy = createdBy;
	}

	public Date getLastModifiedDate() {
		return lastModifiedDate;
	}

	public void setLastModifiedDate(Date lastModifiedDate) {
		this.lastModifiedDate = lastModifiedDate;
	}

	public String getModifiedBy() {
		return modifiedBy;
	}

	public void setModifiedBy(String modifiedBy) {
		this.modifiedBy = modifiedBy;
	}
}
