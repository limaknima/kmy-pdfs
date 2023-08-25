package com.fms.pfc.domain.model.main;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import javax.persistence.Transient;//Kimberley 20210109

@Entity
@Table(name = "REGULATION_DOC")
public class RegulationDoc {

	@Id
	@Column(name = "REG_DOC_ID")
	private String regDocId;

	@Column(name = "REG_ID")
	private String regId;

	@Column(name = "REG_DOC_CAT_ID")
	private String regDocCatId;

	@Column(name = "REG_DOC_CAT_GRP_ID")
	private String regDocCatGrpId;

	@Column(name = "FILE_NAME")
	private String fileName;

	@Column(name = "FILE_PATH")
	private String filePath;

	@Column(name = "DOC_TYPE")
	private String docType;

	@Column(name = "CONTENT_OBJECT", length=1000)
	private byte[] contentObject;

	@Column(name = "ARCHIVED_FLAG")
	private String archivedFlag;

	@Column(name = "VERSION")
	private String version;

	@Column(name = "CRC_VALUE")
	private String crcValue;

	@Column(name = "CREATOR_ID")
	private String creatorId;

	@Column(name = "CREATED_DATETIME")
	private String createdDatetime;

	@Column(name = "MODIFIER_ID")
	private String modifierId;

	@Column(name = "MODIFIED_DATETIME")
	private String modifiedDatetime;
	
	//Start Kimberley 20210109
	@Transient
	private int rowNo;
	@Transient
	private String regDocCatName;
	@Transient
	private String regDocCatGrpName;
	@Transient
	private String indicator;
	//End Kimberley 20210109
	@Transient
	private String fileNameDis;
	
	public String getArchiveDesc() {
		return (this.archivedFlag.equals("Y")?"Yes":"No");
	}

	public String getRegDocId() {
		return regDocId;
	}

	public void setRegDocId(String regDocId) {
		this.regDocId = regDocId;
	}

	public String getRegId() {
		return regId;
	}

	public void setRegId(String regId) {
		this.regId = regId;
	}

	public String getRegDocCatId() {
		return regDocCatId;
	}

	public void setRegDocCatId(String regDocCatId) {
		this.regDocCatId = regDocCatId;
	}

	public String getRegDocCatGrpId() {
		return regDocCatGrpId;
	}

	public void setRegDocCatGrpId(String regDocCatGrpId) {
		this.regDocCatGrpId = regDocCatGrpId;
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

	public String getDocType() {
		return docType;
	}

	public void setDocType(String docType) {
		this.docType = docType;
	}

	public byte[] getContentObject() {
		return contentObject;
	}

	public void setContentObject(byte[] contentObject) {
		this.contentObject = contentObject;
	}

	public String getArchivedFlag() {
		return archivedFlag;
	}

	public void setArchivedFlag(String archivedFlag) {
		this.archivedFlag = archivedFlag;
	}

	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	public String getCrcValue() {
		return crcValue;
	}

	public void setCrcValue(String crcValue) {
		this.crcValue = crcValue;
	}

	public String getCreatorId() {
		return creatorId;
	}

	public void setCreatorId(String creatorId) {
		this.creatorId = creatorId;
	}

	public String getCreatedDatetime() {
		return createdDatetime;
	}

	public void setCreatedDatetime(String createdDatetime) {
		this.createdDatetime = createdDatetime;
	}

	public String getModifierId() {
		return modifierId;
	}

	public void setModifierId(String modifierId) {
		this.modifierId = modifierId;
	}

	public String getModifiedDatetime() {
		return modifiedDatetime;
	}

	public void setModifiedDatetime(String modifiedDatetime) {
		this.modifiedDatetime = modifiedDatetime;
	}

	//Start Kimberley 20210109
	public int getRowNo() {
		return rowNo;
	}
	public void setRowNo(int rowNo) {
		this.rowNo = rowNo;
	}
	
	public String getRegDocCatName() {
		return regDocCatName;
	}
	public void setRegDocCatName(String regDocCatName) {
		this.regDocCatName = regDocCatName;
	}
	
	public String getRegDocCatGrpName() {
		return regDocCatGrpName;
	}
	public void setRegDocCatGrpName(String regDocCatGrpName) {
		this.regDocCatGrpName = regDocCatGrpName;
	}

	public String getIndicator() {
		return indicator;
	}
	public void setIndicator(String indicator) {
		this.indicator = indicator;
	}
	//End Kimberley 20210109

	public String getFileNameDis() {
		return fileNameDis;
	}

	public void setFileNameDis(String fileNameDis) {
		this.fileNameDis = fileNameDis;
	}

	
}
