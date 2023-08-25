package com.fms.pfc.domain.converter.main;

import java.util.List;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

import com.fms.pfc.domain.dto.main.RegDto;
import com.fms.pfc.domain.model.main.Reg;

@Component
public class RegConverter {

	public RegDto entityToDto(Reg reg) {
		ModelMapper mapper = new ModelMapper();
		RegDto map = mapper.map(reg, RegDto.class);
		return map;
	}

	public List<RegDto> entityToDtoList(List<Reg> reg) {
		return reg.stream().map(arg0 -> entityToDto(arg0)).collect(Collectors.toList());
	}

	public Reg dtoToEntity(RegDto reg) {
		ModelMapper mapper = new ModelMapper();
		Reg map = mapper.map(reg, Reg.class);
		return map;
	}

	public List<Reg> dtoToEntityList(List<RegDto> reg) {
		return reg.stream().map(arg0 -> dtoToEntity(arg0)).collect(Collectors.toList());
	}
}
