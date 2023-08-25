package com.fms.pfc.controller.main;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.context.annotation.SessionScope;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import com.fms.pfc.common.Authority;
import com.fms.pfc.common.CommonConstants;
import com.fms.pfc.common.MediaTypeUtils;
import com.fms.pfc.domain.dto.main.RegulationCatDto;
import com.fms.pfc.domain.model.main.RawMaterial;
import com.fms.pfc.domain.model.main.Regl;
import com.fms.pfc.domain.model.main.ReglDoc;
import com.fms.pfc.service.api.base.CountryService;
import com.fms.pfc.service.api.base.TrxHisService;
import com.fms.pfc.service.api.main.RawMaterialService;
import com.fms.pfc.service.api.main.RegService;
import com.fms.pfc.service.api.main.RegulationCategoryService;
import com.fms.pfc.service.api.main.RegulationDocService;
import com.fms.pfc.service.api.main.RegulationGroupService;
import com.fms.pfc.validation.common.CommonValidation;

@SuppressWarnings("unchecked")
@Controller
@SessionScope
public class MaterialRegController {
	
	private static final Logger logger = LoggerFactory.getLogger(MaterialRegController.class);
	
	private Authority auth;	
	private CountryService countryServ;	
	private CommonValidation commonValServ;	
	private RawMaterialService rawMatlServ;	
	private RegService regServ;	
	private RegulationDocService regDocServ;	
	private RegulationCategoryService regCatServ;	
	private RegulationGroupService regGroupServ;
	private TrxHisService trxHistServ;
	private ServletContext servletContext;
	
	private Map<String, Object> model = new HashMap<String, Object>();
	
	@Autowired
	public MaterialRegController(Authority auth, CountryService countryServ, CommonValidation commonValServ,
			RawMaterialService rawMatlServ, RegService regServ, RegulationDocService regDocServ,
			RegulationCategoryService regCatServ, RegulationGroupService regGroupServ, TrxHisService trxHistServ, ServletContext servletContext) {
		super();
		this.auth = auth;
		this.countryServ = countryServ;
		this.commonValServ = commonValServ;
		this.rawMatlServ = rawMatlServ;
		this.regServ = regServ;
		this.regDocServ = regDocServ;
		this.regCatServ = regCatServ;
		this.regGroupServ = regGroupServ;
		this.trxHistServ = trxHistServ;
		this.servletContext = servletContext;
	}

	private Map<String, Object> setFileButton(boolean fileAdd, boolean fileUpdate, boolean fileDelete,
			Map<String, Object> model) {
		model.put("fileAdd", fileAdd);
		model.put("fileUpdate", fileUpdate);
		model.put("fileDelete", fileDelete);

		return model;
	}

	private Map<String, Object> clearFileForm(Map<String, Object> model) {
		model.remove("rowNo");
		model.remove("regDocId");
		model.remove("regDocCatGrp");
		model.remove("regDocCat");
		model.remove("fileStatus");
		model.remove("viewFileName");
		model.remove("newFileContent");

		return model;
	}

	private Map<String, Object> holdValue(String countryId, int rawMatlId, String regName, String refUrl1,
			String refUrl2, String refUrl3, String refUrl4, String refUrl5, String remarks, int roNo, String regDocId,
			String regDocCatGrpId, String regDocCatId, String fileStatus, String fileName, Map<String, Object> model) {

		model.put("countryId", countryId);
		model.put("rawMatlId", rawMatlId);
		model.put("regName", regName);
		model.put("refUrl1", refUrl1);
		model.put("refUrl2", refUrl2);
		model.put("refUrl3", refUrl3);
		model.put("refUrl4", refUrl4);
		model.put("refUrl5", refUrl5);
		model.put("remarks", remarks);
		model.put("rowNo", regDocId);
		model.put("regDocId", regDocId);
		model.put("regDocCatGrp", regDocCatGrpId);
		model.put("regDocCat", regDocCatId);
		model.put("fileStatus", fileStatus);

		String screenMode = model.get("screenMode").toString();
		if (screenMode.equals(CommonConstants.SCREEN_MODE_EDIT)) {
			model.put("viewFileName", fileName);
		}

		return model;
	}

	private Map<String, Object> onPageLoadCommon(Map<String, Object> model) {
		model.put("countryItems", countryServ.findByCriteria("", "", "", "", String.valueOf(CommonConstants.FLAG_YES), ""));
		model.put("rawMatlItems", rawMatlServ.searchRawMaterial("", "", 0, 0));
		model.put("regDocCatGrpItems", regGroupServ.findAll());
		model.put("regDocCatItems", regCatServ.findAll());

		String screenMode = model.get("screenMode").toString();
		if (screenMode.equals(CommonConstants.SCREEN_MODE_ADD))
			model.put("editDiv", true);
		else
			model.put("editDiv", false);

		return model;
	}

	private boolean saveFuncErrorCheck(String countryId, int rawMatlId, String regName, String refUrl1, String refUrl2,
			String refUrl3, String refUrl4, String refUrl5, String remarks, List<ReglDoc> regDocTable,
			Map<String, Object> model) {

		model.remove("error");
		model.remove("success");
		String errorMsg = "";
		String screenMode = model.get("screenMode").toString();

		errorMsg += commonValServ.validateMandatoryInput(countryId, "Country");
		errorMsg += commonValServ.validateMandatoryInput(String.valueOf(rawMatlId), "Raw Material");
		errorMsg += commonValServ.validateMandatoryInput(regName, "Regulation Name");
		errorMsg += commonValServ.validateMandatoryInput(refUrl1, "Reference Url 1");

		errorMsg += commonValServ.validateInputLength(regName, 100, "Regulation Name");
		errorMsg += commonValServ.validateInputLength(refUrl1, 100, "Reference Url 1");
		errorMsg += commonValServ.validateInputLength(refUrl2, 100, "Reference Url 2");
		errorMsg += commonValServ.validateInputLength(refUrl3, 100, "Reference Url 3");
		errorMsg += commonValServ.validateInputLength(refUrl4, 100, "Reference Url 4");
		errorMsg += commonValServ.validateInputLength(refUrl5, 100, "Reference Url 5");
		errorMsg += commonValServ.validateInputLength(remarks, 500, "Remarks");

		if (regDocTable.size() == 0) {
			errorMsg += "Please enter regulation file information.<br />";
		} else {
			for (ReglDoc regDoc : regDocTable) {
				errorMsg += commonValServ.validateMandatoryInput(String.valueOf(regDoc.getRegDocCatGrpId()), "File Group");
				errorMsg += commonValServ.validateMandatoryInput(String.valueOf(regDoc.getRegDocCatId()), "File Category");
				errorMsg += commonValServ.validateMandatoryInput(regDoc.getFileName(), "File Name");
				errorMsg += commonValServ.validateMandatoryInput(regDoc.getArchivedFlag(), "Old File Status");

				errorMsg += commonValServ.validateInputLength(regDoc.getRegDocCatGrpName(), 50, "File Group");
				errorMsg += commonValServ.validateInputLength(regDoc.getRegDocCatName(), 100, "File Category");
				errorMsg += commonValServ.validateInputLength(regDoc.getFileName(), 100, "File Name");
			}
		}

		if (screenMode.equals(CommonConstants.SCREEN_MODE_ADD)) {
			List<Regl> regList = regServ.searchReglList(rawMatlId, countryId);
			if (regList.size() > 0) {
				errorMsg += "Raw Material Regulation already exist.";
			}
		}

		if (errorMsg.length() != 0) {
			model.put("error", errorMsg);

			holdValue(countryId, rawMatlId, regName, refUrl1, refUrl2, refUrl3, refUrl4, refUrl5, remarks, 0, "", "",
					"", "", "", model);
			return false;
		}

		return true;
	}

	private boolean saveMatlRegData(int regId, String regDocId, int rawMatlId, String rawMatlName, String countryId,
			String regName, String refUrl1, String refUrl2, String refUrl3, String refUrl4, String refUrl5,
			String remarks, String creatorId, Map<String, Object> model) {

		List<ReglDoc> regDocTable = (List<ReglDoc>) model.get("regDocItems");
		String screenMode = model.get("screenMode").toString();

		if (!saveFuncErrorCheck(countryId, rawMatlId, regName, refUrl1, refUrl2, refUrl3, refUrl4, refUrl5, remarks,
				regDocTable, model)) {
			return false;
		}

		String countryName = countryServ.findOneById(countryId).getCountryName();

		try {
			if (screenMode.equals(CommonConstants.SCREEN_MODE_ADD)) {
				String archiveFlag = String.valueOf(CommonConstants.FLAG_NO);// not active
				// add to db header
				Regl reglAdd = regServ.insertRegl(rawMatlId, countryId, regName, refUrl1, refUrl2, refUrl3, refUrl4, refUrl5, remarks, creatorId);
				
				for (ReglDoc regDoc : regDocTable) {
					// add to db detail
					regDocServ.insertReglDoc(reglAdd.getRegId(), regDoc.getRegDocCatId(),
							regDoc.getRegDocCatGrpId(), regDoc.getFileName(), regDoc.getFilePath(), regDoc.getDocType(),
							regDoc.getContentObject(), archiveFlag, regDoc.getVersion(), 
							creatorId);
				}

				model.put("success", "Raw Material " + rawMatlName + " Regulation File ("+countryName+") has been added successfully");

				trxHistServ.addTrxHistory(new Date(), "Insert Raw Material Regulation", creatorId,
						CommonConstants.FUNCTION_TYPE_INSERT, rawMatlName, CommonConstants.RECORD_TYPE_ID_REGL, null);

			} else if (screenMode.equals(CommonConstants.SCREEN_MODE_EDIT)) {
				
				regServ.updateRegl(regId, regName, refUrl1, refUrl2, refUrl3, refUrl4, refUrl5, remarks, creatorId);

				for (ReglDoc regDoc : regDocTable) {
					if (regDoc.getIndicator().equals("new")) {
						regDocServ.insertReglDoc(regId, regDoc.getRegDocCatId(),
								regDoc.getRegDocCatGrpId(), regDoc.getFileName(), regDoc.getFilePath(),
								regDoc.getDocType(), regDoc.getContentObject(), regDoc.getArchivedFlag(),
								regDoc.getVersion(), creatorId);

					} else if (regDoc.getIndicator().equals("exist")) {
						int version = regDoc.getVersion() + 1;

						regDocServ.updReglDoc(regDoc.getRegDocCatId(), regDoc.getRegDocCatGrpId(), regDoc.getFileName(),
								regDoc.getFilePath(), regDoc.getDocType(), regDoc.getContentObject(),
								regDoc.getArchivedFlag(), version, creatorId,
								regDoc.getRegDocId());
					}

				}
				
				List<ReglDoc> regDocDel = (List<ReglDoc>) model.get("regDocDel");
				if (regDocDel.size() > 0) {
					for (ReglDoc regDocItem : regDocDel) {
						regDocServ.delRegDocByRegDocId(String.valueOf(regDocItem.getRegDocId()));
					}
					regDocDel = new ArrayList<ReglDoc>();
				}
				model.put("regDocDel", regDocDel);

				model.put("success", "Raw material " + rawMatlName + " Regulation File ("+countryName+") has been updated successfully");

				trxHistServ.addTrxHistory(new Date(), "Update Raw Material Regulation", creatorId,
						CommonConstants.FUNCTION_TYPE_UPDATE, rawMatlName, CommonConstants.RECORD_TYPE_ID_REGL, null);
			}

			model.put("regDocTable", regDocTable);
		} catch (Exception e) {
			e.printStackTrace();
			model.put("regDocTable", regDocTable);

			if (screenMode.equals(CommonConstants.SCREEN_MODE_ADD))
				model.put("error", "Fail to add Raw Material " + rawMatlName + " Regulation File ("+countryName+").");
			else if (screenMode.equals(CommonConstants.SCREEN_MODE_EDIT))
				model.put("error", "Fail to update Raw Material " + rawMatlName + " Regulation File ("+countryName+").");

			return false;
		}

		return true;
	}

	@GetMapping("/main/material/rawMatlRegList")
	public ModelAndView getRawMatlRegList(HttpServletRequest request, HttpServletResponse response,
			HttpSession session) {

		model = auth.onPageLoad(model, request);
		auth.isAuthUrl(request, response);
		searchRegListLoad(model);

		return new ModelAndView("/main/material/rawMatlRegList", model);
	}

	private Map<String, Object> searchRegListLoad(Map<String, Object> model) {

		List<Regl> regList = regServ.searchReglList(0, "");
		List<RawMaterial> rawMatlList = rawMatlServ.searchRawMaterial("", "", 0, 0);

		// set the detail data into the header rawMatlRegForm object
		genRegListItems(regList);

		model.put("regListItems", regList);
		model.put("rawMatlItems", rawMatlList);
		model.put("countryItems", countryServ.findByCriteria("", "", "", "", String.valueOf(CommonConstants.FLAG_YES), ""));

		return model;
	}

	private void genRegListItems(List<Regl> regList) {
		for (Regl reg : regList) {
			
			RawMaterial rawMatl = rawMatlServ.searchRawMaterial("", "", reg.getRawMatlId(), 0).get(0);
			reg.setRawMatlName(rawMatl.getRawMatlName());
			reg.setRawMatlId(rawMatl.getRawMatlId());
			reg.setCountryName(countryServ.findOneById(reg.getCountryId()).getCountryName());
			
			List<ReglDoc> regDocList = regDocServ.findTopFour(reg.getRegId());
			for (ReglDoc regDoc : regDocList) {
				regDoc.setRegDocCatGrpName(regGroupServ.findOneById(regDoc.getRegDocCatGrpId()).getGrpName());
				regDoc.setRegDocCatName(regCatServ.findOneById(regDoc.getRegDocCatId()).getCatName());
				regDoc.setFileNameDis(regDoc.getFileName());
			}

			if (regDocList.size() == 4) {
				regDocList.get(3).setRegDocCatGrpName(".....");
				regDocList.get(3).setRegDocCatName(".....");
				regDocList.get(3).setFileNameDis(".....");
			}

			reg.setRegDocList(regDocList);
		}
	}

	@PostMapping("/main/material/searchRawMatlRegList")
	public ModelAndView searchRawMatlReg(HttpServletRequest request, @RequestParam(name = "rawMatlId") int rawMatlId,
			@RequestParam(name = "countryId") String countryId, HttpSession session) {

		List<Regl> regList = regServ.searchReglList(rawMatlId, countryId);

		genRegListItems(regList);

		model.put("regListItems", regList);
		model.put("rawMatlItems", rawMatlServ.searchRawMaterial("", "", 0, 0));
		model.put("countryItems", countryServ.findByCriteria("", "", "", "", String.valueOf(CommonConstants.FLAG_YES), ""));
		model.put("rawMatlId", rawMatlId);
		model.put("countryId", countryId);

		StringBuffer sb = new StringBuffer();
		sb.append("Search Criteria: ");
		sb.append("Raw Material Id=").append(rawMatlId).append(", ");
		sb.append("Country Id=").append(countryId.isEmpty() ? "<ALL>" : countryId);

		trxHistServ.addTrxHistory(new Date(), "Search Raw Material Regulation", request.getRemoteUser(),
				CommonConstants.FUNCTION_TYPE_SEARCH, "Search Raw Material", CommonConstants.RECORD_TYPE_ID_REGL,
				sb.toString());

		return new ModelAndView("/main/material/rawMatlRegList", model);
	}

	@GetMapping("/main/material/rawMatlRegForm")
	public ModelAndView loadRawMatlRegForm(HttpServletRequest request, HttpServletResponse response,
			HttpSession session) {

		model = auth.onPageLoad(model, request);
		model.put("header", "Add Raw Material Regulation File Form");
		model.put("screenMode", CommonConstants.SCREEN_MODE_ADD);
		model.put("btnEdit", true);
		model.put("btnSaveSts", false);
		model.put("btnAction", false);
		model.put("btnSave", "Save");
		setFileButton(false, true, true, model);
		onPageLoadCommon(model);

		List<ReglDoc> regDocList = new ArrayList<ReglDoc>();
		model.put("regDocItems", regDocList);

		Regl reg = new Regl();
		model.put("regItems", reg);

		model.put("regId", 0);// to initialise regId as type int

		List<ReglDoc> regDocDel = new ArrayList<ReglDoc>();
		model.put("regDocDel", regDocDel);

		return new ModelAndView("/main/material/rawMatlRegForm", model);
	}

	/*
	 * Validate File List
	 */
	private boolean errorCheckFileList(int rowNo, String mode, String regDocId, String catGrpId, String catId,
			MultipartFile file, String fileStatus, Map<String, Object> model) {
		model.remove("error");

		String errorMsg = "";
		errorMsg += commonValServ.validateMandatoryInput(catGrpId, "File Group");
		errorMsg += commonValServ.validateMandatoryInput(catId, "File Category");
		errorMsg += commonValServ.validateFileSize(file.getSize(), "Regulation File");
		
		if (mode.equals(CommonConstants.SCREEN_MODE_ADD)) {
			if (file.getOriginalFilename().equals("")) {
				errorMsg += "Upload file is mandatory.<br />";
			}
		} else if (mode.equals("update")) {
			errorMsg += commonValServ.validateMandatoryInput(fileStatus, "Old File Status");

			List<ReglDoc> regDocTable = (List<ReglDoc>) model.get("regDocItems");

			for (ReglDoc regDoc : regDocTable) {
				if (regDoc.getRowNo() == rowNo) {
					if (regDoc.getContentObject() == null && file.getOriginalFilename().equals("")) {
						errorMsg += "Upload file is mandatory.<br />";
					}
				}
			}
		}

		if (errorMsg.length() != 0) {
			model.put("error", errorMsg);
			return true;
		}

		return false;
	}

	/*
	 * Download File
	 */
	@GetMapping("/main/material/rawMatlRegFormDownloadFile")
	public ResponseEntity<byte[]> downloadFile(HttpServletRequest request, @RequestParam(name = "rowNo") int rowNo,
			HttpSession session) {

		List<ReglDoc> regDocTable = (List<ReglDoc>) model.get("regDocItems");
		String fileName = "";
		byte[] data = new byte[1024];

		for (ReglDoc regDoc : regDocTable) {
			if (regDoc.getRowNo() == rowNo) {
				fileName = regDoc.getFileName();
				data = regDoc.getContentObject();
			}
		}

		MediaType mt = MediaTypeUtils.getMediaTypeForFileName(servletContext, fileName);

		return ResponseEntity.ok().contentType(mt)
				.header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + fileName + "\"").body(data);
	}

	/*
	 * Download View File
	 */
	@GetMapping("/main/material/rawMatlRegFormDownloadViewFile")
	public ResponseEntity<byte[]> downloadViewFile(HttpServletRequest request, @RequestParam(name = "id") int id) {

		ReglDoc regDoc = regDocServ.findReglDocById(id);
		String fileName = "";
		byte[] data = new byte[1024];

		if (regDoc != null) {
			fileName = regDoc.getFileName();
			data = regDoc.getContentObject();
		}

		MediaType mt = MediaTypeUtils.getMediaTypeForFileName(servletContext, fileName);

		return ResponseEntity.ok().contentType(mt)
				.header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + fileName + "\"").body(data);
	}

	/*
	 * Add to Table list
	 */
	@PostMapping(value = "/main/material/rawMatlRegForm", params = "fileAction=add")
	public ModelAndView addToFileList(HttpServletRequest request, @RequestParam(name = "countryId") String countryId,
			@RequestParam(name = "rawMatlId") int rawMatlId, @RequestParam(name = "regName") String regName,
			@RequestParam(name = "refUrl1") String refUrl1, @RequestParam(name = "refUrl2") String refUrl2,
			@RequestParam(name = "refUrl3") String refUrl3, @RequestParam(name = "refUrl4") String refUrl4,
			@RequestParam(name = "refUrl5") String refUrl5, @RequestParam(name = "remarks") String remarks,
			@RequestParam(name = "regDocCatGrp") String regDocCatGrpId,
			@RequestParam(name = "regDocCat2", required = false) String regDocCatId2,
			@RequestParam(name = "regDocCat", required = false) String regDocCatId1,
			@RequestParam(name = "fileContent") MultipartFile fileContent,
			@RequestParam(name = "newFileContent") MultipartFile newFileContent, HttpSession session) {

		String screenMode = model.get("screenMode").toString();
		String regDocCatId = regDocCatId2;

		String mode = CommonConstants.SCREEN_MODE_ADD;
		String fileStatus = "Active"; // default when first add record
		String regDocId = ""; // regDocId is generated in db so leave it blank

		if (screenMode.equals(CommonConstants.SCREEN_MODE_EDIT)) {
			fileContent = newFileContent;
			regDocCatId = regDocCatId1;
		}

		// If hit error, hold all input value and set back into the form
		if (errorCheckFileList(0, mode, "", regDocCatGrpId, regDocCatId, fileContent, "", model)) {

			holdValue(countryId, rawMatlId, regName, refUrl1, refUrl2, refUrl3, refUrl4, refUrl5, remarks, 0, regDocId,
					regDocCatGrpId, regDocCatId, "", fileContent.getOriginalFilename(), model); // Archive flag for Add
																								// is
			// always Active
			return new ModelAndView("/main/material/rawMatlRegForm", model);
		} else
			holdValue(countryId, rawMatlId, regName, refUrl1, refUrl2, refUrl3, refUrl4, refUrl5, remarks, 0, regDocId,
					"", "", "", "", model); // If no error, leave the details blank

		List<ReglDoc> regDocTable = (List<ReglDoc>) model.get("regDocItems");
		int row = regDocTable.size();

		ReglDoc regDoc = new ReglDoc();

		regDoc.setRowNo(row);
		regDoc.setRegDocCatGrpId(Integer.parseInt(regDocCatGrpId));
		regDoc.setRegDocCatGrpName(regGroupServ.findOneById(Integer.parseInt(regDocCatGrpId)).getGrpName());
		regDoc.setRegDocCatName(regCatServ.findOneById(Integer.parseInt(regDocCatId)).getCatName());
		regDoc.setRegDocCatId(Integer.parseInt(regDocCatId));
		regDoc.setVersion(1);
		regDoc.setIndicator("new");

		if (fileStatus.equals("Archived")) {
			regDoc.setArchivedFlag(String.valueOf(CommonConstants.FLAG_YES));
		} else {
			regDoc.setArchivedFlag(String.valueOf(CommonConstants.FLAG_NO));
		}

		try {
			regDoc.setFileName(fileContent.getOriginalFilename());
			regDoc.setContentObject(fileContent.getBytes());
		} catch (IOException e) {
			e.printStackTrace();
		}

		regDocTable.add(regDoc);
		model.put("regDocItems", regDocTable);
		setFileButton(false, true, true, model);
		clearFileForm(model);

		return new ModelAndView("/main/material/rawMatlRegForm", model);
	}

	/*
	 * Update Table list
	 */
	@PostMapping(value = "/main/material/rawMatlRegForm", params = "fileAction=update")
	public ModelAndView updateFileList(HttpServletRequest request, @RequestParam(name = "countryId") String countryId,
			@RequestParam(name = "rawMatlId") int rawMatlId, @RequestParam(name = "regName") String regName,
			@RequestParam(name = "refUrl1") String refUrl1, @RequestParam(name = "refUrl2") String refUrl2,
			@RequestParam(name = "refUrl3") String refUrl3, @RequestParam(name = "refUrl4") String refUrl4,
			@RequestParam(name = "refUrl5") String refUrl5, @RequestParam(name = "remarks") String remarks,
			@RequestParam(name = "rowNo") int rowNo, @RequestParam(name = "regDocId") String regDocId,
			@RequestParam(name = "regDocCatGrp") String regDocCatGrpId,
			@RequestParam(name = "regDocCat", required = false) String regDocCatId1,
			@RequestParam(name = "regDocCat2", required = false) String regDocCatId2,
			@RequestParam(name = "fileContent") MultipartFile fileContent,
			@RequestParam(name = "fileStatus") String fileStatus,
			@RequestParam(name = "newFileContent") MultipartFile newFileContent, HttpSession session) {

		String screenMode = model.get("screenMode").toString();
		String regDocCatId = regDocCatId2;

		String mode = "update";

		if (screenMode.equals(CommonConstants.SCREEN_MODE_EDIT)) {
			fileContent = newFileContent;
			regDocCatId = regDocCatId1;
		}

		if (errorCheckFileList(rowNo, mode, regDocId, regDocCatGrpId, regDocCatId, fileContent, fileStatus, model)) {

			holdValue(countryId, rawMatlId, regName, refUrl1, refUrl2, refUrl3, refUrl4, refUrl5, remarks, rowNo,
					regDocId, regDocCatGrpId, regDocCatId, fileStatus, fileContent.getOriginalFilename(), model);
			return new ModelAndView("/main/material/rawMatlRegForm", model);
		} else
			holdValue(countryId, rawMatlId, regName, refUrl1, refUrl2, refUrl3, refUrl4, refUrl5, remarks, 0, "", "",
					"", "", "", model);// If no error, leave the details blank

		List<ReglDoc> regDocTable = (List<ReglDoc>) model.get("regDocItems");

		for (ReglDoc regDoc : regDocTable) {
			if (regDoc.getRowNo() == rowNo) {
				regDoc.setRegDocCatGrpId(Integer.parseInt(regDocCatGrpId));
				regDoc.setRegDocCatGrpName(regGroupServ.findOneById(Integer.parseInt(regDocCatGrpId)).getGrpName());
				regDoc.setRegDocCatName(regCatServ.findOneById(Integer.parseInt(regDocCatId)).getCatName());
				regDoc.setRegDocCatId(Integer.parseInt(regDocCatId));

				if (fileStatus.equals("Archived")) {
					regDoc.setArchivedFlag(String.valueOf(CommonConstants.FLAG_YES));
				} else {
					regDoc.setArchivedFlag(String.valueOf(CommonConstants.FLAG_NO));
				}

				try {
					regDoc.setContentObject(fileContent.getBytes());

					// only set if have value. Otherwise, it will hit error
					if (!fileContent.getOriginalFilename().equals("")) {
						regDoc.setFileName(fileContent.getOriginalFilename());
					}
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}

		model.put("regDocItems", regDocTable);
		setFileButton(false, true, true, model);
		clearFileForm(model);

		return new ModelAndView("/main/material/rawMatlRegForm", model);
	}

	/*
	 * Remove Table list
	 */
	@PostMapping(value = "/main/material/rawMatlRegForm", params = "fileAction=delete")
	public ModelAndView deleteManufList(HttpServletRequest request, @RequestParam(name = "rowNo") int rowNo, Regl reg,
			BindingResult result, @RequestParam(name = "countryId") String countryId,
			@RequestParam(name = "rawMatlId") int rawMatlId, @RequestParam(name = "regName") String regName,
			@RequestParam(name = "refUrl1") String refUrl1, @RequestParam(name = "refUrl2") String refUrl2,
			@RequestParam(name = "refUrl3") String refUrl3, @RequestParam(name = "refUrl4") String refUrl4,
			@RequestParam(name = "refUrl5") String refUrl5, @RequestParam(name = "remarks") String remarks,
			HttpSession session) {

		model.put("regItems", reg);
		model.remove("error");

		holdValue(countryId, rawMatlId, regName, refUrl1, refUrl2, refUrl3, refUrl4, refUrl5, remarks, 0, "", "", "",
				"", "", model);// If no error, leave the details blank

		List<ReglDoc> regDocTable = (List<ReglDoc>) model.get("regDocItems");

		try {
			List<ReglDoc> regDocDel = (List<ReglDoc>) model.get("regDocDel");

			regDocDel.add(regDocTable.get(rowNo));
			regDocTable.remove(rowNo);

			int reCalRowNo = 0;
			for (int i = 0; i < regDocTable.size(); i++) {
				regDocTable.get(i).setRowNo(reCalRowNo);
				reCalRowNo++;
			}

		} catch (Exception e) {
			model.put("regDocItems", regDocTable);
		}

		model.put("regDocItems", regDocTable);
		setFileButton(false, true, true, model);
		clearFileForm(model);

		return new ModelAndView("/main/material/rawMatlRegForm", model);
	}

	/*
	 * Save function
	 */
	@PostMapping(value = "/main/material/rawMatlRegForm", params = "action=save")
	public ModelAndView saveRawMatlRegForm(@RequestParam(name = "regId") int regId,
			@RequestParam(name = "regDocId") String regDocId, @RequestParam(name = "rawMatlId") int rawMatlId,
			@RequestParam(name = "regName") String regName, @RequestParam(name = "countryId") String countryId,
			@RequestParam(name = "refUrl1") String refUrl1, @RequestParam(name = "refUrl2") String refUrl2,
			@RequestParam(name = "refUrl3") String refUrl3, @RequestParam(name = "refUrl4") String refUrl4,
			@RequestParam(name = "refUrl5") String refUrl5, @RequestParam(name = "remarks") String remarks,
			HttpServletRequest request, HttpServletResponse response, HttpSession session) {

		String rawMatlName = rawMatlServ.searchRawMaterial("", "", rawMatlId, 0).get(0).getRawMatlName();

		if (!saveMatlRegData(regId, regDocId, rawMatlId, rawMatlName, countryId, regName, refUrl1, refUrl2, refUrl3,
				refUrl4, refUrl5, remarks, request.getRemoteUser(), model)) {
			return new ModelAndView("/main/material/rawMatlRegForm", model);
		}

		searchRegListLoad(model);
		
		model.put("btnEdit", false);
		
		return new ModelAndView("/main/material/rawMatlRegList", model);
	}

	/*
	 * Retrieve and view raw material regulation
	 */
	@RequestMapping("/main/material/rawMatlRegFormView")
	public ModelAndView viewRawMatlRegForm(@RequestParam(name = "regId") int regId,
			@RequestParam(name = "countryId") String countryId, HttpServletRequest request,
			HttpServletResponse response, HttpSession session) {
				
		model.remove("success");
		model.remove("error");

		String screenMode = CommonConstants.SCREEN_MODE_VIEW;
		model.put("screenMode", screenMode);

		onPageLoadCommon(model);
		setFileButton(true, true, true, model);

		Regl reg = regServ.findReglById(regId);
		model.put("regItems", reg);
		model.put("header", "View Raw Material Regulation File Form");
		model.put("btnSaveSts", true);

		model.put("regId", reg.getRegId());
		model.put("countryId", reg.getCountryId());
		model.put("rawMatlId", reg.getRawMatlId());
		model.put("regName", reg.getRegName());
		model.put("refUrl1", reg.getRefUrl1());
		model.put("refUrl2", reg.getRefUrl2());
		model.put("refUrl3", reg.getRefUrl3());
		model.put("refUrl4", reg.getRefUrl4());
		model.put("refUrl5", reg.getRefUrl5());
		model.put("remarks", reg.getRemarks());
		
		List<RegulationCatDto> all = regCatServ.findAll().stream()
				.filter(arg0 -> ((arg0.getCountryList() != null ? arg0.getCountryList().contains(countryId) : false)))
				.collect(Collectors.toList());
		all.sort(Comparator.comparing(RegulationCatDto::getCatName));
		
		model.put("regDocCatItems", all);


		if (!CommonConstants.SCREEN_MODE_EDIT.equals(screenMode)) {
			String rawMatlName = rawMatlServ.searchRawMaterial("", "", reg.getRawMatlId(), 0).get(0).getRawMatlName();
			
			trxHistServ.addTrxHistory(new Date(), "View Raw Material Regulation", request.getRemoteUser(),
					CommonConstants.FUNCTION_TYPE_VIEW, rawMatlName, CommonConstants.RECORD_TYPE_ID_REGL, null);
		}
		List<ReglDoc> regDocTable = regDocServ.findReglDocByRegId(reg.getRegId());

		int rowNo = 0;
		for (ReglDoc regDoc : regDocTable) {
			regDoc.setRowNo(rowNo);
			rowNo++;
			regDoc.setRegDocCatGrpName(regGroupServ.findOneById(regDoc.getRegDocCatGrpId()).getGrpName());
			regDoc.setRegDocCatName(regCatServ.findOneById(regDoc.getRegDocCatId()).getCatName());
			regDoc.setIndicator("exist");
		}
		model.put("regDocItems", regDocTable);

		DateFormat formatter = new SimpleDateFormat("dd/MM/yyy hh:mm:ss a");
		try {
			String createdInfo = "Created by " + reg.getCreatorId() + " on "
					+ formatter.format(reg.getCreatedDateTime());
			model.put("createdInfo", createdInfo);
		} catch (Exception e) {
		}

		String modifiedInfo = "";
		try {
			modifiedInfo = "Modified by " + reg.getModifierId() + " on " + formatter.format(reg.getModifiedDateTime());
		} catch (Exception e) {
			modifiedInfo = "Modified by --";
		}
		model.put("modifiedInfo", modifiedInfo);
		
		List<ReglDoc> regDocDel = new ArrayList<ReglDoc>();
		model.put("regDocDel", regDocDel);
		
		return new ModelAndView("/main/material/rawMatlRegForm", model);
	}

	@GetMapping("/main/material/rawMatlRegFormGetData")
	public ModelAndView retrieveFilefTableData(HttpServletRequest request, @RequestParam(name = "rowNo") int rowNo,
			Regl reg, BindingResult result, HttpSession session) {

		String screenMode = model.get("screenMode").toString();

		model.put("regItems", reg);
		model.remove("error");
		List<ReglDoc> regDocTable = (List<ReglDoc>) model.get("regDocItems");

		for (ReglDoc regDoc : regDocTable) {

			if (regDoc.getRowNo() == rowNo) {
				model.put("regDocId", regDoc.getRegDocId());
				model.put("regDocCatGrp", Integer.valueOf(regDoc.getRegDocCatGrpId()));
				model.put("regDocCat", Integer.valueOf(regDoc.getRegDocCatId()));
				model.put("rowNo", regDoc.getRowNo());
				model.put("viewFileName", regDoc.getFileName());

				if (regDoc.getArchivedFlag() != null) {
					if (regDoc.getArchivedFlag().equals(String.valueOf(CommonConstants.FLAG_YES))) {
						model.put("fileStatus", "Archived");
					}
				}
			}
		}

		model.put("regDocItems", regDocTable);

		if (!screenMode.equals(CommonConstants.SCREEN_MODE_VIEW)) {
			setFileButton(false, false, false, model);
		}

		return new ModelAndView("/main/material/rawMatlRegForm", model);
	}

	@PostMapping(value = "/main/material/rawMatlRegForm", params = "action=edit")
	public ModelAndView editRawMatlRegForm(HttpServletRequest request, HttpSession session) {

		model.put("screenMode", CommonConstants.SCREEN_MODE_EDIT);
		setFileButton(false, true, true, model);
		model.put("btnEdit", true);
		model.put("btnSaveSts", false);
		model.put("btnSave", "Save");
		model.put("header", "Edit Raw Material Regulation File Form");
		model.remove("success");
		model.remove("error");
		clearFileForm(model);

		return new ModelAndView("/main/material/rawMatlRegForm", model);
	}
	
	@GetMapping(value = "/main/material/getRegCatItemsByCountry")
	@ResponseBody
	public Map<String, Object> getRegCatItemsByCountry(@RequestParam(name = "cId") String countryId,
			HttpServletRequest request, HttpSession session) {

		List<RegulationCatDto> all = regCatServ.findAll().stream()
				.filter(arg0 -> ((arg0.getCountryList() != null ? arg0.getCountryList().contains(countryId) : false)))
				.collect(Collectors.toList());
		all.sort(Comparator.comparing(RegulationCatDto::getCatName));

		Map<String, Object> model = new HashMap<String, Object>();
		model.put("items", all);

		return model;
	}

		
}
