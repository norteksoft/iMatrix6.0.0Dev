package com.example.expense.entity;

import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;

import com.example.expense.base.enumeration.OrderType;

@Entity
@Table(name="ES_ORDER")
public class Order extends IdEntity {

	private static final long serialVersionUID = 1L;
	private String orderNumber;//订单编号
	private String customer;//顾客
	private String customerLoginName;
	private String customerId;
	private String postCode;//邮编
	private String phone;//电话
	private Float height;//身高
	private Double weight;//体重
	private Boolean overTime;//是否过期
	private Boolean checkboxBoolean;//布尔时复选框
	private String checkboxStr;//文本时复选框
	private String country;//国家，下拉框
	private String area;//地区，子下拉框
	private Date createDate;//创建日期
	private String productNo;//产品编号（数据选择）
	private String productName;//产品名称（数据选择）
	private OrderType type; //订单类型
	private Boolean ifCreateMessage; //是否新建消息
	@Transient
	private String url; //打开portal小窗体的url
	private Integer displayIndex;//显示顺序
	
	private String string1;
	private String string2;
	private String string3;
	private String string4;
	
	@OneToMany(cascade=CascadeType.ALL,mappedBy="order")
	@LazyCollection(LazyCollectionOption.TRUE)
	@OrderBy("displayIndex asc")
	List<OrderItem> orderItems;
	public List<OrderItem> getOrderItems() {
		return orderItems;
	}
	public void setOrderItems(List<OrderItem> orderItems) {
		this.orderItems = orderItems;
	}
	public String getOrderNumber() {
		return orderNumber;
	}
	public void setOrderNumber(String orderNumber) {
		this.orderNumber = orderNumber;
	}
	public String getCustomer() {
		return customer;
	}
	public void setCustomer(String customer) {
		this.customer = customer;
	}
	public String getPostCode() {
		return postCode;
	}
	public void setPostCode(String postCode) {
		this.postCode = postCode;
	}
	public String getPhone() {
		return phone;
	}
	public void setPhone(String phone) {
		this.phone = phone;
	}
	public Float getHeight() {
		return height;
	}
	public void setHeight(Float height) {
		this.height = height;
	}
	public Double getWeight() {
		return weight;
	}
	public void setWeight(Double weight) {
		this.weight = weight;
	}
	public String getCustomerLoginName() {
		return customerLoginName;
	}
	public void setCustomerLoginName(String customerLoginName) {
		this.customerLoginName = customerLoginName;
	}
	public Boolean getOverTime() {
		return overTime;
	}
	public void setOverTime(Boolean overTime) {
		this.overTime = overTime;
	}
	public Boolean getCheckboxBoolean() {
		return checkboxBoolean;
	}
	public void setCheckboxBoolean(Boolean checkboxBoolean) {
		this.checkboxBoolean = checkboxBoolean;
	}
	public String getCheckboxStr() {
		return checkboxStr;
	}
	public void setCheckboxStr(String checkboxStr) {
		this.checkboxStr = checkboxStr;
	}
	public String getCountry() {
		return country;
	}
	public void setCountry(String country) {
		this.country = country;
	}
	public String getArea() {
		return area;
	}
	public void setArea(String area) {
		this.area = area;
	}
	public Date getCreateDate() {
		return createDate;
	}
	public void setCreateDate(Date createDate) {
		this.createDate = createDate;
	}
	public String getProductNo() {
		return productNo;
	}
	public void setProductNo(String productNo) {
		this.productNo = productNo;
	}
	public String getProductName() {
		return productName;
	}
	public void setProductName(String productName) {
		this.productName = productName;
	}
	public OrderType getType() {
		return type;
	}
	public void setType(OrderType type) {
		this.type = type;
	}
	public Boolean getIfCreateMessage() {
		return ifCreateMessage;
	}
	public void setIfCreateMessage(Boolean ifCreateMessage) {
		this.ifCreateMessage = ifCreateMessage;
	}
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	public Integer getDisplayIndex() {
		return displayIndex;
	}
	public void setDisplayIndex(Integer displayIndex) {
		this.displayIndex = displayIndex;
	}
	public String getCustomerId() {
		return customerId;
	}
	public void setCustomerId(String customerId) {
		this.customerId = customerId;
	}
	public String getString1() {
		return string1;
	}
	public void setString1(String string1) {
		this.string1 = string1;
	}
	public String getString2() {
		return string2;
	}
	public void setString2(String string2) {
		this.string2 = string2;
	}
	public String getString3() {
		return string3;
	}
	public void setString3(String string3) {
		this.string3 = string3;
	}
	public String getString4() {
		return string4;
	}
	public void setString4(String string4) {
		this.string4 = string4;
	}

	
}
