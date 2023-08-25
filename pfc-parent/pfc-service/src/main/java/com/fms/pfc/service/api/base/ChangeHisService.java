package com.fms.pfc.service.api.base;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;

import com.fms.pfc.domain.converter.ChangeHistoryConverter;
import com.fms.pfc.domain.dto.ChangeHistoryDto;
import com.fms.pfc.domain.model.ChangeHistory;
import com.fms.pfc.repository.base.api.ChangeHisRepository;

@Service
public class ChangeHisService {

	private ChangeHisRepository chgHisRepo;
	private MessageSource msgSource;
	private DocumentTypeService docTypeServ;
	private ChangeHistoryConverter chgConv;

	@Autowired
	public ChangeHisService(ChangeHisRepository chgHisRepo, MessageSource msgSource, DocumentTypeService docTypeServ,
			ChangeHistoryConverter chgConv) {
		super();
		this.chgHisRepo = chgHisRepo;
		this.msgSource = msgSource;
		this.docTypeServ = docTypeServ;
		this.chgConv = chgConv;
	}

	public List<ChangeHistory> searchChgHisDetail(String chgHisDate) {
		return chgHisRepo.searchChgHistoryDetail(chgHisDate);
	}

	public List<ChangeHistory> searchChgHisDetail(String chgHisDate, int recTypeId, String recRef) {
		return chgHisRepo.searchChgHistoryDetail(chgHisDate, recTypeId, recRef);
	}

	public ChangeHistory findById(int id) {
		return chgHisRepo.findById(id).orElse(null);
	}

	public List<ChangeHistoryDto> findByCriteria(String chgHisDate, int recTypeId, String recRef) {
		List<ChangeHistory> chgHist = chgHisRepo.searchChgHistoryDetail(chgHisDate, recTypeId, recRef);
		List<ChangeHistoryDto> chgHisDto = chgConv.entityToDtoList(chgHist);
		List<ChangeHistoryDto> chgHisDataList = new ArrayList<ChangeHistoryDto>();
		chgHisDataList.addAll(chgHisDto);

		// Set value according to field name
		for (int i = 0; i < chgHisDataList.size(); i++) {
			convertVipdAndFinalStatus(chgHisDataList, i);
			convertRecordStatus(chgHisDataList, i);
			convertDocType(chgHisDataList, i);
		}

		chgHisDataList = checkRmManfSuppl(chgHisDataList);

		return chgHisDataList;
	}

	/*
	 * When field name equals Final Status / VIPD Status
	 */
	private void convertVipdAndFinalStatus(List<ChangeHistoryDto> chgHisData, int i) {
		if (chgHisData.get(i).getFieldName().equals("Final Status")
				|| chgHisData.get(i).getFieldName().equals("VIPD Status")
				|| chgHisData.get(i).getFieldName().equals("System Final Status")) {
			// Set Final Status name
			chgHisData.get(i).setOldValue(setFinalStatus(chgHisData.get(i).getOldValue()));
			chgHisData.get(i).setNewValue(setFinalStatus(chgHisData.get(i).getNewValue()));
		}
	}

	private void convertRecordStatus(List<ChangeHistoryDto> chgHisData, int i) {
		if (chgHisData.get(i).getFieldName().equals("Record Status")) {
			// Set Record Status name
			chgHisData.get(i).setOldValue(setRecordStatus(chgHisData.get(i).getOldValue()));
			chgHisData.get(i).setNewValue(setRecordStatus(chgHisData.get(i).getNewValue()));
		}
	}

	/*
	 * When field name equals to Document Type
	 */
	private void convertDocType(List<ChangeHistoryDto> chgHisData, int i) {
		if (chgHisData.get(i).getFieldName().equals("Document Type")) {
			int oldDocTypeId = Integer.parseInt(chgHisData.get(i).getOldValue());
			int newDocTypeId = Integer.parseInt(chgHisData.get(i).getNewValue());

			chgHisData.get(i).setOldValue(docTypeServ.searchDocType(oldDocTypeId).getName());
			chgHisData.get(i).setNewValue(docTypeServ.searchDocType(newDocTypeId).getName());
		}
	}

	/*
	 * Set Final Status name
	 */
	private String setFinalStatus(String getStatus) {

		int status = 0;
		try {
			status = Integer.parseInt(getStatus);
		} catch (Exception e) {
			return "";
		}

		switch (status) {
		case 1:
			getStatus = msgSource.getMessage("vfStatus_1", null, Locale.getDefault());
			break;
		case 2:
			getStatus = msgSource.getMessage("vfStatus_2", null, Locale.getDefault());
			break;
		case 3:
			getStatus = msgSource.getMessage("vfStatus_3", null, Locale.getDefault());
			break;
		case 4:
			getStatus = msgSource.getMessage("vfStatus_4", null, Locale.getDefault());
			break;
		default:
			getStatus = "";
			break;
		}

		return getStatus;
	}

	/*
	 * Set Record Status name
	 */
	private String setRecordStatus(String getStatus) {

		int status = 0;
		try {
			status = Integer.parseInt(getStatus);
		} catch (Exception e) {
			return "";
		}

		switch (status) {
		case 1:
			getStatus = msgSource.getMessage("vrStatus_1", null, Locale.getDefault());
			break;
		case 2:
			getStatus = msgSource.getMessage("vrStatus_2", null, Locale.getDefault());
			break;
		case 3:
			getStatus = msgSource.getMessage("vrStatus_3", null, Locale.getDefault());
			break;
		case 4:
			getStatus = msgSource.getMessage("vrStatus_4", null, Locale.getDefault());
			break;
		case 5:
			getStatus = msgSource.getMessage("vrStatus_5", null, Locale.getDefault());
			break;
		case 6:
			getStatus = msgSource.getMessage("vrStatus_6", null, Locale.getDefault());
			break;
		case 7:
			getStatus = msgSource.getMessage("vrStatus_7", null, Locale.getDefault());
			break;
		case 8:
			getStatus = msgSource.getMessage("vrStatus_8", null, Locale.getDefault());
			break;
		case 9:
			getStatus = msgSource.getMessage("vrStatus_9", null, Locale.getDefault());
			break;
		case 10:
			getStatus = msgSource.getMessage("vrStatus_10", null, Locale.getDefault());
			break;
		case 11:
			getStatus = msgSource.getMessage("vrStatus_11", null, Locale.getDefault());
			break;
		case 12:
			getStatus = msgSource.getMessage("vrStatus_12", null, Locale.getDefault());
			break;
		case 13:
			getStatus = msgSource.getMessage("vrStatus_13", null, Locale.getDefault());
			break;
		default:
			getStatus = "";
			break;

		}

		return getStatus;
	}

	/*
	 * When field name equals to Supplier ID
	 */
	private List<ChangeHistoryDto> checkRmManfSuppl(List<ChangeHistoryDto> chgHisData) {
		List<ChangeHistoryDto> chdata = new ArrayList<ChangeHistoryDto>();
		for (ChangeHistoryDto ch : chgHisData) {
			if (ch.getTableName().equals("Raw Material Manufacturer Supplier")
					&& ch.getFieldName().equals("Supplier ID")) {
				if (ch.getFuncType() == 3 && (ch.getNewValue() == null || ch.getNewValue().isEmpty())) {
					continue;
				}
			}
			chdata.add(ch);
		}

		return chdata;
	}
}