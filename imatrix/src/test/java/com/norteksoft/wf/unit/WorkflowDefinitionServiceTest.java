package com.norteksoft.wf.unit;

import java.util.List;

import org.junit.Assert;
import org.junit.Test;
import org.unitils.database.annotations.Transactional;
import org.unitils.database.util.TransactionMode;
import org.unitils.spring.annotation.SpringApplicationContext;
import org.unitils.spring.annotation.SpringBeanByName;

import com.norteksoft.product.api.ApiFactory;
import com.norteksoft.product.api.entity.WorkflowDefinition;
import com.norteksoft.product.util.ParameterUtils;
import com.norteksoft.task.base.enumeration.TaskState;
import com.norteksoft.task.dao.WorkflowTaskDao;
import com.norteksoft.task.entity.WorkflowTask;
import com.norteksoft.wf.engine.dao.WorkflowInstanceDao;
import com.norteksoft.wf.engine.dao.WorkflowTypeDao;
import com.norteksoft.wf.engine.entity.WorkflowInstance;
import com.norteksoft.wf.engine.entity.WorkflowType;


@Transactional(TransactionMode.ROLLBACK)
@SpringApplicationContext({"applicationContext-test.xml","applicationContext-memcache.xml"})
public class WorkflowDefinitionServiceTest extends BaseWorkflowTestCase {
	
	@SpringBeanByName
	WorkflowTaskDao workflowTaskDao;
	
	@SpringBeanByName
	WorkflowInstanceDao workflowInstanceDao;
	
	@SpringBeanByName
	WorkflowTypeDao workflowTypeDao;
	
	@Test
	public void getEnabledHighestVersionWorkflowDefinition() {
		deploy("test_wf_1.xml");
		ParameterUtils.setParameters(getPrmt(1L, 33L, "ldx","ems"));
		
		com.norteksoft.product.api.entity.WorkflowDefinition result = ApiFactory.getDefinitionService().getEnabledHighestVersionWorkflowDefinition("test_wf_1");
		Assert.assertNotNull(result);
		
	}
	
	@Test
	public void getWorkflowDefinitionIdByTask() {
		
		WorkflowInstance wi= new WorkflowInstance();
		wi.setProcessCode("wangjing_process_code_33");
		wi.setProcessInstanceId("workflow_aaaaaaaaaaaa");
		wi.setWorkflowDefinitionId(212121l);
		workflowInstanceDao.save(wi);
		
		WorkflowTask task = new WorkflowTask();
		task.setCode("wangjing_12212");
		task.setActive(TaskState.COMPLETED.getIndex());
		task.setProcessInstanceId(workflowInstanceDao.getInstanceByCode("wangjing_process_code_33").get(0).getProcessInstanceId());
		workflowTaskDao.save(task);
		
		Long taskId=workflowTaskDao.getTaskByCode("wangjing_12212").get(0).getId();
		Long instanceId = ApiFactory.getDefinitionService().getWorkflowDefinitionIdByTask(taskId);
		Assert.assertEquals(212121l, instanceId.longValue());
		
	}
	
	@Test
	public void getWorkflowDefinition() {
		String fileContent = readFileContent("test_wf_1.xml");
		Assert.assertNotNull("文件不存在", fileContent);
		// 创建流程定义
		Long id = workflowDefinitionManager.createWfDefinition(1L, fileContent, "expense", "ems");
		Assert.assertNotNull(id);
		// 部署流程
		try {
			String msg = workflowDefinitionManager.deployProcess(id);
			Assert.assertEquals("草稿 -> 启用", msg);
		} catch (Exception e) {
			Assert.assertTrue("流程启用失败", false);
		}
		ParameterUtils.setParameters(getPrmt(1L, 33L, "ldx","ems"));
		
		WorkflowDefinition result = ApiFactory.getDefinitionService().getWorkflowDefinition(id);
		Assert.assertNotNull(result);
		
	}
	
	@Test
	public void getWorkflowDefinitionsByTypeCode() {
		deploy("test_wf_1.xml");
		ParameterUtils.setParameters(getPrmt(1L, 33L, "ldx","ems"));
		
		List<WorkflowDefinition> result = ApiFactory.getDefinitionService().getWorkflowDefinitionsByTypeCode("expense");
		Assert.assertNotNull(result);
		
	}
	
	
	@Test
	public void getWorkflowDefinitionsByCode() {
		deploy("test_wf_1.xml");
		ParameterUtils.setParameters(getPrmt(1L, 33L, "ldx","ems"));
		
		List<WorkflowDefinition> result = ApiFactory.getDefinitionService().getWorkflowDefinitionsByCode("test_wf_1");
		Assert.assertNotNull(result);
		
	}
	
	@Test
	public void getWorkflowDefinitionByCodeAndVersion() {
		deploy("test_wf_1.xml");
		ParameterUtils.setParameters(getPrmt(1L, 33L, "ldx","ems"));
		
		WorkflowDefinition result = ApiFactory.getDefinitionService().getWorkflowDefinitionByCodeAndVersion("test_wf_1",new Integer(1));
		Assert.assertNotNull(result);
	}
	
	
	@Test
	public void getApproveSystemWorkflowTypes() {
		WorkflowType workflowType = new WorkflowType();
		workflowType.setApproveSystem(true);
		workflowType.setCompanyId(1L);
		workflowTypeDao.save(workflowType);
		
		List<WorkflowType> result = ApiFactory.getDefinitionService().getApproveSystemWorkflowTypes();
		Assert.assertNotNull(result);
	}
	
	@Test
	public void getWorkflowDefinitionsByFormCodeAndVersion() {
		deploy("test_wf_1.xml");
		ParameterUtils.setParameters(getPrmt(1L, 33L, "ldx","ems"));
		
		List<WorkflowDefinition> result = ApiFactory.getDefinitionService().getWorkflowDefinitionsByFormCodeAndVersion("ES_EXPENSE_REPORT",new Integer(1));
		Assert.assertNotNull(result);
	}
	
	@Test
	public void getWorkflowDefinitionsByName() {
		deploy("test_wf_1.xml");
		ParameterUtils.setParameters(getPrmt(1L, 33L, "ldx","ems"));
		
		List<WorkflowDefinition> result = ApiFactory.getDefinitionService().getWorkflowDefinitionsByName("expense","报销");
		Assert.assertNotNull(result);
	}
	
}
