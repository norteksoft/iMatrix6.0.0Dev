package com.norteksoft.acs.base.utils.permission.impl.dataRule.advanced;

import java.util.List;


import com.norteksoft.acs.entity.authority.PermissionInfo;

import com.norteksoft.acs.entity.authority.PermissionItem;
import com.norteksoft.product.api.ApiFactory;
import com.norteksoft.product.api.entity.Role;
import com.norteksoft.product.api.entity.User;

/**
 * 当前用户直属上级所具有的角色的相关处理
 * @author Administrator
 *
 */
public class CurrentUserDirectSuperiorRole implements DataRuleConditionValueSetting{
	/**
	 * 数据分类中条件值中的标准值:当前用户直属上级的角色
	 * @author Administrator
	 *
	 */
	public ConditionVlaueInfo getValues(String conditionValue,List<PermissionItem> permissionItems,PermissionInfo permissionInfo) {
		Long userId = permissionInfo.getUserId();
		User user = ApiFactory.getDataDictService().getDirectLeader(userId);
		if(user==null){
			return new ConditionVlaueInfo(DataRuleConditionValueType.STANDARD_VALUE,"");
		}else{
			String value="";
			List<Role> roles = ApiFactory.getAcsService().getRolesExcludeTrustedRole(user.getId());
			for (Role r : roles) {
				value = value+ r.getId()+",";
			}
			if(value.indexOf(",")>=0)value = value.substring(0,value.lastIndexOf(","));
			return new ConditionVlaueInfo(DataRuleConditionValueType.STANDARD_VALUE,value);
		}
	}


}
