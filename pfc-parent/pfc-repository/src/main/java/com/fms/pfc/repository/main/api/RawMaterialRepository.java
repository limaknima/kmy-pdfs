package com.fms.pfc.repository.main.api;

import java.util.Date;
import java.util.List;

import javax.transaction.Transactional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.fms.pfc.domain.model.main.RawMaterial;

@Repository
public interface RawMaterialRepository extends JpaRepository<RawMaterial, String> {

	@Query(value = "SELECT * FROM RAW_MATERIAL "
			+ "WHERE (?1 = '' OR (RAW_MATL_NAME LIKE CASE WHEN ?2 = '1' THEN CONCAT('%', ?1, '%') "
			+ "						 			  WHEN ?2 = '2' THEN CONCAT('%', ?1) "
			+ "        				  			  WHEN ?2 = '3' THEN ?1 "
			+ "        				   			  WHEN ?2 = '4' THEN CONCAT(?1, '%') END)) "
			+ "AND (?3 = 0 OR RAW_MATL_ID = ?3) "
			+ "AND (?4 = 0 OR RECORD_STATUS = ?4) "
			+ "ORDER BY RAW_MATL_NAME", nativeQuery = true)
	List<RawMaterial> searchRawMaterial(String rawMaterial, String expr, int rawMatlId, int recordStatus);

	@Modifying
	@Transactional
	@Query(value = "DELETE FROM RAW_MATERIAL WHERE RAW_MATL_ID = ?1", nativeQuery = true)
	void delRawMatl(int rawMatlId);

	@Modifying
	@Transactional
	@Query(value = "INSERT INTO RAW_MATERIAL "
			+ "(RAW_MATL_NAME, TS_NO, REMARKS, REMARKS_USER_ID, RECORD_STATUS, INS_NO, E_NO, FEMA_NO, JECFA_NO, "
			+ "CAS_NO, GMO_STATUS, FLAVOR_STATUS_ID, PHO_FLAG, CREATOR_ID, CREATED_DATETIME, OPT_LOCK) "
			+ "VALUES "
			+ "(?1, ?2, ?3, ?4, ?5, ?6, ?7, ?8, ?9, ?10, ?11, ?12, ?13, ?14, ?15, 1)", nativeQuery = true)

	void addRawMatl(String matlName, String tsNo, String remark, String remarkUser, int recordStatus, String insNo,
			String eNo, String femaNo, String jecfaNo, String casNo, String gmoStatus, Integer favStatus, String phoFlag,
			String creatorId, Date date);
	
	@Modifying
	@Transactional
	@Query(value = "UPDATE RAW_MATERIAL SET RAW_MATL_NAME = ?2, REMARKS = ?3, RECORD_STATUS = ?4, INS_NO = ?5, E_NO = ?6, "
			+ "FEMA_NO = ?7, JECFA_NO = ?8, CAS_NO = ?9, GMO_STATUS = ?10, FLAVOR_STATUS_ID = ?11, PHO_FLAG = ?12, "
			+ "MODIFIER_ID = ?13, REMARKS_USER_ID = ?14, MODIFIED_DATETIME = ?16, CHECKER_ID = ?15, "
			+ "SUBMITTED_DATETIME = (CASE WHEN ?15 != '' THEN ?16 WHEN ?15 = '' THEN SUBMITTED_DATETIME END)"
			+ "WHERE RAW_MATL_ID = ?1", nativeQuery = true)
	void updRawMatl(int rawMatlId, String matlName, String remark, int recordStatus, String insNo,
			String eNo, String femaNo, String jecfaNo, String casNo, String gmoStatus, Integer favStatus, String phoFlag,
			String creatorId, String remarkUser, String checkerId, Date date);
	
	@Modifying
	@Transactional
	@Query(value = "UPDATE RAW_MATERIAL SET OPT_LOCK = OPT_LOCK+1, RAW_MATL_NAME = ?2, REMARKS = ?3, RECORD_STATUS = ?4, INS_NO = ?5, E_NO = ?6, "
			+ "FEMA_NO = ?7, JECFA_NO = ?8, CAS_NO = ?9, GMO_STATUS = ?10, FLAVOR_STATUS_ID = ?11, PHO_FLAG = ?12, "
			+ "MODIFIER_ID = ?13, REMARKS_USER_ID = ?14, MODIFIED_DATETIME = ?16, CHECKER_ID = ?15, "
			+ "SUBMITTED_DATETIME = (CASE WHEN ?15 != '' THEN ?16 WHEN ?15 = '' THEN SUBMITTED_DATETIME END)"
			+ "WHERE 1=1 AND RAW_MATL_ID = ?1 AND (0 = ?17 OR OPT_LOCK = ?17) ", nativeQuery = true)
	void updRawMatlWithOptLock(int rawMatlId, String matlName, String remark, int recordStatus, String insNo,
			String eNo, String femaNo, String jecfaNo, String casNo, String gmoStatus, Integer favStatus, String phoFlag,
			String creatorId, String remarkUser, String checkerId, Date date, Integer optLock);
	
	@Modifying
	@Transactional
	@Query(value = "UPDATE RAW_MATERIAL SET RECORD_STATUS = ?2, MODIFIER_ID = ?3, MODIFIED_DATETIME = ?4, "
			+ "CHECKER_ID = (CASE WHEN ?2 = 3 THEN ?3 WHEN ?2 != 3 THEN CHECKER_ID END), "
			+ "SUBMITTED_DATETIME = (CASE WHEN ?2 = 3 THEN ?4 WHEN ?2 != 3 THEN SUBMITTED_DATETIME END) "
			+ "WHERE RAW_MATL_ID = ?1", nativeQuery = true)
	void updRawMatlStatus(int rawMatlId, int recordStatus, String modifierId, Date date);
	
	@Modifying
	@Transactional
	@Query(value = "UPDATE RAW_MATERIAL SET OPT_LOCK = OPT_LOCK+1, RECORD_STATUS = ?2, MODIFIER_ID = ?3, MODIFIED_DATETIME = ?4, "
			+ "CHECKER_ID = (CASE WHEN ?2 = 3 THEN ?3 WHEN ?2 != 3 THEN CHECKER_ID END), "
			+ "SUBMITTED_DATETIME = (CASE WHEN ?2 = 3 THEN ?4 WHEN ?2 != 3 THEN SUBMITTED_DATETIME END), "
			+ "REMARKS = ?6 "
			+ "WHERE 1=1 AND RAW_MATL_ID = ?1 AND (0 = ?5 OR OPT_LOCK = ?5) ", nativeQuery = true)
	void updRawMatlStatusWithOptLock(int rawMatlId, int recordStatus, String modifierId, Date date, Integer optLock, String remarks);
	
	@Modifying
	@Transactional
	@Query(value = "UPDATE RAW_MATERIAL SET OPT_LOCK = OPT_LOCK+1, RECORD_STATUS = ?2, MODIFIER_ID = ?3, MODIFIED_DATETIME = ?4, "
			+ "CHECKER_ID = (CASE WHEN ?2 = 3 THEN ?3 WHEN ?2 != 3 THEN CHECKER_ID END), "
			+ "SUBMITTED_DATETIME = (CASE WHEN ?2 = 3 THEN ?4 WHEN ?2 != 3 THEN SUBMITTED_DATETIME END) "
			+ "WHERE 1=1 AND RAW_MATL_ID = ?1 AND (0 = ?5 OR OPT_LOCK = ?5) ", nativeQuery = true)
	void updRawMatlStatusWithOptLock(int rawMatlId, int recordStatus, String modifierId, Date date, Integer optLock);
	
	@Query(value = "select count(*) from RAW_MATERIAL where 1=1 and FLAVOR_STATUS_ID=?1", nativeQuery = true)
	int countRawMatlByFlavorStatus(int flavStatusId);
	
	@Modifying
	@Transactional
	@Query(value = "UPDATE RAW_MATERIAL SET FLAVOR_STATUS_ID=?2, MODIFIER_ID=?3, MODIFIED_DATETIME=?4 WHERE RAW_MATL_ID=?1", nativeQuery = true)
	void updRawMatl(Integer rmId, Integer fsId, String modifierId, Date date);
}
