package com.fms.pfc.domain.converter.main;

import java.util.List;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

import com.fms.pfc.domain.dto.main.LetterContConfPRDto;
import com.fms.pfc.domain.model.main.LetterContConfPR;

@Component
public class LetterContConfPRConverter {
	
	public LetterContConfPRDto entityToDto(LetterContConfPR ltConf) {
		ModelMapper mapper = new ModelMapper();
		LetterContConfPRDto map = mapper.map(ltConf, LetterContConfPRDto.class);
		return map;
	}

	public List<LetterContConfPRDto> entityToDtoList(List<LetterContConfPR> ltConf) {
		return ltConf.stream().map(arg0 -> entityToDto(arg0)).collect(Collectors.toList());
	}

	public LetterContConfPR dtoToEntity(LetterContConfPRDto ltConf) {
		ModelMapper mapper = new ModelMapper();
		LetterContConfPR map = mapper.map(ltConf, LetterContConfPR.class);
		return map;
	}

	public List<LetterContConfPR> dtoToEntityList(List<LetterContConfPRDto> ltConf) {
		return ltConf.stream().map(arg0 -> dtoToEntity(arg0)).collect(Collectors.toList());
	}
}
