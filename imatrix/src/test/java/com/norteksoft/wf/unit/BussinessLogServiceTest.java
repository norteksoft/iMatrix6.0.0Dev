package com.norteksoft.wf.unit;



import junit.framework.Assert;

import org.hibernate.SessionFactory;
import org.junit.Test;
import org.unitils.database.annotations.Transactional;
import org.unitils.database.util.TransactionMode;
import org.unitils.spring.annotation.SpringApplicationContext;
import org.unitils.spring.annotation.SpringBeanByName;

import com.norteksoft.acs.base.orm.hibernate.SimpleHibernateTemplate;
import com.norteksoft.acs.entity.log.Log;
import com.norteksoft.product.api.ApiFactory;



@Transactional(TransactionMode.ROLLBACK)
@SpringApplicationContext({"applicationContext-test.xml","applicationContext.xml","applicationContext-memcache.xml"})
public class BussinessLogServiceTest extends BaseWorkflowTestCase {
	
	private SimpleHibernateTemplate<Log, Long> logDao;
	@SpringBeanByName
	public void setSessionFactory(SessionFactory sessionFactory){
		logDao = new SimpleHibernateTemplate<Log, Long>(sessionFactory, Log.class);
	}


	@Test
	public void log(){
		ApiFactory.getBussinessLogService().log("用户管理wangjing", "查看用户列表wangjing");
		Log result = logDao.findUniqueByProperty("operationType", "用户管理wangjing");
		Assert.assertNotNull(result);
		Assert.assertEquals("用户管理wangjing", result.getOperationType());
	}
	
	@Test
	public void logTwo(){
		ApiFactory.getBussinessLogService().log("用户管理wangjing", "查看用户列表wangjing",7L);
		Log result = logDao.findUniqueByProperty("operationType", "用户管理wangjing");
		Assert.assertNotNull(result);
		Assert.assertEquals("用户管理wangjing", result.getOperationType());
	}
	
	@Test
	public void logThree(){
		ApiFactory.getBussinessLogService().log("wangjinghgukhgk7u","用户管理wangjing", "查看用户列表wangjing");
		Log result = logDao.findUniqueByProperty("operationType", "用户管理wangjing");
		Assert.assertNotNull(result);
		Assert.assertEquals("wangjinghgukhgk7u", result.getOperator());
	}
	
	@Test
	public void logFour(){
		ApiFactory.getBussinessLogService().log(33L,"用户管理wangjing", "查看用户列表wangjing");
		Log result = logDao.findUniqueByProperty("operationType", "用户管理wangjing");
		Assert.assertNotNull(result);
		Assert.assertEquals("用户管理wangjing", result.getOperationType());
	}
}
