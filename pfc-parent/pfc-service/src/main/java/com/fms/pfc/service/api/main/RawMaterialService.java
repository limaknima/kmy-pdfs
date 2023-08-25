package com.fms.pfc.service.api.main;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import org.apache.commons.compress.utils.Lists;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fms.pfc.domain.converter.main.RawMaterialConverter;
import com.fms.pfc.domain.dto.LabelAndValueDto;
import com.fms.pfc.domain.dto.main.RawMaterialDto;
import com.fms.pfc.domain.model.main.RawMaterial;
import com.fms.pfc.domain.model.main.RawMaterialList;
import com.fms.pfc.exception.CommonException;
import com.fms.pfc.repository.main.api.RawMaterialListRepository;
import com.fms.pfc.repository.main.api.RawMaterialRepository;

@Service
public class RawMaterialService {
	
	private final Logger logger = LoggerFactory.getLogger(RawMaterialService.class);

	private RawMaterialRepository rawMatlRepo;	
	private RawMaterialListRepository rawMatlListRepo;
	private RawMaterialConverter rmConv;

	@Autowired
	public RawMaterialService(RawMaterialRepository rawMatlRepo, RawMaterialListRepository rawMatlListRepo, RawMaterialConverter rmConv) {
		super();
		this.rawMatlRepo = rawMatlRepo;
		this.rawMatlListRepo = rawMatlListRepo;
		this.rmConv = rmConv;
	}

	public List<RawMaterial> searchRawMaterial(String rawMaterial, String expr, int rawMatlId, int recordStatus) {
		return rawMatlRepo.searchRawMaterial(rawMaterial, expr, rawMatlId, recordStatus);
	}

	public void delRawMatl(int rawMatlId) {
		rawMatlRepo.delRawMatl(rawMatlId);
	}

	public void addRawMatl(String matlName, String tsNo, String remark, String remarkUser, int recordStatus,
			String insNo, String eNo, String femaNo, String jecfaNo, String casNo, String gmoStatus, Integer favStatus,
			String phoFlag, String creatorId, Date date) {
		rawMatlRepo.addRawMatl(matlName, tsNo, remark, remarkUser, recordStatus, insNo, eNo, femaNo, jecfaNo, casNo,
				gmoStatus, favStatus, phoFlag, creatorId, date);
	}

	public void updRawMatl(int rawMatlId, String matlName, String remark, int recordStatus, String insNo, String eNo,
			String femaNo, String jecfaNo, String casNo, String gmoStatus, Integer favStatus, String phoFlag,
			String creatorId, String remarkUser, String checkerId, Date date) {
		rawMatlRepo.updRawMatl(rawMatlId, matlName, remark, recordStatus, insNo, eNo, femaNo, jecfaNo, casNo, gmoStatus,
				favStatus, phoFlag, creatorId, remarkUser, checkerId, date);
	}

	public void updRawMatlStatus(int rawMatlId, int recordStatus, String modifierId, Date date) {
		rawMatlRepo.updRawMatlStatus(rawMatlId, recordStatus, modifierId, date);
	}

	public List<RawMaterialList> searchRawMaterialList(String rawMaterial, String expr1, String tsNo, String expr2,
			String manufacturer, String expr3, String supplier, String expr4, int recordStatus) {
		return rawMatlListRepo.searchRawMaterialList(rawMaterial, expr1, tsNo, expr2, manufacturer, expr3, supplier,
				expr4, recordStatus);
	}
	
	public void updRawMatlWithOptLock(int rawMatlId, String matlName, String remark, int recordStatus, String insNo,
			String eNo, String femaNo, String jecfaNo, String casNo, String gmoStatus, Integer favStatus, String phoFlag,
			String creatorId, String remarkUser, String checkerId, Date date, Integer currentLock) throws Exception {
		concurrentUpdateLockCheck(rawMatlId, currentLock);
		rawMatlRepo.updRawMatlWithOptLock(rawMatlId, matlName, remark, recordStatus, insNo, eNo, femaNo, jecfaNo, casNo,
				gmoStatus, favStatus, phoFlag, creatorId, remarkUser, checkerId, date, currentLock);
	}

	public void updRawMatlStatusWithOptLock(int rawMatlId, int recordStatus, String modifierId, Date date,
			Integer currentLock, String remarks) throws Exception {
		concurrentUpdateLockCheck(rawMatlId, currentLock);

		if (null == remarks || StringUtils.isEmpty(remarks))
			rawMatlRepo.updRawMatlStatusWithOptLock(rawMatlId, recordStatus, modifierId, date, currentLock);
		else
			rawMatlRepo.updRawMatlStatusWithOptLock(rawMatlId, recordStatus, modifierId, date, currentLock, remarks);
	}

	private void concurrentUpdateLockCheck(int rawMatlId, Integer currentLock) throws Exception {
		if (null == currentLock)
			return;

		Integer latesLock = searchRawMaterial("", "", rawMatlId, 0).get(0).getOptLock();
		logger.debug("concurrentUpdateLockCheck() rawMatlId={}, currLock={}, latestLock={}",rawMatlId, currentLock, latesLock);
		try {
			if (currentLock.intValue() != latesLock.intValue()) {
				throw new CommonException("Concurrent update occurred.", "Update Raw Material");
			}

		} catch (Exception e) {
			throw e;
		}
	}
	
	public List<LabelAndValueDto> getRmNameLabelAndValue(){
		List<LabelAndValueDto> lblVal = new ArrayList<LabelAndValueDto>();
		List<RawMaterial> rms = this.searchRawMaterial("", "", 0, 0);
		for (RawMaterial rm : rms) {
			LabelAndValueDto obj = new LabelAndValueDto(rm.getRawMatlName(), rm.getRawMatlId());
			lblVal.add(obj);
		}
		return lblVal;
	}
	
	public String getRmNamesBasedOnMultiIds(String rmIds) {
		String result = "";

		if (StringUtils.isEmpty(rmIds))
			return result;

		List<RawMaterial> rms = this.searchRawMaterial("", "", 0, 0);
		List<String> names = rms.stream()
				.filter(arg0 -> Arrays.asList(rmIds.split(",")).contains(String.valueOf(arg0.getRawMatlId())))
				.map(RawMaterial::getRawMatlName).collect(Collectors.toList());

		result = String.join(" <br /> ", names);

		return result;
	}
	
	public int countRawMatlByFlavorStatus(int flavStatusId) {
		return rawMatlRepo.countRawMatlByFlavorStatus(flavStatusId);
	}
	
	public RawMaterialDto findRawMatlDto(String rawMaterial, String expr, int rawMatlId, int recordStatus) {
		List<RawMaterial> rmList = searchRawMaterial(rawMaterial, expr, rawMatlId, recordStatus);
		RawMaterial entity = null;
		
		if(Objects.isNull(rmList) || rmList.isEmpty())
			return null;

		entity = rmList.get(0);
		RawMaterialDto rmDto = rmConv.entityToDto(entity);
		return rmDto;
	}
	
	public void updRawMatl(Integer rmId, Integer fsId, String modifierId, Date date) {
		rawMatlRepo.updRawMatl(rmId, fsId, modifierId, date);
	}

}