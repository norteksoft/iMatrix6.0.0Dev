package com.example.expense.entity;

import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;

import com.example.expense.base.enumeration.PlanState;
import com.norteksoft.product.orm.IdEntity;
@Entity
@Table(name="ES_PLAN")
public class Plan extends IdEntity{
	private static final long serialVersionUID = 1L;
	private String name;//计划名称
	private String code;//计划名称
	private Date beginDate;//开始时间
	private Date endDate;//结束时间
	private Integer amount;//计划数目
	private Double money;//计划金额
	private Boolean finished;//是否完成
	private PlanState planState;//计划状态
	private String remark;//备注
	
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
	
	private Long parentDepartmentId;
	private Long topDepartmentId;
	private Long superiorDepartmentId;
	private Long workgroupId;
	private Long superiorWorkgroupId;
	
	
	@OneToMany(cascade=CascadeType.ALL,mappedBy="plan")
	@LazyCollection(LazyCollectionOption.TRUE)
	List<PlanItem> planItems;
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getCode() {
		return code;
	}
	public void setCode(String code) {
		this.code = code;
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
	public Integer getAmount() {
		return amount;
	}
	public void setAmount(Integer amount) {
		this.amount = amount;
	}
	public Double getMoney() {
		return money;
	}
	public void setMoney(Double money) {
		this.money = money;
	}
	public Boolean getFinished() {
		return finished;
	}
	public void setFinished(Boolean finished) {
		this.finished = finished;
	}
	public PlanState getPlanState() {
		return planState;
	}
	public void setPlanState(PlanState planState) {
		this.planState = planState;
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
	public List<PlanItem> getPlanItems() {
		return planItems;
	}
	public void setPlanItems(List<PlanItem> planItems) {
		this.planItems = planItems;
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
	public Long getParentDepartmentId() {
		return parentDepartmentId;
	}
	public void setParentDepartmentId(Long parentDepartmentId) {
		this.parentDepartmentId = parentDepartmentId;
	}
	public Long getTopDepartmentId() {
		return topDepartmentId;
	}
	public void setTopDepartmentId(Long topDepartmentId) {
		this.topDepartmentId = topDepartmentId;
	}
	public Long getSuperiorDepartmentId() {
		return superiorDepartmentId;
	}
	public void setSuperiorDepartmentId(Long superiorDepartmentId) {
		this.superiorDepartmentId = superiorDepartmentId;
	}
	public Long getWorkgroupId() {
		return workgroupId;
	}
	public void setWorkgroupId(Long workgroupId) {
		this.workgroupId = workgroupId;
	}
	public Long getSuperiorWorkgroupId() {
		return superiorWorkgroupId;
	}
	public void setSuperiorWorkgroupId(Long superiorWorkgroupId) {
		this.superiorWorkgroupId = superiorWorkgroupId;
	}
	
}
