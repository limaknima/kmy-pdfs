package com.fms.pfc.repository.main.api;

import java.util.Date;
import java.util.List;

import javax.transaction.Transactional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.fms.pfc.domain.model.main.PrStat;

@Repository
public interface ProductStatusRepository extends JpaRepository<PrStat, String> {

	@Query(value = " SELECT A.*, B.COUNTRY_NAME AS CNTRY_NAME FROM PR_STAT A "
			+ "INNER JOIN REF_COUNTRY B "
			+ "ON A.COUNTRY_ID = B.COUNTRY_ID "
			+ "WHERE (?1 = '' OR (A.PR_ID LIKE CASE WHEN ?2 = '1' THEN CONCAT('%', ?1, '%') "
			+ "									    WHEN ?2 = '2' THEN CONCAT('%', ?1) "
			+ "			     				  	    WHEN ?2 = '3' THEN ?1 "
			+ "			     				   	    WHEN ?2 = '4' THEN CONCAT(?1, '%') END))"
			+ "AND (?3 = '' OR A.COUNTRY_ID = ?3) "
			+ "AND (?4 = 0 OR A.FINAL_STATUS = ?4) ", nativeQuery = true)
	List<PrStat> searchProductStatus(int prId, String para, String countryId, int status);

	@Modifying
	@Transactional
	@Query(value = "INSERT INTO PR_STAT "
			+ "(PR_ID, FINAL_STATUS, COUNTRY_ID, REMARKS, REMARKS_USER_ID, CREATOR_ID, CREATED_DATETIME) "
			+ "VALUES "
			+ "((SELECT PR_ID FROM PRODUCT_RECIPE WHERE PR_CODE = ?1), ?2, ?3, ?4, ?5, ?6, ?7)", nativeQuery = true)
	void addPrStat(String prCode, int finalStatus, String countryId, String remarks, String remarksUserId,
			String creatorId, Date date);

	@Modifying
	@Transactional
	@Query(value = "UPDATE PR_STAT SET FINAL_STATUS = ?2, COUNTRY_ID = ?3, REMARKS = ?4, REMARKS_USER_ID = ?5, "
			+ "MODIFIER_ID = ?6, MODIFIED_DATETIME = ?7 "
			+ "WHERE PR_STAT_ID = ?1", nativeQuery = true)
	void updPrStat(int prStatId, int finalStatus, String countryId, String remarks, String remarksUserId,
			String modifierId, Date date);

	@Modifying
	@Transactional
	@Query(value = "UPDATE PR_STAT SET FINAL_STATUS = ?3, MODIFIER_ID = ?4, MODIFIED_DATETIME = ?5 "
			+ "WHERE PR_ID = ?1 "
			+ "AND COUNTRY_ID = ?2 ", nativeQuery = true)
	void updPrStatFlag(int prId, String countryId, int finalStatus, String modifierId, Date date);

	@Modifying
	@Transactional
	@Query(value = "DELETE FROM PR_STAT WHERE PR_ID = ?1", nativeQuery = true)
	void delPrStat(int prId);

	@Modifying
	@Transactional
	@Query(value = "DELETE FROM PR_STAT WHERE PR_STAT_ID = ?1", nativeQuery = true)
	void delDetailPrStat(int prStatId);
}
