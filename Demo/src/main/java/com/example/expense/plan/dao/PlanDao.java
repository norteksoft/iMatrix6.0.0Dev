package com.example.expense.plan.dao;

import org.springframework.stereotype.Repository;

import com.example.expense.entity.Plan;
import com.norteksoft.acs.base.enumeration.LogicOperator;
import com.norteksoft.product.api.ApiFactory;
import com.norteksoft.product.orm.Page;
import com.norteksoft.product.orm.hibernate.HibernateDao;
import com.norteksoft.product.util.ContextUtils;
import com.norteksoft.product.util.PropUtils;

@Repository
public class PlanDao extends HibernateDao<Plan, Long> {
	public Page<Plan> list(Page<Plan> page){
		StringBuilder hql = new StringBuilder("select distinct plan from Plan plan inner join plan.planItems planItems ");
		LogicOperator authlink = LogicOperator.OR;
		LogicOperator dataRulelink = LogicOperator.OR;
		if("AND".equals(PropUtils.getProp("plan.data.auth.link")))authlink=LogicOperator.AND;
		if("AND".equals(PropUtils.getProp("product.data.rule.link")))dataRulelink = LogicOperator.AND;
		ApiFactory.getDataPermissionService().addPermissionCondition(hql.toString(),authlink,dataRulelink);
		return searchPageByHql(page,hql.toString());
	}
	
	public Page<Plan> listWidgets(Page<Plan> page){
		return searchPageByHql(page,"from Plan plan where plan.creator=?",ContextUtils.getLoginName());
	}
}
