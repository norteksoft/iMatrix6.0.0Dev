package com.example.expense.base.utils;

import java.io.Serializable;


import com.example.expense.entity.ExpenseReport;
import com.example.expense.expensereport.service.ExpenseReportManager;
import com.norteksoft.product.util.ParameterUtils;
import com.norteksoft.product.util.ThreadParameters;

public class ExpenseThread implements Runnable,Serializable{
	private static final long serialVersionUID = 1L;
	
	private ExpenseReport expenseReport;
	
	private Long companyId;
	
	private String userName;
	
	private String loginName;
	
	private ExpenseReportManager expenseReportManager;

	@Override
	public void run() {
		ThreadParameters parameters=new ThreadParameters();
		parameters.setLoginName(loginName);
		parameters.setCompanyId(companyId);
		parameters.setUserName(userName);
		ParameterUtils.setParameters(parameters);
		expenseReportManager.saveInstance("expense-report",expenseReport);
	}

	public void setExpenseReport(ExpenseReport expenseReport) {
		this.expenseReport = expenseReport;
	}

	public void setCompanyId(Long companyId) {
		this.companyId = companyId;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public void setLoginName(String loginName) {
		this.loginName = loginName;
	}

	public void setExpenseReportManager(ExpenseReportManager expenseReportManager) {
		this.expenseReportManager = expenseReportManager;
	}
	
}
