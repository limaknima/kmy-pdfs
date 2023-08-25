package com.fms.pfc.web;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import com.fms.pfc.domain.model.Usr;
import com.fms.pfc.service.api.base.LoginService;
import com.fms.pfc.service.api.base.UsrActivityService;

@Component
public class CustomLoginSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {
 
    @Autowired
    private LoginService logSer;
    
    @Autowired
    private UsrActivityService usrActSer;
    
    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
            Authentication authentication) throws IOException, ServletException {
    	String userId = request.getParameter("user_id");
    	Usr user = logSer.searchUser(userId);
    	
    	HttpSession session = request.getSession();
    	usrActSer.insertUsrActivityLog(userId, "Y", "N", session.getId());
    	
        if (user.getFailedAttemptCnt() > 0) {
            logSer.resetFailedAttempts(userId);
        }
         
        super.onAuthenticationSuccess(request, response, authentication);
    }
     
}