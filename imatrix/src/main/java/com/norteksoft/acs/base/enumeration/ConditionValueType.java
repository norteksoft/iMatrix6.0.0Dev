package com.norteksoft.acs.base.enumeration;

public enum ConditionValueType {
	/**
	 *	数据授权条件
	 */
	PERMISSION("permission.data.type.permission"),
	
	/**
	 * 数据分类条件
	 */
	DATA_RULE("permission.data.type.dataRule")
	
	;
	public String code;
	ConditionValueType(String code){
		this.code=code;
	}
	public Short getIndex(){
		return (short)(this.ordinal()+1);
	}
	public String getCode(){
		return this.code;
	}
}
