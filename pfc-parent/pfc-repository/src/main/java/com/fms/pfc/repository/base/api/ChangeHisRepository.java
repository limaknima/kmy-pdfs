package com.fms.pfc.repository.base.api;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.fms.pfc.domain.model.ChangeHistory;

@Repository
public interface ChangeHisRepository extends JpaRepository<ChangeHistory, Integer> {

	@Query(value = "SELECT * FROM CHANGE_HISTORY_LOG WHERE CONVERT(VARCHAR, LOG_DATETIME, 120) = ?1 ", nativeQuery = true)
	List<ChangeHistory> searchChgHistoryDetail(String chgHisDate);
	
	@Query(value = "SELECT * FROM CHANGE_HISTORY_LOG "
			+ "WHERE 1=1 "
			+ "AND CONVERT(VARCHAR, LOG_DATETIME, 120) = ?1 "
			+ "AND (?2 = 0 OR RECORD_TYPE_ID = ?2) "
			+ "AND (?3 = '' OR RECORD_REF = ?3)", nativeQuery = true)
	List<ChangeHistory> searchChgHistoryDetail(String chgHisDate, int recTypeId, String recRef);
}
