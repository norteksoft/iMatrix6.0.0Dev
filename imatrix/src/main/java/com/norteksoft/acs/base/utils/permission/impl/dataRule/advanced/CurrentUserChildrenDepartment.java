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
 * 数据分类中条件值中的标准值:当前用户所在部门的子部门(含继承)
 * @author Administrator
 *
 */
public class CurrentUserChildrenDepartment implements DataRuleConditionValueSetting{

	public ConditionVlaueInfo getValues(String conditionValue,List<PermissionItem> permissionItems,PermissionInfo permissionInfo) {
		StringBuilder value= new StringBuilder();
		AcsServiceImpl acsService = (AcsServiceImpl)ContextUtils.getBean("acsServiceImpl");
		Collection<Long> departmentIds = null;
		if(permissionItems.size()<=0){//数据分类api解析
			departmentIds = permissionInfo.getDepartmentIds();
		}else{//数据授权对应的数据分类解析
			departmentIds = PermissionUtils.getDepartmentIds(permissionItems,permissionInfo);
		}
		boolean hasChildren = false;
		for (Long departmentId : departmentIds) {
			 Set<Long> result = new HashSet<Long>();
			 List<Long> childDeptIds = acsService.getSubDepartmentIdList(departmentId);
			 if(childDeptIds.size()>0){//如果存在子部门
				 hasChildren = true;
				 result.addAll(childDeptIds);
				 getChildDepartments(result,value,acsService);
			 }
		}
		 if(hasChildren){
			 if(value.indexOf(",")>=0)value.deleteCharAt(value.toString().length()-1);
		 }else{
			 value .append(PermissionUtils.NO_DEPARTMENT);//不存在下级部门
		 }
		return new ConditionVlaueInfo(DataRuleConditionValueType.STANDARD_VALUE,value.toString());
	}
	
	public void getChildDepartments(Set<Long> childDeptIds,StringBuilder value,AcsServiceImpl acsService){
		 for(Long deptId : childDeptIds){
			 Set<Long> result = new HashSet<Long>();
			 List<Long> childDepts = acsService.getSubDepartmentIdList(deptId);
			 if(!childDepts.isEmpty())result.addAll(childDepts);
			 value.append(deptId)
			 .append(",");
			 for(Long childDept : result){
				 value.append(childDept)
				 .append(",");
				 Set<Long> childresult = new HashSet<Long>();
				 childDepts = acsService.getSubDepartmentIdList(childDept);
				 if(!childDepts.isEmpty())childresult.addAll(childDepts);
				 getChildDepartments(childresult,value,acsService);
			 }
		 }
	}

}
