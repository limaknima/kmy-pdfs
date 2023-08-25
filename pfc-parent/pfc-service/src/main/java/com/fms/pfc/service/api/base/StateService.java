package com.fms.pfc.service.api.base;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.fms.pfc.domain.model.State;
import com.fms.pfc.repository.base.api.StateRepository;

@Service
public class StateService {

	@Autowired
	private StateRepository stateRes;
	
	public List<State> searchState(String stateId) {		
		return stateRes.searchState(stateId);
	}
}
