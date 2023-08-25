package com.fms.pfc.service.api.main;

import java.util.Date;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import javax.transaction.Transactional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fms.pfc.domain.converter.main.UsbUsrConverter;
import com.fms.pfc.domain.dto.main.UsbUsrDto;
import com.fms.pfc.domain.model.main.UsbUsr;
import com.fms.pfc.repository.main.api.UsbUsrRepository;

@Service
public class UsbUsrService {

	private final Logger logger = LoggerFactory.getLogger(UsbUsrService.class);

	private UsbUsrRepository usbUsrRepo;
	private UsbUsrConverter usbUsrConv;

	@Autowired
	public UsbUsrService(UsbUsrRepository usbUsrRepo, UsbUsrConverter usbUsrConv) {
		super();
		this.usbUsrRepo = usbUsrRepo;
		this.usbUsrConv = usbUsrConv;
	}

	public UsbUsr findById(Integer id) {
		return usbUsrRepo.findById(id).orElse(null);
	}

	public UsbUsrDto findDtoById(Integer id) {
		UsbUsr rel = findById(id);
		return usbUsrConv.entityToDto(rel);
	}

	public List<UsbUsrDto> findAll() {
		List<UsbUsr> models = usbUsrRepo.findAll();
		
		logger.debug("findAll UsbUsrDto "+ models.size());
		
		return usbUsrConv.entityToDtoList(models);
	}

	public List<UsbUsrDto> findAllByParent(Integer parentId) {
		List<UsbUsrDto> models = findAll();
		Predicate<UsbUsrDto> filter = arg0 -> parentId != 0 ? arg0.getFkUconfId() == parentId : (1 > 0);
		models = models.stream().filter(filter).collect(Collectors.toList());
		
		logger.debug("findAllByParent UsbUsrDto "+ models.size());
		
		return models;
	}

	@Transactional
	public void save(Integer parentId, String usrId) {
		UsbUsrDto dto = new UsbUsrDto();
		dto.setFkUconfId(parentId);
		dto.setFkUsrId(usrId);

		UsbUsr rel = usbUsrConv.dtoToEntity(dto);
		usbUsrRepo.saveAndFlush(rel);
	}

	@Transactional
	public void delete(Integer id) {
		usbUsrRepo.deleteById(id);
	}

	@Transactional
	public void save(List<String> usrIdList, Integer parentId, Date currentDate, String userId) {

		if (!usrIdList.isEmpty()) {
			// delete by parent
			usbUsrRepo.deleteUsbUsrByParentId(parentId);
		}

		for (String usr : usrIdList) {
			UsbUsrDto uuDto = new UsbUsrDto();
			uuDto.setFkUconfId(parentId);
			uuDto.setFkUsrId(usr);
			uuDto.setCreatorId(userId);
			uuDto.setCreatedDatetime(currentDate);

			UsbUsr entity = usbUsrConv.dtoToEntity(uuDto);
			usbUsrRepo.saveAndFlush(entity);
		}
	}

	@Transactional
	public void save(List<UsbUsrDto> usbUsrList, Integer parentId, boolean isCreate, Date currentDate, String userId) {

		if (isCreate) {
			usbUsrList.forEach(dto -> {
				dto.setFkUconfId(parentId);
				dto.setCreatorId(userId);
				dto.setCreatedDatetime(currentDate);
				usbUsrRepo.saveAndFlush(usbUsrConv.dtoToEntity(dto));
			});
		} else {
			// existing from db
			List<UsbUsrDto> db = findAllByParent(parentId);

			// current from ui
			List<UsbUsrDto> ui = usbUsrList;

			// compare db to current
			List<UsbUsrDto> delete = db.stream().filter(obj -> !ui.contains(obj)).collect(Collectors.toList());
			delete.forEach(obj -> usbUsrRepo.deleteById(obj.getPkUsbusrId()));

			// compare current to db
			List<UsbUsrDto> add = ui.stream().filter(obj -> !db.contains(obj)).collect(Collectors.toList());
			add.forEach(obj -> {
				obj.setFkUconfId(parentId);
				obj.setCreatorId(userId);
				obj.setCreatedDatetime(currentDate);
			});
			add.forEach(obj -> usbUsrRepo.saveAndFlush(usbUsrConv.dtoToEntity(obj)));

		}

	}
}
