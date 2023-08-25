package com.fms.pfc.domain.converter.main;

import java.util.List;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

import com.fms.pfc.domain.dto.main.RegulationGroupDto;
import com.fms.pfc.domain.model.main.RegulationGroup;

@Component
public class RegulationGrpConverter {

	public RegulationGroupDto entityToDto(RegulationGroup regGrp) {
		ModelMapper mapper = new ModelMapper();
		RegulationGroupDto map = mapper.map(regGrp, RegulationGroupDto.class);
		return map;
	}

	public List<RegulationGroupDto> entityToDtoList(List<RegulationGroup> regGrp) {
		return regGrp.stream().map(arg0 -> entityToDto(arg0)).collect(Collectors.toList());
	}

	public RegulationGroup dtoToEntity(RegulationGroupDto regGrp) {
		ModelMapper mapper = new ModelMapper();
		RegulationGroup map = mapper.map(regGrp, RegulationGroup.class);
		return map;
	}

	public List<RegulationGroup> dtoToEntityList(List<RegulationGroupDto> regGrp) {
		return regGrp.stream().map(arg0 -> dtoToEntity(arg0)).collect(Collectors.toList());
	}
}
