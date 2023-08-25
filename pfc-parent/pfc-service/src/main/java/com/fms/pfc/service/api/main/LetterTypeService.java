package com.fms.pfc.service.api.main;

import java.util.Date;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import javax.transaction.Transactional;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fms.pfc.domain.converter.main.LetterTypeConverter;
import com.fms.pfc.domain.dto.main.LetterTypeDto;
import com.fms.pfc.domain.model.main.LetterType;
import com.fms.pfc.repository.main.api.LetterTypeRepository;

@Service
public class LetterTypeService {

	private LetterTypeRepository ltRepo;
	private LetterTypeConverter ltConverter;

	@Autowired
	public LetterTypeService(LetterTypeRepository ltRepo, LetterTypeConverter ltConverter) {
		super();
		this.ltRepo = ltRepo;
		this.ltConverter = ltConverter;
	}

	public LetterType findById(int id) {
		return ltRepo.findById(id).orElse(null);
	}

	public LetterTypeDto findByName(String ltName) {
		List<LetterTypeDto> allFs = findAll();

		Predicate<LetterTypeDto> nameFilter = arg0 -> (!StringUtils.isEmpty(ltName))
				? (arg0.getLtName().equalsIgnoreCase(ltName))
				: (1 > 0);

		allFs = allFs.stream().filter(nameFilter).collect(Collectors.toList());

		return (null != allFs && !allFs.isEmpty()) ? allFs.get(0) : null;
	}

	public LetterTypeDto findOneById(int id) {
		LetterType fs = findById(id);
		return ltConverter.entityToDto(fs);
	}

	public List<LetterTypeDto> findAll() {
		List<LetterType> allFs = ltRepo.findAll();
		return ltConverter.entityToDtoList(allFs);
	}

	public List<LetterTypeDto> searchByCriteria(String ltName, String ltDesc, String fsNameExp, String fsDescExp) {
		List<LetterType> allFs = ltRepo.searchByCriteria(ltName.trim(), ltDesc.trim(), fsNameExp, fsDescExp);
		return ltConverter.entityToDtoList(allFs);
	}

	@Transactional
	public void saveLetterType(Integer ltId, String ltName, String ltDesc, String userId) {
		LetterTypeDto dto = new LetterTypeDto();
		dto.setLtName(ltName);
		dto.setLtDesc(ltDesc);

		if (null == ltId) {
			dto.setCreatorId(userId);
			dto.setCreatedDate(new Date());
		} else {
			LetterTypeDto existing = findOneById(ltId);
			dto.setLtId(ltId);
			dto.setModifierId(userId);
			dto.setModifiedDatetime(new Date());
			dto.setCreatorId(existing.getCreatorId());
			dto.setCreatedDate(existing.getCreatedDate());
		}

		LetterType fs = ltConverter.dtoToEntity(dto);
		ltRepo.saveAndFlush(fs);
	}

	public void deleteById(int id) {
		ltRepo.deleteById(id);
	}
}
