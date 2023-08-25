package com.fms.pfc.service.api.base;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.fms.pfc.domain.model.Group;
import com.fms.pfc.repository.base.api.GroupRepository;

@Service
public class GroupService {

	private GroupRepository grpRepo;

	@Autowired
	public GroupService(GroupRepository grpRepo) {
		super();
		this.grpRepo = grpRepo;
	}

	public List<Group> searchGroup(String orgId, String groupId) {		
		return grpRepo.searchGroup(orgId, groupId);
	}
	
	public List<Group> searchGroupList(String orgName, String orgNameWildcard, String groupId, String groupIdWildcard, String groupName, String groupNameWildcard) {		
		return grpRepo.searchGroupList(orgName, orgNameWildcard, groupId, groupIdWildcard, groupName, groupNameWildcard);
	}
	
	public void addGroup(String orgId, String groupId, String groupName, String description, String creatorId) {		
		grpRepo.addGroup(orgId, groupId, groupName, description, creatorId);
	}
	
	public void updateGroup(String groupName, String description, String modifierId, String orgId, String groupId) {		
		grpRepo.updateGroup(groupName, description, modifierId, orgId, groupId);
	}
	
	public void deleteGroupBatch(String orgId, String groupId) {		
		grpRepo.deleteGroupBatch(orgId, groupId);
	}
}
