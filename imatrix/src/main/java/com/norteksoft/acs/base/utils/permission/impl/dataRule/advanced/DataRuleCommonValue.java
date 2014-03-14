package com.norteksoft.acs.base.utils.permission.impl.dataRule.advanced;

import java.util.List;

import com.norteksoft.acs.entity.authority.PermissionInfo;

import com.norteksoft.acs.entity.authority.PermissionItem;


/**
 * 数据分类中条件值中的非标准值
 * @author Administrator
 *
 */
public class DataRuleCommonValue implements DataRuleConditionValueSetting{

	public ConditionVlaueInfo getValues(String conditionValue,List<PermissionItem> permissionItems,PermissionInfo permissionInfo) {
		return new ConditionVlaueInfo(DataRuleConditionValueType.CUSTOM_VALUE,conditionValue);
	}

}
