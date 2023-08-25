package com.fms.pfc.controller.base;

import static com.cronutils.model.field.expression.FieldExpressionFactory.always;
import static com.cronutils.model.field.expression.FieldExpressionFactory.between;
import static com.cronutils.model.field.expression.FieldExpressionFactory.every;
import static com.cronutils.model.field.expression.FieldExpressionFactory.on;
import static com.cronutils.model.field.expression.FieldExpressionFactory.questionMark;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.StringTokenizer;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.quartz.CronScheduleBuilder;
import org.quartz.Trigger;
import org.quartz.TriggerBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.context.annotation.SessionScope;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.view.RedirectView;

import com.cronutils.builder.CronBuilder;
import com.cronutils.model.Cron;
import com.cronutils.model.CronType;
import com.cronutils.model.definition.CronDefinitionBuilder;
import com.fms.pfc.common.Authority;
import com.fms.pfc.common.CommonConstants;
import com.fms.pfc.domain.model.JobScheduler;
import com.fms.pfc.domain.model.SchedulerActivityLog;
import com.fms.pfc.service.api.base.JobSchedulerService;
import com.fms.pfc.service.api.base.MenuRoleFunctionService;
import com.fms.pfc.service.api.base.SchedulerActivityLogService;
import com.fms.pfc.service.api.base.SchedulerService;
import com.fms.pfc.service.api.base.TrxHisService;
import com.fms.pfc.validation.common.CommonValidation;

@Controller
@SessionScope
public class JobSchedulerController {
	
	private final Logger logger = LoggerFactory.getLogger(JobSchedulerController.class);	
	
	private JobSchedulerService jobSchedServ;		
	private CommonValidation commonValServ;		
	private SchedulerService schedulerServ;		
	private Authority auth;	
	private TrxHisService trxHistServ;	
	private SchedulerActivityLogService schedActServ;
	private MessageSource msgSource;
	private MenuRoleFunctionService mrfServ;
	
	private static final SimpleDateFormat dateFormatterOld = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
	private static final int MENU_ITEM_ID = 803;
	
	private Map<String, Object> model = new HashMap<String, Object>();

	@Autowired
	public JobSchedulerController(JobSchedulerService jobSchedServ, CommonValidation commonValServ,
			SchedulerService schedulerServ, Authority auth, TrxHisService trxHistServ,
			SchedulerActivityLogService schedActServ, MessageSource msgSource, MenuRoleFunctionService mrfServ) {
		super();
		this.jobSchedServ = jobSchedServ;
		this.commonValServ = commonValServ;
		this.schedulerServ = schedulerServ;
		this.auth = auth;
		this.trxHistServ = trxHistServ;
		this.schedActServ = schedActServ;
		this.msgSource = msgSource;
		this.mrfServ = mrfServ;
	}

	@GetMapping("/base/maint/schedulerList")
	public ModelAndView getAllTaskList(HttpServletRequest request, HttpServletResponse response) {
		
		//Map<String, Object> model = new HashMap<String, Object>();
		
		model = auth.onPageLoad(model, request);
		auth.isAuthUrl(request, response);
		mrfServ.menuRoleFunction(model, MENU_ITEM_ID, (String) model.get("loggedUser"));
		jobSchedServ.getAllJobs();
		model.put("jobLists", schedulerServ.searchJobScheduler("", "", "", ""));
		model.put("jobGroupInfo", schedulerServ.searchAllJobGroup());
		model.put("jobStatusInfo", schedulerServ.searchAllJobStatus());
				
		return new ModelAndView("/base/maint/schedulerList", model);
	}
	
	@PostMapping("/base/maint/jobSchedulerSrc")
	public ModelAndView searchJobScheduler(HttpServletRequest request, 
			@RequestParam(name = "jobGroup") String jobGroup,
			@RequestParam(name = "jobName") String jobName, @RequestParam(name = "jobStatus") String jobStatus,
			@RequestParam(name = "exp1") String exp1, HttpSession session) {
		
		//Map<String, Object> model = (Map<String, Object>) session.getAttribute("sessionModel");
		
		String errorMsg = "";
		model.remove("error");
		model.remove("success");
		errorMsg += commonValServ.validateInputLength(jobGroup, 50, "Job group");
		errorMsg += commonValServ.validateInputLength(jobName, 200, "Job name");
		errorMsg += commonValServ.validateInputLength(jobStatus, 10, "Job status");
		
		model.put("jobGroup", jobGroup);
		model.put("jobName", jobName);
		model.put("jobStatus", jobStatus);
		
		if (errorMsg.length() != 0) {
			
			errorMsg += msgSource.getMessage("msgFailSearchRecord", new Object[] {}, Locale.getDefault());
			model.put("error", errorMsg);
			jobSchedServ.getAllJobs();
			model.put("jobLists", schedulerServ.searchJobScheduler("", "", "", ""));
			model.put("jobGroupInfo", schedulerServ.searchAllJobGroup());
			model.put("jobStatusInfo", schedulerServ.searchAllJobStatus());			
			
			return new ModelAndView("/base/maint/schedulerList", model);
		}
		
		jobSchedServ.getAllJobs();
		if (jobStatus.equals("All") && jobGroup.equals("All")) {
			model.put("jobLists", schedulerServ.searchJobScheduler("", jobName.trim(), "", exp1));
		} else if (jobStatus.equals("All")) {
			model.put("jobLists", schedulerServ.searchJobScheduler(jobGroup, jobName.trim(), "", exp1));
		} else if (jobGroup.equals("All")) {
			model.put("jobLists", schedulerServ.searchJobScheduler("", jobName.trim(), jobStatus, exp1));
		} else {
			model.put("jobLists", schedulerServ.searchJobScheduler(jobGroup, jobName.trim(), jobStatus, exp1));
		}
		model.put("jobGroupInfo", schedulerServ.searchAllJobGroup());
		model.put("jobStatusInfo", schedulerServ.searchAllJobStatus());
		
		//FSGS) Kent 26/2/2021 Add/changed - Add Function executed log START
		StringBuffer sb = new StringBuffer();
		sb.append("Search Criteria: ");
		sb.append("Job Type=").append(jobGroup.isEmpty()?"<ALL>":jobGroup).append(", ");
		sb.append("Job Name=").append(jobName.isEmpty()?"<ALL>":jobName).append(", ");
		sb.append("Job Status=").append(jobStatus.isEmpty()?"<ALL>":jobStatus);	
	
		trxHistServ.addTrxHistory(new Date(), "Search Scheduler", request.getRemoteUser(),
				CommonConstants.FUNCTION_TYPE_SEARCH, "Search Scheduler", CommonConstants.RECORD_TYPE_ID_JOB_SCHEDULER, 
				sb.toString());
		//FSGS) Kent 26/2/2021 Add/changed - Add Function executed log END
		
		return new ModelAndView("/base/maint/schedulerList", model);
	}
	
	@GetMapping("/base/maint/addScheduler")
	public ModelAndView addSchedulerPanel(HttpServletRequest request) {
		
		/*
		 * Map<String, Object> model = new HashMap<String, Object>();
		 * 
		 * model = auth.onPageLoad(model, request);
		 */
		
		//request.getSession().setAttribute("sessionModel", model);
		
		return new ModelAndView("/base/maint/addScheduler", model);
	}
	
	@PostMapping("/base/maint/addSchedulerSrc")
	public ModelAndView addScheduler(@RequestParam(name = "jobType") String jobType,
			@RequestParam(name = "jobName") String jobName, @RequestParam(name = "jobDescription") String description,
			@RequestParam(name = "onHold", required = false) String onHold,
			@RequestParam(name = "startDate") String startDate, @RequestParam(name = "startTime") String startTime,
			@RequestParam(name = "selectEndDate") String selectionEndDate,
			@RequestParam(name = "endDate", required = false) String endDate,
			@RequestParam(name = "endTime", required = false) String endTime,
			@RequestParam(name = "performTask") String performTask,
			@RequestParam(name = "secPerformTaskValue", required = false) String secSelectionPerformTask,
			@RequestParam(name = "checkbox-perform-task[]", required = false) List<String> thirdSelectionPerformTask,
			@RequestParam(name = "durationTimeStart", required = false) String durationStart,
			@RequestParam(name = "durationTimeEnd", required = false) String durationEnd, HttpServletRequest request, HttpSession session)
			throws ParseException {

		//Map<String, Object> model = (Map<String, Object>) session.getAttribute("sessionModel");
		
		jobSchedServ.getAllJobs();
		model.put("jobLists", schedulerServ.searchJobScheduler("", "", "", ""));
		model.put("jobGroupInfo", schedulerServ.searchAllJobGroup());
		model.put("jobStatusInfo", schedulerServ.searchAllJobStatus());
		model.remove("error");
		model.remove("success");
		
		try {
			
				String errorMsg = "";
				
				errorMsg += commonValServ.validateMandatoryInput(jobType, "Job Type");
				errorMsg += commonValServ.validateInputLength(jobType, 20, "Job Type");
				errorMsg += commonValServ.validateMandatoryInput(jobName, "Job Name");
				errorMsg += commonValServ.validateInputLength(jobName, 200, "Job Name");
				errorMsg += commonValServ.validateMandatoryInput(startDate, "Start Date");
				errorMsg += commonValServ.validateDateFormat(startDate, "Start Date");
				if (selectionEndDate.equals("1")) {
					errorMsg += commonValServ.validateMandatoryInput(endDate, "End Date");
					errorMsg += commonValServ.validateDateFormat(endDate, "End Date");
					errorMsg += commonValServ.validateDateRange(startDate, endDate, "Start Date", "End Date");
				}
				errorMsg += commonValServ.validateMandatoryInput(selectionEndDate, "Selection End Date");
				errorMsg += commonValServ.validateMandatoryInput(performTask, "Perform Task");
				errorMsg += errorChecking(performTask, thirdSelectionPerformTask, model);
		
			if (errorMsg.length() != 0) {
				model.put("error", errorMsg);
				jobSchedServ.getAllJobs();
				
				holdData(jobType, jobName, description, onHold, startDate, startTime, selectionEndDate, endDate, endTime,
						performTask, secSelectionPerformTask, thirdSelectionPerformTask, durationStart, durationEnd, model);
				
				return new ModelAndView("/base/maint/addScheduler", model);
			}
		
		//FSGS) Azmeer 09/03/2021 - Add checking for startTime START
		String startDateStr = "";
		if (startTime == null || startTime.isEmpty()) {
			startDateStr = startDate + " " + "00:00:00";
		} else {
			startDateStr = startDate + " " + startTime;
		}
		//FSGS) Azmeer 09/03/2021 - Add checking for startTime END
		
		String endDateStr = "";
		
		if (endTime == null || endTime.isEmpty()) {
			endDateStr = endDate + " " + "00:00:00";
		} else {
			endDateStr = endDate + " " + endTime;
		}
		
		Date startDateConvert = dateFormatterOld.parse(startDateStr);
		Date endDateConvert = null;
		
		if (selectionEndDate.equals("1")) {
			endDateConvert = dateFormatterOld.parse(endDateStr);
		}
		
		logger.debug(
				"addScheduler() performTask==>{}; secSelectionPerformTask==>{}; startDateStr==>{}; endDateStr==>{}; durationStart==>{}; durationEnd==>{}",
				performTask, secSelectionPerformTask, startDateStr, endDateStr, durationStart, durationEnd);
		
		Trigger cronTriggerBean = null;
		
		if (!jobSchedServ.isJobWithNamePresent(jobName, jobType)) {
			if (performTask.equals("1") || performTask.equals("5") || performTask.equals("7")) {
				//1 every day, 5 every hour, 7 every minute
				
				if (durationStart != null && !durationStart.isEmpty()) {
					cronTriggerBean = doCronWithDuration(jobType, jobName, selectionEndDate, durationStart, durationEnd,
							startDateConvert, endDateConvert);
					
				} else {
					cronTriggerBean = doDefaultCronEvery(jobType, jobName, selectionEndDate, performTask,
							startDateConvert, endDateConvert);
				}
					
			} else if (performTask.equals("2") || performTask.equals("6") || performTask.equals("8")) {
				// 2 every x days, 6 every x hours, 8 every x minutes
				if (!secSelectionPerformTask.isEmpty()) {

					if (secSelectionPerformTask.contains(",")) {
						secSelectionPerformTask = secSelectionPerformTask.replace(',', ' ');
						secSelectionPerformTask = secSelectionPerformTask.trim();
					}

					if (durationStart != null && !durationStart.isEmpty() && performTask.equals("2")) {
						cronTriggerBean = doCronEveryXWithDuration(jobType, jobName, selectionEndDate,
								secSelectionPerformTask, durationStart, durationEnd, startDateConvert, endDateConvert);
					} else {
						cronTriggerBean = doDefaultCronEveryX(jobType, jobName, selectionEndDate, performTask,
								secSelectionPerformTask, startDateConvert, endDateConvert);
					}
				}
			} else if (performTask.equals("3")) {
				cronTriggerBean = doWeekDaysCron(jobType, jobName, selectionEndDate, thirdSelectionPerformTask,
						durationStart, durationEnd, startDateConvert, endDateConvert, cronTriggerBean);

			} else if (performTask.equals("4")) {
				cronTriggerBean = doMonthlyCron(jobType, jobName, selectionEndDate, durationStart, durationEnd,
						startDateConvert, endDateConvert);
			}
			
		} else {

			holdData(jobType, jobName, description, onHold, startDate, startTime, selectionEndDate, endDate, endTime,
					performTask, secSelectionPerformTask, thirdSelectionPerformTask, durationStart, durationEnd, model);

			model.put("error", "Job name " + jobName + " already exist!");

			return new ModelAndView("/base/maint/addScheduler", model);
		}
		
		boolean status = jobSchedServ.scheduleCronJob(jobName, jobType, description, CronJob.class, cronTriggerBean, (String)model.get("loggedUser"));
		
		if (status) {
			String performTaskDatabase = setTaskDescription(performTask, secSelectionPerformTask,
					thirdSelectionPerformTask);
			
			String taskOptionDetail = setTaskOptionDetail(performTask, secSelectionPerformTask, thirdSelectionPerformTask, durationStart, durationEnd);
			
			schedulerServ.addJobScheduler(jobName, jobType, description, startDateConvert, endDateConvert,
					performTaskDatabase, request.getRemoteUser(), performTask, taskOptionDetail);
			
			model.put("success", "Job has been scheduled successfully!");
			
			//FSGS) Kent 26/2/2021 Add/changed - Add Function executed log START
			trxHistServ.addTrxHistory(new Date(), "Insert Scheduler", request.getRemoteUser(),
					CommonConstants.FUNCTION_TYPE_INSERT, jobName, CommonConstants.RECORD_TYPE_ID_JOB_SCHEDULER, null);
			//FSGS) Kent 26/2/2021 Add/changed - Add Function executed log END
		} else {
			model.put("error", "Job has been failed to scheduled!");			
		}
		
		if (onHold != null) {
			jobSchedServ.pauseJob(jobName, jobType, (String)model.get("loggedUser"));
		}
		
			return new ModelAndView("/base/maint/schedulerList", model);
		
		}catch (Exception e){
			
			model.put("error", "Please select appropriate radio button and values for perform task.");
			
			return new ModelAndView("/base/maint/addScheduler", model);
		}
	}
	
	@PostMapping("/base/maint/schedulerDel")
	public ModelAndView deleteScheduler(HttpServletRequest request, HttpSession session) {
		
		//Map<String, Object> model = (Map<String, Object>) session.getAttribute("sessionModel");
		
		model.remove("error");
		model.remove("success");
		try {
			if (request.getParameterValues("tableRow") != null) {
				for (String jobName : request.getParameterValues("tableRow")) {
					String[] schedulerInfo = jobName.split(":");
					
					jobSchedServ.deleteJob(schedulerInfo[0], schedulerInfo[1], (String)model.get("loggedUser"));
					schedulerServ.deleteJobScheduler(
							schedulerServ.searchIdJobScheduler(schedulerInfo[0], schedulerInfo[1]));
					
					//FSGS) Kent 26/2/2021 Add/changed - Add Function executed log START
					trxHistServ.addTrxHistory(new Date(), "Delete Scheduler", request.getRemoteUser(),
							CommonConstants.FUNCTION_TYPE_DELETE, jobName, CommonConstants.RECORD_TYPE_ID_JOB_SCHEDULER, null);
					//FSGS) Kent 26/2/2021 Add/changed - Add Function executed log END
				}
			}
			model.put("success", msgSource.getMessage("msgSuccessDelete", new Object[] {}, Locale.getDefault()));
		} catch (Exception e) {
			model.put("error", msgSource.getMessage("msgFailDelete", new Object[] {}, Locale.getDefault()));
		}

		jobSchedServ.getAllJobs();
		model.put("jobLists", schedulerServ.searchJobScheduler("", "", "", ""));
		model.put("jobGroupInfo", schedulerServ.searchAllJobGroup());
		model.put("jobStatusInfo", schedulerServ.searchAllJobStatus());

		return new ModelAndView("/base/maint/schedulerList", model);
	}
	
	@GetMapping("/base/maint/viewScheduler")
	public ModelAndView viewScheduler(@RequestParam(name = "jobId", required = false) String jobId, HttpServletRequest request) {
		
		//Map<String, Object> model = new HashMap<String, Object>();
		
		{
			/*
			 * to cater for navigate back from sub-module/page 
			 * e.g. fromschedulerActivityList
			 */
			if (null == jobId || jobId.isEmpty())
				jobId = (String) model.get("jobId");
		}
		
		model.remove("error");
		model.remove("success");
		//model = auth.onPageLoad(model, request);
		SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy hh:mm:ss a"); 

		JobScheduler job = schedulerServ.searchOnlyOneJob(jobId);
		model.put("jobId", jobId); // to be used in sub-module/page 
		model.put("jobGroup", job.getJobGroup());
		model.put("jobName", job.getJobName());
		model.put("jobDescription", job.getDescription());
		model.put("jobStatus", job.getJobStatus());
		
		String startDateConvert = formatter.format(job.getStartDate());
		model.put("startDate", startDateConvert);
		
		if (job.getEndDate() != null) {
			String endDateConvert = formatter.format(job.getEndDate());
			model.put("endDate", endDateConvert);
		} else {
			model.put("endDate", "No End Date");
		}
		
		model.put("performTask", job.getPerformTask());
		
		String createdTime = formatter.format(job.getCreatedTime());
		String createdMsg = "Created by " + job.getCreatedUser() + " on " + createdTime; 
		model.put("createdMsg", createdMsg);
		
		if (job.getUpdatedUser() != null && job.getUpdatedTime() != null) {
			String updatedTime = formatter.format(job.getUpdatedTime());
			String updatedMsg = "Modified by " + job.getUpdatedUser() + " on " + updatedTime;
			model.put("modifiedMsg", updatedMsg);
		} else {
			model.put("modifiedMsg", "Modified by --");
		}
		
		// FSGS) Kent 26/2/2021 Add/changed - Add Function executed log START
		trxHistServ.addTrxHistory(new Date(), "View Scheduler", request.getRemoteUser(), CommonConstants.FUNCTION_TYPE_VIEW,
				job.getJobName(), CommonConstants.RECORD_TYPE_ID_JOB_SCHEDULER, null);
		// FSGS) Kent 26/2/2021 Add/changed - Add Function executed log END
		
		//request.getSession().setAttribute("sessionModel", model);
		
		return new ModelAndView("/base/maint/viewScheduler", model);
	}
	

	
	// Initialize edit scheduler screen
	@PostMapping("/base/maint/editScheduler")
	public ModelAndView editSchedulerPanel(@RequestParam(name = "jobGroup") String jobGroup, 
			@RequestParam(name = "jobName") String jobName, HttpServletRequest request, HttpSession session) {
		
		//Map<String, Object> model = (Map<String, Object>) session.getAttribute("sessionModel");
		
		//model = auth.onPageLoad(model, request);
		JobScheduler job = schedulerServ.searchOnlyOneJob(schedulerServ.searchIdJobScheduler(jobName, jobGroup));
		
		setScreen(model, job);		
								
		return new ModelAndView("/base/maint/editScheduler", model);
	}
	
	
	@PostMapping("/base/maint/editSchedulerSrc")
	public ModelAndView editScheduler(@RequestParam(name = "jobGroup") String jobType,
			@RequestParam(name = "jobName") String jobName, @RequestParam(name = "jobDescription") String description,
			@RequestParam(name = "onHold", required = false) String onHold,
			@RequestParam(name = "startDate") String startDate, @RequestParam(name = "startTime") String startTime,
			@RequestParam(name = "selectEndDate") String selectionEndDate,
			@RequestParam(name = "endDate", required = false) String endDate,
			@RequestParam(name = "endTime", required = false) String endTime,
			@RequestParam(name = "performTask") String performTask,
			@RequestParam(name = "secPerformTaskValue", required = false) String secSelectionPerformTask,
			@RequestParam(name = "checkbox-perform-task[]", required = false) List<String> thirdSelectionPerformTask,
			@RequestParam(name = "durationTimeStart", required = false) String durationStart,
			@RequestParam(name = "durationTimeEnd", required = false) String durationEnd, HttpServletRequest request, 
			HttpSession session)
			throws ParseException {

		//Map<String, Object> model = (Map<String, Object>) session.getAttribute("sessionModel");
		JobScheduler job = schedulerServ.searchOnlyOneJob(schedulerServ.searchIdJobScheduler(jobName, jobType));
		try {
		
		model.remove("error");
		model.remove("success");
		String errorMsg = "";
		
		errorMsg += commonValServ.validateMandatoryInput(jobType, "Job Type");
		errorMsg += commonValServ.validateInputLength(jobType, 20, "Job Type");
		errorMsg += commonValServ.validateMandatoryInput(jobName, "Job Name");
		errorMsg += commonValServ.validateInputLength(jobName, 200, "Job Name");
		errorMsg += commonValServ.validateMandatoryInput(startDate, "Start Date");
		errorMsg += commonValServ.validateDateFormat(startDate, "Start Date");
		if (selectionEndDate.equals("1")) {
			errorMsg += commonValServ.validateMandatoryInput(endDate, "End Date");
			errorMsg += commonValServ.validateDateFormat(endDate, "End Date");
			errorMsg += commonValServ.validateDateRange(startDate, endDate, "Start Date", "End Date");
		}
		errorMsg += commonValServ.validateMandatoryInput(selectionEndDate, "Selection End Date");
		errorMsg += commonValServ.validateMandatoryInput(performTask, "Perform Task");
		errorMsg += errorChecking(performTask, thirdSelectionPerformTask, model);
		
		if (errorMsg.length() != 0) {
			model.put("error", errorMsg);
			return new ModelAndView("/base/maint/editScheduler", setScreen(model, job));
		}
		
		String startDateStr = startDate + " " + startTime;
		String endDateStr = "";
		
		if (endTime == null || endTime.isEmpty()) {
			endDateStr = endDate + " " + "00:00:00";
		} else {
			endDateStr = endDate + " " + endTime;
		}
		
		Date startDateConvert = dateFormatterOld.parse(startDateStr);
		Date endDateConvert = null;
		
		if (selectionEndDate.equals("1")) {
			endDateConvert = dateFormatterOld.parse(endDateStr);
		}
		
		logger.debug(
				"editScheduler() performTask==>{}; secSelectionPerformTask==>{}; startDateStr==>{}; endDateStr==>{}; durationStart==>{}; durationEnd==>{}",
				performTask, secSelectionPerformTask, startDateStr, endDateStr, durationStart, durationEnd);
		
		Trigger cronTriggerBean = null;
		
		if (performTask.equals("1") || performTask.equals("5") || performTask.equals("7")) {			
			if (durationStart != null && !durationStart.isEmpty()) {
				cronTriggerBean = doCronWithDuration(jobType, jobName, selectionEndDate, durationStart, durationEnd,
						startDateConvert, endDateConvert);
				
			} else {
				cronTriggerBean = doDefaultCronEvery(jobType, jobName, selectionEndDate, performTask,
						startDateConvert, endDateConvert);
				
			}
		} else if (performTask.equals("2") || performTask.equals("6") || performTask.equals("8")) {
			//2 every x days, 6 every x hours, 8 every x minutes			
			if (!secSelectionPerformTask.isEmpty()) {
				
				if(secSelectionPerformTask.contains(",")) {
					secSelectionPerformTask=secSelectionPerformTask.replace(',',' ');
					secSelectionPerformTask=secSelectionPerformTask.trim();
				}
				
				if (durationStart != null && !durationStart.isEmpty() && performTask.equals("2")) {
					cronTriggerBean = doCronEveryXWithDuration(jobType, jobName, selectionEndDate,
							secSelectionPerformTask, durationStart, durationEnd, startDateConvert, endDateConvert);
					
				} else {
					cronTriggerBean = doDefaultCronEveryX(jobType, jobName, selectionEndDate, performTask,
							secSelectionPerformTask, startDateConvert, endDateConvert);
					
				}
			}
		} else if (performTask.equals("3")) {
			cronTriggerBean = doWeekDaysCron(jobType, jobName, selectionEndDate, thirdSelectionPerformTask,
					durationStart, durationEnd, startDateConvert, endDateConvert, cronTriggerBean);
			
		} else if (performTask.equals("4")) {
			cronTriggerBean = doMonthlyCron(jobType, jobName, selectionEndDate, durationStart, durationEnd,
					startDateConvert, endDateConvert);
			
		}
		
		boolean status = jobSchedServ.updateCronJob(jobName, jobType, description, CronJob.class, cronTriggerBean, (String)model.get("loggedUser"));
		
		if (status) {
			String performTaskDatabase = setTaskDescription(performTask, secSelectionPerformTask,
					thirdSelectionPerformTask);
			
			String taskOptionDetail = setTaskOptionDetail(performTask, secSelectionPerformTask, thirdSelectionPerformTask, durationStart, durationEnd);
			
			schedulerServ.updateJobScheduler(description, startDateConvert, endDateConvert, performTaskDatabase,
					request.getRemoteUser(), schedulerServ.searchIdJobScheduler(jobName, jobType), performTask, taskOptionDetail);
			model.put("success", "Job has been rescheduled successfully!");
			
			//FSGS) Kent 26/2/2021 Add/changed - Add Function executed log START
			trxHistServ.addTrxHistory(new Date(), "Update Scheduler", request.getRemoteUser(),
					CommonConstants.FUNCTION_TYPE_UPDATE, jobName, CommonConstants.RECORD_TYPE_ID_JOB_SCHEDULER, null);
			//FSGS) Kent 26/2/2021 Add/changed - Add Function executed log END
		} else {
			model.put("error", "Job has been failed to rescheduled!");
		}
		
		if (onHold != null) {
			jobSchedServ.pauseJob(jobName, jobType, (String)model.get("loggedUser"));
		}
		
		jobSchedServ.getAllJobs();
		model.remove("jobName");
		model.put("jobLists", schedulerServ.searchJobScheduler("", "", "", ""));
		model.put("jobGroupInfo", schedulerServ.searchAllJobGroup());
		model.put("jobStatusInfo", schedulerServ.searchAllJobStatus());
		return new ModelAndView("/base/maint/schedulerList", model);
		} catch (Exception e){
						
			setScreen(model, job);
			model.put("error", "Please select appropriate radio button and values for perform task.");
			
			return new ModelAndView("/base/maint/editScheduler", model);
		}
	}
	
	private Map<String, Object> setScreen(Map<String, Object> model, JobScheduler job){
		
		if (job.getTaskOpt().equals(CommonConstants.PERFORM_TASK_EVERY_DAY)){
			
			model.put("chkDay", CommonConstants.RADIO_BUTTON_CHECKED);
			
		} else if (job.getTaskOpt().equals(CommonConstants.PERFORM_TASK_EVERY_OPTION_DAY)){
			
			model.put("dayValue", job.getTaskOptDet());
			model.put("chkOptionDay", CommonConstants.RADIO_BUTTON_CHECKED);
			
		} else if (job.getTaskOpt().equals(CommonConstants.PERFORM_TASK_EVERY_HOUR)){
			
			model.put("chkHour", CommonConstants.RADIO_BUTTON_CHECKED);
			
		} else if (job.getTaskOpt().equals(CommonConstants.PERFORM_TASK_EVERY_OPTION_HOUR)){
			
			model.put("hourValue", job.getTaskOptDet());
			model.put("chkOptionHour", CommonConstants.RADIO_BUTTON_CHECKED);
			
		} else if (job.getTaskOpt().equals(CommonConstants.PERFORM_TASK_EVERY_MINUTE)){
			
			model.put("chkMinute", CommonConstants.RADIO_BUTTON_CHECKED);
			
		} else if (job.getTaskOpt().equals(CommonConstants.PERFORM_TASK_EVERY_OPTION_MINUTE)){
			
			model.put("minuteValue", job.getTaskOptDet());
			model.put("chkOptionMinute", CommonConstants.RADIO_BUTTON_CHECKED);
			
		} else if (job.getTaskOpt().equals(CommonConstants.PERFORM_TASK_WEEKDAYS)){
			
			job.getTaskOptDet().split(",");
			
			String taskOptDet = job.getTaskOptDet(); 
			
			String[] weekdays = taskOptDet.split(",");
		
			model.put("chkWeekday", CommonConstants.RADIO_BUTTON_CHECKED);

			for (int i = 0; i < weekdays.length; i++) {
				
				if (weekdays[i].contains(CommonConstants.MONDAY)) {
					model.put("chkMonday", CommonConstants.RADIO_BUTTON_CHECKED);
				} if (weekdays[i].contains(CommonConstants.TUESDAY)) {
					model.put("chkTuesday", CommonConstants.RADIO_BUTTON_CHECKED);
				} if (weekdays[i].contains(CommonConstants.WEDNESDAY)) {
					model.put("chkWednesday", CommonConstants.RADIO_BUTTON_CHECKED);
				} if (weekdays[i].contains(CommonConstants.THURSDAY)) {
					model.put("chkThursday", CommonConstants.RADIO_BUTTON_CHECKED);
				} if (weekdays[i].contains(CommonConstants.FRIDAY)) {
					model.put("chkFriday", CommonConstants.RADIO_BUTTON_CHECKED);
				} if (weekdays[i].contains(CommonConstants.SATURDAY)) {
					model.put("chkSaturday", CommonConstants.RADIO_BUTTON_CHECKED);
				} if (weekdays[i].contains(CommonConstants.SUNDAY)) {
					model.put("chkSunday", CommonConstants.RADIO_BUTTON_CHECKED);
				}
			}

		} else if (job.getTaskOpt().equals(CommonConstants.PERFORM_TASK_MONTHLY)){
			
			job.getTaskOptDet().split(",");
			
			String taskOptDet = job.getTaskOptDet(); 
			String[] time = taskOptDet.split(",");
			String timeStart = "";
			String timeEnd   = "";

			if (!time[0].equals("EMPTY")) {
				timeStart = time[0];
			}
			
			if (!time[1].equals("EMPTY")) {
				timeEnd = time[1];
			}
			
			model.put("chkMonth", CommonConstants.RADIO_BUTTON_CHECKED);
			model.put("durationTimeStart", timeStart);
			model.put("durationTimeEnd", timeEnd);
			
		} else if (job.getTaskOpt().equals("")){
			
			//error
		} 
		
		model.put("jobGroup", job.getJobGroup());
		model.put("jobName", job.getJobName());
		model.put("jobDescription", job.getDescription());
		
		if (job.getJobStatus().equals("Paused")) {
			model.put("onHold", 1);
		}
		
		DateFormat formatter = new SimpleDateFormat("dd/MM/yyy");
		DateFormat timeFormatter = new SimpleDateFormat("HH:mm:ss");
		model.put("startDate", formatter.format(job.getStartDate()));
		model.put("startTime", timeFormatter.format(job.getStartDate()));
		model.put("radioEndDate", 2);
		
		if (job.getEndDate() != null) {
			model.put("endDate", formatter.format(job.getEndDate()));
			model.put("endTime", timeFormatter.format(job.getEndDate()));
			model.put("radioEndDate", 1);
		}
		
		return model;
	}
	
	private String errorChecking(String performTask, List<String> thirdSelectionPerformTask, Map<String, Object> model) {

		String errorMsg = "";
		
		if (performTask.equals(CommonConstants.PERFORM_TASK_WEEKDAYS) && thirdSelectionPerformTask == null) {
			errorMsg += "Please select at least one weekday from checkbox";	
		}
		
		return errorMsg;		
	}
	
	@GetMapping("/base/maint/schedulerActivityLogList")
	public ModelAndView loadSchedulerActivityLogList(HttpServletRequest request, HttpServletResponse response, HttpSession session) {
		
		//Map<String, Object> model = (Map<String, Object>) session.getAttribute("sessionModel");
		
		String view = "/base/maint/schedulerActivityLogList";
		String job = (String) model.get("jobName");
		String group = (String) model.get("jobGroup");
		String tempId = (String) model.get("jobId");

		model = auth.onPageLoad(model, request);

		List<SchedulerActivityLog> list = schedActServ.searchSchedulerActivityLog(job, group);

		model.put("schedulerActList", list);
		model.put("jobId", tempId); // to be used to navigate back to form
		model.put("jobName", job);
		model.put("jobGroup", group);
		
		//request.getSession().setAttribute("sessionModel", model);

		return new ModelAndView(view, model);
	}
	
	@RequestMapping("/base/maint/deleteSchedulerActLog")
	public ModelAndView deleteSchedulerActLog(HttpServletRequest request,
			@RequestParam(value = "tableRow") String[] tableRow, HttpSession session, RedirectAttributes ra) {

		String view = "/base/maint/schedulerActivityLogList";

		if (null == tableRow) {
			return new ModelAndView(new RedirectView(view, true));
		}

		try {
			for (String schedulerActivityLogPK : tableRow) {
				
				logger.debug("deleteSchedulerActLog() id={}", schedulerActivityLogPK);

				StringTokenizer token = new StringTokenizer(schedulerActivityLogPK, ";");
				String tn = "";
				String tg = "";
				String tt = "";

				while (token.hasMoreTokens()) {
					String str = token.nextToken();

					if (str.startsWith("tn=")) {
						tn = str.substring(3);
					} else if (str.startsWith("tg=")) {
						tg = str.substring(3);
					} else if (str.startsWith("tt=")) {
						tt = str.substring(3);
					}

				}
				schedActServ.deleteByIdNative(tn, tg, tt);
			}

			ra.addFlashAttribute("success", "Logs successfully purged.");

		} catch (Exception ex) {
			logger.error("deleteSchedulerActLog() ex={}", ex.getMessage());
			ra.addFlashAttribute("error", "Unable to purge logs. Please try again.");
		}

		return new ModelAndView(new RedirectView(view, true));
	}
	
	private Map<String, Object> holdData(String jobType, String jobName, String description, String onHold, String startDate,
			String startTime, String selectionEndDate, String endDate, String endTime, String performTask,
			String secSelectionPerformTask, List<String> thirdSelectionPerformTask, String durationStart,
			String durationEnd, Map<String, Object> model) {
		
		model.put("jobType", jobType);
		model.put("jobName", jobName);
		model.put("jobDescription", description);
		model.put("onHold", onHold);
		model.put("startDate", startDate);
		model.put("startTime", startTime);
		model.put("selectionEndDate", selectionEndDate);
		model.put("endDate", endDate);
		model.put("endTime", endTime);
		model.put("performTask", performTask);
		model.put("secSelectionPerformTask", secSelectionPerformTask);
		model.put("thirdSelectionPerformTask", thirdSelectionPerformTask);
		model.put("durationStart", durationStart);
		model.put("durationEnd", durationEnd);
		
		return model;

	}
	
	private Trigger doCronWithDuration(String jobType, String jobName, String selectionEndDate, String durationStart,
			String durationEnd, Date startDateConvert, Date endDateConvert) {
		Trigger cronTriggerBean;
		String[] arrayDurationStart = durationStart.split(":");
		
		int durationStartHour;
		int durationStartMinute;
		int durationStartSecond;
		
		try {
			durationStartHour = Integer.parseInt(arrayDurationStart[0]);
			durationStartMinute = Integer.parseInt(arrayDurationStart[1]);
			durationStartSecond = Integer.parseInt(arrayDurationStart[2]);
		} catch (NumberFormatException e) {
			durationStartHour = 0;
			durationStartMinute = 0;
			durationStartSecond = 0;
		}
		
		Cron cronDurationStart = CronBuilder
				.cron(CronDefinitionBuilder.instanceDefinitionFor(CronType.QUARTZ)).withYear(always())
				.withDoW(always()).withMonth(always()).withDoM(questionMark())
				.withHour(on(durationStartHour)).withMinute(on(durationStartMinute))
				.withSecond(on(durationStartSecond)).instance();
		
		String cronExpression = cronDurationStart.asString();
		
		logger.debug("doCronWithDuration() cron1={}",cronExpression);
		
		if (durationEnd == null) {
			if (selectionEndDate.equals("2")) {
				cronTriggerBean = TriggerBuilder.newTrigger()
				          .withIdentity(jobName, jobType)
				          .startAt(startDateConvert)
				          .withSchedule(CronScheduleBuilder.cronSchedule(cronExpression).withMisfireHandlingInstructionFireAndProceed())
				          .build();
			} else {
				cronTriggerBean = TriggerBuilder.newTrigger()
				          .withIdentity(jobName, jobType)
				          .startAt(startDateConvert)
				          .withSchedule(CronScheduleBuilder.cronSchedule(cronExpression).withMisfireHandlingInstructionFireAndProceed())
				          .endAt(endDateConvert)
				          .build();	
			}
		} else {
			String[] arrayDurationEnd = durationEnd.split(":");
			int durationEndHour;
			int durationEndMinute;
			int durationEndSecond;
			
			try {
				durationEndHour = Integer.parseInt(arrayDurationEnd[0]);
				durationEndMinute = Integer.parseInt(arrayDurationEnd[1]);
				durationEndSecond = Integer.parseInt(arrayDurationEnd[2]);
			} catch (NumberFormatException e) {
				durationEndHour = 0;
				durationEndMinute = 0;
				durationEndSecond = 0;
			}

			Cron cronDurationEnd = CronBuilder
					.cron(CronDefinitionBuilder.instanceDefinitionFor(CronType.QUARTZ)).withYear(always())
					.withDoW(always()).withMonth(always()).withDoM(questionMark())
					.withHour(between(durationStartHour, durationEndHour))
					.withMinute(between(durationStartMinute, durationEndMinute))
					.withSecond(between(durationStartSecond, durationEndSecond)).instance();						
			
			String cronExpressionWithEnd = cronDurationEnd.asString();
			logger.debug("doCronWithDuration() cron2={}",cronExpressionWithEnd);
			
			if (selectionEndDate.equals("2")) {
				cronTriggerBean = TriggerBuilder.newTrigger()
				          .withIdentity(jobName, jobType)
				          .startAt(startDateConvert)
				          .withSchedule(CronScheduleBuilder.cronSchedule(cronExpressionWithEnd).withMisfireHandlingInstructionFireAndProceed())
				          .build();
			} else {
				cronTriggerBean = TriggerBuilder.newTrigger()
				          .withIdentity(jobName, jobType)
				          .startAt(startDateConvert)
				          .withSchedule(CronScheduleBuilder.cronSchedule(cronExpressionWithEnd).withMisfireHandlingInstructionFireAndProceed())
				          .endAt(endDateConvert)
				          .build();
			}
		}
		return cronTriggerBean;
	}
	
	private Trigger doDefaultCronEvery(String jobType, String jobName, String selectionEndDate, String performTask,
			Date startDateConvert, Date endDateConvert) {
		Trigger cronTriggerBean;
		Cron cronDefaultDuration = null;					
		if (performTask.equals("1")) { // day
			cronDefaultDuration = CronBuilder
					.cron(CronDefinitionBuilder.instanceDefinitionFor(CronType.QUARTZ)).withYear(always())
					.withDoW(always()).withMonth(always()).withDoM(questionMark()).withHour(on(0))
					.withMinute(on(0)).withSecond(on(0)).instance();
		} else if (performTask.equals("5")) { // hour
			cronDefaultDuration = CronBuilder
					.cron(CronDefinitionBuilder.instanceDefinitionFor(CronType.QUARTZ)).withYear(always())
					.withDoW(always()).withMonth(always()).withDoM(questionMark()).withHour(always())
					.withMinute(on(0)).withSecond(on(0)).instance();
		} else if (performTask.equals("7")) { // minute
			cronDefaultDuration = CronBuilder
					.cron(CronDefinitionBuilder.instanceDefinitionFor(CronType.QUARTZ)).withYear(always())
					.withDoW(always()).withMonth(always()).withDoM(questionMark()).withHour(always())
					.withMinute(always()).withSecond(on(0)).instance();
		}
		
		String cronExpressionDefault = cronDefaultDuration.asString();					
		logger.debug("doDefaultCronEvery() cron3={}",cronExpressionDefault);
		
		if (selectionEndDate.equals("2")) {
			cronTriggerBean = TriggerBuilder.newTrigger()
			          .withIdentity(jobName, jobType)
			          .startAt(startDateConvert)
			          .withSchedule(CronScheduleBuilder.cronSchedule(cronExpressionDefault)
			        		  .withMisfireHandlingInstructionFireAndProceed())
			          .build();
		} else {
			cronTriggerBean = TriggerBuilder.newTrigger()
			          .withIdentity(jobName, jobType)
			          .startAt(startDateConvert)
			          .withSchedule(CronScheduleBuilder.cronSchedule(cronExpressionDefault)
			        		  .withMisfireHandlingInstructionFireAndProceed())
			          .endAt(endDateConvert)
			          .build();
		}
		return cronTriggerBean;
	}
	
	private Trigger doCronEveryXWithDuration(String jobType, String jobName, String selectionEndDate,
			String secSelectionPerformTask, String durationStart, String durationEnd, Date startDateConvert,
			Date endDateConvert) {
		Trigger cronTriggerBean;
		String[] arrayDurationStart = durationStart.split(":");
		int durationStartHour;
		int durationStartMinute;
		int durationStartSecond;
		int occurance;
		
		try {
			durationStartHour = Integer.parseInt(arrayDurationStart[0]);
			durationStartMinute = Integer.parseInt(arrayDurationStart[1]);
			durationStartSecond = Integer.parseInt(arrayDurationStart[2]);
			occurance = Integer.parseInt(secSelectionPerformTask);
		} catch (NumberFormatException e) {
			durationStartHour = 0;
			durationStartMinute = 0;
			durationStartSecond = 0;
			occurance = 1;
		}
		
		Cron cronDurationStart = CronBuilder
				.cron(CronDefinitionBuilder.instanceDefinitionFor(CronType.QUARTZ)).withYear(always())
				.withDoW(questionMark()).withMonth(always()).withDoM(every(occurance))
				.withHour(on(durationStartHour)).withMinute(on(durationStartMinute))
				.withSecond(on(durationStartSecond)).instance();
		
		String cronExpression = cronDurationStart.asString();
		logger.debug("doCronEveryXWithDuration() cron4={}",cronExpression);
		
		if (durationEnd == null) {
			if (selectionEndDate.equals("2")) {
				cronTriggerBean = TriggerBuilder.newTrigger()
				          .withIdentity(jobName, jobType)
				          .startAt(startDateConvert)
				          .withSchedule(CronScheduleBuilder.cronSchedule(cronExpression)
				        		  .withMisfireHandlingInstructionFireAndProceed())
				          .build();
			} else {
				cronTriggerBean = TriggerBuilder.newTrigger()
				          .withIdentity(jobName, jobType)
				          .startAt(startDateConvert)
				          .withSchedule(CronScheduleBuilder.cronSchedule(cronExpression)
				        		  .withMisfireHandlingInstructionFireAndProceed())
				          .endAt(endDateConvert)
				          .build();
			}
		} else {
			String[] arrayDurationEnd = durationEnd.split(":");
			int durationEndHour;
			int durationEndMinute;
			int durationEndSecond;
			
			try {
				durationEndHour = Integer.parseInt(arrayDurationEnd[0]);
				durationEndMinute = Integer.parseInt(arrayDurationEnd[1]);
				durationEndSecond = Integer.parseInt(arrayDurationEnd[2]);
			} catch (NumberFormatException e) {
				durationEndHour = 0;
				durationEndMinute = 0;
				durationEndSecond = 0;
			}
			
			Cron cronDurationEnd = CronBuilder
					.cron(CronDefinitionBuilder.instanceDefinitionFor(CronType.QUARTZ))
					.withYear(always()).withDoW(questionMark()).withMonth(always())
					.withDoM(every(occurance)).withHour(between(durationStartHour, durationEndHour))
					.withMinute(between(durationStartMinute, durationEndMinute))
					.withSecond(between(durationStartSecond, durationEndSecond)).instance();
			
			String cronExpressionWithEnd = cronDurationEnd.asString();
			logger.debug("doCronEveryXWithDuration() cron5={}",cronExpressionWithEnd);
			
			if (selectionEndDate.equals("2")) {
				cronTriggerBean = TriggerBuilder.newTrigger()
				          .withIdentity(jobName, jobType)
				          .startAt(startDateConvert)
				          .withSchedule(CronScheduleBuilder.cronSchedule(cronExpressionWithEnd)
				        		  .withMisfireHandlingInstructionFireAndProceed())
				          .build();
			} else {
				cronTriggerBean = TriggerBuilder.newTrigger()
				          .withIdentity(jobName, jobType)
				          .startAt(startDateConvert)
				          .withSchedule(CronScheduleBuilder.cronSchedule(cronExpressionWithEnd)
				        		  .withMisfireHandlingInstructionFireAndProceed())
				          .endAt(endDateConvert)
				          .build();
			}
		}
		return cronTriggerBean;
	}
	
	private Trigger doDefaultCronEveryX(String jobType, String jobName, String selectionEndDate, String performTask,
			String secSelectionPerformTask, Date startDateConvert, Date endDateConvert) {
		Trigger cronTriggerBean;
		int occurance = Integer.parseInt(secSelectionPerformTask);
		Cron cronDefaultDuration = null;
		if (performTask.equals("2")) {
			cronDefaultDuration = CronBuilder
					.cron(CronDefinitionBuilder.instanceDefinitionFor(CronType.QUARTZ))
					.withYear(always()).withDoW(questionMark()).withMonth(always())
					.withDoM(every(occurance)).withHour(on(0)).withMinute(on(0)).withSecond(on(0))
					.instance();
		} else if (performTask.equals("6")) {
			cronDefaultDuration = CronBuilder
					.cron(CronDefinitionBuilder.instanceDefinitionFor(CronType.QUARTZ))
					.withYear(always()).withDoW(questionMark()).withMonth(always()).withDoM(always())
					.withHour(every(occurance)).withMinute(on(0)).withSecond(on(0)).instance();
		} else if (performTask.equals("8")) {
			cronDefaultDuration = CronBuilder
					.cron(CronDefinitionBuilder.instanceDefinitionFor(CronType.QUARTZ))
					.withYear(always()).withDoW(questionMark()).withMonth(always()).withDoM(always())
					.withHour(always()).withMinute(every(occurance)).withSecond(on(0)).instance();
		}
		
		String cronExpressionDefault = cronDefaultDuration.asString();
		logger.debug("doDefaultCronEveryX() cron6={}",cronExpressionDefault);
		
		if (selectionEndDate.equals("2")) {
			cronTriggerBean = TriggerBuilder.newTrigger()
			          .withIdentity(jobName, jobType)
			          .startAt(startDateConvert)
			          .withSchedule(CronScheduleBuilder.cronSchedule(cronExpressionDefault)
			        		  .withMisfireHandlingInstructionFireAndProceed())
			          .build();
		} else {
			cronTriggerBean = TriggerBuilder.newTrigger()
			          .withIdentity(jobName, jobType)
			          .startAt(startDateConvert)
			          .withSchedule(CronScheduleBuilder.cronSchedule(cronExpressionDefault)
			        		  .withMisfireHandlingInstructionFireAndProceed())
			          .endAt(endDateConvert)
			          .build();
		}
		return cronTriggerBean;
	}
	
	private Trigger doWeekDaysCron(String jobType, String jobName, String selectionEndDate,
			List<String> thirdSelectionPerformTask, String durationStart, String durationEnd, Date startDateConvert,
			Date endDateConvert, Trigger cronTriggerBean) {
		if (thirdSelectionPerformTask.size() > 0) {
			ArrayList<String> cronWeekdays = new ArrayList<>();
			
			for (int i = 0; i < thirdSelectionPerformTask.size(); i++) {
				String thirdSelectionValue = thirdSelectionPerformTask.get(i);
				if (thirdSelectionValue.equals("1")) {
					cronWeekdays.add("MON");
				} else if (thirdSelectionValue.equals("2")) {
					cronWeekdays.add("TUE");
				} else if (thirdSelectionValue.equals("3")) {
					cronWeekdays.add("WED");
				} else if (thirdSelectionValue.equals("4")) {
					cronWeekdays.add("THU");
				} else if (thirdSelectionValue.equals("5")) {
					cronWeekdays.add("FRI");
				} else if (thirdSelectionValue.equals("6")) {
					cronWeekdays.add("SAT");
				} else if (thirdSelectionValue.equals("7")) {
					cronWeekdays.add("SUN");
				}
			}
			
			StringBuilder cronBuilder = new StringBuilder();
			for (int i = 0; i < cronWeekdays.size(); i++) {
				cronBuilder.append(cronWeekdays.get(i));
				if (i != cronWeekdays.size() - 1) {
					cronBuilder.append(",");
				}
			}
			String dayOfWeek = cronBuilder.toString();
			
			if (durationStart != null && !durationStart.isEmpty()) {
				String[] arrayDurationStart = durationStart.split(":");
				int durationStartHour;
				int durationStartMinute;
				int durationStartSecond;
				
				try {
					durationStartHour = Integer.parseInt(arrayDurationStart[0]);
					durationStartMinute = Integer.parseInt(arrayDurationStart[1]);
					durationStartSecond = Integer.parseInt(arrayDurationStart[2]);
				} catch (NumberFormatException e) {
					durationStartHour = 0;
					durationStartMinute = 0;
					durationStartSecond = 0;
				}				
				
				Cron cronDurationStart = CronBuilder
						.cron(CronDefinitionBuilder.instanceDefinitionFor(CronType.QUARTZ)).withYear(always())
						.withDoM(questionMark()).withMonth(always()).withDoW(always())
						.withHour(on(durationStartHour)).withMinute(on(durationStartMinute))
						.withSecond(on(durationStartSecond)).instance();
																			
				String cronExpression = cronDurationStart.asString();        
				String[] arrayCronExpression = cronExpression.split("\\s+"); 
				String finalCronExpression = "";
				
				for (int i = 0; i < 7; i++) {
					if (i == 5) {
						finalCronExpression += dayOfWeek + " ";
					} else if (i == 6) {
						finalCronExpression += arrayCronExpression[i];
					} else {
						finalCronExpression += arrayCronExpression[i] + " ";
					}
				}						

				logger.debug("doWeekDaysCron() cron7={}",finalCronExpression);
				
				if (durationEnd == null) {
					if (selectionEndDate.equals("2")) {
						cronTriggerBean = TriggerBuilder.newTrigger()
						          .withIdentity(jobName, jobType)
						          .startAt(startDateConvert)
						          .withSchedule(CronScheduleBuilder.cronSchedule(finalCronExpression)
						        		  .withMisfireHandlingInstructionFireAndProceed())
						          .build();
					} else {
						cronTriggerBean = TriggerBuilder.newTrigger()
						          .withIdentity(jobName, jobType)
						          .startAt(startDateConvert)
						          .withSchedule(CronScheduleBuilder.cronSchedule(finalCronExpression)
						        		  .withMisfireHandlingInstructionFireAndProceed())
						          .endAt(endDateConvert)
						          .build();
					}
				} else {
					String[] arrayDurationEnd = durationEnd.split(":");
					int durationEndHour;
					int durationEndMinute;
					int durationEndSecond;
					
					try {
						durationEndHour = Integer.parseInt(arrayDurationEnd[0]);
						durationEndMinute = Integer.parseInt(arrayDurationEnd[1]);
						durationEndSecond = Integer.parseInt(arrayDurationEnd[2]);
					} catch (NumberFormatException e) {
						durationEndHour = 0;
						durationEndMinute = 0;
						durationEndSecond = 0;
					}
				
					Cron cronDurationEnd = CronBuilder
							.cron(CronDefinitionBuilder.instanceDefinitionFor(CronType.QUARTZ))
							.withYear(always()).withDoW(always()).withMonth(always()).withDoM(questionMark())
							.withHour(between(durationStartHour, durationEndHour))
							.withMinute(between(durationStartMinute, durationEndMinute))
							.withSecond(between(durationStartSecond, durationEndSecond)).instance();
					
					String cronExpressionWithEnd = cronDurationEnd.asString();
					String[] arrayCronExpressionWithEnd = cronExpressionWithEnd.split("\\s+"); 
					String finalCronExpressionWithEnd = "";
					
					for (int i = 0; i < 7; i++) {
						if (i == 5) {
							finalCronExpressionWithEnd += dayOfWeek + " ";
						} else if (i == 6) {
							finalCronExpressionWithEnd += arrayCronExpressionWithEnd[i];
						} else {
							finalCronExpressionWithEnd += arrayCronExpressionWithEnd[i] + " ";
						}
					}
					
					logger.debug("doWeekDaysCron() cron8={}",finalCronExpressionWithEnd);
					
					if (selectionEndDate.equals("2")) {
						cronTriggerBean = TriggerBuilder.newTrigger()
						          .withIdentity(jobName, jobType)
						          .startAt(startDateConvert)
						          .withSchedule(CronScheduleBuilder.cronSchedule(finalCronExpressionWithEnd)
						        		  .withMisfireHandlingInstructionFireAndProceed())
						          .build();
					} else {
						cronTriggerBean = TriggerBuilder.newTrigger()
						          .withIdentity(jobName, jobType)
						          .startAt(startDateConvert)
						          .withSchedule(CronScheduleBuilder.cronSchedule(finalCronExpressionWithEnd)
						        		  .withMisfireHandlingInstructionFireAndProceed())
						          .endAt(endDateConvert)
						          .build();
					}
				}
			} else {
				Cron cronDefaultDuration = CronBuilder
						.cron(CronDefinitionBuilder.instanceDefinitionFor(CronType.QUARTZ)).withYear(always())
						.withDoW(always()).withMonth(always()).withDoM(questionMark()).withHour(on(0))
						.withMinute(on(0)).withSecond(on(0)).instance();
				
				String cronExpressionDefault = cronDefaultDuration.asString();
				String[] arrayCronExpressionDefault = cronExpressionDefault.split("\\s+"); 
				String finalCronExpressionDefault = "";
				
				for (int i = 0; i < 7; i++) {
					if (i == 5) {
						finalCronExpressionDefault += dayOfWeek + " ";
					} else if (i == 6) {
						finalCronExpressionDefault += arrayCronExpressionDefault[i];
					} else {
						finalCronExpressionDefault += arrayCronExpressionDefault[i] + " ";
					}
				}
				
				logger.debug("doWeekDaysCron() cron9={}",finalCronExpressionDefault);
				
				if (selectionEndDate.equals("2")) {
					cronTriggerBean = TriggerBuilder.newTrigger()
					          .withIdentity(jobName, jobType)
					          .startAt(startDateConvert)
					          .withSchedule(CronScheduleBuilder.cronSchedule(finalCronExpressionDefault)
					        		  .withMisfireHandlingInstructionFireAndProceed())
					          .build();
				} else {
					cronTriggerBean = TriggerBuilder.newTrigger()
					          .withIdentity(jobName, jobType)
					          .startAt(startDateConvert)
					          .withSchedule(CronScheduleBuilder.cronSchedule(finalCronExpressionDefault)
					        		  .withMisfireHandlingInstructionFireAndProceed())
					          .endAt(endDateConvert)
					          .build();
				}
			}
		}
		return cronTriggerBean;
	}
	
	private Trigger doMonthlyCron(String jobType, String jobName, String selectionEndDate, String durationStart,
			String durationEnd, Date startDateConvert, Date endDateConvert) {
		Trigger cronTriggerBean;
		if (durationStart != null  && !durationStart.isEmpty()) {
			String[] arrayDurationStart = durationStart.split(":");
			int durationStartHour;
			int durationStartMinute;
			int durationStartSecond;
			
			try {
				durationStartHour = Integer.parseInt(arrayDurationStart[0]);
				durationStartMinute = Integer.parseInt(arrayDurationStart[1]);
				durationStartSecond = Integer.parseInt(arrayDurationStart[2]);
			} catch (NumberFormatException e) {
				durationStartHour = 0;
				durationStartMinute = 0;
				durationStartSecond = 0;
			}
		
			Cron cronDurationStart = CronBuilder
					.cron(CronDefinitionBuilder.instanceDefinitionFor(CronType.QUARTZ)).withYear(always())
					.withDoW(questionMark()).withMonth(always()).withDoM(on(1)).withHour(on(durationStartHour))
					.withMinute(on(durationStartMinute)).withSecond(on(durationStartSecond)).instance();
			
			String cronExpression = cronDurationStart.asString();
			logger.debug("doMonthlyCron() cron10={}",cronExpression);
			
			if (durationEnd == null) {
				if (selectionEndDate.equals("2")) {
					cronTriggerBean = TriggerBuilder.newTrigger()
					          .withIdentity(jobName, jobType)
					          .startAt(startDateConvert)
					          .withSchedule(CronScheduleBuilder.cronSchedule(cronExpression)
					        		  .withMisfireHandlingInstructionFireAndProceed())
					          .build();
				} else {
					cronTriggerBean = TriggerBuilder.newTrigger()
					          .withIdentity(jobName, jobType)
					          .startAt(startDateConvert)
					          .withSchedule(CronScheduleBuilder.cronSchedule(cronExpression)
					        		  .withMisfireHandlingInstructionFireAndProceed())
					          .endAt(endDateConvert)
					          .build();
				}
			} else {
				String[] arrayDurationEnd = durationEnd.split(":");
				int durationEndHour;
				int durationEndMinute;
				int durationEndSecond;
				
				try {
					durationEndHour = Integer.parseInt(arrayDurationEnd[0]);
					durationEndMinute = Integer.parseInt(arrayDurationEnd[1]);
					durationEndSecond = Integer.parseInt(arrayDurationEnd[2]);
				} catch (NumberFormatException e) {
					durationEndHour = 0;
					durationEndMinute = 0;
					durationEndSecond = 0;
				}
				
				Cron cronDurationEnd = CronBuilder
						.cron(CronDefinitionBuilder.instanceDefinitionFor(CronType.QUARTZ)).withYear(always())
						.withDoW(questionMark()).withMonth(always()).withDoM(on(1))
						.withHour(between(durationStartHour, durationEndHour))
						.withMinute(between(durationStartMinute, durationEndMinute))
						.withSecond(between(durationStartSecond, durationEndSecond)).instance();
				
				String cronExpressionWithEnd = cronDurationEnd.asString();
				logger.debug("doMonthlyCron() cron11={}",cronExpressionWithEnd);
				
				if (selectionEndDate.equals("2")) {
					cronTriggerBean = TriggerBuilder.newTrigger()
					          .withIdentity(jobName, jobType)
					          .startAt(startDateConvert)
					          .withSchedule(CronScheduleBuilder.cronSchedule(cronExpressionWithEnd)
					        		  .withMisfireHandlingInstructionFireAndProceed())
					          .build();
				} else {
					cronTriggerBean = TriggerBuilder.newTrigger()
					          .withIdentity(jobName, jobType)
					          .startAt(startDateConvert)
					          .withSchedule(CronScheduleBuilder.cronSchedule(cronExpressionWithEnd)
					        		  .withMisfireHandlingInstructionFireAndProceed())
					          .endAt(endDateConvert)
					          .build();
				}
			}
		} else {
			Cron cronDefaultDuration = CronBuilder
					.cron(CronDefinitionBuilder.instanceDefinitionFor(CronType.QUARTZ)).withYear(always())
					.withDoW(questionMark()).withMonth(always()).withDoM(on(1)).withHour(on(0))
					.withMinute(on(0)).withSecond(on(0)).instance();
			
			String cronExpressionDefault = cronDefaultDuration.asString();
			logger.debug("doMonthlyCron() cron12={}",cronExpressionDefault);
			
			if (selectionEndDate.equals("2")) {
				cronTriggerBean = TriggerBuilder.newTrigger()
				          .withIdentity(jobName, jobType)
				          .startAt(startDateConvert)
				          .withSchedule(CronScheduleBuilder.cronSchedule(cronExpressionDefault)
				        		  .withMisfireHandlingInstructionFireAndProceed())
				          .build();
			} else {
				cronTriggerBean = TriggerBuilder.newTrigger()
				          .withIdentity(jobName, jobType)
				          .startAt(startDateConvert)
				          .withSchedule(CronScheduleBuilder.cronSchedule(cronExpressionDefault)
				        		  .withMisfireHandlingInstructionFireAndProceed())
				          .endAt(endDateConvert)
				          .build();
			}
		}
		return cronTriggerBean;
	}
	
	private String setTaskDescription(String performTask, String secSelectionPerformTask,
			List<String> thirdSelectionPerformTask) {
		String performTaskDatabase = "";
		if (performTask.equals("1")) {
			performTaskDatabase = "Every Day";
		} else if (performTask.equals("2")) {
			performTaskDatabase = "Every " + secSelectionPerformTask + " Days";
		} else if (performTask.equals("5")) {
			performTaskDatabase = "Every Hour";
		} else if (performTask.equals("6")) {
			performTaskDatabase = "Every " + secSelectionPerformTask + " Hours";
		} else if (performTask.equals("7")) {
			performTaskDatabase = "Every Minute";
		} else if (performTask.equals("8")) {
			performTaskDatabase = "Every " + secSelectionPerformTask + " Minutes";
		} else if (performTask.equals("3")) {
			ArrayList<String> listWeekdays = new ArrayList<>();
			
			for (int i = 0; i < thirdSelectionPerformTask.size(); i++) {
				String thirdSelectionValue = thirdSelectionPerformTask.get(i);
				if (thirdSelectionValue.equals("1")) {
					listWeekdays.add("Monday");
				} else if (thirdSelectionValue.equals("2")) {
					listWeekdays.add("Tuesday");
				} else if (thirdSelectionValue.equals("3")) {
					listWeekdays.add("Wednesday");
				} else if (thirdSelectionValue.equals("4")) {
					listWeekdays.add("Thursday");
				} else if (thirdSelectionValue.equals("5")) {
					listWeekdays.add("Friday");
				} else if (thirdSelectionValue.equals("6")) {
					listWeekdays.add("Saturday");
				} else if (thirdSelectionValue.equals("7")) {
					listWeekdays.add("Sunday");
				}
			}
			
			StringBuilder cronBuilder = new StringBuilder();
			for (int i = 0; i < listWeekdays.size(); i++) {
				cronBuilder.append(listWeekdays.get(i));
				if (i != listWeekdays.size() - 1) {
					cronBuilder.append(", ");
				}
			}
			performTaskDatabase = cronBuilder.toString();
		} else if (performTask.equals("4")) {
			performTaskDatabase = "Every Month";
		}
		return performTaskDatabase;
	}
	
	private String setTaskOptionDetail(String performTask, String secSelectionPerformTask,
			List<String> thirdSelectionPerformTask, String monthlyTimeStart, String monthlyTimeEnd) {
		
		String taskDetail = secSelectionPerformTask;
		
		if (performTask.equals(CommonConstants.PERFORM_TASK_EVERY_DAY)  ||
			performTask.equals(CommonConstants.PERFORM_TASK_EVERY_HOUR) ||
			performTask.equals(CommonConstants.PERFORM_TASK_EVERY_MINUTE)	) {
			
			taskDetail = "";
		}
		
		else if (performTask.equals(CommonConstants.PERFORM_TASK_WEEKDAYS)) {
			
			ArrayList<String> listWeekdays = new ArrayList<>();
			
			for (int i = 0; i < thirdSelectionPerformTask.size(); i++) {
				String thirdSelectionValue = thirdSelectionPerformTask.get(i);
				if (thirdSelectionValue.equals(CommonConstants.PERFORM_TASK_MONDAY)) {
					listWeekdays.add(CommonConstants.MONDAY);
				} else if (thirdSelectionValue.equals(CommonConstants.PERFORM_TASK_TUESDAY)) {
					listWeekdays.add(CommonConstants.TUESDAY);
				} else if (thirdSelectionValue.equals(CommonConstants.PERFORM_TASK_WEDNESDAY)) {
					listWeekdays.add(CommonConstants.WEDNESDAY);
				} else if (thirdSelectionValue.equals(CommonConstants.PERFORM_TASK_THURSDAY)) {
					listWeekdays.add(CommonConstants.THURSDAY);
				} else if (thirdSelectionValue.equals(CommonConstants.PERFORM_TASK_FRIDAY)) {
					listWeekdays.add(CommonConstants.FRIDAY);
				} else if (thirdSelectionValue.equals(CommonConstants.PERFORM_TASK_SATURDAY)) {
					listWeekdays.add(CommonConstants.SATURDAY);
				} else if (thirdSelectionValue.equals(CommonConstants.PERFORM_TASK_SUNDAY)) {
					listWeekdays.add(CommonConstants.SUNDAY);
				}
			}
			
			StringBuilder cronBuilder = new StringBuilder();
			for (int i = 0; i < listWeekdays.size(); i++) {
				cronBuilder.append(listWeekdays.get(i));
				if (i != listWeekdays.size() - 1) {
					cronBuilder.append(",");
				}
			}
			taskDetail = cronBuilder.toString();
		
		} else if (performTask.equals(CommonConstants.PERFORM_TASK_MONTHLY)) {
			
			if (monthlyTimeStart.equals("")) {
				
				monthlyTimeStart = "EMPTY";
			}
			
			if (monthlyTimeEnd.equals("")) {
				
				monthlyTimeEnd = "EMPTY";
			}
						
				taskDetail = monthlyTimeStart + "," +monthlyTimeEnd;			
			
		} else {
			taskDetail = secSelectionPerformTask;
		}
		
		return taskDetail;
	}

}
