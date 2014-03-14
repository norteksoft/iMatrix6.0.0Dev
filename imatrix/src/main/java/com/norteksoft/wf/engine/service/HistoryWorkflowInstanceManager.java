package com.norteksoft.wf.engine.service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import com.norteksoft.product.util.ContextUtils;
import com.norteksoft.wf.engine.dao.HistoryOpinionDao;
import com.norteksoft.wf.engine.dao.HistoryWorkflowInstanceDao;
import com.norteksoft.wf.engine.entity.HistoryOpinion;
import com.norteksoft.wf.engine.entity.HistoryWorkflowInstance;
import com.norteksoft.wf.engine.entity.InstanceHistory;
import com.norteksoft.wf.engine.entity.WorkflowInstance;
@Service
@Transactional
public class HistoryWorkflowInstanceManager{
	@Autowired
	private HistoryWorkflowInstanceDao historyWorkflowInstanceDao;
	@Autowired
	private HistoryOpinionDao historyOpinionDao;

	/**
	 * 根据ID查询对象
	 * @param id
	 * @return
	 */
	public HistoryWorkflowInstance getHistoryWorkflowInstance(Long id){
		return historyWorkflowInstanceDao.get(id);
	}
	
	
	public Set<HistoryWorkflowInstance> getHistoryWorkflowInstances(Set<Long> workflowIds) {
		Set<HistoryWorkflowInstance> instances = new HashSet<HistoryWorkflowInstance>();
		for(Long workflowId : workflowIds){
			instances.add(this.getHistoryWorkflowInstance(workflowId));
		}
		return instances;
	}
	
	/**
	 *  根据processInstanceId查询流程实例
	 * @param processId
	 * @return
	 */
	public HistoryWorkflowInstance getWorkflowInstance(String processInstanceId){
		Assert.notNull(ContextUtils.getCompanyId(),"companyId不能为null");
		return historyWorkflowInstanceDao.getInstanceByJbpmInstanceId(processInstanceId,ContextUtils.getCompanyId());
	}	
	public List<HistoryWorkflowInstance>  getHistoryInstancesByFormId(Long formId){
		return historyWorkflowInstanceDao.getHistoryInstancesByFormId(formId);
	}
	
	/**
	 * 查询流程实例的办理意见
	 */
	public List<HistoryOpinion> getOpinionsByInstanceId(String workflowId,Long companyId) {
		if(workflowId == null) throw new RuntimeException("没有给定查询意见集合的查询条件：流程实例ID");
		if(companyId == null) throw new RuntimeException("没有给定查询意见集合的查询条件：公司ID.");
		return historyOpinionDao.getOpinionsByInstanceId(workflowId,companyId);
	}


	public HistoryWorkflowInstance getHistoryWorkflowInstance(
			String processInstanceId) {
		return historyWorkflowInstanceDao.getHistoryWorkflowInstance(processInstanceId);
	}


	/**
	 * 根据父流程的workflowId和环节名获得它的子流程实例(历史实例表中查询)
	 * @param parentWorkflowId
	 * @return
	 */
	public List<HistoryWorkflowInstance> getSubProcessHistoryInstanceByTaskName(String parentWorkflowId,String tacheName){
		return historyWorkflowInstanceDao.getSubProcessHistoryInstanceByTaskName( parentWorkflowId,tacheName);
	}


	public void saveHistoryWorkflowInstance(
			HistoryWorkflowInstance historyInctance) {
		historyWorkflowInstanceDao.save(historyInctance);
		
	}
}
	
