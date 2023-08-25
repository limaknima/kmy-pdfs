package com.fms.pfc.controller.base;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.context.annotation.SessionScope;
import org.springframework.web.servlet.ModelAndView;

import com.fms.pfc.common.Authority;
import com.fms.pfc.common.CommonConstants;
import com.fms.pfc.domain.dto.ManufacturerDto;
import com.fms.pfc.service.api.base.CountryService;
import com.fms.pfc.service.api.base.ManufacturerService;
import com.fms.pfc.service.api.base.StateService;
import com.fms.pfc.service.api.base.TrxHisService;
import com.fms.pfc.validation.common.CommonValidation;

@Controller
@SessionScope
public class ManufacturerController {
	
	private ManufacturerService manfServ;	
	private StateService refStateServ;	
	private CountryService refCountryServ;	
	private CommonValidation commonValServ;	
	private Authority auth;	
	private TrxHisService trxHistServ;
	private MessageSource msgSource;

	private final String VENDOR_TYPE_TXT = "Category/Type";
	private final String VENDOR_NAME_TXT = "Manufacturer/Supplier Name";
	private final String EMAIL_TXT = "Email Address";
	private final String TEL_NO_TXT = "Telephone No.";
	private final String FAX_NO_TXT = "Fax No.";
	private final String ADDRESS_TXT = "Address";
	private final String CITY_TXT = "City";
	private final String POSTCODE_TXT = "Postcode";
	private final String URL_TXT = "Url";
	
	private Map<String, Object> model = new HashMap<String, Object>();

	@Autowired
	public ManufacturerController(ManufacturerService manfServ, StateService refStateServ,
			CountryService refCountryServ, CommonValidation commonValServ, Authority auth, TrxHisService trxHistServ,
			MessageSource msgSource) {
		super();
		this.manfServ = manfServ;
		this.refStateServ = refStateServ;
		this.refCountryServ = refCountryServ;
		this.commonValServ = commonValServ;
		this.auth = auth;
		this.trxHistServ = trxHistServ;
		this.msgSource = msgSource;
	}	

	@GetMapping("/base/admin/manufacturer/addManufacturer")
	public ModelAndView getAddManufacturer(HttpServletRequest request, HttpServletResponse response) {

		model.put("stateItems", refStateServ.searchState(""));
		model.put("countryItems", refCountryServ.findAll());
		model.put("manufacturer", new ManufacturerDto());

		return new ModelAndView("/base/admin/manufacturer/addManufacturer", model);
	}

	@PostMapping("/base/admin/manufacturer/saveManufacturer")
	public ModelAndView addManufacturer(@Valid ManufacturerDto m, BindingResult bindingResult, HttpServletRequest request,
			HttpSession session) {

		String errorMsg = "";

		model.remove("error");
		model.remove("success");
		model.put("stateItems", refStateServ.searchState(""));
		model.put("countryItems", refCountryServ.findAll());

		try {
			// validation
			errorMsg = commonValServ.validateUniqueManuName(m.getVendorName(), VENDOR_NAME_TXT);
			errorMsg += commonValServ.validateMandatoryInput(m.getVendorName(), VENDOR_NAME_TXT);
			errorMsg += commonValServ.validateMandatoryInput(m.getVendorType(), VENDOR_TYPE_TXT);

			errorMsg += commonValServ.validateInputLength(m.getVendorType(), 100, VENDOR_TYPE_TXT);
			errorMsg += commonValServ.validateInputLength(m.getVendorName(), 100, VENDOR_NAME_TXT);
			errorMsg += commonValServ.validateInputLength(m.getEmail(), 50, EMAIL_TXT);
			errorMsg += commonValServ.validateInputLength(m.getOfficeTel(), 50, TEL_NO_TXT);
			errorMsg += commonValServ.validateInputLength(m.getFaxNo(), 50, FAX_NO_TXT);
			errorMsg += commonValServ.validateInputLength(m.getAddress(), 300, ADDRESS_TXT);
			errorMsg += commonValServ.validateInputLength(m.getTown(), 100, CITY_TXT);
			errorMsg += commonValServ.validateInputLength(m.getPostcode(), 10, POSTCODE_TXT);
			errorMsg += commonValServ.validateInputLength(m.getUrl(), 255, URL_TXT);
			errorMsg += commonValServ.validateEmailFormat(m.getEmail(), EMAIL_TXT);
			errorMsg += commonValServ.validateUrlFormat(m.getUrl(), URL_TXT);

			if (errorMsg.length() == 0) {
				int id = manfServ.saveManufacturer(m, request.getRemoteUser());

				trxHistServ.addTrxHistory(new Date(), "Insert Manufacturer/Supplier", request.getRemoteUser(),
						CommonConstants.FUNCTION_TYPE_INSERT, String.valueOf(id), CommonConstants.RECORD_TYPE_ID_VENDOR,
						null);
				
				model.put("success", msgSource.getMessage("msgSuccessAdd", new Object[] {}, Locale.getDefault()));
				model.put("manufacturer", new ManufacturerDto());
			} else {
				model.put("error", errorMsg);
				model.put("manufacturer", m); // return back user input
				return new ModelAndView("/base/admin/manufacturer/addManufacturer", model);
			}

		} catch (Exception e) {
			e.printStackTrace();
			model.put("error", msgSource.getMessage("msgFailAdd", new Object[] {}, Locale.getDefault()));
		}

		model.put("manufacturerItems", manfServ.findAll());
		return new ModelAndView("/base/admin/manufacturer/listManufacturer", model);
	}

	@GetMapping("/base/admin/manufacturer/editManufacturer")
	public ModelAndView getEditManufacturer(@RequestParam(name = "vendorId") int vendorId, HttpServletRequest request,
			HttpServletResponse response, HttpSession session) {

		model.remove("error");
		model.remove("success");
		model.put("stateItems", refStateServ.searchState(""));
		model.put("countryItems", refCountryServ.findAll());
		model.put("manufacturer", manfServ.findOneById(vendorId));

		return new ModelAndView("/base/admin/manufacturer/editManufacturer", model);
	}

	@PostMapping("/base/admin/manufacturer/updateManufacturer")
	public ModelAndView updateManufacturer(@Valid ManufacturerDto manufacturer, BindingResult bindingResult,
			HttpServletRequest request, HttpSession session) {

		String errorMsg = "";

		ManufacturerDto manf = manfServ.findOneById(manufacturer.getVendorId());
		model.remove("error");
		model.remove("success");
		model.put("stateItems", refStateServ.searchState(""));
		model.put("countryItems", refCountryServ.findAll());

		try {
			// validation
			if (!manf.getVendorName().equals(manufacturer.getVendorName())){							
				errorMsg = commonValServ.validateUniqueManuName(manufacturer.getVendorName(), VENDOR_NAME_TXT);			
			}			
			errorMsg += commonValServ.validateMandatoryInput(manufacturer.getVendorType(), VENDOR_TYPE_TXT);
			errorMsg += commonValServ.validateMandatoryInput(manufacturer.getVendorName(), VENDOR_NAME_TXT);			
			errorMsg += commonValServ.validateInputLength(manufacturer.getVendorName(), 100, VENDOR_NAME_TXT);
			errorMsg += commonValServ.validateInputLength(manufacturer.getVendorType(), 100, VENDOR_TYPE_TXT);
			errorMsg += commonValServ.validateInputLength(manufacturer.getEmail(), 50, EMAIL_TXT);
			errorMsg += commonValServ.validateInputLength(manufacturer.getOfficeTel(), 50, TEL_NO_TXT);
			errorMsg += commonValServ.validateInputLength(manufacturer.getFaxNo(), 50, FAX_NO_TXT);
			errorMsg += commonValServ.validateInputLength(manufacturer.getAddress(), 300, ADDRESS_TXT);
			errorMsg += commonValServ.validateInputLength(manufacturer.getTown(), 100, CITY_TXT);
			errorMsg += commonValServ.validateInputLength(manufacturer.getPostcode(), 10, POSTCODE_TXT);
			errorMsg += commonValServ.validateInputLength(manufacturer.getUrl(), 255, URL_TXT);
			errorMsg += commonValServ.validateEmailFormat(manufacturer.getEmail(), EMAIL_TXT);
			errorMsg += commonValServ.validateUrlFormat(manufacturer.getUrl(), URL_TXT);

			if (errorMsg.length() == 0) {
				manfServ.saveManufacturer(manufacturer, request.getRemoteUser());

				trxHistServ.addTrxHistory(new Date(), "Update Manufacturer/Supplier", request.getRemoteUser(),
						CommonConstants.FUNCTION_TYPE_UPDATE, String.valueOf(manufacturer.getVendorId()),
						CommonConstants.RECORD_TYPE_ID_VENDOR, null);

				model.put("success", msgSource.getMessage("msgSuccessUpdate", new Object[] {}, Locale.getDefault()));
			} else {
				model.put("error", errorMsg);
				model.put("manufacturer", manufacturer);
				return new ModelAndView("/base/admin/manufacturer/editManufacturer", model);
			}

		} catch (Exception e) {
			e.printStackTrace();
			model.put("error", msgSource.getMessage("msgFailUpdate", new Object[] {}, Locale.getDefault()));

			return new ModelAndView("/base/admin/manufacturer/editManufacturer", model);
		}
		
		model.put("manufacturerItems", manfServ.findAll());
		return new ModelAndView("/base/admin/manufacturer/listManufacturer", model);
	}

	@GetMapping("/base/admin/manufacturer/listManufacturer")
	public ModelAndView getManufacturerList(HttpServletRequest request, HttpServletResponse response,
			HttpSession session) {

		model = auth.onPageLoad(model, request);
		model.put("vendorType", "");
		model.put("manufacturerItems", manfServ.findAll());

		return new ModelAndView("/base/admin/manufacturer/listManufacturer", model);
	}

	@PostMapping("/base/admin/manufacturer/listManufacturer")
	public ModelAndView searchManufacturerList(HttpServletRequest request,
			@RequestParam(name = "vendorName") String vendorName,
			@RequestParam(name = "vendorNameWildcard") String vendorNameWildcard,
			@RequestParam(name = "email") String email, @RequestParam(name = "emailWildcard") String emailWildcard,
			@RequestParam(name = "vendorType") String vendorType, HttpSession session) {

		StringBuffer sb = new StringBuffer();
		sb.append("Search Criteria: ");
		sb.append("Manufacturer Name=").append(vendorName.isEmpty() ? "<ALL>" : vendorName).append(", ");
		sb.append("Email Address=").append(email.isEmpty() ? "<ALL>" : email).append(", ");
		sb.append("Category/Type=").append(vendorType.isEmpty() ? "<ALL>" : vendorType);

		trxHistServ.addTrxHistory(new Date(), "Search Manufacturer/Supplier", request.getRemoteUser(),
				CommonConstants.FUNCTION_TYPE_SEARCH, "Search Manufacturer/Supplier",
				CommonConstants.RECORD_TYPE_ID_VENDOR, sb.toString());

		model.remove("error");
		model.remove("success");

		model.put("manufacturerItems", manfServ.findByCriteria(vendorName.trim(), vendorNameWildcard, email.trim(),
				emailWildcard, vendorType, 0));

		model.put("vendorName", vendorName);
		model.put("vendorNameWildcard", vendorNameWildcard);
		model.put("email", email);
		model.put("emailWildcard", emailWildcard);
		model.put("vendorType", vendorType);

		String listTitle = vendorType.equals("1") ? "Manufacturer Listing"
				: vendorType.equals("2") ? "Supplier Listing" : "Manufacturer and Supplier Listing";
		String nameTitle = vendorType.equals("1") ? "Manufacturer Name"
				: vendorType.equals("2") ? "Supplier Name" : "Manufacturer and Supplier Name";
		model.put("listTitle", listTitle);
		model.put("nameTitle", nameTitle);

		return new ModelAndView("/base/admin/manufacturer/listManufacturer", model);
	}

	@GetMapping("/base/admin/manufacturer/viewManufacturer")
	public ModelAndView getViewManufacturerData(@RequestParam(name = "vendorId") int vendorId,
			HttpServletRequest request, HttpServletResponse response) {

		model.remove("error");
		model.remove("success");

		try {
			ManufacturerDto m = manfServ.findOneById(vendorId);

			model.put("vendorId", m.getVendorId());
			model.put("vendorName", m.getVendorName());
			model.put("vendorType", m.getVendorType());
			model.put("email", m.getEmail());
			model.put("street", m.getStreet());
			model.put("address", m.getAddress());
			model.put("town", m.getTown());
			model.put("postcode", m.getPostcode());

			trxHistServ.addTrxHistory(new Date(), "View Manufacturer/Supplier", request.getRemoteUser(),
					CommonConstants.FUNCTION_TYPE_VIEW, String.valueOf(m.getVendorId()), CommonConstants.RECORD_TYPE_ID_VENDOR, null);

			if (m.getStateId() != null) {
				if (!m.getStateId().equals("")) {
					model.put("stateName", refStateServ.searchState(m.getStateId()).get(0).getStateName());
				}
			}

			if (m.getCountryId() != null) {
				if (!m.getCountryId().equals("")) {
					model.put("countryName", refCountryServ.findOneById(m.getCountryId()).getCountryName());
				}
			}

			model.put("url", m.getUrl());
			model.put("officeTel", m.getOfficeTel());
			model.put("faxNo", m.getFaxNo());
			model.put("effDateFrom", m.getEffDateFrom());
			model.put("effDateTo", m.getEffDateTo());
			DateFormat formatter = new SimpleDateFormat("dd/MM/yyy hh:mm:ss a");

			try {
				String createdInfo = "Created by " + m.getCreatorId() + " on "
						+ formatter.format(m.getCreatedDateTime());
				model.put("createdInfo", createdInfo);
			} catch (Exception e) {
			}

			String modifiedInfo = "";
			try {
				modifiedInfo = "Modified by " + m.getModifierId() + " on " + formatter.format(m.getModifiedDateTime());
			} catch (Exception e) {
				modifiedInfo = "Modified by --";
			}
			model.put("modifiedInfo", modifiedInfo);

		} catch (Exception e) {
			e.printStackTrace();
			model.put("error", msgSource.getMessage("msgFailSearchRecord", new Object[] {}, Locale.getDefault()));
			model.put("manufacturerItems", manfServ.findAll());

			return new ModelAndView("/base/admin/manufacturer/listManufacturer", model);
		}

		return new ModelAndView("/base/admin/manufacturer/viewManufacturer", model);
	}

	@PostMapping("/base/admin/manufacturer/listManufacturerDelete")
	public ModelAndView deleteManufacturerBatch(HttpServletRequest request,
			@RequestParam(name = "tableRow") String[] vendorIdList, HttpSession session) {

		model.remove("error");
		model.remove("success");
		try {
			for (int i = 0; i < vendorIdList.length; i++) {

				if (checkVendorIdInUsed(vendorIdList[i])) {
					manfServ.deleteById(Integer.parseInt(vendorIdList[i]));
					trxHistServ.addTrxHistory(new Date(), "Delete Manufacturer/Supplier", request.getRemoteUser(),
							CommonConstants.FUNCTION_TYPE_DELETE, String.valueOf(vendorIdList[i]), CommonConstants.RECORD_TYPE_ID_VENDOR,
							null);
					model.put("success",
							msgSource.getMessage("msgSuccessDelete", new Object[] {}, Locale.getDefault()));
				} else
					model.put("error", "Manufacturer code is in used and it is not allow to delete from the system.");
			}
		} catch (Exception e) {
			e.printStackTrace();
			model.put("error", msgSource.getMessage("msgFailDelete", new Object[] {}, Locale.getDefault()));
			return new ModelAndView("/base/admin/manufacturer/listManufacturer", model);
		}

		model.put("manufacturerItems", manfServ.findAll());

		return new ModelAndView("/base/admin/manufacturer/listManufacturer", model);
	}

	// FSGS) Hani 2/3/2021 Add check VendorID usage START
	private Boolean checkVendorIdInUsed(String vendorID) {

		String result = "";
		result = manfServ.validateVendor(Integer.parseInt(vendorID));

		if (result != null && !result.isEmpty()) {
			if ("0".equals(result))
				return true;
		}

		return false;
	}
	// FSGS) Hani 2/3/2021 Add check VendorID usage END

}
