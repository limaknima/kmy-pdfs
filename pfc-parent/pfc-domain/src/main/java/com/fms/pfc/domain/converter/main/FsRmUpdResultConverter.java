package com.fms.pfc.domain.converter.main;

import java.util.List;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

import com.fms.pfc.domain.dto.main.FsRmUpdResultDto;
import com.fms.pfc.domain.model.main.FsRmUpdResult;

@Component
public class FsRmUpdResultConverter {
	public FsRmUpdResultDto entityToDto(FsRmUpdResult fsRmUpd) {
		ModelMapper mapper = new ModelMapper();
		FsRmUpdResultDto map = mapper.map(fsRmUpd, FsRmUpdResultDto.class);
		return map;
	}

	public List<FsRmUpdResultDto> entityToDtoList(List<FsRmUpdResult> fsRmUpd) {
		return fsRmUpd.stream().map(arg0 -> entityToDto(arg0)).collect(Collectors.toList());
	}

	public FsRmUpdResult dtoToEntity(FsRmUpdResultDto fsRmUpd) {
		ModelMapper mapper = new ModelMapper();
		FsRmUpdResult map = mapper.map(fsRmUpd, FsRmUpdResult.class);
		return map;
	}

	public List<FsRmUpdResult> dtoToEntityList(List<FsRmUpdResultDto> fsRmUpd) {
		return fsRmUpd.stream().map(arg0 -> dtoToEntity(arg0)).collect(Collectors.toList());
	}
}
