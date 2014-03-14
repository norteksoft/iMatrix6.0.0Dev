package com.norteksoft.tags.workflow;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.tagext.Tag;
import javax.servlet.jsp.tagext.TagSupport;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.norteksoft.product.api.ApiFactory;
import com.norteksoft.product.util.ContextUtils;
import com.norteksoft.product.util.freemarker.TagUtil;
import com.norteksoft.wf.engine.entity.WorkflowDefinition;
import com.norteksoft.wf.engine.service.WorkflowDefinitionManager;

public class WorkflowButtonGroupTag extends TagSupport {
	private static final long serialVersionUID = 1L;
	private Log log = LogFactory.getLog(WorkflowButtonGroupTag.class); 
	
	private Long taskId;
	
	private String formCode;
	
	private Integer version;
	
	private WorkflowDefinitionManager workflowDefinitionManager;
	
	
	private String webRoot;
	private String name = "workflowButtonGroup";
	
	private Boolean showOtherButton=true;
	@Override
	public int doStartTag() throws JspException{
		workflowDefinitionManager = (WorkflowDefinitionManager)ContextUtils.getBean("workflowDefinitionManager");
		if(taskId != null && taskId != 0l){
			com.norteksoft.product.api.entity.WorkflowTask task = ApiFactory.getTaskService().getTask(taskId);
			com.norteksoft.product.api.entity.WorkflowInstance workflow = ApiFactory.getInstanceService().getInstance(task.getProcessInstanceId());
			Long definitionId = workflow.getWorkflowDefinitionId();
			WorkflowDefinition definition = workflowDefinitionManager.getWfDefinition(definitionId);
			formCode = definition.getFormCode();
			version = definition.getFromVersion();
		}
		try {
			 JspWriter out=pageContext.getOut(); 
			 out.print(readTemplet());
		} catch (Exception e) {
			log.error(e);
			throw new JspException(e);
		}
		return Tag.EVAL_PAGE;
	 }
	
	
	private String readTemplet() throws Exception {
		webRoot = ((HttpServletRequest)this.pageContext.getRequest()).getContextPath();
		com.norteksoft.product.api.entity.WorkflowTask task = null;
		com.norteksoft.product.api.entity.WorkflowInstance workflow = null;
		boolean isFirstTask=false;
		if(taskId!=null&&taskId!=0l){
			task = ApiFactory.getTaskService().getTask(taskId);
			workflow = ApiFactory.getInstanceService().getInstance(task.getProcessInstanceId());
			if(taskId.equals(workflow.getFirstTaskId())){
				isFirstTask=true;
			}
		}
		Map<String, Object> root = new HashMap<String, Object>();
		root.put("formCode", formCode);
		root.put("version", version);
		root.put("ctx", webRoot);
		root.put("taskId", taskId);
		root.put("task", task);
		root.put("workflow", workflow);
		root.put("workflowButtonGroupName", name);
		root.put("isFirstTask", isFirstTask);
		root.put("companyId", ContextUtils.getCompanyId().toString());
		root.put("showOtherButton", showOtherButton==null?true:showOtherButton);
		String result =TagUtil.getContent(root, "workflow/workflowButtonGroup.ftl");
		return result;
	}
	
	@Override
	public int doEndTag() throws JspException {
		return Tag.EVAL_PAGE;
	}
	public void setTaskId(Long taskId) {
		this.taskId = taskId;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setVersion(Integer version) {
		this.version = version;
	}

	public void setFormCode(String formCode) {
		this.formCode = formCode;
	}
	public void setShowOtherButton(Boolean showOtherButton) {
		this.showOtherButton = showOtherButton;
	}
}
