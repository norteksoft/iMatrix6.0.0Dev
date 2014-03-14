package com.norteksoft.mms.custom.web;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.apache.struts2.ServletActionContext;
import org.apache.struts2.convention.annotation.Action;
import org.apache.struts2.convention.annotation.Namespace;
import org.apache.struts2.convention.annotation.ParentPackage;
import org.apache.struts2.convention.annotation.Result;
import org.apache.struts2.convention.annotation.Results;
import org.apache.struts2.dispatcher.multipart.MultiPartRequestWrapper;
import org.springframework.beans.factory.annotation.Autowired;

import com.norteksoft.mms.base.TotalColumnValues;
import com.norteksoft.mms.custom.service.CommonManager;
import com.norteksoft.mms.form.entity.DataTable;
import com.norteksoft.mms.form.entity.FormView;
import com.norteksoft.mms.form.service.DataTableManager;
import com.norteksoft.mms.form.service.FormViewManager;
import com.norteksoft.mms.module.entity.Button;
import com.norteksoft.mms.module.entity.ModulePage;
import com.norteksoft.mms.module.enumeration.ViewType;
import com.norteksoft.mms.module.service.MenuManager;
import com.norteksoft.mms.module.service.ModulePageManager;
import com.norteksoft.product.api.ApiFactory;
import com.norteksoft.product.api.entity.Document;
import com.norteksoft.product.api.entity.Opinion;
import com.norteksoft.product.api.entity.TaskPermission;
import com.norteksoft.product.api.entity.User;
import com.norteksoft.product.api.entity.WorkflowAttachment;
import com.norteksoft.product.api.entity.WorkflowDefinition;
import com.norteksoft.product.api.entity.WorkflowInstance;
import com.norteksoft.product.orm.Page;
import com.norteksoft.product.util.ContextUtils;
import com.norteksoft.product.util.JsonParser;
import com.norteksoft.product.util.PageUtils;
import com.norteksoft.product.util.tree.ZTreeNode;
import com.norteksoft.product.web.struts2.CrudActionSupport;
import com.norteksoft.product.web.struts2.Struts2Utils;
import com.norteksoft.task.base.enumeration.TaskProcessingMode;
import com.norteksoft.task.base.enumeration.TaskProcessingResult;
import com.norteksoft.task.entity.HistoryWorkflowTask;
import com.norteksoft.task.entity.WorkflowTask;
import com.norteksoft.task.service.HistoryWorkflowTaskManager;
import com.norteksoft.task.service.WorkflowTaskManager;
import com.norteksoft.wf.base.enumeration.CompleteTaskTipType;
import com.norteksoft.wf.engine.entity.Temp;
import com.norteksoft.wf.engine.service.TaskService;
import com.norteksoft.wf.engine.service.WorkflowInstanceManager;
import com.norteksoft.wf.engine.service.WorkflowRightsManager;

/**
 * 自定义表单操作
 */
@Namespace("/common")
@ParentPackage("default")
@Results( { @Result(name = "redirectInput", location = "input", type = "redirectAction", params={"menuId", "${menuId}","onlyTable", "${onlyTable}"})})
public class CommonAction extends CrudActionSupport<Object>{

	private static final long serialVersionUID = 1L;
	private Object data;
	private Page<Object> page = new Page<Object>(0, true);
	private Long pageId;
	private Long documentId;
	private String listCode;
	private String formCode;
	private Integer formVersion;
	private String formCodes;//脚本样式标签需要使用该参数 view:JC
	private String formVersions;//脚本样式标签需要使用该参数view:JC
	private Long dataId;
	private List<Long> deleteIds;
	private Long menuId;
	private String queryString;
	private String validateString;
	private String richString;
	private ModulePage modulePage;
	// private Long oldPageId; // 原页面的ID
	
	private String workflowUrl;
	private List<WorkflowDefinition> workflows;
	private String processId;
	private Long taskId;
	private String chooseUrl; // 选择人、环节的url
	private Long companyId;
	private List<String> transactors;
	private String[] targetNames; // 任务名称s
	private List<String> opinionRight; // 意见权限
	private List<Opinion> opinions; // 意见
	private String opinion;
	private String transactor;

	private boolean view = false;//会签结果的查看
	private boolean  instanceInHistory  = false;//实例是否在历史实例表中,true 表示在历史实例表中,false 表示在实例表中
	private List<Temp> temps;
	private String message;  //提示信息
	
	private List<String> textRight;//正文权限
	private List<Document> offices;//已有正文列表
	private String workflowId;//流程实例id
	
	private List<WorkflowAttachment> WorkflowAttachments;//附件list
	private WorkflowAttachment workflowAttachment;//附件
	
	private String dataIds;
	private String deleteMsg;//删除提示信息
	
	private String onlyTable;//标示没有列表只有一个表单
	
	private static final String SUCCESS_MESSAGE_LEFT = "<font class=\"onSuccess\"><nobr>";
	private static final String MESSAGE_RIGHT = "</nobr></font>";
	
	private String assignee; //指派人
	
	private String tacheCode;//环节编号
	
	private Map<Long, User> choiceTransactor = new HashMap<Long, User>();//减签环节办理人list
	private Map<String, String> choiceTache = new HashMap<String, String>();//减签环节办理人list
	
	private Long id;
	
	private boolean closeFlag=true;
	private String instanceId;
	private TaskPermission permission;
	private boolean monitorFlag=false;//流程监控标志，打开任务办理页面，可以下载正文和附件
	private Long toPageId;
	private Integer opinionSize;
	private String doneOpinion;
	private boolean hasBranch;
	private boolean canSubmitTask = true;
	private String dataTableName;
	
	@Autowired
	private TaskService TaskService;
	@Autowired
	private WorkflowRightsManager workflowRightsManager;
	@Autowired
	private WorkflowTaskManager workflowTaskManager;
	@Autowired
	private HistoryWorkflowTaskManager historyWorkflowTaskManager;
	@Autowired
	private TaskService taskService;
	@Autowired
	private WorkflowInstanceManager workflowInstanceManager;
	@Autowired
	private CommonManager commonManager;
	@Autowired
	private ModulePageManager modulePageManager;
	@Autowired
	private MenuManager menuManager;
	@Autowired
	private FormViewManager formViewManager;
	@Autowired
	private DataTableManager dataTableManager;

	/**
	 * 通用列表
	 */
	@Action("list")
	@Override
	public String list() throws Exception {
		if(pageId == null){
			modulePage = menuManager.getDefaultModulePageByMenu(menuId);
		}else{
			modulePage = modulePageManager.getModulePageByPageId(pageId);
		}
		if(modulePage == null) return SUCCESS;
		pageId = modulePage.getId();
		menuId = modulePage.getMenuId();
		Struts2Utils.getRequest().setAttribute("menuId", menuId);
		//如果默认的为表单页面
		if(modulePage.getViewType()==ViewType.FORM_VIEW){
			onlyTable="onlyTable";
			return "redirectInput";
		}
		listCode = modulePage.getView().getCode();
		
		viewJc(modulePage);
		
		if(page.getPageSize()>1){
			commonManager.list(page, modulePage.getView());
			ApiFactory.getBussinessLogService().log("自定义系统", 
					"自定义列表", 
					ContextUtils.getSystemId("mms"));
			renderText(PageUtils.PageToJson(page, new TotalColumnValues(){
				public Map<String,Object> getValues(List<String> names) {
					return commonManager.getAmountTotal(names);
				}
			}));
			return null;
		}
		return SUCCESS;
	}
	
	private void viewJc(ModulePage modulePage){
		//页面中的view:JC标签需要formCode和formVersion参数
		List<Button> buttons = modulePage.getButtons();
		for(Button button:buttons){
			ModulePage topage = button.getToPage();
			if(topage!=null){
				if(topage.getViewType()==ViewType.FORM_VIEW){
					if(StringUtils.isEmpty(formCodes)){
						formCodes = topage.getView().getCode();
						formVersions = ((FormView)topage.getView()).getVersion()+"";
					}else{
						formCodes = formCodes+","+topage.getView().getCode();
						formVersions = formVersions+","+((FormView)topage.getView()).getVersion();
					}
					break;
				}
			}
		}
	}

	/**
	 * 通用的表单新建、修改
	 */
	@SuppressWarnings("unchecked")
	@Action(value="input",
			results={@Result(name="input", location="input.jsp"),
					 @Result(name="view", location="view.jsp")
			})
	public String input() throws Exception {
		//表单查看页面：流程监控中的链接或swing小窗体中的链接或【任务委托监控】查看任务
		if(instanceId!=null){
			com.norteksoft.product.api.entity.WorkflowInstance wi=ApiFactory.getInstanceService().getInstance(instanceId);
			if(wi!=null){
				com.norteksoft.product.api.entity.WorkflowTask firstTask=commonManager.getFirstTask(wi.getProcessInstanceId());
				if(firstTask!=null)taskId=firstTask.getId();	
			}
			if(taskId!=null){
				data = commonManager.getDataByTaskId(taskId);
				FormView view = commonManager.getViewByTask(taskId);
				formCode = view.getCode();
				formVersion = view.getVersion();
				validateString = fieldReadOnly();
			}
			return "view";
		}
		if(pageId == null){
			modulePage = menuManager.getDefaultModulePageByMenu(menuId);
		}else{
			modulePage = modulePageManager.getModulePageByPageId(pageId);
		}
		//如果没有定义表单页面，返回input页面，提示用户新建页面
		if(modulePage==null) return "input";
		richString = formViewManager.getRichSetting((FormView) modulePage.getView());
		pageId = modulePage.getId();
		FormView currenFormView = null;
		formCode = modulePage.getView().getCode();
		if(modulePage.getViewType()==ViewType.FORM_VIEW){
			currenFormView = (FormView)modulePage.getView();
			formVersion = currenFormView.getVersion();
		}
		menuId = modulePage.getMenuId();
		List<Button> buttons=modulePage.getButtons();
		for(Button b:buttons){
			if("back".equals(b.getCode())&&b.getToPage()!=null){
				toPageId=b.getToPage().getId();
			}
		}
		Struts2Utils.getRequest().setAttribute("menuId", menuId);
		
		// 如果为修改
		if(dataId != null){
			data = commonManager.getDateById(modulePage.getView(), dataId);
			if(((Map)data).get("INSTANCE_ID") != null && !((Map)data).get("INSTANCE_ID").equals("")){
				String instanceId = ((Map)data).get("INSTANCE_ID").toString();
				com.norteksoft.product.api.entity.WorkflowTask firstTask = commonManager.getFirstTask(instanceId);
				taskId = firstTask.getId();
				if(firstTask.getTransactorId()!=null){
					if(firstTask.getTransactorId().equals(ContextUtils.getUserId())){
						canSubmitTask = true;
					}else{
						canSubmitTask = false;
					}
				}else{
					canSubmitTask = true;
				}
				opinionRight = commonManager.opinionRightByTask(taskId);
				opinionSize = commonManager.getOpinionsBytaskId(taskId,ContextUtils.getLoginName()).size(); 
				com.norteksoft.product.api.entity.WorkflowInstance wf = commonManager.getWorkflowInforById(instanceId);
				processId = wf.getProcessDefinitionId();
				if(commonManager.isTaskComplete(taskId)){ // 任务已经完成, 表单只读
					validateString = fieldReadOnly();
					permission = ApiFactory.getPermissionService().getActivityPermission(taskId);
				}else{
					//表单中设置的字段权限
//					validateString = formViewManager.getValidateSetting((FormView) modulePage.getView());
					//流程中设置的字段权限，当走流程时已流程图中设置的为准
					validateString = commonManager.getFieldPermissionByTaskId(taskId);
					permission = ApiFactory.getPermissionService().getActivityPermission(taskId);
				}
			}else{
				validateString = formViewManager.getValidateSetting((FormView) modulePage.getView());
			}
			return "input";
		}else{
			validateString = formViewManager.getValidateSetting((FormView) modulePage.getView());
			// 根据 formCode 判断是否走流程，如果走流程且只有一个，直接发起，否则选择流程
		}

		if(StringUtils.isNotEmpty(processId)){
			WorkflowDefinition wfd=commonManager.getWorkflowDefinitionByProcessId(processId);
			if(wfd!=null){
				validateString = commonManager.getFieldPermission(wfd.getId());
				if(data==null)data=new HashMap();
				commonManager.fillEntityByDefinition((Map)data, wfd.getCode(), wfd.getVersion(),wfd.getSystemId());
				opinionRight = commonManager.opinionRightByTask(wfd);
			}
			
		}else{
			List<WorkflowDefinition> workflows = commonManager.getWorkflows(//"test_x", 1);
					((FormView) modulePage.getView()).getCode(), 
					((FormView) modulePage.getView()).getVersion());
			if(workflows.size() > 1){
				workflowUrl = "/common/select-workflow.htm";
			}else if(workflows.size() == 1){
				opinionRight = commonManager.opinionRightByTask(workflows.get(0));
				processId = workflows.get(0).getProcessId();
				validateString = commonManager.getFieldPermission(workflows.get(0).getId());
				if(data==null)data=new HashMap();
				commonManager.fillEntityByDefinition((Map)data, workflows.get(0).getCode(), workflows.get(0).getVersion(),workflows.get(0).getSystemId());
			}
		}
		companyId=ContextUtils.getCompanyId();
		ApiFactory.getBussinessLogService().log("自定义系统", 
				"自定义表单页面", 
				ContextUtils.getSystemId("mms"));
		return "input";
	}

	/**
	 * 通用表单保存
	 */
	@Action("save")
	@SuppressWarnings("unchecked")
	public String save() throws Exception {
		Map<String,String[]> parameterMap = Struts2Utils.getRequest().getParameterMap();
		Long id = commonManager.save(parameterMap);
		
		modulePage = modulePageManager.getModulePage(pageId);
		
		menuId = modulePage.getMenuId();
		Struts2Utils.getRequest().setAttribute("menuId", menuId);
		formCode = modulePage.getView().getCode();
		if(modulePage.getViewType()==ViewType.FORM_VIEW){
			FormView myview = (FormView)modulePage.getView();
			formVersion = myview.getVersion();
		}
		data = commonManager.getDateById(modulePage.getView(), id);
		ApiFactory.getBussinessLogService().log("自定义系统", 
				"保存自定义表单", 
				ContextUtils.getSystemId("mms"));
		return "input";
	}
	/**
	 * 通用表单删除方法
	 */
	@Action("delete")
	@Override
	public String delete() throws Exception {
		modulePage = modulePageManager.getModulePage(pageId);
	    deleteMsg = commonManager.delete(modulePage.getView(), deleteIds);
		
		listCode = modulePage.getView().getCode();
		menuId = modulePage.getMenuId();
		Struts2Utils.getRequest().setAttribute("menuId", menuId);
		commonManager.list(page, modulePage.getView());
		viewJc(modulePage);
		this.addActionSuccessMessage(deleteMsg);
		ApiFactory.getBussinessLogService().log("自定义系统", 
				"删除自定义表单", 
				ContextUtils.getSystemId("mms"));
		return "list";
	}
	
	// *****************************     工作流相关方法               ********************************
	
	@Action("select-workflow")
	public String selectWorkflow() throws Exception{
		modulePage = modulePageManager.getModulePage(pageId);
		workflows = commonManager.getWorkflows(
				((FormView) modulePage.getView()).getCode(), 
				((FormView) modulePage.getView()).getVersion());
		return SUCCESS;
	}
	
	/**
	 * 第一环节保存表单 并 启动流程
	 */
	@SuppressWarnings("unchecked")
	@Action("start")
	public String start() throws Exception{
		Map<String,String[]> parameterMap = Struts2Utils.getRequest().getParameterMap();
		Map<String,String[]> values = new HashMap<String, String[]>();
		values.putAll(parameterMap);
		Long id = commonManager.startWorkflow(values);

		modulePage = modulePageManager.getModulePage(pageId);
		menuId = modulePage.getMenuId();
		List<Button> buttons=modulePage.getButtons();
		for(Button b:buttons){
			if("back".equals(b.getCode())){
				toPageId=b.getToPage().getId();
			}
		}
		formCode = modulePage.getView().getCode();
		if(modulePage.getViewType()==ViewType.FORM_VIEW){
			FormView myview = (FormView)modulePage.getView();
			formVersion = myview.getVersion();
		}
		data = commonManager.getDateById(modulePage.getView(), id);
		
		com.norteksoft.product.api.entity.WorkflowInstance wi=ApiFactory.getInstanceService().getInstance((String)((Map)data).get("instance_id"));
		if(wi!=null){
			String instanceId = wi.getProcessInstanceId();
			taskId = commonManager.getFirstTask(instanceId).getId();
			opinionRight = commonManager.opinionRightByTask(taskId);
			opinionSize = commonManager.getOpinionsBytaskId(taskId,ContextUtils.getLoginName()).size(); 
			permission = ApiFactory.getPermissionService().getActivityPermission(taskId);
		}
		
		ApiFactory.getBussinessLogService().log("自定义系统", 
				"第一环节保存表单 并 启动流程", 
				ContextUtils.getSystemId("mms"));
		return "input";
	}
	
	/**
	 * 第一环节保存表单,启动流程,并提交第一环节任务
	 */
	@SuppressWarnings("unchecked")
	@Action("submit")
	public String submit() throws Exception{
		Map<String,String[]> parameterMap = Struts2Utils.getRequest().getParameterMap();
		Map<String,String[]> values = new HashMap<String, String[]>();
		values.putAll(parameterMap);
		
		Map submitResult = commonManager.submitWorkflow(values);
		
		CompleteTaskTipType result = (CompleteTaskTipType)submitResult.get("result");
		Long dataId=(Long)submitResult.get("dataId");
		
		modulePage = modulePageManager.getModulePage(pageId);
		if(dataId!=null)data = commonManager.getDateById(modulePage.getView(), dataId);
		if(((Map)data).get("first_task_id")!=null){
			taskId=Long.valueOf(((Map)data).get("first_task_id").toString());
		}
		if(StringUtils.isNotEmpty(values.get("taskId")[0])||taskId!=null){
			if(StringUtils.isNotEmpty(values.get("taskId")[0])){
				taskId = Long.valueOf(values.get("taskId")[0].toString());
			}
			processResult(result);
		}
		
		
		menuId = modulePage.getMenuId();
		Struts2Utils.getRequest().setAttribute("menuId", menuId);
		List<Button> buttons=modulePage.getButtons();
		for(Button b:buttons){
			if("back".equals(b.getCode())&&b.getToPage()!=null){
				toPageId=b.getToPage().getId();
			}
		}
		formCode = modulePage.getView().getCode();
		if(modulePage.getViewType()==ViewType.FORM_VIEW){
			FormView myview = (FormView)modulePage.getView();
			formVersion = myview.getVersion();
		}
		
//		if(dataId!=null)data = commonManager.getDateById(modulePage.getView(), dataId);
		ApiFactory.getBussinessLogService().log("自定义系统", 
				"第一环节保存表单,启动流程,并提交第一环节任务", 
				ContextUtils.getSystemId("mms"));
		return "input";
	}
	
	@Action("task")
	public String task() throws Exception{
		if(taskId==null){
			if(id!=null){
				com.norteksoft.product.api.entity.WorkflowInstance wi=ApiFactory.getInstanceService().getInstance(instanceId);
				if(wi!=null){
					com.norteksoft.product.api.entity.WorkflowTask firstTask=commonManager.getFirstTask(wi.getProcessInstanceId());
					if(firstTask!=null)taskId=firstTask.getId();
				}
			}
		}
		if(taskId!=null){
			opinionRight = commonManager.opinionRightByTask(taskId);
			opinionSize = commonManager.getOpinionsBytaskId(taskId,ContextUtils.getLoginName()).size(); 
			data = commonManager.getDataByTaskId(taskId);
			FormView view = commonManager.getViewByTask(taskId);
			formCode = view.getCode();
			formVersion = view.getVersion();
			richString = formViewManager.getRichSetting(view);
			if(commonManager.isTaskComplete(taskId)){ // 任务已经完成
				validateString = fieldReadOnly();
			}else{
				validateString = commonManager.getFieldPermision(taskId);
			}
			permission = ApiFactory.getPermissionService().getActivityPermission(taskId);
			
			//办理前填写字段
			com.norteksoft.product.api.entity.WorkflowTask task=commonManager.getTaskByTaskId(taskId);
			if(task!=null){
				WorkflowInstance instance=ApiFactory.getInstanceService().getInstance(task.getProcessInstanceId());
				WorkflowDefinition wfd=commonManager.getWorkflowDefinitionByProcessId(instance.getProcessDefinitionId());
				if(wfd!=null){
					commonManager.fillEntityByTask((Map)data, taskId);
				}
			}
		}
		ApiFactory.getBussinessLogService().log("自定义系统", 
				"办理任务页面", 
				ContextUtils.getSystemId("mms"));
		return "task";
	}
	
	private String fieldReadOnly(){
		return new StringBuilder("[").append( "{request:\"").append(false)
		.append("\",readonly:\"").append((true))
		.append("\",controlType:\"allReadolny\"}]").toString();
	}
	
	/**
	 * 任务提交
	 */
	@SuppressWarnings("unchecked")
	@Action("submit-task")
	public String submitTask() throws Exception{
		Map<String,String[]> parameterMap = Struts2Utils.getRequest().getParameterMap();
		Map<String,String[]> values = new HashMap<String, String[]>();
		values.putAll(parameterMap);
		CompleteTaskTipType result = commonManager.submitTask(values);
		processResult(result);
		
		data = commonManager.getDataByTaskId(taskId);
		FormView myview=commonManager.getViewByTask(taskId);
		formCode = myview.getCode();
		formVersion = myview.getVersion();
		
		if(commonManager.isTaskComplete(taskId)){ // 任务已经完成, 表单只读
			validateString = fieldReadOnly();
		}else{
			if(modulePage!=null)
			validateString = formViewManager.getValidateSetting((FormView) modulePage.getView());
		}
		ApiFactory.getBussinessLogService().log("自定义系统", 
				"提交任务", 
				ContextUtils.getSystemId("mms"));
		validateString = fieldReadOnly();
		permission = ApiFactory.getPermissionService().getActivityPermission(taskId);
		return "task";
		
	}
	
	private void processResult(CompleteTaskTipType result){
		switch(result){
		case OK:
			validateString = fieldReadOnly();
			addActionMessage("提交成功");
			break;
		case MESSAGE:
			
			break;
		case RETURN_URL: //如果需要指定办理人
			addActionMessage("请选择办理人");
			chooseUrl = "/common/task-assign.htm?taskId="+taskId;
			break;
		case TACHE_CHOICE_URL: //如果需要选择环节
			chooseUrl = "/common/select-tache.htm?taskId="+taskId;
			break;
		case SINGLE_TRANSACTOR_CHOICE:
			addActionMessage("请选择办理人");
			hasBranch = ContextUtils.hasBranch();
			Collection<Long> col = result.getCanChoiceTransactorId();
			if(col != null && !col.isEmpty()){
				Iterator<Long> it = col.iterator();
				com.norteksoft.product.api.entity.User u = null;
				while(it.hasNext()){
					 u = ApiFactory.getAcsService().getUserById(it.next());
					 if(u != null){
						 if(hasBranch){
							 u.setName(u.getName()+"/"+u.getSubCompanyName());
						 }
						 choiceTransactor.put(u.getId(), u);
					 }
				}
				chooseUrl = "choose_user";
			}else{
				chooseUrl = "/common/task-assign.htm?taskId="+taskId;
			}
			break;
		}
	}
	
	/**
	 * 保存任务
	 */
	@SuppressWarnings("unchecked")
	@Action("save-task")
	public String saveTask() throws Exception{
		Map<String,String[]> parameterMap = Struts2Utils.getRequest().getParameterMap();
		Map<String,String[]> values = new HashMap<String, String[]>();
		values.putAll(parameterMap);
		
		FormView view = commonManager.getViewByTask(taskId);
		Long id = commonManager.saveDate(values, view);
		
		data = commonManager.getDateById(view, id);
		formCode = view.getCode();
		formVersion = view.getVersion();
		validateString = formViewManager.getValidateSetting(view);
		ApiFactory.getBussinessLogService().log("自定义系统", 
				"保存任务", 
				ContextUtils.getSystemId("mms"));
		renderText("保存成功!");
		return null;
	}
	
	/**
	 * 取回
	 */
	@Action("get-back")
	public String getBack() throws Exception{
		String msg = commonManager.getBack(taskId);
		
		FormView view = commonManager.getViewByTask(taskId);
		data = commonManager.getDataByTaskId(taskId);
		formCode = view.getCode();
		formVersion = view.getVersion();
		if("任务已取回".equals(msg)){
			validateString = commonManager.getFieldPermision(taskId);
		}else{
			validateString = fieldReadOnly();
		}
		ApiFactory.getBussinessLogService().log("自定义系统", 
				"取回任务", 
				ContextUtils.getSystemId("mms"));
		renderText(msg);
		return null;
	}
	
	/**
	 * 流转历史
	 */
	@Action("history")
	public String history() throws Exception{
		companyId = commonManager.getCompanyId();
		ApiFactory.getBussinessLogService().log("自定义系统", 
				"查看流转历史", 
				ContextUtils.getSystemId("mms"));
		return SUCCESS;
	}
	
	/**
	 * 会签结果
	 */
	@Action("countersign")
	public String countersign() throws Exception{
	 temps = new ArrayList<Temp>();
		if(taskId != 0) {
			com.norteksoft.product.api.entity.WorkflowTask task = ApiFactory.getTaskService().getTask(taskId);
			TaskPermission  permission = ApiFactory.getPermissionService().getActivityPermission(taskId);
			view = permission.getCountersignResultVisible();
			WorkflowInstanceManager workflowInstanceManager = (WorkflowInstanceManager)ContextUtils.getBean("workflowInstanceManager");
			instanceInHistory = workflowInstanceManager.isInstanceInHistory(task.getProcessInstanceId());
			if(view) {
				List<String> nameList = new ArrayList<String>();
				if(instanceInHistory){
					nameList = historyWorkflowTaskManager.getCountersignByProcessInstanceId(task.getProcessInstanceId(), TaskProcessingMode.TYPE_COUNTERSIGNATURE);
				}else{
					nameList=taskService.getCountersignByProcessInstanceId(task.getProcessInstanceId(),TaskProcessingMode.TYPE_COUNTERSIGNATURE);
				}
				if(nameList!=null){
					for (int i=0;i<nameList.size();i++) {
						String name= nameList.get(i);
						
						if(instanceInHistory){
							List<HistoryWorkflowTask> listYes = historyWorkflowTaskManager.getCountersignByProcessInstanceIdResult(task.getProcessInstanceId(),name,TaskProcessingResult.APPROVE);
							List<HistoryWorkflowTask> listNo= historyWorkflowTaskManager.getCountersignByProcessInstanceIdResult(task.getProcessInstanceId(),name,TaskProcessingResult.REFUSE);
							List<Integer> groupNums= historyWorkflowTaskManager.getGroupNumByTaskName(task.getProcessInstanceId(),name);
							for(Integer num:groupNums){
								int yesnum = 0,nonum = 0;
								List<WorkflowTask> resultList = new ArrayList<WorkflowTask>();
								List<HistoryWorkflowTask> histList = new ArrayList<HistoryWorkflowTask>();
								for(HistoryWorkflowTask yesTask:listYes){
									if(num.equals(yesTask.getGroupNum())){
										yesnum++;
										histList.add(yesTask);
									}
								}
								for(HistoryWorkflowTask noTask:listNo){
									if(num.equals(noTask.getGroupNum())){
										nonum++;
										histList.add(noTask);
									}
								}
								Temp temp = new Temp(name,yesnum,nonum,resultList,histList);
								temps.add(temp);
							}
						}else{
							List<WorkflowTask> listYes = taskService.getCountersignByProcessInstanceIdResult(task.getProcessInstanceId(),TaskProcessingMode.TYPE_COUNTERSIGNATURE,name,TaskProcessingResult.APPROVE);
							List<WorkflowTask> listNo= taskService.getCountersignByProcessInstanceIdResult(task.getProcessInstanceId(),TaskProcessingMode.TYPE_COUNTERSIGNATURE,name,TaskProcessingResult.REFUSE);
							List<Integer> groupNums= taskService.getGroupNumByTaskName(task.getProcessInstanceId(),name);
							for(Integer num:groupNums){
								int yesnum = 0,nonum = 0;
								List<WorkflowTask> resultList = new ArrayList<WorkflowTask>();
								List<HistoryWorkflowTask> histList = new ArrayList<HistoryWorkflowTask>();
								for(WorkflowTask yesTask:listYes){
									if(num.equals(yesTask.getGroupNum())){
										yesnum++;
										resultList.add(yesTask);
									}
								}
								for(WorkflowTask noTask:listNo){
									if(num.equals(noTask.getGroupNum())){
										nonum++;
										resultList.add(noTask);
									}
								}
								Temp temp = new Temp(name,yesnum,nonum,resultList,histList);
								temps.add(temp);
							}
						}
					}
				}
			} else {
				message = "你没有权限查看会签结果";
			}
		} else {
			message = "没有任务id，无法查看会签结果";
		}
		ApiFactory.getBussinessLogService().log("自定义系统", 
				"查看会签结果", 
				ContextUtils.getSystemId("mms"));
		return SUCCESS;
	}
	
	/**
	 * 投票结果
	 */
	@Action("vote")
	public String vote() throws Exception{
	  temps = new ArrayList<Temp>();
		if(taskId != 0){
			com.norteksoft.product.api.entity.WorkflowTask task = ApiFactory.getTaskService().getTask(taskId);
			TaskPermission  permission = ApiFactory.getPermissionService().getActivityPermission(taskId);
			view = permission.getVoteResultVisible();
			WorkflowInstanceManager workflowInstanceManager = (WorkflowInstanceManager)ContextUtils.getBean("workflowInstanceManager");
			instanceInHistory = workflowInstanceManager.isInstanceInHistory(task.getProcessInstanceId());
			if(view) {
				List<String> nameList = new ArrayList<String>();
				if(instanceInHistory){
					nameList = historyWorkflowTaskManager.getCountersignByProcessInstanceId(task.getProcessInstanceId(),TaskProcessingMode.TYPE_VOTE);
				}else{
					nameList = taskService.getCountersignByProcessInstanceId(task.getProcessInstanceId(),TaskProcessingMode.TYPE_VOTE);
				}
				if(nameList!=null){
					for (int i=0;i<nameList.size();i++) {
						String name= nameList.get(i);
						if(instanceInHistory){
							List<HistoryWorkflowTask>listYes = historyWorkflowTaskManager.getCountersignByProcessInstanceIdResult(task.getProcessInstanceId(),name,TaskProcessingResult.AGREEMENT);
							List<HistoryWorkflowTask>listNo = historyWorkflowTaskManager.getCountersignByProcessInstanceIdResult(task.getProcessInstanceId(),name,TaskProcessingResult.OPPOSE);
							List<HistoryWorkflowTask>listInva = historyWorkflowTaskManager.getCountersignByProcessInstanceIdResult(task.getProcessInstanceId(),name,TaskProcessingResult.KIKEN);
							List<Integer> groupNums= historyWorkflowTaskManager.getGroupNumByTaskName(task.getProcessInstanceId(),name);
							for(Integer num:groupNums){
								int yesnum = 0,nonum = 0,invanum=0;
								List<WorkflowTask> list = new ArrayList<WorkflowTask>();
								List<HistoryWorkflowTask> resultList = new ArrayList<HistoryWorkflowTask>();
								for(HistoryWorkflowTask yesTask:listYes){
									if(num.equals(yesTask.getGroupNum())){
										yesnum++;
										resultList.add(yesTask);
									}
								}
								for(HistoryWorkflowTask noTask:listNo){
									if(num.equals(noTask.getGroupNum())){
										nonum++;
										resultList.add(noTask);
									}
								}
								for(HistoryWorkflowTask invaTask:listInva){
									if(num.equals(invaTask.getGroupNum())){
										invanum++;
										resultList.add(invaTask);
									}
								}
								Temp temp = new Temp(name,yesnum,nonum,invanum,list,resultList);
								temps.add(temp);
							}
						}else{
							List<WorkflowTask>listYes = taskService.getCountersignByProcessInstanceIdResult(task.getProcessInstanceId(),TaskProcessingMode.TYPE_VOTE,name,TaskProcessingResult.AGREEMENT);
							List<WorkflowTask>listNo = taskService.getCountersignByProcessInstanceIdResult(task.getProcessInstanceId(),TaskProcessingMode.TYPE_VOTE,name,TaskProcessingResult.OPPOSE);
							List<WorkflowTask>listInva = taskService.getCountersignByProcessInstanceIdResult(task.getProcessInstanceId(),TaskProcessingMode.TYPE_VOTE,name,TaskProcessingResult.KIKEN);
							List<Integer> groupNums= taskService.getGroupNumByTaskName(task.getProcessInstanceId(),name);
							for(Integer num:groupNums){
								int yesnum = 0,nonum = 0,invanum=0;
								List<WorkflowTask> resultList = new ArrayList<WorkflowTask>();
								List<HistoryWorkflowTask> histList = new ArrayList<HistoryWorkflowTask>();
								for(WorkflowTask yesTask:listYes){
									if(num.equals(yesTask.getGroupNum())){
										yesnum++;
										resultList.add(yesTask);
									}
								}
								for(WorkflowTask noTask:listNo){
									if(num.equals(noTask.getGroupNum())){
										nonum++;
										resultList.add(noTask);
									}
								}
								for(WorkflowTask invaTask:listInva){
									if(num.equals(invaTask.getGroupNum())){
										invanum++;
										resultList.add(invaTask);
									}
								}
								Temp temp = new Temp(name,yesnum,nonum,invanum,resultList,histList);
								temps.add(temp);
							}
						}
					}
				}
			} 
			else {
				message = "你没有权限查看投票结果";
			}
		} else {
			message = "没有任务id，无法查看投票结果";
		}
		ApiFactory.getBussinessLogService().log("自定义系统", 
				"查看投票结果", 
				ContextUtils.getSystemId("mms"));
		return SUCCESS;
	}

	/**
	 * 抄送
	 */
	@Action("copy-tache")
	public String copyTache() throws Exception{
		commonManager.createCopyTasks(taskId, Arrays.asList(assignee.split(",")), null, null);
		renderText("已抄送");
		return null;
	}
	
	/**
	 * 加签
	 */
	@Action("add-assign")
	public String addAssign() throws Exception{
		commonManager.addSigner(taskId, Arrays.asList(assignee.split(",")));
		renderText("加签成功！");
		return null;
	}
	
	/**
	 * 减签
	 */
	@Action("remove-assign")
	public String removeAssign() throws Exception{
		if(StringUtils.isNotEmpty(assignee)){
			commonManager.removeSigners(taskId, getUserIds(assignee));
			renderText("减签成功！");
		}else{
			renderText("不需要减签!");
		}
		return null;
	}
	
	private List<Long> getUserIds(String assigner){
		List<Long> idList = new ArrayList<Long>();
		String[] idStrs = assignee.split(",");
		for(String idStr:idStrs){
			idList.add(Long.parseLong(idStr));
		}
		return idList;
	}
	/**
	 * 减签树
	 */
	@Action("remove-assign-tree")
	public String removeAssignTree() throws Exception{
		if(taskId!=null){
			com.norteksoft.product.api.entity.WorkflowTask task = ApiFactory.getTaskService().getTask(taskId);
			List<ZTreeNode> treeNodes = new ArrayList<ZTreeNode>();
			
			WorkflowInstance instance = ApiFactory.getInstanceService().getInstance(task.getProcessInstanceId());
			String name = "";
			if(instance.getSubCompanyId()==null){
				name = ContextUtils.getCompanyName();
			}else{
				name = instance.getSubCompanyName();
			}
			String parentId = "company_company~~company~~company";
			ZTreeNode root = new ZTreeNode("company_company~~company~~company","0", name, "true", "false", "", "", "root", "");
			treeNodes.add(root);
			
			List<com.norteksoft.product.api.entity.WorkflowTask> currentTasks=ApiFactory.getTaskService().getActivityTaskByInstance(task.getProcessInstanceId());
			List<com.norteksoft.product.api.entity.WorkflowTask> currentPrincipalTasks=ApiFactory.getTaskService().getActivityPrincipalTasks(task.getProcessInstanceId());
			int transactNum=currentTasks.size()+currentPrincipalTasks.size();
			if(transactNum>1){
				for(int i=0;i<currentTasks.size();i++){
					com.norteksoft.product.api.entity.WorkflowTask currentTask=currentTasks.get(i);
					if((currentTask.getTransactorId()!=null&&!currentTask.getTransactorId().equals(ContextUtils.getUserId()))){//如果办理人不是当前用户
						String showName = currentTask.getTransactorName();
						Long transactorId = currentTask.getTransactorId();
						if(transactorId==null){
							User u = ApiFactory.getAcsService().getUserByLoginName(currentTask.getTransactor());
							if(u!=null)transactorId = u.getId();
						}
						String nodeId = "user~~"+ currentTask.getTransactorName()+"~~"+currentTask.getTransactor()+"~~"+(transactorId==null?"":transactorId);
						root = new ZTreeNode(nodeId,parentId, showName, "true", "false", "", "", "user", "");
						treeNodes.add(root);
					}
				}
				for(com.norteksoft.product.api.entity.WorkflowTask currentTask:currentPrincipalTasks){
					if((currentTask.getTransactorId()!=null&&!currentTask.getTransactor().equals(ContextUtils.getUserId()))){//如果办理人不是当前用户
						String showName = currentTask.getTrustorName();
						Long trustorId = currentTask.getTrustorId();
						if(trustorId==null){
							User u = ApiFactory.getAcsService().getUserByLoginName(currentTask.getTrustor());
							if(u!=null)trustorId = u.getId();
						}
						String nodeId = "user~~"+ currentTask.getTrustorName()+"~~"+currentTask.getTrustor()+"~~"+(trustorId==null?"":trustorId);
						root = new ZTreeNode(nodeId,parentId, showName, "true", "false", "", "", "user", "");
						treeNodes.add(root);
					}
				}
			}
			renderText(JsonParser.object2Json(treeNodes));
		}
		return null;
	}
	
	/**
	 * 打开指派树
	 */
	@Action("assign-tree")
	public String assign() throws Exception{
		commonManager.assign(taskId, Long.parseLong(assignee));
		renderText("已指派");
		return null;
	}
	
	
	
	/**
	 * 执行抄送
	 */
	@Action("copy")
	public String copy() throws Exception{
		commonManager.createCopyTaches(taskId,transactors,"","/common/task.htm");
		ApiFactory.getBussinessLogService().log("自定义系统", 
				"执行抄送", 
				ContextUtils.getSystemId("mms"));
		return null;
	}
	
	/*
	 * 领取任务
	 */
	@Action("drawTask")
	public String receive() throws Exception {
		ApiFactory.getBussinessLogService().log("自定义系统", 
				"领取任务", 
				ContextUtils.getSystemId("mms"));
		return  this.renderText(commonManager.receive(taskId));
	}
	
	/*
	 * 放弃领取的任务
	 */
	@Action("abandonReceive")
	public String abandonReceive() throws Exception {
		ApiFactory.getBussinessLogService().log("自定义系统", 
				"放弃领取的任务", 
				ContextUtils.getSystemId("mms"));
		return  this.renderText(commonManager.abandonReceive(taskId));
	}
	
	/////////////////////////////////////////////////////////////////////////////////
	
	private Map<String[], List<String[]>> candidates;
	private Map<String,String> canChoiceTaches;
	/**
	 * 指定办理人
	 */
	@Action(value = "task-assign")
	public String assignTransactor() throws Exception{
		CompleteTaskTipType result = commonManager.isNeedAssigningTransactor(taskId);
		switch(result){
		case OK:
		case MESSAGE:	
			addActionMessage(result.getContent());
			break;
		case RETURN_URL:
			candidates = commonManager.getNextTasksCandidates(taskId);
			addActionMessage("请选择办理人");
			break;
		case TACHE_CHOICE_URL:
			addActionMessage("请选择环节");
			canChoiceTaches = result.getCanChoiceTaches();
		case SINGLE_TRANSACTOR_CHOICE:
			
		}
		
		return "assign";
	}
	
	@Action("select-tache")
	public String selectTache(){
		CompleteTaskTipType result =  ApiFactory.getTaskService().completeWorkflowTask(taskId, null);
		choiceTache = result.getCanChoiceTaches();
		return "select-tache";
	}
	
	/**
	 * 完成交互
	 */
	@Action(value = "assign")
	public String completeInteractiveTask() throws Exception{
		if(StringUtils.isNotEmpty(transactor)){
			if(transactor.equals("allCompany")){//解析所有公司人员
				transactor="";
				List<Long> userIds = ApiFactory.getAcsService().getUserIdsByCompanyWithoutAdmin();
				transactor = userIds.toString().replace("[", "").replace("]", "").replace(" ", "");
			}
			if(transactor.equals("allGroup")){//解析所有工作组人员
				List<Long> userIds = ApiFactory.getAcsService().getUserIdsWithWorkgroup();
				transactor = userIds.toString().replace("[", "").replace("]", "").replace(" ", "");
			}
			 commonManager.setTasksTransactor(taskId, Arrays.asList(transactor.split(",")));
		}else if(StringUtils.isNotEmpty(tacheCode)){
			 commonManager.distributeTask(taskId, tacheCode,null);
		}
		FormView view = commonManager.getViewByTask(taskId);
		data = commonManager.getDataByTaskId(taskId);
		formCode = view.getCode();
		formVersion = view.getVersion();
		validateString = fieldReadOnly();
		ApiFactory.getBussinessLogService().log("自定义系统", 
				"执行指定办理人", 
				ContextUtils.getSystemId("mms"));
		return null;
	}
	
	/**
	 * 意见列表
	 */
	@Action(value = "opinion")
	public String opinion() throws Exception{
		opinionRight = commonManager.opinionRightByTask(taskId);
		opinions = commonManager.getOpinions(commonManager.getTaskByTaskId(taskId).getProcessInstanceId(),ContextUtils.getCompanyId());
		companyId = commonManager.getCompanyId();
		return SUCCESS;
	}
	
	/**
	 * 保存意见
	 */
	@Action(value = "save-opinion")
	public String saveOpinion() throws Exception {
		Opinion opi = new Opinion();
		opi.setTransactor(ContextUtils.getUserName());
		opi.setCreatedTime(new Date());
		opi.setOpinion(opinion);
		opi.setCompanyId(ContextUtils.getCompanyId());
		opi.setTaskId(taskId);
		commonManager.saveOpinion(opi);
		opinions = commonManager.getOpinions(commonManager.getTaskByTaskId(taskId).getProcessInstanceId(),ContextUtils.getCompanyId());
		opinionRight = commonManager.opinionRightByTask(taskId);
		ApiFactory.getBussinessLogService().log("自定义系统", 
				"保存意见", 
				ContextUtils.getSystemId("mms"));
		return "opinion";
	}
	
	/**
	 * 附件列表 
	 */
	@Action(value = "accessory")
	public String accessory() throws Exception{
		
		return SUCCESS;
	}
	
	/**
	 * 正文列表
	 */
	@Action(value = "do-text")
	public String doText()throws Exception {
		textRight = commonManager.textRightByTask(taskId);
		com.norteksoft.product.api.entity.WorkflowTask task = commonManager.getTaskByTaskId(taskId);
		workflowId = task.getProcessInstanceId();
		offices = commonManager.getDocumentsByInstance(task.getProcessInstanceId());
		ApiFactory.getBussinessLogService().log("自定义系统", 
				"正文列表", 
				ContextUtils.getSystemId("mms"));
		return "do-text";
	}
	
	
	/**
	 * 附件列表
	 */
	@Action(value = "do-attachment")
	public String doAttachment()throws Exception {
		textRight = commonManager.attachmentRightByTask(taskId);
		com.norteksoft.product.api.entity.WorkflowTask task = commonManager.getTaskByTaskId(taskId);
		workflowId = task.getProcessInstanceId();
		companyId = ContextUtils.getCompanyId();
		WorkflowAttachments = commonManager.getAttachments(task.getProcessInstanceId());
		ApiFactory.getBussinessLogService().log("自定义系统", 
				"附件列表", 
				ContextUtils.getSystemId("mms"));
		return SUCCESS;
	}
	

	/**
	 * 上传正文
	 */
	@Action(value = "do-upload")
	public String doUpload()throws Exception {
		HttpServletRequest request = ServletActionContext.getRequest();
		MultiPartRequestWrapper wrapper = (MultiPartRequestWrapper)request;
		File filePath = wrapper.getFiles("Filedata")[0];
		String fileName = request.getParameter("Filename");
		BufferedInputStream bis = new BufferedInputStream(new FileInputStream(filePath));
		Document document = new Document();
		
		document.setFileSize(bis.available());
		document.setFileName(fileName);
		if(request.getParameter("taskId") != null){
			long taskId = Long.valueOf(request.getParameter("taskId"));
			com.norteksoft.product.api.entity.WorkflowTask task = commonManager.getTaskByTaskId(taskId);
			document.setTaskId(taskId);
			document.setTaskName(task.getName());
			document.setWorkflowId(task.getProcessInstanceId());
		}
		String fileType = fileName.substring(fileName.lastIndexOf(".")+1,fileName.length());
		if(!("pdf").equals(fileType) ){
			fileType = "."+fileType;
		}
		document.setFileType(fileType);
		byte[] content=null;
		try {
			content = new byte[bis.available()];
			bis.read(content);
		} catch (IOException e) {
			e.printStackTrace();
		}finally{
			bis.close();
		}
		document.setFileBody(content);
        ApiFactory.getDocumentService().saveDocument(document);
        renderText("ok");
//        ApiFactory.getBussinessLogService().log("自定义系统", 
//				"删除正文", 
//				ContextUtils.getSystemId("mms"));
		return null;
	}
	
	
	/**
	 * 上传附件
	 */
	@Action(value = "do-attache-upload")
	public String doAttacheUpload()throws Exception {
		HttpServletRequest request = ServletActionContext.getRequest();
		MultiPartRequestWrapper wrapper = (MultiPartRequestWrapper)request;
		File filePath = wrapper.getFiles("Filedata")[0];
		String fileName = request.getParameter("Filename");
		BufferedInputStream bis = new BufferedInputStream(new FileInputStream(filePath));
		WorkflowAttachment attachment = new WorkflowAttachment();
		
		attachment.setFileName(fileName);
		if(request.getParameter("taskId") != null){
			long taskId = Long.valueOf(request.getParameter("taskId"));
			com.norteksoft.product.api.entity.WorkflowTask task = commonManager.getTaskByTaskId(taskId);
			attachment.setTaskId(taskId);
			attachment.setTaskName(task.getName());
			attachment.setWorkflowId(task.getProcessInstanceId());
		}
		String fileType = fileName.substring(fileName.lastIndexOf(".")+1,fileName.length());
		if(!("pdf").equals(fileType) ){
			fileType = "."+fileType;
		}
		attachment.setFileType(fileType);
		byte[] content=null;
		try {
			content = new byte[bis.available()];
			bis.read(content);
		} catch (IOException e) {
			e.printStackTrace();
		}finally{
			bis.close();
		}
		attachment.setFileBody(content);
        ApiFactory.getAttachmentService().saveAttachment(attachment);
        renderText("ok");
//        ApiFactory.getBussinessLogService().log("自定义系统", 
//				"删除正文", 
//				ContextUtils.getSystemId("mms"));
		return null;
	}
	
	/**
	 * 删除正文
	 */
	@Action(value = "delete-document")
	public String deleteDocument() throws Exception{
		ApiFactory.getDocumentService().deleteDocument(documentId);
		return null;
	}
	
	/**
	 * 删除附件
	 */
	@Action(value = "delete-attachment")
	public String deleteAttachment() throws Exception{
		ApiFactory.getAttachmentService().deleteAttachment(documentId);
		return null;
	}
	/**
	 * 下载正文
	 */
	@Action(value = "download-document")
	public String downloadDocument() throws Exception{
		Document doc =  ApiFactory.getDocumentService().getDocument(documentId);
		this.download(doc.getFileName(),doc.getFileBody());
		return null;
	}
	
	
	/**
	 * 下载文档
	 * @param fileName
	 * @param content
	 * @throws IOException 
	 */
	private void download(String fileName,byte[] content) throws IOException{
		BufferedInputStream bis = new BufferedInputStream(new ByteArrayInputStream(content));
		HttpServletResponse response = ServletActionContext.getResponse();
		response.reset();
		response.setContentType("application/x-download");
		OutputStream out=null;
		try {
			byte[] byname=fileName.getBytes("gbk");
			fileName=new String(byname,"8859_1");
			response.addHeader("Content-Disposition", "attachment;filename=\""+fileName+"\"");
			out=response.getOutputStream();
			byte[] buffer = new byte[4096];
			int size = 0;
			while ((size = bis.read(buffer, 0, buffer.length)) != -1) {
				out.write(buffer, 0, size);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}finally{
			out.close();
			bis.close();
		}
	}
	
	/**
	 * 下载附件
	 */
	@Action(value = "download-attachment")
	public String downloadAttachment() throws Exception{
		WorkflowAttachment doc =  ApiFactory.getAttachmentService().getAttachment(documentId);
		this.download(doc.getFileName(),doc.getFileBody());
		return null;
	}
	
	/**
	 * 保存附件
	 */
	@Action(value = "save-accessory")
	public String saveAccessory() throws Exception{
		
		return "accessory";
	}
	
	@Action("custom-list-control-deleteRow")
	public String deleteRow() throws Exception{
		DataTable dataTable=dataTableManager.getDataTableByTableName(dataTableName);
		commonManager.deleteRow(dataTable.getId(), dataId);
		this.renderText("ok");
		return null;
	}
	
	@Override
	protected void prepareModel() throws Exception {
	}

	public Object getModel() {
		return null;
	}

	public Map<String[], List<String[]>> getCandidates() {
		return candidates;
	}

	public Map<String, String> getCanChoiceTaches() {
		return canChoiceTaches;
	}

	public Page<Object> getPage() {
		return page;
	}

	public void setPage(Page<Object> page) {
		this.page = page;
	}

	public String getListCode() {
		return listCode;
	}

	public void setListCode(String listCode) {
		this.listCode = listCode;
	}

	public String getFormCode() {
		return formCode;
	}

	public void setFormCode(String formCode) {
		this.formCode = formCode;
	}

	public Object getData() {
		return data;
	}

	public Integer getFormVersion() {
		return formVersion;
	}

	public ModulePage getModulePage() {
		return modulePage;
	}

	public Long getPageId() {
		return pageId;
	}

	public void setPageId(Long pageId) {
		this.pageId = pageId;
	}

	public Long getMenuId() {
		return menuId;
	}

	public void setMenuId(Long menuId) {
		this.menuId = menuId;
	}

	public String getQueryString() {
		return queryString;
	}

	public String getValidateString() {
		return validateString;
	}

	public String getWorkflowUrl() {
		return workflowUrl;
	}

	public List<WorkflowDefinition> getWorkflows() {
		return workflows;
	}

	public String getProcessId() {
		return processId;
	}

	public void setProcessId(String processId) {
		this.processId = processId;
	}

	public Long getTaskId() {
		return taskId;
	}

	public void setTaskId(Long taskId) {
		this.taskId = taskId;
	}

	public String getChooseUrl() {
		return chooseUrl;
	}

	public Long getCompanyId() {
		return companyId;
	}

	public void setTransactors(List<String> transactors) {
		this.transactors = transactors;
	}

	public void setTargetNames(String[] targetNames) {
		this.targetNames = targetNames;
	}


	public List<String> getOpinionRight() {
		return opinionRight;
	}

	public void setOpinionRight(List<String> opinionRight) {
		this.opinionRight = opinionRight;
	}

	public List<Opinion> getOpinions() {
		return opinions;
	}

	public void setOpinions(List<Opinion> opinions) {
		this.opinions = opinions;
	}

	public String getOpinion() {
		return opinion;
	}

	public void setOpinion(String opinion) {
		this.opinion = opinion;
	}

	public TaskService getTaskService() {
		return taskService;
	}

	public void setTaskService(TaskService taskService) {
		this.taskService = taskService;
	}

	public List<Temp> getTemps() {
		return temps;
	}

	public void setTemps(List<Temp> temps) {
		this.temps = temps;
	}

	public WorkflowInstanceManager getWorkflowInstanceManager() {
		return workflowInstanceManager;
	}

	public void setWorkflowInstanceManager(
			WorkflowInstanceManager workflowInstanceManager) {
		this.workflowInstanceManager = workflowInstanceManager;
	}

	public CommonManager getCommonManager() {
		return commonManager;
	}

	public void setCommonManager(CommonManager commonManager) {
		this.commonManager = commonManager;
	}

	public ModulePageManager getModulePageManager() {
		return modulePageManager;
	}

	public void setModulePageManager(ModulePageManager modulePageManager) {
		this.modulePageManager = modulePageManager;
	}

	public MenuManager getMenuManager() {
		return menuManager;
	}

	public void setMenuManager(MenuManager menuManager) {
		this.menuManager = menuManager;
	}

	public FormViewManager getFormViewManager() {
		return formViewManager;
	}

	public void setFormViewManager(FormViewManager formViewManager) {
		this.formViewManager = formViewManager;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}


	public List<String> getTransactors() {
		return transactors;
	}

	public String[] getTargetNames() {
		return targetNames;
	}

	public void setData(Object data) {
		this.data = data;
	}

	public void setFormVersion(Integer formVersion) {
		this.formVersion = formVersion;
	}

	public void setQueryString(String queryString) {
		this.queryString = queryString;
	}

	public void setValidateString(String validateString) {
		this.validateString = validateString;
	}

	public void setModulePage(ModulePage modulePage) {
		this.modulePage = modulePage;
	}

	public void setWorkflowUrl(String workflowUrl) {
		this.workflowUrl = workflowUrl;
	}

	public void setWorkflows(List<WorkflowDefinition> workflows) {
		this.workflows = workflows;
	}

	public void setChooseUrl(String chooseUrl) {
		this.chooseUrl = chooseUrl;
	}

	public void setCompanyId(Long companyId) {
		this.companyId = companyId;
	}

	public void setCandidates(Map<String[], List<String[]>> candidates) {
		this.candidates = candidates;
	}

	public void setCanChoiceTaches(Map<String, String> canChoiceTaches) {
		this.canChoiceTaches = canChoiceTaches;
	}

	public boolean isView() {
		return view;
	}
	public boolean isInstanceInHistory() {
		return instanceInHistory;
	}

	public void setView(boolean view) {
		this.view = view;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public WorkflowRightsManager getWorkflowRightsManager() {
		return workflowRightsManager;
	}

	public void setWorkflowRightsManager(WorkflowRightsManager workflowRightsManager) {
		this.workflowRightsManager = workflowRightsManager;
	}

	public WorkflowTaskManager getWorkflowTaskManager() {
		return workflowTaskManager;
	}

	public void setWorkflowTaskManager(WorkflowTaskManager workflowTaskManager) {
		this.workflowTaskManager = workflowTaskManager;
	}

	public String getTransactor() {
		return transactor;
	}

	public List<String> getTextRight() {
		return textRight;
	}

	public void setTextRight(List<String> textRight) {
		this.textRight = textRight;
	}

	public List<Document> getOffices() {
		return offices;
	}

	public void setOffices(List<Document> offices) {
		this.offices = offices;
	}

	public List<WorkflowAttachment> getWorkflowAttachments() {
		return WorkflowAttachments;
	}

	public void setWorkflowAttachments(List<WorkflowAttachment> workflowAttachments) {
		WorkflowAttachments = workflowAttachments;
	}

	public WorkflowAttachment getWorkflowAttachment() {
		return workflowAttachment;
	}

	public void setWorkflowAttachment(WorkflowAttachment workflowAttachment) {
		this.workflowAttachment = workflowAttachment;
	}

	public String getDataIds() {
		return dataIds;
	}

	public void setDataIds(String dataIds) {
		this.dataIds = dataIds;
	}

	public void setTransactor(String transactor) {
		this.transactor = transactor;
	}
	public String getDeleteMsg() {
		return deleteMsg;
	}

	public String getWorkflowId() {
		return workflowId;
	}

	public void setWorkflowId(String workflowId) {
		this.workflowId = workflowId;
	}

	public void setDeleteMsg(String deleteMsg) {
		this.deleteMsg = deleteMsg;
	}
	private void addActionSuccessMessage(String message){
		this.addActionMessage(SUCCESS_MESSAGE_LEFT+message+MESSAGE_RIGHT);
	}

	public List<Long> getDeleteIds() {
		return deleteIds;
	}

	public void setDeleteIds(List<Long> deleteIds) {
		this.deleteIds = deleteIds;
	}

	public String getOnlyTable() {
		return onlyTable;
	}

	public void setOnlyTable(String onlyTable) {
		this.onlyTable = onlyTable;
	}

	public String getAssignee() {
		return assignee;
	}

	public void setAssignee(String assignee) {
		this.assignee = assignee;
	}

	public Long getDocumentId() {
		return documentId;
	}

	public void setDocumentId(Long documentId) {
		this.documentId = documentId;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Map<Long, User> getChoiceTransactor() {
		return choiceTransactor;
	}

	public String getTacheCode() {
		return tacheCode;
	}

	public void setTacheCode(String tacheCode) {
		this.tacheCode = tacheCode;
	}

	public boolean isCloseFlag() {
		return closeFlag;
	}

	public void setCloseFlag(boolean closeFlag) {
		this.closeFlag = closeFlag;
	}
	public void setInstanceId(String instanceId) {
		this.instanceId = instanceId;
	}

	public String getInstanceId() {
		return instanceId;
	}

	public TaskPermission getPermission() {
		return permission;
	}

	public void setPermission(TaskPermission permission) {
		this.permission = permission;
	}

	public Long getDataId() {
		return dataId;
	}

	public void setDataId(Long dataId) {
		this.dataId = dataId;
	}

	public boolean getMonitorFlag() {
		return monitorFlag;
	}

	public void setMonitorFlag(boolean monitorFlag) {
		this.monitorFlag = monitorFlag;
	}

	public Long getToPageId() {
		return toPageId;
	}
	public void setToPageId(Long toPageId) {
		this.toPageId = toPageId;
	}
	public Integer getOpinionSize() {
		return opinionSize;
	}
	public void setOpinionSize(Integer opinionSize) {
		this.opinionSize = opinionSize;
	}

	public String getDoneOpinion() {
		return doneOpinion;
	}

	public void setDoneOpinion(String doneOpinion) {
		this.doneOpinion = doneOpinion;
	}

	public boolean isHasBranch() {
		return hasBranch;
	}

	public Map<String, String> getChoiceTache() {
		return choiceTache;
	}

	public String getFormCodes() {
		return formCodes;
	}

	public String getFormVersions() {
		return formVersions;
	}

	public String getRichString() {
		return richString;
	}

	public void setRichString(String richString) {
		this.richString = richString;
	}

	public boolean isCanSubmitTask() {
		return canSubmitTask;
	}

	public void setCanSubmitTask(boolean canSubmitTask) {
		this.canSubmitTask = canSubmitTask;
	}

	public String getDataTableName() {
		return dataTableName;
	}

	public void setDataTableName(String dataTableName) {
		this.dataTableName = dataTableName;
	}

}
