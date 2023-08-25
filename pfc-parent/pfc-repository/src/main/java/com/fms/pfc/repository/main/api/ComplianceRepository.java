package com.fms.pfc.repository.main.api;

import java.util.List;

import javax.transaction.Transactional;

import org.springframework.data.jpa.repository.JpaRepository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.fms.pfc.domain.model.main.Compliance;
import com.fms.pfc.domain.model.main.PrStat;

@Repository
public interface ComplianceRepository extends JpaRepository<Compliance, String> {    				   
	
	//Query to get product name and country name	
	@Query(value = " SELECT C.COUNTRY_NAME,"
			+ " B.*,"
			+ " B.PR_ID,"
			+ " CONCAT("
			+ "		A.PR_NAME,"
			+ "		(case when ISNULL(A.PR_OTHER_NAME1,'')!='' then concat(' / ' ,A.PR_OTHER_NAME1) end),"
			+ "		(case when ISNULL(A.PR_OTHER_NAME2,'')!='' then concat(' / ' ,A.PR_OTHER_NAME2) end),"
			+ "		(case when ISNULL(A.PR_OTHER_NAME3,'')!='' then concat(' / ' ,A.PR_OTHER_NAME3) end),"
			+ "		(case when ISNULL(A.PR_OTHER_NAME4,'')!='' then concat(' / ' ,A.PR_OTHER_NAME4) end),"
			+ "		(case when ISNULL(A.PR_OTHER_NAME5,'')!='' then concat(' / ' ,A.PR_OTHER_NAME5) end)"
			+ "	) as PR_NAME,"
			+ " B.FINAL_STATUS"
			+ " FROM PR_STAT B "
			+ "						LEFT JOIN PRODUCT_RECIPE A "
			+ "						ON A.PR_ID = B.PR_ID "
			+ "						LEFT JOIN REF_COUNTRY C "
			+ "						ON C.COUNTRY_ID = B.COUNTRY_ID"
			+ "			WHERE B.PR_ID  IN (?1) "
			+ "			AND B.COUNTRY_ID IN (?2) "
			+ "order by C.COUNTRY_NAME ", nativeQuery = true)
	
	List<Compliance> searchComplianceCountry(List<Integer> prId,List<String> countryID);

}

