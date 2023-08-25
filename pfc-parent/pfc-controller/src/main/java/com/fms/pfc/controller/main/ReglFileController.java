package com.fms.pfc.controller.main;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.context.annotation.SessionScope;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import com.fms.pfc.common.Authority;
import com.fms.pfc.common.CommonConstants;
import com.fms.pfc.common.MediaTypeUtils;
import com.fms.pfc.domain.dto.LabelAndValueDto;
import com.fms.pfc.domain.dto.main.ReglFileDto;
import com.fms.pfc.domain.dto.main.ReglInfoDto;
import com.fms.pfc.domain.dto.main.ReglRmDto;
import com.fms.pfc.domain.dto.main.RegulationCatDto;
import com.fms.pfc.domain.model.main.ReglFileSearch;
import com.fms.pfc.service.api.base.CountryService;
import com.fms.pfc.service.api.base.TrxHisService;
import com.fms.pfc.service.api.main.RawMaterialService;
import com.fms.pfc.service.api.main.ReglFileService;
import com.fms.pfc.service.api.main.RegulationCategoryService;
import com.fms.pfc.service.api.main.RegulationGroupService;
import com.fms.pfc.validation.common.CommonValidation;

@Controller
@SessionScope
public class ReglFileController {
	private static final Logger logger = LoggerFactory.getLogger(ReglFileController.class);

	private Authority auth;
	private CommonValidation commonValServ;
	private ReglFileService rfServ;
	private RegulationCategoryService regCatServ;
	private RegulationGroupService regGroupServ;
	private TrxHisService trxHistServ;
	private CountryService countryServ;
	private RawMaterialService rmServ;
	private ServletContext servletContext;
	private MessageSource msgSource;

	private Map<String, Object> model = new HashMap<String, Object>();
	private String screenMode = "";

	private static final String LBL_FILE_GRP = "File Group";
	private static final String LBL_FILE_CAT = "File Category";
	private static final String LBL_FILE_NAME = "File Name";
	private static final String LBL_COUNTRY = "Country";
	private static final String LBL_RM = "Raw Material";
	private static final String LBL_SEL_RM = "Selected RM";
	private static final String LBL_REGL_NAME = "Regulation Name";
	private static final String LBL_REF_URL = "Reference URL";
	private static final String LBL_REMARK = "Remark";

	@Autowired
	public ReglFileController(Authority auth, CommonValidation commonValServ, ReglFileService rfServ,
			RegulationCategoryService regCatServ, RegulationGroupService regGroupServ, TrxHisService trxHistServ,
			ServletContext servletContext, MessageSource msgSource, CountryService countryServ,
			RawMaterialService rmServ) {
		super();
		this.auth = auth;
		this.commonValServ = commonValServ;
		this.rfServ = rfServ;
		this.regCatServ = regCatServ;
		this.regGroupServ = regGroupServ;
		this.trxHistServ = trxHistServ;
		this.servletContext = servletContext;
		this.msgSource = msgSource;
		this.countryServ = countryServ;
		this.rmServ = rmServ;
	}

	/**
	 * Display initial listing screen
	 * 
	 * @param request
	 * @param response
	 * @param session
	 * @return
	 */
	@GetMapping("/main/material/reglFileRmList")
	public ModelAndView loadListing(HttpServletRequest request, HttpServletResponse response, HttpSession session) {

		model = auth.onPageLoad(model, request);
		auth.isAuthUrl(request, response);

		model.put("searchRawMatlItems", rmServ.getRmNameLabelAndValue());
		model.put("searchCountryItems",
				countryServ.findByCriteria("", "", "", "", String.valueOf(CommonConstants.FLAG_YES), ""));

		List<ReglFileSearch> allRegFiles = rfServ.searchReglFileByCritria(0, "", "", "");
		model.put("allRegFiles", allRegFiles);
//		loadListing(model);

		return new ModelAndView("/main/material/reglFileRmList", model);
	}

	/**
	 * Search all listing
	 * 
	 * @param model
	 */
	private void loadListing(Map<String, Object> model) {
		Integer rmId = (Integer) model.get("searchRawMatlId");
		String countryId = (String) model.get("searchCountryId");
		String fileName = (String) model.get("searchFileName");
		String fileNameExp = (String) model.get("fileNameExp");
		// List<ReglFileRmInfoSearchDto> allRegFiles = rfServ.findAllFileRmInfoDto(rmId,
		// countryId, fileName, fileNameExp);
		List<ReglFileSearch> allRegFiles = rfServ.searchReglFileByCritria(rmId, countryId, fileName, fileNameExp);
		model.put("allRegFiles", allRegFiles);

	}

	/**
	 * Do search
	 * 
	 * @param request
	 * @param rawMatlId
	 * @param countryId
	 * @param session
	 * @return
	 */
	@PostMapping("/main/material/searchRegFileRmList")
	public ModelAndView doSearchReglFile(@RequestParam(name = "searchRawMatlId") Integer rawMatlId,
			@RequestParam(name = "searchCountryId") String countryId,
			@RequestParam(name = "searchFileName") String fileName,
			@RequestParam(name = "fileNameExp") String fileNameExp, HttpServletRequest request, HttpSession session) {

		removeAlert(model);
		boolean hasError = false;
		String errorMsg = "";
		model.put("searchRawMatlId", rawMatlId);
		model.put("searchCountryId", countryId);
		model.put("searchFileName", fileName);
		model.put("fileNameExp", fileNameExp);

		/*{
			// validate input first
			// to prevent search performance, at least 1 criteria must be filled
			if ((Objects.isNull(rawMatlId) || rawMatlId == 0) && StringUtils.isEmpty(countryId)
					&& StringUtils.isEmpty(fileName)) {
				errorMsg += "Please select at least one (1) criteria to search.";
				model.put("error", errorMsg);
				return new ModelAndView("/main/material/reglFileRmList", model);
			}
		}*/

		try {
			// List<ReglFileRmInfoSearchDto> allRegFiles =
			// rfServ.findAllFileRmInfoDto(rawMatlId, countryId, fileName,
			// fileNameExp);

			List<ReglFileSearch> allRegFiles = rfServ.searchReglFileByCritria(rawMatlId, countryId, fileName,
					fileNameExp);
			model.put("allRegFiles", allRegFiles);

			logger.debug("doSearchReglFile() rawMatlId={},countryId={},fileName={}", rawMatlId, countryId, fileName);

			StringBuffer sb = new StringBuffer();
			sb.append("Search Criteria: ");
			sb.append("Raw Material Id=").append(rawMatlId).append(", ");
			sb.append("Country Id=").append(countryId.isEmpty() ? "<ALL>" : countryId);

			trxHistServ.addTrxHistory(new Date(), "Search Raw Material Regulation File", request.getRemoteUser(),
					CommonConstants.FUNCTION_TYPE_SEARCH, "Search Raw Material Regulation File",
					CommonConstants.RECORD_TYPE_ID_REGL, sb.toString());

		} catch (Exception e) {
			e.printStackTrace();
			hasError = true;
			errorMsg += "Failed to get record.";
			errorMsg += e.toString();

		} finally {
			if (hasError) {
				model.put("error", errorMsg);
				// return back user input
				loadListing(model);
			}
		}

		return new ModelAndView("/main/material/reglFileRmList", model);
	}

	/**
	 * View form from listing
	 * 
	 * @param request
	 * @param reglFileId
	 * @return
	 */
	@RequestMapping("/main/material/reglFileRmFormView")
	public ModelAndView viewForm(HttpServletRequest request, @RequestParam(name = "reglFileId") Integer reglFileId) {
		removeAlert(model);

		String screenMode = CommonConstants.SCREEN_MODE_VIEW;
		model.put("screenMode", screenMode);

		onPageLoadCommon(model);
		manageInfoButton(true, true, true, model);

		// Set confirmation message
		model.put("confirmHeader", "Edit cancellation");
		model.put("confirmMsg", "Do you want to cancel Edit this record ?");
		model.put("header", "View Raw Material Regulation File Form");
		model.put("btnSaveSts", true);

		ReglFileDto dto = rfServ.findDtoById(reglFileId);

		logger.debug("viewForm() ... id={}", dto.getReglFileId());

		model.put("reglFileItem", dto);
		model.put("reglInfoItems", dto.getRegInfoList());
		model.put("reglInfoDelItems", new ArrayList<ReglInfoDto>());

		List<LabelAndValueDto> selected = getSelectedRm(reglFileId);
		List<LabelAndValueDto> remaininig = getRemainingRm(selected);

		model.put("rmSelected", selected);
		model.put("rmRemaining", remaininig);

		DateFormat formatter = new SimpleDateFormat("dd/MM/yyy hh:mm:ss a");
		try {
			String createdInfo = "Created by " + dto.getCreatorId() + " on "
					+ formatter.format(dto.getCreatedDatetime());
			model.put("createdInfo", createdInfo);
		} catch (Exception e) {
		}

		String modifiedInfo = "";
		try {
			modifiedInfo = "Modified by " + dto.getModifierId() + " on " + formatter.format(dto.getModifiedDatetime());
		} catch (Exception e) {
			modifiedInfo = "Modified by --";
		}
		model.put("modifiedInfo", modifiedInfo);

		trxHistServ.addTrxHistory(new Date(), "View Raw Material Regulation File", request.getRemoteUser(),
				CommonConstants.FUNCTION_TYPE_VIEW, dto.getFileName(), CommonConstants.RECORD_TYPE_ID_REGL, null);

		return new ModelAndView("/main/material/reglFileRmForm", model);
	}

	/**
	 * Create new form
	 * 
	 * @param request
	 * @return
	 */
	@GetMapping("/main/material/reglFileRmForm")
	public ModelAndView loadNewForm(HttpServletRequest request) {
		removeAlert(model);

		logger.debug("loadNewForm()");

		// Set mode
		screenMode = CommonConstants.SCREEN_MODE_ADD;
		model.put("screenMode", screenMode);
		model.put("btnEdit", true);
		model.put("btnSaveSts", false);
		model.put("btnAction", false);
		model.put("btnSave", "Save");
		manageInfoButton(false, true, true, model);

		// Form item/fields
		model.put("reglFileItem", new ReglFileDto());
		model.put("reglInfoItems", new ArrayList<ReglInfoDto>());
		onPageLoadCommon(model);

		// Set form header
		model.put("header", "Add RM Regulation File Form");
		// Set confirmation message
		model.put("confirmHeader", "Cancel confirmation");
		model.put("confirmMsg", "Do you want to cancel this record ?");

		return new ModelAndView("/main/material/reglFileRmForm", model);
	}

	/**
	 * Save form
	 * 
	 * @param reglFileDto
	 * @param rmChoice
	 * @param request
	 * @param addModeFileContent
	 * @param bindingResult
	 * @param session
	 * @return
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	@PostMapping(value = "/main/material/reglFileRmFormSave", params = "action=save")
	public ModelAndView saveReglFileRm(@Valid ReglFileDto reglFileDto,
			// @RequestParam(value = "rmChoice1", required = false) String[] rmChoice,
			@RequestPart(name = "fileContent") MultipartFile addModeFileContent,
			@RequestPart(name = "newFileContent") MultipartFile editModeFileContent,
			@RequestParam(name = "docCatId2", required = false) Integer docCatId2, BindingResult bindingResult,
			HttpSession session, HttpServletRequest request) throws Exception {

		logger.debug("saveReglFileRm() ... id={}", reglFileDto.getReglFileId());
		String[] rmChoice = null;

		removeAlert(model);
		String mode = screenMode;
		MultipartFile finalFileContent = null;

		List<ReglInfoDto> reglInfoList = (List<ReglInfoDto>) model.get("reglInfoItems");
		model.put("reglFileItem", reglFileDto);

		if (StringUtils.equals(mode, CommonConstants.SCREEN_MODE_ADD)) {
			finalFileContent = addModeFileContent;

			if (Objects.isNull(finalFileContent) || finalFileContent.getSize() == 0) {
				logger.debug("saveReglFileRm() setting file");
				finalFileContent = (MultipartFile) model.get("fileContentTemp");
			}

		} else if (StringUtils.equals(mode, CommonConstants.SCREEN_MODE_EDIT)) {
			if (Objects.nonNull(editModeFileContent))
				finalFileContent = editModeFileContent;
			// regDocCatId = regDocCatId1;
		}

		String errorMsg = validateForm(reglFileDto, rmChoice, finalFileContent, mode, docCatId2);
		if (StringUtils.isNotEmpty(errorMsg)) {
			model.put("error", errorMsg);
			model.put("reglFileItem", reglFileDto);
			model.put("currTab", "fileTab1");

			if (mode.equals(CommonConstants.SCREEN_MODE_EDIT)) {
				screenMode = CommonConstants.SCREEN_MODE_EDIT;
				// Set mode to HTML header
				model.put("screenMode", screenMode);
				model.put("header", "RM Regulation File");
				// Set confirmation message
				model.put("confirmHeader", "Edit cancellation");
				model.put("confirmMsg", "Do you want to cancel edit this record ?");
			}

			return new ModelAndView("/main/material/reglFileRmForm", model);
		}

		try {
			String sucessMsg = "";
			rmChoice = reglFileDto.getRmChoice();
			List<Integer> choiceRmIds = new ArrayList<Integer>();
			if (Objects.nonNull(rmChoice) && rmChoice.length != 0) {
				choiceRmIds = Arrays.asList(rmChoice).stream().map(s -> Integer.parseInt(s))
						.collect(Collectors.toList());
			}

			// set file details
			if (Objects.nonNull(finalFileContent) && finalFileContent.getSize() > 0) {
				reglFileDto.setFileName(finalFileContent.getOriginalFilename());
				reglFileDto.setContentObject(finalFileContent.getBytes());
			}
			// ----

			if (StringUtils.equals(mode, CommonConstants.SCREEN_MODE_ADD)) {
				errorMsg = "Record failed to be added.";
				sucessMsg = "Record added successfully.";

				rfServ.saveReglFile(reglFileDto, request.getRemoteUser(), choiceRmIds, reglInfoList);
				model.put("reglFileItem", new ReglFileDto());

				trxHistServ.addTrxHistory(new Date(), "Insert RM Regulation File", request.getRemoteUser(),
						CommonConstants.FUNCTION_TYPE_INSERT, reglFileDto.getFileName(),
						CommonConstants.RECORD_TYPE_ID_REGL, null);

			} else if (StringUtils.equals(mode, CommonConstants.SCREEN_MODE_EDIT)) {
				// Set mode to HTML header
				screenMode = CommonConstants.SCREEN_MODE_VIEW;
				model.put("screenMode", screenMode);
				model.put("header", "RM Regulation File");

				errorMsg = "Record failed to be updated.";
				sucessMsg = "Record updated successfully.";

				rfServ.saveReglFile(reglFileDto, request.getRemoteUser(), choiceRmIds, reglInfoList);

				trxHistServ.addTrxHistory(new Date(), "Update RM Regulation File", request.getRemoteUser(),
						CommonConstants.FUNCTION_TYPE_UPDATE, reglFileDto.getFileName(),
						CommonConstants.RECORD_TYPE_ID_REGL, null);

			}
			// Print add success
			model.put("success", sucessMsg);

		} catch (Exception e) {
			// Print DB exception
			e.printStackTrace();
			model.put("error", errorMsg);
		}

		model.put("btnEdit", false);
		{
			model.put("searchCountryId", reglFileDto.getCountryId());
			model.put("searchFileName", reglFileDto.getFileName());
			model.put("fileNameExp", "3");
			loadListing(model);
		}
		return new ModelAndView("/main/material/reglFileRmList", model);
	}

	/**
	 * Retrieve Info form from Info listing
	 * 
	 * @param request
	 * @param rowNo
	 * @param reglFileDto
	 * @param result
	 * @param session
	 * @return
	 */
	@SuppressWarnings("unchecked")
	@GetMapping("/main/material/reglInfoFormGetData")
	public ModelAndView retrieveFilefTableData(HttpServletRequest request, @RequestParam(name = "rowNo") int rowNo,
			ReglFileDto reglFileDto, BindingResult result, HttpSession session) {

		String mode = model.get("screenMode").toString();
		model.remove("error");

		List<ReglInfoDto> reglInfoList = (List<ReglInfoDto>) model.get("reglInfoItems");
		reglInfoList.forEach(obj -> {
			if (obj.getRowNo() == rowNo) {
				model.put("infoRmName", obj.getRmName());
				model.put("infoRmId", obj.getRmId());
				model.put("infoRegName", obj.getRegName());
				model.put("infoRef1", obj.getRefUrl1());
				model.put("infoRef2", obj.getRefUrl2());
				model.put("infoRef3", obj.getRefUrl3());
				model.put("infoRef4", obj.getRefUrl4());
				model.put("infoRef5", obj.getRefUrl5());
				model.put("infoRemark", obj.getRemarks());
				model.put("infoRowNo", obj.getRowNo());
			}
		});

		model.put("currTab", "refTab2");

		if (!StringUtils.equals(mode, CommonConstants.SCREEN_MODE_VIEW)) {
			manageInfoButton(false, false, false, model);
		}

		return new ModelAndView("/main/material/reglFileRmForm", model);
	}

	/**
	 * Add to Table list
	 * 
	 * @param request
	 * @param reglFileDto
	 * @param rawMatlId
	 * @param regName
	 * @param infoRegName
	 * @param refUrl1
	 * @param refUrl2
	 * @param refUrl3
	 * @param refUrl4
	 * @param refUrl5
	 * @param remarks
	 * @param session
	 * @return
	 */
	@SuppressWarnings("unchecked")
	@PostMapping(value = "/main/material/reglFileRmFormSave", params = "infoAction=add")
	public ModelAndView addToInfoList(HttpServletRequest request, ReglFileDto reglFileDto,
			@RequestParam(name = "infoRmId", required = false) Integer rawMatlId,
			@RequestParam(name = "infoRegName", required = false) String infoRegName,
			@RequestParam(name = "infoRef1", required = false) String refUrl1,
			@RequestParam(name = "infoRef2", required = false) String refUrl2,
			@RequestParam(name = "infoRef3", required = false) String refUrl3,
			@RequestParam(name = "infoRef4", required = false) String refUrl4,
			@RequestParam(name = "infoRef5", required = false) String refUrl5,
			@RequestParam(name = "infoRemark", required = false) String remarks,
			@RequestParam(name = "docCatId2", required = false) Integer docCatId2,
			@RequestPart(name = "fileContent", required = false) MultipartFile fileContent,
			@RequestParam(name = "docCatId", required = false) Integer docCatId,
			@RequestPart(name = "newFileContent", required = false) MultipartFile newFileContent,
			@RequestParam(name = "infoRmId2", required = false) Integer infoRmId2,
			// @RequestParam(name = "rmChoice1", required = false) String[] rmChoice,
			HttpSession session) {

		model.remove("error");
		model.put("currTab", "refTab2");
		List<ReglInfoDto> reglInfoList = (List<ReglInfoDto>) model.get("reglInfoItems");

		Integer chosenRmId = null;
		if (StringUtils.equals(screenMode, CommonConstants.SCREEN_MODE_ADD)) {
			chosenRmId = infoRmId2;
			if (Objects.isNull(docCatId2)) {
				docCatId2 = (Integer) model.get("docCatId2Temp");
			}

			if (Objects.isNull(fileContent) || fileContent.getSize() == 0) {
				fileContent = (MultipartFile) model.get("fileContentTemp");
			}
			/*
			 * if(Objects.isNull(rmChoice) || rmChoice.length == 0) { rmChoice =
			 * (String[])model.get("rmChoice1Temp"); }
			 */
		} else {
			chosenRmId = rawMatlId;
		}

		holdValue(reglFileDto, docCatId2, fileContent, docCatId, newFileContent, null, model, screenMode, infoRmId2);

		logger.debug("addToInfoList() ... screenMode={},rmId={}", screenMode,chosenRmId);
		
		// validation
		{
			String errorMsg = "";
			// if no RM
			if (Objects.isNull(chosenRmId) || chosenRmId == 0) {
				errorMsg += commonValServ.validateMandatoryInput("", LBL_SEL_RM);
				model.put("error", errorMsg);
				return new ModelAndView("/main/material/reglFileRmForm", model);
			}
			
			// if RM duplicate
			List<ReglInfoDto> checkList = reglInfoList;
			for (ReglInfoDto reglInfoDto : checkList) {
				logger.debug("addToInfoList() loop id = "+reglInfoDto.getRmId());
				if(reglInfoDto.getRmId().intValue() == chosenRmId.intValue()) {
					logger.debug("addToInfoList() loop id={},chosenRmId={}"+reglInfoDto.getRmId(),chosenRmId);
					errorMsg += "RM "+reglInfoDto.getRmName()+" already in the list.";
					model.put("error", errorMsg);
					return new ModelAndView("/main/material/reglFileRmForm", model);
				}
			}
			
			// selection of RM must be within ${rmSelected}
			String[] selected = reglFileDto.getRmChoice();
			List<String> sel = Arrays.asList(selected);
			if (!sel.contains(String.valueOf(chosenRmId))) {
				errorMsg += "RM chosen " + rmServ.findRawMatlDto("", "", chosenRmId, 0).getRawMatlName()
						+ " not within the list of selected RM.";
				model.put("error", errorMsg);
				return new ModelAndView("/main/material/reglFileRmForm", model);
			}
		}
		
		
		int row = reglInfoList.size();
		ReglInfoDto dto = new ReglInfoDto();
		dto.setReglFileId(reglFileDto.getReglFileId());
		dto.setRmId(chosenRmId);
		dto.setRmName(Objects.nonNull(chosenRmId) ? rmServ.findRawMatlDto("", "", chosenRmId, 0).getRawMatlName() : "");
		dto.setRegName(infoRegName);
		dto.setRefUrl1(refUrl1);
		dto.setRefUrl2(refUrl2);
		dto.setRefUrl3(refUrl3);
		dto.setRefUrl4(refUrl4);
		dto.setRefUrl5(refUrl5);
		dto.setRemarks(remarks);
		dto.setRowNo(row);
		dto.setIndicator("new");
		reglInfoList.add(dto);

		model.put("reglInfoItems", reglInfoList);
		manageInfoButton(false, true, true, model);
		clearInfoForm(model);

		return new ModelAndView("/main/material/reglFileRmForm", model);
	}

	/**
	 * Update to Table list
	 * 
	 * @param request
	 * @param reglFileDto
	 * @param rawMatlId
	 * @param regName
	 * @param infoRegName
	 * @param refUrl1
	 * @param refUrl2
	 * @param refUrl3
	 * @param refUrl4
	 * @param refUrl5
	 * @param remarks
	 * @param session
	 * @return
	 */
	@SuppressWarnings("unchecked")
	@PostMapping(value = "/main/material/reglFileRmFormSave", params = "infoAction=update")
	public ModelAndView updateToInfoList(HttpServletRequest request, ReglFileDto reglFileDto,
			@RequestParam(name = "infoRowNo") int rowNo, @RequestParam(name = "infoRmId") int rawMatlId,
			@RequestParam(name = "infoRegName") String infoRegName, @RequestParam(name = "infoRef1") String refUrl1,
			@RequestParam(name = "infoRef2") String refUrl2, @RequestParam(name = "infoRef3") String refUrl3,
			@RequestParam(name = "infoRef4") String refUrl4, @RequestParam(name = "infoRef5") String refUrl5,
			@RequestParam(name = "infoRemark") String remarks,
			@RequestParam(name = "docCatId2", required = false) Integer docCatId2,
			@RequestParam(name = "fileContent", required = false) MultipartFile fileContent,
			@RequestParam(name = "docCatId", required = false) Integer docCatId,
			@RequestParam(name = "newFileContent", required = false) MultipartFile newFileContent,
			// @RequestParam(name = "rmChoice1", required = false) String[] rmChoice,
			@RequestParam(name = "infoRmId2", required = false) Integer infoRmId2, HttpSession session) {

		model.remove("error");
		model.put("currTab", "refTab2");
		holdValue(reglFileDto, docCatId2, fileContent, docCatId, newFileContent, null, model, screenMode, infoRmId2);
		List<ReglInfoDto> reglInfoList = (List<ReglInfoDto>) model.get("reglInfoItems");
		int chosenRmId = 0;
		// validation
		{
			boolean rm = true;
			if (StringUtils.equals(screenMode, CommonConstants.SCREEN_MODE_ADD)) {
				chosenRmId = infoRmId2;
				if (Objects.isNull(infoRmId2) || infoRmId2 == 0) {
					rm = false; 
				}
			} else {
				chosenRmId = rawMatlId;
				if (Objects.isNull(rawMatlId) || rawMatlId == 0) {
					rm = false;
				}
			}
			
			String errorMsg = "";
			if (!rm) {
				errorMsg += commonValServ.validateMandatoryInput("", LBL_SEL_RM);
				model.put("error", errorMsg);
				return new ModelAndView("/main/material/reglFileRmForm", model);
			}		
			
			// if RM duplicate
			List<ReglInfoDto> checkList = reglInfoList;
			for (ReglInfoDto reglInfoDto : checkList) {
				if(reglInfoDto.getRowNo() != rowNo && reglInfoDto.getRmId().intValue() == chosenRmId) {
					errorMsg += "RM "+reglInfoDto.getRmName()+" already in the list.";
					model.put("error", errorMsg);
					return new ModelAndView("/main/material/reglFileRmForm", model);
				}
			}
			
			// selection of RM must be within ${rmSelected}
			String[] selected = reglFileDto.getRmChoice();
			List<String> sel = Arrays.asList(selected);
			if (!sel.contains(String.valueOf(chosenRmId))) {
				errorMsg += "RM chosen " + rmServ.findRawMatlDto("", "", chosenRmId, 0).getRawMatlName()
						+ " not within the list of selected RM.";
				model.put("error", errorMsg);
				return new ModelAndView("/main/material/reglFileRmForm", model);
			}
		}

		logger.debug("updateToInfoList() ... rowNo={},rawMatlId={},infoRegName={}", rowNo, rawMatlId, infoRegName);
		logger.debug("updateToInfoList() ... refUrl1={},refUrl2={},refUrl3={}", refUrl1, refUrl2, refUrl3);

		/*
		reglInfoList.forEach(obj -> {
			if (obj.getRowNo() == rowNo) {
				obj.setRmId(StringUtils.equals(screenMode, CommonConstants.SCREEN_MODE_ADD) ? infoRmId2 : rawMatlId);
				obj.setRegName(infoRegName);
				obj.setRefUrl1(refUrl1);
				obj.setRefUrl2(refUrl2);
				obj.setRefUrl3(refUrl3);
				obj.setRefUrl4(refUrl4);
				obj.setRefUrl5(refUrl5);
				obj.setRemarks(remarks);
			}
		});
		*/
		
		for (ReglInfoDto obj : reglInfoList) {
			if (obj.getRowNo() == rowNo) {
				obj.setRmId(chosenRmId);
				obj.setRmName(Objects.nonNull(chosenRmId) ? rmServ.findRawMatlDto("", "", chosenRmId, 0).getRawMatlName() : "");
				obj.setRegName(infoRegName);
				obj.setRefUrl1(refUrl1);
				obj.setRefUrl2(refUrl2);
				obj.setRefUrl3(refUrl3);
				obj.setRefUrl4(refUrl4);
				obj.setRefUrl5(refUrl5);
				obj.setRemarks(remarks);
			}
		}

		model.put("reglInfoItems", reglInfoList);
		manageInfoButton(false, true, true, model);
		clearInfoForm(model);

		return new ModelAndView("/main/material/reglFileRmForm", model);
	}

	/**
	 * Delete from Table list
	 * 
	 * @param request
	 * @param reglFileDto
	 * @param rowNo
	 * @param rawMatlId
	 * @param regName
	 * @param infoRegName
	 * @param refUrl1
	 * @param refUrl2
	 * @param refUrl3
	 * @param refUrl4
	 * @param refUrl5
	 * @param remarks
	 * @param session
	 * @return
	 */
	@SuppressWarnings("unchecked")
	@PostMapping(value = "/main/material/reglFileRmFormSave", params = "infoAction=delete")
	public ModelAndView deleteFromInfoList(HttpServletRequest request, ReglFileDto reglFileDto,
			@RequestParam(name = "infoRowNo") int rowNo, @RequestParam(name = "infoRmId") int rawMatlId,
			@RequestParam(name = "infoRegName") String infoRegName, @RequestParam(name = "infoRef1") String refUrl1,
			@RequestParam(name = "infoRef2") String refUrl2, @RequestParam(name = "infoRef3") String refUrl3,
			@RequestParam(name = "infoRef4") String refUrl4, @RequestParam(name = "infoRef5") String refUrl5,
			@RequestParam(name = "infoRemark") String remarks,
			@RequestParam(name = "docCatId2", required = false) Integer docCatId2,
			@RequestParam(name = "fileContent", required = false) MultipartFile fileContent,
			@RequestParam(name = "docCatId", required = false) Integer docCatId,
			@RequestParam(name = "newFileContent", required = false) MultipartFile newFileContent,
			// @RequestParam(name = "rmChoice1", required = false) String[] rmChoice,
			@RequestParam(name = "infoRmId2", required = false) Integer infoRmId2, HttpSession session) {

		logger.debug("deleteFromInfoList() ... rawMatlId={},screenMode={},docCatId2={},rmChoice={},rowNo={}", rawMatlId,
				screenMode, docCatId2, "",rowNo);

		if (StringUtils.equals(screenMode, CommonConstants.SCREEN_MODE_ADD)) {
			if (Objects.isNull(docCatId2)) {
				docCatId2 = (Integer) model.get("docCatId2Temp");
			}
			if (Objects.isNull(fileContent) || fileContent.getSize() == 0) {
				fileContent = (MultipartFile) model.get("fileContentTemp");
			}
			/*
			 * if(Objects.isNull(rmChoice) || rmChoice.length == 0) { rmChoice =
			 * (String[])model.get("rmChoice1Temp"); }
			 */
		}

		model.put("currTab", "refTab2");
		holdValue(reglFileDto, docCatId2, fileContent, docCatId, newFileContent, null, model, screenMode, infoRmId2);

		List<ReglInfoDto> reglInfoList = (List<ReglInfoDto>) model.get("reglInfoItems");
		reglInfoList.remove(rowNo);

		int reDoRowNo = 0;
		for (int row = 0; row < reglInfoList.size(); row++) {
			reglInfoList.get(row).setRowNo(reDoRowNo);
			reDoRowNo++;
		}

		model.put("reglInfoItems", reglInfoList);
		manageInfoButton(false, true, true, model);
		clearInfoForm(model);

		return new ModelAndView("/main/material/reglFileRmForm", model);
	}

	/**
	 * Download file from hyperlink (view mode)
	 * 
	 * @param request
	 * @param rowNo
	 * @param session
	 * @return
	 */
	@GetMapping("/main/material/reglFileRmFormDownload")
	public ResponseEntity<byte[]> downloadFileView(HttpServletRequest request, ReglFileDto reglFileDto) {
		String fileName = "";
		byte[] data = new byte[1024];

		fileName = reglFileDto.getFileName();
		data = reglFileDto.getContentObject();

		MediaType mt = MediaTypeUtils.getMediaTypeForFileName(servletContext, fileName);

		return ResponseEntity.ok().contentType(mt)
				.header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + fileName + "\"").body(data);
	}

	/**
	 * Download file from hyperlink (search listing)
	 * 
	 * @param request
	 * @param reglFileId
	 * @return
	 */
	@GetMapping("/main/material/reglFileRmListDownload")
	public ResponseEntity<byte[]> downloadFileListing(HttpServletRequest request,
			@RequestParam(name = "reglFileId") int reglFileId) {
		String fileName = "";
		byte[] data = new byte[1024];

		ReglFileDto reglFileDto = rfServ.findDtoById(reglFileId);

		fileName = reglFileDto.getFileName();
		data = reglFileDto.getContentObject();

		MediaType mt = MediaTypeUtils.getMediaTypeForFileName(servletContext, fileName);

		return ResponseEntity.ok().contentType(mt)
				.header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + fileName + "\"").body(data);
	}

	@GetMapping(value = "/main/material/getCategoryItemsByCountry")
	@ResponseBody
	public Map<String, Object> getRegCatItemsByCountry(@RequestParam(name = "cId") String countryId,
			HttpServletRequest request, HttpSession session) {
		logger.debug("getRegCatItemsByCountry() countryId={}",countryId);
		
		List<RegulationCatDto> all = regCatServ.findAll().stream()
				.filter(arg0 -> ((arg0.getCountryList() != null ? arg0.getCountryList().contains(countryId) : false)))
				.collect(Collectors.toList());
		all.sort(Comparator.comparing(RegulationCatDto::getCatName));

		Map<String, Object> model = new HashMap<String, Object>();
		model.put("items", all);

		return model;
	}

	@SuppressWarnings("unchecked")
	@GetMapping(value = "/main/material/filterInfoRmByRmChoice")
	@ResponseBody
	public Map<String, Object> filterInfoRmByRmChoice(@RequestParam(name = "selected") String selected,
			ReglFileDto reglFileDto, HttpServletRequest request, HttpSession session) {
		logger.debug("filterInfoRmByRmChoice1v1() selected={}",selected);

		List<LabelAndValueDto> rm = (List<LabelAndValueDto>) model.get("rmSelectItems");

		if (StringUtils.isNotEmpty(selected)) {
			String[] rmChoice = selected.split(",");

			if (Objects.nonNull(rmChoice)) {
				List<Integer> selRm = Arrays.asList(rmChoice).stream().map(str -> Integer.valueOf(str))
						.collect(Collectors.toList());
				rm = rm.stream().filter(arg0 -> selRm.contains(arg0.getValue())).collect(Collectors.toList());
				rm.sort(Comparator.comparing(LabelAndValueDto::getLabel));
			}
		}

		Map<String, Object> model = new HashMap<String, Object>();
		model.put("items", rm);

		return model;
	}
	
	@RequestMapping(value = "/main/material/filterInfoRmByRmChoice1v2", method = RequestMethod.GET)
	public String filterInfoRmByRmChoice(@RequestParam(name = "selected") String selected, HttpSession session, ModelMap modelMap) {	
		logger.debug("filterInfoRmByRmChoice1v2() selected={}",selected);

		List<LabelAndValueDto> rm = rmServ.getRmNameLabelAndValue();
		if (StringUtils.isNotEmpty(selected)) {
			String[] rmChoice = selected.split(",");

			if (Objects.nonNull(rmChoice)) {
				List<Integer> selRm = Arrays.asList(rmChoice).stream().map(str -> Integer.valueOf(str))
						.collect(Collectors.toList());
				rm = rm.stream().filter(arg0 -> selRm.contains(arg0.getValue())).collect(Collectors.toList());
				rm.sort(Comparator.comparing(LabelAndValueDto::getLabel));
			}
		}
		logger.debug("filterInfoRmByRmChoice1v2() sel size={}",rm.size());
		model.put("rmSelectItems",rm);
		modelMap.addAllAttributes(model);

		return "/main/material/reglFileRmForm :: #selRM";
	}

	@PostMapping(value = "/main/material/reglFileRmFormSave", params = "action=edit")
	public ModelAndView editReglFileRmForm(HttpServletRequest request, HttpSession session) {

		screenMode = CommonConstants.SCREEN_MODE_EDIT;
		model.put("screenMode", screenMode);
		manageInfoButton(false, true, true, model);
		model.put("btnEdit", true);
		model.put("btnSaveSts", false);
		model.put("btnSave", "Save");
		model.put("header", "Edit Raw Material Regulation File Form");
		model.remove("success");
		model.remove("error");
		clearInfoForm(model);

		return new ModelAndView("/main/material/reglFileRmForm", model);
	}

	private String validateForm(ReglFileDto reglFileDto, String[] rmChoice, MultipartFile fileContent, String mode,
			Integer docCatId2) {
		rmChoice = reglFileDto.getRmChoice();
		String errorMsg = "";
		if (StringUtils.equals(mode, CommonConstants.SCREEN_MODE_ADD)) {
			if (Objects.isNull(fileContent) || fileContent.getOriginalFilename().equals("")) {
				errorMsg += "Upload file is mandatory.<br />";
			}

			reglFileDto.setDocCatId(docCatId2);
			model.put("docCatId2Temp", docCatId2);
		}

		errorMsg += commonValServ.validateMandatoryInput(
				reglFileDto.getDocCatGrpId() == null ? "" : reglFileDto.getDocCatGrpId().toString(), LBL_FILE_GRP);
		errorMsg += commonValServ.validateMandatoryInput(
				reglFileDto.getDocCatId() == null ? "" : reglFileDto.getDocCatId().toString(), LBL_FILE_CAT);
		// errorMsg += commonValServ.validateMandatoryInput(reglFileDto.getFileName(),
		// LBL_FILE_NAME);
		errorMsg += commonValServ.validateMandatoryInput(reglFileDto.getCountryId(), LBL_COUNTRY);
		
		if(Objects.nonNull(fileContent))
			errorMsg += commonValServ.validateFileSize(fileContent.getSize(), LBL_FILE_NAME);

		if (Objects.isNull(rmChoice) || (Objects.nonNull(rmChoice) && rmChoice.length <= 0)) {
			errorMsg += commonValServ.validateMandatoryInput("", LBL_RM);
		}

		{
			if (Objects.nonNull(fileContent) && StringUtils.isNotEmpty(reglFileDto.getCountryId())) {

				try {
					byte[] curr = fileContent.getBytes();
					List<ReglFileDto> fromDB = rfServ.findAllParentNativeDtoByCountry(reglFileDto.getCountryId());
					fromDB = fromDB.stream().filter(db -> db.getContentObject() != null)
							.filter(db -> Arrays.equals(db.getContentObject(), curr))
							.collect(Collectors.toList());

					if (!fromDB.isEmpty())
						errorMsg += "File " + fileContent.getOriginalFilename()
								+ " has already been uploaded for the same country.";
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			}
		}

		return errorMsg;
	}

	private List<LabelAndValueDto> getSelectedRm(int reglFileId) {
		List<ReglRmDto> rmList = rfServ.findAllRmByParent(reglFileId);
		List<Integer> rmIds = rmList.stream().map(obj -> obj.getRawMatlId()).collect(Collectors.toList());
		List<LabelAndValueDto> result = rmServ.getRmNameLabelAndValue();
		result = result.stream().filter(obj -> rmIds.contains(obj.getValue())).collect(Collectors.toList());
		return result;
	}

	private List<LabelAndValueDto> getRemainingRm(List<LabelAndValueDto> selected) {
		List<LabelAndValueDto> remaining = rmServ.getRmNameLabelAndValue();
		remaining = remaining.stream().filter(obj -> !selected.contains(obj)).collect(Collectors.toList());
		return remaining;
	}

	private Map<String, Object> removeAlert(Map<String, Object> model) {
		model.remove("error");
		model.remove("success");

		return model;
	}

	private Map<String, Object> manageInfoButton(boolean infoAdd, boolean infoUpdate, boolean infoDelete,
			Map<String, Object> model) {
		model.put("disInfoAdd", infoAdd);
		model.put("disInfoUpdate", infoUpdate);
		model.put("disInfoDelete", infoDelete);

		return model;
	}

	private Map<String, Object> clearInfoForm(Map<String, Object> model) {
		model.remove("infoRowNo");
		model.remove("infoReglInfoId");
		model.remove("infoRmId");
		model.remove("infoRmId2");
		model.remove("infoRmName");
		model.remove("infoRegName");
		model.remove("infoRef1");
		model.remove("infoRef2");
		model.remove("infoRef3");
		model.remove("infoRef4");
		model.remove("infoRef5");
		model.remove("infoRemark");

		return model;
	}

	private Map<String, Object> onPageLoadCommon(Map<String, Object> model) {
		model.put("fileGroupSelectItems", regGroupServ.findAll());
		model.put("fileCategorySelectItems", regCatServ.findAll());
		model.put("countrySelectItems",
				countryServ.findByCriteria("", "", "", "", String.valueOf(CommonConstants.FLAG_YES), ""));
		model.put("rmSelectItems", rmServ.getRmNameLabelAndValue());
		model.put("rmRemaining", rmServ.getRmNameLabelAndValue());
		model.put("currTab", "fileTab1");

		String screenMode = model.get("screenMode").toString();
		if (screenMode.equals(CommonConstants.SCREEN_MODE_ADD))
			model.put("editDiv", true);
		else
			model.put("editDiv", false);

		return model;
	}

	private Map<String, Object> holdValue(ReglFileDto reglFileDto, Integer docCatId2, MultipartFile fileContent,
			Integer docCatId, MultipartFile newFileContent, String[] rmChoice, Map<String, Object> model,
			String screenMode, Integer infoRmId2) {
		reglFileDto.setCountryName(countryServ.findOneById(reglFileDto.getCountryId()).getCountryName());
		model.put("reglFileItem", reglFileDto);
		model.put("screenMode", screenMode);

		if (StringUtils.equals(screenMode, CommonConstants.SCREEN_MODE_ADD)) {
			model.put("docCatId2Temp", docCatId2);
			model.put("fileContentTemp", fileContent);
			// model.put("rmChoice1Temp", rmChoice);
			// model.put("rmChoice1", rmChoice);
		} else if (StringUtils.equals(screenMode, CommonConstants.SCREEN_MODE_EDIT)) {
			model.put("docCatIdTemp", docCatId);
			model.put("newFileContentTemp", newFileContent);
		}

		return model;
	}

}
