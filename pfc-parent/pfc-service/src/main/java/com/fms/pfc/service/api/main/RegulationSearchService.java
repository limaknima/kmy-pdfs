package com.fms.pfc.service.api.main;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fms.pfc.domain.model.main.Regulation;
import com.fms.pfc.repository.main.api.RegulationSearchRepository;


@Service
public class RegulationSearchService {

	@Autowired
	private RegulationSearchRepository regRes;
	  
	public List<Regulation> searchRegulation(String country, String group, 
			String category, String file, String isActive, String expr, String rmName) {
		
		return regRes.searchRegulation(country, group, category, file, isActive, expr, rmName);
		
	}
	
}
