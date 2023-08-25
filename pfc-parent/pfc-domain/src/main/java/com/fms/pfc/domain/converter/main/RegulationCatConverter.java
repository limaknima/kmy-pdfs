package com.fms.pfc.domain.converter.main;

import java.util.List;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

import com.fms.pfc.domain.dto.main.RegulationCatDto;
import com.fms.pfc.domain.model.main.RegulationCategory;

@Component
public class RegulationCatConverter {

	public RegulationCatDto entityToDto(RegulationCategory regCat) {
		ModelMapper mapper = new ModelMapper();
		RegulationCatDto map = mapper.map(regCat, RegulationCatDto.class);
		return map;
	}

	public List<RegulationCatDto> entityToDtoList(List<RegulationCategory> regCat) {
		return regCat.stream().map(arg0 -> entityToDto(arg0)).collect(Collectors.toList());
	}

	public RegulationCategory dtoToEntity(RegulationCatDto regCat) {
		ModelMapper mapper = new ModelMapper();
		RegulationCategory map = mapper.map(regCat, RegulationCategory.class);
		return map;
	}

	public List<RegulationCategory> dtoToEntityList(List<RegulationCatDto> regCat) {
		return regCat.stream().map(arg0 -> dtoToEntity(arg0)).collect(Collectors.toList());
	}
}
