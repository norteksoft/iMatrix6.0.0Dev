package com.norteksoft.wf.unit;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.dom4j.Document;
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
import com.norteksoft.product.util.ContextUtils;
import com.norteksoft.product.util.ParameterUtils;
import com.norteksoft.product.util.ThreadParameters;
import com.norteksoft.task.base.enumeration.TaskProcessingMode;
import com.norteksoft.task.base.enumeration.TaskProcessingResult;
import com.norteksoft.wf.base.enumeration.CompleteTaskTipType;
import com.norteksoft.wf.base.enumeration.ProcessState;
import com.norteksoft.wf.base.enumeration.TrustRecordState;
import com.norteksoft.wf.base.utils.DocumentParameterUtils;
import com.norteksoft.wf.base.utils.DocumentThreadParameters;
import com.norteksoft.wf.base.utils.Dom4jUtils;
import com.norteksoft.wf.engine.entity.TrustRecord;
import com.norteksoft.wf.engine.entity.WorkflowDefinition;
import com.norteksoft.wf.engine.service.DelegateMainManager;
import com.norteksoft.wf.engine.service.WorkflowDefinitionManager;

/**
 * 公开提供给用户使用的工作流任务api部署测试
 * @author Administrator
 *
 */
@Transactional(TransactionMode.ROLLBACK)
@SpringApplicationContext({"applicationContext-test.xml","applicationContext-memcache.xml"})
public class WorkflowTaskServiceTest extends BaseWorkflowTestCase{
	@SpringBeanByName
	ExpenseReportManager expenseReportManager;
	@SpringBeanByName
	DelegateMainManager delegateMainManager;
	@SpringBeanByName
	WorkflowDefinitionManager workflowDefinitionManager;
	
	@Test
	public void getTask(){
		// 准备数据
		deploy("wf_test_8.xml");
		ParameterUtils.setParameters(getPrmt(1L, 65L, "test2", "test2"));
		ExpenseReport dp = new ExpenseReport();
		dp.setCompanyId(1L);
		expenseReportManager.save(dp);
		// 提交第一环节任务
		ApiFactory.getInstanceService().submitInstance("wf_test_8", dp);
		WorkflowTask task= ApiFactory.getTaskService().getTask(dp.getWorkflowInfo().getFirstTaskId());
		Assert.assertNotNull(task);
	}
	
	@Test
	public void completeWorkflowTaskOne(){
		// 准备数据
		deploy("wf_test_8.xml");
		ParameterUtils.setParameters(getPrmt(1L, 65L, "test2", "test2"));
		ExpenseReport dp = new ExpenseReport();
		dp.setCompanyId(1L);
		expenseReportManager.save(dp);
		// 提交第一环节任务
		ApiFactory.getInstanceService().submitInstance("wf_test_8", dp);
		WorkflowTask task=ApiFactory.getTaskService().getActiveTaskByLoginName(dp,"test2");
		CompleteTaskTipType ctt=ApiFactory.getTaskService().completeWorkflowTask(task.getId(),TaskProcessingResult.APPROVE,"");
		Assert.assertEquals(CompleteTaskTipType.RETURN_URL, ctt);
	}
	
	@Test
	public void completeWorkflowTaskTwo(){
		// 准备数据
		deploy("wf_test_8.xml");
		ParameterUtils.setParameters(getPrmt(1L, 65L, "test2", "test2"));
		ExpenseReport dp = new ExpenseReport();
		dp.setCompanyId(1L);
		expenseReportManager.save(dp);
		// 提交第一环节任务
		ApiFactory.getInstanceService().submitInstance("wf_test_8", dp);
		WorkflowTask task=ApiFactory.getTaskService().getActiveTaskByLoginName(dp,"test2");
		CompleteTaskTipType ctt=ApiFactory.getTaskService().completeWorkflowTask(task.getId(),TaskProcessingResult.APPROVE);
		Assert.assertEquals(CompleteTaskTipType.RETURN_URL, ctt);
	}
	
	@Test
	public void completeWorkflowTaskThree(){
		// 准备数据
		deploy("wf_test_8.xml");
		ParameterUtils.setParameters(getPrmt(1L, 65L, "test2", "test2"));
		ExpenseReport dp = new ExpenseReport();
		dp.setCompanyId(1L);
		expenseReportManager.save(dp);
		// 提交第一环节任务
		ApiFactory.getInstanceService().submitInstance("wf_test_8", dp);
		WorkflowTask task=ApiFactory.getTaskService().getActiveTaskByLoginName(dp,"test2");
		CompleteTaskTipType ctt=ApiFactory.getTaskService().completeWorkflowTask(task,TaskProcessingResult.APPROVE,"");
		Assert.assertEquals(CompleteTaskTipType.RETURN_URL, ctt);
	}
	
	@Test
	public void completeWorkflowTaskFour(){
		// 准备数据
		deploy("wf_test_8.xml");
		ParameterUtils.setParameters(getPrmt(1L, 65L, "test2", "test2"));
		ExpenseReport dp = new ExpenseReport();
		dp.setCompanyId(1L);
		expenseReportManager.save(dp);
		// 提交第一环节任务
		ApiFactory.getInstanceService().submitInstance("wf_test_8", dp);
		WorkflowTask task=ApiFactory.getTaskService().getActiveTaskByLoginName(dp,"test2");
		CompleteTaskTipType ctt=ApiFactory.getTaskService().completeWorkflowTask(task,TaskProcessingResult.APPROVE);
		Assert.assertEquals(CompleteTaskTipType.RETURN_URL, ctt);
	}
	
	@Test
	public void completeInteractiveWorkflowTaskOne(){
		// 准备数据
		deploy("wf_test_8.xml");
		ParameterUtils.setParameters(getPrmt(1L, 65L, "test2", "test2"));
		ExpenseReport dp = new ExpenseReport();
		dp.setCompanyId(1L);
		expenseReportManager.save(dp);
		// 提交第一环节任务
		ApiFactory.getInstanceService().submitInstance("wf_test_8", dp);
		WorkflowTask task=ApiFactory.getTaskService().getActiveTaskByLoginName(dp,"test2");
		CompleteTaskTipType ctt=ApiFactory.getTaskService().completeWorkflowTask(task.getId(),TaskProcessingResult.APPROVE);
		//办理人设置为“上一环节办理人指定”
		Assert.assertEquals(CompleteTaskTipType.RETURN_URL, ctt);
		ctt=ApiFactory.getTaskService().completeInteractiveWorkflowTask(task.getId(),"");
		Assert.assertEquals(CompleteTaskTipType.OK, ctt);
	}
	
	@Test
	public void completeInteractiveWorkflowTaskTwo(){
		// 准备数据
		deploy("wf_test_8.xml");
		ParameterUtils.setParameters(getPrmt(1L, 65L, "test2", "test2"));
		ExpenseReport dp = new ExpenseReport();
		dp.setCompanyId(1L);
		expenseReportManager.save(dp);
		// 提交第一环节任务
		ApiFactory.getInstanceService().submitInstance("wf_test_8", dp);
		WorkflowTask task=ApiFactory.getTaskService().getActiveTaskByLoginName(dp,"test2");
		CompleteTaskTipType ctt=ApiFactory.getTaskService().completeWorkflowTask(task.getId(),TaskProcessingResult.APPROVE);
		//办理人设置为“上一环节办理人指定”
		Assert.assertEquals(CompleteTaskTipType.RETURN_URL, ctt);
		List<String> loginName=new ArrayList<String>();
		loginName.add("test2");
		ctt=ApiFactory.getTaskService().completeInteractiveWorkflowTask(task.getId(),loginName,"");
		Assert.assertEquals(CompleteTaskTipType.OK, ctt);
	}
	
	@Test
	public void completeInteractiveWorkflowTaskThree(){
		// 准备数据
		deploy("wf_test_8.xml");
		ParameterUtils.setParameters(getPrmt(1L, 65L, "test2", "test2"));
		ExpenseReport dp = new ExpenseReport();
		dp.setCompanyId(1L);
		expenseReportManager.save(dp);
		// 提交第一环节任务
		ApiFactory.getInstanceService().submitInstance("wf_test_8", dp);
		WorkflowTask task=ApiFactory.getTaskService().getActiveTaskByLoginName(dp,"test2");
		CompleteTaskTipType ctt=ApiFactory.getTaskService().completeWorkflowTask(task.getId(),TaskProcessingResult.APPROVE);
		//办理人设置为“上一环节办理人指定”
		Assert.assertEquals(CompleteTaskTipType.RETURN_URL, ctt);
		ctt=ApiFactory.getTaskService().completeInteractiveWorkflowTask(task.getId(),"","test2");
		Assert.assertEquals(CompleteTaskTipType.OK, ctt);
	}
	
	@Test
	public void completeInteractiveWorkflowTaskFour(){
		// 准备数据
		deploy("wf_test_8.xml");
		ParameterUtils.setParameters(getPrmt(1L, 65L, "test2", "test2"));
		ExpenseReport dp = new ExpenseReport();
		dp.setCompanyId(1L);
		expenseReportManager.save(dp);
		// 提交第一环节任务
		ApiFactory.getInstanceService().submitInstance("wf_test_8", dp);
		WorkflowTask task=ApiFactory.getTaskService().getActiveTaskByLoginName(dp,"test2");
		CompleteTaskTipType ctt=ApiFactory.getTaskService().completeWorkflowTask(task.getId(),TaskProcessingResult.APPROVE);
		//办理人设置为“上一环节办理人指定”
		Assert.assertEquals(CompleteTaskTipType.RETURN_URL, ctt);
		ctt=ApiFactory.getTaskService().completeInteractiveWorkflowTask(task,"");
		Assert.assertEquals(CompleteTaskTipType.OK, ctt);
	}
	
	@Test
	public void completeInteractiveWorkflowTaskFive(){
		// 准备数据
		deploy("wf_test_8.xml");
		ParameterUtils.setParameters(getPrmt(1L, 65L, "test2", "test2"));
		ExpenseReport dp = new ExpenseReport();
		dp.setCompanyId(1L);
		expenseReportManager.save(dp);
		// 提交第一环节任务
		ApiFactory.getInstanceService().submitInstance("wf_test_8", dp);
		WorkflowTask task=ApiFactory.getTaskService().getActiveTaskByLoginName(dp,"test2");
		CompleteTaskTipType ctt=ApiFactory.getTaskService().completeWorkflowTask(task.getId(),TaskProcessingResult.APPROVE);
		//办理人设置为“上一环节办理人指定”
		Assert.assertEquals(CompleteTaskTipType.RETURN_URL, ctt);
		List<String> loginName=new ArrayList<String>();
		loginName.add("test2");
		ctt=ApiFactory.getTaskService().completeInteractiveWorkflowTask(task,loginName,"");
		Assert.assertEquals(CompleteTaskTipType.OK, ctt);
	}
	
	@Test
	public void completeInteractiveWorkflowTaskSix(){
		// 准备数据
		deploy("wf_test_8.xml");
		ParameterUtils.setParameters(getPrmt(1L, 65L, "test2", "test2"));
		ExpenseReport dp = new ExpenseReport();
		dp.setCompanyId(1L);
		expenseReportManager.save(dp);
		// 提交第一环节任务
		ApiFactory.getInstanceService().submitInstance("wf_test_8", dp);
		WorkflowTask task=ApiFactory.getTaskService().getActiveTaskByLoginName(dp,"test2");
		CompleteTaskTipType ctt=ApiFactory.getTaskService().completeWorkflowTask(task.getId(),TaskProcessingResult.APPROVE);
		//办理人设置为“上一环节办理人指定”
		Assert.assertEquals(CompleteTaskTipType.RETURN_URL, ctt);
		ctt=ApiFactory.getTaskService().completeInteractiveWorkflowTask(task,"","test2");
		Assert.assertEquals(CompleteTaskTipType.OK, ctt);
	}
	
	@Test
	public void getNextTasksCandidates(){
		// 准备数据
		deploy("wf_test_8.xml");
		ParameterUtils.setParameters(getPrmt(1L, 65L, "test2", "test2"));
		ExpenseReport dp = new ExpenseReport();
		dp.setCompanyId(1L);
		expenseReportManager.save(dp);
		// 提交第一环节任务
		ApiFactory.getInstanceService().submitInstance("wf_test_8", dp);
		WorkflowTask task=ApiFactory.getTaskService().getActiveTaskByLoginName(dp,"test2");
		CompleteTaskTipType ctt=ApiFactory.getTaskService().completeWorkflowTask(task.getId(),TaskProcessingResult.APPROVE);
		//办理人设置为“上一环节办理人指定”
		Assert.assertEquals(CompleteTaskTipType.RETURN_URL, ctt);
		Map<String[], List<String[]>> map=ApiFactory.getTaskService().getNextTasksCandidates(task.getId());
		Assert.assertEquals(1,map.size());
	}
	
	@Test
	public void getFormIdByTask(){
		// 准备数据
		deploy("wf_test_8.xml");
		ParameterUtils.setParameters(getPrmt(1L, 65L, "test2", "test2"));
		ExpenseReport dp = new ExpenseReport();
		dp.setCompanyId(1L);
		expenseReportManager.save(dp);
		// 提交第一环节任务
		ApiFactory.getInstanceService().submitInstance("wf_test_8", dp);
		WorkflowTask task=ApiFactory.getTaskService().getActiveTaskByLoginName(dp,"test2");
		Long formId=ApiFactory.getTaskService().getFormIdByTask(task);
		Assert.assertEquals(dp.getWorkflowInfo().getFormId(),formId);
	}
	
	@Test
	public void getDataIdByTask(){
		// 准备数据
		deploy("wf_test_8.xml");
		ParameterUtils.setParameters(getPrmt(1L, 65L, "test2", "test2"));
		ExpenseReport dp = new ExpenseReport();
		dp.setCompanyId(1L);
		expenseReportManager.save(dp);
		// 提交第一环节任务
		ApiFactory.getInstanceService().submitInstance("wf_test_8", dp);
		WorkflowTask task=ApiFactory.getTaskService().getActiveTaskByLoginName(dp,"test2");
		Long dataId=ApiFactory.getTaskService().getDataIdByTask(task);
		Assert.assertEquals(dp.getId(),dataId);
	}
	
	@Test
	public void addSignerOne(){
		// 准备数据
		deploy("wf_test_8.xml");
		ParameterUtils.setParameters(getPrmt(1L, 65L, "test2", "test2"));
		ExpenseReport dp = new ExpenseReport();
		dp.setCompanyId(1L);
		dp.setSignLoginNames("test2");
		expenseReportManager.save(dp);
		// 提交第一环节任务
		ApiFactory.getInstanceService().submitInstance("wf_test_8", dp);
		WorkflowTask task=ApiFactory.getTaskService().getActiveTaskByLoginName(dp,"test2");
		CompleteTaskTipType ctt=ApiFactory.getTaskService().completeWorkflowTask(task.getId(),TaskProcessingResult.APPROVE);
		//办理人设置为“上一环节办理人指定”
		Assert.assertEquals(CompleteTaskTipType.RETURN_URL, ctt);
		ctt=ApiFactory.getTaskService().completeInteractiveWorkflowTask(task.getId(),"","test2");
		task=ApiFactory.getTaskService().getActiveTaskByLoginName(dp,"test2");
		ctt=ApiFactory.getTaskService().completeWorkflowTask(task.getId(),TaskProcessingResult.SUBMIT);
		task=ApiFactory.getTaskService().getActiveTaskByLoginName(dp,"test2");
		List<String> loginName=new ArrayList<String>();
		loginName.add("ldx");
		//加签
		ApiFactory.getTaskService().addSigner(task.getId(),loginName);
		task=ApiFactory.getTaskService().getActiveTaskByLoginName(dp,"ldx");
		Assert.assertNotNull(task);
	}
	
	@Test
	public void addSignerTwo(){
		// 准备数据
		deploy("wf_test_8.xml");
		ParameterUtils.setParameters(getPrmt(1L, 65L, "test2", "test2"));
		ExpenseReport dp = new ExpenseReport();
		dp.setCompanyId(1L);
		dp.setSignLoginNames("test2");
		expenseReportManager.save(dp);
		// 提交第一环节任务
		ApiFactory.getInstanceService().submitInstance("wf_test_8", dp);
		WorkflowTask task=ApiFactory.getTaskService().getActiveTaskByLoginName(dp,"test2");
		CompleteTaskTipType ctt=ApiFactory.getTaskService().completeWorkflowTask(task.getId(),TaskProcessingResult.APPROVE);
		//办理人设置为“上一环节办理人指定”
		Assert.assertEquals(CompleteTaskTipType.RETURN_URL, ctt);
		ctt=ApiFactory.getTaskService().completeInteractiveWorkflowTask(task.getId(),"","test2");
		task=ApiFactory.getTaskService().getActiveTaskByLoginName(dp,"test2");
		ctt=ApiFactory.getTaskService().completeWorkflowTask(task.getId(),TaskProcessingResult.SUBMIT);
		task=ApiFactory.getTaskService().getActiveTaskByLoginName(dp,"test2");
		//加签
		ApiFactory.getTaskService().addSigner(task.getId(),"ldx");
		task=ApiFactory.getTaskService().getActiveTaskByLoginName(dp,"ldx");
		Assert.assertNotNull(task);
	}
	
	@Test
	public void removeSignerOne(){
		// 准备数据
		deploy("wf_test_8.xml");
		ParameterUtils.setParameters(getPrmt(1L, 65L, "test2", "test2"));
		ExpenseReport dp = new ExpenseReport();
		dp.setCompanyId(1L);
		dp.setSignLoginNames("test2,ldx");
		expenseReportManager.save(dp);
		// 提交第一环节任务
		ApiFactory.getInstanceService().submitInstance("wf_test_8", dp);
		WorkflowTask task=ApiFactory.getTaskService().getActiveTaskByLoginName(dp,"test2");
		CompleteTaskTipType ctt=ApiFactory.getTaskService().completeWorkflowTask(task.getId(),TaskProcessingResult.APPROVE);
		//办理人设置为“上一环节办理人指定”
		Assert.assertEquals(CompleteTaskTipType.RETURN_URL, ctt);
		ctt=ApiFactory.getTaskService().completeInteractiveWorkflowTask(task.getId(),"","test2");
		
		task=ApiFactory.getTaskService().getActiveTaskByLoginName(dp,"test2");
		ctt=ApiFactory.getTaskService().completeWorkflowTask(task.getId(),TaskProcessingResult.SUBMIT);
		
		task=ApiFactory.getTaskService().getActiveTaskByLoginName(dp,"ldx");
		Assert.assertNotNull(task);
		List<String> loginName=new ArrayList<String>();
		loginName.add("ldx");
		//减签
		ApiFactory.getTaskService().removeSigner(task.getId(),loginName);
		task=ApiFactory.getTaskService().getActiveTaskByLoginName(dp,"ldx");
		Assert.assertNull(task);
	}
	
	@Test
	public void removeSignerTwo(){
		// 准备数据
		deploy("wf_test_8.xml");
		ParameterUtils.setParameters(getPrmt(1L, 65L, "test2", "test2"));
		ExpenseReport dp = new ExpenseReport();
		dp.setCompanyId(1L);
		dp.setSignLoginNames("test2,ldx");
		expenseReportManager.save(dp);
		// 提交第一环节任务
		ApiFactory.getInstanceService().submitInstance("wf_test_8", dp);
		WorkflowTask task=ApiFactory.getTaskService().getActiveTaskByLoginName(dp,"test2");
		CompleteTaskTipType ctt=ApiFactory.getTaskService().completeWorkflowTask(task.getId(),TaskProcessingResult.APPROVE);
		//办理人设置为“上一环节办理人指定”
		Assert.assertEquals(CompleteTaskTipType.RETURN_URL, ctt);
		ctt=ApiFactory.getTaskService().completeInteractiveWorkflowTask(task.getId(),"","test2");
		
		task=ApiFactory.getTaskService().getActiveTaskByLoginName(dp,"test2");
		ctt=ApiFactory.getTaskService().completeWorkflowTask(task.getId(),TaskProcessingResult.SUBMIT);
		
		task=ApiFactory.getTaskService().getActiveTaskByLoginName(dp,"ldx");
		Assert.assertNotNull(task);
		//减签
		ApiFactory.getTaskService().removeSigner(task.getId(),"ldx");
		task=ApiFactory.getTaskService().getActiveTaskByLoginName(dp,"ldx");
		Assert.assertNull(task);
	}
	
	@Test
	public void getCountersignTransactors(){
		// 准备数据
		deploy("wf_test_8.xml");
		ParameterUtils.setParameters(getPrmt(1L, 65L, "test2", "test2"));
		ExpenseReport dp = new ExpenseReport();
		dp.setCompanyId(1L);
		dp.setSignLoginNames("test2,ldx");
		expenseReportManager.save(dp);
		// 提交第一环节任务
		ApiFactory.getInstanceService().submitInstance("wf_test_8", dp);
		WorkflowTask task=ApiFactory.getTaskService().getActiveTaskByLoginName(dp,"test2");
		CompleteTaskTipType ctt=ApiFactory.getTaskService().completeWorkflowTask(task.getId(),TaskProcessingResult.APPROVE);
		//办理人设置为“上一环节办理人指定”
		Assert.assertEquals(CompleteTaskTipType.RETURN_URL, ctt);
		ctt=ApiFactory.getTaskService().completeInteractiveWorkflowTask(task.getId(),"","test2");
		task=ApiFactory.getTaskService().getActiveTaskByLoginName(dp,"test2");
		ctt=ApiFactory.getTaskService().completeWorkflowTask(task.getId(),TaskProcessingResult.SUBMIT);
		task=ApiFactory.getTaskService().getActiveTaskByLoginName(dp,"test2");
		Assert.assertNotNull(task);
		List<String> transactors=ApiFactory.getTaskService().getCountersignTransactors(task.getId(),0);
		Assert.assertEquals(2, transactors.size());
	}
	
	@Test
	public void retrieve(){
		// 准备数据
		deploy("wf_test_8.xml");
		ParameterUtils.setParameters(getPrmt(1L, 65L, "test2", "test2"));
		ExpenseReport dp = new ExpenseReport();
		dp.setCompanyId(1L);
		dp.setSignLoginNames("test2,ldx");
		expenseReportManager.save(dp);
		// 提交第一环节任务
		ApiFactory.getInstanceService().submitInstance("wf_test_8", dp);
		String result=ApiFactory.getTaskService().retrieve(dp.getWorkflowInfo().getFirstTaskId());
		Assert.assertEquals("任务已取回", result);
	}
	
	@Test
	public void drawTask(){
		// 准备数据
		deploy("wf_test_8.xml");
		ParameterUtils.setParameters(getPrmt(1L, 65L, "test2", "test2"));
		ExpenseReport dp = new ExpenseReport();
		dp.setCompanyId(1L);
		expenseReportManager.save(dp);
		// 提交第一环节任务
		ApiFactory.getInstanceService().submitInstance("wf_test_8", dp);
		WorkflowTask task=ApiFactory.getTaskService().getActiveTaskByLoginName(dp,"test2");
		CompleteTaskTipType ctt=ApiFactory.getTaskService().completeWorkflowTask(task.getId(),TaskProcessingResult.APPROVE);
		//办理人设置为“上一环节办理人指定”
		Assert.assertEquals(CompleteTaskTipType.RETURN_URL, ctt);
		List<String> loginName=new ArrayList<String>();
		loginName.add("test2");
		loginName.add("ldx");
		ctt=ApiFactory.getTaskService().completeInteractiveWorkflowTask(task.getId(),loginName,"");
		task=ApiFactory.getTaskService().getActiveTaskByLoginName(dp,"ldx");
		String result=ApiFactory.getTaskService().drawTask(task.getId());
		Assert.assertEquals("task.receive.success", result);
	}
	
	@Test
	public void abandonReceive(){
		// 准备数据
		deploy("wf_test_8.xml");
		ParameterUtils.setParameters(getPrmt(1L, 65L, "test2", "test2"));
		ExpenseReport dp = new ExpenseReport();
		dp.setCompanyId(1L);
		expenseReportManager.save(dp);
		// 提交第一环节任务
		ApiFactory.getInstanceService().submitInstance("wf_test_8", dp);
		WorkflowTask task=ApiFactory.getTaskService().getActiveTaskByLoginName(dp,"test2");
		CompleteTaskTipType ctt=ApiFactory.getTaskService().completeWorkflowTask(task.getId(),TaskProcessingResult.APPROVE);
		//办理人设置为“上一环节办理人指定”
		Assert.assertEquals(CompleteTaskTipType.RETURN_URL, ctt);
		List<String> loginName=new ArrayList<String>();
		loginName.add("test2");
		loginName.add("ldx");
		ctt=ApiFactory.getTaskService().completeInteractiveWorkflowTask(task.getId(),loginName,"");
		task=ApiFactory.getTaskService().getActiveTaskByLoginName(dp,"ldx");
		String result=ApiFactory.getTaskService().abandonReceive(task.getId());
		Assert.assertEquals("task.abandon.receive.success", result);
	}
	
	@Test
	public void assign(){
		// 准备数据
		deploy("wf_test_8.xml");
		ParameterUtils.setParameters(getPrmt(1L, 65L, "test2", "test2"));
		ExpenseReport dp = new ExpenseReport();
		dp.setCompanyId(1L);
		expenseReportManager.save(dp);
		// 提交第一环节任务
		ApiFactory.getInstanceService().submitInstance("wf_test_8", dp);
		WorkflowTask task=ApiFactory.getTaskService().getActiveTaskByLoginName(dp,"test2");
		CompleteTaskTipType ctt=ApiFactory.getTaskService().completeWorkflowTask(task.getId(),TaskProcessingResult.APPROVE);
		//办理人设置为“上一环节办理人指定”
		Assert.assertEquals(CompleteTaskTipType.RETURN_URL, ctt);
		List<String> loginName=new ArrayList<String>();
		loginName.add("test2");
		ctt=ApiFactory.getTaskService().completeInteractiveWorkflowTask(task.getId(),loginName,"");
		task=ApiFactory.getTaskService().getActiveTaskByLoginName(dp,"test2");
		Assert.assertNotNull(task);
		ApiFactory.getTaskService().assign(task.getId(),"ldx");
		task=ApiFactory.getTaskService().getActiveTaskByLoginName(dp,"ldx");
		Assert.assertNotNull(task);
		task=ApiFactory.getTaskService().getActiveTaskByLoginName(dp,"test2");
		Assert.assertNull(task);
	}
	
	@Test
	public void getReturnableTaskNames(){
		// 准备数据
		deploy("wf_test_8.xml");
		ParameterUtils.setParameters(getPrmt(1L, 65L, "test2", "test2"));
		ExpenseReport dp = new ExpenseReport();
		dp.setCompanyId(1L);
		dp.setSignLoginNames("test2,ldx");
		expenseReportManager.save(dp);
		// 提交第一环节任务
		ApiFactory.getInstanceService().submitInstance("wf_test_8", dp);
		WorkflowTask task=ApiFactory.getTaskService().getActiveTaskByLoginName(dp,"test2");
		CompleteTaskTipType ctt=ApiFactory.getTaskService().completeWorkflowTask(task.getId(),TaskProcessingResult.APPROVE);
		//办理人设置为“上一环节办理人指定”
		Assert.assertEquals(CompleteTaskTipType.RETURN_URL, ctt);
		ctt=ApiFactory.getTaskService().completeInteractiveWorkflowTask(task.getId(),"","test2");
		task=ApiFactory.getTaskService().getActiveTaskByLoginName(dp,"test2");
		ctt=ApiFactory.getTaskService().completeWorkflowTask(task.getId(),TaskProcessingResult.SUBMIT);
		task=ApiFactory.getTaskService().getActiveTaskByLoginName(dp,"test2");
		Assert.assertNotNull(task);
		List<String> taskNames=ApiFactory.getTaskService().getReturnableTaskNames(task.getId());
		Assert.assertEquals(3, taskNames.size());
	}
	
	@Test
	public void returnTaskTo(){
		// 准备数据
		deploy("wf_test_8.xml");
		ParameterUtils.setParameters(getPrmt(1L, 65L, "test2", "test2"));
		ExpenseReport dp = new ExpenseReport();
		dp.setCompanyId(1L);
		dp.setSignLoginNames("test2,ldx");
		expenseReportManager.save(dp);
		// 提交第一环节任务
		ApiFactory.getInstanceService().submitInstance("wf_test_8", dp);
		WorkflowTask task=ApiFactory.getTaskService().getActiveTaskByLoginName(dp,"test2");
		CompleteTaskTipType ctt=ApiFactory.getTaskService().completeWorkflowTask(task.getId(),TaskProcessingResult.APPROVE);
		//办理人设置为“上一环节办理人指定”
		Assert.assertEquals(CompleteTaskTipType.RETURN_URL, ctt);
		ctt=ApiFactory.getTaskService().completeInteractiveWorkflowTask(task.getId(),"","test2");
		task=ApiFactory.getTaskService().getActiveTaskByLoginName(dp,"test2");
		ctt=ApiFactory.getTaskService().completeWorkflowTask(task.getId(),TaskProcessingResult.SUBMIT);
		task=ApiFactory.getTaskService().getActiveTaskByLoginName(dp,"test2");
		Assert.assertNotNull(task);
		//退回到某环节
		ApiFactory.getTaskService().returnTaskTo(task.getId(),"审批报销单");
		task=ApiFactory.getTaskService().getActiveTaskByLoginName(dp,"test2");
		Assert.assertNotNull(task);
		Assert.assertEquals("task2", task.getCode());
	}
	
	@Test
	public void getActiveTaskByLoginName(){
		// 准备数据
		deploy("wf_test_8.xml");
		ParameterUtils.setParameters(getPrmt(1L, 65L, "test2", "test2"));
		ExpenseReport dp = new ExpenseReport();
		dp.setCompanyId(1L);
		dp.setSignLoginNames("test2,ldx");
		expenseReportManager.save(dp);
		// 提交第一环节任务
		ApiFactory.getInstanceService().submitInstance("wf_test_8", dp);
		WorkflowTask task=ApiFactory.getTaskService().getActiveTaskByLoginName(dp,"test2");
		Assert.assertNotNull(task);
		Assert.assertEquals("task2", task.getCode());
	}
	
	@Test
	public void completeDistributeTaskOne(){//暂且不写
		//completeDistributeTask(Long taskId, List<String> receivers)
	}
	
	@Test
	public void completeDistributeTaskTwo(){//暂且不写
		//completeDistributeTask(Long taskId, String... receivers)
	}
	
	@Test
	public void selectActivity(){
		// 准备数据
		deploy("wf_test_8.xml");
		ParameterUtils.setParameters(getPrmt(1L, 65L, "test2", "test2"));
		ExpenseReport dp = new ExpenseReport();
		dp.setCompanyId(1L);
		dp.setSignLoginNames("test2");
		dp.setFirstLoginName("test2");
		expenseReportManager.save(dp);
		// 提交第一环节任务
		ApiFactory.getInstanceService().submitInstance("wf_test_8", dp);
		WorkflowTask task=ApiFactory.getTaskService().getActiveTaskByLoginName(dp,"test2");
		CompleteTaskTipType ctt=ApiFactory.getTaskService().completeWorkflowTask(task.getId(),TaskProcessingResult.APPROVE);
		//办理人设置为“上一环节办理人指定”
		Assert.assertEquals(CompleteTaskTipType.RETURN_URL, ctt);
		ctt=ApiFactory.getTaskService().completeInteractiveWorkflowTask(task.getId(),"","test2");
		
		task=ApiFactory.getTaskService().getActiveTaskByLoginName(dp,"test2");
		ctt=ApiFactory.getTaskService().completeWorkflowTask(task.getId(),TaskProcessingResult.SUBMIT);
		
		task=ApiFactory.getTaskService().getActiveTaskByLoginName(dp,"test2");
		Assert.assertNotNull(task);
		ctt=ApiFactory.getTaskService().completeWorkflowTask(task.getId(),TaskProcessingResult.APPROVE);
		
		task=ApiFactory.getTaskService().getActiveTaskByLoginName(dp,"test2");
		ctt=ApiFactory.getTaskService().selectActivity(task.getId(),"transitionUI33");
		task=ApiFactory.getTaskService().getActiveTaskByLoginName(dp,"test2");
		Assert.assertEquals("task6", task.getCode());
	}
	
	@Test
	public void getWorkflowTasksByDefinitonName(){
		// 准备数据
		deploy("wf_test_8.xml");
		ParameterUtils.setParameters(getPrmt(1L, 65L, "test2", "test2"));
		ExpenseReport dp = new ExpenseReport();
		dp.setCompanyId(1L);
		dp.setSignLoginNames("test2");
		dp.setFirstLoginName("test2");
		expenseReportManager.save(dp);
		// 提交第一环节任务
		ApiFactory.getInstanceService().submitInstance("wf_test_8", dp);
		List<WorkflowTask> tasks=ApiFactory.getTaskService().getWorkflowTasksByDefinitonName("wf_test_8","test2");
		Assert.assertEquals(1, tasks.size());
	}
	
	@Test
	public void createCopyTasks(){
		// 准备数据
		deploy("wf_test_8.xml");
		ParameterUtils.setParameters(getPrmt(1L, 65L, "test2", "test2"));
		ExpenseReport dp = new ExpenseReport();
		dp.setCompanyId(1L);
		expenseReportManager.save(dp);
		// 提交第一环节任务
		ApiFactory.getInstanceService().submitInstance("wf_test_8", dp);
		WorkflowTask task=ApiFactory.getTaskService().getActiveTaskByLoginName(dp,"test2");
		List<String> loginName=new ArrayList<String>();
		loginName.add("ldx");
		ApiFactory.getTaskService().createCopyTasks(task.getId(),loginName,"","");
		task=ApiFactory.getTaskService().getActiveTaskByLoginName(dp,"ldx");
		Assert.assertEquals(TaskProcessingMode.TYPE_READ, task.getProcessingMode());
	}
	
	@Test
	public void getActiveTaskCountByTransactor(){
		// 准备数据
		deploy("wf_test_8.xml");
		ParameterUtils.setParameters(getPrmt(1L, 65L, "test2", "test2"));
		ExpenseReport dp1 = new ExpenseReport();
		dp1.setCompanyId(1L);
		expenseReportManager.save(dp1);
		// 提交第一环节任务
		ApiFactory.getInstanceService().submitInstance("wf_test_8", dp1);
		
		ExpenseReport dp2 = new ExpenseReport();
		dp2.setCompanyId(1L);
		expenseReportManager.save(dp2);
		// 提交第一环节任务
		ApiFactory.getInstanceService().submitInstance("wf_test_8", dp2);
		
		Integer taskAmount=ApiFactory.getTaskService().getActiveTaskCountByTransactor("test2");
		Assert.assertEquals(2, (int)taskAmount);
	}
	
	@Test
	public void getActiveOverdueTasks(){
		// 准备数据
		deploy("wf_test_8.xml");
		ParameterUtils.setParameters(getPrmt(1L, 65L, "test2", "test2"));
		ExpenseReport dp1 = new ExpenseReport();
		dp1.setCompanyId(1L);
		expenseReportManager.save(dp1);
		// 提交第一环节任务
		ApiFactory.getInstanceService().submitInstance("wf_test_8", dp1);
		WorkflowTask task=ApiFactory.getTaskService().getActiveTaskByLoginName(dp1,"test2");
		task.setLastReminderTime(new Date());
		ApiFactory.getTaskService().saveTask(task);
		
		ParameterUtils.setParameters(getPrmt(1L, 33L, "ldx", "刘冬霞"));
		ExpenseReport dp2 = new ExpenseReport();
		dp2.setCompanyId(1L);
		expenseReportManager.save(dp2);
		// 提交第一环节任务
		ApiFactory.getInstanceService().submitInstance("wf_test_8", dp2);
		task=ApiFactory.getTaskService().getActiveTaskByLoginName(dp2,"ldx");
		task.setLastReminderTime(new Date());
		ApiFactory.getTaskService().saveTask(task);
		
		ExpenseReport dp3 = new ExpenseReport();
		dp3.setCompanyId(1L);
		expenseReportManager.save(dp3);
		// 提交第一环节任务
		ApiFactory.getInstanceService().submitInstance("wf_test_8", dp3);
		
		List<WorkflowTask> tasks=ApiFactory.getTaskService().getActiveOverdueTasks();
		Assert.assertEquals(2, tasks.size());
	}
	
	@Test
	public void getOverdueTasksNumByTransactor(){
		// 准备数据
		deploy("wf_test_8.xml");
		ParameterUtils.setParameters(getPrmt(1L, 65L, "test2", "test2"));
		ExpenseReport dp1 = new ExpenseReport();
		dp1.setCompanyId(1L);
		expenseReportManager.save(dp1);
		// 提交第一环节任务
		ApiFactory.getInstanceService().submitInstance("wf_test_8", dp1);
		WorkflowTask task=ApiFactory.getTaskService().getActiveTaskByLoginName(dp1,"test2");
		task.setLastReminderTime(new Date());
		ApiFactory.getTaskService().saveTask(task);
		
		ParameterUtils.setParameters(getPrmt(1L, 33L, "ldx", "刘冬霞"));
		ExpenseReport dp2 = new ExpenseReport();
		dp2.setCompanyId(1L);
		expenseReportManager.save(dp2);
		// 提交第一环节任务
		ApiFactory.getInstanceService().submitInstance("wf_test_8", dp2);
		task=ApiFactory.getTaskService().getActiveTaskByLoginName(dp2,"ldx");
		task.setLastReminderTime(new Date());
		ApiFactory.getTaskService().saveTask(task);
		
		ExpenseReport dp3 = new ExpenseReport();
		dp3.setCompanyId(1L);
		expenseReportManager.save(dp3);
		// 提交第一环节任务
		ApiFactory.getInstanceService().submitInstance("wf_test_8", dp3);
		
		Map<String,Integer> tasks=ApiFactory.getTaskService().getOverdueTasksNumByTransactor();
		Assert.assertEquals(2, tasks.size());
		Assert.assertEquals(1, (int)tasks.get("test2"));
		Assert.assertEquals(1, (int)tasks.get("ldx"));
	}
	
	@Test
	public void getAllOverdueTasks(){
		// 准备数据
		deploy("wf_test_8.xml");
		ParameterUtils.setParameters(getPrmt(1L, 65L, "test2", "test2"));
		ExpenseReport dp1 = new ExpenseReport();
		dp1.setCompanyId(1L);
		expenseReportManager.save(dp1);
		// 提交第一环节任务
		ApiFactory.getInstanceService().submitInstance("wf_test_8", dp1);
		WorkflowTask task=ApiFactory.getTaskService().getActiveTaskByLoginName(dp1,"test2");
		task.setLastReminderTime(new Date());
		ApiFactory.getTaskService().saveTask(task);
		
		ParameterUtils.setParameters(getPrmt(1L, 33L, "ldx", "刘冬霞"));
		ExpenseReport dp2 = new ExpenseReport();
		dp2.setCompanyId(1L);
		expenseReportManager.save(dp2);
		// 提交第一环节任务
		ApiFactory.getInstanceService().submitInstance("wf_test_8", dp2);
		task=ApiFactory.getTaskService().getActiveTaskByLoginName(dp2,"ldx");
		task.setLastReminderTime(new Date());
		ApiFactory.getTaskService().saveTask(task);
		
		ExpenseReport dp3 = new ExpenseReport();
		dp3.setCompanyId(1L);
		expenseReportManager.save(dp3);
		// 提交第一环节任务
		ApiFactory.getInstanceService().submitInstance("wf_test_8", dp3);
		
		List<WorkflowTask> tasks=ApiFactory.getTaskService().getAllOverdueTasks();
		Assert.assertEquals(2, tasks.size());
	}
	
	@Test
	public void getOverdueTaskCountGroupByTransactor(){
		// 准备数据
		deploy("wf_test_8.xml");
		ParameterUtils.setParameters(getPrmt(1L, 65L, "test2", "test2"));
		ExpenseReport dp1 = new ExpenseReport();
		dp1.setCompanyId(1L);
		expenseReportManager.save(dp1);
		// 提交第一环节任务
		ApiFactory.getInstanceService().submitInstance("wf_test_8", dp1);
		WorkflowTask task=ApiFactory.getTaskService().getActiveTaskByLoginName(dp1,"test2");
		task.setLastReminderTime(new Date());
		ApiFactory.getTaskService().saveTask(task);
		
		ParameterUtils.setParameters(getPrmt(1L, 33L, "ldx", "刘冬霞"));
		ExpenseReport dp2 = new ExpenseReport();
		dp2.setCompanyId(1L);
		expenseReportManager.save(dp2);
		// 提交第一环节任务
		ApiFactory.getInstanceService().submitInstance("wf_test_8", dp2);
		task=ApiFactory.getTaskService().getActiveTaskByLoginName(dp2,"ldx");
		task.setLastReminderTime(new Date());
		ApiFactory.getTaskService().saveTask(task);
		
		ExpenseReport dp3 = new ExpenseReport();
		dp3.setCompanyId(1L);
		expenseReportManager.save(dp3);
		// 提交第一环节任务
		ApiFactory.getInstanceService().submitInstance("wf_test_8", dp3);
		
		Map<String,Integer> tasks=ApiFactory.getTaskService().getOverdueTaskCountGroupByTransactor();
		Assert.assertEquals(2, tasks.size());
		Assert.assertEquals(1, (int)tasks.get("test2"));
		Assert.assertEquals(1, (int)tasks.get("ldx"));
	}
	
	@Test
	public void getActiveTasksByLoginName(){
		// 准备数据
		deploy("wf_test_8.xml");
		ParameterUtils.setParameters(getPrmt(1L, 65L, "test2", "test2"));
		ExpenseReport dp1 = new ExpenseReport();
		dp1.setCompanyId(1L);
		expenseReportManager.save(dp1);
		// 提交第一环节任务
		ApiFactory.getInstanceService().submitInstance("wf_test_8", dp1);
		WorkflowTask task=ApiFactory.getTaskService().getActiveTaskByLoginName(dp1,"test2");
		task.setLastReminderTime(new Date());
		//保存任务
		ApiFactory.getTaskService().saveTask(task);
		
		ParameterUtils.setParameters(getPrmt(1L, 33L, "ldx", "刘冬霞"));
		ExpenseReport dp2 = new ExpenseReport();
		dp2.setCompanyId(1L);
		expenseReportManager.save(dp2);
		// 提交第一环节任务
		ApiFactory.getInstanceService().submitInstance("wf_test_8", dp2);
		task=ApiFactory.getTaskService().getActiveTaskByLoginName(dp2,"ldx");
		task.setLastReminderTime(new Date());
		//保存任务
		ApiFactory.getTaskService().saveTask(task);
		
		ExpenseReport dp3 = new ExpenseReport();
		dp3.setCompanyId(1L);
		expenseReportManager.save(dp3);
		// 提交第一环节任务
		ApiFactory.getInstanceService().submitInstance("wf_test_8", dp3);
		
		List<WorkflowTask> tasks=ApiFactory.getTaskService().getActiveTasksByLoginName("ldx");
		Assert.assertEquals(2, tasks.size());
	}
	
	@Test
	public void isFirstTask(){
		// 准备数据
		deploy("wf_test_8.xml");
		ParameterUtils.setParameters(getPrmt(1L, 65L, "test2", "test2"));
		ExpenseReport dp = new ExpenseReport();
		dp.setCompanyId(1L);
		expenseReportManager.save(dp);
		// 启动流程
		ApiFactory.getInstanceService().startInstance("wf_test_8", dp);
		WorkflowTask task=ApiFactory.getTaskService().getActiveTaskByLoginName(dp,"test2");
		boolean isFirstTask=ApiFactory.getTaskService().isFirstTask(task.getId());
		Assert.assertTrue(isFirstTask);
		// 提交第一环节任务
		ApiFactory.getInstanceService().submitInstance("wf_test_8", dp);
		task=ApiFactory.getTaskService().getActiveTaskByLoginName(dp,"test2");
		isFirstTask=ApiFactory.getTaskService().isFirstTask(task.getId());
		Assert.assertFalse(isFirstTask);
	}
	
	@Test
	public void getTransactorsExcludeGivenTask(){
		// 准备数据
		deploy("wf_test_8.xml");
		ParameterUtils.setParameters(getPrmt(1L, 65L, "test2", "test2"));
		ExpenseReport dp = new ExpenseReport();
		dp.setCompanyId(1L);
		dp.setSignLoginNames("test2");
		expenseReportManager.save(dp);
		// 提交第一环节任务
		ApiFactory.getInstanceService().submitInstance("wf_test_8", dp);
		
		WorkflowTask task1=ApiFactory.getTaskService().getActiveTaskByLoginName(dp,"test2");
		CompleteTaskTipType ctt=ApiFactory.getTaskService().completeWorkflowTask(task1.getId(),TaskProcessingResult.APPROVE);
		//办理人设置为“上一环节办理人指定”
		Assert.assertEquals(CompleteTaskTipType.RETURN_URL, ctt);
		ctt=ApiFactory.getTaskService().completeInteractiveWorkflowTask(task1.getId(),"","ldx");
		
		WorkflowTask task2=ApiFactory.getTaskService().getActiveTaskByLoginName(dp,"ldx");
		ParameterUtils.setParameters(getPrmt(1L, 33L, "ldx", "刘冬霞"));
		ctt=ApiFactory.getTaskService().completeWorkflowTask(task2.getId(),TaskProcessingResult.SUBMIT);
		
		List<String> transactors=ApiFactory.getTaskService().getTransactorsExcludeGivenTask(task1.getId());
		Assert.assertEquals(2, transactors.size());
	}
	
	@Test
	public void setTaskRead(){
		// 准备数据
		deploy("wf_test_8.xml");
		ParameterUtils.setParameters(getPrmt(1L, 65L, "test2", "test2"));
		ExpenseReport dp = new ExpenseReport();
		dp.setCompanyId(1L);
		dp.setSignLoginNames("test2");
		expenseReportManager.save(dp);
		// 提交第一环节任务
		ApiFactory.getInstanceService().submitInstance("wf_test_8", dp);
		
		WorkflowTask task=ApiFactory.getTaskService().getActiveTaskByLoginName(dp,"test2");
		Assert.assertFalse(task.getRead());
		ApiFactory.getTaskService().setTaskRead(task.getId());
		task=ApiFactory.getTaskService().getActiveTaskByLoginName(dp,"test2");
		Assert.assertTrue(task.getRead());
	}
	
	@Test
	public void taskJump(){
		// 准备数据
		deploy("wf_test_8.xml");
		ParameterUtils.setParameters(getPrmt(1L, 65L, "test2", "test2"));
		ExpenseReport dp = new ExpenseReport();
		dp.setCompanyId(1L);
		dp.setSignLoginNames("test2");
		expenseReportManager.save(dp);
		// 启动流程
		ApiFactory.getInstanceService().startInstance("wf_test_8", dp);
		CompleteTaskTipType taskJump=ApiFactory.getTaskService().taskJump(dp.getWorkflowInfo().getWorkflowId(),"会签",ContextUtils.getCompanyId());
		Assert.assertEquals(CompleteTaskTipType.OK, taskJump);
		WorkflowTask task=ApiFactory.getTaskService().getActiveTaskByLoginName(dp,"test2");
		Assert.assertEquals("task4", task.getCode());
	}
	
	@Test
	public void getOptionalTasks(){
		// 准备数据
		deploy("wf_test_8.xml");
		ParameterUtils.setParameters(getPrmt(1L, 65L, "test2", "test2"));
		ExpenseReport dp = new ExpenseReport();
		dp.setCompanyId(1L);
		dp.setSignLoginNames("test2");
		dp.setFirstLoginName("test2");
		expenseReportManager.save(dp);
		// 提交第一环节任务
		ApiFactory.getInstanceService().submitInstance("wf_test_8", dp);
		WorkflowTask task=ApiFactory.getTaskService().getActiveTaskByLoginName(dp,"test2");
		CompleteTaskTipType ctt=ApiFactory.getTaskService().completeWorkflowTask(task.getId(),TaskProcessingResult.APPROVE);
		//办理人设置为“上一环节办理人指定”
		Assert.assertEquals(CompleteTaskTipType.RETURN_URL, ctt);
		ctt=ApiFactory.getTaskService().completeInteractiveWorkflowTask(task.getId(),"","test2");
		
		task=ApiFactory.getTaskService().getActiveTaskByLoginName(dp,"test2");
		ctt=ApiFactory.getTaskService().completeWorkflowTask(task.getId(),TaskProcessingResult.SUBMIT);
		
		task=ApiFactory.getTaskService().getActiveTaskByLoginName(dp,"test2");
		Assert.assertNotNull(task);
		ctt=ApiFactory.getTaskService().completeWorkflowTask(task.getId(),TaskProcessingResult.APPROVE);
		
		task=ApiFactory.getTaskService().getActiveTaskByLoginName(dp,"test2");
		CompleteTaskTipType isNeedChoiceTache=ApiFactory.getTaskService().getOptionalTasks(task);
		Assert.assertEquals(CompleteTaskTipType.TACHE_CHOICE_URL,isNeedChoiceTache);
	}
	
	@Test
	public void getActivityTaskTransactorsOne(){
		// 准备数据
		deploy("wf_test_8.xml");
		ParameterUtils.setParameters(getPrmt(1L, 65L, "test2", "test2"));
		ExpenseReport dp = new ExpenseReport();
		dp.setCompanyId(1L);
		dp.setSignLoginNames("test2");
		dp.setFirstLoginName("test2");
		expenseReportManager.save(dp);
		// 提交第一环节任务
		ApiFactory.getInstanceService().submitInstance("wf_test_8", dp);
		WorkflowTask task=ApiFactory.getTaskService().getActiveTaskByLoginName(dp,"test2");
		CompleteTaskTipType ctt=ApiFactory.getTaskService().completeWorkflowTask(task.getId(),TaskProcessingResult.APPROVE);
		//办理人设置为“上一环节办理人指定”
		Assert.assertEquals(CompleteTaskTipType.RETURN_URL, ctt);
		ctt=ApiFactory.getTaskService().completeInteractiveWorkflowTask(task.getId(),"","test2","ldx");
		List<String[]> transactors=ApiFactory.getTaskService().getActivityTaskTransactors(dp);
		Assert.assertEquals(2,transactors.size());
	}
	
	@Test
	public void getActivityTaskPrincipalsOne(){
		DateFormat df = DateFormat.getInstance();
		Calendar calendar = df.getCalendar();
		calendar.setTime(new Date());
		calendar.set(Calendar.HOUR_OF_DAY,0);
		calendar.set(Calendar.MINUTE, 0);
		calendar.set(Calendar.SECOND, 0);
		calendar.set(Calendar.MILLISECOND, 0);
		Date beginTime=calendar.getTime();
		calendar.add(Calendar.DAY_OF_YEAR,1);
		Date endTime=calendar.getTime();
		
		// 准备数据
		deploy("wf_test_8.xml");
		ParameterUtils.setParameters(getPrmt(1L, 65L, "test2", "test2"));
		WorkflowDefinition workflowDefinition=workflowDefinitionManager.getEnabledWorkflowDefinitionByCodeAndVersion("wf_test_8", 1,ContextUtils.getCompanyId());
		TrustRecord trustRecord=new TrustRecord();
		trustRecord.setCompanyId(ContextUtils.getCompanyId());
		trustRecord.setCreatedTime(new Date());
		trustRecord.setTrustor(ContextUtils.getLoginName());
		trustRecord.setBeginTime(beginTime);
		trustRecord.setEndTime(endTime);
		trustRecord.setStyle((short)2);
		trustRecord.setName("全权委托");
		trustRecord.setState(TrustRecordState.EFFICIENT);
		trustRecord.setTrustee("ldx");
		trustRecord.setProcessId(workflowDefinition.getProcessId());
		delegateMainManager.saveDelegateMain(trustRecord);
		delegateMainManager.startDelegateMain(trustRecord.getId());
		
		ExpenseReport dp = new ExpenseReport();
		dp.setCompanyId(1L);
		expenseReportManager.save(dp);
		// 提交第一环节任务
		ApiFactory.getInstanceService().submitInstance("wf_test_8", dp);
		
		List<String> activityTasks=ApiFactory.getTaskService().getActivityTaskPrincipals(dp);
		Assert.assertEquals("test2",activityTasks.get(0));
	}
	
	@Test
	public void getActivityTaskPrincipalsDetailOne(){
		DateFormat df = DateFormat.getInstance();
		Calendar calendar = df.getCalendar();
		calendar.setTime(new Date());
		calendar.set(Calendar.HOUR_OF_DAY,0);
		calendar.set(Calendar.MINUTE, 0);
		calendar.set(Calendar.SECOND, 0);
		calendar.set(Calendar.MILLISECOND, 0);
		Date beginTime=calendar.getTime();
		calendar.add(Calendar.DAY_OF_YEAR,1);
		Date endTime=calendar.getTime();
		
		// 准备数据
		deploy("wf_test_8.xml");
		ParameterUtils.setParameters(getPrmt(1L, 65L, "test2", "test2"));
		WorkflowDefinition workflowDefinition=workflowDefinitionManager.getEnabledWorkflowDefinitionByCodeAndVersion("wf_test_8", 1,ContextUtils.getCompanyId());
		TrustRecord trustRecord=new TrustRecord();
		trustRecord.setCompanyId(ContextUtils.getCompanyId());
		trustRecord.setCreatedTime(new Date());
		trustRecord.setTrustor(ContextUtils.getLoginName());
		trustRecord.setBeginTime(beginTime);
		trustRecord.setEndTime(endTime);
		trustRecord.setStyle((short)2);
		trustRecord.setName("全权委托");
		trustRecord.setState(TrustRecordState.EFFICIENT);
		trustRecord.setTrustee("ldx");
		trustRecord.setProcessId(workflowDefinition.getProcessId());
		delegateMainManager.saveDelegateMain(trustRecord);
		delegateMainManager.startDelegateMain(trustRecord.getId());
		
		ExpenseReport dp = new ExpenseReport();
		dp.setCompanyId(1L);
		expenseReportManager.save(dp);
		// 提交第一环节任务
		ApiFactory.getInstanceService().submitInstance("wf_test_8", dp);
		
		List<String[]> activityTasks=ApiFactory.getTaskService().getActivityTaskPrincipalsDetail(dp);
		Object[] activityTask=activityTasks.get(0);
		String loginName=activityTask[0].toString();
		Assert.assertEquals("test2",loginName);
	}
	
	@Test
	public void getCompletedTaskTransactorOne(){
		// 准备数据
		deploy("wf_test_8.xml");
		ParameterUtils.setParameters(getPrmt(1L, 65L, "test2", "test2"));
		ExpenseReport dp = new ExpenseReport();
		dp.setCompanyId(1L);
		dp.setSignLoginNames("test2");
		dp.setFirstLoginName("test2");
		expenseReportManager.save(dp);
		// 提交第一环节任务
		ApiFactory.getInstanceService().submitInstance("wf_test_8", dp);
		WorkflowTask task=ApiFactory.getTaskService().getActiveTaskByLoginName(dp,"test2");
		CompleteTaskTipType ctt=ApiFactory.getTaskService().completeWorkflowTask(task.getId(),TaskProcessingResult.APPROVE);
		//办理人设置为“上一环节办理人指定”
		Assert.assertEquals(CompleteTaskTipType.RETURN_URL, ctt);
		ctt=ApiFactory.getTaskService().completeInteractiveWorkflowTask(task.getId(),"","ldx");
		
		ParameterUtils.setParameters(getPrmt(1L, 33L, "ldx", "刘冬霞"));
		task=ApiFactory.getTaskService().getActiveTaskByLoginName(dp,"ldx");
		ctt=ApiFactory.getTaskService().completeWorkflowTask(task.getId(),TaskProcessingResult.SUBMIT);
		Set<String> transactor=ApiFactory.getTaskService().getCompletedTaskTransactor(dp);
		Assert.assertEquals(2, transactor.size());
	}
	
	@Test
	public void getCompletedTaskTransactorTwo(){
		// 准备数据
		deploy("wf_test_8.xml");
		ParameterUtils.setParameters(getPrmt(1L, 65L, "test2", "test2"));
		ExpenseReport dp = new ExpenseReport();
		dp.setCompanyId(1L);
		dp.setSignLoginNames("test2");
		dp.setFirstLoginName("test2");
		expenseReportManager.save(dp);
		// 提交第一环节任务
		ApiFactory.getInstanceService().submitInstance("wf_test_8", dp);
		WorkflowTask task=ApiFactory.getTaskService().getActiveTaskByLoginName(dp,"test2");
		CompleteTaskTipType ctt=ApiFactory.getTaskService().completeWorkflowTask(task.getId(),TaskProcessingResult.APPROVE);
		//办理人设置为“上一环节办理人指定”
		Assert.assertEquals(CompleteTaskTipType.RETURN_URL, ctt);
		ctt=ApiFactory.getTaskService().completeInteractiveWorkflowTask(task.getId(),"","ldx");
		
		ParameterUtils.setParameters(getPrmt(1L, 33L, "ldx", "刘冬霞"));
		task=ApiFactory.getTaskService().getActiveTaskByLoginName(dp,"ldx");
		ctt=ApiFactory.getTaskService().completeWorkflowTask(task.getId(),TaskProcessingResult.SUBMIT);
		ParameterUtils.setParameters(getPrmt(1L, 65L, "test2", "test2"));
		task=ApiFactory.getTaskService().getActiveTaskByLoginName(dp,"test2");
		Set<String> transactor=ApiFactory.getTaskService().getCompletedTaskTransactor(task.getId());
		Assert.assertEquals(2, transactor.size());
	}
	
	@Test
	public void getExtendFields(){
		// 准备数据
		deploy("wf_test_8.xml");
//		WorkflowDefinition workflowDefinition=workflowDefinitionManager.getEnabledWorkflowDefinitionByCodeAndVersion("wf_test_8", 1,ContextUtils.getCompanyId());
//		Map<String,Document> documents=new HashMap<String,Document>();
//		DocumentThreadParameters p= new DocumentThreadParameters();
//		String fileContent = readFileContent("wf_test_8.xml");
//		Document document=Dom4jUtils.getDocument(fileContent);
//		documents.put(workflowDefinition.getProcessId(), document);
//		p.setDocuments(documents);
//		DocumentParameterUtils.setParameters(p);
		ParameterUtils.setParameters(getPrmt(1L, 65L, "test2", "test2"));
		ExpenseReport dp = new ExpenseReport();
		dp.setCompanyId(1L);
		dp.setSignLoginNames("test2");
		dp.setFirstLoginName("test2");
		expenseReportManager.save(dp);
		// 提交第一环节任务
		ApiFactory.getInstanceService().submitInstance("wf_test_8", dp);
		WorkflowTask task=ApiFactory.getTaskService().getActiveTaskByLoginName(dp,"test2");
		Map<String,String> extendFields=ApiFactory.getTaskService().getExtendFields(task.getId());
		Assert.assertEquals(2, extendFields.size());
	}
	
	@Test
	public void getActivityTaskTransactorsTwo(){
		// 准备数据
		deploy("wf_test_8.xml");
		ParameterUtils.setParameters(getPrmt(1L, 65L, "test2", "test2"));
		ExpenseReport dp = new ExpenseReport();
		dp.setCompanyId(1L);
		dp.setSignLoginNames("test2");
		dp.setFirstLoginName("test2");
		expenseReportManager.save(dp);
		// 提交第一环节任务
		ApiFactory.getInstanceService().submitInstance("wf_test_8", dp);
		WorkflowTask task=ApiFactory.getTaskService().getActiveTaskByLoginName(dp,"test2");
		CompleteTaskTipType ctt=ApiFactory.getTaskService().completeWorkflowTask(task.getId(),TaskProcessingResult.APPROVE);
		//办理人设置为“上一环节办理人指定”
		Assert.assertEquals(CompleteTaskTipType.RETURN_URL, ctt);
		ctt=ApiFactory.getTaskService().completeInteractiveWorkflowTask(task.getId(),"","test2","ldx");
		List<String[]> transactors=ApiFactory.getTaskService().getActivityTaskTransactors(dp.getWorkflowInfo().getWorkflowId());
		Assert.assertEquals(2,transactors.size());
	}
	
	@Test
	public void getActivityTaskPrincipalsTwo(){
		DateFormat df = DateFormat.getInstance();
		Calendar calendar = df.getCalendar();
		calendar.setTime(new Date());
		calendar.set(Calendar.HOUR_OF_DAY,0);
		calendar.set(Calendar.MINUTE, 0);
		calendar.set(Calendar.SECOND, 0);
		calendar.set(Calendar.MILLISECOND, 0);
		Date beginTime=calendar.getTime();
		calendar.add(Calendar.DAY_OF_YEAR,1);
		Date endTime=calendar.getTime();
		
		// 准备数据
		deploy("wf_test_8.xml");
		ParameterUtils.setParameters(getPrmt(1L, 65L, "test2", "test2"));
		WorkflowDefinition workflowDefinition=workflowDefinitionManager.getEnabledWorkflowDefinitionByCodeAndVersion("wf_test_8", 1,ContextUtils.getCompanyId());
		TrustRecord trustRecord=new TrustRecord();
		trustRecord.setCompanyId(ContextUtils.getCompanyId());
		trustRecord.setCreatedTime(new Date());
		trustRecord.setTrustor(ContextUtils.getLoginName());
		trustRecord.setBeginTime(beginTime);
		trustRecord.setEndTime(endTime);
		trustRecord.setStyle((short)2);
		trustRecord.setName("全权委托");
		trustRecord.setState(TrustRecordState.EFFICIENT);
		trustRecord.setTrustee("ldx");
		trustRecord.setProcessId(workflowDefinition.getProcessId());
		delegateMainManager.saveDelegateMain(trustRecord);
		delegateMainManager.startDelegateMain(trustRecord.getId());
		
		ExpenseReport dp = new ExpenseReport();
		dp.setCompanyId(1L);
		expenseReportManager.save(dp);
		// 提交第一环节任务
		ApiFactory.getInstanceService().submitInstance("wf_test_8", dp);
		
		List<String> activityTasks=ApiFactory.getTaskService().getActivityTaskPrincipals(dp.getWorkflowInfo().getWorkflowId());
		Assert.assertEquals("test2",activityTasks.get(0));
	}
	
	@Test
	public void getActivityTaskPrincipalsDetailTwo(){
		DateFormat df = DateFormat.getInstance();
		Calendar calendar = df.getCalendar();
		calendar.setTime(new Date());
		calendar.set(Calendar.HOUR_OF_DAY,0);
		calendar.set(Calendar.MINUTE, 0);
		calendar.set(Calendar.SECOND, 0);
		calendar.set(Calendar.MILLISECOND, 0);
		Date beginTime=calendar.getTime();
		calendar.add(Calendar.DAY_OF_YEAR,1);
		Date endTime=calendar.getTime();
		
		// 准备数据
		deploy("wf_test_8.xml");
		ParameterUtils.setParameters(getPrmt(1L, 65L, "test2", "test2"));
		WorkflowDefinition workflowDefinition=workflowDefinitionManager.getEnabledWorkflowDefinitionByCodeAndVersion("wf_test_8", 1,ContextUtils.getCompanyId());
		TrustRecord trustRecord=new TrustRecord();
		trustRecord.setCompanyId(ContextUtils.getCompanyId());
		trustRecord.setCreatedTime(new Date());
		trustRecord.setTrustor(ContextUtils.getLoginName());
		trustRecord.setBeginTime(beginTime);
		trustRecord.setEndTime(endTime);
		trustRecord.setStyle((short)2);
		trustRecord.setName("全权委托");
		trustRecord.setState(TrustRecordState.EFFICIENT);
		trustRecord.setTrustee("ldx");
		trustRecord.setProcessId(workflowDefinition.getProcessId());
		delegateMainManager.saveDelegateMain(trustRecord);
		delegateMainManager.startDelegateMain(trustRecord.getId());
		
		ExpenseReport dp = new ExpenseReport();
		dp.setCompanyId(1L);
		expenseReportManager.save(dp);
		// 提交第一环节任务
		ApiFactory.getInstanceService().submitInstance("wf_test_8", dp);
		
		List<String[]> activityTasks=ApiFactory.getTaskService().getActivityTaskPrincipalsDetail(dp.getWorkflowInfo().getWorkflowId());
		Object[] activityTask=activityTasks.get(0);
		String loginName=activityTask[0].toString();
		Assert.assertEquals("test2",loginName);
	}
	
	@Test
	public void getDataByTaskId(){
		// 准备数据
		deploy("wf_test_8.xml");
		ParameterUtils.setParameters(getPrmt(1L, 65L, "test2", "test2"));
		ExpenseReport dp = new ExpenseReport();
		dp.setCompanyId(1L);
		dp.setSignLoginNames("test2");
		dp.setFirstLoginName("test2");
		expenseReportManager.save(dp);
		// 提交第一环节任务
		ApiFactory.getInstanceService().submitInstance("wf_test_8", dp);
		WorkflowTask task=ApiFactory.getTaskService().getActiveTaskByLoginName(dp,"test2");
		Map data=ApiFactory.getTaskService().getDataByTaskId(task.getId());
		Assert.assertNotNull(data);
	}
	
	@Test
	public void getActivityTasks(){
		// 准备数据
		deploy("wf_test_8.xml");
		ParameterUtils.setParameters(getPrmt(1L, 65L, "test2", "test2"));
		ExpenseReport dp = new ExpenseReport();
		dp.setCompanyId(1L);
		dp.setSignLoginNames("test2");
		dp.setFirstLoginName("test2");
		expenseReportManager.save(dp);
		// 提交第一环节任务
		ApiFactory.getInstanceService().submitInstance("wf_test_8", dp);
		WorkflowTask task=ApiFactory.getTaskService().getActiveTaskByLoginName(dp,"test2");
		CompleteTaskTipType ctt=ApiFactory.getTaskService().completeWorkflowTask(task.getId(),TaskProcessingResult.APPROVE);
		//办理人设置为“上一环节办理人指定”
		Assert.assertEquals(CompleteTaskTipType.RETURN_URL, ctt);
		ctt=ApiFactory.getTaskService().completeInteractiveWorkflowTask(task.getId(),"","test2","ldx");
		List<WorkflowTask> transactors=ApiFactory.getTaskService().getActivityTasks(dp);
		Assert.assertEquals(2,transactors.size());
	}
	
	@Test
	public void returnTask(){
		// 准备数据
		deploy("wf_test_8.xml");
		ParameterUtils.setParameters(getPrmt(1L, 65L, "test2", "test2"));
		ExpenseReport dp = new ExpenseReport();
		dp.setCompanyId(1L);
		dp.setSignLoginNames("test2");
		dp.setFirstLoginName("test2");
		expenseReportManager.save(dp);
		// 提交第一环节任务
		ApiFactory.getInstanceService().submitInstance("wf_test_8", dp);
		WorkflowTask task=ApiFactory.getTaskService().getActiveTaskByLoginName(dp,"test2");
		CompleteTaskTipType ctt=ApiFactory.getTaskService().completeWorkflowTask(task.getId(),TaskProcessingResult.APPROVE);
		//办理人设置为“上一环节办理人指定”
		Assert.assertEquals(CompleteTaskTipType.RETURN_URL, ctt);
		ctt=ApiFactory.getTaskService().completeInteractiveWorkflowTask(task.getId(),"","test2","ldx");
		task=ApiFactory.getTaskService().getActiveTaskByLoginName(dp,"test2");
		String result=ApiFactory.getTaskService().drawTask(task.getId());
		Assert.assertEquals("task.receive.success",result);
		result=ApiFactory.getTaskService().returnTask(task.getId());
		Assert.assertEquals("退回成功",result);
		task=ApiFactory.getTaskService().getActiveTaskByLoginName(dp,"test2");
		Assert.assertEquals("task2",task.getCode());
	}
	
	@Test
	public void endInstance(){
		// 准备数据
		deploy("wf_test_8.xml");
		ParameterUtils.setParameters(getPrmt(1L, 65L, "test2", "test2"));
		ExpenseReport dp = new ExpenseReport();
		dp.setCompanyId(1L);
		dp.setSignLoginNames("test2");
		dp.setFirstLoginName("test2");
		expenseReportManager.save(dp);
		// 提交第一环节任务
		ApiFactory.getInstanceService().submitInstance("wf_test_8", dp);
		WorkflowInstance workflow=ApiFactory.getInstanceService().getInstance(dp.getWorkflowInfo().getWorkflowId());
		ApiFactory.getTaskService().endInstance(workflow);
		workflow=ApiFactory.getInstanceService().getInstance(dp.getWorkflowInfo().getWorkflowId());
		Assert.assertEquals(ProcessState.MANUAL_END,workflow.getProcessState());
	}
	
}
