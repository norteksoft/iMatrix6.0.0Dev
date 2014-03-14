package com.norteksoft.acs.web.sale;

import java.util.List;

import org.apache.struts2.convention.annotation.Action;
import org.apache.struts2.convention.annotation.ParentPackage;
import org.apache.struts2.convention.annotation.Result;
import org.apache.struts2.convention.annotation.Results;
import org.springframework.beans.factory.annotation.Required;

import com.norteksoft.acs.base.web.struts2.CRUDActionSupport;
import com.norteksoft.acs.entity.authorization.BusinessSystem;
import com.norteksoft.acs.entity.authorization.Function;
import com.norteksoft.acs.entity.authorization.Role;
import com.norteksoft.acs.service.authorization.BusinessSystemManager;
import com.norteksoft.acs.service.authorization.FunctionManager;
import com.norteksoft.product.orm.Page;

/**
 * author 李洪�? version 创建时间�?2009-3-11 上午09:51:10 资源管理Action
 */
@ParentPackage("default")
@Results( { @Result(name = CRUDActionSupport.RELOAD, location = "/sale/function!getFunctionsBySystem.action?systemId=${systemId}", type = "redirect") })
public class FunctionAction extends CRUDActionSupport<Function> {

	private static final long serialVersionUID = 4814560124772644966L;
	private FunctionManager functionManager;
	private Page<Function> page = new Page<Function>(30, true);
	private Page<Role> RolePage = new Page<Role>(5, true);
	private Page<Function> pageFunction;
	private Function function;
	private Long id;
	private List<Function> allFunction;
	private String functionName;
	private String functionId;
	private List<Long> checkedRoleIds;
	private List<Long> roleIds;
	private Long function_Id;//资源添加角色时传过来的id
	private Long systemId;
	private BusinessSystemManager businessSystemManager;
	private Long paternId;
	private String paternName;
	private String checkNodetype;
	private Integer addOrRemove;
	private String ids;
	//targetId, moveType msg
	//拖拽时使用的参数
	private String moveType;
	private String targetId;
	private String msg;
	private Boolean canEditRadio;
	private Boolean ism;
	private String menuId;
	private Integer addOrEdit=0;
	private String code;
	
	public String getCode() {
		return code;
	}


	public void setCode(String code) {
		this.code = code;
	}


	public Long getId() {
		return id;
	}


	public Integer getAddOrEdit() {
		return addOrEdit;
	}


	public void setAddOrEdit(Integer addOrEdit) {
		this.addOrEdit = addOrEdit;
	}


	public String getMenuId() {
		return menuId;
	}


	public void setMenuId(String menuId) {
		this.menuId = menuId;
	}


	public void setPaternName(String paternName) {
		this.paternName = paternName;
	}


	public String getPaternName() {
		return paternName;
	}


	public Boolean getCanEditRadio() {
		return canEditRadio;
	}

	public void setCanEditRadio(Boolean canEditRadio) {
		this.canEditRadio = canEditRadio;
	}
	
	public Boolean getIsm() {
		return ism;
	}

	public void setIsm(Boolean ism) {
		this.ism = ism;
	}

	public String getCheckNodetype() {
		return checkNodetype;
	}
	
	public void setCheckNodetype(String checkNodetype) {
		this.checkNodetype = checkNodetype;
	}

	public String getMsg() {
		return msg;
	}

	public void setMsg(String msg) {
		this.msg = msg;
	}

	public String getMoveType() {
		return moveType;
	}

	public void setMoveType(String moveType) {
		this.moveType = moveType;
	}

	public String getTargetId() {
		return targetId;
	}

	public void setTargetId(String targetId) {
		this.targetId = targetId;
	}

	public String getFunctionName() {
		return functionName;
	}

	public void setFunctionName(String functionName) {
		this.functionName = functionName;
	}

	public String getFunctionId() {
		return functionId;
	}

	public void setFunctionId(String functionId) {
		this.functionId = functionId;
	}

	/**
	 * 删除
	 * @return
	 * @throws Exception
	 */
	@Override
	public String delete() throws Exception {
		functionManager.deleteFunction(ids);
		addActionMessage("删除资源成功");
		return null;
	}

	@Override
	public String list() throws Exception {
		page = functionManager.getAllFunction(page, systemId);
		//allFunction = functionManager.getAllFunction();
		return SUCCESS;
	}

	/**
	 * 按条件查�?
	 * 
	 * @return
	 */
	public void prepareSearch() throws Exception {
		prepareModel();
	}
	
	public String search() throws Exception {
		page = functionManager.getSearchFunction(page, systemId, function, false);
		if(paternId != null){
			pageFunction = page;
			return "search";
		}
		return SUCCESS;
	}

	@Override
	public String input() throws Exception {
		if(function.getId()!=null){
			addOrEdit=1;
		};
		if(systemId!=null||function.getBusinessSystem()!=null){
			BusinessSystem bs=systemId==null?function.getBusinessSystem():businessSystemManager.getBusiness(systemId);
			if(bs!=null&&bs.getCode().equals("portal")){
				this.canEditRadio=false;
				this.ism=false;
				return INPUT;
			}
		}
		if(checkNodetype!=null&&paternId!=null){
			if(checkNodetype.equals("system")){
				this.canEditRadio=false;
				this.ism=true;
			}else if(checkNodetype.equals("void")){
				this.canEditRadio=false;
				this.ism=false;
			}else if(checkNodetype.equals("menu")){
				this.canEditRadio=true;
				this.ism=true;
			}else if(checkNodetype.equals("function")){
				this.canEditRadio=false;
				this.ism=false;
			}else{
				this.canEditRadio=true;
				this.ism=true;
			}
			if(paternId==0){
				paternName="无";
			}else{
				paternName=functionManager.getFunction(paternId).getName();
			}
		}else{
			if(function.getPid()!=null){
				paternId=function.getPid();
				Function p=functionManager.getFunction(paternId);
				paternName=p.getName();
				if(function.getIsmenu()){
					List<Function> fs=functionManager.getFunctionsByPid(function.getId());
					this.canEditRadio=true;
					if(fs.size()>0){
						for(Function f:fs){
							if(f.getIsmenu()){
								this.canEditRadio=false;
								this.ism=true;
								break;
							}
						}
					}
					this.ism=true;
				}else{
					Function f=functionManager.getFunction(function.getPid());
					this.canEditRadio=false;
					if(f.getIsmenu()){
						this.canEditRadio=true;
					}
					
					this.ism=false;
				}
			}else{
				paternId=0L;
				paternName="无";
				if(function.getIsmenu()){
					if(functionManager.getFunctionsByPid(function.getId()).size()>0){
						this.canEditRadio=false;
					}else{
						this.canEditRadio=true;
					}
				}else{
					this.canEditRadio=true;
				}
			}
		}
		return INPUT;
	}

	@Override
	protected void prepareModel() throws Exception {
		if (id != null) {
			function = functionManager.getFunction(id);
		} else {
			function = new Function();
			if(systemId != null){
				BusinessSystem bs = businessSystemManager.getBusiness(systemId);
				function.setBusinessSystem(bs);
			}
		}
	}

	@Override
	public String save() throws Exception {
		Boolean ischenge=ischenge(function,ism);
		String codek=function.getCode().trim();
		String namek=function.getName().trim();
		String pathk=function.getPath().trim();
		function.setCode(codek);
		function.setName(namek);
		function.setPath(pathk);
		function.setIsmenu(ism);
		if(function.getId()!=null){
			Long parentId=function.getPid();
			if(parentId==null){
				if(ism){
					function.setMenulevel(1);
					if(ischenge){
						function.setOrdinal(functionManager.getLastOrdinal(systemId)+1);
					}
				}
			}
			functionManager.saveFunction(function,ischenge);
		}else{
			function.setPid(paternId==0?null:paternId);
			functionManager.saveFunction(function,ischenge);
		}
		addActionMessage("保存用户成功");
		this.setSystemId(function.getBusinessSystem().getId());
		return null;
	}
	private Boolean ischenge(Function function2, Boolean ism2) {
		return function.getIsmenu()==null?true:(!function.getIsmenu().equals(ism2));
	}


	public String moveNode(){
		functionManager.execute(msg,targetId,moveType,systemId);
		return null;
	}
	//更新系统菜单
	public String asyncMenu(){
		functionManager.asyncMenu(systemId);
		renderText("ok");
		return null;
	}
	public String validata(){
		renderText(functionManager.validata(id,code));
		return null;
	}
	/**
	 * 资源添加角色
	 */
	public void prepareFunctionToRoleList() throws Exception {
		function = functionManager.getFunction(function_Id);
	}
	public String functionToRoleList() throws Exception {
//		HttpServletRequest request = ServletActionContext.getRequest();
//		Role role = new Role();
//		role.setRoleName(request.getParameter("roleName"));
//		//RolePage = functionManager.functionToRoleList(RolePage,role);
//		checkedRoleIds = functionManager.getRoleIds(function_Id);
		return "role-list";
	}
	public void getTree(){
		renderText(functionManager.createFunctionTree(systemId));
	}
	public void getMenuTree(){
		renderText(functionManager.createMenuTree(systemId));
	}
	@Action("menuTree")
	public String menuTree(){
		return "menuTree";
	}
	public void addFunctionsToMenu(){
		renderText(functionManager.addFunctionsToMenu(msg,menuId,systemId));
	}
	public String functionAddRole() throws Exception {
		//functionManager.functionAddRole(function_Id, roleIds);
		return RELOAD;
	}
	
	public Function getModel() {

		return function;
	}

	public Page<Function> getPage() {
		return page;
	}

	public void setPage(Page<Function> page) {
		this.page = page;
	}

	@Required
	public void setFunctionManager(FunctionManager functionManager) {
		this.functionManager = functionManager;
	}

	public List<Function> getAllFunction() {
		return allFunction;
	}

	public void setId(Long id) {
		this.id = id;
	}
	
	public Page<Role> getRolePage() {
		return RolePage;
	}

	public void setRolePage(Page<Role> rolePage) {
		RolePage = rolePage;
	}

	public List<Long> getCheckedRoleIds() {
		return checkedRoleIds;
	}

	public void setCheckedRoleIds(List<Long> checkedRoleIds) {
		this.checkedRoleIds = checkedRoleIds;
	}

	public List<Long> getRoleIds() {
		return roleIds;
	}

	public void setRoleIds(List<Long> roleIds) {
		this.roleIds = roleIds;
	}
	
	public Long getFunction_Id() {
		return function_Id;
	}

	public void setFunction_Id(Long function_Id) {
		this.function_Id = function_Id;
	}
	
	private Long functionGroupId;

	public Long getFunctionGroupId() {
		return functionGroupId;
	}

	public void setFunctionGroupId(Long functionGroupId) {
		this.functionGroupId = functionGroupId;
	}
	
	public String getFuncsByFunctionGroup(){
		if(functionGroupId != null){
			page = functionManager.getFunctionsByFunctionGroup(page, functionGroupId);
		}
		return SUCCESS;
	}
	
	public String getFunctionsBySystem(){
		if(systemId != null){
			page = functionManager.getFunctionsBySystem(page, systemId);
		}
		return SUCCESS;
	}

	public Long getSystemId() {
		return systemId;
	}

	public void setSystemId(Long systemId) {
		this.systemId = systemId;
	}

	@Required
	public void setBusinessSystemManager(BusinessSystemManager businessSystemManager) {
		this.businessSystemManager = businessSystemManager;
	}

	public Long getPaternId() {
		return paternId;
	}

	public void setPaternId(Long paternId) {
		this.paternId = paternId;
	}

	public Integer getAddOrRemove() {
		return addOrRemove;
	}

	public void setAddOrRemove(Integer addOrRemove) {
		this.addOrRemove = addOrRemove;
	}

	public Page<Function> getPageFunction() {
		return pageFunction;
	}

	public void setPageFunction(Page<Function> pageFunction) {
		this.pageFunction = pageFunction;
	}

	public Function getFunction() {
		return function;
	}

	public void setFunction(Function function) {
		this.function = function;
	}

	public String getIds() {
		return ids;
	}

	public void setIds(String ids) {
		this.ids = ids;
	}
	
}
