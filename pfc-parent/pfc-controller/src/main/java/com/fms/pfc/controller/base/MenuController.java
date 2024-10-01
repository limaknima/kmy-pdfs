package com.fms.pfc.controller.base;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.context.annotation.SessionScope;
import org.springframework.web.servlet.ModelAndView;

import com.fms.pfc.common.Authority;
import com.fms.pfc.common.CommonUtil;
import com.fms.pfc.domain.model.Usr;
import com.fms.pfc.service.api.base.AlertService;
import com.fms.pfc.service.api.base.LoginService;
import com.fms.pfc.service.api.main.G2LotViewService;
import com.fms.pfc.service.api.main.ProdFileService;

import lombok.Data;

@Controller
@SessionScope
public class MenuController {
	
	private final Logger logger = LoggerFactory.getLogger(MenuController.class);
			
	private LoginService logServ;
	private AlertService alertServ;	
	private Authority auth;
	private G2LotViewService g2Serv;
	private ProdFileService pfServ;
	
	private List<Object[]> lotCount = new ArrayList<>();
	private List<Object[]> fileCount = new ArrayList<>();
	
	@Autowired
	public MenuController(LoginService logServ, AlertService alertServ, Authority auth, G2LotViewService g2Serv, ProdFileService pfServ) {
		super();
		this.logServ = logServ;
		this.alertServ = alertServ;
		this.auth = auth;
		this.g2Serv = g2Serv;
		this.pfServ = pfServ;
		
		this.lotCount = g2Serv.lotCountByHpl();
		this.fileCount = pfServ.fileCountByHpl2();
	}	

	@GetMapping("/")
	public ModelAndView loadHomepage(HttpServletRequest request) {

		Map<String, Object> model = new HashMap<String, Object>();

		model = auth.onPageLoad(model, request);
		Usr usr = logServ.searchUser(request.getRemoteUser());
		model.put("loggedUserName", usr.getUserName());

		int alertCnt = alertServ.getCount(request.getRemoteUser());

		Date todayDate = new Date();
		boolean changePass = false;
		if (usr.getPasswordSetDate() != null) {
			if (todayDate.after(usr.getPasswordSetDate())) {
				changePass = true;
			}
		}

		boolean showReminder = false;
		if (alertCnt > 0 || changePass) {
			showReminder = true;
		}

		model.put("alertCount", alertCnt);
		model.put("changePass", changePass);
		model.put("showReminder", showReminder);
		// for quick info card
		{
			String lotDesc = "(";			
			for (Object[] ob : lotCount) {
				String hpl = (String)ob[0];
				String cnt = String.valueOf(ob[1]);
				String appnd = hpl+"="+cnt+", ";
				lotDesc +=appnd;
			}			
			lotDesc = lotDesc.substring(0, lotDesc.lastIndexOf(", "));			
			lotDesc +=")";			
			model.put("lotCount", g2Serv.countAll());
			model.put("lotDesc", lotDesc);			
			
			int sum = 0;
			String fileDesc = "(";			
			for (Object[] ob : fileCount) {
				String hpl = (String)ob[0];
				String cnt = String.valueOf(ob[1]);
				String appnd = hpl+"="+cnt+", ";
				sum += Integer.parseInt(cnt);
				fileDesc +=appnd;
			}			
			fileDesc = fileDesc.substring(0, fileDesc.lastIndexOf(", "));			
			fileDesc +=")";
			//model.put("prodFileCount", pfServ.countAll());
			model.put("prodFileCount", sum);
			model.put("prodFileDesc", fileDesc);
		}
        // for chart
        {
        	//generatePieChart(model);        	
        	generateBarChart(model);
        	generateStackedBarChart(model);
        }
        // for tables
		{
			String year = String.valueOf(LocalDate.now().getYear());
			model.put("lotTop5", g2Serv.searchTop5ByCriteria("", "", "", "", "", "", "", ""));
			model.put("fileTop5", pfServ.searchTop5ByCriteria("", "", "", "", "", "", "", ""));
		}

		return new ModelAndView("homepage", model);
	}

	/**
	 * @param model
	 */
	private void generatePieChart(Map<String, Object> model) {
		List<Object[]> result = lotCount;
		List<String> cols = result.stream().map(arg0 -> (String) arg0[0]).sorted().collect(Collectors.toList());
		// List<String> data = result.stream().map(arg0 ->
		// arg0[1].toString()).collect(Collectors.toList());
		model.put("cols", String.join(",", cols));
		// model.put("data", String.join(",", data));

		PieChartJs pcLot = new PieChartJs();
		List<String> bg = new ArrayList<String>();
		pcLot.setData(result.stream().map(arg0 -> (Integer) arg0[1]).collect(Collectors.toList()));
		for (int idx = 0; idx < result.size(); idx++) {
			//bg.add(CommonUtil.randomHexColor());
			bg.add(CommonUtil.hplHexColor().get(result.get(idx)[0]));
		}
		pcLot.setBackgroundColor(bg);		

		List<Object[]> result2 = fileCount;
		PieChartJs pcFile = new PieChartJs();
//		List<String> bg2 = new ArrayList<String>();
		pcFile.setData(result2.stream().map(arg0 -> (Integer) arg0[1]).collect(Collectors.toList()));
//		for (int idx = 0; idx < result.size(); idx++) {
//			bg2.add(CommonUtil.randomHexColor());
//		}
		pcFile.setBackgroundColor(bg);

		List<PieChartJs> pcList = new ArrayList<MenuController.PieChartJs>();
		pcList.add(pcLot);
		pcList.add(pcFile);

		logger.debug("pcjs ={}", pcList);
		model.put("pcList", pcList);
	}

	/**
	 * @param model
	 */
	private void generateBarChart(Map<String, Object> model) {
		LocalDate d = LocalDate.now();
		int year = d.getYear();
		int month = d.getMonthValue();
		List<BarChartJs> bars = new ArrayList<MenuController.BarChartJs>();
		List<String> hpls = g2Serv.hplList();
		for (String hpl : hpls) {
			BarChartJs bar = new BarChartJs();
			bar.setLabel(hpl);
			bar.setName(hpl);
			bar.setBackgroundColor(CommonUtil.hplHexColor().get(hpl));
			
			List<Integer> mths = new ArrayList<Integer>();
			for(int idx = 1 ; idx <=month; idx ++) {
				mths.add(pfServ.countFileByCriteria(hpl, year, idx));
			}
			bar.setData(mths);
			bars.add(bar);				
		}     
		
		model.put("bars", bars);
	}
	
	private void generateStackedBarChart(Map<String, Object> model) {
		List<String> cols = lotCount.stream().map(arg0 -> (String) arg0[0]).sorted().collect(Collectors.toList());
		model.put("cols", String.join(",", cols));
		List<BarChartJs> bars = new ArrayList<MenuController.BarChartJs>();
		List<String> lbls = Arrays.asList("Completed G2 lot", "File upload");
		for (String lbl : lbls) {
			BarChartJs bar = new BarChartJs();
			bar.setLabel(lbl);
			bar.setBackgroundColor(CommonUtil.randomHexColor());
			
			List<Integer> mths = new ArrayList<Integer>();
			if(lbl.equals("Completed G2 lot")) {
				List<Object[]> fls = this.lotCount;
				List<Integer> ints = fls.stream().map(ob -> Integer.parseInt(String.valueOf(ob[1])))
						.collect(Collectors.toList());
				mths.addAll(ints);
				
			} else {
				List<Object[]> fls = this.fileCount;
				List<Integer> ints = fls.stream().map(ob -> Integer.parseInt(String.valueOf(ob[1])))
						.collect(Collectors.toList());
				mths.addAll(ints);
			}
			
			bar.setData(mths);
			bars.add(bar);				
		}     
		
		model.put("stackedBars", bars);
	}
	
	private void generatePieChart2(Map<String, Object> model) {
		List<Object[]> result = lotCount;
		//List<String> cols = result.stream().map(arg0 -> (String) arg0[0]).sorted().collect(Collectors.toList());
		// List<String> data = result.stream().map(arg0 ->
		// arg0[1].toString()).collect(Collectors.toList());
		//model.put("cols", String.join(",", cols));
		// model.put("data", String.join(",", data));

		List<PieChartData> pcList = new ArrayList<PieChartData>();
		for (Object[] obj : result) {
			PieChartData pc = new PieChartData();
			pc.setName((String)obj[0]);
			pc.setY((Integer)obj[1]);
			pcList.add(pc);
		}
		
		model.put("pcList", pcList);
	}

	@GetMapping("/menu/regulationMenu")
	public ModelAndView regulationMenu(HttpServletRequest request, HttpServletResponse response) {

		Map<String, Object> model = new HashMap<String, Object>();

		model = auth.onPageLoad(model, request);
		auth.isAuthUrl(request, response);

		return new ModelAndView("menu/regulationMenu", model);
	}

	@GetMapping("/menu/rawMaterialMenu")
	public ModelAndView rawMaterialMenu(HttpServletRequest request, HttpServletResponse response) {

		Map<String, Object> model = new HashMap<String, Object>();

		model = auth.onPageLoad(model, request);
		auth.isAuthUrl(request, response);

		return new ModelAndView("menu/rawMaterialMenu", model);
	}

	@GetMapping("/menu/recipeMenu")
	public ModelAndView recipeMenu(HttpServletRequest request, HttpServletResponse response) {

		Map<String, Object> model = new HashMap<String, Object>();

		model = auth.onPageLoad(model, request);
		auth.isAuthUrl(request, response);

		return new ModelAndView("menu/recipeMenu", model);
	}

	@GetMapping("/menu/permissibilityMenu")
	public ModelAndView permissibilityMenu(HttpServletRequest request, HttpServletResponse response) {

		Map<String, Object> model = new HashMap<String, Object>();

		model = auth.onPageLoad(model, request);
		auth.isAuthUrl(request, response);

		return new ModelAndView("menu/permissibilityMenu", model);
	}

	@GetMapping("/menu/letterMenu")
	public ModelAndView letterMenu(HttpServletRequest request, HttpServletResponse response) {

		Map<String, Object> model = new HashMap<String, Object>();

		model = auth.onPageLoad(model, request);
		auth.isAuthUrl(request, response);

		return new ModelAndView("menu/letterMenu", model);
	}

	@GetMapping("/menu/reportMenu")
	public ModelAndView reportMenu(HttpServletRequest request, HttpServletResponse response) {

		Map<String, Object> model = new HashMap<String, Object>();

		model = auth.onPageLoad(model, request);
		auth.isAuthUrl(request, response);
		
		generateBarChart(model);
		generatePieChart2(model);

		return new ModelAndView("menu/reportMenu", model);
	}

	@GetMapping("/menu/adminMenu")
	public ModelAndView adminMenu(HttpServletRequest request, HttpServletResponse response) {

		Map<String, Object> model = new HashMap<String, Object>();

		model = auth.onPageLoad(model, request);
		auth.isAuthUrl(request, response);

		return new ModelAndView("menu/adminMenu", model);
	}

	@GetMapping("/menu/maintenanceMenu")
	public ModelAndView maintenanceMenu(HttpServletRequest request, HttpServletResponse response) {

		Map<String, Object> model = new HashMap<String, Object>();

		model = auth.onPageLoad(model, request);
		auth.isAuthUrl(request, response);

		return new ModelAndView("menu/maintenanceMenu", model);
	}

	@GetMapping("/menu/activityMenu")
	public ModelAndView activityMenu(HttpServletRequest request, HttpServletResponse response) {

		Map<String, Object> model = new HashMap<String, Object>();

		model = auth.onPageLoad(model, request);
		auth.isAuthUrl(request, response);

		return new ModelAndView("menu/activityMenu", model);
	}

	@GetMapping("/menu/prodFileMenu")
	public ModelAndView prodFileMenu(HttpServletRequest request, HttpServletResponse response) {

		Map<String, Object> model = new HashMap<String, Object>();

		model = auth.onPageLoad(model, request);
		auth.isAuthUrl(request, response);

		return new ModelAndView("menu/prodFileMenu", model);
	}
	
	@Data
	public class BarChartJs{
		private String label;
		private String name;
		private String backgroundColor;
		private List<Integer> data;
		
	}
	
	@Data
	public class PieChartJs{
		private List<String> backgroundColor;
		private List<Integer> data;
	}
	
	@Data
	public class PieChartData{
		private String name;
		private int y;
	}

}
