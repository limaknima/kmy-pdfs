package com.fms.pfc.domain.model;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Entity
@EntityListeners(AuditingEntityListener.class)
@Table(name = "document")
public class Document {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "document_id", unique = true, nullable = false)
	private Integer id;

	@Column(name = "record_ref", nullable = false, length = 50)
	private String recordRef;

	@Column(name = "record_type_id", nullable = false)
	private Integer recordType;

	@Column(name = "document_type_id")
	private Integer documentType;

	@Column(name = "subject", length = 250)
	private String subject;

	@Column(name = "file_name", length = 250)
	private String fileName;

	@Column(name = "file_path", length = 500)
	private String filePath;

	@Column(name = "content_object")
	private byte[] contentObj;

	@Column(name = "created_datetime", updatable = false)
	private Date createdDate;

	@Column(name = "creator_id", updatable = false)
	private String createdBy;

	@Column(name = "modified_datetime")
	private Date lastModifiedDate;

	@Column(name = "modifier_id")
	private String modifiedBy;

	@Transient
	private int docRowNo;

	@Transient
	private String docTypeName;

	@Transient
	private String docRecStatus;

	public Document() {
	}

	public Document(Integer id, String recordRef, Integer recordType, Integer documentType, String subject,
			String fileName, String filePath, byte[] contentObj, Date createdDate, String createdBy,
			Date lastModifiedDate, String modifiedBy, DocumentType documentInfo) {
		this.id = id;
		this.recordRef = recordRef;
		this.recordType = recordType;
		this.documentType = documentType;
		this.subject = subject;
		this.fileName = fileName;
		this.filePath = filePath;
		this.contentObj = contentObj;
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

	public String getRecordRef() {
		return recordRef;
	}

	public void setRecordRef(String recordRef) {
		this.recordRef = recordRef;
	}

	public Integer getRecordType() {
		return recordType;
	}

	public void setRecordType(Integer recordType) {
		this.recordType = recordType;
	}

	public Integer getDocumentType() {
		return documentType;
	}

	public void setDocumentType(Integer documentType) {
		this.documentType = documentType;
	}

	public String getSubject() {
		return subject;
	}

	public void setSubject(String subject) {
		this.subject = subject;
	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public String getFilePath() {
		return filePath;
	}

	public void setFilePath(String filePath) {
		this.filePath = filePath;
	}

	public byte[] getContentObj() {
		return contentObj;
	}

	public void setContentObj(byte[] contentObj) {
		this.contentObj = contentObj;
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

	public int getDocRowNo() {
		return docRowNo;
	}

	public void setDocRowNo(int docRowNo) {
		this.docRowNo = docRowNo;
	}

	public String getDocTypeName() {
		return docTypeName;
	}

	public void setDocTypeName(String docTypeName) {
		this.docTypeName = docTypeName;
	}

	public String getDocRecStatus() {
		return docRecStatus;
	}

	public void setDocRecStatus(String docRecStatus) {
		this.docRecStatus = docRecStatus;
	}

}
