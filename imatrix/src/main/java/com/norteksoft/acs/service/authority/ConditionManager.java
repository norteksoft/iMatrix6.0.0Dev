package com.norteksoft.acs.service.authority;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.norteksoft.acs.dao.authority.ConditionDao;
import com.norteksoft.acs.entity.authority.Condition;
import com.norteksoft.product.orm.Page;

@Service
@Transactional
public class ConditionManager {
	@Autowired
	private ConditionDao conditionDao;
	@Autowired
	private PermissionItemConditionManager permissionItemConditionManager;

	/**
	 * 根据id删除数据规则条件
	 * @param id
	 */
	public void delete(Long id) {
		//删除规则条件项
		permissionItemConditionManager.deleteDataRuleConditionItemConditions(id);
		//删除规则条件
		conditionDao.delete(id);
	}

	/**
	 * 根据规则id获得数据表规则条件
	 * @param conditionPage
	 * @param id
	 */
	public void getConditionPage(Page<Condition> conditionPage, Long id) {
		conditionDao.getConditionPage(conditionPage,id);
	}

}
