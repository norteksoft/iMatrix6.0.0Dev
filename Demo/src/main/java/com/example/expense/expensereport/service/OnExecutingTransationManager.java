package com.example.expense.expensereport.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.expense.entity.ExpenseReport;
import com.example.expense.expensereport.dao.ExpenseReportDao;
import com.norteksoft.wf.engine.client.OnExecutingTransation;
@Service
@Transactional
public class OnExecutingTransationManager implements OnExecutingTransation{
	@Autowired
	private ExpenseReportDao expenseReportDao;
	
	@Override
	public void execute(Long id) {
		ExpenseReport expenseReport=expenseReportDao.get(id);
		expenseReport.setField8("field8");
		expenseReport.setField9("field9");
		expenseReport.setField10("field10");
		expenseReport.setField11("field11");
		expenseReport.setField12("field12");
		expenseReport.setField13("field13");
		expenseReport.setField14("field14");
		expenseReport.setField15("field15");
		expenseReportDao.save(expenseReport);
	}

}
