<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3c.org/TR/1999/REC-html401-19991224/loose.dtd">
<%@ page language="java" pageEncoding="UTF-8"%>
 <%@ include file="/common/taglibs.jsp"%>
<html>
<head>
<title><s:text name="function.functionManager"/>(list)</title>
<%@ include file="/common/meta.jsp"%>
<script type="text/javascript">
//翻页
var nodeList;
var curDragNodes;
var currentClickNode;
function pageFunctionNext(){
	$("#page_form").submit();
}
function pageFunctionPre(){
	$("#pagepre_form").submit();
}
var setting = {
		async : {
			enable: true,
			url: "${ctx}/sale/function!getTree.action?systemId=${systemId}"
		},
		edit : {
			drag: {
				autoExpandTrigger: true,
				prev: dropPrev,
				inner: dropInner,
				next: dropNext
			
			},
			enable: true,
			showRemoveBtn : false,
			showRenameBtn : false
		},
		check: {
			autoCheckTrigger : true,
			chkboxType : {"Y": "s", "N": "ps"},
			chkStyle : "checkbox",
			enable : true,
			nocheckInherit: false
		},
		data: {
			simpleData: {
				enable: true
			}
		},
		view: {
			fontCss: getFontCss
		},
		callback : {
			beforeAsync : null,
			beforeCheck : null,
			beforeClick : null,
			beforeCollapse : null,
			beforeDblClick : null,
			beforeDrag : beforeDrag,
			beforeDragOpen : beforeDragOpen,
			beforeDrop : beforeDrop,
			beforeEditName : null,
			beforeExpand : null,
			beforeMouseDown : null,
			beforeMouseUp : null,
			beforeRemove : null,
			beforeRename : null,
			beforeRightClick : null,
			onAsyncError : onAsyncError,
			onAsyncSuccess : null,
			onCheck : onCheck,
			onClick : onClick,
			onCollapse : null,
			onDblClick : null,
			onDrag : onDrag,
			onDrop : onDrop,
			onExpand : onExpand,
			onMouseDown : null,
			onMouseUp : null,
			onNodeCreated : null,
			onRemove : null,
			onRename : null,
			onRightClick : null
		}
	};
	function onClick(event, treeId, treeNode){
		currentClickNode=treeNode;
	}
	function onCheck(event, treeId, treeNode){
	}
	function searchNode(e) {
		var ok=false;
		updateNodes(false);
		var zTree = $.fn.zTree.getZTreeObj("treeDemo");
		var code=$("#code").val().replace(/(^\s*)|(\s*$)/g, "");
		var name=$("#name").val().replace(/(^\s*)|(\s*$)/g, "");
		var path=$("#path").val().replace(/(^\s*)|(\s*$)/g, "");
		var searchValue={};
		if(code!=""){
			searchValue["code"]=code;
		}
		if(name!=""){
			searchValue["name"]=name;
		}
		if(path!=""){
			searchValue["path"]=path;
		}
		nodeList = zTree.getNodesByFilter(function(node){
			for(var key in searchValue){
				if(searchValue[key]!=""){
					if(node[key]){
						if(node[key].indexOf(searchValue[key])==-1){
							return false;
						}
					}else{
						return false;
					}
					ok=true;
				}
			}
			return ok;
		});
		
		updateNodes(true);
	}
	function updateNodes(highlight) {
		if(nodeList){
			var zTree = $.fn.zTree.getZTreeObj("treeDemo");
			for( var i=0, l=nodeList.length; i<l; i++) {
				var aObj = $.fn.zTree._z.tools.$(nodeList[i], "_a", zTree.setting);//获得当前选中的a标签，$.fn.zTree._z.tools.$是ztree源码中的方法
				if(highlight){
					aObj.attr("style","color: rgb(166, 0, 0); font-weight: bold");
				}else{
					aObj.attr("style","color:#333;font-weight:normal;");
				}
			}
		}
	}

	function getFontCss(treeId, treeNode) {
		return (!!treeNode.highlight) ? {color:"#A60000", "font-weight":"bold"} : {color:"#333", "font-weight":"normal"};
	}
	
	function onAsyncError(event, treeId, treeNode, XMLHttpRequest, textStatus, errorThrown) {
		var zTree = $.fn.zTree.getZTreeObj("treeDemo");
		zTree.updateNode(treeNode);
	}
	
	function filter(node) {
	    return (node.checked == true && !node.getCheckStatus().half);
	}
	$(function(){
		$.fn.zTree.init($("#treeDemo"), setting);
	});
	
	function addFunction(){
		var treeObj = $.fn.zTree.getZTreeObj("treeDemo");
		var nodes = treeObj.getSelectedNodes();
		if(nodes.length>1){
			alert("请选中一条!");	
			return;
		}
		var checkNodetype="";
		var parentId="0";
		if(currentClickNode){
			var checkNodetype=currentClickNode.type;
			if(checkNodetype=='system'||checkNodetype=='void'){
				var parentId="0";
			}else{
				var parentId=currentClickNode.id;
			}
		}
		init_tb("${ctx}/sale/function!input.action?paternId="+parentId+"&checkNodetype="+checkNodetype+"&systemId=${systemId}&TB_iframe=true&width=400&height=150","添加资源");
	}
	function editFuncion(){
		var treeObj = $.fn.zTree.getZTreeObj("treeDemo");
		var nodes = treeObj.getSelectedNodes();
		if(nodes.length>1){
			alert("请选中一条!");	
			return;
		}
		if(!currentClickNode||(currentClickNode.type!="function"&&currentClickNode.type!="menu")){
			alert("请选择有效节点");
			return;
		}
		var functionId=currentClickNode.id;
		init_tb("${ctx}/sale/function!input.action?id="+functionId+"&TB_iframe=true&width=400&height=150","修改资源");
	}
	function delFunction(){
		var treeObj = $.fn.zTree.getZTreeObj("treeDemo");
		var nodes=treeObj.getNodesByFilter(filter);
		if(nodes&&nodes.length==0){
			alert("请勾择有效节点!");
			return;
		}
		var ids="";
		for(var i=0;i<nodes.length;i++){
			ids=ids+nodes[i].id+((i==nodes.length-1)?"":",");
		}
		$("#ids").attr("value",ids);
		if(window.confirm("确认删除?（删除时会将子节点删除）")){
			ajaxSubmit("inputForm","${ctx}/sale/function!delete.action","",delFunCallback);
		}
	}
	function refreshTree(){
		currentClickNode=null;
		var treeObj = $.fn.zTree.getZTreeObj("treeDemo");
		treeObj.reAsyncChildNodes(null, "refresh");
	}
	function delFunCallback(){
		var treeObj = $.fn.zTree.getZTreeObj("treeDemo");
		var nodes = treeObj.getNodesByFilter(filter); 
		for (var i=0, l=nodes.length; i < l; i++) {
			treeObj.removeNode(nodes[i]);
		}
	}
	function beforeDrag(treeId, treeNodes) {
		for (var i=0,l=treeNodes.length; i<l; i++) {
			if (treeNodes[i].drag == "false") {
				return false;
			}
		}
		return true;
	}
	function beforeDragOpen(treeId, treeNode) {
	}
	function beforeDrop(treeId, treeNodes, targetNode, moveType, isCopy) {
		if(targetNode==null){
			return false;
		}
		curDragNodes=null;
		if(targetNode){
			if(targetNode.type!="menu"){
				if(moveType=="inner"){
					if(targetNode.type=="system"){
						for(var i=0;i<treeNodes.length;i++){
							if(treeNodes[i].type=="function"){
								alert("非菜单资源不能移动到根目录下");
								return false;
							}
						}
					}else if(targetNode.type=="function"||targetNode.type=="void"){
						for(var i=0;i<treeNodes.length;i++){
							if(treeNodes[i].type=="menu"){
								alert("菜单资源不能移动到非菜单资源下");
								return false;
							}
						}
					}
				}else if(moveType=="prev"||moveType=="next"){
					var parent=targetNode.getParentNode();
					if(parent&&parent.type=="function"){
						for(var i=0;i<treeNodes.length;i++){
							if(treeNodes[i].type=="menu"){
								alert("菜单资源不能移动到非菜单资源下");
								return false;
							}
						}
					}else if(parent&&parent.type=="system"){
						for(var i=0;i<treeNodes.length;i++){
							if(treeNodes[i].type=="function"){
								alert("非菜单资源不能移动到根目录下");
								return false;
							}
						}
					}else if(targetNode.type=="function"){
						if(getRoot(targetNode).type=="void"){
							for(var i=0;i<treeNodes.length;i++){
								if(treeNodes[i].type=="menu"){
									alert("菜单资源不能移动到非菜单资源下");
									return false;
								}
							}
						}
					}
				}
			}else{
				if(moveType=="prev"||moveType=="next"){
					var parent=targetNode.getParentNode();
					if(parent&&parent.type=="function"){
						for(var i=0;i<treeNodes.length;i++){
							if(treeNodes[i].type=="menu"){
								alert("菜单资源不能移动到非菜单资源下");
								return false;
							}
						}
					}else if(parent&&parent.type=="system"){
						for(var i=0;i<treeNodes.length;i++){
							if(treeNodes[i].type=="function"){
								alert("非菜单资源不能移动到根目录下");
								return false;
							}
						}
					}
				}
			}
		}
		curDragNodes = treeNodes;
		return true;
	}
	function getRoot(node){
		var parent=node.getParentNode();
		return parent==null?node:getRoot(parent);
	}
	function onDrag(event, treeId, treeNodes) {
	}
	function onDrop(event, treeId, treeNodes, targetNode, moveType, isCopy) {
		var treeObj = $.fn.zTree.getZTreeObj("treeDemo");
		var msg="";
		var ids="";
		for(var i=0;i<treeNodes.length;i++){
			if(treeNodes[i].level==0){
				return false;
			}
			ids=ids+treeNodes[i].id+((i==treeNodes.length-1)?"":",");
			msg=msg+treeNodes[i].id+","+treeObj.getNodeIndex(treeNodes[i])+","+treeNodes[i].level+(i==treeNodes.length-1?"":";");
		}
		if(targetNode){
			$.ajax({
				data:{msg:msg,targetId:targetNode.id,moveType:moveType,systemId:"${systemId}"},
				cache:false,
				type:"post",
				url:"${ctx}/sale/function!moveNode.action",
				success:function(data){
				},
				error:function(){
				}
		    });
		}
	}
	function onExpand(event, treeId, treeNode) {
	}
	function dropPrev(treeId, nodes, targetNode) {
		var pNode = targetNode.getParentNode();
		if (pNode && pNode.dropInner === "false") {
			return false;
		}
		if(targetNode.dropPrev==="false"){
			return false;
		}
		return true;
	}
	function dropInner(treeId, nodes, targetNode) {
		if (targetNode && targetNode.dropInner === "false") {
			return false;
		} 
		return true;
	}
	function dropNext(treeId, nodes, targetNode) {
		var pNode = targetNode.getParentNode();
		if (pNode && pNode.dropInner === "false") {
			return false;
		}
		if (targetNode && targetNode.dropNext === "false") {
			return false;
		} 
		return true;
	}

	function back(){
		window.history.go(-1);
	}
	function asyncMenuFunction(){
		if(window.confirm("确认更新?")){
			$.ajax({
				data:{systemId:"${systemId}"},
				cache:false,
				type:"post",
				url:"${ctx}/sale/function!asyncMenu.action",
				success:function(data){
					if(data=="ok"){
						alert("更新成功!");
					}else{
						alert("更新失败!");
					}
					
				}
		    });
		}
	}

	$(function(){
		$("#treeDemo").height($("#col3").height()-150);
	});
</script>
</head>
<body> 
<div class="page_margins">
  <div class="page">
 		<%@ include file="/menus/header.jsp"%>
		<%@ include file="/menus/second_menu.jsp"%>
		<div id="main">
		<%@ include file="/menus/left_menu.jsp"%>
    	<div id="col3" style="margin: 0 0 0 150px;">
    		<form name="inputForm" action="${ctx}/sale/function!delete.action" method="post">
    		<input id="ids" type="hidden" name="ids" value="" /> 
    		</form>
    		<div class="widget-place" id="widget-place-1">
    			<div class="widget" id="identifierwidget-1">
					<div class="widget-header">
						<h5><s:text name="function.functionList"/> </h5>
					</div>
						<div class="widget-content">
						     <div id="message"><s:actionmessage theme="mytheme"/></div>
				             <div id="search">
							   <form action="function!search.action" method="post">
							   		<input type="hidden" name="systemId" value="${systemId}"/>
							       <table>
								          <tr>
								               <td><s:text name="function.functionCode"/></td><td><input type="text" id="code" name="code"/>&nbsp;&nbsp;&nbsp;</td>
								               <td><s:text name="function.functionName"/></td><td><input type="text" id="name" name="name"/>&nbsp;&nbsp;&nbsp;</td>
								               <td><s:text name="function.functionPath"/></td><td><input type="text" id="path" name="path"/></td>
								               <td align="center" colspan="4"><p class="buttonP"><input class="btnStyle" type="button" value='<s:text name="common.search" />' onclick="searchNode()"/>&nbsp;<input class="btnStyle" type="reset" value='<s:text name="common.reset"/>'/></p></td>
								          </tr>
								          <tr>
							          		<p>
                               					<input class="btnStyle" type="button" value='新建资源' onclick="addFunction()"/>
                               					<input class="btnStyle" type="button" value='修改资源' onclick="editFuncion()"/>
                               					<input class="btnStyle" type="button" value='删除资源' onclick="delFunction()"/>
                               					<input class="btnStyle" type="button" value='更新菜单' onclick="asyncMenuFunction()"/>
                               					<input class="btnStyle" type="button" value='返回' onclick="back()"/>
                          					</p>
								          </tr>
							        </table>
							    </form>
							</div>
							<div id="content">
								<ul id="treeDemo" class="ztree" style="width:98%; overflow-y:auto;overflow-x:hidden; border:1px;"></ul>
							</div>
						</div>
					<b class="xbottom"><b class="xb5"></b><b class="xb4"></b><b class="xb3"></b><b class="xb2"></b><b class="xb1"></b></b>
				</div>
			</div>
    	</div>
    </div>
  </div>
</div>
</body>
</html>