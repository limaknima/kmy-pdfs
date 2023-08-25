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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.context.annotation.SessionScope;
import org.springframework.web.servlet.ModelAndView;

@Controller
@SessionScope
public class ViewRmController {

	private final Logger logger = LoggerFactory.getLogger(ViewRmController.class);

	private RawMaterialController rmc;
	
	private static final String VIEW = "/main/material/rmForm";
	private static Map<String, Object> MODEL = new HashMap<String, Object>() ;

	@Autowired
	public ViewRmController(RawMaterialController rmc) {
		super();
		this.rmc = rmc;
	}

	/**
	 * View Data
	 * @param rawMatlId
	 * @param request
	 * @param response
	 * @param session
	 * @return
	 */
	@GetMapping("/main/material/viewRmForm")
	public ModelAndView viewRawMatl(@RequestParam(name = "matlId") int rawMatlId, HttpServletRequest request,
			HttpServletResponse response, HttpSession session) {
		MODEL = rmc.viewRawMatl(rawMatlId, request, response, session).getModel();
		return new ModelAndView(VIEW, MODEL); 
	}

	/**
	 * Get manufacturer data
	 * @param request
	 * @param id
	 * @param session
	 * @return
	 */
	@GetMapping("/main/material/viewRmFormGetData")
	public ModelAndView retrieveManufTableData(HttpServletRequest request, @RequestParam(name = "id") int id,
			HttpSession session) {
		MODEL = rmc.retrieveManufTableData(request, id, session).getModel();
		return new ModelAndView(VIEW, MODEL);
	}

	/**
	 * Download vipd file
	 * @param request
	 * @param id
	 * @param session
	 * @return
	 */
	@GetMapping("/main/material/viewRmFormDownloadVipd")
	public ResponseEntity<byte[]> downloadVipdFile(HttpServletRequest request, @RequestParam(name = "id") int id,
			HttpSession session) {
		return rmc.downloadVipdFile(request, id, session);
	}

	/**
	 * Download supplier declaration file
	 * @param request
	 * @param id
	 * @param session
	 * @return
	 */
	@GetMapping("/main/material/viewRmFormDownloadSuppl")
	public ResponseEntity<byte[]> downloadSupplFile(HttpServletRequest request, @RequestParam(name = "id") int id,
			HttpSession session) {
		return rmc.downloadSupplFile(request, id, session);
	}

	/**
	 * Get ingredient data
	 * @param request
	 * @param id
	 * @param session
	 * @return
	 */
	@GetMapping("/main/material/viewRmFormIngGetData")
	public ModelAndView retrieveIngTableData(HttpServletRequest request, @RequestParam(name = "id") int id,
			HttpSession session) {
		MODEL = rmc.retrieveIngTableData(request, id, session).getModel();
		return new ModelAndView(VIEW, MODEL);
	}

	/**
	 * Get regulation data
	 * @param request
	 * @param id
	 * @param session
	 * @return
	 */
	@GetMapping("/main/material/viewRmFormRegGetData")
	public ModelAndView retrieveRegTableData(HttpServletRequest request, @RequestParam(name = "id") int id,
			HttpSession session) {
		MODEL = rmc.retrieveRegTableData(request, id, session).getModel();
		return new ModelAndView(VIEW, MODEL);
	}

	/**
	 * Download regulation file
	 * @param request
	 * @param regDocId
	 * @return
	 */
	@GetMapping("/main/material/viewRmFormDownloadReg")
	public ResponseEntity<byte[]> downloadRegulationFile(HttpServletRequest request,
			@RequestParam(name = "id") int regDocId) {
		return rmc.downloadRegulationFile(request, regDocId);
	}
	
	/**
	 * Download attachment
	 * @param request
	 * @param docId
	 * @param session
	 * @return
	 */
	@GetMapping("/main/material/viewRmFormDocDwn")
	public ResponseEntity<byte[]> downloadDocumentFile(HttpServletRequest request, @RequestParam(name = "id") int docId,
			HttpSession session) {		
		return rmc.downloadDocumentFile(request, docId, session);
	}
}
