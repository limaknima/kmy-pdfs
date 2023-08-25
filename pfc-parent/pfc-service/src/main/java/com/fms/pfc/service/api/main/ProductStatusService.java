package com.fms.pfc.service.api.main;

import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fms.pfc.domain.model.main.PrStat;
import com.fms.pfc.repository.main.api.ProductStatusRepository;

@Service
public class ProductStatusService {

	@Autowired
	private ProductStatusRepository psRepo;

	public List<PrStat> searchProductStatus(int prId, String para, String countryId, int status) {
		return psRepo.searchProductStatus(prId, para, countryId, status);
	}

	public void addPrStat(String prCode, int finalStatus, String countryId, String remarks, String remarksUserId,
			String creatorId, Date date) {
		psRepo.addPrStat(prCode, finalStatus, countryId, remarks, remarksUserId, creatorId, date);
	}

	public void updPrStat(int prStatId, int finalStatus, String countryId, String remarks, String remarksUserId,
			String modifierId, Date date) {
		psRepo.updPrStat(prStatId, finalStatus, countryId, remarks, remarksUserId, modifierId, date);
	}

	public void updPrStatFlag(int prId, String countryId, int finalStatus, String modifierId, Date date) {
		psRepo.updPrStatFlag(prId, countryId, finalStatus, modifierId, date);
	}

	public void delPrStat(int prId) {
		psRepo.delPrStat(prId);
	}

	public void delDetailPrStat(int prStatId) {
		psRepo.delDetailPrStat(prStatId);
	}
}
