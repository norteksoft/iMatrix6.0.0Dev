package com.norteksoft.acs.service.organization;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.norteksoft.acs.base.enumeration.MailboxDeploy;
import com.norteksoft.acs.base.enumeration.SecretGrade;
import com.norteksoft.acs.base.orm.hibernate.SimpleHibernateTemplate;
import com.norteksoft.acs.entity.authorization.BranchAuthority;
import com.norteksoft.acs.entity.organization.Company;
import com.norteksoft.acs.entity.organization.Department;
import com.norteksoft.acs.entity.organization.DepartmentUser;
import com.norteksoft.acs.entity.organization.User;
import com.norteksoft.acs.entity.organization.UserInfo;
import com.norteksoft.acs.service.authorization.BranchAuthorityManager;
import com.norteksoft.acs.service.authorization.RoleManager;
import com.norteksoft.bs.options.entity.ImportDefinition;
import com.norteksoft.product.api.impl.DefaultDataImporterCallBack;
import com.norteksoft.product.util.ContextUtils;

@Service
@Transactional
public class ImportUserManager extends DefaultDataImporterCallBack{
	private SimpleHibernateTemplate<Company, Long> companyDao;
	private SimpleHibernateTemplate<Department, Long> departmentDao;
	private SimpleHibernateTemplate<DepartmentUser, Long> departmentToUserDao;
	private SimpleHibernateTemplate<UserInfo, Long> userInfoDao;
	
	@Autowired
	private UserManager userManager;
	@Autowired
	private UserInfoManager userInfoManager;
	@Autowired
	private RoleManager roleManager;
	@Autowired
	private BranchAuthorityManager branchAuthorityManager;
	@Autowired
	private DepartmentManager departmentManager;
	
	@Autowired
	public void setSessionFactory(SessionFactory sessionFactory) {
		companyDao=new SimpleHibernateTemplate<Company, Long>(sessionFactory, Company.class);
		departmentDao = new SimpleHibernateTemplate<Department, Long>(sessionFactory, Department.class);
		departmentToUserDao = new SimpleHibernateTemplate<DepartmentUser, Long>(sessionFactory, DepartmentUser.class);
		userInfoDao = new SimpleHibernateTemplate<UserInfo, Long>(sessionFactory, UserInfo.class);
	}
	
	private String validateUser(User u){
		if(ContextUtils.getCompanyId().equals(u.getCompanyId())){
			if(u.isDeleted()&&u.getUserInfo().getDr()==0){
				return "登陆名为"+u.getLoginName()+"的用户没有彻底删除";
			}
		}else{
			if(!u.isDeleted()||(u.isDeleted()&&u.getUserInfo().getDr()==0)){
				return "其他租户中已有登陆名为"+u.getLoginName()+"的用户";
			}
		}
		return "";
	}
	
	private boolean hasDepartment(List<Department> depts,Department dept){
		if(depts!=null && depts.size()>0 && dept != null){
			for(Department d:depts){
				if(d.getId().equals(dept.getId())){
					return true;
				}
			}
		}
		return false;
	}
	
	public String afterValidate(List<String> results) {
		String str="";
		Set<String> resultSet=new HashSet<String>();
		for(String result:results){
			resultSet.add(result);
		}
		for(String result:resultSet){
			str+=result+"！\n";
		}
		return str;
	}
	
	@SuppressWarnings("unchecked")
	public String saveSingleRow(String[] rowValue,ImportDefinition importDefinition) {
//		List<User> users = userManager.getUsersByLoginName(rowValue[2]);
//		String result="";
//		if(users != null && users.size()>0){
//			for(User u:users){
//				result=validateUser(u);
//				if(StringUtils.isNotEmpty(result))
//					return result;
//			}
//		}
		Department parent=null;
		boolean isBranchManager=false;
		List<Department> branchManagerDepts = new ArrayList<Department>();
		User importUser=userManager.getUserById(ContextUtils.getUserId());
		boolean systemAdminable = roleManager.hasSystemAdminRole(importUser);
		if(!systemAdminable){//如果不是系统管理员，则判断是否是分支管理员
			if(roleManager.hasBranchAdminRole(importUser)){//分支机构管理员
				isBranchManager=true;
				List<BranchAuthority> branches=branchAuthorityManager.getBranchByUser(ContextUtils.getUserId());
				for(BranchAuthority branch:branches){
					Department d=departmentManager.getDepartment(branch.getBranchesId());
					branchManagerDepts.add(d);
					userManager.getSubDepartment(d.getId(),branchManagerDepts);
				}
			}
		}
		
//		Integer currentUserNumber = userManager.getUserNumberByCompanyId(ContextUtils.getCompanyId());
//		Integer companyUserLimit=userManager.getAllowedNumbByCompany(ContextUtils.getCompanyId());
// 		Integer importCount = 0;
		if(StringUtils.isNotEmpty(rowValue[0])){
			String[] depts=rowValue[0].split("/");
			if(isBranchManager){
				if(depts[0].contains("#")){
					if(depts[0].split("#").length>1){
						String branchName=depts[0].split("#")[1];
						Department branch=getBranchByName(branchName);
						if(branchManagerDepts.size()>0&&!hasDepartment(branchManagerDepts,branch)){
							return "名称为"+branchName+"的分支机构不在您的管理范围内";
						}
					}else{
						String branchName=depts[0].substring(0,depts[0].length()-1);
						Department branch=getBranchByName(branchName);
						if(branchManagerDepts.size()>0&&!hasDepartment(branchManagerDepts,branch)){
							return "名称为"+branchName+"的分支机构不在您的管理范围内";
						}
					}
				}else{
					return "名称为"+depts[0]+"的部门不在您的管理范围内";
				}
			}
			for(int i=0;i<depts.length;i++){//for-start
				Department department=null;
				if(depts[i].contains("#")){//分支机构或分支机构下的部门
					if(depts[i].endsWith("#")){//分支机构
						String departmentName=depts[i].substring(0,depts[i].length()-1);
						department=getBranchByName(departmentName);
						if(department==null){//分支机构不存在
							department=new Department();
							Company company = companyDao.get(ContextUtils.getCompanyId());
							department.setCompany(company);
							//////////////////////部门编号统一处理
							department.setCode(departmentManager.createDepartmentCode());
							department.setName(departmentName);
							department.setBranch(true);
							departmentDao.save(department);
							department.setSubCompanyId(department.getId());
							departmentDao.save(department);
						}else{
							if(!ContextUtils.getCompanyId().equals(department.getCompany().getId())){//其他集团公司下已经有此分支机构
								return "其他集团公司下已经有名称为"+departmentName+"的分支机构";
							}
							if(isBranchManager&&branchManagerDepts.size()>0&&!hasDepartment(branchManagerDepts,department)){
								return "名称为"+departmentName+"的分支机构不在您的管理范围内";
							}
						}
						
					}else{//分支机构下的部门-start
						String[] departmentName=(depts[i]).split("#");
						Department branch=getBranchByName(departmentName[1]);
						department=getDepartmentByName(parent,departmentName[0],true,branch.getId());
						if(department==null){//分支机构下的此部门不存在
							department=new Department();
							Company company = companyDao.get(ContextUtils.getCompanyId());
							department.setCompany(company);
							//////////////////////部门编号统一处理
							department.setCode(departmentManager.createDepartmentCode());
							department.setName(departmentName[0]);
							department.setBranch(false);
							department.setSubCompanyId(branch.getId());
							departmentDao.save(department);
						}
					}//分支机构下的部门-end
				}else{//集团公司下的部门-start
					department=getDepartmentByName(parent,depts[i],false,null);
					if(department==null){//部门不存在
						department=new Department();
						Company company = companyDao.get(ContextUtils.getCompanyId());
						department.setCompany(company);
						//////////////////////部门编号统一处理
						department.setCode(departmentManager.createDepartmentCode());
						department.setName(depts[i]);
						department.setBranch(false);
						departmentDao.save(department);
					}
				}//集团公司下的部门-end
				if(i>0){
					department.setParent(parent);
				}
				parent=department;
				
				//如果是最后一个部门,则添加人。如：办公室/后勤/车队,周宏1,zhouhong1,68963158,男,zhouhong@bky.com,50,10,如果是“车队”则添加人员
				if(depts.length-1==i){
					//#####用户
					if(StringUtils.isNotEmpty(rowValue[2])){//用户登录名不为空,添加用户
//						if(currentUserNumber+importCount+1>companyUserLimit)return "已导入"+importCount+"条,超出系统允许注册人数";
						departmentDao.save(department);
						if(department.getBranch()){
							department.setSubCompanyId(department.getId());
							departmentDao.save(department);
						}
						UserInfo userInfo=importUserSaveUser(rowValue,department);
						if(userInfo==null){
							return "导入的条数已经超出系统允许注册人数，剩下的不会导入系统，若想继续导入请找管理员增加系统允许注册人数";
						}
						//新建用户时默认给用户portal普通用户权限
						userInfoManager.giveNewUserPortalCommonRole(userInfo.getUser());
						//####部门人员
						DepartmentUser departmentToUser;
						String hql="from DepartmentUser d where d.user.id=? and d.department.id=? and d.deleted=?";
						List<DepartmentUser> dtu=departmentToUserDao.find(hql, userInfo.getUser().getId(),department.getId(),false);
						if(dtu!=null&&dtu.size()>0){
							departmentToUser=dtu.get(0);
							departmentToUser.setDepartment(department);
							departmentToUser.setSubCompanyId(department.getSubCompanyId());
//							d.setDeleted(false);
							departmentToUserDao.save(departmentToUser);
						}else{
							if(department.getSubCompanyId()!=null){
								List<DepartmentUser> nodept=departmentToUserDao.find(hql, userInfo.getUser().getId(),department.getSubCompanyId(),false);
								if(nodept!=null && nodept.size()>0){//分支机构无部门下有此用户
									departmentToUser=nodept.get(0);
									departmentToUser.setDepartment(department);
									departmentToUserDao.save(departmentToUser);
								}else{
									
									if(department.getId().equals(department.getSubCompanyId())){//分支机构
										
									}else{//分支机构下的部门
										
									}
									departmentToUser = new DepartmentUser();
									userInfo = userInfoDao.get(userInfo.getId());
									departmentToUser.setUser(userInfo.getUser());
									departmentToUser.setDepartment(department);
									departmentToUser.setSubCompanyId(department.getSubCompanyId());
									departmentToUser.setCompanyId(ContextUtils.getCompanyId());
									departmentToUserDao.save(departmentToUser);
								}
							}else{
								departmentToUser = new DepartmentUser();
								userInfo = userInfoDao.get(userInfo.getId());
								departmentToUser.setUser(userInfo.getUser());
								departmentToUser.setDepartment(department);
								departmentToUser.setSubCompanyId(department.getSubCompanyId());
								departmentToUser.setCompanyId(ContextUtils.getCompanyId());
								departmentToUserDao.save(departmentToUser);
							}
							
							//记录公司用户数量
//							importCount++;
						}
					}
				}
			}//for-end
		}else{//部门为空，即无部门人员导入
			if(isBranchManager){
				return "部门不能为空";
			}else{
				if(StringUtils.isNotEmpty(rowValue[2])){
//					if(currentUserNumber+importCount+1>companyUserLimit)return "已导入"+importCount+"条,超出系统允许注册人数";
					UserInfo userInfo=importUserSaveUser(rowValue,null);
					//新建用户时默认给用户portal普通用户权限
					if(userInfo!=null&&userInfo.getUser()!=null)userInfoManager.giveNewUserPortalCommonRole(userInfo.getUser());
					if(userInfo!=null&&userInfo.getUser()==null){
					//记录公司用户数量
//					importCount++;
					}
				}
			}
		}
		return "";
	}
	
	@SuppressWarnings("unchecked")
	public Department getBranchByName(String name){
		if(name == null) throw new RuntimeException("没有给定查询分支机构时的查询条件：分支机构名称");
		List<Department> depts = departmentDao.find("from Department d where d.name=? and d.deleted=? and d.branch=? ", name, false,true);
		if(depts!=null && depts.size() >0){
			return depts.get(0);
		}
		return null;
	}
	
	@SuppressWarnings("unchecked")
	public Department getDepartmentByName(Department parent,String name,boolean isBranch,Long branchId){
		if(name == null) throw new RuntimeException("没有给定查询部门时的查询条件：部门名称");
		List<Department> depts = new ArrayList<Department>();
		String hql1="from Department d where d.company.id=? and d.name=? and d.deleted=? and d.branch=? and d.subCompanyId=? ";
		String hql2="from Department d where d.company.id=? and d.name=? and d.deleted=? and d.branch=?  and d.subCompanyId is null ";
		if(isBranch){//分支机构下的部门
			if(parent!=null){
				hql1=hql1+"and d.parent.id=?";
				depts = departmentDao.find(hql1, ContextUtils.getCompanyId(), name, false,false,branchId,parent.getId());
			}else{
				hql1=hql1+"and d.parent is null";
				depts = departmentDao.find(hql1, ContextUtils.getCompanyId(), name, false,false,branchId);
			}
		}else{//集团公司下的部门
			if(parent!=null){
				hql2=hql2+"and d.parent.id=?";
				depts = departmentDao.find(hql2, ContextUtils.getCompanyId(), name, false,false,parent.getId());
			}else{
				hql2=hql2+"and d.parent is null";
				depts = departmentDao.find(hql2, ContextUtils.getCompanyId(), name, false,false);
			}
		}
		if(depts.size() == 1){
			return depts.get(0);
		}
		return null;
	}
	
	public UserInfo importUserSaveUser(String[] values,Department dept){
 		//#####用户
		Long subCompanyId=null;
		if(dept!=null && dept.getSubCompanyId()!=null){
			subCompanyId=dept.getSubCompanyId();
		}
		User user = userManager.getUserInBranchDepartment(values[2], ContextUtils.getCompanyId(), subCompanyId);
		UserInfo userInfo =null;
		List<UserInfo> userInfos=new ArrayList<UserInfo>();
		if(user==null){
			user = userInfoManager.getDeletedUser(0,true,subCompanyId,values[2]);//在已删除中的用户
			if(user==null){
				Integer currentUserNumber = userManager.getUserNumberByCompanyId(ContextUtils.getCompanyId());
				Integer companyUserLimit=userManager.getAllowedNumbByCompany(ContextUtils.getCompanyId());
				if(currentUserNumber+1>companyUserLimit){
					return null;
				}else{
					user=new User();
					userInfo = new UserInfo();
				}
			}else{
				user.setDeleted(false);
				userManager.saveUser(user);
				userInfo=user.getUserInfo();
				userInfo.setDeleted(false);
				userInfoDao.save(userInfo);
				if(subCompanyId!=null){
					DepartmentUser du=new DepartmentUser();
					du.setCompanyId(ContextUtils.getCompanyId());
					du.setUser(user);
					Department branch=departmentManager.getDepartment(subCompanyId);
					du.setDepartment(branch);
					du.setSubCompanyId(subCompanyId);
					departmentToUserDao.save(du);
				}
			}
		}else{
			userInfo=user.getUserInfo();
		}
		if(dept!=null && !dept.getBranch())user.setMainDepartmentId(dept.getId());
		user.setName(StringUtils.trim(values[1]));
		user.setPassword("123");
		user.setLoginName(StringUtils.trim(values[2]));
		user.setCompanyId(ContextUtils.getCompanyId());
		if(dept!=null && dept.getSubCompanyId()!=null){
			user.setSubCompanyId(dept.getSubCompanyId());
			Department branch=departmentManager.getDepartment(dept.getSubCompanyId());
			user.setSubCompanyName(StringUtils.isNotEmpty(branch.getShortTitle())?branch.getShortTitle():branch.getName());
		}
		userInfo.setCompanyId(ContextUtils.getCompanyId());
		if(StringUtils.isEmpty(user.getSubCompanyName())){
			user.setSubCompanyName(ContextUtils.getCompanyName());
		}
		userInfo.setPasswordUpdatedTime(getNewDate());
		for(int i=4;i<=values.length;i++){
			switch (i) {
			case 4: if(StringUtils.isNotEmpty(values[3]))userInfo.setTelephone(StringUtils.trim(values[3])); break;
			case 5: user.setSex("男".equals(values[4])); break;
			case 6: if(StringUtils.isNotEmpty(values[5]))user.setEmail(StringUtils.trim(values[5])); break;
			case 7: if(StringUtils.isNotEmpty(values[6]))user.setWeight(Integer.parseInt(StringUtils.trim(values[6]))); break;
			case 8: if(StringUtils.isNotEmpty(values[7]))user.setMailSize(Float.parseFloat(StringUtils.trim(values[7]))); break;
			case 9: if(StringUtils.isNotEmpty(values[8])){
				if("一般".equals(StringUtils.trim(values[8]))){
					user.setSecretGrade(SecretGrade.COMMON);
				}else if("重要".equals(StringUtils.trim(values[8]))){
					user.setSecretGrade(SecretGrade.MAJOR);
				}else if("核心".equals(StringUtils.trim(values[8]))){
					user.setSecretGrade(SecretGrade.CENTRE);
				}
			}
			break;
			case 10: if(StringUtils.isNotEmpty(values[9])){
				if("内网".equals(StringUtils.trim(values[9]))){
					user.setMailboxDeploy(MailboxDeploy.INSIDE);
				}else if("外网".equals(StringUtils.trim(values[9]))){
					user.setMailboxDeploy(MailboxDeploy.EXTERIOR);
				}
			}
			break;
			}
		}
		userInfos.add(userInfo);
		user.setUserInfos(userInfos);
		userManager.saveUser(user);
		userInfo.setUser(user);
		userInfoDao.save(userInfo);
		return userInfo;
 	}
	
	private Date newDate;
	private Date getNewDate() {
		if(newDate==null){
			Calendar cal=Calendar.getInstance();
			int year = cal.get(Calendar.YEAR);
			int month = cal.get(Calendar.MONTH);
			int day = cal.get(Calendar.DATE);
			cal.clear();
			cal.set(year, month, day);
			newDate = cal.getTime();
		}
		return newDate;
	}
}
