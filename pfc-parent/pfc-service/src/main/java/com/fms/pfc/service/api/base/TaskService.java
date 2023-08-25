package com.fms.pfc.service.api.base;

import java.util.List;
import java.util.StringTokenizer;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fms.pfc.domain.model.Task;
import com.fms.pfc.repository.base.api.TaskRepository;

@Service
public class TaskService {
	
	private final Logger logger = LoggerFactory.getLogger(TaskService.class);

	private TaskRepository taskRepository;

	@Autowired
	public TaskService(TaskRepository taskRepository) {
		super();
		this.taskRepository = taskRepository;
	}

	public List<Task> searchTask(String dateFr, String dateTo, String refNum, String assignedTo, String subject,
			String taskStatus, String exp1, String exp2, String exp3, String roleId, String currUser) {
		return taskRepository.searchTask(dateFr, dateTo, refNum, assignedTo, subject, taskStatus, exp1, exp2, exp3, roleId,
				currUser);
	}

	public Task searchDetailTask(String recordRef, int recordTypeId, String startDateTime) {
		return taskRepository.searchDetailTask(recordRef, recordTypeId, startDateTime);
	}

	public List<Task> searchTaskByReference(String recordRef, int recordTypeId) {
		return taskRepository.searchTaskByReference(recordRef, recordTypeId);
	}

	public void addProcess(String recordRef, int recordType, int processType, String orgId, String creatorId) {
		taskRepository.addProcess(recordRef, recordType, processType, orgId, creatorId);
	}

	public void addTask(String recordRef, int recordType, int processType, String orgId, String pooledActorId,
			int pooledActorType, String taskDesc, String creatorId, String taskRemark) {
		taskRepository.addTask(recordRef, recordType, processType, orgId, pooledActorId, pooledActorType, taskDesc, creatorId, taskRemark);
	}

	public void updEndTask(String recordRef, String actorId, int taskAction, String taskRemarks, int recordTypeId) {
		taskRepository.updEndTask(recordRef, actorId, taskAction, taskRemarks, recordTypeId);
	}

	public Task searchLatestTask(String recordRef, int recordTypeId) {
		return taskRepository.searchLatestTask(recordRef, recordTypeId);
	}
	
	public List<Task> searchOverdueTask(String recordRef, int recordType, String poolActor, int actorType){
		return taskRepository.searchOverdueTask(recordRef, recordType, poolActor, actorType);
	}
	
	public Task searchLatestTask(String recordRef, int recordTypeId, String pooledActor, int actionType,
			String fromRole, String taskRemarks) {
		return taskRepository.searchLatestTask(recordRef, recordTypeId, pooledActor, actionType, fromRole, taskRemarks);
	}

	public Task searchFirstTask(String recordRef, int recordTypeId, String pooledActor, int actionType, String fromRole,
			String taskRemarks) {
		return taskRepository.searchFirstTask(recordRef, recordTypeId, pooledActor, actionType, fromRole, taskRemarks);
	}
	
	public String findCreatorIfSystem(int refId, int recordTypeId, String tempActor) {
		//Special case - If creator still system, then:
		//Find this PR task having remark 'byMatl'
		// - In the remark, find rmid
		// - Then find that RM task by that rmid, look for latest 
		//    occurrence of Task involving maker assign to checker, 
		//    take the creator which is maker
		if (StringUtils.equalsIgnoreCase(tempActor, "system") || StringUtils.isEmpty(tempActor)) {
			Task prTask = searchLatestTask(String.valueOf(refId), recordTypeId, "CKR".trim(), 0, "CKR".trim(),
					"byMatl");

			if (null != prTask) {
				String remarks = StringUtils.remove(prTask.getTaskRemarks().trim(), "byMatl:");
				StringTokenizer st = new StringTokenizer(remarks, ":");
				String rmid = "";
				while (st.hasMoreElements()) {
					String elem = st.nextToken();
					if (StringUtils.startsWith(elem, "rm=")) {
						rmid = elem.substring(3);
					}
				}

				logger.debug("findCreatorIfSystem() remarks={},rmid={}", remarks, rmid);

				Task rmTask = searchLatestTask(rmid, 201, "CKR".trim(), 0, "MKR".trim(), "");

				if (null != rmTask)
					tempActor = rmTask.getCreatorId();
			}
		}
		return tempActor;
	}
}