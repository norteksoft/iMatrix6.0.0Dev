package com.norteksoft.wf.unit;



import junit.framework.Assert;

import org.junit.Test;
import org.unitils.database.annotations.Transactional;
import org.unitils.database.util.TransactionMode;
import org.unitils.spring.annotation.SpringApplicationContext;
import org.unitils.spring.annotation.SpringBeanByName;

import com.norteksoft.product.api.ApiFactory;
import com.norteksoft.task.dao.TaskDao;
import com.norteksoft.task.entity.Task;



@Transactional(TransactionMode.ROLLBACK)
@SpringApplicationContext({"applicationContext-test.xml","applicationContext.xml","applicationContext-memcache.xml"})
public class CommonTaskServiceTest extends BaseWorkflowTestCase {
	
	@SpringBeanByName
	private TaskDao taskDao;


	@Test
	public void saveTask(){
		Task task = new Task();
		task.setCompanyId(1L);
		task.setName("wangjing_task_name");
		task.setTitle("wangjing_task_title");
		taskDao.save(task);
		
		Long taskId = taskDao.findUniqueBy("name", "wangjing_task_name").getId();
		ApiFactory.getCommonTaskService().saveTask(taskId);
	}
	
	@Test
	public void createTask(){
		ApiFactory.getCommonTaskService().createTask("/aa/bb.html","wangjing_task_name","wangjing_task_title","王晶的类型",33l);
		Task result = taskDao.findUniqueBy("name", "wangjing_task_name");
		Assert.assertNotNull(result);
		Assert.assertEquals("王晶的类型", result.getCategory());
	}
	
	@Test
	public void createTaskTwo(){
		ApiFactory.getCommonTaskService().createTask("wangjing_task_name","wangjing_taks_title", "王晶的类型",33l);
		Task result = taskDao.findUniqueBy("name", "wangjing_task_name");
		Assert.assertNotNull(result);
		Assert.assertEquals("王晶的类型", result.getCategory());
	}
	
	@Test
	public void completeTask(){
		Task task = new Task();
		task.setCompanyId(1L);
		task.setName("wangjing_task_name");
		task.setTitle("wangjing_task_title");
		taskDao.save(task);
		
		Long taskId = taskDao.findUniqueBy("name", "wangjing_task_name").getId();
		ApiFactory.getCommonTaskService().completeTask(taskId);
	}
}
