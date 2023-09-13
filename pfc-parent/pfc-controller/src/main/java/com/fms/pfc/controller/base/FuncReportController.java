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
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.context.annotation.SessionScope;
import org.springframework.web.servlet.ModelAndView;

import com.fms.pfc.common.Authority;
import com.fms.pfc.common.CommonConstants;
import com.fms.pfc.domain.model.TranxHistory;
import com.fms.pfc.service.api.base.LoginService;
import com.fms.pfc.service.api.base.MenuRoleFunctionService;
import com.fms.pfc.service.api.base.OrganizationService;
import com.fms.pfc.service.api.base.TrxHisService;
import com.fms.pfc.validation.common.CommonValidation;

@Controller
@SessionScope
public class FuncReportController {
	
	private Authority auth;	
	private LoginService logServ;	
	private OrganizationService orgServ;	
	private TrxHisService trxHisServ;	
	private CommonValidation commonValServ;	
	private MessageSource msgSource;

	private XSSFWorkbook workbook;
	private XSSFSheet sheet;	
	private Map<String, Object> model = new HashMap<String, Object>();

	@Autowired
	public FuncReportController(Authority auth, LoginService logServ, OrganizationService orgServ,
			TrxHisService trxHisServ, CommonValidation commonValServ, MessageSource msgSource) {
		super();
		this.auth = auth;
		this.logServ = logServ;
		this.orgServ = orgServ;
		this.trxHisServ = trxHisServ;
		this.commonValServ = commonValServ;
		this.msgSource = msgSource;
	}

	@GetMapping("/base/activity/funcExeReport")
	public ModelAndView loadAuthReport(HttpServletRequest request, HttpServletResponse response) {

		//Map<String, Object> model = new HashMap<String, Object>();

		clearForm(model);

		model = auth.onPageLoad(model, request);
		auth.isAuthUrl(request, response);
		model.put("userList", logServ.searchAllActiveUser());
		//model.put("orgList", orgServ.searchOrganization("", "", "", "", ""));

		//request.getSession().setAttribute("sessionModel", model);

		return new ModelAndView("/base/activity/funcExeReport", model);
	}

	@PostMapping("/base/activity/funcExeReport")
	@ResponseBody
	public ModelAndView PrintAuthReport(HttpServletRequest request, HttpServletResponse response,
			@RequestParam(name = "userId") String userId,
			@RequestParam(name = "dateFr") String dateFr, @RequestParam(name = "dateTo") String dateTo,
			@RequestParam(name = "insert", required = false) String insert,
			@RequestParam(name = "update", required = false) String update,
			@RequestParam(name = "delete", required = false) String delete,
			@RequestParam(name = "view", required = false) String view,
			@RequestParam(name = "print", required = false) String print,
			@RequestParam(name = "search", required = false) String search, HttpSession session) {

		//Map<String, Object> model = (Map<String, Object>) session.getAttribute("sessionModel");
		
		String orgId = (String) model.get("loggedUserOrg");

		clearForm(model);

		int insertFlag = 0;
		int updateFlag = 0;
		int deleteFlag = 0;
		int viewFlag = 0;
		int printFlag = 0;
		int searchFlag = 0;

		if (insert != null) {
			insertFlag = CommonConstants.FUNCTION_TYPE_INSERT;
		}
		if (update != null) {
			updateFlag = CommonConstants.FUNCTION_TYPE_UPDATE;
		}
		if (delete != null) {
			deleteFlag = CommonConstants.FUNCTION_TYPE_DELETE;
		}
		if (view != null) {
			viewFlag = CommonConstants.FUNCTION_TYPE_VIEW;
		}
		if (print != null && !print.isEmpty()) {
			printFlag = CommonConstants.FUNCTION_TYPE_DOWNLOAD;
		}
		if (search != null && !search.isEmpty()) {
			searchFlag = CommonConstants.FUNCTION_TYPE_SEARCH;
		}
		if (insert == null && update == null && delete == null && view == null && print == null && search == null) {
			insertFlag = CommonConstants.FUNCTION_TYPE_INSERT;
			updateFlag = CommonConstants.FUNCTION_TYPE_UPDATE;
			deleteFlag = CommonConstants.FUNCTION_TYPE_DELETE;
			viewFlag = CommonConstants.FUNCTION_TYPE_VIEW;
			printFlag = CommonConstants.FUNCTION_TYPE_DOWNLOAD;
			searchFlag = CommonConstants.FUNCTION_TYPE_SEARCH;
		}

		boolean error = false;
		List<TranxHistory> tranxHis = trxHisServ.searchTranxHistory(userId, orgId, dateFr, dateTo, insertFlag,
				updateFlag, deleteFlag, viewFlag, printFlag, searchFlag);

		String errorMsg = commonValServ.validateDateRange(dateFr, dateTo, "Date from", "Date to");

		if (errorMsg.length() != 0) {
			model.put("error", errorMsg);
			error = true;
		}

		if (tranxHis.isEmpty() || tranxHis.size() == 0) {
			model.put("error", msgSource.getMessage("msgReportNoData", new Object[] {}, Locale.getDefault()));
			error = true;
		}

		if (error) {
			holdValue(userId, orgId, dateFr, dateTo, insertFlag, updateFlag, deleteFlag, viewFlag, printFlag,
					searchFlag, model);
			return new ModelAndView("/base/activity/funcExeReport", model);
		}

		response.setContentType("application/octet-stream");
		DateFormat dateFormatter = new SimpleDateFormat("yyyyMMddHHmmss");
		String currentDateTime = dateFormatter.format(new Date());

		String reportName = msgSource.getMessage(CommonConstants.REPORT_ID_FUNC_EXE, new Object[] {},
				Locale.getDefault());

		String headerKey = "Content-Disposition";
		String headerValue = "attachment; filename=" + reportName + "_" + currentDateTime + "."
				+ CommonConstants.REPORT_FORMAT_XLSX.toLowerCase();
		response.setHeader(headerKey, headerValue);

		export(response, tranxHis);

		// FSGS) Kent 26/2/2021 Add/changed - Add Function executed log START
		String functionType = insertFlag + "," + updateFlag + "," + deleteFlag + "," + viewFlag + "," + printFlag;
		StringBuffer sb = new StringBuffer();
		sb.append("Search Criteria: ");
		sb.append("User ID=").append(userId.isEmpty() ? "<ALL>" : userId).append(", ");
		sb.append("Organization=").append(orgId.isEmpty() ? "<ALL>" : orgId).append(", ");
		sb.append("Date From=").append(dateFr.isEmpty() ? "<ALL>" : dateFr).append(", ");
		sb.append("Date To=").append(dateTo.isEmpty() ? "<ALL>" : dateTo).append(", ");
		sb.append("Function Type=").append(functionType);

		trxHisServ.addTrxHistory(new Date(), "Download Function Executed Report", request.getRemoteUser(),
				CommonConstants.FUNCTION_TYPE_DOWNLOAD, reportName, CommonConstants.RECORD_TYPE_ID_FUNC_EXE,
				sb.toString());
		// FSGS) Kent 26/2/2021 Add/changed - Add Function executed log END

		return new ModelAndView("/base/activity/funcExeReport", model);
	}

	private void holdValue(String userId, String orgId, String dateFr, String dateTo, int insertFlag, int updateFlag,
			int deleteFlag, int viewFlag, int printFlag, int searchFlag, Map<String, Object> model) {

		model.put("userId", userId);
		model.put("orgId", orgId);
		model.put("dateFr", dateFr);
		model.put("dateTo", dateTo);
		if (insertFlag != 0) {
			model.put("insert", "insert");
		}
		if (updateFlag != 0) {
			model.put("update", "update");
		}
		if (deleteFlag != 0) {
			model.put("delete", "delete");
		}
		if (viewFlag != 0) {
			model.put("view", "view");
		}
		if (printFlag != 0) {
			model.put("print", "print");
		}
		if (searchFlag != 0) {
			model.put("search", "search");
		}
	}

	private void clearForm(Map<String, Object> model) {

		model.remove("success");
		model.remove("error");
		model.remove("userId");
		model.remove("orgId");
		model.remove("dateFr");
		model.remove("dateTo");
		model.remove("insert");
		model.remove("update");
		model.remove("delete");
		model.remove("view");
		model.remove("print");
		model.remove("search");
	}

	private void writeHeaderLine() {
		workbook = new XSSFWorkbook();
		sheet = workbook.createSheet(msgSource.getMessage(CommonConstants.REPORT_ID_FUNC_EXE, null, Locale.getDefault()));

		Row row = sheet.createRow(0);

		CellStyle style = workbook.createCellStyle();
		XSSFFont font = workbook.createFont();
		font.setBold(true);
		font.setFontHeight(12);
		style.setFont(font);

		createCell(row, 0, "Timestamp", style);
		createCell(row, 1, "Organization", style);
		createCell(row, 2, "User Name", style);
		createCell(row, 3, "Function Type", style);
		createCell(row, 4, "Activity Description", style);
		createCell(row, 5, "Record Identifier", style);

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

	private void writeDataLines(List<TranxHistory> trxHisList) {
		int rowCount = 1;

		CellStyle style = workbook.createCellStyle();
		XSSFFont font = workbook.createFont();
		font.setFontHeight(11);
		style.setFont(font);

		for (TranxHistory trxHis : trxHisList) {
			Row row = sheet.createRow(rowCount++);
			int columnCount = 0;

			DateFormat dateFormatter = new SimpleDateFormat("dd/MM/yyy hh:mm:ss a");
			String timestamp = "";
			String funcType = "";
			String identifier = "";

			try {
				timestamp = dateFormatter.format(trxHis.getLogDateTime());
			} catch (Exception e) {
				timestamp = "";
			}

			funcType = getFunctionTypeDesc(trxHis.getFuncType());
			identifier = (trxHis.getFuncType() == CommonConstants.FUNCTION_TYPE_SEARCH
					|| trxHis.getFuncType() == CommonConstants.FUNCTION_TYPE_DOWNLOAD) ? trxHis.getSearchStr()
							: trxHis.getRecordRef();

			createCell(row, columnCount++, timestamp, style);
			createCell(row, columnCount++, trxHis.getOrgId(), style);
			createCell(row, columnCount++, getUserName(trxHis.getUserId()), style);
			createCell(row, columnCount++, funcType, style);
			createCell(row, columnCount++, trxHis.getLogDesc(), style);
			createCell(row, columnCount++, identifier, style);

		}
	}

	private String getUserName(String userId) {
		return logServ.searchUser(userId.trim()).getUserName();
	}

	private String getFunctionTypeDesc(int type) {
		String funcTypeDesc = "";
		switch (type) {
		case CommonConstants.FUNCTION_TYPE_INSERT:
			funcTypeDesc = msgSource.getMessage("functionType_" + CommonConstants.FUNCTION_TYPE_INSERT, null,
					Locale.getDefault());
			break;
		case CommonConstants.FUNCTION_TYPE_UPDATE:
			funcTypeDesc = msgSource.getMessage("functionType_" + CommonConstants.FUNCTION_TYPE_UPDATE, null,
					Locale.getDefault());
			break;
		case CommonConstants.FUNCTION_TYPE_DELETE:
			funcTypeDesc = msgSource.getMessage("functionType_" + CommonConstants.FUNCTION_TYPE_DELETE, null,
					Locale.getDefault());
			break;
		case CommonConstants.FUNCTION_TYPE_VIEW:
			funcTypeDesc = msgSource.getMessage("functionType_" + CommonConstants.FUNCTION_TYPE_VIEW, null,
					Locale.getDefault());
			break;
		case CommonConstants.FUNCTION_TYPE_DOWNLOAD:
			funcTypeDesc = msgSource.getMessage("functionType_" + CommonConstants.FUNCTION_TYPE_DOWNLOAD, null,
					Locale.getDefault());
			break;
		case CommonConstants.FUNCTION_TYPE_SEARCH:
			funcTypeDesc = msgSource.getMessage("functionType_" + CommonConstants.FUNCTION_TYPE_SEARCH, null,
					Locale.getDefault());
			break;
		default:
			break;
		}
		return funcTypeDesc;
	}

	private void export(HttpServletResponse response, List<TranxHistory> trxHisList) {
		writeHeaderLine();
		writeDataLines(trxHisList);

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
