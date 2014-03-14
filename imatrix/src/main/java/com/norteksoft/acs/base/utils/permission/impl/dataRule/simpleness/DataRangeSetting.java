package com.norteksoft.acs.base.utils.permission.impl.dataRule.simpleness;

import java.util.List;

import com.norteksoft.acs.entity.authority.PermissionInfo;
import com.norteksoft.acs.entity.authority.PermissionItem;

public interface DataRangeSetting {
	/**
	 * 获得数据分类中简易设置的条件值相应的值，以逗号隔开
	 * @param deparmentInheritable  本部门时是否继承该权限
	 * @param permissionInfo  包含数据授权解析后的人员、当前用户信息等
	 * @return
	 */
	public String getValues(List<PermissionItem> permissionItems,PermissionInfo permissionInfo);

}
