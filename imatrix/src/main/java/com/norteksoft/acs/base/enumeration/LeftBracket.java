package com.norteksoft.acs.base.enumeration;

/**
 * 左括号
 */
public enum LeftBracket {
	/**
	 * 单层括号
	 */
	LEFTSINGLE("bracket.left.single"),
	/**
	 * 双层括号
	 */
	LEFTDOUBLE("bracket.left.double");
	public String code;
	LeftBracket(String code){
		this.code=code;
	}
	public Short getIndex(){
		return (short)(this.ordinal()+1);
	}
	public String getCode(){
		return this.code;
	}
}
