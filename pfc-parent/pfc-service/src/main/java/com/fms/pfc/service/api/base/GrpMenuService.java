package com.fms.pfc.service.api.base;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fms.pfc.domain.converter.GrpMenuItemConverter;
import com.fms.pfc.domain.dto.GrpMenuItemDto;
import com.fms.pfc.domain.dto.LabelAndValueDto;
import com.fms.pfc.domain.model.GrpMenuItem;
import com.fms.pfc.domain.model.MenuItem;
import com.fms.pfc.repository.base.api.GrpMenuRepository;
import com.fms.pfc.repository.base.api.MenuRepository;

@Service
public class GrpMenuService {

	private final Logger logger = LoggerFactory.getLogger(GrpMenuService.class);
			
	private GrpMenuRepository grpMenuRepo;
	private GrpMenuItemConverter gmiConv;
	private MenuRepository menuRepo;

	@Autowired
	public GrpMenuService(GrpMenuRepository grpMenuRepo, MenuRepository menuRepo, GrpMenuItemConverter gmiConv) {
		super();
		this.grpMenuRepo = grpMenuRepo;
		this.menuRepo = menuRepo;
		this.gmiConv = gmiConv;
	}

	public List<GrpMenuItem> searchMenu(String groupId) {
		return grpMenuRepo.searchMenu(groupId);
	}

	public List<GrpMenuItemDto> searchMenuDto(String groupId) {
		List<GrpMenuItem> entList = grpMenuRepo.searchMenu(groupId);
		List<GrpMenuItemDto> dtos = gmiConv.entityToDtoList(entList);

		int idx = 1;
		for (GrpMenuItemDto dto : dtos) {
			if (dto.getParentMenuId() != 0) {
				dto.setMenuItemNameTemp("---- " + menuRepo.searchMenuItem(dto.getMenuItemId()).getMenuItemName());
			} else {
				dto.setMenuItemNameTemp(
						"#" + idx + " " + menuRepo.searchMenuItem(dto.getMenuItemId()).getMenuItemName());
				idx++;
			}
		}
		return dtos;
	}
	
	public List<LabelAndValueDto> searchMenuDto2(String groupId) {
		List<LabelAndValueDto> result = new ArrayList<LabelAndValueDto>();
		List<GrpMenuItem> entList = grpMenuRepo.searchMenu(groupId);
		List<GrpMenuItemDto> dtos = gmiConv.entityToDtoList(entList);

		int idx = 1;
		String label = "";
		for (GrpMenuItemDto dto : dtos) {
			if (dto.getParentMenuId() != 0) {
				label = "---- " + menuRepo.searchMenuItem(dto.getMenuItemId()).getMenuItemName();
				//dto.setMenuItemNameTemp(label);
			} else {
				label = "#" + idx + " " + menuRepo.searchMenuItem(dto.getMenuItemId()).getMenuItemName();
				//dto.setMenuItemNameTemp(label);
				idx++;
			}
			result.add(new LabelAndValueDto(label, dto.getMenuItemId()));
		}
		return result;
	}

	public List<GrpMenuItem> searchMenu(String groupId, int menuItemId) {
		return grpMenuRepo.searchMenu(groupId, menuItemId);
	}

	public List<GrpMenuItemDto> searchMenuDto(String groupId, int menuItemId) {
		List<GrpMenuItem> entList = grpMenuRepo.searchMenu(groupId, menuItemId);
		List<GrpMenuItemDto> dtos = gmiConv.entityToDtoList(entList);
		int idx = 1;
		for (GrpMenuItemDto dto : dtos) {
			if (dto.getParentMenuId() != 0) {
				dto.setMenuItemNameTemp("---- " + menuRepo.searchMenuItem(dto.getMenuItemId()).getMenuItemName());
			} else {
				dto.setMenuItemNameTemp(
						"#" + idx + " " + menuRepo.searchMenuItem(dto.getMenuItemId()).getMenuItemName());
				idx++;
			}
		}
		return dtos;
	}

	public MenuItem checkAuthority(String groupId, String url) {
		return menuRepo.checkAuthority(groupId, url);
	}

	public void addGrpMenuItem(String grpId, int menuId, int parentMenuItemId, int menuItemId) {
		grpMenuRepo.addGrpMenuItem(grpId, menuId, parentMenuItemId, menuItemId);
	}

	public void deleteGrpMenuItem(String grpId, int menuItemId) {
		grpMenuRepo.deleteGrpMenuItem(grpId, menuItemId);
	}

	public List<LabelAndValueDto> searchMenuItems() {
		List<LabelAndValueDto> result = new ArrayList<LabelAndValueDto>();
		List<MenuItem> items = menuRepo.searchMenuItems();
		int idx = 1;
		String label = "";
		for (MenuItem dto : items) {
			if (dto.getParentMenuId() != 0) {
				label = "---- " + menuRepo.searchMenuItem(dto.getMenuItemId()).getMenuItemName();
				//dto.setMenuItemNameTemp(label);
			} else {
				label = "#" + idx + " " + menuRepo.searchMenuItem(dto.getMenuItemId()).getMenuItemName();
				//dto.setMenuItemNameTemp(label);
				idx++;
			}
			result.add(new LabelAndValueDto(label, dto.getMenuItemId()));
		}
		return result;
	}

	public void save(String hpl, List<String> selected, List<String> current) {
		// existing from db
		List<String> db = current;

		// current from ui
		List<String> ui = selected;

		// compare db to current
		List<String> deleted = db.stream().filter(obj -> !ui.contains(obj)).collect(Collectors.toList());
		deleted.forEach(arg0 -> {
			grpMenuRepo.deleteGrpMenuItem(hpl, Integer.valueOf(arg0));
		});
//		deleted.forEach(arg0 -> {
//			logger.debug(" deleted >>> "+ arg0);
//		});

		// compare current to db
		List<String> add = ui.stream().filter(obj -> !db.contains(obj)).collect(Collectors.toList());
		add.forEach(arg0 -> {
			grpMenuRepo.addGrpMenuItem(hpl, 1, menuRepo.searchMenuItem(Integer.valueOf(arg0)).getParentMenuId(),
					Integer.valueOf(arg0));
		});
//		add.forEach(arg0 -> {
//			logger.debug(" add >>> "+ arg0);
//		});
		

		logger.debug("save() hpl={}, deleted={}, add={}",hpl,deleted,add);

	}

}