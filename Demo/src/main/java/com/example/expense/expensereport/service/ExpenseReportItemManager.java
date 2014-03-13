package com.example.expense.expensereport.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.norteksoft.wf.engine.client.OnExecutingTransation;

/**
 *报销流程中经过第一环节时执行此操作
 */
@Service
@Transactional
public class ExpenseReportItemManager implements OnExecutingTransation{

	public void execute(Long dataId) {
		System.out.println("经过此处："+dataId);
	}

}
