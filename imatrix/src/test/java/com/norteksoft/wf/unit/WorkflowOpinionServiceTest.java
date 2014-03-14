package com.norteksoft.wf.unit;

import java.util.Date;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;
import org.unitils.database.annotations.Transactional;
import org.unitils.database.util.TransactionMode;
import org.unitils.spring.annotation.SpringApplicationContext;
import org.unitils.spring.annotation.SpringBeanByName;

import com.norteksoft.ems.entity.ExpenseReport;
import com.norteksoft.ems.service.ExpenseReportManager;
import com.norteksoft.product.api.ApiFactory;
import com.norteksoft.product.api.entity.Opinion;
import com.norteksoft.product.api.entity.WorkflowTask;
import com.norteksoft.product.util.ParameterUtils;
import com.norteksoft.task.base.enumeration.TaskProcessingMode;

/**
 * 工作流办理意见的api部署测试 
 * @author Administrator
 *
 */
@Transactional(TransactionMode.ROLLBACK)
@SpringApplicationContext({"applicationContext-test.xml","applicationContext-memcache.xml"})
public class WorkflowOpinionServiceTest extends BaseWorkflowTestCase{
	@SpringBeanByName
	ExpenseReportManager expenseReportManager;
	
	@Test
	public void getOpinionsOne(){
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
		// 保存意见,环节的办理模式为当前环节办理模式,且任务名为当前任务名
		Opinion opinion=new Opinion("同意", new Date(), task.getId(), "");
		ApiFactory.getOpinionService().saveOpinion(opinion);
		//查询某个任务的办理意见
		List<Opinion> opinions=ApiFactory.getOpinionService().getOpinions(task.getId());
		Assert.assertEquals(1, opinions.size());
	}
	
	@Test
	public void getOpinionsTwo(){
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
		// 保存意见
		Opinion opinion=new Opinion();
		opinion.setOpinion("同意");
		ApiFactory.getOpinionService().saveOpinion(opinion,task.getId());
		//查询整个流程的办理意见
		List<Opinion> opinions=ApiFactory.getOpinionService().getOpinions(dp);
		Assert.assertEquals(1, opinions.size());
		opinion=opinions.get(0);
		//删除意见
		ApiFactory.getOpinionService().deleteOpinion(opinion.getId());
		//查询整个流程的办理意见
		opinions=ApiFactory.getOpinionService().getOpinions(dp);
		Assert.assertEquals(0, opinions.size());
	}
	
	@Test
	public void getAllOpinionsOne(){
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
		// 保存意见
		Opinion opinion=new Opinion();
		opinion.setOpinion("同意");
		ApiFactory.getOpinionService().saveOpinion(opinion,task.getId());
		//查询整个流程的办理意见
		List<Opinion> opinions=ApiFactory.getOpinionService().getAllOpinions(dp.getWorkflowInfo().getWorkflowId());
		Assert.assertEquals(1, opinions.size());
	}
	
	@Test
	public void getOpinionsThree(){
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
		// 保存意见
		Opinion opinion=new Opinion();
		opinion.setOpinion("同意");
		ApiFactory.getOpinionService().saveOpinion(opinion,task.getId());
		//查询整个实例中具体办理模式的意见
		List<Opinion> opinions=ApiFactory.getOpinionService().getOpinions(dp,TaskProcessingMode.TYPE_APPROVAL);
		//审批式下有一条意见
		Assert.assertEquals(1, opinions.size());
	}
	
	@Test
	public void getAllOpinionsTwo(){
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
		// 保存意见
		Opinion opinion=new Opinion();
		opinion.setOpinion("同意");
		ApiFactory.getOpinionService().saveOpinion(opinion,task.getId());
		//查询整个实例中具体办理模式的意见
		List<Opinion> opinions=ApiFactory.getOpinionService().getAllOpinions(dp.getWorkflowInfo().getWorkflowId(),TaskProcessingMode.TYPE_APPROVAL);
		//审批式下有一条意见
		Assert.assertEquals(1, opinions.size());
	}
	
	@Test
	public void getOpinionsExcludeTaskModeOne(){
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
		// 保存意见
		Opinion opinion=new Opinion();
		opinion.setOpinion("同意");
		ApiFactory.getOpinionService().saveOpinion(opinion,task.getId());
		// 查询整个实例中不是该办理模式的意见
		List<Opinion> opinions=ApiFactory.getOpinionService().getOpinionsExcludeTaskMode(dp,TaskProcessingMode.TYPE_EDIT);
		//审批式下有一条意见
		Assert.assertEquals(1, opinions.size());
	}
	
	@Test
	public void getOpinionsExcludeTaskModeTwo(){
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
		// 保存意见
		Opinion opinion=new Opinion();
		opinion.setOpinion("同意");
		ApiFactory.getOpinionService().saveOpinion(opinion,task.getId());
		// 查询整个实例中不是该办理模式的意见
		List<Opinion> opinions=ApiFactory.getOpinionService().getOpinionsExcludeTaskMode(dp.getWorkflowInfo().getWorkflowId(),TaskProcessingMode.TYPE_EDIT);
		//审批式下有一条意见
		Assert.assertEquals(1, opinions.size());
	}
	
	@Test
	public void getOpinionsFour(){
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
		// 保存意见
		Opinion opinion=new Opinion();
		opinion.setOpinion("同意");
		ApiFactory.getOpinionService().saveOpinion(opinion,task.getId());
		// 查询整个实例中具体环节的意见
		List<Opinion> opinions=ApiFactory.getOpinionService().getOpinions(dp,"审批报销单");
		Assert.assertEquals(1, opinions.size());
	}
	
	@Test
	public void getAllOpinionsThree(){
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
		// 保存意见
		Opinion opinion=new Opinion();
		opinion.setOpinion("同意");
		ApiFactory.getOpinionService().saveOpinion(opinion,task.getId());
		// 查询整个实例中具体环节的意见
		List<Opinion> opinions=ApiFactory.getOpinionService().getAllOpinions(dp.getWorkflowInfo().getWorkflowId(),"审批报销单");
		Assert.assertEquals(1, opinions.size());
	}
	
	@Test
	public void getOpinionsFive(){
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
		// 保存意见
		Opinion opinion=new Opinion();
		opinion.setOpinion("同意");
		ApiFactory.getOpinionService().saveOpinion(opinion,task.getId());
		//查询整个实例中具体环节的意见
		List<Opinion> opinions=ApiFactory.getOpinionService().getOpinions(dp,"审批报销单");
		Assert.assertEquals(1, opinions.size());
	}
	
	@Test
	public void getOpinionsSix(){
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
		// 保存意见
		Opinion opinion=new Opinion();
		opinion.setOpinion("同意");
		ApiFactory.getOpinionService().saveOpinion(opinion,task.getId());
		//查询整个实例中具体环节的意见
		List<Opinion> opinions=ApiFactory.getOpinionService().getOpinions(dp.getWorkflowInfo().getWorkflowId(),"审批报销单");
		Assert.assertEquals(1, opinions.size());
	}
	
	@Test
	public void getOpinionsExcludeTaskNameOne(){
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
		// 保存意见
		Opinion opinion=new Opinion();
		opinion.setOpinion("同意");
		ApiFactory.getOpinionService().saveOpinion(opinion,task.getId());
		//查询整个实例中不是该环节的意见
		List<Opinion> opinions=ApiFactory.getOpinionService().getOpinionsExcludeTaskName(dp,"填写报销单");
		Assert.assertEquals(1, opinions.size());
	}
	
	@Test
	public void getOpinionsExcludeTaskNameTwo(){
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
		// 保存意见
		Opinion opinion=new Opinion();
		opinion.setOpinion("同意");
		ApiFactory.getOpinionService().saveOpinion(opinion,task.getId());
		//查询整个实例中不是该环节的意见
		List<Opinion> opinions=ApiFactory.getOpinionService().getOpinionsExcludeTaskName(dp.getWorkflowInfo().getWorkflowId(),"填写报销单");
		Assert.assertEquals(1, opinions.size());
		//查询具体某条办理意见
		opinion=ApiFactory.getOpinionService().getOpinionById(opinions.get(0).getId());
		Assert.assertNotNull(opinion);
	}
	
	@Test
	public void getOpinionsByCustomFieldOne(){
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
		// 保存意见
		Opinion opinion=new Opinion();
		opinion.setOpinion("同意");
		opinion.setCustomField("类别1");
		ApiFactory.getOpinionService().saveOpinion(opinion,task.getId());
		
		opinion=new Opinion();
		opinion.setOpinion("同意");
		opinion.setCustomField("类别1");
		ApiFactory.getOpinionService().saveOpinion(opinion,task.getId());
		
		opinion=new Opinion();
		opinion.setOpinion("同意");
		opinion.setCustomField("类别2");
		ApiFactory.getOpinionService().saveOpinion(opinion,task.getId());
		
		opinion=new Opinion();
		opinion.setOpinion("同意");
		opinion.setCustomField("类别2");
		ApiFactory.getOpinionService().saveOpinion(opinion,task.getId());
		
		//查询整个实例中“自定义类别”的意见
		List<Opinion> opinions=ApiFactory.getOpinionService().getOpinionsByCustomField(dp,"类别1");
		Assert.assertEquals(2, opinions.size());
	}
	
	@Test
	public void getOpinionsByCustomFieldTwo(){
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
		// 保存意见
		Opinion opinion=new Opinion();
		opinion.setOpinion("同意");
		opinion.setCustomField("类别1");
		ApiFactory.getOpinionService().saveOpinion(opinion,task.getId());
		
		opinion=new Opinion();
		opinion.setOpinion("同意");
		opinion.setCustomField("类别1");
		ApiFactory.getOpinionService().saveOpinion(opinion,task.getId());
		
		opinion=new Opinion();
		opinion.setOpinion("同意");
		opinion.setCustomField("类别2");
		ApiFactory.getOpinionService().saveOpinion(opinion,task.getId());
		
		opinion=new Opinion();
		opinion.setOpinion("同意");
		opinion.setCustomField("类别2");
		ApiFactory.getOpinionService().saveOpinion(opinion,task.getId());
		
		//查询整个实例中“自定义类别”的意见
		List<Opinion> opinions=ApiFactory.getOpinionService().getOpinionsByCustomField(dp.getWorkflowInfo().getWorkflowId(),"类别2");
		Assert.assertEquals(2, opinions.size());
	}
	
	@Test
	public void getOpinionsExcludeCustomFieldOne(){
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
		// 保存意见
		Opinion opinion=new Opinion();
		opinion.setOpinion("同意");
		opinion.setCustomField("类别1");
		ApiFactory.getOpinionService().saveOpinion(opinion,task.getId());
		
		opinion=new Opinion();
		opinion.setOpinion("同意");
		opinion.setCustomField("类别1");
		ApiFactory.getOpinionService().saveOpinion(opinion,task.getId());
		
		opinion=new Opinion();
		opinion.setOpinion("同意");
		opinion.setCustomField("类别2");
		ApiFactory.getOpinionService().saveOpinion(opinion,task.getId());
		
		opinion=new Opinion();
		opinion.setOpinion("同意");
		opinion.setCustomField("类别2");
		ApiFactory.getOpinionService().saveOpinion(opinion,task.getId());
		
		opinion=new Opinion();
		opinion.setOpinion("同意");
		ApiFactory.getOpinionService().saveOpinion(opinion,task.getId());
		
		//查询整个实例中不是“自定义类别”的意见
		List<Opinion> opinions=ApiFactory.getOpinionService().getOpinionsExcludeCustomField(dp,"类别2");
		Assert.assertEquals(3, opinions.size());
	}
	
	@Test
	public void getOpinionsExcludeCustomFieldTwo(){
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
		// 保存意见
		Opinion opinion=new Opinion();
		opinion.setOpinion("同意");
		opinion.setCustomField("类别1");
		ApiFactory.getOpinionService().saveOpinion(opinion,task.getId());
		
		opinion=new Opinion();
		opinion.setOpinion("同意");
		opinion.setCustomField("类别1");
		ApiFactory.getOpinionService().saveOpinion(opinion,task.getId());
		
		opinion=new Opinion();
		opinion.setOpinion("同意");
		opinion.setCustomField("类别2");
		ApiFactory.getOpinionService().saveOpinion(opinion,task.getId());
		
		opinion=new Opinion();
		opinion.setOpinion("同意");
		opinion.setCustomField("类别2");
		ApiFactory.getOpinionService().saveOpinion(opinion,task.getId());
		
		opinion=new Opinion();
		opinion.setOpinion("同意");
		ApiFactory.getOpinionService().saveOpinion(opinion,task.getId());
		
		//查询整个实例中不是“自定义类别”的意见
		List<Opinion> opinions=ApiFactory.getOpinionService().getOpinionsExcludeCustomField(dp.getWorkflowInfo().getWorkflowId(),"类别2");
		Assert.assertEquals(3, opinions.size());
	}
	
	@Test
	public void getOpinionsByTaskCodeOne(){
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
		// 保存意见
		Opinion opinion=new Opinion();
		opinion.setOpinion("同意");
		ApiFactory.getOpinionService().saveOpinion(opinion,task.getId());
		//查询整个实例中具体环节编码的意见
		List<Opinion> opinions=ApiFactory.getOpinionService().getOpinionsByTaskCode(dp,"task2");
		Assert.assertEquals(1, opinions.size());
	}
	
	@Test
	public void getOpinionsByTaskCodeTwo(){
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
		// 保存意见
		Opinion opinion=new Opinion();
		opinion.setOpinion("同意");
		ApiFactory.getOpinionService().saveOpinion(opinion,task.getId());
		//查询整个实例中具体环节编码的意见
		List<Opinion> opinions=ApiFactory.getOpinionService().getOpinionsByTaskCode(dp.getWorkflowInfo().getWorkflowId(),"task2");
		Assert.assertEquals(1, opinions.size());
	}
}
