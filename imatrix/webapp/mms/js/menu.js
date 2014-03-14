var currentMenuId;
var currentTitle;
var currentNode;
var canDorp=true;
var setting = {
		async : {
			enable: true,
			url: getUrl
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
		data: {
			simpleData: {
				enable: true
			}
		},
		callback : {
			onClick:onClick,
			onDrop:onDrop,
			beforeDrop:beforeDrop,
			beforeDrag:beforeDrag,
			onAsyncSuccess:onAsyncSuccess
		}
	};
function onClick(event, treeId, treeNode){
	if(treeNode){
		currentNode=treeNode;
		currentMenuId=treeNode.id;
		currentTitle=treeNode.name;
	}
}
function onAsyncSuccess(){
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
function dropPrev(treeId, nodes, targetNode) {
	var type=targetNode?targetNode.type:"";
	var pNode = targetNode.getParentNode();
	if (pNode && pNode.dropInner === "false") {
		return false;
	}
	if(targetNode.dropPrev==="false"){
		return false;
	}
	for(var i=0;i<nodes.length;i++){
		if(nodes[i].type!=type){
			return false;
		}
	}
	return true;
}
function dropInner(treeId, nodes, targetNode) {
	var type=targetNode?targetNode.type:"";
	if (targetNode && targetNode.dropInner === "false") {
		return false;
	}
	
	if(targetNode && targetNode.type=="system"){
		for(var i=0;i<nodes.length;i++){
				if(nodes[i].type=="system"){
					return false;
				}
		}
	}else{
		for(var i=0;i<nodes.length;i++){
			if(nodes[i].type!=type){
				return false;
			}
		}
	}
	
	return true;
}
function dropNext(treeId, nodes, targetNode) {
	var type=targetNode?targetNode.type:"";
	var pNode = targetNode.getParentNode();
	if (pNode && pNode.dropInner === "false") {
		return false;
	}
	if (targetNode && targetNode.dropNext === "false") {
		return false;
	}
	for(var i=0;i<nodes.length;i++){
		if(nodes[i].type!=type){
			return false;
		}
	}
	return true;
}
function onDrop(event, treeId, treeNodes, targetNode, moveType, isCopy) {
	var treeObj = $.fn.zTree.getZTreeObj("menu-tree");
	var msg="";
	for(var i=0;i<treeNodes.length;i++){
		msg=msg+treeNodes[i].id+","+treeObj.getNodeIndex(treeNodes[i])+","+treeNodes[i].level+(i==treeNodes.length-1?"":";");
	}
	if(targetNode){
		canDorp=false;
		$.ajax({
			data:{msgs:msg,targetId:targetNode.id,moveType:moveType},
			cache:false,
			type:"post",
			url:webRoot+"/module/menu!moveNodes.htm",
			success:function(data){
				canDorp=true;
			},
			error:function(){
			}
	    });
	}
}
function beforeDrop(treeId, treeNodes, targetNode, moveType, isCopy) {
	var type=targetNode?targetNode.type:"";
	if(targetNode&&targetNode.data=='portal'){
		$("#message").html("<font class=\"onError\"><nobr>不能将菜单移动到门户下!</nobr></font>");
		location.hash="anchor";
		showMsg();
		return false;
	}
}
function beforeDrag(treeId, treeNodes, targetNode, moveType, isCopy){
	if(!canDorp){
		return false;
	}
}
//创建菜单树
function createMenuTree(chirldId){
	$.fn.zTree.init($("#menu-tree"), setting);
}
function getUrl(treeId, treeNode) {
	var param = "";
	if(typeof(treeNode)!="undefined"&&treeNode!=null){
		param = "currentId="+treeNode.id;
    }else{
    	param = "currentId=0";
    }
	return webRoot+"/module/menu!menuTree.htm?" + param;
}

var newCreate = true;
//创建菜单
function createMenu(){
	var treeObj = $.fn.zTree.getZTreeObj("menu-tree");
	var nodes = treeObj.getSelectedNodes();
	if(nodes.length!=1){
		$("#message").html("<font class=\"onError\"><nobr>请选择一个父菜单</nobr></font>");
		location.hash="anchor";
		showMsg();
		return;
	}else{
		currentNode=nodes[0];
		currentMenuId=nodes[0].id;
	}
	if(currentNode.data=='portal'){
		$("#message").html("<font class=\"onError\"><nobr>部门在门户下新建菜单</nobr></font>");
		location.hash="anchor";
		showMsg();
		return;
	}
	newCreate = true;
	if(!currentMenuId||typeof(currentMenuId)=="undefined"){
		$("#message").html("<font class=\"onError\"><nobr>请选择一个父菜单</nobr></font>");
		location.hash="anchor";
		showMsg();
		return;
    }
	$.colorbox({href:webRoot+"/module/menu-input.htm?parentMenuId="+currentMenuId,iframe:true, innerWidth:500, innerHeight:350,overlayClose:false,title:"创建菜单"});
}

var isCreateSystem = false;

//创建系统
function createSystem(){
	currentMenuId=null;
	isCreateSystem = true;
	newCreate = false;
	$("#menu-tree").jstree("deselect_all");
	$.colorbox({href:webRoot+"/module/menu-input.htm?parentMenuId="+currentMenuId+"&isCreateSystem="+isCreateSystem,iframe:true, innerWidth:500, innerHeight:350,overlayClose:false,title:"创建系统"});
}
//判断是否选择一个菜单
function isSelected(){
	if(!currentMenuId||typeof(currentMenuId)=="undefined"){
		$("#message").html("<font class=\"onError\"><nobr>请选择一个菜单</nobr></font>");
		location.hash="anchor";
		showMsg();
		return false;
	}else{
		return true;
	}
}

//修改菜单
function updateMenu(){
	var treeObj = $.fn.zTree.getZTreeObj("menu-tree");
	var nodes = treeObj.getSelectedNodes();
	if(nodes.length!=1){
		$("#message").html("<font class=\"onError\"><nobr>请选择一个菜单</nobr></font>");
		location.hash="anchor";
		showMsg();
		return;
	}else{
		currentNode=nodes[0];
		currentMenuId=nodes[0].id;
	}
	if(isSelected()){
		newCreate = false;
		$.get(webRoot+"/module/menu-input.htm?menuId="+currentMenuId+"&_data="+new Date(),function(data){
			if(data=="false"){
				parent.scrollTo(0,0);
				$("#message").html("<font class=\"onError\"><nobr>"+"菜单已启用不能修改"+"</nobr></font>");
				$("#message").show();
				location.hash="anchor";
				setTimeout('$("#message").hide();',3000);
			}else{
				$.colorbox({href:webRoot+"/module/menu-input.htm?menuId="+currentMenuId,iframe:true, innerWidth:500, innerHeight:350,overlayClose:false,title:"修改菜单"});
			}
		});
	}
}
//删除表单
function deleteMenu(){
	if(versionType=="online"){
		if(currentTitle.indexOf("启用")>0|| currentTitle.indexOf("禁用")>0){
			alert("为确保系统的正常演示，只能删除草稿状态的菜单");return;
		}
	}
	if(isSelected()){
		if(confirm("确认删除吗?")){
			$.get(webRoot+"/module/menu!delete.htm?menuId="+currentMenuId+"&_data="+new Date(), function(data){
				if(data=="false"){
					$("#message").html("<font class=\"onError\"><nobr>"+"不能删除已启用的一级菜单"+"</nobr></font>");
					$("#message").show();
					location.hash="anchor";
					setTimeout('$("#message").hide();',3000);
				}else if(data=="success"){
					var treeObj = $.fn.zTree.getZTreeObj("menu-tree");
					treeObj.removeNode(currentNode);
					currentNode=null;
					currentMenuId=null;
					currentTitle=null;
				}else{
					$("#message").html("<font class=\"onError\"><nobr>"+data+"</nobr></font>");
					$("#message").show();
					location.hash="anchor";
					setTimeout('$("#message").hide();',3000);
				}
			});
		}
	}
}
//启用表单
function enableMenu(){
	var treeObj = $.fn.zTree.getZTreeObj("menu-tree");
	var nodes = treeObj.getSelectedNodes();
	if(nodes.length!=1){
		$("#message").html("<font class=\"onError\"><nobr>请选择一个菜单</nobr></font>");
		location.hash="anchor";
		showMsg();
		return;
	}else{
		currentNode=nodes[0];
		currentMenuId=nodes[0].id;
	}
	if(isSelected()){
		$.get(webRoot+"/module/menu!enable.htm?menuId="+currentMenuId+"&___time="+new Date().getTime(), function(data){
		//$.tree_reference('menu-tree').selected;
		var menus = data.split(",");
			for(var i = 0 ;i<menus.length;i++){
				var menuMessage = menus[i].split("=");
				var treeObj = $.fn.zTree.getZTreeObj("menu-tree");
				var node = treeObj.getNodesByFilter(function(node){
					var result=node.id==menuMessage[0];
					return result;
				}, true);
				node.name=menuMessage[1];
				treeObj.updateNode(node,false);
			}
		});
	}
}
//禁用表单
function disableMenu(){
	var treeObj = $.fn.zTree.getZTreeObj("menu-tree");
	var nodes = treeObj.getSelectedNodes();
	if(nodes.length!=1){
		$("#message").html("<font class=\"onError\"><nobr>请选择一个菜单</nobr></font>");
		location.hash="anchor";
		showMsg();
		return;
	}else{
		currentNode=nodes[0];
		currentMenuId=nodes[0].id;
	}
	if(currentNode.data=="mms-menu"){
		$("#message").html("<font class=\"onError\"><nobr>菜单管理不能禁用</nobr></font>");
		location.hash="anchor";
		showMsg();
		return;
	}
	if(isSelected()){
		$.get(webRoot+"/module/menu!disableMenu.htm?menuId="+currentMenuId+"&___time="+new Date().getTime(), function(data){
			var treeObj = $.fn.zTree.getZTreeObj("menu-tree");
			currentNode.name=data;
			treeObj.updateNode(currentNode,false);
		});
	}
}

function updateTree(chirldId,chirldName){
	if(newCreate){
		$("#menu-tree").jstree("create","#"+currentMenuId,"first",{ attr : {id : chirldId}, data : chirldName },function(){},true);
	}else{
		$("#menu-tree").jstree("refresh",-1);
	}
}

//页面验证
function  validate_menu(){
	$("#menuForm").validate({
		submitHandler: function() {
			ajax_submit_form();
		},
		rules: {
			code: "required",
			name:"required",
			displayOrder:{
				required:true,
				number:true
			}
		},
		messages: {
			code: "必填",
			name:"必填",
			displayOrder:{
				required:"必填",
				number:"请输入数字"
			}
		}
	});
}

function ajax_submit_form(){
	$("#menuForm").ajaxSubmit(function (){
		saveMenuCallback();
	});
//	$("#menuForm").ajaxSubmit(function (data){
//		var datas = data.split(":");
//		if(datas[0]=="msg"){
//			$("#message").html("<font class=\"onError\"><nobr>" +datas[1]+"</nobr></font>");
//			location.hash="anchor";
//			showMsg();
//		}else if(datas[0]=="enable"){
//			var menus = datas[1].split(",");
//			var crnMenus=datas[2].split("-");
//			parent.updateTree(crnMenus[0],crnMenus[1]);
//			for(var i = 0 ;i<menus.length;i++){
//				var menuMessage = menus[i].split("=");
//				parent.$("#menu-tree").jstree("rename_node","#"+menuMessage[0],menuMessage[1]);
//			}
//				parent.$.colorbox.close();
//		}else{
//			parent.updateTree(datas[0],datas[1]);
//			parent.$.colorbox.close();
//		}
//	});
}
function saveAndEnable(){
	save('saveAndEnable');
}
function save(position){
	if($("#code").attr("value")==""){
		$("#menuForm").submit();
	}else{
		validateMenuCode(position);
	}
}

function saveMenuCallback(){
	window.parent.getTreeObj().reAsyncChildNodes(null, "refresh");
	window.parent.currentMenuId=null;
	window.parent.currentTitle=null;
	window.parent.currentNode=null;
	parent.$.colorbox.close();
	
}

//选择系统时自动填值
function choseSys(){
	var system=$("#choseSystems").val();
	var sysMessage = system.split(",");//${id},${code},${businessName},${businessPath}
	if(sysMessage!=''){
		$("#choseSystemId").attr("value",sysMessage[0]);
		$("#code").attr("readOnly","true");
		$("#code").attr("value",sysMessage[1]);
		$("#name").attr("value",sysMessage[2]);
		$("#url").attr("value",sysMessage[3]);
	}else{
		$("#choseSystemId").attr("value","");
		$("#code").removeAttr("readOnly");
		$("#code").attr("value","");
		$("#name").attr("value","");
		$("#url").attr("value","#this");
	}
}
//验证菜单编码唯一
function validateMenuCode(position){
	$.ajax({
		   type: "POST",
		   url: "validate-menu-code.htm",
		   data: "menuCode="+$("#code").attr("value")+"&menuId="+$($("form[id='menuForm']").find("input[id='menuId']")[0]).attr("value"),
		   success: function(msg){
			   if(msg=="true"){
				  alert("该菜单编号已存在");
			   }else{
				   if(position=='saveAndEnable'){//保存并启用
					   $("#enableState").attr("value","ENABLE");
				   }
				   $("#menuForm").submit();
			   }
		   }
	});
}