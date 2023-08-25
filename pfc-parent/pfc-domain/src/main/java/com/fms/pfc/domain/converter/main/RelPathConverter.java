package com.fms.pfc.domain.converter.main;

import java.util.List;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

import com.fms.pfc.domain.dto.main.RelPathDto;
import com.fms.pfc.domain.model.main.RelPath;

@Component
public class RelPathConverter {

	public RelPathDto entityToDto(RelPath relPath) {
		ModelMapper mapper = new ModelMapper();
		RelPathDto map = mapper.map(relPath, RelPathDto.class);
		return map;
	}

	public List<RelPathDto> entityToDtoList(List<RelPath> relPath) {
		return relPath.stream().map(arg0 -> entityToDto(arg0)).collect(Collectors.toList());
	}

	public RelPath dtoToEntity(RelPathDto relPath) {
		ModelMapper mapper = new ModelMapper();
		RelPath map = mapper.map(relPath, RelPath.class);
		return map;
	}

	public List<RelPath> dtoToEntityList(List<RelPathDto> relPath) {
		return relPath.stream().map(arg0 -> dtoToEntity(arg0)).collect(Collectors.toList());
	}
}
