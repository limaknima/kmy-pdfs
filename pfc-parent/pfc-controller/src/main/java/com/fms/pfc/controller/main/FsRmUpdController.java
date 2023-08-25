package com.fms.pfc.controller.main;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.context.annotation.SessionScope;
import org.springframework.web.servlet.ModelAndView;

import com.fms.pfc.common.AlertDefEnum;
import com.fms.pfc.common.Authority;
import com.fms.pfc.common.CommonConstants;
import com.fms.pfc.common.RecordStatusEnum;
import com.fms.pfc.domain.dto.LabelAndValueDto;
import com.fms.pfc.domain.dto.main.FlavorStatusDto;
import com.fms.pfc.domain.dto.main.FsRmUpdDto;
import com.fms.pfc.domain.dto.main.FsRmUpdResultDto;
import com.fms.pfc.domain.dto.main.ProductRecipeDto;
import com.fms.pfc.domain.dto.main.RawMaterialDto;
import com.fms.pfc.domain.model.AlertMessage;
import com.fms.pfc.domain.model.Usr;
import com.fms.pfc.domain.model.UsrRole;
import com.fms.pfc.service.api.base.AlertMessageService;
import com.fms.pfc.service.api.base.AlertService;
import com.fms.pfc.service.api.base.LoginService;
import com.fms.pfc.service.api.base.UsrRoleService;
import com.fms.pfc.service.api.main.FlavorStatusService;
import com.fms.pfc.service.api.main.FsRmUpdService;
import com.fms.pfc.service.api.main.RawMaterialService;
import com.fms.pfc.validation.common.CommonValidation;

@Controller
@SessionScope
public class FsRmUpdController {

	private static final Logger logger = LoggerFactory.getLogger(FsRmUpdController.class);

	private Authority auth;
	private CommonValidation commonValServ;
	private MessageSource msgSource;
	private FsRmUpdService fsRmUpdServ;
	private FlavorStatusService fsServ;
	private RawMaterialService rmServ;
	private ProductRecipeController prc;
	private UsrRoleService usrRoleServ;
	private LoginService loginServ;
	private AlertService alertServ;
	private AlertMessageService alertMsgServ;
	private JavaMailSender javaMailSender;

	private Map<String, Object> mainModel = new HashMap<String, Object>();

	private static final String RM_TXT = "Raw Material";
	private static final String FS_TXT = "New Flavor Status";

	@Autowired
	public FsRmUpdController(Authority auth, CommonValidation commonValServ, MessageSource msgSource,
			FsRmUpdService fsRmUpdServ, FlavorStatusService fsServ, RawMaterialService rmServ,
			ProductRecipeController prc, UsrRoleService usrRoleServ, LoginService loginServ, AlertService alertServ,
			AlertMessageService alertMsgServ, JavaMailSender javaMailSender) {
		super();
		this.auth = auth;
		this.commonValServ = commonValServ;
		this.msgSource = msgSource;
		this.fsRmUpdServ = fsRmUpdServ;
		this.fsServ = fsServ;
		this.rmServ = rmServ;
		this.prc = prc;
		this.usrRoleServ = usrRoleServ;
		this.loginServ = loginServ;
		this.alertServ = alertServ;
		this.alertMsgServ = alertMsgServ;
		this.javaMailSender = javaMailSender;
	}

	/**
	 * Display initial screen
	 * 
	 * @param request
	 * @param response
	 * @return
	 */
	@GetMapping("/main/material/flavStatusRmList")
	public ModelAndView searchListing(HttpServletRequest request, HttpServletResponse response) {

		removeAlert(mainModel);
		mainModel = auth.onPageLoad(mainModel, request);
		auth.isAuthUrl(request, response);
		checkAccessControl(request.getRemoteUser());

		mainModel.put("rawMatlSelectItems", rmSelectItems());

		if ((Boolean) mainModel.get("authLvl1"))
			mainModel.put("flavStatusSelectItems", fsServ.findAll());

		return new ModelAndView("/main/material/flavStatusRmList", mainModel);
	}

	/**
	 * Search affected PR
	 * 
	 * @param request
	 * @param rawMatl
	 * @param newFlavStatus
	 * @param session
	 * @return
	 */
	@PostMapping("/main/material/flavStatusRmSearch")
	public ModelAndView doSearch(HttpServletRequest request, @RequestParam(name = "rawMatl") String rawMatl,
			@RequestParam(name = "newFlavStatus") String newFlavStatus, HttpSession session) {

		boolean hasError = false;

		removeAlert(mainModel);
		String errorMsg = validateForm(rawMatl, newFlavStatus);

		mainModel.put("rawMatl", rawMatl);
		mainModel.put("newFlavStatus", newFlavStatus);

		logger.debug("doSearch() rawMatlId ={}, newFlavStatus ={}", rawMatl, newFlavStatus);

		try {
			if (errorMsg.length() == 0) {
				FsRmUpdDto fsRmUpd = fsRmUpdServ.findOneByRmId(Integer.parseInt(rawMatl));
				mainModel.put("fsRmUpdItem", fsRmUpd);
				findAffectedPr(rawMatl, newFlavStatus, fsRmUpd);
				mainModel.put("newFlavStatusName", fsServ.findOneById(Integer.parseInt(newFlavStatus)).getFsName());

			} else {
				hasError = true;
			}

		} catch (Exception e) {
			e.printStackTrace();
			hasError = true;
			errorMsg += "Failed to get record. ";
			errorMsg += e.toString();
			logger.error("doSearch() ex={}", e.getMessage());

		} finally {
			if (hasError == true) {
				mainModel.put("error", errorMsg);
				// return back user input
			}
		}

		return new ModelAndView("/main/material/flavStatusRmList", mainModel);
	}

	@RequestMapping(value = "/main/material/rawMatlOnChange", method = RequestMethod.GET)
	public String rawMatlOnChange(@RequestParam(name = "rawMatlId") String rawMatlId, HttpSession session,
			ModelMap model) {

		RawMaterialDto rmDto = rmServ.findRawMatlDto("", "", Integer.parseInt(rawMatlId), 0);
		mainModel.put("currenRmItem", rmDto);
		String fsName = "";
		FlavorStatusDto fsDto = null;
		if (Objects.nonNull(rmDto.getFlavStatusId())) {
			fsDto = fsServ.findOneById(rmDto.getFlavStatusId());
			fsName = fsDto.getFsName();
		}

		mainModel.put("currFlavStatus", fsName);

		if ((Boolean) mainModel.get("authLvl2")) {
			String newFsName = "";
			FsRmUpdDto fsUpdDTo = fsRmUpdServ.findOneByRmId(Integer.parseInt(rawMatlId));
			if (Objects.nonNull(fsUpdDTo.getNewFsId())) {
				fsDto = fsServ.findOneById(fsUpdDTo.getNewFsId());
				newFsName = fsDto.getFsName();
			}
			mainModel.put("newFlavStatus", String.valueOf(fsUpdDTo.getNewFsId()));
			mainModel.put("newFlavStatusName", newFsName);
		}

		model.addAllAttributes(mainModel);

		return "/main/material/flavStatusRmList :: #rmCurrFs";
	}

	@RequestMapping(value = "/main/material/prNameOnClick", method = RequestMethod.GET)
	@ResponseBody
	public Map<String, Object> prNameOnClick(@RequestParam(name = "prId") String prId, HttpServletRequest request,
			HttpServletResponse response, HttpSession session) {

		Map<String, Object> prIngModel = new HashMap<String, Object>();
		prIngModel = prc.viewPrdRcp(Integer.parseInt(prId), request, response, session).getModel();
		// need to get this ingSummaryItems

		return prIngModel;
	}

	@RequestMapping("/main/material/flavStatusRmSubmit")
	public ModelAndView submitFsRmUpdBatch(HttpServletRequest request,
			@RequestParam(value = "tableRow", required = false) String[] check, HttpSession session) {

		mainModel.remove("error");
		mainModel.remove("success");
		try {
			// Submit form

			if (Objects.nonNull(check) && check.length > 0) {
				// Submit selected
				// for (int i = 0; i < check.length; i++) {}
			} else {
				// Submit all
				FsRmUpdDto dto = (FsRmUpdDto) mainModel.get("fsRmUpdItem");
				String usr = request.getRemoteUser().trim();
				Date currentDate = new Date();
				if ((Boolean) mainModel.get("authLvl1")) {
					List<ProductRecipeDto> psDtoList = (List<ProductRecipeDto>) mainModel.get("affectedPrItems");
					List<Integer> statusList = Arrays.asList(RecordStatusEnum.PEND_AUTH.intValue(),
							RecordStatusEnum.SUBMITTED.intValue());
					if (statusList.contains(dto.getUpdStatus())) {
						dto.setUpdStatus(RecordStatusEnum.PEND_AUTH.intValue());
						dto.setMakerId(usr);
						dto.setNewFsId(Integer.parseInt((String) mainModel.get("newFlavStatus")));

						// create temp table to store affected PR
						fsRmUpdServ.saveFsRmUpd(dto, usr, psDtoList, null, false, currentDate);
						// send alert to checker
						sendAlert(CommonConstants.ROLE_ID_CHECKER, dto, currentDate);

						mainModel.put("success",
								msgSource.getMessage("msgSuccessSubmit", new Object[] {}, Locale.getDefault()));

					}

				} else if ((Boolean) mainModel.get("authLvl2")) {
					List<FsRmUpdResultDto> psDtoList = (List<FsRmUpdResultDto>) mainModel.get("affectedPrItems");
					if (dto.getUpdStatus() == RecordStatusEnum.PEND_AUTH.intValue()) {
						dto.setUpdStatus(RecordStatusEnum.SUBMITTED.intValue());
						dto.setCheckerId(usr);
						dto.setCurrFsId(dto.getNewFsId());
						dto.setNewFsId(null);

						fsRmUpdServ.saveFsRmUpd(dto, usr, null, psDtoList, true, currentDate);

						// send alert to maker
						sendAlert(CommonConstants.ROLE_ID_MAKER, dto, currentDate);

						mainModel.put("success",
								msgSource.getMessage("msgSuccessSubmit", new Object[] {}, Locale.getDefault()));
					}
				}

			}

		} catch (Exception e) {
			// Print DB exception
			mainModel.put("error", msgSource.getMessage("msgFailSubmit", new Object[] {}, Locale.getDefault()));
			e.printStackTrace();

			logger.error("submitFsRmUpdBatch() ex={}", e.getMessage());
		} finally {
			// mainModel.put("fsAllItems", fsServ.findAll());
			if ((Boolean) mainModel.get("authLvl2")) {
				mainModel.remove("currFlavStatus");
				mainModel.remove("newFlavStatus");
				mainModel.remove("newFlavStatusName");
				mainModel.remove("affectedPrItems");
				mainModel.put("rawMatlSelectItems", rmSelectItems());
			}
		}

		return new ModelAndView(("/main/material/flavStatusRmList"), mainModel);
	}

	private String validateForm(String rawMatl, String newFlavStatus) {
		String errorMsg = "";

		errorMsg += commonValServ.validateMandatoryInput(rawMatl, RM_TXT);
		errorMsg += commonValServ.validateMandatoryInput(newFlavStatus, FS_TXT);

		return errorMsg;
	}

	private Map<String, Object> removeAlert(Map<String, Object> model) {
		model.remove("error");
		model.remove("success");

		return model;
	}

	private void checkAccessControl(String userId) {
		boolean authLvl1 = false;
		boolean authLvl2 = false;
		List<UsrRole> usrRoleList = usrRoleServ.searchUserRole(userId);
		for (UsrRole usrRole : usrRoleList) {
			if (usrRole.getRoleId().equals(CommonConstants.ROLE_ID_CHECKER)
					|| usrRole.getRoleId().equals(CommonConstants.ROLE_ID_SUPERUSER)
					|| usrRole.getRoleId().equals(CommonConstants.ROLE_ID_HOD)) {
				authLvl2 = true;
			}

			if (usrRole.getRoleId().equals(CommonConstants.ROLE_ID_MAKER)) {
				authLvl1 = true;
			}
		}

		mainModel.put("authLvl1", authLvl1);
		mainModel.put("authLvl2", authLvl2);

	}

	private List<LabelAndValueDto> rmSelectItems() {

		if ((Boolean) mainModel.get("authLvl1"))
			return rmServ.getRmNameLabelAndValue();

		if ((Boolean) mainModel.get("authLvl2")) {

			List<FsRmUpdDto> dtoList = fsRmUpdServ.findAll();
			dtoList = dtoList.stream().filter(obj -> obj.getUpdStatus() == RecordStatusEnum.PEND_AUTH.intValue())
					.collect(Collectors.toList());

			List<LabelAndValueDto> items = new ArrayList<LabelAndValueDto>();
			for (FsRmUpdDto d : dtoList) {
				RawMaterialDto rmd = rmServ.findRawMatlDto("", "", d.getRmId(), 0);
				LabelAndValueDto lbl = new LabelAndValueDto(rmd.getRawMatlName(), rmd.getRawMatlId());
				items.add(lbl);
			}

			return items;
		}

		return null;
	}

	private void findAffectedPr(String rawMatl, String newFlavStatus, FsRmUpdDto fsRmUpd) {
		logger.debug("findAffectedPr() rm={}, newFsId={}, updid={}", rawMatl, newFlavStatus, fsRmUpd.getUpdId());

		if ((Boolean) mainModel.get("authLvl1"))
			mainModel.put("affectedPrItems",
					fsRmUpdServ.findAffectedPr(Integer.parseInt(rawMatl), Integer.parseInt(newFlavStatus)));

		if ((Boolean) mainModel.get("authLvl2"))
			mainModel.put("affectedPrItems", fsRmUpdServ.findAffectedPrResult(fsRmUpd.getUpdId()));
	}

	private void sendAlert(String sendToRole, FsRmUpdDto dto, Date currentDate) {
		RawMaterialDto rmDto = (RawMaterialDto) mainModel.get("currenRmItem");
		DateFormat formatter = new SimpleDateFormat("dd/MM/yyyy hh:mm:ss a");

		//to maker
		if (sendToRole.equalsIgnoreCase(CommonConstants.ROLE_ID_MAKER)) {
			Usr usr = loginServ.searchUser(dto.getMakerId());
			AlertMessage alertMsg = alertMsgServ.searchAlertById(AlertDefEnum.RM_AMEND_FS_APPR.strValue());
			String subject = alertMsg.getSubject().replace("[@Name]", rmDto.getRawMatlName());
			String alertDesc = alertMsg.getDescription().replace("[@ReceiverName]", usr.getUserName())
					.replace("[@Name]", rmDto.getRawMatlName())
					.replace("[@NewFsName]", (String) mainModel.get("newFlavStatusName"))
					.replace("[@FsName]", (String) mainModel.get("currFlavStatus"));


			if (usr.getAlertPre() == CommonConstants.MESSAGE_PREF_EMAIL) {
				sendHtml(usr, subject, alertDesc);
			} else if (usr.getAlertPre() == CommonConstants.MESSAGE_PREF_NOTIFICATION) {
				alertServ.addAlert(subject, alertDesc, dto.getCheckerId(), String.valueOf(dto.getRmId()),
						CommonConstants.RECORD_TYPE_ID_RAW_MATL, dto.getMakerId());
			} else if (usr.getAlertPre() == CommonConstants.MESSAGE_PREF_EMAIL_NOTIFICATION) {
				sendHtml(usr, subject, alertDesc);
				alertServ.addAlert(subject, alertDesc, dto.getCheckerId(), String.valueOf(dto.getRmId()),
						CommonConstants.RECORD_TYPE_ID_RAW_MATL, dto.getMakerId());
			}

		} else if (sendToRole.equalsIgnoreCase(CommonConstants.ROLE_ID_CHECKER)) {
			//to checker
			
			List<Usr> checkerList = loginServ.searchGrpUser(1);
			checkerList = checkerList.stream()
					.filter(obj -> obj.getDisabledFlag().equals(String.valueOf(CommonConstants.FLAG_NO)))
					.collect(Collectors.toList());

			AlertMessage alertMsg = alertMsgServ.searchAlertById(AlertDefEnum.RM_AMEND_FS.strValue());
			String subject = alertMsg.getSubject().replace("[@Name]", rmDto.getRawMatlName());
			String alertDesc = alertMsg.getDescription().replace("[@Name]", rmDto.getRawMatlName())
					.replace("[@SenderName]", loginServ.searchUser(dto.getMakerId()).getUserName())
					.replace("[@CreatorDate]", formatter.format(currentDate))
					.replace("[@FsName]", (String) mainModel.get("currFlavStatus"))
					.replace("[@NewFsName]", (String) mainModel.get("newFlavStatusName"));

			int alertId = alertServ.saveAlert(subject, alertDesc, loginServ.searchUser(dto.getMakerId()).getEmail(),
					String.valueOf(dto.getRmId()), CommonConstants.RECORD_TYPE_ID_RAW_MATL_AMEND_FS, new Date());

			checkerList.forEach(obj -> {
				if (obj.getAlertPre() == CommonConstants.MESSAGE_PREF_EMAIL) {
					sendHtml(obj, subject, alertDesc);
				} else if (obj.getAlertPre() == CommonConstants.MESSAGE_PREF_NOTIFICATION) {
					alertServ.addAlertRecipient(alertId, obj.getUserId());
					//alertServ.addAlert(subject, alertDesc.replace("[@ReceiverName]", obj.getUserName()), loginServ.searchUser(dto.getMakerId()).getEmail(),
					//		String.valueOf(dto.getRmId()), CommonConstants.RECORD_TYPE_ID_RAW_MATL_AMEND_FS, obj.getUserId());
				} else if (obj.getAlertPre() == CommonConstants.MESSAGE_PREF_EMAIL_NOTIFICATION) {
					sendHtml(obj, subject, alertDesc);
					alertServ.addAlertRecipient(alertId, obj.getUserId());
					//alertServ.addAlert(subject, alertDesc.replace("[@ReceiverName]", obj.getUserName()), loginServ.searchUser(dto.getMakerId()).getEmail(),
					//		String.valueOf(dto.getRmId()), CommonConstants.RECORD_TYPE_ID_RAW_MATL_AMEND_FS, obj.getUserId());
				}
			});

		}
	}

	private void sendEmail(Usr usr, String subject, String content) {

		SimpleMailMessage msg = new SimpleMailMessage();
		msg.setTo(usr.getEmail());
		msg.setSubject(subject);
		msg.setText(content.replace("[@ReceiverName]", usr.getUserName()));

		javaMailSender.send(msg);

	}
	
	private void sendHtml(Usr usr, String subject, String content) {
		MimeMessage message = javaMailSender.createMimeMessage();
		try {
			MimeMessageHelper helper = new MimeMessageHelper(message, false, "utf-8");
			message.setContent(content.replace("[@ReceiverName]", usr.getUserName()), "text/html");
			helper.setTo(usr.getEmail());
			helper.setSubject(subject);
			javaMailSender.send(message);
		} catch (MessagingException e) {
			logger.error(e.toString());
		}
	}

}
