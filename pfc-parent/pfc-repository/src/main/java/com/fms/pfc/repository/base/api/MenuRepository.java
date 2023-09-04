package com.fms.pfc.repository.base.api;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.fms.pfc.domain.model.MenuItem;

@Repository
public interface MenuRepository extends JpaRepository<MenuItem, String> {
	
	@Query(value = "select a.* from menu_item a "
			+ "inner join grp_menu_item b "
			+ "on a.menu_id = b.menu_id "
			+ "and a.parent_menu_item_id = b.parent_menu_item_id "
			+ "and a.menu_item_id = b.menu_item_id "
			+ "where b.grp_id = ?1 "
			+ "and a.url = ?2", nativeQuery = true)
	MenuItem checkAuthority(String groupId, String url);
	
	@Query(value = "select a.* from menu_item a "
			+ "where 1=1 "
			+ "and a.seq_no != 0 "
			+ "order by a.menu_item_id", nativeQuery = true)
	List<MenuItem> searchMenuItems();
	
	@Query(value = "select a.* from menu_item a "
			+ "where 1=1 "
			+ "and a.menu_item_id =?1 "
			+ "order by a.seq_no", nativeQuery = true)
	MenuItem searchMenuItem(int menuItemId);
	
}
