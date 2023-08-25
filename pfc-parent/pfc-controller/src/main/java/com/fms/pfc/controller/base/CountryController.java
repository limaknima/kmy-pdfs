package com.fms.pfc.controller.base;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

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
import com.fms.pfc.domain.dto.CountryDto;
import com.fms.pfc.service.api.base.CountryService;
import com.fms.pfc.service.api.base.TrxHisService;
import com.fms.pfc.validation.common.CommonValidation;

@Controller
@SessionScope
public class CountryController {

	private final static Logger logger = LoggerFactory.getLogger(CountryController.class);

	private CountryService countryServ;
	private Authority auth;
	private CommonValidation commonValServ;
	private TrxHisService trxHistServ;
	private MessageSource msgSource;

	private final String CON_CODE_TXT = "Country code";
	private final String CON_NAME_TXT = "Country name";
	
	private String screenMode = "";
	private Map<String, Object> model = new HashMap<String, Object>();

	@Autowired
	public CountryController(CountryService countryServ, Authority auth, CommonValidation commonValServ,
			TrxHisService trxHistServ, MessageSource msgSource) {
		super();
		this.countryServ = countryServ;
		this.auth = auth;
		this.commonValServ = commonValServ;
		this.trxHistServ = trxHistServ;
		this.msgSource = msgSource;
	}

	@GetMapping("/base/reference/countryList")
	public ModelAndView getfCountryList(HttpServletRequest request, HttpServletResponse response) {
		
		model = auth.onPageLoad(model, request);
		auth.isAuthUrl(request, response);
		model.put("countryItem", countryServ.findAll());

		return new ModelAndView("/base/reference/countryList", model);
	}

	// Display search results
	@PostMapping("/base/reference/countryFormSearch")
	public ModelAndView getSearchResult(HttpServletRequest request, @RequestParam(name = "countryId") String conID,
			@RequestParam(name = "countryName") String conName, @RequestParam(name = "para1") String para1,
			@RequestParam(name = "para2") String para2, @RequestParam(value = "evalCbox") List<String> evalCbox,
			@RequestParam(value = "cooCbox") List<String> cooCbox, HttpSession session) {

		model.remove("error");
		model.remove("success");
		String errorMsg = "";
		String eval = "";
		String coo = "";

		if (evalCbox.contains(String.valueOf(CommonConstants.FLAG_NO))) {
			eval = String.valueOf(CommonConstants.FLAG_YES);
			model.put("evalCbox", evalCbox);
		} else {
			model.remove("evalCbox");
		}

		if (cooCbox.contains(String.valueOf(CommonConstants.FLAG_NO))) {
			coo = String.valueOf(CommonConstants.FLAG_YES);
			model.put("cooCbox", cooCbox);
		} else {
			model.remove("cooCbox");
		}

		try {
			errorMsg += commonValServ.validateInputLength(conID, 3, CON_CODE_TXT);
			errorMsg += commonValServ.validateInputLength(conName, 200, CON_NAME_TXT);

			if (errorMsg.length() == 0) {
				model.put("countryItem",
						countryServ.findByCriteria(conID.trim(), conName.trim(), para1, para2, eval, coo));
				
				StringBuffer sb = new StringBuffer();
				sb.append("Search Criteria: ");
				sb.append("Country ID=").append(conID.isEmpty() ? "<ALL>" : conID).append(", ");
				sb.append("Country Name=").append(conName.isEmpty() ? "<ALL>" : conName);

				trxHistServ.addTrxHistory(new Date(), "Search Country", request.getRemoteUser(),
						CommonConstants.FUNCTION_TYPE_SEARCH, "Search Country",
						CommonConstants.RECORD_TYPE_ID_REF_COUNTRY, sb.toString());
				
			} else {
				model.put("error", errorMsg);
				model.put("countryItem", countryServ.findAll());
			}

		} catch (Exception e) {
			e.printStackTrace();
			model.put("error", "Failed to get record.");
			model.put("countryItem", countryServ.findAll());
		}

		model.put("countryId", conID);
		model.put("countryName", conName);
		model.put("para1", para1);
		model.put("para2", para2);
		model.put("evalFlagChecked", (eval.equals(String.valueOf(CommonConstants.FLAG_YES)) ? true : false));
		model.put("cooFlagChecked", (coo.equals(String.valueOf(CommonConstants.FLAG_YES)) ? true : false));

		return new ModelAndView("/base/reference/countryList", model);
	}

	// View form
	@RequestMapping("/base/reference/countryFormView")
	public ModelAndView getCountryView(HttpServletRequest request, @RequestParam(name = "countryId") String conID) {

		model.remove("error");
		model.remove("success");
		model.put("evalFlagChecked", false);
		model.put("cooFlagChecked", false);		
		
		screenMode = CommonConstants.SCREEN_MODE_EDIT;
		model.put("mode", screenMode);
		
		CountryDto con = countryServ.findOneById(conID);

		if (!CommonConstants.SCREEN_MODE_EDIT.equals(screenMode)) {
			trxHistServ.addTrxHistory(new Date(), "View Country", request.getRemoteUser(),
					CommonConstants.FUNCTION_TYPE_VIEW, conID,
					CommonConstants.RECORD_TYPE_ID_REF_COUNTRY, null);
		}

		model.put("header", "View Country");
		model.put("confirmHeader", "Edit cancellation");
		model.put("confirmMsg", "Do you want to cancel edit this record ?");
		model.put("evalFlagChecked", (con.getEvalFlag() == null ? false
				: con.getEvalFlag().equals(String.valueOf(CommonConstants.FLAG_YES)) ? true : false));
		model.put("cooFlagChecked", (con.getOriginFlag() == null ? false
				: con.getOriginFlag().equals(String.valueOf(CommonConstants.FLAG_YES)) ? true : false));

		model.put("creator", con.getCreator());
		model.put("modifier", con.getModifier());

		DateFormat formatter = new SimpleDateFormat("dd/MM/yyy hh:mm:ss a");

		try {
			String createdInfo = "Created by " + con.getCreator() + " on " + formatter.format(con.getCreateTime());
			model.put("createdInfo", createdInfo);
		} catch (Exception e) {
		}

		String modifiedInfo = "";
		try {
			modifiedInfo = "Modified by " + con.getModifier() + " on " + formatter.format(con.getModifyTime());
		} catch (Exception e) {
			modifiedInfo = "Modified by --";
		}

		model.put("modifiedInfo", modifiedInfo);
		model.put("countryItem", con);

		return new ModelAndView("/base/reference/countryForm", model);
	}

	// Load add form
	@GetMapping("/base/reference/countryForm")
	public ModelAndView addCountry(HttpServletRequest request) {

		// Set mode
		screenMode = CommonConstants.SCREEN_MODE_ADD;
		model.put("mode", screenMode);

		// Set fields
		model.put("countryItem", new CountryDto());
		// Set form header
		model.put("header", "Add Country");
		// Set confirmation message
		model.put("confirmHeader", "Cancel confirmation");
		model.put("confirmMsg", "Do you want to cancel this record ?");

		model.put("evalFlag", String.valueOf(CommonConstants.FLAG_NO));
		model.put("cooFlag", String.valueOf(CommonConstants.FLAG_NO));

		model.put("evalFlagChecked", false);
		model.put("cooFlagChecked", false);

		return new ModelAndView("/base/reference/countryForm", model);
	}

	// Save add form
	@PostMapping("/base/reference/countryForm")
	public ModelAndView saveCountry(@Valid CountryDto con, BindingResult bindingResult, String[] check,
			HttpServletRequest request, HttpSession session, @RequestParam(name = "evalFlag") String evalFlag,
			@RequestParam(name = "cooFlag") String cooFlag) {

		String mode = screenMode;

		model.remove("error");
		model.remove("success");
		String errorMsg = "";

		//logger.debug("saveCountry() evalFlag={},cooFlag={}", evalFlag, cooFlag);

		if (mode.equals(CommonConstants.SCREEN_MODE_ADD)) {
			try {
				errorMsg += commonValServ.validateUniqueCountryId(con.getCountryId(), CON_CODE_TXT);
				errorMsg += commonValServ.validateMandatoryInput(con.getCountryId(), CON_CODE_TXT);
				errorMsg += commonValServ.validateMandatoryInput(con.getCountryName(), CON_NAME_TXT);
				errorMsg += commonValServ.validateInputLength(con.getCountryId(), 3, CON_CODE_TXT);
				errorMsg += commonValServ.validateInputLength(con.getCountryName(), 200, CON_NAME_TXT);

				if (errorMsg.length() == 0) {
					countryServ.saveCountry(con.getCountryId(), con.getCountryName(), request.getRemoteUser(), evalFlag,
							cooFlag);

					model.put("success", msgSource.getMessage("msgSuccessAdd", new Object[] {}, Locale.getDefault()));
					model.remove("error");

					trxHistServ.addTrxHistory(new Date(), "Insert Country", request.getRemoteUser(),
							CommonConstants.FUNCTION_TYPE_INSERT, con.getCountryId(),
							CommonConstants.RECORD_TYPE_ID_REF_COUNTRY, null);

				} else {
					model.put("error", errorMsg);
					model.put("countryItem", con);

					return new ModelAndView("/base/reference/countryForm", model);
				}

			} catch (Exception e) {
				model.put("error", msgSource.getMessage("msgFailAdd", new Object[] {}, Locale.getDefault()));
				model.put("countryItem", con);
			}

		} else if (mode.equals(CommonConstants.SCREEN_MODE_EDIT)) {

			try {
				errorMsg += commonValServ.validateMandatoryInput(con.getCountryId(), CON_CODE_TXT);
				errorMsg += commonValServ.validateMandatoryInput(con.getCountryName(), CON_NAME_TXT);
				errorMsg += commonValServ.validateInputLength(con.getCountryId(), 3, CON_CODE_TXT);
				errorMsg += commonValServ.validateInputLength(con.getCountryName(), 200, CON_NAME_TXT);

				if (errorMsg.length() == 0) {
					countryServ.saveCountry(con.getCountryId(), con.getCountryName(), request.getRemoteUser(), evalFlag,
							cooFlag);
					model.put("success",
							msgSource.getMessage("msgSuccessUpdate", new Object[] {}, Locale.getDefault()));

					trxHistServ.addTrxHistory(new Date(), "Update Country", request.getRemoteUser(),
							CommonConstants.FUNCTION_TYPE_UPDATE, con.getCountryId(),
							CommonConstants.RECORD_TYPE_ID_REF_COUNTRY, null);

				} else {
					model.put("error", errorMsg);
					model.put("countryItem", con);

					return new ModelAndView("/base/reference/countryForm", model);
				}

			} catch (Exception e) {
				model.put("error", msgSource.getMessage("msgFailUpdate", new Object[] {}, Locale.getDefault()));
				model.put("countryItem", con);

				return new ModelAndView("/base/reference/countryForm", model);
			}
		}

		model.put("countryItem", countryServ.findAll());

		return new ModelAndView("/base/reference/countryList", model);
	}

	@RequestMapping("/base/reference/countryFormDel")
	public ModelAndView deleteCountryBatch(HttpServletRequest request, @RequestParam(value = "tableRow") String[] check,
			HttpSession session) {

		model.remove("error");
		model.remove("success");
		try {
			// Delete form
			for (int i = 0; i < check.length; i++) {

				if (checkCountryCodeInUsed(check[i])) {
					countryServ.deleteCountry(check[i]);
					// Print delete success
					model.put("success",
							msgSource.getMessage("msgSuccessDelete", new Object[] {}, Locale.getDefault()));

					trxHistServ.addTrxHistory(new Date(), "Delete Country", request.getRemoteUser(),
							CommonConstants.FUNCTION_TYPE_DELETE, check[i], CommonConstants.RECORD_TYPE_ID_REF_COUNTRY,
							null);
				} else {
					// Print delete failed
					model.put("error", "Country code is in used and it is not allow to delete from the system.");
				}
			}

		} catch (Exception e) {
			// Print DB exception
			model.put("error", msgSource.getMessage("msgFailDelete", new Object[] {}, Locale.getDefault()));
		}

		model.put("countryItem", countryServ.findAll());

		return new ModelAndView(("/base/reference/countryList"), model);
	}

	// FSGS) Kent 26/2/2021 Add country code usage check START
	private Boolean checkCountryCodeInUsed(String countryCd) {
		String result = "";
		result = countryServ.vlidateCountry(countryCd);

		// check return result if not equals to 0 means in use
		if (result != null && !result.isEmpty()) {
			if ("0".equals(result)) {
				return true;
			}
		}

		return false;
	}
	// FSGS) Kent 26/2/2021 Add country code usage check END
}
