package com.fms.pfc.domain.converter;

import java.util.List;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

import com.fms.pfc.domain.dto.OrganizationDto;
import com.fms.pfc.domain.model.Organization;

@Component
public class OrganizationConverter {

	public OrganizationDto entityToDto(Organization org) {
		ModelMapper mapper = new ModelMapper();
		OrganizationDto map = mapper.map(org, OrganizationDto.class);
		return map;
	}

	public List<OrganizationDto> entityToDtoList(List<Organization> org) {
		return org.stream().map(arg0 -> entityToDto(arg0)).collect(Collectors.toList());
	}

	public Organization dtoToEntity(OrganizationDto org) {
		ModelMapper mapper = new ModelMapper();
		Organization map = mapper.map(org, Organization.class);
		return map;
	}

	public List<Organization> dtoToEntityList(List<OrganizationDto> org) {
		return org.stream().map(arg0 -> dtoToEntity(arg0)).collect(Collectors.toList());
	}
}
