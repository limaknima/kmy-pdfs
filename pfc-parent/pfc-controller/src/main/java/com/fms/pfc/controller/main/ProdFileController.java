package com.fms.pfc.controller.main;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.StringTokenizer;
import java.util.stream.Collectors;

import javax.annotation.PreDestroy;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.disk.DiskFileItem;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
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
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.context.annotation.SessionScope;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.commons.CommonsMultipartFile;
import org.springframework.web.servlet.ModelAndView;

import com.fms.pfc.common.Authority;
import com.fms.pfc.common.CommonConstants;
import com.fms.pfc.common.CommonUtil;
import com.fms.pfc.common.MediaTypeUtils;
import com.fms.pfc.common.UsbDetectorUtil;
import com.fms.pfc.domain.dto.main.FileTypeSzDto;
import com.fms.pfc.domain.dto.main.FoldCatgConfDto;
import com.fms.pfc.domain.dto.main.G2LotViewDto;
import com.fms.pfc.domain.dto.main.ProdFileDto;
import com.fms.pfc.domain.dto.main.RelPathDto;
import com.fms.pfc.domain.dto.main.UsbConfDto;
import com.fms.pfc.domain.model.Usr;
import com.fms.pfc.domain.model.main.ProdFileSearch;
import com.fms.pfc.service.api.base.MenuRoleFunctionService;
import com.fms.pfc.service.api.base.TrxHisService;
import com.fms.pfc.service.api.main.FoldCatgConfService;
import com.fms.pfc.service.api.main.G2LotViewService;
import com.fms.pfc.service.api.main.ProdFileService;
import com.fms.pfc.service.api.main.RelPathService;
import com.fms.pfc.validation.common.CommonValidation;

import net.samuelcampos.usbdrivedetector.USBStorageDevice;

@Controller
@SessionScope
public class ProdFileController {
	private static final Logger logger = LoggerFactory.getLogger(ProdFileController.class);

	private Authority auth;
	private CommonValidation commonValServ;
	private MessageSource msgSource;
	private ProdFileService prodFileServ;
	private TrxHisService trxHistServ;
	private UsbDetectorUtil usbDet;
	private G2LotViewService g2LotServ;
	private FoldCatgConfService foldCatConfServ;
	private ServletContext servletContext;
	private RelPathService relPathServ;
	private MenuRoleFunctionService mrfServ;
	
	private Map<String, Object> model = new HashMap<String, Object>();
	private String screenMode = "";

	private static String HPL_LBL = "";
	private static String MODEL_LBL = "";
	private static String YEAR_LBL = "";
	private static String MONTH_LBL = "";
	private static String DAY_LBL = "";
	private static String PROD_LN_LBL = "";
	private static String FILE_NAME_LBL = "";
	private static String FILE_PATH_LBL = "";
	private static String G2_LOT_LBL = "";
	private static String MODULE_NAME = "";
	private static final int YEAR_COUNT = 20;
	private static final int MENU_ITEM_ID = 1201;
	private static final String BREAKLINE = ".<br />";

	@Autowired
	public ProdFileController(Authority auth, CommonValidation commonValServ, MessageSource msgSource,
			ProdFileService prodFileServ, TrxHisService trxHistServ, G2LotViewService g2LotServ, FoldCatgConfService foldCatConfServ, UsbDetectorUtil usbDet, ServletContext servletContext, RelPathService relPathServ,
			MenuRoleFunctionService mrfServ) {
		super();
		this.auth = auth;
		this.commonValServ = commonValServ;
		this.msgSource = msgSource;
		this.prodFileServ = prodFileServ;
		this.trxHistServ = trxHistServ;
		this.g2LotServ = g2LotServ;
		this.foldCatConfServ = foldCatConfServ;
		this.usbDet = usbDet;
		this.servletContext = servletContext;
		this.relPathServ = relPathServ;
		this.mrfServ = mrfServ;

		getAllLabels();
	}

	/**
	 * Display initial screen
	 * 
	 * @param request
	 * @param response
	 * @return
	 */
	@GetMapping("/main/pfc/prodFileList")
	public ModelAndView initListing(HttpServletRequest request, HttpServletResponse response) {

		removeAlert(model);
		
		model = auth.onPageLoad(model, request);
		auth.isAuthUrl(request, response);
		mrfServ.menuRoleFunction(model, MENU_ITEM_ID, (String) model.get("loggedUser"));

		filter(true);
		
		model.put("searchYearItems", CommonUtil.yearDropdownItems(YEAR_COUNT));
		model.put("searchMonthItems", CommonUtil.monthDropdownItems());
		model.put("searchDayItems", CommonUtil.dayDropdownItems());
		model.put("pathItems", CommonUtil.dayDropdownItems());
		model.put("btnEdit", true);
		model.put("btnSaveSts", false);
		model.put("btnAction", false);
		model.put("btnSave", "Save");
		

		return new ModelAndView("/main/pfc/prodFileList", model);
	}

	/**
	 * Record and criteria filtering
	 * @param init
	 */
	private void filter(boolean init) {
		boolean isSuperUser = (Boolean) model.get("isSuperUser");
		String grp = (String) model.get("loggedUserGrp");
		List<String> defaultHpl = g2LotServ.hplList();
		
		if (init) {
			// check if user is not super user, do filtering
			if (!isSuperUser) {
				model.put("prodFileAllItems", prodFileServ.searchByCriteria(grp, "", "", "", "", "", "", ""));
				model.put("searchHplItems",
						defaultHpl.stream().filter(arg0 -> arg0.equals(grp)).collect(Collectors.toList()));
				model.put("searchHplModelItems", g2LotServ.hplModelList(grp));

			} else {
				// if user is super user, remove filter
				model.put("prodFileAllItems", prodFileServ.searchByCriteria("", "", "", "", "", "", "", ""));
				model.put("searchHplItems", defaultHpl);
				model.put("searchHplModelItems", g2LotServ.hplModelList(""));
			}

		} else {
			// check if user is not super user, do filtering
			if (!isSuperUser) {
				model.put("hplItems",
						defaultHpl.stream().filter(arg0 -> arg0.equals(grp)).collect(Collectors.toList()));

			} else {
				model.put("hplItems", defaultHpl);
			}
		}
		
		// for search
		if (!isSuperUser) {
			model.put("filterHpl", grp);
		}
	}

	/**
	 * View form
	 * 
	 * @param request
	 * @param ltContId
	 * @return
	 */
	@RequestMapping("/main/pfc/prodFileFormView")
	public ModelAndView viewForm(HttpServletRequest request, @RequestParam(name = "pkPfileId") Integer pkPfileId) {

		removeAlert(model);

		ProdFileDto dto = prodFileServ.findDtoById(pkPfileId);
		trxHistServ.addTrxHistory(new Date(), "View " + MODULE_NAME, request.getRemoteUser(),
				CommonConstants.FUNCTION_TYPE_VIEW, dto.getPkPfileId().toString(),
				CommonConstants.RECORD_TYPE_ID_PROD_FILE, null);

		// Set mode
		screenMode = CommonConstants.SCREEN_MODE_VIEW;
		model.put("mode", screenMode);
		model.put("btnSaveSts", true);
		model.put("btnEdit", false);
		model.put("editDiv", false);

		// Set form header
		model.put("header", "View " + MODULE_NAME);

		// Set confirmation message
		model.put("confirmHeader", "Edit cancellation");
		model.put("confirmMsg", "Do you want to cancel Edit this record ?");

		// Put model to HTML
		if (dto != null) {

			model.put("pkPfileId", dto.getPkPfileId());
			model.put("prodFileItem", dto);
			model.put("prodFileItemCurrItem", dto);
			model.put("hplModelId2Temp", dto.getHModel());
			model.put("g2Lot2Temp", dto.getG2Lot());
			model.put("filePath2Temp", dto.getFilePath());
			model.put("prodLn2Temp", dto.getProdLn());

			model.put("hplItems", g2LotServ.hplList());
			model.put("hplModelItems", g2LotServ.hplModelList(dto.getHpl()));
			model.put("yearItems", CommonUtil.yearDropdownItems(YEAR_COUNT));
			model.put("monthItems", CommonUtil.monthDropdownItems());
			generateG2LotItems(dto, model);
			model.put("filePathItems", generatePathsItems(dto.getHpl(), dto.getYear(), dto.getProdLn(), dto.getMth()));
			model.put("prodLnItems",
					g2LotServ.prodLnList(dto.getHpl(), dto.getHModel(), dto.getYear(), dto.getMth(), ""));

			model.put("createdUser", dto.getCreatorId());
			model.put("modifiedUser", dto.getModifierId());

			SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyy hh:mm:ss a");
			try {
				String createdInfo = "Created by " + dto.getCreatorId() + " on "
						+ formatter.format(dto.getCreatedDatetime());
				model.put("createdInfo", createdInfo);
			} catch (Exception e) {
			}

			String modifiedInfo = "";
			try {
				modifiedInfo = "Modified by " + dto.getModifierId() + " on "
						+ formatter.format(dto.getModifiedDatetime());
			} catch (Exception e) {
				modifiedInfo = "Modified by --";
			}
			model.put("modifiedInfo", modifiedInfo);
		}

		return new ModelAndView("/main/pfc/prodFileForm", model);
	}

	/**
	 * Load add form
	 * 
	 * @param request
	 * @return
	 * @throws IOException
	 */
	@GetMapping("/main/pfc/prodFileForm")
	public ModelAndView loadNewForm(HttpServletRequest request) throws IOException {
		// Get USB info if any
		getUSBInfo();

		removeAlert(model);

		// Set mode
		screenMode = CommonConstants.SCREEN_MODE_ADD;
		model.put("mode", screenMode);

		// Set fields
		model.put("prodFileItem", new ProdFileDto());
		
		filter(false);
		
		// model.put("hplModelItems", prodFileServ.findHplModelSelectItems(0));
		model.put("yearItems", CommonUtil.yearDropdownItems(YEAR_COUNT));
		model.put("monthItems", CommonUtil.monthDropdownItems());
		// generateG2LotItems(null, model);

		// Set form header
		model.put("header", "Add " + MODULE_NAME);
		// Set confirmation message
		model.put("confirmHeader", "Cancel confirmation");
		model.put("confirmMsg", "Do you want to cancel this record ?");

		model.put("editDiv", true);

		return new ModelAndView("/main/pfc/prodFileForm", model);
	}

	/**
	 * Save form
	 * 
	 * @param dto
	 * @param request
	 * @param bindingResult
	 * @param session
	 * @return
	 * @throws Exception
	 */
	@PostMapping(value = "/main/pfc/prodFileFormSave", params = "action=save")
	public ModelAndView saveForm(@Valid ProdFileDto dto,
			@RequestPart(name = "fileContent") MultipartFile addModeFileContent,
			@RequestPart(name = "newFileContent") MultipartFile editModeFileContent,
			@RequestParam(name = "hplModelId2", required = false) String hplModelId2,
			@RequestParam(name = "fileContentName", required = false) String fileContentName,
			@RequestParam(name = "newFileContentName", required = false) String newFileContentName,
			@RequestParam(name = "g2Lot2", required = false) String g2Lot2,
			@RequestParam(name = "filePath2", required = false) String filePath2,
			@RequestParam(name = "prodLn2", required = false) String prodLn2, HttpServletRequest request,
			BindingResult bindingResult, HttpSession session) throws Exception {

		removeAlert(model);
		String mode = screenMode;
		MultipartFile finalFileContent = null;
		boolean editFileUpdate = false;

		dto.setHModel(hplModelId2);
		dto.setG2Lot(g2Lot2);
		dto.setFilePath(filePath2);
		dto.setProdLn(prodLn2);
		
		//hold value
		model.put("hplModelId2", hplModelId2);
		model.put("g2Lot2", g2Lot2);
		model.put("filePath2", filePath2);
		model.put("prodLn2", prodLn2);
		model.put("newFileContentName", newFileContentName);

		model.put("prodFileItem", dto);

		if (StringUtils.equals(mode, CommonConstants.SCREEN_MODE_ADD)) {
			finalFileContent = addModeFileContent;
			if (Objects.isNull(finalFileContent)) {
				finalFileContent = (MultipartFile) model.get("fileContentTemp");
				logger.debug("saveForm() add mode, editFileUpdate={}", editFileUpdate);
			}

		} else if (StringUtils.equals(mode, CommonConstants.SCREEN_MODE_EDIT)) {
			if (Objects.nonNull(editModeFileContent)) {
				finalFileContent = editModeFileContent;
				editFileUpdate = true;
				logger.debug("saveForm() edit mode, editFileUpdate={}", editFileUpdate);
			}
		}

		// If finalFileContent still null, suspect come from usb drive
		finalFileContent = setMultipartfileIfFromUSB(fileContentName, newFileContentName, mode, finalFileContent);

		String errorMsg = validateForm(dto, finalFileContent, mode, request.getRemoteUser());

		if (errorMsg.length() != 0) {
			model.put("error", errorMsg);
			model.put("prodFileItem", dto);
			model.put("hplModelId2Temp", hplModelId2);
			model.put("g2Lot2Temp", g2Lot2);
			model.put("filePath2Temp", filePath2);
			model.put("prodLn2Temp", prodLn2);

			if (mode.equals(CommonConstants.SCREEN_MODE_EDIT)) {
				screenMode = CommonConstants.SCREEN_MODE_EDIT;
				// Set mode to HTML header
				model.put("mode", screenMode);
				model.put("header", "Edit " + MODULE_NAME);
				// Set confirmation message
				model.put("confirmHeader", "Edit cancellation");
				model.put("confirmMsg", "Do you want to cancel edit this record ?");
			}
			return new ModelAndView("/main/pfc/prodFileForm", model);
		}

		// set file details
		if (Objects.nonNull(finalFileContent)) {
			logger.debug("saveForm() set file details, editFileUpdate={}, mode={}", editFileUpdate, mode);
			dto.setFileName(finalFileContent.getOriginalFilename());
//			FileUtils.checksumCRC32(finalFileContent.transferTo(new File("")));long fileSize = fileContent.getSize();
			dto.setFileType(finalFileContent.getContentType());
			dto.setFileSize(finalFileContent.getSize());
			dto.setContentObject(finalFileContent.getBytes());
//			dto.setCrcValue(crcValue);
		}

		// ----

		if (mode.equals(CommonConstants.SCREEN_MODE_ADD)) {

			try {
				// If no error proceed to save add form
				// Add form
				Integer result = prodFileServ.save(dto, request.getRemoteUser(), editFileUpdate, finalFileContent);

				// Print add success
				model.put("success", "Record added successfully.");
				model.remove("error");
				model.put("prodFileItem", new ProdFileDto());

				trxHistServ.addTrxHistory(new Date(), "Insert " + MODULE_NAME, request.getRemoteUser(),
						CommonConstants.FUNCTION_TYPE_INSERT, (null != result && result != 0) ? result.toString() : "",
						CommonConstants.RECORD_TYPE_ID_PROD_FILE, null);

			} catch (Exception e) {
				// Print DB exception
				e.printStackTrace();
				model.put("error", "Record failed to be added.");
				model.remove("success");
			}

		} else if (mode.equals(CommonConstants.SCREEN_MODE_EDIT)) {
			// Set mode to HTML header
			screenMode = CommonConstants.SCREEN_MODE_VIEW;
			model.put("mode", screenMode);
			model.put("header", MODULE_NAME);

			try {
				// Update form
				Integer result = prodFileServ.save(dto, request.getRemoteUser(), editFileUpdate, finalFileContent);

				// Print update success
				model.put("success", "Record updated successfully.");
				model.remove("error");

				trxHistServ.addTrxHistory(new Date(), "Update " + MODULE_NAME, request.getRemoteUser(),
						CommonConstants.FUNCTION_TYPE_UPDATE, (null != result && result != 0) ? result.toString() : "",
						CommonConstants.RECORD_TYPE_ID_PROD_FILE, null);

			} catch (Exception e) {
				// Print DB exception
				e.printStackTrace();
				model.put("error", "Record failed to update.");
				model.remove("success");
			}
		}

		model.put("btnEdit", false);
		
		String searchHplId = "";
		if (StringUtils.isNotEmpty((String) model.get("filterHpl")))
			searchHplId = (String) model.get("filterHpl");
		model.put("prodFileAllItems", prodFileServ.searchByCriteria(searchHplId, "", "", "", "", "", "", ""));

		return new ModelAndView("/main/pfc/prodFileList", model);
	}

	@PostMapping(value = "/main/pfc/prodFileFormSave", params = "action=edit")
	public ModelAndView editForm(HttpServletRequest request, HttpSession session) {

		getUSBInfo();

		screenMode = CommonConstants.SCREEN_MODE_EDIT;
		model.put("mode", screenMode);
		model.put("btnEdit", true);
		model.put("btnSaveSts", false);
		model.put("btnSave", "Save");
		model.put("header", "Edit " + MODULE_NAME);
		model.remove("success");
		model.remove("error");

		return new ModelAndView("/main/pfc/prodFileForm", model);
	}

	/**
	 * Delete item
	 * 
	 * @param request
	 * @param check
	 * @param session
	 * @return
	 */
	@RequestMapping("/main/pfc/prodFileFormDel")
	public ModelAndView deleteBatch(HttpServletRequest request, @RequestParam(value = "tableRow") String[] check,
			HttpSession session) {

		model.remove("error");
		model.remove("success");
		try {
			// Delete form
			for (int i = 0; i < check.length; i++) {

				if (fileExists(Integer.parseInt(check[i]))) {
					// Print delete failed
					model.put("error", "Record is in used and it is not allowed to deleted from the system.");
				} else {
					prodFileServ.delete(Integer.parseInt(check[i]));
					// Print delete success`
					model.put("success",
							msgSource.getMessage("msgSuccessDelete", new Object[] {}, Locale.getDefault()));

					trxHistServ.addTrxHistory(new Date(), "Delete " + MODULE_NAME, request.getRemoteUser(),
							CommonConstants.FUNCTION_TYPE_DELETE, check[i], CommonConstants.RECORD_TYPE_ID_PROD_FILE,
							null);
				}
			}

		} catch (Exception e) {
			// Print DB exception
			model.put("error", msgSource.getMessage("msgFailDelete", new Object[] {}, Locale.getDefault()));
			e.printStackTrace();
		}

		String searchHplId = "";
		if (StringUtils.isNotEmpty((String) model.get("filterHpl")))
			searchHplId = (String) model.get("filterHpl");
		
		model.put("prodFileAllItems", prodFileServ.searchByCriteria(searchHplId, "", "", "", "", "", "", ""));

		return new ModelAndView(("/main/pfc/prodFileList"), model);
	}
	
	/**
	 * Do search
	 * 
	 * @param prName
	 * @param ltType
	 * @param prNameExp
	 * @param session
	 * @return
	 */
	@PostMapping("/main/pfc/prodFileSearch")
	public ModelAndView search(HttpServletRequest request, @RequestParam(name = "searchHplId") String searchHplId,
			@RequestParam(name = "searchHplModelId") String searchHplModelId,
			@RequestParam(name = "searchYear") String searchYear, @RequestParam(name = "searchMth") String searchMth,
			@RequestParam(name = "searchG2Lot") String searchG2Lot,
			@RequestParam(name = "searchG2LotExp") String searchG2LotExp,
			@RequestParam(name = "searchPath") String searchPath,
			@RequestParam(name = "searchPathExp") String searchPathExp, HttpSession session) {

		boolean hasError = false;

		removeAlert(model);
		String errorMsg = "";
		
		if (StringUtils.isNotEmpty((String) model.get("filterHpl")))
			searchHplId = (String) model.get("filterHpl");

		model.put("searchHplId", searchHplId);
		model.put("searchHplModelId", searchHplModelId);
		model.put("searchYear", searchYear);
		model.put("searchMth", searchMth);
		model.put("searchG2Lot", searchG2Lot);
		model.put("searchG2LotExp", searchG2LotExp);
		model.put("searchPath", searchPath);
		model.put("searchPathExp", searchPathExp);

		try {
			if (errorMsg.length() == 0) {

				List<ProdFileSearch> items = prodFileServ.searchByCriteria(searchHplId, searchHplModelId, searchYear,
						searchMth, searchG2Lot, searchG2LotExp, searchPath, searchPathExp);
				model.put("prodFileAllItems", items);

				StringBuffer sb = new StringBuffer();
				sb.append("Search Criteria: ");
				sb.append(HPL_LBL).append("=").append(searchHplId.isEmpty() ? "<ALL>" : searchHplId).append(", ");
				sb.append(MODEL_LBL).append("=").append(searchHplModelId.isEmpty() ? "<ALL>" : searchHplModelId)
						.append(", ");
				sb.append(YEAR_LBL).append("=").append(searchYear.isEmpty() ? "<ALL>" : searchYear).append(", ");
				sb.append(MONTH_LBL).append("=").append(searchMth.isEmpty() ? "<ALL>" : searchMth).append(", ");
				sb.append(G2_LOT_LBL).append("=").append(searchG2Lot.isEmpty() ? "<ALL>" : searchG2Lot).append(", ");
				sb.append(FILE_PATH_LBL).append("=").append(searchPath.isEmpty() ? "<ALL>" : searchPath);

				trxHistServ.addTrxHistory(new Date(), "Search " + MODULE_NAME, request.getRemoteUser(),
						CommonConstants.FUNCTION_TYPE_SEARCH, "Search " + MODULE_NAME,
						CommonConstants.RECORD_TYPE_ID_PROD_FILE, sb.toString());

			} else {
				hasError = true;
			}

		} catch (Exception e) {
			e.printStackTrace();
			hasError = true;
			errorMsg += "Failed to get record.";
			errorMsg += e.toString();

		} finally {
			if (hasError == true) {
				model.put("error", errorMsg);
				// return back user input
				model.put("prodFileAllItems", prodFileServ.searchByCriteria(searchHplId, "", "", "", "", "", "", ""));
			}
		}

		return new ModelAndView("/main/pfc/prodFileList", model);
	}

	@GetMapping(value = "/main/pfc/loadCompletedG2Lot")
	@ResponseBody
	public Map<String, Object> loadCompletedG2Lot(ProdFileDto dto,
			@RequestParam(name = "hplId", required = false) String hplId,
			@RequestParam(name = "hplModelId2", required = false) String hplModelId2,
			@RequestParam(name = "year", required = false) String year,
			@RequestParam(name = "mth", required = false) String mth,
			@RequestParam(name = "prodLn2", required = false) String prodLn2, HttpServletRequest request,
			HttpSession session) {

		Map<String, Object> respObjMap = new HashMap<String, Object>();

		// model.put("prodLn2Temp", prodLn2);

		// G2 lot dropdown items
		List<G2LotViewDto> items = generateG2LotItems(year, mth, prodLn2, hplId, hplModelId2);
		respObjMap.put("items", items);

		// Prod ln dropdown items
		List<G2LotViewDto> itemsPrd = items;
		List<String> prodLnList = generateProdLnItems(itemsPrd);
		respObjMap.put("prodLnItems", prodLnList);

		// Path dropdown items
		List<RelPathDto> paths = generatePathsItems(hplId, year, prodLn2, mth);
		respObjMap.put("pathsItems", paths);

		logger.debug(
				"loadCompletedG2Lot() hpl={}, hplModelId2={}, year={}, mth={}, prodLn2={}, lot size={}, path size={}",
				hplId, hplModelId2, year, mth, prodLn2, items.size(), paths.size());
		return respObjMap;
	}
	
	@GetMapping("/main/pfc/prodFileDownload")
	public ResponseEntity<byte[]> prodFileDownload(HttpServletRequest request,
			@RequestParam(name = "pkPfileId") int pkPfileId) {
		String fileName = "";
		byte[] data = new byte[1024];

		ProdFileDto pfDto = prodFileServ.findDtoById(pkPfileId);
		RelPathDto relPath = relPathServ.findDtoById(Integer.valueOf(pfDto.getFilePath()));
		Path p = Paths.get(relPath.getFilePath()+ File.separator + pfDto.getFileName());
		//fileName = pfDto.getFileName();
		fileName = p.getFileName().toString();
		try {
			data = Files.readAllBytes(p);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		//data = reglFileDto.getContentObject();
//		data = pfDto.getContentObject();

		MediaType mt = MediaTypeUtils.getMediaTypeForFileName(servletContext, fileName);

		return ResponseEntity.ok().contentType(mt)
				.header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + fileName + "\"").body(data);
	}

	@PreDestroy
	public void destroy() throws IOException {
		if (Objects.nonNull(usbDet.getUsbDetMan())) {
			usbDet.destroy();
		}
	}

	/**
	 * Construct error message based on validation result
	 * 
	 * @param dto
	 * @return
	 */
	private String validateForm(ProdFileDto dto, MultipartFile fileContent, String mode, String userId) {
		String errorMsg = "";
		if (StringUtils.equals(mode, CommonConstants.SCREEN_MODE_ADD)) {
			if (Objects.isNull(fileContent) || StringUtils.isEmpty(fileContent.getOriginalFilename())) {
				errorMsg += "Upload file is mandatory"+BREAKLINE;
			}
		}

		if (Objects.nonNull(fileContent)) {
			errorMsg += commonValServ.validateFileSize(fileContent.getSize(), FILE_NAME_LBL);
		}

		errorMsg += commonValServ.validateMandatoryInput(dto.getHpl(), HPL_LBL);
		errorMsg += commonValServ.validateMandatoryInput(dto.getProdLn(), PROD_LN_LBL);
		errorMsg += commonValServ.validateMandatoryInput(dto.getFilePath(), FILE_PATH_LBL);
		errorMsg += commonValServ.validateMandatoryInput(dto.getYear(), YEAR_LBL);

		/*
		 * Rules: 1. File must be from the registered USB which has been assigned to a
		 * particular user (Refer Program ID I004). >> If selected folder based on
		 * HPL,Year, Prod Ln not match to selected file in USB - system will warn user
		 * and prevent from upload 2. File will be validated based on File Size/Type
		 * configuration (Refer Program ID I003). >> File below minimum size - system
		 * wont proceed upload >> File exceed maximum size - system will proceed upload
		 * and warn the user 3. Path field dropdown will be based on HPL selection (path
		 * registered - Refer to Program ID I001)
		 */
		if (StringUtils.isEmpty(errorMsg) && (Objects.nonNull(fileContent))) {
			String fileName = fileContent.getOriginalFilename();
			long fileSize = fileContent.getSize();
			String fileType = fileContent.getContentType();
			String fileExt = FilenameUtils.getExtension(fileName);
			Path path = Paths.get(fileName);

			logger.debug("validateForm() currentUser={}, fileName={}, fileSize={}, fullPath={}, fileExt={}", userId,
					fileName, FileUtils.byteCountToDisplaySize(fileSize), path.toAbsolutePath(), fileExt);

			// 1
			errorMsg += validateUsbUsrConf(dto, userId, errorMsg);
			// 2
			errorMsg += validateFileTypeAndSize(dto, userId, errorMsg, fileExt, fileSize);
			// 3
			errorMsg += validateFileNameAgainstG2Lot(dto, fileName, errorMsg);
		}

//
//		if (screenMode == CommonConstants.SCREEN_MODE_ADD) {
//			errorMsg += validateDuplicateFlavStatus(dto.getLtName());
//		} else {
//
//			if (null != model.get("letterTypeCurrItem")) {
//				LetterTypeDto currDto = (LetterTypeDto) model.get("letterTypeCurrItem");
//
//				logger.debug("validateForm cur name=" + currDto.getLtName() + "; new name=" + dto.getLtName());
//				if (!StringUtils.equalsIgnoreCase(currDto.getLtName(), dto.getLtName())) {
//					// check if current name value modified
//					errorMsg += validateDuplicateFlavStatus(dto.getLtName());
//
//				}
//			}
//		}

		return errorMsg;
	}
	

	/**
	 * Validate file type and size
	 * 
	 * @param dto
	 * @param userId
	 * @param errorMsg
	 * @return errorMsg
	 */
	private String validateFileTypeAndSize(ProdFileDto dto, String userId, String errorMsg, String fileType,
			long fileSize) {
		List<FileTypeSzDto> fstList = prodFileServ.findFileTypeSzList(dto.getHpl());
		if (!fstList.isEmpty()) {
			// TODO: need to loop as one hpl could have multiple settings
			int ftypeCnt = 0;
			for (FileTypeSzDto fstDto : fstList) {
				if (StringUtils.equalsIgnoreCase(fileType, fstDto.getFileType())) {
					ftypeCnt++;
					// check min and max file size
					if (fileSize < fstDto.getMinSize()) {
						errorMsg += "Minimum File size must be minimum " + fstDto.getMinSize() + " KB"+BREAKLINE;
					} else if (fileSize > fstDto.getMaxSize()) {
						model.put("info", "Take note that File size has exceeded the maximum file size configured.");
					}
				}
			}

			if (ftypeCnt == 0) {
				errorMsg += "File type/extension=" + fileType + " for the file has not been configured for HPL="
						+ dto.getHpl()+BREAKLINE;
			}

		} else {
			errorMsg += "No file type and size configuration found for this HPL=" + dto.getHpl()+BREAKLINE;
		}
		return errorMsg;
	}

	/**
	 * Validate USB-USR configuration
	 * 
	 * @param dto
	 * @param userId
	 * @param errorMsg
	 * @return errorMsg
	 */
	private String validateUsbUsrConf(ProdFileDto dto, String userId, String errorMsg) {
		if (!usbDet.getUsbDevices().isEmpty()) {
			USBStorageDevice dev = usbDet.getOneDeviceInfo();
			// check conf from db
			List<UsbConfDto> usbConfList = dev != null ? prodFileServ.findUsbConf(dev.getDeviceName(), dev.getUuid())
					: new ArrayList<UsbConfDto>();

			if (!usbConfList.isEmpty()) {
				// check if match hpl and prod ln
				UsbConfDto usbConf = usbConfList.get(0);
				if (!StringUtils.equals(usbConf.getHpl(), dto.getHpl())
						|| !StringUtils.equalsIgnoreCase(usbConf.getProdLn(), dto.getProdLn())) {
					errorMsg += "No configuration found for USB=" + usbConf.getName() + ", Serial No="
							+ usbConf.getSerialNo() + ", HPL=" + usbConf.getHpl() + ",Prod Ln=" + usbConf.getProdLn()+BREAKLINE;
				}
				// check if current user is permitted
				List<Usr> usrList = prodFileServ.findUsbUsrByParent(usbConf.getPkUconfId());
				if (!usrList.isEmpty()) {
					usrList = usrList.stream().filter(arg0 -> arg0.getUserId() == userId).collect(Collectors.toList());
					if (usrList.isEmpty()) {
						errorMsg += "Current user " + userId + " is not configured to be an uploader from this device"+BREAKLINE;
					}
				} else {
					// user list empty
					errorMsg += "No user is configured to be an uploader from this device"+BREAKLINE;
				}
			} else {
				// no conf found
				errorMsg += "No configuration found for USB=" + dev.getDeviceName() + ", Serial No=" + dev.getUuid()+BREAKLINE;
			}

		} else {
			// if no usb devices detected
			// file could be coming from local drive/folder
			//logger.debug("validateUsbUsrConf() No usb devices detected! File could be coming from local drive/folder.");
			model.put("info", "No usb devices detected! File could be uploaded could be from local drive/folder.");
		}
		return errorMsg;
	}
	
	/**
	 * Validate file name against G2 lot information.
	 * 
	 * @param dto
	 * @param fileName
	 * @param errorMsg
	 * @return errorMsg
	 */
	private String validateFileNameAgainstG2Lot(ProdFileDto pfDto, String originalFileName, String errorMsg) {
		logger.debug("Validating HPL={}, File={}", pfDto.getHpl(), originalFileName);

		if (StringUtils.isEmpty(originalFileName) || StringUtils.isEmpty(pfDto.getHpl())) {
			errorMsg += "No file/HPL selected for upload! File={"+originalFileName+"}, HPL={"+pfDto.getHpl()+"}"+BREAKLINE;
			return errorMsg;
		}
		
		String fileFormat = "";
		String path = "";
		List<FoldCatgConfDto> confList = foldCatConfServ.searchByCriteria(pfDto.getHpl(), pfDto.getHModel(), pfDto.getYear(), "", "", pfDto.getProdLn(), "", "");
		if(!confList.isEmpty()) {			
			// check first if hpl is not gtms
			// as gtms file format will be based on the selected folder
			
			for (FoldCatgConfDto confDto : confList) {
				if (StringUtils.equalsIgnoreCase(confDto.getHpl(), CommonConstants.RECORD_TYPE_ID_HPL_GTMS)) {
					// check format form relative path folder
					List<RelPathDto> relPathList = foldCatConfServ.findRelPathListByParent(confDto.getPkCatgId());
					if(!relPathList.isEmpty()) {
						for (RelPathDto pathDto : relPathList) {
							if(pathDto.getPkRelPathId() == Integer.valueOf(pfDto.getFilePath())) {
								path = pathDto.getFilePath();
								fileFormat = pathDto.getProdFileFormat();
								if (StringUtils.isEmpty(fileFormat)) {
									errorMsg += "File format not configured for HPL={" + pfDto.getHpl() + "} and folder={"+pathDto.getFilePath()+"}"+BREAKLINE;
								}
							}
						}
					} else {
						errorMsg += "Path and File format configuration not found for HPL={"+pfDto.getHpl()+"}"+BREAKLINE;
					}
					
				} else {
					// other than gtms
					fileFormat = confDto.getProdFileFormat();
					if (StringUtils.isEmpty(fileFormat)) {
						errorMsg += "File format not configured for HPL={" + pfDto.getHpl() + "}"+BREAKLINE;
					}
				}
			}
			
		} else {
			errorMsg += "Folder-Category configuration not found for HPL={"+pfDto.getHpl()+"}"+BREAKLINE;
		}
		
		if(!StringUtils.isEmpty(errorMsg)) {
			return errorMsg;
		}
		
		FoldCatgConfDto currentConf = confList.get(0);

		// remove file extension
		originalFileName = StringUtils.substring(originalFileName, 0, originalFileName.lastIndexOf("."));
		int nameLength = originalFileName.length();

		logger.debug("File name={}, length={}, hpl={}", originalFileName, nameLength, pfDto.getHpl());

		String modelFile = "";
		String yearFile = "";
		String monthFile = "";
		String dayFile = "";
		String prodLnFile = "";
		String seqFile = "";
		String lotFile = "";
		
		StringTokenizer items = new StringTokenizer(fileFormat, "|");
		while (items.hasMoreElements()) {
			String item = (String) items.nextElement();
			String key = item.split("=")[0];
			String val = item.split("=")[1];

			if (StringUtils.isEmpty(key))
				continue;

			if (StringUtils.isEmpty(val) || StringUtils.equalsIgnoreCase(val, "na"))
				continue;

			if (val.split(",").length < 2)
				continue;

			int post = Integer.valueOf(val.split(",")[0]);
			int len = Integer.valueOf(val.split(",")[1]);

			String result = StringUtils.substring(originalFileName, post - 1, post - 1 + len);

			if (key.equalsIgnoreCase(CommonConstants.HPL_LOT_KEY_MODEL)) {
				modelFile = result;
				logger.debug("post={}, len={}, model={}", post, len, modelFile);
			} else if (key.equalsIgnoreCase(CommonConstants.HPL_LOT_KEY_YEAR)) {
				yearFile = result;
				if (StringUtils.isNotEmpty(yearFile) && yearFile.length() == 2) {
					yearFile = "20" + yearFile;
				}
				logger.debug("post={}, len={}, year={}", post, len, yearFile);
			} else if (key.equalsIgnoreCase(CommonConstants.HPL_LOT_KEY_MONTH)) {
				monthFile = result;
				if (StringUtils.isNotEmpty(monthFile) && monthFile.length() == 1) {
					monthFile = "0" + monthFile;
				}
				logger.debug("post={}, len={}, month={}", post, len, monthFile);
			} else if (key.equalsIgnoreCase(CommonConstants.HPL_LOT_KEY_DAY)) {
				dayFile = result;
				if (StringUtils.isNotEmpty(dayFile) && dayFile.length() == 1) {
					dayFile = "0" + dayFile;
				}
				logger.debug("post={}, len={}, day={}", post, len, dayFile);
			} else if (key.equalsIgnoreCase(CommonConstants.HPL_LOT_KEY_PRODLN)) {
				prodLnFile = result;
				logger.debug("post={}, len={}, prodLn={}", post, len, prodLnFile);
			} else if (key.equalsIgnoreCase(CommonConstants.HPL_LOT_KEY_SEQ)) {
				seqFile = result;
				logger.debug("post={}, len={}, seq={}", post, len, seqFile);
			} else if (key.equalsIgnoreCase(CommonConstants.HPL_LOT_KEY_LOT)) {
				lotFile = result;
				logger.debug("post={}, len={}, lot={}", post, len, lotFile);
			}
		}
		
		if(StringUtils.equals(pfDto.getHpl(), CommonConstants.RECORD_TYPE_ID_HPL_IF)) {
			// IF
			// file -> 220517_KX225HD.csv -> lot KX225HD -> model=KX, y=2022, m=5, d=H, prdLn=D
			// format -> model=8,2|year=1,2|month=3,2|day=5,2|prodLn=14,1|seq=NA|lot=8,7 
			if(!StringUtils.equalsIgnoreCase(lotFile, pfDto.getG2Lot())) {
				errorMsg +=CommonConstants.RECORD_TYPE_ID_HPL_IF+" File >> Lot not matched"+BREAKLINE;
			}
			if(StringUtils.isNotEmpty(currentConf.getHModel()) 
					&& !StringUtils.equalsIgnoreCase(modelFile, currentConf.getHModel())) {
				errorMsg +=CommonConstants.RECORD_TYPE_ID_HPL_IF+" File >> Model not matched"+BREAKLINE;				
			}			
			if(!StringUtils.equalsIgnoreCase(yearFile, currentConf.getYear())) {
				errorMsg +=CommonConstants.RECORD_TYPE_ID_HPL_IF+" File >> Year not matched"+BREAKLINE;				
			}			
			if (StringUtils.isNotEmpty(currentConf.getMth()) && StringUtils.isNotEmpty(monthFile)) {
				// convert month from file
				String temp = CommonUtil.monthConversionFromFileName(monthFile, currentConf.getHpl());
				if (!StringUtils.equalsIgnoreCase(temp, currentConf.getMth())) {
					errorMsg += CommonConstants.RECORD_TYPE_ID_HPL_IF + " File >> Month not matched" + BREAKLINE;
				}
			}		
			if (StringUtils.isNotEmpty(currentConf.getDay()) && StringUtils.isNotEmpty(dayFile)) {
				// convert month from file
				String temp = CommonUtil.dayConversionFromFileName(dayFile, currentConf.getHpl());
				if (!StringUtils.equalsIgnoreCase(temp, currentConf.getDay())) {
					errorMsg += CommonConstants.RECORD_TYPE_ID_HPL_IF + " File >> Day not matched" + BREAKLINE;
				}
			}	
			if(StringUtils.isNotEmpty(prodLnFile)) {
				// convert prodLn from file
				String temp = CommonUtil.prodLnConversionFromFileName(prodLnFile, currentConf.getHpl());
				if(!StringUtils.equalsIgnoreCase(temp, pfDto.getProdLn())) {
					errorMsg +=CommonConstants.RECORD_TYPE_ID_HPL_IF+" File >> Prod Line not matched"+BREAKLINE;				
				}			
			}
			
		} else if(StringUtils.equals(pfDto.getHpl(), CommonConstants.RECORD_TYPE_ID_HPL_MGG)) {
			// MGG
			// file >> 569_2021073107480.csv -> lot KMY569210731M1 -> model=569, y=2021, m=7, d=31, prdLn=M1
			// format >> model=1,3|year=5,4|month=9,2|day=11,2|prodLn=NA|seq=NA|lot=NA
			if(StringUtils.isNotEmpty(currentConf.getHModel()) 
					&& !StringUtils.equalsIgnoreCase(modelFile, currentConf.getHModel())) {
				errorMsg +=CommonConstants.RECORD_TYPE_ID_HPL_MGG+" File >> Model not matched"+BREAKLINE;				
			}			
			if(!StringUtils.equalsIgnoreCase(yearFile, currentConf.getYear())) {
				errorMsg +=CommonConstants.RECORD_TYPE_ID_HPL_MGG+" File >> Year not matched"+BREAKLINE;				
			}			
			if(StringUtils.isNotEmpty(currentConf.getMth()) 
					&& !StringUtils.equalsIgnoreCase(monthFile, currentConf.getMth())) {
				errorMsg +=CommonConstants.RECORD_TYPE_ID_HPL_MGG+" File >> Month not matched"+BREAKLINE;				
			}		
			if(StringUtils.isNotEmpty(currentConf.getDay()) 
					&& !StringUtils.equalsIgnoreCase(dayFile, currentConf.getDay())) {
				errorMsg +=CommonConstants.RECORD_TYPE_ID_HPL_MGG+" File >> Month not matched"+BREAKLINE;				
			}
			if(!StringUtils.equalsIgnoreCase(prodLnFile, currentConf.getProdLn())) {
				errorMsg +=CommonConstants.RECORD_TYPE_ID_HPL_MGG+" File >> Prod Line not matched"+BREAKLINE;				
			}			
		} else if(StringUtils.equals(pfDto.getHpl(), CommonConstants.RECORD_TYPE_ID_HPL_GTMS)) {
			// GTMS
			// file >> Mikron cell 1,2.1,3 KMY210104504.txt -> lot 500421403104 -> model=5004, y=2021, m=4, d=0, prdLn=3, seq=104
			// format >> model=NA|year=4,2|month=NA|day=NA|prodLn=NA|seq=6,4|lot=NA
			if(StringUtils.containsIgnoreCase(path, "mikron")) {
				
			}
			
			// GTMS
			// file >> Back end FET2,3 5004_500421403104.csv -> lot 500421403104 -> model=5004, y=2021, m=4, d=0, prdLn=3, seq=104
			// format >> model=6,4|year=10,2|month=12,1|day=NA|prodLn=13,2|seq=15,3|lot=NA
			if(StringUtils.containsIgnoreCase(path, "fet#2") || StringUtils.containsIgnoreCase(path, "fet#3")) {
				if (StringUtils.isNotEmpty(currentConf.getHModel())
						&& !StringUtils.equalsIgnoreCase(modelFile, currentConf.getHModel())) {
					errorMsg += CommonConstants.RECORD_TYPE_ID_HPL_GTMS + " FET2, FET3 File >> Model not matched"
							+ BREAKLINE;
				}
				if (!StringUtils.equalsIgnoreCase(yearFile, currentConf.getYear())) {
					errorMsg += CommonConstants.RECORD_TYPE_ID_HPL_GTMS + " FET2, FET3 File >> Year not matched"
							+ BREAKLINE;
				}
				if (StringUtils.isNotEmpty(currentConf.getMth())
						&& !StringUtils.equalsIgnoreCase(monthFile, currentConf.getMth())) {
					errorMsg += CommonConstants.RECORD_TYPE_ID_HPL_GTMS + " FET2, FET3 File >> Month not matched"
							+ BREAKLINE;
				}
				if (!StringUtils.equalsIgnoreCase(prodLnFile, currentConf.getProdLn())) {
					errorMsg += CommonConstants.RECORD_TYPE_ID_HPL_GTMS + " FET2, FET3 File >> Prod Line not matched"
							+ BREAKLINE;
				}
				if (StringUtils.isNotEmpty(currentConf.getSeq()) && !StringUtils.equalsIgnoreCase(seqFile, currentConf.getSeq())) {
					errorMsg += CommonConstants.RECORD_TYPE_ID_HPL_GTMS + " FET2, FET3 File >> Sequence not matched"
							+ BREAKLINE;
				}
			}
			
			// GTMS
			// file >> Back end FET1 500421403104.csv -> lot 500421403104 -> model=5004, y=2021, m=4, d=0, prdLn=3, seq=104
			// format >> model=1,4|year=5,2|month=7,1|day=NA|prodLn=8,2|seq=10,3|lot=NA
			if(StringUtils.containsIgnoreCase(path, "fet#1")) {
				if (StringUtils.isNotEmpty(currentConf.getHModel())
						&& !StringUtils.equalsIgnoreCase(modelFile, currentConf.getHModel())) {
					errorMsg += CommonConstants.RECORD_TYPE_ID_HPL_GTMS + " FET1 File >> Model not matched" + BREAKLINE;
				}
				if (!StringUtils.equalsIgnoreCase(yearFile, currentConf.getYear())) {
					errorMsg += CommonConstants.RECORD_TYPE_ID_HPL_GTMS + " FET1 File >> Year not matched" + BREAKLINE;
				}
				if (StringUtils.isNotEmpty(currentConf.getMth())
						&& !StringUtils.equalsIgnoreCase(monthFile, currentConf.getMth())) {
					errorMsg += CommonConstants.RECORD_TYPE_ID_HPL_GTMS + " FET1 File >> Month not matched" + BREAKLINE;
				}
				if (!StringUtils.equalsIgnoreCase(prodLnFile, currentConf.getProdLn())) {
					errorMsg += CommonConstants.RECORD_TYPE_ID_HPL_GTMS + " FET1 File >> Prod Line not matched"
							+ BREAKLINE;
				}
				if (StringUtils.isNotEmpty(currentConf.getSeq()) && !StringUtils.equalsIgnoreCase(seqFile, currentConf.getSeq())) {
					errorMsg += CommonConstants.RECORD_TYPE_ID_HPL_GTMS + " FET1 File >> Sequence not matched"
							+ BREAKLINE;
				}
			}
			
		} else if(StringUtils.equals(pfDto.getHpl(), CommonConstants.RECORD_TYPE_ID_HPL_SQ)) {
			// TBC
		}
		return errorMsg;
	}

	/**
	 * Convert file to multipartfile
	 * 
	 * @param fileName
	 * @return
	 * @throws IOException
	 */
	private MultipartFile convertFileToMultipartFile(String fileName) throws IOException {
		logger.debug("convertFileToMultipartFile() fileName={}", fileName);
		File file = new File(fileName);
		FileItem fileItem = new DiskFileItem("mainFile", Files.probeContentType(file.toPath()), false, file.getName(),
				(int) file.length(), file.getParentFile());

		try {
			InputStream input = new FileInputStream(file);
			OutputStream os = fileItem.getOutputStream();
			IOUtils.copy(input, os);
			// Or faster..
			// IOUtils.copy(new FileInputStream(file), fileItem.getOutputStream());
		} catch (IOException ex) {
			// do something.
		}

		MultipartFile multipartFile = new CommonsMultipartFile(fileItem);
		return multipartFile;
	}

	private Map<String, Object> removeAlert(Map<String, Object> model) {
		model.remove("error");
		model.remove("success");
		model.remove("info");

		return model;
	}

	private void getAllLabels() {
		HPL_LBL = msgSource.getMessage("lblHpl", null, Locale.getDefault());
		MODEL_LBL = msgSource.getMessage("lblHplModel", null, Locale.getDefault());
		YEAR_LBL = msgSource.getMessage("lblYear", null, Locale.getDefault());
		MONTH_LBL = msgSource.getMessage("lblMonth", null, Locale.getDefault());
		DAY_LBL = msgSource.getMessage("lblDay", null, Locale.getDefault());
		PROD_LN_LBL = msgSource.getMessage("lblProdLn", null, Locale.getDefault());
		FILE_NAME_LBL = msgSource.getMessage("lblFileName", null, Locale.getDefault());
		FILE_PATH_LBL = msgSource.getMessage("lblFilePath", null, Locale.getDefault());
		G2_LOT_LBL = msgSource.getMessage("lblG2LotNo", null, Locale.getDefault());
		MODULE_NAME = msgSource.getMessage("menuProdFile", null, Locale.getDefault());
	}

	/**
	 * Get USB information
	 */
	private void getUSBInfo() {
		if (Objects.isNull(usbDet.getUsbDetMan()))
			usbDet.init();

		// det.getUsbDevices().forEach(System.out::println);
		if (usbDet.getUsbDevices().isEmpty()) {
			model.put("info", "No usb connected!");
		} else {
			String usbDetails = "USB connected. <br/>";
			for (USBStorageDevice usb : usbDet.getUsbDevices()) {
				usbDetails = usbDetails + "Drive :" + usb.getDevice() + ", Serial No :" + usb.getUuid() + ", USB Name :"
						+ usb.getDeviceName() + ".<br/>";

				Path p = Paths.get(usb.getDevice());
				if (Files.notExists(p))
					logger.debug("path not exists {}", p.toAbsolutePath());
				else
					logger.debug("path exists {} ", p.toAbsolutePath());
			}
			model.put("info", usbDetails);

		}
	}

	/**
	 * Generate MultipartFile from USB
	 * 
	 * @param fileContentName
	 * @param newFileContentName
	 * @param mode
	 * @param finalFileContent
	 * @return
	 * @throws IOException
	 */
	private MultipartFile setMultipartfileIfFromUSB(String fileContentName, String newFileContentName, String mode,
			MultipartFile finalFileContent) throws IOException {

		if (Objects.isNull(finalFileContent) || StringUtils.isEmpty(finalFileContent.getOriginalFilename())) {
			// get device/USB path
			String path = usbDet.getOneDeviceInfo().getDevice();
			logger.debug(
					"setMultipartfileIfFromUSB() finalFileContent null, fileContentName={}, newFileContentName={}, mode={}, path={}",
					fileContentName, newFileContentName, mode, path);

			if (StringUtils.equals(mode, CommonConstants.SCREEN_MODE_ADD)) {
				fileContentName = path + fileContentName;
				finalFileContent = convertFileToMultipartFile(fileContentName);

			} else if (StringUtils.equals(mode, CommonConstants.SCREEN_MODE_EDIT)) {
				// in edit mode, user might not want to update the file object
				if (StringUtils.isNotEmpty(newFileContentName)) {
					newFileContentName = path + newFileContentName;
					finalFileContent = convertFileToMultipartFile(newFileContentName);
				}

			}
		}
		return finalFileContent;
	}

	private void generateG2LotItems(ProdFileDto dto, Map<String, Object> model) {
		String lot = "";
		String hpl = "";
		String hplModel = "";
		String year = "";
		String mth = "";
		String day = "";
		String prodLn = "";
		String seq = "";

		if (Objects.nonNull(dto)) {
			hpl = dto.getHpl();
			hplModel = dto.getHModel();
			year = dto.getYear();
			mth = dto.getMth();
			prodLn = dto.getProdLn();

		} else {
			// default value
			// hpl = "IF";

			LocalDate crrDate = LocalDate.now();
			year = String.valueOf(crrDate.getYear());
			mth = String.valueOf(crrDate.getMonthValue());
		}

		model.put("g2LotItems", g2LotServ.searchByCriteria(lot, hpl, hplModel, year, mth, day, prodLn, seq));
	}

	private List<RelPathDto> generatePathsItems(String hplId, String year, String prodLn2, String mth) {
		List<FoldCatgConfDto> foldDtoList = foldCatConfServ.searchByCriteria(hplId, "", year, mth, "", prodLn2, "", "");
		List<RelPathDto> paths = new ArrayList<RelPathDto>();
		for (FoldCatgConfDto foldDto : foldDtoList) {
			// paths = foldCatConfServ.findRelPathListByParent(foldDto.getPkCatgId());
			for (RelPathDto relPathDto : foldCatConfServ.findRelPathListByParent(foldDto.getPkCatgId())) {
				logger.debug("path {}", relPathDto.getFilePath());
				paths.add(relPathDto);
			}
		}
		return paths;
	}

	private List<String> generateProdLnItems(List<G2LotViewDto> itemsPrd) {
		List<String> prodLnList = itemsPrd.stream().map(G2LotViewDto::getProdLn).distinct()
				.collect(Collectors.toList());
		return prodLnList;
	}

	private List<G2LotViewDto> generateG2LotItems(String year, String mth, String prodLn2, String hpl,
			String hplModel) {
		List<G2LotViewDto> items = new ArrayList<G2LotViewDto>();
		items = g2LotServ.searchByCriteria("", hpl, hplModel, year, mth, "", prodLn2, "");
		return items;
	}
	
	private boolean fileExists(int parseInt) {
		List<ProdFileSearch> items = (List<ProdFileSearch>) model.get("prodFileAllItems");
		items = items.stream().filter(arg0 -> arg0.getPkPfileId() == parseInt).collect(Collectors.toList());

//		if (!items.isEmpty()) {
//			ProdFileSearch obj = items.get(0);
//			String path = relPathServ.findDtoById(Integer.valueOf(obj.getFilePath())).getFilePath();
//			String fileName = obj.getFileName();
//			String fileURL = path + File.separator + fileName;
//
//			return Files.exists(Paths.get(fileURL));
//		}
		return false;
	}

}
