package com.norteksoft.acs.entity.authority;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * 解析数据授权api用到
 * @author Administrator
 *
 */
public class PermissionInfo implements Serializable{
	
	private static final long serialVersionUID = 1L;
	
	private boolean hasPermission;//当前用户是否在设置的人员范围内
	private List<DataRuleResult> rules = new ArrayList<DataRuleResult>();
	private Long userId;
	private Long directSuperiorId;
	private List<Long> departmentIds = new ArrayList<Long>();//当前用户所在的部门id的集合
	private Set<String> permissionUsers = new HashSet<String>();//有权限的人员集合
	private boolean noPermission=false;//是否是没有授权,没有授权时,不受权限控制
	
	public PermissionInfo(boolean hasPermission, List<DataRuleResult> rules,
			Long userId, Long directSuperiorId, List<Long> departmentIds,
			Set<String> permissionUsers,boolean noPermission) {
		super();
		this.hasPermission = hasPermission;
		this.rules = rules;
		this.userId = userId;
		this.directSuperiorId = directSuperiorId;
		this.departmentIds = departmentIds;
		this.permissionUsers = permissionUsers;
		this.noPermission = noPermission;
	}
	public PermissionInfo(Long userId, Long directSuperiorId, List<Long> departmentIds,List<DataRuleResult> rules){
		this.userId = userId;
		this.directSuperiorId = directSuperiorId;
		this.departmentIds = departmentIds;
		this.rules = rules;
	}
	public boolean isHasPermission() {
		return hasPermission;
	}
	public void setHasPermission(boolean hasPermission) {
		this.hasPermission = hasPermission;
	}
	public List<DataRuleResult> getRules() {
		return rules;
	}
	public void setRules(List<DataRuleResult> rules) {
		this.rules = rules;
	}
	public Long getUserId() {
		return userId;
	}
	public void setUserId(Long userId) {
		this.userId = userId;
	}
	public Long getDirectSuperiorId() {
		return directSuperiorId;
	}
	public void setDirectSuperiorId(Long directSuperiorId) {
		this.directSuperiorId = directSuperiorId;
	}
	public List<Long> getDepartmentIds() {
		return departmentIds;
	}
	public void setDepartmentIds(List<Long> departmentIds) {
		this.departmentIds = departmentIds;
	}
	public Set<String> getPermissionUsers() {
		return permissionUsers;
	}
	public void setPermissionUsers(Set<String> permissionUsers) {
		this.permissionUsers = permissionUsers;
	}
	public boolean isNoPermission() {
		return noPermission;
	}
	public void setNoPermission(boolean noPermission) {
		this.noPermission = noPermission;
	}

}
