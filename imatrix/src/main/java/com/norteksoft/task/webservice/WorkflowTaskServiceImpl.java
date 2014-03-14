package com.norteksoft.task.webservice;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;

import com.norteksoft.product.orm.Page;
import com.norteksoft.task.base.enumeration.TaskProcessingMode;
import com.norteksoft.task.base.enumeration.TaskProcessingResult;
import com.norteksoft.task.entity.HistoryWorkflowTask;
import com.norteksoft.task.entity.Task;
import com.norteksoft.task.entity.WorkflowTask;
import com.norteksoft.task.service.HistoryWorkflowTaskManager;
import com.norteksoft.task.service.WorkflowTaskManager;
import com.norteksoft.wf.engine.entity.WorkflowInstance;

//@WebService(endpointInterface = "com.norteksoft.task.webservice.WorkflowTaskService")
//@Transactional
public class WorkflowTaskServiceImpl implements WorkflowTaskService{

	private WorkflowTaskManager taskManager;
	
	@Autowired
	public void setWorkflowTaskManager(WorkflowTaskManager workflowTaskManager) {
		taskManager = workflowTaskManager;
	}
	
	public void saveTask(WorkflowTask workflowTask) {
		taskManager.saveTask(workflowTask);
	}

	public List<String> getTaskNamesByInstance(Long companyId, String instanceId) {
		return taskManager.getTaskNamesByInstance(companyId, instanceId);
	}
	
	public Page<WorkflowTask> getDelegateTasks(
			Long companyId, String loginName,Long userId, Page<WorkflowTask> page){
		return taskManager.getDelegateTasks(companyId, loginName,userId, page);
	}
	
	public Page<WorkflowTask> getDelegateTasksByActive(Long companyId, String loginName,Long userId, Page<WorkflowTask> page, boolean isEnd){
		return taskManager.getDelegateTasksByActive(companyId, loginName,userId, page, isEnd);
	}
	
	public Page<WorkflowTask> getTaskAsTrustee(Long companyId, String loginName,Long userId, Page<WorkflowTask> page, boolean isEnd){
		return taskManager.getTaskAsTrustee(companyId, loginName,userId, page, isEnd);
	}
	
	public Integer getDelegateTasksNum(Long companyId, String loginName,Long userId){
		return taskManager.getDelegateTasksNum(companyId, loginName,userId);
	}
	
	public Integer getTrusteeTasksNum(Long companyId, String loginName,Long userId, Boolean isCompleted){
		return taskManager.getTrusteeTasksNum(companyId, loginName,userId,isCompleted);
	}
	
	public Integer getDelegateTasksNumByActive(Long companyId, String loginName,Long userId, Boolean isCompleted){
		return taskManager.getDelegateTasksNumByActive(companyId, loginName,userId, isCompleted);
	}

	public List<WorkflowTask> getAllTasksByInstance(Long companyId, String instanceId){
		return taskManager.getAllTasksByInstance(companyId, instanceId);
	}
	public void deleteTask(WorkflowTask task) {
		taskManager.deleteTask(task);
		
	}

	public void deleteTasksByName(Long companyId, String instanceId, String[] taskName) {
		taskManager.deleteTasksByName(companyId, instanceId, taskName);
	}

	public List<WorkflowTask> getTasksByName(Long companyId, String instanceId, String taskName) {
		return taskManager.getTasksByName(companyId, instanceId, taskName);
	}

	public List<WorkflowTask> getNoAssignTasksByName(Long companyId, String instanceId, String taskName,Integer groupNum) {
		return taskManager.getNoAssignTasksByName(companyId, instanceId, taskName,groupNum);
	}

	public void saveTasks(List<WorkflowTask> workflowTasks){
		taskManager.saveTasks(workflowTasks);
	}
	
	public WorkflowTask getFirstTaskByInstance(Long companyId, String instanceId, String transactor,Long userId) {
		return taskManager.getFirstTaskByInstance(companyId, instanceId, transactor,userId);
	}
	public List<WorkflowTask> getWorkflowTasks(String instanceId, String taskName) {
		return taskManager.getWorkflowTasks(instanceId, taskName);
	}

	public WorkflowTask getTask(Long id) {
		return taskManager.getTask(id);
	}

	public void deleteTaskByProcessId(String processId,Long companyId){
		taskManager.deleteTaskByProcessId(processId,companyId);
	}

	public void endTasks(String instanceId, Long companyId) {
		taskManager.endTasks(instanceId, companyId);
	}
	
	public void compelEndTasks(String instanceId, Long companyId) {
		taskManager.compelEndTasks(instanceId, companyId);
	}

	public List<WorkflowTask> getActivityTasks(String instanceId, Long companyId) {
		return taskManager.getActivityTasks(instanceId, companyId);
	}
	
	public List<WorkflowTask> getActivitySignTasks(String instanceId, Long companyId) {
		return taskManager.getActivitySignTasks(instanceId, companyId);
	}

	public WorkflowTask getMyTask(String instanceId,Long companyId,String loginName){
	    	return taskManager.getMyTask(instanceId, companyId, loginName);
	}
	public WorkflowTask getMyTask(String instanceId,Long companyId,Long userId){
		return taskManager.getMyTask(instanceId, companyId, userId);
	}
	
	public List<WorkflowTask> getTasksByActivity(Long companyId,
			String executionId, String taskName) {
		return taskManager.getTasksByActivity(companyId, executionId, taskName);
	}
	
	public List<String> getParticipantsTransactor(Long companyId, String instanceId){
		return taskManager.getParticipantsTransactor(companyId, instanceId);
	}
	public List<Long> getParticipantsTransactorId(Long companyId, String instanceId){
		return taskManager.getParticipantsTransactorId(companyId, instanceId);
	}

	public List<String> getCountersignByProcessInstanceId(
			String processInstanceId, TaskProcessingMode processingMode) {
		return taskManager.getCountersignByProcessInstanceId(processInstanceId, processingMode);
	}
	
	/**
	 * 自定义流程中取会签环节名称
	 */
	public List<String> getSignByProcessInstanceId(
			String processInstanceId, TaskProcessingMode processingMode) {
		return taskManager.getSignByProcessInstanceId(processInstanceId, processingMode);
	}

	public List<WorkflowTask> getCountersignByProcessInstanceIdResult(
			String processInstanceId, String taskName, TaskProcessingResult result) {
		return taskManager.getCountersignByProcessInstanceIdResult(processInstanceId, taskName, result);
	}

	public void deleteWorkflowTask(List<Long> ids) {
		taskManager.deleteWorkflowTask(ids);		
	}

	public List<WorkflowTask> getCountersigns(Long id) {
		return taskManager.getCountersigns(id);
	}
	public List<String> getCountersignsHandler(Long id,Integer handlingState){
		return taskManager.getCountersignsHandler(id,handlingState);
	}

	public void deleteCountersignHandler(Long taskId, Collection<String> users) {
		taskManager.deleteCountersignHandler(taskId,users);
	}
	
	public void deleteSignHandler(Long taskId, Collection<Long> userIds) {
		taskManager.deleteSignHandler(taskId, userIds);
	}

	public String receive(Long taskId) {
		return taskManager.receive(taskId);
	}
	
	public String abandonReceive(Long taskId) {
		return taskManager.abandonReceive(taskId);
	}

	public Set<String> getHandledTransactors(String workflowId) {
		return taskManager.getHandledTransactors(workflowId);
	}

	/**
	 * 得到所有需要催办的task
	 */
	public List<WorkflowTask> getNeedReminderTasks(){
		return taskManager.getNeedReminderTasks();
	}

	public List<WorkflowTask> getProcessCountersigns(Long id) {
		return taskManager.getProcessCountersigns(id);
	}

	public List<WorkflowTask> getCompletedTasks(String workflowId,
			Long companyId) {
		return taskManager.getCompletedTasks( workflowId,
				 companyId);
	}

	public List<WorkflowTask> getTasksOrderByWdfName(String definitionName,
			String loginName) {
		return taskManager.getTasksOrderByWdfName(definitionName, loginName);
	}
	public List<WorkflowTask> getTasksOrderByWdfName(String definitionName,
			Long userId) {
		return taskManager.getTasksOrderByWdfName(definitionName, userId);
	}

	public List<WorkflowTask> getCompletedTasksByTaskName(String workflowId,
			Long companyId, String taskName) {
		return taskManager.getCompletedTasksByTaskName(workflowId, companyId, taskName);
	}
	/**
	 * 根据当前用户查询未完成任务总数
	 * @param companyId 公司id
	 * @param loginName 当前用户登录名
	 * @return 未完成任务总数
	 */
	public Integer getTasksNumByTransactor(Long companyId, String loginName){
		return taskManager.getTasksNumByTransactor(companyId, loginName);
	}
	/**
	 * 根据当前用户查询未完成任务总数
	 * @param companyId 公司id
	 * @param loginName 当前用户登录名
	 * @return 未完成任务总数
	 */
	public Integer getTasksNumByTransactor(Long companyId, Long userId){
		return taskManager.getTasksNumByTransactor(companyId, userId);
	}
	/**
	 * 查找公司中所有的超期任务
	 * @param companyId
	 * @return
	 */
	public List<WorkflowTask> getOverdueTasks(Long companyId) {
		return taskManager.getOverdueTasks(companyId);
	}
	
	/**
	 * 查找当前办理人所有的超期任务的总数
	 * @param companyId
	 * @param transactorName
	 * @return map :key为办理人登录名，value为超期次数
	 */
	public Map<String,Integer> getOverdueTasksNumByTransactor(Long companyId) {
		return taskManager.getOverdueTasksNumByTransactor(companyId);
	}
	/**
	 * 查找公司中所有的超期任务,包括已完成的任务
	 * @param companyId
	 * @return
	 */
	public List<WorkflowTask> getTotalOverdueTasks(Long companyId){
		return taskManager.getTotalOverdueTasks(companyId);
	}
	/**
	 * 查找当前办理人所有的超期任务的总数,包括已完成的任务
	 * @param companyId
	 * @param transactorName
	 * @return
	 */
	public Map<String,Integer> getTotalOverdueTasksNumByTransactor(Long companyId){
		return taskManager.getTotalOverdueTasksNumByTransactor(companyId);
	}
	/**
	 * 查找当前办理人所有的超期任务的总数,包括已完成的任务
	 * @param companyId
	 * @return
	 */
	public Map<Long,Integer> getTotalOverdueTasksNumByTransactorId(Long companyId){
		return taskManager.getTotalOverdueTasksNumByTransactorId(companyId);
	}

	public Set<String> getAllHandleTransactors(String workflowId) {
		return taskManager.getAllHandleTransactors(workflowId);
	}

	public void getAllTasksByUser(Long companyId, String loginName,
			Page<WorkflowTask> page) {
		taskManager.getAllTasksByUser(companyId, loginName, page);
	}
	public void getAllTasksByUser(Long companyId, Long userId,
			Page<WorkflowTask> page) {
		taskManager.getAllTasksByUser(companyId, userId, page);
	}

	public List<WorkflowTask> getAllTasksByUser(Long companyId, String loginName) {
		return taskManager.getAllTasksByUser(companyId, loginName);
	}
	public List<WorkflowTask> getAllTasksByUser(Long companyId, Long userId) {
		return taskManager.getAllTasksByUser(companyId, userId);
	}
	public List<String> getTransactorsExceptTask(Long taskId) {
		return taskManager.getTransactorsExceptTask(taskId);
	}
	public List<WorkflowTask> getTaskOrderByGroupNum(Long companyId,
			String instanceId, String taskName) {
		return taskManager.getTaskOrderByGroupNum(companyId, instanceId, taskName);
	}

	public List<WorkflowTask> getActivityTasksByName(String instanceId,
			Long companyId, String taskName) {
		return taskManager.getActivityTasksByName(instanceId, companyId, taskName);
	}

	public List<String[]> getActivityTaskTransactors(String instanceId,
			Long companyId) {
		return taskManager.getActivityTaskTransactors(instanceId,companyId);
	}

	public List<String> getActivityTaskPrincipals(String instanceId,
			Long companyId) {
		return taskManager.getActivityTaskPrincipals(instanceId,companyId);
	}

	public List<String> getCompletedTaskNames(String workflowId, Long companyId) {
		return taskManager.getCompletedTaskNames(workflowId, companyId);
	}

	public void continueTasks(String instanceId, Long companyId) {
		taskManager.continueTasks(instanceId, companyId);
		
	}

	public void pauseTasks(String instanceId, Long companyId) {
		taskManager.pauseTasks(instanceId, companyId);
		
	}

	public void getActivityTasksByTransactorName(Page<WorkflowTask> tasks,
			Long typeId, String defCode, Long wfdId) {
		taskManager.getActivityTasksByTransactorName(tasks,  typeId, defCode, wfdId);
		
	}

	public List<WorkflowTask> getTasksByInstance(List<String> instanceIds,
			String taskName, String recieveUser,Long recieveId, String consignor,Long consignorId,
			Long companyId) {
		return taskManager.getTasksByInstance(instanceIds, taskName, recieveUser,recieveId, consignor,consignorId, companyId);
	}

	public List<String> getActiveTaskNameWithoutSpecial(String instanceId) {
		return taskManager.getActiveTaskNameWithoutSpecial(instanceId);
	}

	@Deprecated
	public void assign(Long taskId, String transactor) {
		taskManager.assign(taskId, transactor);
	}
	public void assign(Long taskId, Long transactorId) {
		taskManager.assign(taskId, transactorId);
	}
	public List<Integer> getGroupNumByTaskName(String processInstanceId,String taskName){
		return taskManager.getGroupNumByTaskName(processInstanceId, taskName);
	}
	

	public String getTaskUrl(Task task) {
		return taskManager.getTaskUrl(task);
	}
	
	public WorkflowTask getLastCompletedTaskByTaskName(String workflowId,
			Long companyId, String taskName) {
		return taskManager.getLastCompletedTaskByTaskName(workflowId, companyId, taskName);
	}

	public List<String[]> getActivityTaskPrincipalsDetail(String instanceId,
			Long companyId) {
		return taskManager.getActivityTaskPrincipalsDetail(instanceId, companyId);
	}

	public List<WorkflowTask> getActivityTasksByNameWithout(String workflowId,
			Long taskId, String taskName) {
		return taskManager.getActivityTasksByNameWithout(workflowId, taskId, taskName);
	}
	public List<WorkflowTask> getActivityTrustorTasksByTransactor(
			String workflowId, String transactor,Long userId,Long taskId) {
		return taskManager.getActivityTrustorTasksByTransactor(workflowId, transactor,userId,taskId);
	}

	public String getTaskUrl(String taskUrl, Long taskId) {
		return taskManager.getTaskUrl(taskUrl, taskId);
	}

	public void assignTransactorSet(String transactors,Long taskId) {
		taskManager.assignTransactorSet(transactors, taskId);
	}

	public List<WorkflowTask> getTaskByTransactor(String transactor,
			String workflowId) {
		return taskManager.getTaskByTransactor(transactor,workflowId);
	}
	public List<WorkflowTask> getTaskByTransactor(Long userId,
			String workflowId) {
		return taskManager.getTaskByTransactor(userId, workflowId);
	}
	
	public List<HistoryWorkflowTask> getHistoryTaskByTransactor(String transactor,
			String workflowId) {
		return taskManager.getHistoryTaskByTransactor(transactor, workflowId);
	}
	public List<HistoryWorkflowTask> getHistoryTaskByTransactor(Long userId,
			String workflowId) {
		return taskManager.getHistoryTaskByTransactor(userId, workflowId);
	}
	public Set<Long> getHandledTransactorIds(String workflowId){
		return taskManager.getHandledTransactorIds(workflowId);
	}
	public Set<Long> getAllHandleTransactorIds(String workflowId){
		return taskManager.getAllHandleTransactorIds(workflowId);
	}

	public List<WorkflowTask> getActivityPrincipalTask(String instanceId,
			Long companyId) {
		return taskManager.getActivityPrincipalTask(instanceId, companyId);
	}

	public List<WorkflowTask> getActivityTaskByInstance(String instanceId,
			Long companyId) {
		return taskManager.getActivityTaskByInstance(instanceId, companyId);
	}

	public List<Long> getActivityTaskPrincipalIds(String instanceId,
			Long companyId) {
		return taskManager.getActivityTaskPrincipalIds(instanceId, companyId);
	}

	public List<Long> getTransactorIdsExceptTask(Long taskId) {
		return taskManager.getTransactorIdsExceptTask(taskId);
	}
	
	public Set<String> getAllTaskTransactors(String workflowId) {
		return taskManager.getAllTaskTransactors(workflowId); 
	}

	public Set<String> getAllTaskTrustors(String workflowId) {
		return taskManager.getAllTaskTrustors(workflowId); 
	}

	public Set<String> getTransactorsByName(String workflowId, String taskName) {
		return taskManager.getTransactorsByName(workflowId, taskName);
	}
	
	public Set<Long> getAllTaskTransactorIds(String workflowId) {
		return taskManager.getAllTaskTransactorIds(workflowId); 
	}

	public Set<Long> getAllTaskTrustorIds(String workflowId) {
		return taskManager.getAllTaskTrustorIds(workflowId); 
	}

	public Set<Long> getTransactorIdsByName(String workflowId, String taskName) {
		return taskManager.getTransactorIdsByName(workflowId, taskName);
	}

}
