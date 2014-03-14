package com.norteksoft.wf.unit;

import java.util.Collection;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;
import org.unitils.database.annotations.Transactional;
import org.unitils.database.util.TransactionMode;
import org.unitils.spring.annotation.SpringApplicationContext;
import org.unitils.spring.annotation.SpringBeanByName;

import com.norteksoft.ems.entity.ExpenseReport;
import com.norteksoft.ems.service.ExpenseReportManager;
import com.norteksoft.mms.form.entity.FormControl;
import com.norteksoft.product.api.ApiFactory;
import com.norteksoft.product.api.entity.WorkflowDefinition;
import com.norteksoft.product.api.entity.WorkflowTask;
import com.norteksoft.product.util.ParameterUtils;

/**
 * 表单和字段的api部署测试                                                                                                                                                               
 * @author Administrator
 *
 */
@Transactional(TransactionMode.ROLLBACK)
@SpringApplicationContext({"applicationContext-test.xml","applicationContext-memcache.xml"})
public class WorkflowFormServiceTest extends BaseWorkflowTestCase{
	@SpringBeanByName
	ExpenseReportManager expenseReportManager;
	
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
		Assert.assertNotNull(task);
		//根据task查询流程实例表单ID
		Long formId=ApiFactory.getFormService().getFormIdByTask(task.getId());
		Assert.assertNotNull(formId);
		//根据formId查询所有表单字段
		List<FormControl> file=ApiFactory.getFormService().getFormControls(formId);
		Assert.assertNotNull(file);
	}
	
	@Test
	public void getFormFlowableIdByTask(){
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
		//根据task查询业务实体的ID
		Long formId=ApiFactory.getFormService().getFormFlowableIdByTask(task.getId());
		Assert.assertNotNull(formId);
	}
	
	@Test
	public void getFieldPermissionNotStartedOne(){
		// 准备数据
		deploy("wf_test_8.xml");
		ParameterUtils.setParameters(getPrmt(1L, 65L, "test2", "test2"));
		ExpenseReport dp = new ExpenseReport();
		dp.setCompanyId(1L);
		expenseReportManager.save(dp);
		//流程还未启动时,根据流程编号查询第一环节的字段编辑权限,以JSON格式返回
		String editRole=ApiFactory.getFormService().getFieldPermissionNotStarted("wf_test_8");
		//名称必填，部门禁止编辑
		Assert.assertNotNull(editRole);
	}
	
	@Test
	public void getFieldPermissionNotStartedTwo(){
		// 准备数据
		deploy("wf_test_8.xml");
		ParameterUtils.setParameters(getPrmt(1L, 65L, "test2", "test2"));
		ExpenseReport dp = new ExpenseReport();
		dp.setCompanyId(1L);
		expenseReportManager.save(dp);
		//流程还未启动时,根据流程编号和版本查询第一环节的字段编辑权限,以JSON格式返回
		String editRole=ApiFactory.getFormService().getFieldPermissionNotStarted("wf_test_8",1);
		//名称必填，部门禁止编辑
		Assert.assertNotNull(editRole);
	}
	
	@Test
	public void getNeedFillFieldsNotStartedOne(){
		// 准备数据
		deploy("wf_test_8.xml");
		ParameterUtils.setParameters(getPrmt(1L, 65L, "test2", "test2"));
		ExpenseReport dp = new ExpenseReport();
		dp.setCompanyId(1L);
		expenseReportManager.save(dp);
		//流程还未启动时,根据流程编号查询第一环节的必填字段
		Collection<String> editRole=ApiFactory.getFormService().getNeedFillFieldsNotStarted("wf_test_8");
		//名称必填
		Assert.assertNotNull(editRole);
	}
	
	@Test
	public void getForbiddenFieldsNotStarted(){
		// 准备数据
		deploy("wf_test_8.xml");
		ParameterUtils.setParameters(getPrmt(1L, 65L, "test2", "test2"));
		ExpenseReport dp = new ExpenseReport();
		dp.setCompanyId(1L);
		expenseReportManager.save(dp);
		//流程还未启动时,根据流程编号查询第一环节的禁止编辑的字段
		Collection<String> editRole=ApiFactory.getFormService().getForbiddenFieldsNotStarted("wf_test_8");
		//部门禁止编辑
		Assert.assertNotNull(editRole);
	}
	
	@Test
	public void getFieldPermissionNotStartedThree(){
		// 准备数据
		deploy("wf_test_8.xml");
		ParameterUtils.setParameters(getPrmt(1L, 65L, "test2", "test2"));
		ExpenseReport dp = new ExpenseReport();
		dp.setCompanyId(1L);
		expenseReportManager.save(dp);
		WorkflowDefinition definition=ApiFactory.getDefinitionService().getEnabledHighestVersionWorkflowDefinition("wf_test_8");
		Assert.assertNotNull(definition);
		//流程还未启动时,根据流程的definitionId查询第一环节的字段编辑权限,以JSON格式返回
		String editRole=ApiFactory.getFormService().getFieldPermissionNotStarted(definition.getId());
		//名称必填，部门禁止编辑
		Assert.assertNotNull(editRole);
	}
	
	@Test
	public void getNeedFillFieldsNotStartedTwo(){
		// 准备数据
		deploy("wf_test_8.xml");
		ParameterUtils.setParameters(getPrmt(1L, 65L, "test2", "test2"));
		ExpenseReport dp = new ExpenseReport();
		dp.setCompanyId(1L);
		expenseReportManager.save(dp);
		WorkflowDefinition definition=ApiFactory.getDefinitionService().getEnabledHighestVersionWorkflowDefinition("wf_test_8");
		Assert.assertNotNull(definition);
		//流程还未启动时,根据流程的definitionId查询第一环节的必填字段
		Collection<String> editRole=ApiFactory.getFormService().getNeedFillFieldsNotStarted(definition.getId());
		//名称必填
		Assert.assertNotNull(editRole);
	}
	
	@Test
	public void getForbiddenFieldsNotStartedTwo(){
		// 准备数据
		deploy("wf_test_8.xml");
		ParameterUtils.setParameters(getPrmt(1L, 65L, "test2", "test2"));
		ExpenseReport dp = new ExpenseReport();
		dp.setCompanyId(1L);
		expenseReportManager.save(dp);
		WorkflowDefinition definition=ApiFactory.getDefinitionService().getEnabledHighestVersionWorkflowDefinition("wf_test_8");
		Assert.assertNotNull(definition);
		//流程还未启动时,根据流程的definitionId查询第一环节的禁止编辑的字段
		Collection<String> editRole=ApiFactory.getFormService().getForbiddenFieldsNotStarted(definition.getId());
		//部门禁止编辑
		Assert.assertNotNull(editRole);
	}
	
	@Test
	public void getFieldPermissionOne(){
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
		//查询流程中环节的字段编辑权限
		String editRole=ApiFactory.getFormService().getFieldPermission(task.getId());
		//名称必填，部门禁止编辑
		Assert.assertNotNull(editRole);
	}
	
	@Test
	public void getNeedFillFields(){
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
		//查询流程中环节的必填的字段
		Collection<String> editRole=ApiFactory.getFormService().getNeedFillFields(task.getId());
		//名称必填
		Assert.assertNotNull(editRole);
	}
	
	@Test
	public void getForbiddenFields(){
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
		//查询流程中环节的禁止编辑的字段
		Collection<String> editRole=ApiFactory.getFormService().getForbiddenFields(task.getId());
		//部门禁止编辑
		Assert.assertNotNull(editRole);
	}
	
	@Test
	public void getFieldPermissionTwo(){
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
		//所有字段可编辑状态信息查询
		String editRole=ApiFactory.getFormService().getFieldPermission(false);
		Assert.assertNotNull(editRole);
	}
	
	@Test
	public void fillEntityByDefinitionOne(){
		// 准备数据
		deploy("wf_test_8.xml");
		ParameterUtils.setParameters(getPrmt(1L, 65L, "test2", "test2"));
		ExpenseReport dp = new ExpenseReport();
		dp.setCompanyId(1L);
		expenseReportManager.save(dp);
		//标准表单 流程还未启动时,自动填写实体，在转向办理页面时，根据本环节定义的自动填写字段，来自动填充实体
		ApiFactory.getFormService().fillEntityByDefinition(dp,"wf_test_8");
		//在第一环节的自动填写字段中给name赋值“小明”
		Assert.assertNotNull(dp.getName());
	}
	
	@Test
	public void fillEntityByDefinitionTwo(){
		// 准备数据
		deploy("wf_test_8.xml");
		ParameterUtils.setParameters(getPrmt(1L, 65L, "test2", "test2"));
		ExpenseReport dp = new ExpenseReport();
		dp.setCompanyId(1L);
		expenseReportManager.save(dp);
		//标准表单 流程还未启动时,自动填写实体，在转向办理页面时，根据本环节定义的自动填写字段，来自动填充实体
		ApiFactory.getFormService().fillEntityByDefinition(dp,"wf_test_8",1);
		//在第一环节的自动填写字段中给name赋值“小明”
		Assert.assertNotNull(dp.getName());
	}
	
	@Test
	public void fillEntityByTask(){
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
		//标准表单 自动填写实体，在转向办理页面时，根据本环节定义的自动填写字段，来自动填充实体
		ApiFactory.getFormService().fillEntityByTask(dp,task.getId());
		//在第一环节的自动填写字段中给name赋值“小明”
		Assert.assertNotNull(dp.getDepartment());
	}
}
