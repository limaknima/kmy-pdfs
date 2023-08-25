package com.fms.pfc.service.api.base;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.transaction.Transactional;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fms.pfc.domain.converter.ManufacturerConverter;
import com.fms.pfc.domain.dto.LabelAndValueDto;
import com.fms.pfc.domain.dto.ManufacturerDto;
import com.fms.pfc.domain.model.Manufacturer;
import com.fms.pfc.exception.CommonException;
import com.fms.pfc.repository.base.api.ManufacturerRepository;

@Service
public class ManufacturerService {

	private ManufacturerRepository manfRepo;
	private ManufacturerConverter converter;

	@Autowired
	public ManufacturerService(ManufacturerRepository manfRepo, TrxHisService trxHistServ,
			ManufacturerConverter converter) {
		super();
		this.manfRepo = manfRepo;
		this.converter = converter;
	}

	public List<Manufacturer> searchVendor(int vendorType) {
		return manfRepo.searchVendor(vendorType);
	}

	public List<Manufacturer> searchManufacturerList(String vendorName, String vendorNameWildcard, String email,
			String emailWildcard, String vendorType, int vendorId) {
		return manfRepo.searchManufacturerList(vendorName, vendorNameWildcard, email, emailWildcard, vendorType,
				vendorId);
	}

	public void addManufacturer(String vendorName, String vendorType, String email, String address, String street,
			String town, String postcode, String stateId, String countryId, String url, String officeTel, String faxNo,
			String effDateFrom, String effDateTo, String creatorId) {
		manfRepo.addManufacturer(vendorName, vendorType, email, address, street, town, postcode, stateId, countryId,
				url, officeTel, faxNo, effDateFrom, effDateTo, creatorId);
	}

	public void updateManufacturer(String vendorName, String vendorType, String email, String officeTel, String faxNo,
			String address, String town, String postcode, String stateId, String countryId, String url,
			String effDateFrom, String effDateTo, String modifierId, int vendorId) {
		manfRepo.updateManufacturer(vendorName, vendorType, email, officeTel, faxNo, address, town, postcode, stateId,
				countryId, url, effDateFrom, effDateTo, modifierId, vendorId);
	}

	public void deleteManufacturerBatch(String vendorId) {
		manfRepo.deleteManufacturerBatch(vendorId);
	}

	// Validate vendorId usage. return 1 (in use) 0 (not in use)
	public String validateVendor(int vendorId) {
		return manfRepo.validateVendor(vendorId);
	}

	////////////////////

	public Manufacturer findById(int id) {
		return manfRepo.findById(id).orElse(null);
	}

	public ManufacturerDto findOneById(int id) {
		Manufacturer manf = findById(id);
		return converter.entityToDto(manf);
	}

	public List<ManufacturerDto> findAll() {
		List<Manufacturer> all = manfRepo.findAll();
		return converter.entityToDtoList(all);
	}
	
	public List<ManufacturerDto> findByCriteria(String vendorName, String vendorNameWildcard, String email,
			String emailWildcard, String vendorType, int vendorId) {
		List<Manufacturer> all =  manfRepo.searchManufacturerList(vendorName, vendorNameWildcard, email, emailWildcard, vendorType, vendorId);
		return converter.entityToDtoList(all);
	}
	
	public List<ManufacturerDto> findByType(int vendorType){
		List<Manufacturer> all = manfRepo.searchVendor(vendorType);
		return converter.entityToDtoList(all);
	}

	public void deleteById(int id) {
		manfRepo.deleteById(id);
	}

	@Transactional
	public int saveManufacturer(ManufacturerDto manfDto, String userId) {
		if (null == manfDto)
			throw new CommonException("Manufacturer is null!", "MANUFACTURER:SAVE()");

		if (manfDto.getVendorId() == 0) {
			manfDto.setCreatorId(userId);
			manfDto.setCreatedDateTime(new Date());
		} else {
			Manufacturer existing = findById(manfDto.getVendorId());
			manfDto.setCreatorId(existing.getCreatorId());
			manfDto.setCreatedDateTime(existing.getCreatedDateTime());
			manfDto.setModifierId(userId);
			manfDto.setModifiedDateTime(new Date());
		}

		Manufacturer manf = converter.dtoToEntity(manfDto);
		manfRepo.saveAndFlush(manf);

		return manf.getVendorId();
	}
	
	public List<LabelAndValueDto> getVendorNameLabelAndValue(String term, int type){
		List<LabelAndValueDto> suggestions = new ArrayList<LabelAndValueDto>();
		List<ManufacturerDto> manfs = this.findByType(type);
		for (ManufacturerDto manfDto : manfs) {
			if (StringUtils.contains(manfDto.getVendorName().toLowerCase(), term.toLowerCase())) {
				LabelAndValueDto lblVal = new LabelAndValueDto(manfDto.getVendorName(), manfDto.getVendorId());
				suggestions.add(lblVal);
			}
		}
		
		return suggestions;
	}

}
