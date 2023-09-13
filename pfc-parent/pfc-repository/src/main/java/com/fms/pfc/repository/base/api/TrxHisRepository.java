package com.fms.pfc.repository.base.api;

import java.util.Date;
import java.util.List;

import javax.transaction.Transactional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.fms.pfc.domain.model.TranxHistory;

@Repository
public interface TrxHisRepository extends JpaRepository<TranxHistory, String> {

	@Query(value = "SELECT "
			+ "A.LOG_ID, A.LOG_DATETIME, A.LOG_DESC, A.USER_ID, A.FUNCTION_TYPE, A.RECORD_TYPE_ID, A.SEARCH_STRING, B.ORG_ID, "
			+ "CASE "
			+ "WHEN (A.RECORD_TYPE_ID in (3,5) AND A.FUNCTION_TYPE NOT IN (4,7)) THEN (SELECT USER_NAME FROM USR WHERE USER_ID = A.RECORD_REF) "
			+ "ELSE A.RECORD_REF END AS RECORD_REF "
			+ "FROM TRX_HISTORY_LOG A " 
			+ "INNER JOIN USR B ON A.USER_ID = B.USER_ID "
			+ "WHERE (?1 = '' OR A.USER_ID = ?1) " 
			+ "AND (?2 = '' OR B.ORG_ID = ?2) "
			+ "AND (?3 = '' OR CONVERT(DATE, A.LOG_DATETIME) >= CONVERT(DATETIME, ?3, 105)) "
			+ "AND (?4 = '' OR CONVERT(DATE, A.LOG_DATETIME) <= CONVERT(DATETIME, ?4, 105)) "
			+ "AND FUNCTION_TYPE IN (?5, ?6, ?7, ?8, ?9, ?10) "
			//+ "AND (?8 = '' OR RECORD_REF = ?8) "
			+ "ORDER BY A.LOG_DATETIME DESC", nativeQuery = true)
	List<TranxHistory> searchTranxHistory(String userId, String orgId, String dateFr, String dateTo, int insert,
			int update, int delete, int view, int print, int search);

	@Query(value = "SELECT A.*, B.ORG_ID FROM TRX_HISTORY_LOG A "
			+ "INNER JOIN USR B " 
			+ "ON A.USER_ID = B.USER_ID "
			+ "WHERE (?1 = '' OR (A.USER_ID LIKE CASE WHEN ?5 = '1' THEN CONCAT('%', ?1, '%') "
			+ "						 			      WHEN ?5 = '2' THEN CONCAT('%', ?1) "
			+ "        				  			      WHEN ?5 = '3' THEN ?1 "
			+ "        				   			      WHEN ?5 = '4' THEN CONCAT(?1, '%') END)) "
			+ "AND (?2 = '' OR CONVERT(DATE, A.LOG_DATETIME) >= CONVERT(DATETIME, ?2, 105)) "
			+ "AND (?3 = '' OR CONVERT(DATE, A.LOG_DATETIME) <= CONVERT(DATETIME, ?3, 105)) "
			+ "AND (?4 = '' OR A.RECORD_REF = ?4) "
			+ "AND (?6 = 0 OR A.RECORD_TYPE_ID = ?6) "
			+ "AND FUNCTION_TYPE IN (1, 2, 3) "
			+ "ORDER BY A.LOG_DATETIME DESC", nativeQuery = true)
	List<TranxHistory> searchTxHis(String userId, String dateFr, String dateTo, String recordRef, String exp, int recordType);

	@Modifying
	@Transactional
	@Query(value = "INSERT INTO TRX_HISTORY_LOG (LOG_DATETIME, LOG_DESC, USER_ID, FUNCTION_TYPE, RECORD_REF,"
			+ "RECORD_TYPE_ID, SEARCH_STRING) "
			+ "VALUES (?1, ?2, ?3, ?4, ?5, ?6, ?7)", nativeQuery = true)
	void addTrxHistory(Date logDateTime, String logDesc, String userId, int funcType, String recordRef, int recordType,
			String searcString);
}
