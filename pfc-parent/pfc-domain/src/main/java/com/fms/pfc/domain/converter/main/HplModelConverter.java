package com.fms.pfc.domain.converter.main;

import java.util.List;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

import com.fms.pfc.domain.dto.main.HplModelDto;
import com.fms.pfc.domain.model.main.HplModel;

@Component
public class HplModelConverter {

	public HplModelDto entityToDto(HplModel hpl) {
		ModelMapper mapper = new ModelMapper();
		HplModelDto map = mapper.map(hpl, HplModelDto.class);
		return map;
	}

	public List<HplModelDto> entityToDtoList(List<HplModel> hpl) {
		return hpl.stream().map(arg0 -> entityToDto(arg0)).collect(Collectors.toList());
	}

	public HplModel dtoToEntity(HplModelDto hpl) {
		ModelMapper mapper = new ModelMapper();
		HplModel map = mapper.map(hpl, HplModel.class);
		return map;
	}

	public List<HplModel> dtoToEntityList(List<HplModelDto> hpl) {
		return hpl.stream().map(arg0 -> dtoToEntity(arg0)).collect(Collectors.toList());
	}
}
