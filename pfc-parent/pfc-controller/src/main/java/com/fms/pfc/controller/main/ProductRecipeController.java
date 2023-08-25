package com.fms.pfc.controller.main;

import java.io.IOException;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
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

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
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
import com.fms.pfc.domain.dto.main.FlavorStatusDto;
import com.fms.pfc.domain.dto.main.ProductRecipeDto;
import com.fms.pfc.domain.dto.main.RawMaterialDto;
import com.fms.pfc.domain.dto.main.ReglFileDto;
import com.fms.pfc.domain.model.Document;
import com.fms.pfc.domain.model.Task;
import com.fms.pfc.domain.model.UsrProfile;
import com.fms.pfc.domain.model.UsrRole;
import com.fms.pfc.domain.model.main.PrIngredient;
import com.fms.pfc.domain.model.main.PrRmAdditive;
import com.fms.pfc.domain.model.main.PrRmReg;
import com.fms.pfc.domain.model.main.PrRmStat;
import com.fms.pfc.domain.model.main.PrStat;
import com.fms.pfc.domain.model.main.ProductRecipe;
import com.fms.pfc.domain.model.main.RawMaterial;
import com.fms.pfc.domain.model.main.Regl;
import com.fms.pfc.domain.model.main.ReglDoc;
import com.fms.pfc.domain.model.main.ReglFileSearch;
import com.fms.pfc.domain.model.main.RmManf;
import com.fms.pfc.domain.model.main.RmManfSuppl;
import com.fms.pfc.domain.model.main.TsNoDto;
import com.fms.pfc.exception.CommonException;
import com.fms.pfc.service.api.base.AttachmentService;
import com.fms.pfc.service.api.base.CountryService;
import com.fms.pfc.service.api.base.DocumentTypeService;
import com.fms.pfc.service.api.base.TaskService;
import com.fms.pfc.service.api.base.TrxHisService;
import com.fms.pfc.service.api.base.UserProfileService;
import com.fms.pfc.service.api.base.UsrRoleService;
import com.fms.pfc.service.api.main.FlavorStatusService;
import com.fms.pfc.service.api.main.PrIngService;
import com.fms.pfc.service.api.main.PrRmAdditiveService;
import com.fms.pfc.service.api.main.PrRmRegService;
import com.fms.pfc.service.api.main.PrRmStatService;
import com.fms.pfc.service.api.main.ProductRecipeService;
import com.fms.pfc.service.api.main.ProductStatusService;
import com.fms.pfc.service.api.main.RawMaterialService;
import com.fms.pfc.service.api.main.RegService;
import com.fms.pfc.service.api.main.ReglFileService;
import com.fms.pfc.service.api.main.RegulationCategoryService;
import com.fms.pfc.service.api.main.RegulationDocService;
import com.fms.pfc.service.api.main.RegulationGroupService;
import com.fms.pfc.service.api.main.RmManfService;
import com.fms.pfc.service.api.main.RmManfSupplService;
import com.fms.pfc.service.api.main.RmStatService;
import com.fms.pfc.validation.common.CommonValidation;

@SuppressWarnings("unchecked")
@Controller
@SessionScope
public class ProductRecipeController {

	private final Logger logger = LoggerFactory.getLogger(ProductRecipeController.class);

	private RmManfSupplService rmManfSupplServ;
	private ProductRecipeService prdRcpServ;
	private PrIngService prIngServ;
	private PrRmStatService prRmServ;
	private PrRmAdditiveService prRmAddServ;
	private PrRmRegService prRmRegServ;
	private CountryService refCountryServ;
	private RawMaterialService rawMatlServ;
	private ProductStatusService prStatServ;
	private UsrRoleService usrRoleServ;
	private Authority auth;
	private CommonValidation commonValServ;
	private RegService regServ;
	private RegulationDocService regDocServ;
	private RegulationCategoryService regCatServ;
	private RegulationGroupService regGrpServ;
	private RmManfService rmManfServ;
	private RmStatService rmStatServ;
	private TaskCreation taskCreationServ;
	private TrxHisService trxHistServ;
	private AttachmentService docServ;
	private DocumentTypeService docTypeServ;
	private UserProfileService userServ;
	private TaskService taskServ;
	private MessageSource msgSource;
	private ServletContext servletContext;
	private FlavorStatusService fsServ;
	private ReglFileService rfServ;
	
	private Map<String, Object> sessionModel = new HashMap<String, Object>();

	@Autowired
	public ProductRecipeController(RmManfSupplService rmManfSupplServ, ProductRecipeService prdRcpServ,
			PrIngService prIngServ, PrRmStatService prRmServ, PrRmAdditiveService prRmAddServ,
			PrRmRegService prRmRegServ, CountryService refCountryServ, RawMaterialService rawMatlServ,
			ProductStatusService prStatServ, UsrRoleService usrRoleServ, Authority auth, CommonValidation commonValServ,
			RegService regServ, RegulationDocService regDocServ, RegulationCategoryService regCatServ,
			RegulationGroupService regGrpServ, RmManfService rmManfServ, RmStatService rmStatServ, TaskCreation taskCreationServ,
			TrxHisService trxHistServ, AttachmentService docServ, DocumentTypeService docTypeServ, UserProfileService userServ,
			TaskService taskServ, MessageSource msgSource, ServletContext servletContext, FlavorStatusService fsServ, ReglFileService rfServ) {
		super();
		this.rmManfSupplServ = rmManfSupplServ;
		this.prdRcpServ = prdRcpServ;
		this.prIngServ = prIngServ;
		this.prRmServ = prRmServ;
		this.prRmAddServ = prRmAddServ;
		this.prRmRegServ = prRmRegServ;
		this.refCountryServ = refCountryServ;
		this.rawMatlServ = rawMatlServ;
		this.prStatServ = prStatServ;
		this.usrRoleServ = usrRoleServ;
		this.auth = auth;
		this.commonValServ = commonValServ;
		this.regServ = regServ;
		this.regDocServ = regDocServ;
		this.regCatServ = regCatServ;
		this.regGrpServ = regGrpServ;
		this.rmManfServ = rmManfServ;
		this.rmStatServ = rmStatServ;
		this.taskCreationServ = taskCreationServ;
		this.trxHistServ = trxHistServ;
		this.docServ = docServ;
		this.docTypeServ = docTypeServ;
		this.userServ = userServ;
		this.taskServ = taskServ;
		this.msgSource = msgSource;
		this.servletContext = servletContext;
		this.fsServ = fsServ;
		this.rfServ = rfServ;
	}

	@GetMapping("/main/product/productList")
	public ModelAndView loadProductList(HttpServletRequest request, HttpServletResponse response) {
		
		sessionModel = auth.onPageLoad(sessionModel, request);
		auth.isAuthUrl(request, response);
		searchProductRecipe("", "", "", "", "", "", "", "", sessionModel, request.getRemoteUser(), 0, "", "");

		UsrProfile user = userServ.searchUserProfile("", request.getRemoteUser().trim(), "", "", "", "",
				String.valueOf(CommonConstants.SEARCH_OPTION_CONTAIN), "", "").get(0);

		if (user.getGroupId().equals(CommonConstants.GROUP_ID_FR)) {
			sessionModel.put("btnDel", true);
			sessionModel.put("btnAdd", true);
		}

		return new ModelAndView("/main/product/productList", sessionModel);
	}

	@PostMapping("/main/product/productListSrc")
	public ModelAndView searchProductList(@RequestParam(name = "prodName") String prodName,
			@RequestParam(name = "recipeNo") String recipeNo, @RequestParam(name = "rawMatlName") String rawMatlName,
			@RequestParam(name = "interProdName") String interProdName, @RequestParam(name = "exp1") String exp1,
			@RequestParam(name = "exp2") String exp2, @RequestParam(name = "exp3") String exp3,
			@RequestParam(name = "exp4") String exp4, @RequestParam(name = "status") int status,
			@RequestParam(name = "prodOtherName") String prodOtherName, @RequestParam(name = "exp5") String exp5,
			HttpServletRequest request, HttpSession session) {

		StringBuffer sb = new StringBuffer();
		sb.append("Search Criteria: ");
		sb.append("Product Recipe Name=").append(prodName.isEmpty() ? "<ALL>" : prodName).append(", ");
		sb.append("Other Names=").append(prodOtherName.isEmpty() ? "<ALL>" : prodOtherName).append(", ");
		sb.append("Recipe No=").append(recipeNo.isEmpty() ? "<ALL>" : recipeNo).append(", ");
		sb.append("Raw Material Name=").append(rawMatlName.isEmpty() ? "<ALL>" : rawMatlName).append(", ");
		sb.append("Intermediate Product Name=").append(interProdName.isEmpty() ? "<ALL>" : interProdName);

		trxHistServ.addTrxHistory(new Date(), "Search Product Recipe", request.getRemoteUser(),
				CommonConstants.FUNCTION_TYPE_SEARCH, "Search Product Recipe", CommonConstants.RECORD_TYPE_ID_PROD_RECP,
				sb.toString());

		// FSGS) Hani 10/3/2021 Add/Change .trim() to search criteria START
		searchProductRecipe(prodName.trim(), recipeNo.trim(), rawMatlName.trim(), interProdName.trim(), exp1, exp2,
				exp3, exp4, sessionModel, request.getRemoteUser(), status, prodOtherName.trim(), exp5);
		// FSGS) Hani 10/3/2021 Add/Change .trim() to search criteria END

		sessionModel.put("prodName", prodName);
		sessionModel.put("prodOtherName", prodOtherName);
		sessionModel.put("recipeNo", recipeNo);
		sessionModel.put("rawMatlName", rawMatlName);
		sessionModel.put("interProdName", interProdName);
		sessionModel.put("exp1", exp1);
		sessionModel.put("exp2", exp2);
		sessionModel.put("exp3", exp3);
		sessionModel.put("exp4", exp4);
		sessionModel.put("exp5", exp5);
		sessionModel.put("status", status);
		sessionModel.remove("success");
		sessionModel.remove("error");

		return new ModelAndView("/main/product/productList", sessionModel);
	}

	private void searchProductRecipe(String prName, String recipeNo, String rawMatlName, String interProdName,
			String exp1, String exp2, String exp3, String exp4, Map<String, Object> sessionModel, String currUser,
			int status, String prodOtherName, String exp5) {
		List<ProductRecipe> pr = prdRcpServ.searchProductRecipe2(prName, exp1, recipeNo, exp2, 0, status, prodOtherName, exp5);
		List<ProductRecipe> prDelete = new ArrayList<>();

		for (ProductRecipe prList : pr) {
			/*int rawMatlId = 0;
			int prId = 0;
			if (!rawMatlName.equals("")) {
				try {
					rawMatlId = rawMatlServ.searchRawMaterial(rawMatlName, exp3, 0, 0).get(0).getRawMatlId();
				} catch (Exception e) {
				}
			}

			if (!interProdName.equals("")) {
				try {
					prId = prdRcpServ.searchIntermediateProduct(interProdName, exp4, "", "", 0, 0).get(0).getPrId();					
				} catch (Exception e) {
				}
			}
			
			List<PrIngredient> prIng = prIngServ.searchPrIngredient(prList.getPrId(), rawMatlId, prId);*/
			
			List<Integer> rawMatls = new ArrayList<>();
			List<Integer> prodRecps = new ArrayList<>();
			List<PrIngredient> prIng = new ArrayList<>();

			if (!rawMatlName.equals("")) {
				try {
					rawMatls = rawMatlServ.searchRawMaterial(rawMatlName, exp3, 0, 0).stream()
							.map(RawMaterial::getRawMatlId).collect(Collectors.toList());
				} catch (Exception e) {
				}
			}

			if (!interProdName.equals("")) {
				try {
					prodRecps = prdRcpServ.searchIntermediateProduct(interProdName, exp4, "", "", 0, 0).stream()
							.map(ProductRecipe::getPrId).collect(Collectors.toList());
				} catch (Exception e) {
				}
			}

			if (rawMatls.size() > 0 && prodRecps.size() > 0) {
				prIng.addAll(prIngServ.searchPrIngredient(prList.getPrId(), 999, 999, rawMatls, prodRecps));
			} else if (rawMatls.size() > 0 && prodRecps.size() == 0) {
				prIng.addAll(prIngServ.searchPrIngredient(prList.getPrId(), 999, 0, rawMatls, prodRecps));
			} else if (rawMatls.size() == 0 && prodRecps.size() > 0) {
				prIng.addAll(prIngServ.searchPrIngredient(prList.getPrId(), 0, 999, rawMatls, prodRecps));
			} else {
				prIng.addAll(prIngServ.searchPrIngredient(prList.getPrId(), 0, 0, rawMatls, prodRecps));
			}
			
			String ingredientName = "";
			int numberPermitted = 0;
			int numberNotPermitted = 0;
			int numberNotSure = 0;
			boolean hasValue = false;

			for (PrIngredient prIngList : prIng) {
				hasValue = true;
				if (ingredientName.equals("")) {
					if (prIngList.getRefType() == CommonConstants.RECORD_TYPE_ID_RAW_MATL) {
						List<RawMaterial> rawMatlInfo = rawMatlServ.searchRawMaterial("", "", prIngList.getRefId(), 0);
						if (rawMatlInfo.size() != 0) {
							ingredientName = rawMatlInfo.get(0).getRawMatlName();
						}
					} else if (prIngList.getRefType() == CommonConstants.RECORD_TYPE_ID_PROD_RECP) {
						List<ProductRecipe> productRecipeInfo = prdRcpServ.searchProductRecipe("", "", "", "",
								prIngList.getRefId(), 0);
						if (productRecipeInfo.size() != 0) {
							ingredientName = productRecipeInfo.get(0).getPrName();
						}
					}
				} else {
					if (prIngList.getRefType() == CommonConstants.RECORD_TYPE_ID_RAW_MATL) {
						List<RawMaterial> rawMatlInfo = rawMatlServ.searchRawMaterial("", "", prIngList.getRefId(), 0);
						if (rawMatlInfo.size() != 0) {
							ingredientName = ingredientName + "<br />" + rawMatlInfo.get(0).getRawMatlName();
						}
					} else if (prIngList.getRefType() == CommonConstants.RECORD_TYPE_ID_PROD_RECP) {
						List<ProductRecipe> productRecipeInfo = prdRcpServ.searchProductRecipe("", "", "", "",
								prIngList.getRefId(), 0);
						if (productRecipeInfo.size() != 0) {
							ingredientName = ingredientName + "<br />" + productRecipeInfo.get(0).getPrName();
						}
					}
				}
			}

			List<PrStat> prStat = prStatServ.searchProductStatus(prList.getPrId(),
					String.valueOf(CommonConstants.SEARCH_OPTION_EXACT), "", 0);

			for (PrStat prStatList : prStat) {
				if (prStatList.getFinalStatus() == FinalStatusEnum.PERMITTED.intValue()) {
					numberPermitted++;
				} else if (prStatList.getFinalStatus() == FinalStatusEnum.NOT_PERMITTED.intValue()) {
					numberNotPermitted++;
				} else if (prStatList.getFinalStatus() == FinalStatusEnum.NOT_SURE.intValue()) {
					numberNotSure++;
				}
			}

			prList.setIngredientName(ingredientName);
			prList.setNumberPermitted(numberPermitted);
			prList.setNumberNotPermitted(numberNotPermitted);
			prList.setNumberNotSure(numberNotSure);

			if (prList.getCreatorId().equals(currUser)) {
				prList.setPermissionToDelete(true);
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
					prList.setPermissionToDelete(true);
				} else {
					prList.setPermissionToDelete(false);
				}
			}

			if (!hasValue || ingredientName.equals("")) {
				prDelete.add(prList);
			}

			if ((!rawMatlName.equals("") && rawMatls.size() == 0)
					|| (!interProdName.equals("") && prodRecps.size() == 0)) {
				prDelete.add(prList);
			}
			
			// 20211026 set other names to display in UI search list
			{
				StringBuffer otherNames = new StringBuffer();
				otherNames.append(StringUtils.isNotEmpty(prList.getPrOtherName1()) ? prList.getPrOtherName1() : "");
				otherNames.append(
						StringUtils.isNotEmpty(prList.getPrOtherName2()) ? "<br />" + prList.getPrOtherName2() : "");
				otherNames.append(
						StringUtils.isNotEmpty(prList.getPrOtherName3()) ? "<br />" + prList.getPrOtherName3() : "");
				otherNames.append(
						StringUtils.isNotEmpty(prList.getPrOtherName4()) ? "<br />" + prList.getPrOtherName4() : "");
				otherNames.append(
						StringUtils.isNotEmpty(prList.getPrOtherName5()) ? "<br />" + prList.getPrOtherName5() : "");

				prList.setAllOtherNames(otherNames.toString());
			}
		}

		pr.removeAll(prDelete);

		sessionModel.put("productLists", pr);
	}
	
	// FSGS) KarVei Add 26/02/2021 - Search function for prSearchModal
	private void searchProductRecipeModal(String prName, String recipeNo, String rawMatlName, String interProdName,
			String exp1, String exp2, String exp3, String exp4, Map<String, Object> sessionModel) {
		List<ProductRecipe> pr = prdRcpServ.searchProductRecipe(prName, exp1, recipeNo, exp2, 0,
				RecordStatusEnum.SUBMITTED.intValue());
		List<ProductRecipe> prDelete = new ArrayList<>();

		for (ProductRecipe prList : pr) {
			int rawMatlId = 0;
			int prId = 0;
			if (!rawMatlName.equals("")) {
				try {
					rawMatlId = rawMatlServ.searchRawMaterial(rawMatlName, exp3, 0, 0).get(0).getRawMatlId();
				} catch (Exception e) {
				}
			}

			if (!interProdName.equals("")) {
				try {
					prId = prdRcpServ.searchProductRecipe(interProdName, exp4, "", "", 0, 0).get(0).getPrId();
				} catch (Exception e) {
				}
			}

			List<PrIngredient> prIng = prIngServ.searchPrIngredient(prList.getPrId(), rawMatlId, prId);
			String ingredientName = "";
			int numberPermitted = 0;
			int numberNotPermitted = 0;
			int numberNotSure = 0;
			boolean hasValue = false;

			for (PrIngredient prIngList : prIng) {
				hasValue = true;
				if (ingredientName.equals("")) {
					if (prIngList.getRefType() == CommonConstants.RECORD_TYPE_ID_RAW_MATL) {
						List<RawMaterial> rawMatlInfo = rawMatlServ.searchRawMaterial("", "", prIngList.getRefId(), 0);
						if (rawMatlInfo.size() != 0) {
							ingredientName = rawMatlInfo.get(0).getRawMatlName();
						}
					} else if (prIngList.getRefType() == CommonConstants.RECORD_TYPE_ID_PROD_RECP) {
						List<ProductRecipe> productRecipeInfo = prdRcpServ.searchProductRecipe("", "", "", "",
								prIngList.getRefId(), 0);
						if (productRecipeInfo.size() != 0) {
							ingredientName = productRecipeInfo.get(0).getPrName();
						}
					}
				} else {
					if (prIngList.getRefType() == CommonConstants.RECORD_TYPE_ID_RAW_MATL) {
						List<RawMaterial> rawMatlInfo = rawMatlServ.searchRawMaterial("", "", prIngList.getRefId(), 0);
						if (rawMatlInfo.size() != 0) {
							ingredientName = ingredientName + "<br />" + rawMatlInfo.get(0).getRawMatlName();
						}
					} else if (prIngList.getRefType() == CommonConstants.RECORD_TYPE_ID_PROD_RECP) {
						List<ProductRecipe> productRecipeInfo = prdRcpServ.searchProductRecipe("", "", "", "",
								prIngList.getRefId(), 0);
						if (productRecipeInfo.size() != 0) {
							ingredientName = ingredientName + "<br />" + productRecipeInfo.get(0).getPrName();
						}
					}
				}
			}

			List<PrStat> prStat = prStatServ.searchProductStatus(prList.getPrId(),
					String.valueOf(CommonConstants.SEARCH_OPTION_EXACT), "", 0);

			for (PrStat prStatList : prStat) {
				if (prStatList.getFinalStatus() == FinalStatusEnum.PERMITTED.intValue()) {
					numberPermitted++;
				} else if (prStatList.getFinalStatus() == FinalStatusEnum.NOT_PERMITTED.intValue()) {
					numberNotPermitted++;
				} else if (prStatList.getFinalStatus() == FinalStatusEnum.NOT_SURE.intValue()) {
					numberNotSure++;
				}
			}

			prList.setIngredientName(ingredientName);
			prList.setNumberPermitted(numberPermitted);
			prList.setNumberNotPermitted(numberNotPermitted);
			prList.setNumberNotSure(numberNotSure);

			if (!hasValue || ingredientName.equals("")) {
				prDelete.add(prList);
			}

			if ((!rawMatlName.equals("") && rawMatlId == 0) || (!interProdName.equals("") && prId == 0)) {
				prDelete.add(prList);
			}
		}

		pr.removeAll(prDelete);

		sessionModel.put("prModal", pr);
	}

	@PostMapping("/main/product/productListDel")
	public ModelAndView delProductRecipe(HttpServletRequest request, @RequestParam(value = "tableRow") int[] tableRow,
			HttpSession session) {

		sessionModel.remove("error");
		sessionModel.remove("success");
		sessionModel.remove("showModal");
		if (tableRow.equals(null)) {
			searchProductRecipe("", "", "", "", "", "", "", "", sessionModel, request.getRemoteUser(), 0, "", "");
			return new ModelAndView("/main/product/productList", sessionModel);
		}

		try {
			for (int i = 0; i < tableRow.length; i++) {

				prdRcpServ.delProductRecipe(tableRow[i]);
				prIngServ.delPrIngredient(tableRow[i]);
				prRmServ.delPrRmStat(tableRow[i]);
				prRmAddServ.delPrRmAdditive(tableRow[i]);
				prStatServ.delPrStat(tableRow[i]);
				docServ.deleteDocumentByRef(String.valueOf(tableRow[i]), CommonConstants.RECORD_TYPE_ID_PROD_RECP);
				
				trxHistServ.addTrxHistory(new Date(), "Delete Product Recipe", request.getRemoteUser(),
						CommonConstants.FUNCTION_TYPE_DELETE, String.valueOf(tableRow[i]), CommonConstants.RECORD_TYPE_ID_PROD_RECP, null);
			}

			searchProductRecipe("", "", "", "", "", "", "", "", sessionModel, request.getRemoteUser(), 0, "", "");
			sessionModel.put("success", "Record deleted successfully.");
		} catch (Exception e) {
			sessionModel.put("error", "Failed to delete record.");
		}

		return new ModelAndView("/main/product/productList", sessionModel);
	}

	@RequestMapping(value = "/main/product/productListStatus", method = RequestMethod.GET)
	@ResponseBody
	public Map<String, Object> getProductStatus(@RequestParam(name = "prId") int prId,
			@RequestParam(name = "status") int status, @RequestParam(name = "prName") String prName) {

		Map<String, Object> statusModel = new HashMap<String, Object>();
		String header = "";

		if (status == 1) {
			header = prName + " - Permitted Country";
		} else if (status == 2) {
			header = prName + " - Not Permitted Country";
		} else if (status == 3) {
			header = prName + " - Not Sure Country";
		}

		statusModel.put("modalHeader", header);
		statusModel.put("statusItems",
				prStatServ.searchProductStatus(prId, String.valueOf(CommonConstants.SEARCH_OPTION_EXACT), "", status));

		return statusModel;
	}

	@GetMapping("/main/product/productForm")
	public ModelAndView loadProductForm(HttpServletRequest request, HttpServletResponse response, HttpSession session) {

		sessionModel.put("prodRecipeStatus", RecordStatusEnum.DRAFT.intValue());
		sessionModel.put("header", "Add Product Recipe");
		sessionModel.put("screenMode", CommonConstants.SCREEN_MODE_ADD);
		sessionModel.put("btnSave", "Save as draft");
		sessionModel.put("btnEdit", true);
		sessionModel.put("btnReject", true);
		sessionModel.put("btnRework", true);
		sessionModel.put("asterisk", false);
		sessionModel.put("creatorName", request.getRemoteUser());
		sessionModel.put("dateAmendments", new Date());
		sessionModel.put("productCategory", CommonConstants.RECORD_TYPE_ID_RAW_MATL);
		setProductButton(false, true, true, sessionModel);
		setPrStatButton(false, true, true, sessionModel);
		setFoodAdditiveButton(false, true, true, sessionModel);
		setAttachButton(false, true, true, sessionModel);
		onPageLoadCommon(sessionModel);

		List<PrIngredient> prIng = new ArrayList<PrIngredient>();
		sessionModel.put("prIngItems", prIng);

		List<PrIngredient> prIngSummary = new ArrayList<PrIngredient>();
		sessionModel.put("ingSummaryItems", prIngSummary);

		List<PrStat> prStat = new ArrayList<PrStat>();
		sessionModel.put("prStatItems", prStat);

		List<PrRmStat> prRmStat = new ArrayList<PrRmStat>();
		sessionModel.put("prRmStatItems", prRmStat);

		List<PrRmStat> prRmStatTemp = new ArrayList<PrRmStat>();
		sessionModel.put("prRmStatTempItems", prRmStatTemp);

		ProductRecipe prd = new ProductRecipe();
		prd.setTotalPerc(0.00);
		//prd.setRemarks("Initial creation");
		sessionModel.put("productRecipeItems", prd);

		List<PrRmAdditive> prRmAdditive = new ArrayList<PrRmAdditive>();
		sessionModel.put("prRmAdditiveItems", prRmAdditive);

		List<PrIngredient> ingDropDownList = new ArrayList<PrIngredient>();
		sessionModel.put("ingDropDownItems", ingDropDownList);

		List<Document> attachTable = new ArrayList<Document>();
		sessionModel.put("attachItems", attachTable);

		List<PrIngredient> prIngDel = new ArrayList<>();
		List<PrStat> prStatDel = new ArrayList<PrStat>();
		List<PrRmAdditive> prRmAddDel = new ArrayList<PrRmAdditive>();
		List<Document> docDel = new ArrayList<Document>();
		List<PrRmReg> prRmRegItems = new ArrayList<PrRmReg>();

		sessionModel.put("prIngDel", prIngDel);
		sessionModel.put("prStatDel", prStatDel);
		sessionModel.put("prRmAddDel", prRmAddDel);
		sessionModel.put("docDel", docDel);
		sessionModel.put("prRmRegItems", prRmRegItems);
		sessionModel.put("updateToProductList", false);

		return new ModelAndView("/main/product/productForm", sessionModel);
	}

	/*
	 * Retrieve and view raw material
	 */
	@GetMapping("/main/product/productFormView")
	public ModelAndView viewPrdRcp(@RequestParam(name = "prId") int prId, HttpServletRequest request,
			HttpServletResponse response, HttpSession session) {

		String screenMode = CommonConstants.SCREEN_MODE_VIEW;

		sessionModel = auth.onPageLoad(sessionModel, request);
		sessionModel.put("header", "View Product Recipe");
		sessionModel.put("screenMode", screenMode);
		sessionModel.put("asterisk", true);
		sessionModel.put("btnSaveSts", true);
		onPageLoadCommon(sessionModel);
		setProductButton(true, true, true, sessionModel);
		setPrStatButton(true, true, true, sessionModel);
		setFoodAdditiveButton(true, true, true, sessionModel);
		setAttachButton(true, true, true, sessionModel);

		DateFormat formatter = new SimpleDateFormat("dd/MM/yyy hh:mm:ss a");
		ProductRecipe prdRcp = prdRcpServ.searchProductRecipe("", "", "", "", prId, 0).get(0);
		onViewModeScreenControl(request, prdRcp, sessionModel);
		
		//concurrent update 
		sessionModel.put("currentOptlock", prdRcp.getOptLock());
		
		sessionModel.put("currentRemarks", prdRcp.getRemarks());
		
		//for validation when edit
		sessionModel.put("tempPrCode", prdRcp.getPrCode());
		sessionModel.put("tempPrName", prdRcp.getPrName());
		//
		
		logger.info("viewPrdRcp() prId={}, prStatus={}, prLock={}, user={}, session={}", prId,
				prdRcp.getRecordStatus(), prdRcp.getOptLock(), request.getRemoteUser(), session.getId());

		UsrProfile user = userServ.searchUserProfile("", request.getRemoteUser().trim(), "", "", "", "",
				String.valueOf(CommonConstants.SEARCH_OPTION_CONTAIN), "", "").get(0);

		if (!user.getGroupId().equals(CommonConstants.GROUP_ID_FR)) {
			sessionModel.put("btnAction", true);
		}

		sessionModel.put("prId", prId);
		sessionModel.put("prodName", prdRcp.getPrName());
		sessionModel.put("productRecipeItems", prdRcp);
		sessionModel.put("prodRecipeStatus", prdRcp.getRecordStatus());
		sessionModel.put("creatorName", prdRcp.getCreatorId());

		if (!CommonConstants.SCREEN_MODE_EDIT.equals(screenMode)) {
			// FSGS) Kent 26/2/2021 Add/changed - Add Function executed log START
			trxHistServ.addTrxHistory(new Date(), "View Product Recipe", request.getRemoteUser(),
					CommonConstants.FUNCTION_TYPE_VIEW, String.valueOf(prId), CommonConstants.RECORD_TYPE_ID_PROD_RECP,
					null);
			// FSGS) Kent 26/2/2021 Add/changed - Add Function executed log END
		}

		if (prdRcp.getModifiedDate() == null) {
			sessionModel.put("dateAmendments", prdRcp.getCreatedDate());
		} else {
			sessionModel.put("dateAmendments", prdRcp.getModifiedDate());
		}

		try {
			String createdInfo = "Created by " + prdRcp.getCreatorId() + " on "
					+ formatter.format(prdRcp.getCreatedDate());
			sessionModel.put("createdInfo", createdInfo);
		} catch (Exception e) {
		}

		String modifiedInfo = "";
		try {
			modifiedInfo = "Modified by " + prdRcp.getModifierId() + " on "
					+ formatter.format(prdRcp.getModifiedDate());
		} catch (Exception e) {
			modifiedInfo = "Modified by --";
		}
		sessionModel.put("modifiedInfo", modifiedInfo);
		
		//set flavor status
		if(Objects.nonNull(prdRcp.getFlavStatusId()))
			prdRcp.setFlavStatusName(fsServ.findOneById(prdRcp.getFlavStatusId()).getFsName());

		List<PrIngredient> prIngList = prIngServ.searchPrIngredient(prId, 0, 0);
		prIngList.sort(Comparator.comparing(PrIngredient::getIngPerc, Collections.reverseOrder()));
		
		List<PrStat> prStatList = prStatServ.searchProductStatus(prId,
				String.valueOf(CommonConstants.SEARCH_OPTION_EXACT), "", 0);
		List<PrRmStat> prRmStatList = prRmServ.searchPrRmStat(prId, "");
		List<PrRmAdditive> prRmAddList = prRmAddServ.searchPrRmAdditive(prId);
		List<Document> docList = docServ.searchDocument(String.valueOf(prId), CommonConstants.RECORD_TYPE_ID_PROD_RECP);

		List<PrIngredient> ingDropDownList = new ArrayList<PrIngredient>();
		List<PrIngredient> prIngTable = new ArrayList<PrIngredient>();

		int ingRow = 0;
		for (PrIngredient prIng : prIngList) {
			PrIngredient prIngData = new PrIngredient();
			PrIngredient ingDrpDwn = new PrIngredient();
			prIngData.setPrIngRowNo(ingRow);
			prIngData.setPrIngId(prIng.getPrIngId());
			prIngData.setRefType(prIng.getRefType());
			prIngData.setRefId(prIng.getRefId());
			prIngData.setIngPerc(prIng.getIngPerc());
			prIngData.setIngStatus("exist");
			prIngData.setSelTsNo(prIng.getSelTsNo());

			if (prIng.getRefType() == CommonConstants.RECORD_TYPE_ID_RAW_MATL) {
				prIngData.setCategoryName("Raw Material");
				
				List<RawMaterial> rawMatlInfo = rawMatlServ.searchRawMaterial("", "", prIng.getRefId(), 0);
				if (rawMatlInfo.size() != 0) {
					prIngData.setIngredientName(rawMatlInfo.get(0).getRawMatlName());
					prIngData.setRmFlavStatusName(Objects.nonNull(rawMatlInfo.get(0).getFlavStatusId())
							? getFlavStatusName(rawMatlInfo.get(0).getFlavStatusId())
							: "");

					ingDrpDwn.setIngredientName(rawMatlInfo.get(0).getRawMatlName());
					ingDrpDwn.setRefId(rawMatlInfo.get(0).getRawMatlId());
					ingDrpDwn.setRefType(CommonConstants.RECORD_TYPE_ID_RAW_MATL);
					ingDropDownList.add(ingDrpDwn);
				}
			} else if (prIng.getRefType() == CommonConstants.RECORD_TYPE_ID_PROD_RECP) {
				prIngData.setCategoryName("Product Recipe");

				List<ProductRecipe> prodRecipeInfo = prdRcpServ.searchProductRecipe("", "", "", "", prIng.getRefId(),
						0);
				if (prodRecipeInfo.size() != 0) {
					prIngData.setIngredientName(prodRecipeInfo.get(0).getPrName());
					prIngData.setRmFlavStatusName(Objects.nonNull(prodRecipeInfo.get(0).getFlavStatusId())
							? getFlavStatusName(prodRecipeInfo.get(0).getFlavStatusId())
							: "");
				}
			}
			
			prIngData.setAltRm(prIng.getAltRm());
			prIngData.setAltRmNames(rawMatlServ.getRmNamesBasedOnMultiIds(prIng.getAltRm()));

			prIngTable.add(prIngData);
			ingRow++;
		}

		sessionModel.put("productCategory", CommonConstants.RECORD_TYPE_ID_RAW_MATL);
		sessionModel.put("prIngItems", prIngTable);
		sessionModel.put("ingDropDownItems", ingDropDownList);
		calProdSummary(prIngTable, sessionModel);

		List<PrStat> prStatTable = new ArrayList<PrStat>();
		int statRow = 0;
		for (PrStat prStat : prStatList) {
			PrStat prStatData = new PrStat();
			prStatData.setPrStatId(prStat.getPrStatId());
			prStatData.setPrStatRowNo(statRow);
			prStatData.setCountryId(prStat.getCountryId());
			prStatData.setCountryName(
					refCountryServ.searchCountry(prStat.getCountryId(), "", String.valueOf(CommonConstants.SEARCH_OPTION_EXACT), "", "", "")
							.get(0).getCountryName());
			prStatData.setFinalStatus(prStat.getFinalStatus());
			prStatData.setFinalStatusDesc(getFinalStatusDesc(prStat.getFinalStatus()));
			prStatData.setPrStatRecStatus("exist");
			prStatData.setRemarks(prStat.getRemarks());
			
			// uat2#72 - enhance impacted rm/country blink
			for (PrRmStat prRmStat : prRmStatList) {					
				if (prRmStat.getAffectedFlag().equals(String.valueOf(CommonConstants.FLAG_YES))) {
					if (prStatData.getCountryId().equals(prRmStat.getCountryId())) {
						prStatData.setAffectedFlag(String.valueOf(CommonConstants.FLAG_YES));
						break;
					}
				}
			}			
			//logger.debug("prStatData() affectedFlag={}, country={}", prStatData.getAffectedFlag(), prStatData.getCountryId() );

			prStatTable.add(prStatData);
			statRow++;
		}

		prStatTable.sort(Comparator.comparing(a -> a.getCountryName()));
		sessionModel.put("prStatItems", prStatTable);

		List<PrRmAdditive> prRmAddTable = new ArrayList<PrRmAdditive>();
		int prRmAddRow = 0;
		for (PrRmAdditive prRmAdd : prRmAddList) {
			PrRmAdditive prRmAddData = new PrRmAdditive();
			prRmAddData.setPrRmAddtID(prRmAdd.getPrRmAddtID());
			prRmAddData.setPrRmAdditiveRowNo(prRmAddRow);
			prRmAddData.setRawMatlId(prRmAdd.getRawMatlId());
			prRmAddData.setProductName(
					rawMatlServ.searchRawMaterial("", "", prRmAdd.getRawMatlId(), 0).get(0).getRawMatlName());
			prRmAddData.setAdditiveDesc(prRmAdd.getAdditiveDesc());
			prRmAddData.setPrRmAdditiveRecStatus("exist");
			prRmAddData.setChanged(false);

			prRmAddTable.add(prRmAddData);
			prRmAddRow++;
		}

		sessionModel.put("prRmAdditiveItems", prRmAddTable);

		List<String> affectedCountry = new ArrayList<String>();
		List<String> affectedMatl = new ArrayList<String>();
		List<PrRmStat> prRmStatTemp = new ArrayList<PrRmStat>();
		for (PrRmStat prRmStat : prRmStatList) {
			//logger.debug(prRmStat.getCountryId() + " | " + prRmStat.getRawMatlId() + " | " + prRmStat.getFinalStatus());

			PrRmStat prRmStatData = new PrRmStat();
			prRmStatData.setPrRmStatId(prRmStat.getPrRmStatId());
			prRmStatData.setCountryId(prRmStat.getCountryId());
			prRmStatData.setRawMatlId(prRmStat.getRawMatlId());
			prRmStatData.setSysFinalStatus(prRmStat.getSysFinalStatus());
			prRmStatData.setSysFinalStatusName(getFinalStatusDesc(prRmStat.getSysFinalStatus()));
			prRmStatData.setFinalStatus(prRmStat.getFinalStatus());
			prRmStatData.setRemarks(prRmStat.getRemarks());
			prRmStatData.setAffectedFlag(prRmStat.getAffectedFlag()); //uat2#72 20210602

			try {
				if (prRmStat.getAffectedFlag().equals(String.valueOf(CommonConstants.FLAG_YES))) {
					affectedCountry.add(refCountryServ.searchCountry(prRmStat.getCountryId(), "",
							String.valueOf(CommonConstants.SEARCH_OPTION_EXACT), "", "", "").get(0).getCountryName());
					affectedMatl.add(
							rawMatlServ.searchRawMaterial("", "", prRmStat.getRawMatlId(), 0).get(0).getRawMatlName());
				}
			} catch (Exception e) {
			}

			prRmStatTemp.add(prRmStatData);
		}

		if (affectedCountry.size() > 0 && affectedMatl.size() > 0) {
			affectedCountry = affectedCountry.stream().distinct().collect(Collectors.toList());
			affectedMatl = affectedMatl.stream().distinct().collect(Collectors.toList());
			
			String affectedMsg = "Country affected by Raw Material: <br />Country - "
					+ affectedCountry.toString() + "<br />Raw Material - " + affectedMatl.toString();

			sessionModel.put("affectedCountry", affectedMsg);
		}

		sessionModel.put("prRmStatTempItems", prRmStatTemp);

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

		List<PrRmReg> prRmRegItems = new ArrayList<PrRmReg>();
		List<PrRmReg> prRmRegList = prRmRegServ.searchPrRmReg(prId);
		for (PrRmReg prRmReg : prRmRegList) {
			PrRmReg prRmRegData = new PrRmReg();
			prRmRegData.setPrCode(prdRcp.getPrCode());
			prRmRegData.setRawMatlId(prRmReg.getRawMatlId());
			prRmRegData.setCountryId(prRmReg.getCountryId());
			prRmRegData.setRegDocId(prRmReg.getRegDocId());
			prRmRegItems.add(prRmRegData);
		}

		sessionModel.put("prRmRegItems", prRmRegItems);

		List<PrIngredient> prIngDel = new ArrayList<>();
		List<PrStat> prStatDel = new ArrayList<PrStat>();
		List<PrRmAdditive> prRmAddDel = new ArrayList<PrRmAdditive>();
		List<Document> docDel = new ArrayList<Document>();

		sessionModel.put("prIngDel", prIngDel);
		sessionModel.put("prStatDel", prStatDel);
		sessionModel.put("prRmAddDel", prRmAddDel);
		sessionModel.put("docDel", docDel);
		sessionModel.put("prRmRegItems", prRmRegItems);
		
		//refreshIngDropdownItemsList(sessionModel, prRmStatTemp, ingDropDownList);

		return new ModelAndView("/main/product/productForm", sessionModel);
	}

	private void onViewModeScreenControl(HttpServletRequest request, ProductRecipe pr,
			Map<String, Object> sessionModel) {

		sessionModel.put("screenMode", CommonConstants.SCREEN_MODE_VIEW);

		boolean authLvl1 = false;
		boolean authLvl2 = false;
		boolean authLvl3 = false;
		List<UsrRole> usrRoleList = usrRoleServ.searchUserRole(request.getRemoteUser());
		for (UsrRole usrRole : usrRoleList) {
			if (usrRole.getRoleId().equals(CommonConstants.ROLE_ID_HOD)) {
				authLvl3 = true;
			}

			if (usrRole.getRoleId().equals(CommonConstants.ROLE_ID_CHECKER)
					|| usrRole.getRoleId().equals(CommonConstants.ROLE_ID_SUPERUSER)) {
				authLvl2 = true;
			}

			if (usrRole.getRoleId().equals(CommonConstants.ROLE_ID_MAKER)) {
				authLvl1 = true;
			}
		}
		
		boolean isOnHold = false;
		if(StringUtils.isNotEmpty(pr.getOnHoldFlag())) {
			if(StringUtils.equals(pr.getOnHoldFlag(), String.valueOf(CommonConstants.FLAG_YES))) {
				isOnHold = true;
			}
		}
		
		if (pr.getRecordStatus() == RecordStatusEnum.DRAFT.intValue()) {
			// Checker or superuser or HOD
			if (authLvl2 || authLvl3) {
				sessionModel.put("btnEdit", false);
				sessionModel.put("btnSubmit", false);
				sessionModel.put("btnReject", false);
				sessionModel.put("btnRework", true);
				sessionModel.put("btnSaveSts", true);

			} else {
				// Maker only
				if (!authLvl2 && authLvl1) {
					if (pr.getCreatorId().equals(request.getRemoteUser())) {
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

		if (pr.getRecordStatus() == RecordStatusEnum.PEND_CONFIRM.intValue()
				|| pr.getRecordStatus() == RecordStatusEnum.CHG_PEND_CONFIRM.intValue()) {
			// HOD only
			if (authLvl3) {
				sessionModel.put("btnEdit", true);
				sessionModel.put("btnSubmit", false);
				sessionModel.put("btnReject", false);
				sessionModel.put("btnRework", false);
				sessionModel.put("btnSaveSts", true);
			} else {
				sessionModel.put("btnAction", true);
			}
		} else {
			if (pr.getRecordStatus() == RecordStatusEnum.PEND_AUTH.intValue()
					|| pr.getRecordStatus() == RecordStatusEnum.REWORK_PEND_AUTH.intValue()
					|| pr.getRecordStatus() == RecordStatusEnum.CHG_PEND_AUTH.intValue()
					|| pr.getRecordStatus() == RecordStatusEnum.CHG_REWORK.intValue()) {
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
		}

		if (pr.getRecordStatus() == RecordStatusEnum.SUBMITTED.intValue()) {
			sessionModel.put("btnAction", (isOnHold?true:false));
			sessionModel.put("btnEdit", (isOnHold?true:false));
			sessionModel.put("btnSubmit", true);
			sessionModel.put("btnReject", true);
			sessionModel.put("btnRework", true);
			sessionModel.put("btnSaveSts", true);

			// Maker only
			if (!authLvl2 && authLvl1) {
				sessionModel.put("btnAction", (isOnHold?true:false));
				sessionModel.put("btnEdit", (isOnHold?true:false));
				sessionModel.put("btnSubmit", true);
				sessionModel.put("btnReject", true);
				sessionModel.put("btnRework", true);
				sessionModel.put("btnSaveSts", true);
			}
		}

		if (pr.getRecordStatus() == RecordStatusEnum.REJECTED.intValue()) {
			sessionModel.put("btnAction", true);
		}

		if (pr.getRecordStatus() == RecordStatusEnum.REWORK.intValue()) {
			// HOD only
			if (authLvl3) {
				sessionModel.put("btnAction", true);
			} else {
				if (pr.getCreatorId().equals(request.getRemoteUser())) {
					sessionModel.put("btnEdit", false);
					sessionModel.put("btnSubmit", true);
					sessionModel.put("btnReject", true);
					sessionModel.put("btnRework", true);
					sessionModel.put("btnSaveSts", true);
					
				} else if (pr.getCreatorId().equals(CommonConstants.USER_ID_SYSTEM)) {
					String creatorId = "";
					Task task = taskServ.searchLatestTask(String.valueOf(pr.getPrId()),
							CommonConstants.RECORD_TYPE_ID_PROD_RECP, CommonConstants.ROLE_ID_CHECKER, 0,
							CommonConstants.ROLE_ID_MAKER, "");
					if (null != task)
						creatorId = task.getCreatorId();					

					if (StringUtils.isNotEmpty(creatorId) && creatorId.equals(request.getRemoteUser())) {
						sessionModel.put("btnEdit", false);
						sessionModel.put("btnSubmit", true);
						sessionModel.put("btnReject", true);
						sessionModel.put("btnRework", true);
						sessionModel.put("btnSaveSts", true);
						
					} else if (StringUtils.isEmpty(creatorId)){
						creatorId = taskServ.findCreatorIfSystem(pr.getPrId(), CommonConstants.RECORD_TYPE_ID_PROD_RECP, creatorId);
						if (StringUtils.isNotEmpty(creatorId)) {
							sessionModel.put("btnEdit", false);
							sessionModel.put("btnSubmit", true);
							sessionModel.put("btnReject", true);
							sessionModel.put("btnRework", true);
							sessionModel.put("btnSaveSts", true);
						} 
						
					} else {
						sessionModel.put("btnAction", true);						
					}

				} else {
					sessionModel.put("btnAction", true);
				}
			}
		}
	}

	private String getFinalStatusDesc(int finalStatus) {

		String finalStatusDesc = "";
		if (finalStatus == FinalStatusEnum.PERMITTED.intValue()) {
			finalStatusDesc = msgSource.getMessage(FinalStatusEnum.PERMITTED.strKey(), null, Locale.getDefault());
		} else if (finalStatus == FinalStatusEnum.NOT_PERMITTED.intValue()) {
			finalStatusDesc = msgSource.getMessage(FinalStatusEnum.NOT_PERMITTED.strKey(), null, Locale.getDefault());
		} else if (finalStatus == FinalStatusEnum.NOT_SURE.intValue()) {
			finalStatusDesc = msgSource.getMessage(FinalStatusEnum.NOT_SURE.strKey(), null, Locale.getDefault());
		} else if (finalStatus == FinalStatusEnum.YET_COMPELTE.intValue()) {
			finalStatusDesc = msgSource.getMessage(FinalStatusEnum.YET_COMPELTE.strKey(), null, Locale.getDefault());
		} else {
			finalStatusDesc = "";
		}

		return finalStatusDesc;
	}

	private Map<String, Object> setProductButton(boolean productAdd, boolean productUpdate, boolean productDelete,
			Map<String, Object> sessionModel) {
		sessionModel.put("productAdd", productAdd);
		sessionModel.put("productUpdate", productUpdate);
		sessionModel.put("productDelete", productDelete);

		return sessionModel;
	}

	private Map<String, Object> setPrStatButton(boolean prStatAdd, boolean prStatUpdate, boolean prStatDelete,
			Map<String, Object> sessionModel) {
		sessionModel.put("prStatAdd", prStatAdd);
		sessionModel.put("prStatUpdate", prStatUpdate);
		sessionModel.put("prStatDelete", prStatDelete);

		return sessionModel;
	}

	private Map<String, Object> setFoodAdditiveButton(boolean foodAdditiveAdd, boolean foodAdditiveUpdate,
			boolean foodAdditiveDelete, Map<String, Object> sessionModel) {
		sessionModel.put("foodAdditiveAdd", foodAdditiveAdd);
		sessionModel.put("foodAdditiveUpdate", foodAdditiveUpdate);
		sessionModel.put("foodAdditiveDelete", foodAdditiveDelete);

		return sessionModel;
	}

	private Map<String, Object> onPageLoadCommon(Map<String, Object> sessionModel) {
		sessionModel.remove("error");
		sessionModel.remove("success");
		sessionModel.put("rawMaterialItems", rawMatlServ.searchRawMaterial("", "", 0, RecordStatusEnum.SUBMITTED.intValue()));
		sessionModel.put("productItems", prdRcpServ.searchProductRecipe("", "", "", "", 0, RecordStatusEnum.SUBMITTED.intValue()));
		retrieveMatlDataModal("", "", "", "", "", "", "", "", sessionModel);
		searchProductRecipeModal("", "", "", "", "", "", "", "", sessionModel);
		sessionModel.put("rmSearchHeader", "Raw Materal Search Listing");
		sessionModel.put("prSearchHeader", "Product Recipe Search Listing");
		sessionModel.put("countryItems", refCountryServ.searchCountry("", "", "", "", String.valueOf(CommonConstants.FLAG_YES), ""));
		sessionModel.put("documentType", docTypeServ.listDocumentType());
		sessionModel.put("currTab", "productRecipe");
		sessionModel.put("regDocBlock", true);
		sessionModel.put("regDocBlockNotFound", true);

		return sessionModel;
	}

	@PostMapping("/main/product/productForm")
	public ModelAndView retrieveTsNo(@RequestParam(name = "rmName") int rmId, @RequestParam(name = "prdName") int prId,
			@RequestParam(name = "ingPercentage") String ingPercentage,
			@RequestParam(name = "prStatCountryId") String countryId, ProductRecipe prodRecipe, BindingResult result,
			HttpSession session) {

		sessionModel.remove("error");
		sessionModel.put("productRecipeItems", prodRecipe);
		
		logger.debug("retrieveRmTsNo rmis={}, prid={}",rmId,prId);

		if (rmId != 0) {
			sessionModel.put("productCategory", CommonConstants.RECORD_TYPE_ID_RAW_MATL);
			sessionModel.put("refId", rmId);
			sessionModel.put("ingPercentage", ingPercentage);
			sessionModel.put("screenPosition", "bottom");
			loadTsNoItems2(rmId, 0, sessionModel);
		}
		
		if (prId != 0) {
			sessionModel.put("productCategory", CommonConstants.RECORD_TYPE_ID_PROD_RECP);
			sessionModel.put("refId", prId);
			sessionModel.put("ingPercentage", ingPercentage);
			sessionModel.put("screenPosition", "bottom");
			loadTsNoItems2(0, prId, sessionModel);
		}

		if (!countryId.equals("")) {
			sessionModel.put("prStatCountryId", countryId);
			sessionModel.put("screenPosition", "bottom");
			updFinalStatus(countryId, sessionModel);
		}

		return new ModelAndView("/main/product/productForm", sessionModel);
	}

	private void updFinalStatus(String countryId, Map<String, Object> sessionModel) {

		List<PrRmStat> prRmStatTable = (List<PrRmStat>) sessionModel.get("prRmStatItems");

		for (PrRmStat prRmStat : prRmStatTable) {
			try {
				int rmFinalStatus = rmStatServ.getRmFinalStatus(prRmStat.getRawMatlId(), countryId, 0);

				prRmStat.setFinalStatus(rmFinalStatus);
				prRmStat.setSysFinalStatus(rmFinalStatus);
				prRmStat.setSysFinalStatusName(getFinalStatusDesc(rmFinalStatus));
			} catch (Exception e) {
				prRmStat.setFinalStatus(0);
				prRmStat.setSysFinalStatus(0);
				prRmStat.setSysFinalStatusName("");
			}
		}

		sessionModel.put("prRmStatItems", prRmStatTable);
	}

	@RequestMapping(value = "/main/product/productFormUpdTab", method = RequestMethod.GET)
	@ResponseBody
	public void updTab(@RequestParam(name = "tab") String currTab, HttpSession session) {

		sessionModel.put("currTab", currTab);
	}

	@Deprecated
	private void loadTsNoItems(int rmId, Map<String, Object> sessionModel) {

		List<RmManf> rmManfList = rmManfServ.searchRmManufacturer(rmId, "", "", "", "");
		List<RmManf> tsNoList = new ArrayList<RmManf>();

		for (RmManf rmManf : rmManfList) {
			boolean isFound = false;
			for (RmManf rm : tsNoList) {
				if (rm.getTsNo().equals(rmManf.getTsNo()) || (rm.equals(rmManf))) {
					isFound = true;
					break;
				}
			}

			if (!isFound) {
				tsNoList.add(rmManf);
			}
		}

		sessionModel.put("tsNoItems", tsNoList);
	}
	
	private void loadTsNoItems2(int rmId, int prId, Map<String, Object> sessionModel) {
		List<TsNoDto> tsNoList = rmManfServ.findAllTsNos(rmId, prId);
		tsNoList = tsNoList.stream().distinct().collect(Collectors.toList());
		sessionModel.put("tsNoItems", tsNoList);
	}

	@PostMapping(value = "/main/product/productForm", params = "action=edit")
	public ModelAndView editProductForm(HttpServletRequest request, HttpSession session) {

		sessionModel.remove("error");
		sessionModel.put("screenMode", CommonConstants.SCREEN_MODE_EDIT);
		sessionModel.put("currTab", "productRecipe");
		sessionModel.put("asterisk", false);
		sessionModel.put("header", "Edit Product Recipe");
		sessionModel.put("btnSave", "Save");
		sessionModel.remove("screenPosition");
		clearFoodAdditiveForm(sessionModel);
		clearProductRecipeForm(sessionModel);
		clearQAForm(sessionModel);
		clearAttachForm(sessionModel);
		sessionModel.remove("createdInfo");
		sessionModel.remove("modifiedInfo");

		onEditModeScreenControl(request, sessionModel);

		return new ModelAndView("/main/product/productForm", sessionModel);
	}

	private void onEditModeScreenControl(HttpServletRequest request, Map<String, Object> sessionModel) {

		int prStatus = (int) sessionModel.get("prodRecipeStatus");

		if (prStatus == RecordStatusEnum.DRAFT.intValue()) {
			sessionModel.put("btnEdit", true);
			sessionModel.put("btnSubmit", false);
			sessionModel.put("btnReject", true);
			sessionModel.put("btnRework", true);
			sessionModel.put("btnSaveSts", false);
		}

		if (prStatus == RecordStatusEnum.SUBMITTED.intValue() || prStatus == RecordStatusEnum.REWORK.intValue()) {
			sessionModel.put("btnEdit", true);
			sessionModel.put("btnSubmit", false);
			sessionModel.put("btnReject", true);
			sessionModel.put("btnRework", true);
			sessionModel.put("btnSaveSts", true);
		}

	}

	@PostMapping(value = "/main/product/productForm", params = "productAction=add")
	public ModelAndView addToProductList(HttpServletRequest request, @RequestParam(name = "productCat") int productCat,
			@RequestParam(name = "rmName") int rmId, @RequestParam(name = "prdName") int prId,
			@RequestParam(name = "rmTsNo") List<String> tsNo, @RequestParam(name = "altRm") List<String> altRmList,
			@RequestParam(name = "ingPercentage") String ingPercentage, ProductRecipe prodRecipe, BindingResult result,
			HttpSession session) {

		String rmTsNo = "";
		String altRmStr = "";
		PrIngredient prIngredient = new PrIngredient();
		PrIngredient ingDrpDwn = new PrIngredient();

		sessionModel.put("currTab", "productRecipe");
		sessionModel.put("productRecipeItems", prodRecipe);
		sessionModel.remove("screenPosition");

		List<PrIngredient> ingDropDownList = (List<PrIngredient>) sessionModel.get("ingDropDownItems");
		List<PrIngredient> prIngTable = (List<PrIngredient>) sessionModel.get("prIngItems");
		
		for (int i = 0; i < tsNo.size(); i++) {
			if (i == 0) {
				rmTsNo = tsNo.get(i);
			} else {
				rmTsNo = rmTsNo + "," + tsNo.get(i);
			}
		}
		
		if(!rmTsNo.isEmpty() && rmTsNo.startsWith(",")) rmTsNo = rmTsNo.substring(1);
		
		for(String alt : altRmList) {
			altRmStr = altRmStr + alt+",";
		}	
		
		if(StringUtils.isNotEmpty(altRmStr))
			altRmStr = altRmStr.substring(0, altRmStr.lastIndexOf(","));

		int rmPrId = 0;
		if (productCat == CommonConstants.RECORD_TYPE_ID_RAW_MATL) {
			rmPrId = rmId;

		} else if (productCat == CommonConstants.RECORD_TYPE_ID_PROD_RECP) {
			rmPrId = prId;
		}
		
		logger.debug("addToProductList() rmId={},prId={},tsNo={},rmPrId={}",rmId,prId,tsNo,rmPrId);

		if (errorCheckProductList(productCat, prodRecipe.getTotalPerc(), rmPrId, rmTsNo, ingPercentage, prIngTable,
				"insert", 0, sessionModel, prodRecipe, altRmStr)) {
			holdProdValue(productCat, rmPrId, rmTsNo, ingPercentage, sessionModel, altRmStr);
			return new ModelAndView("/main/product/productForm", sessionModel);
		}

		int row = prIngTable.size();
		if (row != 0 && duplicateCheckProductList(rmPrId, prIngTable, productCat, sessionModel)) {
			return new ModelAndView("/main/product/productForm", sessionModel);
		}

		prIngredient.setPrIngRowNo(row);
		prIngredient.setRefType(productCat);
		prIngredient.setRefId(rmPrId);
		prIngredient.setSelTsNo(rmTsNo);
		prIngredient.setAltRm(altRmStr);	
		prIngredient.setAltRmNames(rawMatlServ.getRmNamesBasedOnMultiIds(altRmStr));

		if (productCat == CommonConstants.RECORD_TYPE_ID_RAW_MATL) {
			prIngredient.setCategoryName("Raw Material");
			
			List<RawMaterial> rawMatlInfo = rawMatlServ.searchRawMaterial("", "", rmPrId, 0);
			if (rawMatlInfo.size() != 0) {
				prIngredient.setIngredientName(rawMatlInfo.get(0).getRawMatlName());
				prIngredient.setRmFlavStatusName(Objects.nonNull(rawMatlInfo.get(0).getFlavStatusId())
						? getFlavStatusName(rawMatlInfo.get(0).getFlavStatusId())
						: "");

				ingDrpDwn.setIngredientName(rawMatlInfo.get(0).getRawMatlName());
				ingDrpDwn.setRefId(rawMatlInfo.get(0).getRawMatlId());
				ingDrpDwn.setRefType(CommonConstants.RECORD_TYPE_ID_RAW_MATL);
				ingDropDownList.add(ingDrpDwn);
			}
		} else if (productCat == CommonConstants.RECORD_TYPE_ID_PROD_RECP) {
			prIngredient.setCategoryName("Product Recipe");
			
			List<ProductRecipe> prodRecipeInfo = prdRcpServ.searchProductRecipe("", "", "", "", rmPrId, 0);
			if (prodRecipeInfo.size() != 0) {
				prIngredient.setIngredientName(prodRecipeInfo.get(0).getPrName());
				prIngredient.setRmFlavStatusName(Objects.nonNull(prodRecipeInfo.get(0).getFlavStatusId())
						? getFlavStatusName(prodRecipeInfo.get(0).getFlavStatusId())
						: "");
			}
		}

		prIngredient.setIngPerc(Double.parseDouble(ingPercentage));
		prIngredient.setIngStatus("new");
		prIngTable.add(prIngredient);

		sessionModel.put("productCategory", CommonConstants.RECORD_TYPE_ID_RAW_MATL);
		sessionModel.put("prIngItems", prIngTable);
		sessionModel.put("ingDropDownItems", ingDropDownList);
		calProdSummary(prIngTable, sessionModel);
		clearProductRecipeForm(sessionModel);

		List<PrStat> prStatTable = (List<PrStat>) sessionModel.get("prStatItems");
		List<PrRmStat> prRmStatTable = (List<PrRmStat>) sessionModel.get("prRmStatItems");
		List<PrRmStat> prRmStatTempTable = (List<PrRmStat>) sessionModel.get("prRmStatTempItems");
		if (prRmStatTempTable.size() > 0) {
			for (PrRmStat prRmStat : prRmStatTable) {
				boolean found = false;

				for (PrRmStat prRmStatTemp : prRmStatTempTable) {
					if (prRmStat.getRawMatlId() == prRmStatTemp.getRawMatlId()) {
						found = true;
					}
				}

				if (!found) {
					for (PrStat prStat : prStatTable) {
						PrRmStat prRmStatData = new PrRmStat();
						prRmStatData.setRawMatlId(prRmStat.getRawMatlId());
						prRmStatData.setCountryId(prStat.getCountryId());
						prRmStatData.setFinalStatus(0);

						prRmStatTempTable.add(prRmStatData);
					}
				}
			}

			sessionModel.put("prRmStatTempItems", prRmStatTempTable);
		}
		
		//refreshIngDropdownItemsList(sessionModel, prRmStatTable, ingDropDownList);

		return new ModelAndView("/main/product/productForm", sessionModel);
	}

	@PostMapping(value = "/main/product/productForm", params = "productAction=update")
	public ModelAndView updateToProductList(HttpServletRequest request,
			@RequestParam(name = "productRowNo") int productRowNo, @RequestParam(name = "productCat") int productCat,
			@RequestParam(name = "rmName") int rmId, @RequestParam(name = "prdName") int prId,
			@RequestParam(name = "rmTsNo") List<String> tsNo, @RequestParam(name = "altRm") List<String> altRmList,
			@RequestParam(name = "ingPercentage") String ingPercentage, ProductRecipe prodRecipe, BindingResult result,
			HttpSession session) {

		sessionModel.put("currTab", "productRecipe");
		sessionModel.put("productRecipeItems", prodRecipe);
		sessionModel.remove("screenPosition");
		sessionModel.put("updateToProductList", true);

		List<PrIngredient> prIngTable = (List<PrIngredient>) sessionModel.get("prIngItems");
		String rmTsNo = "";
		String altRmStr = "";
		int rmPrId = 0;
		
		for (int i = 0; i < tsNo.size(); i++) {
			if (i == 0) {
				rmTsNo = tsNo.get(i);
			} else {
				rmTsNo = rmTsNo + "," + tsNo.get(i);
			}
		}
		
		if(!rmTsNo.isEmpty() && rmTsNo.startsWith(",")) rmTsNo = rmTsNo.substring(1);
		
		for(String alt : altRmList) {
			altRmStr = altRmStr + alt+",";
		}	
		
		if(StringUtils.isNotEmpty(altRmStr))
			altRmStr = altRmStr.substring(0, altRmStr.lastIndexOf(","));

		if (productCat == CommonConstants.RECORD_TYPE_ID_RAW_MATL) {
			rmPrId = rmId;

		} else if (productCat == CommonConstants.RECORD_TYPE_ID_PROD_RECP) {
			rmPrId = prId;
		}
		
		logger.debug("updateToProductList() rmId={},prId={},tsNo={},rmPrId={}",rmId,prId,tsNo,rmPrId);

		if (errorCheckProductList(productCat, prodRecipe.getTotalPerc(), rmPrId, rmTsNo, ingPercentage, prIngTable,
				"update", productRowNo, sessionModel, prodRecipe, altRmStr)) {
			holdProdValue(productCat, rmPrId, rmTsNo, ingPercentage, sessionModel, altRmStr);
			return new ModelAndView("/main/product/productForm", sessionModel);
		}

		Map<Integer, Integer> rmMap = new HashMap<Integer, Integer>();
		for (PrIngredient prInfo : prIngTable) {
			if (prInfo.getPrIngRowNo() == productRowNo) {
				
				if (productCat == CommonConstants.RECORD_TYPE_ID_RAW_MATL) 
					rmMap.put(prInfo.getRefId(), rmPrId);
				
				prInfo.setRefType(productCat);
				prInfo.setRefId(rmPrId);
				prInfo.setSelTsNo(rmTsNo);
				prInfo.setAltRm(altRmStr);
				prInfo.setAltRmNames(rawMatlServ.getRmNamesBasedOnMultiIds(altRmStr));

				if (productCat == CommonConstants.RECORD_TYPE_ID_RAW_MATL) {
					prInfo.setCategoryName("Raw Material");
					
					List<RawMaterial> rawMatlInfo = rawMatlServ.searchRawMaterial("", "", rmPrId, 0);
					if (rawMatlInfo.size() != 0) {
						prInfo.setIngredientName(rawMatlInfo.get(0).getRawMatlName());
						prInfo.setRmFlavStatusName(Objects.nonNull(rawMatlInfo.get(0).getFlavStatusId())
								? getFlavStatusName(rawMatlInfo.get(0).getFlavStatusId())
								: "");
					}
				} else if (productCat == CommonConstants.RECORD_TYPE_ID_PROD_RECP) {
					prInfo.setCategoryName("Product Recipe");
					
					List<ProductRecipe> prodRecipeInfo = prdRcpServ.searchProductRecipe("", "", "", "", rmPrId, 0);
					if (prodRecipeInfo.size() != 0) {
						prInfo.setIngredientName(prodRecipeInfo.get(0).getPrName());
						prInfo.setRmFlavStatusName(Objects.nonNull(prodRecipeInfo.get(0).getFlavStatusId())
								? getFlavStatusName(prodRecipeInfo.get(0).getFlavStatusId())
								: "");
					}
				}
				prInfo.setIngPerc(Double.parseDouble(ingPercentage));
			}
		}
		sessionModel.put("updateRmToProductList", rmMap);
		sessionModel.put("productCategory", CommonConstants.RECORD_TYPE_ID_RAW_MATL);
		sessionModel.put("prIngItems", prIngTable);
		setProductButton(false, true, true, sessionModel);
		calProdSummary(prIngTable, sessionModel);
		clearProductRecipeForm(sessionModel);

		return new ModelAndView("/main/product/productForm", sessionModel);
	}

	@PostMapping(value = "/main/product/productForm", params = "productAction=delete")
	public ModelAndView deleteProductList(HttpServletRequest request, @RequestParam(name = "productRowNo") int rowNo,
			ProductRecipe prodRecipe, BindingResult result, HttpSession session) {

		sessionModel.put("productRecipeItems", prodRecipe);
		sessionModel.remove("screenPosition");
		sessionModel.remove("error");
		List<PrIngredient> prIngTable = (List<PrIngredient>) sessionModel.get("prIngItems");
		List<PrRmAdditive> prRmAddTable = (List<PrRmAdditive>) sessionModel.get("prRmAdditiveItems");

		try {
			for (PrIngredient prIng : prIngTable) {
				if (prIng.getPrIngRowNo() == rowNo && prIng.getRefType() == CommonConstants.RECORD_TYPE_ID_RAW_MATL) {
					Iterator<PrRmAdditive> prRmAddIterator = prRmAddTable.iterator();
					while (prRmAddIterator.hasNext()) {
						if (prRmAddIterator.next().getRawMatlId() == prIng.getRefId()) {
							prRmAddIterator.remove();
						}
					}
				}
			}

			List<PrIngredient> prIngDel = (List<PrIngredient>) sessionModel.get("prIngDel");

			prIngDel.add(prIngTable.get(rowNo));
			prIngTable.remove(rowNo);

			int reCalRowNo = 0;
			for (int i = 0; i < prIngTable.size(); i++) {
				prIngTable.get(i).setPrIngRowNo(reCalRowNo);
				reCalRowNo++;
			}
		} catch (Exception e) {
			sessionModel.put("prIngItems", prIngTable);
		}

		sessionModel.put("productCategory", CommonConstants.RECORD_TYPE_ID_RAW_MATL);
		sessionModel.put("currTab", "productRecipe");
		sessionModel.put("prIngItems", prIngTable);
		setProductButton(false, true, true, sessionModel);
		calProdSummary(prIngTable, sessionModel);
		clearProductRecipeForm(sessionModel);

		return new ModelAndView("/main/product/productForm", sessionModel);
	}

	private boolean errorCheckProductList(int productCat, Double totalPercentage, int rmPrId, String rmTsNo,
			String ingPercent, List<PrIngredient> prIngTable, String mode, int productRowNo,
			Map<String, Object> sessionModel, ProductRecipe prodRecipe, String altRmStr) {

		sessionModel.remove("error");

		String errorMsg = "";
		errorMsg += commonValServ.validateMandatorySelect(rmPrId, "PM / PR Name");
		errorMsg += commonValServ.validateMandatoryInput(ingPercent, "Percentage %");

		//if (productCat == CommonConstants.RECORD_TYPE_ID_RAW_MATL) {
			errorMsg += commonValServ.validateMandatoryInput(rmTsNo, "TS No");
		//}

		if (totalPercentage == null) {
			errorMsg += "Total Percentage % is mandatory.<br />";
		} else if (totalPercentage < 0) {
			errorMsg += "Total Percentage % is less than 0.<br />";
		} else if (totalPercentage > 100) {
			errorMsg += "Total Percentage % is greater than 100.<br />";
		}

		if (!ingPercent.equals("") && totalPercentage != null) {
			double prPercentage = 0.0;

			if (prIngTable != null) {
				for (PrIngredient prIng : prIngTable) {
					if (mode.equals("insert")) {
						prPercentage += prIng.getIngPerc();
					} else {
						if (prIng.getPrIngRowNo() != productRowNo) {
							prPercentage += prIng.getIngPerc();
						}
					}					
					//logger.debug("calc() prPercentage={},getIngPerc={}",prPercentage,prIng.getIngPerc());
				}

				double ingPerc = Double.parseDouble(ingPercent);
				double total = prPercentage + ingPerc;
				
				DecimalFormat decimalFormat = new DecimalFormat("###.00");
				decimalFormat.setParseBigDecimal(true);
				String format = decimalFormat.format(total);
				
				double newTotal = Double.valueOf(format);
				
				logger.debug("errorCheckProductList() ingPerc={},total={},newTotal={}",ingPerc,total,newTotal);
				
				if (newTotal > 100) {
					sessionModel.put("error", "Percentage Ingredient is Over Limit.");
					return true;
				} else {
					prodRecipe.setTotalPerc(newTotal);
				}
			}
		}

		if (errorMsg.length() != 0) {
			sessionModel.put("error", errorMsg);
			return true;
		}

		return false;
	}

	private boolean duplicateCheckProductList(int rmPrId, List<PrIngredient> prIngTable, int productCat,
			Map<String, Object> sessionModel) {
		for (PrIngredient prIng : prIngTable) {
			if (rmPrId == prIng.getRefId() && productCat == prIng.getRefType()) {
				sessionModel.put("error", "Duplicate Raw Material or Product Recipe Data Found.");
				return true;
			}
		}
		return false;
	}

	private Map<String, Object> holdProdValue(int productCategory, int refId, String rmTsNo, String ingPercentage,
			Map<String, Object> sessionModel, String altRmStr) {
		sessionModel.put("productCategory", productCategory);
		sessionModel.put("refId", refId);
		sessionModel.put("rmTsNo", rmTsNo);
		sessionModel.put("altRm", altRmStr);
		sessionModel.put("ingPercentage", ingPercentage);

		return sessionModel;
	}

	private Map<String, Object> clearProductRecipeForm(Map<String, Object> sessionModel) {
		sessionModel.remove("productRowNo");
		sessionModel.put("productCategory", CommonConstants.RECORD_TYPE_ID_RAW_MATL);
		sessionModel.remove("refId");
		sessionModel.remove("rmTsNo");
		sessionModel.remove("ingPercentage");
		sessionModel.remove("tsNoItems");
		sessionModel.remove("updateRmToProductList");
		sessionModel.remove("altRm");

		return sessionModel;
	}

	@GetMapping("/main/product/productFormPRGetData")
	public ModelAndView retrieveProductPRFormData(HttpServletRequest request, @RequestParam(name = "id") int id,
			HttpSession session) {

		String screenMode = sessionModel.get("screenMode").toString();

		sessionModel.remove("error");
		sessionModel.put("screenPosition", "bottom");
		List<PrIngredient> prIngTable = (List<PrIngredient>) sessionModel.get("prIngItems");

		for (PrIngredient prIng : prIngTable) {
			if (prIng.getPrIngRowNo() == id) {
				sessionModel.put("productRowNo", prIng.getPrIngRowNo());
				if (prIng.getRefType() == CommonConstants.RECORD_TYPE_ID_RAW_MATL) {
					sessionModel.put("productCategory", CommonConstants.RECORD_TYPE_ID_RAW_MATL);
					sessionModel.put("rmTsNo", prIng.getSelTsNo());
					sessionModel.put("altRm", prIng.getAltRm());

					List<RawMaterial> rawMatlInfo = rawMatlServ.searchRawMaterial("", "", prIng.getRefId(), 0);
					if (rawMatlInfo.size() != 0) {
						sessionModel.put("refId", rawMatlInfo.get(0).getRawMatlId());
					}

					List<String> tsNoList = Arrays.asList(prIng.getSelTsNo().split(","));
					sessionModel.put("rmTsNo", tsNoList);

					List<String> altRmList = prIng.getAltRm() != null ? Arrays.asList(prIng.getAltRm().split(","))
							: new ArrayList<String>();
					
					sessionModel.put("altRm", altRmList);
					sessionModel.put("altRmNamesTxt", rawMatlServ.getRmNamesBasedOnMultiIds(prIng.getAltRm()));

					loadTsNoItems2(prIng.getRefId(), 0, sessionModel);

				} else if (prIng.getRefType() == CommonConstants.RECORD_TYPE_ID_PROD_RECP) {
					sessionModel.put("productCategory", CommonConstants.RECORD_TYPE_ID_PROD_RECP);
					List<ProductRecipe> prodRecipeInfo = prdRcpServ.searchProductRecipe("", "", "", "",
							prIng.getRefId(), 0);
					if (prodRecipeInfo.size() != 0) {
						sessionModel.put("refId", prodRecipeInfo.get(0).getPrId());
					}
					
					List<String> tsNoList = Arrays.asList(prIng.getSelTsNo().split(","));
					sessionModel.put("rmTsNo", tsNoList);
										
					loadTsNoItems2(0, prIng.getRefId(), sessionModel);
				}
				sessionModel.put("ingPercentage", prIng.getIngPerc());
				sessionModel.put("tsNosTxt",StringUtils.replace(prIng.getSelTsNo(), ",", "<br />"));
			}
		}

		if (!screenMode.equals(CommonConstants.SCREEN_MODE_VIEW)) {
			setProductButton(false, false, false, sessionModel);
		}
		sessionModel.put("currTab", "productRecipe");

		return new ModelAndView("/main/product/productForm", sessionModel);
	}

	private void calProdSummary(List<PrIngredient> prIngTable, Map<String, Object> sessionModel) {

		List<PrIngredient> prIngSumTable = (List<PrIngredient>) sessionModel.get("ingSummaryItems");
		prIngSumTable = new ArrayList<PrIngredient>();
		String space = "";

		for (PrIngredient prIng : prIngTable) {
			PrIngredient ingSum = new PrIngredient();
			ingSum.setRefId(prIng.getRefId());
			ingSum.setRefType(prIng.getRefType());
			ingSum.setIngredientName(prIng.getIngredientName());
			ingSum.setIngPerc(prIng.getIngPerc());
			ingSum.setSelTsNo(prIng.getSelTsNo());

			if (prIng.getRefType() == CommonConstants.RECORD_TYPE_ID_RAW_MATL) {
				RawMaterial rmInfo = rawMatlServ.searchRawMaterial("", "", prIng.getRefId(), 0).get(0);
				ingSum.setCategoryName("Raw Material");
				ingSum.setAltRm(prIng.getAltRm());
				ingSum.setAltRmNames(rawMatlServ.getRmNamesBasedOnMultiIds(prIng.getAltRm()));
				ingSum.setRmFlavStatusName(
						Objects.nonNull(rmInfo.getFlavStatusId()) ? getFlavStatusName(rmInfo.getFlavStatusId()) : "");
				prIngSumTable.add(ingSum);

			} else if (prIng.getRefType() == CommonConstants.RECORD_TYPE_ID_PROD_RECP) {
				ProductRecipe prInfo = prdRcpServ.searchProductRecipe("", "", "", "", prIng.getRefId(), 0).get(0);
				ingSum.setCategoryName("Product Recipe");
				ingSum.setRmFlavStatusName(
						Objects.nonNull(prInfo.getFlavStatusId()) ? getFlavStatusName(prInfo.getFlavStatusId()) : "");
				prIngSumTable.add(ingSum);

				getDetailRecipeProduct(prIng.getRefId(), ingSum, prIngSumTable, prIng.getIngPerc(), space);
			}
		}

		sessionModel.put("ingSummaryItems", prIngSumTable);
		calFinalCheckIng(prIngSumTable, sessionModel);
	}

	private void getDetailRecipeProduct(int refId, PrIngredient ingSum, List<PrIngredient> prIngSumTable,
			double ingPercent, String space) {

		List<PrIngredient> prIngMatlList = prIngServ.searchPrIngredient(refId, 0, 0);
		space = space + "&emsp;&emsp;";

		for (PrIngredient prIngMatl : prIngMatlList) {
			double ingPerc = 0;
			ingSum = new PrIngredient();
			ingSum.setRefId(prIngMatl.getRefId());
			ingSum.setRefType(prIngMatl.getRefType());
			ingSum.setSelTsNo(prIngMatl.getSelTsNo());

			ingPerc = (ingPercent / 100) * prIngMatl.getIngPerc();
			ingSum.setIngPerc(ingPerc);

			if (prIngMatl.getRefType() == CommonConstants.RECORD_TYPE_ID_RAW_MATL) {
				RawMaterial rmInfo = rawMatlServ.searchRawMaterial("", "", prIngMatl.getRefId(), 0).get(0);
				ingSum.setCategoryName("Raw Material");
				ingSum.setIngredientName(
						space + rmInfo.getRawMatlName());
				ingSum.setAltRm(prIngMatl.getAltRm());
				ingSum.setAltRmNames(rawMatlServ.getRmNamesBasedOnMultiIds(prIngMatl.getAltRm()));
				ingSum.setRmFlavStatusName(space + (
						Objects.nonNull(rmInfo.getFlavStatusId()) ? getFlavStatusName(rmInfo.getFlavStatusId()) : ""));

				prIngSumTable.add(ingSum);

			} else if (prIngMatl.getRefType() == CommonConstants.RECORD_TYPE_ID_PROD_RECP) {
				ProductRecipe prInfo = prdRcpServ.searchProductRecipe("", "", "", "", prIngMatl.getRefId(), 0).get(0);
				ingSum.setCategoryName("Product Recipe");
				ingSum.setIngredientName(space+ prInfo.getPrName());
				ingSum.setRmFlavStatusName(space+ (
						Objects.nonNull(prInfo.getFlavStatusId()) ? getFlavStatusName(prInfo.getFlavStatusId()) : ""));

				prIngSumTable.add(ingSum);

				getDetailRecipeProduct(prIngMatl.getRefId(), ingSum, prIngSumTable, ingPerc, space);
			}
		}
	}

	private void calFinalCheckIng(List<PrIngredient> prIngSumTable, Map<String, Object> sessionModel) {

		List<PrRmStat> prRmStatTable = (List<PrRmStat>) sessionModel.get("prRmStatItems");
		List<PrIngredient> prIngTable = (List<PrIngredient>) sessionModel.get("prIngItems");
		prRmStatTable = new ArrayList<PrRmStat>();

		for (PrIngredient prIng : prIngSumTable) {
			boolean updateValue = false;

			if (prIng.getRefType() == CommonConstants.RECORD_TYPE_ID_RAW_MATL) {
				if (prRmStatTable.size() > 0) {
					for (PrRmStat prStat : prRmStatTable) {
						if (prStat.getRawMatlId() == prIng.getRefId()) {
							prStat.setTotalPerc(prStat.getTotalPerc() + prIng.getIngPerc());

							updateValue = true;
						}
					}
				}

				if (!updateValue) {
					PrRmStat prRmStat = new PrRmStat();
					prRmStat.setRawMatlId(prIng.getRefId());
					prRmStat.setProductName(
							rawMatlServ.searchRawMaterial("", "", prIng.getRefId(), 0).get(0).getRawMatlName());
					prRmStat.setTotalPerc(prIng.getIngPerc());

					// FSGS) KarVei Add 26/02/2021 - Check if ingredient has more than 1 TS No START
					for (PrIngredient prIngTs : prIngTable) {
						if (prIngTs.getRefId() == prIng.getRefId() && prIngTs.getRefType() == CommonConstants.RECORD_TYPE_ID_RAW_MATL) {
							if (prIngTs.getSelTsNo().contains(",")) {
								prRmStat.setTSNo(true);
							}
						}
					}
					// FSGS) KarVei Add 26/02/2021 - Check if ingredient has more than 1 TS No END

					prRmStatTable.add(prRmStat);
				}
			}
		}
		
		{
			//checking if rawMatlId changes to new one
			//check pr_rm_stat, update rawMatlId
			//check pr_rm_additive, update rawMatlId
			Map<Integer, Integer> rmMap = (Map<Integer, Integer>)sessionModel.get("updateRmToProductList");
			List<PrRmStat> prRmStatTempTable = (List<PrRmStat>) sessionModel.get("prRmStatTempItems");
			List<PrRmAdditive> prRmAdditiveTable = (List<PrRmAdditive>) sessionModel.get("prRmAdditiveItems");
			List<PrIngredient> ingDropDownList = (List<PrIngredient>) sessionModel.get("ingDropDownItems");
			
			if (rmMap != null && rmMap.size() > 0) {
				for (Map.Entry<Integer, Integer> entry : rmMap.entrySet()) {

					for (PrRmStat prRmStat : prRmStatTempTable) {
						if (prRmStat.getRawMatlId() == entry.getKey().intValue()
								&& prRmStat.getRawMatlId() != entry.getValue().intValue()) {
							prRmStat.setRawMatlId(entry.getValue());
							prRmStat.setRmRefChanged(entry.getKey());
							prRmStat.setFinalStatus(0);
						}
					}

					for (PrRmAdditive prRmAdditive : prRmAdditiveTable) {
						if (prRmAdditive.getRawMatlId().intValue() == entry.getKey().intValue()
								&& prRmAdditive.getRawMatlId().intValue() != entry.getValue().intValue()) {

							// Update dropdown for Food Additive tab
							//ingDropDownList.removeIf(e -> (e.getRefId() == prRmAdditive.getRawMatlId().intValue()
							//		&& e.getRefType() == CommonConstants.RECORD_TYPE_ID_RAW_MATL));

							// Update Food Additive list for Food Additive tab
							prRmAdditive.setRawMatlId(entry.getValue());
							prRmAdditive.setProductName(rawMatlServ
									.searchRawMaterial("", "", prRmAdditive.getRawMatlId(), 0).get(0).getRawMatlName());
							prRmAdditive.setChanged(true);
						}
					}

				}
				//sessionModel.put("ingDropDownItems", ingDropDownList);
				sessionModel.put("prRmStatTempItems", prRmStatTempTable);
				sessionModel.put("prRmAdditiveItems", prRmAdditiveTable);
			} else {
				//Compare rm_id in both prRmStatTable and prRmAdditiveTable
				//Put a mark in rm in prRmAdditiveTable having rm which is not same as in prRmStatTable
				
				if (null != prRmAdditiveTable) {

					for (PrRmAdditive prRmAdditive : prRmAdditiveTable) {
						for (PrRmStat prStat : prRmStatTable) {
							if (prRmAdditive.getRawMatlId() == prStat.getRawMatlId())
								continue;

							prRmAdditive.setChanged(true);
						}

					}
					sessionModel.put("prRmAdditiveItems", prRmAdditiveTable);
				}
			}
			refreshIngDropdownItemsList(sessionModel, prRmStatTable, ingDropDownList);
			
		}

		sessionModel.put("prRmStatItems", prRmStatTable);
	}

	private void refreshIngDropdownItemsList(Map<String, Object> sessionModel, List<PrRmStat> prRmStatTempTable,
			List<PrIngredient> ingDropDownList) {
		{
			// 20220310
			// move the entire logic to update/refresh ingDropDownList for pr-rm-additive
			// usage here
			// checking need to be done based on latest RM list in pr-rm-stats
			ingDropDownList.clear();
			for (PrRmStat prRmStat : prRmStatTempTable) {

				// Update dropdown for Food Additive tab
//				ingDropDownList.removeIf(e -> (e.getRefId() == prRmStat.getRawMatlId()
//						&& e.getRefType() == CommonConstants.RECORD_TYPE_ID_RAW_MATL));

				List<RawMaterial> rawMatlInfo = rawMatlServ.searchRawMaterial("", "", prRmStat.getRawMatlId(), 0);
				PrIngredient ingDrpDwn = new PrIngredient();
				if (rawMatlInfo.size() != 0) {
					ingDrpDwn.setIngredientName(rawMatlInfo.get(0).getRawMatlName());
					ingDrpDwn.setRefId(rawMatlInfo.get(0).getRawMatlId());
					ingDrpDwn.setRefType(CommonConstants.RECORD_TYPE_ID_RAW_MATL);
					ingDropDownList.add(ingDrpDwn);
				}
			}

			sessionModel.put("ingDropDownItems", ingDropDownList);
		}
	}

	@PostMapping(value = "/main/product/productForm", params = "prStatAction=add")
	public ModelAndView addToFinalCheckList(HttpServletRequest request,
			@RequestParam(name = "prStatCountryId") String prStatCountryId,
			@RequestParam(name = "prAutoStatus") String prAutoStatus,
			@RequestParam(name = "finalStatus", required = false) int[] finalStatus,
			@RequestParam(name = "remarksQaFinalChecker", required = false, defaultValue = " ") String[] remarks,
			ProductRecipe prodRecipe, BindingResult result, HttpSession session) {

		sessionModel.put("currTab", "qaFinalChecker");
		sessionModel.put("productRecipeItems", prodRecipe);
		sessionModel.remove("screenPosition");

		if (errorCheckQAList(prStatCountryId, finalStatus, prAutoStatus, sessionModel)) {
			holdQAValue(prStatCountryId, prAutoStatus, sessionModel);
			return new ModelAndView("/main/product/productForm", sessionModel);
		}

		List<PrStat> prStatTable = (List<PrStat>) sessionModel.get("prStatItems");

		int row = prStatTable.size();
		if (row != 0 && duplicateCheckQAList(prStatCountryId, prStatTable, sessionModel)) {
			return new ModelAndView("/main/product/productForm", sessionModel);
		}

		PrStat prStat = new PrStat();
		prStat.setPrStatRowNo(row);
		prStat.setCountryId(prStatCountryId);
		prStat.setCountryName(
				refCountryServ.searchCountry(prStatCountryId, "", String.valueOf(CommonConstants.SEARCH_OPTION_EXACT), "", "", "").get(0)
						.getCountryName());
		prStat.setFinalStatus(getFinalStatus(prAutoStatus));
		prStat.setFinalStatusDesc(prAutoStatus);
		prStat.setPrStatRecStatus("new");

		prStatTable.add(prStat);
		prStatTable.sort(Comparator.comparing(a -> a.getCountryName()));
		sessionModel.put("prStatItems", prStatTable);

		List<PrRmStat> prRmStatTable = (List<PrRmStat>) sessionModel.get("prRmStatItems");
		List<PrRmStat> prRmStatTempTable = (List<PrRmStat>) sessionModel.get("prRmStatTempItems");
		for (int i = 0; i < prRmStatTable.size(); i++) {
			PrRmStat prRmStatData = new PrRmStat();
			prRmStatData.setRawMatlId(prRmStatTable.get(i).getRawMatlId());
			prRmStatData.setCountryId(prStatCountryId);
			prRmStatData.setSysFinalStatus(prRmStatTable.get(i).getSysFinalStatus());
			prRmStatData.setFinalStatus(finalStatus[i]);
			prRmStatData.setRemarks(remarks[i]);

			prRmStatTempTable.add(prRmStatData);
		}

		sessionModel.put("prRmStatTempItems", prRmStatTempTable);

		setPrStatButton(false, true, true, sessionModel);
		clearQAForm(sessionModel);

		return new ModelAndView("/main/product/productForm", sessionModel);
	}

	@PostMapping(value = "/main/product/productForm", params = "prStatAction=update")
	public ModelAndView updateToFinalCheckList(HttpServletRequest request,
			@RequestParam(name = "prStatRowNo") int prStatRowNo,
			@RequestParam(name = "prStatCountryId") String prStatCountryId,
			@RequestParam(name = "prAutoStatus") String prAutoStatus,
			@RequestParam(name = "finalStatus", required = false) int[] finalStatus,
			@RequestParam(name = "remarksQaFinalChecker", required = false) String[] remarks, ProductRecipe prodRecipe,
			BindingResult result, HttpSession session) {

		sessionModel.put("currTab", "qaFinalChecker");
		sessionModel.put("productRecipeItems", prodRecipe);
		sessionModel.remove("screenPosition");

		if (errorCheckQAList(prStatCountryId, finalStatus, prAutoStatus, sessionModel)) {
			holdQAValue(prStatCountryId, prAutoStatus, sessionModel);
			return new ModelAndView("/main/product/productForm", sessionModel);
		}

		List<PrStat> prStatTable = (List<PrStat>) sessionModel.get("prStatItems");

		for (PrStat prStat : prStatTable) {
			if (prStat.getPrStatRowNo() == prStatRowNo) {
				prStat.setCountryId(prStatCountryId);
				prStat.setCountryName(
						refCountryServ.searchCountry(prStatCountryId, "", String.valueOf(CommonConstants.SEARCH_OPTION_EXACT), "", "", "")
								.get(0).getCountryName());
				prStat.setFinalStatus(getFinalStatus(prAutoStatus));
				prStat.setFinalStatusDesc(prAutoStatus);
			}
		}

		prStatTable.sort(Comparator.comparing(a -> a.getCountryName()));
		sessionModel.put("prStatItems", prStatTable);

		List<PrRmStat> prRmStatTable = (List<PrRmStat>) sessionModel.get("prRmStatItems");
		List<PrRmStat> prRmStatTempTable = (List<PrRmStat>) sessionModel.get("prRmStatTempItems");
		for (int i = 0; i < prRmStatTable.size(); i++) {//ui
			for (PrRmStat prRmStat : prRmStatTempTable) {//db
				if (prRmStat.getRawMatlId() == prRmStatTable.get(i).getRawMatlId()
						&& prRmStat.getCountryId().equals(prStatCountryId)) {
					prRmStat.setFinalStatus(finalStatus[i]);
					if (remarks.length != 0) {
						prRmStat.setRemarks(remarks[i]);
					}
				}
			}
		}

		sessionModel.put("prRmStatTempItems", prRmStatTempTable);

		setPrStatButton(false, true, true, sessionModel);
		clearQAForm(sessionModel);

		return new ModelAndView("/main/product/productForm", sessionModel);
	}

	@PostMapping(value = "/main/product/productForm", params = "prStatAction=delete")
	public ModelAndView deleteToFinalCheckList(HttpServletRequest request,
			@RequestParam(name = "prStatRowNo") int rowNo, ProductRecipe prodRecipe, BindingResult result,
			HttpSession session) {

		sessionModel.put("productRecipeItems", prodRecipe);
		sessionModel.put("currTab", "qaFinalChecker");
		sessionModel.remove("screenPosition");
		sessionModel.remove("error");
		List<PrStat> prStatTable = (List<PrStat>) sessionModel.get("prStatItems");
		List<PrRmStat> prRmStatTempTable = (List<PrRmStat>) sessionModel.get("prRmStatTempItems");

		try {
			String delCountry = "";
			for (PrStat prStat : prStatTable) {
				if (prStat.getPrStatRowNo() == rowNo) {
					delCountry = prStat.getCountryId();
				}
			}

			Iterator<PrRmStat> prRmStatIterator = prRmStatTempTable.iterator();
			while (prRmStatIterator.hasNext()) {
				if (prRmStatIterator.next().getCountryId().equals(delCountry)) {
					prRmStatIterator.remove();
				}
			}

			List<PrStat> prStatDel = (List<PrStat>) sessionModel.get("prStatDel");

			prStatDel.add(prStatTable.get(rowNo));
			prStatTable.remove(rowNo);

			int reCalRowNo = 0;
			for (int i = 0; i < prStatTable.size(); i++) {
				prStatTable.get(i).setPrStatRowNo(reCalRowNo);
				reCalRowNo++;
			}
		} catch (Exception e) {
			sessionModel.put("prStatItems", prStatTable);
		}

		prStatTable.sort(Comparator.comparing(a -> a.getCountryName()));
		sessionModel.put("prStatItems", prStatTable);
		setPrStatButton(false, true, true, sessionModel);
		clearQAForm(sessionModel);

		return new ModelAndView("/main/product/productForm", sessionModel);
	}

	@GetMapping("/main/product/productFormFCGetData")
	public ModelAndView retrieveProductFCFormData(HttpServletRequest request, @RequestParam(name = "id") int id,
			HttpSession session) {

		String screenMode = sessionModel.get("screenMode").toString();

		sessionModel.remove("error");
		sessionModel.remove("screenPosition");
		List<PrStat> prStatTable = (List<PrStat>) sessionModel.get("prStatItems");

		for (PrStat prStat : prStatTable) {
			if (prStat.getPrStatRowNo() == id) {
				sessionModel.put("prStatRowNo", prStat.getPrStatRowNo());
				sessionModel.put("prStatCountryId", prStat.getCountryId());
				sessionModel.put("prAutoStatus", prStat.getFinalStatusDesc());

				List<PrRmStat> prRmStatTable = (List<PrRmStat>) sessionModel.get("prRmStatItems");
				List<PrRmStat> prRmStatTempTable = (List<PrRmStat>) sessionModel.get("prRmStatTempItems");
				for (PrRmStat prRmStat : prRmStatTable) {
					try {
						int rmFinalStatus = rmStatServ.getRmFinalStatus(prRmStat.getRawMatlId(), prStat.getCountryId(),
								0);
						prRmStat.setSysFinalStatus(rmFinalStatus);
						prRmStat.setSysFinalStatusName(getFinalStatusDesc(rmFinalStatus));
					} catch (Exception e) {
						prRmStat.setSysFinalStatus(0);
						prRmStat.setSysFinalStatusName("");
					}

					for (PrRmStat prRmStatTemp : prRmStatTempTable) {
						if (prRmStatTemp.getRawMatlId() == prRmStat.getRawMatlId()
								&& prRmStatTemp.getCountryId().equals(prStat.getCountryId())) {
							//logger.debug(prRmStatTemp.getRawMatlId() + " | " + prStat.getCountryId());

							prRmStat.setFinalStatus(prRmStatTemp.getFinalStatus());
							prRmStat.setRemarks(prRmStatTemp.getRemarks());
							
							// uat2#72 - enhance impacted rm/country blink
							if (null != prRmStatTemp.getAffectedFlag() && prRmStatTemp.getAffectedFlag()
									.equals(String.valueOf(CommonConstants.FLAG_YES))) {
								prRmStat.setAffectedFlag(prRmStatTemp.getAffectedFlag()); 																							
								prStat.setAffectedFlag(prRmStatTemp.getAffectedFlag());

							}
						}
					}
				}

				sessionModel.put("prRmStatItems", prRmStatTable);
			}
		}

		if (!screenMode.equals(CommonConstants.SCREEN_MODE_VIEW)) {
			setPrStatButton(false, false, false, sessionModel);
		}
		sessionModel.put("currTab", "qaFinalChecker");

		return new ModelAndView("/main/product/productForm", sessionModel);
	}

	private boolean duplicateCheckQAList(String prStatCountryId, List<PrStat> prStatTable,
			Map<String, Object> sessionModel) {

		for (PrStat prStat : prStatTable) {
			if (prStatCountryId.equals(prStat.getCountryId())) {
				sessionModel.put("error", "Country existed in the list.");
				return true;
			}
		}

		return false;
	}

	private boolean errorCheckQAList(String prStatCountryId, int[] finalStatus, String prAutoStatus,
			Map<String, Object> sessionModel) {

		sessionModel.remove("error");

		String errorMsg = "";
		errorMsg += commonValServ.validateMandatoryInput(prStatCountryId, "Country");
		errorMsg += commonValServ.validateMandatoryInput(prAutoStatus, "Final Permissibility Status");

		boolean finalStatusErr = false;

		if (finalStatus == null) {
			finalStatusErr = true;
		} else {
			for (int i = 0; i < finalStatus.length; i++) {
				if (finalStatus[i] == 0) {
					finalStatusErr = true;
				}
			}
		}

		if (finalStatusErr) {
			errorMsg += "(NEW) Final Status is mandatory.<br />";
		}

		if (errorMsg.length() != 0) {
			sessionModel.put("error", errorMsg);
			return true;
		}

		return false;
	}

	private int getFinalStatus(String prAutoStatus) {

		int finalStatus = 0;
		if (prAutoStatus.equals(msgSource.getMessage(FinalStatusEnum.PERMITTED.strKey(), null, Locale.getDefault()))) {
			finalStatus = FinalStatusEnum.PERMITTED.intValue();
		} else if (prAutoStatus
				.equals(msgSource.getMessage(FinalStatusEnum.NOT_PERMITTED.strKey(), null, Locale.getDefault()))) {
			finalStatus = FinalStatusEnum.NOT_PERMITTED.intValue();
		} else if (prAutoStatus
				.equals(msgSource.getMessage(FinalStatusEnum.NOT_SURE.strKey(), null, Locale.getDefault()))) {
			finalStatus = FinalStatusEnum.NOT_SURE.intValue();
		} else if (prAutoStatus
				.equals(msgSource.getMessage(FinalStatusEnum.YET_COMPELTE.strKey(), null, Locale.getDefault()))) {
			finalStatus = FinalStatusEnum.YET_COMPELTE.intValue();
		}

		return finalStatus;
	}

	private Map<String, Object> holdQAValue(String prStatCountryId, String prAutoStatus,
			Map<String, Object> sessionModel) {

		sessionModel.put("prStatCountryId", prStatCountryId);
		sessionModel.put("prAutoStatus", prAutoStatus);

		return sessionModel;
	}

	private Map<String, Object> clearQAForm(Map<String, Object> sessionModel) {

		sessionModel.remove("prStatCountryId");
		sessionModel.remove("prAutoStatus");

		List<PrRmStat> prRmStatTable = (List<PrRmStat>) sessionModel.get("prRmStatItems");
		if (prRmStatTable.size() > 0) {
			for (PrRmStat prRmStat : prRmStatTable) {
				prRmStat.setSysFinalStatusName(null);
				prRmStat.setFinalStatus(0);
				prRmStat.setRemarks(null);
			}
		}

		return sessionModel;
	}

	@PostMapping(value = "/main/product/productForm", params = "foodAdditiveAction=add")
	public ModelAndView addToFoodAdditiveList(HttpServletRequest request,
			@RequestParam(name = "ingredientName") int refId,
			@RequestParam(name = "foodAdditiveFunc") String foodAdditiveFunc, ProductRecipe prodRecipe,
			BindingResult result, HttpSession session) {

		sessionModel.put("currTab", "foodAdditive");
		sessionModel.put("productRecipeItems", prodRecipe);

		if (errorCheckFoodAdditiveList(refId, foodAdditiveFunc, sessionModel)) {
			holdFoodAdditiveValue(refId, foodAdditiveFunc, sessionModel);
			return new ModelAndView("/main/product/productForm", sessionModel);
		}

		List<PrRmAdditive> prRmAdditiveTable = (List<PrRmAdditive>) sessionModel.get("prRmAdditiveItems");

		int row = prRmAdditiveTable.size();
		if (row != 0 && duplicateCheckFoodAdditiveList(refId, prRmAdditiveTable, sessionModel)) {
			return new ModelAndView("/main/product/productForm", sessionModel);
		}

		PrRmAdditive additiveInfo = new PrRmAdditive();

		additiveInfo.setPrRmAdditiveRowNo(row);
		additiveInfo.setRawMatlId(refId);
		additiveInfo.setProductName(rawMatlServ.searchRawMaterial("", "", refId, 0).get(0).getRawMatlName());
		additiveInfo.setAdditiveDesc(foodAdditiveFunc);
		additiveInfo.setPrRmAdditiveRecStatus("new");

		prRmAdditiveTable.add(additiveInfo);
		sessionModel.put("prRmAdditiveItems", prRmAdditiveTable);

		setFoodAdditiveButton(false, true, true, sessionModel);
		clearFoodAdditiveForm(sessionModel);

		return new ModelAndView("/main/product/productForm", sessionModel);
	}

	@PostMapping(value = "/main/product/productForm", params = "foodAdditiveAction=update")
	public ModelAndView updateToFoodAdditiveList(HttpServletRequest request,
			@RequestParam(name = "prRmAdditiveRowNo") int rowNo, @RequestParam(name = "ingredientName") int refId,
			@RequestParam(name = "foodAdditiveFunc") String foodAdditiveFunc, ProductRecipe prodRecipe,
			BindingResult result, HttpSession session) {

		sessionModel.put("currTab", "foodAdditive");
		sessionModel.put("prodRecipeItems", prodRecipe);

		if (errorCheckFoodAdditiveList(refId, foodAdditiveFunc, sessionModel)) {
			holdFoodAdditiveValue(refId, foodAdditiveFunc, sessionModel);
			return new ModelAndView("/main/product/productForm", sessionModel);
		}

		List<PrRmAdditive> prRmAdditiveTable = (List<PrRmAdditive>) sessionModel.get("prRmAdditiveItems");

		for (PrRmAdditive prRmAdditive : prRmAdditiveTable) {
			if (prRmAdditive.getPrRmAdditiveRowNo() == rowNo) {
				prRmAdditive.setProductName(rawMatlServ.searchRawMaterial("", "", refId, 0).get(0).getRawMatlName());
				prRmAdditive.setAdditiveDesc(foodAdditiveFunc);
				prRmAdditive.setChanged(false);
			}
		}

		sessionModel.put("prRmAdditiveItems", prRmAdditiveTable);
		setFoodAdditiveButton(false, true, true, sessionModel);
		clearFoodAdditiveForm(sessionModel);

		return new ModelAndView("/main/product/productForm", sessionModel);
	}

	@PostMapping(value = "/main/product/productForm", params = "foodAdditiveAction=delete")
	public ModelAndView deleteFoodAdditiveList(HttpServletRequest request,
			@RequestParam(name = "prRmAdditiveRowNo") int rowNo, ProductRecipe prodRecipe, BindingResult result,
			HttpSession session) {

		sessionModel.put("currTab", "foodAdditive");
		sessionModel.put("productRecipeItems", prodRecipe);
		sessionModel.remove("error");
		List<PrRmAdditive> prRmAdditiveTable = (List<PrRmAdditive>) sessionModel.get("prRmAdditiveItems");

		try {
			List<PrRmAdditive> prRmAddDel = (List<PrRmAdditive>) sessionModel.get("prRmAddDel");

			prRmAddDel.add(prRmAdditiveTable.get(rowNo));
			prRmAdditiveTable.remove(rowNo);

			int reCalRowNo = 0;
			for (int i = 0; i < prRmAdditiveTable.size(); i++) {
				prRmAdditiveTable.get(i).setPrRmAdditiveRowNo(reCalRowNo);
				reCalRowNo++;
			}
		} catch (Exception e) {
			sessionModel.put("prRmAdditiveItems", prRmAdditiveTable);
		}

		sessionModel.put("prRmAdditiveItems", prRmAdditiveTable);
		setFoodAdditiveButton(false, true, true, sessionModel);
		clearFoodAdditiveForm(sessionModel);

		return new ModelAndView("/main/product/productForm", sessionModel);
	}

	private boolean duplicateCheckFoodAdditiveList(int refId, List<PrRmAdditive> prRmAdditive,
			Map<String, Object> sessionModel) {

		for (PrRmAdditive prRmAdd : prRmAdditive) {
			if (refId == prRmAdd.getRawMatlId()) {
				sessionModel.put("error", "Raw material existed in the list.");
				return true;
			}
		}

		return false;
	}

	@GetMapping("/main/product/productFormFAGetData")
	public ModelAndView retrieveProductFAFormData(HttpServletRequest request, @RequestParam(name = "id") int id,
			HttpSession session) {

		String screenMode = sessionModel.get("screenMode").toString();

		sessionModel.remove("error");
		List<PrRmAdditive> prRmAdditive = (List<PrRmAdditive>) sessionModel.get("prRmAdditiveItems");

		for (PrRmAdditive prRmAdd : prRmAdditive) {
			if (prRmAdd.getPrRmAdditiveRowNo() == id) {
				sessionModel.put("prRmAdditiveRowNo", prRmAdd.getPrRmAdditiveRowNo());
				sessionModel.put("ingredientId", prRmAdd.getRawMatlId());
				sessionModel.put("foodAdditiveFunc", prRmAdd.getAdditiveDesc());
			}
		}

		if (!screenMode.equals(CommonConstants.SCREEN_MODE_VIEW)) {
			setFoodAdditiveButton(false, false, false, sessionModel);
		}
		sessionModel.put("currTab", "foodAdditive");

		return new ModelAndView("/main/product/productForm", sessionModel);
	}

	private Map<String, Object> holdFoodAdditiveValue(int refId, String foodAdditiveFunc,
			Map<String, Object> sessionModel) {

		sessionModel.put("ingredientId", refId);
		sessionModel.put("foodAdditiveFunc", foodAdditiveFunc);

		return sessionModel;
	}

	private boolean errorCheckFoodAdditiveList(int refId, String foodAdditiveFunc, Map<String, Object> sessionModel) {

		sessionModel.remove("error");

		String errorMsg = "";
		errorMsg += commonValServ.validateMandatorySelect(refId, "Ingredient");
		errorMsg += commonValServ.validateMandatoryInput(foodAdditiveFunc, "Food Additive Function");

		if (errorMsg.length() != 0) {
			sessionModel.put("error", errorMsg);
			return true;
		}

		return false;
	}

	private Map<String, Object> clearFoodAdditiveForm(Map<String, Object> sessionModel) {

		sessionModel.remove("prRmAdditiveRowNo");
		sessionModel.remove("ingredientId");
		sessionModel.remove("foodAdditiveFunc");

		return sessionModel;
	}

	@PostMapping(value = "/main/product/productForm", params = "attachAction=add")
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
			return new ModelAndView("/main/product/productForm", sessionModel);
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

		return new ModelAndView("/main/product/productForm", sessionModel);
	}

	@PostMapping(value = "/main/product/productForm", params = "attachAction=update")
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
			return new ModelAndView("/main/product/productForm", sessionModel);
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

		return new ModelAndView("/main/product/productForm", sessionModel);
	}

	@PostMapping(value = "/main/product/productForm", params = "attachAction=delete")
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

		return new ModelAndView("/main/product/productForm", sessionModel);
	}

	@GetMapping("/main/product/productFormDocGetData")
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

		return new ModelAndView("/main/product/productForm", sessionModel);
	}

	private boolean errorCheckAttachment(String attachTitle, int attachCategory, MultipartFile attachFile,
			List<Document> attachTable, int check, Map<String, Object> sessionModel) {

		sessionModel.remove("error");

		String errorMsg = "";
		errorMsg += commonValServ.validateMandatoryInput(attachTitle, "Attachment Title");
		errorMsg += commonValServ.validateInputLength(attachTitle, 50, "Attachment Title");
		errorMsg += commonValServ.validateMandatorySelect(attachCategory, "Attachment Category");
		errorMsg += commonValServ.validateFileSize(attachFile.getSize(), "Attachment File");

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

	private Map<String, Object> holdAttachValue(String attachTitle, int attachCategory,
			Map<String, Object> sessionModel) {

		sessionModel.put("attachTitle", attachTitle);
		sessionModel.put("attachCategory", attachCategory);

		return sessionModel;
	}

	private Map<String, Object> clearAttachForm(Map<String, Object> sessionModel) {

		sessionModel.remove("attachTitle");
		sessionModel.remove("attachCategory");

		return sessionModel;
	}

	@GetMapping("/main/product/productFormDocDwn")
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

	private void addTrxHis(Date modifyDate, String userId, String prName, int matlStatus,
			Map<String, Object> sessionModel, int prId, String remark) {

		String screenMode = sessionModel.get("screenMode").toString();
		String logDesc = "";
		int funcType = 0;

		if (screenMode.equals(CommonConstants.SCREEN_MODE_ADD)) {

			if (checkLogDesc(matlStatus).equals("") || checkLogDesc(matlStatus) == null) {
				logDesc = "Insert Product Recipe";
			} else {
				logDesc = checkLogDesc(matlStatus);
			}

			funcType = CommonConstants.FUNCTION_TYPE_INSERT;
		} else if (screenMode.equals(CommonConstants.SCREEN_MODE_EDIT)) {

			if (checkLogDesc(matlStatus).equals("") || checkLogDesc(matlStatus) == null) {
				logDesc = "Update Product Recipe";
			} else {
				logDesc = checkLogDesc(matlStatus);
			}

			funcType = CommonConstants.FUNCTION_TYPE_UPDATE;
		} else {
			logDesc = checkLogDesc(matlStatus);
			funcType = CommonConstants.FUNCTION_TYPE_UPDATE;
		}

		if (prId == 0) {
			prId = prdRcpServ.searchProductIdByName(prName, String.valueOf(CommonConstants.SEARCH_OPTION_EXACT)).get(0)
					.getPrId();
		}

		trxHistServ.saveTrxHistoryLog(modifyDate, logDesc, userId, funcType, String.valueOf(prId),
				CommonConstants.RECORD_TYPE_ID_PROD_RECP, remark);
	}

	private String checkLogDesc(int matlStatus) {

		String logDesc = "";

		if (matlStatus == RecordStatusEnum.PEND_AUTH.intValue()
				|| matlStatus == RecordStatusEnum.PEND_CONFIRM.intValue()
				|| matlStatus == RecordStatusEnum.CHG_PEND_AUTH.intValue()
				|| matlStatus == RecordStatusEnum.CHG_PEND_CONFIRM.intValue()
				|| matlStatus == RecordStatusEnum.CHG_REWORK.intValue()) {
			logDesc = "Submit Product Recipe for Approval";
		} else if (matlStatus == RecordStatusEnum.SUBMITTED.intValue()) {
			logDesc = "Submit Product Recipe";
		} else if (matlStatus == RecordStatusEnum.REJECTED.intValue()) {
			logDesc = "Reject Product Recipe";
		} else if (matlStatus == RecordStatusEnum.REWORK.intValue()) {
			logDesc = "Rework Product Recipe";
		}

		return logDesc;
	}

	/*
	 * Save function
	 */
	@PostMapping(value = "/main/product/productForm", params = "action=save")
	public ModelAndView saveProductRecipe(ProductRecipe prdRcp, BindingResult result, HttpServletRequest request,
			HttpSession session, @RequestParam(name = "remarksFinalDecision", required = false) String[] hodRemark) {

		Date modifyDate = new Date();
		int prStatus = (int) sessionModel.get("prodRecipeStatus");
		String action = "saveForm";

		if (!saveProductRecipeData(prdRcp, request.getRemoteUser(), prStatus, modifyDate, action, sessionModel,
				hodRemark)) {
			return new ModelAndView("main/product/productForm", sessionModel);
		}

		addTrxHis(modifyDate, request.getRemoteUser(), prdRcp.getPrName(), prStatus, sessionModel, 0, null);

		return new ModelAndView("main/product/productList", sessionModel);
	}

	@RequestMapping(value = "/main/product/productFormCheckRole", method = RequestMethod.GET)
	@ResponseBody
	public Map<String, Object> saveIngReg(HttpServletRequest request, HttpSession session) {

		Map<String, Object> model = new HashMap<String, Object>();
		String screenMode = sessionModel.get("screenMode").toString();
		List<PrStat> prStatList = (List<PrStat>) sessionModel.get("prStatItems");
		List<PrRmStat> prRmStatList = (List<PrRmStat>) sessionModel.get("prRmStatTempItems");

		boolean authLvl1 = false;
		boolean authLvl2 = false;
		boolean authLvl3 = false;

		List<UsrRole> usrRoleList = usrRoleServ.searchUserRole(request.getRemoteUser());
		for (UsrRole usrRole : usrRoleList) {
			if (usrRole.getRoleId().equals(CommonConstants.ROLE_ID_HOD)) {
				authLvl3 = true;
			}

			if (usrRole.getRoleId().equals(CommonConstants.ROLE_ID_CHECKER)
					|| usrRole.getRoleId().equals(CommonConstants.ROLE_ID_SUPERUSER)) {
				authLvl2 = true;
			}

			if (usrRole.getRoleId().equals(CommonConstants.ROLE_ID_MAKER)) {
				authLvl1 = true;
			}
		}

		if (authLvl3) {
			model.put("role", "hod");
			model = checkLatestStatus(model, prStatList, prRmStatList);
		} else {
			if (authLvl2) {
				model.put("role", "checker");
			} else {
				if (authLvl1) {
					model.put("role", "maker");
				}
			}
		}

		model.put("screenMode", screenMode);

		return model;
	}

	private Map<String, Object> checkLatestStatus(Map<String, Object> model, List<PrStat> prStatList,
			List<PrRmStat> prRmStatList) {

		List<PrStat> newPrStatList = new ArrayList<PrStat>();

		if (prRmStatList.size() > 0) {
			for (PrStat prStat : prStatList) {
				PrStat prStatItem = new PrStat();
				String finalStatus = "";
				boolean notPermitted = false;
				boolean notSure = false;

				for (PrRmStat prRmStat : prRmStatList) {					
					if (prStat.getCountryId().equals(prRmStat.getCountryId())) {
						if (prRmStat.getAffectedFlag() != null) {
							if (prRmStat.getAffectedFlag().equals(String.valueOf(CommonConstants.FLAG_YES))) {
								
								logger.debug("checkLatestStatus() rm={}, cty={}, fs={}, sysfs={}"
										,prRmStat.getRawMatlId(), prRmStat.getCountryId(), prRmStat.getFinalStatus(), prRmStat.getSysFinalStatus());
								
								if (prRmStat.getSysFinalStatus() == FinalStatusEnum.NOT_PERMITTED.intValue()
										|| prRmStat.getSysFinalStatus() == FinalStatusEnum.YET_COMPELTE.intValue()) {
									notPermitted = true;
								}

								if (prRmStat.getSysFinalStatus() == FinalStatusEnum.NOT_SURE.intValue()) {
									notSure = true;
								}
							} else {
								if (prRmStat.getFinalStatus() == FinalStatusEnum.NOT_PERMITTED.intValue()
										|| prRmStat.getFinalStatus() == FinalStatusEnum.YET_COMPELTE.intValue()) {
									notPermitted = true;
								}

								if (prRmStat.getFinalStatus() == FinalStatusEnum.NOT_SURE.intValue()) {
									notSure = true;
								}
							}
						} else {
							if (prRmStat.getFinalStatus() == FinalStatusEnum.NOT_PERMITTED.intValue()
									|| prRmStat.getFinalStatus() == FinalStatusEnum.YET_COMPELTE.intValue()) {
								notPermitted = true;
							}

							if (prRmStat.getFinalStatus() == FinalStatusEnum.NOT_SURE.intValue()) {
								notSure = true;
							}
						}
					}
				}

				if (notPermitted) {
					finalStatus = msgSource.getMessage(FinalStatusEnum.NOT_PERMITTED.strKey(), null,
							Locale.getDefault());
				} else if (!notPermitted && notSure) {
					finalStatus = msgSource.getMessage(FinalStatusEnum.NOT_SURE.strKey(), null, Locale.getDefault());
				} else {
					finalStatus = msgSource.getMessage(FinalStatusEnum.PERMITTED.strKey(), null, Locale.getDefault());
				}

				prStatItem
						.setCntryName(refCountryServ
								.searchCountry(prStat.getCountryId(), "",
										String.valueOf(CommonConstants.SEARCH_OPTION_EXACT), "", "", "")
								.get(0).getCountryName());
				prStatItem.setFinalStatusDesc(finalStatus);
				newPrStatList.add(prStatItem);
			}

			model.put("countryData", newPrStatList);
		}

		return model;
	}

	/*
	 * Submit/Approve function
	 */
	@PostMapping(value = "/main/product/productForm", params = "action=submit")
	public ModelAndView submitProductRecipe(ProductRecipe prdRcp, BindingResult result, HttpServletRequest request,
			HttpSession session,
			@RequestParam(name = "remarksFinalDecisionSubmit", required = false) String[] hodRemark,
			@RequestParam(name = "submitConfirmRemark1", required = false) String submitConfirmRemark1,
			@RequestParam(name = "submitConfirmRemark2", required = false) String submitConfirmRemark2) {

		String screenMode = sessionModel.get("screenMode").toString();
		String remarks = "";

		logger.debug("submitProductRecipe() screenMode={},submitConfirmRemark={},submitConfirmRemark2={}", screenMode,
				submitConfirmRemark1, submitConfirmRemark2);
		
		if (StringUtils.isNotEmpty(submitConfirmRemark1))
			remarks = submitConfirmRemark1;
		if (StringUtils.isNotEmpty(submitConfirmRemark2))
			remarks = submitConfirmRemark2;

		int nextStatus = 0;
		boolean authLvl1 = false;
		boolean authLvl2 = false;
		boolean authLvl3 = false;
		String action = "submitForm";
		int currPrdRcpStatus = (int) sessionModel.get("prodRecipeStatus");
		String prName = "";
		int prId = 0;

		List<UsrRole> usrRoleList = usrRoleServ.searchUserRole(request.getRemoteUser());
		for (UsrRole usrRole : usrRoleList) {
			if (usrRole.getRoleId().equals(CommonConstants.ROLE_ID_HOD)) {
				authLvl3 = true;
			}

			if (usrRole.getRoleId().equals(CommonConstants.ROLE_ID_CHECKER)
					|| usrRole.getRoleId().equals(CommonConstants.ROLE_ID_SUPERUSER)) {
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
				return new ModelAndView("/main/product/productForm", sessionModel);				
			}
		} else {
			remarks = "Initial creation";
		}

		nextStatus = getNextStatus(nextStatus, authLvl1, authLvl2, authLvl3, currPrdRcpStatus);

		Date modifyDate = new Date();

		if (!screenMode.equals(CommonConstants.SCREEN_MODE_VIEW)) {
			if (!saveProductRecipeData(prdRcp, request.getRemoteUser(), nextStatus, modifyDate, action, sessionModel,
					hodRemark)) {
				return new ModelAndView("main/product/productForm", sessionModel);
			}
			prName = prdRcp.getPrName();
			
			if (screenMode.equals(CommonConstants.SCREEN_MODE_EDIT))
				prId = (int) sessionModel.get("prId");
			else
				prId = prdRcpServ.searchProductIdByName(prName, String.valueOf(CommonConstants.SEARCH_OPTION_EXACT))
						.get(0).getPrId();
			
		} else {
			prName = (String)sessionModel.get("prodName");
			prId = (int) sessionModel.get("prId");
			List<PrStat> prStatTable = (List<PrStat>) sessionModel.get("prStatItems");
			if (prStatTable.size() == 0) {
				sessionModel.put("error", "Please enter QA final check information.");
				sessionModel.remove("success");
				return new ModelAndView("main/product/productForm", sessionModel);
			}

			if (!checkTotalPerc(sessionModel)) {
				return new ModelAndView("main/product/productForm", sessionModel);
			}

			for (int i = 0; i < prStatTable.size(); i++) {
				String prStatRemark = "";
				if (hodRemark != null && hodRemark.length != 0) {
					prStatRemark = hodRemark[i];
				}

				prStatServ.updPrStat(prStatTable.get(i).getPrStatId(), prStatTable.get(i).getFinalStatus(),
						prStatTable.get(i).getCountryId(), prStatRemark, prStatTable.get(i).getRemarksUserId(),
						request.getRemoteUser(), modifyDate);
			}

			Integer currentLock = (Integer) sessionModel.get("currentOptlock");
			try {
				prdRcpServ.updProductRecipeStatusWithOptLock(prId, nextStatus, request.getRemoteUser(), modifyDate,
						currentLock, null);
				searchProductRecipe("", "", "", "", "", "", "", "", sessionModel, request.getRemoteUser(), 0, "", "");
			} catch (Exception e) {
				logger.error("submitProductRecipe() Error!", e);
				e.printStackTrace();

				String errorMsg = "Submit Product Recipe " + prName + " was unsuccessful.";

				if (e instanceof CommonException) {
					if (e.getMessage().contains("Concurrent")) {
						errorMsg = errorMsg.concat(
								" Record could have been updated by another user.<br /> Kindly refresh the listing and view back the record.");
					}
				}

				sessionModel.put("error", errorMsg);
				sessionModel.remove("success");
				return new ModelAndView("main/product/productForm", sessionModel);
			}
		}
		
		if (authLvl3) {
			prRmServ.updPrRmStatByFlag(prId);
			prRmServ.updPrRmStatFlag(String.valueOf(CommonConstants.FLAG_NO), request.getRemoteUser(), modifyDate, prId,
					0, "",0);
			prdRcpServ.updatePrOnHoldFlag(prId, String.valueOf(CommonConstants.FLAG_NO), request.getRemoteUser(),
					modifyDate, null);

			List<PrStat> prStatTable = (List<PrStat>) sessionModel.get("prStatItems");
			for (PrStat prStat : prStatTable) {
				List<PrRmStat> prRmStatList = prRmServ.searchPrRmStat(prId, prStat.getCountryId());

				boolean notPermitted = false;
				boolean notSure = false;
				int finStatus = 1;
				for (PrRmStat prRmStat : prRmStatList) {
					if (prRmStat.getFinalStatus() == FinalStatusEnum.NOT_PERMITTED.intValue()
							|| prRmStat.getFinalStatus() == FinalStatusEnum.YET_COMPELTE.intValue()) {
						notPermitted = true;
					}

					if (prRmStat.getFinalStatus() == FinalStatusEnum.NOT_SURE.intValue()) {
						notSure = true;
					}
				}

				if (notPermitted) {
					finStatus = FinalStatusEnum.NOT_PERMITTED.intValue();
				} else if (!notPermitted && notSure) {
					finStatus = FinalStatusEnum.NOT_SURE.intValue();
				} else {
					finStatus = FinalStatusEnum.PERMITTED.intValue();
				}

				prStatServ.updPrStatFlag(prId, prStat.getCountryId(), finStatus, request.getRemoteUser(), modifyDate);
			}
		}

		addTrxHis(modifyDate, request.getRemoteUser(), prName, nextStatus, sessionModel, prId, remarks);

		String successMsg = "";
		if (nextStatus == RecordStatusEnum.PEND_AUTH.intValue()
				|| nextStatus == RecordStatusEnum.CHG_PEND_AUTH.intValue()
				|| nextStatus == RecordStatusEnum.REWORK_PEND_AUTH.intValue()) {
			successMsg = "Product recipe " + prName + " has been submitted for approval.";
		}

		if (nextStatus == RecordStatusEnum.PEND_CONFIRM.intValue()
				|| nextStatus == RecordStatusEnum.CHG_PEND_CONFIRM.intValue()) {
			successMsg = "Product recipe " + prName + " has been submitted for confirmation.";
		}

		if (nextStatus == RecordStatusEnum.SUBMITTED.intValue()) {
			successMsg = "Product recipe " + prName + " has been submitted.";
		}

		sessionModel.put("success", successMsg);
		sessionModel.remove("error");

		taskCreationServ.taskCreationPrd(prId, prName, request.getRemoteUser(), nextStatus, "", screenMode, modifyDate);

		return new ModelAndView("main/product/productList", sessionModel);
	}

	private int getNextStatus(int nextStatus, boolean authLvl1, boolean authLvl2, boolean authLvl3,
			int currPrdRcpStatus) {
		if (authLvl3) {
			nextStatus = RecordStatusEnum.SUBMITTED.intValue();
		} else {
			if (authLvl2) {
				if (currPrdRcpStatus == RecordStatusEnum.SUBMITTED.intValue()
						|| currPrdRcpStatus == RecordStatusEnum.CHG_PEND_AUTH.intValue()) {
					nextStatus = RecordStatusEnum.CHG_PEND_CONFIRM.intValue();
				} else {
					nextStatus = RecordStatusEnum.PEND_CONFIRM.intValue();
				}
			} else {
				if (authLvl1) {
					if (currPrdRcpStatus == RecordStatusEnum.SUBMITTED.intValue()) {
						nextStatus = RecordStatusEnum.CHG_PEND_AUTH.intValue();
					} else if (currPrdRcpStatus == RecordStatusEnum.REWORK.intValue()) {
						nextStatus = RecordStatusEnum.REWORK_PEND_AUTH.intValue();
					} else {
						nextStatus = RecordStatusEnum.PEND_AUTH.intValue();
					}
				}
			}
		}
		return nextStatus;
	}

	private boolean checkTotalPerc(Map<String, Object> sessionModel) {

		List<PrIngredient> prIngTable = (List<PrIngredient>) sessionModel.get("prIngItems");
		double totalPerc = 0;
		for (PrIngredient prIng : prIngTable) {
			totalPerc = totalPerc + prIng.getIngPerc();
		}

		if ((Math.round(totalPerc * 100) / 100) != 100) {
			sessionModel.put("error", " Total ingredient percentage not meet 100%.");
			return false;
		}

		return true;
	}

	/*
	 * Reject function
	 */
	@PostMapping(value = "/main/product/productForm", params = "action=reject")
	public ModelAndView rejectProductRecipe(HttpServletRequest request, ProductRecipe prdRcp, BindingResult result,
			@RequestParam(name = "rejectRemark") String rejectRemark, HttpSession session) {
		
		if(StringUtils.isEmpty(rejectRemark)) {
			sessionModel.put("error", "Reject Remarks is mandatory as it will be used for tracing reason of change/update");
			sessionModel.remove("success");
			return new ModelAndView("/main/product/productForm", sessionModel);
		}

		String screenMode = sessionModel.get("screenMode").toString();
		int prId = (int) sessionModel.get("prId");
		Date modifyDate = new Date();
		String prName = (prdRcp.getPrName() == null || prdRcp.getPrName().isEmpty())
				? (String) sessionModel.get("prodName")
						: prdRcp.getPrName();

		Integer currentLock = (Integer) sessionModel.get("currentOptlock");
		try {
			prdRcpServ.updProductRecipeStatusWithOptLock(prId, RecordStatusEnum.REJECTED.intValue(),
					request.getRemoteUser(), modifyDate, currentLock, null);
			addTrxHis(modifyDate, request.getRemoteUser(), prName, RecordStatusEnum.REJECTED.intValue(),
					sessionModel, prId, rejectRemark);
			taskCreationServ.taskCreationPrd(prId, prName, request.getRemoteUser(),
					RecordStatusEnum.REJECTED.intValue(), rejectRemark, screenMode, modifyDate);
			
			sessionModel.put("success", "Product recipe " + prName + " has been rejected.");
			sessionModel.remove("error");
			
		} catch (Exception e) {
			logger.error("rejectProductRecipe() Error!", e);
			e.printStackTrace();

			String errorMsg = "Reject Product recipe " + prName + " was unsuccessful.";

			if (e instanceof CommonException) {
				if (e.getMessage().contains("Concurrent")) {
					errorMsg = errorMsg.concat(
							" Record could have been updated by another user.<br /> Kindly refresh the listing and view back the record.");
				}
			}

			sessionModel.put("error", errorMsg);
			sessionModel.remove("success");
		}
		
		searchProductRecipe("", "", "", "", "", "", "", "", sessionModel, request.getRemoteUser(), 0, "", "");
		return new ModelAndView("/main/product/productList", sessionModel);
	}

	/*
	 * Rework function
	 */
	@PostMapping(value = "/main/product/productForm", params = "action=rework")
	public ModelAndView reworkProductRecipe(HttpServletRequest request, ProductRecipe prdRcp, BindingResult result,
			@RequestParam(name = "reworkRemark") String reworkRemark, HttpSession session) {
		
		if(StringUtils.isEmpty(reworkRemark)) {
			sessionModel.put("error", "Rework Remarks is mandatory as it will be used for tracing reason of change/update");
			sessionModel.remove("success");
			return new ModelAndView("/main/product/productForm", sessionModel);
		}

		String screenMode = sessionModel.get("screenMode").toString();
		int prId = (int) sessionModel.get("prId");
		Date modifyDate = new Date();
		String prName = (prdRcp.getPrName() == null || prdRcp.getPrName().isEmpty())
				? (String) sessionModel.get("prodName")
				: prdRcp.getPrName();

		Integer currentLock = (Integer) sessionModel.get("currentOptlock");
		try {
			prdRcpServ.updProductRecipeStatusWithOptLock(prId, RecordStatusEnum.REWORK.intValue(), request.getRemoteUser(),
					modifyDate,currentLock,null);
			addTrxHis(modifyDate, request.getRemoteUser(), prName, RecordStatusEnum.REWORK.intValue(),
					sessionModel, prId, reworkRemark);
			taskCreationServ.taskCreationPrd(prId, prName, request.getRemoteUser(), RecordStatusEnum.REWORK.intValue(),
					reworkRemark, screenMode, modifyDate);
			
			sessionModel.put("success", "Product recipe " + prName + " has been revert to creator for rework.");
			sessionModel.remove("error");
			
		} catch (Exception e) {
			logger.error("reworkProductRecipe() Error!", e);
			e.printStackTrace();

			String errorMsg = "Revert Product recipe " +  prName + " to creator was unsuccessful.";

			if (e instanceof CommonException) {
				if (e.getMessage().contains("Concurrent")) {
					errorMsg = errorMsg.concat(
							" Record could have been updated by another user.<br /> Kindly refresh the listing and view back the record.");
				}
			}

			sessionModel.put("error", errorMsg);
			sessionModel.remove("success");
		}
		
		searchProductRecipe("", "", "", "", "", "", "", "", sessionModel, request.getRemoteUser(), 0, "", "");
		return new ModelAndView("/main/product/productList", sessionModel);
	}

	private boolean saveProductRecipeData(ProductRecipe prdRcp, String currUser, int prdRcpStatus, Date modifyDate,
			String action, Map<String, Object> sessionModel, String[] hodRemark) {

		String screenMode = sessionModel.get("screenMode").toString();
		List<PrIngredient> prIngTable = (List<PrIngredient>) sessionModel.get("prIngItems");
		List<PrStat> prStatTable = (List<PrStat>) sessionModel.get("prStatItems");
		List<PrRmStat> prRmStatTable = (List<PrRmStat>) sessionModel.get("prRmStatItems");
		List<PrRmStat> prRmStatTempTable = (List<PrRmStat>) sessionModel.get("prRmStatTempItems");
		List<PrRmAdditive> prRmAdditiveTable = (List<PrRmAdditive>) sessionModel.get("prRmAdditiveItems");

		if (hodRemark != null) {
			for (int x = 0; x < hodRemark.length; x++) {
				prStatTable.get(x).setRemarks(hodRemark[x]);
			}
		}

		if (!saveFuncErrorCheck(prdRcp, prIngTable, prStatTable, prRmAdditiveTable, prRmStatTempTable, action,
				sessionModel)) {
			return false;
		}
		
		String prName = "";
		int prId = 0;
		
		// generate final flavor status
		Integer finalPrFsId = generateFinalFlavorStatus(prIngTable);
		// set final flavor status for product
		prdRcp.setFlavStatusId(finalPrFsId);

		try {
			if (screenMode.equals(CommonConstants.SCREEN_MODE_ADD)) {
				saveAddPrTable(prdRcp, currUser, prdRcpStatus, modifyDate);

				saveAddPrIngTable(prdRcp, currUser, modifyDate, prIngTable);

				saveAddPrRmAdditiveTable(prdRcp, currUser, modifyDate, prRmAdditiveTable);

				saveAddPrStatTable(prdRcp, currUser, modifyDate, prStatTable, prRmStatTable);

				saveAddPrRmStatTable(prdRcp, currUser, modifyDate, prRmStatTempTable);

			} else if (screenMode.equals(CommonConstants.SCREEN_MODE_EDIT)) {
				prId = (int) sessionModel.get("prId");
				prRmRegServ.delPrRmReg(prId);

				saveEditPrTable(prdRcp, currUser, prdRcpStatus, modifyDate, sessionModel, prId);

				saveEditPrIngTable(prdRcp, currUser, modifyDate, sessionModel, prIngTable, prStatTable, prRmStatTable,
						prId);

				saveEditPrStatTable(prdRcp, currUser, modifyDate, sessionModel, prStatTable, prRmStatTable, prId);

				saveEditPrRmAdditiveTable(prdRcp, currUser, modifyDate, sessionModel, prRmAdditiveTable);

				saveEditPrRmStatTable(prdRcp, currUser, modifyDate, prRmStatTempTable, prId);
			}
			
			prName = (prdRcp.getPrName() == null || prdRcp.getPrName().isEmpty())
					? (String) sessionModel.get("prodName")
							: prdRcp.getPrName();
					
			savePrRmRegsTable(prdRcp, sessionModel);

			doCheckAttachement(currUser, modifyDate, sessionModel, prName, prId);

			sessionModel.put("success", "Product recipe " + prName + " saved successfully.");
			sessionModel.remove("error");
			searchProductRecipe("", "", "", "", "", "", "", "", sessionModel, currUser, 0, "", "");

		} catch (Exception e) {
			logger.error("saveProductRecipeData() Error!", e);
			e.printStackTrace();

			String errorMsg = "Fail to @[action] Product recipe " + prName + ".";

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
			searchProductRecipe("", "", "", "", "", "", "", "", sessionModel, currUser, 0, "", "");

			return false;
		}

		return true;
	}

	private void saveAddPrTable(ProductRecipe prdRcp, String currUser, int prdRcpStatus, Date modifyDate) {
		prdRcpServ.addProductRecipe(prdRcp.getPrCode(), prdRcp.getPrName(), prdRcp.getRemarks(),
				prdRcp.getRemarksUserId(), prdRcp.getTotalPerc(), prdRcpStatus, currUser,
				prdRcp.getPrOtherName1(), prdRcp.getPrOtherName2(), prdRcp.getPrOtherName3(),
				prdRcp.getPrOtherName4(), prdRcp.getPrOtherName5(), modifyDate, prdRcp.getFlavStatusId());
	}
	
	private void saveAddPrIngTable(ProductRecipe prdRcp, String currUser, Date modifyDate,
			List<PrIngredient> prIngTable) {
		for (PrIngredient prIng : prIngTable) {
			prIngServ.addPrIngredient(prdRcp.getPrCode(), 0, prIng.getRefId(), prIng.getRefType(),
					prIng.getIngPerc(), prIng.getSelTsNo(), prIng.getSeqNo(), prIng.getAdditiveDesc(), currUser,
					modifyDate, prIng.getAltRm());
		}
	}	
	
	private void saveAddPrRmAdditiveTable(ProductRecipe prdRcp, String currUser, Date modifyDate,
			List<PrRmAdditive> prRmAdditiveTable) {
		for (PrRmAdditive prRmAdd : prRmAdditiveTable) {
			prRmAddServ.addPrRmAdditive(prdRcp.getPrCode(), prRmAdd.getRawMatlId(), prRmAdd.getAdditiveDesc(),
					currUser, modifyDate);
		}
	}		
	
	private void saveAddPrStatTable(ProductRecipe prdRcp, String currUser, Date modifyDate, List<PrStat> prStatTable,
			List<PrRmStat> prRmStatTable) {
		for (PrStat prStat : prStatTable) {
			prStatServ.addPrStat(prdRcp.getPrCode(), prStat.getFinalStatus(), prStat.getCountryId(),
					prStat.getRemarks(), prStat.getRemarksUserId(), currUser, modifyDate);

			for (PrRmStat prRmStat : prRmStatTable) {
				int rmFinalStatus = 0;
				try {
					rmFinalStatus = rmStatServ.getRmFinalStatus(prRmStat.getRawMatlId(), prStat.getCountryId(),
							0);
				} catch (Exception e) {
				}
				prRmServ.addPrRmStat(prdRcp.getPrCode(), prRmStat.getRawMatlId(), prRmStat.getTotalPerc(),
						rmFinalStatus, 0, prStat.getCountryId(), prRmStat.getRemarks(), currUser, modifyDate);
			}
		}
	}	
	
	private void saveAddPrRmStatTable(ProductRecipe prdRcp, String currUser, Date modifyDate,
			List<PrRmStat> prRmStatTempTable) {
		for (PrRmStat prRmStat : prRmStatTempTable) {
			prRmServ.updPrRmStat(prRmStat.getFinalStatus(), prRmStat.getRemarks(), prdRcp.getPrCode(),
					prRmStat.getRawMatlId(), prRmStat.getCountryId(), currUser, modifyDate);
		}
	}
	
	private void savePrRmRegsTable(ProductRecipe prdRcp, Map<String, Object> sessionModel) {
		List<PrRmReg> prRmRegItems = (List<PrRmReg>) sessionModel.get("prRmRegItems");
		for (PrRmReg prRmReg : prRmRegItems) {
			prRmRegServ.addPrRmReg(prdRcp.getPrCode(), prRmReg.getRawMatlId(), prRmReg.getCountryId(),
					prRmReg.getRegDocId());
		}
	}	

	private void saveEditPrTable(ProductRecipe prdRcp, String currUser, int prdRcpStatus, Date modifyDate,
			Map<String, Object> sessionModel, int prId) throws Exception {
		logger.info("saveEditPrTable() prCode={}",prdRcp.getPrCode());
		
		Integer currentLock = (Integer) sessionModel.get("currentOptlock");
		prdRcpServ.updProductRecipeWithOptLock(prId, prdRcp.getPrCode(),
				prdRcp.getPrName(), prdRcp.getRemarks(), prdRcp.getRemarksUserId(), prdRcp.getTotalPerc(),
				prdRcpStatus, currUser, prdRcp.getPrOtherName1(), prdRcp.getPrOtherName2(),
				prdRcp.getPrOtherName3(), prdRcp.getPrOtherName4(), prdRcp.getPrOtherName5(), modifyDate,
				currentLock, prdRcp.getFlavStatusId());
	}
	
	private void saveEditPrIngTable(ProductRecipe prdRcp, String currUser, Date modifyDate,
			Map<String, Object> sessionModel, List<PrIngredient> prIngTable, List<PrStat> prStatTable,
			List<PrRmStat> prRmStatTable, int prId) {
		logger.info("saveEditPrIngTable() prCode={}",prdRcp.getPrCode());

		// Deleted ingredient from 'delete from list' action in PR tab 1
		List<PrIngredient> prIngDel = (List<PrIngredient>) sessionModel.get("prIngDel");		
		if (prIngDel.size() > 0) {
			for (PrIngredient delPrIng : prIngDel) {
				logger.debug("saveEditPrIngTable() Deleted ing ref={},type={}",delPrIng.getRefId(),delPrIng.getRefType());
				
				prIngServ.delDetailPrIngredient(delPrIng.getPrIngId());

				if (delPrIng.getRefType() == CommonConstants.RECORD_TYPE_ID_RAW_MATL) {
					prRmAddServ.delPrRmAddByMatl(prId, delPrIng.getRefId());
					prRmServ.delPrRmStatByMatl(prId, delPrIng.getRefId());
				} else {
					
					
				}

			}			
			
			prIngDel = new ArrayList<PrIngredient>();
			sessionModel.replace("prIngDel", prIngDel);
			
			//20211209 - check if pr-ing is intermediate product, then need to delete
			// it's rm from pr-rm-stat, pr-rm-addtv
			List<PrRmStat> prRmDetails = prRmServ.searchPrRmDetails(prId);
			// Compare pr-rm-stat list from db with current state 
			List<Integer> dbRmIds = prRmDetails.stream().map(PrRmStat::getRawMatlId).distinct().collect(Collectors.toList());
			List<Integer> prRmStats = prRmStatTable.stream().map(PrRmStat::getRawMatlId).distinct().collect(Collectors.toList());
			List<Integer> diff = dbRmIds.stream().filter(arg0 -> !prRmStats.contains(arg0)).collect(Collectors.toList());
			
			for (Integer rmid : diff) {
				logger.debug("saveEditPrIngTable() Deleted diff prid={},rmid={}",prId,rmid);
				prRmAddServ.delPrRmAddByMatl(prId, rmid);
				prRmServ.delPrRmStatByMatl(prId, rmid);
			}
		}
		
		// Loop pr-ing table
		for (PrIngredient prIng : prIngTable) {
			
			// Newly added ingredient
			if (prIng.getIngStatus().equals("new")) {
				logger.debug("saveEditPrIngTable() Added new ref={},type={}",prIng.getRefId(),prIng.getRefType());
				
				prIngServ.addPrIngredient(prdRcp.getPrCode(), 0, prIng.getRefId(), prIng.getRefType(),
						prIng.getIngPerc(), prIng.getSelTsNo(), prIng.getSeqNo(), prIng.getAdditiveDesc(),
						currUser, modifyDate, prIng.getAltRm());

				for (PrStat prStat : prStatTable) { // Loop pr-stat to get country
					for (PrRmStat prRmStat : prRmStatTable) { // Loop pr-rm-stat to get RM
						int rmFinalStatus = 0;
						try {
							rmFinalStatus = rmStatServ.getRmFinalStatus(prRmStat.getRawMatlId(), prStat.getCountryId(),
									0);
						} catch (Exception e) {
						}
						if (prIng.getRefType() == CommonConstants.RECORD_TYPE_ID_RAW_MATL
								&& prRmStat.getRawMatlId() == prIng.getRefId()) {

							// For newly added ingredient(RM), need to add also to pr-rm-stat based on
							// country from pr-stat
							logger.debug("saveEditPrIngTable() Added new pr-rm-stat rm={},country={}",
									prRmStat.getRawMatlId(), prStat.getCountryId());

							prRmServ.addPrRmStat(prdRcp.getPrCode(), prRmStat.getRawMatlId(), prRmStat.getTotalPerc(),
									rmFinalStatus, 0, prStat.getCountryId(), prRmStat.getRemarks(), currUser,
									modifyDate);
						} else {
							// 20211209 - check if pr-ing is intermediate product, then need to add
							// it's rm to pr-rm-stat
							// add the remaining RM if not exists in current pr-rm-stat

							PrRmStat uniquePrRmStat = prRmServ.findUnique(prId, prRmStat.getRawMatlId(),
									prStat.getCountryId());
							if (uniquePrRmStat == null) {
								prRmServ.addPrRmStat(prdRcp.getPrCode(), prRmStat.getRawMatlId(),
										prRmStat.getTotalPerc(), rmFinalStatus, 0, prStat.getCountryId(),
										prRmStat.getRemarks(), currUser, modifyDate);
							}
						}
					}
				}

			} else if (prIng.getIngStatus().equals("exist")) {
				// Current ingredient but with updated values
				logger.debug("saveEditPrIngTable() Update for ing ref={},type={}",prIng.getRefId(),prIng.getRefType());
				
				prIngServ.updPrIngredient(prIng.getPrIngId(), prIng.getRefId(), prIng.getRefType(),
						prIng.getIngPerc(), prIng.getSelTsNo(), currUser, modifyDate, prIng.getAltRm());
			}
		}
	}
	
	private void saveEditPrStatTable(ProductRecipe prdRcp, String currUser, Date modifyDate,
			Map<String, Object> sessionModel, List<PrStat> prStatTable, List<PrRmStat> prRmStatTable, int prId) {
		logger.info("saveEditPrStatTable() prCode={}",prdRcp.getPrCode());
		
		// Deleted stats from 'delete from list' action in PR tab 3
		List<PrStat> prStatDel = (List<PrStat>) sessionModel.get("prStatDel");
		if (prStatDel.size() > 0) {
			for (PrStat delPrStat : prStatDel) {
				logger.debug("saveEditPrStatTable() Deleted stat={}",delPrStat.getCountryId());
				
				prStatServ.delDetailPrStat(delPrStat.getPrStatId());
				prRmServ.delPrRmStatByCountry(prId, delPrStat.getCountryId());
			}
			prStatDel = new ArrayList<PrStat>();
			sessionModel.replace("prStatDel", prStatDel);
		}
		
		// Loop pr-stat table
		for (PrStat prStat : prStatTable) {
			// Newly added stats
			if (prStat.getPrStatRecStatus().equals("new")) {
				logger.debug("saveEditPrStatTable() Added new stat={}",prStat.getCountryId());
				
				prStatServ.addPrStat(prdRcp.getPrCode(), prStat.getFinalStatus(), prStat.getCountryId(),
						prStat.getRemarks(), prStat.getRemarksUserId(), currUser, modifyDate);

				for (PrRmStat prRmStat : prRmStatTable) {
					
					//check unique constraint
					//prRmStat record might have been added earlier due to new/add pr_ingredient logic above							
					PrRmStat uniquePrRmStat = prRmServ.findUnique(prId, prRmStat.getRawMatlId(),
							prStat.getCountryId());
					if (uniquePrRmStat != null)
						continue;

					// For newly added stats, need to add also to pr-rm-stat based on prStat.getCountryId()
					logger.debug("saveEditPrStatTable() Added new pr-rm-stat rm={},country={}",prRmStat.getRawMatlId(),prStat.getCountryId());
					
					prRmServ.addPrRmStat(prdRcp.getPrCode(), prRmStat.getRawMatlId(), prRmStat.getTotalPerc(),
							0, 0, prStat.getCountryId(), prRmStat.getRemarks(), currUser, modifyDate);
				}

			} else if (prStat.getPrStatRecStatus().equals("exist")) {
				// Current stats but with updated values
				logger.debug("saveEditPrStatTable() Update for stat={}",prStat.getCountryId());
				
				prStatServ.updPrStat(prStat.getPrStatId(), prStat.getFinalStatus(), prStat.getCountryId(),
						prStat.getRemarks(), prStat.getRemarksUserId(), currUser, modifyDate);
			}
		}
		
	}
	
	private void saveEditPrRmStatTable(ProductRecipe prdRcp, String currUser, Date modifyDate,
			List<PrRmStat> prRmStatTempTable, int prId) {
		logger.info("saveEditPrRmStatTable() prCode={}",prdRcp.getPrCode());
		
		for (PrRmStat prRmStat : prRmStatTempTable) {
			if(prRmStat.getRmRefChanged() != null && prRmStat.getRmRefChanged() !=0) {
				logger.debug("saveEditPrRmStatTable() RM ref changed! toRM={},fromRM={},country={}",prRmStat.getRawMatlId(),prRmStat.getRmRefChanged(),prRmStat.getCountryId());
				
				//to cater for rm ref change due to ingredient changed
				//202112141411
				//Find RM existence before replace
				//If exists, do normal update, no need replace, but need to delete its original rm
				//Otherwise, replace
				PrRmStat toBePrRmStat = prRmServ.findUnique(prId, prRmStat.getRawMatlId(), prRmStat.getCountryId());
				if (toBePrRmStat != null) {
					logger.debug("saveEditPrRmStatTable() Found already exist! toRM={},fromRM={},country={}",
							prRmStat.getRawMatlId(), prRmStat.getRmRefChanged(), prRmStat.getCountryId());
					prRmServ.updPrRmStat(prRmStat.getFinalStatus(), prRmStat.getRemarks(), prdRcp.getPrCode(),
							prRmStat.getRawMatlId(), prRmStat.getCountryId(), currUser, modifyDate);
					prRmServ.delPrRmStatByMatl(prId, prRmStat.getRmRefChanged());
				} else {
					logger.debug("saveEditPrRmStatTable() Not found! toRM={},fromRM={},country={}",
							prRmStat.getRawMatlId(), prRmStat.getRmRefChanged(), prRmStat.getCountryId());
					prRmServ.updPrRmStat(prRmStat.getFinalStatus(), prRmStat.getRemarks(), prdRcp.getPrCode(),
							prRmStat.getRmRefChanged(), prRmStat.getCountryId(), currUser, modifyDate,
							prRmStat.getRawMatlId());
				}
				
			} else {
				logger.debug("saveEditPrRmStatTable() NO RM ref changed! matl={},refChg={},country={}",prRmStat.getRawMatlId(),prRmStat.getRmRefChanged(),prRmStat.getCountryId());
				prRmServ.updPrRmStat(prRmStat.getFinalStatus(), prRmStat.getRemarks(), prdRcp.getPrCode(),
						prRmStat.getRawMatlId(), prRmStat.getCountryId(), currUser, modifyDate);
			}
		}
	}
	
	private void saveEditPrRmAdditiveTable(ProductRecipe prdRcp, String currUser, Date modifyDate,
			Map<String, Object> sessionModel, List<PrRmAdditive> prRmAdditiveTable) {
		logger.info("saveEditPrRmAdditiveTable() prCode={}",prdRcp.getPrCode());
		
		// Deleted additive from 'delete from list' action in PR tab 4
		List<PrRmAdditive> prRmAddDel = (List<PrRmAdditive>) sessionModel.get("prRmAddDel");
		if (prRmAddDel.size() > 0) {
			for (PrRmAdditive delPrRmAdd : prRmAddDel) {
				logger.debug("saveEditPrRmAdditiveTable() Deleted rm={}",delPrRmAdd.getRawMatlId());
				
				prRmAddServ.delDetailPrRmAdditive(delPrRmAdd.getPrRmAddtID());
			}
			prRmAddDel = new ArrayList<PrRmAdditive>();
			sessionModel.replace("prRmAddDel", prRmAddDel);
		}
		
		// Loop pr-rm-additve table
		for (PrRmAdditive prRmAdd : prRmAdditiveTable) {
			// Newly added additive
			if (prRmAdd.getPrRmAdditiveRecStatus().equals("new")) {
				logger.debug("saveEditPrRmAdditiveTable() Added new rm={}",prRmAdd.getRawMatlId());
				
				prRmAddServ.addPrRmAdditive(prdRcp.getPrCode(), prRmAdd.getRawMatlId(),
						prRmAdd.getAdditiveDesc(), currUser, modifyDate);

			} else if (prRmAdd.getPrRmAdditiveRecStatus().equals("exist")) {
				// Current additive but with updated values
				logger.debug("saveEditPrRmAdditiveTable() Update for rm={}",prRmAdd.getRawMatlId());
				
				prRmAddServ.updPrRmAdditive(prRmAdd.getPrRmAddtID(), prRmAdd.getRawMatlId(),
						prRmAdd.getAdditiveDesc(), currUser, modifyDate);
			}
		}		
	}	

	private void doCheckAttachement(String currUser, Date modifyDate, Map<String, Object> sessionModel, String prName,
			int prId) {
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

		List<Document> attachTable = (List<Document>) sessionModel.get("attachItems");

		if (prId == 0) {
			prId = prdRcpServ.searchProductIdByName(prName, String.valueOf(CommonConstants.SEARCH_OPTION_EXACT)).get(0)
					.getPrId();
		}

		for (Document doc : attachTable) {
			if (doc.getDocRecStatus().equals("new")) {
				docServ.addDocument(doc.getSubject(), String.valueOf(prId), CommonConstants.RECORD_TYPE_ID_PROD_RECP,
						doc.getDocumentType(), doc.getFileName(), doc.getContentObj(), doc.getCreatedBy());

			} else if (doc.getDocRecStatus().equals("exist")) {
				docServ.updDocument(doc.getSubject(), doc.getDocumentType(), doc.getFileName(), doc.getContentObj(),
						currUser, modifyDate, doc.getId());
			}
		}
	}

	private boolean saveFuncErrorCheck(ProductRecipe prdRcp, List<PrIngredient> prIng, List<PrStat> prStat,
			List<PrRmAdditive> prRmAdditive, List<PrRmStat> prRmStatTempTable, String action,
			Map<String, Object> sessionModel) {

		String screenMode = sessionModel.get("screenMode").toString();
		sessionModel.remove("error");
		sessionModel.remove("success");
		String errorMsg = "";

		if (prIng.size() == 0) {
			errorMsg += "Please enter ingredient information.<br />";
		}

		if (action.equals("submitForm")) {
			if (prStat.size() == 0) {
				errorMsg += "Please enter QA final check information.<br />";
			}

			if (prIng.size() != 0 && prdRcp.getTotalPerc() != null) {
				if (!checkTotalPerc(sessionModel)) {
					holdPrValuesTab1(prdRcp, sessionModel);
					return false;
				}
			}
		}

		errorMsg += commonValServ.validateMandatoryInput(prdRcp.getPrCode(), "Product Recipe TS No");
		errorMsg += commonValServ.validateMandatoryInput(prdRcp.getPrName(), "Product Recipe Name");
//		errorMsg += commonValServ.validateMandatoryInputRemark(prdRcp.getRemarks(), "Remark");

		if (prdRcp.getTotalPerc() == null) {
			errorMsg += "Total Percentage % is mandatory.<br />";
		} else if (prdRcp.getTotalPerc() < 0) {
			errorMsg += "Total Percentage % is less than 0.<br />";
		} else if (prdRcp.getTotalPerc() > 100) {
			errorMsg += "Total Percentage % is greater than 100.<br />";
		}

		boolean finalStatusNotEnter = false;
		List<String> countryList = new ArrayList<>();

		for (PrRmStat prRmStatTemp : prRmStatTempTable) {
			if (prRmStatTemp.getFinalStatus() == 0) {
				countryList.add(refCountryServ.searchCountry(prRmStatTemp.getCountryId(), "",
						String.valueOf(CommonConstants.SEARCH_OPTION_EXACT), "", "", "").get(0).getCountryName());
				finalStatusNotEnter = true;
			}
		}

		List<String> countryListNoDupl = countryList.stream().distinct().collect(Collectors.toList());
		String countryName = "";
		for (int i = 0; i < countryListNoDupl.size(); i++) {
			if (countryName.equals("")) {
				countryName = countryListNoDupl.get(i);
			} else {
				countryName = countryName + ", " + countryListNoDupl.get(i);
			}
		}

		if (finalStatusNotEnter) {
			sessionModel.put("error", "Please enter (NEW) final status for these country: " + countryName + ".");
			holdPrValuesTab1(prdRcp, sessionModel);
			return false;
		}

		if (errorMsg.length() != 0) {
			sessionModel.put("error", errorMsg);
			//hold tab1 values
			holdPrValuesTab1(prdRcp, sessionModel);
			
			return false;
		}

		if (screenMode.equals(CommonConstants.SCREEN_MODE_ADD) && !prdRcp.getPrCode().equals("")) {
			List<ProductRecipe> prdRcpList = prdRcpServ.searchProductId(prdRcp.getPrCode(),
					String.valueOf(CommonConstants.SEARCH_OPTION_EXACT));

			if (prdRcpList.size() > 0) {
				sessionModel.put("error", "Product recipe already exist.");
				holdPrValuesTab1(prdRcp, sessionModel);
				return false;
			}
		}

		if (screenMode.equals(CommonConstants.SCREEN_MODE_ADD) && !prdRcp.getPrName().equals("")) {
			List<ProductRecipe> prdRcpList = prdRcpServ.searchProductRecipe(prdRcp.getPrName(),
					String.valueOf(CommonConstants.SEARCH_OPTION_EXACT), "", "", 0, 0);

			if (prdRcpList.size() > 0) {
				sessionModel.put("error", "Product recipe name already exist.");
				holdPrValuesTab1(prdRcp, sessionModel);
				return false;
			}
		}		

		if (screenMode.equals(CommonConstants.SCREEN_MODE_EDIT)) {
			//additional checking for both prName and prCode modification
			String retrievedPrCode = (String) sessionModel.get("tempPrCode");
			String retrievedPrName = (String) sessionModel.get("tempPrName");
			
			logger.debug("saveFuncErrorCheck() mode={},retrievedPrCode={},code={},retrievedPrName={},name={}", screenMode,
					retrievedPrCode, prdRcp.getPrCode(), retrievedPrName, prdRcp.getPrName());

			if (!prdRcp.getPrCode().equals("")) {
				if (!retrievedPrCode.trim().equals(prdRcp.getPrCode().trim())) {
					List<ProductRecipe> prdRcpList = prdRcpServ.searchProductId(prdRcp.getPrCode().trim(),
							String.valueOf(CommonConstants.SEARCH_OPTION_EXACT));
					
					if (prdRcpList.size() > 0) {
						sessionModel.put("error", "Product recipe Ts No (" + prdRcp.getPrCode() + ") already exist.");
						holdPrValuesTab1(prdRcp, sessionModel);
						return false;
					}
				}
			}

			if (!prdRcp.getPrName().equals("")) {
				if (!retrievedPrName.trim().equals(prdRcp.getPrName().trim())) {

					List<ProductRecipe> prdRcpList = prdRcpServ.searchProductRecipe(prdRcp.getPrName().trim(),
							String.valueOf(CommonConstants.SEARCH_OPTION_EXACT), "", "", 0, 0);
					
					if (prdRcpList.size() > 0) {
						sessionModel.put("error", "Product recipe name (" + prdRcp.getPrName() + ") already exist.");
						holdPrValuesTab1(prdRcp, sessionModel);
						return false;
					}
				}
			}
		}

		return true;
	}

	private void holdPrValuesTab1(ProductRecipe prdRcp, Map<String, Object> sessionModel) {
		ProductRecipe pr = (ProductRecipe) sessionModel.get("productRecipeItems");
		pr.setPrCode(prdRcp.getPrCode());
		pr.setPrName(prdRcp.getPrName());
		pr.setPrOtherName1(prdRcp.getPrOtherName1());
		pr.setPrOtherName2(prdRcp.getPrOtherName2());
		pr.setPrOtherName3(prdRcp.getPrOtherName3());
		pr.setPrOtherName4(prdRcp.getPrOtherName4());
		pr.setPrOtherName5(prdRcp.getPrOtherName5());
		pr.setRemarks(prdRcp.getRemarks());
	}

	@GetMapping("/main/product/productFormRegGetData")
	public ModelAndView retrieveRegTableData(HttpServletRequest request, @RequestParam(name = "id") String countryId,
			HttpSession session) {

		sessionModel.put("currTab", "finalDecision");
		sessionModel.remove("error");

		String countryName = refCountryServ.searchCountry(countryId, "", String.valueOf(CommonConstants.SEARCH_OPTION_EXACT), "", "", "")
				.get(0).getCountryName();
		sessionModel.put("regHeader", "Regulation - " + countryName);

		List<PrIngredient> prIngTable = (List<PrIngredient>) sessionModel.get("prIngItems");
	//	List<Regl> regTable = new ArrayList<Regl>();

		for (PrIngredient prIng : prIngTable) {

			if (prIng.getRefType() == CommonConstants.RECORD_TYPE_ID_RAW_MATL) {
				//Regl regData = new Regl();
				//List<Regl> reg = regServ.searchReglList(prIng.getRefId(), countryId);
				List<ReglFileSearch> obj = rfServ.searchFileAndInfoByCriteria(prIng.getRefId(), countryId);
				sessionModel.put("countryRegItems", obj);

			/*	if (reg.size() != 0) {
					regData.setRawMatlName(
							rawMatlServ.searchRawMaterial("", "", prIng.getRefId(), 0).get(0).getRawMatlName());
					regData.setCountryName(countryName);
					regData.setRefUrl1(reg.get(0).getRefUrl1());
					regData.setRefUrl2(reg.get(0).getRefUrl2());
					regData.setRefUrl3(reg.get(0).getRefUrl3());
					regData.setRefUrl4(reg.get(0).getRefUrl4());
					regData.setRefUrl5(reg.get(0).getRefUrl5());
					regData.setRemarks(reg.get(0).getRemarks());

					List<ReglDoc> regDocList = regDocServ.findReglDocByRegId(reg.get(0).getRegId());

					if (regDocList.size() == 4) {
						regDocList.get(3).setFileName(".....");
					}

					regData.setRegDocList(regDocList);
					regTable.add(regData);
				}*/
			}
		}

		

		return new ModelAndView("main/product/productForm", sessionModel);
	}

	@GetMapping("/main/product/productFormDownloadRegFile")
	public ResponseEntity<byte[]> downloadRegulationFile(HttpServletRequest request,
			@RequestParam(name = "id") int regDocId) {

		//ReglDoc regDoc = regDocServ.findReglDocById(regDocId);
		ReglFileDto regDoc = rfServ.findDtoById(regDocId);

		MediaType mt = MediaTypeUtils.getMediaTypeForFileName(servletContext, regDoc.getFileName());

		return ResponseEntity.ok().contentType(mt)
				.header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + regDoc.getFileName() + "\"")
				.body(regDoc.getContentObject());
	}

	// FSGS) KarVei Add 26/02/2021 - Search function for rmSearchModal
	private void retrieveMatlDataModal(String rawMat, String tsNo, String manufacturer, String supplier, String exp1,
			String exp2, String exp3, String exp4, Map<String, Object> sessionModel) {
		DateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
		List<RawMaterial> rm = rawMatlServ.searchRawMaterial(rawMat, exp1, 0, RecordStatusEnum.SUBMITTED.intValue());

		for (RawMaterial rmList : rm) {
			String joinTsNo = "";
			String joinManufac = "";
			String joinSupplier = "";
			String joinVipdDate = "";
			String joinDeclareDate = "";

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
		}

		sessionModel.put("materialLists", rm);
	}

	@RequestMapping(value = "/main/product/productFormIngReg", method = RequestMethod.GET)
	@ResponseBody
	public Map<String, Object> getIngRegulation(@RequestParam(name = "rawMatlName") String rawMatlName,
			@RequestParam(name = "countryId") String countryId, @RequestParam(name = "prCode") String prCode,
			HttpSession session) {

		Map<String, Object> regIngModel = new HashMap<String, Object>();
		RawMaterial rm = rawMatlServ
				.searchRawMaterial(rawMatlName, String.valueOf(CommonConstants.SEARCH_OPTION_EXACT), 0, 0).get(0);
		List<RmManf> rmManfItems = rmManfServ.searchRmManufacturer(rm.getRawMatlId(), "", "", "", "");
		List<PrRmReg> prRmRegItems = (List<PrRmReg>) sessionModel.get("prRmRegItems");
		String countryName = refCountryServ.searchCountry(countryId, "", String.valueOf(CommonConstants.SEARCH_OPTION_EXACT), "", "", "")
				.get(0).getCountryName();

		if (prRmRegItems.size() > 0) {
			List<PrRmReg> prRmRegList = new ArrayList<PrRmReg>();
			for (PrRmReg prRmReg : prRmRegItems) {
				if (prRmReg.getCountryId().equals(countryId) && prRmReg.getRawMatlId() == rm.getRawMatlId()
						&& prRmReg.getPrCode().equals(prCode)) {
					PrRmReg prRmRegData = new PrRmReg();
					prRmRegData.setRegDocId(prRmReg.getRegDocId());

					prRmRegList.add(prRmRegData);
				}
			}
			regIngModel.put("prRmRegItems", prRmRegList);
		}

		for (RmManf rmManf : rmManfItems) {
			rmManf.setManfRecStatus(getFinalStatusDesc(rmStatServ.getRmFinalStatus(rm.getRawMatlId(), countryId, 0)));
		}

		//TODO : CR
		List<ReglFileSearch> obj = rfServ.searchFileAndInfoByCriteria(rm.getRawMatlId(), countryId);		
		if(!obj.isEmpty()) {
			regIngModel.put("ingRegItems", obj);
			regIngModel.put("ingRegFileItems", obj);
		}
		
		/*
		List<Regl> reg = regServ.searchReglList(rm.getRawMatlId(), countryId);
		if (reg.size() != 0) {
			List<ReglDoc> regDocList = regDocServ.findReglDocByRegId(reg.get(0).getRegId());
			regIngModel.put("ingRegItems", reg);

			if (regDocList.size() != 0) {
				for (ReglDoc regDoc : regDocList) {
					regDoc.setRegDocCatGrpName(
							regGrpServ.findOneById(regDoc.getRegDocCatGrpId()).getGrpName());
					regDoc.setRegDocCatName(regCatServ.findOneById(regDoc.getRegDocCatId()).getCatName());
				}

				regIngModel.put("ingRegFileItems", regDocList);
			}

		}*/

		regIngModel.put("rmManfItems", rmManfItems);
		regIngModel.put("rawMatlItems", rm);
		regIngModel.put("countryName", countryName);

		return regIngModel;
	}

	@RequestMapping(value = "/main/product/productFormSaveReg", method = RequestMethod.GET)
	@ResponseBody
	public void saveIngReg(@RequestParam(name = "id") int[] checkedRow,
			@RequestParam(name = "rawMatlName") String rawMatlName, @RequestParam(name = "countryId") String countryId,
			@RequestParam(name = "prCode") String prCode, HttpSession session) {

		List<PrRmReg> prRmRegItems = (List<PrRmReg>) sessionModel.get("prRmRegItems");

		int rawMatlId = rawMatlServ
				.searchRawMaterial(rawMatlName, String.valueOf(CommonConstants.SEARCH_OPTION_EXACT), 0, 0).get(0)
				.getRawMatlId();

		if (prRmRegItems.size() > 0) {
			for (int j = 0; j < prRmRegItems.size(); j++) {
				if (prRmRegItems.get(j).getCountryId().equals(countryId)
						&& prRmRegItems.get(j).getRawMatlId() == rawMatlId
						&& prRmRegItems.get(j).getPrCode().equals(prCode)) {
					prRmRegItems.remove(j);
				}
			}
		}

		if (checkedRow.length != 0) {
			for (int i = 0; i < checkedRow.length; i++) {
				PrRmReg prRmReg = new PrRmReg();
				prRmReg.setPrCode(prCode);
				prRmReg.setRawMatlId(rawMatlId);
				prRmReg.setCountryId(countryId);
				prRmReg.setRegDocId(checkedRow[i]);

				prRmRegItems.add(prRmReg);
			}
		}

		sessionModel.put("prRmRegItems", prRmRegItems);
	}
	
	private String getFlavStatusName(int flavStatId) {
		String name = "";
		name = fsServ.findOneById(flavStatId).getFsName();
		return name;
	}
	
	/**
	 * Return Flavor Status ID based on rank
	 * @param prIngTable
	 * @return
	 */
	private Integer generateFinalFlavorStatus(List<PrIngredient> prIngTable) {
		List<Integer> rankList = new ArrayList<Integer>();
		for (PrIngredient prIngredient : prIngTable) {
			if (prIngredient.getRefType() == CommonConstants.RECORD_TYPE_ID_RAW_MATL) {
				RawMaterialDto rm = rawMatlServ.findRawMatlDto("", "", prIngredient.getRefId(), 0);
				if (Objects.nonNull(rm) && Objects.nonNull(rm.getFlavStatusId())) {
					FlavorStatusDto fs = fsServ.findOneById(rm.getFlavStatusId());
					rankList.add(fs.getFsRank());
				}

			} else if (prIngredient.getRefType() == CommonConstants.RECORD_TYPE_ID_PROD_RECP) {
				ProductRecipeDto pr = prdRcpServ.findProductRecipeDto("", "", "", "", prIngredient.getRefId(), 0, "",
						"");
				if (Objects.nonNull(pr) && Objects.nonNull(pr.getFlavStatusId())) {
					FlavorStatusDto fs = fsServ.findOneById(pr.getFlavStatusId());
					rankList.add(fs.getFsRank());
				}
			}
		}

		if (Objects.nonNull(rankList) && !rankList.isEmpty()) {
			int max = rankList.stream().max(Integer::compareTo).get();
			FlavorStatusDto fs = fsServ.findByRank(max);

			if (Objects.nonNull(fs))
				return fs.getFsId();
		}

		return null;
	}

}
