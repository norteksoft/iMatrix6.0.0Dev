package com.norteksoft.acs.base.enumeration;

/**
 * 左括号
 */
public enum RightBracket {
	/**
	 * 单层括号
	 */
	RIGHTSINGLE("bracket.right.single"),
	/**
	 * 双层括号
	 */
	RIGHTDOUBLE("bracket.right.double");
	public String code;
	RightBracket(String code){
		this.code=code;
	}
	public Short getIndex(){
		return (short)(this.ordinal()+1);
	}
	public String getCode(){
		return this.code;
	}
}
