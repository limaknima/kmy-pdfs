package com.fms.pfc.service.api.main;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import javax.transaction.Transactional;

import org.apache.commons.collections4.map.HashedMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fms.pfc.domain.converter.main.LetterContConfConverter;
import com.fms.pfc.domain.converter.main.LetterContConfPRConverter;
import com.fms.pfc.domain.dto.CountryDto;
import com.fms.pfc.domain.dto.main.LetterContConfDto;
import com.fms.pfc.domain.dto.main.LetterContConfPRDto;
import com.fms.pfc.domain.dto.main.LetterTypeDto;
import com.fms.pfc.domain.model.main.LetterContConf;
import com.fms.pfc.domain.model.main.LetterContConfPR;
import com.fms.pfc.domain.model.main.ProductRecipe;
import com.fms.pfc.repository.main.api.LetterContConfPRRepository;
import com.fms.pfc.repository.main.api.LetterContConfRepository;
import com.fms.pfc.repository.main.api.ProductRecipeRepository;
import com.fms.pfc.service.api.base.CountryService;

@Service
public class LetterContConfService {
	private LetterContConfRepository ltRepo;
	private LetterContConfPRRepository ltPrRepo;
	private ProductRecipeRepository prodRecipeRepo;

	private LetterContConfConverter ltConverter;
	private LetterContConfPRConverter ltPrConverter;

	private LetterContConfPRService ltPrServ;
	private CountryService countrySrv;
	private LetterTypeService lTypeServ;
	
	private static final String GENERIC_TYPE = "Generic"; 
	private static final String ALL = "ALL"; 

	@Autowired
	public LetterContConfService(LetterContConfRepository ltRepo, LetterContConfConverter ltConverter,
			LetterContConfPRConverter ltPrConverter, LetterContConfPRRepository ltPrRepo,
			LetterContConfPRService ltPrServ, ProductRecipeRepository prodRecipeRepo, CountryService countrySrv,
			LetterTypeService lTypeServ) {
		super();
		this.ltRepo = ltRepo;
		this.ltConverter = ltConverter;
		this.ltPrConverter = ltPrConverter;
		this.ltPrRepo = ltPrRepo;
		this.ltPrServ = ltPrServ;
		this.prodRecipeRepo = prodRecipeRepo;
		this.countrySrv = countrySrv;
		this.lTypeServ = lTypeServ;
	}

	public LetterContConf findById(int id) {
		return ltRepo.findById(id).orElse(null);
	}

	public LetterContConfDto findByLetterType(Integer ltId) {
		List<LetterContConfDto> allFs = findAll();

		Predicate<LetterContConfDto> ltypeFilter = arg0 -> (ltId != null && ltId != 0) ? (arg0.getLtId() == ltId)
				: (1 > 0);

		allFs = allFs.stream().filter(ltypeFilter).collect(Collectors.toList());

		if (null != allFs && !allFs.isEmpty()) {

			LetterContConfDto dto = allFs.get(0);
			setDto(dto);

			return dto;
		}

		return null;
	}

	public LetterContConfDto findOneById(int id) {
		LetterContConf fs = findById(id);
		LetterContConfDto dto = ltConverter.entityToDto(fs);
		setDto(dto);
		return dto;
	}

	public List<LetterContConfDto> findAll() {
		List<LetterContConf> allFs = ltRepo.findAll();
		List<LetterContConfDto> result = ltConverter.entityToDtoList(allFs);
		setDtoForEach(result);
		return result;
	}
	
	public List<LetterContConfDto> findAll2() {
		List<LetterContConf> allFs = ltRepo.findAll();
		List<LetterContConfDto> result = ltConverter.entityToDtoList(allFs);
		setDtoForEach2(result);
		return result;
	}

	public List<LetterContConfDto> searchByCriteria(Integer ltId, String prName, String prNameExp) {

		// get list of prId from prName
		List<Integer> prIds = prodRecipeRepo.searchProductRecipe(prName, prNameExp, "", "", 0, 0).stream()
				.map(ProductRecipe::getPrId).collect(Collectors.toList());

		// get list of confid from child based on prid
		List<Integer> confIds = ltPrServ.findAll().stream().filter(arg0 -> prIds.contains(arg0.getPrId()))
				.map(LetterContConfPRDto::getLtConfId).collect(Collectors.toList());

		// get list of conf based on confid
		List<LetterContConfDto> dtos = findAll().stream().filter(arg0 -> confIds.contains(arg0.getLtConfId()))
				.collect(Collectors.toList());

		// construct main search result
		// filter by letter type if any
		Predicate<LetterContConfDto> ltypeFilter = arg0 -> (ltId != null && ltId != 0) ? (arg0.getLtId() == ltId)
				: (1 > 0);
		List<LetterContConfDto> result = dtos.stream().filter(ltypeFilter).collect(Collectors.toList());
		setDtoForEach(result);

		return result;
	}

	@Transactional
	public void saveLetterConf(Integer ltConfId, Integer ltId, String desc1, String desc2, String desc3,
			String countryId, String userId, List<Integer> prIds) {
		LetterContConfDto dto = new LetterContConfDto();
		dto.setLtId(ltId);
		dto.setSec1Desc(desc1);
		dto.setSec2Desc(desc2);
		dto.setSec3Desc(desc3);
		dto.setCountryId(countryId);

		if (null == ltConfId) {
			dto.setCreatorId(userId);
			dto.setCreatedDate(new Date());
		} else {
			LetterContConfDto existing = findOneById(ltConfId);
			dto.setLtConfId(ltConfId);
			dto.setModifierId(userId);
			dto.setModifiedDatetime(new Date());
			dto.setCreatorId(existing.getCreatorId());
			dto.setCreatedDate(existing.getCreatedDate());
		}

		LetterContConf fs = ltConverter.dtoToEntity(dto);
		ltRepo.saveAndFlush(fs);

		// Save child table LETTER_CONF_PR
		{
			
			if(!prIds.isEmpty()) {
				ltPrRepo.delLetterContConfPRByParent(fs.getLtConfId());
			}
			
			for (Integer idx : prIds) {
				LetterContConfPRDto child = new LetterContConfPRDto();
				child.setLtConfId(fs.getLtConfId());
				child.setPrId(idx);

				LetterContConfPR cpr = ltPrConverter.dtoToEntity(child);
				ltPrRepo.saveAndFlush(cpr);
			}

		}
	}

	public void deleteById(int id) {
		ltRepo.deleteById(id);
	}

	public List<CountryDto> getRefCountryList() {
		return countrySrv.findByCriteria("", "", "", "", "Y", "");
	}

	public List<LetterTypeDto> getLetterTypeList() {
		return lTypeServ.findAll();
	}

	public List<ProductRecipe> getPrList() {
		return prodRecipeRepo.searchProductRecipe("", "", "", "", 0, 0);
	}

	public List<ProductRecipe> getPrSelectedList(Integer ltConfId) {
		List<LetterContConfPRDto> confPr = findChildByParent(ltConfId);
		List<Integer> ids = confPr.stream().map(LetterContConfPRDto::getPrId).collect(Collectors.toList());
		List<ProductRecipe> prList = prodRecipeRepo.searchProductRecipe("", "", "", "", 0, 0);
		prList = prList.stream().filter(arg0 -> ids.contains(arg0.getPrId())).collect(Collectors.toList());

		return prList;
	}

	private List<LetterContConfPRDto> findChildByParent(Integer ltConfId) {
		List<LetterContConfPRDto> allFs = ltPrServ.findAll();
		Predicate<LetterContConfPRDto> ltypeFilter = arg0 -> (ltConfId != null && ltConfId != 0)
				? (arg0.getLtConfId() == ltConfId)
				: (1 > 0);

		allFs = allFs.stream().filter(ltypeFilter).collect(Collectors.toList());

		return allFs;
	}

	private String generateProductNameList(LetterContConfDto confDto) {
		
		String t = lTypeServ.findOneById(confDto.getLtId()).getLtName();
		if (t.equals(GENERIC_TYPE)) {
			return ALL;
		}
		
		List<LetterContConfPRDto> confPr = findChildByParent(confDto.getLtConfId());
		confPr.forEach(
				arg0 -> arg0.setPrName(prodRecipeRepo.searchProductName(arg0.getPrId(), "3").get(0).getPrName()));

		List<String> prNames = confPr.stream().map(LetterContConfPRDto::getPrName).collect(Collectors.toList());

		return (prNames == null || prNames.isEmpty()) ? "" : String.join("<br />", prNames);
	}
	
	private Map<String, String> generateProductMap(LetterContConfDto confDto) {
		Map<String, String> prodMap = new HashedMap<>();
		{
			String t = lTypeServ.findOneById(confDto.getLtId()).getLtName();
			if (t.equals(GENERIC_TYPE)) {
				prodMap.put("allPrNames", ALL);
				prodMap.put("allPrIds", ALL);
				return prodMap;
			}
		}
		List<LetterContConfPRDto> confPr = findChildByParent(confDto.getLtConfId());
		confPr.forEach(
				arg0 -> arg0.setPrName(prodRecipeRepo.searchProductName(arg0.getPrId(), "3").get(0).getPrName()));

		List<String> prNames = confPr.stream().map(LetterContConfPRDto::getPrName).collect(Collectors.toList());
		String allPrNames = (prNames == null || prNames.isEmpty()) ? "" : String.join("<br />", prNames);
		prodMap.put("allPrNames", allPrNames);

		List<String> prIds = confPr.stream().map(LetterContConfPRDto::getPrId).map(String::valueOf)
				.collect(Collectors.toList());
		String allPrIds = (prIds == null || prIds.isEmpty()) ? "" : String.join(",", prIds);
		prodMap.put("allPrIds", allPrIds);

		return prodMap;
	}

	protected void setDtoForEach(List<LetterContConfDto> result) {
		result.forEach(
				confDto -> confDto.setCountryName(getCountryNames(confDto)));
		result.forEach(confDto -> confDto.setLetterTypeName(lTypeServ.findOneById(confDto.getLtId()).getLtName()));
		result.forEach(confDto -> confDto.setProdNameList(generateProductNameList(confDto)));
	}

	protected String getCountryNames(LetterContConfDto confDto) {
		
		String t = lTypeServ.findOneById(confDto.getLtId()).getLtName();
		if (t.equals(GENERIC_TYPE)) {
			return ALL;
		}
		
		return countrySrv.findOneById(confDto.getCountryId()).getCountryName();
	}
	
	protected void setDtoForEach2(List<LetterContConfDto> result) {
		result.forEach(
				confDto -> confDto.setCountryName(getCountryNames(confDto)));
		result.forEach(confDto -> confDto.setLetterTypeName(lTypeServ.findOneById(confDto.getLtId()).getLtName()));
		result.forEach(confDto -> confDto.setProdNameList(generateProductMap(confDto).get("allPrNames")));
		result.forEach(confDto -> confDto.setProdIdList(generateProductMap(confDto).get("allPrIds")));
	}

	protected void setDto(LetterContConfDto dto) {
		dto.setCountryName(getCountryNames(dto));
		dto.setLetterTypeName(lTypeServ.findOneById(dto.getLtId()).getLtName());
		dto.setProdNameList(generateProductNameList(dto));
	}
	
	public LetterTypeDto findLetterType(int id) {
		return lTypeServ.findOneById(id);
	}
}
