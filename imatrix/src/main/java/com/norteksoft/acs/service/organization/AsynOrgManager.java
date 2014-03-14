package com.norteksoft.acs.service.organization;

import java.util.Date;
import java.util.List;
import java.util.Set;

import javax.ws.rs.core.Response;

import org.apache.commons.lang.StringUtils;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.norteksoft.acs.base.orm.hibernate.SimpleHibernateTemplate;
import com.norteksoft.acs.entity.authorization.BranchAuthority;
import com.norteksoft.acs.entity.authorization.Role;
import com.norteksoft.acs.entity.authorization.RoleDepartment;
import com.norteksoft.acs.entity.organization.Company;
import com.norteksoft.acs.entity.organization.Department;
import com.norteksoft.acs.entity.organization.DepartmentUser;
import com.norteksoft.acs.entity.organization.User;
import com.norteksoft.acs.entity.organization.UserInfo;
import com.norteksoft.acs.entity.organization.Workgroup;
import com.norteksoft.acs.entity.organization.WorkgroupUser;
import com.norteksoft.acs.service.AcsUtils;
import com.norteksoft.acs.service.authorization.BranchAuthorityManager;
import com.norteksoft.acs.service.authorization.RoleManager;
import com.norteksoft.product.util.ContextUtils;
import com.norteksoft.product.util.ParameterUtils;
import com.norteksoft.product.util.ThreadParameters;
/**
 * 同步组织机构使用的service
 *
 */

@Service
@Transactional
public class AsynOrgManager {
	private SimpleHibernateTemplate<Company, Long> companyDao;
	private SimpleHibernateTemplate<User, Long> userDao;
	private SimpleHibernateTemplate<UserInfo, Long> userInfoDao;
	private SimpleHibernateTemplate<DepartmentUser, Long> departmentToUserDao;
	private SimpleHibernateTemplate<WorkgroupUser, Long> workGroupToUserDao;
	private SimpleHibernateTemplate<Department, Long> departmentDao;
	private SimpleHibernateTemplate<RoleDepartment, Long> roleDepartmentDao;
	private static String USERID = "user.id";

	@Autowired
	private AcsUtils acsUtils;
	@Autowired
	private UserInfoManager userInfoManager;
	@Autowired
	private UserManager userManager;
	@Autowired
	private DepartmentManager departmentManager;
	@Autowired
	private CompanyManager companyManager;
	@Autowired
	private WorkGroupManager workGroupManager;
	@Autowired
	private RoleManager roleManager;
	@Autowired
	private BranchAuthorityManager branchAuthorityManager;
	public Long getSystemIdByCode(String code) {
	   return acsUtils.getSystemsByCode(code).getId();
	}
	
	@Autowired
	public void setSessionFactory(SessionFactory sessionFactory){
		userDao = new SimpleHibernateTemplate<User, Long>(sessionFactory, User.class);
		workGroupToUserDao = new SimpleHibernateTemplate<WorkgroupUser, Long>(sessionFactory, WorkgroupUser.class);
		departmentToUserDao = new SimpleHibernateTemplate<DepartmentUser, Long>(sessionFactory, DepartmentUser.class);
		departmentDao = new SimpleHibernateTemplate<Department, Long>(sessionFactory, Department.class);
		userInfoDao = new SimpleHibernateTemplate<UserInfo, Long>(sessionFactory, UserInfo.class);
		roleDepartmentDao=new SimpleHibernateTemplate<RoleDepartment, Long>(sessionFactory,RoleDepartment.class);
		companyDao=new SimpleHibernateTemplate<Company,Long>(sessionFactory,Company.class);
	}
	@Transactional
	public Response saveUserForWebService(String path,String loginName, String name,
			String password) {
		if(StringUtils.isEmpty(path)){
			return Response.status(500).entity("path必填!").build();
		}
		if(StringUtils.isEmpty(loginName)){
			return Response.status(500).entity("登陆名必填!").build();
		}
		if(StringUtils.isEmpty(name)){
			return Response.status(500).entity("用户名必填!").build();
		}
		if(StringUtils.isEmpty(password)){
			return Response.status(500).entity("密码必填!").build();
		}
		Object o = parsePath(path);
		if(o!=null){
			if(o instanceof Company){
				Company company=(Company)o;
				return saveUserToCompany(company,loginName,name,password,null);
			}else if(o instanceof Department){
				Department department=(Department)o;
				if(department.getBranch()){
					return saveUserToBranch(department,loginName,name,password,null);
				}else{
					if(department.getSubCompanyId()==null){
						return saveUserToCompany(department.getCompany(),loginName,name,password,department);
					}else{
						return saveUserToBranch(departmentDao.get(department.getSubCompanyId()),loginName,name,password,department);
					}
				}
				
			}
		}else{
			return Response.status(500).entity("path错误!").build();
		}
		return Response.status(200).entity("ok").build();
	}

	private Response saveUserToBranch(Department branch, String loginName,
			String name, String password, Department object) {
		List<User> us=userDao.findList("select user from User user join user.userInfos ui where ui.companyId=? and  ui.deleted=? and user.deleted=? and user.subCompanyId=? and user.loginName=?", branch.getCompany().getId(),true,true,branch.getId(),loginName);
		if(us!=null&&us.size()>0){
			return Response.status(500).entity("已存在登录名是相同的已删除用户!").build();
		}
		List<User> users=userDao.findList("from User u where u.deleted=? and u.companyId=? and u.subCompanyId=? and u.loginName=?", false,branch.getCompany().getId(),branch.getId(),loginName);
		User user=null;
		if(users!=null&&users.size()>0){
			user=users.get(0);
			departmentToUserDao.createQuery("delete from DepartmentUser du where du.user.id=?",user.getId()).executeUpdate();
			if(object!=null){
				user.setMainDepartmentId(object.getId());
				DepartmentUser du=new DepartmentUser();
				du.setUser(user);
				du.setDepartment(object);
				du.setSubCompanyId(user.getSubCompanyId());
				du.setCompanyId(branch.getCompany().getId());
				departmentToUserDao.save(du);
			}else{
				DepartmentUser du=new DepartmentUser();
				du.setUser(user);
				du.setDepartment(branch);
				du.setCompanyId(branch.getCompany().getId());
				du.setSubCompanyId(branch.getId());
				departmentToUserDao.save(du);
			}
			user.setName(name);
			user.setPassword(password);
			userDao.save(user);
		}else{
			ThreadParameters parameters = new ThreadParameters(branch.getCompany().getId());
			ParameterUtils.setParameters(parameters);
			UserInfo userInfo=null;
			//新建用户
			user=new User();
			user.setLoginName(loginName);
			user.setName(name);
			user.setPassword(password);
			user.setCompanyId(branch.getCompany().getId());
			user.setSubCompanyId(branch.getId());
			user.setSubCompanyName(branch.getName());
			userManager.saveUser(user);
			if(object!=null){
				user.setMainDepartmentId(object.getId());
				DepartmentUser du=new DepartmentUser();
				du.setUser(user);
				du.setDepartment(object);
				du.setCompanyId(branch.getCompany().getId());
				du.setSubCompanyId(branch.getId());
				departmentToUserDao.save(du);
			}else{
				DepartmentUser du=new DepartmentUser();
				du.setUser(user);
				du.setDepartment(branch);
				du.setCompanyId(branch.getCompany().getId());
				du.setSubCompanyId(branch.getId());
				departmentToUserDao.save(du);
			}
			//给用户添加基本的权限
			userInfoManager.giveNewUserPortalCommonRole(user);
			userInfo=new UserInfo();
			userInfo.setUser(user);
			userInfo.setPasswordUpdatedTime(new Date());
			userInfoManager.add(userInfo);
		}
		return Response.status(200).entity("ok").build();
	}
	private Response saveUserToCompany(Company company, String loginName,String name,String password,Department department) {
		User user=null;
		List<User> us=userDao.findList("select user from User user join user.userInfos ui where ui.companyId=? and  ui.deleted=? and user.deleted=? and user.subCompanyId is null and user.loginName=?", company.getId(),true,true,loginName);
		if(us!=null&&us.size()>0){
			return Response.status(500).entity("已存在登录名是相同的已删除用户!").build();
		}
		List<User> users = userDao.findList("from User u where u.companyId=? and u.deleted=? and u.subCompanyId is null and loginName=?",company.getId(),false,loginName);
		if(users!=null&&users.size()>0){
			user=users.get(0);
			departmentToUserDao.createQuery("delete from DepartmentUser du where du.user.id=?",user.getId()).executeUpdate();
			if(department!=null){
				user.setMainDepartmentId(department.getId());
				DepartmentUser du=new DepartmentUser();
				du.setUser(user);
				du.setDepartment(department);
				du.setSubCompanyId(department.getSubCompanyId());
				du.setCompanyId(company.getId());
				departmentToUserDao.save(du);
			}
			user.setName(name);
			user.setPassword(password);
			userDao.save(user);
		}else{
			ThreadParameters parameters = new ThreadParameters(company.getId());
			ParameterUtils.setParameters(parameters);
			UserInfo userInfo=null;
			//新建用户
			user=new User();
			user.setLoginName(loginName);
			user.setName(name);
			user.setPassword(password);
			user.setCompanyId(company.getId());
			user.setSubCompanyId(null);
			user.setSubCompanyName(company.getName());
			userManager.saveUser(user);
			if(department!=null){
				user.setMainDepartmentId(department.getId());
				DepartmentUser du=new DepartmentUser();
				du.setUser(user);
				du.setDepartment(department);
				du.setSubCompanyId(department.getSubCompanyId());
				du.setCompanyId(company.getId());
				departmentToUserDao.save(du);
			}
			//给用户添加基本的权限
			userInfoManager.giveNewUserPortalCommonRole(user);
			userInfo=new UserInfo();
			userInfo.setUser(user);
			userInfo.setPasswordUpdatedTime(new Date());
			userInfoManager.add(userInfo);
		}
		return Response.status(200).entity("ok").build();
	}

	@Transactional
	public Response saveDepartmentForWebService(String path,Boolean branchFlag, String name,
			String code) {
		if(StringUtils.isEmpty(path)){
			return Response.status(500).entity("path必填!").build();
		}
		if(StringUtils.isEmpty(name)){
			return Response.status(500).entity("部门名称必填!").build();
		}
		Object o = parsePath(path);
		if(o!=null){
			return addDepartment(o,branchFlag,name,code);
		}else{
			return Response.status(500).entity("path错误!").build();
		}
	}
	private Response addDepartment(Object o, Boolean branchFlag, String name,
			String code) {
		Department department=null;
		if(o instanceof Company){
			Company company=(Company)o;
			Long count = departmentDao.findLong("select count(*) from Department d where d.deleted=? and d.company.id=? and d.subCompanyId is null and d.name=?",false, company.getId(),name);
			if(count>0){
				return Response.status(500).entity("该部门已存在!").build();
			}
			ThreadParameters parameters = new ThreadParameters(company.getId());
			ParameterUtils.setParameters(parameters);
			department = new Department();
			department.setCode(departmentManager.createDepartmentCode());
			department.setCompany(company);
			department.setName(name);
			department.setBranch(branchFlag==null?false:branchFlag);
			
		}else if(o instanceof Department){
			Department dept=(Department)o;
			Long count = departmentDao.findLong("select count(*) from Department d where d.deleted=? and d.company.id=? and d.parent.id=? and d.name=?",false,dept.getCompany().getId(),dept.getId(),name);
			if(count>0){
				return Response.status(500).entity("该部门已存在!").build();
			}
			ThreadParameters parameters = new ThreadParameters(dept.getCompany().getId());
			ParameterUtils.setParameters(parameters);
			department = new Department();
			department.setCode(departmentManager.createDepartmentCode());
			department.setCompany(dept.getCompany());
			department.setName(name);
			department.setParent(dept);
			department.setSubCompanyId(dept.getSubCompanyId());
			department.setSubCompanyName(dept.getSubCompanyName());
			department.setBranch(branchFlag==null?false:branchFlag);
		}
		departmentDao.save(department);
		return Response.status(200).entity("ok").build();
	}

	@Transactional
	public Response deleteDepartmentForWebService(String path,String code) {
		Object o = parsePath(path);
		if(o!=null){
			if(o instanceof Company){
				return Response.status(500).entity("路径错误!").build();
			}else if(o instanceof Department){
				Department department=(Department)o;
				ThreadParameters parameters = new ThreadParameters(department.getCompany().getId());
				ParameterUtils.setParameters(parameters);
				if(department.getBranch()){
					if(validateBranchDelete(department.getId())){
						departmentDao.delete(department);
						return Response.status(200).entity("ok").build();
					}else{
						return Response.status(500).entity("请先删除分支机构下的部门,人员,工作组,分支机构,分支机构授权管理,角色!").build();
					}
				}else{
					if(validateDepartmentDelete(department.getId())){
						List<User> users=userManager.getUsersByDeptId(department.getId());
						for(User user:users){
							departmentToUserDao.createQuery("delete from DepartmentUser du where du.user.id=? and du.department.id=?",user.getId(),department.getId()).executeUpdate();
							List<DepartmentUser> departmentToUser = departmentToUserDao.findList("from DepartmentUser du where  du.deleted=? and du.user.id=?",false,user.getId());
							if(departmentToUser==null||departmentToUser.size()==0){
								if(department.getSubCompanyId()!=null){
									DepartmentUser du=new DepartmentUser();
									du.setUser(user);
									du.setDepartment(departmentDao.get(user.getSubCompanyId()));
									du.setCompanyId(user.getCompanyId());
									du.setSubCompanyId(user.getSubCompanyId());
									departmentToUserDao.save(du);
								}
							}
							if(user.getMainDepartmentId().equals(department.getId())){
								user.setMailboxDeploy(null);
							}
						}
						departmentDao.delete(department);
						return Response.status(200).entity("ok").build();
					}else{
						return Response.status(500).entity("请先删除子部门或分支机构!").build();
					}
				}
			}else{
				return Response.status(500).entity("未知错误!").build();
			}
		}else{
			return Response.status(500).entity("路径错误!").build();
		}
	}
	@Transactional
	public Response deleteUserForWebService(String path,String loginName) {
		if(StringUtils.isEmpty(path)){
			return Response.status(500).entity("path必填!").build();
		}
		if(StringUtils.isEmpty(loginName)){
			return Response.status(500).entity("登陆名必填!").build();
		}
		Object o = parsePath(path);
		if(o!=null){
			return deleteUser(o,loginName);
		}else{
			return Response.status(500).entity("path错误!").build();
		}
	}
	private Response deleteUser(Object o,String loginName) {
		List<User> users=null;
		if(o instanceof Company){
			Company company=(Company)o;
			users=userDao.findList("from User u where u.companyId=? and u.deleted=? and u.subCompanyId is null and u.loginName=?",company.getId(),false,loginName);
			if(users!=null&&users.size()>0){
				User user=users.get(0);
				clearUser(user.getId());
				user.setDeleted(true);
				return Response.status(200).entity("ok").build();
			}else{
				return Response.status(500).entity("用户不存在!").build();
			}
		}else if(o instanceof Department){
			Department department=(Department)o;
			
			if(department.getSubCompanyId()==null){
				users=userDao.findList("from User u where u.companyId=? and u.deleted=? and u.subCompanyId is null and u.loginName=?",department.getCompany().getId(),false,loginName);
			}else{
				users=userDao.findList("from User u where u.companyId=? and u.deleted=? and u.companyId=? and u.subCompanyId=? and u.loginName=?",department.getCompany().getId(),false,department.getCompany().getId(),department.getSubCompanyId(),loginName);
			}
			if(users!=null&&users.size()>0){
				User user=users.get(0);
				clearUser(user.getId());
				user.setDeleted(true);
				return Response.status(200).entity("ok").build();
			}else{
				return Response.status(500).entity("用户不存在!").build();
			}
			
		}else{
			return Response.status(500).entity("用户不存在!").build();
		}
		
	}
	/**
	 * 解析路径     --测试公司\部门1\分支机构1-3\部门1
	 * @param path
	 * @return
	 */
	private Object parsePath(String path) {
		if(StringUtils.isEmpty(path)){
			return null;
		}
		String[] names=path.split("\\\\");
		List<Company> companys=companyDao.findList("from Company c where c.deleted=? and c.name=?", false,names[0]);
		for(Company company:companys){
			if(1==names.length){
				return company;
			}
			Object o=findDepartment(company,null,names,1);
			if(o!=null){
				return o;
			}
		}
		return null;
	}

	private Object findDepartment(Company company,Department dept,String[] names, int i) {
		Object obj=null;
		if(i>names.length-1){  
			return null;
		}
		StringBuilder hql=new StringBuilder("from Department d where d.deleted=? and d.name=? and d.company.id=? ");
		if(dept!=null){
			hql.append("and d.parent.id="+dept.getId().toString());
		}else{
			hql.append("and d.parent is null");
		}
		List<Department> departments=departmentDao.findList(hql.toString(),false,names[i],company.getId());
		for(Department department:departments){
			if(i==names.length-1){
				obj=department;
			}else{
				obj=findDepartment(company,department,names,++i);
			}
		}
		return obj;
	}

	private void clearUser(Long id) {
		User user=userManager.getUserById(id);
		UserInfo userInfo = user.getUserInfo();
		userInfo.setDeleted(true);
		departmentToUserDao.createQuery("delete from DepartmentUser du where du.user.id=?",id).executeUpdate();
		workGroupToUserDao.createQuery("delete from WorkgroupUser du where du.user.id=?",id).executeUpdate();
		userInfo.setDeleted(true);
		userInfoDao.save(userInfo);
		userDao.save(user);
	}
	
	
	private void deleteDepartmemtToUser(Long id, Long id2) {
		List<DepartmentUser> departmentToUser = departmentToUserDao.findByCriteria(Restrictions.eq("department.id", id),Restrictions.eq(USERID, id2));
		for (DepartmentUser departmentToUser2 : departmentToUser) {
			departmentToUserDao.delete(departmentToUser2);
		}
		
	}
	/**
	 * webService版
	 * @param departmentId2
	 * @param company
	 */
	private void cleanDept(Long departmentId2) {
		Department dept=(Department)departmentDao.findUnique("from Department d where d.company.id=? and d.id=? and d.deleted=? ",ContextUtils.getCompanyId(), departmentId2,false);
		Set<RoleDepartment> roleDepartments=dept.getRoleDepartments();
		if(!roleDepartments.isEmpty()){
			for(RoleDepartment roleDepartment:roleDepartments){
				roleDepartmentDao.delete(roleDepartment);
			}
		}
	}
	
	private void deleteDepartment(Long id) {
		Department dept=departmentDao.get(id);
		departmentToUserDao.createQuery("delete from DepartmentUser du where du.deleted=? and du.department.id=?", false,id).executeUpdate();
		departmentDao.delete(dept);
	}
	private boolean validateDepartmentDelete(Long deptId){
		List<Department> subDepartments=departmentManager.getSubDeptments(deptId);
		List<Workgroup> workgroups=workGroupManager.getWorkgroupsByBranch(deptId);
		List<BranchAuthority> branchAuthoritys=branchAuthorityManager.getBranchAuthorityByBranch(deptId);
		List<Role> roles=roleManager.getRoleByBranches(deptId);
		if((subDepartments!=null && subDepartments.size()>0) || (workgroups!=null && workgroups.size()>0) || (branchAuthoritys!=null&&branchAuthoritys.size()>0) || (roles!=null&&roles.size()>0)){
			return false;
		}else{
			return true;
		}
	}
	private boolean validateBranchDelete(Long deptId){
		List<Department> subDepartments=departmentManager.getSubDeptments(deptId);
		List<User> users=userManager.getUsersBySubCompany(deptId);
		List<Workgroup> workgroups=workGroupManager.getWorkgroupsByBranch(deptId);
		List<BranchAuthority> branchAuthoritys=branchAuthorityManager.getBranchAuthorityByBranch(deptId);
		List<Role> roles=roleManager.getRoleByBranches(deptId);
		if((subDepartments!=null && subDepartments.size()>0) || (users!=null&& users.size()>0) || (workgroups!=null && workgroups.size()>0) || (branchAuthoritys!=null&&branchAuthoritys.size()>0) || (roles!=null&&roles.size()>0)){
			return false;
		}else{
			return true;
		}
	}
	private Department getDepartmentByCode(String code) {
		List<Department> depts=departmentDao.findList("from Department d where d.code=? and d.deleted=? and d.company.id=?", code,false,ContextUtils.getCompanyId());
		if(depts.size()>0){
			return depts.get(0);
		}
		return null;
	}

				
}