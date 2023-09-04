package com.fms.pfc.common;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.StringTokenizer;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import com.fms.pfc.domain.dto.ChangeHistoryDto;
import com.fms.pfc.domain.model.AlertMessage;
import com.fms.pfc.domain.model.Task;
import com.fms.pfc.domain.model.TrxHistoryLog;
import com.fms.pfc.domain.model.Usr;
import com.fms.pfc.service.api.base.AlertMessageService;
import com.fms.pfc.service.api.base.AlertService;
import com.fms.pfc.service.api.base.ChangeHisService;
import com.fms.pfc.service.api.base.LoginService;
import com.fms.pfc.service.api.base.TaskService;
import com.fms.pfc.service.api.base.TrxHisService;

@Service
public class TaskCreation {
	
	private final Logger logger = LoggerFactory.getLogger(TaskCreation.class);

	private LoginService loginServ;
//	private RawMaterialService rmServ;
	//private ProductRecipeService prdRcpServ;
	private AlertService alertServ;
	private AlertMessageService alertMsgServ;
	private TaskService taskServ;
	private JavaMailSender javaMailSender;
	private ChangeHisService chgHisServ;
	private TrxHisService trxServ;

	private String pooledActorId = "";
	private String alertType = "";
	private int pooledActorType = 0;
	private int taskAction = 0;

//	@Autowired
//	public TaskCreation(LoginService loginServ, RawMaterialService rmServ, ProductRecipeService prdRcpServ,
//			AlertService alertServ, AlertMessageService alertMsgServ, TaskService taskServ,
//			JavaMailSender javaMailSender, ChangeHisService chgHisServ, TrxHisService trxServ) {
//		super();
//		this.loginServ = loginServ;
//		this.rmServ = rmServ;
//		this.prdRcpServ = prdRcpServ;
//		this.alertServ = alertServ;
//		this.alertMsgServ = alertMsgServ;
//		this.taskServ = taskServ;
//		this.javaMailSender = javaMailSender;
//		this.chgHisServ = chgHisServ;
//		this.trxServ = trxServ;
//	}

	/*
	 * Task Creation for Raw Material
	 */
	public void taskCreationMatl(int rawMatlId, String rawMatlName, String currUser, int nextStatus, String taskRemarks,
			String screenMode, Date modifyDate) {

		if (screenMode.equals(CommonConstants.SCREEN_MODE_ADD)) {
			taskServ.addProcess(String.valueOf(rawMatlId), CommonConstants.RECORD_TYPE_ID_RAW_MATL,
					CommonConstants.PROCESS_TYPE_RAW_MATL, CommonConstants.ORG_ID_AMBKL, currUser);
		}

		getMatlStatus(rawMatlId, nextStatus);
		AlertMessage alertMsg = alertMsgServ.searchAlertById(alertType);

		Date date = new Date();
		DateFormat formatter = new SimpleDateFormat("dd/MM/yyyy hh:mm:ss a");
		String modDateStr = formatter.format(modifyDate == null ? new Date() : modifyDate);

		String subject = alertMsg.getSubject().replace("[@Name]", rawMatlName).replace("[@Reason]", taskRemarks);
		String subDesc = alertMsg.getDescription().replace("[@Name]", rawMatlName)
				.replace("[@SenderName]", loginServ.searchUser(currUser).getUserName())
				.replace("[@CreatorDate]", formatter.format(date))
				.replace("[@ModifiedDate]", modDateStr)
				.replace("[@Reason]", taskRemarks);

		doRMTask(rawMatlId, currUser, nextStatus, taskRemarks, subject);			
		
		doRMTaskAlert(rawMatlId, currUser, date, subject, subDesc, modDateStr);
	}

	private void doRMTask(int rawMatlId, String currUser, int nextStatus, String taskRemarks, String subject) {
		if (nextStatus == RecordStatusEnum.PEND_AUTH.intValue()
				|| nextStatus == RecordStatusEnum.REWORK_PEND_AUTH.intValue()
				|| nextStatus == RecordStatusEnum.CHG_PEND_AUTH.intValue()
				|| nextStatus == RecordStatusEnum.PEND_DEACTIVATE.intValue()
				|| nextStatus == RecordStatusEnum.PEND_ACTIVATE.intValue()) {

			// TODO: For rework case, when creator submit, end creator task first, then
			// proceed to assign to checker
			if (nextStatus == RecordStatusEnum.REWORK_PEND_AUTH.intValue()) {
				taskServ.updEndTask(String.valueOf(rawMatlId), currUser, taskAction, taskRemarks,
						CommonConstants.RECORD_TYPE_ID_RAW_MATL);
			}

			taskServ.addTask(String.valueOf(rawMatlId), CommonConstants.RECORD_TYPE_ID_RAW_MATL,
					CommonConstants.PROCESS_TYPE_RAW_MATL, CommonConstants.ORG_ID_AMBKL, pooledActorId, pooledActorType,
					subject, currUser, taskRemarks);

		} else if (nextStatus == RecordStatusEnum.SUBMITTED.intValue()
				|| nextStatus == RecordStatusEnum.REWORK.intValue()
				|| nextStatus == RecordStatusEnum.REJECTED.intValue()
				|| nextStatus == RecordStatusEnum.DEACTIVATED.intValue()) {
			taskServ.updEndTask(String.valueOf(rawMatlId), currUser, taskAction, taskRemarks,
					CommonConstants.RECORD_TYPE_ID_RAW_MATL);

			// TODO: For rework, assign back to creator
			if (nextStatus == RecordStatusEnum.REWORK.intValue()) {
				taskServ.addTask(String.valueOf(rawMatlId), CommonConstants.RECORD_TYPE_ID_RAW_MATL,
						CommonConstants.PROCESS_TYPE_RAW_MATL, CommonConstants.ORG_ID_AMBKL, pooledActorId,
						pooledActorType, subject, currUser, taskRemarks);
			}
		}
	}

	private void doRMTaskAlert(int rawMatlId, String currUser, Date date, String subject, String subDesc, String modDateStr) {
		
		String chgLogBody = getChangLogEmailBody(rawMatlId, modDateStr, CommonConstants.RECORD_TYPE_ID_RAW_MATL);		
		if (!Objects.isNull(subDesc) && subDesc.contains("[@ActivityLog]")) {
			//subDesc = setChangeLogEmailBody(rawMatlId, subDesc, modDateStr, CommonConstants.RECORD_TYPE_ID_RAW_MATL);
			subDesc = subDesc.replace("[@ActivityLog]", chgLogBody);
		}
		
		int alertId = saveAlert(rawMatlId, loginServ.searchUser(currUser).getEmail(), date, subject, subDesc,
				CommonConstants.RECORD_TYPE_ID_RAW_MATL, modDateStr);

		if (alertType.equals(AlertDefEnum.RM_AUTH.strValue())//
				|| alertType.equals(AlertDefEnum.RM_RESUBMIT_AUTH.strValue())
				|| alertType.equals(AlertDefEnum.RM_REWORK_AUTH.strValue())//
				|| alertType.equals(AlertDefEnum.RM_DEACTIVATE_AUTH.strValue())
				|| alertType.equals(AlertDefEnum.RM_ACTIVATE_AUTH.strValue())) {
			List<Usr> usrList = loginServ.searchGrpUser(1);
			for (Usr usr : usrList) {
				
				if (alertType.equals(AlertDefEnum.RM_AUTH.strValue())
						|| alertType.equals(AlertDefEnum.RM_REWORK_AUTH.strValue())
						|| alertType.equals(AlertDefEnum.RM_RESUBMIT_AUTH.strValue())) {
					//subDesc = setChangeLogEmailBody(rawMatlId, subDesc, modDateStr, CommonConstants.RECORD_TYPE_ID_RAW_MATL);
				}
				
				sendNotification(usr, subject, subDesc, date, alertId);
			}
		} else {
			String notifyUser = "";
			if (alertType.equals(AlertDefEnum.RM_REWORK.strValue())//
					|| alertType.equals(AlertDefEnum.RM_APPROVED.strValue())//
					|| alertType.equals(AlertDefEnum.RM_REJECTED.strValue())
					|| alertType.equals(AlertDefEnum.RM_DEACTIVATED.strValue())) {
				notifyUser = pooledActorId;
				
				if (alertType.equals(AlertDefEnum.RM_REWORK.strValue())
						|| alertType.equals(AlertDefEnum.RM_APPROVED.strValue())) {
					//subDesc = setChangeLogEmailBody(rawMatlId, subDesc, modDateStr, CommonConstants.RECORD_TYPE_ID_RAW_MATL);
				}
				
			} else {
			//	notifyUser = rmServ.searchRawMaterial("", "", rawMatlId, 0).get(0).getCreatorId();

				if (notifyUser.equals(CommonConstants.USER_ID_SYSTEM)) {
					notifyUser = taskServ
							.searchLatestTask(String.valueOf(rawMatlId), CommonConstants.RECORD_TYPE_ID_RAW_MATL)
							.getCreatorId();
				}
			}

			Usr usr = loginServ.searchUser(notifyUser);
			sendNotification(usr, subject, subDesc, date, alertId);
		}
	}

	protected int saveAlert(int refId, String email, Date date, String subject, String subDesc, int recType,
			String modDateStr) {

		int alertId = alertServ.saveAlert(subject, subDesc, email, String.valueOf(refId), recType, date);
		return alertId;
	}
	
	private String getChangLogEmailBody(int refId, String modDateStr, int recType) {
		RetrieveChangeLog log = new RetrieveChangeLog(modDateStr, recType, String.valueOf(refId));
		String emailBody = log.genEmailBody();
		logger.debug("getChangLogEmailBody() body={}",emailBody);
		return emailBody;
	}

	private void sendNotification(Usr usr, String subject, String subDesc, Date date, int alertId) {

		if (usr == null || usr.getDisabledFlag().equals(String.valueOf(CommonConstants.FLAG_YES)))
			return;

		if (usr.getAlertPre() == CommonConstants.MESSAGE_PREF_EMAIL) {
			sendEmail(usr, subject, subDesc);
		} else if (usr.getAlertPre() == CommonConstants.MESSAGE_PREF_NOTIFICATION) {
			alertServ.addAlertRecipient(alertId, usr.getUserId());
		} else if (usr.getAlertPre() == CommonConstants.MESSAGE_PREF_EMAIL_NOTIFICATION) {
			alertServ.addAlertRecipient(alertId, usr.getUserId());
			sendEmail(usr, subject, subDesc);
		}
	}

	private void sendEmail(Usr usr, String subject, String content) {

		if (content.contains("Old Value") || content.contains("New Value")) {
			sendHtml(usr, subject, content);
		} else {

			SimpleMailMessage msg = new SimpleMailMessage();
			msg.setTo(usr.getEmail());
			msg.setSubject(subject);
			msg.setText(content.replace("[@ReceiverName]", usr.getUserName()));

			javaMailSender.send(msg);
		}

	}

	private void sendHtml(Usr usr, String subject, String content) {
		MimeMessage message = javaMailSender.createMimeMessage();
		try {
			MimeMessageHelper helper = new MimeMessageHelper(message, false, "utf-8");
			message.setContent(content.replace("[@ReceiverName]", usr.getUserName()), "text/html");
			helper.setTo(usr.getEmail());
			helper.setSubject(subject);
			javaMailSender.send(message);
		} catch (MessagingException e) {
			logger.error(e.toString());
		}
	}

	private void getMatlStatus(int rawMatlId, int nextStatus) {

		if (nextStatus == RecordStatusEnum.PEND_AUTH.intValue()) {
			alertType = AlertDefEnum.RM_AUTH.strValue();
			pooledActorId = CommonConstants.ROLE_ID_CHECKER;
			pooledActorType = CommonConstants.ACTOR_TYPE_GROUP;
			taskAction = CommonConstants.TASK_ACTION_SUBMIT;
		} else if (nextStatus == RecordStatusEnum.SUBMITTED.intValue()) {
			findPooledActor(rawMatlId, nextStatus, CommonConstants.RECORD_TYPE_ID_RAW_MATL);
		} else if (nextStatus == RecordStatusEnum.REJECTED.intValue()) {
			findPooledActor(rawMatlId, nextStatus, CommonConstants.RECORD_TYPE_ID_RAW_MATL);
		} else if (nextStatus == RecordStatusEnum.REWORK.intValue()) {
			findPooledActor(rawMatlId, nextStatus, CommonConstants.RECORD_TYPE_ID_RAW_MATL);
		} else if (nextStatus == RecordStatusEnum.REWORK_PEND_AUTH.intValue()) {
			alertType = AlertDefEnum.RM_REWORK_AUTH.strValue();
			pooledActorId = CommonConstants.ROLE_ID_CHECKER;
			pooledActorType = CommonConstants.ACTOR_TYPE_GROUP;
			taskAction = CommonConstants.TASK_ACTION_REWORK_SUBMIT;
		} else if (nextStatus == RecordStatusEnum.CHG_PEND_AUTH.intValue()) {
			alertType = AlertDefEnum.RM_RESUBMIT_AUTH.strValue();
			pooledActorId = CommonConstants.ROLE_ID_CHECKER;
			pooledActorType = CommonConstants.ACTOR_TYPE_GROUP;
			taskAction = CommonConstants.TASK_ACTION_REWORK_SUBMIT;
		} else if (nextStatus == RecordStatusEnum.PEND_DEACTIVATE.intValue()) {
			alertType = AlertDefEnum.RM_DEACTIVATE_AUTH.strValue();
			pooledActorId = CommonConstants.ROLE_ID_CHECKER;
			pooledActorType = CommonConstants.ACTOR_TYPE_GROUP;
			taskAction = CommonConstants.TASK_ACTION_DEACTIVATE;
		} else if (nextStatus == RecordStatusEnum.DEACTIVATED.intValue()) {
			findPooledActor(rawMatlId, nextStatus, CommonConstants.RECORD_TYPE_ID_RAW_MATL);
		} else if (nextStatus == RecordStatusEnum.PEND_ACTIVATE.intValue()) {
			alertType = AlertDefEnum.RM_ACTIVATE_AUTH.strValue();
			pooledActorId = CommonConstants.ROLE_ID_CHECKER;
			pooledActorType = CommonConstants.ACTOR_TYPE_GROUP;
			taskAction = CommonConstants.TASK_ACTION_ACTIVATE;
		}
	}

	private void findPooledActor(int refId, int nextStatus, int recordTypeId) {

		switch (recordTypeId) {
		case CommonConstants.RECORD_TYPE_ID_RAW_MATL:
			findPooledActorRM(refId, nextStatus, CommonConstants.RECORD_TYPE_ID_RAW_MATL);
			break;

		case CommonConstants.RECORD_TYPE_ID_PROD_RECP:
			findPooledActorPR(refId, nextStatus, CommonConstants.RECORD_TYPE_ID_PROD_RECP);
			break;

		default:
			logger.warn("findPooledActor() Unknown recordTypeId");

		}

	}

	protected void findPooledActorPR(int refId, int nextStatus, int recordTypeId) {
		//Find PR creator, and it should not be system
		String tempActor = "";//prdRcpServ.searchProductRecipe("", "", "", "", refId, 0).get(0).getCreatorId();
		if (StringUtils.equals(tempActor, CommonConstants.USER_ID_SYSTEM)) {
			// Find latest occurrence of Task involving maker assign to checker, take the
			// creator which is maker
			Task task = taskServ.searchLatestTask(String.valueOf(refId), recordTypeId,
					CommonConstants.ROLE_ID_CHECKER, 0, CommonConstants.ROLE_ID_MAKER, "");
			
			if(null != task)
				tempActor = task.getCreatorId();
		}
		
		if (nextStatus == RecordStatusEnum.REWORK.intValue()) {
			tempActor = findCreatorIfSystem(refId, recordTypeId, tempActor); 
			
			alertType = AlertDefEnum.PR_REWORK.strValue();
			pooledActorId = tempActor;
			pooledActorType = CommonConstants.ACTOR_TYPE_USER;
			taskAction = CommonConstants.TASK_ACTION_REWORK;
			
		} else if (nextStatus == RecordStatusEnum.SUBMITTED.intValue()) {
			tempActor = findCreatorIfSystem(refId, recordTypeId, tempActor); 
			
			pooledActorId = tempActor;
			alertType = AlertDefEnum.PR_APPROVED.strValue();
			pooledActorType = CommonConstants.ACTOR_TYPE_USER;
			taskAction = CommonConstants.TASK_ACTION_SUBMIT;
			
		} else if (nextStatus == RecordStatusEnum.REJECTED.intValue()) {
			alertType = AlertDefEnum.PR_REJECTED.strValue();
			pooledActorId = tempActor;
			pooledActorType = CommonConstants.ACTOR_TYPE_USER;
			taskAction = CommonConstants.TASK_ACTION_SUBMIT;
			
		}
	}

	protected void findPooledActorRM(int refId, int nextStatus, int recordTypeId) {
		String tempActor = "";//rmServ.searchRawMaterial("", "", refId, 0).get(0).getCreatorId();
		if (StringUtils.equals(tempActor, CommonConstants.USER_ID_SYSTEM)) {
			// Find latest occurrence of Task involving maker assign to checker, take the
			// creator which is maker
			Task task = taskServ.searchLatestTask(String.valueOf(refId), recordTypeId,
					CommonConstants.ROLE_ID_CHECKER, 0, CommonConstants.ROLE_ID_MAKER, "");
			
			if(null != task)
				tempActor = task.getCreatorId();
		}
		
		if (nextStatus == RecordStatusEnum.REWORK.intValue()) {
			alertType = AlertDefEnum.RM_REWORK.strValue();
			pooledActorId = tempActor;
			pooledActorType = CommonConstants.ACTOR_TYPE_USER;
			taskAction = CommonConstants.TASK_ACTION_REWORK;

		} else if (nextStatus == RecordStatusEnum.SUBMITTED.intValue()) {
			alertType = AlertDefEnum.RM_APPROVED.strValue();
			pooledActorId = tempActor;
			pooledActorType = CommonConstants.ACTOR_TYPE_USER;
			taskAction = CommonConstants.TASK_ACTION_SUBMIT;
			
		} else if (nextStatus == RecordStatusEnum.REJECTED.intValue()) {
			alertType = AlertDefEnum.RM_REJECTED.strValue();
			pooledActorId = tempActor;
			pooledActorType = CommonConstants.ACTOR_TYPE_USER;
			taskAction = CommonConstants.TASK_ACTION_REJECT;
			
		} else if (nextStatus == RecordStatusEnum.DEACTIVATED.intValue()) {
			alertType = AlertDefEnum.RM_DEACTIVATED.strValue();
			pooledActorId = tempActor;
			pooledActorType = CommonConstants.ACTOR_TYPE_USER;
			taskAction = CommonConstants.TASK_ACTION_DEACTIVATE;
			
		}
	}

	protected String findCreatorIfSystem(int refId, int recordTypeId, String tempActor) {
		//Special case - If creator still system, then:
		//Find this PR task having remark 'byMatl'
		// - In the remark, find rmid
		// - Then find that RM task by that rmid, look for latest 
		//    occurrence of Task involving maker assign to checker, 
		//    take the creator which is maker
		if (StringUtils.equals(tempActor, CommonConstants.USER_ID_SYSTEM)) {
			Task prTask = taskServ.searchLatestTask(String.valueOf(refId), recordTypeId,
					CommonConstants.ROLE_ID_CHECKER, 0, CommonConstants.ROLE_ID_CHECKER, "byMatl");
			
			if(null != prTask) {
				String remarks = StringUtils.remove(prTask.getTaskRemarks().trim(), "byMatl:");
				StringTokenizer st = new StringTokenizer(remarks, ":");
				String rmid = "";
				while (st.hasMoreElements()) {
					String elem = st.nextToken();
					if (StringUtils.startsWith(elem, "rm=")) {
						rmid = elem.substring(3);
					}
				}
				
				logger.debug("findCreatorIfSystem() remarks={},rmid={}",remarks,rmid);
				
				Task rmTask = taskServ.searchLatestTask(rmid, CommonConstants.RECORD_TYPE_ID_RAW_MATL,
						CommonConstants.ROLE_ID_CHECKER, 0, CommonConstants.ROLE_ID_MAKER, "");
				
				if(null != rmTask)
					tempActor = rmTask.getCreatorId();						
			}					
		}
		return tempActor;
	}

	/*
	 * Task Creation for Product Recipe
	 */
	public void taskCreationPrd(int prId, String prName, String currUser, int nextStatus, String taskRemarks,
			String screenMode, Date modifyDate) {

		if (screenMode.equals(CommonConstants.SCREEN_MODE_ADD)) {
			taskServ.addProcess(String.valueOf(prId), CommonConstants.RECORD_TYPE_ID_PROD_RECP,
					CommonConstants.PROCESS_TYPE_PROD_RECP, CommonConstants.ORG_ID_AMBKL, currUser);
		}

		getPrStatus(prId, nextStatus);
		AlertMessage alertMsg = alertMsgServ.searchAlertById(alertType);

		Date date = new Date();
		DateFormat formatter = new SimpleDateFormat("dd/MM/yyyy hh:mm:ss a");
		String modDateStr = formatter.format(modifyDate == null ? new Date() : modifyDate);
		
		String subject = alertMsg.getSubject().replace("[@Name]", prName).replace("[@Reason]", taskRemarks);
		String subDesc = alertMsg.getDescription().replace("[@Name]", prName)
				.replace("[@SenderName]", loginServ.searchUser(currUser).getUserName())
				.replace("[@CreatorDate]", formatter.format(date))
				.replace("[@ModifiedDate]", modDateStr)
				.replace("[@Reason]", taskRemarks);

		doPRTask(prId, currUser, nextStatus, taskRemarks, screenMode, subject);

		doPRTaskAlert(prId, prName, currUser, taskRemarks, date, formatter, subject, subDesc, modDateStr);
	}

	private void doPRTaskAlert(int prId, String prName, String currUser, String taskRemarks, Date date,
			DateFormat formatter, String subject, String subDesc, String modDateStr) {
		//int alertId = alertServ.saveAlert(subject, subDesc, loginServ.searchUser(currUser).getEmail(), String.valueOf(prId),
			//	CommonConstants.RECORD_TYPE_ID_PROD_RECP, date);
		
		String chgLogBody = getChangLogEmailBody(prId, modDateStr, CommonConstants.RECORD_TYPE_ID_PROD_RECP);
		if (!Objects.isNull(subDesc) && subDesc.contains("[@ActivityLog]")) {
			//subDesc = setChangeLogEmailBody(prId, subDesc, modDateStr, CommonConstants.RECORD_TYPE_ID_PROD_RECP);
			subDesc = subDesc.replace("[@ActivityLog]", chgLogBody);			
		}
		
		int alertId = saveAlert(prId, loginServ.searchUser(currUser).getEmail(), date, subject, subDesc,
				CommonConstants.RECORD_TYPE_ID_PROD_RECP, modDateStr);

		if (alertType.equals(AlertDefEnum.PR_AUTH.strValue())//
				|| alertType.equals(AlertDefEnum.PR_REWORK_AUTH.strValue())//
				|| alertType.equals(AlertDefEnum.PR_CHG_BYMTL.strValue())) {
			
			if (alertType.equals(AlertDefEnum.PR_AUTH.strValue())
					|| alertType.equals(AlertDefEnum.PR_REWORK_AUTH.strValue())){
				//subDesc = setChangeLogEmailBody(prId, subDesc, modDateStr, CommonConstants.RECORD_TYPE_ID_PROD_RECP);
			}
			
			List<Usr> usrList = loginServ.searchGrpUser(1);
			for (Usr usr : usrList) {
				sendNotification(usr, subject, subDesc, date, alertId);
			}
		} else {
			Task searchLatestTask = taskServ.searchLatestTask(String.valueOf(prId),CommonConstants.RECORD_TYPE_ID_PROD_RECP);			
			//ProductRecipe productRecipe = prdRcpServ.searchProductRecipe("", "", "", "", prId, 0).get(0);
			
			if (alertType.equals(AlertDefEnum.PR_CONFIRMED.strValue())) {//
				
				//subDesc = setChangeLogEmailBody(prId, subDesc, modDateStr, CommonConstants.RECORD_TYPE_ID_PROD_RECP);
				
				List<Usr> usrList = loginServ.searchGrpUser(2);
				for (Usr usr : usrList) {
					sendNotification(usr, subject, subDesc, date, alertId);
				}

				// Send notification to creator about verification status
				String creatorId = "";//productRecipe.getCreatorId();
				if (creatorId.equals(CommonConstants.USER_ID_SYSTEM)) {
					if (null == searchLatestTask)
						return;
					creatorId = searchLatestTask.getCreatorId();
				}

				Usr creator = loginServ.searchUser(creatorId);
				// Verification message ID = PR_AUTH_PROG
				AlertMessage verifyMsg = alertMsgServ.searchAlertById(AlertDefEnum.PR_AUTH_PROG.strValue());//
				String verifySub = verifyMsg.getSubject().replace("[@Name]", prName);
				String verifySubDesc = verifyMsg.getDescription().replace("[@Name]", prName)
						.replace("[@SenderName]", loginServ.searchUser(currUser).getUserName())
						.replace("[@CreatorDate]", formatter.format(date)).replace("[@Reason]", taskRemarks);

				//int alertIdCreator = alertServ.saveAlert(verifySub, verifySubDesc, creator.getEmail(), String.valueOf(prId),
					//	CommonConstants.RECORD_TYPE_ID_PROD_RECP, date);
				
				if (!Objects.isNull(verifySubDesc) && verifySubDesc.contains("[@ActivityLog]")) {
					//verifySubDesc = setChangeLogEmailBody(prId, verifySubDesc, modDateStr, CommonConstants.RECORD_TYPE_ID_PROD_RECP);
					verifySubDesc = verifySubDesc.replace("[@ActivityLog]", chgLogBody);
				}
				
				int alertIdCreator = saveAlert(prId, creator.getEmail(), date, verifySub, verifySubDesc,
						CommonConstants.RECORD_TYPE_ID_PROD_RECP, modDateStr);

				sendNotification(creator, verifySub, verifySubDesc, date, alertIdCreator);

			} else {
				String notifyUser = "";
				if (alertType.equals(AlertDefEnum.PR_REWORK.strValue())//
						|| alertType.equals(AlertDefEnum.PR_APPROVED.strValue())//
						|| alertType.equals(AlertDefEnum.PR_REJECTED.strValue())) {
					notifyUser = pooledActorId;
					
					if (alertType.equals(AlertDefEnum.PR_REWORK.strValue())//
							|| alertType.equals(AlertDefEnum.PR_APPROVED.strValue())) {
						//subDesc = setChangeLogEmailBody(prId, subDesc, modDateStr, CommonConstants.RECORD_TYPE_ID_PROD_RECP);
					}
				} else {
					notifyUser = "";//productRecipe.getCreatorId();
					if (notifyUser.equals(CommonConstants.USER_ID_SYSTEM)) {
						if (null == searchLatestTask)
							return;
						notifyUser = searchLatestTask.getCreatorId();
					}
				}

				Usr usr = loginServ.searchUser(notifyUser);
				sendNotification(usr, subject, subDesc, date, alertId);
			}
		}
	}

	private void doPRTask(int prId, String currUser, int nextStatus, String taskRemarks, String screenMode,
			String subject) {
		if (nextStatus == RecordStatusEnum.PEND_AUTH.intValue()
				|| nextStatus == RecordStatusEnum.REWORK_PEND_AUTH.intValue()
				|| nextStatus == RecordStatusEnum.CHG_PEND_AUTH.intValue()) {
			
			// TODO: For rework case, when creator submit, end creator task first, then
			// proceed to assign to checker
			if (nextStatus == RecordStatusEnum.REWORK_PEND_AUTH.intValue()) {
				taskServ.updEndTask(String.valueOf(prId), currUser, taskAction, taskRemarks,
						CommonConstants.RECORD_TYPE_ID_PROD_RECP);
			}
			
			if (nextStatus == RecordStatusEnum.CHG_PEND_AUTH.intValue()) {
				taskRemarks = screenMode;
			}
			
			taskServ.addTask(String.valueOf(prId), CommonConstants.RECORD_TYPE_ID_PROD_RECP, CommonConstants.PROCESS_TYPE_PROD_RECP,
					CommonConstants.ORG_ID_AMBKL, pooledActorId, pooledActorType, subject, currUser, taskRemarks);
			
		} else if (nextStatus == RecordStatusEnum.SUBMITTED.intValue()
				|| nextStatus == RecordStatusEnum.REWORK.intValue()
				|| nextStatus == RecordStatusEnum.REJECTED.intValue()
				|| nextStatus == RecordStatusEnum.PEND_CONFIRM.intValue()
				|| nextStatus == RecordStatusEnum.CHG_PEND_CONFIRM.intValue()) {
			if (!StringUtils.contains(screenMode,"byMatl")) {
				taskServ.updEndTask(String.valueOf(prId), currUser, taskAction, taskRemarks, CommonConstants.RECORD_TYPE_ID_PROD_RECP);
			}

			if (nextStatus == RecordStatusEnum.PEND_CONFIRM.intValue()
					|| nextStatus == RecordStatusEnum.CHG_PEND_CONFIRM.intValue()
					|| nextStatus == RecordStatusEnum.REWORK.intValue()) { // TODO: For rework, assign back to creator
				taskServ.addTask(String.valueOf(prId), CommonConstants.RECORD_TYPE_ID_PROD_RECP,
						CommonConstants.PROCESS_TYPE_PROD_RECP, CommonConstants.ORG_ID_AMBKL, pooledActorId,
						pooledActorType, subject, currUser, taskRemarks);
			}
		}
	}

	private void getPrStatus(int prId, int nextStatus) {

		if (nextStatus == RecordStatusEnum.PEND_AUTH.intValue()) {
			alertType = AlertDefEnum.PR_AUTH.strValue();
			pooledActorId = CommonConstants.ROLE_ID_CHECKER;
			pooledActorType = CommonConstants.ACTOR_TYPE_GROUP;
			taskAction = CommonConstants.TASK_ACTION_SUBMIT;
		} else if (nextStatus == RecordStatusEnum.CHG_PEND_AUTH.intValue()) {
			alertType = AlertDefEnum.PR_CHG_BYMTL.strValue();
			pooledActorId = CommonConstants.ROLE_ID_CHECKER;
			pooledActorType = CommonConstants.ACTOR_TYPE_GROUP;
			taskAction = CommonConstants.TASK_ACTION_REWORK_SUBMIT;
		} else if (nextStatus == RecordStatusEnum.SUBMITTED.intValue()) {
			findPooledActor(prId, nextStatus, CommonConstants.RECORD_TYPE_ID_PROD_RECP);
		} else if (nextStatus == RecordStatusEnum.REJECTED.intValue()) {
			findPooledActor(prId, nextStatus, CommonConstants.RECORD_TYPE_ID_PROD_RECP);
		} else if (nextStatus == RecordStatusEnum.REWORK.intValue()) {
			findPooledActor(prId, nextStatus, CommonConstants.RECORD_TYPE_ID_PROD_RECP);
		} else if (nextStatus == RecordStatusEnum.REWORK_PEND_AUTH.intValue()) {
			alertType = AlertDefEnum.PR_REWORK_AUTH.strValue();
			pooledActorId = CommonConstants.ROLE_ID_CHECKER;
			pooledActorType = CommonConstants.ACTOR_TYPE_GROUP;
			taskAction = CommonConstants.TASK_ACTION_REWORK_SUBMIT;
		} else if (nextStatus == RecordStatusEnum.PEND_CONFIRM.intValue()
				|| nextStatus == RecordStatusEnum.CHG_PEND_CONFIRM.intValue()) {
			alertType = AlertDefEnum.PR_CONFIRMED.strValue();
			pooledActorId = CommonConstants.ROLE_ID_HOD;
			pooledActorType = CommonConstants.ACTOR_TYPE_GROUP;
			taskAction = CommonConstants.TASK_ACTION_SUBMIT;
		}
	}
	
	class RetrieveChangeLog {

		List<ChangeHistoryDto> chgs = new ArrayList<ChangeHistoryDto>();
		TrxHistoryLog trx = new TrxHistoryLog();

		public RetrieveChangeLog(String chgHisDate, int recTypeId, String recRef) {
			chgHisDate = convertProperFormat(chgHisDate);
			chgs = chgHisServ.findByCriteria(chgHisDate, recTypeId, recRef);
			List<TrxHistoryLog> trxList = trxServ.findByCriteria("", chgHisDate, recRef, "", recTypeId,
					Arrays.asList(CommonConstants.FUNCTION_TYPE_INSERT, CommonConstants.FUNCTION_TYPE_UPDATE,
							CommonConstants.FUNCTION_TYPE_DELETE));
			if (Objects.nonNull(trxList) && !trxList.isEmpty()) {
				trx = trxList.get(0);
			}
			logger.debug("RetrieveChangeLog() chgHisDate={}, recTypeId={}, recRef={}, trx.size={}", chgHisDate,
					recTypeId, recRef, trxList.size());
		}

		private String convertProperFormat(String chgHisDate) {

			try {
				String pattern = "dd/MM/yyyy hh:mm:ss a";
				SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
				Date dt = simpleDateFormat.parse(chgHisDate);

				pattern = "yyyy-MM-dd HH:mm:ss";
				simpleDateFormat = new SimpleDateFormat(pattern);
				String date = simpleDateFormat.format(dt);

				return date;

			} catch (Exception ex) {
				logger.warn("convertProperFormat() err={}", ex.getMessage());
			}

			return "";
		}

		private String genEmailBody() {
			StringBuffer stringBuffer = generateCommonHtmlHead();
			int rowno = 1;
			for (ChangeHistoryDto chg : chgs) {
				generateTdData(stringBuffer, rowno, chg);
				rowno++;
			}

			generateCommonFooter(stringBuffer);
			return stringBuffer.toString();
		}

		private StringBuffer generateCommonHtmlHead() {
			StringBuffer stringBuffer = new StringBuffer();

			return stringBuffer.append("<head>").append("<p> Traceability remark: ").append(trx.getSearchStr()).append("</p>")
					.append("</head>").append("<body>").append("<table border=1>").append("<tr>")
					.append("<th>No.</th><th>Reference</th><th>Field</th><th>Old Value</th><th>New Value</th>")
					.append("</tr>");
		}

		private void generateCommonFooter(StringBuffer stringBuffer) {
			stringBuffer.append("</table></body>");
		}

		private void generateTdData(StringBuffer stringBuffer, int rowno, ChangeHistoryDto chg) {
			stringBuffer.append("<tr>");
			stringBuffer.append("<td align ='right'>").append(rowno).append("</td>");
			stringBuffer.append("<td>").append(chg.getTableName()).append("</td>");
			stringBuffer.append("<td>").append(chg.getFieldName()).append("</td>");
			stringBuffer.append("<td>").append(chg.getOldValue()).append("</td>");
			stringBuffer.append("<td>").append(chg.getNewValue()).append("</td>");
			stringBuffer.append("</tr>");
		}
	}

}
