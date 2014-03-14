package com.norteksoft.wf.engine.web.rest;

import javax.ws.rs.Consumes;
import javax.ws.rs.FormParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.norteksoft.product.api.ApiFactory;
import com.norteksoft.product.api.entity.User;
import com.norteksoft.product.util.ParameterUtils;
import com.norteksoft.product.util.ThreadParameters;
import com.norteksoft.product.util.ThreadPool;
import com.norteksoft.wf.base.utils.WorkflowFinishThread;
import com.norteksoft.wf.engine.service.WorkflowFinishManager;

@Component
@Path("/workflow")
public class WorkflowFinishTimer {
	@Autowired
	private WorkflowFinishManager workflowFinishManager;
	
	@POST
	@Path("/finish")
	@Produces("text/html;charset=UTF-8")
	@Consumes("text/html;charset=UTF-8")
	public Response userAuthentication(@FormParam("runAsUser")String loginName,@FormParam("runAsUserId")String userId,@FormParam("companyId")String companyId) {
		try {
			WorkflowFinishThread thread = new  WorkflowFinishThread();
			if(StringUtils.isNotEmpty(companyId)){
				ThreadParameters parameters = new ThreadParameters(Long.parseLong(companyId));
				ParameterUtils.setParameters(parameters);
				User user = null;
				if(StringUtils.isNotEmpty(userId)){
					user = ApiFactory.getAcsService().getUserById(Long.parseLong(userId));
				}else{
					user = ApiFactory.getAcsService().getUserByLoginName(loginName);
				}
				if(user!=null){
					thread.setCompanyId(user.getCompanyId());
					thread.setLoginName(user.getLoginName());
					thread.setUserId(user.getId());
					thread.setUserName(user.getName());
					thread.setWorkflowFinishManager(workflowFinishManager);
					ThreadPool.execute(thread);
				}
			}
		} catch (Exception e) {
			return Response.status(201).entity(e.getMessage()).build();
		}
		return Response.status(201).entity(" workflow finish ok ").build();
	}
}
