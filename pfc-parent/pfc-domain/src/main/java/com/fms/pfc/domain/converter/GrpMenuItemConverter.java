package com.fms.pfc.domain.converter;

import java.util.List;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

import com.fms.pfc.domain.dto.GrpMenuItemDto;
import com.fms.pfc.domain.model.GrpMenuItem;

@Component
public class GrpMenuItemConverter {
	public GrpMenuItemDto entityToDto(GrpMenuItem gmi) {
		ModelMapper mapper = new ModelMapper();
		GrpMenuItemDto map = mapper.map(gmi, GrpMenuItemDto.class);
		return map;
	}

	public List<GrpMenuItemDto> entityToDtoList(List<GrpMenuItem> gmi) {
		return gmi.stream().map(arg0 -> entityToDto(arg0)).collect(Collectors.toList());
	}

	public GrpMenuItem dtoToEntity(GrpMenuItemDto gmi) {
		ModelMapper mapper = new ModelMapper();
		GrpMenuItem map = mapper.map(gmi, GrpMenuItem.class);
		return map;
	}

	public List<GrpMenuItem> dtoToEntityList(List<GrpMenuItemDto> gmi) {
		return gmi.stream().map(arg0 -> dtoToEntity(arg0)).collect(Collectors.toList());
	}
}
