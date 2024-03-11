package com.fms.pfc.controller.base;

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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.context.annotation.SessionScope;
import org.springframework.web.servlet.ModelAndView;

import com.fms.pfc.common.Authority;
import com.fms.pfc.common.CommonConstants;
import com.fms.pfc.domain.model.PassHistory;
import com.fms.pfc.domain.model.Usr;
import com.fms.pfc.service.api.base.GroupService;
import com.fms.pfc.service.api.base.LoginService;
import com.fms.pfc.service.api.base.PassHisService;
import com.fms.pfc.service.api.base.TrxHisService;
import com.fms.pfc.service.api.base.UsrRoleService;
import com.fms.pfc.validation.common.CommonValidation;

@Controller
@SessionScope
public class ProfileController {
	
	private static final Logger logger = LoggerFactory.getLogger(ProfileController.class);
	
	private LoginService loginServ;	
	private UsrRoleService usrRoleServ;
	private PassHisService pwdHistServ;	
	private GroupService groupServ;	
	private CommonValidation commonValServ;	
	private Authority auth;
	private TrxHisService trxHistServ;
	
	private Map<String, Object> model = new HashMap<String, Object>();

	@Autowired
	public ProfileController(LoginService loginServ, UsrRoleService usrRoleServ, PassHisService pwdHistServ,
			GroupService groupServ, CommonValidation commonValServ, Authority auth, TrxHisService trxHistServ) {
		super();
		this.loginServ = loginServ;
		this.usrRoleServ = usrRoleServ;
		this.pwdHistServ = pwdHistServ;
		this.groupServ = groupServ;
		this.commonValServ = commonValServ;
		this.auth = auth;
		this.trxHistServ = trxHistServ;
	}

	@GetMapping("/base/tray/myProfile")
	public ModelAndView loadProfile(HttpServletRequest request, HttpServletResponse response) {

		//Map<String, Object> model = new HashMap<String, Object>();

		model = auth.onPageLoad(model, request);
		auth.isAuthUrl(request, response);
		Usr user = loginServ.searchUser(request.getRemoteUser());
		model.put("user", user);
		model.put("pwChgSucc", false);
		
		//20240308 - Logout modal
		
		setData(model, user, request);

		//request.getSession().setAttribute("sessionModel", model);

		return new ModelAndView("/base/tray/myProfile", model);
	}

	@GetMapping("/base/tray/changePassword")
	public ModelAndView loadChangePassword(HttpServletRequest request, HttpServletResponse response) {

		//Map<String, Object> model = new HashMap<String, Object>();

		model = auth.onPageLoad(model, request);
		auth.isAuthUrl(request, response);
		
		if (null != model.get("pwChgSucc") && (Boolean) model.get("pwChgSucc")) {
			logger.debug("pwChgSucc true");
		} else {
			logger.debug("pwChgSucc false");
		}
		
		//model.put("pwChgSucc", false);

		//request.getSession().setAttribute("sessionModel", model);

		return new ModelAndView("/base/tray/changePassword", model);
	}

	@PostMapping("/base/tray/changePassword")
	public ModelAndView updatePassword(@RequestParam(name = "oldPass") String oldPass,
			@RequestParam(name = "newPass") String newPass, @RequestParam(name = "conNewPass") String conNewPass,
			HttpServletRequest request, HttpSession session) {

		model.put("pwChgSucc", false);

		//Map<String, Object> model = (Map<String, Object>) session.getAttribute("sessionModel");

		//model = auth.onPageLoad(model, request);
		BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
		boolean error = false;
		String errorMsg = "";
		String pattern = "(?=.*[0-9])(?=.*[a-z]).{8,10}"; // FSGS) Faiz 2021/3/4 - limit password to 10 chars

		errorMsg += commonValServ.validateMandatoryInput(oldPass, "Old Password");
		errorMsg += commonValServ.validateMandatoryInput(newPass, "New Password");
		errorMsg += commonValServ.validateMandatoryInput(conNewPass, "Confirm New Password");

		if (errorMsg.length() != 0) {
			model.put("error", errorMsg);
			model.remove("success");
			return new ModelAndView("/base/tray/changePassword", model);
		}

		Usr user = loginServ.searchUser(request.getRemoteUser());
		boolean isPasswordMatch = passwordEncoder.matches(oldPass.trim(), user.getPassword());

		List<PassHistory> passHis = pwdHistServ.searchHistory(request.getRemoteUser());
		for (int i = 0; i < passHis.size(); i++) {
			boolean oldPassMatch = passwordEncoder.matches(newPass.trim(), passHis.get(i).getPassword());
			if (oldPassMatch) {
				model.put("error", "New password cannot same as password history!");
				error = true;
			}
		}

		if (oldPass.equals(newPass)) {
			model.put("error", "Old password is same as new password!");
			error = true;
		} else if (!newPass.equals(conNewPass)) {
			model.put("error", "New password do not match with confirm new password!");
			error = true;
		} else if (!isPasswordMatch) {
			model.put("error", "Old password do not match with current password!");
			error = true;
		} else if (oldPass.equals("") || newPass.equals("") || conNewPass.equals("")) {
			model.put("error", "Please fill out all required field!");
			error = true;
		} else if (!newPass.matches(pattern)) {
			model.put("error", "New password regex pattern is incorrect!");
			error = true;
		}

		if (!error) {
			try {				
				String encodedPassword = passwordEncoder.encode(newPass.trim());
				logger.debug("chgPwdNew={}, encoded={}",newPass,encodedPassword);
				
				loginServ.updateUserPass(request.getRemoteUser(), encodedPassword);
				pwdHistServ.insertPassHis(request.getRemoteUser(), user.getPassword());
				model.put("success", "Password changed successfully! Please logout and login again with new password!");
				model.remove("error");
				model.put("pwChgSucc", true);
				
				//20240308 - Logout modal
				
				
				// FSGS) Kent 26/2/2021 Add/changed - Add Function executed log START
				trxHistServ.addTrxHistory(new Date(), "Update Password", request.getRemoteUser(),
						CommonConstants.FUNCTION_TYPE_UPDATE, user.getUserId(),
						CommonConstants.RECORD_TYPE_ID_MY_PROFILE, null);
				// FSGS) Kent 26/2/2021 Add/changed - Add Function executed log END
			} catch (Exception e) {
				model.put("error", "Password fail to change!");
				model.remove("success");
			}
		} else {
			model.put("oldPass", oldPass);
			model.put("newPass", newPass);
			model.put("conNewPass", conNewPass);
			model.put("pwChgSucc", false);
			model.remove("success");
		}

		return new ModelAndView("/base/tray/changePassword", model);
	}

	@PostMapping("/base/tray/myProfile")
	public ModelAndView updateProfile(@Valid Usr usr, HttpServletRequest request, HttpSession session) {

		//Map<String, Object> model = (Map<String, Object>) session.getAttribute("sessionModel");

		//model = auth.onPageLoad(model, request);
		String errorMsg = "";
		errorMsg += commonValServ.validateMandatoryInput(usr.getUserName(), "User Name");
		errorMsg += commonValServ.validateInputLength(usr.getUserName(), 100, "User Name");
		errorMsg += commonValServ.validateMandatoryInput(usr.getEmail(), "Email");
		errorMsg += commonValServ.validateInputLength(usr.getEmail(), 50, "Email");
		errorMsg += commonValServ.validateEmailFormat(usr.getEmail(), "Email");
		errorMsg += commonValServ.validateDateFormat(usr.getEffecDateFr(), "Date from");
		errorMsg += commonValServ.validateDateFormat(usr.getEffecDateTo(), "Date to");
		errorMsg += commonValServ.validateDateRange(usr.getEffecDateFr(), usr.getEffecDateTo(), "Date from", "Date to");

		if (errorMsg.length() != 0) {
			model.put("error", errorMsg);
			Usr user = loginServ.searchUser(request.getRemoteUser());
			model.put("user", usr);
			setData(model, user, request);
			return new ModelAndView("/base/tray/myProfile", model);
		}

		try {
			loginServ.updateUser(usr.getUserId(), usr.getUserName(), usr.getEmail(), usr.getEffecDateFr(),
					usr.getEffecDateTo(), usr.getAlertPre(), request.getRemoteUser());

			Usr user = loginServ.searchUser(request.getRemoteUser());
			model.put("user", usr);
			setData(model, user, request);
			model.put("success", "Record updated successfully.");

			// FSGS) Kent 26/2/2021 Add/changed - Add Function executed log START
			trxHistServ.addTrxHistory(new Date(), "Update Profile", request.getRemoteUser(),
					CommonConstants.FUNCTION_TYPE_UPDATE, usr.getUserId(), CommonConstants.RECORD_TYPE_ID_MY_PROFILE,
					null);
			// FSGS) Kent 26/2/2021 Add/changed - Add Function executed log END
		} catch (Exception e) {
			model.put("error", "Record fail to update.");
		}

		return new ModelAndView("/base/tray/myProfile", model);
	}

	private void setData(Map<String, Object> model, Usr user, HttpServletRequest request) {

		model.put("createdUser", user.getCreatorId());
		model.put("modifiedUser", user.getModifierId());

		DateFormat formatter = new SimpleDateFormat("dd/MM/yyy hh:mm:ss a");

		try {
			model.put("createdDate", formatter.format(user.getCreatedDate()));
		} catch (Exception e) {
			model.put("createdDate", "--");
		}

		try {
			model.put("modifiedDate", formatter.format(user.getModifiedDate()));
		} catch (Exception e) {
			model.put("modifiedDate", "--");
		}

		model.put("userRoles", usrRoleServ.searchUserRole(request.getRemoteUser()));
		model.put("groupId", groupServ.searchGroup("", user.getGroupId()).get(0).getGroupName());
	}

}
