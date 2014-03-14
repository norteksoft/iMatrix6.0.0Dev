package com.norteksoft.product.util;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;

import com.norteksoft.product.api.entity.Department;
import com.norteksoft.product.api.entity.Workgroup;
import com.norteksoft.acs.web.authorization.JsTreeUtil1;
import com.norteksoft.product.api.ApiFactory;
import com.norteksoft.product.api.entity.User;
import com.norteksoft.product.util.tree.TreeAttr;
import com.norteksoft.product.util.tree.TreeNode;
import com.norteksoft.tags.tree.DepartmentDisplayType;

public class TreeUtils{
	private static String DEPARTMENT="department";
	private static String WORKGROUP="workGroup";
	private static String NOTINDEPARTMENT="notInDepartment";
	//"_"--->"~~"
	private static String SPLIT_ONE="~~";
	//"="--->"=="
	private static String SPLIT_TWO="==";
	//"-"--->"*#"
	private static String SPLIT_THREE="*#";
	//"|"--->"|#"
	private static String SPLIT_FOUR="|#";
	//"+"--->"+#"
	private static String SPLIT_FIVE="+#";
	//"~"--->"~#"
	private static String SPLIT_SIX="~#";
	//"*"--->"**"
	private static String SPLIT_SEVEN="**";
	//","--->"=#"
	private static String SPLIT_EIGHT="=#";
	//"~*"
	private static String SPLIT_NINE="~*";
	//"~+"
	private static String SPLIT_TEN="~+";
	//几种节点
	//"user","department","workGroup","allDepartment"(部门)
	//"NODEPARTMENT"(总公司无部门),"company","NOBRANCH"(分支机构无部门)
	//"branch"（分支机构根节点）,"allWorkGroup"（工作组）
	/**
	 * 部门和工作组人员树
	 * @param onlineVisible 
	 */
	public static String getCreateManDepartmentGroupTree(Long companyId,String currentId, boolean onlineVisible,DepartmentDisplayType departmentDisplayType,boolean userWithoutDeptVisible,String branchIds) {
		StringBuilder tree = new StringBuilder();
		//查处公司所有顶层部门
		List<Department> departments = ApiFactory.getAcsService().getDepartments();
		String[] str = currentId.split(SPLIT_ONE);
		if (currentId.equals("0")) {
			if(StringUtils.isNotEmpty(branchIds)&&!("null".equals(branchIds))&&!("ADMIN".equals(branchIds))){
				//多个分支机构树
				tree.append(getBranchCompanyTrees(onlineVisible,departmentDisplayType,userWithoutDeptVisible,branchIds));
			}else{	
			    tree.append(defaultTreeTwo(departments,companyId,onlineVisible,departmentDisplayType,userWithoutDeptVisible));
			}
		}else if(str[0].equals("department")) {
			tree.append(departmentTreeChange(Long.parseLong(str[1].substring(0,str[1].indexOf("="))),onlineVisible,userWithoutDeptVisible));
		}else if(str[0].equals("workGroup")){
			tree.append(workGroupTree(Long.parseLong(str[1].substring(0,str[1].indexOf("="))),onlineVisible));
		}else if(str[0].equals("NOBRANCH")){
			tree.append(usersNotInBranch(Long.parseLong(str[1].substring(0,str[1].indexOf("~*")))));
		}else if(str[0].equals("NODEPARTMENT")){
			tree.append(usersNotInDepartment());
		}
		return tree.toString();
	}
	private static String defaultTreeTwo(List<Department> departments,Long companyId,boolean onlineVisible,DepartmentDisplayType departmentDisplayType,boolean userWithoutDeptVisible){
		List<TreeNode> treeNodes = new ArrayList<TreeNode>();
		
		TreeNode root = new TreeNode(
		new TreeAttr("company"+SPLIT_ONE+"company"+SPLIT_TWO+"全公司"+SPLIT_THREE+"全公司","company"), 
		"open", 
		ContextUtils.getCompanyName());
		
		List<TreeNode> headNode = new ArrayList<TreeNode>();
		
		//封装部门树节点
		TreeNode headDepartmentTreeContent = null;
		if(departments.size()>0){
			headDepartmentTreeContent = new TreeNode(
			new TreeAttr("allDepartment"+SPLIT_ONE+"allDepartment"+SPLIT_TWO+"所有部门"+SPLIT_THREE+"部门","folder"), 	
			"open",
			"部门");
		}else{
			headDepartmentTreeContent = new TreeNode(
			new TreeAttr("allDepartment"+SPLIT_ONE+"allDepartment"+SPLIT_TWO+"所有部门"+SPLIT_THREE+"部门","folder"), 	
			"",
			"部门");
		}
		//封装部门子节点
		
		//如果显示无部门人员，则封装子节点
		if(userWithoutDeptVisible){
			//封装子节点
			List<TreeNode> childrenTreeNode = new ArrayList<TreeNode>();
			childrenTreeNode.addAll(departmentsTree(departments,departmentDisplayType));
			//封装无部门人员节点
			TreeNode noDepartmentUserTreeContent = new TreeNode(
					new TreeAttr("NODEPARTMENT"+SPLIT_ONE+"NODEPARTMENT"+SPLIT_TWO,"folder"), 	
					"closed",
			"无部门人员");
			//noDepartmentUserTreeContent.setChildren(usersNotInDepartment());
			childrenTreeNode.add(noDepartmentUserTreeContent);
			headDepartmentTreeContent.setChildren(childrenTreeNode);
		}else{
			headDepartmentTreeContent.setChildren(departmentsTree(departments,departmentDisplayType));
		}
		headNode.add(headDepartmentTreeContent);
		
		//封装工作组树节点
		TreeNode headWorkGroupTreeContent = null;
		List<Workgroup> workGroups = ApiFactory.getAcsService().getAllWorkgroups();
		if(workGroups.size()>0){
			headWorkGroupTreeContent = new TreeNode(
			new TreeAttr("allWorkGroup"+SPLIT_ONE+"allWorkGroup"+SPLIT_TWO+"工作组"+SPLIT_THREE+"工作组","folder"), 	
			"open",
			"工作组");
			
		}else{
			headWorkGroupTreeContent = new TreeNode(
			new TreeAttr("allWorkGroup"+SPLIT_ONE+"allWorkGroup"+SPLIT_TWO+"工作组"+SPLIT_THREE+"工作组","folder"), 	
			"",
			"工作组");
		}
		//封装工作组子节点
		headWorkGroupTreeContent.setChildren(workGroupsTree(workGroups,onlineVisible,true));
		headNode.add(headWorkGroupTreeContent);
		root.setChildren(headNode);
		treeNodes.add(root);
		
		return JsonParser.object2Json(treeNodes);
	}
	
	/**
	 * 部门人员树
	 * @param onlineVisible 
	 */
	
	public static String getCreateManDepartmentTree
	       (Long companyId,String currentId, 
			boolean onlineVisible,DepartmentDisplayType departmentDisplayType,
			boolean userWithoutDeptVisible,String branchIds) {
		StringBuilder tree = new StringBuilder();
		List<Department> departments = ApiFactory.getAcsService().getDepartments();
		String[] str = currentId.split(SPLIT_ONE);
		if (currentId.equals("0")) {//初始化部门人员树
			if(StringUtils.isNotEmpty(branchIds)&&!("null".equals(branchIds))&&!("ADMIN".equals(branchIds))){
			   //多个分支机构树
			   tree.append(branchsTree(branchIds,departmentDisplayType,userWithoutDeptVisible));	
			}else{
			   tree.append(defaultTreeThree(departments,companyId,departmentDisplayType,userWithoutDeptVisible));
			}
		}else if(str[0].equals("department")) {//点击部门或分支机构
			tree.append(departmentTreeChange(Long.parseLong(str[1].substring(0,str[1].indexOf("="))),onlineVisible,userWithoutDeptVisible));
		}else if(str[0].equals("NOBRANCH")){
			tree.append(usersNotInBranch(Long.parseLong(str[1].substring(0,str[1].indexOf("~*")))));
		}else if(str[0].equals("NODEPARTMENT")){
			tree.append(usersNotInDepartment());
		}
		return tree.toString();
	}
	public static String getCreateManDepartmentTreeIncludeDeleted(Long companyId,String currentId,boolean onlineVisible,DepartmentDisplayType departmentDisplayType,boolean userWithoutDeptVisible) {
		StringBuilder tree = new StringBuilder();
		List<Department> departments = ApiFactory.getAcsService().getDepartments();
		String[] str = currentId.split(SPLIT_ONE);
		if (currentId.equals("0")) {
			tree.append(defaultTreeThree(departments,companyId,departmentDisplayType,userWithoutDeptVisible));
		}else if(str[0].equals("department")) {
			tree.append(departmentTreeChange(Long.parseLong(str[1].substring(0,str[1].indexOf("="))),onlineVisible,userWithoutDeptVisible));
		}
		return tree.toString();
	}
	private static String departmentTreeChange(Long departmentId,boolean onlineVisible,boolean userWithoutDeptVisible){
		List<Department> childer = ApiFactory.getAcsService().getSubDepartmentList(departmentId);
		List<User> users = ApiFactory.getAcsService().getUsersByDepartmentId(departmentId);
		Department department=ApiFactory.getAcsService().getDepartmentById(departmentId);
	    List<TreeNode> treeNodes = new ArrayList<TreeNode>();
	    boolean isBranch = department.getBranch();
	    boolean isHasBranch = ContextUtils.hasBranch();
	    //加载此部门下用户
	    if(!isBranch){
	    	List<TreeNode> userTreeNode = usersTree(users,department.getId().toString(),department.getName(),DEPARTMENT,onlineVisible,isHasBranch);
	    	treeNodes.addAll(userTreeNode);
	    }
	    
	    //加载此部门下的子部门
	    List<TreeNode> childTreeNode = loadChildDepartmentTreeNode(childer,onlineVisible,userWithoutDeptVisible);
	    treeNodes.addAll(childTreeNode);
	    
	    //判断是否是分支机构，如果是并且显示无部门节点则拼无部门节点
	    List<User> usersList = ApiFactory.getAcsService().getUsersWithoutBranch(department.getId());
	    if(isBranch&&userWithoutDeptVisible&&usersList.size()>0){
	    	//封装分支机构的无部门人员节点
	    	TreeNode noDepartmentUserTreeContent = new TreeNode(
					new TreeAttr("NOBRANCH"+SPLIT_ONE+department.getId()+SPLIT_NINE+department.getName(),"folder"), 	
					"closed",
			"无部门人员");
			noDepartmentUserTreeContent.setChildren(usersNotInBranch(usersList));
			treeNodes.add(noDepartmentUserTreeContent);
	    }
	    
		return JsonParser.object2Json(treeNodes);
	}
	
	private static List<TreeNode> loadChildDepartmentTreeNode(List<Department> children,boolean onlineVisible,boolean userWithoutDeptVisible){
		List<TreeNode> childTreeNodes = new ArrayList<TreeNode>();
		TreeNode childTreeContent = null;
		
		for (Department department : children) {
			childTreeContent = new TreeNode(new TreeAttr("department"+SPLIT_ONE+ department.getId()+SPLIT_TWO+department.getName()+SPLIT_THREE+department.getSubCompanyName()+SPLIT_FOUR+department.getBranch()+SPLIT_FIVE+department.getSubCompanyId(),department.getBranch()?"company":"folder") 	
				,getDepartmentTreeNodeStatus(department)
				,department.getName());
			childTreeNodes.add(childTreeContent);
		}
		return childTreeNodes;
	}
	
	private static String getDepartmentTreeNodeStatus(Department department){
		String folderStatus="closed";//文件夹图标的展开折叠状态默认为折叠
		long userCount = ApiFactory.getAcsService().getUserCountInDepartment(department);
		long childDepartmentCount=ApiFactory.getAcsService().getChildDepartmentCount(department);
		if(userCount==0 && childDepartmentCount==0){
			folderStatus="";//空的部门不需要展开折叠图标
		}
		return folderStatus;
	}
	
	private static String branchsTree(String branchIds,DepartmentDisplayType departmentDisplayType,boolean userWithoutDeptVisible){
		List<TreeNode> branchTreeNodes = new ArrayList<TreeNode>();
		String[] ids = branchIds.split(",");
		for(int j=0;j<ids.length;j++ ){
		    branchTreeNodes.add(getBranchTree(Long.valueOf(ids[j]),departmentDisplayType,userWithoutDeptVisible,false));
		}
		return JsonParser.object2Json(branchTreeNodes);
	}
   
	//拼一棵分支机构树
	private static TreeNode getBranchTree(Long departmentId,DepartmentDisplayType departmentDisplayType,boolean userWithoutDeptVisible,boolean isClosed){
		List<Department> childer = ApiFactory.getAcsService().getSubDepartmentList(departmentId);
		Department department=ApiFactory.getAcsService().getDepartmentById(departmentId);
		
		 TreeNode branchTreeContent = new TreeNode(
					new TreeAttr("branch"+SPLIT_ONE+ department.getId()+SPLIT_TWO+department.getName()+SPLIT_THREE+department.getName()+SPLIT_FOUR+department.getBranch(),department.getBranch()?"company":"folder"), 	
					isClosed?"closed":"open",
					department.getName());
			
			List<TreeNode> treeNodes = new ArrayList<TreeNode>();
		    
		    //加载此分支机构下的第一级部门
		    List<TreeNode> childTreeNode = departmentsTree(childer,departmentDisplayType);
		    treeNodes.addAll(childTreeNode);
		    
		    //判断是否是分支机构，如果是并且显示无部门节点则拼无部门节点
		    List<User> usersList = ApiFactory.getAcsService().getUsersWithoutBranch(department.getId());
		    if(department.getBranch()&&userWithoutDeptVisible&&usersList.size()>0){
		    	//封装分支机构的无部门人员节点
		    	TreeNode noDepartmentUserTreeContent = new TreeNode(
						new TreeAttr("NOBRANCH"+SPLIT_ONE+department.getId()+SPLIT_NINE+department.getName(),"folder"), 	
						"closed",
				"无部门人员");
				treeNodes.add(noDepartmentUserTreeContent);
		    }
		    
		    branchTreeContent.setChildren(treeNodes);
		    return branchTreeContent;
	}
	
	private static String defaultTreeThree(List<Department> departments,Long companyId,DepartmentDisplayType departmentDisplayType,boolean userWithoutDeptVisible){
		List<TreeNode> treeNodes = new ArrayList<TreeNode>();
		TreeNode root = new TreeNode(
		new TreeAttr("company"+SPLIT_ONE+"company"+SPLIT_TWO+"全公司"+SPLIT_THREE+"全公司","company"), 
		"open", 
		ContextUtils.getCompanyName());
		
		List<TreeNode> headNode = new ArrayList<TreeNode>();
		TreeNode headTreeContent = null;
		if(departments.size()>0){
			
			headTreeContent = new TreeNode(
			new TreeAttr("allDepartment"+SPLIT_ONE+"allDepartment"+SPLIT_TWO+"所有部门"+SPLIT_THREE+"部门","folder"), 	
			"open",
			"部门");
			
		}else{
			
			headTreeContent = new TreeNode(
			new TreeAttr("allDepartment"+SPLIT_ONE+"allDepartment"+SPLIT_TWO+"所有部门"+SPLIT_THREE+"部门","folder"), 	
			"",
			"部门");
			
		}
		//如果显示无部门人员，则封装子节点
		if(userWithoutDeptVisible){
			//封装子节点
			List<TreeNode> childrenTreeNode = new ArrayList<TreeNode>();
			childrenTreeNode.addAll(departmentsTree(departments,departmentDisplayType));
			//封装无部门人员节点
			TreeNode noDepartmentUserTreeContent = new TreeNode(
					new TreeAttr("NODEPARTMENT"+SPLIT_ONE+"NODEPARTMENT"+SPLIT_TWO,"folder"), 	
					"closed",
			"无部门人员");
			//noDepartmentUserTreeContent.setChildren(usersNotInDepartment());
			childrenTreeNode.add(noDepartmentUserTreeContent);
			headTreeContent.setChildren(childrenTreeNode);
		}else{
			headTreeContent.setChildren(departmentsTree(departments,departmentDisplayType));
		}
		headNode.add(headTreeContent);
		root.setChildren(headNode);
		treeNodes.add(root);
		return JsonParser.object2Json(treeNodes);
	}
	private static String defaultTreeThreeIncludeDeleted(List<Department> departments,Long companyId,DepartmentDisplayType departmentDisplayType){
		
        List<TreeNode> treeNodes = new ArrayList<TreeNode>();
		
		TreeNode root = new TreeNode(
		new TreeAttr("company"+SPLIT_ONE+"company"+SPLIT_TWO+"全公司"+SPLIT_THREE+"全公司","company"), 
		"open", 
		ContextUtils.getCompanyName());
		
		List<TreeNode> headNode = new ArrayList<TreeNode>();
		TreeNode headTreeContent = null;
		if(departments.size()>0){
		    
		    headTreeContent = new TreeNode(
			new TreeAttr("allDepartment"+SPLIT_ONE+"allDepartment"+SPLIT_TWO+"所有部门"+SPLIT_THREE+"部门","folder"), 	
			"open",
			"部门");
		}else{
			headTreeContent = new TreeNode(
			new TreeAttr("allDepartment"+SPLIT_ONE+"allDepartment"+SPLIT_TWO+"所有部门"+SPLIT_THREE+"部门","folder"), 	
			"",
			"部门");
		}
		//封装子节点
		List<TreeNode> childrenTreeNode = new ArrayList<TreeNode>();
		childrenTreeNode.addAll(departmentsTree(departments,departmentDisplayType));
		//封装无部门人员节点
		TreeNode noDepartmentUserTreeContent = new TreeNode(
		new TreeAttr("NODEPARTMENT"+SPLIT_ONE+"NODEPARTMENT"+SPLIT_TWO,""), 	
		"closed",
		"无部门人员");
		//noDepartmentUserTreeContent.setChildren(usersNotInDepartment());
		childrenTreeNode.add(noDepartmentUserTreeContent);
		
		headTreeContent.setChildren(childrenTreeNode);
		headNode.add(headTreeContent);
		root.setChildren(headNode);
		treeNodes.add(root);
		return JsonParser.object2Json(treeNodes);
	}
	
	/**
	 * 工作组人员树
	 * @param onlineVisible 
	 */
	
	public static String getCreateManGroupTree(Long companyId,String currentId, boolean onlineVisible,String branchIds) {
		StringBuilder tree = new StringBuilder();
		
		String[] str = currentId.split(SPLIT_ONE);
		if (currentId.equals("0")) {
			if(StringUtils.isNotEmpty(branchIds)&&!("null".equals(branchIds))&&!("ADMIN".equals(branchIds))){
			   //多个分支机构树
			   tree.append(branchsWorkgroupTree(companyId,onlineVisible,branchIds));
			}else{
			   tree.append(defaultTreeFour(companyId,onlineVisible));
			}
		}else if(str[0].equals("workGroup")){
			tree.append(workGroupTree(Long.parseLong(str[1].substring(0,str[1].indexOf("="))),onlineVisible));
		}
		return tree.toString();
	}
	private static String defaultTreeFour(Long companyId,boolean onlineVisible){
		List<TreeNode> treeNodes = new ArrayList<TreeNode>();
		
		treeNodes.add(getCompanyWorkGroup(onlineVisible));
		
		//得到某个公司的所有分支机构
	/*	List<Department> branchs = ApiFactory.getAcsService().getBranchs();
		for(Department branch:branchs){
			List<Workgroup> workGroups = ApiFactory.getAcsService().getWorkgroupsByBranchId(branch.getId());
			if(workGroups.size()>0){
			  treeNodes.add(getBranchWorkGroup(branch,workGroups,onlineVisible,true));
			}
		}*/
		
		return JsonParser.object2Json(treeNodes);
	}
	//根据branchIds得到分支机构工作组树
	private static String branchsWorkgroupTree(Long companyId,boolean onlineVisible,String branchIds){
		List<TreeNode> treeNodes = new ArrayList<TreeNode>();
		//根据branchIds得到分支机构id
		String[] ids = branchIds.split(",");
		for(int i=0;i<ids.length;i++){
			Department branch = ApiFactory.getAcsService().getDepartmentById(Long.valueOf(ids[i]));
			List<Workgroup> workGroups = ApiFactory.getAcsService().getWorkgroupsByBranchId(branch.getId());
			if(workGroups.size()>0){
			  treeNodes.add(getBranchWorkGroup(branch,workGroups,onlineVisible,false));
			}
		}
		
		return JsonParser.object2Json(treeNodes);
	}
	
	
	private static TreeNode getBranchWorkGroup(Department department,List<Workgroup> workGroups,boolean onlineVisible,boolean isClosed){
		    TreeNode root = new TreeNode(
				new TreeAttr("branch"+SPLIT_ONE+ department.getId()+SPLIT_TWO+department.getName()+SPLIT_THREE+department.getName()+SPLIT_FOUR+department.getBranch(),department.getBranch()?"company":"folder"), 	
				isClosed?"closed":"open",
				department.getName());
			root.setChildren(workGroupsHaveUserTree(workGroups,onlineVisible));
		return root;
	}
	
	private static TreeNode getCompanyWorkGroup(boolean onlineVisible){
		TreeNode root = new TreeNode(
			new TreeAttr("company"+SPLIT_ONE+"company"+SPLIT_TWO+"全公司"+SPLIT_THREE+"全公司","company"), 
			"open", 
			ContextUtils.getCompanyName());
			List<Workgroup> workGroups = ApiFactory.getAcsService().getAllWorkgroups();
			root.setChildren(workGroupsHaveUserTree(workGroups,onlineVisible));
			
		return root;
	}
	
	/**
	 * 部门树
	 */
	public static String getCreateDepartmentTree(Long companyId,String currentId,DepartmentDisplayType departmentDisplayType,String branchIds) {
		List<TreeNode> treeNodes = new ArrayList<TreeNode>();
		
		if (currentId.equals("0")) {
			if(StringUtils.isNotEmpty(branchIds)&&!("null".equals(branchIds))&&!("ADMIN".equals(branchIds))){
			    //多分支机构	
				treeNodes.addAll(getBranchDepartmentTree(companyId,departmentDisplayType,branchIds));
			}else{
				treeNodes.add(getAllDepartmentTree(companyId,departmentDisplayType));
			}
		}
		return JsonParser.object2Json(treeNodes);
	}
	
	private static List<TreeNode> getBranchDepartmentTree(Long companyId,DepartmentDisplayType departmentDisplayType,String branchIds){
		List<TreeNode> treeNodes = new ArrayList<TreeNode>();
		
		String[] ids = branchIds.split(",");
		for(int i=0;i<ids.length;i++){
			Department branch = ApiFactory.getAcsService().getDepartmentById(Long.valueOf(ids[i]));
			treeNodes.add(generateDeptNodeByBranch(branch,departmentDisplayType));
		}
		
		return treeNodes;
	}
	
	//递归查找分支机构的部门
	private static TreeNode generateDeptNodeByBranch(Department department,DepartmentDisplayType departmentDisplayType){
		if(departmentDisplayType == null) departmentDisplayType = DepartmentDisplayType.NAME;
		String deptDisplayInfor = "";
		switch (departmentDisplayType) {
		case CODE:
			deptDisplayInfor = department.getCode();
			break;
		case NAME:
			deptDisplayInfor = department.getName();
			break;
		case SHORTTITLE:
			deptDisplayInfor = department.getShortTitle();
			break;
		case SUMMARY:
			deptDisplayInfor = department.getSummary();
			break;
		default:
			deptDisplayInfor = department.getName();
			break;
		}
		TreeNode departmentTreeContent = null;
		List<Department> subDepts = ApiFactory.getAcsService().getSubDepartmentList(department.getId());
		if(subDepts.isEmpty()){
				departmentTreeContent = new TreeNode(
				new TreeAttr("branch"+SPLIT_ONE+department.getId()+SPLIT_TWO+deptDisplayInfor+SPLIT_THREE+department.getName()+SPLIT_FOUR+department.getBranch(),department.getBranch()?"company":"folder"), 	
				"",
				deptDisplayInfor);
		}else{
				List<TreeNode> subDepartmentNode = new ArrayList<TreeNode>();
				for(Department subDept : subDepts){
					subDepartmentNode.add(generateSubDeptNodeByBranch(subDept,departmentDisplayType));
				}
				departmentTreeContent = new TreeNode(
				new TreeAttr("branch"+SPLIT_ONE+department.getId()+SPLIT_TWO+deptDisplayInfor+SPLIT_THREE+department.getName()+SPLIT_FOUR+department.getBranch(),department.getBranch()?"company":"folder"), 	
				"open",
				deptDisplayInfor);
				departmentTreeContent.setChildren(subDepartmentNode);
		}
		return departmentTreeContent;
	}
	
	//递归查找分支机构的部门
	private static TreeNode generateSubDeptNodeByBranch(Department department,DepartmentDisplayType departmentDisplayType){
		if(departmentDisplayType == null) departmentDisplayType = DepartmentDisplayType.NAME;
		String deptDisplayInfor = "";
		switch (departmentDisplayType) {
		case CODE:
			deptDisplayInfor = department.getCode();
			break;
		case NAME:
			deptDisplayInfor = department.getName();
			break;
		case SHORTTITLE:
			deptDisplayInfor = department.getShortTitle();
			break;
		case SUMMARY:
			deptDisplayInfor = department.getSummary();
			break;
		default:
			deptDisplayInfor = department.getName();
			break;
		}
		TreeNode departmentTreeContent = null;
		List<Department> subDepts = ApiFactory.getAcsService().getSubDepartmentList(department.getId());
		if(subDepts.isEmpty()){
				departmentTreeContent = new TreeNode(
				new TreeAttr("department"+SPLIT_ONE+department.getId()+SPLIT_TWO+deptDisplayInfor+SPLIT_THREE+department.getSubCompanyName()+SPLIT_FOUR+department.getBranch()+SPLIT_FIVE+department.getSubCompanyId(),department.getBranch()?"company":"folder"), 	
				"",
				deptDisplayInfor);
		}else{
				List<TreeNode> subDepartmentNode = new ArrayList<TreeNode>();
				for(Department subDept : subDepts){
					subDepartmentNode.add(generateSubDeptNodeByBranch(subDept,departmentDisplayType));
				}
				departmentTreeContent = new TreeNode(
				new TreeAttr("department"+SPLIT_ONE+department.getId()+SPLIT_TWO+deptDisplayInfor+SPLIT_THREE+department.getSubCompanyName()+SPLIT_FOUR+department.getBranch()+SPLIT_FIVE+department.getSubCompanyId(),department.getBranch()?"company":"folder"), 	
				"open",
				deptDisplayInfor);
				departmentTreeContent.setChildren(subDepartmentNode);
		}
		return departmentTreeContent;
	}
	
	private static TreeNode getAllDepartmentTree(Long companyId,DepartmentDisplayType departmentDisplayType){
		   TreeNode root = new TreeNode(
				new TreeAttr("company"+SPLIT_ONE+"company"+SPLIT_TWO+"全公司"+SPLIT_THREE+"全公司","company"), 
				"open", 
				ContextUtils.getCompanyName());
				
				List<Department> departments = ApiFactory.getAcsService().getDepartments();
				root.setChildren(defaultTreeFive(departments,companyId,departmentDisplayType));
				
		 return root;
	}
	
	private static List<TreeNode> defaultTreeFive(List<Department> departments,Long companyId,DepartmentDisplayType departmentDisplayType){
		List<TreeNode> treeNodes = new ArrayList<TreeNode>();
		
		TreeNode headTreeContent = null;
		if(departments.size()>0){
		    headTreeContent = new TreeNode(
			new TreeAttr("allDepartment"+SPLIT_ONE+"allDepartment"+SPLIT_TWO+"所有部门"+SPLIT_THREE+"部门","folder"), 	
			"open",
			"部门");
		}else{
			headTreeContent = new TreeNode(
			new TreeAttr("allDepartment"+SPLIT_ONE+"allDepartment"+SPLIT_TWO+"所有部门"+SPLIT_THREE+"部门","folder"), 	
			"",
			"部门");
		}
		headTreeContent.setChildren(departmentsOnlyTree(departments,departmentDisplayType));
		treeNodes.add(headTreeContent);
		return treeNodes;
	}
	public static String getCreateDepartmentTreeIncludeDeleted(Long companyId,String currentId,DepartmentDisplayType departmentDisplayType) {
		StringBuilder tree = new StringBuilder();
		tree.append("[");
		tree.append("{\"attr\":{").append(JsTreeUtil1.treeAttrBefore).append("company"+SPLIT_ONE+"company"+SPLIT_TWO+"全公司"+SPLIT_THREE+"全公司").append(JsTreeUtil1.treeAttrMiddle).append("company").append(JsTreeUtil1.treeAttrAfter).append("},\"state\":\"open\",\"data\":\""+ContextUtils.getCompanyName() + "\",\"children\":");
		List<Department> departments = ApiFactory.getAcsService().getDepartments();
		if(currentId.equals("INITIALIZED_USERS")) {
			tree.append(defaultTreeFiveIncludeDeleted(departments,companyId));
		}else if (currentId.equals("INITIALIZED")) {
			tree.append(defaultTreeFive(departments,companyId,departmentDisplayType));
		}
		tree.append("}");
		tree.append("]");
		return tree.toString();
	}
    //wj
	private static String defaultTreeFiveIncludeDeleted(List<Department> departments,Long companyId){
		StringBuilder tree = new StringBuilder();
		tree.append("[");
		if(departments.size()>0){
		tree.append("{\"attr\":{").append(JsTreeUtil1.treeAttrBefore).append("allDepartment"+SPLIT_ONE+companyId+SPLIT_TWO+"所有部门"+SPLIT_THREE+"部门").append(JsTreeUtil1.treeAttrMiddle).append("folder").append(JsTreeUtil1.treeAttrAfter).append("},\"state\":\"open\",\"data\":\""+ "部门" + "\",\"children\":[");
		}else{
			tree.append("{\"attr\":{").append(JsTreeUtil1.treeAttrBefore).append("allDepartment"+SPLIT_ONE+companyId+SPLIT_TWO+"所有部门"+SPLIT_THREE+"部门").append(JsTreeUtil1.treeAttrMiddle).append("folder").append(JsTreeUtil1.treeAttrAfter).append("},\"state\":\"\",\"data\":\""+ "部门" + "\",\"children\":[");
		}
		//tree.append(delComma(departmentsOnlyTree(departments)));
		tree.append("]},");
		tree.append(JsTreeUtil1.generateJsTreeNodeNew("NODEPARTMENT"+SPLIT_ONE+"NODEPARTMENT"+SPLIT_TWO,"","无部门人员","")).append(",");
		tree.append(JsTreeUtil1.generateJsTreeNodeNew("DELETED"+SPLIT_ONE+"0"+SPLIT_TWO,"","已删除用户",""));
		tree.append("]");
		return tree.toString();
	}
	
	private static List<TreeNode> departmentsOnlyTree(List<Department> departments,DepartmentDisplayType departmentDisplayType){
		List<TreeNode> departmentNode = new ArrayList<TreeNode>();
		for (Department department : departments) {
			departmentNode.add(generatSubDeptNode(department,departmentDisplayType));
		}
		return departmentNode;
	}

	private static TreeNode generatSubDeptNode(Department department,DepartmentDisplayType departmentDisplayType){
		if(departmentDisplayType == null) departmentDisplayType = DepartmentDisplayType.NAME;
		String deptDisplayInfor = "";
		switch (departmentDisplayType) {
		case CODE:
			deptDisplayInfor = department.getCode();
			break;
		case NAME:
			deptDisplayInfor = department.getName();
			break;
		case SHORTTITLE:
			deptDisplayInfor = department.getShortTitle();
			break;
		case SUMMARY:
			deptDisplayInfor = department.getSummary();
			break;
		default:
			deptDisplayInfor = department.getName();
			break;
		}
		TreeNode departmentTreeContent = null;
		List<Department> subDepts = ApiFactory.getAcsService().getSubDepartmentList(department.getId());
		if(subDepts.isEmpty()){
				departmentTreeContent = new TreeNode(
				new TreeAttr("department"+SPLIT_ONE+department.getId()+SPLIT_TWO+deptDisplayInfor+SPLIT_THREE+department.getSubCompanyName()+SPLIT_FOUR+department.getBranch()+SPLIT_FIVE+department.getSubCompanyId()+SPLIT_SIX+department.getShortTitle()+SPLIT_SEVEN+department.getCode(),department.getBranch()?"company":"folder"), 	
				"",
				deptDisplayInfor);
		}else{
				List<TreeNode> subDepartmentNode = new ArrayList<TreeNode>();
				for(Department subDept : subDepts){
					subDepartmentNode.add(generatSubDeptNode(subDept,departmentDisplayType));
				}
				departmentTreeContent = new TreeNode(
				new TreeAttr("department"+SPLIT_ONE+department.getId()+SPLIT_TWO+deptDisplayInfor+SPLIT_THREE+department.getSubCompanyName()+SPLIT_FOUR+department.getBranch()+SPLIT_FIVE+department.getSubCompanyId()+SPLIT_SIX+department.getShortTitle()+SPLIT_SEVEN+department.getCode(),department.getBranch()?"company":"folder"), 	
				"open",
				deptDisplayInfor);
				departmentTreeContent.setChildren(subDepartmentNode);
		}
		return departmentTreeContent;
	}
	
	/**
	 * 部门工作组树
	 */
	public static String getCreateDepartmentWorkgroupTree(Long companyId,String currentId,DepartmentDisplayType departmentDisplayType,String branchIds) {
		StringBuilder tree = new StringBuilder();
		List<Department> departments = ApiFactory.getAcsService().getDepartments();
		String[] str = currentId.split(SPLIT_ONE);
		if (currentId.equals("0")) {
			if(StringUtils.isNotEmpty(branchIds)&&!("null".equals(branchIds))&&!("ADMIN".equals(branchIds))){
			   //多个分支机构树
			   tree.append(getBranchDepartmentWorkgroupTrees(branchIds,departmentDisplayType));
			}else{
			   tree.append(defaultTreeSeven(departments,companyId,departmentDisplayType));
			}
		}else if(str[0].equals("department")) {
			tree.append(departmentTreeSeven(Long.parseLong(str[1].substring(0,str[1].indexOf("=")))));
		}
		return tree.toString();
	}
	
	/**
	 * 分支机构的部门工作组树
	 */
	private static String getBranchDepartmentWorkgroupTrees(String branchIds,DepartmentDisplayType departmentDisplayType){
		List<TreeNode> treeNodes = new ArrayList<TreeNode>();
		String[] ids = branchIds.split(",");
		for(int i=0;i<ids.length;i++){
			Department branch = ApiFactory.getAcsService().getDepartmentById(Long.valueOf(ids[i]));
			treeNodes.add(getBranchDepartmentWorkgroupTree(branch,departmentDisplayType,false));
		}
		
		return JsonParser.object2Json(treeNodes);
	}
	
	private static TreeNode getBranchDepartmentWorkgroupTree(Department department,DepartmentDisplayType departmentDisplayType,boolean isClosed){
		
		List<Department> departments = ApiFactory.getAcsService().getSubDepartmentList(department.getId());
		
		    TreeNode root = new TreeNode(
				new TreeAttr("branch"+SPLIT_ONE+ department.getId()+SPLIT_TWO+department.getName()+SPLIT_THREE+department.getName()+SPLIT_FOUR+department.getBranch(),department.getBranch()?"company":"folder"), 	
				isClosed?"closed":"open",
				department.getName());
		    
				
				List<TreeNode> headNode = new ArrayList<TreeNode>();
				//封装部门节点
				TreeNode headDepartmentTreeContent = null;
				if(departments.size()>0){
				    headDepartmentTreeContent = new TreeNode(
					new TreeAttr("allDepartment"+SPLIT_ONE+department.getId()+SPLIT_TWO+"所有部门"+SPLIT_THREE+"部门"+SPLIT_NINE+department.getName(),"folder"), 	
					"open",
					"部门");
				}else{
				    headDepartmentTreeContent = new TreeNode(
					new TreeAttr("allDepartment"+SPLIT_ONE+department.getId()+SPLIT_TWO+"所有部门"+SPLIT_THREE+"部门"+SPLIT_NINE+department.getName(),"folder"), 	
					"",
					"部门");
				}
				headDepartmentTreeContent.setChildren(departmentsOnlyTree(departments,departmentDisplayType));
				headNode.add(headDepartmentTreeContent);
				
				//封装工作组节点
				List<Workgroup> workGroups = ApiFactory.getAcsService().getWorkgroupsByBranchId(department.getId());
				
				TreeNode headWorkGroupTreeContent = null;
				if(workGroups.size()>0){
					headWorkGroupTreeContent = new TreeNode(
					new TreeAttr("allWorkGroup"+SPLIT_ONE+department.getId()+SPLIT_TWO+"工作组"+SPLIT_THREE+"工作组"+SPLIT_NINE+department.getName(),"folder"), 	
					"open",
					"工作组");
					headWorkGroupTreeContent.setChildren(workGroupsTreeSeven(workGroups, departments,false));
					headNode.add(headWorkGroupTreeContent);
				}else{
					headWorkGroupTreeContent = new TreeNode(
					new TreeAttr("allWorkGroup"+SPLIT_ONE+department.getId()+SPLIT_TWO+"工作组"+SPLIT_THREE+"工作组"+SPLIT_NINE+department.getName(),"folder"), 	
					"",
					"工作组");
					headNode.add(headWorkGroupTreeContent);
				}
				root.setChildren(headNode);
		return root;
	}
	
	
	/**
	 * 总公司的部门工作组树
	 */
	private static String defaultTreeSeven(List<Department> departments,Long companyId,DepartmentDisplayType departmentDisplayType){
		List<TreeNode> treeNodes = new ArrayList<TreeNode>();
		
		TreeNode root = new TreeNode(
		new TreeAttr("company"+SPLIT_ONE+"company"+SPLIT_TWO+"全公司"+SPLIT_THREE+"全公司","company"), 
		"open", 
		ContextUtils.getCompanyName());
		
		List<TreeNode> headNode = new ArrayList<TreeNode>();
		//封装部门节点
		TreeNode headDepartmentTreeContent = null;
		if(departments.size()>0){
		    headDepartmentTreeContent = new TreeNode(
			new TreeAttr("allDepartment"+SPLIT_ONE+"allDepartment"+SPLIT_TWO+"所有部门"+SPLIT_THREE+"部门","folder"), 	
			"open",
			"部门");
		}else{
		    headDepartmentTreeContent = new TreeNode(
			new TreeAttr("allDepartment"+SPLIT_ONE+"allDepartment"+SPLIT_TWO+"所有部门"+SPLIT_THREE+"部门","folder"), 	
			"",
			"部门");
		}
		headDepartmentTreeContent.setChildren(departmentsOnlyTree(departments,departmentDisplayType));
		headNode.add(headDepartmentTreeContent);
		
		//封装工作组节点
		List<Workgroup> workGroups = ApiFactory.getAcsService().getAllWorkgroups();
		
		TreeNode headWorkGroupTreeContent = null;
		if(workGroups.size()>0){
			headWorkGroupTreeContent = new TreeNode(
			new TreeAttr("allWorkGroup"+SPLIT_ONE+"allWorkGroup"+SPLIT_TWO+"工作组"+SPLIT_THREE+"工作组","folder"), 	
			"open",
			"工作组");
			headWorkGroupTreeContent.setChildren(workGroupsTreeSeven(workGroups, departments,true));
			headNode.add(headWorkGroupTreeContent);
		}else{
			headWorkGroupTreeContent = new TreeNode(
			new TreeAttr("allWorkGroup"+SPLIT_ONE+"allWorkGroup"+SPLIT_TWO+"工作组"+SPLIT_THREE+"工作组","folder"), 	
			"",
			"工作组");
			headNode.add(headWorkGroupTreeContent);
		}
		root.setChildren(headNode);
		treeNodes.add(root);
		return JsonParser.object2Json(treeNodes);
	}
	
	private static List<TreeNode> workGroupsTreeSeven(List<Workgroup> workGroups,List<Department> departments,boolean showBranchName){
		List<TreeNode> workGroupTreeNodes = new ArrayList<TreeNode>();
		
		TreeNode workGroupTreeContent = null;
		for (Workgroup workGroup : workGroups) {
			workGroupTreeContent = new TreeNode(
			new TreeAttr("workGroup"+SPLIT_ONE+ workGroup.getId()+SPLIT_TWO+workGroup.getName()+SPLIT_THREE+workGroup.getSubCompanyName()+SPLIT_FOUR+workGroup.getSubCompanyId(),"folder"), 	
			"",
			(workGroup.getSubCompanyId()==null||(!showBranchName))?workGroup.getName():workGroup.getName()+"/"+ApiFactory.getAcsService().getDepartmentById(workGroup.getSubCompanyId()).getName());
			workGroupTreeNodes.add(workGroupTreeContent);
		}
		return workGroupTreeNodes;
	}
	
	private static String departmentTreeSeven(Long departmentId){
		List<Department> childer = ApiFactory.getAcsService().getSubDepartmentList(departmentId);
		return JsonParser.object2Json(childerTreeSeven(childer));
	}
	
	private static List<TreeNode> childerTreeSeven(List<Department> childer){
		List<TreeNode> departmentTreeNode = new ArrayList<TreeNode>();
		TreeNode departmentTreeContent = null;
		for (Department department : childer) {
			departmentTreeContent = new TreeNode(
			new TreeAttr("department"+SPLIT_ONE+ department.getId()+SPLIT_TWO+department.getName()+SPLIT_THREE+department.getSubCompanyName()+SPLIT_FOUR+department.getBranch()+SPLIT_FIVE+department.getSubCompanyId(),department.getBranch()?"company":"folder"), 	
			"closed",
			department.getName());
			departmentTreeNode.add(departmentTreeContent);
		}
		return departmentTreeNode;
	}
	
	
	/**
	 * 工作组树
	 */	
	public static String getCreateGroupTree(Long companyId,String currentId,String branchIds) {
		
		List<TreeNode> treeNodes = new ArrayList<TreeNode>();
		
		if (currentId.equals("0")) {
			if(StringUtils.isNotEmpty(branchIds)&&!("null".equals(branchIds))&&!("ADMIN".equals(branchIds))){
				//多个分支机构树
				treeNodes.addAll(getBranchWorkgroupTrees(branchIds));
			}else{
			    treeNodes.add(getCompanyWorkgroupTree());
			}
			
		}
		return JsonParser.object2Json(treeNodes);
	}
	
	private static List<TreeNode> getBranchWorkgroupTrees(String branchIds){
		List<TreeNode> treeNodes = new ArrayList<TreeNode>();
		//根据branchIds得到分支机构id
		String[] ids = branchIds.split(",");
		for(int i=0;i<ids.length;i++){
			Department branch = ApiFactory.getAcsService().getDepartmentById(Long.valueOf(ids[i]));
			List<Workgroup> workGroups = ApiFactory.getAcsService().getWorkgroupsByBranchId(branch.getId());
			if(workGroups.size()>0){
			  treeNodes.add(getBranchWorkgroupTree(branch,workGroups));
			}
		}
		
		return treeNodes;
		
	}
	
	private static TreeNode getBranchWorkgroupTree(Department department,List<Workgroup> workGroups){
		 TreeNode root = new TreeNode(
			new TreeAttr("branch"+SPLIT_ONE+ department.getId()+SPLIT_TWO+department.getName()+SPLIT_THREE+department.getName()+SPLIT_FOUR+department.getBranch(),department.getBranch()?"company":"folder"), 	
			"open",
			department.getName());
		root.setChildren(workGroupsOnlyTree(workGroups,false));
		return root;
	}
	
	private static TreeNode getCompanyWorkgroupTree(){
		//查询所有工作组（包含全公司和分支机构）
		List<Workgroup> workGroups = ApiFactory.getAcsService().getAllWorkgroups();
		TreeNode root = null;
			if(workGroups.size()>0){
				root = new TreeNode(
				new TreeAttr("company"+SPLIT_ONE+"company"+SPLIT_TWO+"全公司"+SPLIT_THREE+"全公司","company"), 
				"open", 
				ContextUtils.getCompanyName());
					root.setChildren(defaultTreeSix(workGroups));
			}else{
				root = new TreeNode(
				new TreeAttr("company"+SPLIT_ONE+"company"+SPLIT_TWO+"全公司"+SPLIT_THREE+"全公司","company"), 
				"", 
				ContextUtils.getCompanyName());
			}
		return root;
	}
	private static List<TreeNode> defaultTreeSix(List<Workgroup> workGroups){
		List<TreeNode> workGroupTreeNodes = new ArrayList<TreeNode>();
		TreeNode headTreeContent = null;
		if(workGroups.size()>0){
			headTreeContent = new TreeNode(
			new TreeAttr("allWorkGroup"+SPLIT_ONE+"allWorkGroup"+SPLIT_TWO+"工作组"+SPLIT_THREE+"工作组","folder"), 	
			"open",
			"工作组");
			headTreeContent.setChildren(workGroupsOnlyTree(workGroups,true));
		}else{
			headTreeContent = new TreeNode(
			new TreeAttr("allWorkGroup"+SPLIT_ONE+"allWorkGroup"+SPLIT_TWO+"工作组"+SPLIT_THREE+"工作组","folder"), 	
			"",
			"工作组");
		}
		workGroupTreeNodes.add(headTreeContent);
		return workGroupTreeNodes;
	}
	
	private static List<TreeNode> workGroupsOnlyTree(List<Workgroup> workGroups,boolean showBranchName){
		List<TreeNode> workGroupsTreeNodes = new ArrayList<TreeNode>();
		TreeNode workGroupsTreeContent = null;
		for (Workgroup workGroup : workGroups) {
			workGroupsTreeContent = new TreeNode(
			new TreeAttr("workGroup"+SPLIT_ONE+ workGroup.getId()+SPLIT_TWO+workGroup.getName()+SPLIT_THREE+workGroup.getSubCompanyName()+SPLIT_FOUR+workGroup.getSubCompanyId(),"folder"), 	
			"",
			(workGroup.getSubCompanyId()==null||(!showBranchName))?workGroup.getName():workGroup.getName()+"/"+ApiFactory.getAcsService().getDepartmentById(workGroup.getSubCompanyId()).getName());
			workGroupsTreeNodes.add(workGroupsTreeContent);
		}
		return workGroupsTreeNodes;
	}
	
	
	
	
	
	/**
	 * 公司人员树
	 * @param onlineVisible 
	 */
	public static String getCreateManCompanyTree(Long companyId,String companyName,
			String currentId, boolean onlineVisible,DepartmentDisplayType departmentDisplayType,
			boolean userWithoutDeptVisible,String branchIds) {
		StringBuilder tree = new StringBuilder();
		List<Department> departments = ApiFactory.getAcsService().getDepartments();
		List<User> usersList = ApiFactory.getAcsService().getUsersWithoutDepartment();
		String[] str = currentId.split(SPLIT_ONE);
		if (currentId.equals("0")) {
			if(StringUtils.isNotEmpty(branchIds)&&!("null".equals(branchIds))&&!("ADMIN".equals(branchIds))){
			  //多个分支机构树
				tree.append(getBranchCompanyTrees(onlineVisible,departmentDisplayType,userWithoutDeptVisible,branchIds));
			}else{
			    tree.append(defaultTree(companyName,departments,usersList,companyId,onlineVisible,departmentDisplayType,userWithoutDeptVisible));
			}
		}else if(str[0].equals("department")) {
			tree.append(departmentTreeChange(Long.parseLong(str[1].substring(0,str[1].indexOf("="))),onlineVisible,userWithoutDeptVisible));
		}else if(str[0].equals("workGroup")){
			tree.append(workGroupTree(Long.parseLong(str[1].substring(0,str[1].indexOf("="))),onlineVisible));
		}else if(str[0].equals("NOBRANCH")){
			tree.append(usersNotInBranch(Long.parseLong(str[1].substring(0,str[1].indexOf("~*")))));
		}else if(str[0].equals("NODEPARTMENT")){
			tree.append(usersNotInDepartment());
		}
		return tree.toString();
	}
	
	private static String getBranchCompanyTrees(boolean onlineVisible,DepartmentDisplayType departmentDisplayType,
			boolean userWithoutDeptVisible,String branchIds){
		List<TreeNode> treeNodes = new ArrayList<TreeNode>();
		String[] ids = branchIds.split(",");
		for(int i=0;i<ids.length;i++){
			treeNodes.addAll(getBranchCompanyTree(onlineVisible,departmentDisplayType,userWithoutDeptVisible,Long.valueOf(ids[i]),false));
		}
		return JsonParser.object2Json(treeNodes);
	}
	
	/**
	 * 查询分支机构下的部门人员和工作组人员
	 * @param departments
	 * @param usersList
	 * @return
	 */
	private static List<TreeNode> getBranchCompanyTree(boolean onlineVisible,DepartmentDisplayType departmentDisplayType,
			boolean userWithoutDeptVisible,Long branchId,boolean isClosed){
		List<Department> departments = ApiFactory.getAcsService().getSubDepartmentList(branchId);
		Department department=ApiFactory.getAcsService().getDepartmentById(branchId);
		
		
		 List<TreeNode> treeNodes = new ArrayList<TreeNode>();
			TreeNode root = new TreeNode(
						new TreeAttr("branch"+SPLIT_ONE+ department.getId()+SPLIT_TWO+department.getName()+SPLIT_THREE+department.getName()+SPLIT_FOUR+department.getBranch(),department.getBranch()?"company":"folder"), 	
						isClosed?"closed":"open",
						department.getName());
			
			//封装部门子节点
			List<TreeNode> headDepartmentNode = new ArrayList<TreeNode>();
			TreeNode headDepartmentTreeContent = null;
			if(departments.size()>0){
				headDepartmentTreeContent = new TreeNode(
				new TreeAttr("allDepartment"+SPLIT_ONE+department.getId()+SPLIT_TWO+"所有部门"+SPLIT_THREE+"部门"+SPLIT_NINE+department.getName(),"folder"), 	
				"open",
				"部门");
			}else{
				headDepartmentTreeContent = new TreeNode(
				new TreeAttr("allDepartment"+SPLIT_ONE+department.getId()+SPLIT_TWO+"所有部门"+SPLIT_THREE+"部门"+SPLIT_NINE+department.getName(),"folder"), 	
				"",
				"部门");
			}
			
			//如果显示无部门人员，则封装子节点
			List<User> usersList = ApiFactory.getAcsService().getUsersWithoutBranch(department.getId());
			if(department.getBranch()&&userWithoutDeptVisible&&usersList.size()>0){
				//封装子节点
				List<TreeNode> childrenTreeNode = new ArrayList<TreeNode>();
				childrenTreeNode.addAll(departmentsTree(departments,departmentDisplayType));
				//封装分支机构的无部门人员节点
				TreeNode noDepartmentUserTreeContent = new TreeNode(
						new TreeAttr("NOBRANCH"+SPLIT_ONE+department.getId()+SPLIT_NINE+department.getName(),"folder"), 	
						"closed",
				"无部门人员");
				//noDepartmentUserTreeContent.setChildren(usersNotInBranch(usersList));
				childrenTreeNode.add(noDepartmentUserTreeContent);
				headDepartmentTreeContent.setChildren(childrenTreeNode);
			}else{
				headDepartmentTreeContent.setChildren(departmentsTree(departments,departmentDisplayType));
			}
			
			headDepartmentNode.add(headDepartmentTreeContent);
			
			//封装工作组子节点(分支机下的)
			List<Workgroup> workGroups = ApiFactory.getAcsService().getWorkgroupsByBranchId(branchId);
			List<TreeNode> headWorkGroupNode = new ArrayList<TreeNode>();
			TreeNode headWorkGroupTreeContent = null;
			if(workGroups.size()>0){
				headWorkGroupTreeContent = new TreeNode(
				new TreeAttr("allWorkGroup"+SPLIT_ONE+branchId+SPLIT_TWO+"工作组"+SPLIT_THREE+"工作组"+SPLIT_NINE+department.getName(),"folder"), 	
				"open",
				"工作组");
			}else{
				headWorkGroupTreeContent = new TreeNode(
				new TreeAttr("allWorkGroup"+SPLIT_ONE+branchId+SPLIT_TWO+"工作组"+SPLIT_THREE+"工作组"+SPLIT_NINE+department.getName(),"folder"), 	
				"",
				"工作组");
			}
			headWorkGroupTreeContent.setChildren(workGroupsTree(workGroups,onlineVisible,false));
			headWorkGroupNode.add(headWorkGroupTreeContent);
			
			List<TreeNode> togetherTreeNode = new ArrayList<TreeNode>();
			togetherTreeNode.addAll(headDepartmentNode);
			togetherTreeNode.addAll(headWorkGroupNode);
			root.setChildren(togetherTreeNode);
			treeNodes.add(root);
		 return treeNodes;
	}
	
	/**
	 * 只查部门，工作组和没有部门的用户
	 * @param departments
	 * @param usersList
	 * @return
	 */

	private static String defaultTree(String companyName,List<Department> departments,List<User> usersList,Long companyId,boolean onlineVisible,DepartmentDisplayType departmentDisplayType,boolean userWithoutDeptVisible){
		List<TreeNode> treeNodes = new ArrayList<TreeNode>();
		TreeNode root = new TreeNode(
		new TreeAttr("company"+SPLIT_ONE+"company"+SPLIT_TWO+"全公司"+SPLIT_THREE+"全公司","company"), 
		"open", 
		ContextUtils.getCompanyName());
		
		//封装部门子节点
		List<TreeNode> headDepartmentNode = new ArrayList<TreeNode>();
		TreeNode headDepartmentTreeContent = null;
		if(departments.size()>0){
			headDepartmentTreeContent = new TreeNode(
			new TreeAttr("allDepartment"+SPLIT_ONE+"allDepartment"+SPLIT_TWO+"所有部门"+SPLIT_THREE+"部门","folder"), 	
			"open",
			"部门");
		}else{
			headDepartmentTreeContent = new TreeNode(
			new TreeAttr("allDepartment"+SPLIT_ONE+"allDepartment"+SPLIT_TWO+"所有部门"+SPLIT_THREE+"部门","folder"), 	
			"",
			"部门");
		}
		
		//如果显示无部门人员，则封装子节点
		if(userWithoutDeptVisible){
			//封装子节点
			List<TreeNode> childrenTreeNode = new ArrayList<TreeNode>();
			childrenTreeNode.addAll(departmentsTree(departments,departmentDisplayType));
			//封装无部门人员节点
			TreeNode noDepartmentUserTreeContent = new TreeNode(
					new TreeAttr("NODEPARTMENT"+SPLIT_ONE+"NODEPARTMENT"+SPLIT_TWO,"folder"), 	
					"closed",
			"无部门人员");
			//noDepartmentUserTreeContent.setChildren(usersNotInDepartment());
			childrenTreeNode.add(noDepartmentUserTreeContent);
			headDepartmentTreeContent.setChildren(childrenTreeNode);
		}else{
			headDepartmentTreeContent.setChildren(departmentsTree(departments,departmentDisplayType));
		}
		
		headDepartmentNode.add(headDepartmentTreeContent);
		
		//封装工作组子节点(包括总公司的和分支机构的)
		List<Workgroup> workGroups = ApiFactory.getAcsService().getAllWorkgroups();
		List<TreeNode> headWorkGroupNode = new ArrayList<TreeNode>();
		TreeNode headWorkGroupTreeContent = null;
		if(workGroups.size()>0){
			headWorkGroupTreeContent = new TreeNode(
			new TreeAttr("allWorkGroup"+SPLIT_ONE+"allWorkGroup"+SPLIT_TWO+"工作组"+SPLIT_THREE+"工作组","folder"), 	
			"open",
			"工作组");
		}else{
			headWorkGroupTreeContent = new TreeNode(
			new TreeAttr("allWorkGroup"+SPLIT_ONE+"allWorkGroup"+SPLIT_TWO+"工作组"+SPLIT_THREE+"工作组","folder"), 	
			"",
			"工作组");
		}
		headWorkGroupTreeContent.setChildren(workGroupsTree(workGroups,onlineVisible,true));
		headWorkGroupNode.add(headWorkGroupTreeContent);
		
		List<TreeNode> togetherTreeNode = new ArrayList<TreeNode>();
		togetherTreeNode.addAll(headDepartmentNode);
		togetherTreeNode.addAll(headWorkGroupNode);
		root.setChildren(togetherTreeNode);
		treeNodes.add(root);
		return JsonParser.object2Json(treeNodes);
	}
	
	
	
	/**
	 * 只查部门
	 * @param departments
	 * @param usersList
	 * @return
	 */
	private static List<TreeNode> departmentsTree(List<Department> departments,DepartmentDisplayType departmentDisplayType){
		List<TreeNode> departmentsTreeNode = new ArrayList<TreeNode>();
		TreeNode childTreeContent = null;
		String deptDisplayInfor="";
		if(departmentDisplayType == null) departmentDisplayType = DepartmentDisplayType.NAME;
		for (Department department : departments) {
			List<Department> childer = ApiFactory.getAcsService().getSubDepartmentList(department.getId());
			List<User> users = ApiFactory.getAcsService().getUsersByDepartmentId(department.getId());
			switch (departmentDisplayType) {
			case CODE:
				deptDisplayInfor = department.getCode();
				break;
			case NAME:
				deptDisplayInfor = department.getName();
				break;
			case SHORTTITLE:
				deptDisplayInfor = department.getShortTitle();
				break;
			case SUMMARY:
				deptDisplayInfor = department.getSummary();
				break;
			default:
				deptDisplayInfor = department.getName();
				break;
			}
			
			//判断部门下面是否有人
			if ((childer != null && childer.size() > 0|| users != null && users.size() > 0)) {
				childTreeContent = new TreeNode(
				new TreeAttr("department"+SPLIT_ONE+ department.getId()+SPLIT_TWO+deptDisplayInfor+SPLIT_THREE+department.getSubCompanyName()+SPLIT_FOUR+department.getBranch()+SPLIT_FIVE+department.getSubCompanyId(),department.getBranch()?"company":"folder"), 	
				"closed",
				deptDisplayInfor);
			}else{
				childTreeContent = new TreeNode(
				new TreeAttr("department"+SPLIT_ONE+ department.getId()+SPLIT_TWO+deptDisplayInfor+SPLIT_THREE+department.getSubCompanyName()+SPLIT_FOUR+department.getBranch()+SPLIT_FIVE+department.getSubCompanyId(),department.getBranch()?"company":"folder"), 	
				"",
				deptDisplayInfor);
			}
			departmentsTreeNode.add(childTreeContent);
		}
		return departmentsTreeNode;
	}
	
	
	/**
	 * 只查有人员的工作组
	 * @param departments
	 * @param usersList
	 * @return
	 */
	
	private static List<TreeNode> workGroupsHaveUserTree(List<Workgroup> workGroups,boolean onlineVisible){
		boolean isHasBranch = ContextUtils.hasBranch();//当前租户中是否存在分支
		List<TreeNode> workGroupsChildNode = new ArrayList<TreeNode>();
		TreeNode workGroupsChildContent = null;
		for (Workgroup workGroup : workGroups) {
			List<User> users = ApiFactory.getAcsService().getUsersByWorkgroupId(workGroup.getId());
			if (workGroups != null && workGroups.size() > 0&&users != null && users.size() > 0) {
				workGroupsChildContent = new TreeNode(
				new TreeAttr("workGroup"+SPLIT_ONE+workGroup.getId()+SPLIT_TWO+workGroup.getName()+SPLIT_THREE+workGroup.getSubCompanyName()+SPLIT_FOUR+workGroup.getSubCompanyId(),"folder"), 	
				"closed",
				workGroup.getSubCompanyId()==null?workGroup.getName():workGroup.getName()+"/"+workGroup.getSubCompanyName());
				workGroupsChildContent.setChildren(usersTree(users,workGroup.getId().toString(),workGroup.getName(),WORKGROUP,onlineVisible,isHasBranch));
			}else{
				workGroupsChildContent = new TreeNode(
				new TreeAttr("workGroup"+SPLIT_ONE+workGroup.getId()+SPLIT_TWO+workGroup.getName()+SPLIT_THREE+workGroup.getSubCompanyName()+SPLIT_FOUR+workGroup.getSubCompanyId(),"folder"), 	
				"",
				workGroup.getSubCompanyId()==null?workGroup.getName():workGroup.getName()+"/"+workGroup.getSubCompanyName());
				
			}
			workGroupsChildNode.add(workGroupsChildContent);
		}
		return workGroupsChildNode;
	}
	
	/**
	 * 只查工作组
	 * @param departments
	 * @param usersList
	 * @return
	 */
	
	private static List<TreeNode> workGroupsTree(List<Workgroup> workGroups,boolean onlineVisible,boolean showBranchName){
		List<TreeNode> workGroupsChildNode = new ArrayList<TreeNode>();
		TreeNode workGroupsChildContent = null;
		for (Workgroup workGroup : workGroups) {
			List<User> users = ApiFactory.getAcsService().getUsersByWorkgroupId(workGroup.getId());
			if (workGroups != null && workGroups.size() > 0&&users != null && users.size() > 0) {
				workGroupsChildContent = new TreeNode(
				new TreeAttr("workGroup"+SPLIT_ONE+workGroup.getId()+SPLIT_TWO+workGroup.getName()+SPLIT_THREE+workGroup.getSubCompanyName()+SPLIT_FOUR+workGroup.getSubCompanyId(),"folder"), 	
				"closed",
				(workGroup.getSubCompanyId()==null||(!showBranchName))?workGroup.getName():workGroup.getName()+"/"+ApiFactory.getAcsService().getDepartmentById(workGroup.getSubCompanyId()).getName());
				//加载工作组下面人员
				//workGroupsChildContent.setChildren(usersTree(users,workGroup.getId().toString(),workGroup.getName(),WORKGROUP,onlineVisible));
			}else{
				workGroupsChildContent = new TreeNode(
				new TreeAttr("workGroup"+SPLIT_ONE+workGroup.getId()+SPLIT_TWO+workGroup.getName()+SPLIT_THREE+workGroup.getSubCompanyName()+SPLIT_FOUR+workGroup.getSubCompanyId(),"folder"), 	
				"",
				(workGroup.getSubCompanyId()==null||(!showBranchName))?workGroup.getName():workGroup.getName()+"/"+ApiFactory.getAcsService().getDepartmentById(workGroup.getSubCompanyId()).getName());
			}
			workGroupsChildNode.add(workGroupsChildContent);
		}
		return workGroupsChildNode;
	}
	
	

	/**
	 * 2查部门及下面的用户和子部门
	 * @param departments
	 * @param usersList
	 * @return
	 */
	@SuppressWarnings("unused")
	private static String departmentTree(Long departmentId,boolean onlineVisible){
		List<Department> childer = ApiFactory.getAcsService().getSubDepartmentList(departmentId);
		List<User> users = ApiFactory.getAcsService().getUsersByDepartmentId(departmentId);
		Department department=ApiFactory.getAcsService().getDepartmentById(departmentId);
		boolean isHasBranch  = department.getBranch();
		StringBuilder tree = new StringBuilder();
		tree.append("[");
		tree.append(delComma(usersTree(users,department.getId().toString(),department.getName(),DEPARTMENT,onlineVisible,isHasBranch)+childerTree(childer)));
		tree.append("]");
		return tree.toString();
	}
	/**
	 * 
	 * @param usersList
	 * @param departId
	 * @param name
	 * @param type
	 * @param onlineVisible
	 * @param isHasBranch :当前租户内是否有分支，true表示有分支
	 * @return
	 */
	private static List<TreeNode> usersTree(List<User> usersList,String departId,String name,String type,boolean onlineVisible,boolean isHasBranch){
		List<TreeNode> usersTreeNodes = new ArrayList<TreeNode>();
		TreeNode userTreeContent = null;
		List<Long> onlineUserIds = ApiFactory.getAcsService().getOnlineUserIds();
		for (User user : usersList) {
			String userinfo = user.getName();
			if(isHasBranch){
				userinfo = user.getName()+"("+user.getSubCompanyName()+")";
			}
			if(onlineVisible){
				if(onlineUserIds.contains(user.getId())){
					userTreeContent = new TreeNode(
							new TreeAttr("user"+SPLIT_ONE+user.getId()+SPLIT_TWO+ user.getName()+SPLIT_THREE+user.getLoginName()+SPLIT_FOUR+type+SPLIT_FIVE+name+SPLIT_SIX+departId+SPLIT_EIGHT+user.getEmail()+SPLIT_SEVEN+user.getHonorificName()+SPLIT_SEVEN+user.getWeight()+SPLIT_NINE+user.getSubCompanyName()+SPLIT_TEN+user.getSubCompanyId(),"onlineUser"), 	
							"",
							userinfo);
							usersTreeNodes.add(userTreeContent);
				}else{
					userTreeContent = new TreeNode(
							new TreeAttr("user"+SPLIT_ONE+user.getId()+SPLIT_TWO+ user.getName()+SPLIT_THREE+user.getLoginName()+SPLIT_FOUR+type+SPLIT_FIVE+name+SPLIT_SIX+departId+SPLIT_EIGHT+user.getEmail()+SPLIT_SEVEN+user.getHonorificName()+SPLIT_SEVEN+user.getWeight()+SPLIT_NINE+user.getSubCompanyName()+SPLIT_TEN+user.getSubCompanyId(),"user"), 	
							"",
							userinfo);
							usersTreeNodes.add(userTreeContent);
				}
			}else{
				userTreeContent = new TreeNode(
						new TreeAttr("user"+SPLIT_ONE+user.getId()+SPLIT_TWO+ user.getName()+SPLIT_THREE+user.getLoginName()+SPLIT_FOUR+type+SPLIT_FIVE+name+SPLIT_SIX+departId+SPLIT_EIGHT+user.getEmail()+SPLIT_SEVEN+user.getHonorificName()+SPLIT_SEVEN+user.getWeight()+SPLIT_NINE+user.getSubCompanyName()+SPLIT_TEN+user.getSubCompanyId(),"user"), 	
						"",
						userinfo);
						usersTreeNodes.add(userTreeContent);
			}
		}
		return usersTreeNodes;
	}
	
    //无部门用户(全公司)
	private static String usersNotInDepartment(){
		List<User> usersList = ApiFactory.getAcsService().getUsersWithoutDepartment();
		List<TreeNode> usersTreeNodes = new ArrayList<TreeNode>();
		TreeNode userTreeContent = null;
		for (User user : usersList) {
			userTreeContent = new TreeNode(
			new TreeAttr("user"+SPLIT_ONE+user.getId()+SPLIT_TWO+ user.getName()+SPLIT_THREE+user.getLoginName()+SPLIT_FOUR+NOTINDEPARTMENT+SPLIT_FIVE+NOTINDEPARTMENT+SPLIT_SIX+NOTINDEPARTMENT+SPLIT_EIGHT+user.getEmail()+SPLIT_SEVEN+user.getHonorificName()+SPLIT_SEVEN+user.getWeight()+SPLIT_NINE+user.getSubCompanyName()+SPLIT_TEN+user.getSubCompanyId(),"user"), 	
			"",
			user.getName());
			usersTreeNodes.add(userTreeContent);
		}
		return JsonParser.object2Json(usersTreeNodes);
	}
	//无部门用户（分支机构）
	private static String usersNotInBranch(Long branchId){
		List<User> usersList =  ApiFactory.getAcsService().getUsersWithoutBranch(branchId);
		List<TreeNode> usersTreeNodes = new ArrayList<TreeNode>();
		TreeNode userTreeContent = null;
		for (User user : usersList) {
			userTreeContent = new TreeNode(
			new TreeAttr("user"+SPLIT_ONE+user.getId()+SPLIT_TWO+ user.getName()+SPLIT_THREE+user.getLoginName()+SPLIT_FOUR+NOTINDEPARTMENT+SPLIT_FIVE+NOTINDEPARTMENT+SPLIT_SIX+NOTINDEPARTMENT+SPLIT_EIGHT+user.getEmail()+SPLIT_SEVEN+user.getHonorificName()+SPLIT_SEVEN+user.getWeight()+SPLIT_NINE+user.getSubCompanyName()+SPLIT_TEN+user.getSubCompanyId(),"user"), 	
			"",
			user.getName());
			usersTreeNodes.add(userTreeContent);
		}
		return JsonParser.object2Json(usersTreeNodes);
	}
	
	private static List<TreeNode> usersNotInBranch(List<User> usersList){
		List<TreeNode> usersTreeNodes = new ArrayList<TreeNode>();
		TreeNode userTreeContent = null;
		for (User user : usersList) {
			userTreeContent = new TreeNode(
			new TreeAttr("user"+SPLIT_ONE+user.getId()+SPLIT_TWO+ user.getName()+SPLIT_THREE+user.getLoginName()+SPLIT_FOUR+NOTINDEPARTMENT+SPLIT_FIVE+NOTINDEPARTMENT+SPLIT_SIX+NOTINDEPARTMENT+SPLIT_EIGHT+user.getEmail()+SPLIT_SEVEN+user.getHonorificName()+SPLIT_SEVEN+user.getWeight()+SPLIT_NINE+user.getSubCompanyName()+SPLIT_TEN+user.getSubCompanyId(),"user"), 	
			"",
			user.getName());
			usersTreeNodes.add(userTreeContent);
		}
		return usersTreeNodes;
	}
	
	private static String childerTree(List<Department> childer){
		StringBuilder tree = new StringBuilder();
		for (Department department : childer) {
			List<User> users1 = ApiFactory.getAcsService().getUsersByDepartmentId(department.getId());
			if (users1 != null && users1.size() > 0) {
				tree.append(JsTreeUtil1.generateJsTreeNodeNew("department"+SPLIT_ONE+ department.getId()+SPLIT_TWO+department.getName()+SPLIT_THREE+department.getSubCompanyName()+SPLIT_FOUR+department.getBranch()+SPLIT_FIVE+department.getSubCompanyId(), "closed", department.getName(),department.getBranch()?"company":"folder")).append(",");
			}else {
				// 如果子部门下没有人员，则不显示(可以将下面代码注释)
				tree.append(JsTreeUtil1.generateJsTreeNodeNew("department"+SPLIT_ONE+ department.getId()+SPLIT_TWO+department.getName()+SPLIT_THREE+department.getSubCompanyName()+SPLIT_FOUR+department.getBranch()+SPLIT_FIVE+department.getSubCompanyId(), "", department.getName(),department.getBranch()?"company":"folder")).append(",");
			}
		}
		return tree.toString();
	}
		
	
	/**
	 * 3查工作组及下面的用户
	 * @param departments
	 * @param usersList
	 * @return
	 */
	private static String workGroupTree(Long workGroupId,boolean onlineVisible){
		boolean isHasBranch = ContextUtils.hasBranch();
		List<User> users = ApiFactory.getAcsService().getUsersByWorkgroupId(workGroupId);
		Workgroup group=ApiFactory.getAcsService().getWorkgroupById(workGroupId);
		return JsonParser.object2Json(usersTree(users,group.getId().toString(),group.getName(),WORKGROUP,onlineVisible,isHasBranch));
	}
	
	
	/**
	 * 去逗号
	 * @param str
	 * @return
	 */
	private static String delComma(String str){
		if(StringUtils.endsWith(str, ","))str= str.substring(0,str.length() - 1);
		return str;
	}	
}
