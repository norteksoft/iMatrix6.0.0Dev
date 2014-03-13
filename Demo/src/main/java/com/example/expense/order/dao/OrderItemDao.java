package com.example.expense.order.dao;

import java.util.List;

import org.springframework.stereotype.Repository;

import com.example.expense.entity.OrderItem;
import com.norteksoft.product.api.ApiFactory;
import com.norteksoft.product.orm.Page;
import com.norteksoft.product.orm.hibernate.HibernateDao;
import com.norteksoft.product.util.ContextUtils;

@Repository
public class OrderItemDao extends HibernateDao<OrderItem, Long> {
		
	public Page<OrderItem> list(Page<OrderItem> page){
		return findPage(page, "from OrderItem orderItem");
	}
	
	public Page<OrderItem> listWidgets(Page<OrderItem> page){
		return findPage(page, "from OrderItem orderItem where orderItem.creator=? ",ContextUtils.getLoginName());
	}
	
	public List<OrderItem> getAllOrderItem(){
		return find("from OrderItem orderItem");
	}
	
	public Page<OrderItem> search(Page<OrderItem> page) {
        return searchPageByHql(page, "from OrderItem o order by productName asc");
    }
	
	public List<OrderItem> getOrderItemByOrder(Long orderId){
		return find("from OrderItem orderItem where orderItem.order.id=?",orderId);
	}

	public Page<OrderItem> getOrderItemById(Page<OrderItem> pageItem, Long id) {
		return this.searchPageSubByHql(pageItem, "from OrderItem orderItems where orderItems.order.id=? ",id);
	}

	public List<Object> getOrderItem() {
		return find("select distinct o.productName from OrderItem o order by o.productName ");
	}

	public List<Integer> getProductAmount(String productName, Long orderId) {
		return find("select o.amount from OrderItem o where o.productName=? and o.order.id=? ",productName,orderId);
	}

	public Page<Object> getCombinePage(Page<Object> combinePage) {
		String partSql = ApiFactory.getMmsService().getColumnsByCode("ES_ITEM_COMBINE_ORDER");
		StringBuilder sql = new StringBuilder("select ");
		sql.append(partSql);
		sql.append(" from ES_ORDER_ITEM orderItem ");
		sql.append("left join ES_ORDER o on orderItem.FK_ORDER_ID = o.id ");
		sql.append("where orderItem.company_id=? ");
		return this.searchPageBySql(combinePage, sql.toString(),ContextUtils.getCompanyId());
	}
}
