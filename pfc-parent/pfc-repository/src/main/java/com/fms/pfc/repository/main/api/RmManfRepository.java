package com.fms.pfc.repository.main.api;

import java.util.Date;
import java.util.List;

import javax.transaction.Transactional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.fms.pfc.domain.model.main.RmManf;

@Repository
public interface RmManfRepository extends JpaRepository<RmManf, String> {

	@Query(value = "SELECT A.*, V.VENDOR_NAME FROM RM_MANF A "
			+ "INNER JOIN VENDOR V "
			+ "ON V.VENDOR_ID = A.MANF_ID "
			+ "WHERE A.RAW_MATL_ID = ?1 "
			+ "AND (?2 = '' OR (A.TS_NO LIKE CASE WHEN ?4 = '1' THEN CONCAT('%', ?2, '%') "
			+ "						 			  WHEN ?4 = '2' THEN CONCAT('%', ?2) "
			+ "        				  			  WHEN ?4 = '3' THEN ?2 "
			+ "        				   			  WHEN ?4 = '4' THEN CONCAT(?2, '%') END)) "
			+ "AND (?3 = '' OR (V.VENDOR_NAME LIKE CASE WHEN ?5 = '1' THEN CONCAT('%', ?3, '%') "
			+ "							 	      WHEN ?5 = '2' THEN CONCAT('%', ?3) "
			+ "			      				  	  WHEN ?5 = '3' THEN ?3 "
			+ "			    				   	  WHEN ?5 = '4' THEN CONCAT(?3, '%') END)) ", nativeQuery = true)
	List<RmManf> searchRmManufacturer(int rawMaterialId, String tsNo, String vendor, String expr1, String expr2);

	@Query(value = "SELECT A.*, B.RAW_MATL_NAME, V.VENDOR_NAME, V.VENDOR_TYPE FROM RM_MANF A "
			+ "	INNER JOIN VENDOR V "
			+ " ON V.VENDOR_ID = A.MANF_ID "
			+ " INNER JOIN RAW_MATERIAL B "
			+ " ON A.RAW_MATL_ID = B.RAW_MATL_ID ", nativeQuery = true)
	List<RmManf> searchRenewaldate();

	@Modifying
	@Transactional
	@Query(value = "DELETE FROM RM_MANF WHERE RAW_MATL_ID = ?1", nativeQuery = true)
	void delRmManf(int rawMatlId);
	
	@Modifying
	@Transactional
	@Query(value = "DELETE FROM RM_MANF WHERE RAW_MATL_ID = ?1 AND RM_MANF_ID = ?2", nativeQuery = true)
	void delDetailRmManf(int rawMatlId, int rmManfId);

	@Modifying
	@Transactional
	@Query(value = "INSERT INTO RM_MANF (RAW_MATL_ID, TS_NO, MANF_ID, VIPD_DATE, VIPD_ANNEX2_DATE, "
			+ "CREATOR_ID, CREATED_DATETIME, ORIGIN_COUNTRY_ID, VIPD_OBJECT, VIPD_ANNEX2_OBJECT, "
			+ "VIPD_FILENAME, VIPD_ANNEX2_FILENAME) "
			+ "VALUES "
			+ "((SELECT RAW_MATL_ID FROM RAW_MATERIAL WHERE RAW_MATL_NAME = ?1), ?2, ?3, ?4, "
			+ "?5, ?6, ?12, ?7, ?8, ?9, ?10, ?11)", nativeQuery = true)
	void addRmManf(String matlName, String tsNo, int manfId, Date vipdDate, Date vipdAnnex2Date, String creatorId, 
			String countryId, byte[] vipdObject, byte[] vipdAnnex2Object, String vipdFileName, String vipdAnnex2FileName,
			Date date);
	
	@Modifying
	@Transactional
	@Query(value = "UPDATE RM_MANF SET TS_NO = ?2, MANF_ID = ?3, VIPD_DATE = ?4, VIPD_OBJECT = ?9, VIPD_ANNEX2_DATE = ?5, "
			+ "VIPD_ANNEX2_OBJECT = ?10, MODIFIER_ID = ?6, MODIFIED_DATETIME = ?13, ORIGIN_COUNTRY_ID = ?7, "
			+ "VIPD_FILENAME = ?11, VIPD_ANNEX2_FILENAME = ?12 "
			+ "WHERE RAW_MATL_ID = ?1 AND RM_MANF_ID = ?8", nativeQuery = true)
	void updRmManf(int rawMatlId, String tsNo, int manfId, Date vipdDate, Date vipdAnnex2Date, String modifierId, 
			String countryId, int rmManfId, byte[] vipdObject, byte[] vipdAnnex2Object, String vipdFileName, 
			String vipdAnnex2FileName, Date date);
	
}
