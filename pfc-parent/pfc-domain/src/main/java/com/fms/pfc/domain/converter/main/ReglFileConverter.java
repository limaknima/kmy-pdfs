package com.fms.pfc.domain.converter.main;

import java.util.List;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

import com.fms.pfc.domain.dto.main.ReglFileDto;
import com.fms.pfc.domain.model.main.ReglFile;

@Component
public class ReglFileConverter {

	public ReglFileDto entityToDto(ReglFile reg) {
		ModelMapper mapper = new ModelMapper();
		ReglFileDto map = mapper.map(reg, ReglFileDto.class);
		return map;
	}

	public List<ReglFileDto> entityToDtoList(List<ReglFile> reg) {
		return reg.stream().map(arg0 -> entityToDto(arg0)).collect(Collectors.toList());
	}

	public ReglFile dtoToEntity(ReglFileDto reg) {
		ModelMapper mapper = new ModelMapper();
		ReglFile map = mapper.map(reg, ReglFile.class);
		return map;
	}

	public List<ReglFile> dtoToEntityList(List<ReglFileDto> reg) {
		return reg.stream().map(arg0 -> dtoToEntity(arg0)).collect(Collectors.toList());
	}
}
