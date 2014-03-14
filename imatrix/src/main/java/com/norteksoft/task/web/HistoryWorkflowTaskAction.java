package com.norteksoft.task.web;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.struts2.convention.annotation.Action;
import org.apache.struts2.convention.annotation.Namespace;
import org.apache.struts2.convention.annotation.ParentPackage;
import org.apache.struts2.convention.annotation.Result;
import org.apache.struts2.convention.annotation.Results;
import org.springframework.beans.factory.annotation.Autowired;

import com.norteksoft.product.api.ApiFactory;
import com.norteksoft.product.api.entity.Menu;
import com.norteksoft.product.orm.Page;
import com.norteksoft.product.util.ContextUtils;
import com.norteksoft.product.util.JsTreeUtils;
import com.norteksoft.product.util.JsonParser;
import com.norteksoft.product.util.PageUtils;
import com.norteksoft.product.util.SystemUrls;
import com.norteksoft.product.util.tree.ZTreeNode;
import com.norteksoft.product.web.struts2.CrudActionSupport;
import com.norteksoft.product.web.struts2.Struts2Utils;
import com.norteksoft.task.base.enumeration.TaskCategory;
import com.norteksoft.task.base.enumeration.TaskType;
import com.norteksoft.task.entity.HistoryWorkflowTask;
import com.norteksoft.task.entity.TaskMark;
import com.norteksoft.task.service.HistoryWorkflowTaskManager;

@Namespace("/task")
@ParentPackage("default")
@Results( { @Result(name = CrudActionSupport.RELOAD, location = "history-workflow-task", type = "redirectAction")})
public class HistoryWorkflowTaskAction extends CrudActionSupport<HistoryWorkflowTask>{
	private static final long serialVersionUID = 4658506181455886084L;
	
	private Long id;
	private HistoryWorkflowTask historyWorkflowTask;
	private Page<HistoryWorkflowTask> page = new Page<HistoryWorkflowTask>(0, true);
	private String taskCategory;//任务状态，其值为：complete,cancel
	private String taskType;//任务类别：默认类别，流程名称，流程自定义类别
	private String typeName;
	private String ids;
	private TaskMark taskMarks;//标识的颜色
	private String taskUrl;
	
	@Autowired
	private HistoryWorkflowTaskManager historyWorkflowTaskManager;
	
	@Override
	public String delete() throws Exception {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	@Action("history-workflow-task-input")
	public String input() throws Exception {
		if(!historyWorkflowTask.getRead()){
			historyWorkflowTask.setRead(true);
			historyWorkflowTaskManager.saveHistoryWorkflowTask(historyWorkflowTask);
			}
		taskUrl = getTaskUrl(historyWorkflowTask);
			return INPUT;
	}
	
	private String getTaskUrl(HistoryWorkflowTask historyWorkflowTask){
		String url=historyWorkflowTask.getUrl();
		if(!historyWorkflowTask.getUrl().contains("http://")&&historyWorkflowTask.getUrl().contains("?")){
			url=SystemUrls.getSystemUrl(StringUtils.substringBefore(historyWorkflowTask.getUrl(), "/"))+StringUtils.substring(historyWorkflowTask.getUrl(), historyWorkflowTask.getUrl().indexOf('/'))+historyWorkflowTask.getSourceTaskId();
		}else if(!historyWorkflowTask.getUrl().contains("http://")){
			url=SystemUrls.getSystemUrl(StringUtils.substringBefore(historyWorkflowTask.getUrl(), "/"))+StringUtils.substring(historyWorkflowTask.getUrl(), historyWorkflowTask.getUrl().indexOf('/'))+"?taskId="+historyWorkflowTask.getSourceTaskId();
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
	
	/**
	 * 已完成任务列表
	 */
	@Override
	@Action("history-workflow-task")
	public String list() throws Exception {
		Menu menu=ApiFactory.getMmsService().getTopMenu("historyWorkflowTask");
		if(menu!=null){
			Struts2Utils.getRequest().setAttribute("menuId", menu.getId());
		}
		taskCategory="complete";
		if("default_type".equals(typeName)||"custom_type".equals(typeName)||"workflow_name".equals(typeName)){
			typeName="";
		}
		if(TaskType.WORKFLOW_NAME.toString().equalsIgnoreCase(taskType)){
			if(page.getPageSize()>1){
				historyWorkflowTaskManager.getCompletedTasksByGroupName(page,typeName);
				ApiFactory.getBussinessLogService().log("历史事宜", 
						"已完成任务列表", 
						ContextUtils.getSystemId("task"));
				this.renderText(PageUtils.pageToJson(page));
				return null;
			}
		}else if(TaskType.CUSTOM_TYPE.toString().equalsIgnoreCase(taskType)){
			if(page.getPageSize()>1){
				historyWorkflowTaskManager.getCompletedTasksByCustomType(page,typeName);
				ApiFactory.getBussinessLogService().log("历史事宜", 
						"已完成任务列表", 
						ContextUtils.getSystemId("task"));
				this.renderText(PageUtils.pageToJson(page));
				return null;
			}
		}else{
			if(page.getPageSize()>1){
				historyWorkflowTaskManager.getCompletedTasksByUserType(page,typeName);
				ApiFactory.getBussinessLogService().log("历史事宜", 
						"已完成任务列表", 
						ContextUtils.getSystemId("task"));
				this.renderText(PageUtils.pageToJson(page));
				return null;
			}
		}
		return "history-workflow-task";
	}
	
	/**
	 * 已取消任务列表
	 */
	@Action("history-workflow-task-canceled")
	public String canceledTasks() throws Exception{
		Menu menu=ApiFactory.getMmsService().getTopMenu("historyWorkflowTask");
		if(menu!=null){
			Struts2Utils.getRequest().setAttribute("menuId", menu.getId());
		}
		if("default_type".equals(typeName)||"custom_type".equals(typeName)||"workflow_name".equals(typeName)){
			typeName="";
		}
		if(TaskType.WORKFLOW_NAME.toString().equalsIgnoreCase(taskType)){
			if(page.getPageSize()>1){
				historyWorkflowTaskManager.getCancelTasksByGroupName(page,typeName);
				ApiFactory.getBussinessLogService().log("历史事宜", 
						"已取消任务列表", 
						ContextUtils.getSystemId("task"));
				this.renderText(PageUtils.pageToJson(page));
				return null;
			}
		}else if(TaskType.CUSTOM_TYPE.toString().equalsIgnoreCase(taskType)){
			if(page.getPageSize()>1){
				historyWorkflowTaskManager.getCancelTasksByCustomType(page,typeName);
				ApiFactory.getBussinessLogService().log("历史事宜", 
						"已取消任务列表", 
						ContextUtils.getSystemId("task"));
				this.renderText(PageUtils.pageToJson(page));
				return null;
			}
		}else{
			if(page.getPageSize()>1){
				historyWorkflowTaskManager.getCanceledTasksByUserType(page,typeName);
				ApiFactory.getBussinessLogService().log("历史事宜", 
						"已取消任务列表", 
						ContextUtils.getSystemId("task"));
				this.renderText(PageUtils.pageToJson(page));
				return null;
			}
		}
		return "history-workflow-task-canceled";
	}
	
	/**
	 * 标记任务
	 * @return
	 * @throws Exception
	 */
	@Action("history-workflow-task-mark")
	public String mark() throws Exception{
		String[] idStr=ids.split(",");
		for(int i=0;i<idStr.length;i++){
			historyWorkflowTaskManager.changeTaskMark(Long.parseLong(idStr[i]),taskMarks);
		}
		ApiFactory.getBussinessLogService().log("历史事宜", 
				"标记任务", 
				ContextUtils.getSystemId("task"));
		if(TaskCategory.COMPLETE.equals(taskCategory)){
			return list();
		}else {
			return canceledTasks();
		}
	}
	
	@Override
	protected void prepareModel() throws Exception {
		if(id==null){
			historyWorkflowTask=new HistoryWorkflowTask();
		}else{
			historyWorkflowTask=historyWorkflowTaskManager.getHistoryWorkflowTask(id);
		}
		
	}
	@Override
	public String save() throws Exception {
		// TODO Auto-generated method stub
		return null;
	}
	
	/**
	 * 任务类型树
	 * @return
	 * @throws Exception
	 */
	@Action("history-task-type-tree")
	public String typeTree() throws Exception{
		List<ZTreeNode> treeNodes = new ArrayList<ZTreeNode>();
		
		List<Object[]> typeInfos=null;
		if(TaskType.WORKFLOW_NAME.toString().equalsIgnoreCase(taskType)){
			typeInfos=historyWorkflowTaskManager.getGroupNames(taskCategory);
			ZTreeNode root = new ZTreeNode("workflow_name","0","流程名称", "true", "false", "", "", "folder", "");
			treeNodes.add(root);
			typeTree(typeInfos,treeNodes,"workflow_name");
		}else if(TaskType.CUSTOM_TYPE.toString().equalsIgnoreCase(taskType)){
			typeInfos=historyWorkflowTaskManager.getCustomTypes(taskCategory);
			ZTreeNode root = new ZTreeNode("custom_type","0","流程自定义类别", "true", "false", "", "", "folder", "");
			treeNodes.add(root);
			typeTree(typeInfos,treeNodes,"custom_type");
		}else{
			typeInfos=historyWorkflowTaskManager.getTypeInfos(taskCategory);
			ZTreeNode root = new ZTreeNode("default_type","0","默认类别", "true", "false", "", "", "folder", "");
			treeNodes.add(root);
			typeTree(typeInfos,treeNodes,"default_type");
		}
		this.renderText(JsonParser.object2Json(treeNodes));
		return null;
	}
	
	private void typeTree(List<Object[]> typeInfos,List<ZTreeNode> treeNodes,String parentId){
		Integer taskNum=historyWorkflowTaskManager.getAllTaskNumByUser(taskCategory);
		StringBuilder tree=new StringBuilder();
		tree.append("[");
		if(TaskCategory.COMPLETE.equals(taskCategory)){
			ZTreeNode root = new ZTreeNode("complete_task",parentId,"所有事宜", "false", "false", "", "", "folder", "");
			treeNodes.add(root);
		}else if(TaskCategory.CANCEL.equals(taskCategory)){
			ZTreeNode root = new ZTreeNode("cancel_task",parentId,"所有事宜", "false", "false", "", "", "folder", "");
			treeNodes.add(root);
		}else{
			ZTreeNode root = new ZTreeNode("active_task",parentId,"所有事宜("+taskNum+")", "false", "false", "", "", "folder", "");
			treeNodes.add(root);
		}
		for(Object[] objs:typeInfos){
			String typeName=(String)objs[0];
			Long countTask=(Long)objs[1];
			if(StringUtils.isNotEmpty(typeName)){
				if(TaskCategory.COMPLETE.equals(taskCategory)||TaskCategory.CANCEL.equals(taskCategory)){
					ZTreeNode root = new ZTreeNode(typeName,parentId,typeName, "false", "false", "", "", "folder", "");
					treeNodes.add(root);
				}else{
					ZTreeNode root = new ZTreeNode(typeName,parentId,typeName+"("+countTask+")", "false", "false", "", "", "folder", "");
					treeNodes.add(root);
				}
			}
		}
	}
	
	public HistoryWorkflowTask getModel() {
		return historyWorkflowTask;
	}
	
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public HistoryWorkflowTask getHistoryWorkflowTask() {
		return historyWorkflowTask;
	}
	public void setHistoryWorkflowTask(HistoryWorkflowTask historyWorkflowTask) {
		this.historyWorkflowTask = historyWorkflowTask;
	}
	public Page<HistoryWorkflowTask> getPage() {
		return page;
	}
	public void setPage(Page<HistoryWorkflowTask> page) {
		this.page = page;
	}
	public String getTaskCategory() {
		return taskCategory;
	}
	public void setTaskCategory(String taskCategory) {
		this.taskCategory = taskCategory;
	}
	public String getTaskType() {
		return taskType;
	}
	public void setTaskType(String taskType) {
		this.taskType = taskType;
	}
	public String getTypeName() {
		return typeName;
	}
	public void setTypeName(String typeName) {
		this.typeName = typeName;
	}
	public String getIds() {
		return ids;
	}
	public void setIds(String ids) {
		this.ids = ids;
	}
	public TaskMark getTaskMarks() {
		return taskMarks;
	}
	public void setTaskMarks(TaskMark taskMarks) {
		this.taskMarks = taskMarks;
	}
	public String getTaskUrl() {
		return taskUrl;
	}
}
