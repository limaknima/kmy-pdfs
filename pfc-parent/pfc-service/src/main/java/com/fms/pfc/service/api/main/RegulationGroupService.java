package com.fms.pfc.service.api.main;

import java.io.IOException;
import java.util.Date;
import java.util.List;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fms.pfc.domain.converter.main.RegulationGrpConverter;
import com.fms.pfc.domain.dto.main.RegulationGroupDto;
import com.fms.pfc.domain.model.main.RegulationGroup;
import com.fms.pfc.repository.main.api.RegulationGroupRepository;

@Service
public class RegulationGroupService {

	private RegulationGroupRepository regGrpRepo;
	private RegulationGrpConverter converter;

	@Autowired
	public RegulationGroupService(RegulationGroupRepository regGrpRepo, RegulationGrpConverter converter) {
		super();
		this.regGrpRepo = regGrpRepo;
		this.converter = converter;
	}

	//Start Kimberley 20210109
	//Search query based on id
	public RegulationGroup searchGroupRegulation(String grpId) {
		
		return regGrpRepo.searchGroupRegulation(grpId);
		
	}//End Kimberley 20210109
	  
	public List<RegulationGroup> searchGroupRegulation(String grpName, String grpDesc, 
			String exp1, String exp2) { 
		
		return regGrpRepo.searchGroupRegulation(grpName, grpDesc, exp1, exp2);
		
	}
	
	//FSGS) KarVei 27/2/2021 Add - Check if group is used in REGULATION_DOC
	public List<RegulationGroup> checkGroupExist() {
		
		return regGrpRepo.checkGroupExist();
		
	}
	
	//Insert function
	public void addGroupRegulation(String grpName, String grpDesc, String creatorId) throws IOException {   
	        
		regGrpRepo.addGroupRegulation( grpName, grpDesc, creatorId);
		
	}	
	
	//Update function
	public void updateGroupRegulation(String grpName, String grpDesc, String creatorId) {
		
		regGrpRepo.updateGroupRegulation( grpName, grpDesc, creatorId);		
	}
	
	//Delete function
	public void deleteGroupRegulation(String grpName) {
		regGrpRepo.deleteGroupRegulation(grpName);
	}	
	
	////////////////////
	
	public RegulationGroup findById(int id) {
		return regGrpRepo.findById(id).orElse(null);
	}

	public RegulationGroupDto findOneById(int id) {
		RegulationGroup regGrp = regGrpRepo.findById(id).orElse(null);
		return converter.entityToDto(regGrp);
	}

	public List<RegulationGroupDto> findAll() {
		List<RegulationGroup> all = regGrpRepo.findAll();
		return converter.entityToDtoList(all);
	}

	public List<RegulationGroupDto> findByCriteria(String grpName, String grpDesc, String exp1, String exp2) {
		List<RegulationGroup> all = regGrpRepo.searchGroupRegulation(grpName, grpDesc, exp1, exp2);
		return converter.entityToDtoList(all);
	}
	
	@Transactional
	public void saveRegGrp(RegulationGroupDto dto, String userId) {
		RegulationGroup existing = regGrpRepo.findById(dto.getRegDocCatGrpId()).orElse(null);
		if(existing == null) {
			dto.setCreatorId(userId);
			dto.setCreatedDate(new Date());
		} else {
			dto.setCreatorId(existing.getCreatorId());
			dto.setCreatedDate(existing.getCreatedDate());
			dto.setModifierId(userId);
			dto.setModifiedDatetime(new Date());
		}
		
		RegulationGroup regGrp = converter.dtoToEntity(dto);
		regGrpRepo.saveAndFlush(regGrp);
	}
}
