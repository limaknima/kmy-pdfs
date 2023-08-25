package com.fms.pfc.service.api.base;

import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fms.pfc.domain.converter.RoleConverter;
import com.fms.pfc.domain.dto.RoleDto;
import com.fms.pfc.domain.model.Role;
import com.fms.pfc.repository.base.api.RoleRepository;


@Service
public class RoleService {

	private RoleRepository roleRepo;
	private RoleConverter converter;

	@Autowired
	public RoleService(RoleRepository roleRepo, RoleConverter converter) {
		super();
		this.roleRepo = roleRepo;
		this.converter = converter;
	}

	public List<Role> searchUserRole(String roleId, String roleIdWildcard, String roleName, String roleNameWildcard) {		
		return roleRepo.searchUserRole(roleId, roleIdWildcard, roleName, roleNameWildcard);
	}
	
	public List<Role> searchListBoxUserRole(String userId) {		
		return roleRepo.searchListBoxUserRole(userId);
	};	
	
	public void addUserRole(String roleId, String roleName, String roleDesc, String creatorId) {		
		roleRepo.addUserRole(roleId, roleName, roleDesc, creatorId);
	}
	
	public void updateUserRole(String roleName, String roleDesc, String modifierId, String roleId) {		
		roleRepo.updateUserRole(roleName, roleDesc, modifierId, roleId);
	}	
	
	public void deleteUserRoleBatch(String roleId) {		
		roleRepo.deleteUserRoleBatch(roleId);
	}
	
	////////////////////
	
	public Role findById(String id) {
		return roleRepo.findById(id).orElse(null);
	}
	
	public RoleDto findOneById(String id) {
		Role role = roleRepo.findById(id).orElse(null);
		return converter.entityToDto(role);
	}
	
	public List<RoleDto> findAll(){
		List<Role> all = roleRepo.findAll();
		return converter.entityToDtoList(all);
	}
	
	public List<RoleDto> findByCriteria(String roleId, String roleIdWildcard, String roleName, String roleNameWildcard){
		List<Role> all = roleRepo.searchUserRole(roleId, roleIdWildcard, roleName, roleNameWildcard);
		return converter.entityToDtoList(all);
	}
	
	public void deleteById (String id) {
		roleRepo.deleteById(id);
	}
	
	public void saveRole(RoleDto dto, String userId) {
		Role existing = roleRepo.findById(dto.getRoleId()).orElse(null);
		dto.setSystemUseFlag("Y");
		if(existing == null) {
			dto.setCreatorId(userId);
			dto.setCreatedDateTime(new Date());
		} else {
			dto.setCreatorId(existing.getCreatorId());
			dto.setCreatedDateTime(existing.getCreatedDateTime());
			dto.setModifierId(userId);
			dto.setModifiedDateTime(new Date());
		}
		
		Role role = converter.dtoToEntity(dto);
		roleRepo.saveAndFlush(role);
	}
	
}
