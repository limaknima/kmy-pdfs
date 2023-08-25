package com.fms.pfc.service.api.base;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fms.pfc.domain.model.UsrActivityLog;
import com.fms.pfc.repository.base.api.UsrActivityRepository;

@Service
public class UsrActivityService {

	@Autowired
	private UsrActivityRepository usrActRepo;

	public List<UsrActivityLog> searchActivityLog(String userSession, String userId, String orgId, String dateFr,
			String dateTo) {
		return usrActRepo.searchActivityLog(userSession, userId, orgId, dateFr, dateTo);
	}

	public void insertUsrActivityLog(String user_id, String successFlag, String timeoutFlag, String sessionId) {
		usrActRepo.insertUsrActivityLog(user_id, successFlag, timeoutFlag, sessionId);
	}

	public void updateUsrActivityLog(String sessionId) {
		usrActRepo.updateUsrActivityLog(sessionId);
	}

}