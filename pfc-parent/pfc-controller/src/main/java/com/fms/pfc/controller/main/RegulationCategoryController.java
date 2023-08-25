package com.fms.pfc.controller.main;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;

import org.apache.commons.lang.ArrayUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
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
import com.fms.pfc.domain.dto.main.RegulationCatDto;
import com.fms.pfc.domain.model.Country;
import com.fms.pfc.domain.model.main.RegulationCategory;
import com.fms.pfc.service.api.base.CountryService;
import com.fms.pfc.service.api.base.TrxHisService;
import com.fms.pfc.service.api.main.RegulationCategoryService;
import com.fms.pfc.validation.common.CommonValidation;

@Controller
@SessionScope
public class RegulationCategoryController {
	
	private static final Logger logger = LoggerFactory.getLogger(RegulationCategoryController.class);

	private RegulationCategoryService regCatServ;
	private Authority auth;
	private CommonValidation commonValServ;
	private TrxHisService trxHistServ;
	private CountryService countryServ;

	private final String CAT_NAME_TXT = "File Category";
	private final String CAT_DESC_TXT = "Description";	
	
	private String screenMode = "";
	private Map<String, Object> model = new HashMap<String, Object>();

	@Autowired
	public RegulationCategoryController(RegulationCategoryService regCatServ, Authority auth,
			CommonValidation commonValServ, TrxHisService trxHistServ, CountryService countryServ) {
		super();
		this.regCatServ = regCatServ;
		this.auth = auth;
		this.commonValServ = commonValServ;
		this.trxHistServ = trxHistServ;
		this.countryServ = countryServ;
	}

	// Display initial screen
	@GetMapping("/main/regulation/fileCategoryList")
	public ModelAndView getFileCategoryList(HttpServletRequest request,
			HttpServletResponse response) {

		removeAlert(model);
		model = auth.onPageLoad(model, request);
		auth.isAuthUrl(request, response);
		model.put("fileCategoryItems", getAllItems("", "", "", "", ""));
		model.put("countryItems", getRefCountryList());

		return new ModelAndView("/main/regulation/fileCategoryList", model);

	}

	// View form
	@RequestMapping("/main/regulation/regulationCategoryFormView")
	public ModelAndView getRegulationCategoryView(HttpServletRequest request,
			@RequestParam(name = "catId") Integer catId) {

		removeAlert(model);
		RegulationCatDto regCat = regCatServ.findOneById(catId);

		trxHistServ.addTrxHistory(new Date(), "View File Category", request.getRemoteUser(), CommonConstants.FUNCTION_TYPE_VIEW,
				regCat.getCatName(), CommonConstants.RECORD_TYPE_ID_REGL, null);

		// Set mode
		screenMode = CommonConstants.SCREEN_MODE_VIEW;
		model.put("mode", screenMode);

		// Set form header
		model.put("header", "View Regulation File Category");

		// Set confirmation message
		model.put("confirmHeader", "Edit cancellation");
		model.put("confirmMsg", "Do you want to cancel Edit this record ?");

		// Put model to HTML
		if (regCat != null) {
			
			List<Country> selCountryItems = getCategoryCountryList(regCat.getCountryList());
			List<Country> refCountryItems = getRefCountryList().stream().filter(arg -> !selCountryItems.contains(arg))
					.collect(Collectors.toList());
						
			model.put("catId", regCat.getRegDocCatId());
			model.put("fileCategoryItem", regCat);
			model.put("createdUser", regCat.getCreatorId());
			model.put("modifiedUser", regCat.getModifierId());
			model.put("selCountryItems", selCountryItems);
			model.put("refCountryItems", refCountryItems);
			
			DateFormat formatter = new SimpleDateFormat("dd/MM/yyy hh:mm:ss a");
			try {
				String createdInfo = "Created by " + regCat.getCreatorId() + " on "
						+ formatter.format(regCat.getCreatedDate());
				model.put("createdInfo", createdInfo);
			} catch (Exception e) {
			}

			String modifiedInfo = "";
			try {
				modifiedInfo = "Modified by " + regCat.getModifierId() + " on "
						+ formatter.format(regCat.getModifiedDatetime());
			} catch (Exception e) {
				modifiedInfo = "Modified by --";
			}
			model.put("modifiedInfo", modifiedInfo);
		}

		return new ModelAndView("/main/regulation/fileCategoryForm", model);
	}

	// Load add form
	@GetMapping("/main/regulation/fileCategoryForm")
	public ModelAndView addRegulationCategory(HttpServletRequest request) {
		
		removeAlert(model);
		
		// Set mode
		screenMode = CommonConstants.SCREEN_MODE_ADD;
		model.put("mode", screenMode);

		// Set fields
		model.put("fileCategoryItem", new RegulationCatDto());
		model.put("refCountryItems", getRefCountryList());
		// Set form header
		model.put("header", "Add Regulation File Category");
		// Set confirmation message
		model.put("confirmHeader", "Cancel confirmation");
		model.put("confirmMsg", "Do you want to cancel this record ?");

		return new ModelAndView("/main/regulation/fileCategoryForm", model);
	}

	// Save add form
	@PostMapping("/main/regulation/fileCategoryForm")
	public ModelAndView saveRegulationCategory(@Valid RegulationCatDto regCat, HttpServletRequest request,
			BindingResult bindingResult, @RequestParam(value = "countryChoice1") String[] countryChoiceList, HttpSession session)
			throws IOException {

		String mode = screenMode;
		String countryList = (countryChoiceList==null)?"":String.join(",", countryChoiceList);

		removeAlert(model);
		String errorMsg = "";
		model.put("fileCategoryItem", regCat);

		errorMsg += commonValServ.validateMandatoryInput(regCat.getCatName(), CAT_NAME_TXT);
		errorMsg += commonValServ.validateInputLength(regCat.getCatName(), 100, CAT_NAME_TXT);
		errorMsg += commonValServ.validateInputLength(regCat.getCatDesc(), 200, CAT_DESC_TXT);

		if (errorMsg.length() != 0) {
			model.put("error", errorMsg);
			model.put("fileCategoryItem", regCat);

			if (mode.equals(CommonConstants.SCREEN_MODE_EDIT)) {
				screenMode = CommonConstants.SCREEN_MODE_EDIT;
				// Set mode to HTML header
				model.put("mode", screenMode);
				model.put("header", "Edit Regulation File Category");
				// Set confirmation message
				model.put("confirmHeader", "Edit cancellation");
				model.put("confirmMsg", "Do you want to cancel edit this record ?");
			}
			return new ModelAndView("/main/regulation/fileCategoryForm", model);
		}

		if (mode.equals(CommonConstants.SCREEN_MODE_ADD)) {
			try {
				// If no error proceed to save add form
				if (!checkFileCategoryExist(regCat.getCatName())) {
					// Add form
					regCatServ.saveRegCat(regCat, request.getRemoteUser(), countryList);

					// Print add success
					model.put("success", "Record added successfully.");
					model.put("fileCategoryItem", new RegulationCatDto());

					trxHistServ.addTrxHistory(new Date(), "Insert File Category ", request.getRemoteUser(),
							CommonConstants.FUNCTION_TYPE_INSERT, regCat.getCatName(),
							CommonConstants.RECORD_TYPE_ID_REGL, null);

				} else {
					// Print ID existed
					model.put("error", "File Category Name already existed. Please use other File Category Name");
					return new ModelAndView("/main/regulation/fileCategoryForm", model);
				}
			} catch (Exception e) {
				// Print DB exception
				e.printStackTrace();
				model.put("error", "Record failed to be added.");
			}
		} else if (mode.equals(CommonConstants.SCREEN_MODE_VIEW)) {
			// Set mode to HTML header
			screenMode = CommonConstants.SCREEN_MODE_VIEW;
			model.put("mode", screenMode);
			model.put("header", "Regulation File Category");

			try {
				// Update form
				regCat.setRegDocCatId((int)model.get("catId"));
				regCatServ.saveRegCat(regCat, request.getRemoteUser(), countryList);

				// Print update success
				model.put("success", "Record updated successfully.");

				trxHistServ.addTrxHistory(new Date(), "Update File Category ", request.getRemoteUser(),
						CommonConstants.FUNCTION_TYPE_UPDATE, regCat.getCatName(), CommonConstants.RECORD_TYPE_ID_REGL,
						null);

			} catch (Exception e) {
				// Print DB exception
				e.printStackTrace();
				model.put("error", "Record failed to update.");
			}

		}

		model.put("fileCategoryItems", getAllItems("","","","",""));
		return new ModelAndView("/main/regulation/fileCategoryList", model);
	}

	// Check if file category exist in database
	private boolean checkFileCategoryExist(String catName) {

		boolean exist = false;
		List<RegulationCategory> catList = regCatServ.searchCategoryRegulation(catName, "", String.valueOf(CommonConstants.SEARCH_OPTION_EXACT), "","");

		// If file category existed return true
		if (catList.size() > 0) {
			exist = true;
		}

		return exist;
	}

	// Display search results
	@PostMapping("/main/regulation/fileCategoryFormSearch")
	public ModelAndView getSearchResult(HttpServletRequest request, @RequestParam(name = "catName") String catName,
			@RequestParam(name = "catDesc") String catDesc, @RequestParam(name = "exp1") String exp1,
			@RequestParam(name = "exp2") String exp2, @RequestParam(name = "countryId") String countryId,
			HttpSession session) {

		boolean hasError = false;

		removeAlert(model);
		String errorMsg = "";

		model.put("catName", catName);
		model.put("catDesc", catDesc);

		try {
			errorMsg += commonValServ.validateInputLength(catName, 100, CAT_NAME_TXT);
			errorMsg += commonValServ.validateInputLength(catDesc, 200, CAT_DESC_TXT);

			if (errorMsg.length() == 0) {
				hasError = false;
				
				List<RegulationCatDto> items = regCatServ.findByCriteria(catName.trim(), catDesc.trim(), exp1, exp2, countryId);
				items.forEach(arg0 -> arg0.setCountryListDesc(getCountryListDesc(arg0.getCountryList(),null)));

				model.put("fileCategoryItems",items);
				model.put("exp1", exp1);
				model.put("exp2", exp2);

				StringBuffer sb = new StringBuffer();
				sb.append("Search Criteria: ");
				sb.append("File Category=").append(catName.isEmpty() ? "<ALL>" : catName).append(", ");
				sb.append("Description=").append(catDesc.isEmpty() ? "<ALL>" : catDesc);

				trxHistServ.addTrxHistory(new Date(), "Search File Category", request.getRemoteUser(),
						CommonConstants.FUNCTION_TYPE_SEARCH, "Search File Category",
						CommonConstants.RECORD_TYPE_ID_REGL, sb.toString());

			} else
				hasError = true;

		} catch (Exception e) {
			e.printStackTrace();
			hasError = true;
			errorMsg += "Failed to get record.";
			errorMsg += e.toString();

		} finally {
			if (hasError == true) {
				model.put("error", errorMsg);
				// return back user input
				model.put("fileCategoryItems", getAllItems("","","","",""));
			}
		}

		return new ModelAndView("main/regulation/fileCategoryList", model);
	}

	@RequestMapping("/main/regulation/fileCategoryFormDel")
	public ModelAndView deleteRegulationCategoryBatch(HttpServletRequest request,
			@RequestParam(value = "tableRow") String[] check, HttpSession session) {

		List<RegulationCategory> categoryList = regCatServ.checkCategoryExist();
		String errorMsg = "";
		removeAlert(model);

		try {
			// FSGS) KarVei 28/2/2021 - Add Validation before delete file category START
			for (RegulationCategory catList : categoryList) {

				for (int x = 0; x < check.length; x++) {

					if (catList.getCatName().equals(check[x])) {

						errorMsg += check[x]
								+ " cannot be deleted as it was used in raw material management setup. </BR>";
						// Remove category from array if check exist
						check = (String[]) ArrayUtils.remove(check, x);
					}
				}
			}
			// FSGS) KarVei 28/2/2021 - Add Validation before delete file category END

			if (errorMsg.length() == 0) {
				// Delete form
				for (int i = 0; i < check.length; i++) {

					regCatServ.deleteCategoryRegulation(check[i]);
					trxHistServ.addTrxHistory(new Date(), "Delete File Category", request.getRemoteUser(),
							CommonConstants.FUNCTION_TYPE_DELETE, check[i], CommonConstants.RECORD_TYPE_ID_REGL, null);
				}

				// Print update success
				model.put("success", "Record deleted successfully.");

			} else {
				model.put("error", errorMsg);
				model.put("fileCategoryItems", getAllItems("","","","",""));
				return new ModelAndView(("/main/regulation/fileCategoryList"), model);
			}

		} catch (Exception e) {
			// Print DB exception
			model.put("error", "Record failed to delete.");
		}

		model.put("fileCategoryItems", getAllItems("","","","",""));

		return new ModelAndView(("/main/regulation/fileCategoryList"), model);
	}

	private Map<String, Object> removeAlert(Map<String, Object> model) {
		model.remove("error");
		model.remove("success");

		return model;
	}
	
	private List<Country> getCategoryCountryList(String list) {
		List<Country> country = new ArrayList<Country>();
		if (null == list || list.isEmpty())
			return country;

		List<String> toList = Stream.of(list.split(",", -1)).collect(Collectors.toList());

		return countryServ.searchCountry(toList);
	}

	private List<Country> getRefCountryList() {
		return countryServ.searchCountry("", "", "", "", String.valueOf(CommonConstants.FLAG_YES), "");
	}
	
	private String getCountryListDesc(String strList, List<Country> countryList) {
		List<Country> country = new ArrayList<Country>();
		List<String> descList = new ArrayList<String>();
		
		if((strList == null || strList.isEmpty()) && (countryList != null && !countryList.isEmpty()))
			country = countryList;
		
		if((strList != null && !strList.isEmpty()) && (countryList == null || countryList.isEmpty()))
			country = getCategoryCountryList(strList);	

		if (!country.isEmpty())
			descList = country.stream().map(arg -> arg.getCountryName()).collect(Collectors.toList());

		return (descList == null || descList.isEmpty()) ? "" : String.join(",", descList);
	}
	
	private List<RegulationCatDto> getAllItems(String catName, String catDesc, String exp1, String exp2, String countryId) {
		List<RegulationCatDto> items = regCatServ.findByCriteria(catName, catDesc, exp1, exp2, countryId);
		items.forEach(arg0 -> arg0.setCountryListDesc(getCountryListDesc(arg0.getCountryList(), null)));
		return items;
	}

}
