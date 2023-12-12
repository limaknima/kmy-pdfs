package com.fms.pfc.service.api.base;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fms.pfc.domain.model.Usr;
import com.fms.pfc.repository.base.api.LoginRepository;

@Service
public class LoginService {

	private LoginRepository loginRepository;

	@Autowired
	public LoginService(LoginRepository loginRepository) {
		super();
		this.loginRepository = loginRepository;
	}

	public Usr searchUser(String userId) {
		return loginRepository.searchUser(userId);
	}

	public Usr searchUserByFlag(String userId, String disabledFlag) {
		Usr usr = searchAllUser().stream()
				.filter(arg -> (arg.getUserId().equals(userId) && arg.getDisabledFlag().equals(disabledFlag)))
				.findFirst().orElse(null);
		return usr;
	}

	private List<Usr> searchAllUser() {
		return loginRepository.searchAllUser();
	}

	public List<Usr> searchAllActiveUser() {
		return searchAllUser().stream().filter(arg -> arg.getDisabledFlag().equals("N")).collect(Collectors.toList());
	}

	public List<Usr> searchGrpUser(int authLvl) {
		return loginRepository.searchGrpUser(authLvl).stream().filter(arg -> arg.getDisabledFlag().equals("N"))
				.collect(Collectors.toList());
	}

	public void updateUser(String userId, String user_name, String email, String effecDateFr, String effecDateTo,
			int alert, String loggedUser) {
		loginRepository.updateUser(userId, user_name, email, effecDateFr, effecDateTo, alert, loggedUser);
	}

	public void updateUserPass(String userId, String newPassword) {
		loginRepository.updateUserPass(userId, newPassword);
	}

	public void updateFailAttempt(int attempts, String userId) {
		loginRepository.updateFailAttempt(attempts, userId);
	}

	public void increaseFailedAttempts(Usr user) {
		int newFailAttempts = user.getFailedAttemptCnt() + 1;
		loginRepository.updateFailAttempt(newFailAttempts, user.getUserId());
	}

	public void resetFailedAttempts(String userId) {
		loginRepository.resetFailAttempt(userId);
	}

	public void lockUser(Usr user, int ret) {
		loginRepository.lockUser(user.getUserId(), ret);
	}
	
	public List<Usr> searchUserByRole(String roleId) {
		return loginRepository.searchUserByRole(roleId);
	}

}