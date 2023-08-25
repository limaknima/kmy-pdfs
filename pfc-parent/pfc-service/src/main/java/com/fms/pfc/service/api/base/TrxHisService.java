package com.fms.pfc.service.api.base;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.transaction.Transactional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fms.pfc.domain.dto.LabelAndValueDto;
import com.fms.pfc.domain.model.TranxHistory;
import com.fms.pfc.domain.model.TrxHistoryLog;
import com.fms.pfc.repository.base.api.TrxHisRepository;
import com.fms.pfc.repository.base.api.TrxHistoryLogRepository;

@Service
public class TrxHisService {

	private final Logger logger = LoggerFactory.getLogger(TrxHisService.class);
			
	private TrxHisRepository trxHisRepo;
	private TrxHistoryLogRepository trxHistLogRepo;

	@Autowired
	public TrxHisService(TrxHisRepository trxHisRepo, TrxHistoryLogRepository trxHistLogRepo) {
		super();
		this.trxHisRepo = trxHisRepo;
		this.trxHistLogRepo = trxHistLogRepo;
	}

	public List<TranxHistory> searchTranxHistory(String userId, String orgId, String dateFr, String dateTo, int insert,
			int update, int delete, int view, int print, int search) {
		return trxHisRepo.searchTranxHistory(userId, orgId, dateFr, dateTo, insert, update, delete, view, print, search);
	}

	public List<TranxHistory> searchTxHis(String userId, String dateFr, String dateTo, String recordRef, String exp, int recordType) {
		return trxHisRepo.searchTxHis(userId, dateFr, dateTo, recordRef, exp, recordType);
	}

	public void addTrxHistory(Date logDateTime, String logDesc, String userId, int funcType, String recordRef,
			int recordType, String searcString) {
		trxHisRepo.addTrxHistory(logDateTime, logDesc, userId, funcType, recordRef, recordType, searcString);
	}
	
	//////////
	
	public TrxHistoryLog findOneById(int id) {
		return trxHistLogRepo.findById(id).orElse(null);
	}

	public List<TrxHistoryLog> findByCriteria(String userId, String dateFr, String dateTo, String recordRef, String exp,
			int recordType, List<Integer> funcTypes) {
		return trxHistLogRepo.findByCriteria(userId, dateFr, dateTo, recordRef, exp, recordType, funcTypes);
	}
	
	public List<TrxHistoryLog> findByCriteria(String userId, String logDate, String recordRef, String exp,
			int recordType, List<Integer> funcTypes) {
		//logger.debug("findByCriteria() userId={},logDate={},recordRef={},exp={},recordType={},funcTypes={}", userId,
		//		logDate, recordRef, exp, recordType, funcTypes);
		return trxHistLogRepo.findByCriteria(userId, logDate, recordRef, exp, recordType, funcTypes);
	}
	
	public List<LabelAndValueDto> getTrxYearsLabelAndValue(){
		List<LabelAndValueDto> lblVal = new ArrayList<LabelAndValueDto>();
		List<Integer> all = trxHistLogRepo.getYears();
		for (Integer idx : all) {
			LabelAndValueDto obj = new LabelAndValueDto(String.valueOf(idx), idx);
			lblVal.add(obj);
		}
		return lblVal;
	}

	@Transactional
	public TrxHistoryLog saveTrxHistoryLog(Date logDateTime, String logDesc, String userId, int funcType,
			String recordRef, int recordType, String searchStr) {

		TrxHistoryLog trx = new TrxHistoryLog(logDateTime, logDesc, userId, funcType, recordRef, recordType, searchStr);
		trxHistLogRepo.saveAndFlush(trx);

		return trx;
	}

}