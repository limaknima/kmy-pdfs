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
import java.util.stream.Collectors;

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
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.context.annotation.SessionScope;
import org.springframework.web.servlet.ModelAndView;

import com.fms.pfc.common.Authority;
import com.fms.pfc.common.CommonConstants;
import com.fms.pfc.domain.model.main.PrNameDto;
import com.fms.pfc.domain.model.main.ProductRecipe;
import com.fms.pfc.service.api.base.TrxHisService;
import com.fms.pfc.service.api.main.ProductRecipeService;
import com.fms.pfc.service.api.main.RawMaterialService;

import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.export.ooxml.JRXlsxExporter;
import net.sf.jasperreports.export.SimpleExporterInput;
import net.sf.jasperreports.export.SimpleOutputStreamExporterOutput;

@Controller
@SessionScope
public class ProductReportController {
	
	private final Logger logger = LoggerFactory.getLogger(ProductReportController.class);
	
	private RawMaterialService rawMatlServ;
	private ProductRecipeService prodRecpServ;
	private TrxHisService trxHistServ;
	private Authority auth;
	private MessageSource msgSource;
	private DataSource ds;
	
	private Map<String, Object> model = new HashMap<String, Object>();

	@Autowired
	public ProductReportController(RawMaterialService rawMatlServ, ProductRecipeService prodRecpServ,
			TrxHisService trxHistServ, Authority auth, MessageSource msgSource, DataSource ds) {
		super();
		this.rawMatlServ = rawMatlServ;
		this.prodRecpServ = prodRecpServ;
		this.trxHistServ = trxHistServ;
		this.auth = auth;
		this.msgSource = msgSource;
		this.ds = ds;
	}

	@GetMapping("/main/report/productReport")
	public ModelAndView getProductReport(HttpServletRequest request, HttpServletResponse response) {

		//Map<String, Object> model = new HashMap<String, Object>();

		model = auth.onPageLoad(model, request);
		auth.isAuthUrl(request, response);
		onLoad(model);	
		
		//request.getSession().setAttribute("sessionModel", model);

		return new ModelAndView("/main/report/productReport", model);
	}

	private Map<String, Object> onLoad(Map<String, Object> model) {
		
		List<ProductRecipe> prList = prodRecpServ.searchProductRecipe("", "", "", "", 0, 0);
		List<ProductRecipe> sortedPrCode = prList.stream().sorted((o1, o2)->o1.getPrCode().
                					compareTo(o2.getPrCode())).collect(Collectors.toList());
		List<ProductRecipe> sortedPrName = prList.stream().sorted((o1, o2)->o1.getPrName().
									compareTo(o2.getPrName())).collect(Collectors.toList());		
		List<PrNameDto> allPrOtherNames = prodRecpServ.findAllPrNames(0, 0, "");
		allPrOtherNames = allPrOtherNames.stream().filter(arg0 -> (arg0.getType() != 1)).collect(Collectors.toList());
		
		//Set dropdown item
		model.put("prCodeItem", sortedPrCode);
		model.put("prNameItem", sortedPrName);
		model.put("prOtherNameItem", allPrOtherNames);
		model.put("rmItem", rawMatlServ.searchRawMaterial("", "", 0, 0));
		model.put("imPrItem", prodRecpServ.searchIntermediate());
		model.put("confirmHeader",msgSource.getMessage("msgReportConfirmHeader", new Object[] {}, Locale.getDefault()));
		model.put("confirmMsg", msgSource.getMessage("msgReportConfirm", new Object[] {}, Locale.getDefault()));
		//Default radio button for preview = 1
		//1 = PDF, 2 = Excel
		model.put("previewType", 1);

		return model;
	}
	
	@PostMapping(value = "/main/report/productReport", params = "print=true")
	public ModelAndView printReport(@RequestParam(name = "prCode", required = false) String prCode,
			@RequestParam(name = "prName", required = false) String prName,
			@RequestParam(name = "prOtherName", required = false) String prOtherName,
			@RequestParam(name = "rmName", required = false) String rmName,
			@RequestParam(name = "impName", required = false) String impName, 
			@RequestParam(name = "previewType", required = false) int previewType, HttpServletRequest request,
			HttpServletResponse response, ProductRecipe prodRecipe, BindingResult result, HttpSession session) {

		//Map<String, Object> model = (Map<String, Object>) session.getAttribute("sessionModel");
		boolean hasError = false;
		String errorMsg = "";
		List<Integer> prIdList = new ArrayList<Integer>();
		// Get list of products
		
		logger.info("printReport() prCode={},prName={},rmName={},impName={},prOtherName={}",prCode,prName,rmName,impName,prOtherName);
		
		List<ProductRecipe> productList = prodRecpServ.searchProductReport(prCode, prName, rmName, impName,
				prOtherName);

		// Validate data size - do not proceed to report generation if no data found
		if (productList.isEmpty() || productList.size() == 0) {
			model.put("error", msgSource.getMessage("msgReportNoData", new Object[] {}, Locale.getDefault()));
			model.remove("success");
			putBackData(prCode, prName, rmName, impName, prOtherName, model);
			return new ModelAndView("/main/report/productReport", model);
		} else {

			for (int i = 0; i < productList.size(); i++) {
				prIdList.add(productList.get(i).getPrId());				
			}
		}

		// Print report
		String reportId = CommonConstants.REPORT_ID_PROD_RECP;		
		String reportName = msgSource.getMessage(reportId, new Object[] {}, Locale.getDefault());
		try {

			generate(request, response, prIdList, previewType, reportId, reportName);

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
				getProductReport(request, response);
			}
			// FSGS) Kent 26/2/2021 Add/changed - Add Function executed log START
			StringBuffer sb = new StringBuffer();
			sb.append("Print Criteria: ");
			sb.append("Product Recipe TS No=").append(prCode.isEmpty() ? "<ALL>" : prCode).append(", ");
			sb.append("Product Recipe Name=").append(prName.isEmpty() ? "<ALL>" : prName).append(", ");
			sb.append("Other Name=").append(prOtherName.isEmpty() ? "<ALL>" : prOtherName).append(", ");
			sb.append("Raw Material Name=").append(rmName.isEmpty() ? "<ALL>" : rmName).append(", ");
			sb.append("Intermediate Product Name=").append(impName.isEmpty() ? "<ALL>" : impName);

			trxHistServ.addTrxHistory(new Date(), "Print Product Recipe Report", request.getRemoteUser(),
					CommonConstants.FUNCTION_TYPE_DOWNLOAD, reportName, CommonConstants.RECORD_TYPE_ID_REPORT_PR,
					sb.toString());
			// FSGS) Kent 26/2/2021 Add/changed - Add Function executed log END
		}
		return new ModelAndView("/main/report/productReport", model);
	}
	
	private void generate(HttpServletRequest request, HttpServletResponse response, List<Integer> pridList, 
			int previewType, String reportId, String reportName)
			throws Exception {
		
		Map<String, Object> parameters = new HashMap<String, Object>();
		Connection conn = ds.getConnection();
		
		StringBuffer fileName = new StringBuffer();		
		
		Date date = java.util.Calendar.getInstance().getTime();
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		long timeInMillis = cal.getTimeInMillis();
		
		parameters.put("pridList", pridList);
		parameters.put("userId", request.getRemoteUser());

		if (previewType == CommonConstants.REPORT_PREVIEW_PDF_TYPE) {

			//Get pdf jrxml file
			InputStream pdfReport = getClass().getResourceAsStream("/report/" + reportId + ".jrxml");
			JasperReport main = JasperCompileManager.compileReport(pdfReport);
			JasperPrint jasperPrint = JasperFillManager.fillReport(main, parameters, conn);
			
			fileName.append(reportName).append("_").append(timeInMillis).append(".")
					.append(CommonConstants.REPORT_FORMAT_PDF.toLowerCase());

			response.setContentType("application/x-download");
			response.setHeader(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + fileName.toString() + "\"");
			OutputStream outputStream = response.getOutputStream();
			//Export in pdf format
			JasperExportManager.exportReportToPdfStream(jasperPrint, outputStream);
			
		} else if (previewType == CommonConstants.REPORT_PREVIEW_EXCEL_TYPE) {
			
			//Get excel jrxml file
			InputStream excelReport = getClass().getResourceAsStream("/report/" + reportId + "(xlsx).jrxml");
			JasperReport main = JasperCompileManager.compileReport(excelReport);
			JasperPrint jasperPrint = JasperFillManager.fillReport(main, parameters, conn);
			
			fileName.append(reportName).append("_").append(timeInMillis).append(".")
			.append(CommonConstants.REPORT_FORMAT_XLSX.toLowerCase());

			response.setContentType("application/x-download");
			response.setHeader(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + fileName.toString() + "\"");
			OutputStream outputStream = response.getOutputStream();

			//Export in excel format
			JRXlsxExporter exporter = new JRXlsxExporter();			
			exporter.setExporterInput(new SimpleExporterInput(jasperPrint));			
			exporter.setExporterOutput(new SimpleOutputStreamExporterOutput(outputStream));			
			exporter.exportReport();	               			

		}
		conn.close();
	}
	
	private Map<String, Object> putBackData(String prCode, String prName, String rmName, String impName,
			String prOtherName, Map<String, Object> model) {

		model.put("prCode", prCode);
		model.put("prName", prName);
		model.put("prOtherName", prOtherName);
		model.put("rmName", rmName);
		model.put("impName", impName);

		return model;
	}

}
