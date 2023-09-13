package com.fms.pfc.controller.main;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.context.annotation.SessionScope;

import com.fms.pfc.service.api.main.G2LotViewService;

@Controller
@SessionScope
public class CommonController {
	private static final Logger logger = LoggerFactory.getLogger(CommonController.class);

	private G2LotViewService g2LotServ;

	@Autowired
	public CommonController(G2LotViewService g2LotServ) {
		super();
		this.g2LotServ = g2LotServ;
	}

	@GetMapping(value = "/main/pfc/api/getHplModelByHpl")
	@ResponseBody
	public Map<String, Object> getHplModelByHpl(@RequestParam(name = "hpl") String hpl, HttpServletRequest request,
			HttpSession session) {

		List<String> all = g2LotServ.hplModelList(hpl);
		Map<String, Object> modelMap = new HashMap<String, Object>();
		modelMap.put("items", all);

		logger.debug("api>> getHplModelByHpl() hpl={}, items size={}", hpl, all.size());

		return modelMap;
	}

	@GetMapping(value = "/main/pfc/api/getProdLnByHpl")
	@ResponseBody
	public Map<String, Object> getProdLnByHpl(@RequestParam(name = "hplId") String hpl,
			@RequestParam(name = "hplModelId2") String model, @RequestParam(name = "year") String year,
			@RequestParam(name = "mth") String mth, @RequestParam(name = "day") String day, HttpServletRequest request,
			HttpSession session) {

		List<String> all = g2LotServ.prodLnList(hpl, model, year, mth, "");
		Map<String, Object> modelMap = new HashMap<String, Object>();
		modelMap.put("items", all);

		logger.debug("api>> getProdLnByHpl() hpl={}, model={}, year={}, mth={}, day={}, items size={}", hpl, model,
				year, mth, day, all.size());

		return modelMap;
	}

	@GetMapping(value = "/main/pfc/api/getYearByHpl")
	@ResponseBody
	public Map<String, Object> getYearByHpl(@RequestParam(name = "hplId") String hpl,
			@RequestParam(name = "hplModelId2") String model, HttpServletRequest request, HttpSession session) {

		List<String> all = g2LotServ.yearList(hpl, model);
		Map<String, Object> modelMap = new HashMap<String, Object>();
		modelMap.put("items", all);

		logger.debug("api>> getYearByHpl() hpl={}, model={}, items size={}", hpl, model, all.size());

		return modelMap;
	}

	@GetMapping(value = "/main/pfc/api/getMonthByHpl")
	@ResponseBody
	public Map<String, Object> getMonthByHpl(@RequestParam(name = "hplId") String hpl,
			@RequestParam(name = "hplModelId2") String model, @RequestParam(name = "year") String year,
			HttpServletRequest request, HttpSession session) {

		List<String> all = g2LotServ.monthList(hpl, model, year);
		Map<String, Object> modelMap = new HashMap<String, Object>();
		modelMap.put("items", all);

		logger.debug("api>> getMonthByHpl() hpl={}, model={}, year={}, items size={}", hpl, model, year, all.size());

		return modelMap;
	}

	@GetMapping(value = "/main/pfc/api/getDayByHpl")
	@ResponseBody
	public Map<String, Object> getDayByHpl(@RequestParam(name = "hplId") String hpl,
			@RequestParam(name = "hplModelId2") String model, @RequestParam(name = "year") String year,
			@RequestParam(name = "mth") String mth, HttpServletRequest request, HttpSession session) {

		List<String> all = g2LotServ.dayList(hpl, model, year, mth);
		Map<String, Object> modelMap = new HashMap<String, Object>();
		modelMap.put("items", all);

		logger.debug("api>> getDayByHpl() hpl={}, model={}, year={}, mth={}, items size={}", hpl, model, year, mth,
				all.size());

		return modelMap;
	}

	@GetMapping(value = "/main/pfc/api/getSeqByHpl")
	@ResponseBody
	public Map<String, Object> getSeqByHpl(@RequestParam(name = "hplId") String hpl,
			@RequestParam(name = "hplModelId2") String model, @RequestParam(name = "year") String year,
			@RequestParam(name = "mth") String mth, @RequestParam(name = "day") String day,
			@RequestParam(name = "prodLn") String prodLn, HttpServletRequest request, HttpSession session) {

		List<String> all = g2LotServ.seqList(hpl, model, year, mth, day, prodLn);
		Map<String, Object> modelMap = new HashMap<String, Object>();
		modelMap.put("items", all);

		logger.debug("api>> getSeqByHpl() hpl={}, model={}, year={}, mth={}, day={}, prodLn={}, items size={}", hpl,
				model, year, mth, day, prodLn, all.size());

		return modelMap;
	}
	
	@GetMapping(value = "/main/pfc/api/getModelYearMonthByHpl")
	@ResponseBody
	public Map<String, Object> getModelYearMonthByHpl(@RequestParam(name = "hpl") String hpl,
			@RequestParam(name = "hplModel") String model, @RequestParam(name = "year") String year,
			HttpServletRequest request, HttpSession session) {

		Map<String, Object> modelMap = new HashMap<String, Object>();
		List<String> models = g2LotServ.hplModelList(hpl);
		List<String> years = g2LotServ.yearList(hpl, model);
		List<String> months = g2LotServ.monthList(hpl, model, year);
		modelMap.put("models", models);
		modelMap.put("months", months);
		modelMap.put("years", years);

		logger.debug("api>> getModelYearMonthByHpl() hpl={}, model={}, year={}, items size={}", hpl, model, year, modelMap.size());

		return modelMap;
	}
	
	@GetMapping(value = "/main/pfc/api/getModelYearMonthDayByHpl")
	@ResponseBody
	public Map<String, Object> getModelYearMonthDayByHpl(@RequestParam(name = "hpl") String hpl,
			@RequestParam(name = "hplModel") String model, @RequestParam(name = "year") String year,
			@RequestParam(name = "mth") String mth,
			HttpServletRequest request, HttpSession session) {

		Map<String, Object> modelMap = new HashMap<String, Object>();
		List<String> models = g2LotServ.hplModelList(hpl);
		List<String> years = g2LotServ.yearList(hpl, model);
		List<String> months = g2LotServ.monthList(hpl, model, year);
		List<String> days = g2LotServ.dayList(hpl, model, year, mth);
		modelMap.put("models", models);
		modelMap.put("years", years);
		modelMap.put("months", months);
		modelMap.put("days", days);

		logger.debug("api>> getModelYearMonthDayByHpl() hpl={}, model={}, year={}, mth={}, items size={}", hpl, model, year, mth, modelMap.size());

		return modelMap;
	}
	
	@GetMapping(value = "/main/pfc/api/getModelYearMonthDayProdLnByHpl")
	@ResponseBody
	public Map<String, Object> getModelYearMonthDayProdLnByHpl(@RequestParam(name = "hpl") String hpl,
			@RequestParam(name = "hplModel") String model, @RequestParam(name = "year") String year,
			@RequestParam(name = "mth") String mth, @RequestParam(name = "day") String day,
			HttpServletRequest request, HttpSession session) {

		Map<String, Object> modelMap = new HashMap<String, Object>();
		List<String> models = g2LotServ.hplModelList(hpl);
		List<String> years = g2LotServ.yearList(hpl, model);
		List<String> months = g2LotServ.monthList(hpl, model, year);
		List<String> days = g2LotServ.dayList(hpl, model, year, mth);
		List<String> prodLns = g2LotServ.prodLnList(hpl, model, year, mth, "");
		modelMap.put("models", models);
		modelMap.put("years", years);
		modelMap.put("months", months);
		modelMap.put("days", days);
		modelMap.put("prodLns", prodLns);

		logger.debug("api>> getModelYearMonthDayProdLnByHpl() hpl={}, model={}, year={}, mth={}, day={}, items size={}", hpl, model, year, mth, day, modelMap.size());

		return modelMap;
	}
}
