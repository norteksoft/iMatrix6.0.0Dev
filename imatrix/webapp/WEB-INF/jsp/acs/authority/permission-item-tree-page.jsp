<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/common/acs-taglibs.jsp"%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3c.org/TR/1999/REC-html401-19991224/loose.dtd">
<html>
<head>
	<title>数据表</title>
	<%@ include file="/common/acs-iframe-meta.jsp"%>
	<style type="text/css">
	.form-table-without-border td input{
		width:200px;
	}
	</style>
</head>
<body onload="getContentHeight();">
<div class="ui-layout-center">
<div class="opt-body">
	<form id="defaultForm" name="defaultForm"action="" method="post" >
		<input type="hidden" id="itemType" value="${itemType }">
		<input type="hidden" id="rowId" value="${rowId }">
	</form>
	<aa:zone name="main_zone">
		<div class="opt-btn">
			<button class="btn" onclick="select();"><span><span >确定</span></span></button>
		</div>
		<div id="opt-content">
			<div id="treeDiv" class="demo"  type=""></div>
		</div>
		<script>
		$(document).ready(function(){
			//当条件类型是部门时，控制去掉子节点的选中状态，不清除父节点的选中状态
			if('DEPARTMENT'=='${itemType }'){
				$("#treeDiv").attr("type","DEPARTMENT_TREE");
			}else{
				$("#treeDiv").attr("type","");
			}
			create_tree();
		});
		function create_tree(){
			var itemType=$("#itemType").val();
			$.ajaxSetup({cache:false});
			$("#treeDiv").bind("select_node.jstree",function(e){
				//selectNode($("#"+treeId).find(".jstree-clicked").parent().attr("id"),treeId);
			}).jstree({
				"json_data":{
						"ajax" : { "url" : webRoot+"/authority/permission-item-tree.htm?itemType="+itemType,
							"data" : function (n) {  
								return { currentTreeId : n!=-1 ? n.attr("id") : 0 };   
							}
						}
			   },
			   "themes" : {  
					  "theme" : "classic",  
					  "dots" : true,  
					  "icons" : true 
					 },
			 "types" :{
					"types" : {
						"company" : {
							"icon" : {
								"image" : resourceRoot+"/widgets/jstree/themes/root.gif"
							}
						},
						"folder" : {
							"icon" : {
								"image" : resourceRoot+"/widgets/jstree/themes/folder.gif"
							}
						},
						"user" : {
							"icon" : {
								"image" : resourceRoot+"/widgets/jstree/themes/file.gif"
							}
						}
					}
				 }, 
			 "ui":{ "select_multiple_modifier" : "alt"},
			 "plugins" : [ "themes", "json_data","types","ui","checkbox"]
				});
		}

		//树标签中各信息之间的分隔符,用到了以下几个分隔符
		var split_one = "~~";
		var split_two = "==";
		var split_three = "*#";
		var split_four = "|#";
		var split_five="+#";
		var split_six="~#";
		var split_eight="=#";
		var split_night = "~*";
		var split_ten = "~+";
		function select(){
			if(typeof($("#treeDiv").find(".jstree-checked").attr("id"))=='undefined') {
				alert("请选择正确的节点！"); return;
			}
			var checkedNodes = $("#treeDiv").find(".jstree-checked");
			var conditionValues = "";
			var conditionNames = "";
			for(var i=0;i<checkedNodes.length;i++){
				var sign=true;
				var nodeId = $(checkedNodes[i]).attr("id");
				var values = nodeId.split(split_one);
				var itemType=$("#itemType").val();
				var rowId = $("#rowId").val();
				var value = values[1];
				if(itemType=="USER"){
					if(values[0]=="user"){//选中的人员节点
						//表示不是所有人
						var userId = nodeId.substring(nodeId.indexOf(split_one)+2,nodeId.indexOf(split_two));//用户id
						var userName = nodeId.substring(nodeId.indexOf(split_two)+2,nodeId.indexOf(split_three));//用户姓名
						var userBranch = nodeId.substring(nodeId.indexOf(split_night)+2,nodeId.indexOf(split_ten));//用户所在的分支机构
						var deptName = nodeId.substring(nodeId.indexOf(split_five)+2,nodeId.indexOf(split_six));//用户所在的部门名称
						var deptId = nodeId.substring(nodeId.indexOf(split_six)+2,nodeId.indexOf(split_eight));//用户所在的部门id
						
						var userinfo = userId+"~"+deptId;
						if(deptName=="notInDepartment"){//人员不是无部门人员
							userinfo = userId+"~noDeptId";
						}
						conditionValues = getConditionValues(conditionValues,userinfo);
						var usernameinfo = userName+"("+deptName+"/"+userBranch+")";
						if(deptName=="notInDepartment"){//人员不是无部门人员
							usernameinfo = userName+"("+userBranch+")";
						}
						conditionNames = getConditionNames(conditionNames,usernameinfo);
						
					}else{
						if(values[0]=="company" || values[0]=="allDepartment"){//表示所有人
							conditionValues="ALL_USER";
							conditionNames="所有人";
							break;
						}
					}
				}else if(itemType=="DEPARTMENT"){
					if(values[0]=="department"){//选中的部门节点
						var branchable = nodeId.substring(nodeId.indexOf(split_four)+2,nodeId.indexOf(split_five));//当前节点是否是分支机构
						var deptId = nodeId.substring(nodeId.indexOf(split_one)+2,nodeId.indexOf(split_two));//部门id
						var deptName = nodeId.substring(nodeId.indexOf(split_two)+2,nodeId.indexOf(split_three));//部门名称
						var deptBranch = nodeId.substring(nodeId.indexOf(split_three)+2,nodeId.indexOf(split_four));//部门所在的分支机构
						var deptnameInfo=deptName;
						if("false"==branchable){//不是分支机构
							deptnameInfo=deptName+"("+deptBranch+")";
						}
						conditionValues = getConditionValues(conditionValues,deptId);
						conditionNames = getConditionNames(conditionNames,deptnameInfo);
					}
				}else if(itemType=="ROLE"){
					if(values[0]=="role"){//选中的角色节点
						//nodeId=role~~roleId~~roleName(roleSystem/roleBranch)
						var roleId = nodeId.split(split_one)[1];//角色id
						var str = nodeId.substring(nodeId.indexOf(split_one)+2);//获得的字符串为roleId~~roleName(roleSystem/roleBranch)
						var roleInfo = str.substring(str.indexOf(split_one)+2);//角色信息【角色名称(角色所在系统/角色所在分支机构)】
						conditionValues = getConditionValues(conditionValues,roleId);
						conditionNames = getConditionNames(conditionNames,roleInfo);
					}
				}else if(itemType=="WORKGROUP"){
					if(values[0]=="workGroup"){//选中的工作组节点
						var wgId = nodeId.substring(nodeId.indexOf(split_one)+2,nodeId.indexOf(split_two));//工作组id
						var wgName = nodeId.substring(nodeId.indexOf(split_two)+2,nodeId.indexOf(split_three));//工作组名称
						var wgBranch = nodeId.substring(nodeId.indexOf(split_three)+2);//工作组所在的分支机构
						var branch=wgBranch.split(split_four);
						conditionValues = getConditionValues(conditionValues,wgId);
						conditionNames = getConditionNames(conditionNames,wgName+"("+branch[0]+")");
					}
				}
			}
			if(conditionValues!=""){
				window.parent.$("#"+rowId+"_conditionValue").attr("value",conditionValues);
				window.parent.$("#"+rowId+"_conditionName").attr("value",conditionNames);
				window.parent.$.colorbox.close();
			}else{
				alert("请选择正确的节点！");
			}
		}

		function getConditionValues(conditionValues,conditionValue){
			if(conditionValues==""){
				conditionValues += conditionValue;
			}else{
				conditionValues += ","+conditionValue;
			}
			return conditionValues;
		}

		function getConditionNames(conditionNames,conditionName){
			if(conditionNames==""){
				conditionNames += conditionName;
			}else{
				conditionNames += ","+conditionName;
			}
			return conditionNames;
		}
		</script>
		
	</aa:zone>
</div>
</div>
</body>
</html>
