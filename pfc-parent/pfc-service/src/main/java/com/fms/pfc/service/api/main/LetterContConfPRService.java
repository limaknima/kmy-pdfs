package com.fms.pfc.service.api.main;

import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fms.pfc.domain.converter.main.LetterContConfPRConverter;
import com.fms.pfc.domain.dto.main.LetterContConfPRDto;
import com.fms.pfc.domain.model.main.LetterContConfPR;
import com.fms.pfc.repository.main.api.LetterContConfPRRepository;

@Service
public class LetterContConfPRService {
	private LetterContConfPRRepository ltPrRepo;
	private LetterContConfPRConverter ltPrConverter;
	
	@Autowired
	public LetterContConfPRService(LetterContConfPRRepository ltPrRepo, LetterContConfPRConverter ltPrConverter) {
		super();
		this.ltPrRepo = ltPrRepo;
		this.ltPrConverter = ltPrConverter;
	}
	
	public LetterContConfPR findById(int id) {
		return ltPrRepo.findById(id).orElse(null);
	}

	public LetterContConfPRDto findByLetterConf(Integer ltConfId) {
		List<LetterContConfPRDto> allFs = findAll();

		Predicate<LetterContConfPRDto> ltypeFilter = arg0 -> (ltConfId != null && ltConfId != 0) ? (arg0.getLtConfId() == ltConfId)
				: (1 > 0);

		allFs = allFs.stream().filter(ltypeFilter).collect(Collectors.toList());

		return (null != allFs && !allFs.isEmpty()) ? allFs.get(0) : null;
	}

	public LetterContConfPRDto findOneById(int id) {
		LetterContConfPR fs = findById(id);
		return ltPrConverter.entityToDto(fs);
	}

	public List<LetterContConfPRDto> findAll() {
		List<LetterContConfPR> allFs = ltPrRepo.findAll();
		return ltPrConverter.entityToDtoList(allFs);
	}

	@Transactional
	public void saveLetterConfPR(Integer ltConfPrId, Integer ltConfId, Integer prId) {
		LetterContConfPRDto dto = new LetterContConfPRDto();
		dto.setLtConfPrId(ltConfPrId);
		dto.setLtConfId(ltConfId);
		dto.setPrId(prId);

		LetterContConfPR fs = ltPrConverter.dtoToEntity(dto);
		ltPrRepo.saveAndFlush(fs);

	}

	public void deleteById(int id) {
		ltPrRepo.deleteById(id);
	}
	
	
}
