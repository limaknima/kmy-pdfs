package com.fms.pfc.domain.converter;

import java.util.List;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

import com.fms.pfc.domain.dto.RoleDto;
import com.fms.pfc.domain.model.Role;

@Component
public class RoleConverter {

	public RoleDto entityToDto(Role role) {
		ModelMapper mapper = new ModelMapper();
		RoleDto map = mapper.map(role, RoleDto.class);
		return map;
	}

	public List<RoleDto> entityToDtoList(List<Role> role) {
		return role.stream().map(arg0 -> entityToDto(arg0)).collect(Collectors.toList());
	}

	public Role dtoToEntity(RoleDto role) {
		ModelMapper mapper = new ModelMapper();
		Role map = mapper.map(role, Role.class);
		return map;
	}

	public List<Role> dtoToEntityList(List<RoleDto> role) {
		return role.stream().map(arg0 -> dtoToEntity(arg0)).collect(Collectors.toList());
	}	

}
