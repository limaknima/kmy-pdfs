package com.fms.pfc.domain.converter.main;

import java.util.List;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

import com.fms.pfc.domain.dto.main.ReglRmDto;
import com.fms.pfc.domain.model.main.ReglRm;

@Component
public class ReglRmConverter {
	public ReglRmDto entityToDto(ReglRm reg) {
		ModelMapper mapper = new ModelMapper();
		ReglRmDto map = mapper.map(reg, ReglRmDto.class);
		return map;
	}

	public List<ReglRmDto> entityToDtoList(List<ReglRm> reg) {
		return reg.stream().map(arg0 -> entityToDto(arg0)).collect(Collectors.toList());
	}

	public ReglRm dtoToEntity(ReglRmDto reg) {
		ModelMapper mapper = new ModelMapper();
		ReglRm map = mapper.map(reg, ReglRm.class);
		return map;
	}

	public List<ReglRm> dtoToEntityList(List<ReglRmDto> reg) {
		return reg.stream().map(arg0 -> dtoToEntity(arg0)).collect(Collectors.toList());
	}
}
