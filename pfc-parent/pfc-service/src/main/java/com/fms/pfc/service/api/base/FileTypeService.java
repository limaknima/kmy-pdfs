package com.fms.pfc.service.api.base;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fms.pfc.domain.converter.FileTypeConverter;
import com.fms.pfc.domain.dto.FileTypeDto;
import com.fms.pfc.repository.base.api.FileTypeRepository;

@Service
public class FileTypeService {
	private final Logger logger = LoggerFactory.getLogger(FileTypeService.class);

	private FileTypeRepository ftRepo;
	private FileTypeConverter ftconv;

	@Autowired
	public FileTypeService(FileTypeRepository ftRepo, FileTypeConverter ftconv) {
		super();
		this.ftRepo = ftRepo;
		this.ftconv = ftconv;
	}

	public List<FileTypeDto> findAllDto() {
		return ftconv.entityToDtoList(ftRepo.findAll());
	}

}
