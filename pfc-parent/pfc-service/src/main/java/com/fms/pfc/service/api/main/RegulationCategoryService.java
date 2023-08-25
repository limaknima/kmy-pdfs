package com.fms.pfc.service.api.main;

import java.io.IOException;
import java.util.Date;
import java.util.List;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fms.pfc.domain.converter.main.RegulationCatConverter;
import com.fms.pfc.domain.dto.main.RegulationCatDto;
import com.fms.pfc.domain.model.main.RegulationCategory;
import com.fms.pfc.repository.main.api.RegulationCategoryRepository;

@Service
public class RegulationCategoryService {

	private RegulationCategoryRepository regCatRepo;
	private RegulationCatConverter converter;

	@Autowired
	public RegulationCategoryService(RegulationCategoryRepository regCatRepo, RegulationCatConverter converter) {
		super();
		this.regCatRepo = regCatRepo;
		this.converter = converter;
	}

	// Start Kimberley 20210109
	// Search query based on id
	public RegulationCategory searchCategoryRegulation(String catId) {
		return regCatRepo.searchCategoryRegulation(catId);

	}// End Kimberley 20210109

	public List<RegulationCategory> searchCategoryRegulation(String catName, String catDesc, String exp1, String exp2, String countryId) {
		return regCatRepo.searchCategoryRegulation(catName, catDesc, exp1, exp2, countryId);

	}

	// FSGS) KarVei 27/2/2021 Add - Check if category is used in REGULATION_DOC
	public List<RegulationCategory> checkCategoryExist() {
		return regCatRepo.checkCategoryExist();

	}

	// Insert function
	public void addCategoryRegulation(String catName, String catDesc, String creatorId, String countryList)
			throws IOException {
		regCatRepo.addCategoryRegulation(catName, catDesc, creatorId, countryList);

	}

	// Update function
	public void updateCategoryRegulation(String catName, String catDesc, String creatorId, String countryList) {
		regCatRepo.updateCategoryRegulation(catName, catDesc, creatorId, countryList);
	}

	// Delete function
	public void deleteCategoryRegulation(String catName) {
		regCatRepo.deleteCategoryRegulation(catName);
	}
	
	public List<RegulationCategory> searchCategoryRegulationByCountry(String countryId) {
		return regCatRepo.searchCategoryRegulationByCountry(countryId);
	}
	
	////////////////////
	
	public RegulationCategory findById(int id) {
		return regCatRepo.findById(id).orElse(null);
	}

	public RegulationCatDto findOneById(int id) {
		RegulationCategory regCat = regCatRepo.findById(id).orElse(null);
		return converter.entityToDto(regCat);
	}

	public List<RegulationCatDto> findAll() {
		List<RegulationCategory> all = regCatRepo.findAll();
		return converter.entityToDtoList(all);
	}

	public List<RegulationCatDto> findByCriteria(String catName, String catDesc, String exp1, String exp2,
			String countryId) {
		List<RegulationCategory> all = regCatRepo.searchCategoryRegulation(catName, catDesc, exp1, exp2, countryId);
		return converter.entityToDtoList(all);
	}
	
	@Transactional
	public void saveRegCat(RegulationCatDto dto, String userId, String countryList) {
		RegulationCategory existing = regCatRepo.findById(dto.getRegDocCatId()).orElse(null);
		dto.setCountryList(countryList);
		if(existing == null) {
			dto.setCreatorId(userId);
			dto.setCreatedDate(new Date());
		} else {
			dto.setCreatorId(existing.getCreatorId());
			dto.setCreatedDate(existing.getCreatedDate());
			dto.setModifierId(userId);
			dto.setModifiedDatetime(new Date());
		}
		
		RegulationCategory regCat = converter.dtoToEntity(dto);
		regCatRepo.saveAndFlush(regCat);
	}
}
