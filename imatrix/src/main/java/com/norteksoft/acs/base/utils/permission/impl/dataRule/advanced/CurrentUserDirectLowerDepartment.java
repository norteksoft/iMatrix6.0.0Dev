package com.norteksoft.acs.base.utils.permission.impl.dataRule.advanced;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.norteksoft.acs.entity.authority.PermissionInfo;

import com.norteksoft.acs.entity.authority.PermissionItem;
import com.norteksoft.product.api.ApiFactory;
import com.norteksoft.product.api.entity.Department;
import com.norteksoft.product.api.entity.User;

/**
 * 数据分类中条件值中的标准值:当前用户直属下级的部门
 * @author Administrator
 *
 */
public class CurrentUserDirectLowerDepartment implements DataRuleConditionValueSetting{

	public ConditionVlaueInfo getValues(String conditionValue,List<PermissionItem> permissionItems,PermissionInfo permissionInfo) {
		String value="";
		Long userId =permissionInfo.getUserId();
		List<User> users = ApiFactory.getDataDictService().getDirectLower(userId);
		Set<Department> result = new HashSet<Department>();
		for (User u : users) {
			List<Department> depts = ApiFactory.getAcsService().getDepartmentsByUserId(u.getId());
			//去掉重复
			result.addAll(depts);
		}
		for (Department department : result) {
			value = value+ department.getId()+",";
		}
		if(value.indexOf(",")>=0)value = value.substring(0,value.lastIndexOf(","));
		return new ConditionVlaueInfo(DataRuleConditionValueType.STANDARD_VALUE,value);
	}


}
