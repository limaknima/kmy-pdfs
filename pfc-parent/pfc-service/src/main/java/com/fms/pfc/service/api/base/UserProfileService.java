package com.fms.pfc.service.api.base;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fms.pfc.domain.model.UsrProfile;
import com.fms.pfc.repository.base.api.UserProfileRepository;


@Service
public class UserProfileService {

	private UserProfileRepository usrProfileRepo;
	
	@Autowired
	public UserProfileService(UserProfileRepository usrProfileRepo) {
		super();
		this.usrProfileRepo = usrProfileRepo;
	}

	public int getUserProfileCountByGrp(String groupId) {
		
		return usrProfileRepo.getUserProfileCountByGrp(groupId);		
	}
	
	public int getUserProfileCountByUsr(String usrId) {
		
		return usrProfileRepo.getUserProfileCountByUsr(usrId);		
	}
	
	public List<UsrProfile> searchUserProfile(String orgName, String userID, String userName, 
			String email, String userGp, String para1, String para2, String para3,
			String para4) {
		
		return usrProfileRepo.searchUserProfile(orgName, userID, userName, email, userGp, para1,
				para2, para3, para4);		
	}
	
	//Insert function
	public void addUserProfile(String userId, String userName, String orgId, String grpId, String password,
			  String email, String disabledFlag, int failCount, int alertPre, String creatorId,  
			  String effecDate, String expDate) {
		
		usrProfileRepo.addUserProfile(userId, userName, orgId, grpId, password, email, disabledFlag, failCount,
				alertPre, creatorId, effecDate, expDate);		
	}
	
	//Update function
	//Para1 = selected orgaID
	public void updateUserProfile(String userID, String userName, String orgId, String grpId, String email,
			  int alertPre, String modifierId, String effecDate, String expDate) {
		
		usrProfileRepo.updateUserProfile(userID, userName, orgId, grpId, email, alertPre, modifierId,
				effecDate, expDate);		
	}
	
	//Delete function
	public void deleteUserProfile(String userId) {
		usrProfileRepo.deleteUserProfile(userId);
	}	

	//Reset password
	public void resetPassword(String user_id, String newPassword, String modifier) {
		usrProfileRepo.resetPassword(user_id, newPassword, modifier);		
	}
	
	//Unlock account
	public void lockOrUnlockAccount(String user_id, String modifier, String flag) {

		if (flag.equalsIgnoreCase("N")) {
			usrProfileRepo.unlockAccountOnly(user_id, modifier, flag);
		} else if (flag.equalsIgnoreCase("Y")) {
			usrProfileRepo.lockOrUnlockAccount(user_id, modifier, flag);
		}
	}
	
	public List<UsrProfile> findUsersByGroup(String grpId){
		return usrProfileRepo.findUsersByGroup(grpId);
	}
}
