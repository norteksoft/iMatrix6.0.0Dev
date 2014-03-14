package com.norteksoft.product.api;

import java.util.Collection;
import java.util.List;
import java.util.Set;

import com.norteksoft.product.api.entity.BusinessSystem;
import com.norteksoft.product.api.entity.Role;
import com.norteksoft.product.api.entity.Department;
import com.norteksoft.product.api.entity.User;
import com.norteksoft.acs.entity.organization.UserInfo;
import com.norteksoft.product.api.entity.Workgroup;

public interface AcsService {
	/**
	 * 替换为<code> Long getOnlineUserCount();</code>
	 * @param companyId
	 * @return
	 */
	@Deprecated
	public Long getOnlineUserCount(Long companyId);
	
	/**
	 * 查询公司在线用户数量
	 * @return
	 */
	public Long getOnlineUserCount();
	
	/**
	 * 查询在线用户人员ID
	 * @return
	 */
	public List<Long> getOnlineUserIds();
	/**
	 * 替换为<code> List<Department> getDepartments();</code>
	 * @param companyId
	 * @return
	 */
	@Deprecated
	public List<Department> getDepartmentList(Long companyId);
	
	/**
	 * 查询集团公司下的所有的顶级部门(包含分支机构)
	 * @return List<Department>
	 */
	public List<Department> getDepartments();
	/**
	 * 查询集团公司下的所有的顶级部门（不包含分支机构）
	 * @return List<Department>
	 */
	public List<Department> getDepartmentsByCompany();
	
	/**
	 * 查询公司所有的分支机构
	 * @return List<Department>
	 */
	public List<Department> getBranchs();

	/**
	 * 替换为<code> List<Workgroup> getWorkgroups();</code>
	 * @param companyId
	 * @return
	 */
	@Deprecated
	public List<Workgroup> getWorkGroupList(Long companyId);
	
	/**
	 * 查询总公司的所有的工作组(不包含分支机构)
	 * @return List<WorkGroup>
	 */
	public List<Workgroup> getWorkgroups();
	
	/**
	 * 查询该分支机构下的所有工作组
	 * @return List<WorkGroup>
	 */
	public List<Workgroup> getWorkgroupsByBranchId(Long branchId);

	/**
	 * 查询总公司的所有的工作组(包含分支机构内的)
	 * @return List<WorkGroup>
	 */
	public List<Workgroup> getAllWorkgroups();
	/**
	 * 替换为<code> List<User> getUsersByDepartmentId(Long departmentId);</code>
	 * @param companyId
	 * @return
	 */
	@Deprecated
	public List<com.norteksoft.acs.entity.organization.User> getUserListByDepartmentId(Long departmentId);
	
	/**
	 * 根据部门ID查询该部门的人员
	 * @param departmentId 部门Id
	 * @return List<User>
	 */
	public List<User> getUsersByDepartmentId(Long departmentId);
	
	/**
	 * 根据部门名称得到部门下用户的登录名
	 * 该方法不适用于多分支机构，当分支之间存在同名部门时该方法会有问题，请调用<code>List<String> getLoginNamesByDepartmentId(Long departmentId);</code>
	 * @param departmentName 部门名称
	 * @return
	 */	
	public List<String> getUserLoginNamesByDepartmentName(String departmentName);
	/**
	 * 根据部门id得到部门下用户的登录名
	 * @param departmentId 部门id
	 * @return
	 */	
	public List<String> getLoginNamesByDepartmentId(Long departmentId);
	
	/**
	 * 根据工作组ID查询工作组的人员
	 * @param workgroupId 工作组Id
	 * @return List<User>
	 */
	public List<User> getUsersByWorkgroupId(Long workgroupId);

	/**
	 * 根据父部门id查询该父部门下所有子部门
	 * @param paternDepartmentId 父部门Id
	 * @return List<Department>
	 */
	public List<Department> getSubDepartmentList(Long paternDepartmentId);

	/**
	 * 根据用户Id得到用户实体
	 * @return User
	 */
	public User getUserById(Long id);

	/**
	 * 查询用户委托的角色。 
	 * @param trusteeId 受托人ID
	 * @param trustorId 委托人ID
	 * @return
	 */
	public Set<Role> getTrustedRolesByUserId(Long trusteeId, Long trustorId);
	
	/**
	 * 根据用户获取用户的角色字符串形式（不含委托）。
	 * 替换为<code>String getRolesExcludeTrustedRole(User user);</code>
	 */
	@Deprecated
	public String getRoleCodesFromUser(com.norteksoft.acs.entity.organization.User user);
	
	/**
	 * 根据用户查询用户的角色（不含委托）
	 * @param userId
	 * @return Set<Role>
	 */
	public String getRolesExcludeTrustedRole(User user);
	
	/**
	 * 替换为<code>String getRolesExcludeTrustedRole(User user);</code>
	 * @param user
	 * @return
	 */
	@Deprecated
	public String getRolesExcludeTrustedRole(com.norteksoft.acs.entity.organization.User user);
	
	/**
	 * 查询用户所有的角色
	 * @param userId
	 * @return
	 */
	public Set<Role> getRolesByUser(User user);
	
	/**
	 * 替换为<code>Set<Role> getRolesByUser(User user);</code>
	 * @return
	 */
	@Deprecated
	public Set<Role> getRolesByUser(com.norteksoft.acs.entity.organization.User user);
	
	/**
	 * 替换为<code>List<User> getUsersWithoutDepartment();</code>
	 * @return
	 */
	@Deprecated
	public List<User> getUsersNotInDepartment(Long companyId);
	
	/**
	 * 获取全公司内不属于任何部门的用户(不包含分支机构内的无部门用户)
	 * @return List<User>
	 */
	public List<User> getUsersWithoutDepartment();
	
	/**
	 * 获取分支机构内不属于任何部门的用户
	 * @return List<User>
	 */
	public List<User> getUsersWithoutBranch(Long branchId);
	
	/**
	 * 替换为<code>assignTrustedRole(Long trustorId, String[]roleIds, Long trusteeId);</code>
	 * @return
	 */
	@Deprecated
	public void assignRolesToSomeone(Long someoneId,String[] roleIds,Long companyId,Long sourceUserId);
	
	/**
	 * 将角色授权给别人，自己还保留该角色
	 * @param someoneId 受权人
	 * @param roleIds 角色id数组
	 * @param companyId
	 * @param sourceUserId //授权人
	 */
	public void assignTrustedRole(Long trustorId, String[]roleIds, Long trusteeId);
	
	/**
	 * 替换为<code>deleteTrustedRole(Long trustorId, String[]roleIds,Long trusteeId);</code>
	 * @return
	 */
	@Deprecated
	public void deleteRoleUsers(Long userId,String[] rIds,Long companyId,Long sourceId)	;
	
	/**
	 * 删除委托人委托出去的角色
	 * @param userId 受委托人的id
	 * @param rIds 角色id数组
	 * @param companyId 公司id
	 * @param sourceId 委托人id
	 */
	public void deleteTrustedRole(Long trustorId, String[]roleIds,Long trusteeId);
	
	/**
	 * 根据roleId得到role
	 * @param roleId
	 */
	public Role getRoleById(Long roleId);
	/**
	 * 根据roleCode得到role
	 * @param roleCode
	 */
	public Role getRoleByCode(String roleCode);
	/**
	 * 根据roleName得到role
	 * @param roleCode
	 */
	public List<Role> getRolesByName(String roleName);
	
	/**
	 * 替换为<code>deleteAllTrustedRole(Long trustorId, Long trusteeId);</code>
	 * @return
	 */
	@Deprecated
	public void deleteAssignedAuthority(Long sourceId,Long userId,Long companyId);
	
	/**
	 * 删除由别人分配的权限
	 * @param sourceId
	 * @param userId
	 * @param companyId
	 */
	public void deleteAllTrustedRole(Long trustorId, Long trusteeId);

	/**
	 * 通过工作组ID获取工作组
	 * @param workgroupId
	 * @return Workgroup
	 */
	public Workgroup getWorkgroupById(Long workgroupId);
	
	/**
	 * 替换为<code>Workgroup getWorkgroupByName(String name);</code>
	 * @param name
	 * @param companyId
	 * @return
	 */
	@Deprecated
	public Workgroup getWorkGroupByName(String name, Long companyId);
	
	/**
	 * 通过工作组名称获取工作组
	 * 该方法不适用于多分支机构，当分支之间存在同名工作组时该方法会有问题，请调用<code>Workgroup getWorkgroupByCode(String code); 或Workgroup getWorkgroupById(Long workgroupId); </code>
	 * @param workgroupName
	 * @return Workgroup
	 */
	public Workgroup getWorkgroupByName(String name);
	
	/**
	 * 通过工作组编号获取工作组
	 * @param workgroupCode
	 * @return Workgroup
	 */
	public Workgroup getWorkgroupByCode(String code);
	
	/**
	 * 通过部门ID获取部门实体
	 * @param departmentId
	 * @return Department
	 */
	public Department getDepartmentById(Long departmentId);
	
	/**
	 * 通过工作组名称获取工作组
	 * 替换为<code>Department getDepartmentByName(String name); </code>
	 */
	@Deprecated
	public Department getDepartmentByName(String name, Long companyId);
	
	/**
	 * 通过部门名称获取部门实体
	 * 该方法不适用于多分支机构，当分支之间存在同名部门时该方法会有问题，请调用<code>Department getDepartmentByCode(String code); 或Department getDepartmentById(Long departmentId); </code>
	 * @param name
	 * @return Department
	 */
	public Department getDepartmentByName(String name);
	
	/**
	 * 通过部门编号获取部门实体
	 * @param code
	 * @return Department
	 */
	public Department getDepartmentByCode(String code);
	/**
	 * 查询所有人员（不包含无部门人员）
	 * 替换为<code>List<User> getUsersByCompany(); </code>
	 * @param companyId
	 * @return
	 */
	@Deprecated
	public List<User> getUsersByCompany(Long companyId);
	/**
	 * 查询所有人员（不包含无部门人员）
	 * @return
	 */
	public List<User> getUsersByCompany();
	/**
	 * 查询所有人员（包含无部门人员）
	 * 替换为<code>List<User> getAllUsersByCompany(); </code>
	 * @param companyId
	 * @return
	 */
	@Deprecated
	public List<User> getAllUsersByCompany(Long companyId);
	/**
	 * 查询所有人员（包含无部门人员）
	 * @return
	 */
	public List<User> getAllUsersByCompany();
	/**
	 * 根据popTree传到后台的字符串解析，得到相应的人员数组
	 * @param companyId
	 * @return
	 */
	public List<User> getTreeUser(String str);
	/**
	 * 根据popTree传到后台的字符串解析，得到相应的人员数组
	 * @param companyId
	 * @return
	 */
	public List<Long> getTreeUserIds(String str);
	/**
	 * 根据popTree传到后台的字符串解析，得到相应的人员数组
	 * @param companyId
	 * @return
	 */
	public List<String> getTreeUserLoginNames(String str);
	/**
	 * 根据popTree传到后台的字符串解析，得到相应的部门数组
	 * @param companyId
	 * @return
	 */
	public List<Department> getTreeDepartment(String str,boolean isIncludeBranch);
	/**
	 * 根据popTree传到后台的字符串解析，得到相应的部门数组
	 * @param companyId
	 * @return
	 */
	public List<Long> getTreeDepartmentIds(String str,boolean isIncludeBranch);
	/**
	 * 根据popTree传到后台的字符串解析，得到相应的工作组数组
	 * @param companyId
	 * @return
	 */
	public List<Workgroup> getTreeWorkgroup(String str);
	
	public List<Long> getTreeWorkgroupIds(String str);
	
	/**
	 * 查询分支机构下所有人员（包含无部门人员，不包含子分支机构下人员）
	 * @param companyId
	 * @return
	 */
	public List<User> getAllUsersByBranch(Long branchId);
	
	/**
	 * 查询分支机构下所有人员（包含无部门人员和子分支机构下的人员）
	 * @param companyId
	 * @return
	 */
	public List<User> getAllUsersByBranchIncludeChildren(List<Long> branchId);
	/**
	 * 查询所有人员（包含无部门人员，但不包含系统默认的三员）
	 * @param companyId
	 * @return
	 */
	public List<User> getUsersByCompanyWithoutAdmin();
	/**
	 * 查询所有人员id（包含无部门人员，但不包含系统默认的三员）
	 * @param companyId
	 * @return
	 */
	public List<Long> getUserIdsByCompanyWithoutAdmin();
	/**
	 * 查询所有人员名称（包含无部门人员，但不包含系统默认的三员）
	 * @param companyId
	 * @return
	 */
	public List<String> getUserNamesByCompanyWithoutAdmin();
	
	/**
	 * 替换为<code>Set<User> getUsersByRoleName(Long systemId, String roleName); </code>
	 * @param systemId
	 * @param companyId
	 * @param roleName
	 * @return
	 */
	@Deprecated
	public Set<User> getUsersByRoleName(Long systemId, Long companyId, String roleName);
	
	/**
	 * 通过角色名称查询拥有该角色的用户
	 * 该方法不适用于多分支机构，当分支之间存在同名角色时该方法会有问题，请调用<code>Set<User> getUsersByRoleCodeExceptTrustedRole(Long systemId, String roleCode); </code>
	 * @param systemId
	 * @param roleName
	 * @return
	 */
	public Set<User> getUsersByRoleName(Long systemId, String roleName);
	
	/**
	 * 查询没有该角色名称权限的用户
	 * 替换为<code>Set<User> getUsersWithoutRoleName(Long systemId, String roleName); </code>
	 * @param systemId
	 * @param companyId
	 * @param roleName
	 * @return
	 */
	@Deprecated
	public Set<User> getUsersExceptRoleName(Long systemId, Long companyId, String roleName);

	/**
	 * 查询没有该角色名称权限的用户
	 * 该方法不适用于多分支机构，当分支之间存在同名角色时该方法会有问题，请调用<code>Set<User> getUsersWithoutRoleCode(Long systemId, String roleCode); </code>
	 * @param systemId
	 * @param roleName
	 * @return
	 */
	public Set<User> getUsersWithoutRoleName(Long systemId, String roleName);
	/**
	 * 查询没有该角色编码权限的用户
	 * @param systemId
	 * @param roleCode
	 * @return
	 */
	public Set<User> getUsersWithoutRoleCode(Long systemId, String roleCode);
	/**
	 * 通过角色编号查询所有的用户（不含委托）
	 * 替换为<code>Set<User> getUsersByRoleCodeExceptTrustedRole(Long systemId, String roleCode); </code>
	 * @param systemId
	 * @param companyId
	 * @param roleCode
	 * @return
	 */
	
	@Deprecated
	public Set<User> getUsersByRole(Long systemId, Long companyId, String roleCode);
	
	/**
	 * 通过角色编号查询所有的用户（不含委托）
	 * @param systemId
	 * @param roleCode
	 * @return
	 */
	public Set<User> getUsersByRoleCodeExceptTrustedRole(Long systemId, String roleCode);
	/**
	 * 通过角色编号查询所有的用户（不含委托）
	 * @param systemId
	 * @param roleCode
	 * @return
	 */
	public Set<Long> getUserIdsByRoleCodeExceptTrustedRole(Long systemId, String roleCode);
	/**
	 * 获得腾讯通的url地址
	 * 替换为<code>String getRtxUrl(); </code>
	 * @param companyId
	 * @return
	 */
	@Deprecated
	public String getRtxUrl(Long companyId);
	
	public String getRtxUrl();
	/**
	 * 是否启用了rtx集成
	 * 替换为<code>Boolean isRtxEnable(); </code>
	 * @param companyId
	 * @return
	 */
	@Deprecated
	public Boolean isRtxInvocation(Long companyId);
	
	/**
	 * 是否启用了rtx集成
	 * */
	public Boolean isRtxEnable();
	/**
	 * 根据用户ID查询用户所在的部门
	 * 替换为<code>List<Department> getDepartmentsByUserId(Long userId); </code>
	 * @param companyId
	 * @param userId
	 * @return
	 */
	@Deprecated
	public List<Department> getDepartmentsByUser(Long companyId,Long userId);
	
	/**
	 * 根据用户ID查询用户所在的部门
	 * @param userId
	 * @return
	 */
	public List<Department> getDepartmentsByUserId(Long userId);
	/**
	 * 根据用户登录名查询用户
	 * 替换为<code>User getUserByLoginName(String loginName); </code>
	 * @param companyId
	 * @param loginName
	 * @return
	 */
	@Deprecated
	public User getUser(Long companyId, String loginName);
	
	/**
	 * 根据登录名查询用户信息(单个公司)
	 * 当存在多分支机构时会存在登录名重复的情况， 请用<code>User getUserByLoginNameAndBranchId(String loginName,Long branchId) 或 User  getUserByLoginNameAndBranchCode(String loginName,String branchCode)</code>
	 * @param loginName
	 * @return
	 */
	public User getUserByLoginName(String loginName);
	
	/**
	 * 根据登录名查询用户信息(多分支机构情况)
	 * @param loginName
	 * @param branchId：只能是分支机构id，如果为空，则表示在集团下查询该登录名的用户
	 * @return
	 */
	public User getUserByLoginNameAndBranchId(String loginName,Long branchId);
	
	/**
	 * 根据登录名查询用户信息(多分支机构情况)
	 * @param loginName
	 * @param branchCode :可以是公司编码或分支机构编码
	 * @return
	 */
	public User getUserByLoginNameAndBranchCode(String loginName,String branchCode);
	/**
	 * 根据用户邮箱查询用户
	 * 替换为<code>User getUserByEmail(String email); </code>
	 * @param email
	 * @return
	 */
	@Deprecated
	public User getUser(String email);
	
	/**
	 * 根据邮件地址查询用户信息
	 * @param email:5.2.1.RC版本及之前的版本中根据邮箱只能查到一个用户
	 * @return
	 */
	public User getUserByEmail(String email);
	/**
	 * 根据邮件地址查询用户信息
	 * @param email：5.5版本中根据邮箱可以查到多个用户
	 * @return
	 */
	public List<User> getUsersByEmail(String email);
	
	/**
	 * 根据用户姓名查询用户
	 * @param userName
	 * @return
	 */
	public List<User> getUsersByName(String userName);
	
	/**
	 * 查询除该登录名外的其他用户的登录名
	 * 替换为<code>Set<String> getLoginNamesExclude(String loginName); </code>
	 * @param companyId
	 * @param loginName
	 * @return
	 */
	@Deprecated
	public Set<String> getUserExceptLoginName(Long companyId, String loginName);
	
	/**
	 * 查询除该登录名外的其他用户的登录名
	 * 该方法不适用于多分支机构，当分支之间存在同登录名用户时该方法会有问题，请调用<code>Set<Long> getLoginNamesExclude(Long userId);</code>
	 * @param loginName
	 * @return
	 */
	public Set<String> getLoginNamesExclude(String loginName);
	/**
	 * 查询除该用户外的其他用户的id
	 * @param loginName
	 * @return
	 */
	public Set<Long> getLoginNamesExclude(Long userId);
	
	/**
	 * 根据用户登录名查询用户所在的部门
	 * @param companyId
	 * @param loginName
	 * @return
	 * @deprecated 替换为<code>List<Department> getDepartments(String loginName);</code>
	 */
	@Deprecated
	public List<Department> getDepartmentsByUser(Long companyId, String loginName);
	/**
	 * 根据用户登录名查询用户所在的部门。
	 * 该方法不适用于多分支机构，当分支之间存在同登录名用户时该方法会有问题，请调用<code>List<Department> getDepartments(Long userId);</code>
	 * @param companyId
	 * @param loginName
	 * @return
	 */
	public List<Department> getDepartments(String loginName);
	/**
	 * 根据用户登录名查询用户所在的部门。
	 * 该方法不适用于多分支机构，当分支之间存在同名用户时该方法会有问题，请调用<code>List<String> getDepartmentNames(Long userId);</code>
	 * @param companyId
	 * @param loginName
	 * @return
	 */
	public List<String> getDepartmentNames(String loginName);
	/**
	 * 根据用户id查询用户所在的部门
	 * @param userId
	 * @return
	 */
	public List<Department> getDepartments(Long userId);
	/**
	 * 根据用户id查询用户所在的部门名称
	 * @param userId
	 * @return
	 */
	public List<String> getDepartmentNames(Long userId);
	/**
	 * 根据用户id查询用户所在的部门id
	 * @param userId
	 * @return
	 */
	public List<Long> getDepartmentIds(Long userId);
	/**
	 * 根据公司ID和用户的登录名查询该用户所具有的角色的字符串表示
	 * 替换为<code>Set<Role> getRolesByUser(String loginName);</code>
	 * @param companyId
	 * @param loginName
	 * @return
	 */
	@Deprecated
	public Set<Role> getRolesByUser(Long companyId, String loginName);
	
	/**
	 * 根据用户的登录名查询该用户所具有的角色的字符串表示。
	 * 该方法不适用于多分支机构，当分支之间存在同名用户时该方法会有问题，请调用<code>Set<Role> getRolesByUser(Long userId);</code>
	 * @param companyId
	 * @param loginName
	 * @return
	 */
	public Set<Role> getRolesByUser(String loginName);
	/**
	 * 查询用户所有的角色
	 * @param userId
	 * @return
	 */
	public Set<Role> getRolesByUser(Long userId);
	/**
	 * 查询用户所有的角色名称
	 * @param userId
	 * @return
	 */
	public Set<String> getRoleNamesByUser(Long userId);
	/**
	 * 查询用户所有的角色编码
	 * @param userId
	 * @return
	 */
	public Set<String> getRoleCodesByUser(Long userId);
	/**
	 * 查询用户所有的角色id
	 * @param userId
	 * @return
	 */
	public Set<Long> getRoleIdsByUser(Long userId);
	/**
	 * 根据公司id和用户登录名查询该用户所在的工作组。
	 * 替换为<code>List<Workgroup> getWorkgroupsByUser(String loginName);</code>
	 * @param companyId
	 * @param loginName
	 * @return
	 */
	@Deprecated
	public List<Workgroup> getWorkGroupsByUser(Long companyId, String loginName);
	
	/**
	 * 根据用户登录名查询该用户所在的工作组。
	 * 该方法不适用于多分支机构，当分支之间存在同名用户时该方法会有问题，请调用<code>List<Workgroup> getWorkgroupsByUser(Long userId);</code>
	 * @param loginName
	 * @return
	 */
	public List<Workgroup> getWorkgroupsByUser(String loginName);
	/**
	 * 根据用户登录名查询该用户所在的工作组
	 * @param userId
	 * @return
	 */
	public List<Workgroup> getWorkgroupsByUser(Long userId);
	/**
	 * 根据用户登录名查询该用户所在的工作组名称
	 * @param userId
	 * @return
	 */
	public Set<String> getWorkgroupNamesByUser(Long userId);
	/**
	 * 根据用户登录名查询该用户所在的工作组id
	 * @param userId
	 * @return
	 */
	public Set<Long> getWorkgroupIdsByUser(Long userId);
	
	/**
	 * 根据公司ID和用户登录名查询该用户所在的工作组
	 * 替换为<code>List<Workgroup> getWorkGroupsByUserLike(String name);</code>
	 * @param companyId
	 * @param name
	 * @return
	 */
	@Deprecated
	public List<Workgroup> getWorkGroupsByUserLike(Long companyId, String name);
	/**
	 * 根据用户登录名查询该用户所在的工作组
	 * @param name
	 * @return
	 */
	public List<Workgroup> getWorkGroupsByUserLike(String name);
	
	/**
	 * 根据登录名获得用户列表
	 * 如：flex中查询流转历史需要使用
	 */
	public List<User> getUsersByLoginNames(Long companyId, List<String> loginNames);
	/**
	 * 根据用户id获得用户列表
	 * 如：flex中查询流转历史需要使用
	 */
	public List<User> getUsersByIds(Long companyId, List<Long> userIds);
	/**
	 * 根据用户登录名集合获取用户集合
	 * 该方法不适用于多分支机构，当分支之间存在同登录名用户时该方法会有问题，请调用<code>List<User> getUsersByIds(Long companyId,List<String> loginNames);</code>
	 * @param loginNames
	 * @return
	 */
	public List<User> getUsersByLoginNames(List<String> loginNames);
	/**
	 * 根据登录名查询用户姓名
	 * 该方法不适用于多分支机构，当分支之间存在同登录名用户时该方法会有问题，请调用<code>Set<String> getUserNamesByIds(Collection<Long> userIds);</code>
	 * @param loginNames
	 * @return
	 */
	public Set<String> getUserNamesByLoginNames(Collection<String> loginNames);
	/**
	 * 根据登录名查询用户id
	 * 该方法不适用于多分支机构，当分支之间存在同登录名用户时该方法会有问题
	 * @param loginNames
	 * @return
	 */
	public Set<Long> getUserIdsByLoginNames(Collection<String> loginNames);
	/**
	 * 根据用户id查询用户姓名
	 * @param userIds
	 * @return
	 */
	public Set<String> getUserNamesByIds(Collection<Long> userIds);
	/**
	 * 根据用户id查询用户登录名
	 * @param userIds
	 * @return
	 */
	public Set<String> getLoginNamesByIds(Collection<Long> userIds);
	/**
	 * 根据用户id查询角色(不含委托)
	 * 替换为<code>List<Role> getRolesExcludeTrustedRole(Long userId)</code>
	 * @param userId
	 * @return
	 */
	@Deprecated
	public List<Role> getRolesListByUserExceptDelegateMain(Long userId);
	
	/**
	 * 根据用户查询角色(不含委托)
	 * 替换为<code>List<Role> getRolesExcludeTrustedRole(User user)</code>
	 * @param user
	 * @return
	 */
	@Deprecated
	public List<Role> getRolesListByUserExceptDelegateMain(com.norteksoft.acs.entity.organization.User user);
	/**
	 * 根据用户查询角色(不含委托)
	 *  替换为<code>List<Role> getRolesExcludeTrustedRole(User user)</code>
	 * @param user
	 * @return
	 */
	@Deprecated
	public List<Role> getRolesListByUserExceptDelegateMain(User user);
	/**
	 * 替换为<code>List<Role> getRolesExcludeTrustedRole(User user)</code>
	 * @param user
	 * @return
	 */
	@Deprecated
	public List<Role> getRolesListByUser(com.norteksoft.acs.entity.organization.User user);
	/**
	 * 替换为<code>List<Role> getRolesExcludeTrustedRole(User user)</code>
	 * @param user
	 * @return
	 */
	@Deprecated
	public List<Role> getRolesListByUser(User user);
	
	/**
	 * 根据用户id查询角色(不含委托)
	 * @param user
	 * @return
	 */
	public List<Role> getRolesExcludeTrustedRole(Long userId);
	/**
	 * 根据用户登录名查询用户所在的部门的上级部门
	 * 替换为<code>List<Department> getParentDepartmentsByUser(String loginName);</code>
	 * @param companyId
	 * @param loginName
	 * @return
	 */
	@Deprecated
	public List<Department> getSuperiorDepartmentsByUser(Long companyId, String loginName);
	
	/**
	 * 根据用户登录名查询用户所在的部门的上级部门
	 * 该方法不适用于多分支机构，当分支之间存在同名用户时该方法会有问题，请调用<code>List<Department> getParentDepartmentsByUser(Long userId);</code>
	 * @param loginName
	 * @return
	 */
	public List<Department> getParentDepartmentsByUser(String loginName);
	/**
	 * 根据用户登录名查询用户所在的部门的上级部门
	 * 该方法不适用于多分支机构，当分支之间存在同名用户时该方法会有问题，请调用<code>List<String> getParentDepartmentNamesByUser(Long userId);</code>
	 * @param loginName
	 * @return
	 */
	public List<String> getParentDepartmentNamesByUser(String loginName);
	/**
	 * 根据用户id查询用户所在的部门的上级部门。
	 * @param loginName
	 * @return
	 */
	public List<Department> getParentDepartmentsByUser(Long userId);
	/**
	 * 根据用户id查询用户所在的部门的上级部门。
	 * @param loginName
	 * @return
	 */
	public List<Long> getParentDepartmentIdsByUser(Long userId);
	/**
	 * 根据用户id查询用户所在的部门的上级部门。
	 * @param loginName
	 * @return
	 */
	public List<String> getParentDepartmentNamesByUser(Long userId);
	/**
	 * 获得用户的顶级部门
	 * 替换为<code>List<Department> getTopDepartmentsByUser(String loginName);</code>
	 * @param companyId
	 * @param loginName
	 * @return
	 */
	@Deprecated
	public List<Department> getUpstageDepartmentsByUser(Long companyId, String loginName);
	
	/**
	 * 获得用户的顶级部门
	 * 该方法不适用于多分支机构，当分支之间存在同名用户时该方法会有问题，请调用<code>List<Department> getTopDepartmentsByUser(Long userId);</code>
	 * @param companyId
	 * @param loginName
	 * @return
	 */
	public List<Department> getTopDepartmentsByUser(String loginName);
	/**
	 * 获得用户的顶级部门名称
	 * 该方法不适用于多分支机构，当分支之间存在同名用户时该方法会有问题，请调用<code>List<String> getTopDepartmentNamesByUser(Long userId);</code>
	 * @param companyId
	 * @param loginName
	 * @return
	 */
	public List<String> getTopDepartmentNamesByUser(String loginName);
	/**
	 * 获得用户的顶级部门名称
	 * @param companyId
	 * @param userId
	 * @return
	 */
	public List<Department> getTopDepartmentsByUser(Long userId);
	/**
	 * 获得用户的顶级部门名称
	 * @param companyId
	 * @param userId
	 * @return
	 */
	public List<String> getTopDepartmentNamesByUser(Long userId);
	/**
	 * 获得用户的顶级部门id
	 * @param companyId
	 * @param userId
	 * @return
	 */
	public List<Long> getTopDepartmentIdsByUser(Long userId);
	
	/**
	 * 根据用户姓名获得用户的顶级部门
	 * @param companyId
	 * @param loginName
	 * @return
	 */
	@Deprecated
	public List<Department> getUpstageDepartmentsByUserLike(Long companyId, String loginName);
	/**
	 * 返回该部门的一级部门
	 * 替换为<code>Department getTopDepartment(Department department);</code>
	 * @param department
	 * @return
	 */
	@Deprecated
	public Department getFirstDegreeDepartment(Department department);
	
	/**
	 * 返回该部门的一级部门
	 * @param department
	 * @return
	 */
	public Department getTopDepartment(Department department);
	/**
	 * 根据系统编码获得系统
	 * @param code
	 * @return
	 */
	public BusinessSystem getSystemByCode(String code);
	/**
	 * 根据系统id获得系统
	 * @param code
	 * @return
	 */
	public BusinessSystem getSystemById(Long id);
	/**
	 * 保存用户
	 * 替换为<code>saveUser(User user);</code>
	 * @param user
	 */
	@Deprecated
	public void saveUser(com.norteksoft.acs.entity.organization.User user,UserInfo userInfo);
	/**
	 * 保存用户
	 * @param user
	 */
	public void saveUser(User user);
	
	/**
	 * 删除用户
	 * @param userId
	 */
	public void deleteUser(Long userId);
	/**
	 * 保存部门
	 * @param department
	 */
	public void saveDepartment(Department department,Long companyId);
	/**
	 * 替换为<code>saveDepartment(Department department);</code>
	 * @param department
	 * @param companyId
	 */
	@Deprecated
	public void saveDepartment(com.norteksoft.acs.entity.organization.Department department,Long companyId);
	/**
	 * 保存部门
	 * @param department
	 */
	public void saveDepartment(Department department);
	/**
	 * 替换为<code>saveDepartment(Department department);</code>
	 * @param department
	 */
	@Deprecated
	public void saveDepartment(com.norteksoft.acs.entity.organization.Department department);
	/**
	 * 删除部门
	 * @param departmentId
	 */
	public void deleteDepartment(Long departmentId);
	/**
	 * 保存用户部门关系
	 * @param userIds 用户User的id的集合
	 * @param department 部门
	 */
	public void saveDepartmentUser(List<Long> userIds,Department department);
	/**
	 * 替换为<code>saveDepartmentUser(List<Long> userIds,Department department);</code>
	 * @param userIds
	 * @param department
	 */
	@Deprecated
	public void saveDepartmentUser(List<Long> userIds,com.norteksoft.acs.entity.organization.Department department);
	/**
	 * 查询当前用户的角色（不含委托）
	 * @return
	 */
	public String getCurrentUserRolesExcludeTrustedRole();
	/**
	 * 查询指定用户的角色（不含委托）
	 * @return
	 */
	public String getUserRolesExcludeTrustedRole(Long userId);
	/**
	 * 获得该公司的系统管理员（默认的系统管理员systemAdmin）
	 * @param companyId
	 * @return
	 */
	public String getSystemAdminLoginName();
	/**
	 * 获得该公司的系统管理员（默认的系统管理员systemAdmin）
	 * @param companyId
	 * @return
	 */
	public User getSystemAdmin();
	/**
	 * 查询公司中所有人员登录名（不包含无部门人员）
	 * 当存在多分支机构时该方法获得的登录名可能会有问题，因为不同分支之间可以存在同登录名用户
	 * @param companyId
	 * @return
	 */
	public List<String> getLoginNamesByCompany(Long companyId);
	/**
	 * 查询工作组所有人员登录名。
	 * 当存在多分支机构时该方法获得的登录名可能会有问题，因为不同分支之间可以存在同登录名用户
	 * @param companyId
	 * @return
	 */
	public List<String> getLoginNamesByWorkgroup(Long companyId);
	/**
	 * 查询所有工作组中的人员id集合
	 * @param companyId
	 * @return
	 */
	public List<Long> getUserIdsWithWorkgroup();
	/**
	 * 查询所有部门中的人员id集合
	 * @return
	 */
	public List<Long> getUserIdsWithDepartment();
	/**
	 * 查询当前工作组中的人员id集合
	 * @param companyId
	 * @return
	 */
	public List<Long> getUserIdsByWorkgroup(Long workgroupId);
	/**
	 * 获得父部门
	 * @param departmentId
	 * @return
	 */
	public Department getParentDepartment(Long departmentId);
	
	/**
	 * 根据用户id查询该用户所在的工作组
	 * @param userId
	 * @return
	 */
	public List<Workgroup> getWorkgroupsByUserId(Long userId);
	/**
	 * 根据用户id查询用户所在的部门的上级部门
	 * @param userId
	 * @return
	 */
	public List<Department> getParentDepartmentsByUserId(Long userId);
	/**
	 * 通过角色id查询所有的用户（不含委托）
	 * @param systemId
	 * @param roleId
	 * @return
	 */
	public Set<User> getUsersByRoleIdExceptTrustedRole(Long roleId);
	
	/**
	 * 返回该部门的一级部门
	 * @param departmentId
	 * @return
	 */
	public Department getTopDepartment(Long departmentId);
	
	/**
	 * 查询公司中所有人员登录名（包含无部门人员）
	 * @param companyId
	 * @return
	 */
	public List<Long> getAllUserIdsByCompany(Long companyId);
	/**
	 * 查询公司所有部门(不包含分支机构)
	 * @return
	 */
	public List<Department> getAllDepartments();
	/**
	 * 查询公司所有部门id(包含分支机构)
	 * @return
	 */
	public List<Long> getAllDepartmentIds();
	/**
	 * 根据公司编号(分支机构编号、集团公司编号)获得用户
	 * @return
	 */
	public List<User> getUsersByCompanyCode(String companyCode);
	/**
	 * 根据部门简称查询部门
	 * @param shortTitle 部门简称
	 * @return
	 */
	public Department getDepartmentByShortTitle(String shortTitle);
	/**
	 * 查询公司中所有人员id（包含无部门人员,但不包含三员systemAdmin、securityAdmin、auditAdmin）
	 * @return
	 */
	public List<Long> getAllUserIdsWithoutAdminByCompany();
	/**
	 * 查询在同一部门的所有用户
	 * @param userId
	 * @return
	 */
	public List<User> getUsersInSameDept(Long userId);
	/**
	 * 查询含有工作组的所有分支机构
	 * @param userId
	 * @return
	 */
	public List<Department> getDepartmentIfHasWorkGroup();
	/**
	 * 模糊查询用户
	 * @param searchValue
	 * @return List<User>
	 */
	public List<User> getUsersBySearchValue(String searchValue);
	/**
	 * 模糊查询工作组
	 * @param searchValue
	 * @return List<Workgroup>
	 */
	public List<Workgroup> getWorkGroupsBySearchValue(String searchValue);
	/**
	 * 模糊查询部门
	 * @param searchValue
	 * @return List<Department>
	 */
	public List<Department> getDepartmentsBySearchValue(String searchValue);
	/**
	 * 当前用户是否具有某角色
	 * @param roleId
	 * @return true表示有该角色，false表示没有
	 */
	public boolean isUserHasRole( Long roleId);
	/**
	 * 获得department中的用户个数
	 * @param department
	 * @return
	 */
	public long getUserCountInDepartment(Department department);
	/**
	 * 获得department中的子部门个数
	 * @param department
	 * @return
	 */
	public long getChildDepartmentCount(Department department);
}
