package com.norteksoft.product.api.entity;
import org.apache.commons.lang.StringUtils;

import com.norteksoft.acs.entity.organization.Company;
import com.norteksoft.product.api.ApiFactory;
import com.norteksoft.product.util.ContextUtils;

public class Workgroup {
	private static final long serialVersionUID = 1L;

	private Long id;
	private boolean deleted;
	private Company company;
	private String code;
	private String name;
	private String description;
	private Integer weight; //权重
	private Long subCompanyId;//分支机构id
	private String subCompanyName;
	
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public boolean isDeleted() {
		return deleted;
	}
	public void setDeleted(boolean deleted) {
		this.deleted = deleted;
	}
	public Company getCompany() {
		return company;
	}
	public void setCompany(Company company) {
		this.company = company;
	}
	public String getCode() {
		return code;
	}
	public void setCode(String code) {
		this.code = code;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public Integer getWeight() {
		return weight;
	}
	public void setWeight(Integer weight) {
		this.weight = weight;
	}
	public boolean equals(Workgroup group) {
		return group.getId().equals(this.id);
	}
	public Long getSubCompanyId() {
		return subCompanyId;
	}
	public void setSubCompanyId(Long subCompanyId) {
		this.subCompanyId = subCompanyId;
	}
	public String getSubCompanyName() {
		if(subCompanyId==null){
			return ContextUtils.getCompanyName();
		}else{
			com.norteksoft.product.api.entity.Department dept=ApiFactory.getAcsService().getDepartmentById(subCompanyId);
			return StringUtils.isNotEmpty(dept.getShortTitle())?dept.getShortTitle():dept.getName();
		}
	}
	public void setSubCompanyName(String subCompanyName) {
		this.subCompanyName = subCompanyName;
	}
}