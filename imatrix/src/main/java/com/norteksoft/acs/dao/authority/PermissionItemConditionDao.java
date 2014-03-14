package com.norteksoft.acs.dao.authority;


import java.util.List;

import org.springframework.stereotype.Repository;

import com.norteksoft.acs.base.enumeration.ConditionValueType;
import com.norteksoft.acs.entity.authority.PermissionItemCondition;
import com.norteksoft.product.orm.hibernate.HibernateDao;
import com.norteksoft.product.util.ContextUtils;
@Repository
public class PermissionItemConditionDao extends HibernateDao<PermissionItemCondition,Long>{

	/**
	 * 删除某数据授权对应的人员条件项
	 * @param permissionId
	 */
	public void deleteAllPermissionItemConditions(Long permissionId){
		this.createQuery("delete from PermissionItemCondition pic where pic.valueType=? and pic.dataId in (select pi.id from PermissionItem pi where pi.permission.id=? and pi.companyId=?)",ConditionValueType.PERMISSION, permissionId,ContextUtils.getCompanyId()).executeUpdate();
	}
	/**
	 * 删除某数据授权的人员对应的所有的条件项
	 * @param permissionId
	 */
	public void deletePermissionItemConditions(Long permissionItemId){
		this.createQuery("delete from PermissionItemCondition pic where pic.valueType=? and pic.dataId=?",ConditionValueType.PERMISSION, permissionItemId).executeUpdate();
	}
	/**
	 * 删除某数据分类对应的所有规则条件的条件项
	 * @param permissionId
	 */
	public void deleteAllDataRuleItemConditions(Long dataRuleId){
		this.createQuery("delete from PermissionItemCondition pic where pic.valueType=? and pic.dataId in (select pi.id from Condition pi where pi.dataRule.id=? and pi.companyId=?)",ConditionValueType.DATA_RULE, dataRuleId,ContextUtils.getCompanyId()).executeUpdate();
	}
	/**
	 * 删除某数据分类的规则条件对应的所有条件项
	 * @param permissionId
	 */
	public void deleteDataRuleConditionItemConditions(Long dataRuleConditionId){
		this.createQuery("delete from PermissionItemCondition pic where pic.valueType=? and pic.dataId =?",ConditionValueType.DATA_RULE, dataRuleConditionId).executeUpdate();
	}
	/**
	 * 查询某数据分类的规则条件对应的所有条件项
	 * @param dataRuleConditionId
	 * @return
	 */
	public List<PermissionItemCondition> getDataRuleConditionItemConditions(Long dataRuleConditionId){
		return this.find("from PermissionItemCondition pic where pic.valueType=? and pic.dataId =?", ConditionValueType.DATA_RULE, dataRuleConditionId);
	}
	/**
	 * 根据数据授权的人员查询所有的条件项
	 * @param permissionItemId
	 * @return
	 */
	public List<PermissionItemCondition> getPermissionItemConditions(Long permissionItemId){
		return this.find("from PermissionItemCondition pic where pic.valueType=? and pic.dataId=?", ConditionValueType.PERMISSION, permissionItemId);
		
	}
	/**
	 * 根据数据授权的人员查询所有的条件项
	 * @param permissionItemId
	 * @return
	 */
	public List<PermissionItemCondition> getPermissionItemTypeUserConditions(Long permissionItemId){
		return this.find("from PermissionItemCondition pic where pic.valueType=? and pic.dataId=? and pic.conditionValue like ?", ConditionValueType.PERMISSION, permissionItemId,"%"+ContextUtils.getUserId()+"~%");
		
	}
}
