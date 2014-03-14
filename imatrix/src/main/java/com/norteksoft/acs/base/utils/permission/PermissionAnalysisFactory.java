package com.norteksoft.acs.base.utils.permission;

import org.apache.commons.lang.xwork.StringUtils;

import com.norteksoft.acs.base.enumeration.ConditionType;
import com.norteksoft.acs.base.enumeration.DataRange;
import com.norteksoft.acs.base.enumeration.ItemType;
import com.norteksoft.acs.base.utils.permission.impl.dataRule.advanced.AllDeptValue;
import com.norteksoft.acs.base.utils.permission.impl.dataRule.advanced.AllUserValue;
import com.norteksoft.acs.base.utils.permission.impl.dataRule.advanced.AllWorkgroupValue;
import com.norteksoft.acs.base.utils.permission.impl.dataRule.advanced.CurrentUser;
import com.norteksoft.acs.base.utils.permission.impl.dataRule.advanced.CurrentUserChildDepartment;
import com.norteksoft.acs.base.utils.permission.impl.dataRule.advanced.CurrentUserChildrenDepartment;
import com.norteksoft.acs.base.utils.permission.impl.dataRule.advanced.CurrentUserDepartment;
import com.norteksoft.acs.base.utils.permission.impl.dataRule.advanced.CurrentUserDirectLower;
import com.norteksoft.acs.base.utils.permission.impl.dataRule.advanced.CurrentUserDirectLowerDepartment;
import com.norteksoft.acs.base.utils.permission.impl.dataRule.advanced.CurrentUserDirectLowerRole;
import com.norteksoft.acs.base.utils.permission.impl.dataRule.advanced.CurrentUserDirectLowerWorkgroup;
import com.norteksoft.acs.base.utils.permission.impl.dataRule.advanced.CurrentUserDirectSuperior;
import com.norteksoft.acs.base.utils.permission.impl.dataRule.advanced.CurrentUserDirectSuperiorDepartment;
import com.norteksoft.acs.base.utils.permission.impl.dataRule.advanced.CurrentUserDirectSuperiorRole;
import com.norteksoft.acs.base.utils.permission.impl.dataRule.advanced.CurrentUserDirectSuperiorWorkgroup;
import com.norteksoft.acs.base.utils.permission.impl.dataRule.advanced.CurrentUserRole;
import com.norteksoft.acs.base.utils.permission.impl.dataRule.advanced.CurrentUserSuperiorDepartment;
import com.norteksoft.acs.base.utils.permission.impl.dataRule.advanced.CurrentUserTopDepartment;
import com.norteksoft.acs.base.utils.permission.impl.dataRule.advanced.CurrentUserWorkgroup;
import com.norteksoft.acs.base.utils.permission.impl.dataRule.advanced.DataRuleCommonValue;
import com.norteksoft.acs.base.utils.permission.impl.dataRule.advanced.DataRuleConditionValueSetting;
import com.norteksoft.acs.base.utils.permission.impl.dataRule.simpleness.AllDataValue;
import com.norteksoft.acs.base.utils.permission.impl.dataRule.simpleness.DataRangeSetting;
import com.norteksoft.acs.base.utils.permission.impl.dataRule.simpleness.DepartmentValue;
import com.norteksoft.acs.base.utils.permission.impl.dataRule.simpleness.MyselfValue;
import com.norteksoft.acs.entity.authority.PermissionItem;

public class PermissionAnalysisFactory {
	/**
	 * 获得数据分类中条件值的设置对象
	 * @param conditionValue
	 * @return
	 */
	public static DataRuleConditionValueSetting getDataRuleConditionValueSetting(String conditionValue){
		DataRuleConditionValueSetting valueSetting=null;
		if(conditionValue==null){
			return new DataRuleCommonValue();
		}else{
			if(StringUtils.trim(conditionValue).equals(ConditionType.CURRENT_USER_ID.toString())){//当前用户
				valueSetting=new CurrentUser();
			}else if(StringUtils.trim(conditionValue).equals(ConditionType.CURRENT_USER_DEPARTMENT.toString())){//当前用户部门
				valueSetting=new CurrentUserDepartment();
			}else if(StringUtils.trim(conditionValue).equals(ConditionType.CURRENT_USER_ROLE.toString())){//当前用户角色
				valueSetting=new CurrentUserRole();
			}else if(StringUtils.trim(conditionValue).equals(ConditionType.CURRENT_USER_WORKGROUP.toString())){//当前用户工作组
				valueSetting=new CurrentUserWorkgroup();
			}else if(StringUtils.trim(conditionValue).equals(ConditionType.CURRENT_USER_SUPERIOR_DEPARTMENT.toString())){//上级部门
				valueSetting=new CurrentUserSuperiorDepartment();
			}else if(StringUtils.trim(conditionValue).equals(ConditionType.CURRENT_USER_TOP_DEPARTMENT.toString())){//顶级部门
				valueSetting=new CurrentUserTopDepartment();
			}else if(StringUtils.trim(conditionValue).equals(ConditionType.CURRENT_USER_DIRECT_SUPERIOR_ID.toString())){//直属上级
				valueSetting=new CurrentUserDirectSuperior();
			}else if(StringUtils.trim(conditionValue).equals(ConditionType.CURRENT_USER_DIRECT_SUPERIOR_DEPARTMENT.toString())){//直属上级的部门
				valueSetting=new CurrentUserDirectSuperiorDepartment();
			}else if(StringUtils.trim(conditionValue).equals(ConditionType.CURRENT_USER_DIRECT_SUPERIOR_ROLE.toString())){//直属上级的角色
				valueSetting=new CurrentUserDirectSuperiorRole();
			}else if(StringUtils.trim(conditionValue).equals(ConditionType.CURRENT_USER_DIRECT_SUPERIOR_WORKGROUP.toString())){//直属上级的工作组
				valueSetting=new CurrentUserDirectSuperiorWorkgroup();
			}else if(StringUtils.trim(conditionValue).equals(ConditionType.CURRENT_USER_DIRECT_LOWER_ID.toString())){//直属下级
				valueSetting=new CurrentUserDirectLower();
			}else if(StringUtils.trim(conditionValue).equals(ConditionType.CURRENT_USER_DIRECT_LOWER_DEPARTMENT.toString())){//直属下级的部门
				valueSetting=new CurrentUserDirectLowerDepartment();
			}else if(StringUtils.trim(conditionValue).equals(ConditionType.CURRENT_USER_CHILD_DEPARTMENT.toString())){//当前用户所在部门的子部门
				valueSetting=new CurrentUserChildDepartment();
			}else if(StringUtils.trim(conditionValue).equals(ConditionType.CURRENT_USER_CHILDREN_DEPARTMENT.toString())){//子部门（含继承）
				valueSetting=new CurrentUserChildrenDepartment();
			}else if(StringUtils.trim(conditionValue).equals(ConditionType.CURRENT_USER_DIRECT_LOWER_ROLE.toString())){//直属下级的角色
				valueSetting=new CurrentUserDirectLowerRole();
			}else if(StringUtils.trim(conditionValue).equals(ConditionType.CURRENT_USER_DIRECT_LOWER_WORKGROUP.toString())){//直属下级的工作组
				valueSetting=new CurrentUserDirectLowerWorkgroup();
			}else if(StringUtils.trim(conditionValue).equals("ALL_USER")){//所有用户
				valueSetting=new AllUserValue();
			}else if(StringUtils.trim(conditionValue).equals("ALL_DEPARTMENT")){//所有部门
				valueSetting=new AllDeptValue();
			}else if(StringUtils.trim(conditionValue).equals("ALL_WORKGROUP")){//所有工作组
				valueSetting=new AllWorkgroupValue();
			}else{
				valueSetting=new DataRuleCommonValue();
			}
		}
		return valueSetting;
	}
	
	/**
	 * 获得数据分类中简易设置的对象
	 * @param conditionValue
	 * @return
	 */
	public static DataRangeSetting getDataRangeSetting(DataRange range,Boolean deparmentInheritable){
		DataRangeSetting rangeSetting = null;
		if(range==null)return rangeSetting;
		if(range==DataRange.CURRENT_DEPARTMENT){
			rangeSetting = new DepartmentValue(deparmentInheritable);
		}else if(range==DataRange.MYSELF){
			rangeSetting = new MyselfValue();
		}else if(range==DataRange.ALL){
			rangeSetting = new AllDataValue();
		}
		return rangeSetting;
	}
	/**
	 * 获得数据授权中条件值的设置对象
	 * @param conditionValue
	 * @return
	 */
	public static DataRuleConditionValueSetting getPermissionUserSetting(PermissionItem item){
		DataRuleConditionValueSetting valueSetting=null;
		if(item.getItemType()==ItemType.USER){
			valueSetting=new CurrentUser();
		}else if(item.getItemType()==ItemType.DEPARTMENT){
			valueSetting=new CurrentUserDepartment();
		}else if(item.getItemType()==ItemType.ROLE){
			valueSetting=new CurrentUserRole();
		}else if(item.getItemType()==ItemType.WORKGROUP){
			valueSetting=new CurrentUserWorkgroup();
		}
		return valueSetting;
	}

}
