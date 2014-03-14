package com.norteksoft.tags.workflow;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.Tag;
import javax.servlet.jsp.tagext.TagSupport;

import com.norteksoft.product.api.ApiFactory;
import com.norteksoft.product.util.ContextUtils;
import com.norteksoft.wf.engine.service.WorkflowRightsManager;

public class DeleteRightsTag extends TagSupport {
	private static final long serialVersionUID = 5L;
	
	private Long taskId;
	
	public void setTaskId(Long taskId) {
		this.taskId = taskId;
	}
	
	@Override
	public int doStartTag() throws JspException {
		WorkflowRightsManager rightsManager = (WorkflowRightsManager)ContextUtils.getBean("workflowRightsManager");
		com.norteksoft.product.api.entity.WorkflowTask task = ApiFactory.getTaskService().getTask(taskId);
		com.norteksoft.product.api.entity.WorkflowInstance instance = ApiFactory.getInstanceService().getInstance(task.getProcessInstanceId());
		boolean rights = rightsManager.workflowDeleteRight(instance, task.getName());
		if(rights) {
			return Tag.EVAL_PAGE;
		}
		return Tag.SKIP_BODY;
	}
	
	@Override
	public int doEndTag() throws JspException {
		return Tag.EVAL_PAGE;
	}
}
