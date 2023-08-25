package com.fms.pfc.controller.base;

import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import com.fms.pfc.common.Authority;
import com.fms.pfc.common.CommonConstants;
import com.fms.pfc.domain.model.Document;
import com.fms.pfc.service.api.base.AttachmentService;
import com.fms.pfc.service.api.base.DocumentTypeService;
import com.fms.pfc.service.api.base.TrxHisService;
import com.fms.pfc.validation.common.CommonValidation;

@Controller
public class AttachmentController {

	@Autowired
	private AttachmentService attachService;

	@Autowired
	private DocumentTypeService docTypeService;

	@Autowired
	private CommonValidation cv;

	@Autowired
	private Authority auth;
	
	@Autowired
	private TrxHisService ts;
	
	@Autowired
	private MessageSource msgSource;
	
	@Value("${server.servlet.context-path}")
	private String context;

	Map<String, Object> model = new HashMap<String, Object>();

	String rawMatlName = "";
	String rawMatlId = "";
	String rawMatlScreen = "";
	int recordType = 0;

	@GetMapping("/base/common/attachList")
	public ModelAndView getAllAttachmentList(HttpServletRequest request,
			@RequestParam(name = "rawMatlName", required = false) String matlName,
			@RequestParam(name = "prCode", required = false) String prCode,
			@RequestParam(name = "id", required = false) String matlId,
			@RequestParam(name = "prId", required = false) String prId,
			@RequestParam(name = "mode", required = false) String screenMode) {

		model = auth.onPageLoad(model, request);
		rawMatlId = matlId;
		rawMatlName = matlName;
		rawMatlScreen = screenMode;
		model.put("id", matlId);
		model.put("rawMatlName", matlName);

		try {
			if (screenMode.equals("view")) {
				model.put("buttonDisabled", true);
			} else {
				model.put("buttonDisabled", false);
			}
		} catch (Exception e) {
			model.put("buttonDisabled", false);
		}

		if (rawMatlName != null) {
			recordType = CommonConstants.RECORD_TYPE_ID_RAW_MATL;
		}

		Integer categoryInt = Integer.parseInt("0");
//		model.put("attachLists",
//				attachService.searchDocument("", categoryInt, "", "", "", "", "", "", "", "", "", rawMatlName));
		model.put("documentType", docTypeService.listDocumentType());

		return new ModelAndView("base/common/attachList", model);
	}

	@PostMapping(value = "/base/common/attachList", params = "action=search")
	public ModelAndView search(HttpServletRequest request, 
			@RequestParam(name = "attachTitle") String title,
			@RequestParam(name = "category") String category, @RequestParam(name = "fileName") String fileName,
			@RequestParam(name = "attachOwner") String owner,
			@RequestParam(name = "dateCreatedFr") String dateCreatedFr,
			@RequestParam(name = "dateCreatedTo") String dateCreatedTo,
			@RequestParam(name = "dateModifiedFr") String dateModifiedFr,
			@RequestParam(name = "dateModifiedTo") String dateModifiedTo, @RequestParam(name = "exp1") String exp1,
			@RequestParam(name = "exp2") String exp2, @RequestParam(name = "exp3") String exp3) {

		String errorMsg = "";

		errorMsg += cv.validateInputLength(title, 100, "Title");
		errorMsg += cv.validateInputLength(category, 20, "Category");
		errorMsg += cv.validateInputLength(fileName, 100, "File name");
		errorMsg += cv.validateInputLength(owner, 100, "Owner");
		errorMsg += cv.validateDateFormat(dateCreatedFr, "Date created from");
		errorMsg += cv.validateDateFormat(dateCreatedTo, "Date created to");
		errorMsg += cv.validateDateFormat(dateModifiedFr, "Date modified from");
		errorMsg += cv.validateDateFormat(dateModifiedTo, "Date modified to");

		if (errorMsg.length() != 0) {
			errorMsg += msgSource.getMessage("msgFailSearchRecord", new Object[] {}, Locale.getDefault());
			model.put("error", errorMsg);
			Integer categoryInt = Integer.parseInt("0");
//			model.put("attachLists",
//					attachService.searchDocument("", categoryInt, "", "", "", "", "", "", "", "", "", rawMatlName));
			model.put("documentType", docTypeService.listDocumentType());

			return new ModelAndView("/base/common/attachList", model);
		}

		Integer categoryInt = Integer.parseInt(category);
//		model.put("attachLists", attachService.searchDocument(title, categoryInt, fileName, owner, dateCreatedFr,
//				dateCreatedTo, dateModifiedFr, dateModifiedTo, exp1, exp2, exp3, rawMatlName));
		model.put("documentType", docTypeService.listDocumentType());
		model.put("attachTitle", title);
		model.put("category", category);
		model.put("fileName", fileName);
		model.put("attachOwner", owner);
		model.put("dateCreatedFr", dateCreatedFr);
		model.put("dateCreatedTo", dateCreatedTo);
		model.put("dateModifiedFr", dateModifiedFr);
		model.put("dateModifiedTo", dateModifiedTo);
		model.put("exp1", exp1);
		model.put("exp2", exp2);
		model.put("exp3", exp3);
		
		//FSGS) Kent 26/2/2021 Add/changed - Add Function executed log START
		StringBuffer sb = new StringBuffer();
		sb.append("Search Criteria: ");
		sb.append("Title=").append(title.isEmpty()?"<ALL>":title).append(", ");
		sb.append("Category=").append(category.isEmpty()?"<ALL>":category).append(", ");
		sb.append("File Name=").append(fileName.isEmpty()?"<ALL>":fileName).append(", ");
		sb.append("Owner=").append(owner.isEmpty()?"<ALL>":owner).append(", ");
		sb.append("Date Created From=").append(dateCreatedFr.isEmpty()?"<ALL>":dateCreatedFr).append(", ");
		sb.append("Date Created To=").append(dateCreatedTo.isEmpty()?"<ALL>":dateCreatedTo).append(", ");
		sb.append("Date Modified From=").append(dateModifiedFr.isEmpty()?"<ALL>":dateModifiedFr).append(", ");
		sb.append("Date Modified To=").append(dateModifiedTo.isEmpty()?"<ALL>":dateModifiedTo).append(", ");

		ts.addTrxHistory(new Date(), "Search Attachment", request.getRemoteUser(),
				CommonConstants.FUNCTION_TYPE_SEARCH, "Search Attachment", CommonConstants.RECORD_TYPE_ID_ATTACHMENT, sb.toString());
		//FSGS) Kent 26/2/2021 Add/changed - Add Function executed log END

		return new ModelAndView("base/common/attachList", model);
	}

	@PostMapping(value = "/base/common/attachList", params = "action=reset")
	public void reset(HttpServletResponse response) throws IOException {

		if (rawMatlId == null) {
			response.sendRedirect(context+"/base/common/attachList?rawMatlName=" + rawMatlName);
		} else {
			response.sendRedirect(context+"/base/common/attachList?rawMatlName=" + rawMatlName + "&id=" + rawMatlId
					+ "&mode=" + rawMatlScreen);
		}
	}

	@PostMapping("/base/common/attachListDel")
	public ModelAndView deleteAttach(HttpServletRequest request) {

		Integer categoryInt = Integer.parseInt("0");

		try {
			if (request.getParameterValues("tableRow") != null) {
				for (String attachFile : request.getParameterValues("tableRow")) {
//					attachService.deleteDocument(attachFile);
					//FSGS) Kent 26/2/2021 Add/changed - Add Function executed log START
					ts.addTrxHistory(new Date(), "Delete Attachment", request.getRemoteUser(),
							CommonConstants.FUNCTION_TYPE_DELETE, attachFile, CommonConstants.RECORD_TYPE_ID_ATTACHMENT, null);
					//FSGS) Kent 26/2/2021 Add/changed - Add Function executed log END
				}
			}
			model.put("success", "The record is deleted successfully.");
		} catch (Exception e) {
			model.put("error", "Failed to delete the record.");
		}

//		model.put("attachLists",
//				attachService.searchDocument("", categoryInt, "", "", "", "", "", "", "", "", "", rawMatlName));
		model.put("documentType", docTypeService.listDocumentType());

		return new ModelAndView("base/common/attachList", model);
	}

	@GetMapping("/base/common/addAttach")
	public ModelAndView addAttachPanel(HttpServletRequest request) {
		model = auth.onPageLoad(model, request);
		model.put("documentType", docTypeService.listDocumentType());
		return new ModelAndView("base/common/addAttach", model);
	}

	@PostMapping("/base/common/attachListBack")
	public void returnToMatl(HttpServletResponse response) throws IOException {

		if (rawMatlId == null) {
			response.sendRedirect(context+"/main/material/rawMatlForm");
		} else {
			response.sendRedirect(context+"/main/material/rawMatlFormView?matlId=" + rawMatlId);
		}
	}

	@PostMapping("/base/common/addAttachBack")
	public void returnToAttachList(HttpServletResponse response) throws IOException {

		if (rawMatlId == null) {
			response.sendRedirect(context+"/base/common/attachList?rawMatlName=" + rawMatlName);
		} else {
			response.sendRedirect(context+"/base/common/attachList?rawMatlName=" + rawMatlName + "&id=" + rawMatlId
					+ "&mode=" + rawMatlScreen);
		}
	}

	@PostMapping("/base/common/addAttach")
	public ModelAndView addAttach(@RequestParam("attachTitle") String attachTitle,
			@RequestParam("category") String category, @RequestParam("files") MultipartFile file,
			HttpServletRequest request) throws IOException {

		Integer categoryInt = Integer.parseInt("0");
		String errorMsg = "";

		errorMsg += cv.validateMandatoryInput(attachTitle, "Title");
		errorMsg += cv.validateMandatoryInput(category, "Category");
		if (file.isEmpty()) {
			errorMsg += "The file is not attached" + ".<br />";
		}
		errorMsg += cv.validateInputLength(attachTitle, 20, "Title");
		errorMsg += cv.validateInputLength(category, 100, "Title");

		if (errorMsg.length() != 0) {
			errorMsg += "Record failed to search!";
			model.put("error", errorMsg);
//			model.put("attachLists",
//					attachService.searchDocument("", categoryInt, "", "", "", "", "", "", "", "", "", rawMatlName));
			model.put("documentType", docTypeService.listDocumentType());

			return new ModelAndView("/base/common/attachList", model);
		}

		try {
//			attachService.searchDocument("", categoryInt, file.getOriginalFilename(), "", "", "", "", "", "", "3", "",
//					rawMatlName).get(0);
			model.put("error", "Failed to attach the document!");
		} catch (Exception e) {
//			attachService.addDocument(attachTitle, rawMatlName, recordType, category, file.getOriginalFilename(),
//					file.getBytes(), request.getRemoteUser());
			//FSGS) Kent 26/2/2021 Add/changed - Add Function executed log START
			ts.addTrxHistory(new Date(), "Insert Attachment", request.getRemoteUser(),
					CommonConstants.FUNCTION_TYPE_INSERT, attachTitle, CommonConstants.RECORD_TYPE_ID_ATTACHMENT, null);
			//FSGS) Kent 26/2/2021 Add/changed - Add Function executed log END
			model.put("success", "Attach the document successfully!");
		}

//		model.put("attachLists",
//				attachService.searchDocument("", categoryInt, "", "", "", "", "", "", "", "", "", rawMatlName));
		model.put("documentType", docTypeService.listDocumentType());
		return new ModelAndView("base/common/attachList", model);
	}

//	@GetMapping("/base/common/attachDownload")
//	public ResponseEntity<byte[]> downloadFile(@RequestParam(name = "fileInfo") String fileName) {
//
//		Integer categoryInt = Integer.parseInt("0");
//		Document file = attachService
//				.searchDocument("", categoryInt, fileName, "", "", "", "", "", "", "3", "", rawMatlName).get(0);
//		return ResponseEntity.ok()
//				.header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + file.getFileName() + "\"")
//				.body(file.getContentObj());
//	}

}
