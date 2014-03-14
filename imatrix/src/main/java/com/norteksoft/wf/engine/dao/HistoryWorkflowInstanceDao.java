package com.norteksoft.wf.engine.dao;


import java.util.List;

import org.springframework.stereotype.Repository;

import com.norteksoft.product.orm.hibernate.HibernateDao;
import com.norteksoft.product.util.ContextUtils;
import com.norteksoft.wf.engine.entity.HistoryWorkflowInstance;
import com.norteksoft.wf.engine.entity.WorkflowInstance;

@Repository
public class HistoryWorkflowInstanceDao extends HibernateDao<HistoryWorkflowInstance, Long>{

	public List<HistoryWorkflowInstance> getSubWorkflowInstances(String processInstanceId,
			Long companyId, Long systemId) {
		String hql = "from HistoryWorkflowInstance wi where wi.companyId = ? and wi.systemId = ? and wi.parentProcessId = ?" ;
		return this.find(hql, companyId,systemId,processInstanceId);
	}

		public List<HistoryWorkflowInstance>  getHistoryInstancesByFormId(Long formId){
		String hql = "from HistoryWorkflowInstance wi where  wi.formId = ?";
		return this.find(hql,formId);
	}

		public HistoryWorkflowInstance getHistoryWorkflowInstance(
				String processInstanceId) {
			return findUnique("from HistoryWorkflowInstance hwfi where hwfi.companyId=? and hwfi.processInstanceId=?", ContextUtils.getCompanyId(), processInstanceId);
		}
	
	public HistoryWorkflowInstance getInstanceByJbpmInstanceId(String jbpmInstanceId, Long companyId){
		return findUnique("from HistoryWorkflowInstance wfi where wfi.companyId=? and wfi.processInstanceId=?", companyId, jbpmInstanceId);
	}

	public List<HistoryWorkflowInstance> getSubProcessHistoryInstanceByTaskName(
			String parentWorkflowId, String tacheName) {
		String hql = "from HistoryWorkflowInstance wi where wi.parentProcessId = ? and wi.parentProcessTacheName=? order by submitTime desc";
		return this.findNoCompanyCondition(hql, parentWorkflowId,tacheName);
	}
	
	public List<HistoryWorkflowInstance> getSubProcessInstance(
			String parentWorkflowId, String tacheName) {
		String hql = "from HistoryWorkflowInstance wi where wi.parentProcessId = ? and wi.parentProcessTacheName=? order by submitTime desc";
		return this.findNoCompanyCondition(hql, parentWorkflowId,tacheName);
	}
	
}
