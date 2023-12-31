package com.fms.pfc.repository.main.api;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.fms.pfc.domain.model.main.ProdFileSearch;

public interface ProdFileSearchRepository extends JpaRepository<ProdFileSearch, Integer> {

	@Query(value = "select * from ( "
			+ " select "
			+ " pf.PFILE_ID, "
			+ " pf.HPL as HPL_NAME, "
			+ " pf.HMODEL as HPL_MODEL_NAME, "
			+ " pf.YEAR, pf.MTH, pf.G2_LOT_NO, pf.FILE_NAME, "
			+ " (select rp.FILE_PATH from REL_PATH2 rp where 1=1 and rp.REL_ID = pf.FILE_PATH) as FILE_PATH, "
			+ " pf.FILE_TYPE, pf.FILE_SIZE, pf.CRC_VALUE, "
			+ " IIF(pf.CREATOR_ID='system','system',(select u.user_name from USR u where u.user_id = pf.CREATOR_ID)) as CREATOR_ID, pf.CREATED_DATETIME, pf.MODIFIER_ID, pf.MODIFIED_DATETIME, "
			+ " pf.HPL_ID, pf.HMODEL_ID "
			+ " from PROD_FILE pf "
			+ " where 1=1 "
			+ " and (?1 = '' or pf.HPL = ?1) "
			+ " and (?2 = '' or pf.HMODEL = ?2) "
			+ " and (?3 = '' or pf.YEAR = ?3) "
			+ " and (?4 = '' or pf.MTH = ?4) "
			+ "	and (?5 = '' OR ( "
			+ "		pf.G2_LOT_NO like case when ?6 = '1' then concat('%', ?5, '%') "
			+ "		when ?6 = '2' then concat('%', ?5) "
			+ "		when ?6 = '3' then ?5 "
			+ "		when ?6 = '4' then concat(?5, '%') end "
			+ "	 ) "
			+ " ) "
			+ "and (?7 = '' OR pf.FILE_PATH in ( "
			+ "	 select rp2.REL_ID from REL_PATH2 rp2 where rp2.REL_ID = pf.FILE_PATH "
			+ "	 and rp2.FILE_PATH like case when ?8 = '1' then concat('%', ?7, '%') "
			+ "	 when ?8 = '2' then concat('%', ?7) "
			+ "	 when ?8 = '3' then ?7 "
			+ "	 when ?8 = '4' then concat(?7, '%') end "
			+ "	 ) "
			+ " ) "
			+ "	and (?9 = '' OR ( "
			+ "		pf.FILE_NAME like case when ?10 = '1' then concat('%', ?9, '%') "
			+ "		when ?10 = '2' then concat('%', ?9) "
			+ "		when ?10 = '3' then ?9 "
			+ "		when ?10 = '4' then concat(?9, '%') end "
			+ "	 ) "
			+ " ) "
			+ ") tbl "
			, nativeQuery = true)
	public List<ProdFileSearch> searchByCriteria(String searchHplId, String searchHplModelId, String searchYear,
			String searchMth, String g2LotNo, String g2LotNoExp, String path, String pathExp, String fn, String fnExp);
	
	@Query(value = "select top 5 * from ( "
			+ " select "
			+ " pf.PFILE_ID, "
			+ " pf.HPL as HPL_NAME, "
			+ " pf.HMODEL as HPL_MODEL_NAME, "
			+ " pf.YEAR, pf.MTH, pf.G2_LOT_NO, pf.FILE_NAME, pf.FILE_PATH, "
			+ " pf.FILE_TYPE, pf.FILE_SIZE, pf.CRC_VALUE, "
			+ " IIF(pf.CREATOR_ID='system','system',(select u.user_name from USR u where u.user_id = pf.CREATOR_ID)) as CREATOR_ID, pf.CREATED_DATETIME, pf.MODIFIER_ID, pf.MODIFIED_DATETIME, "
			+ " pf.HPL_ID, pf.HMODEL_ID "
			+ " from PROD_FILE pf "
			+ " where 1=1 "
			+ " and (?1 = '' or pf.HPL = ?1) "
			+ " and (?2 = '' or pf.HMODEL = ?2) "
			+ " and (?3 = '' or pf.YEAR = ?3) "
			+ " and (?4 = '' or pf.MTH = ?4) "
			+ "	and (?5 = '' OR ( "
			+ "		pf.G2_LOT_NO like case when ?6 = '1' then concat('%', ?5, '%') "
			+ "		when ?6 = '2' then concat('%', ?5) "
			+ "		when ?6 = '3' then ?5 "
			+ "		when ?6 = '4' then concat(?5, '%') end "
			+ "	 ) "
			+ " ) "
			+ "and (?7 = '' OR pf.FILE_PATH in ( "
			+ "	 select rp2.REL_ID from REL_PATH2 rp2 where rp2.REL_ID = pf.FILE_PATH "
			+ "	 and rp2.FILE_PATH like case when ?8 = '1' then concat('%', ?7, '%') "
			+ "	 when ?8 = '2' then concat('%', ?7) "
			+ "	 when ?8 = '3' then ?7 "
			+ "	 when ?8 = '4' then concat(?7, '%') end "
			+ "	 ) "
			+ " ) "
			+ ") tbl "
			+ "order by tbl.CREATED_DATETIME desc"
			, nativeQuery = true)
	public List<ProdFileSearch> searchTop5ByCriteria(String searchHplId, String searchHplModelId, String searchYear,
			String searchMth, String g2LotNo, String g2LotNoExp, String path, String pathExp);
}
