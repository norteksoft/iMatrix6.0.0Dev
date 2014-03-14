package com.norteksoft.acs.entity.authority;

import java.io.Serializable;

import com.norteksoft.acs.entity.authority.DataRule;
import com.norteksoft.acs.entity.authority.Permission;

public class DataRuleResult implements Serializable{

	private static final long serialVersionUID = 1L;
	private DataRule dataRule;
	private Permission permission;
	public DataRuleResult(DataRule dataRule, Permission permission) {
		this.dataRule = dataRule;
		this.permission = permission;
	}
	public DataRuleResult(){}
	public DataRule getDataRule() {
		return dataRule;
	}
	public void setDataRule(DataRule dataRule) {
		this.dataRule = dataRule;
	}
	public Permission getPermission() {
		return permission;
	}
	public void setPermission(Permission permission) {
		this.permission = permission;
	}
	

}
