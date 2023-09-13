package com.fms.pfc.domain.converter.main;

import java.util.List;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

import com.fms.pfc.domain.dto.main.RelPathDto2;
import com.fms.pfc.domain.model.main.RelPath2;

@Component
public class RelPathConverter2 {

	public RelPathDto2 entityToDto(RelPath2 relPath) {
		ModelMapper mapper = new ModelMapper();
		RelPathDto2 map = mapper.map(relPath, RelPathDto2.class);
		return map;
	}

	public List<RelPathDto2> entityToDtoList(List<RelPath2> relPath) {
		return relPath.stream().map(arg0 -> entityToDto(arg0)).collect(Collectors.toList());
	}

	public RelPath2 dtoToEntity(RelPathDto2 relPath) {
		ModelMapper mapper = new ModelMapper();
		RelPath2 map = mapper.map(relPath, RelPath2.class);
		return map;
	}

	public List<RelPath2> dtoToEntityList(List<RelPathDto2> relPath) {
		return relPath.stream().map(arg0 -> dtoToEntity(arg0)).collect(Collectors.toList());
	}
}
