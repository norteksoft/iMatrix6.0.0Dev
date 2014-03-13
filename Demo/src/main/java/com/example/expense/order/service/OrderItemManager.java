package com.example.expense.order.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.norteksoft.mms.base.utils.view.ComboxValues;
import com.norteksoft.product.orm.Page;
import com.example.expense.order.dao.OrderItemDao;
import com.example.expense.entity.OrderItem;

@Service
@Transactional
public class OrderItemManager implements ComboxValues{
	@Autowired
	private OrderItemDao orderItemDao;

	public OrderItem getOrderItem(Long id){
		return orderItemDao.get(id);
	}
	
	public void saveOrderItem(OrderItem orderItem){
		orderItemDao.save(orderItem);
	}
	
	public void deleteOrderItem(Long id){
		orderItemDao.delete(id);
	}
	
	public void deleteOrderItem(OrderItem orderItem){
		orderItemDao.delete(orderItem);
	}
	
	public Page<OrderItem> list(Page<OrderItem>page){
		return orderItemDao.list(page);
	}
	
	public List<OrderItem> listAll(){
		return orderItemDao.getAllOrderItem();
	}

	public Page<OrderItem> search(Page<OrderItem> page) {
		return orderItemDao.search(page);
	}

	@Override
	public Map<String, String> getValues(Object entity) {
		StringBuilder result=new StringBuilder();
		Map<String,String> map=new HashMap<String, String>();
		result.append("'China':'中国',")
		.append("'America':'美国',")
		.append("'Japan':'日本',")
		.append("'England':'英国'");
		map.put("country", result.toString());
		return map;
	}

	public Page<Object> getCombinePage(Page<Object> combinePage) {
		return orderItemDao.getCombinePage(combinePage);
	}
}
