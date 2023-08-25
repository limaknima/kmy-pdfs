package com.fms.pfc.service.api.main;
//Kimberley 

//Differences with Regulation.java are some some difference with the datatypes
//Used for 5.3.2	Upload RM regulation

import java.util.Date;
import java.util.List;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fms.pfc.domain.model.main.Reg;
import com.fms.pfc.domain.model.main.Regl;
import com.fms.pfc.repository.main.api.RegRepository;
import com.fms.pfc.repository.main.api.ReglRepository;

@Service
public class RegService {

	private RegRepository regRepo;
	private ReglRepository reglRepo;

	@Autowired
	public RegService(RegRepository regRepo, ReglRepository reglRepo) {
		super();
		this.regRepo = regRepo;
		this.reglRepo = reglRepo;
	}

	public List<Reg> searchRegList(int rawMatlId, String countryId) {
		return regRepo.searchRegList(rawMatlId, countryId);
	}

	public List<Reg> getRegList(int regId, String countryId) {
		return regRepo.getRegList(regId, countryId);
	}

	public void addRegulation(int rawMatlId, String countryId, String regName, String refUrl1, String refUrl2,
			String refUrl3, String refUrl4, String refUrl5, String remarks, String creatorId) {

		regRepo.addRegulation(rawMatlId, countryId, regName, refUrl1, refUrl2, refUrl3, refUrl4, refUrl5, remarks,
				creatorId);
	}

	public void updRegulation(String regName, String refUrl1, String refUrl2, String refUrl3, String refUrl4,
			String refUrl5, String remarks, String modifiedId, int regId) {

		regRepo.updRegulation(regName, refUrl1, refUrl2, refUrl3, refUrl4, refUrl5, remarks, modifiedId, regId);
	}
	
	public Regl findReglById(int id) {
		return reglRepo.findById(id).orElse(null);
	}
	
	public List<Regl> searchReglList(int rawMatlId, String countryId) {
		return reglRepo.searchReglList(rawMatlId, countryId);
	}
	
	@Transactional
	public Regl insertRegl(int rawMatlId, String countryId, String regName, String refUrl1, String refUrl2,
			String refUrl3, String refUrl4, String refUrl5, String remarks, String userId) {

		Regl dto = new Regl();
		dto.setRawMatlId(rawMatlId);
		dto.setCountryId(countryId);
		dto.setRegName(regName);
		dto.setRefUrl1(refUrl1);
		dto.setRefUrl2(refUrl2);
		dto.setRefUrl3(refUrl3);
		dto.setRefUrl4(refUrl4);
		dto.setRefUrl5(refUrl5);
		dto.setRemarks(remarks);
		dto.setCreatorId(userId);
		dto.setCreatedDateTime(new Date());

		reglRepo.save(dto);

		return dto;
	}
	
	@Transactional
	public Regl updateRegl(int regId, String regName, String refUrl1, String refUrl2, String refUrl3, String refUrl4,
			String refUrl5, String remarks, String userId) {

		Regl dto = findReglById(regId);
		dto.setRegName(regName);
		dto.setRefUrl1(refUrl1);
		dto.setRefUrl2(refUrl2);
		dto.setRefUrl3(refUrl3);
		dto.setRefUrl4(refUrl4);
		dto.setRefUrl5(refUrl5);
		dto.setRemarks(remarks);
		dto.setModifierId(userId);
		dto.setModifiedDateTime(new Date());

		reglRepo.save(dto);

		return dto;
	}
}
