package com.fms.pfc.controller.main;

import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.WordUtils;
import org.apache.tomcat.util.codec.binary.Base64;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.context.annotation.SessionScope;
import org.springframework.web.servlet.ModelAndView;

import com.fms.pfc.common.Authority;
import com.fms.pfc.common.CommonConstants;
import com.fms.pfc.domain.dto.LabelAndValueDto;
import com.fms.pfc.domain.model.Country;
import com.fms.pfc.domain.model.Organization;
import com.fms.pfc.domain.model.State;
import com.fms.pfc.domain.model.Usr;
import com.fms.pfc.domain.model.main.PrNameDto;
import com.fms.pfc.domain.model.main.PrStat;
import com.fms.pfc.domain.model.main.ProductRecipe;
import com.fms.pfc.service.api.base.CountryService;
import com.fms.pfc.service.api.base.LoginService;
import com.fms.pfc.service.api.base.OrganizationService;
import com.fms.pfc.service.api.base.StateService;
import com.fms.pfc.service.api.base.TrxHisService;
import com.fms.pfc.service.api.main.ProductRecipeService;
import com.fms.pfc.service.api.main.ProductStatusService;
import com.fms.pfc.validation.common.CommonValidation;

import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;

@Controller
@SessionScope
public class PrintDeclarationController {
	
	private final Logger logger = LoggerFactory.getLogger(PrintDeclarationController.class);
	
	private CountryService refCountryServ;	
	private ProductRecipeService prodRecpServ;	
	private ProductStatusService prodStatServ;	
	private StateService refStateServ;	
	private LoginService loginServ;	
	private OrganizationService orgServ;	
	private CommonValidation commonValServ;	
	private TrxHisService trxHistServ;	
	private Authority auth;
	private MessageSource msgSource;
	
	private final String PR_NAME_TXT = "Product Name";
	private final String CN_NAME_TXT = "Country Name";
	private final String CS_NAME_TXT = "Customer Name";
	private final String REMARK_TXT = "Remark";

	private Map<String, Object> model = new HashMap<String, Object>();
	
	@Autowired
	public PrintDeclarationController(CountryService refCountryServ, ProductRecipeService prodRecpServ,
			ProductStatusService prodStatServ, StateService refStateServ, LoginService loginServ,
			OrganizationService orgServ, CommonValidation commonValServ, TrxHisService trxHistServ, Authority auth,
			MessageSource msgSource) {
		super();
		this.refCountryServ = refCountryServ;
		this.prodRecpServ = prodRecpServ;
		this.prodStatServ = prodStatServ;
		this.refStateServ = refStateServ;
		this.loginServ = loginServ;
		this.orgServ = orgServ;
		this.commonValServ = commonValServ;
		this.trxHistServ = trxHistServ;
		this.auth = auth;
		this.msgSource = msgSource;
	}

	@GetMapping("/main/product/declaration")
	public ModelAndView getPrintDeclaration(HttpServletRequest request, HttpServletResponse response) {
			
		model = auth.onPageLoad(model, request);
		auth.isAuthUrl(request, response);
		
		model.put("confirmHeader",
				msgSource.getMessage("msgReportConfirmHeader", new Object[] {}, Locale.getDefault()));
		model.put("confirmMsg", msgSource.getMessage("msgReportConfirm", new Object[] {}, Locale.getDefault()));

		return new ModelAndView("/main/product/declaration", model);
	}

	@PostMapping("/main/product/declaration")
	public ModelAndView refreshPage(@RequestParam(name = "product") String productId,
			@RequestParam(name = "prodautocomplete") String prodautocomplete,
			@RequestParam(name = "customer") String customer, @RequestParam(name = "remark") String remark,
			HttpServletRequest request, HttpSession session) {
		
		logger.debug("refreshPage() productId={}",productId);
		
		model.remove("success");
		model.remove("error");
		
		String prId = "";		
		StringTokenizer st = new StringTokenizer(productId,"|");
		while (st.hasMoreTokens()) {
			String idx = (String) st.nextElement();
			if(idx.startsWith("id=")) {
				prId = idx.substring(3);
			}				
		}
				
		if (!prId.equals("0") && StringUtils.isNotEmpty(prId)) {
			// Get prId from product recipe
			//ProductRecipe product = prodRecpServ.searchProductId(prId, String.valueOf(CommonConstants.SEARCH_OPTION_EXACT)).get(0);
			// Product's country list
			List<Country> countryList = new ArrayList<Country>();
			// List to get country id and permissibility id
			List<PrStat> productCountryList = prodStatServ.searchProductStatus(Integer.parseInt(prId), String.valueOf(CommonConstants.SEARCH_OPTION_EXACT), "", 0);

			for (PrStat prStat : productCountryList) {
				// Get all country code for screen
				countryList.addAll(refCountryServ.searchCountry(prStat.getCountryId(), "", String.valueOf(CommonConstants.SEARCH_OPTION_EXACT), "", "", ""));
			}
			
			model.put("product", productId);
			model.put("prodautocomplete", prodautocomplete);
			model.put("countryList", countryList);
			model.put("customer", customer);
			model.put("remark", remark);
		} else {
			model.remove("selectedPrCode");
			model.remove("countryList");
		}

		return new ModelAndView("/main/product/declaration", model);
	}

	@PostMapping(value = "/main/product/declaration", params = "print=true")
	public ModelAndView printReport(@RequestParam(name = "product") String productId, 
			@RequestParam(name = "prodautocomplete") String prodautocomplete,
			@RequestParam(name = "country", required = false) List<String> country,
			@RequestParam(name = "customer") String customer, @RequestParam(name = "remark") String remark,
			HttpServletRequest request, HttpServletResponse response, HttpSession session)
			throws IllegalArgumentException, SecurityException, IllegalAccessException, NoSuchFieldException {
		
		model.remove("success");
		model.remove("error");
		
		//logger.debug("printReport() productId={},prodautocomplete={}",productId, prodautocomplete);

		List<ProductRecipe> productList = prodRecpServ.searchProductRecipe("", "", "", "", 0, CommonConstants.RECORD_STATUS_SUBMITTED);
		List<ProductRecipe> sortedproductList = productList.stream().sorted((o1, o2)->o1.getPrName().
	            compareTo(o2.getPrName())).collect(Collectors.toList());
		
		boolean hasError = false;

		String errorMsg = "";

		try {

			if (productId.equals("0") || productId.isEmpty()) {
				errorMsg += PR_NAME_TXT + " is mandatory.<br />";
			}

			if (country == null) {
				errorMsg += CN_NAME_TXT + " is mandatory.<br />";
			}
			errorMsg += commonValServ.validateMandatoryInput(customer, CS_NAME_TXT);
			errorMsg += commonValServ.validateInputLength(remark, 100, REMARK_TXT);

			if (!errorMsg.isEmpty()) {
				model.put("productItem", sortedproductList);
				model.put("selectedPrCode", productId);
				model.put("remark", remark);
				model.put("countryx", country);
				model.put("remark", remark);
				model.put("customer", customer);
				model.put("error", errorMsg);
				return new ModelAndView("/main/product/declaration", model);
			}

		} catch (Exception e) {
			e.printStackTrace();
			hasError = true;
			errorMsg += e.toString();
		}
		
		String prId = "";
		String selName = "";
		
		StringTokenizer st = new StringTokenizer(productId,"|");
		while (st.hasMoreTokens()) {
			String idx = (String) st.nextElement();
			if(idx.startsWith("id=")) {
				prId = idx.substring(3);
			}	
			if(idx.startsWith("name=")) {
				selName = idx.substring(5);
			}				
		}
		
		// Get prId from product recipe
		//ProductRecipe product = prodRecpServ.searchProductId(productId, String.valueOf(CommonConstants.SEARCH_OPTION_EXACT)).get(0);
		Country countryName = new Country();
		// Get product status
		List<PrStat> prStatList = new ArrayList<PrStat>();

		for (int i = 0; i < country.size(); i++) {

			prStatList.addAll(prodStatServ.searchProductStatus(Integer.parseInt(prId), String.valueOf(CommonConstants.SEARCH_OPTION_EXACT), country.get(i), 0));
		}

		// Validate data size - do not proceed to report generation if no data found
		if (prStatList.isEmpty() || prStatList.size() == 0) {
			model.put("error", msgSource.getMessage("msgReportNoData", new Object[] {}, Locale.getDefault()));
			model.remove("success");
			return new ModelAndView("/main/product/declaration", model);
		}

		// List to store country name and permissibility status
		List<PrStat> reportList = new ArrayList<PrStat>();

		for (PrStat prStat : prStatList) {
			// Get country name
			countryName = refCountryServ.searchCountry(prStat.getCountryId(), "", String.valueOf(CommonConstants.SEARCH_OPTION_EXACT), "", "", "").get(0);
			// Set country name for each product
			prStat.setCountryName(countryName.getCountryName());
			// Set Status name for each product
			if (prStat.getFinalStatus() == 1) {
				prStat.setStatusName("Permitted");
			} else if (prStat.getFinalStatus() == 2) {
				prStat.setStatusName("Not Permitted");
			} else if (prStat.getFinalStatus() == 3) {
				prStat.setStatusName("Not sure");
			}
			reportList.add(prStat);
		}

		// Initialize objects for report
		// Get current user
		Usr loginUsr = loginServ.searchUser(request.getRemoteUser());
		Organization org = orgServ.searchOrganization(loginUsr.getOrgId(), "", "", String.valueOf(CommonConstants.SEARCH_OPTION_EXACT), "").get(0);
		Country con = refCountryServ.searchCountry(org.getCountry(), "", String.valueOf(CommonConstants.SEARCH_OPTION_EXACT), "", "", "").get(0);
		State sta = refStateServ.searchState(org.getState()).get(0);
		// Set address for report
		String address = genAddressStr(org, con, sta);
		String base64Encoded = "";
		List<String> selectedCountry = new ArrayList<>();

		for (int i = 0; i < prStatList.size(); i++) {

			selectedCountry.add(prStatList.get(i).getCountryName());

		}

		DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd/MM/yyyy");  
		LocalDate date = LocalDate.now();  
		   
		// searchString for TRX_HISTOR_LOG
		String searchString = "Letter Date: " + dtf.format(date) + "," + "Product Name: " + selName + "," + "Country: "
				+ selectedCountry.toString() + "," + "Customer: " + customer + "," + "Remark: " + remark;
		// Encode image
		if (org.getLogo() != null) {

			byte[] encodeBase64 = Base64.encodeBase64(org.getLogo());
			try {
				base64Encoded = new String(encodeBase64, "UTF-8");
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
				hasError = true;
				errorMsg += e.toString();
			}
		}

		// Print report
		StringBuffer fileName = new StringBuffer();
		String reportName = msgSource.getMessage(CommonConstants.REPORT_ID_PROD_DECL, new Object[] {},
				Locale.getDefault());
		try {

			InputStream employeeReportStream = getClass().getResourceAsStream("/report/Declaration_Letter.jrxml");
			JasperReport jasperReport = JasperCompileManager.compileReport(employeeReportStream);

			Map<String, Object> parameters = new HashMap<String, Object>();
			// Set parameters for report fields
			parameters.put("product", selName);
			parameters.put("address", address);
			parameters.put("orgName", org.getOrgaName());
			parameters.put("logo", base64Encoded);
			parameters.put("tel", org.getTel());
			parameters.put("fax", org.getFax());

			Date Reportdate = java.util.Calendar.getInstance().getTime();
			DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
			parameters.put("date", dateFormat.format(Reportdate));

			JRBeanCollectionDataSource source = new JRBeanCollectionDataSource(reportList);
			JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, parameters, source);

			{
				Calendar cal = Calendar.getInstance();
				cal.setTime(Reportdate);
				long timeInMillis = cal.getTimeInMillis();
				
				fileName.append(reportName).append("_").append(timeInMillis).append(".")
						.append(CommonConstants.REPORT_FORMAT_PDF.toLowerCase());

				response.setContentType("application/x-download");
				response.setHeader(HttpHeaders.CONTENT_DISPOSITION,
						"attachment; filename=\"" + fileName.toString() + "\"");

				OutputStream outputStream = response.getOutputStream();
				JasperExportManager.exportReportToPdfStream(jasperPrint, outputStream);
			}

		} catch (Exception e) {
			e.printStackTrace();
			hasError = true;
			errorMsg += e.toString();
		} finally {
			if (hasError == true) {
				errorMsg += msgSource.getMessage("msgReportErr", new Object[] {}, Locale.getDefault());
				model.put("error", errorMsg);
				model.remove("success");
			} else {
				model.put("success", msgSource.getMessage("msgReportSuccess", new Object[] {}, Locale.getDefault()));
				model.remove("error");
				model.remove("date");
				model.put("productItem", sortedproductList);
				model.remove("selectedPrCode");
				model.remove("countryList");
				model.remove("customer");
				model.remove("remark");
			}
			// FSGS) Kent 26/2/2021 Add/changed - Add Function executed log START
			trxHistServ.addTrxHistory(new Date(), "Print Declaration Letter", request.getRemoteUser(),
					CommonConstants.FUNCTION_TYPE_DOWNLOAD, reportName,
					CommonConstants.RECORD_TYPE_ID_PROD_RECP_DECL, searchString);
			// FSGS) Kent 26/2/2021 Add/changed - Add Function executed log END
		}
		return new ModelAndView("/main/product/declaration", model);
	}

	protected String genAddressStr(Organization org, Country con, State sta) {
		String address = "";
		if (StringUtils.isNotEmpty(org.getAddress()) && StringUtils.isNotEmpty(org.getCity())
				&& StringUtils.isNotEmpty(org.getPostcode())) {
			address = WordUtils.capitalizeFully(org.getAddress()) + ", " + WordUtils.capitalizeFully(org.getCity())
					+ ", " + org.getPostcode() + " " + WordUtils.capitalizeFully(sta.getStateName()) + ", "
					+ WordUtils.capitalizeFully(con.getCountryName());
		}

		return address;
	}
	
	public static <T> Predicate<T> distinctByKey(Function<? super T, ?> keyExtractor) {
	    Set<Object> seen = ConcurrentHashMap.newKeySet();
	    return t -> seen.add(keyExtractor.apply(t));
	}
	
	@RequestMapping(value = "/main/product/prodAutocomplete", method = RequestMethod.GET, headers = "Accept=application/json")
	@ResponseBody
	public List<LabelAndValueDto> prodAutocomplete(
			@RequestParam(name = "term", required = false, defaultValue = "") String term) {

		List<LabelAndValueDto> suggestions = new ArrayList<LabelAndValueDto>();
		List<PrNameDto> allNames = prodRecpServ.findAllPrNames();
		for (PrNameDto prNameDto : allNames) {
			if (StringUtils.contains(prNameDto.getPrName().toLowerCase(), term.toLowerCase())) {
				suggestions.add(new LabelAndValueDto(prNameDto.getPrName(), prNameDto.getIdx()));
			}
		}

		return suggestions;
	}
}
