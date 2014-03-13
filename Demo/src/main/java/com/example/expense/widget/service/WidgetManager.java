package com.example.expense.widget.service;


import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.expense.base.utils.Util;
import com.example.expense.entity.EmsProduct;
import com.example.expense.entity.ExpenseReport;
import com.example.expense.entity.LoanBill;
import com.example.expense.entity.Order;
import com.example.expense.entity.OrderItem;
import com.example.expense.entity.Plan;
import com.example.expense.entity.PlanTask;
import com.example.expense.entity.Report;
import com.example.expense.expensereport.dao.ExpenseReportDao;
import com.example.expense.loan.dao.LoanBillDao;
import com.example.expense.loan.dao.PlanTaskDao;
import com.example.expense.order.dao.OrderDao;
import com.example.expense.order.dao.OrderItemDao;
import com.example.expense.plan.dao.PlanDao;
import com.example.expense.product.dao.EmsProductDao;
import com.example.expense.report.dao.ReportDao;
import com.norteksoft.product.orm.Page;

@Service
@Transactional
public class WidgetManager {
	@Autowired
	private OrderDao orderDao;
	@Autowired
	private ReportDao reportDao;
	@Autowired
	private OrderItemDao orderItemDao;
	@Autowired
	private PlanDao planDao;
	@Autowired
	private ExpenseReportDao expenseReportDao;
	@Autowired
	private PlanTaskDao planTaskDao;
	@Autowired
	private LoanBillDao loanBillDao;
	@Autowired
	private EmsProductDao emsProductDao;

	//test1-order
	public Page<Order> listAll(Page<Order> portalOrderpage, String type, String rows,String pageNo) throws Exception{
		if(StringUtils.isNotEmpty(rows))portalOrderpage.setPageSize(Integer.parseInt(rows));
		if(StringUtils.isNotEmpty(pageNo))portalOrderpage.setPageNo(Integer.parseInt(pageNo));
		portalOrderpage=orderDao.getPortalOrdersWidget(portalOrderpage,type);
		for (Order order : portalOrderpage.getResult()) {
			order.setUrl(Util.readProperties("host.app")+"/order/portal-order-input.htm?id="+order.getId());
		}
		return portalOrderpage;
	}

    //test2-report
	public Object listAll(Page<Report> portalReportpage, String rows) {
		if(StringUtils.isNotEmpty(rows))portalReportpage.setPageSize(Integer.parseInt(rows));
		return reportDao.listWidgets(portalReportpage);
	}
	//test3-order-item
	public Object listOrderItem(Page<OrderItem> portalOrderItemtpage,
			String rows) {
		if(StringUtils.isNotEmpty(rows))portalOrderItemtpage.setPageSize(Integer.parseInt(rows));
		return orderItemDao.listWidgets(portalOrderItemtpage);
	}
	//test4-plan
	public Object listPlan(Page<Plan> portalPlanpage, String rows) {
		if(StringUtils.isNotEmpty(rows))portalPlanpage.setPageSize(Integer.parseInt(rows));
		return planDao.listWidgets(portalPlanpage);
	}
	//test5-expense-report
	public Object listExpenseReport(
			Page<ExpenseReport> portalExpenseReportpage, String rows) {
		if(StringUtils.isNotEmpty(rows))portalExpenseReportpage.setPageSize(Integer.parseInt(rows));
		return expenseReportDao.listWidgets(portalExpenseReportpage);
	}
	//test6/7-plan-task
	public Object listPlanTask(Page<PlanTask> portalPlanTaskpage, String rows) {
		if(StringUtils.isNotEmpty(rows))portalPlanTaskpage.setPageSize(Integer.parseInt(rows));
		return planTaskDao.listPortal(portalPlanTaskpage);
	}
	//test8/9-loan-bill
	public Object listLoanBill(Page<LoanBill> portalLoanBillpage, String rows) {
		if(StringUtils.isNotEmpty(rows))portalLoanBillpage.setPageSize(Integer.parseInt(rows));
		return loanBillDao.listWidgets(portalLoanBillpage);
	}
	//test10-ems-product
	public Object listEmsProduct(Page<EmsProduct> portalEmsProductpage,
			String rows) {
		if(StringUtils.isNotEmpty(rows))portalEmsProductpage.setPageSize(Integer.parseInt(rows));
		return emsProductDao.listWidgets(portalEmsProductpage);
	}

	
}
