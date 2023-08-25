package com.fms.pfc.service.api.base;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fms.pfc.domain.model.PassHistory;
import com.fms.pfc.repository.base.api.PassHisRepository;

@Service
public class PassHisService {

	@Autowired
	private PassHisRepository passHisRep;

	public List<PassHistory> searchHistory(String user_id) {
		return passHisRep.searchHistory(user_id);
	}
	
	public void insertPassHis(String user_id, String oldPass) {
		passHisRep.insertPassHis(user_id, oldPass);
	}

}