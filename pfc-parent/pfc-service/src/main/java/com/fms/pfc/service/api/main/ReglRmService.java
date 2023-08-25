package com.fms.pfc.service.api.main;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fms.pfc.domain.converter.main.ReglRmConverter;
import com.fms.pfc.domain.dto.main.ReglRmDto;
import com.fms.pfc.domain.model.main.ReglRm;
import com.fms.pfc.repository.main.api.ReglRmRepository;

@Service
public class ReglRmService {

	private ReglRmRepository reglRmRepo;
	private ReglRmConverter rrConv;
	private RawMaterialService rmServ;

	@Autowired
	public ReglRmService(ReglRmRepository reglRmRepo, ReglRmConverter rrConv, RawMaterialService rmServ) {
		super();
		this.reglRmRepo = reglRmRepo;
		this.rrConv = rrConv;
		this.rmServ = rmServ;
	}

	public ReglRm findById(int id) {
		return reglRmRepo.findById(id).orElse(null);
	}

	public ReglRmDto findDtoById(int id) throws Exception {
		ReglRm rr = findById(id);
		ReglRmDto dto = rrConv.entityToDto(rr);
		dto.setRmName(getRmName(dto.getRawMatlId()));
		return dto;
	}

	private String getRmName(int rawMatlId) throws Exception {
		String name = rmServ.searchRawMaterial("", "", rawMatlId, 0).get(0).getRawMatlName();
		return name;
	}

	public List<ReglRmDto> findAllByParent(int reglFileId) {
		List<ReglRm> entList = reglRmRepo.findAll();
		List<ReglRmDto> dtoList = rrConv.entityToDtoList(entList);
		dtoList = dtoList.stream().filter(obj -> obj.getReglFileId() == reglFileId).collect(Collectors.toList());

		dtoList.forEach(obj -> {
			try {
				obj.setRmName(getRmName(obj.getRawMatlId()));
			} catch (Exception e) {
				e.printStackTrace();
			}
		});

		return dtoList;

	}
	
	public List<ReglRmDto> findAllByParent2(int reglFileId) {
		List<ReglRm> entList = reglRmRepo.findByParentId(reglFileId);
		List<ReglRmDto> dtoList = rrConv.entityToDtoList(entList);
		dtoList.forEach(obj -> {
			try {
				obj.setRmName(getRmName(obj.getRawMatlId()));
			} catch (Exception e) {
				e.printStackTrace();
			}
		});

		return dtoList;

	}
	
	@Transactional
	public void createReglRm(int rawMatlId, int reglFileId) {
		ReglRm entity = new ReglRm();
		entity.setReglFileId(reglFileId);
		entity.setRawMatlId(rawMatlId);
		reglRmRepo.saveAndFlush(entity);
	}

	@Transactional
	public void saveReglRm(List<Integer> rmIds, int reglFileId, boolean isCreate) {
		if (isCreate) {
			// create
			rmIds.forEach(rawMatlId -> {
				ReglRm entity = new ReglRm();
				entity.setReglFileId(reglFileId);
				entity.setRawMatlId(rawMatlId);
				reglRmRepo.saveAndFlush(entity);
			});

		} else {
			// update

			// existing
			List<ReglRmDto> db = findAllByParent2(reglFileId);
			// current
			List<ReglRmDto> ui = new ArrayList<ReglRmDto>();
			rmIds.forEach(rawMatlId -> {
				ReglRmDto dto = new ReglRmDto();
				dto.setReglFileId(reglFileId);
				dto.setRawMatlId(rawMatlId);
				ui.add(dto);
			});

			// compare db to curr
			List<ReglRmDto> delete = db.stream().filter(dbObj -> !ui.contains(dbObj)).collect(Collectors.toList());
			// delete.forEach(obj -> reglRmRepo.deleteByFileAndRmId(obj.getReglFileId(),
			// obj.getRawMatlId()));
			delete.forEach(obj -> reglRmRepo.deleteById(obj.getReglRmId()));

			// compare curr to db
			List<ReglRmDto> add = ui.stream().filter(currObj -> !db.contains(currObj)).collect(Collectors.toList());
			add.forEach(obj -> obj.setReglFileId(reglFileId));
			add.forEach(obj -> reglRmRepo.saveAndFlush(rrConv.dtoToEntity(obj)));
		}

	}

}
