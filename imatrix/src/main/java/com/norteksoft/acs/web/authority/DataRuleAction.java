package com.norteksoft.acs.web.authority;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.struts2.convention.annotation.Action;
import org.apache.struts2.convention.annotation.Namespace;
import org.apache.struts2.convention.annotation.ParentPackage;
import org.apache.struts2.convention.annotation.Result;
import org.apache.struts2.convention.annotation.Results;
import org.springframework.beans.factory.annotation.Autowired;

import com.norteksoft.acs.base.enumeration.ConditionType;
import com.norteksoft.acs.base.utils.PermissionItemTreeUtil;
import com.norteksoft.acs.base.utils.PermissionUtils;
import com.norteksoft.acs.entity.authority.Condition;
import com.norteksoft.acs.entity.authority.DataRule;
import com.norteksoft.acs.entity.authority.Permission;
import com.norteksoft.acs.service.authority.ConditionManager;
import com.norteksoft.acs.service.authority.DataRuleManager;
import com.norteksoft.acs.service.authority.PermissionItemManager;
import com.norteksoft.acs.service.authority.PermissionManager;
import com.norteksoft.mms.form.entity.DataTable;
import com.norteksoft.mms.form.entity.ListView;
import com.norteksoft.mms.form.entity.TableColumn;
import com.norteksoft.mms.form.service.ListColumnManager;
import com.norteksoft.mms.form.service.ListViewManager;
import com.norteksoft.mms.module.entity.Menu;
import com.norteksoft.mms.module.service.MenuManager;
import com.norteksoft.product.orm.Page;
import com.norteksoft.product.util.ContextUtils;
import com.norteksoft.product.util.JsTreeUtils;
import com.norteksoft.product.util.JsonParser;
import com.norteksoft.product.util.PageUtils;
import com.norteksoft.product.util.tree.ZTreeNode;
import com.norteksoft.product.web.struts2.CrudActionSupport;
import com.norteksoft.product.web.struts2.Struts2Utils;

@Namespace("/authority")
@ParentPackage("default")
@Results( { @Result(name = CrudActionSupport.RELOAD, location = "data-rule", type = "redirectAction") })
public class DataRuleAction extends CrudActionSupport<DataRule>{
	private static final long serialVersionUID = 1L;
	
	private Long id;
	private String ids;
	private DataRule dataRule;
	private Page<DataRule> page=new Page<DataRule>(0,true);
	private Page<DataTable> dataRulePage=new Page<DataTable>(0,true);
	private Page<TableColumn> tableColumnPage=new Page<TableColumn>(0,true);
	private Page<Condition> conditionPage=new Page<Condition>(0,true);
	private Long tableId;
	private String currentInputId;
	private Long ruletypeId;
	private Long dataRuleId;
	private String dataValue;
	private List<String[]> values = new ArrayList<String[]>();
	private Long sysMenuId;//关联的菜单id
	private boolean selectPageFlag=false;
	private Long dataTableId;
	private String valueUrl;
	private List<ListView> listViews;
	private Long tableColumnId;//数据表列表的的行id
	private String standardField;//标准字段名：~~creatorId，~~departmentId，~~roleId，~~workgroupId
	private String currentTreeId;
	private String rowId;//数据分类中规则条件的id
	private Boolean deparmentInheritable=true;//简易设置中子部门是否继承权限
	private String position;//选择数据表所在的位置，其值为fast（快速授权中）或dataRule（数据分类中）
	private String messageTip="";//当修改数据表时的提示信息
	private Long permissionId;//快速授权的permissionId，当修改数据表时的提示信息要用到
	private Long listViewId;//快速授权的listViewId，当修改数据表时的提示信息要用到
	private boolean hasBranch = false;//是否有分支
	@Autowired
	private DataRuleManager dataRuleManager;
	@Autowired
	private MenuManager menuManager;
	@Autowired
	private ConditionManager conditionManager;
	@Autowired
	private ListViewManager listViewManager;
	@Autowired
	private ListColumnManager listColumnManager;
	@Autowired
	private PermissionManager permissionManager;
	private static final String SUCCESS_MESSAGE_LEFT = "<font class=\"onSuccess\"><nobr>";
	private static final String MESSAGE_RIGHT = "</nobr></font>";
	private static final String ERROR_MESSAGE_LEFT = "<font class=\"onError\"><nobr>";
	
	
	protected void addErrorMessage(String message){
		this.addActionMessage(ERROR_MESSAGE_LEFT+message+MESSAGE_RIGHT);
	}
	protected void addSuccessMessage(String message){
		this.addActionMessage(SUCCESS_MESSAGE_LEFT+message+MESSAGE_RIGHT);
	}

	@Override
	@Action("data-rule-delete")
	public String delete() throws Exception {
		dataRuleManager.deleteDataRule(ids);
		this.renderText("ok");
		return null;
	}
	
	@Action("data-rule-validateDelete")
	public String validateDelete() throws Exception {
		String result=dataRuleManager.validateDelete(ids);
		if(StringUtils.isNotEmpty(result)){
			this.renderText(result);
		}else{
			this.renderText("ok");
		}
		return null;
	}

	@Override
	@Action("data-rule-input")
	public String input() throws Exception {
		if(dataRuleId==null && sysMenuId != null){
			dataRule.setMenuId(sysMenuId);
			dataRule.setCode(createDataRuleCode());
		}
		return "data-rule-input";
	}
	
	private String createDataRuleCode(){
		int num=0;
		List<DataRule> dataRuleList=dataRuleManager.getDefaultCodeDataRules();
		if(dataRuleList != null && dataRuleList.size()>0){
			for(DataRule w:dataRuleList){
				String codeNum=w.getCode().replace("dataRule-", "");
				if(codeNum.matches("^-?\\d+$")&&Integer.valueOf(codeNum)>num){
					num=Integer.valueOf(codeNum);
				}
			}
		}else{
			return "dataRule-1";
		}
		return "dataRule-"+(num+1);
	}

	@Override
	@Action("data-rule")
	public String list() throws Exception {
		List<Menu> menus = menuManager.getEnabledRootMenuByCompany();
		if(sysMenuId==null&&menus.size()>0){
			sysMenuId = menus.get(0).getId();
		}
		if(page.getPageSize()>1){
			dataRuleManager.getDataRulesByMenuId(page,sysMenuId);
			this.renderText(PageUtils.pageToJson(page));
			return null;
		}
		return "data-rule";
	}

	protected void prepareModel() throws Exception {
		if(dataRuleId==null){
			dataRule=new DataRule();
		}else{
			dataRule=dataRuleManager.getDataRule(dataRuleId);
		}
	}

	@Override
	@Action("data-rule-save")
	public String save() throws Exception {
		dataRule.setMenuId(sysMenuId);
		dataRuleManager.saveDataRule(dataRule);
		dataRuleId=dataRule.getId();
		addSuccessMessage("保存成功");
		return "data-rule-input";
	}
	
	public void prepareValidateOnlyCode() throws Exception {
		prepareModel();
	}
	
	/**
	 * 验证数据规则的编码是否唯一
	 * @return
	 */
	@Action("validate-only-code")
	public String validateOnlyCode() throws Exception{
		boolean sign=true;
		DataRule original=null;
		if(dataRuleId==null){
			original=dataRuleManager.getDataRuleByCode(dataRule.getCode());
			if(original!=null){
				sign=false;
			}
		}else{
			original=dataRuleManager.getDataRuleByCode(dataRule.getCode(),dataRuleId);
			if(original!=null){
				sign=false;
			}
		}
		if(sign){
			this.renderText("ok");
		}else{
			this.renderText("no");
		}
		return null;
	}
	
	/**
	 * 选择数据表
	 * @return
	 */
	@Action("data-rule-selectDataTable")
	public String selectDataTable() {
		if(dataRuleId!=null){//数据分类中修改数据表时提示信息
			messageTip = "请修改数据授权【";
			String message = "";
			List<Permission> permissions = permissionManager.getPermissionsByDataRule(dataRuleId);
			for(int i = 0;i<permissions.size();i++){
				Permission p = permissions.get(i);
				if(p.getListViewId()!=null){
					message = message+p.getCode();
					if(i<permissions.size()-1){
						message = message+",";
					}
				}
			}
			if(StringUtils.isNotEmpty(message)){
				messageTip = messageTip+message+"】选择的列表";
			}else{
				messageTip="";
			}
		}
		if(!selectPageFlag){
			List<ZTreeNode> treeNodes = new ArrayList<ZTreeNode>();
			ZTreeNode node=null;
			List<Menu> menus = menuManager.getEnabledStandardRootMenuByCompany();
			if(menus.size()<=0){
				node = new ZTreeNode("menu","0","数据表", "false", "false", "", "", "folder", "");
			}else{
				for (Menu menu : menus) {
					List<DataTable> dataTables = dataRuleManager.getEnabledStandardDataTableByMenuId(menu.getId());
					node = new ZTreeNode(menu.getId().toString(),"0",menu.getName(), "false", "false", "", "", "folder", "");
					treeNodes.add(node);
					for(DataTable dataTable:dataTables){
						node = new ZTreeNode(dataTable.getId().toString()+"="+dataTable.getAlias()+"="+menu.getId(),menu.getId().toString(),dataTable.getAlias()+"("+ dataTable.getName()+")", "false", "false", "", "", "folder", "");
						treeNodes.add(node);
					}
				}
			}
			renderText(JsonParser.object2Json(treeNodes));
			return null;
		}
		return SUCCESS;
	}
	
	/**
	 * 选择列表
	 * @return
	 */
	@Action("data-rule-selectListView")
	public String selectListView() {
		listViews = listViewManager.getListViewByTabelId(dataTableId);
		return "data-rule-selectListView";
	}
	
	/**
	 * 选择数据规则
	 * @return
	 */
	@Action("data-rule-selectDataRule")
	public String selectDataRule() {
		if(!selectPageFlag){
			List<Menu> menus = menuManager.getEnabledStandardRootMenuByCompany();
			StringBuilder tree = new StringBuilder("[ ");
			if(menus.size()<=0){
				tree.append(JsTreeUtils.generateJsTreeNodeNew("menu", "", "数据规则", "root"));
			}else{
				for (Menu menu : menus) {
					List<DataRule> dataRules = dataRuleManager.getDataRulesByMenuId(menu.getId());
					if(dataRules.size()>0){
						tree.append(JsTreeUtils.generateJsTreeNodeDefault("menu", "open", 
								menu.getName(),getDataRuleByMenu(dataRules))).append(",");
					}else{
						tree.append(JsTreeUtils.generateJsTreeNodeDefault("menu", null, 
								menu.getName())).append(",");
					}
				}
			}
			JsTreeUtils.removeLastComma(tree);
			tree.append(" ]");
			renderText(tree.toString());
			return null;
		}
		return SUCCESS;
	}
	private String getDataRuleByMenu(List<DataRule> dataRules) {
		StringBuilder subNodes=new StringBuilder();
		for (DataRule dataRule : dataRules) {
			subNodes.append(JsTreeUtils.generateJsTreeNodeDefault(dataRule.getId().toString()+"_"+dataRule.getName(), null,dataRule.getName())).append(",");
		}
		JsTreeUtils.removeLastComma(subNodes);
		return subNodes.toString();
	}
	
	/**
	 * 选择数据表字段
	 * @return
	 */
	@Action("data-rule-selectColumn")
	public String selectColumn() {
		if(tableColumnPage.getPageSize()>1){
			dataRuleManager.getTableColumnByDataTableId(tableColumnPage,tableId);
			this.renderText(PageUtils.pageToJson(tableColumnPage));
			return null;
		}
		return "data-rule-selectColumn";
	}
	/**
	 * 设置布尔型数据的条件值
	 * @return
	 */
	@Action("data-rule-setValue")
	public String setValue() {
		String[] stringArray = dataValue.split(",");
		for (String s : stringArray) {
			String[] str = new String[2];
			str[0] = s.split(":")[0];
			str[1] = s.split(":")[1].replace("'", "");
			values.add(str);
		}
		return "data-rule-setValue";
	}
	
	
	
	/**
	 * 删除数据表规则条件
	 * @return
	 */
	@Action("data-rule-deleteCondition")
	public String deleteCondition() {
		conditionManager.delete(id);
		String callback=Struts2Utils.getParameter("callback");
		this.renderText(callback+"({msg:'删除成功'})");
		return null;
	}
	
	/**
	 * 根据规则id获得数据表规则条件
	 * @return
	 */
	@Action("data-rule-condition-list")
	public String conditionList() {
		if(conditionPage.getPageSize()>1){
			if(id!=null){
				conditionManager.getConditionPage(conditionPage, id);
				this.renderText(PageUtils.pageToJson(conditionPage));
			}
		}
		return null;
	}
	
	/**
	 * 规则类别树
	 * @return
	 * @throws Exception
	 */
	@Action("data-rule-type-tree")
	public String dataRuleTypeTree() throws Exception {
		List<ZTreeNode> treeNodes = new ArrayList<ZTreeNode>();
		List<Menu> menus = menuManager.getEnabledStandardRootMenuByCompany();
		java.util.Collections.sort(menus);
		String result ="";
		for(Menu menu :menus){
			ZTreeNode root = new ZTreeNode("menuId_"+menu.getId().toString(),"0",menu.getName(), "false", "false", "", "", "folder", "");
			treeNodes.add(root);
		}
		result = JsonParser.object2Json(treeNodes);
		renderText(result);
		return null;
	}
	
	/**
	 * 树页面
	 */
	@Action("data-rule-tree-page")
	public String dataRuleTreePage(){
		hasBranch = ContextUtils.hasBranch();
		return SUCCESS;
	}
	
	/**
	 * 组织结构+标准值树
	 * @return
	 */
	@Action("data-rule-selectRelativeCondition")
	public String relativeConditionTree(){
		List<ZTreeNode> treeNodes = new ArrayList<ZTreeNode>();
		if(PermissionUtils.STANDARD_FIELD_CREATOR.equals(standardField)){
			this.renderText(PermissionItemTreeUtil.getCreatorTree(currentTreeId));
			return null;
		}else if(PermissionUtils.STANDARD_FIELD_DEPARMENT.equals(standardField)){
			this.renderText(PermissionItemTreeUtil.getDepartmentTree(currentTreeId));
			return null;
		}else if(PermissionUtils.STANDARD_FIELD_ROLE.equals(standardField)){
			this.renderText(PermissionItemTreeUtil.getRoleTree());
			return null;
		}else if(PermissionUtils.STANDARD_FIELD_WORKGROUP.equals(standardField)){
			this.renderText(PermissionItemTreeUtil.getWorkgroupTree(currentTreeId));
			return null;
		}else{
			ZTreeNode root = new ZTreeNode("root~~root~~root","0","标准值", "true", "false", "", "", "folder", "");
			treeNodes.add(root);
			conditionChildren(treeNodes,"root~~root~~root");
			this.renderText(JsonParser.object2Json(treeNodes));
			return null;
		}
	}
	

	private void conditionChildren(List<ZTreeNode> treeNodes,String parentId) {
		ConditionType[] types = ConditionType.values();
		for(int i=0;i<types.length;i++){
			ZTreeNode root = new ZTreeNode("condition~~"+types[i].toString()+"~~"+Struts2Utils.getText(types[i].getCode()),parentId,Struts2Utils.getText(types[i].getCode()), "false", "false", "", "", "folder", "");
			treeNodes.add(root);
		}
	}
	
	@Action("data-rule-getSystemUrlByTalbeId")
	public String getSystemUrlByTalbeId(){
		this.renderText(dataRuleManager.getSystemUrlByTalbeId(dataTableId)+valueUrl);
		return null;
	}
	
	@Action("data-rule-getOptionValue")
	public String getOptionValue(){
		String result = dataRuleManager.getOptionValue(dataValue);
		this.renderText(result+"-"+currentInputId);
		return null;
	}
	/**
	 * 根据数据表字段信息的行id获取列表中的值设置
	 * @return
	 */
	@Action("data-rule-getValuesetByTableColumn")
	public String getValuesetByTableColumn(){
		String valueSet = listColumnManager.getValuesetByTableColumn(tableColumnId);
		renderText(valueSet);
		return null;
	}
	
	@Action("data-rule-selectStandardColumn")
	public String selectStandardColumn(){
		return "data-rule-selectStandardColumn";
	}
	
	
	public DataRule getModel() {
		return dataRule;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getIds() {
		return ids;
	}

	public void setIds(String ids) {
		this.ids = ids;
	}

	public DataRule getDataRule() {
		return dataRule;
	}

	public void setDataRule(DataRule dataRule) {
		this.dataRule = dataRule;
	}

	public Page<DataRule> getPage() {
		return page;
	}

	public void setPage(Page<DataRule> page) {
		this.page = page;
	}

	public Page<DataTable> getDataRulePage() {
		return dataRulePage;
	}

	public void setDataRulePage(Page<DataTable> dataRulePage) {
		this.dataRulePage = dataRulePage;
	}

	public void setTableId(Long tableId) {
		this.tableId = tableId;
	}

	public Long getTableId() {
		return tableId;
	}

	public Page<TableColumn> getTableColumnPage() {
		return tableColumnPage;
	}

	public void setTableColumnPage(Page<TableColumn> tableColumnPage) {
		this.tableColumnPage = tableColumnPage;
	}

	public String getCurrentInputId() {
		return currentInputId;
	}

	public void setCurrentInputId(String currentInputId) {
		this.currentInputId = currentInputId;
	}

	public Page<Condition> getConditionPage() {
		return conditionPage;
	}

	public void setConditionPage(Page<Condition> conditionPage) {
		this.conditionPage = conditionPage;
	}

	public Long getRuletypeId() {
		return ruletypeId;
	}

	public void setRuletypeId(Long ruletypeId) {
		this.ruletypeId = ruletypeId;
	}

	public Long getDataRuleId() {
		return dataRuleId;
	}

	public void setDataRuleId(Long dataRuleId) {
		this.dataRuleId = dataRuleId;
	}
	public String getDataValue() {
		return dataValue;
	}
	public void setDataValue(String dataValue) {
		this.dataValue = dataValue;
	}
	public List<String[]> getValues() {
		return values;
	}
	public void setValues(List<String[]> values) {
		this.values = values;
	}
	public Long getSysMenuId() {
		return sysMenuId;
	}
	public void setSysMenuId(Long sysMenuId) {
		this.sysMenuId = sysMenuId;
	}
	public boolean isSelectPageFlag() {
		return selectPageFlag;
	}
	public void setSelectPageFlag(boolean selectPageFlag) {
		this.selectPageFlag = selectPageFlag;
	}
	public Long getDataTableId() {
		return dataTableId;
	}
	public void setDataTableId(Long dataTableId) {
		this.dataTableId = dataTableId;
	}
	public String getValueUrl() {
		return valueUrl;
	}
	public void setValueUrl(String valueUrl) {
		this.valueUrl = valueUrl;
	}
	public List<ListView> getListViews() {
		return listViews;
	}
	public void setTableColumnId(Long tableColumnId) {
		this.tableColumnId = tableColumnId;
	}
	public String getStandardField() {
		return standardField;
	}
	public void setStandardField(String standardField) {
		this.standardField = standardField;
	}
	public String getCurrentTreeId() {
		return currentTreeId;
	}
	public void setCurrentTreeId(String currentTreeId) {
		this.currentTreeId = currentTreeId;
	}
	public String getRowId() {
		return rowId;
	}
	public void setRowId(String rowId) {
		this.rowId = rowId;
	}
	public Boolean getDeparmentInheritable() {
		return deparmentInheritable;
	}
	public void setDeparmentInheritable(Boolean deparmentInheritable) {
		this.deparmentInheritable = deparmentInheritable;
	}
	public void setPosition(String position) {
		this.position = position;
	}
	public String getPosition() {
		return position;
	}
	public String getMessageTip() {
		return messageTip;
	}
	public void setPermissionId(Long permissionId) {
		this.permissionId = permissionId;
	}
	public Long getListViewId() {
		return listViewId;
	}
	public void setListViewId(Long listViewId) {
		this.listViewId = listViewId;
	}
	public boolean isHasBranch() {
		return hasBranch;
	}
	public void setHasBranch(boolean hasBranch) {
		this.hasBranch = hasBranch;
	}

}
