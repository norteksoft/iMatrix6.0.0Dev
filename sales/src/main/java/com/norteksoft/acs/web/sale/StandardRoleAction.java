package com.norteksoft.acs.web.sale;


import java.io.File;
import java.net.URLEncoder;
import java.util.ArrayList;

import java.util.List;

import javax.servlet.http.HttpServletResponse;



import org.apache.commons.lang.StringUtils;
import org.apache.struts2.ServletActionContext;
import org.apache.struts2.convention.annotation.ParentPackage;
import org.apache.struts2.convention.annotation.Result;
import org.apache.struts2.convention.annotation.Results;
import org.springframework.beans.factory.annotation.Required;

import com.norteksoft.acs.base.web.struts2.CRUDActionSupport;
import com.norteksoft.acs.entity.authorization.BusinessSystem;
import com.norteksoft.acs.entity.authorization.Function;
import com.norteksoft.acs.entity.authorization.FunctionGroup;
import com.norteksoft.acs.entity.authorization.Role;
import com.norteksoft.acs.service.authorization.BusinessSystemManager;
import com.norteksoft.acs.service.authorization.FunctionManager;
import com.norteksoft.acs.service.authorization.StandardRoleManager;
import com.norteksoft.acs.web.authorization.JsTreeUtil;
import com.norteksoft.mms.base.data.DataHandle;
import com.norteksoft.product.orm.Page;

@ParentPackage("default")
@Results( { @Result(name = CRUDActionSupport.RELOAD, 
		location = "/sale/standard-role.action?businessSystemId=${businessSystemId}", type="redirect") })
public class StandardRoleAction extends CRUDActionSupport<Role> {
	private static final long serialVersionUID = 1L;
	private Page<Role> page = new Page<Role>(20 ,true);
	private Page<FunctionGroup> functionpage = new Page<FunctionGroup>(10, true);
	private Role entity;
	private Long businessSystemId;
	private StandardRoleManager roleManager;
	private BusinessSystemManager businessSystemManager;
	private FunctionManager functionManager;
	private Integer isAddOrRomove;
	private Long roleId;
	private Long id;
	private List<Long> functionIds;
	private List<Long> checkedFunctionIds;
	private String currentId;
    private String ids;
    
    private DataHandle dataHandle;
	
	private File file;
	private String fileName;
    
    public String roleToFunctionList()throws Exception{
    	entity = roleManager.getStandardRole(roleId);
    	functionpage = roleManager.listFunctions(functionpage, entity.getBusinessSystem().getId());
    	checkedFunctionIds = roleManager.getFunctionIds(roleId, entity.getBusinessSystem().getId());
    	isAddOrRomove=0;
    	return "function-list";
    	
    }
    public String roleRomoveFunctionList()throws Exception{
    	entity = roleManager.getStandardRole(roleId);
    	isAddOrRomove=1;
    	return "function-list";
    }
   public String  functionTree(){
//	   entity = roleManager.getStandardRole(roleId);
//	   checkedFunctionIds = roleManager.getFunctionIds(roleId, entity.getBusinessSystem().getId());
	   return renderText(getRoleFunctionTree(roleId));
   } 
    
    
    
    public String roleAddFunction()throws Exception{
    	Role role = roleManager.getStandardRole(roleId);
    	this.setBusinessSystemId(role.getBusinessSystem().getId());
    	roleManager.roleAddFunction(roleId, getFunctionsIds(ids),isAddOrRomove);
    	return RELOAD;
    }
    

    

	@Override
	public String delete() throws Exception {
		entity = roleManager.getStandardRole(id);
		businessSystemId=entity.getBusinessSystem().getId();
		roleManager.deleteStandardRole(id);
		return list();
	}

	@Override
	public String list() throws Exception {
		page = roleManager.getAllStandardRole(page, businessSystemId);
		return SUCCESS;
	}
	
	/**
	 * 导出资源组及资源
	 * @return
	 * @throws Exception
	 */
	public String exportRole() throws Exception{
		BusinessSystem system=businessSystemManager.getBusiness(businessSystemId);
		HttpServletResponse response = ServletActionContext.getResponse();
		response.reset();
		response.setContentType("application/x-download");
		String name="acs-role";
		if(system!=null){
			name=name+"-"+system.getCode();
		}
		response.addHeader("Content-Disposition", "attachment;filename="+URLEncoder.encode(name+".xls","UTF-8"));
		dataHandle.exportRole(response.getOutputStream(),businessSystemId, ids,null);
		return null;
	}
	public String showImportRole() throws Exception{
		return "show-import";
	}
	/**
	 * 导入资源组及资源
	 * @return
	 * @throws Exception
	 */
	public String importRole() throws Exception{
		if(fileName==null || !fileName.endsWith(".xls")){
			this.addActionMessage("请选择excel文件格式");
			return "show-import";
		}
		boolean success = true;
		try {
			dataHandle.importRole(file,businessSystemId,null);
		} catch (Exception e) {
			success = false;
		}
		if(success){
			this.addActionMessage("导入成功");
		}else{
			this.addActionMessage("导入失败，请检查excel文件格式");
		}
		return "show-import";
	}

	@Override
	protected void prepareModel() throws Exception {
		if(id != null){
			entity = roleManager.getStandardRole(id);
		}else{
			entity = new Role();
			if(businessSystemId != null){
				BusinessSystem businessSystem = businessSystemManager.getBusiness(businessSystemId);
				entity.setBusinessSystem(businessSystem);
			}
		}
	}

	@Override
	public String input() throws Exception {
		return INPUT;
	}

	@Override
	public String save() throws Exception {
		//去掉字段两边的空格
		String codek=entity.getCode().trim();
		String namek=entity.getName().trim();
		entity.setCode(codek);
		entity.setName(namek);
		
		businessSystemId = entity.getBusinessSystem().getId();
		boolean validateResult = functionManager.validateRoleCode(entity);
		if(validateResult){
			roleManager.saveStandardRole(entity);
			renderText(entity.getId()+"_true");
			return null;
		}else{
			renderText(entity.getId()+"_false");
			return null;
		}
	}

	public Integer getIsAddOrRomove() {
		return isAddOrRomove;
	}

	public void setIsAddOrRomove(Integer isAddOrRomove) {
		this.isAddOrRomove = isAddOrRomove;
	}

	public Role getModel() {
		return entity;
	}

	public Long getBusinessSystemId() {
		return businessSystemId;
	}

	public void setBusinessSystemId(Long businessSystemId) {
		this.businessSystemId = businessSystemId;
	}

	public Long getRoleId() {
		return roleId;
	}

	public void setRoleId(Long roleId) {
		this.roleId = roleId;
	}

	public Page<Role> getPage() {
		return page;
	}

	public void setPage(Page<Role> page) {
		this.page = page;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Page<FunctionGroup> getFunctionpage() {
		return functionpage;
	}

	public void setFunctionpage(Page<FunctionGroup> functionpage) {
		this.functionpage = functionpage;
	}

	public List<Long> getFunctionIds() {
		return functionIds;
	}

	public void setFunctionIds(List<Long> functionIds) {
		this.functionIds = functionIds;
	}

	public List<Long> getCheckedFunctionIds() {
		return checkedFunctionIds;
	}

	public void setCheckedFunctionIds(List<Long> checkedFunctionIds) {
		this.checkedFunctionIds = checkedFunctionIds;
	}
	
	

	public String getCurrentId() {
		return currentId;
	}

	public void setCurrentId(String currentId) {
		this.currentId = currentId;
	}

	
	
	
	
	public String getIds() {
		return ids;
	}

	public void setIds(String ids) {
		this.ids = ids;
	}

	@Required
	public void setStandardRoleManager(StandardRoleManager roleManager) {
		this.roleManager = roleManager;
	}

	@Required
	public void setBusinessSystemManager(BusinessSystemManager businessSystemManager) {
		this.businessSystemManager = businessSystemManager;
	}
	
	@Required
	public void setFunctionManager(FunctionManager functionManager) {
		this.functionManager = functionManager;
	}

	/**
	 * 角色分配资源树
	 */
	public  String getRoleFunctionTree(Long roleId){
		entity = roleManager.getStandardRole(roleId);
		Long systemId=entity.getBusinessSystem().getId();
		if(isAddOrRomove.equals(1)){
			return functionManager.createFunctionTreeByRoleRemove(entity,systemId);
		}else{
			return functionManager.createFunctionTreeByRoleAdd(entity,systemId);
		}
		
	}
	
	public String defaultTree(List<FunctionGroup> FunctionGroups,Long roleId ){
		StringBuilder tree = new StringBuilder();
		tree.append("[");
		tree.append("{attributes:{id:\"functionGroup_functionGroup=资源组"+"\"},state:\"open\",data:\"资源组\",children:[");
		tree.append(delComma(functionGroupsTree(FunctionGroups,roleId)));
		tree.append("]}");
		tree.append("]");
		return tree.toString();
		
	} 	
	public String functionGroupsTree(List<FunctionGroup> FunctionGroups,Long roleId){
		StringBuilder tree = new StringBuilder();
		for (FunctionGroup FunctionGroup : FunctionGroups) {
			//List<User> users = AuthHelpAPI.getUserListByWorkGroupId(workGroup.getId());判断这个资源组是否有资源
			//if (departments != null && departments.size() > 0&&users != null && users.size() > 0) {
				tree.append(JsTreeUtil.generateJsTreeNode("functionGroup_"+FunctionGroup.getId() +"="+FunctionGroup.getName(), "closed", FunctionGroup.getName())).append(",");
			//}else{
			//	tree.append(JsTreeUtil.generateJsTreeNode("workGroup_"+ workGroup.getId()+"="+workGroup.getWorkGroupName(), "", workGroup.getWorkGroupName())).append(",");
			//}
		}
		return tree.toString();
	}
	
	public  String functionGroupTree(Long functionGroupId,List<Long>checkedFunctionIds){
		StringBuilder tree = new StringBuilder();
		
		List<Function>functions=functionManager.getFunctionByFunctionGruopId(functionGroupId);
		boolean l=false;
		for (Function function : functions) {
			
			if(checkedFunctionIds.contains(function.getId())){
				l=true;
				break;
			}
			
		}
		if(l){
		tree.append("[");
		tree.append("{attributes:{id:\"functionsChecked_functionsChecked=已选资源"+"\"},state:\"closed\",data:\"已选资源\",children:[");
		tree.append(delComma(functionsCheckedTree(functions,checkedFunctionIds)));
		tree.append("]}");
		}else{
			
			tree.append("[{}");
			
		}
		
		boolean f=false;
		for (Function function : functions) {
			
			if(!checkedFunctionIds.contains(function.getId())){
				f=true;
				break;
			}
			
		}
		if(f){
		tree.append(",{attributes:{id:\"functionsUnchecked_functionsUnchecked=未选资源"+"\"},state:\"open\",data:\"未选资源\",children:[");
		tree.append(delComma(functionsUncheckedTree(functions,checkedFunctionIds)));
		tree.append("]}");
		}else{
			tree.append(",{}");
			
		}
		tree.append("]");
		return tree.toString();
	}
	

	
	public  String functionsCheckedTree(List<Function> functions,List<Long>checkedFunctionIds){
		StringBuilder tree = new StringBuilder();
		for (Function function : functions) {
			if(checkedFunctionIds.contains(function.getId())){
			tree.append(JsTreeUtil.generateJsTreeNode("functionChecked_" +function.getId()+ "="+ function.getName(), "", function.getName())).append(",");
			}
		}
		return tree.toString();
	}
	
	public  String functionsUncheckedTree(List<Function> functions,List<Long>checkedFunctionIds){
		StringBuilder tree = new StringBuilder();
		for (Function function : functions) {
		
			if(!checkedFunctionIds.contains(function.getId())){
			tree.append(JsTreeUtil.generateJsTreeNode("functionUnchecked_" +function.getId()+ "="+ function.getName(), "", function.getName())).append(",");
			}
			
		}
		return tree.toString();
	}
	
	
	/**
	 * 去逗号
	 * @param str
	 * @return
	 */
	public  String delComma(String str){
		if(StringUtils.isNotEmpty(str)){
			int length=str.length();
			if(str.charAt(length-1)==',')str= str.substring(0,length - 1);
		}
		return str;
	}
	
	public List<Long> getFunctionsIds(String ids){
		List<Long> fIds=new ArrayList<Long>();
		String[] Ids=ids.split(",");
		for(int i=0;i<Ids.length;i++){
			fIds.add(Long.parseLong(Ids[i]));
		}
		return fIds;
		
	}

	@Required
	public void setDataHandle(DataHandle dataHandle) {
		this.dataHandle = dataHandle;
	}

	public void setFile(File file) {
		this.file = file;
	}

	public void setFileFileName(String fileName) {
		this.fileName = fileName;
	}
	
	
	
}
