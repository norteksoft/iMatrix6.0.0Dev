package com.norteksoft.wf.unit;

import org.junit.Assert;
import org.junit.Test;
import org.unitils.database.annotations.Transactional;
import org.unitils.database.util.TransactionMode;
import org.unitils.spring.annotation.SpringApplicationContext;
import org.unitils.spring.annotation.SpringBeanByName;
import com.norteksoft.bs.options.dao.OptionDao;
import com.norteksoft.bs.signature.dao.SignatureDao;
import com.norteksoft.bs.signature.entity.Signature;
import com.norteksoft.product.api.ApiFactory;
import com.norteksoft.product.orm.Page;
import com.norteksoft.task.dao.TaskDao;
import com.norteksoft.task.entity.Task;


@Transactional(TransactionMode.ROLLBACK)
@SpringApplicationContext({"applicationContext-test.xml","applicationContext.xml","applicationContext-memcache.xml"})
public class DBServiceTest extends BaseWorkflowTestCase {
	@SpringBeanByName
	private TaskDao taskDao;

	@SpringBeanByName
	OptionDao optionDao;
	
	@SpringBeanByName
	SignatureDao signatureDao;

	@Test
	public void save(){
		Task entity = new Task();
		entity.setCompanyId(1L);
		entity.setName("wangjing_task_name");
		entity.setTitle("wangjing_task_title");
		ApiFactory.getDbService().save(entity);
		
		Task result = taskDao.findUniqueBy("name", "wangjing_task_name");
		Assert.assertNotNull(result);
	}
	
	@Test
	public void getObject(){
		Task entity = new Task();
		entity.setCompanyId(1L);
		entity.setName("wangjing_task_name");
		entity.setTitle("wangjing_task_title");
		ApiFactory.getDbService().save(entity);
		
		Long id = taskDao.findUniqueBy("name", "wangjing_task_name").getId();
		Object result = ApiFactory.getDbService().getObject("Task", id);
		Assert.assertNotNull(result);
	}
	
	@Test
	public void delete(){
		Task entity = new Task();
		entity.setCompanyId(1L);
		entity.setName("wangjing_task_name");
		entity.setTitle("wangjing_task_title");
		ApiFactory.getDbService().save(entity);
		
		Long id = taskDao.findUniqueBy("name", "wangjing_task_name").getId();
		ApiFactory.getDbService().delete("Task", id);
		Object result = ApiFactory.getDbService().getObject("Task", id);
		Assert.assertNull(result);
	}
	
	@Test
	public void findPageBySql(){
		Signature signature = new Signature();
		signature.setCompanyId(1L);
		signature.setUserName("ldx");
		signature.setUserId(33l);
		signatureDao.save(signature);
		
		Page<Object> page = new Page<Object>();
		page.setAutoCount(false);
		String sql = "select * from BS_SIGNATURE t where t.user_id=?";
		Object[] values = {33l};
		Page<Object> result = ApiFactory.getDbService().findPageBySql(sql,page,values);
		Assert.assertNotNull(result);
	}

}
