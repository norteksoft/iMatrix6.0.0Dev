$(document).ready(function (){ 
    var leafPageVal = $("#leafPageId").val();
	if(leafPageVal=='true'){
	    //初始化页签
		$( "#tabs" ).tabs({select:function(event,ui){
		}});
		 $("#defaultTreeType").attr("value",defaultTreeType);
         $("#defaultTreeValue").attr("value",defaultTreeValue);
         singleLeafPageTree(defaultTreeType);
	}else{
	     singleTree();
	}
});

//树脚本
function singleTree(){
	$.ajaxSetup({cache:false});
	   $("#"+treeId).bind("search.jstree",function(e,data){
				$.jstree.rollback(data.rlbk); 
         }).jstree({
		"json_data":{
				"ajax" : { "url" : actionUrl,
							"data" : function (n) {  
								return { currentId : n!=-1 ? n.attr("id") : 0 };   
							}
						}
		   },
		   "themes" : {  
			  "theme" : "classic",  
			  "dots" : true,  
			  "icons" : true 
			 },
			"search" : {
					"ajax" : {
						"url" : searchUrl,
						"async":true,
						// You get the search string as a parameter
						"data" : function (str) {
							return { 
								"searchValue" : str 
							}; 
						},
						"success":function(data){
							$("#"+treeId).find("li").find("a").removeClass("jstree-search");  
							var arr=eval(data);
							for(var i=0;i<arr.length;i++){
									var deptInfos = arr[i].split(";");
									var deptInfo = deptInfos[0];
									var parentInfo = deptInfos[1];
									var openDeptInfo = deptInfo;
									if(parentInfo!=""){
										openDeptInfo = parentInfo;
									}
									$.jstree._reference($("#"+treeId)).open_node($("li[id="+openDeptInfo+"]"),
									function(){
										//打开子部门节点
										for(var j=0;j<arr.length;j++){
											var jdeptInfos = arr[j].split(";");
											var jdeptInfo = jdeptInfos[0];
											var jparentInfo = jdeptInfos[1];
											if(jparentInfo!=""){
												$.jstree._reference($("li[id="+jparentInfo+"]")).open_node($("li[id="+jdeptInfo+"]"),
												function(){
													var result = $("#"+treeId).find("a" +  ":" +"contains" + "(" + $("#searchInput").attr("value") + ")");
													result.addClass("jstree-search");
												},true);
											}
										}
										//添加选中样式
										var result = $("#"+treeId).find("a" +  ":" +"contains" + "(" + $("#searchInput").attr("value") + ")");
										result.addClass("jstree-search");
									},true);
									
							}
							
						}
					}
				},
			 "types" :{
					"types" : {
						"company" : {
							"icon" : {
								"image" : resourceCtx+"/widgets/jstree/themes/root.gif"
							}
						},
						"folder" : {
							"icon" : {
								"image" : resourceCtx+"/widgets/jstree/themes/folder.gif"
							}
						},
						"user" : {
							"icon" : {
								"image" : resourceCtx+"/widgets/jstree/themes/file.gif"
							}
						},
						"onlineUser" : {
						"icon" : {
							"image" : resourceCtx+"/widgets/jstree/themes/online.gif"
							}
						}
					}
				 }, 
			"ui":{"select_limit":1},
		   "plugins" : [ "themes", "json_data","types","ui","search" ]
		}).bind("select_node.jstree",function(e){
			id=$(".jstree-clicked").parent().attr("id");
			if(getType()=='user'){
				getId();
				getName();
				getType();
				getTreeType();
				getLoginName();
				getEmail();
				getHonorificName();
		        getWeight();
		        getUserDepartmentName();
		        getDepartmentId();
		        getSubCompanyName();
		        getSubCompanyId();
			}else if(getType()=='department'){
				getDepartmentName();
				getDepartmentId();
				getDeptSubCompanyName();
		        getDeptSubCompanyId();
		        getDepartmentCode();
		        getDepartmentShortTitle();
			}else if(getType()=='workGroup'){
				getWorkGroupName();
				getWorkGroupId();
			}
		});
}
	
function search_fun(){
	$("#"+treeId).jstree("search",$("#searchInput").val());
}

var id="";	
//从树上获取json
//treeType:user,department,workGroup,默认为"user"
function getInfo(treeType){
    //id=$("#"+treeId).find("li a.jstree-clicked").parent().attr("id");
	var currentTreeType="user";
	if(treeType!=""&&typeof (treeType)!="undefined"){
		currentTreeType=treeType;
	}
 	if(id!=""){
        var info="[";
		if(currentTreeType=="user"){
	     	var type=id.substring(0,id.indexOf(split_one));     	
	     	if(type=="user"){
	     	 info+="{type:"+"\""+id.substring(0,id.indexOf(split_one))+"\""
			     +",id:"+"\""+id.substring(id.indexOf(split_one)+2,id.indexOf(split_two))+"\""
			     +",name:"+"\""+id.substring(id.indexOf(split_two)+2,id.indexOf(split_three))+"\""
			     +",loginName:"+"\""+id.substring(id.indexOf(split_three)+2,id.indexOf(split_four))+"\""
			     +",parentType:"+"\""+id.substring(id.indexOf(split_five)+2,id.indexOf(split_eight))+"\""
			     +",parentName:"+"\""+id.substring(id.indexOf(split_four)+2,id.indexOf(split_five))+"\""
			     +",nobranchId:"+"\""+id.substring(id.indexOf(split_one)+2,id.indexOf(split_nine))+"\""
			     +",branchName:"+"\""+id.substring(id.indexOf(split_nine)+2,id.indexOf(split_ten))+"\""
			     +",subCompanyId:"+"\""+id.substring(id.indexOf(split_ten)+2,id.length)+"\""+"},";			     
	     	}
		}else if(currentTreeType=="department"){
		   var type=id.substring(0,id.indexOf(split_one));
		   if(type=="department"){	   
		    info+="{type:"+"\""+id.substring(0,id.indexOf(split_one))+"\""
			     +",id:"+"\""+id.substring(id.indexOf(split_one)+2,id.indexOf(split_two))+"\""
			     +",name:"+"\""+id.substring(id.indexOf(split_two)+2,id.indexOf(split_three))+"\""
			     +",branchName:"+"\""+id.substring(id.indexOf(split_three)+2,id.indexOf(split_four))+"\""
				 +",isBranch:"+"\""+id.substring(id.indexOf(split_four)+2,id.indexOf(split_five))+"\""
			     +",subCompanyId:"+"\""+id.substring(id.indexOf(split_five)+2,id.indexOf(split_six))+"\""
			     +",shortTitle:"+"\""+id.substring(id.indexOf(split_six)+2,id.indexOf(split_seven))+"\""
			     +",code:"+"\""+id.substring(id.indexOf(split_seven)+2,id.length)+"\""+"},";
		   }
		}else if(currentTreeType=="workGroup"){
		   var type=id.substring(0,id.indexOf(split_one));
		   if(type=="workGroup"){
		    info+="{type:"+"\""+id.substring(0,id.indexOf(split_one))+"\""
			     +",id:"+"\""+id.substring(id.indexOf(split_one)+2,id.indexOf(split_two))+"\""
			     +",name:"+"\""+id.substring(id.indexOf(split_two)+2,id.indexOf(split_three))+"\""
			     +",branchName:"+"\""+id.substring(id.indexOf(split_three)+2,id.indexOf(split_four))+"\""
			     +",subCompanyId:"+"\""+id.substring(id.indexOf(split_four)+2,id.length)+"\""+"},";
		   }		   
		}else if(currentTreeType=="departmentAndGroup"){
		   var type=id.substring(0,id.indexOf(split_one));
		   if(type=="department"){
		    info+="{type:"+"\""+id.substring(0,id.indexOf(split_one))+"\""
			     +",id:"+"\""+id.substring(id.indexOf(split_one)+2,id.indexOf(split_two))+"\""
			     +",name:"+"\""+id.substring(id.indexOf(split_two)+2,id.indexOf(split_three))+"\""
			     +",branchName:"+"\""+id.substring(id.indexOf(split_three)+2,id.indexOf(split_four))+"\""
				 +",isBranch:"+"\""+id.substring(id.indexOf(split_four)+2,id.length)+"\""
			     +",subCompanyId:"+"\""+id.substring(id.indexOf(split_four)+2,id.length)+"\""+"},";				 
		   }else if(type=="workGroup"){
		    info+="{type:"+"\""+id.substring(0,id.indexOf(split_one))+"\""
			     +",id:"+"\""+id.substring(id.indexOf(split_one)+2,id.indexOf(split_two))+"\""
			     +",name:"+"\""+id.substring(id.indexOf(split_two)+2,id.indexOf(split_three))+"\""
			     +",branchName:"+"\""+id.substring(id.indexOf(split_three)+2,id.indexOf(split_four))+"\""
			     +",subCompanyId:"+"\""+id.substring(id.indexOf(split_four)+2,id.length)+"\""+"},";			     
		   }
		}
	    if(info.indexOf(",")>=0){
	    	info=info.substring(0,info.length-1);
	    }
       info+="]";
       window.parent.sInfor = info;
   return info;
  }else{
  	return "";
  }
}

//页签点击事件
function changeSelected(type,value){
    $("#defaultTreeType").attr("value",type);
    $("#defaultTreeValue").attr("value",value);
     singleLeafPageTree(type);
}
//初始化页签树
function singleLeafPageTree(type){
$.ajaxSetup({cache:false});
	   $("#"+treeId).jstree({
		"json_data":{
				"ajax" : { "url" : actionUrl+"?treeType="+type,
							"data" : function (n) {  
								return { currentId : n!=-1 ? n.attr("id") : 0 };   
							}
						}
		   },
		   "themes" : {  
			  "theme" : "classic",  
			  "dots" : true,  
			  "icons" : true 
			 },
			"search" : {
					"ajax" : {
						"url" : searchUrl+"?treeType="+type,
						"async":true,
						// You get the search string as a parameter
						"data" : function (str) {
							return { 
								"searchValue" : str 
							}; 
						},
						"success":function(data){
							$("#"+treeId).find("li").find("a").removeClass("jstree-search");  
							var arr=eval(data);
							for(var i=0;i<arr.length;i++){
									var deptInfos = arr[i].split(";");
									var deptInfo = deptInfos[0];
									var parentInfo = deptInfos[1];
									var openDeptInfo = deptInfo;
									if(parentInfo!=""){
										openDeptInfo = parentInfo;
									}
									$.jstree._reference($("#"+treeId)).open_node($("li[id="+openDeptInfo+"]"),
									function(){
										//打开子部门节点
										for(var j=0;j<arr.length;j++){
											var jdeptInfos = arr[j].split(";");
											var jdeptInfo = jdeptInfos[0];
											var jparentInfo = jdeptInfos[1];
											if(jparentInfo!=""){
												$.jstree._reference($("li[id="+jparentInfo+"]")).open_node($("li[id="+jdeptInfo+"]"),
												function(){
													var result = $("#"+treeId).find("a" +  ":" +"contains" + "(" + $("#"+inputId).attr("value") + ")");
													result.addClass("jstree-search");
												},true);
											}
										}
										//添加选中样式
										var result = $("#"+treeId).find("a" +  ":" +"contains" + "(" + $("#"+inputId).attr("value") + ")");
										result.addClass("jstree-search");
									},true);
									
							}
							
						}
					}
				},
			 "types" :{
					"types" : {
						"company" : {
							"icon" : {
								"image" : ctx+"/widgets/jstree/themes/root.gif"
							}
						},
						"folder" : {
							"icon" : {
								"image" : ctx+"/widgets/jstree/themes/folder.gif"
							}
						},
						"user" : {
							"icon" : {
								"image" : ctx+"/widgets/jstree/themes/file.gif"
							}
						},
						"onlineUser" : {
						"icon" : {
							"image" : resourceCtx+"/widgets/jstree/themes/online.gif"
							}
						}	
					}
				 }, 
			"ui":{"select_limit":1},
		   "plugins" : [ "themes", "json_data","types","ui","search" ]
		}).bind("select_node.jstree",function(e){
				    id=$(".jstree-clicked").parent().attr("id");
					getId();
				    getName();
				    getType();
				    getLoginName();
				}).bind("search.jstree",function(e,data){
		$.jstree.rollback(data.rlbk); 
     });
			
}
	//用户调用的方法
	//<---start--->
	//用户id
	function getId(){
		var ids=id.substring(id.indexOf(split_one)+2,id.indexOf(split_two));
		window.parent.singleId=ids;
		return ids;
	}
	//用户名称	
	function getName(){
		var name=id.substring(id.indexOf(split_two)+2,id.indexOf(split_three));
		window.parent.singleName=name;
		return name;
	}	
	//树类型		
	function getType(){
		var type=id.substring(0,id.indexOf(split_one));
		window.parent.singleType=type;
		return type;
	}	
	function getTreeType(){
	   var treeType = $("#defaultTreeType").val();
	   window.parent.mTreeType=treeType;     
	   return treeType;
	}
	//用户登录名
	function getLoginName(){
		var loginName=id.substring(id.indexOf(split_three)+2,id.indexOf(split_four));
		window.parent.singleLoginName=loginName;
		return loginName;
	}
	//用户分支机构名称
	function getSubCompanyName(){
		var subCompanyName=id.substring(id.indexOf(split_nine)+2,id.indexOf(split_ten));
		window.parent.singleSubCompanyName=subCompanyName;
		return subCompanyName;
	}
	//用户分支机构ID
	function getSubCompanyId(){
		var subCompanyId=id.substring(id.indexOf(split_ten)+2,id.length);
		window.parent.singleSubCompanyId=subCompanyId;
		return subCompanyId;
	}
	//部门分支机构名称
	function getDeptSubCompanyName(){
		var subCompanyName=id.substring(id.indexOf(split_three)+2,id.indexOf(split_four));
		window.parent.singleSubCompanyName=subCompanyName;
		return subCompanyName;
	}
	//部门分支机构ID
	function getDeptSubCompanyId(){
		var subCompanyId=id.substring(id.indexOf(split_five)+2,id.indexOf(split_six));
		window.parent.singleSubCompanyId=subCompanyId;
		return subCompanyId;
	}
	
	//部门名称
	function getDepartmentName(){
		var departmentName=id.substring(id.indexOf(split_three)+2,id.length);
		window.parent.singleDepartmentName=departmentName;     
		return departmentName;
	}
	//部门id
	function getDepartmentId(){
		var departmentId=id.substring(id.indexOf(split_one)+2,id.indexOf(split_two));
		window.parent.singleDepartmentId=departmentId;     
		return departmentId;
	}
	//部门编号
	function getDepartmentCode(){
		var departmentCode=id.substring(id.indexOf(split_seven)+2,id.length);
		window.parent.singleDepartmentCode=departmentCode;     
		return departmentCode;
	}
	//部门简称
	function getDepartmentShortTitle(){
		var departmentShortTitle=id.substring(id.indexOf(split_six)+2,id.indexOf(split_seven));
		window.parent.singleDepartmentShortTitle=departmentShortTitle;     
		return departmentShortTitle;
	}
	
	//工作组名称
	function getWorkGroupName(){
		var workGroupName=id.substring(id.indexOf(split_three)+2,id.indexOf(split_four));
		window.parent.singleWorkGroupName=workGroupName;     
		return workGroupName;
	}
	//工作组id
	function getWorkGroupId(){
		var workGroupId=id.substring(id.indexOf(split_one)+2,id.indexOf(split_two));
		window.parent.singleWorkGroupId=workGroupId;     
		return workGroupId;
	}
	
	//用户邮件
	function getEmail(){
		var email=id.split(split_eight)[1].split(split_seven)[0];
		window.parent.singleEmail=email;     
		return email;
	}

	//用户尊称
	function getHonorificName(){
		var honorificName=id.split(split_eight)[1].split(split_seven)[1];
		window.parent.singleHonorificName=honorificName;     
		return honorificName;
	}
	//用户权重
	function getWeight(){
		var weight=id.split(split_eight)[1].split(split_seven)[2];
		window.parent.singleWeight=weight;     
		return weight;
	}
	//获取用户部门名称
	function getUserDepartmentName(){
		var deptName=id.substring(id.indexOf(split_five)+2,id.indexOf(split_six));
		window.parent.singleUserDeptName=deptName;     
		return deptName;
	}
	
//<---END--->

function returnParamater(obj,para){
    var result = "";
    if(para=='id'){
	result=obj.id;
	}else if(para=='name'){
	result=obj.name;
	}else if(para=='loginName'){
	result=obj.loginName;
	}else if(para=='email'){
	result=obj.email;
	}
	return 	result;
}
    
function createValueStr(obj,treeValue){
    var result = "";
    var values = treeValue.split(",");
    var val="";
	for(var j=0;j<values.length;j++){
		result+=returnParamater(obj,values[j])+",";
	}
	return result.substring(0,result.length-1);
}
		
function selectMan(treeValue){
	var info=getInfo("user");
	if(info!=""&&info!="[]"){
		var user=eval(info);
		if(user!=''){
			var type=user[0].type;
			if(type == "user"){
				if(hiddenInputId!='NOHiddenInputId'){
					window.parent.$("#"+hiddenInputId).attr("value", createValueStr(user[0],treeValue));
				}
				//多分支机构情况下，名称后加分支机构名
				var branchName = user[0].branchName;
				var name = "";
				if(isSingleCompany=="true"){
				   name = user[0].name+"("+branchName+")";
				}else{
				   name = user[0].name;
				}
				if(showInputId!='NOShowInputId'){
					window.parent.$("#"+showInputId).attr("value", name);
				}
				
				return true;
			}else{
				alert("请选择人员");
				return false;
			}
		}else{
		      alert("请选择正确的节!");
		      return false;
		}
	}else{
		alert("请选择人员");
		return false;
	}
}
	
function selectDepartment(treeValue){
	var info=getInfo("department");
	if(info!=""&&info!="[]"){
		var user=eval(info);
		if(user!=''){
			var type=user[0].type;
			var isBranch=user[0].isBranch;
			if(type == "department"&&isBranch!='true'){
			if(hiddenInputId!='NOHiddenInputId'){
				window.parent.$("#"+hiddenInputId).attr("value", createValueStr(user[0],treeValue));
			}	
				//多分支机构情况下，名称后加分支机构名
				var branchName = user[0].branchName;
				var name = "";
				if(isSingleCompany=="true"){
				   name = user[0].name+"("+branchName+")";
				}else{
				   name = user[0].name;
				}
				if(showInputId!='NOShowInputId'){
					window.parent.$("#"+showInputId).attr("value", name);
				}
				return true;
			}else{
				alert("请选择部门");
				return false;
			}
		}else{
		      alert("请选择正确的节!");
		      return false;
		}
	}else{
		alert("请选择部门");
		return false;
	}
}
function selectWorkGroup(treeValue){
	var info=getInfo("workGroup");
	if(info!=""&&info!="[]"){
		var user=eval(info);
		if(user!=''){
			var type=user[0].type;
			if(type == "workGroup"){
			if(hiddenInputId!='NOHiddenInputId'){
				window.parent.$("#"+hiddenInputId).attr("value",createValueStr(user[0],treeValue));
			}	
				//多分支机构情况下，名称后加分支机构名
				var branchName = user[0].branchName;
				var name = "";
				if(isSingleCompany=="true"){
				   name = user[0].name+"("+branchName+")";
				}else{
				   name = user[0].name;
				}
			if(showInputId!='NOShowInputId'){
				window.parent.$("#"+showInputId).attr("value", name);
			}
				return true;
			}else{
				alert("请选择工作组");
				return false;
			}
		}else{
		      alert("请选择正确的节!");
		      return false;
		}
	}else{
		alert("请选择工作组");
		return false;
	}
}
function selectDepartmentAndWorkGroup(treeValue){
	var info=getInfo("departmentAndGroup");
	if(info!=""&&info!="[]"){
		var user=eval(info);
		if(user!=''){
			var type=user[0].type;
			var isBranch=user[0].isBranch;
			if(type == "department"&&isBranch!='true'){
			if(hiddenInputId!='NOHiddenInputId'){
				window.parent.$("#"+hiddenInputId).attr("value", createValueStr(user[0],treeValue));
			}	
				//多分支机构情况下，名称后加分支机构名
				var branchName = user[0].branchName;
				var name = "";
				if(isSingleCompany=="true"){
				   name = user[0].name+"("+branchName+")";
				}else{
				   name = user[0].name;
				}
			if(showInputId!='NOShowInputId'){
				window.parent.$("#"+showInputId).attr("value", name);
			}	
				return true;
			}else if(type == "workGroup"){
			if(hiddenInputId!='NOHiddenInputId'){
			    window.parent.$("#"+hiddenInputId).attr("value", createValueStr(user[0],treeValue));
			}    
				//多分支机构情况下，名称后加分支机构名
				var branchName = user[0].branchName;
				var name = "";
				if(isSingleCompany=="true"){
				   name = user[0].name+"("+branchName+")";
				}else{
				   name = user[0].name;
				}
			if(showInputId!='NOShowInputId'){
				window.parent.$("#"+showInputId).attr("value", name);
			}
				return true;
			}else{
				alert("请选择部门或工作组");
				return false;
			}
		}else{
		      alert("请选择正确的节!");
		      return false;
		}
	}else{
		alert("请选择部门或工作组");
		return false;
	}
}
//提交按钮点击事件
function _ok_tree(){
    var treeType = $("#defaultTreeType").val();
    var treeValue = $("#defaultTreeValue").val();
    var clickFlag = true;
	if(treeType=='COMPANY'||treeType=='MAN_DEPARTMENT_GROUP_TREE'||treeType=='MAN_DEPARTMENT_TREE'||treeType=='MAN_GROUP_TREE'){
	    clickFlag = selectMan(treeValue);
	}else if(treeType=='DEPARTMENT_TREE'){
	    clickFlag = selectDepartment(treeValue);
	}else if(treeType=='GROUP_TREE'){
	    clickFlag = selectWorkGroup(treeValue);
	}else if(treeType=='DEPARTMENT_WORKGROUP_TREE'){
	    clickFlag = selectDepartmentAndWorkGroup(treeValue);
	}
	var tor;
	if(hiddenInputId!='NOHiddenInputId'){
		tor=window.parent.$("#"+hiddenInputId).attr("value");
	}
	if(typeof tor == "undefined" || tor == ""){
		//alert("请选择正确的节!");
	}else{
	   if(clickFlag){
	    window.parent.okEnsure="OK";
		window.parent.$.colorbox.close();
	   }
	}
}