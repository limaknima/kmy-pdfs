package com.fms.pfc.domain.converter.main;

import java.util.List;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

import com.fms.pfc.domain.dto.main.ReglInfoDto;
import com.fms.pfc.domain.model.main.ReglInfo;

@Component
public class ReglInfoConverter {
	public ReglInfoDto entityToDto(ReglInfo reg) {
		ModelMapper mapper = new ModelMapper();
		ReglInfoDto map = mapper.map(reg, ReglInfoDto.class);
		return map;
	}

	public List<ReglInfoDto> entityToDtoList(List<ReglInfo> reg) {
		return reg.stream().map(arg0 -> entityToDto(arg0)).collect(Collectors.toList());
	}

	public ReglInfo dtoToEntity(ReglInfoDto reg) {
		ModelMapper mapper = new ModelMapper();
		ReglInfo map = mapper.map(reg, ReglInfo.class);
		return map;
	}

	public List<ReglInfo> dtoToEntityList(List<ReglInfoDto> reg) {
		return reg.stream().map(arg0 -> dtoToEntity(arg0)).collect(Collectors.toList());
	}
}
