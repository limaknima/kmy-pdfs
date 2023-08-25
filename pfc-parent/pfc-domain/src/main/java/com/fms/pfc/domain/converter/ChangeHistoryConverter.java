package com.fms.pfc.domain.converter;

import java.util.List;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

import com.fms.pfc.domain.dto.ChangeHistoryDto;
import com.fms.pfc.domain.model.ChangeHistory;

@Component
public class ChangeHistoryConverter {

	public ChangeHistoryDto entityToDto(ChangeHistory chg) {
		ModelMapper mapper = new ModelMapper();
		ChangeHistoryDto map = mapper.map(chg, ChangeHistoryDto.class);
		return map;
	}

	public List<ChangeHistoryDto> entityToDtoList(List<ChangeHistory> chg) {
		return chg.stream().map(arg0 -> entityToDto(arg0)).collect(Collectors.toList());
	}

	public ChangeHistory dtoToEntity(ChangeHistoryDto ChangeHistoryDto) {
		ModelMapper mapper = new ModelMapper();
		ChangeHistory map = mapper.map(ChangeHistoryDto, ChangeHistory.class);
		return map;
	}

	public List<ChangeHistory> dtoToEntityList(List<ChangeHistoryDto> ChangeHistoryDto) {
		return ChangeHistoryDto.stream().map(arg0 -> dtoToEntity(arg0)).collect(Collectors.toList());
	}
}
