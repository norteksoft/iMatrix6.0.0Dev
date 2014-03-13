package com.example.expense.loan.dao;

import java.util.List;

import org.springframework.stereotype.Repository;

import com.example.expense.entity.LoanBill;
import com.norteksoft.product.orm.Page;
import com.norteksoft.product.orm.hibernate.HibernateDao;
import com.norteksoft.product.util.ContextUtils;

@Repository
public class LoanBillDao extends HibernateDao<LoanBill, Long> {
		
	public Page<LoanBill> list(Page<LoanBill> page){
		return findPage(page, "from LoanBill loanBill where companyId=?",ContextUtils.getCompanyId());
	}
	
	public Page<LoanBill> listWidgets(Page<LoanBill> page){
		return findPage(page, "from LoanBill loanBill  where loanBill.creator=?",ContextUtils.getLoginName());
	}
	
	public List<LoanBill> getAllLoanBill(){
		return find("from LoanBill loanBill where companyId=?", ContextUtils.getCompanyId());
	}
}
