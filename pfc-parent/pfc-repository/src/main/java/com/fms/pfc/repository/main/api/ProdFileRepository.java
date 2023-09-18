package com.fms.pfc.repository.main.api;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.fms.pfc.domain.model.main.ProdFile;

@Repository
public interface ProdFileRepository extends JpaRepository<ProdFile, Integer> {

	@Query(value = "select count(*) as cntpermth from PROD_FILE "
			+ "where 1=1 "
			+ "and hpl = ?1 "
			+ "and YEAR(CREATED_DATETIME) = ?2 "
			+ "and MONTH(CREATED_DATETIME) = ?3 "
			+ "group by hpl, MONTH(CREATED_DATETIME)", nativeQuery = true)
	Integer countFileByCriteria(String hpl, int year, int month);
	
	@Query(value = "select count(*) as cnt from PROD_FILE "
			+ "where 1=1 "
			+ "and FILE_NAME = ?1 "
			+ "and G2_LOT_NO = ?2 "
			+ "and HPL = ?3 ", nativeQuery = true)
	Integer countDuplicateFile(String fileName, String lotNo, String hpl);
}
