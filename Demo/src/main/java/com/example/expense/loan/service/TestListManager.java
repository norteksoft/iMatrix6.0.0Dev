package com.example.expense.loan.service;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.expense.entity.TestList;
import com.example.expense.loan.dao.TestListDao;
import com.norteksoft.product.orm.Page;

@Service
@Transactional
public class TestListManager {
	@Autowired
	private TestListDao testListDao;

	public TestList getTestList(Long id){
		return testListDao.get(id);
	}
	
	public void saveTestList(TestList testList){
		testListDao.save(testList);
	}
	
	public void deleteTestList(Long id){
		testListDao.delete(id);
	}
	
	public void deleteTestList(TestList planTask){
		testListDao.delete(planTask);
	}
	
	public Page<TestList> list(Page<TestList>page){
		return testListDao.list(page);
	}
	public void batchInsertData() {
		testListDao.batchInsertData();
	}
}
