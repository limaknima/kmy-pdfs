package com.fms.pfc.repository.base.api;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.fms.pfc.domain.model.TrxHistoryLog;

@Repository
public interface TrxHistoryLogRepository extends JpaRepository<TrxHistoryLog, Integer> {
	
	@Query(value = "select a.* from trx_history_log a "
			+ "where 1=1 "
			+ "and (?1 = '' or (a.user_id like case when ?5 = '1' then concat('%', ?1, '%') "
			+ " when ?5 = '2' then concat('%', ?1) "
			+ " when ?5 = '3' then ?1 "
			+ " when ?5 = '4' then concat(?1, '%') end)) "
			+ "and (?2 = '' or convert(date, a.log_datetime) >= convert(datetime, ?2, 105)) "
			+ "and (?3 = '' or convert(date, a.log_datetime) <= convert(datetime, ?3, 105)) "
			+ "and (?4 = '' or a.record_ref = ?4) "
			+ "and (?6 = 0 or a.record_type_id = ?6) "
			+ "and function_type in (?7) "
			+ "order by a.log_datetime desc", nativeQuery = true)
	List<TrxHistoryLog> findByCriteria(String userId, String dateFr, String dateTo, String recordRef, String exp,
			int recordType, List<Integer> funcTypes);
	
	@Query(value = "select a.* from trx_history_log a "
			+ "where 1=1 "
			+ "and (?1 = '' or (a.user_id like case when ?4 = '1' then concat('%', ?1, '%') "
			+ " when ?4 = '2' then concat('%', ?1) "
			+ " when ?4 = '3' then ?1 "
			+ " when ?4 = '4' then concat(?1, '%') end)) "
			+ "and (?2 = '' or convert(varchar, a.log_datetime, 120) = ?2) "
			+ "and (?3 = '' or a.record_ref = ?3) "
			+ "and (?5 = 0 or a.record_type_id = ?5) "
			+ "and function_type in (?6) "
			+ "order by a.log_datetime desc", nativeQuery = true)
	List<TrxHistoryLog> findByCriteria(String userId, String logDate, String recordRef, String exp,
			int recordType, List<Integer> funcTypes);
	
	@Query(value = "select distinct year(log_datetime) as _year from trx_history_log "
			+ "order by _year", nativeQuery = true)
	List<Integer> getYears();

}
