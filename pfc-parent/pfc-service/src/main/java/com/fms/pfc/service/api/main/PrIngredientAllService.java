package com.fms.pfc.service.api.main;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fms.pfc.domain.model.main.PrIngredientAll;
import com.fms.pfc.repository.main.api.PrIngredientAllRepository;

@Service
public class PrIngredientAllService {

	private PrIngredientAllRepository prIngAllRepo;

	@Autowired
	public PrIngredientAllService(PrIngredientAllRepository prIngAllRepo) {
		this.prIngAllRepo = prIngAllRepo;
	}

	List<PrIngredientAll> findIngredientsByParentId(int prId) {
		return prIngAllRepo.findIngredientsByParentId(prId);
	}
}
