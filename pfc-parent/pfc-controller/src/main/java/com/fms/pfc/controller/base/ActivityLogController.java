package com.fms.pfc.controller.base;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.context.annotation.SessionScope;
import org.springframework.web.servlet.ModelAndView;

import com.fms.pfc.common.Authority;
import com.fms.pfc.common.CommonConstants;
import com.fms.pfc.common.MediaTypeUtils;
import com.fms.pfc.domain.model.ChangeHistory;
import com.fms.pfc.service.api.base.ChangeHisService;
import com.fms.pfc.service.api.base.DocumentTypeService;
import com.fms.pfc.service.api.base.TrxHisService;

@Controller
@SessionScope
public class ActivityLogController {
	
	private final Logger logger = LoggerFactory.getLogger(ActivityLogController.class);

	private ChangeHisService chgHisServ;
	private DocumentTypeService docTypeServ;
	private TrxHisService trxHistServ;
	private Authority auth;
	private MessageSource msgSource;
	private ServletContext servletContext;

	private Map<String, Object> model = new HashMap<String, Object>();

	private int matlId = 0;
	private int prRcpId = 0;
	private int recTypeId = 0;
	private String recRef = "";

	@Autowired
	public ActivityLogController(ChangeHisService chgHisServ, DocumentTypeService docTypeServ, TrxHisService trxHistServ,
			Authority auth, MessageSource msgSource, ServletContext servletContext) {
		super();
		this.chgHisServ = chgHisServ;
		this.docTypeServ = docTypeServ;
		this.trxHistServ = trxHistServ;
		this.auth = auth;
		this.msgSource = msgSource;
		this.servletContext = servletContext;
	}

	@GetMapping("/base/audit/activityLogList")
	public ModelAndView loadActivityLogList(HttpServletRequest request, HttpServletResponse response,
			@RequestParam(name = "rawMatlId", required = false, defaultValue = "0") int rawMatlId,
			@RequestParam(name = "prId", required = false, defaultValue = "0") int prId) {

		model = auth.onPageLoad(model, request);
		matlId = rawMatlId;
		model.put("rawMatlId", rawMatlId);
		prRcpId = prId;
		model.put("prId", prId);

		String recordRef = "";
		int recordTypeId = 0; // 20210406 - added filter by record type id
		if (rawMatlId != 0) {
			recordTypeId = CommonConstants.RECORD_TYPE_ID_RAW_MATL;
			recordRef = String.valueOf(rawMatlId);
		} else if (prId != 0) {
			recordTypeId = CommonConstants.RECORD_TYPE_ID_PROD_RECP;
			recordRef = String.valueOf(prId);
		}
		recTypeId = recordTypeId;
		recRef = recordRef;

		model.put("historyLogs", trxHistServ.searchTxHis("", "", "", recordRef, "", recordTypeId));

		return new ModelAndView("/base/audit/activityLogList", model);
	}
	
	@GetMapping("/base/audit/activityLogListNoNav")
	public ModelAndView loadActivityLogListNoNav(HttpServletRequest request, HttpServletResponse response,
			@RequestParam(name = "rawMatlId", required = false, defaultValue = "0") int rawMatlId,
			@RequestParam(name = "prId", required = false, defaultValue = "0") int prId) {

		model = auth.onPageLoad(model, request);
		model.putAll(loadActivityLogList(request, response, rawMatlId, prId).getModel());

		return new ModelAndView("/base/audit/activityLogListNoNav", model);
	}

	@GetMapping("/base/audit/viewActivityLog")
	public ModelAndView loadViewActivityLog(@RequestParam(name = "date") String chgHisDate,
			@RequestParam(name = "ref") String ref, HttpServletRequest request) {

		try {
			List<ChangeHistory> chgHisDataList = chgHisServ.searchChgHisDetail(chgHisDate, recTypeId, ref);
			List<ChangeHistory> chgHisData = new ArrayList<ChangeHistory>();
			chgHisData.addAll(chgHisDataList);
			
			logger.debug("loadViewActivityLog() chgHisDate={},recTypeId={},ref={},chgHisData.size={}", chgHisDate,
					recTypeId, ref, chgHisData.size());

			// Set value according to field name
			for (int i = 0; i < chgHisData.size(); i++) {
				convertVipdAndFinalStatus(chgHisData, i);
				convertRecordStatus(chgHisData, i);
				convertDocType(chgHisData, i);
			}
			
			chgHisData = checkRmManfSuppl(chgHisData);
			
			model.put("rawMatlId", matlId);
			model.put("prId", prRcpId);
			
			if(chgHisData.isEmpty()) {
				model.put("error", "No Activity Log Details found for "+chgHisDate);
			} else {
				model.put("historyLogsDetail", chgHisData);
				model.put("dateTime", chgHisData.get(0).getLogDateTime());
				model.put("userId", chgHisData.get(0).getUserId());				
			}

		} catch (Exception e) {
			e.printStackTrace();
			model.put("error", e.toString());
		}

		return new ModelAndView("/base/audit/viewActivityLog", model);
	}
	
	@GetMapping("/base/audit/viewActivityLogNoNav")
	public ModelAndView loadViewActivityLogNoNav(@RequestParam(name = "date") String chgHisDate,
			@RequestParam(name = "ref") String ref, HttpServletRequest request) {
		Map<String, Object> model = loadViewActivityLog(chgHisDate, ref, request).getModel();
		return new ModelAndView("/base/audit/viewActivityLogNoNav", model);
	}
	
	@Deprecated
	private void checkVipdRelated(List<ChangeHistory> chgHisData, int i) {
		if (chgHisData.get(i).getTableName().equals("Raw Material Manufacturer Supplier")) {
			if(chgHisData.get(i).getFieldName().equals("VIPD Date File Name")) {
				for (ChangeHistory changeHistory : chgHisData) {
					if(changeHistory.getFieldName().equals("VIPD Object")) {
						chgHisData.get(i).setOldContentObj(changeHistory.getOldContentObj());
						chgHisData.get(i).setNewContentObj(changeHistory.getNewContentObj());						
					}
				}
			}
			if(chgHisData.get(i).getFieldName().equals("Declaration Date File Name")) {
				for (ChangeHistory changeHistory : chgHisData) {
					if(changeHistory.getFieldName().equals("VIPD Annex2 Object")) {
						chgHisData.get(i).setOldContentObj(changeHistory.getOldContentObj());
						chgHisData.get(i).setNewContentObj(changeHistory.getNewContentObj());						
					}
				}
			}
		}
		
	}

	/*
	 * When field name equals to Supplier ID
	 */
	private void checkRmManfSuppl(List<ChangeHistory> chgHisData, int i) {
		if (chgHisData.get(i).getTableName().equals("Raw Material Manufacturer Supplier")
				&& chgHisData.get(i).getFieldName().equals("Supplier ID")) {
			if (chgHisData.get(i).getFuncType() == CommonConstants.FUNCTION_TYPE_DELETE
					&& (chgHisData.get(i).getNewValue() == null || chgHisData.get(i).getNewValue().isEmpty())) {
				//chgHisData.get(i).setNewValue(chgHisData.get(i).getOldValue());
				chgHisData.remove(i);
			}
		}
	}
	
	/*
	 * When field name equals to Supplier ID
	 */
	private List<ChangeHistory> checkRmManfSuppl(List<ChangeHistory> chgHisData) {
		List<ChangeHistory> chdata = new ArrayList<ChangeHistory>();
		for (ChangeHistory ch : chgHisData) {
			if (ch.getTableName().equals("Raw Material Manufacturer Supplier")
					&& ch.getFieldName().equals("Supplier ID")) {
				if (ch.getFuncType() == CommonConstants.FUNCTION_TYPE_DELETE
						&& (ch.getNewValue() == null || ch.getNewValue().isEmpty())) {
					continue;
				}
			}
			chdata.add(ch);
		}

		return chdata;
	}

	/*
	 * When field name equals to Document Type
	 */
	private void convertDocType(List<ChangeHistory> chgHisData, int i) {
		if (chgHisData.get(i).getFieldName().equals("Document Type")) {
			int oldDocTypeId = Integer.parseInt(chgHisData.get(i).getOldValue());
			int newDocTypeId = Integer.parseInt(chgHisData.get(i).getNewValue());

			chgHisData.get(i).setOldValue(docTypeServ.searchDocType(oldDocTypeId).getName());
			chgHisData.get(i).setNewValue(docTypeServ.searchDocType(newDocTypeId).getName());
		}
	}

	/*
	 * When field name equals to Record Status
	 */
	private void convertRecordStatus(List<ChangeHistory> chgHisData, int i) {
		if (chgHisData.get(i).getFieldName().equals("Record Status")) {
			// Set Record Status name
			chgHisData.get(i).setOldValue(setRecordStatus(chgHisData.get(i).getOldValue()));
			chgHisData.get(i).setNewValue(setRecordStatus(chgHisData.get(i).getNewValue()));
		}
	}

	/*
	 *  When field name equals Final Status / VIPD Status
	 */
	private void convertVipdAndFinalStatus(List<ChangeHistory> chgHisData, int i) {
		if (chgHisData.get(i).getFieldName().equals("Final Status")
				|| chgHisData.get(i).getFieldName().equals("VIPD Status")
				|| chgHisData.get(i).getFieldName().equals("System Final Status")) {
			// Set Final Status name
			chgHisData.get(i).setOldValue(setFinalStatus(chgHisData.get(i).getOldValue()));
			chgHisData.get(i).setNewValue(setFinalStatus(chgHisData.get(i).getNewValue()));
		}
	}

	/*
	 *  Set Final Status name
	 */
	public String setFinalStatus(String getStatus) {

		int status = 0;
		try {
			status = Integer.parseInt(getStatus);
		} catch (Exception e) {
			return "";
		}

		switch (status) {
		case CommonConstants.VF_STATUS_PERMITTED:
			getStatus = msgSource.getMessage("vfStatus_" + CommonConstants.VF_STATUS_PERMITTED, null,
					Locale.getDefault());
			break;
		case CommonConstants.VF_STATUS_NOT_PERMITTED:
			getStatus = msgSource.getMessage("vfStatus_" + CommonConstants.VF_STATUS_NOT_PERMITTED, null,
					Locale.getDefault());
			break;
		case CommonConstants.VF_STATUS_NOT_SURE:
			getStatus = msgSource.getMessage("vfStatus_" + CommonConstants.VF_STATUS_NOT_SURE, null,
					Locale.getDefault());
			break;
		case CommonConstants.VF_STATUS_YET_COMPELTE:
			getStatus = msgSource.getMessage("vfStatus_" + CommonConstants.VF_STATUS_YET_COMPELTE, null,
					Locale.getDefault());
			break;
		default:
			getStatus = "";
			break;
		}

		return getStatus;
	}

	/*
	 *  Set Record Status name
	 */
	public String setRecordStatus(String getStatus) {

		int status = 0;
		try {
			status = Integer.parseInt(getStatus);
		} catch (Exception e) {
			return "";
		}

		switch (status) {
		case CommonConstants.RECORD_STATUS_DRAFT:
			getStatus = msgSource.getMessage("vrStatus_" + CommonConstants.RECORD_STATUS_DRAFT, null,
					Locale.getDefault());
			break;
		case CommonConstants.RECORD_STATUS_PENDING_AUTH:
			getStatus = msgSource.getMessage("vrStatus_" + CommonConstants.RECORD_STATUS_PENDING_AUTH, null,
					Locale.getDefault());
			break;
		case CommonConstants.RECORD_STATUS_SUBMITTED:
			getStatus = msgSource.getMessage("vrStatus_" + CommonConstants.RECORD_STATUS_SUBMITTED, null,
					Locale.getDefault());
			break;
		case CommonConstants.RECORD_STATUS_REJECTED:
			getStatus = msgSource.getMessage("vrStatus_" + CommonConstants.RECORD_STATUS_REJECTED, null,
					Locale.getDefault());
			break;
		case CommonConstants.RECORD_STATUS_REWORK:
			getStatus = msgSource.getMessage("vrStatus_" + CommonConstants.RECORD_STATUS_REWORK, null,
					Locale.getDefault());
			break;
		case CommonConstants.RECORD_STATUS_REWORK_PENDING_AUTH:
			getStatus = msgSource.getMessage("vrStatus_" + CommonConstants.RECORD_STATUS_REWORK_PENDING_AUTH, null,
					Locale.getDefault());
			break;
		case CommonConstants.RECORD_STATUS_PENDING_CONFIRM:
			getStatus = msgSource.getMessage("vrStatus_" + CommonConstants.RECORD_STATUS_PENDING_CONFIRM, null,
					Locale.getDefault());
			break;
		case CommonConstants.RECORD_STATUS_CHG_PENDING_AUTH:
			getStatus = msgSource.getMessage("vrStatus_" + CommonConstants.RECORD_STATUS_CHG_PENDING_AUTH, null,
					Locale.getDefault());
			break;
		case CommonConstants.RECORD_STATUS_CHG_PENDING_CONFIRM:
			getStatus = msgSource.getMessage("vrStatus_" + CommonConstants.RECORD_STATUS_CHG_PENDING_CONFIRM, null,
					Locale.getDefault());
			break;
		case CommonConstants.RECORD_STATUS_CHG_REWORK:
			getStatus = msgSource.getMessage("vrStatus_" + CommonConstants.RECORD_STATUS_CHG_REWORK, null,
					Locale.getDefault());
			break;
		case CommonConstants.RECORD_STATUS_PEND_DEACTIVATE:
			getStatus = msgSource.getMessage("vrStatus_" + CommonConstants.RECORD_STATUS_PEND_DEACTIVATE, null,
					Locale.getDefault());
			break;
		case CommonConstants.RECORD_STATUS_DEACTIVATED:
			getStatus = msgSource.getMessage("vrStatus_" + CommonConstants.RECORD_STATUS_DEACTIVATED, null,
					Locale.getDefault());
			break;
		case CommonConstants.RECORD_STATUS_PEND_ACTIVATE:
			getStatus = msgSource.getMessage("vrStatus_" + CommonConstants.RECORD_STATUS_PEND_ACTIVATE, null,
					Locale.getDefault());
			break;
		default:
			getStatus = "";
			break;

		}

		return getStatus;
	}

	@PostMapping("/base/audit/activityLogList")
	public ModelAndView searchActivityLog(@RequestParam(name = "dateFr") String dateFr,
			@RequestParam(name = "dateTo") String dateTo, @RequestParam(name = "userId") String recId,
			@RequestParam(name = "exp1") String exp1, @RequestParam(name = "insert", required = false) String insert,
			@RequestParam(name = "update", required = false) String update, HttpServletRequest request) {

		int rawMatlId = (int) model.get("rawMatlId");
		int prId = (int) model.get("prId");

		String recordRef = "";
		int recordTypeId = 0; // 20210406 - added filter by record type id
		if (rawMatlId != 0) {
			recordTypeId = CommonConstants.RECORD_TYPE_ID_RAW_MATL;
			recordRef = String.valueOf(rawMatlId);
		} else if (prId != 0) {
			recordTypeId = CommonConstants.RECORD_TYPE_ID_PROD_RECP;
			recordRef = String.valueOf(prId);
		}

		model.put("historyLogs", trxHistServ.searchTxHis(recId, dateFr, dateTo, recordRef, exp1, recordTypeId));
		model.put("dateFr", dateFr);
		model.put("dateTo", dateTo);
		model.put("userId", recId);
		model.put("exp1", exp1);
		model.put("insert", insert);
		model.put("update", update);

		return new ModelAndView("/base/audit/activityLogList", model);
	}
	
	@PostMapping("/base/audit/activityLogListNoNav")
	public ModelAndView searchActivityLogNoNav(@RequestParam(name = "dateFr") String dateFr,
			@RequestParam(name = "dateTo") String dateTo, @RequestParam(name = "userId") String recId,
			@RequestParam(name = "exp1") String exp1, @RequestParam(name = "insert", required = false) String insert,
			@RequestParam(name = "update", required = false) String update, HttpServletRequest request) {
		
		model.putAll(searchActivityLog(dateFr, dateTo, recId, exp1, insert, update, request).getModel());
		return new ModelAndView("/base/audit/activityLogListNoNav", model);
	}
	
	@GetMapping("/base/audit/downloadVipdFile")
	public ResponseEntity<byte[]> downloadVipdFile(HttpServletRequest request, @RequestParam(name = "id") int id,
			@RequestParam(name = "type") String type, HttpSession session) {
		byte[] data = new byte[1024];
		String fileName = "";

		ChangeHistory chgHist = chgHisServ.findById(id);

		if (type.equals("old")) {
			data = chgHist.getOldContentObj();
			fileName = chgHist.getOldValue();

		} else if (type.equals("new")) {
			data = chgHist.getNewContentObj();
			fileName = chgHist.getNewValue();
		}

		MediaType mt = MediaTypeUtils.getMediaTypeForFileName(servletContext, fileName);

		return ResponseEntity.ok().contentType(mt)
				.header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + fileName + "\"").body(data);
	}

}
