package com.norteksoft.wf.engine.dao;


import org.springframework.stereotype.Repository;

import com.norteksoft.product.orm.hibernate.HibernateDao;
import com.norteksoft.wf.engine.entity.WorkflowAttachmentFile;

@Repository
public class WorkflowAttachmentFileDao extends HibernateDao<WorkflowAttachmentFile, Long>{
	
  public WorkflowAttachmentFile getAttachmentFileByAttachmentId(Long attachmentId){
	  return findUnique("from WorkflowAttachmentFile af where af.attachmentId=?", attachmentId);
  }	
  
  public void deleteAttachmentFiles(Long companyId){
	  if(companyId==null){
		  createQuery("delete from WorkflowAttachmentFile t "
				  ).executeUpdate();
	  }else{
		  createQuery("delete from WorkflowAttachmentFile t where t.companyId=? ", 
				  companyId).executeUpdate();
	  }
	}
  
}
