<%@ page contentType="text/html;charset=UTF-8"%>
<%@ include file="/common/taglibs.jsp"%>

<div id="accordion" >
	<h3><a href="list.htm" id="dynamic-tree">动态树</a></h3>
	<div>
		<div class="demo" id="dynamic-menu" style="margin-top: 10px;"></div>
	</div>
	
	<h3><a href="static-tree.htm" id="static-tree">静态树</a></h3>
	<div>
		<div class="demo" id="static-menu" style="margin-top: 10px;"></div>
	</div>
	<!--<h3><a href="function-test.htm" id="static-tree">性能测试</a></h3>
	<div>
		<div class="demo" id="function-test-menu" style="margin-top: 10px;"></div>
	</div>
-->
    <h3><a href="loan-bill-dynamic-ztree.htm" id="dynamic-ztree">动态树(ztree)</a></h3>
	<div>
		<div class="ztree" id="zdynamic-menu" style="margin-top: 10px;"></div>
	</div>
	 <h3><a href="loan-bill-static-ztree.htm" id="static-ztree">静态树(ztree)</a></h3>
	<div>
		<div class="ztree" id="zstatic-menu" style="margin-top: 10px;"></div>
	</div>
	<h3><a href="loan-bill-authority-tag.htm" id="autority-tag">自定义权限标签</a></h3>
	<div>
	</div>
</div>
<script type="text/javascript" src="${resourcesCtx}/js/tree.js"></script>

<script type="text/javascript">
$(function () {
	$.ajaxSetup({cache:false});
	var url=window.location.href;

	if(url.indexOf('list')!=-1){
		$("#accordion").accordion({fillSpace:true, change:accordionChange,active: 0});
		folderTreeMenu();
	}else if(url.indexOf('static-tree')!=-1){
		$("#accordion").accordion({fillSpace:true, change:accordionChange,active: 1});
		createStaticTree();
	}else if(url.indexOf('loan-bill-dynamic-ztree')!=-1){
		$("#accordion").accordion({fillSpace:true, change:accordionChange,active: 2});
		createDynamicZtree();
	}else if(url.indexOf('loan-bill-static-ztree')!=-1){
		$("#accordion").accordion({fillSpace:true, change:accordionChange,active: 3});
		createStaticZtree();
	}else{
		$("#accordion").accordion({fillSpace:true, change:accordionChange,active: 4});
	}
});

function accordionChange(event,ui){
	var url=ui.newHeader.children("a").attr("href");
	if(url=="list.htm"){
		$("#static-menu").html(""); 
			folderTreeMenu();
	}else if(url=="static-tree.htm"){
		$("#dynamic-menu").html("");
		createStaticTree();
	}else if(url=="function-test.htm"){
		$("#static-menu").html(""); 
		$("#dynamic-menu").html("");
		createFunctionTree();
	}else if(url=="loan-bill-static-ztree.htm"){
		$("#static-menu").html(""); 
		$("#dynamic-menu").html("");
		$("#zdynamic-menu").html("");
		createStaticZtree();
	}else if(url=="loan-bill-dynamic-ztree.htm"){
		$("#static-menu").html(""); 
		$("#dynamic-menu").html("");
		$("#zstatic-menu").html("");
		createDynamicZtree();
	}else if(url=="loan-bill-authority-tag.htm"){
		$("#static-menu").html(""); 
		$("#dynamic-menu").html("");
		$("#zstatic-menu").html("");
		$("#zdynamic-menu").html("");
	}
	treechange(url);
}

function treechange(url){
	if(url=="list.htm"){
		url="${ctx}/loan-bill/loan-bill-list.htm";
		ajaxSubmit('contentForm', url, 'main'); 
	}else if(url=="static-tree.htm"){
		var url="${ctx}/loan-bill/static-tree.htm";
		ajaxSubmit('contentForm', url, 'main'); 
	}else if(url=="function-test.htm"){
		var url="${ctx}/loan-bill/test-tree.htm";
		ajaxSubmit('contentForm', url, 'main'); 
	}else if(url=="loan-bill-dynamic-ztree.htm"){
		var url="${ctx}/loan-bill/loan-bill-dynamic-ztree.htm";
		ajaxSubmit('contentForm', url, 'main'); 
	}else if(url=="loan-bill-static-ztree.htm"){
		var url="${ctx}/loan-bill/loan-bill-static-ztree.htm";
		ajaxSubmit('contentForm', url, 'main'); 
	}else if(url=="loan-bill-authority-tag.htm"){
		var url="${ctx}/loan-bill/loan-bill-authority-tag.htm";
		ajaxSubmit('contentForm', url, 'main');
	}
}

/**
 * 加载动态树
 */
function folderTreeMenu(){
	$("#dynamic-menu").jstree({
			"json_data":{
				"ajax" : { "url" : "${ctx}/loan-bill/load-tree.htm",
							"data" : function (n) {  
							 return { parentFolderId : n!=-1 ? n.attr("id") : -1};   
						}
					}
	   },
	   "themes" : {
			 "theme" : "default",  
			 "dots" : true,  
			 "icons" : true 
		},  
		"ui" : {    
			"initially_select" : [ "loadbill-menu" ]  
		},  
   		"plugins" : [ "themes", "json_data","ui" ]
	 }).bind("select_node.jstree",function(e){
		 var id = $("#dynamic-menu").find(".jstree-clicked").parent().attr("id");
		 $('#_folderId').attr('value', id);  
		 $('#id').attr('value', id);  
		loadFolder();
		});
    }

function loadFolder(){
	ajaxSubmit('contentForm', webRoot+'/loan-bill/loan-bill-list.htm', 'main');
}

/**
 * 静态树加载
 */
function createStaticTree(){
	$.ajaxSetup({cache:false});
		$("#static-menu")
		.jstree({
			"json_data" : {  
				"data":[{
					"attr" : { "id" : "_lemonTree" },   
				  	"data" :  "柠檬树",
				  		"children" 	:[{"attr":{"id":"_lemon1"},"data":"小柠檬"},
						           	  {"attr":{"id":"_lemon2"},"data":"大柠檬"},
						           	  {"attr":{"id":"_lemon3"},"data":"老柠檬"},
						              ]
		   		}
		   		,{
					"attr" : { "id" : "_appleTree" },   
				  	"data" :  "苹果树"
		   		}
		   		,{
					"attr" : { "id" : "_grapeTree" },   
				  	"data" :  "葡萄树",
				  		"children" 	:[{"attr":{"id":"_grape1"},"data":"小葡萄"},
						           	  {"attr":{"id":"_grape2"},"data":"大葡萄"},
						           	  {"attr":{"id":"_grape3"},"data":"老葡萄"},
						              ]
		   		}
		   		,{
					"attr" : { "id" : "_bigTree" },   
				  	"data" :  "梧桐树"
		   		}]
		},
	   "themes" : {  
			 "theme" : "default",  
			 "dots" : true,  
			 "icons" : true 
		},  
		"ui" : {    
			"initially_select" : [ "_meetingBook" ]  
		},  
   		"plugins" : [ "themes", "json_data","ui" ]
	}).bind("select_node.jstree",function(e){selectNode();});
}

function selectNode(){
	treechangeStatic($(".jstree-clicked").parent().attr("id"));
}

function createFunctionTree(){
	$.ajaxSetup({cache:false});
	$("#function-test-menu")
	.jstree({
		"json_data" : {  
			"data":[{
				"attr" : { "id" : "_treeTest" },   
				"data" :  "树测试"
	   		}
	   		,{
	   			"attr" : { "id" : "_gridTest" },   
			  	"data" :  "列表测试"
			}
	   		,{
	   			"attr" : { "id" : "_gridTest2" },   
			  	"data" :  "列表测试二"
			}
	   		,{
				"attr" : { "id" : "_formTest" },   
			  	"data" :  "表单测试"
	   		}]
	},
   "themes" : {  
		 "theme" : "default",  
		 "dots" : true,  
		 "icons" : true 
	},  
	"ui" : {    
		"initially_select" : [ "_meetingBook" ]  
	},  
		"plugins" : [ "themes", "json_data","ui" ]
}).bind("select_node.jstree",function(e){selectFunctionNode();});
}

function selectFunctionNode(){
	treechangeTest($(".jstree-clicked").parent().attr("id"));
}

//ztree 静态
function createStaticZtree(){
	var zNodes =[
	         	{ id:1, pId:0, name:"父节点1", open:true},
	         	{ id:11, pId:1, name:"父节点11"},
	         	{ id:111, pId:11, name:"叶子节点111"},
	         	{ id:112, pId:11, name:"叶子节点112"},
	         	{ id:113, pId:11, name:"叶子节点113"},
	         	{ id:114, pId:11, name:"叶子节点114"},
	         	{ id:12, pId:1, name:"父节点12"},
	         	{ id:121, pId:12, name:"叶子节点121"},
	         	{ id:122, pId:12, name:"叶子节点122"},
	         	{ id:123, pId:12, name:"叶子节点123"},
	         	{ id:124, pId:12, name:"叶子节点124"},
	         	{ id:13, pId:1, name:"父节点13", isParent:true},
	         	{ id:2, pId:0, name:"父节点2"},
	         	{ id:21, pId:2, name:"父节点21", open:true},
	         	{ id:211, pId:21, name:"叶子节点211"},
	         	{ id:212, pId:21, name:"叶子节点212"},
	         	{ id:213, pId:21, name:"叶子节点213"},
	         	{ id:214, pId:21, name:"叶子节点214"},
	         	{ id:22, pId:2, name:"父节点22"},
	         	{ id:221, pId:22, name:"叶子节点221"},
	         	{ id:222, pId:22, name:"叶子节点222"},
	         	{ id:223, pId:22, name:"叶子节点223"},
	         	{ id:224, pId:22, name:"叶子节点224"},
	         	{ id:23, pId:2, name:"父节点23"},
	         	{ id:231, pId:23, name:"叶子节点231"},
	         	{ id:232, pId:23, name:"叶子节点232"},
	         	{ id:233, pId:23, name:"叶子节点233"},
	         	{ id:234, pId:23, name:"叶子节点234"},
	         	{ id:3, pId:0, name:"父节点3", isParent:true}
	         ];
tree.initTree({treeId:"zstatic-menu",
	data:zNodes,
	type:"ztree",
	callback:{
			onClick:onClickStaticZtree
		}});

}

function onClickStaticZtree(event, treeId, treeNode){
	$("#ztree_static_id").attr("value",treeNode.id);
	$("#ztree_static_name").attr("value",treeNode.name);
	if(treeNode.pId==null){
		$("#ztree_static_pid").attr("value","");
	}else{
	    $("#ztree_static_pid").attr("value",treeNode.pId);
	}
}

//ztree 动态
function createDynamicZtree(){
tree.initTree({treeId:"zdynamic-menu",
	url:getUrl,
	type:"ztree",
	callback:{
			onClick:onClickDynamicZtree
		}});
}

function getUrl(treeId, treeNode) {
	var param = "";
	if(typeof(treeNode)!="undefined"&&treeNode!=null){
		param = "currentId="+treeNode.id;
    }else{
    	param = "currentId=0";
     }
	return "${ctx}/loan-bill/load-ztree.htm?" + param;
}

function onClickDynamicZtree(event, treeId, treeNode){
	$("#_ztree_folderId").attr("value",treeNode.id);
	$("#ztree_dynamic_id").attr("value",treeNode.id);
	$("#ztree_dynamic_name").attr("value",treeNode.name);
	if(treeNode.pId==null){
		$("#ztree_dynamic_pid").attr("value","");
	}else{
	    $("#ztree_dynamic_pid").attr("value",treeNode.pId);
	}
}

function treechangeTest(id){
	if(id=="_treeTest"){
		ajaxSubmit('contentForm','${ctx}/loan-bill/test-tree.htm', 'main'); 
	}else if(id=="_gridTest"){
		ajaxSubmit('contentForm','${ctx}/loan-bill/task-list.htm' , 'main'); 
	}else if(id=="_gridTest2"){
		ajaxSubmit('contentForm','${ctx}/loan-bill/test-list.htm' , 'main'); 
	}else if(id=="_formTest"){
		ajaxSubmit('contentForm','${ctx}/loan-bill/test-form-list.htm' , 'main'); 
	}
}

//点击静态树跳转页面
function treechangeStatic(id){
	var url="${ctx}/loan-bill/loan-bill-static-tree.htm?treeId="+id;
	if(id=="_lemonTree"||id=="_lemon1"||id=="_lemon2"||id=="_lemon3"){//柠檬树
		ajaxSubmit('contentForm', url, 'main'); 
	}else if(id=="_appleTree"){//苹果树
		ajaxSubmit('contentForm', url, 'main'); 
	}else if(id=="_grapeTree"||id=="_grape1"||id=="_grape2"||id=="_grape3"){//葡萄树
		ajaxSubmit('contentForm', url, 'main'); 
	}else if(id=="_bigTree"){//梧桐树
		ajaxSubmit('contentForm', url, 'main'); 
	}
}
</script>