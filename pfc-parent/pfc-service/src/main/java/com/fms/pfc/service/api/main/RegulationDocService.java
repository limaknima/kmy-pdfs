package com.fms.pfc.service.api.main;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fms.pfc.domain.model.main.ReglDoc;
import com.fms.pfc.domain.model.main.RegulationDoc;
import com.fms.pfc.repository.main.api.ReglDocRepository;
import com.fms.pfc.repository.main.api.RegulationDocRepository;

@Service
public class RegulationDocService {

	private RegulationDocRepository regDocRepo;
	private ReglDocRepository reglDocRepo;

	@Autowired
	public RegulationDocService(RegulationDocRepository regDocRepo, ReglDocRepository reglDocRepo) {
		super();
		this.regDocRepo = regDocRepo;
		this.reglDocRepo = reglDocRepo;
	}

	public List<RegulationDoc> getDocument(int fileId) {

		return regDocRepo.getDocument(fileId);

	}

	// Start Kimberley 20210109
	public List<RegulationDoc> getDocumentDesc(int fileId) {

		return regDocRepo.getDocumentDesc(fileId);
	}

	public List<RegulationDoc> getDocListByRegId(int regId) {

		return regDocRepo.getDocListByRegId(regId);
	}

	public RegulationDoc getRegDoc(int regDocId) {

		return regDocRepo.getRegDoc(regDocId);
	}

	public void addRegulationDoc(String regName, int rawMatlId, String countryId, String regDocCatId,
			String regDocCatGrpId, String fileName, String filepath, String docType, byte[] contentObj,
			String archivedFlag, String version, String crcValue, String creatorId) {

		regDocRepo.addRegulationDoc(regName, rawMatlId, countryId, regDocCatId, regDocCatGrpId, fileName, filepath,
				docType, contentObj, archivedFlag, version, crcValue, creatorId);
	}

	public void updRegulationDoc(String regDocCatId, String regDocCatGrpId, String fileName, String filepath,
			String docType, byte[] contentObj, String archivedFlag, String version, String crcValue, String modifierId,
			String regDocId) {

		regDocRepo.updRegulationDoc(regDocCatId, regDocCatGrpId, fileName, filepath, docType, contentObj, archivedFlag,
				version, crcValue, modifierId, regDocId);
	}

	public void delRegDocByRegDocId(String regDocId) {

		regDocRepo.delRegDocByRegDocId(regDocId);
	}
	// End Kimberley 20210109
	
	public ReglDoc findReglDocById(int id) {
		return reglDocRepo.findById(id).orElse(null);
	}
	
	public List<ReglDoc> findReglDocByRegId(int id) {
		/*return reglDocRepo.findAll().stream()
				.filter(arg0 -> (arg0.getRegId() == id && arg0.getArchivedFlag().equals("N")))
				.collect(Collectors.toList());*/
		return reglDocRepo.findReglDocByRegId(id);
	}
	
	public List<ReglDoc> findTopFour(int id) {
		return reglDocRepo.findTopFour(id);
	}
	
	@Transactional
	public ReglDoc insertReglDoc(int regId, int regDocCatId, int regDocCatGrpId, String fileName,
			String filepath, String docType, byte[] contentObj, String archivedFlag, int version,
			String userId) {

		ReglDoc dto = new ReglDoc();
		dto.setRegId(regId);
		dto.setRegDocCatGrpId(regDocCatGrpId);
		dto.setRegDocCatId(regDocCatId);
		dto.setFileName(fileName);
		dto.setFilePath(filepath);
		dto.setDocType(docType);
		dto.setContentObject(contentObj);
		dto.setArchivedFlag(archivedFlag);
		dto.setVersion(version);
		dto.setCreatorId(userId);
		dto.setCreatedDatetime(new Date());

		reglDocRepo.save(dto);

		return dto;

	}

	@Transactional
	public void updReglDoc(int regDocCatId, int regDocCatGrpId, String fileName, String filePath, String docType,
			byte[] contentObject, String archivedFlag, int version, String userId,
			int regDocId) {
		
		ReglDoc dto = this.findReglDocById(regDocId);
		dto.setRegDocCatGrpId(regDocCatGrpId);
		dto.setRegDocCatId(regDocCatId);
		dto.setFileName(fileName);
		dto.setFilePath(filePath);
		dto.setDocType(docType);
		dto.setContentObject(contentObject);
		dto.setArchivedFlag(archivedFlag);
		dto.setVersion(version);
		dto.setModifierId(userId);
		dto.setModifiedDatetime(new Date());
		
		reglDocRepo.save(dto);
		
	}
}
