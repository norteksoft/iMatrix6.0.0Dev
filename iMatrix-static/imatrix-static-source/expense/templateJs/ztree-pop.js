function check(){
	var obj={};
	if(chkStyle&&chkStyle!=""){
		if(chkboxType&&chkboxType!=""){
			eval("c="+chkboxType);
			obj.chkboxType=c;
		}
		obj.enable=true;
		obj.chkStyle=chkStyle;
	}
	return obj;
}
function show(){
	$("#message").html("<font class=\"onError\"><nobr>"+"没有查询结果!"+"</nobr></font>");
	$("#message").show();
	setTimeout('$("#message").hide();',1500);
}
function getUrl(treeId, treeNode) {
		var param = "";
		if(typeof(treeNode)!="undefined"&&treeNode!=null){
			param = "currentId="+treeNode.id;
        }else{
        	var param = "currentId=0";
        }
		return actionUrl+"&" + param;
	}
	//单选
	function onClick(event, treeId, treeNode){
	  if(treeNode){
	 	var zTree = $.fn.zTree.getZTreeObj(ztreeId);
	 	zTree.checkNode(treeNode, null, true);
	 	 if(treeNode.checked){
	 	 	zTree.expandNode(treeNode,true,false,true,true);
	 	 }
	  }
	  //获取当前点击的节点
	  currentClickNode = treeNode;
	  //获取当前用户的父节点
	  currentClickParentNode = treeNode.getParentNode();
	  //给模板外赋值
      window.parent.myztreeObj.pCurrentClickNode=currentClickNode;
	  window.parent.myztreeObj.pCurrentClickParentNode=currentClickParentNode;
	  
	}
	
	//多选checkbox
	function onCheck(event, treeId, treeNode){
	  //获取所有选择节点集合selectNodeList
	  var zTree = $.fn.zTree.getZTreeObj(ztreeId);
	  if(treeNode){
	  	if(treeNode.checked){
	  		zTree.expandNode(treeNode,true,false,true,true);
	  	}
	  }
	}
	
	
	function beforeExpand(treeId, treeNode) {
		if(treeNode.open){
			return false;
		}else{
			return true;
		}
	}
	
    function beforeAsync() {
	}
	
	function onAsyncSuccess(event, treeId, treeNode, msg) {
		if(treeNode){
			var nodeList=treeNode.children;
			if(treeNode.checked){
				for(var i=0;i<nodeList.length;i++){
					nodeList[i].checked=treeNode.checked;
					var ztree = $.fn.zTree.getZTreeObj(ztreeId);
			   		ztree.updateNode(nodeList[i]);
				}
			}
			openNode[treeNode.id]=treeNode;
			ShowSearchNode(nodeList);
	  		appendNode(expandNodes);
		}
	}
	function onAsyncError(event, treeId, treeNode, XMLHttpRequest, textStatus, errorThrown) {
		var zTree = $.fn.zTree.getZTreeObj(ztreeId);
		alert("异步获取数据出现异常。");
		zTree.updateNode(treeNode);
	}
	//高亮显示查出结果
	function ShowSearchNode(nodes){
	   var nodeList=null;
		   if(nodes){
		   		nodeList=nodes;
		   }else{
		   		var zTree = $.fn.zTree.getZTreeObj(ztreeId);
		   		nodeList=zTree.getNodes();
		   }
		   for(var i=0;i<nodeList.length;i++){
		   		for(var n in searchMsgs){
			   		if(nodeList[i].id==n){
			   			 nodeList[i].highlight = true;
			   			 var ztree = $.fn.zTree.getZTreeObj(ztreeId);
			   			 ztree.updateNode(nodeList[i]);
			   		}
		   		}
		   }
	}
	
	function getFontCss(treeId, treeNode) {
			return (!!treeNode.highlight) ? {color:"#FF0000"} : {color:"#333"};
	}
	//展开节点
	function appendNode(nodeIds){
	    for(var parentNodeId in nodeIds){
	    	var zTree = $.fn.zTree.getZTreeObj(ztreeId);
	    	var node=zTree.getNodeByParam("id", parentNodeId);
	    	if(node){
	    		zTree.expandNode(node,true,false,true,true);
	    	}
	  	}
	}
	
	//收集展开的节点
	function onExpand(event, treeId, treeNode){
		if(!treeNode.open){
			openNode[treeNode.id]=treeNode;
		}
		
	}
	
	//查询节点方法
	function ajaxSearch(){
       	searchMsgs="";
			expandNodes="";
			var treeObj = $.fn.zTree.getZTreeObj(ztreeId);
			var nodes = treeObj.transformToArray(treeObj.getNodes());
			  	for(var i=0;i<nodes.length;i++){
			  	    if(nodes[i].highlight==true){
			  	    	nodes[i].highlight=false;
			  			treeObj.updateNode(nodes[i]);
			  	    }
			  	}
			var searchInputValue=$("#searchInput").val();
			searchInputValue=searchInputValue.replace(/(^\s*)|(\s*$)/g, "");
			if(searchInputValue==""){
				return;
			}
			ShowSearchNode(nodes);
	       	$.ajax({
				data:{searchValue:searchInputValue},
				cache:false,
				type:"post",
				url:searchUrl,
				success:function(data){
				  if(data!=""){
				  	if(data=="[{},{}]"){
				  		show();
				  		return;
				  	}
				  	if(eval("nodeIds="+data)){
				  		expandNodes=nodeIds[0];
				  		searchMsgs=nodeIds[1];
					  	for(var n in openNode){
					  		treeObj.expandNode(openNode[n],false);
					  	}
					  	appendNode(nodeIds[0]);
					  	ShowSearchNode(nodes);
				  	}
				  }
				},
				error:function(){
					    window.location.reload();
				}
		    });
	}
	var curAsyncCount = 0,curStatus = "init";
	function asyncNodes(nodes) {
		if (!nodes) return;
		var zTree = $.fn.zTree.getZTreeObj(ztreeId);
		for (var i=0, l=nodes.length; i<l; i++) {
			if (nodes[i].isParent && nodes[i].zAsync) {
				asyncNodes(nodes[i].children);
			} else {
				zTree.reAsyncChildNodes(nodes[i], "refresh", true);
				curStatus = "";
			}
		}
	}
	//例外1
	function liwai(node){
		var nodes=[];
		if(node.children){
			nodes=node.children;
			for(var i=0;i<nodes.length;i++){
				if((nodes[i].type=='branch'||nodes[i].type=='department')&&nodes[i].getCheckStatus().checked&&!nodes[i].getCheckStatus().half){
					staticVar.push(nodes[i]);
					liwai(nodes[i]);
				}
			}
		}
		staticVar.push(node);
	}
	
	//获取需要传入后台的节点
	function getNodeByLevel(nodes,lv){
		for(var n=0;n<nodes.length;n++){
			if(nodes[n].getCheckStatus().checked){
				if(!nodes[n].getCheckStatus().half){
					if(nodes[n].type=='branch'||nodes[n].type=='department'){
						liwai(nodes[n]);
					}
					staticVar.push(nodes[n]);
				}else{
					getNodeByLevel(nodes[n].children,lv+1);
				}
			}	
		}
		return staticVar;
	}
	
	//部门树时过滤前台节点（全选）
	function getSelectFullNode(node){
		if(node.getCheckStatus().checked&&!node.getCheckStatus().half){
			if(node.type!="company"){
				var c1=node.children;
				if(c1){
					for(var j=0;j<c1.length;j++){
						getSelectFullNode(c1[j]);
					}
				}
				if(node.type=="branch"){
					staticVar.push(node);
				}
				if(node.type=="workgroup"){
					staticVar.push(node);
				}
			}else{
				staticVar.push(node);
			}
			return;
		}
		var c=node.children;
		if(c){
			for(var i=0;i<c.length;i++){
				if(c[i].getCheckStatus().checked){
					if(!c[i].getCheckStatus().half){
						if(c[i].type=='branch'||c[i].type=='department'){
							liwai(c[i]);
						}
						staticVar.push(c[i]);
					}else{
						getSelectFullNode(c[i]);
					}
				}else{
					getSelectFullNode(c[i]);
				}
			}
		}
	}

	//判断是否是选人树
	function getTreeType(){
		if(treeType=='COMPANY'){
	      	return 0;
		}else if(treeType=='MAN_DEPARTMENT_TREE'){
			return 1;
		}else if(treeType=='MAN_GROUP_TREE'){
			return 2;
		}else if(treeType=='DEPARTMENT_TREE'){
			return 3;
		}else if(treeType=='GROUP_TREE'){
			return 4;
		}else if(treeType=='DEPARTMENT_WORKGROUP_TREE'){
			return 5;
		}else{
			alert('出错了!!!');
		}
	}
	//点击确认后事件函数
	function _ok_ztree(){
	  var isManTree=getTreeType();
	  var type='type';
	  if(chkStyle=='checkbox'){//多选时
	    var zTree = $.fn.zTree.getZTreeObj(ztreeId);
        var checkNodes = zTree.getCheckedNodes(true);
        if(checkNodes.length==0){
            alert("请选择!");
            return;
        }
        window.parent.iscollback=true;
		var treeObj = $.fn.zTree.getZTreeObj(ztreeId);
		var nodes = treeObj.getNodesByFilter(function filter(obk) {
			return (obk.level == 0);
		});
		var msg=treeType+"&"+userWithoutDeptVisible+":";
		var name="";
		if(isManTree==0||isManTree==1||isManTree==2){
				staticVar=[];
				newNodes=getNodeByLevel(nodes,0);
				var distinct={};
				var map={};
				var nameMap={};
				for(var p=0;p<newNodes.length;p++){
					distinct[newNodes[p].id]=newNodes[p];
				}
				for(var temp in distinct){
					//type:[nodes]
					var arr=temp.split("_");
					var nodeType=distinct[temp].type;
					if(nodeType=="hasWorkgroupBranch"){
						nodeType="workgroup";
						if(distinct[temp].children){
							if(!nameMap[nodeType]){
								nameMap[nodeType]=[];
								for(var t=0;t<distinct[temp].children.length;t++){
									nameMap[nodeType].push(distinct[temp].children[t]);
								}
							}else{
								for(var t=0;t<distinct[temp].children.length;t++){
									nameMap[nodeType].push(distinct[temp].children[t]);
								}		
							}
						}
					}else{
						if(!nameMap[nodeType]){
							nameMap[nodeType]=[];
							nameMap[nodeType].push(distinct[temp]);
						}else{
							nameMap[nodeType].push(distinct[temp]);			
						}
					}
					
					//拼接ID map
					if(!map[arr[0]]){
						map[arr[0]]=arr[1];
					}else{
						map[arr[0]]=map[arr[0]]+","+arr[1];
					}
				}
				for(var key in map){
					msg=msg+key+"_"+map[key]+";";
				}
				msg=msg.substring(0,msg.length-1);
		}else{
			staticVar=[];		
			var map={};
			for(var i=0;i<checkNodes.length;i++){
				var arr=checkNodes[i].id.split("_");
				var key=arr[0];
				var value=arr[1];
				if(checkNodes[i].getCheckStatus().half&&(checkNodes[i].type="branch"||checkNodes[i].type=="department")){
					if(!map["realDepartment"]){
						map["realDepartment"]=arr[1];
					}else{
						map["realDepartment"]=map["realDepartment"]+","+arr[1];
					}
				}
			}
			for(var c=0;c<nodes.length;c++){
				getSelectFullNode(nodes[c]);
			}
			for(var i=0;i<staticVar.length;i++){
				var arr=staticVar[i].id.split("_");
				var key=arr[0];
				var value=arr[1];
				var nodeType=staticVar[i].type;
				if(nodeType=="hasWorkgroupBranch"){
					nodeType="workgroup";
					if(staticVar[i].children){
						for(var t=0;t<staticVar[i].children.length;t++){
							if(!map[nodeType]){
								map[nodeType]=staticVar[i].children[t].id.split("_")[1];
							}else{
								map[nodeType]=map[nodeType]+","+staticVar[i].children[t].id.split("_")[1];
							}
						}
					}
				}else{
					if(!map[arr[0]]){
						map[arr[0]]=arr[1];
					}else{
						map[arr[0]]=map[arr[0]]+","+arr[1];
					}
				}
			}
			staticVar=[];
			for(var key in map){
					msg=msg+key+"_"+map[key]+";";
			}
			msg=msg.substring(0,msg.length-1);
		}
		$.ajax({
				data:{msg:msg,showBranch:showBranch,userWithoutDeptVisible:userWithoutDeptVisible},
				cache:false,
				type:"post",
				url:webapp+"/portal/getSelectNodeValue.action",
				success:function(data){
					eval(data);
					if(multiObj){
						window.parent.multiObj=multiObj;
						var id="";
						var name="";
						if(isManTree==0||isManTree==1||isManTree==2){
							id=window.parent.ztree.getIds();
							name=window.parent.ztree.getNames();
						}else if(isManTree==3){
							id=window.parent.ztree.getDepartmentIds();
							name=window.parent.ztree.getDepartmentNames();
						}else if(isManTree==4){
							id=window.parent.ztree.getWorkGroupIds();
							name=window.parent.ztree.getWorkGroupNames();
						}else if(isManTree==5){
							id='workgroupIds:'+window.parent.ztree.getWorkGroupIds()+';departmentIds'+window.parent.ztree.getDepartmentIds();
							name='workgroupIds:'+window.parent.ztree.getWorkGroupNames()+';departmentIds'+window.parent.ztree.getDepartmentNames();
						}
						fillMultiValue(multiObj,name,id);
						window.parent.$.colorbox.close();				
					}
				},
				error:function(){
					    window.location.reload();
				}
		    });
		return;
        
	  }else{//单选时
	      if(currentClickNode==null){	      
	        alert("请选!");
	        return;
	      }else{
	      	if(treeType=='MAN_DEPARTMENT_TREE'||treeType=='COMPANY'||treeType=='MAN_GROUP_TREE'){
	      		if(currentClickNode[type]!='user'){
	      			alert('请选择人员!');
	      			return;
	      		}
	      	}else if(treeType=='DEPARTMENT_TREE'){
	      		if(currentClickNode[type]!='department'&&currentClickNode[type]!='branch'){
	      			alert('请选择部门!');
	      			return;
	      		}
	      	}else if(treeType=='GROUP_TREE'){
	      		if(currentClickNode[type]!='workgroup'){
	      			alert('请选择工作组!');
	      			return;
	      		}
	      	}else if(treeType=='DEPARTMENT_WORKGROUP_TREE'){
	      		if(currentClickNode[type]!='department'&&currentClickNode[type]!='branch'&&currentClickNode[type]!='workgroup'){
	      			alert('请选择部门或工作组!');
	      			return;
	      		}
	      	}
	      }
	  }
	  window.parent.iscollback=true;
	  fillValue();
	  //关闭弹框
	  window.parent.$.colorbox.close();
	}
	//单选赋值函数
	function fillValue(){
		eval("obj="+currentClickNode.data);
		var hiddenMsg=obj['id'];
		var showMsg=obj['name'];
		if(obj[showThing]){
			showMsg=obj[showThing];
		}
		if(obj[hiddenThing]){
			hiddenMsg=obj[showThing];
		}
	 	if(hiddenInput!=null&&hiddenInput!=""){
			parent.$("#"+hiddenInput).attr("value",hiddenMsg);
		}
		if(showInput!=null&&showInput!=""){
			parent.$("#"+showInput).attr("value",showMsg);
		}
	}
	//多选赋值函数
	function fillMultiValue(multiObj,defualtNames,defualtIds){
		var hiddenMsg=defualtIds;
		var showMsg=defualtNames;
		if(treeType=='MAN_DEPARTMENT_TREE'||treeType=='COMPANY'||treeType=='MAN_GROUP_TREE'){
			if(multiObj['user']){
				if(multiObj['user'][showThing]){
					showMsg=multiObj['user'][showThing];
				}
				if(multiObj['user'][hiddenThing]){
					hiddenMsg=multiObj['user'][hiddenThing];
				}
			}
      	}else if(treeType=='DEPARTMENT_TREE'){
      		if(multiObj['department']){
				if(multiObj['department'][showThing]){
					showMsg=multiObj['department'][showThing];
				}
				if(multiObj['department'][hiddenThing]){
					hiddenMsg=multiObj['department'][hiddenThing];
				}
			}
      	}else if(treeType=='GROUP_TREE'){
      		if(multiObj['workgroup']){
				if(multiObj['workgroup'][showThing]){
					showMsg=multiObj['workgroup'][showThing];
				}
				if(multiObj['workgroup'][hiddenThing]){
					hiddenMsg=multiObj['workgroup'][hiddenThing];
				}
			}
      	}else if(treeType=='DEPARTMENT_WORKGROUP_TREE'){
      		if(multiObj['department']){
				if(multiObj['department'][showThing]){
					showMsg='部门:'+multiObj['department'][showThing]+';';
				}
				if(multiObj['department'][hiddenThing]){
					hiddenMsg='workgroup:'+multiObj['department'][hiddenThing]+';';
				}
			}
			if(multiObj['workgroup']){
				if(multiObj['workgroup'][showThing]){
					showMsg=showMsg+'工作组:'+multiObj['workgroup'][showThing]+';';
				}
				if(multiObj['workgroup'][hiddenThing]){
					hiddenMsg=hiddenMsg+'workgroup:'+multiObj['workgroup'][hiddenThing]+';';
				}
			}
      	}
	 	if(hiddenInput!=null&&hiddenInput!=""){
			parent.$("#"+hiddenInput).attr("value",hiddenMsg);
		}
		if(showInput!=null&&showInput!=""){
			parent.$("#"+showInput).attr("value",showMsg);
		}
	}
	
	
	//showThing:'{"company":"name","department":"id","user":"email","workgroup":"name"}'
	//showInput:显示input的id或者隐藏域的id
	//showThing:内容设置字符串
	//chkStyle:多选还是单选
	//isFill:是否往input框填值
	function getClickValueByNode(param){
        	eval("obj="+currentClickNode.data);
        	if(obj){
        		return obj[param];
        	}else{
        		return "";
        	}
        	
    }