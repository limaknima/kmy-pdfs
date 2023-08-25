package com.fms.pfc.service.api.main;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fms.pfc.domain.converter.main.ReglInfoConverter;
import com.fms.pfc.domain.dto.main.RawMaterialDto;
import com.fms.pfc.domain.dto.main.ReglInfoDto;
import com.fms.pfc.domain.model.main.ReglInfo;
import com.fms.pfc.repository.main.api.ReglInfoRepository;

@Service
public class ReglInfoService {

	private ReglInfoRepository reglInfoRepo;
	private ReglInfoConverter riConv;
	private RawMaterialService rmServ;

	@Autowired
	public ReglInfoService(ReglInfoRepository reglInfoRepo, ReglInfoConverter riConv, RawMaterialService rmServ) {
		super();
		this.reglInfoRepo = reglInfoRepo;
		this.riConv = riConv;
		this.rmServ = rmServ;
	}

	public ReglInfo findById(int id) {
		return reglInfoRepo.findById(id).orElse(null);
	}

	public ReglInfoDto findDtoById(int id) {
		ReglInfo ri = findById(id);
		ReglInfoDto dto = riConv.entityToDto(ri);
		getRmName(dto);
		return dto;
	}
	
	public List<ReglInfoDto> findAllByParent2(int reglFileId) {
		List<ReglInfo> entList = reglInfoRepo.findByParentId(reglFileId);
		List<ReglInfoDto> dtoList = riConv.entityToDtoList(entList);
		dtoList.forEach(obj -> {
			getRmName(obj);
		});
		return dtoList;
	}

	public List<ReglInfoDto> findAllByParent(int reglFileId) {
		List<ReglInfo> entList = reglInfoRepo.findAll();
		List<ReglInfoDto> dtoList = riConv.entityToDtoList(entList);
		dtoList = dtoList.stream().filter(obj -> obj.getReglFileId() == reglFileId).collect(Collectors.toList());
		dtoList.forEach(obj -> {
			getRmName(obj);
		});
		return dtoList;
	}
	
	@Transactional
	public void createReglInfo(ReglInfoDto dto) {
		reglInfoRepo.saveAndFlush(riConv.dtoToEntity(dto));
	}

	@Transactional
	public void saveReglInfo(List<ReglInfoDto> reglInfoList, int reglFileId, boolean isCreate) {
		if (isCreate) {
			reglInfoList.forEach(dto -> {
				dto.setReglFileId(reglFileId);
				reglInfoRepo.saveAndFlush(riConv.dtoToEntity(dto));
			});
		} else {
			// existing
			List<ReglInfoDto> db = findAllByParent2(reglFileId);
			// current
			List<ReglInfoDto> ui = reglInfoList;

			/// compare db to current
			List<ReglInfoDto> delete = db.stream().filter(del -> !ui.contains(del)).collect(Collectors.toList());
			delete.forEach(obj -> reglInfoRepo.deleteById(obj.getReglInfoId()));

			// compare current to db
			List<ReglInfoDto> add = ui.stream().filter(obj -> !db.contains(obj)).collect(Collectors.toList());
			add.forEach(obj -> obj.setReglFileId(reglFileId));
			add.forEach(obj -> reglInfoRepo.saveAndFlush(riConv.dtoToEntity(obj)));

		}
	}

	protected void getRmName(ReglInfoDto dto) {
		if (Objects.isNull(dto.getRmId()))
			return;

		String name = "";
		RawMaterialDto rmDto = rmServ.findRawMatlDto("", "", dto.getRmId(), 0);
		if (Objects.nonNull(rmDto))
			name = rmDto.getRawMatlName();

		dto.setRmName(name);
	}

}
