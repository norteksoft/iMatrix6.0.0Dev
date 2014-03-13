package com.example.expense.plan.dao;

import org.springframework.stereotype.Repository;

import com.example.expense.entity.PlanItem;
import com.norteksoft.product.orm.Page;
import com.norteksoft.product.orm.hibernate.HibernateDao;

@Repository
public class PlanItemDao extends HibernateDao<PlanItem, Long> {
	public Page<PlanItem> list(Page<PlanItem> pageItem,Long id){
		return this.searchPageSubByHql(pageItem, "from PlanItem planItems where planItems.plan.id=? ",id);
	}
}
