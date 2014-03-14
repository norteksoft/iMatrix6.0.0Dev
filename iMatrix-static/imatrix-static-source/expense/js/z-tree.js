var ztools = {
		apply: function(fun, param, defaultValue) {
			if ((typeof fun) == "function") {
				return fun.apply(fun, param?param:[]);
			}
			return defaultValue;
           }
};
var iscollback=false;
//view
var rootUrl ;
var title ;
var width ;
var height ;

//type
var treeType;
var showContent ;
var noDeparmentUser;
var onlineVisible ;
var multiObj={};

//data
var chkStyle;
var treeNodeData;
var chkboxType;
//显示的部门
var departmentShow ;

//leaf
var leafEnable ;
var multiLeafJson ;

//feedback
var feedbackEnable;
var showInput;
var showThing ;
var hiddenInput;
var hiddenThing;
var append;

var callback ;


var myztreeObj={
   pCheckNodeList:"",
   pCheckUserNodeList:"",
   pCheckDeparmentNodeList:"",
   pCheckWorkgroupNodeList:"",
   pCurrentCheckedNode:"",
   pCurrentClickNode:"",
   pCurrentClickParentNode:"",
   checkAll:""
};
//api
var API=[{
	myObj:{
	   pCheckNodeList:myztreeObj.pCheckNodeList,
	   pCurrentCheckedNode:myztreeObj.pCurrentCheckedNode
    },	
     single:{
    	 //获取当前点击的节点
    	 getCurrentClickNode:function(){
    	    return myztreeObj.pCurrentClickNode;
         },
         //获取当前点击节点的父节点
         getCurrentClickParentNode:function(){
        	 return myztreeObj.pCurrentClickParentNode;
         },
         //获取当前点击节点的id
         getCurrentClickNodeId:function(){
        	 return myztreeObj.pCurrentClickNode.id.split("_")[1];
         },
         //获取当前点击节点的parentId
         getCurrentClickNodeParentId:function(){
        	 return myztreeObj.pCurrentClickNode.pId.split("_")[1];
         },
         //获取当前点击节点的name
         getCurrentClickNodeName:function(){
             return myztreeObj.pCurrentClickNode.name;
         },
         //获取当前点击节点的type
         getCurrentClickNodeType:function(){
         return myztreeObj.pCurrentClickNode.type;
         },
         //获取当前点击节点的data
         getCurrentClickNodeData:function(){
         return myztreeObj.pCurrentClickNode.data;
         },
         //根据参数名获取对象属性
         getClickValueByNode:function(param){
        	var currentClickNode = myztreeObj.pCurrentClickNode;
        	eval("obj="+currentClickNode.data);
        	if(obj){
        		return obj[param];
        	}else{
        		return "";
        	}
        }
     }
    }];

function popZtree(zSetting){
//view
rootUrl = zSetting.view.url;
title = zSetting.view.title;

width = zSetting.view.width;
height = zSetting.view.height;
showBranch=zSetting.view.showBranch;//显示分支机构
branchIds=zSetting.data.branchIds;
//type
treeType = zSetting.type.treeType;//树的类型
	if(treeType=='COMPANY'){
		title="公司树";
	}else if(treeType=='MAN_DEPARTMENT_TREE'){
		title="部门人员树";
	}else if(treeType=='MAN_GROUP_TREE'){
		title="工作组人员树";
	}else if(treeType=='DEPARTMENT_TREE'){
		title="部门树";
	}else if(treeType=='GROUP_TREE'){
		title="工作组树";
	}else if(treeType=='DEPARTMENT_WORKGROUP_TREE'){
		title="部门工作组树";
	}
showContent = ztools.apply(zSetting.type.showContent,'',zSetting.type.showContent);//设置树节点显示信息
noDeparmentUser = zSetting.type.noDeparmentUser;//是否显示无部门人员
onlineVisible = zSetting.type.onlineVisible;//是否标出在线人员

//data
chkStyle = zSetting.data.chkStyle;//多选单选
chkboxType = zSetting.data.chkboxType;//设置父子节点勾选关联关系
if(chkboxType){
	if(chkboxType.constructor==Object){
		chkboxType="{'Y' : '"+((chkboxType["Y"]||chkboxType["y"])?(chkboxType["Y"]||chkboxType["y"]):"")+"', 'N' : '"+((chkboxType["Y"]||chkboxType["y"])?(chkboxType["Y"]||chkboxType["y"]):"")+"'}";
	}
}
treeNodeData = ztools.apply(zSetting.data.treeNodeData,'',zSetting.data.treeNodeData);//设定树节点data属性

//显示的部门
departmentShow = ztools.apply(zSetting.data.departmentShow,'',zSetting.data.departmentShow);

//leaf
leafEnable = zSetting.leaf.enable;//是否启用多页签
multiLeafJson = ztools.apply(zSetting.leaf.multiLeafJson,'',zSetting.leaf.multiLeafJson);//页签设置

//feedback
feedbackEnable = zSetting.feedback.enable;//是否启用自动赋值
showInput = zSetting.feedback.showInput;//显示input框id
showThing = ztools.apply(zSetting.feedback.showThing,'',zSetting.feedback.showThing);//显示input内容设置
hiddenInput = zSetting.feedback.hiddenInput;//隐藏input框id
hiddenThing = ztools.apply(zSetting.feedback.hiddenThing,'',zSetting.feedback.hiddenThing);//隐藏input内容设置
append = zSetting.feedback.append;//是否追加(只针对多选)

callback = zSetting.callback.onClose;
var url = rootUrl+"/portal/ztree-pop.action?treeType="+treeType
          +"&chkStyle="+chkStyle+"&treeNodeData="+treeNodeData+"&chkboxType="+chkboxType
          +"&treeNodeShowContent="+showContent+"&userWithoutDeptVisible="+noDeparmentUser
          +"&onlineVisible="+onlineVisible+"&leafEnable="+leafEnable+"&multiLeafJson="+multiLeafJson
          +"&feedbackEnable="+feedbackEnable+"&showInput="+showInput+"&showThing="+showThing
          +"&hiddenInput="+hiddenInput+"&hiddenThing="+hiddenThing+"&append="+append
          +"&departmentShow="+departmentShow+"&showBranch="+showBranch+"&branchIds="+branchIds;
	
$.colorbox({href:encodeURI(url),iframe:true, innerWidth:width, innerHeight:height,overlayClose:false,title:title,onClosed:function(){
	if(iscollback){
		ztools.apply(callback,API,callback);
		iscollback=false;
	}
}});
	return API;
}

var myZtree={
   clearInput:function(){
	    if(typeof(showInput)=='undefined'||showInput=='')return;
	    $("#"+showInput).attr("value","");
	    if(typeof(hiddenInput)=='undefined'||hiddenInput=='')return;
	    $("#"+hiddenInput).attr("value","");
   },
   deleteInput:function(formId){
	    if(typeof(showInput)=='undefined'||showInput=='')return;
	    var showContent = getCachFromCookieByKey('showCachContent'+showInput);
	    
	    if(typeof(hiddenInput)=='undefined'||hiddenInput=='')return;
	    var hiddenContent = getCachFromCookieByKey('hiddenCachContent'+hiddenInput);
	    
	    if(showContent==''){alert("显示缓存为空!");return;}
		if(hiddenContent==''){alert("隐藏缓存为空!");return;}
	  
		//校验输入字符串的合法性
		if(!isAvailable(showContent,hiddenContent)){alert("显示的格式和隐藏域格式设置不一致!");return;}
		
	    var showContentJson = createJson(showContent,hiddenContent);
	    var removeStaffJsonOld = $("#removeStaffJson").val();
		if(typeof(removeStaffJsonOld)=='undefined'){
		      $("#"+formId).append("<input type='hidden' id='removeStaffJson' name='removeStaffJson' value='"+showContentJson+"'/> ");
		}else{
		      $("#removeStaffJson").attr("value",showContentJson);
		}
		var url = rootUrl+"/portal/remove-ztree.action?showInput="+showInput+"&hiddenInput="+hiddenInput;
		$.colorbox({href:encodeURI(url),iframe:true, innerWidth:400, innerHeight:400,overlayClose:false,title:"移除",onClosed:function(){}});
   }
};

//根据key值从缓存里取东西	
function getCachFromCookieByKey(key){
	var result = "";
	var cookieArr = unescape(document.cookie).split(";");
	for(var i=0;i<cookieArr.length-1;i++){
		var k = cookieArr[i].split("=")[0];
		var v = cookieArr[i].split("=")[1];
		if($.trim(k)==$.trim(key)){
			result = v;
		}
	}
	return result;
}
//拼接json
//两个字符串的类型(如 user/department/workgroup)的名称和数量都应该的一致的

function createJson(showContent,hiddenContent){
	
	var arr = showContent.split("+");
	var harr = hiddenContent.split("+");
	
	var json = '[';
	for(var i=0;i<arr.length;i++){
		var type=$.trim(arr[i].split(":")[0]);
		var valueArr = arr[i].split(":")[1].split(",");
		var hValueArr = harr[i].split(":")[1].split(",");
		if(type=='user'){
			for(var j=0;j<valueArr.length;j++){
			json = json+'{"type":"user","showValue":"'+valueArr[j]+'","hiddenValue":"'+hValueArr[j]+'"},';
			}
		}else if(type=='department'){
			for(var j=0;j<valueArr.length;j++){
			json = json+'{"type":"department","showValue":"'+valueArr[j]+'","hiddenValue":"'+hValueArr[j]+'"},';
			}
		}else if(type=='workgroup'){
			for(var j=0;j<valueArr.length;j++){
				json = json+'{"type":"workgroup","showValue":"'+valueArr[j]+'","hiddenValue":"'+hValueArr[j]+'"},';
			}
		}
	}
	json = json.substring(0,json.length-1)+']';
	return json;
}

function isAvailable(showContent,hiddenContent){
	var result=true;
	var showArr = showContent.split("+");
	var hiddenArr = hiddenContent.split("+");
	if(showArr.length!=hiddenArr.length){result=false;}
		
	if(showContent.indexOf("user:")!=-1&&hiddenContent.indexOf("user:")==-1){result=false ;}
	if(hiddenContent.indexOf("user:")!=-1&&showContent.indexOf("user:")==-1){result=false;}
	
	if(showContent.indexOf("department:")!=-1&&hiddenContent.indexOf("department:")==-1){result=false;}
	if(hiddenContent.indexOf("department:")!=-1&&showContent.indexOf("department:")==-1){result=false;}
	
	if(showContent.indexOf("workgroup:")!=-1&&hiddenContent.indexOf("workgroup:")==-1){result=false;}
	if(hiddenContent.indexOf("workgroup:")!=-1&&showContent.indexOf("workgroup:")==-1){result=false;}
	
	return result;
}
function getMsgById(type,value){
	if(multiObj[type]){
		if(multiObj[type][value]){
			return multiObj[type][value];
		}
	}
	return "";
}
var ztree={
		getCurrentClickNode:function(){return API[0].single.getCurrentClickNode();},
		getId:function(){return API[0].single.getClickValueByNode("id");},	
		getName:function(){return API[0].single.getClickValueByNode("name");},	
		getLoginName:function(){return API[0].single.getClickValueByNode("loginName");},
		//部门名称
		getDepartmentName:function(){return API[0].single.getClickValueByNode("name");;},
		//部门id
		getDepartmentId:function(){return API[0].single.getClickValueByNode("id");;},
		//工作组名称
		getWorkGroupName:function(){ return API[0].single.getClickValueByNode("name");;},
		//工作组id
		getWorkGroupId:function(){ return API[0].single.getClickValueByNode("id");;},
		//用户邮件
		getEmail:function(){ return API[0].single.getClickValueByNode("email");;},
		//用户权重
		getWeight:function(){return API[0].single.getClickValueByNode("weight");;},
		//获取用户部门名称
		getUserDepartmentName:function(){return API[0].single.getClickValueByNode("subCompanyId");},
		//获取所在分支机构ID
		getSubCompanyId:function(){return API[0].single.getClickValueByNode("subCompanyId");},
		//获取所在分支机构名称
		getSubCompanyName:function(){return API[0].single.getClickValueByNode("subCompanyName");},
		//获取部门编码
		getDepartmentCode:function(){return API[0].single.getClickValueByNode("code");;},
		//获取部门简称
		getDepartmentShortTitle:function(){return API[0].single.getClickValueByNode("shortTitle");;},
	
		//获取用户Ids
		getIds:function(){
			return getMsgById('user','id');
		},
		//获取用户Names
		getNames:function(){
			return getMsgById('user','name');
		},
		//获取用户LoginNames
		getLoginNames:function(){
			return getMsgById('user','loginName');
		},
		//获取用户LoginNames
		getSubCompanyIds:function(){
			return getMsgById('user','subCompanyId');
		},
		//获取部门DepartmentNames
		getDepartmentNames:function(){
			return getMsgById('department','name');
		},
		//部门id
		getDepartmentIds:function(){
			return getMsgById('department','id');
		},
		//部门编码Codes
		getDepartmentCodes:function(){
			return getMsgById('department','code');
		},
		//部门简称ShortTitles
		getDepartmentShortTitles:function(){
			return getMsgById('department','shortTitle');
		},
		//工作组名称
		getWorkGroupNames:function(){
			return getMsgById('workgroup','name');
		},
		//工作组id
		getWorkGroupIds:function(){
			return getMsgById('workgroup','id');
			
		}
		
	};