package com.fms.pfc.domain.converter.main;

import java.util.List;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

import com.fms.pfc.domain.dto.main.FsRmUpdDto;
import com.fms.pfc.domain.model.main.FsRmUpd;

@Component
public class FsRmUpdConverter {
	public FsRmUpdDto entityToDto(FsRmUpd fsRmUpd) {
		ModelMapper mapper = new ModelMapper();
		FsRmUpdDto map = mapper.map(fsRmUpd, FsRmUpdDto.class);
		return map;
	}

	public List<FsRmUpdDto> entityToDtoList(List<FsRmUpd> fsRmUpd) {
		return fsRmUpd.stream().map(arg0 -> entityToDto(arg0)).collect(Collectors.toList());
	}

	public FsRmUpd dtoToEntity(FsRmUpdDto fsRmUpd) {
		ModelMapper mapper = new ModelMapper();
		FsRmUpd map = mapper.map(fsRmUpd, FsRmUpd.class);
		return map;
	}

	public List<FsRmUpd> dtoToEntityList(List<FsRmUpdDto> fsRmUpd) {
		return fsRmUpd.stream().map(arg0 -> dtoToEntity(arg0)).collect(Collectors.toList());
	}
}
