package com.fms.pfc.service.api.main;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import javax.transaction.Transactional;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fms.pfc.domain.converter.main.FlavorStatusConverter;
import com.fms.pfc.domain.dto.main.FlavorStatusDto;
import com.fms.pfc.domain.model.main.FlavorStatus;
import com.fms.pfc.repository.main.api.FlavorStatusRepository;

@Service
public class FlavorStatusService {

	private FlavorStatusRepository flRepo;
	private FlavorStatusConverter flConverter;
	private RawMaterialService rmServ;

	@Autowired
	public FlavorStatusService(FlavorStatusRepository flRepo, FlavorStatusConverter flConverter, RawMaterialService rmServ) {
		super();
		this.flRepo = flRepo;
		this.flConverter = flConverter;
		this.rmServ = rmServ;
	}

	public FlavorStatus findById(int id) {
		return flRepo.findById(id).orElse(null);
	}

	public FlavorStatusDto findByName(String fsName) {
		List<FlavorStatusDto> allFs = findAll();

		Predicate<FlavorStatusDto> nameFilter = arg0 -> (!StringUtils.isEmpty(fsName))
				? (arg0.getFsName().equalsIgnoreCase(fsName))
				: (1 > 0);

		allFs = allFs.stream().filter(nameFilter).collect(Collectors.toList());

		return (null != allFs && !allFs.isEmpty()) ? allFs.get(0) : null;
	}

	public FlavorStatusDto findByRank(int fsRank) {
		List<FlavorStatusDto> allFs = findAll();

		Predicate<FlavorStatusDto> rankFilter = arg0 -> (arg0.getFsRank() == fsRank);

		allFs = allFs.stream().filter(rankFilter).collect(Collectors.toList());

		return (null != allFs && !allFs.isEmpty()) ? allFs.get(0) : null;
	}

	public FlavorStatusDto findOneById(int id) {
		FlavorStatus fs = findById(id);
		return flConverter.entityToDto(fs);
	}

	public List<FlavorStatusDto> findAll() {
		List<FlavorStatus> allFs = flRepo.findAll();
		return flConverter.entityToDtoList(allFs);
	}

	public List<FlavorStatusDto> findByCriteria(int fsRank, String fsName, String fsDesc) {
		List<FlavorStatus> allFs = flRepo.findAll();

		Predicate<FlavorStatus> rankFilter = arg0 -> (fsRank != 0) ? (arg0.getFsRank() == fsRank) : (1 > 0);
		Predicate<FlavorStatus> nameFilter = arg0 -> (!StringUtils.isEmpty(fsName))
				? (arg0.getFsName().equalsIgnoreCase(fsName))
				: (1 > 0);
		Predicate<FlavorStatus> descFilter = arg0 -> (!StringUtils.isEmpty(fsDesc))
				? (arg0.getFsDesc().equalsIgnoreCase(fsDesc))
				: (1 > 0);

		allFs = allFs.stream().filter(rankFilter).filter(nameFilter).filter(descFilter).collect(Collectors.toList());

		return flConverter.entityToDtoList(allFs);
	}

	public List<FlavorStatusDto> searchByCriteria(String fsName, String fsDesc, String fsNameExp, String fsDescExp,
			String fsRank) {
		List<FlavorStatus> allFs = flRepo.searchByCriteria(fsName, fsDesc, fsNameExp, fsDescExp, fsRank);
		return flConverter.entityToDtoList(allFs);
	}
	
	public List<Integer> loadRankSelectItems() {
		List<Integer> ranks = new ArrayList<Integer>();
		List<FlavorStatusDto> allDtos = findAll();
		ranks = allDtos.stream().map(arg0 -> arg0.getFsRank()).distinct().collect(Collectors.toList());
		return ranks;
	}

	@Transactional
	public void saveFlavorStatus(Integer fsId, int fsRank, String fsName, String fsDesc, String userId) {
		FlavorStatusDto dto = new FlavorStatusDto();
		dto.setFsRank(fsRank);
		dto.setFsName(fsName);
		dto.setFsDesc(fsDesc);

		if (null == fsId) {
			dto.setCreatorId(userId);
			dto.setCreatedDate(new Date());
		} else {
			FlavorStatusDto existing = findOneById(fsId);
			dto.setFsId(fsId);
			dto.setModifierId(userId);
			dto.setModifiedDatetime(new Date());
			dto.setCreatorId(existing.getCreatorId());
			dto.setCreatedDate(existing.getCreatedDate());
		}

		FlavorStatus fs = flConverter.dtoToEntity(dto);
		flRepo.saveAndFlush(fs);
	}

	public void deleteById(int id) {
		flRepo.deleteById(id);
	}
	
	public int countRawMatlByFlavorStatus(int flavStatusId) {
		return rmServ.countRawMatlByFlavorStatus(flavStatusId);
	}
}
