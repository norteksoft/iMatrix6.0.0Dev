package com.example.expense.test.web;

import java.util.Date;

import org.apache.struts2.convention.annotation.Action;
import org.apache.struts2.convention.annotation.Namespace;
import org.apache.struts2.convention.annotation.ParentPackage;

import com.norteksoft.product.util.SystemUrls;
import com.norteksoft.product.web.struts2.CrudActionSupport;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.config.ClientConfig;
import com.sun.jersey.api.client.config.DefaultClientConfig;

@Namespace("/test")
@ParentPackage("default")
public class TestAction extends CrudActionSupport<Object>{
	private String companyCode;
	private String parentCode;
	private String departmentCode;
	private Boolean isMain;
	private String loginName;
	private String name;
	private String password;
	private Boolean branchFlag;
	private String branchCode;
	private String path;
	public String getPath() {
		return path;
	}
	public void setPath(String path) {
		this.path = path;
	}
	public String getCompanyCode() {
		return companyCode;
	}
	public void setCompanyCode(String companyCode) {
		this.companyCode = companyCode;
	}
	public String getParentCode() {
		return parentCode;
	}
	public void setParentCode(String parentCode) {
		this.parentCode = parentCode;
	}
	public String getDepartmentCode() {
		return departmentCode;
	}
	public void setDepartmentCode(String departmentCode) {
		this.departmentCode = departmentCode;
	}
	public Boolean getIsMain() {
		return isMain;
	}
	public void setIsMain(Boolean isMain) {
		this.isMain = isMain;
	}
	public String getLoginName() {
		return loginName;
	}
	public void setLoginName(String loginName) {
		this.loginName = loginName;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public Boolean getBranchFlag() {
		return branchFlag;
	}
	public void setBranchFlag(Boolean branchFlag) {
		this.branchFlag = branchFlag;
	}
	public String getBranchCode() {
		return branchCode;
	}
	public void setBranchCode(String branchCode) {
		this.branchCode = branchCode;
	}
	@Action("index")
	public String index(){
		return "index";
	}
	@Action("toAddUserPage")
	public String toAddUserPage(){
		return "addUserPage";
	}
	@Action("toDelUserPage")
	public String toDelUserPage(){
		return "delUserPage";
	}
	@Action("toAddDeptPage")
	public String toAddDeptPage(){
		return "addDeptPage";
	}
	@Action("toDelDeptPage")
	public String toDelDeptPage(){
		return "delDeptPage";
	}
	@Action("delDept")
	public String delDept(){
		String msg=getMsg();
		ClientConfig config = new DefaultClientConfig();
		Client client = Client.create(config);
		WebResource service = client.resource(SystemUrls.getSystemPageUrl("imatrix")+"/rest/test/deleteDepartment");
		ClientResponse cr = service.entity(msg,"text/html;charset=UTF-8")
		.accept("text/html;charset=UTF-8")
		.post(ClientResponse.class);
		renderText(cr.getEntity(String.class));
		return null;
	}
	@Action("addDept")
	public String addDept(){
		String msg=getMsg();
		ClientConfig config = new DefaultClientConfig();
		Client client = Client.create(config);
		WebResource service = client.resource(SystemUrls.getSystemPageUrl("imatrix")+"/rest/test/saveDepartment");
		ClientResponse cr = service.entity(msg,"text/html;charset=UTF-8")
		.accept("text/html;charset=UTF-8")
		.post(ClientResponse.class);
		renderText(cr.getEntity(String.class));
		return null;
	}
	@Action("addUser")
	public String addUser(){
		String msg=getMsg();
		ClientConfig config = new DefaultClientConfig();
		Client client = Client.create(config);
		WebResource service = client.resource(SystemUrls.getSystemPageUrl("imatrix")+"/rest/test/saveUser");
		ClientResponse cr = service.entity(msg,"text/html;charset=UTF-8")
		.accept("text/html;charset=UTF-8")
		.post(ClientResponse.class);
		renderText(cr.getEntity(String.class));
		return null;
	}
	@Action("delUser")
	public String delUser(){
		String msg=getMsg();
		ClientConfig config = new DefaultClientConfig();
		Client client = Client.create(config);
		WebResource service = client.resource(SystemUrls.getSystemPageUrl("imatrix")+"/rest/test/deleteUser");
		ClientResponse cr = service.entity(msg,"text/html;charset=UTF-8")
		.accept("text/html;charset=UTF-8")
		.post(ClientResponse.class);
		renderText(cr.getEntity(String.class));
		return null;
	}
	private String getMsg(){
		StringBuilder sb=new StringBuilder();
		if(isNotEmp(path)){
			sb.append("path="+path+"&");
		}
		if(isNotEmp(departmentCode)){
			sb.append("departmentCode="+departmentCode+"&");
		}
		if(isNotEmp(loginName)){
			sb.append("loginName="+loginName+"&");
		}
		if(isNotEmp(name)){
			sb.append("name="+name+"&");
		}
		if(isNotEmp(password)){
			sb.append("password="+password+"&");
		}
		if(isNotEmp(branchFlag)){
			sb.append("branchFlag="+branchFlag+"&");
		}
		sb.append("_date="+new Date().getTime()+"&");
		if(sb.length()>0){
			return sb.substring(0, sb.length()-1);
		}
		return "";
	}
	private boolean isNotEmp(String str){
		if(str==null){
			return false;
		}else if(str.equals("")){
			return false;
		}
		return true;
	}
	private boolean isNotEmp(Boolean str){
		if(str==null){
			return false;
		}
		return true;
	}
	@Override
	public String delete() throws Exception {
		return null;
	}
	@Override
	public String input() throws Exception {
		return null;
	}
	@Override
	public String list() throws Exception {
		return null;
	}
	@Override
	protected void prepareModel() throws Exception {
		
	}
	@Override
	public String save() throws Exception {
		return null;
	}
	@Override
	public Object getModel() {
		return null;
	}
	
}
