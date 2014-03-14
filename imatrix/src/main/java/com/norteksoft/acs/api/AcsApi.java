package com.norteksoft.acs.api;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.norteksoft.acs.entity.authorization.BusinessSystem;
import com.norteksoft.acs.entity.authorization.Role;
import com.norteksoft.acs.entity.organization.Company;
import com.norteksoft.acs.entity.organization.Department;
import com.norteksoft.acs.entity.organization.User;
import com.norteksoft.acs.entity.organization.Workgroup;
import com.norteksoft.acs.service.authorization.AcsApiManager;
import com.norteksoft.product.util.ContextUtils;
import com.norteksoft.product.util.ParameterUtils;
import com.norteksoft.product.util.ThreadParameters;

/**
 * 权限API
 * @author xiaoj
 */
@Service
@Transactional
public class AcsApi {

	private static Log log = LogFactory.getLog(AcsApi.class);
	public static final String DEPARTMENT_NAME_CONDITION = "d.department_name";
	public static final String WORKGROUP_NAME_CONDITION = "wg.work_group_name";
	public static final String ROLE_NAME_CONDITION = "r.role_name";
	public static final String USER_NAME_CONDITION = "u.user_name";
	
	private static AcsApiManager getAcsApiManager(){
		return (AcsApiManager)ContextUtils.getBean("acsApiManager");
	}
	
	/**
	 * 查询公司所有的部门
	 * @param companyId   公司ID
	 * @return List       [部门名称列表, 是否有子部门(true,false),是否有人员(true,false),是否是分支（true，false），部门编码]
	 */
	public static List<String[]> getAllDepts(Long companyId){
		log.debug("*** getAllDepts 方法开始");
		log.debug(new StringBuilder("*** Received parameter:[")
			.append("companyId:").append(companyId)
			.append("]").toString());
		ThreadParameters parameters = new ThreadParameters(companyId);
		ParameterUtils.setParameters(parameters);
		
 		//Map<Department, String> depts = getAcsApiManager().getAllDepts(companyId);
		List<Department> depts = getAcsApiManager().getAllDeptsInOrder(companyId);
		List<String[]> results = new ArrayList<String[]>();
		boolean hasbranch = getAcsApiManager().hasBranch(companyId);
		for(Department dept : depts){
			String isHasUsersInDept=getAcsApiManager().getUsersByDeptCode(companyId, dept.getCode()).size()>0?"true":"false";
			String subCompanyName = dept.getSubCompanyName();
			if(subCompanyName==null){
				subCompanyName = "";
			}
			results.add(new String[]{dept.getName(), getAcsApiManager().hasSubDepartment(dept),isHasUsersInDept,dept.getBranch()==null?"false":dept.getBranch()+"",dept.getCode(),hasbranch+"",subCompanyName});
		}
		
		log.debug("*** getAllDepts 方法结束");
		return results;
	}
	
	
	
	/**
	 * 根据部门名称查询该部门的所有子部门
	 * @param companyId
	 * @param parentDeptName
	 * @return List  [部门名称列表, 是否有子部门(true,false),是否有人员(true,false),是否是分支（true，false），部门编码]
	 */
	public static List<String[]> getSubDeptsByParentDept(Long companyId, String parentDeptCode){
		log.debug("*** getSubDeptsByParentDept 方法开始");
		log.debug(new StringBuilder("*** Received parameter:[")
			.append("companyId:").append(companyId)
			.append(", parentDeptCode:").append(parentDeptCode)
			.append("]").toString());
		ThreadParameters parameters = new ThreadParameters(companyId);
		ParameterUtils.setParameters(parameters);
		
		List<Department> depts = getAcsApiManager().getSubDeptsByParentDept(companyId, parentDeptCode);
		List<String[]> results = new ArrayList<String[]>();
		boolean hasbranch = getAcsApiManager().hasBranch(companyId);
		for(Department dept : depts){
			String isHasUsersInDept=getAcsApiManager().getUsersByDeptCode(companyId, dept.getCode()).size()>0?"true":"false";
			String subCompanyName = dept.getSubCompanyName();
			if(subCompanyName==null){
				subCompanyName = "";
			}
			results.add(new String[]{dept.getName(), getAcsApiManager().hasSubDepartment(dept),isHasUsersInDept,dept.getBranch()==null?"false":dept.getBranch()+"",dept.getCode(),hasbranch+"",subCompanyName});
		}
		
		log.debug("*** getSubDeptsByParentDept 方法结束");
		return results;
	}

	/**
	 * 查询公司所有的角色
	 * @param companyId   公司ID
	 * @return List       角色名称列表
	 */
	public static List<String[]> getAllRolesBySystemId(Long systemId,Long companyId){
		log.debug("*** getAllRolesBySystemId 方法开始");
		List<Role> rs = getAcsApiManager().getAllRoles(systemId,companyId);
		BusinessSystem system = getAcsApiManager().getSystemById(systemId);
		List<String[]> results = new ArrayList<String[]>();
		boolean hasbranch = getAcsApiManager().hasBranch(companyId);
		for(Role r : rs){
			String subCompanyName = r.getSubCompanyName();
			if(subCompanyName==null){
				subCompanyName="";
			}
			results.add(new String[]{r.getName(),r.getCode(),hasbranch+"",system.getName(),subCompanyName});
			
		}
		
		log.debug("*** getAllRolesBySystemId 方法结束");
		return results;
	}
	/**
	 * 查询公司所有的角色
	 * @param companyId   公司ID
	 * @return List       角色名称列表
	 */
	public static List<String[]> getAllRoles(String systemCode,Long companyId){
		log.debug("*** getAllRoles 方法开始");
		ThreadParameters parameters = new ThreadParameters(companyId);
		ParameterUtils.setParameters(parameters);
		BusinessSystem system=getAcsApiManager().getSystemBySystemCode(systemCode);
		if(system!=null)return getAllRolesBySystemId(system.getId(),companyId);
		log.debug("*** getAllRoles 方法结束");
		return new ArrayList<String[]>();
	}

	/**
	 * 查询公司所有的人员
	 * @param companyId
	 * @return String[用户名称，用户登录名称,用户所属分支机构编码] 
	 */
	public static List<String[]> getAllUsers(Long companyId){
		log.debug("*** getAllUsers 方法开始");
		log.debug(new StringBuilder("*** Received parameter:[")
			.append("companyId:").append(companyId)
			.append("]").toString());
		
		ThreadParameters parameters = new ThreadParameters(companyId);
		ParameterUtils.setParameters(parameters);
		List<User> us = getAcsApiManager().getAllUsers(companyId);
		
		log.debug("*** getAllUsers 方法结束");
		return getUserNamesFromList(companyId,us);
	}

	/**
	 * 查询部门下所有的人员
	 * @param companyId
	 * @param deptCode
	 * @return String[用户名称，用户登录名称,用户所属分支机构编码] 
	 */
	public static List<String[]> getUsersByDept(Long companyId, String deptCode){
		log.debug("*** getUsersByDept 方法开始");
		log.debug(new StringBuilder("*** Received parameter:[")
			.append("companyId:").append(companyId)
			.append(", deptCode:").append(deptCode)
			.append("]").toString());
		
		ThreadParameters parameters = new ThreadParameters(companyId);
		ParameterUtils.setParameters(parameters);
		
		List<User> us = getAcsApiManager().getUsersByDeptCode(companyId, deptCode);//兼容历史版本的流程定义设置
		if(us.size()<=0){
			us = getAcsApiManager().getUsersByDeptName(companyId, deptCode);
		}
		
		log.debug("*** getUsersByDept 方法结束");
		return getUserNamesFromList(companyId,us);
	}

	/**
	 * 查询工作组下所有的人员
	 * @param companyId
	 * @param workGroupCode
	 * @return String[用户名称，用户登录名称,用户所属分支机构编码] 
	 */
	public static List<String[]> getUsersByWorkGroup(Long companyId, String workGroupCode){
		log.debug("*** getUsersByWorkGroup 方法开始");
		log.debug(new StringBuilder("*** Received parameter:[")
			.append("companyId:").append(companyId)
			.append(", workGroupCode:").append(workGroupCode)
			.append("]").toString());
		
		ThreadParameters parameters = new ThreadParameters(companyId);
		ParameterUtils.setParameters(parameters);
		
		List<User> us = getAcsApiManager().getUsersByWorkGroupCode(companyId, workGroupCode);//兼容历史版本的流程定义设置
		if(us.size()<=0){
			us = getAcsApiManager().getUsersByWorkGroupName(companyId, workGroupCode);
		}
		log.debug("*** getUsersByWorkGroup 方法结束");
		return getUserNamesFromList(companyId,us);
	}

	/**
	 * 查询拥有该角色的所有人员
	 * @param companyId
	 * @param roleCode
	 * @return String[用户名称，用户登录名称,用户所属分支机构编码] 
	 */
	public static List<String[]> getUsersByRole(Long companyId, String roleCode){
		log.debug("*** getUsersByRole 方法开始");
		log.debug(new StringBuilder("*** Received parameter:[")
			.append("companyId:").append(companyId)
			.append(", roleCode:").append(roleCode)
			.append("]").toString());
		
		ThreadParameters parameters = new ThreadParameters(companyId);
		ParameterUtils.setParameters(parameters);
		
		Set<User> us = getAcsApiManager().getUsersByRoleCode(ContextUtils.getSystemId(), companyId, roleCode);
		if(us.size()<=0){
			us = getAcsApiManager().getUsersByRoleName(ContextUtils.getSystemId(), companyId, roleCode);
		}
		
		log.debug("*** getUsersByRole 方法结束");
		return getUserNamesFromList(companyId,us);
	}
	
	/**
	 * 查询与给定用户名在同一部门的用户
	 * flex中没有使用该方法
	 * @param companyId
	 * @param userLoginName 用户登录名
	 * @return
	 */
	@Deprecated
	public static List<String[]> getUsersInSameDept(Long companyId, String userLoginName){
		log.debug("*** getUsersInSameDept 方法开始");
		log.debug(new StringBuilder("*** Received parameter:[")
			.append("companyId:").append(companyId)
			.append(", userLoginName:").append(userLoginName)
			.append("]").toString());
		
		List<User> us = getAcsApiManager().getUsersInSameDept(companyId, userLoginName);

		log.debug("*** getUsersInSameDept 方法结束");
		return getUserNamesFromList(companyId,us);
	}
	
	/**
	 * 根据条件查询用户
	 * flex中没有使用该方法
	 * @param companyId
	 * @param conditions
	 * @return
	 */
	@Deprecated
	public static List<String[]> getUsersByCondition(Long companyId, String conditions){
		log.debug("*** getUsersByCondition 方法开始");
		log.debug(new StringBuilder("*** Received parameter:[")
			.append("companyId:").append(companyId)
			.append(", conditions:").append(conditions)
			.append("]").toString());
		
		List<User> us = getAcsApiManager().getUsersByCondition(companyId, conditions);

		log.debug("*** getUsersByCondition 方法结束");
		return getUserNamesFromList(companyId,us);
	}
	
	/**
	 * 根据条件查询用户
	 * flex中没有使用该方法
	 * @param companyId
	 * @param conditions
	 * @return List<[用户名, 登录名, 邮件地址]>
	 */
	@Deprecated
	public static List<String[]> getUserEmailByCondition(Long companyId, String conditions){
		
		List<User> users = getAcsApiManager().getUsersByCondition(companyId, conditions);

		List<String[]> results = new ArrayList<String[]>();
		String[] names = null;
		if(users != null){
			for(User u : users){
				names = new String[3];
				names[0] = u.getName();
				names[1] = u.getLoginName();
				names[2] = u.getEmail();
				results.add(names);
			}
		}
		return results;
	}
	
	/**
	 * 查询没有在任何部门的用户
	 * @param companyId
	 * @return
	 */
	public static List<String[]> getUsersNotInDept(Long companyId){
		log.debug("*** getUsersNotInDept 方法开始");
		log.debug(new StringBuilder("*** Received parameter:[")
			.append("companyId:").append(companyId)
			.append("]").toString());
		
		ThreadParameters parameters = new ThreadParameters(companyId);
		ParameterUtils.setParameters(parameters);
		
		List<User> us = getAcsApiManager().getUsersNotInDept(companyId);

		log.debug("*** getUsersNotInDept 方法结束");
		return getUserNamesFromList(companyId,us);
	}
	
	/**
	 * 查询没有在任何部门的用户
	 * @param companyId
	 * @return
	 */
	public static List<String[]> getBranchUsersNotInDept(Long companyId,String deptCode){
		log.debug("*** getUsersNotInDept 方法开始");
		log.debug(new StringBuilder("*** Received parameter:[")
			.append("companyId:").append(companyId)
			.append("]").toString());
		
		ThreadParameters parameters = new ThreadParameters(companyId);
		ParameterUtils.setParameters(parameters);
		
		Department dept = getAcsApiManager().getDepartmentByCode(companyId, deptCode);
		List<User> us = new ArrayList<User>();
		if(dept!=null){
			if(dept.getBranch()){
				us = getAcsApiManager().getUsersWithoutBranch(companyId, dept.getId());
			}
		}

		log.debug("*** getUsersNotInDept 方法结束");
		return getUserNamesFromList(companyId,us);
	}
	
	private static List<String[]> getUserNamesFromList(Long companyId,Collection<User> users){
		log.debug("*** getUserNamesFromList 方法开始");
		
		boolean hasbranch = getAcsApiManager().hasBranch(companyId);
		List<String[]> results = new ArrayList<String[]>();
		String[] names = null;
		if(users != null){
			for(User u : users){
				names = new String[5];
				names[0] = u.getName();
				names[1] = u.getLoginName();
				names[2] = u.getSubCompanyCode();
				names[3] = u.getSubCompanyName();
				names[4] = hasbranch+"";
				results.add(names);
			}
		}
		
		log.debug("*** getUserNamesFromList 方法结束");
		return results;
	}
	
	/**
	 * 查询所有业务系统信息
	 */
	public static List<String[]> getAllBusiness(Long companyId){
		List<BusinessSystem> list=getAcsApiManager().getAllBusiness(companyId);
		List<String[]> results = new ArrayList<String[]>();
		String[] bs = null;
		if(list != null){
			for(BusinessSystem u : list){
				bs = new String[3];
				bs[0] = u.getId().toString();
				bs[1] = u.getName();
				bs[2] = u.getCode();
				results.add(bs);
			}
		}
		return results;
	}
	
	/**
	 * 查询所有分支机构
	 */
	public static List<String[]> getAllBranch(Long companyId){
		List<Department> list=getAcsApiManager().getAllBranchs(companyId);
		List<String[]> results = new ArrayList<String[]>();
		String[] ds = null;
		Company company = getAcsApiManager().getCompanyById(companyId);
		List<Workgroup> wgs = getAcsApiManager().getWorkgroups(companyId);
		ds = new String[3];//name,code,是否存在工作组
		ds[0] = company.getName();
		ds[1] = company.getCode();
		ds[2] = wgs.size()>0?true+"":false+"";
		results.add(ds);
		if(list != null){
			for(Department d : list){
				wgs = getAcsApiManager().getWorkgroupByBranchId(companyId, d.getId());
				ds = new String[3];
				ds[0] = d.getName();
				ds[1] = d.getCode();
				ds[2] = wgs.size()>0?true+"":false+"";
				results.add(ds);
			}
		}
		return results;
	}
	/**
	 * 当前租户是否存在分支机构
	 */
	public static String hasBranch(Long companyId){
		ThreadParameters parameters = new ThreadParameters(companyId);
		ParameterUtils.setParameters(parameters);
		return ContextUtils.hasBranch()+"";
	}
	
	/**
	 * 查询该分支机构内的工作组
	 */
	public static List<String[]> getWorkgroupByBranchCode(Long companyId,String branchCode){
		ThreadParameters parameters = new ThreadParameters(companyId);
		ParameterUtils.setParameters(parameters);
		
		Company company = getAcsApiManager().getCompanyById(companyId);
		boolean hasbranch = getAcsApiManager().hasBranch(companyId);
		List<String[]> results = new ArrayList<String[]>();
		if(branchCode.equals(company.getCode())){
			return getWorkgroups(companyId);
		}else{
			Department branch = getAcsApiManager().getDepartmentByCode(companyId, branchCode);
			if(branch!=null){
				List<Workgroup> wgs = getAcsApiManager().getWorkgroupByBranchId(companyId, branch.getId());
				for(Workgroup wg : wgs){
					String subCompanyName = wg.getSubCompanyName();
					if(subCompanyName==null){
						subCompanyName="";
					}
					results.add(new String[]{wg.getName(),wg.getCode(),hasbranch+"",subCompanyName});
				}
			}
		}
		return results;
	}
	/**
	 * 查询总公司下的分支机构，不包括分支机构的
	 * @param companyId
	 * @return
	 */
	public static List<String[]> getWorkgroups(Long companyId){
		boolean hasbranch = getAcsApiManager().hasBranch(companyId);
		List<Workgroup> wgs = getAcsApiManager().getWorkgroups(companyId);
		List<String[]> results = new ArrayList<String[]>();
		for(Workgroup wg : wgs){
			String subCompanyName = wg.getSubCompanyName();
			if(subCompanyName==null){
				subCompanyName="";
			}
			results.add(new String[]{wg.getName(),wg.getCode(),hasbranch+"",subCompanyName});
		}
		return results;
	}
	
	
	
}
