package com.fms.pfc.domain.converter.main;

import java.util.List;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

import com.fms.pfc.domain.dto.main.RawMaterialDto;
import com.fms.pfc.domain.model.main.RawMaterial;

@Component
public class RawMaterialConverter {
	public RawMaterialDto entityToDto(RawMaterial rm) {
		ModelMapper mapper = new ModelMapper();
		RawMaterialDto map = mapper.map(rm, RawMaterialDto.class);
		return map;
	}

	public List<RawMaterialDto> entityToDtoList(List<RawMaterial> rm) {
		return rm.stream().map(arg0 -> entityToDto(arg0)).collect(Collectors.toList());
	}

	public RawMaterial dtoToEntity(RawMaterialDto rm) {
		ModelMapper mapper = new ModelMapper();
		RawMaterial map = mapper.map(rm, RawMaterial.class);
		return map;
	}

	public List<RawMaterial> dtoToEntityList(List<RawMaterialDto> rm) {
		return rm.stream().map(arg0 -> dtoToEntity(arg0)).collect(Collectors.toList());
	}
}
