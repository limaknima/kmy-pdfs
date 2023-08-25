package com.fms.pfc.repository.main.api;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.fms.pfc.domain.model.main.FoldCatgConfSearch;

public interface FoldCatgConfSearchRepository extends JpaRepository<FoldCatgConfSearch, Integer> {
	
	@Query(value = "select * from ( "
			+ " select "
			+ " fcc.CATG_ID, "
			//+ " (select hpl.NAME from HPL hpl where hpl.HPL_ID = fcc.HPL_ID) as HPL_NAME, "
			//+ " (select hpl.NAME from HPL_MODEL hpl where hpl.HMODEL_ID = fcc.HMODEL_ID) as HPL_MODEL_NAME, "
			+ " fcc.HPL as HPL_NAME, "
			+ " fcc.HMODEL as HPL_MODEL_NAME, "
			+ " fcc.YEAR, fcc.MTH, fcc.DAY, fcc.PROD_LN, fcc.SEQ, "
			+ " fcc.CREATOR_ID, fcc.CREATED_DATETIME, fcc.MODIFIER_ID, fcc.MODIFIED_DATETIME, "
			+ " fcc.HPL_ID, fcc.HMODEL_ID, fcc.PROD_FILE_FORMAT "
			+ " from FOLD_CATG_CONF fcc "
			+ " where 1=1 "
			+ " and (?1 = '' or fcc.HPL = ?1) "
			+ " and (?2 = '' or fcc.HMODEL = ?2) "
			+ " and (?3 = '' or fcc.YEAR = ?3) "
			+ " and (?4 = '' or fcc.MTH = ?4) "
			+ " and (?5 = '' or fcc.DAY = ?5) "
			+ "	and (?6 = '' OR ( "
			+ "		fcc.PROD_LN like case when ?7 = '1' then concat('%', ?6, '%') "
			+ "		when ?7 = '2' then concat('%', ?6) "
			+ "		when ?7 = '3' then ?6 "
			+ "		when ?7 = '4' then concat(?6, '%') end "
			+ "	 ) "
			+ " ) "
			+ "	and (?8 = '' OR ( "
			+ "		fcc.SEQ like case when ?9 = '1' then concat('%', ?8, '%') "
			+ "		when ?9 = '2' then concat('%', ?8) "
			+ "		when ?9 = '3' then ?8 "
			+ "		when ?9 = '4' then concat(?8, '%') end "
			+ "	 ) "
			+ " ) "
			+ ") tbl "
			, nativeQuery = true)
	public List<FoldCatgConfSearch> searchByCriteria(String searchHplId, String searchHplModelId, String searchYear,
			String searchMth, String searchDay, String prodLn, String prodLnExp, String seqNo, String seqNoExp);

}
