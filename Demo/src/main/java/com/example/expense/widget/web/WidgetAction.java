package com.example.expense.widget.web;

import java.util.HashMap;

import org.apache.struts2.convention.annotation.Action;
import org.apache.struts2.convention.annotation.Namespace;
import org.apache.struts2.convention.annotation.ParentPackage;
import org.springframework.beans.factory.annotation.Autowired;

import com.example.expense.entity.EmsProduct;
import com.example.expense.entity.ExpenseReport;
import com.example.expense.entity.LoanBill;
import com.example.expense.entity.Order;
import com.example.expense.entity.OrderItem;
import com.example.expense.entity.Plan;
import com.example.expense.entity.PlanTask;
import com.example.expense.entity.Report;
import com.example.expense.widget.service.WidgetManager;
import com.norteksoft.acs.base.web.struts2.Struts2Utils;
import com.norteksoft.product.orm.Page;
import com.norteksoft.product.util.ParameterUtils;
import com.norteksoft.product.util.ThreadParameters;
import com.norteksoft.product.util.freemarker.TagUtil;
import com.norteksoft.product.web.struts2.CrudActionSupport;


@Namespace("/widget")
@ParentPackage("default")
public class WidgetAction extends CrudActionSupport<Order> {

	private static final long serialVersionUID = 1L;
	private Page<Order> portalOrderpage = new Page<Order>(Page.EACH_PAGE_FIVE, true);
	private Page<Report> portalReportpage = new Page<Report>(Page.EACH_PAGE_FIVE, true);
	private Page<OrderItem> portalOrderItemtpage = new Page<OrderItem>(Page.EACH_PAGE_FIVE, true);
	private Page<Plan> portalPlanpage = new Page<Plan>(Page.EACH_PAGE_FIVE, true);
	private Page<ExpenseReport> portalExpenseReportpage = new Page<ExpenseReport>(Page.EACH_PAGE_FIVE, true);
	private Page<PlanTask> portalPlanTaskpage = new Page<PlanTask>(Page.EACH_PAGE_FIVE, true);
	private Page<LoanBill> portalLoanBillpage = new Page<LoanBill>(Page.EACH_PAGE_FIVE, true);
	private Page<EmsProduct> portalEmsProductpage = new Page<EmsProduct>(Page.EACH_PAGE_FIVE, true);
	
	@Autowired
	private WidgetManager widgetManager;


	@Override
	public String delete() throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String input() throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String list() throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected void prepareModel() throws Exception {
		// TODO Auto-generated method stub
		
	}

	@Override
	public String save() throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Order getModel() {
		// TODO Auto-generated method stub
		return null;
	}
	
	//test1
	@Action("widget-test1")
	public String showOrders()throws Exception{
		String companyId=Struts2Utils.getRequest().getParameter("companyId");
		String userId=Struts2Utils.getRequest().getParameter("userId");
		String type=Struts2Utils.getRequest().getParameter("type");
		String rows= Struts2Utils.getRequest().getParameter("rows");
		String pageNo= Struts2Utils.getRequest().getParameter("pageNo");
		if(!companyId.equals("") && !userId.equals("")){
			ThreadParameters parameters= new ThreadParameters();
			parameters.setCompanyId(Long.parseLong(companyId));
			parameters.setUserId(Long.parseLong(userId));
			ParameterUtils.setParameters(parameters);
			HashMap<String, Object> dataModel=new HashMap<String, Object>();
			dataModel.put("page", widgetManager.listAll(portalOrderpage,type,rows,pageNo));
			renderText(TagUtil.getContent(dataModel, "order.ftl")+"totalNo"+portalOrderpage.getTotalPages());
		}
		
		return null;
	}
	
	//test2
	@Action("widget-test2")
	public String showReport()throws Exception{
		String companyId=Struts2Utils.getRequest().getParameter("companyId");
		String userId=Struts2Utils.getRequest().getParameter("userId");
		String rows= Struts2Utils.getRequest().getParameter("rows");
		if(!companyId.equals("") && !userId.equals("")){
			ThreadParameters parameters= new ThreadParameters();
			parameters.setCompanyId(Long.parseLong(companyId));
			parameters.setUserId(Long.parseLong(userId));
			ParameterUtils.setParameters(parameters);
			HashMap<String, Object> dataModel=new HashMap<String, Object>();
			dataModel.put("page", widgetManager.listAll(portalReportpage,rows));
			renderText(TagUtil.getContent(dataModel, "report.ftl"));
		}
		
		return null;
	}
	//test3
	@Action("widget-test3")
	public String showOrderItem()throws Exception{
		String companyId=Struts2Utils.getRequest().getParameter("companyId");
		String userId=Struts2Utils.getRequest().getParameter("userId");
		String rows= Struts2Utils.getRequest().getParameter("rows");
		if(!companyId.equals("") && !userId.equals("")){
			ThreadParameters parameters= new ThreadParameters();
			parameters.setCompanyId(Long.parseLong(companyId));
			parameters.setUserId(Long.parseLong(userId));
			ParameterUtils.setParameters(parameters);
			HashMap<String, Object> dataModel=new HashMap<String, Object>();
			dataModel.put("page", widgetManager.listOrderItem(portalOrderItemtpage,rows));
			renderText(TagUtil.getContent(dataModel, "order-item.ftl"));
		}
		
		return null;
	}
	//test4
	@Action("widget-test4")
	public String showPlan()throws Exception{
		String companyId=Struts2Utils.getRequest().getParameter("companyId");
		String userId=Struts2Utils.getRequest().getParameter("userId");
		String rows= Struts2Utils.getRequest().getParameter("rows");
		if(!companyId.equals("") && !userId.equals("")){
			ThreadParameters parameters= new ThreadParameters();
			parameters.setCompanyId(Long.parseLong(companyId));
			parameters.setUserId(Long.parseLong(userId));
			ParameterUtils.setParameters(parameters);
			HashMap<String, Object> dataModel=new HashMap<String, Object>();
			dataModel.put("page", widgetManager.listPlan(portalPlanpage,rows));
			renderText(TagUtil.getContent(dataModel, "plan.ftl"));
		}
		
		return null;
	}
	//test5
	@Action("widget-test5")
	public String showExpenseReport()throws Exception{
		String companyId=Struts2Utils.getRequest().getParameter("companyId");
		String userId=Struts2Utils.getRequest().getParameter("userId");
		String rows= Struts2Utils.getRequest().getParameter("rows");
		if(!companyId.equals("") && !userId.equals("")){
			ThreadParameters parameters= new ThreadParameters();
			parameters.setCompanyId(Long.parseLong(companyId));
			parameters.setUserId(Long.parseLong(userId));
			ParameterUtils.setParameters(parameters);
			HashMap<String, Object> dataModel=new HashMap<String, Object>();
			dataModel.put("page", widgetManager.listExpenseReport(portalExpenseReportpage,rows));
			renderText(TagUtil.getContent(dataModel, "expense-report.ftl"));
		}
		
		return null;
	}
	//test6
	@Action("widget-test6")
	public String showPlanTask()throws Exception{
		String companyId=Struts2Utils.getRequest().getParameter("companyId");
		String userId=Struts2Utils.getRequest().getParameter("userId");
		String rows= Struts2Utils.getRequest().getParameter("rows");
		if(!companyId.equals("") && !userId.equals("")){
			ThreadParameters parameters= new ThreadParameters();
			parameters.setCompanyId(Long.parseLong(companyId));
			parameters.setUserId(Long.parseLong(userId));
			ParameterUtils.setParameters(parameters);
			HashMap<String, Object> dataModel=new HashMap<String, Object>();
			dataModel.put("page", widgetManager.listPlanTask(portalPlanTaskpage,rows));
			renderText(TagUtil.getContent(dataModel, "plan-task.ftl"));
		}
		
		return null;
	}
	
	//test7
	@Action("widget-test7")
	public String showPlanTaskt()throws Exception{
		String companyId=Struts2Utils.getRequest().getParameter("companyId");
		String userId=Struts2Utils.getRequest().getParameter("userId");
		String rows= Struts2Utils.getRequest().getParameter("rows");
		if(!companyId.equals("") && !userId.equals("")){
			ThreadParameters parameters= new ThreadParameters();
			parameters.setCompanyId(Long.parseLong(companyId));
			parameters.setUserId(Long.parseLong(userId));
			ParameterUtils.setParameters(parameters);
			HashMap<String, Object> dataModel=new HashMap<String, Object>();
			dataModel.put("page", widgetManager.listPlanTask(portalPlanTaskpage,rows));
			renderText(TagUtil.getContent(dataModel, "plan-task-two.ftl"));
		}
		
		return null;
	}
	//test8
	@Action("widget-test8")
	public String showLoanBill()throws Exception{
		String companyId=Struts2Utils.getRequest().getParameter("companyId");
		String userId=Struts2Utils.getRequest().getParameter("userId");
		String rows= Struts2Utils.getRequest().getParameter("rows");
		if(!companyId.equals("") && !userId.equals("")){
			ThreadParameters parameters= new ThreadParameters();
			parameters.setCompanyId(Long.parseLong(companyId));
			parameters.setUserId(Long.parseLong(userId));
			ParameterUtils.setParameters(parameters);
			HashMap<String, Object> dataModel=new HashMap<String, Object>();
			dataModel.put("page", widgetManager.listLoanBill(portalLoanBillpage,rows));
			renderText(TagUtil.getContent(dataModel, "loan-bill.ftl"));
		}
		
		return null;
	}
	//test9
	@Action("widget-test9")
	public String showLoanBillt()throws Exception{
		String companyId=Struts2Utils.getRequest().getParameter("companyId");
		String userId=Struts2Utils.getRequest().getParameter("userId");
		String rows= Struts2Utils.getRequest().getParameter("rows");
		if(!companyId.equals("") && !userId.equals("")){
			ThreadParameters parameters= new ThreadParameters();
			parameters.setCompanyId(Long.parseLong(companyId));
			parameters.setUserId(Long.parseLong(userId));
			ParameterUtils.setParameters(parameters);
			HashMap<String, Object> dataModel=new HashMap<String, Object>();
			dataModel.put("page", widgetManager.listLoanBill(portalLoanBillpage,rows));
			renderText(TagUtil.getContent(dataModel, "loan-bill.ftl"));
		}
		
		return null;
	}
	//test10
	@Action("widget-test10")
	public String showEmsProduct()throws Exception{
		String companyId=Struts2Utils.getRequest().getParameter("companyId");
		String userId=Struts2Utils.getRequest().getParameter("userId");
		String rows= Struts2Utils.getRequest().getParameter("rows");
		if(!companyId.equals("") && !userId.equals("")){
			ThreadParameters parameters= new ThreadParameters();
			parameters.setCompanyId(Long.parseLong(companyId));
			parameters.setUserId(Long.parseLong(userId));
			ParameterUtils.setParameters(parameters);
			HashMap<String, Object> dataModel=new HashMap<String, Object>();
			dataModel.put("page", widgetManager.listEmsProduct(portalEmsProductpage,rows));
			renderText(TagUtil.getContent(dataModel, "ems-product.ftl"));
		}
		
		return null;
	}
	//test11
	@Action("widget-test11")
	public String showEmsProductt()throws Exception{
		String companyId=Struts2Utils.getRequest().getParameter("companyId");
		String userId=Struts2Utils.getRequest().getParameter("userId");
		String rows= Struts2Utils.getRequest().getParameter("rows");
		if(!"".equals(companyId)&&!"".equals(userId)){
		   ThreadParameters parameters= new ThreadParameters();
		   parameters.setCompanyId(Long.parseLong(companyId));
		   parameters.setUserId(Long.parseLong(userId));
		   ParameterUtils.setParameters(parameters);
		   HashMap<String, Object> dataModel=new HashMap<String, Object>();
		   dataModel.put("page", widgetManager.listEmsProduct(portalEmsProductpage,rows));
			renderText(TagUtil.getContent(dataModel, "ems-product-two.ftl"));
		}
		return null;
	}
}
