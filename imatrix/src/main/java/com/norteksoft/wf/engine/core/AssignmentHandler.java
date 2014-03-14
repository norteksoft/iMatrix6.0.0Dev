package com.norteksoft.wf.engine.core;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.lang.StringUtils;
import org.jbpm.api.activity.ActivityExecution;
import org.jbpm.api.model.OpenExecution;
import org.jbpm.api.task.Assignable;
import org.jbpm.internal.log.Log;

import com.norteksoft.mms.form.dao.GeneralDao;
import com.norteksoft.mms.form.entity.FormView;
import com.norteksoft.mms.form.service.FormViewManager;
import com.norteksoft.product.util.ContextUtils;
import com.norteksoft.task.entity.WorkflowTask;
import com.norteksoft.wf.base.enumeration.CommonStrings;
import com.norteksoft.wf.base.enumeration.TaskTransactorCondition;
import com.norteksoft.wf.base.exception.WorkflowException;
import com.norteksoft.wf.engine.entity.WorkflowInstance;
import com.norteksoft.wf.engine.service.TaskService;
import com.norteksoft.wf.engine.service.WorkflowInstanceManager;

/**
 * 办理人指定处理类
 * @author Administrator
 *
 */
public class AssignmentHandler implements org.jbpm.api.task.AssignmentHandler{

	private static final long serialVersionUID = 1L;
	private String newTransactor;
	private String newTransactorId;
	private static final Log log = Log.getLog(AssignmentHandler.class.getName());
	
	public void assign(Assignable assignable, OpenExecution execution)
			throws Exception {
		Long companyId=getCompanyId();
		if(companyId==null){
			Object compIdStr=execution.getVariable(CommonStrings.COMPANY_ID);
			if(compIdStr!=null){
				companyId=Long.parseLong(compIdStr.toString()); 
			}
		}
		getVariables(execution);
		if(StringUtils.isNotEmpty(newTransactorId)){//如果用户id不为空
			setNewTransactor(assignable,newTransactorId);
		}else if(StringUtils.isNotEmpty(newTransactor)){
			setNewTransactor(assignable,newTransactor);
		}else{
			Object transactorId=execution.getVariable(CommonStrings.RETRIEVE_TASK_USER_ID); 
			execution.removeVariable(CommonStrings.RETRIEVE_TASK_USER_ID);
			Object transactor=execution.getVariable(CommonStrings.RETRIEVE_TASK_USER); 
			execution.removeVariable(CommonStrings.RETRIEVE_TASK_USER);
			if(transactorId!=null&&StringUtils.isNotEmpty(transactorId.toString())){//取回时办理人设置的条件没有查到相应的办理人（如：字段中指定人员时，该字段的值为空； 条件筛选没有获得相应的办理人），该jbpm变量存储当前任务的办理人的id
				assignable.setAssignee(transactorId.toString());
			}else if(transactor!=null&&StringUtils.isNotEmpty(transactor.toString())){//取回时办理人设置的条件没有查到相应的办理人（如：字段中指定人员时，该字段的值为空； 条件筛选没有获得相应的办理人），该jbpm变量存储当前任务的办理人的登录名
				assignable.setAssignee(transactor.toString());
			}else{
				String activityName=((ActivityExecution)execution).getActivityName();
				log.info("开始指定办理人，环节名："+activityName);
				String processId = ((ActivityExecution)execution).getProcessDefinitionId();
				String processInstanceId = ((ActivityExecution)execution).getProcessInstance().getId();
				//设置流程实例中当前环节名称字段值
				WorkflowInstanceManager workflowInstanceManager = (WorkflowInstanceManager)ContextUtils.getBean("workflowInstanceManager");
				WorkflowInstance instance = workflowInstanceManager.getWorkflowInstance(processInstanceId,companyId);
				if(instance==null){throw new RuntimeException("设置办理人监听中，流程实例不能为null");}
				instance.setCurrentActivity(activityName);
				workflowInstanceManager.saveWorkflowInstance(instance);
				
				Set<String> candidates = null;
				Object originalUser=execution.getVariable(CommonStrings.IS_ORIGINAL_USER); 
				execution.removeVariable(CommonStrings.IS_ORIGINAL_USER);
				Object allOriginalUsers=execution.getVariable(CommonStrings.ALL_ORIGINAL_USERS); 
				execution.removeVariable(CommonStrings.ALL_ORIGINAL_USERS);
				if("true".equals(originalUser)){
					TaskService  taskService = (TaskService)ContextUtils.getBean("taskService");
					if(ContextUtils.getCompanyId()==null){throw new RuntimeException("设置办理人监听中，公司id不能为null");}
					List<WorkflowTask> list=taskService.getCompletedTasksByTaskName(processInstanceId, ContextUtils.getCompanyId(), activityName);
					candidates = new HashSet<String>();
					if(allOriginalUsers!=null && !"".equals(allOriginalUsers)){
						String[] aous=allOriginalUsers.toString().split(",");
						for(String s:aous){
							for(WorkflowTask task:list){
								if(s.equals(task.getTransactorId()) || s.equals(task.getTransactor())){//为兼容历史数据。如果原办理人是用户id，且与任务的办理人id相等 || 原办理人是用户登录名，且与任务的办理人登录名相等
									if(task.getTransactorId()==null){//如果办理人id为null
										candidates.add(task.getTransactor());
									}else{
										candidates.add(task.getTransactorId()+"");
									}
									break;
								}
							}
						}
					}
					//当没有传入该环节上次办理人的登录名，则将所有已办理该环节的人加入候选人集合中
					if(allOriginalUsers==null ||(allOriginalUsers!=null && "".equals(allOriginalUsers))){
						for(WorkflowTask task:list){
							if(task.getTransactorId()==null){//如果办理人id为null
								candidates.add(task.getTransactor());
							}else{
								candidates.add(task.getTransactorId()+"");
							}
						}
					}
				}
				if(originalUser==null || "false".equals(originalUser) || ("true".equals(originalUser)&&candidates.size()==0)){
					String creator = execution.getVariable(CommonStrings.CREATOR)==null?null:execution.getVariable(CommonStrings.CREATOR).toString();
					
					
					if(creator!=null){
						if(creator.contains(CommonStrings.SUB_PROCESS_CREATOR_TRUSTOR)){//表示是委托任务(trustor-:-trustee)，只有将子流程环节委托出去才会走该判断
							String[] transactors = creator.split(CommonStrings.SUB_PROCESS_CREATOR_TRUSTOR);
							creator = transactors[0];
						}
					}
					String creatorId = execution.getVariable(CommonStrings.CREATOR_ID)==null?"":execution.getVariable(CommonStrings.CREATOR_ID).toString();
					if(creatorId.contains(CommonStrings.SUB_PROCESS_CREATOR_TRUSTOR)){//表示是委托任务(trustorId-:-trusteeId)，只有将子流程环节委托出去才会走该判断
						String[] transactors = creatorId.split(CommonStrings.SUB_PROCESS_CREATOR_TRUSTOR);
						creatorId = transactors[0];
					}
					//JPDL定义扩展参数
					Map<TaskTransactorCondition, String> conditions = 
						DefinitionXmlParse.getTaskTransactor(processId, activityName);
					
					
					//根据条件选定办理人
					log.info("办理人设置条件为:"+conditions);
					Map<String,String> paramMap = new HashMap<String,String>();
					paramMap.put(TransactorConditionHandler.DOCUMENT_CREATOR, creator);
					paramMap.put(TransactorConditionHandler.DOCUMENT_CREATOR_ID, creatorId);
					paramMap.put(TransactorConditionHandler.PROCESS_INSTANCEID, processInstanceId);
					Object obj = execution.getVariable(CommonStrings.PREVIOUS_TASK_PRINCI_TRANSACTOR);
					if(obj==null){//上一环节办理人委托人为空，取办理人
						obj = execution.getVariable(CommonStrings.PREVIOUS_TASK_TRANSACTOR);
					}
					if(obj!=null){
						paramMap.put(TransactorConditionHandler.PREVIOUS_TRANSACTOR, obj.toString());
					}
					
					obj = execution.getVariable(CommonStrings.PREVIOUS_TASK_PRINCI_TRANSACTOR_ID);
					if(obj==null||StringUtils.isEmpty(obj.toString())){//上一环节办理人委托人为空，取办理人
						obj = execution.getVariable(CommonStrings.PREVIOUS_TASK_TRANSACTOR_ID);
					}
					if(obj!=null&&StringUtils.isNotEmpty(obj.toString())){
						paramMap.put(TransactorConditionHandler.PREVIOUS_TRANSACTOR_ID, obj.toString());
					}
					
					candidates = TransactorConditionHandler.processCondition(conditions, execution,paramMap);
				}
				log.info("选定的办理人为：candidates:" +candidates);
				
				if(candidates.size() == 1){
					assignable.setAssignee(candidates.iterator().next());
				}else if(candidates.size() == 0){
					throw new WorkflowException(WorkflowException.NO_TRANSACTOR);
				}else{
					for(String candidate : candidates){
						assignable.addCandidateUser(candidate);
					}
				}
			}
			
		}
		
		//子流程返回时，需要通知父流程的下一环节来生成任务
		Object needGenerateTask = execution.getVariable(CommonStrings.NEED_GENERATE_TASK);
		Object parentInstanceId = execution.getVariable(CommonStrings.PARENT_INSTANCE_ID);
		if(needGenerateTask!=null&&Boolean.valueOf(needGenerateTask.toString())){
			TaskService taskService = (TaskService)ContextUtils.getBean("taskService");
			ActivityExecution activityExecution = (ActivityExecution)execution;
			String activityName = activityExecution.getActivityName();
			
			String processInstanceId = ((ActivityExecution)execution).getProcessInstance().getId();
			
			WorkflowInstanceManager workflowInstanceManager = (WorkflowInstanceManager)ContextUtils.getBean("workflowInstanceManager");
			GeneralDao generalDao = (GeneralDao)ContextUtils.getBean("generalDao");
			FormViewManager formManager = (FormViewManager)ContextUtils.getBean("formViewManager");
			WorkflowInstance instance = workflowInstanceManager.getWorkflowInstance(processInstanceId,companyId);
			instance.setCurrentActivity(activityName);
			workflowInstanceManager.saveWorkflowInstance(instance);
			FormView  form = formManager.getFormView(instance.getFormId());
			if(form==null){throw new RuntimeException("设置办理人监听中，表单不能为null");}
			if(form.isStandardForm()){
				if(form.getDataTable()==null){throw new RuntimeException("设置办理人监听中，表单对应的数据表不能为null");}
				String className = form.getDataTable().getEntityName();
				log.info("实体类名：" + className);
				//根据表名和id获得实体
				try {
					if(className==null){throw new RuntimeException("设置办理人监听中，表单对应的数据表的实体类名不能为null");}
					Object entity = generalDao.getObject(className, instance.getDataId());
					BeanUtils.setProperty(entity, "workflowInfo.currentActivityName", activityName);
					generalDao.save(entity);
				} catch (Exception e) {
					throw new RuntimeException(e);
				} 
			}
			taskService.generateTask(instance, execution.getId(), null);
		}
	
		if(execution!=null&&parentInstanceId!=null&&parentInstanceId.toString().equals(execution.getId())){
			 execution.removeVariable(CommonStrings.NEED_GENERATE_TASK);
			 execution.removeVariable(CommonStrings.PARENT_INSTANCE_ID);
		}
		
		
	}
	private void setNewTransactor(Assignable assignable,String newTransactor){
		String[] transactors=newTransactor.split(",");
		Set<String> ts=new HashSet<String>();
		for(int i=0;i<transactors.length;i++){
			ts.add(transactors[i]);
		}
		if(ts.size()==1){
			assignable.setAssignee(ts.iterator().next());
		}else{
			for(String t : ts){
				assignable.addCandidateUser(t);
			}
			
		}
	}
	
	private void getVariables(OpenExecution execution){
		Object obj = execution.getVariable(CommonStrings.NEW_TRANSACTOR);
		if(obj!=null){
			newTransactor = obj.toString();
			execution.removeVariable(CommonStrings.NEW_TRANSACTOR);
		}else{
			newTransactor = null;
		}
		obj = execution.getVariable(CommonStrings.NEW_TRANSACTOR_ID);
		if(obj!=null){
			newTransactorId = obj.toString();
			execution.removeVariable(CommonStrings.NEW_TRANSACTOR_ID);
		}else{
			newTransactorId = null;
		}
	}

	private static Long getCompanyId(){
		return ContextUtils.getCompanyId();
	}
}