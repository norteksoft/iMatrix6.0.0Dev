package com.norteksoft.acs.web.authorization;

import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.apache.struts2.ServletActionContext;
import org.apache.struts2.convention.annotation.Action;
import org.apache.struts2.convention.annotation.ParentPackage;
import org.apache.struts2.convention.annotation.Result;
import org.apache.struts2.convention.annotation.Results;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Required;

import com.norteksoft.acs.base.utils.ExportRoleQuery;
import com.norteksoft.acs.base.web.struts2.CRUDActionSupport;
import com.norteksoft.acs.entity.authorization.BranchAuthority;
import com.norteksoft.acs.entity.authorization.BusinessSystem;
import com.norteksoft.acs.entity.authorization.Function;
import com.norteksoft.acs.entity.authorization.FunctionGroup;
import com.norteksoft.acs.entity.authorization.Role;
import com.norteksoft.acs.entity.organization.Company;
import com.norteksoft.acs.entity.organization.Department;
import com.norteksoft.acs.entity.organization.User;
import com.norteksoft.acs.entity.organization.Workgroup;
import com.norteksoft.acs.service.authorization.BranchAuthorityManager;
import com.norteksoft.acs.service.authorization.BusinessSystemManager;
import com.norteksoft.acs.service.authorization.RoleManager;
import com.norteksoft.acs.service.organization.CompanyManager;
import com.norteksoft.acs.service.organization.DepartmentManager;
import com.norteksoft.acs.service.organization.UserManager;
import com.norteksoft.acs.service.organization.WorkGroupManager;
import com.norteksoft.acs.service.syssetting.SecuritySetManager;
import com.norteksoft.acs.web.eunms.AddOrRomoveState;
import com.norteksoft.product.api.ApiFactory;
import com.norteksoft.product.orm.Page;
import com.norteksoft.product.util.ContextUtils;
import com.norteksoft.product.util.JsTreeUtils;
import com.norteksoft.product.util.JsonParser;
import com.norteksoft.product.util.PageUtils;
import com.norteksoft.product.util.tree.ZTreeNode;

@SuppressWarnings("deprecation")
@ParentPackage("default")
@Results( { 
	@Result(name = CRUDActionSupport.RELOAD, location = "role?businessSystemId=${businessSystemId}", type="redirectAction") 
	,@Result(name="RELOAD_CUSTOM_ROLE", location="custom-role?businessSystemId=${businessSystemId}", type="redirectAction")
	,@Result(name="RELOAD_STANDARD_ROLE", location="standard-role!authoritys?businessSystemId=${businessSystemId}&roleId=${roleId}", type="redirectAction")
})
public class RoleAction extends CRUDActionSupport<Role> {
	private static final long serialVersionUID = -5473169092158238538L;
	private static String ACS_SYSTEM_ADMIN="acsSystemAdmin";//系统管理员角色编码
	private static String ACS_SECURITY_ADMIN="acsSecurityAdmin";//安全管理员角色编码
	private static String ACS_AUDIT_ADMIN="acsAuditAdmin";//审计管理员角色编码
	private Page<Role> page = new Page<Role>(0, true);
	private Page<FunctionGroup> functionpage = new Page<FunctionGroup>(20, true);
	private Page<Workgroup> workGroupPage = new Page<Workgroup>(20, true);
	private Page<Department> departmentPage = new Page<Department>(20, true);
	private RoleManager roleManager;
	private BusinessSystemManager businessSystemManager;
	private SecuritySetManager securitySetManager;
	private List<Role> roles;
	private Role entity;
	private Long id;
	private Long paternId;
	private Long roleId;
	private Long businessSystemId;
	private DepartmentManager departmentManager;
	private List<Long> userIds;
	private List<User> allUsers;
	private List<Long> departmentsIds;
	private List<Long> functionIds = new ArrayList<Long>();
	private List<Long> checkedFunctionIds;
	private List<Long> checkedWorkGroupIds;
	private List<Long> workGroupIds;
	private Integer isAddOrRomove;
	private String departmentTree;
	private String usersTree;
	private String currentId;
	private Long roleGroupId;
	private String systemTree;
	private String workgroupTree;
	private CompanyManager companyManager;
	private WorkGroupManager workGroupManager;
	private String queryType;
	private String queryName;
	private String queryIds;
	private String queryTitle;
	private List<BusinessSystem> systems;
	private List<List<Role>> allRoles;
	private Map<User, List<List<Role>>> userRoles;
	private UserManager userManager;
	private String isHave;
	private List<Long> ids;
	private List<Long> roleIds;
	private String allInfos;
	private Boolean isAdminRole=false;//是否是管理员角色
	private List<Department> branches=new ArrayList<Department>();
	private String adminSign;//管理员标识：securityAdmin（安全管理员）、branchAdmin（分支机构管理员）
	private String companyName;//公司名称
	private String roleName;//角色名称
	private String roleCode;//角色编号
	private Long branchesId;//分支机构id
	private String moduleType;//模块类型：值为“role”表示角色管理，值为“log”表示系统日志
	private String manageBranchesIds="";//被管理的分支机构id
	private Map<User,List<Role>> userRoleMap=new HashMap<User,List<Role>>();
	private Map<Department,List<Role>> departmentRoleMap=new HashMap<Department,List<Role>>();
	private Map<Workgroup,List<Role>> workgroupRoleMap=new HashMap<Workgroup,List<Role>>();
	private Boolean containBranches;//集团公司中是否含有分支机构：true含有分支机构，false不含有分支机构
	private String viewType;//查看类型：user表示用户，department表示部门，workgroup表示工作组
	private String detailTitle="";
	private String addUserIds;
	private String addDepartmentIds;
	private String addWorkgroupIds;
	private String fids;//资源ids字符串
	private List<Function> functions=new ArrayList<Function>();
	private String exportQueryIds;
	
	private Boolean hasSecurityAdmin=true;//是否具有安全管理员
	
	public String getFids() {
		return fids;
	}
	public void setFids(String fids) {
		this.fids = fids;
	}
	@Autowired
	private BranchAuthorityManager branchAuthorityManager;
	
	private static final String SUCCESS_MESSAGE_LEFT = "<font class=\"onSuccess\"><nobr>";
	private static final String MESSAGE_RIGHT = "</nobr></font>";
	private static final String ERROR_MESSAGE_LEFT = "<font class=\"onError\"><nobr>";
	protected void addErrorMessage(String message){
		this.addActionMessage(ERROR_MESSAGE_LEFT+message+MESSAGE_RIGHT);
	}
	protected void addSuccessMessage(String message){
		this.addActionMessage(SUCCESS_MESSAGE_LEFT+message+MESSAGE_RIGHT);
	}

	@Required
	public void setDepartmentManager(DepartmentManager departmentManager) {
		this.departmentManager = departmentManager;
	}
	
	@Required
	public void setUserManager(UserManager userManager) {
		this.userManager = userManager;
	}

	@Required
	public void setSecuritySetManager(SecuritySetManager securitySetManager) {
		this.securitySetManager = securitySetManager;
	}

    public void prepareListUsers() throws Exception {
    	entity = roleManager.getRole(roleId);
	}

  
	public void prepareRemoveUsers() throws Exception {
    	entity = roleManager.getRole(roleId);
    	isAdminRole=hasAdminRole(entity);
	}
	
    /**
     * 给角色添加用户列表
     */
	public String listUsers() throws Exception{
		isAddOrRomove = AddOrRomoveState.ADD.code;
		if(!roleManager.hasSecurityAdminRole(ContextUtils.getUserId())&&roleManager.hasBranchAdminRole(ContextUtils.getUserId())){
			List<BranchAuthority> branchesList=branchAuthorityManager.getBranchByUser(ContextUtils.getUserId());
			for(BranchAuthority branches:branchesList){
				if(StringUtils.isNotEmpty(manageBranchesIds)){
					manageBranchesIds+=",";
				}
				manageBranchesIds+=branches.getBranchesId();
			}
		}
		return "user";
	}
	private boolean hasAdminRole(Role role){
		if(ACS_SYSTEM_ADMIN.equals(role.getCode())||ACS_AUDIT_ADMIN.equals(role.getCode())||ACS_SECURITY_ADMIN.equals(role.getCode())){
			return true;
		}
		return false;
	}
	
	public String loadWorkgroupTree(){
		StringBuilder tree = new StringBuilder("[ ");
		Company company = companyManager.getCompany(ContextUtils.getCompanyId());
		tree.append(JsTreeUtils.generateJsTreeNode("", "open", company.getName(), getWorkGroupNodes(company.getId())));
		tree.append(" ]") ;
		renderText(tree.toString());
		return null;
	}
	
	private String getWorkGroupNodes(Long companyId){
		List<Workgroup> workGroups = workGroupManager.queryWorkGroupByCompany(ContextUtils.getCompanyId());
		List<Long> wgIds = roleManager.getWorkGroupIds(roleId);
		StringBuilder nodes = new StringBuilder();
		for(Workgroup wg: workGroups){
			if(wg.isDeleted() || wgIds.contains(wg.getId())) continue;
			nodes.append(JsTreeUtils.generateJsTreeNode("USERSBYWORKGROUP,"+wg.getId().toString(), "", wg.getName()));
			nodes.append(",");
		}
		if(nodes.lastIndexOf(",") != -1 && nodes.lastIndexOf(",") == nodes.length()-1){
			nodes.replace(nodes.length()-1, nodes.length(), "");
		}
		return nodes.toString();
	}

	/**
	 * 给角色移除用户列表
	 */
	public String removeUsers() throws Exception{
		Role role = roleManager.getRole(roleId);
		businessSystemId = role.getBusinessSystem().getId();
		int deleteUserNum=0;//移除用户的个数
		int deleteDepartNum=0;//移除部门的个数
		int deleteWorkgroupNum=0;//移除工作组的个数
		int noDeleteUserNum=0;//未移除用户的个数
		int noDeleteDepartNum=0;//未移除部门的个数
		int noDeleteWorkgroupNum=0;//未移除工作组的个数
		if(roleManager.hasSecurityAdminRole(ContextUtils.getUserId())){
			roleManager.removeUDWFromRoel(roleId, userIds, departmentsIds, workGroupIds);
			if(userIds!=null&&userIds.size()>0){
				deleteUserNum=userIds.size();
			}
			if(departmentsIds!=null&&departmentsIds.size()>0){
				deleteDepartNum=departmentsIds.size();
			}
			if(workGroupIds!=null&&workGroupIds.size()>0){
				deleteWorkgroupNum=workGroupIds.size();
			}
		}else{
			List<BranchAuthority> branchList=branchAuthorityManager.getBranchByUser(ContextUtils.getUserId());
			for(BranchAuthority branch:branchList){
				manageBranchesIds+=branch.getBranchesId();
				manageBranchesIds+=getSubBranches(branch.getBranchesId());
				manageBranchesIds+=",";
			}
			List<Long> uIds=new ArrayList<Long>();
			List<Long> dIds=new ArrayList<Long>();
			List<Long> wIds=new ArrayList<Long>();
			if(StringUtils.isNotEmpty(manageBranchesIds)){
				if(userIds!=null && userIds.size()>0){
					for(Long uId:userIds){
						User u=userManager.getUserById(uId);
						if(u.getSubCompanyId()!=null&&manageBranchesIds.contains(u.getSubCompanyId()+",")){
							uIds.add(uId);
							deleteUserNum++;
						}else{
							noDeleteUserNum++;
						}
					}
				}
				if(departmentsIds!=null && departmentsIds.size()>0){
					for(Long dId:departmentsIds){
						Department d=departmentManager.getDepartment(dId);
						if(d.getSubCompanyId()!=null&&manageBranchesIds.contains(d.getSubCompanyId()+",")){
							dIds.add(dId);
							deleteDepartNum++;
						}else{
							noDeleteDepartNum++;
						}
					}
				}
				if(workGroupIds!=null && workGroupIds.size()>0){
					for(Long wId:workGroupIds){
						Workgroup w=workGroupManager.getWorkGroup(wId);
						if(w.getSubCompanyId()!=null&&manageBranchesIds.contains(w.getSubCompanyId()+",")){
							wIds.add(wId);
							deleteWorkgroupNum++;
						}else{
							noDeleteWorkgroupNum++;
						}
					}
				}
			}
			roleManager.removeUDWFromRoel(roleId, uIds, dIds, wIds);
		}
		if("acsSystemAdmin".equals(role.getCode())||"acsSecurityAdmin".equals(role.getCode())||"acsAuditAdmin".equals(role.getCode())){
			addSuccessMessage("移除"+deleteUserNum+"个用户，未移除"+noDeleteUserNum+"个用户。");
		}else{
			addSuccessMessage("移除"+deleteUserNum+"个用户，未移除"+noDeleteUserNum+"个用户；移除"+deleteDepartNum+"个部门，未移除"+noDeleteDepartNum+"个部门；移除"+deleteWorkgroupNum+"个工作组；未移除"+noDeleteWorkgroupNum+"个工作组。");
		}
		return "RELOAD_STANDARD_ROLE";
	}
	
	/**
	 *  角色添加用户时的树
	 */
	public String getCompanyNodes() throws Exception{
		StringBuilder tree = new StringBuilder("[ ");
		if("INITIALIZED".equals(currentId)){
			//公司里的部门节点
			StringBuilder subNodes = new StringBuilder();
			List<Department> departments = departmentManager.getAllDepartment();
			for(Department d : departments){
				String nodeString = getDdeptNodes(d);
				if(nodeString.length() > 0)
					subNodes.append(nodeString).append(",");
			}
			subNodes.append(generateJsTreeNode("NODEPARTMENTUS," + ContextUtils.getCompanyId(), 
					"closed", getText("user.noDepartment"), ""));
			if(subNodes.lastIndexOf(",") != -1 && subNodes.lastIndexOf(",") == subNodes.length()-1){
				subNodes.replace(subNodes.length()-1, subNodes.length(), "");
			}
			//公司节点
			tree.append(generateJsTreeNode("", "open", ContextUtils.getCompanyName(), subNodes.toString()));
		}else if(currentId.startsWith("DEPARTMENT")){
			tree.append(getUserNodes(Long.valueOf(currentId.substring(currentId.indexOf(',')+1, currentId.length()))));
		}else if(currentId.startsWith("NODEPARTMENTUS")){
			tree.append(getNoDepartmentUserNodes(Long.valueOf(currentId.substring(currentId.indexOf(',')+1, currentId.length()))));
		}
		tree.append(" ]");
		renderText(tree.toString());
		return null;
	}
	
	public String removeFromRole(){
		roleManager.removeUDWFromRoel(roleId, userIds, departmentsIds, workGroupIds);
		return "RELOAD_STANDARD_ROLE";
	}
	
	/**
	 * 角色添加用户时的部门节点 
	 */
	private String getDdeptNodes(Department dept){
		StringBuilder nodes = new StringBuilder();
		if(dept.getParent() == null){
			//部门树节点
			nodes.append(generateJsTreeNode("DEPARTMENT," + dept.getId(), "closed", dept.getName(), ""));
		}
		return nodes.toString();
	}
	
	/**
	 * 角色添加用户时的用户节点 
	 */
	public String getUserNodes(Long deptId) throws Exception{
		StringBuilder nodes = new StringBuilder();
		
		List<User> users = userManager.getUsersByDeptId(deptId);
		
		List<Department> subDepts = departmentManager.getSubDeptments(deptId);
		for(Department subDept : subDepts){
			nodes.append(generateJsTreeNode("DEPARTMENT," + subDept.getId(), "closed", subDept.getName(), ""));
			nodes.append(",");
		}
		List<Long> checkedUsers = roleManager.getCheckedUserByRole(roleId);
		if(isAddOrRomove == 0){
			for(User user : users){
				if(checkedUsers.contains(user.getId())) continue;
				nodes.append(generateJsTreeNode("USER," + user.getId(), "", user.getName(), "")).append(",");
			}
		}else if(isAddOrRomove == 1){
			for(User user : users){
				if(checkedUsers.contains(user.getId()))
					nodes.append(generateJsTreeNode("USER," + user.getId(), "", user.getName(), "")).append(",");
			}
		}
		if(nodes.lastIndexOf(",") != -1 && nodes.lastIndexOf(",") == nodes.length()-1){
			nodes.replace(nodes.length()-1, nodes.length(), "");
		}
		return nodes.toString();
	}
	
	/**
	 * 没有部门的用户的树节点
	 * @param companyId
	 * @return
	 */
	public String getNoDepartmentUserNodes(Long companyId){
		StringBuilder nodes = new StringBuilder();
		List<com.norteksoft.product.api.entity.User> users = ApiFactory.getAcsService().getUsersNotInDepartment(companyId);
		List<Long> checkedUsers = roleManager.getCheckedUserByRole(roleId);
		if(isAddOrRomove == 0){
			for(com.norteksoft.product.api.entity.User user : users){
				if(checkedUsers.contains(user.getId())) continue;
				nodes.append(generateJsTreeNode("USER," + user.getId(), "", user.getLoginName(), "")).append(",");
			}
		}else if(isAddOrRomove == 1){
			for(com.norteksoft.product.api.entity.User user : users){
				if(!checkedUsers.contains(user.getId())) continue;
				nodes.append(generateJsTreeNode("USER," + user.getId(), "", user.getLoginName(), "")).append(",");
			}
		}
		if(nodes.lastIndexOf(",") != -1 && nodes.lastIndexOf(",") == nodes.length()-1){
			nodes.replace(nodes.length()-1, nodes.length(), "");
		}
		return nodes.toString();
	}
	
	/**
	 * 给角色添加用户
	 * @return
	 * @throws Exception
	 */
	public String addUsersToRole() throws Exception{
		entity = roleManager.getRole(roleId);
		businessSystemId = entity.getBusinessSystem().getId();
		addSuccessMessage(roleManager.addUDWFromRoel(entity,stringToList(addUserIds),stringToList(addDepartmentIds),stringToList(addWorkgroupIds),allInfos==null?"":allInfos));
		return "RELOAD_STANDARD_ROLE";
	}
	
//	private PermissionsWebservice permissionsWebservice;
//	private static final String TEACHER_CODE = "LMS_TEACHER";
//	@Required
//	public void setPermissionsWebservice(PermissionsWebservice permissionsWebservice) {
//		this.permissionsWebservice = permissionsWebservice;
//	}
	
    public void prepareListDepartments() throws Exception {
    	entity = roleManager.getRole(roleId);
    	isAdminRole=hasAdminRole(entity);
	}
	
    /**
     * 角色可添加部门列表 
     */
	public String listDepartments() throws Exception{
		isAddOrRomove = AddOrRomoveState.ADD.code;
		if(!roleManager.hasSecurityAdminRole(ContextUtils.getUserId())&&roleManager.hasBranchAdminRole(ContextUtils.getUserId())){
			List<BranchAuthority> branchesList=branchAuthorityManager.getBranchByUser(ContextUtils.getUserId());
			for(BranchAuthority branches:branchesList){
				if(StringUtils.isNotEmpty(manageBranchesIds)){
					manageBranchesIds+=",";
				}
				manageBranchesIds+=branches.getBranchesId();
			}
		}
		return "department";
	}
	
	public String loadDepartmentTree() throws Exception{
		List<Long> checkedDepts = roleManager.getCheckedDepartmentByRole(roleId);
		StringBuilder tree = new StringBuilder("[ ");
		StringBuilder subNodes = new StringBuilder();
		List<Department> departments = departmentManager.getAllDepartment();
		for(Department d : departments){
			if(checkedDepts.contains(d.getId())) continue;
			String nodeString = getDepartmentsNodes(d, false);
			if(nodeString.length() > 0)
				subNodes.append(nodeString).append(",");
		}
		if(subNodes.lastIndexOf(",") != -1 && subNodes.lastIndexOf(",") == subNodes.length()-1){
			subNodes.replace(subNodes.length()-1, subNodes.length(), "");
		}
		tree.append(generateJsTreeNode("company", "open", ContextUtils.getCompanyName(), subNodes.toString()));
		tree.append(" ]") ;
		renderText(tree.toString());
		return null;
	}
	
	/** 
	 * 角色可移除部门列表 
	 */
	public String removeDepartments() throws Exception{
		isAddOrRomove = 1;
		StringBuilder tree = new StringBuilder("[ ");
		StringBuilder nodes = new StringBuilder();
		List<Department> departments = departmentManager.getDepartmentsInRole(roleId);
		for(Department dept : departments){
			String nodeString = getDepartmentsNodes(dept, false);
			if(nodeString.length() > 0)
				nodes.append(nodeString).append(",");
		}
		if(nodes.lastIndexOf(",") != -1 && nodes.lastIndexOf(",") == nodes.length()-1){
			nodes.replace(nodes.length()-1, nodes.length(), "");
		}
		tree.append(generateJsTreeNode("", "open", ContextUtils.getCompanyName(), nodes.toString()));
		tree.append(" ]") ;
		departmentTree = tree.toString();
		return "department";
	}
	
	/**
	 * 根据给定的部门生成树的部门节点
	 */
	private String getDepartmentsNodes(Department dept, boolean isSubDept){
		StringBuilder nodes = new StringBuilder();
		if(dept.getParent() != null && !isSubDept) return "";
		List<Department> subDept = departmentManager.getSubDeptments(dept.getId());
		if(subDept.size() > 0){
			StringBuilder subNodes = new StringBuilder();
			//子部门树节点列表
			for(Department d : subDept){
				if(d.isDeleted()) continue;
				subNodes.append(getDepartmentsNodes(d, true));
				subNodes.append(",");
			}
			//去掉最后一个逗号
			if(subNodes.lastIndexOf(",") == subNodes.length()-1){
				subNodes.replace(subNodes.length()-1, subNodes.length(), "");
			}
			//部门树节点
			nodes.append(generateJsTreeNode(dept.getId().toString(), "closed", dept.getName(), subNodes.toString()));
		}else{
			nodes.append(generateJsTreeNode(dept.getId().toString(), "", dept.getName(), ""));
		}
		return nodes.toString();
	}
	
	/**
	 *  生成树的一个NODE
	 * @param id        NODE的id
	 * @param state     NODE的状态   open || closed || ""
	 * @param data      NODE的显示数据
	 * @param children  NODE的子NODE 
	 * @return
	 */
	protected String generateJsTreeNode(String id, String state, String data, String children){
		StringBuilder node = new StringBuilder();
		node.append("{ attributes: { id : \"").append(id).append("\" }");
		if(state != null && !"".equals(state.trim())){
			node.append(",state : \"").append(state).append("\"");
		}
		node.append(", data: \"").append(data).append("\" ");
		if(children != null && !"".equals(children.trim())){
			node.append(", children : [").append(children).append("]");
		}
		node.append("}");
		return node.toString();
	}
	
	/**
	 * 给角色添加或移除部门
	 * @return
	 * @throws Exception
	 */
	public String addDepartmentsToRole() throws Exception{
		Role role = roleManager.getRole(roleId);
		this.setBusinessSystemId(role.getBusinessSystem().getId());
		roleManager.addDepartmentsToRole(roleId, departmentsIds, isAddOrRomove);
		if(isAddOrRomove == 0){
			addActionMessage(getText("common.saved"));
		}else if(isAddOrRomove == 1){
			addActionMessage(getText("common.saved"));
		}
		return "RELOAD_STANDARD_ROLE";
	}
	
	 public String forward(Object obj){
//		Object target = null;
//		if(obj instanceof HibernateProxy){
//	        HibernateProxy proxy = (HibernateProxy)obj;
//	        target = proxy.getHibernateLazyInitializer().getImplementation();
//	    }
		return "RELOAD_STANDARD_ROLE";
	}
	 
	@Override
	public String delete() throws Exception {
		String logSign="";//该字段只是为了标识日志信息：角色名称
		User user=userManager.getUserById(ContextUtils.getUserId());
		int deleteNum=0;//删除角色的个数
		int noDeleteNum=0;//未删除角色的个数
		if(roleManager.hasSecurityAdminRole(user)){
			for(Long rId : roleIds){
				Role r=roleManager.getRole(rId);
				if(StringUtils.isNotEmpty(logSign)){
					logSign+=",";
				}
				logSign+=r.getName();
				branchAuthorityManager.deleteRoleByBranchesId(rId);
				roleManager.clean(rId);
				roleManager.deleteRole(rId);
			}
		}else if(roleManager.hasBranchAdminRole(user)){
			for(Long rId:roleIds){
				if(validateDelete(rId)){
					Role r=roleManager.getRole(rId);
					if(StringUtils.isNotEmpty(logSign)){
						logSign+=",";
					}
					logSign+=r.getName();
					
					deleteNum++;
					branchAuthorityManager.deleteRoleByBranchesId(rId);
					roleManager.clean(rId);
					roleManager.deleteRole(rId);
				}
			}
			noDeleteNum=roleIds.size()-deleteNum;
		}
		
		if(StringUtils.isNotEmpty(logSign))
			ApiFactory.getBussinessLogService().log("角色管理", 
					"删除角色："+logSign,ContextUtils.getSystemId("acs"));
		
		addSuccessMessage("删除"+(roleIds.size()-noDeleteNum)+"个角色，未删除"+noDeleteNum+"个角色");
		return list();
	}
	
	private boolean validateDelete(Long rId){
		boolean sign=false;
		List<BranchAuthority> branchAuthoritys=branchAuthorityManager.getBranchByUser(ContextUtils.getUserId());
		if(branchAuthoritys!=null && branchAuthoritys.size()>0){
			Set<Department> branchSet=new HashSet<Department>();
			for(BranchAuthority branchAuthority:branchAuthoritys){
				Department department=departmentManager.getDepartment(branchAuthority.getBranchesId());
				packagingSubBranches(department,branchSet);
			}
			for(Department d:branchSet){
				Role r=roleManager.getRole(rId);
				if(d.getId().equals(r.getSubCompanyId())){
					sign=true;
					break;
				}
			}
		}
		return sign;
	}

	@Override
	public String list() throws Exception {
		User user=userManager.getUserById(ContextUtils.getUserId());
		hasSecurityAdmin = roleManager.hasSecurityAdminRole(user);
		if(page.getPageSize()>1){
			if(hasSecurityAdmin){
				page = roleManager.getAllRoles(page, businessSystemId);
			}else if(roleManager.hasBranchAdminRole(user)){
				//所管理的分支机构
				List<BranchAuthority> branchAuthoritys=branchAuthorityManager.getBranchByUser(ContextUtils.getUserId());
				Set<Long> branchesSet=new HashSet<Long>();
				for(BranchAuthority ba:branchAuthoritys){
					branchesSet.add(ba.getBranchesId());
					getSubBranches(ba.getBranchesId(),branchesSet);
				}
				page = roleManager.getRoles(page, businessSystemId,branchesSet);
			}
			this.renderText(PageUtils.pageToJson(page));
			return null;
		}
		return "role";
	}
	
	private void getSubBranches(Long departmentId, Set<Long> branchesSet) {
		List<Department> subDeptments=departmentManager.getSubDeptments(departmentId);
		for(Department d:subDeptments){
			if(d.getBranch()){
				branchesSet.add(d.getId());
			}
			getSubBranches(d.getId(), branchesSet);
		}
	}
	public String input() throws Exception {
		User user=userManager.getUserById(ContextUtils.getUserId());
		if(roleManager.hasSecurityAdminRole(user)){
			adminSign="securityAdmin";
			companyName=ContextUtils.getCompanyName();
			BusinessSystem businessSystem = businessSystemManager.getBusiness(businessSystemId);
			if(businessSystem!=null&&!"acs".equals(businessSystem.getCode())){
				branches=departmentManager.getAllBranches();
			}
		}else if(roleManager.hasBranchAdminRole(user)){
			adminSign="branchAdmin";
			List<BranchAuthority> branchAuthoritys=branchAuthorityManager.getBranchByUser(ContextUtils.getUserId());
			if(branchAuthoritys!=null && branchAuthoritys.size()>0){
				Set<Department> branchSet=new HashSet<Department>();
				for(BranchAuthority branchAuthority:branchAuthoritys){
					Department department=departmentManager.getDepartment(branchAuthority.getBranchesId());
					packagingSubBranches(department,branchSet);
				}
				branches.addAll(branchSet);
			}
		}
		if(entity.getId()==null){
			entity.setCode(createRoleCode());
		}
		return "input";
	}
	
	private String createRoleCode(){
		long num=0;
		List<Role> roles=roleManager.getDefaultCodeRoles();
		if(roles != null && roles.size()>0){
			for(Role r:roles){
				String codeNum=r.getCode().replace("role-", "");
				if(codeNum.matches("^-?\\d+$")&&Long.valueOf(codeNum)>num){
					num=Long.valueOf(codeNum);
				}
			}
		}else{
			return "role-1";
		}
		return "role-"+(num+1);
	}
	
	/**
	 * 封装该分支机构以及该分支机构下的所有子分支机构
	 */
	private void packagingSubBranches(Department department,Set<Department> branchSet) {
		if(department.getBranch()){
			branchSet.add(department);
		}
		List<Department> departments=departmentManager.getSubDeptments(department.getId());
		if(departments!=null && departments.size()>0){
			for(Department d:departments){
				packagingSubBranches(d,branchSet);
			}
		}
	}
	@Override
	protected void prepareModel() throws Exception {
		if (id != null) {
			entity = roleManager.getRole(id);
		} else {
			entity = new Role();
			if(businessSystemId != null){
				BusinessSystem businessSystem = businessSystemManager.getBusiness(businessSystemId);
				entity.setBusinessSystem(businessSystem);
			}
			//控制在acs中建角色时，保存公司id
			entity.setCompanyId(ContextUtils.getCompanyId());
		}
	}

	@Override
	public String save() throws Exception {
		boolean logSign=true;//该字段只是为了标识日志信息：true表示新建角色、false表示修改角色
		if(id!=null)logSign=false;
		if(entity.getId()==null){//只有在权限系统中新建角色时才需加公司id
			entity.setCompanyId(ContextUtils.getCompanyId());
		}
		if(entity.getWeight()==null){
			entity.setWeight(0);
		}
		roleManager.saveRole(entity);
		this.setBusinessSystemId(entity.getBusinessSystem().getId());
		addSuccessMessage(getText("common.saved"));
		
		if(logSign){
			ApiFactory.getBussinessLogService().log("角色管理", 
					"新建角色:"+entity.getName(),ContextUtils.getSystemId("acs"));
		}else{
			ApiFactory.getBussinessLogService().log("角色管理", 
					"修改角色:"+entity.getName(),ContextUtils.getSystemId("acs"));
		}
		return input();
	}
	
	/**
	 * 验证角色名称在同一系统同一分支机构唯一
	 * 验证角色编号在集团唯一
	 * @return
	 * @throws Exception
	 */
	public String validateNameOnly() throws Exception{
		List<Role> roleList=roleManager.getRoles(roleCode);
		List<Role> roles=roleManager.getRoles(businessSystemId,branchesId,roleName);
		boolean codeRepeat=validateOnly(roleList,id);
		boolean nameRepeat=validateOnly(roles,id);
		if(codeRepeat){
			if(nameRepeat){
				this.renderText("codeNameRepeat");//表示角色编号和角色名称都不唯一
			}else{
				this.renderText("codeRepeat");//表示角色编号不唯一
			}
		}else{
			if(nameRepeat){
				this.renderText("nameRepeat");//表示角色名称都不唯一
			}else{
				this.renderText("ok");//表示角色编号和角色名称都唯一
			}
		}
		return null;
	}
	
	private boolean validateOnly(List<Role> roles,Long id){
		boolean repeat=false;
		if(id==null){
			if(roles !=null && roles.size()>0){
				repeat=true;
			}
		}else{
			for(Role r:roles){
				if(!id.equals(r.getId())){
					repeat=true;
					break;
				}
			}
		}
		return repeat;
	}
	
	/**
	 * 跳转的添加子角色的页面
	 */
	public String inputSubRole() throws Exception {
		entity = roleManager.getRole(paternId);
		this.setBusinessSystemId(entity.getBusinessSystem().getId());
		//generateTree();
		return "subrole";
	}
	
	/**
	 * 验证是否有权限修改或删除角色
	 */
	@Action("role-validateRole")
	public String validateRole() throws Exception {
		String sign="no";
		User user=userManager.getUserById(ContextUtils.getUserId());
		if(roleManager.hasSecurityAdminRole(user)){
			sign="ok";
		}else if(roleManager.hasBranchAdminRole(user)){
			if(validateDelete(id)){
				sign="ok";
			}
		}
		this.renderText(sign);
		return null;
	}
	
	/*
	 * 生成系统JSON树
	 */
	@Action("role-systemTree")
	public String systemTree(){
		List<ZTreeNode> treeNodes = new ArrayList<ZTreeNode>();
		String result ="";
		List<BusinessSystem> businessSystems = new ArrayList<BusinessSystem>();
		User user=userManager.getUserById(ContextUtils.getUserId());
		if(roleManager.hasSecurityAdminRole(user)&&"role".equals(moduleType)){
			businessSystems = businessSystemManager.getAllBusiness();
		}else if((roleManager.hasAdminRole(user.getId()))){
			if(roleManager.hasBranchAdminRole(user)&&"role".equals(moduleType)){
				packagingSystemTree(businessSystems);
			}else{
				businessSystems = businessSystemManager.getAllBusiness();
			}
		}else if(roleManager.hasBranchAdminRole(user)){
			packagingSystemTree(businessSystems);
		}
		for(BusinessSystem bs : businessSystems){
			ZTreeNode root = new ZTreeNode("BUSINESSSYSTEM_"+bs.getId(),"0",bs.getName(), "false", "false", "", "", "folder", "");
			treeNodes.add(root);
		}
		if(businessSystems.size() > 0){
			if(businessSystemId == null){
				businessSystemId = businessSystems.get(0).getId();
			}
			
		}
		result = JsonParser.object2Json(treeNodes);
		renderText(result);
		return null;
	}
	
	private void packagingSystemTree(List<BusinessSystem> businessSystems){
		Set<BusinessSystem> businessSystemSet=new HashSet<BusinessSystem>();
		//所管理的分支机构
		List<BranchAuthority> branches=branchAuthorityManager.getBranchByUser(ContextUtils.getUserId());
		for(BranchAuthority b:branches){
			//所管理的分支机构-所拥有的角色
			List<BranchAuthority> roles=branchAuthorityManager.getRolesByBranch(b.getBranchesId());
			for(BranchAuthority ba:roles){
				Role r=roleManager.getRole(ba.getDataId());
				businessSystemSet.add(r.getBusinessSystem());
			}
			//所属分支机构为“所管理的分支机构”的角色
			packagingBusinessSystem(b.getBranchesId(),businessSystemSet);
			
			Set<Long> branchesSet=new HashSet<Long>();
			//所管理的分支机构的子分支机构
			getSubBranches(b.getBranchesId(), branchesSet);
			for(Long branchesId:branchesSet){
				//所属分支机构为“所管理的分支机构的子分支机构”的角色
				packagingBusinessSystem(branchesId,businessSystemSet);
			}
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
			businessSystems.add(bs);
		}
	}
	
    private void packagingBusinessSystem(Long branchesId,Set<BusinessSystem> businessSystemSet) {
    	List<Role> roleList=roleManager.getRoleByBranches(branchesId);
		for(Role r:roleList){
			businessSystemSet.add(r.getBusinessSystem());
		}
	}
	public String roleToFunctionList()throws Exception{
    	isAddOrRomove=AddOrRomoveState.ADD.code;
    	return "function-list";
    }
    
    public String roleRomoveFunctionList()throws Exception{
    	isAddOrRomove=AddOrRomoveState.ROMOVE.code;
    	return "function-list";
    }
    
    public String roleAddFunction()throws Exception{
    	Role role = roleManager.getRole(roleId);
    	this.setBusinessSystemId(role.getBusinessSystem().getId());
    	roleManager.roleAddFunction(roleId, fids,isAddOrRomove);
    	return null;
    }
    
    /**
     * 角色添加工作组
     */
    public void prepareRoleToWorkGroupList() throws Exception {
    	entity = roleManager.getRole(roleId);
    	isAdminRole=hasAdminRole(entity);
	}
    
    public String roleToWorkGroupList()throws Exception{
    	isAddOrRomove=AddOrRomoveState.ADD.code;
    	if(!roleManager.hasSecurityAdminRole(ContextUtils.getUserId())&&roleManager.hasBranchAdminRole(ContextUtils.getUserId())){
			List<BranchAuthority> branchesList=branchAuthorityManager.getBranchByUser(ContextUtils.getUserId());
			for(BranchAuthority branches:branchesList){
				if(StringUtils.isNotEmpty(manageBranchesIds)){
					manageBranchesIds+=",";
				}
				manageBranchesIds+=branches.getBranchesId();
				manageBranchesIds+=getSubBranches(branches.getBranchesId());
			}
		}
    	return "work-group-list";
    }
    
    private String getSubBranches(Long departmentId) {
    	String manageSubBranchesIds="";
		List<Department> subDeptments=departmentManager.getSubDeptments(departmentId);
		for(Department d:subDeptments){
			if(d.getBranch()){
				manageSubBranchesIds+=","+d.getId();
			}
			manageSubBranchesIds+=getSubBranches(d.getId());
		}
		return manageSubBranchesIds;
	}
    
    public void prepareRoleRomoveWorkGroupList() throws Exception {
    	entity = roleManager.getRole(roleId);
	}
    
    public String roleRomoveWorkGroupList()throws Exception{
    	HttpServletRequest request = ServletActionContext.getRequest();
    	Workgroup wgp = new Workgroup();
    	wgp.setCode(request.getParameter("workGroupCode"));
    	wgp.setName(request.getParameter("workGroupName"));
    	workGroupPage = roleManager.roleRomoveWorkGroupList(workGroupPage,wgp,roleId);
    	isAddOrRomove=AddOrRomoveState.ROMOVE.code;
//    	generateTree();
    	return "work-group-list";
    }
   
    public String roleAddWorkGroup()throws Exception{
    	entity = roleManager.getRole(roleId);
    	this.setBusinessSystemId(entity.getBusinessSystem().getId());
    	roleManager.roleAddWorkGroup(roleId, workGroupIds,isAddOrRomove);
    	return forward(entity);
    }
	
	public String getRolesByRoleGroup(){
		if(roleGroupId != null){
			page = roleManager.getRolesByRoleGroup(page, roleGroupId);
		}
		return SUCCESS;
	}
   
	public Role getModel() {
		return entity;
	}

	public Page<Role> getPage() {
		return page;
	}

	public void setPage(Page<Role> page) {
		this.page = page;
	}

	public List<Role> getRoles() {
		return roles;
	}

	public void setRoles(List<Role> roles) {
		this.roles = roles;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Long getPaternId() {
		return paternId;
	}

	public void setPaternId(Long paternId) {
		this.paternId = paternId;
	}
	
	public Long getBusinessSystemId() {
		return businessSystemId;
	}

	public void setBusinessSystemId(Long businessSystemId) {
		this.businessSystemId = businessSystemId;
	}

	@Required
	public void setRoleManager(RoleManager roleManager) {
		this.roleManager = roleManager;
	}

	@Required
	public void setBusinessSystemManager(BusinessSystemManager businessSystemManager) {
		this.businessSystemManager = businessSystemManager;
	}

	public Page<Department> getDepartmentPage() {
		return departmentPage;
	}

	public void setDepartmentPage(Page<Department> departmentPage) {
		this.departmentPage = departmentPage;
	}

	public List<Long> getUserIds() {
		return userIds;
	}

	public void setUserIds(List<Long> userIds) {
		this.userIds = userIds;
	}

	public Long getRoleId() {
		return roleId;
	}

	public void setRoleId(Long roleId) {
		this.roleId = roleId;
	}

	public List<User> getAllUsers() {
		return allUsers;
	}

	public void setAllUsers(List<User> allUsers) {
		this.allUsers = allUsers;
	}

	public List<Long> getDepartmentsIds() {
		return departmentsIds;
	}

	public void setDepartmentsIds(List<Long> departmentsIds) {
		this.departmentsIds = departmentsIds;
	}
	
	public List<Long> getFunctionIds() {
		return functionIds;
	}

	public void setFunctionIds(List<Long> functionIds) {
		this.functionIds = functionIds;
	}
	
	public Page<FunctionGroup> getFunctionpage() {
		return functionpage;
	}

	public void setFunctionpage(Page<FunctionGroup> functionpage) {
		this.functionpage = functionpage;
	}
	
	public List<Long> getCheckedFunctionIds() {
		return checkedFunctionIds;
	}

	public void setCheckedFunctionIds(List<Long> checkedFunctionIds) {
		this.checkedFunctionIds = checkedFunctionIds;
	}

	public Page<Workgroup> getWorkGroupPage() {
		return workGroupPage;
	}

	public void setWorkGroupPage(Page<Workgroup> workGroupPage) {
		this.workGroupPage = workGroupPage;
	}

	public List<Long> getCheckedWorkGroupIds() {
		return checkedWorkGroupIds;
	}

	public void setCheckedWorkGroupIds(List<Long> checkedWorkGroupIds) {
		this.checkedWorkGroupIds = checkedWorkGroupIds;
	}

	public List<Long> getWorkGroupIds() {
		return workGroupIds;
	}

	public void setWorkGroupIds(List<Long> workGroupIds) {
		this.workGroupIds = workGroupIds;
	}

	public Long getRoleGroupId() {
		return roleGroupId;
	}

	public void setRoleGroupId(Long roleGroupId) {
		this.roleGroupId = roleGroupId;
	}
	
	public Integer getIsAddOrRomove() {
		return isAddOrRomove;
	}

	public void setIsAddOrRomove(Integer isAddOrRomove) {
		this.isAddOrRomove = isAddOrRomove;
	}

	public String getDepartmentTree() {
		return departmentTree;
	}

	public void setDepartmentTree(String departmentTree) {
		this.departmentTree = departmentTree;
	}

	public String getUsersTree() {
		return usersTree;
	}

	public void setUsersTree(String usersTree) {
		this.usersTree = usersTree;
	}

	public String getCurrentId() {
		return currentId;
	}

	public void setCurrentId(String currentId) {
		this.currentId = currentId;
	}

	public String getSystemTree() {
		return systemTree;
	}

	public void setSystemTree(String systemTree) {
		this.systemTree = systemTree;
	}

	public String getWorkgroupTree() {
		return workgroupTree;
	}

	public void setWorkgroupTree(String workgroupTree) {
		this.workgroupTree = workgroupTree;
	}

	@Required
	public void setCompanyManager(CompanyManager companyManager) {
		this.companyManager = companyManager;
	}

	@Required
	public void setWorkGroupManager(WorkGroupManager workGroupManager) {
		this.workGroupManager = workGroupManager;
	}

	public String getQueryType() {
		return queryType;
	}

	public void setQueryType(String queryType) {
		this.queryType = queryType;
	}

	public String getQueryName() {
		return queryName;
	}

	public void setQueryName(String queryName) {
		this.queryName = queryName;
	}
	
	public String query(){
		if(!roleManager.hasSecurityAdminRole(ContextUtils.getUserId())&&roleManager.hasBranchAdminRole(ContextUtils.getUserId())){
			List<BranchAuthority> branchesList=branchAuthorityManager.getBranchByUser(ContextUtils.getUserId());
			for(BranchAuthority branches:branchesList){
				if(StringUtils.isNotEmpty(manageBranchesIds)){
					manageBranchesIds+=",";
				}
				manageBranchesIds+=branches.getBranchesId();
			}
		}
		if(queryType == null || "".equals(queryType)){queryTitle="选择用户"; return "query"; }
		String sign="";
		if(StringUtils.isNotEmpty(queryIds)){
			containBranches=departmentManager.containBranches();
			if("ROLE_USER".equals(queryType)){
				if("ALLCOMPANYID".equals(queryIds)){
					List<User> userList=userManager.getUsersByCompanyId(ContextUtils.getCompanyId());
					for(User u:userList){
						List<Role> roleList=roleManager.getRolesByUserId(u.getId());
						userRoleMap.put(u, roleList);
					}
					ApiFactory.getBussinessLogService().log("权限查询", 
							"查询所有用户权限",ContextUtils.getSystemId("acs"));
				}else{
					for(String userId:queryIds.split(",")){
						User u=userManager.getUserById(Long.valueOf(userId));
						if(u!=null){
							List<Role> roleList=roleManager.getRolesByUserId(u.getId());
							userRoleMap.put(u, roleList);
							if(StringUtils.isNotEmpty(sign)){
								sign+=",";
							}
							sign+=u.getName();
						}
					}
					
					if(StringUtils.isNotEmpty(sign)){
						ApiFactory.getBussinessLogService().log("权限查询", 
								"查询"+sign+"用户权限",ContextUtils.getSystemId("acs"));
					}
				}
				
			}else if("ROLE_DEPARTMENT".equals(queryType)){
				if("ALLDEPARTMENTID".equals(queryIds)){
					List<Department> deptList=departmentManager.getAllDepartment();
					for(Department d:deptList){
						List<Role> roleList=roleManager.getRolesByDepartmentId(d.getId());
						departmentRoleMap.put(d, roleList);
					}
					if(StringUtils.isNotEmpty(sign)){
						ApiFactory.getBussinessLogService().log("权限查询", 
								"查询所有部门权限",ContextUtils.getSystemId("acs"));
					}
				}else{
					for(String departmentId:queryIds.split(",")){
						Department d=departmentManager.getDepartment(Long.valueOf(departmentId));
						if(d!=null){
							List<Role> roleList=roleManager.getRolesByDepartmentId(d.getId());
							departmentRoleMap.put(d, roleList);
							if(StringUtils.isNotEmpty(sign)){
								sign+=",";
							}
							sign+=d.getName();
						}
					}
					
					if(StringUtils.isNotEmpty(sign)){
						ApiFactory.getBussinessLogService().log("权限查询", 
								"查询"+sign+"部门权限",ContextUtils.getSystemId("acs"));
					}
				}
				
			}else if("ROLE_WORKGROUP".equals(queryType)){
				if("ALLWORKGROUPID".equals(queryIds)){
					List<Workgroup> workgroupList=workGroupManager.getAllWorkGroup();
					for(Workgroup w:workgroupList){
						List<Role> roleList=roleManager.getRolesByWorkgroupId(w.getId());
						workgroupRoleMap.put(w, roleList);
					}
				}else{
					for(String workgroupId:queryIds.split(",")){
						Workgroup w=workGroupManager.getWorkGroup(Long.valueOf(workgroupId));
						if(w!=null){
							List<Role> roleList=roleManager.getRolesByWorkgroupId(w.getId());
							workgroupRoleMap.put(w, roleList);
							if(StringUtils.isNotEmpty(sign)){
								sign+=",";
							}
							sign+=w.getName();
						}
					}
					
					if(StringUtils.isNotEmpty(sign)){
						ApiFactory.getBussinessLogService().log("权限查询", 
								"查询"+sign+"工作组权限",ContextUtils.getSystemId("acs"));
					}
				}
			}
		}
		
		
		return "query";
	}
	
	/**
	 * 查看权限
	 * @return
	 */
	@Action("role-detail")
	public String detail(){
		entity=roleManager.getRole(roleId);
		containBranches=departmentManager.containBranches();
		if("user".equals(viewType)){
			User u=userManager.getUserById(id);
			detailTitle+="用户："+u.getName();
			if(containBranches)detailTitle+="("+u.getSubCompanyName()+")";
			detailTitle+="&nbsp;&nbsp;&nbsp;&nbsp;"+"角色："+entity.getName()+"("+entity.getBusinessSystem().getName();
			if(containBranches)detailTitle+="/"+entity.getSubCompanyName();
			detailTitle+=")";
		}else if("department".equals(viewType)){
			Department d=departmentManager.getDepartment(id);
			detailTitle+="部门："+d.getName();
			if(containBranches&&!d.getBranch())detailTitle+="("+d.getSubCompanyName()+")";
			detailTitle+="&nbsp;&nbsp;&nbsp;&nbsp;"+"角色："+entity.getName()+"("+entity.getBusinessSystem().getName();
			if(containBranches)detailTitle+="/"+entity.getSubCompanyName();
			detailTitle+=")";
		}else{
			Workgroup w=workGroupManager.getWorkGroup(id);
			detailTitle+="工作组："+w.getName();
			if(containBranches)detailTitle+="("+w.getSubCompanyName()+")";
			detailTitle+="&nbsp;&nbsp;&nbsp;&nbsp;"+"角色："+entity.getName()+"("+entity.getBusinessSystem().getName();
			if(containBranches)detailTitle+="/"+entity.getSubCompanyName();
			detailTitle+=")";
		}
		functions=roleManager.getFunctions(roleId);
		return "role-detail";
	}
	/**
	 * 导出
	 * @return
	 */
	@Action("role-exportRoleQuery")
	public String exportRoleQuery() throws Exception{
		String fileName="";
		List<Long> queryIdList=new ArrayList<Long>();
		if("ROLE_USER".equals(queryType)){
			fileName="用户权限";
			if(roleManager.hasSecurityAdminRole(ContextUtils.getUserId())){
				queryIdList=stringToList(exportQueryIds);
			}else{
				queryIdList=ApiFactory.getAcsService().getTreeUserIds(exportQueryIds);
			}
		}else if("ROLE_DEPARTMENT".equals(queryType)){
			fileName="部门权限";
			if(roleManager.hasSecurityAdminRole(ContextUtils.getUserId())){
				queryIdList=stringToList(exportQueryIds);
			}else{
				queryIdList=ApiFactory.getAcsService().getTreeDepartmentIds(exportQueryIds, true);
			}
		}else{
			fileName="工作组权限";
			if(roleManager.hasSecurityAdminRole(ContextUtils.getUserId())){
				queryIdList=stringToList(exportQueryIds);
			}else{
				queryIdList=ApiFactory.getAcsService().getTreeWorkgroupIds(exportQueryIds);
			}
		}
		HttpServletResponse response = ServletActionContext.getResponse();
		response.reset();
		response.setContentType("application/x-download");
		response.addHeader("Content-Disposition", "attachment;filename="+URLEncoder.encode(fileName+".xls","UTF-8"));
		ExportRoleQuery.exportRoleQuery(response.getOutputStream(), queryIdList, queryType);
		ApiFactory.getBussinessLogService().log("授权管理", 
				"导出角色",ContextUtils.getSystemId("acs"));
		return null;
	}
	

	private List<Long> stringToList(String queryIds) {
		List<Long> queryList=new ArrayList<Long>();
		if(StringUtils.isNotEmpty(queryIds)){
			for(String str:queryIds.split(",")){
				queryList.add(Long.valueOf(str));
			}
		}
		return queryList;
	}
	public List<BusinessSystem> getSystems() {
		return systems;
	}

	public void setSystems(List<BusinessSystem> systems) {
		this.systems = systems;
	}

	public List<List<Role>> getAllRoles() {
		return allRoles;
	}

	public void setAllRoles(List<List<Role>> allRoles) {
		this.allRoles = allRoles;
	}

	public Map<User, List<List<Role>>> getUserRoles() {
		return userRoles;
	}

	public void setUserRoles(Map<User, List<List<Role>>> userRoles) {
		this.userRoles = userRoles;
	}

	public String getAllInfos() {
		return allInfos;
	}

	public void setAllInfos(String allInfos) {
		this.allInfos = allInfos;
	}

	public String getIsHave() {
		return isHave;
	}

	public void setIsHave(String isHave) {
		this.isHave = isHave;
	}

	public void setIds(List<Long> ids) {
		this.ids = ids;
	}

	public String getQueryTitle() {
		return queryTitle;
	}

	public void setQueryTitle(String queryTitle) {
		this.queryTitle = queryTitle;
	}

	public void setRoleIds(List<Long> roleIds) {
		this.roleIds = roleIds;
	}
	public Boolean getIsAdminRole() {
		return isAdminRole;
	}
	public void setIsAdminRole(Boolean isAdminRole) {
		this.isAdminRole = isAdminRole;
	}
	public List<Department> getBranches() {
		return branches;
	}
	public void setBranches(List<Department> branches) {
		this.branches = branches;
	}
	public String getAdminSign() {
		return adminSign;
	}
	public String getCompanyName() {
		return companyName;
	}
	public String getRoleName() {
		return roleName;
	}
	public void setRoleName(String roleName) {
		this.roleName = roleName;
	}
	public Long getBranchesId() {
		return branchesId;
	}
	public void setBranchesId(Long branchesId) {
		this.branchesId = branchesId;
	}
	public void setRoleCode(String roleCode) {
		this.roleCode = roleCode;
	}
	public void setModuleType(String moduleType) {
		this.moduleType = moduleType;
	}
	public String getManageBranchesIds() {
		return manageBranchesIds;
	}
	public String getQueryIds() {
		return queryIds;
	}
	public void setQueryIds(String queryIds) {
		this.queryIds = queryIds;
	}
	public Map<User, List<Role>> getUserRoleMap() {
		return userRoleMap;
	}
	public Map<Department, List<Role>> getDepartmentRoleMap() {
		return departmentRoleMap;
	}
	public Map<Workgroup, List<Role>> getWorkgroupRoleMap() {
		return workgroupRoleMap;
	}
	public Boolean getContainBranches() {
		return containBranches;
	}
	public String getViewType() {
		return viewType;
	}
	public void setViewType(String viewType) {
		this.viewType = viewType;
	}
	public String getDetailTitle() {
		return detailTitle;
	}
	public void setAddUserIds(String addUserIds) {
		this.addUserIds = addUserIds;
	}
	public void setAddDepartmentIds(String addDepartmentIds) {
		this.addDepartmentIds = addDepartmentIds;
	}
	public void setAddWorkgroupIds(String addWorkgroupIds) {
		this.addWorkgroupIds = addWorkgroupIds;
	}
	public List<Function> getFunctions() {
		return functions;
	}
	public void setFunctions(List<Function> functions) {
		this.functions = functions;
	}
	public String getExportQueryIds() {
		return exportQueryIds;
	}
	public void setExportQueryIds(String exportQueryIds) {
		this.exportQueryIds = exportQueryIds;
	}
	public Boolean getHasSecurityAdmin() {
		return hasSecurityAdmin;
	}
}
