package com.norteksoft.wf.unit;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.junit.Assert;
import org.junit.Test;
import org.unitils.database.annotations.Transactional;
import org.unitils.database.util.TransactionMode;
import org.unitils.spring.annotation.SpringApplicationContext;
import org.unitils.spring.annotation.SpringBeanByName;

import com.norteksoft.ems.entity.ExpenseReport;
import com.norteksoft.ems.service.ExpenseReportManager;
import com.norteksoft.product.api.ApiFactory;
import com.norteksoft.product.api.entity.WorkflowInstance;
import com.norteksoft.product.api.entity.WorkflowTask;
import com.norteksoft.product.util.ParameterUtils;
import com.norteksoft.task.service.WorkflowTaskManager;
import com.norteksoft.wf.base.enumeration.CompleteTaskTipType;
import com.norteksoft.wf.engine.entity.WorkflowDefinition;
import com.norteksoft.wf.engine.service.TaskService;
import com.norteksoft.wf.engine.service.WorkflowDefinitionManager;
import com.norteksoft.wf.engine.service.WorkflowInstanceManager;

/**
 *  工作流里流程定义
 *
 */
@Transactional(TransactionMode.ROLLBACK)
@SpringApplicationContext({"applicationContext-test.xml","applicationContext-memcache.xml"})
public class WorkflowDefinitionTest extends BaseWorkflowTestCase{
	
	@SpringBeanByName
	WorkflowDefinitionManager workflowDefinitionManager;
	
	@SpringBeanByName
	WorkflowInstanceManager workflowInstanceManager;
	
	@SpringBeanByName
	ExpenseReportManager expenseReportManager;
	
	@SpringBeanByName
	TaskService taskService;
	
	@SpringBeanByName
	WorkflowTaskManager workflowTaskManager;
	
	//增加
	@Test
	public void saveWorkflowDefinition(){
		String fileContent = readFileContent("wf_test_8.xml");
		Long id = workflowDefinitionManager.saveWorkflowDefinition(null, 1L, fileContent,1L,7L);
		Assert.assertNotNull(id);
	}
	
	
	//修改
	@Test
	public void updateWfDefVersion(){
		String fileContent = readFileContent("wf_test_8.xml");
		Long id = workflowDefinitionManager.saveWorkflowDefinition(null, 1L, fileContent,1L,7L);
		Long result = workflowDefinitionManager.updateWfDefVersion(id, 1L, fileContent,1L,7L);
		Assert.assertNotNull(result);
	}
	
	//修改基本属性
	@Test
	public void saveWfBasic(){
		String fileContent = readFileContent("wf_test_8.xml");
		Long id = workflowDefinitionManager.saveWorkflowDefinition(null, 1L, fileContent,1L,7L);
		WorkflowDefinition wf = workflowDefinitionManager.getWfDefinitionByWfdId(id);
		wf.setName("wangjing_wf_testtttt");
		workflowDefinitionManager.saveWfBasic(wf,"test");
		WorkflowDefinition result = workflowDefinitionManager.getWfDefinitionByWfdId(id);
		Assert.assertEquals("wangjing_wf_testtttt", result.getName());
	}
	
	//删除
	@Test
	public void delete(){
		List<Long> wfIds = new ArrayList<Long>();
		String fileContent = readFileContent("wf_test_8.xml");
		wfIds.add(workflowDefinitionManager.saveWorkflowDefinition(null, 1L, fileContent,1L,7L));
		wfIds.add(workflowDefinitionManager.saveWorkflowDefinition(null, 1L, fileContent,1L,7L));
		int result = workflowDefinitionManager.deleteWfDefinitions(wfIds);
		Assert.assertEquals(2,result);
	}
	
	//启用、禁用
	@Test
	public void deployProcess() throws UnsupportedEncodingException{
		String fileContent = readFileContent("wf_test_8.xml");
		Long id = workflowDefinitionManager.saveWorkflowDefinition(null, 1L, fileContent,1L,7L);
		String result = workflowDefinitionManager.deployProcess(id);
		Assert.assertEquals("草稿 -> 启用",result);
		
		WorkflowDefinition wf = workflowDefinitionManager.getWfDefinitionByWfdId(id);
		String result2 = workflowDefinitionManager.deployProcess(id);
		Assert.assertEquals("启用 -> 禁用",result2);
	}
	
	//取消流程
	@Test
	public void endWorkflowInstance() {
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
		Set<Long> workflowIds = new HashSet<Long>();
		workflowIds.add(instance.getId());
		String result = workflowInstanceManager.endWorkflowInstance(workflowIds);
		Assert.assertEquals("1个已取消", result);
	}
	
	//暂停流程
	@Test
	public void pauseWorkflowInstance() {
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
		Set<Long> workflowIds = new HashSet<Long>();
		workflowIds.add(instance.getId());
		String result = workflowInstanceManager.pauseWorkflowInstance(workflowIds);
		Assert.assertEquals("1个已暂停", result);
	}
	
	//继续流程
	@Test
	public void continueWorkflowInstance() {
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
		Set<Long> workflowIds = new HashSet<Long>();
		workflowIds.add(instance.getId());
		String result1 = workflowInstanceManager.pauseWorkflowInstance(workflowIds);
		Assert.assertEquals("1个已暂停", result1);
		String result = workflowInstanceManager.continueWorkflowInstance(workflowIds);
		Assert.assertEquals("1个已继续", result);
	}
	
	//强制结束流程
	@Test
	public void compelEndWorkflowInstance() {
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
		Set<Long> workflowIds = new HashSet<Long>();
		workflowIds.add(instance.getId());
		String result = workflowInstanceManager.compelEndWorkflowInstance(workflowIds);
		Assert.assertEquals("1个已强制结束", result);
	}
	
	//更改办理人
	@Test
	public void changeTransactor() {
		// 准备数据
		deploy("wf_test_8.xml");
		ParameterUtils.setParameters(getPrmt(1L, 65L, "test2", "test2"));
		ExpenseReport dp = new ExpenseReport();
		dp.setCompanyId(1L);
		expenseReportManager.save(dp);
		// 提交第一环节任务
		ApiFactory.getInstanceService().submitInstance("wf_test_8", dp);
		List<WorkflowTask> tasks= ApiFactory.getTaskService().getActivityTasks(dp);
		Assert.assertNotNull(tasks.get(0));
		taskService.changeTransactor(tasks.get(0).getId(), "ldx");
		WorkflowTask result = ApiFactory.getTaskService().getActiveTaskByLoginName(dp, "ldx");
		Assert.assertEquals("ldx", result.getTransactor());
	}
	
	//增加办理人
	@Test
	public void addTransactor() {
		// 准备数据
		deploy("wf_test_8.xml");
		ParameterUtils.setParameters(getPrmt(1L, 65L, "test2", "test2"));
		ExpenseReport dp = new ExpenseReport();
		dp.setCompanyId(1L);
		expenseReportManager.save(dp);
		// 提交第一环节任务
		ApiFactory.getInstanceService().submitInstance("wf_test_8", dp);
		List<WorkflowTask> tasks= ApiFactory.getTaskService().getActivityTasks(dp);
		Assert.assertNotNull(tasks.get(0));
		List<String> transactors = new ArrayList<String>();
		transactors.add(33+"");
		taskService.addTransactor(tasks.get(0).getProcessInstanceId(), transactors);
		WorkflowTask result = ApiFactory.getTaskService().getActiveTaskByLoginName(dp, "ldx");
		Assert.assertEquals("ldx", result.getTransactor());
	}
	
	//减少办理人
	@Test
	public void delTransactor() {
		// 准备数据
		deploy("wf_test_8.xml");
		ParameterUtils.setParameters(getPrmt(1L, 65L, "test2", "test2"));
		ExpenseReport dp = new ExpenseReport();
		dp.setCompanyId(1L);
		expenseReportManager.save(dp);
		// 提交第一环节任务
		ApiFactory.getInstanceService().submitInstance("wf_test_8", dp);
		List<WorkflowTask> tasks= ApiFactory.getTaskService().getActivityTasks(dp);
		Assert.assertNotNull(tasks.get(0));
		List<String> transactors = new ArrayList<String>();
		transactors.add(33+"");
		taskService.addTransactor(tasks.get(0).getProcessInstanceId(), transactors);
		WorkflowTask result1 = ApiFactory.getTaskService().getActiveTaskByLoginName(dp, "ldx");
		Assert.assertEquals("ldx", result1.getTransactor());
		List<Long> transactorIds = new ArrayList<Long>();
		transactorIds.add(33l);
		//减少办理人
		taskService.delTransactor(tasks.get(0).getProcessInstanceId(),new ArrayList<String>(), transactorIds);
		WorkflowTask result = ApiFactory.getTaskService().getActiveTaskByLoginName(dp, "ldx");
		Assert.assertTrue(result==null);
	}
	
	//环节跳转
	@Test
	public void goback() {
		// 准备数据
		deploy("wf_test_8.xml");
		ParameterUtils.setParameters(getPrmt(1L, 65L, "test2", "test2"));
		ExpenseReport dp = new ExpenseReport();
		dp.setCompanyId(1L);
		expenseReportManager.save(dp);
		// 提交第一环节任务
		ApiFactory.getInstanceService().submitInstance("wf_test_8", dp);
		List<WorkflowTask> tasks= ApiFactory.getTaskService().getActivityTasks(dp);
		Assert.assertNotNull(tasks.get(0));
		com.norteksoft.wf.engine.entity.WorkflowInstance workflowInstance = workflowInstanceManager.getWorkflowInstance(tasks.get(0).getProcessInstanceId());
		CompleteTaskTipType result = taskService.taskJump(workflowInstance, "审批报销单",new ArrayList<String>(),null,false);
		Assert.assertEquals(CompleteTaskTipType.OK, result);
	}
	//批量移除任务
	@Test
	public void delTasksBatch() {
		// 准备数据
		deploy("wf_test_8.xml");
		ParameterUtils.setParameters(getPrmt(1L, 65L, "test2", "test2"));
		ExpenseReport dp = new ExpenseReport();
		dp.setCompanyId(1L);
		expenseReportManager.save(dp);
		// 提交第一环节任务
		ApiFactory.getInstanceService().submitInstance("wf_test_8", dp);
		List<WorkflowTask> tasks= ApiFactory.getTaskService().getActivityTasks(dp);
		Assert.assertNotNull(tasks.get(0));
		List<Long> taskIds = new ArrayList<Long>();
		taskIds.add(tasks.get(0).getId());
		Map<String, List<com.norteksoft.task.entity.WorkflowTask>> chooseTasks = taskService.deleteTasks(taskIds);
		Assert.assertNotNull(chooseTasks.get("JUST_ONE"));
	}
	//批量环节跳转
	@Test
	public void volumeBack() {
		// 准备数据
		deploy("wf_test_8.xml");
		ParameterUtils.setParameters(getPrmt(1L, 65L, "test2", "test2"));
		ExpenseReport dp = new ExpenseReport();
		dp.setCompanyId(1L);
		expenseReportManager.save(dp);
		// 提交第一环节任务
		ApiFactory.getInstanceService().submitInstance("wf_test_8", dp);
		List<WorkflowTask> tasks= ApiFactory.getTaskService().getActivityTasks(dp);
		Assert.assertNotNull(tasks.get(0));
		com.norteksoft.wf.engine.entity.WorkflowInstance workflowInstance = workflowInstanceManager.getWorkflowInstance(tasks.get(0).getProcessInstanceId());
		String[] wfids={workflowInstance.getProcessInstanceId()};
		CompleteTaskTipType result = taskService.taskJumps(wfids, "审批报销单",new ArrayList<String>(),"volumeBack");
		Assert.assertEquals(CompleteTaskTipType.OK, result);
	}
	//删除
	@Test
	public void deleteWorkflowInstances() {
		// 准备数据
		deploy("wf_test_8.xml");
		ParameterUtils.setParameters(getPrmt(1L, 65L, "test2", "test2"));
		ExpenseReport dp = new ExpenseReport();
		dp.setCompanyId(1L);
		expenseReportManager.save(dp);
		// 提交第一环节任务
		ApiFactory.getInstanceService().submitInstance("wf_test_8", dp);
		List<WorkflowTask> tasks= ApiFactory.getTaskService().getActivityTasks(dp);
		Assert.assertNotNull(tasks.get(0));
		com.norteksoft.wf.engine.entity.WorkflowInstance workflowInstance = workflowInstanceManager.getWorkflowInstance(tasks.get(0).getProcessInstanceId());
		Set<com.norteksoft.wf.engine.entity.WorkflowInstance> workflowInstances = new HashSet<com.norteksoft.wf.engine.entity.WorkflowInstance>();
		workflowInstances.add(workflowInstance);
		String result = workflowInstanceManager.deleteWorkflowInstances(workflowInstances);
		Assert.assertEquals("删除成功1个，失败0个。失败是因为不能单独删除子流程，但删除主流程时会自动把关联的子流程删除。", result);
	}
}
