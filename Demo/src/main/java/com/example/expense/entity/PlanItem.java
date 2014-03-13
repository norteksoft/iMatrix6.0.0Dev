package com.example.expense.entity;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.norteksoft.product.orm.IdEntity;
@Entity
@Table(name="ES_PLAN_ITEM")
public class PlanItem extends IdEntity{
	private static final long serialVersionUID = 1L;
	private String taskName;//任务名称
	private Date beginDate;//开始时间
	private Date endDate;//结束时间
	private String document;     //责任部门
	private String responseMan;//负责人
	private Float completionRate;//完成率
	private Double money;        //计划支出    
	private Integer overdueDaysBeforeAlarm; //逾期预警天数
	
	private String loginName;
	private String department;
	private String role;
	private Long roleId;
	private String workgroup;
	private String parentDepartment;
	private String topDepartment;
	private String superiorLoginName;
	private String superiorDepartment;
	private String superiorRole;
	private Long superiorRoleId;
	private String superiorWorkgroup;
	
	
	@ManyToOne
	@JoinColumn(name="FK_PLAN_ID")
	private Plan plan;
	
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
	public Double getMoney() {
		return money;
	}
	public void setMoney(Double money) {
		this.money = money;
	}
	public Integer getOverdueDaysBeforeAlarm() {
		return overdueDaysBeforeAlarm;
	}
	public void setOverdueDaysBeforeAlarm(Integer overdueDaysBeforeAlarm) {
		this.overdueDaysBeforeAlarm = overdueDaysBeforeAlarm;
	}
	public String getLoginName() {
		return loginName;
	}
	public void setLoginName(String loginName) {
		this.loginName = loginName;
	}
	public String getDepartment() {
		return department;
	}
	public void setDepartment(String department) {
		this.department = department;
	}
	public String getRole() {
		return role;
	}
	public void setRole(String role) {
		this.role = role;
	}
	public String getWorkgroup() {
		return workgroup;
	}
	public void setWorkgroup(String workgroup) {
		this.workgroup = workgroup;
	}
	public String getParentDepartment() {
		return parentDepartment;
	}
	public void setParentDepartment(String parentDepartment) {
		this.parentDepartment = parentDepartment;
	}
	public String getTopDepartment() {
		return topDepartment;
	}
	public void setTopDepartment(String topDepartment) {
		this.topDepartment = topDepartment;
	}
	public String getSuperiorLoginName() {
		return superiorLoginName;
	}
	public void setSuperiorLoginName(String superiorLoginName) {
		this.superiorLoginName = superiorLoginName;
	}
	public String getSuperiorDepartment() {
		return superiorDepartment;
	}
	public void setSuperiorDepartment(String superiorDepartment) {
		this.superiorDepartment = superiorDepartment;
	}
	public String getSuperiorRole() {
		return superiorRole;
	}
	public void setSuperiorRole(String superiorRole) {
		this.superiorRole = superiorRole;
	}
	public String getSuperiorWorkgroup() {
		return superiorWorkgroup;
	}
	public void setSuperiorWorkgroup(String superiorWorkgroup) {
		this.superiorWorkgroup = superiorWorkgroup;
	}
	public Plan getPlan() {
		return plan;
	}
	public void setPlan(Plan plan) {
		this.plan = plan;
	}
	public Long getRoleId() {
		return roleId;
	}
	public void setRoleId(Long roleId) {
		this.roleId = roleId;
	}
	public Long getSuperiorRoleId() {
		return superiorRoleId;
	}
	public void setSuperiorRoleId(Long superiorRoleId) {
		this.superiorRoleId = superiorRoleId;
	}

}
