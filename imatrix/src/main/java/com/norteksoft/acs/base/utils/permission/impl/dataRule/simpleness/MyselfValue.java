package com.norteksoft.acs.base.utils.permission.impl.dataRule.simpleness;

import java.util.List;

import com.norteksoft.acs.entity.authority.PermissionInfo;
import com.norteksoft.acs.entity.authority.PermissionItem;
import com.norteksoft.product.util.ContextUtils;

/**
 * 数据分类的简易设置:本人
 * @author Administrator
 *
 */
public class MyselfValue implements DataRangeSetting {

	public String getValues(List<PermissionItem> permissionItems,PermissionInfo permissionInfo) {
		Long userId = ContextUtils.getUserId();
		if(userId==null){
			return "";
		}else{
			return userId.toString();
		}
	}

}
