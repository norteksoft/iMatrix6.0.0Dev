package com.example.expense.plan.web;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.struts2.convention.annotation.Action;
import org.apache.struts2.convention.annotation.Namespace;
import org.apache.struts2.convention.annotation.ParentPackage;
import org.springframework.beans.factory.annotation.Autowired;

import com.example.expense.entity.Plan;
import com.example.expense.entity.PlanItem;
import com.example.expense.plan.service.PlanManager;
import com.norteksoft.acs.entity.authorization.Role;
import com.norteksoft.acs.service.authorization.StandardRoleManager;
import com.norteksoft.product.orm.Page;
import com.norteksoft.product.util.ContextUtils;
import com.norteksoft.product.util.JsTreeUtils;
import com.norteksoft.product.util.JsonParser;
import com.norteksoft.product.util.PageUtils;
import com.norteksoft.product.util.tree.ZTreeNode;
import com.norteksoft.product.web.struts2.CrudActionSupport;
import com.norteksoft.product.web.struts2.Struts2Utils;

@Namespace("/plan")
@ParentPackage("default")
public class PlanAction extends CrudActionSupport<Plan> {

	private static final long serialVersionUID = 1L;
	private Long id;
	private String deleteIds;
	private Plan plan;
	private Page<Plan> page;
	private Page<PlanItem> pageItem;
	private String permissionFlag;
	private String position;
	@Autowired
	private PlanManager planManager;
	@Autowired
	private StandardRoleManager standardRoleManager;
	
	public Plan getModel() {
		return plan;
	}
	
	@Override
	protected void prepareModel() throws Exception {
		if(id==null){
			plan=new Plan();
		}else {
			plan=planManager.getPlan(id);
		}
	}
	
	@Action("input")
	@Override
	public String input() throws Exception {
		return SUCCESS;
	}
	
	public void prepareView() throws Exception {
		prepareModel();
	}
	@Action("view")
	public String view() throws Exception {
		return SUCCESS;
	}
	
	@Action("save")
	@Override
	public String save() throws Exception {
		List<PlanItem> items=new ArrayList<PlanItem>();
		List<Object> objects=JsonParser.getFormTableDatas(PlanItem.class);
		for (Object obj : objects) {
			PlanItem planItem=(PlanItem)obj;
			planItem.setPlan(plan);
			items.add(planItem);
		}
		plan.setPlanItems(items);
		planManager.savePlan(plan);
		addActionMessage("<font class=\"onSuccess\"><nobr>保存成功！</nobr></font>");
		return "input";
	}
	
	@Action("delete")
	@Override
	public String delete() throws Exception {
		addActionMessage("<font class=\"onSuccess\"><nobr>"+planManager.deletePlan(deleteIds)+"</nobr></font>");
		return "list";
	}

	@Action("list")
	@Override
	public String list() throws Exception {
		return SUCCESS;
	}
	
	@Action("list-datas")
	public String getListDatas() throws Exception {
		page = planManager.list(page);
		renderText(PageUtils.pageToJson(page));
		return null;
	}
	
	@Action("list-plan-item")
	public String getItemDatas() throws Exception {
		pageItem = planManager.getPlanItemList(pageItem,id);
		renderText(PageUtils.pageToJson(pageItem));
		return null;
	}
	
	
	@Action("select-role")
	public String selectRole() throws Exception {
		List<ZTreeNode> treeNodes = new ArrayList<ZTreeNode>();
		
		List<Role> roles=standardRoleManager.getAllStandardRole(ContextUtils.getSystemId("ems"));
		if(roles.size()<=0){
			ZTreeNode root = new ZTreeNode("_role","0","角色", "false", "false", "", "", "folder", "");
			treeNodes.add(root);
		}else{
			ZTreeNode root = new ZTreeNode("_role","0","角色", "true", "false", "", "", "folder", "");
			treeNodes.add(root);
			roles(roles,treeNodes,"_role");
		}
		this.renderText(JsonParser.object2Json(treeNodes));
		return null;
	}
	
	private void roles(List<Role> roles,List<ZTreeNode> treeNodes,String parentId){
		for(Role role:roles){
			ZTreeNode root = new ZTreeNode("role~~"+role.getId()+"~~"+role.getName(),parentId,role.getName(), "false", "false", "", "", "folder", "");
			treeNodes.add(root);
		}
	}
	
	@Action("delete-planItem")
	public String deletePlanItem() throws Exception {
		planManager.deletePlanItem(id);
		String callback=Struts2Utils.getParameter("callback");
		this.renderText(callback+"({msg:'删除成功！'})");
		return null;
	}
	
	
	@Action("validate-permission")
	public String validatePermission(){
		boolean flag = false;
		plan=planManager.getPlan(id);
		if("view".equals(permissionFlag)){
			flag = planManager.getViewPermission(plan);
		}else if("update".equals(permissionFlag)){
			flag = planManager.getUpdatePermission(plan);
		}
		renderText(flag+"_"+id);
		return null;
	}
	
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getDeleteIds() {
		return deleteIds;
	}

	public void setDeleteIds(String deleteIds) {
		this.deleteIds = deleteIds;
	}

	public Page<Plan> getPage() {
		return page;
	}

	public void setPage(Page<Plan> page) {
		this.page = page;
	}

	public Plan getPlan() {
		return plan;
	}

	public Page<PlanItem> getPageItem() {
		return pageItem;
	}

	public void setPageItem(Page<PlanItem> pageItem) {
		this.pageItem = pageItem;
	}

	public String getPermissionFlag() {
		return permissionFlag;
	}

	public void setPermissionFlag(String permissionFlag) {
		this.permissionFlag = permissionFlag;
	}

	public void setPosition(String position) {
		this.position = position;
	}

	public String getPosition() {
		return position;
	}

}
