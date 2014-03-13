package com.example.expense.template.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.norteksoft.product.api.entity.Department;
import com.norteksoft.acs.web.authorization.JsTreeUtil1;
import com.norteksoft.mms.base.MmsUtil;
import com.norteksoft.product.api.ApiFactory;
import com.norteksoft.product.orm.Page;
import com.norteksoft.product.util.JsonParser;
import com.norteksoft.product.util.tree.ZTreeNode;
import com.example.expense.template.dao.PlanTemplateDao;
import com.example.expense.entity.PlanTemplate;

@Service
@Transactional
public class PlanTemplateManager {
	@Autowired
	private PlanTemplateDao planTemplateDao;
	@Autowired
	private MmsUtil mmsUtil;

	public PlanTemplate getPlanTemplate(Long id){
		return planTemplateDao.get(id);
	}
	
	public void savePlanTemplate(PlanTemplate planTemplate){
		mmsUtil.saveColums(planTemplate.getName(), "模板1","ES_PLAN_TASK");
		planTemplate.setListCode(planTemplate.getName());
		planTemplateDao.save(planTemplate);
	}
	
	public void deletePlanTemplate(Long id){
		PlanTemplate planTemplate=getPlanTemplate(id);
		planTemplateDao.delete(planTemplate);
		//删除自定义的列表
		ApiFactory.getMmsService().deleteCustomListView(planTemplate.getListCode());
	}
	public void deletePlanTemplate(String ids){
		String[] deleteIds = ids.split(",");
		for (String id : deleteIds) {
			deletePlanTemplate(Long.valueOf(id));
		}
	}
	
	public void deletePlanTemplate(PlanTemplate planTemplate){
		planTemplateDao.delete(planTemplate);
		//删除自定义的列表
		ApiFactory.getMmsService().deleteCustomListView(planTemplate.getListCode());
	}
	
	public Page<PlanTemplate> list(Page<PlanTemplate>page){
		return planTemplateDao.list(page);
	}
	
	public List<PlanTemplate> listAll(){
		return planTemplateDao.getAllPlanTemplate();
	}

	public Page<PlanTemplate> search(Page<PlanTemplate> page) {
		return planTemplateDao.search(page);
	}
	/**
	 * 验证模版名称
	 * @param tempName
	 * @param id 
	 * @return
	 */
	public boolean validateName(String tempName, Long id) {
		List<PlanTemplate> list =planTemplateDao.getPlanTemplateByName(tempName,id);
		if(list.size()>0){
			return false;
		}else{
			return true;
		}
	}

	/**
	 * 自定义树取部门树
	 * @return
	 */
	public String getDepartmentTree() {
		List<ZTreeNode> treeNodes = new ArrayList<ZTreeNode>();
		List<Department> depts = ApiFactory.getAcsService().getAllDepartments();
		if(!depts.isEmpty()){
			departmentTree(depts,treeNodes);
		}
		return JsonParser.object2Json(treeNodes);
	}

	private void departmentTree(List<Department> depts,List<ZTreeNode> treeNodes) {
		for (Department dept : depts) {
			ZTreeNode root = new ZTreeNode("department-" + dept.getId()+"-"+dept.getName(),"0",dept.getName()+"("+dept.getSubCompanyName()+")", "false", "false", "", "", "department", "");
			treeNodes.add(root);
		}
	}
}
