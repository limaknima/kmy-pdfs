package com.fms.pfc.domain.converter.main;

import java.util.List;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

import com.fms.pfc.domain.dto.main.LetterTypeDto;
import com.fms.pfc.domain.model.main.LetterType;

@Component
public class LetterTypeConverter {
	
	public LetterTypeDto entityToDto(LetterType letterType) {
		ModelMapper mapper = new ModelMapper();
		LetterTypeDto map = mapper.map(letterType, LetterTypeDto.class);
		return map;
	}

	public List<LetterTypeDto> entityToDtoList(List<LetterType> letterType) {
		return letterType.stream().map(arg0 -> entityToDto(arg0)).collect(Collectors.toList());
	}

	public LetterType dtoToEntity(LetterTypeDto letterType) {
		ModelMapper mapper = new ModelMapper();
		LetterType map = mapper.map(letterType, LetterType.class);
		return map;
	}

	public List<LetterType> dtoToEntityList(List<LetterTypeDto> letterType) {
		return letterType.stream().map(arg0 -> dtoToEntity(arg0)).collect(Collectors.toList());
	}
}