package com.fms.pfc.repository.base.api;

import com.fms.pfc.domain.model.Document;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

import javax.transaction.Transactional;

@Repository
public interface AttachmentRepository extends JpaRepository<Document, String> {
	
	@Query(value = "SELECT * FROM DOCUMENT "
			+ "WHERE RECORD_REF = ?1 "
			+ "AND RECORD_TYPE_ID = ?2 ", nativeQuery = true)
	List<Document> searchDocument(String recordRef, int recordTypeId);

	@Modifying
	@Transactional
	@Query(value = "DELETE FROM DOCUMENT WHERE DOCUMENT_ID = ?1", nativeQuery = true)
	void deleteDocument(int docId);
	
	@Modifying
	@Transactional
	@Query(value = "DELETE FROM DOCUMENT WHERE RECORD_REF = ?1 AND RECORD_TYPE_ID = ?2", nativeQuery = true)
	void deleteDocumentByRef(String recordRef, int recordTypeId);

	@Modifying
	@Transactional
	@Query(value = "INSERT INTO DOCUMENT (SUBJECT, RECORD_REF, RECORD_TYPE_ID, DOCUMENT_TYPE_ID, FILE_NAME, "
			+ "CONTENT_OBJECT, CREATOR_ID) "
			+ "VALUES (?1,?2,?3,?4,?5,?6,?7)", nativeQuery = true)
	void addDocument(String subject, String recordRef, int recordId, int docTypeId, String fileName, byte[] contentObject,
			String creatorId);

	@Modifying
	@Transactional
	@Query(value = "UPDATE DOCUMENT SET SUBJECT = ?1, DOCUMENT_TYPE_ID = ?2, FILE_NAME = ?3, "
			+ "CONTENT_OBJECT = ?4, MODIFIER_ID = ?5, MODIFIED_DATETIME = ?6 "
			+ "WHERE DOCUMENT_ID = ?7", nativeQuery = true)
	void updDocument(String subject, int docTypeId, String fileName, byte[] contentObject, String modifierId, Date date,
			int docId);
}
