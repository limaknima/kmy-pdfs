package com.fms.pfc.repository.main.api;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.fms.pfc.domain.model.main.RelPath2;

@Repository
public interface RelPathRepository2 extends JpaRepository<RelPath2, Integer> {

	@Query(value = "select * from REL_PATH2 rp "
			+ "where 1=1 "
			+ "and (0 = ?1 or rp.CATG_ID = ?1) "
			+ "and ('' = ?2 or rp.HMODEL = ?2) "
			+ "and ('' = ?3 or rp.YEAR = ?3) "
			+ "and ('' = ?4 or rp.MTH = ?4) "
			+ "and ('' = ?5 or rp.DAY = ?5) "
			+ "and ('' = ?6 or rp.PROD_LN = ?6) "
			+ "and ('' = ?7 or rp.SEQ = ?7) "
			+ "and (0 = ?8 or rp.PROC_TYPE = ?8) "
			+ "and ('' = ?9 or rp.SUB_PROC = ?9) "
			, nativeQuery = true)
	List<RelPath2> searchByCriteria(Integer catgId, String hmodel, String year, String mth, String day, String prodLn,
			String seq, Integer procType, String subProc);
}
