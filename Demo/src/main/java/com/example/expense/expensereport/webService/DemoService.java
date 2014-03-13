package com.example.expense.expensereport.webService;

import java.util.Date;

import javax.ws.rs.Consumes;
import javax.ws.rs.FormParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.example.expense.base.utils.ExpenseThread;
import com.example.expense.entity.ExpenseReport;
import com.example.expense.expensereport.service.ExpenseReportManager;
import com.norteksoft.acs.service.AcsUtils;
import com.norteksoft.product.api.ApiFactory;
import com.norteksoft.product.api.entity.User;
import com.norteksoft.product.util.ThreadPool;

@Component//spring注入
@Path("/ems")
public class DemoService{
	@Autowired
	private AcsUtils acsUtils;
	
	
	@Autowired
	private ExpenseReportManager expenseReportManager;
	/**
	  * 根据与客户确认情况，用户登录使用用户名、密码方式进行登录。用户认证接口暂定义为
	  * @param 	account    输入参数    用户登录时在鼎盾打印客户端输入的用户帐户
	  * @param 	password   输入参数    用户登录时在鼎盾打印客户端输入的用户密码
	  * Md5.toMessageDigest(password))
	  */
	@POST
	@Path("/dispatchReport")
	@Produces("text/html;charset=UTF-8")
	@Consumes("text/html;charset=UTF-8")
	public Response dispatchReport(@FormParam("runAsUser")String loginName,@FormParam("runAsUserId")String userId,@FormParam("companyId")String companyId) {//接收所有参数
//		public Response dispatchReport(@FormParam("runAsUser")String LoginName) {//可以只接收登录名一个参数
//		public Response dispatchReport(@FormParam("runAsUserId")String userId) {//也可以只接收用户id一个参数
//		public Response dispatchReport(@FormParam("companyId")String companyId) {//也可以只接收用户id一个参数
		
		if(StringUtils.isNotEmpty(userId)){
			User user = ApiFactory.getAcsService().getUserById(Long.parseLong(userId));
			if(user!=null){
				ExpenseReport expenseReport = new ExpenseReport();
				expenseReport.setCreator(user.getLoginName());
				expenseReport.setCompanyId(user.getCompanyId());
				expenseReport.setCreatedTime(new Date());
				expenseReport.setName(user.getName());
				expenseReport.setMoney(1000l);
				expenseReport.setInvoiceAmount(10);
				expenseReport.setFirstLoginName(user.getLoginName());
				expenseReport.setSignLoginNames(user.getLoginName());
				expenseReport.setReadLoginNames(user.getLoginName());
				
				ExpenseThread thread = new ExpenseThread();
				thread.setExpenseReport(expenseReport);
				thread.setCompanyId(user.getCompanyId());
				thread.setLoginName(user.getLoginName());
				thread.setUserName(user.getName());
				thread.setExpenseReportManager(expenseReportManager);
				ThreadPool.execute(thread);
			}
		}
		return Response.status(200).entity("ok").build();
	}
}
