package com.example.expense.loan.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.expense.entity.LoanBill;
import com.example.expense.loan.dao.LoanBillDao;
import com.norteksoft.product.orm.Page;
import com.norteksoft.product.util.ContextUtils;

@Service
@Transactional
public class LoanBillManager {
	@Autowired
	private LoanBillDao loanBillDao;

	public LoanBill getLoanBill(Long id){
		return loanBillDao.get(id);
	}
	
	public void saveLoanBill(LoanBill loanBill){
		loanBillDao.save(loanBill);
	}
	
	public void deleteLoanBill(Long id){
		loanBillDao.delete(id);
	}
	
	public void deleteLoanBill(LoanBill loanBill){
		loanBillDao.delete(loanBill);
	}
	
	public Page<LoanBill> list(Page<LoanBill>page){
		return loanBillDao.list(page);
	}
	
	public List<LoanBill> listAll(){
		return loanBillDao.getAllLoanBill();
	}

	public List<LoanBill> getFolder(Long parentId) {
		String hql = "from LoanBill f where f.companyId=? and f.parentId=?  order by f.id asc";
		return loanBillDao.find(hql, ContextUtils.getCompanyId(),parentId);
	}
}
