package com.fms.pfc.repository.main.api;

import java.util.List;

import javax.transaction.Transactional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.fms.pfc.domain.model.main.RegulationDoc;

@Repository
public interface RegulationDocRepository extends JpaRepository<RegulationDoc, Integer> {
	 
	//Search query
	@Query(value = "SELECT * "
			+ "FROM REGULATION_DOC "
			+ "WHERE REG_DOC_ID = ?1 ", nativeQuery = true)
	List<RegulationDoc> getDocument(int fileId);
	
	//Start Kimberley 20210109
	// Search top 4 rows query
	@Query(value = "SELECT TOP 4 * "
			+ "FROM REGULATION_DOC "
			+ "WHERE REG_ID = ?1 "
			+ "AND ARCHIVED_FLAG = 'N'"
			+ "ORDER BY CREATED_DATETIME DESC", nativeQuery = true)
	List<RegulationDoc> getDocumentDesc(int fileId);
	
	//Search query
	@Query(value = "SELECT * "
			+ "FROM REGULATION_DOC "
			+ "WHERE REG_ID = ?1 "
			+ "AND ARCHIVED_FLAG = 'N'", nativeQuery = true)
	List<RegulationDoc> getDocListByRegId(int regId);
	
	@Query(value = "SELECT * "
			+ "FROM REGULATION_DOC "
			+ "WHERE REG_DOC_ID = ?1 ", nativeQuery = true)
	RegulationDoc getRegDoc(int regDocId);
	
	@Modifying
	@Transactional
	@Query(value = "INSERT INTO REGULATION_DOC "
			+ "(REG_ID, REG_DOC_CAT_ID, REG_DOC_CAT_GRP_ID, FILE_NAME, FILE_PATH, "
			+ "DOC_TYPE, CONTENT_OBJECT, ARCHIVED_FLAG, VERSION, CRC_VALUE, CREATOR_ID) "
			+ "VALUES "
			+ "((SELECT REG_ID FROM REGULATION WHERE REG_NAME = ?1 AND RAW_MATL_ID = ?2 and COUNTRY_ID = ?3), ?4, ?5, ?6, ?7, "
			+ "?8, ?9, ?10, ?11, ?12, ?13) ", nativeQuery = true)

	void addRegulationDoc(String regName, int rawMatlId, String countryId, String regDocCatId, String regDocCatGrpId, String fileName, String filepath,
			String docType, byte[] contentObj, String archivedFlag, String version, String crcValue, String creatorId);
	
	@Modifying
	@Transactional
	@Query(value = "UPDATE REGULATION_DOC "
			+ "SET [REG_DOC_CAT_ID] = ?1, "
			+ "	[REG_DOC_CAT_GRP_ID] = ?2, "
			+ "	[FILE_NAME] = ?3, "
			+ "	[FILE_PATH] = ?4, "
			+ "	[DOC_TYPE] = ?5, "
			+ "	[CONTENT_OBJECT] = ?6, "
			+ "	[ARCHIVED_FLAG] = ?7, "
			+ "	[VERSION] = ?8, "
			+ "	[CRC_VALUE] = ?9, "
			+ "	[MODIFIER_ID] = ?10 "
			+ "WHERE [REG_DOC_ID] = ?11 ", nativeQuery = true)
	void updRegulationDoc(String regDocCatId, String regDocCatGrpId, String fileName, String filepath,
			String docType, byte[] contentObj, String archivedFlag, String version, String crcValue, 
			String modifierId, String regDocId);
	
	@Modifying
	@Transactional
	@Query(value = "DELETE FROM REGULATION_DOC WHERE REG_DOC_ID = ?1", nativeQuery = true)
	void delRegDocByRegDocId(String regDocId);
	
	//End Kimberley 20210109 	
}
