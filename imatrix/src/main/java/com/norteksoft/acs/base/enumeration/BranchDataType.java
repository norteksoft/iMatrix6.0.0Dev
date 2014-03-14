package com.norteksoft.acs.base.enumeration;
/**
 * 分支机构授权管理中的数据类型
 * @author Administrator
 *
 */
public enum BranchDataType {
	USER("branch.data.type.user"), // 用户
	ROLE("branch.data.type.role"); // 角色
	
	private String code;
	
	BranchDataType(String code){
		this.code = code;
	}
	
	public String getCode() {
		return code;
	}
}
