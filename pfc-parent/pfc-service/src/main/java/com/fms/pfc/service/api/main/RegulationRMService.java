package com.fms.pfc.service.api.main;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fms.pfc.domain.model.main.RegulationRawMaterial;
import com.fms.pfc.repository.main.api.RegulationRMRepository;

@Service
public class RegulationRMService {

	@Autowired
	private RegulationRMRepository regRmRes;
	
	public List<RegulationRawMaterial> searchRawMaterialName() {
		
		return regRmRes.searchRawMaterialName();
		
	}
}
