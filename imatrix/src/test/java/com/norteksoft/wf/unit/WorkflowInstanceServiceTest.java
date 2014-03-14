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
import com.norteksoft.product.api.entity.WorkflowDefinition;
import com.norteksoft.product.api.entity.WorkflowInstance;
import com.norteksoft.product.util.ParameterUtils;
import com.norteksoft.wf.base.enumeration.CompleteTaskTipType;

/**
 * 公开提供给用户使用的工作流实例的api部署测试
 *
 */
@Transactional(TransactionMode.ROLLBACK)
@SpringApplicationContext({"applicationContext-test.xml","applicationContext-memcache.xml"})
public class WorkflowInstanceServiceTest extends BaseWorkflowTestCase{
	@SpringBeanByName
	ExpenseReportManager expenseReportManager;
	
	@Test
	public void startInstanceOne(){
		// 准备数据
		deploy("wf_test_8.xml");
		ParameterUtils.setParameters(getPrmt(1L, 65L, "test2", "test2"));
		ExpenseReport dp = new ExpenseReport();
		dp.setCompanyId(1L);
		expenseReportManager.save(dp);
		// 启动流程
		ApiFactory.getInstanceService().startInstance("wf_test_8", dp);
		// 提交第一环节任务
		CompleteTaskTipType cttt = ApiFactory.getInstanceService().submitInstance("wf_test_8", dp);
		Assert.assertEquals(CompleteTaskTipType.OK, cttt);
		//流程未结束
		boolean isEnd=ApiFactory.getInstanceService().isInstanceComplete(dp);
		Assert.assertFalse(isEnd);
		//当前环节办理人没有权删除流程实例
		boolean isDeleteRole=ApiFactory.getInstanceService().canDeleteInstanceInTask(dp,"审批报销单");
		Assert.assertFalse(isDeleteRole);
	}
	
	@Test
	public void startInstanceTwo(){
		// 准备数据
		deploy("wf_test_8.xml");
		ParameterUtils.setParameters(getPrmt(1L, 65L, "test2", "test2"));
		ExpenseReport dp = new ExpenseReport();
		dp.setCompanyId(1L);
		expenseReportManager.save(dp);
		// 启动流程
		ApiFactory.getInstanceService().startInstance("wf_test_8",1,dp);
		// 提交第一环节任务
		CompleteTaskTipType cttt = ApiFactory.getInstanceService().submitInstance("wf_test_8",1, dp);
		Assert.assertEquals(CompleteTaskTipType.OK, cttt);
		//根据实例id查询流程实例
		WorkflowInstance instance=ApiFactory.getInstanceService().getInstance(dp.getWorkflowInfo().getWorkflowId());
		Assert.assertNotNull(instance);
		//删除流程实例
		ApiFactory.getInstanceService().deleteInstance(dp);
	}
	
	@Test
	public void startInstanceThree(){
		// 准备数据
		deploy("wf_test_8.xml");
		ParameterUtils.setParameters(getPrmt(1L, 65L, "test2", "test2"));
		ExpenseReport dp = new ExpenseReport();
		dp.setCompanyId(1L);
		expenseReportManager.save(dp);
		WorkflowDefinition definition=ApiFactory.getDefinitionService().getEnabledHighestVersionWorkflowDefinition("wf_test_8");
		// 启动流程
		ApiFactory.getInstanceService().startInstance(definition.getId(),dp);
		// 提交第一环节任务
		CompleteTaskTipType cttt = ApiFactory.getInstanceService().submitInstance(definition.getId(),dp);
		Assert.assertEquals(CompleteTaskTipType.OK, cttt);
	}
	
}
