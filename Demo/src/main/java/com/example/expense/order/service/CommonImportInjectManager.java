package com.example.expense.order.service;

import java.util.List;

import org.apache.commons.beanutils.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.expense.base.enumeration.OrderType;
import com.example.expense.entity.Order;
import com.example.expense.entity.OrderItem;
import com.example.expense.order.dao.OrderDao;
import com.example.expense.order.dao.OrderItemDao;
import com.norteksoft.bs.options.entity.ImportColumn;
import com.norteksoft.bs.options.entity.ImportDefinition;
import com.norteksoft.product.api.ApiFactory;
import com.norteksoft.product.api.impl.DefaultDataImporterCallBack;
import com.norteksoft.product.util.ContextUtils;
@Service
@Transactional
public class CommonImportInjectManager extends DefaultDataImporterCallBack{
	public CommonImportInjectManager() {
		
	}
	
	@Autowired
	private OrderDao oderDao;
	@Autowired
	private OrderItemDao orderItemDao;
	
	public String afterValidate(List<String> results) {
		String str="";
		for(String result:results){
			str+=result+"！！！\n";
		}
		return str;
	}
	
	public String saveSingleRow(String[] rowValue,ImportDefinition importDefinition) {
		Order order=new Order();
		OrderItem orderItem=new OrderItem();
		int i=0;
		for(ImportColumn importColumn:importDefinition.getImportColumns()){
			try {
				if(i<5)
					BeanUtils.copyProperty(order,importColumn.getName() , rowValue[i]);
				if(i==5)
					if("商品订单".equals(rowValue[i])){
						order.setType(OrderType.PRODUCT);
					}else if ("旅游订单".equals(rowValue[i])){
						order.setType(OrderType.TRAVELLING);
					}
				if(i>=6)
					BeanUtils.copyProperty(orderItem,importColumn.getName() , rowValue[i]);
			}  catch (Exception e) {
				e.printStackTrace();
			}
			i++;
		}
		oderDao.save(order);
		orderItem.setOrder(order);
		orderItemDao.save(orderItem);
		return "";
	}
	
	public void afterSaveSingleRow(String[] rowValue,ImportDefinition importDefinition) {
		
	}
	
	public void afterSaveAllRows() {
		try {
			ApiFactory.getPortalService().addMessage("ems", ContextUtils.getUserName(), ContextUtils.getLoginName(), ContextUtils.getLoginName(), "导入", "导入成功", "");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
