package com.norteksoft.acs.base.utils;

import java.util.ArrayList;
import java.util.List;

import com.norteksoft.acs.base.enumeration.ConditionType;
import com.norteksoft.acs.entity.authorization.Role;
import com.norteksoft.acs.service.authorization.StandardRoleManager;
import com.norteksoft.mms.module.entity.Menu;
import com.norteksoft.mms.module.service.MenuManager;
import com.norteksoft.product.util.ContextUtils;
import com.norteksoft.product.util.JsonParser;
import com.norteksoft.product.util.ZTreeUtils;
import com.norteksoft.product.util.tree.TreeNode;
import com.norteksoft.product.util.tree.ZTreeNode;
import com.norteksoft.product.web.struts2.Struts2Utils;


/**
 * 数据授权获取ItemType树
 * @author Administrator
 *
 */
public class PermissionItemTreeUtil {

	/**
	 * 获得系统角色树
	 * @return
	 */
	public static String getSystemRoleTree(){
		List<ZTreeNode> treeNodes = new ArrayList<ZTreeNode>();
		
		MenuManager menuManager = (MenuManager)ContextUtils.getBean("menuManager");
		boolean isHasBranch = ContextUtils.hasBranch();//当前租户内是否有分支机构
		StandardRoleManager standardRoleManager = (StandardRoleManager)ContextUtils.getBean("standardRoleManager");
		List<Menu> menus = menuManager.getEnabledStandardRootMenuByCompany();
		for(Menu menu:menus){
			ZTreeNode root = new ZTreeNode("systemId~~"+menu.getSystemId()+"~~"+menu.getName(),"0",menu.getName(), "false", "false", "", "", "folder", "");
			treeNodes.add(root);
			
			List<Role> roles=standardRoleManager.getAllStandardRole(menu.getSystemId());
			roleSystemTree(roles,isHasBranch,treeNodes,"systemId~~"+menu.getSystemId()+"~~"+menu.getName(),"~~");
		}
		return JsonParser.object2Json(treeNodes);
	}
	
	/**
	 * 数据分类中获取用户+标准值树
	 * 创建人时的树：人员树+当前用户+直属上级+直属下级
	 * @param usersList
	 * @param departments
	 * @return
	 */
	public static String getCreatorTree(String currentTreeId) {
		//******************标准值树*******************************
		List<ZTreeNode> treeNodes = new ArrayList<ZTreeNode>();
		if(currentTreeId.equals("0")){//只是初始化时才加载标准值树,点击部门或分支机构节点时不加载该标准值树
			
			ZTreeNode root = new ZTreeNode("standard~~all","0","标准值", "true", "false", "", "", "department", "");
			treeNodes.add(root);
			String standarName = null;
			standarName = Struts2Utils.getText(ConditionType.CURRENT_USER_ID.getCode());
			
			root = new ZTreeNode("standard~~"+ConditionType.CURRENT_USER_ID+"=="+standarName,"standard~~all",standarName, "false", "false", "", "", "department", "");
			treeNodes.add(root);
			
			standarName = Struts2Utils.getText(ConditionType.CURRENT_USER_DIRECT_SUPERIOR_ID.getCode());
			root = new ZTreeNode("standard~~"+ConditionType.CURRENT_USER_DIRECT_SUPERIOR_ID+"=="+standarName,"standard~~all",standarName, "false", "false", "", "", "department", "");
			treeNodes.add(root);
			
			standarName = Struts2Utils.getText(ConditionType.CURRENT_USER_DIRECT_LOWER_ID.getCode());
			root = new ZTreeNode("standard~~"+ConditionType.CURRENT_USER_DIRECT_LOWER_ID+"=="+standarName,"standard~~all",standarName, "false", "false", "", "", "department", "");
			treeNodes.add(root);
			
		}
		
		return JsonParser.object2Json(treeNodes);
	}
	
	/**
	 * 数据分类中获取部门+标准值树
	 * 创建人部门时的树：部门树+当前用户部门+上级部门+顶级部门+下级部门+下级部门（继承）+直属上级的部门+直属下级的部门
	 * @param columns
	 * @param currentTreeId
	 * @param showStandardField 
	 * @return
	 */
	public static String getDepartmentTree( String currentTreeId) {
		//******************标准值树*******************************
		List<ZTreeNode> treeNodes = new ArrayList<ZTreeNode>();
		if(currentTreeId.equals("0")){//只是初始化时才加载标准值树,点击部门或分支机构节点时不加载该标准值树
			ZTreeNode root = new ZTreeNode("standard~~all","0","标准值", "true", "false", "", "", "department", "");
			treeNodes.add(root);
			
			String standarName = null;
			standarName = Struts2Utils.getText(ConditionType.CURRENT_USER_DEPARTMENT.getCode());
			root = new ZTreeNode("standard~~"+ConditionType.CURRENT_USER_DEPARTMENT+"=="+standarName,"standard~~all",standarName, "false", "false", "", "", "department", "");
			treeNodes.add(root);
			
			standarName = Struts2Utils.getText(ConditionType.CURRENT_USER_SUPERIOR_DEPARTMENT.getCode());
			root = new ZTreeNode("standard~~"+ConditionType.CURRENT_USER_SUPERIOR_DEPARTMENT+"=="+standarName,"standard~~all",standarName, "false", "false", "", "", "department", "");
			treeNodes.add(root);
			
			standarName = Struts2Utils.getText(ConditionType.CURRENT_USER_TOP_DEPARTMENT.getCode());
			root = new ZTreeNode("standard~~"+ConditionType.CURRENT_USER_TOP_DEPARTMENT+"=="+standarName,"standard~~all",standarName, "false", "false", "", "", "department", "");
			treeNodes.add(root);
			standarName = Struts2Utils.getText(ConditionType.CURRENT_USER_CHILD_DEPARTMENT.getCode());
			root = new ZTreeNode("standard~~"+ConditionType.CURRENT_USER_CHILD_DEPARTMENT+"=="+standarName,"standard~~all",standarName, "false", "false", "", "", "department", "");
			treeNodes.add(root);
			standarName = Struts2Utils.getText(ConditionType.CURRENT_USER_CHILDREN_DEPARTMENT.getCode());
			root = new ZTreeNode("standard~~"+ConditionType.CURRENT_USER_CHILDREN_DEPARTMENT+"=="+standarName,"standard~~all",standarName, "false", "false", "", "", "department", "");
			treeNodes.add(root);
			standarName = Struts2Utils.getText(ConditionType.CURRENT_USER_DIRECT_SUPERIOR_DEPARTMENT.getCode());
			root = new ZTreeNode("standard~~"+ConditionType.CURRENT_USER_DIRECT_SUPERIOR_DEPARTMENT+"=="+standarName,"standard~~all",standarName, "false", "false", "", "", "department", "");
			treeNodes.add(root);
			standarName = Struts2Utils.getText(ConditionType.CURRENT_USER_DIRECT_LOWER_DEPARTMENT.getCode());
			root = new ZTreeNode("standard~~"+ConditionType.CURRENT_USER_DIRECT_LOWER_DEPARTMENT+"=="+standarName,"standard~~all",standarName, "false", "false", "", "", "department", "");
			treeNodes.add(root);
		}
		return JsonParser.object2Json(treeNodes);
	}
	
	/**
	 * 数据分类中获取角色+标准值树
	 *  创建人的角色时的树：创建人拥有权限的角色树+当前用户角色+直属上级角色+直属下级角色
	 * @return
	 */
	public static String getRoleTree(){
		//******************标准值树*******************************
		List<ZTreeNode> treeNodes = new ArrayList<ZTreeNode>();
		
		ZTreeNode root = new ZTreeNode("standard~~all","0","标准值", "true", "false", "", "", "folder", "");
		treeNodes.add(root);
		String standarName = null;
		standarName = Struts2Utils.getText(ConditionType.CURRENT_USER_ROLE.getCode());
		root = new ZTreeNode("standard~~"+ConditionType.CURRENT_USER_ROLE+"=="+standarName,"standard~~all",standarName, "false", "false", "", "", "folder", "");
		treeNodes.add(root);
		standarName = Struts2Utils.getText(ConditionType.CURRENT_USER_DIRECT_SUPERIOR_ROLE.getCode());
		root = new ZTreeNode("standard~~"+ConditionType.CURRENT_USER_DIRECT_SUPERIOR_ROLE+"=="+standarName,"standard~~all",standarName, "false", "false", "", "", "folder", "");
		treeNodes.add(root);
		standarName = Struts2Utils.getText(ConditionType.CURRENT_USER_DIRECT_LOWER_ROLE.getCode());
		root = new ZTreeNode("standard~~"+ConditionType.CURRENT_USER_DIRECT_LOWER_ROLE+"=="+standarName,"standard~~all",standarName, "false", "false", "", "", "folder", "");
		treeNodes.add(root);
		
		//******************角色树****************************
		boolean isHasBranch = ContextUtils.hasBranch();//当前租户内是否有分支机构
		MenuManager menuManager = (MenuManager)ContextUtils.getBean("menuManager");
		StandardRoleManager standardRoleManager = (StandardRoleManager)ContextUtils.getBean("standardRoleManager");
		root = new ZTreeNode("allRole","0","角色", "false", "false", "", "", "folder", "");
		treeNodes.add(root);
		List<Menu> menus = menuManager.getEnabledStandardRootMenuByCompany();
		for(Menu menu:menus){
			root = new ZTreeNode("systemId~~"+menu.getSystemId()+"~~"+menu.getName(),"allRole",menu.getName(), "false", "false", "", "", "folder", "");
			treeNodes.add(root);
			List<Role> roles=standardRoleManager.getAllStandardRole(menu.getSystemId());
			roleSystemTree(roles,isHasBranch,treeNodes,"systemId~~"+menu.getSystemId()+"~~"+menu.getName(),null);
		}
		return JsonParser.object2Json(treeNodes);
	}
	
	private static List<TreeNode> roleSystemTree(List<Role> roles,boolean isHasBranch,List<ZTreeNode> treeNodes,String parentId,String operatorFlag) {
		if(operatorFlag==null)operatorFlag = "_";
		List<TreeNode> roleTreeNodes = new ArrayList<TreeNode>();
		TreeNode rolesTreeContent = null;
		for (Role role : roles) {
			String nodeId = "";
			String nodeName = "";
			if(isHasBranch){
				nodeId = "role"+operatorFlag+role.getId()+"~~"+role.getName()+"("+role.getBusinessSystem().getName()+"/"+role.getSubCompanyName()+")";
				nodeName = role.getName()+"("+role.getSubCompanyName()+")";
			}else{
				nodeId = "role"+operatorFlag+role.getId()+"~~"+role.getName()+"("+role.getBusinessSystem().getName()+")";
				nodeName = role.getName();
			}
			ZTreeNode root = new ZTreeNode(nodeId,parentId
					,nodeName, "false", "false", "", "", "folder", "");
			treeNodes.add(root);
			roleTreeNodes.add(rolesTreeContent);
		}
		return roleTreeNodes;
	}
	
	/**
	 * 数据分类中获取工作组+标准值树
	 * 创建人的工作组时的树：工作组树+当前用户工作组+直属上级的工作组+直属下级的工作组 
	 * @param columns
	 * @param currentTreeId
	 * @return
	 */
	public static String getWorkgroupTree(String currentTreeId) {
		List<ZTreeNode> treeNodes = new ArrayList<ZTreeNode>();
		
		//******************标准值树*******************************
		ZTreeNode root = new ZTreeNode("standard~~all","0","标准值", "true", "false", "", "", "department", "");
		treeNodes.add(root);
		
		String standarName = null;
		standarName = Struts2Utils.getText(ConditionType.CURRENT_USER_WORKGROUP.getCode());
		root = new ZTreeNode("standard~~"+ConditionType.CURRENT_USER_WORKGROUP+"=="+standarName,"standard~~all",standarName, "false", "false", "", "", "department", "");
		treeNodes.add(root);
		standarName = Struts2Utils.getText(ConditionType.CURRENT_USER_DIRECT_SUPERIOR_WORKGROUP.getCode());
		root = new ZTreeNode("standard~~"+ConditionType.CURRENT_USER_DIRECT_SUPERIOR_WORKGROUP+"=="+standarName,"standard~~all",standarName, "false", "false", "", "", "department", "");
		treeNodes.add(root);
		standarName = Struts2Utils.getText(ConditionType.CURRENT_USER_DIRECT_LOWER_WORKGROUP.getCode());
		root = new ZTreeNode("standard~~"+ConditionType.CURRENT_USER_DIRECT_LOWER_WORKGROUP+"=="+standarName,"standard~~all",standarName, "false", "false", "", "", "department", "");
		treeNodes.add(root);
		
		return JsonParser.object2Json(treeNodes);
	}

}
