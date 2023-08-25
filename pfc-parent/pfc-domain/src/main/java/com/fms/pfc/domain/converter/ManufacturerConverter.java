package com.fms.pfc.domain.converter;

import java.util.List;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

import com.fms.pfc.domain.dto.ManufacturerDto;
import com.fms.pfc.domain.model.Manufacturer;

@Component
public class ManufacturerConverter {

	public ManufacturerDto entityToDto(Manufacturer manf) {
		ModelMapper mapper = new ModelMapper();
		ManufacturerDto map = mapper.map(manf, ManufacturerDto.class);
		return map;
	}

	public List<ManufacturerDto> entityToDtoList(List<Manufacturer> manf) {
		return manf.stream().map(arg0 -> entityToDto(arg0)).collect(Collectors.toList());
	}

	public Manufacturer dtoToEntity(ManufacturerDto manf) {
		ModelMapper mapper = new ModelMapper();
		Manufacturer map = mapper.map(manf, Manufacturer.class);
		return map;
	}

	public List<Manufacturer> dtoToEntityList(List<ManufacturerDto> manf) {
		return manf.stream().map(arg0 -> dtoToEntity(arg0)).collect(Collectors.toList());
	}

}
