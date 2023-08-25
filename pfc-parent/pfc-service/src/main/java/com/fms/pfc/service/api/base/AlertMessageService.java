package com.fms.pfc.service.api.base;

import java.util.Date;
import java.util.List;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fms.pfc.domain.converter.AlertMsgConverter;
import com.fms.pfc.domain.dto.AlertMessageDto;
import com.fms.pfc.domain.model.AlertMessage;
import com.fms.pfc.repository.base.api.AlertMessageRepository;

@Service
public class AlertMessageService {

	private AlertMessageRepository alerMsgRepo;
	private AlertMsgConverter converter;

	@Autowired
	public AlertMessageService(AlertMessageRepository alerMsgRepo, AlertMsgConverter converter) {
		super();
		this.alerMsgRepo = alerMsgRepo;
		this.converter = converter;
	}

	public List<AlertMessage> searchAlert(String dateFr, String dateTo, 
			String alertType, String subject, String exp1, String exp2) {
		return alerMsgRepo.searchAlert(dateFr, dateTo, alertType, subject, exp1, exp2);
	}
	
	public AlertMessage searchAlertById(String alertType) {
		return alerMsgRepo.searchAlertById(alertType);
	}
	
	public void deleteAlert(String alertType) {
		alerMsgRepo.deleteAlert(alertType);
	}
	
	public void addAlert(String alertType, String subject, String description, String createdUser) {
		alerMsgRepo.addAlert(alertType, subject, description, createdUser);
	}
	
	public void updateAlert(String subject, String description, String updatedUser, String alertType) {
		alerMsgRepo.updateAlert(subject, description, updatedUser, alertType);
	}
	
	////////////////////
	
	public AlertMessage findById(String id) {
		return alerMsgRepo.findById(id.trim()).orElse(null);
	}
	
	public AlertMessageDto findOneById(String id) {
		AlertMessage am = findById(id.trim());
		return converter.entityToDto(am);
	}

	public List<AlertMessageDto> findAll() {
		List<AlertMessage> allAlert = alerMsgRepo.findAll();
		return converter.entityToDtoList(allAlert);
	}

	public List<AlertMessageDto> findByCriteria(String dateFr, String dateTo, String alertType, String subject,
			String exp1, String exp2) {
		List<AlertMessage> allAlert = alerMsgRepo.searchAlert(dateFr, dateTo, alertType, subject, exp1, exp2);
		return converter.entityToDtoList(allAlert);
	}

	@Transactional
	public void saveAlert(String alertType, String subject, String description, String userId) {
		AlertMessageDto dto = new AlertMessageDto();
		dto.setAlertType(alertType.trim());
		dto.setSubject(subject);
		dto.setDescription(description);
		
		AlertMessage existing = findById(alertType.trim());
		if (null == existing) {
			dto.setCreatedBy(userId);
			dto.setCreatedDate(new Date());
		} else {
			dto.setModifiedBy(userId);
			dto.setLastModifiedDate(new Date());
			dto.setCreatedBy(existing.getCreatedBy());
			dto.setCreatedDate(existing.getCreatedDate());
		}
		
		AlertMessage alert = converter.dtoToEntity(dto);
		alerMsgRepo.saveAndFlush(alert);
	}
	
	public void deleteById(String id) {
		alerMsgRepo.deleteById(id.trim());
	}
}