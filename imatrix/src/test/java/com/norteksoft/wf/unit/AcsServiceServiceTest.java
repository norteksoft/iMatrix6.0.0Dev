package com.norteksoft.wf.unit;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import junit.framework.Assert;

import org.hibernate.SessionFactory;
import org.junit.Test;
import org.unitils.database.annotations.Transactional;
import org.unitils.database.util.TransactionMode;
import org.unitils.spring.annotation.SpringApplicationContext;
import org.unitils.spring.annotation.SpringBeanByName;

import com.norteksoft.acs.base.orm.hibernate.SimpleHibernateTemplate;
import com.norteksoft.acs.entity.authorization.BusinessSystem;
import com.norteksoft.acs.entity.authorization.RoleUser;
import com.norteksoft.acs.entity.log.LoginLog;
import com.norteksoft.acs.entity.organization.Company;
import com.norteksoft.acs.entity.organization.DepartmentUser;
import com.norteksoft.acs.entity.organization.UserInfo;
import com.norteksoft.acs.entity.organization.WorkgroupUser;
import com.norteksoft.product.api.ApiFactory;
import com.norteksoft.product.api.entity.Department;
import com.norteksoft.product.api.entity.Role;
import com.norteksoft.product.api.entity.User;
import com.norteksoft.product.api.entity.Workgroup;
import com.norteksoft.product.api.utils.BeanUtil;
import com.norteksoft.product.util.ContextUtils;
import com.norteksoft.product.util.ParameterUtils;
import com.norteksoft.product.util.ThreadParameters;


@Transactional(TransactionMode.ROLLBACK)
@SpringApplicationContext({"applicationContext-test.xml","applicationContext.xml","applicationContext-memcache.xml"})
public class AcsServiceServiceTest extends BaseWorkflowTestCase {
	
	private SimpleHibernateTemplate<LoginLog, Long> logDao;
	private SimpleHibernateTemplate<BusinessSystem, Long> businessSystemDao;
	private SimpleHibernateTemplate<com.norteksoft.acs.entity.authorization.Role, Long> roleDao;
	private SimpleHibernateTemplate<DepartmentUser, Long> departmentUserDao;
	private SimpleHibernateTemplate<RoleUser, Long> roleUserDao;
	private SimpleHibernateTemplate<WorkgroupUser, Long> workgroupUserDao;
	private SimpleHibernateTemplate<UserInfo, Long> userInfoDao;
	private SimpleHibernateTemplate<com.norteksoft.acs.entity.organization.User, Long> userDao;
	private SimpleHibernateTemplate<Company, Long> companyDao;
	private SimpleHibernateTemplate<com.norteksoft.acs.entity.organization.Department, Long> departmentDao;
	private SimpleHibernateTemplate<com.norteksoft.acs.entity.organization.Workgroup, Long> workgroupDao;
	@SpringBeanByName
	public void setSessionFactory(SessionFactory sessionFactory){
		logDao = new SimpleHibernateTemplate<LoginLog, Long>(sessionFactory, LoginLog.class);
		businessSystemDao = new SimpleHibernateTemplate<BusinessSystem, Long>(sessionFactory, BusinessSystem.class);
		roleDao = new SimpleHibernateTemplate<com.norteksoft.acs.entity.authorization.Role, Long>(sessionFactory, com.norteksoft.acs.entity.authorization.Role.class);
		departmentUserDao = new SimpleHibernateTemplate<DepartmentUser, Long>(sessionFactory, DepartmentUser.class);
		roleUserDao = new SimpleHibernateTemplate<RoleUser, Long>(sessionFactory, RoleUser.class);
		userInfoDao = new SimpleHibernateTemplate<UserInfo, Long>(sessionFactory, UserInfo.class);
		workgroupUserDao = new SimpleHibernateTemplate<WorkgroupUser, Long>(sessionFactory, WorkgroupUser.class);
		userDao = new SimpleHibernateTemplate<com.norteksoft.acs.entity.organization.User, Long>(sessionFactory, com.norteksoft.acs.entity.organization.User.class);
		companyDao = new SimpleHibernateTemplate<Company, Long>(sessionFactory, Company.class);
		departmentDao = new SimpleHibernateTemplate<com.norteksoft.acs.entity.organization.Department, Long>(sessionFactory, com.norteksoft.acs.entity.organization.Department.class);
		workgroupDao = new SimpleHibernateTemplate<com.norteksoft.acs.entity.organization.Workgroup, Long>(sessionFactory, com.norteksoft.acs.entity.organization.Workgroup.class);
	}
	
	@Test
	public void getOnlineUserCount(){
		LoginLog log = new LoginLog();
		log.setCompanyId(1L);
		log.setDeleted(false);
		log.setExitTime(null);
		logDao.save(log);
		
		Long result = ApiFactory.getAcsService().getOnlineUserCount();
		Assert.assertTrue(result==1L);
	}
	
	@Test
	public void getOnlineUserIds(){
		User user = new User();
		user.setLoginName("wangjing");
		user.setCompanyId(1L);
		user.setDeleted(false);
		ApiFactory.getAcsService().saveUser(user);
		
		LoginLog log = new LoginLog();
		log.setCompanyId(1L);
		log.setDeleted(false);
		log.setExitTime(null);
		log.setUserId(ApiFactory.getAcsService().getUserByLoginName("wangjing").getId());
		logDao.save(log);
		
		List<Long> result = ApiFactory.getAcsService().getOnlineUserIds();
		Assert.assertNotNull(result);
	}
	
	@Test
	public void getDepartments(){
		com.norteksoft.acs.entity.organization.Department department = new com.norteksoft.acs.entity.organization.Department();
		department.setShortTitle("ss");
		department.setCompany(companyDao.get(1L));
		departmentDao.save(department);
		
		com.norteksoft.acs.entity.organization.Department department2 = new com.norteksoft.acs.entity.organization.Department();
		department2.setShortTitle("pp");
		department2.setCompany(companyDao.get(1L));
		departmentDao.save(department2);
		
		List<Department> result = ApiFactory.getAcsService().getDepartments();
		Assert.assertNotNull(result);
	}
	@Test
	public void getDepartmentsByCompany(){
		com.norteksoft.acs.entity.organization.Department department = new com.norteksoft.acs.entity.organization.Department();
		department.setShortTitle("ss");
		department.setCompany(companyDao.get(1L));
		departmentDao.save(department);
		
		com.norteksoft.acs.entity.organization.Department department2 = new com.norteksoft.acs.entity.organization.Department();
		department2.setShortTitle("pp");
		department2.setBranch(true);
		department2.setCompany(companyDao.get(1L));
		departmentDao.save(department2);
		com.norteksoft.acs.entity.organization.Department department3 = new com.norteksoft.acs.entity.organization.Department();
		department3.setShortTitle("dd");
		department3.setParent(department2);
		department3.setSubCompanyId(department2.getId());
		department3.setCompany(companyDao.get(1L));
		departmentDao.save(department3);
		
		List<Department> result = ApiFactory.getAcsService().getDepartmentsByCompany();
		Assert.assertTrue(result.size()==1);
	}
	@Test
	public void getBranchs(){
		com.norteksoft.acs.entity.organization.Department department = new com.norteksoft.acs.entity.organization.Department();
		department.setShortTitle("ss");
		department.setCompany(companyDao.get(1L));
		departmentDao.save(department);
		
		com.norteksoft.acs.entity.organization.Department department2 = new com.norteksoft.acs.entity.organization.Department();
		department2.setShortTitle("pp");
		department2.setBranch(true);
		department2.setCompany(companyDao.get(1L));
		departmentDao.save(department2);
		com.norteksoft.acs.entity.organization.Department department3 = new com.norteksoft.acs.entity.organization.Department();
		department3.setShortTitle("dd");
		department3.setBranch(true);
		department3.setCompany(companyDao.get(1L));
		departmentDao.save(department3);
		
		List<Department> result = ApiFactory.getAcsService().getBranchs();
		Assert.assertTrue(result.size()==2);
	}
	
	@Test
	public void getWorkgroups(){
		com.norteksoft.acs.entity.organization.Workgroup workgroup = new com.norteksoft.acs.entity.organization.Workgroup();
		workgroup.setCode("workgroup_1");
		workgroup.setCompany(companyDao.get(1L));
		workgroup.setName("工作组1");
		workgroupDao.save(workgroup);
		
		com.norteksoft.acs.entity.organization.Workgroup workgroup2 = new com.norteksoft.acs.entity.organization.Workgroup();
		workgroup2.setCode("workgroup_2");
		workgroup2.setCompany(companyDao.get(1L));
		workgroup2.setName("工作组2");
		workgroupDao.save(workgroup2);
		
		List<Workgroup> result = ApiFactory.getAcsService().getWorkgroups();
		Assert.assertNotNull(result);
	}
	@Test
	public void getWorkgroupsByBranchId(){
		com.norteksoft.acs.entity.organization.Department branch = new com.norteksoft.acs.entity.organization.Department();
		branch.setShortTitle("pp");
		branch.setBranch(true);
		branch.setCompany(companyDao.get(1L));
		departmentDao.save(branch);
		
		com.norteksoft.acs.entity.organization.Workgroup workgroup = new com.norteksoft.acs.entity.organization.Workgroup();
		workgroup.setCode("workgroup_1");
		workgroup.setCompany(companyDao.get(1L));
		workgroup.setName("工作组1");
		workgroupDao.save(workgroup);
		
		com.norteksoft.acs.entity.organization.Workgroup workgroup2 = new com.norteksoft.acs.entity.organization.Workgroup();
		workgroup2.setCode("workgroup_2");
		workgroup2.setSubCompanyId(branch.getId());
		workgroup2.setCompany(companyDao.get(1L));
		workgroup2.setName("工作组2");
		workgroupDao.save(workgroup2);
		
		List<Workgroup> result = ApiFactory.getAcsService().getWorkgroupsByBranchId(branch.getId());
		Assert.assertTrue(result.size()==1);
	}
	@Test
	public void getAllWorkgroups(){
		com.norteksoft.acs.entity.organization.Department branch = new com.norteksoft.acs.entity.organization.Department();
		branch.setShortTitle("pp");
		branch.setBranch(true);
		branch.setCompany(companyDao.get(1L));
		departmentDao.save(branch);
		
		com.norteksoft.acs.entity.organization.Workgroup workgroup = new com.norteksoft.acs.entity.organization.Workgroup();
		workgroup.setCode("workgroup_1");
		workgroup.setCompany(companyDao.get(1L));
		workgroup.setName("工作组1");
		workgroupDao.save(workgroup);
		
		com.norteksoft.acs.entity.organization.Workgroup workgroup2 = new com.norteksoft.acs.entity.organization.Workgroup();
		workgroup2.setCode("workgroup_2");
		workgroup2.setSubCompanyId(branch.getId());
		workgroup2.setCompany(companyDao.get(1L));
		workgroup2.setName("工作组2");
		workgroupDao.save(workgroup2);
		
		List<Workgroup> result = ApiFactory.getAcsService().getAllWorkgroups();
		Assert.assertTrue(result.size()==2);
	}
	
	@Test
	public void getUsersByDepartmentId(){
		ThreadParameters tp = new ThreadParameters();
		tp.setCompanyId(1L);
		com.norteksoft.acs.entity.organization.Department department = new com.norteksoft.acs.entity.organization.Department();
		department.setName("haveUsers");
		department.setCode("haveUsers");
		department.setCompany(companyDao.get(1L));
		department.setDeleted(false);
		departmentDao.save(department);
		
		com.norteksoft.acs.entity.organization.User user = new com.norteksoft.acs.entity.organization.User();
		user.setLoginName("wangjing");
		user.setCompanyId(1L);
		user.setDeleted(false);
		List<UserInfo> userInfos = new ArrayList<UserInfo>();
		UserInfo userInfo = new UserInfo();
		userInfo.setUser(user);
		userInfo.setCompanyId(1L);
		userInfo.setDeleted(false);
		userInfo.setBodyWeight("fsdfsd");
		userInfos.add(userInfo);
		user.setUserInfos(userInfos);
		userInfoDao.save(userInfo);
		userDao.save(user);
		
		DepartmentUser du = new DepartmentUser();
		du.setCompanyId(1L);
		du.setDeleted(false);
		du.setDepartment(department);
		du.setUser(user);
		departmentUserDao.save(du);
		
		com.norteksoft.acs.entity.organization.User user2 = new com.norteksoft.acs.entity.organization.User();
		user2.setLoginName("ldx");
		user2.setCompanyId(1L);
		user2.setDeleted(false);
		List<UserInfo> userInfos2 = new ArrayList<UserInfo>();
		UserInfo userInfo2 = new UserInfo();
		userInfo2.setUser(user2);
		userInfo2.setCompanyId(1L);
		userInfo2.setDeleted(false);
		userInfo2.setBodyWeight("ghhdfh");
		userInfos2.add(userInfo2);
		user2.setUserInfos(userInfos2);
		userInfoDao.save(userInfo2);
		userDao.save(user2);
		
		DepartmentUser du2 = new DepartmentUser();
		du2.setCompanyId(1L);
		du2.setDeleted(false);
		du2.setDepartment(department);
		du2.setUser(user2);
		departmentUserDao.save(du2);
		
		Long id = ApiFactory.getAcsService().getDepartmentByCode("haveUsers").getId();
		List<User> result = ApiFactory.getAcsService().getUsersByDepartmentId(id);
		Assert.assertNotNull(result);
		Assert.assertTrue(result.size()==2);
	}
	
	@Test
	public void getUserLoginNamesByDepartmentName(){
		com.norteksoft.acs.entity.organization.Department department = new com.norteksoft.acs.entity.organization.Department();
		department.setName("haveUsers");
		department.setCode("haveUsers");
		department.setCompany(companyDao.get(1L));
		department.setDeleted(false);
		departmentDao.save(department);
		
		com.norteksoft.acs.entity.organization.User user = new com.norteksoft.acs.entity.organization.User();
		user.setLoginName("wangjing");
		user.setCompanyId(1L);
		user.setDeleted(false);
		List<UserInfo> userInfos = new ArrayList<UserInfo>();
		UserInfo userInfo = new UserInfo();
		userInfo.setUser(user);
		userInfo.setCompanyId(1L);
		userInfo.setDeleted(false);
		userInfo.setBodyWeight("fsdfsd");
		userInfos.add(userInfo);
		user.setUserInfos(userInfos);
		userInfoDao.save(userInfo);
		userDao.save(user);
		
		DepartmentUser du = new DepartmentUser();
		du.setCompanyId(1L);
		du.setDeleted(false);
		du.setDepartment(department);
		du.setUser(user);
		departmentUserDao.save(du);
		
		com.norteksoft.acs.entity.organization.User user2 = new com.norteksoft.acs.entity.organization.User();
		user2.setLoginName("ldx");
		user2.setCompanyId(1L);
		user2.setDeleted(false);
		List<UserInfo> userInfos2 = new ArrayList<UserInfo>();
		UserInfo userInfo2 = new UserInfo();
		userInfo2.setUser(user2);
		userInfo2.setCompanyId(1L);
		userInfo2.setDeleted(false);
		userInfo2.setBodyWeight("ghhdfh");
		userInfos2.add(userInfo2);
		user2.setUserInfos(userInfos2);
		userInfoDao.save(userInfo2);
		userDao.save(user2);
		
		DepartmentUser du2 = new DepartmentUser();
		du2.setCompanyId(1L);
		du2.setDeleted(false);
		du2.setDepartment(department);
		du2.setUser(user2);
		departmentUserDao.save(du2);
		
		List<String> result = ApiFactory.getAcsService().getUserLoginNamesByDepartmentName("haveUsers");
		Assert.assertNotNull(result);
		Assert.assertTrue(result.size()==2);
		Assert.assertTrue(result.contains("wangjing"));
		Assert.assertTrue(result.contains("ldx"));
	}
	@Test
	public void getLoginNamesByDepartmentId(){
		com.norteksoft.acs.entity.organization.Department department = new com.norteksoft.acs.entity.organization.Department();
		department.setName("haveUsers");
		department.setCode("haveUsers");
		department.setCompany(companyDao.get(1L));
		department.setDeleted(false);
		departmentDao.save(department);
		
		com.norteksoft.acs.entity.organization.User user = new com.norteksoft.acs.entity.organization.User();
		user.setLoginName("wangjing");
		user.setCompanyId(1L);
		user.setDeleted(false);
		List<UserInfo> userInfos = new ArrayList<UserInfo>();
		UserInfo userInfo = new UserInfo();
		userInfo.setUser(user);
		userInfo.setCompanyId(1L);
		userInfo.setDeleted(false);
		userInfo.setBodyWeight("fsdfsd");
		userInfos.add(userInfo);
		user.setUserInfos(userInfos);
		userInfoDao.save(userInfo);
		userDao.save(user);
		
		DepartmentUser du = new DepartmentUser();
		du.setCompanyId(1L);
		du.setDeleted(false);
		du.setDepartment(department);
		du.setUser(user);
		departmentUserDao.save(du);
		
		com.norteksoft.acs.entity.organization.User user2 = new com.norteksoft.acs.entity.organization.User();
		user2.setLoginName("ldx");
		user2.setCompanyId(1L);
		user2.setDeleted(false);
		List<UserInfo> userInfos2 = new ArrayList<UserInfo>();
		UserInfo userInfo2 = new UserInfo();
		userInfo2.setUser(user2);
		userInfo2.setCompanyId(1L);
		userInfo2.setDeleted(false);
		userInfo2.setBodyWeight("ghhdfh");
		userInfos2.add(userInfo2);
		user2.setUserInfos(userInfos2);
		userInfoDao.save(userInfo2);
		userDao.save(user2);
		
		DepartmentUser du2 = new DepartmentUser();
		du2.setCompanyId(1L);
		du2.setDeleted(false);
		du2.setDepartment(department);
		du2.setUser(user2);
		departmentUserDao.save(du2);
		
		List<String> result = ApiFactory.getAcsService().getLoginNamesByDepartmentId(department.getId());
		Assert.assertNotNull(result);
		Assert.assertTrue(result.size()==2);
		Assert.assertTrue(result.contains("wangjing"));
		Assert.assertTrue(result.contains("ldx"));
	}
	
	@Test
	public void getUsersByWorkgroupId(){
		com.norteksoft.acs.entity.organization.Workgroup workgroup = new com.norteksoft.acs.entity.organization.Workgroup();
		workgroup.setCode("workgroup_1");
		workgroup.setCompany(companyDao.get(1L));
		workgroup.setName("工作组1");
		workgroupDao.save(workgroup);
		
		com.norteksoft.acs.entity.organization.User user = new com.norteksoft.acs.entity.organization.User();
		user.setLoginName("wangjing");
		user.setCompanyId(1L);
		user.setDeleted(false);
		List<UserInfo> userInfos = new ArrayList<UserInfo>();
		UserInfo userInfo = new UserInfo();
		userInfo.setUser(user);
		userInfo.setCompanyId(1L);
		userInfo.setDeleted(false);
		userInfo.setBodyWeight("fsdfsd");
		userInfos.add(userInfo);
		user.setUserInfos(userInfos);
		userInfoDao.save(userInfo);
		userDao.save(user);
		
		WorkgroupUser wu = new WorkgroupUser();
		wu.setCompanyId(1L);
		wu.setDeleted(false);
		wu.setWorkgroup(workgroup);
		wu.setUser(user);
		workgroupUserDao.save(wu);
		
		com.norteksoft.acs.entity.organization.User user2 = new com.norteksoft.acs.entity.organization.User();
		user2.setLoginName("ldx");
		user2.setCompanyId(1L);
		user2.setDeleted(false);
		List<UserInfo> userInfos2 = new ArrayList<UserInfo>();
		UserInfo userInfo2 = new UserInfo();
		userInfo2.setUser(user2);
		userInfo2.setCompanyId(1L);
		userInfo2.setDeleted(false);
		userInfo2.setBodyWeight("ghhdfh");
		userInfos2.add(userInfo2);
		user2.setUserInfos(userInfos2);
		userInfoDao.save(userInfo2);
		userDao.save(user2);
		
		WorkgroupUser wu2 = new WorkgroupUser();
		wu2.setCompanyId(1L);
		wu2.setDeleted(false);
		wu2.setWorkgroup(workgroup);
		wu2.setUser(user2);
		workgroupUserDao.save(wu2);
		
		Long id = ApiFactory.getAcsService().getWorkgroupByCode("workgroup_1").getId();
		List<User> result = ApiFactory.getAcsService().getUsersByWorkgroupId(id);
		Assert.assertNotNull(result);
		Assert.assertTrue(result.size()==2);
	}
	
	
	@Test
	public void getSubDepartmentList(){
		com.norteksoft.acs.entity.organization.Department department = new com.norteksoft.acs.entity.organization.Department();
		department.setName("subDepartment");
		department.setCode("subDepartment");
		department.setCompany(companyDao.get(1L));
		department.setDeleted(false);
		
		
		com.norteksoft.acs.entity.organization.Department department2 = new com.norteksoft.acs.entity.organization.Department();
		department2.setName("subDepartment2");
		department2.setCode("subDepartment2");
		department2.setCompany(companyDao.get(1L));
		department2.setDeleted(false);
		
		
		com.norteksoft.acs.entity.organization.Department parentDepartment = new com.norteksoft.acs.entity.organization.Department();
		parentDepartment.setName("parentDepartment");
		parentDepartment.setCode("parentDepartment");
		parentDepartment.setCompany(companyDao.get(1L));
		parentDepartment.setDeleted(false);
		Set<com.norteksoft.acs.entity.organization.Department> children = new HashSet<com.norteksoft.acs.entity.organization.Department>();
		children.add(department);
		children.add(department2);
		parentDepartment.setChildren(children);
		
		
		department2.setParent(parentDepartment);
		departmentDao.save(department2);
		department.setParent(parentDepartment);
		departmentDao.save(department);
		departmentDao.save(parentDepartment);
		
		
		Long id = ApiFactory.getAcsService().getDepartmentByCode("parentDepartment").getId();
		List<Department> result = ApiFactory.getAcsService().getSubDepartmentList(id);
		Assert.assertNotNull(result);
		Assert.assertTrue(result.size()==2);
	}
	
	@Test
	public void getUserById(){
		User user = new User();
		user.setLoginName("wangjing");
		user.setCompanyId(1L);
		user.setDeleted(false);
		ApiFactory.getAcsService().saveUser(user);
		
		Long id = ApiFactory.getAcsService().getUserByLoginName("wangjing").getId();
		User result = ApiFactory.getAcsService().getUserById(id);
		Assert.assertNotNull(result);
		Assert.assertEquals(id, result.getId());
	}
	
	@Test
	public void getTrustedRolesByUserId(){
		com.norteksoft.acs.entity.organization.User user = new com.norteksoft.acs.entity.organization.User();
		user.setLoginName("wangjingewewe");
		user.setCompanyId(1L);
		user.setDeleted(false);
		userDao.save(user);
		
		com.norteksoft.acs.entity.authorization.Role role = new com.norteksoft.acs.entity.authorization.Role();
		role.setCode("role_code_1");
		role.setName("role_name_1");
		roleDao.save(role);
		
		RoleUser roleUser = new RoleUser();
		roleUser.setCompanyId(1L);
		roleUser.setConsigner(1111L);
		roleUser.setDeleted(false);
		roleUser.setRole(role);
		roleUser.setUser(user);
		roleUserDao.save(roleUser);
		
		Long id = userDao.findLong("select u.id from User u where u.loginName = ? ", "wangjingewewe");
		Set<Role> result = ApiFactory.getAcsService().getTrustedRolesByUserId(id,1111L);
		Assert.assertNotNull(result);
	}
	
	@Test
	public void getRolesExcludeTrustedRole(){
		com.norteksoft.acs.entity.organization.User user = new com.norteksoft.acs.entity.organization.User();
		user.setLoginName("wangjingewewe");
		user.setCompanyId(1L);
		user.setDeleted(false);
		userDao.save(user);
		
		com.norteksoft.acs.entity.authorization.Role role = new com.norteksoft.acs.entity.authorization.Role();
		role.setCode("role_code_1");
		role.setName("role_name_1");
		roleDao.save(role);
		
		RoleUser roleUser = new RoleUser();
		roleUser.setCompanyId(1L);
		roleUser.setConsigner(1111L);
		roleUser.setDeleted(false);
		roleUser.setRole(role);
		roleUser.setUser(user);
		roleUserDao.save(roleUser);
		
		User u = ApiFactory.getAcsService().getUserByLoginName("wangjingewewe");
		String result = ApiFactory.getAcsService().getRolesExcludeTrustedRole(u);
		Assert.assertEquals("role_code_1", result);
	}
	
	@Test
	public void getRolesByUser(){
		com.norteksoft.acs.entity.organization.User user = new com.norteksoft.acs.entity.organization.User();
		user.setLoginName("wangjingewewe");
		user.setCompanyId(1L);
		user.setDeleted(false);
		userDao.save(user);
		
		com.norteksoft.acs.entity.authorization.Role role = new com.norteksoft.acs.entity.authorization.Role();
		role.setCode("role_code_1");
		role.setName("role_name_1");
		roleDao.save(role);
		
		RoleUser roleUser = new RoleUser();
		roleUser.setCompanyId(1L);
		roleUser.setDeleted(false);
		roleUser.setRole(role);
		roleUser.setUser(user);
		roleUserDao.save(roleUser);
		
		User u = ApiFactory.getAcsService().getUserByLoginName("wangjingewewe");
		Set<Role> result = ApiFactory.getAcsService().getRolesByUser(u.getId());
		Assert.assertNotNull(result);
	}
	
	
	@Test
	public void getRolesByUserTwo(){
		com.norteksoft.acs.entity.organization.User user = new com.norteksoft.acs.entity.organization.User();
		user.setLoginName("wangjingewewe");
		user.setCompanyId(1L);
		user.setDeleted(false);
		userDao.save(user);
		
		com.norteksoft.acs.entity.authorization.Role role = new com.norteksoft.acs.entity.authorization.Role();
		role.setCode("role_code_1");
		role.setName("role_name_1");
		roleDao.save(role);
		
		RoleUser roleUser = new RoleUser();
		roleUser.setCompanyId(1L);
		roleUser.setDeleted(false);
		roleUser.setRole(role);
		roleUser.setUser(user);
		roleUserDao.save(roleUser);
		
		User u = ApiFactory.getAcsService().getUserByLoginName("wangjingewewe");
		Set<Role> result = ApiFactory.getAcsService().getRolesByUser(u);
		Assert.assertNotNull(result);
	}
	
	@SuppressWarnings("unchecked")
	@Test
	public void getUsersWithoutDepartment(){
		User user = new User();
		user.setLoginName("wangjing");
		user.setCompanyId(1L);
		user.setDeleted(false);
		ApiFactory.getAcsService().saveUser(user);
		
		List<User> result = ApiFactory.getAcsService().getUsersWithoutDepartment();
		Assert.assertNotNull(result);
		for(User u : result){
		   List<DepartmentUser> departmentUser = departmentUserDao.find("FROM DepartmentUser du where du.user.id=? and du.deleted=?", u.getId(),false);
		   Assert.assertTrue(departmentUser.size()==0);
		}
	}
	@Test
	public void getUsersWithoutBranch(){
		com.norteksoft.acs.entity.organization.Department branch = new com.norteksoft.acs.entity.organization.Department();
		branch.setShortTitle("pp");
		branch.setBranch(true);
		branch.setCompany(companyDao.get(1L));
		departmentDao.save(branch);
//		com.norteksoft.acs.entity.organization.Department dept = new com.norteksoft.acs.entity.organization.Department();
//		dept.setShortTitle("dd");
//		dept.setSubCompanyId(branch.getId());
//		dept.setCompany(companyDao.get(1L));
//		departmentDao.save(dept);
//		
//		com.norteksoft.acs.entity.organization.User user = new com.norteksoft.acs.entity.organization.User();
//		user.setLoginName("wangjing");
//		user.setCompanyId(1L);
//		user.setDeleted(false);
//		user.setSubCompanyId(branch.getId());
//		userDao.save(user);
//		
//		UserInfo userInfo = new UserInfo();
//		userInfo.setUser(user);
//		userInfoDao.save(userInfo);
//		
//		DepartmentUser deptUser1 = new DepartmentUser();
//		deptUser1.setCompanyId(1L);
//		deptUser1.setSubCompanyId(branch.getId());
//		deptUser1.setDepartment(dept);
//		deptUser1.setUser(user);
//		departmentUserDao.save(deptUser1);
		
		com.norteksoft.acs.entity.organization.User user2 = new com.norteksoft.acs.entity.organization.User();
		user2.setLoginName("liudongxia");
		user2.setCompanyId(1L);
		user2.setDeleted(false);
		user2.setSubCompanyId(branch.getId());
		userDao.save(user2);
		
		UserInfo userInfo = new UserInfo();
		userInfo.setUser(user2);
		userInfoDao.save(userInfo);
		
		DepartmentUser deptUser = new DepartmentUser();
		deptUser.setCompanyId(1L);
		deptUser.setSubCompanyId(branch.getId());
		deptUser.setDepartment(branch);
		deptUser.setUser(user2);
		departmentUserDao.save(deptUser);
		
		List<User> result = ApiFactory.getAcsService().getUsersWithoutBranch(branch.getId());
		Assert.assertTrue(result.size()==1);
	}
	
	@SuppressWarnings("unchecked")
	@Test
	public void assignTrustedRole(){
		com.norteksoft.acs.entity.organization.User user = new com.norteksoft.acs.entity.organization.User();
		user.setLoginName("wangjingewewe");
		user.setCompanyId(1L);
		user.setDeleted(false);
		userDao.save(user);
		
		com.norteksoft.acs.entity.authorization.Role role = new com.norteksoft.acs.entity.authorization.Role();
		role.setCode("role_code_1");
		role.setName("role_name_1");
		role.setCompanyId(1L);
		roleDao.save(role);
		
		com.norteksoft.acs.entity.authorization.Role r = roleDao.findUniqueByProperty("code", "role_code_1");
		String[] roleIds = {r.getId().toString()};
		Long id = userDao.findLong("select u.id from User u where u.loginName = ? ", "wangjingewewe");
		ApiFactory.getAcsService().assignTrustedRole(1111L,roleIds,id);
		List<RoleUser> result = roleUserDao.find("FROM RoleUser du where du.user.id=? and du.deleted=?", id,false);
		Assert.assertNotNull(result);
		Assert.assertEquals(result.get(0).getRole().getCode(), "role_code_1");
	}
	
	@SuppressWarnings("unchecked")
	@Test
	public void deleteTrustedRole(){
		com.norteksoft.acs.entity.organization.User user = new com.norteksoft.acs.entity.organization.User();
		user.setLoginName("wangjingewewe");
		user.setCompanyId(1L);
		user.setDeleted(false);
		userDao.save(user);
		
		com.norteksoft.acs.entity.authorization.Role role = new com.norteksoft.acs.entity.authorization.Role();
		role.setCode("role_code_1");
		role.setName("role_name_1");
		roleDao.save(role);
		
		RoleUser roleUser = new RoleUser();
		roleUser.setCompanyId(1L);
		roleUser.setDeleted(false);
		roleUser.setRole(role);
		roleUser.setUser(user);
		roleUser.setConsigner(1111L);
		roleUserDao.save(roleUser);
		
		Long id = ApiFactory.getAcsService().getUserByLoginName("wangjingewewe").getId();
		com.norteksoft.acs.entity.authorization.Role r = roleDao.findUniqueByProperty("code", "role_code_1");
		String[] roleIds = {r.getId().toString()};
		List<RoleUser> ru = roleUserDao.find("FROM RoleUser du where du.user.id=? and du.deleted=? and du.consigner=?", id,false,1111L);
		Assert.assertNotNull(ru);
		ApiFactory.getAcsService().deleteTrustedRole(1111L,roleIds,id);
		List<RoleUser> ru2 = roleUserDao.find("FROM RoleUser du where du.user.id=? and du.deleted=? and du.consigner=?", id,false,1111L);
		Assert.assertTrue(ru2.size()==0);
	}
	
	@Test
	public void getRoleById(){
		com.norteksoft.acs.entity.authorization.Role role = new com.norteksoft.acs.entity.authorization.Role();
		role.setCode("role_code_1");
		role.setName("role_name_1");
		roleDao.save(role);
		
		Long id = roleDao.findUniqueByProperty("code", "role_code_1").getId();
		Role result = ApiFactory.getAcsService().getRoleById(id);
		Assert.assertNotNull(result);
		Assert.assertEquals("role_code_1", result.getCode());
	}
	
	
	@Test
	public void deleteAllTrustedRole(){
		com.norteksoft.acs.entity.organization.User user = new com.norteksoft.acs.entity.organization.User();
		user.setLoginName("wangjingewewe");
		user.setCompanyId(1L);
		user.setDeleted(false);
		userDao.save(user);
		
		com.norteksoft.acs.entity.authorization.Role role = new com.norteksoft.acs.entity.authorization.Role();
		role.setCode("role_code_1");
		role.setName("role_name_1");
		roleDao.save(role);
		
		RoleUser roleUser = new RoleUser();
		roleUser.setCompanyId(1L);
		roleUser.setConsigner(1111L);
		roleUser.setDeleted(false);
		roleUser.setRole(role);
		roleUser.setUser(user);
		roleUserDao.save(roleUser);
		
		Long id = userDao.findLong("select u.id from User u where u.loginName = ? ", "wangjingewewe");
		Set<Role> r = ApiFactory.getAcsService().getTrustedRolesByUserId(id,1111L);
		Assert.assertNotNull(r);
		ApiFactory.getAcsService().deleteAllTrustedRole(1111L,id);
		Set<Role> result = ApiFactory.getAcsService().getTrustedRolesByUserId(id,1111L);
		Assert.assertTrue(result.size()==0);
	}
	
	@Test
	public void getWorkgroupById(){
		com.norteksoft.acs.entity.organization.Workgroup workgroup = new com.norteksoft.acs.entity.organization.Workgroup();
		workgroup.setCode("workgroup_1");
		workgroup.setCompany(companyDao.get(1L));
		workgroup.setName("工作组1");
		workgroupDao.save(workgroup);
		
		Long id = ApiFactory.getAcsService().getWorkgroupByCode("workgroup_1").getId();
		Workgroup result = ApiFactory.getAcsService().getWorkgroupById(id);
		Assert.assertNotNull(result);
		Assert.assertEquals("workgroup_1", result.getCode());
	}
	
	@Test
	public void getWorkgroupByCode(){
		com.norteksoft.acs.entity.organization.Workgroup workgroup = new com.norteksoft.acs.entity.organization.Workgroup();
		workgroup.setCode("workgroup_1");
		workgroup.setCompany(companyDao.get(1L));
		workgroup.setName("工作组1");
		workgroupDao.save(workgroup);
		
		Workgroup result = ApiFactory.getAcsService().getWorkgroupByCode("workgroup_1");
		Assert.assertNotNull(result);
		Assert.assertEquals("workgroup_1", result.getCode());
	}
	
	@Test
	public void getDepartmentById(){
		com.norteksoft.acs.entity.organization.Department department = new com.norteksoft.acs.entity.organization.Department();
		department.setShortTitle("ss");
		department.setCode("department_code_1");
		department.setName("department_name_1");
		department.setCompany(companyDao.get(1L));
		departmentDao.save(department);
		
		Long id = departmentDao.findUniqueByProperty("code", "department_code_1").getId();
		Department result = ApiFactory.getAcsService().getDepartmentById(id);
		Assert.assertNotNull(result);
		Assert.assertEquals("department_code_1", result.getCode());
	}
	
	@Test
	public void getDepartmentByName(){
		com.norteksoft.acs.entity.organization.Department department = new com.norteksoft.acs.entity.organization.Department();
		department.setShortTitle("ss");
		department.setCode("department_code_1");
		department.setName("department_name_1");
		department.setCompany(companyDao.get(1L));
		departmentDao.save(department);
		
		Department result = ApiFactory.getAcsService().getDepartmentByName("department_name_1");
		Assert.assertNotNull(result);
		Assert.assertEquals("department_name_1", result.getName());
	}
	
	@Test
	public void getDepartmentByCode(){
		com.norteksoft.acs.entity.organization.Department department = new com.norteksoft.acs.entity.organization.Department();
		department.setShortTitle("ss");
		department.setCode("department_code_1");
		department.setName("department_name_1");
		department.setCompany(companyDao.get(1L));
		departmentDao.save(department);
		
		Department result = ApiFactory.getAcsService().getDepartmentByCode("department_code_1");
		Assert.assertNotNull(result);
		Assert.assertEquals("department_code_1", result.getCode());
	}
	
	
	/**
	 * 查询所有人员（不包含无部门人员）
	 * @param companyId
	 * @return
	 */
	@Test
	public void getUsersByCompany(){
		com.norteksoft.acs.entity.organization.Department department = new com.norteksoft.acs.entity.organization.Department();
		department.setName("haveUsers");
		department.setCode("haveUsers");
		department.setCompany(companyDao.get(1L));
		department.setDeleted(false);
		departmentDao.save(department);
		
		com.norteksoft.acs.entity.organization.User user = new com.norteksoft.acs.entity.organization.User();
		user.setLoginName("wangjing");
		user.setCompanyId(1L);
		user.setDeleted(false);
		List<UserInfo> userInfos = new ArrayList<UserInfo>();
		UserInfo userInfo = new UserInfo();
		userInfo.setUser(user);
		userInfo.setCompanyId(1L);
		userInfo.setDeleted(false);
		userInfo.setBodyWeight("fsdfsd");
		userInfos.add(userInfo);
		user.setUserInfos(userInfos);
		userInfoDao.save(userInfo);
		userDao.save(user);
		
		DepartmentUser du = new DepartmentUser();
		du.setCompanyId(1L);
		du.setDeleted(false);
		du.setDepartment(department);
		du.setUser(user);
		departmentUserDao.save(du);
		
		com.norteksoft.acs.entity.organization.User user2 = new com.norteksoft.acs.entity.organization.User();
		user2.setLoginName("ldx");
		user2.setCompanyId(1L);
		user2.setDeleted(false);
		List<UserInfo> userInfos2 = new ArrayList<UserInfo>();
		UserInfo userInfo2 = new UserInfo();
		userInfo2.setUser(user2);
		userInfo2.setCompanyId(1L);
		userInfo2.setDeleted(false);
		userInfo2.setBodyWeight("ghhdfh");
		userInfos2.add(userInfo2);
		user2.setUserInfos(userInfos2);
		userInfoDao.save(userInfo2);
		userDao.save(user2);
		
		DepartmentUser du2 = new DepartmentUser();
		du2.setCompanyId(1L);
		du2.setDeleted(false);
		du2.setDepartment(department);
		du2.setUser(user2);
		departmentUserDao.save(du2);
		
		
		com.norteksoft.acs.entity.organization.User userWithoutDepartment = new com.norteksoft.acs.entity.organization.User();
		user2.setLoginName("userWithoutDepartment");
		user2.setCompanyId(1L);
		user2.setDeleted(false);
		List<UserInfo> userInfos3 = new ArrayList<UserInfo>();
		UserInfo userInfo3 = new UserInfo();
		userInfo3.setUser(user2);
		userInfo3.setCompanyId(1L);
		userInfo3.setDeleted(false);
		userInfo3.setBodyWeight("ghhdfh");
		userInfos3.add(userInfo3);
		user2.setUserInfos(userInfos3);
		userInfoDao.save(userInfo3);
		userDao.save(userWithoutDepartment);
		
		List<User> result = ApiFactory.getAcsService().getUsersByCompany(1L);
		Assert.assertNotNull(result);
		User u = ApiFactory.getAcsService().getUserByLoginName("userWithoutDepartment");
		Assert.assertTrue(!result.contains(u));
	}
	
	
	/**
	 * 查询所有人员（包含无部门人员）
	 * @param companyId
	 * @return
	 */
	@Test
	public void getAllUsersByCompany(){
		com.norteksoft.acs.entity.organization.Department department = new com.norteksoft.acs.entity.organization.Department();
		department.setName("haveUsers");
		department.setCode("haveUsers");
		department.setCompany(companyDao.get(1L));
		department.setDeleted(false);
		departmentDao.save(department);
		
		com.norteksoft.acs.entity.organization.User user = new com.norteksoft.acs.entity.organization.User();
		user.setLoginName("wangjing");
		user.setCompanyId(1L);
		user.setDeleted(false);
		List<UserInfo> userInfos = new ArrayList<UserInfo>();
		UserInfo userInfo = new UserInfo();
		userInfo.setUser(user);
		userInfo.setCompanyId(1L);
		userInfo.setDeleted(false);
		userInfo.setBodyWeight("fsdfsd");
		userInfos.add(userInfo);
		user.setUserInfos(userInfos);
		userInfoDao.save(userInfo);
		userDao.save(user);
		
		DepartmentUser du = new DepartmentUser();
		du.setCompanyId(1L);
		du.setDeleted(false);
		du.setDepartment(department);
		du.setUser(user);
		departmentUserDao.save(du);
		
		com.norteksoft.acs.entity.organization.User user2 = new com.norteksoft.acs.entity.organization.User();
		user2.setLoginName("ldx");
		user2.setCompanyId(1L);
		user2.setDeleted(false);
		List<UserInfo> userInfos2 = new ArrayList<UserInfo>();
		UserInfo userInfo2 = new UserInfo();
		userInfo2.setUser(user2);
		userInfo2.setCompanyId(1L);
		userInfo2.setDeleted(false);
		userInfo2.setBodyWeight("ghhdfh");
		userInfos2.add(userInfo2);
		user2.setUserInfos(userInfos2);
		userInfoDao.save(userInfo2);
		userDao.save(user2);
		
		DepartmentUser du2 = new DepartmentUser();
		du2.setCompanyId(1L);
		du2.setDeleted(false);
		du2.setDepartment(department);
		du2.setUser(user2);
		departmentUserDao.save(du2);
		
		
		com.norteksoft.acs.entity.organization.User userWithoutDepartment = new com.norteksoft.acs.entity.organization.User();
		user2.setLoginName("userWithoutDepartment");
		user2.setCompanyId(1L);
		user2.setDeleted(false);
		List<UserInfo> userInfos3 = new ArrayList<UserInfo>();
		UserInfo userInfo3 = new UserInfo();
		userInfo3.setUser(user2);
		userInfo3.setCompanyId(1L);
		userInfo3.setDeleted(false);
		userInfo3.setBodyWeight("ghhdfh");
		userInfos3.add(userInfo3);
		user2.setUserInfos(userInfos3);
		userInfoDao.save(userInfo3);
		userDao.save(userWithoutDepartment);
		
		List<User> result = ApiFactory.getAcsService().getAllUsersByCompany(1L);
		Assert.assertNotNull(result);
		User u = ApiFactory.getAcsService().getUserByLoginName("userWithoutDepartment");
		Assert.assertTrue(isContain(result,u));
	}

	private boolean isContain(List<User> result, User u) {
		for(User user:result){
			if(u.getId()==user.getId())return true;
		}
		return false;
	}
	
	@Test
	public void getUsersByRoleName(){
		com.norteksoft.acs.entity.organization.User user = new com.norteksoft.acs.entity.organization.User();
		user.setLoginName("wangjingewewe");
		user.setCompanyId(1L);
		user.setDeleted(false);
		userDao.save(user);
		 
		BusinessSystem bs = businessSystemDao.findUniqueByProperty("code", "ems");
		com.norteksoft.acs.entity.authorization.Role role = new com.norteksoft.acs.entity.authorization.Role();
		role.setCode("role_code_1");
		role.setName("role_name_1");
		role.setBusinessSystem(bs);
		roleDao.save(role);
		
		RoleUser roleUser = new RoleUser();
		roleUser.setCompanyId(1L);
		roleUser.setDeleted(false);
		roleUser.setRole(role);
		roleUser.setUser(user);
		roleUserDao.save(roleUser);
		
		Set<User> result = ApiFactory.getAcsService().getUsersByRoleName(bs.getId(),"role_name_1");
		Assert.assertNotNull(result);
	}
	
	@Test
	public void getUsersWithoutRoleName(){
		com.norteksoft.acs.entity.organization.User user = new com.norteksoft.acs.entity.organization.User();
		user.setLoginName("wangjingewewe");
		user.setCompanyId(1L);
		user.setDeleted(false);
		userDao.save(user);
		 
		BusinessSystem bs = businessSystemDao.findUniqueByProperty("code", "ems");
		com.norteksoft.acs.entity.authorization.Role role = new com.norteksoft.acs.entity.authorization.Role();
		role.setCode("role_code_1");
		role.setName("role_name_1");
		role.setBusinessSystem(bs);
		roleDao.save(role);
		
		RoleUser roleUser = new RoleUser();
		roleUser.setCompanyId(1L);
		roleUser.setDeleted(false);
		roleUser.setRole(role);
		roleUser.setUser(user);
		roleUserDao.save(roleUser);
		
		com.norteksoft.acs.entity.organization.User user2 = new com.norteksoft.acs.entity.organization.User();
		user2.setLoginName("ldxewewe");
		user2.setCompanyId(1L);
		user2.setDeleted(false);
		userDao.save(user2);
		 
		com.norteksoft.acs.entity.authorization.Role role2 = new com.norteksoft.acs.entity.authorization.Role();
		role2.setCode("role_code_2");
		role2.setName("role_name_2");
		role2.setBusinessSystem(bs);
		roleDao.save(role2);
		
		RoleUser roleUser2 = new RoleUser();
		roleUser2.setCompanyId(1L);
		roleUser2.setDeleted(false);
		roleUser2.setRole(role2);
		roleUser2.setUser(user2);
		roleUserDao.save(roleUser2);
		
		Set<User> result = ApiFactory.getAcsService().getUsersWithoutRoleName(bs.getId(),"role_name_1");
		Assert.assertNotNull(result);
	}
	

	@Test
	public void getUsersWithoutRoleCode(){
		com.norteksoft.acs.entity.organization.User user = new com.norteksoft.acs.entity.organization.User();
		user.setLoginName("wangjingewewe");
		user.setCompanyId(1L);
		user.setDeleted(false);
		userDao.save(user);
		 
		BusinessSystem bs = businessSystemDao.findUniqueByProperty("code", "ems");
		com.norteksoft.acs.entity.authorization.Role role = new com.norteksoft.acs.entity.authorization.Role();
		role.setCode("role_code_1");
		role.setName("role_name_1");
		role.setBusinessSystem(bs);
		roleDao.save(role);
		
		RoleUser roleUser = new RoleUser();
		roleUser.setCompanyId(1L);
		roleUser.setDeleted(false);
		roleUser.setRole(role);
		roleUser.setUser(user);
		roleUserDao.save(roleUser);
		
		com.norteksoft.acs.entity.organization.User user2 = new com.norteksoft.acs.entity.organization.User();
		user2.setLoginName("ldxewewe");
		user2.setCompanyId(1L);
		user2.setDeleted(false);
		userDao.save(user2);
		 
		com.norteksoft.acs.entity.authorization.Role role2 = new com.norteksoft.acs.entity.authorization.Role();
		role2.setCode("role_code_2");
		role2.setName("role_name_2");
		role2.setBusinessSystem(bs);
		roleDao.save(role2);
		
		RoleUser roleUser2 = new RoleUser();
		roleUser2.setCompanyId(1L);
		roleUser2.setDeleted(false);
		roleUser2.setRole(role2);
		roleUser2.setUser(user2);
		roleUserDao.save(roleUser2);
		
		Set<User> result = ApiFactory.getAcsService().getUsersWithoutRoleCode(bs.getId(),"role_code_1");
		Assert.assertNotNull(result);
	}
	
	
	@Test
	public void getUsersByRoleCodeExceptTrustedRole(){
		com.norteksoft.acs.entity.organization.User user = new com.norteksoft.acs.entity.organization.User();
		user.setLoginName("wangjingewewe");
		user.setCompanyId(1L);
		user.setDeleted(false);
		userDao.save(user);
		
		BusinessSystem bs = businessSystemDao.findUniqueByProperty("code", "ems");
		com.norteksoft.acs.entity.authorization.Role role = new com.norteksoft.acs.entity.authorization.Role();
		role.setCode("role_code_1");
		role.setName("role_name_1");
		role.setBusinessSystem(bs);
		roleDao.save(role);
		
		RoleUser roleUser = new RoleUser();
		roleUser.setCompanyId(1L);
		roleUser.setConsigner(1111L);
		roleUser.setDeleted(false);
		roleUser.setRole(role);
		roleUser.setUser(user);
		roleUserDao.save(roleUser);
		
		Set<User> result = ApiFactory.getAcsService().getUsersByRoleCodeExceptTrustedRole(bs.getId(),"role_code_1");
		Assert.assertTrue(result.isEmpty());
	}
	
	@Test
	public void getDepartmentsByUserId(){
		com.norteksoft.acs.entity.organization.Department department = new com.norteksoft.acs.entity.organization.Department();
		department.setName("haveUsers");
		department.setCode("haveUsers");
		department.setCompany(companyDao.get(1L));
		department.setDeleted(false);
		departmentDao.save(department);
		
		com.norteksoft.acs.entity.organization.User user = new com.norteksoft.acs.entity.organization.User();
		user.setLoginName("wangjing");
		user.setCompanyId(1L);
		user.setDeleted(false);
		List<UserInfo> userInfos = new ArrayList<UserInfo>();
		UserInfo userInfo = new UserInfo();
		userInfo.setUser(user);
		userInfo.setCompanyId(1L);
		userInfo.setDeleted(false);
		userInfo.setBodyWeight("fsdfsd");
		userInfos.add(userInfo);
		user.setUserInfos(userInfos);
		userInfoDao.save(userInfo);
		userDao.save(user);
		
		DepartmentUser du = new DepartmentUser();
		du.setCompanyId(1L);
		du.setDeleted(false);
		du.setDepartment(department);
		du.setUser(user);
		departmentUserDao.save(du);
		
		com.norteksoft.acs.entity.organization.Department department2 = new com.norteksoft.acs.entity.organization.Department();
		department2.setName("haveUsers2");
		department2.setCode("haveUsers2");
		department2.setCompany(companyDao.get(1L));
		department2.setDeleted(false);
		departmentDao.save(department2);
		
		
		DepartmentUser du2 = new DepartmentUser();
		du2.setCompanyId(1L);
		du2.setDeleted(false);
		du2.setDepartment(department2);
		du2.setUser(user);
		departmentUserDao.save(du2);
		
		Long id = ApiFactory.getAcsService().getUserByLoginName("wangjing").getId();
		List<Department> result = ApiFactory.getAcsService().getDepartmentsByUserId(id);
		Assert.assertNotNull(result);
		Assert.assertTrue(result.size()==2);
	}
	
	@Test
	public void getUserByEmail(){
		com.norteksoft.acs.entity.organization.User user = new com.norteksoft.acs.entity.organization.User();
		user.setLoginName("wangjing");
		user.setCompanyId(1L);
		user.setDeleted(false);
		user.setEmail("wangjing@nortek.com");
		userDao.save(user);
		
		
		User result = ApiFactory.getAcsService().getUserByEmail("wangjing@nortek.com");
		Assert.assertNotNull(result);
		Assert.assertEquals("wangjing@nortek.com", result.getEmail());
	}
	
	
	@Test
	public void getUsersByName(){
		com.norteksoft.acs.entity.organization.User user = new com.norteksoft.acs.entity.organization.User();
		user.setLoginName("wangjing");
		user.setName("用户姓名");
		user.setCompanyId(1L);
		user.setDeleted(false);
		user.setEmail("wangjing@nortek.com");
		userDao.save(user);
		
		List<User> result = ApiFactory.getAcsService().getUsersByName("用户姓名");
		Assert.assertNotNull(result);
		Assert.assertEquals("用户姓名", result.get(0).getName());
	}
	
	@Test
	public void getLoginNamesExclude(){
		com.norteksoft.acs.entity.organization.User user = new com.norteksoft.acs.entity.organization.User();
		user.setLoginName("wangjing");
		user.setCompanyId(1L);
		user.setDeleted(false);
		userDao.save(user);
		
		com.norteksoft.acs.entity.organization.User user2 = new com.norteksoft.acs.entity.organization.User();
		user2.setLoginName("wangjing2");
		user2.setCompanyId(1L);
		user2.setDeleted(false);
		userDao.save(user2);
		
		Set<String> result = ApiFactory.getAcsService().getLoginNamesExclude("wangjing");
		Assert.assertNotNull(result);
		Assert.assertTrue(!result.contains("wangjing"));
	}
	
	

	@Test
	public void getDepartmentsTwo(){
		com.norteksoft.acs.entity.organization.Department department = new com.norteksoft.acs.entity.organization.Department();
		department.setName("haveUsers");
		department.setCode("haveUsers");
		department.setCompany(companyDao.get(1L));
		department.setDeleted(false);
		departmentDao.save(department);
		
		com.norteksoft.acs.entity.organization.User user = new com.norteksoft.acs.entity.organization.User();
		user.setLoginName("wangjing");
		user.setCompanyId(1L);
		user.setDeleted(false);
		List<UserInfo> userInfos = new ArrayList<UserInfo>();
		UserInfo userInfo = new UserInfo();
		userInfo.setUser(user);
		userInfo.setCompanyId(1L);
		userInfo.setDeleted(false);
		userInfo.setBodyWeight("fsdfsd");
		userInfos.add(userInfo);
		user.setUserInfos(userInfos);
		userInfoDao.save(userInfo);
		userDao.save(user);
		
		DepartmentUser du = new DepartmentUser();
		du.setCompanyId(1L);
		du.setDeleted(false);
		du.setDepartment(department);
		du.setUser(user);
		departmentUserDao.save(du);
		
		com.norteksoft.acs.entity.organization.Department department2 = new com.norteksoft.acs.entity.organization.Department();
		department2.setName("haveUsers2");
		department2.setCode("haveUsers2");
		department2.setCompany(companyDao.get(1L));
		department2.setDeleted(false);
		departmentDao.save(department2);
		
		
		DepartmentUser du2 = new DepartmentUser();
		du2.setCompanyId(1L);
		du2.setDeleted(false);
		du2.setDepartment(department2);
		du2.setUser(user);
		departmentUserDao.save(du2);
		
		List<Department> result = ApiFactory.getAcsService().getDepartments("wangjing");
		Assert.assertNotNull(result);
		Assert.assertTrue(result.size()==2);
	}
	
	@Test
	public void getRolesByUserThree(){
		com.norteksoft.acs.entity.organization.User user = new com.norteksoft.acs.entity.organization.User();
		user.setLoginName("wangjingewewe");
		user.setCompanyId(1L);
		user.setDeleted(false);
		userDao.save(user);
		
		com.norteksoft.acs.entity.authorization.Role role = new com.norteksoft.acs.entity.authorization.Role();
		role.setCode("role_code_1");
		role.setName("role_name_1");
		roleDao.save(role);
		
		RoleUser roleUser = new RoleUser();
		roleUser.setCompanyId(1L);
		roleUser.setConsigner(1111L);
		roleUser.setDeleted(false);
		roleUser.setRole(role);
		roleUser.setUser(user);
		roleUserDao.save(roleUser);
		
		Set<Role> result = ApiFactory.getAcsService().getRolesByUser("wangjingewewe");
		Assert.assertNotNull(result);
	}
	
	@Test
	public void getWorkgroupsByUser(){
		com.norteksoft.acs.entity.organization.Workgroup workgroup = new com.norteksoft.acs.entity.organization.Workgroup();
		workgroup.setCode("workgroup_1");
		workgroup.setCompany(companyDao.get(1L));
		workgroup.setName("工作组1");
		workgroupDao.save(workgroup);
		
		com.norteksoft.acs.entity.organization.User user = new com.norteksoft.acs.entity.organization.User();
		user.setLoginName("wangjing");
		user.setCompanyId(1L);
		user.setDeleted(false);
		List<UserInfo> userInfos = new ArrayList<UserInfo>();
		UserInfo userInfo = new UserInfo();
		userInfo.setUser(user);
		userInfo.setCompanyId(1L);
		userInfo.setDeleted(false);
		userInfo.setBodyWeight("fsdfsd");
		userInfos.add(userInfo);
		user.setUserInfos(userInfos);
		userInfoDao.save(userInfo);
		userDao.save(user);
		
		WorkgroupUser wu = new WorkgroupUser();
		wu.setCompanyId(1L);
		wu.setDeleted(false);
		wu.setWorkgroup(workgroup);
		wu.setUser(user);
		workgroupUserDao.save(wu);
		
		com.norteksoft.acs.entity.organization.Workgroup workgroup2 = new com.norteksoft.acs.entity.organization.Workgroup();
		workgroup2.setCode("workgroup_2");
		workgroup2.setCompany(companyDao.get(1L));
		workgroup2.setName("工作组2");
		workgroupDao.save(workgroup2);
		
		WorkgroupUser wu2 = new WorkgroupUser();
		wu2.setCompanyId(1L);
		wu2.setDeleted(false);
		wu2.setWorkgroup(workgroup2);
		wu2.setUser(user);
		workgroupUserDao.save(wu2);
		
		List<Workgroup> result = ApiFactory.getAcsService().getWorkgroupsByUser("wangjing");
		Assert.assertNotNull(result);
		Assert.assertTrue(result.size()==2);
	}
	
	@Test
	public void getUsersByLoginNames(){
		com.norteksoft.acs.entity.organization.User user = new com.norteksoft.acs.entity.organization.User();
		user.setLoginName("wangjinge1111");
		user.setCompanyId(1L);
		user.setDeleted(false);
		userDao.save(user);
		
		com.norteksoft.acs.entity.organization.User user2 = new com.norteksoft.acs.entity.organization.User();
		user2.setLoginName("wangjinge2222");
		user2.setCompanyId(1L);
		user2.setDeleted(false);
		userDao.save(user2);
		
		List<String> loginName = new ArrayList<String>();
		loginName.add("wangjinge1111");
		loginName.add("wangjinge2222");
		List<User> result = ApiFactory.getAcsService().getUsersByLoginNames(1L,loginName);
		Assert.assertNotNull(result);
		Assert.assertTrue(result.size()==2);
	}
	
	
	@Test
	public void getUsersByLoginNamesTwo(){
		com.norteksoft.acs.entity.organization.User user = new com.norteksoft.acs.entity.organization.User();
		user.setLoginName("wangjinge1111");
		user.setCompanyId(1L);
		user.setDeleted(false);
		userDao.save(user);
		
		com.norteksoft.acs.entity.organization.User user2 = new com.norteksoft.acs.entity.organization.User();
		user2.setLoginName("wangjinge2222");
		user2.setCompanyId(1L);
		user2.setDeleted(false);
		userDao.save(user2);
		
		List<String> loginName = new ArrayList<String>();
		loginName.add("wangjinge1111");
		loginName.add("wangjinge2222");
		List<User> result = ApiFactory.getAcsService().getUsersByLoginNames(loginName);
		Assert.assertNotNull(result);
		Assert.assertTrue(result.size()==2);
	}
	
	
	@Test
	public void getRolesListByUserExceptDelegateMain(){
		com.norteksoft.acs.entity.organization.User user = new com.norteksoft.acs.entity.organization.User();
		user.setLoginName("wangjingewewe");
		user.setCompanyId(1L);
		user.setDeleted(false);
		userDao.save(user);
		
		com.norteksoft.acs.entity.authorization.Role role = new com.norteksoft.acs.entity.authorization.Role();
		role.setCode("role_code_1");
		role.setName("role_name_1");
		roleDao.save(role);
		
		RoleUser roleUser = new RoleUser();
		roleUser.setCompanyId(1L);
		roleUser.setConsigner(1111L);
		roleUser.setDeleted(false);
		roleUser.setRole(role);
		roleUser.setUser(user);
		roleUserDao.save(roleUser);
		
		User u = ApiFactory.getAcsService().getUserByLoginName("wangjingewewe");
		List<Role> result = ApiFactory.getAcsService().getRolesListByUserExceptDelegateMain(u);
		Assert.assertNotNull(result);
	}
	
	@Test
	public void getRolesExcludeTrustedRoleTwo(){
		com.norteksoft.acs.entity.organization.User user = new com.norteksoft.acs.entity.organization.User();
		user.setLoginName("wangjingewewe");
		user.setCompanyId(1L);
		user.setDeleted(false);
		userDao.save(user);
		
		com.norteksoft.acs.entity.authorization.Role role = new com.norteksoft.acs.entity.authorization.Role();
		role.setCode("role_code_1");
		role.setName("role_name_1");
		roleDao.save(role);
		
		RoleUser roleUser = new RoleUser();
		roleUser.setCompanyId(1L);
		roleUser.setDeleted(false);
		roleUser.setRole(role);
		roleUser.setUser(user);
		roleUserDao.save(roleUser);
		
		User u = ApiFactory.getAcsService().getUserByLoginName("wangjingewewe");
		List<Role> result = ApiFactory.getAcsService().getRolesExcludeTrustedRole(u.getId());
		Assert.assertNotNull(result);
	}
	
	
	@Test
	public void getParentDepartmentsByUser(){
		com.norteksoft.acs.entity.organization.Department department = new com.norteksoft.acs.entity.organization.Department();
		department.setName("subDepartment");
		department.setCode("subDepartment");
		department.setCompany(companyDao.get(1L));
		department.setDeleted(false);
		
		com.norteksoft.acs.entity.organization.Department parentDepartment = new com.norteksoft.acs.entity.organization.Department();
		parentDepartment.setName("parentDepartment");
		parentDepartment.setCode("parentDepartment");
		parentDepartment.setCompany(companyDao.get(1L));
		parentDepartment.setDeleted(false);
		Set<com.norteksoft.acs.entity.organization.Department> children = new HashSet<com.norteksoft.acs.entity.organization.Department>();
		children.add(department);
		parentDepartment.setChildren(children);
		
		department.setParent(parentDepartment);
		departmentDao.save(department);
		departmentDao.save(parentDepartment);
		
		com.norteksoft.acs.entity.organization.User user = new com.norteksoft.acs.entity.organization.User();
		user.setLoginName("wangjing");
		user.setCompanyId(1L);
		user.setDeleted(false);
		List<UserInfo> userInfos = new ArrayList<UserInfo>();
		UserInfo userInfo = new UserInfo();
		userInfo.setUser(user);
		userInfo.setCompanyId(1L);
		userInfo.setDeleted(false);
		userInfo.setBodyWeight("fsdfsd");
		userInfos.add(userInfo);
		user.setUserInfos(userInfos);
		userInfoDao.save(userInfo);
		userDao.save(user);
		
		DepartmentUser du = new DepartmentUser();
		du.setCompanyId(1L);
		du.setDeleted(false);
		du.setDepartment(department);
		du.setUser(user);
		departmentUserDao.save(du);
		
		List<Department> result = ApiFactory.getAcsService().getParentDepartmentsByUser("wangjing");
		Assert.assertNotNull(result);
		Assert.assertEquals("parentDepartment", result.get(0).getName());
	}
	
	@Test
	public void getTopDepartmentsByUser(){
		com.norteksoft.acs.entity.organization.Department department = new com.norteksoft.acs.entity.organization.Department();
		department.setName("subDepartment");
		department.setCode("subDepartment");
		department.setCompany(companyDao.get(1L));
		department.setDeleted(false);
		
		com.norteksoft.acs.entity.organization.Department parentDepartment = new com.norteksoft.acs.entity.organization.Department();
		parentDepartment.setName("parentDepartment");
		parentDepartment.setCode("parentDepartment");
		parentDepartment.setCompany(companyDao.get(1L));
		parentDepartment.setDeleted(false);
		Set<com.norteksoft.acs.entity.organization.Department> children = new HashSet<com.norteksoft.acs.entity.organization.Department>();
		children.add(department);
		parentDepartment.setChildren(children);
		
		
		com.norteksoft.acs.entity.organization.Department topDepartment = new com.norteksoft.acs.entity.organization.Department();
		topDepartment.setName("topDepartment");
		topDepartment.setCode("topDepartment");
		topDepartment.setCompany(companyDao.get(1L));
		topDepartment.setDeleted(false);
		Set<com.norteksoft.acs.entity.organization.Department> children2 = new HashSet<com.norteksoft.acs.entity.organization.Department>();
		children2.add(parentDepartment);
		topDepartment.setChildren(children2);
		
		department.setParent(parentDepartment);
		parentDepartment.setParent(topDepartment);
		departmentDao.save(department);
		departmentDao.save(parentDepartment);
		departmentDao.save(topDepartment);
		
		com.norteksoft.acs.entity.organization.User user = new com.norteksoft.acs.entity.organization.User();
		user.setLoginName("wangjing");
		user.setCompanyId(1L);
		user.setDeleted(false);
		List<UserInfo> userInfos = new ArrayList<UserInfo>();
		UserInfo userInfo = new UserInfo();
		userInfo.setUser(user);
		userInfo.setCompanyId(1L);
		userInfo.setDeleted(false);
		userInfo.setBodyWeight("fsdfsd");
		userInfos.add(userInfo);
		user.setUserInfos(userInfos);
		userInfoDao.save(userInfo);
		userDao.save(user);
		
		DepartmentUser du = new DepartmentUser();
		du.setCompanyId(1L);
		du.setDeleted(false);
		du.setDepartment(department);
		du.setUser(user);
		departmentUserDao.save(du);
		
		List<Department> result = ApiFactory.getAcsService().getTopDepartmentsByUser("wangjing");
		Assert.assertNotNull(result);
		Assert.assertEquals("topDepartment", result.get(0).getName());
	}
	
	
	@Test
	public void getTopDepartment(){
		com.norteksoft.acs.entity.organization.Department department = new com.norteksoft.acs.entity.organization.Department();
		department.setName("subDepartment");
		department.setCode("subDepartment");
		department.setCompany(companyDao.get(1L));
		department.setDeleted(false);
		
		com.norteksoft.acs.entity.organization.Department parentDepartment = new com.norteksoft.acs.entity.organization.Department();
		parentDepartment.setName("parentDepartment");
		parentDepartment.setCode("parentDepartment");
		parentDepartment.setCompany(companyDao.get(1L));
		parentDepartment.setDeleted(false);
		Set<com.norteksoft.acs.entity.organization.Department> children = new HashSet<com.norteksoft.acs.entity.organization.Department>();
		children.add(department);
		parentDepartment.setChildren(children);
		
		
		com.norteksoft.acs.entity.organization.Department topDepartment = new com.norteksoft.acs.entity.organization.Department();
		topDepartment.setName("topDepartment");
		topDepartment.setCode("topDepartment");
		topDepartment.setCompany(companyDao.get(1L));
		topDepartment.setDeleted(false);
		Set<com.norteksoft.acs.entity.organization.Department> children2 = new HashSet<com.norteksoft.acs.entity.organization.Department>();
		children2.add(parentDepartment);
		topDepartment.setChildren(children2);
		
		department.setParent(parentDepartment);
		parentDepartment.setParent(topDepartment);
		departmentDao.save(department);
		departmentDao.save(parentDepartment);
		departmentDao.save(topDepartment);
		
		Department sub = ApiFactory.getAcsService().getDepartmentByCode("subDepartment");
		Department result = ApiFactory.getAcsService().getTopDepartment(sub);
		Assert.assertNotNull(result);
		Assert.assertEquals("topDepartment", result.getName());
	}
	
	@Test
	public void getSystemByCode(){
		BusinessSystem businessSystem = new BusinessSystem();
		businessSystem.setCode("wangjing_system_code");
		businessSystem.setName("系统姓名");
		businessSystem.setDeleted(false);
		businessSystemDao.save(businessSystem);
		
		com.norteksoft.product.api.entity.BusinessSystem result = ApiFactory.getAcsService().getSystemByCode("wangjing_system_code");
		Assert.assertNotNull(result);
		Assert.assertEquals("wangjing_system_code", result.getCode());
	}
	
	@Test
	public void getSystemById(){
		BusinessSystem businessSystem = new BusinessSystem();
		businessSystem.setCode("wangjing_system_code");
		businessSystem.setName("系统姓名");
		businessSystem.setDeleted(false);
		businessSystemDao.save(businessSystem);
		
		Long id = businessSystemDao.findUniqueByProperty("code", "wangjing_system_code").getId();
		com.norteksoft.product.api.entity.BusinessSystem result = ApiFactory.getAcsService().getSystemById(id);
		Assert.assertNotNull(result);
		Assert.assertEquals("wangjing_system_code", result.getCode());
	}
	
	@Test
	public void deleteUser(){
		com.norteksoft.acs.entity.organization.User user = new com.norteksoft.acs.entity.organization.User();
		user.setLoginName("wangjinghhhhhh");
		user.setCompanyId(1L);
		user.setDeleted(false);
		UserInfo userInfo = new UserInfo();
		userInfo.setDeleted(false);
		userInfo.setCompanyId(1L);
		userInfo.setUser(user);
		List<UserInfo> userInfos = new ArrayList<UserInfo>();
		userInfos.add(userInfo);
		user.setUserInfos(userInfos);
		userInfoDao.save(userInfo);
		userDao.save(user);
		
		Long id = userDao.findUniqueByProperty("loginName", "wangjinghhhhhh").getId();
		ApiFactory.getAcsService().deleteUser(id);
		Object result = userDao.findUnique("from UserInfo u where u.user.loginName=? and u.deleted=?", "wangjinghhhhhh",false);
		Assert.assertTrue(result==null);
	}
	
	@Test
	public void saveDepartment(){
		Department department = new Department();
		department.setName("department_name");
		department.setCode("department_code");
		department.setDeleted(false);
		
		ApiFactory.getAcsService().saveDepartment(department,1L);
		Department result = ApiFactory.getAcsService().getDepartmentByCode("department_code");
		Assert.assertNotNull(result);
		Assert.assertEquals("department_code", result.getCode());
	}
	
	@Test
	public void saveDepartmentTwo(){
		Department department = new Department();
		department.setName("department_name");
		department.setCode("department_code");
		department.setDeleted(false);
		
		ApiFactory.getAcsService().saveDepartment(department);
		Department result = ApiFactory.getAcsService().getDepartmentByCode("department_code");
		Assert.assertNotNull(result);
		Assert.assertEquals("department_code", result.getCode());
	}
	
	@Test
	public void deleteDepartment(){
		Department department = new Department();
		department.setName("department_name");
		department.setCode("department_code");
		department.setDeleted(false);
		
		ApiFactory.getAcsService().saveDepartment(department);
		Department d = ApiFactory.getAcsService().getDepartmentByCode("department_code");
		Assert.assertNotNull(d);
		Assert.assertEquals("department_code", d.getCode());
		ApiFactory.getAcsService().deleteDepartment(d.getId());
		Department result = ApiFactory.getAcsService().getDepartmentByCode("department_code");
		Assert.assertTrue(result==null);
	}
	
	@Test
	public void saveDepartmentUser(){
		com.norteksoft.acs.entity.organization.Department department = new com.norteksoft.acs.entity.organization.Department();
		department.setName("haveUserDepartment");
		department.setCode("haveUserDepartment");
		department.setCompany(companyDao.get(1L));
		department.setDeleted(false);
		departmentDao.save(department);
		
		com.norteksoft.acs.entity.organization.User user = new com.norteksoft.acs.entity.organization.User();
		user.setLoginName("wangjing_one");
		user.setCompanyId(1L);
		user.setDeleted(false);
		List<UserInfo> userInfos = new ArrayList<UserInfo>();
		UserInfo userInfo = new UserInfo();
		userInfo.setUser(user);
		userInfo.setCompanyId(1L);
		userInfo.setDeleted(false);
		userInfo.setBodyWeight("fsdfsd");
		userInfos.add(userInfo);
		user.setUserInfos(userInfos);
		userInfoDao.save(userInfo);
		userDao.save(user);
		
		com.norteksoft.acs.entity.organization.User user2 = new com.norteksoft.acs.entity.organization.User();
		user2.setLoginName("wangjing_two");
		user2.setCompanyId(1L);
		user2.setDeleted(false);
		List<UserInfo> userInfos2 = new ArrayList<UserInfo>();
		UserInfo userInfo2 = new UserInfo();
		userInfo2.setUser(user);
		userInfo2.setCompanyId(1L);
		userInfo2.setDeleted(false);
		userInfo2.setBodyWeight("fsdfsd");
		userInfos2.add(userInfo2);
		user.setUserInfos(userInfos2);
		userInfoDao.save(userInfo2);
		userDao.save(user2);
		
		List<Long> userIds = new ArrayList<Long>();
		userIds.add(ApiFactory.getAcsService().getUserByLoginName("wangjing_one").getId());
		userIds.add(ApiFactory.getAcsService().getUserByLoginName("wangjing_two").getId());
		
		Department d = ApiFactory.getAcsService().getDepartmentByCode("haveUserDepartment");
		ApiFactory.getAcsService().saveDepartmentUser(userIds,d);
		
		DepartmentUser du1 = (DepartmentUser)departmentUserDao.findUnique("from DepartmentUser du where du.user.loginName=?", "wangjing_one");
		DepartmentUser du2 = (DepartmentUser)departmentUserDao.findUnique("from DepartmentUser du where du.user.loginName=?", "wangjing_two");
		Assert.assertNotNull(du1);
		Assert.assertNotNull(du2);
		Assert.assertEquals("haveUserDepartment", du1.getDepartment().getCode());
		Assert.assertEquals("haveUserDepartment", du2.getDepartment().getCode());
	}
	
	@Test
	public void getCurrentUserRolesExcludeTrustedRole(){
		com.norteksoft.acs.entity.authorization.Role role = new com.norteksoft.acs.entity.authorization.Role();
		role.setCode("role_code_1");
		role.setName("role_name_1");
		roleDao.save(role);
		
		RoleUser roleUser = new RoleUser();
		roleUser.setCompanyId(1L);
		roleUser.setDeleted(false);
		roleUser.setRole(role);
		roleUser.setUser(userDao.get(ContextUtils.getUserId()));
		roleUserDao.save(roleUser);
		
		String result = ApiFactory.getAcsService().getCurrentUserRolesExcludeTrustedRole();
		Assert.assertNotNull(result);
		Assert.assertTrue(result.indexOf("role_code_1")!=-1);
	}
	
	@Test
	public void getUserRolesExcludeTrustedRole(){
		com.norteksoft.acs.entity.organization.User user = new com.norteksoft.acs.entity.organization.User();
		user.setLoginName("wangjingewewe");
		user.setCompanyId(1L);
		user.setDeleted(false);
		
		
		com.norteksoft.acs.entity.authorization.Role role = new com.norteksoft.acs.entity.authorization.Role();
		role.setCode("role_code_1");
		role.setName("role_name_1");
		
		
		RoleUser roleUser = new RoleUser();
		roleUser.setCompanyId(1L);
		roleUser.setDeleted(false);
		roleUser.setRole(role);
		roleUser.setUser(user);
		Set<RoleUser> roleUserSet = new HashSet<RoleUser>();
		roleUserSet.add(roleUser);
		user.setRoleUsers(roleUserSet);
		role.setRoleUsers(roleUserSet);
		
		roleDao.save(role);
		userDao.save(user);
		roleUserDao.save(roleUser);
		
		Long id = ApiFactory.getAcsService().getUserByLoginName("wangjingewewe").getId();
		String result = ApiFactory.getAcsService().getUserRolesExcludeTrustedRole(id);
		Assert.assertNotNull(result);
		Assert.assertTrue(result.indexOf("role_code_1")!=-1);
	}
	
	
	@Test
	public void getSystemAdminLoginName(){
		String result = ApiFactory.getAcsService().getSystemAdminLoginName();
		Assert.assertNotNull(result);
		Assert.assertTrue(result.indexOf(".systemAdmin")!=-1);
	}
	
	@Test
	public void getLoginNamesByCompany(){
		com.norteksoft.acs.entity.organization.Department department = new com.norteksoft.acs.entity.organization.Department();
		department.setName("haveUsers");
		department.setCode("haveUsers");
		department.setCompany(companyDao.get(1L));
		department.setDeleted(false);
		
		com.norteksoft.acs.entity.organization.User user = new com.norteksoft.acs.entity.organization.User();
		user.setLoginName("wangjing");
		user.setCompanyId(1L);
		user.setWeight(1);
		user.setDeleted(false);
		List<UserInfo> userInfos = new ArrayList<UserInfo>();
		UserInfo userInfo = new UserInfo();
		userInfo.setUser(user);
		userInfo.setCompanyId(1L);
		userInfo.setDeleted(false);
		userInfo.setBodyWeight("fsdfsd");
		userInfos.add(userInfo);
		user.setUserInfos(userInfos);
		userInfoDao.save(userInfo);
		
		DepartmentUser du = new DepartmentUser();
		du.setCompanyId(1L);
		du.setDeleted(false);
		du.setDepartment(department);
		du.setUser(user);
		
		Set<DepartmentUser> duSet = new HashSet<DepartmentUser>();
		duSet.add(du);
		user.setDepartmentUsers(duSet);
		department.setDepartmentUsers(duSet);
		departmentDao.save(department);
		userDao.save(user);
		departmentUserDao.save(du);
		
		List<String> result = ApiFactory.getAcsService().getLoginNamesByCompany(1L);
		Assert.assertNotNull(result);
		Assert.assertTrue(result.contains("wangjing"));
	}
	
	
	@Test
	public void getLoginNamesByWorkgroup(){
		com.norteksoft.acs.entity.organization.Workgroup workgroup = new com.norteksoft.acs.entity.organization.Workgroup();
		workgroup.setCode("workgroup_1");
		workgroup.setCompany(companyDao.get(1L));
		workgroup.setName("工作组1");
		workgroup.setDeleted(false);
		workgroup.setCompany(companyDao.get(1L));
		
		com.norteksoft.acs.entity.organization.User user = new com.norteksoft.acs.entity.organization.User();
		user.setLoginName("wangjing");
		user.setCompanyId(1L);
		user.setWeight(1);
		user.setDeleted(false);
		List<UserInfo> userInfos = new ArrayList<UserInfo>();
		UserInfo userInfo = new UserInfo();
		userInfo.setUser(user);
		userInfo.setCompanyId(1L);
		userInfo.setDeleted(false);
		userInfo.setBodyWeight("fsdfsd");
		userInfos.add(userInfo);
		user.setUserInfos(userInfos);
		userInfoDao.save(userInfo);
		
		WorkgroupUser wu = new WorkgroupUser();
		wu.setCompanyId(1L);
		wu.setDeleted(false);
		wu.setWorkgroup(workgroup);
		wu.setUser(user);
		
		Set<WorkgroupUser> workgroupSet = new HashSet<WorkgroupUser>();
		workgroupSet.add(wu);
		user.setWorkgroupUsers(workgroupSet);
		workgroup.setWorkgroupUsers(workgroupSet);
		userDao.save(user);
		workgroupDao.save(workgroup);
		workgroupUserDao.save(wu);
		
		List<String> result = ApiFactory.getAcsService().getLoginNamesByWorkgroup(1L);
		Assert.assertNotNull(result);
		Assert.assertTrue(result.contains("wangjing"));
	}
	
	@Test
	public void getParentDepartment(){
		com.norteksoft.acs.entity.organization.Department department = new com.norteksoft.acs.entity.organization.Department();
		department.setName("subDepartment");
		department.setCode("subDepartment");
		department.setCompany(companyDao.get(1L));
		department.setDeleted(false);
		
		com.norteksoft.acs.entity.organization.Department parentDepartment = new com.norteksoft.acs.entity.organization.Department();
		parentDepartment.setName("parentDepartment");
		parentDepartment.setCode("parentDepartment");
		parentDepartment.setCompany(companyDao.get(1L));
		parentDepartment.setDeleted(false);
		Set<com.norteksoft.acs.entity.organization.Department> children = new HashSet<com.norteksoft.acs.entity.organization.Department>();
		children.add(department);
		parentDepartment.setChildren(children);
		
		department.setParent(parentDepartment);
		departmentDao.save(department);
		departmentDao.save(parentDepartment);
		
		Long id = ApiFactory.getAcsService().getDepartmentByCode("subDepartment").getId();
		Department result = ApiFactory.getAcsService().getParentDepartment(id);
		Assert.assertNotNull(result);
		Assert.assertEquals("parentDepartment", result.getCode());
	}
	
	/**
	 * 查询所有人员（包含无部门人员，但不包含系统默认的三员）
	 * @param companyId
	 * @return
	 */
	@Test
	public void getUsersByCompanyWithoutAdmin(){
		com.norteksoft.acs.entity.organization.Department department = new com.norteksoft.acs.entity.organization.Department();
		department.setName("haveUsers");
		department.setCode("haveUsers");
		department.setCompany(companyDao.get(1L));
		department.setDeleted(false);
		departmentDao.save(department);
		
		com.norteksoft.acs.entity.organization.User user = new com.norteksoft.acs.entity.organization.User();
		user.setLoginName("wangjing");
		user.setCompanyId(1L);
		user.setDeleted(false);
		List<UserInfo> userInfos = new ArrayList<UserInfo>();
		UserInfo userInfo = new UserInfo();
		userInfo.setUser(user);
		userInfo.setCompanyId(1L);
		userInfo.setDeleted(false);
		userInfo.setBodyWeight("fsdfsd");
		userInfos.add(userInfo);
		user.setUserInfos(userInfos);
		userInfoDao.save(userInfo);
		userDao.save(user);
		
		DepartmentUser du = new DepartmentUser();
		du.setCompanyId(1L);
		du.setDeleted(false);
		du.setDepartment(department);
		du.setUser(user);
		departmentUserDao.save(du);
		
		com.norteksoft.acs.entity.organization.User user2 = new com.norteksoft.acs.entity.organization.User();
		user2.setLoginName("ldx");
		user2.setCompanyId(1L);
		user2.setDeleted(false);
		List<UserInfo> userInfos2 = new ArrayList<UserInfo>();
		UserInfo userInfo2 = new UserInfo();
		userInfo2.setUser(user2);
		userInfo2.setCompanyId(1L);
		userInfo2.setDeleted(false);
		userInfo2.setBodyWeight("ghhdfh");
		userInfos2.add(userInfo2);
		user2.setUserInfos(userInfos2);
		userInfoDao.save(userInfo2);
		userDao.save(user2);
		
		DepartmentUser du2 = new DepartmentUser();
		du2.setCompanyId(1L);
		du2.setDeleted(false);
		du2.setDepartment(department);
		du2.setUser(user2);
		departmentUserDao.save(du2);
		
		
		com.norteksoft.acs.entity.organization.User userWithoutDepartment = new com.norteksoft.acs.entity.organization.User();
		user2.setLoginName("userWithoutDepartment");
		user2.setCompanyId(1L);
		user2.setDeleted(false);
		List<UserInfo> userInfos3 = new ArrayList<UserInfo>();
		UserInfo userInfo3 = new UserInfo();
		userInfo3.setUser(user2);
		userInfo3.setCompanyId(1L);
		userInfo3.setDeleted(false);
		userInfo3.setBodyWeight("ghhdfh");
		userInfos3.add(userInfo3);
		user2.setUserInfos(userInfos3);
		userInfoDao.save(userInfo3);
		userDao.save(userWithoutDepartment);
		
		List<User> result = ApiFactory.getAcsService().getUsersByCompanyWithoutAdmin();
		Assert.assertNotNull(result);
		User u = ApiFactory.getAcsService().getUserByLoginName("test.systemAdmin");
		Assert.assertFalse(isContain(result,u));
		u = ApiFactory.getAcsService().getUserByLoginName("test.securityAdmin");
		Assert.assertFalse(isContain(result,u));
		u = ApiFactory.getAcsService().getUserByLoginName("test.auditAdmin");
		Assert.assertFalse(isContain(result,u));
	}
	
}
