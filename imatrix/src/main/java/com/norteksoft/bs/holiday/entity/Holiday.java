package com.norteksoft.bs.holiday.entity;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Table;

import com.norteksoft.product.orm.IdEntity;


@Entity
@Table(name = "BS_HOLIDAY")
public class Holiday extends IdEntity {
	private static final long serialVersionUID = 1L;

	private Date specialDate; // 日期
	@Enumerated(EnumType.STRING)
	private DateType dateType; // 日期类别： 工作日，节假日
	@Enumerated(EnumType.STRING)
	private HolidaySettingType holidaySettingType; // 设置类别：常规设置 ，快速设置

	public Date getSpecialDate() {
		return specialDate;
	}

	public void setSpecialDate(Date specialDate) {
		this.specialDate = specialDate;
	}

	public DateType getDateType() {
		return dateType;
	}

	public void setDateType(DateType dateType) {
		this.dateType = dateType;
	}
	
	public HolidaySettingType getHolidaySettingType() {
		return holidaySettingType;
	}

	public void setHolidaySettingType(HolidaySettingType holidaySettingType) {
		this.holidaySettingType = holidaySettingType;
	}

	@Override
	public String toString() {
		return "id:"+this.getId()+"；日期："+this.specialDate+"；日期类别："+this.dateType;
	}
}
