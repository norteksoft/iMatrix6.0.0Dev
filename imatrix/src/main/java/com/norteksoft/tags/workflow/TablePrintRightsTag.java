package com.norteksoft.tags.workflow;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.Tag;
import javax.servlet.jsp.tagext.TagSupport;

import com.norteksoft.product.api.ApiFactory;
import com.norteksoft.product.api.entity.TaskPermission;

public class TablePrintRightsTag extends TagSupport {
	private static final long serialVersionUID = 4L;
	
	private Long taskId;
	
	public void setTaskId(Long taskId) {
		this.taskId = taskId;
	}
	
	@Override
	public int doStartTag() throws JspException {
		TaskPermission  permission = ApiFactory.getPermissionService().getActivityPermission(taskId);
		boolean rights = permission.getFormPrintable();
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
