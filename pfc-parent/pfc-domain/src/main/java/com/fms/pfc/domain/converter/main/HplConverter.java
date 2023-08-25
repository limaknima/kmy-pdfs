package com.fms.pfc.domain.converter.main;

import java.util.List;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

import com.fms.pfc.domain.dto.main.HplDto;
import com.fms.pfc.domain.model.main.Hpl;

@Component
public class HplConverter {
	
	public HplDto entityToDto(Hpl hpl) {
		ModelMapper mapper = new ModelMapper();
		HplDto map = mapper.map(hpl, HplDto.class);
		return map;
	}

	public List<HplDto> entityToDtoList(List<Hpl> hpl) {
		return hpl.stream().map(arg0 -> entityToDto(arg0)).collect(Collectors.toList());
	}

	public Hpl dtoToEntity(HplDto hpl) {
		ModelMapper mapper = new ModelMapper();
		Hpl map = mapper.map(hpl, Hpl.class);
		return map;
	}

	public List<Hpl> dtoToEntityList(List<HplDto> hpl) {
		return hpl.stream().map(arg0 -> dtoToEntity(arg0)).collect(Collectors.toList());
	}
}
