package com.fms.pfc.service.api.main;

import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fms.pfc.domain.model.main.RmIngredient;
import com.fms.pfc.repository.main.api.RmIngRepository;

@Service
public class RmIngService {

	@Autowired
	private RmIngRepository rmIngRepo;

	public List<RmIngredient> searchRmIng(int rawMatlId) {
		return rmIngRepo.searchRmIng(rawMatlId);
	}

	public void addRmIngredient(String matlName, int manfId, String ingName, String compPrec, String insNo, String eNo,
			String femaNo, String jecfaNo, String casNo, String creatorId, Date date) {
		rmIngRepo.addRmIngredient(matlName, manfId, ingName, compPrec, insNo, eNo, femaNo, jecfaNo, casNo, creatorId,
				date);
	}

	public void delRmIngredient(int rawMatlId) {
		rmIngRepo.delRmIngredient(rawMatlId);
	}

	public void delDetailRmIngredient(int rawMatlId, int rmIngId) {
		rmIngRepo.delDetailRmIngredient(rawMatlId, rmIngId);
	}

	public void delRmIngByManf(int rawMatlId, int manfId) {
		rmIngRepo.delRmIngByManf(rawMatlId, manfId);
	}

	public void updRmIngredient(int rawMatlId, int manfId, String ingName, String compPrec, String insNo, String eNo,
			String femaNo, String jecfaNo, String casNo, String creatorId, int rmIngId, Date date) {
		rmIngRepo.updRmIngredient(rawMatlId, manfId, ingName, compPrec, insNo, eNo, femaNo, jecfaNo, casNo, creatorId,
				rmIngId, date);
	}

}