package com.fms.pfc.service.api.main;

import java.util.Date;
import java.util.List;
import java.util.Objects;

import javax.transaction.Transactional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fms.pfc.domain.converter.main.FoldCatgConfConverter2;
import com.fms.pfc.domain.dto.main.FoldCatgConfDto2;
import com.fms.pfc.domain.dto.main.RelPathDto2;
import com.fms.pfc.domain.model.main.FoldCatgConf2;
import com.fms.pfc.repository.main.api.FoldCatgConfRepository2;

@Service
public class FoldCatgConfService2 {

	private final Logger logger = LoggerFactory.getLogger(FoldCatgConfService2.class);

	private FoldCatgConfRepository2 foldCatgRepo;
	private FoldCatgConfConverter2 foldCatgConv;
	private RelPathService2 relPathServ;

	@Autowired
	public FoldCatgConfService2(FoldCatgConfRepository2 foldCatgRepo, 
			FoldCatgConfConverter2 foldCatgConv, RelPathService2 relPathServ) {
		super();
		this.foldCatgRepo = foldCatgRepo;
		this.foldCatgConv = foldCatgConv;
		this.relPathServ = relPathServ;
	}

	public FoldCatgConf2 findById(int id) {
		return foldCatgRepo.findById(id).orElse(null);
	}

	public FoldCatgConfDto2 findDtoById(int id) {
		FoldCatgConf2 obj = findById(id);
		return foldCatgConv.entityToDto(obj);

	}

	public List<FoldCatgConfDto2> findAllDto() {
		List<FoldCatgConf2> ls = foldCatgRepo.findAll();
		return foldCatgConv.entityToDtoList(ls);
	}

	public List<FoldCatgConfDto2> searchByCriteria(String hpl, String format) {
		List<FoldCatgConf2> ls = foldCatgRepo.searchByCriteria(hpl,format);
		return foldCatgConv.entityToDtoList(ls);
	}
	
	public RelPathDto2 findRelPathById(Integer id) {
		return relPathServ.findDtoById(id);
	}
	
	public List<RelPathDto2> findRelPathListByParent(Integer parentId) {
		return relPathServ.findAllByParent(parentId);
	}
	
	public List<RelPathDto2> searchRelPathByCriteria(Integer catgId, String hmodel, String year, String mth, String day,
			String prodLn, String seq, Integer procType) {
		logger.debug("searchRelPathByCriteria() catgId={}, hmodel={}, year={}, mth={}, day={}, prodLn={}, seq={}",
				catgId, hmodel, year, mth, day, prodLn, seq);
		return relPathServ.searchByCriteria(catgId, Objects.isNull(hmodel) ? "" : hmodel,
				Objects.isNull(year) ? "" : year, Objects.isNull(mth) ? "" : mth, Objects.isNull(day) ? "" : day,
				Objects.isNull(prodLn) ? "" : prodLn, Objects.isNull(seq) ? "" : seq,
				Objects.isNull(procType) ? 0 : procType);
	}

	@Transactional
	public Integer save(FoldCatgConfDto2 dto, String userId, List<RelPathDto2> relList, List<RelPathDto2> relDelList) {
		boolean isCreate = false;
		Date currentDate = new Date();
		// save parent
		if (Objects.isNull(dto.getPkCatgId()) || 0 == dto.getPkCatgId()) {
			isCreate = true;
			dto.setCreatorId(userId);
			dto.setCreatedDatetime(currentDate);
		} else {
			FoldCatgConfDto2 existing = findDtoById(dto.getPkCatgId());
			dto.setCreatorId(existing.getCreatorId());
			dto.setCreatedDatetime(existing.getCreatedDatetime());
			dto.setModifierId(userId);
			dto.setModifiedDatetime(currentDate);
		}

		FoldCatgConf2 entity = foldCatgConv.dtoToEntity(dto);
		foldCatgRepo.saveAndFlush(entity);

		// save child
		saveRelPath(relList, entity.getPkCatgId(), isCreate, currentDate, userId, relDelList);

		return entity.getPkCatgId();
	}

	@Transactional
	public void delete(Integer id) {
		foldCatgRepo.deleteById(id);
	}

	@Transactional
	public void deleteAll(Integer id) {
		List<RelPathDto2> paths = findRelPathListByParent(id);
		paths.forEach(arg0 -> relPathServ.delete(arg0.getPkRelPathId()));
		foldCatgRepo.deleteById(id);
	}

	private void saveRelPath(List<RelPathDto2> relList, Integer parentId, boolean isCreate, Date currentDate,
			String userId, List<RelPathDto2> relDelList) {
		relPathServ.save(relList, parentId, isCreate, currentDate, userId, relDelList);
	}

}
