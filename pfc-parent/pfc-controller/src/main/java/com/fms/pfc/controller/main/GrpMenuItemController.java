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
		//List<String> defaultHpl = g2LotServ.hplList();
		//List<String> defaultHpl = gmServ.defaultGrpList(org);

		if (init) {
			// check if user is not super user, do filtering
			if (!isSuperUser) {
				model.put("searchHplItems",
						gmServ.defaultGrpList().stream().filter(arg0 -> arg0.equals(grp)).collect(Collectors.toList()));
			} else {
				// if user is super user, remove filter
				model.put("searchHplItems", gmServ.defaultGrpList());
			}

		} else {
			// check if user is not super user, do filtering
			if (!isSuperUser) {
				model.put("hplItems", gmServ.defaultGrpList(org).stream().filter(arg0 -> arg0.equals(grp)).collect(Collectors.toList()));
			} else {
				// if user is super user, remove filter
				model.put("hplItems", gmServ.defaultGrpList(org));
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

		List<LabelAndValueDto> remaining = getRemainingMenuItem(new ArrayList<LabelAndValueDto>(), "");
		model.put("menuItems", remaining);

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
			@RequestParam(name = "hpl", required = false) String hplId,
			HttpServletRequest request,
			BindingResult bindingResult, HttpSession session) throws Exception {

		removeAlert(model);
		String mode = screenMode;

//		dto.setProdLn(prodLn2);
		model.put("grpMenuItemItem", dto);

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
				gmServ.save(hplId, selected, new ArrayList<String>());

				// Print add success
				model.put("success", "Record added successfully.");
				model.remove("error");
				model.put("grpMenuItemItem", new GrpMenuItemDto());

				trxHistServ.addTrxHistory(new Date(), "Insert " + MODULE_NAME, request.getRemoteUser(),
						CommonConstants.FUNCTION_TYPE_INSERT, hplId, CommonConstants.RECORD_TYPE_ID_GRP_MENU_ITEM,
						null);

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
			String hpl = (String) model.get("hpl");

			try {
				// Update form
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
						CommonConstants.FUNCTION_TYPE_UPDATE,(String) model.get("hpl"),
						CommonConstants.RECORD_TYPE_ID_GRP_MENU_ITEM, null);

			} catch (Exception e) {
				// Print DB exception
				e.printStackTrace();
				model.put("error", "Record failed to update.");
				model.remove("success");
			}
		}

		model.put("btnEdit", false);
		//model.put("searchHplItems", g2LotServ.hplList());
		filter(true);

		return new ModelAndView("/base/admin/grpMenuItemList", model);
	}

	@PostMapping(value = "/base/admin/grpMenuItemFormSave", params = "action=edit")
	public ModelAndView editForm(HttpServletRequest request, HttpSession session) {

		screenMode = CommonConstants.SCREEN_MODE_EDIT;
		model.put("mode", screenMode);
		model.put("btnEdit", true);
		model.put("btnSaveSts", false);
		model.put("btnSave", "Save");
		model.put("header", "Edit " + MODULE_NAME);
		model.remove("success");
		model.remove("error");

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
	public ModelAndView grpMenuItemFormDel(HttpServletRequest request,
			@RequestParam(value = "tableRow") String[] check, HttpSession session) {

		model.remove("error");
		model.remove("success");
		try {
			// Delete form
			for (int i = 0; i < check.length; i++) {
				if (checkDelete()) {
					// Print delete failed
					model.put("error", MODULE_NAME+" is in use and it is not allowed to be deleted from the system.");
				} else {
					gmServ.deleteGrpMenuItem(check[i], 0);
					// Print delete success`
					model.put("success", msgSource.getMessage("msgSuccessDelete", new Object[] {}, Locale.getDefault()));
	
					trxHistServ.addTrxHistory(new Date(), "Delete " + MODULE_NAME, request.getRemoteUser(),
							CommonConstants.FUNCTION_TYPE_DELETE, check[i], CommonConstants.RECORD_TYPE_ID_GRP_MENU_ITEM, null);
				}
			}

		} catch (Exception e) {
			// Print DB exception
			model.put("error", msgSource.getMessage("msgFailDelete", new Object[] {}, Locale.getDefault()));
			e.printStackTrace();
		}

		model.put("searchHplItems", g2LotServ.hplList());

		return new ModelAndView(("/base/admin/grpMenuItemList"), model);
	}

	@PreDestroy
	public void destroy() throws IOException {
		
	}
	
	private boolean checkDelete() {
		boolean isSuperUser = (Boolean) model.get("isSuperUser");
		boolean cannotDelete = isSuperUser ? false : true;
		return cannotDelete;
	}
	
	private Map<String, Object> removeAlert(Map<String, Object> model) {
		model.remove("error");
		model.remove("success");

		return model;
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
		List<Integer> selIds = selected.stream().map(LabelAndValueDto :: getValue).collect(Collectors.toList());
		List<LabelAndValueDto> remaining = gmServ.searchMenuItems().stream()
				.filter(arg0 -> !selIds.contains(arg0.getValue()))
				.collect(Collectors.toList());
		return remaining;
	}
	
	private void getAllLables() {
		MODULE_NAME = msgSource.getMessage("moduleGrpMenuItem", null, Locale.getDefault());
	}
	
}
