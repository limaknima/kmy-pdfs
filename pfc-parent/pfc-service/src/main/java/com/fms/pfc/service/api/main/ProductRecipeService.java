package com.fms.pfc.service.api.main;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fms.pfc.domain.converter.main.ProductRecipeConverter;
import com.fms.pfc.domain.dto.LabelAndValueDto;
import com.fms.pfc.domain.dto.main.ProductRecipeDto;
import com.fms.pfc.domain.model.main.PrNameDto;
import com.fms.pfc.domain.model.main.ProductRecipe;
import com.fms.pfc.domain.model.main.ProductRecipeList;
import com.fms.pfc.exception.CommonException;
import com.fms.pfc.repository.main.api.PrNameRepository;
import com.fms.pfc.repository.main.api.ProductRecipeListRepository;
import com.fms.pfc.repository.main.api.ProductRecipeRepository;

@Service
public class ProductRecipeService {
	
	private final Logger logger = LoggerFactory.getLogger(ProductRecipeService.class);

	private ProductRecipeRepository prodRecipeRepo;
	private ProductRecipeListRepository prodRecipeListRepo;
	private PrNameRepository prNameRepo;
	private ProductRecipeConverter prConv;

	@Autowired
	public ProductRecipeService(ProductRecipeRepository prodRecipeRepo,
			ProductRecipeListRepository prodRecipeListRepo, PrNameRepository prNameRepo, ProductRecipeConverter prConv) {
		super();
		this.prodRecipeRepo = prodRecipeRepo;
		this.prodRecipeListRepo = prodRecipeListRepo;
		this.prNameRepo = prNameRepo;
		this.prConv = prConv;
	}

	public List<ProductRecipeList> searchProductRecipeList(String prName, String exp1, String prCode, String exp2, 
			String rawrawMatlName, String exp3, String interProdName, String exp4, int recordStatus, String currUser,
			int matFlag, int imPrFlag){
		
		return prodRecipeListRepo.searchProductRecipeList(prName, exp1, prCode, exp2, rawrawMatlName, exp3,
				interProdName, exp4, recordStatus, currUser, matFlag, imPrFlag);
	}
	
	public List<ProductRecipe> searchProductRecipe(String prName, String exp1, String prCode, String exp2, int prId,
			int recordStatus) {
		return prodRecipeRepo.searchProductRecipe(prName, exp1, prCode, exp2, prId, recordStatus);
	}
	
	public List<ProductRecipe> searchProductRecipe2(String prName, String exp1, String prCode, String exp2, int prId,
			int recordStatus, String otherName, String otherNameOpt) {
		return prodRecipeRepo.searchProductRecipe2(prName, exp1, prCode, exp2, prId, recordStatus, otherName,
				otherNameOpt);
	}

	public List<ProductRecipe> searchIntermediateProduct(String prName, String exp1, String prCode, String exp2, int prId,
			int recordStatus) {
		return prodRecipeRepo.searchIntermediateProduct(prName, exp1, prCode, exp2, prId, recordStatus);
	}
	
	public List<ProductRecipe> searchProductId(String prCode, String para) {
		return prodRecipeRepo.searchProductId(prCode, para);
	}
	
	public List<ProductRecipe> searchProductIdByName(String prCode, String para) {
		return prodRecipeRepo.searchProductIdByName(prCode, para);
	}

	public List<ProductRecipe> searchProductName(int prId, String para) {
		return prodRecipeRepo.searchProductName(prId, para);
	}
	
	public List<ProductRecipe> searchProductLinkage(int rawMatlId) {
		return prodRecipeRepo.searchProductLinkage(rawMatlId);
	}

	public List<ProductRecipe> searchIntermediate() {
		return prodRecipeRepo.searchIntermediate();
	}

	public List<ProductRecipe> searchProductReport(String prCode, String prName, String rmName, String ipName, String prOtherName) {
		return prodRecipeRepo.searchProductReport(prCode, prName, rmName, ipName, prOtherName);
	}

	public void delProductRecipe(Integer prId) {
		prodRecipeRepo.delProductRecipe(prId);
	}

	public void addProductRecipe(String prCode, String prName, String remarks, String remarksUserId, Double totalPerc,
			Integer recordStatus, String creatorId, String otherName1, String otherName2, String otherName3,
			String otherName4, String otherName5, Date date, Integer flavStatus) {
		prodRecipeRepo.addProductRecipe(prCode, prName, remarks, remarksUserId, totalPerc, recordStatus, creatorId,
				otherName1, otherName2, otherName3, otherName4, otherName5, date, flavStatus);
	}

	public void updProductRecipe(Integer prId, String prCode, String prName, String remarks, String remarksUserId,
			Double totalPerc, Integer recordStatus, String modifierId, String otherName1, String otherName2,
			String otherName3, String otherName4, String otherName5, Date date) {
		prodRecipeRepo.updProductRecipe(prId, prCode, prName, remarks, remarksUserId, totalPerc, recordStatus,
				modifierId, otherName1, otherName2, otherName3, otherName4, otherName5, date);
	}

	public void updProductRecipeStatus(Integer prId, Integer recordStatus, String modifierId, Date date, String remarks) {
		prodRecipeRepo.updProductRecipeStatus(prId, recordStatus, modifierId, date, remarks);
	}
	
	public List<Integer> getProductRecipe(String prCode, String prName, String exp, String rmName, String ipName){
		if("0".equals(exp)) {
			return prodRecipeRepo.getProductRecipeId("201", "301", prCode, prName);
		} else if ("1".equals(exp)) {
			return getRecipeWithRawMatOrIntermediate(prCode, prName, rmName, ipName);
		}else if ("2".equals(exp)) {
			return getRec1ipeWithRawMatAndIntermediate(prCode, prName, rmName, ipName);
		}
		return null;
	}
	
	private List<Integer> getRecipeWithRawMatOrIntermediate(String prCode, String prName, String rmName, String ipName){
		return prodRecipeRepo.getRecipeWithRawMatOrIntermediate("201", "301", prCode, prName, rmName, ipName);
	}
	
	private List<Integer> getRec1ipeWithRawMatAndIntermediate(String prCode, String prName, String rmName, String ipName){
		return prodRecipeRepo.getRecipeWithRawMatANDIntermediate("201", "301", prCode, prName, rmName, ipName);
	}
	
	public String getPRSQL(List<Integer>pridList) {
		return prodRecipeRepo.getPRSQL(pridList);
	}
	
	public void updProductRecipeWithOptLock(Integer prId, String prCode, String prName, String remarks,
			String remarksUserId, Double totalPerc, Integer recordStatus, String modifierId, String otherName1,
			String otherName2, String otherName3, String otherName4, String otherName5, Date date, Integer currentLock, Integer flavStatus)
			throws Exception {
		concurrentUpdateLockCheck(prId, currentLock);
		prodRecipeRepo.updProductRecipeWithOptLock(prId, prCode, prName, remarks, remarksUserId, totalPerc,
				recordStatus, modifierId, otherName1, otherName2, otherName3, otherName4, otherName5, date,
				currentLock, flavStatus);
	}

	public void updProductRecipeStatusWithOptLock(Integer prId, Integer recordStatus, String modifierId, Date date,
			Integer currentLock, String remarks) throws Exception {
		concurrentUpdateLockCheck(prId, currentLock);

		if (null == remarks || StringUtils.isEmpty(remarks))
			prodRecipeRepo.updProductRecipeStatusWithOptLock(prId, recordStatus, modifierId, date, currentLock);
		else
			prodRecipeRepo.updProductRecipeStatusWithOptLock(prId, recordStatus, modifierId, date, currentLock,
					remarks);
	}

	private void concurrentUpdateLockCheck(int prId, Integer currentLock) throws Exception {
		if (null == currentLock)
			return;

		Integer latesLock = searchProductRecipe("", "", "", "", prId, 0).get(0).getOptLock();
		logger.debug("concurrentUpdateLockCheck() prId={}, currLock={}, latestLock={}",prId, currentLock, latesLock);
		try {
			if (currentLock.intValue() != latesLock.intValue()) {
				throw new CommonException("Concurrent update occurred.", "Update Product Recipe");
			}

		} catch (Exception e) {
			throw e;
		}
	}
	
	public List<PrNameDto> findAllPrNames() {
		return prNameRepo.findAllPrNames();
	}
	
	public List<PrNameDto> findAllPrNames(int type, int recordStatus, String flag) {
		return prNameRepo.findAllPrNames(type, recordStatus, flag);
	}
	
	public List<LabelAndValueDto> getPrNameLabelAndValue(){
		List<LabelAndValueDto> lblVal = new ArrayList<LabelAndValueDto>();
		List<ProductRecipe> prs = this.searchProductRecipe("", "", "", "", 0, 0);
		for (ProductRecipe pr : prs) {
			LabelAndValueDto obj = new LabelAndValueDto(pr.getPrName(), pr.getPrId());
			lblVal.add(obj);
		}
		return lblVal;
	}
	
	public void updatePrOnHoldFlag (int prId, String flag, String modifierId, Date date, String remarks) {
		if(remarks!=null && StringUtils.isNotEmpty(remarks))
			prodRecipeRepo.updatePrOnHoldFlag(prId, flag, modifierId, date, remarks);
		else
			prodRecipeRepo.updatePrOnHoldFlag(prId, flag, modifierId, date);
	}
	
	public List<ProductRecipeDto> searchProdcutRecipeDto(String prName, String exp1, String prCode, String exp2,
			int prId, int recordStatus, String otherName, String otherNameOpt) {
		List<ProductRecipe> entityList = searchProductRecipe2(prName, exp1, prCode, exp2, prId, recordStatus, otherName,
				otherNameOpt);
		return prConv.entityToDtoList(entityList);

	}
	
	public ProductRecipeDto findProductRecipeDto(String prName, String exp1, String prCode, String exp2, int prId,
			int recordStatus, String otherName, String otherNameOpt) {

		List<ProductRecipeDto> dtoList = searchProdcutRecipeDto(prName, exp1, prCode, exp2, prId, recordStatus,
				otherName, otherNameOpt);

		if (dtoList != null && !dtoList.isEmpty())
			return dtoList.get(0);

		return null;
	}
	
	public void updProductRecipe(Integer prId, Integer fsId, String modifierId, Date date) {
		prodRecipeRepo.updProductRecipe(prId, fsId, modifierId, date);
	}
}