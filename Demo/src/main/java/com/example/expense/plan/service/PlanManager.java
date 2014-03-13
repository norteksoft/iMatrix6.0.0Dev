package com.example.expense.plan.service;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.hibernate.Session;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.expense.entity.Plan;
import com.example.expense.entity.PlanItem;
import com.example.expense.plan.dao.PlanDao;
import com.example.expense.plan.dao.PlanItemDao;
import com.norteksoft.acs.base.enumeration.LogicOperator;
import com.norteksoft.product.api.ApiFactory;
import com.norteksoft.product.orm.Page;
import com.norteksoft.product.util.PropUtils;

@Service
@Transactional
public class PlanManager {
	@Autowired
	private PlanDao planDao;
	@Autowired
	private PlanItemDao planItemDao;
	
	public Session getSession() {
		return planDao.getSession();
	}

	public Plan getPlan(Long id){
		return planDao.get(id);
	}
	
	public void savePlan(Plan plan) throws Exception{
		for (PlanItem item : plan.getPlanItems()) {
			planItemDao.save(item);
		}
		//保存主子表
		planDao.save(plan);
	}
	
	public boolean deletePermission(Long id){
		LogicOperator authlink = LogicOperator.OR;
		LogicOperator dataRulelink = LogicOperator.OR;
		if("AND".equals(PropUtils.getProp("plan.data.auth.link")))authlink=LogicOperator.AND;
		if("AND".equals(PropUtils.getProp("product.data.rule.link")))dataRulelink = LogicOperator.AND;
		return  ApiFactory.getDataPermissionService().deletePermission(getPlan(id),authlink,dataRulelink);
	}
	
	public String deletePlan(String ids){
		String [] s = ids.split(",");
		int deleteNum=0;
		int failNum=0;
		for (String str : s) {
			boolean flag =deletePermission(Long.valueOf(str));
			if(flag){
				planDao.delete(Long.valueOf(str));
				deleteNum++;
			}else{
				failNum++;
			}
		}
		return deleteNum+" 条数据成功删除，"+failNum+" 条数据没有权限删除！";
	}
	
	public Page<Plan> list(Page<Plan> page){
		return planDao.list(page);
	}

	public Page<PlanItem> getPlanItemList(Page<PlanItem> pageItem, Long id) {
		return planItemDao.list(pageItem, id);
	}

	public void deletePlanItem(Long id) {
		planItemDao.delete(id);
	}

	public boolean getViewPermission(Plan plan) {
		LogicOperator authlink = LogicOperator.OR;
		LogicOperator dataRulelink = LogicOperator.OR;
		if("AND".equals(PropUtils.getProp("plan.data.auth.link")))authlink=LogicOperator.AND;
		if("AND".equals(PropUtils.getProp("product.data.rule.link")))dataRulelink = LogicOperator.AND;
		return ApiFactory.getDataPermissionService().viewPermission(plan,authlink,dataRulelink);
	}

	public boolean getUpdatePermission(Plan plan) {
		LogicOperator authlink = LogicOperator.OR;
		LogicOperator dataRulelink = LogicOperator.OR;
		if("AND".equals(PropUtils.getProp("plan.data.auth.link")))authlink=LogicOperator.AND;
		if("AND".equals(PropUtils.getProp("product.data.rule.link")))dataRulelink = LogicOperator.AND;
		return ApiFactory.getDataPermissionService().updatePermission(plan,authlink,dataRulelink);
	}

}
