package com.norteksoft.acs.base.utils.permission.impl.dataRule.advanced;

import java.util.HashSet;
import java.util.List;
import java.util.Set;


import com.norteksoft.acs.entity.authority.PermissionInfo;
import com.norteksoft.acs.entity.authority.PermissionItem;
import com.norteksoft.product.api.impl.AcsServiceImpl;
import com.norteksoft.product.util.ContextUtils;

/**
 * 所有用户(ALL_USER)的相关处理
 * @author Administrator
 *
 */
public class AllUserValue implements DataRuleConditionValueSetting{
	/**
	 * 数据分类中条件值中的值:所有用户(ALL_USER)
	 * @author Administrator
	 *
	 */
	public ConditionVlaueInfo getValues(String conditionValue,List<PermissionItem> permissionItems,PermissionInfo permissionInfo) {
		String value="";
		AcsServiceImpl acsService = (AcsServiceImpl)ContextUtils.getBean("acsServiceImpl");
		List<Long> userIds = acsService.getUserIdsByCompany(ContextUtils.getCompanyId());
		Set<Long> result = new HashSet<Long>();
		if(!userIds.isEmpty())result.addAll(userIds);//去除重复的用户id
		value = result.toString().replace("[", "").replace("]", "").replace(" ", "");
		return new ConditionVlaueInfo(DataRuleConditionValueType.STANDARD_VALUE,value);
	}
	
}
