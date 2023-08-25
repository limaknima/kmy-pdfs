package com.fms.pfc.controller.main;

import java.io.InputStream;
import java.io.OutputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.context.annotation.SessionScope;
import org.springframework.web.servlet.ModelAndView;

import com.fms.pfc.common.Authority;
import com.fms.pfc.common.CommonConstants;
import com.fms.pfc.domain.model.Country;
import com.fms.pfc.domain.model.main.Compliance;
import com.fms.pfc.domain.model.main.PrNameDto;
import com.fms.pfc.domain.model.main.PrStat;
import com.fms.pfc.domain.model.main.ProductRecipe;
import com.fms.pfc.service.api.base.CountryService;
import com.fms.pfc.service.api.base.TrxHisService;
import com.fms.pfc.service.api.main.ComplianceService;
import com.fms.pfc.service.api.main.ProductRecipeService;
import com.fms.pfc.service.api.main.ProductStatusService;

import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;

@Controller
@SessionScope
public class FinalPermissibilityController {
	
	private static final Logger logger = LoggerFactory.getLogger(FinalPermissibilityController.class);
	
	private CountryService refCountryServ;	
	private ProductRecipeService prodRecpServ;	
	private ProductStatusService prodStatServ;	
	private ComplianceService complServ;	
	private Authority auth;	
	private TrxHisService trxHistServ;
	private MessageSource msgSource;
	
	private Map<String, Object> model = new HashMap<String, Object>();
	boolean hasError = false;	

	@Autowired
	public FinalPermissibilityController(CountryService refCountryServ, ProductRecipeService prodRecpServ,
			ProductStatusService prodStatServ, ComplianceService complServ, Authority auth, TrxHisService trxHistServ,
			MessageSource msgSource) {
		super();
		this.refCountryServ = refCountryServ;
		this.prodRecpServ = prodRecpServ;
		this.prodStatServ = prodStatServ;
		this.complServ = complServ;
		this.auth = auth;
		this.trxHistServ = trxHistServ;
		this.msgSource = msgSource;
	}	

	@GetMapping("/main/product/compliance")
	public ModelAndView getPrintDeclaration(HttpServletRequest request, HttpServletResponse response) {

		model = auth.onPageLoad(model, request);
		auth.isAuthUrl(request, response);
		
		// Get all products
		//List<ProductRecipe> productList = prodRecpServ.searchProductRecipe("", "", "", "", 0, 0);
		List<PrNameDto> allPrNames = prodRecpServ.findAllPrNames();
		// Get all products' countries
		List<PrStat> productCountryList = prodStatServ.searchProductStatus(0, "3", "", 0);
		// List of countries from ref_country
		List<Country> countryList = new ArrayList<Country>();
		
		// For each productCountry
		for (PrStat prStat : productCountryList) {
			// Get all country name
			countryList.addAll(refCountryServ.searchCountry(prStat.getCountryId(), "",
					String.valueOf(CommonConstants.SEARCH_OPTION_EXACT), "", "", ""));
		}
			
		// 20210125 - aminkamilm: distinct product list
		//List<ProductRecipe> productListDistinct = productList.stream().distinct().collect(Collectors.toList());
		List<Country> productCountryListDistinct = countryList.stream().distinct().collect(Collectors.toList());
		
		// Sort product dropdown
		//List<ProductRecipe> sortedProductList = productListDistinct.stream().sorted((o1, o2)->o1.getPrName().
          //      compareTo(o2.getPrName())).collect(Collectors.toList());
		
		// Sort country dropdown
		List<Country> sortedCountryList = productCountryListDistinct.stream()
				.filter(arg0 -> StringUtils.equals(arg0.getEvalFlag(), String.valueOf(CommonConstants.FLAG_YES)))
				.sorted((o1, o2) -> o1.getCountryName().compareTo(o2.getCountryName())).collect(Collectors.toList());
		
		model.put("productItem", allPrNames);
		model.put("countryList", sortedCountryList);

		// 20210125 - aminkamilm
		// Use message properties
		model.put("confirmHeader", msgSource.getMessage("msgReportConfirmHeader", new Object[] {}, Locale.getDefault()));
		model.put("confirmMsg", msgSource.getMessage("msgReportConfirm", new Object[] {}, Locale.getDefault()));

		return new ModelAndView("/main/product/compliance", model);
	}

	@PostMapping(value = "/main/product/compliance", params = "preview=true") //FSGS) Faiz 2021/3/4 - change print to preview
	public ModelAndView printReport(@Valid ProductRecipe pr, 
									@RequestParam(name = "product") String[] productId,
									@RequestParam(name = "country") String[] country, 
									HttpServletRequest request, HttpServletResponse response) {

		List<Compliance> productStatList = new ArrayList<Compliance>();
		List<String> paraCId = new ArrayList<String>();
		String errorMsg = "";
		
		for (int x = 0; x < country.length; x++) {
			paraCId.add(country[x]);
		}
		List<Integer> paraPr = new ArrayList<Integer>();

		for (int x = 0; x < productId.length; x++) {
			StringTokenizer st = new StringTokenizer(productId[x], "|");
			String id = "";
			while (st.hasMoreElements()) {
				String str = st.nextToken();
				if (str.startsWith("id=")) {
					id =str.substring(3);
					paraPr.add(Integer.parseInt(id));
				}
			}
		}
		
		productStatList.addAll(complServ.searchComplianceCountry(paraPr, paraCId));
		
		// Validate data size - do not proceed to report generation if no data found
		if(productStatList.isEmpty() || productStatList.size() == 0) {
			model.put("error", msgSource.getMessage("msgReportNoData", new Object[] {}, Locale.getDefault()));
			model.remove("success");
			return new ModelAndView("/main/product/compliance", model);
		}

		List<Compliance> reportList = new ArrayList<Compliance>();

		String selPrdNames = "";
		String selCtyNames = "";
		for (Compliance compliance : productStatList) {
			
//			{
//				for (int x = 0; x < productId.length; x++) {
//					StringTokenizer st = new StringTokenizer(productId[x], "|");
//					String id = "";
//					String name = "";
//					while (st.hasMoreElements()) {
//						String str = st.nextToken();
//						if (str.startsWith("id=")) {
//							id = str.substring(3);
//						} else if (str.startsWith("name=")) {
//							name = str.substring(5);
//						}
//
//						if (StringUtils.equals(id, compliance.getPrId().toString())) {
//							compliance.setProductName(name);
//						}
//					}
//				}
//			}

			// 20210125 - aminkamilm
			// Use CommonConstants and message properties
			if (compliance.getFinalStatus() == CommonConstants.VF_STATUS_PERMITTED) {
				compliance.setStatusName(msgSource.getMessage("vfStatus_" + CommonConstants.VF_STATUS_PERMITTED,
						new Object[] {}, Locale.getDefault()));
			} else if (compliance.getFinalStatus() == CommonConstants.VF_STATUS_NOT_PERMITTED) {
				compliance.setStatusName(msgSource.getMessage("vfStatus_" + CommonConstants.VF_STATUS_NOT_PERMITTED,
						new Object[] {}, Locale.getDefault()));
			} else if (compliance.getFinalStatus() == CommonConstants.VF_STATUS_NOT_SURE) {
				compliance.setStatusName(msgSource.getMessage("vfStatus_" + CommonConstants.VF_STATUS_NOT_SURE,
						new Object[] {}, Locale.getDefault()));
			} else {
				compliance.setStatusName(msgSource.getMessage("vfStatus_" + CommonConstants.VF_STATUS_NA,
						new Object[] {}, Locale.getDefault()));
			}
			reportList.add(compliance);
			
//			if(!StringUtils.contains(selPrdNames, compliance.getProductName())) {
//				selPrdNames = selPrdNames+compliance.getProductName()+", ";
//			}
			
			if(!StringUtils.contains(selCtyNames, compliance.getCountryName())) {
				selCtyNames = selCtyNames+compliance.getCountryName()+", ";
			}
		}
		
		{
			for (int x = 0; x < productId.length; x++) {
				StringTokenizer st = new StringTokenizer(productId[x], "|");
				String id = "";
				String name = "";
				while (st.hasMoreElements()) {
					String str = st.nextToken();
					if (str.startsWith("id=")) {
						id = str.substring(3);
					} else if (str.startsWith("name=")) {
						name = str.substring(5);
						if (!StringUtils.contains(selPrdNames, name)) {
							selPrdNames = selPrdNames + name + ", ";
						}
					}
				}
			}
		}
		
		if(StringUtils.endsWith(selPrdNames, ", "))
			selPrdNames = selPrdNames.substring(0,selPrdNames.lastIndexOf(", "));
		if(StringUtils.endsWith(selCtyNames, ", "))
			selCtyNames = selCtyNames.substring(0,selCtyNames.lastIndexOf(", "));

		// Print report
		StringBuffer fileName = new StringBuffer();
		String reportName = msgSource.getMessage(CommonConstants.REPORT_ID_PERM_COMPL, new Object[] {},
				Locale.getDefault());
		try {

			InputStream reportStream = getClass().getResourceAsStream("/report/FinalPermissibilityReport.jrxml");
			JasperReport jasperReport = JasperCompileManager.compileReport(reportStream);

			Map<String, Object> parameters = new HashMap<String, Object>();

			Date date = java.util.Calendar.getInstance().getTime();
			DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy hh:mm:ss");
			parameters.put("selPrdNames",selPrdNames);
			parameters.put("selCtyNames",selCtyNames);
			parameters.put("creator", request.getRemoteUser());
			parameters.put("time", dateFormat.format(date));

			JRBeanCollectionDataSource source = new JRBeanCollectionDataSource(reportList);
			JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, parameters, source);
			// JasperExportManager.exportReportToPdfFile(jasperPrint,
			// "D://FinalPermissibility_Report.pdf");

			// 20210125 - aminkamilm
			// Changed to export report to stream
			// Append post-fix (time-in-millis) to final file name
			{
				Calendar cal = Calendar.getInstance();
				cal.setTime(date);
				long timeInMillis = cal.getTimeInMillis();
				
				fileName.append(reportName).append("_").append(timeInMillis).append(".")
						.append(CommonConstants.REPORT_FORMAT_PDF.toLowerCase());

				response.setContentType("application/x-download");
				response.setHeader(HttpHeaders.CONTENT_DISPOSITION,
						"attachment; filename=\"" + fileName.toString() + "\"");

				OutputStream outputStream = response.getOutputStream();
				JasperExportManager.exportReportToPdfStream(jasperPrint, outputStream);
			}

		} catch (Exception e) { // TODO Auto-generated catch block
			e.printStackTrace();
			hasError = true;
			errorMsg += e.toString();
		} finally {
			if (hasError == true) {
				// 20210125 - aminkamilm: Use message properties
				errorMsg += msgSource.getMessage("msgReportErr", new Object[] {}, Locale.getDefault());
				model.put("error", errorMsg);
				model.remove("success");
			} else {
				// 20210125 - aminkamilm: Use message properties
				model.put("success", msgSource.getMessage("msgReportSuccess", new Object[] {}, Locale.getDefault()));
				model.remove("error");
			}
			
			//FSGS) Kent 26/2/2021 Add/changed - Add Function executed log START
			// searchString for TRX_HISTOR_LOG
			String pdId = "";
			String cntry = "";
			String searchString = "";
			for (String value : productId) {
				pdId = pdId + value + ",";
			}
			for (String value : country) {
				cntry = cntry + value + ",";
			}
			pdId = pdId.substring(0, pdId.length() - 1);
			cntry = cntry.substring(0, cntry.length() - 1);
			
			searchString = "Product Id: " + pdId + "," + "Country: " + cntry;
			
			trxHistServ.addTrxHistory(new Date(), "Print Final Permissibility Report", request.getRemoteUser(),
					CommonConstants.FUNCTION_TYPE_DOWNLOAD, reportName,
					CommonConstants.RECORD_TYPE_ID_PERMS_COMPL, searchString);
			//FSGS) Kent 26/2/2021 Add/changed - Add Function executed log END
		}

		return new ModelAndView("/main/product/compliance", model);
	}
}
