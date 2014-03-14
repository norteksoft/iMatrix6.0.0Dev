<%@ page contentType="text/html;charset=UTF-8"%>
<%@ include file="/common/acs-taglibs.jsp"%>
<%@ page import="org.ajaxanywhere.AAUtils"%> 
<script type="text/javascript">
$(function () {
	$.ajaxSetup({cache:false});
	initUserTree();
	});

$(function () {
	$("#accordion1").accordion({fillSpace:true, change:accordionChange});
});
function accordionChange(event,ui){
	var url=ui.newHeader.children("a").attr("href");
	if(url=="user.action"){//用户管理
       	initUserTree();
	}else if(url=="department!list.action"){//部门管理
		initDepartmentTree();
	}else if(url=="work-group.action"){//工作组管理
		initWorkgroupTree("");
	}
	$("#myIFrame").attr("src",url);

}                                  
/**
 * 初始化用户树
 */
function initUserTree(){
	$.ajaxSetup({cache:false});
	tree.initTree({treeId:"company_user",
		url:"${acsCtx}/organization/load-tree!loadDepartmentTree.action?currentId=INITIALIZED_USERS",
		type:"ztree",
		initiallySelectFirstChild:false,
		initiallySelectFirst:true,
		callback:{
				onClick:selectUserNode,
				onAsyncSuccess:settingOType
			}});
}

function settingOType(){
	$("#oType").attr("value","company");
}
function reinitUserTree(){
	jQuery.jstree._reference("#company_user").destroy();
	initUserTree();
}
// 用户树选择事件
function selectUserNode(){
	var node = tree.getSelectNodeId();
	if(node=='undefined'||node==''){ return; }
	var id = node.split('-')[1];
	var type = node.split('-')[0];
	var url="";
	if (type == 'USERSBYDEPARTMENT'||type=='USERSBYBRANCH') {//部门或者分支机构
		$("#comy").attr("value","");
		$("#departId").attr("value",id);
		if(type=='USERSBYBRANCH'){
			 url = '${acsCtx}/organization/user!getUserByDepartment.action?oldDid='+id+"&oldType="+type+"&departmentId="+id+"&fromBracnhId="+id;
		}else{
		     url = '${acsCtx}/organization/user!getUserByDepartment.action?oldDid='+id+"&oldType="+type+"&departmentId="+id;
		}
	}else if (type == 'DELETED_USER') {//已删除（总公司）
		$("#comy").attr("value","");
		$("#departId").attr("value",'');
		url = '${acsCtx}/organization/user!deleteList.action?oldType='+type;
	}else if(type=='NODEPARTMENT_USER'){//无部门（总公司）
		url = '${acsCtx}/organization/user!getNoDepartmentUsers.action?comy='+"company"+"&oldType="+type+"&departmType=NODEPARTMENT";
	}else if(type=='DEPARTMENTS'){//总公司节点
		url = '${acsCtx}/organization/user!getUserByCompany.action?comy='+"company"+"&oldType="+type;
	}else if(type=='BRANCH_NODEPARTMENT_USER'){//无部门（分支机构）
		url = '${acsCtx}/organization/user!getNoDepartmentUsers.action?comy='+"company"+"&oldType="+type+"&departmType=NOBRANCH"+"&branchId="+id;
	}else if(type=='BRANCH_DELETED_USER'){//已删除（分支机构）
		$("#comy").attr("value","");
		$("#departId").attr("value",'');
		url = '${acsCtx}/organization/user!deleteList.action?oldType='+type+"&departmType=DELETEDBRANCH"+"&branchId="+id;
	}
	$("#myIFrame").attr("src",url);
}

function initDepartmentTree(){
	$.ajaxSetup({cache:false});
	tree.initTree({treeId:"company_department",
		url:"${acsCtx}/organization/load-tree!loadDepartmentTree.action?currentId=INITIALIZED",
		type:"ztree",
		initiallySelect:getdeptId,
		initiallySelectFirst:true,
		initiallySelectFirstChild:false,
		callback:{
				onClick:selectDeptNode
			}});	
}
function getdeptId(){
	if('${departmentId}' == '' || '${departmentId}' == 'null'){
		return "DEPARTMENTS-${companyId}";
	}else{
		return "USERSBYDEPARTMENT-${departmentId}";
	}
}
function selectDeptNode(){
	var node = tree.getSelectNodeId();
	if(node=='undefined'||node==''){ return; }
	var id = node.split('-')[1].split('=')[0];
	var type = node.split('-')[0];
	var url="";
	if (type == 'USERSBYDEPARTMENT'||type=='USERSBYBRANCH') {
		url=webRoot+"/organization/department.action?departmentId="+id+"&treeSelectedNode="+node;
	}else if(type=='DEPARTMENTS'){
		$('#tree_selected_id').attr('value', '');
		url=webRoot+"/organization/department.action?treeSelectedNode="+node;
	}
	$("#myIFrame").attr("src",url);
}

function initWorkgroupTree(selectNodeId){
	$.ajaxSetup({cache:false});
	tree.initTree({treeId:"company_group",
		url:"${acsCtx}/organization/load-tree!loadWorkgroupTree.action?currentId=INITIALIZED",
		type:"ztree",
		initiallySelectFirstChild:false,
		initiallySelectFirst:true,
		callback:{
				onClick:selectWorkgroupNode
			}});
}

function selectWorkgroupNode(){
	var node = tree.getSelectNodeId();
	if(node=='undefined'||node==''){ return; }
	var id = node.split('-')[1];
	var type = node.split('-')[0];
	$("#_wf_type").attr("value",type);
	$('#_wf_Id').attr('value', id);
	var url="";
	if(type=='COMPANY'){
		url=webRoot+"/organization/work-group.action?wfType="+type;
	}else if(type=='BRANCHES'){
		$("#branchesId").attr("value",id);
		url=webRoot+"/organization/work-group.action?branchesId="+id+"&wfType="+type;
	}else if(type=='DEPARTMENT'){
		url=webRoot+"/organization/work-group.action?wfType="+type;
	}else if(type=='USERSBYWORKGROUP'){
		url=webRoot+"/organization/work-group!getUserByWorkGroup.action?workGroupId="+id;
	}
	$("#myIFrame").attr("src",url);
}
function getTree(){
	
	return "USERSBYBRANCH-49";
}
</script>
<div id="accordion1" class="basic">
	<security:authorize ifAnyGranted="userList">
		<h3><a href="user.action" id="_user" >用户管理</a></h3>
		<div>
			<ul class="ztree" id="company_user"></ul>
		</div>
	</security:authorize>
	<security:authorize ifAnyGranted="departmentManager">
		<h3><a href="department!list.action" id="_department" >部门管理</a></h3>
		<div>
			<ul class="ztree" id="company_department"></ul>
		</div>
	</security:authorize>
	<security:authorize ifAnyGranted="listWorkGroup">
		<h3><a href="work-group.action" id="_group" >工作组管理</a></h3>
		<div>
			<ul class="ztree" id="company_group"></ul>
		</div>
	</security:authorize>
</div>