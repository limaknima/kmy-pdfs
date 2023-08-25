package com.fms.pfc.service.api.main;

import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fms.pfc.domain.model.main.RmManf;
import com.fms.pfc.domain.model.main.TsNoDto;
import com.fms.pfc.repository.main.api.RmManfRepository;
import com.fms.pfc.repository.main.api.TsNoRepository;

@Service
public class RmManfService {

	private RmManfRepository rmManfRepo;
	private TsNoRepository tsNoRepo;

	@Autowired
	public RmManfService(RmManfRepository rmManfRepo, TsNoRepository tsNoRepo) {
		super();
		this.rmManfRepo = rmManfRepo;
		this.tsNoRepo = tsNoRepo;
	}

	public List<RmManf> searchRmManufacturer(int rawMaterialId, String tsNo, String vendor, String expr1,
			String expr2) {
		return rmManfRepo.searchRmManufacturer(rawMaterialId, tsNo, vendor, expr1, expr2);
	}

	public List<RmManf> searchRenewaldate() {
		return rmManfRepo.searchRenewaldate();
	}
	
	public void delRmManf(int rawMatlId) {
		rmManfRepo.delRmManf(rawMatlId);
	}

	public void delDetailRmManf(int rawMatlId, int rmManfId) {
		rmManfRepo.delDetailRmManf(rawMatlId, rmManfId);
	}

	public void addRmManf(String matlName, String tsNo, int manfId, Date vipdDate, Date vipdAnnex2Date,
			String creatorId, String countryId, byte[] vipdObject, byte[] vipdAnnex2Object, String vipdFileName,
			String vipdAnnex2FileName, Date date) {
		rmManfRepo.addRmManf(matlName, tsNo, manfId, vipdDate, vipdAnnex2Date, creatorId, countryId, vipdObject,
				vipdAnnex2Object, vipdFileName, vipdAnnex2FileName, date);
	}

	public void updRmManf(int rawMatlId, String tsNo, int manfId, Date vipdDate, Date vipdAnnex2Date, String modifierId,
			String countryId, int rmManfId, byte[] vipdObject, byte[] vipdAnnex2Object, String vipdFileName,
			String vipdAnnex2FileName, Date date) {
		rmManfRepo.updRmManf(rawMatlId, tsNo, manfId, vipdDate, vipdAnnex2Date, modifierId, countryId, rmManfId,
				vipdObject, vipdAnnex2Object, vipdFileName, vipdAnnex2FileName, date);
	}
	
	public List<TsNoDto> findAllTsNos (int rmId, int prId){
		return tsNoRepo.findAllTsNos(rmId, prId);
	}
}