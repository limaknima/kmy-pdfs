package com.fms.pfc.domain.converter.main;

import java.util.List;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

import com.fms.pfc.domain.dto.main.LetterContConfDto;
import com.fms.pfc.domain.model.main.LetterContConf;

@Component
public class LetterContConfConverter {
	
	public LetterContConfDto entityToDto(LetterContConf ltConf) {
		ModelMapper mapper = new ModelMapper();
		LetterContConfDto map = mapper.map(ltConf, LetterContConfDto.class);
		return map;
	}

	public List<LetterContConfDto> entityToDtoList(List<LetterContConf> ltConf) {
		return ltConf.stream().map(arg0 -> entityToDto(arg0)).collect(Collectors.toList());
	}

	public LetterContConf dtoToEntity(LetterContConfDto ltConf) {
		ModelMapper mapper = new ModelMapper();
		LetterContConf map = mapper.map(ltConf, LetterContConf.class);
		return map;
	}

	public List<LetterContConf> dtoToEntityList(List<LetterContConfDto> ltConf) {
		return ltConf.stream().map(arg0 -> dtoToEntity(arg0)).collect(Collectors.toList());
	}
}
