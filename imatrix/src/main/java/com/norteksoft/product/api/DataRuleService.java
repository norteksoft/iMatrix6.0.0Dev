package com.norteksoft.product.api;

import java.util.List;

import com.norteksoft.acs.base.enumeration.LogicOperator;


public interface DataRuleService {
	/**
	 * 根据数据规则编号的集合获取条件，link为规则之间的与或关系
	 * @param hql
	 * @param link：规则关系
	 * @param dataRuleCodes：规则编号
	 * @throws Exception
	 */
	public void getConditionResult(String hql, List<String> dataRuleCodes, LogicOperator link,Object... values) ;
}
