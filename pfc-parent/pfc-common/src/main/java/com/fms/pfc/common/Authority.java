package com.fms.pfc.common;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fms.pfc.domain.dto.MenuRoleFunctionDto;
import com.fms.pfc.domain.model.GrpMenuItem;
import com.fms.pfc.domain.model.MenuItem;
import com.fms.pfc.domain.model.Usr;
import com.fms.pfc.domain.model.UsrRole;
import com.fms.pfc.service.api.base.GrpMenuService;
import com.fms.pfc.service.api.base.LoginService;
import com.fms.pfc.service.api.base.MenuRoleFunctionService;
import com.fms.pfc.service.api.base.UsrRoleService;

@Service
public class Authority {
	
	private final Logger logger = LoggerFactory.getLogger(Authority.class);
	
	private LoginService logServ;
	private GrpMenuService grpMenuServ;
	private MenuRoleFunctionService mrfServ;
	private UsrRoleService usrRoleServ;

	@Autowired
	public Authority(LoginService logServ, GrpMenuService grpMenuServ, MenuRoleFunctionService mrfServ, UsrRoleService usrRoleServ) {
		super();
		this.logServ = logServ;
		this.grpMenuServ = grpMenuServ;
		this.mrfServ = mrfServ;
		this.usrRoleServ = usrRoleServ;
	}

	public Map<String, Object> onPageLoad(Map<String, Object> model, HttpServletRequest request) {
		
		model = new HashMap<String, Object>();
		menuAccess(model, request);
		model.put("loggedUser", request.getRemoteUser());
		
		return model;
	}
	
	private void menuAccess(Map<String, Object> model, HttpServletRequest request) {
		
		Usr usr = logServ.searchUser(request.getRemoteUser());
		model.put("loggedUserGrp", usr.getGroupId());
		model.put("loggedUserOrg", usr.getOrgId());
		
		List<UsrRole> usrRoleList = usrRoleServ.searchUserRole(request.getRemoteUser());		
		String roleId = usrRoleList.get(0).getRoleId();
		
		List<GrpMenuItem> groupMenu = grpMenuServ.searchMenu(usr.getGroupId(), roleId);
		for (int i=0; i<groupMenu.size(); i ++) {
			String menuId = "M" + String.valueOf(groupMenu.get(i).getMenuItemId());
			model.put(menuId, true);
			menuRoleFunction(model, groupMenu.get(i).getMenuItemId());
		}
		
	}
	
	private void menuRoleFunction(Map<String, Object> model, Integer menuItemId) {
		List<MenuRoleFunctionDto> mrf = mrfServ.searchByCriteria(menuItemId, "", 0, 0, false);
		model.put("MRF"+menuItemId, mrf);
	}
	
	public void isAuthUrl(HttpServletRequest request, HttpServletResponse response) {		
		logger.info("isAuthUrl() URI=" + request.getRequestURI() + "; user=" + request.getRemoteUser()
				+ "; session=" + request.getSession().getId());

		String groupId = logServ.searchUser(request.getRemoteUser()).getGroupId();
		if (!request.getRequestURI().equals("/")) {
			
			String root = "/";
			String uri = request.getRequestURI();
			if(null != request.getContextPath() && !request.getContextPath().isEmpty()) {
				root = request.getContextPath();
				uri = uri.substring(root.length());
			}
			
			MenuItem menuItem = grpMenuServ.checkAuthority(groupId, uri);
			if (menuItem == null) {
				try {
					response.sendRedirect(root);
				} catch (IOException e) {
					e.printStackTrace();
				}
			} 

		}
	}
	
}
