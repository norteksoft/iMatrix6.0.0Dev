package com.example.expense.entity;

import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table(name="ES_ORDER_ITEM_DETAIL")
public class OrderItemDetail extends IdEntity{

	private static final long serialVersionUID = 1L;
	private String productSize;
	private Integer standard;
	
	@ManyToOne
	@JoinColumn(name="FK_ORDER_ITEM_ID")
	private OrderItem orderItem;

	public String getProductSize() {
		return productSize;
	}

	public void setProductSize(String productSize) {
		this.productSize = productSize;
	}

	public Integer getStandard() {
		return standard;
	}

	public void setStandard(Integer standard) {
		this.standard = standard;
	}

	public OrderItem getOrderItem() {
		return orderItem;
	}

	public void setOrderItem(OrderItem orderItem) {
		this.orderItem = orderItem;
	}

	
}
