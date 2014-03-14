package com.norteksoft.wf.base.utils;

import java.io.Serializable;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.norteksoft.product.util.ParameterUtils;
import com.norteksoft.product.util.ThreadParameters;
import com.norteksoft.wf.engine.service.WorkflowFinishManager;

@Service
@Transactional(readOnly=false)
public class WorkflowFinishThread implements Runnable,Serializable{
	private static final long serialVersionUID = 1L;
	private Long companyId;
	
	private String userName;
	
	private String loginName;
	
	private Long userId;
	
	private WorkflowFinishManager workflowFinishManager;
	
	public void run() {
		ThreadParameters parameters=new ThreadParameters();
		parameters.setLoginName(loginName);
		parameters.setCompanyId(companyId);
		parameters.setUserId(userId);
		parameters.setUserName(userName);
		ParameterUtils.setParameters(parameters);
		workflowFinishManager.execute();
	}
	public void setCompanyId(Long companyId) {
		this.companyId = companyId;
	}
	public void setUserName(String userName) {
		this.userName = userName;
	}
	public void setLoginName(String loginName) {
		this.loginName = loginName;
	}
	public void setUserId(Long userId) {
		this.userId = userId;
	}
	public void setWorkflowFinishManager(WorkflowFinishManager workflowFinishManager) {
		this.workflowFinishManager = workflowFinishManager;
	}

}
