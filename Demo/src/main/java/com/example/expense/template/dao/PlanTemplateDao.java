package com.example.expense.template.dao;

import java.util.List;

import org.springframework.stereotype.Repository;

import com.norteksoft.product.orm.Page;
import com.norteksoft.product.orm.hibernate.HibernateDao;
import com.example.expense.entity.PlanTemplate;

@Repository
public class PlanTemplateDao extends HibernateDao<PlanTemplate, Long> {
		
	public Page<PlanTemplate> list(Page<PlanTemplate> page){
		return findPage(page, "from PlanTemplate planTemplate");
	}
	
	public List<PlanTemplate> getAllPlanTemplate(){
		return find("from PlanTemplate planTemplate");
	}
	
	public Page<PlanTemplate> search(Page<PlanTemplate> page) {
        return searchPageByHql(page, "from PlanTemplate planTemplate order by name asc");
    }
	
	/**
	 * 根据模版名称取模版
	 * @param tempName
	 * @param id 
	 * @return
	 */
	public List<PlanTemplate> getPlanTemplateByName(String tempName, Long id) {
		if(id==null){
			return find("from PlanTemplate p where  p.name=? ",tempName);
		}else{
			return find("from PlanTemplate p where  p.name=? and id<>? ",tempName,id);
		}
		
	}
}
