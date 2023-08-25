package com.fms.pfc.repository.main.api;

import java.util.List;

import javax.transaction.Transactional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.fms.pfc.domain.model.main.RmManfSuppl;

@Repository
public interface RmManfSupplRepository extends JpaRepository<RmManfSuppl, String> {

	@Query(value = "SELECT A.*, V.VENDOR_NAME FROM RM_MANF_SUPPL A INNER JOIN VENDOR V "
			+ "ON V.VENDOR_ID = A.SUPPL_ID WHERE A.RM_MANF_ID = ?1 "
			+ "AND (?2 = '' OR (V.VENDOR_NAME LIKE CASE WHEN ?3 = '1' THEN CONCAT('%', ?2, '%') "
			+ "							 	      WHEN ?3 = '2' THEN CONCAT('%', ?2) "
			+ "			      				  	  WHEN ?3 = '3' THEN ?2 "
			+ "			    				   	  WHEN ?3 = '4' THEN CONCAT(?2, '%') END))", nativeQuery = true)
	List<RmManfSuppl> searchRmSupplier(int rmManfId, String vendor, String expr);

	@Modifying
	@Transactional
	@Query(value = "DELETE A FROM RM_MANF_SUPPL A INNER JOIN RM_MANF B ON A.RM_MANF_ID = B.RM_MANF_ID "
			+ "WHERE B.RAW_MATL_ID = ?1", nativeQuery = true)
	void delRmManfSuppl(int rawMatlId);
	
	@Modifying
	@Transactional
	@Query(value = "DELETE FROM RM_MANF_SUPPL WHERE RM_MANF_ID = ?1", nativeQuery = true)
	void clrRmManfSuppl(int rmManfId);

	@Modifying
	@Transactional
	@Query(value = "INSERT INTO RM_MANF_SUPPL (RM_MANF_ID, SUPPL_ID) " 
			+ "VALUES "
			+ "((SELECT RM_MANF_ID FROM RM_MANF WHERE MANF_ID = "
			+ "(SELECT VENDOR_ID FROM VENDOR WHERE VENDOR_ID = ?1) "
			+ "AND RAW_MATL_ID = (SELECT RAW_MATL_ID FROM RAW_MATERIAL WHERE RAW_MATL_NAME = ?2)), ?3)", nativeQuery = true)
	void addRmManfSuppl(int manfId, String matlName, int supplId);
}
