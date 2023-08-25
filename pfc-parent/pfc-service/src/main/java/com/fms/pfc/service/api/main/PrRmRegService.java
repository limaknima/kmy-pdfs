package com.fms.pfc.service.api.main;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fms.pfc.domain.model.main.PrRmReg;
import com.fms.pfc.repository.main.api.PrRmRegRepository;

@Service
public class PrRmRegService {

	@Autowired
	private PrRmRegRepository prRmRegRepo;

	public List<PrRmReg> searchPrRmReg(int prId) {
		return prRmRegRepo.searchPrRmReg(prId);
	}

	public void addPrRmReg(String prCode, int rawMatlId, String countryId, int regDocId) {
		prRmRegRepo.addPrRmReg(prCode, rawMatlId, countryId, regDocId);
	}

	public void delPrRmReg(int prId) {
		prRmRegRepo.delPrRmReg(prId);
	}
}
