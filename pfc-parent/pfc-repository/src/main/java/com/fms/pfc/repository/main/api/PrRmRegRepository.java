package com.fms.pfc.repository.main.api;

import java.util.List;

import javax.transaction.Transactional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.fms.pfc.domain.model.main.PrRmReg;

@Repository
public interface PrRmRegRepository extends JpaRepository<PrRmReg, String> {

	@Query(value = "SELECT * FROM PR_RM_REG "
			+ "WHERE PR_ID = ?1 ", nativeQuery = true)
	List<PrRmReg> searchPrRmReg(int prId);

	@Modifying
	@Transactional
	@Query(value = "INSERT INTO PR_RM_REG "
			+ "(PR_ID, RAW_MATL_ID, COUNTRY_ID, REG_DOC_ID) "
			+ "VALUES "
			+ "((SELECT PR_ID FROM PRODUCT_RECIPE WHERE PR_CODE = ?1), ?2, ?3, ?4)", nativeQuery = true)
	void addPrRmReg(String prCode, int rawMatlId, String countryId, int regDocId);

	@Modifying
	@Transactional
	@Query(value = "DELETE FROM PR_RM_REG "
			+ "WHERE PR_ID = ?1 ", nativeQuery = true)
	void delPrRmReg(int prId);

}
