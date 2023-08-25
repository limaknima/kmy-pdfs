package com.fms.pfc.domain.converter.main;

import java.util.List;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

import com.fms.pfc.domain.dto.main.ProductRecipeDto;
import com.fms.pfc.domain.model.main.ProductRecipe;

@Component
public class ProductRecipeConverter {
	public ProductRecipeDto entityToDto(ProductRecipe prod) {
		ModelMapper mapper = new ModelMapper();
		ProductRecipeDto map = mapper.map(prod, ProductRecipeDto.class);
		return map;
	}

	public List<ProductRecipeDto> entityToDtoList(List<ProductRecipe> prod) {
		return prod.stream().map(arg0 -> entityToDto(arg0)).collect(Collectors.toList());
	}

	public ProductRecipe dtoToEntity(ProductRecipeDto prod) {
		ModelMapper mapper = new ModelMapper();
		ProductRecipe map = mapper.map(prod, ProductRecipe.class);
		return map;
	}

	public List<ProductRecipe> dtoToEntityList(List<ProductRecipeDto> prod) {
		return prod.stream().map(arg0 -> dtoToEntity(arg0)).collect(Collectors.toList());
	}
}
