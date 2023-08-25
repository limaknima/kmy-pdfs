package com.fms.pfc.domain.converter;

import java.util.List;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

import com.fms.pfc.domain.dto.FileTypeDto;
import com.fms.pfc.domain.model.FileType;

@Component
public class FileTypeConverter {

	public FileTypeDto entityToDto(FileType ft) {
		ModelMapper mapper = new ModelMapper();
		FileTypeDto map = mapper.map(ft, FileTypeDto.class);
		return map;
	}

	public List<FileTypeDto> entityToDtoList(List<FileType> ft) {
		return ft.stream().map(arg0 -> entityToDto(arg0)).collect(Collectors.toList());
	}

	public FileType dtoToEntity(FileTypeDto ft) {
		ModelMapper mapper = new ModelMapper();
		FileType map = mapper.map(ft, FileType.class);
		return map;
	}

	public List<FileType> dtoToEntityList(List<FileTypeDto> ft) {
		return ft.stream().map(arg0 -> dtoToEntity(arg0)).collect(Collectors.toList());
	}	
}
