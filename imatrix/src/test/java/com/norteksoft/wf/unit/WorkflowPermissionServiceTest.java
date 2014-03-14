package com.norteksoft.wf.unit;

import org.junit.Assert;
import org.junit.Test;
import org.unitils.database.annotations.Transactional;
import org.unitils.database.util.TransactionMode;
import org.unitils.spring.annotation.SpringApplicationContext;
import org.unitils.spring.annotation.SpringBeanByName;

import com.norteksoft.ems.entity.ExpenseReport;
import com.norteksoft.ems.service.ExpenseReportManager;
import com.norteksoft.product.api.ApiFactory;
import com.norteksoft.product.api.entity.TaskPermission;
import com.norteksoft.product.api.entity.WorkflowTask;
import com.norteksoft.product.util.ParameterUtils;
/**
 * 任务权限api部署测试
 * @author Administrator
 *
 */
@Transactional(TransactionMode.ROLLBACK)
@SpringApplicationContext({"applicationContext-test.xml","applicationContext-memcache.xml"})
public class WorkflowPermissionServiceTest extends BaseWorkflowTestCase{
	@SpringBeanByName
	ExpenseReportManager expenseReportManager;
	
	@Test
	public void getActivityPermissionOne(){
		// 准备数据
		deploy("wf_test_8.xml");
		ParameterUtils.setParameters(getPrmt(1L, 65L, "test2", "test2"));
		ExpenseReport dp = new ExpenseReport();
		dp.setCompanyId(1L);
		expenseReportManager.save(dp);
		// 提交第一环节任务
		ApiFactory.getInstanceService().submitInstance("wf_test_8", dp);
		WorkflowTask task=ApiFactory.getTaskService().getActiveTaskByLoginName(dp,"test2");
		Assert.assertNotNull(task);
		//获得当前任务的所有权限
		TaskPermission tp=ApiFactory.getPermissionService().getActivityPermission(task.getId());
		//该环节有创建正文的权限
		Assert.assertTrue(tp.getDocumentCreateable());
	}
	
	@Test
	public void getActivityPermissionTwo(){
		// 准备数据
		deploy("wf_test_8.xml");
		ParameterUtils.setParameters(getPrmt(1L, 65L, "test2", "test2"));
		ExpenseReport dp = new ExpenseReport();
		dp.setCompanyId(1L);
		expenseReportManager.save(dp);
		//获得启用最高版本的流程还未发起时第一环节的所有权限
		TaskPermission tp=ApiFactory.getPermissionService().getActivityPermission("wf_test_8");
		//该环节有创建正文的权限
		Assert.assertTrue(tp.getDocumentCreateable());
	}
	
	@Test
	public void getActivityPermissionThree(){
		// 准备数据
		deploy("wf_test_8.xml");
		ParameterUtils.setParameters(getPrmt(1L, 65L, "test2", "test2"));
		ExpenseReport dp = new ExpenseReport();
		dp.setCompanyId(1L);
		expenseReportManager.save(dp);
		//获得流程还未发起时第一环节的所有权限
		TaskPermission tp=ApiFactory.getPermissionService().getActivityPermission("wf_test_8",1);
		//该环节有创建正文的权限
		Assert.assertTrue(tp.getDocumentCreateable());
	}
	
	@Test
	public void getDocumentPermission(){
		// 准备数据
		deploy("wf_test_8.xml");
		ParameterUtils.setParameters(getPrmt(1L, 65L, "test2", "test2"));
		ExpenseReport dp = new ExpenseReport();
		dp.setCompanyId(1L);
		expenseReportManager.save(dp);
		// 提交第一环节任务
		ApiFactory.getInstanceService().submitInstance("wf_test_8", dp);
		WorkflowTask task=ApiFactory.getTaskService().getActiveTaskByLoginName(dp,"test2");
		Assert.assertNotNull(task);
		//根据当前任务获得环节办理人的正文权限
		String documentPermission=ApiFactory.getPermissionService().getDocumentPermission(task.getId());
		Assert.assertNotNull(documentPermission);
	}
}
