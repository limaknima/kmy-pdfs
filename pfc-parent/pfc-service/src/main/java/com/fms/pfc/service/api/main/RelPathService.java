package com.fms.pfc.service.api.main;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
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

import com.fms.pfc.domain.converter.main.RelPathConverter;
import com.fms.pfc.domain.dto.main.RelPathDto;
import com.fms.pfc.domain.model.main.RelPath;
import com.fms.pfc.repository.main.api.RelPathRepository;

@Service
public class RelPathService {

	private final Logger logger = LoggerFactory.getLogger(RelPathService.class);

	private RelPathRepository relPathRepo;
	private RelPathConverter relPathConv;

	@Autowired
	public RelPathService(RelPathRepository relPathRepo, RelPathConverter relPathConv) {
		super();
		this.relPathRepo = relPathRepo;
		this.relPathConv = relPathConv;
	}

	public RelPath findById(Integer id) {
		return relPathRepo.findById(id).orElse(null);
	}

	public RelPathDto findDtoById(Integer id) {
		RelPath rel = findById(id);
		return relPathConv.entityToDto(rel);
	}

	public List<RelPathDto> findAll() {
		List<RelPath> rels = relPathRepo.findAll();
		return relPathConv.entityToDtoList(rels);
	}

	public List<RelPathDto> findAllByParent(Integer parentId) {
		List<RelPathDto> rels = findAll();
		Predicate<RelPathDto> filter = arg0 -> parentId != 0 ? arg0.getFkCatgId() == parentId : (1 > 0);
		rels = rels.stream().filter(filter).collect(Collectors.toList());
		
		int rowNo = 0;
		for (RelPathDto relPathDto : rels) {
			relPathDto.setRowNo(rowNo);
			rowNo++;
			relPathDto.setIndicator("exist");
		}
		
		return rels;
	}

	@Transactional
	public void save(Integer parentId, String filePath, String prodFileFormat) {
		RelPathDto dto = new RelPathDto();
		dto.setFkCatgId(parentId);
		dto.setFilePath(filePath);
		dto.setProdFileFormat(prodFileFormat);

		RelPath rel = relPathConv.dtoToEntity(dto);
		relPathRepo.saveAndFlush(rel);
	}

	@Transactional
	public void delete(Integer id) {
		relPathRepo.deleteById(id);
	}

	@Transactional
	public void save(List<RelPathDto> relList, Integer parentId, boolean isCreate, Date currentDate,
			String userId, List<RelPathDto> relDelList) {

		if (isCreate) {
			relList.forEach(dto -> {
				dto.setFkCatgId(parentId);
				dto.setCreatorId(userId);
				dto.setCreatedDatetime(currentDate);
				relPathRepo.saveAndFlush(relPathConv.dtoToEntity(dto));
			});
		} else {
			// existing from db
			/*List<RelPathDto> db = findAllByParent(parentId);

			// current from ui
			List<RelPathDto> ui = relList;

			// compare db to current
			List<RelPathDto> delete = db.stream().filter(obj -> !ui.contains(obj)).collect(Collectors.toList());
			delete.forEach(obj -> relPathRepo.deleteById(obj.getPkRelPathId()));

			// compare current to db
			List<RelPathDto> add = ui.stream().filter(obj -> !db.contains(obj)).collect(Collectors.toList());
			add.forEach(obj -> {
				obj.setFkCatgId(parentId);
				obj.setCreatorId(userId);
				obj.setCreatedDatetime(currentDate);
			});
			add.forEach(obj -> relPathRepo.saveAndFlush(relPathConv.dtoToEntity(obj)));
			*/
						
			// For edit mode -> add to list of update to list
			for (RelPathDto relPathDto : relList) {
				if(StringUtils.equalsIgnoreCase(relPathDto.getIndicator(), "exist")) {
					RelPathDto existing =  findDtoById(relPathDto.getPkRelPathId());
					relPathDto.setCreatorId(existing.getCreatorId());
					relPathDto.setCreatedDatetime(existing.getCreatedDatetime());
					relPathDto.setModifierId(userId);
					relPathDto.setModifiedDatetime(currentDate);
					
				} else if(StringUtils.equalsIgnoreCase(relPathDto.getIndicator(), "new")) {
					relPathDto.setFkCatgId(parentId);
					relPathDto.setCreatorId(userId);
					relPathDto.setCreatedDatetime(currentDate);
				}
				relPathRepo.saveAndFlush(relPathConv.dtoToEntity(relPathDto));
			}
			
			// For edit mode -> delete from list
			for (RelPathDto relPathDto : relDelList) {
				relPathRepo.deleteById(relPathDto.getPkRelPathId());
			}

		}
		
		// create each folder 
		createPathDirectory(relDelList);

	}

	private void createPathDirectory(List<RelPathDto> relDelList) {
		for (RelPathDto relPathDto : relDelList) {
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
