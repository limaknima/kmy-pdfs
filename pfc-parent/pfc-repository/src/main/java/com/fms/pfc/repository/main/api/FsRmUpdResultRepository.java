package com.fms.pfc.repository.main.api;

import javax.transaction.Transactional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import com.fms.pfc.domain.model.main.FsRmUpdResult;

public interface FsRmUpdResultRepository extends JpaRepository<FsRmUpdResult, Integer> {
	@Modifying
	@Transactional
	@Query(value = "DELETE FROM FS_RM_UPD_RESULT WHERE UPD_ID = ?1", nativeQuery = true)
	void delFsRmUpdResultByParentId(Integer parentId);
}
