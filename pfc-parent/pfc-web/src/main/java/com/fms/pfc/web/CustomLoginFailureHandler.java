package com.fms.pfc.web;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;
import org.springframework.stereotype.Component;

import com.fms.pfc.domain.model.AlertMessage;
import com.fms.pfc.domain.model.Usr;
import com.fms.pfc.service.api.base.AlertMessageService;
import com.fms.pfc.service.api.base.LoginService;
import com.fms.pfc.service.api.base.UsrActivityService;

@Component
public class CustomLoginFailureHandler extends SimpleUrlAuthenticationFailureHandler {

	@Autowired
	private LoginService logSer;

	@Autowired
	private UsrActivityService usrActSer;

	@Autowired
	private AlertMessageService alertMsgServ;

	@Autowired
	private JavaMailSender javaMailSender;

	@Override
	public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response,
			AuthenticationException exception) throws IOException, ServletException {
		int maxAttempt = 3;
		HttpSession session = request.getSession();
		String userId = request.getParameter("user_id");
		Usr user = logSer.searchUser(userId);

		if (user != null) {
			if (!user.getDisabledFlag().equals("Y")) {
				if (user.getFailedAttemptCnt() < maxAttempt - 1) {
					int retryRemain = maxAttempt - 1 - user.getFailedAttemptCnt();
					logSer.increaseFailedAttempts(user);
					usrActSer.insertUsrActivityLog(userId, "N", "N", session.getId());
					exception = new LockedException(
							"Invalid username and password. " + retryRemain + " retries remaining.");
				} else {
					logSer.lockUser(user);
					exception = new LockedException("Your account has been locked due to 3 failed attempts.");

					try {
						AlertMessage alertMsg = alertMsgServ.searchAlertById("USER-DISABLED");

						String content = alertMsg.getDescription().replace("[@UserID]", user.getUserId())
								.replace("[@UserName]", user.getUserName());

						SimpleMailMessage msg = new SimpleMailMessage();
						msg.setTo(user.getEmail());
						msg.setSubject(alertMsg.getSubject());
						msg.setText(content);

						javaMailSender.send(msg);

					} catch (Exception e) {
						e.printStackTrace();
					}

				}
			} else {
				exception = new LockedException("Your account has been locked.");
			}
		} else {
			exception = new LockedException("Invalid username and password.");
		}

		super.setDefaultFailureUrl("/login?error");
		super.onAuthenticationFailure(request, response, exception);
	}

}