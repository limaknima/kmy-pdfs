package com.fms.pfc.controller.main;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
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
import com.fms.pfc.domain.model.main.ProductRecipe;
import com.fms.pfc.service.api.base.TrxHisService;
import com.fms.pfc.service.api.main.LetterContConfPRService;
import com.fms.pfc.service.api.main.LetterContConfService;
import com.fms.pfc.validation.common.CommonValidation;

@Controller
@SessionScope
public class LetterContConfController {
	private static final Logger logger = LoggerFactory.getLogger(LetterContConfController.class);

	private Authority auth;
	private CommonValidation commonValServ;
	private TrxHisService trxHistServ;
	private LetterContConfService ltServ;
	private MessageSource msgSource;

	private String screenMode = "";
	private Map<String, Object> model = new HashMap<String, Object>();

	private static final String LT_TXT = "Letter Type";
	private static final String SEC1_TXT = "Section 1";
	private static final String SEC2_TXT = "Section 2";
	private static final String SEC3_TXT = "Section 3";
	private static final String PROD_TXT = "Product Name";
	private static final String COUNTRY_TXT = "Country";	
	private static final String GENERIC_TYPE = "Generic"; 
	private static final String ALL = "ALL"; 
	private static final int DESC_MAX_LENGTH = 1000;

	@Autowired
	public LetterContConfController(Authority auth, CommonValidation commonValServ, TrxHisService trxHistServ,
			LetterContConfService ltServ, MessageSource msgSource) {
		super();
		this.auth = auth;
		this.commonValServ = commonValServ;
		this.trxHistServ = trxHistServ;
		this.ltServ = ltServ;
		this.msgSource = msgSource;
	}

	/**
	 * Display initial screen
	 * 
	 * @param request
	 * @param response
	 * @return
	 */
	@GetMapping("/main/product/letterContentList")
	public ModelAndView searchListing(HttpServletRequest request, HttpServletResponse response) {

		removeAlert(model);
		model = auth.onPageLoad(model, request);
		auth.isAuthUrl(request, response);
		model.put("ltAllItems", ltServ.findAll());
		model.put("letterTypeItems", ltServ.getLetterTypeList());

		return new ModelAndView("/main/product/letterContentList", model);
	}

	/**
	 * View form
	 * 
	 * @param request
	 * @param ltContId
	 * @return
	 */
	@RequestMapping("/main/product/letterContentFormView")
	public ModelAndView viewForm(HttpServletRequest request, @RequestParam(name = "ltContId") Integer ltContId) {

		removeAlert(model);

		LetterContConfDto ltDto = ltServ.findOneById(ltContId);
		trxHistServ.addTrxHistory(new Date(), "View Letter Config", request.getRemoteUser(),
				CommonConstants.FUNCTION_TYPE_VIEW, ltDto.getLtId().toString(),
				CommonConstants.RECORD_TYPE_ID_LETTER_CONF, null);

		// Set mode
		screenMode = CommonConstants.SCREEN_MODE_VIEW;
		model.put("mode", screenMode);

		// Set form header
		model.put("header", "View Letter Config");

		// Set confirmation message
		model.put("confirmHeader", "Edit cancellation");
		model.put("confirmMsg", "Do you want to cancel Edit this record ?");

		model.put("letterTypeItems", ltServ.getLetterTypeList());
		model.put("refCountryItems", ltServ.getRefCountryList());

		// Put model to HTML
		if (ltDto != null) {

			model.put("ltContId", ltDto.getLtConfId());
			model.put("letterContItem", ltDto);
			model.put("letterContCurrItem", ltDto);
			model.put("createdUser", ltDto.getCreatorId());
			model.put("modifiedUser", ltDto.getModifierId());

			List<ProductRecipe> selected = getSelectedPr(ltContId);
			List<ProductRecipe> remaining = getRemainingPr(selected);

			model.put("selPrItems", selected);
			model.put("prItems", remaining);

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

		return new ModelAndView("/main/product/letterContentForm", model);
	}	

	/**
	 * Load add form
	 * 
	 * @param request
	 * @return
	 */
	@GetMapping("/main/product/letterContentForm")
	public ModelAndView loadNewForm(HttpServletRequest request) {
		removeAlert(model);

		// Set mode
		screenMode = CommonConstants.SCREEN_MODE_ADD;
		model.put("mode", screenMode);

		// Set fields
		model.put("letterContItem", new LetterContConfDto());
		model.put("letterTypeItems", ltServ.getLetterTypeList());
		model.put("refCountryItems", ltServ.getRefCountryList());
		model.put("prItems", ltServ.getPrList());

		// Set form header
		model.put("header", "Add Letter Type");
		// Set confirmation message
		model.put("confirmHeader", "Cancel confirmation");
		model.put("confirmMsg", "Do you want to cancel this record ?");

		return new ModelAndView("/main/product/letterContentForm", model);
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
	@PostMapping("/main/product/letterContentFormSave")
	public ModelAndView saveLetterContent(@Valid LetterContConfDto ltDto,
			@RequestParam(value = "prChoice1", required = false) String[] prChoiceList, HttpServletRequest request,
			BindingResult bindingResult, HttpSession session) throws Exception {

		removeAlert(model);
		String mode = screenMode;
		
		//set lt
		ltDto.setLetterTypeName(ltServ.findLetterType(ltDto.getLtId()).getLtName());
		
		model.put("letterContItem", ltDto);
				
		String errorMsg = validateForm(ltDto, prChoiceList);

		if (errorMsg.length() != 0) {
			model.put("error", errorMsg);
			model.put("letterContItem", ltDto);

			if (mode.equals(CommonConstants.SCREEN_MODE_EDIT)) {
				screenMode = CommonConstants.SCREEN_MODE_EDIT;
				// Set mode to HTML header
				model.put("mode", screenMode);
				model.put("header", "Edit Letter Content");
				// Set confirmation message
				model.put("confirmHeader", "Edit cancellation");
				model.put("confirmMsg", "Do you want to cancel edit this record ?");
			}
			return new ModelAndView("/main/product/letterContentForm", model);
		}
		
		List<Integer> prIds = new ArrayList<Integer>();
		if (Objects.nonNull(prChoiceList) && prChoiceList.length != 0) {
			prIds = Arrays.asList(prChoiceList).stream().map(s -> Integer.parseInt(s)).collect(Collectors.toList());
		}

		if (mode.equals(CommonConstants.SCREEN_MODE_ADD)) {

			try {
				// If no error proceed to save add form
				// Add form
				ltServ.saveLetterConf(null, ltDto.getLtId(), ltDto.getSec1Desc(), ltDto.getSec2Desc(),
						ltDto.getSec3Desc(), ltDto.getCountryId(), request.getRemoteUser(), prIds);

				// Print add success
				model.put("success", "Record added successfully.");
				model.put("letterContItem", new LetterContConfDto());

				trxHistServ.addTrxHistory(new Date(), "Insert Letter Content", request.getRemoteUser(),
						CommonConstants.FUNCTION_TYPE_INSERT, ltDto.getLtId().toString(),
						CommonConstants.RECORD_TYPE_ID_LETTER_CONF, null);

			} catch (Exception e) {
				// Print DB exception
				e.printStackTrace();
				model.put("error", "Record failed to be added.");
			}

		} else if (mode.equals(CommonConstants.SCREEN_MODE_VIEW)) {
			// Set mode to HTML header
			screenMode = CommonConstants.SCREEN_MODE_VIEW;
			model.put("mode", screenMode);
			model.put("header", "Letter Content");

			try {
				// Update form
				ltServ.saveLetterConf((int) model.get("ltContId"), ltDto.getLtId(), ltDto.getSec1Desc(),
						ltDto.getSec2Desc(), ltDto.getSec3Desc(), ltDto.getCountryId(), request.getRemoteUser(),
						prIds);

				// Print update success
				model.put("success", "Record updated successfully.");

				trxHistServ.addTrxHistory(new Date(), "Update Letter Content", request.getRemoteUser(),
						CommonConstants.FUNCTION_TYPE_UPDATE, ltDto.getLtId().toString(),
						CommonConstants.RECORD_TYPE_ID_LETTER_CONF, null);

			} catch (Exception e) {
				// Print DB exception
				e.printStackTrace();
				model.put("error", "Record failed to update.");
			}
		}

		model.put("ltAllItems", ltServ.findAll());

		return new ModelAndView("/main/product/letterContentList", model);
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
	@PostMapping("/main/product/letterContentSearch")
	public ModelAndView doSearch(HttpServletRequest request, @RequestParam(name = "prName") String prName,
			@RequestParam(name = "prNameExp") String prNameExp, @RequestParam(name = "ltType") Integer ltType,
			HttpSession session) {

		boolean hasError = false;

		removeAlert(model);
		String errorMsg = "";

		model.put("prName", prName);
		model.put("prNameExp", prNameExp);
		model.put("ltType", ltType);

		try {
			if (errorMsg.length() == 0) {

				List<LetterContConfDto> items = ltServ.searchByCriteria(ltType, prName, prNameExp);

				model.put("ltAllItems", items);

				StringBuffer sb = new StringBuffer();
				sb.append("Search Criteria: ");
				sb.append("Letter Type=").append(ltType == null ? "<ALL>" : ltType).append(", ");
				sb.append("Product Name=").append(prName.isEmpty() ? "<ALL>" : prName);

				trxHistServ.addTrxHistory(new Date(), "Search Letter Content", request.getRemoteUser(),
						CommonConstants.FUNCTION_TYPE_SEARCH, "Search Letter Content",
						CommonConstants.RECORD_TYPE_ID_LETTER_CONF, sb.toString());

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

		return new ModelAndView("/main/product/letterContentList", model);
	}

	private Map<String, Object> removeAlert(Map<String, Object> model) {
		model.remove("error");
		model.remove("success");

		return model;
	}
	
	/**
	 * Get selected PR based on config id
	 * @param ltConfId
	 * @return
	 */
	private List<ProductRecipe> getSelectedPr(Integer ltConfId){		
		return ltServ.getPrSelectedList(ltConfId);
	}
	
	/**
	 * Get remaining PR. Overall PR minus selected PR
	 * @param selected
	 * @return
	 */
	protected List<ProductRecipe> getRemainingPr(List<ProductRecipe> selected) {
		List<ProductRecipe> remaining = ltServ.getPrList().stream().filter(arg0 -> !selected.contains(arg0))
				.collect(Collectors.toList());
		return remaining;
	}

	/**
	 * Construct error message based on validation result
	 * 
	 * @param ltDto
	 * @return
	 */
	private String validateForm(LetterContConfDto ltDto, String[] prChoice) {
		String errorMsg = "";
		errorMsg += commonValServ.validateMandatoryInput(ltDto.getLtId() == null ? "" : ltDto.getLtId().toString(),
				LT_TXT);
		errorMsg += commonValServ.validateMandatoryInput(ltDto.getSec1Desc(), SEC1_TXT);
		errorMsg += commonValServ.validateMandatoryInput(ltDto.getSec2Desc(), SEC2_TXT);
		errorMsg += commonValServ.validateMandatoryInput(ltDto.getSec3Desc(), SEC3_TXT);
		errorMsg += commonValServ.validateInputLength(ltDto.getSec1Desc(), DESC_MAX_LENGTH, SEC1_TXT);
		errorMsg += commonValServ.validateInputLength(ltDto.getSec2Desc(), DESC_MAX_LENGTH, SEC2_TXT);
		errorMsg += commonValServ.validateInputLength(ltDto.getSec3Desc(), DESC_MAX_LENGTH, SEC3_TXT);
		
		if(!ltDto.getLetterTypeName().equalsIgnoreCase(GENERIC_TYPE)) {
			errorMsg += commonValServ.validateMandatoryInput(ltDto.getCountryId(), COUNTRY_TXT);
			if(!Objects.nonNull(prChoice) || (Objects.nonNull(prChoice) && prChoice.length <= 0)) {
				errorMsg += commonValServ.validateMandatoryInput("", PROD_TXT);
			}			
		}
//
//		if (screenMode == CommonConstants.SCREEN_MODE_ADD) {
//			errorMsg += validateDuplicateFlavStatus(ltDto.getLtName());
//		} else {
//
//			if (null != model.get("letterTypeCurrItem")) {
//				LetterTypeDto currDto = (LetterTypeDto) model.get("letterTypeCurrItem");
//
//				logger.debug("validateForm cur name=" + currDto.getLtName() + "; new name=" + ltDto.getLtName());
//				if (!StringUtils.equalsIgnoreCase(currDto.getLtName(), ltDto.getLtName())) {
//					// check if current name value modified
//					errorMsg += validateDuplicateFlavStatus(ltDto.getLtName());
//
//				}
//			}
//		}

		return errorMsg;
	}
	
	@RequestMapping("/main/product/letterContentFormDel")
	public ModelAndView deleteLetterContBatch(HttpServletRequest request,
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
				ltServ.deleteById(Integer.parseInt(check[i]));
				// Print delete success`
				model.put("success", msgSource.getMessage("msgSuccessDelete", new Object[] {}, Locale.getDefault()));

				trxHistServ.addTrxHistory(new Date(), "Delete Content", request.getRemoteUser(),
						CommonConstants.FUNCTION_TYPE_DELETE, check[i], CommonConstants.RECORD_TYPE_ID_LETTER_CONF,
						null);
//				}
			}

		} catch (Exception e) {
			// Print DB exception
			model.put("error", msgSource.getMessage("msgFailDelete", new Object[] {}, Locale.getDefault()));
			e.printStackTrace();
		}

		model.put("ltAllItems", ltServ.findAll());

		return new ModelAndView(("/main/product/letterContentList"), model);
	}

}
