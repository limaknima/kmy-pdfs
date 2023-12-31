package com.fms.pfc.repository.base.api;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.fms.pfc.domain.model.MenuRoleFunction;

public interface MenuRoleFunctionRepository extends JpaRepository<MenuRoleFunction, Integer> {

	@Query(value = "select "
			+ "menu_item_name "
			+ "from menu_item "
			+ "where 1=1 "
			+ "and menu_item_id = ?1", nativeQuery = true)
	String getMenuItemName(Integer menuItemId);
	
	@Query(value = "select "
			+ "menu_item_id "
			+ ",menu_item_name "
			+ "from menu_item "
			+ "where 1=1 "
			+ "and parent_menu_item_id !=0 "
			+ "and seq_no !=0 ", nativeQuery = true)
	List<Object[]> getMenuItemsList();
	
	@Query(value = "select "
			+ "menu_item_id "
			+ ",menu_item_name "
			+ ",parent_menu_item_id "
			+ "from menu_item "
			+ "where 1=1 "
			+ "and seq_no !=0 "
			+ "order by seq_no", nativeQuery = true)
	List<Object[]> getAllMenuItemsList();
	
	@Query(value = "select "
			+ "* "
			+ "from MENU_ITEM_ROLE_FUNCTION "
			+ "where 1=1 "
			+ "and (0 = ?1 or menu_item_id = ?1) "
			+ "and ('' = ?2 or role_id = ?2) "
			+ "and (0 = ?3 or record_type_id = ?3) "
			+ "and (0 = ?4 or function_type = ?4) "
			, nativeQuery = true)
	List<MenuRoleFunction> searchByCriteria(Integer menuItemId, String roleId, Integer recordTypeId, Integer funcType);
}
