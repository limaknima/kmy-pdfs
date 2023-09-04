package com.fms.pfc.controller.base;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.context.annotation.SessionScope;
import org.springframework.web.servlet.ModelAndView;

import com.fms.pfc.common.Authority;
import com.fms.pfc.common.CommonConstants;
import com.fms.pfc.domain.model.UsrRole;
import com.fms.pfc.service.api.base.LoginService;
//import com.fms.pfc.domain.model.Task;
import com.fms.pfc.service.api.base.TaskService;
import com.fms.pfc.service.api.base.TrxHisService;
import com.fms.pfc.service.api.base.UsrRoleService;

@Controller
@SessionScope
public class TaskController {

	private Authority auth;
	private TaskService taskService;
	private UsrRoleService usrRoleServ;
	private LoginService logServ;
	private TrxHisService trxHistServ;

	@Value("${server.servlet.context-path}")
	private String context;
	private Map<String, Object> model = new HashMap<String, Object>();

//	@Autowired
//	public TaskController(Authority auth, TaskService taskService, UsrRoleService usrRoleServ,
//			RawMaterialService rmServ, ProductRecipeService prdRcpServ, LoginService logServ, TrxHisService trxHistServ) {
//		super();
//		this.auth = auth;
//		this.taskService = taskService;
//		this.usrRoleServ = usrRoleServ;
//		this.rmServ = rmServ;
//		this.prdRcpServ = prdRcpServ;
//		this.logServ = logServ;
//		this.trxHistServ = trxHistServ;
//	}

	@GetMapping("/base/tray/myTask")
	public ModelAndView getTaskList(HttpServletRequest request, HttpSession session, HttpServletResponse response) {

		//Map<String, Object> model = new HashMap<String, Object>();

		model = auth.onPageLoad(model, request);
		auth.isAuthUrl(request, response);
		model.put("taskStatus", "Pending");

		String userGrp = logServ.searchUser(request.getRemoteUser()).getGroupId();
		model.put("userGrp", userGrp);

		if (userGrp.equals(CommonConstants.GROUP_ID_FR)) {
			String checker = "";
			List<UsrRole> usrRoleList = usrRoleServ.searchUserRole(request.getRemoteUser());
			for (UsrRole usrRole : usrRoleList) {
				if (usrRole.getRoleId().equals(CommonConstants.ROLE_ID_CHECKER)) {
					checker = CommonConstants.ROLE_ID_CHECKER;
				} else if (usrRole.getRoleId().equals(CommonConstants.ROLE_ID_HOD)) {
					checker = CommonConstants.ROLE_ID_HOD;
				}
			}

			model.put("checker", checker);
			model.put("taskLists", taskService.searchTask("", "", "", "", "", "Pending", "", "", "", checker,
					request.getRemoteUser()));
		}

		//request.getSession().setAttribute("sessionModel", model);

		return new ModelAndView("/base/tray/myTask", model);
	}

	@RequestMapping("/base/tray/myTaskSrc")
	public ModelAndView search(@RequestParam(name = "dateFr") String dateFr,
			@RequestParam(name = "dateTo") String dateTo, @RequestParam(name = "refNum") String refNum,
			@RequestParam(name = "assignedTo") String assignedTo, @RequestParam(name = "subject") String subject,
			@RequestParam(name = "taskStatus") String taskStatus, @RequestParam(name = "exp1") String exp1,
			@RequestParam(name = "exp2") String exp2, @RequestParam(name = "exp3") String exp3,
			HttpServletRequest request, HttpSession session) {

		//Map<String, Object> model = (Map<String, Object>) session.getAttribute("sessionModel");
		String userGrp = model.get("userGrp").toString();

		if (userGrp.equals(CommonConstants.GROUP_ID_FR)) {
			String checker = model.get("checker").toString();

//			// FSGS) Kent 26/2/2021 Add/changed - Add Function executed log START
//			StringBuffer sb = new StringBuffer();
//			sb.append("Search Criteria: ");
//			sb.append("Date Assigned From=").append(dateFr.isEmpty() ? "<ALL>" : dateFr).append(", ");
//			sb.append("Date Assigned To=").append(dateTo.isEmpty() ? "<ALL>" : dateTo).append(", ");
//			sb.append("Reference Number=").append(refNum.isEmpty() ? "<ALL>" : refNum).append(", ");
//			sb.append("Assigned To=").append(assignedTo.isEmpty() ? "<ALL>" : assignedTo).append(", ");
//			sb.append("Subject=").append(subject.isEmpty() ? "<ALL>" : subject).append(", ");
//			sb.append("Task Status=").append(taskStatus);
//
//			trxHistServ.addTrxHistory(new Date(), "Search Task", request.getRemoteUser(), CommonConstants.FUNCTION_TYPE_SEARCH,
//					"Search Task", CommonConstants.RECORD_TYPE_ID_TASK, sb.toString());
//			// FSGS) Kent 26/2/2021 Add/changed - Add Function executed log END

			// FSGS) Hani 10/3/2021 Add/Change .trim() to search criteria START
			model.put("taskLists", taskService.searchTask(dateFr, dateTo, refNum.trim(), assignedTo.trim(),
					subject.trim(), taskStatus, exp1, exp2, exp3, checker, request.getRemoteUser()));
			// FSGS) Hani 10/3/2021 Add/Change .trim() to search criteria END
		}

		model.put("dateFr", dateFr);
		model.put("dateTo", dateTo);
		model.put("refNum", refNum);
		model.put("assignedTo", assignedTo);
		model.put("subject", subject);
		model.put("exp1", exp1);
		model.put("exp2", exp2);
		model.put("exp3", exp3);
		model.put("taskStatus", taskStatus);

		return new ModelAndView("/base/tray/myTask", model);
	}

	@GetMapping("/base/tray/myTaskRef")
	public void directToMaterialSrc(HttpServletRequest request, @RequestParam(name = "ref") String refName,
			@RequestParam(name = "type") int recordTypeId, HttpServletResponse response) throws IOException {

//		if (recordTypeId == CommonConstants.RECORD_TYPE_ID_RAW_MATL) {
//			response.sendRedirect(context + "/main/material/rawMatlFormView?matlId="
//					+ rmServ.searchRawMaterial(refName, String.valueOf(CommonConstants.SEARCH_OPTION_EXACT), 0, 0)
//							.get(0).getRawMatlId());
//		} else if (recordTypeId == CommonConstants.RECORD_TYPE_ID_PROD_RECP) {
//			response.sendRedirect(context + "/main/product/productFormView?prId="
//					+ prdRcpServ.searchProductIdByName(refName, String.valueOf(CommonConstants.SEARCH_OPTION_EXACT))
//							.get(0).getPrId());
//		}

	}
}
