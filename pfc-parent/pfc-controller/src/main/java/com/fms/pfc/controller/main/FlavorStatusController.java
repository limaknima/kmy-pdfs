package com.fms.pfc.controller.main;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
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
import com.fms.pfc.domain.dto.main.FlavorStatusDto;
import com.fms.pfc.service.api.base.TrxHisService;
import com.fms.pfc.service.api.main.FlavorStatusService;
import com.fms.pfc.validation.common.CommonValidation;

@Controller
@SessionScope
public class FlavorStatusController {

	private static final Logger logger = LoggerFactory.getLogger(FlavorStatusController.class);

	private Authority auth;
	private CommonValidation commonValServ;
	private TrxHisService trxHistServ;
	private FlavorStatusService fsServ;
	private MessageSource msgSource;

	private String screenMode = "";
	private Map<String, Object> model = new HashMap<String, Object>();

	private static final String FS_NAME_TXT = "Flavor Status Name";
	private static final String FS_DESC_TXT = "Description";
	private static final String FS_RANK_TXT = "Rank";

	private static final String ERR_MSG_UNIQUE = " already exist ";
	private static final String breakline = ".<br />";

	@Autowired
	public FlavorStatusController(Authority auth, CommonValidation commonValServ, TrxHisService trxHistServ,
			FlavorStatusService fsServ, MessageSource msgSource) {
		super();
		this.auth = auth;
		this.commonValServ = commonValServ;
		this.trxHistServ = trxHistServ;
		this.fsServ = fsServ;
		this.msgSource = msgSource;
	}

	/**
	 * Display initial screen
	 * 
	 * @param request
	 * @param response
	 * @return
	 */
	@GetMapping("/base/maint/flavorStatusList")
	public ModelAndView searchListing(HttpServletRequest request, HttpServletResponse response) {

		removeAlert(model);
		model = auth.onPageLoad(model, request);
		auth.isAuthUrl(request, response);
		model.put("fsAllItems", fsServ.findAll());
		model.put("rankSelectItems", fsServ.loadRankSelectItems());

		return new ModelAndView("/base/maint/flavorStatusList", model);
	}

	/**
	 * View form
	 * 
	 * @param request
	 * @param fsId
	 * @return
	 */
	@RequestMapping("/base/maint/flavorStatusFormView")
	public ModelAndView viewForm(HttpServletRequest request, @RequestParam(name = "fsId") Integer fsId) {

		removeAlert(model);

		FlavorStatusDto fsDto = fsServ.findOneById(fsId);
		trxHistServ.addTrxHistory(new Date(), "View Flavor Status", request.getRemoteUser(),
				CommonConstants.FUNCTION_TYPE_VIEW, fsDto.getFsName(), CommonConstants.RECORD_TYPE_ID_FLV_STATUS, null);

		// Set mode
		screenMode = CommonConstants.SCREEN_MODE_VIEW;
		model.put("mode", screenMode);

		// Set form header
		model.put("header", "View Flavor Status");

		// Set confirmation message
		model.put("confirmHeader", "Edit cancellation");
		model.put("confirmMsg", "Do you want to cancel Edit this record ?");

		// Put model to HTML
		if (fsDto != null) {

			model.put("fsId", fsDto.getFsId());
			model.put("flavStatusItem", fsDto);
			model.put("flavStatusCurrItem", fsDto);
			model.put("createdUser", fsDto.getCreatorId());
			model.put("modifiedUser", fsDto.getModifierId());

			DateFormat formatter = new SimpleDateFormat("dd/MM/yyy hh:mm:ss a");
			try {
				String createdInfo = "Created by " + fsDto.getCreatorId() + " on "
						+ formatter.format(fsDto.getCreatedDate());
				model.put("createdInfo", createdInfo);
			} catch (Exception e) {
			}

			String modifiedInfo = "";
			try {
				modifiedInfo = "Modified by " + fsDto.getModifierId() + " on "
						+ formatter.format(fsDto.getModifiedDatetime());
			} catch (Exception e) {
				modifiedInfo = "Modified by --";
			}
			model.put("modifiedInfo", modifiedInfo);
		}

		return new ModelAndView("/base/maint/flavorStatusForm", model);
	}

	/**
	 * Load add form
	 * 
	 * @param request
	 * @return
	 */
	@GetMapping("/base/maint/flavorStatusForm")
	public ModelAndView loadNewForm(HttpServletRequest request) {
		removeAlert(model);

		// Set mode
		screenMode = CommonConstants.SCREEN_MODE_ADD;
		model.put("mode", screenMode);

		// Set fields
		model.put("flavStatusItem", new FlavorStatusDto());
		// Set form header
		model.put("header", "Add Flavor Status");
		// Set confirmation message
		model.put("confirmHeader", "Cancel confirmation");
		model.put("confirmMsg", "Do you want to cancel this record ?");

		return new ModelAndView("/base/maint/flavorStatusForm", model);
	}

	/**
	 * Save Form
	 * 
	 * @param fsDto
	 * @param request
	 * @param bindingResult
	 * @param session
	 * @return
	 * @throws Exception
	 */
	@PostMapping("/base/maint/flavorStatusFormSave")
	public ModelAndView saveFlavorStatus(@Valid FlavorStatusDto fsDto, HttpServletRequest request,
			BindingResult bindingResult, HttpSession session) throws Exception {

		removeAlert(model);
		String mode = screenMode;
		model.put("flavStatusItem", fsDto);
		String errorMsg = validateForm(fsDto);
		
		logger.debug("saveFlavorStatus mode="+mode);

		if (errorMsg.length() != 0) {
			model.put("error", errorMsg);
			model.put("flavStatusItem", fsDto);

			if (mode.equals(CommonConstants.SCREEN_MODE_EDIT)) {
				screenMode = CommonConstants.SCREEN_MODE_EDIT;
				// Set mode to HTML header
				model.put("mode", screenMode);
				model.put("header", "Edit Flavor Status");
				// Set confirmation message
				model.put("confirmHeader", "Edit cancellation");
				model.put("confirmMsg", "Do you want to cancel edit this record ?");
			}
			return new ModelAndView("/base/maint/flavorStatusForm", model);
		}

		if (mode.equals(CommonConstants.SCREEN_MODE_ADD)) {

			try {
				// If no error proceed to save add form
				// Add form
				fsServ.saveFlavorStatus(null, fsDto.getFsRank(), fsDto.getFsName(), fsDto.getFsDesc(),
						request.getRemoteUser());

				// Print add success
				model.put("success", "Record added successfully.");
				model.put("flavStatusItem", new FlavorStatusDto());

				trxHistServ.addTrxHistory(new Date(), "Insert Flavor Status", request.getRemoteUser(),
						CommonConstants.FUNCTION_TYPE_INSERT, fsDto.getFsName(),
						CommonConstants.RECORD_TYPE_ID_FLV_STATUS, null);

			} catch (Exception e) {
				// Print DB exception
				e.printStackTrace();
				model.put("error", "Record failed to be added.");
			}

		} else if (mode.equals(CommonConstants.SCREEN_MODE_VIEW)) {
			// Set mode to HTML header
			screenMode = CommonConstants.SCREEN_MODE_VIEW;
			model.put("mode", screenMode);
			model.put("header", "Flavor Status");

			try {
				// Update form
				fsServ.saveFlavorStatus((int) (model.get("fsId")), fsDto.getFsRank(), fsDto.getFsName(),
						fsDto.getFsDesc(), request.getRemoteUser());

				// Print update success
				model.put("success", "Record updated successfully.");

				trxHistServ.addTrxHistory(new Date(), "Update Flavor Status", request.getRemoteUser(),
						CommonConstants.FUNCTION_TYPE_UPDATE, fsDto.getFsName(),
						CommonConstants.RECORD_TYPE_ID_FLV_STATUS, null);

			} catch (Exception e) {
				// Print DB exception
				e.printStackTrace();
				model.put("error", "Record failed to update.");
			}
		}
		
		model.put("fsAllItems", fsServ.findAll());

		return new ModelAndView("/base/maint/flavorStatusList", model);
	}

	/**
	 * Do search
	 * 
	 * @param request
	 * @param fsName
	 * @param fsDesc
	 * @param fsRank
	 * @param fsNameExp
	 * @param fsDescExp
	 * @param session
	 * @return
	 */
	@PostMapping("/base/maint/flavorStatusSearch")
	public ModelAndView doSearch(HttpServletRequest request, @RequestParam(name = "fsName") String fsName,
			@RequestParam(name = "fsDesc") String fsDesc, @RequestParam(name = "fsRank") String fsRank,
			@RequestParam(name = "fsNameExp") String fsNameExp, @RequestParam(name = "fsDescExp") String fsDescExp,
			HttpSession session) {

		boolean hasError = false;

		removeAlert(model);
		String errorMsg = "";

		model.put("fsName", fsName);
		model.put("fsDesc", fsDesc);
		model.put("fsRank", fsRank);
		model.put("fsNameExp", fsNameExp);
		model.put("fsDescExp", fsDescExp);

		try {
			if (errorMsg.length() == 0) {

				List<FlavorStatusDto> items = fsServ.searchByCriteria(fsName, fsDesc, fsNameExp, fsDescExp, fsRank);

				model.put("fsAllItems", items);

				StringBuffer sb = new StringBuffer();
				sb.append("Search Criteria: ");
				sb.append("Flavor Status=").append(fsName.isEmpty() ? "<ALL>" : fsName).append(", ");
				sb.append("Description=").append(fsDesc.isEmpty() ? "<ALL>" : fsDesc);

				trxHistServ.addTrxHistory(new Date(), "Search Flavor Status", request.getRemoteUser(),
						CommonConstants.FUNCTION_TYPE_SEARCH, "Search Flavor Status",
						CommonConstants.RECORD_TYPE_ID_FLV_STATUS, sb.toString());

			} else {
				hasError = true;
			}

		} catch (Exception e) {
			e.printStackTrace();
			hasError = true;
			errorMsg += "Failed to get record.";
			errorMsg += e.toString();

		} finally {
			if (hasError == true) {
				model.put("error", errorMsg);
				// return back user input
				model.put("fsAllItems", fsServ.findAll());
			}
		}

		return new ModelAndView("/base/maint/flavorStatusList", model);
	}

	private Map<String, Object> removeAlert(Map<String, Object> model) {
		model.remove("error");
		model.remove("success");

		return model;
	}

	/**
	 * Name must be unique
	 * 
	 * @param fsName
	 * @return
	 */
	private String validateDuplicateFlavStatus(String fsName) {
		FlavorStatusDto fsItem = fsServ.findByName(fsName);

		// If flav status existed return true
		if (null != fsItem) {

			logger.debug("validateDuplicateFlavStatus " + fsItem.getFsName());

			return FS_NAME_TXT + ERR_MSG_UNIQUE + fsName + breakline;
		}

		return "";
	}

	/**
	 * Rank must be unique
	 * 
	 * @param fsRank
	 * @return
	 */
	private String validateDuplicateRank(int fsRank) {
		FlavorStatusDto fsItem = fsServ.findByRank(fsRank);

		// If flav status existed return true
		if (null != fsItem) {

			logger.debug("validateDuplicateRank " + fsItem.getFsRank());

			return FS_RANK_TXT + ERR_MSG_UNIQUE + fsRank + breakline;
		}
		return "";
	}

	/**
	 * Construct error message based on validation result
	 * 
	 * @param fsDto
	 * @return
	 */
	private String validateForm(FlavorStatusDto fsDto) {
		String errorMsg = "";
		errorMsg += commonValServ.validateMandatoryInput(fsDto.getFsName(), FS_NAME_TXT);
		errorMsg += commonValServ
				.validateMandatoryInput(fsDto.getFsRank() == 0 ? "" : String.valueOf(fsDto.getFsRank()), FS_RANK_TXT);
		errorMsg += commonValServ.validateInputLength(fsDto.getFsName(), 100, FS_NAME_TXT);
		errorMsg += commonValServ.validateInputLength(fsDto.getFsDesc(), 200, FS_DESC_TXT);

		if (screenMode == CommonConstants.SCREEN_MODE_ADD) {
			errorMsg += validateDuplicateFlavStatus(fsDto.getFsName());
			errorMsg += validateDuplicateRank(fsDto.getFsRank());
		} else {

			if (null != model.get("flavStatusCurrItem")) {
				FlavorStatusDto currDto = (FlavorStatusDto) model.get("flavStatusCurrItem");

				
				logger.debug("validateForm cur name="+currDto.getFsName()+"; new name="+fsDto.getFsName());
				if (!StringUtils.equalsIgnoreCase(currDto.getFsName(), fsDto.getFsName())) {
					// check if current name value modified
					errorMsg += validateDuplicateFlavStatus(fsDto.getFsName());

				}

				logger.debug("validateForm cur rank="+currDto.getFsRank()+"; new rank="+fsDto.getFsRank());
				if (currDto.getFsRank() != fsDto.getFsRank()) {
					// check if current rank value modified
					errorMsg += validateDuplicateRank(fsDto.getFsRank());
				}
			}
		}

		return errorMsg;
	}
	
	@RequestMapping("/base/maint/flavorStatusFormDel")
	public ModelAndView deleteFlavorStatusBatch(HttpServletRequest request,
			@RequestParam(value = "tableRow") String[] check, HttpSession session) {

		model.remove("error");
		model.remove("success");
		try {
			// Delete form
			for (int i = 0; i < check.length; i++) {

				if (isFlvStatusInUse(check[i])) {
					// Print delete failed
					model.put("error", "Flavor Status is in used and it is not allow to delete from the system.");
				} else {
					fsServ.deleteById(Integer.parseInt(check[i]));
					// Print delete success`
					model.put("success",
							msgSource.getMessage("msgSuccessDelete", new Object[] {}, Locale.getDefault()));

					trxHistServ.addTrxHistory(new Date(), "Delete Flavor Status", request.getRemoteUser(),
							CommonConstants.FUNCTION_TYPE_DELETE, check[i], CommonConstants.RECORD_TYPE_ID_FLV_STATUS,
							null);
				}
			}

		} catch (Exception e) {
			// Print DB exception
			model.put("error", msgSource.getMessage("msgFailDelete", new Object[] {}, Locale.getDefault()));
			e.printStackTrace();
		} finally {
			model.put("fsAllItems", fsServ.findAll());			
		}


		return new ModelAndView(("/base/maint/flavorStatusList"), model);
	}

	private boolean isFlvStatusInUse(String id) {

		if (fsServ.countRawMatlByFlavorStatus(Integer.parseInt(id)) != 0)
			return true;

		return false;
	}

}
