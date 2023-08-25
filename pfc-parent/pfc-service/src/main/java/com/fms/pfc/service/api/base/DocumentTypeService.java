package com.fms.pfc.service.api.base;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fms.pfc.domain.model.DocumentType;
import com.fms.pfc.repository.base.api.DocumentTypeRepository;

@Service
public class DocumentTypeService {

	@Autowired
	private DocumentTypeRepository docTypeRep;

	public List<DocumentType> listDocumentType() {
		return docTypeRep.listDocumentType();
	}

	public DocumentType searchDocType(int docTypeId) {
		return docTypeRep.searchDocType(docTypeId);
	}
}