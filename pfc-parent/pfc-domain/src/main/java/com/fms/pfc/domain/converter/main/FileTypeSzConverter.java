package com.fms.pfc.domain.converter.main;

import java.util.List;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

import com.fms.pfc.domain.dto.main.FileTypeSzDto;
import com.fms.pfc.domain.model.main.FileTypeSz;

@Component
public class FileTypeSzConverter {

	public FileTypeSzDto entityToDto(FileTypeSz fileTypeSz) {
		ModelMapper mapper = new ModelMapper();
		FileTypeSzDto map = mapper.map(fileTypeSz, FileTypeSzDto.class);
		return map;
	}

	public List<FileTypeSzDto> entityToDtoList(List<FileTypeSz> fileTypeSz) {
		return fileTypeSz.stream().map(arg0 -> entityToDto(arg0)).collect(Collectors.toList());
	}

	public FileTypeSz dtoToEntity(FileTypeSzDto fileTypeSz) {
		ModelMapper mapper = new ModelMapper();
		FileTypeSz map = mapper.map(fileTypeSz, FileTypeSz.class);
		return map;
	}

	public List<FileTypeSz> dtoToEntityList(List<FileTypeSzDto> fileTypeSz) {
		return fileTypeSz.stream().map(arg0 -> dtoToEntity(arg0)).collect(Collectors.toList());
	}
}
