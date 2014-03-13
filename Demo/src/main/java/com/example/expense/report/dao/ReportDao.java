package com.example.expense.report.dao;

import java.util.List;

import org.springframework.stereotype.Repository;

import com.example.expense.entity.Report;
import com.norteksoft.product.orm.Page;
import com.norteksoft.product.orm.hibernate.HibernateDao;
import com.norteksoft.product.util.ContextUtils;

@Repository
public class ReportDao extends HibernateDao<Report, Long> {
		
	public Page<Report> list(Page<Report> page){
		return findPage(page, "from Report report");
	}
	
	public Page<Report> listWidgets(Page<Report> page){
		return findPage(page, "from Report report where report.creator = ?",ContextUtils.getLoginName());
	}
	
	public List<Report> getAllExpenseReport(){
		return find("from ExpenseReport expenseReport");
	}

    public Page<Report> search(Page<Report> page) {
        return searchPageByHql(page, "from Report r where r.creatorId=? or (r.name=? and r.creatorId is null) order by money asc",ContextUtils.getUserId(),ContextUtils.getUserName());
    }
}
