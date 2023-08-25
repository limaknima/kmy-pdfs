package com.fms.pfc.repository.main.api;

import java.util.Date;
import java.util.List;

import javax.transaction.Transactional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.fms.pfc.domain.model.main.PrRmStat;

@Repository
public interface PrRmStatRepository extends JpaRepository<PrRmStat, String> {    				   
	
	@Query(value = " SELECT * FROM PR_RM_STAT "
			+ " WHERE PR_ID = ?1 "
			+ " AND (?2 = '' OR COUNTRY_ID = ?2) "
			+ " ORDER BY COUNTRY_ID ", nativeQuery = true)
	List<PrRmStat> searchPrRmStat(int prId, String countryId);

	@Query(value = " SELECT C.COUNTRY_NAME, B.RAW_MATL_NAME, A.* FROM PR_RM_STAT A INNER JOIN RAW_MATERIAL B "
			+ "	ON A.RAW_MATL_ID = B.RAW_MATL_ID "
			+ "	INNER JOIN REF_COUNTRY C "
			+ "	ON A.COUNTRY_ID  = C.COUNTRY_ID "
			+ "	WHERE A.PR_ID = ?1 "
			+ "	ORDER BY A.COUNTRY_ID ", nativeQuery = true)
	List<PrRmStat> searchPrRmDetails(int prId);
	
	@Modifying
	@Transactional
	@Query(value = "INSERT INTO PR_RM_STAT (PR_ID, RAW_MATL_ID, TOTAL_PERC, SYS_FINAL_STATUS, FINAL_STATUS, COUNTRY_ID, "
			+ "REMARKS, CREATOR_ID, CREATED_DATETIME) "
			+ "VALUES ((SELECT PR_ID FROM PRODUCT_RECIPE WHERE PR_CODE = ?1), ?2, ?3, ?4, ?5, ?6, ?7, "
			+ "?8, ?9)", nativeQuery = true)
	void addPrRmStat(String prCode, int rawMatlId, double totalPerc, int sysFinalStatus, int finalStatus, String countryId,
			String remarks, String creatorId, Date date);

	@Modifying
	@Transactional
	@Query(value = "UPDATE PR_RM_STAT SET FINAL_STATUS = ?1, REMARKS = ?2, MODIFIER_ID = ?6, MODIFIED_DATETIME = ?7 "
			+ "WHERE PR_ID = (SELECT PR_ID FROM PRODUCT_RECIPE WHERE PR_CODE = ?3) "
			+ "AND RAW_MATL_ID = ?4 AND COUNTRY_ID = ?5", nativeQuery = true)
	void updPrRmStat(int finalStatus, String remarks, String prCode, int rawMatlId, String countryId, String modifierId,
			Date date);
	
	@Modifying
	@Transactional
	@Query(value = "UPDATE PR_RM_STAT SET FINAL_STATUS = ?1, REMARKS = ?2, MODIFIER_ID = ?6, MODIFIED_DATETIME = ?7, RAW_MATL_ID = ?8 "
			+ "WHERE PR_ID = (SELECT PR_ID FROM PRODUCT_RECIPE WHERE PR_CODE = ?3) "
			+ "AND RAW_MATL_ID = ?4 AND COUNTRY_ID = ?5", nativeQuery = true)
	void updPrRmStat(int finalStatus, String remarks, String prCode, int rawMatlId, String countryId, String modifierId,
			Date date, int rmRefChg);

	@Modifying
	@Transactional
	@Query(value = "UPDATE PR_RM_STAT SET SYS_FINAL_STATUS = ?1, MODIFIER_ID = ?2, MODIFIED_DATETIME = ?3 "
			+ "WHERE PR_ID = ?4 AND RAW_MATL_ID = ?5 AND COUNTRY_ID = ?6", nativeQuery = true)
	void updPrRmStatByMatl(int sysFinalStatus, String modifierId, Date date, int prId, int rawMatlId, String countryId);

	@Modifying
	@Transactional
	@Query(value = "UPDATE PR_RM_STAT SET AFFECTED_FLAG = ?1, MODIFIER_ID = ?2, MODIFIED_DATETIME = ?3 "
			+ "WHERE 1=1 "
			+ "AND (?4 = 0 OR PR_ID = ?4) "
			+ "AND (?5 = 0 OR RAW_MATL_ID = ?5) "
			+ "AND (?6 = '' OR COUNTRY_ID = ?6) "
			+ "AND (?7 = 0 OR PR_RM_STAT_ID = ?7)", nativeQuery = true)
	void updPrRmStatFlag(String affectedFlag, String modifierId, Date date, int prId, int rawMatlId, String countryId, int prRmStatId);
	
	@Modifying
	@Transactional
	@Query(value = "UPDATE PR_RM_STAT SET FINAL_STATUS = SYS_FINAL_STATUS "
			+ "WHERE PR_ID = ?1 AND AFFECTED_FLAG = 'Y'", nativeQuery = true)
	void updPrRmStatByFlag(int prId);
	
	@Modifying
	@Transactional
	@Query(value = "DELETE FROM PR_RM_STAT WHERE PR_ID = ?1", nativeQuery = true)
	void delPrRmStat(int prId);

	@Modifying
	@Transactional
	@Query(value = "DELETE FROM PR_RM_STAT WHERE PR_ID = ?1 AND RAW_MATL_ID = ?2", nativeQuery = true)
	void delPrRmStatByMatl(int prId, int rawMatlId);

	@Modifying
	@Transactional
	@Query(value = "DELETE FROM PR_RM_STAT WHERE PR_ID = ?1 AND COUNTRY_ID = ?2", nativeQuery = true)
	void delPrRmStatByCountry(int prId, String countryId);
	
	@Query(value = "select distinct "
			+"prs.PR_RM_STAT_ID, pr.PR_ID, prs.RAW_MATL_ID"
			+ ", prs.COUNTRY_ID, pr.PR_CODE, pr.PR_NAME"
			+ ", rm.RAW_MATL_NAME, rc.COUNTRY_NAME, pr.RECORD_STATUS"
			+ ", prs.AFFECTED_FLAG, pr.ONHOLD_FLAG "
			+ "from PRODUCT_RECIPE pr "
			+ "inner join PR_RM_STAT prs on prs.PR_ID = pr.PR_ID "
			+ "left join RAW_MATERIAL rm on rm.RAW_MATL_ID = prs.RAW_MATL_ID "
			+ "left join REF_COUNTRY rc on rc.COUNTRY_ID = prs.COUNTRY_ID "
			+ "where 1=1 "
			+ "and (?1 = 0 or pr.RECORD_STATUS = ?1) "
			+ "and prs.RAW_MATL_ID = ?2 "
			+ "and prs.COUNTRY_ID in (?3) "
			+ "and (?4 = '' or prs.AFFECTED_FLAG = ?4) "
			+ "and (?5 = '' or pr.ONHOLD_FLAG = ?5) "
			+ "order by pr.PR_CODE, rc.COUNTRY_NAME ", nativeQuery = true)
	List<Object[]> searchProductLinkage(int recordStatus, int rawMatlId, List<String> countryId, String affFlag, String onHoldFlag);
	
	@Query(value = "select distinct "
			+"prs.PR_RM_STAT_ID, pr.PR_ID, prs.RAW_MATL_ID"
			+ ", prs.COUNTRY_ID, pr.PR_CODE, pr.PR_NAME"
			+ ", rm.RAW_MATL_NAME, rc.COUNTRY_NAME, pr.RECORD_STATUS"
			+ ", prs.AFFECTED_FLAG, pr.ONHOLD_FLAG "
			+ "from PRODUCT_RECIPE pr "
			+ "inner join PR_RM_STAT prs on prs.PR_ID = pr.PR_ID "
			+ "left join RAW_MATERIAL rm on rm.RAW_MATL_ID = prs.RAW_MATL_ID "
			+ "left join REF_COUNTRY rc on rc.COUNTRY_ID = prs.COUNTRY_ID "
			+ "where 1=1 "
			+ "and (?1 = 0 or pr.RECORD_STATUS = ?1) "
			+ "and prs.RAW_MATL_ID = ?2 "
			+ "and prs.COUNTRY_ID in (?3) "
			+ "and (?4 = '' or prs.AFFECTED_FLAG = ?4) "
			+ "and (?5 = '' or pr.ONHOLD_FLAG = ?5) "
			+ "union "
			+ "select distinct "
			+ "0, pr.PR_ID, prs.REF_ID"
			+ ", '', pr.PR_CODE, pr.PR_NAME"
			+ ", rm.RAW_MATL_NAME, '', pr.RECORD_STATUS"
			+ ", '', pr.ONHOLD_FLAG "
			+ "from PRODUCT_RECIPE pr "
			+ "inner join PR_INGREDIENT prs on prs.PR_ID = pr.PR_ID "
			+ "left join RAW_MATERIAL rm on rm.RAW_MATL_ID = prs.REF_ID and prs.REF_TYPE = 201 "
			+ "where 1=1 "
			+ "and prs.REF_ID = ?2 "
			+ "order by pr.PR_CODE, rc.COUNTRY_NAME ", nativeQuery = true)
	List<Object[]> searchProductLinkageAll(int recordStatus, int rawMatlId, List<String> countryId, String affFlag, String onHoldFlag);
	
	@Query(value = " SELECT * FROM PR_RM_STAT "
			+ "WHERE 1=1 "
			+ "AND RAW_MATL_ID = ?1 "
			+ "AND (?2 = '' OR AFFECTED_FLAG = ?2) "
			+ "AND (?3 = '' OR COUNTRY_ID = ?3)", nativeQuery = true)
	List<PrRmStat> searchPrRmStat(int rawMatlId, String affectedFlag, String countryId);
	
	@Query(value = " SELECT * FROM PR_RM_STAT "
			+ "WHERE 1=1 "
			+ "AND (?1 = 0 OR PR_ID = ?1) "
			+ "AND (?2 = 0 OR RAW_MATL_ID = ?2) "
			+ "AND (?3 = '' OR COUNTRY_ID = ?3)", nativeQuery = true)
	PrRmStat findUnique(int prId, int rmId, String countryId);
}

