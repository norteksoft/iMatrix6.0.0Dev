package com.example.expense.expensereport.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.expense.entity.ExpenseReport;
import com.example.expense.expensereport.dao.ExpenseReportDao;
import com.norteksoft.task.base.enumeration.TaskProcessingResult;
import com.norteksoft.wf.engine.client.BeforeTaskSubmit;
@Service
@Transactional
public class BeforeTaskSubmitManager implements BeforeTaskSubmit{
	@Autowired
	private ExpenseReportDao expenseReportDao;
	
	@Override
	public boolean execute(Long id, TaskProcessingResult arg1) {
		ExpenseReport expenseReport=expenseReportDao.get(id);
		expenseReport.setField16("提交前事件处理");
		expenseReportDao.save(expenseReport);
		return true;
	}

}
