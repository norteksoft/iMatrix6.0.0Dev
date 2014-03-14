package com.norteksoft.acs.base.utils.permission.impl.dataRule.advanced;

public enum DataRuleConditionValueType {
	/**
	 *	标准值
	 */
	STANDARD_VALUE("permission.data.rule.condition.value.type.standard"),
	
	/**
	 * 非标准值
	 */
	CUSTOM_VALUE("permission.data.rule.condition.value.type.custom");
	public String code;
	DataRuleConditionValueType(String code){
		this.code=code;
	}
	public Short getIndex(){
		return (short)(this.ordinal()+1);
	}
	public String getCode(){
		return this.code;
	}
}
