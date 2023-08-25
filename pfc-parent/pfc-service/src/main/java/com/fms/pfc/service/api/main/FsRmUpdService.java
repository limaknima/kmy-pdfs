package com.fms.pfc.service.api.main;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import javax.transaction.Transactional;

import org.apache.commons.collections4.map.HashedMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fms.pfc.domain.converter.main.FsRmUpdConverter;
import com.fms.pfc.domain.dto.main.FlavorStatusDto;
import com.fms.pfc.domain.dto.main.FsRmUpdDto;
import com.fms.pfc.domain.dto.main.FsRmUpdResultDto;
import com.fms.pfc.domain.dto.main.PrIngredientDto;
import com.fms.pfc.domain.dto.main.ProductRecipeDto;
import com.fms.pfc.domain.model.main.FsRmUpd;
import com.fms.pfc.domain.model.main.PrIngredientAll;
import com.fms.pfc.repository.main.api.FsRmUpdRepository;
import com.fms.pfc.service.api.base.TrxHisService;

@Service
public class FsRmUpdService {

	private final Logger logger = LoggerFactory.getLogger(FsRmUpdService.class);

	private FsRmUpdRepository fsRepo;
	private FsRmUpdConverter fsConv;
	private FsRmUpdResultService fsRstServ;
	private PrIngService prIngServ;
	private ProductRecipeService prServ;
	private FlavorStatusService fsServ;
	private RawMaterialService rmServ;
	private TrxHisService trxHistServ;
	private PrIngredientAllService prIngAllServ;

	private static final String AMEND_FS_REMARK = "Flavor Status Amended";
	private static final int RM_REC_TYPE = 201;
	private static final int PR_REC_TYPE = 301;
	private static final int FUNC_TYPE_UPDATE = 2;
	private static final String MAP_KEY_NEW_FSID = "newFsId";
	private static final String MAP_KEY_NEW_FSNAME = "newFsName";
	private static final String MAP_KEY_NEW_FSRANK = "newFsRank";

	@Autowired
	public FsRmUpdService(FsRmUpdRepository fsRepo, FsRmUpdConverter fsConv, PrIngService prIngServ,
			ProductRecipeService prServ, FlavorStatusService fsServ, FsRmUpdResultService fsRstServ,
			RawMaterialService rmServ, TrxHisService trxHistServ, PrIngredientAllService prIngAllServ) {
		super();
		this.fsRepo = fsRepo;
		this.fsConv = fsConv;
		this.prIngServ = prIngServ;
		this.prServ = prServ;
		this.fsServ = fsServ;
		this.fsRstServ = fsRstServ;
		this.rmServ = rmServ;
		this.trxHistServ = trxHistServ;
		this.prIngAllServ = prIngAllServ;
	}

	public FsRmUpd findById(int id) {
		return fsRepo.findById(id).orElse(null);
	}

	public FsRmUpdDto findOneById(int id) {
		FsRmUpd fsRmUpd = findById(id);
		return fsConv.entityToDto(fsRmUpd);
	}

	public FsRmUpdDto findOneByRmId(int rmId) {
		List<FsRmUpdDto> allDtos = findAll();
		allDtos = allDtos.stream().filter(arg0 -> arg0.getRmId() == rmId).collect(Collectors.toList());
		FsRmUpdDto dto = allDtos.isEmpty() ? new FsRmUpdDto() : allDtos.get(0);
		return dto;
	}

	public List<FsRmUpdDto> findAll() {
		List<FsRmUpd> fsRmUpd = fsRepo.findAll();
		return fsConv.entityToDtoList(fsRmUpd);
	}

	public List<ProductRecipeDto> findAffectedPr(Integer rawMatlRefId, Integer newFlavStatId) {
		List<ProductRecipeDto> result = new ArrayList<ProductRecipeDto>();

		List<PrIngredientDto> prIngListDto = prIngServ.searchPrIngredientDto(0, rawMatlRefId, 0);
		List<Integer> prIds = prIngListDto.stream().map(arg0 -> arg0.getPrId()).collect(Collectors.toList());

		List<ProductRecipeDto> prListDto = prServ.searchProdcutRecipeDto("", "", "", "", 0, 0, "", "");
		prListDto = prListDto.stream().filter(arg0 -> (prIds.contains(arg0.getPrId()))).collect(Collectors.toList());
		setDto(prListDto, newFlavStatId, rawMatlRefId);

		result = prListDto;

		return result;
	}

	public List<FsRmUpdResultDto> findAffectedPrResult(Integer parentId) {
		return fsRstServ.findAllByParentId(parentId);
	}

	protected void setDto(List<ProductRecipeDto> prListDto, Integer newFlavStatId, Integer rawMatlRefId) {
		prListDto.forEach(arg0 -> {
			arg0.setFlavStatusName((Objects.nonNull(arg0.getFlavStatusId()) && arg0.getFlavStatusId() != 0)
					? fsServ.findOneById(arg0.getFlavStatusId()).getFsName()
					: null);
			arg0.setFlavStatusRank((Objects.nonNull(arg0.getFlavStatusId()) && arg0.getFlavStatusId() != 0)
					? fsServ.findOneById(arg0.getFlavStatusId()).getFsRank()
					: null);

			Map<String, Object> newFsMap = getNewFlavorStatus(arg0.getPrId(), rawMatlRefId, newFlavStatId);
			arg0.setNewFlavStatusId((Integer) newFsMap.get(MAP_KEY_NEW_FSID));
			arg0.setNewFlavStatusName((String) newFsMap.get(MAP_KEY_NEW_FSNAME));
			arg0.setNewFlavStatusRank((Integer) newFsMap.get(MAP_KEY_NEW_FSRANK));

		});
	}

	private Map<String, Object> getNewFlavorStatus(Integer prId, Integer rawMatlRefId, Integer newFlavStatId) {
		FlavorStatusDto newFlav = fsServ.findOneById(newFlavStatId);
		String newFlavName = newFlav.getFsName();
		int newFlavRank = newFlav.getFsRank();

		List<PrIngredientAll> prIngList = prIngAllServ.findIngredientsByParentId(prId);
		prIngList.forEach(obj -> {
			if (obj.getRefType() == RM_REC_TYPE) {
				if (obj.getRefId() == rawMatlRefId) {
					logger.debug("getNewFlavorStatus() rawMatlRefId={}, newFs={}, newFlavRank={}", rawMatlRefId,
							newFlavName, newFlavRank);
					obj.setFlavStatName(newFlavName);
					obj.setFlavStatRank(newFlavRank);
				}
			}
		});

		List<Integer> allRank = prIngList.stream().filter(obj -> Objects.nonNull(obj.getFlavStatRank()))
				.map(obj -> obj.getFlavStatRank()).collect(Collectors.toList());
		Integer maxRank = allRank.stream().max(Integer::compareTo).get();
		Map<String, Object> returnMap = new HashedMap<String, Object>();

		logger.debug("getNewFlavorStatus() maxRank={}", maxRank);

		if (Objects.nonNull(maxRank)) {
			newFlav = fsServ.findByRank(maxRank);
			returnMap.put(MAP_KEY_NEW_FSID, newFlav.getFsId());
			returnMap.put(MAP_KEY_NEW_FSNAME, newFlav.getFsName());
			returnMap.put(MAP_KEY_NEW_FSRANK, newFlav.getFsRank());
		} else {

		}

		return returnMap;
	}

	@Transactional
	public void saveFsRmUpd(Integer updId, Integer currFsId, Integer newFsId, String makerId, String checkerId,
			Integer updStatus, String userId, Integer rmId) {

		FsRmUpdDto dto = new FsRmUpdDto();
		dto.setRmId(rmId);
		dto.setCurrFsId(currFsId);
		dto.setNewFsId(newFsId);
		dto.setMakerId(makerId);
		dto.setCheckerId(checkerId);
		dto.setUpdStatus(updStatus);

		if (Objects.isNull(updId)) {
			// create
			dto.setCreatorId(userId);
			dto.setCreatedDatetime(new Date());

		} else {
			// update
			FsRmUpdDto existing = findOneById(updId);
			dto.setUpdId(updId);
			dto.setModifierId(userId);
			dto.setModifiedDatetime(new Date());
			dto.setCreatorId(existing.getCreatorId());
			dto.setCreatedDatetime(existing.getCreatedDatetime());

		}

		FsRmUpd entity = fsConv.dtoToEntity(dto);
		fsRepo.saveAndFlush(entity);
	}

	@Transactional
	public void saveFsRmUpd(FsRmUpdDto fsRmUpdDto, String userId, List<ProductRecipeDto> prDtoList,
			List<FsRmUpdResultDto> resultDtoList, boolean approveFlag, Date currentDate) {

		if (Objects.isNull(fsRmUpdDto.getUpdId())) {
			// create
			fsRmUpdDto.setCreatorId(userId);
			fsRmUpdDto.setCreatedDatetime(currentDate);

		} else {
			// update
			FsRmUpdDto existing = findOneById(fsRmUpdDto.getUpdId());
			fsRmUpdDto.setUpdId(existing.getUpdId());
			fsRmUpdDto.setModifierId(userId);
			fsRmUpdDto.setModifiedDatetime(currentDate);
			fsRmUpdDto.setCreatorId(existing.getCreatorId());
			fsRmUpdDto.setCreatedDatetime(existing.getCreatedDatetime());

		}

		FsRmUpd entity = fsConv.dtoToEntity(fsRmUpdDto);
		fsRepo.saveAndFlush(entity);

		if (!approveFlag && !prDtoList.isEmpty()) {
			fsRstServ.delFsRmUpdResultByParentId(entity.getUpdId());
			prDtoList.forEach(prDto -> {
				FsRmUpdResultDto rstDto = new FsRmUpdResultDto();
				rstDto.setUpdId(entity.getUpdId());
				rstDto.setPrId(prDto.getPrId());
				rstDto.setPrFsId(prDto.getFlavStatusId());
				rstDto.setNewPrFsId(prDto.getNewFlavStatusId());

				fsRstServ.saveFsRmUpdResult(rstDto);
			});
		}

		if (approveFlag && !resultDtoList.isEmpty()) {
			// update RM table flavor status
			rmServ.updRawMatl(fsRmUpdDto.getRmId(), fsRmUpdDto.getCurrFsId(), userId, currentDate);
			transactionHistory(currentDate, AMEND_FS_REMARK, userId, FUNC_TYPE_UPDATE, String.valueOf(fsRmUpdDto.getRmId()),
					RM_REC_TYPE, AMEND_FS_REMARK);

			// also need to update all affected PR flavor status
			resultDtoList.forEach(prd -> {
				if (!Objects.equals(prd.getPrFsId(), prd.getNewPrFsId())) {
					prServ.updProductRecipe(prd.getPrId(), prd.getNewPrFsId(), userId, currentDate);
					transactionHistory(currentDate, AMEND_FS_REMARK, userId, FUNC_TYPE_UPDATE,
							String.valueOf(prd.getPrId()), PR_REC_TYPE, AMEND_FS_REMARK);
				}
			});
		}
	}

	private void transactionHistory(Date logDateTime, String logDesc, String userId, int funcType, String recordRef,
			int recordType, String searchSt) {
		trxHistServ.saveTrxHistoryLog(logDateTime, logDesc, userId, funcType, recordRef, recordType, searchSt);
	}

}
