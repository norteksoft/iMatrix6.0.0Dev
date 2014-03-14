package com.norteksoft.task.service;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jbpm.api.ProcessEngine;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import rtx.RtxMsgSender;

import com.norteksoft.acs.service.AcsUtils;
import com.norteksoft.acs.service.authorization.AcsApiManager;
import com.norteksoft.acs.service.organization.UserManager;
import com.norteksoft.product.api.ApiFactory;
import com.norteksoft.product.api.entity.User;
import com.norteksoft.product.orm.Page;
import com.norteksoft.product.util.AsyncMailUtils;
import com.norteksoft.product.util.ContextUtils;
import com.norteksoft.product.util.PropUtils;
import com.norteksoft.product.util.SystemUrls;
import com.norteksoft.task.base.enumeration.TaskCategory;
import com.norteksoft.task.base.enumeration.TaskProcessingMode;
import com.norteksoft.task.base.enumeration.TaskProcessingResult;
import com.norteksoft.task.base.enumeration.TaskState;
import com.norteksoft.task.dao.HistoryWorkflowTaskDao;
import com.norteksoft.task.dao.TaskDao;
import com.norteksoft.task.dao.WorkflowTaskDao;
import com.norteksoft.task.entity.HistoryWorkflowTask;
import com.norteksoft.task.entity.Task;
import com.norteksoft.task.entity.WorkflowTask;
import com.norteksoft.task.webservice.WorkflowTaskService;
import com.norteksoft.wf.base.enumeration.InstanceHistoryType;
import com.norteksoft.wf.base.utils.WebUtil;
import com.norteksoft.wf.engine.core.DefinitionXmlParse;
import com.norteksoft.wf.engine.entity.InstanceHistory;
import com.norteksoft.wf.engine.entity.WorkflowDefinition;
import com.norteksoft.wf.engine.entity.WorkflowInstance;
import com.norteksoft.wf.engine.entity.WorkflowType;
import com.norteksoft.wf.engine.service.DelegateMainManager;
import com.norteksoft.wf.engine.service.InstanceHistoryManager;
import com.norteksoft.wf.engine.service.WorkflowDefinitionManager;
import com.norteksoft.wf.engine.service.WorkflowInstanceManager;
import com.norteksoft.wf.engine.service.WorkflowTypeManager;

//@WebService(endpointInterface = "com.norteksoft.task.webservice.WorkflowTaskService")
@Service
@Transactional
public class WorkflowTaskManager implements WorkflowTaskService{
	private Log log = LogFactory.getLog(WorkflowTaskManager.class);
	private SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	//流转历史常量
	private static final String COMMA = ", ";
	private static final String DELTA_START = "[ ";
	private static final String DELTA_END = " ]";
	private WorkflowTaskDao workflowTaskDao;
	private TaskDao taskDao;
	private UserManager userManager;
	private ProcessEngine processEngine;
	private DelegateMainManager delegateManager;
	private WorkflowDefinitionManager workflowDefinitionManager;
	private InstanceHistoryManager instanceHistoryManager;
	private HistoryWorkflowTaskDao historyWorkflowTaskDao;
	
	
	@Autowired
	public void setInstanceHistoryManager(
			InstanceHistoryManager instanceHistoryManager) {
		this.instanceHistoryManager = instanceHistoryManager;
	}
	@Autowired
	private WorkflowInstanceManager workflowInstanceManager;
	@Autowired
	private WorkflowTypeManager workflowTypeManager;
	@Autowired
	public void setUserManager(UserManager userManager) {
		this.userManager = userManager;
	}
	@Autowired
	public void setWorkflowTaskDao(WorkflowTaskDao workflowTaskDao) {
		this.workflowTaskDao = workflowTaskDao;
	}
	@Autowired
	public void setTaskDao(TaskDao taskDao) {
		this.taskDao = taskDao;
	}
	@Autowired
	public void setProcessEngine(ProcessEngine processEngine) {
		this.processEngine = processEngine;
	}
	@Autowired
	public void setDelegateManager(DelegateMainManager delegateManager) {
		this.delegateManager = delegateManager;
	}
	@Autowired
	public void setWorkflowDefinitionManager(
			WorkflowDefinitionManager workflowDefinitionManager) {
		this.workflowDefinitionManager = workflowDefinitionManager;
	}
	@Autowired
	public void setHistoryWorkflowTaskDao(
			HistoryWorkflowTaskDao historyWorkflowTaskDao) {
		this.historyWorkflowTaskDao = historyWorkflowTaskDao;
	}
	public Page<WorkflowTask> getDelegateTasks(
			Long companyId, String loginName,Long userId, Page<WorkflowTask> page){
		return workflowTaskDao.getDelegateTasks(companyId, loginName,userId, page);
	}
	
	public Page<WorkflowTask> getDelegateTasksByActive(
			Long companyId, String loginName,Long userId, Page<WorkflowTask> page, boolean isEnd){
		return workflowTaskDao.getDelegateTasks(companyId, loginName,userId, page, isEnd);
	}
	
	public Page<WorkflowTask> getTaskAsTrustee(
			Long companyId, String loginName,Long userId, Page<WorkflowTask> page, boolean isEnd){
		return workflowTaskDao.getTaskAsTrustee(companyId, loginName,userId, page, isEnd);
	}

	public List<WorkflowTask> getAllTasksByInstance(Long companyId, String instanceId){
		return workflowTaskDao.getAllTasksByInstance(companyId, instanceId);
	}
	
	public Integer getDelegateTasksNum(Long companyId, String loginName,Long userId){
		return workflowTaskDao.getDelegateTasksNum(companyId, loginName,userId);
	}
	
	public Integer getDelegateTasksNumByActive(Long companyId, String loginName,Long userId, Boolean isCompleted){
		if(isCompleted){
			Integer currentNum =  workflowTaskDao.getDelegateTasksNum(companyId, loginName,userId, isCompleted);
			Integer histNum = historyWorkflowTaskDao.getDelegateTasksNum(companyId, loginName,userId);
			if(currentNum == null)currentNum = 0;
			if(histNum == null)histNum = 0;
			return currentNum+histNum;
		}else{
			return  workflowTaskDao.getDelegateTasksNum(companyId, loginName,userId, isCompleted);
		}
	}
	
	public Integer getTrusteeTasksNum(Long companyId, String loginName,Long userId, Boolean isCompleted){
		if(isCompleted){
			Integer currentNum = workflowTaskDao.getTrusteeTasksNum(companyId, loginName,userId, isCompleted);
			Integer histNum = historyWorkflowTaskDao.getTrusteeTasksNum(companyId, loginName,userId);
			if(currentNum == null)currentNum = 0;
			if(histNum == null)histNum = 0;
			return currentNum+histNum;
		}else{
			return  workflowTaskDao.getTrusteeTasksNum(companyId, loginName,userId, isCompleted);
		}
		
	}
	
	/**
     * 流程被手动结束时，强制结束流程实例的当前任务
     */
	@Transactional(readOnly=false)
    public void endTasks(String instanceId,Long companyId){
    	log.debug("*** endTasks 方法开始");
		log.debug(new StringBuilder("*** Received parameter:[")
				.append("instanceId:").append(instanceId)
				.append(", companyId:").append(companyId)
				.append("]").toString());
    	
    	List<WorkflowTask> tasks = getActivityTasks(instanceId,companyId);
    	for(WorkflowTask task:tasks){
    		task.setActive(TaskState.CANCELLED.getIndex());
    		saveTask(task);
    	}
    	
    	log.debug("*** endTasks 方法结束");
    }
	/**
	 * 流程被强制结束时，强制结束流程实例的当前任务
	 */
	@Transactional(readOnly=false)
	public void compelEndTasks(String instanceId,Long companyId){
		log.debug("*** endTasks 方法开始");
		log.debug(new StringBuilder("*** Received parameter:[")
		.append("instanceId:").append(instanceId)
		.append(", companyId:").append(companyId)
		.append("]").toString());
		
		List<WorkflowTask> tasks = getActivityTasks(instanceId,companyId);
		for(WorkflowTask task:tasks){
			task.setActive(TaskState.COMPLETED.getIndex());
			saveTask(task);
		}
		
		log.debug("*** endTasks 方法结束");
	}
    
    /**
     * 活动该流程实例的当前任务
     */
    public List<WorkflowTask> getActivityTasks(String instanceId,Long companyId){
    	log.debug("*** getActivityTasks 方法开始");
		log.debug(new StringBuilder("*** Received parameter:[")
				.append("instanceId:").append(instanceId)
				.append(", companyId:").append(companyId)
				.append("]").toString());
		
    	List<WorkflowTask> tasks = workflowTaskDao.getActivityTasks(instanceId,companyId);
    	
    	log.debug("*** getActivityTasks 方法结束");
    	return tasks;
    }
    
    /**
     * 活动该流程实例的当前任务
     */
    public List<WorkflowTask> getActivitySignTasks(String instanceId,Long companyId){
    	log.debug("*** getActivityTasks 方法开始");
		log.debug(new StringBuilder("*** Received parameter:[")
				.append("instanceId:").append(instanceId)
				.append(", companyId:").append(companyId)
				.append("]").toString());
		
    	List<WorkflowTask> tasks = workflowTaskDao.getActivitySignTasks(instanceId,companyId);
    	
    	log.debug("*** getActivityTasks 方法结束");
    	return tasks;
    }
    
    /**
     * 查询办理人的当前任务
     * @param instanceId
     * @param companyId
     * @param loginName
     * @return
     */
    public WorkflowTask getMyTask(String instanceId,Long companyId,String loginName){
    	log.debug("*** getMyTask 方法开始");
		log.debug(new StringBuilder("*** Received parameter:[")
				.append("instanceId:").append(instanceId)
				.append(", companyId:").append(companyId)
				.append(", loginName:").append(loginName)
				.append("]").toString());
		
    	WorkflowTask task = workflowTaskDao.getMyTask(instanceId, companyId, loginName);
    	
    	log.debug("*** getMyTask 方法结束");
    	return task;
    }
    /**
     * 查询办理人的当前任务
     * @param instanceId
     * @param companyId
     * @param loginName
     * @return
     */
    public WorkflowTask getMyTask(String instanceId,Long companyId,Long userId){
    	log.debug("*** getMyTask 方法开始");
    	log.debug(new StringBuilder("*** Received parameter:[")
    	.append("instanceId:").append(instanceId)
    	.append(", companyId:").append(companyId)
    	.append(", userId:").append(userId)
    	.append("]").toString());
    	
    	WorkflowTask task = workflowTaskDao.getMyTask(instanceId, companyId, userId);
    	
    	log.debug("*** getMyTask 方法结束");
    	return task;
    }
    
	/**
	 * 删除该流程实例中的所有任务。
	 * @param processInstanceId
	 * @param companyId
	 */
    @Transactional(readOnly=false)
	public void deleteTaskByProcessId(String processInstanceId,Long companyId) {
		workflowTaskDao.deleteTaskByProcessId(processInstanceId,companyId);
	}
	
	/**
	 * 保存工作流任务
	 * @param wfTask
	 */
    @Transactional(readOnly=false)
	public void saveTask(WorkflowTask wfTask){
		workflowTaskDao.save(wfTask);
		if(wfTask.getSendingMessage() && ApiFactory.getAcsService().isRtxEnable()) {
			String url=null;
			if(StringUtils.isEmpty(wfTask.getUrl()))return;
			if(wfTask.getUrl().contains("?")){
				url=SystemUrls.getSystemUrl(StringUtils.substringBefore(wfTask.getUrl(), "/"))+StringUtils.substring(wfTask.getUrl(), wfTask.getUrl().indexOf('/'))+wfTask.getId();
			}else{
				url=SystemUrls.getSystemUrl(StringUtils.substringBefore(wfTask.getUrl(), "/"))+StringUtils.substring(wfTask.getUrl(), wfTask.getUrl().indexOf('/'))+"?taskId="+wfTask.getId();
			}
			RtxMsgSender.sendNotify(wfTask.getTransactor(),
					"新任务-"+wfTask.getName(), 
					"1", 
					"你有一个新任务："+wfTask.getName(),
					url,getCompanyId());
		}
	}
	
	public Long getCompanyId(){
		return ContextUtils.getCompanyId();
	}
	
	/**
	 * 领取任务
	 * @param taskId
	 */
	@Transactional(readOnly=false)
	public String receive(String taskIds){
		String[] taskIdStr = taskIds.split(",");
		StringBuilder sb=new StringBuilder("");
		int successNum=0;
		int failNum=0;
		for(int i=0;i<taskIdStr.length;i++){
	    	log.debug("*** receive 方法开始");
			log.debug(new StringBuilder("*** Received parameter:[")
			.append("taskId:").append(taskIdStr[i])
			.append("]").toString());
	    	Long taskId = Long.parseLong(taskIdStr[i]);
			WorkflowTask task = workflowTaskDao.get(taskId);
			if(task.getActive().equals(TaskState.DRAW_WAIT.getIndex())){
				List<WorkflowTask> tasks = workflowTaskDao.getNotCompleteTasksByName(getCompanyId(), task.getProcessInstanceId(), task.getName());
				for(WorkflowTask tsk : tasks){
					if(taskId.equals(tsk.getId())){
						successNum++;
						task.setDrawTask(true);
						tsk.setActive(TaskState.WAIT_TRANSACT.getIndex());//待办理
					}else{
						tsk.setActive(TaskState.HAS_DRAW_OTHER.getIndex());//已领取
					}
				}
			}
		}
		failNum=taskIdStr.length-successNum;
		log.debug("*** receive 方法结束");
		sb.append("成功领取").append(successNum).append("个,");
		sb.append("操作失败").append(failNum).append("个.");
		return sb.toString();
	}
	/**
	 * 放弃领取的任务
	 * @return
	 */
	@Transactional(readOnly=false)
	public String abandonReceive(Long taskId){
		log.debug("*** abandonReceiveTask 方法开始");
		log.debug(new StringBuilder("*** Received parameter:[")
		.append("taskId:").append(taskId)
		.append("]").toString());
		WorkflowTask task = workflowTaskDao.get(taskId);
		if(task.getDrawTask()){
			task.setDrawTask(false);
			task.setActive(TaskState.DRAW_WAIT.getIndex());
		}
		String msg = "";
		List<WorkflowTask> tasks = workflowTaskDao.getHasDrawOtherTasks(getCompanyId(), task.getProcessInstanceId(), task.getName());
		for(WorkflowTask tsk : tasks){
			tsk.setActive(TaskState.DRAW_WAIT.getIndex());
		}
		msg = "task.abandon.receive.success";
		log.debug("*** abandonReceiveTask 方法结束");
		return msg;
	}
	
	@Transactional(readOnly=false)
	public void assigns(String taskIds,Long transactorId){
		if(StringUtils.isNotEmpty(taskIds)){
			User transactorUser = ApiFactory.getAcsService().getUserById(transactorId);
			if(transactorUser!=null){
				String[] taskIdStrs = taskIds.split(",");
				for(String idStr:taskIdStrs){
					assignTask(Long.parseLong(idStr), transactorUser);
					
					//指派任务的业务补偿
					assignTransactorSet(String.valueOf(transactorId), Long.parseLong(idStr));
				}
			}
		}
	}
	
	/**
	 * 指派任务给指定的人员
	 * @param taskId
	 * @param transactor
	 */
	@Deprecated
	@Transactional(readOnly=false)
	public void assign(Long taskId, String transactor){
		log.debug("*** assign 方法开始");
		log.debug(new StringBuilder("*** Received parameter:[")
		.append("taskId:").append(taskId)
		.append(", transactor:").append(transactor)
		.append("]").toString());
		
		//<s:if test="task.processingMode != '编辑式' && task.processingMode != '交办式' && task.active!=2" >
		User transactorUser = ApiFactory.getAcsService().getUserByLoginName(transactor);
		if(transactorUser!=null){
			assignTask(taskId,transactorUser);
		}
		log.debug("*** assign 方法结束");
	}
    /**
     * 指派任务给指定的人员
     * @param taskId
     * @param transactor
     */
	@Transactional(readOnly=false)
    public void assign(Long taskId, Long transactorId){
    	log.debug("*** assign 方法开始");
		log.debug(new StringBuilder("*** Received parameter:[")
		.append("taskId:").append(taskId)
		.append(", transactorId:").append(transactorId)
		.append("]").toString());
    	
		User transactorUser = ApiFactory.getAcsService().getUserById(transactorId);
    	//<s:if test="task.processingMode != '编辑式' && task.processingMode != '交办式' && task.active!=2" >
		assignTask(taskId,transactorUser);
    	
    	log.debug("*** assign 方法结束");
    }
	
	 public void assignTask(Long taskId, User transactorUser){
		WorkflowTask task = workflowTaskDao.get(taskId);
    	//被指派人的新任务
    	WorkflowTask targetTask = task.clone();
    	//设置指派人任务已完成
    	task.setTaskProcessingResult(null);
    	task.setActive(TaskState.ASSIGNED.getIndex());
    	task.setTransactDate(new Date());
    	task.setNextTasks("assign to:" + transactorUser.getId());
    	//新任务办理人
    	targetTask.setId(null);
    	targetTask.setAssignable(true);
    	//任务委托设置
    	WorkflowInstance wi = workflowInstanceManager.getWorkflowInstance(task.getProcessInstanceId());
    	String processId= "";
    	if(wi!=null) processId= wi.getProcessDefinitionId();
		User delegateUser = delegateManager.getDelegateUser(
				task.getCompanyId(), null,transactorUser.getId(), processId, task.getName());
		
		WorkflowDefinition wfDef = workflowDefinitionManager.getWorkflowDefinitionByProcessId(processId);
		//流程类型
		WorkflowType type=null;
		if(wfDef!=null){//修改了流程名称或类型后需要重新赋值
			targetTask.setGroupName(wfDef.getName());
			targetTask.setCustomType(wfDef.getCustomType());
			//流程类型
			type=workflowTypeManager.getWorkflowType(wfDef.getTypeId());
			if(type!=null)targetTask.setCategory(type.getName());
		}
		
		if(delegateUser != null){
			targetTask.setTrustor(transactorUser.getLoginName());
			targetTask.setTrustorId(transactorUser.getId());
			targetTask.setTrustorName(transactorUser.getName());
			targetTask.setTransactor(delegateUser.getLoginName());
			targetTask.setTransactorId(delegateUser.getId());
			targetTask.setTransactorName(delegateUser.getName());
			targetTask.setSubCompanyId(transactorUser.getSubCompanyId());
			targetTask.setSubCompanyName(transactorUser.getSubCompanyName());
		}else{
			targetTask.setTransactor(transactorUser.getLoginName());
			targetTask.setTransactorId(transactorUser.getId());
			targetTask.setTransactorName(transactorUser.getName());
			targetTask.setSubCompanyId(transactorUser.getSubCompanyId());
			targetTask.setSubCompanyName(transactorUser.getSubCompanyName());
		}
    	targetTask.setRead(false);
    	List<WorkflowTask> tasks = new ArrayList<WorkflowTask>();
    	tasks.add(task);
    	tasks.add(targetTask);
    	saveTasks(tasks);
    	
    	//发送消息
    	sendMessage(targetTask,type,"指派");
    	//发送邮件
    	sendMail(task,processId);
    	//生成流转历史
    	generateAssignHistory(task,transactorUser,targetTask,delegateUser);
	}
	
	public void assignTransactorSet(String transactors,Long taskId ){
		WorkflowTask task = workflowTaskDao.get(taskId);
		WorkflowInstance wi = workflowInstanceManager.getWorkflowInstance(task.getProcessInstanceId());
		assignTransactorSet(wi,transactors,taskId);
	}
	
	/**
	 * 指派任务的业务补偿
	 * @param instance
	 * @param form
	 */
	public void assignTransactorSet(WorkflowInstance instance,String transactors,Long taskId){
		Map<String,String> assignTransactorSet = DefinitionXmlParse.getAssignTransactorSet(instance.getProcessDefinitionId());
		String setType=assignTransactorSet.get(DefinitionXmlParse.SET_TYPE);
		String assignTransactorSetUrl=assignTransactorSet.get(DefinitionXmlParse.ASSIGN_TRANSACTOR);
		if(StringUtils.isNotEmpty(assignTransactorSetUrl)){
			String systemCode=WebUtil.getSystemCodeByDef(instance.getProcessDefinitionId());
			if(setType.equals("http")){
				WebUtil.getHttpConnectionTransactor(assignTransactorSetUrl,ContextUtils.getCompanyId(),instance.getDataId(),systemCode,transactors,taskId);
			}else if(setType.equals("RESTful")){
				WebUtil.restfulTransactor(assignTransactorSetUrl,ContextUtils.getCompanyId(),instance.getDataId(),systemCode,transactors,taskId);
			}
		}
	}
	
	/**
	 *生成流转历史
	 * @param task
	 * @param transactor
	 */
	private void generateAssignHistory(WorkflowTask task, User transactorUser,WorkflowTask delegateTask,User delegateUser) {
		StringBuilder historyMessage = new StringBuilder();
		//流转历史结构化
		String inforOne = "";
		String inforTwo = "";
		String inforThree = "";
		String inforFour = "";
		String inforFive = "";
		String inforSix = "";
		String inforSeven = "";
		String inforEight = "";
		String inforType = "";
		AcsApiManager acsApiManager = (AcsApiManager)ContextUtils.getBean("acsApiManager");
		boolean hasBranch = acsApiManager.hasBranch(ContextUtils.getCompanyId());
		User currentUser = ApiFactory.getAcsService().getUserById(ContextUtils.getUserId());
		String userName = currentUser.getName();
		if(hasBranch){
			userName = userName+"("+currentUser.getSubCompanyName()+")";
		}
		
		String transactorUserName = transactorUser.getName();
		if(hasBranch){
			transactorUserName = transactorUserName+"("+transactorUser.getSubCompanyName()+")";
		}
		if(delegateUser!=null){//生成指派和委托的流转历史
			User trustorUser = ApiFactory.getAcsService().getUserById(delegateTask.getTrustorId());
			inforOne = trustorUser.getName();
			inforTwo = trustorUser.getLoginName();
			inforThree = delegateTask.getTransactorName();
			inforFour = delegateTask.getTransactor();
			inforFive = ContextUtils.getUserName();
			inforSix = ContextUtils.getLoginName();
			inforSeven = transactorUser.getName();
			inforEight = transactorUser.getLoginName();
			inforType = "delegateAndAssgin";
			String trustorUserName = trustorUser.getName();
			if(hasBranch){
				trustorUserName = trustorUserName+"("+trustorUser.getSubCompanyName()+")";
			}
			
			String delegateTransactor = delegateTask.getTransactorName();
			if(hasBranch){
				delegateTransactor = delegateTransactor+"("+delegateUser.getSubCompanyName()+")";
			}
			
			historyMessage.append(trustorUserName)
			.append("已把任务委托给了")
			.append(delegateTransactor).append("。\n")
			.append(dateFormat.format(new Date()))
			.append(COMMA).append(userName).append("把当前任务指派给了")
			.append(transactorUserName).append(DELTA_START)
			.append(task.getName()).append(DELTA_END).append("\n");
		}else{//生成指派流转历史
			inforOne = ContextUtils.getUserName();
			inforTwo = ContextUtils.getLoginName();
			inforThree = transactorUser.getName();
			inforFour = transactorUser.getLoginName();
			historyMessage.append(dateFormat.format(new Date()))
			.append(COMMA).append(userName).append("把任务指派给了")
			.append(transactorUserName).append(DELTA_START)
			.append(task.getName()).append(DELTA_END).append("\n");
		}
		
		InstanceHistory history = new InstanceHistory(task.getCompanyId(), task.getProcessInstanceId(), InstanceHistory.TYPE_TASK, historyMessage.toString(), task.getName());
		history.setEffective(false);
		history.setCreatedTime(new Date());
		history.setExecutionId(task.getProcessInstanceId());
		
		if("delegateAndAssgin".equals(inforType)){//委托+指派
			history.setInforOne(inforOne);
			history.setInforTwo(inforTwo);
			history.setInforThree(inforThree);
			history.setInforFour(inforFour);
			history.setInforFive(inforFive);
			history.setInforSix(inforSix);
			history.setInforSeven(inforSeven);
			history.setInforEight(inforEight);
			history.setHistoryType(InstanceHistoryType.HISTORY_DELEGATE_AND_ASSIGN);
		}else{//指派
			history.setInforOne(inforOne);
			history.setInforTwo(inforTwo);
			history.setInforThree(inforThree);
			history.setInforFour(inforFour);
			history.setHistoryType(InstanceHistoryType.HISTORY_ASSIGN);
		}
		
		
        instanceHistoryManager.saveHistory(history);
		
	}
	/**
	 * 消息提醒
	 * @param task
	 * @param type
	 */
	private void sendMessage(WorkflowTask task,WorkflowType type,String customType){
		if(StringUtils.isNotEmpty(task.getTransactor())){//子流程时Transactor为null
			try {
				if(StringUtils.isNotEmpty(customType)){
					ApiFactory.getPortalService().addMessage("task", ContextUtils.getUserName(), ContextUtils.getUserId(), task.getTransactorId(),type==null?customType+":待办任务":customType+":"+type.getName(), task.getTitle(), "/task/message-task.htm?id="+task.getId());
				}else{
					ApiFactory.getPortalService().addMessage("task", ContextUtils.getUserName(), ContextUtils.getUserId(), task.getTransactorId(),type==null?"待办任务":type.getName(), task.getTitle(), "/task/message-task.htm?id="+task.getId());
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	private void sendMail(WorkflowTask task,String processId){
		
		 if(StringUtils.isNotEmpty(task.getTransactor())){//子流程时Transactor为null
			 try{
			        boolean isMailNotice=DefinitionXmlParse.isMailNotice(processId, task.getName());
			        if(isMailNotice){
			            String mailContent=PropUtils.getProp("mail.properties", "task.notice.content");
			            if(StringUtils.isNotEmpty(mailContent)){
			                mailContent=mailContent.replace("${url}", getTaskUrl(task));
			            }
		            	com.norteksoft.product.api.entity.User user=ApiFactory.getAcsService().getUserByLoginName(task.getTransactor());
		            	if(user==null){
		            		throw new RuntimeException("用户不存在："+task.getTransactor());
		            	}else if(StringUtils.isEmpty(user.getEmail())){
		            		throw new RuntimeException("用户邮件地址没有输入："+task.getTransactor());
		            	}else{
		            		AsyncMailUtils.sendMail(user.getEmail(), task.getTitle(),mailContent );
		            	}
			        }
			    }catch (Exception e) {
			        e.printStackTrace();
		            log.error(PropUtils.getExceptionInfo(e));
		        }
		 }
	}
	
	/**
	 * 批量保存Task
	 * @param tasks
	 */
	@Transactional(readOnly=false)
	public void saveTasks(List<WorkflowTask> tasks){
		for(WorkflowTask task : tasks){
			saveTask(task);
		}
	}
	

	public List<String> getParticipantsTransactor(Long companyId,
			String instanceId) {
		return workflowTaskDao.getParticipantsTransactor(companyId, instanceId);
	}
	public List<Long> getParticipantsTransactorId(Long companyId,
			String instanceId) {
		return workflowTaskDao.getParticipantsTransactorId(companyId, instanceId);
	}
	
	/**
	 * 查询task
	 * @param id
	 * @return
	 */
	public WorkflowTask getTask(Long id){
		return workflowTaskDao.getTask(id);
	}
	
	/**
	 * 删除Task
	 * @param task
	 */
	@Transactional(readOnly=false)
	public void deleteTask(WorkflowTask task){
		workflowTaskDao.delete(task);
	}
	
	/**
	 * 根据名称删除Task
	 * @param companyId
	 * @param instanceId
	 * @param taskName
	 */
	@Transactional(readOnly=false)
	public void deleteTasksByName(Long companyId, String instanceId, String[] taskName){
    	log.debug("*** deleteTasksByName 方法开始");
		log.debug(new StringBuilder("*** Received parameter:[")
				.append("companyId:").append(companyId)
				.append(", instanceId:").append(instanceId)
				.append(", taskName:").append(taskName)
				.append("]").toString());
		
		workflowTaskDao.deleteTasksByName(companyId, instanceId, taskName);
		
    	log.debug("*** deleteTasksByName 方法结束");
	}
	
	/**
	 * 查询流程实例的第一个任务
	 * @param companyId
	 * @param instanceId
	 * @param transactor
	 * @return
	 */
	public WorkflowTask getFirstTaskByInstance(Long companyId, String instanceId, String transactor,Long userId) {
		return workflowTaskDao.getFirstTaskByInstance(companyId, instanceId, transactor,userId);
	}
	/**
	 * 根据流程名字和实例id查询workflowTask
	 * @param instanceId
	 * @param taskName
	 * @return
	 */
	public List<WorkflowTask> getWorkflowTasks(String instanceId, String taskName) {
		return workflowTaskDao.getWorkflowTasks(instanceId, taskName);
	}
	
	/**
	 * 查询流程实例所有已经生成的任务
	 * @param companyId
	 * @param instanceId
	 * @return
	 */
	public List<String> getTaskNamesByInstance(Long companyId, String instanceId){
    	log.debug("*** getTaskNamesByInstance 方法开始");
		log.debug(new StringBuilder("*** Received parameter:[")
		.append("companyId:").append(companyId)
		.append(", instanceId:").append(instanceId)
		.append("]").toString());
		
		List<WorkflowTask> tasks = workflowTaskDao.find("from WorkflowTask wft where wft.companyId=? and wft.processInstanceId=? and wft.specialTask=? and (wft.active=? or wft.active=? or wft.active=? or wft.active=?)", 
				companyId, instanceId, false,TaskState.WAIT_TRANSACT.getIndex(), TaskState.WAIT_DESIGNATE_TRANSACTOR.getIndex(),TaskState.DRAW_WAIT.getIndex(),TaskState.WAIT_CHOICE_TACHE.getIndex());
		List<String> result = new ArrayList<String>();
		for(WorkflowTask task : tasks){
			if(!result.contains(task.getName())){
				result.add(task.getName());
			}
		}
		
		log.debug("*** getTaskNamesByInstance 方法结束");
		return result;
	}
	
	/**
	 * 查询所有任务
	 * @return
	 */
	public List<Task> getAllTasks(){
		return taskDao.getAll();
	}
	
	public List<WorkflowTask> getTasksByActivity(Long companyId, String executionId, String taskName){
		return taskDao.find("from WorkflowTask wft where wft.companyId=? and wft.executionId=? and wft.name=? and wft.active = 0 and wft.distributable=0", 
				companyId, executionId, taskName);
	}
	
	
	/**
	 * 查询用户所有未完成任务(不是分页)
	 * @param page
	 */
	public List<WorkflowTask> getAllTasksByUser(Long companyId, String loginName){
		return taskDao.find("from Task t where t.companyId = ? and t.transactor = ?  and t.visible = true and (t.active=? or t.active=? or t.active=?  or t.active=?) order by t.groupName,t.createdTime desc", 
				companyId, loginName,TaskState.DRAW_WAIT.getIndex(), TaskState.WAIT_TRANSACT.getIndex(), TaskState.WAIT_DESIGNATE_TRANSACTOR.getIndex(),TaskState.WAIT_CHOICE_TACHE.getIndex());
	}
	/**
	 * 查询用户所有未完成任务(不是分页)
	 * @param page
	 */
	public List<WorkflowTask> getAllTasksByUser(Long companyId, Long userId){
		return taskDao.find("from Task t where t.companyId = ? and  (t.transactorId is not null and t.transactorId=?) and t.visible = true and (t.active=? or t.active=? or t.active=?  or t.active=?) order by t.groupName,t.createdTime desc", 
				companyId, userId,TaskState.DRAW_WAIT.getIndex(), TaskState.WAIT_TRANSACT.getIndex(), TaskState.WAIT_DESIGNATE_TRANSACTOR.getIndex(),TaskState.WAIT_CHOICE_TACHE.getIndex());
	}
	
	/**
	 * 分页查询用户已完成任务
	 * @param companyId
	 * @param loginName
	 * @param page
	 */
	public void getReadTasksByUser(Long companyId, String loginName,Long userId, Page<WorkflowTask> page) {
		workflowTaskDao.getReadTasksByUser(companyId,loginName,userId,page);
	}
	
	/**
	 * 分页查询用户已完成任务
	 * @param companyId
	 * @param loginName
	 * @param page
	 */
	public void getCompletedTasksByUser(Long companyId, String loginName,Long userId, Page<WorkflowTask> page) {
		workflowTaskDao.getCompletedTasksByUser(companyId, loginName,userId, page);
	}
	
	
	/**
	 * 根据任务名称查询任务
	 * @param instanceId
	 * @param name
	 * @return
	 */
	public List<WorkflowTask> getTasksByName(Long companyId, String instanceId, String name){
		return workflowTaskDao.getTasksByName(companyId, instanceId, name);
	}


	/**
	 * 根据任务名称查询任务,不含指派
	 * @param instanceId
	 * @param name
	 * @return
	 */
	public List<WorkflowTask> getNoAssignTasksByName(Long companyId, String instanceId, String taskName,Integer groupNum) {
		return workflowTaskDao.getNoAssignTasksByName(companyId, instanceId, taskName,groupNum);
	}
	
	/**
	 * 根据办理人查询办理人未完成任务
	 * @param companyId
	 * @param stransactor
	 * @return
	 */
	public List<Task> getTasksByTransactor(Long companyId, String stransactor){
		return null;
	}
	
	/**
	 * 返回对应办理模式的所有环节
	 * @param processInstanceId
	 * @param processingMode
	 * @return
	 */
	public List<String> getCountersignByProcessInstanceId(String processInstanceId,TaskProcessingMode processingMode){
		return workflowTaskDao.getCountersignByProcessInstanceId(processInstanceId, processingMode);
	}
	
	/**
	 * 自定义流程中取会签环节名称
	 */
	public List<String> getSignByProcessInstanceId(String processInstanceId,TaskProcessingMode processingMode){
		return workflowTaskDao.getSignByProcessInstanceId(processInstanceId, processingMode);
	}
	/**
	 * 根据办理结果查询环节
	 */
	public List<WorkflowTask> getCountersignByProcessInstanceIdResult(String processInstanceId,String taskName,TaskProcessingResult result){
		return workflowTaskDao.getCountersignByProcessInstanceIdResult(processInstanceId, taskName, result);
	}
	/**
	 * 获得审批任务组数
	 * @param processInstanceId
	 * @param taskName
	 * @param result
	 * @return
	 */
	public List<Integer> getGroupNumByTaskName(String processInstanceId,String taskName){
		return workflowTaskDao.getGroupNumByTaskName(processInstanceId, taskName);
	}
	
	@Transactional(readOnly=false)
	public void deleteWorkflowTask(List<Long> ids) {
		workflowTaskDao.deleteTaskByIds(ids);
	}
	public List<WorkflowTask> getCountersigns(Long id) {
		WorkflowTask wt = workflowTaskDao.get(id);
		return workflowTaskDao.getCountersigns(wt.getProcessInstanceId(), wt.getName());
	}
	
	public List<WorkflowTask> getProcessCountersigns(Long id) {
		WorkflowTask wt = workflowTaskDao.get(id);
		return workflowTaskDao.getCountersigns(id,wt.getProcessInstanceId(), wt.getName());
	}
	
	public List<String> getCountersignsHandler(Long id,Integer handlingState){
		WorkflowTask wt = workflowTaskDao.get(id);
		if(wt==null){throw new RuntimeException("获得会签环节的会签办理人时任务不能为null");}
		return workflowTaskDao.getCountersignsHandler(wt.getProcessInstanceId(), wt.getName(),handlingState);
	}
	@Transactional(readOnly=false)
	public void deleteCountersignHandler(Long taskId, Collection<String> users) {
		WorkflowTask wt = workflowTaskDao.get(taskId);
		if(wt==null){throw new RuntimeException("减签时任务不能为null");}
		workflowTaskDao.deleteCountersignHandler(wt.getProcessInstanceId(), wt.getName(),users);
	}
	@Transactional(readOnly=false)
	public void deleteSignHandler(Long taskId, Collection<Long> userIds) {
		WorkflowTask wt = workflowTaskDao.get(taskId);
		if(wt==null){throw new RuntimeException("减签时任务不能为null");}
		workflowTaskDao.deleteSignHandler(wt.getProcessInstanceId(), wt.getName(),userIds);
	}
    
	public Set<String> getHandledTransactors(String workflowId) {
		Set<String> result = new HashSet<String>();
		List<String> transactors = workflowTaskDao.getHandledTransactors(workflowId);
		if(transactors.size()<=0){
			transactors = historyWorkflowTaskDao.getHandledTransactors(workflowId);
		}
		result.addAll(transactors);
		return result;
	}
	
	public Set<Long> getHandledTransactorIds(String workflowId){
		Set<Long> result = new HashSet<Long>();
		List<Long> transactors = workflowTaskDao.getHandledTransactorIds(workflowId);
		if(transactors.size()<=0){
			transactors = historyWorkflowTaskDao.getHandledTransactorIds(workflowId);
		}
		result.addAll(transactors);
		return result;
	}
	
	public Set<String> getAllHandleTransactors(String workflowId) {
		Set<String> result = new HashSet<String>();
		List<String> transactors = workflowTaskDao.getAllHandleTransactors(workflowId);
		if(transactors.size()<=0){
			transactors = historyWorkflowTaskDao.getAllHandleTransactors(workflowId);
		}
		result.addAll(transactors);
		return result;
	}
	public Set<Long> getAllHandleTransactorIds(String workflowId) {
		Set<Long> result = new HashSet<Long>();
		List<Long> transactors = workflowTaskDao.getAllHandleTransactorIds(workflowId);
		if(transactors.size()<=0){
			transactors = historyWorkflowTaskDao.getAllHandleTransactorIds(workflowId);
		}
		result.addAll(transactors);
		return result;
	}
	
	/**
	 * 得到所有需要催办的task
	 */
	public List<WorkflowTask> getNeedReminderTasks(){
		return workflowTaskDao.getNeedReminderTasks();
	}
	

	/**
	 * 获得 已完成的任务
	 */
	public List<WorkflowTask> getCompletedTasks(String workflowId,
			Long companyId) {
		return workflowTaskDao.getCompletedTasks( workflowId,
				 companyId);
	}
	
	/**
	 * 查询任务
	 * @param tasks
	 * @param names
	 * @param values
	 */
	public void searchTask(Page<Task> tasks, List<String> names, List<String> values, String finish){
		if(Boolean.valueOf(finish)){
			taskDao.findFinishTaskForPage(tasks, names, values);
		}else{
			taskDao.findUNFinishTaskForPage(tasks, names, values);
		}
	}
	public List<WorkflowTask> getNeedReminderTasks(String loginName,Long userId,
			Long companyId) {
		return workflowTaskDao.getNeedReminderTasks(loginName,userId,companyId);
	}
	public List<WorkflowTask> getTasksOrderByWdfName(String definitionName,
			String loginName) {
		return workflowTaskDao.getTasksOrderByWdfName(definitionName, loginName);
	}
	public List<WorkflowTask> getTasksOrderByWdfName(String definitionName,
			Long userId) {
		return workflowTaskDao.getTasksOrderByWdfName(definitionName, userId);
	}
	public List<WorkflowTask> getCompletedTasksByTaskName(String workflowId,
			Long companyId,String taskName){
		return workflowTaskDao.getCompletedTasksByTaskName(workflowId,companyId,taskName);
	}
	/**
	 * 根据当前用户查询未完成任务总数
	 * @param companyId 公司id
	 * @param loginName 当前用户登录名
	 * @return 未完成任务总数
	 */
	public Integer getTasksNumByTransactor(Long companyId, String loginName){
		return workflowTaskDao.getNotCompleteTasksNumByTransactor(companyId, loginName);
	}
	/**
	 * 根据当前用户查询未完成任务总数
	 * @param companyId 公司id
	 * @param loginName 当前用户登录名
	 * @return 未完成任务总数
	 */
	public Integer getTasksNumByTransactor(Long companyId, Long userId){
		return workflowTaskDao.getNotCompleteTasksNumByTransactor(companyId, userId);
	}
	
	/**
	 * 查找公司中所有的超期任务
	 * @param companyId
	 * @return
	 */
	public List<WorkflowTask> getOverdueTasks(Long companyId) {
		return workflowTaskDao.getOverdueTasks(companyId);
	}
	
	/**
	 * 查找当前办理人所有的超期任务的总数
	 * @param companyId
	 * @param transactorName
	 * @return
	 */
	public Map<String,Integer> getOverdueTasksNumByTransactor(Long companyId) {
		List<WorkflowTask> list=workflowTaskDao.getOverdueTasks(companyId);
		Map<String,Integer> map=new HashMap<String,Integer>();
		for(WorkflowTask task:list){
			map.put(task.getTransactor(), workflowTaskDao.getOverdueTasksNumByTransactor(companyId, task.getTransactor(),task.getTransactorId()));
		}
		return map;
	}
	
	/**
	 * 查找公司中所有的超期任务,包括已完成的任务
	 * @param companyId
	 * @return
	 */
	public List<WorkflowTask> getTotalOverdueTasks(Long companyId) {
		return workflowTaskDao.getTotalOverdueTasks(companyId);
	}
	/**
	 * 查找当前办理人所有的超期任务的总数,包括已完成的任务
	 * @param companyId
	 * @param transactorName
	 * @return
	 */
	public Map<String,Integer> getTotalOverdueTasksNumByTransactor(Long companyId) {
		List<WorkflowTask> list=workflowTaskDao.getTotalOverdueTasks(companyId);
		Map<String,Integer> map=new HashMap<String,Integer>();
		for(WorkflowTask task:list){
			map.put(task.getTransactor(), workflowTaskDao.getTotalOverdueTasksNumByTransactor(companyId, task.getTransactor()));
		}
		List<HistoryWorkflowTask> histlist=historyWorkflowTaskDao.getTotalOverdueTasks(companyId);
		for(HistoryWorkflowTask task:histlist){
			Integer taskNum = map.get(task.getTransactor());
			if(taskNum==null||taskNum.equals(0)){
				map.put(task.getTransactor(), historyWorkflowTaskDao.getTotalOverdueTasksNumByTransactor(companyId, task.getTransactor()));
			}else{
				map.put(task.getTransactor(), taskNum+historyWorkflowTaskDao.getTotalOverdueTasksNumByTransactor(companyId, task.getTransactor()));
			}
		}
		return map;
	}
	/**
	 * 查找当前办理人所有的超期任务的总数,包括已完成的任务
	 * @param companyId
	 * @param transactorName
	 * @return Map<Long,Integer>:key 办理人id，value该办理人对应的过期任务
	 */
	public Map<Long,Integer> getTotalOverdueTasksNumByTransactorId(Long companyId) {
		List<WorkflowTask> list=workflowTaskDao.getTotalOverdueTasks(companyId);
		Map<Long,Integer> map=new HashMap<Long,Integer>();
		for(WorkflowTask task:list){
			map.put(task.getTransactorId(), workflowTaskDao.getTotalOverdueTasksNumByTransactorId(companyId, task.getTransactorId()));
		}
		List<HistoryWorkflowTask> histlist=historyWorkflowTaskDao.getTotalOverdueTasks(companyId);
		for(HistoryWorkflowTask task:histlist){
			Integer taskNum = map.get(task.getTransactor());
			if(taskNum==null||taskNum.equals(0)){
				map.put(task.getTransactorId(), historyWorkflowTaskDao.getTotalOverdueTasksNumByTransactorId(companyId, task.getTransactorId()));
			}else{
				map.put(task.getTransactorId(), taskNum+historyWorkflowTaskDao.getTotalOverdueTasksNumByTransactorId(companyId, task.getTransactorId()));
			}
		}
		return map;
	}
	
	public List<String> getTransactorsExceptTask(Long taskId) {
		if(taskId==null)return null;
		WorkflowTask task=getTask(taskId);
		return workflowTaskDao.getTransactorsExceptTask(task);
	}
	public List<Long> getTransactorIdsExceptTask(Long taskId) {
		if(taskId==null)return null;
		WorkflowTask task=getTask(taskId);
		return workflowTaskDao.getTransactorIdsExceptTask(task);
	}
	
	/**
	 * 根据“任务组”查询任务列表
	 * @param companyId
	 * @param instanceId
	 * @param taskName
	 * @return
	 */
	public List<WorkflowTask> getTaskOrderByGroupNum(Long companyId,String instanceId,String taskName){
		return workflowTaskDao.getTaskOrderByGroupNum(companyId,instanceId,taskName);
	}
	
	public List<WorkflowTask> getActivityTasksByName(String instanceId,Long companyId,String taskName) {
		return workflowTaskDao.getActivityTasksByName(instanceId, companyId, taskName);
	}
	
	public List<String[]> getActivityTaskTransactors(String instanceId,Long companyId) {
		return workflowTaskDao.getActivityTaskTransactors(instanceId,companyId);
	}
	public List<WorkflowTask> getActivityTaskByInstance(String instanceId,Long companyId) {
		return workflowTaskDao.getActivityTaskByInstance(instanceId,companyId);
	}
	
	public List<String> getActivityTaskPrincipals(String instanceId,Long companyId) {
		return workflowTaskDao.getActivityTaskPrincipals(instanceId,companyId);
	}
	
	public List<Long> getActivityTaskPrincipalIds(String instanceId,Long companyId) {
		return workflowTaskDao.getActivityTaskPrincipalIds(instanceId,companyId);
	}
	public List<String[]> getActivityTaskPrincipalsDetail(String instanceId,Long companyId) {
		return workflowTaskDao.getActivityTaskPrincipalsDetail(instanceId,companyId);
	}
	public List<WorkflowTask> getActivityPrincipalTask(String instanceId,Long companyId) {
		return workflowTaskDao.getActivityPrincipalTask(instanceId,companyId);
	}
	public List<String> getCompletedTaskNames(String workflowId,
			Long companyId) {
		return workflowTaskDao.getCompletedTaskNames(workflowId, companyId);
	}
	public String receive(Long taskId) {
		log.debug("*** receive 方法开始");
		log.debug(new StringBuilder("*** Received parameter:[")
		.append("taskId:").append(taskId)
		.append("]").toString());
    	if(taskId==null){log.debug("领取任务时，任务id不能为null");throw new RuntimeException("领取任务时，任务id不能为null");}
		WorkflowTask task = workflowTaskDao.get(taskId);
		if(task==null){log.debug("领取任务时，任务不能为null");throw new RuntimeException("领取任务时，任务不能为null");}
		String msg = "task.not.need.receive";
		if(task.getActive().equals(TaskState.DRAW_WAIT.getIndex())){
			List<WorkflowTask> tasks = workflowTaskDao.getNotCompleteTasksByName(getCompanyId(), task.getProcessInstanceId(), task.getName());
			for(WorkflowTask tsk : tasks){
				if(taskId.equals(tsk.getId())){
					task.setDrawTask(true);
					tsk.setActive(TaskState.WAIT_TRANSACT.getIndex());
				}else{
					tsk.setActive(TaskState.HAS_DRAW_OTHER.getIndex());
				}
			}
			msg = "task.receive.success";
		}
		
    	log.debug("*** receive 方法结束");
		return msg;
	}
	/**
     * 流程被暂停时，强制暂停的当前任务
     */
	@Transactional(readOnly=false)
    public void pauseTasks(String instanceId,Long companyId){
    	log.debug("*** pauseTasks 方法开始");
		log.debug(new StringBuilder("*** Received parameter:[")
				.append("instanceId:").append(instanceId)
				.append(", companyId:").append(companyId)
				.append("]").toString());
    	
    	List<WorkflowTask> tasks = getActivityTasks(instanceId,companyId);
    	for(WorkflowTask task:tasks){
    		task.setPaused(true);
    		saveTask(task);
    	}
    	
    	log.debug("*** pauseTasks 方法结束");
    }
	
	/**
     * 流程被暂停时，强制暂停的当前任务
     */
	@Transactional(readOnly=false)
    public void continueTasks(String instanceId,Long companyId){
    	log.debug("*** continueTasks 方法开始");
		log.debug(new StringBuilder("*** Received parameter:[")
				.append("instanceId:").append(instanceId)
				.append(", companyId:").append(companyId)
				.append("]").toString());
    	
    	List<WorkflowTask> tasks = workflowTaskDao.getPauseTasksByInstance(instanceId,companyId);
    	for(WorkflowTask task:tasks){
    		task.setPaused(false);
    		saveTask(task);
    		String processId= processEngine.getExecutionService().findProcessInstanceById(task.getProcessInstanceId()).getProcessDefinitionId();
    		WorkflowDefinition wfDef = workflowDefinitionManager.getWorkflowDefinitionByProcessId(processId);
    		//流程类型
    		WorkflowType type=null;
    		if(wfDef!=null){
    			//流程类型
    			type=workflowTypeManager.getWorkflowType(wfDef.getTypeId());
    		}
    		//生成消息提醒
    		if(task.getTransactor()!=null){
    			sendMessage(task, type,"");
    		}
    		//*******************邮件通知*************************
    		sendMail(task,processId);
    	}
    	
    	log.debug("*** continueTasks 方法结束");
    }
	/**
	 * 批量移除任务中根据办理人查询当前任务列表
	 * @param tasks
	 * @param transactorName
	 * @param typeId
	 * @param defCode
	 * @param wfdId
	 */
	
	public void getActivityTasksByTransactorName(Page<WorkflowTask> tasks,Long typeId, String defCode,Long wfdId){
		workflowTaskDao.getActivityTasksByTransactorName(tasks,  typeId, defCode, wfdId);
	}
	
	public void getAllTasksByUser(Long companyId, String loginName,
			Page<WorkflowTask> page) {
		workflowTaskDao.getAllTasksByUser(companyId, loginName, page);
		
	}
	public void getAllTasksByUser(Long companyId, Long userId,
			Page<WorkflowTask> page) {
		workflowTaskDao.getAllTasksByUser(companyId, userId, page);
		
	}
	
	public List<WorkflowTask> getTasksByInstance(List<String> instanceIds,String taskName,String recieveUser,Long recieveId,String consignor,Long consignorId,Long companyId){
		return workflowTaskDao.getTasksByInstance(instanceIds, taskName,recieveUser,recieveId,consignor,consignorId,companyId);
	}
	
	public List<String> getActiveTaskNameWithoutSpecial(String instanceId){
		return workflowTaskDao.getActiveTaskNameWithoutSpecial(instanceId);
	}
	
	/**
	 * 获得所有流程名称
	 * @param isComplete
	 * @return
	 */
	public List<Object[]> getGroupNames(String taskCategory){
		if(TaskCategory.COMPLETE.equals(taskCategory)){
			return workflowTaskDao.getAllCompleteTaskGroupNames(ContextUtils.getCompanyId(), ContextUtils.getLoginName(),ContextUtils.getUserId());
		}else if(TaskCategory.CANCEL.equals(taskCategory)){
			return workflowTaskDao.getAllCancelTaskGroupNames(ContextUtils.getCompanyId(), ContextUtils.getLoginName(),ContextUtils.getUserId());
		}else{
			return workflowTaskDao.getAllActiveTaskGroupNames(ContextUtils.getCompanyId(), ContextUtils.getLoginName(),ContextUtils.getUserId());
		}
	}
	/**
	 * 获得所有流程自定义类别
	 * @param isComplete
	 * @return
	 */
	public List<Object[]> getCustomTypes(String taskCategory){
		if(TaskCategory.COMPLETE.equals(taskCategory)){
			return workflowTaskDao.getAllCompleteTaskCustomTypes(ContextUtils.getCompanyId(), ContextUtils.getLoginName(),ContextUtils.getUserId());
		}else if(TaskCategory.CANCEL.equals(taskCategory)){
			return workflowTaskDao.getAllCancelTaskCustomTypes(ContextUtils.getCompanyId(), ContextUtils.getLoginName(),ContextUtils.getUserId());
		}else{
			return workflowTaskDao.getAllActiveTaskCustomTypes(ContextUtils.getCompanyId(), ContextUtils.getLoginName(),ContextUtils.getUserId());
		}
	}
	
	/**
	 * 分页查询用户所有未完成任务
	 * @param page
	 */
	public void getAllTasksByGroupName(Long companyId, String loginName,Long userId, Page<Task> page,String typeName){
		taskDao.getAllTasksByUserType(companyId, loginName,userId, page,typeName);
	}
	/**
	 * 获得办理任务页面
	 */
	@Transactional(readOnly=true)
	public String getTaskUrl(Task task){
		return getTaskUrl(task.getUrl(),task.getId());
	}
	@Transactional(readOnly=true)
	public String getTaskUrl(String taskUrl,Long taskId ){
		String url=taskUrl;
		if(!taskUrl.contains("http://")&&taskUrl.contains("?")){
			url=SystemUrls.getSystemUrl(StringUtils.substringBefore(taskUrl, "/"))+StringUtils.substring(taskUrl, taskUrl.indexOf('/'))+taskId;
		}else if(!taskUrl.contains("http://")){
			url=SystemUrls.getSystemUrl(StringUtils.substringBefore(taskUrl, "/"))+StringUtils.substring(taskUrl, taskUrl.indexOf('/'))+"?taskId="+taskId;
		}
		//重新加载页面样式
		if(!url.contains("_r=1")){
			if(url.contains("?")){
				url=url+"&_r=1";
			}else{
				url=url+"?_r=1";
			}
		}
		return url;
	}
	
	public WorkflowTask getLastCompletedTaskByTaskName(String workflowId,
			Long companyId,String taskName){
		return workflowTaskDao.getLastCompletedTaskByTaskName(workflowId,companyId,taskName);
	}
	public List<WorkflowTask> getActivityTasksByNameWithout(String workflowId,
			Long taskId, String taskName) {
		return workflowTaskDao.getActivityTasksByNameWithout(workflowId, taskId, taskName);
	}
	/**
	 * 根据办理人查找待办理的委托任务
	 * @param workflowId
	 * @param transactor
	 * @return
	 */
	public List<WorkflowTask> getActivityTrustorTasksByTransactor(String workflowId,String transactor,Long userId,Long taskId) {
		return workflowTaskDao.getActivityTrustorTasksByTransactor(workflowId, transactor,userId,taskId);
	}
	
	public List<String> getTransactorsByTask(String name,String transactor,Long userId,String processId){
		return workflowTaskDao.getTransactorsByTask(name,transactor,userId,processId);
	}
	/**
	 * 根据办理人查询当前实例中的任务
	 * @param transactor 办理人登录名
	 * @param workflowId 实例id
	 * @return 
	 */
	public List<WorkflowTask> getTaskByTransactor(String transactor,String workflowId){
		return workflowTaskDao.getTaskByTransactor(transactor, workflowId);
	}
	/**
	 * 根据办理人查询当前实例中的任务
	 * @param userId 办理人id
	 * @param workflowId 实例id
	 * @return 
	 */
	public List<WorkflowTask> getTaskByTransactor(Long userId,String workflowId){
		return workflowTaskDao.getTaskByTransactor(userId, workflowId);
	}
	
	public List<HistoryWorkflowTask> getHistoryTaskByTransactor(String transactor,String workflowId){
		return historyWorkflowTaskDao.getTaskByTransactor(transactor, workflowId);
	}
	public List<HistoryWorkflowTask> getHistoryTaskByTransactor(Long userId,String workflowId){
		return historyWorkflowTaskDao.getTaskByTransactor(userId, workflowId);
	}
	
	public Set<String> getAllTaskTransactors(String workflowId){
		Set<String> result = new HashSet<String>();
		result.addAll(workflowTaskDao.getAllTaskTransactors(workflowId)); 
		result.addAll(historyWorkflowTaskDao.getAllTaskTransactors(workflowId)); 
		return result;
	}
	public Set<String> getAllTaskTrustors(String workflowId){
		Set<String> result = new HashSet<String>();
		result.addAll(workflowTaskDao.getAllTaskTrustors(workflowId)); 
		result.addAll(historyWorkflowTaskDao.getAllTaskTrustors(workflowId)); 
		return result;
	}
	
	/**
	 * 查询当前环节的办理人和委托人集合
	 * @param workflowId 实例id
	 * @param taskName 任务名称
	 * @return 办理人列表
	 */
	public Set<String> getTransactorsByName(String workflowId,String taskName) {
		List<String> transactors =  workflowTaskDao.getTransactorsByName(workflowId, taskName);
		List<String> trustors =  workflowTaskDao.getTrustorsByName(workflowId, taskName);
		List<String> HistTransactors =  historyWorkflowTaskDao.getTransactorsByName(workflowId, taskName);
		List<String> HistTrustors =  historyWorkflowTaskDao.getTrustorsByName(workflowId, taskName);
		Set<String> result = new HashSet<String>();
		result.addAll(transactors);
		result.addAll(trustors);
		result.addAll(HistTransactors);
		result.addAll(HistTrustors);
		return result;
	}
	
	public Set<Long> getAllTaskTransactorIds(String workflowId){
		Set<Long> result = new HashSet<Long>();
		result.addAll(workflowTaskDao.getAllTaskTransactorIds(workflowId)); 
		result.addAll(historyWorkflowTaskDao.getAllTaskTransactorIds(workflowId)); 
		return result;
	}
	public Set<Long> getAllTaskTrustorIds(String workflowId){
		Set<Long> result = new HashSet<Long>();
		result.addAll(workflowTaskDao.getAllTaskTrustorIds(workflowId)); 
		result.addAll(historyWorkflowTaskDao.getAllTaskTrustorIds(workflowId)); 
		return result;
	}
	
	/**
	 * 查询当前环节的办理人和委托人集合
	 * @param workflowId 实例id
	 * @param taskName 任务名称
	 * @return 办理人列表
	 */
	public Set<Long> getTransactorIdsByName(String workflowId,String taskName) {
		List<Long> transactors =  workflowTaskDao.getTransactorIdsByName(workflowId, taskName);
		List<Long> trustors =  workflowTaskDao.getTrustorIdsByName(workflowId, taskName);
		List<Long> HistTransactors =  historyWorkflowTaskDao.getTransactorIdsByName(workflowId, taskName);
		List<Long> HistTrustors =  historyWorkflowTaskDao.getTrustorIdsByName(workflowId, taskName);
		Set<Long> result = new HashSet<Long>();
		result.addAll(transactors);
		result.addAll(trustors);
		result.addAll(HistTransactors);
		result.addAll(HistTrustors);
		return result;
	}
	
}
