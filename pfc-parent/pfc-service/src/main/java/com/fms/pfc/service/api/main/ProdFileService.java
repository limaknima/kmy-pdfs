package com.fms.pfc.service.api.main;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import javax.transaction.Transactional;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.fms.pfc.domain.converter.main.ProdFileConverter;
import com.fms.pfc.domain.dto.main.FileTypeSzDto;
import com.fms.pfc.domain.dto.main.FoldCatgConfDto2;
import com.fms.pfc.domain.dto.main.HplModelDto;
import com.fms.pfc.domain.dto.main.ProdFileDto;
import com.fms.pfc.domain.dto.main.RelPathDto2;
import com.fms.pfc.domain.dto.main.UsbConfDto;
import com.fms.pfc.domain.model.Usr;
import com.fms.pfc.domain.model.main.ProdFile;
import com.fms.pfc.domain.model.main.ProdFileSearch;
import com.fms.pfc.repository.main.api.ProdFileRepository;
import com.fms.pfc.repository.main.api.ProdFileSearchRepository;

@Service
public class ProdFileService {

	private final Logger logger = LoggerFactory.getLogger(ProdFileService.class);

	private ProdFileRepository prodFileRepo;
	private ProdFileConverter prodFileConv;
	private ProdFileSearchRepository prodFileSearchRepo;
	private HplModelService hplModelServ;
	private UsbConfService usbConfServ;
	private FileTypeSzService ftSzServ;
	private RelPathService2 pathServ;
	private FoldCatgConfService2 foldConfServ;
	
	@Value("${data.root.dir}")
	private String DEFAULT_DATA_PATH;

	@Autowired
	public ProdFileService(ProdFileRepository prodFileRepo, ProdFileConverter prodFileConv,
			ProdFileSearchRepository prodFileSearchRepo, HplModelService hplModelServ, UsbConfService usbConfServ,
			FileTypeSzService ftSzServ, RelPathService2 pathServ, FoldCatgConfService2 foldConfServ) {
		super();
		this.prodFileRepo = prodFileRepo;
		this.prodFileConv = prodFileConv;
		this.prodFileSearchRepo = prodFileSearchRepo;
		this.hplModelServ = hplModelServ;
		this.usbConfServ = usbConfServ;
		this.ftSzServ = ftSzServ;
		this.pathServ = pathServ;
		this.foldConfServ = foldConfServ;
	}

	/**
	 * Find Entity by id
	 * @param id
	 * @return
	 */
	public ProdFile findById(Integer id) {
		return prodFileRepo.findById(id).orElse(null);
	}

	/**
	 * Find DTO by id
	 * @param id
	 * @return
	 */
	public ProdFileDto findDtoById(Integer id) {
		ProdFile rel = findById(id);
		return prodFileConv.entityToDto(rel);
	}

	/**
	 * Find list of DTO
	 * @return
	 */
	public List<ProdFileDto> findAllDto() {
		List<ProdFile> models = prodFileRepo.findAll();
		return prodFileConv.entityToDtoList(models);
	}

	/**
	 * Find HPL Model list by HPL ID
	 * @param parentId
	 * @return
	 */
	public List<HplModelDto> findHplModelSelectItems(Integer parentId) {
		return hplModelServ.findAllByParentNative(parentId);
	}

	/**
	 * Search file listing by criteria
	 * @param searchHplId
	 * @param searchHplModelId
	 * @param searchYear
	 * @param searchMth
	 * @param g2LotNo
	 * @param g2LotNoExp
	 * @param path
	 * @param pathExp
	 * @return ProdFileSearch object list
	 */
	public List<ProdFileSearch> searchByCriteria(String searchHplId, String searchHplModelId, String searchYear,
			String searchMth, String g2LotNo, String g2LotNoExp, String path, String pathExp, String fn, String fnExp) {
		return prodFileSearchRepo.searchByCriteria(searchHplId, searchHplModelId, searchYear, searchMth, g2LotNo,
				g2LotNoExp, path, pathExp, fn, fnExp);
	}

	/**
	 * Save record into DB
	 * Save file into Disk
	 * @param dto
	 * @param userId
	 * @param isFileChanged
	 * @param finalFileContent
	 * @return
	 * @throws Exception
	 */
	@Transactional
	public Integer save(ProdFileDto dto, String userId, boolean isFileChanged, MultipartFile finalFileContent)
			throws Exception {		
		RelPathDto2 path = pathServ.findDtoById(Integer.valueOf(dto.getFilePath()));
		Date currentDate = new Date();
		boolean isCreate = false;
		String dir = "";
		String oldDir = "";
		String deleteDir = "";
		// save parent
		if (Objects.isNull(dto.getPkPfileId()) || 0 == dto.getPkPfileId()) {
			dto.setCreatorId(userId);
			dto.setCreatedDatetime(currentDate);
			dir = path.getFilePath();
			isCreate = true;
		} else {
			ProdFileDto existing = findDtoById(dto.getPkPfileId());
			dto.setCreatorId(existing.getCreatorId());
			dto.setCreatedDatetime(existing.getCreatedDatetime());
			dto.setModifierId(userId);
			dto.setModifiedDatetime(currentDate);
			dir = path.getFilePath();
			isCreate = false;

			if (!isFileChanged) {
				dto.setFileName(existing.getFileName());
				dto.setFileSize(existing.getFileSize());
				dto.setFileType(existing.getFileType());
				dto.setCrcValue(existing.getCrcValue());
				dto.setContentObject(existing.getContentObject());
			} else {
				// if file change
				// if (!StringUtils.equals(existing.getFilePath(), dto.getFilePath())) {
				RelPathDto2 oldPath = pathServ.findDtoById(Integer.valueOf(existing.getFilePath()));
				oldDir = oldPath.getFilePath();
				deleteDir = (StringUtils.isEmpty(oldDir) ? dir : oldDir) + File.separator + existing.getFileName();

				logger.debug("save() isFileChanged?={}, deleteDir={}", isFileChanged, deleteDir);
				// }
			}
		}

		ProdFile entity = prodFileConv.dtoToEntity(dto);
		prodFileRepo.saveAndFlush(entity);

		// need to do something to files
		manageFile(isFileChanged, finalFileContent, isCreate, dir, deleteDir);
		
		//TODO: check and create multiple files to other dir for gtms
		if(dto.getHpl().equals("GTMS")) {
			
		}

		return entity.getPkPfileId();
	}
	
	/**
	 * Save record into DB
	 * Save file into Disk
	 * @param dto
	 * @param userId
	 * @param isFileChanged
	 * @param finalFileContent
	 * @return
	 * @throws Exception
	 */
	@Transactional
	public Integer save2(ProdFileDto dto, String userId, boolean isFileChanged, MultipartFile finalFileContent, String defFormat, Integer procType, String subProc)
			throws Exception {
		
		int pathId = 0;
		// check if there isn't FoldCatConf and RelPath created yet, then create one
		if(dto.getFilePath().isEmpty() || dto.getFilePath().equals("0")) {
			pathId = generateFoldConfPath(userId, dto.getHpl(), dto.getYear(), dto.getMth(), dto.getProdLn(), defFormat, procType, subProc);
			// set back pathid to ProdFile path
			dto.setFilePath(String.valueOf(pathId));
		} else {
			pathId = Integer.valueOf(dto.getFilePath());
		}		
		
		RelPathDto2 path = pathServ.findDtoById(pathId);
		Date currentDate = new Date();
		boolean isCreate = false;
		String dir = "";
		String oldDir = "";
		String deleteDir = "";
		// save parent
		if (Objects.isNull(dto.getPkPfileId()) || 0 == dto.getPkPfileId()) {
			dto.setCreatorId(userId);
			dto.setCreatedDatetime(currentDate);
			dir = path.getFilePath();
			isCreate = true;
		} else {
			ProdFileDto existing = findDtoById(dto.getPkPfileId());
			dto.setCreatorId(existing.getCreatorId());
			dto.setCreatedDatetime(existing.getCreatedDatetime());
			dto.setModifierId(userId);
			dto.setModifiedDatetime(currentDate);
			dir = path.getFilePath();
			isCreate = false;

			if (!isFileChanged) {
				dto.setFileName(existing.getFileName());
				dto.setFileSize(existing.getFileSize());
				dto.setFileType(existing.getFileType());
				dto.setCrcValue(existing.getCrcValue());
				dto.setContentObject(existing.getContentObject());
			} else {
				// if file change
				// if (!StringUtils.equals(existing.getFilePath(), dto.getFilePath())) {
				RelPathDto2 oldPath = pathServ.findDtoById(Integer.valueOf(existing.getFilePath()));
				oldDir = oldPath.getFilePath();
				deleteDir = (StringUtils.isEmpty(oldDir) ? dir : oldDir) + File.separator + existing.getFileName();

				logger.debug("save() isFileChanged?={}, deleteDir={}", isFileChanged, deleteDir);
				// }
			}
		}

		ProdFile entity = prodFileConv.dtoToEntity(dto);
		prodFileRepo.saveAndFlush(entity);

		// need to do something to files
		manageFile(isFileChanged, finalFileContent, isCreate, dir, deleteDir);
		
		//TODO: check and create multiple files to other dir for gtms
		if(dto.getHpl().equals("GTMS")) {
			
		}

		return entity.getPkPfileId();
	}

	/**
	 * Delete Prod file record
	 * @param id
	 */
	@Transactional
	public void delete(Integer id) {
		prodFileRepo.deleteById(id);
	}

	/**
	 * Find USB Conf list
	 * @param name
	 * @param serialNo
	 * @return
	 */
	public List<UsbConfDto> findUsbConf(String name, String serialNo) {
		List<UsbConfDto> usbConfList = usbConfServ.findAllDto();
		usbConfList = usbConfList.stream().filter(arg0 -> (StringUtils.equalsIgnoreCase(arg0.getName(), name))
				&& (StringUtils.equalsIgnoreCase(arg0.getSerialNo(), serialNo))).collect(Collectors.toList());

		return usbConfList;
	}

	/**
	 * Find USB Conf user list
	 * @param pkUconfId
	 * @return
	 */
	public List<Usr> findUsbUsrByParent(Integer pkUconfId) {
		List<Usr> usrDtoList = usbConfServ.getUsrSelectedList(pkUconfId);
		return usrDtoList;
	}
	
	/**
	 * Find USB Conf by multiple criteria
	 * @param serialNo
	 * @param name
	 * @param prodLn
	 * @param hpl
	 * @return list
	 */
	public List<UsbConfDto> findAllByCriteria(String serialNo, String name, String prodLn, String hpl){
		return usbConfServ.findAllByCriteria(serialNo, name, prodLn, hpl);
	}
	
	/**
	 * Find file type, size list
	 * @param hplId
	 * @return
	 */
	public List<FileTypeSzDto> findFileTypeSzList(String hplId) {
		List<FileTypeSzDto> ftSzList = ftSzServ.findAllDto();
		ftSzList = ftSzList.stream().filter(arg0 -> StringUtils.equals(arg0.getHpl(), hplId)).collect(Collectors.toList());
		return ftSzList;
	}
	
	/**
	 * Count all
	 * @return long
	 */
	public long countAll() {
		return prodFileRepo.count();
	}
	
	/**
	 * Count file by hpl
	 * @param hpl
	 * @param year
	 * @param month
	 * @return int
	 */
	public int countFileByCriteria(String hpl, int year, int month) {
		Integer cnt = prodFileRepo.countFileByCriteria(hpl, year, month);
		return Objects.isNull(cnt) ? 0 : cnt.intValue();
	}
	
	/**
	 * Find top 5 files
	 * @param searchHplId
	 * @param searchHplModelId
	 * @param searchYear
	 * @param searchMth
	 * @param g2LotNo
	 * @param g2LotNoExp
	 * @param path
	 * @param pathExp
	 * @return List<ProdFileSearch>
	 */
	public List<ProdFileSearch> searchTop5ByCriteria(String searchHplId, String searchHplModelId, String searchYear,
			String searchMth, String g2LotNo, String g2LotNoExp, String path, String pathExp){
		return prodFileSearchRepo.searchTop5ByCriteria(searchHplId, searchHplModelId, searchYear, searchMth, g2LotNo, g2LotNoExp, path, pathExp);
	}
	
	/**
	 * Delete file from disk
	 * @param pfDto
	 */
	public void deleteFileFromDisk(ProdFileDto pfDto) {
		RelPathDto2 rel = pathServ.findDtoById(Integer.parseInt(pfDto.getFilePath()));
		String deleteDir = rel.getFilePath()  + File.separator +  pfDto.getFileName();
		FileUtils.deleteQuietly(new File(deleteDir));
	}
	
	/**
	 * If Folder-Catg-Conf not yet create, create one during file upload save
	 * @param userId
	 */
	public Integer generateFoldConfPath(String userId, String hpl, String year, String mth, String prodLn,
			String defFormat, Integer procType, String subProc) {

		FoldCatgConfDto2 foldDto = new FoldCatgConfDto2();
		foldDto.setHpl(hpl);
		foldDto.setProdFileFormat(!hpl.equals("GTMS") ? defFormat : "");
		String filePath = "";
		{
			filePath = DEFAULT_DATA_PATH + hpl + "/";
			if (hpl.equals("GTMS")) {
				String procTypeDesc = procType == 1 ? "Mikron" : procType == 2 ? "Back End" : "";
				if (procType != 0)
					filePath = filePath + procTypeDesc + "/";
				if (prodLn != "")
					filePath = filePath + procTypeDesc + "#" + prodLn + "/";
				if (subProc != "")
					filePath = filePath + subProc + "/";
			} else {
				if (prodLn != "")
					filePath = filePath + hpl + "#" + prodLn + "/";
			}

			if (StringUtils.isNotEmpty(year))
				filePath = filePath + year + "/";
			
			//if(!hpl.equals("GTMS")) {
				if (StringUtils.isNotEmpty(mth))
					filePath = filePath + mth + "/";				
			//}
		}

		List<RelPathDto2> relList = new ArrayList<RelPathDto2>();
		if (!hpl.equals("GTMS")) {
			// for IF, MGG
			RelPathDto2 relDto = new RelPathDto2();
			relDto.setYear(year);
			relDto.setMth(mth);
			relDto.setProdLn(prodLn);
			relDto.setFilePath(filePath);
			relDto.setHModel("");
			relDto.setDay("");
			relDto.setSeq("");
			relList.add(relDto);
		} else {
			RelPathDto2 relDto = new RelPathDto2();
			relDto.setYear(year);
			relDto.setMth(mth);
			relDto.setProdLn(prodLn);
			relDto.setFilePath(filePath);
			relDto.setProdFileFormat(defFormat);
			relDto.setProcType(procType);
			relDto.setSubProc(subProc);
			relDto.setHModel("");
			relDto.setDay("");
			relDto.setSeq("");
			relList.add(relDto);
		}

		// check existence of FoldCatgConf first in db
		// if not exists then create FoldCatgConf and its RelPath
		List<FoldCatgConfDto2> currDtos = foldConfServ.searchByCriteria(hpl, "");
		int parentId = 0;
		int result = 0;
		if (currDtos.isEmpty()) {
			parentId = foldConfServ.save(foldDto, userId, relList, null);
		} else {
			// if FoldCatgConf exists, check RelPath first
			parentId = currDtos.get(0).getPkCatgId();
			List<RelPathDto2> rels = new ArrayList<RelPathDto2>();
			if (!hpl.equals("GTMS")) {
				// for IF, MGG
				rels = foldConfServ.searchRelPathByCriteria(parentId, "", year, mth, "", prodLn, "", 0, "");
			} else {
				rels = foldConfServ.searchRelPathByCriteria(parentId, "", year, mth, "", prodLn, "", procType, subProc);
			}

			// if RelPath not exists, create RelPath
			if (rels.isEmpty()) {
				result = foldConfServ.saveRelPath(relList.get(0), parentId, new Date(), userId);
			}
		}

		logger.debug(
				"generateFoldConfPath() userId={},hpl={},year={},mth={},prodLn={},defFormat={},procType={},subProc={},filePath,={},result={}",
				userId, hpl, year, mth, prodLn, defFormat, procType, subProc, filePath, result);

		return result;
	}
	
	/**
	 * Find duplicate file - for validation
	 * @param fileName
	 * @param lotNo
	 * @param hpl
	 * @return
	 */
	public Integer countDuplicateFile(String fileName, String lotNo, String hpl) {
		return prodFileRepo.countDuplicateFile(fileName, lotNo, hpl);
	}
	
	/**
	 * Count file by HPL
	 * @return List<Object[]>
	 */
	public List<Object[]> fileCountByHpl(){
		return prodFileRepo.fileCountByHpl();
	}
	
	/**
	 * Count file by HPL
	 * @return List<Object[]>
	 */
	public List<Object[]> fileCountByHpl2() {

		List<Object[]> objs = new ArrayList<>();
		List<Integer> results = prodFileRepo.fileCountByHpl2();
		for (int idx = 0; idx < results.size(); idx++) {
			if (idx == 0) {
				objs.add(new Object[] { "GTMS", results.get(idx) });
			} else if (idx == 1) {
				objs.add(new Object[] { "IF", results.get(idx) });
			} else if (idx == 2) {
				objs.add(new Object[] { "MGG", results.get(idx) });
			}
		}

		return objs;
	}

	/**
	 * Save file to disk
	 * 
	 * @param finalFileContent
	 * @param dir
	 * @throws IOException
	 */
	private void saveFileToDisk(MultipartFile finalFileContent, String dir) throws Exception {

		if (Objects.isNull(finalFileContent) || StringUtils.isEmpty(dir)) {
			throw new Exception("File or directory is not supplied or null!");
		}

		logger.debug("saveFileToDisk() file={}, dir={}", finalFileContent.getOriginalFilename(), dir);

		File saveFile = new File(dir + File.separator + finalFileContent.getOriginalFilename());
		FileUtils.writeByteArrayToFile(saveFile, finalFileContent.getBytes());
	}

	/**
	 * Manage file if it is new, or updated
	 * 
	 * @param isFileChanged
	 * @param finalFileContent
	 * @param isCreate
	 * @param dir
	 * @param deleteDir
	 * @throws Exception
	 */
	private void manageFile(boolean isFileChanged, MultipartFile finalFileContent, boolean isCreate, String dir,
			String deleteDir) throws Exception {
		try {
			// new file, add mode
			if (isCreate) {
				logger.debug("manageFile() new file, add mode");
				// save file to disk
				saveFileToDisk(finalFileContent, dir);
			}

			// old/current file, edit mode
			if (!isCreate && !isFileChanged) {
				logger.debug("manageFile() old/current file, edit mode");
				// do nothing to file
			}

			// file replace/update, edit mode
			// For file update case, delete old file
			if (!isCreate) {
				if (isFileChanged && StringUtils.isNotEmpty(deleteDir)) {
					logger.debug("manageFile() file replace/update, edit mode");
					// remove old file from disk
					FileUtils.deleteQuietly(new File(deleteDir));
					// save new file to disk
					saveFileToDisk(finalFileContent, dir);
				}
			}

		} catch (Exception e) {
			throw new Exception();
		}
	}
	
	//TODO
	private void createRelatedGtmsFiles(ProdFileDto pfDto, RelPathDto2 pathDto) {
		// for gtms, it can has the same file name in multiple folders
		// 1) mikron - cell 1, cell 2.1, cell 3 -  file name (KMY 210104504.txt) should repeat in cell 2.1 and cell 3
		String year = pfDto.getYear();
		String month = pfDto.getMth();
		String prodLn = pfDto.getProdLn();
		String seq = pfDto.getSeq();
		
		if(pathDto.getProcType() == 1) {
			if (pathDto.getSubProc().equals("CELL1")) {

			} else if (pathDto.getSubProc().equals("CELL2.1")) {

			} else if (pathDto.getSubProc().equals("CELL2.2")) {

			} else if (pathDto.getSubProc().equals("CELL3")) {

			}
			
		} else if (pathDto.getProcType() == 2) {				
		// 2) backend fet 2, fet 3 - file name (5004_500421403104.csv) if exists in fet 2, should repeat in fet 3, 
		// otherwise in fet 1
		// 3) backend fet 1 - file name (500421403104.csv) should either in fet 1 or in fet 2 and fet 3 
			if(pathDto.getSubProc().equals("FET1")) {
				
			} else if (pathDto.getSubProc().equals("FET2")) {
				
			} else if (pathDto.getSubProc().equals("FET3")) {
				
			}
		}
	}
	
}
