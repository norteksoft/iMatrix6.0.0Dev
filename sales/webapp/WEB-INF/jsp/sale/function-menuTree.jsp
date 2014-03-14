<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3c.org/TR/1999/REC-html401-19991224/loose.dtd">
<%@ page language="java"  pageEncoding="UTF-8"%>
<%@ include file="/common/taglibs.jsp"%>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
<title><s:text name="functionGroup.functionGroupManager"/>(list)</title>
<%@ include file="/common/meta.jsp"%>
	<link   type="text/css" rel="stylesheet" href="${ctx}/widgets/colorbox/colorbox.css" />
	<script type="text/javascript" src="${ctx}/widgets/colorbox/jquery.colorbox.js"></script>
	<script>
	var msg="${msg}";
	var currentClickNode=null;
	$(function createMenuTree(){
		var setting = {
				async : {
					enable: true,
					url: "${ctx}/sale/function!getMenuTree.action?systemId=${systemId}"
				},
				data: {
					simpleData: {
						enable: true
					}
				},
				callback : {
					onClick:onClick
					//onDrop:onDrop,
					//beforeDrop:beforeDrop,
					//onAsyncSuccess:onAsyncSuccess
				}
			};
		$.fn.zTree.init($("#menu-tree"), setting);
	});
	function addFunctionsToMenu(){
		if(currentClickNode&&currentClickNode.level>0){
			$.ajax({
				data:{msg:msg,menuId:currentClickNode.id,systemId:"${systemId}"},
				cache:false,
				type:"post",
				url:"${ctx}/sale/function!addFunctionsToMenu.action",
				success:function(data){
					window.parent.location="function-group!reload.action?systemId=${systemId}";
					window.parent.tb_remove();
					
				},
				error:function(){
					alert("合并失败!");
				}
			});
		}else{
			alert("请选择有效节点进行合并!");
			return;
		}
	}
	function onClick(event, treeId, treeNode){
		currentClickNode=treeNode;
	}
	</script>
</head>
<body>
<div class="opt-body">
	<div class="cbox-btn">
		<table>
		<tr>
		<td style="width:80px;">
			<button type='button' class="btn" onclick="addFunctionsToMenu()" id="ok"><span><span>确定</span></span></button>
		</td>
		</tr>
		</table>
    </div>
    <div id="opt-content">
	<div id="tabs" style="height:350px;">
	<div id="tabs-1">
	<ul id="menu-tree" class="ztree"></ul>
	</div>
	</div>
	</div>
</div>
</body>
</html>