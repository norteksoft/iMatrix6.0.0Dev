package com.norteksoft.wf.engine.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.norteksoft.wf.engine.entity.HistoryInstanceHistory;
import com.norteksoft.wf.engine.dao.HistoryInstanceHistoryDao;

@Service
@Transactional
public class HistoryInstanceHistoryManager {
	@Autowired
	private HistoryInstanceHistoryDao historyInstanceHistoryDao;
	/**
	 * 保存流转历史
	 * @param ih
	 */
	@Transactional(readOnly=false)
	public void saveHistory(HistoryInstanceHistory ih){
		historyInstanceHistoryDao.save(ih);
	}

}
