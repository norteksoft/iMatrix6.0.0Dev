package com.norteksoft.acs.entity.authority;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.Table;

import com.norteksoft.acs.base.enumeration.ConditionValueType;
import com.norteksoft.product.orm.IdEntity;
/**
 * 数据授权中授权人员值 或  数据分类的条件值的存储
 * 数据授权中授权人员：因为人员、部门、工作组等可以多选，所以需要新加表存储人员和人员条件的关系
 * 数据分类的条件值：因为当是标准字段创建人、部门、角色、工作组时可以选，所以需要新加表存储数据分类和分类条件的关系
 * @author Administrator
 *
 */
@Entity
@Table(name="ACS_PERMISSION_ITEM_CONDITION")
public class PermissionItemCondition  extends IdEntity implements Serializable{
	
	private static final long serialVersionUID = 1L;
	
	private Long dataId;//数据授权条件id（PermissionItem的id） 或 数据分类条件的id（Condition的id）
	private String conditionValue;//条件值
	private String conditionName;//条件值对应的名称，例如：条件值为人员id：100，条件值对应的名称为：张三
	private ConditionValueType valueType=ConditionValueType.DATA_RULE;//数据条件的类型：数据授权、数据分类
	public String getConditionValue() {
		return conditionValue;
	}
	public void setConditionValue(String conditionValue) {
		this.conditionValue = conditionValue;
	}
	public String getConditionName() {
		return conditionName;
	}
	public void setConditionName(String conditionName) {
		this.conditionName = conditionName;
	}
	public Long getDataId() {
		return dataId;
	}
	public void setDataId(Long dataId) {
		this.dataId = dataId;
	}
	public ConditionValueType getValueType() {
		return valueType;
	}
	public void setValueType(ConditionValueType valueType) {
		this.valueType = valueType;
	}
}
