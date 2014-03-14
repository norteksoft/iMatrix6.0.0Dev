package com.norteksoft.wf.engine.core;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.jbpm.api.activity.ActivityExecution;
import org.jbpm.api.model.OpenExecution;

import com.norteksoft.mms.form.entity.FormView;
import com.norteksoft.mms.form.entity.TableColumn;
import com.norteksoft.mms.form.service.FormViewManager;
import com.norteksoft.mms.form.service.TableColumnManager;
import com.norteksoft.product.api.ApiFactory;
import com.norteksoft.product.api.entity.User;
import com.norteksoft.product.util.ContextUtils;
import com.norteksoft.wf.base.enumeration.CommonStrings;
import com.norteksoft.wf.base.enumeration.TaskTransactorCondition;
import com.norteksoft.wf.base.exception.WorkflowException;
import com.norteksoft.wf.engine.client.SingleTransactorSelector;
import com.norteksoft.wf.engine.core.impl.UserParseCalculator;
import com.norteksoft.wf.engine.entity.WorkflowInstance;
import com.norteksoft.wf.engine.service.WorkflowInstanceManager;

public class TransactorConditionHandler {

	public static final String  DOCUMENT_CREATOR = "documentCreator";//文档创建人
	public static final String CURRENT_TRANSACTOR = "currentTransactor";//当前办理人
	public static final String CURRENT_TRANSACTOR_ID = "currentTransactorId";//当前办理人
	public static final String PREVIOUS_TRANSACTOR = "previousTransactor";//上一环节办理人
	public static final String PREVIOUS_TRANSACTOR_ID = "previousTransactorId";//上一环节办理人id
	public static final String PROCESS_ADMIN = "processAdmin";//流程管理员
	public static final String PROCESS_ADMIN_ID = "processAdminId";//流程管理员
	public static final String PROCESS_INSTANCEID = "processInstanceId";//流程管理员
	public static final String  DOCUMENT_CREATOR_ID = "documentCreatorId";//文档创建人id
	/*
	 * 根据条件选定办理人
	 * @param conditions
	 * @param creator
	 * @param _wf_transactor
	 * @return
	 */
	public static  Set<String> processCondition(Map<TaskTransactorCondition, String> conditions, OpenExecution execution,Map<String,String> param){
		Long companyId=ContextUtils.getCompanyId();
		if(companyId==null){
			Object compIdStr=execution.getVariable(CommonStrings.COMPANY_ID);
			if(compIdStr!=null){
				companyId=Long.parseLong(compIdStr.toString()); 
			}
		}
		String userCondition = conditions.get(TaskTransactorCondition.USER_CONDITION);
		if(userCondition==null){throw new RuntimeException("办理人设置中，解析条件时，设置的条件不能为null");}
		//根据条件获取办理人
		Set<String> candidates = new HashSet<String>();
		if("${documentCreator}".equals(userCondition)){
			//文档创建人
			String creatorId = param.get(DOCUMENT_CREATOR_ID);
			if(StringUtils.isNotEmpty(creatorId)){
				candidates.add(creatorId);
			}else{
				candidates.add(param.get(DOCUMENT_CREATOR));
			}
			
		}else if("${previousTransactorAssignment}".equals(userCondition)){
			//上一环节办理人指定
			candidates.add(CommonStrings.TRANSACTOR_ASSIGNMENT);
		}else if(userCondition.startsWith("${field[")){
			//文档字段中指定//${field[name_zn[name_en]]}
			int start = userCondition.lastIndexOf("[");
			int end = userCondition.indexOf("]");
			String fieldName = userCondition.substring(start + 1, end);
			String fieldValues = null;
			WorkflowInstanceManager manager = (WorkflowInstanceManager) ContextUtils.getBean("workflowInstanceManager");
			
			Object obj=execution.getVariable(CommonStrings.TASK_JUMP_FIELD_NO_VALUE_FLAG);
			execution.removeVariable(CommonStrings.TASK_JUMP_FIELD_NO_VALUE_FLAG);
			if(obj==null){//环节跳转中，跳转到的环节的办理人为“字段中的值”不为空时 或  是正常生成任务时 或  批量环节跳转中，由于异常情况而无法获得办理人时
				obj=execution.getVariable(CommonStrings.TASK_JUMP_FIELD_VALUE_FLAG);
				execution.removeVariable(CommonStrings.TASK_JUMP_FIELD_VALUE_FLAG);
				if(obj==null){//正常生成任务 或 批量环节跳转中，由于异常情况而无法获得办理人时
					obj=execution.getVariable(CommonStrings.TASK_JUMP_SELECT_USER);
					if(obj==null){//正常生成任务
						fieldValues = manager.getFieldValueInForm(param.get(PROCESS_INSTANCEID), fieldName);
					}else{//批量环节跳转中，所选的实例跳转到的环节中的字段的值有为空的，所以走该分支判断,当字段的值为空时办理人以jbpm变量 TASK_JUMP_SELECT_USER的值为准，如果字段的值不为空则以字段的值为准
						fieldValues = getFieldValue(param,companyId,fieldName);
					}
				}else{//环节跳转中，跳转到的环节的办理人为“字段中的值”不为空
						fieldValues = getFieldValue(param,companyId,fieldName);
				}
				if(fieldValues==null||StringUtils.isEmpty(fieldValues)){
					obj=execution.getVariable(CommonStrings.TASK_JUMP_SELECT_USER);
					execution.removeVariable(CommonStrings.TASK_JUMP_SELECT_USER);
					if(obj!=null){//批量环节跳转中，所选的实例跳转到的环节中的字段的值有为空的,办理人以jbpm变量 TASK_JUMP_SELECT_USER的值为准
						fieldValues = obj.toString();
					}else{
						throw new RuntimeException("办理人设置中，文档字段中指定时，该字段的值不能为null");
					}
				}
				for(String fieldValue:fieldValues.split(",")){
					fieldValue = fieldValue.trim();
					if(StringUtils.isNotEmpty(fieldValue)){
						if("ALLCOMPANYID".equals(fieldValue)){//所有人员(不包含无部门人员)
							List<Long> userids = ApiFactory.getAcsService().getAllUserIdsWithoutAdminByCompany();
							candidates.addAll(Arrays.asList(userids.toString().replace("[", "").replace("]", "").replace(" ", "").split(",")));
						}else if("ALLWORKGROUP".equals(fieldValue)){//所有工作组中的人员
							List<Long> userids = ApiFactory.getAcsService().getUserIdsByWorkgroup(ContextUtils.getCompanyId());
							candidates.addAll(Arrays.asList(userids.toString().replace("[", "").replace("]", "").replace(" ", "").split(",")));
						}else{
							candidates.add(fieldValue);
						}
					}
				}
			}else{//环节跳转时，且字段中的值为空时，让流程管理员指定办理人
				candidates.add(CommonStrings.TRANSACTOR_ASSIGNMENT);
			}
		}else{ 
			if(execution==null){throw new RuntimeException("办理人设置中，解析条件时，execution不能为null");}
			FormViewManager formManager = (FormViewManager)ContextUtils.getBean("formViewManager");
			WorkflowInstanceManager workflowInstanceManager = (WorkflowInstanceManager)ContextUtils.getBean("workflowInstanceManager");
			WorkflowInstance wi = workflowInstanceManager.getWorkflowInstance(param.get(PROCESS_INSTANCEID),companyId);
			if(wi==null){throw new RuntimeException("办理人设置中，解析条件时，流程实例不能为null");}
			FormView form = formManager.getFormView(wi.getFormId());
			UserParseCalculator upc = new UserParseCalculator();
			upc.setDataId(wi.getDataId());
			upc.setFormView(form);
			upc.setDocumentCreator(param.get(DOCUMENT_CREATOR));
			if(StringUtils.isNotEmpty(param.get(DOCUMENT_CREATOR_ID)))upc.setDocumentCreatorId(Long.parseLong(param.get(DOCUMENT_CREATOR_ID)));
			upc.setPreviousTransactor(param.get(PREVIOUS_TRANSACTOR));
			if(StringUtils.isNotEmpty(param.get(PREVIOUS_TRANSACTOR_ID)))upc.setPreviousTransactorId(Long.parseLong(param.get(PREVIOUS_TRANSACTOR_ID)));
			upc.setCurrentTransactor(param.get(CURRENT_TRANSACTOR));
			if(StringUtils.isNotEmpty(param.get(CURRENT_TRANSACTOR_ID)))upc.setCurrentTransactorId(Long.parseLong(param.get(CURRENT_TRANSACTOR_ID)));
			upc.setProcessAdmin(param.get(PROCESS_ADMIN));
			if(StringUtils.isNotEmpty(param.get(PROCESS_ADMIN_ID)))upc.setProcessAdminId(Long.parseLong(param.get(PROCESS_ADMIN_ID)));
			
			Long creatorId = null;
			if(StringUtils.isNotEmpty(param.get(DOCUMENT_CREATOR_ID))){
				creatorId = Long.parseLong(param.get(DOCUMENT_CREATOR_ID));
			}
			
			Set<String> users = new HashSet<String>();
			Object obj=execution.getVariable(CommonStrings.TASK_JUMP_FIELD_NO_VALUE_FLAG);
			execution.removeVariable(CommonStrings.TASK_JUMP_FIELD_NO_VALUE_FLAG);
			if(obj==null){//环节跳转中，跳转到的环节的办理人为“解析的条件值”不为空时 或  是正常生成任务时 或  批量环节跳转中，由于异常情况而无法获得办理人时
				obj=execution.getVariable(CommonStrings.TASK_JUMP_FIELD_VALUE_FLAG);
				execution.removeVariable(CommonStrings.TASK_JUMP_FIELD_VALUE_FLAG);
				if(obj==null){//正常生成任务 或 批量环节跳转中，由于异常情况而无法获得办理人时
					obj=execution.getVariable(CommonStrings.TASK_JUMP_SELECT_USER);
					if(obj==null){//正常生成任务
						users = processAdditionalCondition(conditions,upc.getUsers(userCondition,wi.getSystemId(),wi.getCompanyId()), param.get(DOCUMENT_CREATOR),creatorId, execution);
					}else{//批量环节跳转中，所选的实例跳转到的环节中的解析的条件值有为空的，所以走该分支判断,当解析的条件值为空时办理人以jbpm变量 TASK_JUMP_SELECT_USER的值为准，如果解析的条件值不为空则以解析的条件值为准
						users = processAdditionalCondition(conditions,upc.getUsers(userCondition,wi.getSystemId(),wi.getCompanyId()),param.get(DOCUMENT_CREATOR),creatorId , execution);
					}
				}else{//环节跳转中，跳转到的环节的办理人为“解析的条件值”不为空
					users = processAdditionalCondition(conditions,upc.getUsers(userCondition,wi.getSystemId(),wi.getCompanyId()), param.get(DOCUMENT_CREATOR),creatorId, execution);
				}
				if(users.size()<=0){
					obj=execution.getVariable(CommonStrings.TASK_JUMP_SELECT_USER);
					execution.removeVariable(CommonStrings.TASK_JUMP_SELECT_USER);
					if(obj!=null){//批量环节跳转中，所选的实例跳转到的环节中的解析的条件值有为空的,办理人以jbpm变量 TASK_JUMP_SELECT_USER的值为准
						for(String fieldValue:obj.toString().split(",")){
							fieldValue = fieldValue.trim();
							if(StringUtils.isNotEmpty(fieldValue)){
								if("ALLCOMPANYID".equals(fieldValue)){//所有人员(不包含无部门人员),只有在条件为【人员  等于 “ 标准字段的值”】时才有用
									List<Long> userids = ApiFactory.getAcsService().getAllUserIdsWithoutAdminByCompany();
									users.addAll(Arrays.asList(userids.toString().replace("[", "").replace("]", "").replace(" ", "").split(",")));
								}else if("ALLWORKGROUP".equals(fieldValue)){//所有工作组中的人员,只有在条件为【人员  等于 “ 标准字段的值”】时才有用
									List<Long> userids = ApiFactory.getAcsService().getUserIdsByWorkgroup(ContextUtils.getCompanyId());
									users.addAll(Arrays.asList(userids.toString().replace("[", "").replace("]", "").replace(" ", "").split(",")));
								}else{
									users.add(fieldValue);
								}
							}
						}
					}else{
						throw new RuntimeException("办理人设置中，条件筛选【"+userCondition+"】时，没有找到指定的办理人");
					}
				}
				candidates.addAll(users);
			}else{//环节跳转时，且解析的条件值为空时，让流程管理员指定办理人
				candidates.add(CommonStrings.TRANSACTOR_ASSIGNMENT);
			}
		}
		return candidates;
	}
	
	private static String getFieldValue(Map<String,String> param,Long companyId,String fieldName){
		String fieldValues = null;
		WorkflowInstanceManager manager = (WorkflowInstanceManager) ContextUtils.getBean("workflowInstanceManager");
		WorkflowInstance wi = manager.getWorkflowInstance(param.get(PROCESS_INSTANCEID),companyId);
		FormViewManager formViewManager = (FormViewManager)ContextUtils.getBean("formViewManager");
		TableColumnManager tableColumnManager = (TableColumnManager)ContextUtils.getBean("tableColumnManager");
		//根据流程实例ID获取流程表单中指定字段的值
		FormView form = formViewManager.getFormView(wi.getFormId());
		TableColumn column=tableColumnManager.getTableColumnByColName(form.getDataTable().getId(), fieldName);
		Map dataMap = formViewManager.getDataMap(form.getDataTable().getName(), wi.getDataId());
		if(column!=null){
			Object objValues = dataMap.get(column.getDbColumnName());
			if(objValues!=null)fieldValues = objValues.toString();
		}
		return fieldValues;
	}
	
	/*
	 * 根据附加条件过滤办理人
	 * @param conditions
	 * @param candidates
	 * @param creator
	 * @param _wf_transactor
	 * @return
	 */
	public static  Set<String> processAdditionalCondition(
			Map<TaskTransactorCondition, String> conditions, Set<Long> candidates, 
			String creator,Long creatorId, OpenExecution execution){
		
		Set<Long> results = new HashSet<Long>();
		String selectOne = conditions.get(TaskTransactorCondition.SELECT_ONE_FROM_MULTIPLE);
		String onlyInCreatorDept = conditions.get(TaskTransactorCondition.ONLY_IN_CREATOR_DEPARTMENT);
		String withCreatorDept = conditions.get(TaskTransactorCondition.WITH_CREATOR_DEPARTMENT);
		if("true".equals(onlyInCreatorDept) || "true".equals(withCreatorDept)){
			List<User> usersInSameDept = new ArrayList<User>();
			
			if(creatorId==null){
				User user = ApiFactory.getAcsService().getUserByLoginName(creator);
				if(user!=null)creatorId = user.getId();
			}
			usersInSameDept = ApiFactory.getAcsService().getUsersInSameDept(creatorId);
			
			//只能为创建人部门(交集)
			if("true".equals(onlyInCreatorDept)){
				for(User u : usersInSameDept){
					if(candidates.contains(u.getId())){
						results.add(u.getId());
					}
				}
			}else{
				results.addAll(candidates);
			}
			//创建人部门参与(并集)
			if("true".equals(withCreatorDept)){
				for(User u : usersInSameDept){
						results.add(u.getId());
				}
			}
		}else{
			results.addAll(candidates);
		}
		Set<String> latest = new HashSet<String>();
		//需要唯一指定办理人
		if("true".equals(selectOne)){
//			  <select-type>autoType</select-type>
//	          <select-bean>workflowInstanceManager</select-bean>
				//只有一个候选人
				if(conditions.get(TaskTransactorCondition.SELECT_TYPE).equals(TaskTransactorCondition.SELECT_TYPE_CUSTOM)){
//					if(results.size() == 1){
//						latest.add(results.iterator().next());
//					}else if(results.size() > 1){
						latest.add(CommonStrings.TRANSACTOR_SINGLE);
						//将候选人加入到execution变量中
						execution.createVariable(CommonStrings.TRANSACTOR_SINGLE_CANDIDATES , results);
//					}
				}else{
					WorkflowInstanceManager workflowInstanceManager = (WorkflowInstanceManager)ContextUtils.getBean("workflowInstanceManager");
					WorkflowInstance instance = workflowInstanceManager.getWorkflowInstance(execution.getProcessInstance().getProcessInstance().getId());
					if(instance==null){throw new RuntimeException("办理人设置中，解析附加条件时，流程实例不能为null");}
					String selectorBeanName = conditions.get(TaskTransactorCondition.SELECT_BEAN);
					ActivityExecution activityExecution = (ActivityExecution)execution;
					String activityName = activityExecution.getActivityName();
					if(StringUtils.isEmpty(selectorBeanName))throw new WorkflowException("环节："+activityName+"没有指定自动选择办理人的bean名");
					SingleTransactorSelector selector = (SingleTransactorSelector)ContextUtils.getBean(selectorBeanName)	;
					boolean moreTransactor = DefinitionXmlParse.hasMoreTransactor(
			    			instance.getProcessDefinitionId(), activityName);
					Set<Long> result=selector.filter(instance.getDataId(), results,moreTransactor);
					Iterator<Long> it=result.iterator();
					while(it.hasNext()){
						latest.add(it.next()==null?"":it.next()+"");
					}
				}
		}else{
			for(Long userid : results){
				latest.add(userid==null?"":userid+"");
			}
		}
		return latest;
	}
}
