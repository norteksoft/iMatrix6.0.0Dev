package com.norteksoft.acs.web.authorization;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.apache.struts2.convention.annotation.Action;
import org.apache.struts2.convention.annotation.ParentPackage;
import org.apache.struts2.convention.annotation.Result;
import org.apache.struts2.convention.annotation.Results;
import org.springframework.beans.factory.annotation.Autowired;

import com.norteksoft.acs.base.enumeration.BranchDataType;
import com.norteksoft.acs.base.web.struts2.CRUDActionSupport;
import com.norteksoft.acs.entity.authorization.BranchAuthority;
import com.norteksoft.acs.entity.authorization.BusinessSystem;
import com.norteksoft.acs.entity.authorization.Role;
import com.norteksoft.acs.entity.organization.Department;
import com.norteksoft.acs.entity.organization.User;
import com.norteksoft.acs.service.authorization.BranchAuthorityManager;
import com.norteksoft.acs.service.authorization.BusinessSystemManager;
import com.norteksoft.acs.service.authorization.RoleManager;
import com.norteksoft.acs.service.authorization.StandardRoleManager;
import com.norteksoft.acs.service.organization.DepartmentManager;
import com.norteksoft.acs.service.organization.UserManager;
import com.norteksoft.product.util.ContextUtils;
import com.norteksoft.product.util.JsonParser;
import com.norteksoft.product.util.tree.ZTreeNode;

/**
 * 分支机构授权管理
 * 
 */
@ParentPackage("default")
@Results( { @Result(name = CRUDActionSupport.RELOAD, location = "branch-authority", type="redirectAction") })
public class BranchAuthorityAction extends CRUDActionSupport<BranchAuthority>{
	private static final long serialVersionUID = 1L;
	
	private Long id;
	private BranchAuthority branchAuthority;
	private List<User> users=new ArrayList<User>();
	private List<Role> roles=new ArrayList<Role>();
	private Long branchesId;//分支机构id
	private boolean selectPageFlag=false;
	private String roleIds;//角色id
	private String userIds;//人员id
	private String manageBranchesIds="";//被管理的分支机构id
	
	@Autowired
	private BranchAuthorityManager branchAuthorityManager;
	@Autowired
	private UserManager userManager;
	@Autowired
	private RoleManager roleManager;
	@Autowired
	private DepartmentManager departmentManager;
	@Autowired
	private BusinessSystemManager businessSystemManager;
	@Autowired
	private StandardRoleManager standardRoleManager;

	@Override
	@Action("branch-authority-clearAway")
	public String delete() throws Exception {
		int deleteNum=0;//删除角色的个数
		int noDeleteNum=0;//未删除角色的个数
		int deleteManagerNum=0;//删除管理员的个数
		if(StringUtils.isNotEmpty(roleIds)&&branchesId!=null){
			User user=userManager.getUserById(ContextUtils.getUserId());
			if(roleManager.hasSecurityAdminRole(user)){
				for(String roleId:roleIds.split(",")){
					deleteNum++;
					branchAuthorityManager.deleteRoleByBranchesId(branchesId,Long.valueOf(roleId));
				}
			}else if(roleManager.hasBranchAdminRole(user)){
				List<BranchAuthority> branchesList=branchAuthorityManager.getBranchByUser(ContextUtils.getUserId());
				Set<Role> roleSet=new HashSet<Role>();
				for(BranchAuthority branches:branchesList){
					List<BranchAuthority> roleList=branchAuthorityManager.getRolesByBranch(branches.getBranchesId());
					for(BranchAuthority role:roleList){
						Role r=roleManager.getRole(role.getDataId());
						roleSet.add(r);
					}
					packagingRole(branches.getBranchesId(),roleSet);
					//增加在角色管理中角色的所属分支机构为他的子分支机构的角色
					List<Department> subBranches=new ArrayList<Department>();
					getSubBranches(branches.getBranchesId(),subBranches);
					for(Department d:subBranches){
						packagingRole(d.getId(),roleSet);
					}
				}
				for(String roleId:roleIds.split(",")){
					for(Role role:roleSet){
						if(Long.valueOf(roleId).equals(role.getId())){
							deleteNum++;
							branchAuthorityManager.deleteRoleByBranchesId(branchesId,Long.valueOf(roleId));
						}
					}
				}
				noDeleteNum=roleIds.split(",").length-deleteNum;
			}
		}
		if(StringUtils.isNotEmpty(userIds)&&branchesId!=null){
			for(String userId:userIds.split(",")){
				List<BranchAuthority> branchesList=branchAuthorityManager.getBranchByUser(Long.valueOf(userId));
				if(branchesList.size()==1){
					Role role=roleManager.getRoleByCode("acsBranchAdmin");
					List<Long> uList=new ArrayList<Long>();
					uList.add(Long.valueOf(userId));
					roleManager.removeUDWFromRoel(role.getId(), uList, null, null);
				}
				deleteManagerNum++;
				branchAuthorityManager.deleteUserByBranchesId(branchesId,Long.valueOf(userId));
			}
		}
		this.renderText("移除"+deleteManagerNum+"个管理员，移除"+deleteNum+"个角色，未移除"+noDeleteNum+"个角色。");
		return null;
	}
	
	/**
	 * 根据用户id和子分支机构id获得该用户管理的分支机构id
	 * @param userId
	 * @param subBranchesId
	 * @return
	 */
	private Long getRootBranches(Long userId,Long subBranchesId){
		List<BranchAuthority> branchesList=branchAuthorityManager.getBranchByUser(userId);
		for(BranchAuthority branches:branchesList){
			boolean haveBranches=haveBranchesValidate(branches.getBranchesId(), subBranchesId);
			if(haveBranches){
				return branches.getBranchesId();
			}
		}
		return null;
	}
	
	/**
	 * 判断分支机构branchesId中是否有子分支机构subBranchesId，如果有返回true,否则返回false
	 * @param branchesId
	 * @param subBranchesId
	 * @return
	 */
	private boolean haveBranchesValidate(Long branchesId,Long subBranchesId) {
		boolean haveBranches=false;
		List<Department> subBranches=departmentManager.getSubDeptments(branchesId);
		for(Department depart:subBranches){
			if(depart.getBranch() && depart.getId().equals(subBranchesId)){
				haveBranches=true;
				break;
			}else{
				haveBranches=haveBranchesValidate(depart.getId(),subBranchesId);
			}
		}
		return haveBranches;
	}

	/**
	 * 分支机构授权管理列表
	 */
	@Override
	@Action("branch-authority")
	public String list() throws Exception {
		List<BranchAuthority> userList=branchAuthorityManager.getUsersByBranch(branchesId);
		for(BranchAuthority branch:userList){
			User u=userManager.getUserById(branch.getDataId());
			users.add(u);
		}
		List<BranchAuthority> roleList=branchAuthorityManager.getRolesByBranch(branchesId);
		for(BranchAuthority branch:roleList){
			Role r=roleManager.getRole(branch.getDataId());
			if(r != null){
				roles.add(r);
			}
		}
		User user=userManager.getUserById(ContextUtils.getUserId());
		if(!roleManager.hasSecurityAdminRole(user)&&roleManager.hasBranchAdminRole(user)){
			List<BranchAuthority> branchesList=branchAuthorityManager.getBranchByUser(ContextUtils.getUserId());
			for(BranchAuthority branches:branchesList){
				if(StringUtils.isNotEmpty(manageBranchesIds)){
					manageBranchesIds+=",";
				}
				manageBranchesIds+=branches.getBranchesId();
			}
		}
		return "branch-authority";
	}

	@Override
	protected void prepareModel() throws Exception {
		if(id==null){
			branchAuthority=new BranchAuthority();
		}else{
			branchAuthority=branchAuthorityManager.getBranchAuthority(id);
		}
	}

	@Override
	public String save() throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	public BranchAuthority getModel() {
		return branchAuthority;
	}
	
	/**
	 * 生成分支机构JSON树
	 */
	@Action("branches-tree")
	public String branchesTree(){
		List<ZTreeNode> treeNodes = new ArrayList<ZTreeNode>();
		String result ="";
		User user=userManager.getUserById(ContextUtils.getUserId());
		if(roleManager.hasSecurityAdminRole(user)){
			ZTreeNode root = new ZTreeNode("COMPANY_" + ContextUtils.getCompanyId(),"0",ContextUtils.getCompanyName(), "true", "false", "", "", "root", "");
			List<ZTreeNode> children = new ArrayList<ZTreeNode>();
			getSubBranches(null,children,"COMPANY_" + ContextUtils.getCompanyId());
			root.setChildren(children);
			treeNodes.add(root);
		}else if(roleManager.hasBranchAdminRole(user)){
			List<BranchAuthority> branches=branchAuthorityManager.getBranchByUser(ContextUtils.getUserId());
			for(BranchAuthority branch:branches){
				Department d=departmentManager.getDepartment(branch.getBranchesId());
				List<Department> subDepartments = departmentManager.getSubDeptments(d.getId());
				if(subDepartments != null && subDepartments.size()>0 && haveBranchesValidate(d.getId())){
					String nodeId="BRANCHES_" + d.getId();
					ZTreeNode root = new ZTreeNode(nodeId,"0",d.getName(), "true", "false", "", "", "root", "");
					List<ZTreeNode> children = new ArrayList<ZTreeNode>();
					getSubBranches(d.getId(),children,nodeId);
					root.setChildren(children);
					treeNodes.add(root);
				}else{
					ZTreeNode root = new ZTreeNode("BRANCHES_" + d.getId(),"0",d.getName(), "false", "false", "", "", "root", "");
					treeNodes.add(root);
				}
			}
		}
		result = JsonParser.object2Json(treeNodes);
		renderText(result);
		return null;
	}
	
	private void getSubBranches(Long departmentId,List<ZTreeNode> treeNodes,String parentId) {
		List<Department> departments = new ArrayList<Department>();
		if(departmentId==null){
			departments = departmentManager.getRootDepartment();
		}else{
			departments = departmentManager.getSubDeptments(departmentId);
		}
		for(Department d:departments){
			if(d.getBranch()){
				List<Department> subDepartments = departmentManager.getSubDeptments(d.getId());
				String nodeId="BRANCHES_" + d.getId();
				if(subDepartments != null && subDepartments.size()>0 && haveBranchesValidate(d.getId())){
					ZTreeNode root = new ZTreeNode(nodeId,"",d.getName(), "false", "false", "", "", "root", "");
					List<ZTreeNode> children = new ArrayList<ZTreeNode>();
					getSubBranches(d.getId(),children,nodeId);
					root.setChildren(children);
					treeNodes.add(root);
				}else{
					ZTreeNode root = new ZTreeNode(nodeId,parentId,d.getName(), "false", "false", "", "", "root", "");
					treeNodes.add(root);
				}
			}else{
				boolean haveBranches=haveBranchesValidate(d.getId());
				if(haveBranches){
					String nodeId="DEPARTMENT_" + d.getId();
					ZTreeNode root = new ZTreeNode(nodeId,"",d.getName(), "false", "false", "", "", "department", "");
					List<ZTreeNode> children = new ArrayList<ZTreeNode>();
					getSubBranches(d.getId(),children,nodeId);
					root.setChildren(children);
					treeNodes.add(root);
				}
			}
		}
	}
	
	/**
	 * 判断departmentId（部门id或分支机构id）中是否含有子分支机构，如果有返回true,否则返回false
	 * @param departmentId
	 * @return
	 */
	private boolean haveBranchesValidate(Long departmentId) {
		boolean haveBranches=false;
		List<Department> subBranches=departmentManager.getSubDeptments(departmentId);
		for(Department depart:subBranches){
			if(haveBranches){
				haveBranches=true;
				break;
			}
			if(depart.getBranch()){
				haveBranches=true;
				break;
			}else{
				haveBranches=haveBranchesValidate(depart.getId());
			}
		}
		return haveBranches;
	}
	
	/**
	 * 添加角色
	 * @return
	 */
	@Action("branch-authority-addRole")
	public String addRole() {
		List<ZTreeNode> treeNodes = new ArrayList<ZTreeNode>();
		ZTreeNode node=null;
		if(!selectPageFlag){
			StringBuilder tree = new StringBuilder("[ ");
			User user=userManager.getUserById(ContextUtils.getUserId());
			if(roleManager.hasSecurityAdminRole(user)){
				List<BusinessSystem> businessSystems = businessSystemManager.getAllBusiness();
				for(BusinessSystem bs : businessSystems){
					if(!"acs".equals(bs.getCode())){
						//tree.append(JsTreeUtils.generateJsTreeNodeNew("BUSINESSSYSTEM", "closed", bs.getName(), getRolesNodes(bs,treeNodes), ""));
						//tree.append(",");
						node = new ZTreeNode(bs.getId().toString(),"0",bs.getName(), "false", "false", "", "", "folder", "");
						treeNodes.add(node);
						getRolesNodes(bs,treeNodes);
					}
				}
			}else if(roleManager.hasBranchAdminRole(user)){
				List<BranchAuthority> branches=branchAuthorityManager.getBranchByUser(ContextUtils.getUserId());
				Set<Role> roleSet=new HashSet<Role>();
				for(BranchAuthority b:branches){
					List<BranchAuthority> roles=branchAuthorityManager.getRolesByBranch(b.getBranchesId());
					for(BranchAuthority ba:roles){
						Role r=roleManager.getRole(ba.getDataId());
						roleSet.add(r);
					}
					packagingRole(b.getBranchesId(),roleSet);
					//增加在角色管理中角色的所属分支机构为他的子分支机构的角色
					List<Department> subBranches=new ArrayList<Department>();
					getSubBranches(b.getBranchesId(),subBranches);
					for(Department d:subBranches){
						packagingRole(d.getId(),roleSet);
					}
				}
				Set<BusinessSystem> businessSystemSet=new HashSet<BusinessSystem>();
				for(Role r:roleSet){
					businessSystemSet.add(r.getBusinessSystem());
				}
				BusinessSystem[] sysArray=businessSystemSet.toArray(new BusinessSystem[businessSystemSet.size()]);
				for(int i=1;i<sysArray.length;i++){
					for(int j=0;j<sysArray.length-i;j++){
						if(sysArray[j].getId()>sysArray[j+1].getId()){
							BusinessSystem temp=sysArray[j];
							sysArray[j]=sysArray[j+1];
							sysArray[j+1]=temp;
						}
					}
				}
				for(BusinessSystem bs:sysArray){
					node = new ZTreeNode(bs.getId().toString(),"0",bs.getName(), "false", "false", "", "", "folder", "");
					treeNodes.add(node);
					getRolesNodes(bs,roleSet,treeNodes);
				}
			}
			renderText(JsonParser.object2Json(treeNodes));
			return null;
		}
		return "branch-authority-addRole";
	}
	
	private void getRolesNodes(BusinessSystem bs,Set<Role> roleSet,List<ZTreeNode> treeNodes){
		List<Role> roleList = new ArrayList<Role>();
		ZTreeNode node=null;
		for(Role r : roleSet){
			if(r.isDeleted()) continue;
			if(r.getCompanyId()!=null && !r.getCompanyId().equals(ContextUtils.getCompanyId())) continue;
			if(bs.equals(r.getBusinessSystem())){
				roleList.add(r);
			}
		}
		Role[] roleArray=roleList.toArray(new Role[roleList.size()]);
		for(int i=1;i<roleArray.length;i++){
			for(int j=0;j<roleArray.length-i;j++){
				if(roleArray[j].getId()>roleArray[j+1].getId()){
					Role temp=roleArray[j];
					roleArray[j]=roleArray[j+1];
					roleArray[j+1]=temp;
				}
			}
		}
		for(Role r:roleArray){
			node = new ZTreeNode("ROLE_"+r.getId().toString(),bs.getId().toString(), r.getName()+"("+r.getSubCompanyName()+")", "false", "false", "", "", "folder", "");
			treeNodes.add(node);
		}
	}
	
	private void packagingRole(Long branchesId,Set<Role> roleSet){
		List<Role> roleList=roleManager.getRoleByBranches(branchesId);
		for(Role r:roleList){
			roleSet.add(r);
		}
	}
	
	/**
	 * 根据分支机构id获得此分支机构的所有子分支机构
	 * @param branchesId
	 * @param subBranches
	 */
	private void getSubBranches(Long departmentId, List<Department> subBranches) {
		List<Department> subDeptments=departmentManager.getSubDeptments(departmentId);
		for(Department d:subDeptments){
			if(d.getBranch()){
				subBranches.add(d);
			}
			getSubBranches(d.getId(), subBranches);
		}
	}

	private void getRolesNodes(BusinessSystem bs,List<ZTreeNode> treeNodes){
		List<Role> roles = standardRoleManager.getRolesBySystemId(bs.getId());
		ZTreeNode node=null;
		for(Role r : roles){
			if(r.isDeleted()) continue;
			if(r.getCompanyId()!=null && !r.getCompanyId().equals(ContextUtils.getCompanyId())) continue;
			node = new ZTreeNode("ROLE_"+r.getId().toString(),bs.getId().toString(), r.getName()+"("+r.getSubCompanyName()+")", "false", "false", "", "", "folder", "");
			treeNodes.add(node);
		}
	}
	
	/**
	 * 添加管理员
	 * @return
	 */
	@Action("branch-authority-addManager")
	public String addManager() {
		if("ALLCOMPANYID".equals(userIds)){
			this.renderText("ALLCOMPANYID");
		}else{
//			User user=userManager.getUserById(ContextUtils.getUserId());
			List<Long> userIdList=new ArrayList<Long>();
//			if(roleManager.hasSecurityAdminRole(user)){
				String[] ids=userIds.split(",");
				for(String str:ids){
					userIdList.add(Long.valueOf(str));
				}
//			}else if(roleManager.hasBranchAdminRole(user)){
//				userIdList=ApiFactory.getAcsService().getTreeUserIds(userIds);
//			}
			String result=validateAddManager(userIdList,branchesId);
			if(StringUtils.isNotEmpty(result)){
				this.renderText(result);
			}else{
				for(Long userId:userIdList){
					branchAuthority=branchAuthorityManager.getBranchAuthorityUser(branchesId,userId);
					if(branchAuthority == null){//表示此分支机构下没有该人员，那就保存，否则不保存
						branchAuthority=new BranchAuthority();
						branchAuthority.setBranchesId(branchesId);
						branchAuthority.setDataId(userId);
						branchAuthority.setBranchDataType(BranchDataType.USER);
						branchAuthority.setCompanyId(ContextUtils.getCompanyId());
						branchAuthorityManager.saveBranchAuthority(branchAuthority);
						Role role=roleManager.getRoleByCode("acsBranchAdmin");
						List<Long> uList=new ArrayList<Long>();
						uList.add(userId);
						roleManager.roleAddUsers(role, uList, new ArrayList<Long>(), new ArrayList<Long>(), null);
					}
				}	
				this.renderText("ok");
			}
		}
		return null;
	}
	private String validateAddManager(List<Long> userIdList,Long branchId){
		List<Department> subBranches=new ArrayList<Department>();
		getSubBranches(branchId, subBranches);
		String result="";
		for(Department dept:subBranches){
			for(Long userId:userIdList){
				BranchAuthority ba=branchAuthorityManager.getBranchAuthorityUser(dept.getId(), userId);
				if(ba!=null){//此用户已经是此分支机构下的管理员
					User u=userManager.getUserById(userId);
					result=u.getName();
					break;
				}
			}
			if(StringUtils.isNotEmpty(result)){
				result="用户 "+result+" 已经是 "+dept.getName()+" 下的管理员";
				break;
			}
		}
		if(StringUtils.isEmpty(result)){
			Department dept=departmentManager.getDepartment(branchId);
			while(dept.getParent()!=null){
				dept=dept.getParent();
				if(dept.getBranch()){
					for(Long userId:userIdList){
						BranchAuthority ba=branchAuthorityManager.getBranchAuthorityUser(dept.getId(), userId);
						if(ba!=null){//此用户已经是此分支机构下的管理员
							User u=userManager.getUserById(userId);
							result=u.getName();
							break;
						}
					}
				}
				if(StringUtils.isNotEmpty(result)){
					result="用户 "+result+" 已经是 "+dept.getName()+" 下的管理员";
					break;
				}
			}
		}
		return result;
	}
	
	/**
	 * 验证添加移除权限
	 * @return
	 */
	@Action("branch-authority-validateAuthority")
	public String validateAuthority() {
		User user=userManager.getUserById(ContextUtils.getUserId());
		if(roleManager.hasSecurityAdminRole(user)){
			this.renderText("ok");
		}else if(roleManager.hasBranchAdminRole(user)){
			branchAuthority=branchAuthorityManager.getBranchAuthorityUser(branchesId,ContextUtils.getUserId());
			if(branchAuthority != null){
				this.renderText("no");
			}else{
				this.renderText("ok");
			}
		}
		return null;
	}
	
	/**
	 * 保存角色
	 * @return
	 */
	@Action("branch-authority-saveRole")
	public String saveRole(){
		String[] ids=roleIds.split(",");
		for(String str:ids){
			branchAuthority=branchAuthorityManager.getBranchAuthority(branchesId,Long.valueOf(str));
			if(branchAuthority == null){//表示此分支机构下没有该角色，那就保存，否则不保存
				branchAuthority=new BranchAuthority();
				branchAuthority.setBranchesId(branchesId);
				branchAuthority.setDataId(Long.valueOf(str));
				branchAuthority.setBranchDataType(BranchDataType.ROLE);
				branchAuthority.setCompanyId(ContextUtils.getCompanyId());
				branchAuthorityManager.saveBranchAuthority(branchAuthority);
			}
		}
		this.renderText("ok");
		return null;
	}
	
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public List<User> getUsers() {
		return users;
	}

	public void setUsers(List<User> users) {
		this.users = users;
	}

	public List<Role> getRoles() {
		return roles;
	}

	public void setRoles(List<Role> roles) {
		this.roles = roles;
	}

	public Long getBranchesId() {
		return branchesId;
	}

	public void setBranchesId(Long branchesId) {
		this.branchesId = branchesId;
	}

	public boolean isSelectPageFlag() {
		return selectPageFlag;
	}

	public void setSelectPageFlag(boolean selectPageFlag) {
		this.selectPageFlag = selectPageFlag;
	}

	public String getRoleIds() {
		return roleIds;
	}

	public void setRoleIds(String roleIds) {
		this.roleIds = roleIds;
	}

	public String getUserIds() {
		return userIds;
	}

	public void setUserIds(String userIds) {
		this.userIds = userIds;
	}

	public String getManageBranchesIds() {
		return manageBranchesIds;
	}
	
}
