package com.fms.pfc.controller.main;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import javax.annotation.PreDestroy;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
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
import com.fms.pfc.domain.dto.GrpMenuItemDto;
import com.fms.pfc.domain.dto.LabelAndValueDto;
import com.fms.pfc.service.api.base.GrpMenuService;
import com.fms.pfc.service.api.base.MenuRoleFunctionService;
import com.fms.pfc.service.api.base.TrxHisService;
import com.fms.pfc.service.api.main.G2LotViewService;
import com.fms.pfc.validation.common.CommonValidation;

@Controller
@SessionScope
public class GrpMenuItemController {
	private static final Logger logger = LoggerFactory.getLogger(GrpMenuItemController.class);

	private Authority auth;
	private CommonValidation commonValServ;
	private MessageSource msgSource;
	private TrxHisService trxHistServ;
	private G2LotViewService g2LotServ;
	private MenuRoleFunctionService mrfServ;
	private GrpMenuService gmServ;
	
	private Map<String, Object> model = new HashMap<String, Object>();
	private String screenMode = "";

	private static String MODULE_NAME = "";
	private static final String ERR_MSG_UNIQUE = " already exist ";
	private static final String BREAKLINE = ".<br />";
	private static final int MENU_ITEM_ID = 706;

	@Autowired
	public GrpMenuItemController(Authority auth, CommonValidation commonValServ, MessageSource msgSource,
			TrxHisService trxHistServ, G2LotViewService g2LotServ, MenuRoleFunctionService mrfServ, GrpMenuService gmServ) {
		super();
		this.auth = auth;
		this.commonValServ = commonValServ;
		this.msgSource = msgSource;
		this.trxHistServ = trxHistServ;
		this.g2LotServ = g2LotServ;
		this.mrfServ = mrfServ;
		this.gmServ = gmServ;
		
		getAllLables();
	}

	/**
	 * Display initial screen
	 * 
	 * @param request
	 * @param response
	 * @return
	 */
	@GetMapping("/base/admin/grpMenuItemList")
	public ModelAndView initListing(HttpServletRequest request, HttpServletResponse response) {

		removeAlert(model);
		model = auth.onPageLoad(model, request);
		auth.isAuthUrl(request, response);
		mrfServ.menuRoleFunction(model, MENU_ITEM_ID, (String) model.get("loggedUser"));
		
		filter(true);		

		return new ModelAndView("/base/admin/grpMenuItemList", model);
	}
	
	private void filter(boolean init) {
		boolean isSuperUser = (Boolean) model.get("isSuperUser");
		String grp = (String) model.get("loggedUserGrp");
		String org = (String) model.get("loggedUserOrg");
		List<String> defaultHpl = g2LotServ.hplList();

		if (init) {
			// check if user is not super user, do filtering
			if (!isSuperUser) {
				model.put("searchHplItems",
						defaultHpl.stream().filter(arg0 -> arg0.equals(grp)).collect(Collectors.toList()));
			} else {
				// if user is super user, remove filter
				model.put("searchHplItems", defaultHpl);
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
	@RequestMapping("/base/admin/grpMenuItemFormView")
	public ModelAndView viewForm(HttpServletRequest request, @RequestParam(name = "hpl") String hpl) {

		removeAlert(model);

		//UsbConfDto dto = usbConfServ.findDtoById(pkUconfId);
		trxHistServ.addTrxHistory(new Date(), "View " + MODULE_NAME, request.getRemoteUser(),
				CommonConstants.FUNCTION_TYPE_VIEW, hpl,
				CommonConstants.RECORD_TYPE_ID_GRP_MENU_ITEM, null);

		// Set mode
		screenMode = CommonConstants.SCREEN_MODE_VIEW;
		model.put("mode", screenMode);
		model.put("btnSaveSts", true);

		// Set form header
		model.put("header", "View " + MODULE_NAME);

		// Set confirmation message
		model.put("confirmHeader", "Edit cancellation");
		model.put("confirmMsg", "Do you want to cancel Edit this record ?");
		
		filter(false);
		model.put("hpl", hpl);
		
		List<LabelAndValueDto> selected = getSelectedMenuItem(hpl);
		List<LabelAndValueDto> remaining = getRemainingMenuItem(selected, hpl);

		model.put("selMenuItems", selected);
		model.put("menuItems", remaining);

		// Put model to HTML
//		if (dto != null) {
//
//			model.put("pkUconfId", dto.getPkUconfId());
//			model.put("grpMenuItemItem", dto);
//			model.put("grpMenuItemCurrItem", dto);
//			model.put("prodLn2Temp", dto.getProdLn());
//
//			model.put("createdUser", dto.getCreatorId());
//			model.put("modifiedUser", dto.getModifierId());
//
//			
//
//			model.put("hplItems", g2LotServ.hplList());
//			model.put("prodLnItems", g2LotServ.prodLnList(dto.getHpl(), "", "", "", ""));
//
//			SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyy hh:mm:ss a");
//			try {
//				String createdInfo = "Created by " + dto.getCreatorId() + " on "
//						+ formatter.format(dto.getCreatedDatetime());
//				model.put("createdInfo", createdInfo);
//			} catch (Exception e) {
//			}
//
//			String modifiedInfo = "";
//			try {
//				modifiedInfo = "Modified by " + dto.getModifierId() + " on "
//						+ formatter.format(dto.getModifiedDatetime());
//			} catch (Exception e) {
//				modifiedInfo = "Modified by --";
//			}
//			model.put("modifiedInfo", modifiedInfo);
//		}

		return new ModelAndView("/base/admin/grpMenuItemForm", model);
	}

	/**
	 * Load add form
	 * 
	 * @param request
	 * @return
	 */
	@GetMapping("/base/admin/grpMenuItemForm")
	public ModelAndView loadNewForm(HttpServletRequest request) {
		removeAlert(model);

		// Set mode
		screenMode = CommonConstants.SCREEN_MODE_ADD;
		model.put("mode", screenMode);

		// Set fields
		model.put("grpMenuItemItem", new GrpMenuItemDto());
		filter(false);
		
		// model.put("letterTypeItems", ltServ.getLetterTypeList());
		// model.put("refCountryItems", ltServ.getRefCountryList());
		// model.put("prItems", ltServ.getPrList());

		// Set form header
		model.put("header", "Add " + MODULE_NAME);
		// Set confirmation message
		model.put("confirmHeader", "Cancel confirmation");
		model.put("confirmMsg", "Do you want to cancel this record ?");

		model.put("btnEdit", true);
		model.put("btnSaveSts", false);
		//model.put("btnAction", false);
		model.put("btnSave", "Save");
		
		return new ModelAndView("/base/admin/grpMenuItemForm", model);
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
	@PostMapping(value = "/base/admin/grpMenuItemFormSave", params = "action=save")
	public ModelAndView saveForm(@Valid GrpMenuItemDto dto,
			@RequestParam(value = "menuItemChoice1", required = false) String[] usrChoiceList,
			HttpServletRequest request,
			BindingResult bindingResult, HttpSession session) throws Exception {

		removeAlert(model);
		String mode = screenMode;

//		dto.setProdLn(prodLn2);
		model.put("grpMenuItemItem", dto);
		String hpl = (String) model.get("hpl");

		String errorMsg = validateForm( usrChoiceList);

		if (errorMsg.length() != 0) {
			model.put("error", errorMsg);
			model.remove("success");
			model.put("grpMenuItemItem", dto);
//			model.put("prodLn2Temp", prodLn2);

			if (mode.equals(CommonConstants.SCREEN_MODE_EDIT)) {
				screenMode = CommonConstants.SCREEN_MODE_EDIT;
				// Set mode to HTML header
				model.put("mode", screenMode);
				model.put("header", "Edit " + MODULE_NAME);
				// Set confirmation message
				model.put("confirmHeader", "Edit cancellation");
				model.put("confirmMsg", "Do you want to cancel edit this record ?");
			}
			return new ModelAndView("/base/admin/grpMenuItemForm", model);
		}

		List<String> selected = new ArrayList<String>();
		if (Objects.nonNull(usrChoiceList) && usrChoiceList.length != 0) {
			selected = Arrays.asList(usrChoiceList).stream().collect(Collectors.toList());
		}

		if (mode.equals(CommonConstants.SCREEN_MODE_ADD)) {

			try {
				// If no error proceed to save add form
				// Add form
				Integer result = null;//usbConfServ.save2(dto, request.getRemoteUser(), usrIds);

				// Print add success
				model.put("success", "Record added successfully.");
				model.remove("error");
				model.put("grpMenuItemItem", new GrpMenuItemDto());

				trxHistServ.addTrxHistory(new Date(), "Insert " + MODULE_NAME, request.getRemoteUser(),
						CommonConstants.FUNCTION_TYPE_INSERT,
						Objects.nonNull(result) ? result.toString() : "",
						CommonConstants.RECORD_TYPE_ID_GRP_MENU_ITEM, null);

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
				Integer result = null;//usbConfServ.save2(dto, request.getRemoteUser(), usrIds);
				List<String> current = getSelectedMenuItem(hpl)
						.stream()
						.map(arg0 -> String.valueOf(arg0.getValue()))
						.collect(Collectors.toList());
				gmServ.save((String) model.get("hpl"), selected, current);
//				logger.debug(" hpl >>> "+ hpl);
//				selected.forEach(arg0 -> {
//					logger.debug(" sel >>> "+ arg0);
//				});
//				current.forEach(arg0 -> {
//					logger.debug(" cur >>> "+ arg0);
//				});

				// Print update success
				model.put("success", "Record updated successfully.");
				model.remove("error");

				trxHistServ.addTrxHistory(new Date(), "Update " + MODULE_NAME, request.getRemoteUser(),
						CommonConstants.FUNCTION_TYPE_UPDATE,
						Objects.nonNull(result) ? result.toString() : "",
						CommonConstants.RECORD_TYPE_ID_GRP_MENU_ITEM, null);

			} catch (Exception e) {
				// Print DB exception
				e.printStackTrace();
				model.put("error", "Record failed to update.");
				model.remove("success");
			}
		}

		model.put("btnEdit", false);
		model.put("searchHplItems", g2LotServ.hplList());

		return new ModelAndView("/base/admin/grpMenuItemList", model);
	}

	@PostMapping(value = "/base/admin/grpMenuItemFormSave", params = "action=edit")
	public ModelAndView editForm(HttpServletRequest request, HttpSession session) {

		screenMode = CommonConstants.SCREEN_MODE_EDIT;
		model.put("mode", screenMode);
		// manageHplModelButton(false, true, true, model);
		model.put("btnEdit", true);
		model.put("btnSaveSts", false);
		model.put("btnSave", "Save");
		model.put("header", "Edit " + MODULE_NAME);
		model.remove("success");
		model.remove("error");
		// clearHplModelForm(model);

		return new ModelAndView("/base/admin/grpMenuItemForm", model);
	}

	/**
	 * Construct error message based on validation result
	 * 
	 * @param dto
	 * @return
	 */
	private String validateForm(String[] usrChoiceList) {
		String errorMsg = "";

		if (!Objects.nonNull(usrChoiceList) || (Objects.nonNull(usrChoiceList) && usrChoiceList.length <= 0)) {
			errorMsg += commonValServ.validateMandatoryInput("", "Menu Item");
		}

		if (screenMode == CommonConstants.SCREEN_MODE_ADD) {
			//errorMsg += validateDuplicateName(dto.getName());
			//errorMsg += validateDuplicateSerialNo(dto.getSerialNo());
		} else {

			if (null != model.get("grpMenuItemCurrItem")) {
//				UsbConfDto currDto = (UsbConfDto) model.get("grpMenuItemCurrItem");
//
//				logger.debug("validateForm cur name=" + currDto.getName() + "; new name=" + dto.getName());
//				if (!StringUtils.equalsIgnoreCase(currDto.getName(), dto.getName())) {
//					// check if current name value modified
//					errorMsg += validateDuplicateName(dto.getName());
//				}
//
//				if (!StringUtils.equalsIgnoreCase(currDto.getSerialNo(), dto.getSerialNo())) {
//					// check if current name value modified
//					errorMsg += validateDuplicateSerialNo(dto.getSerialNo());
//				}
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
	@RequestMapping("/base/admin/grpMenuItemFormDel")
	public ModelAndView deleteUsbConfBatch(HttpServletRequest request,
			@RequestParam(value = "tableRow") String[] check, HttpSession session) {

		model.remove("error");
		model.remove("success");
		try {
			// Delete form
			for (int i = 0; i < check.length; i++) {

//				if (1==1) {
//					// Print delete failed
//					model.put("error", "Letter Content is in used and it is not allow to delete from the system.");
//				} else {
				//usbConfServ.delete(Integer.parseInt(check[i]));
				// Print delete success`
				model.put("success", msgSource.getMessage("msgSuccessDelete", new Object[] {}, Locale.getDefault()));

				trxHistServ.addTrxHistory(new Date(), "Delete " + MODULE_NAME, request.getRemoteUser(),
						CommonConstants.FUNCTION_TYPE_DELETE, check[i], CommonConstants.RECORD_TYPE_ID_GRP_MENU_ITEM, null);
//				}
			}

		} catch (Exception e) {
			// Print DB exception
			model.put("error", msgSource.getMessage("msgFailDelete", new Object[] {}, Locale.getDefault()));
			e.printStackTrace();
		}

		model.put("searchHplItems", g2LotServ.hplList());

		return new ModelAndView(("/base/admin/grpMenuItemList"), model);
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
	@PostMapping("/base/admin/grpMenuItemSearch")
	public ModelAndView search(HttpServletRequest request, @RequestParam(name = "serialNo") String serialNo,
			@RequestParam(name = "serialNoExp") String serialNoExp, @RequestParam(name = "usbName") String usbName,
			@RequestParam(name = "usbNameExp") String usbNameExp, @RequestParam(name = "grpType") String grpType,
			@RequestParam(name = "assignTo") String assignTo, @RequestParam(name = "assignToExp") String assignToExp,
			HttpSession session) {

		boolean hasError = false;

		removeAlert(model);
		String errorMsg = "";
		
		if (StringUtils.isNotEmpty((String) model.get("filterHpl")))
			grpType = (String) model.get("filterHpl");

		model.put("serialNo", serialNo);
		model.put("serialNoExp", serialNoExp);
		model.put("usbName", usbName);
		model.put("usbNameExp", usbNameExp);
		model.put("grpType", grpType);
		model.put("assignTo", assignTo);
		model.put("assignToExp", assignToExp);
		
		logger.debug(
				"doSearch() ... serialNo={}, serialNoExp={}, usbName={}, usbNameExp={}, grpType={}, assignTo={}, assignToExp={} ",
				serialNo, serialNoExp, usbName, usbNameExp, grpType, assignTo, assignToExp);

		try {
			if (errorMsg.length() == 0) {

//				List<UsbConfSearch> items = usbConfServ.searchByCriteria(serialNo, serialNoExp, usbName, usbNameExp,
//						grpType, assignTo, assignToExp);
//				model.put("searchHplItems", items);

//				StringBuffer sb = new StringBuffer();
//				sb.append("Search Criteria: ");
//				sb.append(SERIALNO_TXT).append("=").append(serialNo.isEmpty() ? "<ALL>" : serialNo).append(", ");
//				sb.append(USBNAME_TXT).append("=").append(usbName.isEmpty() ? "<ALL>" : usbName).append(", ");
//				sb.append(GROUP_TXT).append("=").append(grpType.isEmpty() ? "<ALL>" : grpType).append(", ");
//				sb.append(ASSIGNTO_TXT).append("=").append(assignTo.isEmpty() ? "<ALL>" : assignTo);
//
//				trxHistServ.addTrxHistory(new Date(), "Search " + MODULE_NAME, request.getRemoteUser(),
//						CommonConstants.FUNCTION_TYPE_SEARCH, "Search " + MODULE_NAME,
//						CommonConstants.RECORD_TYPE_ID_GRP_MENU_ITEM, sb.toString());

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
				model.put("searchHplItems", g2LotServ.hplList());
			}
		}

		return new ModelAndView("/base/admin/grpMenuItemList", model);
	}
	
	@PreDestroy
	public void destroy() throws IOException {
		
	}
	
	private Map<String, Object> removeAlert(Map<String, Object> model) {
		model.remove("error");
		model.remove("success");

		return model;
	}

	private String validateDuplicateSerialNo(String serialNo) {
//		List<UsbConfDto> dtoList = usbConfServ.findAllBySerialNo(serialNo);
//		if (!dtoList.isEmpty()) {
//			return SERIALNO_TXT + serialNo + ERR_MSG_UNIQUE + BREAKLINE;
//		}
		return "";
	}

	private String validateDuplicateName(String name) {
//		List<UsbConfDto> dtoList = usbConfServ.findAllByName(name);
//		if (!dtoList.isEmpty()) {
//			return USBNAME_TXT + name + ERR_MSG_UNIQUE + BREAKLINE;
//		}
		return "";
	}

	/**
	 * Get selected User List based on Uconf id
	 * 
	 * @param pkUconfId
	 * @return
	 */
	protected List<LabelAndValueDto> getSelectedMenuItem(String hpl) {
		return gmServ.searchMenuDto2(hpl);
	}

	/**
	 * Get remaining User. Overall User minus selected User
	 * 
	 * @param selected
	 * @return
	 */
	protected List<LabelAndValueDto> getRemainingMenuItem(List<LabelAndValueDto> selected, String hpl) {
		List<LabelAndValueDto> remaining = gmServ.searchMenuItems().stream()
				.filter(arg0 -> !selected.contains(arg0))
				.collect(Collectors.toList());
		return remaining;
	}
	
	private void getAllLables() {
		MODULE_NAME = msgSource.getMessage("moduleGrpMenuItem", null, Locale.getDefault());
	}
	
}
