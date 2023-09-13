package com.fms.pfc.domain.converter.main;

import java.util.List;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

import com.fms.pfc.domain.dto.main.FoldCatgConfDto2;
import com.fms.pfc.domain.model.main.FoldCatgConf2;

@Component
public class FoldCatgConfConverter2 {

	public FoldCatgConfDto2 entityToDto(FoldCatgConf2 foldCatgConf) {
		ModelMapper mapper = new ModelMapper();
		FoldCatgConfDto2 map = mapper.map(foldCatgConf, FoldCatgConfDto2.class);
		return map;
	}

	public List<FoldCatgConfDto2> entityToDtoList(List<FoldCatgConf2> foldCatgConf) {
		return foldCatgConf.stream().map(arg0 -> entityToDto(arg0)).collect(Collectors.toList());
	}

	public FoldCatgConf2 dtoToEntity(FoldCatgConfDto2 foldCatgConf) {
		ModelMapper mapper = new ModelMapper();
		FoldCatgConf2 map = mapper.map(foldCatgConf, FoldCatgConf2.class);
		return map;
	}

	public List<FoldCatgConf2> dtoToEntityList(List<FoldCatgConfDto2> foldCatgConf) {
		return foldCatgConf.stream().map(arg0 -> dtoToEntity(arg0)).collect(Collectors.toList());
	}
}
