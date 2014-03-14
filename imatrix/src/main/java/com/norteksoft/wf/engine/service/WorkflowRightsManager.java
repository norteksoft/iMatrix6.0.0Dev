package com.norteksoft.wf.engine.service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import com.norteksoft.mms.form.entity.FormView;
import com.norteksoft.mms.form.service.FormViewManager;
import com.norteksoft.product.api.ApiFactory;
import com.norteksoft.product.api.entity.TaskPermission;
import com.norteksoft.product.api.entity.User;
import com.norteksoft.product.api.entity.WorkflowInstance;
import com.norteksoft.product.api.impl.WorkflowClientManager;
import com.norteksoft.product.util.ContextUtils;
import com.norteksoft.task.base.enumeration.TaskState;
import com.norteksoft.product.api.entity.WorkflowTask;
import com.norteksoft.wf.engine.core.ConditionParseUtil;
import com.norteksoft.wf.engine.core.DefinitionXmlParse;
import com.norteksoft.wf.engine.core.impl.UserParseCalculator;
import com.norteksoft.wf.engine.entity.WorkflowDefinition;

/**
 * 管理流程权限
 * @author Administrator
 *
 */
@Service
@Transactional
public class WorkflowRightsManager {
	private Log log = LogFactory.getLog(WorkflowClientManager.class);
    private FormViewManager formViewManager;
    private WorkflowDefinitionManager workflowDefinitionManager;
    private TaskService taskService;
    private DelegateMainManager delegateManager;
    
    @Autowired
    public void setTaskService(TaskService taskService) {
		this.taskService = taskService;
	}
    @Autowired
    public void setWorkflowDefinitionManager(
			WorkflowDefinitionManager workflowDefinitionManager) {
		this.workflowDefinitionManager = workflowDefinitionManager;
	}
    @Autowired
    public void setFormViewManager(FormViewManager formManager) {
		this.formViewManager = formManager;
	}
    @Autowired
    public void setDelegateManager(DelegateMainManager delegateManager) {
		this.delegateManager = delegateManager;
	}
    /**
	 * 根据流程名称查询第一环节的字段编辑权限,以JSON格式返回
	 * @param processName
	 * @return
	 */
    public String getFieldPermissionNotStarted(WorkflowDefinition definition){
    	Assert.notNull(definition, "WorkflowDefinition实体不能为null");
		String firstTaskName = DefinitionXmlParse.getFirstTaskName(definition.getProcessId());
		boolean editable = DefinitionXmlParse.haveEditRight(definition.getProcessId(), firstTaskName);
		StringBuilder builder = new StringBuilder("[");
		if(editable){
			getFieldList(builder,DefinitionXmlParse.getRequiredFields(definition.getProcessId(),firstTaskName).keySet(),Boolean.TRUE);
			getFieldList(builder,DefinitionXmlParse.getNonEditableFields(definition.getProcessId(), firstTaskName).keySet(),Boolean.FALSE);
		}
		
		return  org.apache.commons.lang.StringUtils.removeEnd(builder.toString(), ",")+"]";
	}
    
    
    private void getFieldList(StringBuilder builder,Collection<String> collections,boolean request){
    	for(String str:collections){
    		formateFields(builder,str,request);
    		builder.append(",");
    	}
    }
    
    private void formateFields(StringBuilder builder,String fieldKey,boolean request){
		String[] strs = fieldKey.split(":");
		if(strs.length==1){//兼容bkyoa历史版本
			builder.append( "{request:\"").append(request)
			.append("\",readonly:\"").append((!request))
			.append("\",id:\"").append(strs[0])
			.append("\",datatype:\"")
			.append("TEXT")
			.append("\",controlType:\"")
			.append("TEXT")
			.append("\",formatType:\"null\"}");
		}else if(strs.length==2){//兼容bkyoa历史版本
			builder.append( "{request:\"").append(request)
			.append("\",readonly:\"").append((!request))
			.append("\",id:\"").append(strs[0])
			.append("\",datatype:\"");
			if(StringUtils.isEmpty(strs[1])){//默认是文本类型
				builder.append("TEXT");
			}else{
				builder.append(strs[1]);
			}
			builder.append("\",controlType:\"")
			.append("TEXT")
			.append("\",formatType:\"null\"}");
		}else if(strs.length==3){//兼容bkyoa历史版本
			builder.append( "{request:\"").append(request)
			.append("\",readonly:\"").append((!request))
			.append("\",id:\"").append(strs[0])
			.append("\",datatype:\"");
			if(StringUtils.isEmpty(strs[1])){//默认是文本类型
				builder.append("TEXT");
			}else{
				builder.append(strs[1]);
			}
			builder.append("\",controlType:\"");
			if(StringUtils.isEmpty(strs[2])){//默认是文本框
				builder.append("TEXT");
			}else{
				builder.append(strs[2]);
			}
			builder.append("\",formatType:\"null\"}");
		}else if(strs.length>=4){//新版本中
			builder.append( "{request:\"").append(request)
					.append("\",readonly:\"").append((!request))
					.append("\",id:\"").append(strs[0])
					.append("\",datatype:\"");
					if(StringUtils.isEmpty(strs[1])){//默认是文本类型
						builder.append("TEXT");
					}else{
						builder.append(strs[1]);
					}
					builder.append("\",controlType:\"");
					if(StringUtils.isEmpty(strs[2])){//默认是文本框
						builder.append("TEXT");
					}else{
						builder.append(strs[2]);
					}
					builder.append("\",name:\"");
					builder.append(strs[3]);
					builder.append("\",formatType:\"null");
					if(strs.length>4){
						builder.append("\",customType:\"");
						builder.append(strs[4]);
						builder.append("\",format:\"");
						builder.append(strs[5]);
					}
					builder.append("\"}");
		}
	}
    
    public Collection<String> getforbiddenFieldsNotStarted(WorkflowDefinition definition){
    	Assert.notNull(definition, "WorkflowDefinition实体不能为null");
		String firstTaskName = DefinitionXmlParse.getFirstTaskName(definition.getProcessId());
		boolean editable = DefinitionXmlParse.haveEditRight(definition.getProcessId(), firstTaskName);
		if(editable){
			return getFieldLists(DefinitionXmlParse.getNonEditableFields(definition.getProcessId(), firstTaskName).keySet());
		}else{
			return new ArrayList<String>(); 
		}
    }
    /*
     * 将静止填写字段中类型标识去掉
     */
    private Collection<String> getFieldLists(Collection<String> keys){
    	Collection<String> set = new HashSet<String>();
    	for(String key:keys){
    		set.add(key.split(":")[0].trim());
    	}
    	return set;
    }
    
    public Collection<String> getNeedFillFieldsNotStarted(WorkflowDefinition definition){
    	Assert.notNull(definition, "WorkflowDefinition实体不能为null");
		String firstTaskName = DefinitionXmlParse.getFirstTaskName(definition.getProcessId());
		boolean editable = DefinitionXmlParse.haveEditRight(definition.getProcessId(), firstTaskName);
		if(editable){
			return getFieldLists(DefinitionXmlParse.getRequiredFields(definition.getProcessId(),firstTaskName).keySet());
		}else{
			return new ArrayList<String>();
		}
    }
    
    /**
     * 获得该环节对字段的编辑权限
     * @param instance
     * @param taskName
     * @return json表示的可编辑字段和不可编辑字段
     */
    public String getFieldPermission(com.norteksoft.product.api.entity.WorkflowTask task){
    	WorkflowInstance instance = ApiFactory.getInstanceService().getInstance(task.getProcessInstanceId());
		boolean editable = DefinitionXmlParse.haveEditRight(instance.getProcessDefinitionId(), task.getName());
		
		StringBuilder builder = new StringBuilder("[");
		if(editable){
			getRequiredFields(builder,task,Boolean.TRUE);
			getRequiredFields(builder,task,Boolean.FALSE);
		}else{
			builder.append( "{request:\"").append(editable)
			.append("\",readonly:\"").append((!editable))
			.append("\",controlType:\"allReadolny\"}");
		}
		
		return  org.apache.commons.lang.StringUtils.removeEnd(builder.toString(), ",")+"]";
    }
    
    public List<String> getforbiddenFields( WorkflowTask task){
    	Assert.notNull(task, "WorkflowTask实体不能为null");
    	WorkflowInstance instance =ApiFactory.getInstanceService().getInstance(task.getProcessInstanceId());
    	if(instance==null){
			log.debug("WorkflowInstance实体不能为null");
			throw new RuntimeException("WorkflowInstance实体不能为null");
		}
		boolean editable = DefinitionXmlParse.haveEditRight(instance.getProcessDefinitionId(), task.getName());
		if(editable){
			return getNonEditableFieldsNoType(task);
		}else{
			return  new ArrayList<String>();
		}
		
    }
    
    public List<String> getNeedFillFields(WorkflowTask task){
    	WorkflowInstance instance =ApiFactory.getInstanceService().getInstance(task.getProcessInstanceId());
		boolean editable = DefinitionXmlParse.haveEditRight(instance.getProcessDefinitionId(), task.getName());
		if(editable){
			return getRequiredFieldsNotType(task);
		}else{
			return  new ArrayList<String>();
		}
		
    }
    
    
    /**
	 * 所有字段可编辑状态信息查询
	 * @param editable 当editable为false时 表示所有字段都禁止填写
	 * @return 返回json格式表示的字段可编辑状态信息
	 */
	public String getFieldPermission(boolean editable){
		return new StringBuilder("[").append( "{request:\"").append(editable)
		.append("\",readonly:\"").append((!editable))
		.append("\",controlType:\"allReadolny\"}]").toString();
	}
    
    /**
     * 表单打印权限（流程启用后使用）
     * @param instance
     * @param taskName
     * @return true为有权限打印，否则为没有权限
     */
   public boolean printFormRight(WorkflowTask task){
	   WorkflowInstance instance = ApiFactory.getInstanceService().getInstance(task.getProcessInstanceId());
		String[] rightSetting = DefinitionXmlParse.getPrintFormRight(instance.getProcessDefinitionId(), task.getName());
		return havaRight(task,rightSetting);
   }
   
   public boolean formPrintRightNotStarted(WorkflowDefinition definition){
		String firstTaskName =  DefinitionXmlParse.getFirstTaskName(definition.getProcessId());
		String[] rightSetting = DefinitionXmlParse.getPrintFormRight(definition.getProcessId(), firstTaskName);
		return havaRight(rightSetting);
   }
   /**
    * 流转历史查看权限（流程启用后使用）
    * @param instance
    * @param taskName
    * @return true为有权限打印，否则为没有权限
    */
	public boolean viewFlowHistoryRight(WorkflowTask task){
		WorkflowInstance instance = ApiFactory.getInstanceService().getInstance(task.getProcessInstanceId());
		if(viewFlowHistoryRightInProcess(instance)) return true;
		String[] rightSetting = DefinitionXmlParse.getViewFlowHistoryRight(instance.getProcessDefinitionId(), task.getName());
		return havaRight(task,rightSetting);
	}
  
	public boolean viewFlowHistoryRightInProcess(WorkflowInstance instance){
		String condition = DefinitionXmlParse.getProcessHistoryPermissions(instance.getProcessDefinitionId());
		if(StringUtils.isEmpty(condition))return false;
		WorkflowDefinition definition = workflowDefinitionManager.getWfDefinition(instance.getWorkflowDefinitionId());
		UserParseCalculator upc = new UserParseCalculator();
		upc.setDataId(instance.getDataId());
		upc.setProcessAdmin(definition.getAdminLoginName());
		upc.setProcessAdminId(definition.getAdminId());
		upc.setDocumentCreator(instance.getCreator());
		upc.setDocumentCreatorId(instance.getCreatorId());
		upc.setHandledTransactors(taskService.getHandledTransactors(instance.getProcessInstanceId()));
		upc.setHandledTransactorIds(taskService.getHandledTransactorIds(instance.getProcessInstanceId()));
		upc.setAllHandleTransactors(taskService.getAllHandleTransactors(instance.getProcessInstanceId()));
		upc.setAllHandleTransactorIds(taskService.getAllHandleTransactorIds(instance.getProcessInstanceId()));
		Set<Long> users = upc.getUsers(condition,instance.getSystemId(),instance.getCompanyId());
		return users.contains(ContextUtils.getUserId());
	}
  
  /**
   * 意见查看权限（流程启用后使用）
   * @param instance
   * @param taskName
   * @return true为有权限打印，否则为没有权限
   */
	 public boolean viewOpinionRight(WorkflowTask task){
		 WorkflowInstance instance = ApiFactory.getInstanceService().getInstance(task.getProcessInstanceId());
		if(editOpinionRight(task))return true;
		String[] rightSetting = DefinitionXmlParse.getViewOpinion(instance.getProcessDefinitionId(), task.getName());
		return havaRight(task,rightSetting);
	 }
	 
	/**
	 * 意见编辑权限（流程启用后使用）
	 * @param instance
	 * @param taskName
	 * @return true为有权限打印，否则为没有权限
	 */
	public boolean editOpinionRight(WorkflowTask task){
		WorkflowInstance instance = ApiFactory.getInstanceService().getInstance(task.getProcessInstanceId());
		if(mustOpinionRight(task)) return true;
		String[] rightSetting = DefinitionXmlParse.getEditOpinion(instance.getProcessDefinitionId(), task.getName());
		return havaRight(task,rightSetting);
	}
	
	/**
	 * @param definition
	 * @return
	 */
	public boolean editOpinionRightNotStarted(WorkflowDefinition definition){
		String firstTaskName =  DefinitionXmlParse.getFirstTaskName(definition.getProcessId());
		String[] rightSetting = DefinitionXmlParse.getEditOpinion(definition.getProcessId(), firstTaskName);
		return havaRight(rightSetting);
	}
	
	public boolean mustOpinionRightNotStarted(WorkflowDefinition definition){
		String firstTaskName =  DefinitionXmlParse.getFirstTaskName(definition.getProcessId());
		String[] rightSetting = DefinitionXmlParse.getMustOpinion(definition.getProcessId(), firstTaskName);
		return havaRight(rightSetting);
	}
	
	/**
	 * 意见必填权限（流程启用后使用）
	 * @param instance
	 * @param taskName
	 * @return true为有权限打印，否则为没有权限
	 */
	public boolean mustOpinionRight(WorkflowTask task){
		WorkflowInstance instance = ApiFactory.getInstanceService().getInstance(task.getProcessInstanceId());
		String[] rightSetting = DefinitionXmlParse.getMustOpinion(instance.getProcessDefinitionId(), task.getName());
		return havaRight(task,rightSetting);
	}
	
	/**
	 * 流程删除权限
	 * @param instance
	 * @param taskName
	 * @return
	 */
	public boolean workflowDeleteRight(WorkflowInstance instance,String taskName){
		 com.norteksoft.task.entity.WorkflowTask task = taskService.getMyTask(instance.getProcessInstanceId(), instance.getCompanyId(), getLoginName());
		String currentTrasactor="";
		Long currentTrasactorId=null;
		if(task!=null){
			if(task.getName().equals(taskName)){//当前用户的当前任务名是taskName时，则当前办理人为当前用户
				currentTrasactor=getLoginName();
				currentTrasactorId = ContextUtils.getUserId();
			}
		}
		if(StringUtils.isEmpty(currentTrasactor)){//判断当前用户为空时，取任一当前任务名为taskName的任务的办理人为当前办理人
			List< com.norteksoft.task.entity.WorkflowTask> tasks= taskService.getActivityTasks(instance.getProcessInstanceId(), instance.getCompanyId());
			if(tasks.size()>0){
				task = tasks.get(0);
				if(task.getName().equals(taskName)){
					if(StringUtils.isNotEmpty(task.getTrustor())){
						currentTrasactor=tasks.get(0).getTrustor();
						currentTrasactorId = tasks.get(0).getTrustorId();
					}else{
						currentTrasactor=tasks.get(0).getTransactor();
						currentTrasactorId = tasks.get(0).getTransactorId();
					}
				}
			}
		}
		String condition = DefinitionXmlParse.getDeleteInstancePermissionsInTask(instance.getProcessDefinitionId(), taskName);
		FormView form = formViewManager.getFormView(instance.getFormId());
		WorkflowDefinition definition = workflowDefinitionManager.getWfDefinition(instance.getWorkflowDefinitionId());
		UserParseCalculator upc = new UserParseCalculator();
		upc.setDataId(instance.getDataId());
		upc.setFormView(form);
		upc.setCurrentTransactor(currentTrasactor);
		upc.setCurrentTransactorId(currentTrasactorId);
		upc.setProcessAdmin(definition.getAdminLoginName());
		upc.setProcessAdminId(definition.getAdminId());
		upc.setDocumentCreator(instance.getCreator());
		upc.setDocumentCreatorId(instance.getCreatorId());
		Set<Long> users = upc.getUsers(condition==null?"":condition,instance.getSystemId(),instance.getCompanyId());
		return users.contains(ContextUtils.getUserId());
	}
	
	/**
	 * 会签结果查看权限（流程启用后使用）
	 * @param instance
	 * @param taskName
	 * @return true为有权限，否则为没有权限
	 */
	public boolean viewMeetingResultRight(WorkflowTask task){
		WorkflowInstance instance = ApiFactory.getInstanceService().getInstance(task.getProcessInstanceId());
		if(viewMeetingResultRightInProcess(instance)) return true;
		String[] rightSetting = DefinitionXmlParse.getViewMeetingResultRight(instance.getProcessDefinitionId(), task.getName());
		return havaRight(task,rightSetting);
	}
	
	public boolean viewMeetingResultRightInProcess(WorkflowInstance instance){
		String condition = DefinitionXmlParse.getProcessMeetingPermissions(instance.getProcessDefinitionId());
		if(StringUtils.isEmpty(condition))return false;
		WorkflowDefinition definition = workflowDefinitionManager.getWfDefinition(instance.getWorkflowDefinitionId());
		UserParseCalculator upc = new UserParseCalculator();
		upc.setDataId(instance.getDataId());
		upc.setProcessAdmin(definition.getAdminLoginName());
		upc.setProcessAdminId(definition.getAdminId());
		upc.setDocumentCreator(instance.getCreator());
		upc.setDocumentCreatorId(instance.getCreatorId());
		upc.setHandledTransactors(taskService.getHandledTransactors(instance.getProcessInstanceId()));
		upc.setHandledTransactorIds(taskService.getHandledTransactorIds(instance.getProcessInstanceId()));
		upc.setAllHandleTransactors(taskService.getAllHandleTransactors(instance.getProcessInstanceId()));
		upc.setAllHandleTransactorIds(taskService.getAllHandleTransactorIds(instance.getProcessInstanceId()));
		Set<Long> users = upc.getUsers(condition,definition.getSystemId(),definition.getCompanyId());
		return users.contains(ContextUtils.getUserId());
	}
	
	/**
	 * 投票结果查看权限（流程启用后使用）
	 * @param instance
	 * @param taskName
	 * @return true为有权限，否则为没有权限
	 */
	public boolean viewVoteResultRight(WorkflowTask task){
		WorkflowInstance instance = ApiFactory.getInstanceService().getInstance(task.getProcessInstanceId());
		if(viewVoteResultRightInProcess(instance)) return true;
		String[] rightSetting = DefinitionXmlParse.getViewVoteResultRight(instance.getProcessDefinitionId(), task.getName());
		return havaRight(task,rightSetting);
	}
  
	public boolean viewVoteResultRightInProcess(WorkflowInstance instance){
		String condition = DefinitionXmlParse.getProcessVotePermissions(instance.getProcessDefinitionId());
		if(StringUtils.isEmpty(condition))return false;
		WorkflowDefinition definition = workflowDefinitionManager.getWfDefinition(instance.getWorkflowDefinitionId());
		UserParseCalculator upc = new UserParseCalculator();
		upc.setDataId(instance.getDataId());
		upc.setProcessAdmin(definition.getAdminLoginName());
		upc.setProcessAdminId(definition.getAdminId());
		upc.setDocumentCreator(instance.getCreator());
		upc.setDocumentCreatorId(instance.getCreatorId());
		upc.setHandledTransactors(taskService.getHandledTransactors(instance.getProcessInstanceId()));
		upc.setHandledTransactorIds(taskService.getHandledTransactorIds(instance.getProcessInstanceId()));
		upc.setAllHandleTransactors(taskService.getAllHandleTransactors(instance.getProcessInstanceId()));
		upc.setAllHandleTransactorIds(taskService.getAllHandleTransactorIds(instance.getProcessInstanceId()));
		Set<Long> users = upc.getUsers(condition,instance.getSystemId(),instance.getCompanyId());
		return users.contains(ContextUtils.getUserId());
	}
	
	/**
	 * 上传附件的权限
	 */
	public boolean attachmentAddRight(WorkflowTask task){
		WorkflowInstance instance = ApiFactory.getInstanceService().getInstance(task.getProcessInstanceId());
		String[] rightSetting = DefinitionXmlParse.getAttachmentAddCondition(instance.getProcessDefinitionId(), task.getName());
		return havaRight(task,rightSetting);
		
	}
	
	/**
	 * 删除附件的权限
	 */
	public boolean attachmentDeleteRight(WorkflowTask task){
		WorkflowInstance instance = ApiFactory.getInstanceService().getInstance(task.getProcessInstanceId());
		String[] rightSetting = DefinitionXmlParse.getAttachmentDeleteCondition(instance.getProcessDefinitionId(), task.getName());
		return havaRight(task,rightSetting);
	}
	
	/**
	 * 下载附件的权限
	 */
	public boolean attachmentDownloadRight(WorkflowTask task){
		WorkflowInstance instance = ApiFactory.getInstanceService().getInstance(task.getProcessInstanceId());
		String[] rightSetting  = DefinitionXmlParse.getAttachmentDownloadCondition(instance.getProcessDefinitionId(), task.getName());
		return havaRight(task,rightSetting);
	}
	
	/**
	 * 流程未启动是查询附件添加权限
	 * @param definition
	 * @return
	 */
	public boolean attachmentAddRightNotStarted(WorkflowDefinition definition){
		String firstTaskName =  DefinitionXmlParse.getFirstTaskName(definition.getProcessId());
		String[] rightSetting = DefinitionXmlParse.getAttachmentAddCondition(definition.getProcessId(), firstTaskName);
		return havaRight(rightSetting);
	}
	
	/**
	 * 流程未启动是查询附件删除权限
	 * @param definition
	 * @return
	 */
	public boolean attachmentDeleteRightNotStarted(WorkflowDefinition definition){
		String firstTaskName =  DefinitionXmlParse.getFirstTaskName(definition.getProcessId());
		String[] rightSetting = DefinitionXmlParse.getAttachmentDeleteCondition(definition.getProcessId(), firstTaskName);
		return havaRight(rightSetting);
	}
	
	public boolean attachmentDownloadRightNotStarted(WorkflowDefinition definition){
		String firstTaskName =  DefinitionXmlParse.getFirstTaskName(definition.getProcessId());
		String[] rightSetting = DefinitionXmlParse.getAttachmentDownloadCondition(definition.getProcessId(), firstTaskName);
		return havaRight(rightSetting);
	}
	
	/**
	 * 正文是否创建
	 * @param instance
	 * @param taskName
	 * @return 权限
	 */
	public boolean officialTextCreateRight(WorkflowTask task){
		WorkflowInstance instance = ApiFactory.getInstanceService().getInstance(task.getProcessInstanceId());
		String[] rightSetting = DefinitionXmlParse.getOfficialTextCreateCondition(instance.getProcessDefinitionId(), task.getName());
		return havaRight(task,rightSetting);
	}
	
	/**
	 * 删除正文权限
	 * @param instance
	 * @param taskName
	 * @return 权限
	 */
	public boolean officialTextDeleteRight(WorkflowTask task){
		WorkflowInstance instance = ApiFactory.getInstanceService().getInstance(task.getProcessInstanceId());
		String[] rightSetting = DefinitionXmlParse.getOfficialTextDeleteCondition(instance.getProcessDefinitionId(), task.getName());
		return havaRight(task,rightSetting);
	}
	
	/**
	 * 正文下载权限
	 * @param instance
	 * @param taskName
	 * @return 权限
	 */
	public boolean officialTextDownloadRight(WorkflowTask task){
		WorkflowInstance instance = ApiFactory.getInstanceService().getInstance(task.getProcessInstanceId());
		String[] rightSetting = DefinitionXmlParse.getOfficialTextDownloadSetting(instance.getProcessDefinitionId(), task.getName());
		return havaRight(task,rightSetting);
	}
	
	/**
	 * 正文打印权限
	 * @param instance
	 * @param taskName
	 * @return 权限
	 */
	public boolean officialTextPrintRight(WorkflowTask task){
		WorkflowInstance instance = ApiFactory.getInstanceService().getInstance(task.getProcessInstanceId());
		String[] rightSetting = DefinitionXmlParse.getOfficialTextPrintSetting(instance.getProcessDefinitionId(), task.getName());
		return havaRight(task,rightSetting);
	}
	
	/**
	 * 正文编辑权限
	 * @param instance
	 * @param taskName
	 * @return 权限
	 */
	public boolean officialTextEditRight(WorkflowTask task){
		WorkflowInstance instance = ApiFactory.getInstanceService().getInstance(task.getProcessInstanceId());
		String[] rightSetting = DefinitionXmlParse.getOfficialTextEditSetting(instance.getProcessDefinitionId(), task.getName());
		return havaRight(task,rightSetting);
	}
	
	/**
	 * 编辑正文时是否保留痕迹
	 * @param instance
	 * @param taskName
	 * @return
	 */
	public boolean officialTextRetainTrace(WorkflowTask task){
		WorkflowInstance instance = ApiFactory.getInstanceService().getInstance(task.getProcessInstanceId());
		if(officialTextEditRight(task)){
			String[] rightSetting = DefinitionXmlParse.getOfficialTextEditSetting(instance.getProcessDefinitionId(), task.getName());
			return Boolean.valueOf(rightSetting[2]);
		}else{
			return false;
		}
	}
	
	/**
	 * 编辑正文时是否保留痕迹
	 * @param instance
	 * @param taskName
	 * @return
	 */
	public boolean officialTextViewTrace(WorkflowTask task){
		WorkflowInstance instance = ApiFactory.getInstanceService().getInstance(task.getProcessInstanceId());
		String[] editRightSetting = DefinitionXmlParse.getOfficialTextEditSetting(instance.getProcessDefinitionId(), task.getName());
		boolean viewRightSetting = DefinitionXmlParse.getOfficialTextViewSetting(instance.getProcessDefinitionId(), task.getName());
		return Boolean.valueOf(editRightSetting[3])||viewRightSetting;
	}
	
	public boolean officialTextCreateRightNotStarted(WorkflowDefinition definition){
		String firstTaskName =  DefinitionXmlParse.getFirstTaskName(definition.getProcessId());
		String[] rightSetting = DefinitionXmlParse.getOfficialTextCreateCondition(definition.getProcessId(), firstTaskName);
		return havaRight(rightSetting);
	}
	
	private boolean havaRight(String[] rightSetting){
		boolean result = false;
		if(rightSetting[0].equalsIgnoreCase(DefinitionXmlParse.RIGHT_ALLOW)){
			result = true;
		}else{
			result = false;
		}
		return result;
	}
	
	private boolean havaRight(WorkflowTask task,String[] rightSetting){
		boolean result = false;
		if(rightSetting==null)return result;
		if(rightSetting[0].equalsIgnoreCase(DefinitionXmlParse.RIGHT_ALLOW)){
			result =  ConditionParseUtil.parseCondition(rightSetting[1],getUserParseCalculator(task));
		}else if(rightSetting[0].equalsIgnoreCase(DefinitionXmlParse.RIGHT_UNALLOW)){
			result =  !ConditionParseUtil.parseCondition(rightSetting[1],getUserParseCalculator(task));
		}
		return result;
	}
	
	private UserParseCalculator getUserParseCalculator(WorkflowTask task){
		WorkflowInstance instance = ApiFactory.getInstanceService().getInstance(task.getProcessInstanceId());
		UserParseCalculator calculator = new UserParseCalculator();
		if(StringUtils.isEmpty(task.getTrustor())){
			calculator.setCurrentTransactor(ContextUtils.getLoginName());
			calculator.setCurrentTransactorId(ContextUtils.getUserId());
		}else{//当是委托任务时，当前办理人为委托人
			calculator.setCurrentTransactor(task.getTrustor());
			calculator.setCurrentTransactorId(task.getTrustorId());
			//注释以下代码的原因是：当人员A把某一环节的任务委托给人员B时，人员B收到任务后指派给人员C,人员C收到任务后查看任务，然后关闭任务，这时待办事宜列表中的任务名称就不再是“A委托+任务名”了，而是”任务名“，造成这种情况是因为trustorName被置为null了。
			//注释start
//			String delegateTransactor = delegateManager.getDelegateMainName(
//					task.getCompanyId(), task.getTrustor(),instance.getProcessDefinitionId() , task.getName());
//			if(StringUtils.isNotEmpty(delegateTransactor)&&delegateTransactor.equals(task.getTransactor())){
//				calculator.setCurrentTransactor(task.getTrustor());
//			}else{
//				task.setTrustor(null);
//				task.setTrustorName(null);
//				calculator.setCurrentTransactor(ContextUtils.getLoginName());
//			}
			//注释end
		}
		calculator.setDataId(instance.getDataId());
		calculator.setDocumentCreator(instance.getCreator());
		calculator.setDocumentCreatorId(instance.getCreatorId());
		calculator.setFormView(formViewManager.getFormView(instance.getFormId()));
		return calculator;
	}
	
	 /*
	 * 返回需要必填的字段
	 */
	private    List<String> getRequiredFieldsNotType(WorkflowTask task){
		WorkflowInstance instance = ApiFactory.getInstanceService().getInstance(task.getProcessInstanceId());
		Map<String,String> requiredFields = DefinitionXmlParse.getRequiredFields(instance.getProcessDefinitionId(),task.getName());
		List<String> list = new ArrayList<String>();
		for(String name: requiredFields.keySet()){
			String userCondition = requiredFields.get(name);
			UserParseCalculator calculator = new UserParseCalculator();
			if(StringUtils.isEmpty(task.getTrustor())){
				calculator.setCurrentTransactor(ContextUtils.getLoginName());
				calculator.setCurrentTransactorId(ContextUtils.getUserId());
			}else{//当是委托任务时，当前办理人为委托人
				User delegateUser = delegateManager.getDelegateUser(
						task.getCompanyId(), task.getTrustor(),task.getTrustorId(),instance.getProcessDefinitionId() , task.getName());
				if(delegateUser!=null&&(delegateUser.getId().equals(task.getTransactorId()) ||delegateUser.getLoginName().equals(task.getTransactor())) ){
					calculator.setCurrentTransactor(task.getTrustor());
					calculator.setCurrentTransactorId(task.getTrustorId());
				}else{
					//注释以下代码的原因是：子流程的任务，在 委托监控-已完成 和受托监控-已完成 中，查看任务时报错；出错的原因是在TaskService.java下方法为getMyFirstTask中的resultTask为null。
					//注释start
//					task.setTrustor(null);
//					task.setTrustorName(null);
					//注释end
					calculator.setCurrentTransactor(ContextUtils.getLoginName());
					calculator.setCurrentTransactorId(ContextUtils.getUserId());
				}
			}
			calculator.setDataId(instance.getDataId());
			calculator.setDocumentCreator(instance.getCreator());
			calculator.setDocumentCreatorId(instance.getCreatorId());
			calculator.setFormView(formViewManager.getFormView(instance.getFormId()));
			if(ConditionParseUtil.parseCondition(userCondition,calculator)){
				list.add(name.split(":")[0].trim());
			}
		}
		return list;
	}
	
	 /*
	 * 返回需要必填的字段
	 */
	private  void getRequiredFields(StringBuilder builder,com.norteksoft.product.api.entity.WorkflowTask task,boolean request){
		WorkflowInstance instance = ApiFactory.getInstanceService().getInstance(task.getProcessInstanceId());
		Map<String,String> requiredFields = null;
		if(request){
			requiredFields = DefinitionXmlParse.getRequiredFields(instance.getProcessDefinitionId(),task.getName());
		}else{
			requiredFields = DefinitionXmlParse.getNonEditableFields(instance.getProcessDefinitionId(), task.getName());
		}
		for(String name: requiredFields.keySet()){
			String userCondition = requiredFields.get(name);
			UserParseCalculator calculator = new UserParseCalculator();
			if(StringUtils.isEmpty(task.getTrustor())){
				calculator.setCurrentTransactor(ContextUtils.getLoginName());
				calculator.setCurrentTransactorId(ContextUtils.getUserId());
			}else{//当是委托任务时，当前办理人为委托人
				User delegateUser = delegateManager.getDelegateUser(
						task.getCompanyId(), task.getTrustor(),task.getTrustorId(),instance.getProcessDefinitionId() , task.getName());
				if(delegateUser!=null&&(delegateUser.getId().equals(task.getTransactorId()) ||delegateUser.getLoginName().equals(task.getTransactor()))){
					calculator.setCurrentTransactor(task.getTrustor());
					calculator.setCurrentTransactorId(task.getTrustorId());
				}else{
					//注释以下代码的原因是：子流程的任务，在 委托监控-已完成 和受托监控-已完成 中，查看任务时报错；出错的原因是在TaskService.java下方法为getMyFirstTask中的resultTask为null。
					//注释start
//					task.setTrustor(null);
//					task.setTrustorName(null);
					//注释end
					calculator.setCurrentTransactor(ContextUtils.getLoginName());
					calculator.setCurrentTransactorId(ContextUtils.getUserId());
				}
			}
			calculator.setDataId(instance.getDataId());
			calculator.setDocumentCreator(instance.getCreator());
			calculator.setDocumentCreatorId(instance.getCreatorId());
			calculator.setFormView(formViewManager.getFormView(instance.getFormId()));
			if(ConditionParseUtil.parseCondition(userCondition,calculator)){
				formateFields(builder,name,request);
				builder.append(",");
			}
		}
	}
    
	/**
	 * 返回不带类型字段id
	 * @param instance
	 * @param taskName
	 * @return
	 */
	private List<String> getNonEditableFieldsNoType(WorkflowTask task){
		WorkflowInstance instance = ApiFactory.getInstanceService().getInstance(task.getProcessInstanceId());
		Map<String,String> requiredFields = DefinitionXmlParse.getNonEditableFields(instance.getProcessDefinitionId(), task.getName());
		List<String> list = new ArrayList<String>();
		for(String name: requiredFields.keySet()){
			String userCondition = requiredFields.get(name);
			UserParseCalculator calculator = new UserParseCalculator();
			if(StringUtils.isEmpty(task.getTrustor())){
				calculator.setCurrentTransactor(ContextUtils.getLoginName());
				calculator.setCurrentTransactorId(ContextUtils.getUserId());
			}else{//当是委托任务时，当前办理人为委托人
				User delegateUser = delegateManager.getDelegateUser(
						task.getCompanyId(), task.getTrustor(),task.getTrustorId(),instance.getProcessDefinitionId() , task.getName());
				//任务的办理人id为null表示是历史任务
				if(delegateUser!=null&&(delegateUser.getId().equals(task.getTransactorId()) ||(task.getTransactorId()==null&&delegateUser.getLoginName().equals(task.getTransactor())))){
					calculator.setCurrentTransactor(task.getTrustor());
					calculator.setCurrentTransactorId(task.getTrustorId());
				}else{
					//注释以下代码的原因是：子流程的任务，在 委托监控-已完成 和受托监控-已完成 中，查看任务时报错；出错的原因是在TaskService.java下方法为getMyFirstTask中的resultTask为null。
					//注释start
//					task.setTrustor(null);
//					task.setTrustorName(null);
					//注释end
					calculator.setCurrentTransactor(ContextUtils.getLoginName());
					calculator.setCurrentTransactorId(ContextUtils.getUserId());
				}
			}
			calculator.setDataId(instance.getDataId());
			calculator.setDocumentCreator(instance.getCreator());
			calculator.setDocumentCreatorId(instance.getCreatorId());
			calculator.setFormView(formViewManager.getFormView(instance.getFormId()));
			if(ConditionParseUtil.parseCondition(userCondition,calculator)){
				list.add(name.split(":")[0]);
			}
		}
		return list;
	}
	
	private String getLoginName(){
		return ContextUtils.getLoginName();
	}
	
	public TaskPermission getActivityPermission(Long taskId){
		if(taskId==null)return new TaskPermission();
		WorkflowTask task = ApiFactory.getTaskService().getTask(taskId);
		if(task==null)return new TaskPermission();
		WorkflowInstance instance = ApiFactory.getInstanceService().getInstance(task.getProcessInstanceId());
		if(instance==null){
			log.debug("WorkflowInstance实体不能为null");
			throw new RuntimeException("WorkflowInstance实体不能为null");
		}
		Map<String,String[]> permissionConditions=DefinitionXmlParse.getActivityPermissionCondition(instance.getProcessDefinitionId(), task.getName()); 
		return getTaskPermission(task,permissionConditions);
		
	}
	public TaskPermission getActivityPermission(WorkflowDefinition definition){
		if(definition==null)return  new TaskPermission();
		String firstTaskName =  DefinitionXmlParse.getFirstTaskName(definition.getProcessId());
		Map<String,String[]> permissionConditions=DefinitionXmlParse.getActivityPermissionCondition(definition.getProcessId(), firstTaskName); 
		return getTaskPermission(null,permissionConditions);
	}
	
	private TaskPermission getTaskPermission(WorkflowTask task,Map<String,String[]> permissionConditions){
		TaskPermission taskPermission = new TaskPermission();
		taskPermission.setAttachmentDownloadable(getPermission(task,permissionConditions.get("attachmentDownloadable")));
		taskPermission.setDocumentDownloadable(getPermission(task,permissionConditions.get("documentDownloadable")));
		taskPermission.setDocumentPrintable(getPermission(task,permissionConditions.get("documentPrintable")));
		taskPermission.setFormPrintable(getPermission(task,permissionConditions.get("formPrintable")));
		if(task!=null){//任务的编辑意见权限
			if(task.getActive().equals(TaskState.WAIT_TRANSACT.getIndex())||task.getActive().equals(TaskState.WAIT_DESIGNATE_TRANSACTOR.getIndex())){//任务未完成时，某些权限的设置
				taskPermission.setAttachmentCreateable(getPermission(task,permissionConditions.get("attachmentCreateable")));
				taskPermission.setAttachmentDeletable(getPermission(task,permissionConditions.get("attachmentDeletable")));
				
				taskPermission.setDocumentCreateable(getPermission(task,permissionConditions.get("documentCreateable")));
				taskPermission.setDocumentDeletable(getPermission(task,permissionConditions.get("documentDeletable")));
				taskPermission.setDocumentEditable(getPermission(task,permissionConditions.get("documentEditable")));
				
				taskPermission.setOpinionEditable(getPermission(task,permissionConditions.get("opinionEditable")));
				taskPermission.setOpinionRequired(getPermission(task,permissionConditions.get("opinionRequired")));
			}else{//任务已完成时，某些权限的设置
				taskPermission.setAttachmentCreateable(false);
				taskPermission.setAttachmentDeletable(false);
				
				taskPermission.setDocumentCreateable(false);
				taskPermission.setDocumentDeletable(false);
				taskPermission.setDocumentEditable(false);
				
				taskPermission.setOpinionEditable(false);
				taskPermission.setOpinionRequired(false);
			}
			taskPermission.setOpinionVisible(getPermission(task,permissionConditions.get("opinionVisible")));
			taskPermission.setDocumentTraceRetainable(getDocumentTraceRetainable(taskPermission.getDocumentEditable(),permissionConditions.get("documentEditable")));
			taskPermission.setDocumentTraceVisible(getDocumentTraceVisible(taskPermission.getDocumentEditable(),permissionConditions.get("documentEditable"),task));
			taskPermission.setDocumentTraceView(getDocumentTraceView(task));
			WorkflowInstance instance = ApiFactory.getInstanceService().getInstance(task.getProcessInstanceId());
			if(viewMeetingResultRightInProcess(instance)) {
				taskPermission.setCountersignResultVisible(true);
			}else{
				taskPermission.setCountersignResultVisible(getPermission(task,permissionConditions.get("countersignResultVisible")));
			}
			if(viewFlowHistoryRightInProcess(instance)) {
				taskPermission.setHistoryVisible(true);
			}else{
				taskPermission.setHistoryVisible(getPermission(task,permissionConditions.get("historyVisible")));
			}
			if(viewVoteResultRightInProcess(instance)){
				taskPermission.setVoteResultVisible(true);
			}else{
				taskPermission.setVoteResultVisible(getPermission(task,permissionConditions.get("voteResultVisible")));
			}
		}else{//流程未发起时
			taskPermission.setAttachmentCreateable(getPermission(task,permissionConditions.get("attachmentCreateable")));
			taskPermission.setAttachmentDeletable(getPermission(task,permissionConditions.get("attachmentDeletable")));
			
			taskPermission.setDocumentCreateable(getPermission(task,permissionConditions.get("documentCreateable")));
			
			taskPermission.setOpinionEditable(getPermission(task,permissionConditions.get("opinionEditable")));
			taskPermission.setOpinionRequired(getPermission(task,permissionConditions.get("opinionRequired")));
			taskPermission.setOpinionVisible(getPermission(task,permissionConditions.get("opinionVisible")));
		}
		return taskPermission;
	}
	
	private boolean getPermission(WorkflowTask task,String[] rightSetting ){
		if(task==null){//流程未发起时
			return havaRight(rightSetting);
		}else{//办理任务过程中，判断任务的权限
			return havaRight(task,rightSetting);
		}
	}
	
	private boolean getDocumentTraceRetainable(boolean editable,String[] rightSetting){
		if(editable){
			return Boolean.valueOf(rightSetting[2]);
		}else{
			return false;
		}
	}
	
	private boolean getDocumentTraceVisible(boolean editable,String[] rightSetting,WorkflowTask task){
		WorkflowInstance instance = ApiFactory.getInstanceService().getInstance(task.getProcessInstanceId());
		boolean viewRightSetting = DefinitionXmlParse.getOfficialTextViewSetting(instance.getProcessDefinitionId(), task.getName());
		if(editable){
			return Boolean.valueOf(rightSetting[3])||viewRightSetting;
		}else{
			return viewRightSetting;
		}
	}
	
	/**
	 * 是否显示编辑痕迹
	 * @param instance
	 * @param taskName
	 * @return
	 */
	public boolean getDocumentTraceView(WorkflowTask task){
		WorkflowInstance instance = ApiFactory.getInstanceService().getInstance(task.getProcessInstanceId());
		boolean viewRightSetting = DefinitionXmlParse.getOfficialTextViewSetting(instance.getProcessDefinitionId(), task.getName());
		return viewRightSetting;
	}
}
