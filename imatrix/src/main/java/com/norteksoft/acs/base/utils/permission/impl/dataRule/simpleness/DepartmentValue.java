package com.norteksoft.acs.base.utils.permission.impl.dataRule.simpleness;

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
 * 数据分类的简易设置:本部门
 * @author Administrator
 *
 */
public class DepartmentValue implements DataRangeSetting {
	private Boolean deparmentInheritable=true;
	public DepartmentValue(Boolean deparmentInheritable) {
		this.deparmentInheritable=deparmentInheritable;
	}
	public String getValues(List<PermissionItem> permissionItems,PermissionInfo permissionInfo) {
		Long userId = ContextUtils.getUserId();
		if(userId==null){
			return "";
		}else{
			StringBuilder value= new StringBuilder();
			AcsServiceImpl acsService = (AcsServiceImpl)ContextUtils.getBean("acsServiceImpl");
			Collection<Long> deptIds = null;
			if(permissionItems.size()<=0){//数据分类api解析
				deptIds = permissionInfo.getDepartmentIds();
			}else{//数据授权对应的数据分类解析
				deptIds = PermissionUtils.getDepartmentIds(permissionItems,permissionInfo);
			}
			Set<Long> result = new HashSet<Long>();
			if(!deptIds.isEmpty())result.addAll(deptIds);
			if(deparmentInheritable){//子部门继承该权限
				for (Long deptId : deptIds) {
					value.append(deptId)
					.append(",");
					 Set<Long> deptresult = new HashSet<Long>();
					 List<Long> childDeptIds = acsService.getSubDepartmentIdList(deptId);
					 if(!childDeptIds.isEmpty())deptresult.addAll(childDeptIds);
					 getChildDepartments(deptresult,value,acsService);
				}
				if(value.toString().length()>0&&value.lastIndexOf(",")==value.toString().length()-1)value.deleteCharAt(value.toString().length()-1);
				return value.toString();
			}else{//子部门不继承
				return result.toString().replace("[", "").replace("]", "").replace(" ", "");
			}
		}
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
