package com.norteksoft.acs.service.authorization;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
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
import com.norteksoft.acs.entity.authorization.BusinessSystem;
import com.norteksoft.acs.entity.authorization.Role;
import com.norteksoft.acs.entity.organization.Company;
import com.norteksoft.acs.entity.organization.Department;
import com.norteksoft.acs.entity.organization.User;
import com.norteksoft.acs.entity.organization.Workgroup;
import com.norteksoft.acs.service.query.QueryManager;

/**
 * 供权限API使用的Manager
 * @author xiaoj
 */
@Service
@Transactional
public class AcsApiManager {

	public final static String DELETED = "deleted";
	private static final String TRUE_STRING = "true";
	private static final String FALSE_STRING = "false";
	private SimpleHibernateTemplate<Department, Long> departmentDao;
	private SimpleHibernateTemplate<Workgroup, Long> workGroupDao;
	private SimpleHibernateTemplate<Role, Long> roleDao;
	private SimpleHibernateTemplate<User, Long> userDao;
	private QueryManager queryManager;
	private SimpleHibernateTemplate<BusinessSystem, Long> businessDao;
	private SimpleHibernateTemplate<Company, Long> companyDao;

	@Autowired
	public void setSessionFactory(SessionFactory sessionFactory) {
		departmentDao = new SimpleHibernateTemplate<Department, Long>(sessionFactory, Department.class);
		workGroupDao = new SimpleHibernateTemplate<Workgroup, Long>(sessionFactory, Workgroup.class);
		roleDao = new SimpleHibernateTemplate<Role, Long>(sessionFactory, Role.class);
		userDao = new SimpleHibernateTemplate<User, Long>(sessionFactory, User.class);
		businessDao=new SimpleHibernateTemplate<BusinessSystem, Long>(sessionFactory, BusinessSystem.class);
		companyDao=new SimpleHibernateTemplate<Company, Long>(sessionFactory, Company.class);
	}
	
    public List<Department> getAllDeptsInOrder(Long companyId){
		return departmentDao.find("FROM Department d WHERE (d.company.id=? AND d.deleted=? and d.parent is null) ORDER BY d.weight desc", companyId, false);
	}
	
	@Autowired
	public void setQueryManager(QueryManager queryManager) {
		this.queryManager = queryManager;
	}
	
	/**
	 * 查询在线用户数
	 * @return
	 */
	public Long getOnlineUserCount(){
		return queryManager.getOnlineUserCount();
	}
	
	/**
	 * 查询一个部门的所有子部门
	 * @param companyId
	 * @param parentDeptName
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public List<Department> getSubDeptsByParentDept(Long companyId, String parentDeptCode){
		StringBuilder hql = new StringBuilder();
		hql.append("select d from Department d join d.parent pd ");
		hql.append("where pd.company.id = ? and pd.code = ? and d.deleted = false and pd.deleted = false order by d.weight desc");
		return  departmentDao.find(hql.toString(), companyId, parentDeptCode);
	}
	
	@SuppressWarnings("unchecked")
	public String hasSubDepartment(Department dept){
		StringBuilder hql = new StringBuilder();
		hql.append("select d from Department d join d.parent pd ");
		hql.append("where pd.id = ? and d.deleted = false and pd.deleted = false");
		List<Department> subDepts =   departmentDao.find(hql.toString(),  dept.getId());
		if(subDepts.size()>0){
			return TRUE_STRING;
		}else{
			return FALSE_STRING;
		}
	}

	/**
	 * 查询所有的工作中
	 * @param companyId
	 * @return
	 */
	public List<Workgroup> getAllWorkGroups(Long companyId){
		return workGroupDao.findByCriteria(
				Restrictions.eq("company.id", companyId), 
				Restrictions.eq(DELETED, false));
	}

	/**
	 * 查询系统所有的角色
	 * @param systemId
	 * @return
	 */
	public List<Role> getAllRoles(Long systemId,Long companyId){
		String hql = "from Role sr where sr.businessSystem.id=? and sr.deleted=? and (sr.companyId is null or sr.companyId=?) order by sr.weight desc";
		return roleDao.find( hql,systemId, false,companyId);
	}

	/**
	 * 查询公司所有的用户
	 * @param companyId
	 * @return
	 */
	public List<User> getAllUsers(Long companyId){
		return userDao.findByCriteria(
				Restrictions.eq("companyId", companyId), 
				Restrictions.eq(DELETED, false));
	}

	/**
	 * 查询公司某部门下所有的用户
	 * @param companyId
	 * @param departmentName
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public List<User> getUsersByDeptName(Long companyId, String departmentName){
		StringBuilder hql = new StringBuilder();
		hql.append("select u from User u join u.departmentUsers du join du.department d ");
		hql.append("where d.company.id=? and d.name = ? and u.deleted=false and ");
		hql.append("du.deleted=false and d.deleted=false and d.branch=? order by u.weight desc");
		return userDao.find(hql.toString(), companyId, departmentName,false);
	}
	
	/**
	 * 查询公司某部门下所有的用户
	 * @param companyId
	 * @param departmentName
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public List<User> getUsersByDeptCode(Long companyId, String departmentCode){
		StringBuilder hql = new StringBuilder();
		hql.append("select u from User u join u.departmentUsers du join du.department d ");
		hql.append("where d.company.id=? and d.code = ? and u.deleted=false and ");
		hql.append("du.deleted=false and d.deleted=false and d.branch=? order by u.weight desc");
		return userDao.find(hql.toString(), companyId, departmentCode,false);
	}

	/**
	 * 查询公司某工作组下所有的用户
	 * @param companyId
	 * @param workGroupName
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public List<User> getUsersByWorkGroupName(Long companyId, String workGroupName){
		StringBuilder hql = new StringBuilder();
		hql.append("select u from User u join u.workgroupUsers wgu join wgu.workgroup wg ");
		hql.append("where wg.company.id=? and wg.name = ? and u.deleted=false and ");
		hql.append("wgu.deleted=false and wg.deleted=false order by u.weight desc");
		return userDao.find(hql.toString(), companyId, workGroupName);
	}
	
	/**
	 * 查询公司某工作组下所有的用户
	 * @param companyId
	 * @param workGroupCode
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public List<User> getUsersByWorkGroupCode(Long companyId, String workGroupCode){
		StringBuilder hql = new StringBuilder();
		hql.append("select u from User u join u.workgroupUsers wgu join wgu.workgroup wg ");
		hql.append("where wg.company.id=? and wg.code = ? and u.deleted=false and ");
		hql.append("wgu.deleted=false and wg.deleted=false order by u.weight desc");
		return userDao.find(hql.toString(), companyId, workGroupCode);
	}

	/**
	 * 根据某系统的角色查询公司所有的用户
	 * @param systemId
	 * @param companyId
	 * @param roleName
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public Set<User> getUsersByRoleName(Long systemId, Long companyId, String roleName){
		Set<User> result = new HashSet<User>();
		//users role
		StringBuilder usersByRole = new StringBuilder();
		usersByRole.append("select u from User u join u.roleUsers ru join ru.role r ");
		usersByRole.append("where r.name = ? and u.companyId=? and r.deleted=false and ");
		usersByRole.append("ru.deleted=false and u.deleted=false");
		List<User> roleUsers = userDao.find(usersByRole.toString(), roleName, companyId);
		//users department role
		StringBuilder usersByDeptRoleHql = new StringBuilder();
		usersByDeptRoleHql.append("select u from User u join u.departmentUsers du join du.department d ");
		usersByDeptRoleHql.append("join d.roleDepartments rd join rd.role r ");
		usersByDeptRoleHql.append("where r.name = ? and d.company.id=? and r.deleted=false and ");
		usersByDeptRoleHql.append("rd.deleted=false and d.deleted=false and du.deleted=false and u.deleted=false");
		List<User> roleDeptUsers = userDao.find(usersByDeptRoleHql.toString(), roleName, companyId);
		//users branches role
		StringBuilder usersByBranchesRoleHql = new StringBuilder();
		usersByBranchesRoleHql.append("select u from User u join u.departmentUsers du join du.department d ");
		usersByBranchesRoleHql.append("join d.roleDepartments rd join rd.role r ");
		usersByBranchesRoleHql.append("where r.name = ? and d.company.id=? and d.branch=true and u.subCompanyId is not null and u.subCompanyId=d.id and r.deleted=false and ");
		usersByBranchesRoleHql.append("rd.deleted=false and d.deleted=false and du.deleted=false and u.deleted=false");
		List<User> roleBranchesUsers = userDao.find(usersByBranchesRoleHql.toString(), roleName, companyId);
		//users work-group role
		StringBuilder usersByWgRoleHql = new StringBuilder();
		usersByWgRoleHql.append("select u from User u join u.workgroupUsers wgu join wgu.workgroup wg ");
		usersByWgRoleHql.append("join wg.roleWorkgroups rwg join rwg.role r join r.businessSystem rbs ");
		usersByWgRoleHql.append("where rbs.id=? and r.name = ? and wg.company.id=? and rbs.deleted=false and r.deleted=false and ");
		usersByWgRoleHql.append("rwg.deleted=false and wg.deleted=false and wgu.deleted=false and u.deleted=false");
		List<User> roleWgUsers = userDao.find(usersByWgRoleHql.toString(), systemId, roleName, companyId);
		
		result.addAll(roleUsers);
		result.addAll(roleDeptUsers);
		result.addAll(roleBranchesUsers);
		result.addAll(roleWgUsers);
		return result;
	}
	
	/**
	 * 根据某系统的角色查询公司所有的用户
	 * @param systemId
	 * @param companyId
	 * @param roleName
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public Set<User> getUsersByRoleCode(Long systemId, Long companyId, String roleCode){
		Set<User> result = new HashSet<User>();
		//users role
		StringBuilder usersByRole = new StringBuilder();
		usersByRole.append("select u from User u join u.roleUsers ru join ru.role r ");
		usersByRole.append("where r.code = ? and u.companyId=? and r.deleted=false and ");
		usersByRole.append("ru.deleted=false and u.deleted=false");
		List<User> roleUsers = userDao.find(usersByRole.toString(), roleCode, companyId);
		//users department role
		StringBuilder usersByDeptRoleHql = new StringBuilder();
		usersByDeptRoleHql.append("select u from User u join u.departmentUsers du join du.department d ");
		usersByDeptRoleHql.append("join d.roleDepartments rd join rd.role r ");
		usersByDeptRoleHql.append("where r.code = ? and d.company.id=? and r.deleted=false and ");
		usersByDeptRoleHql.append("rd.deleted=false and d.deleted=false and du.deleted=false and u.deleted=false");
		List<User> roleDeptUsers = userDao.find(usersByDeptRoleHql.toString(), roleCode, companyId);
		//users work-group role
		StringBuilder usersByWgRoleHql = new StringBuilder();
		usersByWgRoleHql.append("select u from User u join u.workgroupUsers wgu join wgu.workgroup wg ");
		usersByWgRoleHql.append("join wg.roleWorkgroups rwg join rwg.role r join r.businessSystem rbs ");
		usersByWgRoleHql.append("where rbs.id=? and r.code = ? and wg.company.id=? and rbs.deleted=false and r.deleted=false and ");
		usersByWgRoleHql.append("rwg.deleted=false and wg.deleted=false and wgu.deleted=false and u.deleted=false");
		List<User> roleWgUsers = userDao.find(usersByWgRoleHql.toString(), systemId, roleCode, companyId);
		
		result.addAll(roleUsers);
		result.addAll(roleDeptUsers);
		result.addAll(roleWgUsers);
		return result;
	}
	
	/**
	 * 查询在同一部门的所有用户
	 * @param companyId
	 * @param userLoginName
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public List<User> getUsersInSameDept(Long companyId, String userLoginName){
		StringBuilder hql = new StringBuilder();
		hql.append("select DISTINCT u from User u join u.departmentUsers du join du.department d ");
		hql.append("join d.departmentUsers du_ join du_.user u_ ");
		hql.append("where d.company.id=? and u_.loginName = ? and u.deleted=false and ");
		hql.append("du.deleted=false and d.deleted=false and u_.deleted=false and du_.deleted=false order by u.weight desc");
		return userDao.find(hql.toString(), companyId, userLoginName);
	}
	
	/**
	 * 根据特定条件查询用户(WF使用)
	 * @param companyId
	 * @param conditions
	 * @return
	 */
	public List<User> getUsersByCondition(Long companyId, String conditions){
		StringBuilder sql = getQuerySql();
		if(StringUtils.isNotEmpty(conditions)){
			sql.append(" and ").append(conditions);
		}
		return userDao.findByJdbc(sql.toString(), companyId);
	}
	
	private StringBuilder getQuerySql(){
		StringBuilder sql = new StringBuilder();
		sql.append("SELECT DISTINCT u.* FROM acs_user u ");
		sql.append("LEFT JOIN acs_department_user du ON du.fk_user_id = u.id and du.deleted = 0 ");
		sql.append("LEFT JOIN acs_department d ON d.id = du.fk_department_id and d.deleted = 0 ");
		sql.append("LEFT JOIN acs_workgroup_user wgu ON wgu.fk_user_id = u.id and wgu.deleted = 0 ");
		sql.append("LEFT JOIN acs_workgroup wg ON wg.id = wgu.fk_workgroup_id and wg.deleted = 0 ");
		sql.append("LEFT JOIN acs_role_user ru ON ru.fk_user_id = u.id and ru.deleted = 0 ");
		sql.append("LEFT JOIN acs_role r ON r.id = ru.fk_role_id and r.deleted = 0 ");
		sql.append("LEFT JOIN acs_role_department rd ON rd.fk_role_id = r.id AND rd.fk_department_id = d.id and rd.deleted = 0 ");
		sql.append("LEFT JOIN acs_role_workgroup rwg ON rwg.fk_role_id = r.id AND rwg.fk_workgroup_id = wg.id and rwg.deleted = 0 ");
		sql.append("WHERE u.deleted = 0 and u.fk_company_id = ?  ");
		return sql;
	}
	
	/**
	 * 查询不再任何部门的用户
	 * @param companyId
	 * @return
	 */
	public List<User> getUsersNotInDept(Long companyId){
		StringBuilder sqlString = new StringBuilder();
		sqlString.append("SELECT ACS_USER.* FROM ACS_USER LEFT OUTER JOIN ");
		sqlString.append("(SELECT * FROM ACS_DEPARTMENT_USER WHERE ACS_DEPARTMENT_USER.DELETED = 0)");
		sqlString.append(" DEPT_USER ON ACS_USER.ID = DEPT_USER.FK_USER_ID ");
		sqlString.append("WHERE ACS_USER.DELETED=0 AND ACS_USER.FK_COMPANY_ID = ? ");
		sqlString.append("AND DEPT_USER.ID IS NULL ORDER BY ACS_USER.WEIGHT DESC");
		return userDao.findByJdbc(sqlString.toString(), companyId);
	}
	
	//分支机构的无部门用户
	public List<User> getUsersWithoutBranch(Long companyId,Long branchId){
		StringBuilder sqlString = new StringBuilder();
		sqlString.append("SELECT u.* FROM ACS_USERINFO ");
		sqlString.append("inner join ACS_USER u on ACS_USERINFO.FK_USER_ID=u.id ");
		sqlString.append("LEFT OUTER JOIN ");
		sqlString.append("(SELECT * FROM ACS_DEPARTMENT_USER WHERE ACS_DEPARTMENT_USER.DELETED = 0)");
		sqlString.append(" DEPT_USER ON ACS_USERINFO.FK_USER_ID = DEPT_USER.FK_USER_ID ");
		sqlString.append("WHERE ACS_USERINFO.DELETED=0 AND ACS_USERINFO.FK_COMPANY_ID = ? ");
		sqlString.append("AND DEPT_USER.FK_DEPARTMENT_ID=? AND DEPT_USER.SUB_COMPANY_ID=? ORDER BY u.WEIGHT DESC ");
		
		return userDao.findByJdbc(sqlString.toString(), companyId,branchId,branchId);
	}
	
	@SuppressWarnings("unchecked")
	public List<BusinessSystem> getAllBusiness(Long companyId) {
		String hql = "select si.product.systemId from SubscriberItem si join si.subsciber s where s.tenantId=? and si.invalidDate>?";
		List<Long> idList = businessDao.find(hql, companyId, new Date());
		if(idList.isEmpty()){
			return new ArrayList<BusinessSystem>();
		}
		return businessDao.findByCriteria(Restrictions.in("id",idList),Restrictions.eq("deleted",false));
	}
	public BusinessSystem getSystemBySystemCode(String code) {
		BusinessSystem bs = (BusinessSystem) businessDao.findUnique(
				"from BusinessSystem bs where bs.code=? and bs.deleted=?", code, false);
		return bs;
	}
	public BusinessSystem getSystemById(Long systemId) {
		BusinessSystem bs = (BusinessSystem) businessDao.findUnique(
				"from BusinessSystem bs where bs.id=? and bs.deleted=?", systemId, false);
		return bs;
	}
	@SuppressWarnings("unchecked")
	public boolean hasBranch(Long companyId){
		List<Department> depts =  departmentDao.find("FROM Department d WHERE d.company.id=? AND d.deleted=? and d.branch=?  ORDER BY d.weight desc", companyId, false,true);
		if(depts.size()>0){
			return true;
		}
		return false;
	}
	
	@SuppressWarnings("unchecked")
	public Department getDepartmentByCode(Long companyId,String deptCode){
		List<Department> depts =  departmentDao.find("FROM Department d WHERE d.company.id=? AND d.deleted=? and d.code=?  ORDER BY d.weight desc", companyId, false,deptCode);
		if(depts.size()>0){
			return depts.get(0);
		}
		return null;
	}
	/**
	 * 查询所有分支机构
	 * @param companyId
	 * @param deptCode
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public List<Department> getAllBranchs(Long companyId){
		List<Department> depts =  departmentDao.find("FROM Department d WHERE d.company.id=? AND d.deleted=? and d.branch=?  ORDER BY d.weight desc", companyId, false,true);
		return depts;
	}
	
	@SuppressWarnings("unchecked")
	public Company getCompanyById(Long companyId){
		List<Company> companys =  companyDao.find("FROM Company c WHERE c.id=? AND c.deleted=?", companyId, false);
		if(companys.size()>0){
			return companys.get(0);
		}
		return null;
	}
	/**
	 * 查询分支机构下的工作组（不包含总公司的）
	 * @param companyId
	 * @param branchId
	 * @return
	 */
	public List<Workgroup> getWorkgroupByBranchId(Long companyId,Long branchId){
		List<Workgroup> workGroupList = workGroupDao.findByCriteria(
				Restrictions.eq("company.id", companyId), Restrictions.eq(
						DELETED, false),Restrictions.eq("subCompanyId", branchId));
		return workGroupList;
	}
	/**
	 * 查询公司下的工作组（不包含分支机构）
	 * @param companyId
	 * @return
	 */
	public List<Workgroup> getWorkgroups(Long companyId){
		List<Workgroup> workGroupList = workGroupDao.findByCriteria(
				Restrictions.eq("company.id", companyId), Restrictions.eq(
						DELETED, false),Restrictions.isNull("subCompanyId"));
		return workGroupList;
		
	}

	
}
