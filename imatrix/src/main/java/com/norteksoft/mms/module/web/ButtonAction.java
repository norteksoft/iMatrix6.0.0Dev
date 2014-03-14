package com.norteksoft.mms.module.web;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.struts2.convention.annotation.Namespace;
import org.apache.struts2.convention.annotation.ParentPackage;
import org.apache.struts2.convention.annotation.Result;
import org.apache.struts2.convention.annotation.Results;
import org.springframework.beans.factory.annotation.Autowired;

import com.norteksoft.mms.module.entity.Button;
import com.norteksoft.mms.module.entity.Menu;
import com.norteksoft.mms.module.entity.ModulePage;
import com.norteksoft.mms.module.enumeration.ViewType;
import com.norteksoft.mms.module.service.ButtonManager;
import com.norteksoft.mms.module.service.MenuManager;
import com.norteksoft.mms.module.service.ModulePageManager;
import com.norteksoft.product.api.ApiFactory;
import com.norteksoft.product.util.ContextUtils;
import com.norteksoft.product.util.JsTreeUtils;
import com.norteksoft.product.util.JsonParser;
import com.norteksoft.product.util.tree.ZTreeNode;
import com.norteksoft.product.web.struts2.CrudActionSupport;
import com.norteksoft.product.web.struts2.Struts2Utils;

@Namespace("/module")
@ParentPackage("default")
@Results( { @Result(name = CrudActionSupport.RELOAD, location = "button", type = "redirectAction")})
public class ButtonAction extends CrudActionSupport<Button> {
	private static final long serialVersionUID = 1L;
	
	private List<Button> buttons;
	private Button button;
	private Long id;
	private String code;
	private Long pageId;
	private Long menuId;
	private Integer currentDisplayOrder;
	private String currentId;
	private static final String SUCCESS_MESSAGE_LEFT = "<font class=\"onSuccess\"><nobr>";
	private static final String MESSAGE_RIGHT = "</nobr></font>";
	private static final String ERROR_MESSAGE_LEFT = "<font class=\"onError\"><nobr>";
	
	private ButtonManager buttonManager;
	private MenuManager menuManager;
	private ModulePageManager modulePageManager;
	
	
	//列表需要的字段
	private ModulePage modulePage;
	private String rowid;
	
	@Autowired
	public void setMenuManager(MenuManager menuManager) {
		this.menuManager = menuManager;
	}
	@Autowired
	public void setModulePageManager(ModulePageManager modulePageManager) {
		this.modulePageManager = modulePageManager;
	}	
	@Autowired
	public void setButtonManager(ButtonManager buttonManager) {
		this.buttonManager = buttonManager;
	}
	
	private void addErrorMessage(String message){
		this.addActionMessage(ERROR_MESSAGE_LEFT+message+MESSAGE_RIGHT);
	}
	
	private void addSuccessMessage(String message){
		this.addActionMessage(SUCCESS_MESSAGE_LEFT+message+MESSAGE_RIGHT);
	}
	
	@Override
	public String delete() throws Exception {
		buttonManager.deleteButton(id);
		String callback=Struts2Utils.getParameter("callback");
		ApiFactory.getBussinessLogService().log("系统元数据管理/按钮管理", 
				"删除按钮", 
				ContextUtils.getSystemId("mms"));
		this.renderText(callback+"({msg:'删除成功'})");
		return null;
	}

	@Override
	public String input() throws Exception {
		return INPUT;
	}

	@Override
	public String list() throws Exception {
		//buttons = buttonManager.getButtons(pageId);
		modulePage = modulePageManager.getModulePage(pageId);
		ViewType viewType = modulePage.getViewType();
		Boolean sign = buttonManager.isButtonExist(pageId);
		if(sign){
			if(viewType.equals(ViewType.LIST_VIEW)){
				//buttons = buttonManager.createDefaultListButton(pageId);
				buttonManager.saveDefaultListButton(pageId);
			}else{
				//buttons = buttonManager.createDefaultFormButton(pageId);
				buttonManager.saveDefaultFormButton(pageId);
			}
		}
		ApiFactory.getBussinessLogService().log("系统元数据管理/按钮管理", 
				"按钮列表", 
				ContextUtils.getSystemId("mms"));
		return SUCCESS;
	}
	
	@Override
	protected void prepareModel() throws Exception {
		if(id==null){
			button=new Button();
		}else{
			button=buttonManager.getButton(id);
		}
	}

	@Override
	public String save() throws Exception {
		return null;
	}
	
	@SuppressWarnings("unchecked")
	public String saveButtons() throws Exception {
		//Map<String,String[]> parameterMap=Struts2Utils.getRequest().getParameterMap();
		buttonManager.save(pageId);
		ApiFactory.getBussinessLogService().log("系统元数据管理/按钮管理", 
				"保存按钮", 
				ContextUtils.getSystemId("mms"));
		addSuccessMessage("保存成功");
		return list();
	}
	
	public String validateCode() throws Exception {
		Boolean isCodeExist=buttonManager.isCodeExist(code,pageId);
		if(isCodeExist){  
			this.renderText("true");
		}else{
			this.renderText("false");
		}
		return null;
	}
	
	public String selectPage() throws Exception{
		return "selectPage";
	}
	
	/**
	 * 菜单页面树
	 */
	public String pageTree() throws Exception {
		StringBuilder tree = new StringBuilder();
		String[] str=currentId.split(";");
		if("0".equals(currentId)){
			tree.append(defaultTree(menuId));
		}else if("menu".equals(str[str.length-1])){
			tree.append(defaultTree(Long.parseLong(str[0])));
		}
		renderText(tree.toString());
		return null;
	}
	
	private String defaultTree(Long menuId){
		Menu menu=menuManager.getMenu(menuManager.getRootMenu(menuId).getId());
		List<ZTreeNode> treeNodes = new ArrayList<ZTreeNode>();
		ZTreeNode node = null;
		List<Menu> children=menuManager.getChildrenEnabledMenus(menu.getId());
		if(children==null||children.size()<=0){
			List<ModulePage> modulePages=modulePageManager.getEnableModulePagesByMenuId(menu.getId());
			if(modulePages.size()>0){
				node = new ZTreeNode(menu.getId().toString()+";"+menu.getName()+";menu","0",menu.getName()+"(菜单)", "true", "false", "", "", "folder", "");
				treeNodes.add(node);
				childModulePage(modulePages,menu.getId().toString()+";"+menu.getName()+";menu",treeNodes);
			}else{
				node = new ZTreeNode(menu.getId().toString()+";"+menu.getName()+";menu","0",menu.getName()+"(菜单)", "false", "false", "", "", "folder", "");
				treeNodes.add(node);
			}
		}else{
			node = new ZTreeNode(menu.getId().toString()+";"+menu.getName()+";menu","0",menu.getName()+"(菜单)", "true", "false", "", "", "folder", "");
			treeNodes.add(node);
			childModulePage(modulePageManager.getModulePagesByMenuId(menu.getId()),menu.getId().toString()+";"+menu.getName()+";menu",treeNodes);
			childMenu(children,menu.getId().toString()+";"+menu.getName()+";menu",treeNodes);
		}
		return JsonParser.object2Json(treeNodes);
	}
	
	/*
	 * 递归菜单父子关系，形成tree
	 */
	private void childMenu(List<Menu> menus,String pid,List<ZTreeNode> treeNodes){
		java.util.Collections.sort(menus);
		ZTreeNode node=null;
		for(Menu menu :menus){
			List<Menu> children=menuManager.getChildrenEnabledMenus(menu.getId());
			if(children==null||children.size()<=0){
				List<ModulePage> modulePages=modulePageManager.getEnableModulePagesByMenuId(menu.getId());
				if(modulePages.size()>0){
					node = new ZTreeNode(menu.getId().toString()+";"+menu.getName()+";menu",pid,menu.getName()+"(菜单)", "true", "false", "", "", "folder", "");
					treeNodes.add(node);
					childModulePage(modulePages,menu.getId().toString()+";"+menu.getName()+";menu",treeNodes);
				}else{
					node = new ZTreeNode(menu.getId().toString()+";"+menu.getName()+";menu",pid,menu.getName()+"(菜单)", "false", "false", "", "", "folder", "");
					treeNodes.add(node);
				}
			}else{
				node = new ZTreeNode(menu.getId().toString()+";"+menu.getName()+";menu",pid,menu.getName()+"(菜单)", "true", "false", "", "", "folder", "");
				treeNodes.add(node);
				childModulePage(modulePageManager.getModulePagesByMenuId(menu.getId()),menu.getId().toString()+";"+menu.getName()+";menu",treeNodes);
				childMenu(children,menu.getId().toString()+";"+menu.getName()+";menu",treeNodes);
			}
		}
	}
	
	private void childModulePage(List<ModulePage> modulePages,String pid,List<ZTreeNode> treeNodes){
		java.util.Collections.sort(modulePages);
		ZTreeNode node=null;
		for(ModulePage modulePage :modulePages){
			node = new ZTreeNode(modulePage.getId().toString()+";"+modulePage.getName()+";view",pid,modulePage.getName()+"(页面)", "false", "false", "", "", "folder", "");
			treeNodes.add(node);
		}
	}

	public String showEvent() throws Exception{
		return "event";
	}
	
	public Button getModel() {
		return button;
	}
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public void setCode(String code) {
		this.code = code;
	}

	public List<Button> getButtons() {
		return buttons;
	}

	public Integer getCurrentDisplayOrder() {
		return currentDisplayOrder;
	}

	public void setCurrentDisplayOrder(Integer currentDisplayOrder) {
		this.currentDisplayOrder = currentDisplayOrder;
	}
	public Long getMenuId() {
		return menuId;
	}
	public void setMenuId(Long menuId) {
		this.menuId = menuId;
	}
	public Long getPageId() {
		return pageId;
	}
	public void setPageId(Long pageId) {
		this.pageId = pageId;
	}
	public void setCurrentId(String currentId) {
		this.currentId = currentId;
	}
	public ModulePage getModulePage() {
		return modulePage;
	}
	public void setModulePage(ModulePage modulePage) {
		this.modulePage = modulePage;
	}
	public String getRowid() {
		return rowid;
	}
	public void setRowid(String rowid) {
		this.rowid = rowid;
	}
}
