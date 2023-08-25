package com.fms.pfc.service.api.main;

import java.util.Date;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fms.pfc.domain.converter.main.OnDemandSearchConverter;
import com.fms.pfc.domain.dto.main.OnDemandSearchDto;
import com.fms.pfc.domain.model.main.OnDemandSearch;
import com.fms.pfc.repository.main.api.OnDemandSearchRepository;

@Service
public class OnDemandSearchService {
	private final Logger logger = LoggerFactory.getLogger(OnDemandSearchService.class);

	private OnDemandSearchRepository odsRepo;
	private OnDemandSearchConverter odsConv;

	@Autowired
	public OnDemandSearchService(OnDemandSearchRepository odsRepo, OnDemandSearchConverter odsConv) {
		super();
		this.odsRepo = odsRepo;
		this.odsConv = odsConv;
	}

	public OnDemandSearch findById(String id) {
		return odsRepo.findById(id).orElse(null);
	}

	public OnDemandSearchDto findDtoById(String id) {
		OnDemandSearch obj = findById(id);
		return odsConv.entityToDto(obj);

	}

	public List<OnDemandSearchDto> findAllDto() {
		List<OnDemandSearch> ls = odsRepo.findAll();
		return odsConv.entityToDtoList(ls);
	}

	public List<OnDemandSearch> searchByCriteria(String hpl, String g2LotNo, String g2LotNoExp, String fileName,
			String fileNameExp, String path, String pathExp, String fromDate, String toDate) {
		List<OnDemandSearch> ls = odsRepo.searchByCriteria(hpl, g2LotNo, g2LotNoExp, fileName, fileNameExp, path,
				pathExp, fromDate, toDate);
		return ls;
	}

}
