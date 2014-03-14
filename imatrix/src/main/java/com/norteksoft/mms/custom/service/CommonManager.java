package com.norteksoft.mms.custom.service;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import com.norteksoft.mms.custom.dao.CommonDao;
import com.norteksoft.mms.form.dao.DataTableDao;
import com.norteksoft.mms.form.dao.ListColumnDao;
import com.norteksoft.mms.form.entity.DataTable;
import com.norteksoft.mms.form.entity.FormControl;
import com.norteksoft.mms.form.entity.FormView;
import com.norteksoft.mms.form.entity.ListColumn;
import com.norteksoft.mms.form.entity.View;
import com.norteksoft.mms.form.enumeration.DataType;
import com.norteksoft.mms.form.jdbc.JdbcSupport;
import com.norteksoft.mms.form.service.FormHtmlParser;
import com.norteksoft.mms.form.service.FormViewManager;
import com.norteksoft.mms.module.entity.ModulePage;
import com.norteksoft.mms.module.service.ModulePageManager;
import com.norteksoft.product.api.ApiFactory;
import com.norteksoft.product.api.entity.Document;
import com.norteksoft.product.api.entity.Opinion;
import com.norteksoft.product.api.entity.TaskPermission;
import com.norteksoft.product.api.entity.User;
import com.norteksoft.product.api.entity.WorkflowAttachment;
import com.norteksoft.product.api.entity.WorkflowDefinition;
import com.norteksoft.product.api.entity.WorkflowInstance;
import com.norteksoft.product.api.entity.WorkflowTask;
import com.norteksoft.product.orm.Page;
import com.norteksoft.product.util.ContextUtils;
import com.norteksoft.product.web.wf.WorkflowManagerSupport;
import com.norteksoft.task.base.enumeration.TaskProcessingResult;
import com.norteksoft.task.base.enumeration.TaskState;
import com.norteksoft.wf.base.enumeration.CompleteTaskTipType;
import com.norteksoft.wf.engine.client.EndInstanceInterface;
import com.norteksoft.wf.engine.client.FormFlowable;
import com.norteksoft.wf.engine.client.FormFlowableDeleteInterface;
import com.norteksoft.wf.engine.client.RetrieveTaskInterface;
import com.norteksoft.wf.engine.dao.WorkflowAttachmentDao;
import com.norteksoft.wf.engine.service.TaskService;
import com.norteksoft.wf.engine.service.WorkflowInstanceManager;

@Service
public class CommonManager extends WorkflowManagerSupport<FormFlowable> implements FormFlowableDeleteInterface,RetrieveTaskInterface,EndInstanceInterface  {

	@Autowired
	private CommonDao commonDao;
	@Autowired
	private FormViewManager formViewManager;
	@Autowired
	private ListColumnDao listColumnDao;
	@Autowired
	private ModulePageManager modulePageManager;
	
	@Autowired
	private WorkflowInstanceManager workflowInstanceManager;
	@Autowired
	private WorkflowAttachmentDao workflowAttachmentDao;
	@Autowired
	private TaskService taskService;
	@Autowired
	private DataTableDao dataTableDao;
	@Autowired
	private JdbcSupport jdbcDao;
	/**
	 * 根据列表编号查询数据
	 * @param page
	 * @param listCode
	 * @return
	 */
	@Transactional
	public Page<Object> list(Page<Object> page, View listView){
		if(listView.getStandard()){
			return commonDao.listEntity(page, listView.getDataTable().getEntityName());
		}else{
			if(StringUtils.isNotBlank(page.getOrderBy())){
				if(!page.getOrderBy().startsWith("dt_") && !FormHtmlParser.isDefaultField(page.getOrderBy())){
					page.setOrderBy("dt_"+page.getOrderBy());
				}
			}
			commonDao.list(page, listView.getCode());
			if(StringUtils.isNotBlank(page.getOrderBy())){
				if(page.getOrderBy().startsWith("dt_")){
					page.setOrderBy(page.getOrderBy().replaceFirst("dt_", ""));
				}
			}
			return page;
		}
	}

	public Long getCompanyId() {
		return ContextUtils.getCompanyId();
	}
	
	/**
	 * 根据ID，表单视图查询数据
	 * @param formView
	 * @param id
	 * @return
	 */
	@Transactional
	public Object getDateById(View formView, Long id){
		if(formView.getStandard()){
			return commonDao.getEntityById(formView.getDataTable().getEntityName(), id);
		}else{
			return commonDao.getDateById(formView.getDataTable().getName(), id);
		}
	}
	
	/**
	 * 保存表单数据
	 */
	@Transactional
	public Long save(Map<String,String[]> parameter){
		String[] pageIds = parameter.get("pageId");
		
		ModulePage modulePage = modulePageManager.getModulePage(Long.valueOf(pageIds[0]));
		FormView form = (FormView) modulePage.getView();
		
		return saveDate(parameter, form);
	}
	
	@Transactional
	public Long saveDate(Map<String,String[]> parameter, FormView form){
		String[] ids = parameter.get("id");
		List<FormControl> controls = formViewManager.getControls(form);
		Long id = null;
		if(ids != null && StringUtils.isNotBlank(ids[0])){
			id = commonDao.update(parameter, form, controls, Long.parseLong(ids[0]));
		}else{
			id = commonDao.save(parameter, form, controls);
		}
		
		String[]  customListControlVals= parameter.get("customListControlVals");
		if(customListControlVals!=null){
			String fkKey="fk_"+form.getCode()+"_id";
			saveCustomListControlValues(customListControlVals,id,fkKey);
		}
		return id;
	}
	
	private void saveCustomListControlValues(String[] values,Long parentId,String fkKey){
		try {
			JSONArray jsons=JSONArray.fromObject(values[0]);
			Iterator<JSONObject> it=jsons.iterator();
			String tableName="";
			int i=0;
			while(it.hasNext()){
				i++;
				JSONObject obj=it.next();
				tableName=obj.get("dataTableName").toString();
				Object id=obj.get("id");
				if(StringUtils.isEmpty(id.toString())){
					commonDao.save(obj,tableName,fkKey,parentId,i);
				}else{
					commonDao.update(obj,tableName,fkKey,parentId,Long.valueOf(id.toString()),i);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		} 
	}
	
	/**
	 * 根据列表编号获取查询String
	 */
	@Transactional
	public String getQueryString(String listCode){
		List<ListColumn> columns = listColumnDao.getQueryColumnsByCode(listCode);
		StringBuilder query = new StringBuilder();
		query.append("[");
		boolean isFirst = true;
		for(ListColumn lc : columns){
			if(!isFirst) query.append(","); 
			query.append("{");
			query.append("enName:").append("dt_").append(lc.getTableColumn().getName());
			query.append(",chName:").append(getInternation(lc.getHeaderName()));
			if(DataType.TEXT == lc.getTableColumn().getDataType()){
				query.append(",propertyType:").append("STRING");
			}else{
				query.append(",propertyType:").append(lc.getTableColumn().getDataType());
			}
			query.append(",fixedField:false");
			query.append("}");
			isFirst = false;
		}
		query.append("]");
		return query.toString();
	}
	
	 public String getInternation(String code){
		 return ApiFactory.getSettingService().getInternationOptionValue(code);
	 }
	
	/**
	 * 删除
	 */
	@Transactional
	public String delete(View formView, List<Long> ids){
		if(ids != null && !ids.isEmpty()){
			for(Long id : ids){
				Object obj=commonDao.getDateById(formView.getDataTable().getName(),id);
				com.norteksoft.product.api.entity.WorkflowInstance wi=ApiFactory.getInstanceService().getInstance((String)((Map)obj).get("instance_id"));
				if(wi==null){
					commonDao.delete(formView.getDataTable().getName(), id);
				}else{
					ApiFactory.getInstanceService().deleteInstance((String)((Map)obj).get("instance_id"));
				}
			}
		}
		return "删除成功";
	}
	
	/////////////////////////////////////////////////////////////////////////////////////
	
	public List<WorkflowDefinition> getWorkflows(String code, Integer version){
		return ApiFactory.getDefinitionService().getEnableWorkflowDefinitionsByFormCodeAndVersion(code, version,ContextUtils.getSystemId("mms"));
	}
	
	/**
	 * 保存表单，启动流程
	 */
	@Transactional
	public Long startWorkflow(Map<String, String[]> parameter){
		// 从参数中获取 instanceId
		String instanceId = null;
		String[] instanceIds = parameter.get("instance_id");
		if(instanceIds != null && StringUtils.isNotEmpty(instanceIds[0])){
			instanceId = instanceIds[0];
		}
		// jBPM流程定义ID
		String processId = parameter.get("processId")[0];
		if(StringUtils.isNotEmpty(instanceId)&&StringUtils.isEmpty(processId)){
			com.norteksoft.wf.engine.entity.WorkflowInstance wi = workflowInstanceManager.getWorkflowInstance(instanceId);
			processId=wi.getProcessDefinitionId();
		}
		
		WorkflowDefinition def=getWorkflowDefinitionByProcessId(processId);
		Long dataId = null;
		if(def!=null){
			Map result = ApiFactory.getInstanceService().startCustomInstance(def.getId());
			dataId = (Long)result.get("dataId");
			com.norteksoft.product.api.entity.WorkflowInstance workflow = ApiFactory.getInstanceService().getInstance((String)result.get("instanceId"));
			WorkflowTask task = getFirstTask(workflow.getProcessInstanceId());
			parameter.put("taskId",  new String[]{task.getId().toString()});
		}
		if(dataId!=null){
			String[]  customListControlVals= parameter.get("customListControlVals");
			if(customListControlVals!=null){
				String[] pageIds = parameter.get("pageId");
				ModulePage modulePage = modulePageManager.getModulePage(Long.valueOf(pageIds[0]));
				FormView form = (FormView) modulePage.getView();
				String fkKey="fk_"+form.getCode()+"_id";
				saveCustomListControlValues(customListControlVals,dataId,fkKey);
			}
		}
		return dataId;
	}
	
	public WorkflowTask getFirstTask(String instanceId){
		WorkflowInstance wi = ApiFactory.getInstanceService().getInstance(instanceId);
		Long creatorId = wi.getCreatorId();
		if(creatorId==null){
			User creatorUser = ApiFactory.getAcsService().getUserByLoginName(wi.getCreator());
			if(creatorUser!=null){
				creatorId = creatorUser.getId();
			}
		}
		return taskService.getMyFirstTask(instanceId,creatorId);
	}
	
	/**
	 * 保存表单，启动流程，并提交第一环节任务
	 */
	@Transactional
	public Map submitWorkflow(Map<String,String[]> parameter){
		// jBPM流程定义ID
		String processId = parameter.get("processId")[0];
		WorkflowDefinition def=getWorkflowDefinitionByProcessId(processId);
		if(def!=null){
			Map submitResult=ApiFactory.getInstanceService().submitCustomInstance(def.getId());
			Long dataId=(Long)submitResult.get("dataId");
			if(dataId!=null){
				String[]  customListControlVals= parameter.get("customListControlVals");
				if(customListControlVals!=null){
					String[] pageIds = parameter.get("pageId");
					ModulePage modulePage = modulePageManager.getModulePage(Long.valueOf(pageIds[0]));
					FormView form = (FormView) modulePage.getView();
					String fkKey="fk_"+form.getCode()+"_id";
					saveCustomListControlValues(customListControlVals,dataId,fkKey);
				}
			}
			return submitResult;
		}
		return null;
	}
	
	/**
	 * 根据任务查询流程对应的表单
	 * @param taskId
	 * @return
	 */
	public FormView getViewByTask(Long taskId){
		WorkflowTask task = ApiFactory.getTaskService().getTask(taskId);
		WorkflowInstance wi = ApiFactory.getInstanceService().getInstance(task.getProcessInstanceId());
		FormView view = formViewManager.getFormView(wi.getFormId());
		return view;
	}
	
	/**
	 * 根据任务ID查询数据
	 * @param taskId
	 * @return
	 */
	public Object getDataByTaskId(Long taskId){
		WorkflowTask task = ApiFactory.getTaskService().getTask(taskId);
		WorkflowInstance wi = ApiFactory.getInstanceService().getInstance(task.getProcessInstanceId());
		FormView view = formViewManager.getFormView(wi.getFormId());
		return commonDao.getDateById(view.getDataTable().getName(), wi.getDataId());
	}
	
	/**
	 * 提交任务
	 */
	@Transactional
	public CompleteTaskTipType submitTask(Map<String,String[]> parameter){
		Long dataId=ApiFactory.getFormService().saveData(parameter);
		if(dataId!=null){
			String[]  customListControlVals= parameter.get("customListControlVals");
			if(customListControlVals!=null){
				String formCode=null;
				Integer formVersion=null;
				String[] formCodes=parameter.get(JdbcSupport.FORM_CODE);
				if(formCodes!=null){
					formCode=formCodes[0];
				}
				String[] formVersions=parameter.get(JdbcSupport.FORM_VERSION);
				if(formVersions!=null){
					formVersion=Integer.valueOf(formVersions[0]);
				}
				if(StringUtils.isNotEmpty(formCode)&&formVersion!=null){
					FormView form = formViewManager.getFormViewByCodeAndVersion(ContextUtils.getCompanyId(), formCode, formVersion);
					if(form!=null){
						String fkKey="fk_"+form.getCode()+"_id";
						saveCustomListControlValues(customListControlVals,dataId,fkKey);
					}
				}
			}
		}
		// 从参数中获取 instanceId
		String[] taskIds = parameter.get("taskId");
		Long taskId = Long.valueOf(taskIds[0]);
		if(parameter.get("transactor")!=null&&parameter.get("transactor").length>0){
			String tr = parameter.get("transactor")[0];
			return ApiFactory.getTaskService().completeInteractiveWorkflowTask(taskId, "", tr);
		}
		WorkflowTask task = ApiFactory.getTaskService().getTask(taskId);
		TaskProcessingResult transact = TaskProcessingResult.valueOf(parameter.get("transact")[0]);
		return ApiFactory.getTaskService().completeWorkflowTask(task, transact);
	}
	
	/**
	 * 取回
	 * @param taskId
	 */
	public String getBack(Long taskId){
		return ApiFactory.getTaskService().retrieve(taskId);
	}
	
	/**
	 * 是否需要指定办理人
	 */
	public CompleteTaskTipType isNeedAssigningTransactor(Long taskId){
		com.norteksoft.task.entity.WorkflowTask task = taskService.getTask(taskId);
		com.norteksoft.wf.engine.entity.WorkflowInstance instance = workflowInstanceManager.getWorkflowInstance(task.getProcessInstanceId());
		CompleteTaskTipType result = taskService.isNeedAssigningTransactor(instance, task);
		if(result==null){
			result=taskService.isSubProcessNeedChoiceTransactor(task,instance);
		}
		return result;
	}
	
	/**
	 * 抄送
	 */
	public void createCopyTaches(Long taskId, List<String> transactors,String title,String url){
		ApiFactory.getTaskService().createCopyTasks(taskId, transactors, title, url);
	}
	/**
	 * 领取任务
	 * @return
	 */
	public String receive(Long taskId){
		return ApiFactory.getTaskService().drawTask(taskId);
	}
	/**
	 * 放弃领取任务
	 * @param taskId
	 * @return
	 */
	public String abandonReceive(Long taskId){
		return ApiFactory.getTaskService().abandonReceive(taskId);
	}
	
	/**
	 * 获取任务下环节办理人
	 */
	public Map<String[], List<String[]>> getNextTasksCandidates(Long taskId){
		com.norteksoft.task.entity.WorkflowTask task = taskService.getTask(taskId);
		return taskService.getNextTasksCandidates(task);
	}
	
	/**
	 * 任务是否已完成
	 */
	public boolean isTaskComplete(Long taskId){
		WorkflowTask task = ApiFactory.getTaskService().getTask(taskId);
		return TaskState.COMPLETED.getIndex().equals(task.getActive())||TaskState.CANCELLED.getIndex().equals(task.getActive())||TaskState.ASSIGNED.getIndex().equals(task.getActive())||TaskState.HAS_DRAW_OTHER.getIndex().equals(task.getActive());
	}
	
	/**
	 * 查询字段编辑权限
	 */
	public String getFieldPermision(Long taskId){
		return ApiFactory.getFormService().getFieldPermission(taskId);
	}

	/**
	 * 为任务的下环节指定办理人
	 */
	public CompleteTaskTipType setTasksTransactor(Long taskId, List<String> transactors ) {
		return ApiFactory.getTaskService().completeInteractiveWorkflowTask(taskId, transactors, null);
	}
	/**
	 * 意见权限（查看、编辑、必填  read edit must）
	 */
	public List<String> opinionRightByTask(Long taskId){
		List<String> result = new ArrayList<String>();
		com.norteksoft.product.api.entity.WorkflowTask task = ApiFactory.getTaskService().getTask(taskId);
		TaskPermission permission = ApiFactory.getPermissionService().getActivityPermission(taskId);
		if(permission.getOpinionVisible()){
			result.add("view");
		}
		if(task.getActive()!=2&&task.getActive()!=3&&task.getActive()!=5&&task.getActive()!=7){
			if(permission.getOpinionEditable()){
				result.add("edit");
			}
			if(permission.getOpinionRequired()){
				result.add("must");
			}
		}
		return result;
	}
	
	/**
	 * 意见权限（查看、编辑、必填  read edit must）
	 */
	public List<String> opinionRightByTask(WorkflowDefinition definition){
		List<String> result = new ArrayList<String>();
		/*if(workflowRightsManager.v){
			result.add("view");
		}*/
		TaskPermission permission = ApiFactory.getPermissionService().getActivityPermission(definition.getCode(),ContextUtils.getSystemId("mms"));
		if(permission.getOpinionEditable()){
			result.add("edit");
		}
		if(permission.getOpinionRequired()){
			result.add("must");
		}
		return result;
	}
	
	/**
	 * 正文权限（创建,删除 create delete）
	 */
	public List<String> textRightByTask(Long taskId){
		List<String> result = new ArrayList<String>();
		com.norteksoft.product.api.entity.WorkflowTask task = ApiFactory.getTaskService().getTask(taskId);
		TaskPermission permission = ApiFactory.getPermissionService().getActivityPermission(taskId);
		if(task.getActive()!=2&&task.getActive()!=3&&task.getActive()!=5&&task.getActive()!=7){
			if(permission.getDocumentCreateable()){
				result.add("create");
			}
			if(permission.getDocumentDeletable()){
				result.add("delete");
			}
			if(permission.getDocumentDownloadable()){
				result.add("downLoad");
			}
			if(permission.getDocumentEditable()){
				result.add("edit");
			}
		}else if(task.getActive()==2){
			if(permission.getDocumentDownloadable()){
				result.add("downLoad");
			}
		}
		if(permission.getDocumentTraceView()){//当没有【编辑】权限，但有【查看编辑痕迹】的权限时
			result.add("traceVisible");
		}
		return result;
	}
	
	/**
	 * 附件权限
	 */
	public List<String> attachmentRightByTask(Long taskId){
		List<String> result = new ArrayList<String>();
		com.norteksoft.product.api.entity.WorkflowTask task = ApiFactory.getTaskService().getTask(taskId);
		TaskPermission permission = ApiFactory.getPermissionService().getActivityPermission(taskId);
		if(task.getActive()!=2&&task.getActive()!=3&&task.getActive()!=5&&task.getActive()!=7){
			if(permission.getAttachmentCreateable()){
				result.add("create");
			}
			if(permission.getAttachmentDeletable()){
				result.add("delete");
			}
			if(permission.getAttachmentDownloadable()){
				result.add("downLoad");
			}
		}else if(task.getActive()==2){
			if(permission.getAttachmentDownloadable()){
				result.add("downLoad");
			}
		}
		return result;
	}
	/**
	 * 根据taskId得到task
	 */
	public WorkflowTask getTaskByTaskId(Long taskId){
		return ApiFactory.getTaskService().getTask(taskId);
	}
	/**
	 * 根据任务查询意见
	 */
	public List<Opinion> getOpinions(String workflowId,Long companyId){
		return ApiFactory.getOpinionService().getAllOpinions(workflowId);
	}
	
	/**
	 * 根据任务id和登录名查询意见
	 */
	public List<Opinion> getOpinionsBytaskId(Long taskId,String loginName){
		return ApiFactory.getOpinionService().getOpinions(taskId, loginName);
	}
	
//	public List<Opinion> getOpinionsByWfdId(Long wfdId,String loginName){
//		return workflowInstanceManager.getOpinionsByWfdId(wfdId, loginName);
//	}
	
	
	/**
	 * 保存意见
	 */
	public void saveOpinion(Opinion opi){
		com.norteksoft.product.api.entity.WorkflowTask task = ApiFactory.getTaskService().getTask(opi.getTaskId());
		opi.setWorkflowId(task.getProcessInstanceId());
		opi.setTaskName(task.getName());
		ApiFactory.getOpinionService().saveOpinion(opi);
	}
	 /**
     * 通过流程定义的Key和version查询WorkflowDefinition
     * @param key
     * @param version
     * @return
     */
	 public WorkflowDefinition getWorkflowDefinitionByProcessId(String processId){
		 return ApiFactory.getDefinitionService().getWorkflowDefinitionByProcessId(processId);
	 }
	 /**
	  * 发起前自动填写字段
	  */
	 public void fillEntityByDefinition(Map data,String wfDefinationCode, Integer version,Long... systemId){
		 ApiFactory.getFormService().fillEntityByDefinition(data, wfDefinationCode, version,systemId);
	 }
	 /**
	  * 办理前自动填写字段
	  */
	 public void fillEntityByTask(Map data,Long taskId){
		 ApiFactory.getFormService().fillEntityByTask(data, taskId);
	 }
	 
	 public List<Document> getDocumentsByInstance(String instanceId){
		 return ApiFactory.getDocumentService().getDocuments(instanceId);
	 }
	 
	 public List<WorkflowAttachment> getAttachments(String instanceId){
		 return ApiFactory.getAttachmentService().getAllAttachments(instanceId);
	 }
	 

	@Override
	protected FormFlowable getEntity(Long entityId) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected void saveEntity(FormFlowable t) {
		// TODO Auto-generated method stub
		
	}

	public void deleteFormFlowable(Long dataId) {
		// TODO Auto-generated method stub
		
	}

	public void retrieveTaskExecute(Long entityId, Long taskId) {
		// TODO Auto-generated method stub
		
	}

	public void endInstanceExecute(Long entityId) {
		// TODO Auto-generated method stub
		
	}

	public WorkflowInstance getWorkflowInforById(String instanceId) {
		return ApiFactory.getInstanceService().getInstance(instanceId);
	}
	public void deleteAttachment(Long id){
		Assert.notNull(id, "附件id不能为null");
		this.workflowAttachmentDao.delete(id);
	}

	public Map<String, Object> getAmountTotal(List<String> names) {
		return commonDao.getAmountTotal(names);
	}

	public List<String> opinionRightByWorkflowId(Long workflowDefinitionId) {
		List<String> result = new ArrayList<String>();
		TaskPermission permission = ApiFactory.getPermissionService().getActivityPermission(workflowDefinitionId);
		if(permission.getOpinionEditable()){
			result.add("edit");
		}
		if(permission.getOpinionRequired()){
			result.add("must");
		}
		return result;
	}
	
	/**
	 * 根据数据表和数据id删除此数据
	 * @param dataTableId
	 * @param rowId
	 */
	public void deleteRow(Long dataTableId, Long dataId) {
		DataTable dt=dataTableDao.get(dataTableId);
		jdbcDao.deleteData(dt.getName(),dataId);
	}
}
