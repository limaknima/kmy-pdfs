package com.fms.pfc.domain.converter;

import java.util.List;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

import com.fms.pfc.domain.dto.AlertMessageDto;
import com.fms.pfc.domain.model.AlertMessage;

@Component
public class AlertMsgConverter {

	public AlertMessageDto entityToDto(AlertMessage alertMsg) {
		ModelMapper mapper = new ModelMapper();
		AlertMessageDto map = mapper.map(alertMsg, AlertMessageDto.class);
		return map;
	}

	public List<AlertMessageDto> entityToDtoList(List<AlertMessage> alertMsg) {
		return alertMsg.stream().map(arg0 -> entityToDto(arg0)).collect(Collectors.toList());
	}

	public AlertMessage dtoToEntity(AlertMessageDto alertMsgDto) {
		ModelMapper mapper = new ModelMapper();
		AlertMessage map = mapper.map(alertMsgDto, AlertMessage.class);
		return map;
	}

	public List<AlertMessage> dtoToEntityList(List<AlertMessageDto> alertMsgDto) {
		return alertMsgDto.stream().map(arg0 -> dtoToEntity(arg0)).collect(Collectors.toList());
	}
}
