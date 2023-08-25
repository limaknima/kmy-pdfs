package com.fms.pfc.repository.base.api;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.fms.pfc.domain.model.DocumentType;

@Repository
public interface DocumentTypeRepository extends JpaRepository<DocumentType, String> {

	@Query(value = "SELECT * FROM DOCUMENT_TYPE", nativeQuery = true)
	List<DocumentType> listDocumentType();

	@Query(value = "SELECT * FROM DOCUMENT_TYPE WHERE DOCUMENT_TYPE_ID = ?1", nativeQuery = true)
	DocumentType searchDocType(int docTypeId);
}
