package com.fms.pfc.service.api.base;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fms.pfc.domain.model.UsrRole;
import com.fms.pfc.repository.base.api.UsrRoleRepository;

@Service
public class UsrRoleService {

	@Autowired
	private UsrRoleRepository userRoleRes;

	public List<UsrRole> searchUserRole(String userId) {		
		return userRoleRes.searchUserRole(userId);
	}
	
	public void addUserRole(String userId, String roleId) {		
		userRoleRes.addUserRole(userId, roleId);
	}
	
	public void deleteUserRole(String userId) {		
		userRoleRes.deleteUserRole(userId);
	}	

}