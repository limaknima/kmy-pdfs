package com.fms.pfc.service.api.base;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fms.pfc.domain.model.GrpMenuItem;
import com.fms.pfc.domain.model.MenuItem;
import com.fms.pfc.repository.base.api.GrpMenuRepository;
import com.fms.pfc.repository.base.api.MenuRepository;

@Service
public class GrpMenuService {

	@Autowired
	private GrpMenuRepository grpMenuRepo;
	
	@Autowired
	private MenuRepository menuRepo;

	public List<GrpMenuItem> searchMenu(String groupId) {
		return grpMenuRepo.searchMenu(groupId);
	}
	
	public MenuItem checkAuthority(String groupId, String url) {
		return menuRepo.checkAuthority(groupId, url);
	}

}