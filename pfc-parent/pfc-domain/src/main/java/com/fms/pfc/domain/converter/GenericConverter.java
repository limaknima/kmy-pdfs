package com.fms.pfc.domain.converter;

import org.modelmapper.ModelMapper;

import com.fms.pfc.domain.dto.GenericDto;

public class GenericConverter<T> {

	public GenericDto<T> entitiToDto(T t) {
		ModelMapper mapper = new ModelMapper();
		GenericDto<T> dto = mapper.map(t, GenericDto.class);
		return dto;
	}

}
