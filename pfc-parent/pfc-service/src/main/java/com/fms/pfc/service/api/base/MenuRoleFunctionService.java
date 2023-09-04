package com.fms.pfc.service.api.base;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import javax.transaction.Transactional;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fms.pfc.domain.converter.MenuRoleFunctionConverter;
import com.fms.pfc.domain.dto.LabelAndValueDto;
import com.fms.pfc.domain.dto.MenuRoleFunctionDto;
import com.fms.pfc.domain.dto.RoleDto;
import com.fms.pfc.domain.model.MenuRoleFunction;
import com.fms.pfc.domain.model.UsrRole;
import com.fms.pfc.repository.base.api.MenuRoleFunctionRepository;

@Service
public class MenuRoleFunctionService {
	private final Logger logger = LoggerFactory.getLogger(MenuRoleFunctionService.class);

	private MenuRoleFunctionRepository mrfRepo;
	private MenuRoleFunctionConverter mrfConv;
	private RoleService roleServ;
	private UsrRoleService usrRoleServ;
	
	private static final String ROLE_ID_ADM = "ADM";

	@Autowired
	public MenuRoleFunctionService(MenuRoleFunctionRepository mrfRepo, MenuRoleFunctionConverter mrfConv,
			RoleService roleServ, UsrRoleService usrRoleServ) {
		super();
		this.mrfRepo = mrfRepo;
		this.mrfConv = mrfConv;
		this.roleServ = roleServ;
		this.usrRoleServ = usrRoleServ;
	}

	public MenuRoleFunction findById(Integer id) {
		return mrfRepo.findById(id).orElse(null);
	}

	public MenuRoleFunctionDto findDtoById(Integer id) {
		MenuRoleFunction obj = mrfRepo.findById(id).orElse(null);
		MenuRoleFunctionDto dto = mrfConv.entityToDto(obj);
		return dto;
	}

	public List<MenuRoleFunctionDto> findAllDto(boolean uiDisplay) {
		List<MenuRoleFunction> entList = mrfRepo.findAll();
		List<MenuRoleFunctionDto> dtoList = mrfConv.entityToDtoList(entList);
		if (uiDisplay) {
			dtoList.forEach(dto -> {
				dto.setMenuName(getMenuName(dto.getMenuItemId()));
				dto.setRoleName(getRoleName(dto.getRoleId()));
				dto.setFunctionName(getFunctionName(dto.getFunctionType()));
			});
		}
		return dtoList;
	}

	public List<MenuRoleFunctionDto> searchByCriteria(Integer menuItemId, String roleId, Integer recordTypeId,
			Integer funcType, boolean uiDisplay) {
		List<MenuRoleFunction> entList = mrfRepo.searchByCriteria(menuItemId == null ? 0 : menuItemId, roleId,
				recordTypeId == null ? 0 : recordTypeId, funcType == null ? 0 : funcType);
		List<MenuRoleFunctionDto> dtoList = mrfConv.entityToDtoList(entList);
		if (uiDisplay) {
			dtoList.forEach(dto -> {
				dto.setMenuName(getMenuName(dto.getMenuItemId()));
				dto.setRoleName(getRoleName(dto.getRoleId()));
				dto.setFunctionName(getFunctionName(dto.getFunctionType()));
			});
		}
		return dtoList;
	}

	/**
	 * Menu drop down items
	 * 
	 * @return List<LabelAndValueDto>
	 */
	public List<LabelAndValueDto> getMenuItemsList() {
		List<LabelAndValueDto> items = new ArrayList<>();
		List<Object[]> obj = mrfRepo.getMenuItemsList();
		for (Object[] o : obj) {
			LabelAndValueDto lbl = new LabelAndValueDto((String) o[1], (Integer) o[0]);
			items.add(lbl);
		}
		return items;
	}
	
	/**
	 * Menu drop down items
	 * 
	 * @return List<LabelAndValueDto>
	 */
	public List<LabelAndValueDto> getAllMenuItemsList() {
		List<LabelAndValueDto> items = new ArrayList<>();
		List<Object[]> obj = mrfRepo.getAllMenuItemsList();
		int idx = 1;
		for (Object[] o : obj) {
			String label = "#"+idx+" "+(String) o[1];
			
			if((Integer) o[2] ==0 ) idx ++; 
			if((Integer) o[2] !=0 ) {
				label ="---- " + (String) o[1];
			}
			LabelAndValueDto lbl = new LabelAndValueDto(label, (Integer) o[0]);
			items.add(lbl);
		}
		return items;
	}

	/**
	 * Role drop down items
	 * 
	 * @return List<RoleDto>
	 */
	public List<RoleDto> getRoleList() {
		return roleServ.findAll();
	}

	/**
	 * Function drop down items
	 * 
	 * @return List<LabelAndValueDto>
	 */
	public List<LabelAndValueDto> getFunctionList() {
		List<LabelAndValueDto> items = new ArrayList<>();
		int functionLen = 10;
		for (int i = 1; i <= functionLen; i++) {
			items.add(new LabelAndValueDto(getFunctionName(i), i));
		}
		return items;
	}

	@Transactional
	public Integer save(MenuRoleFunctionDto dto, String userId) {
		Date currentDate = new Date();
		// save parent
		if (Objects.isNull(dto.getPkMrfId()) || 0 == dto.getPkMrfId()) {
			dto.setCreatorId(userId);
			dto.setCreatedDatetime(currentDate);
			dto.setRecordTypeId(dto.getMenuItemId());
		} else {
			MenuRoleFunctionDto existing = findDtoById(dto.getPkMrfId());
			dto.setRecordTypeId(existing.getRecordTypeId());
			dto.setCreatorId(existing.getCreatorId());
			dto.setCreatedDatetime(existing.getCreatedDatetime());
			dto.setModifierId(userId);
			dto.setModifiedDatetime(currentDate);
		}

		MenuRoleFunction entity = mrfConv.dtoToEntity(dto);
		mrfRepo.saveAndFlush(entity);

		return entity.getPkMrfId();
	}

	public List<UsrRole> searchUserRole(String userId) {
		return usrRoleServ.searchUserRole(userId);
	}
	
	/**
	 * Function filtering by module and role
	 * @param model
	 * @param menuItemId
	 * @param userId
	 */
	public void menuRoleFunction(Map<String, Object> model, int menuItemId, String userId) {
		List<MenuRoleFunctionDto> mrf = (List<MenuRoleFunctionDto>) model.get("MRF" + menuItemId);
		List<UsrRole> userRoles = searchUserRole(userId);
		List<String> roleStrList = userRoles.stream().map(UsrRole::getRoleId).sorted().collect(Collectors.toList());
		model.put("isAdmin", roleStrList.contains(ROLE_ID_ADM) ? true : false);

		// default function/button to hidden
		List<Integer> func = mrf.stream().map(MenuRoleFunctionDto::getFunctionType).distinct()
				.collect(Collectors.toList());
		for (Integer idx : func) {
			model.put("hidFunctionType_" + idx, true);
		}

		// then set function/button based on role
		for (MenuRoleFunctionDto menuRole : mrf.stream().filter(arg0 -> arg0.getRoleId().equals(roleStrList.get(0)))
				.collect(Collectors.toList())) {
			for (UsrRole role : userRoles) {
				if (StringUtils.equals(menuRole.getRoleId(), role.getRoleId())) {
					logger.debug("1. menurole={}, role={}, function={}", menuRole.getRoleId(), role.getRoleId(),
							menuRole.getFunctionType());
					model.put("hidFunctionType_" + menuRole.getFunctionType(), false);
				} else {
					logger.debug("2. menurole={}, role={}, function={}", menuRole.getRoleId(), role.getRoleId(),
							menuRole.getFunctionType());
					model.put("hidFunctionType_" + menuRole.getFunctionType(), true);
				}
			}
		}
	}

	@Transactional
	public void delete(Integer id) {
		mrfRepo.deleteById(id);
	}

	private String getFunctionName(Integer functionType) {
		String fName = "";
		switch (functionType) {
		case 1:
			fName = "Insert";
			break;
		case 2:
			fName = "Update";
			break;
		case 3:
			fName = "Delete";
			break;
		case 4:
			fName = "Search";
			break;
		case 5:
			fName = "View";
			break;
		case 6:
			fName = "Purge";
			break;
		case 7:
			fName = "Download";
			break;
		case 8:
			fName = "Copy";
			break;
		case 9:
			fName = "Renew";
			break;
		case 10:
			fName = "Upload";
			break;
		default:
			fName = "";
			break;
		}
		return fName;
	}

	private String getRoleName(String roleId) {
		String rName = roleServ.findOneById(roleId).getRoleName();
		return rName;
	}

	private String getMenuName(Integer menuItemId) {
		String rName = mrfRepo.getMenuItemName(menuItemId);
		return rName;
	}

}
