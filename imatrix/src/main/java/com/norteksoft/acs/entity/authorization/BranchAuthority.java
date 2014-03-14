package com.norteksoft.acs.entity.authorization;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Table;

import com.norteksoft.acs.base.enumeration.BranchDataType;
import com.norteksoft.acs.entity.IdEntity;

/**
 * 分支机构授权管理
 * 
 */
@Entity
@Table(name = "ACS_BRANCH_AUTHORITY")
public class BranchAuthority extends IdEntity{
	private static final long serialVersionUID = 1L;
	
	private Long branchesId;//分支机构id
	private Long dataId;//数据id:该字段中可能是用户id，也可能是角色id，是什么由字段branchDataType(USER<用户>，ROLE<角色>)来决定
	private BranchDataType branchDataType;//数据类型
	private Long companyId;
	
	public Long getBranchesId() {
		return branchesId;
	}
	public void setBranchesId(Long branchesId) {
		this.branchesId = branchesId;
	}
	public Long getDataId() {
		return dataId;
	}
	public void setDataId(Long dataId) {
		this.dataId = dataId;
	}
	@Enumerated(EnumType.STRING)
	public BranchDataType getBranchDataType() {
		return branchDataType;
	}
	public void setBranchDataType(BranchDataType branchDataType) {
		this.branchDataType = branchDataType;
	}
	public Long getCompanyId() {
		return companyId;
	}
	public void setCompanyId(Long companyId) {
		this.companyId = companyId;
	}
}
