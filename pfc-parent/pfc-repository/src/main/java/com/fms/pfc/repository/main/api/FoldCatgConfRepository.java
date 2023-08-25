package com.fms.pfc.repository.main.api;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.fms.pfc.domain.model.main.FoldCatgConf;

@Repository
public interface FoldCatgConfRepository extends JpaRepository<FoldCatgConf, Integer> {

	@Query(value = "select * from ( "
			+ " select "
			+ " * "
			+ " from FOLD_CATG_CONF fcc "
			+ " where 1=1 "
			+ " and (?1 = '' or fcc.HPL = ?1) "
			+ " and (?2 = '' or fcc.HMODEL = ?2) "
			+ " and (?3 = '' or fcc.YEAR = ?3) "
			+ " and (?4 = '' or fcc.MTH = ?4) "
			+ " and (?5 = '' or fcc.DAY = ?5) "
			+ "	and (?6 = '' OR fcc.PROD_LN = ?6) "
			+ "	and (?7 = '' OR fcc.SEQ = ?7) "
			+ "	and (?8 = '' OR fcc.PROD_FILE_FORMAT = ?8) "
			+ ") tbl "
			, nativeQuery = true)
	List<FoldCatgConf> searchByCriteria(String hpl, String model, String year, String month, String day, String prodLn,
			String seq, String format);

}
