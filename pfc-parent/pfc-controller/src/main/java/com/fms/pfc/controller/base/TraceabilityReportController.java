package com.fms.pfc.controller.base;

import java.io.InputStream;
import java.io.OutputStream;
import java.sql.Connection;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.sql.DataSource;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.context.annotation.SessionScope;
import org.springframework.web.servlet.ModelAndView;

import com.fms.pfc.common.Authority;
import com.fms.pfc.common.CommonConstants;
import com.fms.pfc.domain.dto.LabelAndValueDto;
import com.fms.pfc.domain.model.TrxHistoryLog;
import com.fms.pfc.service.api.base.TrxHisService;
import com.fms.pfc.service.api.main.ProductRecipeService;
import com.fms.pfc.service.api.main.RawMaterialService;

import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;

@Controller
@SessionScope
public class TraceabilityReportController {

	private final Logger logger = LoggerFactory.getLogger(TraceabilityReportController.class);

	private Authority auth;
	private TrxHisService trxHisServ;
	private MessageSource msgSource;
	private RawMaterialService rmServ;
	private ProductRecipeService prServ;
	private DataSource ds;

	private Map<String, Object> model = new HashMap<String, Object>();

	@Autowired
	public TraceabilityReportController(Authority auth, TrxHisService trxHisServ, MessageSource msgSource,
			RawMaterialService rmServ, ProductRecipeService prServ, DataSource ds) {
		super();
		this.auth = auth;
		this.trxHisServ = trxHisServ;
		this.msgSource = msgSource;
		this.rmServ = rmServ;
		this.prServ = prServ;
		this.ds = ds;
	}

	@GetMapping("/base/activity/traceabilityReport")
	public ModelAndView loadAuthReport(HttpServletRequest request, HttpServletResponse response) {
		model = auth.onPageLoad(model, request);
		model.put("years", trxHisServ.getTrxYearsLabelAndValue());
		model.put("rmOpt",true);
		return new ModelAndView("/base/activity/traceabilityReport", model);
	}

	@PostMapping("/base/activity/traceabilityReport")
	public ModelAndView printReport(HttpServletRequest request, HttpServletResponse response,
			@RequestParam(name = "option") String option, @RequestParam(name = "selCategory") int selCategory,
			@RequestParam(name = "year") int year) {

		logger.debug("printReport() option={},selCategory={},year={}", option, selCategory, year);
		String reportName = msgSource.getMessage(CommonConstants.REPORT_ID_TRACE_LOG, null, Locale.getDefault());
		String reportTamplate = "/report/" + CommonConstants.REPORT_ID_TRACE_LOG + ".jrxml";

		// clear message
		{
			model.remove("error");
			model.remove("success");
		}

		boolean hasError = false;
		String errorMsg = "";

		// check if trx_history exists
		List<TrxHistoryLog> trx = getTrxList(option, selCategory, year);

		if (null == trx || trx.isEmpty()) {
			errorMsg = msgSource.getMessage("msgReportNoData", null, Locale.getDefault());
			model.put("error", errorMsg);

			holdUiData(option, selCategory, year);
			
			return new ModelAndView("/base/activity/traceabilityReport", model);
		}

		List<Integer> logIDs = trx.stream().map(arg0 -> arg0.getLogId()).collect(Collectors.toList());

		try {
			generate(request, response, logIDs, reportName, reportTamplate);
		} catch (Exception e) {
			hasError = true;
			errorMsg += e.toString();

		} finally {

			if (hasError) {
				errorMsg += " " + msgSource.getMessage("msgReportErr", null, Locale.getDefault());
				model.put("error", errorMsg);
			} else {
				model.put("success", msgSource.getMessage("msgReportSuccess", null, Locale.getDefault()));
				addTrxHist(request, option, selCategory, year, reportName);
			}

		}

		return new ModelAndView("/base/activity/traceabilityReport", model);
	}

	private void holdUiData(String option, int selCategory, int year) {
		if (option.equals(String.valueOf(CommonConstants.RECORD_TYPE_ID_RAW_MATL))) {
			model.put("rmOpt", true);
			model.put("prOpt", false);
		} else if (option.equals(String.valueOf(CommonConstants.RECORD_TYPE_ID_PROD_RECP))) {
			model.put("prOpt", true);
			model.put("rmOpt", false);
		}

		model.put("selCategory", selCategory);
		model.put("year", year);
	}

	private List<TrxHistoryLog> getTrxList(String option, int selCategory, int year) {
		List<TrxHistoryLog> trx = trxHisServ.findByCriteria("", "", "", String.valueOf(selCategory), "",
				Integer.valueOf(option), Arrays.asList(CommonConstants.FUNCTION_TYPE_INSERT,
						CommonConstants.FUNCTION_TYPE_UPDATE, CommonConstants.FUNCTION_TYPE_DELETE));

		trx = trx.stream().filter(arg0 -> convertToLocalDateViaInstant(arg0.getLogDateTime()).getYear() == year)
				.collect(Collectors.toList());

		return trx;
	}

	@SuppressWarnings("unchecked")
	private void addTrxHist(HttpServletRequest request, String option, int selCategory, int year, String reportName) {
		StringBuffer sb = new StringBuffer();
		sb.append("Search Criteria: ");
		sb.append("Category=").append(option.isEmpty() ? "<ALL>" : (String) model.get("columnName")).append(", ");
		sb.append((String) model.get("columnName")).append("=")
				.append(selCategory <= 0 ? "<ALL>"
						: ((List<LabelAndValueDto>) model.get("items")).stream()
								.filter(arg0 -> arg0.getValue() == selCategory).collect(Collectors.toList()).get(0)
								.getLabel())
				.append(", ");
		sb.append("Year=").append(year <= 0 ? "<ALL>" : year);

		trxHisServ.saveTrxHistoryLog(new Date(), "Download Traceability Report", request.getRemoteUser(),
				CommonConstants.FUNCTION_TYPE_DOWNLOAD, reportName, CommonConstants.RECORD_TYPE_ID_TRACE_LOG,
				sb.toString());
	}

	@RequestMapping(value = "/base/activity/onCategoryChange", method = RequestMethod.GET)
	public String getRegCatItemsByCountry(@RequestParam(name = "option") String option, HttpServletRequest request,
			HttpSession session, ModelMap modelMap) {

		List<LabelAndValueDto> lblVal = new ArrayList<LabelAndValueDto>();
		String colName = "Raw Material";
		if (StringUtils.equals(option, String.valueOf(CommonConstants.RECORD_TYPE_ID_RAW_MATL))) {
			lblVal = rmServ.getRmNameLabelAndValue();
		} else if (StringUtils.equals(option, String.valueOf(CommonConstants.RECORD_TYPE_ID_PROD_RECP))) {
			lblVal = prServ.getPrNameLabelAndValue();
			colName = "Product Recipe";
		}
		lblVal.sort(Comparator.comparing(LabelAndValueDto::getLabel));

		model.put("items", lblVal);
		model.put("columnName", colName);

		modelMap.addAllAttributes(model);

		return "/base/activity/traceabilityReport :: #rmPrName";
	}

	private void generate(HttpServletRequest request, HttpServletResponse response, List<Integer> logIDs,
			String reportName, String reportTamplate) throws Exception {
		Connection conn = ds.getConnection();

		InputStream mainReport = getClass().getResourceAsStream(reportTamplate);
		JasperReport main = JasperCompileManager.compileReport(mainReport);

		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("userId", request.getRemoteUser());
		parameters.put("logIDList", logIDs);
		parameters.put("columnName", (String) model.get("columnName"));

		JasperPrint jasperPrint = JasperFillManager.fillReport(main, parameters, conn);

		Date date = java.util.Calendar.getInstance().getTime();
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		long timeInMillis = cal.getTimeInMillis();

		StringBuffer fileName = new StringBuffer();
		fileName.append(reportName).append("_").append(timeInMillis).append(".")
				.append(CommonConstants.REPORT_FORMAT_PDF.toLowerCase());

		response.setContentType("application/x-download");
		response.setHeader(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + fileName.toString() + "\"");

		OutputStream outputStream = response.getOutputStream();
		JasperExportManager.exportReportToPdfStream(jasperPrint, outputStream);

		conn.close();

	}

	private LocalDate convertToLocalDateViaInstant(Date dateToConvert) {
		return dateToConvert.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
	}

}
