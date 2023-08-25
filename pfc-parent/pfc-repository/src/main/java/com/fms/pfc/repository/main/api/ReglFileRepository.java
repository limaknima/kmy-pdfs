package com.fms.pfc.repository.main.api;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.fms.pfc.domain.model.main.ReglFile;

@Repository
public interface ReglFileRepository extends JpaRepository<ReglFile, Integer> {

	@Query(value = "select count(1) from REGL_FILE where 1=1 and CRC_VALUE = ?1", nativeQuery = true)
	Long countByCRC(Long crc);
	
	@Query(value = "select * from REGL_FILE where 1=1 and CRC_VALUE = ?1", nativeQuery = true)
	ReglFile findByCRC(Long crc);
	
	@Query(value = "select * from REGL_FILE where 1=1 and REGL_FILE_ID = ?1", nativeQuery = true)
	ReglFile findEntityById(Integer rgelFileId);
	
	@Query(value = "select * from REGL_FILE where 1=1 ", nativeQuery = true)
	List<ReglFile> findAllEntity();
	
	@Query(value = "select * from REGL_FILE where 1=1 and COUNTRY_ID = ?1", nativeQuery = true)
	List<ReglFile> findAllEntityByCountry(String countryId);
}
