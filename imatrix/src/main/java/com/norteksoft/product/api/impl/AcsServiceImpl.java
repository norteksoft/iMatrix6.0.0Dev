package com.norteksoft.product.api.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.norteksoft.acs.base.orm.hibernate.SimpleHibernateTemplate;
import com.norteksoft.acs.entity.authorization.RoleDepartment;
import com.norteksoft.acs.entity.authorization.RoleUser;
import com.norteksoft.acs.entity.authorization.RoleWorkgroup;
import com.norteksoft.acs.entity.log.LoginLog;
import com.norteksoft.acs.entity.organization.Company;
import com.norteksoft.acs.entity.organization.DepartmentUser;
import com.norteksoft.acs.entity.organization.UserInfo;
import com.norteksoft.acs.entity.organization.WorkgroupUser;
import com.norteksoft.acs.entity.sysSetting.ServerConfig;
import com.norteksoft.acs.service.authorization.AcsApiManager;
import com.norteksoft.acs.service.organization.CompanyManager;
import com.norteksoft.acs.service.organization.DepartmentManager;
import com.norteksoft.acs.service.organization.UserManager;
import com.norteksoft.product.api.AcsService;
import com.norteksoft.product.api.entity.BusinessSystem;
import com.norteksoft.product.api.entity.Department;
import com.norteksoft.product.api.entity.Role;
import com.norteksoft.product.api.entity.User;
import com.norteksoft.product.api.entity.Workgroup;
import com.norteksoft.product.api.utils.BeanUtil;
import com.norteksoft.product.orm.Page;
import com.norteksoft.product.util.ContextUtils;
import com.norteksoft.product.util.ParameterUtils;
import com.norteksoft.product.util.ThreadParameters;

@Service
@Transactional
public class AcsServiceImpl implements AcsService{
	public final static String DEPARTMENT = "department";
	public final static String WORKGROUP = "workgroup";
	public final static String DELETED = "deleted";
	protected SessionFactory sessionFactory;
	private SimpleHibernateTemplate<com.norteksoft.acs.entity.organization.Department, Long> departmentDao;
	private SimpleHibernateTemplate<com.norteksoft.acs.entity.organization.Workgroup, Long> workGroupDao;
	private SimpleHibernateTemplate<DepartmentUser, Long> depUserDao;
	private SimpleHibernateTemplate<WorkgroupUser, Long> workGroupToUserDao;
	private SimpleHibernateTemplate<com.norteksoft.acs.entity.organization.User, Long> userDao;
	private SimpleHibernateTemplate<UserInfo, Long> userInfoDao;
	private SimpleHibernateTemplate<com.norteksoft.acs.entity.authorization.BusinessSystem, Long> businessSystemDao;
	private SimpleHibernateTemplate<RoleUser, Long> roleUserDao;
	private SimpleHibernateTemplate<com.norteksoft.acs.entity.authorization.Role, Long> roleDao;
	private SimpleHibernateTemplate<LoginLog, Long> loginUserLogDao;
	private SimpleHibernateTemplate<ServerConfig, Long> serverConfigDao;
	@Autowired
	private UserManager userManager;
	@Autowired
	private DepartmentManager departmentManager;
	@Autowired
	private CompanyManager companyManager;
	
	@Autowired
	public void setSessionFactory(SessionFactory sessionFactory) {
		departmentDao = new SimpleHibernateTemplate<com.norteksoft.acs.entity.organization.Department, Long>(sessionFactory, com.norteksoft.acs.entity.organization.Department.class);
		workGroupDao = new SimpleHibernateTemplate<com.norteksoft.acs.entity.organization.Workgroup, Long>(sessionFactory, com.norteksoft.acs.entity.organization.Workgroup.class);
		depUserDao = new SimpleHibernateTemplate<DepartmentUser, Long>(sessionFactory, DepartmentUser.class);
		workGroupToUserDao = new SimpleHibernateTemplate<WorkgroupUser, Long>(sessionFactory, WorkgroupUser.class);
		userDao = new SimpleHibernateTemplate<com.norteksoft.acs.entity.organization.User, Long>(sessionFactory,com.norteksoft.acs.entity.organization.User.class);
		userInfoDao = new SimpleHibernateTemplate<UserInfo, Long>(sessionFactory,UserInfo.class);
		businessSystemDao = new SimpleHibernateTemplate<com.norteksoft.acs.entity.authorization.BusinessSystem, Long>(sessionFactory,com.norteksoft.acs.entity.authorization.BusinessSystem.class);
		roleUserDao = new SimpleHibernateTemplate<RoleUser, Long>(sessionFactory,RoleUser.class);
		roleDao = new SimpleHibernateTemplate<com.norteksoft.acs.entity.authorization.Role, Long>(sessionFactory,com.norteksoft.acs.entity.authorization.Role.class);
		loginUserLogDao = new SimpleHibernateTemplate<LoginLog, Long>(sessionFactory, LoginLog.class); 
		serverConfigDao=new SimpleHibernateTemplate<ServerConfig, Long>(sessionFactory, ServerConfig.class);
	}

	protected SessionFactory getSessionFactory() {
		sessionFactory = (SessionFactory)ContextUtils.getBean("sessionFactory");
		return sessionFactory;
	}
	
	private Long getCompanyId(){
		Long id = ContextUtils.getCompanyId();
		if(id == null) throw new RuntimeException("公司ID为空");
		return id;
	}
	/**
	 * 请使用  getOnlineUserCount()
	 */
	@Deprecated
	public Long getOnlineUserCount(Long companyId){
		return getOnlineUserCount();
	}
	
	/**
	 * 查询在线用户数量
	 * @param companyId
	 * @return
	 */
	public Long getOnlineUserCount(){
		return loginUserLogDao.findLong(
				"select count(u) from LoginLog u where u.exitTime is null and u.companyId=? and u.deleted=?", 
				getCompanyId(), false);
	}
	
	@SuppressWarnings("unchecked")
	public List<Long> getOnlineUserIds(){
		return loginUserLogDao.find(
				"select u.userId from LoginLog u where u.exitTime is null and u.companyId=? and u.deleted=?", 
				getCompanyId(), false);
	}

	/**
	 * 请使用  getDepartments()
	 */
	@Deprecated
	public List<Department> getDepartmentList(Long companyId) {
		return getDepartments();
	}
	
	/**
	 * 查询集团公司下的所有的顶级部门(包含分支机构)
	 * 
	 * @param companyId 公司ID
	 * @return List<Department>
	 */
	@SuppressWarnings("unchecked")
	@Transactional(readOnly = true)
	public List<Department> getDepartments() {
			List<com.norteksoft.acs.entity.organization.Department>	list =  departmentDao.find(
					"FROM Department d WHERE d.company.id=? AND d.deleted=? and d.parent.id is null ORDER BY d.weight desc", 
					getCompanyId(), false);
		return BeanUtil.turnToModelDepartmentList(list);
	}
	/**
	 * 查询集团公司下的所有的顶级部门（不包含分支机构）
	 * @param companyId 公司ID
	 * @return List<Department>
	 */
	@SuppressWarnings("unchecked")
	@Transactional(readOnly = true)
	public List<Department> getDepartmentsByCompany() {
		List<com.norteksoft.acs.entity.organization.Department>	list =  departmentDao.find(
				"FROM Department d WHERE d.company.id=? AND d.deleted=? and d.parent.id is null and d.branch=? ORDER BY d.weight desc", 
				getCompanyId(), false,false);
		return BeanUtil.turnToModelDepartmentList(list);
	}
	
	
	/**
	 * 根据公司ID查询该公司所有的部门
	 * 
	 * @param companyId 公司ID
	 * @return List<Department>
	 */
	@SuppressWarnings("unchecked")
	@Transactional(readOnly = true)
	public List<Department> getAllDepartments() {
		List<com.norteksoft.acs.entity.organization.Department> list =  departmentDao.find(
				"FROM Department d WHERE d.company.id=? AND d.deleted=? and d.branch=? ORDER BY d.weight desc", 
				getCompanyId(), false,false);
		return BeanUtil.turnToModelDepartmentList(list);
	}
	/**
	 * 根据公司ID查询该公司所有的部门
	 * 
	 * @param companyId 公司ID
	 * @return List<Department>
	 */
	@SuppressWarnings("unchecked")
	@Transactional(readOnly = true)
	public List<Long> getAllDepartmentIds() {
		List<Long> list =  departmentDao.find(
				"select d.id FROM Department d WHERE d.company.id=? AND d.deleted=?", 
				getCompanyId(), false,false);
		return list;
	}
	
	/**
	 * 根据公司ID查询该公司所有的分支机构
	 * 
	 * @param companyId 公司ID
	 * @return List<Department>
	 */
	@SuppressWarnings("unchecked")
	@Transactional(readOnly = true)
	public List<Department> getBranchs() {
		List<com.norteksoft.acs.entity.organization.Department> list =  departmentDao.find(
				"FROM Department d WHERE d.company.id=? AND d.deleted=? and d.branch=? ORDER BY d.weight desc", 
				getCompanyId(), false,true);
		return BeanUtil.turnToModelDepartmentList(list);
	}
	
	/**
	 *  请使用  getWorkgroups()
	 */
	@Deprecated
	public List<Workgroup> getWorkGroupList(Long companyId) {
		return getWorkgroups();
	}

	/**
	 * 查询总公司的所有的工作组(不包含分支机构)
	 * 
	 * @param companyId 公司ID
	 * @return List<WorkGroup>
	 */
	@Transactional(readOnly = true)
	public List<Workgroup> getWorkgroups() {
		List<com.norteksoft.acs.entity.organization.Workgroup> workGroupList = workGroupDao.findByCriteria(
				Restrictions.eq("company.id", getCompanyId()), Restrictions.eq(
						DELETED, false),Restrictions.isNull("subCompanyId"));
		return BeanUtil.turnToModelWorkgroupList(workGroupList);
	}
	
	/**
	 * 查询该分支机构下的所有工作组
	 * 
	 * @param companyId 公司ID
	 * @return List<WorkGroup>
	 */
	@Transactional(readOnly = true)
	public List<Workgroup> getWorkgroupsByBranchId(Long branchId) {
		List<com.norteksoft.acs.entity.organization.Workgroup> workGroupList = workGroupDao.findByCriteria(
				Restrictions.eq("company.id", getCompanyId()), Restrictions.eq(
						DELETED, false),Restrictions.eq("subCompanyId", branchId));
		return BeanUtil.turnToModelWorkgroupList(workGroupList);
	}
	
	public List<Workgroup> getWorkgroupsByBranchIds(String branchIds) {
		List<Workgroup> result = new ArrayList<Workgroup>();
		String[] ids = branchIds.split(",");
		for(int i=0;i<ids.length;i++){
			result.addAll(getWorkgroupsByBranchId(Long.parseLong(ids[i])));
		}
		return result;
	}
	/**
	 * 查询总公司的所有的工作组(包含分支机构)
	 * 
	 * @param companyId 公司ID
	 * @return List<WorkGroup>
	 */
	@SuppressWarnings("unchecked")
	@Transactional(readOnly = true)
	public List<Workgroup> getAllWorkgroups() {
		List<com.norteksoft.acs.entity.organization.Workgroup> workGroupList = workGroupDao.find(
				"FROM Workgroup w WHERE w.company.id=? AND w.deleted=?  ORDER BY w.subCompanyId,w.weight", getCompanyId(),false);
		return BeanUtil.turnToModelWorkgroupList(workGroupList);
	}
	/**
	 * 根据公司ID查询该公司所有的工作组
	 * 
	 * @param companyId 公司ID
	 * @return List<WorkGroup>
	 */
	@Transactional(readOnly = true)
	public List<com.norteksoft.acs.entity.organization.Workgroup> getWorkgroupsByCompany() {
		List<com.norteksoft.acs.entity.organization.Workgroup> workGroupList = workGroupDao.findByCriteria(
				Restrictions.eq("company.id", getCompanyId()), Restrictions.eq(
						DELETED, false));
		return workGroupList;
	}
	
	/**
	 * 请使用  getUsersByDepartmentId
	 */
	@Deprecated
	public List<com.norteksoft.acs.entity.organization.User> getUserListByDepartmentId(Long departmentId) {
		if(departmentId == null) throw new RuntimeException("没有给定查询用户集合的查询条件：部门ID");
		String hql = "select u FROM DepartmentUser d join d.user  u WHERE u.deleted=? and d.department.id=? AND d.deleted=? order by u.weight desc";
		return  depUserDao.find(hql, false,departmentId,false);
	}

	
	/**
	 * 根据部门ID查询该部门所有的人员
	 * 
	 * @param departmentId 部门Id
	 * @return List<User>
	 */
	@Transactional(readOnly = true)
	public List<User> getUsersByDepartmentId(Long departmentId) {
		if(departmentId == null) throw new RuntimeException("没有给定查询用户集合的查询条件：部门ID");
		List<Object[]> list = getUsersByDepartment(departmentId);
		return BeanUtil.turnToModelUserList1(list);
	}
	/**
	 * 根据部门ID查询该部门所有的人员
	 * 
	 * @param departmentId 部门Id
	 * @return List<User>
	 */
	@Transactional(readOnly = true)
	@SuppressWarnings("unchecked")
	public List<Object[]> getUsersByDepartment(Long departmentId) {
		if(departmentId == null) throw new RuntimeException("没有给定查询用户集合的查询条件：部门ID");
		String hql = "select u,ui FROM DepartmentUser d join d.user  u join u.userInfos ui WHERE u.deleted=? and d.department.id=? AND d.deleted=? order by u.weight desc";
		return  depUserDao.find(hql, false,departmentId,false);
	}
	/**
	 * 根据部门名称得到部门下用户的登录名
	 * @param departmentId 部门Id
	 * @return List<User>
	 */
	@Transactional(readOnly = true)
	@SuppressWarnings("unchecked")
	public List<String> getUserLoginNamesByDepartmentName(String departmentName) {
		if(StringUtils.isNotEmpty(departmentName)){
			String hql = "select u.loginName FROM DepartmentUser d join d.user  u join u.userInfos ui WHERE u.deleted=? and d.department.name=? AND d.deleted=? order by u.weight desc";
			return  depUserDao.find(hql, false,departmentName,false);
		}else{
			return new ArrayList<String>();
		}
	}
	/**
	 * 根据部门id得到部门下用户的登录名
	 * @param departmentId 部门Id
	 * @return List<User>
	 */
	@Transactional(readOnly = true)
	@SuppressWarnings("unchecked")
	public List<String> getLoginNamesByDepartmentId(Long departmentId) {
		if(departmentId != null){
			String hql = "select u.loginName FROM DepartmentUser d join d.user  u join u.userInfos ui WHERE u.deleted=? and d.department.id=? AND d.deleted=? order by u.weight desc";
			return  depUserDao.find(hql, false,departmentId,false);
		}else{
			return new ArrayList<String>();
		}
	}
	/**
	 * 请使用 getUserLoginNamesByDepartmentName
	 */
	@Deprecated
	public List<String> getUserLoginNameListByDepartmentName(String departmentName,Long companyId) {
		return getUserLoginNamesByDepartmentName(departmentName);
	}
	

	/**
	 * 请使用 getUsersByWorkgroupId
	 */
	@Deprecated
	public List<com.norteksoft.acs.entity.organization.User> getUserListByWorkGroupId(Long workgroupId) {
		if(workgroupId == null) throw new RuntimeException("没有给定查询用户集合的查询条件： 工作组ID");
		String hql = "select u FROM WorkgroupUser d join d.user u WHERE u.deleted=? and  d.workgroup.id=? AND d.deleted=? ORDER BY d.user.weight DESC";
		return workGroupDao.find(hql,false, workgroupId,false);
	}
	
	/**
	 * 根据工作组ID查询该工作组所有的人员
	 * 
	 * @param workGroupId 工作组Id
	 * @return List<User>
	 */
	@Transactional(readOnly = true)
	@SuppressWarnings("unchecked")
	public List<User> getUsersByWorkgroupId(Long workgroupId) {
		if(workgroupId == null) throw new RuntimeException("没有给定查询用户集合的查询条件： 工作组ID");
		String hql = "select u FROM WorkgroupUser d join d.user u WHERE u.deleted=? and  d.workgroup.id=? AND d.deleted=? ORDER BY d.user.weight DESC";
		List<com.norteksoft.acs.entity.organization.User> list = workGroupDao.find(hql,false, workgroupId,false);
		return BeanUtil.turnToModelUserList(list);
	}

	/**
	 * 根据父部门id查询该父部门下所有子部门
	 */
	@SuppressWarnings("unchecked")
	@Transactional(readOnly = true)
	public List<Department> getSubDepartmentList(Long paternDepartmentId) {
		if(paternDepartmentId == null) throw new RuntimeException("没有给定查询子部门集合的查询条件： 父部门ID");
		List<com.norteksoft.acs.entity.organization.Department> list =  departmentDao.find(
				"FROM Department d WHERE d.parent.id=? AND d.deleted=?  ORDER BY d.weight desc", 
				paternDepartmentId, false);
		return BeanUtil.turnToModelDepartmentList(list);
	}

	/**
	 * 根据用户Id得到用户实体
	 * @return User
	 */
	@Transactional(readOnly = true)
	public User getUserById(Long id) {
		if(id==null)return null;
		if (id instanceof Long)
			return BeanUtil.turnToModelUser(userDao.get(id));
		return null;
	}

	/**
	 * 获取当前用户所有角色的字符串表示形式(即角色编码以逗号隔开)
	 * @return String
	 */
	@Transactional(readOnly = true)
	public String getCurrentUserRoles(){
		Long userId = ContextUtils.getUserId();
		if(userId == null) return "";
		
		com.norteksoft.acs.entity.organization.User user = userDao.get(userId);
		if(user == null) return "";
		
		return getRoleCodesFromUser(user);
	}
	
	@Transactional(readOnly = true)
	public String getCurrentUserRoles(Long userId){
		com.norteksoft.acs.entity.organization.User user = userDao.get(userId);
		if(user == null) return "";
		
		return getRoleCodesFromUser(user);
	}
	
	@Deprecated
	public Set<Role> getRolesByUserId(Long userId,Long consigner,Long companyId){
		return getTrustedRolesByUserId(userId, consigner);
	}
	
	/**
	 * 查询用户委托的角色。 
	 * @param userId
	 * @param sourceId
	 * @param companyId
	 * @return
	 */
	@Transactional(readOnly = true)
	@SuppressWarnings("unchecked")
	public Set<Role> getTrustedRolesByUserId(Long trusteeId, Long trustorId){
		if(trusteeId == null) throw new RuntimeException("没有给定查询委托角色的查询条件：受托人ID");
		if(trustorId == null) throw new RuntimeException("没有给定查询委托角色的查询条件：委托人ID");
		String hql="FROM RoleUser ru WHERE ru.consigner=? AND ru.user.id=? AND ru.companyId=?";
		List<RoleUser> roleUsers = roleUserDao.find(hql, trustorId, trusteeId, getCompanyId());
		Set<Role> roles = new HashSet<Role>();
		for(RoleUser ru : roleUsers){
			Role role=BeanUtil.turnToModelRole(roleDao.get(ru.getRole().getId()));
			roles.add(role);
		}
		return roles;
	}
	
	/**
	 * 根据用户获取用户的角色字符串形式（不含委托）
	 */
	@Deprecated
	public String getRoleCodesFromUser(com.norteksoft.acs.entity.organization.User user){
		return getRolesExcludeTrustedRole(user);
	}
	
	@Deprecated
	public String getRolesExcludeTrustedRole(com.norteksoft.acs.entity.organization.User user){
		if(user == null) return "";
		Set<com.norteksoft.acs.entity.authorization.Role> roles = new HashSet<com.norteksoft.acs.entity.authorization.Role>();
		// 用户具有的角色
		Set<RoleUser> roleUsers = user.getRoleUsers();
		for(RoleUser ru : roleUsers){
			if(ru.isDeleted()) continue;
			if(ru.getConsigner()!= null) continue;
			com.norteksoft.acs.entity.authorization.Role role = ru.getRole();
			if(!role.isDeleted()) roles.add(role);
		}
		// 用户具有的部门拥有的角色
		Set<DepartmentUser> departmentUsers =  user.getDepartmentUsers();
		for(DepartmentUser du : departmentUsers){
			if(du.isDeleted() || du.getDepartment().isDeleted()) continue;
			for(RoleDepartment rd : du.getDepartment().getRoleDepartments()){
				if(!rd.isDeleted() && !rd.getRole().isDeleted()) roles.add(rd.getRole());
			}
		}
		// 用户具有的分支机构拥有的角色
		if(user.getSubCompanyId()!=null){
			Set<RoleDepartment> rds=departmentDao.get(user.getSubCompanyId()).getRoleDepartments();
			for(RoleDepartment rd : rds){
				if(!rd.isDeleted() && !rd.getRole().isDeleted()) roles.add(rd.getRole());
			}
		}
		// 用户具有的工作组拥有的角色
		Set<WorkgroupUser> workgroupUsers = user.getWorkgroupUsers();
		for(WorkgroupUser wu : workgroupUsers){
			if(wu.isDeleted() || wu.getWorkgroup().isDeleted()) continue;
			for(RoleWorkgroup rw : wu.getWorkgroup().getRoleWorkgroups()){
				if(!rw.isDeleted() && !rw.getRole().isDeleted()) roles.add(rw.getRole());
			}
		}
		List< com.norteksoft.acs.entity.authorization.Role> roleList = new ArrayList<com.norteksoft.acs.entity.authorization.Role>();
		roleList.addAll(roles);
		//角色按权重排序
		sortRoles(roleList);
		// 生成字符串形式
		StringBuilder roleStrings = new StringBuilder();
		for(com.norteksoft.acs.entity.authorization.Role role : roleList){
			roleStrings.append(role.getCode()).append(",");
		}
		// 去掉最后一个逗号
		if(roleStrings.lastIndexOf(",") != -1 && roleStrings.lastIndexOf(",") == roleStrings.length()-1){
			roleStrings.replace(roleStrings.length()-1, roleStrings.length(), "");
		}
		return roleStrings.toString();
	}
	
	@SuppressWarnings("unchecked")
	public String getRolesExcludeTrustedRole(User user){
		if(user == null) return "";
		Set<com.norteksoft.acs.entity.authorization.Role> roles = new HashSet<com.norteksoft.acs.entity.authorization.Role>();
		// 用户具有的角色
		String hql = "select r from Role r join r.roleUsers ru where r.deleted=? and ru.deleted=? and ru.user is not null and ru.user.id=? and ru.consigner is not null";
		List<com.norteksoft.acs.entity.authorization.Role> userRoles = roleDao.find(hql, false,false,user.getId());
		roles.addAll(userRoles);
		// 用户具有的部门拥有的角色
		 hql = "select r from Role r join r.roleDepartments rd join rd.department d join d.departmentUsers du where r.deleted=? and rd.deleted=? and d.deleted=? and du.deleted=? and du.user is not null and du.user.id=?";
		 List<com.norteksoft.acs.entity.authorization.Role> deptRoles = roleDao.find(hql, false,false,false,false,user.getId());
		 roles.addAll(deptRoles);
		 // 用户具有的分支机构拥有的角色
		 if(user.getSubCompanyId()!=null){
			 hql = "select r from RoleDepartment du join du.role r join du.department d where r.deleted=? and du.deleted=? and d.deleted=? and d.branch=? and d.id is (select u.subCompanyId from User u where u.id=? and u.deleted=? and u.subCompanyId is not null)";
			 List<com.norteksoft.acs.entity.authorization.Role> branchesRoles = roleDao.find(hql, false, false, false,true,user.getId(), false);
			 roles.addAll(branchesRoles);
		 }
		// 用户具有的工作组拥有的角色
		 hql = "select r from Role r join r.roleWorkgroups rw join rw.workgroup wg join wg.workgroupUsers wu  where r.deleted=? and rw.deleted=? and wg.deleted=? and wu.deleted=?  and wu.user is not null and wu.user.id=?";
		 List<com.norteksoft.acs.entity.authorization.Role> workgroupRoles  = roleDao.find(hql, false,false,false,false,user.getId());
		 roles.addAll(workgroupRoles);
		List< com.norteksoft.acs.entity.authorization.Role> roleList = new ArrayList<com.norteksoft.acs.entity.authorization.Role>();
		roleList.addAll(roles);
		//角色按权重排序
		sortRoles(roleList);
		// 生成字符串形式
		StringBuilder roleStrings = new StringBuilder();
		for(com.norteksoft.acs.entity.authorization.Role role : roleList){
			roleStrings.append(role.getCode()).append(",");
		}
		// 去掉最后一个逗号
		if(roleStrings.lastIndexOf(",") != -1 && roleStrings.lastIndexOf(",") == roleStrings.length()-1){
			roleStrings.replace(roleStrings.length()-1, roleStrings.length(), "");
		}
		return roleStrings.toString();
	}
	
	/**
	 * 根据用户获取用户的角色
	 * @param userId
	 * @return
	 */
	@Transactional(readOnly = true)
	public Set<Role> getRolesByUser(Long userId){
		com.norteksoft.acs.entity.organization.User user = userDao.get(userId);
		if(user == null) return null;
		User modeUser = BeanUtil.turnToModelUser(user);
		return getRolesByUser(modeUser);
	}
	/**
	 * 根据用户获取用户的角色
	 * @param userId
	 * @return
	 */
	@Transactional(readOnly = true)
	public Set<String> getRoleNamesByUser(Long userId){
		com.norteksoft.acs.entity.organization.User user = userDao.get(userId);
		if(user == null) return null;
		User modeUser = BeanUtil.turnToModelUser(user);
		return getRoleNamesByUser(modeUser);
	}
	/**
	 * 根据用户获取用户的角色
	 * @param userId
	 * @return
	 */
	@Transactional(readOnly = true)
	public Set<String> getRoleCodesByUser(Long userId){
		com.norteksoft.acs.entity.organization.User user = userDao.get(userId);
		if(user == null) return null;
		User modeUser = BeanUtil.turnToModelUser(user);
		return getRoleCodesByUser(modeUser);
	}
	/**
	 * 根据用户获取用户的角色
	 * @param userId
	 * @return
	 */
	@Transactional(readOnly = true)
	public Set<Long> getRoleIdsByUser(Long userId){
		com.norteksoft.acs.entity.organization.User user = userDao.get(userId);
		if(user == null) return null;
		User modeUser = BeanUtil.turnToModelUser(user);
		return getRoleIdsByUser(modeUser);
	}
	
	@Deprecated
	public Set<Role> getRolesByUser(com.norteksoft.acs.entity.organization.User user){
		if(user == null) return null;
		
		Set<Role> roles = new HashSet<Role>();
		// 用户具有的角色
		Set<RoleUser> roleUsers = user.getRoleUsers();
		for(RoleUser ru : roleUsers){
			if(ru.isDeleted()) continue;
			if(ru.getConsigner() != null) continue;
			Role role = BeanUtil.turnToModelRole(ru.getRole());
			if(!role.isDeleted()) roles.add(role);
		}
		// 用户具有的部门拥有的角色
		Set<DepartmentUser> departmentUsers =  user.getDepartmentUsers();
		for(DepartmentUser du : departmentUsers){
			if(du.isDeleted() || du.getDepartment().isDeleted()) continue;
			for(RoleDepartment rd : du.getDepartment().getRoleDepartments()){
				if(!rd.isDeleted() && !rd.getRole().isDeleted()) roles.add(BeanUtil.turnToModelRole(rd.getRole()));
			}
		}
		// 用户具有的分支机构拥有的角色
		if(user.getSubCompanyId()!=null){
			Set<RoleDepartment> rds=departmentDao.get(user.getSubCompanyId()).getRoleDepartments();
			for(RoleDepartment rd : rds){
				if(!rd.isDeleted() && !rd.getRole().isDeleted()) roles.add(BeanUtil.turnToModelRole(rd.getRole()));
			}
		}
		// 用户具有的工作组拥有的角色
		Set<WorkgroupUser> workgroupUsers = user.getWorkgroupUsers();
		for(WorkgroupUser wu : workgroupUsers){
			if(wu.isDeleted() || wu.getWorkgroup().isDeleted()) continue;
			for(RoleWorkgroup rw : wu.getWorkgroup().getRoleWorkgroups()){
				if(!rw.isDeleted() && !rw.getRole().isDeleted()) roles.add(BeanUtil.turnToModelRole(rw.getRole()));
			}
		}
		return roles;
	}
	
	/**
	 * 根据用户查询用户的角色（不含委托）
	 * @param user
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public Set<Role> getRolesByUser(User user){
		if(user == null) return null;
		
		Set<Role> roles = new HashSet<Role>();
		Set<com.norteksoft.acs.entity.authorization.Role> oldRoles = new HashSet<com.norteksoft.acs.entity.authorization.Role>();
		// 用户具有的角色
		String hql = "select r from Role r join r.roleUsers ru where r.deleted=? and ru.deleted=? and ru.user is not null and ru.user.id=? and ru.consigner is null";
		List<com.norteksoft.acs.entity.authorization.Role> userRoles = roleDao.find(hql, false,false,user.getId());
		oldRoles.addAll(userRoles);
		// 用户具有的部门拥有的角色
		 hql = "select r from Role r join r.roleDepartments rd join rd.department d join d.departmentUsers du where r.deleted=? and rd.deleted=? and d.deleted=? and du.deleted=? and du.user is not null and du.user.id=?";
		 List<com.norteksoft.acs.entity.authorization.Role> deptRoles = roleDao.find(hql, false,false,false,false,user.getId());
		 oldRoles.addAll(deptRoles);
		 // 用户具有的分支机构拥有的角色
		 if(user.getSubCompanyId()!=null){
			 hql = "select r from RoleDepartment du join du.role r join du.department d where r.deleted=? and du.deleted=? and d.deleted=? and d.branch=? and d.id is (select u.subCompanyId from User u where u.id=? and u.deleted=? and u.subCompanyId is not null)";
			 List<com.norteksoft.acs.entity.authorization.Role> branchesRoles = roleDao.find(hql, false, false, false,true,user.getId(), false);
			 oldRoles.addAll(branchesRoles);
		 }
		// 用户具有的工作组拥有的角色
		 hql = "select r from Role r join r.roleWorkgroups rw join rw.workgroup wg join wg.workgroupUsers wu  where r.deleted=? and rw.deleted=? and wg.deleted=? and wu.deleted=?  and wu.user is not null and wu.user.id=?";
		 List<com.norteksoft.acs.entity.authorization.Role> workgroupRoles  = roleDao.find(hql, false,false,false,false,user.getId());
		 oldRoles.addAll(workgroupRoles);
		 roles = BeanUtil.turnToModelRoleSet(oldRoles);
		return roles;
	}
	/**
	 * 根据用户查询用户的角色名称（不含委托）
	 * @param user
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public Set<String> getRoleNamesByUser(User user){
		if(user == null) return null;
		
		Set<String> roles = new HashSet<String>();
		// 用户具有的角色
		String hql = "select r.name from Role r join r.roleUsers ru where r.deleted=? and ru.deleted=? and ru.user is not null and ru.user.id=? and ru.consigner is null";
		List<String> userRoles = roleDao.find(hql, false,false,user.getId());
		roles.addAll(userRoles);
		// 用户具有的部门拥有的角色
		hql = "select r.name from Role r join r.roleDepartments rd join rd.department d join d.departmentUsers du where r.deleted=? and rd.deleted=? and d.deleted=? and du.deleted=? and du.user is not null and du.user.id=?";
		List<String> deptRoles = roleDao.find(hql, false,false,false,false,user.getId());
		roles.addAll(deptRoles);
		// 用户具有的分支机构拥有的角色
		if(user.getSubCompanyId()!=null){
			hql = "select r.name from RoleDepartment du join du.role r join du.department d where r.deleted=? and du.deleted=? and d.deleted=? and d.branch=? and d.id is (select u.subCompanyId from User u where u.id=? and u.deleted=? and u.subCompanyId is not null)";
			List<String> branchesRoles = roleDao.find(hql, false, false, false,true,user.getId(), false);
			roles.addAll(branchesRoles);
		}
		// 用户具有的工作组拥有的角色
		hql = "select r.name from Role r join r.roleWorkgroups rw join rw.workgroup wg join wg.workgroupUsers wu  where r.deleted=? and rw.deleted=? and wg.deleted=? and wu.deleted=?  and wu.user is not null and wu.user.id=?";
		List<String> workgroupRoles  = roleDao.find(hql, false,false,false,false,user.getId());
		roles.addAll(workgroupRoles);
		return roles;
	}
	/**
	 * 根据用户查询用户的角色编码（不含委托）
	 * @param user
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public Set<String> getRoleCodesByUser(User user){
		if(user == null) return null;
		
		Set<String> roles = new HashSet<String>();
		// 用户具有的角色
		String hql = "select r.code from Role r join r.roleUsers ru where r.deleted=? and ru.deleted=? and ru.user is not null and ru.user.id=? and ru.consigner is null";
		List<String> userRoles = roleDao.find(hql, false,false,user.getId());
		roles.addAll(userRoles);
		// 用户具有的部门拥有的角色
		hql = "select r.code from Role r join r.roleDepartments rd join rd.department d join d.departmentUsers du where r.deleted=? and rd.deleted=? and d.deleted=? and du.deleted=? and du.user is not null and du.user.id=?";
		List<String> deptRoles = roleDao.find(hql, false,false,false,false,user.getId());
		roles.addAll(deptRoles);
		// 用户具有的分支机构拥有的角色
		if(user.getSubCompanyId()!=null){
			hql = "select r.code from RoleDepartment du join du.role r join du.department d where r.deleted=? and du.deleted=? and d.deleted=? and d.branch=? and d.id is (select u.subCompanyId from User u where u.id=? and u.deleted=? and u.subCompanyId is not null)";
			List<String> branchesRoles = roleDao.find(hql, false, false, false,true,user.getId(), false);
			roles.addAll(branchesRoles);
		}
		// 用户具有的工作组拥有的角色
		hql = "select r.code from Role r join r.roleWorkgroups rw join rw.workgroup wg join wg.workgroupUsers wu  where r.deleted=? and rw.deleted=? and wg.deleted=? and wu.deleted=?  and wu.user is not null and wu.user.id=?";
		List<String> workgroupRoles  = roleDao.find(hql, false,false,false,false,user.getId());
		roles.addAll(workgroupRoles);
		return roles;
	}
	/**
	 * 根据用户查询用户的角色名称（不含委托）
	 * @param user
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public Set<Long> getRoleIdsByUser(User user){
		if(user == null) return null;
		
		Set<Long> roles = new HashSet<Long>();
		// 用户具有的角色
		String hql = "select r.id from Role r join r.roleUsers ru where r.deleted=? and ru.deleted=? and ru.user is not null and ru.user.id=? and ru.consigner is null";
		List<Long> userRoles = roleDao.find(hql, false,false,user.getId());
		roles.addAll(userRoles);
		// 用户具有的部门拥有的角色
		hql = "select r.id from Role r join r.roleDepartments rd join rd.department d join d.departmentUsers du where r.deleted=? and rd.deleted=? and d.deleted=? and du.deleted=? and du.user is not null and du.user.id=?";
		List<Long> deptRoles = roleDao.find(hql, false,false,false,false,user.getId());
		roles.addAll(deptRoles);
		// 用户具有的分支机构拥有的角色
		if(user.getSubCompanyId()!=null){
			hql = "select r.id from RoleDepartment du join du.role r join du.department d where r.deleted=? and du.deleted=? and d.deleted=? and d.branch=? and d.id is (select u.subCompanyId from User u where u.id=? and u.deleted=? and u.subCompanyId is not null)";
			List<Long> branchesRoles = roleDao.find(hql, false, false, false,true,user.getId(), false);
			roles.addAll(branchesRoles);
		}
		// 用户具有的工作组拥有的角色
		hql = "select r.id from Role r join r.roleWorkgroups rw join rw.workgroup wg join wg.workgroupUsers wu  where r.deleted=? and rw.deleted=? and wg.deleted=? and wu.deleted=?  and wu.user is not null and wu.user.id=?";
		List<Long> workgroupRoles  = roleDao.find(hql, false,false,false,false,user.getId());
		roles.addAll(workgroupRoles);
		return roles;
	}
	
	
	@Transactional(readOnly = true)
	public List<Role> getRolesListByUser(Long userId){
		com.norteksoft.acs.entity.organization.User user = userDao.get(userId);
		if(user == null) return null;
		User modeUser = BeanUtil.turnToModelUser(user);
		return getRolesListByUser(modeUser);
	}
	
	@Deprecated
	public List<Role> getRolesListByUser(com.norteksoft.acs.entity.organization.User user){
		if(user == null) return null;
		
		List<Role> roles = new ArrayList<Role>();
		// 用户具有的角色
		Set<RoleUser> roleUsers = user.getRoleUsers();
		for(RoleUser ru : roleUsers){
			if(ru.isDeleted()) continue;
			if(ru.getConsigner() != null) continue;
			Role role = BeanUtil.turnToModelRole(ru.getRole());
			if(!role.isDeleted()) {
				if(!roles.contains(role)){
					roles.add(role);
				}
			}
		}
		// 用户具有的部门拥有的角色
		Set<DepartmentUser> departmentUsers =  user.getDepartmentUsers();
		for(DepartmentUser du : departmentUsers){
			if(du.isDeleted() || du.getDepartment().isDeleted()) continue;
			for(RoleDepartment rd : du.getDepartment().getRoleDepartments()){
				if(!rd.isDeleted() && !rd.getRole().isDeleted()) {
					if(!roles.contains(rd.getRole())){
						roles.add(BeanUtil.turnToModelRole(rd.getRole()));
					}
				}
			}
		}
		// 用户具有的分支机构拥有的角色
		if(user.getSubCompanyId()!=null){
			Set<RoleDepartment> rds=departmentDao.get(user.getSubCompanyId()).getRoleDepartments();
			for(RoleDepartment rd : rds){
				if(!rd.isDeleted() && !rd.getRole().isDeleted()){
					if(!roles.contains(rd.getRole())){
						roles.add(BeanUtil.turnToModelRole(rd.getRole()));
					}
				} 
			}
		}
		// 用户具有的工作组拥有的角色
		Set<WorkgroupUser> workgroupUsers = user.getWorkgroupUsers();
		for(WorkgroupUser wu : workgroupUsers){
			if(wu.isDeleted() || wu.getWorkgroup().isDeleted()) continue;
			for(RoleWorkgroup rw : wu.getWorkgroup().getRoleWorkgroups()){
				if(!rw.isDeleted() && !rw.getRole().isDeleted()) {
					if(!roles.contains(rw.getRole())){
						roles.add(BeanUtil.turnToModelRole(rw.getRole()));
					}
				}
			}
		}
		//角色按权重排序
		sortRole(roles);
		return roles;
	}
	
	/**
	 * 根据用户查询用户角色（不含委托）
	 * @param user
	 * @return
	 */
	public List<Role> getRolesListByUser(User user){
		if(user == null) return null;
		return getRolesListByUserExceptDelegateMain(user);
	}
	
	/**
	 * 获取租户名称
	 * @param businessSystemId
	 * @return
	 */
	public String getBusinessSystemNameById(Long businessSystemId){
		com.norteksoft.acs.entity.authorization.BusinessSystem entity = businessSystemDao.get(businessSystemId);
		if(entity==null){
			return "";
		}else{
			return entity.getName();
		}
	}
	/**
	 * 获取不属于任何部门的用户
	 * @return List<User>
	 */
	@Transactional(readOnly = true)
	public List<User> getUsersNotInDepartment(Long companyId){
		return getUsersWithoutDepartment();
	}
	//全公司的无部门用户
	public List<User> getUsersWithoutDepartment(){
		StringBuilder sqlString = new StringBuilder();
		sqlString.append("SELECT u.* FROM ACS_USERINFO ");
		sqlString.append("inner join ACS_USER u on ACS_USERINFO.FK_USER_ID=u.id ");
		sqlString.append("LEFT OUTER JOIN ");
		sqlString.append("(SELECT * FROM ACS_DEPARTMENT_USER WHERE ACS_DEPARTMENT_USER.DELETED = 0)");
		sqlString.append(" DEPT_USER ON ACS_USERINFO.FK_USER_ID = DEPT_USER.FK_USER_ID ");
		sqlString.append("WHERE ACS_USERINFO.DELETED=0 AND ACS_USERINFO.FK_COMPANY_ID = ? ");
		sqlString.append("AND DEPT_USER.ID IS NULL AND u.sub_company_id is null ORDER BY ACS_USERINFO.ID DESC ");
		
		return BeanUtil.turnToModelUserList(userDao.findByJdbc(sqlString.toString(), ContextUtils.getCompanyId()));
	}
	//全公司的无部门用户
	public List<Long> getUserIdsWithoutDepartment(){
		StringBuilder sqlString = new StringBuilder();
		sqlString.append("SELECT u.* FROM ACS_USERINFO ");
		sqlString.append("inner join ACS_USER u on ACS_USERINFO.FK_USER_ID=u.id ");
		sqlString.append("LEFT OUTER JOIN ");
		sqlString.append("(SELECT * FROM ACS_DEPARTMENT_USER WHERE ACS_DEPARTMENT_USER.DELETED = 0)");
		sqlString.append(" DEPT_USER ON ACS_USERINFO.FK_USER_ID = DEPT_USER.FK_USER_ID ");
		sqlString.append("WHERE ACS_USERINFO.DELETED=0 AND ACS_USERINFO.FK_COMPANY_ID = ? ");
		sqlString.append("AND DEPT_USER.ID IS NULL AND u.sub_company_id is null ORDER BY ACS_USERINFO.ID DESC ");
		
		List<Long> userIds = new ArrayList<Long>();
		List<com.norteksoft.acs.entity.organization.User> users = userDao.findByJdbc(sqlString.toString(), ContextUtils.getCompanyId());
		for(com.norteksoft.acs.entity.organization.User u:users){
			userIds.add(u.getId());
		}
		return userIds;
	}
	
	//分支机构的无部门用户
	public List<User> getUsersWithoutBranch(Long branchId){
		StringBuilder sqlString = new StringBuilder();
		sqlString.append("SELECT u.* FROM ACS_USERINFO ");
		sqlString.append("inner join ACS_USER u on ACS_USERINFO.FK_USER_ID=u.id ");
		sqlString.append("LEFT OUTER JOIN ");
		sqlString.append("(SELECT * FROM ACS_DEPARTMENT_USER WHERE ACS_DEPARTMENT_USER.DELETED = 0)");
		sqlString.append(" DEPT_USER ON ACS_USERINFO.FK_USER_ID = DEPT_USER.FK_USER_ID ");
		sqlString.append("WHERE ACS_USERINFO.DELETED=0 AND ACS_USERINFO.FK_COMPANY_ID = ? ");
		sqlString.append("AND DEPT_USER.FK_DEPARTMENT_ID=? AND DEPT_USER.SUB_COMPANY_ID=? ORDER BY ACS_USERINFO.ID DESC ");
		
		return BeanUtil.turnToModelUserList(userDao.findByJdbc(sqlString.toString(), ContextUtils.getCompanyId(),branchId,branchId));
	}
	
	/**
	 * 获取不属于任何部门的用户
	 * @return page
	 */
	public Page<UserInfo> getNoDepartmentUsers(Page<UserInfo> page,Long companyId){
        if(companyId == null) return null;
		
		StringBuilder sqlString = new StringBuilder();
		sqlString.append("SELECT * FROM ACS_USER LEFT OUTER JOIN ");
		sqlString.append("(SELECT * FROM ACS_DEPARTMENT_USER WHERE ACS_DEPARTMENT_USER.DELETED = 0)");
		sqlString.append(" DEPT_USER ON ACS_USER.ID = DEPT_USER.FK_USER_ID ");
		sqlString.append("WHERE ACS_USER.DELETED=0 AND ACS_USER.FK_COMPANY_ID = ? ");
		sqlString.append("AND DEPT_USER.ID IS NULL ORDER BY ACS_USER.WEIGHT DESC");
		List<com.norteksoft.acs.entity.organization.User>us =userDao.findByJdbc(sqlString.toString(), companyId);
		List<UserInfo> uiList = new ArrayList<UserInfo>();
		for(com.norteksoft.acs.entity.organization.User u : us){
			UserInfo ui= (UserInfo)userInfoDao.findUnique("from UserInfo ui where ui.user.id=? and ui.companyId=? ",u.getId() ,companyId);
			uiList.add(ui);
		}
		page.setResult(uiList);
		page.setPageSize(15);
		return page;
	}
	
	
	/**
	 * 将角色授权给别人，自己还保留该角色
	 * @param someoneId 受权人
	 * @param roleIds 角色id数组
	 * @param companyId
	 * @param sourceUserId //授权人
	 */
	
	public void assignRolesToSomeone(Long someoneId,String[] roleIds,Long companyId,Long sourceUserId){
		assignTrustedRole(sourceUserId, roleIds, someoneId);
	}
	
	public void assignTrustedRole(Long trustorId, String[]roleIds, Long trusteeId){
		if(trustorId == null) throw new RuntimeException("没有给定委托角色时的委托人");
		if(roleIds == null) throw new RuntimeException("没有给定需要委托的角色集合");
		if(trusteeId == null) throw new RuntimeException("没有给定委托角色时的受托人");
		for(int i=0;i<roleIds.length;i++){
			if(StringUtils.isNotEmpty(roleIds[i])){
				if((getRoleUserBySourceId(trusteeId, Long.parseLong(roleIds[i]), getCompanyId(),trustorId))==null){
					RoleUser roleUser = new RoleUser();
					com.norteksoft.acs.entity.authorization.Role role = roleDao.get(Long.parseLong(roleIds[i]));
					com.norteksoft.acs.entity.organization.User user = userDao.get(trusteeId);
					roleUser.setRole(role);
					roleUser.setUser(user);
					roleUser.setCompanyId(getCompanyId());
					roleUser.setConsigner(trustorId);
					roleUserDao.save(roleUser);
				}else{
					RoleUser roleUser=getRoleUserBySourceId(trusteeId, Long.parseLong(roleIds[i]), getCompanyId(), trustorId);
					roleUser.setDeleted(false);
					roleUserDao.save(roleUser);
				}
			}
		}
	}
	
	public void deleteRoleUsers(Long userId,String[] rIds,Long companyId)	{
		if(rIds==null) return;
		for(int j=0;j<rIds.length;j++){
			RoleUser roleUser=getRoleUserByRelation(userId,Long.parseLong(rIds[j]),companyId);
			if(roleUser!=null){
			roleUser.setDeleted(true);
			roleUserDao.save(roleUser);
			}
		}
	}
	/**
	 * 删除委托人委托出去的角色
	 * @param userId 受委托人的id
	 * @param rIds 角色id数组
	 * @param companyId 公司id
	 * @param sourceId 委托人id
	 */
	public void deleteRoleUsers(Long userId,String[] rIds,Long companyId,Long sourceId)	{
		deleteTrustedRole(sourceId, rIds, userId);
	}
	
	public void deleteTrustedRole(Long trustorId, String[]roleIds,Long trusteeId){
		if(trustorId == null) throw new RuntimeException("没有给定解除委托角色时的委托人");
		if(trusteeId == null) throw new RuntimeException("没有给定解除委托角色时的受托人");
		if(roleIds==null) return;
		for(int j=0;j<roleIds.length;j++){
			RoleUser roleUser=getRoleUserBySourceId(trusteeId,Long.parseLong(roleIds[j]),getCompanyId(),trustorId);
			if(roleUser!=null){
				roleUser.setDeleted(true);
				roleUserDao.save(roleUser);
			}
		}
	}
	
	/**
	 * 根据roleId得到role
	 * @param sourceId
	 * @param userId
	 * @param companyId
	 */
	public Role getRoleById(Long roleId){
		Role role = BeanUtil.turnToModelRole(roleDao.get(roleId));
		return role;
	}
	/**
	 * 根据roleCode得到role
	 * @param roleCode
	 */
	@SuppressWarnings("unchecked")
	public Role getRoleByCode(String roleCode){
		String hql = "FROM Role r WHERE r.code=? AND r.deleted=? AND (r.companyId is null or r.companyId=? )";
		com.norteksoft.acs.entity.authorization.Role r = null;
		List<com.norteksoft.acs.entity.authorization.Role> roles = roleDao.find(hql, roleCode,false,getCompanyId());
		if(roles.size()>0){
			r = roles.get(0);
		}
		Role role = BeanUtil.turnToModelRole(r);
		return role;
	}
	/**
	 * 根据roleCode得到role
	 * @param roleCode
	 */
	@SuppressWarnings("unchecked")
	public List<Role> getRolesByName(String roleName){
		String hql = "FROM Role r WHERE r.name=? AND r.deleted=? AND (r.companyId is null or r.companyId=? )";
		List<com.norteksoft.acs.entity.authorization.Role> roles = roleDao.find(hql, roleName,false,getCompanyId());
		return BeanUtil.turnToModelRoleList(roles);
	}
	/**
	 * 根据userId得到name
	 * @param companyId
	 */
	public String getNameByUserId(){
		String hql="from User u where u.id=? ";
		com.norteksoft.acs.entity.organization.User user=(com.norteksoft.acs.entity.organization.User) userDao.findUnique(hql, ContextUtils.getUserId());
		return user.getName();
	}
	
	
	/**
	 * 删除由别人分配的权限
	 * @param sourceId
	 * @param userId
	 * @param companyId
	 */
	public void deleteAssignedAuthority(Long sourceId,Long userId,Long companyId){
		deleteAllTrustedRole(sourceId, userId);
	}
	
	@SuppressWarnings("unchecked")
	public void deleteAllTrustedRole(Long trustorId, Long trusteeId){
		if(trustorId == null) throw new RuntimeException("没有给定删除角色委托关系时的委托人");
		if(trusteeId == null) throw new RuntimeException("没有给定删除角色委托关系时的受托人");
		String hql = "FROM RoleUser ru WHERE ru.consigner=? AND ru.user.id=? AND ru.companyId=?";
		List<RoleUser> roleUsers = roleUserDao.find(hql, trustorId,trusteeId,getCompanyId());
		for(RoleUser ru:roleUsers){
			roleUserDao.delete(ru);
		}
	}
	
	/**
	 * 按条件获取角色用户表数据
	 * @param userId
	 * @param roleId
	 * @param companyId
	 */
	public RoleUser getRoleUserByRelation(Long userId,Long roleId,Long companyId){
		String hql = "FROM RoleUser ru WHERE ru.role.id=? AND ru.user.id=? AND ru.companyId=?";
		return (RoleUser)roleUserDao.findUnique(hql, roleId,userId,companyId);
	}
	
	/**
	 * 按条件获取角色用户表数据
	 * @param userId
	 * @param roleId
	 * @param companyId
	 */
	public RoleUser getRoleUserBySourceId(Long userId,Long roleId,Long companyId,Long sourceId){
		String hql = "FROM RoleUser ru WHERE ru.role.id=? AND ru.user.id=? AND ru.companyId=? and ru.consigner=?";
		return (RoleUser)roleUserDao.findUnique(hql, roleId,userId,companyId,sourceId);
	}
	/**
	 * 获取所有公司的用户
	 * @return List<User>
	 */
	@SuppressWarnings("unchecked")
	@Transactional(readOnly = true)
	public List<User> getAllUsers(){
		String hql = "from User u where u.deleted=0 order by u.weight desc";
		List<com.norteksoft.acs.entity.organization.User> list =  userDao.find(hql);
		return BeanUtil.turnToModelUserList(list);
	}
	/**
	 * 通过工作组ID获取工作组实体
	 * @param workGroupId
	 * @return
	 */
	@SuppressWarnings("unchecked")
	@Transactional(readOnly = true)
	public Workgroup getWorkgroupById(Long workGroupId){
		if(workGroupId == null) 
			return null;
		List<com.norteksoft.acs.entity.organization.Workgroup> workGroups = workGroupDao.find("from Workgroup wg where wg.company.id=? and wg.id=? ", getCompanyId(), workGroupId);
		if(workGroups.size() == 1){
			return BeanUtil.turnToModelWorkgroup(workGroups.get(0));
		}
		return null;
	}
	
	public Workgroup getWorkGroupByName(String name, Long companyId){
		return getWorkgroupByName(name);
	}
	
	@SuppressWarnings("unchecked")
	public Workgroup getWorkgroupByName(String name){
		if(name == null) throw new RuntimeException("没有给定查询工作组时的查询条件：工作组名称");
		List<com.norteksoft.acs.entity.organization.Workgroup> workGroups = workGroupDao.find("from Workgroup wg where wg.company.id=? and wg.name=? ", getCompanyId(), name);
		if(workGroups.size() == 1){
			return BeanUtil.turnToModelWorkgroup(workGroups.get(0));
		}
		return null;
	}
	
	@SuppressWarnings("unchecked")
	public Workgroup getWorkgroupByCode(String code){
		if(code == null) throw new RuntimeException("没有给定查询工作组时的查询条件：工作组编号");
		List<com.norteksoft.acs.entity.organization.Workgroup> workGroups = workGroupDao.find("from Workgroup wg where wg.company.id=? and wg.code=? ", getCompanyId(), code);
		if(workGroups.size() == 1){
			return BeanUtil.turnToModelWorkgroup(workGroups.get(0));
		}
		return null;
	}
	
	/**
	 * 通过部门ID获取部门实体
	 * @param workGroupId
	 * @return
	 */
	@Transactional(readOnly = true)
	public Department getDepartmentById(Long departmentId){
		if(departmentId == null) 
			return null;
		return BeanUtil.turnToModelDepartment(departmentDao.get(departmentId));
	}
	
	/**
	 * 通过部门名称获取部门实体
	 * @param name
	 * @param companyId
	 * @return
	 */
	public Department getDepartmentByName(String name, Long companyId){
		return getDepartmentByName(name);
	}
	
	@SuppressWarnings("unchecked")
	public Department getDepartmentByName(String name){
		if(name == null) throw new RuntimeException("没有给定查询部门时的查询条件：部门名称");
		List<com.norteksoft.acs.entity.organization.Department> depts = departmentDao.find("from Department d where d.company.id=? and d.name=? and d.deleted=?", getCompanyId(), name, false);
		if(depts.size() == 1){
			return BeanUtil.turnToModelDepartment(depts.get(0));
		}
		return null;
	}
	
	@SuppressWarnings("unchecked")
	public Department getDepartmentByCode(String code){
		if(code == null) throw new RuntimeException("没有给定查询部门时的查询条件：部门编号");
		List<com.norteksoft.acs.entity.organization.Department> depts = departmentDao.find("from Department d where d.company.id=? and d.code=? and d.deleted=?", getCompanyId(), code, false);
		if(depts.size() == 1){
			return BeanUtil.turnToModelDepartment(depts.get(0));
		}
		return null;
	}
	
	/**
	 * 保存注册用户信息
	 * @param userInfo
	 * @param workGroupId
	 * @param companyId
	 */
	public void saveRegisterUser(UserInfo userInfo,Long workGroupId,Long companyId){
		
		userInfo.getUser().setCompanyId(companyId);
		userInfo.setCompanyId(companyId);
		userInfo.setPasswordUpdatedTime(new Date());
		userInfoDao.save(userInfo);
		
		WorkgroupUser workUser = new WorkgroupUser();
		workUser.setUser(userInfo.getUser());
		workUser.setWorkgroup(BeanUtil.turnToWorkgroup(getWorkgroupById(workGroupId)));
		workUser.setCompanyId(companyId);
		workGroupToUserDao.save(workUser);
		
	}
	
	/**
	 * 根据用户得到电话
	 * @param userInfo
	 * @param workGroupId
	 * @param companyId
	 */
	public String getPhoneByUserId(Long userId,Long companyId){
		UserInfo userInfo=(UserInfo)userInfoDao.findUnique("from UserInfo ui where ui.user.id=? and ui.companyId=? ",userId ,companyId);
		if(userInfo.getTelephone()==null){
			return "";
		}else{
			return userInfo.getTelephone();
		}
	}
	
	
	/**
	 * 
	 * @param companyId
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public List<User> getUsersByCompany(Long companyId){
		if(companyId != null){
			List<com.norteksoft.acs.entity.organization.User> list = userDao.find("select distinct u FROM User u join u.departmentUsers du join du.department d  WHERE u.companyId=? AND u.deleted=? AND du.deleted=?  AND d.deleted=? ORDER BY u.weight DESC", companyId,false,false,false);
			return BeanUtil.turnToModelUserList(list);
		}else{
			return new ArrayList<User>();
		}
	}
	/**
	 * 
	 * @param companyId
	 * @return
	 */
	public List<User> getUsersByCompany(){
		return getUsersByCompany(ContextUtils.getCompanyId());
	}
	/**
	 * 
	 * @param companyId
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public List<User> getAllUsersByCompany(Long companyId){//dzy
		if(companyId != null){
			List<com.norteksoft.acs.entity.organization.User> list = userDao.find("select distinct u FROM User u  WHERE u.companyId=? AND u.deleted=?  ORDER BY u.weight DESC", companyId,false );
			return BeanUtil.turnToModelUserList(list);
		}else{
			return new ArrayList<User>();
		}
	}
	/**
	 * 
	 * @param companyId
	 * @return
	 */
	public List<User> getAllUsersByCompany(){//dzy
		return getAllUsersByCompany(ContextUtils.getCompanyId());
	}
	/**
	 * 根据popTree传到后台的字符串解析，得到相应的人员数组（包含无部门人员）
	 * //branch-12,23..,branchalldepartment-34..,branchallworkgroup-9..,
     *  user-32,54,56..或者32,54,56..
	 * @return
	 */
	public List<Long> getTreeUserIds(String str){
		List<Long> result = new ArrayList<Long>();
		List<User> userList = getTreeUser(str);
		for(User u:userList){
			result.add(u.getId());
		}
		return result;
	}
	public List<String> getTreeUserLoginNames(String str){
		List<String> result = new ArrayList<String>();
		List<User> userList = getTreeUser(str);
		for(User u:userList){
			result.add(u.getLoginName());
		}
		return result;
	}
	public List<User> getTreeUser(String str){
		List<User> result = new ArrayList<User>();
		if(str.indexOf("-")==-1&&!"ALLCOMPANYID".equals(str)&&!"ALLWORKGROUP".equals(str)){//管理员
			result.addAll(getUsers(str));
		}else if("ALLCOMPANYID".equals(str)){//管理员
			return getAllUsers();
		}else if("ALLWORKGROUP".equals(str)){//管理员
			return getAllWorkGroupUsersIncludeChildren();
		}else{//多分支机构
			String[] arr = str.split(";");
			for(int i=0;i<arr.length;i++){
				String[] a = arr[i].split("-");
				String type = arr[i].split("-")[0];
				if(a.length==2){
					String ids = arr[i].split("-")[1];
					if(("branch").equals(type)){
						//分支机构下所有人，包括子分支机构,下属所有部门人员,下属所有工作组人员(不含子分支机构)
						//不包含无部门人员
						result.addAll(getAllUsersByBranchIncludeChildrenWithGroup(ids));
					}else if(("branchalldepartment").equals(type)){
						//分支机构下的所有部门的所有人员(不包含无部门人员)
						result.addAll(getAllUsersByBranchIncludeChildren(ids));
					}else if(("branchallworkgroup").equals(type)){
						//分支机构下属所有工作组人员(不含子分支机构)
						result.addAll(getAllWorkGroupUsers(ids));
					}else if(("user").equals(type)){
						result.addAll(getUsers(ids));
					}else if(("branchonlydepartment").equals(type)){
						result.addAll(getAllUsersByBranchIncludeChildren(ids));
					}else if(("branchonlyworkgroup").equals(type)){
						result.addAll(getAllWorkGroupUsers(ids));
					}else if(("nobranch").equals(type)){
						result.addAll(getNoBranchUserUsers(ids));
					}
				}
			}
		}
		
		//去除重复
		List<User> list = new ArrayList<User>();
		for(User u : result){
			if(!list.contains(u)){
				list.add(u);
			}
		}
		return list;
	}
	public List<Long> getTreeDepartmentIds(String str,boolean isIncludeBranch){
		List<Long> result = new ArrayList<Long>();
		List<Department> departmentList = getTreeDepartment(str, isIncludeBranch);
		for(Department d :departmentList){
			result.add(d.getId());
		}
		return result;
	}
	/**
	 * 根据popTree传到后台的字符串解析，得到相应的工作组数组
	 * @param companyId
	 * @return
	 */
	public List<Long> getTreeWorkgroupIds(String str){
		List<Long> result = new ArrayList<Long>();
		List<Workgroup> list = getTreeWorkgroup(str);
		for(Workgroup w :list){
			result.add(w.getId());
		}
		return result;
		
	}
	
	/**
	 * 根据popTree传到后台的字符串解析，得到相应的工作组数组
	 * @param companyId
	 * @return
	 */
	public List<Workgroup> getTreeWorkgroup(String str){
		List<Workgroup> result = new ArrayList<Workgroup>();
		if(str.indexOf("-")==-1&&!"ALLCOMPANYID".equals(str)){//管理员
			result.addAll(getWorkgroupsByIdStr(str));
		}else if("ALLCOMPANYID".equals(str)){//管理员
			return getAllWorkgroups();
		}else{//多分支机构
			String[] arr = str.split(";");
			for(int i=0;i<arr.length;i++){
				String[] a = arr[i].split("-");
				String type = arr[i].split("-")[0];
				if(a.length==2){
					String ids = arr[i].split("-")[1];
					/*if(("branch").equals(type)){
						//分支机构下所有部门，包括子分支机构的部门
						result.addAll(getWorkgroupsByBranchIds(ids));
					}else */
					if(("workgroup").equals(type)){
						result.addAll(getWorkgroupsByIdStr(ids));
					}
				}
			}
		}
		
		//去除重复
		List<Workgroup> list = new ArrayList<Workgroup>();
		for(Workgroup u : result){
			if(!list.contains(u)){
				list.add(u);
			}
		}
		return list;
		
	}
	/**
	 * 根据popTree传到后台的字符串解析，得到相应的部门数组
	 * @param companyId
	 * @return
	 */
	public List<Department> getTreeDepartment(String str,boolean isIncludeBranch){
		List<Department> result = new ArrayList<Department>();
		if(str.indexOf("-")==-1&&!"ALLCOMPANYID".equals(str)){//管理员
			result.addAll(getDepartmentsByIdStr(str));
		}else if("ALLCOMPANYID".equals(str)){//管理员
			return getAllDepartments();
		}else{//多分支机构
			String[] arr = str.split(";");
			for(int i=0;i<arr.length;i++){
				String[] a = arr[i].split("-");
				String type = arr[i].split("-")[0];
				if(a.length==2){
					String ids = arr[i].split("-")[1];
					/*if(("branch").equals(type)){
						//分支机构下所有部门，包括子分支机构的部门
						result.addAll(getAllDepartmentsByBranchIncludeChildren(ids));
					}else */
					if(("department").equals(type)){
						result.addAll(getDepartmentsByIdStr(ids));
					}
				}
			}
		}
		
		//去除重复
		List<Department> list = new ArrayList<Department>();
			if(isIncludeBranch){
				for(Department u : result){
					if(!list.contains(u)){
						list.add(u);
					}
				}
			}else{
				for(Department u : result){
					if(!list.contains(u)&&!u.getBranch()){
						list.add(u);
					}
				}
			}
		return list;
	}
	
	
	private List<User> getUsers(String ids){
		List<User> result = new ArrayList<User>();
		String[] arr = ids.split(",");
		for(int i=0;i<arr.length;i++){
			result.add(getUserById(Long.valueOf(arr[i])));
		}
		return result;
	}
	
	private List<Workgroup> getWorkgroupsByIdStr(String ids){
		List<Workgroup> result = new ArrayList<Workgroup>();
		String[] arr = ids.split(",");
		for(int i=0;i<arr.length;i++){
			result.add(getWorkgroupById(Long.valueOf(arr[i])));
		}
		return result;
	}
	
	private List<Department> getDepartmentsByIdStr(String ids){
		List<Department> result = new ArrayList<Department>();
		String[] arr = ids.split(",");
		for(int i=0;i<arr.length;i++){
			result.add(getDepartmentById(Long.valueOf(arr[i])));
		}
		return result;
	}
	
	/**
	 * 
	 * @param 得到分支机构下的无部门人员
	 * @return
	 */
	public List<User> getNoBranchUserUsers(String ids){
		List<User> result = new ArrayList<User>();
		String[] arr = ids.split(",");
		for(int i=0;i<arr.length;i++){
			result.addAll(getUsersWithoutBranch(Long.valueOf(arr[i])));
		}
		return result;
	}
	
	/**
	 * 
	 * @param 得到分支机构下所有人员（不包含子分支机构但是包含无部门和下属部门的人员）
	 * @return
	 */
	public List<User> getAllUsersByBranch(Long branchId){
		List<com.norteksoft.acs.entity.organization.User> list = getAllUsersByBranchId(branchId);
		return BeanUtil.turnToModelUserList(list);
	}
	/**
	 * 
	 * @param 得到分支机构下所有人员（不包含子分支机构但是包含无部门和下属部门的人员）
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public List<com.norteksoft.acs.entity.organization.User> getAllUsersByBranchId(Long branchId){
		List<com.norteksoft.acs.entity.organization.User> list = userDao.find(
				"select distinct user from User user inner join user.departmentUsers  du where user.deleted=? and du.deleted = ? and user.companyId=? and du.subCompanyId=? order by user.weight desc",
				false,false,getCompanyId(),branchId);
		return list;
	}
	
	/**
	 * 
	 * @param 得到分支机构下所有人员（不包含子分支机构但是不包含无部门和下属部门的人员）
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public List<User> getAllUsersByBranchWithoutNoDepartmentUser(Long branchId){
		List<com.norteksoft.acs.entity.organization.User> list = userDao.find(
				"select distinct user from User user inner join user.departmentUsers  du where user.deleted=? and du.deleted = ? and user.companyId=? and du.subCompanyId=? and du.subCompanyId!=du.department.id order by user.weight desc",
				false,false,getCompanyId(),branchId);
		return BeanUtil.turnToModelUserList(list);
	}
	
	/**
	 * 
	 * @param 得到分支机构下所有人员（包含子分支机构,无部门和下属部门）
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public List<User> getAllUsersByBranchIncludeChildren(List<Long> branchId){
		List<User> users = new ArrayList<User>();
		List<Long> allBranchIds = getAllNoRepeatBranchId(branchId);
		for(Long id: allBranchIds){
			List<User> userList = getAllUsersByBranch(id);
			for(User u : userList){
				if(!users.contains(u)){
					users.add(u);
				}
			}
		}
		
		return users;
		
	}
	
	public List<User> getAllUsersByBranchIncludeChildren(String branchId){
		List<User> users = new ArrayList<User>();
		List<Long> allBranchIds = getAllNoRepeatBranchId(getLongList(branchId));
		for(Long id: allBranchIds){
			List<User> userList = getAllUsersByBranchWithoutNoDepartmentUser(id);
			for(User u : userList){
				if(!users.contains(u)){
					users.add(u);
				}
			}
		}
		
		return users;
		
	}
	
	private List<User> getAllWorkGroupUsersIncludeChildren(){
		List<Long> branchIds = getAllBranchIds();
		List<User> users = new ArrayList<User>();
		for(Long id: branchIds){
			List<Workgroup> workGroups = getWorkgroupsByBranchId(id);
			for(Workgroup workGroup : workGroups){
				users.addAll(getUsersByWorkgroupId(workGroup.getId()));
			}
		}
		
		//去除重复
		List<User> list = new ArrayList<User>();
		for(User u : users){
			if(!list.contains(u)){
				list.add(u);
			}
		}
		return list;
	}
	
	private List<Long> getAllBranchIds(){
		List<Long> result = new ArrayList<Long>();
		List<Department> branchs = getBranchs();
		for(Department d: branchs){
			result.add(d.getId());
		}
		return result;
	}
	/**
	 * 
	 * @param 得到分支机构下所有人员（包含子分支机构,下属部门和下属工作组）
	 * @return
	 */
	@SuppressWarnings("unchecked")
	private List<User> getAllUsersByBranchIncludeChildrenWithGroup(String branchId){
		List<User> users = new ArrayList<User>();
		List<Long> allBranchIds = getAllNoRepeatBranchId(getLongList(branchId));
		for(Long id: allBranchIds){
			//子分支机构,下属部门
			users.addAll(getAllUsersByBranchWithoutNoDepartmentUser(id));
		}
		
		   //下属工作组
		   users.addAll(getAllWorkGroupUsers(branchId));
		return users;
		
	}
	
	private List<User> getAllWorkGroupUsers(String branchIds){
		List<Long> longList = getLongList(branchIds);
		List<User> users = new ArrayList<User>();
		for(Long id: longList){
			List<Workgroup> workGroups = getWorkgroupsByBranchId(id);
			for(Workgroup workGroup : workGroups){
				users.addAll(getUsersByWorkgroupId(workGroup.getId()));
			}
		}
		return users;
	}
	
	private List<Long> getLongList(String ids){
		List<Long> result = new ArrayList<Long>();
		String[] arr = ids.split(",");
		for(int i=0;i<arr.length;i++){
			result.add(Long.valueOf(arr[i]));
		}
		return result;
	}
	
	//去重复
	private List<Long> getAllNoRepeatBranchId(List<Long> branchId){
		List<Long> result = new ArrayList<Long>();
		List<Long> all = new ArrayList<Long>();
		getAllBranchId(branchId,all);
		for(Long obj : all ){
			if(!result.contains(obj)){
				result.add(obj);
			}
		}
		return result;
	}
	
	//递归得到子分支机构id(不用递归也可以????)
	@SuppressWarnings("unused")
	private List<Long> getAllBranchId(List<Long> branchId,List<Long> all){
		for(Long id : branchId){
			List<Long> children = getChildrenBranchIds(id);
			if(children.size()>0){
			  if(!all.contains(id))	
			  all.add(id);
			  all.addAll(children);
			  //all.addAll(getAllBranchId(children,all));
			}
		}
	
		return all;
	}
	
	//得到某一分支机构的所有子分支机构不用递归
	@SuppressWarnings("unchecked")
	private List<Long> getChildrenBranchIds(Long branchId){
//		sqlserver不支持该写法，报异常“如果指定了 SELECT DISTINCT，那么 ORDER BY 子句中的项就必须出现在选择列表中”，所以改为了当前实现方法
//		return userDao.find("select distinct d.id FROM Department d  WHERE d.company.id=? AND d.deleted=? AND d.subCompanyId=? AND d.subCompanyId!=d.id AND d.branch=? ORDER BY d.weight DESC", ContextUtils.getCompanyId(),false,branchId,true);
		List<Object[]> list = userDao.find("select distinct d.id.d.weight FROM Department d  WHERE d.company.id=? AND d.deleted=? AND d.subCompanyId=? AND d.subCompanyId!=d.id AND d.branch=? ORDER BY d.weight DESC", ContextUtils.getCompanyId(),false,branchId,true);
		Set<Long> tempResult = new HashSet<Long>();
		List<Long> result = new ArrayList<Long>();
		for(Object[] obj:list){
			Long userid = (Long)obj[0];
			tempResult.add(userid);
		}
		result.addAll(tempResult);
		return result;
	}
	
	//得到某一分支机构的所有子分支机构和子部门不用递归
	@SuppressWarnings("unchecked")
	private List<Department> getAllDepartmentWithBranchByBranchId(Long branchId){
		return userDao.find("select distinct d FROM Department d  WHERE d.company.id=? AND d.deleted=? AND d.subCompanyId=? AND d.subCompanyId!=d.id ORDER BY d.weight DESC", ContextUtils.getCompanyId(),false,branchId);
	}
	

	/**
	 * 
	 * @param companyId
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public List<User> getUsersByCompanyWithoutAdmin(){
		List<com.norteksoft.acs.entity.organization.User> list = userDao.find("select distinct u FROM User u  WHERE u.companyId=? AND u.deleted=? AND (u.loginName not like ? AND u.loginName not like ? AND u.loginName not like ?) ORDER BY u.weight DESC", ContextUtils.getCompanyId(),false,"%.systemAdmin%","%.securityAdmin%","%.auditAdmin%" );
		return BeanUtil.turnToModelUserList(list);
	}
	/**
	 * 
	 * @param companyId
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public List<Long> getUserIdsByCompanyWithoutAdmin(){
//		sqlserver不支持该写法，报异常“如果指定了 SELECT DISTINCT，那么 ORDER BY 子句中的项就必须出现在选择列表中”，所以改为了当前实现方法
//		List<Long> list = userDao.find("select distinct u.id FROM User u  WHERE u.companyId=? AND u.deleted=? AND (u.loginName not like ? AND u.loginName not like ? AND u.loginName not like ?) ORDER BY u.weight DESC,u.id", ContextUtils.getCompanyId(),false,"%.systemAdmin%","%.securityAdmin%","%.auditAdmin%" );
		List<Object[]> list = userDao.find("select distinct u.id,u.weight FROM User u  WHERE u.companyId=? AND u.deleted=? AND (u.loginName not like ? AND u.loginName not like ? AND u.loginName not like ?) ORDER BY u.weight DESC", ContextUtils.getCompanyId(),false,"%.systemAdmin%","%.securityAdmin%","%.auditAdmin%" );
		Set<Long> tempResult = new HashSet<Long>();
		List<Long> result = new ArrayList<Long>();
		for(Object[] obj:list){
			Long userid = (Long)obj[0];
			tempResult.add(userid);
		}
		result.addAll(tempResult);
		return result;
	}
	/**
	 * 
	 * @param companyId
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public List<String> getUserNamesByCompanyWithoutAdmin(){
//		sqlserver不支持该写法，报异常“如果指定了 SELECT DISTINCT，那么 ORDER BY 子句中的项就必须出现在选择列表中”，所以改为了当前实现方法
//		List<Object[]> list = userDao.find("select distinct u.name FROM User u  WHERE u.companyId=? AND u.deleted=? AND (u.loginName not like ? AND u.loginName not like ? AND u.loginName not like ?) ORDER BY u.weight DESC", ContextUtils.getCompanyId(),false,"%.systemAdmin%","%.securityAdmin%","%.auditAdmin%" );
		List<Object[]> list = userDao.find("select distinct u.name,u.weight FROM User u  WHERE u.companyId=? AND u.deleted=? AND (u.loginName not like ? AND u.loginName not like ? AND u.loginName not like ?) ORDER BY u.weight DESC", ContextUtils.getCompanyId(),false,"%.systemAdmin%","%.securityAdmin%","%.auditAdmin%" );
		Set<String> tempResult = new HashSet<String>();
		List<String> result = new ArrayList<String>();
		for(Object[] obj:list){
			String name = (String)obj[0];
			tempResult.add(name);
		}
		result.addAll(tempResult);
		return result;
	}
	
	public Set<User> getUsersByRoleName(Long systemId, Long companyId, String roleName){
		return getUsersByRoleName(systemId, roleName);
	}
	
	@SuppressWarnings("unchecked")
	public Set<User> getUsersByRoleName(Long systemId, String roleName){
		if(systemId == null) throw new RuntimeException("没有给定查询拥有某角色的用户列表的查询条件：系统ID");
		if(roleName == null) throw new RuntimeException("没有给定查询拥有某角色的用户列表的查询条件：角色名称");
		List<com.norteksoft.acs.entity.authorization.Role> roles = roleDao.find("from Role r where r.businessSystem.id=? and r.name=? and r.deleted=?", systemId, roleName, false);
		if(roles.size() == 1){
			return getUsersByRole(systemId, getCompanyId(), roles.get(0).getCode());
		}
		return new HashSet<User>(0);
	}
	
	public Set<User> getUsersExceptRoleName(Long systemId, Long companyId, String roleName){
		return getUsersWithoutRoleName(systemId, roleName);
	}
	
	@SuppressWarnings("unchecked")
	public Set<User> getUsersWithoutRoleName(Long systemId, String roleName){
		if(systemId == null) throw new RuntimeException("没有给定查询没有某角色的用户列表的查询条件：系统ID");
		if(roleName == null) throw new RuntimeException("没有给定查询没有某角色的用户列表的查询条件：角色名称");
		Set<User> userSet = new HashSet<User>();
		List<com.norteksoft.acs.entity.authorization.Role> roles = roleDao.find("from Role r where r.businessSystem.id=? and r.name<>? and r.deleted=?", systemId, roleName, false);
		for(com.norteksoft.acs.entity.authorization.Role role: roles){
			userSet.addAll(getUsersByRole(systemId, getCompanyId(), role.getCode()));
		}
		return userSet;
	}

	@SuppressWarnings("unchecked")
	public Set<User> getUsersWithoutRoleCode(Long systemId, String roleCode){
		if(systemId == null) throw new RuntimeException("没有给定查询没有某角色的用户列表的查询条件：系统ID");
		if(roleCode == null) throw new RuntimeException("没有给定查询没有某角色的用户列表的查询条件：角色编号");
		Set<User> userSet = new HashSet<User>();
		List<com.norteksoft.acs.entity.authorization.Role> roles = roleDao.find("from Role r where r.businessSystem.id=? and r.code<>? and r.deleted=?", systemId, roleCode, false);
		for(com.norteksoft.acs.entity.authorization.Role role: roles){
			userSet.addAll(getUsersByRole(systemId, getCompanyId(), role.getCode()));
		}
		return userSet;
	}
	
	/**
	 * 通过角色编号查询所有的用户（不含委托）
	 * @param systemId
	 * @param companyId
	 * @param roleCode
	 * @return
	 */
	public Set<User> getUsersByRole(Long systemId, Long companyId, String roleCode){
		return getUsersByRoleCodeExceptTrustedRole(systemId, roleCode);
	}
	
	@SuppressWarnings("unchecked")
	public Set<User> getUsersByRoleCodeExceptTrustedRole(Long systemId, String roleCode){
		if(systemId == null) throw new RuntimeException("没有给定查询拥有某角色的用户列表的查询条件：系统ID");
		if(roleCode == null) throw new RuntimeException("没有给定查询拥有某角色的用户列表的查询条件：角色编号");
		Set<com.norteksoft.acs.entity.organization.User> result = new LinkedHashSet<com.norteksoft.acs.entity.organization.User>();
		//users role
		StringBuilder usersByRole = new StringBuilder();
		usersByRole.append("select u from User u join u.roleUsers ru join ru.role r join r.businessSystem rbs ");
		usersByRole.append("where rbs.id=? and  r.code = ? and u.companyId=? and r.deleted=false and ");
		usersByRole.append("ru.deleted=false and ru.consigner is null and u.deleted=false ");
		List<com.norteksoft.acs.entity.organization.User> roleUsers = userDao.find(usersByRole.toString(), systemId, roleCode, getCompanyId());
		//users department role
		StringBuilder usersByDeptRoleHql = new StringBuilder();
		usersByDeptRoleHql.append("select u from User u join u.departmentUsers du join du.department d ");
		usersByDeptRoleHql.append("join d.roleDepartments rd join rd.role r join r.businessSystem rbs ");
		usersByDeptRoleHql.append("where rbs.id=? and  r.code = ? and d.company.id=? and r.deleted=false and ");
		usersByDeptRoleHql.append("rd.deleted=false and d.deleted=false and du.deleted=false and u.deleted=false ");
		List<com.norteksoft.acs.entity.organization.User> roleDeptUsers = userDao.find(usersByDeptRoleHql.toString(), systemId, roleCode, getCompanyId());
		//users branches role
		StringBuilder usersByBranchesRoleHql = new StringBuilder();
		usersByBranchesRoleHql.append("select u from User u where u.subCompanyId is not null and u.subCompanyId in (select d.id from Department d join d.roleDepartments rd join rd.role r join r.businessSystem rbs where rbs.id=? and  r.code = ? and d.company.id=? and d.branch=true and r.deleted=false and rd.deleted=false and d.deleted=false) and u.deleted=false ");
		List<com.norteksoft.acs.entity.organization.User> roleBranchesUsers = roleDao.find(usersByBranchesRoleHql.toString(), systemId, roleCode, getCompanyId());
		//users work-group role
		StringBuilder usersByWgRoleHql = new StringBuilder();
		usersByWgRoleHql.append("select u from User u join u.workgroupUsers wgu join wgu.workgroup wg ");
		usersByWgRoleHql.append("join wg.roleWorkgroups rwg join rwg.role r join r.businessSystem rbs ");
		usersByWgRoleHql.append("where rbs.id=? and r.code = ? and wg.company.id=? and rbs.deleted=false and r.deleted=false and ");
		usersByWgRoleHql.append("rwg.deleted=false and wg.deleted=false and wgu.deleted=false and u.deleted=false ");
		List<com.norteksoft.acs.entity.organization.User> roleWgUsers = userDao.find(usersByWgRoleHql.toString(), systemId, roleCode, getCompanyId());
		
		result.addAll(roleUsers);
		result.addAll(roleDeptUsers);
		result.addAll(roleBranchesUsers);
		result.addAll(roleWgUsers);
		return BeanUtil.turnToModelUserSet(result);
	}
	@SuppressWarnings("unchecked")
	public Set<Long> getUserIdsByRoleCodeExceptTrustedRole(Long systemId, String roleCode){
		if(systemId == null) throw new RuntimeException("没有给定查询拥有某角色的用户列表的查询条件：系统ID");
		if(roleCode == null) throw new RuntimeException("没有给定查询拥有某角色的用户列表的查询条件：角色编号");
		Set<Long> result = new LinkedHashSet<Long>();
		//users role
		StringBuilder usersByRole = new StringBuilder();
		usersByRole.append("select u.id from User u join u.roleUsers ru join ru.role r join r.businessSystem rbs ");
		usersByRole.append("where rbs.id=? and  r.code = ? and u.companyId=? and r.deleted=false and ");
		usersByRole.append("ru.deleted=false and ru.consigner is null and u.deleted=false ");
		List<Long> roleUsers = userDao.find(usersByRole.toString(), systemId, roleCode, getCompanyId());
		//users department role
		StringBuilder usersByDeptRoleHql = new StringBuilder();
		usersByDeptRoleHql.append("select u.id from User u join u.departmentUsers du join du.department d ");
		usersByDeptRoleHql.append("join d.roleDepartments rd join rd.role r join r.businessSystem rbs ");
		usersByDeptRoleHql.append("where rbs.id=? and  r.code = ? and d.company.id=? and r.deleted=false and ");
		usersByDeptRoleHql.append("rd.deleted=false and d.deleted=false and du.deleted=false and u.deleted=false ");
		List<Long> roleDeptUsers = userDao.find(usersByDeptRoleHql.toString(), systemId, roleCode, getCompanyId());
		//users branches role
		StringBuilder usersByBranchesRoleHql = new StringBuilder();
		usersByBranchesRoleHql.append("select u.id from User u where u.subCompanyId is not null and u.subCompanyId in (select d.id from Department d join d.roleDepartments rd join rd.role r join r.businessSystem rbs where rbs.id=? and  r.code = ? and d.company.id=? and d.branch=true and r.deleted=false and rd.deleted=false and d.deleted=false) and u.deleted=false ");
		List<Long> roleBranchesUsers = roleDao.find(usersByBranchesRoleHql.toString(), systemId, roleCode, getCompanyId());
		//users work-group role
		StringBuilder usersByWgRoleHql = new StringBuilder();
		usersByWgRoleHql.append("select u.id from User u join u.workgroupUsers wgu join wgu.workgroup wg ");
		usersByWgRoleHql.append("join wg.roleWorkgroups rwg join rwg.role r join r.businessSystem rbs ");
		usersByWgRoleHql.append("where rbs.id=? and r.code = ? and wg.company.id=? and rbs.deleted=false and r.deleted=false and ");
		usersByWgRoleHql.append("rwg.deleted=false and wg.deleted=false and wgu.deleted=false and u.deleted=false ");
		List<Long> roleWgUsers = userDao.find(usersByWgRoleHql.toString(), systemId, roleCode, getCompanyId());
		
		result.addAll(roleUsers);
		result.addAll(roleDeptUsers);
		result.addAll(roleBranchesUsers);
		result.addAll(roleWgUsers);
		return result;
	}
	
	
	public String getRtxUrl(Long companyId){
		return getRtxUrl();
	}
	
	public String getRtxUrl(){
		String rtxurl="";
		ServerConfig  serverConfig= serverConfigDao.findUniqueByProperty("companyId", getCompanyId());
		if(serverConfig!=null && serverConfig.getRtxUrl()!=null && isRtxInvocation(getCompanyId())){
			rtxurl=serverConfig.getRtxUrl();
			if(rtxurl.endsWith("/")){
				rtxurl=rtxurl.substring(0,rtxurl.lastIndexOf("/"));
			}
		}
		return rtxurl;
	}
	
	/**
	 * 是否启用了rtx集成
	 * @param companyId
	 * */
	public Boolean isRtxInvocation(Long companyId){
		return isRtxEnable();
	}
	
	public Boolean isRtxEnable(){
		ServerConfig  serverConfig= serverConfigDao.findUniqueByProperty("companyId", getCompanyId());
		if(serverConfig!=null){
			return serverConfig.getRtxInvocation();
		}else{
			return false;
		}
	}
	
	/**
	 * 根据用户ID查询用户所在的部门
	 * @param companyId
	 * @param userId
	 * @return
	 */
	public List<Department> getDepartmentsByUser(Long companyId,Long userId){
		return getDepartmentsByUserId(userId);
	}
	
	@SuppressWarnings("unchecked")
	public List<com.norteksoft.acs.entity.organization.Department> getOldDepartmentsByUserId(Long userId){
		if(userId == null) throw new RuntimeException("没有给定查询用户所在部门列表的查询条件：用户ID");
		StringBuilder hql = new StringBuilder();
		hql.append("select d from Department d join d.departmentUsers du join du.user u ");
		hql.append("where u.companyId=? and u.id=? and u.deleted=? and du.deleted=? and d.deleted=? order by d.weight desc");
		List<com.norteksoft.acs.entity.organization.Department> list = departmentDao.find(hql.toString(), getCompanyId(), userId, false, false, false);
		return list;
	}
	@SuppressWarnings("unchecked")
	public List<Department> getDepartmentsByUserId(Long userId){
		return BeanUtil.turnToModelDepartmentList(getOldDepartmentsByUserId(userId));
	}
	
	/**
	 * 根据登录名查询用户信息
	 * @param companyId
	 * @param loginName
	 * @return
	 */
	public User getUser(Long companyId, String loginName){
		return getUserByLoginName(loginName);
	}
	
	public User getUserByLoginName(String loginName){
		if(StringUtils.isEmpty(loginName)) return null;
		List<com.norteksoft.acs.entity.organization.User> oldUsers= userDao.find("from User u where u.loginName=? and u.deleted=? and u.companyId=? order by u.id",  loginName, false,getCompanyId());
		if(oldUsers.size()>0){
			return BeanUtil.turnToModelUser(oldUsers.get(0));
		}
		return null;
	}
	
	public User getUserByLoginNameAndBranchId(String loginName,Long branchId){
		if(StringUtils.isEmpty(loginName)) return null;;
		com.norteksoft.acs.entity.organization.User oldUser = null;
		if(branchId==null){
			oldUser = (com.norteksoft.acs.entity.organization.User)userDao.findUnique("from User u where u.loginName=? and u.deleted=? and u.subCompanyId is null and u.companyId=?",  loginName, false,getCompanyId());
		}else{
			oldUser = (com.norteksoft.acs.entity.organization.User)userDao.findUnique("from User u where u.loginName=? and u.deleted=? and u.subCompanyId=? and u.companyId=?",  loginName, false,branchId,getCompanyId());
		}
		
		return BeanUtil.turnToModelUser(oldUser);
	}
	
	public User getUserByLoginNameAndBranchCode(String loginName,String branchCode){
		if(StringUtils.isEmpty(loginName)) return null;
		if(StringUtils.isEmpty(branchCode)) return null;
		com.norteksoft.acs.entity.organization.User oldUser = null;
		Company company = companyManager.getCompanyByCode(branchCode);
		if(company==null){
			Department d = getDepartmentByCode(branchCode);
			if(d==null){
				return null;
			}
			if(!d.getBranch()){
				return null;
			}
			List<com.norteksoft.acs.entity.organization.User> oldUsers =  userDao.find("from User u where u.loginName=? and u.deleted=? and u.subCompanyId=? and u.companyId=? ",  loginName, false,d.getId(),getCompanyId());
			if(oldUsers.size()>0){
				oldUser = oldUsers.get(0);
			}
		}else{
			List<com.norteksoft.acs.entity.organization.User> oldUsers =  userDao.find("from User u where u.loginName=? and u.deleted=? and u.subCompanyId is null and u.companyId=? ",  loginName, false,company.getId());
			if(oldUsers.size()>0){
				oldUser = oldUsers.get(0);
			}
		}
		return BeanUtil.turnToModelUser(oldUser);
	}
	
	public com.norteksoft.acs.entity.organization.User getUserByLoginNameOld(String loginName){
		if(StringUtils.isEmpty(loginName)) throw new RuntimeException("没有给定查询用户的查询条件：用户登录名");
		com.norteksoft.acs.entity.organization.User oldUser= (com.norteksoft.acs.entity.organization.User)userDao.findUnique("from User u where u.loginName=? and u.deleted=? ",  loginName, false);
		return oldUser;
	}
	
	public User getUser(String email){
		return getUserByEmail(email);
	}
	
	/**
	 * 根据邮件地址查询用户信息
	 * @param companyId
	 * @param loginName
	 * @return
	 */
	public User getUserByEmail(String email){
		List<User> users = getUsersByEmail(email);
		if(users.size()>0){
			return users.get(0);
		}
		return null;
	}
	/**
	 * 根据邮件地址查询用户信息
	 * @param companyId
	 * @param loginName
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public List<User> getUsersByEmail(String email){
		if(StringUtils.isEmpty(email)) return null;
		List<User> users = new ArrayList<User>();
		List<com.norteksoft.acs.entity.organization.User> list=userDao.find("from User u where u.email=? and u.deleted=? ",email, false);
		if(list!=null&&!list.isEmpty()){
			users = BeanUtil.turnToModelUserList(list);
		}
		return users;
	}
	
	public Set<String> getUserExceptLoginName(Long companyId,String loginName){
		return getLoginNamesExclude(loginName);
	}
	
	/**
	 * 查询出该登录名外的其他用户的登录名
	 * @param companyId
	 * @param loginName
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public Set<String> getLoginNamesExclude(String loginName){
		if(StringUtils.isEmpty(loginName)) throw new RuntimeException("没有给定查询不含某登录名用户列表的查询条件：用户登录名");
		return new HashSet<String>(userDao.find("select u.loginName from User u where u.companyId=? and u.loginName<>? and u.deleted=? ", getCompanyId(), loginName, false));
	}
	/**
	 * 查询除该用户外的其他用户的id
	 * @param companyId
	 * @param loginName
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public Set<Long> getLoginNamesExclude(Long userId){
		if(userId!=null){
			return new HashSet<Long>(userDao.find("select u.id from User u where u.companyId=? and u.id<>? and u.deleted=? ", getCompanyId(), userId, false));
		}else{
			return new HashSet<Long>();
			
		}
	}
	
	/**
	 * 根据用户登录名查询用户所在的部门
	 * @param companyId
	 * @param loginName
	 * @return
	 */
	public List<Department> getDepartmentsByUser(Long companyId, String loginName){
		ThreadParameters parameters = new ThreadParameters(companyId);
		ParameterUtils.setParameters(parameters);
		return getDepartments(loginName);
	}
	
	@SuppressWarnings("unchecked")
	public List<Department> getDepartments(String loginName){
		if(StringUtils.isEmpty(loginName)) throw new RuntimeException("没有给定用户所在部门列表的查询条件：用户登录名");
		StringBuilder hql = new StringBuilder();
		hql.append("select d from Department d join d.departmentUsers du join du.user u ");
		hql.append("where u.companyId=? and u.loginName =? and u.deleted=? and du.deleted=? and d.deleted=?");
		List<com.norteksoft.acs.entity.organization.Department> list = departmentDao.find(hql.toString(), getCompanyId(), loginName, false, false, false);
		return BeanUtil.turnToModelDepartmentList(list);
	}
	@SuppressWarnings("unchecked")
	public List<String> getDepartmentNames(String loginName){
		if(StringUtils.isEmpty(loginName)) throw new RuntimeException("没有给定用户所在部门列表的查询条件：用户登录名");
		StringBuilder hql = new StringBuilder();
		hql.append("select d.name from Department d join d.departmentUsers du join du.user u ");
		hql.append("where u.companyId=? and u.loginName =? and u.deleted=? and du.deleted=? and d.deleted=?");
		List<String> list = departmentDao.find(hql.toString(), getCompanyId(), loginName, false, false, false);
		return list;
	}
	@SuppressWarnings("unchecked")
	public List<Department> getDepartments(Long userId){
		if(userId==null) throw new RuntimeException("没有给定用户所在部门列表的查询条件：用户id");
		StringBuilder hql = new StringBuilder();
		hql.append("select d from Department d join d.departmentUsers du join du.user u ");
		hql.append("where u.companyId=? and u.id =? and u.deleted=? and du.deleted=? and d.deleted=?");
		List<com.norteksoft.acs.entity.organization.Department> list = departmentDao.find(hql.toString(), getCompanyId(), userId, false, false, false);
		return BeanUtil.turnToModelDepartmentList(list);
	}
	@SuppressWarnings("unchecked")
	public List<String> getDepartmentNames(Long userId){
		if(userId==null) throw new RuntimeException("没有给定用户所在部门列表的查询条件：用户id");
		StringBuilder hql = new StringBuilder();
		hql.append("select d.name from Department d join d.departmentUsers du join du.user u ");
		hql.append("where u.companyId=? and u.id =? and u.deleted=? and du.deleted=? and d.deleted=?");
		List<String> list = departmentDao.find(hql.toString(), getCompanyId(), userId, false, false, false);
		return list;
	}
	
	/**
	 * 查询用户所在的部门id集合
	 * @param userId
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public List<Long> getDepartmentIds(Long userId){
		if(userId == null) throw new RuntimeException("没有给定查询用户所在部门列表的查询条件：用户ID");
		StringBuilder hql = new StringBuilder();
		hql.append("select d.id from Department d join d.departmentUsers du join du.user u ");
		hql.append("where u.companyId=? and u.id=? and u.deleted=? and du.deleted=? and d.deleted=? and d.branch=? order by d.weight desc");
		List<Long> list = departmentDao.find(hql.toString(), getCompanyId(), userId, false, false, false,false);
		return list;
	}
	
	/**
	 * 根据用户登录名查询用户所在的部门
	 * @param companyId
	 * @param loginName
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public List<Department> getDepartmentsByUserLike(Long companyId, String name){
		StringBuilder hql = new StringBuilder();
		hql.append("select d from Department d join d.departmentUsers du join du.user u ");
		hql.append("where u.companyId=? and u.name like ? and u.deleted=? and du.deleted=? and d.deleted=?");
		List<com.norteksoft.acs.entity.organization.Department> list =  departmentDao.find(hql.toString(), companyId, "%"+name+"%", false, false, false);
		return BeanUtil.turnToModelDepartmentList(list);
	}
	
	/**
	 * 根据公司ID和用户的登录名查询该用户所具有的角色的字符串表示
	 * @param companyId
	 * @param loginName
	 * @return
	 */
	public Set<Role> getRolesByUser(Long companyId, String loginName){
		return getRolesByUser(loginName);
	}
	
	public Set<Role> getRolesByUser(String loginName){
		if(StringUtils.isEmpty(loginName)) throw new RuntimeException("没有给定用户角色列表的查询条件：用户登录名");
		com.norteksoft.acs.entity.organization.User user = getUserByLoginNameOld(loginName);
		if(user == null) throw new RuntimeException("用户登录名为["+loginName+"]的用户不存在");
		return getRolesByUser(user.getId());
	}
	
	/**
	 * 根据公司ID和用户登录名查询该用户所在的工作组
	 * @param companyId
	 * @param loginName
	 * @return
	 */
	public List<Workgroup> getWorkGroupsByUser(Long companyId, String loginName){
		return getWorkgroupsByUser(loginName);
	}
	
	@SuppressWarnings("unchecked")
	public List<Workgroup> getWorkgroupsByUser(String loginName){
		if(StringUtils.isEmpty(loginName)) throw new RuntimeException("没有给出查询用户所在工作组列表的查询条件：用户登录名");
		StringBuilder hql = new StringBuilder();
		hql.append("select wg from Workgroup wg join wg.workgroupUsers wgu join wgu.user u ");
		hql.append("where u.companyId=? and u.loginName=? and u.deleted=? and wgu.deleted=? and wg.deleted=?");
		List<com.norteksoft.acs.entity.organization.Workgroup> list =  workGroupDao.find(hql.toString(), getCompanyId(), loginName, false, false, false);
		return BeanUtil.turnToModelWorkgroupList(list);
	}
	@SuppressWarnings("unchecked")
	public List<Workgroup> getWorkgroupsByUser(Long userId){
		if(userId==null) throw new RuntimeException("没有给出查询用户所在工作组列表的查询条件：用户id");
		StringBuilder hql = new StringBuilder();
		hql.append("select wg from Workgroup wg join wg.workgroupUsers wgu join wgu.user u ");
		hql.append("where u.companyId=? and u.id=? and u.deleted=? and wgu.deleted=? and wg.deleted=?");
		List<com.norteksoft.acs.entity.organization.Workgroup> list =  workGroupDao.find(hql.toString(), getCompanyId(), userId, false, false, false);
		return BeanUtil.turnToModelWorkgroupList(list);
	}
	@SuppressWarnings("unchecked")
	public Set<String> getWorkgroupNamesByUser(Long userId){
		if(userId==null) throw new RuntimeException("没有给出查询用户所在工作组列表的查询条件：用户id");
		StringBuilder hql = new StringBuilder();
		hql.append("select wg.name from Workgroup wg join wg.workgroupUsers wgu join wgu.user u ");
		hql.append("where u.companyId=? and u.id=? and u.deleted=? and wgu.deleted=? and wg.deleted=?");
		List<String> list =  workGroupDao.find(hql.toString(), getCompanyId(), userId, false, false, false);
		return new HashSet<String>(list);
	}
	@SuppressWarnings("unchecked")
	public Set<Long> getWorkgroupIdsByUser(Long userId){
		if(userId==null) throw new RuntimeException("没有给出查询用户所在工作组列表的查询条件：用户id");
		StringBuilder hql = new StringBuilder();
		hql.append("select wg.id from Workgroup wg join wg.workgroupUsers wgu join wgu.user u ");
		hql.append("where u.companyId=? and u.id=? and u.deleted=? and wgu.deleted=? and wg.deleted=?");
		List<Long> list =  workGroupDao.find(hql.toString(), getCompanyId(), userId, false, false, false);
		return new HashSet<Long>(list);
	}
	
	/**
	 * 根据公司ID和用户登录名查询该用户所在的工作组
	 * @param companyId
	 * @param loginName
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public List<Workgroup> getWorkGroupsByUserLike(Long companyId, String name){
		StringBuilder hql = new StringBuilder();
		hql.append("select wg from Workgroup wg join wg.workgroupUsers wgu join wgu.user u ");
		hql.append("where u.companyId=? and u.name like ? and u.deleted=? and wgu.deleted=? and wg.deleted=?");
		List<com.norteksoft.acs.entity.organization.Workgroup> list = workGroupDao.find(hql.toString(), companyId, "%"+name+"%", false, false, false);
		return BeanUtil.turnToModelWorkgroupList(list);
	}
	/**
	 * 根据用户登录名查询该用户所在的工作组
	 * @param companyId
	 * @param loginName
	 * @return
	 */
	public List<Workgroup> getWorkGroupsByUserLike(String name){
		return getWorkGroupsByUserLike(ContextUtils.getCompanyId(),name);
	}
	
	/**
	 * 查询所有的系统并排序
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public List<BusinessSystem> getSystems(){
		List<com.norteksoft.acs.entity.authorization.BusinessSystem> list = businessSystemDao.find("from BusinessSystem bs where bs.deleted=? order by bs.id", false);
		return BeanUtil.turnToModelBusinessSystemList(list);
	}
	
	public BusinessSystem getSystemByCode(String code){
		if(StringUtils.isEmpty(code)) throw new RuntimeException("没有查询业务系统的查询条件：系统编号");
		return BeanUtil.turnToModelBusinessSystem(businessSystemDao.findUniqueByProperty("code", code));
	}
	public BusinessSystem getSystemById(Long id){
		if(id == null) throw new RuntimeException("没有查询业务系统的查询条件：系统ID");
		return BeanUtil.turnToModelBusinessSystem(businessSystemDao.findUniqueByProperty("id", id));
	}
	
	public List<User> getUsersByLoginNames(Long companyId, List<String> loginNames){
		if(companyId == null) throw new RuntimeException("没有给定根据用户登录名集合查询用户列表的查询条件：公司ID");
		ThreadParameters parameters=new ThreadParameters(companyId);
		ParameterUtils.setParameters(parameters);
		return getUsersByLoginNames(loginNames);
	}
	
	@SuppressWarnings("unchecked")
	public List<User> getUsersByLoginNames(List<String> loginNames){
		if(loginNames==null||loginNames.size()<=0){
			return new ArrayList<User>();
		}else{
			StringBuilder hql = new StringBuilder("from User u where u.companyId=? and (");
			Object[] parameters = new Object[loginNames.size()+1];
			parameters[0] = getCompanyId();
			int index = 1;
			for(String loginName : loginNames){
				parameters[index++] = loginName;
				hql.append(" u.loginName=? or");
			}
			hql.replace(hql.length()-2, hql.length(), "");
			hql.append(") and u.deleted=false order by u.weight desc");
			List<com.norteksoft.acs.entity.organization.User> list =  userDao.find(hql.toString(), parameters);
			return BeanUtil.turnToModelUserList(list);
		}
	}
	
	public Set<String> getUserNamesByLoginNames(Collection<String> loginNames){
		AcsApiManager acsApiManager = (AcsApiManager)ContextUtils.getBean("acsApiManager");
		if(acsApiManager.hasBranch(getCompanyId())){
			return getUserNamesByLoginNamesWithBranch(loginNames);
		}else{
			return getUserNamesByLoginNamesWithNoBranch(loginNames);
		}
	}
	@SuppressWarnings("unchecked")
	public Set<Long> getUserIdsByLoginNames(Collection<String> loginNames){
		if(loginNames==null||loginNames.size()<=0){
			return new HashSet<Long>();
		}else{
			StringBuilder hql = new StringBuilder("select u.id from User u where u.companyId=? and (");
			Object[] parameters = new Object[loginNames.size()+1];
			parameters[0] = getCompanyId();
			int index = 1;
			for(String loginName : loginNames){
				parameters[index++] = loginName;
				hql.append(" u.loginName=? or");
			}
			hql.replace(hql.length()-2, hql.length(), "");
			hql.append(") and u.deleted=false order by u.weight desc");
			List<Long> list =  userDao.find(hql.toString(), parameters);
			Set<Long> result = new HashSet<Long>();
			result.addAll(list);
			return result;
		}
	}
	@SuppressWarnings("unchecked")
	private Set<String> getUserNamesByLoginNamesWithNoBranch(Collection<String> loginNames){
		if(loginNames==null||loginNames.size()<=0){
			return new HashSet<String>();
		}else{
			StringBuilder hql = new StringBuilder("select u.name from User u where u.companyId=? and (");
			Object[] parameters = new Object[loginNames.size()+1];
			parameters[0] = getCompanyId();
			int index = 1;
			for(String loginName : loginNames){
				parameters[index++] = loginName;
				hql.append(" u.loginName=? or");
			}
			hql.replace(hql.length()-2, hql.length(), "");
			hql.append(") and u.deleted=false order by u.weight desc");
			List<String> list =  userDao.find(hql.toString(), parameters);
			Set<String> result = new HashSet<String>();
			result.addAll(list);
			return result;
		}
	}
	@SuppressWarnings("unchecked")
	private Set<String> getUserNamesByLoginNamesWithBranch(Collection<String> loginNames){
		if(loginNames==null||loginNames.size()<=0){
			return new HashSet<String>();
		}else{
			StringBuilder hql = new StringBuilder("from User u where u.companyId=? and (");
			Object[] parameters = new Object[loginNames.size()+1];
			parameters[0] = getCompanyId();
			int index = 1;
			for(String loginName : loginNames){
				parameters[index++] = loginName;
				hql.append(" u.loginName=? or");
			}
			hql.replace(hql.length()-2, hql.length(), "");
			hql.append(") and u.deleted=false order by u.weight desc");
			List<com.norteksoft.acs.entity.organization.User> list =  userDao.find(hql.toString(), parameters);
			Set<String> result = new HashSet<String>();
			for(com.norteksoft.acs.entity.organization.User u:list){
				result.add(u.getName()+"("+u.getSubCompanyName()+")");
			}
			return result;
		}
	}
	public List<User> getUsersByIds(Long companyId, List<Long> userIds){
		if(companyId == null) throw new RuntimeException("没有给定根据用户登录名集合查询用户列表的查询条件：公司ID");
		ThreadParameters parameters=new ThreadParameters(companyId);
		ParameterUtils.setParameters(parameters);
		return getUsersByIds(userIds);
	}
	@SuppressWarnings("unchecked")
	public List<User> getUsersByIds(List<Long> userIds){
		if(userIds==null||userIds.size()<=0){
			return new ArrayList<User>();
		}else{
			StringBuilder hql = new StringBuilder("from User u where u.companyId=? and (");
			Object[] parameters = new Object[userIds.size()+1];
			parameters[0] = getCompanyId();
			int index = 1;
			for(Long userid : userIds){
				parameters[index++] = userid;
				hql.append(" u.id=? or");
			}
			hql.replace(hql.length()-2, hql.length(), "");
			hql.append(") and u.deleted=false order by u.weight desc");
			List<com.norteksoft.acs.entity.organization.User> list =  userDao.find(hql.toString(), parameters);
			return BeanUtil.turnToModelUserList(list);
		}
	}
	public Set<String> getUserNamesByIds(Collection<Long> userIds){
		AcsApiManager acsApiManager = (AcsApiManager)ContextUtils.getBean("acsApiManager");
		if(acsApiManager.hasBranch(getCompanyId())){
			return getUserNamesByIdsWithBranch(userIds);
		}else{
			return getUserNamesByIdsWithNoBranch(userIds);
		}
	}
	@SuppressWarnings("unchecked")
	private Set<String> getUserNamesByIdsWithNoBranch(Collection<Long> userIds){
		if(userIds==null||userIds.size()<=0){
			return new HashSet<String>();
		}else{
			StringBuilder hql = new StringBuilder("select u.name from User u where u.companyId=? and (");
			Object[] parameters = new Object[userIds.size()+1];
			parameters[0] = getCompanyId();
			int index = 1;
			for(Long userid : userIds){
				parameters[index++] = userid;
				hql.append(" u.id=? or");
			}
			hql.replace(hql.length()-2, hql.length(), "");
			hql.append(") and u.deleted=false order by u.weight desc");
			List<String> list =  userDao.find(hql.toString(), parameters);
			Set<String> result = new HashSet<String>();
			result.addAll(list);
			return result;
		}
	}
	
	@SuppressWarnings("unchecked")
	private Set<String> getUserNamesByIdsWithBranch(Collection<Long> userIds){
		if(userIds==null||userIds.size()<=0){
			return new HashSet<String>();
		}else{
			StringBuilder hql = new StringBuilder("from User u where u.companyId=? and (");
			Object[] parameters = new Object[userIds.size()+1];
			parameters[0] = getCompanyId();
			int index = 1;
			for(Long userid : userIds){
				parameters[index++] = userid;
				hql.append(" u.id=? or");
			}
			hql.replace(hql.length()-2, hql.length(), "");
			hql.append(") and u.deleted=false order by u.weight desc");
			List<com.norteksoft.acs.entity.organization.User> list =  userDao.find(hql.toString(), parameters);
			Set<String> result = new HashSet<String>();
			for(com.norteksoft.acs.entity.organization.User u:list){
				result.add(u.getName()+"("+u.getSubCompanyName()+")");
			}
			return result;
		}
		
	}
	
	
	@SuppressWarnings("unchecked")
	public Set<String> getLoginNamesByIds(Collection<Long> userIds){
		if(userIds==null||userIds.size()<=0){
			return new HashSet<String>();
		}else{
			StringBuilder hql = new StringBuilder("select u.loginName from User u where u.companyId=? and (");
			Object[] parameters = new Object[userIds.size()+1];
			parameters[0] = getCompanyId();
			int index = 1;
			for(Long userid : userIds){
				parameters[index++] = userid;
				hql.append(" u.id=? or");
			}
			hql.replace(hql.length()-2, hql.length(), "");
			hql.append(") and u.deleted=false order by u.weight desc");
			List<String> list =  userDao.find(hql.toString(), parameters);
			Set<String> result = new HashSet<String>();
			result.addAll(list);
			return result;
		}
	}
	
	
	public List<Role> getRolesListByUserExceptDelegateMain(Long userId){
		return getRolesExcludeTrustedRole(userId);
	}
	
	public List<Role> getRolesExcludeTrustedRole(Long userId){
		com.norteksoft.acs.entity.organization.User user = userDao.get(userId);
		if(user == null) return null;
		User modeUser = BeanUtil.turnToModelUser(user);
		return getRolesListByUserExceptDelegateMain(modeUser);
	}
	
	@Deprecated
	@Transactional(readOnly = true)
	public List<Role> getRolesListByUserExceptDelegateMain(com.norteksoft.acs.entity.organization.User user){
		if(user == null) return null;
		
		List<Role> roles = new ArrayList<Role>();
		// 用户具有的角色
		Set<RoleUser> roleUsers = user.getRoleUsers();
		for(RoleUser ru : roleUsers){
			if(ru.isDeleted()) continue;
			if(ru.getConsigner()!=null)continue;
			Role role = BeanUtil.turnToModelRole(ru.getRole());
			if(!role.isDeleted()) {
				if(!roles.contains(role)){
					roles.add(role);
				}
			}
		}
		// 用户具有的部门拥有的角色
		Set<DepartmentUser> departmentUsers =  user.getDepartmentUsers();
		for(DepartmentUser du : departmentUsers){
			if(du.isDeleted() || du.getDepartment().isDeleted()) continue;
			for(RoleDepartment rd : du.getDepartment().getRoleDepartments()){
				if(!rd.isDeleted() && !rd.getRole().isDeleted()) {
					if(!roles.contains(rd.getRole())){
						roles.add(BeanUtil.turnToModelRole(rd.getRole()));
					}
				}
			}
		}
		// 用户具有的分支机构拥有的角色
		if(user.getSubCompanyId()!=null){
			Set<RoleDepartment> rds=departmentDao.get(user.getSubCompanyId()).getRoleDepartments();
			for(RoleDepartment rd : rds){
				if(!rd.isDeleted() && !rd.getRole().isDeleted()){
					if(!roles.contains(rd.getRole())){
						roles.add(BeanUtil.turnToModelRole(rd.getRole()));
					}
				} 
			}
		}
		// 用户具有的工作组拥有的角色
		Set<WorkgroupUser> workgroupUsers = user.getWorkgroupUsers();
		for(WorkgroupUser wu : workgroupUsers){
			if(wu.isDeleted() || wu.getWorkgroup().isDeleted()) continue;
			for(RoleWorkgroup rw : wu.getWorkgroup().getRoleWorkgroups()){
				if(!rw.isDeleted() && !rw.getRole().isDeleted()) {
					if(!roles.contains(rw.getRole())){
						roles.add(BeanUtil.turnToModelRole(rw.getRole()));
					}
				}
			}
		}
		//角色按权重排序
		sortRole(roles);
		return roles;
	}
	/**
	 * 根据用户查询角色(不含委托)
	 * @param user
	 * @return
	 */
	@Transactional(readOnly = true)
	@SuppressWarnings("unchecked")
	public List<Role> getRolesListByUserExceptDelegateMain(User user){
		if(user == null) return null;
		
		List<Role> roles = new ArrayList<Role>();
		Set<com.norteksoft.acs.entity.authorization.Role> oldRoles = new HashSet<com.norteksoft.acs.entity.authorization.Role>();
		// 用户具有的角色
		String hql = "select r from Role r join r.roleUsers ru where r.deleted=? and ru.deleted=? and ru.user is not null and ru.user.id=? and ru.consigner is  null";
		List<com.norteksoft.acs.entity.authorization.Role> userRoles = roleDao.find(hql, false,false,user.getId());
		oldRoles.addAll(userRoles);
		// 用户具有的部门拥有的角色
		 hql = "select r from Role r join r.roleDepartments rd join rd.department d join d.departmentUsers du where r.deleted=? and rd.deleted=? and d.deleted=? and du.deleted=? and du.user is not null and du.user.id=?";
		 List<com.norteksoft.acs.entity.authorization.Role> deptRoles = roleDao.find(hql, false,false,false,false,user.getId());
		 oldRoles.addAll(deptRoles);
		 // 用户具有的分支机构拥有的角色
		 if(user.getSubCompanyId()!=null){
			 hql = "select r from RoleDepartment du join du.role r join du.department d where r.deleted=? and du.deleted=? and d.deleted=? and d.branch=? and d.id is (select u.subCompanyId from User u where u.id=? and u.deleted=? and u.subCompanyId is not null)";
			 List<com.norteksoft.acs.entity.authorization.Role> branchesRoles = roleDao.find(hql, false, false, false,true,user.getId(), false);
			 oldRoles.addAll(branchesRoles);
		 }
		// 用户具有的工作组拥有的角色
		 hql = "select r from Role r join r.roleWorkgroups rw join rw.workgroup wg join wg.workgroupUsers wu  where r.deleted=? and rw.deleted=? and wg.deleted=? and wu.deleted=?  and wu.user is not null and wu.user.id=?";
		 List<com.norteksoft.acs.entity.authorization.Role> workgroupRoles  = roleDao.find(hql, false,false,false,false,user.getId());
		 oldRoles.addAll(workgroupRoles);
		 Set<Role> modeRoles = BeanUtil.turnToModelRoleSet(oldRoles);
		if(modeRoles!=null){
			roles.addAll(modeRoles);
		}
		//角色按权重排序
		sortRoleId(roles);
		return roles;
	}
	//角色按Id排序
	private void sortRoleId(List<Role> roles){
		Collections.sort(roles, new Comparator<Role>() {
			public int compare(Role role1, Role role2) {
				if(role1.getId()<role2.getId()){
						return 1;
				}
				return 0;
			}
		});
	}
	//角色按权重排序
	private void sortRole(List<Role> roles){
		Collections.sort(roles, new Comparator<Role>() {
			public int compare(Role role1, Role role2) {
				if(role1.getWeight()==null&&role2.getWeight()!=null)return 1;
				if(role1.getWeight()!=null&&role2.getWeight()==null)return 0;
				if(role1.getWeight()==null&&role2.getWeight()==null)return 0;
				if(role1.getWeight()<role2.getWeight()){
					return 1;
				}else if(role1.getWeight().equals(role2.getWeight())){
					if(role1.getId()<role2.getId()){
						return 1;
					}
				}
			    return 0;
			}
		});
	}
	@Deprecated
	private void sortRoles(List<com.norteksoft.acs.entity.authorization.Role> roles){
		Collections.sort(roles, new Comparator<com.norteksoft.acs.entity.authorization.Role>() {
			public int compare(com.norteksoft.acs.entity.authorization.Role role1, com.norteksoft.acs.entity.authorization.Role role2) {
				if(role1.getWeight()==null&&role2.getWeight()!=null)return 1;
				if(role1.getWeight()!=null&&role2.getWeight()==null)return 0;
				if(role1.getWeight()==null&&role2.getWeight()==null)return 0;
				if(role1.getWeight()<role2.getWeight()){
					return 1;
				}
				return 0;
			}
		});
	}
	
	public List<Department> getSuperiorDepartmentsByUser(Long companyId, String loginName){
		return getParentDepartmentsByUser(loginName);
	}
	
	/**
	 * 根据用户登录名查询用户所在的部门的上级部门
	 * @param companyId
	 * @param loginName
	 * @return
	 */
	@SuppressWarnings("unchecked")
	@Transactional(readOnly = true)
	public List<Department> getParentDepartmentsByUser(String loginName){
		if(loginName == null) throw new RuntimeException("没有查询用户所在的部门的上级部门的查询条件：用户登录名");
		StringBuilder hql = new StringBuilder(" select d from Department d join d.children sd join sd.departmentUsers du join du.user u ");
		hql.append(" where u.companyId=? and u.loginName=? and u.deleted=? and du.deleted=? and sd.deleted=? and d.deleted=?");
		List<com.norteksoft.acs.entity.organization.Department> list = departmentDao.find(hql.toString(), getCompanyId(), loginName, false, false, false,false);
		return BeanUtil.turnToModelDepartmentList(list);
	}
	/**
	 * 根据用户登录名查询用户所在的部门的上级部门
	 * @param companyId
	 * @param loginName
	 * @return
	 */
	@SuppressWarnings("unchecked")
	@Transactional(readOnly = true)
	public List<String> getParentDepartmentNamesByUser(String loginName){
		if(loginName == null) throw new RuntimeException("没有查询用户所在的部门的上级部门的查询条件：用户登录名");
		StringBuilder hql = new StringBuilder(" select d.name from Department d join d.children sd join sd.departmentUsers du join du.user u ");
		hql.append(" where u.companyId=? and u.loginName=? and u.deleted=? and du.deleted=? and sd.deleted=? and d.deleted=?");
		List<String> list = departmentDao.find(hql.toString(), getCompanyId(), loginName, false, false, false,false);
		return list;
	}
	/**
	 * 根据用户id查询用户所在的部门的上级部门
	 * @param companyId
	 * @param loginName
	 * @return
	 */
	@SuppressWarnings("unchecked")
	@Transactional(readOnly = true)
	public List<Department> getParentDepartmentsByUser(Long userId){
		if(userId == null) throw new RuntimeException("没有查询用户所在的部门的上级部门的查询条件：用户id");
		StringBuilder hql = new StringBuilder(" select d from Department d join d.children sd join sd.departmentUsers du join du.user u ");
		hql.append(" where u.companyId=? and u.id=? and u.deleted=? and du.deleted=? and sd.deleted=? and d.deleted=?");
		List<com.norteksoft.acs.entity.organization.Department> list = departmentDao.find(hql.toString(), getCompanyId(), userId, false, false, false,false);
		return BeanUtil.turnToModelDepartmentList(list);
	}
	/**
	 * 根据用户id查询用户所在的部门的上级部门
	 * @param companyId
	 * @param loginName
	 * @return
	 */
	@SuppressWarnings("unchecked")
	@Transactional(readOnly = true)
	public List<String> getParentDepartmentNamesByUser(Long userId){
		if(userId == null) throw new RuntimeException("没有查询用户所在的部门的上级部门的查询条件：用户id");
		StringBuilder hql = new StringBuilder(" select d.name from Department d join d.children sd join sd.departmentUsers du join du.user u ");
		hql.append(" where u.companyId=? and u.id=? and u.deleted=? and du.deleted=? and sd.deleted=? and d.deleted=?");
		List<String> list = departmentDao.find(hql.toString(), getCompanyId(), userId, false, false, false,false);
		return list;
	}
	/**
	 * 根据用户id查询用户所在的部门的上级部门
	 * @param companyId
	 * @param loginName
	 * @return
	 */
	@SuppressWarnings("unchecked")
	@Transactional(readOnly = true)
	public List<Long> getParentDepartmentIdsByUser(Long userId){
		if(userId == null) throw new RuntimeException("没有查询用户所在的部门的上级部门的查询条件：用户id");
		StringBuilder hql = new StringBuilder(" select d.id from Department d join d.children sd join sd.departmentUsers du join du.user u ");
		hql.append(" where u.companyId=? and u.id=? and u.deleted=? and du.deleted=? and sd.deleted=? and d.deleted=?");
		List<Long> list = departmentDao.find(hql.toString(), getCompanyId(), userId, false, false, false,false);
		return list;
	}
	
	/**
	 * 获得用户的顶级部门
	 * @param companyId
	 * @param loginName
	 * @return
	 */
	public List<Department> getUpstageDepartmentsByUser(Long companyId, String loginName){
		return getTopDepartmentsByUser(loginName);
	}
	
	public List<Department> getTopDepartmentsByUser(String loginName){
		if(loginName == null) throw new RuntimeException("没有查询用户所在的部门的顶级部门的查询条件：用户登录名");
		Set<Department> result = new HashSet<Department>();
		List<Department> departments = getDepartmentsByUser(getCompanyId(), loginName);
		for(Department department:departments){
			result.add(getFirstDegreeDepartment(department));
		}
		return new ArrayList<Department>(result);
	}
	public List<Department> getTopDepartmentsByUser(Long userId){
		if(userId == null) throw new RuntimeException("没有查询用户所在的部门的顶级部门的查询条件：用户id");
		Set<Department> result = new HashSet<Department>();
		List<Department> departments = getDepartmentsByUser(getCompanyId(), userId);
		for(Department department:departments){
			result.add(getFirstDegreeDepartment(department));
		}
		return new ArrayList<Department>(result);
	}
	public List<String> getTopDepartmentNamesByUser(String loginName){
		if(loginName == null) throw new RuntimeException("没有查询用户所在的部门的顶级部门的查询条件：用户登录名");
		Set<String> result = new HashSet<String>();
		List<Department> departments = getDepartmentsByUser(getCompanyId(), loginName);
		for(Department department:departments){
			result.add(getFirstDegreeDepartmentName(department));
		}
		return new ArrayList<String>(result);
	}
	public List<String> getTopDepartmentNamesByUser(Long userId){
		if(userId == null) throw new RuntimeException("没有查询用户所在的部门的顶级部门的查询条件：用户id");
		Set<String> result = new HashSet<String>();
		List<Department> departments = getDepartmentsByUser(getCompanyId(), userId);
		for(Department department:departments){
			result.add(getFirstDegreeDepartmentName(department));
		}
		return new ArrayList<String>(result);
	}
	public List<Long> getTopDepartmentIdsByUser(Long userId){
		if(userId == null) throw new RuntimeException("没有查询用户所在的部门的顶级部门的查询条件：用户id");
		Set<Long> result = new HashSet<Long>();
		List<Department> departments = getDepartmentsByUser(getCompanyId(), userId);
		for(Department department:departments){
			result.add(getFirstDegreeDepartmentId(department));
		}
		return new ArrayList<Long>(result);
	}
	
	/**
	 * 获得用户的顶级部门
	 * @param companyId
	 * @param loginName
	 * @return
	 */
	public List<Department> getUpstageDepartmentsByUserLike(Long companyId, String userName){
		Set<Department> result = new HashSet<Department>();
		List<Department> departments = getDepartmentsByUserLike(companyId, userName);
		for(Department department:departments){
			result.add(getFirstDegreeDepartment(department));
		}
		return new ArrayList<Department>(result);
	}

	/**
	 * 返回该部门的一级部门
	 * @param department
	 * @return
	 */
	public Department getFirstDegreeDepartment(Department department){
		return getTopDepartment(department);
	}
	
	public Department getTopDepartment(Department department){
		if(department == null) throw new RuntimeException("没有查询部门的顶级部门的查询条件：部门实体");
		Department parentDept = getParentDepartment(department.getId());
		if(parentDept!=null){
			if(parentDept.getBranch()){//如果是分支机构，则不再递归
				return department;
			}else{
				return getFirstDegreeDepartment(parentDept);
			}
		}else{
			return department;
		}
	}
	/**
	 * 返回该部门的一级部门
	 * @param department
	 * @return
	 */
	public String getFirstDegreeDepartmentName(Department department){
		return getTopDepartmentName(department);
	}
	
	public String getTopDepartmentName(Department department){
		if(department == null) throw new RuntimeException("没有查询部门的顶级部门的查询条件：部门实体");
		Department parentDept = getParentDepartment(department.getId());
		if(parentDept!=null){
			if(parentDept.getBranch()){//如果是分支机构，则不再递归
				return department.getName();
			}else{
				return getFirstDegreeDepartmentName(parentDept);
			}
		}else{
			return department.getName();
		}
	}
	/**
	 * 返回该部门的一级部门
	 * @param department
	 * @return
	 */
	public Long getFirstDegreeDepartmentId(Department department){
		return getTopDepartmentId(department);
	}
	
	public Long getTopDepartmentId(Department department){
		if(department == null) throw new RuntimeException("没有查询部门的顶级部门的查询条件：部门实体");
		Department parentDept = getParentDepartment(department.getId());
		if(parentDept!=null){
			if(parentDept.getBranch()){//如果是分支机构，则不再递归
				return department.getId();
			}else{
				return getFirstDegreeDepartmentId(parentDept);
			}
		}else{
			return department.getId();
		}
	}
	
	/**
	 * 员工查询
	 * @param department
	 * @return
	 */
	@Deprecated
	public void userSearch(String userName ,String userDepart,  boolean userSex, Long companyId, Page<com.norteksoft.acs.entity.organization.User> page){
		StringBuilder hql = new StringBuilder();
		hql.append("select distinct u from User u join u.userInfos ui join u.departmentUsers du join du.department d ");
		hql.append("where u.companyId=? and u.deleted=? and ui.deleted=? and du.deleted=? and d.deleted=? ");
		hql.append("and u.name like ? and u.sex=? and d.name like ?");
		userDao.find(page,hql.toString(), companyId,false,false,false,false,"%" + userName + "%", userSex, "%" + userDepart+ "%");
		
	}
	/**
	 * 员工查询所有性别
	 * @param department
	 * @return
	 */
	@Deprecated
	public void userSearchAllSex(String userName ,String userDepart,  Long companyId, Page<com.norteksoft.acs.entity.organization.User> page){
		StringBuilder hql = new StringBuilder();
		hql.append("select distinct u from User u join u.userInfos ui join u.departmentUsers du join du.department d ");
		hql.append("where u.companyId=? and u.deleted=? and ui.deleted=? and du.deleted=? and d.deleted=? ");
		hql.append("and u.name like ? and d.name like ?");
		userDao.find(page,hql.toString(), companyId,false,false,false,false,"%" + userName + "%", "%" + userDepart+ "%");
		
	}
	/**
	 * 获取本公司所有用户的生日
	 * @return
	 */
	 @SuppressWarnings("unchecked")
	public Map<Long,String> getUserBirthdayByCompany(Long companyId){
		List<UserInfo> userInfoList=userInfoDao.find("from UserInfo ui where ui.companyId=?  and ui.deleted=?",companyId,false);
		Map<Long,String> birthdayMap=new HashMap();
		for(int i=0;i<userInfoList.size();i++){
			if(StringUtils.isNotEmpty(userInfoList.get(i).getBirthday())){
			birthdayMap.put(userInfoList.get(i).getUser().getId(), userInfoList.get(i).getBirthday());
			}
		}
		return birthdayMap;
	}
    /**
	 * 得到无部门人员
	 * @return
	 */
	public List<UserInfo> getNoDepartmentUsers(Long companyId){
		StringBuilder sqlString = new StringBuilder();
		sqlString.append("SELECT ACS_USERINFO.* FROM ACS_USERINFO LEFT OUTER JOIN ");
		sqlString.append("(SELECT * FROM ACS_DEPARTMENT_USER WHERE ACS_DEPARTMENT_USER.DELETED = 0)");
		sqlString.append(" DEPT_USER ON ACS_USERINFO.FK_USER_ID = DEPT_USER.FK_USER_ID ");
		sqlString.append("WHERE ACS_USERINFO.DELETED=0 AND ACS_USERINFO.FK_COMPANY_ID = ? ");
		sqlString.append("AND DEPT_USER.ID IS NULL ORDER BY ACS_USERINFO.ID DESC ");
		return userInfoDao.findByJdbc( sqlString.toString(), companyId);
	}

	public void deleteUser(Long userId) {
		if(userId==null)return;
		com.norteksoft.acs.entity.organization.User user = userDao.get(userId);
		if(user!=null){
			 user.getUserInfo().setDeleted(true);
			 user.getUserInfo().setDr(1);
			 userInfoDao.save(user.getUserInfo());
		}
	}

	public void saveUser(com.norteksoft.acs.entity.organization.User user,UserInfo userInfo) {
		if(user.getCompanyId()==null){
			user.setCompanyId(ContextUtils.getCompanyId());
		}
		if(userInfo.getCompanyId()==null){
			userInfo.setCompanyId(user.getCompanyId());
		}
	    userDao.save(user);
		userInfo.setUser(user);
		userInfoDao.save(userInfo);
	}
	

	public void deleteDepartment(Long departmentId) {
		if(departmentId==null)return;
		com.norteksoft.acs.entity.organization.Department department=departmentDao.get(departmentId);
		if(department==null)return;
		
		List<com.norteksoft.acs.entity.organization.User> users=userManager.getUsersByDeptId(departmentId);
		departmentManager.deleteDepart(department,users);
	}

	@Deprecated
	public void saveDepartment(com.norteksoft.acs.entity.organization.Department department,Long companyId) {
		if(companyId==null)throw new RuntimeException("公司id不能为null");
		Company company=companyManager.getCompany(companyId);
		if(company==null)throw new RuntimeException("公司不存在");
		department.setCompany(company);
		departmentDao.save(department);
		
	}
	
	public void saveDepartment(Department department,Long companyId) {
		if(companyId==null)throw new RuntimeException("公司id不能为null");
		Company company=companyManager.getCompany(companyId);
		if(company==null)throw new RuntimeException("公司不存在");
		department.setCompany(company);
		departmentDao.save(BeanUtil.turnToDepartment(department));
		
	}

	@Deprecated
	public void saveDepartmentUser(List<Long> userIds, com.norteksoft.acs.entity.organization.Department department) {
		if(userIds==null||department==null)return;
		departmentManager.departmentToUser(department.getId(), userIds, 0,null);
	}
	
	public void saveDepartmentUser(List<Long> userIds, Department department) {
		if(userIds==null||department==null)return;
		departmentManager.departmentToUser(department.getId(), userIds, 0,null);
	}

	@SuppressWarnings("unchecked")
	public List<User> getUsersByName(String userName) {
		List<com.norteksoft.acs.entity.organization.User> list = userDao.find("from User u where u.companyId=? and u.name=? and u.deleted=? ", getCompanyId(), userName, false);
		return BeanUtil.turnToModelUserList(list);
	}

	public String getCurrentUserRolesExcludeTrustedRole() {
		Long userId=ContextUtils.getUserId();
		if(userId==null) return "";
		com.norteksoft.acs.entity.organization.User user = userDao.get(userId);
		return getRolesExcludeTrustedRole(user);
	}

	public String getUserRolesExcludeTrustedRole(Long userId) {
		if(userId==null) return "";
		com.norteksoft.acs.entity.organization.User user = userDao.get(userId);
		return getRolesExcludeTrustedRole(user);
	}
	
	@SuppressWarnings("unchecked")
	public String getSystemAdminLoginName() {
		StringBuilder hql = new StringBuilder();
		hql.append("from User u ");
		hql.append("where u.companyId=? and u.deleted=? and u.loginName like ?");
		List<com.norteksoft.acs.entity.organization.User> users = userDao.find(hql.toString(), ContextUtils.getCompanyId(),false,"%.systemAdmin%");
		if(users.size()>0)return users.get(0).getLoginName();
		return null;
	}
	@SuppressWarnings("unchecked")
	public User getSystemAdmin() {
		StringBuilder hql = new StringBuilder();
		hql.append("from User u ");
		hql.append("where u.companyId=? and u.deleted=? and u.loginName like ?");
		List<com.norteksoft.acs.entity.organization.User> users = userDao.find(hql.toString(), ContextUtils.getCompanyId(),false,"%.systemAdmin%");
		if(users.size()>0)return BeanUtil.turnToModelUser(users.get(0));
		return null;
	}

	@Deprecated
	public void saveDepartment(com.norteksoft.acs.entity.organization.Department department) {
		saveDepartment(department,ContextUtils.getCompanyId());
	}
	
	public void saveDepartment(Department department) {
		saveDepartment(department,ContextUtils.getCompanyId());
	}
	
	/**
	 * 查询公司中所有人员（不包含无部门人员）
	 * @param companyId
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public List<String> getLoginNamesByCompany(Long companyId){
		if(companyId == null) throw new RuntimeException("没有给定查询用户列表的查询条件：公司ID");
		return userDao.find("select distinct u.loginName FROM User u join u.departmentUsers du join du.department d  WHERE u.companyId=? AND u.deleted=? AND du.deleted=?  AND d.deleted=?", companyId,false,false,false);
	}
	/**
	 * 查询工作组所有人员
	 * @param companyId
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public List<String> getLoginNamesByWorkgroup(Long companyId){
		if(companyId == null) throw new RuntimeException("查询工作组人员时，没有给定查询用户列表的查询条件：公司ID");
		return userDao.find("select distinct u.loginName FROM User u join u.workgroupUsers du join du.workgroup d  WHERE u.companyId=? AND u.deleted=? AND du.deleted=?  AND d.deleted=?", companyId,false,false,false);
	}

	public void saveUser(User user) {
		com.norteksoft.acs.entity.organization.User oldUser = BeanUtil.turnToUser(user);
		userDao.save(oldUser);
	}
	@SuppressWarnings("unchecked")
	public Department getParentDepartment(Long departmentId){
		String hql = "select d.parent from Department d where d.parent is not null and d.id=? and d.deleted=? and d.parent.deleted=?";
		List<com.norteksoft.acs.entity.organization.Department> parents =  departmentDao.find(hql, departmentId,false,false);
		if(parents.size()>0)return BeanUtil.turnToModelDepartment(parents.get(0));
		return null;
	}
	@SuppressWarnings("unchecked")
	public List<Workgroup> getWorkgroupsByUserId(Long userId) {
		if(userId==null) throw new RuntimeException("没有给出查询用户所在工作组列表的查询条件：用户id");
		StringBuilder hql = new StringBuilder();
		hql.append("select wg from Workgroup wg join wg.workgroupUsers wgu join wgu.user u ");
		hql.append("where u.companyId=? and u.id=? and u.deleted=? and wgu.deleted=? and wg.deleted=?");
		List<com.norteksoft.acs.entity.organization.Workgroup> list =  workGroupDao.find(hql.toString(), getCompanyId(), userId, false, false, false);
		return BeanUtil.turnToModelWorkgroupList(list);
	}
	
	/**
	 * 根据用户id查询用户所在的部门的上级部门
	 * @param userId
	 * @return
	 */
	@SuppressWarnings("unchecked")
	@Transactional(readOnly = true)
	public List<Department> getParentDepartmentsByUserId(Long userId){
		if(userId == null) throw new RuntimeException("没有查询用户所在的部门的上级部门的查询条件：用户id");
		StringBuilder hql = new StringBuilder(" select d from Department d join d.children sd join sd.departmentUsers du join du.user u ");
		hql.append(" where u.companyId=? and u.id=? and u.deleted=? and du.deleted=? and sd.deleted=? and d.deleted=?");
		List<com.norteksoft.acs.entity.organization.Department> list = departmentDao.find(hql.toString(), getCompanyId(), userId, false, false, false,false);
		return BeanUtil.turnToModelDepartmentList(list);
	}
	
	@SuppressWarnings("unchecked")
	public Set<User> getUsersByRoleIdExceptTrustedRole( Long roleId){
		if(roleId == null) throw new RuntimeException("没有给定查询拥有某角色的用户列表的查询条件：角色id");
		Set<com.norteksoft.acs.entity.organization.User> result = new LinkedHashSet<com.norteksoft.acs.entity.organization.User>();
		//users role
		StringBuilder usersByRole = new StringBuilder();
		usersByRole.append("select u from User u join u.roleUsers ru join ru.role r  ");
		usersByRole.append("where r.id = ? and u.companyId=? and r.deleted=false and ");
		usersByRole.append("ru.deleted=false and ru.consigner is null and u.deleted=false ");
		List<com.norteksoft.acs.entity.organization.User> roleUsers = userDao.find(usersByRole.toString(),  roleId, getCompanyId());
		//users department role
		StringBuilder usersByDeptRoleHql = new StringBuilder();
		usersByDeptRoleHql.append("select u from User u join u.departmentUsers du join du.department d ");
		usersByDeptRoleHql.append("join d.roleDepartments rd join rd.role r ");
		usersByDeptRoleHql.append("where r.id = ? and d.company.id=? and r.deleted=false and ");
		usersByDeptRoleHql.append("rd.deleted=false and d.deleted=false and du.deleted=false and u.deleted=false ");
		List<com.norteksoft.acs.entity.organization.User> roleDeptUsers = userDao.find(usersByDeptRoleHql.toString(), roleId, getCompanyId());
		//users branches role
		StringBuilder usersByBranchesRoleHql = new StringBuilder();
		usersByBranchesRoleHql.append("select u from User u where u.subCompanyId is not null and u.subCompanyId in (select d.id from Department d join d.roleDepartments rd join rd.role r where r.id = ? and d.company.id=? and d.branch=true and r.deleted=false and rd.deleted=false and d.deleted=false) and u.deleted=false ");
		List<com.norteksoft.acs.entity.organization.User> roleBranchesUsers = roleDao.find(usersByBranchesRoleHql.toString(), roleId,getCompanyId());
		//users work-group role
		StringBuilder usersByWgRoleHql = new StringBuilder();
		usersByWgRoleHql.append("select u from User u join u.workgroupUsers wgu join wgu.workgroup wg ");
		usersByWgRoleHql.append("join wg.roleWorkgroups rwg join rwg.role r ");
		usersByWgRoleHql.append("where  r.id = ? and wg.company.id=? and r.deleted=false and ");
		usersByWgRoleHql.append("rwg.deleted=false and wg.deleted=false and wgu.deleted=false and u.deleted=false ");
		List<com.norteksoft.acs.entity.organization.User> roleWgUsers = userDao.find(usersByWgRoleHql.toString(), roleId, getCompanyId());
		
		result.addAll(roleUsers);
		result.addAll(roleDeptUsers);
		result.addAll(roleBranchesUsers);
		result.addAll(roleWgUsers);
		return BeanUtil.turnToModelUserSet(result);
	}
	/**
	 * 当前用户是否具有某角色
	 * @param roleId
	 * @return
	 */
	public boolean isUserHasRole( Long roleId){
		if(roleId == null) throw new RuntimeException("没有给定查询拥有某角色的用户列表的查询条件：角色id");
		boolean isHasRole=false;
		isHasRole = userHasRole(roleId);
		if(isHasRole)return true;
		isHasRole = deptHasRole(roleId);
		if(isHasRole)return true;
		isHasRole = wgHasRole(roleId);
		if(isHasRole)return true;
		return isHasRole;
	}
	
	private boolean userHasRole(Long roleId){
		StringBuilder usersByRole = new StringBuilder();
		usersByRole.append("select count(u.id) from User u join u.roleUsers ru join ru.role r  ");
		usersByRole.append("where r.id = ? and u.id=? and u.companyId=? and r.deleted=false and ");
		usersByRole.append("ru.deleted=false and ru.consigner is null and u.deleted=false ");
		Object num = userDao.createQuery(usersByRole.toString(), roleId,ContextUtils.getUserId(),getCompanyId()).uniqueResult();
		if(num!=null){
			Integer count = Integer.valueOf(num.toString());
			if(count>0)return true;
		}
		return false;
	}
	
	private boolean deptHasRole(Long roleId){
		StringBuilder usersByDeptRoleHql = new StringBuilder();
		usersByDeptRoleHql.append("select count(u.id) from User u join u.departmentUsers du join du.department d ");
		usersByDeptRoleHql.append("join d.roleDepartments rd join rd.role r ");
		usersByDeptRoleHql.append("where r.id = ? and u.id=? and d.company.id=? and r.deleted=false and ");
		usersByDeptRoleHql.append("rd.deleted=false and d.deleted=false and du.deleted=false and u.deleted=false ");
		Object num = userDao.createQuery(usersByDeptRoleHql.toString(), roleId,ContextUtils.getUserId(),getCompanyId()).uniqueResult();
		if(num!=null){
			Integer count = Integer.valueOf(num.toString());
			if(count>0)return true;
		}
		return false;
	}
	
	private boolean wgHasRole(Long roleId){
		StringBuilder usersByWgRoleHql = new StringBuilder();
		usersByWgRoleHql.append("select count(u.id) from User u join u.workgroupUsers wgu join wgu.workgroup wg ");
		usersByWgRoleHql.append("join wg.roleWorkgroups rwg join rwg.role r ");
		usersByWgRoleHql.append("where  r.id = ? and u.id=? and wg.company.id=? and r.deleted=false and ");
		usersByWgRoleHql.append("rwg.deleted=false and wg.deleted=false and wgu.deleted=false and u.deleted=false ");
		Object num = userDao.createQuery(usersByWgRoleHql.toString(), roleId,ContextUtils.getUserId(),getCompanyId()).uniqueResult();
		if(num!=null){
			Integer count = Integer.valueOf(num.toString());
			if(count>0)return true;
		}
		return false;
	}
	
	/**
	 * 获得用户id的集合
	 * @param roleId
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public Set<Long> getUserIdsByRoleIdExceptTrustedRole( Long roleId){
		if(roleId == null) throw new RuntimeException("没有给定查询拥有某角色的用户列表的查询条件：角色id");
		Set<Long> result = new LinkedHashSet<Long>();
		//users role
		StringBuilder usersByRole = new StringBuilder();
		usersByRole.append("select u.id from User u join u.roleUsers ru join ru.role r  ");
		usersByRole.append("where r.id = ? and u.companyId=? and r.deleted=false and ");
		usersByRole.append("ru.deleted=false and ru.consigner is null and u.deleted=false ");
		List<Long> roleUsers = userDao.find(usersByRole.toString(),  roleId, getCompanyId());
		//users department role
		StringBuilder usersByDeptRoleHql = new StringBuilder();
		usersByDeptRoleHql.append("select u.id from User u join u.departmentUsers du join du.department d ");
		usersByDeptRoleHql.append("join d.roleDepartments rd join rd.role r ");
		usersByDeptRoleHql.append("where r.id = ? and d.company.id=? and r.deleted=false and ");
		usersByDeptRoleHql.append("rd.deleted=false and d.deleted=false and du.deleted=false and u.deleted=false ");
		List<Long> roleDeptUsers = userDao.find(usersByDeptRoleHql.toString(), roleId, getCompanyId());
		//users branches role
		StringBuilder usersByBranchesRoleHql = new StringBuilder();
		usersByBranchesRoleHql.append("select u.id from User u where u.subCompanyId is not null and u.subCompanyId in (select d.id from Department d join d.roleDepartments rd join rd.role r where r.id = ? and d.company.id=? and d.branch=true and r.deleted=false and rd.deleted=false and d.deleted=false) and u.deleted=false ");
		List<Long> roleBranchesUsers = roleDao.find(usersByBranchesRoleHql.toString(), roleId,getCompanyId());
		//users work-group role
		StringBuilder usersByWgRoleHql = new StringBuilder();
		usersByWgRoleHql.append("select u.id from User u join u.workgroupUsers wgu join wgu.workgroup wg ");
		usersByWgRoleHql.append("join wg.roleWorkgroups rwg join rwg.role r ");
		usersByWgRoleHql.append("where  r.id = ? and wg.company.id=? and r.deleted=false and ");
		usersByWgRoleHql.append("rwg.deleted=false and wg.deleted=false and wgu.deleted=false and u.deleted=false ");
		List<Long> roleWgUsers = userDao.find(usersByWgRoleHql.toString(), roleId, getCompanyId());
		
		result.addAll(roleUsers);
		result.addAll(roleDeptUsers);
		result.addAll(roleBranchesUsers);
		result.addAll(roleWgUsers);
		return result;
	}
	
	/**
	 * 根据父部门id查询该父部门下所有子部门的id
	 */
	@SuppressWarnings("unchecked")
	@Transactional(readOnly = true)
	public List<Long> getSubDepartmentIdList(Long paternDepartmentId) {
		if(paternDepartmentId == null) throw new RuntimeException("没有给定查询子部门集合的查询条件： 父部门ID");
		List<Long> list =  departmentDao.find(
				"select d.id FROM Department d WHERE d.parent.id=? AND d.deleted=? and d.branch=?  ORDER BY d.weight desc", 
				paternDepartmentId, false,false);
		return list;
	}
	
	/**
	 * 查询所有用户（包含无部门人员，也包含三员Admin）
	 * @param companyId
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public List<Long> getUserIdsByCompany(Long companyId){
		if(companyId == null) throw new RuntimeException("没有给定查询用户列表的查询条件：公司ID");
		List<Long> list = userDao.find("select u.id FROM User u  WHERE u.companyId=? AND u.deleted=?  ORDER BY u.weight DESC", companyId,false );
		return list;
	}
	
	public Department getTopDepartment(Long departmentId){
		if(departmentId == null) throw new RuntimeException("没有查询部门的顶级部门的查询条件：部门id");
		Department parentDept = getParentDepartment(departmentId);
		if(parentDept!=null){
			if(parentDept.getBranch()){//如果是分支机构，则不再递归
				return getDepartmentById(departmentId);
			}else{
				return getFirstDegreeDepartment(parentDept);
			}
		}else{
			return getDepartmentById(departmentId);
		}
	}
	
	/**
	 * 根据用户id查询用户所在的部门的上级部门
	 * @param userId
	 * @return
	 */
	@SuppressWarnings("unchecked")
	@Transactional(readOnly = true)
	public List<Long> getParentDepartmentIdsByUserId(Long userId){
		if(userId == null) throw new RuntimeException("没有查询用户所在的部门的上级部门的查询条件：用户id");
		StringBuilder hql = new StringBuilder(" select d.id from Department d join d.children sd join sd.departmentUsers du join du.user u ");
		hql.append(" where u.companyId=? and u.id=? and u.deleted=? and du.deleted=? and sd.deleted=? and d.deleted=?");
		return departmentDao.find(hql.toString(), getCompanyId(), userId, false, false, false,false);
	}
	
	/**
	 * 查询公司中所有人员（包含无部门人员）
	 * @param companyId
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public List<Long> getAllUserIdsByCompany(Long companyId){
		if(companyId == null) throw new RuntimeException("没有给定查询用户列表的查询条件：公司ID");
		return userDao.find("select u.id FROM User u  WHERE u.companyId=? AND u.deleted=?", companyId,false);
	}
	/**
	 * 查询公司中所有人员（包含无部门人员,但不包含三员systemAdmin、securityAdmin、auditAdmin）
	 * @param companyId
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public List<Long> getAllUserIdsWithoutAdminByCompany(){
		return userDao.find("select u.id FROM User u  WHERE u.companyId=? AND u.deleted=? AND (u.loginName not like ? AND u.loginName not like ? AND u.loginName not like ?)", getCompanyId(),false,"%.systemAdmin","%.securityAdmin","%.auditAdmin");
	}

	public List<User> getUsersByCompanyCode(String companyCode) {
		return userManager.getUsersByCompanyCode(companyCode);
	}
	
	/**
	 * 
	 * @param 得到分支机构下所有人员的id（不包含子分支机构但是包含无部门和下属部门的人员）
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public List<Long> getAllUserIdsByBranch(Long branchId){
//		sqlserver不支持该写法，报异常“如果指定了 SELECT DISTINCT，那么 ORDER BY 子句中的项就必须出现在选择列表中”，所以改为了当前实现方法
//		List<Long> list = userDao.find(
//				"select distinct user.id from User user inner join user.departmentUsers  du where user.deleted=? and du.deleted = ? and user.companyId=? and du.subCompanyId=? order by user.weight desc ",
		List<Object[]> list = userDao.find(
				"select distinct user.id,user.weight from User user inner join user.departmentUsers  du where user.deleted=? and du.deleted = ? and user.companyId=? and du.subCompanyId=? order by user.weight desc ",
				false,false,getCompanyId(),branchId);
		Set<Long> tempresult = new HashSet<Long>();
		List<Long> result = new ArrayList<Long>();
		for(Object[] obj:list){
			Long userid = (Long)obj[0];
			tempresult.add(userid);
		}
		result.addAll(tempresult);
		return result;
	}
	
	/**
	 * 根据部门ID查询该部门所有的人员的id
	 * 
	 * @param departmentId 部门Id
	 * @return List<User>
	 */
	@Transactional(readOnly = true)
	@SuppressWarnings("unchecked")
	public List<Long> getUserIdsByDepartment(Long departmentId) {
		if(departmentId == null) throw new RuntimeException("没有给定查询用户集合的查询条件：部门ID");
		String hql = "select u.id FROM DepartmentUser d join d.user  u join u.userInfos ui WHERE u.deleted=? and d.department.id=? AND d.deleted=? order by u.weight desc";
		return  depUserDao.find(hql, false,departmentId,false);
	}
	
	@SuppressWarnings("unchecked")
	public Department getDepartmentByShortTitle(String shortTitle){
		if(shortTitle == null) throw new RuntimeException("没有给定查询部门时的查询条件：部门简称");
		List<com.norteksoft.acs.entity.organization.Department> depts = departmentDao.find("from Department d where d.company.id=? and d.shortTitle=? and d.deleted=?", getCompanyId(), shortTitle, false);
		if(depts.size() == 1){
			return BeanUtil.turnToModelDepartment(depts.get(0));
		}
		return null;
	}
	
	@SuppressWarnings("unchecked")
	@Transactional(readOnly = true)
	public List<Long> getAllWorkgroupIds() {
		List<Long> workGroupids = workGroupDao.find(
				"select w.id FROM Workgroup w WHERE w.company.id=? AND w.deleted=?  ORDER BY w.subCompanyId,w.weight", getCompanyId(),false);
		return workGroupids;
	}
	@SuppressWarnings("unchecked")
	public List<Long> getUserIdsWithWorkgroup() {
		return userDao.find("select distinct u.id FROM User u join u.workgroupUsers du join du.workgroup d  WHERE u.companyId=? AND u.deleted=? AND du.deleted=?  AND d.deleted=?", ContextUtils.getCompanyId(),false,false,false);
	}
	public List<Long> getUserIdsWithDepartment() {
		Long companyId = ContextUtils.getCompanyId();
		List<Long> allUserIds = getAllUserIdsByCompany(companyId);
		List<Long> noDeptUserIds = getUserIdsWithoutDepartment();
		allUserIds.removeAll(noDeptUserIds);
		return allUserIds;
	}
	@SuppressWarnings("unchecked")
	public List<Long> getUserIdsByWorkgroup(Long workgroupId) {
//		sqlserver不支持该写法，报异常“如果指定了 SELECT DISTINCT，那么 ORDER BY 子句中的项就必须出现在选择列表中”，所以改为了当前实现方法
//		String hql = "select distinct u.id FROM WorkgroupUser d join d.user u WHERE u.deleted=? and  d.workgroup.id=? AND d.deleted=? ORDER BY d.user.weight DESC";
		String hql = "select distinct u.id,d.user.weight FROM WorkgroupUser d join d.user u WHERE u.deleted=? and  d.workgroup.id=? AND d.deleted=? ORDER BY d.user.weight DESC";
		List<Object[]> list = userDao.find(hql, ContextUtils.getCompanyId(),false,false,false,workgroupId);
		Set<Long> tempresult = new HashSet<Long>();
		List<Long> result = new ArrayList<Long>();
		for(Object[] obj:list){
			Long userid = (Long)obj[0];
			tempresult.add(userid);
		}
		result.addAll(tempresult);
		return result;
	}
	
	/**
	 * 查询在同一部门的所有用户
	 * @param companyId
	 * @param userLoginName
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public List<User> getUsersInSameDept(Long userId){
		StringBuilder hql = new StringBuilder();
		hql.append("select DISTINCT u from User u join u.departmentUsers du join du.department d ");
		hql.append("join d.departmentUsers du_ join du_.user u_ ");
		hql.append("where d.company.id=? and u_.id = ? and u.deleted=false and ");
		hql.append("du.deleted=false and d.deleted=false and u_.deleted=false and du_.deleted=false");
		return BeanUtil.turnToModelUserList(userDao.find(hql.toString(), getCompanyId(), userId));
	}
	
	@SuppressWarnings("unchecked")
	public List<Department> getDepartmentIfHasWorkGroup() {
		String hql="select DISTINCT wk.subCompanyId from Workgroup wk where wk.subCompanyId is not null and wk.company.id=? and wk.deleted=?";
		List<Long> branchIds=workGroupDao.find(hql, ContextUtils.getCompanyId(),false);
		if(branchIds!=null&&!branchIds.isEmpty()){
			List<com.norteksoft.acs.entity.organization.Department> depts=departmentDao.find("from Department d where d.deleted=? and d.branch=? and d.company.id=? and d.id in ("+branchIds.toString().substring(1, branchIds.toString().length()-1)+")", false,true,ContextUtils.getCompanyId());
			return  BeanUtil.turnToModelDepartmentList(depts);
		}else{
			return BeanUtil.turnToModelDepartmentList(new ArrayList<com.norteksoft.acs.entity.organization.Department>());
		}
		
		
	}

	@SuppressWarnings("unchecked")
	public List<User> getUsersBySearchValue(String searchValue) {
		String hql="from User u where u.name like ? and u.deleted=false and u.companyId=? ";
		List<com.norteksoft.acs.entity.organization.User> users=userDao.find(hql, "%"+searchValue+"%",ContextUtils.getCompanyId());
		return BeanUtil.turnToModelUserList(users);
	}
	@SuppressWarnings("unchecked")
	public List<Department> getDepartmentsBySearchValue(String searchValue) {
		String hql="from Department d where d.name like ? and d.deleted=false and d.company.id=? ";
		List<com.norteksoft.acs.entity.organization.Department> departments=departmentDao.find(hql, "%"+searchValue+"%",ContextUtils.getCompanyId());
		return BeanUtil.turnToModelDepartmentList(departments);
	}

	@SuppressWarnings("unchecked")
	public List<Workgroup> getWorkGroupsBySearchValue(String searchValue) {
		String hql="from Workgroup w where w.name like ? and w.deleted=false and w.company.id=? ";
		List<com.norteksoft.acs.entity.organization.Workgroup> workgroups=workGroupDao.find(hql, "%"+searchValue+"%",ContextUtils.getCompanyId());
		return BeanUtil.turnToModelWorkgroupList(workgroups);
	}

	public long getChildDepartmentCount(Department department) {
		if(department != null){
			String hql = "select count(d) FROM Department d   WHERE d.deleted=? and (d.parent is not null and d.parent.id=?)";
			return  departmentDao.findLong(hql, false,department.getId());
		}
		return 0;
	}

	public long getUserCountInDepartment(Department department) {
		if(department != null){
			String hql = "select count(u) FROM DepartmentUser d join d.user  u join u.userInfos ui WHERE u.deleted=? and d.department.id=? AND d.deleted=?";
			return  depUserDao.findLong(hql, false,department.getId(),false);
		}
		return 0;
	}
}
