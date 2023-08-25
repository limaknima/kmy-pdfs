package com.fms.pfc.repository.main.api;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.fms.pfc.domain.model.main.RawMaterialReport;

@Repository
public interface RawMatlReportRepository extends JpaRepository<RawMaterialReport, String> {

	@Query(value = "SELECT "
			+ "R.RAW_MATL_ID "
			+ ",R.RAW_MATL_NAME "
			+ ",I.RM_ING_ID "
			+ ",I.ING_NAME "
			+ ",I.COMP_PERC "
			+ ",M.TS_NO AS MANF_TS "
			+ ",(SELECT CONVERT(varchar, M.VIPD_DATE, 103)) AS VIPD_DATE "
			+ ",(SELECT CONVERT(varchar, M.VIPD_ANNEX2_DATE, 103)) AS VIPD_ANNEX2_DATE "
			+ ",(SELECT V.VENDOR_NAME FROM VENDOR V WHERE V.VENDOR_ID=P.SUPPL_ID) AS SUPPLIER "
			+ ",(SELECT V.VENDOR_NAME FROM VENDOR V WHERE V.VENDOR_ID=M.MANF_ID) AS MANUFACTURER "
			+ ",R.INS_NO AS RM_INS "
			+ ",R.E_NO AS RM_ENO "
			+ ",R.FEMA_NO AS RM_FEMA "
			+ ",R.JECFA_NO AS RM_JECFA "
			+ ",R.CAS_NO AS RM_CAS "
			+ ",R.GMO_STATUS AS RM_GMO "
			+ ",R.FLAVOR_STATUS AS RM_FLAV "
			+ ",R.PHO_FLAG AS RM_PHO "
			+ ",M.RM_MANF_ID "
			+ ",S.RM_STAT_ID "
			+ ",CASE S.VIPD_STATUS WHEN 1 THEN 'PERMITTED' WHEN 2 THEN 'NOT PERMITTED' ELSE 'NOT SURE' END AS VIPD_STATUS "
			+ ",CASE S.FINAL_STATUS WHEN 1 THEN 'PERMITTED' WHEN 2 THEN 'NOT PERMITTED' ELSE 'NOT SURE' END AS FINAL_STATUS "
			+ ",(SELECT COUNTRY_NAME FROM REF_COUNTRY WHERE COUNTRY_ID = S.COUNTRY_ID) AS COUNTRY "
			+ ",RE.REG_NAME "
			+ ",RE.REF_URL1 "
			+ ",RE.REMARKS "
			+ "FROM RAW_MATERIAL R "
			+ "LEFT JOIN RM_MANF M ON R.RAW_MATL_ID=M.RAW_MATL_ID "
			+ "LEFT JOIN RM_MANF_SUPPL P ON M.RM_MANF_ID = P.RM_MANF_ID "
			+ "LEFT JOIN RM_INGREDIENT I ON (M.RAW_MATL_ID = I.RAW_MATL_ID AND M.MANF_ID = I.MANF_ID) "
			+ "LEFT JOIN RM_STAT S ON (M.RAW_MATL_ID = S.RAW_MATL_ID AND M.MANF_ID = S.MANF_ID) "
			+ "LEFT JOIN REGULATION RE ON (RE.RAW_MATL_ID = M.RAW_MATL_ID AND RE.COUNTRY_ID = S.COUNTRY_ID) "
			+ "WHERE 1=1 "
			+ "AND ('0' = ?1 OR R.RAW_MATL_ID = ?1) "
			+ "AND ('' = ?2 OR R.TS_NO LIKE CONCAT('%', ?2, '%')) "
			+ "AND ('0' = ?3 OR M.RM_MANF_ID  = ?3) "
			+ "AND ('0' = ?4 OR P.SUPPL_ID = ?4) "
			+ "AND ('' = ?5 OR S.COUNTRY_ID = ?5) "
			+ "AND ('' = ?6 OR R.FEMA_NO LIKE CONCAT('%', ?6, '%')) "
			+ "AND ('' = ?7 OR R.JECFA_NO LIKE CONCAT('%', ?7, '%')) "
			+ "AND ('' = ?8 OR R.INS_NO LIKE CONCAT('%', ?8, '%')) "
			+ "ORDER BY S.COUNTRY_ID ", nativeQuery = true)
	List<RawMaterialReport> getRawMaterialReport(int rmId, String tsNo, int manufacturerId, int supplierId, 
			String countryId, String fema, String jecfa, String ins);
	
	@Query(value = "SELECT "
			+ "DISTINCT(R.RAW_MATL_ID) "
			+ "FROM RAW_MATERIAL R "
			+ "LEFT JOIN RM_MANF M ON R.RAW_MATL_ID=M.RAW_MATL_ID "
			+ "LEFT JOIN RM_MANF_SUPPL P ON M.RM_MANF_ID = P.RM_MANF_ID "
			+ "LEFT JOIN RM_INGREDIENT I ON (M.RAW_MATL_ID = I.RAW_MATL_ID AND M.MANF_ID = I.MANF_ID) "
			+ "LEFT JOIN RM_STAT S ON (M.RAW_MATL_ID = S.RAW_MATL_ID AND M.MANF_ID = S.MANF_ID) "
			+ "LEFT JOIN REGULATION RE ON (RE.RAW_MATL_ID = M.RAW_MATL_ID AND RE.COUNTRY_ID = S.COUNTRY_ID) "
			+ "WHERE 1=1 "
			+ "AND ('0' = ?1 OR R.RAW_MATL_ID = ?1) "
			+ "AND ('' = ?2 OR R.TS_NO LIKE CONCAT('%', ?2, '%')) "
//			+ "AND ('0' = ?3 OR M.RM_MANF_ID  = ?3) "
			+ "AND ('0' = ?3 OR M.MANF_ID  = ?3) "
			+ "AND ('0' = ?4 OR P.SUPPL_ID = ?4) "
//			+ "AND ('' = ?5 OR S.COUNTRY_ID = ?5) "
			+ "AND ('' = ?5 OR M.ORIGIN_COUNTRY_ID = ?5) "
			+ "AND ('' = ?6 OR R.FEMA_NO LIKE CONCAT('%', ?6, '%')) "
			+ "AND ('' = ?7 OR R.JECFA_NO LIKE CONCAT('%', ?7, '%')) "
			+ "AND ('' = ?8 OR R.INS_NO LIKE CONCAT('%', ?8, '%')) "
			+ "ORDER BY R.RAW_MATL_ID ", nativeQuery = true)
	List<Integer> getRawMaterialIds(int rmId, String tsNo, int manufacturerId, int supplierId, 
			String countryId, String fema, String jecfa, String ins);

}
