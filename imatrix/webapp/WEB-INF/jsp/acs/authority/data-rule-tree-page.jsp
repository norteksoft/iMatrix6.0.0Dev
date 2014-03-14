<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/common/acs-taglibs.jsp"%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3c.org/TR/1999/REC-html401-19991224/loose.dtd">
<html>
<head>
	<title>数据表</title>
	<%@ include file="/common/acs-iframe-meta.jsp"%>
	<script type="text/javascript">
	var creatorFieldName="~~creatorId";//标准字段创建人的字段名
	var deptFieldName="~~departmentId";//标准字段创建人的部门字段名
	var roleFieldName="~~roleId";//标准字段创建人的角色字段名
	var wgFieldName="~~workgroupId";//标准字段创建人的工作组字段名

	
		$(document).ready(function(){
			create_tree();
		});

		function getTreeUrl(treeId, treeNode) {
			var standardField=$("#standardField").val();
			var actionUrl = webRoot+"/authority/data-rule-selectRelativeCondition.htm?standardField="+standardField;
			var param = "";
			if(typeof(treeNode)!="undefined"&&treeNode!=null){
				param = "currentTreeId="+treeNode.id;
	        }else{
	        	var param = "currentTreeId=0";
	        }
			return actionUrl+"&" + param;
		}
		
		function create_tree(){
			$.ajaxSetup({cache:false});
			var standardField=$("#standardField").val();
			var treeId = "";
			var roletreeid="";
			if(standardField==creatorFieldName){//创建人时选择的节点是否正确
				treeId ="user_tree_standard"; 
			}else if(standardField==deptFieldName){//创建人部门
				treeId ="dept_tree_standard"; 
			}else if(standardField==wgFieldName){//创建人工作组
				treeId ="wg_tree_standard"; 
			}else if(standardField==roleFieldName){//创建人角色
				treeId ="role_tree"; 
			}
			//treeId:,url:,data(静态树才需要该参数):,multiple:,callback:
			tree.initTree({treeId:treeId,
				url:getTreeUrl,
				type:"ztree",
				multiple:true,
				callback:{
				}});
		}

		function selectMans(){
			ztree.selectTreeValue(function(){
				select();
			});
		}

		var pos = "org";
		//树标签中各信息之间的分隔符,用到了以下几个分隔符
		var split_one = "~~";
		var split_two = "==";
		function select(){
			var hasBranch = "${hasBranch}";
			
			var standardValues = "";
			var standardNames = "";
			
			var conditionValues = "";
			var conditionNames = "";
			var standardField=$("#standardField").val();
			if(standardField==creatorFieldName){//创建人时选择的节点是否正确
					//组织结构
					conditionNames = ztree.getNames();
					if(conditionNames=="所有人员"){
						conditionValues = "ALL_USER";
					}else{
						conditionValues = ztree.getIds();
					}
					//标准值
					var values = getStandardTreeValue();
					standardNames = values[0];
					standardValues = values[1];
			}else if(standardField==deptFieldName){//创建人部门
				//组织结构
				conditionNames = ztree.getRealDepartmentNames();
				if(conditionNames=="所有部门"){
					conditionValues = "ALL_DEPARTMENT";
				}else{
					conditionValues = ztree.getRealDepartmentIds();
				}
				//标准值
				var values = getStandardTreeValue();
				standardNames = values[0];
				standardValues = values[1];
			}else if(standardField==wgFieldName){//创建人工作组
				//组织结构
				conditionNames = ztree.getWorkGroupNames();
				if(conditionNames=="所有工作组"){
					conditionValues = "ALL_WORKGROUP";
				}else{
					conditionValues = ztree.getWorkGroupIds();
				}
				//标准值
				var values = getStandardTreeValue();
				standardNames = values[0];
				standardValues = values[1];
			}else if(standardField==roleFieldName){//创建人角色
				var values = getRoleTreeValue();
				conditionNames = values[0];
				conditionValues = values[1];
			}
			if(standardValues==""&&conditionValues==""){
				alert("请选择正确的节点！"); return;
			}
			if((conditionValues!="ALL_USER"&&conditionValues!="ALL_WORKGROUP")&&standardValues!=""){//所有人时不需要追加
				if(conditionValues==""){
					conditionValues = standardValues;
					conditionNames = standardNames;
				}else{
					conditionValues = conditionValues+","+standardValues;
					conditionNames = conditionNames+","+standardNames;
				}
			}
			var rowId = $("#rowId").val();
			window.parent.$("#"+rowId+"_conditionValue").attr("value",conditionValues+"~~~~");//conditionValues带有~~~~:表示是在弹框中回填的值
			window.parent.$("#"+rowId+"_conditionName").attr("value",conditionNames);
			window.parent.$.colorbox.close();
		}

		function getStandardTreeValue(){
			var standardValues = "";
			var standardNames = "";
			var checkedNodes = tree.getSelectNodes();
			for(var i=0;i<checkedNodes.length;i++){
				var nodeId = checkedNodes[i].id;
				if(nodeId=="standard~~all")continue;
				var values = nodeId.split(split_one);
				if(values[0]=="standard"){//标准值
					var fieldValue = nodeId.substring(nodeId.indexOf(split_one)+2,nodeId.indexOf(split_two));//标准值value,如:CURRENT_USER_ID
					var fieldName = nodeId.substring(nodeId.indexOf(split_two)+2);//标准值显示的值,如:当前用户
					standardValues = getConditionValues(standardValues,fieldValue);
					standardNames = getConditionNames(standardNames,fieldName);
				}
			}
			return [standardNames,standardValues];
		}

		function getRoleTreeValue(){
			var standardField=$("#standardField").val();
			
			var standardValues = "";
			var standardNames = "";
			
			var conditionValues = "";
			var conditionNames = "";
			var checkedNodes = tree.getSelectNodes();
			for(var i=0;i<checkedNodes.length;i++){
				//var nodeId = $(checkedNodes[i]).attr("id");
				var nodeId = checkedNodes[i].id;
				if(nodeId=="standard~~all")continue;
				var values = nodeId.split(split_one);
				if(values[0]=="standard"){//标准值
					var fieldValue = nodeId.substring(nodeId.indexOf(split_one)+2,nodeId.indexOf(split_two));//标准值value,如:CURRENT_USER_ID
					var fieldName = nodeId.substring(nodeId.indexOf(split_two)+2);//标准值显示的值,如:当前用户
					standardValues = getConditionValues(standardValues,fieldValue);
					standardNames = getConditionNames(standardNames,fieldName);
				}else{//组织机构
					var type = nodeId.split("_")[0];
					if(standardField==roleFieldName){//创建人角色
						if(type=="role"){//选中的角色节点
							//nodeId=role_roleId~~roleName(roleSystem/roleBranch)
							var roleId = nodeId.substring(nodeId.indexOf("_")+1,nodeId.indexOf(split_one));//角色id
							var str = nodeId.substring(nodeId.indexOf("_")+1);//获得的字符串为roleId~~roleName(roleSystem/roleBranch)
							var roleInfo = str.substring(str.indexOf(split_one)+2);//角色信息【角色名称(角色所在系统/角色所在分支机构)】
							conditionValues = getConditionValues(conditionValues,roleId);
							conditionNames = getConditionNames(conditionNames,roleInfo);
						}
					}
				}
			}
			if(standardValues!=""){//所有人时不需要追加
				if(conditionValues==""){
					conditionValues = standardValues;
					conditionNames = standardNames;
				}else{
					conditionValues = standardValues+","+conditionValues;
					conditionNames = standardNames+","+conditionNames;
				}
			}
			return [conditionNames,conditionValues];
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

		function changeViewSet(position){
			pos = position;
			if(position=="org"){//组织结构 页签
				$("#tabs-1").css("display","block");
				$("#tabs-2").css("display","none");
			}else if(position=="standard"){//标准值 页签
				$("#tabs-2").css("display","block");
				$("#tabs-1").css("display","none");
			}
		}

	</script>
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
		<input type="hidden" id="standardField" value="${standardField }">
		<input type="hidden" id="rowId" value="${rowId }">
	</form>
	<aa:zone name="main_zone">
	
		<div class="opt-btn">
			<button class="btn" onclick="<s:if test="standardField=='~~roleId'">select();</s:if><s:else>selectMans();</s:else>"><span><span >确定</span></span></button>
		</div>
		<div id="opt-content">
			<s:if test="standardField=='~~roleId'">
				<table>
				<tr>
				<td >
					<input id="searchroleInput" /></td><td ><a class="search-btn" href="#" onclick="tree.searchNodes($('#searchroleInput').val());" ><b class="ui-icon ui-icon-search"></b></a>
				</td>
				</tr>
				</table>
				<ul class="ztree" id="role_tree"></ul>
			</s:if><s:else>
				<script type="text/javascript">
					$(document).ready(function() {
						$( "#tabs" ).tabs();
					});
				</script>
				<div id="tabs">
					<ul>
						<li><a href="#tabs-1" onclick="changeViewSet('org');">组织结构</a></li>
						<li><a href="#tabs-2" onclick="changeViewSet('standard');">标准值</a></li>
					</ul>
					<div id="tabs-1">
						<s:if test="standardField=='~~creatorId'">
						<ztree:ztree
									treeType="MAN_DEPARTMENT_TREE"
									treeId="user_tree" 
									userWithoutDeptVisible="true"  
									showBranch="true"
									chkboxType="{'Y':'ps','N':'ps'}"
									chkStyle="checkbox"
									>
						</ztree:ztree>
						</s:if><s:elseif test="standardField=='~~departmentId'">
						<ztree:ztree
									treeType="DEPARTMENT_TREE"
									treeId="dept_tree" 
									userWithoutDeptVisible="true"  
									showBranch="true"
									chkboxType="{'Y':'s','N':'s'}"
									chkStyle="checkbox"
									>
						</ztree:ztree>
						</s:elseif><s:elseif test="standardField=='~~workgroupId'">
						<ztree:ztree
									treeType="GROUP_TREE"
									treeId="wg_tree" 
									userWithoutDeptVisible="true"  
									showBranch="true"
									chkboxType="{'Y':'ps','N':'ps'}"
									chkStyle="checkbox"
									>
						</ztree:ztree>
						</s:elseif>
					</div>
					<div id="tabs-2" style="display: none;">
						<s:if test="standardField=='~~creatorId'">
							<ul class="ztree" id="user_tree_standard"></ul>
						</s:if><s:elseif test="standardField=='~~departmentId'">
							<ul class="ztree" id="dept_tree_standard"></ul>
						</s:elseif><s:elseif test="standardField=='~~workgroupId'">
							<ul class="ztree" id="wg_tree_standard"></ul>
						</s:elseif>
					</div>
				</div>
			</s:else>
			
		</div>
	</aa:zone>
</div>
</div>
</body>
</html>
