package com.fms.pfc.repository.main.api;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.fms.pfc.domain.model.main.ReglInfo;

@Repository
public interface ReglInfoRepository extends JpaRepository<ReglInfo, Integer> {

	@Query(value = "select * from REGL_INFO where 1=1 and REGL_FILE_ID = ?1", nativeQuery = true)
	List<ReglInfo> findByParentId(Integer parent);
}
