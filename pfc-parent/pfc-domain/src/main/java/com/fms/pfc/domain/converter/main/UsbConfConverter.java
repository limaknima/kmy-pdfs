package com.fms.pfc.domain.converter.main;

import java.util.List;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

import com.fms.pfc.domain.dto.main.UsbConfDto;
import com.fms.pfc.domain.model.main.UsbConf;

@Component
public class UsbConfConverter {

	public UsbConfDto entityToDto(UsbConf usbConf) {
		ModelMapper mapper = new ModelMapper();
		UsbConfDto map = mapper.map(usbConf, UsbConfDto.class);
		return map;
	}

	public List<UsbConfDto> entityToDtoList(List<UsbConf> usbConf) {
		return usbConf.stream().map(arg0 -> entityToDto(arg0)).collect(Collectors.toList());
	}

	public UsbConf dtoToEntity(UsbConfDto usbConf) {
		ModelMapper mapper = new ModelMapper();
		UsbConf map = mapper.map(usbConf, UsbConf.class);
		return map;
	}

	public List<UsbConf> dtoToEntityList(List<UsbConfDto> usbConf) {
		return usbConf.stream().map(arg0 -> dtoToEntity(arg0)).collect(Collectors.toList());
	}
}
