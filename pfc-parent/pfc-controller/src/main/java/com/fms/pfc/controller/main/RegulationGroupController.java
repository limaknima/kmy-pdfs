package com.fms.pfc.controller.main;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;

import org.apache.commons.lang.ArrayUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.context.annotation.SessionScope;
import org.springframework.web.servlet.ModelAndView;

import com.fms.pfc.common.Authority;
import com.fms.pfc.common.CommonConstants;
import com.fms.pfc.domain.dto.main.RegulationGroupDto;
import com.fms.pfc.domain.model.main.RegulationGroup;
import com.fms.pfc.service.api.base.TrxHisService;
import com.fms.pfc.service.api.main.RegulationGroupService;
import com.fms.pfc.validation.common.CommonValidation;

@Controller
@SessionScope
public class RegulationGroupController {

	private static final Logger logger = LoggerFactory.getLogger(RegulationGroupController.class);
	
	private RegulationGroupService regGrpServ;	
	private Authority auth;	
	private CommonValidation commonValServ;	
	private TrxHisService trxHistServ;

	private final String GRP_NAME_TXT = "File Group Name";
	private final String GRP_DESC_TXT = "Description";

	private String screenMode = "";
	private Map<String, Object> model = new HashMap<String, Object>();	
	
	@Autowired
	public RegulationGroupController(RegulationGroupService regGrpServ, Authority auth, CommonValidation commonValServ,
			TrxHisService trxHistServ) {
		super();
		this.regGrpServ = regGrpServ;
		this.auth = auth;
		this.commonValServ = commonValServ;
		this.trxHistServ = trxHistServ;
	}

	// Display initial screen
	@GetMapping("/main/regulation/fileGroupList")
	public ModelAndView getFileGroupList(@Valid RegulationGroupDto regGrp, HttpServletRequest request,
			HttpServletResponse response) {

		removeAlert(model);
		model = auth.onPageLoad(model, request);
		auth.isAuthUrl(request, response);
		model.put("fileGroupItems", regGrpServ.findAll());

		return new ModelAndView("/main/regulation/fileGroupList", model);
	}

	// View form
	@RequestMapping("/main/regulation/regulationGroupFormView")
	public ModelAndView getRegulationGroupView(HttpServletRequest request,
			@RequestParam(name = "regGrpId") Integer regGrpId) {

		removeAlert(model);
		RegulationGroupDto regGrp = regGrpServ.findOneById(regGrpId);
		
		model.put("regGrpId", regGrp.getRegDocCatGrpId());

		trxHistServ.addTrxHistory(new Date(), "View File Group", request.getRemoteUser(), CommonConstants.FUNCTION_TYPE_VIEW,
				regGrp.getGrpName(), CommonConstants.RECORD_TYPE_ID_REGL, null);

		// Set mode
		screenMode = CommonConstants.SCREEN_MODE_VIEW;
		model.put("mode", screenMode);

		// Set form header
		model.put("header", "View Regulation File Group");

		// Set confirmation message
		model.put("confirmHeader", "Edit cancellation");
		model.put("confirmMsg", "Do you want to cancel Edit this record ?");

		// Put model to HTML
		if (regGrp != null)  {
			model.put("fileGroupItem", regGrp);
			DateFormat formatter = new SimpleDateFormat("dd/MM/yyy hh:mm:ss a");

			try {
				String createdInfo = "Created by " + regGrp.getCreatorId() + " on "
						+ formatter.format(regGrp.getCreatedDate());
				model.put("createdInfo", createdInfo);
			} catch (Exception e) {
			}

			String modifiedInfo = "";
			try {
				modifiedInfo = "Modified by " + regGrp.getModifierId() + " on "
						+ formatter.format(regGrp.getModifiedDatetime());
			} catch (Exception e) {
				modifiedInfo = "Modified by --";
			}
			model.put("modifiedInfo", modifiedInfo);
		}

		return new ModelAndView("/main/regulation/fileGroupForm", model);
	}

	// Load add form
	@GetMapping("/main/regulation/fileGroupForm")
	public ModelAndView addRegulationGroup(HttpServletRequest request) {

		removeAlert(model);
		// Set mode
		screenMode = CommonConstants.SCREEN_MODE_ADD;
		model.put("mode", screenMode);

		// Set fields
		model.put("fileGroupItem", new RegulationGroupDto());
		// Set form header
		model.put("header", "Add Regulation File Group");
		// Set confirmation message
		model.put("confirmHeader", "Cancel confirmation");
		model.put("confirmMsg", "Do you want to cancel this record ?");

		return new ModelAndView("/main/regulation/fileGroupForm", model);
	}

	// Save add form
	@PostMapping("/main/regulation/fileGroupForm")
	public ModelAndView saveRegulationGroup(@Valid RegulationGroupDto regGrp, HttpServletRequest request,
			BindingResult bindingResult, String[] check, HttpSession session) throws IOException {
		
		String mode = screenMode;
		
		removeAlert(model);
		String errorMsg = "";
		model.put("fileGroupItem", regGrp);

		errorMsg += commonValServ.validateMandatoryInput(regGrp.getGrpName(), GRP_NAME_TXT);
		errorMsg += commonValServ.validateInputLength(regGrp.getGrpName(), 100, GRP_NAME_TXT);
		errorMsg += commonValServ.validateInputLength(regGrp.getGrpDesc(), 200, GRP_DESC_TXT);

		if (errorMsg.length() != 0) {
			model.put("error", errorMsg);
			model.put("fileGroupItem", regGrp);

			if (mode.equals(CommonConstants.SCREEN_MODE_VIEW)) {
				// Set mode to HTML header
				model.put("mode", CommonConstants.SCREEN_MODE_EDIT);
				model.put("header", "Edit Regulation File Group");
				// Set confirmation message
				model.put("confirmHeader", "Edit cancellation");
				model.put("confirmMsg", "Do you want to cancel edit this record ?");
			}
			return new ModelAndView("/main/regulation/fileGroupForm", model);
		}

		if (mode.equals(CommonConstants.SCREEN_MODE_ADD)) {
			try {
				// If no error proceed to save add form
				if (!checkGrpNameExist(regGrp.getGrpName())) {
					// Add form
					regGrpServ.saveRegGrp(regGrp, request.getRemoteUser());

					// Print add success
					model.put("success", "Record added successfully.");
					model.put("fileGroupItem", new RegulationGroupDto());

					trxHistServ.addTrxHistory(new Date(), "Insert File Group ", request.getRemoteUser(),
							CommonConstants.FUNCTION_TYPE_INSERT, regGrp.getGrpName(),
							CommonConstants.RECORD_TYPE_ID_REGL, null);

				} else {
					// Print ID existed
					model.put("error", "File Group Name already existed. Please use other File Group Name");
					return new ModelAndView("/main/regulation/fileGroupForm", model);
				}

			} catch (Exception e) {
				// Print DB exception
				e.printStackTrace();
				model.put("error", "Record failed to be added.");
			}

		} else if (mode.equals(CommonConstants.SCREEN_MODE_VIEW)) {
			// Set mode to HTML header
			model.put("mode", CommonConstants.SCREEN_MODE_VIEW);
			model.put("header", "Regulation File Group");

			try {
				// Update form
				regGrp.setRegDocCatGrpId((int)model.get("regGrpId"));
				regGrpServ.saveRegGrp(regGrp, request.getRemoteUser());

				// Print update success
				model.put("success", "Record updated successfully.");

				trxHistServ.addTrxHistory(new Date(), "Update File Group ", request.getRemoteUser(),
						CommonConstants.FUNCTION_TYPE_UPDATE, regGrp.getGrpName(), CommonConstants.RECORD_TYPE_ID_REGL,
						null);

			} catch (Exception e) {
				// Print DB exception
				e.printStackTrace();
				model.put("error", "Record failed to update.");
				return new ModelAndView("/main/regulation/fileGroupForm", model);
			}

		}

		model.put("fileGroupItems", regGrpServ.findAll());
		return new ModelAndView("/main/regulation/fileGroupList", model);
	}

	// Check if group name exist in database
	public boolean checkGrpNameExist(String grpName) {

		boolean exist = false;
		List<RegulationGroup> grpList = regGrpServ.searchGroupRegulation(grpName, "", "3", "");

		// If group name existed return true
		if (grpList.size() > 0) {
			exist = true;
		}

		return exist;
	}

	// Display search results
	@PostMapping("/main/regulation/fileGroupFormSearch")
	public ModelAndView getSearchResult(HttpServletRequest request, @RequestParam(name = "grpName") String grpName,
			@RequestParam(name = "grpDesc") String grpDesc, @RequestParam(name = "exp1") String exp1,
			@RequestParam(name = "exp2") String exp2, HttpSession session) {

		boolean hasError = false;

		removeAlert(model);
		String errorMsg = "";

		model.put("grpName", grpName);
		model.put("grpDesc", grpDesc);

		try {
			errorMsg += commonValServ.validateInputLength(grpName, 100, GRP_NAME_TXT);
			errorMsg += commonValServ.validateInputLength(grpDesc, 200, GRP_DESC_TXT);

			if (errorMsg.length() == 0) {
				hasError = false;

				model.put("fileGroupItems", regGrpServ.findByCriteria(grpName.trim(), grpDesc.trim(), exp1, exp2));
				model.put("exp1", exp1);
				model.put("exp2", exp2);

				StringBuffer sb = new StringBuffer();
				sb.append("Search Criteria: ");
				sb.append("File Group=").append(grpName.isEmpty() ? "<ALL>" : grpName).append(", ");
				sb.append("Description=").append(grpDesc.isEmpty() ? "<ALL>" : grpDesc);

				trxHistServ.addTrxHistory(new Date(), "Search File Group", request.getRemoteUser(),
						CommonConstants.FUNCTION_TYPE_SEARCH, "Search File Group", CommonConstants.RECORD_TYPE_ID_REGL,
						sb.toString());

			} else
				hasError = true;

		} catch (Exception e) {

			e.printStackTrace();
			hasError = true;
			errorMsg += "Failed to get record.";
			errorMsg += e.toString();

		} finally {
			if (hasError == true) {
				model.put("error", errorMsg);
				// return back user input
				model.put("fileGroupItems", regGrpServ.findAll());
			}
		}

		return new ModelAndView("main/regulation/fileGroupList", model);
	}

	@RequestMapping("/main/regulation/fileGroupFormDel")
	public ModelAndView deleteRegulationGroupBatch(HttpServletRequest request,
			@RequestParam(value = "tableRow") String[] check, HttpSession session) {

		List<RegulationGroup> groupList = regGrpServ.checkGroupExist();
		String errorMsg = "";
		removeAlert(model);
		try {

			for (RegulationGroup grpList : groupList) {

				for (int x = 0; x < check.length; x++) {

					if (grpList.getGrpName().equals(check[x])) {

						errorMsg += check[x]
								+ " cannot be deleted as it was used in raw material management setup. </BR>";
						// Ask man yew how to throw exception
						check = (String[]) ArrayUtils.remove(check, x);
					}
				}
			}

			if (errorMsg.length() == 0) {
				// Delete form
				for (int i = 0; i < check.length; i++) {
					regGrpServ.deleteGroupRegulation(check[i]);

					trxHistServ.addTrxHistory(new Date(), "Delete File Group", request.getRemoteUser(),
							CommonConstants.FUNCTION_TYPE_DELETE, check[i], CommonConstants.RECORD_TYPE_ID_REGL, null);

				}

				// Print update success
				model.put("success", "Record deleted successfully.");

			} else {
				model.put("error", errorMsg);
				model.put("fileGroupItems", regGrpServ.findAll());
				return new ModelAndView(("/main/regulation/fileGroupList"), model);
			}

		} catch (Exception e) {
			// Print DB exception
			model.put("error", "Record failed to delete.");
		}

		model.put("fileGroupItems", regGrpServ.findAll());

		return new ModelAndView(("/main/regulation/fileGroupList"), model);
	}

	public Map<String, Object> removeAlert(Map<String, Object> model) {
		model.remove("error");
		model.remove("success");

		return model;
	}

}
