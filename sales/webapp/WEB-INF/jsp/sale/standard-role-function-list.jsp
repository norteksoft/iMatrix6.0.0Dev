<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3c.org/TR/1999/REC-html401-19991224/loose.dtd">
<%@ page language="java" import="com.norteksoft.acs.entity.authorization.*"
	pageEncoding="UTF-8"%>
<%@ include file="/common/taglibs.jsp"%>
<%     
  response.setHeader("Pragma","No-cache");     
  response.setHeader("Cache-Control","no-cache");     
  response.setDateHeader("Expires",   0);     
  %>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
<title><s:text name="function.functionManager"/>(list)</title>
<%@ include file="/common/meta.jsp"%>
<link rel="stylesheet" type="text/css" href="${ctx}/widgets/jquerytree/tree_component.css" />
<script type="text/javascript" src="${ctx}/widgets/jquerytree/_lib/css.js"></script>
<script type="text/javascript" src="${ctx}/widgets/jquerytree/tree_component.js"></script>
<script src="${ctx}/widgets/jquery-form/jquery.form.js" type="text/javascript"></script>
<script type="text/javascript">
$(document).ready(function (){
	createMenuTree();
});
var currentMenuId;
var currentTitle;
var currentNode;
var nodeList;
function onClick(event, treeId, treeNode){
	if(treeNode){
		currentNode=treeNode;
		currentMenuId=treeNode.id;
		currentTitle=treeNode.name;
	}
}
function onAsyncError(event, treeId, treeNode, XMLHttpRequest, textStatus, errorThrown) {
	var zTree = $.fn.zTree.getZTreeObj("menu-tree");
	
	zTree.updateNode(treeNode);
}
function filter(node) {
    return (node.checked == true && !node.getCheckStatus().half);
}
function refreshTree(){
	currentClickNode=null;
	var treeObj = $.fn.zTree.getZTreeObj("menu-tree");
	treeObj.reAsyncChildNodes(null, "refresh");
}

function getFontCss(treeId, treeNode) {
	return (!!treeNode.highlight) ? {color:"#A60000", "font-weight":"bold"} : {color:"#333", "font-weight":"normal"};
}
function searchNode(e) {
	var ok=false;
	updateNodes(false);
	var zTree = $.fn.zTree.getZTreeObj("menu-tree");
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
		var zTree = $.fn.zTree.getZTreeObj("menu-tree");
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
//创建菜单树
function createMenuTree(chirldId){
	var roleId=$("#roleId").val();
	var isAddOrRomove="${isAddOrRomove}";
	var setting = {
			async : {
				enable: true,
				url: "${ctx}/sale/standard-role!functionTree.action?roleId="+roleId+"&isAddOrRomove="+isAddOrRomove
			},
			check: {
				autoCheckTrigger : true,
				chkboxType : {"Y": "s", "N": "s"},
				chkStyle : "checkbox",
				enable : true,
				nocheckInherit: false
			},
			view: {
				fontCss: getFontCss
			},
			data: {
				simpleData: {
					enable: true
				}
			},
			callback : {
				onClick:onClick
				//onDrop:onDrop,
				//beforeDrop:beforeDrop
			}
		};
	$.fn.zTree.init($("#menu-tree"), setting);
}
function multipleTree(){
	$.ajaxSetup({cache:false});
	$("#functionTree").tree({
		data:{
		    type:"json",
		    url: "${ctx}/sale/standard-role!functionTree.action?roleId="+roleId,
		    async:true,
		    async_data:function (NODE){ return {currentId:$(NODE).attr("id") || 0}}
				  },
		rules : {
	            droppable : [ "tree-drop" ],
	            multiple : true,
	            deletable : "all",
	            draggable : "all"
	    },
		ui : {
			theme_name : "checkbox",
			context:[]
		},
		callback : { 
		    onselect    : function(NODE,TREE_OBJ) {},
			onchange : function (NODE, TREE_OBJ) {
				if(TREE_OBJ.settings.ui.theme_name == "checkbox") {
					var state;
					var $this = $(NODE).is("li") ? $(NODE) : $(NODE).parent();
					if($this.children("a.unchecked").size() == 0) {
						TREE_OBJ.container.find("a").addClass("unchecked");
					}
					$this.children("a").removeClass("clicked");
					if($this.children("a").hasClass("checked")) {
						$this.find("li").andSelf().children("a").removeClass("checked").removeClass("undetermined").addClass("unchecked");
						state = 0;
					}
					else {
						var isAddOrRomove=$("#isAddOrRomove").attr("value");
						
						if(isAddOrRomove==0){
						var str = $(NODE).attr("id").split("_");
						if(str[0]=="functionsChecked"){
							alert("资源已选！");
							selectSubDeptUser(NODE,TREE_OBJ,$this);
						}else if(str[0]=="functionChecked"){
							alert("资源已选！");
						}else if(str[0]=="functionGroup"){
							//alert("对不起！不能选择");
							alert("不能全选, 请选择资源组中的'未选资源'！");

						}else {
							$this.find("li").andSelf().children("a").removeClass("unchecked").removeClass("undetermined").addClass("checked");
							state = 1;
						}
						}else if(isAddOrRomove==1){
							var str = $(NODE).attr("id").split("_");
							if(str[0]=="functionsUnchecked"){
								alert("资源已移除！");
								selectSubDeptUser(NODE,TREE_OBJ,$this);
							}else if(str[0]=="functionUnchecked"){
								alert("资源已移除！");
							}else if(str[0]=="functionGroup"){
								//alert("对不起！不能选择");
								alert("不能全选, 请选择资源组中的'已选资源'！");

							}else {
								$this.find("li").andSelf().children("a").removeClass("unchecked").removeClass("undetermined").addClass("checked");
								state = 1;
							}


							}
					}
					$this.parents("li").each(function () { 
						if(state == 1) {
									if($(this).find("a.unchecked, a.undetermined").size() + 1 > 0) {
										$(this).parents("li").andSelf().children("a").removeClass("unchecked").removeClass("checked").addClass("undetermined");
										return false;
									}
									else $(this).children("a").removeClass("unchecked").removeClass("undetermined").addClass("checked");		
							
							
						}
						else {
							if($(this).find("a.checked, a.undetermined").size() - 1 > 0) {
								$(this).parents("li").andSelf().children("a").removeClass("unchecked").removeClass("checked").addClass("undetermined");
								return false;
							}
							else $(this).children("a").removeClass("checked").removeClass("undetermined").addClass("unchecked");
						}
					});
				}
			}
		}
	});

}

function selectSubDeptUser(NODE,TREE_OBJ,obj){
	TREE_OBJ.open_branch(NODE,false,function(){
		var lists=obj.find("li");
		for(var i=0;i<lists.length;i++){
			selectSubDeptUser($(lists[i]),TREE_OBJ,$(lists[i]).is("li") ? $(lists[i]) : $(lists[i]).parent());
		}
	});
}

function getIds(){
	var treeObj = $.fn.zTree.getZTreeObj("menu-tree");
	var nodes = treeObj.getCheckedNodes(true);
	var ar="${isAddOrRomove}";
  if(nodes.length<=0){
	  alert("请选择资源！");
       return;
	  }
     var ids="";
	for(var i=0;i<nodes.length;i++){
		ids=ids+nodes[i].id+(i==nodes.length-1?"":",");
	}
	$("#ids").attr("value",ids);
	$("#functions_form").ajaxSubmit(function (){
		if(ar=='0'){
			alert("添加成功");
		}else{
			alert("移除成功");
		}
		refreshTree();
	});
}

function itde()
{
	var checkset=document.getElementsByName("functionIds");
	var result=0;
	var itemId=0;
	for(var i=0;i<checkset.length;i++){
	    if(checkset[i].checked==true){
	      result++;
	      itemId=checkset[i].value;
	    }
	}
	if(result==0){
       alert('<s:text name="common.selectOne"/>');
       return false;
	}
	return true;
}

function selectAll(id){

	var chicket = document.getElementById('functionGroupIds'+id);
	var list = document.getElementsByName('functionIds');

     if(chicket.checked){
		 for(var i=0;i<list.length;i++){
			 var item = list[i];
			 if(item.getAttribute("id").indexOf(id) > 0)
	    	 	list[i].checked=true;
	    	 
	     }
	 }else{
		 for(var y=0;y<list.length;y++){
			 var item2 = list[y];
			 if(item2.getAttribute("id").indexOf(id) > 0)
				list[y].checked=false;
         }

     }
	 
}

function submit(){
	var isAddOrRomove = $("#isAddOrRomove").val();
	if(isAddOrRomove==0){
		$("#searchForm").attr("src","${ctx}/authorization/role!roleToFunctionList.action?roleId=${roleId}"); 
	}else{
		$("#searchForm").attr("src","${ctx}/authorization/role!roleRomoveFunctionList.action?roleId=${roleId}"); 
	}
	$("form:first").submit(); 
	
}
$(function(){
	$("#menu-tree").height($("#col3").height()-150);
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
    		<div class="widget-place" id="widget-place-1">
					<div class="widget" id="identifierwidget-1">
						<div class="widget-header">
							<h5>资源 </h5>
						</div>
						<div class="widget-content">
							<form action="" id="searchForm" onsubmit="submit()" method="post">
							 
							<table>
							<tr>
								<td><s:text name="function.functionCode"/></td><td><input type="text" id="code" name="code"/>&nbsp;&nbsp;&nbsp;</td>
						               <td><s:text name="function.functionName"/></td><td><input type="text" id="name" name="name"/>&nbsp;&nbsp;&nbsp;</td>
						               <td><s:text name="function.functionPath"/></td><td><input type="text" id="path" name="path"/></td>
						               <td align="center" colspan="4"><p class="buttonP"><input id="searchBtn" class="btnStyle" type="button" value='<s:text name="common.search" />' onclick="searchNode()"/>&nbsp;<input class="btnStyle" type="reset" value='<s:text name="common.reset"/>'/></p></td>
							</tr>
							<tr>
								<td colspan="4">
						 			<input class="btnStyle" type="button" value="提交" onclick="getIds();"></input>
						 			<input class="btnStyle" type="button" value="返回" onclick="history.back();"></input>
							 	</td>
							</tr>
							</table>
							</form>
							<div id="content">
								<ul id="menu-tree" class="ztree" style="width:98%; overflow-y:auto;overflow-x:hidden; border:1px;"></ul>
							</div>
						</div>
							
							<input  id="roleId" type="hidden" name="roleId" value="${roleId}" />
							<div  style="position:absolute; left:420px">
							
							<form id="functions_form" name="functions_form" method="post" action="${ctx}/sale/standard-role!roleAddFunction.action" >
							<input type="hidden" name="roleId" value="${roleId}">
							<input type="hidden" id="isAddOrRomove" name="isAddOrRomove" value="${isAddOrRomove}">
							<input type="hidden" name="ids" id="ids" value="">
							</form>
							
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