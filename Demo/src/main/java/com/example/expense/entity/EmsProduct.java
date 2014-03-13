package com.example.expense.entity;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.Table;

import org.apache.commons.lang.StringUtils;

import com.norteksoft.product.orm.IdEntity;


@Entity
@Table(name="ES_PRODUCT")
public class EmsProduct extends IdEntity {
	private static final long serialVersionUID = 1L;
	private String productNumber;//产品编号
	private String productName;  //产品名称
	private Float price;         //产品价格
	private Integer amount;      //采购数量
	private Float sum;           //采购总金额
	private Integer displayIndex;//显示顺序
	private Date buyDate;//采购日期
	private ColorEnum color;//颜色(枚举)
	private String groupName;//选项组
	private String keyValue;//键值对
	private String interfaceVal;//接口
	private String customElement;//自定义控件
	private String buyer;//采购人姓名
	private String buyerLoginName;//采购人登录名
	private String department;//采购部门
	
	private Long userId;//创建人id（数据分类api会用到）
	private Long deptId;//部门id（数据分类api会用到）
	
	private Integer interfaceInt;//整型接口
	public String getProductNumber() {
		return productNumber;
	}
	public void setProductNumber(String productNumber) {
		this.productNumber = productNumber;
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
	public Integer getDisplayIndex() {
		return displayIndex;
	}
	public void setDisplayIndex(Integer displayIndex) {
		this.displayIndex = displayIndex;
	}
	public Date getBuyDate() {
		return buyDate;
	}
	public void setBuyDate(Date buyDate) {
		this.buyDate = buyDate;
	}
	public ColorEnum getColor() {
		return color;
	}
	public void setColor(ColorEnum color) {
		this.color = color;
	}
	public String getGroupName() {
		return groupName;
	}
	public void setGroupName(String groupName) {
		if(StringUtils.isNotEmpty(groupName)){
			this.groupName = groupName;
		}
	}
	public String getKeyValue() {
		return keyValue;
	}
	public void setKeyValue(String keyValue) {
		if(StringUtils.isNotEmpty(keyValue)){
			this.keyValue = keyValue;
		}
	}
	public String getInterfaceVal() {
		return interfaceVal;
	}
	public void setInterfaceVal(String interfaceVal) {
		this.interfaceVal = interfaceVal;
	}
	public String getBuyer() {
		return buyer;
	}
	public void setBuyer(String buyer) {
		this.buyer = buyer;
	}
	public String getBuyerLoginName() {
		return buyerLoginName;
	}
	public void setBuyerLoginName(String buyerLoginName) {
		this.buyerLoginName = buyerLoginName;
	}
	public Float getPrice() {
		return price;
	}
	public void setPrice(Float price) {
		this.price = price;
	}
	public String getCustomElement() {
		return customElement;
	}
	public void setCustomElement(String customElement) {
		this.customElement = customElement;
	}
	public Integer getInterfaceInt() {
		return interfaceInt;
	}
	public void setInterfaceInt(Integer interfaceInt) {
		this.interfaceInt = interfaceInt;
	}
	public Float getSum() {
		return sum;
	}
	public void setSum(Float sum) {
		this.sum = sum;
	}
	public String getDepartment() {
		return department;
	}
	public void setDepartment(String department) {
		this.department = department;
	}
	public Long getUserId() {
		return userId;
	}
	public void setUserId(Long userId) {
		this.userId = userId;
	}
	public Long getDeptId() {
		return deptId;
	}
	public void setDeptId(Long deptId) {
		this.deptId = deptId;
	}
	
}
