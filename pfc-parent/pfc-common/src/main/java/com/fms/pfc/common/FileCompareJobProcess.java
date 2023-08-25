package com.fms.pfc.common;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

import com.fms.pfc.domain.model.AlertMessage;
import com.fms.pfc.domain.model.UsrProfile;
import com.fms.pfc.domain.model.main.OnDemandSearch;
import com.fms.pfc.service.api.base.AlertMessageService;
import com.fms.pfc.service.api.base.UserProfileService;
import com.fms.pfc.service.api.main.G2LotViewService;
import com.fms.pfc.service.api.main.OnDemandSearchService;

@Service
public class FileCompareJobProcess {
	private final Logger logger = LoggerFactory.getLogger(FileCompareJobProcess.class);

	private Environment env;
	private AlertMessageService alertMsgServ;
	private OnDemandSearchService odsServ;
	private G2LotViewService g2Serv;
	private SendAlertAndEmailService sendServ;
	private UserProfileService usrProfServ;

	private static final int EMAIL_LIST_LIMIT = 50;
	private static final String FROM_EMAIL = "PDFS - System";

	@Autowired
	public FileCompareJobProcess(Environment env, AlertMessageService alertMsgServ, OnDemandSearchService odsServ,
			G2LotViewService g2Serv, SendAlertAndEmailService sendServ, UserProfileService usrProfServ) {
		this.env = env;
		this.alertMsgServ = alertMsgServ;
		this.odsServ = odsServ;
		this.g2Serv = g2Serv;
		this.sendServ = sendServ;
		this.usrProfServ = usrProfServ;
	}

	public void process() {
		LocalDate todayDate = LocalDate.now();
		String toDate = todayDate.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
		
		LocalDate backDate = todayDate.minusDays(2);
		String fromDate = backDate.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
		
		LocalDateTime curTime = LocalDateTime.now();
		String currTimeStr = curTime.format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss"));

		long daysBetween = ChronoUnit.DAYS.between(backDate, todayDate);

		AlertMessage alertMsg = alertMsgServ.searchAlertById(AlertDefEnum.FILE_COMPARE.strValue());

		List<String> hplList = g2Serv.hplList();

		// loop each hpl
		for (String hpl : hplList) {
			// check from ODS tables - g2 completion lot vs prod file table
			List<OnDemandSearch> search = odsServ.searchByCriteria(hpl, "", "", "", "", "", "", fromDate, toDate);
			// filter by alert other than OK
			search = search.stream().filter(arg0 -> !StringUtils.equalsIgnoreCase(arg0.getAlert(), "ok"))
					.collect(Collectors.toList());
			if (!search.isEmpty()) {
				// find user list based on group = hpl
				List<UsrProfile> userProfiles = usrProfServ.findUsersByGroup(hpl).stream()
						.filter(usr -> usr.getDisabledFlag().equals(String.valueOf(CommonConstants.FLAG_NO)))
						.collect(Collectors.toList());

				if (!search.isEmpty()) {
					String tblStr = sendServ.genEmailBody(search);
					for (UsrProfile usr : userProfiles) {
						String subject = alertMsg.getSubject().replace("[@Hpl]", hpl) + " (Auto-job)";
						String content = alertMsg.getDescription().replace("[@Name]", usr.getUserName())
								.replace("[@Date]", currTimeStr).replace("[@Table]", tblStr);

						sendServ.sendAlertAndEmail(FROM_EMAIL, hpl, usr, subject, content, true);
					}
				}
			}
		}
	}

}
