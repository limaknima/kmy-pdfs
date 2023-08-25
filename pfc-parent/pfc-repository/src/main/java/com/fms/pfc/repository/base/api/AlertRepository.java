package com.fms.pfc.repository.base.api;

import java.util.Date;
import java.util.List;

import javax.transaction.Transactional;

import com.fms.pfc.domain.model.Alert;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;

@Repository
public interface AlertRepository extends JpaRepository<Alert, String> {

	/*@Query(value = "SELECT A.*, B.READ_RECIPIENT FROM ALERT A " 
			+ "INNER JOIN ALERT_RECIPIENT B " 
			+ "ON A.ALERT_ID = B.ALERT_ID "
			+ "AND B.TO_USER_ID = ?7 "
			+ "WHERE (?1 = '' OR CONVERT(DATE, A.CREATED_DATETIME) >= CONVERT(DATETIME, ?1, 105)) "
			+ "AND (?2 = '' OR CONVERT(DATE, A.CREATED_DATETIME) <= CONVERT(DATETIME, ?2, 105)) "
			+ "AND (?3 = '' OR (A.RECORD_REF LIKE CASE WHEN ?5 = '1' THEN CONCAT('%', ?3, '%') "
			+ "						 			    WHEN ?5 = '2' THEN CONCAT('%', ?3) "
			+ "        				  			    WHEN ?5 = '3' THEN ?3 "
			+ "        				   			    WHEN ?5 = '4' THEN CONCAT(?3, '%') END)) "
			+ "AND (?4 = '' OR (A.SUBJECT LIKE CASE WHEN ?6 = '1' THEN CONCAT('%', ?4, '%') "
			+ "					     			  WHEN ?6 = '2' THEN CONCAT('%', ?4) "
			+ "        				 			  WHEN ?6 = '3' THEN ?4 "
			+ "        				 		  	  WHEN ?6 = '4' THEN CONCAT(?4, '%') END))", nativeQuery = true)
	List<Alert> searchNotification(String dateFr, String dateTo, String recRef, String subject, String exp1,
			String exp2, String userId);*/
	
	@Query(value = "select tbl.*, B.READ_RECIPIENT from (SELECT "
			+ "A.ALERT_ID, A.SUBJECT, A.ALERT_DESC, A.FROM_USER_ID, A.CREATED_DATETIME, "
			+ "CASE WHEN (A.RECORD_TYPE_ID = 201) THEN (SELECT RAW_MATL_NAME FROM RAW_MATERIAL WHERE RAW_MATL_ID = A.RECORD_REF) "
			+ "WHEN (A.RECORD_TYPE_ID = 301) THEN (SELECT PR_NAME FROM PRODUCT_RECIPE WHERE PR_ID = A.RECORD_REF) "
			+ "WHEN (A.RECORD_TYPE_ID = 202) THEN (SELECT RAW_MATL_NAME FROM RAW_MATERIAL WHERE RAW_MATL_ID = A.RECORD_REF) "
			+ "ELSE A.RECORD_REF END AS RECORD_REF_DESC, A.RECORD_REF, "
			+ "A.RECORD_TYPE_ID "
			+ "FROM ALERT A) tbl " 
			+ "INNER JOIN ALERT_RECIPIENT B ON tbl.ALERT_ID = B.ALERT_ID AND B.TO_USER_ID = ?7 "
			+ "WHERE (?1 = '' OR CONVERT(DATE, tbl.CREATED_DATETIME) >= CONVERT(DATETIME, ?1, 105)) "
			+ "AND (?2 = '' OR CONVERT(DATE, tbl.CREATED_DATETIME) <= CONVERT(DATETIME, ?2, 105)) "
			+ "AND (?3 = '' OR (tbl.RECORD_REF_DESC LIKE CASE WHEN ?5 = '1' THEN CONCAT('%', ?3, '%') "
			+ "WHEN ?5 = '2' THEN CONCAT('%', ?3) "
			+ "WHEN ?5 = '3' THEN ?3 "
			+ "WHEN ?5 = '4' THEN CONCAT(?3, '%') END)) "
			+ "AND (?4 = '' OR (tbl.SUBJECT LIKE CASE WHEN ?6 = '1' THEN CONCAT('%', ?4, '%') "
			+ "WHEN ?6 = '2' THEN CONCAT('%', ?4) "
			+ "WHEN ?6 = '3' THEN ?4 "
			+ "WHEN ?6 = '4' THEN CONCAT(?4, '%') END))", nativeQuery = true)
	List<Alert> searchNotification(String dateFr, String dateTo, String recRef, String subject, String exp1,
			String exp2, String userId);

	@Query(value = "SELECT "
			+ "A.ALERT_ID, A.SUBJECT, A.ALERT_DESC, A.FROM_USER_ID, A.CREATED_DATETIME, "
			+ "CASE WHEN A.RECORD_TYPE_ID = 201 THEN (SELECT RAW_MATL_NAME FROM RAW_MATERIAL WHERE RAW_MATL_ID = A.RECORD_REF) "
			+ "WHEN A.RECORD_TYPE_ID = 301 THEN (SELECT PR_NAME FROM PRODUCT_RECIPE WHERE PR_ID = A.RECORD_REF) "
			+ "ELSE A.RECORD_REF END AS RECORD_REF_DESC, A.RECORD_REF, "
			+ "A.RECORD_TYPE_ID, B.READ_RECIPIENT "
			+ "FROM ALERT A " 
			+ "INNER JOIN ALERT_RECIPIENT B ON A.ALERT_ID = B.ALERT_ID AND B.TO_USER_ID = ?2 "
			+ "WHERE A.ALERT_ID = ?1", nativeQuery = true)
	Alert searchNotificationDetail(int alertId, String userId);
	
	@Query(value = "SELECT COUNT(*) FROM ALERT_RECIPIENT WHERE TO_USER_ID = ?1 AND READ_RECIPIENT = 1", nativeQuery = true)
	int getCount(String userId);

	@Modifying
	@Transactional
	@Query(value = "DELETE FROM ALERT_RECIPIENT WHERE ALERT_ID = ?1 AND TO_USER_ID = ?2", nativeQuery = true)
	void deleteNotification(int alertId, String userId);
	
	@Modifying
	@Transactional
	@Query(value = "UPDATE ALERT_RECIPIENT SET READ_RECIPIENT = 0 WHERE ALERT_ID = ?1 AND TO_USER_ID = ?2", nativeQuery = true)
	void updateReadRecipient(int alertId, String userId);

	@Modifying
	@Transactional
	@Query(value = "INSERT INTO ALERT (SUBJECT, ALERT_DESC, FROM_USER_ID, RECORD_REF, RECORD_TYPE_ID, CREATED_DATETIME) "
			+ "VALUES (?1, ?2, ?3, ?4, ?5, ?6)", nativeQuery = true)
	void addAlert(String subject, String alertDesc, String fromUserId, String recordRef, int recordTypeId, Date dateTime);

	@Modifying
	@Transactional
	@Query(value = "INSERT INTO ALERT_RECIPIENT VALUES "
			+ "(?1, ?2, 1)", nativeQuery = true)
	void addAlertRecipient(int alertId, String userId);
	
	@Query(value = "SELECT COUNT(*) FROM ALERT_RECIPIENT WHERE TO_USER_ID = ?1 AND ALERT_ID = ?2", nativeQuery = true)
	int isAlertRecipExist(String userId, int alertId);
	
}
