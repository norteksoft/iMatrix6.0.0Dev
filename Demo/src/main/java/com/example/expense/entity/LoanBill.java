package com.example.expense.entity;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * 文件夹
 */
@Entity
@Table(name = "ES_LOANBILL")
public class LoanBill extends IdEntity implements Serializable {
	private static final long serialVersionUID = 1L;
	
	private String name; // 文件夹名称
	private Long parentId; // 父文件夹ID
	
	private String parentName; // 父文件夹名称

	private Long creatorId; // 文件夹创建者ID
	private String creatorName; // 文件夹创建者名称
	private Date createDate; // 文件夹创建日期
	
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public Long getParentId() {
		return parentId;
	}
	public void setParentId(Long parentId) {
		this.parentId = parentId;
	}
	public String getParentName() {
		return parentName;
	}
	public void setParentName(String parentName) {
		this.parentName = parentName;
	}
	public Long getCreatorId() {
		return creatorId;
	}
	public void setCreatorId(Long creatorId) {
		this.creatorId = creatorId;
	}
	public String getCreatorName() {
		return creatorName;
	}
	public void setCreatorName(String creatorName) {
		this.creatorName = creatorName;
	}
	public Date getCreateDate() {
		return createDate;
	}
	public void setCreateDate(Date createDate) {
		this.createDate = createDate;
	}
	

}
