package com.fms.pfc.repository.base.api;

import java.util.List;

import javax.transaction.Transactional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.fms.pfc.domain.model.GrpMenuItem;

@Repository
public interface GrpMenuRepository extends JpaRepository<GrpMenuItem, String> {

	@Query(value = "select * from grp_menu_item where grp_id = ?1 order by menu_item_id", nativeQuery = true)
	List<GrpMenuItem> searchMenu(String groupId);
	
	@Query(value = "select * from grp_menu_item where 1=1 "
			+ "and grp_id = ?1 "
			+ "and charindex(?2, roles) <> 0 "
			+ "and ?2 in (select value from [dbo].[fn_split](ROLES,',') ) "
			+ "order by menu_item_id ", nativeQuery = true)
	List<GrpMenuItem> searchMenu(String groupId, String roleId);
	
	@Query(value = "select * from grp_menu_item where grp_id = ?1 and menu_item_id = ?2 order by menu_item_id", nativeQuery = true)
	List<GrpMenuItem> searchMenu(String groupId, int menuItemId);
	
	@Query(value = "select group_id from grp where 1=1 and org_id =?1 order by 1", nativeQuery = true)
	List<String> defaultGrpList(String orgId);
	
	@Query(value = "select distinct grp_id from grp_menu_item where 1=1 order by 1", nativeQuery = true)
	List<String> defaultGrpList();

	@Modifying
	@Transactional
	@Query(value = "INSERT INTO GRP_MENU_ITEM (GRP_ID,MENU_ID,PARENT_MENU_ITEM_ID,MENU_ITEM_ID,ROLES) " + "VALUES "
			+ "(?1, ?2, ?3, ?4, ?5)", nativeQuery = true)
	void addGrpMenuItem(String grpId, int menuId, int parentMenuItemId, int menuItemId, String roles);

	@Modifying
	@Transactional
	@Query(value = "DELETE FROM GRP_MENU_ITEM WHERE GRP_ID = ?1 "
			+ "AND (0 = ?2 OR MENU_ITEM_ID = ?2) ", nativeQuery = true)
	void deleteGrpMenuItem(String grpId, int menuItemId);

}
