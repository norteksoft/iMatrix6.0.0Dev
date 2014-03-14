package com.norteksoft.acs.base.enumeration;

public enum DataRange {
	/**
	 * 本人
	 */
	MYSELF("data.range.myself"),
	/**
	 * 本部门
	 */
	CURRENT_DEPARTMENT("data.range.current.department"),
	/**
	 * 所有数据
	 */
	ALL("data.range.all");
	
	public String code;
	DataRange(String code){
		this.code=code;
	}
	public Short getIndex(){
		return (short)(this.ordinal()+1);
	}
	public String getCode(){
		return this.code;
	}
}
