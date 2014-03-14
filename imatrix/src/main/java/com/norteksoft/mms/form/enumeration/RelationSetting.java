package com.norteksoft.mms.form.enumeration;
/**
 * 关系配置
 * @author Administrator
 *
 */
public enum RelationSetting {
	/**
	 * 一对一
	 */
	ONE_TO_ONE("relation.setting.oneToOne"),
	/**
	 * 多对一
	 */
	MANY_TO_ONE("relation.setting.manyToOne"),
	/**
	 * 嵌入
	 */
	IMPLANT("relation.setting.implant");
	
	public String code;
	
	RelationSetting(String code){
		this.code=code;
	}
	
	public Short getIndex(){
		return (short)(this.ordinal()+1);
	}
	
	public String getCode(){
		return this.code;
	}
	
	/**
	 * 返回枚举的名称
	 * @return
	 */
	public String getEnumName(){
		return this.toString();
	}
}
