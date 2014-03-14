/*
 *系统中用到的树的封装使用 
 *initTree：方法的参数obj说明：
 *     jsTree:{treeId:,url:,theme:,types:,plugins:,callback:,data(静态树才需要该参数):,initiallySelect:}
 *		zTree:{treeId:,url:,data(静态树才需要该参数):,multiple:,callback:,initiallySelect:}
 */
var tree={
	treeId:"",
	__treeType:"jstree",
	mType:"",
	paramName:"",
	initiallySelect:"",
	__multiple:false,
	initiallySelectFirst:false,
	initiallySelectFirstChild:false,
	callback:{},
	initTree:function(obj){
		tree.treeId=obj.treeId;
		tree.paramName=obj.paramName;
		var type=obj.type;
		if(typeof(type)=='undefined'||type=='')type="jstree";
		tree.__treeType=type;
		var initiallySelect = obj.initiallySelect;
		if(typeof(initiallySelect)=='undefined'){
			obj.initiallySelect = "";
		}
		tree.initiallySelect=obj.initiallySelect;
		if(typeof(obj.callback)=='undefined'){
			obj.callback={};
		}
		tree.callback = obj.callback;
		if(typeof(obj.multiple)=='undefined'){
			obj.multiple = false;
		}
		tree.__multiple=obj.multiple;
		tree.mType=obj.treeType;
		tree.initiallySelectFirst=obj.initiallySelectFirst;
		if(typeof(obj.initiallySelectFirst)=='undefined'){
			tree.initiallySelectFirst=false;
		}
		tree.initiallySelectFirstChild=obj.initiallySelectFirstChild;
		if(typeof(obj.initiallySelectFirstChild)=='undefined'){
			tree.initiallySelectFirstChild=false;
		}
		if(tree.initiallySelectFirstChild){
			tree.initiallySelectFirst=true;
		}
		if(type=="jstree"){
			___createJSTree(obj);
		}else if(type=="ztree"){
			___createZTree(obj);
		}
	},
	getSelectNodeId:___getSelectNodeId,
	getSelectNodeIds:___getSelectNodeIds,
	getSelectNode:___getSelectNode,
	getSelectNodes:___getSelectNodes,
	getTreeObject:___getZTreeObj,
	searchNodes:___ztreeSearchNode
};

function ___createJSTree(obj){
	
	if(typeof(obj.theme)=='undefined'){
		obj.theme = "default";
	}
	var isStatic = isStaticTree(obj);
	var jstreeSetting={
			"json_data":{
					"ajax" : { "url" : obj.url,
						"data" : function (n) {  
							return ___getPostDataParam(n);   
						}
					}
			},
			"themes" : {  
			  "theme" : obj.theme,  
			  "dots" : true,  
			  "icons" : true 
			 },
			 "ui" : {  "initially_select":[ obj.initiallySelect ] },
			 "types" :{ 
					"types" : ___getJstreePluginTypes(obj)
				 },
			 "plugins" : ___getJstreePlugins(obj)
		};
	if(isStatic){
		jstreeSetting.json_data={"data":obj.data};
	}
	$("#"+obj.treeId).bind("loaded.jstree",function(e){
		var loadTreecallback=obj.callback.loadTree;
		if(typeof(loadTreecallback)=='function'){
			loadTreecallback();
		}
	}).bind("select_node.jstree",function(e){
		var selectNodecallback=obj.callback.onClick;
		if(typeof(selectNodecallback)=='function'){
			selectNodecallback();
		}
	}).jstree(jstreeSetting);
}

function isStaticTree(obj){
	var isStaticTree = false;//是否是静态树
	if(obj.url==""||typeof(obj.url)=='undefined'){
		isStaticTree = true;
	}
	return isStaticTree;
}
function ___getJstreePluginTypes(obj){
	var types = "[{";
	var typeInfos = obj.types;
	if(typeof(typeInfos)=='undefined'||typeInfos==""){
		return {};
	}
	for(var t in typeInfos){
		types+="\""+t+"\":{\"icon\":{\"image\":\""+typeInfos[t]+"\"}},";
	}
	if(types.indexOf(",")>=0)types = types.substring(0, types.lastIndexOf(","));
	types = types+"}]";
	return eval(types)[0];
}
function ___getJstreePlugins(obj){
	var plugins = obj.plugins;
	return plugins.split(",");
}

function ___getSelectNodeId(){
	var selectNodeId = "";
	if(tree.__treeType=="jstree"){
		var clickNodes=$("#"+tree.treeId).find(".jstree-clicked");
		var node;
		if(clickNodes.length>0){
			node=clickNodes[0];
			selectNodeId = $(node).parent().attr("id");
		}
	}else if(tree.__treeType=="ztree"){
		var treeObj = $.fn.zTree.getZTreeObj(tree.treeId);
		var nodes = treeObj.getSelectedNodes();
		var treeNode = nodes.length>0?nodes[0]:"";
		if(treeNode!=""){
			selectNodeId = treeNode.id;
		}
	}
	return selectNodeId;
}
function ___getSelectNodeIds(){
	var selectNodeIds = [];
	var j=0;
	if(tree.__treeType=="jstree"){
		var clickNodes=$("#"+tree.treeId).find(".jstree-clicked");
		var node;
		for(var i=0;i<clickNodes.length;i++){
			node=clickNodes[i];
			selectNodeIds[j++] = $(node).parent().attr("id");
		}
	}else if(tree.__treeType=="ztree"){
		var nodes = ___getSelectNodes();
		for(var i=0;i<nodes.length;i++){
			selectNodeIds[j++] = nodes[i].id;
		}
	}
	return selectNodeIds;
}
function ___getSelectNode(){
	if(tree.__treeType=="ztree"){
		var treeObj = $.fn.zTree.getZTreeObj(tree.treeId);
		var nodes = treeObj.getSelectedNodes();
		var treeNode = nodes.length>0?nodes[0]:"";
		return treeNode;
	}else{
		return "";
	}
}
function ___getSelectNodes(){
	if(tree.__treeType=="ztree"){
		var treeObj = $.fn.zTree.getZTreeObj(tree.treeId);
		var nodes = treeObj.getCheckedNodes();
		var treeType =tree.mType;
		var selectNodes = [];
		var j=0;
		for(var i=0;i<nodes.length;i++){
			if(treeType!=3&&treeType!=4&&treeType!=5){//当不是DEPARTMENT_TREE GROUP_TREE DEPARTMENT_WORKGROUP_TREE
				if(!nodes[i].getCheckStatus().half){//如果是全选状态
					selectNodes[j++]=nodes[i];
				}
			}else{//当是部门节点时直接选中
				selectNodes[j++]=nodes[i];
			}
		}
		return selectNodes;
	}else{
		return "";
	}
}

function ___getPostDataParam(cunrrentNode){
	var paramName = tree.paramName;
	if(typeof(paramName)=='undefined'||paramName==""){
		return {};
	}
	var currentId = cunrrentNode!=-1 ? cunrrentNode.attr("id") : -1;
	return eval("["+paramName+":"+currentId+"]")[0];
}


function ___createZTree(obj){
	var ztreeSetting = {
			data: {
				simpleData: {
					enable: true
				},
				treeType:obj.treeType
			},
			view: {
				fontCss: _______getFontCss
			},
			callback: {
				beforeExpand: typeof(obj.callback.beforeExpand)=='function'?obj.callback.beforeExpand:function(){},
				beforeAsync: typeof(obj.callback.beforeAsync)=='function'?obj.callback.beforeAsync:function(){},
				onAsyncSuccess: __onAsyncSuccess,
				onAsyncError: typeof(obj.callback.onAsyncError)=='function'?obj.callback.onAsyncError:function(){},
				onExpand: typeof(obj.callback.onExpand)=='function'?obj.callback.onExpand:function(){},
				onClick: __treeOnClick,
				onCheck: __treeOnCheck
			}
		};
	if(obj.multiple){//如果是多选树
		if(typeof(obj.chkboxType)=='undefined'){
			obj.chkboxType = {"Y": "ps", "N": "ps"};
		}
		ztreeSetting.check={chkboxType : obj.chkboxType,
				chkStyle : "checkbox",
				enable : true};
	}
	var isStatic = isStaticTree(obj);
	if(isStatic){
		$.fn.zTree.init($("#"+tree.treeId), ztreeSetting,obj.data);
		__intSelectNode();
	}else{
		ztreeSetting.async={enable: true,
				url: obj.url};
		$.fn.zTree.init($("#"+tree.treeId), ztreeSetting);
	}
}

function __onAsyncSuccess(event, treeId, treeNode){
	__intSelectNode();
	var onAsyncSuccessCallback=tree.callback.onAsyncSuccess;
	if(typeof(onAsyncSuccessCallback)=='function')onAsyncSuccessCallback(event, treeId, treeNode);
}
function __treeOnClick(event, treeId, treeNode){
	___onClickExpandNode(event, treeId, treeNode);
	var onClickCallback=tree.callback.onClick;
	if(typeof(onClickCallback)=='function')onClickCallback(event, treeId, treeNode);
}
function __treeOnCheck(event, treeId, treeNode){
	if(tree.__multiple){
		___onCheckExpandNode(event, treeId, treeNode);
	}
	var onCheckCallback=tree.callback.onCheck;
	if(typeof(onClickCallback)=='function')onCheckCallback(event, treeId, treeNode);
}

function __intSelectNode(){
	var __selectNode = _______getInitSelectNode();
	if(__selectNode!=null){
		var treeObj = $.fn.zTree.getZTreeObj(tree.treeId);
		treeObj.selectNode(__selectNode);
		var selectNodecallback=tree.callback.onClick;
		if(typeof(selectNodecallback)=='function'){
			selectNodecallback();
		}
	}
}

function ___onClickExpandNode(event, treeId, treeNode){
	if(treeNode){
	 	var zTree = $.fn.zTree.getZTreeObj(treeId);
	 	if(tree.__multiple){//多选时
	 		zTree.checkNode(treeNode, null, true);
	 		if(treeNode.checked){
	 			zTree.expandNode(treeNode,true,false,true,true);
	 		}
	 	}else{
	 		zTree.expandNode(treeNode,true,false,true,true);
	 	}
	  }
}
function ___onCheckExpandNode(event, treeId, treeNode){
	//获取所有选择节点集合selectNodeList
	  var zTree = $.fn.zTree.getZTreeObj(treeId);
	  if(treeNode){
	  	if(treeNode.checked){
	  		zTree.expandNode(treeNode,true,false,true,true);
	  	}
	  }
}

function ___getZTreeObj(){
	return $.fn.zTree.getZTreeObj(tree.treeId);
}

var nodeList = [];
function ___ztreeSearchNode(value){
	___zteeUpdateNodes(false);
	var treeObj =tree.getTreeObject();
	nodeList = treeObj.getNodesByParamFuzzy("name", value, null);
	___zteeUpdateNodes(true);
}
function ___zteeUpdateNodes(highlight) {
	if(nodeList){
		var zTree = $.fn.zTree.getZTreeObj(tree.treeId);
		for( var i=0, l=nodeList.length; i<l; i++) {
			var parentNode = nodeList[i].getParentNode();
			if(parentNode!=null){
	    		zTree.expandNode(parentNode,true,true,true,false);
	    	}
			nodeList[i].highlight = highlight;
			zTree.updateNode(nodeList[i]);
		}
	}
}
function _______getFontCss(treeId, treeNode) {
	return (!!treeNode.highlight) ? {color:"#FF0000"} : {color:"#333"};
}

function _______getInitSelectNode(){
	var __selectNode = null;
	if(tree.initiallySelect!=""){
		var __initiallySelect=tree.initiallySelect;
		if(typeof(tree.initiallySelect)=='function'){
			var initSelectCall = tree.initiallySelect;
			__initiallySelect = initSelectCall();
		}
		var treeObj = $.fn.zTree.getZTreeObj(tree.treeId);
		if(treeObj!=null&&typeof(treeObj)!='undefined'){
			__selectNode = treeObj.getNodesByFilter(function(node){
				if(node.id==__initiallySelect){
					return true;
				}
				return false;
			},true);
		}
		
	}
	if(__selectNode==null){
		if(tree.initiallySelectFirst){
			var treeObj = $.fn.zTree.getZTreeObj(tree.treeId);
			if(treeObj!=null&&typeof(treeObj)!='undefined'){
				var nodes = treeObj.getNodes();
				for(var i=0;i<nodes.length;i++){
					if(tree.initiallySelectFirstChild){
						return ____getInitSelectChildNode(nodes);
					}else{
						return nodes[i];
					}
				}
			}
		}
	}
	return __selectNode;
}

function ____getInitSelectChildNode(nodes){
	for(var i=0;i<nodes.length;i++){
		var children = nodes[i].children;
		if(children==null||children.length<=0){
			return nodes[i];
		}else{
			return ____getInitSelectChildNode(children);
		}
	}
}


