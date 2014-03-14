package com.norteksoft.portal.dao;

import org.springframework.stereotype.Repository;

import com.norteksoft.portal.entity.BaseSetting;
import com.norteksoft.product.orm.hibernate.HibernateDao;
import com.norteksoft.product.util.ContextUtils;

@Repository
public class BaseSettingDao extends HibernateDao<BaseSetting, Long>{

	public BaseSetting getBaseSettingByCreatorId() {
		return findUnique("from BaseSetting bs where bs.creatorId=? and bs.companyId=?",ContextUtils.getUserId(),ContextUtils.getCompanyId());
	}
	
	public BaseSetting getBaseSettingByCreatorId(Long creatorId,Long companyId) {
		return findUnique("from BaseSetting bs where bs.creatorId=? and bs.companyId=?",creatorId,companyId);
	}
}
