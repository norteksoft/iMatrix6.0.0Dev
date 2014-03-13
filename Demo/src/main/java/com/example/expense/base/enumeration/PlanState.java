package com.example.expense.base.enumeration;

public enum PlanState {
	NOTSTART("plan.notstart"),//未开始
	START("plan.start"),//开始
	FINISH("plan.finish");//已完成
	String code;
	private PlanState(String code) {
		this.code=code;
	}
	public String getCode() {
		return code;
	}

}
