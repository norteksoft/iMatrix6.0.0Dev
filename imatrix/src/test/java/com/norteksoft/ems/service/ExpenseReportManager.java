package com.norteksoft.ems.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.norteksoft.ems.dao.ExpenseReportDao;
import com.norteksoft.ems.entity.ExpenseReport;

/**
 * 打印manager
 * 
 * @author zzl
 *
 */
@Service
@Transactional
public class ExpenseReportManager {
	
	@Autowired
	private ExpenseReportDao expenseReportDao;
	
	public ExpenseReport getExpenseReport(Long id){
		return this.expenseReportDao.get(id);
	}
	
	public void save(ExpenseReport dp){
		expenseReportDao.save(dp);
	}

	protected void saveEntity(ExpenseReport t) {
		this.expenseReportDao.save(t);
	}

}
