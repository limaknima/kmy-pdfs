package com.fms.pfc.service.api.main;

import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fms.pfc.domain.model.main.PrRmAdditive;
import com.fms.pfc.repository.main.api.PrRmAdditiveRepository;

@Service
public class PrRmAdditiveService {

	@Autowired
	private PrRmAdditiveRepository prRmAddRepo;

	public List<PrRmAdditive> searchPrRmAdditive(int prId) {
		return prRmAddRepo.searchPrRmAdditive(prId);
	}

	public void addPrRmAdditive(String prCode, int rawMatlId, String additiveDesc, String creatorId, Date date) {
		prRmAddRepo.addPrRmAdditive(prCode, rawMatlId, additiveDesc, creatorId, date);
	}

	public void updPrRmAdditive(int prRmAddtId, int rawMatlId, String additiveDesc, String modifierId, Date date) {
		prRmAddRepo.updPrRmAdditive(prRmAddtId, rawMatlId, additiveDesc, modifierId, date);
	}

	public void delPrRmAdditive(int prId) {
		prRmAddRepo.delPrRmAdditive(prId);
	}

	public void delDetailPrRmAdditive(int prRmAddId) {
		prRmAddRepo.delDetailPrRmAdditive(prRmAddId);
	}

	public void delPrRmAddByMatl(int prId, int rawMatlId) {
		prRmAddRepo.delPrRmAddByMatl(prId, rawMatlId);
	}
}