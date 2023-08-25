package com.fms.pfc.service.api.base;

import java.util.Date;
import java.util.List;
import java.util.Optional;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fms.pfc.domain.converter.CountryConverter;
import com.fms.pfc.domain.dto.CountryDto;
import com.fms.pfc.domain.model.Country;
import com.fms.pfc.exception.CommonException;
import com.fms.pfc.repository.base.api.CountryRepository;

@Service
public class CountryService {

	private CountryRepository countryRepo;
	private CountryConverter converter;

	@Autowired
	public CountryService(CountryRepository countryRepo, CountryConverter converter) {
		super();
		this.countryRepo = countryRepo;
		this.converter = converter;
	}

	public List<Country> searchCountry(String conID, String conName, String conIDSelOpt, String conNameSelOpt,
			String evalFlag, String originFlag) {
		return countryRepo.searchCountry(conID, conName, conIDSelOpt, conNameSelOpt, evalFlag, originFlag);

	}

	public List<Country> searchCountry(List<String> ids) {
		return countryRepo.searchCountry(ids);

	}

	// Insert function
	public void addCountry(String conID, String conName, String creator, String evalFlag, String cooFlag) {
		countryRepo.addCountry(conID, conName, creator, evalFlag, cooFlag);

	}	

	// Update function
	public void updateCountry(String conID, String conName, String modifier, String evalFlag, String cooFlag) {
		countryRepo.updateCountry(conID, conName, modifier, evalFlag, cooFlag);
	}

	// Delete function
	public void deleteCountry(String conID) {
		countryRepo.deleteCountry(conID);
	}

	// Validate country code return 1 (in use) 0 (not in use)
	public String vlidateCountry(String conID) {
		return countryRepo.validateCountry(conID);
	}	
	
	//////////
	
	public List<CountryDto> findAll() {
		List<Country> allCountry = countryRepo.findAll();
		return converter.entityToDtoList(allCountry);
	}

	public List<CountryDto> findByCriteria(String conID, String conName, String conIDSelOpt, String conNameSelOpt,
			String evalFlag, String originFlag) {
		List<Country> countries = countryRepo.searchCountry(conID, conName, conIDSelOpt, conNameSelOpt, evalFlag,
				originFlag);
		return converter.entityToDtoList(countries);
	}

	public CountryDto findOneById(String id) {
		Country country = countryRepo.findById(id).orElse(null);
		return converter.entityToDto(country);
	}
	
	@Transactional
	public void saveCountry (String conID, String conName, String userId, String evalFlag, String cooFlag) {
		CountryDto countryDto = new CountryDto();
		countryDto.setCountryId(conID);
		countryDto.setCountryName(conName);
		countryDto.setEvalFlag(evalFlag);
		countryDto.setOriginFlag(cooFlag);
		
		Country existing = countryRepo.findById(conID).orElse(null);
		if(null == existing) {
			countryDto.setCreateTime(new Date());
			countryDto.setCreator(userId);
		} else {
			countryDto.setCreator(existing.getCreator());
			countryDto.setCreateTime(existing.getCreateTime());
			countryDto.setModifier(userId);
			countryDto.setModifyTime(new Date());
		}
		
		Country country = converter.dtoToEntity(countryDto);
		countryRepo.saveAndFlush(country);
	}

	@Transactional
	public void saveCountry(Country con, String evalFlag, String cooFlag) {
		if (null == con)
			throw new CommonException("Country is null!", "COUNTRY:SAVE()");

		con.setEvalFlag(evalFlag);
		con.setOriginFlag(cooFlag);
		countryRepo.saveAndFlush(con);
	}

	public Country findOneCountry(String id) {
		Optional<Country> c = countryRepo.findById(id);
		return c.get();
	}

}
