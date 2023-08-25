package com.fms.pfc.repository.main.api;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.fms.pfc.domain.model.main.HplModel;

@Repository
public interface HplModelRepository extends JpaRepository<HplModel, Integer> {

	@Query(value = "select * from HPL_MODEL where 1=1 and 0 = ?1 or HPL_ID = ?1", nativeQuery = true)
	List<HplModel> findAllByParentNative(Integer parentId);
}
