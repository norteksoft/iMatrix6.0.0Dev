package com.example.expense.entity;

import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

@Entity
@Table(name="ES_ORDER_ITEM")
public class OrderItem extends IdEntity{

	private static final long serialVersionUID = 1L;
	private String productName;
	private Integer amount;
	private Float money;//编辑时失去焦点事件
	private String applyName;
	private String applyLoginName;
	private Integer displayIndex;//显示顺序
	private String applyRole;//编辑时点击事件
	private String applyDepartment;//编辑时触发的事件
	private Float price;//单价
	private String country;//编辑时切换事件
	private Date produceDate;//生产日期
	private Integer displayOrder;//显示顺序
	
	@ManyToOne
	@JoinColumn(name="FK_ORDER_ID")
	private Order order;
	
	@OneToMany(cascade=CascadeType.ALL,mappedBy="orderItem")
	List<OrderItemDetail> itemDetails;
	
	public Order getOrder() {
		return order;
	}
	public void setOrder(Order order) {
		this.order = order;
	}
	
	public String getProductName() {
		return productName;
	}
	public void setProductName(String productName) {
		this.productName = productName;
	}
	public Integer getAmount() {
		return amount;
	}
	public void setAmount(Integer amount) {
		this.amount = amount;
	}
	public Float getMoney() {
		return money;
	}
	public void setMoney(Float money) {
		this.money = money;
	}
	public List<OrderItemDetail> getItemDetails() {
		return itemDetails;
	}
	public void setItemDetails(List<OrderItemDetail> itemDetails) {
		this.itemDetails = itemDetails;
	}
	public Integer getDisplayIndex() {
		return displayIndex;
	}
	public void setDisplayIndex(Integer displayIndex) {
		this.displayIndex = displayIndex;
	}
	public String getApplyName() {
		return applyName;
	}
	public void setApplyName(String applyName) {
		this.applyName = applyName;
	}
	public String getApplyLoginName() {
		return applyLoginName;
	}
	public void setApplyLoginName(String applyLoginName) {
		this.applyLoginName = applyLoginName;
	}
	public String getApplyRole() {
		return applyRole;
	}
	public void setApplyRole(String applyRole) {
		this.applyRole = applyRole;
	}
	public String getApplyDepartment() {
		return applyDepartment;
	}
	public void setApplyDepartment(String applyDepartment) {
		this.applyDepartment = applyDepartment;
	}
	public Float getPrice() {
		return price;
	}
	public void setPrice(Float price) {
		this.price = price;
	}
	public String getCountry() {
		return country;
	}
	public void setCountry(String country) {
		this.country = country;
	}
	public Date getProduceDate() {
		return produceDate;
	}
	public void setProduceDate(Date produceDate) {
		this.produceDate = produceDate;
	}
	public Integer getDisplayOrder() {
		return displayOrder;
	}
	public void setDisplayOrder(Integer displayOrder) {
		this.displayOrder = displayOrder;
	}
	
}
