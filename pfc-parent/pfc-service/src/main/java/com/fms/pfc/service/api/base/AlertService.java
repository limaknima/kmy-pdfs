package com.fms.pfc.service.api.base;

import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fms.pfc.domain.model.Alert;
import com.fms.pfc.domain.model.Alertx;
import com.fms.pfc.domain.model.AlertxRecipient;
import com.fms.pfc.domain.model.AlertxRecipientPk;
import com.fms.pfc.repository.base.api.AlertRepository;
import com.fms.pfc.repository.base.api.AlertxRecipientRepository;
import com.fms.pfc.repository.base.api.AlertxRepository;

@Service
public class AlertService {

	private AlertRepository alertRepo;
	private AlertxRepository alertxRepo;
	private AlertxRecipientRepository alertxRecRepo;

	@Autowired
	public AlertService(AlertRepository alertRepo, AlertxRepository alertxRepo,
			AlertxRecipientRepository alertxRecRepo) {
		super();
		this.alertRepo = alertRepo;
		this.alertxRepo = alertxRepo;
		this.alertxRecRepo = alertxRecRepo;
	}

	public List<Alert> searchNotification(String dateFr, String dateTo, String recRef, String subject, String exp1,
			String exp2, String userId) {
		return alertRepo.searchNotification(dateFr, dateTo, recRef, subject, exp1, exp2, userId);
	}

	public Alert searchNotificationDetail(int alertId, String userId) {
		return alertRepo.searchNotificationDetail(alertId, userId);
	}

	public int getCount(String userId) {
		return alertRepo.getCount(userId);
	}

	public void deleteNotification(int alertId, String userId) {
		alertRepo.deleteNotification(alertId, userId);
	}

	public void updateReadRecipient(int alertId, String userId) {
		alertRepo.updateReadRecipient(alertId, userId);
	}

	public void addAlert(String subject, String alertDesc, String fromUserId, String recordRef, int recordTypeId,
			Date dateTime) {
		alertRepo.addAlert(subject, alertDesc, fromUserId, recordRef, recordTypeId, dateTime);
	}

	public void addAlertRecipient(int alertId, String userId) {
		int existCount = alertRepo.isAlertRecipExist(userId, alertId);
		if (existCount != 0)
			return;
		alertRepo.addAlertRecipient(alertId, userId);
	}

	public void addAlert(String subject, String alertDesc, String fromUserId, String recordRef, int recordTypeId,
			String userId) {

		Alertx alertx = new Alertx();
		alertx.setAlertDesc(alertDesc);
		alertx.setCreatedDate(new Date());
		alertx.setFrUserId(fromUserId);
		alertx.setRecordRef(recordRef);
		alertx.setRecordTypeId(recordTypeId);
		alertx.setSubject(subject);

		alertxRepo.saveAndFlush(alertx);

		if (!userId.isEmpty() && alertx.getAlertId() != 0) {
			AlertxRecipientPk arPk = new AlertxRecipientPk();
			arPk.setAlertId(alertx.getAlertId());
			arPk.setUserId(userId);

			AlertxRecipient recipient = new AlertxRecipient();
			recipient.setAlertxRecipientPk(arPk);
			recipient.setReadRecipient(1);

			alertxRecRepo.saveAndFlush(recipient);
		}
	}

	public int saveAlert(String subject, String alertDesc, String fromUserId, String recordRef, int recordTypeId,
			Date dateTime) {

		Alertx alertx = new Alertx();
		alertx.setAlertDesc(alertDesc);
		alertx.setCreatedDate(dateTime);
		alertx.setFrUserId(fromUserId);
		alertx.setRecordRef(recordRef);
		alertx.setRecordTypeId(recordTypeId);
		alertx.setSubject(subject);

		alertxRepo.saveAndFlush(alertx);

		return alertx.getAlertId();
	}
	
	public int isAlertRecipExist(String userId, int alertId) {
		return alertRepo.isAlertRecipExist(userId, alertId);
	}

}