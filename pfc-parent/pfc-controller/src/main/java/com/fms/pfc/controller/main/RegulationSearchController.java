package com.fms.pfc.controller.main;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.context.annotation.SessionScope;
import org.springframework.web.servlet.ModelAndView;

import com.fms.pfc.common.Authority;
import com.fms.pfc.common.CommonConstants;
import com.fms.pfc.common.MediaTypeUtils;
import com.fms.pfc.domain.dto.main.ReglFileDto;
import com.fms.pfc.domain.model.main.Regulation;
import com.fms.pfc.domain.model.main.RegulationDoc;
import com.fms.pfc.service.api.base.CountryService;
import com.fms.pfc.service.api.base.TrxHisService;
import com.fms.pfc.service.api.main.RawMaterialService;
import com.fms.pfc.service.api.main.ReglFileService;
import com.fms.pfc.service.api.main.RegulationCategoryService;
import com.fms.pfc.service.api.main.RegulationDocService;
import com.fms.pfc.service.api.main.RegulationGroupService;
import com.fms.pfc.service.api.main.RegulationRMService;
import com.fms.pfc.service.api.main.RegulationSearchService;
import com.fms.pfc.validation.common.CommonValidation;

@Controller
@SessionScope
public class RegulationSearchController {

	private static final Logger logger = LoggerFactory.getLogger(RegulationSearchController.class);

	private RegulationCategoryService regCatServ;
	private RegulationGroupService regGrpServ;
	private CountryService refCountryServ;
	private Authority auth;
	private CommonValidation commonValServ;
	private TrxHisService trxHistServ;
	private ServletContext servletContext;
	private ReglFileService reglFileServ;
	private RawMaterialService rmServ;

	private Map<String, Object> model = new HashMap<String, Object>();

	private final String FILE_NAME_TXT = "File Name";

	@Autowired
	public RegulationSearchController(RegulationCategoryService regCatServ, RegulationGroupService regGrpServ,
			CountryService refCountryServ, Authority auth, CommonValidation commonValServ, TrxHisService trxHistServ,
			ServletContext servletContext, ReglFileService reglFileServ, RawMaterialService rmServ) {
		super();
		this.regCatServ = regCatServ;
		this.regGrpServ = regGrpServ;
		this.refCountryServ = refCountryServ;
		this.auth = auth;
		this.commonValServ = commonValServ;
		this.trxHistServ = trxHistServ;
		this.servletContext = servletContext;
		this.reglFileServ = reglFileServ;
		this.rmServ = rmServ;
	}

	// Display initial screen
	@GetMapping("/main/regulation/fileSearchList")
	public ModelAndView getFileSearchList(@Valid Regulation regSearch, HttpServletRequest request,
			HttpServletResponse response) {

		removeAlert(model);
		model = auth.onPageLoad(model, request);
		auth.isAuthUrl(request, response);
		model.put("fileSearchItems", reglFileServ.searchReglFileByCritriaQA(0, "", "", "", 0, 0));
		model.put("countryItems",
				refCountryServ.searchCountry("", "", "", "", String.valueOf(CommonConstants.FLAG_YES), ""));
		model.put("rmItems", rmServ.getRmNameLabelAndValue());
		model.put("fileGroupItems", regGrpServ.searchGroupRegulation("", "", "", ""));
		model.put("fileCategoryItems", regCatServ.searchCategoryRegulation("", "", "", "", ""));

		return new ModelAndView("/main/regulation/fileSearchList", model);

	}

	// Display search results
	@PostMapping("/main/regulation/fileSearchFormSearch")
	public ModelAndView getSearchResult(HttpServletRequest request, @RequestParam(name = "countryId") String countryId,
			@RequestParam(name = "grpName") Integer fileGroup, @RequestParam(name = "catName") Integer fileCategory,
			@RequestParam(name = "fileName") String fileName, @RequestParam(name = "exp") String exp,
			@RequestParam(name = "rmName") Integer rmName, HttpSession session) {

		removeAlert(model);
		String errorMsg = "";

		model.put("countryId", countryId);
		model.put("grpName", fileGroup);
		model.put("rmName", rmName);
		model.put("catName", fileCategory);
		model.put("fileName", fileName);
		model.put("exp", exp);

		try {
			errorMsg += commonValServ.validateInputLength(fileName, 100, FILE_NAME_TXT);

			// When contain error
			if (errorMsg.length() > 0) {
				model.put("fileSearchItems", reglFileServ.searchReglFileByCritriaQA(0, "", "", "", 0, 0));
				model.put("error", errorMsg);
			} else {
				// FSGS) Hani 10/3/2021 Add/Change .trim() to search criteria START
				model.put("fileSearchItems", reglFileServ.searchReglFileByCritriaQA(Objects.isNull(rmName)?0:rmName, countryId, fileName.trim(),
						exp, Objects.isNull(fileGroup)?0:fileGroup, Objects.isNull(fileCategory)?0:fileCategory));
				// FSGS) Hani 10/3/2021 Add/Change .trim() to search criteria END

				// FSGS) Kent 26/2/2021 Add/changed - Add Function executed log START
				StringBuffer sb = new StringBuffer();
				sb.append("Search Criteria: ");
				sb.append("Country Id=").append(countryId.isEmpty() ? "<ALL>" : countryId).append(", ");
				sb.append("File Group=").append(Objects.isNull(fileGroup) || fileGroup == 0 ? "<ALL>" : fileGroup).append(", ");
				sb.append("Raw Material Group=").append(Objects.isNull(rmName) || rmName == 0 ? "<ALL>" : rmName).append(", ");
				sb.append("File Category=").append(Objects.isNull(fileCategory) || fileCategory == 0 ? "<ALL>" : fileCategory).append(", ");
				sb.append("File Name=").append(fileName.isEmpty() ? "<ALL>" : fileName).append(", ");

				trxHistServ.addTrxHistory(new Date(), "Search Regulation File Directory", request.getRemoteUser(),
						CommonConstants.FUNCTION_TYPE_SEARCH, "Search Regulation File Directory",
						CommonConstants.RECORD_TYPE_ID_REGL, sb.toString());
				// FSGS) Kent 26/2/2021 Add/changed - Add Function executed log END
			}

		} catch (Exception e) {
			errorMsg += "Failed to get record.";
			errorMsg += e.toString();
			model.put("error", errorMsg);
		}

		return new ModelAndView("main/regulation/fileSearchList", model);
	}

	private Map<String, Object> removeAlert(Map<String, Object> model) {
		model.remove("error");
		model.remove("success");

		return model;
	}

	@GetMapping("/main/regulation/fileDownload")
	public ResponseEntity<byte[]> downloadFileListing(HttpServletRequest request,
			@RequestParam(name = "reglFileId") int reglFileId) {
		String fileName = "";
		byte[] data = new byte[1024];

		ReglFileDto reglFileDto = reglFileServ.findDtoById(reglFileId);

		fileName = reglFileDto.getFileName();
		data = reglFileDto.getContentObject();

		MediaType mt = MediaTypeUtils.getMediaTypeForFileName(servletContext, fileName);

		return ResponseEntity.ok().contentType(mt)
				.header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + fileName + "\"").body(data);
	}
}
