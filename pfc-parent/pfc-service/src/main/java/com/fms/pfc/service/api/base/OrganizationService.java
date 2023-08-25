package com.fms.pfc.service.api.base;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import javax.transaction.Transactional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fms.pfc.domain.converter.OrganizationConverter;
import com.fms.pfc.domain.dto.OrganizationDto;
import com.fms.pfc.domain.model.Organization;
import com.fms.pfc.repository.base.api.OrganizationRepository;


@Service
public class OrganizationService {
	
	private static final Logger logger = LoggerFactory.getLogger(OrganizationService.class);

	private OrganizationRepository orgRepo;
	private OrganizationConverter converter;

	@Autowired
	public OrganizationService(OrganizationRepository orgRepo, OrganizationConverter converter) {
		super();
		this.orgRepo = orgRepo;
		this.converter = converter;
	}

	public List<Organization> searchOrganization(String orgaID, String orgaName, 
			String orgaType, String exp1, String exp2) {
		
		return orgRepo.searchOrganization(orgaID, orgaName, orgaType, exp1, exp2);
		
	}
	
	//Insert function
	public void addOrganization(String orgaID, String orgaName, String orgaType, String email, 
			String street, String town, String postcode, String state, String tel, String fax,
			String url, String effectStart, String effectEnd, byte[] logo, String creator 
			) throws IOException {   
	        
		orgRepo.addOrganization( orgaID, orgaName, orgaType, email, street, town, postcode, 
				state, tel, fax, url, effectStart, effectEnd, logo , creator);
		
	}	
	
	//Update function
	//Para1 = selected orgaID
	public void updateOrganization(String orgaID, String orgaName, String orgaType, String email, 
			String street, String town, String postcode, String state, String tel, String fax,
			String url, String effectStart, String effectEnd, byte[] logo, String modifier) {
		
		orgRepo.updateOrganization( orgaID, orgaName, orgaType, email, street, town, postcode, state, 
				tel, fax, url, effectStart, effectEnd, logo, modifier);		
	}
	
	//Delete function
	public void deleteOrganization(String orgaID) {
		orgRepo.deleteOrganization(orgaID);
	}	
	
	// Check OrgCode usage. return 1 (in use) 0 (not in use)
	public String checkOrgCodeUsage(String orgaID) {
		return orgRepo.checkOrgCodeUsage(orgaID);
	}
	
	////////////////////

	public Organization findById(String id) {
		return orgRepo.findById(id).orElse(null);
	}

	public OrganizationDto findOneById(String id) {
		Organization org = orgRepo.findById(id).orElse(null);
		SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
		OrganizationDto dto = converter.entityToDto(org);

		try {
			if (org.getEffectStart() != null) {
				dto.setEffectStartStr(formatter.format(org.getEffectStart()));
			}

			if (org.getEffectEnd() != null) {
				dto.setEffectEndStr(formatter.format(org.getEffectEnd()));
			}

			if (null != org.getLogo()) {
				dto.setLogo(org.getLogo());
			}

		} catch (Exception ex) {
			logger.error("findOneById() exp={}", ex.getMessage());
		}

		return dto;
	}

	public List<OrganizationDto> findAll() {
		List<Organization> all = orgRepo.findAll();
		return converter.entityToDtoList(all);
	}

	public List<OrganizationDto> findByCriteria(String orgaID, String orgaName, String orgaType, String exp1,
			String exp2) {
		List<Organization> all = orgRepo.searchOrganization(orgaID, orgaName, orgaType, exp1, exp2);
		return converter.entityToDtoList(all);
	}

	@Transactional
	public void saveOrganization(OrganizationDto dto, byte[] logo, String userId) {
		SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");

		try {
			if (!dto.getEffectStartStr().isEmpty()) {
				dto.setEffectStart(formatter.parse(dto.getEffectStartStr()));
			}

			if (!dto.getEffectEndStr().isEmpty()) {
				dto.setEffectEnd(formatter.parse(dto.getEffectEndStr()));
			}

			if (null != logo && !Arrays.equals(logo, new byte[logo.length])) {
				dto.setLogo(logo);
			}

		} catch (Exception ex) {
			logger.error("saveOrganization() exp={}", ex.getMessage());
		}

		Organization existing = orgRepo.findById(dto.getOrgaID()).orElse(null);
		if (null == existing) {
			dto.setCreator(userId);
			dto.setCreateTime(new Date());
		} else {
			dto.setCreator(existing.getCreator());
			dto.setCreateTime(existing.getCreateTime());
			dto.setModifier(userId);
			dto.setModifyTime(new Date());
		}

		Organization org = converter.dtoToEntity(dto);
		orgRepo.saveAndFlush(org);

	}
}
