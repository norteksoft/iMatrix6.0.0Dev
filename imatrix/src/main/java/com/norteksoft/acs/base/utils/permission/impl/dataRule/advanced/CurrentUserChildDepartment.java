package com.norteksoft.acs.base.utils.permission.impl.dataRule.advanced;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.norteksoft.acs.base.utils.PermissionUtils;
import com.norteksoft.acs.entity.authority.PermissionInfo;

import com.norteksoft.acs.entity.authority.PermissionItem;
import com.norteksoft.product.api.impl.AcsServiceImpl;
import com.norteksoft.product.util.ContextUtils;


/**
 * 数据分类中条件值中的标准值:当前用户所在部门的子部门
 * @author Administrator
 *
 */
public class CurrentUserChildDepartment implements DataRuleConditionValueSetting{

	public ConditionVlaueInfo getValues(String conditionValue,List<PermissionItem> permissionItems,PermissionInfo permissionInfo) {
		String value="";
		Collection<Long> departmentIds = null;
		if(permissionItems.size()<=0){//数据分类api解析
			departmentIds = permissionInfo.getDepartmentIds();
		}else{//数据授权对应的数据分类解析
			departmentIds = PermissionUtils.getDepartmentIds(permissionItems,permissionInfo);
		}
		
		Set<Long> result = new HashSet<Long>();
		AcsServiceImpl acsService = (AcsServiceImpl)ContextUtils.getBean("acsServiceImpl");
		 for (Long departmentId : departmentIds) {
			 List<Long> deptIds = acsService.getSubDepartmentIdList(departmentId);
			 if(!deptIds.isEmpty()){//如果存在子部门
				 result.addAll(deptIds);//去除重复的部门id
			 }
		}
		if(result.size()>0){
			 value = result.toString().replace("[", "").replace("]", "").replace(" ", "");
		}else{
			value = PermissionUtils.NO_DEPARTMENT;//不存在下级部门
		}
		return new ConditionVlaueInfo(DataRuleConditionValueType.STANDARD_VALUE,value);
	}


}
