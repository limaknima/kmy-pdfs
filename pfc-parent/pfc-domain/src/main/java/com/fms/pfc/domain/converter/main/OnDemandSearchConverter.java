package com.fms.pfc.domain.converter.main;

import java.util.List;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

import com.fms.pfc.domain.dto.main.OnDemandSearchDto;
import com.fms.pfc.domain.model.main.OnDemandSearch;

@Component
public class OnDemandSearchConverter {
	public OnDemandSearchDto entityToDto(OnDemandSearch ods) {
		ModelMapper mapper = new ModelMapper();
		OnDemandSearchDto map = mapper.map(ods, OnDemandSearchDto.class);
		return map;
	}

	public List<OnDemandSearchDto> entityToDtoList(List<OnDemandSearch> ods) {
		return ods.stream().map(arg0 -> entityToDto(arg0)).collect(Collectors.toList());
	}

	public OnDemandSearch dtoToEntity(OnDemandSearchDto ods) {
		ModelMapper mapper = new ModelMapper();
		OnDemandSearch map = mapper.map(ods, OnDemandSearch.class);
		return map;
	}

	public List<OnDemandSearch> dtoToEntityList(List<OnDemandSearchDto> ods) {
		return ods.stream().map(arg0 -> dtoToEntity(arg0)).collect(Collectors.toList());
	}
}
