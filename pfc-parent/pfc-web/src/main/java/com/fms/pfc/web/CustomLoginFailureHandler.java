package com.fms.pfc.web;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
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
	
	private static final Logger logger = LoggerFactory.getLogger(CustomLoginFailureHandler.class);

	@Autowired
	private LoginService logSer;

	@Autowired
	private UsrActivityService usrActSer;

	@Autowired
	private AlertMessageService alertMsgServ;

	@Autowired
	private JavaMailSender javaMailSender;

	@Value("${usr.lock.retention}")
	private String USR_LOCK_RET;

	@Override
	public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response,
			AuthenticationException exception) throws IOException, ServletException {
		int maxAttempt = 3;
		HttpSession session = request.getSession();
		String userId = request.getParameter("user_id");
		Usr user = logSer.searchUser(userId);

		exception = authExp(exception, maxAttempt, session, userId, user);

		super.setDefaultFailureUrl("/login?error");
		super.onAuthenticationFailure(request, response, exception);
	}

	private AuthenticationException authExp(AuthenticationException exception, int maxAttempt, HttpSession session,
			String userId, Usr user) {
		if (user != null) {
			if (user.getDisabledFlag().equals("N")) { // normal user
				if (user.getFailedAttemptCnt() < maxAttempt - 1) {
					int retryRemain = maxAttempt - 1 - user.getFailedAttemptCnt();
					logSer.increaseFailedAttempts(user);
					usrActSer.insertUsrActivityLog(userId, "N", "N", session.getId());
					exception = new LockedException(
							"Invalid username and password. " + retryRemain + " retries remaining.");
				} else {
					logSer.lockUser(user, Integer.parseInt(USR_LOCK_RET));
					String future = LocalDateTime.now().plusMinutes(Integer.parseInt(USR_LOCK_RET))
							.format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss a"));

					exception = new LockedException("Your account has been locked due to 3 failed attempts."
							+ " Please try again at " + future);

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
				
				
				if(user.getDisabledFlag().equals("Y") 
						&& user.getFailedAttemptCnt() != 0
						&& null != user.getRelDisableDate()
						&& user.getRelDisableDate().before(new Date())) {
					// disabled user but rel dis datetime less than current datetime
					// for auto release
					logger.debug("authExp() maxAttempt={}, session={}, userId={}",maxAttempt, session.getId(), userId);
					logSer.resetFailedAttempts(userId);
					Usr user1 = logSer.searchUser(userId);
					logger.debug("authExp() flag={}, cnt={}, relDt={}",user1.getDisabledFlag(), user1.getFailedAttemptCnt(), user1.getRelDisableDate());
					exception = new LockedException("Attempt to unlock user '"+user1.getUserId()+"' has been executed. "
							+ "Please try again with the correct credentials.");
					//authExp(exception, maxAttempt, session, user1.getUserId(), user1);
				} else {
					SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyy hh:mm:ss a");
					String future = "";
					if (null != user.getRelDisableDate()) {
						future = "Please try again at " + formatter.format(user.getRelDisableDate());
					}
					exception = new LockedException("Your account has been locked. " + future);					
				}
				
			}
		} else { // no user account
			exception = new LockedException("Invalid username and password.");
		}
		return exception;
	}

}