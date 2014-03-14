package com.norteksoft.acs.base.enumeration;

public enum ConditionType {
	/**
	 * 当前用户id
	 */
	CURRENT_USER_ID("permission.item.type.currentUserId"),
	
	/**
	 * 当前用户部门
	 */
	CURRENT_USER_DEPARTMENT("permission.item.type.currentUserDepartment"),
	
	/**
	 * 当前用户角色
	 */
	CURRENT_USER_ROLE("permission.item.type.currentUserRole"),
	
	/**
	 * 当前用户工作组
	 */
	CURRENT_USER_WORKGROUP("permission.item.type.currentUserWorkgroup"),
	
	/**
	 * 当前用户上级部门
	 */
	CURRENT_USER_SUPERIOR_DEPARTMENT("permission.item.type.currentUserSuperiorDepartment"),
	
	/**
	 * 当前用户顶级部门
	 */
	CURRENT_USER_TOP_DEPARTMENT("permission.item.type.currentUserTopDepartment"),
	
	/**
	 * 直属上级id
	 */
	CURRENT_USER_DIRECT_SUPERIOR_ID("permission.item.type.currentUserDirectSuperiorId"),
	/**
	 * 直属上级部门
	 */
	CURRENT_USER_DIRECT_SUPERIOR_DEPARTMENT("permission.item.type.currentUserDirectSuperiorDepartment"),
	/**
	 * 直属上级角色
	 */
	CURRENT_USER_DIRECT_SUPERIOR_ROLE("permission.item.type.currentUserDirectSuperiorRole"),
	/**
	 * 直属上级工作组
	 */
	CURRENT_USER_DIRECT_SUPERIOR_WORKGROUP("permission.item.type.currentUserDirectSuperiorWorkgroup"),
	/**
	 * 直属下级id
	 */
	CURRENT_USER_DIRECT_LOWER_ID("permission.item.type.currentUserDirectLowerId"),
	/**
	 * 直属下级部门
	 */
	CURRENT_USER_DIRECT_LOWER_DEPARTMENT("permission.item.type.currentUserDirectLowerDepartment"),
	/**
	 * 当前用户所在部门的子部门
	 */
	CURRENT_USER_CHILD_DEPARTMENT("permission.item.type.currentUserChildDepartment"),
	/**
	 * 当前用户所在部门的子部门(含继承关系)
	 */
	CURRENT_USER_CHILDREN_DEPARTMENT("permission.item.type.currentUserChildrenDepartment"),
	/**
	 * 直属下级的角色
	 */
	CURRENT_USER_DIRECT_LOWER_ROLE("permission.item.type.currentUserDirectLowerRole"),
	/**
	 * 直属下级的工作组
	 */
	CURRENT_USER_DIRECT_LOWER_WORKGROUP("permission.item.type.currentUserDirectLowerWorkgroup");
	public String code;
	ConditionType(String code){
		this.code=code;
	}
	public Short getIndex(){
		return (short)(this.ordinal()+1);
	}
	public String getCode(){
		return this.code;
	}
}
