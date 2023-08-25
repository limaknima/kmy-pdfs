package com.fms.pfc.domain.converter.main;

import java.util.List;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

import com.fms.pfc.domain.dto.main.UsbUsrDto;
import com.fms.pfc.domain.model.main.UsbUsr;

@Component
public class UsbUsrConverter {

	public UsbUsrDto entityToDto(UsbUsr usbUsr) {
		ModelMapper mapper = new ModelMapper();
		UsbUsrDto map = mapper.map(usbUsr, UsbUsrDto.class);
		return map;
	}

	public List<UsbUsrDto> entityToDtoList(List<UsbUsr> usbUsr) {
		return usbUsr.stream().map(arg0 -> entityToDto(arg0)).collect(Collectors.toList());
	}

	public UsbUsr dtoToEntity(UsbUsrDto usbUsr) {
		ModelMapper mapper = new ModelMapper();
		UsbUsr map = mapper.map(usbUsr, UsbUsr.class);
		return map;
	}

	public List<UsbUsr> dtoToEntityList(List<UsbUsrDto> usbUsr) {
		return usbUsr.stream().map(arg0 -> dtoToEntity(arg0)).collect(Collectors.toList());
	}
}
