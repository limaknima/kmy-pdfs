package com.fms.pfc.service.api.main;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fms.pfc.domain.dto.CountryDto;
import com.fms.pfc.domain.model.main.PrRmStat;
import com.fms.pfc.domain.model.main.ProductRecipe;
import com.fms.pfc.repository.main.api.PrRmStatRepository;
import com.fms.pfc.service.api.base.CountryService;

@Service
public class PrRmStatService {

	private PrRmStatRepository prRmRepo;
	private CountryService refCountryServ;

	@Autowired
	public PrRmStatService(PrRmStatRepository prRmRepo, CountryService refCountryServ) {
		super();
		this.prRmRepo = prRmRepo;
		this.refCountryServ = refCountryServ;
	}

	public List<PrRmStat> searchPrRmStat(int prId, String countryId) {
		return prRmRepo.searchPrRmStat(prId, countryId);
	}

	public List<PrRmStat> searchPrRmDetails(int prId) {
		return prRmRepo.searchPrRmDetails(prId);
	}

	public void addPrRmStat(String prCode, int rawMatlId, double totalPerc, int sysFinalStatus, int finalStatus,
			String countryId, String remarks, String creatorId, Date date) {
		prRmRepo.addPrRmStat(prCode, rawMatlId, totalPerc, sysFinalStatus, finalStatus, countryId, remarks, creatorId,
				date);
	}

	public void updPrRmStat(int finalStatus, String remarks, String prCode, int rawMatlId, String countryId,
			String modifierId, Date date) {
		prRmRepo.updPrRmStat(finalStatus, remarks, prCode, rawMatlId, countryId, modifierId, date);
	}
	
	public void updPrRmStat(int finalStatus, String remarks, String prCode, int rawMatlId, String countryId,
			String modifierId, Date date, int rmRefChg) {
		prRmRepo.updPrRmStat(finalStatus, remarks, prCode, rawMatlId, countryId, modifierId, date, rmRefChg);
	}

	public void updPrRmStatByMatl(int sysFinalStatus, String modifierId, Date date, int prId, int rawMatlId,
			String countryId) {
		prRmRepo.updPrRmStatByMatl(sysFinalStatus, modifierId, date, prId, rawMatlId, countryId);
	}

	public void updPrRmStatFlag(String affectedFlag, String modifierId, Date date, int prId, int rawMatlId,
			String countryId, int prRmStatId) {
		prRmRepo.updPrRmStatFlag(affectedFlag, modifierId, date, prId, rawMatlId, countryId, prRmStatId);
	}

	public void updPrRmStatByFlag(int prId) {
		prRmRepo.updPrRmStatByFlag(prId);
	}

	public void delPrRmStat(int prId) {
		prRmRepo.delPrRmStat(prId);
	}

	public void delPrRmStatByMatl(int prId, int rawMatlId) {
		prRmRepo.delPrRmStatByMatl(prId, rawMatlId);
	}

	public void delPrRmStatByCountry(int prId, String countryId) {
		prRmRepo.delPrRmStatByCountry(prId, countryId);
	}
	
	public List<PrRmStat> searchProductLinkage(int recordStatus, int rawMatlId, List<String> countryId, String affFlag, String onHoldFlag) {
		List<PrRmStat> prRmStats = new ArrayList<PrRmStat>();
		List<Object[]> objs = prRmRepo.searchProductLinkage(recordStatus, rawMatlId, countryId, affFlag, onHoldFlag);
		for (Object[] o : objs) {
			PrRmStat stat = new PrRmStat();
			stat.setPrRmStatId((Integer) o[0]);
			stat.setPrId((Integer) o[1]);
			stat.setRawMatlId((Integer) o[2]);
			stat.setCountryId((String) o[3]);
			stat.setPrCode((String) o[4]);
			stat.setPrName((String) o[5]);
			stat.setRawMatlName((String) o[6]);
			stat.setCountryName((String) o[7]);
			prRmStats.add(stat);
		}

		return prRmStats;
	}
	
	public List<ProductRecipe> searchProductLinkage2(int recordStatus, int rawMatlId, String affFlag, String onHoldFlag) {
		List<ProductRecipe> productRecipes = new ArrayList<ProductRecipe>();
		List<String> countryId = refCountryServ.findByCriteria("", "", "", "", "Y", "").stream()
				.map(arg0 -> arg0.getCountryId()).collect(Collectors.toList());
		List<Object[]> objs = prRmRepo.searchProductLinkage(recordStatus, rawMatlId, countryId, affFlag, onHoldFlag);
		for (Object[] o : objs) {
			ProductRecipe pr = new ProductRecipe();
			pr.setPrId((Integer) o[1]);
			pr.setPrCode((String) o[4]);
			pr.setPrName((String) o[5]);
			pr.setRecordStatus((Integer) o[8]);
			pr.setOnHoldFlag(String.valueOf(o[10]));
			productRecipes.add(pr);
		}

		productRecipes = productRecipes.stream().filter(distinctByKey(ProductRecipe::getPrId))
				.collect(Collectors.toList());

		return productRecipes;
	}
	
	public List<ProductRecipe> searchProductLinkageAll(int recordStatus, int rawMatlId, String affFlag, String onHoldFlag){
		List<ProductRecipe> productRecipes = new ArrayList<ProductRecipe>();
		List<String> countryId = refCountryServ.findByCriteria("", "", "", "", "Y", "").stream()
				.map(arg0 -> arg0.getCountryId()).collect(Collectors.toList());
		List<Object[]> objs = prRmRepo.searchProductLinkageAll(recordStatus, rawMatlId, countryId, affFlag, onHoldFlag);
		for (Object[] o : objs) {
			ProductRecipe pr = new ProductRecipe();
			pr.setPrId((Integer) o[1]);
			pr.setPrCode((String) o[4]);
			pr.setPrName((String) o[5]);
			pr.setRecordStatus((Integer) o[8]);
			pr.setOnHoldFlag(String.valueOf(o[10]));
			productRecipes.add(pr);
		}

		productRecipes = productRecipes.stream().filter(distinctByKey(ProductRecipe::getPrId))
				.collect(Collectors.toList());

		return productRecipes;
	}
	
	public static <T> Predicate<T> distinctByKey(Function<? super T, ?> keyExtractor) {
	    Set<Object> seen = ConcurrentHashMap.newKeySet();
	    return t -> seen.add(keyExtractor.apply(t));
	}
	
	public List<PrRmStat> searchPrRmStat(int rawMatlId, String affectedFlag, String countryId){
		return prRmRepo.searchPrRmStat(rawMatlId, affectedFlag, countryId);
	}
	
	public PrRmStat findUnique(int prId, int rmId, String countryId) {
		return prRmRepo.findUnique(prId, rmId, countryId);
	}
}
