package com.fms.pfc.controller.main;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.context.annotation.SessionScope;
import org.springframework.web.servlet.ModelAndView;

import com.fms.pfc.common.Authority;
import com.fms.pfc.common.CommonConstants;
import com.fms.pfc.common.CommonUtil;
import com.fms.pfc.domain.dto.main.FoldCatgConfDto;
import com.fms.pfc.domain.dto.main.FoldCatgConfDto2;
import com.fms.pfc.domain.dto.main.RelPathDto;
import com.fms.pfc.domain.dto.main.RelPathDto2;
import com.fms.pfc.domain.model.main.RelPath;
import com.fms.pfc.service.api.base.MenuRoleFunctionService;
import com.fms.pfc.service.api.base.TrxHisService;
import com.fms.pfc.service.api.main.FoldCatgConfService2;
import com.fms.pfc.service.api.main.G2LotViewService;
import com.fms.pfc.validation.common.CommonValidation;

@Controller
@SessionScope
public class FoldCatgConfController2 {
	private static final Logger logger = LoggerFactory.getLogger(FoldCatgConfController2.class);

	private Authority auth;
	private CommonValidation commonValServ;
	private MessageSource msgSource;
	private FoldCatgConfService2 foldCatgConfServ;
	private TrxHisService trxHistServ;
	private G2LotViewService g2LotServ;
	private MenuRoleFunctionService mrfServ;
	
	private Map<String, Object> model = new HashMap<String, Object>();
	private String screenMode = "";

	private static String HPL_LBL = "";
	private static String MODEL_LBL = "";
	private static String YEAR_LBL = "";
	private static String MONTH_LBL = "";
	private static String DAY_LBL = "";
	private static String PROD_LN_LBL = "";
	private static String PROC_TYPE_LBL = "";
	private static String FILE_FORMAT_LBL = "";
	private static String FILE_PATH_LBL = "";
	private static String MODULE_NAME = "";
	private static final String ERR_MSG_UNIQUE = " already exist ";
	private static final String BREAKLINE = ".<br />";
	private static final int YEAR_COUNT = 20;
	private static final int MENU_ITEM_ID = 808;	

	@Value("${data.root.dir}")
	private String DEFAULT_DATA_PATH;

	@Autowired
	public FoldCatgConfController2(Authority auth, CommonValidation commonValServ, MessageSource msgSource,
			FoldCatgConfService2 foldCatgConfServ, TrxHisService trxHistServ, G2LotViewService g2LotServ, MenuRoleFunctionService mrfServ) {
		super();
		this.auth = auth;
		this.commonValServ = commonValServ;
		this.msgSource = msgSource;
		this.foldCatgConfServ = foldCatgConfServ;
		this.trxHistServ = trxHistServ;
		this.g2LotServ = g2LotServ;
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
	@GetMapping("/main/pfc/foldCatgConfList2")
	public ModelAndView initListing(HttpServletRequest request, HttpServletResponse response) {

		removeAlert(model);
		model = auth.onPageLoad(model, request);
		auth.isAuthUrl(request, response);
		mrfServ.menuRoleFunction(model, MENU_ITEM_ID, (String) model.get("loggedUser"));
		
		filter(true);
		model.put("searchYearItems", CommonUtil.yearDropdownItems(YEAR_COUNT));
		model.put("searchMonthItems", CommonUtil.monthDropdownItems());
		model.put("searchDayItems", CommonUtil.dayDropdownItems());
		
		return new ModelAndView("/main/pfc/foldCatgConfList2", model);
	}
	
	private void filter(boolean init) {
		boolean isSuperUser = (Boolean) model.get("isSuperUser");
		String grp = (String) model.get("loggedUserGrp");
		List<String> defaultHpl = g2LotServ.hplList();

		if (init) {
			// check if user is not super user, do filtering
			if (!isSuperUser) {
				model.put("foldCatgAllItems", foldCatgConfServ.searchByCriteria(grp, ""));
				model.put("searchHplItems", defaultHpl.stream().filter(arg0 -> arg0.equals(grp)).collect(Collectors.toList()));
				model.put("searchHplModelItems", g2LotServ.hplModelList(grp));
			} else {
				// if user is super user, remove filter
				model.put("foldCatgAllItems", foldCatgConfServ.searchByCriteria("", ""));
				model.put("searchHplItems", defaultHpl);
				model.put("searchHplModelItems", g2LotServ.hplModelList(""));
			}

		} else {
			// check if user is not super user, do filtering
			if (!isSuperUser) {
				model.put("hplItems", defaultHpl.stream().filter(arg0 -> arg0.equals(grp)).collect(Collectors.toList()));
			} else {
				// if user is super user, remove filter
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
	@RequestMapping("/main/pfc/foldCatgConfForm2View")
	public ModelAndView viewForm(HttpServletRequest request, @RequestParam(name = "pkCatgId") Integer pkCatgId) {

		removeAlert(model);

		FoldCatgConfDto2 dto = foldCatgConfServ.findDtoById(pkCatgId);
		trxHistServ.addTrxHistory(new Date(), "View " + MODULE_NAME, request.getRemoteUser(),
				CommonConstants.FUNCTION_TYPE_VIEW, dto.getHpl(),
				CommonConstants.RECORD_TYPE_ID_FOLD_CATG_CONF, null);

		// Set mode
		screenMode = CommonConstants.SCREEN_MODE_VIEW;
		model.put("mode", screenMode);
		model.put("btnSaveSts", true);

		// Set form header
		model.put("header", "View " + MODULE_NAME);

		// Set confirmation message
		model.put("confirmHeader", "Edit cancellation");
		model.put("confirmMsg", "Do you want to cancel Edit this record ?");

		manageListButton(true, true, true, model);
		
		model.put("relPathDelList", new ArrayList<RelPathDto>());

		// Put model to HTML
		if (dto != null) {

			model.put("pkCatgId", dto.getPkCatgId());
			model.put("foldCatgConfItem", dto);
			model.put("foldCatgConfItemCurrItem", dto);
			model.put("relPathList", foldCatgConfServ.findRelPathListByParent(dto.getPkCatgId()));
			model.put("hplItems", g2LotServ.hplList());
			model.put("hplModelItems", g2LotServ.hplModelList(dto.getHpl()));
			model.put("yearItems", CommonUtil.yearDropdownItems(YEAR_COUNT));
			model.put("monthItems", CommonUtil.monthDropdownItems());
			model.put("dayItems", CommonUtil.dayDropdownItems());
			model.put("procTypeItems", CommonUtil.hplProcTypeDropdownItems());
			
			List<String> prodLnList = g2LotServ.prodLnList(dto.getHpl(), "", "", "", "");			
			logger.debug("view prodLnList size={}", prodLnList.size());
			
			model.put("prodLnItems", prodLnList);
			
			model.put("createdUser", dto.getCreatorId());
			model.put("modifiedUser", dto.getModifierId());

			DateFormat formatter = new SimpleDateFormat("dd/MM/yyy hh:mm:ss a");
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

		return new ModelAndView("/main/pfc/foldCatgConfForm2", model);
	}

	/**
	 * Load add form
	 * 
	 * @param request
	 * @return
	 */
	@GetMapping("/main/pfc/foldCatgConfForm2")
	public ModelAndView loadNewForm(HttpServletRequest request) {
		removeAlert(model);

		// Set mode
		screenMode = CommonConstants.SCREEN_MODE_ADD;
		model.put("mode", screenMode);

		// Set fields
		model.put("foldCatgConfItem", new FoldCatgConfDto());
		model.put("relPathList", new ArrayList<RelPath>());
		model.put("relPathDelList", new ArrayList<RelPathDto>());

		// Set form header
		model.put("header", "Add " + MODULE_NAME);
		// Set confirmation message
		model.put("confirmHeader", "Cancel confirmation");
		model.put("confirmMsg", "Do you want to cancel this record ?");

		model.put("btnEdit", true);
		model.put("btnSaveSts", false);
		//model.put("btnAction", false);
		model.put("btnSave", "Save");

		filter(false);
		model.put("yearItems", CommonUtil.yearDropdownItems(YEAR_COUNT));
		model.put("monthItems", CommonUtil.monthDropdownItems());
		model.put("dayItems", CommonUtil.dayDropdownItems());
		model.put("defaultDataPath", DEFAULT_DATA_PATH);
		model.put("procTypeItems", CommonUtil.hplProcTypeDropdownItems());

		manageListButton(false, true, true, model);

		return new ModelAndView("/main/pfc/foldCatgConfForm2", model);
	}

	/**
	 * Save form
	 * 
	 * @param foldDto
	 * @param request
	 * @param bindingResult
	 * @param session
	 * @return
	 * @throws Exception
	 */
	@PostMapping(value = "/main/pfc/foldCatgConfForm2Save", params = "action=save")
	public ModelAndView saveForm(@Valid FoldCatgConfDto2 foldDto,
			@RequestParam(name = "hplModelId2", required = false) String hplModelId2,
			@RequestParam(name = "prodLn2", required = false) String prodLn2, 
			@RequestParam(name = "year2", required = false) String year2, HttpServletRequest request,
			BindingResult bindingResult, HttpSession session) throws Exception {

		removeAlert(model);
		String mode = screenMode;

		List<RelPathDto2> relPathList = (List<RelPathDto2>) model.get("relPathList");
		
		model.put("foldCatgConfItem", foldDto);

		String errorMsg = validateForm(foldDto);

		if (errorMsg.length() != 0) {
			model.put("error", errorMsg);
			model.remove("success");
			model.put("foldCatgConfItem", foldDto);
			model.put("prodLn2Temp", prodLn2);
			model.put("year2Temp", year2);

			if (mode.equals(CommonConstants.SCREEN_MODE_EDIT)) {
				screenMode = CommonConstants.SCREEN_MODE_EDIT;
				// Set mode to HTML header
				model.put("mode", screenMode);
				model.put("header", "Edit " + MODULE_NAME);
				// Set confirmation message
				model.put("confirmHeader", "Edit cancellation");
				model.put("confirmMsg", "Do you want to cancel edit this record ?");
			}
			return new ModelAndView("/main/pfc/foldCatgConfForm2", model);
		}

		if (mode.equals(CommonConstants.SCREEN_MODE_ADD)) {

			try {
				// If no error proceed to save add form
				// Add form
				Integer result = foldCatgConfServ.save(foldDto, request.getRemoteUser(), relPathList, null);

				// Print add success
				model.put("success", "Record added successfully.");
				model.remove("error");
				model.put("foldCatgConfItem", new FoldCatgConfDto());

				trxHistServ.addTrxHistory(new Date(), "Insert " + MODULE_NAME, request.getRemoteUser(),
						CommonConstants.FUNCTION_TYPE_INSERT, foldDto.getHpl(),
						CommonConstants.RECORD_TYPE_ID_FOLD_CATG_CONF, null);

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
			
			List<RelPathDto2> relPathDelList = (List<RelPathDto2>) model.get("relPathDelList");

			try {
				// Update form
				Integer result = foldCatgConfServ.save(foldDto, request.getRemoteUser(), relPathList, relPathDelList);

				// Print update success
				model.put("success", "Record updated successfully.");
				model.remove("error");

				trxHistServ.addTrxHistory(new Date(), "Update " + MODULE_NAME, request.getRemoteUser(),
						CommonConstants.FUNCTION_TYPE_UPDATE, foldDto.getHpl(),
						CommonConstants.RECORD_TYPE_ID_FOLD_CATG_CONF, null);

			} catch (Exception e) {
				// Print DB exception
				e.printStackTrace();
				model.put("error", "Record failed to update.");
				model.remove("success");
			}
		}

		model.put("btnEdit", false);
		model.put("foldCatgAllItems", foldCatgConfServ.searchByCriteria("", ""));

		return new ModelAndView("/main/pfc/foldCatgConfList2", model);
	}

	/**
	 * Construct error message based on validation result
	 * 
	 * @param ltDto
	 * @return
	 */
	private String validateForm(FoldCatgConfDto2 foldDto) {
		String errorMsg = "";
		errorMsg += commonValServ.validateMandatoryInput(foldDto.getHpl(), HPL_LBL);
		//errorMsg += commonValServ.validateMandatoryInput(foldDto.getYear(), YEAR_LBL);
		//errorMsg += commonValServ.validateMandatoryInput(foldDto.getProdLn(), PROD_LN_LBL);
		
		// validate file format for other than gtms
		if(Objects.nonNull(foldDto.getHpl())) {
			String hplName = foldDto.getHpl();//hplServ.findDtoById(foldDto.getFkHplId()).getHplName();
			if(!StringUtils.equalsIgnoreCase(hplName, CommonConstants.RECORD_TYPE_ID_HPL_GTMS)) {
				errorMsg += commonValServ.validateMandatoryInput(foldDto.getProdFileFormat(), FILE_FORMAT_LBL);
			} else {
				// TBC - if gtms, might need to key in seq
				//errorMsg += commonValServ.validateMandatoryInput(foldDto.getSeq(), SEQ_LBL);
			}
		}
		
		if (screenMode == CommonConstants.SCREEN_MODE_ADD) {
			if(StringUtils.isEmpty(errorMsg)) {
				// check duplication, if exists return error
				// hpl + model + year + mth + day + prodln + seq + file format
				List<FoldCatgConfDto2> list = foldCatgConfServ.searchByCriteria(foldDto.getHpl(), foldDto.getProdFileFormat());
				if (!list.isEmpty()) {
					errorMsg += "Configuration with the same parameter already exists.";
				}
			}			
		} else if (screenMode == CommonConstants.SCREEN_MODE_EDIT) {
			// modify mode
			if(StringUtils.isEmpty(errorMsg)) {
				FoldCatgConfDto2 existing = (FoldCatgConfDto2) model.get("foldCatgConfItemCurrItem");
				if (Objects.nonNull(existing) && !StringUtils.equalsIgnoreCase(existing.toString(), foldDto.toString())) {
					// check if current foldDto modified
					List<FoldCatgConfDto2> list = foldCatgConfServ.searchByCriteria(foldDto.getHpl(), foldDto.getProdFileFormat());
					if (!list.isEmpty()) {
						errorMsg += "Configuration with the same parameter already exists.";
					}
				}				
			}
		}

		return errorMsg;
	}

	/**
	 * Delete item
	 * 
	 * @param request
	 * @param check
	 * @param session
	 * @return
	 */
	@RequestMapping("/main/pfc/foldCatgConfForm2Del")
	public ModelAndView deleteBatch(HttpServletRequest request, @RequestParam(value = "tableRow") String[] check,
			HttpSession session) {

		model.remove("error");
		model.remove("success");
		try {
			// Delete form
			for (int i = 0; i < check.length; i++) {

				if (checkUsage(Integer.parseInt(check[i]))) {
					// Print delete failed
					model.put("error", MODULE_NAME + " is in use and it is not allowed to be deleted from the system.");
				} else {
					String hpl = foldCatgConfServ.findDtoById(Integer.parseInt(check[i])).getHpl();
					foldCatgConfServ.deleteAll(Integer.parseInt(check[i]));
					// Print delete success`
					model.put("success", msgSource.getMessage("msgSuccessDelete", new Object[] {}, Locale.getDefault()));
					model.remove("error");
	
					trxHistServ.addTrxHistory(new Date(), "Delete " + MODULE_NAME, request.getRemoteUser(),
							CommonConstants.FUNCTION_TYPE_DELETE, hpl, CommonConstants.RECORD_TYPE_ID_FOLD_CATG_CONF,
							null);
				}
			}

		} catch (Exception e) {
			// Print DB exception
			model.put("error", msgSource.getMessage("msgFailDelete", new Object[] {}, Locale.getDefault()));
			model.remove("success");
			e.printStackTrace();
		}

		model.put("foldCatgAllItems",
				model.put("foldCatgAllItems", foldCatgConfServ.searchByCriteria("", "")));

		return new ModelAndView(("/main/pfc/foldCatgConfList2"), model);
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
	@PostMapping("/main/pfc/foldCatgConfSearch2")
	public ModelAndView search(HttpServletRequest request, @RequestParam(name = "searchHplId") String searchHplId,
			@RequestParam(name = "searchHplModelId") String searchHplModelId,
			@RequestParam(name = "searchYear") String searchYear, @RequestParam(name = "searchMth") String searchMth,
			@RequestParam(name = "searchDay") String searchDay, @RequestParam(name = "prodLn") String prodLn,
			@RequestParam(name = "prodLnExp") String prodLnExp, @RequestParam(name = "seqNo") String seqNo,
			@RequestParam(name = "seqNoExp") String seqNoExp, HttpSession session) {

		boolean hasError = false;

		removeAlert(model);
		String errorMsg = "";
		
		if (StringUtils.isNotEmpty((String) model.get("filterHpl")))
			searchHplId = (String) model.get("filterHpl");

		model.put("searchHplId", searchHplId);
		model.put("searchHplModelId", searchHplModelId);
		model.put("searchYear", searchYear);
		model.put("searchMth", searchMth);
		model.put("searchDay", searchDay);
		model.put("prodLn", prodLn);
		model.put("prodLnExp", prodLnExp);
		model.put("seqNo", seqNo);
		model.put("seqNoExp", seqNoExp);

		try {
			if (errorMsg.length() == 0) {

				List<FoldCatgConfDto2> items = foldCatgConfServ.searchByCriteria(searchHplId, "");

				model.put("foldCatgAllItems", items);

				StringBuffer sb = new StringBuffer();
				sb.append("Search Criteria: ");
				sb.append(HPL_LBL).append("=").append(searchHplId.isEmpty() ? "<ALL>" : searchHplId).append(", ");
				sb.append(MODEL_LBL).append("=").append(searchHplModelId.isEmpty() ? "<ALL>" : searchHplModelId).append(", ");
				sb.append(YEAR_LBL).append("=").append(searchYear.isEmpty() ? "<ALL>" : searchYear).append(", ");
				sb.append(MONTH_LBL).append("=").append(searchMth.isEmpty() ? "<ALL>" : searchMth).append(", ");
				sb.append(DAY_LBL).append("=").append(searchDay.isEmpty() ? "<ALL>" : searchDay).append(", ");
				sb.append(PROD_LN_LBL).append("=").append(prodLn.isEmpty() ? "<ALL>" : prodLn);

				trxHistServ.addTrxHistory(new Date(), "Search " + MODULE_NAME, request.getRemoteUser(),
						CommonConstants.FUNCTION_TYPE_SEARCH, "Search " + MODULE_NAME,
						CommonConstants.RECORD_TYPE_ID_FOLD_CATG_CONF, sb.toString());

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
				model.remove("success");
				// return back user input
				model.put("foldCatgAllItems", model.put("foldCatgAllItems",
						foldCatgConfServ.searchByCriteria("", "")));
			}
		}

		return new ModelAndView("/main/pfc/foldCatgConfList2", model);
	}

	@SuppressWarnings("unchecked")
	@PostMapping(value = "/main/pfc/foldCatgConfForm2Save", params = "action=add")
	public ModelAndView addToList(HttpServletRequest request, FoldCatgConfDto2 foldDto,
			@RequestParam(name = "pkRelPathId", required = false) Integer pkRelPathId,
			@RequestParam(name = "relPathFilePath", required = false) String relPathFilePath,
			@RequestParam(name = "relPathFileFormat", required = false) String relPathFileFormat,
			@RequestParam(name = "hplModelId2", required = false) String hplModelId2, 
			@RequestParam(name = "prodLn2", required = false) String prodLn2,
			@RequestParam(name = "year2", required = false) String year,
			@RequestParam(name = "mth2", required = false) String mth,
			@RequestParam(name = "day", required = false) String day,
			@RequestParam(name = "seq", required = false) String seq,
			@RequestParam(name = "procType", required = false) int procType,
			HttpSession session) {

		String errorMsg = "";
		model.remove("error");
		List<RelPathDto2> relPathList = (List<RelPathDto2>) model.get("relPathList");
		
		holdValue(foldDto, model, screenMode, relPathFilePath, relPathFileFormat, hplModelId2, prodLn2, year, mth,
				day, seq);
		
		logger.debug("addToList() hplid={}, year={}, prodLn={}, hplModelId2={}, prodLn2={}", foldDto.getHpl(),year,prodLn2, hplModelId2, prodLn2);

		// validation
		{
			//1) check main form first
			if(StringUtils.isEmpty(foldDto.getHpl())) {
				errorMsg += "Kindly make sure the above required information has been filled in - " + HPL_LBL ;
				model.put("error", errorMsg);
				return new ModelAndView("/main/pfc/foldCatgConfForm2", model);
			}
			
			//2) check sub-form
			// as of current specs, GTMS has different folder format to cater for mikron and back end process
			// so only validate both in sub-form for GTMS
			String hplName = foldDto.getHpl();
			if(StringUtils.equalsIgnoreCase(hplName.trim(), CommonConstants.GROUP_ID_GTMS)) {
				if (StringUtils.isEmpty(relPathFilePath) 
						|| StringUtils.isEmpty(relPathFileFormat)
						|| StringUtils.isEmpty(year)
						|| StringUtils.isEmpty(mth)
						|| StringUtils.isEmpty(prodLn2)
						|| procType <= 0) {
					errorMsg += "Please key in " + FILE_PATH_LBL 
							+ ", " + FILE_FORMAT_LBL 
							+ ", " + YEAR_LBL 
							+ ", " + MONTH_LBL 
							+ ", " + PROD_LN_LBL 
							+ ", " + PROC_TYPE_LBL
							+ BREAKLINE;
					model.put("error", errorMsg);
					return new ModelAndView("/main/pfc/foldCatgConfForm2", model);
				}				
			} else {
				if (StringUtils.isEmpty(relPathFilePath)
						|| StringUtils.isEmpty(year)
						|| StringUtils.isEmpty(mth)
						|| StringUtils.isEmpty(prodLn2)) {
					errorMsg += "Please key in " + FILE_PATH_LBL 
							+ ", " + YEAR_LBL 
							+ ", " + MONTH_LBL 
							+ ", " + PROD_LN_LBL 
							+ BREAKLINE;
					model.put("error", errorMsg);
					return new ModelAndView("/main/pfc/foldCatgConfForm2", model);
				}	
			}

			if (!CommonUtil.isPathValid(relPathFilePath)) {
				errorMsg += FILE_PATH_LBL + " is not a valid path.";
				model.put("error", errorMsg);
				return new ModelAndView("/main/pfc/foldCatgConfForm2", model);
			}
			
			String paramStr = "hModel=" + hplModelId2 
					+ ";year=" + year
					+ ";mth=" + mth
					+ ";day=" + day
					+ ";prodLn=" + prodLn2
					+ ";seq=" + seq
					+ ";filePath=" + relPathFilePath
					+ ";prodFileFormat=" + relPathFileFormat
					+ ";procType=" + procType
					;

			// if relPath duplicate
			List<RelPathDto2> checkList = relPathList;
			for (RelPathDto2 relPathDto : checkList) {
				logger.debug("addToRelPathList() loop dto={}, param={}", relPathDto.toString(), paramStr);
				if (StringUtils.equals(relPathDto.toString(),paramStr)) {
					errorMsg += "Combination already in the list" + BREAKLINE;
					model.put("error", errorMsg);
					return new ModelAndView("/main/pfc/foldCatgConfForm2", model);
				}
			}
		}

		int row = relPathList.size();
		RelPathDto2 dto = new RelPathDto2();
		dto.setFkCatgId(foldDto.getPkCatgId());
		dto.setFilePath(relPathFilePath);
		dto.setProdFileFormat(relPathFileFormat);
		dto.setHModel(hplModelId2);
		dto.setYear(year);
		dto.setMth(mth);
		dto.setDay(day);
		dto.setSeq(seq);
		dto.setProdLn(prodLn2);
		dto.setProcType(procType);
		dto.setRowNo(row);
		dto.setIndicator("new");
		relPathList.add(dto);

		model.put("relPathList", relPathList);
		manageListButton(false, true, true, model);
		clearRelPathForm(model);

		return new ModelAndView("/main/pfc/foldCatgConfForm2", model);
	}

	@SuppressWarnings("unchecked")
	@PostMapping(value = "/main/pfc/foldCatgConfForm2Save", params = "action=delete")
	public ModelAndView deleteFromList(HttpServletRequest request, FoldCatgConfDto2 foldDto,
			@RequestParam(name = "rowNo") int rowNo, @RequestParam(name = "relPathFilePath") String relPathFilePath,
			@RequestParam(name = "relPathFileFormat") String relPathFileFormat,
			@RequestParam(name = "hplModelId2", required = false) String hplModelId2, 
			@RequestParam(name = "prodLn2", required = false) String prodLn2,
			@RequestParam(name = "year2", required = false) String year,
			@RequestParam(name = "mth2", required = false) String mth,
			@RequestParam(name = "day", required = false) String day,
			@RequestParam(name = "seq", required = false) String seq,
			@RequestParam(name = "procType", required = false) int procType,
			HttpSession session) {

		logger.debug("deleteFromRelPathList() ... ");

		holdValue(foldDto, model, screenMode, relPathFilePath, relPathFileFormat, hplModelId2, prodLn2, year, mth,
				day, seq);
		
		List<RelPathDto2> relPathList = (List<RelPathDto2>) model.get("relPathList");
		
		if(StringUtils.equalsIgnoreCase(screenMode, CommonConstants.SCREEN_MODE_EDIT)) {
			
			//check if folder in use, then can't delete
			Path path = Paths.get(relPathList.get(rowNo).getFilePath());
			try {
				if(!CommonUtil.isEmptyDir(path)) {
					model.put("error", "Folder "+path.toString()+" is currently in used.");
					return new ModelAndView("/main/pfc/foldCatgConfForm2", model);
				}
			} catch (IOException e) {
				model.put("error", "Unable to check folder. "+e.getMessage());
				return new ModelAndView("/main/pfc/foldCatgConfForm2", model);
			}
			
			List<RelPathDto2> delList = (List<RelPathDto2>) model.get("relPathDelList");
			delList.add(relPathList.get(rowNo));
		}		
		
		relPathList.remove(rowNo);

		int reDoRowNo = 0;
		for (int row = 0; row < relPathList.size(); row++) {
			relPathList.get(row).setRowNo(reDoRowNo);
			reDoRowNo++;
		}

		model.put("relPathList", relPathList);
		manageListButton(false, true, true, model);
		clearRelPathForm(model);

		return new ModelAndView("/main/pfc/foldCatgConfForm2", model);
	}

	@PostMapping(value = "/main/pfc/foldCatgConfForm2Save", params = "action=edit")
	public ModelAndView editForm(HttpServletRequest request, HttpSession session) {

		screenMode = CommonConstants.SCREEN_MODE_EDIT;
		model.put("mode", screenMode);
		manageListButton(false, true, true, model);
		model.put("btnEdit", true);
		model.put("btnSaveSts", false);
		model.put("btnSave", "Save");
		model.put("header", "Edit " + MODULE_NAME);
		model.put("defaultDataPath", DEFAULT_DATA_PATH);
		
		model.remove("success");
		model.remove("error");
		clearRelPathForm(model);

		return new ModelAndView("/main/pfc/foldCatgConfForm2", model);
	}

	@SuppressWarnings("unchecked")
	@GetMapping("/main/pfc/relPathFormGetData2")
	public ModelAndView retrieveFromDataTable(HttpServletRequest request, @RequestParam(name = "rowNo") int rowNo,
			RelPathDto2 relPathDto, BindingResult result, HttpSession session) {

		String mode = model.get("mode").toString();
		model.remove("error");

		List<RelPathDto2> relPathList = (List<RelPathDto2>) model.get("relPathList");
		relPathList.forEach(obj -> {
			if (obj.getRowNo() == rowNo) {
				model.put("pkRelPathId", obj.getPkRelPathId());
				model.put("relPathFilePath", obj.getFilePath());
				model.put("relPathFileFormat", obj.getProdFileFormat());
				model.put("day", obj.getDay());
				model.put("seq", obj.getSeq());
				model.put("procType", obj.getProcType());
				model.put("rowNo", obj.getRowNo());
				
				if (!StringUtils.equals(mode, CommonConstants.SCREEN_MODE_VIEW)) {
					model.put("prodLn2Temp", obj.getProdLn());
					model.put("hplModelId2Temp", obj.getHModel());
					model.put("year2Temp", obj.getYear());
					model.put("mth2Temp", obj.getMth());
				} else {
					model.put("prodLn", obj.getProdLn());
					model.put("hModel", obj.getHModel());
					model.put("year", obj.getYear());
					model.put("mth", obj.getMth());
				}
			}
		});

		if (!StringUtils.equals(mode, CommonConstants.SCREEN_MODE_VIEW)) {
			manageListButton(false, false, false, model);
		}

		return new ModelAndView("/main/pfc/foldCatgConfForm2", model);
	}

	@SuppressWarnings("unchecked")
	@PostMapping(value = "/main/pfc/foldCatgConfForm2Save", params = "action=update")
	public ModelAndView updateToList(HttpServletRequest request, FoldCatgConfDto2 foldDto,
			@RequestParam(name = "rowNo") int rowNo, @RequestParam(name = "relPathFilePath") String relPathFilePath,
			@RequestParam(name = "relPathFileFormat") String relPathFileFormat,
			@RequestParam(name = "hplModelId2", required = false) String hplModelId2, 
			@RequestParam(name = "prodLn2", required = false) String prodLn2,
			@RequestParam(name = "year2", required = false) String year,
			@RequestParam(name = "mth2", required = false) String mth,
			@RequestParam(name = "day", required = false) String day,
			@RequestParam(name = "seq", required = false) String seq,
			@RequestParam(name = "procType", required = false) int procType,
			HttpSession session) {

		model.remove("error");
		holdValue(foldDto, model, screenMode, relPathFilePath, relPathFileFormat, hplModelId2, prodLn2, year, mth,
				day, seq);
		List<RelPathDto2> relPathList = (List<RelPathDto2>) model.get("relPathList");
		
		/*if(StringUtils.equalsIgnoreCase(screenMode, CommonConstants.SCREEN_MODE_EDIT)) {
			
			//check if folder in use, then can't delete
			Path path = Paths.get(relPathList.get(rowNo).getFilePath());
			try {
				if(!CommonUtil.isEmpty(path)) {
					model.put("error", "Folder "+path.toString()+" is currently in used.");
					return new ModelAndView("/main/pfc/foldCatgConfForm2", model);
				}
			} catch (IOException e) {
				model.put("error", "Unable to check folder. "+e.getMessage());
				return new ModelAndView("/main/pfc/foldCatgConfForm2", model);
			}
		}	*/	

		// validation
		{
			String paramStr = "hModel=" + hplModelId2 
					+ ";year=" + year
					+ ";mth=" + mth
					+ ";day=" + day
					+ ";prodLn=" + prodLn2
					+ ";seq=" + seq
					+ ";filePath=" + relPathFilePath
					+ ";prodFileFormat=" + relPathFileFormat
					+ ";procType=" + procType
					;
			
			String errorMsg = "";
			// if Rel Path duplicate
			List<RelPathDto2> checkList = relPathList;
			for (RelPathDto2 relPathDto : checkList) {
				logger.debug("updateToList() loop dto={}, param={}", relPathDto.toString(), paramStr);
				if (StringUtils.equals(relPathDto.toString(),paramStr)) {
					errorMsg += "Combination already in the list" + BREAKLINE;
					model.put("error", errorMsg);
					return new ModelAndView("/main/pfc/foldCatgConfForm2", model);
				}
			}
		}

		for (RelPathDto2 obj : relPathList) {
			if (obj.getRowNo() == rowNo) {
				obj.setFilePath(relPathFilePath);
				obj.setProdFileFormat(relPathFileFormat);
				obj.setHModel(hplModelId2);
				obj.setYear(year);
				obj.setMth(mth);
				obj.setDay(day);
				obj.setSeq(seq);
				obj.setProdLn(prodLn2);
				obj.setProcType(procType);
			}
		}

		model.put("relPathList", relPathList);
		manageListButton(false, true, true, model);
		clearRelPathForm(model);

		return new ModelAndView("/main/pfc/foldCatgConfForm2", model);
	}	
	
	private boolean checkUsage(int parseInt) {
		List<RelPathDto2> relPathList = foldCatgConfServ.findRelPathListByParent(parseInt);
		if(relPathList.isEmpty()) return true;
		
		int listSize = relPathList.size();
		int emptyPathCnt = 0;
		int emptyFolderCnt = 0;
		for (RelPathDto2 relPathDto : relPathList) {
			if(StringUtils.isEmpty(relPathDto.getFilePath())) {
				emptyPathCnt ++;
			} else {
				try {
					if(CommonUtil.isEmptyDir(Paths.get(relPathDto.getFilePath()))) {
						emptyFolderCnt ++;
					}
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		
		if(emptyPathCnt == listSize) return true;
		
		if(emptyFolderCnt == listSize) return true;
		
		return false;
	}

	private Map<String, Object> removeAlert(Map<String, Object> model) {
		model.remove("error");
		model.remove("success");

		return model;
	}

	private Map<String, Object> manageListButton(boolean relPathAdd, boolean relPathUpdate, boolean relPathDelete,
			Map<String, Object> model) {
		model.put("listAddBtn", relPathAdd);
		model.put("listUpdateBtn", relPathUpdate);
		model.put("listDeleteBtn", relPathDelete);

		return model;
	}

	private Map<String, Object> clearRelPathForm(Map<String, Object> model) {
		model.remove("rowNo");
		model.remove("relPathFilePath");
		model.remove("relPathFileFormat");
		model.remove("pkRelPathId");
		//model.remove("year");
		//model.remove("mth");
		model.remove("day");
		model.remove("seq");
		model.remove("hplModelId2Temp");
		model.remove("prodLn2Temp");
		model.remove("year2Temp");
		model.remove("mth2Temp");

		return model;
	}

	private Map<String, Object> holdValue(FoldCatgConfDto2 foldDto, Map<String, Object> model, String screenMode,
			String relPathFilePath, String relPathFileFormat, String hplModelId2, String prodLn2
			, String year
			, String mth
			, String day
			, String seq) {

		model.put("mode", screenMode);
		model.put("foldCatgConfItem", foldDto);
		model.put("relPathFilePath", relPathFilePath);
		model.put("relPathFileFormat", relPathFileFormat);
		//model.put("year", year);
		//model.put("mth", mth);
		model.put("day", day);
		model.put("seq", seq);

		if (StringUtils.equals(screenMode, CommonConstants.SCREEN_MODE_ADD)) {
			model.put("hplModelId2Temp", hplModelId2);
			model.put("prodLn2Temp", prodLn2);
			model.put("year2Temp", year);
			model.put("mth2Temp", mth);

		} else if (StringUtils.equals(screenMode, CommonConstants.SCREEN_MODE_EDIT)) {

		}

		return model;
	}
	
	private void getAllLabels() {
		HPL_LBL = msgSource.getMessage("lblHpl", null, Locale.getDefault());
		MODEL_LBL = msgSource.getMessage("lblHplModel", null, Locale.getDefault());
		YEAR_LBL = msgSource.getMessage("lblYear", null, Locale.getDefault());
		MONTH_LBL = msgSource.getMessage("lblMonth", null, Locale.getDefault());
		DAY_LBL = msgSource.getMessage("lblDay", null, Locale.getDefault());
		PROD_LN_LBL = msgSource.getMessage("lblProdLn", null, Locale.getDefault());
		PROC_TYPE_LBL = msgSource.getMessage("lblProcType", null, Locale.getDefault());
		FILE_FORMAT_LBL = msgSource.getMessage("lblFileFormat", null, Locale.getDefault());
		FILE_PATH_LBL = msgSource.getMessage("lblFilePath", null, Locale.getDefault());
		MODULE_NAME = msgSource.getMessage("moduleFoldCatgConf", null, Locale.getDefault());
	}
	
}
