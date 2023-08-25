package com.fms.pfc.service.api.main;

import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fms.pfc.domain.converter.main.PrIngredientConverter;
import com.fms.pfc.domain.dto.main.PrIngredientDto;
import com.fms.pfc.domain.model.main.PrIngredient;
import com.fms.pfc.repository.main.api.PrIngRepository;

@Service
public class PrIngService {

	private PrIngRepository prIngRepo;
	private PrIngredientConverter prIngConv;

	@Autowired
	public PrIngService(PrIngRepository prIngRepo, PrIngredientConverter prIngConv) {
		super();
		this.prIngRepo = prIngRepo;
		this.prIngConv = prIngConv;
	}

	public List<PrIngredient> searchPrIngredient(int prId, int rawMatlRefId, int prRefId) {
		return prIngRepo.searchPrIngredient(prId, rawMatlRefId, prRefId);
	}

	public List<PrIngredient> searchPrIngredient(int prId, int rawMatlRefId, int prRefId, List<Integer> rmIds,
			List<Integer> prIds) {
		return prIngRepo.searchPrIngredient(prId, rawMatlRefId, prRefId, rmIds, prIds);
	}

	public void addPrIngredient(String prCode, int parentRefId, int refId, int refType, double ingPerc, String selTsNo,
			int seqNo, String additiveDesc, String creatorId, Date date, String altRm) {
		prIngRepo.addPrIngredient(prCode, parentRefId, refId, refType, ingPerc, selTsNo, seqNo, additiveDesc, creatorId,
				date, altRm);
	}

	public void delPrIngredient(int prId) {
		prIngRepo.delPrIngredient(prId);
	}

	public void delDetailPrIngredient(int prIngId) {
		prIngRepo.delDetailPrIngredient(prIngId);
	}

	public void updPrIngredient(int prIngId, int refId, int refType, double ingPrec, String selTsNo, String modifierId,
			Date date, String altRm) {
		prIngRepo.updPrIngredient(prIngId, refId, refType, ingPrec, selTsNo, modifierId, date, altRm);
	}

	public List<PrIngredientDto> searchPrIngredientDto(int prId, int rawMatlRefId, int prRefId) {
		List<PrIngredient> entityList = searchPrIngredient(prId, rawMatlRefId, prRefId);
		return prIngConv.entityToDtoList(entityList);
	}
}