package com.norteksoft.mms.form.dao;


import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Repository;

import com.norteksoft.mms.form.entity.FormAttachment;
import com.norteksoft.product.orm.hibernate.HibernateDao;
import com.norteksoft.product.util.ContextUtils;

@Repository
public class FormAttachmentDao extends HibernateDao<FormAttachment, Long> {

	/**
	 * 
	 * @param recordInfo
	 * @param controlId
	 * @param pluginType 上传控件类型：ATTACH_UPLOAD  IMAGE_UPLOAD
	 * @return
	 */
	public List<FormAttachment> getAttachments(String recordInfo,String controlId,String pluginType){
		if(StringUtils.isNotEmpty(recordInfo)){
			return find("from FormAttachment a where a.companyId=? and a.recordInfo=? and a.controlId=? and a.pluginType=? order by a.id desc", ContextUtils.getCompanyId(),recordInfo,controlId,pluginType);
		}else{
			return new ArrayList<FormAttachment>();
		}
	}
	public FormAttachment getAttachment(Long attachId){
		List<FormAttachment> attachs =  findNoCompanyCondition("from FormAttachment a where a.id=?", attachId);
		if(attachs.size()>0){
			return attachs.get(0);
		}
		return null;
	}
	public List<FormAttachment> getAttachmentsByRecordInfo(String recordInfo){
		return find("from FormAttachment t where t.companyId=? and t.recordInfo=? ", 
				ContextUtils.getCompanyId(), recordInfo);
	}
	public void deleteAttachmentsByTable(String tableName){
		createQuery("delete from FormAttachment t where t.companyId=? and t.recordInfo like  ? ", 
				ContextUtils.getCompanyId(), tableName+",%").executeUpdate();
	}
	public void deleteAttachmentsByRecordInfo(String recordInfo){
		createQuery("delete from FormAttachment t where t.companyId=? and t.recordInfo=? ", 
				ContextUtils.getCompanyId(), recordInfo).executeUpdate();
	}
}
