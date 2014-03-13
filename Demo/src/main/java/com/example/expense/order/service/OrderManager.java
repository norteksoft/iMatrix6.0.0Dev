package com.example.expense.order.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.expense.base.utils.Util;
import com.example.expense.entity.Order;
import com.example.expense.entity.OrderItem;
import com.example.expense.order.dao.OrderDao;
import com.example.expense.order.dao.OrderItemDao;
import com.norteksoft.mms.form.service.PlaceholderContent;
import com.norteksoft.product.api.ApiFactory;
import com.norteksoft.product.orm.Page;
import com.norteksoft.product.util.ContextUtils;
import com.norteksoft.product.util.JsonParser;

@Service
@Transactional
public class OrderManager implements PlaceholderContent{
	@Autowired
	private OrderDao orderDao;
	@Autowired
	private OrderItemDao orderItemDao;

	public Order getOrder(Long id){
		return orderDao.get(id);
	}
	
	public Order getOrderById(Long id){
		return orderDao.getOrderById(id);
	}
	
	public void saveOrder(Order order){
		orderDao.save(order);
		List<OrderItem> items=new ArrayList<OrderItem>();
		List<Object> objects=JsonParser.getFormTableDatas(OrderItem.class);
		for(Object obj:objects){
			OrderItem orderItem=(OrderItem)obj;
			orderItem.setOrder(order);
			orderItem.setCompanyId(ContextUtils.getCompanyId());
			orderItem.setCreatedTime(new Date());
			orderItem.setCreator(ContextUtils.getUserName());
			orderItemDao.save(orderItem);
			items.add(orderItem);
		}
		order.setOrderItems(items);
	}
	
	public void deleteOrder(Long id){
		List<OrderItem> items=orderItemDao.getOrderItemByOrder(id);
		for(OrderItem item:items){
			orderItemDao.delete(item);
		}
		orderDao.delete(id);
	}
	
	
	public void deleteOrder(String ids){
		String[] deleteIds = ids.split(",");
		for (String id : deleteIds) {
			List<OrderItem> items=orderItemDao.getOrderItemByOrder(Long.valueOf(id));
			for(OrderItem item:items){
				orderItemDao.delete(item);
			}
			if(orderDao.get(Long.valueOf(id)).getDisplayIndex()!=null){
				orderDao.decreaseIndex(orderDao.get(Long.valueOf(id)).getDisplayIndex());//删除一条数据前比displayIndex大的要减1
			}
			//删除附件上传控件上传的控件
			ApiFactory.getMmsService().deleteFormAttachments("ES_ORDER,"+id);
			orderDao.delete(Long.valueOf(id));
		}
	}
	
	public void deleteOrder(Order order){
		orderDao.delete(order);
	}
	
	public Page<Order> list(Page<Order>page){
		return orderDao.list(page);
	}
	
	public Page<Order> listAll(Page<Order> portalOrderpage, String type, String rows,String pageNo) throws Exception{
		if(StringUtils.isNotEmpty(rows))portalOrderpage.setPageSize(Integer.parseInt(rows));
		if(StringUtils.isNotEmpty(pageNo))portalOrderpage.setPageNo(Integer.parseInt(pageNo));
		portalOrderpage=orderDao.getPortalOrders(portalOrderpage,type);
		for (Order order : portalOrderpage.getResult()) {
			order.setUrl(Util.readProperties("host.app")+"/order/portal-order-input.htm?id="+order.getId());
		}
		return portalOrderpage;
	}

	public Page<Order> search(Page<Order> page) {
		return orderDao.search(page);
	}

	public Page<OrderItem> getOrderItemById(Page<OrderItem> pageItem, Long id) {
		return orderItemDao.getOrderItemById(pageItem,id);
	}

	public List<Object> getOrderItem() {
		return orderItemDao.getOrderItem();
	}

	public Integer getProductAmount(String productName,Long orderId) {
		List<Integer> amountList =  orderItemDao.getProductAmount(productName,orderId);
		Integer totalAmount = 0;
		for(Integer itemAmount : amountList){
			totalAmount+=itemAmount;
		}
		return totalAmount;
	}

	/**
	 * 在portal里显示订单小窗体
	 * @param page
	 * @param companyId
	 * @param userId
	 * @param type
	 * @param rows
	 * @return
	 * @throws Exception
	 */
	public String getOrderHTML(Page<Order> page, String companyId, String userId, String type, String rows) throws Exception {
		if(StringUtils.isNotEmpty(rows))page.setPageSize(Integer.parseInt(rows));
		page=orderDao.getPortalOrders(page,type);
		StringBuilder buffer=new StringBuilder();
		buffer.append("<table  class=\"leadTable\">");
		buffer.append("<thead><th>订单编号</th><th>顾客</th><th>邮编</th></thead>");
		buffer.append("<tbody>");
		for(Order p:page.getResult()){
			Order order=orderDao.get(p.getId());
			String showOrderAddress=Util.readProperties("host.app")+"/order/input.htm?id="+order.getId()+"&companyId="+companyId+"&userId="+userId+"&_r=1";
			buffer.append("<tr><td><a href=\"#\" title=\""+order.getOrderNumber()+"\" onclick=\"popWindow(this,'"+showOrderAddress+"');\">"+order.getOrderNumber()+"</a></td>");
			buffer.append("<td style=\"width:30%;\">"+order.getCustomer()+"</td>");
			buffer.append("<td style=\"width:15%;\">"+order.getPostCode()+"</td></tr>");
		}
		buffer.append("</tbody>");
		buffer.append("</table>");
		return buffer.toString();
	}

	public void saveOrder(Integer originalIndex, Integer newIndex) {
		orderDao.updateIndex(originalIndex, Integer.MAX_VALUE);
        if (originalIndex < newIndex) {// 从上往下移动 两者之间的displayIndex要自减
        	orderDao.decreaseIndex(originalIndex,newIndex);
        } else {// 从下往上移动 两者之间的displayIndex要自增
        	orderDao.increaseIndex(newIndex,originalIndex);
        }
        orderDao.updateIndex(Integer.MAX_VALUE, newIndex);
	}

	public Page<Order> searchSort(Page<Order> page) {
		return orderDao.searchSort(page);
	}

	public Integer getMaxIndex() {
		return orderDao.getMaxIndex();
	}

	@Override
	public String placeholderContent(String placehoderId, Object entity,boolean isPrint) {
		StringBuilder html = new StringBuilder();
		Order order = (Order)entity;
		if(isPrint){//打印页面时
			html.append("<table class=\"form-table-border-left\">");
			html.append("<tr>");
			html.append("<td>");
			html.append("字段1:");
			html.append("</td>");
			html.append("<td>");
			html.append(order.getString1()==null?"":order.getString1());
			html.append("</td>");
			html.append("<td>");
			html.append("字段2:");
			html.append("</td>");
			html.append("<td>");
			html.append(order.getString2()==null?"":order.getString2());
			html.append("</td>");
			html.append("</tr>");
			html.append("<tr>");
			html.append("<td>");
			html.append("字段3:");
			html.append("</td>");
			html.append("<td>");
			html.append(order.getString3()==null?"":order.getString3());
			html.append("</td>");
			html.append("<td>");
			html.append("字段4:");
			html.append("</td>");
			html.append("<td>");
			html.append(order.getString4()==null?"":order.getString4());
			html.append("</td>");
			html.append("</tr>");
			html.append("</table>");
		}else{//表单页面时
			html.append("<table class=\"form-table-border-left\">");
			html.append("<tr>");
			html.append("<td>");
			html.append("字段1:");
			html.append("</td>");
			html.append("<td>");
			html.append("<input name='string1' value='").append(order.getString1()==null?"":order.getString1()).append("'/>");
			html.append("</td>");
			html.append("<td>");
			html.append("字段2:");
			html.append("</td>");
			html.append("<td>");
			html.append("<input name='string2' value='").append(order.getString2()==null?"":order.getString2()).append("'/>");
			html.append("</td>");
			html.append("</tr>");
			html.append("<tr>");
			html.append("<td>");
			html.append("字段3:");
			html.append("</td>");
			html.append("<td>");
			html.append("<input name='string3' value='").append(order.getString3()==null?"":order.getString3()).append("'/>");
			html.append("</td>");
			html.append("<td>");
			html.append("字段4:");
			html.append("</td>");
			html.append("<td>");
			html.append("<input name='string4' value='").append(order.getString4()==null?"":order.getString4()).append("'/>");
			html.append("</td>");
			html.append("</tr>");
			html.append("</table>");
		}
		return html.toString();
	}
}
