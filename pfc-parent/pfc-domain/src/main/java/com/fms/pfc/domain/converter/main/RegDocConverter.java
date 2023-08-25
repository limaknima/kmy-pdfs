package com.fms.pfc.domain.converter.main;

import java.util.List;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

import com.fms.pfc.domain.dto.main.RegDocDto;
import com.fms.pfc.domain.model.main.RegulationDoc;

@Component
public class RegDocConverter {

	public RegDocDto entityToDto(RegulationDoc reg) {
		ModelMapper mapper = new ModelMapper();
		RegDocDto map = mapper.map(reg, RegDocDto.class);
		return map;
	}

	public List<RegDocDto> entityToDtoList(List<RegulationDoc> reg) {
		return reg.stream().map(arg0 -> entityToDto(arg0)).collect(Collectors.toList());
	}

	public RegulationDoc dtoToEntity(RegDocDto reg) {
		ModelMapper mapper = new ModelMapper();
		RegulationDoc map = mapper.map(reg, RegulationDoc.class);
		return map;
	}

	public List<RegulationDoc> dtoToEntityList(List<RegDocDto> reg) {
		return reg.stream().map(arg0 -> dtoToEntity(arg0)).collect(Collectors.toList());
	}
}
