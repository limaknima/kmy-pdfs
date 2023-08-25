package com.fms.pfc.service.api.main;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import javax.transaction.Transactional;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fms.pfc.domain.converter.main.ReglFileConverter;
import com.fms.pfc.domain.dto.main.ReglFileDto;
import com.fms.pfc.domain.dto.main.ReglFileRmInfoSearchDto;
import com.fms.pfc.domain.dto.main.ReglInfoDto;
import com.fms.pfc.domain.dto.main.ReglRmDto;
import com.fms.pfc.domain.model.main.ReglFile;
import com.fms.pfc.domain.model.main.ReglFileSearch;
import com.fms.pfc.domain.model.main.ReglInfo;
import com.fms.pfc.domain.model.main.ReglRm;
import com.fms.pfc.repository.main.api.ReglFileRepository;
import com.fms.pfc.repository.main.api.ReglFileSearchRepository;
import com.fms.pfc.service.api.base.CountryService;

@Service
public class ReglFileService {

	private static final Logger logger = LoggerFactory.getLogger(ReglFileService.class);

	private ReglFileRepository reglFileRepo;
	private ReglInfoService riServ;
	private ReglRmService rrServ;
	private CountryService ctyServ;
	private ReglFileConverter rfConv;
	private RegulationGroupService regGrpServ;
	private RegulationCategoryService regCatServ;
	private ReglFileSearchRepository rfsRepo;

	private static final String NEWLINE = "\n";
	private static final String BREAKLINE = "<br/>";
	private static final String MULTIPLE = "Multiple @[TYPE] ... ";

	@Autowired
	public ReglFileService(ReglFileRepository reglFileRepo, ReglInfoService riServ, ReglRmService rrServ,
			CountryService ctyServ, ReglFileConverter rfConv, RegulationGroupService regGrpServ,
			RegulationCategoryService regCatServ, ReglFileSearchRepository rfsRepo) {
		super();
		this.reglFileRepo = reglFileRepo;
		this.riServ = riServ;
		this.rrServ = rrServ;
		this.ctyServ = ctyServ;
		this.rfConv = rfConv;
		this.regGrpServ = regGrpServ;
		this.regCatServ = regCatServ;
		this.rfsRepo = rfsRepo;
	}

	// search one
	public ReglFile findById(int id) {
		return reglFileRepo.findById(id).orElse(null);
	}

	public ReglFileDto findDtoById(int id) {
		ReglFile rf = findById(id);
		ReglFileDto rfDto = rfConv.entityToDto(rf);
		rfDto.setCountryName(getCountryRef(rfDto.getCountryId()));
		setInfoListDto(rfDto);
		setRmListDto(rfDto);
		setRmChoiceFromDto(rfDto);
		return rfDto;
	}
	
	public ReglFileDto findDtoNoChildById(int id) {
		ReglFile rf = findById(id);
		ReglFileDto rfDto = rfConv.entityToDto(rf);
		rfDto.setCountryName(getCountryRef(rfDto.getCountryId()));
		
		return rfDto;
	}
	
	public Long countByCRC(Long crc) {		
		return reglFileRepo.countByCRC(crc);
	}
	
	public ReglFileDto findDtoByCRC(Long crc) {
		ReglFile rf = reglFileRepo.findByCRC(crc);

		if (Objects.isNull(rf))
			return null;

		ReglFileDto rfDto = rfConv.entityToDto(rf);
		return rfDto;
	}
	
	public List<ReglFileDto> findAllParentDto() {
		List<ReglFile> entList = reglFileRepo.findAll();
		List<ReglFileDto> dtoList = rfConv.entityToDtoList(entList);
		return dtoList;
	}
	
	public List<ReglFileDto> findAllParentNativeDto() {
		List<ReglFile> entList = reglFileRepo.findAllEntity();
		List<ReglFileDto> dtoList = rfConv.entityToDtoList(entList);
		return dtoList;
	}
	
	public List<ReglFileDto> findAllParentNativeDtoByCountry(String countryId) {
		List<ReglFile> entList = reglFileRepo.findAllEntityByCountry(countryId);
		List<ReglFileDto> dtoList = rfConv.entityToDtoList(entList);
		return dtoList;
	}
	
	public ReglFileDto findDtoNoChildNativeById(int id) {
		ReglFile rf = reglFileRepo.findEntityById(id);

		if (Objects.isNull(rf))
			return null;

		ReglFileDto rfDto = rfConv.entityToDto(rf);
		return rfDto;
	}

	// search all
	public List<ReglFileDto> findAllDto() {
		List<ReglFile> entList = reglFileRepo.findAll();
		List<ReglFileDto> dtoList = rfConv.entityToDtoList(entList);

		// set other detail
		dtoList.forEach(obj -> {
			obj.setCountryName(ctyServ.findOneById(obj.getCountryId()).getCountryName());
			obj.setDocCatGrpName(regGrpServ.findOneById(obj.getDocCatGrpId()).getGrpName());
			obj.setDocCatName(regCatServ.findOneById(obj.getDocCatId()).getCatName());
		});
		
		// set child
		dtoList.forEach(obj -> setInfoListDto(obj));
		dtoList.forEach(obj -> setRmListDto(obj));

		return dtoList;
	}
	
	public List<ReglFileDto> findAllDtoByCriteria(String countryId, String fileName, String fileNameExp) {
		List<ReglFile> entList = reglFileRepo.findAll();
		List<ReglFileDto> dtoList = rfConv.entityToDtoList(entList);

		if (StringUtils.isNotEmpty(countryId)) {
			dtoList = dtoList.stream().filter(reglFile -> StringUtils.equals(reglFile.getCountryId(), countryId))
					.collect(Collectors.toList());
		}

		if (StringUtils.isNotEmpty(fileName)) {
			dtoList = dtoList.stream()
					.filter(reglFile -> fileNameExpression(reglFile.getFileName(), fileName, fileNameExp))
					.collect(Collectors.toList());
		}

		// set other detail
		dtoList.forEach(obj -> {
			obj.setCountryName(ctyServ.findOneById(obj.getCountryId()).getCountryName());
			obj.setDocCatGrpName(regGrpServ.findOneById(obj.getDocCatGrpId()).getGrpName());
			obj.setDocCatName(regCatServ.findOneById(obj.getDocCatId()).getCatName());
		});

		// set child
		dtoList.forEach(obj -> setInfoListDto(obj));
		dtoList.forEach(obj -> setRmListDto(obj));

		return dtoList;
	}

	// search all by criteria
	public List<ReglFileRmInfoSearchDto> findAllFileRmInfoDto(Integer rmId, String countryId, String fileName, String fileNameExp) {
		logger.debug("findAllFileRmInfoDto() rmId={},countryId={},fileName={}", rmId, countryId, fileName);
		List<ReglFileRmInfoSearchDto> result = new ArrayList<ReglFileRmInfoSearchDto>();

		// search all
		if ((Objects.isNull(rmId) || rmId == 0) && StringUtils.isEmpty(countryId) && StringUtils.isEmpty(fileName)) {
			logger.debug("findAllFileRmInfoDto() all");
			List<ReglFileDto> reglFileAll = findAllDto();
			reglFileAll.forEach(reglFile -> {
				generateSearchResult(result, reglFile, null);

			});

		} else {

			// by country only
			if (StringUtils.isNotEmpty(countryId) && (Objects.isNull(rmId) || rmId == 0) && StringUtils.isEmpty(fileName)) {
				logger.debug("findAllFileRmInfoDto() country only");
				List<ReglFileDto> reglFileAll = findAllDtoByCriteria(countryId, null, null);
				/*reglFileAll = reglFileAll.stream()
						.filter(reglFile -> StringUtils.equals(reglFile.getCountryId(), countryId))
						.collect(Collectors.toList());*/
				reglFileAll.forEach(reglFile -> {
					generateSearchResult(result, reglFile, null);
				});

			} else if ((Objects.nonNull(rmId) && rmId != 0) && StringUtils.isEmpty(countryId) && StringUtils.isEmpty(fileName)) {
				logger.debug("findAllFileRmInfoDto() rm only");
				// by rm only
				List<ReglFileDto> reglFileAll = findAllDto();
				reglFileAll.forEach(reglFile -> {
					generateSearchResult(result, reglFile, rmId);

				});

			} else if (StringUtils.isNotEmpty(fileName) && (Objects.isNull(rmId) || rmId == 0) && StringUtils.isEmpty(countryId)) {
				logger.debug("findAllFileRmInfoDto() fileName only");
				// by fileName only
				List<ReglFileDto> reglFileAll = findAllDtoByCriteria(null, fileName, fileNameExp);
				/*reglFileAll = reglFileAll.stream()
						.filter(reglFile -> StringUtils.equals(reglFile.getFileName(), fileName))
						.collect(Collectors.toList());*/
				reglFileAll.forEach(reglFile -> {
					generateSearchResult(result, reglFile, null);
				});
			} else if (StringUtils.isNotEmpty(countryId) && (Objects.nonNull(rmId) && rmId != 0) && StringUtils.isEmpty(fileName)) {
				logger.debug("findAllFileRmInfoDto()  country,rm");
				// by country,rm
				List<ReglFileDto> reglFileAll = findAllDtoByCriteria(countryId, null, null);
				/*reglFileAll = reglFileAll.stream()
						.filter(reglFile -> StringUtils.equals(reglFile.getCountryId(), countryId))
						.collect(Collectors.toList());*/
				reglFileAll.forEach(reglFile -> {
					generateSearchResult(result, reglFile, rmId);
				});
				
			}else if (StringUtils.isNotEmpty(countryId) && StringUtils.isNotEmpty(fileName) && (Objects.isNull(rmId) || rmId == 0)) {
				logger.debug("findAllFileRmInfoDto()  country,file");
				// by country,file
				List<ReglFileDto> reglFileAll = findAllDtoByCriteria(countryId, fileName, fileNameExp);
				/*reglFileAll = reglFileAll.stream()
						.filter(reglFile -> StringUtils.equals(reglFile.getCountryId(), countryId))
						.filter(reglFile -> StringUtils.equals(reglFile.getFileName(), fileName))
						.collect(Collectors.toList());*/
				reglFileAll.forEach(reglFile -> {
					generateSearchResult(result, reglFile, null);
				});
				
			}else if (StringUtils.isNotEmpty(fileName) && (Objects.nonNull(rmId) && rmId != 0) && StringUtils.isEmpty(countryId)) {
				logger.debug("findAllFileRmInfoDto()  file,rm");
				// by file,rm
				List<ReglFileDto> reglFileAll = findAllDtoByCriteria(null, fileName, fileNameExp);
				/*reglFileAll = reglFileAll.stream()
						.filter(reglFile -> StringUtils.equals(reglFile.getFileName(), fileName))
						.collect(Collectors.toList());*/
				reglFileAll.forEach(reglFile -> {
					generateSearchResult(result, reglFile, rmId);
				});
			}
			else if ((Objects.nonNull(rmId) && rmId != 0) && StringUtils.isNotEmpty(countryId) && StringUtils.isNotEmpty(fileName) ) {
				logger.debug("findAllFileRmInfoDto()  rm,country,file");
				// by rm,country,file
				List<ReglFileDto> reglFileAll = findAllDtoByCriteria(countryId, fileName, fileNameExp);
				/*reglFileAll = reglFileAll.stream()
						.filter(reglFile -> StringUtils.equals(reglFile.getCountryId(), countryId))
						.filter(reglFile -> StringUtils.equals(reglFile.getFileName(), fileName))
						.collect(Collectors.toList());*/
				reglFileAll.forEach(reglFile -> {
					generateSearchResult(result, reglFile, rmId);
				});
			}

		}

		return result;
	}

	public List<ReglRmDto> findAllRmByParent(int reglFileId) {
		return rrServ.findAllByParent2(reglFileId);
	}
	
	public List<ReglFileSearch> searchReglFileByCritria(Integer rmId, String countryId, String fileName, String fileNameExp) {
		return rfsRepo.searchReglFileByCritria(fileName, fileNameExp, countryId, rmId);
	}
	
	public List<ReglFileSearch> searchFileAndInfoByCriteria(Integer rmId, String countryId) {
		return rfsRepo.searchFileAndInfoByCriteria(countryId, rmId);
	}
	
	public List<ReglFileSearch> searchReglFileByCritriaQA(Integer rmId, String countryId, String fileName, String fileNameExp, Integer grpId, Integer catId) {
		
		logger.debug("searchReglFileByCritriaQA() fileName={} fileNameExp={} countryId={} rmId={} grpId={} catId={}",fileName, fileNameExp, countryId, rmId, grpId, catId);
		
		return rfsRepo.searchReglFileByCritriaQA(fileName, fileNameExp, countryId, rmId, grpId, catId);
		
		// countryid, grpid, catid, filename, exp, rmname
	}

	// save
	// - insert
	// - update
	@Transactional
	public void saveReglFile(ReglFileDto rfDto, String userId, List<Integer> rmIds, List<ReglInfoDto> reglInfoList) {
		boolean isCreate = false;
		Date currentDate = new Date();
		// save parent
		if (Objects.isNull(rfDto.getReglFileId()) || 0 == rfDto.getReglFileId()) {
			isCreate = true;
			rfDto.setCreatorId(userId);
			rfDto.setCreatedDatetime(currentDate);
		} else {
			//ReglFileDto existing = findDtoById(rfDto.getReglFileId());
			ReglFileDto existing = findDtoNoChildNativeById(rfDto.getReglFileId());
			rfDto.setCreatorId(existing.getCreatorId());
			rfDto.setCreatedDatetime(existing.getCreatedDatetime());
			rfDto.setModifierId(userId);
			rfDto.setModifiedDatetime(currentDate);
		}

		ReglFile entity = rfConv.dtoToEntity(rfDto);
		reglFileRepo.saveAndFlush(entity);

		// save child
		// -rm list
		saveReglRmList(rmIds, entity.getReglFileId(), isCreate);
		// -info list
		saveReglInfoList(reglInfoList, entity.getReglFileId(), isCreate);
	}
	
	@Transactional
	public void saveReglFile(ReglFileDto rfDto, int rawMatlId, ReglInfoDto reglInfo, int type, String userId) {
		Date currentDate = new Date();
		if (type == 1) { // new
			rfDto.setCreatorId(userId);
			rfDto.setCreatedDatetime(currentDate);
		} else { // existing
			ReglFileDto existing = findDtoNoChildNativeById(rfDto.getReglFileId());
			rfDto.setCreatorId(existing.getCreatorId());
			rfDto.setCreatedDatetime(existing.getCreatedDatetime());
			rfDto.setModifierId(userId);
			rfDto.setModifiedDatetime(currentDate);
		}		

		ReglFile entity = rfConv.dtoToEntity(rfDto);
		reglFileRepo.saveAndFlush(entity);
		
		rrServ.createReglRm(rawMatlId, entity.getReglFileId());
		reglInfo.setReglFileId(entity.getReglFileId());
		riServ.createReglInfo(reglInfo);
	}

	// ----------------------------------------------------------------------------------------------//

	private void saveReglInfoList(List<ReglInfoDto> reglInfoList, int reglFileId, boolean isCreate) {
		riServ.saveReglInfo(reglInfoList, reglFileId, isCreate);
	}

	private void saveReglRmList(List<Integer> rmIds, int reglFileId, boolean isCreate) {
		rrServ.saveReglRm(rmIds, reglFileId, isCreate);
	}

	private String getCountryRef(String id) {
		String name = ctyServ.findOneById(id).getCountryName();
		return name;
	}

	private void setInfoListDto(ReglFileDto rfDto) {
		List<ReglInfoDto> ls = new ArrayList<ReglInfoDto>();
		ls = riServ.findAllByParent2(rfDto.getReglFileId());

		int rowNo = 0;
		for (ReglInfoDto riDto : ls) {
			riDto.setRowNo(rowNo);
			rowNo++;
			riDto.setIndicator("exist");
		}

		rfDto.setRegInfoList(ls);
	}

	private void setRmListDto(ReglFileDto rfDto) {
		List<ReglRmDto> ls = new ArrayList<ReglRmDto>();
		ls = rrServ.findAllByParent2(rfDto.getReglFileId());

		rfDto.setRegRmList(ls);
	}
	
	protected void setRmChoiceFromDto(ReglFileDto dto) {
		try {
			List<ReglRmDto> rm = dto.getRegRmList();
			List<String> rmid = rm.stream().map(obj -> obj.getRawMatlId().toString()).collect(Collectors.toList());

			String[] itemsArray = new String[rmid.size()];
			itemsArray = rmid.toArray(itemsArray);

			dto.setRmChoice(itemsArray);
		} catch (Exception ex) {
			dto.setRmChoice(null);
		}
	}

	private void generateSearchResult(List<ReglFileRmInfoSearchDto> result, ReglFileDto reglFile, Integer rmId) {
		ReglFileRmInfoSearchDto searchDto = new ReglFileRmInfoSearchDto();
		searchDto.setReglFileId(reglFile.getReglFileId());
		searchDto.setCountryName(reglFile.getCountryName());
		searchDto.setDocCatName(reglFile.getDocCatName());
		searchDto.setDocCatGrpName(reglFile.getDocCatGrpName());
		searchDto.setFileName(reglFile.getFileName());

		// multiple names
		//List<String> rmNames = new ArrayList<String>();
		List<Integer> rmIdList = new ArrayList<Integer>();
		reglFile.getRegRmList().forEach(reglRm -> {
			rmIdList.add(reglRm.getRawMatlId());
			//rmNames.add(reglRm.getRmName());
		});

		if (Objects.nonNull(rmId) && !rmIdList.contains(rmId))
			return;

		searchDto.setRmIdList(rmIdList);
		searchDto.setRmName(MULTIPLE.replace("@[TYPE]", "RM"));

		// multiple ref/remark
		/*List<String> refs = new ArrayList<String>();
		List<String> remarks = new ArrayList<String>();
		reglFile.getRegInfoList().forEach(reglInfo -> {
			refs.add(generateRefUrls(reglInfo.getRefUrl1(), reglInfo.getRefUrl2(), reglInfo.getRefUrl3(),
					reglInfo.getRefUrl4(), reglInfo.getRefUrl5()));
			remarks.add(reglInfo.getRemarks());
		});*/
		searchDto.setRefUrls(MULTIPLE.replace("@[TYPE]", "references"));
		searchDto.setRemarks(MULTIPLE.replace("@[TYPE]", "remarks"));

		result.add(searchDto);
	}

	private String generateRefUrls(String... urls) {
		if (Objects.isNull(urls) || urls.length != 5)
			return "";

		String ref1 = urls[0];
		String ref2 = urls[1];
		String ref3 = urls[2];
		String ref4 = urls[3];
		String ref5 = urls[4];

		StringBuffer sb = new StringBuffer();
		if (StringUtils.isNotEmpty(ref1))
			sb.append(ref1).append(BREAKLINE);
		if (StringUtils.isNotEmpty(ref2))
			sb.append(ref2).append(BREAKLINE);
		if (StringUtils.isNotEmpty(ref3))
			sb.append(ref3).append(BREAKLINE);
		if (StringUtils.isNotEmpty(ref4))
			sb.append(ref4).append(BREAKLINE);
		if (StringUtils.isNotEmpty(ref5))
			sb.append(ref5).append(BREAKLINE);

		return sb.toString();
	}
	
	private boolean fileNameExpression(String actualFileName, String fileName, String fileNameExp) {
		// 1-any, 2-end, 3-exact, 4-start

		if (StringUtils.equals("1", fileNameExp)) {
			return StringUtils.containsIgnoreCase(actualFileName, fileName);

		} else if (StringUtils.equals("2", fileNameExp)) {
			return StringUtils.endsWithIgnoreCase(actualFileName, fileName);

		} else if (StringUtils.equals("3", fileNameExp)) {
			return StringUtils.equalsIgnoreCase(actualFileName, fileName);

		} else if (StringUtils.equals("4", fileNameExp)) {
			return StringUtils.startsWithIgnoreCase(actualFileName, fileName);

		}

		return true;
	}

}
