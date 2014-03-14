package com.norteksoft.acs.web.organization;

import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.struts2.convention.annotation.Action;
import org.apache.struts2.convention.annotation.Namespace;
import org.apache.struts2.convention.annotation.ParentPackage;
import org.apache.struts2.convention.annotation.Result;
import org.apache.struts2.convention.annotation.Results;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Required;

import com.norteksoft.acs.base.utils.Ldaper;
import com.norteksoft.acs.base.web.struts2.CRUDActionSupport;
import com.norteksoft.acs.entity.authorization.BranchAuthority;
import com.norteksoft.acs.entity.authorization.BusinessSystem;
import com.norteksoft.acs.entity.authorization.Role;
import com.norteksoft.acs.entity.organization.Company;
import com.norteksoft.acs.entity.organization.Department;
import com.norteksoft.acs.entity.organization.DepartmentUser;
import com.norteksoft.acs.entity.organization.User;
import com.norteksoft.acs.entity.organization.UserInfo;
import com.norteksoft.acs.entity.organization.Workgroup;
import com.norteksoft.acs.service.authorization.BranchAuthorityManager;
import com.norteksoft.acs.service.authorization.BusinessSystemManager;
import com.norteksoft.acs.service.authorization.RoleManager;
import com.norteksoft.acs.service.organization.CompanyManager;
import com.norteksoft.acs.service.organization.DepartmentManager;
import com.norteksoft.acs.service.organization.UserInfoManager;
import com.norteksoft.acs.service.organization.UserManager;
import com.norteksoft.acs.service.organization.WorkGroupManager;
import com.norteksoft.product.api.ApiFactory;
import com.norteksoft.product.orm.Page;
import com.norteksoft.product.util.ContextUtils;
import com.norteksoft.product.util.JsTreeUtils;
import com.norteksoft.product.util.PageUtils;
import com.norteksoft.product.util.TreeUtils;
import com.norteksoft.tags.tree.DepartmentDisplayType;

/**
 * author 李洪超
 * version 创建时间：2009-3-11 上午09:51:10
 *  部门管理Action
 *  
 *  2010-07-27 xiaoj
 */
@SuppressWarnings("deprecation")
@Namespace("/organization")
@ParentPackage("default")
@Results( { @Result(name = CRUDActionSupport.RELOAD, location = "department!list?departmentId=${departmentId}&treeSelectedNode=${treeSelectedNode}", type="redirectAction") })
public class DepartmentAction extends CRUDActionSupport<Department>{
	
	private static final long serialVersionUID = 4814560124772644966L;
	private DepartmentManager departmentManager;
	private CompanyManager companyManager;
	private BranchAuthorityManager branchAuthorityManager;
	private RoleManager roleManager;
	private Page<Department> page = new Page<Department>(20, true);
	private Page<User> userPage = new Page<User>(0, true);
	private Page<UserInfo> pageUserInfo = new Page<UserInfo>(20, true);
	private Department department;
	private Long id;
	private Long parentId;
	private Long companyId;
	private List<Long> checkedUserIds;
	private String departmentName;
	private String departmentShortName;
	private String departmentCode;
	private Long departmentId;
	private List<Role> allRoles;
	private List<Long> roleIds;
	private List<Long> checkedRoleIds;
	private Integer isAddOrRomove;
	private List<Long> departmentIds;
	private User user;
	private List<BusinessSystem> systems;
	private BusinessSystemManager businessSystemManager;
	private UserManager userManager;
	private UserInfoManager userInfoManager;
	private String message = "";
	private String currentId;
	private List<Long> userIds;
	private String ids;
	private String treeSelectedNode;
	private Long currentDeptId;
	private Long deptId;
	private Boolean branchFlag;//是否是分支机构
	private List<Long> branchId;//分支机构id
	private String branchIds;
	private String choseUserIds;
	private String chooseDepartmentId;
	private Long parentDepartmentId;
	private Boolean canEditParentDepartment=true;
	private Boolean canEditDepartmentInfor=true;
	private Boolean canTransform=true;
	private Long firstBranchId;
	private String addUserIds;
	
	@Autowired
	private WorkGroupManager workGroupManager;
	/**
	 * 分页查询所有不在任何部门的用户
	 */
	@Override
	@Action("department")
	public String list() throws Exception {
		boolean systemAdminable =  roleManager.hasSystemAdminRole(ContextUtils.getUserId());
		if(!systemAdminable){//如果不是系统管理员
			branchIds=branchAuthorityManager.getBranchIdsByUser(ContextUtils.getUserId());
		}
		if(userPage.getPageSize() <= 1){
			//如果是系统管理员
			if(!roleManager.hasAdminRole(ContextUtils.getUserId())){
				List<BranchAuthority> branchAuthoritys = branchAuthorityManager.getBranchByUser(ContextUtils.getUserId());
				if(!branchAuthoritys.isEmpty()&&departmentId==null){
					departmentId=branchAuthoritys.get(0).getBranchesId();
				}
			}
			return SUCCESS;
		}else{
			if(departmentId != null){
				Department department = departmentManager.getDepartmentById(departmentId);
				if(department.getBranch()){//分支机构
					userInfoManager.queryUsersByBranch(userPage, departmentId);
				}else if((!department.getBranch())
						&&(!department.getId().equals(department.getSubCompanyId()))
						&&department.getSubCompanyId()!=null){//分支机构下部门
					userInfoManager.queryUsersByBranchDepartment(userPage, departmentId);
				}else{
				    userInfoManager.queryUsersByDepartment(userPage, departmentId);
				}
			}else{
				//如果是系统管理员
				if(roleManager.hasAdminRole(ContextUtils.getUserId())){
					 userInfoManager.getSearchUser(userPage);
				}else{
					List<BranchAuthority> branchAuthoritys = branchAuthorityManager.getBranchByUser(ContextUtils.getUserId());
					if(!branchAuthoritys.isEmpty()){
					 userInfoManager.queryUsersByBranch(userPage,branchAuthoritys.get(0).getBranchesId());
					}
				}
			}
			renderHtml(PageUtils.pageToJson(userPage));
			ApiFactory.getBussinessLogService().log("部门管理", 
					"查看部门列表",ContextUtils.getSystemId("acs"));
			return null;
		}
	}
	
	/**
	 * 修改部门
	 */
	@Override
	public String input() throws Exception {
		if(id == null){
			//自动生成部门编号
			department.setCode(departmentManager.createDepartmentCode());
			ApiFactory.getBussinessLogService().log("部门管理", 
					"新建部门",ContextUtils.getSystemId("acs"));
		}else{
			//修改分支机构时，如果当前分支机构下有人员，工作组，部门，分支机构或授权就不能修改父部门
			canEditParentDepartment = departmentManager.canEditParentDepartment(id);
			//判断当前分支机构是否时当前分支机构管理管理的分支机构,如果是就只能查看，不能修改
			canEditDepartmentInfor = departmentManager.canEditDepartmentInfor(id);
			//当分支机构下有部门或人员或工作组或分支机构或分支机构授权和有角色时不能转成部门，反之可以；
			//当部门下有人或子部门时不能转成分支机构，反之可以；
			canTransform = departmentManager.canTransform(id);
			ApiFactory.getBussinessLogService().log("部门管理", 
					"修改部门",ContextUtils.getSystemId("acs"));
		}
		return INPUT;
	}
	public String getDepartmentCodeForBranch() throws Exception{
		this.renderText(departmentManager.createDepartmentCodeForBranch());
		return null;
	}
	
	/**
	 * 保存部门信息
	 */
	public String saveDepartment() throws Exception{
		boolean logSign=true;//该字段只是为了标识日志信息：true表示新建部门、false表示修改部门
		//保存是否是分支机构字段
		if(branchFlag){
			department.setBranch(true);
		}else{
			department.setBranch(false);
		}
		
		if(id==null){
			Company company = companyManager.getCompany(ContextUtils.getCompanyId());
			department.setCompany(company);
			departmentManager.saveDept(department);
			
			//保存分支机构id,如果当前部门就是分支机构且是顶层则它的分支机构id是它本身的id，
			if(department.getBranch()){
				//如果当前分支机构有父部门或父分支机构则存它的分支机构id,查询子分支机构时用到 
				department.setSubCompanyId(department.getId());
			}else{
			    department.setSubCompanyId(departmentManager.getBranchId(department));
			}
			logSign=true;
		}else{
			//当修改为部门时
			if(!department.getBranch()){
				if(department.getParent()!=null){
					department.setSubCompanyId(department.getParent().getSubCompanyId());
				}else{
					department.setSubCompanyId(null);
				}
			}else{
		    //当修改为分支机构时
				department.setSubCompanyId(department.getId());
			}
			departmentManager.saveDept(department);
			if(Ldaper.isStartedAboutLdap()){
				message = Ldaper.addGroup(department,false);
			}
			logSign=false;
		}
		
		//修改分支机构下，人员的"所属分支机构"字段
		if(department.getBranch()==true){
			//分支机构下的人员
			List<User> users=userManager.getUsersBySubCompany(department.getId());
			for(User u : users){
				u.setSubCompanyName(StringUtils.isNotEmpty(department.getShortTitle())?department.getShortTitle():department.getName());
				userManager.saveUser(u);
			}
		}
		
		addActionMessage(getText("common.saved"));
		
		if(logSign){
			ApiFactory.getBussinessLogService().log("部门管理", 
					"新建部门："+department.getName(),ContextUtils.getSystemId("acs"));
		}else{
			ApiFactory.getBussinessLogService().log("部门管理", 
					"修改部门："+department.getName(),ContextUtils.getSystemId("acs"));
		}
		if(roleManager.hasAdminRole(ContextUtils.getUserId())){
			return null;
		}else{
		    return list();
		}
	}
	
	/**
	 * 删除部门
	 */
	@Override
	public String delete() throws Exception {
		String logSign="";//该字段只是为了标识日志信息：部门名称
		Department dept = departmentManager.getDepartment(departmentId);
		logSign=dept.getName();
		Department parentDept = dept.getParent();
		List<User> users=userManager.getUsersByDeptId(departmentId);
		departmentManager.deleteDepart(dept,users);
		departmentManager.cleanDept(departmentId);
		if(parentDept != null)
			departmentId = dept.getParent().getId();
		else
			departmentId=null;
		addActionMessage(getText("common.deleted"));
		ApiFactory.getBussinessLogService().log("部门管理", 
				"删除部门:"+logSign,ContextUtils.getSystemId("acs"));
	    return RELOAD;
	}
	
	/**
	 * 验证删除分支机构
	 */
	public String validateDepartmentDelete() throws Exception {
		//部门、人员、工作组、分支机构、分支机构授权管理、角色
		//分支机构下的部门和分支机构
		List<Department> subDepartments=departmentManager.getSubDeptments(deptId);
		//分支机构下的人员
		List<User> users=userManager.getUsersBySubCompany(deptId);
		//分支机构下的工作组
		List<Workgroup> workgroups=workGroupManager.getWorkgroupsByBranch(deptId);
		//分支机构授权管理
		List<BranchAuthority> branchAuthoritys=branchAuthorityManager.getBranchAuthorityByBranch(deptId);
		//角色
		List<Role> roles=roleManager.getRoleByBranches(deptId);
		if((subDepartments!=null && subDepartments.size()>0) || (users!=null&& users.size()>0) || (workgroups!=null && workgroups.size()>0) || (branchAuthoritys!=null&&branchAuthoritys.size()>0) || (roles!=null&&roles.size()>0)){
			this.renderText("no");
		}else{
			this.renderText("ok");
		}
		return null;
	}
	
	public void prepareSaveDepartment() throws Exception {
		prepareModel();
		if(parentId == null){
			department.setParent(null);
		}
	}
	
	@Override
	protected void prepareModel() throws Exception {
		if (id != null) {
			department = departmentManager.getDepartment(id);
		} else {
			department = new Department();
		}
		if(parentId != null){
			department.setParent(departmentManager.getDepartment(parentId));
		}
	}

    /**
     * 部门添加人员
     */
    public String addDepartmentToUsers()throws Exception{
    	boolean systemAdminable =  roleManager.hasSystemAdminRole(ContextUtils.getUserId());
		if(!systemAdminable){//如果不是系统管理员
			branchIds=branchAuthorityManager.getBranchIdsByUser(ContextUtils.getUserId());
		}
    	return "user-tree";
    }
    
    /**
     * 部门树
     * @return
     * @throws Exception
     */
    public String tree() throws Exception{
    	return "tree";
    }
	
    /**
     * 人员树
     * @return
     * @throws Exception
     */
	public String getCompanyNodes() throws Exception{
		this.renderText(TreeUtils.getCreateManDepartmentTree(ContextUtils.getCompanyId(),currentId,false,DepartmentDisplayType.NAME,false,""));
		return null;
		
	}
	
	public String getUserNodes(Long deptId) throws Exception{
		StringBuilder nodes = new StringBuilder();
		List<User> users = userManager.getUsersByDeptId(deptId);
		
		List<Department> subDepts = departmentManager.getSubDeptments(deptId);
		for(Department subDept : subDepts){
			nodes.append(JsTreeUtils.generateJsTreeNode("DEPARTMENT," + subDept.getId(), "closed", subDept.getName(), ""));
			nodes.append(",");
		}
		for(User user : users){
			nodes.append(JsTreeUtils.generateJsTreeNode("USER," + user.getId(), "", user.getName(), "")).append(",");
		}
		if(nodes.lastIndexOf(",") != -1 && nodes.lastIndexOf(",") == nodes.length()-1){
			nodes.replace(nodes.length()-1, nodes.length(), "");
		}
		return nodes.toString();
	}
	public String getNoDepartmentUserNodes(Long companyId){
		StringBuilder nodes = new StringBuilder();
		List<com.norteksoft.product.api.entity.User> users =ApiFactory.getAcsService().getUsersNotInDepartment(companyId);
		for(com.norteksoft.product.api.entity.User user : users){
			nodes.append(JsTreeUtils.generateJsTreeNode("USER," + user.getId(), "", user.getName(), "")).append(",");
		}
		if(nodes.lastIndexOf(",") != -1 && nodes.lastIndexOf(",") == nodes.length()-1){
			nodes.replace(nodes.length()-1, nodes.length(), "");
		}
		return nodes.toString();
	}
	
	/**
	 * 保存部门添加用户
	 * @return
	 * @throws Exception
	 */
    public String departmentAddUser()throws Exception{
    	departmentManager.departmentToUser(departmentId, addUserIds);
    	
    	String logSign="";//该字段只是为了标识日志信息：用户1，用户2，...
    	department = departmentManager.getDepartment(departmentId);
    	for(String userId:addUserIds.split(",")){
			if(userId.equals(0L)){//全公司时
				logSign+="公司所有人";
				break;
			}else{
				user=userManager.getUserById(Long.valueOf(userId));
				if(StringUtils.isNotEmpty(logSign)){
					logSign+=",";
				}
				logSign+=user.getName();
			}
		}
    	ApiFactory.getBussinessLogService().log("部门管理", 
    			department.getName()+"添加兼职人员:"+logSign,ContextUtils.getSystemId("acs"));
    	this.renderText("ok");
    	return null;
    }
    
  //判断所选择的部门下是否已经存在相同登录名的用户并得到这些用户
    public String getRepeatUserNames()throws Exception{
    	return renderText(departmentManager.getRepeatUserNames(choseUserIds,chooseDepartmentId));
	}
    
    /**
     * 保存部门移除用户
     * @return
     * @throws Exception
     */
    public String removeDepartmentToUsers() throws Exception{
    	departmentManager.departmentToUser(departmentId, userIds, 1,null);
    	
    	String logSign="";//该字段只是为了标识日志信息：用户1，用户2，...
    	department = departmentManager.getDepartment(departmentId);
		for(Long userId:userIds){
			user=userManager.getUserById(Long.valueOf(userId));
			if(StringUtils.isNotEmpty(logSign)){
				logSign+=",";
			}
			logSign+=user.getName();
		}
    	ApiFactory.getBussinessLogService().log("部门管理", 
    			department.getName()+"移除兼职人员:"+logSign,ContextUtils.getSystemId("acs"));
    	return RELOAD;
    }
    
    @Autowired
    public void setUserInfoManager(UserInfoManager userInfoManager) {
		this.userInfoManager = userInfoManager;
	}
    
	
	/**
	 * 验证部门名称唯一性
	 */
	public String checkDeptName() throws Exception{
		List<Department> department =null;
		if(id!=null){//修改
			if(branchFlag){//是分支机构
				department = departmentManager.checkBranchCode(departmentCode,id);
            	if(department.size()>0)return this.renderText("branchCodeFalse");
            	
            	department = departmentManager.checkBranchName(departmentName,id);
            	if(department.size()>0)return this.renderText("branchNameFalse");
            	if(departmentName.equals(ContextUtils.getCompanyName())){
            		return this.renderText("branchNameFalseOfCompany");
            	}
            	department = departmentManager.checkBranchShortName(departmentShortName,id);
            	if(department.size()>0)return this.renderText("branchShortNameFalse");
			}else{//部门
				department = departmentManager.checkDeparmentCode(departmentCode,id);
            	if(department.size()>0)return this.renderText("departmentCodeFalse");
            	if(parentDepartmentId==null){//没有父部门,属于集团
            		department = departmentManager.checkDepartmentNameCompany(departmentName,id,null);
         	        if(department.size()>0)return this.renderText("departmentNameFalse");
            	}else{
            	   Long branchId = departmentManager.getDepartment(parentDepartmentId).getSubCompanyId();
            	   if(branchId==null){//属于集团
            		   department = departmentManager.checkDepartmentNameCompany(departmentName,id,parentDepartmentId);
            	       if(department.size()>0)return this.renderText("departmentNameFalse");
            	   }else{//属于某一分支机构
            	       department = departmentManager.checkDepartmentName(departmentName,branchId,id,parentDepartmentId);
            	       if(department.size()>0)return this.renderText("departmentNameFalse");
            	   }
            	}
			}
		}else{//新建
            if(branchFlag){//是分支机构
            	department = departmentManager.checkBranchCode(departmentCode);
            	if(department.size()>0)return this.renderText("branchCodeFalse");
            	
            	department = departmentManager.checkBranchName(departmentName);
            	if(department.size()>0)return this.renderText("branchNameFalse");
            	if(departmentName.equals(ContextUtils.getCompanyName())){
            		return this.renderText("branchNameFalseOfCompany");
            	}
            	department = departmentManager.checkBranchShortName(departmentShortName);
            	if(department.size()>0)return this.renderText("branchShortNameFalse");
			}else{//部门
				department = departmentManager.checkDeparmentCode(departmentCode);
            	if(department.size()>0)return this.renderText("departmentCodeFalse");
            	
            	if(parentDepartmentId==null){//没有父部门,属于集团
            		department = departmentManager.checkDepartmentName(departmentName);
         	        if(department.size()>0)return this.renderText("departmentNameFalse");
            	}else{
            	   Long branchId = departmentManager.getDepartment(parentDepartmentId).getSubCompanyId();
            	   if(branchId==null){//属于集团
            		   department = departmentManager.checkDepartmentName(parentDepartmentId,departmentName);
            	       if(department.size()>0)return this.renderText("departmentNameFalse");
            	   }else{//属于某一分支机构
            	       department = departmentManager.checkDepartmentName(departmentName,branchId,parentDepartmentId);
            	       if(department.size()>0)return this.renderText("departmentNameFalse");
            	   }
            	}
			}
		}
		return null;
	}
	
	/**
	 * 验证部门编号唯一性
	 * liudongxia
	 */
	public String checkDeptCode() throws Exception{
		return null;
	}
	
	/**
	 * 按条件查询
	 * @return
	 */
	public void prepareSearch() throws Exception {
		prepareModel();
	}
	
	public String search() throws Exception {
		page = departmentManager.getSearchDepartment(page, department, false);
		return SUCCESS;

	}
    
    
    
    
    
	public void prepareSaveUser() throws Exception {
		prepareModel();
	}
	

	public String saveUser() throws Exception{
		Department department = departmentManager.getDepartment(parentId);
		this.department.setParent(department);
		departmentManager.saveDept(this.department);
		addActionMessage(getText("common.saved"));
		 return RELOAD;
	}
	    
	/**
	 * 新建部门
	 */
	public String inputDepartment() throws Exception{
		companyId = companyManager.getCompanyId();
		return "input";
	}
	
	public void prepareListRoles() throws Exception {
		department = departmentManager.getDepartment(departmentId);
	}
	
	/**
	 * 部门添加角色 
	 */
	public String listRoles()throws Exception{
		isAddOrRomove = 0;
		systems = businessSystemManager.getAllBusiness();
		checkedRoleIds = departmentManager.getCheckedRoleIdsByDepartment(departmentId);
		return "role";
	}
	
	/**
	 * 部门移除角色
	 */
	public String removeRoles() throws Exception{
		isAddOrRomove = 1;
		systems = businessSystemManager.getAllBusiness();
		checkedRoleIds = departmentManager.getCheckedRoleIdsByDepartment(departmentId);
		return "role";
	}
	
	/**
	 * 给部门分配角色
	 */
	public String addRolesToDepartment(){
		departmentManager.addRolesToDepartments(departmentId, roleIds, isAddOrRomove);
		if(isAddOrRomove == 0){
			addActionMessage(getText("department.addRolesSuccess"));
		}else if(isAddOrRomove == 1){
			addActionMessage(getText("department.removeRolesSuccess"));
		}
		ApiFactory.getBussinessLogService().log("部门管理", 
				"部门添加或移除角色",ContextUtils.getSystemId("acs"));
		return RELOAD;
	}
	
	/**
	 * 保存
	 */
	@Override
	public String save() throws Exception {
		departmentManager.saveDept(department);
		addActionMessage(getText("common.saved"));
		ApiFactory.getBussinessLogService().log("部门管理", 
				"保存部门信息",ContextUtils.getSystemId("acs"));
		return RELOAD;
	}
	
	public String isSubDepartment(){
	    if(currentDeptId!=null&&departmentManager.isSubDepartment(currentDeptId,deptId)){
	    	renderText("true");
	    }else{
	    	if(currentDeptId!=null){
	    		Department d=departmentManager.getDepartment(currentDeptId);
	    		if(d.getBranch()){
	    			//部门、人员、工作组、分支机构、分支机构授权管理、角色
	    			//分支机构下的部门和分支机构
	    			List<Department> subDepartments=departmentManager.getSubDeptments(currentDeptId);
	    			//分支机构下的人员
	    			List<DepartmentUser> departmentUsers=departmentManager.getDepartmentToUserByBranchId(currentDeptId);
	    			//分支机构下的工作组
	    			List<Workgroup> workgroups=workGroupManager.getWorkgroupsByBranch(currentDeptId);
	    			//分支机构授权管理
	    			List<BranchAuthority> branchAuthoritys=branchAuthorityManager.getBranchAuthorityByBranch(currentDeptId);
	    			//角色
	    			List<Role> roles=roleManager.getRoleByBranches(currentDeptId);
	    			if((subDepartments!=null && subDepartments.size()>0) || (departmentUsers!=null&& departmentUsers.size()>0) || (workgroups!=null && workgroups.size()>0) || (branchAuthoritys!=null&&branchAuthoritys.size()>0) || (roles!=null&&roles.size()>0)){
	    				renderText("no");
	    			}else{
	    				renderText("false");
	    			}
	    		}else{
	    			renderText("false");
	    		}
	    	}else{
	    		renderText("false");
	    	}
	    }
		return null;
	}
	
	public String isBranchBelongToTopCompany(){
	    if(currentDeptId!=null&&departmentManager.isBranchBelongToTopCompany(currentDeptId)){
	    	renderText("true");
	    }else{
	    	renderText("false");
	    }
		return null;
	}
	
	public Department getModel() {
		return department;
	}
	
	public User getUser() {
		return user;
	}
	public void setUser(User user) {
		this.user = user;
	}
	public Integer getIsAddOrRomove() {
		return isAddOrRomove;
	}
	public void setIsAddOrRomove(Integer isAddOrRomove) {
		this.isAddOrRomove = isAddOrRomove;
	}
	public Page<UserInfo> getPageUserInfo() {
		return pageUserInfo;
	}
	public void setPageUserInfo(Page<UserInfo> pageUserInfo) {
		this.pageUserInfo = pageUserInfo;
	}

	public Long getCompanyId() {
		return ContextUtils.getCompanyId();
	}
	public void setCompanyId(Long companyId) {
		this.companyId = companyId;
	}

	public Long getDepartmentId() {
		return departmentId;
	}

	public void setDepartmentId(Long departmentId) {
		this.departmentId = departmentId;
	}
	public List<Long> getCheckedUserIds() {
		return checkedUserIds;
	}

	public void setCheckedUserIds(List<Long> checkedUserIds) {
		this.checkedUserIds = checkedUserIds;
	}
	
	public Page<User> getUserPage() {
		return userPage;
	}
	public void setUserPage(Page<User> userPage) {
		this.userPage = userPage;
	}

	public String getDepartmentCode() {
		return departmentCode;
	}

	public void setDepartmentCode(String departmentCode) {
		this.departmentCode = departmentCode;
	}
	
	public String getDepartmentName() {
		return departmentName;
	}

	public void setDepartmentName(String departmentName) {
		this.departmentName = departmentName;
	}

	public Long getParentId() {
		return parentId;
	}

	public void setParentId(Long parentId) {
		this.parentId = parentId;
	}
	
	public Page<Department> getPage() {
		return page;
	}

	public void setPage(Page<Department> page) {
		this.page = page;
	}

	public String temp()throws Exception{
		return SUCCESS;
	}
	
	@Required
	public void setDepartmentManager(DepartmentManager departmentManager) {
		this.departmentManager = departmentManager;
	}
	
	@Required
	public void setCompanyManager(CompanyManager companyManager) {
		this.companyManager = companyManager;
	}
	@Required
	public void setUserManager(UserManager userManager) {
		this.userManager = userManager;
	}
	@Required
	public void setRoleManager(RoleManager roleManager) {
		this.roleManager = roleManager;
	}

	@Required
	public void setBranchAuthorityManager(
			BranchAuthorityManager branchAuthorityManager) {
		this.branchAuthorityManager = branchAuthorityManager;
	}

	public void setId(Long id) {
		this.id = id;
	}
	
	public List<Role> getAllRoles() {
		return allRoles;
	}

	public void setAllRoles(List<Role> allRoles) {
		this.allRoles = allRoles;
	}

	public List<Long> getRoleIds() {
		return roleIds;
	}

	public void setRoleIds(List<Long> roleIds) {
		this.roleIds = roleIds;
	}

	public List<Long> getCheckedRoleIds() {
		return checkedRoleIds;
	}

	public void setCheckedRoleIds(List<Long> checkedRoleIds) {
		this.checkedRoleIds = checkedRoleIds;
	}
	
	public String getDepartmentByCompany(){
		if(companyId != null){
			page = departmentManager.queryDepartmentByCompany(page, companyId);
		}
		return SUCCESS;
	}
	
	public List<BusinessSystem> getSystems() {
		return systems;
	}
	
	public void setSystems(List<BusinessSystem> systems) {
		this.systems = systems;
	}
	
	@Required
	public void setBusinessSystemManager(BusinessSystemManager businessSystemManager) {
		this.businessSystemManager = businessSystemManager;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public void setDepartmentIds(List<Long> departmentIds) {
		this.departmentIds = departmentIds;
	}

	public List<Long> getDepartmentIds() {
		return departmentIds;
	}
	
	public void setCurrentId(String currentId) {
		this.currentId = currentId;
	}

	public List<Long> getUserIds() {
		return userIds;
	}

	public void setUserIds(List<Long> userIds) {
		this.userIds = userIds;
	}

	public String getIds() {
		return ids;
	}

	public void setIds(String ids) {
		this.ids = ids;
	}

	public String getTreeSelectedNode() {
		return treeSelectedNode;
	}

	public void setTreeSelectedNode(String treeSelectedNode) {
		this.treeSelectedNode = treeSelectedNode;
	}

	public Long getCurrentDeptId() {
		return currentDeptId;
	}

	public void setCurrentDeptId(Long currentDeptId) {
		this.currentDeptId = currentDeptId;
	}

	public Long getDeptId() {
		return deptId;
	}

	public void setDeptId(Long deptId) {
		this.deptId = deptId;
	}

	public Boolean getBranchFlag() {
		return branchFlag;
	}

	public void setBranchFlag(Boolean branchFlag) {
		this.branchFlag = branchFlag;
	}

	public List<Long> getBranchId() {
		return branchId;
	}

	public void setBranchId(List<Long> branchId) {
		this.branchId = branchId;
	}

	public String getBranchIds() {
		return branchIds;
	}

	public void setBranchIds(String branchIds) {
		this.branchIds = branchIds;
	}

	public String getChoseUserIds() {
		return choseUserIds;
	}

	public void setChoseUserIds(String choseUserIds) {
		this.choseUserIds = choseUserIds;
	}

	public String getChooseDepartmentId() {
		return chooseDepartmentId;
	}

	public void setChooseDepartmentId(String chooseDepartmentId) {
		this.chooseDepartmentId = chooseDepartmentId;
	}

	public Long getParentDepartmentId() {
		return parentDepartmentId;
	}

	public void setParentDepartmentId(Long parentDepartmentId) {
		this.parentDepartmentId = parentDepartmentId;
	}

	public Boolean getCanEditParentDepartment() {
		return canEditParentDepartment;
	}

	public void setCanEditParentDepartment(Boolean canEditParentDepartment) {
		this.canEditParentDepartment = canEditParentDepartment;
	}

	public Boolean getCanEditDepartmentInfor() {
		return canEditDepartmentInfor;
	}

	public void setCanEditDepartmentInfor(Boolean canEditDepartmentInfor) {
		this.canEditDepartmentInfor = canEditDepartmentInfor;
	}

	public Boolean getCanTransform() {
		return canTransform;
	}

	public void setCanTransform(Boolean canTransform) {
		this.canTransform = canTransform;
	}

	public Long getFirstBranchId() {
		return firstBranchId;
	}

	public void setFirstBranchId(Long firstBranchId) {
		this.firstBranchId = firstBranchId;
	}

	public String getDepartmentShortName() {
		return departmentShortName;
	}

	public void setDepartmentShortName(String departmentShortName) {
		this.departmentShortName = departmentShortName;
	}

	public String getAddUserIds() {
		return addUserIds;
	}

	public void setAddUserIds(String addUserIds) {
		this.addUserIds = addUserIds;
	}
}
