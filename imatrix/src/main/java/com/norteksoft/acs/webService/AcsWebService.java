package com.norteksoft.acs.webService;
import javax.ws.rs.Consumes;
import javax.ws.rs.FormParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.norteksoft.acs.service.organization.AsynOrgManager;
@Service
@Path("/test")
@Transactional
public class AcsWebService{
	@Autowired
	private AsynOrgManager asynOrgManager;
	@POST
	@Path("/saveUser")
	@Produces("text/html;charset=UTF-8")
	@Consumes("text/html;charset=UTF-8")
	public Response saveUser(
			@FormParam("path")String path,@FormParam("loginName")String loginName,@FormParam("name")String name,
			@FormParam("password")String password
	) {
		return asynOrgManager.saveUserForWebService(path,loginName,name,password);
	}
	@POST
	@Path("/deleteUser")
	@Produces("text/html;charset=UTF-8")
	@Consumes("text/html;charset=UTF-8")
	public Response deleteUser(
			@FormParam("path")String path,@FormParam("loginName")String loginName
	) {
		return asynOrgManager.deleteUserForWebService(path,loginName);
	}
	@POST
	@Path("/saveDepartment")
	@Produces("text/html;charset=UTF-8")
	@Consumes("text/html;charset=UTF-8")
	public Response saveDepartment(
			@FormParam("path")String path,@FormParam("branchFlag")Boolean branchFlag,@FormParam("name")String name,@FormParam("departmentCode")String departmentCode
	) {
		return asynOrgManager.saveDepartmentForWebService(path,branchFlag,name,departmentCode);
	}
	@POST
	@Path("/deleteDepartment")
	@Produces("text/html;charset=UTF-8")
	@Consumes("text/html;charset=UTF-8")
	public Response deleteDepartment(
			@FormParam("path")String path,@FormParam("departmentCode")String departmentCode
	) {
		return asynOrgManager.deleteDepartmentForWebService(path,departmentCode);
	}
}