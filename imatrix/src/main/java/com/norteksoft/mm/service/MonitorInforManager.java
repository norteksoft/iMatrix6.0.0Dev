package com.norteksoft.mm.service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.norteksoft.mm.dao.MonitorInforDao;
import com.norteksoft.mm.entity.MonitorInfor;
import com.norteksoft.product.orm.Page;


/**
 * 性能参监控
 */
@Service
@Transactional
public class MonitorInforManager {
	
	@Autowired
	private MonitorInforDao monitorInforDao;
	
	public void saveMonitorInfor(MonitorInfor monitorInfor){
		this.monitorInforDao.save(monitorInfor);
	}
	
	public MonitorInfor getMonitorInfor(Long id){
		return this.monitorInforDao.get(id);
	}
	
	public Page<MonitorInfor> getMonitorInfor(Page<MonitorInfor> page,String systemCode,String jwebType){
		return this.monitorInforDao.findPage(page,"from MonitorInfor m where m.systemCode=? and m.jweb_type=?",systemCode,jwebType);
	}
	
}
