package com.norteksoft.product.api.entity;

import java.io.Serializable;
import java.util.List;

public class SingleSearch implements Serializable{
	private static final long serialVersionUID = 1L; 
	
	private String condition;
	private List<Object> conditionValue;
	public String getCondition() {
		return condition;
	}
	public void setCondition(String condition) {
		this.condition = condition;
	}
	public List<Object> getConditionValue() {
		return conditionValue;
	}
	public void setConditionValue(List<Object> conditionValue) {
		this.conditionValue = conditionValue;
	}
}
