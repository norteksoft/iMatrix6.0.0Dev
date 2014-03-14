package com.norteksoft.product.api.entity;


import java.io.Serializable;

import javax.persistence.Transient;

import org.apache.commons.lang.StringUtils;

import com.norteksoft.acs.entity.authorization.BusinessSystem;
import com.norteksoft.acs.entity.authorization.RoleGroup;
import com.norteksoft.product.api.ApiFactory;
import com.norteksoft.product.api.utils.BeanUtil;
import com.norteksoft.product.util.ContextUtils;


public class Role implements Serializable{
	private static final long serialVersionUID = 1L;
	
	private Long id;
	private boolean deleted;
	private String code;
	private String name;
	private BusinessSystem businessSystem;
	private RoleGroup roleGroup;
	private com.norteksoft.acs.entity.authorization.Role parentRole;
	private Long companyId;
	private Integer weight;
    private Long subCompanyId;//所属分支机构id
    
    @Transient
	private String subCompanyName;//所属分支机构名称
    
    public Role(){}
	public Role(String code, String name){
		this.name = name;
		this.code = code;
	}
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
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
	public BusinessSystem getBusinessSystem() {
		return businessSystem;
	}
	public void setBusinessSystem(BusinessSystem businessSystem) {
		this.businessSystem = businessSystem;
	}
	public RoleGroup getRoleGroup() {
		return roleGroup;
	}
	public void setRoleGroup(RoleGroup roleGroup) {
		this.roleGroup = roleGroup;
	}
	public Role getParentRole() {
		return BeanUtil.turnToModelRole(parentRole);
	}
	public void setParentRole(com.norteksoft.acs.entity.authorization.Role parentRole) {
		this.parentRole = parentRole;
	}
	public Long getCompanyId() {
		return companyId;
	}
	public void setCompanyId(Long companyId) {
		this.companyId = companyId;
	}
	public Integer getWeight() {
		return weight;
	}
	public void setWeight(Integer weight) {
		this.weight = weight;
	}
	public boolean isDeleted() {
		return deleted;
	}
	public void setDeleted(boolean deleted) {
		this.deleted = deleted;
	}
	public boolean equals(Role role) {
		return role.getId().equals(this.id);
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
			Department subCompany = ApiFactory.getAcsService().getDepartmentById(subCompanyId);
			if(StringUtils.isNotEmpty(subCompany.getShortTitle()))return subCompany.getShortTitle();
			return subCompany.getName();
		}
	}

	public void setSubCompanyName(String subCompanyName) {
		this.subCompanyName = subCompanyName;
	}
}