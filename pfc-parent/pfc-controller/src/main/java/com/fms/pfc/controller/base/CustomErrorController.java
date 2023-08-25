package com.fms.pfc.controller.base;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.context.annotation.SessionScope;
import org.springframework.web.servlet.ModelAndView;

import com.fms.pfc.common.Authority;

@Controller
@SessionScope
public class CustomErrorController implements ErrorController {

	private final Logger logger = LoggerFactory.getLogger(CustomErrorController.class);

	private Map<String, Object> model = new HashMap<String, Object>();
	private Authority auth;
	private MessageSource messageSource;

	@Autowired
	public CustomErrorController(Authority auth, MessageSource messageSource) {
		this.auth = auth;
		this.messageSource = messageSource;
	}

	@Override
	public String getErrorPath() {
		return "/error";
	}

	@GetMapping("/error")
	public ModelAndView handleError(HttpServletRequest request, HttpServletResponse response) {
		//model = auth.onPageLoad(model, request);
		
		int code = response.getStatus();
		StringBuffer codeDesc = new StringBuffer();

		if (code == HttpStatus.NOT_FOUND.value()) {
			codeDesc.append(code).append("-").append(HttpStatus.NOT_FOUND.getReasonPhrase());
		} else if (code == HttpStatus.FORBIDDEN.value()) {
			codeDesc.append(code).append("-").append(HttpStatus.FORBIDDEN.getReasonPhrase());
		} else if (code == HttpStatus.INTERNAL_SERVER_ERROR.value()) {
			codeDesc.append(code).append("-").append(HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase());
		} else {
			codeDesc.append(messageSource.getMessage("errRuntime2", new Object[] {}, Locale.getDefault()));
		}

		model.put("codeDesc", codeDesc.toString());

		logger.error("Error with code {} happened! Do something!", codeDesc);

		return new ModelAndView("error", model);

	}

}
