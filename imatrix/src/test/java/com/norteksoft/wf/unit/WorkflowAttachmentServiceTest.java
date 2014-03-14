package com.norteksoft.wf.unit;

import java.util.List;

import junit.framework.Assert;

import org.apache.commons.lang.StringUtils;
import org.junit.Before;
import org.junit.Test;
import org.unitils.database.annotations.Transactional;
import org.unitils.database.util.TransactionMode;
import org.unitils.spring.annotation.SpringApplicationContext;
import org.unitils.spring.annotation.SpringBeanByName;

import com.norteksoft.product.api.ApiFactory;
import com.norteksoft.product.api.entity.WorkflowAttachment;
import com.norteksoft.product.util.MemCachedUtils;
import com.norteksoft.product.util.ParameterUtils;
import com.norteksoft.task.base.enumeration.TaskProcessingMode;
import com.norteksoft.task.base.enumeration.TaskState;
import com.norteksoft.task.dao.WorkflowTaskDao;
import com.norteksoft.task.entity.WorkflowTask;
import com.norteksoft.wf.engine.client.WorkflowInfo;
import com.norteksoft.wf.engine.dao.WorkflowAttachmentDao;
import com.norteksoft.wf.engine.dao.WorkflowInstanceDao;
import com.norteksoft.wf.engine.entity.WorkflowInstance;
import com.norteksoft.wf.entity.TestEntity;


@Transactional(TransactionMode.ROLLBACK)
@SpringApplicationContext({"applicationContext-test.xml","applicationContext.xml","applicationContext-memcache.xml"})
public class WorkflowAttachmentServiceTest extends BaseWorkflowTestCase {
	
	@SpringBeanByName
	WorkflowAttachmentDao workflowAttachmentDao;
	
	@SpringBeanByName
	WorkflowTaskDao workflowTaskDao;
	
	@SpringBeanByName
	WorkflowInstanceDao workflowInstanceDao;
	
	@Before
	public void beforeSet() throws Exception {
		ParameterUtils.setParameters(getPrmt(1L, 1L, "wangjing"));
	}
	
	@Test
	public void saveAttachmentTest(){
		WorkflowAttachment attachment = new WorkflowAttachment();
		attachment.setFileName("wangjingtest1");
		attachment.setFileType(".doc");
		attachment.setFileSize(10000f);
		byte[] fileBody = {1,1,1,1,1,1,1,1,1};
		attachment.setFileBody(fileBody);
		
		ApiFactory.getAttachmentService().saveAttachment(attachment);
		com.norteksoft.wf.engine.entity.WorkflowAttachment result = workflowAttachmentDao.getAttachment(10000f,"wangjingtest1",".doc");
		Assert.assertNotNull(result);
	}

	

	@Test
	public void saveAttachmentTwoTest(){
		WorkflowAttachment attachment = new WorkflowAttachment();
		attachment.setFileName("wangjingtest2");
		attachment.setFileType(".doc");
		attachment.setFileSize(10000f);
		byte[] fileBody = {1,1,1,1,1,1,1,1,1};
		attachment.setFileBody(fileBody);
		
		ApiFactory.getAttachmentService().saveAttachment(attachment,"workflow_6.1370001");
		com.norteksoft.wf.engine.entity.WorkflowAttachment result = workflowAttachmentDao.getAttachment(10000f,"wangjingtest2",".doc");
		Assert.assertNotNull(result);
	}
	
	@Test
	public void saveAttachmentFourTest(){
		WorkflowAttachment attachment = new WorkflowAttachment();
		attachment.setFileName("wangjingtest2");
		attachment.setFileType(".doc");
		attachment.setFileSize(10000f);
		byte[] fileBody = {1,1,1,1,1,1,1,1,1};
		attachment.setFileBody(fileBody);
		
		TestEntity testEntity = new TestEntity();
		WorkflowInfo workflowInfo = new WorkflowInfo();
		workflowInfo.setWorkflowId("workflow_6.1370001");
		testEntity.setWorkflowInfo(workflowInfo);
		
		ApiFactory.getAttachmentService().saveAttachment(attachment,testEntity);
		com.norteksoft.wf.engine.entity.WorkflowAttachment result = workflowAttachmentDao.getAttachment(10000f,"wangjingtest2",".doc");
		Assert.assertNotNull(result);
	}
	
	@Test
	public void saveAttachmentTHreeTest(){
		WorkflowAttachment attachment = new WorkflowAttachment();
		attachment.setFileName("wangjingtest3");
		attachment.setFileType(".doc");
		attachment.setFileSize(10000f);
		byte[] fileBody = {1,1,1,1,1,1,1,1,1};
		attachment.setFileBody(fileBody);
		
		WorkflowTask task = new WorkflowTask();
		task.setCode("wangjing_111111");
		workflowTaskDao.save(task);
		
		ApiFactory.getAttachmentService().saveAttachment(attachment,workflowTaskDao.getTaskByCode("wangjing_111111").get(0).getId());
		com.norteksoft.wf.engine.entity.WorkflowAttachment result = workflowAttachmentDao.getAttachment(10000f,"wangjingtest3",".doc");
		Assert.assertNotNull(result);
	}
	
	
	@Test
	public void deleteAttachment(){
		WorkflowAttachment attachment = new WorkflowAttachment();
		attachment.setFileName("wangjingtest4");
		attachment.setFileType(".doc");
		attachment.setFileSize(10000f);
		byte[] fileBody = {1,1,1,1,1,1,1,1,1};
		attachment.setFileBody(fileBody);
		
		ApiFactory.getAttachmentService().saveAttachment(attachment);
		com.norteksoft.wf.engine.entity.WorkflowAttachment workflowAttachment = workflowAttachmentDao.getAttachment(10000f,"wangjingtest4",".doc");
		ApiFactory.getAttachmentService().deleteAttachment(workflowAttachment.getId());
		com.norteksoft.wf.engine.entity.WorkflowAttachment result = workflowAttachmentDao.getAttachment(10000f,"wangjingtest4",".doc");
		Assert.assertNull(result);
	}
	
	@Test
	public void getAttachment(){
		WorkflowAttachment attachment = new WorkflowAttachment();
		attachment.setFileName("wangjingtest5");
		attachment.setFileType(".doc");
		attachment.setFileSize(10000f);
		byte[] fileBody = {1,1,1,1,1,1,1,1,1};
		attachment.setFileBody(fileBody);
		
		ApiFactory.getAttachmentService().saveAttachment(attachment);
		com.norteksoft.wf.engine.entity.WorkflowAttachment workflowAttachment = workflowAttachmentDao.getAttachment(10000f,"wangjingtest5",".doc");
		WorkflowAttachment result = ApiFactory.getAttachmentService().getAttachment(workflowAttachment.getId());
		Assert.assertNotNull(result);
	}
	
	@Test
	public void getAttachments(){
		//将流程定义文件放入cache
		MemCachedUtils.add("process_id_001",  readFileContent("wf_test_8.xml"));
		
		WorkflowAttachment attachment = new WorkflowAttachment();
		attachment.setFileName("wangjingtest6");
		attachment.setFileType(".doc");
		attachment.setFileSize(10000f);
		byte[] fileBody = {1,1,1,1,1,1,1,1,1};
		attachment.setFileBody(fileBody);
		
		WorkflowInstance wi= new WorkflowInstance();
		wi.setProcessCode("wangjing_process_code");
		wi.setProcessInstanceId("workflow_aaaaaaaaaaaa");
		wi.setProcessDefinitionId("process_id_001");
		workflowInstanceDao.save(wi);
		
		WorkflowTask task = new WorkflowTask();
		task.setCode("wangjing_333333");
		task.setActive(TaskState.COMPLETED.getIndex());
		task.setProcessInstanceId(workflowInstanceDao.getInstanceByCode("wangjing_process_code").get(0).getProcessInstanceId());
		workflowTaskDao.save(task);
		
		
		
		Long taskId=workflowTaskDao.getTaskByCode("wangjing_333333").get(0).getId();
		ApiFactory.getAttachmentService().saveAttachment(attachment,taskId);
		List<WorkflowAttachment> result = ApiFactory.getAttachmentService().getAttachments(taskId);
		Assert.assertNotNull(result);
	}
	
	@Test
	public void getAttachmentsTwo(){
		WorkflowAttachment attachment = new WorkflowAttachment();
		attachment.setFileName("wangjingtest7");
		attachment.setFileType(".doc");
		attachment.setFileSize(10000f);
		byte[] fileBody = {1,1,1,1,1,1,1,1,1};
		attachment.setFileBody(fileBody);
		
		WorkflowInstance wi= new WorkflowInstance();
		wi.setProcessCode("wangjing_process_code_1");
		wi.setProcessInstanceId("workflow_bbbbbbbbbbbb");
		workflowInstanceDao.save(wi);
		
		WorkflowTask task = new WorkflowTask();
		task.setCode("wangjing_4444444");
		task.setActive(TaskState.COMPLETED.getIndex());
		task.setProcessInstanceId(workflowInstanceDao.getInstanceByCode("wangjing_process_code_1").get(0).getProcessInstanceId());
		workflowTaskDao.save(task);
		
		
		Long taskId=workflowTaskDao.getTaskByCode("wangjing_4444444").get(0).getId();
		ApiFactory.getAttachmentService().saveAttachment(attachment,taskId);
		List<WorkflowAttachment> result = ApiFactory.getAttachmentService().getAllAttachments("workflow_bbbbbbbbbbbb");
		Assert.assertNotNull(result);
	}
	
	@Test
	public void getAttachmentsFour(){
		WorkflowAttachment attachment = new WorkflowAttachment();
		attachment.setFileName("wangjingtest7");
		attachment.setFileType(".doc");
		attachment.setFileSize(10000f);
		byte[] fileBody = {1,1,1,1,1,1,1,1,1};
		attachment.setFileBody(fileBody);
		
		WorkflowInstance wi= new WorkflowInstance();
		wi.setProcessCode("wangjing_process_code_1");
		wi.setProcessInstanceId("workflow_bbbbbbbbbbbb");
		workflowInstanceDao.save(wi);
		
		WorkflowTask task = new WorkflowTask();
		task.setCode("wangjing_4444444");
		task.setActive(TaskState.COMPLETED.getIndex());
		task.setProcessInstanceId(workflowInstanceDao.getInstanceByCode("wangjing_process_code_1").get(0).getProcessInstanceId());
		workflowTaskDao.save(task);
		
		TestEntity testEntity = new TestEntity();
		WorkflowInfo workflowInfo = new WorkflowInfo();
		workflowInfo.setWorkflowId("workflow_bbbbbbbbbbbb");
		testEntity.setWorkflowInfo(workflowInfo);
		
		ParameterUtils.setParameters(getPrmt(1L, 33L, "ldx","ems"));
		
		Long taskId=workflowTaskDao.getTaskByCode("wangjing_4444444").get(0).getId();
		ApiFactory.getAttachmentService().saveAttachment(attachment,taskId);
		List<WorkflowAttachment> result = ApiFactory.getAttachmentService().getAttachments(testEntity);
		Assert.assertNotNull(result);
	}
	
	@Test
	public void getAttachmentsFive(){
		WorkflowInstance wi= new WorkflowInstance();
		wi.setProcessCode("wangjing_process_code_2");
		wi.setProcessInstanceId("workflow_ccccccccc");
		workflowInstanceDao.save(wi);
		
		com.norteksoft.wf.engine.entity.WorkflowAttachment attachment = new com.norteksoft.wf.engine.entity.WorkflowAttachment();
		attachment.setFileName("wangjingtest8");
		attachment.setFileType(".doc");
		attachment.setFileSize(10000f);
		attachment.setTaskMode(TaskProcessingMode.TYPE_APPROVAL);
		attachment.setWorkflowId(workflowInstanceDao.getInstanceByCode("wangjing_process_code_2").get(0).getProcessInstanceId());
		workflowAttachmentDao.save(attachment);
		
		TestEntity testEntity = new TestEntity();
		WorkflowInfo workflowInfo = new WorkflowInfo();
		workflowInfo.setWorkflowId("workflow_ccccccccc");
		testEntity.setWorkflowInfo(workflowInfo);
		
		List<WorkflowAttachment> result = ApiFactory.getAttachmentService().getAttachments(testEntity,TaskProcessingMode.TYPE_APPROVAL);
		Assert.assertNotNull(result);
	}
	
	@Test
	public void getAttachmentsThree(){
		WorkflowInstance wi= new WorkflowInstance();
		wi.setProcessCode("wangjing_process_code_2");
		wi.setProcessInstanceId("workflow_ccccccccc");
		workflowInstanceDao.save(wi);
		
		com.norteksoft.wf.engine.entity.WorkflowAttachment attachment = new com.norteksoft.wf.engine.entity.WorkflowAttachment();
		attachment.setFileName("wangjingtest8");
		attachment.setFileType(".doc");
		attachment.setFileSize(10000f);
		attachment.setTaskMode(TaskProcessingMode.TYPE_APPROVAL);
		attachment.setWorkflowId(workflowInstanceDao.getInstanceByCode("wangjing_process_code_2").get(0).getProcessInstanceId());
		workflowAttachmentDao.save(attachment);
		
		List<WorkflowAttachment> result = ApiFactory.getAttachmentService().getAllAttachments("workflow_ccccccccc",TaskProcessingMode.TYPE_APPROVAL);
		Assert.assertNotNull(result);
	}
	
	@Test
	public void getAttachmentsExcludeTaskMode(){
		WorkflowInstance wi= new WorkflowInstance();
		wi.setProcessCode("wangjing_process_code_3");
		wi.setProcessInstanceId("workflow_ddddddddd");
		workflowInstanceDao.save(wi);
		
		com.norteksoft.wf.engine.entity.WorkflowAttachment attachment = new com.norteksoft.wf.engine.entity.WorkflowAttachment();
		attachment.setFileName("wangjingtest8");
		attachment.setFileType(".doc");
		attachment.setFileSize(10000f);
		attachment.setTaskMode(TaskProcessingMode.TYPE_APPROVAL);
		attachment.setWorkflowId(workflowInstanceDao.getInstanceByCode("wangjing_process_code_3").get(0).getProcessInstanceId());
		workflowAttachmentDao.save(attachment);
		
		List<WorkflowAttachment> result = ApiFactory.getAttachmentService().getAttachmentsExcludeTaskMode("workflow_ddddddddd",TaskProcessingMode.TYPE_ASSIGN);
		Assert.assertNotNull(result);
	}
	
	

	@Test
	public void getAllAttachmentsByCustomField(){
		WorkflowInstance wi= new WorkflowInstance();
		wi.setProcessCode("wangjing_process_code_5");
		wi.setProcessInstanceId("workflow_fffffffff");
		workflowInstanceDao.save(wi);
		
		com.norteksoft.wf.engine.entity.WorkflowAttachment attachment = new com.norteksoft.wf.engine.entity.WorkflowAttachment();
		attachment.setFileName("wangjingtest10");
		attachment.setFileType(".doc");
		attachment.setFileSize(10000f);
		attachment.setCustomField("gfg");
		attachment.setWorkflowId(workflowInstanceDao.getInstanceByCode("wangjing_process_code_5").get(0).getProcessInstanceId());
		workflowAttachmentDao.save(attachment);
		
		List<WorkflowAttachment> result = ApiFactory.getAttachmentService().getAllAttachmentsByCustomField("workflow_fffffffff","gfg");
		Assert.assertNotNull(result);
	}
	
	
	@Test
	public void getAttachmentsExcludeCustomField(){
		WorkflowInstance wi= new WorkflowInstance();
		wi.setProcessCode("wangjing_process_code_6");
		wi.setProcessInstanceId("workflow_ggggggggggg");
		workflowInstanceDao.save(wi);
		
		com.norteksoft.wf.engine.entity.WorkflowAttachment attachment = new com.norteksoft.wf.engine.entity.WorkflowAttachment();
		attachment.setFileName("wangjingtest11");
		attachment.setFileType(".doc");
		attachment.setFileSize(10000f);
		attachment.setCustomField("gfg");
		attachment.setWorkflowId(workflowInstanceDao.getInstanceByCode("wangjing_process_code_6").get(0).getProcessInstanceId());
		workflowAttachmentDao.save(attachment);
		
		com.norteksoft.wf.engine.entity.WorkflowAttachment attachment2 = new com.norteksoft.wf.engine.entity.WorkflowAttachment();
		attachment2.setFileName("wangjingfdsf");
		attachment2.setFileType(".doc");
		attachment2.setFileSize(10000f);
		attachment2.setCustomField("dfddd");
		attachment2.setWorkflowId(workflowInstanceDao.getInstanceByCode("wangjing_process_code_6").get(0).getProcessInstanceId());
		workflowAttachmentDao.save(attachment2);
		
		List<WorkflowAttachment> result = ApiFactory.getAttachmentService().getAttachmentsExcludeCustomField("workflow_ggggggggggg","gfg");
		for(WorkflowAttachment workflowAttachment:result){
			if(StringUtils.isEmpty(workflowAttachment.getCustomField()))continue;
		    Assert.assertFalse(workflowAttachment.getCustomField().equals("gfg"));
		}
	}
	
}
