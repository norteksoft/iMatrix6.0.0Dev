package com.example.expense.loan.service;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.expense.entity.PlanTask;
import com.example.expense.loan.dao.PlanTaskDao;
import com.norteksoft.product.orm.Page;

@Service
@Transactional
public class PlanTaskManager {
	@Autowired
	private PlanTaskDao planTaskDao;

	public PlanTask getPlanTask(Long id){
		return planTaskDao.get(id);
	}
	
	public void savePlanTask(PlanTask planTask){
		planTaskDao.save(planTask);
	}
	
	public void deletePlanTask(Long id){
		planTaskDao.delete(id);
	}
	
	public void deletePlanTask(PlanTask planTask){
		planTaskDao.delete(planTask);
	}
	
	public Page<PlanTask> list(Page<PlanTask>page){
		return planTaskDao.list(page);
	}

	public void batchInsertData() {
		planTaskDao.batchInsertData();
	}

}
