package com.norteksoft.acs.web.authority;

import java.util.ArrayList;
import java.util.List;

import org.apache.struts2.convention.annotation.Action;
import org.apache.struts2.convention.annotation.Namespace;
import org.apache.struts2.convention.annotation.ParentPackage;
import org.apache.struts2.convention.annotation.Result;
import org.apache.struts2.convention.annotation.Results;
import org.springframework.beans.factory.annotation.Autowired;

import com.norteksoft.acs.base.enumeration.ConditionValueType;
import com.norteksoft.acs.base.enumeration.DataRange;
import com.norteksoft.acs.base.enumeration.ItemType;
import com.norteksoft.acs.base.enumeration.PermissionAuthorize;
import com.norteksoft.acs.base.utils.PermissionItemTreeUtil;
import com.norteksoft.acs.entity.authority.DataRule;
import com.norteksoft.acs.entity.authority.Permission;
import com.norteksoft.acs.entity.authority.PermissionItem;
import com.norteksoft.acs.entity.authority.PermissionUser;
import com.norteksoft.acs.entity.authorization.Role;
import com.norteksoft.acs.service.authority.DataRuleManager;
import com.norteksoft.acs.service.authority.PermissionItemManager;
import com.norteksoft.acs.service.authority.PermissionManager;
import com.norteksoft.acs.service.authorization.StandardRoleManager;
import com.norteksoft.mms.module.entity.Menu;
import com.norteksoft.mms.module.service.MenuManager;
import com.norteksoft.product.api.ApiFactory;
import com.norteksoft.product.orm.Page;
import com.norteksoft.product.util.ContextUtils;
import com.norteksoft.product.util.JsTreeUtils;
import com.norteksoft.product.util.JsonParser;
import com.norteksoft.product.util.PageUtils;
import com.norteksoft.product.util.tree.TreeAttr;
import com.norteksoft.product.util.tree.TreeNode;
import com.norteksoft.product.util.tree.ZTreeNode;
import com.norteksoft.product.web.struts2.CrudActionSupport;
import com.norteksoft.product.web.struts2.Struts2Utils;

@Namespace("/authority")
@ParentPackage("default")
@Results( { @Result(name = CrudActionSupport.RELOAD, location = "permission", type = "redirectAction") })
public class PermissionAction extends CrudActionSupport<Permission>{

	private static final long serialVersionUID = 1L;
	private Permission permission;
	private Long permissionId;
	private Long id;//主子表中用到
	private String ids;
	private Page<Permission> page=new Page<Permission>(0, true);
	private Page<PermissionItem> itemPage=new Page<PermissionItem>(0, true);
	private Long dataRuleId;
	private DataRule permissionDataRule;
	private List<PermissionAuthorize> docAuthes;
	private String validateAuths;//验证数据授权用到了，以逗号隔开的数字，如：1,2,4
	private Integer permissionPriority;//验证数据授权用到
	private Long sysMenuId;//系统菜单id
	private ItemType itemType;
	private String currentTreeId;
	private String rowId;
	private String permissionCode;//数据授权编码
	private Long dataTableId;//数据表id
	private String dataTableName;//数据表名称
	private DataRange dataRange;//数据范围
	private Boolean deparmentInheritable=true;//子部门是否继承该权限
	private String permissionUsers;//json格式的字符串,用于存储快速授权中人员列表,其格式为{{type:itemType,value:{{conditionName:,conditionValue:},{conditionName:,conditionValue:},...}},{type:itemType,value:{{conditionName:,conditionValue:},{conditionName:,conditionValue:},...}},...}
	private String pointUserValues;//快速授权中具体人json字符串，[{"con1":"bb","con2":"cc"},{"con1":bbb,"con2":"ccc"}]
	private String pointDeptValues;//快速授权中具体部门json字符串，[{"con1":"bb1","con2":"cc1"}]
	private String pointRoleValues;//快速授权中具体角色json字符串，[{"con1":"bb1","con2":"cc1"}]
	private String pointWorkgroupValues;//快速授权中具体工作组json字符串，[{"con1":"bb1","con2":"cc1"}]
	private String pointUserNames;//快速授权中具体人姓名，以逗号隔开
	private String pointDeptNames;//快速授权中具体部门姓名，以逗号隔开
	private String pointRoleNames;//快速授权中具体角色姓名，以逗号隔开
	private String pointWorkgroupNames;//快速授权中具体工作组姓名，以逗号隔开
	
	private Boolean allUser=true;//数据授权中人员是否是所有人,true表示是所有人，false表示不是所有人
	private List<PermissionItem> permissionItems = new ArrayList<PermissionItem>();
	private boolean hasBranch = false;//是否有分支
	
	@Autowired
	private PermissionManager permissionManager;
	@Autowired
	private PermissionItemManager permissionItemManager;
	@Autowired
	private StandardRoleManager standardRoleManager;
	@Autowired
	private DataRuleManager dataRuleManager;
	@Autowired
	private MenuManager menuManager;

	
	private static final String SUCCESS_MESSAGE_LEFT = "<font class=\"onSuccess\"><nobr>";
	private static final String MESSAGE_RIGHT = "</nobr></font>";
	private static final String ERROR_MESSAGE_LEFT = "<font class=\"onError\"><nobr>";
	
	protected void addErrorMessage(String message){
		this.addActionMessage(ERROR_MESSAGE_LEFT+message+MESSAGE_RIGHT);
	}
	protected void addSuccessMessage(String message){
		this.addActionMessage(SUCCESS_MESSAGE_LEFT+message+MESSAGE_RIGHT);
	}
	/**
	 * 删除数据授权
	 */
	@Override
	@Action("permission-delete")
	public String delete() throws Exception {
		permissionManager.deletePermissions(ids);
		addSuccessMessage("删除成功");
		ApiFactory.getBussinessLogService().log("数据授权", "删除数据授权",ContextUtils.getSystemId("acs"));
		return "permission-data";
	}

	/**
	 * 数据授权表单页面
	 */
	@Override
	@Action("permission-input")
	public String input() throws Exception {
		if(permission.getId()==null){
			permission.setCode(createPermissionCode());
			dataTableId=dataRuleManager.getDataRule(dataRuleId).getDataTableId();
		}else{
			dataRuleId=permission.getDataRule().getId();
			dataTableId=permission.getDataRule().getDataTableId();
			permissionItems = permissionItemManager.getItemTypeNotAllUserPermissionItems(permission.getId());
			//设置所有人为默认
			Boolean flag = false;
			for(PermissionItem item:permission.getItems()){
				if(ItemType.ALL_USER.equals(item.getItemType())){
					flag = true;
					break;
				}
			}
			allUser = flag;
		}
		ApiFactory.getBussinessLogService().log("数据授权", "数据授权表单",ContextUtils.getSystemId("acs"));
		return "permission-input";
	}

	
	@Override
	public String list() throws Exception {
		return SUCCESS;
	}
	/**
	 * 数据授权列表
	 * @return
	 * @throws Exception
	 */
	@Action("permission-data")
	public String permissionData() throws Exception {
		hasBranch = ContextUtils.hasBranch();
		List<Menu> menus = menuManager.getEnabledStandardRootMenuByCompany();
		if(sysMenuId==null&&menus.size()>0){
			sysMenuId = menus.get(0).getId();
		}
		if(page.getPageSize()>1){
			if(dataRuleId==null){
				ApiFactory.getBussinessLogService().log("数据授权", "数据授权列表",ContextUtils.getSystemId("acs"));
				permissionManager.getPermissionsByMenuId(page,sysMenuId,false);
			}else{
				ApiFactory.getBussinessLogService().log("数据授权", "数据授权列表",ContextUtils.getSystemId("acs"));
				permissionManager.getPermissionPageByDataRule(page,dataRuleId);
			}
			this.renderText(PageUtils.pageToJson(page));
			return null;
		}
		return SUCCESS;
	}
	
	/**
	 * 数据授权条件项列表
	 * @return
	 * @throws Exception
	 */
	@Action("permission-item-list")
	public String permissionItemList() throws Exception {
		if(itemPage.getPageSize()>1){
			if(id!=null){
				permissionItemManager.getPermissionItems(itemPage, id);
				this.renderText(PageUtils.pageToJson(itemPage));
			}
		}
		return null;
	}

	@Override
	protected void prepareModel() throws Exception {
		if(permissionId==null){
			permission=new Permission();
		}else{
			permission=permissionManager.getPermission(permissionId);
		}
	}

	/**
	 * 保存数据授权
	 */
	@Override
	@Action("permission-save")
	public String save() throws Exception {
		permissionDataRule=dataRuleManager.getDataRule(dataRuleId);
		permission.setDataRule(permissionDataRule);
		permission.setMenuId(permissionDataRule.getMenuId());
		permissionManager.savePermission(permission,docAuthes,allUser);
		permissionId=permission.getId();
		permissionItems = permissionItemManager.getItemTypeNotAllUserPermissionItems(permission.getId());
		dataTableId=permissionDataRule.getDataTableId();
		addSuccessMessage("保存成功");
		ApiFactory.getBussinessLogService().log("数据授权", "保存数据授权",ContextUtils.getSystemId("acs"));
		return "permission-input";
	}
	
	/**
	 * 角色树
	 * @return
	 * @throws Exception
	 */
	@Action("role-tree")
	public String roleTree() throws Exception{
		permissionDataRule=dataRuleManager.getDataRule(dataRuleId);
		StringBuilder tree=new StringBuilder();
		List<Role> roles=standardRoleManager.getAllStandardRole(permissionDataRule.getSystemId());
		if(roles.size()<=0){
			tree.append(JsTreeUtils.generateJsTreeNodeNew("_role", "", "角色", ""));
		}else{
			tree.append(JsTreeUtils.generateJsTreeNodeNew("_role", "open", "角色",roles(roles) ,""));
		}
		this.renderText(tree.toString());
		return null;
	}
	
	private String roles(List<Role> roles){
		StringBuilder tree=new StringBuilder();
		for(Role role:roles){
			tree.append(JsTreeUtils.generateJsTreeNodeNew("role-"+role.getCode()+"-"+role.getName(), "", role.getName(),"")).append(",");
		}
		JsTreeUtils.removeLastComma(tree);
		return tree.toString();
	}
	/**
	 * 删除条件项
	 * @return
	 * @throws Exception
	 */
	@Action("permission-item-delete")
	public String permissionItemDelete() throws Exception {
		permissionItemManager.deletePermissionItem(id);
		String callback=Struts2Utils.getParameter("callback");
		ApiFactory.getBussinessLogService().log("数据授权", "删除数据授权中的条件项",ContextUtils.getSystemId("acs"));
		this.renderText(callback+"({msg:'删除成功'})");
		return null;
	}
	
	/**
	 * 验证该授权的优先级及操作权限
	 * @return
	 * @throws Exception
	 */
	@Action("validate-permission")
	public String validatePermission() throws Exception {
		ApiFactory.getBussinessLogService().log("数据授权", "验证数据授权",ContextUtils.getSystemId("acs"));
		this.renderText(permissionManager.validatePermission(validateAuths,dataRuleId,permissionId,permissionPriority));
		return null;
	}
	
	/**
	 * 树页面
	 */
	@Action("permission-item-tree-page")
	public String permissionItemTreePage(){
		return SUCCESS;
	}
	
	/**
	 * 获取itemType树
	 */
	@Action("permission-item-tree")
	public String getPermissionItemTree(){
		this.renderText(permissionManager.getPermissionItemTree(itemType,currentTreeId));
		return null;
	}
	
	/**
	 * 快速授权页面
	 * @return
	 * @throws Exception
	 */
	@Action("permission-list")
	public String permissionList() throws Exception {
		return "permission-data";
	}
	/**
	 * 快速授权列表
	 * @return
	 * @throws Exception
	 */
	@Action("fast-permission-data")
	public String fastListData() throws Exception {
		List<Menu> menus = menuManager.getEnabledStandardRootMenuByCompany();
		if(sysMenuId==null&&menus.size()>0){
			sysMenuId = menus.get(0).getId();
		}
		if(page.getPageSize()>1){
			ApiFactory.getBussinessLogService().log("快速授权", "快速授权列表",ContextUtils.getSystemId("acs"));
			permissionManager.getPermissionsByMenuId(page,sysMenuId,true);
			this.renderText(PageUtils.pageToJson(page));
			return null;
		}
		return "fast-permission-data";
	}
	
	/**
	 * 删除快速授权
	 */
	@Action("fast-permission-delete")
	public String fastPermissionDelete() throws Exception {
		permissionManager.deleteFastPermissions(ids);
		addSuccessMessage("删除成功");
		ApiFactory.getBussinessLogService().log("数据授权", "删除快速授权",ContextUtils.getSystemId("acs"));
		return "fast-permission-data";
	}
	public void prepareFastPermissionInput() throws Exception {
		prepareModel();
	}
	/**
	 * 快速授权表单页面
	 */
	@Action("fast-permission-input")
	public String fastPermissionInput() throws Exception {
		if(permission.getId()==null)permission.setCode(createPermissionCode());
		if(permission.getId()!=null){
			permissionDataRule = permission.getDataRule();
			//快速授权中人员处理
			dealWithPermissionUsers();
		}
		ApiFactory.getBussinessLogService().log("快速授权", "快速授权表单",ContextUtils.getSystemId("acs"));
		return "fast-permission-input";
	}
	//是否应该加逗号,返回true表示应该加逗号
	private boolean shouldAddComma(String perUsers){
		//因为permissionUsers的默认值为[
		if(!"[".equals(perUsers))return true;
		return false;
	}
	//获得具体人员
	private void getPointPermissionUsers(String itemType,String pointPermissionUserJsons){
		if(shouldAddComma(permissionUsers)){
			permissionUsers+=",{\"itemType\":\""+itemType+"\",\"value\":"+pointPermissionUserJsons+"}";
		}else{
			permissionUsers+="{\"itemType\":\""+itemType+"\",\"value\":"+pointPermissionUserJsons+"}";
		}
	}
	
	private void dealWithPermissionUsers(){
		List<PermissionItem> allUserItems = permissionItemManager.getPermissionItemsByPermission(permission.getId(),ItemType.ALL_USER);
		if(allUserItems.size()>0){//所有人
			permissionUsers = "[{\"itemType\",\"ALL_USER\"}]";
		}else{//不是所有人
			permissionUsers="[";
			//具体人
			List<PermissionItem> pointUserItems = permissionItemManager.getPermissionItemsByPermission(permission.getId(),ItemType.USER);
			if(pointUserItems.size()>0){
				List<String> pointUsernames = permissionItemManager.getPermissionItemConditionNameByItem(pointUserItems.get(0).getId(),ConditionValueType.PERMISSION);
				if(pointUsernames.size()>0)pointUserNames = pointUsernames.toString().replace("[", "").replace("]","").replace(" ", "");
				List<PermissionUser> pointUsers = permissionManager.getPermissionUsers(pointUserItems,ItemType.USER);
				pointUserValues = JsonParser.object2Json(pointUsers);
				getPointPermissionUsers(ItemType.USER.toString(),pointUserValues);
			}
			//具体部门
			List<PermissionItem> pointDeptItems = permissionItemManager.getPermissionItemsByPermission(permission.getId(),ItemType.DEPARTMENT);
			if(pointDeptItems.size()>0){
				List<String> pointdeptnames = permissionItemManager.getPermissionItemConditionNameByItem(pointDeptItems.get(0).getId(),ConditionValueType.PERMISSION);
				if(pointdeptnames.size()>0)pointDeptNames = pointdeptnames.toString().replace("[", "").replace("]","").replace(" ", "");
				List<PermissionUser> pointDepts = permissionManager.getPermissionUsers(pointDeptItems,ItemType.DEPARTMENT);
				pointDeptValues = JsonParser.object2Json(pointDepts);
				getPointPermissionUsers(ItemType.DEPARTMENT.toString(),pointDeptValues);
			}
			//具体角色
			List<PermissionItem> pointRoleItems = permissionItemManager.getPermissionItemsByPermission(permission.getId(),ItemType.ROLE);
			if(pointRoleItems.size()>0){
				List<String> pointRolenames = permissionItemManager.getPermissionItemConditionNameByItem(pointRoleItems.get(0).getId(),ConditionValueType.PERMISSION);
				if(pointRolenames.size()>0)pointRoleNames = pointRolenames.toString().replace("[", "").replace("]","").replace(" ", "");
				List<PermissionUser> pointRoles = permissionManager.getPermissionUsers(pointRoleItems,ItemType.ROLE);
				pointRoleValues = JsonParser.object2Json(pointRoles);
				getPointPermissionUsers(ItemType.ROLE.toString(),pointRoleValues);
			}
			//具体工作组
			List<PermissionItem> pointWgItems = permissionItemManager.getPermissionItemsByPermission(permission.getId(),ItemType.WORKGROUP);
			if(pointWgItems.size()>0){
				List<String> pointWgnames = permissionItemManager.getPermissionItemConditionNameByItem(pointWgItems.get(0).getId(),ConditionValueType.PERMISSION);
				if(pointWgnames.size()>0)pointWorkgroupNames = pointWgnames.toString().replace("[", "").replace("]","").replace(" ", "");
				List<PermissionUser> pointWgs = permissionManager.getPermissionUsers(pointWgItems,ItemType.WORKGROUP);
				pointWorkgroupValues = JsonParser.object2Json(pointWgs);
				getPointPermissionUsers(ItemType.WORKGROUP.toString(),pointWorkgroupValues);
			}
			permissionUsers+="]";
		}
	}
	private String createPermissionCode(){
		long num=0;
		List<Permission> permissions=permissionManager.getDefaultCodePermissions();
		if(permissions != null && permissions.size()>0){
			for(Permission p:permissions){
				String codeNum=p.getCode().replace("dataAuth-", "");
				if(codeNum.matches("^-?\\d+$")&&Long.valueOf(codeNum)>num){
					num=Long.valueOf(codeNum);
				}
			}
		}else{
			return "dataAuth-1";
		}
		return "dataAuth-"+(num+1);
	}
	
	public void prepareFastPermissionSave() throws Exception {
		prepareModel();
	}
	/**
	 * 保存快速授权
	 */
	@Action("fast-permission-save")
	public String fastPermissionSave() throws Exception {
		permission.setMenuId(sysMenuId);
		permissionManager.saveFastPermission(permission,docAuthes,dataTableId,dataTableName,dataRange,deparmentInheritable,permissionUsers);
		
		permissionId=permission.getId();
		permissionDataRule = permission.getDataRule();
		//快速授权中人员处理
		dealWithPermissionUsers();
		
		addSuccessMessage("保存成功");
		ApiFactory.getBussinessLogService().log("快速授权", "保存快速授权",ContextUtils.getSystemId("acs"));
		return "fast-permission-input";
	}
	
	@Action("validate-permission-code")
	public String validateWidget() throws Exception{
		boolean isExist=permissionManager.isPermissionCodeExist(permissionCode,permissionId);
		if(isExist){//存在
			this.renderText("true");
		}else{
			this.renderText("false");
		}
		return null;
	}
	/**
	 * 快速授权中选择具体角色树
	 * @return
	 */
	@Action("select-role-tree")
	public String selectRoleTree(){
		this.renderText(PermissionItemTreeUtil.getSystemRoleTree());
		return null;
	}
	
	/**
	 * 数据授权中左侧的系统分类树
	 * @return
	 */
	@Action("system-data-rule-tree")
	public String systemDataRuleTree(){
		List<ZTreeNode> treeNodes = new ArrayList<ZTreeNode>();
		List<Menu> menus = menuManager.getEnabledStandardRootMenuByCompany();
		String result ="";
		for(Menu menu:menus){
			List<DataRule> dataRules = dataRuleManager.getDataRulesByMenuId(menu.getId());
			ZTreeNode root = new ZTreeNode("menuId_"+menu.getId(),"0",menu.getName(), "false", "false", "", "", "folder", "");
			treeNodes.add(root);
			dataRuleTree(dataRules,treeNodes,"menuId_"+menu.getId());
		}
		result = JsonParser.object2Json(treeNodes);
		renderText(result);
		return null;
	}
	
	private void dataRuleTree(List<DataRule> dataRules,List<ZTreeNode> treeNodes,String parentId) {
		for (DataRule rule : dataRules) {
			ZTreeNode root = new ZTreeNode("dataRuleId_"+rule.getId(),parentId,rule.getName(), "false", "false", "", "", "folder", "");
			treeNodes.add(root);
		}
	}
	
	/**
	 * 数据授权中人员查看
	 * @return
	 */
	@Action("permission-viewCondition")
	public String viewCondition(){
		
		return "permission-viewCondition";
	}
	
	public Permission getModel() {
		return permission;
	}

	public Permission getPermission() {
		return permission;
	}

	public void setPermission(Permission permission) {
		this.permission = permission;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Page<Permission> getPage() {
		return page;
	}

	public void setIds(String ids) {
		this.ids = ids;
	}


	public DataRule getPermissionDataRule() {
		return permissionDataRule;
	}

	public Long getPermissionId() {
		return permissionId;
	}

	public void setPermissionId(Long permissionId) {
		this.permissionId = permissionId;
	}

	public Page<PermissionItem> getItemPage() {
		return itemPage;
	}

	public void setPage(Page<Permission> page) {
		this.page = page;
	}

	public void setItemPage(Page<PermissionItem> itemPage) {
		this.itemPage = itemPage;
	}

	public List<PermissionAuthorize> getDocAuthes() {
		return docAuthes;
	}

	public void setDocAuthes(List<PermissionAuthorize> docAuthes) {
		this.docAuthes = docAuthes;
	}

	public Long getDataRuleId() {
		return dataRuleId;
	}

	public void setDataRuleId(Long dataRuleId) {
		this.dataRuleId = dataRuleId;
	}
	public void setValidateAuths(String validateAuths) {
		this.validateAuths = validateAuths;
	}
	public void setPermissionPriority(Integer permissionPriority) {
		this.permissionPriority = permissionPriority;
	}
	public Long getSysMenuId() {
		return sysMenuId;
	}
	public void setSysMenuId(Long sysMenuId) {
		this.sysMenuId = sysMenuId;
	}
	public ItemType getItemType() {
		return itemType;
	}
	public void setItemType(ItemType itemType) {
		this.itemType = itemType;
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
	public void setPermissionCode(String permissionCode) {
		this.permissionCode = permissionCode;
	}
	public void setDataTableId(Long dataTableId) {
		this.dataTableId = dataTableId;
	}
	public void setDataTableName(String dataTableName) {
		this.dataTableName = dataTableName;
	}
	public void setDataRange(DataRange dataRange) {
		this.dataRange = dataRange;
	}
	public void setDeparmentInheritable(Boolean deparmentInheritable) {
		this.deparmentInheritable = deparmentInheritable;
	}
	public void setPermissionUsers(String permissionUsers) {
		this.permissionUsers = permissionUsers;
	}
	public String getPermissionUsers() {
		return permissionUsers;
	}
	public String getPointUserNames() {
		return pointUserNames;
	}
	public String getPointDeptNames() {
		return pointDeptNames;
	}
	public String getPointRoleNames() {
		return pointRoleNames;
	}
	public String getPointWorkgroupNames() {
		return pointWorkgroupNames;
	}
	public String getPointUserValues() {
		return pointUserValues;
	}
	public String getPointDeptValues() {
		return pointDeptValues;
	}
	public String getPointRoleValues() {
		return pointRoleValues;
	}
	public String getPointWorkgroupValues() {
		return pointWorkgroupValues;
	}
	public Long getDataTableId() {
		return dataTableId;
	}
	public Boolean getAllUser() {
		return allUser;
	}
	public void setAllUser(Boolean allUser) {
		this.allUser = allUser;
	}
	public List<PermissionItem> getPermissionItems() {
		return permissionItems;
	}
	public boolean isHasBranch() {
		return hasBranch;
	}
	public void setHasBranch(boolean hasBranch) {
		this.hasBranch = hasBranch;
	}

}
