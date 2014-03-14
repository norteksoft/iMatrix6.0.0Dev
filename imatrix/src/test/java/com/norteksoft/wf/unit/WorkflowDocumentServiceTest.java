package com.norteksoft.wf.unit;

import java.util.List;

import junit.framework.Assert;

import org.junit.Test;
import org.unitils.database.annotations.Transactional;
import org.unitils.database.util.TransactionMode;
import org.unitils.spring.annotation.SpringApplicationContext;
import org.unitils.spring.annotation.SpringBeanByName;

import com.norteksoft.product.api.ApiFactory;
import com.norteksoft.product.api.entity.Document;
import com.norteksoft.product.util.MemCachedUtils;
import com.norteksoft.task.base.enumeration.TaskProcessingMode;
import com.norteksoft.task.base.enumeration.TaskState;
import com.norteksoft.task.dao.WorkflowTaskDao;
import com.norteksoft.task.entity.WorkflowTask;
import com.norteksoft.wf.engine.client.WorkflowInfo;
import com.norteksoft.wf.engine.dao.OfficeDao;
import com.norteksoft.wf.engine.dao.WorkflowInstanceDao;
import com.norteksoft.wf.engine.entity.WorkflowInstance;
import com.norteksoft.wf.entity.TestEntity;


@Transactional(TransactionMode.ROLLBACK)
@SpringApplicationContext({"applicationContext-test.xml","applicationContext.xml","applicationContext-memcache.xml"})
public class WorkflowDocumentServiceTest extends BaseWorkflowTestCase {
	
	@SpringBeanByName
	WorkflowTaskDao workflowTaskDao;
	
	@SpringBeanByName
	WorkflowInstanceDao workflowInstanceDao;
	
	@SpringBeanByName
	OfficeDao officeDao;
	
	
	@Test
	public void getDocuments(){
		//将流程定义文件放入cache
		MemCachedUtils.add("process_id_002",  readFileContent("wf_test_8.xml"));
		
		WorkflowInstance wi= new WorkflowInstance();
		wi.setProcessCode("wangjing_process_code_33");
		wi.setProcessInstanceId("wangjing_workflow_1");
		wi.setWorkflowDefinitionId(212121l);
		wi.setProcessDefinitionId("process_id_002");
		workflowInstanceDao.save(wi);
		
		WorkflowTask task = new WorkflowTask();
		task.setCode("wangjing_task_1");
		task.setActive(TaskState.COMPLETED.getIndex());
		task.setProcessInstanceId("wangjing_workflow_1");
		workflowTaskDao.save(task);
		
		Long taskId=workflowTaskDao.getTaskByCode("wangjing_task_1").get(0).getId();
		Document document = new Document();
		document.setFileName("wangjing_document_1");
		byte[] fileBody = {1,1,1,1,1,1,1,1,1};
		document.setFileBody(fileBody);
		document.setFileSize(1000);
		document.setFileType(".doc");
		document.setTaskId(taskId);
		ApiFactory.getDocumentService().saveDocument(document);
		
		List<Document> result = ApiFactory.getDocumentService().getDocuments(taskId);
		Assert.assertNotNull(result);
		Assert.assertEquals(result.get(0).getFileName(), "wangjing_document_1");
	}
	
	
	@Test
	public void getDocumentsTwo(){
		WorkflowInstance wi= new WorkflowInstance();
		wi.setProcessCode("wangjing_process_code_44");
		wi.setProcessInstanceId("wangjing_workflow_2");
		wi.setWorkflowDefinitionId(212121l);
		workflowInstanceDao.save(wi);
		
		WorkflowTask task = new WorkflowTask();
		task.setCode("wangjing_task_2");
		task.setActive(TaskState.COMPLETED.getIndex());
		task.setProcessInstanceId("wangjing_workflow_2");
		workflowTaskDao.save(task);
		
		Long taskId=workflowTaskDao.getTaskByCode("wangjing_task_2").get(0).getId();
		Document document = new Document();
		document.setFileName("wangjing_document_2");
		byte[] fileBody = {1,1,1,1,1,1,1,1,1};
		document.setFileBody(fileBody);
		document.setFileSize(1000);
		document.setFileType(".doc");
		document.setTaskId(taskId);
		ApiFactory.getDocumentService().saveDocument(document);
		
		List<Document> result = ApiFactory.getDocumentService().getDocuments("wangjing_workflow_2");
		Assert.assertNotNull(result);
		Assert.assertEquals(result.get(0).getFileName(), "wangjing_document_2");
	}
	
	
	@Test
	public void getDocumentsThree(){
		WorkflowInstance wi= new WorkflowInstance();
		wi.setProcessCode("wangjing_process_code_44");
		wi.setProcessInstanceId("wangjing_workflow_2");
		wi.setWorkflowDefinitionId(212121l);
		workflowInstanceDao.save(wi);
		
		WorkflowTask task = new WorkflowTask();
		task.setCode("wangjing_task_2");
		task.setActive(TaskState.COMPLETED.getIndex());
		task.setProcessInstanceId("wangjing_workflow_2");
		workflowTaskDao.save(task);
		
		Long taskId=workflowTaskDao.getTaskByCode("wangjing_task_2").get(0).getId();
		Document document = new Document();
		document.setFileName("wangjing_document_2");
		byte[] fileBody = {1,1,1,1,1,1,1,1,1};
		document.setFileBody(fileBody);
		document.setFileSize(1000);
		document.setFileType(".doc");
		document.setTaskId(taskId);
		document.setWorkflowId("wangjing_workflow_2");
		ApiFactory.getDocumentService().saveDocument(document);
		
		TestEntity testEntity = new TestEntity();
		WorkflowInfo workflowInfo = new WorkflowInfo();
		workflowInfo.setWorkflowId("wangjing_workflow_2");
		testEntity.setWorkflowInfo(workflowInfo);
		
		List<Document> result = ApiFactory.getDocumentService().getDocuments(testEntity);
		Assert.assertNotNull(result);
		Assert.assertEquals(result.get(0).getFileName(), "wangjing_document_2");
	}
	
	
	@Test
	public void getDocumentsFour(){
		WorkflowInstance wi= new WorkflowInstance();
		wi.setProcessCode("wangjing_process_code_44");
		wi.setProcessInstanceId("wangjing_workflow_2");
		wi.setWorkflowDefinitionId(212121l);
		workflowInstanceDao.save(wi);
		
		WorkflowTask task = new WorkflowTask();
		task.setCode("wangjing_task_2");
		task.setActive(TaskState.COMPLETED.getIndex());
		task.setProcessInstanceId("wangjing_workflow_2");
		workflowTaskDao.save(task);
		
		Long taskId=workflowTaskDao.getTaskByCode("wangjing_task_2").get(0).getId();
		Document document = new Document();
		document.setFileName("wangjing_document_2");
		byte[] fileBody = {1,1,1,1,1,1,1,1,1};
		document.setFileBody(fileBody);
		document.setFileSize(1000);
		document.setFileType(".doc");
		document.setTaskId(taskId);
		document.setWorkflowId("wangjing_workflow_2");
		ApiFactory.getDocumentService().saveDocument(document);
		
		
		List<Document> result = ApiFactory.getDocumentService().getDocuments("wangjing_workflow_2");
		Assert.assertNotNull(result);
		Assert.assertEquals(result.get(0).getFileName(), "wangjing_document_2");
	}
	
	@Test
	public void getAllDocuments(){
		WorkflowInstance wi= new WorkflowInstance();
		wi.setProcessCode("wangjing_process_code_44");
		wi.setProcessInstanceId("wangjing_workflow_2");
		wi.setWorkflowDefinitionId(212121l);
		workflowInstanceDao.save(wi);
		
		WorkflowTask task = new WorkflowTask();
		task.setCode("wangjing_task_2");
		task.setActive(TaskState.COMPLETED.getIndex());
		task.setProcessInstanceId("wangjing_workflow_2");
		workflowTaskDao.save(task);
		
		Long taskId=workflowTaskDao.getTaskByCode("wangjing_task_2").get(0).getId();
		com.norteksoft.wf.engine.entity.Document document = new com.norteksoft.wf.engine.entity.Document();
		document.setFileName("wangjing_document_2");
		document.setFileSize(1000);
		document.setFileType(".doc");
		document.setTaskId(taskId);
		document.setWorkflowId("wangjing_workflow_2");
		document.setTaskMode(TaskProcessingMode.TYPE_APPROVAL);
		officeDao.save(document);
		
		List<Document> result = ApiFactory.getDocumentService().getAllDocuments("wangjing_workflow_2",TaskProcessingMode.TYPE_APPROVAL);
		Assert.assertNotNull(result);
		Assert.assertEquals(result.get(0).getFileName(), "wangjing_document_2");
	}
	
	
	@Test
	public void getDocumentsExcludeTaskMode(){
		WorkflowInstance wi= new WorkflowInstance();
		wi.setProcessCode("wangjing_process_code_44");
		wi.setProcessInstanceId("wangjing_workflow_2");
		wi.setWorkflowDefinitionId(212121l);
		workflowInstanceDao.save(wi);
		
		WorkflowTask task = new WorkflowTask();
		task.setCode("wangjing_task_2");
		task.setActive(TaskState.COMPLETED.getIndex());
		task.setProcessInstanceId("wangjing_workflow_2");
		workflowTaskDao.save(task);
		
		Long taskId=workflowTaskDao.getTaskByCode("wangjing_task_2").get(0).getId();
		com.norteksoft.wf.engine.entity.Document document = new com.norteksoft.wf.engine.entity.Document();
		document.setFileName("wangjing_document_2");
		document.setFileSize(1000);
		document.setFileType(".doc");
		document.setTaskId(taskId);
		document.setWorkflowId("wangjing_workflow_2");
		document.setTaskMode(TaskProcessingMode.TYPE_APPROVAL);
		officeDao.save(document);
		
		com.norteksoft.wf.engine.entity.Document document2 = new com.norteksoft.wf.engine.entity.Document();
		document2.setFileName("wangjing_document_3");
		document2.setFileSize(1000);
		document2.setFileType(".doc");
		document2.setTaskId(taskId);
		document2.setWorkflowId("wangjing_workflow_2");
		document2.setTaskMode(TaskProcessingMode.TYPE_ASSIGN);
		officeDao.save(document2);
		
		List<Document> result = ApiFactory.getDocumentService().getDocumentsExcludeTaskMode("wangjing_workflow_2",TaskProcessingMode.TYPE_APPROVAL);
		Assert.assertNotNull(result);
		Assert.assertEquals(result.get(0).getFileName(), "wangjing_document_3");
	}
	
	
	@Test
	public void getDocumentsExcludeTaskModeTwo(){
		WorkflowInstance wi= new WorkflowInstance();
		wi.setProcessCode("wangjing_process_code_44");
		wi.setProcessInstanceId("wangjing_workflow_2");
		wi.setWorkflowDefinitionId(212121l);
		workflowInstanceDao.save(wi);
		
		WorkflowTask task = new WorkflowTask();
		task.setCode("wangjing_task_2");
		task.setActive(TaskState.COMPLETED.getIndex());
		task.setProcessInstanceId("wangjing_workflow_2");
		workflowTaskDao.save(task);
		
		Long taskId=workflowTaskDao.getTaskByCode("wangjing_task_2").get(0).getId();
		com.norteksoft.wf.engine.entity.Document document = new com.norteksoft.wf.engine.entity.Document();
		document.setFileName("wangjing_document_2");
		document.setFileSize(1000);
		document.setFileType(".doc");
		document.setTaskId(taskId);
		document.setWorkflowId("wangjing_workflow_2");
		document.setTaskMode(TaskProcessingMode.TYPE_APPROVAL);
		officeDao.save(document);
		
		com.norteksoft.wf.engine.entity.Document document2 = new com.norteksoft.wf.engine.entity.Document();
		document2.setFileName("wangjing_document_3");
		document2.setFileSize(1000);
		document2.setFileType(".doc");
		document2.setTaskId(taskId);
		document2.setWorkflowId("wangjing_workflow_2");
		document2.setTaskMode(TaskProcessingMode.TYPE_ASSIGN);
		officeDao.save(document2);
		
		TestEntity testEntity = new TestEntity();
		WorkflowInfo workflowInfo = new WorkflowInfo();
		workflowInfo.setWorkflowId("wangjing_workflow_2");
		testEntity.setWorkflowInfo(workflowInfo);
		
		List<Document> result = ApiFactory.getDocumentService().getDocumentsExcludeTaskMode(testEntity,TaskProcessingMode.TYPE_APPROVAL);
		Assert.assertNotNull(result);
		Assert.assertEquals(result.get(0).getFileName(), "wangjing_document_3");
	}
	
	
	@Test
	public void getDocumentsFive(){
		WorkflowInstance wi= new WorkflowInstance();
		wi.setProcessCode("wangjing_process_code_44");
		wi.setProcessInstanceId("wangjing_workflow_2");
		wi.setWorkflowDefinitionId(212121l);
		workflowInstanceDao.save(wi);
		
		WorkflowTask task = new WorkflowTask();
		task.setCode("wangjing_task_2");
		task.setName("wangjing_task_name");
		task.setActive(TaskState.COMPLETED.getIndex());
		task.setProcessInstanceId("wangjing_workflow_2");
		workflowTaskDao.save(task);
		
		Long taskId=workflowTaskDao.getTaskByCode("wangjing_task_2").get(0).getId();
		com.norteksoft.wf.engine.entity.Document document = new com.norteksoft.wf.engine.entity.Document();
		document.setFileName("wangjing_document_2");
		document.setFileSize(1000);
		document.setFileType(".doc");
		document.setTaskId(taskId);
		document.setTaskName("wangjing_task_name");
		document.setWorkflowId("wangjing_workflow_2");
		document.setTaskMode(TaskProcessingMode.TYPE_APPROVAL);
		officeDao.save(document);
		
		TestEntity testEntity = new TestEntity();
		WorkflowInfo workflowInfo = new WorkflowInfo();
		workflowInfo.setWorkflowId("wangjing_workflow_2");
		testEntity.setWorkflowInfo(workflowInfo);
		
		List<Document> result = ApiFactory.getDocumentService().getDocuments(testEntity,"wangjing_task_name");
		Assert.assertNotNull(result);
		Assert.assertEquals(result.get(0).getTaskName(), "wangjing_task_name");
	}
	
	
	@Test
	public void getDocumentsSix(){
		WorkflowInstance wi= new WorkflowInstance();
		wi.setProcessCode("wangjing_process_code_44");
		wi.setProcessInstanceId("wangjing_workflow_2");
		wi.setWorkflowDefinitionId(212121l);
		workflowInstanceDao.save(wi);
		
		WorkflowTask task = new WorkflowTask();
		task.setCode("wangjing_task_2");
		task.setName("wangjing_task_name");
		task.setActive(TaskState.COMPLETED.getIndex());
		task.setProcessInstanceId("wangjing_workflow_2");
		workflowTaskDao.save(task);
		
		Long taskId=workflowTaskDao.getTaskByCode("wangjing_task_2").get(0).getId();
		com.norteksoft.wf.engine.entity.Document document = new com.norteksoft.wf.engine.entity.Document();
		document.setFileName("wangjing_document_2");
		document.setFileSize(1000);
		document.setFileType(".doc");
		document.setTaskId(taskId);
		document.setTaskName("wangjing_task_name");
		document.setWorkflowId("wangjing_workflow_2");
		document.setTaskMode(TaskProcessingMode.TYPE_APPROVAL);
		officeDao.save(document);
		
		List<Document> result = ApiFactory.getDocumentService().getAllDocuments("wangjing_workflow_2","wangjing_task_name");
		Assert.assertNotNull(result);
		Assert.assertEquals(result.get(0).getTaskName(), "wangjing_task_name");
	}
	
	
	@Test
	public void getDocumentsExcludeTaskName(){
		WorkflowInstance wi= new WorkflowInstance();
		wi.setProcessCode("wangjing_process_code_44");
		wi.setProcessInstanceId("wangjing_workflow_2");
		wi.setWorkflowDefinitionId(212121l);
		workflowInstanceDao.save(wi);
		
		WorkflowTask task = new WorkflowTask();
		task.setCode("wangjing_task_2");
		task.setName("wangjing_task_name");
		task.setActive(TaskState.COMPLETED.getIndex());
		task.setProcessInstanceId("wangjing_workflow_2");
		workflowTaskDao.save(task);
		
		Long taskId=workflowTaskDao.getTaskByCode("wangjing_task_2").get(0).getId();
		com.norteksoft.wf.engine.entity.Document document = new com.norteksoft.wf.engine.entity.Document();
		document.setFileName("wangjing_document_2");
		document.setFileSize(1000);
		document.setFileType(".doc");
		document.setTaskId(taskId);
		document.setTaskName("wangjing_task_name");
		document.setWorkflowId("wangjing_workflow_2");
		document.setTaskMode(TaskProcessingMode.TYPE_APPROVAL);
		officeDao.save(document);
		
		WorkflowTask task2 = new WorkflowTask();
		task.setCode("wangjing_task_3");
		task.setName("wangjing_task_name_2");
		task.setActive(TaskState.COMPLETED.getIndex());
		task.setProcessInstanceId("wangjing_workflow_2");
		workflowTaskDao.save(task2);
		
		Long taskId2=workflowTaskDao.getTaskByCode("wangjing_task_3").get(0).getId();
		com.norteksoft.wf.engine.entity.Document document2 = new com.norteksoft.wf.engine.entity.Document();
		document2.setFileName("wangjing_document_3");
		document2.setFileSize(1000);
		document2.setFileType(".doc");
		document2.setTaskId(taskId2);
		document2.setTaskName("wangjing_task_name_2");
		document2.setWorkflowId("wangjing_workflow_2");
		document2.setTaskMode(TaskProcessingMode.TYPE_APPROVAL);
		officeDao.save(document2);
		
		TestEntity testEntity = new TestEntity();
		WorkflowInfo workflowInfo = new WorkflowInfo();
		workflowInfo.setWorkflowId("wangjing_workflow_2");
		testEntity.setWorkflowInfo(workflowInfo);
		
		List<Document> result = ApiFactory.getDocumentService().getDocumentsExcludeTaskName(testEntity,"wangjing_task_name");
		Assert.assertNotNull(result);
		Assert.assertEquals(result.get(0).getTaskName(), "wangjing_task_name_2");
	}
	
	@Test
	public void getDocumentsExcludeTaskNameTwo(){
		WorkflowInstance wi= new WorkflowInstance();
		wi.setProcessCode("wangjing_process_code_44");
		wi.setProcessInstanceId("wangjing_workflow_2");
		wi.setWorkflowDefinitionId(212121l);
		workflowInstanceDao.save(wi);
		
		WorkflowTask task = new WorkflowTask();
		task.setCode("wangjing_task_2");
		task.setName("wangjing_task_name");
		task.setActive(TaskState.COMPLETED.getIndex());
		task.setProcessInstanceId("wangjing_workflow_2");
		workflowTaskDao.save(task);
		
		Long taskId=workflowTaskDao.getTaskByCode("wangjing_task_2").get(0).getId();
		com.norteksoft.wf.engine.entity.Document document = new com.norteksoft.wf.engine.entity.Document();
		document.setFileName("wangjing_document_2");
		document.setFileSize(1000);
		document.setFileType(".doc");
		document.setTaskId(taskId);
		document.setTaskName("wangjing_task_name");
		document.setWorkflowId("wangjing_workflow_2");
		document.setTaskMode(TaskProcessingMode.TYPE_APPROVAL);
		officeDao.save(document);
		
		WorkflowTask task2 = new WorkflowTask();
		task.setCode("wangjing_task_3");
		task.setName("wangjing_task_name_2");
		task.setActive(TaskState.COMPLETED.getIndex());
		task.setProcessInstanceId("wangjing_workflow_2");
		workflowTaskDao.save(task2);
		
		Long taskId2=workflowTaskDao.getTaskByCode("wangjing_task_3").get(0).getId();
		com.norteksoft.wf.engine.entity.Document document2 = new com.norteksoft.wf.engine.entity.Document();
		document2.setFileName("wangjing_document_3");
		document2.setFileSize(1000);
		document2.setFileType(".doc");
		document2.setTaskId(taskId2);
		document2.setTaskName("wangjing_task_name_2");
		document2.setWorkflowId("wangjing_workflow_2");
		document2.setTaskMode(TaskProcessingMode.TYPE_APPROVAL);
		officeDao.save(document2);
		
		List<Document> result = ApiFactory.getDocumentService().getDocumentsExcludeTaskName("wangjing_workflow_2","wangjing_task_name");
		Assert.assertNotNull(result);
		Assert.assertEquals(result.get(0).getTaskName(), "wangjing_task_name_2");
	}
	
	@Test
	public void getDocumentsByCustomField(){
		WorkflowInstance wi= new WorkflowInstance();
		wi.setProcessCode("wangjing_process_code_44");
		wi.setProcessInstanceId("wangjing_workflow_2");
		wi.setWorkflowDefinitionId(212121l);
		workflowInstanceDao.save(wi);
		
		WorkflowTask task = new WorkflowTask();
		task.setCode("wangjing_task_2");
		task.setName("wangjing_task_name");
		task.setActive(TaskState.COMPLETED.getIndex());
		task.setProcessInstanceId("wangjing_workflow_2");
		workflowTaskDao.save(task);
		
		Long taskId=workflowTaskDao.getTaskByCode("wangjing_task_2").get(0).getId();
		com.norteksoft.wf.engine.entity.Document document = new com.norteksoft.wf.engine.entity.Document();
		document.setFileName("wangjing_document_2");
		document.setFileSize(1000);
		document.setFileType(".doc");
		document.setTaskId(taskId);
		document.setTaskName("wangjing_task_name");
		document.setWorkflowId("wangjing_workflow_2");
		document.setTaskMode(TaskProcessingMode.TYPE_APPROVAL);
		document.setCustomField("custom_field_1");
		officeDao.save(document);
		
		TestEntity testEntity = new TestEntity();
		WorkflowInfo workflowInfo = new WorkflowInfo();
		workflowInfo.setWorkflowId("wangjing_workflow_2");
		testEntity.setWorkflowInfo(workflowInfo);
		
		List<Document> result = ApiFactory.getDocumentService().getDocumentsByCustomField(testEntity,"custom_field_1");
		Assert.assertNotNull(result);
		Assert.assertEquals(result.get(0).getCustomField(), "custom_field_1");
	}
	
	@Test
	public void getAllDocumentsByCustomField(){
		WorkflowInstance wi= new WorkflowInstance();
		wi.setProcessCode("wangjing_process_code_44");
		wi.setProcessInstanceId("wangjing_workflow_2");
		wi.setWorkflowDefinitionId(212121l);
		workflowInstanceDao.save(wi);
		
		WorkflowTask task = new WorkflowTask();
		task.setCode("wangjing_task_2");
		task.setName("wangjing_task_name");
		task.setActive(TaskState.COMPLETED.getIndex());
		task.setProcessInstanceId("wangjing_workflow_2");
		workflowTaskDao.save(task);
		
		Long taskId=workflowTaskDao.getTaskByCode("wangjing_task_2").get(0).getId();
		com.norteksoft.wf.engine.entity.Document document = new com.norteksoft.wf.engine.entity.Document();
		document.setFileName("wangjing_document_2");
		document.setFileSize(1000);
		document.setFileType(".doc");
		document.setTaskId(taskId);
		document.setTaskName("wangjing_task_name");
		document.setWorkflowId("wangjing_workflow_2");
		document.setTaskMode(TaskProcessingMode.TYPE_APPROVAL);
		document.setCustomField("custom_field_1");
		officeDao.save(document);
		
		List<Document> result = ApiFactory.getDocumentService().getAllDocumentsByCustomField("wangjing_workflow_2","custom_field_1");
		Assert.assertNotNull(result);
		Assert.assertEquals(result.get(0).getCustomField(), "custom_field_1");
	}
	
	@Test
	public void getDocumentsExcludeCustomField(){
		WorkflowInstance wi= new WorkflowInstance();
		wi.setProcessCode("wangjing_process_code_44");
		wi.setProcessInstanceId("wangjing_workflow_2");
		wi.setWorkflowDefinitionId(212121l);
		workflowInstanceDao.save(wi);
		
		WorkflowTask task = new WorkflowTask();
		task.setCode("wangjing_task_2");
		task.setName("wangjing_task_name");
		task.setActive(TaskState.COMPLETED.getIndex());
		task.setProcessInstanceId("wangjing_workflow_2");
		workflowTaskDao.save(task);
		
		Long taskId=workflowTaskDao.getTaskByCode("wangjing_task_2").get(0).getId();
		com.norteksoft.wf.engine.entity.Document document = new com.norteksoft.wf.engine.entity.Document();
		document.setFileName("wangjing_document_2");
		document.setFileSize(1000);
		document.setFileType(".doc");
		document.setTaskId(taskId);
		document.setTaskName("wangjing_task_name");
		document.setWorkflowId("wangjing_workflow_2");
		document.setTaskMode(TaskProcessingMode.TYPE_APPROVAL);
		document.setCustomField("custom_field_1");
		officeDao.save(document);
		
		com.norteksoft.wf.engine.entity.Document document2 = new com.norteksoft.wf.engine.entity.Document();
		document2.setFileName("wangjing_document_2");
		document2.setFileSize(1000);
		document2.setFileType(".doc");
		document2.setTaskId(taskId);
		document2.setTaskName("wangjing_task_name");
		document2.setWorkflowId("wangjing_workflow_2");
		document2.setTaskMode(TaskProcessingMode.TYPE_APPROVAL);
		document2.setCustomField("custom_field_2");
		officeDao.save(document2);
		
		TestEntity testEntity = new TestEntity();
		WorkflowInfo workflowInfo = new WorkflowInfo();
		workflowInfo.setWorkflowId("wangjing_workflow_2");
		testEntity.setWorkflowInfo(workflowInfo);
		
		List<Document> result = ApiFactory.getDocumentService().getDocumentsExcludeCustomField(testEntity,"custom_field_1");
		Assert.assertNotNull(result);
		Assert.assertEquals(result.get(0).getCustomField(), "custom_field_2");
	}
	
	@Test
	public void getDocumentsExcludeCustomFieldTwo(){
		WorkflowInstance wi= new WorkflowInstance();
		wi.setProcessCode("wangjing_process_code_44");
		wi.setProcessInstanceId("wangjing_workflow_2");
		wi.setWorkflowDefinitionId(212121l);
		workflowInstanceDao.save(wi);
		
		WorkflowTask task = new WorkflowTask();
		task.setCode("wangjing_task_2");
		task.setName("wangjing_task_name");
		task.setActive(TaskState.COMPLETED.getIndex());
		task.setProcessInstanceId("wangjing_workflow_2");
		workflowTaskDao.save(task);
		
		Long taskId=workflowTaskDao.getTaskByCode("wangjing_task_2").get(0).getId();
		com.norteksoft.wf.engine.entity.Document document = new com.norteksoft.wf.engine.entity.Document();
		document.setFileName("wangjing_document_2");
		document.setFileSize(1000);
		document.setFileType(".doc");
		document.setTaskId(taskId);
		document.setTaskName("wangjing_task_name");
		document.setWorkflowId("wangjing_workflow_2");
		document.setTaskMode(TaskProcessingMode.TYPE_APPROVAL);
		document.setCustomField("custom_field_1");
		officeDao.save(document);
		
		com.norteksoft.wf.engine.entity.Document document2 = new com.norteksoft.wf.engine.entity.Document();
		document2.setFileName("wangjing_document_2");
		document2.setFileSize(1000);
		document2.setFileType(".doc");
		document2.setTaskId(taskId);
		document2.setTaskName("wangjing_task_name");
		document2.setWorkflowId("wangjing_workflow_2");
		document2.setTaskMode(TaskProcessingMode.TYPE_APPROVAL);
		document2.setCustomField("custom_field_2");
		officeDao.save(document2);
		
		List<Document> result = ApiFactory.getDocumentService().getDocumentsExcludeCustomField("wangjing_workflow_2","custom_field_1");
		Assert.assertNotNull(result);
		Assert.assertEquals(result.get(0).getCustomField(), "custom_field_2");
	}
	
	
	@Test
	public void createDocument(){
		WorkflowInstance wi= new WorkflowInstance();
		wi.setProcessCode("wangjing_process_code_44");
		wi.setProcessInstanceId("wangjing_workflow_2");
		wi.setWorkflowDefinitionId(212121l);
		workflowInstanceDao.save(wi);
		
		WorkflowTask task = new WorkflowTask();
		task.setCode("wangjing_task_2");
		task.setName("wangjing_task_name");
		task.setActive(TaskState.COMPLETED.getIndex());
		task.setProcessInstanceId("wangjing_workflow_2");
		workflowTaskDao.save(task);
		
		TestEntity testEntity = new TestEntity();
		WorkflowInfo workflowInfo = new WorkflowInfo();
		workflowInfo.setWorkflowId("wangjing_workflow_2");
		testEntity.setWorkflowInfo(workflowInfo);
		
		Document document = ApiFactory.getDocumentService().createDocument(testEntity, ".doc");
		Assert.assertNotNull(document);
	}
	
	
	@Test
	public void createDocumentTwo(){
		WorkflowInstance wi= new WorkflowInstance();
		wi.setProcessCode("wangjing_process_code_44");
		wi.setProcessInstanceId("wangjing_workflow_2");
		wi.setWorkflowDefinitionId(212121l);
		workflowInstanceDao.save(wi);
		
		WorkflowTask task = new WorkflowTask();
		task.setCode("wangjing_task_2");
		task.setName("wangjing_task_name");
		task.setActive(TaskState.COMPLETED.getIndex());
		task.setProcessInstanceId("wangjing_workflow_2");
		workflowTaskDao.save(task);
		
		Document document = ApiFactory.getDocumentService().createDocument("wangjing_workflow_2", ".doc");
		Assert.assertNotNull(document);
	}
	
	@Test
	public void getDocument(){
		//将流程定义文件放入cache
		MemCachedUtils.add("process_id_003",  readFileContent("wf_test_8.xml"));
		
		WorkflowInstance wi= new WorkflowInstance();
		wi.setProcessCode("wangjing_process_code_33");
		wi.setProcessInstanceId("wangjing_workflow_1");
		wi.setWorkflowDefinitionId(212121l);
		wi.setProcessDefinitionId("process_id_003");
		workflowInstanceDao.save(wi);
		
		WorkflowTask task = new WorkflowTask();
		task.setCode("wangjing_task_1");
		task.setActive(TaskState.COMPLETED.getIndex());
		task.setProcessInstanceId("wangjing_workflow_1");
		workflowTaskDao.save(task);
		
		Long taskId=workflowTaskDao.getTaskByCode("wangjing_task_1").get(0).getId();
		Document document = new Document();
		document.setFileName("wangjing_document_1");
		byte[] fileBody = {1,1,1,1,1,1,1,1,1};
		document.setFileBody(fileBody);
		document.setFileSize(1000);
		document.setFileType(".doc");
		document.setTaskId(taskId);
		ApiFactory.getDocumentService().saveDocument(document);
		List<Document> list = ApiFactory.getDocumentService().getDocuments(taskId);
		Assert.assertNotNull(list);
		
		Document result = ApiFactory.getDocumentService().getDocument(list.get(0).getId());
		Assert.assertNotNull(result);
		Assert.assertEquals(result.getFileName(), "wangjing_document_1");
	}
	
	@Test
	public void saveDocument(){
		WorkflowInstance wi= new WorkflowInstance();
		wi.setProcessCode("wangjing_process_code_33");
		wi.setProcessInstanceId("wangjing_workflow_1");
		wi.setWorkflowDefinitionId(212121l);
		workflowInstanceDao.save(wi);
		
		WorkflowTask task = new WorkflowTask();
		task.setCode("wangjing_task_1");
		task.setActive(TaskState.COMPLETED.getIndex());
		task.setProcessInstanceId("wangjing_workflow_1");
		workflowTaskDao.save(task);
		
		Long taskId=workflowTaskDao.getTaskByCode("wangjing_task_1").get(0).getId();
		Document document = new Document();
		document.setFileName("wangjing_document_1");
		byte[] fileBody = {1,1,1,1,1,1,1,1,1};
		document.setFileBody(fileBody);
		document.setFileSize(1000);
		document.setFileType(".doc");
		document.setCustomField("custom_field_1");
		document.setTaskId(taskId);
		
		TestEntity testEntity = new TestEntity();
		WorkflowInfo workflowInfo = new WorkflowInfo();
		workflowInfo.setWorkflowId("wangjing_workflow_1");
		testEntity.setWorkflowInfo(workflowInfo);
		
		ApiFactory.getDocumentService().saveDocument(document,testEntity);
		List<Document> result = ApiFactory.getDocumentService().getAllDocumentsByCustomField("wangjing_workflow_1","custom_field_1");
		Assert.assertNotNull(result);
		Assert.assertEquals(result.get(0).getFileName(), "wangjing_document_1");
	}
	
	@Test
	public void deleteDocument(){
		WorkflowInstance wi= new WorkflowInstance();
		wi.setProcessCode("wangjing_process_code_33");
		wi.setProcessInstanceId("wangjing_workflow_1");
		wi.setWorkflowDefinitionId(212121l);
		workflowInstanceDao.save(wi);
		
		WorkflowTask task = new WorkflowTask();
		task.setCode("wangjing_task_1");
		task.setActive(TaskState.COMPLETED.getIndex());
		task.setProcessInstanceId("wangjing_workflow_1");
		workflowTaskDao.save(task);
		
		Long taskId=workflowTaskDao.getTaskByCode("wangjing_task_1").get(0).getId();
		Document document = new Document();
		document.setFileName("wangjing_document_1");
		byte[] fileBody = {1,1,1,1,1,1,1,1,1};
		document.setFileBody(fileBody);
		document.setFileSize(1000);
		document.setFileType(".doc");
		document.setCustomField("custom_field_1");
		document.setTaskId(taskId);
		ApiFactory.getDocumentService().saveDocument(document);
		
		List<Document> list = ApiFactory.getDocumentService().getAllDocumentsByCustomField("wangjing_workflow_1","custom_field_1");
		Assert.assertNotNull(list);
		ApiFactory.getDocumentService().deleteDocument(list.get(0).getId());
		List<Document> result = ApiFactory.getDocumentService().getAllDocumentsByCustomField("wangjing_workflow_1","custom_field_1");
		Assert.assertEquals(result.size(),0);
	}
	
}
