package com.norteksoft.acs.entity.authority;

import java.io.Serializable;
import java.util.List;

/**
 * 快速授权中人员实体,用于解析json字符串用
 * @author Administrator
 *
 */
public class PermissionUser implements Serializable{
	
	private static final long serialVersionUID = 1L;
	
	private String itemType;
	private String conditionName;
	private String conditionValue;
	private List<PermissionUser> permissionValues;
	public String getItemType() {
		return itemType;
	}
	public void setItemType(String itemType) {
		this.itemType = itemType;
	}
	public List<PermissionUser> getPermissionValues() {
		return permissionValues;
	}
	public void setPermissionValues(List<PermissionUser> permissionValues) {
		this.permissionValues = permissionValues;
	}
	public String getConditionName() {
		return conditionName;
	}
	public void setConditionName(String conditionName) {
		this.conditionName = conditionName;
	}
	public String getConditionValue() {
		return conditionValue;
	}
	public void setConditionValue(String conditionValue) {
		this.conditionValue = conditionValue;
	}

}
