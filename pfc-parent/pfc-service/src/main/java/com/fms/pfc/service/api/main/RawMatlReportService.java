package com.fms.pfc.service.api.main;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fms.pfc.domain.model.main.RawMaterialReport;
import com.fms.pfc.repository.main.api.RawMatlReportRepository;

@Service
public class RawMatlReportService {

	@Autowired
	private RawMatlReportRepository rmR;
	
	public List<RawMaterialReport> getRawMaterialReport(int rmId, String tsNo, int manufacturerId, int supplierId, 
			String countryId, String fema, String jecfa, String ins) {
		return rmR.getRawMaterialReport(rmId, tsNo, manufacturerId, supplierId, countryId, fema, jecfa, ins);
	}
	
	public List<Integer> getRawMatlIds(int rmId, String tsNo, int manufacturerId, int supplierId, 
			String countryId, String fema, String jecfa, String ins) {
		return rmR.getRawMaterialIds(rmId, tsNo, manufacturerId, supplierId, countryId, fema, jecfa, ins);
	}
}
