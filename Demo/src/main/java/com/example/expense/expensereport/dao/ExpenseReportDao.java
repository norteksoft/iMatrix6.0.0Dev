package com.example.expense.expensereport.dao;

import java.util.List;

import org.springframework.stereotype.Repository;

import com.example.expense.entity.ExpenseReport;
import com.norteksoft.product.orm.Page;
import com.norteksoft.product.orm.hibernate.HibernateDao;
import com.norteksoft.product.util.ContextUtils;

@Repository
public class ExpenseReportDao extends HibernateDao<ExpenseReport, Long> {
		
	public Page<ExpenseReport> list(Page<ExpenseReport> page){
		return findPage(page, "from ExpenseReport expenseReport");
	}
	
	public Page<ExpenseReport> listWidgets(Page<ExpenseReport> page){
		return findPage(page, "from ExpenseReport expenseReport where expenseReport.creator=?","user1");
	}
	
	public List<ExpenseReport> getAllExpenseReport(){
		return find("from ExpenseReport expenseReport");
	}

    public Page<ExpenseReport> search(Page<ExpenseReport> page) {
        return searchPageByHql(page, "from ExpenseReport expenseReport where expenseReport.creatorId=? or(expenseReport.name=? and expenseReport.creatorId is null) order by money asc",ContextUtils.getUserId(),ContextUtils.getUserName());
    }
}
