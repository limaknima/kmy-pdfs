package com.fms.pfc.controller.main;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

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
import com.fms.pfc.domain.dto.main.LetterContConfDto;
import com.fms.pfc.domain.dto.main.LetterTypeDto;
import com.fms.pfc.service.api.base.TrxHisService;
import com.fms.pfc.service.api.main.LetterContConfService;
import com.fms.pfc.service.api.main.LetterTypeService;
import com.fms.pfc.validation.common.CommonValidation;

@Controller
@SessionScope
public class LetterTypeController {

	private static final Logger logger = LoggerFactory.getLogger(LetterTypeController.class);

	private Authority auth;
	private CommonValidation commonValServ;
	private TrxHisService trxHistServ;
	private LetterTypeService ltServ;
	private MessageSource msgSource;
	private LetterContConfService ltContServ;

	private String screenMode = "";
	private Map<String, Object> model = new HashMap<String, Object>();

	private static final String LT_NAME_TXT = "Letter Type";
	private static final String LT_DESC_TXT = "Description";

	private static final String ERR_MSG_UNIQUE = " already exist ";
	private static final String breakline = ".<br />";

	@Autowired
	public LetterTypeController(Authority auth, CommonValidation commonValServ, TrxHisService trxHistServ,
			LetterTypeService ltServ, MessageSource msgSource, LetterContConfService ltContServ) {
		super();
		this.auth = auth;
		this.commonValServ = commonValServ;
		this.trxHistServ = trxHistServ;
		this.ltServ = ltServ;
		this.msgSource = msgSource;
		this.ltContServ = ltContServ;
	}

	/**
	 * Display initial screen
	 * 
	 * @param request
	 * @param response
	 * @return
	 */
	@GetMapping("/base/maint/letterTypeList")
	public ModelAndView searchListing(HttpServletRequest request, HttpServletResponse response) {

		removeAlert(model);
		model = auth.onPageLoad(model, request);
		auth.isAuthUrl(request, response);
		model.put("ltAllItems", ltServ.findAll());

		return new ModelAndView("/base/maint/letterTypeList", model);
	}

	/**
	 * View form
	 * 
	 * @param request
	 * @param ltId
	 * @return
	 */
	@GetMapping("/base/maint/letterTypeFormView")
	public ModelAndView viewForm(HttpServletRequest request, @RequestParam(name = "ltId") Integer ltId) {

		removeAlert(model);

		LetterTypeDto ltDto = ltServ.findOneById(ltId);
		trxHistServ.addTrxHistory(new Date(), "View Letter Type", request.getRemoteUser(),
				CommonConstants.FUNCTION_TYPE_VIEW, ltDto.getLtName(), CommonConstants.RECORD_TYPE_ID_LETTER_TYPE,
				null);

		// Set mode
		screenMode = CommonConstants.SCREEN_MODE_VIEW;
		model.put("mode", screenMode);

		// Set form header
		model.put("header", "View Letter Type");

		// Set confirmation message
		model.put("confirmHeader", "Edit cancellation");
		model.put("confirmMsg", "Do you want to cancel Edit this record ?");

		// Put model to HTML
		if (ltDto != null) {

			model.put("ltId", ltDto.getLtId());
			model.put("letterTypeItem", ltDto);
			model.put("letterTypeCurrItem", ltDto);
			model.put("createdUser", ltDto.getCreatorId());
			model.put("modifiedUser", ltDto.getModifierId());

			DateFormat formatter = new SimpleDateFormat("dd/MM/yyy hh:mm:ss a");
			try {
				String createdInfo = "Created by " + ltDto.getCreatorId() + " on "
						+ formatter.format(ltDto.getCreatedDate());
				model.put("createdInfo", createdInfo);
			} catch (Exception e) {
			}

			String modifiedInfo = "";
			try {
				modifiedInfo = "Modified by " + ltDto.getModifierId() + " on "
						+ formatter.format(ltDto.getModifiedDatetime());
			} catch (Exception e) {
				modifiedInfo = "Modified by --";
			}
			model.put("modifiedInfo", modifiedInfo);
		}

		return new ModelAndView("/base/maint/letterTypeForm", model);
	}

	/**
	 * Load add form
	 * 
	 * @param request
	 * @return
	 */
	@GetMapping("/base/maint/letterTypeForm")
	public ModelAndView loadNewForm(HttpServletRequest request) {
		removeAlert(model);

		// Set mode
		screenMode = CommonConstants.SCREEN_MODE_ADD;
		model.put("mode", screenMode);

		// Set fields
		model.put("letterTypeItem", new LetterTypeDto());
		// Set form header
		model.put("header", "Add Letter Type");
		// Set confirmation message
		model.put("confirmHeader", "Cancel confirmation");
		model.put("confirmMsg", "Do you want to cancel this record ?");

		return new ModelAndView("/base/maint/letterTypeForm", model);
	}

	/**
	 * Save Form
	 * 
	 * @param ltDto
	 * @param request
	 * @param bindingResult
	 * @param session
	 * @return
	 * @throws Exception
	 */
	@PostMapping("/base/maint/letterTypeFormSave")
	public ModelAndView saveLetterType(@Valid LetterTypeDto ltDto, HttpServletRequest request,
			BindingResult bindingResult, HttpSession session) throws Exception {

		removeAlert(model);
		String mode = screenMode;
		model.put("letterTypeItem", ltDto);
		String errorMsg = validateForm(ltDto);

		logger.debug("saveLetterType mode=" + mode);

		if (errorMsg.length() != 0) {
			model.put("error", errorMsg);
			model.put("letterTypeItem", ltDto);

			if (mode.equals(CommonConstants.SCREEN_MODE_EDIT)) {
				screenMode = CommonConstants.SCREEN_MODE_EDIT;
				// Set mode to HTML header
				model.put("mode", screenMode);
				model.put("header", "Edit Letter Type");
				// Set confirmation message
				model.put("confirmHeader", "Edit cancellation");
				model.put("confirmMsg", "Do you want to cancel edit this record ?");
			}
			return new ModelAndView("/base/maint/letterTypeForm", model);
		}

		if (mode.equals(CommonConstants.SCREEN_MODE_ADD)) {

			try {
				// If no error proceed to save add form
				// Add form
				ltServ.saveLetterType(null, ltDto.getLtName(), ltDto.getLtDesc(), request.getRemoteUser());

				// Print add success
				model.put("success", "Record added successfully.");
				model.put("letterTypeItem", new LetterTypeDto());

				trxHistServ.addTrxHistory(new Date(), "Insert Letter Type", request.getRemoteUser(),
						CommonConstants.FUNCTION_TYPE_INSERT, ltDto.getLtName(),
						CommonConstants.RECORD_TYPE_ID_LETTER_TYPE, null);

			} catch (Exception e) {
				// Print DB exception
				e.printStackTrace();
				model.put("error", "Record failed to be added.");
			}

		} else if (mode.equals(CommonConstants.SCREEN_MODE_VIEW)) {
			// Set mode to HTML header
			screenMode = CommonConstants.SCREEN_MODE_VIEW;
			model.put("mode", screenMode);
			model.put("header", "Letter Type");

			try {
				// Update form
				ltServ.saveLetterType((int) (model.get("ltId")), ltDto.getLtName(), ltDto.getLtDesc(),
						request.getRemoteUser());

				// Print update success
				model.put("success", "Record updated successfully.");

				trxHistServ.addTrxHistory(new Date(), "Update Letter Type", request.getRemoteUser(),
						CommonConstants.FUNCTION_TYPE_UPDATE, ltDto.getLtName(),
						CommonConstants.RECORD_TYPE_ID_LETTER_TYPE, null);

			} catch (Exception e) {
				// Print DB exception
				e.printStackTrace();
				model.put("error", "Record failed to update.");
			}
		}

		model.put("ltAllItems", ltServ.findAll());

		return new ModelAndView("/base/maint/letterTypeList", model);
	}

	/**
	 * Do search
	 * 
	 * @param request
	 * @param ltName
	 * @param ltDesc
	 * @param ltNameExp
	 * @param ltDescExp
	 * @param session
	 * @return
	 */
	@PostMapping("/base/maint/letterTypeSearch")
	public ModelAndView doSearch(HttpServletRequest request, @RequestParam(name = "ltName") String ltName,
			@RequestParam(name = "ltDesc") String ltDesc, @RequestParam(name = "ltNameExp") String ltNameExp,
			@RequestParam(name = "ltDescExp") String ltDescExp, HttpSession session) {

		boolean hasError = false;

		removeAlert(model);
		String errorMsg = "";

		model.put("ltName", ltName);
		model.put("ltDesc", ltDesc);
		model.put("ltNameExp", ltNameExp);
		model.put("ltDescExp", ltDescExp);

		try {
			if (errorMsg.length() == 0) {

				List<LetterTypeDto> items = ltServ.searchByCriteria(ltName, ltDesc, ltNameExp, ltDescExp);

				model.put("ltAllItems", items);

				StringBuffer sb = new StringBuffer();
				sb.append("Search Criteria: ");
				sb.append("Letter Type=").append(ltName.isEmpty() ? "<ALL>" : ltName).append(", ");
				sb.append("Description=").append(ltDesc.isEmpty() ? "<ALL>" : ltDesc);

				trxHistServ.addTrxHistory(new Date(), "Search Letter Type", request.getRemoteUser(),
						CommonConstants.FUNCTION_TYPE_SEARCH, "Search Letter Type",
						CommonConstants.RECORD_TYPE_ID_LETTER_TYPE, sb.toString());

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
				model.put("ltAllItems", ltServ.findAll());
			}
		}

		return new ModelAndView("/base/maint/letterTypeList", model);
	}

	private Map<String, Object> removeAlert(Map<String, Object> model) {
		model.remove("error");
		model.remove("success");

		return model;
	}

	/**
	 * Name must be unique
	 * 
	 * @param ltName
	 * @return
	 */
	private String validateDuplicateFlavStatus(String ltName) {
		LetterTypeDto fsItem = ltServ.findByName(ltName);

		// If flav status existed return true
		if (null != fsItem) {

			logger.debug("validateDuplicateFlavStatus " + fsItem.getLtName());

			return LT_NAME_TXT + ERR_MSG_UNIQUE + ltName + breakline;
		}

		return "";
	}

	/**
	 * Construct error message based on validation result
	 * 
	 * @param ltDto
	 * @return
	 */
	private String validateForm(LetterTypeDto ltDto) {
		String errorMsg = "";
		errorMsg += commonValServ.validateMandatoryInput(ltDto.getLtName(), LT_NAME_TXT);
		errorMsg += commonValServ.validateInputLength(ltDto.getLtName(), 100, LT_NAME_TXT);
		errorMsg += commonValServ.validateInputLength(ltDto.getLtDesc(), 200, LT_DESC_TXT);

		if (screenMode == CommonConstants.SCREEN_MODE_ADD) {
			errorMsg += validateDuplicateFlavStatus(ltDto.getLtName());
		} else {

			if (null != model.get("letterTypeCurrItem")) {
				LetterTypeDto currDto = (LetterTypeDto) model.get("letterTypeCurrItem");

				logger.debug("validateForm cur name=" + currDto.getLtName() + "; new name=" + ltDto.getLtName());
				if (!StringUtils.equalsIgnoreCase(currDto.getLtName(), ltDto.getLtName())) {
					// check if current name value modified
					errorMsg += validateDuplicateFlavStatus(ltDto.getLtName());

				}
			}
		}

		return errorMsg;
	}
	
	@RequestMapping("/base/maint/letterTypeDelete")
	public ModelAndView deleteLetterBatch(HttpServletRequest request, @RequestParam(value = "tableRow") String[] check,
			HttpSession session) {

		model.remove("error");
		model.remove("success");
		try {
			// Delete form
			for (int i = 0; i < check.length; i++) {

				if (checkLetterTypeInUsed(check[i])) {
					// Print delete failed
					model.put("error", "Letter Type is in used and it is not allow to delete from the system.");
				} else {
					ltServ.deleteById(Integer.parseInt(check[i]));
					// Print delete success`
					model.put("success",
							msgSource.getMessage("msgSuccessDelete", new Object[] {}, Locale.getDefault()));

					trxHistServ.addTrxHistory(new Date(), "Delete Letter Type", request.getRemoteUser(),
							CommonConstants.FUNCTION_TYPE_DELETE, check[i], CommonConstants.RECORD_TYPE_ID_LETTER_TYPE,
							null);
				}
			}

		} catch (Exception e) {
			// Print DB exception
			model.put("error", msgSource.getMessage("msgFailDelete", new Object[] {}, Locale.getDefault()));
			e.printStackTrace();
		}

		model.put("ltAllItems", ltServ.findAll());

		return new ModelAndView(("/base/maint/letterTypeList"), model);
	}

	private boolean checkLetterTypeInUsed(String id) {
		boolean inUse = false;
		LetterContConfDto dto = ltContServ.findByLetterType(Integer.parseInt(id));
		if (!Objects.isNull(dto)) {
			inUse = true;
		}
		return inUse;
	}

}
