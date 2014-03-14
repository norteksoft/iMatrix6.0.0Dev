<%@ page contentType="text/html;charset=UTF-8"%>
<%@ include file="/common/mms-taglibs.jsp"%>

<div id="accordion" >
	<h3><a href="${mmsCtx}/module/menu-tree.htm" id="menu_manage">菜单管理</a></h3>
	<div>
		<div class="demo" id="menu_manage_content" style="margin-top: 10px;"></div>
	</div>
	<h3><a href="${mmsCtx}/module/operation.htm" id="operation_manage">通用类别管理</a></h3>
	<div>
		<div class="ztree" id="operation_manage_content"></div>
	</div>
</div>

<script type="text/javascript">
	$(function () {
		$("#accordion").accordion({fillSpace:true, change:accordionChange});
	});
	function accordionChange(event,ui){
		var url=ui.newHeader.children("a").attr("href");
		if(url=="${mmsCtx}/module/menu-tree.htm"){
			$("#myIFrame").attr("src",ui.newHeader.children("a").attr("href"));
		}else{
			createSystemTree("operation_manage_content");
			$("#myIFrame").attr("src",ui.newHeader.children("a").attr("href"));
		}
	}

	//创建页面树菜单
	function createSystemTree(treeId){
		$.ajaxSetup({cache:false});
		//treeId:,url:,data(静态树才需要该参数):,multiple:,callback:
		tree.initTree({treeId:treeId,
			url:"${mmsCtx}/module/operation-system-tree.htm",
			type:"ztree",
			initiallySelectFirstChild:true,
			callback:{
					onClick:selectNode
				}});
	}
	function selectNode(){
		var currentId = tree.getSelectNodeId();
		var treeId = tree.treeId;
		$("#myIFrame").attr("src","${mmsCtx}/module/operation.htm?systemId="+currentId);
	}
</script>

