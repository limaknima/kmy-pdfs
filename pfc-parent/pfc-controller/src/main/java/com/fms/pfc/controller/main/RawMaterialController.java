package com.fms.pfc.controller.main;

import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.context.annotation.SessionScope;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import com.fms.pfc.common.Authority;
import com.fms.pfc.common.CommonConstants;
import com.fms.pfc.common.FinalStatusEnum;
import com.fms.pfc.common.MediaTypeUtils;
import com.fms.pfc.common.RecordStatusEnum;
import com.fms.pfc.common.TaskCreation;
import com.fms.pfc.domain.dto.LabelAndValueDto;
import com.fms.pfc.domain.dto.main.FsRmUpdDto;
import com.fms.pfc.domain.dto.main.ReglFileDto;
import com.fms.pfc.domain.model.Document;
import com.fms.pfc.domain.model.UsrProfile;
import com.fms.pfc.domain.model.UsrRole;
import com.fms.pfc.domain.model.main.PrRmStat;
import com.fms.pfc.domain.model.main.ProductRecipe;
import com.fms.pfc.domain.model.main.RawMaterial;
import com.fms.pfc.domain.model.main.Regl;
import com.fms.pfc.domain.model.main.ReglDoc;
import com.fms.pfc.domain.model.main.ReglFileSearch;
import com.fms.pfc.domain.model.main.RmIngredient;
import com.fms.pfc.domain.model.main.RmManf;
import com.fms.pfc.domain.model.main.RmManfSuppl;
import com.fms.pfc.domain.model.main.RmStat;
import com.fms.pfc.domain.model.main.RmStatGrp;
import com.fms.pfc.exception.CommonException;
import com.fms.pfc.service.api.base.AttachmentService;
import com.fms.pfc.service.api.base.CountryService;
import com.fms.pfc.service.api.base.DocumentTypeService;
import com.fms.pfc.service.api.base.ManufacturerService;
import com.fms.pfc.service.api.base.TrxHisService;
import com.fms.pfc.service.api.base.UserProfileService;
import com.fms.pfc.service.api.base.UsrRoleService;
import com.fms.pfc.service.api.main.FlavorStatusService;
import com.fms.pfc.service.api.main.FsRmUpdService;
import com.fms.pfc.service.api.main.PrRmStatService;
import com.fms.pfc.service.api.main.ProductRecipeService;
import com.fms.pfc.service.api.main.RawMaterialService;
import com.fms.pfc.service.api.main.RegService;
import com.fms.pfc.service.api.main.ReglFileService;
import com.fms.pfc.service.api.main.RegulationDocService;
import com.fms.pfc.service.api.main.RmIngService;
import com.fms.pfc.service.api.main.RmManfService;
import com.fms.pfc.service.api.main.RmManfSupplService;
import com.fms.pfc.service.api.main.RmStatService;
import com.fms.pfc.validation.common.CommonValidation;

@SuppressWarnings("unchecked")
@Controller
@SessionScope
public class RawMaterialController {

	private final Logger logger = LoggerFactory.getLogger(RawMaterialController.class);

	private Authority auth;
	private TaskCreation taskCreationServ;
	private CommonValidation commonValidatorServ;
	private CountryService refCountryServ;
	private ManufacturerService refManfServ;
	private RawMaterialService rmServ;
	private RmManfService rmManfServ;
	private RmManfSupplService rmManfSupplServ;
	private RmStatService rmStatServ;
	private RmIngService rmIngServ;
	private ProductRecipeService prdRcpServ;
	private PrRmStatService prRmStatServ;
	private UsrRoleService usrRoleServ;
	private RegService regServ;
	private RegulationDocService regDocServ;
	private TrxHisService trxHistServ;
	private AttachmentService docServ;
	private DocumentTypeService docTypeServ;
	private UserProfileService userServ;
	private MessageSource msgSource;
	private ServletContext servletContext;
	private FlavorStatusService fsServ;
	private FsRmUpdService fsUpdServ;
	private ReglFileService rfServ;
	
	private Map<String, Object> sessionModel = new HashMap<String, Object>();

	@Autowired
	public RawMaterialController(Authority auth, TaskCreation taskCreationServ, CommonValidation commonValidatorServ,
			CountryService refCountryServ, ManufacturerService refManfServ, RawMaterialService rmServ,
			RmManfService rmManfServ, RmManfSupplService rmManfSupplServ, RmStatService rmStatServ,
			RmIngService rmIngServ, ProductRecipeService prdRcpServ, PrRmStatService prRmStatServ,
			UsrRoleService usrRoleServ, RegService regServ, RegulationDocService regDocServ, TrxHisService trxHistServ,
			AttachmentService docServ, DocumentTypeService docTypeServ, UserProfileService userServ,
			MessageSource msgSource, ServletContext servletContext, FlavorStatusService fsServ, FsRmUpdService fsUpdServ, ReglFileService rfServ) {
		super();
		this.auth = auth;
		this.taskCreationServ = taskCreationServ;
		this.commonValidatorServ = commonValidatorServ;
		this.refCountryServ = refCountryServ;
		this.refManfServ = refManfServ;
		this.rmServ = rmServ;
		this.rmManfServ = rmManfServ;
		this.rmManfSupplServ = rmManfSupplServ;
		this.rmStatServ = rmStatServ;
		this.rmIngServ = rmIngServ;
		this.prdRcpServ = prdRcpServ;
		this.prRmStatServ = prRmStatServ;
		this.usrRoleServ = usrRoleServ;
		this.regServ = regServ;
		this.regDocServ = regDocServ;
		this.trxHistServ = trxHistServ;
		this.docServ = docServ;
		this.docTypeServ = docTypeServ;
		this.userServ = userServ;
		this.msgSource = msgSource;
		this.servletContext = servletContext;
		this.fsServ = fsServ;
		this.fsUpdServ = fsUpdServ;
		this.rfServ = rfServ;
	}

	@GetMapping("/main/material/rawMatlList")
	public ModelAndView loadRawMatlList(HttpServletRequest request, HttpServletResponse response, HttpSession session) {

		sessionModel = auth.onPageLoad(sessionModel, request);
		auth.isAuthUrl(request, response);	
		UsrProfile user = userServ.searchUserProfile("", request.getRemoteUser().trim(), "", "", "", "",
				String.valueOf(CommonConstants.SEARCH_OPTION_CONTAIN), "", "").get(0);	
		
		if (user.getGroupId().equals(CommonConstants.GROUP_ID_FR)) {
			sessionModel.put("btnDel", true);
			sessionModel.put("btnAdd", true);
		}
		
		retrieveMatlData("", "", "", "", "", "", "", "", sessionModel, request.getRemoteUser(), 0);

		return new ModelAndView("/main/material/rawMatlList", sessionModel);
	}

	@PostMapping("/main/material/rawMatlList")
	public ModelAndView searchMatl(@RequestParam(name = "rawMat") String rawMat,
			@RequestParam(name = "tsNo") String tsNo, @RequestParam(name = "manufacturer") String manufacturer,
			@RequestParam(name = "supplier") String supplier, @RequestParam(name = "exp1") String exp1,
			@RequestParam(name = "exp2") String exp2, @RequestParam(name = "exp3") String exp3,
			@RequestParam(name = "exp4") String exp4, @RequestParam(name = "status") int status, 
			HttpServletRequest request, HttpSession session) {

		StringBuffer sb = new StringBuffer();
		sb.append("Search Criteria: ");
		sb.append("Raw Material Name=").append(rawMat.isEmpty() ? "<ALL>" : rawMat).append(", ");
		sb.append("TS No=").append(tsNo.isEmpty() ? "<ALL>" : tsNo).append(", ");
		sb.append("Manufacturer=").append(manufacturer.isEmpty() ? "<ALL>" : manufacturer).append(", ");
		sb.append("Supplier=").append(supplier.isEmpty() ? "<ALL>" : supplier);

		trxHistServ.addTrxHistory(new Date(), "Search Raw Material", request.getRemoteUser(),
				CommonConstants.FUNCTION_TYPE_SEARCH, "Search Raw Material", CommonConstants.RECORD_TYPE_ID_RAW_MATL,
				sb.toString());

		// FSGS) Hani 10/3/2021 Add/Change .trim() to search criteria START
		retrieveMatlData(rawMat.trim(), tsNo.trim(), manufacturer.trim(), supplier.trim(), exp1, exp2, exp3, exp4,
				sessionModel, request.getRemoteUser(), status);
		// FSGS) Hani 10/3/2021 Add/Change .trim() to search criteria END

		sessionModel.put("rawMat", rawMat);
		sessionModel.put("tsNo", tsNo);
		sessionModel.put("manufacturer", manufacturer);
		sessionModel.put("supplier", supplier);
		sessionModel.put("exp1", exp1);
		sessionModel.put("exp2", exp2);
		sessionModel.put("exp3", exp3);
		sessionModel.put("exp4", exp4);
		sessionModel.put("status", status);
		sessionModel.remove("success");
		sessionModel.remove("error");

		return new ModelAndView("/main/material/rawMatlList", sessionModel);
	}

	@RequestMapping("/main/material/rawMatlListDel")
	public ModelAndView deleteRawMatl(HttpServletRequest request, @RequestParam(value = "tableRow") int[] tableRow,
			HttpSession session) {

		sessionModel.remove("error");
		sessionModel.remove("success");
		if (tableRow.equals(null)) {
			retrieveMatlData("", "", "", "", "", "", "", "", sessionModel, request.getRemoteUser(), 0);
			return new ModelAndView("/main/material/rawMatlList", sessionModel);
		}

		try {
			for (int i = 0; i < tableRow.length; i++) {
				rmManfSupplServ.delRmManfSuppl(tableRow[i]);
				rmManfServ.delRmManf(tableRow[i]);
				rmServ.delRawMatl(tableRow[i]);
				rmStatServ.delRmStat(tableRow[i]);
				rmIngServ.delRmIngredient(tableRow[i]);
				docServ.deleteDocumentByRef(String.valueOf(tableRow[i]), CommonConstants.RECORD_TYPE_ID_RAW_MATL);
				
				trxHistServ.addTrxHistory(new Date(), "Delete Raw Material", request.getRemoteUser(),
						CommonConstants.FUNCTION_TYPE_DELETE, String.valueOf(tableRow[i]), CommonConstants.RECORD_TYPE_ID_RAW_MATL,
						null);
			}

			retrieveMatlData("", "", "", "", "", "", "", "", sessionModel, request.getRemoteUser(), 0);
			sessionModel.put("success", "Record deleted successfully.");
		} catch (Exception e) {
			sessionModel.put("error", "Failed to delete record.");
		}

		return new ModelAndView("/main/material/rawMatlList", sessionModel);
	}

	private void retrieveMatlData(String rawMat, String tsNo, String manufacturer, String supplier, String exp1,
			String exp2, String exp3, String exp4, Map<String, Object> sessionModel, String currUser, int status) {
		DateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
		List<RawMaterial> rm = rmServ.searchRawMaterial(rawMat, exp1, 0, status);
		List<RawMaterial> rmDelete = new ArrayList<RawMaterial>();

		for (RawMaterial rmList : rm) {
			String joinTsNo = "";
			String joinManufac = "";
			String joinSupplier = "";
			String joinVipdDate = "";
			String joinDeclareDate = "";
			boolean hasValue = false;
			List<RmManf> rmManf = rmManfServ.searchRmManufacturer(rmList.getRawMatlId(), tsNo, manufacturer, exp2,
					exp3);

			for (RmManf rmManfList : rmManf) {
				if (joinManufac.equals("")) {
					joinManufac = rmManfList.getVendorName();
				} else {
					joinManufac = joinManufac + "<br />" + rmManfList.getVendorName();
				}

				if (joinTsNo.equals("")) {
					joinTsNo = (rmManfList.getTsNo() == null ? "" : rmManfList.getTsNo());
				} else {
					joinTsNo = joinTsNo + "<br />" + (rmManfList.getTsNo() == null ? "" : rmManfList.getTsNo());
				}

				if (joinVipdDate.equals("")) {
					joinVipdDate = (rmManfList.getVipdDate() == null ? "" : formatter.format(rmManfList.getVipdDate()));
				} else {
					joinVipdDate = joinVipdDate + "<br />"
							+ (rmManfList.getVipdDate() == null ? "" : formatter.format(rmManfList.getVipdDate()));
				}

				if (joinDeclareDate.equals("")) {
					joinDeclareDate = (rmManfList.getVipdAnnex2Date() == null ? ""
							: formatter.format(rmManfList.getVipdAnnex2Date()));
				} else {
					joinDeclareDate = joinDeclareDate + "<br />" + (rmManfList.getVipdAnnex2Date() == null ? ""
							: formatter.format(rmManfList.getVipdAnnex2Date()));
				}

				List<RmManfSuppl> rmManfSuppl = rmManfSupplServ.searchRmSupplier(rmManfList.getRmManfId(), supplier,
						exp4);

				for (RmManfSuppl rmManfSupplList : rmManfSuppl) {
					hasValue = true;

					if (joinSupplier.equals("")) {
						joinSupplier = rmManfSupplList.getVendorName();
					} else {
						joinSupplier = joinSupplier + "<br />" + rmManfSupplList.getVendorName();
					}
				}
			}

			rmList.setTsNoManufacturer(joinTsNo);
			rmList.setManufacturer(joinManufac);
			rmList.setSupplier(joinSupplier);
			rmList.setVipdDate(joinVipdDate);
			rmList.setVipdAnnex2Date(joinDeclareDate);
			rmList.setFlavStatusName(Objects.nonNull(rmList.getFlavStatusId())?(fsServ.findOneById(rmList.getFlavStatusId()).getFsName()):"");

			if (rmList.getCreatorId().equals(currUser)) {
				rmList.setPermissionToDelete(true);
			} else {
				List<UsrRole> usrRoleList = usrRoleServ.searchUserRole(currUser);
				boolean authLvl = false;
				for (UsrRole usrRole : usrRoleList) {
					if (usrRole.getRoleId().equals(CommonConstants.ROLE_ID_CHECKER)
							|| usrRole.getRoleId().equals(CommonConstants.ROLE_ID_HOD)) {
						authLvl = true;
					}
				}

				if (authLvl) {
					rmList.setPermissionToDelete(true);
				} else {
					rmList.setPermissionToDelete(false);
				}
			}

			if (!hasValue) {
				rmDelete.add(rmList);
			}
		}

		rm.removeAll(rmDelete);

		sessionModel.put("materialLists", rm);
	}

	/*
	 * Load material form function
	 */
	@GetMapping("/main/material/rawMatlForm")
	public ModelAndView loadRawMatlForm(HttpServletRequest request, HttpServletResponse response) {

		sessionModel = auth.onPageLoad(sessionModel, request);
		sessionModel.put("matlStatus", RecordStatusEnum.DRAFT.intValue());
		sessionModel.put("header", "Add Raw Material");
		sessionModel.put("screenMode", CommonConstants.SCREEN_MODE_ADD);
		sessionModel.put("btnSave", "Save as draft");
		sessionModel.put("btnEdit", true);
		sessionModel.put("btnReject", true);
		sessionModel.put("btnRework", true);
		sessionModel.put("asterisk", false);
		sessionModel.put("btnActivate", true);
		sessionModel.put("btnDeactivate", true);
		sessionModel.put("divRegRefHid", true);
		setManfButton(false, true, true, sessionModel);
		setIngButton(false, true, true, sessionModel);
		setRegButton(false, true, true, sessionModel);
		setAttachButton(false, true, true, sessionModel);
		onPageLoadCommon(sessionModel);

		List<RmManf> rmManf = new ArrayList<RmManf>();
		sessionModel.put("rmManfItems", rmManf);

		List<RmIngredient> rmIng = new ArrayList<RmIngredient>();
		sessionModel.put("ingItems", rmIng);

		List<RmStat> rmStat = new ArrayList<RmStat>();
		sessionModel.put("regItems", rmStat);

		RawMaterial newRawMatl = new RawMaterial();
		//newRawMatl.setRemarks("Initial creation");
		sessionModel.put("rawMatlItems", newRawMatl);

		List<RmStat> rmStatSuppl = new ArrayList<RmStat>();
		sessionModel.put("rmStatSupplItems", rmStatSuppl);

		List<RmManf> ingDropDownList = new ArrayList<RmManf>();
		sessionModel.put("ingDropDownItems", ingDropDownList);

		List<Document> attachTable = new ArrayList<Document>();
		sessionModel.put("attachItems", attachTable);

		List<RmManf> rmManfDel = new ArrayList<RmManf>();
		List<RmIngredient> rmIngDel = new ArrayList<RmIngredient>();
		List<RmStat> rmStatDel = new ArrayList<RmStat>();
		List<Document> docDel = new ArrayList<Document>();

		sessionModel.put("rmManfDel", rmManfDel);
		sessionModel.put("rmIngDel", rmIngDel);
		sessionModel.put("rmStatDel", rmStatDel);
		sessionModel.put("docDel", docDel);

		return new ModelAndView("/main/material/rawMatlForm", sessionModel);
	}

	/*
	 * Retrieve and view raw material
	 */
	@RequestMapping("/main/material/rawMatlFormView")
	public ModelAndView viewRawMatl(@RequestParam(name = "matlId") int rawMatlId, HttpServletRequest request,
			HttpServletResponse response, HttpSession session) {

		String screenMode = CommonConstants.SCREEN_MODE_VIEW;

		sessionModel = auth.onPageLoad(sessionModel, request);
		sessionModel.put("regChange", false);
		onPageLoadCommon(sessionModel);
		sessionModel.put("screenMode", screenMode);
		sessionModel.put("asterisk", true);
		setManfButton(true, true, true, sessionModel);
		setIngButton(true, true, true, sessionModel);
		setRegButton(true, true, true, sessionModel);
		setAttachButton(true, true, true, sessionModel);

		DateFormat formatter = new SimpleDateFormat("dd/MM/yyy hh:mm:ss a");
		RawMaterial rm = rmServ.searchRawMaterial("", "", rawMatlId, 0).get(0);
		sessionModel.put("rawMatlItems", rm);
		sessionModel.put("currentRemarks", rm.getRemarks());
		onViewModeScreenControl(request, rm, sessionModel);
		
		sessionModel.put("currentOptlock", rm.getOptLock());
		
		logger.info("viewRawMatl() rmId={}, rmStatus={}, rmLock={}, user={}, session={}", rawMatlId,
				rm.getRecordStatus(), rm.getOptLock(), request.getRemoteUser(), session.getId());

		UsrProfile user = userServ.searchUserProfile("", request.getRemoteUser().trim(), "", "", "", "",
				String.valueOf(CommonConstants.SEARCH_OPTION_CONTAIN), "", "").get(0);
		
		if (!user.getGroupId().equals(CommonConstants.GROUP_ID_FR)) {
			sessionModel.put("btnAction", true);
		}
		// FSGS) Kent 26/2/2021 Add/changed - Add Function executed log START
		trxHistServ.addTrxHistory(new Date(), "View Raw Material", request.getRemoteUser(), CommonConstants.FUNCTION_TYPE_VIEW,
				String.valueOf(rawMatlId), CommonConstants.RECORD_TYPE_ID_RAW_MATL, null);
		// FSGS) Kent 26/2/2021 Add/changed - Add Function executed log END

		List<RmManf> rmManf = rmManfServ.searchRmManufacturer(rawMatlId, "", "", "", "");
		List<RmIngredient> rmIng = rmIngServ.searchRmIng(rawMatlId);
		List<RmStatGrp> rmStatGrp = rmStatServ.searchRmStatGrp(rawMatlId);
		List<RmStat> rmStatSuppl = rmStatServ.searchRmStat(rawMatlId);
		List<Document> docList = docServ.searchDocument(String.valueOf(rawMatlId), CommonConstants.RECORD_TYPE_ID_RAW_MATL);

		sessionModel.put("header", "View Raw Material");
		sessionModel.put("matlStatus", rm.getRecordStatus());
		sessionModel.put("rawMatlName", rm.getRawMatlName());
		sessionModel.put("rawMatlId", rawMatlId);
		sessionModel.put("divRegRefHid", true);

		try {
			String createdInfo = "Created by " + rm.getCreatorId() + " on " + formatter.format(rm.getCreatedDate());
			sessionModel.put("createdInfo", createdInfo);
		} catch (Exception e) {
		}

		String modifiedInfo = "";
		try {
			modifiedInfo = "Modified by " + rm.getModifierId() + " on " + formatter.format(rm.getModifiedDate());
		} catch (Exception e) {
			modifiedInfo = "Modified by --";
		}
		sessionModel.put("modifiedInfo", modifiedInfo);
		
		//set flavor status
		if(Objects.nonNull(rm.getFlavStatusId()))
			rm.setFlavStatusName(fsServ.findOneById(rm.getFlavStatusId()).getFsName());

		List<RmManf> rmManfTable = new ArrayList<RmManf>();
		List<RmManf> rmIngDropDown = new ArrayList<RmManf>();
		int manfRow = 0;
		for (RmManf rmManfData : rmManf) {
			RmManf rmManfSrcData = new RmManf();
			rmManfSrcData.setRmManfId(rmManfData.getRmManfId());
			rmManfSrcData.setManfRowNo(manfRow);
			rmManfSrcData.setTsNo(rmManfData.getTsNo());
			rmManfSrcData.setManfId(rmManfData.getManfId());
			rmManfSrcData.setVendorName(
					refManfServ.searchManufacturerList("", "", "", "", "", rmManfData.getManfId()).get(0).getVendorName());
			rmManfSrcData.setOriginCountryId(rmManfData.getOriginCountryId());
			rmManfSrcData.setCountryName(refCountryServ.findOneById(rmManfData.getOriginCountryId()).getCountryName());
			rmManfSrcData.setManfRecStatus("exist");

			String supplierName = "";
			List<Integer> supplId = new ArrayList<Integer>();
			List<RmManfSuppl> rmManfSuppl = rmManfSupplServ.searchRmSupplier(rmManfData.getRmManfId(), "", "");
			for (RmManfSuppl rmManfSupplData : rmManfSuppl) {
				if (supplierName.equals("")) {
					supplierName = refManfServ.searchManufacturerList("", "", "", "", "", rmManfSupplData.getSupplId()).get(0)
							.getVendorName();
				} else {
					supplierName = supplierName + "<br />"
							+ refManfServ.searchManufacturerList("", "", "", "", "", rmManfSupplData.getSupplId()).get(0)
									.getVendorName();
				}

				supplId.add(rmManfSupplData.getSupplId());
			}

			rmManfSrcData.setSupplId(supplId);
			rmManfSrcData.setSupplierName(supplierName);
			rmManfSrcData.setVipdDate(rmManfData.getVipdDate());
			if (rmManfData.getVipdObject() != null) {
				rmManfSrcData.setVipdObject(rmManfData.getVipdObject());
			}
			if (rmManfData.getVipdAnnex2Object() != null) {
				rmManfSrcData.setVipdAnnex2Object(rmManfData.getVipdAnnex2Object());
			}
			rmManfSrcData.setVipdAnnex2Date(rmManfData.getVipdAnnex2Date());
			rmManfSrcData.setVipdFileName(rmManfData.getVipdFileName());
			rmManfSrcData.setVipdAnnex2FileName(rmManfData.getVipdAnnex2FileName());
			manfRow++;

			rmManfTable.add(rmManfSrcData);
			rmIngDropDown.add(rmManfSrcData);
		}

		sessionModel.put("rmManfItems", rmManfTable);
		sessionModel.put("ingDropDownItems", rmIngDropDown);

		List<RmIngredient> rmIngTable = new ArrayList<RmIngredient>();
		int ingRow = 0;
		for (RmIngredient rmIngData : rmIng) {
			RmIngredient rmIngSrcData = new RmIngredient();
			rmIngSrcData.setRmIngId(rmIngData.getRmIngId());
			rmIngSrcData.setIngRowNo(ingRow);
			rmIngSrcData.setManfId(rmIngData.getManfId());
			rmIngSrcData.setVendorName(
					refManfServ.searchManufacturerList("", "", "", "", "", rmIngData.getManfId()).get(0).getVendorName());
			rmIngSrcData.setIngName(rmIngData.getIngName());
			rmIngSrcData.setCompPerc(rmIngData.getCompPerc());
			rmIngSrcData.setInsNo(rmIngData.getInsNo());
			rmIngSrcData.setENo(rmIngData.getENo());
			rmIngSrcData.setFemaNo(rmIngData.getFemaNo());
			rmIngSrcData.setJecfaNo(rmIngData.getJecfaNo());
			rmIngSrcData.setCasNo(rmIngData.getCasNo());
			rmIngSrcData.setIngRecStatus("exist");
			ingRow++;

			rmIngTable.add(rmIngSrcData);
		}

		rmIngTable.sort(Comparator.comparing(obj -> obj.getVendorName()));
		sessionModel.put("ingItems", rmIngTable);

		List<RmStat> rmStatTable = new ArrayList<RmStat>();
		List<RmStat> rmStatSupplTable = new ArrayList<RmStat>();
		int statRow = 0;
		for (RmStatGrp rmStatData : rmStatGrp) {
			RmStat rmStatSrcData = new RmStat();
			rmStatSrcData.setRmStatId(rmStatData.getRmStatId());
			rmStatSrcData.setRmStatRowNo(statRow);
			rmStatSrcData.setCountryId(rmStatData.getCountryId());
			rmStatSrcData.setCountryName(refCountryServ.findOneById(rmStatSrcData.getCountryId()).getCountryName());
			rmStatSrcData.setFinalStatus(rmStatData.getFinalStatus());
			rmStatSrcData.setFinalStatusDesc(getFinalStatusDesc(rmStatData.getFinalStatus()));
			rmStatSrcData.setRmStatRecStatus("exist");
			rmStatSrcData.setDataChange(rmStatData.getDataChange());
			
			logger.debug("view.. dataChgflag={}, bool={}",rmStatData.getDataChange(),rmStatSrcData.isDataChgBool());

			statRow++;

			rmStatTable.add(rmStatSrcData);
		}

		rmStatTable.sort(Comparator.comparing(a -> a.getCountryName()));
		sessionModel.put("regItems", rmStatTable);

		for (RmStat rmStatSupplData : rmStatSuppl) {
			RmStat rmStatSupplSrcData = new RmStat();
			rmStatSupplSrcData.setRmStatId(rmStatSupplData.getRmStatId());
			rmStatSupplSrcData.setManfId(rmStatSupplData.getManfId());
			rmStatSupplSrcData.setVipdStatus(rmStatSupplData.getVipdStatus());
			rmStatSupplSrcData.setCountryId(rmStatSupplData.getCountryId());
			rmStatSupplSrcData.setDataChange(rmStatSupplData.getDataChange());

			rmStatSupplTable.add(rmStatSupplSrcData);
		}

		sessionModel.put("rmStatSupplItems", rmStatSupplTable);

		List<Document> attachTable = new ArrayList<Document>();
		int docRowNo = 0;
		for (Document doc : docList) {
			Document docData = new Document();
			docData.setDocRowNo(docRowNo);
			docData.setId(doc.getId());
			docData.setSubject(doc.getSubject());
			docData.setFileName(doc.getFileName());
			docData.setContentObj(doc.getContentObj());
			docData.setCreatedBy(doc.getCreatedBy());
			docData.setDocumentType(doc.getDocumentType());
			docData.setDocTypeName(docTypeServ.searchDocType(doc.getDocumentType()).getName());
			docData.setDocRecStatus("exist");

			docRowNo++;
			attachTable.add(docData);
		}

		sessionModel.put("attachItems", attachTable);

		List<RmManf> rmManfDel = new ArrayList<RmManf>();
		List<RmIngredient> rmIngDel = new ArrayList<RmIngredient>();
		List<RmStat> rmStatDel = new ArrayList<RmStat>();
		List<Document> docDel = new ArrayList<Document>();

		sessionModel.put("rmManfDel", rmManfDel);
		sessionModel.put("rmIngDel", rmIngDel);
		sessionModel.put("rmStatDel", rmStatDel);
		sessionModel.put("docDel", docDel);

		return new ModelAndView("/main/material/rawMatlForm", sessionModel);
	}

	private Map<String, Object> onPageLoadCommon(Map<String, Object> sessionModel) {

		sessionModel.put("countryItems", refCountryServ.findByCriteria("", "", "", "", String.valueOf(CommonConstants.FLAG_YES), ""));
		sessionModel.put("originCountryItems", refCountryServ.findByCriteria("", "", "", "", "", String.valueOf(CommonConstants.FLAG_YES)));
		sessionModel.put("manufacturerItems", refManfServ.searchVendor(CommonConstants.VENDOR_TYPE_MFR));
		sessionModel.put("supplierItems", refManfServ.searchVendor(CommonConstants.VENDOR_TYPE_SUPPL));
		sessionModel.put("documentType", docTypeServ.listDocumentType());
		sessionModel.put("currTab", "manufacturer");
		sessionModel.put("regDocBlock", true);
		sessionModel.put("regDocBlockNotFound", true);
		sessionModel.put("flavStatusItems", fsServ.findAll());

		return sessionModel;
	}

	@PostMapping(value = "/main/material/rawMatlForm", params = "action=edit")
	public ModelAndView editMatlForm(HttpServletRequest request, HttpSession session) {

		String screenMode = CommonConstants.SCREEN_MODE_EDIT;
		
		logger.debug("editMatlForm() screenMode={}",screenMode);

		sessionModel.remove("error");
		sessionModel.put("screenMode", screenMode);
		sessionModel.put("asterisk", false);
		sessionModel.put("currTab", "manufacturer");
		sessionModel.put("header", "Edit Raw Material");
		sessionModel.put("btnSave", "Save");
		sessionModel.remove("vipdFileView");
		sessionModel.remove("supplierFileView");

		sessionModel = clearManfForm(sessionModel);
		clearIngForm(sessionModel);
		clearRegForm(sessionModel);
		clearAttachForm(sessionModel);
		sessionModel.remove("createdInfo");
		sessionModel.remove("modifiedInfo");

		List<RmManf> rmManfTable = (List<RmManf>) sessionModel.get("rmManfItems");
		for (RmManf rmManf : rmManfTable) {
			rmManf.setSupplDeclareStatus(0);
		}

		onEditModeScreenControl(request, sessionModel);

		return new ModelAndView("/main/material/rawMatlForm", sessionModel);
	}

	private void onEditModeScreenControl(HttpServletRequest request, Map<String, Object> sessionModel) {

		int matlStatus = (int) sessionModel.get("matlStatus");

		if (matlStatus == RecordStatusEnum.DRAFT.intValue()) {
			sessionModel.put("btnEdit", true);
			sessionModel.put("btnSubmit", false);
			sessionModel.put("btnReject", true);
			sessionModel.put("btnRework", true);
			sessionModel.put("btnSaveSts", false);
			sessionModel.put("btnSaveSts", true);
			sessionModel.put("btnDeactivate", true);
		}

		if (matlStatus == RecordStatusEnum.SUBMITTED.intValue()
				|| matlStatus == RecordStatusEnum.REWORK.intValue()) {
			sessionModel.put("btnEdit", true);
			sessionModel.put("btnSubmit", false);
			sessionModel.put("btnReject", true);
			sessionModel.put("btnRework", true);
			sessionModel.put("btnSaveSts", true);
			sessionModel.put("btnDeactivate", true);
		}
	}

	private Map<String, Object> onViewModeScreenControl(HttpServletRequest request, RawMaterial rm,
			Map<String, Object> sessionModel) {

		sessionModel.put("screenMode", CommonConstants.SCREEN_MODE_VIEW);
		sessionModel.put("btnDeactivate", true);
		sessionModel.put("btnActivate", true);

		boolean authLvl1 = false;
		boolean authLvl2 = false;
		List<UsrRole> usrRoleList = usrRoleServ.searchUserRole(request.getRemoteUser());
		for (UsrRole usrRole : usrRoleList) {
			if (usrRole.getRoleId().equals(CommonConstants.ROLE_ID_CHECKER)
					|| usrRole.getRoleId().equals(CommonConstants.ROLE_ID_SUPERUSER)
					|| usrRole.getRoleId().equals(CommonConstants.ROLE_ID_HOD)) {
				authLvl2 = true;
			}

			if (usrRole.getRoleId().equals(CommonConstants.ROLE_ID_MAKER)) {
				authLvl1 = true;
			}
		}
		
		sessionModel.put("authLvl1", authLvl1);
		sessionModel.put("authLvl2", authLvl2);
		
		//boolean linkageDeactivateExists = isLinkageDeactivateExists(rm.getRawMatlId());		
		boolean linkageDeactivateExists = false;		

		if (rm.getRecordStatus() == RecordStatusEnum.DRAFT.intValue()) {
			// Checker or superuser
			if (authLvl2) {
				sessionModel.put("btnEdit", false);
				sessionModel.put("btnSubmit", false);
				sessionModel.put("btnReject", false);
				sessionModel.put("btnRework", true);
				sessionModel.put("btnSaveSts", true);

			} else {
				// Maker only
				if (!authLvl2 && authLvl1) {
					if (rm.getCreatorId().equals(request.getRemoteUser())) {
						sessionModel.put("btnEdit", false);
						sessionModel.put("btnSubmit", false);
						sessionModel.put("btnReject", true);
						sessionModel.put("btnRework", true);
						sessionModel.put("btnSaveSts", true);
					} else {
						sessionModel.put("btnAction", true);
					}
				}
			}
		}

		if (rm.getRecordStatus() == RecordStatusEnum.PEND_AUTH.intValue()
				|| rm.getRecordStatus() == RecordStatusEnum.REWORK_PEND_AUTH.intValue()
				|| rm.getRecordStatus() == RecordStatusEnum.CHG_PEND_AUTH.intValue()
				|| rm.getRecordStatus() == RecordStatusEnum.CHG_REWORK.intValue()) {
			// Checker or superuser
			if (authLvl2) {
				sessionModel.put("btnEdit", true);
				sessionModel.put("btnSubmit", false);
				sessionModel.put("btnReject", false);
				sessionModel.put("btnRework", false);
				sessionModel.put("btnSaveSts", true);

			} else {
				// Maker only
				if (!authLvl2 && authLvl1) {
					sessionModel.put("btnAction", true);
				}
			}
		}

		if (rm.getRecordStatus() == RecordStatusEnum.SUBMITTED.intValue()) {
			// Checker or superuser
			if (authLvl2 && !authLvl1) {
				sessionModel.put("btnEdit", false);
				sessionModel.put("btnSubmit", true);
				sessionModel.put("btnReject", true);
				sessionModel.put("btnRework", true);
				sessionModel.put("btnSaveSts", true);
				sessionModel.put("btnDeactivate", linkageDeactivateExists?true:false);
				sessionModel.put("btnActivate", true);

			}
			// Checker or superuser and maker
			else if (authLvl2 && authLvl1) {
				sessionModel.put("btnAction", false);
				sessionModel.put("btnEdit", false);
				sessionModel.put("btnSubmit", true);
				sessionModel.put("btnReject", true);
				sessionModel.put("btnRework", true);
				sessionModel.put("btnSaveSts", true);
				sessionModel.put("btnDeactivate", linkageDeactivateExists?true:false);
				sessionModel.put("btnActivate", true);

			}
			// Maker only
			else if (!authLvl2 && authLvl1) { 
				sessionModel.put("btnAction", false);
				sessionModel.put("btnEdit", false);
				sessionModel.put("btnSubmit", true);
				sessionModel.put("btnReject", true);
				sessionModel.put("btnRework", true);
				sessionModel.put("btnSaveSts", true);
				sessionModel.put("btnDeactivate", linkageDeactivateExists?true:false);
				sessionModel.put("btnActivate", true);
			}
		}

		if (rm.getRecordStatus() == RecordStatusEnum.REJECTED.intValue()) {
			sessionModel.put("btnAction", true);
		}

		if (rm.getRecordStatus() == RecordStatusEnum.REWORK.intValue()) {
			// Checker or superuser
			if (authLvl2) {
				if (rm.getCreatorId().equals(request.getRemoteUser())) {
					sessionModel.put("btnEdit", false);
					sessionModel.put("btnSubmit", true);
					sessionModel.put("btnReject", true);
					sessionModel.put("btnRework", true);
					sessionModel.put("btnSaveSts", true);
				} else {
					sessionModel.put("btnAction", true);
				}

			} else {
				// Maker only
				if (!authLvl2 && authLvl1) {
					sessionModel.put("btnEdit", false);
					sessionModel.put("btnSubmit", true);
					sessionModel.put("btnReject", true);
					sessionModel.put("btnRework", true);
					sessionModel.put("btnSaveSts", true);
				}
			}
		}
		
		if (rm.getRecordStatus() == RecordStatusEnum.DEACTIVATED.intValue()) {
			sessionModel.put("btnAction", false);
			sessionModel.put("btnActivate", false);
			
			sessionModel.put("btnEdit", true);
			sessionModel.put("btnSubmit", true);
			sessionModel.put("btnReject", true);
			sessionModel.put("btnRework", true);
			sessionModel.put("btnDeactivate", true);
		}
		
		if (rm.getRecordStatus() == RecordStatusEnum.PEND_DEACTIVATE.intValue()) {
			sessionModel.put("btnAction", false);
			sessionModel.put("btnActivate", true);
			
			if (authLvl2) {
				sessionModel.put("btnDeactivate", false);				
			} else {
				sessionModel.put("btnAction", true);
				sessionModel.put("btnDeactivate", true);
			}
			
			sessionModel.put("btnEdit", true);
			sessionModel.put("btnSubmit", true);
			sessionModel.put("btnReject", true);
			sessionModel.put("btnRework", true);
		}
		
		if (rm.getRecordStatus() == RecordStatusEnum.PEND_ACTIVATE.intValue()) {
			sessionModel.put("btnAction", false);
			sessionModel.put("btnDeactivate", true);
			
			if (authLvl2) {
				sessionModel.put("btnActivate", false);				
			} else {
				sessionModel.put("btnAction", true);
				sessionModel.put("btnActivate", true);
			}
			
			sessionModel.put("btnEdit", true);
			sessionModel.put("btnSubmit", true);
			sessionModel.put("btnReject", true);
			sessionModel.put("btnRework", true);
		}

		return sessionModel;
	}

	/*
	 * Manufacturer tab button function
	 */
	@PostMapping(value = "/main/material/rawMatlForm", params = "manfAction=add")
	public ModelAndView addToManufList(HttpServletRequest request, @RequestParam(name = "tsNo") String tsNo,
			@RequestParam(name = "manufacturer") int manufacturerId,
			@RequestParam(name = "mfrautocomplete") String mfrautocomplete,
			@RequestParam(name = "supplier", required = false) List<Integer> supplierId,
			@RequestParam(name = "vipdDate") String vipdDate, @RequestParam(name = "vipdFile") MultipartFile vipdFile,
			@RequestParam(name = "supplDeclareDate") String declareDate,
			@RequestParam(name = "supplierFile") MultipartFile supplierFile,
			@RequestParam(name = "countryOfOrigin") String countryId, RawMaterial rawMatl, BindingResult result,
			HttpSession session) {

		sessionModel.put("currTab", "manufacturer");
		sessionModel.put("rawMatlItems", rawMatl);

		if (errorCheckManfList(0, CommonConstants.SCREEN_MODE_ADD, tsNo, manufacturerId, supplierId, vipdDate, declareDate, countryId, vipdFile,
				supplierFile, sessionModel)) {
			holdManfValue(tsNo, manufacturerId, supplierId, vipdDate, declareDate, countryId, mfrautocomplete, sessionModel);
			return new ModelAndView("/main/material/rawMatlForm", sessionModel);
		}

		List<RmManf> rmManfTable = (List<RmManf>) sessionModel.get("rmManfItems");
		int row = rmManfTable.size();
		if (row != 0 && duplicateCheckManfList(tsNo, manufacturerId, rmManfTable, sessionModel)) {
			return new ModelAndView("/main/material/rawMatlForm", sessionModel);
		}

		SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
		RmManf rmManf = new RmManf();

		rmManf.setManfRowNo(row);
		rmManf.setTsNo(tsNo);
		rmManf.setManfId(manufacturerId);
		rmManf.setSupplId(supplierId);

		String vendorName = refManfServ.searchManufacturerList("", "", "", "", "", manufacturerId).get(0).getVendorName();
		rmManf.setVendorName(vendorName);
		rmManf.setManfRecStatus("new");

		String supplierName = "";
		for (int i = 0; i < supplierId.size(); i++) {
			if (supplierName.equals("")) {
				supplierName = refManfServ.searchManufacturerList("", "", "", "", "", supplierId.get(i)).get(0).getVendorName();
			} else {
				supplierName = supplierName + "<br />"
						+ refManfServ.searchManufacturerList("", "", "", "", "", supplierId.get(i)).get(0).getVendorName();
			}
		}

		rmManf.setSupplierName(supplierName);
		rmManf.setOriginCountryId(countryId);
		rmManf.setCountryName(refCountryServ.findOneById(countryId).getCountryName());
		try {
			rmManf.setVipdDate(formatter.parse(vipdDate));
			rmManf.setVipdAnnex2Date(formatter.parse(declareDate));
		} catch (ParseException e) {
			e.printStackTrace();
		}

		try {
			rmManf.setVipdFileName(vipdFile.getOriginalFilename());
			rmManf.setVipdObject(vipdFile.getBytes());
			rmManf.setVipdAnnex2FileName(supplierFile.getOriginalFilename());
			rmManf.setVipdAnnex2Object(supplierFile.getBytes());
		} catch (IOException e) {
			e.printStackTrace();
		}

		List<RmManf> ingDropDownList = (List<RmManf>) sessionModel.get("ingDropDownItems");
		RmManf ingDrpDwn = new RmManf();
		ingDrpDwn.setManfRowNo(row);
		ingDrpDwn.setManfId(manufacturerId);
		ingDrpDwn.setVendorName(vendorName);
		ingDropDownList.add(ingDrpDwn);

		sessionModel.put("ingDropDownItems", ingDropDownList);

		List<RmStat> rmStatTable = (List<RmStat>) sessionModel.get("regItems");
		List<RmStat> rmStatSupplTable = (List<RmStat>) sessionModel.get("rmStatSupplItems");
		if (rmStatTable.size() > 0) {
			for (RmStat rmStat : rmStatTable) {
				RmStat rmStatSuppl = new RmStat();
				rmStatSuppl.setManfId(manufacturerId);
				rmStatSuppl.setVipdStatus(0);
				rmStatSuppl.setCountryId(rmStat.getCountryId());

				rmStatSupplTable.add(rmStatSuppl);
			}

			sessionModel.put("rmStatSupplItems", rmStatSupplTable);
		}

		rmManfTable.add(rmManf);
		sessionModel.put("rmManfItems", rmManfTable);
		clearManfForm(sessionModel);

		return new ModelAndView("/main/material/rawMatlForm", sessionModel);
	}

	@PostMapping(value = "/main/material/rawMatlForm", params = "manfAction=update")
	public ModelAndView updateToManufList(HttpServletRequest request, @RequestParam(name = "rowNo") int rowNo,
			@RequestParam(name = "tsNo") String tsNo, @RequestParam(name = "manufacturer") int manufacturerId,
			@RequestParam(name = "supplier", required = false) List<Integer> supplierId,
			@RequestParam(name = "vipdDate") String vipdDate, @RequestParam(name = "vipdFile") MultipartFile vipdFile,
			@RequestParam(name = "supplDeclareDate") String declareDate,
			@RequestParam(name = "supplierFile") MultipartFile supplierFile,
			@RequestParam(name = "countryOfOrigin") String countryId, RawMaterial rawMatl, BindingResult result,
			HttpSession session) {

		sessionModel.put("currTab", "manufacturer");
		sessionModel.put("rawMatlItems", rawMatl);

		if (errorCheckManfList(rowNo, "update", tsNo, manufacturerId, supplierId, vipdDate, declareDate, countryId,
				vipdFile, supplierFile, sessionModel)) {
			return new ModelAndView("/main/material/rawMatlForm", sessionModel);
		}

		List<RmManf> rmManfTable = (List<RmManf>) sessionModel.get("rmManfItems");
		SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
		List<Integer> oriManfIds = new ArrayList<Integer>();
		for (RmManf rmManf : rmManfTable) {
			if (rmManf.getManfRowNo() == rowNo) {
				oriManfIds.add(rmManf.getManfId());
				rmManf.setTsNo(tsNo);
				rmManf.setManfId(manufacturerId);
				rmManf.setSupplId(supplierId);
				rmManf.setVendorName(
						refManfServ.searchManufacturerList("", "", "", "", "", manufacturerId).get(0).getVendorName());
				String supplierName = "";
				for (int i = 0; i < supplierId.size(); i++) {
					if (supplierName.equals("")) {
						supplierName = refManfServ.searchManufacturerList("", "", "", "", "", supplierId.get(i)).get(0)
								.getVendorName();
					} else {
						supplierName = supplierName + "<br />" + refManfServ
								.searchManufacturerList("", "", "", "", "", supplierId.get(i)).get(0).getVendorName();
					}
				}
				rmManf.setSupplierName(supplierName);

				try {
					rmManf.setVipdDate(formatter.parse(vipdDate));
					rmManf.setVipdAnnex2Date(formatter.parse(declareDate));
				} catch (ParseException e) {
					e.printStackTrace();
				}

				try {
					rmManf.setVipdObject(vipdFile.getBytes());
					rmManf.setVipdAnnex2Object(supplierFile.getBytes());

					if (!vipdFile.getOriginalFilename().equals("")) {
						rmManf.setVipdFileName(vipdFile.getOriginalFilename());
					}

					if (!supplierFile.getOriginalFilename().equals("")) {
						rmManf.setVipdAnnex2FileName(supplierFile.getOriginalFilename());
					}
				} catch (IOException e) {
					e.printStackTrace();
				}

				rmManf.setOriginCountryId(countryId);
				rmManf.setCountryName(refCountryServ.findOneById(countryId).getCountryName());
			}
		}
		
		{
			//check rm_ingredient after manufacturer change
			//update that new manufacturer to rm_ingredient
			List<RmIngredient> rmIngTable = (List<RmIngredient>) sessionModel.get("ingItems");
			for (RmIngredient currentIng : rmIngTable) {
				for (Integer oriManfId : oriManfIds) {
					if (currentIng.getManfId() == oriManfId && currentIng.getManfId() != manufacturerId) {
						currentIng.setManfId(manufacturerId);
						currentIng.setVendorName(refManfServ.findOneById(manufacturerId).getVendorName());
					}
				}
			}
			sessionModel.put("ingItems", rmIngTable);			
			
			//check rm_stat after manufacturer change
			//update that new manufacturer to rm_stat
			List<RmStat> rmStatSupplTable = (List<RmStat>) sessionModel.get("rmStatSupplItems");
			for (RmStat currentRmStatSuppl : rmStatSupplTable) {
				for (Integer oriManfId : oriManfIds) {
					if(currentRmStatSuppl.getManfId() == oriManfId && currentRmStatSuppl.getManfId() != manufacturerId) {
						currentRmStatSuppl.setManfId(manufacturerId);
						currentRmStatSuppl.setManfIdChanged(true);
					}
				}
			}
			sessionModel.put("rmStatSupplItems", rmStatSupplTable);				
		}

		sessionModel.put("rmManfItems", rmManfTable);
		sessionModel = setManfButton(false, true, true, sessionModel);
		clearManfForm(sessionModel);

		return new ModelAndView("/main/material/rawMatlForm", sessionModel);
	}

	@PostMapping(value = "/main/material/rawMatlForm", params = "manfAction=delete")
	public ModelAndView deleteManufList(HttpServletRequest request, @RequestParam(name = "rowNo") int rowNo,
			RawMaterial rawMatl, BindingResult result, HttpSession session) {

		sessionModel.put("rawMatlItems", rawMatl);
		sessionModel.remove("error");
		List<RmManf> rmManfTable = (List<RmManf>) sessionModel.get("rmManfItems");
		List<RmManf> ingDropDownList = (List<RmManf>) sessionModel.get("ingDropDownItems");
		List<RmIngredient> rmIngTable = (List<RmIngredient>) sessionModel.get("ingItems");
		List<RmStat> rmStatTable = (List<RmStat>) sessionModel.get("regItems");
		List<RmStat> rmStatSupplTable = (List<RmStat>) sessionModel.get("rmStatSupplItems");

		try {
			for (RmManf rmManf : rmManfTable) {
				if (rmManf.getManfRowNo() == rowNo) {
					Iterator<RmIngredient> rmIngIterator = rmIngTable.iterator();
					while (rmIngIterator.hasNext()) {
						if (rmIngIterator.next().getManfId() == rmManf.getManfId()) {
							rmIngIterator.remove();
						}
					}

					Iterator<RmStat> rmStatIterator = rmStatSupplTable.iterator();
					while (rmStatIterator.hasNext()) {
						if (rmStatIterator.next().getManfId() == rmManf.getManfId()) {
							rmStatIterator.remove();
						}
					}
				}
			}

			for (RmStat rmStat : rmStatTable) {
				boolean finalStatus = true;

				for (RmStat rmStatDetail : rmStatSupplTable) {
					if (rmStat.getCountryId().equals(rmStatDetail.getCountryId())) {
						if (rmStatDetail.getVipdStatus() == FinalStatusEnum.NOT_PERMITTED.intValue()
								|| rmStatDetail.getVipdStatus() == FinalStatusEnum.NOT_SURE.intValue()
								|| rmStatDetail.getVipdStatus() == FinalStatusEnum.YET_COMPELTE.intValue()) {
							finalStatus = false;
						}
					}
				}

				if (finalStatus) {
					if (rmStat.getFinalStatus() != FinalStatusEnum.PERMITTED.intValue()) {
						sessionModel.put("regChange", true);
					}

					rmStat.setFinalStatus(FinalStatusEnum.PERMITTED.intValue());
					rmStat.setFinalStatusDesc(getFinalStatusDesc(FinalStatusEnum.PERMITTED.intValue()));
				} else {
					if (rmStat.getFinalStatus() != FinalStatusEnum.NOT_PERMITTED.intValue()) {
						sessionModel.put("regChange", true);
					}

					rmStat.setFinalStatus(FinalStatusEnum.NOT_PERMITTED.intValue());
					rmStat.setFinalStatusDesc(getFinalStatusDesc(FinalStatusEnum.NOT_PERMITTED.intValue()));
				}
			}

			List<RmManf> rmManfDel = (List<RmManf>) sessionModel.get("rmManfDel");

			rmManfDel.add(rmManfTable.get(rowNo));
			rmManfTable.remove(rowNo);
			ingDropDownList.remove(rowNo);

			int reCalRowNo = 0;
			for (int i = 0; i < rmManfTable.size(); i++) {
				rmManfTable.get(i).setManfRowNo(reCalRowNo);
				reCalRowNo++;
			}
		} catch (Exception e) {
			sessionModel.put("rmManfItems", rmManfTable);
		}

		sessionModel.put("rmStatSupplItems", rmStatSupplTable);
		sessionModel.put("regItems", rmStatTable);
		sessionModel.put("rmManfItems", rmManfTable);
		sessionModel = setManfButton(false, true, true, sessionModel);
		sessionModel.put("currTab", "manufacturer");
		clearManfForm(sessionModel);

		return new ModelAndView("/main/material/rawMatlForm", sessionModel);
	}

	@GetMapping("/main/material/rawMatlFormGetData")
	public ModelAndView retrieveManufTableData(HttpServletRequest request, @RequestParam(name = "id") int id,
			HttpSession session) {

		String screenMode = sessionModel.get("screenMode").toString();

		sessionModel.remove("error");
		List<RmManf> rmManfTable = (List<RmManf>) sessionModel.get("rmManfItems");

		for (RmManf rmManf : rmManfTable) {
			if (rmManf.getManfRowNo() == id) {
				sessionModel.put("rowNo", rmManf.getManfRowNo());
				sessionModel.put("manfTsNo", rmManf.getTsNo());
				sessionModel.put("manfManufacturer", rmManf.getManfId());
				sessionModel.put("manufacturer", rmManf.getManfId());
				sessionModel.put("mfrautocomplete", refManfServ.findOneById(rmManf.getManfId()).getVendorName());
				sessionModel.put("manfSupplier", rmManf.getSupplId());

				String supplierName = "";
				for (int i = 0; i < rmManf.getSupplId().size(); i++) {
					if (supplierName.equals("")) {
						supplierName = refManfServ.searchManufacturerList("", "", "", "", "", rmManf.getSupplId().get(i)).get(0)
								.getVendorName();
					} else {
						supplierName = supplierName + "<br />"
								+ refManfServ.searchManufacturerList("", "", "", "", "", rmManf.getSupplId().get(i)).get(0)
										.getVendorName();
					}
				}

				sessionModel.put("manfSupplierText", supplierName);
				sessionModel.put("manfVipdDate", rmManf.getVipdDate());
				sessionModel.put("manfDeclareDate", rmManf.getVipdAnnex2Date());
				sessionModel.put("manfOriginCountry", rmManf.getOriginCountryId());

				if (screenMode.equals(CommonConstants.SCREEN_MODE_VIEW)) {
					sessionModel.put("vipdFileView", rmManf.getVipdFileName());
					sessionModel.put("supplierFileView", rmManf.getVipdAnnex2FileName());
				}
			}
		}

		if (!screenMode.equals(CommonConstants.SCREEN_MODE_VIEW)) {
			sessionModel = setManfButton(false, false, false, sessionModel);
		}
		sessionModel.put("currTab", "manufacturer");

		return new ModelAndView("/main/material/rawMatlForm", sessionModel);
	}

	@GetMapping("/main/material/rawMatlFormDownloadVipd")
	public ResponseEntity<byte[]> downloadVipdFile(HttpServletRequest request, @RequestParam(name = "id") int id,
			HttpSession session) {

		String fileName = "";
		byte[] data = new byte[1024];
		List<RmManf> rmManfTable = (List<RmManf>) sessionModel.get("rmManfItems");

		for (RmManf rmManf : rmManfTable) {
			if (rmManf.getManfRowNo() == id) {
				fileName = rmManf.getVipdFileName();
				data = rmManf.getVipdObject();
			}
		}

		MediaType mt = MediaTypeUtils.getMediaTypeForFileName(servletContext, fileName);

		return ResponseEntity.ok().contentType(mt)
				.header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + fileName + "\"").body(data);
	}

	@GetMapping("/main/material/rawMatlFormDownloadSuppl")
	public ResponseEntity<byte[]> downloadSupplFile(HttpServletRequest request, @RequestParam(name = "id") int id,
			HttpSession session) {

		String fileName = "";
		byte[] data = new byte[1024];
		List<RmManf> rmManfTable = (List<RmManf>) sessionModel.get("rmManfItems");

		for (RmManf rmManf : rmManfTable) {
			if (rmManf.getManfRowNo() == id) {
				fileName = rmManf.getVipdAnnex2FileName();
				data = rmManf.getVipdAnnex2Object();
			}
		}

		MediaType mt = MediaTypeUtils.getMediaTypeForFileName(servletContext, fileName);

		return ResponseEntity.ok().contentType(mt)
				.header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + fileName + "\"").body(data);
	}

	private void holdManfValue(String tsNo, int manufacturerId, List<Integer> supplierId, String vipdDate,
			String declareDate, String countryId, String mfrautocomplete, Map<String, Object> sessionModel) {

		sessionModel.put("manfTsNo", tsNo);
		sessionModel.put("manfManufacturer", manufacturerId);
		sessionModel.put("manufacturer", manufacturerId);
		sessionModel.put("mfrautocomplete", mfrautocomplete);
		sessionModel.put("manfSupplier", supplierId);
		if (!vipdDate.isEmpty()) {
			sessionModel.put("manfVipdDate", vipdDate);
		}
		if (!declareDate.isEmpty()) {
			sessionModel.put("manfDeclareDate", declareDate);
		}
		sessionModel.put("manfOriginCountry", countryId);
	}

	private Map<String, Object> setManfButton(boolean manfAdd, boolean manfUpdate, boolean manfDelete,
			Map<String, Object> sessionModel) {

		sessionModel.put("manfAdd", manfAdd);
		sessionModel.put("manfUpdate", manfUpdate);
		sessionModel.put("manfDelete", manfDelete);

		return sessionModel;
	}

	private boolean duplicateCheckManfList(String tsNo, int manufacturerId, List<RmManf> rmManfTable,
			Map<String, Object> sessionModel) {

		for (RmManf rmManf : rmManfTable) {
			if (manufacturerId == rmManf.getManfId()) {
				sessionModel.put("error", "Manufacturer already exist.");
				return true;
			}
		}

		return false;
	}

	private boolean errorCheckManfList(int row, String mode, String tsNo, int manufacturerId, List<Integer> supplierId,
			String vipdDate, String declareDate, String countryId, MultipartFile vipdFile, MultipartFile supplierFile,
			Map<String, Object> sessionModel) {

		sessionModel.remove("error");

		String errorMsg = "";
		errorMsg += commonValidatorServ.validateMandatoryInput(tsNo, "Ts No");
		errorMsg += commonValidatorServ.validateMandatorySelect(manufacturerId, "Manufacturer");
		if (supplierId == null) {
			errorMsg += "Supplier is mandatory.<br />";
		}
		errorMsg += commonValidatorServ.validateMandatoryInput(vipdDate, "VIPD Date");
		errorMsg += commonValidatorServ.validateMandatoryInput(declareDate, "Declaration Date");
		errorMsg += commonValidatorServ.validateDateFormat(vipdDate, "VIPD Date");
		errorMsg += commonValidatorServ.validateDateFormat(declareDate, "Declaration Date");
		errorMsg += commonValidatorServ.validateMandatoryInput(countryId, "Country of Origin");
		errorMsg += commonValidatorServ.validateFileSize(vipdFile.getSize(), "VIPD File");
		errorMsg += commonValidatorServ.validateFileSize(supplierFile.getSize(), "Supplier File");

		if (mode.equals(CommonConstants.SCREEN_MODE_ADD)) {
			if (vipdFile.getOriginalFilename().equals("")) {
				errorMsg += "Vipd file is mandatory.<br />";
			}

			if (supplierFile.getOriginalFilename().equals("")) {
				errorMsg += "Supplier declaration file is mandatory.<br />";
			}
		} else if (mode.equals("update")) {
			List<RmManf> rmManfTable = (List<RmManf>) sessionModel.get("rmManfItems");
			for (RmManf rmManf : rmManfTable) {
				if (rmManf.getManfRowNo() == row) {
					if (rmManf.getVipdObject() == null && vipdFile.getOriginalFilename().equals("")) {
						errorMsg += "Vipd file is mandatory.<br />";
					}

					if (rmManf.getVipdAnnex2Object() == null && supplierFile.getOriginalFilename().equals("")) {
						errorMsg += "Supplier declaration file is mandatory.<br />";
					}
				}
			}
		}

		if (errorMsg.length() != 0) {
			sessionModel.put("error", errorMsg);
			return true;
		}

		return false;
	}

	private Map<String, Object> clearManfForm(Map<String, Object> sessionModel) {

		sessionModel.remove("rowNo");
		sessionModel.remove("manfTsNo");
		sessionModel.remove("manfManufacturer");
		sessionModel.remove("mfrautocomplete");
		sessionModel.remove("manfSupplier");
		sessionModel.remove("manfVipdDate");
		sessionModel.remove("manfDeclareDate");
		sessionModel.remove("manfOriginCountry");

		return sessionModel;
	}

	/*
	 * Ingredient tab button function
	 */
	@PostMapping(value = "/main/material/rawMatlForm", params = "ingAction=add")
	public ModelAndView addToIngList(HttpServletRequest request,
			@RequestParam(name = "ingManufacturer") int ingManufacturer,
			@RequestParam(name = "ingContent") String ingContent, @RequestParam(name = "ingPercent") String ingPercent,
			@RequestParam(name = "ingIns") String ingIns, @RequestParam(name = "ingENo") String ingENo,
			@RequestParam(name = "ingFema") String ingFema, @RequestParam(name = "ingJecfa") String ingJecfa,
			@RequestParam(name = "ingCas") String ingCas, RawMaterial rawMatl, BindingResult result,
			HttpSession session) {

		sessionModel.put("currTab", "ingredient");
		sessionModel.put("rawMatlItems", rawMatl);

		if (errorCheckIngList(ingManufacturer, ingContent, ingPercent, sessionModel)) {
			holdIngValue(ingManufacturer, ingContent, ingPercent, ingIns, ingENo, ingFema, ingJecfa, ingCas,
					sessionModel);
			return new ModelAndView("/main/material/rawMatlForm", sessionModel);
		}

		List<RmIngredient> rmIngTable = (List<RmIngredient>) sessionModel.get("ingItems");
		int row = rmIngTable.size();
		if (row != 0 && duplicateCheckIngList(ingManufacturer, ingContent, rmIngTable, sessionModel)) {
			return new ModelAndView("/main/material/rawMatlForm", sessionModel);
		}

		RmIngredient rmIng = new RmIngredient();

		rmIng.setIngRowNo(row);
		rmIng.setManfId(ingManufacturer);
		rmIng.setVendorName(refManfServ.searchManufacturerList("", "", "", "", "", ingManufacturer).get(0).getVendorName());
		rmIng.setIngName(ingContent);
		rmIng.setCompPerc(ingPercent);
		rmIng.setInsNo(ingIns);
		rmIng.setENo(ingENo);
		rmIng.setFemaNo(ingFema);
		rmIng.setJecfaNo(ingJecfa);
		rmIng.setCasNo(ingCas);
		rmIng.setIngRecStatus("new");

		rmIngTable.add(rmIng);
		sessionModel.put("ingItems", rmIngTable);
		setIngButton(false, true, true, sessionModel);
		clearIngForm(sessionModel);

		return new ModelAndView("/main/material/rawMatlForm", sessionModel);
	}

	@PostMapping(value = "/main/material/rawMatlForm", params = "ingAction=update")
	public ModelAndView updateToIngList(HttpServletRequest request,
			@RequestParam(name = "ingManufacturer") int ingManufacturer, @RequestParam(name = "ingRowNo") int ingRowNo,
			@RequestParam(name = "ingContent") String ingContent, @RequestParam(name = "ingPercent") String ingPercent,
			@RequestParam(name = "ingIns") String ingIns, @RequestParam(name = "ingENo") String ingENo,
			@RequestParam(name = "ingFema") String ingFema, @RequestParam(name = "ingJecfa") String ingJecfa,
			@RequestParam(name = "ingCas") String ingCas, RawMaterial rawMatl, BindingResult result,
			HttpSession session) {

		sessionModel.put("currTab", "ingredient");
		sessionModel.put("rawMatlItems", rawMatl);

		if (errorCheckIngList(ingManufacturer, ingContent, ingPercent, sessionModel)) {
			return new ModelAndView("/main/material/rawMatlForm", sessionModel);
		}

		List<RmIngredient> rmIngTable = (List<RmIngredient>) sessionModel.get("ingItems");

		for (RmIngredient rmIng : rmIngTable) {
			if (rmIng.getIngRowNo() == ingRowNo) {
				rmIng.setManfId(ingManufacturer);
				rmIng.setVendorName(
						refManfServ.searchManufacturerList("", "", "", "", "", ingManufacturer).get(0).getVendorName());
				rmIng.setIngName(ingContent);
				rmIng.setCompPerc(ingPercent);
				rmIng.setInsNo(ingIns);
				rmIng.setENo(ingENo);
				rmIng.setFemaNo(ingFema);
				rmIng.setJecfaNo(ingJecfa);
				rmIng.setCasNo(ingCas);
			}
		}

		sessionModel.put("ingItems", rmIngTable);
		setIngButton(false, true, true, sessionModel);
		clearIngForm(sessionModel);

		return new ModelAndView("/main/material/rawMatlForm", sessionModel);
	}

	@PostMapping(value = "/main/material/rawMatlForm", params = "ingAction=delete")
	public ModelAndView deleteIngList(HttpServletRequest request, @RequestParam(name = "ingRowNo") int ingRowNo,
			RawMaterial rawMatl, BindingResult result, HttpSession session) {

		sessionModel.put("rawMatlItems", rawMatl);
		sessionModel.remove("error");
		List<RmIngredient> rmIngTable = (List<RmIngredient>) sessionModel.get("ingItems");

		try {
			List<RmIngredient> rmIngDel = (List<RmIngredient>) sessionModel.get("rmIngDel");

			rmIngDel.add(rmIngTable.get(ingRowNo));
			rmIngTable.remove(ingRowNo);

			int reCalRowNo = 0;
			for (int i = 0; i < rmIngTable.size(); i++) {
				rmIngTable.get(i).setIngRowNo(reCalRowNo);
				reCalRowNo++;
			}
		} catch (Exception e) {
			sessionModel.put("ingItems", rmIngTable);
		}

		sessionModel.put("ingItems", rmIngTable);
		setIngButton(false, true, true, sessionModel);
		sessionModel.put("currTab", "ingredient");
		clearIngForm(sessionModel);

		return new ModelAndView("/main/material/rawMatlForm", sessionModel);
	}

	@GetMapping("/main/material/rawMatlFormIngGetData")
	public ModelAndView retrieveIngTableData(HttpServletRequest request, @RequestParam(name = "id") int id,
			HttpSession session) {

		String screenMode = sessionModel.get("screenMode").toString();

		sessionModel.remove("error");
		List<RmIngredient> rmIngTable = (List<RmIngredient>) sessionModel.get("ingItems");

		for (RmIngredient rmIng : rmIngTable) {
			if (rmIng.getIngRowNo() == id) {
				sessionModel.put("ingRowNo", rmIng.getIngRowNo());
				sessionModel.put("ingManufacturer", rmIng.getManfId());
				sessionModel.put("ingContent", rmIng.getIngName());
				sessionModel.put("ingPercent", rmIng.getCompPerc());
				sessionModel.put("ingIns", rmIng.getInsNo());
				sessionModel.put("ingENo", rmIng.getENo());
				sessionModel.put("ingFema", rmIng.getFemaNo());
				sessionModel.put("ingJecfa", rmIng.getJecfaNo());
				sessionModel.put("ingCas", rmIng.getCasNo());
			}
		}

		sessionModel.put("currTab", "ingredient");
		if (!screenMode.equals(CommonConstants.SCREEN_MODE_VIEW)) {
			setIngButton(false, false, false, sessionModel);
		}

		return new ModelAndView("/main/material/rawMatlForm", sessionModel);
	}

	private Map<String, Object> setIngButton(boolean ingAdd, boolean ingUpdate, boolean ingDelete,
			Map<String, Object> sessionModel) {

		sessionModel.put("ingAdd", ingAdd);
		sessionModel.put("ingUpdate", ingUpdate);
		sessionModel.put("ingDelete", ingDelete);

		return sessionModel;
	}

	private void holdIngValue(int ingManufacturer, String ingContent, String ingPercent, String ingIns, String ingENo,
			String ingFema, String ingJecfa, String ingCas, Map<String, Object> sessionModel) {

		sessionModel.put("ingManufacturer", ingManufacturer);
		sessionModel.put("ingContent", ingContent);
		sessionModel.put("ingPercent", ingPercent);
		sessionModel.put("ingIns", ingIns);
		sessionModel.put("ingENo", ingENo);
		sessionModel.put("ingFema", ingFema);
		sessionModel.put("ingJecfa", ingJecfa);
		sessionModel.put("ingCas", ingCas);
	}

	private boolean duplicateCheckIngList(int ingManf, String ingContent, List<RmIngredient> rmIngTable,
			Map<String, Object> sessionModel) {

		for (RmIngredient rmIng : rmIngTable) {
			if (ingContent.equals(rmIng.getIngName()) && ingManf == rmIng.getManfId()) {
				sessionModel.put("error", "Duplicate combination - Manufacturer and Ingredient Content.");
				return true;
			}
		}

		return false;
	}

	private boolean errorCheckIngList(int ingManf, String ingContent, String ingPercent,
			Map<String, Object> sessionModel) {

		sessionModel.remove("error");

		String errorMsg = "";
		errorMsg += commonValidatorServ.validateMandatorySelect(ingManf, "Ingredient Manufacturer");
		errorMsg += commonValidatorServ.validateMandatoryInput(ingContent, "Ingredient Content");
		errorMsg += commonValidatorServ.validateMandatoryInput(ingPercent, "Percentage");

		if (errorMsg.length() != 0) {
			sessionModel.put("error", errorMsg);
			return true;
		}

		return false;
	}

	private void clearIngForm(Map<String, Object> sessionModel) {

		sessionModel.remove("ingRowNo");
		sessionModel.remove("ingContent");
		sessionModel.remove("ingPercent");
		sessionModel.remove("ingIns");
		sessionModel.remove("ingENo");
		sessionModel.remove("ingFema");
		sessionModel.remove("ingJecfa");
		sessionModel.remove("ingCas");
	}

	/*
	 * Regulation tab button function
	 */
	@PostMapping(value = "/main/material/rawMatlForm", params = "regAction=add")
	public ModelAndView addToRegList(HttpServletRequest request,
			@RequestParam(name = "regCountryId") String regCountryId,
			@RequestParam(name = "regFinalStatus") int regFinalStatus,
			@RequestParam(name = "manfSupplDeclare", required = false) int[] manfSupplDeclare, RawMaterial rawMatl,
			BindingResult result, HttpSession session) {

		sessionModel.put("currTab", "regulation");
		sessionModel.put("rawMatlItems", rawMatl);

		if (errorCheckRegList(regCountryId, regFinalStatus, manfSupplDeclare, sessionModel)) {
			holdRegValue(regCountryId, regFinalStatus, manfSupplDeclare, sessionModel);
			return new ModelAndView("/main/material/rawMatlForm", sessionModel);
		}

		List<RmStat> rmStatTable = (List<RmStat>) sessionModel.get("regItems");
		int row = rmStatTable.size();
		if (row != 0 && duplicateCheckRegList(regCountryId, rmStatTable, sessionModel)) {
			return new ModelAndView("/main/material/rawMatlForm", sessionModel);
		}

		RmStat rmStat = new RmStat();
		rmStat.setRmStatRowNo(row);
		rmStat.setCountryId(regCountryId);
		rmStat.setCountryName(refCountryServ.findOneById(regCountryId).getCountryName());
		rmStat.setFinalStatus(regFinalStatus);
		rmStat.setFinalStatusDesc(getFinalStatusDesc(regFinalStatus));
		rmStat.setDataChange(String.valueOf(CommonConstants.FLAG_NO));
		rmStat.setRmStatRecStatus("new");

		rmStatTable.add(rmStat);
		rmStatTable.sort(Comparator.comparing(a -> a.getCountryName()));
		sessionModel.put("regItems", rmStatTable);

		List<RmManf> rmManfTable = (List<RmManf>) sessionModel.get("rmManfItems");
		List<RmStat> rmStatSupplTable = (List<RmStat>) sessionModel.get("rmStatSupplItems");

		for (int i = 0; i < rmManfTable.size(); i++) {
			RmStat rmStatSuppl = new RmStat();
			rmStatSuppl.setManfId(rmManfTable.get(i).getManfId());
			rmStatSuppl.setVipdStatus(manfSupplDeclare[i]);
			rmStatSuppl.setCountryId(regCountryId);

			rmStatSupplTable.add(rmStatSuppl);
		}

		sessionModel.put("rmStatSupplItems", rmStatSupplTable);

		setRegButton(false, true, true, sessionModel);
		clearRegForm(sessionModel);
		clearSupplDropDown(sessionModel);

		return new ModelAndView("/main/material/rawMatlForm", sessionModel);
	}

	@PostMapping(value = "/main/material/rawMatlForm", params = "regAction=update")
	public ModelAndView updateToRegList(HttpServletRequest request, @RequestParam(name = "regRowNo") int regRowNo,
			@RequestParam(name = "regCountryId") String regCountryId,
			@RequestParam(name = "regFinalStatus") int regFinalStatus,
			@RequestParam(name = "manfSupplDeclare") int[] manfSupplDeclare, RawMaterial rawMatl, BindingResult result,
			HttpSession session) {

		sessionModel.put("currTab", "regulation");
		sessionModel.put("rawMatlItems", rawMatl);

		if (errorCheckRegList(regCountryId, regFinalStatus, manfSupplDeclare, sessionModel)) {
			return new ModelAndView("/main/material/rawMatlForm", sessionModel);
		}

		List<RmStat> rmStatTable = (List<RmStat>) sessionModel.get("regItems");
		List<RmManf> rmManfTable = (List<RmManf>) sessionModel.get("rmManfItems");
		List<RmStat> rmStatSupplTable = (List<RmStat>) sessionModel.get("rmStatSupplItems");
		List<String> effCountry = (List<String>) sessionModel.get("effRegCountries");
		
		if(null == effCountry) effCountry = new ArrayList<String>();

		for (RmStat rmStat : rmStatTable) {
			if (rmStat.getRmStatRowNo() == regRowNo) {
				rmStat.setCountryId(regCountryId);
				rmStat.setCountryName(
						refCountryServ.findOneById(regCountryId).getCountryName());
				if (rmStat.getFinalStatus() != regFinalStatus) {
					sessionModel.put("regChange", true);
					rmStat.setDataChange(String.valueOf(CommonConstants.FLAG_YES));
					effCountry.add(rmStat.getCountryId());
				}
				rmStat.setFinalStatus(regFinalStatus);
				rmStat.setFinalStatusDesc(getFinalStatusDesc(regFinalStatus));
			}
		}

		if(effCountry.size()>0)	sessionModel.put("effRegCountries", effCountry);

		for (int i = 0; i < rmManfTable.size(); i++) {
			for (RmStat rmStatSuppl : rmStatSupplTable) {
				if (rmStatSuppl.getManfId() == rmManfTable.get(i).getManfId()
						&& rmStatSuppl.getCountryId().equals(regCountryId)) {
					rmStatSuppl.setVipdStatus(manfSupplDeclare[i]);
				}
			}
		}

		rmStatTable.sort(Comparator.comparing(a -> a.getCountryName()));
		sessionModel.put("regItems", rmStatTable);
		sessionModel.put("rmStatSupplItems", rmStatSupplTable);
		setRegButton(false, true, true, sessionModel);
		clearRegForm(sessionModel);
		clearSupplDropDown(sessionModel);

		return new ModelAndView("/main/material/rawMatlForm", sessionModel);
	}

	@PostMapping(value = "/main/material/rawMatlForm", params = "regAction=delete")
	public ModelAndView deleteRegList(HttpServletRequest request, @RequestParam(name = "regRowNo") int regRowNo,
			RawMaterial rawMatl, BindingResult result, HttpSession session) {

		sessionModel.put("rawMatlItems", rawMatl);
		sessionModel.remove("error");
		List<RmStat> rmStatTable = (List<RmStat>) sessionModel.get("regItems");
		List<RmStat> rmStatSupplTable = (List<RmStat>) sessionModel.get("rmStatSupplItems");

		try {
			String delCountry = "";
			for (RmStat rmStat : rmStatTable) {
				if (rmStat.getRmStatRowNo() == regRowNo) {
					delCountry = rmStat.getCountryId();
				}
			}

			for (int j = 0; j < rmStatSupplTable.size(); j++) {
				if (rmStatSupplTable.get(j).getCountryId().equals(delCountry)) {
					rmStatSupplTable.remove(j);
				}
			}

			List<RmStat> rmStatDel = (List<RmStat>) sessionModel.get("rmStatDel");

			rmStatDel.add(rmStatTable.get(regRowNo));
			rmStatTable.remove(regRowNo);

			int reCalRowNo = 0;
			for (int i = 0; i < rmStatTable.size(); i++) {
				rmStatTable.get(i).setRmStatRowNo(reCalRowNo);
				reCalRowNo++;
			}
		} catch (Exception e) {
			sessionModel.put("regItems", rmStatTable);
		}

		rmStatTable.sort(Comparator.comparing(a -> a.getCountryName()));
		sessionModel.put("regItems", rmStatTable);
		setRegButton(false, true, true, sessionModel);
		sessionModel.put("currTab", "regulation");
		clearRegForm(sessionModel);
		clearSupplDropDown(sessionModel);

		return new ModelAndView("/main/material/rawMatlForm", sessionModel);
	}

	@GetMapping("/main/material/rawMatlFormRegGetData")
	public ModelAndView retrieveRegTableData(HttpServletRequest request, @RequestParam(name = "id") int id,
			HttpSession session) {

		String screenMode = sessionModel.get("screenMode").toString();
		
		logger.debug("retrieveRegTableData() screenMode={}",screenMode);
		
		sessionModel.remove("error");
		List<RmStat> rmStatTable = (List<RmStat>) sessionModel.get("regItems");
		List<RmManf> rmManfTable = (List<RmManf>) sessionModel.get("rmManfItems");
		List<RmStat> rmStatSupplTable = (List<RmStat>) sessionModel.get("rmStatSupplItems");

		for (RmStat rmStat : rmStatTable) {
			if (rmStat.getRmStatRowNo() == id) {
				sessionModel.put("regRowNo", rmStat.getRmStatRowNo());
				sessionModel.put("regCountryId", rmStat.getCountryId());
				sessionModel.put("regFinalStatus", rmStat.getFinalStatus());

				rawMatlFormReg(rmStat.getCountryId(), sessionModel);

				for (RmManf rmManf : rmManfTable) {
					int status = 0;
					for (RmStat rmStatSuppl : rmStatSupplTable) {
						if (rmStatSuppl.getManfId() == rmManf.getManfId()
								&& rmStatSuppl.getCountryId().equals(rmStat.getCountryId())) {
							status = rmStatSuppl.getVipdStatus();
						}
					}

					rmManf.setSupplDeclareStatus(status);
				}
			}
		}

		sessionModel.put("rmManfItems", rmManfTable);
		sessionModel.put("currTab", "regulation");
		if (!screenMode.equals(CommonConstants.SCREEN_MODE_VIEW)) {
			setRegButton(false, false, false, sessionModel);
		}

		return new ModelAndView("/main/material/rawMatlForm", sessionModel);
	}

	private void clearSupplDropDown(Map<String, Object> sessionModel) {

		List<RmManf> rmManfTable = (List<RmManf>) sessionModel.get("rmManfItems");
		for (RmManf rmManf : rmManfTable) {
			rmManf.setSupplDeclareStatus(0);
		}

		sessionModel.put("rmManfItems", rmManfTable);
	}

	private String getFinalStatusDesc(int regFinalStatus) {

		String finalStatusDesc = "";
		if (regFinalStatus == FinalStatusEnum.PERMITTED.intValue()) {
			finalStatusDesc = msgSource.getMessage(FinalStatusEnum.PERMITTED.strKey(), null, Locale.getDefault());
		} else if (regFinalStatus == FinalStatusEnum.NOT_PERMITTED.intValue()) {
			finalStatusDesc = msgSource.getMessage(FinalStatusEnum.NOT_PERMITTED.strKey(), null, Locale.getDefault());
		} else if (regFinalStatus == FinalStatusEnum.NOT_SURE.intValue()) {
			finalStatusDesc = msgSource.getMessage(FinalStatusEnum.NOT_SURE.strKey(), null, Locale.getDefault());
		} else if (regFinalStatus == FinalStatusEnum.YET_COMPELTE.intValue()) {
			finalStatusDesc = msgSource.getMessage(FinalStatusEnum.YET_COMPELTE.strKey(), null, Locale.getDefault());
		}

		return finalStatusDesc;
	}

	private Map<String, Object> setRegButton(boolean regAdd, boolean regUpdate, boolean regDelete,
			Map<String, Object> sessionModel) {

		sessionModel.put("regAdd", regAdd);
		sessionModel.put("regUpdate", regUpdate);
		sessionModel.put("regDelete", regDelete);

		return sessionModel;
	}

	private void holdRegValue(String regCountryId, int regFinalStatus, int[] manfSupplDeclare,
			Map<String, Object> sessionModel) {

		sessionModel.put("regCountryId", regCountryId);
		sessionModel.put("regFinalStatus", regFinalStatus);

		List<RmManf> rmManfTable = (List<RmManf>) sessionModel.get("rmManfItems");
		for (int i = 0; i < rmManfTable.size(); i++) {
			rmManfTable.get(i).setSupplDeclareStatus(manfSupplDeclare[i]);
		}
	}

	private boolean duplicateCheckRegList(String regCountryId, List<RmStat> rmRegTable,
			Map<String, Object> sessionModel) {

		for (RmStat rmStat : rmRegTable) {
			if (regCountryId.equals(rmStat.getCountryId())) {
				sessionModel.put("error", "Country existed in the list.");
				return true;
			}
		}

		return false;
	}

	private boolean errorCheckRegList(String regCountryId, int regFinalStatus, int[] manfSupplDeclare,
			Map<String, Object> sessionModel) {

		sessionModel.remove("error");

		String errorMsg = "";
		errorMsg += commonValidatorServ.validateMandatoryInput(regCountryId, "Country");
		errorMsg += commonValidatorServ.validateMandatorySelect(regFinalStatus, "Country Final Status");

		boolean manfSupplDeclareErr = false;

		if (manfSupplDeclare == null) {
			manfSupplDeclareErr = true;
		} else {
			for (int i = 0; i < manfSupplDeclare.length; i++) {
				if (manfSupplDeclare[i] == 0) {
					manfSupplDeclareErr = true;
				}
			}
		}

		if (manfSupplDeclareErr) {
			errorMsg += "Supplier Declaration Status is mandatory.<br />";
		}

		if (errorMsg.length() != 0) {
			sessionModel.put("error", errorMsg);
			return true;
		}
		
		// check regulation info existence, just to alert user
		/*String screenMode = sessionModel.get("screenMode").toString();	
		if(!screenMode.equals(CommonConstants.SCREEN_MODE_ADD)){
			try {
				int rawMatlId = 0;
				
				if(!screenMode.equals(CommonConstants.SCREEN_MODE_ADD))
					rawMatlId = (int) sessionModel.get("rawMatlId");
				
				Regl reg = regServ.searchReglList(rawMatlId, regCountryId).get(0);
				
			} catch (Exception ex) {
				
				sessionModel.put("success", "Missing regulation information for "+regCountryId);
			}
		}*/

		return false;
	}

	private void clearRegForm(Map<String, Object> sessionModel) {

		sessionModel.remove("regCountryId");
		sessionModel.remove("regFinalStatus");
	}

	@PostMapping(value = "/main/material/rawMatlForm", params = "attachAction=add")
	public ModelAndView addToAttachList(@RequestParam(name = "attachTitle") String attachTitle,
			@RequestParam(name = "attachCategory") int attachCategory,
			@RequestParam(name = "attachFile") MultipartFile attachFile, HttpServletRequest request,
			RawMaterial rawMatl, BindingResult result, HttpSession session) {

		sessionModel.put("currTab", "attachment");
		sessionModel.put("rawMatlItems", rawMatl);
		List<Document> attachTable = (List<Document>) sessionModel.get("attachItems");
		int docRowNo = attachTable.size();

		if (errorCheckAttachment(attachTitle, attachCategory, attachFile, attachTable, 1, sessionModel)) {
			holdAttachValue(attachTitle, attachCategory, sessionModel);
			return new ModelAndView("/main/material/rawMatlForm", sessionModel);
		}

		Document doc = new Document();
		doc.setDocRowNo(docRowNo);
		doc.setSubject(attachTitle);
		doc.setDocumentType(attachCategory);
		doc.setDocTypeName(docTypeServ.searchDocType(attachCategory).getName());
		doc.setFileName(attachFile.getOriginalFilename());
		doc.setDocRecStatus("new");
		try {
			doc.setContentObj(attachFile.getBytes());
		} catch (IOException e) {
			e.printStackTrace();
		}
		doc.setCreatedBy(request.getRemoteUser());

		attachTable.add(doc);
		sessionModel.put("attachItems", attachTable);
		setAttachButton(false, true, true, sessionModel);
		clearAttachForm(sessionModel);

		return new ModelAndView("/main/material/rawMatlForm", sessionModel);
	}

	@PostMapping(value = "/main/material/rawMatlForm", params = "attachAction=update")
	public ModelAndView updateToDocList(@RequestParam(name = "attachRow") int attachRow,
			@RequestParam(name = "attachTitle") String attachTitle,
			@RequestParam(name = "attachCategory") int attachCategory,
			@RequestParam(name = "attachFile") MultipartFile attachFile, HttpServletRequest request,
			RawMaterial rawMatl, BindingResult result, HttpSession session) {

		sessionModel.put("currTab", "attachment");
		sessionModel.put("rawMatlItems", rawMatl);
		List<Document> attachTable = (List<Document>) sessionModel.get("attachItems");

		if (errorCheckAttachment(attachTitle, attachCategory, attachFile, attachTable, 2, sessionModel)) {
			holdAttachValue(attachTitle, attachCategory, sessionModel);
			return new ModelAndView("/main/material/rawMatlForm", sessionModel);
		}

		for (Document doc : attachTable) {
			if (doc.getDocRowNo() == attachRow) {
				doc.setSubject(attachTitle);
				doc.setDocumentType(attachCategory);
				doc.setDocTypeName(docTypeServ.searchDocType(attachCategory).getName());

				if (!attachFile.getOriginalFilename().equals("")) {
					doc.setFileName(attachFile.getOriginalFilename());
					try {
						doc.setContentObj(attachFile.getBytes());
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
		}

		sessionModel.put("attachItems", attachTable);
		setAttachButton(false, true, true, sessionModel);
		clearAttachForm(sessionModel);

		return new ModelAndView("/main/material/rawMatlForm", sessionModel);
	}

	@PostMapping(value = "/main/material/rawMatlForm", params = "attachAction=delete")
	public ModelAndView deleteAttachList(HttpServletRequest request, @RequestParam(name = "attachRow") int attachRow,
			RawMaterial rawMatl, BindingResult result, HttpSession session) {

		sessionModel.put("rawMatlItems", rawMatl);
		sessionModel.remove("error");
		List<Document> attachTable = (List<Document>) sessionModel.get("attachItems");

		try {
			List<Document> docDel = (List<Document>) sessionModel.get("docDel");

			docDel.add(attachTable.get(attachRow));
			attachTable.remove(attachRow);

			int reCalRowNo = 0;
			for (int i = 0; i < attachTable.size(); i++) {
				attachTable.get(i).setDocRowNo(reCalRowNo);
				reCalRowNo++;
			}
		} catch (Exception e) {
			sessionModel.put("attachItems", attachTable);
		}

		sessionModel.put("attachItems", attachTable);
		setAttachButton(false, true, true, sessionModel);
		sessionModel.put("currTab", "attachment");
		clearAttachForm(sessionModel);

		return new ModelAndView("/main/material/rawMatlForm", sessionModel);
	}

	@GetMapping("/main/material/rawMatlFormDocGetData")
	public ModelAndView retrieveDocTableData(HttpServletRequest request, @RequestParam(name = "id") int id,
			HttpSession session) {

		String screenMode = sessionModel.get("screenMode").toString();

		sessionModel.remove("error");
		List<Document> attachTable = (List<Document>) sessionModel.get("attachItems");

		for (Document doc : attachTable) {
			if (doc.getDocRowNo() == id) {
				sessionModel.put("attachRow", doc.getDocRowNo());
				sessionModel.put("attachTitle", doc.getSubject());
				sessionModel.put("attachCategory", doc.getDocumentType());
				sessionModel.put("attachFileName", doc.getFileName());
			}
		}

		sessionModel.put("currTab", "attachment");
		if (!screenMode.equals(CommonConstants.SCREEN_MODE_VIEW)) {
			setAttachButton(false, false, false, sessionModel);
		}

		return new ModelAndView("/main/material/rawMatlForm", sessionModel);
	}

	private boolean errorCheckAttachment(String attachTitle, int attachCategory, MultipartFile attachFile,
			List<Document> attachTable, int check, Map<String, Object> sessionModel) {

		sessionModel.remove("error");

		String errorMsg = "";
		errorMsg += commonValidatorServ.validateMandatoryInput(attachTitle, "Attachment Title");
		errorMsg += commonValidatorServ.validateInputLength(attachTitle, 50, "Attachment Title");
		errorMsg += commonValidatorServ.validateMandatorySelect(attachCategory, "Attachment Category");
		errorMsg += commonValidatorServ.validateFileSize(attachFile.getSize(), "Attachment File");

		if (check == 1) {
			if (attachFile.getOriginalFilename().equals("")) {
				errorMsg += "Attachment File is mandatory.<br />";
			}
		}

		if (errorMsg.length() != 0) {
			sessionModel.put("error", errorMsg);
			return true;
		}

		return false;
	}

	private Map<String, Object> setAttachButton(boolean regAdd, boolean regUpdate, boolean regDelete,
			Map<String, Object> sessionModel) {

		sessionModel.put("attachAdd", regAdd);
		sessionModel.put("attachUpdate", regUpdate);
		sessionModel.put("attachDelete", regDelete);

		return sessionModel;
	}

	private void holdAttachValue(String attachTitle, int attachCategory, Map<String, Object> sessionModel) {

		sessionModel.put("attachTitle", attachTitle);
		sessionModel.put("attachCategory", attachCategory);
	}

	private void clearAttachForm(Map<String, Object> sessionModel) {

		sessionModel.remove("attachTitle");
		sessionModel.remove("attachCategory");
	}

	@GetMapping("/main/material/rawMatlFormDocDwn")
	public ResponseEntity<byte[]> downloadDocumentFile(HttpServletRequest request, @RequestParam(name = "id") int docId,
			HttpSession session) {

		List<Document> attachTable = (List<Document>) sessionModel.get("attachItems");
		String fileName = "";
		byte[] content = new byte[1024];

		for (Document doc : attachTable) {
			if (doc.getDocRowNo() == docId) {
				fileName = doc.getFileName();
				content = doc.getContentObj();
			}
		}

		MediaType mt = MediaTypeUtils.getMediaTypeForFileName(servletContext, fileName);

		return ResponseEntity.ok().contentType(mt)
				.header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + fileName + "\"").body(content);
	}

	private void addTrxHis(Date modifyDate, String userId, String rawMatlName, int matlStatus,
			Map<String, Object> sessionModel, int rawMatlId, String remark) {

		String screenMode = sessionModel.get("screenMode").toString();
		String logDesc = "";
		int funcType = 0;

		if (screenMode.equals(CommonConstants.SCREEN_MODE_ADD)) {

			if (checkLogDesc(matlStatus).equals("") || checkLogDesc(matlStatus) == null) {
				logDesc = "Insert Raw Material";
			} else {
				logDesc = checkLogDesc(matlStatus);
			}
			funcType = CommonConstants.FUNCTION_TYPE_INSERT;

		} else if (screenMode.equals(CommonConstants.SCREEN_MODE_EDIT)) {

			if (checkLogDesc(matlStatus).equals("") || checkLogDesc(matlStatus) == null) {
				logDesc = "Update Raw Material";
			} else {
				logDesc = checkLogDesc(matlStatus);
			}
			funcType = CommonConstants.FUNCTION_TYPE_UPDATE;
		} else {
			logDesc = checkLogDesc(matlStatus);
			funcType = CommonConstants.FUNCTION_TYPE_UPDATE;
		}
		
		if (rawMatlId == 0) {
			rawMatlId = rmServ.searchRawMaterial(rawMatlName, String.valueOf(CommonConstants.SEARCH_OPTION_EXACT), 0, 0)
					.get(0).getRawMatlId();
		}

//		trxHistServ.addTrxHistory(modifyDate, logDesc, userId, funcType, String.valueOf(rawMatlId), CommonConstants.RECORD_TYPE_ID_RAW_MATL,
//				null);
		
		trxHistServ.saveTrxHistoryLog(modifyDate, logDesc, userId, funcType, String.valueOf(rawMatlId), CommonConstants.RECORD_TYPE_ID_RAW_MATL, remark);
	}

	private String checkLogDesc(int matlStatus) {

		String logDesc = "";

		if (matlStatus == RecordStatusEnum.PEND_AUTH.intValue()
				|| matlStatus == RecordStatusEnum.CHG_PEND_AUTH.intValue()
				|| matlStatus == RecordStatusEnum.CHG_REWORK.intValue()) {
			logDesc = "Submit Raw Material for Approval";
		} else if (matlStatus == RecordStatusEnum.SUBMITTED.intValue()) {
			logDesc = "Submit Raw Material";
		} else if (matlStatus == RecordStatusEnum.REJECTED.intValue()) {
			logDesc = "Reject Raw Material";
		} else if (matlStatus == RecordStatusEnum.REWORK.intValue()) {
			logDesc = "Rework Raw Material";
		}

		return logDesc;
	}

	/*
	 * Save function
	 */
	@PostMapping(value = "/main/material/rawMatlForm", params = "action=save")
	public ModelAndView saveMatl(RawMaterial rawMatl, BindingResult result, HttpServletRequest request,
			HttpSession session) {

		Date modifyDate = new Date();
		int matlStatus = (int) sessionModel.get("matlStatus");
		String action = "saveForm";

		if (!saveMatlData(rawMatl.getRawMatlName(), rawMatl.getInsNo(), rawMatl.getENo(), rawMatl.getFemaNo(),
				rawMatl.getJecfaNo(), rawMatl.getCasNo(), rawMatl.getGmoStatus(), rawMatl.getFlavStatusId(),
				rawMatl.getPhoFlag(), rawMatl.getRemarks(), request, matlStatus, modifyDate, action, sessionModel)) {
			sessionModel.put("rawMatlItems", rawMatl);
			return new ModelAndView("/main/material/rawMatlForm", sessionModel);
		}

		addTrxHis(modifyDate, request.getRemoteUser(), rawMatl.getRawMatlName(), matlStatus, sessionModel, 0, null);

		return new ModelAndView("/main/material/rawMatlList", sessionModel);
	}

	@RequestMapping(value = "/main/material/rawMatlFormCheck", method = RequestMethod.GET)
	@ResponseBody
	public Map<String, Object> checkLinkageBetweenRawMatlPR(@RequestParam(name = "id") int id,
			@RequestParam(name = "type", required = false) String type, HttpSession session) {

		Map<String, Object> linkedObject = new HashMap<String, Object>();
		
		if(StringUtils.isNotEmpty(type) && StringUtils.equals(type, "deactivate")) {
			checkLinkageRule2(id, linkedObject);
			return linkedObject;
		}
		
		//vipd-final status
		//get list of PR-RM-Status
		checkLinkageRule1(id, linkedObject);
		
		//general info
		//get list of PR
		checkLinkageRule2(id, linkedObject);

		return linkedObject;
	}

	private void checkLinkageRule1(int rawMatlId, Map<String, Object> linkedObject) {
		linkedObject.put("linkedPrRmStats", 0);
		boolean regChange = (boolean) sessionModel.get("regChange");
		List<String> effRegCountries = (List<String>) sessionModel.get("effRegCountries");

		if (regChange && (effRegCountries != null && !effRegCountries.isEmpty())) {
			// normal scenario: maker/checker do reg change in ui
			List<PrRmStat> prRmStats = prRmStatServ.searchProductLinkage(RecordStatusEnum.SUBMITTED.intValue(),
					rawMatlId, effRegCountries, "", "");
			sessionModel.put("linkedPrRmStats", prRmStats);

			logger.debug("checkLinkageRule1() rawMatlId={}, effRegCountries.size={}, prRmStats.size={}", rawMatlId,
					effRegCountries.size(), prRmStats.size());

			if (!prRmStats.isEmpty()) {
				linkedObject.put("linkedPrRmStats", prRmStats);
			} else {
				linkedObject.put("linkedPrRmStats", 0);
			}

		} else {
			// lookup from db
			// to cater scenario checker submit, but did not do reg change
			List<String> countryIds = refCountryServ
					.findByCriteria("", "", "", "", String.valueOf(CommonConstants.FLAG_YES), "").stream()
					.map(arg0 -> arg0.getCountryId()).collect(Collectors.toList());

			List<PrRmStat> prRmStats = prRmStatServ.searchProductLinkage(RecordStatusEnum.SUBMITTED.intValue(),
					rawMatlId, countryIds, String.valueOf(CommonConstants.FLAG_YES), "");
			sessionModel.put("linkedPrRmStats", prRmStats);

			logger.debug("checkLinkageRule1() lookup: rawMatlId={}, countryIds.size={}, prRmStats.size={}", rawMatlId,
					countryIds.size(), prRmStats.size());

			if (!prRmStats.isEmpty()) {
				linkedObject.put("linkedPrRmStats", prRmStats);
			} else {
				linkedObject.put("linkedPrRmStats", 0);
			}
		}
	}
	
	private void checkLinkageRule2(int rawMatlId, Map<String, Object> linkedObject) {
		// maker amend completed record
		linkedObject.put("linkedPr", 0);
		boolean authLvl2 = (Boolean) sessionModel.get("authLvl2");
		int currMatlStatus = (int) sessionModel.get("matlStatus");

		if (!authLvl2 && currMatlStatus != RecordStatusEnum.SUBMITTED.intValue())
			return;

		//Filter the list of effected PR by RM, record_status!=REJECTED
		List<ProductRecipe> prods = prRmStatServ.searchProductLinkageAll(0, rawMatlId, "", "").stream()
				.filter(pr -> pr.getRecordStatus() != CommonConstants.RECORD_STATUS_REJECTED)
				.collect(Collectors.toList());

		for (ProductRecipe productRecipe : prods) {
			productRecipe.setProdRecipeRecStatus(getRecordStatusDesc(productRecipe.getRecordStatus()));
		}
		
		sessionModel.put("linkedPr", prods);

		logger.debug("checkLinkageRule2() rawMatlId={}, prods.size={}", rawMatlId, prods.size());

		if (!prods.isEmpty()) {
			linkedObject.put("linkedPr", prods);
		} else {
			linkedObject.put("linkedPr", 0);
		}
	}

	/*
	 * Submit/Approve function
	 */
	@PostMapping(value = "/main/material/rawMatlForm", params = "action=submit")
	public ModelAndView submitMatl(@Valid RawMaterial rawMatl, BindingResult result, HttpServletRequest request,
			HttpSession session,
			@RequestParam(name = "submitConfirmRemark1", required = false) String submitConfirmRemark1,
			@RequestParam(name = "submitConfirmRemark2", required = false) String submitConfirmRemark2,
			@RequestParam(name = "hidFlavStatusId", required = false) Integer hidFlavStatusId) {
		
		String screenMode = sessionModel.get("screenMode").toString();
		String remarks = "";
		
		logger.debug("submitMatl() screenMode={}, submitConfirmRemark={}, submitConfirmRemark2={}", screenMode,
				submitConfirmRemark1, submitConfirmRemark2, hidFlavStatusId);
		
		if(StringUtils.isNotEmpty(submitConfirmRemark1))
			remarks = submitConfirmRemark1;
		
		if(StringUtils.isNotEmpty(submitConfirmRemark2))
			remarks = submitConfirmRemark2;
		
		boolean regChange = false;
		try {
			regChange = (boolean) sessionModel.get("regChange");
		} catch (Exception e) {
		}

		int nextStatus = 0;
		boolean authLvl1 = false;
		boolean authLvl2 = false;
		String rawMatlName = rawMatl.getRawMatlName();
		String action = "submitForm";
		int currMatlStatus = (int) sessionModel.get("matlStatus");

		List<UsrRole> usrRoleList = usrRoleServ.searchUserRole(request.getRemoteUser());
		for (UsrRole usrRole : usrRoleList) {
			if (usrRole.getRoleId().equals(CommonConstants.ROLE_ID_CHECKER)
					|| usrRole.getRoleId().equals(CommonConstants.ROLE_ID_SUPERUSER)
					|| usrRole.getRoleId().equals(CommonConstants.ROLE_ID_HOD)) {
				authLvl2 = true;
			}

			if (usrRole.getRoleId().equals(CommonConstants.ROLE_ID_MAKER)) {
				authLvl1 = true;
			}
		}
		
		if (!StringUtils.equals(screenMode, CommonConstants.SCREEN_MODE_ADD)) {
			if(StringUtils.isEmpty(remarks)) {
				sessionModel.put("error",
						"Submit Remarks is mandatory as it will be used for tracing reason of change/update");
				sessionModel.remove("success");
				return new ModelAndView("/main/material/rawMatlForm", sessionModel);				
			}
		} else {
			remarks = "Initial creation";
		}

		nextStatus = getNextStatus(nextStatus, authLvl1, authLvl2, currMatlStatus);

		Date modifyDate = new Date();

		if (!screenMode.equals(CommonConstants.SCREEN_MODE_VIEW)) {
			if (Objects.nonNull(hidFlavStatusId))
				rawMatl.setFlavStatusId(hidFlavStatusId);
			
			if (!saveMatlData(rawMatlName, rawMatl.getInsNo(), rawMatl.getENo(), rawMatl.getFemaNo(),
					rawMatl.getJecfaNo(), rawMatl.getCasNo(), rawMatl.getGmoStatus(), rawMatl.getFlavStatusId(),
					rawMatl.getPhoFlag(), rawMatl.getRemarks(), request, nextStatus, modifyDate, action,
					sessionModel)) {
				return new ModelAndView("/main/material/rawMatlForm", sessionModel);
			}
		} else {

			rawMatlName = (rawMatl.getRawMatlName() == null || rawMatl.getRawMatlName().isEmpty())
					? (String) sessionModel.get("rawMatlName")
							: rawMatl.getRawMatlName();
					
			List<RmStat> rmStatTable = (List<RmStat>) sessionModel.get("regItems");
			List<RmIngredient> rmIngTable = (List<RmIngredient>) sessionModel.get("ingItems");
			int rawMatlId = (int) sessionModel.get("rawMatlId");
			String errorMsg = "";
			errorMsg = checkTableData(errorMsg, rmStatTable, rmIngTable);

			if (errorMsg.length() != 0) {
				sessionModel.put("error", errorMsg);
				sessionModel.remove("success");
				return new ModelAndView("/main/material/rawMatlForm", sessionModel);
			}

			Integer currentLock = (Integer) sessionModel.get("currentOptlock");
			try {
				rmServ.updRawMatlStatusWithOptLock(rawMatlId, nextStatus, request.getRemoteUser(), modifyDate,
						currentLock, null);
				retrieveMatlData("", "", "", "", "", "", "", "", sessionModel, request.getRemoteUser(), 0);
			} catch (Exception e) {
				logger.error("submitMatl() Error!", e);
				e.printStackTrace();

				errorMsg = "Submit Raw material " + rawMatlName + " was unsuccessful.";

				if (e instanceof CommonException) {
					if (e.getMessage().contains("Concurrent")) {
						errorMsg = errorMsg.concat(
								" Record could have been updated by another user.<br /> Kindly refresh the listing and view back the record.");
					}
				}

				sessionModel.put("error", errorMsg);
				sessionModel.remove("success");
				return new ModelAndView("/main/material/rawMatlForm", sessionModel);
			}
		}

		RawMaterial obj = rmServ.searchRawMaterial(rawMatlName, String.valueOf(CommonConstants.SEARCH_OPTION_EXACT), 0, 0)
				.get(0);
		int rawMatlId = obj.getRawMatlId();
		addTrxHis(modifyDate, request.getRemoteUser(), rawMatlName, nextStatus, sessionModel, rawMatlId, remarks);

		submitCheckEffectedPrRmStatList(request, modifyDate, rawMatlId, remarks, authLvl1, authLvl2,
				regChange, currMatlStatus);
		
		//maker edit completed status record and submit to checker
		if(authLvl1 && currMatlStatus == RecordStatusEnum.SUBMITTED.intValue()) {
			//Change RM the record status from Completed to Changed-Pending-authorization
			//Affected PR: Status Completed, add blocked flag set to yes
			//System send  task and notification to Checker
			//20211025 - FIX: Only update prd on-hold when it's impacted to RM Final Status
			//submitCheckEffectedPrListForBlock(request, modifyDate, rawMatlId, remarks);			
			taskCreationServ.taskCreationMatl(rawMatlId, rawMatlName, request.getRemoteUser(), nextStatus, "", screenMode, modifyDate);
		}

		String successMsg = "";
		if (nextStatus == RecordStatusEnum.PEND_AUTH.intValue()
				|| nextStatus == RecordStatusEnum.CHG_PEND_AUTH.intValue()
				|| nextStatus == RecordStatusEnum.REWORK_PEND_AUTH.intValue()) {
			successMsg = "Raw Material " + rawMatlName + " has been submitted for approval.";
		}

		if (nextStatus == RecordStatusEnum.SUBMITTED.intValue()) {
			successMsg = "Raw Material " + rawMatlName + " has been submitted.";
			
			// create record in amend flavor status table
			createAmendFlavStatus(request.getRemoteUser(), obj);
			
		}

		sessionModel.put("success", successMsg);
		sessionModel.remove("error");

		if (currMatlStatus != RecordStatusEnum.SUBMITTED.intValue()) {
			taskCreationServ.taskCreationMatl(rawMatlId, rawMatlName, request.getRemoteUser(), nextStatus, "", screenMode, modifyDate);
		}

		return new ModelAndView("/main/material/rawMatlList", sessionModel);
	}

	protected void createAmendFlavStatus(String userId, RawMaterial obj) {
		FsRmUpdDto fsUpdDto = fsUpdServ.findOneByRmId(obj.getRawMatlId());
		if (Objects.isNull(fsUpdDto)) {
			fsUpdServ.saveFsRmUpd(null, obj.getFlavStatusId(), null, userId, null,
					RecordStatusEnum.SUBMITTED.intValue(), userId, obj.getRawMatlId());
		}
	}

	private int getNextStatus(int nextStatus, boolean authLvl1, boolean authLvl2, int currMatlStatus) {
		if (authLvl2) {
			nextStatus = RecordStatusEnum.SUBMITTED.intValue();
		} else {
			if (authLvl1) {
				if (currMatlStatus == RecordStatusEnum.SUBMITTED.intValue()) {
					nextStatus = RecordStatusEnum.CHG_PEND_AUTH.intValue();
				} else if (currMatlStatus == RecordStatusEnum.REWORK.intValue()) {
					nextStatus = RecordStatusEnum.REWORK_PEND_AUTH.intValue();
				} else {
					nextStatus = RecordStatusEnum.PEND_AUTH.intValue();
				}
			}
		}
		return nextStatus;
	}

	private void submitCheckEffectedPrRmStatList(HttpServletRequest request, Date modifyDate, int rawMatlId,
			String remarks, boolean authLvl1, boolean authLvl2, boolean regChange, int currMatlStatus) {
		logger.debug(
				"submitCheckEffectedPrRmStatList() For affected products having rmId={},authLvl1={},authLvl2={},regChange={},currMatlStatus={}",
				rawMatlId, authLvl1, authLvl2, regChange, currMatlStatus);

		List<RmStat> regTable = (List<RmStat>) sessionModel.get("regItems");// from ui
		// from modal popup onsubmit: on the fly and db lookup
		List<PrRmStat> impactedPrRmStats = (List<PrRmStat>) sessionModel.get("linkedPrRmStats");

		if (regChange) { // when directly change in ui
			regTable = (List<RmStat>) sessionModel.get("regItems");// from ui
		} else {
			// check status
			if (currMatlStatus == RecordStatusEnum.CHG_PEND_AUTH.intValue()) {
				// take from db
				regTable = rmStatServ.searchRmStat(rawMatlId);
			}
		}		
		
		if (authLvl1 && currMatlStatus == RecordStatusEnum.SUBMITTED.intValue()) {
			if (impactedPrRmStats == null || impactedPrRmStats.isEmpty())
				return;

			List<Integer> pIds = new ArrayList<Integer>();
			updatePrRmStatAffectedFlag(request, modifyDate, rawMatlId, regTable, impactedPrRmStats, pIds);
		}

		if (authLvl2 && (currMatlStatus == RecordStatusEnum.SUBMITTED.intValue()
				|| currMatlStatus == RecordStatusEnum.CHG_PEND_AUTH.intValue()
				|| currMatlStatus == RecordStatusEnum.REWORK_PEND_AUTH.intValue())) {
			// update back rm stats data change flag to 'N'
			rmStatServ.updRmStatDataChgFlag(0, "", rawMatlId, String.valueOf(CommonConstants.FLAG_YES),
					String.valueOf(CommonConstants.FLAG_NO));
			
			if (impactedPrRmStats == null || impactedPrRmStats.isEmpty())
				return;
			
			// checker edit for complete status
			// checker approve chg-pend-auth
			List<Integer> pIds = new ArrayList<Integer>();
			updatePrRmStatAffectedFlag(request, modifyDate, rawMatlId, regTable, impactedPrRmStats, pIds);

			//checker edit n submit complete status
			//checker approve chg-pend-auth
			//if (authLvl2 && (currMatlStatus == RecordStatusEnum.SUBMITTED.intValue()
			//		|| currMatlStatus == RecordStatusEnum.CHG_PEND_AUTH.intValue())) {
				updateAffectedPrStatus(request, modifyDate, remarks, pIds, rawMatlId);				
				
			//}
		}

	}

	private void updateAffectedPrStatus(HttpServletRequest request, Date modifyDate, String remarks, List<Integer> pIds, int rawMatlId) {
		pIds = pIds.stream().distinct().collect(Collectors.toList());
		SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy hh:mm:ss a"); 
		for (Integer idx : pIds) {
			logger.debug("updateAffectedPrStatus() Updating PR status and Task>> prId={}", idx);

			ProductRecipe prdRcp = prdRcpServ.searchProductRecipe("", "", "", "", idx, 0).get(0);
			try {
				prdRcpServ.updProductRecipeStatusWithOptLock(prdRcp.getPrId(),
						RecordStatusEnum.CHG_PEND_AUTH.intValue(), request.getRemoteUser(), modifyDate,
						prdRcp.getOptLock(), null);
				String logDesc = "Product recipe status changed to '"
						+ msgSource.getMessage("vrStatus_" + RecordStatusEnum.CHG_PEND_AUTH.intValue(), null,
								Locale.getDefault())
						+ "' due to Raw material Final Status updated.";
				trxHistServ.saveTrxHistoryLog(modifyDate, logDesc, request.getRemoteUser(),
						CommonConstants.FUNCTION_TYPE_UPDATE, String.valueOf(prdRcp.getPrId()),
						CommonConstants.RECORD_TYPE_ID_PROD_RECP, remarks);
				taskCreationServ.taskCreationPrd(prdRcp.getPrId(), prdRcp.getPrName(), request.getRemoteUser(),
						RecordStatusEnum.CHG_PEND_AUTH.intValue(), "", "byMatl:pr="+prdRcp.getPrId()+":rm="+rawMatlId+":dt="+formatter.format(modifyDate), modifyDate);
			} catch (Exception e) {
				logger.error("updateAffectedPrStatus() Exception!", e);
				continue;
			}		
		}
	}

	private void updatePrRmStatAffectedFlag(HttpServletRequest request, Date modifyDate, int rawMatlId,
			List<RmStat> regTable, List<PrRmStat> impactedPrRmStats, List<Integer> pIds) {
		for (PrRmStat prRmStats : impactedPrRmStats) {
			Integer prId = prRmStats.getPrId();
			
			if(pIds != null)
				pIds.add(prId);
			
			for (RmStat rmStat : regTable) {
				if (rmStat.getDataChange() != null
						&& rmStat.getDataChange().equals(String.valueOf(CommonConstants.FLAG_YES))) {
					logger.debug("updatePrRmStatAffectedFlag() Updating PrRmStat>> prId={}, rmId={}, ctyId={}",
							prId, rawMatlId, rmStat.getCountryId());

					// update pr-rm-stat final status
					prRmStatServ.updPrRmStatByMatl(rmStat.getFinalStatus(), request.getRemoteUser(), modifyDate,
							prId, rawMatlId, rmStat.getCountryId());
					// update pr-rm-stat affected flag
					prRmStatServ.updPrRmStatFlag(String.valueOf(CommonConstants.FLAG_YES), request.getRemoteUser(),
							modifyDate, prId, rawMatlId, rmStat.getCountryId(), 0);
				}
			}
		}
		
		{
			//20211025 - FIX: Only update PR on-hold when it's impacted to RM Final Status
			List<Integer> pids = pIds.stream().distinct().collect(Collectors.toList());
			for (Integer id : pids) {
				prdRcpServ.updatePrOnHoldFlag(id, String.valueOf(CommonConstants.FLAG_YES),
						request.getRemoteUser(), modifyDate, null);				
			}
		}
	}

	@Deprecated
	//20211025 - FIX: Only update PR on-hold when it's impacted to RM Final Status
	private void submitCheckEffectedPrListForBlock(HttpServletRequest request, Date modifyDate, int rawMatlId,
			String remarks) {

		List<ProductRecipe> impactedPr = (List<ProductRecipe>) sessionModel.get("linkedPr");

		if (impactedPr == null || impactedPr.isEmpty())
			return;

		// update product_recipe onhold_flag=Y
		for (ProductRecipe prdRcp : impactedPr) {
			prdRcpServ.updatePrOnHoldFlag(prdRcp.getPrId(), String.valueOf(CommonConstants.FLAG_YES),
					request.getRemoteUser(), modifyDate, remarks);
			String logDesc = "On Hold/Block Product Recipe due to Raw Material update.";
			trxHistServ.saveTrxHistoryLog(modifyDate, logDesc, request.getRemoteUser(),
					CommonConstants.FUNCTION_TYPE_UPDATE, String.valueOf(prdRcp.getPrId()),
					CommonConstants.RECORD_TYPE_ID_PROD_RECP, remarks);
		}
	}

	/*
	 * Reject function
	 */
	@PostMapping(value = "/main/material/rawMatlForm", params = "action=reject")
	public ModelAndView rejectMatl(HttpServletRequest request, RawMaterial rawMatl, BindingResult result,
			@RequestParam(name = "rejectRemark") String rejectRemark, HttpSession session) {
		
		String rmName = (rawMatl.getRawMatlName() == null || rawMatl.getRawMatlName().isEmpty())
				? (String) sessionModel.get("rawMatlName")
						: rawMatl.getRawMatlName();
		
		if(StringUtils.isEmpty(rejectRemark)) {
			sessionModel.put("error", "Reject Remarks is mandatory as it will be used for tracing reason of change/update");
			sessionModel.remove("success");
			return new ModelAndView("/main/material/rawMatlForm", sessionModel);
		}

		String screenMode = sessionModel.get("screenMode").toString();

		int rawMatlId = (int) sessionModel.get("rawMatlId");
		Date modifyDate = new Date();

		Integer currentLock = (Integer) sessionModel.get("currentOptlock");
		try {
			rmServ.updRawMatlStatusWithOptLock(rawMatlId, RecordStatusEnum.REJECTED.intValue(),
					request.getRemoteUser(), modifyDate, currentLock, null);
			
			revertValues(request, rawMatlId, modifyDate);
			
			addTrxHis(modifyDate, request.getRemoteUser(), rmName, RecordStatusEnum.REJECTED.intValue(),
					sessionModel, rawMatlId, rejectRemark);
			taskCreationServ.taskCreationMatl(rawMatlId, rmName, request.getRemoteUser(),
					RecordStatusEnum.REJECTED.intValue(), rejectRemark, screenMode, modifyDate);
			

			sessionModel.put("success", "Raw material " + rmName + " has been rejected.");
			sessionModel.remove("error");
			
		} catch (Exception e) {
			logger.error("rejectMatl() Error!", e);
			e.printStackTrace();

			String errorMsg = "Reject Raw material " + rmName + " was unsuccessful.";

			if (e instanceof CommonException) {
				if (e.getMessage().contains("Concurrent")) {
					errorMsg = errorMsg.concat(
							" Record could have been updated by another user.<br /> Kindly refresh the listing and view back the record.");
				}
			}

			sessionModel.put("error", errorMsg);
			sessionModel.remove("success");
		}

		retrieveMatlData("", "", "", "", "", "", "", "", sessionModel, request.getRemoteUser(), 0);
		return new ModelAndView("/main/material/rawMatlList", sessionModel);
	}

	private void revertValues(HttpServletRequest request, int rawMatlId, Date modifyDate) {
		//revert pr-rm-stat afft flag, pr onhold flag, rm-stat chg flag if any
		
		List<PrRmStat> prRmStats = prRmStatServ.searchPrRmStat(rawMatlId, String.valueOf(CommonConstants.FLAG_YES), "");
		for (PrRmStat prm : prRmStats) {
			prRmStatServ.updPrRmStatFlag(String.valueOf(CommonConstants.FLAG_NO), request.getRemoteUser(), modifyDate,
					prm.getPrId(), rawMatlId, "", prm.getPrRmStatId());
		}
		
		List<ProductRecipe> prods = prRmStatServ.searchProductLinkage2(RecordStatusEnum.SUBMITTED.intValue(), rawMatlId,
				"", String.valueOf(CommonConstants.FLAG_YES));
		for (ProductRecipe prdRcp : prods) {
			prdRcpServ.updatePrOnHoldFlag(prdRcp.getPrId(), String.valueOf(CommonConstants.FLAG_NO),
					request.getRemoteUser(), modifyDate, "Modification of RM in RM Module rejected.");
		}

		rmStatServ.updRmStatDataChgFlag(0, "", rawMatlId, String.valueOf(CommonConstants.FLAG_YES),
				String.valueOf(CommonConstants.FLAG_NO));
	}

	/*
	 * Rework function
	 */
	@PostMapping(value = "/main/material/rawMatlForm", params = "action=rework")
	public ModelAndView reworkMatl(HttpServletRequest request, @Valid RawMaterial rawMatl, BindingResult result,
			@RequestParam(name = "reworkRemark") String reworkRemark, HttpSession session) {
		
		String rmName = (rawMatl.getRawMatlName() == null || rawMatl.getRawMatlName().isEmpty())
				? (String) sessionModel.get("rawMatlName")
						: rawMatl.getRawMatlName();
		
		if(StringUtils.isEmpty(reworkRemark)) {
			sessionModel.put("error", "Rework Remarks is mandatory as it will be used for tracing reason of change/update");
			sessionModel.remove("success");
			return new ModelAndView("/main/material/rawMatlForm", sessionModel);
		}
		
		String screenMode = sessionModel.get("screenMode").toString();

		int rawMatlId = (int) sessionModel.get("rawMatlId");
		Date modifyDate = new Date();

		Integer currentLock = (Integer) sessionModel.get("currentOptlock");
		try {
			rmServ.updRawMatlStatusWithOptLock(rawMatlId, RecordStatusEnum.REWORK.intValue(), request.getRemoteUser(),
					modifyDate, currentLock, null);
			addTrxHis(modifyDate, request.getRemoteUser(), rmName, RecordStatusEnum.REWORK.intValue(),
					sessionModel, rawMatlId, reworkRemark);
			taskCreationServ.taskCreationMatl(rawMatlId, rmName, request.getRemoteUser(),
					RecordStatusEnum.REWORK.intValue(), reworkRemark, screenMode, modifyDate);
			
			
			sessionModel.put("success",
					"Raw material " + rmName + " has been revert to creator for rework.");
			sessionModel.remove("error");

		} catch (Exception e) {
			logger.error("reworkMatl() Error!", e);
			e.printStackTrace();

			String errorMsg = "Revert Raw material " + rmName + " to creator was unsuccessful.";

			if (e instanceof CommonException) {
				if (e.getMessage().contains("Concurrent")) {
					errorMsg = errorMsg.concat(
							" Record could have been updated by another user.<br /> Kindly refresh the listing and view back the record.");
				}
			}

			sessionModel.put("error", errorMsg);
			sessionModel.remove("success");
		}

		retrieveMatlData("", "", "", "", "", "", "", "", sessionModel, request.getRemoteUser(), 0);
		return new ModelAndView("/main/material/rawMatlList", sessionModel);
	}
	
	/*
	 * Deactivate function
	 */
	@PostMapping(value = "/main/material/rawMatlForm", params = "action=deactivate")
	public ModelAndView deactivateMatl(HttpServletRequest request, RawMaterial rawMatl, BindingResult result,
			@RequestParam(name = "deactivateRemark") String deactivateRemark, HttpSession session) {
		
		String rmName = (rawMatl.getRawMatlName() == null || rawMatl.getRawMatlName().isEmpty())
				? (String) sessionModel.get("rawMatlName")
						: rawMatl.getRawMatlName();
		
		if(StringUtils.isEmpty(deactivateRemark)) {
			sessionModel.put("error", "Deactivate Remarks is mandatory as it will be used for tracing reason of change/update");
			sessionModel.remove("success");
			return new ModelAndView("/main/material/rawMatlForm", sessionModel);
		}
		
		String screenMode = sessionModel.get("screenMode").toString();
		int rawMatlId = (int) sessionModel.get("rawMatlId");
		Date modifyDate = new Date();
		int nextStatus = RecordStatusEnum.PEND_DEACTIVATE.intValue();
		boolean authLvl2 = (Boolean)sessionModel.get("authLvl2");
		
		if(authLvl2) {
			nextStatus = RecordStatusEnum.DEACTIVATED.intValue();
		}

		Integer currentLock = (Integer) sessionModel.get("currentOptlock");
		try {
			rmServ.updRawMatlStatusWithOptLock(rawMatlId, nextStatus, request.getRemoteUser(),
					modifyDate, currentLock, (authLvl2?null:deactivateRemark));
			addTrxHis(modifyDate, request.getRemoteUser(), rmName, nextStatus,
					sessionModel, rawMatlId, deactivateRemark);
			taskCreationServ.taskCreationMatl(rawMatlId, rmName, request.getRemoteUser(),
					nextStatus, deactivateRemark, screenMode, modifyDate);
			
			
			sessionModel.put("success",
					"Raw material " + rmName + " has been submitted to for deactivate.");
			sessionModel.remove("error");

		} catch (Exception e) {
			logger.error("deactivateMatl() Error!", e);
			e.printStackTrace();

			String errorMsg = "Deactivate Raw material " + rmName + " was unsuccessful.";

			if (e instanceof CommonException) {
				if (e.getMessage().contains("Concurrent")) {
					errorMsg = errorMsg.concat(
							" Record could have been updated by another user.<br /> Kindly refresh the listing and view back the record.");
				}
			}

			sessionModel.put("error", errorMsg);
			sessionModel.remove("success");
		}
		
		retrieveMatlData("", "", "", "", "", "", "", "", sessionModel, request.getRemoteUser(), 0);
		return new ModelAndView("/main/material/rawMatlList", sessionModel);
	}
	
	/*
	 * Activate function
	 */
	@PostMapping(value = "/main/material/rawMatlForm", params = "action=activate")
	public ModelAndView activateMatl(HttpServletRequest request, RawMaterial rawMatl, BindingResult result,
			@RequestParam(name = "activateRemark") String activateRemark, HttpSession session) {
		
		String rmName = (rawMatl.getRawMatlName() == null || rawMatl.getRawMatlName().isEmpty())
				? (String) sessionModel.get("rawMatlName")
						: rawMatl.getRawMatlName();
		
		if(StringUtils.isEmpty(activateRemark)) {
			sessionModel.put("error", "Activate Remarks is mandatory as it will be used for tracing reason of change/update");
			sessionModel.remove("success");
			return new ModelAndView("/main/material/rawMatlForm", sessionModel);
		}
		
		String screenMode = sessionModel.get("screenMode").toString();
		int rawMatlId = (int) sessionModel.get("rawMatlId");
		Date modifyDate = new Date();
		int nextStatus = RecordStatusEnum.PEND_ACTIVATE.intValue();
		boolean authLvl2 = (Boolean)sessionModel.get("authLvl2");

		if(authLvl2) {
			nextStatus = RecordStatusEnum.SUBMITTED.intValue();
		}
		
		Integer currentLock = (Integer) sessionModel.get("currentOptlock");
		try {
			rmServ.updRawMatlStatusWithOptLock(rawMatlId, nextStatus, request.getRemoteUser(),
					modifyDate, currentLock, null);
			addTrxHis(modifyDate, request.getRemoteUser(), rmName, nextStatus,
					sessionModel, rawMatlId, activateRemark);
			taskCreationServ.taskCreationMatl(rawMatlId, rmName, request.getRemoteUser(),
					nextStatus, activateRemark, screenMode, modifyDate);
			
			
			sessionModel.put("success",
					"Raw material " + rmName + " has been submitted for activate.");
			sessionModel.remove("error");

		} catch (Exception e) {
			logger.error("activateMatl() Error!", e);
			e.printStackTrace();

			String errorMsg = "Activate Raw material " + rmName + " was unsuccessful.";

			if (e instanceof CommonException) {
				if (e.getMessage().contains("Concurrent")) {
					errorMsg = errorMsg.concat(
							" Record could have been updated by another user.<br /> Kindly refresh the listing and view back the record.");
				}
			}

			sessionModel.put("error", errorMsg);
			sessionModel.remove("success");
		}
		retrieveMatlData("", "", "", "", "", "", "", "", sessionModel, request.getRemoteUser(), 0);
		return new ModelAndView("/main/material/rawMatlList", sessionModel);
	}
	
	private boolean saveMatlData(String rawMatlName, String manfIns, String manfENo, String manfFema, String manfJecfa,
			String manfCas, String manfGmo, Integer manfFlavorStatus, String manfPHO, String remark,
			HttpServletRequest request, int matlStatus, Date modifyDate, String action,
			Map<String, Object> sessionModel) {

		String screenMode = sessionModel.get("screenMode").toString();

		List<RmManf> rmManfTable = (List<RmManf>) sessionModel.get("rmManfItems");
		List<RmStat> rmStatTable = (List<RmStat>) sessionModel.get("regItems");
		List<RmStat> rmStatSupplTable = (List<RmStat>) sessionModel.get("rmStatSupplItems");
		List<RmIngredient> rmIngTable = (List<RmIngredient>) sessionModel.get("ingItems");

		if (!saveFuncErrorCheck(rmManfTable, rmStatTable, rmIngTable, rmStatSupplTable, rawMatlName, manfGmo,
				manfFlavorStatus, manfPHO, action, sessionModel, remark)) {
			return false;
		}

		int rawMatlId = 0;
		try {

			if (screenMode.equals(CommonConstants.SCREEN_MODE_ADD)) {
				rmServ.addRawMatl(rawMatlName, "", remark, request.getRemoteUser(), matlStatus, manfIns, manfENo,
						manfFema, manfJecfa, manfCas, manfGmo, manfFlavorStatus, manfPHO, request.getRemoteUser(),
						modifyDate);

				for (RmManf rmManf : rmManfTable) {
					rmManfServ.addRmManf(rawMatlName, rmManf.getTsNo(), rmManf.getManfId(), rmManf.getVipdDate(),
							rmManf.getVipdAnnex2Date(), request.getRemoteUser(), rmManf.getOriginCountryId(),
							rmManf.getVipdObject(), rmManf.getVipdAnnex2Object(), rmManf.getVipdFileName(),
							rmManf.getVipdAnnex2FileName(), modifyDate);

					for (int i = 0; i < rmManf.getSupplId().size(); i++) {
						rmManfSupplServ.addRmManfSuppl(rmManf.getManfId(), rawMatlName, rmManf.getSupplId().get(i));
					}

					for (RmStat rmStat : rmStatTable) {
						rmStatServ.addRmStat(rawMatlName, rmManf.getManfId(), rmManf.getSupplDeclareStatus(),
								rmStat.getFinalStatus(), rmStat.getCountryId(), request.getRemoteUser());
					}
				}

				for (RmIngredient rmIng : rmIngTable) {
					rmIngServ.addRmIngredient(rawMatlName, rmIng.getManfId(), rmIng.getIngName(), rmIng.getCompPerc(),
							rmIng.getInsNo(), rmIng.getENo(), rmIng.getFemaNo(), rmIng.getJecfaNo(), rmIng.getCasNo(),
							request.getRemoteUser(), modifyDate);
				}

				for (RmStat rmStat : rmStatSupplTable) {
					rmStatServ.updRmStatVipd(rawMatlName, rmStat.getManfId(), rmStat.getVipdStatus(),
							rmStat.getCountryId(), request.getRemoteUser(), modifyDate);
				}

			} else if (screenMode.equals(CommonConstants.SCREEN_MODE_EDIT)) {

				rawMatlId = (int) sessionModel.get("rawMatlId");
				String checkerId = "";
				if (matlStatus == RecordStatusEnum.SUBMITTED.intValue()) {
					checkerId = request.getRemoteUser();
				} else {
					checkerId = "";
				}
				
				Integer currentLock = (Integer) sessionModel.get("currentOptlock");
				rmServ.updRawMatlWithOptLock(rawMatlId, rawMatlName, remark, matlStatus,
						manfIns, manfENo, manfFema, manfJecfa, manfCas, manfGmo, manfFlavorStatus, manfPHO,
						request.getRemoteUser(), request.getRemoteUser(), checkerId, modifyDate, currentLock);

				List<RmManf> rmManfDel = (List<RmManf>) sessionModel.get("rmManfDel");
				if (rmManfDel.size() > 0) {
					for (RmManf delRmManf : rmManfDel) {
						rmManfSupplServ.clrRmManfSuppl(delRmManf.getRmManfId());
						rmManfServ.delDetailRmManf(rawMatlId, delRmManf.getRmManfId());
						rmStatServ.delRmStatByManf(rawMatlId, delRmManf.getManfId());
						rmIngServ.delRmIngByManf(rawMatlId, delRmManf.getManfId());
					}
					rmManfDel = new ArrayList<RmManf>();
					sessionModel.replace("rmManfDel", rmManfDel);
				}

				List<RmStat> rmStatDel = (List<RmStat>) sessionModel.get("rmStatDel");
				if (rmStatDel.size() > 0) {
					for (RmStat delRmStat : rmStatDel) {
						rmStatServ.delRmStatByCountry(rawMatlId, delRmStat.getCountryId());
					}
					rmStatDel = new ArrayList<RmStat>();
					sessionModel.replace("rmStatDel", rmStatDel);
				}

				List<RmIngredient> rmIngDel = (List<RmIngredient>) sessionModel.get("rmIngDel");
				if (rmIngDel.size() > 0) {
					for (RmIngredient delRmIng : rmIngDel) {
						rmIngServ.delDetailRmIngredient(rawMatlId, delRmIng.getRmIngId());
					}
					rmIngDel = new ArrayList<RmIngredient>();
					sessionModel.replace("rmIngDel", rmIngDel);
				}

				for (RmManf rmManf : rmManfTable) {
					rmManfSupplServ.clrRmManfSuppl(rmManf.getRmManfId());

					if (rmManf.getManfRecStatus().equals("new")) {
						rmManfServ.addRmManf(rawMatlName, rmManf.getTsNo(), rmManf.getManfId(), rmManf.getVipdDate(),
								rmManf.getVipdAnnex2Date(), request.getRemoteUser(), rmManf.getOriginCountryId(),
								rmManf.getVipdObject(), rmManf.getVipdAnnex2Object(), rmManf.getVipdFileName(),
								rmManf.getVipdAnnex2FileName(), modifyDate);

					} else if (rmManf.getManfRecStatus().equals("exist")) {
						rmManfServ.updRmManf(rawMatlId, rmManf.getTsNo(), rmManf.getManfId(),
								rmManf.getVipdDate(), rmManf.getVipdAnnex2Date(), request.getRemoteUser(),
								rmManf.getOriginCountryId(), rmManf.getRmManfId(), rmManf.getVipdObject(),
								rmManf.getVipdAnnex2Object(), rmManf.getVipdFileName(), rmManf.getVipdAnnex2FileName(),
								modifyDate);
					}

					for (int i = 0; i < rmManf.getSupplId().size(); i++) {
						rmManfSupplServ.addRmManfSuppl(rmManf.getManfId(), rawMatlName, rmManf.getSupplId().get(i));
					}

					for (RmStat rmStat : rmStatTable) {
						logger.debug("saveMatlData() manfStat={}, recStat={}, dataChg={}, supplStat={}", rmManf.getManfRecStatus(),
								rmStat.getRmStatRecStatus(), rmStat.getDataChange(), rmManf.getSupplDeclareStatus());

						if (rmManf.getManfRecStatus().equals("new") || rmStat.getRmStatRecStatus().equals("new")) {
							rmStatServ.addRmStat(rawMatlName, rmManf.getManfId(), rmManf.getSupplDeclareStatus(),
									rmStat.getFinalStatus(), rmStat.getCountryId(), request.getRemoteUser());

						} else if (rmStat.getRmStatRecStatus().equals("exist")) {
							rmStatServ.updRmStat(rmStat.getCountryId(), rawMatlId,
									rmStat.getFinalStatus(), request.getRemoteUser(), modifyDate, rmStat.getDataChange());
						}
					}
				}

				for (RmIngredient rmIng : rmIngTable) {
					if (rmIng.getIngRecStatus().equals("new")) {
						rmIngServ.addRmIngredient(rawMatlName, rmIng.getManfId(), rmIng.getIngName(),
								rmIng.getCompPerc(), rmIng.getInsNo(), rmIng.getENo(), rmIng.getFemaNo(),
								rmIng.getJecfaNo(), rmIng.getCasNo(), request.getRemoteUser(), modifyDate);

					} else if (rmIng.getIngRecStatus().equals("exist")) {
						rmIngServ.updRmIngredient(rawMatlId, rmIng.getManfId(),
								rmIng.getIngName(), rmIng.getCompPerc(), rmIng.getInsNo(), rmIng.getENo(),
								rmIng.getFemaNo(), rmIng.getJecfaNo(), rmIng.getCasNo(), request.getRemoteUser(),
								rmIng.getRmIngId(), modifyDate);
					}
				}

				for (RmStat rmStat : rmStatSupplTable) {
					
					//if(rmStat.isManfIdChanged()) {
						//update based om rm stat id
						logger.debug("saveMatlData() isManfIdChanged={},id={},vipd={},country={}",rmStat.isManfIdChanged(), rmStat.getRmStatId(), rmStat.getVipdStatus(), rmStat.getCountryId());
					//}
					
					if (rmStat.getRmStatId() != 0)
						rmStatServ.updRmStatByRmStatId(rmStat.getRmStatId(), rmStat.getManfId(), rmStat.getVipdStatus(),
								rmStat.getCountryId(), request.getRemoteUser(), modifyDate);
					else
						rmStatServ.updRmStatVipd(rawMatlName, rmStat.getManfId(), rmStat.getVipdStatus(),
								rmStat.getCountryId(), request.getRemoteUser(), modifyDate);
				}
			}

			doCheckAttachment(rawMatlName, request, modifyDate, sessionModel, rawMatlId);

			sessionModel.put("success", "Raw material " + rawMatlName + " saved successfully.");
			sessionModel.remove("error");
			retrieveMatlData("", "", "", "", "", "", "", "", sessionModel, request.getRemoteUser(), 0);

		} catch (Exception e) {
			logger.error("saveMatlData() Error!", e);
			e.printStackTrace();

			String errorMsg = "Fail to @[action] raw material " + rawMatlName + ".";

			if (e instanceof CommonException) {
				if (e.getMessage().contains("Concurrent")) {
					errorMsg = errorMsg.concat(
							" Record could have been updated by another user.<br /> Kindly refresh the listing and view back the record.");
				}
			}

			if (screenMode.equals(CommonConstants.SCREEN_MODE_ADD)) {
				errorMsg = errorMsg.replace("@[action]", msgSource
						.getMessage("functionType_" + CommonConstants.FUNCTION_TYPE_INSERT, null, Locale.getDefault()));
			} else if (screenMode.equals(CommonConstants.SCREEN_MODE_EDIT)) {
				errorMsg = errorMsg.replace("@[action]", msgSource
						.getMessage("functionType_" + CommonConstants.FUNCTION_TYPE_UPDATE, null, Locale.getDefault()));
			}
			
			sessionModel.put("error", errorMsg);
			sessionModel.remove("success");
			retrieveMatlData("", "", "", "", "", "", "", "", sessionModel, request.getRemoteUser(), 0);

			return false;
		}

		return true;
	}

	private void doCheckAttachment(String rawMatlName, HttpServletRequest request, Date modifyDate,
			Map<String, Object> sessionModel, int rawMatlId) {
		List<Document> docDel = (List<Document>) sessionModel.get("docDel");
		if (docDel.size() > 0) {
			for (Document doc : docDel) {
				try {
					docServ.deleteDocument(doc.getId());
				} catch (Exception e) {
				}
			}
			docDel = new ArrayList<Document>();
			sessionModel.replace("docDel", docDel);
		}
		
		if (rawMatlId == 0) {
			rawMatlId = rmServ.searchRawMaterial(rawMatlName, String.valueOf(CommonConstants.SEARCH_OPTION_EXACT), 0, 0)
					.get(0).getRawMatlId();
		}

		List<Document> attachTable = (List<Document>) sessionModel.get("attachItems");
		for (Document doc : attachTable) {
			if (doc.getDocRecStatus().equals("new")) {
				docServ.addDocument(doc.getSubject(), String.valueOf(rawMatlId), CommonConstants.RECORD_TYPE_ID_RAW_MATL,
						doc.getDocumentType(), doc.getFileName(), doc.getContentObj(), doc.getCreatedBy());

			} else if (doc.getDocRecStatus().equals("exist")) {
				docServ.updDocument(doc.getSubject(), doc.getDocumentType(), doc.getFileName(), doc.getContentObj(),
						request.getRemoteUser(), modifyDate, doc.getId());
			}
		}
	}

	private boolean saveFuncErrorCheck(List<RmManf> rmManfTable, List<RmStat> rmStatTable, List<RmIngredient> rmIngTable,
			List<RmStat> rmStatSupplTable, String rawMatlName, String manfGmo, Integer manfFlavorStatus, String manfPHO,
			String action, Map<String, Object> sessionModel, String remark) {

		sessionModel.remove("error");
		sessionModel.remove("success");
		String screenMode = sessionModel.get("screenMode").toString();
		String errorMsg = "";

		if (rmManfTable.size() == 0) {
			errorMsg += "Please enter manufacturer information.<br />";
		}

		if (action.equals("submitForm")) {
			errorMsg += checkTableData(errorMsg, rmStatTable, rmIngTable);
		}

		errorMsg += commonValidatorServ.validateMandatoryInput(rawMatlName, "Raw Material");
		errorMsg += commonValidatorServ.validateMandatoryInput(manfGmo, "GMO Status");
		
		if (Objects.isNull(manfFlavorStatus) || manfFlavorStatus == 0) {
			errorMsg += "Flavor Status is mandatory.<br />";
		}
		
		errorMsg += commonValidatorServ.validateMandatoryInput(manfPHO, "Contain PHO");
//		errorMsg += commonValidatorServ.validateMandatoryInputRemark(remark, "Remark");

		if (errorMsg.length() != 0) {		
			
			//Set back values of current fields based on latest value, not retrieved value
			holdRmValues(rawMatlName, manfGmo, manfFlavorStatus, manfPHO, sessionModel, remark, errorMsg);
			//
			
			sessionModel.put("error", errorMsg);
			return false;
		}

		if (screenMode.equals(CommonConstants.SCREEN_MODE_ADD)) {
			List<RawMaterial> rm = rmServ.searchRawMaterial(rawMatlName.trim(),
					String.valueOf(CommonConstants.SEARCH_OPTION_EXACT), 0, 0);
			if (null != rm && rm.size() > 0) {
				sessionModel.put("error", "Raw Material already exist.");
				return false;
			}
		}
		
		if (screenMode.equals(CommonConstants.SCREEN_MODE_EDIT) && StringUtils.isNotEmpty(rawMatlName)) {
			String retrievedPrName = (String) sessionModel.get("rawMatlName");
			if (!StringUtils.equalsIgnoreCase(retrievedPrName.trim(), rawMatlName.trim())) {
				List<RawMaterial> rm = rmServ.searchRawMaterial(rawMatlName.trim(),
						String.valueOf(CommonConstants.SEARCH_OPTION_EXACT), 0, 0);
				if (null != rm && rm.size() > 0) {
					sessionModel.put("error", "Raw Material already exist.");
					return false;
				}
			}
		}

		boolean supplDeclareNotEnter = false;
		for (RmStat rmStat : rmStatSupplTable) {
			if (rmStat.getVipdStatus() == 0) {
				supplDeclareNotEnter = true;
			}
		}

		if (supplDeclareNotEnter) {
			// FSGS)Azmeer 05/03/2021 - Change the error message
			sessionModel.put("error",
					"Please enter supplier declaration status to each country for new added manufacturer.");
			return false;
		}

		return true;
	}

	private void holdRmValues(String rawMatlName, String manfGmo, Integer manfFlavorStatus, String manfPHO,
			Map<String, Object> sessionModel, String remark, String errorMsg) {
		RawMaterial rm = (RawMaterial) sessionModel.get("rawMatlItems");
		if (errorMsg.contains("Raw Material"))
			rm.setRawMatlName(rawMatlName);
		if (errorMsg.contains("Flavor Status"))
			rm.setFlavStatusId(manfFlavorStatus);
		if (errorMsg.contains("GMO Status"))
			rm.setGmoStatus(manfGmo);
		if (errorMsg.contains("Contain PHO"))
			rm.setPhoFlag(manfPHO);
		if (errorMsg.contains("Remark"))
			rm.setRemarks(remark);
	}

	private String checkTableData(String errorMsg, List<RmStat> rmStatTable, List<RmIngredient> rmIngTable) {
		if (rmStatTable.size() == 0) {
			errorMsg += "Please enter regulation information.<br />";
		}

		if (rmIngTable.size() == 0) {
			errorMsg += "Please enter ingredient information.<br />";
		}

		return errorMsg;
	}

	private void rawMatlFormReg(String countryId, Map<String, Object> sessionModel) {

		sessionModel.put("currTab", "regulation");
		sessionModel.put("btnAddRegHid", true);
		sessionModel.put("btnAddRegRfhHid", true);
		sessionModel.put("divRegRefHid", false);
		
		String screenMode = sessionModel.get("screenMode").toString();	
		
		int rawMatlId = 0;
		
		if(!screenMode.equals(CommonConstants.SCREEN_MODE_ADD))
			rawMatlId = (int) sessionModel.get("rawMatlId");

		try {
			
			if (rawMatlId <= 0)
				throw new CommonException();
			
			logger.debug("rawMatlFormReg() screenMode={},regCountry={},rawMatlId={}",screenMode,countryId,rawMatlId);
			
			//Regl reg = regServ.searchReglList(rawMatlId, countryId).get(0);
			//List<ReglDoc> regDoc = regDocServ.findReglDocByRegId(reg.getRegId());
			List<ReglFileSearch> regList = rfServ.searchFileAndInfoByCriteria(rawMatlId, countryId);
			ReglFileSearch reg = regList.get(0);

			sessionModel.put("regDocBlockNotFound", true);
			sessionModel.put("regDocBlock", false);
			sessionModel.put("regHeader",
					"Country Regulation Information - "
							+ refCountryServ.findOneById(countryId).getCountryName());
			
			sessionModel.put("regCountry",countryId);
			sessionModel.put("regName", reg.getRegName());
			sessionModel.put("regRef1", reg.getRefUrl1());
			sessionModel.put("regRef2", reg.getRefUrl2());
			sessionModel.put("regRef3", reg.getRefUrl3());
			sessionModel.put("regRef4", reg.getRefUrl4());
			sessionModel.put("regRef5", reg.getRefUrl5());
			sessionModel.put("regDocItems", regList);
			sessionModel.put("regRemark", reg.getRemarks());

		} catch (Exception e) {
			sessionModel.put("regDocBlockNotFound", false);
			sessionModel.put("regDocBlock", true);
			sessionModel.put("regHeader",
					"Country Regulation Information Not Found - "
							+ refCountryServ.findOneById(countryId).getCountryName());
			sessionModel.put("btnAddRegHid", false);
			sessionModel.put("btnAddRegRfhHid", false);
			sessionModel.put("regCountry",countryId);
		}
	}

	@GetMapping("/main/material/rawMatlFormDownloadReg")
	public ResponseEntity<byte[]> downloadRegulationFile(HttpServletRequest request,
			@RequestParam(name = "id") int regDocId) {

		//ReglDoc regDoc = regDocServ.findReglDocById(regDocId);

		ReglFileDto regDoc = rfServ.findDtoById(regDocId);
		
		MediaType mt = MediaTypeUtils.getMediaTypeForFileName(servletContext, regDoc.getFileName());

		return ResponseEntity.ok().contentType(mt)
				.header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + regDoc.getFileName() + "\"")
				.body(regDoc.getContentObject());
	}
	
	@RequestMapping(value = "/main/material/regCountryOnChange", method = RequestMethod.GET)
	public String regCountryOnChange(@RequestParam(name = "countryId") String countryId, HttpSession session, ModelMap model) {		

		rawMatlFormReg(countryId,sessionModel);		
		model.addAllAttributes(sessionModel);

		return "/main/material/rawMatlForm :: #reg1";
	}
	
	@RequestMapping(value = "/main/material/mfrAutocomplete", method = RequestMethod.GET, headers = "Accept=application/json")
	@ResponseBody
	public List<LabelAndValueDto> mfrAutocomplete(
			@RequestParam(name = "term", required = false, defaultValue = "") String term) {

		List<LabelAndValueDto> suggestions = refManfServ.getVendorNameLabelAndValue(term,
				CommonConstants.VENDOR_TYPE_MFR);

		return suggestions;
	}
			
	private String getRecordStatusDesc(int status) {
		String getStatus = "";
		switch (status) {
		case CommonConstants.RECORD_STATUS_DRAFT:
			getStatus = msgSource.getMessage("vrStatus_" + CommonConstants.RECORD_STATUS_DRAFT, null,
					Locale.getDefault());
			break;
		case CommonConstants.RECORD_STATUS_PENDING_AUTH:
			getStatus = msgSource.getMessage("vrStatus_" + CommonConstants.RECORD_STATUS_PENDING_AUTH, null,
					Locale.getDefault());
			break;
		case CommonConstants.RECORD_STATUS_SUBMITTED:
			getStatus = msgSource.getMessage("vrStatus_" + CommonConstants.RECORD_STATUS_SUBMITTED, null,
					Locale.getDefault());
			break;
		case CommonConstants.RECORD_STATUS_REJECTED:
			getStatus = msgSource.getMessage("vrStatus_" + CommonConstants.RECORD_STATUS_REJECTED, null,
					Locale.getDefault());
			break;
		case CommonConstants.RECORD_STATUS_REWORK:
			getStatus = msgSource.getMessage("vrStatus_" + CommonConstants.RECORD_STATUS_REWORK, null,
					Locale.getDefault());
			break;
		case CommonConstants.RECORD_STATUS_REWORK_PENDING_AUTH:
			getStatus = msgSource.getMessage("vrStatus_" + CommonConstants.RECORD_STATUS_REWORK_PENDING_AUTH, null,
					Locale.getDefault());
			break;
		case CommonConstants.RECORD_STATUS_PENDING_CONFIRM:
			getStatus = msgSource.getMessage("vrStatus_" + CommonConstants.RECORD_STATUS_PENDING_CONFIRM, null,
					Locale.getDefault());
			break;
		case CommonConstants.RECORD_STATUS_CHG_PENDING_AUTH:
			getStatus = msgSource.getMessage("vrStatus_" + CommonConstants.RECORD_STATUS_CHG_PENDING_AUTH, null,
					Locale.getDefault());
			break;
		case CommonConstants.RECORD_STATUS_CHG_PENDING_CONFIRM:
			getStatus = msgSource.getMessage("vrStatus_" + CommonConstants.RECORD_STATUS_CHG_PENDING_CONFIRM, null,
					Locale.getDefault());
			break;
		case CommonConstants.RECORD_STATUS_CHG_REWORK:
			getStatus = msgSource.getMessage("vrStatus_" + CommonConstants.RECORD_STATUS_CHG_REWORK, null,
					Locale.getDefault());
			break;
		default:
			getStatus = "";
			break;

		}
		return getStatus;
	}
}