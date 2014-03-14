package com.norteksoft.acs.web.organization;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.struts2.convention.annotation.ParentPackage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Required;

import com.norteksoft.acs.base.enumeration.TreeType;
import com.norteksoft.acs.base.web.struts2.CRUDActionSupport;
import com.norteksoft.acs.entity.authorization.BranchAuthority;
import com.norteksoft.acs.entity.organization.Company;
import com.norteksoft.acs.entity.organization.Department;
import com.norteksoft.acs.entity.organization.User;
import com.norteksoft.acs.entity.organization.Workgroup;
import com.norteksoft.acs.service.authorization.BranchAuthorityManager;
import com.norteksoft.acs.service.authorization.RoleManager;
import com.norteksoft.acs.service.organization.CompanyManager;
import com.norteksoft.acs.service.organization.DepartmentManager;
import com.norteksoft.acs.service.organization.UserManager;
import com.norteksoft.acs.service.organization.WorkGroupManager;
import com.norteksoft.product.util.ContextUtils;
import com.norteksoft.product.util.JsonParser;
import com.norteksoft.product.util.TreeUtils;
import com.norteksoft.product.util.tree.ZTreeNode;
import com.norteksoft.tags.tree.DepartmentDisplayType;


@SuppressWarnings("deprecation")
@ParentPackage("default")
public class LoadTreeAction extends CRUDActionSupport<Company> {
	private static final long serialVersionUID = 1L;
	private CompanyManager companyManager;
	private DepartmentManager departmentManager;
	private WorkGroupManager workGroupManager;
	private RoleManager roleManager;
	private String currentId;
	private String treeType;
	private boolean systemAdminable = false;//是否是系统管理员
	
	//"-"--->"|#"
	private static String SPLIT_ONE="|#";
	//"="--->"=="
	private static String SPLIT_TWO="==";
	//"~"--->"*#"
	private static String SPLIT_THREE="*#";
	
	@Autowired
	private BranchAuthorityManager branchAuthorityManager;
	@Autowired
	private UserManager userManager;
	
	public String loadWorkgroupTree(){
		List<ZTreeNode> treeNodes = new ArrayList<ZTreeNode>();
		String result ="";
		User user=userManager.getUserById(ContextUtils.getUserId());
		if(roleManager.hasSystemAdminRole(user)){
			ZTreeNode root = new ZTreeNode("COMPANY-" + ContextUtils.getCompanyId(),"0",ContextUtils.getCompanyName(), "true", "false", "", "", "root", "");
			treeNodes.add(root);
			getSubBranches(null,treeNodes,"COMPANY-" + ContextUtils.getCompanyId());
		}else if(roleManager.hasBranchAdminRole(user)){
			StringBuilder nodes = new StringBuilder();
			List<BranchAuthority> branches=branchAuthorityManager.getBranchByUser(ContextUtils.getUserId());
			for(BranchAuthority branch:branches){
				if(StringUtils.isNotEmpty(nodes.toString())){
					nodes.append(",");
				}
				Department d=departmentManager.getDepartment(branch.getBranchesId());
				List<Department> subDepartments = departmentManager.getSubDeptments(d.getId());
				if(subDepartments != null && subDepartments.size()>0 && haveBranchesValidate(d.getId())){
					String nodeId="BRANCHES-" + d.getId();
					ZTreeNode root = new ZTreeNode(nodeId,"0",d.getName(), "true", "false", "", "", "root", "");
					treeNodes.add(root);
					getSubBranches(d.getId(),treeNodes,nodeId);
				}else{
					String nodeId="BRANCHES-" + d.getId();
					ZTreeNode root = new ZTreeNode(nodeId,"0",d.getName(), "true", "false", "", "", "root", "");
					treeNodes.add(root);
					getWorkGroupNodes(d.getId(),treeNodes,nodeId);
				}
			}
		}
		result = JsonParser.object2Json(treeNodes);
		renderText(result);
		return null;
	}
	
	private void getSubBranches(Long departmentId,List<ZTreeNode> treeNodes,String parentId) {
		List<Department> departments = new ArrayList<Department>();
		if(departmentId==null){
			departments = departmentManager.getRootDepartment();
			getWorkGroupNodes(null,treeNodes,parentId);
		}else{
			departments = departmentManager.getSubDeptments(departmentId);
			Department d=departmentManager.getDepartment(departmentId);
			if(d.getBranch()){
				getWorkGroupNodes(departmentId,treeNodes,parentId);
			}
		}
		for(Department d:departments){
			if(d.getBranch()){
				List<Department> subDepartments = departmentManager.getSubDeptments(d.getId());
				if(subDepartments != null && subDepartments.size()>0 && haveBranchesValidate(d.getId())){
					String nodeId="BRANCHES-" + d.getId();
					ZTreeNode root = new ZTreeNode(nodeId,parentId,d.getName(), "true", "false", "", "", "root", "");
					treeNodes.add(root);
					getSubBranches(d.getId(),treeNodes,nodeId);
				}else{
					String nodeId="BRANCHES-" + d.getId();
					ZTreeNode root = new ZTreeNode(nodeId,parentId,d.getName(), "true", "false", "", "", "root", "");
					treeNodes.add(root);
					getWorkGroupNodes(d.getId(),treeNodes,nodeId);
				}
			}else{
				boolean haveBranches=haveBranchesValidate(d.getId());
				if(haveBranches){
					String nodeId="DEPARTMENT-" + d.getId();
					ZTreeNode root = new ZTreeNode(nodeId,parentId,StringUtils.isNotEmpty(d.getShortTitle())?d.getShortTitle():d.getName(), "true", "false", "", "", "department", "");
					treeNodes.add(root);
					getSubBranches(d.getId(),treeNodes,nodeId);
				}
			}
		}
	}
	
	/**
	 * 判断departmentId（部门id或分支机构id）中是否含有子分支机构，如果有返回true,否则返回false
	 * @param departmentId
	 * @return
	 */
	private boolean haveBranchesValidate(Long departmentId) {
		boolean haveBranches=false;
		List<Department> subBranches=departmentManager.getSubDeptments(departmentId);
		for(Department depart:subBranches){
			if(haveBranches){
				haveBranches=true;
				break;
			}
			if(depart.getBranch()){
				haveBranches=true;
				break;
			}else{
				haveBranches=haveBranchesValidate(depart.getId());
			}
		}
		return haveBranches;
	}
	
	/**
	 * 以公司为根节点的树
	 * @return
	 */
	public String loadDepartmentTree(){
		List<ZTreeNode> treeNodes = new ArrayList<ZTreeNode>();
		String result ="";
		if(currentId == null || currentId.trim().length() <= 0) return null;
		//初始化时显示公司根节点和工作站根节点
		Company company = companyManager.getCompany(ContextUtils.getCompanyId());
		boolean hasBranch = roleManager.hasBranchAdminRole(ContextUtils.getUserId());
		if("INITIALIZED".equals(currentId)){//部门管理左侧树
			boolean systemAdminable = roleManager.hasSystemAdminRole(ContextUtils.getUserId());
			if(systemAdminable){//如果是系统管理员且不是分支管理员
//				sb.append(JsTreeUtils.generateJsTreeNodeNew("DEPARTMENTS-" + company.getId(), "open", company.getName(), getDepartmentNodes(company.getId()), "company"));
				ZTreeNode root = new ZTreeNode("DEPARTMENTS-" + company.getId(),"0",company.getName(), "true", "false", "", "", "department", "");
				treeNodes.add(root);
				getDepartmentNodes(company.getId(),treeNodes,"DEPARTMENTS-" + company.getId());
			}else if(hasBranch){//如果不是系统管理员且是分支管理员
				generateBranchTree(treeNodes);
			}
		}else if("INITIALIZED_USERS".equals(currentId)){//用户管理左侧树
			boolean adminable = roleManager.hasAdminRole(ContextUtils.getUserId());
			if(adminable&&!hasBranch){//如果是管理员且不是分支管理员
				generateAdminTree(company,"true",treeNodes);
			}else if(adminable&&hasBranch){//如果是管理员且是分支管理员
				generateAdminTree(company,"false",treeNodes);
				generateBranchUserTree(treeNodes);
			}else if(!adminable&&hasBranch){//如果不是管理员且是分支管理员
				generateBranchUserTree(treeNodes);
			}
		}
		result = JsonParser.object2Json(treeNodes);
		renderText(result);
		return null;
	}
	
	private void generateAdminTree(Company company,String state,List<ZTreeNode> treeNodes){
		String nodeId="DEPARTMENTS-" + company.getId();
		ZTreeNode root = new ZTreeNode(nodeId,"0",company.getName(), state, "false", "", "", "root", "");
		List<ZTreeNode> children=new ArrayList<ZTreeNode>();
		getDepartmentNodesUser(company,children);
		ZTreeNode nodepartmentUser = new ZTreeNode("NODEPARTMENT_USER-"+ company.getId(),nodeId,getText("user.noDepartment"), "false", "false", "", "", "department", "");
		children.add(nodepartmentUser);
		ZTreeNode deletedUser = new ZTreeNode("DELETED_USER-" + company.getId(),nodeId,getText("common.userDelete"), "false", "false", "", "", "department", "");
		children.add(deletedUser);
		root.setChildren(children);
		treeNodes.add(root);
	}
	
	/**
	 * 生成公司的子公司及部门的树
	 * @param companyId
	 */
	public void getDepartmentNodesUser(Company company,List<ZTreeNode> treeNodes){
		for(Company comp : company.getChildren()){
			ZTreeNode root = new ZTreeNode("DEPARTMENTS-"+comp.getId().toString(),"",comp.getName(), "false", "false", "", "", "root", "");
			treeNodes.add(root);
		}
		List<Department> departments = departmentManager.getAllDepartment();
		for(Department d : departments){
			getDepartmentsNodesUser(d, false,treeNodes);
		}
	}
	
	private void generateBranchUserTree(List<ZTreeNode> treeNodes){
		List<BranchAuthority> branchAuthoritys = branchAuthorityManager.getBranchByUser(ContextUtils.getUserId());
		int i=0;
		for(BranchAuthority b : branchAuthoritys ){
			Department d = departmentManager.getDepartment(b.getBranchesId());
			if(i==0){
				getBranchsNodesUser(d, false,false,treeNodes);
			}else{
				getBranchsNodesUser(d, false,true,treeNodes);	
			}
			i++;
		}
	}
	
	private void generateBranchTree(List<ZTreeNode> treeNodes){
		List<BranchAuthority> branchAuthoritys = branchAuthorityManager.getBranchByUser(ContextUtils.getUserId());
		int i=0;
		for(BranchAuthority b : branchAuthoritys ){
			Department d = departmentManager.getDepartment(b.getBranchesId());
			if(i==0){
				getBranchsNodes(d, false,false,treeNodes,"0");
			}else{
				getBranchsNodes(d, false,true,treeNodes,"0");	
			}
			i++;
		}
	}
	
	/**
	 * 部门树
	 * @return
	 */
	public String loadDepartment(){
		boolean hasBranch = roleManager.hasBranchAdminRole(ContextUtils.getUserId());
		boolean adminable = roleManager.hasSystemAdminRole(ContextUtils.getUserId());
		List<ZTreeNode> treeNodes = new ArrayList<ZTreeNode>();
		
		
		
		if(currentId == null || currentId.trim().length() <= 0) return null;
		//初始化时显示公司根节点和工作站根节点
		Company company = companyManager.getCompany(ContextUtils.getCompanyId());
		if(adminable){//如果是系统管理员且不是分支管理员
			loadDepartmentGenerateAdminTree(company,treeNodes);
		}else if(hasBranch){//如果不是管理员且是分支管理员
			loadDepartmentGenerateBranchTree(treeNodes);
		}
		this.renderText(JsonParser.object2Json(treeNodes));
		return null;
	}
	private String loadDepartmentGenerateAdminTree(Company company,List<ZTreeNode> treeNodes){
		List<ZTreeNode> children = new ArrayList<ZTreeNode>();
		StringBuilder sb = new StringBuilder();
		if("INITIALIZED".equals(currentId)){
			ZTreeNode root = new ZTreeNode("DEPARTMENTS"+SPLIT_ONE+ company.getId()+SPLIT_TWO+company.getName(),"0",company.getName(), "true", "false", "", "", "root", "");
			getDepartmentNodes2(company.getId(),children);
			root.setChildren(children);
			treeNodes.add(root);
		}
		return sb.toString();
	}
	
	private void loadDepartmentGenerateBranchTree(List<ZTreeNode> treeNodes){
		List<BranchAuthority> branchAuthoritys = branchAuthorityManager.getBranchByUser(ContextUtils.getUserId());
		int i=0;
		for(BranchAuthority b : branchAuthoritys ){
			Department d = departmentManager.getDepartment(b.getBranchesId());
			if(i==0){
				getBranchsNodesChoose(d, false,false,treeNodes);
			}else{
				getBranchsNodesChoose(d, false,true,treeNodes);
			}
			i++;
		}
	}
	
	/**
	 * 生成公司的子公司及部门的树
	 * @param companyId
	 */
	public void getDepartmentNodes(Long companyId,List<ZTreeNode> treeNodes,String parentId){
		Company company = companyManager.getCompany(companyId);
		for(Company comp : company.getChildren()){
			ZTreeNode root = new ZTreeNode("DEPARTMENTS-"+comp.getId().toString(),parentId,comp.getName(), "false", "false", "", "", "root", "");
			treeNodes.add(root);
		}
		List<Department> departments = departmentManager.getAllDepartment();
		for(Department d : departments){
			getDepartmentsNodes(d, false,true,treeNodes,parentId);
		}
	}
	
	private void getDepartmentNodes2(Long companyId,List<ZTreeNode> treeNodes){
		Company company = companyManager.getCompany(companyId);
		for(Company comp : company.getChildren()){
			ZTreeNode root = new ZTreeNode("DEPARTMENTS"+SPLIT_ONE+comp.getId().toString()+SPLIT_TWO+comp.getName(),"",comp.getName(), "false", "false", "", "", "department", "");
			treeNodes.add(root);
		}
		List<Department> departments = departmentManager.getAllDepartment();
		for(Department d : departments){
			 getDepartmentsNodes2(d, false,treeNodes);
		}
	}
	//系统管理员看到的部门树
	private void getDepartmentsNodes(Department dept, boolean isSubDept,boolean isClosed,List<ZTreeNode> treeNodes,String parentId){
		if(!(dept.getParent() != null && !isSubDept)){
			List<Department> subDepts = departmentManager.getSubDeptments(dept.getId());
			if(subDepts.size() > 0){
				ZTreeNode root = new ZTreeNode((dept.getBranch()?"USERSBYBRANCH-":"USERSBYDEPARTMENT-")+dept.getId()+"="+dept.getSubCompanyId(),parentId,dept.getName(), isClosed?"false":"true", "false", "", "",  dept.getBranch()?"root":"department", "");
				treeNodes.add(root);
				for(Department d : subDepts){
					if(d.isDeleted()) continue;
					getDepartmentsNodes(d, true,isClosed,treeNodes,(dept.getBranch()?"USERSBYBRANCH-":"USERSBYDEPARTMENT-")+dept.getId()+"="+dept.getSubCompanyId());
				}
			}else{
				ZTreeNode root = new ZTreeNode((dept.getBranch()?"USERSBYBRANCH-":"USERSBYDEPARTMENT-")+dept.getId()+"="+dept.getSubCompanyId(),parentId,dept.getName(), "false", "false", "", "",  dept.getBranch()?"root":"department", "");
				treeNodes.add(root);
			}
		}
	}
	//分支机构管理员看到的部门树
	private void getBranchsNodes(Department dept, boolean isSubDept,boolean isClosed,List<ZTreeNode> treeNodes,String parentId){
		List<Department> subDepts = departmentManager.getSubDeptments(dept.getId());
		if(subDepts.size() > 0){
			ZTreeNode root = new ZTreeNode((dept.getBranch()?"USERSBYBRANCH-":"USERSBYDEPARTMENT-")+dept.getId()+"="+dept.getSubCompanyId(),parentId,dept.getName(), isClosed?"false":"true", "false", "", "", dept.getBranch()?"root":"department", "");
			treeNodes.add(root);
			for(Department d : subDepts){
				if(d.isDeleted()) continue;
				getBranchsNodes(d, true,isClosed,treeNodes,(dept.getBranch()?"USERSBYBRANCH-":"USERSBYDEPARTMENT-")+dept.getId()+"="+dept.getSubCompanyId());
			}
		}else{
			ZTreeNode root = new ZTreeNode((dept.getBranch()?"USERSBYBRANCH-":"USERSBYDEPARTMENT-")+dept.getId()+"="+dept.getSubCompanyId(),parentId,dept.getName(), "false", "false", "", "", dept.getBranch()?"root":"department", "");
			treeNodes.add(root);
		}
	}
	
	private void getBranchsNodesChoose(Department dept, boolean isSubDept,boolean isClosed,List<ZTreeNode> treeNodes){
		List<Department> subDepts = departmentManager.getSubDeptments(dept.getId());
		if(subDepts.size() > 0){
			//部门树节点
			String nodeId = (dept.getBranch()?"USERSBYBRANCH":"USERSBYDEPARTMENT")+SPLIT_ONE+dept.getId()+SPLIT_TWO+dept.getName()+SPLIT_THREE+dept.getSubCompanyId();
			ZTreeNode root = new ZTreeNode(nodeId,"",dept.getName(), isClosed?"false":"true", "false", "", "", dept.getBranch()?"branch":"department", "");
			List<ZTreeNode> children = new ArrayList<ZTreeNode>();
			//子部门树节点列表
			for(Department d : subDepts){
				if(d.isDeleted()) continue;
				getBranchsNodesChoose(d, true,isClosed,children);
			}
			root.setChildren(children);
			treeNodes.add(root);
		}else{
			ZTreeNode root = new ZTreeNode((dept.getBranch()?"USERSBYBRANCH":"USERSBYDEPARTMENT")+SPLIT_ONE+dept.getId()+SPLIT_TWO+dept.getName()+SPLIT_THREE+dept.getSubCompanyId()
					,"",dept.getName(), "false", "false", "", "", dept.getBranch()?"branch":"department", "");
			treeNodes.add(root);
		}
	}
	
	//分支机构管理员看到的部门树(带无部门节点和已删除节点的部门树)
	private void getBranchsNodesUser(Department dept, boolean isSubDept,boolean isClosed,List<ZTreeNode> treeNodes){
		List<Department> subDepts = departmentManager.getSubDeptments(dept.getId());
		if(subDepts.size() > 0){
			//部门树节点
			String nodeId=(dept.getBranch()?"USERSBYBRANCH-":"USERSBYDEPARTMENT-")+dept.getId();
			ZTreeNode root = new ZTreeNode(nodeId,"",dept.getName(), isClosed?"false":"true", "false", "", "", dept.getBranch()?"root":"department", "");
			List<ZTreeNode> children=new ArrayList<ZTreeNode>();
			
			//子部门树节点列表
			for(Department d : subDepts){
				if(d.isDeleted()) continue;
				getBranchsNodesUser(d, true,isClosed,children);
			}
			
			//无部门节点和已删除节点
			//判断是否是分支机构，如果是则拼无部门节点和已删除节点
		    if(dept.getBranch()){
		    	ZTreeNode root1 = new ZTreeNode("BRANCH_NODEPARTMENT_USER-"+ dept.getId(),"",getText("user.noDepartment"), "false", "false", "", "", "department", "");
		    	children.add(root1);
				ZTreeNode root2 = new ZTreeNode("BRANCH_DELETED_USER-" + dept.getId(),"",getText("common.userDelete"), "false", "false", "", "", "department", "");
				children.add(root2);
		    }
		    root.setChildren(children);
		    treeNodes.add(root);
		}else{
			String nodeId=(dept.getBranch()?"USERSBYBRANCH-":"USERSBYDEPARTMENT-")+dept.getId();
			ZTreeNode root = new ZTreeNode(nodeId,"",dept.getName(), "false", "false", "", "", dept.getBranch()?"root":"department", "");
			List<ZTreeNode> children=new ArrayList<ZTreeNode>();
			getBranchExtraFolder(dept,children);
			root.setChildren(children);
			treeNodes.add(root);
		}
	}
	//带无部门节点和已删除节点的部门树
	private void getDepartmentsNodesUser(Department dept, boolean isSubDept,List<ZTreeNode> treeNodes){
		if(!(dept.getParent() != null && !isSubDept)) {
			List<Department> subDepts = departmentManager.getSubDeptments(dept.getId());
			if(subDepts.size() > 0){
				//部门树节点
				String nodeId=(dept.getBranch()?"USERSBYBRANCH-":"USERSBYDEPARTMENT-")+dept.getId();
				ZTreeNode root = new ZTreeNode(nodeId,"",dept.getName(), "false", "false", "", "", dept.getBranch()?"root":"department", "");
				List<ZTreeNode> children=new ArrayList<ZTreeNode>();
				
				//子部门树节点列表
				for(Department d : subDepts){
					if(d.isDeleted()) continue;
					getDepartmentsNodesUser(d, true,children);
				}
				//无部门节点和已删除节点
				//判断是否是分支机构，如果是则拼无部门节点和已删除节点
				if(dept.getBranch()){
					ZTreeNode root1 = new ZTreeNode("BRANCH_NODEPARTMENT_USER-"+ dept.getId(),"",getText("user.noDepartment"), "false", "false", "", "", "department", "");
					children.add(root1);
					ZTreeNode root2 = new ZTreeNode("BRANCH_DELETED_USER-" + dept.getId(),"",getText("common.userDelete"), "false", "false", "", "", "department", "");
					children.add(root2);
				}
				root.setChildren(children);
				treeNodes.add(root);
			}else{
				String nodeId=(dept.getBranch()?"USERSBYBRANCH-":"USERSBYDEPARTMENT-")+dept.getId();
				ZTreeNode root = new ZTreeNode(nodeId,"",dept.getName(), "false", "false", "", "", dept.getBranch()?"root":"department", "");
				List<ZTreeNode> children=new ArrayList<ZTreeNode>();
				getBranchExtraFolder(dept,children);
				root.setChildren(children);
				treeNodes.add(root);
			}
		}
	}
	private void getBranchExtraFolder(Department dept,List<ZTreeNode> treeNodes){
		if(dept.getBranch()){
			ZTreeNode root = new ZTreeNode("BRANCH_NODEPARTMENT_USER-"+ dept.getId(),"",getText("user.noDepartment"), "false", "false", "", "", "department", "");
			treeNodes.add(root);
			root = new ZTreeNode("BRANCH_DELETED_USER-" + dept.getId(),"",getText("common.userDelete"), "false", "false", "", "", "department", "");
			treeNodes.add(root);
	    }
	}
	private void getDepartmentsNodes2(Department dept, boolean isSubDept,List<ZTreeNode> treeNodes){
		if(dept.getParent() != null && !isSubDept) return ;
		List<Department> subDepts = departmentManager.getSubDeptments(dept.getId());
		if(subDepts.size() > 0){
			//部门树节点
			String nodeId = (dept.getBranch()?"USERSBYBRANCH":"USERSBYDEPARTMENT")+SPLIT_ONE+dept.getId()+SPLIT_TWO+dept.getName()+SPLIT_THREE+dept.getSubCompanyId();
			ZTreeNode root = new ZTreeNode(nodeId,"",dept.getName(), "false", "false", "", "", dept.getBranch()?"branch":"department", "");
			//子部门树节点列表
			List<ZTreeNode> children = new ArrayList<ZTreeNode>();
			for(Department d : subDepts){
				if(d.isDeleted()) continue;
				getDepartmentsNodes2(d, true,children);
			}
			root.setChildren(children);
			treeNodes.add(root);
		}else{
			ZTreeNode root = new ZTreeNode((dept.getBranch()?"USERSBYBRANCH":"USERSBYDEPARTMENT")+SPLIT_ONE+dept.getId()+SPLIT_TWO+dept.getName()+SPLIT_THREE+dept.getSubCompanyId(),
					"",dept.getName(), "false", "false", "", "", dept.getBranch()?"branch":"department", "");
			treeNodes.add(root);
		}
	}
	
	@Required
	public void setDepartmentManager(DepartmentManager departmentManager) {
		this.departmentManager = departmentManager;
	}
	
	public void getWorkGroupNodes(Long branchesId,List<ZTreeNode> treeNodes,String parentId){
		List<Workgroup> workGroups = workGroupManager.queryWorkGroupByBranches(branchesId);
		for(Workgroup wg: workGroups){
			if(wg.isDeleted()) continue;
			ZTreeNode root = new ZTreeNode("USERSBYWORKGROUP-"+wg.getId().toString(),parentId,wg.getName(), "false", "false", "", "", "workgroup", "");
			treeNodes.add(root);
		}
	}
	
	
	
	//公司人员树
	public String createManCompanyTree() throws Exception {
		renderText(TreeUtils.getCreateManCompanyTree(ContextUtils.getCompanyId(), ContextUtils.getCompanyName(), currentId,false,DepartmentDisplayType.NAME,false,""));
		return null;
	}
	
	
	
	//部门工作组人员树
	public String createManDepartmentGroupTree(){
		renderText(TreeUtils.getCreateManDepartmentGroupTree(ContextUtils.getCompanyId(),  currentId,false,DepartmentDisplayType.NAME,false,""));
		return null;
	}
	
	
	
	//部门人员树
	public String createManDepartmentTree(){
		renderText(TreeUtils.getCreateManDepartmentTree(ContextUtils.getCompanyId(),  currentId,false,DepartmentDisplayType.NAME,false,""));
		return null;
	}
	//工作组人员树
	public String createManGroupTree(){
		renderText(TreeUtils.getCreateManGroupTree(ContextUtils.getCompanyId(),  currentId,false,""));
		return null;
		
	}
	//部门树
	public String createDepartmentTree(){
		renderText(TreeUtils.getCreateDepartmentTree(ContextUtils.getCompanyId(),  currentId,DepartmentDisplayType.NAME,""));
		return null;
	}
	//工作组树
	public String createGroupTree(){
		renderText(TreeUtils.getCreateGroupTree(ContextUtils.getCompanyId(),  currentId,""));
		return null;
	}	
	
	/*public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {    
        ((HttpServletResponse)response).setHeader("Pragma","No-cache");     
        ((HttpServletResponse)response).setHeader("Cache-Control","no-cache");     
        ((HttpServletResponse)response).setHeader("Expires","0");    
        chain.doFilter(request, response);    
    }*/
	
	//标签树
	public String getTree(){
	
		 switch(TreeType.valueOf(treeType)) {
	       case COMPANY:
	    	   renderText(TreeUtils.getCreateManCompanyTree(ContextUtils.getCompanyId(), ContextUtils.getCompanyName(), currentId,false,DepartmentDisplayType.NAME,false,""));
	    	   break;
	       case MAN_DEPARTMENT_GROUP_TREE:
	    	   renderText(TreeUtils.getCreateManDepartmentGroupTree(ContextUtils.getCompanyId(),  currentId,false,DepartmentDisplayType.NAME,false,""));
	          break;
	       case MAN_DEPARTMENT_TREE:
	    	   renderText(TreeUtils.getCreateManDepartmentTree(ContextUtils.getCompanyId(),  currentId,false,DepartmentDisplayType.NAME,false,""));
	          break;
	       case MAN_GROUP_TREE:
	    	   renderText(TreeUtils.getCreateManGroupTree(ContextUtils.getCompanyId(),  currentId,false,""));
	           break;
	       case DEPARTMENT_TREE:
	    	   renderText(TreeUtils.getCreateDepartmentTree(ContextUtils.getCompanyId(),  currentId,DepartmentDisplayType.NAME,""));
	         break;
	       case GROUP_TREE:
	    	   renderText(TreeUtils.getCreateGroupTree(ContextUtils.getCompanyId(),  currentId,""));
	          break;
	       
	       default:  return renderText(TreeUtils.getCreateManCompanyTree(ContextUtils.getCompanyId(), ContextUtils.getCompanyName(), currentId,false,DepartmentDisplayType.NAME,false,""));
	       }
		
		return null;
	}
	
	
	
	
	
	
	
	
	
	
	

	public String getCurrentId() {
		return currentId;
	}

	public void setCurrentId(String currentId) {
		this.currentId = currentId;
	}

	@Required
	public void setCompanyManager(CompanyManager companyManager) {
		this.companyManager = companyManager;
	}
  
	@Required
	public void setWorkGroupManager(WorkGroupManager workGroupManager) {
		this.workGroupManager = workGroupManager;
	}
	
	@Required
    public void setRoleManager(RoleManager roleManager) {
		this.roleManager = roleManager;
	}

	// 继承自父类的方法=======================================================================
	@Override
	public String delete() throws Exception {
		return null;
	}
	
	@Override
	public String list() throws Exception {
		return null;
	}
	
	@Override
	protected void prepareModel() throws Exception {
		
	}
	
	@Override
	public String save() throws Exception {
		return null;
	}
	
	public Company getModel() {
		return null;
	}

	public String getTreeType() {
		return treeType;
	}

	public void setTreeType(String treeType) {
		this.treeType = treeType;
	}

	public boolean isSystemAdminable() {
		return systemAdminable;
	}
	
	
}
