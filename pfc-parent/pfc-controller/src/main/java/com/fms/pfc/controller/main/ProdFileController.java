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
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.StringTokenizer;
import java.util.regex.Pattern;
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
import org.springframework.beans.factory.annotation.Value;
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
import com.fms.pfc.domain.dto.main.FoldCatgConfDto2;
import com.fms.pfc.domain.dto.main.G2LotViewDto;
import com.fms.pfc.domain.dto.main.ProdFileDto;
import com.fms.pfc.domain.dto.main.RelPathDto2;
import com.fms.pfc.domain.dto.main.UsbConfDto;
import com.fms.pfc.domain.model.Usr;
import com.fms.pfc.domain.model.main.ProdFileSearch;
import com.fms.pfc.service.api.base.MenuRoleFunctionService;
import com.fms.pfc.service.api.base.TrxHisService;
import com.fms.pfc.service.api.main.FoldCatgConfService2;
import com.fms.pfc.service.api.main.G2LotViewService;
import com.fms.pfc.service.api.main.ProdFileService;
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
	private ServletContext servletContext;
	private MenuRoleFunctionService mrfServ;
	private FoldCatgConfService2 foldCatConfServ2;
	
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
	private static String SEQ_LBL = "";
	private static String MODULE_NAME = "";
	private static final int YEAR_COUNT = 20;
	private static final int MENU_ITEM_ID = 1201;
	private static final String BREAKLINE = ".<br />";
	
	@Value("${data.root.dir}")
	private String DEFAULT_DATA_PATH;
	
	@Value("${def.file.format.if}")
	private String DEF_FILE_FORMAT_IF;
	@Value("${def.file.format.mgg}")
	private String DEF_FILE_FORMAT_MGG;
	@Value("${def.file.format.mgg.1}")
	private String DEF_FILE_FORMAT_MGG_1;
	@Value("${def.file.format.gtms.mikron}")
	private String DEF_FILE_FORMAT_MIKRON;
	@Value("${def.file.format.gtms.backend.fet2.fet3}")
	private String DEF_FILE_FORMAT_BACKEND_FET2_FET3;
	@Value("${def.file.format.gtms.backend.fet1}")
	private String DEF_FILE_FORMAT_BACKEND_FET1;
	
	@Value("${filename.len.if}")
	private String FILENAME_LEN_IF;
	@Value("${filename.len.mgg}")
	private String FILENAME_LEN_MGG;
	@Value("${filename.len.gtms.mikron}")
	private String FILENAME_LEN_GTMS_M;
	@Value("${filename.len.gtms.be.fet1}")
	private String FILENAME_LEN_GTMS_FET1;
	@Value("${filename.len.gtms.be.fet2.fet3}")
	private String FILENAME_LEN_GTMS_FET2_FET3;

	@Autowired
	public ProdFileController(Authority auth, CommonValidation commonValServ, MessageSource msgSource,
			ProdFileService prodFileServ, TrxHisService trxHistServ, G2LotViewService g2LotServ, UsbDetectorUtil usbDet, ServletContext servletContext,
			MenuRoleFunctionService mrfServ, FoldCatgConfService2 foldCatConfServ2) {
		super();
		this.auth = auth;
		this.commonValServ = commonValServ;
		this.msgSource = msgSource;
		this.prodFileServ = prodFileServ;
		this.trxHistServ = trxHistServ;
		this.g2LotServ = g2LotServ;
		this.usbDet = usbDet;
		this.servletContext = servletContext;
		this.mrfServ = mrfServ;
		this.foldCatConfServ2 = foldCatConfServ2;

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
		model.put("btnResetForm", true);
		

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
				//model.put("prodFileAllItems", prodFileServ.searchByCriteria(grp, "", "", "", "", "", "", ""));
				model.put("prodFileAllItems", new ArrayList<>());
				model.put("searchHplItems",
						defaultHpl.stream().filter(arg0 -> arg0.equals(grp)).collect(Collectors.toList()));
				model.put("searchHplModelItems", g2LotServ.hplModelList(grp));

			} else {
				// if user is super user, remove filter
				//model.put("prodFileAllItems", prodFileServ.searchByCriteria("", "", "", "", "", "", "", ""));
				model.put("prodFileAllItems", new ArrayList<>());
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
				CommonConstants.FUNCTION_TYPE_VIEW, dto.getHpl() +"-"+ dto.getG2Lot()+"-"+ dto.getFileName(),
				CommonConstants.RECORD_TYPE_ID_PROD_FILE, null);

		// Set mode
		screenMode = CommonConstants.SCREEN_MODE_VIEW;
		model.put("mode", screenMode);
		model.put("btnSaveSts", true);
		model.put("btnEdit", false);
		model.put("editDiv", false);
		model.put("btnResetForm", true);

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
			model.put("seq2Temp", dto.getSeq());

			//model.put("hplItems", g2LotServ.hplList());
			filter(false);
			model.put("hplModelItems", g2LotServ.hplModelList(dto.getHpl()));
			model.put("yearItems", g2LotServ.yearList(dto.getHpl(), ""));
			model.put("monthItems", CommonUtil.monthDropdownItems());
			model.put("dayItems", CommonUtil.dayDropdownItems());
			model.put("seqItems", g2LotServ.seqList("", "", "", "", "", ""));
			generateG2LotItems(dto, model);
			model.put("filePathItems", generatePathsItems2(dto.getHpl(), dto.getYear(), dto.getProdLn(), dto.getMth(), "", 0, ""));
			model.put("prodLnItems",
					g2LotServ.prodLnList(dto.getHpl(), dto.getHModel(), dto.getYear(), dto.getMth(), ""));
			model.put("procTypeItems", CommonUtil.hplProcTypeDropdownItems());
			model.put("subProcItems", CommonUtil.gtmsSubFoldDropdownItems(0));

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
		removeTempVal(model);

		// Set mode
		screenMode = CommonConstants.SCREEN_MODE_ADD;
		model.put("mode", screenMode);

		// Set fields
		model.put("prodFileItem", new ProdFileDto());
		
		filter(false);
		
		String searchHplId = "";
		if (StringUtils.isNotEmpty((String) model.get("filterHpl"))) {
			searchHplId = (String) model.get("filterHpl");			
		}
		model.put("hpl", searchHplId);
		model.put("yearItems", g2LotServ.yearList("", ""));
		model.put("monthItems", CommonUtil.monthDropdownItems());
		model.put("dayItems", CommonUtil.dayDropdownItems());
		model.put("seqItems", g2LotServ.seqList("", "", "", "", "", ""));
		model.put("procTypeItems", CommonUtil.hplProcTypeDropdownItems());
		model.put("subProcItems", CommonUtil.gtmsSubFoldDropdownItems(0));
		model.put("defaultDataPath", DEFAULT_DATA_PATH);

		// Set form header
		model.put("header", "Add " + MODULE_NAME);
		// Set confirmation message
		model.put("confirmHeader", "Cancel confirmation");
		model.put("confirmMsg", "Do you want to cancel this record ?");

		model.put("editDiv", true);
		model.put("btnResetForm", false);

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
			@RequestParam(name = "prodLn2", required = false) String prodLn2,
			@RequestParam(name = "seq2", required = false) String seq2, 
			@RequestParam(name = "procType", required = false) Integer procType,
			@RequestParam(name = "subProc", required = false) String subProc,
			HttpServletRequest request,
			BindingResult bindingResult, HttpSession session) throws Exception {

		removeAlert(model);
		String mode = screenMode;
		MultipartFile finalFileContent = null;
		boolean editFileUpdate = false;

		dto.setHModel(hplModelId2);
		dto.setG2Lot(g2Lot2);
		dto.setFilePath(filePath2);
		dto.setProdLn(prodLn2);
		dto.setSeq(seq2);
		
		//hold value
		model.put("hplModelId2", hplModelId2);
		model.put("g2Lot2", g2Lot2);
		model.put("filePath2", filePath2);
		model.put("prodLn2", prodLn2);
		model.put("newFileContentName", newFileContentName);
		model.put("seq2", seq2);
		model.put("procType", procType);
		model.put("subProc", subProc);

		model.put("prodFileItem", dto);

		if (StringUtils.equals(mode, CommonConstants.SCREEN_MODE_ADD)) {
			finalFileContent = addModeFileContent;
			if (Objects.isNull(finalFileContent)) {
				finalFileContent = (MultipartFile) model.get("fileContentTemp");
				logger.debug("saveForm() add mode, editFileUpdate={}", editFileUpdate);
			}

		} else if (StringUtils.equals(mode, CommonConstants.SCREEN_MODE_EDIT)) {
			if (Objects.nonNull(editModeFileContent) && editModeFileContent.getSize() > 0) {
				finalFileContent = editModeFileContent;
				editFileUpdate = true;
				logger.debug("saveForm() edit mode, editFileUpdate={}", editFileUpdate);
			}
		}

		// If finalFileContent still null, suspect come from usb drive
		finalFileContent = setMultipartfileIfFromUSB(fileContentName, newFileContentName, mode, finalFileContent);

		String errorMsg = validateForm(dto, finalFileContent, mode, request.getRemoteUser(), procType, subProc);

		if (errorMsg.length() != 0) {
			model.put("error", errorMsg);
			model.put("prodFileItem", dto);
			model.put("hplModelId2Temp", hplModelId2);
			model.put("g2Lot2Temp", g2Lot2);
			model.put("filePath2Temp", filePath2);
			model.put("prodLn2Temp", prodLn2);
			model.put("seq2Temp", seq2);

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
			// dto.setFileType(finalFileContent.getContentType());
			dto.setFileType(FilenameUtils.getExtension(finalFileContent.getOriginalFilename()));
			dto.setFileSize(finalFileContent.getSize());
			dto.setContentObject(finalFileContent.getBytes());
		}

		// ----

		if (mode.equals(CommonConstants.SCREEN_MODE_ADD)) {

			try {
				// If no error proceed to save add form
				// Add form
				Integer result = 0;
				if(((boolean)model.get("autoCreateIndicator"))) {
					result = prodFileServ.save2(dto, request.getRemoteUser(), editFileUpdate, finalFileContent,
							 (String) model.get("fileFormatTemp"), procType, subProc);
				} else {
					result = prodFileServ.save(dto, request.getRemoteUser(), editFileUpdate, finalFileContent);	
				}

				// Print add success
				model.put("success", "Record added successfully.");
				model.remove("error");
				model.put("prodFileItem", new ProdFileDto());

				trxHistServ.addTrxHistory(new Date(), "Insert " + MODULE_NAME, request.getRemoteUser(),
						CommonConstants.FUNCTION_TYPE_INSERT, (null != result && result != 0) ? dto.getHpl() +"-"+ dto.getG2Lot()+"-"+ dto.getFileName() : "",
						CommonConstants.RECORD_TYPE_ID_PROD_FILE, null);

			} catch (Exception e) {
				// Print DB exception
				e.printStackTrace();
				model.put("error", "Opss.Something happened during record saving. Please check in the listing if the file exists.");
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
						CommonConstants.FUNCTION_TYPE_UPDATE, (null != result && result != 0) ? dto.getHpl() +"-"+ dto.getG2Lot()+"-"+ dto.getFileName() : "",
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
		if (StringUtils.isNotEmpty((String) model.get("filterHpl"))) {
			searchHplId = (String) model.get("filterHpl");			
		}
		model.put("prodFileAllItems", prodFileServ.searchByCriteria(searchHplId, "", "", "", dto.getG2Lot(), "3", "", "", "", ""));

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
		model.put("btnResetForm", true);
		model.put("header", "Edit " + MODULE_NAME);
		model.remove("success");
		model.remove("error");
		model.put("defaultDataPath", DEFAULT_DATA_PATH);

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

				if (checkDelete()) {
					// Print delete failed
					model.put("error", "Record is in used and it is not allowed to be deleted from the system.");
				} else {
					ProdFileDto dto = prodFileServ.findDtoById(Integer.parseInt(check[i]));
					prodFileServ.delete(Integer.parseInt(check[i]));
					prodFileServ.deleteFileFromDisk(dto);
					// Print delete success`
					model.put("success",
							msgSource.getMessage("msgSuccessDelete", new Object[] {}, Locale.getDefault()));

					trxHistServ.addTrxHistory(new Date(), "Delete " + MODULE_NAME, request.getRemoteUser(),
							CommonConstants.FUNCTION_TYPE_DELETE, dto.getHpl() +"-"+ dto.getG2Lot()+"-"+ dto.getFileName(), CommonConstants.RECORD_TYPE_ID_PROD_FILE,
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
		
		model.put("prodFileAllItems", prodFileServ.searchByCriteria(searchHplId, "", "", "", "", "", "", "", "", ""));

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
			@RequestParam(name = "searchPathExp") String searchPathExp, 
			@RequestParam(name = "searchFN") String searchFN,
			@RequestParam(name = "searchFnExp") String searchFnExp,
			HttpSession session) {

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
		model.put("searchFN", searchFN);
		model.put("searchFnExp", searchFnExp);
		
		model.put("tempHplModel", searchHplModelId);
		model.put("tempYear", searchYear);
		model.put("tempMth", searchMth);

		try {
			if (errorMsg.length() == 0) {

				List<ProdFileSearch> items = prodFileServ.searchByCriteria(searchHplId, searchHplModelId, searchYear,
						searchMth, searchG2Lot, searchG2LotExp, searchPath, searchPathExp, searchFN, searchFnExp);
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
				model.put("prodFileAllItems", prodFileServ.searchByCriteria(searchHplId, searchHplModelId, searchYear,
						searchMth, searchG2Lot, searchG2LotExp, searchPath, searchPathExp, searchFN, searchFnExp));
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
			@RequestParam(name = "day", required = false) String day,
			@RequestParam(name = "prodLn2", required = false) String prodLn2, 
			@RequestParam(name = "seq2", required = false) String seq2, HttpServletRequest request,
			HttpSession session) {

		Map<String, Object> respObjMap = new HashMap<String, Object>();

		// model.put("prodLn2Temp", prodLn2);

		// G2 lot dropdown items
		List<G2LotViewDto> items = generateG2LotItems(year, mth, day, prodLn2, hplId, hplModelId2, seq2);
		respObjMap.put("items", items);

		// Prod ln dropdown items
		List<G2LotViewDto> itemsPrd = items;
		List<String> prodLnList = generateProdLnItems(itemsPrd);
		respObjMap.put("prodLnItems", prodLnList);		

		List<String> seqList = generateSeqItems(itemsPrd);
		respObjMap.put("seqItems", seqList);

		// Path dropdown items
		String subProc = (String) model.get("subProcTemp");
		Integer procType = (Integer) model.get("procTypeTemp");
		List<RelPathDto2> paths = generatePathsItems2(hplId, year, prodLn2, mth, seq2, procType, subProc);
		respObjMap.put("pathsItems", paths);
		model.put("pathsItemsSize", paths.size());

		logger.debug(
				"loadCompletedG2Lot() hpl={}, hplModelId2={}, year={}, mth={}, day={}, prodLn2={}, seq2={}, lot size={}, path size={}",
				hplId, hplModelId2, year, mth, day, prodLn2, seq2, items.size(), paths.size());
		return respObjMap;
	}
	
	@GetMapping(value = "/main/pfc/loadCompletedG2LotOnly")
	@ResponseBody
	public Map<String, Object> loadCompletedG2LotOnly(ProdFileDto dto,
			@RequestParam(name = "hplId", required = false) String hplId,
			@RequestParam(name = "hplModelId2", required = false) String hplModelId2,
			@RequestParam(name = "year", required = false) String year,
			@RequestParam(name = "mth", required = false) String mth,
			@RequestParam(name = "day", required = false) String day,
			@RequestParam(name = "prodLn2", required = false) String prodLn2, 
			@RequestParam(name = "seq2", required = false) String seq2, HttpServletRequest request,
			HttpSession session) {
		Map<String, Object> respObjMap = new HashMap<String, Object>();
		// G2 lot dropdown items
		List<G2LotViewDto> items = generateG2LotItems(year, mth, day, prodLn2, hplId, hplModelId2, seq2);
		respObjMap.put("items", items);
		logger.debug(
				"loadCompletedG2LotOnly() hpl={}, hplModelId2={}, year={}, mth={}, prodLn2={}, seq2={}, lot size={}",
				hplId, hplModelId2, year, mth, prodLn2, seq2, items.size());
		return respObjMap;
	}
	
	@GetMapping(value = "/main/pfc/loadFilePathOnly")
	@ResponseBody
	public Map<String, Object> loadFilePathOnly(ProdFileDto dto,
			@RequestParam(name = "hplId", required = false) String hplId,
			@RequestParam(name = "year", required = false) String year,
			@RequestParam(name = "mth", required = false) String mth,
			@RequestParam(name = "prodLn2", required = false) String prodLn2, 
			@RequestParam(name = "seq2", required = false) String seq2, 
			@RequestParam(name = "procType", required = false) Integer procType, 
			@RequestParam(name = "subProc", required = false) String subProc, 
			HttpServletRequest request,
			HttpSession session) {
		
		//String subProc = (String) model.get("subProcTemp");
		//Integer procType = (Integer) model.get("procTypeTemp");
		
		Map<String, Object> respObjMap = new HashMap<String, Object>();
		// Path dropdown items
		List<RelPathDto2> paths = generatePathsItems2(hplId, year, prodLn2, mth, seq2, procType, subProc);
		respObjMap.put("pathsItems", paths);
		model.put("pathsItemsSize", paths.size());
		logger.debug("loadFilePathOnly() hpl={}, year={}, mth={}, prodLn2={}, seq2={}, procType={}, subProc={}, paths size={}", hplId, year, mth,
				prodLn2, seq2, procType, subProc, paths.size());
		return respObjMap;
	}
	
	@GetMapping("/main/pfc/prodFileDownload")
	public ResponseEntity<byte[]> prodFileDownload(HttpServletRequest request,
			@RequestParam(name = "pkPfileId") int pkPfileId) {
		String fileName = "";
		byte[] data = new byte[1024];

		ProdFileDto pfDto = prodFileServ.findDtoById(pkPfileId);
		//RelPathDto relPath = relPathServ.findDtoById(Integer.valueOf(pfDto.getFilePath()));
		RelPathDto2 relPath = foldCatConfServ2.findRelPathById(Integer.valueOf(pfDto.getFilePath()));
		Path p = Paths.get(relPath.getFilePath()+ File.separator + pfDto.getFileName());
		//fileName = pfDto.getFileName();
		fileName = p.getFileName().toString();
		try {
			data = Files.readAllBytes(p);
//		data = pfDto.getContentObject();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			data = pfDto.getContentObject();
		}
		
		trxHistServ.addTrxHistory(new Date(), "Download " + MODULE_NAME, request.getRemoteUser(),
				CommonConstants.FUNCTION_TYPE_DOWNLOAD, pfDto.getHpl() +"-"+ pfDto.getG2Lot()+"-"+ pfDto.getFileName(),
				CommonConstants.RECORD_TYPE_ID_PROD_FILE, null);

		MediaType mt = MediaTypeUtils.getMediaTypeForFileName(servletContext, fileName);

		return ResponseEntity.ok().contentType(mt)
				.header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + fileName + "\"").body(data);
	}
	
	@GetMapping(value = "/main/pfc/loadInfoByFile")
	@ResponseBody
	public Map<String, Object> loadInfoByFile(@RequestParam(name = "hpl", required = false) String hplId,
			@RequestParam(name = "file", required = false) String fileName, HttpServletRequest request,
			HttpSession session) {
		
		logger.debug("loadInfoByFile() hpl={}, file={}", hplId, fileName);
		
		// remove temp values used in this function
		model.remove("procTypeTemp");
		model.remove("subProcTemp");
		model.remove("fileFormatTemp");
		model.remove("prodLn2Temp");
		model.remove("seq2Temp");
		model.remove("g2Lot2Temp");
		model.remove("filePath2Temp");
		//
		
		Map<String, Object> respObjMap = new HashMap<String, Object>();
		List<FoldCatgConfDto2> confList = foldCatConfServ2.searchByCriteria(hplId, "");
		respObjMap = extractFileInfo(hplId, fileName, confList.get(0));
		
		// map back file path (RelPathDto2) based on hpl, year, month, prodln
		List<RelPathDto2> relPathList = foldCatConfServ2.searchRelPathByCriteria(confList.get(0).getPkCatgId(), "",
				(String) respObjMap.get(CommonConstants.HPL_LOT_KEY_YEAR),
				!hplId.equals(CommonConstants.RECORD_TYPE_ID_HPL_GTMS)
						? (String) respObjMap.get(CommonConstants.HPL_LOT_KEY_MONTH)
						: "",
				"", (String) respObjMap.get(CommonConstants.HPL_LOT_KEY_PRODLN), "", null, "");
		model.put("filePath2Temp", relPathList.isEmpty() ? 0 : relPathList.get(0).getPkRelPathId());		

		return respObjMap;
	}	
	
	@GetMapping(value = "/main/pfc/loadInfoByLotNo")
	@ResponseBody
	public Map<String, Object> loadInfoByLotNo(@RequestParam(name = "hpl", required = false) String hpl,
			@RequestParam(name = "lot", required = false) String lot, HttpServletRequest request,
			HttpSession session) {
		
		logger.debug("loadInfoByFile() hpl={}, lot={}", hpl, lot);
		
		Map<String, Object> respObjMap = new HashMap<String, Object>();
		List<G2LotViewDto> dto = g2LotServ.searchByCriteria(lot, hpl, "", "", "", "", "", "");
		
		if(!dto.isEmpty()) {
			G2LotViewDto obj = dto.get(0);
			respObjMap.put("model", obj.getModel());
			respObjMap.put("year", obj.getYear());
			respObjMap.put("mth", obj.getMth());
			respObjMap.put("day", obj.getDay());
			respObjMap.put("prodLn", obj.getProdLn());
			respObjMap.put("seq", obj.getSeq());
		}

		return respObjMap;
	}

	@PreDestroy
	public void destroy() throws IOException {
		if (Objects.nonNull(usbDet.getUsbDetMan())) {
			usbDet.destroy();
		}
	}
	
	/**
	 * Extract information from file namme
	 * @param hplId
	 * @param fileName
	 * @return Map<String, Object>
	 */	
	private Map<String, Object> extractFileInfo(String hplId, String fileName, FoldCatgConfDto2 foldConf) {		
		
		Map<String, Object> respObjMap = new HashMap<String, Object>();
		String originalFileName = StringUtils.substring(fileName, 0, fileName.lastIndexOf("."));
		String fileFormat = foldConf.getProdFileFormat();//"model=8,2|year=1,2|month=3,2|day=5,2|prodLn=14,1|seq=NA|lot=8,7";
		int procType = 0;
		String year = "";
		String subProc = "";
		if (StringUtils.isEmpty(fileFormat)) {
			if (StringUtils.equals(foldConf.getHpl(), CommonConstants.RECORD_TYPE_ID_HPL_GTMS)) {
				// expecting file format from child table - rel_path2
				// currently for gtms
				int fileNameLen = originalFileName.length();
				if (fileNameLen == Integer.parseInt(FILENAME_LEN_GTMS_M) 
						&& Pattern.compile("^[A-Z]").matcher(originalFileName).find()) {
					// mikron all folder
					procType = CommonConstants.PROCESS_TYPE_HPL_MIKRON;
					subProc = CommonConstants.PROCESS_SUBTYPE_GTMS_M_CELL1; // or 2 or 3
					year = StringUtils.substring(originalFileName, 3, 5);
					year = year.length() == 2 ? "20" + year : year;
				} else if (fileNameLen == Integer.parseInt(FILENAME_LEN_GTMS_FET2_FET3)
						&& Pattern.compile("^[0-9_]").matcher(originalFileName).find()) {
					// back-end fet2,fet3
					// db file format len = 67
					procType = CommonConstants.PROCESS_TYPE_HPL_BACKEND;
					subProc = CommonConstants.PROCESS_SUBTYPE_GTMS_B_FET2; // or 3
					year = StringUtils.substring(originalFileName, 9, 11);
					year = year.length() == 2 ? "20" + year : year;
				} else if (fileNameLen == Integer.parseInt(FILENAME_LEN_GTMS_FET1)
						&& !StringUtils.startsWith(originalFileName,"KMY")) {
					// back-end fet1
					// db file format len = 64
					procType = CommonConstants.PROCESS_TYPE_HPL_BACKEND;
					subProc = CommonConstants.PROCESS_SUBTYPE_GTMS_B_FET1;
					year = StringUtils.substring(originalFileName, 4, 6);
					year = year.length() == 2 ? "20" + year : year;
				}

				List<RelPathDto2> relPathList = foldCatConfServ2.searchRelPathByCriteria(foldConf.getPkCatgId(), "",
						year, "", "", "", "", procType, "");
				
				if(!relPathList.isEmpty()) {
					if (procType == CommonConstants.PROCESS_TYPE_HPL_MIKRON) {
						// all folders same format
						fileFormat = relPathList.get(0).getProdFileFormat();
					} else if (procType == CommonConstants.PROCESS_TYPE_HPL_BACKEND) {
						// folder fet2,fet3
						if (fileNameLen == Integer.parseInt(FILENAME_LEN_GTMS_FET2_FET3)) {
							relPathList = relPathList.stream()
									.filter(arg0 -> arg0.getProdFileFormat().contains("lot=6,10") 
											&& (arg0.getSubProc().equals(CommonConstants.PROCESS_SUBTYPE_GTMS_B_FET2)
											|| arg0.getSubProc().equals(CommonConstants.PROCESS_SUBTYPE_GTMS_B_FET3)))
									.collect(Collectors.toList());
							if(!relPathList.isEmpty()) {
								fileFormat = relPathList.get(0).getProdFileFormat();
								
							} else {
								// if no configuration done yet
								fileFormat = getSysDefaultFileformat(foldConf.getHpl(), procType, subProc);
							}
							model.put("fileFormatTemp", fileFormat);
						} else {
							// folder fet1
							relPathList = relPathList.stream()
									.filter(arg0 -> arg0.getProdFileFormat().contains("lot=1,10")
											&& arg0.getSubProc().equals(CommonConstants.PROCESS_SUBTYPE_GTMS_B_FET1))
									.collect(Collectors.toList());
							if(!relPathList.isEmpty()) {
								fileFormat = relPathList.get(0).getProdFileFormat();
								
							} else {
								// if no configuration done yet
								fileFormat = getSysDefaultFileformat(foldConf.getHpl(), procType, subProc);
							}
							model.put("fileFormatTemp", fileFormat);
						}
					}
				} else {
					// if no configuration done yet
					fileFormat = getSysDefaultFileformat(foldConf.getHpl(), procType, subProc);
					model.put("fileFormatTemp", fileFormat);
				}				
				
				model.put("procTypeTemp", procType);
				model.put("subProcTemp", subProc);
				
				respObjMap.put("procTypeTemp", procType);
				respObjMap.put("subProcTemp", subProc);
				
				logger.debug("extractFileInfo() procType={}, subProc={}",procType,subProc);

			} else {
				logger.info("extractFileInfo() File format empty but not from GTMS!");
			}
		}
		
		// if fileformat still empty, get system default
		if(StringUtils.isEmpty(fileFormat))
			fileFormat = getSysDefaultFileformat(foldConf.getHpl(), procType, subProc);
		
		//20231212 - MGG got diff format for 4digit model
		{
			if(hplId.equals(CommonConstants.RECORD_TYPE_ID_HPL_MGG)) {
				//check first 4 digits are number
				String mModel = StringUtils.substring(originalFileName, 0, originalFileName.indexOf("_"));
				if(mModel.length() == 4) {
					fileFormat = DEF_FILE_FORMAT_MGG_1;
				} else if(mModel.length() == 3) {
					fileFormat = DEF_FILE_FORMAT_MGG;
				}
			}
		}
		
		model.put("fileFormatTemp", fileFormat);
		
		logger.debug("extractFileInfo() fileFormat={}",fileFormat);
		
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
				respObjMap.put(CommonConstants.HPL_LOT_KEY_MODEL, result);
				logger.debug("post={}, len={}, model={}", post, len, result);
			} else if (key.equalsIgnoreCase(CommonConstants.HPL_LOT_KEY_YEAR)) {
				if (StringUtils.isNotEmpty(result) && result.length() == 2) {
					result = "20" + result;
				}
				respObjMap.put(CommonConstants.HPL_LOT_KEY_YEAR, result);
				logger.debug("post={}, len={}, year={}", post, len, result);
			} else if (key.equalsIgnoreCase(CommonConstants.HPL_LOT_KEY_MONTH)) {
				/*if (StringUtils.isNotEmpty(result) && result.length() == 1) {
					result = "0" + result;
				}*/
				result = CommonUtil.monthConversionFromFileName(result, "");
				respObjMap.put(CommonConstants.HPL_LOT_KEY_MONTH, result);
				logger.debug("post={}, len={}, month={}", post, len, result);
			} else if (key.equalsIgnoreCase(CommonConstants.HPL_LOT_KEY_DAY)) {
				if (StringUtils.isNotEmpty(result) && result.length() == 1) {
					result = "0" + result;
				}
				if(StringUtils.equals(hplId, CommonConstants.RECORD_TYPE_ID_HPL_IF)) {
					result = CommonUtil.dayConversionFromFileName(result, CommonConstants.RECORD_TYPE_ID_HPL_IF);
				}
				respObjMap.put(CommonConstants.HPL_LOT_KEY_DAY, result);
				logger.debug("post={}, len={}, day={}", post, len, result);
			} else if (key.equalsIgnoreCase(CommonConstants.HPL_LOT_KEY_PRODLN)) {				
				if(StringUtils.equals(hplId, CommonConstants.RECORD_TYPE_ID_HPL_IF)) {
					result = CommonUtil.prodLnConversionFromFileName(result, CommonConstants.RECORD_TYPE_ID_HPL_IF);
				}				
				model.put("prodLn2Temp", result);//for drop-down
				respObjMap.put(CommonConstants.HPL_LOT_KEY_PRODLN, result);
				logger.debug("post={}, len={}, prodLn={}", post, len, result);
			} else if (key.equalsIgnoreCase(CommonConstants.HPL_LOT_KEY_SEQ)) {
				model.put("seq2Temp", result);//for drop-down
				respObjMap.put(CommonConstants.HPL_LOT_KEY_SEQ, result);
				logger.debug("post={}, len={}, seq={}", post, len, result);
			} else if (key.equalsIgnoreCase(CommonConstants.HPL_LOT_KEY_LOT)) {
				model.put("g2Lot2Temp", result);//for drop-down
				respObjMap.put(CommonConstants.HPL_LOT_KEY_LOT, result);
				logger.debug("post={}, len={}, lot={}", post, len, result);
			}
		}
		return respObjMap;
	}

	private String getSysDefaultFileformat(String hpl, int procType, String subProc) {
		logger.debug("getSysDefaultFileformat() hpl={}, proc={}, sub={}", hpl, procType, subProc);
		String result = "";
		if (hpl.equals(CommonConstants.RECORD_TYPE_ID_HPL_IF)) {
			result = DEF_FILE_FORMAT_IF;
		} else if (hpl.equals(CommonConstants.RECORD_TYPE_ID_HPL_MGG)) {
			result = DEF_FILE_FORMAT_MGG;
		} else if (hpl.equals(CommonConstants.RECORD_TYPE_ID_HPL_GTMS)) {
			if (procType == CommonConstants.PROCESS_TYPE_HPL_MIKRON) {
				if (subProc.equals(CommonConstants.PROCESS_SUBTYPE_GTMS_M_CELL1)
						|| subProc.equals(CommonConstants.PROCESS_SUBTYPE_GTMS_M_CELL2_1)
						|| subProc.equals(CommonConstants.PROCESS_SUBTYPE_GTMS_M_CELL2_2)
						|| subProc.equals(CommonConstants.PROCESS_SUBTYPE_GTMS_M_CELL3)) {
					result = DEF_FILE_FORMAT_MIKRON;
				} 
			} else if (procType == CommonConstants.PROCESS_TYPE_HPL_BACKEND) {
				if (subProc.equals(CommonConstants.PROCESS_SUBTYPE_GTMS_B_FET1)) {
					result = DEF_FILE_FORMAT_BACKEND_FET1;
				} else if (subProc.equals(CommonConstants.PROCESS_SUBTYPE_GTMS_B_FET2)
						|| subProc.equals(CommonConstants.PROCESS_SUBTYPE_GTMS_B_FET3)) {
					result = DEF_FILE_FORMAT_BACKEND_FET2_FET3;
				}
			}
		}
		return result;
	}

	/**
	 * Construct error message based on validation result
	 * 
	 * @param dto
	 * @return
	 */
	private String validateForm(ProdFileDto dto, MultipartFile fileContent, String mode, String userId,
			Integer procType, String subProc) {
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
		errorMsg += commonValServ.validateMandatoryInput(dto.getYear(), YEAR_LBL);
		errorMsg += commonValServ.validateMandatoryInput(dto.getG2Lot(), G2_LOT_LBL);
		
		if (StringUtils.isNotEmpty(dto.getHpl())
				&& StringUtils.equals(dto.getHpl(), CommonConstants.RECORD_TYPE_ID_HPL_GTMS)) {
			errorMsg += commonValServ.validateMandatoryInput(dto.getSeq(), SEQ_LBL);
		}
		
		if ((Integer) model.get("pathsItemsSize") != 0) {
			errorMsg += commonValServ.validateMandatoryInput(dto.getFilePath(), FILE_PATH_LBL);
			model.put("autoCreateIndicator", false);
		} else {
			// if no path found from FoldCatConf, notify user through info
			String info = commonValServ.validateMandatoryInput(dto.getFilePath(), FILE_PATH_LBL);
			if (!info.isEmpty()) {
				info += FILE_PATH_LBL + " will automatically be created" + BREAKLINE;
				if (StringUtils.isEmpty(errorMsg)) {
					// only display if no error message
					model.put("info", info);
				}
				model.put("autoCreateIndicator", true);
			} else {
				model.put("autoCreateIndicator", false);
			}
		}
		

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
			
			// 0 
			// validate duplicate file against DB
			// IF, MGG -> lot no + filename
			// GTMS -> file name can be duplicate across multiple folders
			{
				if (screenMode == CommonConstants.SCREEN_MODE_ADD) {
					errorMsg += validateDuplicateFile(fileName, dto, procType, subProc);
				} else {
					if (Objects.nonNull(model.get("prodFileItemCurrItem"))) {
						ProdFileDto fromDB = (ProdFileDto) model.get("prodFileItemCurrItem");

						logger.debug("validateForm fromDB name=" + fromDB.getFileName() + "; new name=" + fileName);
						if (!StringUtils.equalsIgnoreCase(fromDB.getFileName(), fileName)) {
							// check if current name value modified
							errorMsg += validateDuplicateFile(fileName, dto, procType, subProc);
						}
					}
				}
			}

			if (StringUtils.isEmpty(errorMsg)) {
				// 1
				errorMsg += validateUsbUsrConf(dto, userId);
				// 2
				errorMsg += validateFileTypeAndSize(dto, userId, fileExt, fileSize);
				// 3
				// errorMsg += validateFileNameAgainstG2Lot(dto, fileName);
			}
		}

		return errorMsg;
	}
	
	/**
	 * Validate - duplicate file checking
	 * @param fileName
	 * @param lotNo
	 * @param hpl
	 * @return errorMsg
	 */
	private String validateDuplicateFile(String fileName, ProdFileDto dto, Integer procType, String subProc) {
		logger.debug("validateDuplicateFile() ... fileName={}, lotNo={}, hpl={}", fileName, dto.getG2Lot(),
				dto.getHpl());
		String errorMsg = "";
		if (!StringUtils.equals(dto.getHpl(), CommonConstants.RECORD_TYPE_ID_HPL_GTMS)) {
			Integer count = prodFileServ.countDuplicateFile(fileName, dto.getG2Lot(), dto.getHpl());
			if (count > 0) {
				errorMsg = "File already exists in database - File name=" + fileName + "; Lot no="
						+ dto.getG2Lot() + "; HPL=" + dto.getHpl();
			}
		} else {
			logger.debug("validateDuplicateFile() ... procType={},subProc={}", procType, subProc);
			// for gtms, it can has the same file name in multiple folders
			// 1) mikron - cell 1, cell 2.1, cell 3 -  file name (KMY 210104504.txt) should repeat in cell 2.1 and cell 3
			if(procType == CommonConstants.PROCESS_TYPE_HPL_MIKRON) {
				
			} else if (procType == CommonConstants.PROCESS_TYPE_HPL_BACKEND) {				
			// 2) backend fet 2, fet 3 - file name (5004_500421403104.csv) if exists in fet 2, should repeat in fet 3, 
			// otherwise in fet 1
			// 3) backend fet 1 - file name (500421403104.csv) should either in fet 1 or in fet 2 and fet 3 

			}
		}
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
	private String validateFileTypeAndSize(ProdFileDto dto, String userId, String fileType,
			long fileSize) {
		String errorMsg = "";
		List<FileTypeSzDto> fstList = prodFileServ.findFileTypeSzList(dto.getHpl());
		logger.debug("validateFileTypeAndSize() fstList={}", fstList.size());
		
		if (!fstList.isEmpty()) {
			// TODO: need to loop as one hpl could have multiple settings
			int ftypeCnt = 0;
			int prdlnCnt = 0;
			for (FileTypeSzDto fstDto : fstList) {
				if (StringUtils.equalsIgnoreCase(fileType, fstDto.getFileType())) {
					ftypeCnt++;
					if(StringUtils.equalsIgnoreCase(dto.getProdLn(), fstDto.getProdLn())) {
						prdlnCnt++;
						// check min and max file size
						if (fileSize < fstDto.getMinSize()) {
							errorMsg += "File-type-size config >> Minimum File size must be minimum " + fstDto.getMinSize() + " KB. Current size="+fileSize+BREAKLINE;
						} else if (fileSize > fstDto.getMaxSize()) {
							model.put("info", "File-type-size config >> Take note that File size has exceeded the maximum file size configured.");
						}						
					}
				}
			}

			if (ftypeCnt == 0) {
				errorMsg += "File-type-size config >> File type/extension=" + fileType + " for the file has not been configured for HPL="
						+ dto.getHpl()+BREAKLINE;
			}
			
			if (prdlnCnt == 0) {
				errorMsg += "File-type-size config >> Prod line=" + dto.getProdLn() + " for the file has not been configured for HPL="
						+ dto.getHpl()+BREAKLINE;
			}

		} else {
			errorMsg += "File-type-size config >> No file type and size configuration found for this HPL=" + dto.getHpl()+BREAKLINE;
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
	private String validateUsbUsrConf(ProdFileDto dto, String userId) {
		String errorMsg = "";
		if (!usbDet.getUsbDevices().isEmpty()) {
			// if device detected
			USBStorageDevice dev = usbDet.getOneDeviceInfo();
			// check conf from db
			List<UsbConfDto> usbConfList = dev != null ? prodFileServ.findUsbConf(dev.getDeviceName(), dev.getUuid())
					: new ArrayList<UsbConfDto>();

			if (!usbConfList.isEmpty()) {
				// check if match hpl and prod ln
				UsbConfDto usbConf = usbConfList.get(0);
				if (!StringUtils.equals(usbConf.getHpl(), dto.getHpl())
						|| !StringUtils.equalsIgnoreCase(usbConf.getProdLn(), dto.getProdLn())) {
					errorMsg += "USB-User config >> No configuration found for USB=" + usbConf.getName() + ", Serial No="
							+ usbConf.getSerialNo() + ", HPL=" + usbConf.getHpl() + ",Prod Ln=" + usbConf.getProdLn()+BREAKLINE;
				}
				// check if current user is permitted
				List<Usr> usrList = prodFileServ.findUsbUsrByParent(usbConf.getPkUconfId());
				if (!usrList.isEmpty()) {
					usrList = usrList.stream().filter(arg0 -> arg0.getUserId().equals(userId)).collect(Collectors.toList());
					if (usrList.isEmpty()) {
						errorMsg += "USB-User config >> Current user " + userId + " is not configured to be an uploader from this device"+BREAKLINE;
					}
				} else {
					// user list empty
					errorMsg += "USB-User config >> No user is configured to be an uploader from this device"+BREAKLINE;
				}
			} else {
				// no conf found
				errorMsg += "USB-User config >> No configuration found for USB=" + dev.getDeviceName() + ", Serial No=" + dev.getUuid()+BREAKLINE;
			}

		} else {
			// if no usb devices detected
			// file could be coming from local drive/folder
			//logger.debug("validateUsbUsrConf() No usb devices detected! File could be coming from local drive/folder.");
			model.put("info", "USB-User config >> No usb devices detected! However, file can be uploaded from local drive/folder"+BREAKLINE);
			
			//allow user to continue upload from folder
			// do further checking if user is eligible to upload
			// based on grp/hpl, prod ln - check against UsbConf
			// check conf from db
			List<UsbConfDto> usbConfList = prodFileServ.findAllByCriteria("", "", dto.getProdLn(), dto.getHpl());
			if (usbConfList.isEmpty()) {
				errorMsg += "No configuration found for HPL=" + dto.getHpl() + " and Prod Line=" + dto.getProdLn()
						+ BREAKLINE;
			} else {
				// if there is config, check against assign User
				List<Usr> usrList = new ArrayList<Usr>();
				for (UsbConfDto usbConf : usbConfList) {
					usrList.addAll(prodFileServ.findUsbUsrByParent(usbConf.getPkUconfId()));
				}

				usrList = usrList.stream().filter(arg0 -> arg0.getUserId().equals(userId)).collect(Collectors.toList());

				if (usrList.isEmpty()) {
					errorMsg += "User " + userId + " is not configured to upload for HPL=" + dto.getHpl()
							+ " and Prod Line=" + dto.getProdLn() + BREAKLINE;
				}
			}			
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
	private String validateFileNameAgainstG2Lot(ProdFileDto pfDto, String originalFileName) {
		logger.debug("validateFileNameAgainstG2Lot HPL={}, File={}, y={}, m={}, pl={}", pfDto.getHpl(), originalFileName, pfDto.getYear(), pfDto.getMth(), pfDto.getProdLn());
		String errorMsg = "";
		if (StringUtils.isEmpty(originalFileName) || StringUtils.isEmpty(pfDto.getHpl())) {
			errorMsg += "No file/HPL selected for upload! File={"+originalFileName+"}, HPL={"+pfDto.getHpl()+"}"+BREAKLINE;
			return errorMsg;
		}
		
		// get these from file name extraction
		int procTypeTemp = (Integer) model.get("procTypeTemp"); // use for gtms only
		String subProcTemp = (String) model.get("subProcTemp"); // use for gtms only
		String fileFormatTemp = (String) model.get("fileFormatTemp");
		
		String fileFormat = fileFormatTemp;
		//String path = "";
		//int gtmsProcType = 0;
		List<FoldCatgConfDto2> confList = foldCatConfServ2.searchByCriteria(pfDto.getHpl(), "");
		if(!confList.isEmpty()) {			
			// check first if hpl is not gtms
			// as gtms file format will be based on the selected folder
			
			for (FoldCatgConfDto2 confDto : confList) {
				if (StringUtils.equalsIgnoreCase(confDto.getHpl(), CommonConstants.RECORD_TYPE_ID_HPL_GTMS)) {
					// check format form relative path folder
					List<RelPathDto2> relPathList = foldCatConfServ2.searchRelPathByCriteria(confDto.getPkCatgId(), "",
							pfDto.getYear(), pfDto.getMth(), "", pfDto.getProdLn(), "", null, "");
					if(!relPathList.isEmpty()) {
						for (RelPathDto2 pathDto : relPathList) {
							if(pathDto.getPkRelPathId() == Integer.valueOf(pfDto.getFilePath())) {
								//path = pathDto.getFilePath();
								//fileFormat = pathDto.getProdFileFormat();
								//gtmsProcType = pathDto.getProcType();
								if (StringUtils.isEmpty(fileFormat)) {
									//errorMsg += "File format not configured for HPL={" + pfDto.getHpl() + "} and folder={"+pathDto.getFilePath()+"}"+BREAKLINE;
								}
							}
						}
					} else {
						//errorMsg += "Path and File format configuration not found for HPL={"+pfDto.getHpl()+"}"+BREAKLINE;
					}
					
				} else {
					// other than gtms
					//fileFormat = confDto.getProdFileFormat();
					if (StringUtils.isEmpty(fileFormat)) {
						//errorMsg += "File format not configured for HPL={" + pfDto.getHpl() + "}"+BREAKLINE;
					}
				}
			}
			
		} else {
			errorMsg += "Folder-Category configuration not found for HPL={"+pfDto.getHpl()+"}"+BREAKLINE;
		}
		
		if(!StringUtils.isEmpty(errorMsg)) {
			return errorMsg;
		}
		
		FoldCatgConfDto2 currentConf = confList.get(0);
		List<RelPathDto2> relPathList = foldCatConfServ2.searchRelPathByCriteria(currentConf.getPkCatgId(), "",
				pfDto.getYear(), pfDto.getMth(), pfDto.getDay(), pfDto.getProdLn(), pfDto.getSeq(), 0, "");
		RelPathDto2 relPathDto2 = null;
		if(relPathList.isEmpty()) {
			errorMsg += "Path configuration not found for HPL={"+pfDto.getHpl()+"}"+BREAKLINE;
			return errorMsg;
		} else {
			relPathDto2 = relPathList.get(0);
		}

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
			// file >> 220517_KX225HD.csv -> model=8,2|year=1,2|month=3,2|day=5,2|prodLn=14,1|seq=NA|lot=8,7 
			// lot >> KX225HD -> model=KX, year=2022, month=5, day=H, prodLn=D
			// file len -> 14
			if(!StringUtils.equalsIgnoreCase(lotFile, pfDto.getG2Lot())) {
				errorMsg +=CommonConstants.RECORD_TYPE_ID_HPL_IF+" File >> Lot not matched"+BREAKLINE;
			}
			if(StringUtils.isNotEmpty(relPathDto2.getHModel()) 
					&& !StringUtils.equalsIgnoreCase(modelFile, relPathDto2.getHModel())) {
				errorMsg +=CommonConstants.RECORD_TYPE_ID_HPL_IF+" File >> Model not matched"+BREAKLINE;				
			}			
			if(!StringUtils.equalsIgnoreCase(yearFile, relPathDto2.getYear())) {
				errorMsg +=CommonConstants.RECORD_TYPE_ID_HPL_IF+" File >> Year not matched"+BREAKLINE;				
			}			
			if (StringUtils.isNotEmpty(relPathDto2.getMth()) && StringUtils.isNotEmpty(monthFile)) {
				// convert month from file
				String temp = CommonUtil.monthConversionFromFileName(monthFile, currentConf.getHpl());
				if (!StringUtils.equalsIgnoreCase(temp, relPathDto2.getMth())) {
					errorMsg += CommonConstants.RECORD_TYPE_ID_HPL_IF + " File >> Month not matched" + BREAKLINE;
				}
			}		
			if (StringUtils.isNotEmpty(relPathDto2.getDay()) && StringUtils.isNotEmpty(dayFile)) {
				// convert month from file
				String temp = CommonUtil.dayConversionFromFileName(dayFile, currentConf.getHpl());
				if (!StringUtils.equalsIgnoreCase(temp, relPathDto2.getDay())) {
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
			// file >> 569_2021073107480.csv -> model=1,3|year=5,4|month=9,2|day=11,2|prodLn=NA|seq=NA|lot=NA
			// lot >> KMY569210731M1 -> model=569, year=2021, month=7, day=31, prodLn=M1
			// file len -> 17
			if(StringUtils.isNotEmpty(relPathDto2.getHModel()) 
					&& !StringUtils.equalsIgnoreCase(modelFile, relPathDto2.getHModel())) {
				errorMsg +=CommonConstants.RECORD_TYPE_ID_HPL_MGG+" File >> Model not matched"+BREAKLINE;				
			}			
			if(!StringUtils.equalsIgnoreCase(yearFile, relPathDto2.getYear())) {
				errorMsg +=CommonConstants.RECORD_TYPE_ID_HPL_MGG+" File >> Year not matched"+BREAKLINE;				
			}			
			if(StringUtils.isNotEmpty(relPathDto2.getMth()) 
					&& !StringUtils.equalsIgnoreCase(monthFile, relPathDto2.getMth())) {
				errorMsg +=CommonConstants.RECORD_TYPE_ID_HPL_MGG+" File >> Month not matched"+BREAKLINE;				
			}		
			if(StringUtils.isNotEmpty(relPathDto2.getDay()) 
					&& !StringUtils.equalsIgnoreCase(dayFile, relPathDto2.getDay())) {
				errorMsg +=CommonConstants.RECORD_TYPE_ID_HPL_MGG+" File >> Day not matched"+BREAKLINE;				
			}
			// Note : prodln for mgg do not have in mgg filename
			if(StringUtils.isNotEmpty(prodLnFile) && !StringUtils.equalsIgnoreCase(prodLnFile, relPathDto2.getProdLn())) {
				errorMsg +=CommonConstants.RECORD_TYPE_ID_HPL_MGG+" File >> Prod Line not matched"+BREAKLINE;				
			}			
		} else if(StringUtils.equals(pfDto.getHpl(), CommonConstants.RECORD_TYPE_ID_HPL_GTMS)) {
			// GTMS
			// file >> Mikron cell 1,2.1,3 KMY210104504.txt -> model=NA|year=4,2|month=NA|day=NA|prodLn=NA|seq=7,3|lot=NA
			// lot >> 500421403104 -> model=5004, year=2021, month=4, day=0, prodLn=3, seq=104
			// file len -> 12
			if (procTypeTemp == CommonConstants.PROCESS_TYPE_HPL_MIKRON) {
				if (Arrays.asList(CommonConstants.PROCESS_SUBTYPE_GTMS_M_CELL1,
						CommonConstants.PROCESS_SUBTYPE_GTMS_M_CELL2_1, CommonConstants.PROCESS_SUBTYPE_GTMS_M_CELL2_2,
						CommonConstants.PROCESS_SUBTYPE_GTMS_M_CELL3).contains(subProcTemp)) {
				}
			}
			
			// GTMS
			// file >> Back end FET2,3 5004_500421403104.csv -> model=6,4|year=10,2|month=12,1|day=NA|prodLn=13,2|seq=15,3|lot=6,12
			// lot >> 500421403104 -> model=5004, year=2021, month=4, day=0, prodLn=3, seq=104
			// file len -> 17
			if (procTypeTemp == CommonConstants.PROCESS_TYPE_HPL_BACKEND) {
				if (Arrays.asList(CommonConstants.PROCESS_SUBTYPE_GTMS_B_FET2,
						CommonConstants.PROCESS_SUBTYPE_GTMS_B_FET3).contains(subProcTemp)) {

					if (StringUtils.isNotEmpty(relPathDto2.getHModel())
							&& !StringUtils.equalsIgnoreCase(modelFile, relPathDto2.getHModel())) {
						errorMsg += CommonConstants.RECORD_TYPE_ID_HPL_GTMS + " FET2, FET3 File >> Model not matched"
								+ BREAKLINE;
					}
					if (!StringUtils.equalsIgnoreCase(yearFile, relPathDto2.getYear())) {
						errorMsg += CommonConstants.RECORD_TYPE_ID_HPL_GTMS + " FET2, FET3 File >> Year not matched"
								+ BREAKLINE;
					}
					if (StringUtils.isNotEmpty(relPathDto2.getMth())
							&& !StringUtils.equalsIgnoreCase(monthFile, relPathDto2.getMth())) {
						errorMsg += CommonConstants.RECORD_TYPE_ID_HPL_GTMS + " FET2, FET3 File >> Month not matched"
								+ BREAKLINE;
					}
					if (!StringUtils.equalsIgnoreCase(prodLnFile, relPathDto2.getProdLn())) {
						errorMsg += CommonConstants.RECORD_TYPE_ID_HPL_GTMS
								+ " FET2, FET3 File >> Prod Line not matched" + BREAKLINE;
					}
					if (StringUtils.isNotEmpty(relPathDto2.getSeq())
							&& !StringUtils.equalsIgnoreCase(seqFile, relPathDto2.getSeq())) {
						errorMsg += CommonConstants.RECORD_TYPE_ID_HPL_GTMS + " FET2, FET3 File >> Sequence not matched"
								+ BREAKLINE;
					}
				}
			}
			
			// GTMS
			// file >> Back end FET1 500421403104.csv -> model=1,4|year=5,2|month=7,1|day=NA|prodLn=8,2|seq=10,3|lot=1,12
			// lot >> 500421403104 -> model=5004, year=2021, month=4, day=0, prodLn=3, seq=104
			// file len -> 12
			if (procTypeTemp == CommonConstants.PROCESS_TYPE_HPL_BACKEND) {
				if (Arrays.asList(CommonConstants.PROCESS_SUBTYPE_GTMS_B_FET1).contains(subProcTemp)) {
					if (StringUtils.isNotEmpty(relPathDto2.getHModel())
							&& !StringUtils.equalsIgnoreCase(modelFile, relPathDto2.getHModel())) {
						errorMsg += CommonConstants.RECORD_TYPE_ID_HPL_GTMS + " FET1 File >> Model not matched"
								+ BREAKLINE;
					}
					if (!StringUtils.equalsIgnoreCase(yearFile, relPathDto2.getYear())) {
						errorMsg += CommonConstants.RECORD_TYPE_ID_HPL_GTMS + " FET1 File >> Year not matched"
								+ BREAKLINE;
					}
					if (StringUtils.isNotEmpty(relPathDto2.getMth())
							&& !StringUtils.equalsIgnoreCase(monthFile, relPathDto2.getMth())) {
						errorMsg += CommonConstants.RECORD_TYPE_ID_HPL_GTMS + " FET1 File >> Month not matched"
								+ BREAKLINE;
					}
					if (!StringUtils.equalsIgnoreCase(prodLnFile, relPathDto2.getProdLn())) {
						errorMsg += CommonConstants.RECORD_TYPE_ID_HPL_GTMS + " FET1 File >> Prod Line not matched"
								+ BREAKLINE;
					}
					if (StringUtils.isNotEmpty(relPathDto2.getSeq())
							&& !StringUtils.equalsIgnoreCase(seqFile, relPathDto2.getSeq())) {
						errorMsg += CommonConstants.RECORD_TYPE_ID_HPL_GTMS + " FET1 File >> Sequence not matched"
								+ BREAKLINE;
					}
				}
			}
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
	
	private Map<String, Object> removeTempVal(Map<String, Object> model){
		model.remove("hplModelId2Temp");
		model.remove("g2Lot2Temp");
		model.remove("filePath2Temp");
		model.remove("prodLn2Temp");
		model.remove("seq2Temp");

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
		SEQ_LBL = msgSource.getMessage("lblSeq", null, Locale.getDefault());
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
			model.put("info", "No usb connected! However, file can be uploaded from local drive/folder" + BREAKLINE);
		} else {
			String usbDetails = "USB connected" + BREAKLINE;
			for (USBStorageDevice usb : usbDet.getUsbDevices()) {
				usbDetails = usbDetails + "Drive :" + usb.getDevice() + ", Serial No :" + usb.getUuid() + ", USB Name :"
						+ usb.getDeviceName() + BREAKLINE;

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
		
		if(Objects.isNull(usbDet.getOneDeviceInfo())) {
			return finalFileContent;
		}

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

	/*
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
	*/
	
	private List<RelPathDto2> generatePathsItems2(String hplId, String year, String prodLn2, String mth, String seq, Integer procType, String subProc) {
		List<RelPathDto2> paths = new ArrayList<RelPathDto2>();
		List<FoldCatgConfDto2> foldDtoList = foldCatConfServ2.searchByCriteria(hplId, "");
		for (FoldCatgConfDto2 foldDto : foldDtoList) {
			// paths = foldCatConfServ.findRelPathListByParent(foldDto.getPkCatgId());
			//for (RelPathDto2 relPathDto : foldCatConfServ2.findRelPathListByParent(foldDto.getPkCatgId())) {
			//	logger.debug("path {}", relPathDto.getFilePath());
			//	paths.add(relPathDto);
			//}
			if(hplId.equals(CommonConstants.RECORD_TYPE_ID_HPL_GTMS)) {
				paths.addAll(foldCatConfServ2.searchRelPathByCriteria(foldDto.getPkCatgId(), "", year, "", "", prodLn2, "", procType, subProc));
			} else {
				paths.addAll(foldCatConfServ2.searchRelPathByCriteria(foldDto.getPkCatgId(), "", year, mth, "", prodLn2, "", 0, ""));	
			}
			
		}
		return paths;
	}

	private List<String> generateProdLnItems(List<G2LotViewDto> itemsPrd) {
		List<String> prodLnList = itemsPrd.stream().map(G2LotViewDto::getProdLn).distinct()
				.collect(Collectors.toList());
		return prodLnList;
	}
	
	private List<String> generateSeqItems(List<G2LotViewDto> itemsPrd) {
		List<String> seqList = itemsPrd.stream().map(G2LotViewDto::getSeq).distinct()
				.collect(Collectors.toList());
		return seqList;
	}

	private List<G2LotViewDto> generateG2LotItems(String year, String mth, String day, String prodLn2, String hpl,
			String hplModel, String seq) {
		logger.debug(">> generateG2LotItems() "
				+ "year={}, mth={}, day={}, prodLn2={}, hplId={}, hplModelId2={}, seq2={}",year, mth, day, prodLn2, hpl, hplModel, seq);
		List<G2LotViewDto> items = new ArrayList<G2LotViewDto>();
		items = g2LotServ.searchByCriteria("", hpl, hplModel, year, mth, day, prodLn2, seq);
		return items;
	}
	
	private boolean checkDelete() {
		boolean isSuperUser = (Boolean) model.get("isSuperUser");
		boolean cannotDelete = isSuperUser ? false : true;
		return cannotDelete;
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
	
	private void generateFoldConfPath(String hpl, String year) {
		
	}

}
