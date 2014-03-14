package com.norteksoft.acs.service.organization;

import java.util.ArrayList;
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
import com.norteksoft.acs.base.utils.log.LogUtilDao;
import com.norteksoft.acs.entity.authorization.BranchAuthority;
import com.norteksoft.acs.entity.authorization.Role;
import com.norteksoft.acs.entity.authorization.RoleDepartment;
import com.norteksoft.acs.entity.organization.Company;
import com.norteksoft.acs.entity.organization.Department;
import com.norteksoft.acs.entity.organization.DepartmentUser;
import com.norteksoft.acs.entity.organization.User;
import com.norteksoft.acs.entity.organization.UserInfo;
import com.norteksoft.acs.entity.organization.Workgroup;
import com.norteksoft.acs.service.AcsUtils;
import com.norteksoft.acs.service.authorization.BranchAuthorityManager;
import com.norteksoft.acs.service.authorization.RoleManager;
import com.norteksoft.product.orm.Page;
import com.norteksoft.product.util.ContextUtils;

@Service
@Transactional
public class DepartmentManager {

	private SimpleHibernateTemplate<Department, Long> departmentDao;
	private SimpleHibernateTemplate<User, Long> userDao;
	private SimpleHibernateTemplate<UserInfo, Long> userInfoDao;
	private SimpleHibernateTemplate<DepartmentUser, Long> departmentToUserDao;
	private SimpleHibernateTemplate<RoleDepartment, Long> roleDepartmentDao;
	private SimpleHibernateTemplate<Company, Long> companyDao;
	private LogUtilDao logUtilDao;
	private static String DELETED = "deleted";
	private static String COMPANYID = "companyId";
	private static String DEPARTMENTID = "department.id";
	private static String hql = "from Department d where d.company.id=? and d.deleted=?";
	private static final String SBU_DEPT_HQL = "from Department d where d.company.id=? and d.parent.id=? and d.deleted=? order by d.weight desc,d.id";
	private static final String SBU_BRANCH_HQL = "from Department d where d.company.id=? and d.parent.id=? and d.deleted=? order by d.weight desc";
	
	@Autowired
	private AcsUtils acsUtils;
	
	@Autowired
	private UserInfoManager userInfoManager;
	
	@Autowired
	private WorkGroupManager workGroupManager;
	
	@Autowired
	private CompanyManager companyManager;
	
	@Autowired
	private UserManager userManager;
	
	@Autowired
	private BranchAuthorityManager branchAuthorityManager;
	
	@Autowired
	private RoleManager roleManager;
	
	@Autowired
	public void setSessionFactory(SessionFactory sessionFactory) {

		companyDao = new SimpleHibernateTemplate<Company, Long>(
				sessionFactory, Company.class);
		departmentDao = new SimpleHibernateTemplate<Department, Long>(
				sessionFactory, Department.class);
		userDao = new SimpleHibernateTemplate<User, Long>(sessionFactory,
				User.class);
		userInfoDao = new SimpleHibernateTemplate<UserInfo, Long>(
				sessionFactory, UserInfo.class);
		departmentToUserDao = new SimpleHibernateTemplate<DepartmentUser, Long>(
				sessionFactory, DepartmentUser.class);
		roleDepartmentDao = new SimpleHibernateTemplate<RoleDepartment, Long>(
				sessionFactory, RoleDepartment.class);
		logUtilDao = new LogUtilDao(sessionFactory);
	}
  
	public LogUtilDao getLogUtilDao() {
		return logUtilDao;
	}

	public void setLogUtilDao(LogUtilDao logUtilDao) {
		this.logUtilDao = logUtilDao;
	}

	private Long companyId;

	public Long getCompanyId() {
		if (companyId == null) {
			return ContextUtils.getCompanyId();
		} else{
			return companyId;
		}
	}
	public Long getSystemIdByCode(String code) {
		return acsUtils.getSystemsByCode(code).getId();
    }
	public void setCompanyId(Long companyId) {
		this.companyId = companyId;
	}

	/**
	 *根据userId得到DepartmentToUser
	 */
	@SuppressWarnings("unchecked")
	public List<DepartmentUser> getDepartmentToUserByuserId(Long userId,Long departmentId){
		String hql="from DepartmentUser d where d.user.id=? and d.department.id=? and d.deleted=?";
		return departmentToUserDao.find(hql, userId,departmentId,false);
	}
	/**
	 *根据userId得到DepartmentToUser
	 */
	@SuppressWarnings("unchecked")
	public List<DepartmentUser> getDepartmentToUserByuserId(Long userId){
		String hql="from DepartmentUser d where d.companyId=? and d.user.id=? and d.deleted=?";
		return departmentToUserDao.find(hql,ContextUtils.getCompanyId(), userId,false);
	}
	/**
	 *根据分支机构id得到DepartmentToUser
	 */
	@SuppressWarnings("unchecked")
	public List<DepartmentUser> getDepartmentToUserByBranchId(Long branchId){
		String hql="from DepartmentUser d where d.companyId=? and d.subCompanyId=? and d.deleted=?";
		return departmentToUserDao.find(hql,ContextUtils.getCompanyId(), branchId,false);
	}
	
	/**
	 * 验证部门名称唯一性
	 */
	public boolean checkDeptName(String name,Long id){
		String hql = "FROM Department d WHERE d.name=? AND d.id<>? AND d.company.id=? AND d.deleted=0";
		Object obj = departmentDao.findUnique(hql, name,id,ContextUtils.getCompanyId());
		if(obj == null){
			return false;
		}
		return true;
	}
	
	/**
	 * 验证分纸机构名称唯一性(全数据库)
	 */
	public List<Department> checkBranchName(String name){
		String hql = "FROM Department d WHERE d.name=? AND d.deleted=0 and branch=?";
		return departmentDao.find(hql, name,true);
	}
	
	public List<Department> checkBranchName(String name,Long id){
		String hql = "FROM Department d WHERE d.name=? AND d.id<>? AND d.deleted=0 and branch=?";
		return departmentDao.find(hql, name,id,true);
	}
	public List<Department> checkBranchShortName(String shortTitle){
		String hql = "FROM Department d WHERE d.shortTitle=? AND d.deleted=0 and branch=?";
		return departmentDao.find(hql, shortTitle,true);
	}
	public List<Department> checkBranchShortName(String shortTitle,Long id){
		String hql = "FROM Department d WHERE d.shortTitle=? AND d.id<>? AND d.deleted=0 and branch=?";
		return departmentDao.find(hql, shortTitle,id,true);
	}
	/**
	 * 验证部门名称唯一性(集团)
	 */
	public List<Department> checkDepartmentNameCompany(String name,Long id,Long parentId){
		String hql = "FROM Department d WHERE d.name=? AND d.id<>? AND d.subCompanyId is null AND d.deleted=0 AND d.company.id=? and branch=?";
		if(parentId!=null){
			hql=hql+" and d.parent.id=?";
			return departmentDao.find(hql, name,id,ContextUtils.getCompanyId(),false,parentId);
		}else{
			hql=hql+" and d.parent is null";
			return departmentDao.find(hql, name,id,ContextUtils.getCompanyId(),false);
		}
		
	}
	
	/**
	 * 验证部门名称唯一性(同一分支机构)
	 */
	public List<Department> checkDepartmentName(String name,Long branchId){
		String hql = "FROM Department d WHERE d.name=? AND d.subCompanyId=? AND d.deleted=0 AND d.company.id=? and branch=?";
		return departmentDao.find(hql, name,branchId,ContextUtils.getCompanyId(),false);
	}
	public List<Department> checkDepartmentName(String name,Long branchId,Long parentId){
		String hql = "FROM Department d WHERE d.name=? AND d.subCompanyId=? AND d.deleted=0 AND d.company.id=? and branch=?";
		if(parentId!=null){
			hql=hql+" and d.parent.id=?";
			return departmentDao.find(hql, name,branchId,ContextUtils.getCompanyId(),false,parentId);
		}else{
			hql=hql+" and d.parent is null";
			return departmentDao.find(hql, name,branchId,ContextUtils.getCompanyId(),false);
		}
		
	}
	
	public List<Department> checkDepartmentName(String name,Long branchId,Long id,Long parentId){
		String hql = "FROM Department d WHERE d.name=? AND d.subCompanyId=? AND d.id<>? AND d.deleted=0 and branch=?";
		if(parentId!=null){
			hql=hql+" and d.parent.id=?";
			return departmentDao.find(hql, name,branchId,id,false,parentId);
		}else{
			hql=hql+" and d.parent is null";
			return departmentDao.find(hql, name,branchId,id,false);
		}
	}
	
	public List<Department> checkDepartmentName(String name){
		String hql = "FROM Department d WHERE d.name=? AND d.subCompanyId is null AND d.deleted=0 AND d.company.id=? and branch=? and d.parent is null";
		return departmentDao.find(hql, name,ContextUtils.getCompanyId(),false);
	}
	public List<Department> checkDepartmentName(Long parentId,String name){
		String hql = "FROM Department d WHERE d.name=? AND d.subCompanyId is null AND d.deleted=0 AND d.company.id=? and branch=?";
		if(parentId!=null){
			hql=hql+" and d.parent.id=?";
			return departmentDao.find(hql, name,ContextUtils.getCompanyId(),false,parentId);
		}else{
			hql=hql+" and d.parent is null";
			return departmentDao.find(hql, name,ContextUtils.getCompanyId(),false);
		}
	}
	public boolean checkDeptName(String name){
		String hql = "FROM Department d WHERE d.name=? AND d.company.id=? AND d.deleted=0";
		Object obj = departmentDao.findUnique(hql, name,ContextUtils.getCompanyId());
		if(obj == null){
			return false;
		}
		return true;
	}
	
	/**
	 * 验证分支机构编码唯一性(全数据库)
	 * liudongxia
	 */
	public List<Department>  checkBranchCode(String code){
		String hql = "FROM Department d WHERE d.code=? AND d.deleted=0 and branch=?";
		return departmentDao.find(hql, code,true);
	}
	
	public List<Department>  checkBranchCode(String code,Long id){
		String hql = "FROM Department d WHERE d.code=? AND d.id<>? AND d.deleted=0 and branch=?";
		return departmentDao.find(hql, code,id,true);
	}

	/**
	 * 验证部门编码唯一性(全数据库)
	 * liudongxia
	 */
	public List<Department>  checkDeparmentCode(String code){
		String hql = "FROM Department d WHERE d.code=? AND d.company.id=? AND d.deleted=0 and branch=?";
		return departmentDao.find(hql, code,ContextUtils.getCompanyId(),false);
	}
	
	public List<Department>  checkDeparmentCode(String code,Long id){
		String hql = "FROM Department d WHERE d.code=? AND d.company.id=? AND d.id<>? AND d.deleted=0 and branch=?";
		return departmentDao.find(hql, code,ContextUtils.getCompanyId(),id,false);
	}
	/**
	 * 检测公司根目录下是否存在此部门
	 * @param name
	 * @return
	 */
	public Department checkDeptNoParent(String name){
		String hql = "FROM Department d WHERE d.name=? AND d.company.id=? AND d.deleted=0 AND d.parent is null";
		return (Department)departmentDao.findUnique(hql, name,ContextUtils.getCompanyId());
	}
	/**
	 * 检测部门下是否存在此子部门
	 * @param name
	 * @return
	 */
	public Department checkDeptHasParent(String name,Long parentId){
		String hql = "FROM Department d WHERE d.name=? AND d.company.id=? AND d.deleted=0 AND d.parent.id=?";
		return (Department)departmentDao.findUnique(hql, name,ContextUtils.getCompanyId(),parentId);
	}
	/**
	 * 查询所有部门信息
	 */
	@SuppressWarnings("unchecked")
	@Transactional(readOnly = true)
	public List<Department> getAllDepartment() {
		return departmentDao.find("FROM Department d WHERE d.company.id=? AND d.deleted=? ORDER BY d.weight desc,d.id", getCompanyId(), false);
	}

	/**
	 * 获取单条部门信息
	 */
	@Transactional(readOnly = true)
	public Department getDepartment(Long id) {
		return (Department)departmentDao.findUnique("from Department d where d.company.id=? and d.id=? ",ContextUtils.getCompanyId(), id);
	}
	/**
	 * 获取单条部门信息
	 */
	@SuppressWarnings("unchecked")
	@Transactional(readOnly = true)
	public Department getDepartmentById(Long id) {
		List<Department> depts=departmentDao.find("FROM Department d WHERE d.id=? AND d.deleted=?", id, false);
		if(depts.size()>0)return depts.get(0);
		return null;
	}

	/**
	 * 分页查询所有部门信息
	 */
	@Transactional(readOnly = true)
	public Page<Department> getAllDepartment(Page<Department> page) {
		page.setOrderBy("weight");
		page.setOrder("desc");
		return departmentDao.findByCriteria(page, Restrictions.eq("company.id",
				getCompanyId()), Restrictions.eq(DELETED, false));

	}

	/**
	 * 保存部门信息
	 */
	public void saveDept(Department department) {
		departmentDao.save(department);
	}

	/**
	 * 保存子部门信息
	 */
	public void saveSubDepartment(Long departmentId, Department subDepartment) {
		Department Parentdepartment = departmentDao.get(departmentId);
		subDepartment.setParent(Parentdepartment);
		subDepartment.setCompany(Parentdepartment.getCompany());
		if(subDepartment.getId()==null){
		}
		else{
		}
		    departmentDao.save(subDepartment);
	}
	
	/**
	 * 删除部门信息
	 */
	public void deleteDepartmet(Long id) {
		Department department = departmentDao.get(id);
		deleteDepartmet(department);
	}
	public void deleteDepartmet(Department department) {
		department.setDeleted(true);
		departmentDao.save(department);
	}

	public void deleteDepart(Department department,List<User> users) {
		department.setDeleted(true);
		departmentDao.save(department);
		List<DepartmentUser> dtus=new ArrayList<DepartmentUser>();
		List<DepartmentUser> departmentUserList=new ArrayList<DepartmentUser>();
		for(User user:users){
			if(department.getId().equals(user.getMainDepartmentId())) user.setMainDepartmentId(null);
			departmentUserList=getDepartmentToUserByuserId(user.getId());
			dtus=getDepartmentToUserByuserId(user.getId(),department.getId());
			if(dtus.size()!=0 && departmentUserList.size()!=0){
				DepartmentUser dtu= dtus.get(0);
				if(dtu.getSubCompanyId()==null){//集团公司下的部门
					departmentToUserDao.delete(dtu);
				}else{//某分支机构下的部门
					if(departmentUserList.size()>1){
						departmentToUserDao.delete(dtu);
					}else{
						if(dtu.getSubCompanyId().equals(dtu.getDepartment().getId())){//分支机构
							departmentToUserDao.delete(dtu);
						}else{//让此用户为该分支机构下的无部门人员
							dtu.setDepartment(getDepartment(dtu.getSubCompanyId()));
							departmentToUserDao.save(dtu);
						}
					}
				}
			}
		}
	}
	public Page<UserInfo> departmentToUsers(Page<UserInfo> userPage,
			boolean deleted, Long companyId, Integer dr) {
		return userInfoDao.findByCriteria(userPage, Restrictions.eq(DELETED,
				false), Restrictions.eq(COMPANYID, getCompanyId()),
				Restrictions.eq("dr", 0));

	}

	/**
	 *查询部门已经添加的用户
	 */
	public List<Long> getUserIds(Long departmentId) throws Exception {
		List<Long> userIds = new ArrayList<Long>();
		List<DepartmentUser> departmnetToUsers = departmentToUserDao
				.findByCriteria(Restrictions.eq(DEPARTMENTID, departmentId),
						Restrictions.eq(COMPANYID, getCompanyId()),
						Restrictions.eq(DELETED, false));
		for (DepartmentUser departmentToUser : departmnetToUsers) {
			userIds.add(departmentToUser.getUser().getUserInfo().getId());
		}
		return userIds;
	}

	/**
	 * 查询部门要移除的用户
	 */
	public Page<UserInfo> departmentToRomoveUserList(Page<UserInfo> page,
			User user, Long departmentId) {

		String hql = "select userInfo from UserInfo userInfo join userInfo.user.departmentUsers du where du.department.id=? and du.companyId=? and userInfo.deleted=? and du.deleted=?";
		if (user != null) {

			String userName = user.getLoginName();
			if (userName != null && !"".equals(userName)) {
				StringBuilder hqL = new StringBuilder(hql);
				hqL.append(" and userInfo.user.userName like ? ");
				return userInfoDao.find(page, hqL.toString(), departmentId,
						getCompanyId(), false, false, "%" + userName + "%");
			}
		}
		return userInfoDao.find(page, hql, departmentId, getCompanyId(), false,
				false);
	}
             
	/**
	 * 部门管理添加用户
	 */
	public void departmentToUser(Long departmentId, String userIds) {
		Department department = departmentDao.get(departmentId);
		for (String userId : userIds.split(",")) {
		    User user = userDao.get(Long.valueOf(userId));
		    if(!isSameBranch(user,department)){
		    	if(!isRepeatLoginNameUser(user,department)){
		           removeDepartmentToUser(user,department);
		    	}
		    }
		    if(!isSameBranch(user,department)){
		    	if(!isRepeatLoginNameUser(user,department)){
		    		 departmentToUserSingle(Long.valueOf(userId),department);
		    	}
		    }else{
			    departmentToUserSingle(Long.valueOf(userId),department);
		    }
		}
	}
	/**
	 * 部门添加人员
	 */
	public void departmentToUser(Long departmentId, List<Long> userIds,
			Integer isAdd,List<Long> branchId) {
		
		if(userIds==null&&branchId==null){
			return;
		}
		Department department = departmentDao.get(departmentId);
		/**
		 * 添加人员
		 */
		if (isAdd == 0) {
			/*if(branchId!=null){//点击分支机构时
				List<com.norteksoft.product.api.entity.User> users = ApiFactory.getAcsService().getAllUsersByBranchIncludeChildren(branchId);
				for(com.norteksoft.product.api.entity.User u:users){
					departmentToUserSingle(u.getId(),department);
				}
			}else{*/
			for (Long userId : userIds) {
				/*if(userId.equals(0L)){//全公司时
						List<com.norteksoft.product.api.entity.User> users = ApiFactory.getAcsService().getAllUsersByCompany(ContextUtils.getCompanyId());
						for(com.norteksoft.product.api.entity.User u:users){
							departmentToUserSingle(u.getId(),department);
						}
					}else{*/
				//如果跨分支机构，先要把user和原来分支机构下部门关系删除，
				//并更换人员的分支机构id
				User user = userDao.get(userId);
				if(!isSameBranch(user,department)){
					if(!isRepeatLoginNameUser(user,department)){
						removeDepartmentToUser(user,department);
					}
				}
				if(!isSameBranch(user,department)){
					if(!isRepeatLoginNameUser(user,department)){
						departmentToUserSingle(userId,department);
					}
				}else{
					departmentToUserSingle(userId,department);
				}
				//}
		}
		}
		/**
		 *移除人员
		 */
		if (isAdd == 1) {
			for (Long userId : userIds) {
				List<DepartmentUser> list = departmentToUserDao.findByCriteria(
						Restrictions.eq("user.id", userId), Restrictions.eq(
								COMPANYID, getCompanyId()), Restrictions.eq(
										DELETED, false));
				if(list.size()==1){//不兼职
					User user = userDao.get(userId);
					user.setMainDepartmentId(null);
					List<DepartmentUser> du = departmentToUserDao.findByCriteria(
							Restrictions.eq("user.id", userId), Restrictions.eq(
									DEPARTMENTID, departmentId), Restrictions.eq(
											COMPANYID, getCompanyId()), Restrictions.eq(
													DELETED, false));
					if(user.getSubCompanyId()!=null){//分支机构
						for (DepartmentUser departmentToUser : du) {
							//departmentToUser.setSubCompanyId(user.getSubCompanyId());
							Department d = departmentDao.get(user.getSubCompanyId());
							departmentToUser.setDepartment(d);
							departmentToUserDao.save(departmentToUser);
						}
					}else{//集团
						for (DepartmentUser departmentToUser : du) {
							departmentToUser.setDeleted(true);
							departmentToUserDao.save(departmentToUser);
						}
					}
				}else{//兼职
					User user = userDao.get(userId);
					if(departmentId.equals(user.getMainDepartmentId())){
						user.setMainDepartmentId(null);
					}
					
					List<DepartmentUser> du = departmentToUserDao.findByCriteria(
							Restrictions.eq("user.id", userId), Restrictions.eq(
									DEPARTMENTID, departmentId), Restrictions.eq(
											COMPANYID, getCompanyId()), Restrictions.eq(
													DELETED, false));
					for (DepartmentUser departmentToUser : du) {
						departmentToUser.setDeleted(true);
						departmentToUserDao.save(departmentToUser);
					}
				}	
			}
		}
		
	}
	
	public boolean isSameBranch(User user,Department department){
		if(user.getSubCompanyId()==null&&department.getSubCompanyId()==null){
			return true;
		}
		
		if(user.getSubCompanyId()!=null&&department.getSubCompanyId()!=null){
			if(user.getSubCompanyId().equals(department.getSubCompanyId())){
				return true;
			}
			
		}
		return false;
	}
	
	public void removeDepartmentToUser(User user,Department department){
		if(user!=null&&user.getId()!=null){
			List<DepartmentUser> list=getDepartmentToUserByuserId(user.getId());
			for(DepartmentUser du : list){
				departmentToUserDao.delete(du);
			}
			user.setMainDepartmentId(null);
			userDao.save(user);
		}
	}
	
	private void departmentToUserSingle(Long userId,Department department){
		DepartmentUser departmentToUser;
		User user = null;
		List<DepartmentUser> dtu=getDepartmentToUserByuserId(userId,department.getId());
		user = userDao.get(userId);
		if(dtu.size()==0){
			departmentToUser = new DepartmentUser();
			departmentToUser.setUser(user);
			departmentToUser.setDepartment(department);
			departmentToUser.setCompanyId(user.getCompanyId());
			//给部门添加人员时设置分支机构id
			//可以点击分支机构新建用户，用户会自动保存到分支机构的无部门人员中
			if(department.getBranch()){
				user.setSubCompanyName(StringUtils.isNotEmpty(department.getShortTitle())?department.getShortTitle():department.getName());
				departmentToUser.setSubCompanyId(department.getId());
			}else{
				if(department.getSubCompanyId()!=null){
					Department newBranch=this.getDepartment(department.getSubCompanyId());
					user.setSubCompanyName(StringUtils.isNotEmpty(newBranch.getShortTitle())?newBranch.getShortTitle():newBranch.getName());
					departmentToUser.setSubCompanyId(department.getSubCompanyId());
				}else{
					user.setSubCompanyName(ContextUtils.getCompanyName());
				}
			}
			user.setSubCompanyId(departmentToUser.getSubCompanyId());
			departmentToUserDao.save(departmentToUser);
		}else{
			DepartmentUser d=dtu.get(0);
			d.setDeleted(false);
			departmentToUserDao.save(d);
		}
		Set<DepartmentUser> departmentsUsers=user.getDepartmentUsers();
		if(departmentsUsers.size()>1){
			for(DepartmentUser du:departmentsUsers){
				if(du.getDepartment().getId().equals(du.getSubCompanyId())){
					departmentToUserDao.delete(du);
				}
			}
		}
		if(user.getMainDepartmentId()==null){
			user.setMainDepartmentId(department.getId());
		}
	}

	/**
	 * 按条件检索部门
	 */
	@Transactional(readOnly = true)
	public Page<Department> getSearchDepartment(Page<Department> page,
			Department department, boolean deleted) {
		    StringBuilder departmentHql = new StringBuilder(hql);

		if (department != null) {

			String departmentCode = department.getCode();
			String name = department.getName();

			if (!"".equals(departmentCode) && !"".equals(name)) {
				departmentHql.append(" and d.code like ?");
				departmentHql.append(" and d.name like ?");
				return departmentDao.find(page, departmentHql.toString(),
						getCompanyId(), false, "%" + departmentCode + "%", "%"
								+ name + "%");
			}

			if (!"".equals(departmentCode)) {
				departmentHql.append(" and d.departmentCode like ?");
				return departmentDao.find(page, departmentHql.toString(),
						getCompanyId(), false, "%" + departmentCode + "%");
			}

			if (!"".equals(name)) {
				departmentHql.append(" and d.name like ?");
				return departmentDao.find(page, departmentHql.toString(),
						getCompanyId(), false, "%" + name + "%");
			}
		}
		return departmentDao.find(page, hql, getCompanyId(), false);
	}

	/**
	 * 查询己分配给公司的部门
	 */
	public List<Department> saveDepart(List<Long> departmentId) {

		return departmentDao
				.findByCriteria(Restrictions.in("id", departmentId));
	}

	/**
	 * 给部门分配角色
	 * 
	 * @param departmentId
	 * @param roleIds
	 */
	public void addRolesToDepartments(Long departmentId, List<Long> roleIds,
			Integer isAdd) {
		Department department = departmentDao.get(departmentId);
		RoleDepartment roleDepartment = null;
		if (isAdd == 0) {
			Role role = null;
			for (Long id : roleIds) {
				role = new Role();
				role.setId(id);
				roleDepartment = new RoleDepartment();
				roleDepartment.setRole(role);
				roleDepartment.setDepartment(department);
				roleDepartment.setCompanyId(getCompanyId());
				roleDepartmentDao.save(roleDepartment);
			}
		} else if (isAdd == 1) {
			List<RoleDepartment> roleDepartments = roleDepartmentDao
					.findByCriteria(Restrictions.eq(DEPARTMENTID,
							departmentId), Restrictions.in("role.id", roleIds),
							Restrictions.eq(COMPANYID, getCompanyId()),
							Restrictions.eq(DELETED, false));
			for (RoleDepartment rd : roleDepartments) {
				rd.setDeleted(true);
				roleDepartmentDao.save(rd);
			}
		}
	}

	/**
	 * 查询部门已经分配的角色
	 */
	public List<Long> getCheckedRoleIdsByDepartment(Long departmentId) {
		List<RoleDepartment> roleDepartment = roleDepartmentDao.findByCriteria(
				Restrictions.eq(DEPARTMENTID, departmentId), Restrictions
						.eq(COMPANYID, getCompanyId()), Restrictions.eq(
								DELETED, false));
		List<Long> checkedRoleIds = new ArrayList<Long>();
		for (RoleDepartment rd : roleDepartment) {
			checkedRoleIds.add(rd.getRole().getId());
		}
		return checkedRoleIds;
	}

	public Page<Department> queryDepartmentByCompany(Page<Department> page,
			Long company) {
		return departmentDao.findByCriteria(page, Restrictions.eq("company.id",
				company), Restrictions.eq(DELETED, false));
	}

	public SimpleHibernateTemplate<com.norteksoft.acs.entity.organization.User, Long> getUserDao() {
		return userDao;
	}

	public SimpleHibernateTemplate<com.norteksoft.acs.entity.organization.DepartmentUser, Long> getDepartmentToUserDao() {
		return departmentToUserDao;
	}

	public SimpleHibernateTemplate<Department, Long> getDepartmentDao() {
		return departmentDao;
	}
	
	@SuppressWarnings("unchecked")
	public List<Department> getSubDeptments(Long deptId){
		return departmentDao.find(SBU_DEPT_HQL, getCompanyId(), deptId, false);
	}
	
	@SuppressWarnings("unchecked")
	public List<Department> getSubBranchs(Long deptId){
		return departmentDao.find(SBU_BRANCH_HQL, getCompanyId(), deptId, false);
	}

	public Page<Department> getDepartmentsCanAddToRoel(Page<Department> page,
			Long roleId) {

		return getDepartmentDao()
				.find(
						page,
						"select d from Department d where d not in (select d from Department d join d.roleDepartments rd where rd.role.id=? and rd.companyId=? and d.deleted=? and rd.deleted=? )",
						roleId, getCompanyId(), false, false);
		 
	}

	@SuppressWarnings("unchecked")
	public List<Department> getDepartmentsInRole(Long roleId) {
		return departmentDao.find(
						"select d from Department d join d.roleDepartments rd where rd.role.id=? and rd.companyId=? and d.deleted=? and rd.deleted=? order by d.weight desc",
						roleId, getCompanyId(), false, false);
	}
	
	@SuppressWarnings("unchecked")
	public List<Department> getDepartmentsByUser(Long companyId,Long userId){
		StringBuilder hql = new StringBuilder();
		hql.append("select d from Department d join d.departmentUsers du join du.user u ");
		hql.append("where u.companyId=? and u.id=? and u.deleted=? and du.deleted=? and d.deleted=?");
		return departmentDao.find(hql.toString(), companyId, userId, false, false, false);
	}

	public SimpleHibernateTemplate<com.norteksoft.acs.entity.organization.UserInfo, Long> getUserInfoDao() {
		return userInfoDao;
	}

	public SimpleHibernateTemplate<Company, Long> getCompanyDao() {
		return companyDao;
	}

	public void setCompanyDao(SimpleHibernateTemplate<Company, Long> companyDao) {
		this.companyDao = companyDao;
	}

	public boolean isSubDepartment(Long currentDeptId, Long deptId) {
		Set<Department> currentDeptChildren = departmentDao.get(currentDeptId).getChildren();
		Department choseDepartment = departmentDao.get(deptId);
		if(currentDeptChildren.contains(choseDepartment)) return true;
		for(Department d : currentDeptChildren){
			return isSubDepartment(d.getId(),deptId);
		}
		return false;
	}
    //得到部门的分支机构id
	public Long getBranchId(Department department) {
		if(department==null){
			return null;
		}else{
			if(department.getBranch()){
				return department.getId();
			}else{
				return getBranchId(department.getParent());
			}
		}
	}
	
	public Department getBranchDepartmentByCode(String code){
		String hql = "FROM Department d WHERE d.code=? AND d.deleted=? and d.branch=?";
		List<Department> depts = departmentDao.find(hql, code,false,true);
		if(depts.size()>0)return depts.get(0);
		return null;
	}
	
	/**
	 * 获得所有分支机构
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public List<Department> getAllBranches() {
		StringBuilder hql = new StringBuilder();
		hql.append("from Department d ");
		hql.append("where d.company.id=? and d.deleted=? and d.branch=?");
		return departmentDao.find(hql.toString(), ContextUtils.getCompanyId(),false,true);
	}
	
	@SuppressWarnings("unchecked")
	public List<Department> getRootDepartment() {
		String hql="from Department d where d.company.id=? and d.parent.id is null and d.deleted=? order by d.weight desc";
		return departmentDao.find(hql, ContextUtils.getCompanyId(), false);
	}

	//判断所选择的部门下是否已经存在相同登录名的用户并得到这些用户
	public String getRepeatUserNames(String choseUserIds,String chooseDepartmentId) {
		String result = "";
		String[] ids = choseUserIds.split(",");
		for(int i=0;i<ids.length;i++){
			User user = userDao.get(Long.valueOf(ids[i]));
			String userLoginName = user.getLoginName();
			Department department = departmentDao.get(Long.valueOf(chooseDepartmentId));
			if(isRepeatLoginNameUser(user,department)){
				result+=userLoginName+",";
			}
		}
		if(StringUtils.isNotEmpty(result)){
			return result.substring(0,result.length()-1);
		}else{
		    return result;
		}
	}
	
	private boolean isRepeatLoginNameUser(User user,Department department){
		//当用户和部门属于不同分支机构时
		if(!userInfoManager.inSameBranch(user.getSubCompanyId(),department.getSubCompanyId())){
			if(department.getSubCompanyId()==null){//集团
				StringBuilder sql = new StringBuilder();
				sql.append("select  u from User u ");
				sql.append("where u.companyId=?  and u.loginName=? and u.subCompanyId is null");
				List<User> users = userDao.find(sql.toString(),ContextUtils.getCompanyId(),user.getLoginName());
				if(users.size()>0) return true;
			}else{//分支
				StringBuilder sql = new StringBuilder();
				sql.append("select  u from User u ");
				sql.append("where u.companyId=? and u.loginName=? and u.subCompanyId=?");
				List<User> users = userDao.find(sql.toString(),ContextUtils.getCompanyId(),user.getLoginName(),department.getSubCompanyId());
				if(users.size()>0) return true;
			}
		}
		return false;
	}

	public List<Department> getDefaultCodeDepartment() {
		String hql = "from Department d where d.deleted=? and d.code like 'department-%' " ;
		return departmentDao.find(hql,false);
	}

	public boolean canEditParentDepartment(Long id) {
		boolean systemAdminable =  roleManager.hasSystemAdminRole(ContextUtils.getUserId());
		boolean BranchAdminable=roleManager.hasBranchAdminRole(ContextUtils.getUserId());
		if(systemAdminable||BranchAdminable){
		Department d = getDepartment(id);
		if(d.getBranch()){//是分支机构
			List<User> users= userInfoManager.queryUsersForBranch(id);
			if(users.size()>0)return false;
			
			String hql="from Department d where d.company.id=? and d.parent.id=? and deleted=?";
			List<Department> departments =  departmentDao.find(hql, ContextUtils.getCompanyId(),id,false);
			if(departments.size()>0)return false;
			
			List<Workgroup>  workgroups = workGroupManager.getWorkgroupsByBranch(id);
			if(workgroups.size()>0)return false;
			
			List<BranchAuthority> authorities = branchAuthorityManager.getBranchAuthorityByBranch(id);
			if(authorities.size()>0)return false;
			
			List<Role> roles = roleManager.getRoleByBranches(id);
			if(roles.size()>0)return false;
			
		}
		return true;
		}
		return false; 
	}
	
	public boolean canEditDepartmentInfor(Long id) {
		boolean systemAdminable =  roleManager.hasSystemAdminRole(ContextUtils.getUserId());
		boolean BranchAdminable=roleManager.hasBranchAdminRole(ContextUtils.getUserId());
		if(systemAdminable||BranchAdminable){
		Department d = getDepartment(id);
		List<BranchAuthority> branchAuthoritys = branchAuthorityManager.getBranchByUser(ContextUtils.getUserId());
		if(BranchAdminable&&!systemAdminable&&d.getBranch()){//是分支机构
			for(BranchAuthority ba:branchAuthoritys){
				if(ba.getBranchesId().equals(id)){
					return false;
				}
			}
		}
			return true;
		}
		return false; 
	}
	
	public boolean canTransform(Long id) {
		Department d = getDepartment(id);
		if(d.getBranch()){//是分支机构
			List<User> users= userInfoManager.queryUsersForBranch(id);
			if(users.size()>0)return false;
			
			String hql="from Department d where d.company.id=? and d.parent.id=? and d.deleted=?";
			List<Department> departments =  departmentDao.find(hql, ContextUtils.getCompanyId(),id,false);
			if(departments.size()>0)return false;
			
			List<Workgroup>  workgroups = workGroupManager.getWorkgroupsByBranch(id);
			if(workgroups.size()>0)return false;
			
			List<BranchAuthority> authorities = branchAuthorityManager.getBranchAuthorityByBranch(id);
			if(authorities.size()>0)return false;
			
			List<Role> roles = roleManager.getRoleByBranches(id);
			if(roles.size()>0)return false;
		}else{//是部门
			List<User> users= userInfoManager.queryUsersByDepartment(id);
			if(users.size()>0)return false;
			
			String hql="from Department d where d.company.id=? and d.parent.id=? and d.deleted=?";
			List<Department> departments =  departmentDao.find(hql, ContextUtils.getCompanyId(),id,false);
			if(departments.size()>0)return false;
		}
		return true ; 
	}

	public boolean isBranchBelongToTopCompany(Long currentDeptId) {
		Department department = departmentDao.get(currentDeptId);
		Long id = getBranchId(department.getParent());
	    if(id==null){
	    	return true;
	    }else{
	    	return false;
	    }
	}
	
	public String createDepartmentCode(){
		long num=0;
		List<Department> departments=getDefaultCodeDepartment();
		if(departments != null && departments.size()>0){
			for(Department p:departments){
				String codeNum=p.getCode().replace("department-", "");
				if(codeNum.matches("^-?\\d+$")&&Long.valueOf(codeNum)>num){
					num=Long.valueOf(codeNum);
				}
			}
		}else{
			return "department-1";
		}
		return "department-"+(num+1);
	}

	public String createDepartmentCodeForBranch() {
		long num=0;
		List<Department> departments=getDefaultCodeDepartmentForBranch();
		if(departments != null && departments.size()>0){
			for(Department p:departments){
				String codeNum=p.getCode().replace("department-", "");
				if(codeNum.matches("^-?\\d+$")&&Long.valueOf(codeNum)>num){
					num=Long.valueOf(codeNum);
				}
			}
		}else{
			return "department-1";
		}
		return "department-"+(num+1);
	}

	private List<Department> getDefaultCodeDepartmentForBranch() {
		String hql = "from Department d where d.code like 'department-%' and d.deleted=? " ;
		return departmentDao.find(hql,false);
	}
	
	/**
	 * 集团公司中是否含有分支机构：true含有分支机构，false不含有分支机构
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public boolean containBranches() {
		String hql = "from Department d where d.company.id=? and d.deleted=? and d.branch=?" ;
		List <Department> branches=departmentDao.find(hql,ContextUtils.getCompanyId(),false,true);
		if(branches!=null&&branches.size()>0){
			return true;
		}else{
			return false;
		}
	}
	
	public void cleanDept(Long departmentId2) {
		Department dept=getDepartment(departmentId2);
		Set<RoleDepartment> roleDepartments=dept.getRoleDepartments();
		if(!roleDepartments.isEmpty()){
			for(RoleDepartment roleDepartment:roleDepartments){
				roleDepartmentDao.delete(roleDepartment);
			}
		}
	}
	
}
