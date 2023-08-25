package com.fms.pfc.repository.main.api;

import java.util.List;

import javax.transaction.Transactional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.fms.pfc.domain.model.main.ReglRm;

@Repository
public interface ReglRmRepository extends JpaRepository<ReglRm, Integer> {

	@Modifying
	@Transactional
	@Query(value = "delete from regl_rm where regl_file_id = ?1 and raw_matl_id = ?2 ", nativeQuery = true)
	Object deleteByFileAndRmId(int reglFileId, int rawMatlId);

	@Query(value = "select * from REGL_RM where 1=1 and REGL_FILE_ID = ?1", nativeQuery = true)
	List<ReglRm> findByParentId(Integer parent);
}
