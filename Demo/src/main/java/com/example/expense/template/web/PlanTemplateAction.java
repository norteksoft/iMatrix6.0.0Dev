package com.example.expense.template.web;

import java.util.Date;
import java.util.List;

import org.apache.struts2.convention.annotation.Action;
import org.apache.struts2.convention.annotation.Namespace;
import org.apache.struts2.convention.annotation.ParentPackage;
import org.springframework.beans.factory.annotation.Autowired;

import com.norteksoft.product.api.entity.Option;
import com.norteksoft.product.api.ApiFactory;
import com.norteksoft.product.orm.Page;
import com.norteksoft.product.util.ContextUtils;
import com.norteksoft.product.util.JsTreeUtils;
import com.norteksoft.product.util.PageUtils;
import com.norteksoft.product.web.struts2.CrudActionSupport;
import com.example.expense.template.service.PlanTemplateManager;
import com.example.expense.entity.PlanTemplate;


@Namespace("/template")
@ParentPackage("default")
public class PlanTemplateAction extends CrudActionSupport<PlanTemplate> {

	private static final long serialVersionUID = 1L;
	private Long id;
	private String deleteIds;
	private PlanTemplate planTemplate;
	private List<Option> types;
	private String tempName; //验证模版名称
	@Autowired
	private PlanTemplateManager planTemplateManager;
	private Page<PlanTemplate> page;
	
	
	public void setId(Long id) {
		this.id = id;
	}
	
	public Long getId() {
		return id;
	}
	
	public String getTempName() {
		return tempName;
	}

	public void setTempName(String tempName) {
		this.tempName = tempName;
	}
	
	public void setPage(Page<PlanTemplate> page) {
		this.page = page;
	}
	
	public Page<PlanTemplate> getPage() {
		return page;
	}
	
	public PlanTemplate getModel() {
		return planTemplate;
	}
	
	@Override
	protected void prepareModel() throws Exception {
		if(id==null){
			planTemplate=new PlanTemplate();
			planTemplate.setCompanyId(ContextUtils.getCompanyId());
			planTemplate.setCreatedTime(new Date());
			planTemplate.setCreator(ContextUtils.getUserName());
		}else {
			planTemplate=planTemplateManager.getPlanTemplate(id);
		}
	}
	
	@Action("input")
	@Override
	public String input() throws Exception {
		types=ApiFactory.getSettingService().getOptionsByGroupCode("demo_plan_template");
		return SUCCESS;
	}
	
	@Action("save")
	@Override
	public String save() throws Exception {
		planTemplateManager.savePlanTemplate(planTemplate);
		types=ApiFactory.getSettingService().getOptionsByGroupCode("demo_plan_template");
		addActionMessage("<font class=\"onSuccess\"><nobr>保存成功!</nobr></font>");
		return "input";
	}
	
	@Action("delete")
	@Override
	public String delete() throws Exception {
		planTemplateManager.deletePlanTemplate(deleteIds);
		return "list";
	}

	@Action("list")
	@Override
	public String list() throws Exception {
		return SUCCESS;
	}
	@Action("validate-name")
	public String validateName() throws Exception {
		if(id==null){
			boolean result = planTemplateManager.validateName(tempName,id);
			renderText(result+"_");
		}else{
			boolean result = planTemplateManager.validateName(tempName,id);
			renderText(result+"_");
		}
		
		return null;
	}
	
	@Action("list-datas")
	public String getListDatas() throws Exception {
		page = planTemplateManager.search(page);
		renderText(PageUtils.pageToJson(page));
		return null;
	}
	@Action("task-tree")
	public String taskTree() throws Exception{
		List<PlanTemplate> templates=planTemplateManager.listAll();
		StringBuilder tree = new StringBuilder("[ ");
		for(PlanTemplate template:templates){
			tree.append(JsTreeUtils.generateJsTreeNodeNew(template.getId().toString(), "close", template.getName(),"")).append(",");
		}
		JsTreeUtils.removeLastComma(tree);
		tree.append(" ]");
		renderText(tree.toString());
		return null;
	}
	
	/**
	 * 自定义树取部门树
	 * @return
	 */
	@Action("department-tree")
	public String departmentTree(){
		renderText(planTemplateManager.getDepartmentTree());
		return null;
	}
	
	public List<Option> getTypes() {
		return types;
	}

	public void setDeleteIds(String deleteIds) {
		this.deleteIds = deleteIds;
	}


}
