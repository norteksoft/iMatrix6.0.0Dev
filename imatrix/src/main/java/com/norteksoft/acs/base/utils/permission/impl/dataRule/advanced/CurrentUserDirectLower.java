package com.norteksoft.acs.base.utils.permission.impl.dataRule.advanced;

import java.util.List;

import com.norteksoft.acs.entity.authority.PermissionInfo;

import com.norteksoft.acs.entity.authority.PermissionItem;
import com.norteksoft.product.api.ApiFactory;
import com.norteksoft.product.api.entity.User;

/**
 * 数据分类中条件值中的标准值:当前用户直属下级
 * @author Administrator
 *
 */
public class CurrentUserDirectLower implements DataRuleConditionValueSetting{

	public ConditionVlaueInfo getValues(String conditionValue,List<PermissionItem> permissionItems,PermissionInfo permissionInfo) {
		String value="";
		Long userId = permissionInfo.getUserId();
		List<User> users = ApiFactory.getDataDictService().getDirectLower(userId);
		 for (User u : users) {
			value = value+ u.getId()+",";
		}
		if(value.indexOf(",")>=0)value = value.substring(0,value.lastIndexOf(","));
		return new ConditionVlaueInfo(DataRuleConditionValueType.STANDARD_VALUE,value);
	}


}
