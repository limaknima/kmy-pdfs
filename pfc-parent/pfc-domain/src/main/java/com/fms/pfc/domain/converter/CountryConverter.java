package com.fms.pfc.domain.converter;

import java.util.List;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

import com.fms.pfc.domain.dto.CountryDto;
import com.fms.pfc.domain.model.Country;

@Component
public class CountryConverter {

	public CountryDto entityToDto(Country country) {
		ModelMapper mapper = new ModelMapper();
		CountryDto map = mapper.map(country, CountryDto.class);
		return map;
	}

	public List<CountryDto> entityToDtoList(List<Country> country) {
		return country.stream().map(arg0 -> entityToDto(arg0)).collect(Collectors.toList());
	}

	public Country dtoToEntity(CountryDto countryDto) {
		ModelMapper mapper = new ModelMapper();
		Country map = mapper.map(countryDto, Country.class);
		return map;
	}

	public List<Country> dtoToEntityList(List<CountryDto> countryDto) {
		return countryDto.stream().map(arg0 -> dtoToEntity(arg0)).collect(Collectors.toList());
	}
}
