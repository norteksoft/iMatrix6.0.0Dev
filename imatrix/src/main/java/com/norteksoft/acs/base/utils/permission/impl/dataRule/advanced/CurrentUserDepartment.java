package com.norteksoft.acs.base.utils.permission.impl.dataRule.advanced;

import java.util.HashSet;
import java.util.List;
import java.util.Set;


import com.norteksoft.acs.base.utils.PermissionUtils;
import com.norteksoft.acs.entity.authority.PermissionInfo;

import com.norteksoft.acs.entity.authority.PermissionItem;

/**
 * 当前用户部门相关处理
 * @author Administrator
 *
 */
public class CurrentUserDepartment implements DataRuleConditionValueSetting{
	/**
	 * 数据分类中条件值中的标准值:当前用户部门
	 * @author Administrator
	 *
	 */
	public ConditionVlaueInfo getValues(String conditionValue,List<PermissionItem> permissionItems,PermissionInfo permissionInfo) {
		String value="";
		Set<Long> result = new HashSet<Long>();
		if(permissionItems.size()<=0){//数据分类api解析
			List<Long> deptIds = permissionInfo.getDepartmentIds();
			if(!deptIds.isEmpty())result.addAll(deptIds);//去除重复的部门id
			value = result.toString().replace("[", "").replace("]", "").replace(" ", "");
		}else{//数据授权对应的数据分类解析
			Set<Long> deptIds = PermissionUtils.getDepartmentIds(permissionItems,permissionInfo);
			value = deptIds.toString().replace("[", "").replace("]", "").replace(" ", "");
		}
		return new ConditionVlaueInfo(DataRuleConditionValueType.STANDARD_VALUE,value);
	}

}
