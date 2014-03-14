$(document).ready(function (){
	var leafPageVal = $("#leafPageId").val();
	if(leafPageVal=='true'){
	     //初始化页签
		 $( "#tabs" ).tabs({select:function(event,ui){
		 }});
		 $("#"+treeId).attr("type",defaultTreeType);
		 $("#"+defaultTreeType).attr("value",defaultTreeType);
		 $("#"+defaultTreeValue).attr("value",defaultTreeValue);
	     multipleLeafPageTree(defaultTreeType);
	}else{
	     multipleTree();
	}
});
//树脚本
function multipleTree(){     
$.ajaxSetup({cache:false});
$("#"+treeId).jstree({
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
								$.jstree._reference($("#"+treeId)).open_node($("li[id='"+openDeptInfo+"']"),
								function(){
									//打开子部门节点
									for(var j=0;j<arr.length;j++){
										var jdeptInfos = arr[j].split(";");
										var jdeptInfo = jdeptInfos[0];
										var jparentInfo = jdeptInfos[1];
										if(jparentInfo!=""){
											$.jstree._reference($("li[id='"+jparentInfo+"']")).open_node($("li[id='"+jdeptInfo+"']"),
											function(){
												//添加子部门选中样式
												var result = $("#"+treeId).find("a" +  ":" +"contains" + "(" + $("#searchInput").attr("value") + ")");
												result.addClass("jstree-search");
											},true);
										}
									}
									//添加父部门选中样式
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
	   "plugins" : [ "themes", "json_data","checkbox","types","search" ]
	}).bind("search.jstree",function(e,data){
	$.jstree.rollback(data.rlbk); 
 });
}

//树function
function search_fun(){
	$("#"+treeId).jstree("search",$("#searchInput").val());
}
//树function
function allUsers(id){
	var lists = $("#"+id).find("li.jstree-checked");
	var v="" ;
	for(var i=0; i<lists.length; i++){
		v+=$(lists[i]).attr("id");
		if(i!=lists.length-1)
			v+=";";
	}
	if(v!=""){
		var arr=v.split(";");
		return arr;
	}else{
		return "";
	}
}
//树function	
function selectSubDeptUser(treeObj,obj){
	$("#"+treeId).bind("open_node.jstree",function(){
		var lists=$(obj).find("li");
		for(var i=0;i<lists.length;i++){
			selectSubDeptUser(treeObj,$(lists[i]).is("li") ? $(lists[i]) : $(lists[i]).parent());
		}
		$(obj).find("li").andSelf().removeClass("jstree-unchecked jstree-undetermined").addClass("jstree-checked");
		if($(treeObj).data.ui) { $(treeObj).data.ui.last_selected = $(obj); }
		$(treeObj).data.checkbox.last_selected = $(obj);
	});
}
	
//treeType:user,department,workGroup,默认为"user"
//树取值function
function getInfo(treeType){
	var currentTreeType="user";
	if(treeType!=""&&typeof (treeType)!="undefined"){
		currentTreeType=treeType;
	}
      var arr=allUsers(treeId);
          if(arr!=""){
	            var info="[";
				if(currentTreeType=="user"){
			     	for(var i=0; i<arr.length; i++){
				     	var type=arr[i].substring(0,arr[i].indexOf(split_one));
						  info+="{type:"+"\""+arr[i].substring(0,arr[i].indexOf(split_one))+"\""
						     +",id:"+"\""+arr[i].substring(arr[i].indexOf(split_one)+2,arr[i].indexOf(split_two))+"\""
						     +",name:"+"\""+arr[i].substring(arr[i].indexOf(split_two)+2,arr[i].indexOf(split_three))+"\""
						     +",loginName:"+"\""+arr[i].substring(arr[i].indexOf(split_three)+2,arr[i].indexOf(split_four))+"\""
						     +",parentType:"+"\""+arr[i].substring(arr[i].indexOf(split_five)+2,arr[i].indexOf(split_eight))+"\""
						     +",parentName:"+"\""+arr[i].substring(arr[i].indexOf(split_four)+2,arr[i].indexOf(split_five))+"\""
						     +",nobranchId:"+"\""+arr[i].substring(arr[i].indexOf(split_one)+2,arr[i].indexOf(split_nine))+"\""
						     +",branchName:"+"\""+arr[i].substring(arr[i].indexOf(split_nine)+2,arr[i].indexOf(split_ten))+"\""
						     +",subCompanyId:"+"\""+arr[i].substring(arr[i].indexOf(split_ten)+2,arr[i].length)+"\""+"},";
				    }				    
				}else if(currentTreeType=="department"){
					for(var i=0;i<arr.length;i++){
					   var type=arr[i].substring(0,arr[i].indexOf(split_one));
					    info+="{type:"+"\""+arr[i].substring(0,arr[i].indexOf(split_one))+"\""
						     +",id:"+"\""+arr[i].substring(arr[i].indexOf(split_one)+2,arr[i].indexOf(split_two))+"\""
						     +",name:"+"\""+arr[i].substring(arr[i].indexOf(split_two)+2,arr[i].indexOf(split_three))+"\""
						     +",branchName:"+"\""+arr[i].substring(arr[i].indexOf(split_three)+2,arr[i].indexOf(split_four))+"\""
						     +",isBranch:"+"\""+arr[i].substring(arr[i].indexOf(split_four)+2,arr[i].indexOf(split_five))+"\""
						     +",subCompanyId:"+"\""+arr[i].substring(arr[i].indexOf(split_five)+2,arr[i].indexOf(split_six))+"\""
						     +",shortTitle:"+"\""+arr[i].substring(arr[i].indexOf(split_six)+2,arr[i].indexOf(split_seven))+"\""
						     +",code:"+"\""+arr[i].substring(arr[i].indexOf(split_seven)+2,arr[i].length)+"\""+"},";
				   }				   
				}else if(currentTreeType=="workGroup"){
					for(var i=0;i<arr.length;i++){
					   var type=arr[i].substring(0,arr[i].indexOf(split_one));
					   //if(type=="workGroup"){
					    info+="{type:"+"\""+arr[i].substring(0,arr[i].indexOf(split_one))+"\""
						     +",id:"+"\""+arr[i].substring(arr[i].indexOf(split_one)+2,arr[i].indexOf(split_two))+"\""
						     +",name:"+"\""+arr[i].substring(arr[i].indexOf(split_two)+2,arr[i].indexOf(split_three))+"\""
						     +",branchName:"+"\""+arr[i].substring(arr[i].indexOf(split_three)+2,arr[i].indexOf(split_four))+"\""
						     +",subCompanyId:"+"\""+arr[i].substring(arr[i].indexOf(split_four)+2,arr[i].length)+"\""+"},";
					   //}
				   }
				   
				}else if(currentTreeType=="departmentAndGroup"){
					for(var i=0;i<arr.length;i++){
						   var type=arr[i].substring(0,arr[i].indexOf(split_one));
						   if(type=="department"){
							   info+="{type:"+"\""+arr[i].substring(0,arr[i].indexOf(split_one))+"\""
							       +",id:"+"\""+arr[i].substring(arr[i].indexOf(split_one)+2,arr[i].indexOf(split_two))+"\""
							       +",name:"+"\""+arr[i].substring(arr[i].indexOf(split_two)+2,arr[i].indexOf(split_three))+"\""
							       +",branchName:"+"\""+arr[i].substring(arr[i].indexOf(split_three)+2,arr[i].indexOf(split_four))+"\""
						           +",isBranch:"+"\""+arr[i].substring(arr[i].indexOf(split_four)+2,arr[i].indexOf(split_five))+"\""
						           +",subCompanyId:"+"\""+arr[i].substring(arr[i].indexOf(split_five)+2,arr[i].length)+"\""+"},";
						   }else if(type=="workGroup"){
					           info+="{type:"+"\""+arr[i].substring(0,arr[i].indexOf(split_one))+"\""
						           +",id:"+"\""+arr[i].substring(arr[i].indexOf(split_one)+2,arr[i].indexOf(split_two))+"\""
						           +",name:"+"\""+arr[i].substring(arr[i].indexOf(split_two)+2,arr[i].indexOf(split_three))+"\""
						           +",branchName:"+"\""+arr[i].substring(arr[i].indexOf(split_three)+2,arr[i].indexOf(split_four))+"\""
						           +",subCompanyId:"+"\""+arr[i].substring(arr[i].indexOf(split_four)+2,arr[i].length)+"\""+"},";
					        }				        
				      }
				}
			    if(info.indexOf(",")>=0){
			    	info=info.substring(0,info.length-1);
			    }
	           info+="]";
	            window.parent.infor = info;
	       return info;
	      }else{
	      	return "";
	      }
}

//树取值function
function getInfoWithTreeId(){
  var arr=allUsers(treeId);
  if(arr!=""){
        var info="[";
     	for(var i=0; i<arr.length; i++){
	     	type=arr[i].substring(0,arr[i].indexOf(split_one));
			  info+="{type:"+"\""+arr[i].substring(0,arr[i].indexOf(split_one))+"\""
			     +",id:"+"\""+arr[i].substring(arr[i].indexOf(split_one)+2,arr[i].indexOf(split_two))+"\""
			     +",name:"+"\""+arr[i].substring(arr[i].indexOf(split_two)+2,arr[i].indexOf(split_three))+"\""
			     +",loginName:"+"\""+arr[i].substring(arr[i].indexOf(split_three)+2,arr[i].indexOf(split_eight))+"\""+"},";
	    }
	    if(info.indexOf(",")>=0){
	    	info=info.substring(0,info.length-1);
	    }
       info+="]";
   return info;
  }else{
  	return "";
  }
}
//点击页签事件
function changeSelected(type,value){
	  $("#defaultTreeType").attr("value",type);
	  $("#defaultTreeValue").attr("value",value);
	  $("#"+treeId).attr("type",type);
	  //初始化页签树	
	  multipleLeafPageTree(type);
}
//初始化页签树	
function multipleLeafPageTree(type){     
	$.ajaxSetup({cache:false});
	$("#"+treeId).bind("search.jstree",function(e,data){
		$.jstree.rollback(data.rlbk); 
     }).jstree({
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
						}
					}
				 }, 
		   "plugins" : [ "themes", "json_data","checkbox","types","search" ]
		});
}

//用户调用的方法    
//<-----start---->
function getIds(){
    var arr=allUsers(treeId);
	var ids="";
	for(var i=0; i<arr.length; i++){
	  var id = arr[i].substring(arr[i].indexOf(split_one)+2,arr[i].indexOf(split_two));
	  var type=arr[i].substring(0,arr[i].indexOf(split_one));
	  if(type=='user'&&!isContain(id,ids)){
	  	ids+=arr[i].substring(arr[i].indexOf(split_one)+2,arr[i].indexOf(split_two))+",";
	  } 
	}
	window.parent.mId=ids.substring(0,ids.length-1);
	return ids.substring(0,ids.length-1);
}	
    
function getNames(){
    var arr=allUsers(treeId);
      var names="";
 	for(var i=0; i<arr.length; i++){
 		var name="";
 		if(isSingleCompany=="true"){
 			name = arr[i].substring(arr[i].indexOf(split_two)+2,arr[i].indexOf(split_three))+"("+arr[i].substring(arr[i].indexOf(split_nine)+2,arr[i].indexOf(split_ten))+")";
 		}else{
 			name = arr[i].substring(arr[i].indexOf(split_two)+2,arr[i].indexOf(split_three));
 		}
 		var type=arr[i].substring(0,arr[i].indexOf(split_one));
	  	if(type=='user'&&!isContain(name,names)){
	  		if(isSingleCompany=="true"){
	  			names+=name+",";
	  		}
 			
 		 }
 	}
 	window.parent.mName=names.substring(0,names.length-1);
 	return names.substring(0,names.length-1);
}
function getTypes(){
    var arr=allUsers(treeId);
      var types="";
 	for(var i=0; i<arr.length; i++){
 		types+=arr[i].substring(0,arr[i].indexOf(split_one))+",";
 	}
 	window.parent.mType=types.substring(0,types.length-1);
 	return types.substring(0,types.length-1);
}

function getTreeType(){
   var treeType = $("#defaultTreeType").val();
   window.parent.mTreeType=treeType;     
   return treeType;
}
function getLoginNames(){
    var isAppend = $("#isAppend").val();
    var loginNameId = $("#loginNameId").val();
    var arr=allUsers(treeId);
    var loginNames="";
 	for(var i=0; i<arr.length; i++){
 	  var type = arr[i].substring(0,arr[i].indexOf(split_one))
 	  if(type=='user'){
 	   var loginName = arr[i].substring(arr[i].indexOf(split_three)+2,arr[i].indexOf(split_four));
	 	   if(loginNames.indexOf(loginName+',')<0){
	 	   		loginNames+=loginName+",";
	 	   }
 	  }
 	}
 	var loginNameResult = "";
 	if(isAppend=='true'&&loginNameId!='noLoginName'&&loginNameId!='undefined'){
 	     var oldValue = window.parent.$("#"+loginNameId).val(); 
 	     if(oldValue!=''){
 	       loginNameResult = removeRepeate(oldValue,loginNames.substring(0,loginNames.length-1),",");
 	     }else{
 	       loginNameResult = getNoRepeatLoginName(loginNames.substring(0,loginNames.length-1));
 	     }
 	}else{
 	      loginNameResult = getNoRepeatLoginName(loginNames.substring(0,loginNames.length-1));
 	}
 	   window.parent.mLoginName=loginNameResult;
       return loginNameResult;
}
function getUserInfoWithTreeId(){
	var arr=allUsers(treeId);
  if(arr!=""){
        var info="[";
     	for(var i=0; i<arr.length; i++){
	     	type=arr[i].substring(0,arr[i].indexOf(split_one));
	     	if(type=="user"){
			  info+="{type:"+"\""+arr[i].substring(0,arr[i].indexOf(split_one))+"\""
						     +",id:"+"\""+arr[i].substring(arr[i].indexOf(split_one)+2,arr[i].indexOf(split_two))+"\""
						     +",name:"+"\""+arr[i].substring(arr[i].indexOf(split_two)+2,arr[i].indexOf(split_three))+"\""
						     +",loginName:"+"\""+arr[i].substring(arr[i].indexOf(split_three)+2,arr[i].indexOf(split_four))+"\""
						     +",parentName:"+"\""+arr[i].substring(arr[i].indexOf(split_five)+2,arr[i].indexOf(split_six))+"\""
						     +",parentId:"+"\""+arr[i].substring(arr[i].indexOf(split_six)+2,arr[i].indexOf(split_eight))+"\""
						     +",branchName:"+"\""+arr[i].substring(arr[i].indexOf(split_nine)+2,arr[i].indexOf(split_ten))+"\""
						     +",subCompanyId:"+"\""+arr[i].substring(arr[i].indexOf(split_ten)+2,arr[i].length)+"\""+"},";
			}
	    }
	    if(info.indexOf(",")>=0){
	    	info=info.substring(0,info.length-1);
	    }
       info+="]";
       window.parent.userInfo = info;
   return info;
  }else{
  	return "";
  }
}

 	//部门名称
	function getDepartmentNames(){
		var arr=allUsers(treeId);
	    var departmentNames="";
     	for(var i=0; i<arr.length; i++){
     	var type=arr[i].substring(0,arr[i].indexOf(split_one));
     		var name="";
	 		if(isSingleCompany=="true"&&arr[i].substring(arr[i].indexOf(split_four)+2,arr[i].indexOf(split_five))=="false"){
	 			name = arr[i].substring(arr[i].indexOf(split_two)+2,arr[i].indexOf(split_three))+"("+arr[i].substring(arr[i].indexOf(split_three)+2,arr[i].indexOf(split_four))+")";
	 		}else{
	 			name = arr[i].substring(arr[i].indexOf(split_two)+2,arr[i].indexOf(split_three));
	 		}
		    if(type=='department'){
     			departmentNames+=name+",";
		    }
     	}
     	window.parent.mDepartmentName=departmentNames.substring(0,departmentNames.length-1);
     	return departmentNames.substring(0,departmentNames.length-1); 
	}
	//部门id
	function getDepartmentIds(){
		var arr=allUsers(treeId);
	    var departmentIds="";
     	for(var i=0; i<arr.length; i++){
     		var type=arr[i].substring(0,arr[i].indexOf(split_one));
     		if(type=='department'){
     			departmentIds+=arr[i].substring(arr[i].indexOf(split_one)+2,arr[i].indexOf(split_two))+",";
		    }
     	}
     	window.parent.mDepartmentId=departmentIds.substring(0,departmentIds.length-1);
     	return departmentIds.substring(0,departmentIds.length-1); 
	}
	//部门code
	function getDepartmentCodes(){
		var arr=allUsers(treeId);
	    var departmentCodes="";
     	for(var i=0; i<arr.length; i++){
     		var type=arr[i].substring(0,arr[i].indexOf(split_one));
     		if(type=='department'){
     			departmentCodes+=arr[i].substring(arr[i].indexOf(split_seven)+2,arr[i].length)+",";
		    }
     	}
     	window.parent.departmentCodes=departmentCodes.substring(0,departmentCodes.length-1);
     	return departmentCodes.substring(0,departmentCodes.length-1); 
	}
	//部门ShortTitle
	function getDepartmentShortTitles(){
		var arr=allUsers(treeId);
	    var departmentShortTitles="";
     	for(var i=0; i<arr.length; i++){
     		var type=arr[i].substring(0,arr[i].indexOf(split_one));
     		if(type=='department'){
     			departmentShortTitles+=arr[i].substring(arr[i].indexOf(split_six)+2,arr[i].indexOf(split_seven))+",";
		    }
     	}
     	window.parent.departmentShortTitles=departmentShortTitles.substring(0,departmentShortTitles.length-1);
     	return departmentShortTitles.substring(0,departmentShortTitles.length-1); 
	}
	
	
	//工作组名称
	function getWorkGroupNames(){
		var arr=allUsers(treeId);
	    var workGroupNames="";
     	for(var i=0; i<arr.length; i++){
     		var type=arr[i].substring(0,arr[i].indexOf(split_one));
     		var workGroupName="";
     		if(isSingleCompany=="true"){
     			workGroupName=arr[i].substring(arr[i].indexOf(split_two)+2,arr[i].indexOf(split_three))+"("+arr[i].substring(arr[i].indexOf(split_three)+2,arr[i].indexOf(split_four))+")";
     		}else{
     			workGroupName=arr[i].substring(arr[i].indexOf(split_two)+2,arr[i].indexOf(split_three));
     		}
     		if(type=='workGroup'){
     		
     			workGroupNames+=workGroupName+",";
		    }
     	}
     	window.parent.mWorkGroupName=workGroupNames.substring(0,workGroupNames.length-1);
     	return workGroupNames.substring(0,workGroupNames.length-1); 
	}
	//工作组id
	function getWorkGroupIds(){
		var arr=allUsers(treeId);
	    var workGroupIds="";
     	for(var i=0; i<arr.length; i++){
     		var type=arr[i].substring(0,arr[i].indexOf(split_one));
     		if(type=='workGroup'){
     			workGroupIds+=arr[i].substring(arr[i].indexOf(split_one)+2,arr[i].indexOf(split_two))+",";
		    }
     	}
     	window.parent.mWorkGroupId=workGroupIds.substring(0,workGroupIds.length-1);
     	return workGroupIds.substring(0,workGroupIds.length-1); 
	}
	function getAllIds(){
		var isAppend = $("#isAppend").val();
		var info=getInfo("user");
		var users=eval(info);
		var ids=getBranchId(users,"departmentAndGroup",isAppend);
		var arr=ids.split(";");
		for(var i=0;i<arr.length;i++){
			var temp=arr[i].split("-");
			if(temp[0]=="department"){
				window.parent.mDepartmentId=temp[1];
			}else if(temp[0]=="workgroup"){
				window.parent.mWorkGroupId=temp[1];
			}
		}
		return ids;
	}
	//用户邮件
	function getEmails(){
		var arr=allUsers(treeId);
		var emails="";
		for(var i=0; i<arr.length; i++){
			var email =  arr[i].substring(arr[i].indexOf(split_one)+2,arr[i].indexOf(split_two));
			var type=arr[i].substring(0,arr[i].indexOf(split_one));
		    if(type=='user'&&emails.indexOf(email+",")<0){
		    	emails+=arr[i].split(split_eight)[1].split(split_seven)[0]+",";
		    }
		}
		window.parent.mEmail=emails.substring(0,emails.length-1);
		return emails.substring(0,emails.length-1);    
	}
	
	//用户尊称
	function getHonorificNames(){
		var arr=allUsers(treeId);
		var honorificNames="";
		for(var i=0; i<arr.length; i++){
			var honorificName =  arr[i].substring(arr[i].indexOf(split_one)+2,arr[i].indexOf(split_two));
			var type=arr[i].substring(0,arr[i].indexOf(split_one));
		    if(type=='user'&&honorificNames.indexOf(honorificName+",")<0){
		    	honorificNames+=arr[i].split(split_eight)[1].split(split_seven)[1]+",";
		    }
		}
		window.parent.mHonorificName=honorificNames.substring(0,honorificNames.length-1);   
		return honorificNames.substring(0,honorificNames.length-1);   
	}
	
    //用户权重
	function getWeights(){
		var arr=allUsers(treeId);
		var weights="";
		for(var i=0; i<arr.length; i++){
			var weight =  arr[i].substring(arr[i].indexOf(split_one)+2,arr[i].indexOf(split_two));
			var type=arr[i].substring(0,arr[i].indexOf(split_one));
		    if(type=='user'&&weights.indexOf(weight+",")<0){
		    	weights+=arr[i].split(split_eight)[1].split(split_seven)[2]+",";
		    }
		}
		window.parent.mWeight=weights.substring(0,weights.length-1);   
		return weights.substring(0,weights.length-1);    
	}
//<-----end---->

//得到去重复的登录名
function getNoRepeatLoginName(loginName){
   var loginNameStr = loginName.split(",");
   var result = "";
   for(var i=0;i<loginNameStr.length;i++){
     if(!isContain(loginNameStr[i],result)){
       result+=loginNameStr[i]+",";
     }
   }
   return result.substring(0,result.length-1);
}

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
		}else if(para=='parentName'){
		result=obj.parentName;
		}else if(para=='parentId'){
		result=obj.parentId;
		}else if(para=='parentType'){
		result=obj.parentType;
		}
	return 	result;
}

//拼接id   
//需要什么值就返回什么值 id=12，loginName=wangjing..
//treValue是字符串..name,loginName,weight..
function createValueStr(obj,treeValue){
    var result = "";
    var values = treeValue.split(",");
    var val="";
	for(var j=0;j<values.length;j++){
		result+=values[j]+"="+returnParamater(obj,values[j])+",";
	}
	return result.substring(0,result.length-1);
}
//需要什么值就返回什么值 ,返回形式
//treeValue是单值name/id/loginName..
function createValueStrSingle(obj,treeValue){
    if(treeValue==''){
        return returnParamater(obj,id);
    }else{
        var values = treeValue.split(",");
	    return returnParamater(obj,values[0]);
	}
}

//去除name重复
function removeRepeate(oldValues,newValues,splitValue){
	 var newValuesStr = newValues.split(splitValue);
	 var result = "";
	 var newStr = "";
	 for(var j=0;j<newValuesStr.length;j++){
	  if(!isHasId(oldValues,newValuesStr[j])){
	     newStr+=newValuesStr[j]+splitValue;
	  }
	 }
	 result = oldValues+splitValue+newStr;
	 return result.substring(0,result.length-1);
}

//去除id重复(user)
function removeRepeateId(oldValues,newValues,splitValue){
	 //new id=1;id=22
	 //old 1,2
	 var newValuesStr = newValues.split(splitValue);
	 var result = "";
	 var newStr = "";
	 for(var j=0;j<newValuesStr.length;j++){
	    var newId = newValuesStr[j].split("=")[1];
	    if(!isHasId(oldValues,newId)){
	        newStr+=newId+",";
	    }
	 }
	 result = oldValues+","+newStr;
	 return result.substring(0,result.length-1);
}
//去除id和name的重复(user)
function getNoRepeatIdAndName(ids,names){
	  result = ids+split_night+names;
	  return result;
}
//去除部门和工作组的重复
function removeRepeateDepartmentAndGroupId(oldValues,newValues){
     var departmentIds = oldValues.split(split_five)[0].split("==")[1];
     var groupIds = oldValues.split(split_five)[1].split("==")[1];
     var departmentResult = "";
	 var groupResult = "";
	 if(typeof(departmentIds)!='undefined'){
	   var newValueStr = newValues.split(split_five)[0].split("==")[1].split(",");
	   for(var i = 0;i<newValueStr.length;i++){
	      if(!isHasId(departmentIds,newValueStr[i])){
	          if(departmentIds!=''){
	            departmentIds+=","+newValueStr[i];
	          }else{
	            departmentIds+=newValueStr[i]+",";
	          }
	      }
	   }
	   if(departmentIds.substring(departmentIds.length-1,departmentIds.length)!=','){
	      departmentResult = "departmentIds=="+departmentIds;
	   }else{
	      departmentResult = "departmentIds=="+departmentIds.substring(0,departmentIds.length-1);
	   }
	 }else{
	   departmentResult = "departmentIds==";
	 }
	 
	  if(typeof(groupIds)!='undefined'){
	   var newValueStr = newValues.split(split_five)[1].split("==")[1].split(",");
	   for(var i = 0;i<newValueStr.length;i++){
	      if(!isHasId(groupIds,newValueStr[i])){
	          if(groupIds!=''){
	            groupIds+=","+newValueStr[i];
	          }else{
	            groupIds+=newValueStr[i]+",";
	          }
	      }
	   }
	   if(groupIds.substring(groupIds.length-1,groupIds.length)!=','){
	        groupResult = "groupIds=="+groupIds;
	   }else{
	        groupResult = "groupIds=="+groupIds.substring(0,groupIds.length-1);
	   }
	 }else{
	        groupResult = "groupIds==";
	 }
	 return departmentResult+split_five+groupResult;
}
//判断oldValues中是否包含newId,包含返回true
function isHasId(oldValues,newId){
	 var str = oldValues.split(",");
	 for(i=0;i<str.length;i++){
	    if(str[i]==newId){
	      return true;
	    }
	 }
	 return false;
}
//判断ids中是否包含target,包含返回true
function isContain(target,ids){
	 var idStr = ids.split(",");
	 var result = false;
	  for(var i=0;i<idStr.length;i++){
	      if(target==idStr[i]){
	        result = true;
	        break;
	      }
	  }
	  return result;
}
//去除id的=,返回的是以逗号分隔的id字符串
function translateId(oIds){
	  var idsStr = oIds.split(";");
	  var result = "";
	  for(i=0;i<idsStr.length;i++){
	    result+=idsStr[i].split("=")[1]+",";
	  }
	  return result.substring(0,result.length-1);
}
//处理部门和工作组的id,返回的是"departmentIds==1,2+#groupIds==3,2"
function translateDepartmentAndGroupId(totalIds){
	 var departmentIds = totalIds.split(split_five)[0].split("==")[1];
	 var groupIds = totalIds.split(split_five)[1].split("==")[1];
	 var departmentResult = "";
	 var groupResult = "";
	 if(typeof(departmentIds)!='undefined'&&departmentIds!=''){
	   var dIds = departmentIds.split(";");
	   for(var i = 0;i<dIds.length;i++){
	      departmentResult+=dIds[i].split("=")[1]+",";
	   }
	   departmentResult = "departmentIds=="+departmentResult.substring(0,departmentResult.length-1);
	 }else{
	   departmentResult = "departmentIds==";
	 }
	 
	  if(typeof(groupIds)!='undefined'&&groupIds!=''){
	   var gIds = groupIds.split(";");
	   for(var i = 0;i<gIds.length;i++){
	      groupResult+=gIds[i].split("=")[1]+",";
	   }
	   groupResult = "groupIds=="+groupResult.substring(0,groupResult.length-1);
	 }else{
	   groupResult = "groupIds==";
	 }
	 return departmentResult+split_five+groupResult;
}
//判断选中的是否是全公司节点
function isWholeCompany(info){
	for(var i=0;i<info.length;i++){
	    var type=info[i].type;
		if(type == "company"){
		  return true;
		}
	}
	return false;
}
//判断选中的是否是所有部门节点
function isWholeDepartment(info){
	for(var i=0;i<info.length;i++){
	    var type=info[i].type;
		if(type == "allDepartment"){
		  return true;
		}
	}
	return false;
}
//判断选中的是否只是所有工作组节点
function isWholeWorkGroup(info){
    var result = false;
	for(var i=0;i<info.length;i++){
	    var type=info[i].type;
		if(type == "allWorkGroup"){
		  result = true;
		}
		if(type == "user"){
		  result = false;
		  break;
		}
	}
	return result;
}
//判断选中的是否是所有工作组和 人员节点
function isWholeWorkGroupAndMan(info){
    var result1 = false;
    var result2 = false;
	for(var i=0;i<info.length;i++){
	    var type=info[i].type;
		if(type == "allWorkGroup"){
		  result1 = true;
		}
		if(type == "user"){
		  result2 = true;
		}
	}
	return result1&&result2;
}
//判断选中的是否是根分支机构节点
function isWholeBranch(info){
	for(var i=0;i<info.length;i++){
	    var type=info[i].type;
		if(type == "branch"){
		  return true;
		}
	}
	return false;
}
//*****************处理分支机构id（人员树）start***************
//得到分支机构的id
function getBranchId(info,treeType,isAppend){
    var result = handleNodeFromTree(info,treeType);
    var oldIds = getValueFromSomeWhere("HIDDEN");
	if(isAppend!='true'||oldIds==''){
	     return combineResult(result);
	}else{
	     return getAppendResult(oldIds,result);
	}
}

//得到追加的结果(多分支机构情况下)
//oldIds===
//branch-12,23..;branchalldepartment-34..;branchallworkgroup-9..;
//user-32,54,56..;branchonlydepartment-12,22..;branchonlyworkgroup-34,54..;
//nobranch-43,65..
//newIds===
//branch-12,branchalldepartment-45,branchallworkgroup-4,user-7,
//branchonlydepartment-22,branchonlyworkgroup-33,nobranch-22
function getAppendResult(oldIds,newIds){
  var unitStr = rebackSplitIds(oldIds,newIds)+","+newIds;
  return combineResult(unitStr);
}
//把oldIds形式的字符串转成newIds形式
function rebackSplitIds(oldIds,newIds){
  var result="";
  var arr = oldIds.split(";");
  for(var i=0;i<arr.length;i++){
     var old = arr[i].split("-");
     var type = arr[i].split("-")[0];
     if(old.length==2){
        var idStr = arr[i].split("-")[1].split(",");
         for(var j=0;j<idStr.length;j++){
           var unit = type+"-"+idStr[j];
           if(!isContain(unit,newIds)&&idStr[j]!=''){
             result+=unit+",";
           }
         }
     }
  }
  return result.substring(0,result.length-1);
}

//处理从树上取到得节点
//branch-12,branchalldepartment-45,branchallworkgroup-4,user-7,
//branchonlydepartment-22,branchonlyworkgroup-33,nobranch-22
//department-12,department-45,workgroup-54,workgroup-34
function handleNodeFromTree(info,treeType){
  var result = "";
	for(var i=0;i<info.length;i++){
	    var type=info[i].type;
		if(type == "branch"){//选择分支机构根节点时
		   if(treeType=='MAN_DEPARTMENT_TREE'){
		       result+="branchonlydepartment-"+info[i].id+",";
		   }else if(treeType=='MAN_GROUP_TREE'){
		       result+="branchonlyworkgroup-"+info[i].id+","; 
		   }else{
		       if(treeType!='DEPARTMENT_TREE'&&treeType!='GROUP_TREE'){
		         result+="branch-"+info[i].id+",";
		       }
		   }
		}else if(type == "allDepartment"){
		   result+="branchalldepartment-"+info[i].id+",";
		}else if(type == "allWorkGroup"){
		   result+="branchallworkgroup-"+info[i].id+",";
		}else if(type == "user"){
		   result+="user-"+info[i].id+",";
		}else if(type == "NOBRANCH"){
		   result+="nobranch-"+info[i].nobranchId+",";
		}else if(type == "department"){
		   result+="department-"+info[i].id+",";
		}else if(type == "workGroup"){
		   result+="workgroup-"+info[i].id+",";
		}
	}
	return result.substring(0,result.length-1);
}

//整合所有类型的节点,并去除重复
//branch-12,23..;branchalldepartment-34..;branchallworkgroup-9..;
//user-32,54,56..;branchonlydepartment-12,22..;branchonlyworkgroup-34,54..;
//nobranch-43,65..;department-21,33..;workgroup-34,7..
//把newIds形式的字符串转成oldIds形式
function combineResult(str){
	var branchIds="";
	var allDepartmentIds="";
	var allWorkgroupIds="";
	var userIds="";
	var onlyDepartmentIds="";
	var onlyWorkgroupIds="";
	var nobranchIds="";
	var departmentIds="";
	var workgroupIds="";
	var arr = str.split(",");
	for(var i=0;i<arr.length;i++){
	   var type = arr[i].split("-")[0];
	   var id = arr[i].split("-")[1];
	   if(type=="branch"){
	       branchIds+=id+",";
	   }else if(type=="branchalldepartment"){
	       if(!isContain("branch-"+id,str)){
	         allDepartmentIds+=id+",";
	       }
	   }else if(type=="branchallworkgroup"){
	       if(!isContain("branch-"+id,str)){
	         allWorkgroupIds+=id+",";
	       }
	   }else if(type=="user"){
	         userIds+=id+",";
	   }else if(type=="branchonlydepartment"){
	         onlyDepartmentIds+=id+",";
	   }else if(type=="branchonlyworkgroup"){
	         onlyWorkgroupIds+=id+",";
	   }else if(type=="nobranch"){//无部门节点
	         nobranchIds+=id+",";
	   }else if(type=="department"){
	         departmentIds+=id+",";
	   }else if(type=="workgroup"){
	         workgroupIds+=id+",";
	   }
	}
	var b = branchIds==""?"":branchIds.substring(0,branchIds.length-1);
	var d = allDepartmentIds==""?"":allDepartmentIds.substring(0,allDepartmentIds.length-1);
	var w = allWorkgroupIds==""?"":allWorkgroupIds.substring(0,allWorkgroupIds.length-1);
	var u = userIds==""?"":userIds.substring(0,userIds.length-1);
	var od = onlyDepartmentIds==""?"":onlyDepartmentIds.substring(0,onlyDepartmentIds.length-1);
	var ow = onlyWorkgroupIds==""?"":onlyWorkgroupIds.substring(0,onlyWorkgroupIds.length-1);
	var ob = nobranchIds==""?"":nobranchIds.substring(0,nobranchIds.length-1);
	var dp = departmentIds==""?"":departmentIds.substring(0,departmentIds.length-1);
	var wg = workgroupIds==""?"":workgroupIds.substring(0,workgroupIds.length-1);
	var result = "branch-"+b+";"+"branchalldepartment-"+d+";"
	            +"branchallworkgroup-"+w+";"+"user-"+u+";"
	            +"branchonlydepartment-"+od+";"
	            +"branchonlyworkgroup-"+ow+";"
	            +"nobranch-"+ob+";"+"department-"+dp+";"
	            +"workgroup-"+wg;
	return result;
}
//*****************处理分支机构id（人员树）end***************
//*****************处理分支机构name（人员树）start***********
//得到带分支机构的节点姓名(考虑了不同分支机构间重名的问题，去重复)
function getBranchName(info,isAppend){
   var oldNames = getValueFromSomeWhere("SHOW");
	if(isAppend!='true'||oldNames==''){
	     return getBranchNameNoAppend(info);
	}else{
	     return getBranchNameAppend(oldNames,info);
	}
}
//追加
function getBranchNameAppend(oldNames,info){
	for(var i=0;i<info.length;i++){
	    var type=info[i].type;
	    var branchName = info[i].branchName;
	    
		if((type == "allDepartment"||type == "allWorkGroup"||type == "user")&&branchName!="null"){
		   if(!isContain(info[i].name+"("+info[i].branchName+")",oldNames)){
		     oldNames+=","+info[i].name+"("+info[i].branchName+")";
		   }
		}else if(type == "NOBRANCH"){
		   if(!isContain("无部门人员("+branchName+")",oldNames)){
		     if(branchName!="null"){
		        oldNames+=","+"无部门人员("+branchName+")";
		     }else{
		        oldNames+=","+"无部门人员";
		     }
		   }
		}else if(type == "department"){
		    var isBranch = users[i].isBranch;
		    if(!isContain(info[i].name,oldNames)){
		      if(branchName!="null"){
		         if(isBranch !="true"){
		            oldNames+=","+info[i].name+"("+branchName+")";
		         }else{
		            oldNames+=","+info[i].name;
		         }
		      }else{
		         oldNames+=","+info[i].name;
		      }
		    }
		}else{
		   if(!isContain(info[i].name,oldNames)){
		      if(branchName!="null"){
		         oldNames+=","+info[i].name+"("+branchName+")";
		      }else{
		         oldNames+=","+info[i].name;
		      }
		    }
		}
	}
   return oldNames;
}
//不追加,去重复
function getBranchNameNoAppend(info){
    var result = "";
	for(var i=0;i<info.length;i++){
	    var type=info[i].type;
	    var branchName = info[i].branchName;
		if((type == "allDepartment"||type == "allWorkGroup"||type == "user")&&branchName!="null"){
		    if(!isContain(info[i].name+"("+info[i].branchName+")",result)){
		      result+=info[i].name+"("+info[i].branchName+")"+",";
		    }
		}else if(type == "NOBRANCH"){
		   if(!isContain("无部门人员("+branchName+")",result)){
		      if(branchName!="null"){
		         result+="无部门人员("+branchName+")"+",";
		      }else{
		         result+="无部门人员"+",";
		      }
		   }
		}else if(type == "department"){
		    var isBranch = info[i].isBranch;
		    if(!isContain(info[i].name,result)){
		        if(branchName!="null"&&type!='branch'){
		           if(isBranch !="true"){
		              result+=info[i].name+"("+branchName+"),";
		           }else{
		              result+=info[i].name+",";
		           }
		        }else{
		           result+=info[i].name+",";
		        }
		   }
		}else{
		   if(!isContain(info[i].name,result)){
		        if(branchName!="null"&&type!='branch'){
		           result+=info[i].name+"("+branchName+"),";
		        }else{
		           result+=info[i].name+",";
		        }
		   }
		}
	}
	return result.substring(0,result.length-1);
}
//*****************处理分支机构name（人员树）end***********
//给文本框付值方法
function fulfillInput(value,inputName){
		var hiddenInputId = $("#hiddenInputId").val();
		var showInputId = $("#showInputId").val();
		var formId = $("#formId").val();
		var mode = $("#mode").val();
		if(inputName=='HIDDEN'){	
			if(hiddenInputId=='NOHiddenInputId'||mode=='parent'){
			  if(formId=='noFormId'){alert("请设置formId!");}
			  window.parent.$("#builtInInput").attr("value",value);
			}else{
			  window.parent.$("#"+hiddenInputId).attr("value", value);
			}
		}else if(inputName=='SHOW'){
			if(showInputId=='NOShowInputId'||mode=='parent'){
			   if(formId=='noFormId'){alert("请设置formId!");}
			   window.parent.$("#builtInShowInput").attr("value",value);
			}else{
			   window.parent.$("#"+showInputId).attr("value", value);
			}
		}
}
//从页面或者内置文本框取值
function getValueFromSomeWhere(inputName){
	    var hiddenInputId = $("#hiddenInputId").val();
		var showInputId = $("#showInputId").val();
		var formId = $("#formId").val();
		var mode = $("#mode").val();
		if(inputName=='HIDDEN'){
			if(hiddenInputId=='NOHiddenInputId'||mode=='parent'){
			  if(formId=='noFormId'){alert("请设置formId!");}
			  return window.parent.$("#builtInInput").val();
			}else{
				if(window.parent.$("#"+hiddenInputId).attr("id")==hiddenInputId){
				    return window.parent.$("#"+hiddenInputId).val();
				}else{
					return "保存值的文本框不存在!";
				}
			}
		}else if(inputName=='SHOW'){
			if(showInputId=='NOShowInputId'||mode=='parent'){
			  if(formId=='noFormId'){alert("请设置formId!");}
			  return window.parent.$("#builtInShowInput").val();
			}else{
				if(window.parent.$("#"+showInputId).attr("id")==showInputId){
					return window.parent.$("#"+showInputId).val();
				}else{
					return "显示值的文本框不存在!";
				}
			}
		}
	}
	
//选择人员树
function selectMan(treeValue,treeType){
	var isAppend = $("#isAppend").val();
	var info=getInfo("user");
	if(info!=""&&info!="[]"){	
		var users=eval(info);
		if(users!=''){
		   if(branchIds=="ADMIN"){//管理员   
			   if(!isWholeCompany(users)&&!isWholeDepartment(users)&&!isWholeWorkGroup(users)){
			         if(isNoUser(users)){
					     alert("请选择人员");
					 }else{
					      //处理树上的人员节点
					     getUserInfo(users,treeValue,isAppend);
					 }
			    }else if(isWholeDepartment(users)&&!isWholeCompany(users)&&!isWholeWorkGroup(users)){
			    	if(treeType!='COMPANY'&&treeType!='MAN_DEPARTMENT_GROUP_TREE'){
				         //当点击所有部门节点时
						 fulfillInput("ALLCOMPANYID","HIDDEN");
						 fulfillInput("所有部门人员","SHOW");
			    	}else{
			    		alert("请选择具体人员");
			    	}
			    }else if(isWholeWorkGroup(users)&&!isWholeCompany(users)&&!isWholeDepartment(users)){
			    	if(treeType!='COMPANY'&&treeType!='MAN_DEPARTMENT_GROUP_TREE'){
				        //当点击所有工作组节点时
						 fulfillInput("ALLWORKGROUP","HIDDEN");
						 fulfillInput("所有工作组人员","SHOW");
			    	}else{
			    		alert("请选择具体人员");
			    	}
			    }else{
			        //当点击公司节点时
					fulfillInput("ALLCOMPANYID","HIDDEN");
				 	fulfillInput("所有人员","SHOW");
			    }	
		    }else{//多分支机构树时		    
	            //处理选择分支机构根节点            
	            var branchCombineIds = getBranchId(users,treeType,isAppend);            
	            if(isNoSelectUser(branchCombineIds)){
	                   alert("请选择人员");
	            }else{
	               fulfillInput(branchCombineIds,"HIDDEN");
				   fulfillInput(getNames(),"SHOW");
				}
		    }	
		}else{
		      alert("请选择正确的节!");
		}
	}else{
		 alert("请选择人员");
	}
}

function isNoUser(users){
	for(var i=0;i<users.length;i++){
		var type=users[i].type;
			if(type == "user"){
				return false;
			}
	}
	return true;
}

function selectManGroup(treeValue,treeType){
var isAppend = $("#isAppend").val();
	var info=getInfo("user");
	if(info!=""&&info!="[]"){
		var users=eval(info);
		if(users!=''){
		   if(branchIds=="ADMIN"){//管理员
			   if(!isWholeCompany(users)&&!isWholeDepartment(users)&&!isWholeWorkGroup(users)){
					 //处理树上的人员节点
					 getUserInfo(users,treeValue,isAppend);
			    }else{
			        //当点击公司节点时
					fulfillInput("ALLCOMPANYID","HIDDEN");
				 	fulfillInput("所有人员","SHOW");
			    }	
		    }else{//多分支机构树时
	            //处理选择分支机构根节点
	            var branchCombineIds = getBranchId(users,treeType,isAppend);
	            if(isNoSelectUser(branchCombineIds)){
	                   alert("请选择人员");
	            }else{
	               fulfillInput(branchCombineIds,"HIDDEN");
				   fulfillInput(getBranchName(users,isAppend),"SHOW");
				}
		    }	
		}else{
		      alert("请选择正确的节!");
		}
	}else{
		 alert("请选择人员");
	}
}

//branch-12,23..;branchalldepartment-34..;branchallworkgroup-9..;
//user-32,54,56..;branchonlydepartment-12,22..;branchonlyworkgroup-34,54..;
//nobranch-43,65..;department-21,33..;workgroup-34,7..
function isNoSelectUser(str){
   var arr = str.split(";");
   var departmentIds = "";
   var userIds = "";
   for(var i=0;i<arr.length;i++){
      var obj = arr[i].split("-");
      var type = arr[i].split("-")[0];
      if(obj.length==2){
        if(type=='user'){
            userIds=arr[i].split("-")[1];
        }else if(type=='department'){
            departmentIds=arr[i].split("-")[1];
        }
      }
   }
   if(departmentIds!=''&&userIds==''){
      return true;
   }
   return false;
}
//处理isAppend属性
function getUserInfo(users,treeValue,isAppend){
	var ids = "";
	var names = "";
	for(var i=0;i<users.length;i++){
		var type=users[i].type;
			if(type == "user"){
				var id = createValueStr(users[i],treeValue);
				var branchName = users[i].branchName;
				//在名字后添加分支机构名称
				var name = "";
				if(isSingleCompany=='true'){
				    name = users[i].name+"("+branchName+")";
				}else{
				    name = users[i].name;
				}
				
				if(!isContain(id,ids)){
					ids+=id+";";
				}
				if(!isContain(name,names)){
					names+=name+",";
				}
			}
	}
	var idsFromTree = ids.substring(0,ids.length-1);
	if(idsFromTree!=''){
		if(isAppend!='true'){
		   //去掉选择的重复
		    var noRepeatStr = getNoRepeatIdAndName(translateId(idsFromTree),names.substring(0,names.length-1));
			fulfillInput(noRepeatStr.split(split_night)[0],"HIDDEN");
			fulfillInput(noRepeatStr.split(split_night)[1],"SHOW");
		}else{
			var oldIds = getValueFromSomeWhere("HIDDEN");
			var oldNames = getValueFromSomeWhere("SHOW");
				if(oldIds!=''){
					fulfillInput(removeRepeateId(oldIds,idsFromTree,";"),"HIDDEN");
			        fulfillInput(removeRepeate(oldNames,names.substring(0,names.length-1),","),"SHOW");
				}else{
				    //去掉选择的重复
				    var noRepeatStr = getNoRepeatIdAndName(translateId(idsFromTree),names.substring(0,names.length-1));
					fulfillInput(noRepeatStr.split(split_night)[0],"HIDDEN");
			        fulfillInput(noRepeatStr.split(split_night)[1],"SHOW");
				}
		}
	}
}

//选择部门树
function selectDepartment(treeValue,treeType){
    var isAppend = $("#isAppend").val();
	var info=getInfo("department");
	if(info!=""&&info!="[]"){
		var users=eval(info);
		if(users!=''){
		   if(branchIds=="ADMIN"){//管理员
			   if(!isWholeCompany(users)&&!isWholeDepartment(users)){
			         getDepartmentInfo(users,treeValue,isAppend);
			   }else{
					 fulfillInput("ALLCOMPANYID","HIDDEN");
				     fulfillInput("所有部门","SHOW");
			   }
		   }else{//多分支机构
		            fulfillInput(getBranchId(users,treeType,isAppend),"HIDDEN");
			        fulfillInput(getDepartmentNames(),"SHOW");
		   }   			
		}else{
		      alert("请选择正确的节!");
		      return;
		}
	}else{
		alert("请选择部门");
		return;
	}
}

function getDepartmentInfo(users,treeValue,isAppend){
	var ids = "";
	var names = "";
	for(var i=0;i<users.length;i++){
		var type=users[i].type;
			if(type == "department"){
			    var id = createValueStr(users[i],treeValue);
			    var branchName = users[i].branchName;
			    var isBranch = users[i].isBranch;
			    //在名字后添加分支机构名称
			    var name = "";
			    if(isSingleCompany=='true'){
			        if(isBranch !="true"){
				      name = users[i].name+"("+branchName+")";
				    }else{
				      name = users[i].name;
				    }
			    }else{
				    name = users[i].name;
			    } 
			
				if(!isContain(id,ids)){
					ids+=id+";";
				}
				if(!isContain(name,names)){
					names+=name+",";
				}
			}
	}
	var idsFromTree = ids.substring(0,ids.length-1);
	if(idsFromTree!=null){
	  if(isAppend!='true'){
			//去掉选择的重复
		    var noRepeatStr = getNoRepeatIdAndName(translateId(idsFromTree),names.substring(0,names.length-1));
			fulfillInput(noRepeatStr.split(split_night)[0],"HIDDEN");
			fulfillInput(noRepeatStr.split(split_night)[1],"SHOW");
	   }else{
			var oldIds = getValueFromSomeWhere("HIDDEN");
			var oldNames = getValueFromSomeWhere("SHOW");
			if(oldIds!=''){
				fulfillInput(removeRepeateId(oldIds,idsFromTree,";"),"HIDDEN");
			    fulfillInput(removeRepeate(oldNames,names.substring(0,names.length-1),","),"SHOW");
			}else{
			   //去掉选择的重复
		       var noRepeatStr = getNoRepeatIdAndName(translateId(idsFromTree),names.substring(0,names.length-1));
			   fulfillInput(noRepeatStr.split(split_night)[0],"HIDDEN");
			   fulfillInput(noRepeatStr.split(split_night)[1],"SHOW");
			}
	   }
	}
}

//选择工作组树
function selectWorkGroup(treeValue,treeType){
    var isAppend = $("#isAppend").val();
	var info=getInfo("workGroup");
	if(info!=""&&info!="[]"){
		var users=eval(info);
		if(users!=''){
		   if(branchIds=="ADMIN"){//管理员
			   if(!isWholeCompany(users)&&!isWholeWorkGroup(users)){
					 getWorkGroupInfo(users,treeValue,isAppend);
			   }else{
					 fulfillInput("ALLCOMPANYID","HIDDEN");
					 fulfillInput("所有工作组","SHOW");
			   }
		   }else{//多分支机构
		   			fulfillInput(getBranchId(users,treeType,isAppend),"HIDDEN");
			        fulfillInput(getWorkGroupNames(),"SHOW");
		   } 		
		}else{
		      alert("请选择正确的节!");
		      return;
		}
	}else{
		alert("请选择工作组");
		 return;
	}
}

function getWorkGroupInfo(users,treeValue,isAppend){
	var ids = "";
	var names = "";
	for(var i=0;i<users.length;i++){
		var type=users[i].type;
			if(type == "workGroup"){
			    var id = createValueStr(users[i],treeValue);
			    var branchName = users[i].branchName;
			    //在名字后添加分支机构名称
			    var name = "";
			    if(isSingleCompany=='true'){
				    name = users[i].name+"("+branchName+")";
			    }else{
				    name = users[i].name;
			    } 
				
				if(!isContain(id,ids)){
					ids+=id+";";
				}
				if(!isContain(name,names)){
					names+=name+",";
				}
			}
	}
	var idsFromTree = ids.substring(0,ids.length-1);
	if(idsFromTree!=null){
	   if(isAppend!='true'){
			//去掉选择的重复
		    var noRepeatStr = getNoRepeatIdAndName(translateId(idsFromTree),names.substring(0,names.length-1));
			fulfillInput(noRepeatStr.split(split_night)[0],"HIDDEN");
	        fulfillInput(noRepeatStr.split(split_night)[1],"SHOW");
		}else{	
			var oldIds = getValueFromSomeWhere("HIDDEN");
			var oldNames = getValueFromSomeWhere("SHOW");
			if(oldIds!=''){
				 fulfillInput(removeRepeateId(oldIds,idsFromTree,";"),"HIDDEN");
	             fulfillInput(removeRepeate(oldNames,names.substring(0,names.length-1),","),"SHOW");
			}else{
			     //去掉选择的重复
			     var noRepeatStr = getNoRepeatIdAndName(translateId(idsFromTree),names.substring(0,names.length-1));
				 fulfillInput(noRepeatStr.split(split_night)[0],"HIDDEN");
		         fulfillInput(noRepeatStr.split(split_night)[1],"SHOW");
			}
		}
	}
}

//选择工作组和部门树
function selectDepartmentAndWorkGroup(treeValue,treeType){
    var isAppend = $("#isAppend").val();
	var info=getInfo("departmentAndGroup");
	if(info!=""&&info!="[]"){
		var users=eval(info);
		if(users!=''){
		    if(!isWholeCompany(users)&&!isWholeDepartment(users)&&!isWholeWorkGroup(users)){
			      //getDepartmentAndWorkGroupInfor(users,treeValue,isAppend);
			
			      //处理选择分支机构根节点
	              fulfillInput(getBranchId(users,treeType,isAppend),"HIDDEN");
				  fulfillInput(getBranchName(users,isAppend),"SHOW");
			}else if(isWholeDepartment(users)){
			     //当点击所有部门节点时
				 fulfillInput("ALLCOMPANYID","HIDDEN");
				 fulfillInput("所有人员","SHOW");
			}else if(isWholeWorkGroup(users)){
		         //当点击所有工作组节点时
				 fulfillInput("ALLWORKGROUP","HIDDEN");
				 fulfillInput("所有工作组","SHOW");
			}else{
		         //当点击公司节点时
				 fulfillInput("ALLCOMPANYID","HIDDEN");
				 fulfillInput("所有人员","SHOW");
			}	
		}else{
		      alert("请选择正确的节!");
		      return;
		}
	}else{
		alert("请选择部门或工作组");
		return;
	}
}

//提交按钮点击事件	
function _ok_tree(){
	var treeType = $("#defaultTreeType").val();
	var treeValue = $("#defaultTreeValue").val();
	var formId = $("#formId").val();
	var mode = $("#mode").val();
	
	//初始化form
	if(formId!='noFormId'){
	    window.parent.$("#"+formId)
	    .append("<input id='builtInInput' type='hidden' value='' >")
	    .append("<input id='builtInShowInput' type='hidden' value='' >");
	}
	
    //根据树类型的不同调用不同的方法赋值
	if(treeType=='COMPANY'||treeType=='MAN_DEPARTMENT_GROUP_TREE'||treeType=='MAN_DEPARTMENT_TREE'){
	    selectMan(treeValue,treeType);
	}else if(treeType=='MAN_GROUP_TREE'){
	    selectManGroup(treeValue,treeType);
	}else if(treeType=='DEPARTMENT_TREE'){
	    selectDepartment(treeValue,treeType);
	}else if(treeType=='GROUP_TREE'){
	    selectWorkGroup(treeValue,treeType);
	}else if(treeType=='DEPARTMENT_WORKGROUP_TREE'){
	    selectDepartmentAndWorkGroup(treeValue,treeType);
	}
	
	
	//统一给全局变量赋值
	var tor = getValueFromSomeWhere("HIDDEN");
	if(typeof tor == "undefined" || tor == ""){
		//alert("请选择正确的节点!");
		return;
	}else if(tor=="保存值的文本框不存在!"||tor=="显示值的文本框不存在!"){
		alert(tor);
	}else{
		getIds();
		getNames();
		getInfo();
		getEmails();
		getNames();
		getLoginNames();
		getWeights();
		getHonorificNames();
		getTypes();
		getTreeType();
		getUserInfoWithTreeId();
		getDepartmentNames();
		getDepartmentCodes();
		getDepartmentShortTitles()
		getDepartmentIds();
		getWorkGroupNames();
		getWorkGroupIds();
		getAllIds();
	    window.parent.okEnsure="OK";
	    if(mode!='parent')
		window.parent.$.colorbox.close();
	}
	
	//如果是parent模式
	if(mode=='parent'){
	parent.openMessage(getValueFromSomeWhere("HIDDEN"),getValueFromSomeWhere("SHOW"));
	}
}