package com.fms.pfc.repository.main.api;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.fms.pfc.domain.model.main.RmStatGrp;

@Repository
public interface RmStatGrpRepository extends JpaRepository<RmStatGrp, String> {

	@Query(value = "SELECT RAW_MATL_ID, FINAL_STATUS, COUNTRY_ID, DATA_CHG_FLAG FROM RM_STAT "
			+ "WHERE RAW_MATL_ID = ?1 "
			+ "GROUP BY RAW_MATL_ID, FINAL_STATUS, COUNTRY_ID, DATA_CHG_FLAG", nativeQuery = true)
	List<RmStatGrp> searchRmStatGrp(int rawMatlId);
}
