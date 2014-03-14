package com.norteksoft.task.service;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import com.norteksoft.product.orm.Page;
import com.norteksoft.product.util.ContextUtils;
import com.norteksoft.task.base.enumeration.TaskCategory;
import com.norteksoft.task.base.enumeration.TaskProcessingMode;
import com.norteksoft.task.base.enumeration.TaskProcessingResult;
import com.norteksoft.task.dao.HistoryWorkflowTaskDao;
import com.norteksoft.task.entity.HistoryWorkflowTask;
import com.norteksoft.task.entity.TaskMark;

@Service
@Transactional
public class HistoryWorkflowTaskManager {
	private Log log = LogFactory.getLog(HistoryWorkflowTaskManager.class);
	@Autowired
	private HistoryWorkflowTaskDao historyWorkflowTaskDao;
	
	public HistoryWorkflowTask getHistoryWorkflowTask(Long id) {
		return historyWorkflowTaskDao.get(id);
	}

	/**
	 * 获得所有流程名称
	 * @param taskCategory
	 * @return
	 */
	public List<Object[]> getGroupNames(String taskCategory) {
		if(TaskCategory.COMPLETE.equals(taskCategory)){
			return historyWorkflowTaskDao.getAllCompleteTaskGroupNames();
		}else {
			return historyWorkflowTaskDao.getAllCancelTaskGroupNames();
		}
	}

	/**
	 * 获得所有流程自定义类别
	 * @param taskCategory
	 * @return
	 */
	public List<Object[]> getCustomTypes(String taskCategory){
		if(TaskCategory.COMPLETE.equals(taskCategory)){
			return historyWorkflowTaskDao.getAllCompleteTaskCustomTypes();
		}else {
			return historyWorkflowTaskDao.getAllCancelTaskCustomTypes();
		}
	}

	/**
	 * 获得所有任务类型
	 * @param taskCategory
	 * @return
	 */
	public List<Object[]> getTypeInfos(String taskCategory) {
		if(TaskCategory.COMPLETE.equals(taskCategory)){
			return historyWorkflowTaskDao.getAllCompleteTaskTypeInfos();
		}else {
			return historyWorkflowTaskDao.getAllCancelTaskTypeInfos();
		}
	}

	/**
	 * 根据办理人获得任务数目
	 * @param taskCategory
	 * @return
	 */
	public Integer getAllTaskNumByUser(String taskCategory) {
		if(TaskCategory.COMPLETE.equals(taskCategory)){
			return historyWorkflowTaskDao.getAllCompleteTasksNum();
		}else {
			return historyWorkflowTaskDao.getAllCancelTasksNum();
		}
	}

	/**
	 * 根据流程名称分页查询用户所有已完成任务
	 * @param page
	 * @param typeName
	 */
	public void getCompletedTasksByGroupName(Page<HistoryWorkflowTask> page,String typeName) {
		historyWorkflowTaskDao.getCompletedTasksByGroupName(page,typeName);
	}

	/**
	 * 根据自定义类型分页查询用户所有已完成任务
	 * @param page
	 * @param typeName
	 */
	public void getCompletedTasksByCustomType(Page<HistoryWorkflowTask> page,String typeName) {
		historyWorkflowTaskDao.getCompletedTasksByCustomType(page,typeName);
	}

	/**
	 * 分页查询用户已完成任务
	 * @param page
	 * @param typeName
	 */
	public void getCompletedTasksByUserType(Page<HistoryWorkflowTask> page,String typeName) {
		historyWorkflowTaskDao.getCompletedTasksByUserType(page,typeName);
	}

	/**
	 * 根据流程名称分页查询用户所有已取消任务
	 * @param page
	 * @param typeName
	 */
	public void getCancelTasksByGroupName(Page<HistoryWorkflowTask> page,String typeName) {
		historyWorkflowTaskDao.getCancelTasksByGroupName(page,typeName);
	}

	/**
	 * 根据自定义类型分页查询用户所有已取消任务
	 * @param page
	 * @param typeName
	 */
	public void getCancelTasksByCustomType(Page<HistoryWorkflowTask> page,String typeName) {
		historyWorkflowTaskDao.getCancelTasksByCustomType(page,typeName);
	}

	/**
	 * 分页查询用户已取消任务
	 * @param page
	 * @param typeName
	 */
	public void getCanceledTasksByUserType(Page<HistoryWorkflowTask> page,String typeName) {
		historyWorkflowTaskDao.getCanceledTasksByUserType(page,typeName);
	}

	/**
	 * 改变任务标识
	 * @param taskId
	 * @param taskMarks
	 */
	public void changeTaskMark(long taskId, TaskMark taskMark) {
		HistoryWorkflowTask task = getHistoryWorkflowTask(taskId);
		switch(taskMark) {
	       case RED:
	    	   task.setTaskMark(TaskMark.RED);
	    	   break;
	       case BLUE:
	    	   task.setTaskMark(TaskMark.BLUE);
	          break;
	       case YELLOW:
	    	   task.setTaskMark(TaskMark.YELLOW);
	          break;
	       case GREEN:
	    	   task.setTaskMark(TaskMark.GREEN);
	           break;
	       case ORANGE:
	    	   task.setTaskMark(TaskMark.ORANGE);
	         break;
	       case PURPLE:
	    	   task.setTaskMark(TaskMark.PURPLE);
	          break;
	       case CANCEL:
	    	   task.setTaskMark(TaskMark.CANCEL);
	          break;
	       default:    
	       }
		historyWorkflowTaskDao.save(task);
	}
	
	public HistoryWorkflowTask getTask(Long taskId){
		return historyWorkflowTaskDao.getTask(taskId);
	}
	public HistoryWorkflowTask getTaskBySourceTaskId(Long taskId){
		return historyWorkflowTaskDao.getTaskBySourceTaskId(taskId);
	}
	public List<String> getCountersignByProcessInstanceId(String processInstanceId,TaskProcessingMode processingMode){
		return historyWorkflowTaskDao.getCountersignByProcessInstanceId(processInstanceId, processingMode);
	}
	
	/**
	 * 根据办理结果查询环节
	 */
	public List<HistoryWorkflowTask> getCountersignByProcessInstanceIdResult(String processInstanceId,String taskName,TaskProcessingResult result){
		return historyWorkflowTaskDao.getCountersignByProcessInstanceIdResult(processInstanceId, taskName, result);
	}
	
	/**
	 * 获得审批任务组数
	 * @param processInstanceId
	 * @param taskName
	 * @param result
	 * @return
	 */
	public List<Integer> getGroupNumByTaskName(String processInstanceId,String taskName){
		return historyWorkflowTaskDao.getGroupNumByTaskName(processInstanceId, taskName);
	}
	
	/**
	 *  根据实力id删除historyTask
	 * @param taskId
	 * @param taskMarks
	 */
	public void deleteHistoryTaskByProcessId(String processInstanceId, Long companyId) {
		historyWorkflowTaskDao.deleteHistoryTaskByProcessId(processInstanceId, companyId);
	}

	/**
	 * 保存历史任务
	 * @param historyWorkflowTask
	 */
	public void saveHistoryWorkflowTask(HistoryWorkflowTask historyWorkflowTask) {
		historyWorkflowTaskDao.save(historyWorkflowTask);
	}
	
	public Page<HistoryWorkflowTask> getTaskAsTrustee(Long companyId,
			Page<HistoryWorkflowTask> tasks, String loginName,Long userId, Boolean isEnd) {
		return historyWorkflowTaskDao.getTaskAsTrustee(companyId, loginName,userId, tasks, isEnd);
	}
	/**
	 * 根据流程名字和实例id查询workflowTask
	 * @param instanceId
	 * @param taskName
	 * @return
	 */
	public List<HistoryWorkflowTask> getWorkflowTasks(String instanceId, String taskName) {
		return historyWorkflowTaskDao.getWorkflowTasks(instanceId, taskName);
	}

	public Page<HistoryWorkflowTask> getHistoryDelegateTasksByActive(
			Long companyId, String loginName,Long userId,
			Page<HistoryWorkflowTask> historyTasks) {
		return historyWorkflowTaskDao.getHistoryDelegateTasksByActive(companyId, loginName,userId, historyTasks);
	}
	
	/**
	 * 查找公司中所有的超期任务,包括已完成的任务
	 * @param companyId
	 * @return
	 */
	public List<HistoryWorkflowTask> getTotalOverdueTasks() {
		Assert.notNull(ContextUtils.getCompanyId(),"公司id不能为null");
		return historyWorkflowTaskDao.getTotalOverdueTasks(ContextUtils.getCompanyId());
	}
	
	public List<String> getTransactorsExceptTask(Long taskId) {
		if(taskId==null)return null;
		HistoryWorkflowTask task=getTask(taskId);
		return historyWorkflowTaskDao.getTransactorsExceptTask(task);
	}
	
	public Set<String> getHandledTransactors(String instanceId){
		Assert.notNull(instanceId,"流程实例Id不能为null");
		return new HashSet<String>(historyWorkflowTaskDao.getHandledTransactors(instanceId));
	}
	
	public List<HistoryWorkflowTask> getTaskByTransactor(String transactor,String workflowId){
		return historyWorkflowTaskDao.getTaskByTransactor(transactor, workflowId);
	}
	public List<HistoryWorkflowTask> getTaskByTransactor(Long userId,String workflowId){
		return historyWorkflowTaskDao.getTaskByTransactor(userId, workflowId);
	}
	
}
