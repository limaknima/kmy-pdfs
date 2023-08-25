package com.fms.pfc.domain.converter.main;

import java.util.List;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

import com.fms.pfc.domain.dto.main.G2LotViewDto;
import com.fms.pfc.domain.model.main.G2LotView;

@Component
public class G2LotViewConverter {

	public G2LotViewDto entityToDto(G2LotView g2LotView) {
		ModelMapper mapper = new ModelMapper();
		G2LotViewDto map = mapper.map(g2LotView, G2LotViewDto.class);
		return map;
	}

	public List<G2LotViewDto> entityToDtoList(List<G2LotView> g2LotView) {
		return g2LotView.stream().map(arg0 -> entityToDto(arg0)).collect(Collectors.toList());
	}

	public G2LotView dtoToEntity(G2LotViewDto g2LotView) {
		ModelMapper mapper = new ModelMapper();
		G2LotView map = mapper.map(g2LotView, G2LotView.class);
		return map;
	}

	public List<G2LotView> dtoToEntityList(List<G2LotViewDto> g2LotView) {
		return g2LotView.stream().map(arg0 -> dtoToEntity(arg0)).collect(Collectors.toList());
	}
}
