package com.norteksoft.acs.base.utils.permission.impl.dataRule.advanced;

import java.io.Serializable;

public class ConditionVlaueInfo implements Serializable{
	private static final long serialVersionUID = 1L;
	private String value;
	private DataRuleConditionValueType valueType;//值的类型:标准值、非标准值
	public ConditionVlaueInfo(DataRuleConditionValueType valueType,String value) {
		this.valueType = valueType;
		this.value = value;
	}
	public String getValue() {
		return value;
	}
	public void setValue(String value) {
		this.value = value;
	}
	public DataRuleConditionValueType getValueType() {
		return valueType;
	}
	public void setValueType(DataRuleConditionValueType valueType) {
		this.valueType = valueType;
	}
	
	
}
