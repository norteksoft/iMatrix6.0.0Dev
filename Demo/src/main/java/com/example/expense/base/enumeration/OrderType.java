package com.example.expense.base.enumeration;

public enum OrderType {
	PRODUCT("order.product"),//商品订单
	TRAVELLING("order.travelling");//旅游订单
	String code;
	private OrderType(String code) {
		this.code=code;
	}
	public String getCode() {
		return code;
	}

}
