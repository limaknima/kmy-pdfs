package com.fms.pfc.controller.main;

import java.io.IOException;
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
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
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
public class ReglFileController2 {
	private static final Logger logger = LoggerFactory.getLogger(ReglFileController2.class);

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
	private static final String EXISTING_FILE = "Existing File";
	private static final int ADD_TYPE_NEW = 1;
	private static final int ADD_TYPE_EXISTING = 2;

	@Autowired
	public ReglFileController2(Authority auth, CommonValidation commonValServ, ReglFileService rfServ,
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
	 * Create new form
	 * 
	 * @param request
	 * @return
	 */
	@GetMapping("/main/material/reglFileRmForm2")
	public ModelAndView loadNewForm(@RequestParam(name = "country", required = false) String countryId,
			@RequestParam(name = "matl", required = false) Integer matlId, HttpServletRequest request) {
		removeAlert(model);
		clearForm();

		// Set mode
		screenMode = CommonConstants.SCREEN_MODE_ADD;
		model.put("screenMode", screenMode);
		model.put("btnEdit", true);
		model.put("btnSaveSts", false);
		model.put("btnAction", false);
		model.put("btnSave", "Save");
		model.put("countryId", countryId);
		model.put("infoRmId", matlId);

		// Find current file list based on country
		List<ReglFileSearch> existing = rfServ.searchFileAndInfoByCriteria(0, countryId);
		existing = existing.stream().distinct().collect(Collectors.toList());
		existing.forEach(
				obj -> obj.setFileName(obj.getFgroup() + " > " + obj.getFcategory() + " > " + obj.getFileName()));

		if (Objects.nonNull(existing) && !existing.isEmpty()) {
			model.put("fileSelectItems", existing);
			model.put("addType", ADD_TYPE_EXISTING);
			model.put("hideExistRadio", false);
			model.put("hideExistDiv", false);
			model.put("hideNewDiv", true);

		} else {
			model.put("fileSelectItems", null);
			model.put("addType", ADD_TYPE_NEW);
			model.put("hideExistRadio", true);
			model.put("hideNewDiv", false);
			model.put("hideExistDiv", true);
			model.put("info", "No existing file/regulation for the selected country. Please proceed to create new.");
		}

		logger.debug("loadNewForm() countryId={},matlId={},fileSelectItems={}", countryId, matlId, existing.size());

		// Form item/fields
		ReglFileDto dto = new ReglFileDto();
		dto.setCountryId(countryId);
		model.put("reglFileItem", dto);
		onPageLoadCommon(model);

		// Set form header
		model.put("header", "Add RM Regulation File Form");
		// Set confirmation message
		model.put("confirmHeader", "Cancel confirmation");
		model.put("confirmMsg", "Do you want to cancel this record ?");

		return new ModelAndView("/main/material/reglFileRmForm2", model);
	}

	private void clearForm() {
		model.remove("infoRegName");
		model.remove("infoRef1");
		model.remove("infoRef2");
		model.remove("infoRef3");
		model.remove("infoRef4");
		model.remove("infoRef5");
		model.remove("infoRemark");
		model.remove("addType");
		model.remove("infoRmId");
		model.remove("countryId");
		model.remove("reglFileItem");
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
	@PostMapping(value = "/main/material/reglFileRmFormSave2", params = "action=save")
	public ModelAndView saveReglFileRm(@Valid ReglFileDto reglFileDto,
			@RequestPart(name = "fileContent", required = false) MultipartFile addModeFileContent,
			@RequestPart(name = "newFileContent", required = false) MultipartFile editModeFileContent,
			@RequestParam(name = "exFile", required = false) Integer exFile,
			@RequestParam(name = "addType", required = false) int addType,
			@RequestParam(name = "infoRegName", required = false) String infoRegName,
			@RequestParam(name = "infoRef1", required = false) String refUrl1,
			@RequestParam(name = "infoRef2", required = false) String refUrl2,
			@RequestParam(name = "infoRef3", required = false) String refUrl3,
			@RequestParam(name = "infoRef4", required = false) String refUrl4,
			@RequestParam(name = "infoRef5", required = false) String refUrl5,
			@RequestParam(name = "infoRemark", required = false) String remarks, BindingResult bindingResult,
			HttpSession session, HttpServletRequest request) throws Exception {

		logger.debug("saveReglFileRm() start ... id={},exFile={},addType={}", reglFileDto.getReglFileId(), exFile, addType);

		removeAlert(model);
		String mode = screenMode;
		MultipartFile finalFileContent = null;
		String errorMsg = "";

		if (addType == ADD_TYPE_EXISTING) {
			// validate
			{
				holdData(reglFileDto, exFile, addType, infoRegName, refUrl1, refUrl2, refUrl3, refUrl4, refUrl5, remarks,
						finalFileContent);
				errorMsg += commonValServ.validateMandatoryInput(
						exFile == null ? "" : exFile.toString(),
								EXISTING_FILE);
				if (StringUtils.isNotEmpty(errorMsg)) {
					model.put("error", errorMsg);
					
					model.put("reglFileItem", reglFileDto);
					
					if (mode.equals(CommonConstants.SCREEN_MODE_EDIT)) {
						screenMode = CommonConstants.SCREEN_MODE_EDIT;
						// Set mode to HTML header
						model.put("screenMode", screenMode);
						model.put("header", "RM Regulation File");
						// Set confirmation message
						model.put("confirmHeader", "Edit cancellation");
						model.put("confirmMsg", "Do you want to cancel edit this record ?");
					}
					
					return new ModelAndView("/main/material/reglFileRmForm2", model);
				}				
			}
			reglFileDto = rfServ.findDtoNoChildById(exFile);
		}
		model.put("reglFileItem", reglFileDto);

		if (StringUtils.equals(mode, CommonConstants.SCREEN_MODE_ADD)) {
			finalFileContent = addModeFileContent;

		} else if (StringUtils.equals(mode, CommonConstants.SCREEN_MODE_EDIT)) {
			if (Objects.nonNull(editModeFileContent))
				finalFileContent = editModeFileContent;
		}

		logger.debug("saveReglFileRm() ... id={},cat={},grp={},country={}", reglFileDto.getReglFileId(),
				reglFileDto.getDocCatId(), reglFileDto.getDocCatGrpId(), reglFileDto.getCountryId());

		holdData(reglFileDto, exFile, addType, infoRegName, refUrl1, refUrl2, refUrl3, refUrl4, refUrl5, remarks,
				finalFileContent);
		
		errorMsg += validateForm(reglFileDto, finalFileContent, mode, addType, infoRegName);
		if (StringUtils.isNotEmpty(errorMsg)) {
			model.put("error", errorMsg);
						
			model.put("reglFileItem", reglFileDto);

			if (mode.equals(CommonConstants.SCREEN_MODE_EDIT)) {
				screenMode = CommonConstants.SCREEN_MODE_EDIT;
				// Set mode to HTML header
				model.put("screenMode", screenMode);
				model.put("header", "RM Regulation File");
				// Set confirmation message
				model.put("confirmHeader", "Edit cancellation");
				model.put("confirmMsg", "Do you want to cancel edit this record ?");
			}

			return new ModelAndView("/main/material/reglFileRmForm2", model);
		}

		try {
			String sucessMsg = "";
			Integer rmId = (Integer) model.get("infoRmId");

			ReglInfoDto riDto = new ReglInfoDto();
			riDto.setRmId(rmId);
			riDto.setRegName(infoRegName);
			riDto.setRefUrl1(refUrl1);
			riDto.setRefUrl2(refUrl2);
			riDto.setRefUrl3(refUrl3);
			riDto.setRefUrl4(refUrl4);
			riDto.setRefUrl5(refUrl5);
			riDto.setRemarks(remarks);

			if (addType == ADD_TYPE_NEW) {
				// set file details
				if (Objects.nonNull(finalFileContent) && finalFileContent.getSize() > 0) {
					reglFileDto.setFileName(finalFileContent.getOriginalFilename());
					reglFileDto.setContentObject(finalFileContent.getBytes());
				}
				// ----
			}

			if (StringUtils.equals(mode, CommonConstants.SCREEN_MODE_ADD)) {
				errorMsg = "Record failed to be added.";
				sucessMsg = "Record added successfully.";

			} else if (StringUtils.equals(mode, CommonConstants.SCREEN_MODE_EDIT)) {
				// Set mode to HTML header
				screenMode = CommonConstants.SCREEN_MODE_VIEW;
				model.put("screenMode", screenMode);
				model.put("header", "RM Regulation File");

				errorMsg = "Record failed to be updated.";
				sucessMsg = "Record updated successfully.";
			}
			
			rfServ.saveReglFile(reglFileDto, rmId, riDto, addType, request.getRemoteUser());
			
			trxHistServ.addTrxHistory(new Date(), "Update RM Regulation File", request.getRemoteUser(),
					CommonConstants.FUNCTION_TYPE_UPDATE, reglFileDto.getFileName(),
					CommonConstants.RECORD_TYPE_ID_REGL, null);
			
			// Print add success
			model.put("success", sucessMsg);

		} catch (Exception e) {
			// Print DB exception
			e.printStackTrace();
			model.put("error", errorMsg);
		}
		// Set mode
		screenMode = CommonConstants.SCREEN_MODE_VIEW;
		model.put("screenMode", screenMode);
		model.put("btnEdit", false);
		model.put("btnSaveSts", true);
		model.put("btnAction", true);
		logger.debug("saveReglFileRm() finished");
		return new ModelAndView("/main/material/reglFileRmForm2", model);
	}

	protected void holdData(ReglFileDto reglFileDto, Integer exFile, int addType, String infoRegName, String refUrl1,
			String refUrl2, String refUrl3, String refUrl4, String refUrl5, String remarks,
			MultipartFile finalFileContent) {
		{
			// hold data
			model.put("reglFileItem", reglFileDto);
			model.put("exFile", exFile);
			model.put("infoRegName", infoRegName);
			model.put("infoRef1", refUrl1);
			model.put("infoRef2", refUrl2);
			model.put("infoRef3", refUrl3);
			model.put("infoRef4", refUrl4);
			model.put("infoRef5", refUrl5);
			model.put("infoRemark", remarks);
			model.put("fileContent", finalFileContent);
			model.put("addType", addType);
		}
	}

	/**
	 * Download file from hyperlink (view mode)
	 * 
	 * @param request
	 * @param rowNo
	 * @param session
	 * @return
	 */
	@GetMapping("/main/material/reglFileRmFormDownload2")
	public ResponseEntity<byte[]> downloadFileView(HttpServletRequest request, ReglFileDto reglFileDto) {
		String fileName = "";
		byte[] data = new byte[1024];

		fileName = reglFileDto.getFileName();
		data = reglFileDto.getContentObject();

		MediaType mt = MediaTypeUtils.getMediaTypeForFileName(servletContext, fileName);

		return ResponseEntity.ok().contentType(mt)
				.header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + fileName + "\"").body(data);
	}

	@PostMapping(value = "/main/material/reglFileRmFormSave2", params = "action=edit")
	public ModelAndView editReglFileRmForm(HttpServletRequest request, HttpSession session) {

		screenMode = CommonConstants.SCREEN_MODE_EDIT;
		model.put("screenMode", screenMode);
		model.put("btnEdit", true);
		model.put("btnSaveSts", false);
		model.put("btnSave", "Save");
		model.put("header", "Edit Raw Material Regulation File Form");
		model.remove("success");
		model.remove("error");
		clearInfoForm(model);

		return new ModelAndView("/main/material/reglFileRmForm2", model);
	}

	private String validateForm(ReglFileDto reglFileDto, MultipartFile fileContent, String mode, int addType,
			String infoRegName) {
		logger.debug("validateForm() start");
		String errorMsg = "";
		if (StringUtils.equals(mode, CommonConstants.SCREEN_MODE_ADD)) {
			if (addType == ADD_TYPE_NEW) {
				if (fileContent.getOriginalFilename().equals("")) {
					errorMsg += "Upload file is mandatory.<br />";
				}

				errorMsg += commonValServ.validateMandatoryInput(
						reglFileDto.getDocCatGrpId() == null ? "" : reglFileDto.getDocCatGrpId().toString(),
						LBL_FILE_GRP);
				errorMsg += commonValServ.validateMandatoryInput(
						reglFileDto.getDocCatId() == null ? "" : reglFileDto.getDocCatId().toString(), LBL_FILE_CAT);
				//errorMsg += commonValServ.validateMandatoryInput(infoRegName, LBL_REGL_NAME);
				
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
		}
		logger.debug("validateForm() end");

		return errorMsg;
	}

	private Map<String, Object> removeAlert(Map<String, Object> model) {
		model.remove("error");
		model.remove("success");
		model.remove("info");

		return model;
	}

	private Map<String, Object> clearInfoForm(Map<String, Object> model) {
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
		String countryId = (String) model.get("countryId");

		List<RegulationCatDto> all = regCatServ.findAll().stream()
				.filter(arg0 -> ((arg0.getCountryList() != null ? arg0.getCountryList().contains(countryId) : false)))
				.collect(Collectors.toList());
		all.sort(Comparator.comparing(RegulationCatDto::getCatName));

		model.put("fileGroupSelectItems", regGroupServ.findAll());
		model.put("fileCategorySelectItems", all);
		model.put("countrySelectItems",
				countryServ.findByCriteria("", "", "", "", String.valueOf(CommonConstants.FLAG_YES), ""));
		model.put("rmSelectItems", rmServ.getRmNameLabelAndValue());

		String screenMode = model.get("screenMode").toString();
		if (screenMode.equals(CommonConstants.SCREEN_MODE_ADD))
			model.put("editDiv", true);
		else
			model.put("editDiv", false);

		return model;
	}
}
