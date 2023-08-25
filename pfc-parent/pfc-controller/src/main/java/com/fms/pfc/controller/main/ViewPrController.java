package com.fms.pfc.controller.main;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.context.annotation.RequestScope;
import org.springframework.web.context.annotation.SessionScope;
import org.springframework.web.servlet.ModelAndView;

@Controller
@SessionScope
@RequestScope
public class ViewPrController {

	private final Logger logger = LoggerFactory.getLogger(ViewPrController.class);

	private ProductRecipeController prc;
	
	private static final String VIEW = "/main/product/prForm";
	private static Map<String, Object> MODEL = new HashMap<String, Object>() ;

	@Autowired
	public ViewPrController(ProductRecipeController prc) {
		super();
		this.prc = prc;
	}

	/**
	 * View PR data
	 * 
	 * @param prId
	 * @param request
	 * @param response
	 * @param session
	 * @return
	 */
	@GetMapping("/main/product/viewPrForm")
	public ModelAndView viewPrdRcp(@RequestParam(name = "prId") int prId, HttpServletRequest request,
			HttpServletResponse response, HttpSession session) {
		MODEL = prc.viewPrdRcp(prId, request, response, session).getModel();
		return new ModelAndView(VIEW, MODEL);
	}

	/**
	 * Get ingredient data
	 * 
	 * @param request
	 * @param id
	 * @param session
	 * @return
	 */
	@GetMapping("/main/product/viewPrFormPRGetData")
	public ModelAndView retrieveProductPRFormData(HttpServletRequest request, @RequestParam(name = "id") int id,
			HttpSession session) {
		MODEL = prc.retrieveProductPRFormData(request, id, session).getModel();
		return new ModelAndView(VIEW, MODEL);
	}

	/**
	 * Get rm-stat data
	 * 
	 * @param request
	 * @param id
	 * @param session
	 * @return
	 */
	@GetMapping("/main/product/viewPrFormFCGetData")
	public ModelAndView retrieveProductFCFormData(HttpServletRequest request, @RequestParam(name = "id") int id,
			HttpSession session) {
		MODEL = prc.retrieveProductFCFormData(request, id, session).getModel();
		return new ModelAndView(VIEW, MODEL);
	}

	/**
	 * Get food additive data
	 * 
	 * @param request
	 * @param id
	 * @param session
	 * @return
	 */
	@GetMapping("/main/product/viewPrFormFAGetData")
	public ModelAndView retrieveProductFAFormData(HttpServletRequest request, @RequestParam(name = "id") int id,
			HttpSession session) {
		MODEL = prc.retrieveProductFAFormData(request, id, session).getModel();
		return new ModelAndView(VIEW, MODEL);
	}

	/**
	 * Get regulation data
	 * 
	 * @param request
	 * @param countryId
	 * @param session
	 * @return
	 */
	@GetMapping("/main/product/viewPrFormRegGetData")
	public ModelAndView retrieveRegTableData(HttpServletRequest request, @RequestParam(name = "id") String countryId,
			HttpSession session) {
		MODEL = prc.retrieveRegTableData(request, countryId, session).getModel();
		return new ModelAndView(VIEW, MODEL);
	}

	/**
	 * Download regulation file
	 * 
	 * @param request
	 * @param regDocId
	 * @return
	 */
	@GetMapping("/main/product/viewPrFormDownloadRegFile")
	public ResponseEntity<byte[]> downloadRegulationFile(HttpServletRequest request,
			@RequestParam(name = "id") int regDocId) {
		return prc.downloadRegulationFile(request, regDocId);
	}

	/**
	 * Get attachment data
	 * 
	 * @param request
	 * @param id
	 * @param session
	 * @return
	 */
	@GetMapping("/main/product/viewPrFormDocGetData")
	public ModelAndView retrieveDocTableData(HttpServletRequest request, @RequestParam(name = "id") int id,
			HttpSession session) {
		MODEL = prc.retrieveDocTableData(request, id, session).getModel();
		return new ModelAndView(VIEW, MODEL);
	}

	/**
	 * Download attachment
	 * 
	 * @param request
	 * @param docId
	 * @param session
	 * @return
	 */
	@GetMapping("/main/product/viewPrFormDocDwn")
	public ResponseEntity<byte[]> downloadDocumentFile(HttpServletRequest request, @RequestParam(name = "id") int docId,
			HttpSession session) {
		return prc.downloadDocumentFile(request, docId, session);
	}

	/**
	 * Get RM regulation information
	 * 
	 * @param rawMatlName
	 * @param countryId
	 * @param prCode
	 * @param session
	 * @return
	 */
	@RequestMapping(value = "/main/product/viewPrFormIngReg", method = RequestMethod.GET)
	@ResponseBody
	public Map<String, Object> getIngRegulation(@RequestParam(name = "rawMatlName") String rawMatlName,
			@RequestParam(name = "countryId") String countryId, @RequestParam(name = "prCode") String prCode,
			HttpSession session) {
		return prc.getIngRegulation(rawMatlName, countryId, prCode, session);
	}
}
