package com.norteksoft.acs.base.utils.permission.impl.dataRule.advanced;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


import com.norteksoft.acs.base.utils.PermissionUtils;
import com.norteksoft.acs.entity.authority.PermissionInfo;

import com.norteksoft.acs.entity.authority.PermissionItem;
import com.norteksoft.product.api.ApiFactory;
import com.norteksoft.product.api.entity.Department;

/**
 *当前用户的顶级部门的相关处理
 * @author Administrator
 *
 */
public class CurrentUserTopDepartment implements DataRuleConditionValueSetting{
	/**
	 * 数据分类中条件值中的标准值:当前用户的顶级部门
	 * @author Administrator
	 *
	 */
	public ConditionVlaueInfo getValues(String conditionValue,List<PermissionItem> permissionItems,PermissionInfo permissionInfo) {
		String value="";
		Set<Long> result = new HashSet<Long>();
		Collection<Long> deptIds = null;
		if(permissionItems.size()<=0){//数据分类api解析
			deptIds = permissionInfo.getDepartmentIds();
		}else{//数据授权对应的数据分类解析
			deptIds = PermissionUtils.getDepartmentIds(permissionItems,permissionInfo);
		}
		for (Long departmentId : deptIds) {
			Department dept = ApiFactory.getAcsService().getTopDepartment(departmentId);
			if(dept==null){//如果不存在顶级部门，则取当前用户所在的部门
				result.add(departmentId);
			}else{//如果存在顶级部门
				if(dept.getBranch()){//顶级部门如果是分支机构,则取当前用户的部门
					result.add(departmentId);
				}else{
					result.add(dept.getId());
				}
			}
		}
		value = result.toString().replace("[", "").replace("]", "").replace(" ", "");
		return new ConditionVlaueInfo(DataRuleConditionValueType.STANDARD_VALUE,value);
	}

}
