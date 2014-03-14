package com.norteksoft.acs.base.utils.permission.impl.dataRule.simpleness;

import java.util.List;

import com.norteksoft.acs.entity.authority.PermissionInfo;
import com.norteksoft.acs.entity.authority.PermissionItem;
import com.norteksoft.product.util.ContextUtils;

/**
 * 数据分类的简易设置:本部门
 * @author Administrator
 *
 */
public class AllDataValue implements DataRangeSetting {
	public String getValues(List<PermissionItem> permissionItems,PermissionInfo permissionInfo) {
		Long companyId = ContextUtils.getCompanyId();
		if(companyId==null){
			return "";
		}else{
			return companyId+"";
		}
	}
	

}
