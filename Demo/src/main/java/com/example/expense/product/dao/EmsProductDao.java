package com.example.expense.product.dao;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Repository;

import com.example.expense.entity.EmsProduct;
import com.norteksoft.acs.base.enumeration.LogicOperator;
import com.norteksoft.product.api.ApiFactory;
import com.norteksoft.product.orm.Page;
import com.norteksoft.product.orm.hibernate.HibernateDao;
import com.norteksoft.product.util.ContextUtils;
import com.norteksoft.product.util.PropUtils;

@Repository
public class EmsProductDao extends HibernateDao<EmsProduct, Long> {
	public Page<EmsProduct> list(Page<EmsProduct> page){
		return findPage(page, "from EmsProduct p");
	}
	
	public Page<EmsProduct> listWidgets(Page<EmsProduct> page){
		return findPage(page, "from EmsProduct p where p.creator=?",ContextUtils.getLoginName());
	}
	
	public List<EmsProduct> getAllProduct(){
		return find("from EmsProduct p order by productNumber asc");
	}
	
	public Page<EmsProduct> search(Page<EmsProduct> page) {
        return searchPageByHql(page, "from EmsProduct p ");
    }
	
	public void decreaseIndex(Integer start, Integer end) {
        createQuery("update EmsProduct formView set formView.displayIndex=formView.displayIndex-1 where formView.displayIndex>? and formView.displayIndex<=?",
                start,end).executeUpdate();
    }

    public void increaseIndex(Integer start, Integer end) {
        createQuery("update EmsProduct formView set formView.displayIndex=formView.displayIndex+1 where formView.displayIndex>=? and formView.displayIndex<?",
                start,end).executeUpdate();
    }

    public void updateIndex(Integer originalIndex, Integer newIndex) {
        createQuery("update EmsProduct formView set formView.displayIndex=? where formView.displayIndex=?",
                newIndex,originalIndex).executeUpdate();
    }

	public Map<String,Object> getAmountTotal(List<String> names) {
		StringBuilder sql=new StringBuilder();
		sql.append("select ");
		for(int i=0;i<names.size();i++){
			if(i>0){
				sql.append(",");
			}
			sql.append("sum(p.");
			sql.append(names.get(i));
			sql.append(")");
		}
		sql.append(" from EmsProduct p ");
		Object[] values=findUnique(sql.toString());
		Map<String,Object> totalValues=new HashMap<String, Object>();
		for(int i=0;i<names.size();i++){
			totalValues.put(names.get(i), values[i]);
		}
		return totalValues;
	}

	public Integer getMaxIndex() {
		return findUnique("select Max(o.displayIndex) from EmsProduct o  ");
	}

	public void decreaseIndex(Integer displayIndex) {
		createQuery("update EmsProduct o set o.displayIndex=o.displayIndex-1 where o.displayIndex>? ",
				displayIndex).executeUpdate();
	}

	public Page<EmsProduct> searchDataRuleList(Page<EmsProduct> page) {
		List<String> codes = new ArrayList<String>();
		codes.add("PRODUCT_RULE1");
		codes.add("PRODUCT_RULE2");
		String hql = "from EmsProduct o where o.companyId=? ";
		if("AND".endsWith(PropUtils.getProp("product.data.rule.link"))){
			ApiFactory.getDataRuleService().getConditionResult(hql, codes, LogicOperator.AND, ContextUtils.getCompanyId());
		}else{
			ApiFactory.getDataRuleService().getConditionResult(hql, codes, LogicOperator.OR, ContextUtils.getCompanyId());
		}
		return searchPageByHql(page,hql);
	}
}
