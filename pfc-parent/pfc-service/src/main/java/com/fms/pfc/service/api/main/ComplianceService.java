package com.fms.pfc.service.api.main;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fms.pfc.domain.model.main.Compliance;
import com.fms.pfc.domain.model.main.PrStat;
import com.fms.pfc.repository.main.api.ComplianceRepository;
import com.fms.pfc.repository.main.api.ProductStatusRepository;

@Service
public class ComplianceService {

	@Autowired
	private ComplianceRepository cRepo;
	
	public List<Compliance> searchComplianceCountry(List<Integer> prId,List<String> countryID){
		
		return cRepo.searchComplianceCountry(prId, countryID);
	}
}
