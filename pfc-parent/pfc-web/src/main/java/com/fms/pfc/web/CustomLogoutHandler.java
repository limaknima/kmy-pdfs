package com.fms.pfc.web;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.springframework.stereotype.Service;

import com.fms.pfc.service.api.base.UsrActivityService;

@Service
public class CustomLogoutHandler implements LogoutHandler {
	
	@Autowired
    private UsrActivityService usrActSer;
	
    @Override
    public void logout(HttpServletRequest request, HttpServletResponse response, 
      Authentication authentication) {
    	HttpSession session = request.getSession();
		usrActSer.updateUsrActivityLog(session.getId());
		
		//invalidate current session
		session.invalidate();
    }
}