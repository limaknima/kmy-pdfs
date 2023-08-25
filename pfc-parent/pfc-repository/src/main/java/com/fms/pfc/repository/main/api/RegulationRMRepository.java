package com.fms.pfc.repository.main.api;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.fms.pfc.domain.model.main.RegulationRawMaterial;

@Repository
public interface RegulationRMRepository extends JpaRepository<RegulationRawMaterial, String> {	 
	
	@Query(value = " SELECT RM.RAW_MATL_NAME FROM REGULATION R "
			+ "	INNER JOIN RAW_MATERIAL RM "
			+ "		ON R.RAW_MATL_ID = RM.RAW_MATL_ID "
			+ "	INNER JOIN REGULATION_DOC RD "
			+ "		ON R.REG_ID = RD.REG_ID "
			+ "	AND RD.ARCHIVED_FLAG = 'N' "
			+ "	GROUP BY RM.RAW_MATL_NAME", nativeQuery = true)
	List<RegulationRawMaterial> searchRawMaterialName();

}
