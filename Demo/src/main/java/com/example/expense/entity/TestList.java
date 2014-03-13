package com.example.expense.entity;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

@Entity
@Table(name="ES_TEST_LIST")
public class TestList extends IdEntity{
	private static final long serialVersionUID = 1L;
	private String taskName;//任务名称
	private Date beginDate;//开始时间
	private Date endDate;//结束时间
	
	private Integer layer;        //任务级别    
	private String code;        //任务编号    
	private String document;     //责任部门
	private String responseMan;//负责人
	private Float completionRate;//完成率
	private String status;       //任务状态
	private Integer displayOrder;//显示顺序
	// 计划
	private Double money;        //计划支出    
	private Date remindingDate;   //任务提醒日期
	private Integer overdueDaysBeforeAlarm; //逾期预警天数

	// 管理
	@Temporal(TemporalType.DATE)
	private Date terminatedDate;  //终止日期
	private String terminatedPerson;//终止人
	
	private String string1;
	private String string2;
	private String string3;
	private String string4;
	private String string5;
	
	private String string6;
	private String string7;
	private String string8;
	private String string9;
	private String string10;
	private String string11;
	private String string12;
	private String string13;
	private String string14;
	private String string15;
	
	private Double double1;
	private Double double2;
	private Double double3;
	private Double double4;
	private Double double5;
	
	private Boolean boolean1;
	private Boolean boolean2;
	private Boolean boolean3;
	private Boolean boolean4;
	private Boolean boolean5;
	
	private Date date1;
	private Date date2;
	private Date date3;
	private Date date4;
	private Date date5;
	
	private Long long1;
	private Long long2;
	private Long long3;
	private Long long4;
	private Long long5;
	private Long long6;
	private Long long7;
	private Long long8;
	private Long long9;
	private Long long10;
	private String remark;//备注
	public TestList(String taskName,Date beginDate,Date endDate,Integer layer,String code,String document,
			String responseMan,Float completionRate,String status,
			Double money,Date remindingDate,Integer overdueDaysBeforeAlarm,Date terminatedDate,String terminatedPerson,
			String string1,String string2,String string3,String string4,String string5,
			String string6,String string7,String string8,String string9,String string10,
			String string11,String string12,String string13,String string14,String string15,
			Double double1,Double double2,Double double3,Double double4,Double double5,
			Boolean boolean1,Boolean boolean2,Boolean boolean3,Boolean boolean4,Boolean boolean5,
			Date date1,Date date2,Date date3,Date date4,Date date5,
			Long long1,Long long2,Long long3,Long long4,Long long5,
			Long long6,Long long7,Long long8,Long long9,Long long10){
		this.taskName=taskName;
		this.beginDate=beginDate;
		this.endDate=endDate;
		this.layer=layer;
		this.code=code;
		this.document=document;
		this.responseMan=responseMan;
		this.completionRate=completionRate;
		this.status=status;
		this.money=money;
		this.remindingDate=remindingDate;
		this.overdueDaysBeforeAlarm=overdueDaysBeforeAlarm;
		this.terminatedDate=terminatedDate;
		this.terminatedPerson=terminatedPerson;
		this.string1=string1;
		this.string2=string2;
		this.string3=string3;
		this.string4=string4;
		this.string5=string5;
		this.string6=string6;
		this.string7=string7;
		this.string8=string8;
		this.string9=string9;
		this.string10=string10;
		this.string11=string11;
		this.string12=string12;
		this.string13=string13;
		this.string14=string14;
		this.string15=string15;
		
		this.double1=double1;
		this.double2=double2;
		this.double3=double3;
		this.double4=double4;
		this.double5=double5;
		this.boolean1=boolean1;
		this.boolean2=boolean2;
		this.boolean3=boolean3;
		this.boolean4=boolean4;
		this.boolean5=boolean5;
		this.date1=date1;
		this.date2=date2;
		this.date3=date3;
		this.date4=date4;
		this.date5=date5;
		this.long1=long1;
		this.long2=long2;
		this.long3=long3;
		this.long4=long4;
		this.long5=long5;
		this.long6=long6;
		this.long7=long7;
		this.long8=long8;
		this.long9=long9;
		this.long10=long10;
	}
	public TestList(){}
	
	public String getTaskName() {
		return taskName;
	}
	public void setTaskName(String taskName) {
		this.taskName = taskName;
	}
	public Date getBeginDate() {
		return beginDate;
	}
	public void setBeginDate(Date beginDate) {
		this.beginDate = beginDate;
	}
	public Date getEndDate() {
		return endDate;
	}
	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}
	public String getRemark() {
		return remark;
	}
	public void setRemark(String remark) {
		this.remark = remark;
	}
	public Integer getLayer() {
		return layer;
	}
	public void setLayer(Integer layer) {
		this.layer = layer;
	}
	public String getCode() {
		return code;
	}
	public void setCode(String code) {
		this.code = code;
	}
	public String getDocument() {
		return document;
	}
	public void setDocument(String document) {
		this.document = document;
	}
	public String getResponseMan() {
		return responseMan;
	}
	public void setResponseMan(String responseMan) {
		this.responseMan = responseMan;
	}
	public Float getCompletionRate() {
		return completionRate;
	}
	public void setCompletionRate(Float completionRate) {
		this.completionRate = completionRate;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public Integer getDisplayOrder() {
		return displayOrder;
	}
	public void setDisplayOrder(Integer displayOrder) {
		this.displayOrder = displayOrder;
	}
	public Double getMoney() {
		return money;
	}
	public void setMoney(Double money) {
		this.money = money;
	}
	public Date getRemindingDate() {
		return remindingDate;
	}
	public void setRemindingDate(Date remindingDate) {
		this.remindingDate = remindingDate;
	}
	public Integer getOverdueDaysBeforeAlarm() {
		return overdueDaysBeforeAlarm;
	}
	public void setOverdueDaysBeforeAlarm(Integer overdueDaysBeforeAlarm) {
		this.overdueDaysBeforeAlarm = overdueDaysBeforeAlarm;
	}
	public Date getTerminatedDate() {
		return terminatedDate;
	}
	public void setTerminatedDate(Date terminatedDate) {
		this.terminatedDate = terminatedDate;
	}
	public String getTerminatedPerson() {
		return terminatedPerson;
	}
	public void setTerminatedPerson(String terminatedPerson) {
		this.terminatedPerson = terminatedPerson;
	}

	public Double getDouble1() {
		return double1;
	}

	public void setDouble1(Double double1) {
		this.double1 = double1;
	}

	public Double getDouble2() {
		return double2;
	}

	public void setDouble2(Double double2) {
		this.double2 = double2;
	}

	public Double getDouble3() {
		return double3;
	}

	public void setDouble3(Double double3) {
		this.double3 = double3;
	}

	public Double getDouble4() {
		return double4;
	}

	public void setDouble4(Double double4) {
		this.double4 = double4;
	}

	public Double getDouble5() {
		return double5;
	}

	public void setDouble5(Double double5) {
		this.double5 = double5;
	}

	public Boolean getBoolean1() {
		return boolean1;
	}

	public void setBoolean1(Boolean boolean1) {
		this.boolean1 = boolean1;
	}

	public Boolean getBoolean2() {
		return boolean2;
	}

	public void setBoolean2(Boolean boolean2) {
		this.boolean2 = boolean2;
	}

	public Boolean getBoolean3() {
		return boolean3;
	}

	public void setBoolean3(Boolean boolean3) {
		this.boolean3 = boolean3;
	}

	public Boolean getBoolean4() {
		return boolean4;
	}

	public void setBoolean4(Boolean boolean4) {
		this.boolean4 = boolean4;
	}

	public Boolean getBoolean5() {
		return boolean5;
	}

	public void setBoolean5(Boolean boolean5) {
		this.boolean5 = boolean5;
	}

	public Date getDate1() {
		return date1;
	}

	public void setDate1(Date date1) {
		this.date1 = date1;
	}

	public Date getDate2() {
		return date2;
	}

	public void setDate2(Date date2) {
		this.date2 = date2;
	}

	public Date getDate3() {
		return date3;
	}

	public void setDate3(Date date3) {
		this.date3 = date3;
	}

	public Date getDate4() {
		return date4;
	}

	public void setDate4(Date date4) {
		this.date4 = date4;
	}

	public Date getDate5() {
		return date5;
	}

	public void setDate5(Date date5) {
		this.date5 = date5;
	}

	public Long getLong1() {
		return long1;
	}

	public void setLong1(Long long1) {
		this.long1 = long1;
	}

	public Long getLong2() {
		return long2;
	}

	public void setLong2(Long long2) {
		this.long2 = long2;
	}

	public Long getLong3() {
		return long3;
	}

	public void setLong3(Long long3) {
		this.long3 = long3;
	}

	public Long getLong4() {
		return long4;
	}

	public void setLong4(Long long4) {
		this.long4 = long4;
	}

	public Long getLong5() {
		return long5;
	}

	public void setLong5(Long long5) {
		this.long5 = long5;
	}

	public String getString1() {
		return string1;
	}

	public void setString1(String string1) {
		this.string1 = string1;
	}

	public String getString2() {
		return string2;
	}

	public void setString2(String string2) {
		this.string2 = string2;
	}

	public String getString3() {
		return string3;
	}

	public void setString3(String string3) {
		this.string3 = string3;
	}

	public String getString4() {
		return string4;
	}

	public void setString4(String string4) {
		this.string4 = string4;
	}

	public String getString5() {
		return string5;
	}

	public void setString5(String string5) {
		this.string5 = string5;
	}

	public Long getLong6() {
		return long6;
	}

	public void setLong6(Long long6) {
		this.long6 = long6;
	}

	public Long getLong7() {
		return long7;
	}

	public void setLong7(Long long7) {
		this.long7 = long7;
	}

	public Long getLong8() {
		return long8;
	}

	public void setLong8(Long long8) {
		this.long8 = long8;
	}

	public Long getLong9() {
		return long9;
	}

	public void setLong9(Long long9) {
		this.long9 = long9;
	}

	public Long getLong10() {
		return long10;
	}

	public void setLong10(Long long10) {
		this.long10 = long10;
	}
	public String getString6() {
		return string6;
	}
	public void setString6(String string6) {
		this.string6 = string6;
	}
	public String getString7() {
		return string7;
	}
	public void setString7(String string7) {
		this.string7 = string7;
	}
	public String getString8() {
		return string8;
	}
	public void setString8(String string8) {
		this.string8 = string8;
	}
	public String getString9() {
		return string9;
	}
	public void setString9(String string9) {
		this.string9 = string9;
	}
	public String getString10() {
		return string10;
	}
	public void setString10(String string10) {
		this.string10 = string10;
	}
	public String getString11() {
		return string11;
	}
	public void setString11(String string11) {
		this.string11 = string11;
	}
	public String getString12() {
		return string12;
	}
	public void setString12(String string12) {
		this.string12 = string12;
	}
	public String getString13() {
		return string13;
	}
	public void setString13(String string13) {
		this.string13 = string13;
	}
	public String getString14() {
		return string14;
	}
	public void setString14(String string14) {
		this.string14 = string14;
	}
	public String getString15() {
		return string15;
	}
	public void setString15(String string15) {
		this.string15 = string15;
	}
	
}
