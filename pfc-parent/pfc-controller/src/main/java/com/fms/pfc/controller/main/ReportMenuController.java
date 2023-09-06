package com.fms.pfc.controller.main;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.context.annotation.SessionScope;
import org.springframework.web.servlet.ModelAndView;

import com.fms.pfc.common.Authority;
import com.fms.pfc.service.api.main.G2LotViewService;
import com.fms.pfc.service.api.main.ProdFileService;

import lombok.Data;

@Controller
@SessionScope
public class ReportMenuController {
	private static final Logger logger = LoggerFactory.getLogger(ReportMenuController.class);
	
	private Authority auth;
	private G2LotViewService g2Serv;
	private ProdFileService pfServ;
	
	private Map<String, Object> model = new HashMap<String, Object>();
	
	@Autowired
	public ReportMenuController(Authority auth, G2LotViewService g2Serv, ProdFileService pfServ) {
		super();
		this.auth = auth;
		this.g2Serv = g2Serv;
		this.pfServ = pfServ;
	}
	
	@GetMapping("/menu/reportMenux")
	public ModelAndView init(HttpServletRequest request) {
		
		return new ModelAndView("menu/reportMenu", model);
	}
	
	@Data
	public class BarChartJs{
		private String label;
		private String backgroundColor;
		private List<Integer> data;
		
	}
	
	@Data
	public class PieChartJs{
		private List<String> backgroundColor;
		private List<Integer> data;
	}
	
	
}
