package com.fms.pfc.controller.base;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.context.annotation.SessionScope;
import org.springframework.web.servlet.ModelAndView;

import com.fms.pfc.common.Authority;
import com.fms.pfc.domain.model.Alert;
import com.fms.pfc.service.api.base.AlertService;
import com.fms.pfc.service.api.base.LoginService;
import com.fms.pfc.service.api.base.TrxHisService;
import com.fms.pfc.validation.common.CommonValidation;

@Controller
@SessionScope
public class NotificationController {

	private AlertService alertServ;
	private Authority auth;
	private CommonValidation commonValServ;
	private TrxHisService trxHistServ;		
	private LoginService loginServ;
	
	private Map<String, Object> model = new HashMap<String, Object>();

	@Autowired
	public NotificationController(AlertService alertServ, Authority auth, CommonValidation commonValServ, TrxHisService trxHistServ,
			LoginService loginServ) {
		super();
		this.alertServ = alertServ;
		this.auth = auth;
		this.commonValServ = commonValServ;
		this.trxHistServ = trxHistServ;
		this.loginServ = loginServ;
	}

	@GetMapping("/base/tray/notificationList")
	public ModelAndView getNotifactionList(HttpServletRequest request, HttpServletResponse response,
			HttpSession session) {

		//Map<String, Object> model = new HashMap<String, Object>();

		model = auth.onPageLoad(model, request);
		auth.isAuthUrl(request, response);
		model.put("notificationItems", alertServ.searchNotification("", "", "", "", "", "", request.getRemoteUser()));

		//request.getSession().setAttribute("sessionModel", model);

		return new ModelAndView("base/tray/notificationList", model);
	}

	@GetMapping("/base/tray/viewNotification")
	public ModelAndView viewNotification(@RequestParam(name = "alertId") int alertId, HttpServletRequest request,
			HttpServletResponse response, HttpSession session) {

		//Map<String, Object> model = (Map<String, Object>) session.getAttribute("sessionModel");

		//model = auth.onPageLoad(model, request);
		Alert notif;

		try {
			notif = alertServ.searchNotificationDetail(alertId, request.getRemoteUser());
			alertServ.updateReadRecipient(alertId, request.getRemoteUser());
			String currUserName = loginServ.searchUser(request.getRemoteUser()).getUserName();
			
			model.put("alertId", notif.getAlertId());
			model.put("date", notif.getCreatedDate());
			model.put("recordId", notif.getRecordRef());
			model.put("subject", notif.getSubject());
			model.put("description", notif.getAlertDesc().replace("[@ReceiverName]", currUserName));

//			// FSGS) Kent 26/2/2021 Add/changed - Add Function executed log START
//			ts.addTrxHistory(new Date(), "View Notification", request.getRemoteUser(),
//					CommonConstants.FUNCTION_TYPE_VIEW, notif.getSubject(), CommonConstants.RECORD_TYPE_ID_NOTIFICATION,
//					null);
//			// FSGS) Kent 26/2/2021 Add/changed - Add Function executed log END
		} catch (Exception e) {
			model.put("error", "Error occur. Please try again later.");
			return new ModelAndView("base/tray/notificationList", model);
		}

		return new ModelAndView("base/tray/viewNotification", model);
	}

	@PostMapping("/base/tray/notificationList")
	public ModelAndView search(@RequestParam(name = "dateFr") String dateFr,
			@RequestParam(name = "dateTo") String dateTo, @RequestParam(name = "recId") String recId,
			@RequestParam(name = "subject") String subject, @RequestParam(name = "exp1") String exp1,
			@RequestParam(name = "exp2") String exp2, HttpServletRequest request, HttpSession session) {

		//Map<String, Object> model = (Map<String, Object>) session.getAttribute("sessionModel");

		String errorMsg = "";
		model.remove("error");
		model.remove("success");
		model.put("dateFr", dateFr);
		model.put("dateTo", dateTo);
		model.put("recId", recId);
		model.put("subject", subject);
		model.put("exp1", exp1);
		model.put("exp2", exp2);

//		// FSGS) Kent 26/2/2021 Add/changed - Add Function executed log START
//		StringBuffer sb = new StringBuffer();
//		sb.append("Search Criteria: ");
//		sb.append("Date From=").append(dateFr.isEmpty() ? "<ALL>" : dateFr).append(", ");
//		sb.append("Date To=").append(dateTo.isEmpty() ? "<ALL>" : dateTo).append(", ");
//		sb.append("Record ID=").append(recId.isEmpty() ? "<ALL>" : recId).append(", ");
//		sb.append("Subject=").append(subject.isEmpty() ? "<ALL>" : subject);
//
//		ts.addTrxHistory(new Date(), "Search Notification", request.getRemoteUser(),
//				CommonConstants.FUNCTION_TYPE_SEARCH, "Search Notification",
//				CommonConstants.RECORD_TYPE_ID_NOTIFICATION, sb.toString());
//		// FSGS) Kent 26/2/2021 Add/changed - Add Function executed log END

		errorMsg += commonValServ.validateDateFormat(dateFr, "Date from");
		errorMsg += commonValServ.validateDateFormat(dateTo, "Date to");
		errorMsg += commonValServ.validateDateRange(dateFr, dateTo, "Date from", "Date to");
		errorMsg += commonValServ.validateInputLength(recId, 50, "Record ID");
		errorMsg += commonValServ.validateInputLength(subject, 250, "Subject");

		if (errorMsg.length() != 0) {
			model.put("error", errorMsg);
			return new ModelAndView("base/tray/notificationList", model);
		}

		// FSGS) Hani 10/3/2021 Add/Change .trim() to search criteria START
		model.put("notificationItems", alertServ.searchNotification(dateFr, dateTo, recId.trim(), subject.trim(), exp1,
				exp2, request.getRemoteUser()));
		// FSGS) Hani 10/3/2021 Add/Change .trim() to search criteria END

		return new ModelAndView("base/tray/notificationList", model);
	}

	@PostMapping("/base/tray/viewNotificationDel")
	public ModelAndView deleteNotification(@RequestParam(name = "alertId") int alertId, HttpServletRequest request,
			HttpSession session) {

		//Map<String, Object> model = (Map<String, Object>) session.getAttribute("sessionModel");

		try {
			//String subject = alertServ.searchNotificationDetail(alertId, request.getRemoteUser()).getSubject();
			alertServ.deleteNotification(alertId, request.getRemoteUser());
			model.put("success", "Record deleted successfully.");
//			// FSGS) Kent 26/2/2021 Add/changed - Add Function executed log START
//			ts.addTrxHistory(new Date(), "Delete Notification", request.getRemoteUser(),
//					CommonConstants.FUNCTION_TYPE_DELETE, subject, CommonConstants.RECORD_TYPE_ID_NOTIFICATION, null);
//			// FSGS) Kent 26/2/2021 Add/changed - Add Function executed log END
		} catch (Exception e) {
			model.put("error", "Failed to delete record.");
		}

		model.put("notificationItems", alertServ.searchNotification("", "", "", "", "", "", request.getRemoteUser()));

		return new ModelAndView("base/tray/notificationList", model);
	}

	@RequestMapping("/base/tray/notificationListDel")
	public ModelAndView deleteNotificationBatch(@RequestParam(value = "tableRow") int[] tableRow,
			HttpServletRequest request, HttpSession session) {

		//Map<String, Object> model = (Map<String, Object>) session.getAttribute("sessionModel");

		if (tableRow.equals(null)) {
			model.put("notificationItems",
					alertServ.searchNotification("", "", "", "", "", "", request.getRemoteUser()));
			return new ModelAndView("base/tray/notificationList", model);
		}

		try {
			for (int i = 0; i < tableRow.length; i++) {
				String subject = alertServ.searchNotificationDetail(tableRow[i], request.getRemoteUser()).getSubject();
				alertServ.deleteNotification(tableRow[i], request.getRemoteUser());
//				// FSGS) Kent 26/2/2021 Add/changed - Add Function executed log START
//				ts.addTrxHistory(new Date(), "Delete Notification", request.getRemoteUser(),
//						CommonConstants.FUNCTION_TYPE_DELETE, subject, CommonConstants.RECORD_TYPE_ID_NOTIFICATION,
//						null);
//				// FSGS) Kent 26/2/2021 Add/changed - Add Function executed log END
			}
			model.put("success", "Record deleted successfully.");
		} catch (Exception e) {
			model.put("error", "Failed to delete record.");
		}

		model.put("notificationItems", alertServ.searchNotification("", "", "", "", "", "", request.getRemoteUser()));

		return new ModelAndView("base/tray/notificationList", model);
	}

}
