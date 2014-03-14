package com.norteksoft.wf.engine.dao;

import java.util.List;

import org.hibernate.SQLQuery;
import org.springframework.stereotype.Repository;

import com.norteksoft.product.orm.hibernate.HibernateDao;
import com.norteksoft.wf.engine.entity.HistoryInstanceHistory;
import com.norteksoft.wf.engine.entity.HistoryOpinion;
import com.norteksoft.wf.engine.entity.InstanceHistory;

@Repository
public class HistoryInstanceHistoryDao extends HibernateDao<HistoryInstanceHistory, Long>{
	/**
	 * 流程自动环节和人工环节的流转历史
	 * @param companyId
	 * @param instanceId
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public List<Object[]> getHistoryBySpecial(Long companyId, String instanceId, boolean isSpecial){
		
		StringBuilder hql = new StringBuilder();
		hql.append("select {ih.*}, {o.*} ");
		hql.append("from WF_HISTORY_INSTANCE_HISTORY ih left join WF_HISTORY_OPINION o on ih.task_id=o.task_id ");
		hql.append("where ih.company_id=? and ih.instance_id=? and (ih.type = ? or (ih.type = ? and o.id is null)) and ih.special_task=? order by ih.id,ih.task_id");
		
		SQLQuery query = this.getSession().createSQLQuery(hql.toString());
		query.setParameter(0, companyId);
		query.setParameter(1, instanceId);
		query.setParameter(2, InstanceHistory.TYPE_TASK);
		query.setParameter(3, InstanceHistory.TYPE_AUTO);
		query.setParameter(4, isSpecial);
		
		return query.addEntity("ih", HistoryInstanceHistory.class).addEntity("o", HistoryOpinion.class).list();
		
	}
	
	/**
	 * 文本流转历史
	 * @param companyId
	 * @param workflowId
	 * @return InstanceHistory
	 */	
	@SuppressWarnings("unchecked")
	public List<Object[]> getHistoryByWorkflowId(Long companyId, String workflowId){
		StringBuilder hql = new StringBuilder();
		hql.append("select {ih.*}, {o.*} ");
		hql.append("from WF_HISTORY_INSTANCE_HISTORY ih left join WF_HISTORY_OPINION o on ih.task_id=o.task_id ");
		hql.append("where ih.company_id=? and ih.instance_id=? and (ih.type = ? or (ih.type = ? or ih.type = ? and o.id is null)) order by ih.id,ih.task_id");
		SQLQuery query = this.getSession().createSQLQuery(hql.toString());
		query.setParameter(0, companyId);
		query.setParameter(1, workflowId);
		query.setParameter(2, InstanceHistory.TYPE_TASK);
		query.setParameter(3, InstanceHistory.TYPE_FLOW_START);
		query.setParameter(4, InstanceHistory.TYPE_FLOW_END);
		return query.addEntity("ih", HistoryInstanceHistory.class).addEntity("o", HistoryOpinion.class).list();
	}
	/**
	 * 删除流程实例的所有流转历史
	 * @param companyId
	 * @param workflowId
	 */
	public void deleteHistoryInstanceHistoryByworkflowId(String workflowId,Long companyId){
		createQuery("delete HistoryInstanceHistory ih where ih.companyId=? and ih.instanceId=? ", 
				companyId, workflowId).executeUpdate();
	}
}
