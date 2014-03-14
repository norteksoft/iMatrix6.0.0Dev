package com.norteksoft.acs.base.utils.permission.impl.dataRule.advanced;

import java.util.List;


import com.norteksoft.acs.entity.authority.PermissionInfo;

import com.norteksoft.acs.entity.authority.PermissionItem;
import com.norteksoft.product.api.ApiFactory;
import com.norteksoft.product.api.entity.User;

public class CurrentUserDirectSuperior implements DataRuleConditionValueSetting{

	/**
	 * 数据分类中条件值中的标准值:当前用户直属上级
	 * @author Administrator
	 *
	 */
	public ConditionVlaueInfo getValues(String conditionValue,List<PermissionItem> permissionItems,PermissionInfo permissionInfo) {
		Long userId = permissionInfo.getUserId();
		User user = ApiFactory.getDataDictService().getDirectLeader(userId);
		if(user==null){
			return new ConditionVlaueInfo(DataRuleConditionValueType.STANDARD_VALUE,"");
		}else{
			return new ConditionVlaueInfo(DataRuleConditionValueType.STANDARD_VALUE,user.getId()+"");
		}
	}

}
