package com.fms.pfc.domain.converter.main;

import java.util.List;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

import com.fms.pfc.domain.dto.main.FoldCatgConfDto;
import com.fms.pfc.domain.model.main.FoldCatgConf;

@Component
public class FoldCatgConfConverter {

	public FoldCatgConfDto entityToDto(FoldCatgConf foldCatgConf) {
		ModelMapper mapper = new ModelMapper();
		FoldCatgConfDto map = mapper.map(foldCatgConf, FoldCatgConfDto.class);
		return map;
	}

	public List<FoldCatgConfDto> entityToDtoList(List<FoldCatgConf> foldCatgConf) {
		return foldCatgConf.stream().map(arg0 -> entityToDto(arg0)).collect(Collectors.toList());
	}

	public FoldCatgConf dtoToEntity(FoldCatgConfDto foldCatgConf) {
		ModelMapper mapper = new ModelMapper();
		FoldCatgConf map = mapper.map(foldCatgConf, FoldCatgConf.class);
		return map;
	}

	public List<FoldCatgConf> dtoToEntityList(List<FoldCatgConfDto> foldCatgConf) {
		return foldCatgConf.stream().map(arg0 -> dtoToEntity(arg0)).collect(Collectors.toList());
	}
}
