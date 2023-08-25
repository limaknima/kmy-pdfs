package com.fms.pfc.controller.base;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.context.annotation.SessionScope;
import org.springframework.web.servlet.ModelAndView;

import com.fms.pfc.common.Authority;
import com.fms.pfc.common.CommonConstants;
import com.fms.pfc.domain.model.UsrActivityLog;
import com.fms.pfc.service.api.base.LoginService;
import com.fms.pfc.service.api.base.OrganizationService;
import com.fms.pfc.service.api.base.TrxHisService;
import com.fms.pfc.service.api.base.UsrActivityService;
import com.fms.pfc.validation.common.CommonValidation;

@Controller
@SessionScope
public class AuthReportController {
	
	private Authority auth;	
	private LoginService logServ;	
	private OrganizationService orgServ;	
	private UsrActivityService userActServ;	
	private CommonValidation commonValServ;	
	private MessageSource msgSource;
	private TrxHisService trxHistServ;

	private XSSFWorkbook workbook;
	private XSSFSheet sheet;
	private Map<String, Object> model = new HashMap<String, Object>();

	@Autowired
	public AuthReportController(Authority auth, LoginService logServ, OrganizationService orgServ,
			UsrActivityService userActServ, CommonValidation commonValServ, MessageSource msgSource,
			TrxHisService trxHistServ) {
		super();
		this.auth = auth;
		this.logServ = logServ;
		this.orgServ = orgServ;
		this.userActServ = userActServ;
		this.commonValServ = commonValServ;
		this.msgSource = msgSource;
		this.trxHistServ = trxHistServ;
	}

	@GetMapping("/base/activity/authReport")
	public ModelAndView loadAuthReport(HttpServletRequest request, HttpServletResponse response) {

		//Map<String, Object> model = new HashMap<String, Object>();

		clearForm(model);

		model = auth.onPageLoad(model, request);
		auth.isAuthUrl(request, response);
		model.put("userList", logServ.searchAllActiveUser());
		model.put("orgList", orgServ.searchOrganization("", "", "", "", ""));

		//request.getSession().setAttribute("sessionModel", model);

		return new ModelAndView("/base/activity/authReport", model);
	}

	@PostMapping("/base/activity/authReport")
	public ModelAndView PrintAuthReport(HttpServletRequest request, HttpServletResponse response,
			@RequestParam(name = "userSession") String userSession, @RequestParam(name = "userId") String userId,
			@RequestParam(name = "orgId") String orgId, @RequestParam(name = "dateFr") String dateFr,
			@RequestParam(name = "dateTo") String dateTo, HttpSession session) {

		//Map<String, Object> model = (Map<String, Object>) session.getAttribute("sessionModel");

		clearForm(model);

		boolean error = false;
		List<UsrActivityLog> usrActLogList = userActServ.searchActivityLog(userSession, userId, orgId, dateFr, dateTo);
		String errorMsg = commonValServ.validateDateRange(dateFr, dateTo, "Date from", "Date to");
		if (errorMsg.length() != 0) {
			model.put("error", errorMsg);
			error = true;
		}

		if (usrActLogList.isEmpty() || usrActLogList.size() == 0) {
			model.put("error", msgSource.getMessage("msgReportNoData", new Object[] {}, Locale.getDefault()));
			error = true;
		}

		if (error) {
			holdValue(userSession, userId, orgId, dateFr, dateTo, model);
			return new ModelAndView("/base/activity/authReport", model);
		}

		response.setContentType("application/octet-stream");
		DateFormat dateFormatter = new SimpleDateFormat("yyyyMMddHHmmss");
		String currentDateTime = dateFormatter.format(new Date());

		String reportName = msgSource.getMessage(CommonConstants.REPORT_ID_AUTH_LOG, new Object[] {},
				Locale.getDefault());

		String headerKey = "Content-Disposition";
		String headerValue = "attachment; filename=" + reportName + "_" + currentDateTime + "."
				+ CommonConstants.REPORT_FORMAT_XLSX.toLowerCase();
		response.setHeader(headerKey, headerValue);

		export(response, usrActLogList);

		// FSGS) Kent 26/2/2021 Add/changed - Add Function executed log START
		StringBuffer sb = new StringBuffer();
		sb.append("Search Criteria: ");
		sb.append("User Session=").append(userSession.isEmpty() ? "<ALL>" : userSession).append(", ");
		sb.append("User ID=").append(userId.isEmpty() ? "<ALL>" : userId).append(", ");
		sb.append("Organization=").append(orgId.isEmpty() ? "<ALL>" : orgId).append(", ");
		sb.append("Date From=").append(dateFr.isEmpty() ? "<ALL>" : dateFr).append(", ");
		sb.append("Date To=").append(dateTo.isEmpty() ? "<ALL>" : dateTo);

		trxHistServ.addTrxHistory(new Date(), "Download Authentication Report", request.getRemoteUser(),
				CommonConstants.FUNCTION_TYPE_DOWNLOAD, reportName, CommonConstants.RECORD_TYPE_ID_ACTIVITY_LOG,
				sb.toString());
		// FSGS) Kent 26/2/2021 Add/changed - Add Function executed log END

		return new ModelAndView("/base/activity/authReport", model);
	}

	private void holdValue(String userSession, String userId, String orgId, String dateFr, String dateTo,
			Map<String, Object> model) {

		model.put("userSession", userSession);
		model.put("userId", userId);
		model.put("orgId", orgId);
		model.put("dateFr", dateFr);
		model.put("dateTo", dateTo);
	}

	private void clearForm(Map<String, Object> model) {

		model.remove("success");
		model.remove("error");
		model.remove("userSession");
		model.remove("userId");
		model.remove("orgId");
		model.remove("dateFr");
		model.remove("dateTo");
	}

	private void writeHeaderLine() {
		workbook = new XSSFWorkbook();
		sheet = workbook.createSheet("Authentication Report");

		Row row = sheet.createRow(0);

		CellStyle style = workbook.createCellStyle();
		XSSFFont font = workbook.createFont();
		font.setBold(true);
		font.setFontHeight(12);
		style.setFont(font);

		createCell(row, 0, "User ID", style);
		createCell(row, 1, "Organization", style);
		createCell(row, 2, "Username", style);
		createCell(row, 3, "Login Timestamp", style);
		createCell(row, 4, "Logout Timestamp", style);
		createCell(row, 5, "Login Status", style);

	}

	private void createCell(Row row, int columnCount, Object value, CellStyle style) {
		sheet.autoSizeColumn(columnCount);
		Cell cell = row.createCell(columnCount);
		if (value instanceof Integer) {
			cell.setCellValue((Integer) value);
		} else if (value instanceof Boolean) {
			cell.setCellValue((Boolean) value);
		} else {
			cell.setCellValue((String) value);
		}
		cell.setCellStyle(style);
	}

	private void writeDataLines(List<UsrActivityLog> usrActLogList) {
		int rowCount = 1;

		CellStyle style = workbook.createCellStyle();
		XSSFFont font = workbook.createFont();
		font.setFontHeight(11);
		style.setFont(font);

		for (UsrActivityLog usrActLog : usrActLogList) {
			Row row = sheet.createRow(rowCount++);
			int columnCount = 0;

			DateFormat dateFormatter = new SimpleDateFormat("dd/MM/yyy hh:mm:ss a");
			String loginStatus = "";
			String loginDate = "";
			String logoutDate = "";

			if (usrActLog.getSuccessFlag().equals("Y")) {
				loginStatus = "Successful";
			} else {
				loginStatus = "Unsuccessful";
			}

			try {
				loginDate = dateFormatter.format(usrActLog.getLoginDate());
			} catch (Exception e) {
				loginDate = "";
			}

			try {
				logoutDate = dateFormatter.format(usrActLog.getLogoutDate());
			} catch (Exception e) {
				logoutDate = "";
			}

			createCell(row, columnCount++, usrActLog.getUserId(), style);
			createCell(row, columnCount++, usrActLog.getOrgId(), style);
			createCell(row, columnCount++, usrActLog.getUserName(), style);
			createCell(row, columnCount++, loginDate, style);
			createCell(row, columnCount++, logoutDate, style);
			createCell(row, columnCount++, loginStatus, style);

		}
	}

	private void export(HttpServletResponse response, List<UsrActivityLog> usrActLogList) {
		writeHeaderLine();
		writeDataLines(usrActLogList);

		try {
			ServletOutputStream outputStream = response.getOutputStream();
			workbook.write(outputStream);
			workbook.close();

			outputStream.close();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

}
