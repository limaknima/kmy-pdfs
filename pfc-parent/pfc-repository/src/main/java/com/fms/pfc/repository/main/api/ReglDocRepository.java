package com.fms.pfc.repository.main.api;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.fms.pfc.domain.model.main.ReglDoc;

@Repository
public interface ReglDocRepository extends JpaRepository<ReglDoc, Integer> {
	
	@Query(value = "SELECT TOP 4 * "
			+ "FROM REGULATION_DOC "
			+ "WHERE REG_ID = ?1 "
			+ "AND ARCHIVED_FLAG = 'N' "
			+ "ORDER BY CREATED_DATETIME DESC", nativeQuery = true)
	List<ReglDoc> findTopFour(int fileId);
	
	@Query(value = "SELECT * "
			+ "FROM REGULATION_DOC "
			+ "WHERE REG_ID = ?1 "
			+ "AND ARCHIVED_FLAG = 'N'", nativeQuery = true)
	List<ReglDoc> findReglDocByRegId(int regId);

}
