package com.fms.pfc.repository.base.api;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.fms.pfc.domain.model.GrpMenuItem;

@Repository
public interface GrpMenuRepository extends JpaRepository<GrpMenuItem, String> {

	@Query(value = "select * from grp_menu_item where grp_id = ?1", nativeQuery = true)
	List<GrpMenuItem> searchMenu(String groupId);

}
