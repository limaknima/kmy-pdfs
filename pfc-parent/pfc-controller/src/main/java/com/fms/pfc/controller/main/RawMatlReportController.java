package com.fms.pfc.controller.main;

import java.io.InputStream;
import java.io.OutputStream;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.sql.DataSource;

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
import com.fms.pfc.service.api.base.CountryService;
import com.fms.pfc.service.api.base.ManufacturerService;
import com.fms.pfc.service.api.base.TrxHisService;
import com.fms.pfc.service.api.main.RawMaterialService;
import com.fms.pfc.service.api.main.RawMatlReportService;
import com.fms.pfc.validation.common.CommonValidation;

import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;

@Controller
@SessionScope
public class RawMatlReportController {
	
	private final Logger logger = LoggerFactory.getLogger(RawMatlReportController.class);

	private CountryService refCountryServ;
	private ManufacturerService manfServ;
	private RawMatlReportService rawMatlRepServ;
	private CommonValidation commonValServ;
	private Authority auth;
	private MessageSource msgSource;
	private DataSource ds;
	private RawMaterialService rawMatlServ;
	private TrxHisService trxHistServ;
	
	private Map<String, Object> model = new HashMap<String, Object>();

	@Autowired
	public RawMatlReportController(CountryService refCountryServ, ManufacturerService manfServ,
			RawMatlReportService rawMatlRepServ, CommonValidation commonValServ, Authority auth,
			MessageSource msgSource, DataSource ds, RawMaterialService rawMatlServ, TrxHisService trxHistServ) {
		super();
		this.refCountryServ = refCountryServ;
		this.manfServ = manfServ;
		this.rawMatlRepServ = rawMatlRepServ;
		this.commonValServ = commonValServ;
		this.auth = auth;
		this.msgSource = msgSource;
		this.ds = ds;
		this.rawMatlServ = rawMatlServ;
		this.trxHistServ = trxHistServ;
	}

	@GetMapping("/main/report/rawMatlReport")
	public ModelAndView getRawMatlReport(HttpServletRequest request, HttpServletResponse response) {

		//Map<String, Object> model = new HashMap<String, Object>();

		model = auth.onPageLoad(model, request);
		auth.isAuthUrl(request, response);
		onLoad(model);

		//request.getSession().setAttribute("sessionModel", model);

		return new ModelAndView("/main/report/rawMatlReport", model);
	}

	private Map<String, Object> onLoad(Map<String, Object> model) {
		model.put("countryItems", refCountryServ.searchCountry("", "", "", "", "", String.valueOf(CommonConstants.FLAG_YES)));
		model.put("manufacturerItems", manfServ.searchVendor(1));
		model.put("supplierItems", manfServ.searchVendor(2));
		model.put("rawMatlItems", rawMatlServ.searchRawMaterial("", "", 0, 0));
		model.put("confirmHeader",
				msgSource.getMessage("msgReportConfirmHeader", new Object[] {}, Locale.getDefault()));
		model.put("confirmMsg", msgSource.getMessage("msgReportConfirm", new Object[] {}, Locale.getDefault()));

		return model;
	}

	private Map<String, Object> clearForm(Map<String, Object> model) {
		model.remove("rawMatl");
		model.remove("tsNo");
		model.remove("supplier");
		model.remove("manufacturer");
		model.remove("country");
		model.remove("fema");
		model.remove("jecfa");
		model.remove("ins");

		return model;
	}

	private Map<String, Object> putBackData(String tsNo, int supplier, int manufacturer, String country, String fema,
			String jecfa, String ins, int rawMatl, String supplautocomplete, String mfrautocomplete, Map<String, Object> model) {
		model.put("rawMatl", rawMatl);
		model.put("tsNo", tsNo);
		model.put("supplier", supplier);
		model.put("supplautocomplete", supplautocomplete);
		model.put("manufacturer", manufacturer);
		model.put("mfrautocomplete", mfrautocomplete);
		model.put("country", country);
		model.put("fema", fema);
		model.put("jecfa", jecfa);
		model.put("ins", ins);

		return model;
	}

	private String saveFuncErrorCheck(String tsNo, String fema, String jecfa, String ins, Map<String, Object> model) {
		model.remove("error");
		model.remove("success");
		String errorMsg = "";

		errorMsg += commonValServ.validateInputLength(tsNo, 10, "TS No");
		errorMsg += commonValServ.validateInputLength(fema, 10, "FEMA");
		errorMsg += commonValServ.validateInputLength(jecfa, 10, "JECFA");
		errorMsg += commonValServ.validateInputLength(ins, 10, "INS");

		return errorMsg;
	}

	private List<Integer> getRawMatlIds(int rmId, String tsNo, int manufacturerId, int supplierId, String countryId,
			String fema, String jecfa, String insNo) {

		List<Integer> rmidList = new ArrayList<Integer>();
		rmidList = rawMatlRepServ.getRawMatlIds(rmId, tsNo, manufacturerId, supplierId, countryId, fema, jecfa, insNo);
		return rmidList;
	}

	@PostMapping(value = "/main/report/rawMatlReport", params = "print=true")
	public ModelAndView printReport(@RequestParam(name = "tsNo") String tsNo,
			@RequestParam(name = "supplier", required = false, defaultValue = "0") int supplier,
			@RequestParam(name = "mfrautocomplete") String mfrautocomplete,
			@RequestParam(name = "manufacturer", required = false, defaultValue = "0") int manufacturer,
			@RequestParam(name = "supplautocomplete") String supplautocomplete,
			@RequestParam(name = "country") String country, @RequestParam(name = "fema") String fema,
			@RequestParam(name = "jecfa") String jecfa, @RequestParam(name = "ins") String ins,
			@RequestParam(name = "rawMatl") int rawMatl, HttpServletRequest request, HttpServletResponse response,
			HttpSession session) {
		
		logger.debug("printReport() supplier={},supplautocomplete={},manufacturer={},mfrautocomplete={}", supplier,
				supplautocomplete, manufacturer, mfrautocomplete);

		//Map<String, Object> model = (Map<String, Object>) session.getAttribute("sessionModel");
		boolean hasError = false;

		String errorMsg = saveFuncErrorCheck(tsNo, fema, jecfa, ins, model);

		if (errorMsg.length() != 0) {
			model.put("error", errorMsg);
			putBackData(tsNo, supplier, manufacturer, country, fema, jecfa, ins, rawMatl, supplautocomplete, mfrautocomplete, model);
		} else {
			// Initialize objects for report

			// FSGS) Hani 11/3/2021 Add/Change .trim() to search criteria START
			List<Integer> rmidList = getRawMatlIds(rawMatl, tsNo.trim(), manufacturer, supplier, country, fema.trim(),
					jecfa.trim(), ins.trim());
			// FSGS) Hani 11/3/2021 Add/Change .trim() to search criteria END

			if (rmidList.isEmpty()) {
				errorMsg = msgSource.getMessage("msgReportNoData", new Object[] {}, Locale.getDefault());
				model.put("error", errorMsg);
				putBackData(tsNo, supplier, manufacturer, country, fema, jecfa, ins, rawMatl, supplautocomplete, mfrautocomplete, model);
				return new ModelAndView("/main/report/rawMatlReport", model);
			}

			// Print report
			try {
				generate(request, response, rmidList);

			} catch (Exception e) {
				e.printStackTrace();
				hasError = true;
				errorMsg += e.toString();
			} finally {
				if (hasError == true) {
					errorMsg += msgSource.getMessage("msgReportErr", new Object[] {}, Locale.getDefault());
					putBackData(tsNo, supplier, manufacturer, country, fema, jecfa, ins, rawMatl, supplautocomplete, mfrautocomplete, model);
					model.put("error", errorMsg);
				} else {
					model.put("success",
							msgSource.getMessage("msgReportSuccess", new Object[] {}, Locale.getDefault()));
					clearForm(model);
					onLoad(model);
				}
				// FSGS) Kent 26/2/2021 Add/changed - Add Function executed log START
				StringBuffer sb = new StringBuffer();
				sb.append("Print Criteria: ");
				sb.append("Raw Material=").append(rawMatl).append(", ");
				sb.append("TS No=").append(tsNo.isEmpty() ? "<ALL>" : tsNo).append(", ");
				sb.append("Supplier=").append(supplier).append(", ");
				sb.append("Manufacturer=").append(manufacturer).append(", ");
				sb.append("Country of Origin=").append(country.isEmpty() ? "<ALL>" : country).append(", ");
				sb.append("FEMA=").append(fema.isEmpty() ? "<ALL>" : fema).append(", ");
				sb.append("JECFA=").append(jecfa.isEmpty() ? "<ALL>" : jecfa).append(", ");
				sb.append("INS=").append(ins.isEmpty() ? "<ALL>" : ins);

				String reportName = msgSource.getMessage(CommonConstants.REPORT_ID_RAW_MATL, new Object[] {},
						Locale.getDefault());

				trxHistServ.addTrxHistory(new Date(), "Print Raw Material Report", request.getRemoteUser(),
						CommonConstants.FUNCTION_TYPE_DOWNLOAD, reportName, CommonConstants.RECORD_TYPE_ID_REPORT_RM,
						sb.toString());
				// FSGS) Kent 26/2/2021 Add/changed - Add Function executed log END
			}
		}

		return new ModelAndView("/main/report/rawMatlReport", model);
	}

	private void generate(HttpServletRequest request, HttpServletResponse response, List<Integer> rmidList)
			throws Exception {
		Connection conn = ds.getConnection();

		String reportId = CommonConstants.REPORT_ID_RAW_MATL;

		InputStream mainReport = getClass().getResourceAsStream("/report/" + reportId + ".jrxml");
		InputStream subReport1 = getClass().getResourceAsStream("/report/" + reportId + "_1.jrxml");
		InputStream subReport2 = getClass().getResourceAsStream("/report/" + reportId + "_2.jrxml");
		JasperReport main = JasperCompileManager.compileReport(mainReport);
		JasperReport sub1 = JasperCompileManager.compileReport(subReport1);
		JasperReport sub2 = JasperCompileManager.compileReport(subReport2);

		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("rmidList", rmidList);
		parameters.put("userId", request.getRemoteUser());
		parameters.put("sub1", sub1);
		parameters.put("sub2", sub2);

		JasperPrint jasperPrint = JasperFillManager.fillReport(main, parameters, conn);

		Date date = java.util.Calendar.getInstance().getTime();
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		long timeInMillis = cal.getTimeInMillis();

		String reportName = msgSource.getMessage(reportId, new Object[] {}, Locale.getDefault());

		StringBuffer fileName = new StringBuffer();
		fileName.append(reportName).append("_").append(timeInMillis).append(".")
				.append(CommonConstants.REPORT_FORMAT_PDF.toLowerCase());

		response.setContentType("application/x-download");
		response.setHeader(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + fileName.toString() + "\"");

		OutputStream outputStream = response.getOutputStream();
		JasperExportManager.exportReportToPdfStream(jasperPrint, outputStream);

		conn.close();

	}
	
	@RequestMapping(value = "/main/report/mfrAutocomplete", method = RequestMethod.GET, headers = "Accept=application/json")
	@ResponseBody
	public List<LabelAndValueDto> mfrAutocomplete(
			@RequestParam(name = "term", required = false, defaultValue = "") String term,
			@RequestParam(name = "type") int type) {

		List<LabelAndValueDto> suggestions = manfServ.getVendorNameLabelAndValue(term, type);

		return suggestions;
	}

}
