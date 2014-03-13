package com.example.expense.order.dao;

import java.util.List;

import org.springframework.stereotype.Repository;

import com.example.expense.base.enumeration.OrderType;
import com.example.expense.entity.Order;
import com.norteksoft.product.orm.Page;
import com.norteksoft.product.orm.hibernate.HibernateDao;
import com.norteksoft.product.util.ContextUtils;

@Repository
public class OrderDao extends HibernateDao<Order, Long> {
		
	public Page<Order> list(Page<Order> page){
		return findPage(page, "from Order o");
	}
	
	public List<Order> getAllOrder(){
		return find("from Order o");
	}
	
	public Page<Order> search(Page<Order> page) {
        return searchPageByHql(page, "select distinct o from Order o inner join o.orderItems orderItems order by o.orderNumber asc");
    }
	
	public Page<Order> getPortalOrders(Page<Order> page, String type) {
		if(type==null){
			return searchPageByHql(page, "select distinct o from Order o inner join o.orderItems orderItems where o.companyId = ? order by o.orderNumber asc",ContextUtils.getCompanyId());
		}else{
			return searchPageByHql(page, "select distinct o from Order o inner join o.orderItems orderItems where o.type=? and o.companyId = ? order by o.orderNumber asc",OrderType.valueOf(type),ContextUtils.getCompanyId());
		}
		
	}
	public Page<Order> getPortalOrdersWidget(Page<Order> page, String type) {
		if(type==null){
			return searchPageByHql(page, "select distinct o from Order o inner join o.orderItems orderItems where o.companyId = ? and o.creator=? order by o.orderNumber asc",ContextUtils.getCompanyId(),ContextUtils.getLoginName());
		}else{
			return searchPageByHql(page, "select distinct o from Order o inner join o.orderItems orderItems where o.type=? and o.companyId = ? and o.creator=? order by o.orderNumber asc",OrderType.valueOf(type),ContextUtils.getCompanyId(),ContextUtils.getLoginName());
		}
		
	}

	public Order getOrderById(Long id) {
		return findUnique("from Order o where o.id=? ",id);
	}

	public void updateIndex(Integer originalIndex, Integer newIndex) {
		createQuery("update Order o set o.displayIndex=? where o.displayIndex=?",
                newIndex,originalIndex).executeUpdate();
	}

	public void decreaseIndex(Integer start, Integer end) {
		createQuery("update Order o set o.displayIndex=o.displayIndex-1 where o.displayIndex>? and o.displayIndex<=?",
                start,end).executeUpdate();
	}

	public void increaseIndex(Integer start, Integer end) {
		createQuery("update Order o set o.displayIndex=o.displayIndex+1 where o.displayIndex>=? and o.displayIndex<?",
                start,end).executeUpdate();
	}

	public Page<Order> searchSort(Page<Order> page) {
		return searchPageByHql(page, "select o from Order o ");
	}

	public Integer getMaxIndex() {
		return findUnique("select Max(o.displayIndex) from Order o  ");
	}
	
	public void decreaseIndex(Integer displayIndex) {
		createQuery("update Order o set o.displayIndex=o.displayIndex-1 where o.displayIndex>? ",
				displayIndex).executeUpdate();
	}
}
