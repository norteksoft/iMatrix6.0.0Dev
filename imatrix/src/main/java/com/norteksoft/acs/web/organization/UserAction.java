package com.norteksoft.acs.web.organization;

import java.io.File;
import java.io.IOException;
import java.net.URLEncoder;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.apache.struts2.ServletActionContext;
import org.apache.struts2.convention.annotation.Action;
import org.apache.struts2.convention.annotation.Namespace;
import org.apache.struts2.convention.annotation.ParentPackage;
import org.apache.struts2.convention.annotation.Result;
import org.apache.struts2.convention.annotation.Results;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Required;

import com.norteksoft.acs.base.utils.ExportUserInfo;
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
import com.norteksoft.acs.service.organization.ImportUserManager;
import com.norteksoft.acs.service.organization.UserInfoManager;
import com.norteksoft.acs.service.organization.UserManager;
import com.norteksoft.acs.service.sale.SubsciberManager;
import com.norteksoft.acs.web.eunms.AddOrRomoveState;
import com.norteksoft.product.api.ApiFactory;
import com.norteksoft.product.orm.Page;
import com.norteksoft.product.util.CollectionUtils;
import com.norteksoft.product.util.ContextUtils;
import com.norteksoft.product.util.Md5;
import com.norteksoft.product.util.PageUtils;

@SuppressWarnings("deprecation")
@Namespace("/organization")
@ParentPackage("default")
@Results( {
		@Result(name = CRUDActionSupport.RELOAD, location = "user?synchronousLdapMessage=${synchronousLdapMessage}&message=${message}&did=${did}", type = "redirectAction"),
		@Result(name = "deleteList", location = "user!deleteList", type = "redirectAction"),
		@Result(name = "redirect_url", location = "${redirectUrl}", type = "redirect")})
public class UserAction extends CRUDActionSupport<UserInfo> {

	private static final long serialVersionUID = 4814560124772644966L;
	private Page<User> page = new Page<User>(0, true);// 每页5项，自动查询计算总页数.
	private Page<Department> pageUserToDepart = new Page<Department>(20, true);// 每页5项，自动查询计算总页数.
	private Page<Workgroup> pageUserToWork = new Page<Workgroup>(20, true);// 每页5项，自动查询计算总页数.
	private UserInfoManager userInfoManager;
	private UserManager userManager;
	private DepartmentManager departmentManager;
	private List<UserInfo> allUser;
	private List<Department> allDepartment;
	private List<Workgroup> allWorkGroup;
	private List<Long> checkedWorkGroupIds;
	private List<Long> checkedDepartmentIds;
	private Long userInfoIds;
	private User user;
	private UserInfo entity;
	private List<UserInfo> userInfos;
	private Long id;
	private String passWord_CreateTime;
	private String ids;
	private List<Long> workGroupIds;
	private List<Long> departmentIds;
	private String dids;
	private Long userId;
	private List<Role> allRoles;
	private List<Long> roleIds;
	private List<Long> checkedRoleIds;
	private List<Long> passWordOverdueIds;
	private Map<Long,Integer> passwordOverNoticeId;
	private Integer isAddOrRomove;
	private String flag;
	private List<BusinessSystem> systems;
	private BusinessSystemManager businessSystemManager;
	private CompanyManager companyManager;
	private SubsciberManager subsciberManager;
	private String redirectUrl;
	private String password;
	private String states;
	private Long companyId;
	private String historyUserName;
	private String synchronousLdapMessage;
	private String message;
	private String departmentName;
	private String deId;
	private String type;
	private String depIds;
	private String usersId;
	private String mode;
	private String lookId;
	private String look;
	private String looked;
    private String oldDid;
	private String oldType;
	private String olDid;
	private String olType;
	private String passWordChange;
	private String departmId;
	private String departmType;
	private String edit;
	private String edited;;
	
	private String oraginalPassword;
	private Boolean isPasswordChange;
	private String levelpassword;
	
	private File file;
	private String fileName;
	
	private String oneDid;
	private String mainDepartmentName;
	
	private String comy;
	private String fromWorkgroup;
	private String fromChangeMainDepartment;//来自批量更换主职部门
	private Long newMainDepartmentId;
	private String userEmail;
	private String dscId;//正职部门的分支机构id
	private Long branchId;//分支机构id
	private String mainDepartmentSubCompanyId;
	private String validateLoginName;
	private String userLoginName;
	private String chooseDepartmentId;
	private String chooseDepartmentIds;
	private String isCrossBranch;
	private String branId;//分支机构id
	private Long deptId;
	private Long fromBracnhId;
	private Long uusId;
	private Long did;
	private Boolean containBranches=false;//集团公司中是否含有分支机构：true含有分支机构，false不含有分支机构
	private boolean canEditUser = false;//是否可以操作用户
	@Autowired
	private ImportUserManager importUserManager;
	
	@Autowired
	private BranchAuthorityManager branchAuthorityManager;
	
	@Autowired
	private RoleManager roleManager;
	@Action("list")
	public String toList() throws Exception{
		return SUCCESS;
	}
	
	/**
	 *=================用户管理==================== 用户管理的列表界面 条件是用户信息的dr字段等于0
	 */
	@Override
	@Action("user")
	public String list() throws Exception {
		containBranches=departmentManager.containBranches();
		if(departmId!=null&&departmType!=null){
			if(departmType.equals("USERSBYDEPARTMENT")
					||departmType.equals("USERSBYBRANCH"))
				departmentId=Long.parseLong(departmId);
		}
		if(did!=null){
			departmentId=did;
		}
		if(departmentId != null){
			return getUserByDepartment();
		}else if(workGroupId != null){
			return getUserByWorkGroup();
		}else if(departmType!=null&&(departmType.equals("NODEPARTMENT")||departmType.equals("NODEPARTMENT_USER")
				||departmType.equals("NOBRANCH")||departmType.equals("BRANCH_NODEPARTMENT_USER"))){
			return getNoDepartmentUsers();
		}else if(departmType!=null&&(departmType.equals("DELETED")||departmType.equals("DELETEDBRANCH"))){
			return deleteList();
		}else if(departmType!=null&&departmType.equals("allDepartment")){
			return getUserByCompanyHasLog();
		}else if(departmType!=null&&departmType.equals("company")){
			return getUserByCompanyHasLog();
		}else{
			flag = "true";
			if(!roleManager.hasAdminRole(ContextUtils.getUserId())){
				List<BranchAuthority> branchAuthoritys = branchAuthorityManager.getBranchByUser(ContextUtils.getUserId());
				if(!branchAuthoritys.isEmpty()){
				  fromBracnhId = branchAuthoritys.get(0).getBranchesId();
				}
			}
			return 	search();
		}
		
	}
	
	public String getUserByCompanyHasLog()throws Exception{
		if(page.getPageSize() <= 1){
			return SUCCESS; 
		}else{
			ApiFactory.getBussinessLogService().log("用户管理","查看用户列表",ContextUtils.getSystemId("acs"));
			page = userInfoManager.queryUsersByCompany(page, companyManager.getCompanyId());
			renderHtml(PageUtils.pageToJson(page));
			return null;
		}
	}

	public void prepareSearch() throws Exception {
		prepareModel();
	}

	public String search() throws Exception {
		searchCanEditUser();
		if(page.getPageSize() <= 1){
			ApiFactory.getBussinessLogService().log("用户管理","查看用户列表",ContextUtils.getSystemId("acs"));
			return SUCCESS; 
		}else{
			//如果是系统管理员
			if(roleManager.hasAdminRole(ContextUtils.getUserId())){
			    page = userInfoManager.getSearchUser(page, entity, 0, false);
			}else{
				List<BranchAuthority> branchAuthoritys = branchAuthorityManager.getBranchByUser(ContextUtils.getUserId());
				if(!branchAuthoritys.isEmpty()){
				  page = userInfoManager.queryUsersByBranch(page,branchAuthoritys.get(0).getBranchesId());
				}
			}
			this.renderText(PageUtils.pageToJson(page));
			return null;
		}
	}
	
	@Override
	public String save() throws Exception {
		boolean logSign=true;//该字段只是为了标识日志信息：true表示新建用户、false表示修改用户
		if(id!=null)logSign=false;
		Long oldDeptId = entity.getUser().getMainDepartmentId();
		if(StringUtils.isNotEmpty(oneDid)){//正职部门id
			  Department d = departmentManager.getDepartmentById(Long.valueOf(oneDid));
			  //如果跨分支机构，先要把user和原来分支机构下部门关系删除，
			  if(!departmentManager.isSameBranch(entity.getUser(),d)){
				  departmentManager.removeDepartmentToUser(entity.getUser(),d);
			  }
			//设置用户的分支机构id
			  if(d!=null){
					if(d.getBranch()){
						entity.getUser().setSubCompanyId(d.getId());
						entity.getUser().setSubCompanyName(StringUtils.isNotEmpty(d.getShortTitle())?d.getShortTitle():d.getName());
					}else{
						if(d.getSubCompanyId()!=null){
							entity.getUser().setSubCompanyId(d.getSubCompanyId());
							Department newbranch=departmentManager.getDepartment(d.getSubCompanyId());
							entity.getUser().setSubCompanyName(StringUtils.isNotEmpty(newbranch.getShortTitle())?newbranch.getShortTitle():newbranch.getName());
						}else{
							entity.getUser().setSubCompanyId(null);
							entity.getUser().setSubCompanyName(ContextUtils.getCompanyName());
						}
					}
				}
			  
		}else{//没有正职部门
			if(StringUtils.isNotEmpty(oldDid)){
				
				Department branch1=departmentManager.getDepartment(Long.valueOf(oldDid));
				if(branch1!=null){
					if(branch1.getBranch()){
						entity.getUser().setSubCompanyId(branch1.getId());
						entity.getUser().setSubCompanyName(StringUtils.isNotEmpty(branch1.getShortTitle())?branch1.getShortTitle():branch1.getName());
					}else{
						if(branch1.getSubCompanyId()!=null){
							entity.getUser().setSubCompanyId(branch1.getSubCompanyId());
							Department newbranch=departmentManager.getDepartment(branch1.getSubCompanyId());
							entity.getUser().setSubCompanyName(StringUtils.isNotEmpty(newbranch.getShortTitle())?newbranch.getShortTitle():newbranch.getName());
						}else{
							entity.getUser().setSubCompanyId(null);
							entity.getUser().setSubCompanyName(ContextUtils.getCompanyName());
						}
					}
				}
				
			}else{//集团公司下的无部门人员
				if(entity.getUser().getSubCompanyId()==null){
					entity.getUser().setSubCompanyName(ContextUtils.getCompanyName());
				}
			}
		}
		if((entity!=null&&entity.getId()==null)||"yes".equals(passWordChange)){
			entity.setPasswordUpdatedTime(new Date()); 
		}
		if(StringUtils.isNotEmpty(oneDid)){
			entity.getUser().setMainDepartmentId(Long.valueOf(oneDid));
		}
		
		userInfoManager.save(entity);
		id = entity.getId();
		
		//新建用户是默认给用户portal普通用户权限
		userInfoManager.giveNewUserPortalCommonRole(entity.getUser());
		
		//修改ldap密码
		if(Ldaper.isStartedAboutLdap()){
			Company company = companyManager.getCompany(ContextUtils.getCompanyId());
			List<Department> departments = userManager.getDepartmentsByUser(entity.getId());
			message = Ldaper.modifyUser(entity.getUser(), company.getCode(), departments, false, entity.getUser().getLoginName());
		}
		
		
		// 处理部门关系，正职或兼职有修改
		Set<Long> addDeptIds = new HashSet<Long>();
		List<Long> delDeptIds = new ArrayList<Long>();
		//dids是新兼职部门id  //deId为原来的兼职部门id
		if(StringUtils.isNotEmpty(oneDid)){
			//正职机构
			addDeptIds.add(Long.valueOf(oneDid));
			if(oldDeptId != null) delDeptIds.add(oldDeptId);
		}else{
			//if(StringUtils.isNotEmpty(oldDid)){//分支机构中无部门人员
			//	addDeptIds.add(Long.valueOf(oldDid));
			//}
			if(oldDeptId != null) delDeptIds.add(oldDeptId);
		}
		if(StringUtils.isEmpty(dids)){// 新兼职部门没有值
			if(StringUtils.isNotEmpty(deId)){ // 而原来的兼职部门有值，删除原来的
				String[] tempDelIds = deId.split("=");
				delDeptIds.addAll(CollectionUtils.changeList(tempDelIds));
			}
		}else{ // 新兼职部门有值
			// 增加的新兼职部门
			String[] tempAddIds = dids.split("=");
			addDeptIds.addAll(CollectionUtils.changeList(tempAddIds));
			if(StringUtils.isNotEmpty(deId)){ // 而原来的兼职部门有值，删除原来的
				String[] tempDelIds = deId.split("=");
				delDeptIds.addAll(CollectionUtils.changeList(tempDelIds));
			}
		}
		if(!delDeptIds.isEmpty()){// 删除的
			userManager.deleteDepartmemtToUser(delDeptIds, entity.getUser().getId());
			deId=null;
			if(addDeptIds.isEmpty()){
				userManager.addNoDepartmentToUser(entity.getId());
			}
		}
		if(!addDeptIds.isEmpty()){ // 增加的
			departmentIds = new ArrayList<Long>();departmentIds.addAll(addDeptIds);
			userManager.addDepartmentToUserDel(entity.getId(), departmentIds, 0);
		}
		
		//分支机构下点击无部门节点新建用户
		if(delDeptIds.isEmpty()&&addDeptIds.isEmpty()&&StringUtils.isNotEmpty(branId)){
			userManager.addBranchUser(entity.getId(),Long.valueOf(branId));
			branId="";
		}else if((!delDeptIds.isEmpty()||!addDeptIds.isEmpty())&&StringUtils.isNotEmpty(branId)){
			userManager.deleteBranchUser(entity.getId(),Long.valueOf(branId));
			branId="";
		}
			
		setUserDeptmentInfo(entity.getUser());
		if(logSign){
			ApiFactory.getBussinessLogService().log("用户管理", 
					"新建用户："+entity.getUser().getName(),ContextUtils.getSystemId("acs"));
		}else{
			ApiFactory.getBussinessLogService().log("用户管理", 
					"修改用户:"+entity.getUser().getName(),ContextUtils.getSystemId("acs"));
		}
		user=entity.getUser();
		Department department=null;
		if(user.getMainDepartmentId()!=null){
			department=departmentManager.getDepartment(user.getMainDepartmentId());
		}
		 if(department!=null){
		    	if(department.getBranch()){
		    		    mainDepartmentSubCompanyId=department.getId().toString();
		    	}else{
				    if(department.getSubCompanyId()==null){
			    		mainDepartmentSubCompanyId = "null";
			    	}else{
			    		mainDepartmentSubCompanyId = department.getSubCompanyId().toString();
			    	}
		    	}
		  }
	
			return INPUT;
	}
	
	/**
	 * 弹选多个部门树
	 */
	public String chooseDepartments(){
		
		return "departmentTree";
	}
	/**
	 * 弹选单个部门树
	 */
	public String chooseOneDepartment(){
		
		return "departmentSingleTree";
	}
	
	
	
	/**
	 * 修改密码方法
	 */
	public void prepareModifyPassWord() throws Exception {
		prepareModel();
	}
	
	public String modifyPassWord()throws Exception {
		SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd");
		passWord_CreateTime = sdf.format(new Date());
		return "modify-password";
	}
	
	/**
	 *各项目中的 修改密码的方法
	 */
	public void prepareUpdateUserPassword() throws Exception {
		userId=ContextUtils.getUserId();
		prepareModel();
	}
	
	public String updateUserPassword()throws Exception {
		SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd");
		passWord_CreateTime = sdf.format(new Date());
		User u=userManager.getUserById(ContextUtils.getUserId());
		oraginalPassword=u.getPassword();
		return "update-user-password";
	}
	
	public void prepareSavePassWord() throws Exception {
		prepareModel();
	}
    public String savePassWord()throws Exception {
    	if(Md5.toMessageDigest(levelpassword).equals(oraginalPassword)){
    		entity.setPasswordUpdatedTime(new Date());
    		userInfoManager.savePassWord(entity);
    		Company company = companyManager.getCompany(ContextUtils.getCompanyId());
    		List<Department> departments = userManager.getDepartmentsByUser(entity.getId());
    		if(Ldaper.isStartedAboutLdap()){
    			message = Ldaper.modifyUser(entity.getUser(), company.getCode(), departments, false, entity.getUser().getName());
    		}
    		isPasswordChange=true;
    	}else{
    		isPasswordChange=false;
    		User myuser = entity.getUser();
    		myuser.setPassword(oraginalPassword);
    		userManager.saveUser(myuser);
    		addActionMessage("原密码错误");
    	}
    	ApiFactory.getBussinessLogService().log("所有系统中", 
				"修改用户密码",ContextUtils.getSystemId("acs"));
		return "update-user-password";
	}
    
	public String input() throws Exception {
		if(id!=null){
			entity = user.getUserInfo();
			setUserDeptmentInfo(user);
			looked=look;
			edited=edit;
			ApiFactory.getBussinessLogService().log("用户管理", 
					"修改用户",ContextUtils.getSystemId("acs"));
			
			Department department=null;
			if(user.getMainDepartmentId()!=null){
				department=departmentManager.getDepartment(user.getMainDepartmentId());
			}
			 if(department!=null){
			    	if(department.getBranch()){
			    		    mainDepartmentSubCompanyId=department.getId().toString();
			    	}else{
					    if(department.getSubCompanyId()==null){
				    		mainDepartmentSubCompanyId = "null";
				    	}else{
				    		mainDepartmentSubCompanyId = department.getSubCompanyId().toString();
				    	}
			    	}
			  }
		}else{
			ApiFactory.getBussinessLogService().log("用户管理", 
					"新建用户",ContextUtils.getSystemId("acs"));
			if(departmentIds.size()>0&&departmentIds.get(0)!=null&&departmentIds.get(0)!=0){
				Department department=departmentManager.getDepartment(departmentIds.get(0));
			    if(department!=null&&!department.getBranch()){
			    	mainDepartmentName = department.getName();
			    }
			    if(department!=null){
			    	if(department.getBranch()){
			    		    mainDepartmentSubCompanyId=department.getId().toString();
			    	}else{
					    if(department.getSubCompanyId()==null){
				    		mainDepartmentSubCompanyId = "null";
				    	}else{
				    		mainDepartmentSubCompanyId = department.getSubCompanyId().toString();
				    	}
			    	}
			    }
			}
		}
		return INPUT;
	}
	
	private void setUserDeptmentInfo(User user){
		// 正职部门
		if(user.getMainDepartmentId()!=null){
			Department d = departmentManager.getDepartmentById(user.getMainDepartmentId());
			mainDepartmentName = (d==null?"":d.getName());
		}
		// 兼职部门
		List<Department> departments= userManager.getDepartmentsByUser(user.getId());
		departmentName="";deId="";
		if(departments.size()>0){
			for(Department department:departments){
				
				if(department.getName()==null?false:!department.getId().equals(user.getMainDepartmentId())&&!department.getBranch()){
			        departmentName+=department.getName()+",";
			        deId+=department.getId()+"=";
				}
			}
			if(StringUtils.isNotEmpty(deId)) deId=deId.substring(0, deId.length()-1);
			if(StringUtils.isNotEmpty(departmentName)) departmentName=departmentName.substring(0,departmentName.length()-1);
		}
	}
	public void prepareInputLook() throws Exception {
		prepareModel();
	}
	public String inputLook() throws Exception {
		if(id!=null){
			user=userInfoManager.getUserInfoById(id).getUser();
			if(user.getMainDepartmentId()!=null){
				Department d = departmentManager.getDepartment(user.getMainDepartmentId());
				mainDepartmentName = (d==null?"":d.getName());
			}
			List<Department> departments= userManager.getDepartmentsByUser(user.getId());
			departmentName="";
			if(departments.size()>0){
				for(Department department:departments){
					if(!department.getId().equals(user.getMainDepartmentId())&&!department.getBranch())
			        departmentName+=department.getName()+",";
				}
				if(departmentName.length() > 0) departmentName = departmentName.substring(0,departmentName.length()-1);
			}
			looked=look;
		}
		return INPUT;
	}

	

  
  public String checkUserRegister()throws Exception{
	  Integer maxUser = subsciberManager.getAllowedNumbByCompany(userInfoManager.getCompanyId());
  	  Integer currentUser = userInfoManager.getCompanyIsUsers();
  	  HttpServletRequest request = ServletActionContext.getRequest();
	  String weburl = request.getParameter("weburl");
  	  if(maxUser.intValue()<(currentUser.intValue()+1)){
  		 renderText("1");
  	  }else{
  		renderText(weburl);
  	  }
  	  return null;
  }
  
  
  /**
   * 移除用户
   * @return
   * @throws Exception
   */
	public String falseDelete() throws Exception {
		String logSign="";//该字段只是为了标识日志信息：用户1，用户2，...
		
		String[] arr=ids.split(",");
		for(String userId:arr){
			userInfoManager.falseDelete(Long.valueOf(userId),departmentIds);
			
			user=userManager.getUserById(Long.valueOf(userId));
			if(StringUtils.isNotEmpty(logSign)){
				logSign+=",";
			}
			logSign+=user.getName();
		}
		if(departmentIds.get(0)!=null){
		    departmentId=departmentIds.get(0);
		}else{
			if(StringUtils.isEmpty(departmType)){
				departmType = "NODEPARTMENT";
			}
		}
		ApiFactory.getBussinessLogService().log("用户管理", 
				"移除用户:"+logSign,ContextUtils.getSystemId("acs"));
		return list();
	}
	
	/**
	 * 删除用户
	 * @return
	 * @throws Exception
	 */
	public String clearUser() throws Exception {
		String logSign="";//该字段只是为了标识日志信息：用户1，用户2，...
		
		String[] arr=ids.split(",");
		for(String userId:arr){
			userInfoManager.clearUser(Long.valueOf(userId));
			
			user=userManager.getUserById(Long.valueOf(userId));
			if(StringUtils.isNotEmpty(logSign)){
				logSign+=",";
			}
			logSign+=user.getName();
		}
		if(departmentIds.get(0)!=null){
			departmentId=departmentIds.get(0);
		}else{
			if(StringUtils.isEmpty(departmType)){
				departmType = "NODEPARTMENT";
			}
		}
		ApiFactory.getBussinessLogService().log("用户管理", 
				"删除用户:"+logSign,ContextUtils.getSystemId("acs"));
		return list();
	}
	/**
	 * 判断是否是管理员
	 * @return
	 * @throws Exception
	 */
	public String checkIsAdmin() throws Exception {
		//User user = userManager.getUserById(id);
		String roles = "";
		String result = "";
		String[] arr=ids.split(",");
		for(String userId : arr){
			user = userManager.getUserById(Long.valueOf(userId));
			roles = roleManager.getRoleUsersIncludeTrustedRole(user);
			if(roles.indexOf("Admin")>-1){
				result = "yes";
			}
		}
		renderText(result);
		return null;
	}

	/**
	 * 用户禁用
	 */
	public void forbidden() throws Exception {
		userInfoManager.forbidden(id);
		
	}

	/**
	 * 用户启用
	 */
	public void invocation() throws Exception {
		userInfoManager.invocation(id);
		
	}

	/**
	 * 用户解锁
	 */
	public void unblock() throws Exception {
		userInfoManager.unblock(id);
		
	}
	/**
	 * 锁定用户
	 */
	public void lock() throws Exception {
		userInfoManager.lock(id);
	}
	public void prepareUserManger() throws Exception {
		prepareModel();
	}
	
	public String userManger()throws Exception{
		return "state";
	}
	
	
	public String saveUserState()throws Exception{
		if(StringUtils.isEmpty(states)){
			return list();
		}else{
			boolean logSign=true;//该字段只是为了标识日志信息：true表示启用、false表示禁用
			entity = userInfoManager.getUserInfoById(id);
			
			String[] stateStr = states.split(",");
			for (int i=0;i<stateStr.length;i++) {
				if(stateStr[i].equals("accountUnLock"))//用户密码过期解锁
					unblock();
				if(stateStr[i].equals("accountLock"))//用户密码过期不解锁
					lock();
				if(stateStr[i].equals("forbidden"))//禁用
					forbidden();
					logSign=false;
				if(stateStr[i].equals("invocation")){//启用
					invocation();
					logSign=true;
				}
			}
			
			if(logSign){
				ApiFactory.getBussinessLogService().log("用户管理", 
						"改变用户状态:启用"+entity.getUser().getName(),ContextUtils.getSystemId("acs"));
			}else{
				ApiFactory.getBussinessLogService().log("用户管理", 
						"改变用户状态:禁用"+entity.getUser().getName(),ContextUtils.getSystemId("acs"));
			}
			
			return list();
		}
		
	}

	/**
	 * ================已删除用户管理==============
	 * 
	 */
	@Override
	public String delete() throws Exception {
		String userNames = userInfoManager.delete(ids);
		if(StringUtils.isNotEmpty(userNames))
		ApiFactory.getBussinessLogService().log("已删除用户管理", 
				"彻底删除用户:"+userNames,ContextUtils.getSystemId("acs"));
		return deleteList();
	}

	/**
	 * 已删除用户的列表界面
	 */
	public String deleteList() throws Exception {
		deleteListCanEditUser();
		containBranches=departmentManager.containBranches();
		if(page.getPageSize() <= 1){
			return "delete"; 
		}else{
			if(departmType.equals("DELETEDBRANCH")){
				userInfoManager.getBranchDeletedUser(page, entity, 0, true,branchId);
			}else{
			    userInfoManager.getCompanyDeleteUser(page, entity, 0, true);
			}
			renderHtml(PageUtils.pageToJson(page));
			ApiFactory.getBussinessLogService().log("已删除用户管理", 
					"已删除用户列表",ContextUtils.getSystemId("acs"));
			return null;
		}
	}
	private void deleteListCanEditUser(){
		canEditUser = roleManager.hasSystemAdminRole(ContextUtils.getUserId());
		if(canEditUser)return;
		if(departmType!=null&&departmType.equals("DELETEDBRANCH")){
			hasBranchAdminRole(branchId);
		}
	}
	
	private void hasBranchAdminRole(Long branchId){
		canEditUser = branchAuthorityManager.hasBranchAdminRole(branchId, ContextUtils.getUserId());
		if(canEditUser)return;
		List<Long> branchIds = new ArrayList<Long>();
		List<BranchAuthority> branches=branchAuthorityManager.getBranchByUser(ContextUtils.getUserId());
		for(BranchAuthority branch:branches){
			Department d=departmentManager.getDepartment(branch.getBranchesId());
			branchIds.add(d.getId());
			userManager.getSubBranchs(d.getId(),branchIds);
		}
		if(branchIds.contains(branchId)){
			canEditUser=true;
		}
	}
	
	/**
	 * 无部门人员列表
	 */
	
	public String getNoDepartmentUsers() throws Exception{
		getNoDepartmentUsersCanEditUser();
		containBranches=departmentManager.containBranches();
		if(page.getPageSize() <= 1){
			return SUCCESS; 
		}else{
			ApiFactory.getBussinessLogService().log("用户管理", 
					"查看用户列表",ContextUtils.getSystemId("acs"));
			if(departmType.equals("NOBRANCH")||departmType.equals("BRANCH_NODEPARTMENT_USER")){
				userInfoManager.getNoBranchUsers(page,branchId);
			}else{
			    userInfoManager.getNoDepartmentUsers(page);
			}
			renderHtml(PageUtils.pageToJson(page));
			return null;
		}
	}
	private void getNoDepartmentUsersCanEditUser(){
		canEditUser = roleManager.hasSystemAdminRole(ContextUtils.getUserId());
		if(canEditUser)return;
		if(departmType.equals("NOBRANCH")||departmType.equals("BRANCH_NODEPARTMENT_USER")){
			hasBranchAdminRole(branchId);
		}
	}
	/**
	 * 已删除用户管理查询方法
	 */
	public void prepareSearchDelete() throws Exception {
		prepareModel();
	}

	public String searchDelete() throws Exception {
		page = userInfoManager.getCompanyDeleteUser(page, entity,0, true);
		return "delete";
	}
	
	  /**
     * 跳转到己删除用户添加部门页面
     */
    public void prepareToDepartmentToUsersDel() throws Exception {
    	
    	//entity =userInfoManager.getUserInfoById(userId);
    	userInfos=new ArrayList<UserInfo>();
    	String[] arr=ids.split(",");
    	for(int i=0;i<arr.length;i++){
    		User user=userManager.getUserById(Long.valueOf(arr[i]));
    		userInfos.add(user.getUserInfo());
    	}
	} 
    
    public String toDepartmentToUsersDel()throws Exception{
    	pageUserToDepart = userManager.getDepartmentList(pageUserToDepart);
    	//checkedDepartmentIds = userManager.getCheckedDepartmentIds(userId);
    	 isAddOrRomove=AddOrRomoveState.ADD.code;
    	return "deleted-department-list";
    }

	/**
	 * 给用户(己删除)分配部门
	 */
	public String saveDepartmentToUserDel() throws Exception{
		String[] arr=ids.split(",");
		String logUserNames ="";
		String logDepartmentName = "";
		for(int i=0;i<arr.length;i++){
			User user = userManager.getUserById(Long.valueOf(arr[i]));
			Department department=departmentManager.getDepartment(departmentId);
			if(!userInfoManager.isDepartmentHasSameLoginNameUser(user,department)){
				logUserNames+=user.getName()+",";
				user.getUserInfo().setDr(0);
				user.getUserInfo().setDeleted(false);
				user.setDeleted(false);
				
				logDepartmentName=department.getName();
				if(department != null){
					user.setMainDepartmentId(departmentId);
				}
				//设置用户的分支机构id
				if(department.getBranch()){
				    user.setSubCompanyId(department.getId());
				}else{
					user.setSubCompanyId(department.getSubCompanyId());
				}
				userInfoManager.save(user.getUserInfo());
				userManager.saveUser(user);
				List<Long> dIds = new ArrayList<Long>();
				dIds.add(departmentId);
				userManager.addDepartmentToUserDel(user.getUserInfo().getId(), dIds,0);
		   }
		}
		if(StringUtils.isNotEmpty(logUserNames)&&StringUtils.isNotEmpty(logDepartmentName)){
		ApiFactory.getBussinessLogService().log("用户管理", 
				"已删除用户:把用户["+logUserNames.substring(0, logUserNames.length()-1)+"]分配给部门["+logDepartmentName+"]",ContextUtils.getSystemId("acs"));
		}
		return deleteList();
	}
	
	/**
	 * 批量更换用户的主职部门
	 */
	public String batchChangeUserMainDepartment() throws Exception{
		userManager.batchChangeMainDepartment(ids,newMainDepartmentId);
		
		String logSign="";//该字段只是为了标识日志信息：用户1，用户2，...
		if(StringUtils.isNotEmpty(ids)){
			String[] userids=ids.split(",");
			Department department = departmentManager.getDepartment(newMainDepartmentId);
			for(String userid:userids){
				user=userManager.getUserById(Long.valueOf(userid));
				if(StringUtils.isNotEmpty(logSign)){
					logSign+=",";
				}
				logSign+=user.getName();
			}
			ApiFactory.getBussinessLogService().log("用户管理", 
					"更改用户"+logSign+"正职部门为"+department.getName(),ContextUtils.getSystemId("acs"));
		}
		return list();
	}

	/**
	 * 给用户分配部门
	 */
	
	public String  changeDepartment(){
		String[] d=depIds.split("=");
		String[] u=usersId.split(",");
		List<Long> departIds=CollectionUtils.changeList(d);
		List<Long> uIds=CollectionUtils.changeList(u);
		for(Long id:uIds){
			List<Department> departments=userManager.getDepartmentsByUser(id);
			if(departments.size()>0){
				List<Long> depaIds=new ArrayList<Long>();
				for(Department department :departments){
					depaIds.add(department.getId());
				}
				userManager.deleteDepartmemtToUser(depaIds,id);
			}
			userManager.addDepartmentToUserDel(id,departIds ,0);
		}
		return RELOAD;
	}

	/**
	 * 用户分配角色
	 */
	public String listRoles() throws Exception {
		isAddOrRomove = 0;
		userId = entity.getUser().getId();
		systems = businessSystemManager.getAllBusiness();
		checkedRoleIds = userManager.getCheckedRoleIdsByUser(userId);
		return "role";
	}
	
	/**
	 * 用户移除角色
	 */
	public String removeRoles() throws Exception{
		isAddOrRomove = 1;
		userId = entity.getUser().getId();
		systems = businessSystemManager.getAllBusiness();
		checkedRoleIds = userManager.getCheckedRoleIdsByUser(userId);
		return "role";
	}

	/**
	 * 给用户分配角色
	 */
	public String addRolesToUser() {
		userManager.addRolesToUser(userId, roleIds, isAddOrRomove);
		addActionMessage(getText("department.addRolesSuccess"));
		return RELOAD;
	}
	
	  /**
     * 跳转到人员添加部门页面
     */
    public void prepareAddDepartmentToUsers() throws Exception {
    	//entity =userInfoManager.getUserInfoById(userId);
	} 
    public String addDepartmentToUsers()throws Exception{
    	
    	pageUserToDepart = userManager.getDepartmentList(pageUserToDepart);
    	//checkedDepartmentIds = userManager.getCheckedDepartmentIds(userId);
    	 isAddOrRomove=AddOrRomoveState.ADD.code;
    	return "department-list";
    	
    }
    
    /**
     * 跳转到人员移除部门页面
     */
    
    public void prepareRemoveDepartmentToUsers() throws Exception {
    	entity = userInfoManager.getUserInfoById(userId);
	} 
    public String removeDepartmentToUsers()throws Exception{
    	
    	pageUserToDepart = userManager.userToRomoveDepartmentList(pageUserToDepart, null, userId);
    	isAddOrRomove=AddOrRomoveState.ROMOVE.code;
    	return "department-list";
    }

	/**
	 * 给用户分配部门
	 */
	public String addDepartmentToUser() {
		userManager.addDepartmentToUser(userId, departmentIds,isAddOrRomove);
		addActionMessage(getText("user.addDepartmentSuccess"));
		return RELOAD;
	}
	  /**
     * 跳转到人员添加工作组页面
     */
    public void prepareAddWorkGroupToUsers() throws Exception {
    	entity = userInfoManager.getUserInfoById(userId);
	} 
    
    public String addWorkGroupToUsers()throws Exception{
    	
    	pageUserToWork = userManager.getWorkGroupList(pageUserToWork);
    	checkedWorkGroupIds = userManager.getCheckedWorkGroupIds(userId);
    	isAddOrRomove=AddOrRomoveState.ADD.code; 
    	return "work-group-list";
    }
    
    /**
     * 跳转人员移除工作组页面
     */
    
    public void prepareRemoveWorkGroupToUsers() throws Exception {
    	entity = userInfoManager.getUserInfoById(userId);
	} 
    public String removeWorkGroupToUsers()throws Exception{   
    	
    	pageUserToWork = userManager.userToRomoveWorkGroupList(pageUserToWork, null, userId);
	    isAddOrRomove=AddOrRomoveState.ROMOVE.code;
    	return "work-group-list";
    }

	/**
	 * 给用户分配工作组
	 */
	public String addWorkGroupToUser() {
		userManager.addWorkGroupToUser(userId, workGroupIds,isAddOrRomove);
		addActionMessage(getText("user.addWorkGroupSuccess"));
		return RELOAD;
	}
	/**
	 * 同步LDAP的用户
	 * @return
	 * @throws Exception
	 */
	public String synchronous() throws Exception{
		synchronousLdapMessage = userInfoManager.synchronize();
		renderText(synchronousLdapMessage);
		ApiFactory.getBussinessLogService().log("用户管理", "同步Ldap",ContextUtils.getSystemId("acs"));
		return null;
	}
	
	public String validateLdapStart() throws Exception{
		boolean falg = userInfoManager.validateLdapStart();
		if(falg){
			renderText("true");
		}else{
			renderText("false");
		}
		return null;
	}

	public String checkLoginPassword() throws Exception {
		HttpServletRequest request = ServletActionContext.getRequest();
		String orgPassword = request.getParameter("orgPassword");
		boolean istrue = userInfoManager.checkLoginPassword(orgPassword);
		if(istrue){
			renderText("");
			return null;
		}
		   renderText(getText("user.rulesNotMatch"));
		   return null;
	}
	

	public String checkOldPassword() throws Exception{
		HttpServletRequest request = ServletActionContext.getRequest();
		String oldPassword = request.getParameter("oldPassword");
		Long id = Long.valueOf(request.getParameter("id"));
		User user = userManager.getUserById(id);
		if(oldPassword==null || "".equals(oldPassword.trim())){
			this.renderText("false");
		}else if(oldPassword.equals(user.getPassword())){
		//}else if(PasswordEncoder.encode(oldPassword).equals(user.getPassword())){
			this.renderText("true");
		}else{
			this.renderText("false");
		}
		return null;
	}
	
	public String updatePassword() throws Exception{
		User user = userManager.getUserById(id);
		String oldPassword = Md5.toMessageDigest(oraginalPassword);
		if(StringUtils.isNotBlank(oraginalPassword) && oldPassword.equals(user.getPassword())){
			user.getUserInfo().setPasswordUpdatedTime(new Date());
			user.setPassword(password);
			userManager.saveUser(user);
			renderText("");
		}else{
			renderText("old_pwd_error");
		}
		return null;
	}
	
	public  void overdueUnblock()throws Exception{
		userInfoManager.overdueUnblock(id);
	}
	public  void overdueblock()throws Exception{
		userInfoManager.overdueblock(id);
	}
	
	public String showImportUser() throws Exception{
		return "import-user";
	}
	
	public String importUser() throws Exception{
		String result = "";
		try {
			result = ApiFactory.getDataImporterService().importData(file, fileName,importUserManager);
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ParseException e) {
			e.printStackTrace();
		}
		renderText(result);
		return null;
	}
	
	public String exportUser() throws Exception{
		HttpServletResponse response = ServletActionContext.getResponse();
		response.reset();
		response.setContentType("application/x-download");
		response.addHeader("Content-Disposition", "attachment;filename="+URLEncoder.encode("用户信息.xls","UTF-8"));
		User user=userManager.getUserById(ContextUtils.getUserId());
		
		if(roleManager.hasSystemAdminRole(user)){//系统管理员
			List<Department> depts = departmentManager.getAllDepartment();
			ExportUserInfo.exportUser(response.getOutputStream(), depts, false);
		}else if(roleManager.hasBranchAdminRole(user)){//分支机构管理员
			List<Department> depts = new ArrayList<Department>();
			List<BranchAuthority> branches=branchAuthorityManager.getBranchByUser(ContextUtils.getUserId());
			for(BranchAuthority branch:branches){
				Department d=departmentManager.getDepartment(branch.getBranchesId());
				depts.add(d);
				userManager.getSubDepartment(d.getId(),depts);
			}
			ExportUserInfo.exportUser(response.getOutputStream(), depts, true);
		}
		ApiFactory.getBussinessLogService().log("用户管理", 
				"导出用户",ContextUtils.getSystemId("acs"));
		return null;
	}
	
	/**
	 * 更新用户缓存
	 * @return
	 * @throws Exception
	 */
	@Action("update-user-cache")
	public String updateUserCache() throws Exception{
		userManager.updateUsers();
		ApiFactory.getBussinessLogService().log("用户管理", 
				"更新用户缓存", 
				ContextUtils.getSystemId("acs"));
		return null;
	}
	/**
	 * 用户解锁
	 * @return
	 * @throws Exception
	 */
	public String unlockUser() throws Exception{
		String logSign="";//该字段只是为了标识日志信息：用户1，用户2，...
		if(StringUtils.isNotEmpty(ids)){
			String[] userids=ids.split(",");
			for(String userid:userids){
				user=userManager.getUserById(Long.valueOf(userid));
				if(StringUtils.isNotEmpty(logSign)){
					logSign+=",";
				}
				logSign+=user.getName();
			}
		}
		
		this.renderText(userManager.unlockUser(ids));
		ApiFactory.getBussinessLogService().log("用户管理", 
				"用户解锁:"+logSign,ContextUtils.getSystemId("acs"));
		return null;
	}
	/**
	 * 验证用户邮箱不能重复
	 * @return
	 */
	public String validateEamil()throws Exception{
		renderText(userManager.validateEmail(id,dscId,validateLoginName,fromBracnhId,branId));
		return null;
	}
	public String validateLoginNameRepeat()throws Exception{
		  renderText(userManager.validateLoginNameRepeat(userLoginName,Long.valueOf(chooseDepartmentId),isCrossBranch));
	  	  return null;
	}
	
	
	public String validateLoginNameRepeatByDepartIds()throws Exception{
		  renderText(userManager.validateLoginNameRepeatByDepartIds(userLoginName,chooseDepartmentIds,uusId));
	  	  return null;
	}
	
	@Override
	protected void prepareModel() throws Exception {
		if (id != null) {
			entity = userInfoManager.getUserInfoById(id);
		}else if(userId != null){
			user = userManager.getUserById(userId);
			entity = user.getUserInfo();
			id = entity.getId();
		}else {
			entity = new UserInfo();
			entity.setUser(new User());
		}
	}

	public UserInfo getModel() {
		return entity;
	}
	
	public User getUser() {
		return user;
	}
	
	public Long getUserInfoIds() {
		return userInfoIds;
	}

	public UserInfo getEntity() {
		return entity;
	}

	public List<Long> getCheckedWorkGroupIds() {
		return checkedWorkGroupIds;
	}

	public List<Long> getCheckedDepartmentIds() {
		return checkedDepartmentIds;
	}

	public Integer getIsAddOrRomove() {
		return isAddOrRomove;
	}

	public void setIsAddOrRomove(Integer isAddOrRomove) {
		this.isAddOrRomove = isAddOrRomove;
	}

	@Required
	public void setUserInfoManager(UserInfoManager userInfoManager) {
		this.userInfoManager = userInfoManager;
	}
	
	@Required
	public void setDepartmentManager(DepartmentManager departmentManager) {
		this.departmentManager = departmentManager;
	}
	@Required
	public void setCompanyManager(CompanyManager companyManager) {
		this.companyManager = companyManager;
	}
	public UserManager getUserManager() {
		return userManager;
	}

	@Required
	public void setUserManager(UserManager userManager) {
		this.userManager = userManager;
	}

	public void setCheckedWorkGroupIds(List<Long> checkedWorkGroupIds) {
		this.checkedWorkGroupIds = checkedWorkGroupIds;
	}

	public void setCheckedDepartmentIds(List<Long> checkedDepartmentIds) {
		this.checkedDepartmentIds = checkedDepartmentIds;
	}

	public List<UserInfo> getAllUser() {
		return allUser;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Long getId() {
		return id;
	}

	public Page<User> getPage() {
		return page;
	}

	public void setPage(Page<User> page) {
		this.page = page;
	}

	
	public Page<Workgroup> getPageUserToWork() {
		return pageUserToWork;
	}

	public void setPageUserToWork(Page<Workgroup> pageUserToWork) {
		this.pageUserToWork = pageUserToWork;
	}


	public List<Department> getAllDepartment() {
		return allDepartment;
	}

	public void setAllDepartment(List<Department> allDepartment) {
		this.allDepartment = allDepartment;
	}

	public List<Long> getDepartmentIds() {
		return departmentIds;
	}

	public void setDepartmentIds(List<Long> departmentIds) {
		this.departmentIds = departmentIds;
	}

	public void setUserInfoIds(Long userInfoIds) {
		this.userInfoIds = userInfoIds;
	}

	public List<Role> getAllRoles() {
		return allRoles;
	}
  
	public List<Workgroup> getAllWorkGroup() {
		return allWorkGroup;
	}

	public void setWorkGroupIds(List<Long> workGroupIds) {
		this.workGroupIds = workGroupIds;
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

	public void setEntity(UserInfo entity) {
		this.entity = entity;
	}

	
	public void prepareListRoles() throws Exception {
		entity = userInfoManager.getUserInfoById(userId);
	}
	
	public void prepareRemoveRoles() throws Exception {
		entity = userInfoManager.getUserInfoById(userId);
	}
	
	public Long getUserId() {
		return userId;
	}

	public void setUserId(Long userId) {
		this.userId = userId;
	}


	public Page<Department> getPageUserToDepart() {
		return pageUserToDepart;
	}

	public void setPageUserToDepart(Page<Department> pageUserToDepart) {
		this.pageUserToDepart = pageUserToDepart;
	}

	public String getUserByDepartment() throws Exception{
		containBranches=departmentManager.containBranches();
		if(departmentId != null){
			getUserByDepartmentDepartmentIdNoNullCanEditUser();
			if(page.getPageSize() <= 1){
				return SUCCESS; 
			}else{
				ApiFactory.getBussinessLogService().log("用户管理", 
						"查看用户列表",ContextUtils.getSystemId("acs"));
				Department department = departmentManager.getDepartmentById(departmentId);
				if(department.getBranch()){//分支机构
					userInfoManager.queryUsersByBranch(page, departmentId);
				}else if((!department.getBranch())
						&&(!department.getId().equals(department.getSubCompanyId()))
						&&department.getSubCompanyId()!=null){//分支机构下部门
					userInfoManager.queryUsersByBranchDepartment(page, departmentId);
				}else{
				    userInfoManager.queryUsersByDepartment(page, departmentId);
				}
				renderHtml(PageUtils.pageToJson(page));
				return null;
			}
			
		}else{
			search();
		}
		return SUCCESS;
	}
	
	private void getUserByDepartmentDepartmentIdNoNullCanEditUser(){
		Department department = departmentManager.getDepartmentById(departmentId);
		canEditUser = roleManager.hasSystemAdminRole(ContextUtils.getUserId());
		if(canEditUser) return;
		if(department.getBranch()){//分支机构
			hasBranchAdminRole(departmentId);
		}else if((!department.getBranch())
				&&(!department.getId().equals(department.getSubCompanyId()))
				&&department.getSubCompanyId()!=null){//分支机构下部门
			hasBranchAdminRole(department.getSubCompanyId());
		}
	}
	private void searchCanEditUser(){
		canEditUser = roleManager.hasSystemAdminRole(ContextUtils.getUserId());
		if(canEditUser)return;
		List<BranchAuthority> branchAuthoritys = branchAuthorityManager.getBranchByUser(ContextUtils.getUserId());
		if(!branchAuthoritys.isEmpty()){
			hasBranchAdminRole(branchAuthoritys.get(0).getBranchesId());
		}
		User u = userManager.getUserById(ContextUtils.getUserId()); 
		boolean securityAuditAdminable = (roleManager.hasSecurityAdminRole(u)||roleManager.hasAuditAdminRole(u));
		if(securityAuditAdminable)canEditUser = false;//如果兼职安全管理员但不兼职系统管理员则不能修改用户
	}
	
	
	public String getUserByCompany()throws Exception{
		canEditUser = roleManager.hasSystemAdminRole(ContextUtils.getUserId());
		containBranches=departmentManager.containBranches();
		if(page.getPageSize() <= 1){
			return SUCCESS; 
		}else{
			page = userInfoManager.queryUsersByCompany(page, companyManager.getCompanyId());
			renderHtml(PageUtils.pageToJson(page));
			return null;
		}
	}
	
	/**
     * 验证更换正职部门
     * @return
     * @throws Exception
     */
    @Action("user-changeMainDepartment")
    public String changeMainDepartment()throws Exception{
    	String result="ok";
    	String repeatLoginName="";
    	int i=0;
		for(String str:ids.split(",")){
			User u=userManager.getUserById(Long.valueOf(str));
			if(i==0){
				if(u.getSubCompanyId()==null){
					if(branchId==null){
						break;
					}
				}else{
					if(u.getSubCompanyId().equals(branchId)){
						break;
					}
				}
			}
			User newUser=userManager.getUserInBranchDepartment(u.getLoginName(), ContextUtils.getCompanyId(), branchId);
			User deleteUser=userManager.getDeleteUserInBranchDepartment(u.getLoginName(), ContextUtils.getCompanyId(), branchId);
			if(newUser != null||deleteUser!=null){
				if(StringUtils.isNotEmpty(repeatLoginName)){
					repeatLoginName+=",";
				}
				repeatLoginName+=u.getLoginName();
			}
			i++;
		}
    	if(StringUtils.isNotEmpty(repeatLoginName)){
    		result="选择的部门所属分支机构中已有登录名为："+repeatLoginName+"。请重新选择！";
    	}else{
    		for(String str:ids.split(",")){
        		User u=userManager.getUserById(Long.valueOf(str));
        		if(!userManager.sameBranches(branchId, u)){
        			result="no";
        			break;
        		}
        	}
    	}
    	this.renderText(result);
    	return null;
    }
	/**
     * 验证已删除添加部门
     * @return
     * @throws Exception
     */
    public String validateDeletedUserAdd(){
    	this.renderText(userInfoManager.validateDeletedUserAdd(ids,branchId,deptId));
    	return null;
    }
	/**
	 * 检测用户名是否注册
	 * @return
	 * @throws Exception
	 */
	public String checkUserName() throws Exception{
		HttpServletRequest request = ServletActionContext.getRequest();
		String userName = request.getParameter("userName");
		UserInfo ui = userInfoManager.checkUserName(userName);
		if(ui==null){
			this.renderText("true");
		}else{
			this.renderText(userName);
		}
		return null;
	}
	
	public String getUserByWorkGroup() throws Exception{
		look = "look";
		fromWorkgroup = "fromWorkgroup";
		if(page.getPageSize() <= 1){
			return SUCCESS; 
		}else{
			if(workGroupId != null){
				ApiFactory.getBussinessLogService().log("用户管理", 
						"查看用户列表",ContextUtils.getSystemId("acs"));
				page = userInfoManager.queryUsersByWorkGroup(page, workGroupId);
			}
			renderHtml(PageUtils.pageToJson(page));
			return null;
		}
	}
	
	private Long workGroupId;
	private Long departmentId;

	public Long getWorkGroupId() {
		return workGroupId;
	}

	public void setWorkGroupId(Long workGroupId) {
		this.workGroupId = workGroupId;
	}

	public Long getDepartmentId() {
		return departmentId;
	}

	public void setDepartmentId(Long departmentId) {
		this.departmentId = departmentId;
	}

	public String getPassWord_CreateTime() {
		return passWord_CreateTime;
	}

	public void setPassWord_CreateTime(String passWord_CreateTime) {
		this.passWord_CreateTime = passWord_CreateTime;
	}

	public List<Long> getPassWordOverdueIds() {
		return passWordOverdueIds;
	}

	public void setPassWordOverdueIds(List<Long> passWordOverdueIds) {
		this.passWordOverdueIds = passWordOverdueIds;
	}

	public Map<Long, Integer> getPasswordOverNoticeId() {
		return passwordOverNoticeId;
	}

	public void setPasswordOverNoticeId(Map<Long, Integer> passwordOverNoticeId) {
		this.passwordOverNoticeId = passwordOverNoticeId;
	}

	public String getFlag() {
		return flag;
	}

	public void setFlag(String flag) {
		this.flag = flag;
	}
	
	public List<BusinessSystem> getSystems() {
		return systems;
	}
	
	public void setSystems(List<BusinessSystem> systems) {
		this.systems = systems;
	}
	
	public String getRedirectUrl() {
		return redirectUrl;
	}

	public void setRedirectUrl(String redirectUrl) {
		this.redirectUrl = redirectUrl;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	@Required
	public void setBusinessSystemManager(BusinessSystemManager businessSystemManager) {
		this.businessSystemManager = businessSystemManager;
	}

	public SubsciberManager getSubsciberManager() {
		return subsciberManager;
	}
	@Required
	public void setSubsciberManager(SubsciberManager subsciberManager) {
		this.subsciberManager = subsciberManager;
	}

	
	public String getStates() {
		return states;
	}

	public void setStates(String states) {
		this.states = states;
	}

	public Long getCompanyId() {
		return ContextUtils.getCompanyId();
	}

	public void setCompanyId(Long companyId) {
		this.companyId = companyId;
	}

	public void setHistoryUserName(String historyUserName) {
		this.historyUserName = historyUserName;
	}

	public String getSynchronousLdapMessage() {
		return synchronousLdapMessage;
	}

	public void setSynchronousLdapMessage(String synchronousLdapMessage) {
		this.synchronousLdapMessage = synchronousLdapMessage;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public List<UserInfo> getUserInfos() {
		return userInfos;
	}

	public void setUserInfos(List<UserInfo> userInfos) {
		this.userInfos = userInfos;
	}

	public String getDepartmentName() {
		return departmentName;
	}

	public void setDepartmentName(String departmentName) {
		this.departmentName = departmentName;
	}

	public String getDids() {
		return dids;
	}

	public void setDids(String dids) {
		this.dids = dids;
	}


	public String getDeId() {
		return deId;
	}

	public void setDeId(String deId) {
		this.deId = deId;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getDepIds() {
		return depIds;
	}

	public void setDepIds(String depIds) {
		this.depIds = depIds;
	}

	public String getUsersId() {
		return usersId;
	}


	public String getLookId() {
		return lookId;
	}

	public void setLookId(String lookId) {
		this.lookId = lookId;
	}

	public String getLook() {
		return look;
	}

	public void setLook(String look) {
		this.look = look;
	}

	public void setUsersId(String usersId) {
		this.usersId = usersId;
	}

	public String getMode() {
		return mode;
	}

	public void setMode(String mode) {
		this.mode = mode;
	}

	public String getLooked() {
		return looked;
	}

	public void setLooked(String looked) {
		this.looked = looked;
	}

	public String getOldDid() {
		return oldDid;
	}

	public void setOldDid(String oldDid) {
		this.oldDid = oldDid;
	}

	public String getOldType() {
		return oldType;
	}

	public void setOldType(String oldType) {
		this.oldType = oldType;
	}

	public String getOlDid() {
		return olDid;
	}

	public void setOlDid(String olDid) {
		this.olDid = olDid;
	}

	public String getOlType() {
		return olType;
	}

	public void setOlType(String olType) {
		this.olType = olType;
	}

	public String getPassWordChange() {
		return passWordChange;
	}

	public void setPassWordChange(String passWordChange) {
		this.passWordChange = passWordChange;
	}

	public String getDepartmId() {
		return departmId;
	}

	public void setDepartmId(String departmId) {
		this.departmId = departmId;
	}

	public String getDepartmType() {
		return departmType;
	}

	public void setDepartmType(String departmType) {
		this.departmType = departmType;
	}

	public String getEdit() {
		return edit;
	}

	public void setEdit(String edit) {
		this.edit = edit;
	}

	public String getEdited() {
		return edited;
	}

	public void setEdited(String edited) {
		this.edited = edited;
	}
	public void setOraginalPassword(String oraginalPassword) {
		this.oraginalPassword = oraginalPassword;
	}
	public String getOraginalPassword() {
		return oraginalPassword;
	}
	public Boolean getIsPasswordChange() {
		return isPasswordChange;
	}
	public void setLevelpassword(String levelpassword) {
		this.levelpassword = levelpassword;
	}
	public void setFile(File file) {
		this.file = file;
	}
	
	public void setFileFileName(String fileName) {
		this.fileName = fileName;
	}

	public String getOneDid() {
		return oneDid;
	}

	public void setOneDid(String oneDid) {
		this.oneDid = oneDid;
	}

	public String getMainDepartmentName() {
		return mainDepartmentName;
	}

	public void setMainDepartmentName(String mainDepartmentName) {
		this.mainDepartmentName = mainDepartmentName;
	}

	public String getIds() {
		return ids;
	}

	public void setIds(String ids) {
		this.ids = ids;
	}

	public String getComy() {
		return comy;
	}

	public void setComy(String comy) {
		this.comy = comy;
	}

	public String getFromWorkgroup() {
		return fromWorkgroup;
	}

	public void setFromWorkgroup(String fromWorkgroup) {
		this.fromWorkgroup = fromWorkgroup;
	}

	public String getFromChangeMainDepartment() {
		return fromChangeMainDepartment;
	}

	public void setFromChangeMainDepartment(String fromChangeMainDepartment) {
		this.fromChangeMainDepartment = fromChangeMainDepartment;
	}

	public Long getNewMainDepartmentId() {
		return newMainDepartmentId;
	}

	public void setNewMainDepartmentId(Long newMainDepartmentId) {
		this.newMainDepartmentId = newMainDepartmentId;
	}

	public String getUserEmail() {
		return userEmail;
	}

	public void setUserEmail(String userEmail) {
		this.userEmail = userEmail;
	}

	public Long getBranchId() {
		return branchId;
	}

	public void setBranchId(Long branchId) {
		this.branchId = branchId;
	}

	public String getMainDepartmentSubCompanyId() {
		return mainDepartmentSubCompanyId;
	}

	public void setMainDepartmentSubCompanyId(String mainDepartmentSubCompanyId) {
		this.mainDepartmentSubCompanyId = mainDepartmentSubCompanyId;
	}

	public String getDscId() {
		return dscId;
	}
	public void setDscId(String dscId) {
		this.dscId = dscId;
	}

	public String getValidateLoginName() {
		return validateLoginName;
	}

	public void setValidateLoginName(String validateLoginName) {
		this.validateLoginName = validateLoginName;
	}

	public String getUserLoginName() {
		return userLoginName;
	}

	public void setUserLoginName(String userLoginName) {
		this.userLoginName = userLoginName;
	}

	public String getChooseDepartmentId() {
		return chooseDepartmentId;
	}

	public void setChooseDepartmentId(String chooseDepartmentId) {
		this.chooseDepartmentId = chooseDepartmentId;
	}

	public String getIsCrossBranch() {
		return isCrossBranch;
	}

	public void setIsCrossBranch(String isCrossBranch) {
		this.isCrossBranch = isCrossBranch;
	}

	public String getBranId() {
		return branId;
	}

	public void setBranId(String branId) {
		this.branId = branId;
	}

	public Long getDeptId() {
		return deptId;
	}

	public void setDeptId(Long deptId) {
		this.deptId = deptId;
	}

	public Long getFromBracnhId() {
		return fromBracnhId;
	}

	public void setFromBracnhId(Long fromBracnhId) {
		this.fromBracnhId = fromBracnhId;
	}

	public String getChooseDepartmentIds() {
		return chooseDepartmentIds;
	}

	public void setChooseDepartmentIds(String chooseDepartmentIds) {
		this.chooseDepartmentIds = chooseDepartmentIds;
	}

	public Long getUusId() {
		return uusId;
	}

	public void setUusId(Long uusId) {
		this.uusId = uusId;
	}

	public boolean isCanEditUser() {
		return canEditUser;
	}

	public Long getDid() {
		return did;
	}

	public void setDid(Long did) {
		this.did = did;
	}
	
	public Boolean getContainBranches() {
		return containBranches;
	}
}
