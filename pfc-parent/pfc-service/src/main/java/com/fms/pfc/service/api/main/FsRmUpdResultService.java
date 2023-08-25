package com.fms.pfc.service.api.main;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fms.pfc.domain.converter.main.FsRmUpdResultConverter;
import com.fms.pfc.domain.dto.main.FsRmUpdResultDto;
import com.fms.pfc.domain.model.main.FsRmUpdResult;
import com.fms.pfc.repository.main.api.FsRmUpdResultRepository;

@Service
public class FsRmUpdResultService {

	private FsRmUpdResultRepository fsUdpRstRepo;
	private FsRmUpdResultConverter fsUpdRstConv;
	private ProductRecipeService prServ;
	private FlavorStatusService fsServ;

	@Autowired
	public FsRmUpdResultService(FsRmUpdResultRepository fsUdpRstRepo, FsRmUpdResultConverter fsUpdRstConv,
			ProductRecipeService prServ, FlavorStatusService fsServ) {
		super();
		this.fsUdpRstRepo = fsUdpRstRepo;
		this.fsUpdRstConv = fsUpdRstConv;
		this.prServ = prServ;
		this.fsServ = fsServ;
	}

	public FsRmUpdResultDto findOneById(Integer id) {
		FsRmUpdResult entity = fsUdpRstRepo.findById(id).orElse(null);
		return Objects.nonNull(entity) ? fsUpdRstConv.entityToDto(entity) : null;
	}

	public List<FsRmUpdResultDto> findAllByParentId(Integer parentId) {
		List<FsRmUpdResult> entityList = fsUdpRstRepo.findAll();
		List<FsRmUpdResultDto> dtoList = fsUpdRstConv.entityToDtoList(entityList);
		dtoList = dtoList.stream().filter(arg0 -> arg0.getUpdId() == parentId).collect(Collectors.toList());
		dtoList.forEach(arg0 -> {
			arg0.setPrName(prServ.findProductRecipeDto("", "", "", "", arg0.getPrId(), 0, "", "").getPrName());
			arg0.setFlavStatusName(
					Objects.nonNull(arg0.getPrFsId()) ? fsServ.findOneById(arg0.getPrFsId()).getFsName() : "");
			arg0.setNewFlavStatusName(
					Objects.nonNull(arg0.getNewPrFsId()) ? fsServ.findOneById(arg0.getNewPrFsId()).getFsName()
							: arg0.getFlavStatusName()); // set new name same with old name for display purpose
		});

		return dtoList;

	}

	@Transactional
	public void saveFsRmUpdResult(FsRmUpdResultDto dto) {
		FsRmUpdResult entity = fsUpdRstConv.dtoToEntity(dto);
		fsUdpRstRepo.saveAndFlush(entity);
	}

	public void delFsRmUpdResultByParentId(Integer parentId) {
		fsUdpRstRepo.delFsRmUpdResultByParentId(parentId);
	}

}
