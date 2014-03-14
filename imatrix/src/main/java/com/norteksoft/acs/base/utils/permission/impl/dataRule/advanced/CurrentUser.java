package com.norteksoft.acs.base.utils.permission.impl.dataRule.advanced;

import java.util.List;


import com.norteksoft.acs.entity.authority.PermissionInfo;

import com.norteksoft.acs.entity.authority.PermissionItem;

/**
 * 当前用户的相关处理
 * @author Administrator
 *
 */
public class CurrentUser implements DataRuleConditionValueSetting{
	/**
	 * 数据分类中条件值中的标准值:当前用户
	 * @author Administrator
	 *
	 */
	public ConditionVlaueInfo getValues(String conditionValue,List<PermissionItem> permissionItems,PermissionInfo permissionInfo) {
		Long currentUserId = permissionInfo.getUserId();
		if(currentUserId==null){
			return new ConditionVlaueInfo(DataRuleConditionValueType.STANDARD_VALUE,"");
		}else{
			return new ConditionVlaueInfo(DataRuleConditionValueType.STANDARD_VALUE,currentUserId+"");
		}
	}
}
