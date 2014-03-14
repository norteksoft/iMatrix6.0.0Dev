package com.norteksoft.bs.holiday.dao;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.springframework.stereotype.Repository;

import com.norteksoft.bs.holiday.entity.Holiday;
import com.norteksoft.bs.holiday.entity.HolidaySettingType;
import com.norteksoft.product.orm.hibernate.HibernateDao;
import com.norteksoft.product.util.ContextUtils;

@Repository
public class HolidayDao extends HibernateDao<Holiday, Long>{
	
	public Long getCompanyId(){
		return ContextUtils.getCompanyId();
	}
	
	public List<Holiday> getHolidaySetting(Date startDate, Date endDate,Long branchId){
		if(branchId!=null){
			return this.find("from Holiday h where h.companyId=? and h.specialDate between ? and ? and h.subCompanyId=?", getCompanyId(), startDate, endDate,branchId);
		}else{
			return this.find("from Holiday h where h.companyId=? and h.specialDate between ? and ? and h.subCompanyId is null", getCompanyId(), startDate, endDate);
		}
	}
	
	public Holiday getHolidayByDate(Date date,Long branchId){
		List<Holiday> list = new ArrayList<Holiday>();
		if(branchId!=null){
			list = this.find("from Holiday h where h.companyId=? and h.specialDate=? and h.subCompanyId=?", getCompanyId(), date,branchId);
		}else{
			list = this.find("from Holiday h where h.companyId=? and h.specialDate=? and h.subCompanyId is null", getCompanyId(), date);
		}
		if(list.size() == 1) {
			return list.get(0);
		}else{
			return null;
		}
	}

	/**
	 * 根据分支机构id获得该分支机构下的所有节假日设置
	 * @param branchId
	 * @return
	 */
	public List<Holiday> getHolidaySetting(Long branchId) {
		return this.find("from Holiday h where h.companyId=? and h.subCompanyId=? ",ContextUtils.getCompanyId(),branchId);
	}
}
