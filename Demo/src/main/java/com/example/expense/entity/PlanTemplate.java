package com.example.expense.entity;

import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name="ES_PLAN_TEMPLATE")
public class PlanTemplate extends IdEntity {

	private static final long serialVersionUID = 1L;
	private String name;
	private String department;
	private String layer;
	private String type;
	
	private String listCode;// 自定义列表的code

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDepartment() {
		return department;
	}

	public void setDepartment(String department) {
		this.department = department;
	}

	public String getLayer() {
		return layer;
	}

	public void setLayer(String layer) {
		this.layer = layer;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getListCode() {
		return listCode;
	}

	public void setListCode(String listCode) {
		this.listCode = listCode;
	}
	
	
}
