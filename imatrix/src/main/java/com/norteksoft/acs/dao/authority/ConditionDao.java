package com.norteksoft.acs.dao.authority;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.springframework.stereotype.Repository;

import com.norteksoft.acs.base.enumeration.FieldOperator;
import com.norteksoft.acs.base.enumeration.LogicOperator;
import com.norteksoft.acs.entity.authority.Condition;
import com.norteksoft.mms.form.enumeration.DataType;
import com.norteksoft.product.orm.Page;
import com.norteksoft.product.orm.hibernate.HibernateDao;
import com.norteksoft.product.util.ContextUtils;

/**
 * 数据规则条件
 * @author Administrator
 *
 */
@Repository
public class ConditionDao extends HibernateDao<Condition, Long>{

	/**
	 * 根据规则ID删除条件
	 * @param valueOf
	 */
	public void deleteConditionByRuleId(Long dataRuleId) {
		this.batchExecute("delete Condition c where c.companyId=? and c.dataRule.id=? ", ContextUtils.getCompanyId(),dataRuleId);
	}
	/**
	 * 根据规则条件ID集合删除条件
	 * @param valueOf
	 */
	public void deleteConditionByConIds(List<Long> conditionIds) {
		if(conditionIds.size()>0){
			Long[] objs = new Long[1+conditionIds.size()];
			objs[0]=ContextUtils.getCompanyId();
			String hql = "delete Condition c where c.companyId=? and (";
			int i=1;
			for(int j=0;j<conditionIds.size();j++){
				hql = hql+" c.id=?  ";
				objs[i]=conditionIds.get(j);
				if(j<conditionIds.size()-1){
					hql = hql+" or ";
				}
				i++;
			}
			hql = hql+" )";
			this.batchExecute(hql, objs);
		}
	}

	/**
	 * 根据规则id获得数据表规则条件
	 * @param conditionPage
	 * @param id
	 */
	public void getConditionPage(Page<Condition> conditionPage, Long id) {
		this.searchPageSubByHql(conditionPage,"from Condition c where c.companyId=? and c.dataRule.id=? order by c.displayIndex ",ContextUtils.getCompanyId(),id);
	}
	
	/**
	 * 根据规则id获得数据表规则条件
	 * @param conditionPage
	 * @param id
	 */
	public List<Condition> getConditionsByDataRuleId(Long id) {
		return this.find("from Condition c where c.companyId=? and c.dataRule.id=? ",ContextUtils.getCompanyId(),id);
	}

	public Condition getCondition(String field, FieldOperator operator,
			LogicOperator lgicOperator, DataType dataType, String conditionValue,
			Long dataRuleId) {
		List<Condition> conditions=this.find("from Condition c where c.companyId=? and c.field=? and c.operator=? and c.lgicOperator=? and c.dataType=? and c.conditionValue=? and c.dataRule.id=? ",ContextUtils.getCompanyId(),field,operator,lgicOperator,dataType,conditionValue,dataRuleId);
		if(conditions!=null && conditions.size()>0){
			return conditions.get(0);
		}else{
			return null;
		}
	}

}
