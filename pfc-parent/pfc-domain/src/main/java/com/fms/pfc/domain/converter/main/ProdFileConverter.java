package com.fms.pfc.domain.converter.main;

import java.util.List;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

import com.fms.pfc.domain.dto.main.ProdFileDto;
import com.fms.pfc.domain.model.main.ProdFile;

@Component
public class ProdFileConverter {

	public ProdFileDto entityToDto(ProdFile prodFile) {
		ModelMapper mapper = new ModelMapper();
		ProdFileDto map = mapper.map(prodFile, ProdFileDto.class);
		return map;
	}

	public List<ProdFileDto> entityToDtoList(List<ProdFile> prodFile) {
		return prodFile.stream().map(arg0 -> entityToDto(arg0)).collect(Collectors.toList());
	}

	public ProdFile dtoToEntity(ProdFileDto prodFile) {
		ModelMapper mapper = new ModelMapper();
		ProdFile map = mapper.map(prodFile, ProdFile.class);
		return map;
	}

	public List<ProdFile> dtoToEntityList(List<ProdFileDto> prodFile) {
		return prodFile.stream().map(arg0 -> dtoToEntity(arg0)).collect(Collectors.toList());
	}
}
