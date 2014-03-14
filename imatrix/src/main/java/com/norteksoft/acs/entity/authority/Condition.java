package com.norteksoft.acs.entity.authority;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.apache.commons.lang.StringUtils;

import com.norteksoft.acs.base.enumeration.ConditionValueType;
import com.norteksoft.acs.base.enumeration.FieldOperator;
import com.norteksoft.acs.base.enumeration.LeftBracket;
import com.norteksoft.acs.base.enumeration.LogicOperator;
import com.norteksoft.acs.base.enumeration.RightBracket;
import com.norteksoft.acs.service.authority.PermissionItemManager;
import com.norteksoft.mms.form.enumeration.DataType;
import com.norteksoft.product.orm.IdEntity;
import com.norteksoft.product.util.ContextUtils;
/**
 * 数据规则条件
 * @author Administrator
 *
 */
@Entity
@Table(name="ACS_CONDITION")
public class Condition extends IdEntity implements Serializable{
	private static final long serialVersionUID = 1L;
	
	private String fieldName;//字段名
	@Enumerated(EnumType.STRING)
	private FieldOperator operator;//比较符号
	@Transient
	private String conditionValue;//条件值
	@Transient
	private String conditionName;//条件值对应的名称
	@Enumerated(EnumType.STRING)
	private LogicOperator lgicOperator;//条件连接类型
	private String field;//数据表字段;当是标准字段时，该值格式~~creatorId,~~departmentId,~~roleId,~~workgroupId
	@Enumerated(EnumType.STRING)
	private DataType dataType;//字段数据类型
	private String enumPath;//当dataType值为枚举类型时，该值有用
	private String keyValue;//保存枚举类型，选项组和key：Value形式的值设置
	
	@ManyToOne
	@JoinColumn(name="FK_DATA_RULE_ID")
	private DataRule dataRule;
//	private ConditionType relativeType;//条件为相对条件时，此字段有值，表示相对值
	
	private LeftBracket leftBracket;//左括号
	private RightBracket rightBracket;//右括号
	
	private Integer displayIndex;//显示顺序
	
	public String getFieldName() {
		return fieldName;
	}
	public void setFieldName(String fieldName) {
		this.fieldName = fieldName;
	}
	public FieldOperator getOperator() {
		return operator;
	}
	public void setOperator(FieldOperator operator) {
		this.operator = operator;
	}
	public String getConditionValue() {
		if(getId()==null)return null;
		if(StringUtils.isNotEmpty(conditionValue))return conditionValue;
		PermissionItemManager permissionItemManager = (PermissionItemManager)ContextUtils.getBean("permissionItemManager");
		return permissionItemManager.getPermissionItemConditionValueByItem(getId(),ConditionValueType.DATA_RULE).toString().replace("[", "").replace("]", "");
	}
	public void setConditionValue(String conditionValue) {
		this.conditionValue = conditionValue;
	}
	public String getConditionName() {
		if(getId()==null)return null;
		if(StringUtils.isNotEmpty(conditionName))return conditionName;
		PermissionItemManager permissionItemManager = (PermissionItemManager)ContextUtils.getBean("permissionItemManager");
		return permissionItemManager.getPermissionItemConditionNameByItem(getId(),ConditionValueType.DATA_RULE).toString().replace("[", "").replace("]", "");
	}
	public void setConditionName(String conditionName) {
		this.conditionName = conditionName;
	}
	public LogicOperator getLgicOperator() {
		return lgicOperator;
	}
	public void setLgicOperator(LogicOperator lgicOperator) {
		this.lgicOperator = lgicOperator;
	}
	public DataRule getDataRule() {
		return dataRule;
	}
	public void setDataRule(DataRule dataRule) {
		this.dataRule = dataRule;
	}
	public DataType getDataType() {
		return dataType;
	}
	public void setDataType(DataType dataType) {
		this.dataType = dataType;
	}
	public String getField() {
		return field;
	}
	public void setField(String field) {
		this.field = field;
	}
	public String getEnumPath() {
		return enumPath;
	}
	public void setEnumPath(String enumPath) {
		this.enumPath = enumPath;
	}
	public LeftBracket getLeftBracket() {
		return leftBracket;
	}
	public void setLeftBracket(LeftBracket leftBracket) {
		this.leftBracket = leftBracket;
	}
	public RightBracket getRightBracket() {
		return rightBracket;
	}
	public void setRightBracket(RightBracket rightBracket) {
		this.rightBracket = rightBracket;
	}
	public Integer getDisplayIndex() {
		return displayIndex;
	}
	public void setDisplayIndex(Integer displayIndex) {
		this.displayIndex = displayIndex;
	}
	public String getKeyValue() {
		return keyValue;
	}
	public void setKeyValue(String keyValue) {
		this.keyValue = keyValue;
	}
	@Override
	public boolean equals(Object obj) {
		if((Condition)obj!=null&&((Condition)obj).getId()!=null&&((Condition)obj).getId().equals(this.getId())){
			return true;
		}
		return false;
	}
	
}
