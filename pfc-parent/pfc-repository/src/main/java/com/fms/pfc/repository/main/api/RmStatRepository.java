package com.fms.pfc.repository.main.api;

import java.util.Date;
import java.util.List;

import javax.transaction.Transactional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.fms.pfc.domain.model.main.RmStat;

@Repository
public interface RmStatRepository extends JpaRepository<RmStat, String> {

	@Query(value = "SELECT * FROM RM_STAT "
			+ "WHERE RAW_MATL_ID = ?1 "
			+ "ORDER BY COUNTRY_ID", nativeQuery = true)
	List<RmStat> searchRmStat(int rawMatlId);
	
	@Query(value = "SELECT DISTINCT(FINAL_STATUS) FROM RM_STAT "
			+ "WHERE RAW_MATL_ID = ?1 "
			+ "AND COUNTRY_ID = ?2 "
			+ "AND (?3 = 0 OR MANF_ID = ?3) ", nativeQuery = true)
	Integer getRmFinalStatus(int rawMatlId, String countryId, int manfId);

	@Modifying
	@Transactional
	@Query(value = "INSERT INTO RM_STAT (RAW_MATL_ID, MANF_ID, VIPD_STATUS, FINAL_STATUS, COUNTRY_ID, CREATOR_ID, DATA_CHG_FLAG) "
			+ "VALUES "
			+ "((SELECT RAW_MATL_ID FROM RAW_MATERIAL WHERE RAW_MATL_NAME = ?1), ?2, ?3, ?4, ?5, ?6, 'N')", nativeQuery = true)
	void addRmStat(String matlName, int manfId, int vipdStatus, int finalStatus, String countryId, String creatorId);

	@Modifying
	@Transactional
	@Query(value = "UPDATE RM_STAT SET FINAL_STATUS = ?3, MODIFIER_ID = ?4, "
			+ "MODIFIED_DATETIME = ?5, DATA_CHG_FLAG = ?6 "
			+ "WHERE COUNTRY_ID = ?1 AND RAW_MATL_ID = ?2", nativeQuery = true)
	void updRmStat(String countryId, int rawMatlId, int finalStatus, String modifierId, Date date, String dataChgFlag);

	@Modifying
	@Transactional
	@Query(value = "UPDATE RM_STAT SET VIPD_STATUS = ?3, MODIFIER_ID = ?5, MODIFIED_DATETIME = ?6 "
			+ "WHERE RAW_MATL_ID = (SELECT RAW_MATL_ID FROM RAW_MATERIAL WHERE RAW_MATL_NAME = ?1) "
			+ "AND MANF_ID = ?2 AND COUNTRY_ID = ?4", nativeQuery = true)
	void updRmStatVipd(String rawMatlName, int manfId, int vipdStatus, String countryId, String modifierId, Date date);
	
	@Modifying
	@Transactional
	@Query(value = "UPDATE RM_STAT SET VIPD_STATUS = ?3, MODIFIER_ID = ?5, MODIFIED_DATETIME = ?6, MANF_ID = ?2, COUNTRY_ID = ?4 "
			+ "WHERE RM_STAT_ID = ?1 ", nativeQuery = true)
	void updRmStatByRmStatId(int rmStatId, int manfId, int vipdStatus, String countryId, String modifierId, Date date);
	
	@Modifying
	@Transactional
	@Query(value = "UPDATE RM_STAT SET DATA_CHG_FLAG = ?5 "
			+ "WHERE 1=1 "
			+ "AND (?1 = 0 or RM_STAT_ID = ?1) "
			+ "AND (?2 = '' or COUNTRY_ID = ?2) "
			+ "AND (?3 = 0 or RAW_MATL_ID = ?3) "
			+ "AND (?4 = '' or DATA_CHG_FLAG = ?4)", nativeQuery = true)
	void updRmStatDataChgFlag(int rmStatId, String countryId, int rawMatlId, String currentFlag, String newFlag);


	@Modifying
	@Transactional
	@Query(value = "DELETE FROM RM_STAT WHERE RAW_MATL_ID = ?1", nativeQuery = true)
	void delRmStat(int rawMatlId);
	
	@Modifying
	@Transactional
	@Query(value = "DELETE FROM RM_STAT WHERE RAW_MATL_ID = ?1 AND MANF_ID = ?2", nativeQuery = true)
	void delRmStatByManf(int rawMatlId, int manfId);
	
	@Modifying
	@Transactional
	@Query(value = "DELETE FROM RM_STAT WHERE RAW_MATL_ID = ?1 AND COUNTRY_ID = ?2", nativeQuery = true)
	void delRmStatByCountry(int rawMatlId, String countryId);
}
