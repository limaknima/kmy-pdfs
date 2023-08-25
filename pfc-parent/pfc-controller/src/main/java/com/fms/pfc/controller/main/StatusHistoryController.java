package com.fms.pfc.controller.main;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.context.annotation.SessionScope;
import org.springframework.web.servlet.ModelAndView;

import com.fms.pfc.common.Authority;
import com.fms.pfc.common.CommonConstants;
import com.fms.pfc.domain.model.Task;
import com.fms.pfc.domain.model.main.ProductRecipe;
import com.fms.pfc.domain.model.main.RawMaterial;
import com.fms.pfc.service.api.base.TaskService;
import com.fms.pfc.service.api.main.ProductRecipeService;
import com.fms.pfc.service.api.main.RawMaterialService;

@Controller
@SessionScope
public class StatusHistoryController {

	private Authority auth;
	private RawMaterialService rawMatlServ;
	private ProductRecipeService prodRecpServ;
	private TaskService taskServ;
	
	private Map<String, Object> model = new HashMap<String, Object>();

	@Autowired
	public StatusHistoryController(Authority auth, RawMaterialService rawMatlServ, ProductRecipeService prodRecpServ,
			TaskService taskServ) {
		super();
		this.auth = auth;
		this.rawMatlServ = rawMatlServ;
		this.prodRecpServ = prodRecpServ;
		this.taskServ = taskServ;
	}

	@GetMapping("/main/common/statusHistoryList")
	public ModelAndView getPrintDeclaration(HttpServletRequest request,
			@RequestParam(name = "matlId", required = false, defaultValue = "0") int rawMatlId,
			@RequestParam(name = "prId", required = false, defaultValue = "0") int prId) {

		//Map<String, Object> model = new HashMap<String, Object>();
		int currScreen = 0;

		model = auth.onPageLoad(model, request);
		DateFormat formatter = new SimpleDateFormat("dd/MM/yyyy hh:mm:ss a");

		if (rawMatlId != 0) {
			currScreen = CommonConstants.RECORD_TYPE_ID_RAW_MATL;

			RawMaterial rm = rawMatlServ.searchRawMaterial("", "", rawMatlId, 0).get(0);
			model.put("lblName", "Raw Material Name");
			model.put("txtText", rm.getRawMatlName());
			model.put("createdBy", rm.getCreatorId());
			model.put("createdOn", formatter.format(rm.getCreatedDate()));
			model.put("submittedTo", rm.getCheckerId());
			if (rm.getSubmittedDate() != null) {
				model.put("submittedOn", formatter.format(rm.getSubmittedDate()));
			}
			model.put("rawMatlId", rawMatlId);

			model.put("taskList", taskServ.searchTaskByReference(String.valueOf(rawMatlId), currScreen));
		} else if (prId != 0) {
			currScreen = CommonConstants.RECORD_TYPE_ID_PROD_RECP;

			ProductRecipe pr = prodRecpServ.searchProductRecipe("", "", "", "", prId, 0).get(0);
			model.put("lblName", "Product Recipe Name");
			model.put("txtText", pr.getPrName());
			model.put("prCode", pr.getPrCode());
			model.put("createdBy", pr.getCreatorId());
			model.put("createdOn", formatter.format(pr.getCreatedDate()));
			model.put("submittedTo", pr.getCheckerId());
			if (pr.getSubmittedDate() != null) {
				model.put("submittedOn", formatter.format(pr.getSubmittedDate()));
			}
			model.put("prId", prId);

			model.put("taskList", taskServ.searchTaskByReference(String.valueOf(prId), currScreen));
		}

		model.put("currScreen", currScreen);

		//request.getSession().setAttribute("sessionModel", model);

		return new ModelAndView("/main/common/statusHistoryList", model);
	}

	@GetMapping("/main/common/statusHistoryListDetail")
	@ResponseBody
	public Map<String, Object> statusHisDetail(@RequestParam(name = "date") String dateAssigned, HttpSession session) {

		//Map<String, Object> model = (Map<String, Object>) session.getAttribute("sessionModel");
		int currScreen = (int) model.get("currScreen");

		DateFormat formatter = new SimpleDateFormat("dd/MM/yyyy hh:mm:ss a");
		Map<String, Object> statusModel = new HashMap<String, Object>();
		String recordRef = "";

		if (currScreen == CommonConstants.RECORD_TYPE_ID_RAW_MATL) {
			recordRef = String.valueOf((int) model.get("rawMatlId"));
		} else if (currScreen == CommonConstants.RECORD_TYPE_ID_PROD_RECP) {
			recordRef = String.valueOf((int) model.get("prId"));
		}

		Task task = taskServ.searchDetailTask(recordRef, currScreen, dateAssigned);
		task.setAssignedFormattedDate(formatter.format(task.getDateAssigned()));

		try {
			task.setTakenOnFormattedDate(formatter.format(task.getEndedDate()));
		} catch (Exception e) {
			task.setTakenOnFormattedDate("");
		}

		statusModel.put("taskData", task);

		return statusModel;
	}

}
