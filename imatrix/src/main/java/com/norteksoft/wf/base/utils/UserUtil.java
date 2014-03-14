package com.norteksoft.wf.base.utils;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang.StringUtils;

import com.norteksoft.acs.entity.authorization.BusinessSystem;
import com.norteksoft.acs.entity.organization.Workgroup;
import com.norteksoft.acs.service.authorization.BusinessSystemManager;
import com.norteksoft.acs.service.authorization.StandardRoleManager;
import com.norteksoft.acs.service.organization.UserManager;
import com.norteksoft.product.api.ApiFactory;
import com.norteksoft.product.api.entity.Department;
import com.norteksoft.product.api.entity.Role;
import com.norteksoft.product.api.entity.User;
import com.norteksoft.product.util.ContextUtils;
import com.norteksoft.product.util.ParameterUtils;
import com.norteksoft.product.util.ThreadParameters;
import com.norteksoft.wf.base.enumeration.CommonStrings;
import com.norteksoft.wf.base.enumeration.TextOperator;
import com.norteksoft.wf.engine.core.impl.UserParseCalculator;

public class UserUtil {
	
	/**
	 * 判断用户是不是在部门里，如果在返回ture；否则，返回false。
	 * @param companyId 
	 * @param loginName 
	 * @param departmentName
	 * @return 用户在部门里，返回ture；否则，返回false。
	 */
	public static boolean userInDepartment(Long companyId,Long userId,String departmentName){
		if(ContextUtils.getCompanyId()==null){
			ThreadParameters parameters=new ThreadParameters(companyId);
			ParameterUtils.setParameters(parameters);
		}
		List<Department> departments = ApiFactory.getAcsService().getDepartments(userId);
		for (Department department : departments) {
			if((department.getCode()!=null&&department.getCode().equals(departmentName))||department.getName().equals(departmentName)){
				return true;
			}
		}
		return false;
	}
	/**
	 * 判断用户是不是拥有某权限，如果有返回ture；否则，返回false。
	 * @param companyId 
	 * @param loginName 
	 * @param departmentName
	 * @return 用户拥有某权限，返回ture；否则，返回false。
	 */
	public static boolean userHaveRole(Long companyId,Long userid,String roleCode){
		//获得子系统的id集合
		BusinessSystemManager businessSystemManager = (BusinessSystemManager)ContextUtils.getBean("businessSystemManager");
		List<Long> subSystemIds=businessSystemManager.getSystemIdsByParentCode(ContextUtils.getSystemCode());
		if(ContextUtils.getCompanyId()==null){
			ThreadParameters parameters=new ThreadParameters(companyId);
			ParameterUtils.setParameters(parameters);
		}
		Set<Role> roles = ApiFactory.getAcsService().getRolesByUser(userid);
		if(StringUtils.isNotEmpty(roleCode)){
			for (Role role : roles) {
				for(String fieldValue:roleCode.split(",")){//字段中可能会有多个角色
					fieldValue = fieldValue.trim();
					if(StringUtils.isNotEmpty(fieldValue)){
						if(role.getCode().equals(fieldValue)||role.getName().equals(fieldValue)){//角色编码或角色名称等于指定的角色
							boolean result =  validateRole(role,subSystemIds);
							if(result){
								return result;
							}else{
								continue;
							}
						}
					}
				}
			}
		}
		return false;
	}
	
	private static boolean validateRole(Role role,List<Long> subSystemIds){
		BusinessSystem system = role.getBusinessSystem();
		if(system!=null){
			if(subSystemIds.contains(system.getId())){//但当前系统是子系统，且角色所在的系统包含在子系统集合中
				return true;
			}else{
				if(subSystemIds.size()<=0){//当前系统不是子系统
					if(ContextUtils.getSystemId().equals(system.getId())){
						return true;
					}
				}
			}
		}
		return false;
	}
	/**
	 * 判断用户是不是在工作组里，如果在返回ture；否则，返回false。
	 * @param companyId 
	 * @param loginName 
	 * @param departmentName
	 * @return 用户在工作组里，返回ture；否则，返回false。
	 */
	public static boolean userInWorkGroup(Long companyId,Long userId,String workGroupName){
		if(ContextUtils.getCompanyId()==null){
			ThreadParameters parameters=new ThreadParameters(companyId);
			ParameterUtils.setParameters(parameters);
		}
		List<com.norteksoft.product.api.entity.Workgroup> workGroups = ApiFactory.getAcsService().getWorkgroupsByUser(userId);
		for (com.norteksoft.product.api.entity.Workgroup workGroup : workGroups) {
			if((workGroup.getCode()!=null&&workGroup.getCode().equals(workGroupName))||workGroup.getName().equals(workGroupName)){
				return true;
			}
		}
		return false;
	}
	
	/**
	 * 解析用户是否满足条件 ，判断条件的值有3中来源，分别为组织结构中、表单字段中和标准值
	 * ${currentTransactorName} operator.text.et '吴荣[wurong]' 
	 *  ${currentTransactorName} operator.text.et '${documentCreatorName}'
	 * ${currentTransactorName} operator.text.et '${field[姓名[name]]}
	 */
	public static boolean parseUser(String atomicExpress,String loginName){
		if(StringUtils.contains(atomicExpress, TextOperator.NET.getCode())) {
			String userLoginName = StringUtils.substringBetween(atomicExpress, "'", "'").trim();
			return !loginName.equals(userLoginName);
		}else if(StringUtils.contains(atomicExpress, TextOperator.ET.getCode())){
			String userLoginName = StringUtils.substringBetween(atomicExpress, "'", "'").trim();
			return loginName.equals(userLoginName);
		}
		return false;
	}
	
	
	/**
	 * 解析用户是否拥有某角色
	 */
	public static boolean parseRole(String atomicExpress,Long userid){
		if(StringUtils.contains(atomicExpress, TextOperator.NET.getCode())) {
			String roleName = StringUtils.substringBetween(atomicExpress, "'", "'").trim();
			if(roleName.indexOf(UserParseCalculator.SQUARE_BRACKETS_LEFT)>=0){//新版本记录的是角色编码
				roleName = roleName.substring(roleName.indexOf(UserParseCalculator.SQUARE_BRACKETS_LEFT)+1,roleName.lastIndexOf(UserParseCalculator.SQUARE_BRACKETS_RIGHT));
			}
			return !userHaveRole(ContextUtils.getCompanyId(), userid, roleName);
		}else if(StringUtils.contains(atomicExpress, TextOperator.ET.getCode())){
			String roleName = StringUtils.substringBetween(atomicExpress, "'", "'").trim();
			if(roleName.indexOf(UserParseCalculator.SQUARE_BRACKETS_LEFT)>=0){//新版本记录的是角色编码
				roleName = roleName.substring(roleName.indexOf(UserParseCalculator.SQUARE_BRACKETS_LEFT)+1,roleName.lastIndexOf(UserParseCalculator.SQUARE_BRACKETS_RIGHT));
			}
			return userHaveRole(ContextUtils.getCompanyId(), userid, roleName);
		}
		return false;
	}
	
	/**
	 * 解析用户和部门的关系
	 */
	public static boolean parseDepartment(String atomicExpress,Long userId){
		if(StringUtils.contains(atomicExpress, TextOperator.NET.getCode())) {
			String departmentName = StringUtils.substringBetween(atomicExpress, "'", "'").trim();
			return !userInDepartment(ContextUtils.getCompanyId(), userId, departmentName);
		}else if(StringUtils.contains(atomicExpress, TextOperator.ET.getCode())){
			String departmentName = StringUtils.substringBetween(atomicExpress, "'", "'").trim();
			return userInDepartment(ContextUtils.getCompanyId(), userId, departmentName);
		}
		return false;
	}
	/**
	 * 解析用户和工作组的关系
	 */
	public static boolean parseWorkGroup(String atomicExpress,Long userId){
		 if(StringUtils.contains(atomicExpress, TextOperator.NET.getCode())) {
				String workGroupName = StringUtils.substringBetween(atomicExpress, "'", "'").trim();
				return !userInWorkGroup(ContextUtils.getCompanyId(), userId, workGroupName);
		}else if(StringUtils.contains(atomicExpress, TextOperator.ET.getCode())){
			String workGroupName = StringUtils.substringBetween(atomicExpress, "'", "'").trim();
			return userInWorkGroup(ContextUtils.getCompanyId(), userId, workGroupName);
		}
		return false;
	}
	
	public static Set<String> getUserExceptLoginName(String loginName){
		return ApiFactory.getAcsService().getLoginNamesExclude(loginName);
	} 
	
	public static Set<String> getUsersExceptRoleName(Long systemId,String roleName){
		Set<String> userNames = new HashSet<String>();
		for(User user: ApiFactory.getAcsService().getUsersWithoutRoleName(systemId,roleName)){
			if(user!=null){
				userNames.add(user.getLoginName());
			}
		}
		return userNames;
	}
	
	public static Set<String> getUsersExceptRoleName(String roleName,Long systemId,Long companyId){
		Set<String> userNames = new HashSet<String>();
		for(User user: ApiFactory.getAcsService().getUsersWithoutRoleName(systemId,roleName)){
			if(user!=null){
				userNames.add(user.getLoginName());
			}
		}
		return userNames;
	}
	
	public static Set<Long> getUsersByRoleName( Long systemId,String roleName){
		Set<Long> userIds = new HashSet<Long>();
		for(User user: ApiFactory.getAcsService().getUsersByRoleName(systemId,roleName)){
			if(user!=null){
				userIds.add(user.getId());
			}
		}
		return userIds;
	}
	public static Set<Long> getUsersByRoleCode( Long systemId,String roleCode){
		Set<Long> userIds = new HashSet<Long>();
		for(User user: ApiFactory.getAcsService().getUsersByRoleCodeExceptTrustedRole(systemId,roleCode)){
			if(user!=null){
				userIds.add(user.getId());
			}
		}
		return userIds;
	}
	
	public static Set<Long> getUsersByRoleName( String roleName,Long systemId,Long companyId){
		Set<Long> userIds = new HashSet<Long>();
		for(User user: ApiFactory.getAcsService().getUsersByRoleName(systemId,roleName)){
			if(user!=null){
				userIds.add(user.getId());
			}
		}
		return userIds;
	}
	public static Set<Long> getUsersByRoleCode( String roleCode,Long systemId,Long companyId){
		Set<Long> userIds = new HashSet<Long>();
		for(User user: ApiFactory.getAcsService().getUsersByRoleCodeExceptTrustedRole(systemId,roleCode)){
			if(user!=null){
				userIds.add(user.getId());
			}
		}
		return userIds;
	}
	
	public static List<Department> getDepartmentsByUser(String loginName){
		return ApiFactory.getAcsService().getDepartments(loginName);
	}
	
	public static List<Department> getDepartmentsByUserId(Long userId){
		return ApiFactory.getAcsService().getDepartmentsByUserId(userId);
	}
	
	public static Department getDepartmentByName(String name){
		return ApiFactory.getAcsService().getDepartmentByName(name);
	}
	
	public static Department getDepartmentByCode(String code){
		return ApiFactory.getAcsService().getDepartmentByCode(code);
	}
	
	public static Set<Long> getUsersNotInDepartment(Set<Department> departmentSet){
		Set<Long> userIdInDepts = getUsersByDepartment(departmentSet);
		List<Long> allUserids = ApiFactory.getAcsService().getAllUserIdsWithoutAdminByCompany();
		allUserids.removeAll(userIdInDepts);
		Set<Long> result = new HashSet<Long>();
		result.addAll(allUserids);
		return result;
	}
	
	public static Set<Long> getUsersByDepartment(Set<Department> departmentSet){
		Set<Long> userIds = new HashSet<Long>();
		for(Department department:departmentSet){
			if(department!=null){
				userIds.addAll(getUserIds(ApiFactory.getAcsService().getUsersByDepartmentId(department.getId())));
			}
		}
		return userIds;
	}
	public static Set<Long> getUserIds(Collection<User> users){
		Set<Long> userIds = new HashSet<Long>();
		for(User user : users){
			if(user!=null){
				userIds.add(user.getId());
			}
		}
		return userIds;
	}
	public static Set<Long> getUsersNotInWorkGroup(Set<Workgroup> workgroupSet){
		Set<Long> userIdsInWg = getUsersByWorkGroup(workgroupSet);
		List<Long> allUserids = ApiFactory.getAcsService().getAllUserIdsWithoutAdminByCompany();
		allUserids.removeAll(userIdsInWg);
		Set<Long> result = new HashSet<Long>();
		result.addAll(allUserids);
		return result;
	}
	
	
	public static Set<Long> getUsersByWorkGroup(Set<Workgroup> workgroupSet){
		Set<Long> userIds = new HashSet<Long>();
		for(Workgroup workGroup:workgroupSet){
			if(workGroup!=null){
				userIds.addAll(getUserIds(ApiFactory.getAcsService().getUsersByWorkgroupId(workGroup.getId())));
			}
		}
		return userIds;
	}
	
	public static Workgroup getWorkGroupByName(String workGroupName){
		UserManager userManager = (UserManager)ContextUtils.getBean("userManager");
		return userManager.getWorkgroupByName(workGroupName);
	}
	public static Workgroup getWorkGroupByCode(String workGroupCode){
		UserManager userManager = (UserManager)ContextUtils.getBean("userManager");
		return userManager.getWorkgroupByCode(workGroupCode);
	}
	
	public static List<Workgroup> getWorkGroupsByUser(String loginName){
		UserManager userManager = (UserManager)ContextUtils.getBean("userManager");
		return userManager.getWorkgroupsByUser(loginName);
	}
	public static List<Workgroup> getWorkGroupsByUserId(Long userId){
		UserManager userManager = (UserManager)ContextUtils.getBean("userManager");
		return userManager.getWorkgroupsByUserId(userId);
	}
	public static List<String> getRoleCodesBySystem(Long systemId){
		StandardRoleManager standardRoleManager = (StandardRoleManager)ContextUtils.getBean("standardRoleManager");
		return standardRoleManager.getRoleCodesBySystemId(systemId);
	}
	
	public static com.norteksoft.product.api.entity.User getUserByInfo(String userinfo){
		com.norteksoft.product.api.entity.User user = null;
		if(userinfo.matches(CommonStrings.NUMBER_REG)){//如果是用户id
			user = ApiFactory.getAcsService().getUserByLoginName(userinfo);//判断是否有以该数字为登录名的用户
    		if(user==null){//没有以该数字为登录名的用户，则表示该数字是用户id
    			user = ApiFactory.getAcsService().getUserById(Long.parseLong(userinfo));
    		}
		}else{
			user = ApiFactory.getAcsService().getUserByLoginName(userinfo);
		}
		return user;
	}
	
	
}
