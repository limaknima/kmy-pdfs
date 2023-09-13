package com.fms.pfc.service.api.main;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import javax.transaction.Transactional;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fms.pfc.domain.converter.main.RelPathConverter2;
import com.fms.pfc.domain.dto.main.RelPathDto2;
import com.fms.pfc.domain.model.main.RelPath2;
import com.fms.pfc.repository.main.api.RelPathRepository2;

@Service
public class RelPathService2 {

	private final Logger logger = LoggerFactory.getLogger(RelPathService2.class);

	private RelPathRepository2 relPathRepo;
	private RelPathConverter2 relPathConv;

	@Autowired
	public RelPathService2(RelPathRepository2 relPathRepo, RelPathConverter2 relPathConv) {
		super();
		this.relPathRepo = relPathRepo;
		this.relPathConv = relPathConv;
	}

	public RelPath2 findById(Integer id) {
		return relPathRepo.findById(id).orElse(null);
	}

	public RelPathDto2 findDtoById(Integer id) {
		RelPath2 rel = findById(id);
		return relPathConv.entityToDto(rel);
	}

	public List<RelPathDto2> findAll() {
		List<RelPath2> rels = relPathRepo.findAll();
		return relPathConv.entityToDtoList(rels);
	}

	public List<RelPathDto2> findAllByParent(Integer parentId) {
		List<RelPathDto2> rels = findAll();
		Predicate<RelPathDto2> filter = arg0 -> parentId != 0 ? arg0.getFkCatgId() == parentId : (1 > 0);
		rels = rels.stream().filter(filter).collect(Collectors.toList());

		int rowNo = 0;
		for (RelPathDto2 relPathDto : rels) {
			relPathDto.setRowNo(rowNo);
			rowNo++;
			relPathDto.setIndicator("exist");
		}

		return rels;
	}
	
	public List<RelPathDto2> searchByCriteria(Integer catgId, String hmodel, String year, String mth, String day, String prodLn,
			String seq, Integer procType){
		List<RelPathDto2> result = new ArrayList<RelPathDto2>();
		List<RelPath2> rels = relPathRepo.searchByCriteria(catgId, hmodel, year, mth, day, prodLn, seq, procType);
		result = relPathConv.entityToDtoList(rels);
		
		int rowNo = 0;
		for (RelPathDto2 relPathDto : result) {
			relPathDto.setRowNo(rowNo);
			rowNo++;
			relPathDto.setIndicator("exist");
		}
		
		return result;
	}

	@Transactional
	public void save(Integer parentId, String filePath, String prodFileFormat) {
		RelPathDto2 dto = new RelPathDto2();
		dto.setFkCatgId(parentId);
		dto.setFilePath(filePath);
		dto.setProdFileFormat(prodFileFormat);

		RelPath2 rel = relPathConv.dtoToEntity(dto);
		relPathRepo.saveAndFlush(rel);
	}

	@Transactional
	public void delete(Integer id) {
		relPathRepo.deleteById(id);
	}

	@Transactional
	public void save(List<RelPathDto2> relList, Integer parentId, boolean isCreate, Date currentDate, String userId,
			List<RelPathDto2> relDelList) {

		if (isCreate) {
			relList.forEach(dto -> {
				dto.setFkCatgId(parentId);
				dto.setCreatorId(userId);
				dto.setCreatedDatetime(currentDate);
				relPathRepo.saveAndFlush(relPathConv.dtoToEntity(dto));
			});
		} else {

			// For edit mode -> add to list of update to list
			for (RelPathDto2 relPathDto : relList) {
				if (StringUtils.equalsIgnoreCase(relPathDto.getIndicator(), "exist")) {
					RelPathDto2 existing = findDtoById(relPathDto.getPkRelPathId());
					relPathDto.setCreatorId(existing.getCreatorId());
					relPathDto.setCreatedDatetime(existing.getCreatedDatetime());
					relPathDto.setModifierId(userId);
					relPathDto.setModifiedDatetime(currentDate);

				} else if (StringUtils.equalsIgnoreCase(relPathDto.getIndicator(), "new")) {
					relPathDto.setFkCatgId(parentId);
					relPathDto.setCreatorId(userId);
					relPathDto.setCreatedDatetime(currentDate);
				}
				relPathRepo.saveAndFlush(relPathConv.dtoToEntity(relPathDto));
			}

			// For edit mode -> delete from list
			for (RelPathDto2 relPathDto : relDelList) {
				relPathRepo.deleteById(relPathDto.getPkRelPathId());
			}

		}

		// create each folder
		createPathDirectory(relList);

	}

	private void createPathDirectory(List<RelPathDto2> relList) {
		for (RelPathDto2 relPathDto : relList) {
			Path pt = Paths.get(relPathDto.getFilePath());
			try {
				Files.createDirectories(pt);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				logger.error("createPathDirectory() Error! dir={}", relPathDto.getFilePath());
			}
		}
	}

}
