<%@ page contentType="text/html;charset=UTF-8"%>
<%@ include file="/common/acs-taglibs.jsp"%>
<div id="accordion" >
	<h3><a href="fast-permission-data.htm" id="permission-manager">快速授权</a></h3>
	<div>
		<ul class="ztree" id="fast_permission_content"></ul>
	</div>
	<h3><a href="permission-data.htm" id="permission-manager">数据授权</a></h3>
	<div>
		<ul class="ztree" id="permission-manager_content"></ul>
	</div>
		
	<h3><a href="data-rule.htm" id="data-rule-manager">数据分类</a></h3>
	<div>
		<ul class="ztree" id="data-rule-manage_content"></ul>
	</div>
</div>
<style>
</style>
<script type="text/javascript">
	$(function () {
		$("#accordion").accordion({fillSpace:true, change:accordionChange});
	});
	function accordionChange(event,ui){
		var url=ui.newHeader.children("a").attr("href");
		if(url=="fast-permission-data.htm"){
			ruleTypeDataTree('fast_permission_content','/authority/data-rule-type-tree.htm');
		}else if(url=="permission-data.htm"){
			ruleTypeDataTree('permission-manager_content','/authority/system-data-rule-tree.htm');
		}else if(url=="data-rule.htm"){
			ruleTypeDataTree('data-rule-manage_content','/authority/data-rule-type-tree.htm');
		}
		$("#myIFrame").attr("src",url);
	}

	function ruleTypeDataTree(treeId,url){
		$.ajaxSetup({cache:false});
		tree.initTree({treeId:treeId,
			url:webRoot+url,
			type:"ztree",
			initiallySelectFirstChild:true,
			callback:{
					onClick:selectNode
				}});
	}

	function selectNode(){
		var currentId = tree.getSelectNodeId();
		var treeId=tree.treeId;
		if(treeId=='fast_permission_content'){
			if(currentId.indexOf("menuId_")>=0){
				var menuId=currentId.substring(currentId.indexOf("_")+1);
				$("#myIFrame").attr("src","${acsCtx}/authority/fast-permission-data.htm?sysMenuId="+menuId);
			}else{
				$("#myIFrame").attr("src","${acsCtx}/authority/fast-permission-data.htm");
			}
		}
		if(treeId=='permission-manager_content'){
			if(currentId.indexOf("menuId_")>=0){
				var menuId=currentId.substring(currentId.indexOf("_")+1);
				$("#myIFrame").attr("src","${acsCtx}/authority/permission-data.htm?sysMenuId="+menuId);
			}else if(currentId.indexOf("dataRuleId_")>=0){
				var dataRuleId=currentId.substring(currentId.indexOf("_")+1);
				$("#myIFrame").attr("src","${acsCtx}/authority/permission-data.htm?dataRuleId="+dataRuleId);
			}else{
				$("#myIFrame").attr("src","${acsCtx}/authority/permission-data.htm");
			}
		}
		if(treeId=='data-rule-manage_content'){
			if(currentId.indexOf("menuId_")>=0){
				var menuId=currentId.substring(currentId.indexOf("_")+1);
				$("#myIFrame").attr("src","${acsCtx}/authority/data-rule.htm?sysMenuId="+menuId);
			}else{
				$("#myIFrame").attr("src","${acsCtx}/authority/data-rule.htm");
			}
		}
	}
</script>
