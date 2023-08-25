package com.fms.pfc.service.api.main;

import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fms.pfc.domain.model.main.RmStat;
import com.fms.pfc.domain.model.main.RmStatGrp;
import com.fms.pfc.repository.main.api.RmStatGrpRepository;
import com.fms.pfc.repository.main.api.RmStatRepository;

@Service
public class RmStatService {

	@Autowired
	private RmStatRepository rmStatRepo;

	@Autowired
	private RmStatGrpRepository rmStatGrpRepo;

	public List<RmStatGrp> searchRmStatGrp(int rawMatlId) {
		return rmStatGrpRepo.searchRmStatGrp(rawMatlId);
	}

	public Integer getRmFinalStatus(int rawMatlId, String countryId, int manfId) {
		return rmStatRepo.getRmFinalStatus(rawMatlId, countryId, manfId);
	}

	public List<RmStat> searchRmStat(int rawMatlId) {
		return rmStatRepo.searchRmStat(rawMatlId);
	}

	public void addRmStat(String matlName, int manfId, int vipdStatus, int finalStatus, String countryId,
			String creatorId) {
		rmStatRepo.addRmStat(matlName, manfId, vipdStatus, finalStatus, countryId, creatorId);
	}

	public void updRmStat(String countryId, int rawMatlId, int finalStatus, String modifierId, Date date, String dataChgFlag) {
		rmStatRepo.updRmStat(countryId, rawMatlId, finalStatus, modifierId, date, dataChgFlag);
	}

	public void updRmStatVipd(String rawMatlName, int manfId, int vipdStatus, String countryId, String modifierId,
			Date date) {
		rmStatRepo.updRmStatVipd(rawMatlName, manfId, vipdStatus, countryId, modifierId, date);
	}

	public void delRmStat(int rawMatlId) {
		rmStatRepo.delRmStat(rawMatlId);
	}

	public void delRmStatByManf(int rawMatlId, int manfId) {
		rmStatRepo.delRmStatByManf(rawMatlId, manfId);
	}

	public void delRmStatByCountry(int rawMatlId, String countryId) {
		rmStatRepo.delRmStatByCountry(rawMatlId, countryId);
	}
	
	public void updRmStatByRmStatId(int rmStatId, int manfId, int vipdStatus, String countryId, String modifierId, Date date) {
		rmStatRepo.updRmStatByRmStatId(rmStatId, manfId, vipdStatus, countryId, modifierId, date);
	}
	
	public void updRmStatDataChgFlag(int rmStatId, String countryId, int rawMatlId, String currentFlag, String newFlag) {
		rmStatRepo.updRmStatDataChgFlag(rmStatId, countryId, rawMatlId, currentFlag, newFlag);
	}

}