package com.norteksoft.acs.base.utils.permission.impl.dataRule.advanced;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.norteksoft.acs.entity.authority.PermissionInfo;

import com.norteksoft.acs.entity.authority.PermissionItem;
import com.norteksoft.product.api.ApiFactory;
import com.norteksoft.product.api.entity.User;
import com.norteksoft.product.api.entity.Workgroup;

/**
 * 数据分类中条件值中的标准值:当前用户直属下级的工作组
 * @author Administrator
 *
 */
public class CurrentUserDirectLowerWorkgroup implements DataRuleConditionValueSetting{

	public ConditionVlaueInfo getValues(String conditionValue,List<PermissionItem> permissionItems,PermissionInfo permissionInfo) {
		String value="";
		Long userId = permissionInfo.getUserId();
		List<User> users = ApiFactory.getDataDictService().getDirectLower(userId);
		Set<Workgroup> result = new HashSet<Workgroup>();
		for (User u : users) {
			List<Workgroup> wgs = ApiFactory.getAcsService().getWorkgroupsByUserId(u.getId());
			//去掉重复
			result.addAll(wgs);
		}
		for (Workgroup wg : result) {
			value = value+ wg.getId()+",";
		}
		if(value.indexOf(",")>=0)value = value.substring(0,value.lastIndexOf(","));
		return new ConditionVlaueInfo(DataRuleConditionValueType.STANDARD_VALUE,value);
	}


}
