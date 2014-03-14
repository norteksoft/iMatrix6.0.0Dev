package com.norteksoft.acs.service.authority;


import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.norteksoft.acs.dao.authority.PermissionItemConditionDao;
import com.norteksoft.acs.entity.authority.PermissionItemCondition;

@Service
@Transactional
public class PermissionItemConditionManager {
	@Autowired
	private PermissionItemConditionDao permissionItemConditionDao;
	/**
	 * 删除数据授权对应的条件集合
	 * @param permissionId
	 */
	public void deleteAllPermissionItemConditions(Long permissionId){
		permissionItemConditionDao.deleteAllPermissionItemConditions(permissionId);
	}
	/**
	 * 删除某数据授权的人员对应的所有的条件项
	 * @param permissionItemId
	 */
	public void deletePermissionItemConditions(Long permissionItemId){
		permissionItemConditionDao.deletePermissionItemConditions(permissionItemId);
	}
	/**
	 * 删除数据分类对应的规则条件集合
	 * @param dataRuleId
	 */
	public void deleteAllDataRuleItemConditions(Long dataRuleId){
		permissionItemConditionDao.deleteAllDataRuleItemConditions(dataRuleId);
	}
	/**
	 * 删除某数据分类的规则条件对应的所有条件项
	 * @param dataRuleConditionId
	 */
	public void deleteDataRuleConditionItemConditions(Long dataRuleConditionId){
		permissionItemConditionDao.deleteDataRuleConditionItemConditions(dataRuleConditionId);
	}
	
	public void save(PermissionItemCondition condition){
		permissionItemConditionDao.save(condition);
	}
	
	public List<PermissionItemCondition> getPermissionItemConditions(Long permissionItemId){
		return permissionItemConditionDao.getPermissionItemConditions(permissionItemId);
	}
	
}
