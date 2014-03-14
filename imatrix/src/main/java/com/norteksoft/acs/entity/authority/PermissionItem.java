package com.norteksoft.acs.entity.authority;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.apache.commons.lang.StringUtils;

import com.norteksoft.acs.base.enumeration.ConditionValueType;
import com.norteksoft.acs.base.enumeration.ItemType;
import com.norteksoft.acs.base.enumeration.LeftBracket;
import com.norteksoft.acs.base.enumeration.LogicOperator;
import com.norteksoft.acs.base.enumeration.RightBracket;
import com.norteksoft.acs.base.enumeration.UserOperator;
import com.norteksoft.acs.service.authority.PermissionItemManager;
import com.norteksoft.product.orm.IdEntity;
import com.norteksoft.product.util.ContextUtils;
/**
 * 授权条件
 * @author Administrator
 *
 */
@Entity
@Table(name="ACS_PERMISSION_ITEM")
public class PermissionItem  extends IdEntity implements Serializable{
	
	private static final long serialVersionUID = 1L;
	
	private ItemType itemType;
	private UserOperator operator;
	private LogicOperator joinType;
	@Transient
	private String conditionValue;
	@Transient
	private String conditionName;
	private Integer displayOrder;
	private LeftBracket leftBracket;//左括号
	private RightBracket rightBracket;//右括号
	@ManyToOne
	@JoinColumn(name="FK_PERMISSION_ID")
	private Permission permission;
	public ItemType getItemType() {
		return itemType;
	}
	public void setItemType(ItemType itemType) {
		this.itemType = itemType;
	}
	public UserOperator getOperator() {
		return operator;
	}
	public void setOperator(UserOperator operator) {
		this.operator = operator;
	}
	public LogicOperator getJoinType() {
		return joinType;
	}
	public void setJoinType(LogicOperator joinType) {
		this.joinType = joinType;
	}
	public String getConditionValue() {
		if(getId()==null)return null;
		if(StringUtils.isNotEmpty(conditionValue))return conditionValue;
		PermissionItemManager permissionItemManager = (PermissionItemManager)ContextUtils.getBean("permissionItemManager");
		return permissionItemManager.getPermissionItemConditionValueByItem(getId(),ConditionValueType.PERMISSION).toString().replace("[", "").replace("]", "").replace(" ","" );
	}
	public Permission getPermission() {
		return permission;
	}
	public void setPermission(Permission permission) {
		this.permission = permission;
	}
	public String getConditionName() {
		if(getId()==null)return null;
		if(StringUtils.isNotEmpty(conditionName))return conditionName;
		PermissionItemManager permissionItemManager = (PermissionItemManager)ContextUtils.getBean("permissionItemManager");
		return permissionItemManager.getPermissionItemConditionNameByItem(getId(),ConditionValueType.PERMISSION).toString().replace("[", "").replace("]", "").replace(" ","" );
	}
	public void setConditionValue(String conditionValue) {
		this.conditionValue = conditionValue;
	}
	public void setConditionName(String conditionName) {
		this.conditionName = conditionName;
	}
	public Integer getDisplayOrder() {
		return displayOrder;
	}
	public void setDisplayOrder(Integer displayOrder) {
		this.displayOrder = displayOrder;
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

}
