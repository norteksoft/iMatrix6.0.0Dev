<%@ page contentType="text/html;charset=UTF-8"%>
<%@ include file="/common/acs-taglibs.jsp"%>

<script type="text/javascript">
$(function () {
	$("#log_accordion").accordion({fillSpace:true, change:accordionChange});
});

function accordionChange(event,ui){
	var url=ui.newHeader.children("a").attr("href");
	if(url=="log-data.action"){
		initSystemTree();
	}
	$("#myIFrame").attr("src",url);
}

function initSystemTree(){
	$.ajaxSetup({cache:false});
	tree.initTree({treeId:"auth_role_tree",
		url:"${acsCtx}/authorization/role-systemTree.action?moduleType=log&currentId=INITIALIZED_USERS",
		type:"ztree",
		initiallySelect:"BUSINESSSYSTEM_${businessSystemId}",
		initiallySelectFirstChild:true,
		callback:{
				onClick:selectNode
			}});
}

function selectNode(obj){
	var currentId = tree.getSelectNodeId();
	loadContent(currentId);
}
function loadContent(treeId){
	var index = treeId.indexOf("_");
	if($('#firstLoading').attr('value')=='0'){
		$("#myIFrame").attr("src","${acsCtx}/log/log-data.action?sysId="+treeId.substring(index+1, treeId.length));
		$('#firstLoading').attr('value', '1');
	}else{
		loadTreeContent(treeId.substring(0, index), treeId.substring(index+1, treeId.length));
	}
}
function loadTreeContent(type, id){
	$("#myIFrame").attr("src","${acsCtx}/log/log-data.action?sysId="+id);
}
</script>
<div id="log_accordion" class="basic">

	<security:authorize ifAnyGranted="log_data">
	<h3><a href="log-data.action" id="_system_log">系统日志</a></h3>
	<div>
		<ul class="ztree" id="auth_role_tree"></ul>
	</div>
	</security:authorize>
	<security:authorize ifAnyGranted="userLoginLog">
	<h3><a href="log!lookUserLoginLog.action" id="_login_log">登录日志</a></h3>
	<div> 
	</div>
	</security:authorize>
	
</div>