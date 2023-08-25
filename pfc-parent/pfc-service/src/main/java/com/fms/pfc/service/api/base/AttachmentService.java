package com.fms.pfc.service.api.base;

import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fms.pfc.domain.model.Document;
import com.fms.pfc.repository.base.api.AttachmentRepository;

@Service
public class AttachmentService {

	private AttachmentRepository attachRes;

	@Autowired
	public AttachmentService(AttachmentRepository attachRes) {
		super();
		this.attachRes = attachRes;
	}

	public List<Document> searchDocument(String recordRef, int recordTypeId) {
		return attachRes.searchDocument(recordRef, recordTypeId);
	}

	public void deleteDocument(int docId) {
		attachRes.deleteDocument(docId);
	}

	public void deleteDocumentByRef(String recordRef, int recordTypeId) {
		attachRes.deleteDocumentByRef(recordRef, recordTypeId);
	}

	public void addDocument(String subject, String recordRef, int recordId, int docTypeId, String fileName,
			byte[] contentObject, String creatorId) {
		attachRes.addDocument(subject, recordRef, recordId, docTypeId, fileName, contentObject, creatorId);
	}

	public List<Document> findAll() {
		return attachRes.findAll();
	}

	public void updDocument(String subject, int docTypeId, String fileName, byte[] contentObject, String modifierId,
			Date date, int docId) {
		attachRes.updDocument(subject, docTypeId, fileName, contentObject, modifierId, date, docId);
	}
}