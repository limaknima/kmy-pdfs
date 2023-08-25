package com.fms.pfc.domain.converter.main;

import java.util.List;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

import com.fms.pfc.domain.dto.main.FlavorStatusDto;
import com.fms.pfc.domain.model.main.FlavorStatus;

@Component
public class FlavorStatusConverter {
	
	public FlavorStatusDto entityToDto(FlavorStatus flStatus) {
		ModelMapper mapper = new ModelMapper();
		FlavorStatusDto map = mapper.map(flStatus, FlavorStatusDto.class);
		return map;
	}

	public List<FlavorStatusDto> entityToDtoList(List<FlavorStatus> flStatus) {
		return flStatus.stream().map(arg0 -> entityToDto(arg0)).collect(Collectors.toList());
	}

	public FlavorStatus dtoToEntity(FlavorStatusDto flStatus) {
		ModelMapper mapper = new ModelMapper();
		FlavorStatus map = mapper.map(flStatus, FlavorStatus.class);
		return map;
	}

	public List<FlavorStatus> dtoToEntityList(List<FlavorStatusDto> flStatus) {
		return flStatus.stream().map(arg0 -> dtoToEntity(arg0)).collect(Collectors.toList());
	}
}
