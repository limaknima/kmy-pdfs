package com.fms.pfc.repository.main.api;

import java.util.Date;
import java.util.List;

import javax.transaction.Transactional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.fms.pfc.domain.model.main.PrRmAdditive;

@Repository
public interface PrRmAdditiveRepository extends JpaRepository<PrRmAdditive, String> {    				   
	
	@Query(value = " SELECT * FROM PR_RM_ADDITIVE "
			+ " WHERE PR_ID = ?1 ", nativeQuery = true)
	List<PrRmAdditive> searchPrRmAdditive(int prId);
	
	@Modifying
	@Transactional
	@Query(value = "INSERT INTO PR_RM_ADDITIVE "
			+ "(PR_ID, RAW_MATL_ID, ADDITIVE_DESC, CREATOR_ID, CREATED_DATETIME) "
			+ "VALUES "
			+ "((SELECT PR_ID FROM PRODUCT_RECIPE WHERE PR_CODE = ?1), ?2, ?3, ?4, ?5)", nativeQuery = true)
	void addPrRmAdditive(String prCode, int rawMatlId, String additiveDesc, String creatorId, Date date);
	
	@Modifying
	@Transactional
	@Query(value = "UPDATE PR_RM_ADDITIVE SET RAW_MATL_ID = ?2, ADDITIVE_DESC = ?3, MODIFIER_ID = ?4, "
			+ "MODIFIED_DATETIME = ?5 "
			+ "WHERE PR_RM_ADDT_ID = ?1", nativeQuery = true)
	void updPrRmAdditive(int prRmAddtId, int rawMatlId, String additiveDesc, String modifierId, Date date);
	
	@Modifying
	@Transactional
	@Query(value = "DELETE FROM PR_RM_ADDITIVE WHERE PR_ID = ?1", nativeQuery = true)
	void delPrRmAdditive(int prId);

	@Modifying
	@Transactional
	@Query(value = "DELETE FROM PR_RM_ADDITIVE WHERE PR_RM_ADDT_ID = ?1", nativeQuery = true)
	void delDetailPrRmAdditive(int prRmAddId);
	
	@Modifying
	@Transactional
	@Query(value = "DELETE FROM PR_RM_ADDITIVE WHERE PR_ID = ?1 AND RAW_MATL_ID = ?2", nativeQuery = true)
	void delPrRmAddByMatl(int prId, int rawMatlId);
}

