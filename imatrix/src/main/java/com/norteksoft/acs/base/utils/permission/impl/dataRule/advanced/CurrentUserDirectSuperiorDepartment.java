package com.norteksoft.acs.base.utils.permission.impl.dataRule.advanced;

import java.util.HashSet;
import java.util.List;
import java.util.Set;


import com.norteksoft.acs.entity.authority.PermissionInfo;

import com.norteksoft.acs.entity.authority.PermissionItem;
import com.norteksoft.product.api.ApiFactory;
import com.norteksoft.product.api.entity.User;
import com.norteksoft.product.api.impl.AcsServiceImpl;
import com.norteksoft.product.util.ContextUtils;

/**
 * 当前用户直属上级的部门的相关处理
 * @author Administrator
 *
 */
public class CurrentUserDirectSuperiorDepartment implements DataRuleConditionValueSetting{
	/**
	 * 数据分类中条件值中的标准值:当前用户直属上级的部门
	 * @author Administrator
	 *
	 */
	public ConditionVlaueInfo getValues(String conditionValue,List<PermissionItem> permissionItems,PermissionInfo permissionInfo) {
		Long userId = permissionInfo.getUserId();
		Set<Long> result = new HashSet<Long>();
		User user = ApiFactory.getDataDictService().getDirectLeader(userId);
		if(user==null){
			return new ConditionVlaueInfo(DataRuleConditionValueType.STANDARD_VALUE,"");
		}else{
			String value="";
			AcsServiceImpl acsService = (AcsServiceImpl)ContextUtils.getBean("acsServiceImpl");
			List<Long> deptIds = acsService.getDepartmentIds(user.getId());
			 if(!deptIds.isEmpty())result.addAll(deptIds);//去除重复的部门id
			 value = result.toString().replace("[", "").replace("]", "").replace(" ", "");
			return new ConditionVlaueInfo(DataRuleConditionValueType.STANDARD_VALUE,value);
		}
	}

}
