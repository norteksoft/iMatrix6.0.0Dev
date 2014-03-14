package com.norteksoft.acs.base.utils.permission.impl.dataRule.advanced;

import java.util.List;

import com.norteksoft.acs.entity.authority.PermissionInfo;
import com.norteksoft.acs.entity.authority.PermissionItem;

public interface DataRuleConditionValueSetting {
	/**
	 * 根据数据分类中设置的条件值获得相应的值，以逗号隔开
	 * @param conditionValue  数据分类中设置的条件值
	 * @return
	 */
	public ConditionVlaueInfo getValues(String conditionValue,List<PermissionItem> permissionItems,PermissionInfo permissionInfo);

}
