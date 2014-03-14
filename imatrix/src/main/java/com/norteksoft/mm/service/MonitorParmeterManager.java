package com.norteksoft.mm.service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.norteksoft.mm.dao.MonitorParmeterDao;
import com.norteksoft.mm.entity.MonitorParmeter;
import com.norteksoft.product.orm.Page;


/**
 * 性能参监控参数
 */
@Service
@Transactional
public class MonitorParmeterManager {
	
	@Autowired
	private MonitorParmeterDao monitorParmeterDao;
	
	public void saveParmeter(MonitorParmeter monitorParmeter){
		this.monitorParmeterDao.save(monitorParmeter);
	}
	
	public MonitorParmeter getParmeter(Long id){
		return this.monitorParmeterDao.get(id);
	}
	
	public Page<MonitorParmeter> getMonitorParmeterPage(Page<MonitorParmeter> page){
		return this.monitorParmeterDao.findPage(page,"from MonitorParmeter m");
	}
	
	public MonitorParmeter getMonitorParmeter(String systemCode){
		return this.monitorParmeterDao.findUnique("from MonitorParmeter m where m.systemCode=?",systemCode);
	}
}
