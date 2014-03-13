package com.example.expense.order.web;

import java.util.Date;

import org.apache.struts2.convention.annotation.Action;
import org.apache.struts2.convention.annotation.Namespace;
import org.apache.struts2.convention.annotation.ParentPackage;
import org.springframework.beans.factory.annotation.Autowired;

import com.norteksoft.product.orm.Page;
import com.norteksoft.product.util.ContextUtils;
import com.norteksoft.product.util.PageUtils;
import com.norteksoft.product.web.struts2.CrudActionSupport;
import com.example.expense.order.service.OrderItemManager;
import com.example.expense.entity.OrderItem;


@Namespace("/order-item")
@ParentPackage("default")
public class OrderItemAction extends CrudActionSupport<OrderItem> {

	private static final long serialVersionUID = 1L;
	private Long id;
	private OrderItem orderItem;
	@Autowired
	private OrderItemManager orderItemManager;
	private Page<OrderItem> page;
	private Page<Object> combinePage;
	
	public void setId(Long id) {
		this.id = id;
	}
	
	public Long getId() {
		return id;
	}
	
	public void setPage(Page<OrderItem> page) {
		this.page = page;
	}
	
	public Page<OrderItem> getPage() {
		return page;
	}
	
	public OrderItem getModel() {
		return orderItem;
	}
	
	@Override
	protected void prepareModel() throws Exception {
		if(id==null){
			orderItem=new OrderItem();
			orderItem.setCompanyId(ContextUtils.getCompanyId());
			orderItem.setCreatedTime(new Date());
			orderItem.setCreator(ContextUtils.getUserName());
		}else {
			orderItem=orderItemManager.getOrderItem(id);
		}
	}
	
	@Action("input")
	@Override
	public String input() throws Exception {
		return SUCCESS;
	}
	
	@Action("save")
	@Override
	public String save() throws Exception {
	
		return "input";
	}
	
	@Action("delete")
	@Override
	public String delete() throws Exception {
		orderItemManager.deleteOrderItem(id);
		return "list";
	}

	@Action("list")
	@Override
	public String list() throws Exception {
		return SUCCESS;
	}
	
	@Action("order-combine-list")
	public String combineList(){
		return SUCCESS;
	}
	@Action("order-combine-data")
	public String combineData(){
		combinePage = orderItemManager.getCombinePage(combinePage);
		renderText(PageUtils.pageToJson(combinePage).replace("orderItem.id", "id"));
		return null;
	}
	
	@Action("list-datas")
	public String getListDatas() throws Exception {
		page = orderItemManager.search(page);
		renderText(PageUtils.pageToJson(page));
		return null;
	}

	public Page<Object> getCombinePage() {
		return combinePage;
	}

	public void setCombinePage(Page<Object> combinePage) {
		this.combinePage = combinePage;
	}
	
	
}
