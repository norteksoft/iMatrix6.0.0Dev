package com.norteksoft.product.util;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.apache.commons.lang.StringUtils;

import com.norteksoft.product.api.AcsService;
import com.norteksoft.product.api.ApiFactory;
import com.norteksoft.product.api.entity.Department;
import com.norteksoft.product.api.entity.User;
import com.norteksoft.product.api.entity.Workgroup;
import com.norteksoft.product.util.tree.ZTreeNode;
import com.norteksoft.tags.tree.TreeType;

//ztree json util
public class ZTreeUtils{
	private static AcsService as=null;
	static{
		as=ApiFactory.getAcsService();
	}
	//拼节点id时为了防止节点id重复,前面加的前缀
	private static String COMPANY_="company_";
	private static String ALLDEPARTMENT_="allDepartment_";
	private static String DEPARTMENT_="department_";
	private static String ALLWORKGROUP_="allWorkgroup_";
	private static String WORKGROUP_="workgroup_";
	private static String USER_="user_";
	private static String BRANCH_="branch_";
	private static String USERHASNOTDEPARTMENT_="userHasNotDepartment_";
	private static String HASWORKGROUPBRANCH_="hasWorkgroupBranch_";
	
	//节点类型
	private static String COMPANY="company";//公司
	private static String ALLDEPARTMENT="allDepartment";//所有部门
	private static String DEPARTMENT="department";//部门
	private static String ALLWORKGROUP="allWorkgroup";//所有工作组
	private static String WORKGROUP="workgroup";//工作组
	private static String USER="user";//员工
	private static String BRANCH="branch";//分支机构
	private static String USERHASNOTDEPARTMENT="userHasNotDepartment";//无部门节点
	private static String HASWORKGROUPBRANCH="hasWorkgroupBranch";
	//可以考虑在配置文件里配
	private static String USERNODEDATA="id,name,loginName,mainDepartmentId,email,weight,subCompanyId,subCompanyName";
	private static String DEPARTMENTNODEDATA="id,name,weight,code,shortTitle,summary,parentDepartmentId,branch,subCompanyId,subCompanyName";
	private static String WORKGROUPNODEDATA="id,name,weight,code,description,shortTitle,summary,subCompanyId,subCompanyName";
	
	//设置树节点显示内容
	public static String treeNodeShowContent;//线程不安全
	public static void setTreeNodeShowContent(String treeNodeShowContent) {
		ZTreeUtils.treeNodeShowContent = treeNodeShowContent;
	}
	
	//是否显示无部门人员
	private static boolean userWithoutDeptVisible;
	public static void setUserWithoutDeptVisible(boolean userWithoutDeptVisible) {
		ZTreeUtils.userWithoutDeptVisible = userWithoutDeptVisible;
	}
	
	//标准tree的参数
    private static String treeNodeData;//设置树节点data
	public static void setTreeNodeData(String treeNodeData) {
		ZTreeUtils.treeNodeData = treeNodeData;
	}
	
	//是否显示在线人员
	private static boolean onlineVisible;
	public static void setOnlineVisible(boolean onlineVisible) {
		ZTreeUtils.onlineVisible = onlineVisible;
	}
	
	//显示设定部门
	private static String departmentShow;
	public static void setDepartmentShow(String departmentShow) {
		ZTreeUtils.departmentShow = departmentShow;
	}
	
	private static boolean showBranch;
	public static void setShowBranch(boolean showBranch) {
		ZTreeUtils.showBranch = showBranch;
	}
	
	private static String branchIds;
	public static void setBranchIds(String branchIds){
		ZTreeUtils.branchIds=branchIds;
	}

	/**
	 * ****公司人员树*******************************************************************************************************************
	 */	
	/**
	 * 公司人员树(异步)
	 * COMPANY
	 */
	public static String createCompanyTree(Long companyId,String companyName,String currentId) {
		StringBuilder tree = new StringBuilder();
		String[] str = currentId.split("_");
		if (currentId.equals("0")) {
			tree.append(getInitialCompanyTree(currentId,companyId,companyName));
		}else if(str[0].equals("department")||str[0].equals("branch")) {
			tree.append(getNodeInDepartment(Long.parseLong(str[1]),str[0],currentId));
		}else if(str[0].equals("userHasNotDepartment")){
			tree.append(getNodeInNoDepartment(Long.parseLong(str[1]),str[0],currentId));
		}else if(str[0].equals("workgroup")){
			tree.append(getNodesOnExpandOneWorkgroup(Long.parseLong(str[1])));
		}
		
		return tree.toString();
	}

	/**
	 * 只查部门，工作组和没有部门的用户
	 * @param departments
	 * @param usersList
	 * @return
	 */
	private static Object getInitialCompanyTree(String currentId,Long companyId, String companyName) {
		List<ZTreeNode> treeNodes = new ArrayList<ZTreeNode>();
			//公司节点
			createCompanyNode(companyId,companyName,treeNodes,true);
	
			//所有部门节点
			addAllDepartmentNode(companyId.toString(),treeNodes,currentId);
			
			//封装所有工作组节点
			addUserAllWorkgroup(companyId.toString(),treeNodes);
			return JsonParser.object2Json(treeNodes);
	}

	//根据部门名字字符串得到部门list 
	private static List<Department> getDepartmentByNameStr(String departmentShow) {
		String[] arr = departmentShow.split(",");
		List<Department> list = new ArrayList<Department>();
		for(String departmentName : arr){
			Department department=as.getDepartmentByName(departmentName.trim());
			if(department!=null)
			list.add(department);
		}
		return list;
	}
	//根据部门名字字符串得到部门list 
	private static List<Department> getBranchByIds() {
		List<Department> list = new ArrayList<Department>();
		if(branchIds!=null&&!branchIds.trim().equals("")){
			String[] arr = branchIds.split(",");
			for(String id : arr){
				if(id.matches("^[1-9]\\d*")){
					Department department=as.getDepartmentById(Long.parseLong(id));
					if(department!=null)
					list.add(department);
				}
			}
		}
		branchIds="";
		return list;
	}
	
	//所有部门节点
	private static void addAllDepartmentNode(String companyId , List<ZTreeNode> treeNodes,String currentId) {
		List<Department> departments=as.getDepartments();
		createAllDepartmentNode(companyId.toString(),treeNodes,departments);
		for(Department dept:departments){
			ZTreeNode company=null;
			if(dept.getBranch()){
				company = 
					new ZTreeNode("branch_"+dept.getId().toString(),ALLDEPARTMENT_+companyId, dept.getName(),"false","false",BRANCH,object2Json(dept),"branch");
			}else{
				String nodeName=dept.getName();
				if(showBranch&&ContextUtils.hasBranch()){
					dept.setName(dept.getName()+"("+dept.getSubCompanyName()+")");
				}
				company = 
					new ZTreeNode("department_"+dept.getId().toString(),ALLDEPARTMENT_+companyId, nodeName,"false","false",DEPARTMENT,object2Json(dept),"department");
			}
			if(haschlid(dept)){
				company.setIsParent("true");
			}else{
				company.setIsParent("false");
			}
			treeNodes.add(company);
		}
		//封装无部门人员节点
		addUserHasNotDepartment(currentId,companyId.toString(),treeNodes);
	}

	//封装无部门人员节点
	private static ZTreeNode addUserHasNotDepartment(String currentId,String id,List<ZTreeNode> treeNodes) {
		ZTreeNode userHasNotDepartmentNode = null;
		if(userWithoutDeptVisible){
			List<User> users=null;
				if(currentId.equals("0")){
					users=as.getUsersWithoutDepartment();
					userHasNotDepartmentNode = 
						new ZTreeNode(USERHASNOTDEPARTMENT_+id, COMPANY_+id, "无部门人员","false","false",USERHASNOTDEPARTMENT,"{\"name\" : \"\" }","department");
				}else{
					users=as.getUsersWithoutBranch(Long.valueOf(id));
					userHasNotDepartmentNode = 
						new ZTreeNode(USERHASNOTDEPARTMENT_+currentId.split("_")[1], currentId, "无部门人员","false","fasle",USERHASNOTDEPARTMENT,"{\"name\" : \"无部门人员\" }","department");
				}
				if(users.size()>0){
					userHasNotDepartmentNode.setIsParent("true");
					treeNodes.add(userHasNotDepartmentNode);
				}
		}
		return userHasNotDepartmentNode;
	}
	

	//封装所有工作组节点
	private static void addUserAllWorkgroup(String companyId,List<ZTreeNode> treeNodes) {
		List<Department> depts=as.getDepartmentIfHasWorkGroup();
		 //封装公司下工作组节点
	    List<Workgroup> workGroups2=as.getWorkgroups();
	    for(int i=0;i<workGroups2.size();i++){
	    	List<User> users = as.getUsersByWorkgroupId(workGroups2.get(i).getId());
			if(workGroups2 != null && workGroups2.size() > 0&&users != null && users.size() > 0){
				createWorkgroupNodeOpen(workGroups2.get(i),ALLWORKGROUP_+companyId,treeNodes);
			}else{
				createWorkgroupNodeClose(workGroups2.get(i),ALLWORKGROUP_+companyId,treeNodes);
			}
	    }
		//封装"工作组"节点
		createAllWorkgroupNode(companyId.toString(),treeNodes,depts);
		String pId="";
		for(Department dept:depts){
			pId=HASWORKGROUPBRANCH_+dept.getId();
			treeNodes.add(new ZTreeNode(pId,ALLWORKGROUP_+companyId, dept.getName(),"true","true",HASWORKGROUPBRANCH,object2Json(dept),"branch"));
			List<Workgroup> workGroups=as.getWorkgroupsByBranchId(dept.getId());
			for(Workgroup w : workGroups){
				List<User> users = as.getUsersByWorkgroupId(w.getId());
				if(workGroups != null && workGroups.size() > 0&&users != null && users.size() > 0){
					createWorkgroupNodeOpen(w,pId,treeNodes);
				}else{
					createWorkgroupNodeClose(w,pId,treeNodes);
				}
			}
		}
		
		
	}
	//封装工作组用户树的所有工作组节点
	private static void addAllWorkgroup(String companyId,List<ZTreeNode> treeNodes, List<Department> depts,List<Workgroup> companyWorkgroups) {
		for(Workgroup wg:companyWorkgroups){
			List<User> users = as.getUsersByWorkgroupId(wg.getId());
			if(companyWorkgroups != null && companyWorkgroups.size() > 0&&users != null && users.size() > 0){
				createWorkgroupNodeOpen(wg,COMPANY_+companyId,treeNodes);
			}else{
				createWorkgroupNodeClose(wg,COMPANY_+companyId,treeNodes);
			}
		}
		String pId="";
		for(Department dept:depts){
			pId=HASWORKGROUPBRANCH_+dept.getId();
			treeNodes.add(new ZTreeNode(pId,COMPANY_+companyId, dept.getName(),"true","true",HASWORKGROUPBRANCH,object2Json(dept),"branch"));
			List<Workgroup> workGroups=as.getWorkgroupsByBranchId(dept.getId());
			for(Workgroup w : workGroups){
				List<User> users = as.getUsersByWorkgroupId(w.getId());
				if(workGroups != null && workGroups.size() > 0&&users != null && users.size() > 0){
					createWorkgroupNodeOpen(w,pId,treeNodes);
				}else{
					createWorkgroupNodeClose(w,pId,treeNodes);
				}
			}
		}
		
	}
	
	
	/**
	 * 获取部门或分支机构下的所有节点
	 * @param 
	 * @param 
	 * @return
	 */	
	private static Object getNodeInDepartment(Long departmentId,String departmentType,String currentId) {
		List<ZTreeNode> treeNodes = new ArrayList<ZTreeNode>();
		List<Department> departments=as.getSubDepartmentList(departmentId);
		if(departmentType.equals("department")){
			addUserNode(treeNodes,departmentId,departmentType,currentId);
		}
		ZTreeNode node=null;
		for(Department dept:departments){
		if(dept.getBranch()){
			node = 
				new ZTreeNode("branch_"+dept.getId().toString(),currentId, dept.getName(),"false","false",BRANCH,object2Json(dept),"branch");
		}else{
			String nodeName=dept.getName();
			if(showBranch&&ContextUtils.hasBranch()){
				dept.setName(dept.getName()+"("+dept.getSubCompanyName()+")");
			}
			node = 
				new ZTreeNode("department_"+dept.getId().toString(),currentId, nodeName,"false","false",DEPARTMENT,object2Json(dept),"department");
		}
		if(haschlid(dept)){
			node.setIsParent("true");
		}else{
			node.setIsParent("false");
		}
		treeNodes.add(node);
	}
		if(departmentType.equals("branch")){
			//封装无部门人员节点
			addUserHasNotDepartment(currentId,departmentId.toString(),treeNodes);
		}
		return JsonParser.object2Json(treeNodes);
	
	}
	

	private static void addUserNode(List<ZTreeNode> treeNodes,
			Long id, String departmentType, String currentId) {
		List<User> users=null;
		if(departmentType.equals("userHasNotDepartment")){
			if(id.equals(ContextUtils.getCompanyId())){
				users=as.getUsersWithoutDepartment();
			}else{
				users=as.getUsersWithoutBranch(id);
			}
		}else if(departmentType.equals("department")){
			users=as.getUsersByDepartmentId(id);
		}
		ZTreeNode node=null;
		for(User user:users){
			String nodeName=user.getName();
			if(showBranch&&ContextUtils.hasBranch()){
				user.setName(user.getName()+"("+user.getSubCompanyName()+")");
			}
			node=new ZTreeNode(USER_+user.getId().toString(),currentId, nodeName,"false","false",USER,object2Json(user),"user");
			if(onlineVisible){
				List<Long> ids=ApiFactory.getAcsService().getOnlineUserIds();
				if(ids.contains(user.getId())){
					node.setIconSkin("userOnline");
				}
			}
			treeNodes.add(node);
		}
		
	}

	//封装此部门下的子部门
/*	private static void addSubDepartmentsInTheDepartment(String departmentId,List<ZTreeNode> treeNodes, List<Department> subDepartments) {
		for (Department d : subDepartments) {
			List<Department> subDepts =as.getSubDepartmentList(d.getId());
			List<User> users=as.getUsersByDepartmentId(d.getId());
			if ((users != null && users.size() > 0)||(subDepts != null && subDepts.size() > 0)) {
				createDepartmentNodeOpen(d,DEPARTMENT_+departmentId,treeNodes);
			}else{
				createDepartmentNodeClose(d,DEPARTMENT_+departmentId,treeNodes);
			}
		}
	}*/
	
	
	/**
	 * 只查某一工作组下的员工(无子工作组)
	 * @param 
	 * @param 
	 * @return
	 */	
	private static Object getNodesOnExpandOneWorkgroup(long workgroupId) {
		List<ZTreeNode> treeNodes = new ArrayList<ZTreeNode>();
		
		List<User> users = as.getUsersByWorkgroupId(workgroupId);
		
		//封装员工
		createUserNode(users,WORKGROUP_+workgroupId,treeNodes);
		
		return JsonParser.object2Json(treeNodes);
	}
	/**
	 * ***部门人员树********************************************************************************************************************
	 */	
	
	/**
	 * 部门人员树
	 * MAN_DEPARTMENT_TREE
	 */
	
	public static String createDepartmentUserTree(Long companyId,String companyName,String currentId) {
		StringBuilder tree = new StringBuilder();
		String[] str = currentId.split("_");
		if (currentId.equals("0")) {
			tree.append(getInitialDepartmentUserTree(companyId,companyName,currentId));
		}else if(str[0].equals("department")||str[0].equals("branch")) {
			tree.append(getNodeInDepartment(Long.parseLong(str[1]),str[0],currentId));
		}else if(str[0].equals("userHasNotDepartment")){
			tree.append(getNodeInNoDepartment(Long.parseLong(str[1]),str[0],currentId));
		}
		return tree.toString();
	}
	//获取无部门人员下所有人员
	private static Object getNodeInNoDepartment(Long id ,String type,String currentId) {
		List<ZTreeNode> treeNodes=new ArrayList<ZTreeNode>();
		addUserNode(treeNodes,id,type, currentId);
		return JsonParser.object2Json(treeNodes);
	}

	/**
	 * 初始化部门人员树
	 * @param companyId,companyName
	 */
	private static Object getInitialDepartmentUserTree(Long companyId,String companyName,String currentId) {
		List<Department> lists=getBranchByIds();
		List<ZTreeNode> treeNodes = new ArrayList<ZTreeNode>();
		if(lists.size()==0){
		        List<Department> departments=as.getDepartments();
		        boolean companyNodeHasSubNode=false;
		        List<User> user=as.getUsersWithoutDepartment();
			    if(departments.size()>0||userWithoutDeptVisible?(user.size()>0):false){
			    	companyNodeHasSubNode=true;
			    }
				//集团节点
				createCompanyNode(companyId,companyName,treeNodes,companyNodeHasSubNode);
				//封装集团下部门和分支机构节点和无部门人员节点
				createNodeInCompany(companyId,companyName,treeNodes,currentId);
				return JsonParser.object2Json(treeNodes);
		}else{
			for(Department dept:lists){
				ZTreeNode company=null;
				if(dept.getBranch()){
					company = 
						new ZTreeNode("branch_"+dept.getId().toString(),"0", dept.getName(),"false","false",BRANCH,object2Json(dept),"branch");
				}else{
					String nodeName=dept.getName();
					if(showBranch&&ContextUtils.hasBranch()){
						dept.setName(dept.getName()+"("+dept.getSubCompanyName()+")");
					}
					company = 
						new ZTreeNode("department_"+dept.getId().toString(),"0", nodeName,"false","false",DEPARTMENT,object2Json(dept),"department");
				}
				if(haschlid(dept)){
					company.setIsParent("true");
				}else{
					company.setIsParent("false");
				}
				treeNodes.add(company);
			}
			return JsonParser.object2Json(treeNodes);
		}
	}
	/*
	 * 创建集团下部门，分支机构和无部门节点
	 */
	private static void createNodeInCompany(Long companyId,
			String companyName, List<ZTreeNode> treeNodes,String currentId) {
		List<Department> departments=as.getDepartments();
		for(Department dept:departments){
			ZTreeNode company=null;
			if(dept.getBranch()){
				company = 
					new ZTreeNode(BRANCH_+dept.getId().toString(),COMPANY_+companyId, dept.getName(),"false","false",BRANCH,object2Json(dept),BRANCH);
			}else{
				String nodeName=dept.getName();
				if(showBranch&&ContextUtils.hasBranch()){
					dept.setName(dept.getName()+"("+dept.getSubCompanyName()+")");
				}
				company = 
					new ZTreeNode(DEPARTMENT_+dept.getId().toString(),COMPANY_+companyId, nodeName,"false","false",DEPARTMENT,object2Json(dept),DEPARTMENT);
			}
			if(haschlid(dept)){
				company.setIsParent("true");
			}else{
				company.setIsParent("false");
			}
			treeNodes.add(company);
		}
		//封装无部门人员节点
		addUserHasNotDepartment(currentId,companyId.toString(),treeNodes);
	}
	private static boolean haschlid(Department dept){
		List<Department> departments=as.getSubDepartmentList(dept.getId());
		List<User> users=new ArrayList<User>();
		if(dept.getBranch()){
			if(userWithoutDeptVisible){
				users = as.getUsersWithoutBranch(dept.getId());
			}
		}else{
			users = as.getUsersByDepartmentId(dept.getId());
		}
		if(departments!=null&&departments.size()>0||users!=null&&users.size()>0){
			return true;
		}
		return false;
		
	}
	
	
	

	/**
	 * 工作组人员树(异步)
	 * MAN_DEPARTMENT_TREE
	 */
	public static String createWorkgroupUserTree(Long companyId,String companyName, String currentId) {
		// TODO Auto-generated method stub
		StringBuilder tree = new StringBuilder();
		String[] str = currentId.split("_");
		if (currentId.equals("0")) {
			tree.append(getInitialWorkgroupUserTree(currentId,companyId,companyName));
		}else if(str[0].equals("workgroup")){
			tree.append(getNodesOnExpandOneWorkgroup(Long.parseLong(str[1])));
		}
		
		return tree.toString();
	}
	
	/**
	 * 只查工作组和没有部门的用户
	 * @param onlineVisible 
	 */
	private static Object getInitialWorkgroupUserTree(String currentId,Long companyId,String companyName) {
		List<Department> lists=getBranchByIds();
		List<ZTreeNode> treeNodes = new ArrayList<ZTreeNode>();
		if(lists.size()==0){
			List<Department> depts=as.getDepartmentIfHasWorkGroup();
			List<Workgroup> workGroups = as.getWorkgroups();//得到所有一级工作组
	        
	        //判断根节点是不是父节点
	        boolean companyNodeHasSubNode=false;
		    if(depts.size()>0||workGroups.size()>0){
		    	companyNodeHasSubNode=true;
		    }
			//公司节点
		    createCompanyNode(companyId,companyName,treeNodes,companyNodeHasSubNode);
			//封装所有工作组节点
			addAllWorkgroup(companyId.toString(),treeNodes,depts,workGroups);
			return JsonParser.object2Json(treeNodes);
		}else{
			String pId="";
			List<Department> branchs=new ArrayList<Department>();
			lists=getSubBranch(branchs,lists);
			for(Department dept:branchs){
				pId=HASWORKGROUPBRANCH_+dept.getId();
				List<Workgroup> workGroups=as.getWorkgroupsByBranchId(dept.getId());
				if(workGroups.size()>0){
					treeNodes.add(new ZTreeNode(pId,"0", dept.getName(),"true","true",HASWORKGROUPBRANCH,object2Json(dept),"branch"));
				}else{
					treeNodes.add(new ZTreeNode(pId,"0", dept.getName(),"false","false",HASWORKGROUPBRANCH,object2Json(dept),"branch"));
				}
				for(Workgroup w : workGroups){
					List<User> users = as.getUsersByWorkgroupId(w.getId());
					if(workGroups != null && workGroups.size() > 0&&users != null && users.size() > 0){
						createWorkgroupNodeOpen(w,pId,treeNodes);
					}else{
						createWorkgroupNodeClose(w,pId,treeNodes);
					}
				}
			}
			return JsonParser.object2Json(treeNodes);
		}
	}
	

	private static List<Department> getSubBranch(List<Department> branchs,List<Department> lists) {
		for(Department dept:lists){
			if(dept.getBranch()){
				branchs.add(dept);
			}
			getSubBranch(branchs,as.getSubDepartmentList(dept.getId()));
		}
		return null;
	}

	/**
	 * 部门树(一下全部加载)
	 * MAN_DEPARTMENT_TREE
	 */
	public static String createDepartmentsTree(Long companyId,String companyName, String currentId) {
		StringBuilder tree = new StringBuilder();
		String[] str = currentId.split("_");
		if (currentId.equals("0")) {
			tree.append(getInitialDepartmentTree(companyId,companyName,currentId));
		}else if(str[0].equals("department")||str[0].equals("branch")) {
			tree.append(getSubDepartmentNodes(Long.parseLong(str[1]),str[0],currentId));
		}
		return tree.toString();
	}
	/*
	 * 部门树 点击部门节点时加载部门
	 */
	  private static Object getSubDepartmentNodes(long departmentId, String departmentType,String currentId) {
		  List<ZTreeNode> treeNodes = new ArrayList<ZTreeNode>();
			List<Department> departments=as.getSubDepartmentList(departmentId);
			ZTreeNode node=null;
			for(Department dept:departments){
				if(dept.getBranch()){
					node = 
						new ZTreeNode(BRANCH_+dept.getId().toString(),currentId, dept.getName(),"false","false",BRANCH,object2Json(dept),BRANCH);
				}else{
					String nodeName=dept.getName();
					if(showBranch&&ContextUtils.hasBranch()){
						dept.setName(dept.getName()+"("+dept.getSubCompanyName()+")");
					}
					node = 
						new ZTreeNode(DEPARTMENT_+dept.getId().toString(),currentId, nodeName,"false","false",DEPARTMENT,object2Json(dept),DEPARTMENT);
				}
				List<Department> depts=as.getSubDepartmentList(dept.getId());
				if(depts!=null&&depts.size()>0){
					node.setIsParent("true");
				}
				treeNodes.add(node);
			}
			return JsonParser.object2Json(treeNodes);
	}

	private static Object getInitialDepartmentTree(Long companyId,
			String companyName, String currentId) {
		List<Department> lists=getBranchByIds();
		List<ZTreeNode> treeNodes = new ArrayList<ZTreeNode>();
		if(lists.size()==0){
			  List<Department> departments=as.getDepartments();
			  boolean companyNodeHasSubNode=false;
			    if(departments.size()>0){
			    	companyNodeHasSubNode=true;
			    }
				//公司节点
			    createCompanyNode(companyId,companyName,treeNodes,companyNodeHasSubNode);
			    
				addAllDepartment(COMPANY_+companyId.toString(),treeNodes,departments);
			return JsonParser.object2Json(treeNodes);
		}else{
			for(Department d : lists){
				List<Department> subDepartments = as.getSubDepartmentList(d.getId());//得到子部门 
				if((subDepartments != null && subDepartments.size() > 0)){
					createDepartmentNodeOpen(d,"0",treeNodes);
				}else{
					createDepartmentNodeClose(d,"0",treeNodes);
				}
			}
			return JsonParser.object2Json(treeNodes);
		}
	}

	

	//当前部门下的子部门节点
	private static void addAllDepartment(String pId,List<ZTreeNode> treeNodes, List<Department> departments) {

		for(Department d : departments){
			List<Department> subDepartments = as.getSubDepartmentList(d.getId());//得到子部门 
			if((subDepartments != null && subDepartments.size() > 0)){
				createDepartmentNodeOpen(d,pId,treeNodes);
			}else{
				createDepartmentNodeClose(d,pId,treeNodes);
			}
		}
		
	}
	
	/**
	 * 工作组树
	 * GROUP_TREE
	 */

	public static String createWorkgroupsTree(Long companyId,String companyName, String currentId) {
			// TODO Auto-generated method stub
			List<Department> lists=getBranchByIds();
			List<ZTreeNode> treeNodes = new ArrayList<ZTreeNode>();
			if(lists.size()==0){
			    List<Workgroup> workGroups = as.getAllWorkgroups();
			    boolean companyNodeHasSubNode=false;
			    if(workGroups.size()>0){
			    	companyNodeHasSubNode=true;
			    }
				//公司节点
			    createCompanyNode(companyId,companyName,treeNodes,companyNodeHasSubNode);
			    //封装公司下工作组节点
			    List<Workgroup> workGroups2=as.getWorkgroups();
			    for(int i=0;i<workGroups2.size();i++){
			    	createWorkgroupNodeClose(workGroups2.get(i),COMPANY_+companyId.toString(),treeNodes);
			    }
				//封装"工作组节点"
			    List<Department> depts=as.getDepartmentIfHasWorkGroup();
				addWorkgroupExceptUser(COMPANY_+companyId,treeNodes,depts);
				return JsonParser.object2Json(treeNodes);
			}else{
				String pId="";
				List<Department> branchs=new ArrayList<Department>();
				lists=getSubBranch(branchs,lists);
				for(Department dept:branchs){
					pId=HASWORKGROUPBRANCH_+dept.getId();
					List<Workgroup> workGroups=as.getWorkgroupsByBranchId(dept.getId());
					if(workGroups.size()>0){
						treeNodes.add(new ZTreeNode(pId,"0", dept.getName(),workGroups.size()>0?"true":"false",workGroups.size()>0?"true":"false",HASWORKGROUPBRANCH,object2Json(dept),"branch"));
						for(Workgroup w : workGroups){
							createWorkgroupNodeClose(w,pId,treeNodes);
						}
					}
				}
				return JsonParser.object2Json(treeNodes);
			}
	}
	//封装工作组树的工作组节点
	private static void addWorkgroupExceptUser(String pid,List<ZTreeNode> treeNodes, List<Department> depts) {
		String pId="";
		for(Department dept:depts){
			pId=HASWORKGROUPBRANCH_+dept.getId();
			treeNodes.add(new ZTreeNode(pId,pid, dept.getName(),"true","true",HASWORKGROUPBRANCH,object2Json(dept),"branch"));
			List<Workgroup> workGroups=as.getWorkgroupsByBranchId(dept.getId());
			for(Workgroup w : workGroups){
				createWorkgroupNodeClose(w,pId,treeNodes);
			}
		}
	}
	/**
	 * 部门和工作组树(异步)
	 * DEPARTMENT_WORKGROUP_TREE
	 */
	public static String createDepartmentsAndWorkgroupsTree(Long companyId,String companyName, String currentId) {
		// TODO Auto-generated method stub
		StringBuilder tree = new StringBuilder();
		String[] str = currentId.split("_");
		if (currentId.equals("0")) {
			tree.append(getInitialDepartmentsAndWorkgroupsTree(currentId,companyId,companyName));
		}else if(str[0].equals("department")||str[0].equals("branch")) {
			tree.append(getSubDepartmentNodes(Long.parseLong(str[1]),str[0],currentId));
		}
		return tree.toString();
	}
	private static Object getInitialDepartmentsAndWorkgroupsTree(
			String currentId, Long companyId, String companyName) {
		List<ZTreeNode> treeNodes = new ArrayList<ZTreeNode>();
		List<Department> departments = getSettingDepartment(departmentShow);//得到所有一级部门
		List<Department> depts=as.getDepartmentIfHasWorkGroup();//得到所有含有工作组分支机构
		//公司节点
		createCompanyNode(companyId,companyName,treeNodes,true);
		//封装"部门"节点
		createAllDepartmentNode(companyId.toString(),treeNodes,departments);
		//封装第一层部门节点
		addAllDepartment(ALLDEPARTMENT_+companyId.toString(),treeNodes,departments);
		 //封装公司下工作组节点
	    List<Workgroup> workGroups2=as.getWorkgroups();
	    for(int i=0;i<workGroups2.size();i++){
	    	createWorkgroupNodeClose(workGroups2.get(i),ALLWORKGROUP_+companyId.toString(),treeNodes);
	    }
		//封装"工作组节点"
		createAllWorkgroupNode(companyId.toString(),treeNodes,depts);
		String pId="";
		for(Department dept:depts){
			pId=HASWORKGROUPBRANCH_+dept.getId();
			treeNodes.add(new ZTreeNode(pId,ALLWORKGROUP_+companyId, dept.getName(),"true","true",HASWORKGROUPBRANCH,object2Json(dept),"branch"));
			List<Workgroup> workGroups=as.getWorkgroupsByBranchId(dept.getId());
			for(Workgroup w : workGroups){
				createWorkgroupNodeClose(w,pId,treeNodes);
			}
		}
		return JsonParser.object2Json(treeNodes);
	}
	
	/**
	 * 得到公司节点
	 * 
	 */
	public static void createCompanyNode(Long companyId,String companyName,List<ZTreeNode> treeNodes,boolean companyNodeHasSubNode) {
		ZTreeNode company = 
			new ZTreeNode(COMPANY_+companyId.toString(), "0", companyName,"true","true",COMPANY,"{\"name\" : \""+companyName+"\" }","root");
		if(!companyNodeHasSubNode){
			company.setIsParent("false");
		}
		treeNodes.add(company);
	}
	/**
	 * 得到"部门"节点
	 * 
	 */
	public static void createAllDepartmentNode(String companyId,List<ZTreeNode> treeNodes,List<Department> departments) {
		ZTreeNode allDepartmentNode = null;
		if(departments.size()>0){
			allDepartmentNode = 
			new ZTreeNode(ALLDEPARTMENT_+companyId, COMPANY_+companyId, "部门","true","true",ALLDEPARTMENT,"{\"name\" : \"所有部门\" }",DEPARTMENT);
		}else{
			allDepartmentNode = 
			new ZTreeNode(ALLDEPARTMENT_+companyId, COMPANY_+companyId, "部门","false","false",ALLDEPARTMENT,"{\"name\" : \"所有部门\" }",DEPARTMENT);
		}
		treeNodes.add(allDepartmentNode);
	}
	
	
	/**
	 * 得到"工作组"节点
	 * 
	 */
	public static void createAllWorkgroupNode(String companyId,List<ZTreeNode> treeNodes,List<Department> depts) {
		if(depts.size()>0){
			treeNodes.add(new  ZTreeNode(ALLWORKGROUP_+companyId,COMPANY_+companyId, "工作组","true","true",ALLWORKGROUP,"{\"name\" : \"所有工作组\" }","department"));
		}else{
			treeNodes.add(new ZTreeNode(ALLWORKGROUP_+companyId,COMPANY_+companyId, "工作组","false","false",ALLWORKGROUP,"{\"name\" : \"所有工作组\" }","department"));
		}
		
	}
	
	
	/**
	 * 得到open部门节点
	 * 
	 */
	public static void createDepartmentNodeOpen(Department d,String parentId ,List<ZTreeNode> treeNodes) {
		ZTreeNode department = null;
			
			if(d.getBranch()){
				department=new ZTreeNode(BRANCH_+d.getId().toString(), parentId, d.getName(),"false","true",BRANCH,object2Json(d),BRANCH);
			}else{
				String nodeName=d.getName();
				if(showBranch&&ContextUtils.hasBranch()){
					d.setName(d.getName()+"("+d.getSubCompanyName()+")");
				}
				department=new ZTreeNode(DEPARTMENT_+d.getId().toString(), parentId,  nodeName,"false","true",DEPARTMENT,object2Json(d),DEPARTMENT);
			}
			treeNodes.add(department);
	}
	
	/**
	 * 得到close部门节点
	 * 
	 */
	private static void createDepartmentNodeClose(Department d, String parentId,List<ZTreeNode> treeNodes) {
		ZTreeNode department = null;
		if(d.getBranch()){
			department=new ZTreeNode(BRANCH_+d.getId().toString(), parentId,  d.getName(),"false","false",BRANCH,object2Json(d),BRANCH);
		}else{
			String nodeName=d.getName();
			if(showBranch&&ContextUtils.hasBranch()){
				d.setName(d.getName()+"("+d.getSubCompanyName()+")");
			}
			department=new ZTreeNode(DEPARTMENT_+d.getId().toString(), parentId,  nodeName,"false","false",DEPARTMENT,object2Json(d),DEPARTMENT);
		}
		treeNodes.add(department);
	}
	
	
	/**
	 * 得到open工作组节点
	 * 
	 */
	public static void createWorkgroupNodeOpen(Workgroup w,String parentId ,List<ZTreeNode> treeNodes) {
		ZTreeNode workgroup = 
		new ZTreeNode(WORKGROUP_+w.getId().toString(), parentId, w.getName(),"true","true",WORKGROUP,object2Json(w),WORKGROUP);
		treeNodes.add(workgroup);
	}
	
	/**
	 * 得到close工作组节点
	 * 
	 */
	private static void createWorkgroupNodeClose(Workgroup w, String parentId,List<ZTreeNode> treeNodes) {
		ZTreeNode workgroup = 
		new ZTreeNode(WORKGROUP_+w.getId().toString(), parentId, w.getName(),"false","false",WORKGROUP,object2Json(w),WORKGROUP);	
		treeNodes.add(workgroup);
	}
	
	/**
	 * 得到员工节点
	 * 
	 */
	private static void createUserNode(List<User> users,String parentId,List<ZTreeNode> treeNodes) {
		ZTreeNode userNode = null;
		//得到在线人员
		List<Long> onlineUserIds = new ArrayList<Long>();
		if(onlineVisible)
				onlineUserIds = as.getOnlineUserIds();
		
		for(User u : users){
			String nodeName=u.getName();
			if(showBranch&&ContextUtils.hasBranch()){
				u.setName(u.getName()+"("+u.getSubCompanyName()+")");
			}
			if(onlineVisible&&onlineUserIds.contains(u.getId())){//显示在线人员
				userNode = 
				new ZTreeNode(USER_+u.getId().toString(), parentId, nodeName,"false","false",USER,object2Json(u),"userOnline");
				treeNodes.add(userNode);
			}else{
				userNode = 
				new ZTreeNode(USER_+u.getId().toString(), parentId, nodeName,"false","false",USER,object2Json(u),"user");
				treeNodes.add(userNode);
			}
		}
	}
	
	/**
	 * 得到节点显示数据
	 * 
	 */
	public static String getNodeShowName(Object obj) {
		String fieldName = "";
		if(!treeNodeShowContent.equals("null")&&!StringUtils.isEmpty(treeNodeShowContent)){
			JSONArray array = JSONArray.fromObject(treeNodeShowContent);
			JSONObject jsonObj = array.getJSONObject(0);
			if(jsonObj.containsKey(USER)){
				 fieldName = jsonObj.getString(USER);
			}else if(jsonObj.containsKey(DEPARTMENT)){
				 fieldName = jsonObj.getString(DEPARTMENT);
			}else if(jsonObj.containsKey(WORKGROUP)){
				 fieldName = jsonObj.getString(WORKGROUP);
			}
		}
		
		try {
		  if(containTheField(obj,fieldName)){
		      return BeanUtils.getFieldValue(obj,StringUtils.isEmpty(fieldName)?"name":fieldName).toString();
		  }else{
			  return BeanUtils.getFieldValue(obj,"name").toString();
		  }
		   
		} catch (NoSuchFieldException e) {
			e.printStackTrace();
		}
		
		return "";
	}
	
	/**
	 * 得到节点数据
	 * 
	 */
	public static String getNodeData(Object obj) {
		String DEFAULTTREENODEDATA=null;
		StringBuilder json = new StringBuilder("{");
	    String[] str = null;
	    if(obj instanceof User){
	    	DEFAULTTREENODEDATA=USERNODEDATA;
	    }else if(obj instanceof Department){
	    	DEFAULTTREENODEDATA=DEPARTMENTNODEDATA;
	    }else if(obj instanceof Workgroup){
	    	DEFAULTTREENODEDATA=WORKGROUPNODEDATA;
	    }else{
	    	throw new RuntimeException("参数错误! 请传入User Department Workgroup 对象");
	    }
	    if(StringUtils.isEmpty(treeNodeData)||treeNodeData.equals("undefined")){
		    str = DEFAULTTREENODEDATA.split(",");
	    }else{
		    str = treeNodeData.split(",");
	    }
		
		for(int i =0;i<str.length;i++){
			try {
				if(containTheField(obj,str[i])){
					if(str[i].equals("subCompanyName")){
						Object o=BeanUtils.getFieldValue(obj,"subCompanyId");
						if(o!=null&&o instanceof Long){
							json.append("\""+str[i]+"\" : \""+as.getDepartmentById((Long)(o)).getName()+"\" ,");
						}else{
							json.append("\""+str[i]+"\" : \""+ContextUtils.getCompanyName()+"\" ,");
						}
					}else if(str[i].equals("parentDepartmentId")){
						Department d=((Department)obj).getParent();
						if(d!=null){
							json.append("\""+str[i]+"\" : \""+d.getId()+"\" ,");
						}
					}else{
						json.append("\""+str[i]+"\" : \""+BeanUtils.getFieldValue(obj, str[i])+"\" ,");
					}
				 
				}
			} catch (NoSuchFieldException e) {
				e.printStackTrace();
			}
		}
		return json.substring(0, json.length()-1)+"}";
	}
	/**
	 * 判断某个类是否含有此属性
	 * 
	 */
	private static boolean containTheField(Object obj,String fieldName) {
		Field[] fields = obj.getClass().getDeclaredFields();
		for(Field field : fields){
			if(fieldName.equals(field.getName())){
				return true;
			}
		}
		return false;
	}
	
	/**
	 * 得到树的部门
	 * 
	 */
	private static List<Department> getSettingDepartment(String departmentShow) {
		List<Department> departments = null;
		if(!StringUtils.isEmpty(departmentShow)&&!departmentShow.equals("undefined")&&!departmentShow.equals("null")){
			departments = getDepartmentByNameStr(departmentShow);
		}else{
			departments = as.getDepartments();//得到所有一级部门
		}
		return departments;
	}
	private static String object2Json(Object obj){
		return getNodeData(obj);
	}

	public static String getSearchTreeWillOpenNodes(TreeType treeType, String searchValue) {
		switch(treeType) {
	       case COMPANY:
	    	   return getMsgByCompany(searchValue);
	       case MAN_DEPARTMENT_TREE:
	    	   return getMsgByDepartmentUser(searchValue);
	       case MAN_GROUP_TREE:
	    	   return getMsgByWorkgroupUser(searchValue);
	       case DEPARTMENT_TREE:
	    	   return getMsgByDepartment(searchValue);
	       case GROUP_TREE:
	    	   return getMsgByWorkgroup(searchValue);
	       case DEPARTMENT_WORKGROUP_TREE:
	    	   return getMsgByDepartmentWorkgroup(searchValue);
	       }
		return null;
	}

	//递归查找需要展开的部门节点ID(部门树情况)
	private static boolean getWillOpenNodeByDepartment(Map<String, Integer> nodeIds, Department  dept, List<Department> ds) {
		boolean isOpen=false;
		String type=DEPARTMENT_;
		List<Department> depts=as.getSubDepartmentList(dept.getId());
		if(dept.getBranch()){
			type=BRANCH_;
		}
		if(contains_dept(depts, ds)){
			isOpen=true;
		}
		for(Department d:depts){
			if(isOpen){
				getWillOpenNodeByDepartment(nodeIds,d,ds);
			}else{
				isOpen=getWillOpenNodeByDepartment(nodeIds,d,ds);
			}
		}
		if(isOpen){
			nodeIds.put(type+dept.getId(), -1);
		}
		return isOpen;
	}
	//递归查找需要展开的部门节点ID(人员树情况)
	private static boolean getWillOpenNodeByDepartment(Map<String,Integer> nodeIds,long deptId,List<User> users,Integer index) {
		Department dept=as.getDepartmentById(deptId);
		boolean isOpen=false;
		List<Department> depts=null;
		String type=DEPARTMENT_;
		List<User> us=null;
		if(dept.getBranch()){
			type=BRANCH_;
			us=as.getUsersWithoutBranch(deptId);
			depts=as.getSubDepartmentList(deptId);
			if(userWithoutDeptVisible&&us!=null&&us.size()>0&&contains(users,us)){
				nodeIds.put(USERHASNOTDEPARTMENT_+dept.getId(),index+1);
				isOpen=true;
			}
		}else{
			us=as.getUsersByDepartmentId(deptId);
			if(us!=null&&us.size()>0&&contains(users,us)){
				isOpen=true;
			}
		}
		depts=as.getSubDepartmentList(deptId);
		for(Department d:depts){
			if(isOpen){
				getWillOpenNodeByDepartment(nodeIds,d.getId(),users,index+1);
			}else{
				isOpen=getWillOpenNodeByDepartment(nodeIds,d.getId(),users,index+1);
			}
		}
		if(isOpen){
			nodeIds.put(type+deptId,index);
		}
		return isOpen;
	}
	private static void getWillOpenNodeByWorkgroup(Map<String,Integer> nodeIds,List<User> users){
		List<Workgroup> workgroups=as.getAllWorkgroups();
		List<User> us = new ArrayList<User>();
		for(Workgroup w:workgroups){
			us=as.getUsersByWorkgroupId(w.getId());
			if(us!=null&&!us.isEmpty()&&contains(us ,users)){
				nodeIds.put(WORKGROUP_+w.getId(), -1);
			}
		}
	}

	private static boolean contains(List<User> users, List<User> us) {
		for(User user:users){
			if(us.contains(user)){
				return true;
			}
		}
		return false;
	}
	private static boolean contains_dept(List<Department> depts, List<Department> ds) {
		for(Department d:depts){
			if(ds.contains(d)){
				return true;
			}
		}
		return false;
	}
	
	/*
	 * 获取查询部门树所需节点
	 */
	private static String getMsgByDepartment(String searchValue){
		StringBuilder sb=new StringBuilder("[");
		Map<String,Integer> nodeIds=new HashMap<String,Integer>();
		List<Department> ds=as.getDepartmentsBySearchValue(searchValue);
		List<Department> depts=as.getDepartments();
		for(Department dept:depts){
			getWillOpenNodeByDepartment(nodeIds,dept,ds);
		}
		Iterator<Entry<String, Integer>> i=nodeIds.entrySet().iterator();
		sb.append("{");
		while(i.hasNext()){
			Entry<String, Integer> e=i.next();
			sb.append(e.getKey()+":"+e.getValue()+",");
		}
		if(sb.length()>2){
			sb.replace(sb.length()-1, sb.length(),"}");
		}else{
			sb.append("}");
		}
		sb.append(",{");
		for(int n=0;n<ds.size();n++){
			if(ds.get(n).getBranch()){
				sb.append(BRANCH_+ds.get(n).getId()+":-1"+",");
			}else{
				sb.append(DEPARTMENT_+ds.get(n).getId()+":-1"+",");
			}
		}
		sb.replace(sb.length()-1, sb.length(),"}");
		sb.append("]");
		return (sb.toString().equals("[{},}]"))?"[{},{}]":sb.toString();
	}
	
	/*
	 * 获取查询工作组树所需节点
	 */
	private static String getMsgByWorkgroup(String searchValue){
		List<Workgroup> wgs=as.getWorkGroupsBySearchValue(searchValue);
		StringBuilder sb=new StringBuilder("[{},{");
		for(int n=0;n<wgs.size();n++){
			sb.append(WORKGROUP_+wgs.get(n).getId()+":-1"+",");
		}
		sb.replace(sb.length()-1, sb.length(),"}");
		sb.append("]");
		return (sb.toString().equals("[{},}]"))?"[{},{}]":sb.toString();
	}
	/*
	 * 获取查询工作组部门树所需节点
	 */
	private static String getMsgByDepartmentWorkgroup(String searchValue){
		StringBuilder sb=new StringBuilder("[");
		Map<String,Integer> nodeIds=new HashMap<String,Integer>();
		List<Department> ds=as.getDepartmentsBySearchValue(searchValue);
		List<Workgroup> wgs=as.getWorkGroupsBySearchValue(searchValue);
		List<Department> depts=as.getDepartments();
		for(Department dept:depts){
			getWillOpenNodeByDepartment(nodeIds,dept,ds);
		}
		Iterator<Entry<String, Integer>> i=nodeIds.entrySet().iterator();
		sb.append("{");
		while(i.hasNext()){
			Entry<String, Integer> e=i.next();
			sb.append(e.getKey()+":"+e.getValue()+",");
		}
		if(sb.length()>2){
			sb.replace(sb.length()-1, sb.length(),"}");
		}else{
			sb.append("}");
		}
		sb.append(",{");
		for(int n=0;n<ds.size();n++){
			if(ds.get(n).getBranch()){
				sb.append(BRANCH_+ds.get(n).getId()+":-1"+",");
			}else{
				sb.append(DEPARTMENT_+ds.get(n).getId()+":-1"+",");
			}
		}
		for(int n=0;n<wgs.size();n++){
			sb.append(WORKGROUP_+wgs.get(n).getId()+":-1"+",");
		}
		sb.replace(sb.length()-1, sb.length(),"}");
		sb.append("]");
		
		return (sb.toString().equals("[{},}]"))?"[{},{}]":sb.toString();
	}
	/*
	 * 获取查询公司树所需节点
	 */
	private static String getMsgByCompany(String searchValue){
		StringBuilder sb=new StringBuilder("[");
		Map<String,Integer> nodeIds=new HashMap<String,Integer>();
		List<User> users=as.getUsersBySearchValue(searchValue);
		List<Department> depts=as.getDepartments();
		for(Department dept:depts){
			getWillOpenNodeByDepartment(nodeIds,dept.getId(),users,1);
		}
		List<User> nodeDeptUsers=as.getUsersWithoutDepartment();
		if(userWithoutDeptVisible&&nodeDeptUsers!=null&&!nodeDeptUsers.isEmpty()&&contains(users,nodeDeptUsers)){
			nodeIds.put(USERHASNOTDEPARTMENT_+ContextUtils.getCompanyId(), 0);
		}
		getWillOpenNodeByWorkgroup(nodeIds,users);
		Iterator<Entry<String, Integer>> i=nodeIds.entrySet().iterator();
		sb.append("{");
		while(i.hasNext()){
			Entry<String, Integer> e=i.next();
			sb.append(e.getKey()+":"+e.getValue()+",");
		}
		if(sb.length()>2){
			sb.replace(sb.length()-1, sb.length(),"}");
		}else{
			sb.append("}");
		}
		sb.append(",{");
		for(int n=0;n<users.size();n++){
			sb.append(USER_+users.get(n).getId()+":-1"+",");
		}
		sb.replace(sb.length()-1, sb.length(),"}");
		sb.append("]");
		return (sb.toString().equals("[{},}]"))?"[{},{}]":sb.toString();
	}
	/*
	 * 获取查询部门人员树所需节点
	 */
	private static String getMsgByDepartmentUser(String searchValue){
		StringBuilder sb=new StringBuilder("[");
		Map<String,Integer> nodeIds=new HashMap<String,Integer>();
		List<User> users=as.getUsersBySearchValue(searchValue);
		List<Department> depts=as.getDepartments();
		for(Department dept:depts){
			getWillOpenNodeByDepartment(nodeIds,dept.getId(),users,1);
		}
		List<User> nodeDeptUsers=as.getUsersWithoutDepartment();
		if(userWithoutDeptVisible&&nodeDeptUsers!=null&&!nodeDeptUsers.isEmpty()&&contains(users,nodeDeptUsers)){
			nodeIds.put(USERHASNOTDEPARTMENT_+ContextUtils.getCompanyId(), 0);
		}
		Iterator<Entry<String, Integer>> i=nodeIds.entrySet().iterator();
		sb.append("{");
		while(i.hasNext()){
			Entry<String, Integer> e=i.next();
			sb.append(e.getKey()+":"+e.getValue()+",");
		}
		if(sb.length()>2){
			sb.replace(sb.length()-1, sb.length(),"}");
		}else{
			sb.append("}");
		}
		sb.append(",{");
		for(int n=0;n<users.size();n++){
			sb.append(USER_+users.get(n).getId()+":-1"+",");
		}
		sb.replace(sb.length()-1, sb.length(),"}");
		sb.append("]");
		return (sb.toString().equals("[{},}]"))?"[{},{}]":sb.toString();
	}
	/*
	 * 获取查询工作组人员树所需节点
	 */
	private static String getMsgByWorkgroupUser(String searchValue){
		StringBuilder sb=new StringBuilder("[");
		Map<String,Integer> nodeIds=new HashMap<String,Integer>();
		List<User> users=as.getUsersBySearchValue(searchValue);
		getWillOpenNodeByWorkgroup(nodeIds,users);
		Iterator<Entry<String, Integer>> i=nodeIds.entrySet().iterator();
		sb.append("{");
		while(i.hasNext()){
			Entry<String, Integer> e=i.next();
			sb.append(e.getKey()+":"+e.getValue()+",");
		}
		if(sb.length()>2){
			sb.replace(sb.length()-1, sb.length(),"}");
		}else{
			sb.append("}");
		}
		sb.append(",{");
		for(int n=0;n<users.size();n++){
			sb.append(USER_+users.get(n).getId()+":-1"+",");
		}
		sb.replace(sb.length()-1, sb.length(),"}");
		sb.append("]");
		return (sb.toString().equals("[{},}]"))?"[{},{}]":sb.toString();
	}
}
