package com.fms.pfc.repository.main.api;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.fms.pfc.domain.model.main.TsNoDto;

@Repository
public interface TsNoRepository extends JpaRepository<TsNoDto, String> {
	@Query(value = "select concat(m.RAW_MATL_ID,201,m.TS_NO) as idx, 201 as rec_Type, m.TS_NO as ts_No from RM_MANF m "
			+ "where 1=1 "
			+ "and m.RAW_MATL_ID = ?1 "
			+ "union all "
			+ "select concat(pr.PR_ID,301,pr.PR_CODE) as idx, 301 as rec_Type, pr.PR_CODE as ts_No from PRODUCT_RECIPE pr "
			+ "where 1=1 "
			+ "and pr.PR_ID = ?2", nativeQuery = true)
	List<TsNoDto> findAllTsNos (int rmId, int prId);
}
