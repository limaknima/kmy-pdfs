package com.fms.pfc.domain.converter.main;

import java.util.List;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

import com.fms.pfc.domain.dto.main.PrIngredientDto;
import com.fms.pfc.domain.model.main.PrIngredient;

@Component
public class PrIngredientConverter {
	
	public PrIngredientDto entityToDto(PrIngredient prIng) {
		ModelMapper mapper = new ModelMapper();
		PrIngredientDto map = mapper.map(prIng, PrIngredientDto.class);
		return map;
	}

	public List<PrIngredientDto> entityToDtoList(List<PrIngredient> prIng) {
		return prIng.stream().map(arg0 -> entityToDto(arg0)).collect(Collectors.toList());
	}

	public PrIngredient dtoToEntity(PrIngredientDto prIng) {
		ModelMapper mapper = new ModelMapper();
		PrIngredient map = mapper.map(prIng, PrIngredient.class);
		return map;
	}

	public List<PrIngredient> dtoToEntityList(List<PrIngredientDto> prIng) {
		return prIng.stream().map(arg0 -> dtoToEntity(arg0)).collect(Collectors.toList());
	}
}
